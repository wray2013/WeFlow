package com.cmmobi.looklook.httpproxy.downloader;

import java.io.File;
import java.net.HttpURLConnection;
import java.util.HashMap;
import java.util.UUID;

import org.apache.http.protocol.HTTP;

import com.cmmobi.looklook.info.profile.AccountInfo;
import com.cmmobi.looklook.info.profile.ActiveAccount;
import com.cmmobi.looklook.info.profile.MediaValue;

import android.content.Intent;
import android.os.Environment;
import android.util.Log;

import cn.zipper.framwork.core.ZApplication;
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
public class CacheDownloader extends Thread {
	
	static private final String TAG_CD = "CacheDownloader";
	private static boolean OPEN_LOG = true;//false;
	
	public static final String PROXY_ACTION_UPDATE_MAPPING = "PROXY_ACTION_UPDATE_MAPPING";
	
	private String prefix = "";
	
	private String mRemoteUrl, mLocalUrl;
	private String mMineType;
	private File tmpFile;
	
	private int mSize, mRange, mPercent;
	private int mRetryTimes;
	private long mDownloadSize;//已经下载的大小
	
	private boolean mStop, misAlive, misCompleted;
	
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
		if(tmpFile.exists()) {
			mDownloadSize = (int) tmpFile.length();
		} else {
			mDownloadSize = 0;
		}
		mPercent = (int) (mDownloadSize * 100 / mSize);
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
	
	public int getPercent() {
		return mPercent;
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
			mDownloadSize = newfile.length();
			mSize = (int) mDownloadSize;
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
				mPercent = 100;
				return;
			}
			misAlive = true;
//			synchronized (Thread.currentThread()) {
//				Thread.currentThread().wait(10000);				
//			}
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
//			 response.printHeaders();
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
				mMineType = response.getHeader(HTTP.CONTENT_TYPE);
				Log.d(TAG_CD+prefix, "mMineType is " + mMineType);
			} else {
				//TODO:
				ProxyLog(TAG_CD+prefix, "http响应为空，任务重试！");
				retry();
				return;
			}
			ZLog.e("content length = " + response.getHeader(HTTP.CONTENT_LEN));
			long contentLength = 0;
			
			if(mDownloadSize >= mSize) {
				ProxyLog(TAG_CD+prefix, "已经下载完毕！("+ mDownloadSize +"/"+ mSize +")");
				misCompleted = true;
				mPercent = 100;
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
//							utils.sendToMP(bytes, bytes.length);
//							tmpFile.writeTmpFile(mRange, bytes);
							writer.writeBlock(0, bytes);
//							mRange += bytes.length;
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
			if(!reader.readByBlockSize2(contentLength, 1024 * 100)) {
				//TODO:
				ProxyLog(TAG_CD+prefix, "下载读取数据时出错，重试！");
				retry();
				return;
			}
			if(reader.isEnding()) {
				misCompleted = true;
				mPercent = 100;
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
			updateMediaMapping(newfile, mRemoteUrl);
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
		mPercent = (int) (mDownloadSize * 100 / mSize);
		//TODO:可以通知界面
	}
	
	private void updateMediaMapping(File file, String remoteUrl) {
		long fileLength = 0;
		if (file.exists()) {
			fileLength = file.length();
		}
		String userID = ActiveAccount.getInstance(ZApplication.getInstance()).getUID();
		
		MediaValue tmpMV = AccountInfo.getInstance(userID).mediamapping.getMedia(userID, remoteUrl);
		//未登记过，文件大小尚未登记
		if (tmpMV == null || !MediaValue.checkMediaAvailable(tmpMV)) {
			Log.d(TAG_CD + prefix, "Put in MediaMapping");
			tmpMV = new MediaValue();
			tmpMV.UID = userID;
			tmpMV.localpath = file.getPath().replace(Environment.getExternalStorageDirectory().getPath(), "");
			tmpMV.url = remoteUrl;
			tmpMV.MediaType = mMineType == null ? 0 : (mMineType.contains("video") ? 5 : 4);//文件类型  0： 未定义 ，1： 文本， 2：图片  3： 短音频， 4：长音频， 5：视频
			tmpMV.Direction = 1;
			tmpMV.Sync = 1;
			tmpMV.SyncSize = fileLength;
			tmpMV.totalSize = fileLength;
			tmpMV.realSize = fileLength;
			Log.d(TAG_CD + prefix, "userid" + userID +" mappingKey " + tmpMV.url + " path = " + tmpMV.localpath);
			ZLog.printObject(tmpMV);
			AccountInfo.getInstance(userID).mediamapping.setMedia(userID, tmpMV.url, tmpMV);
		} else {
			Log.d(TAG_CD + prefix, "download mappingKey " + tmpMV.url + " path = " + tmpMV.localpath);
			tmpMV.Belong = 1;
			String uid = ActiveAccount.getInstance(ZApplication.getInstance()).getLookLookID();
			if(uid != null && uid.equals(userID)) {
				tmpMV.Belong = 2;
			}
			tmpMV.Sync = 1;
			tmpMV.SyncSize = fileLength;
			tmpMV.totalSize = fileLength;
			tmpMV.realSize = fileLength;
			AccountInfo.getInstance(userID).mediamapping.delMedia(userID, tmpMV.url);
			AccountInfo.getInstance(userID).mediamapping.setMedia(userID, tmpMV.url, tmpMV);
		}
		Intent intent = new Intent(PROXY_ACTION_UPDATE_MAPPING);
		intent.putExtra("remoteUrl", remoteUrl);
		ZApplication.getInstance().sendLocalBroadcast(intent);
		ZLog.e();
	}
	
	private void ProxyLog(String tag, String log) {
		if(OPEN_LOG) {
			Log.d(tag, log);
		}
	}
}
