package com.cmmobi.looklook.v31.httpproxybak;


import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.http.protocol.HTTP;

import cn.zipper.framwork.core.ZLog;
import cn.zipper.framwork.io.file.ZFileWriter;
import cn.zipper.framwork.io.network.ZHttp2;
import cn.zipper.framwork.io.network.ZHttpReader;
import cn.zipper.framwork.io.network.ZHttpResponse;
import cn.zipper.framwork.utils.ZPercent;
import cn.zipper.framwork.utils.ZPercent.OnPercentChangedListener;

import android.util.Log;
/**
 * 下载模块
 * @author Ray
 * 
 */
public class DownloadThread1 extends Thread {
	static private final String TAG="DownloadThread";
	private String mUrl;
	private String mPath;
	private ZHttp2 http;
	private ZHttpReader reader;
	private ZFileWriter writer;
	private long mDownloadSize;
	
	private long mDownloadTotalSize;
	
	private int mTargetSize;
	private int mRetryTimes;
	private boolean mStop,mDeleteFile;
	private boolean mDownloading, mIsComplete;
	private boolean mStarted;
	private boolean mError;
	
	public DownloadThread1(String url, String savePath,int targetSize) {
		mUrl = url;
		mPath = savePath;
		mTargetSize=targetSize;
		mDownloadSize = 0;
		mRetryTimes = 3;
		
		mStop = false;
		mDeleteFile=false;
		mDownloading = false;
		mIsComplete = false;
		mStarted = false;
		mError=false;
	}

	@Override
	public void run() {
		mDownloading = true;
		if(!mIsComplete) {
			download2();
		}
	}
	
	/** 启动下载线程 */
	public void startThread() {
		if (!mStarted) {
			getTargetSize();
			this.start();
			// 只能启动一次
			mStarted = true;
		}
	}

	/** 停止下载线程, deleteFile是否要删除临时文件 */
	public void stopThread(boolean deleteFile) {
		mStop = true;
		mDeleteFile=deleteFile;
		
	}

	/** 是否正在下载 */
	public boolean isDownloading() {
		return mDownloading;
	}

	/**
	 * 是否下载异常
	 * @return
	 */
	public boolean isError(){
		return mError;
	}
	
	public long getDownloadedSize() {
		return mDownloadSize;
	}
	
	public long getTotalSize() {
		return mDownloadTotalSize;
	}
	
	public String getRemoteUrl() {
		return mUrl;
	}

	private void retry() {
		mRetryTimes --;
		if(mRetryTimes > 0) {
			Log.d("==WR==", "下载失败，重试！");
			download2();
		} else {
			Log.d("==WR==", "重试失败！");
			mError = true;
		}
	}
	
	private void removeTmpFile() {
		String newPath = mPath.replace(".tmp", ".mp4");
		File file = new File(mPath);
		File newfile = new File(newPath);
		if(file.exists()) {
			file.renameTo(newfile);
		}
	}
	
	/** 是否下载成功 */
	public synchronized boolean isDownloadSuccessed() {
		return (mDownloadSize != 0 && mDownloadSize == mTargetSize || mIsComplete);
	}

	private synchronized void getTotal() throws Exception {
		URL url = new URL(mUrl);
		URLConnection con = url.openConnection();

		mDownloadTotalSize = con.getContentLength();
	}
	
	private void getTargetSize() {
		try {
			URL url = new URL(mUrl);
			HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
			mDownloadTotalSize = urlConnection.getContentLength();
		} catch (Exception e) {
			Log.i(TAG, "getTargetSize error:" + e.toString() + "");
			Log.i(TAG, Utils.getExceptionMessage(e));
		}
	}
	
	private synchronized void download2() {
		
		try {
//			synchronized (Thread.currentThread()) {
//				Thread.currentThread().wait(10000);				
//			}
			HashMap<String, String> headers = new HashMap<String, String>();
			long fileLength = 0;
			
			ZLog.e("df.path = " + mPath);
			File file = new File(mPath);
			if(!file.getParentFile().exists()) {
				file.getParentFile().mkdirs();
			}
			if (file.exists()) {
				fileLength = file.length();
				headers.put("range", "bytes=" + fileLength + "-");
				ZLog.e("range = " + "bytes=" + fileLength + "-");
			}
			ZLog.e("fileLength = " + fileLength + " path=" + file.getAbsolutePath());
//			getTargetSize();
			
			mDownloadSize = fileLength;
			
			http = new ZHttp2();
			http.setHeaders(headers);
			ZLog.e("mUrl=" + mUrl);
			ZHttpResponse response = http.get(mUrl);
//			 response.printHeaders();
			
			ZLog.e(response==null?"response is null":"response is not null");
			if (response != null && response.getResponseCode() == HttpURLConnection.HTTP_OK) { // 从头下载;
				Log.d("==WR==", "http回传[200]，从头开始下载");
				if (file.exists()) {
					mDownloadSize = 0;
					file.delete();
				}
				writer = new ZFileWriter(file, false, null);
				ZLog.e(200);
			} else if (response != null && response.getResponseCode() == HttpURLConnection.HTTP_PARTIAL) { // 断点续传;
				Log.d("==WR==", "http回传[206]，断点续传模式下载");
				Log.d("==WR==", "已经下载:" + mDownloadSize);
				writer = new ZFileWriter(file, true, null);
				ZLog.e(206);
			} else if(response == null) {
				//TODO:
				mDownloadTotalSize = -1;
				Log.d("==WR==", "http响应为空，任务重试！");
				retry();
				return;
			}
			ZLog.e("content length = " + response.getHeader(HTTP.CONTENT_LEN));
			long contentLength = 0;
			if(response.getHeader(HTTP.CONTENT_LEN) != null) {
				contentLength = Long.valueOf(response.getHeader(HTTP.CONTENT_LEN));
				mDownloadTotalSize = contentLength > 0 ? contentLength : -1;
			}
			
			if(mDownloadTotalSize <= mDownloadSize) {
				Log.d("==WR==", "已经下载完毕！");
				mIsComplete = true;
				return;
			}

			OnPercentChangedListener listener = new OnPercentChangedListener() {
				@Override
				public void onPercentChanged(ZPercent percent) {
					try {
						if (mStop) {
							Log.d("==WR==", "下载过程暂停");
							ZLog.e("STATE_PAUSED while reading");
							if (reader != null) {
								reader.stop();
							}
						}
						Object object = percent.getObject();
						if (object != null) {
							byte[] bytes = (byte[]) object;
							// ZLog.e("read length = " + bytes.length);
							writer.writeBlock(0, bytes);
							addStep(bytes.length);
//							ZThread.sleep(5);
						}
					} catch (Exception e) {
						e.printStackTrace();
						if (reader != null) {
							reader.stop();
						}
					}
				}
			};
			reader = new ZHttpReader(response.getInputStream(), listener);
			if(!reader.readByBlockSize2(contentLength, 1024 * 4)) {
				//TODO:
				Log.d("==WR==", "下载读取数据时出错，重试！");
				retry();
				return;
			}
			reader.close();
			writer.close();
			http.close();
			mIsComplete = true;
			Log.d("==WR==", "已经全部下载完毕！");
			removeTmpFile();
		} catch (Exception e) {
			e.printStackTrace();
			closeHttp();
			Log.d("==WR==", "下载过程发生异常，任务重试！");
			retry();
		}

	}
	
	private void closeHttp() {
		if (reader != null) {
			reader.close();
		}
		if (writer != null) {
			writer.close();
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
