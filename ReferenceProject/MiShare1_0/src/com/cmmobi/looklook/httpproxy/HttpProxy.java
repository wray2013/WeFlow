package com.cmmobi.looklook.httpproxy;

import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.URI;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.LinkedBlockingQueue;

import android.content.Intent;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import cn.zipper.framwork.io.network.ZNetworkStateDetector;
import cn.zipper.framwork.utils.MD5Util;
import cn.zipper.framwork.utils.ZByteToSize;

import com.cmmobi.looklook.MainApplication;
import com.cmmobi.looklook.httpproxy.downloader.CacheDownloader;
import com.cmmobi.looklook.httpproxy.utils.HttpGetProxyUtils;
import com.cmmobi.looklook.httpproxy.utils.HttpParser;
import com.cmmobi.looklook.httpproxy.utils.ProxyConfig;
import com.cmmobi.looklook.httpproxy.utils.ProxyUtils;
import com.cmmobi.looklook.httpproxy.utils.ProxyConfig.ProxyRequest;
import com.cmmobi.looklook.httpproxy.utils.ProxyConfig.ProxyRequestWrapper;
import com.cmmobi.looklook.httpproxy.utils.ProxyConfig.ProxyResponse;
import com.cmmobi.looklook.info.profile.AccountInfo;

/**
 * 
 * @author Ray
 *
 */
public class HttpProxy {
	
	private final static String TAG = "HttpProxy";
	private static boolean OPEN_LOG = false;//true;//
	/**避免某些MediaPlayer不播放尾部就结束*/
	private final static int CACHE_BYTE_DATA_SIZE = 150 * 1024;//150k
	
	public static final int HANDLER_LOCAL_URL_PREPARED = 0x0086;
	
	public static final int STATE_UNKNOWN = -1;
	public static final int STATE_INITIALIZING = 1;
	public static final int STATE_PREPARED = 2;
	public static final int STATE_CACHING = 3;
	public static final int STATE_STOP = 4;
	public static final int STATE_COMPLETED = 5;
	public static final int STATE_ERROR = 6;
	public static final int STATE_WAITING = 7;
	
	public static final int PROXY_ERROR_UNKNOWN = -1;
	public static final int PROXY_ERROR_UNKNOWNHOST = 0;
	public static final int PROXY_ERROR_IO	= 1;
	public static final int PROXY_ERROR_NETWORK = 2;
	public static final int PROXY_ERROR_FAIL_CREATE_FILE = 3;
	public static final int PROXY_ERROR_NO_SPACE = 4;
	
	public static final int MTypeAudio = 0;
	public static final int MTypeVideo = 1;
	
	private static HttpProxy ins = null;
	
	private MPConnector mpConnector;
//	private MPConnector2 mpConnector2;
//	private SVConnector svConnector;
	private ProxyPlayer mProxyPlayer;
	private CacheDownloader downloader = null;
	private LinkedBlockingQueue<ProxyRequestWrapper> fifo;
	
	/** 收发Media Player请求的Socket */
	private Socket sckPlayer = null;
	/** 收发Media Server请求的Socket */
//	private Socket sckServer = null;
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
	/**媒体文件类型*/
	private String mMineType;
	/**缓存文件夹*/
	private String mBufferDirPath=null;
	/**视频原始连接、可用链接 ,本地连接*/
	private String mOrigUrl, mValidUrl, mLocalUrl;
	
//	boolean sentResponseHeader = false;
	
//	private ProxyState state;
	private int state = STATE_UNKNOWN;
	
	private AccountInfo ai;
	private static String userID;
	
	public enum ProxyMediaType {
		AUDIO, VIDEO
	};
	
	private int errorno;
	
	/**
	 * 取得实例
	 * @return
	 */
	public static HttpProxy getInstance(String uid) {
		if (ins == null || ins.state == STATE_ERROR) {
			userID = uid;
			ins = new HttpProxy();
			Log.d(TAG, "new HttpProxy created!");
		}
		return ins;
	}
	
	/**
	 * default constructor
	 */
	private HttpProxy() {
		
		ai = AccountInfo.getInstance(userID);
		errorno = PROXY_ERROR_UNKNOWN;
		
		fifo = new LinkedBlockingQueue<ProxyRequestWrapper>();
		
		state = STATE_INITIALIZING;
		//初始化代理服务器
		mBufferDirPath = ProxyConfig.CACHE_FILE_PATH + userID + "/tmp/"; 
		localHost = ProxyConfig.LOCAL_IP_ADDRESS;
		
		try {
			localServer = new ServerSocket(0, 1,InetAddress.getByName(localHost));
			localPort =localServer.getLocalPort();//有ServerSocket自动分配端口
			//开始监听MediaPlayer请求
		} catch (UnknownHostException e) {
			state = STATE_ERROR;
			errorno = PROXY_ERROR_UNKNOWNHOST;
			e.printStackTrace();
		} catch (IOException e) {
			state = STATE_ERROR;
			errorno = PROXY_ERROR_IO;
			e.printStackTrace();
		}
	}
	
	/**
	 * 获取媒体文件总大小
	 * @return
	 */
	public int getTotalSize() {
		return mTmpFileSize;
	}
	
	/**
	 * 获取当前媒体文件已缓存的大小
	 * @return
	 */
	public int getCachedSize() {
		if(downloader != null)
			return (int) downloader.getDownloadedSize();
		Log.e(TAG, "getCachedSize <--- downloader is null");
		return 0;
	}
	
	public int getPercent() {
		if(downloader != null)
			return downloader.getPercent();
//		Log.e(TAG, "getPercent <--- downloader is null");
		return 0;
	}
	
	public int getStatus() {
		return state;
	}
	
	/**
	 * 获取错误码
	 * @return
	 */
	public int getErrorCode() {
		return errorno;
	}
	
	public String getCurLocalUrl() {
		return mLocalUrl;
	}
	
	public void resetProxy() {
		fifo.clear();
		//重启下载线程
		if(downloader != null) {
			downloader.stopThread();
			downloader = null;
		}
		downloader = new CacheDownloader(mValidUrl, tmpfile, 0, mTmpFileSize);
		downloader.startThread();
		//重启MP监听线程
		if(mpConnector != null) {
			mpConnector.stopThread();
			mpConnector = null;
		}
		mpConnector = new MPConnector();
		mpConnector.execute();
		//重启Proxy
		if(mProxyPlayer != null) {
			mProxyPlayer.stopThread();
			mProxyPlayer = null;
		}
		mProxyPlayer = new ProxyPlayer();
		mProxyPlayer.execute();
	}
	
	/**
	 * 启动代理
	 */
	private void startProxy() {
		Log.e(TAG, "开启代理");
		httpParser = new HttpParser(remoteHost, remotePort, localHost, localPort);
		resetProxy();
	}
	
	/**
	 * 停止代理
	 */
	public void stopProxy() {
		Log.e(TAG, "停止代理");
		if(downloader != null) {
			if(downloader.isComplete()) {
				downloader.removeTmpFile();
			}
			downloader.stopThread();
			downloader = null;
		}
		if(mpConnector != null) {
			mpConnector.stopThread();
			mpConnector = null;
		}
		/*
		if(mpConnector2 != null) {
			mpConnector2.stopThread();
			mpConnector2 = null;
		}
		*/
		if(mProxyPlayer != null) {
			mProxyPlayer.stopThread();
			mProxyPlayer = null;
		}
		closeSocket();
	}
	
	
	/**
	 * 异步获取本地地址
	 * 
	 * @throws IOException
	 */
	public void asynGetProxyUrl(final Handler handler, final String origUrl, final int response, final int MType) {
		new Thread() {
			public void run() {
				Log.d(TAG, "origUrl = " + origUrl);
				if(MType == MTypeAudio) {
					mMineType = "audio/mpeg";
				} else if(MType == MTypeVideo) {
					mMineType = "video/mp4";
				}
				String proxyUrl = prepare(origUrl);
				Message message = handler.obtainMessage(response, proxyUrl);
				handler.sendMessage(message);
			}
		}.start();
	}
	
	/**
	 * 加载网络视频，准备播放
	 * 地址重定向排除、文件大小获得、空间可用大小判断，tmp文件创建。
	 * @param origUrl 视频url
	 * @return String 本地127.0.0.1地址
	 *         null   发生错误
	 */
	public String prepare(String origUrl) {
		//简单检查合法性
		if(origUrl == null || origUrl.equals(""))
			return null;
		if(!origUrl.startsWith("http://")) {
			Log.i(TAG, "本地文件，直接播放");
			return origUrl;
		}
		mOrigUrl = origUrl;
		//1.停止当前正在进行的任务
		stopProxy();
		
		//2.判断是否已经有该任务
		String keyName = MD5Util.getMD5String(mOrigUrl);
		keyName = ProxyUtils.getValidFileName(keyName);
		
		Log.d(TAG, "key name = " + keyName);
		
		switch(taskExist(keyName)) {
		case 0://存在完整mp4
			state = STATE_COMPLETED;
			return mBufferDirPath + "/" + keyName + ".mp4";
		case -1://新任务
			if (!ZNetworkStateDetector.isAvailable()) {
				state = STATE_ERROR;
				errorno = PROXY_ERROR_NETWORK;
				return null;
			}
		case 1://已经有缓存
			//获取文件大小
			String[] targetinfo = getTargetSize(mOrigUrl);
			mTmpFileSize = Integer.parseInt(targetinfo[0]);
			mMineType = targetinfo[1];
			if(mMineType != null && userID != null && !userID.equals("")) {
				if(mMineType.startsWith("video")) {
					mBufferDirPath = Environment.getExternalStorageDirectory() + "/.looklook/" + userID + "/tmp/video";
				} else if(mMineType.startsWith("audio")) {
					mBufferDirPath = Environment.getExternalStorageDirectory() + "/.looklook/" + userID + "/tmp/audio";
				}
			}
			//创建临时文件
			createTmpFile(keyName);
			
			if(mTmpFileSize <= 0) {
				Log.w(TAG, "获取不到文件总大小, 读记录");
				//TODO:
				Map<String, String> map = getMediaInfo(origUrl);
				if(!map.isEmpty()) {
					mTmpFileSize = Integer.parseInt(map.get("size"));
					Log.i(TAG, "读取到文件总大小[" + mTmpFileSize + "]");
				}
			} else {
				persistMediaInfo(origUrl, mTmpFileSize + "");
			}
			break;
		}
		
		//判断空间是否够用
		if(!getSpaceEnable()) {
			state = STATE_ERROR;
			errorno = PROXY_ERROR_NO_SPACE;
			if(tmpfile.exists())
				tmpfile.delete();
			return null;
		}
		
		//3.获取可用地址
		if (ZNetworkStateDetector.isAvailable()) {
			// 排除HTTP特殊,如重定向
			Log.e(TAG, "排除HTTP特殊,如重定向");
			mValidUrl = ProxyUtils.getRedirectUrl(origUrl);
		} else {
			Log.e(TAG, "直接使用原始url");
			mValidUrl = origUrl;
		}
		Log.e("Http mMediaUrl", mValidUrl);
		
		//4. ----获取对应本地代理服务器的链接----//
		String localUrl = "";
		URI originalURI = URI.create(mValidUrl);
		remoteHost = originalURI.getHost();

		if (originalURI.getPort() != -1) {// URL带Port
			serverAddress = new InetSocketAddress(remoteHost, originalURI.getPort());// 使用默认端口
			remotePort = originalURI.getPort();// 保存端口，中转时替换
			localUrl = mValidUrl.replace(remoteHost + ":" + originalURI.getPort(), localHost + ":" + localPort);
		} else {// URL不带Port
			serverAddress = new InetSocketAddress(remoteHost, ProxyConfig.HTTP_PORT);// 使用80端口
			remotePort = -1;
			localUrl = mValidUrl.replace(remoteHost, localHost + ":" + localPort);
		}
		state = STATE_PREPARED;
		
		//5.启动代理服务
		startProxy();
		Log.i(TAG, "代理准备就绪...");
		mLocalUrl = localUrl;
		return localUrl;
	}
	
	private void createTmpFile(String key) {
		/*创建临时文件*/
		mTmpFilePath = mBufferDirPath + "/" + key + ".tmp";
		File f = new File(mBufferDirPath);
		if(!f.exists()) {
			f.mkdirs();
		}
		tmpfile = new File(mTmpFilePath);
		if(!tmpfile.exists()) {
			try {
				Log.i(TAG, "临时文件创建中...");
				tmpfile.createNewFile();
//				tmpfile = new ProxyTempFile(mTmpFilePath, mTmpFileSize);
			} catch (Exception e) {
				state = STATE_ERROR;
				errorno = PROXY_ERROR_FAIL_CREATE_FILE;
				e.printStackTrace();
			}
		}
		Log.i(TAG, "临时文件准备完毕...");
	}
	
	/**
	 * -1.还没有缓存  0.已经缓存好  1.在缓存中
	 */
	private int taskExist(String key) {
		if(mMineType.startsWith("video")) {
			mBufferDirPath = Environment.getExternalStorageDirectory() + "/.looklook/" + userID + "/tmp/video";
		} else if(mMineType.startsWith("audio")) {
			mBufferDirPath = Environment.getExternalStorageDirectory() + "/.looklook/" + userID + "/tmp/audio";
		}
		File tmpFile = new File(mBufferDirPath + "/" + key + ".tmp");
		File avFile = new File(mBufferDirPath + "/" + key + ".mp4");
		if(avFile.exists()) {
			Log.i(TAG, "直接播放本地文件。");
			return 0; //已经缓存好
		} else if(tmpFile.exists()) {
			Log.i(TAG, "已有缓存，继续边下边播。");
			return 1; //在缓存中
		}
		Log.i(TAG, "新地址，启动边下边播。");
		return -1; //还没有缓存
	}
	
	private String[] getTargetSize(String remoteurl) {
		String[] ret = {"0", "0"};
		if (ZNetworkStateDetector.isAvailable()) {
			try {
				URL url = new URL(remoteurl);
				HttpURLConnection urlConnection = (HttpURLConnection) url
						.openConnection();
				ret[0] = urlConnection.getContentLength() + "";
				ret[1] = urlConnection.getContentType();
				Log.d(TAG, "MineType = " + ret[1]);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return ret;
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
		} catch (IOException e1) {
		}
	}
	
	private void persistMediaInfo(String path, String size) {
		if (mTmpFilePath != null && !mTmpFilePath.equals("")
				&& mTmpFileSize > 0) {
			Map<String, String> ca = new HashMap<String, String>();
			ca.put("path", path/*mTmpFilePath*/);
			ca.put("size", size/*mTmpFileSize + ""*/);
			ai.cachefileinfo.add(ca);
			ai.persist();
		}
	}
	
	private Map<String, String> getMediaInfo(String path) {
		Map<String, String> tmp = new HashMap<String, String>();
		if(ai != null && ai.cachefileinfo != null) {
			for(Map<String, String> ca : ai.cachefileinfo) {
				if(ca.get("path").equals(path)) {
					tmp = ca;
					break;
				}
			}
		}
		return tmp;
	}
	
	/**
	 * 空间是否够用
	 * @return
	 */
	private boolean getSpaceEnable() {
		boolean mEnable = false;
		// 判断外部存储器是否可用
		File dir = new File(mBufferDirPath);
		mEnable = dir.exists();
		if (!mEnable)
			return mEnable;
		// 获取可用空间大小
		long freeSize = ProxyUtils.getAvailaleSize(mBufferDirPath);
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
		private boolean isStop = false;
		
		public MPConnector() {
			ProxyLog(TAG_MP, "remoteHost " + remoteHost + "\n"
					+ "remotePort " + remotePort + "\n"
					+ "localHost " + localHost + "\n"
					+ "localPort " + localPort + "\n");
//			sentResponseHeader = false;
		}
		
		public void execute() {
			state = STATE_CACHING;
			isStop = false;
			this.start();
		}
		
		public void stopThread() {
			ProxyLog(TAG_MP, "stop MPConnector");
			state = STATE_STOP;
			this.isStop = true;
		}
		
		@Override
		public void run() {
			
			state = STATE_CACHING;
			
			while(!isStop) {
				try {
					if(localServer == null || localServer.isClosed()) {
						localServer = new ServerSocket(0, 1,InetAddress.getByName(localHost));
						localPort =localServer.getLocalPort();//有ServerSocket自动分配端口
					}
					Socket s = localServer.accept();
					ProxyLog(TAG_MP, "Socket timeout = " + s.getSoTimeout());
					if(gUtils == null)
						gUtils = new HttpGetProxyUtils(s, serverAddress);
					Log.i(TAG_MP, "<-----------------MP端有新请求,socket:[" + s + "]----------------->");
					
					int bytes_read;
					byte[] local_request = new byte[1024];
					ProxyRequest request = null;
					
					try {
						while ((bytes_read = s.getInputStream().read(local_request)) != -1) {
							byte[] buffer = httpParser.getRequestBody(local_request,
									bytes_read);
							if (buffer != null) {
								request = httpParser.getProxyRequest(buffer);
								Log.i(TAG_MP, "...... A.读取请求[" + ZByteToSize.smartSize(request._rangePosition) + "] ......");
								break;
							}
						}
						
						if(request != null) {
							ProxyRequestWrapper wrapper = new ProxyRequestWrapper(request, s, false);
							//为了防止播放线程等待上次请求而死锁
							if(mProxyPlayer != null) {
								mProxyPlayer.stopThread();
								mProxyPlayer = null;
							}
							mProxyPlayer = new ProxyPlayer();
							mProxyPlayer.execute();
							fifo.put(wrapper);
						}
					} catch (IOException e1) {
						e1.printStackTrace();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			
			Log.i(TAG_MP, "......play over...........");
		}
	}
	
	private class ProxyPlayer extends Thread {

		private final static String TAG_PROXY = "Proxy";
		private String prefix = "";
		private boolean isStop = false;
		private boolean isEnd = false;
		private boolean isCacheOK = false;
		private boolean sentResponseHeader = false;
		
		private Socket mSocket = null;
		
		private HttpGetProxyUtils proxyUtils = null;
		
		private ProxyRequestWrapper requestWrapper = null;

		ProxyRequest reqRange = null;
		
		public ProxyPlayer() {
			prefix = UUID.randomUUID().toString().substring(0, 8);
			ProxyLog(TAG_PROXY + prefix, "...... Proxy Construction ......");
//			mSocket = s;
//			proxyUtils = new HttpGetProxyUtils(s, serverAddress);
		}

		public void execute() {
			this.start();
		}

		public Socket getSocket() {
			return mSocket;
		}
		
		public void stopThread() {
			ProxyLog(TAG_PROXY + prefix, "...... 停止ProxyPlayer代理，stopThread ......");
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
					e.printStackTrace();
				}
			}
		}
		@Override
		public void run() {

			// HttpGetProxyUtils utils = null;
			ProxyRequest request = null;
			
			while (!isStop) {
				if(!fifo.isEmpty()) {
					try {
						requestWrapper = null;
						requestWrapper = fifo.take();
						sentResponseHeader = false;
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					
					if(requestWrapper != null) {
						request = requestWrapper.pr;
						Log.i(TAG_PROXY + prefix, "...... A.收到Player请求[" + request._rangePosition + "] ......");
						proxyUtils = new HttpGetProxyUtils(requestWrapper.s, serverAddress);
						mSocket = requestWrapper.s;
					}
				} else {
					if(reqRange != null) {
						request = reqRange;
						reqRange = null;
					}
				}
				
				try {
					if (request != null) {// MediaPlayer的request有效
						ProxyLog(TAG_PROXY + prefix, "...... B.请求[" + request._rangePosition + "] ......");
						if (tmpfile == null) {
							// tmpfile = new ProxyTempFile(mTmpFilePath,
							// mTmpFileSize);
							tmpfile = new File(mTmpFilePath);
						}
						
						if(downloader != null && !downloader.isDownloading() && !downloader.isComplete()) {
							downloader.stopThread();
							downloader = new CacheDownloader(mValidUrl, tmpfile, 0, mTmpFileSize);
							downloader.startThread();
						}
						
						if (!sentResponseHeader) {
							ProxyLog(TAG_PROXY + prefix, "...... D0.先返回伪装http头到MP ......");
							proxyResponse = proxyUtils.fadeResponseHeader(mTmpFileSize, request._rangePosition, httpParser);
							proxyUtils.sendToMP(proxyResponse._body);
							sentResponseHeader = true;
						}
						long downloadedsize = downloader.getDownloadedSize();
						isEnd = downloadedsize >= mTmpFileSize;
						isCacheOK = (downloadedsize - CACHE_BYTE_DATA_SIZE) > request._rangePosition;
						boolean isInRange = isCacheOK || isEnd;
//						Log.d(TAG_PROXY + prefix, "downloaded : " + downloadedsize + "; range : " + request._rangePosition);
						if (isInRange) {
							ProxyLog(TAG_PROXY + prefix, "...... C.在缓存中，需要读取缓存 ......");
//							Log.i(TAG_PROXY + prefix, "request : " + request._rangePosition + "; cache : [ 0 - " + downloader.getDownloadedSize() + "]");
							if (!sentResponseHeader) {
								ProxyLog(TAG_PROXY + prefix, "...... D.先返回伪装http头到MP ......");
								proxyResponse = proxyUtils.fadeResponseHeader(mTmpFileSize, request._rangePosition, httpParser);
								proxyUtils.sendToMP(proxyResponse._body);
								sentResponseHeader = true;
							}
							int sentBufferSize = 0;
							if(request._rangePosition < downloadedsize) {
								ProxyLog(TAG_PROXY + prefix, "...... E.返回读取的缓存数据到MP ......");
								sentBufferSize = proxyUtils.sendPrebufferToMP3(mTmpFilePath, request._rangePosition, downloadedsize - CACHE_BYTE_DATA_SIZE);
							}
							if (sentBufferSize > 0) {// 成功发送缓存，重新发送请求到服务器
								ProxyLog(TAG_PROXY + prefix, "...... F.成功发送缓存(" + sentBufferSize + "byte)，修改新range ......");
								// 修改Range后的Request发送给服务器
								int newRange = (int) (sentBufferSize + request._rangePosition);
								// String newRequestStr =
								// httpParser.modifyRequestRange(request._body,
								// newRange);
								request._rangePosition = newRange;
//								Thread.sleep(2000);
								continue;
							} else {
								if(isEnd)
									ProxyLog(TAG_PROXY + prefix, "...... E.数据已全部发送完毕 ......");
								else
									ProxyLog(TAG_PROXY + prefix, "...... E.缓存不够，不发送缓存数据到MP......");
//								Thread.sleep(2000);
								continue;
							}
						} else {
							ProxyLog(TAG_PROXY + prefix, "...... C.等待缓存 ......");
							state = STATE_WAITING;
							Thread.sleep(2000);
							continue;
						}
					} else {
//						ProxyLog(TAG_PROXY + prefix, "...... B.request不合法 ......");
					}
				} catch (Exception e) {
					ProxyLog(TAG_PROXY + prefix, "...... F.Socket出错, socket:[" + mSocket + "] ......");
				}
				// closeSocket();
			}
			ProxyLog(TAG_PROXY + prefix, "...... G.Proxy OVER! ......");
//			closeSocket();
		}
	}
	
	private void sendMessage(int msg, String extra) {
		Intent intent = new Intent(ProxyConfig.HTTPPROXY_RESULT_MSG);
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
