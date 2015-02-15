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
import java.util.UUID;
import java.util.concurrent.LinkedBlockingQueue;

import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import cn.zipper.framwork.io.network.ZNetworkStateDetector;
import cn.zipper.framwork.utils.MD5Util;

import com.cmmobi.looklook.MainApplication;
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
public class HttpProxy0 {
	
	private final static String TAG = "HttpProxy";
	/**避免某些MediaPlayer不播放尾部就结束*/
	private final static int SIZE = 1024*1024;
	
	private final int READ_BYTE_DATA_SIZE = 1024 * 4;
	
	private static HttpProxy0 ins = null;
	
	private LinkedBlockingQueue<ProxyRequestWrapper> fifo;
	
	private MPConnector mpConnector;
	private SVConnector svConnector;
	
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
	private ProxyTempFile tmpfile = null;
	
	
	
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
	
	public enum ProxyState {
		INITIALIZING, PREPARED, CACHING, PAUSE, COMPLETED, ERROR
	};
	
	/**
	 * 取得实例
	 * @return
	 */
	public static HttpProxy0 getInstance() {
		if (ins == null || ins.state == ProxyState.ERROR) {
			ins = new HttpProxy0();
		}
		return ins;
	}
	
	/**
	 * default constructor
	 */
	private HttpProxy0() {
		fifo = new LinkedBlockingQueue<ProxyRequestWrapper>();
		state = ProxyState.INITIALIZING;
		//初始化代理服务器
		mBufferDirPath = Config.CACHE_FILE_PATH; 
		localHost = Config.LOCAL_IP_ADDRESS;
		
		try {
			localServer = new ServerSocket(0, 1,InetAddress.getByName(localHost));
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
	/**
	 * 加载网络视频，准备播放
	 * 地址重定向排除、文件大小获得、空间可用大小判断，tmp文件创建。
	 * @param origUrl 视频url
	 * @return String 本地127.0.0.1地址
	 *         null   发生错误
	 */
	public String prepare(String origUrl) {
		// 获得文件大小
		try {
			mTmpFileSize = getTargetSize(origUrl);
		} catch (Exception e) {
			state = ProxyState.ERROR;
			e.printStackTrace();
			return null;
		}
			
		if(mTmpFileSize <= 0) {
			state = ProxyState.ERROR;
			return null;
		}
			
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
		Log.d("Http mMediaUrl",mValidUrl);
		
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
		downloader = new CacheDownloader(mValidUrl, tmpfile, 0, mTmpFileSize);
		try {
			if(mpConnector != null) {
				mpConnector = null;
			}
			
			mpConnector = new MPConnector();
			mpConnector.execute();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if(svConnector != null) {
			svConnector = null;
		}
		svConnector = new SVConnector();
		svConnector.execute();
		
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
		Log.d(TAG, "filekey = " + filekey);
		String fileName = Utils.getValidFileName(filekey);
		
		mTmpFilePath = mBufferDirPath + "/" + fileName + ".tmp";
		String comfileName = mBufferDirPath + "/" + fileName + ".mp4";
		//判断文件是否存在，忽略已经缓冲过的文件
		File comFile = new File(comfileName);
		
		if (comFile.exists()) {
			Log.i(TAG, "Use Local File[" + comfileName + "]");
			return comfileName;
		}
		
		if (ZNetworkStateDetector.isAvailable()) {
			mTmpFileSize = getTargetSize(mOrigUrl);
			Log.i(TAG, "mTmpFileSize:" + mTmpFileSize);
			if(mTmpFileSize <= 0) {
				return null;
			}
			//TODO:
//			createTmpFile(mTmpFilePath, mTmpFileSize);
			tmpfile = new ProxyTempFile(mTmpFilePath, mTmpFileSize);
			
			return "OK";
		} else {
			return null;
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
	 * 
	 * @author Ray
	 *
	 */
	private class MPConnector extends Thread {
		
		private final static String TAG_MP = "MPConnector";
		private Proxy proxy = null;
		
		public MPConnector() {
			Log.d(TAG_MP, "remoteHost " + remoteHost + "\n"
					+ "remotePort " + remotePort + "\n"
					+ "localHost " + localHost + "\n"
					+ "localPort " + localPort + "\n");
//			sentResponseHeader = false;
		}
		
		public void execute() {
			this.start();
		}
		
		@Override
		public void run() {
			
			state = ProxyState.CACHING;
			
			while(state == ProxyState.CACHING) {
				try {
					Socket s = localServer.accept();
					gUtils = new HttpGetProxyUtils(s, serverAddress);
					Log.i(TAG_MP, "<-----------------MP端有新请求----------------->");
					if(proxy != null) {
						Log.i(TAG_MP, "<-----------------关闭上一次代理----------------->");
						closeSocket();
						proxy.stopThread();
						proxy = null;
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
	}
	
	/**
	 * 
	 * @author Ray
	 *
	 */
	private class SVConnector extends Thread {
		
		private final static String TAG_SV = "SVConnector";
		private ProxyRequestWrapper requestWrapper = null;
		private ProxyRequest request = null;
		private Socket svsckServer = null;
		
		public SVConnector() {
		}
		
		public void execute() {
			this.start();
		}
		
		@Override
		public void run() {
			
			int bytes_read;
			
			byte[] remote_reply = new byte[1024];
			
			while(state != ProxyState.COMPLETED || !fifo.isEmpty()) {
				Log.d(TAG_SV, ".......... Begin Loop ! ...........");
				try {
					requestWrapper = fifo.take();
					request = requestWrapper.pr;
					svsckServer = requestWrapper.s;
					Log.d(TAG_SV, "......A QUEST COMING! [" + request._rangePosition + "] ...........");
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				if(request != null) {
					try {
						long offset = request._rangePosition;
						boolean sentResponseHeader = requestWrapper.isSentHeader;
//						try {
//							if(downloader != null) {
//								downloader.stopThread();
//								downloader = null;
//							}
//							downloader = new CacheDownloader(mValidUrl, tmpfile, 0, mTmpFileSize);
//							downloader.startThread((int)offset);
//						} catch (Exception e) {
//							Log.d(TAG_SV, "......Exception Ocurred, Continue Next Loop...........");
//							Log.e(TAG_SV, e.toString());
//							Log.e(TAG_SV, Utils.getExceptionMessage(e));
//						}

						// ------------------------------------------------------
						// 把网络服务器的反馈发到MediaPlayer，网络服务器->代理服务器->MediaPlayer
						// ------------------------------------------------------
						while (svsckServer != null
								&& ((bytes_read = svsckServer.getInputStream().read(remote_reply)) != -1)) {
							if (sentResponseHeader) {
								try {
									offset += gUtils.sendToMP(remote_reply, bytes_read, tmpfile, offset, false);
								} catch (Exception e) {
									Log.e(TAG_SV, "发送异常直接退出while");
//									Log.e(TAG_SV, e.toString());
//									Log.e(TAG_SV, Utils.getExceptionMessage(e));
									break;// 发送异常直接退出while
								}
								if (proxyResponse == null)
									continue;// 没Response Header则退出本次循环
							}
							
							proxyResponse = httpParser.getProxyResponse(remote_reply, bytes_read);
							if (proxyResponse == null)
								continue;// 没Response Header则退出本次循环
							
							sentResponseHeader = true;
							
							Log.d(TAG_SV, "......SEND HEADER (" + offset + ")...........");
							offset += gUtils.sendToMP(proxyResponse._body, tmpfile, offset, true);
							
							
							boolean isExists = new File(mTmpFilePath).exists();
							tmpBlock tb2 = tmpfile.isInsideRange((int) request._rangePosition);
							if (isExists && tb2 != null) {// 需要发送缓存到MediaPlayer
								int sentBufferSize = 0;
								sentBufferSize = gUtils.sendPrebufferToMP(mTmpFilePath, request._rangePosition);
								if (sentBufferSize > 0) {// 成功发送缓存，重新发送请求到服务器
									// 修改Range后的Request发送给服务器
									int newRange = (int) (sentBufferSize + request._rangePosition);
									String newRequestStr = httpParser.modifyRequestRange(request._body, newRange);
									svsckServer = gUtils.sentToServer(newRequestStr);
									
									// 把服务器的Response的Header去掉
									ProxyResponseWrapper wrapper = null;
									Log.d(TAG_SV, "......SEND HEADER OTHER DATA B(" + offset + ")...........");
									wrapper = gUtils.removeResponseHeader(svsckServer, httpParser, tmpfile, offset);
									if (wrapper != null) {
										proxyResponse = wrapper.pr;
										offset += wrapper._offset;
									}
									continue;
								}
							}
							
							// 发送剩余数据
							if (proxyResponse._other != null) {
								Log.d(TAG_SV, "......SEND HEADER OTHER DATA (" + offset + ")...........");
								offset += gUtils.sendToMP(proxyResponse._other, tmpfile, offset, false);
							}
							Log.d(TAG_SV, "...... Loop End ......");
						}
					} catch(Exception e) {
						e.printStackTrace();
					}
				}
			}
		}
	}
	
	private class Proxy extends Thread {
		
		private final static String TAG_PROXY = "Proxy";
		private String prefix = "";
		private boolean isStop = false;
//		private Socket sckPlayer = null;
//		private Socket sckServer = null;
		
		public Proxy(Socket s) {
			prefix = UUID.randomUUID().toString().substring(0, 8);
			Log.d(TAG_PROXY+prefix, "...... Proxy Construction ......");
			sckPlayer = s;
		}
		
		public void execute() {
			this.start();
		}
		
		public void stopThread() {
			this.isStop = true;
		}
		
		@Override
		public void run() {
			
//			HttpGetProxyUtils utils = null;
			int bytes_read;

			byte[] local_request = new byte[1024];

			boolean sentResponseHeader = false;
			ProxyRequest request = null;
			while(!isStop) {
				try {
					while ((bytes_read = sckPlayer.getInputStream().read(local_request)) != -1) {
						byte[] buffer = httpParser.getRequestBody(local_request, bytes_read);
						if (buffer != null) {
							Log.d(TAG_PROXY+prefix, "...... A.读取请求 ......");
							request = httpParser.getProxyRequest(buffer);
							break;
						}
					}

//					utils = new HttpGetProxyUtils(sckPlayer, serverAddress);
					
					if (request != null) {// MediaPlayer的request有效
						Log.d(TAG_PROXY+prefix, "...... B.请求[" + request._rangePosition + "] ......");
						if(tmpfile == null) {
							tmpfile = new ProxyTempFile(mTmpFilePath, mTmpFileSize);
						}
						tmpBlock tb = tmpfile.isInsideRange((int) request._rangePosition);
						if(tb != null) {// 需要发送缓存到MediaPlayer
							Log.d(TAG_PROXY+prefix, "...... C.Mapping命中，需要读取缓存 ......");
							Log.i(TAG_PROXY+prefix, "request : " + request._rangePosition + "; cache : [" + tb.range + " - " + (tb.range + tb.size) + "]");
							if (!sentResponseHeader) {
								Log.d(TAG_PROXY+prefix, "...... D.先返回伪装http头到MP ......");
								gUtils.fadeResponseHeader(mTmpFileSize, request._rangePosition, httpParser);
								sentResponseHeader = true;
							}
							int sentBufferSize = 0;
							Log.d(TAG_PROXY+prefix, "...... E.返回读取的缓存数据到MP ......");
							sentBufferSize = gUtils.sendPrebufferToMP2(mTmpFilePath, request._rangePosition, (tb.range + tb.size - request._rangePosition));
							if (sentBufferSize > 0) {// 成功发送缓存，重新发送请求到服务器
								Log.d(TAG_PROXY+prefix, "...... F.成功发送缓存(" + sentBufferSize + "byte)，修改新range ......");
								// 修改Range后的Request发送给服务器
								int newRange = (int) (sentBufferSize + request._rangePosition);
								String newRequestStr = httpParser.modifyRequestRange(request._body, newRange);
								request._rangePosition = newRange;
								
								sckServer = gUtils.sentToServer(newRequestStr);//"Range: bytes=XXX-"
								
								ProxyResponseWrapper wrapper = null;
								Log.d(TAG_PROXY+prefix, "......SEND HEADER OTHER DATA A(" + request._rangePosition + ")...........");
								wrapper = gUtils.removeResponseHeader(sckServer, httpParser, tmpfile, request._rangePosition);
								if (wrapper != null) {
									proxyResponse = wrapper.pr;
									request._rangePosition = wrapper._offset;
								}
								fifo.put(new ProxyRequestWrapper(request, sckServer, true));
							} else {
								Log.d(TAG_PROXY+prefix, "...... F.发送缓存失败 ......");
								break;
							}
						} else {
							Log.d(TAG_PROXY+prefix, "...... C.未命中，直接发送请求到服务器 ......");
							sckServer = gUtils.sentToServer(request._body);// 发送MediaPlayer的request
							fifo.put(new ProxyRequestWrapper(request, sckServer, false));
						}
					}
				} catch (SocketException ex) {
					Log.d(TAG_PROXY+prefix, "...... Socket出错 ......");
//					fifo.put(new ProxyRequestWrapper(request, sckServer, true));
					break;
				} catch (Exception e) {
					Log.e(TAG_PROXY+prefix, e.toString());
					Log.e(TAG_PROXY+prefix, Utils.getExceptionMessage(e));
				}
				
			}
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
	
}
