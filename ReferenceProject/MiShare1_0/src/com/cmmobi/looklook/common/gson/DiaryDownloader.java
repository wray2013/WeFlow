package com.cmmobi.looklook.common.gson;

import java.io.File;
import java.net.HttpURLConnection;
import java.util.HashMap;
import java.util.LinkedList;

import org.apache.http.protocol.HTTP;

import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import cn.zipper.framwork.core.ZApplication;
import cn.zipper.framwork.core.ZLog;
import cn.zipper.framwork.io.file.ZFileWriter;
import cn.zipper.framwork.io.network.ZHttp2;
import cn.zipper.framwork.io.network.ZHttpReader;
import cn.zipper.framwork.io.network.ZHttpResponse;
import cn.zipper.framwork.utils.ZPercent;
import cn.zipper.framwork.utils.ZPercent.OnPercentChangedListener;

import com.cmmobi.looklook.common.gson.GsonResponse3.MyDiary;
import com.cmmobi.looklook.info.profile.AccountInfo;
import com.cmmobi.looklook.info.profile.ActiveAccount;
import com.cmmobi.looklook.info.profile.MediaValue;
import com.cmmobi.looklook.info.profile.NetworkTaskInfo;
import com.cmmobi.looklook.info.profile.NetworkTaskInfo.MediaFile;
import com.cmmobi.looklook.info.profile.NetworkTaskInfo.downMedia;
import com.cmmobi.looklook.networktask.INetworkTask;
import com.cmmobi.looklook.prompt.Prompt;

public class DiaryDownloader extends Thread {
	
	private final static String TAG = "DiaryDownloader";
	
	public static final String ACTION_DIARY_DOWNLOADED = "ACTION_DIARY_DOWNLOADED";

	private int retryTimes;
	int currentFileIndex; // 当前下载中的文件编号;
	int finishedFileIndex;// 已经完成的文件编号;
	
	public NetworkTaskInfo info;
	
	private ZHttp2 http;
	private ZHttpReader reader;
	private ZFileWriter writer;
	
	private boolean mStop;
	private boolean isResumeTrans; //是否断点续传
	
	private MyDiary mDiary; 
	private static LinkedList<MyDiary> sTasklist = new LinkedList<GsonResponse3.MyDiary>();
	
	public DiaryDownloader(MyDiary myDiary) {
		if(myDiary != null) {
			mDiary = myDiary;
			info = new NetworkTaskInfo(mDiary, INetworkTask.TASK_TYPE_DOWNLOAD);
			currentFileIndex = 0;
			finishedFileIndex = -1;
			mStop = false;
			isResumeTrans = false;
		} else {
			Log.e(TAG, "日记为空，下载失败！");
		}
	}
	
	public static boolean isDownloading(MyDiary diary)
	{
		synchronized (DiaryDownloader.class)
		{
			if(sTasklist.contains(diary))
			{
				return true;
			}
			else
			{
				return false;
			}
		}
	}
	
	public void excute() {
		if(!sTasklist.contains(mDiary))
		{
			synchronized (DiaryDownloader.class)
			{
				sTasklist.add(mDiary);
			}
			this.start();
		}
	}

	public void stopThread() {
		mStop = true;
	}
	
	@Override
	public void run() {
		Log.d(TAG, "启动下载任务！");
		download();
		synchronized (DiaryDownloader.class)
		{
			sTasklist.remove(mDiary);
		}
	}
	
	private void retry() {
		closeHttp();
		retryTimes --;
		if(retryTimes > 0) {
			Log.d(TAG, "任务重试！");
			download();
		} else {
			Prompt.Alert("下载失败！");
			Log.d(TAG, "任务下载失败！");
		}
	}
	
	private void download() {
		try {
			
			if(info.downMedias == null || info.downMedias.size() <= 0 ||
					currentFileIndex > info.downMedias.size() -1) {
				Log.d(TAG, "下载文件列表为空，不需要启动下载任务！");
				sendBroadcast();
				return;
			}
			
			HashMap<String, String> headers = new HashMap<String, String>();
			long fileLength = 0;
			
			downMedia df = new downMedia();
			df = info.downMedias.get(currentFileIndex);
			
			File file = new File(df.attachMedia.localpath);
			
			if(!file.getParentFile().exists()) {
				file.getParentFile().mkdirs();
			}
			if (file.exists()) {
				fileLength = file.length();
				headers.put("range", "bytes=" + fileLength + "-");
			}
			
			http = new ZHttp2();
			http.setHeaders(headers);
			ZHttpResponse response = http.get(df.attachMedia.remotepath);
			ZLog.e(response==null?"response is null":"response is not null");
			
			if (response != null && response.getResponseCode() == HttpURLConnection.HTTP_OK) { // 从头下载;
				Log.d(TAG, "http回传[200]，从头开始下载");
				if (file.exists()) {
					info.currentDownloaingTotalLength -= info.currentDownloaingLength;
					if(info.currentDownloaingTotalLength < 0) info.currentDownloaingTotalLength = 0; 
					info.currentDownloaingLength = 0;
					file.delete();
				}
				isResumeTrans = false;
				writer = new ZFileWriter(file, false, null);
				ZLog.e(200);
			} else if (response != null && response.getResponseCode() == HttpURLConnection.HTTP_PARTIAL) { // 断点续传;
				Log.d(TAG, "http回传[206]，断点续传模式下载");
				writer = new ZFileWriter(file, true, null);
				isResumeTrans = true;
				ZLog.e(206);
			} else if(response == null) {
				retry();
				return;
			}
			
			long contentLength = 0;
			if(response.getHeader(HTTP.CONTENT_LEN) != null) {
				contentLength = Long.valueOf(response.getHeader(HTTP.CONTENT_LEN));
			}
			
			if (contentLength != 0 && contentLength <= info.currentDownloaingLength && !isResumeTrans) {
				currentFileIndex++;
				info.currentDownloaingTotalLength += info.currentDownloaingLength;
				updateMediaMapping(file, df.attachMedia);
				//开始下载下一个文件
				ZLog.e("开始下载下一个文件");
				download();
				return;
			}
					
			OnPercentChangedListener listener = new OnPercentChangedListener() {
				@Override
				public void onPercentChanged(ZPercent percent) {
					try {
						if (mStop) {
							Log.d(TAG, "下载过程暂停");
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
				Log.d(TAG, "下载读取数据时出错，重试！");
				Prompt.Alert("下载失败！");
				ZLog.e(" retry=" + retryTimes);
				retry();
				return;
			}
			reader.close();
			writer.close();
			http.close();
			
			if(reader.isEnding()) {
				info.finishedFileIndex = currentFileIndex;
				updateMediaMapping(file, df.attachMedia);

				if (info.finishedFileIndex == info.downMedias.size() - 1) {
//					if (mStop) {
//						Log.d(TAG, "已经全部下载完毕，但之前已暂停！");
//						return;
//					}
					Log.d(TAG, "已经全部下载完毕！");
					sendBroadcast();
					Prompt.Alert("内容已下载完毕！");
					return;
				}
				Log.d(TAG, "还有其他文件待下载");
				
				currentFileIndex++;
				info.currentDownloaingTotalLength += info.currentDownloaingLength;
				//开始下载下一个文件
				ZLog.e("开始下载下一个文件");
				download();
				return;
			}
			
		} catch(Exception e) {
			e.printStackTrace();
			closeHttp();
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
	
	private float getPercent(long c,long t) {
		float per = 0;
		if(t > 0) {
			per = c * 100 / t ;
			//ZLog.e("Percent = "+per);
		}

		return per;
	}
	
	private void addStep(long step) {
		info.currentDownloaingTotalLength += step;
		info.percentValue = getPercent(info.currentDownloaingTotalLength,
				info.totalTaskSize);
	}
	/*
	private ArrayList<String> ExtractMediaFile(MyDiary diary) {
		ArrayList<String> mediafiles = new ArrayList<String>();
		ArrayList<String> filelist = diary.getMediaFiles();
		if (filelist != null) {
			for (int i = 0; i < filelist.size(); i++) {
				String filepath = filelist.get(i);
				if(filepath.startsWith("http://")) {
					
					
					
					mediafiles.add(filepath);
				}
			}
		}
		return mediafiles;
	}*/
	private void updateMediaMapping(File file, MediaFile mediafile) {
		long fileLength = 0;
		if (file.exists()) {
			fileLength = file.length();
		}
		MediaValue mediaValue = new MediaValue();
		mediaValue = AccountInfo.getInstance(info.userid).mediamapping.getMedia(info.userid, mediafile.remotepath);
		if (mediaValue != null) {
			Log.d(TAG, "download mappingKey " + mediaValue.url + " path = " + mediaValue.localpath);
			mediaValue.Belong = 1;
			String uid = ActiveAccount.getInstance(ZApplication.getInstance()).getLookLookID();
			if(uid != null && uid.equals(info.userid)) {
				mediaValue.Belong = 2;
			}
			mediaValue.Sync = 1;
			mediaValue.SyncSize = fileLength;
			mediaValue.totalSize = fileLength;
			mediaValue.realSize = fileLength;
			AccountInfo.getInstance(info.userid).mediamapping.delMedia(info.userid, mediaValue.url);
			AccountInfo.getInstance(info.userid).mediamapping.setMedia(info.userid, mediaValue.url, mediaValue);
		}
	}
	
	private void sendBroadcast() {
		Intent intent = new Intent(ACTION_DIARY_DOWNLOADED);
		intent.putExtra("diaryuuid", info.diaryuuid);
		LocalBroadcastManager bc = LocalBroadcastManager.getInstance(null);
		bc.sendBroadcast(intent);
	}
	
}
