package com.cmmobi.looklook.httpproxy;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketAddress;

import com.cmmobi.looklook.httpproxy.Config.ProxyResponse;
import com.cmmobi.looklook.httpproxy.Config.ProxyResponseWrapper;

import android.util.Log;
/**
 * 代理服务器工具类
 * @author Ray
 *
 */
public class HttpGetProxyUtils {
	final static public String TAG = "HttpGetProxyUtils";
	final static public int ERROR_FILE_NOT_EXIST  = -1;
	final static public int ERROR_OUT_OF_RANGE    = -2;
	final static public int ERROR_CACHE_TOO_SMALL = -3;
	
	/** 收发Media Player请求的Socket */
	private Socket mSckPlayer = null;

	/**服务器的Address*/
	private SocketAddress mServerAddress;
	
	public HttpGetProxyUtils(Socket sckPlayer,SocketAddress address){
		mSckPlayer=sckPlayer;
		mServerAddress=address;
	}
	
	/**
	 * 发送缓存至服务器
	 * @param fileName 缓存文件
	 * @param range skip的大小
	 * @return 已发送的大小，不含skip的大小
	 * @throws Exception
	 */
	public int sendPrebufferToMP(String fileName,long range){
		final int MIN_SIZE= 100*1024;
		int fileBufferSize=0;

		byte[] file_buffer = new byte[1024];
		int bytes_read = 0;
		long startTimeMills = System.currentTimeMillis();

		File file = new File(fileName);
		if (file.exists() == false) {
			Log.i(TAG, ">>>不存在缓存文件");
			return ERROR_FILE_NOT_EXIST;
		}
		if (range > (file.length() - MIN_SIZE)) {// Range大小超过预缓存的太小
			Log.i(TAG,">>>不读取缓存文件 range:" + range + ",buffer:" + file.length());
			return ERROR_OUT_OF_RANGE;
		}

		if (file.length() < MIN_SIZE) {// 可用的预缓存太小，没必要读取以及重发Request
			Log.i(TAG, ">>>缓存文件太小，不读取缓存");
			return ERROR_CACHE_TOO_SMALL;
		}
		
		FileInputStream fInputStream = null;
		try {
			fInputStream = new FileInputStream(file);
			if (range > 0) {
				byte[] tmp = new byte[(int) range];
				long skipByteCount = fInputStream.read(tmp);
				Log.i(TAG, ">>>skip:" + skipByteCount);
			}

			while ((bytes_read = fInputStream.read(file_buffer)) != -1) {
				mSckPlayer.getOutputStream().write(file_buffer, 0, bytes_read);
				fileBufferSize += bytes_read;//成功发送才计算
			}
			mSckPlayer.getOutputStream().flush();
			
			long costTime = (System.currentTimeMillis() - startTimeMills);
			Log.i(TAG, ">>>读取缓存耗时:" + costTime);
			Log.i(TAG, ">>>读取完毕...下载:" + file.length() + ",读取:"+ fileBufferSize);
		} catch (Exception ex) {
			Log.e(TAG, ex.toString());
		} finally {
			try {
				if (fInputStream != null)
					fInputStream.close();
			} catch (IOException e) {
			}
		}
		return fileBufferSize;
	}

	/**
	 * 发送缓存至服务器
	 * @param fileName 缓存文件
	 * @param range skip的大小
	 * @param max   能读取的最大长度
	 * @return 已发送的大小，不含skip的大小
	 * @throws Exception
	 */
	public int sendPrebufferToMP2(String fileName, long range, long max){
		int fileBufferSize=0;

		byte[] file_buffer = new byte[1024];
		int bytes_read = 0;
		long startTimeMills = System.currentTimeMillis();

		File file = new File(fileName);
		if (file.exists() == false) {
			Log.i(TAG, ">>>不存在缓存文件");
			return ERROR_FILE_NOT_EXIST;
		}
		if (range > file.length()) {// Range大小超过文件大小
			Log.i(TAG,">>>不读取缓存文件 range:" + range + ",buffer:" + file.length());
			return ERROR_OUT_OF_RANGE;
		}

		FileInputStream fInputStream = null;
		try {
			fInputStream = new FileInputStream(file);
			if (range > 0) {
				byte[] tmp = new byte[(int) range];
				long skipByteCount = fInputStream.read(tmp);
				Log.i(TAG, ">>>skip:" + skipByteCount);
			}
			while ((bytes_read = fInputStream.read(file_buffer)) != -1) {
				if(fileBufferSize + bytes_read > max) {
					break;
				}
				mSckPlayer.getOutputStream().write(file_buffer, 0, bytes_read);
				fileBufferSize += bytes_read;//成功发送才计算
			}
			mSckPlayer.getOutputStream().flush();
			
			long costTime = (System.currentTimeMillis() - startTimeMills);
			Log.i(TAG, ">>>读取缓存耗时:" + costTime);
			Log.i(TAG, ">>>读取完毕...读取:[" + range + " - " + (range + fileBufferSize) + "]");
		} catch (Exception ex) {
			Log.e(TAG, ex.toString());
		} finally {
			try {
				if (fInputStream != null)
					fInputStream.close();
			} catch (IOException e) {
			}
		}
		return fileBufferSize;
	}
	
	/**
	 * 把服务器的Response的Header去掉
	 * @throws IOException 
	 */
	public ProxyResponse removeResponseHeader(Socket sckServer,HttpParser httpParser)throws IOException {
		ProxyResponse result = null;
		int bytes_read;
		byte[] tmp_buffer = new byte[1024];
		while ((bytes_read = sckServer.getInputStream().read(tmp_buffer)) != -1) {
			result = httpParser.getProxyResponse(tmp_buffer, bytes_read);
			if (result == null)
				continue;// 没Header则退出本次循环

			// 接收到Response的Header
			if (result._other != null) {// 发送剩余数据
				sendToMP(result._other);
			}
			break;
		}
		return result;
	}
	
	/**
	 * 把服务器的Response的Header去掉
	 * @throws IOException 
	 */
	public ProxyResponseWrapper removeResponseHeader(Socket sckServer,HttpParser httpParser, ProxyTempFile tmpfile, long offset)throws IOException {
		ProxyResponse result = null;
		ProxyResponseWrapper wrapper = new ProxyResponseWrapper();
		int bytes_read;
		byte[] tmp_buffer = new byte[1024];
		while ((bytes_read = sckServer.getInputStream().read(tmp_buffer)) != -1) {
			result = httpParser.getProxyResponse(tmp_buffer, bytes_read);
			if (result == null)
				continue;// 没Header则退出本次循环

			// 接收到Response的Header
			if (result._other != null) {// 发送剩余数据
				offset += sendToMP(result._other, tmpfile, offset, false);
			}
			break;
		}
		wrapper._offset = offset;
		wrapper.pr = result;
		return wrapper;
	}
	
	public ProxyResponse fadeResponseHeader(long contentLength, long range, HttpParser httpParser) {
		ProxyResponse result = null;
		String fadeheader = "HTTP/1.1 206 Partial Content\r\n" +
				"Server: nginx/1.4.2\r\n" +
//				"Date: Wed, 06 Nov 2013 04:28:49 GMT" +
				"Date: " + new java.util.Date() + "\r\n" +
				"Content-Type: video/mp4\r\n" +
				"Content-Length: " + (contentLength - range) + "\r\n" +
				"Last-Modified: Sat, 17 Aug 2013 09:48:01 GMT\r\n" +
				"Connection: close\r\n" +
				"ETag: \"520f46d1-7b5abf9\"\r\n" +
				"Content-Range: bytes " + range + "-" + (contentLength - 1) + "/" + contentLength + "\r\n" +
				Config.HTTP_BODY_END;
		Log.w(TAG + "<---", fadeheader);
		byte[] source = fadeheader.getBytes();
		result = httpParser.getProxyResponse(source, source.length);
		
		return result;
	}
	
	public void sendToMP(byte[] bytes, int length) throws IOException {
		mSckPlayer.getOutputStream().write(bytes, 0, length);
		mSckPlayer.getOutputStream().flush();
	}

	public void sendToMP(byte[] bytes) throws IOException{
		if(bytes.length==0)
			return;
		mSckPlayer.getOutputStream().write(bytes);
		mSckPlayer.getOutputStream().flush();	
	}
	
	public long sendToMP(byte[] bytes, int length, ProxyTempFile tmpfile, long offset, boolean isHeader) throws IOException {
		if(tmpfile == null)
			return 0;
		mSckPlayer.getOutputStream().write(bytes, 0, length);
		mSckPlayer.getOutputStream().flush();
		if(!isHeader) {
			tmpfile.writeTmpFile((int) offset, bytes);
		} else {
			return 0;
		}
		return length;
	}

	public long sendToMP(byte[] bytes, ProxyTempFile tmpfile, long offset, boolean isHeader) throws IOException{
		if(bytes.length==0 || tmpfile == null)
			return 0;
		mSckPlayer.getOutputStream().write(bytes);
		mSckPlayer.getOutputStream().flush();
		if(!isHeader) {
			tmpfile.writeTmpFile((int) offset, bytes);
		} else {
			return 0;
		}
		return bytes.length;
	}
	
	public Socket sentToServer(String requestStr) throws IOException{
		Socket sckServer = new Socket();
		sckServer.connect(mServerAddress);
		sckServer.getOutputStream().write(requestStr.getBytes());// 发送MediaPlayer的请求
		sckServer.getOutputStream().flush();
		return sckServer;
	}
}
