package com.cmmobi.looklook.v31.httpproxybak;

import java.io.File;
import java.net.HttpURLConnection;
import java.util.HashMap;

import org.apache.http.protocol.HTTP;

import android.util.Log;
import cn.zipper.framwork.core.ZLog;
import cn.zipper.framwork.io.network.ZHttp2;
import cn.zipper.framwork.io.network.ZHttpReader;
import cn.zipper.framwork.io.network.ZHttpResponse;
import cn.zipper.framwork.utils.ZPercent;
import cn.zipper.framwork.utils.ZPercent.OnPercentChangedListener;

public class CacheDownloader extends Thread {
	
	static private final String TAG = "CacheDownloader";
	
	private String mRemoteUrl, mLocalUrl;
	private ProxyTempFile tmpFile;
	
	private int mSize, mRange;
	private int mRetryTimes;
	private long mDownloadSize;//已经下载的大小
	
	private boolean mStop;
	
	private ZHttp2 http;
	private ZHttpReader reader;
	public CacheDownloader(String url, ProxyTempFile file, int range, int targetSize) {
		if(url != null && file != null) {
			mRemoteUrl = url;
			mLocalUrl = file.getFilePath();
			tmpFile = file;
			mSize = targetSize;
			mRange = range;
		}
		mDownloadSize = 0;
		mRetryTimes = 3;
		mStop = false;
	}
	
	public void startThread(int range) {
		Log.d(TAG, "start downloading from [" + range + "]");
		mRange = range;
		this.start();
	}
	
	public void stopThread() {
		mStop = true;
	}
	
	@Override
	public void run() {
		download();
	}
	
	private void retry() {
		mRetryTimes --;
		if(mRetryTimes > 0) {
			Log.d("==WR==", "下载失败，重试！");
			download();
		} else {
			Log.d("==WR==", "重试失败！");
//			mError = true;
		}
	}
	
	private synchronized void download() {
		
		try {
//			synchronized (Thread.currentThread()) {
//				Thread.currentThread().wait(10000);				
//			}
			HashMap<String, String> headers = new HashMap<String, String>();
			
			http = new ZHttp2();
			http.setHeaders(headers);
			ZLog.e("mRemoteUrl=" + mRemoteUrl);
			ZHttpResponse response = http.get(mRemoteUrl);
//			 response.printHeaders();
			ZLog.e(response==null?"response is null":"response is not null");
			if (response != null && response.getResponseCode() == HttpURLConnection.HTTP_OK ||
					response.getResponseCode() == HttpURLConnection.HTTP_PARTIAL) { // 断点续传;
				
			} else {
				//TODO:
				Log.d("==WR==", "http响应为空，任务重试！");
				retry();
				return;
			}
			ZLog.e("content length = " + response.getHeader(HTTP.CONTENT_LEN));
			long contentLength = 0;
			
			if(mDownloadSize >= mSize - mRange) {
				Log.d("==WR==", "已经下载完毕！");
				removeTmpFile();
//				mIsComplete = true;
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
							
//							writer.writeBlock(0, bytes);
							tmpFile.writeTmpFile(mRange, bytes);
							mRange += bytes.length;
							
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
			http.close();
			if(reader.isEnding()) {
//			mIsComplete = true;
				removeTmpFile();
				Log.d("==WR==", "已经全部下载完毕！");
			}
//			removeTmpFile();
		} catch (Exception e) {
			e.printStackTrace();
			closeHttp();
			Log.d("==WR==", "下载过程发生异常，任务重试！");
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
