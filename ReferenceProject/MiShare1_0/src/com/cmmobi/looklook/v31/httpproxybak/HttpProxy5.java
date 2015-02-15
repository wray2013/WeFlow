package com.cmmobi.looklook.v31.httpproxybak;

import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketException;
import java.net.URI;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.LinkedBlockingQueue;

import org.apache.http.protocol.HTTP;

import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import cn.zipper.framwork.core.ZLog;
import cn.zipper.framwork.io.file.ZFileWriter;
import cn.zipper.framwork.io.network.ZHttp2;
import cn.zipper.framwork.io.network.ZHttpReader;
import cn.zipper.framwork.io.network.ZHttpResponse;
import cn.zipper.framwork.io.network.ZNetworkStateDetector;
import cn.zipper.framwork.utils.MD5Util;
import cn.zipper.framwork.utils.ZPercent;
import cn.zipper.framwork.utils.ZPercent.OnPercentChangedListener;

import com.cmmobi.looklook.MainApplication;
import com.cmmobi.looklook.info.profile.AccountInfo;
import com.cmmobi.looklook.v31.httpproxybak.Config.ProxyRequest;
import com.cmmobi.looklook.v31.httpproxybak.Config.ProxyRequestWrapper;
import com.cmmobi.looklook.v31.httpproxybak.Config.ProxyResponse;
import com.cmmobi.looklook.v31.httpproxybak.Config.ProxyResponseWrapper;
import com.cmmobi.looklook.v31.httpproxybak.ProxyTempFile.tmpBlock;

/**
 * 
 * @author Ray
 *
 */
public class HttpProxy5 {
	
	private final static String TAG = "HttpProxy";
	private static boolean OPEN_LOG = false;
	/**避免某些MediaPlayer不播放尾部就结束*/
	private final static int CACHE_SIZE = 1024 * 1024;//500k
	
	private final int READ_BYTE_DATA_SIZE = 1024 * 4;
	
	private static HttpProxy5 ins = null;
	
	private MPConnector mpConnector;
//	private SVConnector svConnector;
	
	/** 收发Media Player请求的Socket */
	private Socket sckPlayer = null;
	/** 收发Media Server请求的Socket */
	private Socket sckServer = null;
	/**Response对象*/
	private ProxyResponse proxyResponse=null;
	
	HttpParser httpParser = null;
	
	HttpGetProxyUtils gUtils = null;
	
	private int remotePort=-1;
	/** 远程服务器地址 */
	private String remoteHost;
	/** 代理服务器使用的端口 */
	private int localPort;
	/** 本地服务器地址 */
	private String localHost;
	/**TCP Server，接收Media Player连接*/
	private ServerSocket localServer = null;
	/**服务器的Address*/
	private SocketAddress serverAddress;
	/** 缓存临时文件*/
//	private ProxyTempFile tmpfile = null;
	private File tmpfile = null;
	
	
	/**缓存文件路径*/
	private String mTmpFilePath;
	/**缓存所需的大小*/
	private int mTmpFileSize;
	/**缓存文件夹*/
	private String mBufferDirPath=null;
	/**视频原始连接、可用链接 */
	private String mOrigUrl, mValidUrl;
	
//	boolean sentResponseHeader = false;
	
	private CacheDownloader downloader = null;
	
	private ProxyState state;
	
	private AccountInfo ai;
	private static String userID;
	
	public enum ProxyState {
		INITIALIZING, PREPARED, CACHING, PAUSE, STOP, COMPLETED, ERROR
	};
	
	/**
	 * 取得实例
	 * @return
	 */
	public static HttpProxy5 getInstance(String uid) {
		if (ins == null || ins.state == ProxyState.ERROR) {
			userID = uid;
			ins = new HttpProxy5();
		}
		return ins;
	}
	
	/**
	 * default constructor
	 */
	private HttpProxy5() {
		ai = AccountInfo.getInstance(userID);
		state = ProxyState.INITIALIZING;
		//初始化代理服务器
		mBufferDirPath = Config.CACHE_FILE_PATH; 
		localHost = Config.LOCAL_IP_ADDRESS;
		
		try {
			localServer = new ServerSocket(0, 1,InetAddress.getByName(localHost));
			ProxyLog(TAG, "localServer timeout = " + localServer.getSoTimeout());
			localServer.setSoTimeout(0);
			localPort =localServer.getLocalPort();//有ServerSocket自动分配端口
			//开始监听MediaPlayer请求
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			state = ProxyState.ERROR;
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			state = ProxyState.ERROR;
			e.printStackTrace();
		}
	}
	
	public int getTotalSize() {
		return mTmpFileSize;
	}
	
	public int getCachedSize() {
		if(downloader != null)
			return (int) downloader.getDownloadedSize();
		return 0;
	}
	/**
	 * 加载网络视频，准备播放
	 * 地址重定向排除、文件大小获得、空间可用大小判断，tmp文件创建。
	 * @param origUrl 视频url
	 * @return String 本地127.0.0.1地址
	 *         null   发生错误
	 */
	public String prepare(String origUrl) {
		
		// 获得文件大小
//		try {
//			mTmpFileSize = getTargetSize(origUrl);
//		} catch (Exception e) {
//			state = ProxyState.ERROR;
//			e.printStackTrace();
//			return null;
//		}
//			
//		if(mTmpFileSize <= 0) {
//			state = ProxyState.ERROR;
//			return null;
//		}
		stop();
		
		mOrigUrl = origUrl;
		
		/*创建临时文件*/
		try {
			String ret = createCacheFile();
			if(ret != null) {
				if(!ret.equals("OK")) {
					state = ProxyState.ERROR;
					return ret;
				}
			} else {
				Log.e(TAG, "Something Error Occured When Cache Initialized.");
				state = ProxyState.ERROR;
				return null;
			}
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			state = ProxyState.ERROR;
			e.printStackTrace();
			return null;
		}
			
		if (ZNetworkStateDetector.isAvailable()) {
			// 排除HTTP特殊,如重定向
			Log.e(TAG, "排除HTTP特殊,如重定向");
			mValidUrl = Utils.getRedirectUrl(origUrl);
		} else {
			Log.e(TAG, "直接使用原始url");
			mValidUrl = origUrl;
		}
		ProxyLog("Http mMediaUrl",mValidUrl);
		
		// ----获取对应本地代理服务器的链接----//
		String localUrl="";
		URI originalURI = URI.create(mValidUrl);
		remoteHost = originalURI.getHost();
		
		if (originalURI.getPort() != -1) {// URL带Port
			serverAddress = new InetSocketAddress(remoteHost, originalURI.getPort());// 使用默认端口
			remotePort = originalURI.getPort();// 保存端口，中转时替换
			localUrl = mValidUrl.replace(remoteHost + ":" + originalURI.getPort(), localHost + ":" + localPort);
		} else {// URL不带Port
			serverAddress = new InetSocketAddress(remoteHost, Config.HTTP_PORT);// 使用80端口
			remotePort = -1;
			localUrl = mValidUrl.replace(remoteHost, localHost + ":" + localPort);
		}
		state = ProxyState.PREPARED;
		start();
		return localUrl;
	}
	
	private void start() {
		
		httpParser = new HttpParser(remoteHost, remotePort, localHost, localPort);
		
		if(downloader != null) {
			downloader.stopThread();
			downloader = null;
		}
		downloader = new CacheDownloader(mValidUrl, tmpfile, 0, mTmpFileSize);
		downloader.startThread(0);
		try {
			if(mpConnector != null) {
				mpConnector.stopThread();
				mpConnector = null;
			}
			
			mpConnector = new MPConnector();
			mpConnector.execute();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void stop() {
		if(downloader != null) {
			if(downloader.targetFileExist()) {
				downloader.removeTmpFile();
			}
			downloader.stopThread();
			downloader = null;
		}
		if(mpConnector != null) {
			mpConnector.stopThread();
			mpConnector = null;
		}
		closeSocket();
	}
	
	private int getTargetSize(String remoteurl) throws Exception {
		URL url = new URL(remoteurl);
		HttpURLConnection urlConnection = (HttpURLConnection) url
				.openConnection();
		return urlConnection.getContentLength();
	}
	
	/**
	 * 创建临时文件
	 * @return
	 * @throws Exception
	 */
	private String createCacheFile() throws Exception {
		//代理服务器不可用
		if(!getEnable() || mOrigUrl == null || mOrigUrl.equals("")) {
			Log.e(TAG, "代理服务器不可用或mUrl空:[" + mOrigUrl + "]");
			return null;
		}

		String filekey = MD5Util.getMD5String(mOrigUrl);
		ProxyLog(TAG, "filekey = " + filekey);
		String fileName = Utils.getValidFileName(filekey);
		
		mTmpFilePath = mBufferDirPath + "/" + fileName + ".tmp";
		String comfileName = mBufferDirPath + "/" + fileName + ".mp4";
		//判断文件是否存在，忽略已经缓冲过的文件
		File comFile = new File(comfileName);
		
		if (comFile.exists()) {
			Log.i(TAG, "Use Local File[" + comfileName + "]");
			return comfileName;
		}
		
		checkInMap(mTmpFilePath);
		
		tmpfile = new File(mTmpFilePath);
		if(!tmpfile.exists()) {
			tmpfile.createNewFile();
		}
		
		if (ZNetworkStateDetector.isAvailable()) {
			mTmpFileSize = getTargetSize(mOrigUrl);
			Log.i(TAG, "mTmpFileSize:" + mTmpFileSize);
			if(mTmpFileSize <= 0) {
				return null;
			}
			//TODO:
//			createTmpFile(mTmpFilePath, mTmpFileSize);
//			tmpfile = new ProxyTempFile(mTmpFilePath, mTmpFileSize);
//			return "OK";
		} /*else {
			return null;
		}*/
		

		persist();
		
		return "OK";
	}
	
	/**
	 * 关闭现有的链接
	 */
	private void closeSocket() {
		try {
			if (sckPlayer != null) {
				sckPlayer.close();
				sckPlayer = null;
			}
			if (sckServer != null) {
				sckServer.close();
				sckServer = null;
			}
		} catch (IOException e1) {
		}
	}
	
	private void persist() {
		if (mTmpFilePath != null && !mTmpFilePath.equals("")
				&& mTmpFileSize > 0) {
			Map<String, String> ca = new HashMap<String, String>();
			ca.put("path", mTmpFilePath);
			ca.put("size", mTmpFileSize + "");
			ai.cachefileinfo.add(ca);
		}
	}
	
	private void checkInMap(String path) {
		if(ai != null && ai.cachefileinfo != null) {
			for(Map<String, String> ca : ai.cachefileinfo) {
				if(ca.get("path").equals(path)) {
					try {
						mTmpFileSize = Integer.parseInt(ca.get("size"));
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		}
	}
	
	/**
	 * 空间是否可用
	 * @return
	 */
	private boolean getEnable() {
		boolean mEnable = false;
		// 判断外部存储器是否可用
		File dir = new File(mBufferDirPath);
		mEnable = dir.exists();
		if (!mEnable)
			return mEnable;
		// 获取可用空间大小
		long freeSize = Utils.getAvailaleSize(mBufferDirPath);
		mEnable = (freeSize > mTmpFileSize);

		return mEnable;
	}
	
	/**
	 * 监听MediaPlayer发送的请求
	 * @author Ray
	 *
	 */
	private class MPConnector extends Thread {
		
		private final static String TAG_MP = "MPConnector";
		private Proxy proxy = null;
		
		public MPConnector() {
			ProxyLog(TAG_MP, "remoteHost " + remoteHost + "\n"
					+ "remotePort " + remotePort + "\n"
					+ "localHost " + localHost + "\n"
					+ "localPort " + localPort + "\n");
//			sentResponseHeader = false;
		}
		
		public void execute() {
			state = ProxyState.CACHING;
			this.start();
		}
		
		public void stopThread() {
			state = ProxyState.STOP;
			if(proxy != null) {
				proxy.stopThread();
				proxy = null;
			}
		}
		
		@Override
		public void run() {
			
			state = ProxyState.CACHING;
			
			while(state == ProxyState.CACHING) {
				try {
					Socket s = localServer.accept();
					ProxyLog(TAG, "Socket timeout = " + s.getSoTimeout());
					s.setSoTimeout(0);
					if(gUtils == null)
						gUtils = new HttpGetProxyUtils(s, serverAddress);
					Log.i(TAG_MP, "<-----------------MP端有新请求,socket:[" + s + "]----------------->");
					if(proxy != null) {
						Log.i(TAG_MP, "<-----------------关闭上一次代理socket:[" + proxy.getSocket() + "]----------------->");
						proxy.stopThread();
//						proxy = null;
					}
					proxy = new Proxy(s);
					proxy.execute();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
			Log.i(TAG_MP, "......play over...........");
		}
	}
	
	/**
	 * 
	 * @author Ray
	 *
	 */
	public class CacheDownloader extends Thread {
		
		static private final String TAG_CD = "CacheDownloader";
		
		private String prefix = "";
		
		private String mRemoteUrl, mLocalUrl;
		private File tmpFile;
		
		private int mSize, mRange;
		private int mRetryTimes;
		private long mDownloadSize;//已经下载的大小
		
		private boolean mStop, misAlive;
		
		private ZHttp2 http;
		private ZHttpReader reader;
		private ZFileWriter writer;
		
		public CacheDownloader(String url, File file, int range, int targetSize) {
			prefix = UUID.randomUUID().toString().substring(0, 8);
			if(url != null && file != null) {
				mRemoteUrl = url;
				mLocalUrl = file.getPath();
				tmpFile = file;
				mSize = targetSize;
				mRange = range;
			}
			mDownloadSize = 0;
			mRetryTimes = 3;
			mStop = false;
			misAlive = false;
//			misCompleted = false;
		}
		
		public void startThread(int range) {
			ProxyLog(TAG_CD+prefix, "start downloading from [" + range + "]");
			mRange = range;
			this.start();
		}
		
		public void startThread() {
			ProxyLog(TAG_CD+prefix, "start downloading from [" + mRange + "]");
			this.start();
		}
		
		public void stopThread() {
			mStop = true;
		}
		
		public long getDownloadedSize() {
			return mDownloadSize;
		}
		
		public boolean isDownloading() {
			return misAlive;
		}
		
		@Override
		public void run() {
			download();
		}
		
		private void retry() {
			mRetryTimes --;
			if(mRetryTimes > 0) {
				ProxyLog(TAG_CD+prefix, "下载失败，重试！");
				download();
			} else {
				ProxyLog(TAG_CD+prefix, "重试失败！");
//				mError = true;
			}
		}
		
		private boolean targetFileExist() {
			boolean isExist = false;
			String newPath = mLocalUrl.replace(".tmp", ".mp4");
			File newfile = new File(newPath);
			if(newfile.exists()) {
				isExist = true;
			}
			return isExist;
		}
		
		private synchronized void download() {
			
			try {
				if (targetFileExist()) {
					ProxyLog(TAG_CD+prefix, "已经下载过该文件");
					return;
				}
				misAlive = true;
//				synchronized (Thread.currentThread()) {
//					Thread.currentThread().wait(10000);				
//				}
				HashMap<String, String> headers = new HashMap<String, String>();
				if (tmpFile.exists()) {
					mRange = (int) tmpFile.length();
					mDownloadSize = mRange;
					headers.put("range", "bytes=" + mRange + "-");
				}
				headers.put("range", "bytes=" + mRange + "-");
				http = new ZHttp2();
				http.setHeaders(headers);
				ZLog.e("headers=" + headers);
				ZLog.e("mRemoteUrl=" + mRemoteUrl);
				ZHttpResponse response = http.get(mRemoteUrl);
//				 response.printHeaders();
				ZLog.e(response==null?"response is null":"response is not null");
				if (response != null) {
					if (response.getResponseCode() == HttpURLConnection.HTTP_PARTIAL) { // 断点续传;
						ProxyLog(TAG_CD+prefix, "http回传[206]，断点续传模式下载");
						writer = new ZFileWriter(tmpFile, true, null);
					} else if (response.getResponseCode() == HttpURLConnection.HTTP_OK) { // 重新传;
						ProxyLog(TAG_CD+prefix, "http回传[200]，从头开始下载");
						if(tmpFile.exists()) {
							tmpFile.delete();
						}
						writer = new ZFileWriter(tmpFile, false, null);
					}
				} else {
					//TODO:
					ProxyLog(TAG_CD+prefix, "http响应为空，任务重试！");
					retry();
					return;
				}
				ZLog.e("content length = " + response.getHeader(HTTP.CONTENT_LEN));
				long contentLength = 0;
				
				if(mDownloadSize >= mSize) {
					ProxyLog(TAG_CD+prefix, "已经下载完毕！");
//					misCompleted = true;
//					removeTmpFile();
//					mIsComplete = true;
					return;
				}

				OnPercentChangedListener listener = new OnPercentChangedListener() {
					@Override
					public void onPercentChanged(ZPercent percent) {
						try {
							if (mStop) {
								ProxyLog(TAG_CD+prefix, "下载过程暂停");
								ZLog.e("STATE_PAUSED while reading");
								if (reader != null) {
									reader.stop();
								}
							}
							Object object = percent.getObject();
							if (object != null) {
								byte[] bytes = (byte[]) object;
//								utils.sendToMP(bytes, bytes.length);
//								tmpFile.writeTmpFile(mRange, bytes);
								writer.writeBlock(0, bytes);
//								mRange += bytes.length;
								addStep(bytes.length);
//								ZThread.sleep(5);
							}
						} /*catch (SocketException e) {
							e.printStackTrace();
							mStop = true;
							ProxyLog(TAG_CD+prefix, "下载过程Socket异常");
							if (reader != null) {
								reader.stop();
							}
						}*/ catch (Exception e) {
							e.printStackTrace();
							mStop = true;
							if (reader != null) {
								reader.stop();
							}
						}
					}
				};
				reader = new ZHttpReader(response.getInputStream(), listener);
				if(!reader.readByBlockSize2(contentLength, 1024 * 4)) {
					//TODO:
					ProxyLog(TAG_CD+prefix, "下载读取数据时出错，重试！");
					retry();
					return;
				}
				if(reader.isEnding()) {
//					misCompleted = true;
//					removeTmpFile();
					ProxyLog(TAG_CD+prefix, "已经全部下载完毕！");
				}
				closeHttp();
//				removeTmpFile();
			} catch (Exception e) {
				e.printStackTrace();
				closeHttp();
				ProxyLog(TAG_CD+prefix, "下载过程发生异常，任务重试！");
				retry();
			}
		}
		
		private void removeTmpFile() {
			String newPath = mLocalUrl.replace(".tmp", ".mp4");
			File file = new File(mLocalUrl);
			File newfile = new File(newPath);
			if(file.exists()) {
				file.renameTo(newfile);
			}
		}
		
		private void closeHttp() {
			misAlive = false;
			if (reader != null) {
				reader.close();
			}
			if (http != null) {
				http.close();
			}
		}
		
		private void addStep(long step) {
			mDownloadSize += step;
			//TODO:可以通知界面
		}
	}
	
	private class Proxy extends Thread {

		private final static String TAG_PROXY = "Proxy";
		private String prefix = "";
		private boolean isStop = false;

		private Socket mSocket = null;
		
		HttpGetProxyUtils proxyUtils = null;
		// private Socket sckPlayer = null;
		// private Socket sckServer = null;

		public Proxy(Socket s) {
			prefix = UUID.randomUUID().toString().substring(0, 8);
			ProxyLog(TAG_PROXY + prefix, "...... Proxy Construction ......");
			mSocket = s;
			proxyUtils = new HttpGetProxyUtils(s, serverAddress);
		}

		public void execute() {
			this.start();
		}

		public Socket getSocket() {
			return mSocket;
		}
		
		public void stopThread() {
			ProxyLog(TAG_PROXY + prefix, "...... 停止代理stopThread ......");
			this.isStop = true;
			closeSocket();
		}

		private void closeSocket() {
			if(mSocket != null) {
				try {
					mSocket.close();
					mSocket = null;
					ProxyLog(TAG_PROXY + prefix, "...... Socket已关闭 ......");
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		@Override
		public void run() {

			// HttpGetProxyUtils utils = null;
			int bytes_read;

			byte[] local_request = new byte[1024];

			boolean sentResponseHeader = false;
			ProxyRequest request = null;
			try {
				while ((bytes_read = mSocket.getInputStream().read(local_request)) != -1) {
					byte[] buffer = httpParser.getRequestBody(local_request,
							bytes_read);
					if (buffer != null) {
						ProxyLog(TAG_PROXY + prefix, "...... A.读取请求 ......");
						request = httpParser.getProxyRequest(buffer);
						break;
					}
				}
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			while (!isStop) {
				try {
					if (request != null) {// MediaPlayer的request有效
						ProxyLog(TAG_PROXY + prefix, "...... B.请求[" + request._rangePosition + "] ......");
						if (tmpfile == null) {
							// tmpfile = new ProxyTempFile(mTmpFilePath,
							// mTmpFileSize);
							tmpfile = new File(mTmpFilePath);
						}
						
						if(downloader != null && !downloader.isDownloading()) {
							downloader.stopThread();
							downloader = new CacheDownloader(mValidUrl, tmpfile, 0, mTmpFileSize);
							downloader.startThread();
						}

						boolean isInRange = ((downloader.getDownloadedSize() - CACHE_SIZE) > request._rangePosition || 
								downloader.getDownloadedSize() >= mTmpFileSize);
						if (isInRange) {
							ProxyLog(TAG_PROXY + prefix, "...... C.在缓存中，需要读取缓存 ......");
							Log.i(TAG_PROXY + prefix, "request : " + request._rangePosition + "; cache : [ 0 - " + downloader.getDownloadedSize() + "]");
							if (!sentResponseHeader) {
								ProxyLog(TAG_PROXY + prefix, "...... D.先返回伪装http头到MP ......");
								proxyResponse = proxyUtils.fadeResponseHeader(mTmpFileSize, request._rangePosition, httpParser);
								proxyUtils.sendToMP(proxyResponse._body);
								sentResponseHeader = true;
							}
							int sentBufferSize = 0;
							ProxyLog(TAG_PROXY + prefix, "...... E.返回读取的缓存数据到MP ......");
							sentBufferSize = proxyUtils.sendPrebufferToMP3(mTmpFilePath, request._rangePosition, downloader.getDownloadedSize());
							if (sentBufferSize > 0) {// 成功发送缓存，重新发送请求到服务器
								ProxyLog(TAG_PROXY + prefix, "...... F.成功发送缓存(" + sentBufferSize + "byte)，修改新range ......");
								// 修改Range后的Request发送给服务器
								int newRange = (int) (sentBufferSize + request._rangePosition);
								// String newRequestStr =
								// httpParser.modifyRequestRange(request._body,
								// newRange);
								request._rangePosition = newRange;
								// TODO:
//								Thread.sleep(2000);
								continue;
							} else {
								ProxyLog(TAG_PROXY + prefix, "...... E.发送缓存数据到MP失败 ......");
//								Thread.sleep(2000);
								continue;
							}
						} else {
							ProxyLog(TAG_PROXY + prefix, "...... C.等待缓存 ......");
							Thread.sleep(2000);
							continue;
						}
					} else {
						ProxyLog(TAG_PROXY + prefix, "...... B.request不合法 ......");
					}
				} catch (Exception e) {
					ProxyLog(TAG_PROXY + prefix, "...... F.Socket出错, socket:[" + mSocket + "] ......");
//					try {
//						mSocket.connect(serverAddress);
//						ProxyLog(TAG_PROXY + prefix, "...... G.mSocket重新建立链接 [" + mSocket + "]......");
//					} catch (IOException e1) {
//						// TODO Auto-generated catch block
//						e1.printStackTrace();
//					}
//					Log.e(TAG_PROXY + prefix, e.toString());
//					Log.e(TAG_PROXY + prefix, Utils.getExceptionMessage(e));
				}
				// closeSocket();
			}
			ProxyLog(TAG_PROXY + prefix, "...... G.Proxy OVER! ......");
//			closeSocket();
		}
	}
	
	private void sendMessage(int msg, String extra) {
		// TODO Auto-generated method stub
		Intent intent = new Intent(Config.HTTPPROXY_RESULT_MSG);
		// You can also include some extra data.
		intent.putExtra("type", msg);
		intent.putExtra("extra", extra);

		if (extra != null) {
			intent.putExtra("extra", extra);
		}

		LocalBroadcastManager.getInstance(MainApplication.getAppInstance()).sendBroadcast(intent);
	}
	
	private void ProxyLog(String tag, String log) {
		if(OPEN_LOG) {
			Log.d(tag, log);
		}
	}
	
}
