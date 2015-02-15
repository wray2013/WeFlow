package com.cmmobi.looklook.httpproxy.downloader;

import java.io.File;
import java.net.HttpURLConnection;
import java.util.HashMap;
import java.util.UUID;

import org.apache.http.protocol.HTTP;

import com.cmmobi.looklook.httpproxy.utils.HttpGetProxyUtils;
import com.cmmobi.looklook.httpproxy.utils.ProxyTempFile;

import android.util.Log;

import cn.zipper.framwork.core.ZLog;
import cn.zipper.framwork.io.file.ZFileWriter;
import cn.zipper.framwork.io.network.ZHttp2;
import cn.zipper.framwork.io.network.ZHttpReader;
import cn.zipper.framwork.io.network.ZHttpResponse;
import cn.zipper.framwork.utils.ZPercent;
import cn.zipper.framwork.utils.ZPercent.OnPercentChangedListener;

/**
 * 
 * @author Ray
 *
 */
public class CacheDownloaderWithSeek extends Thread {
	
	static private final String TAG_CD = "CacheDownloader";
	private static boolean OPEN_LOG = true;//false;
	
	private String prefix = "";
	
	private String mRemoteUrl, mLocalUrl;
	private ProxyTempFile tmpFile;
	
	private int mSize, mRange;
	private int mRetryTimes;
	private long mDownloadSize;//已经下载的大小
	
	private boolean mStop, misAlive, misCompleted;
	
	private ZHttp2 http;
	private ZHttpReader reader;
	
	private HttpGetProxyUtils gUtils;
	
	public CacheDownloaderWithSeek(HttpGetProxyUtils utils, String url, ProxyTempFile file, int range, int targetSize) {
		prefix = UUID.randomUUID().toString().substring(0, 8);
		if(url != null && file != null) {
			mRemoteUrl = url;
			mLocalUrl = file.getFilePath();
			tmpFile = file;
			mSize = targetSize;
			mRange = range;
			gUtils = utils;
		}
		mDownloadSize = 0;
		mRetryTimes = 3;
		mStop = false;
		misAlive = false;
		misCompleted = false;
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
//			mError = true;
		}
	}
	
	public boolean targetFileExist() {
		boolean isExist = false;
		String newPath = mLocalUrl.replace(".tmp", ".mp4");
		File newfile = new File(newPath);
		if(newfile.exists()) {
			isExist = true;
		}
		return isExist;
	}
	
	public boolean isComplete() {
		return misCompleted;
	}
	
	private synchronized void download() {
		
		try {
			if (targetFileExist()) {
				ProxyLog(TAG_CD+prefix, "已经下载过该文件");
				misCompleted = true;
				return;
			}
			misAlive = true;
//			synchronized (Thread.currentThread()) {
//				Thread.currentThread().wait(10000);				
//			}
			HashMap<String, String> headers = new HashMap<String, String>();
			headers.put("range", "bytes=" + mRange + "-");
			http = new ZHttp2();
			http.setHeaders(headers);
			ZLog.e("headers=" + headers);
			ZLog.e("mRemoteUrl=" + mRemoteUrl);
			ZHttpResponse response = http.get(mRemoteUrl);
//			 response.printHeaders();
			ZLog.e(response==null?"response is null":"response is not null");
			if (response != null) {
				if (response.getResponseCode() == HttpURLConnection.HTTP_PARTIAL ||
						response.getResponseCode() == HttpURLConnection.HTTP_OK) { // 重新传;
					ProxyLog(TAG_CD+prefix, "http回传[200]，从头开始下载");
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
				misCompleted = true;
//				removeTmpFile();
//				mIsComplete = true;
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
							//发送回MediaPlayer
							gUtils.sendToMP(bytes, bytes.length);
							//写入临时文件
							tmpFile.writeTmpFile(mRange, bytes);
							mRange += bytes.length;
							addStep(bytes.length);
//							ZThread.sleep(5);
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
				misCompleted = true;
//				removeTmpFile();
				ProxyLog(TAG_CD+prefix, "已经全部下载完毕！");
			}
			closeHttp();
//			removeTmpFile();
		} catch (Exception e) {
			e.printStackTrace();
			closeHttp();
			ProxyLog(TAG_CD+prefix, "下载过程发生异常，任务重试！");
			retry();
		}
	}
	
	public void removeTmpFile() {
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
	
	private void ProxyLog(String tag, String log) {
		if(OPEN_LOG) {
			Log.d(tag, log);
		}
	}
}
