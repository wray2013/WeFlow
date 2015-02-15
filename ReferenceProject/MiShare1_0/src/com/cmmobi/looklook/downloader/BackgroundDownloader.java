package com.cmmobi.looklook.downloader;

import java.io.File;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.http.protocol.HTTP;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;
import cn.zipper.framwork.core.ZApplication;
import cn.zipper.framwork.core.ZDataPool;
import cn.zipper.framwork.core.ZLog;
import cn.zipper.framwork.io.file.ZFileWriter;
import cn.zipper.framwork.io.network.ZHttp2;
import cn.zipper.framwork.io.network.ZHttpReader;
import cn.zipper.framwork.io.network.ZHttpResponse;
import cn.zipper.framwork.utils.ZPercent;
import cn.zipper.framwork.utils.ZPercent.OnPercentChangedListener;
import cn.zipper.framwork.utils.ZThread;

import com.cmmobi.looklook.activity.LookLookActivity;
import com.cmmobi.looklook.info.profile.PrivateCommonMessage;
import com.cmmobi.looklook.info.profile.TimeHelper;

public final class BackgroundDownloader {
	
	public static final String ACTION_DOWNLOAD_PROGRESS = "ACTION_DOWNLOAD_PROGRESS";
	public static final String BACKGROUND_DOWNLOADER = "BACKGROUND_DOWNLOADER";
	private static final String TAG = "BackgroundDownloader";
	
	private static long lastRepaintMills;
	private static List<String> tasks = new ArrayList<String>();
	
	private boolean needWorking;
	private float lastPercent;
	private int mediaType;
	private int retryTimes;
	private String url;
	private File file;
	private Runner runner;
	private ZHttp2 http;
	private ZHttpReader reader;
	private ZFileWriter writer;
	private PrivateCommonMessage message;

	BroadcastReceiver mBroadcastReceiver;


	
	public BackgroundDownloader(PrivateCommonMessage message, String url, File file, int mediaType) {
		this.message = message;
		this.url = url;
		this.file = file;
		this.mediaType = mediaType;
		this.mBroadcastReceiver = new BroadcastReceiver() {

			@Override
			public void onReceive(Context context, Intent intent) {
				String action=intent.getAction();
				if(LookLookActivity.ACTION_HOMEACTIVITY_EXIT.equals(action)){
					BackgroundDownloader.this.stop(true);
				}

			}
		};

	}
	
	public boolean start() {
		boolean b = !tasks.contains(url);
		
		if (b) {
			tasks.add(url);
			
			stop(false);
			needWorking = true;
			runner = new Runner();
			Thread thread = new Thread(runner);
			thread.start();
			ZApplication.getInstance().registerReceiver(mBroadcastReceiver, new IntentFilter(LookLookActivity.ACTION_HOMEACTIVITY_EXIT));
		}
		
		return b;
	}
	
	public void stop(boolean unreg) {
		if (runner != null) {
			runner.cancel();
		}
		if (reader != null) {
			reader.stop();
			reader.close();
		}
		if (writer != null) {
			writer.close();
		}
		if (http != null) {
			http.close();
		}
		
		if(unreg){
			ZApplication.getInstance().unregisterReceiver(mBroadcastReceiver);
		}

	}
	
	public boolean isError() {
		return needWorking;
	}
	
	public PrivateCommonMessage getPrivateCommonMessage() {
		return message;
	}
	
	public String getURL() {
		return url;
	}
	
	public File getFile() {
		return file;
	}
	
	public ZPercent getZPercent() {
		return reader.getZPercent();
	}
	
	public int getMediaType() {
		return mediaType;
	}
	
	private class Runner implements Runnable {
		
		public Runner() {
		}
		
		public void cancel() {
			if (reader != null) {
				reader.stop();
			}
		}

		@Override
		public void run() {
			
			while (needWorking) {
				retryTimes ++;
				if (retryTimes > 5) {
					break;
				} else {
					needWorking = work();
				}
			}
			
			tasks.remove(url);
			if (needWorking) {
				notifyRepaint(true);
			}
		}
	}
	
	private boolean work() {
		try {
			ZLog.alert();
			ZLog.e("开始下载......");
			
			HashMap<String, String> headers = new HashMap<String, String>();
			long fileLength = 0;
			
			if (file.exists()) {
				fileLength = file.length();
				headers.put("range", "bytes=" + fileLength + "-");
			}
			
			http = new ZHttp2();
			http.setHeaders(headers);
			ZHttpResponse response = http.get(url);
			response.printHeaders();
			
			boolean wasDownloaded = false;
			
			if (response.getResponseCode() == HttpURLConnection.HTTP_OK) { // 从头下载;
				writer = new ZFileWriter(file, false, null);
				retryTimes = 0;
				ZLog.e(200);
			} else if (response.getResponseCode() == HttpURLConnection.HTTP_PARTIAL) { // 断点续传;
				writer = new ZFileWriter(file, true, null);
				retryTimes = 0;
				ZLog.e(206);
			} else if (response.getResponseCode() == 416 && fileLength > 0) {
				wasDownloaded = true;
				retryTimes = 0;
				ZLog.e(416);
			}
			
			OnPercentChangedListener listener = new OnPercentChangedListener() {
				
				@Override
				public void onPercentChanged(ZPercent percent) {
					Object object = percent.getObject();
					if (object != null) {
						byte[] bytes = (byte[]) object;
						writer.writeBlock(0, bytes);
					}
					needWorking = false;
					notifyRepaint(false);
					ZThread.sleep(10);
				}
			};
			
			long contentLength = Long.valueOf(response.getHeader(HTTP.CONTENT_LEN));
			
			reader = new ZHttpReader(response.getInputStream(), listener);
			
			if (wasDownloaded) {
				reader.getZPercent().setMaxValue(1);
				reader.getZPercent().setCurrentValue(1, null);
			} else {
				reader.getZPercent().setCurrentValue(fileLength, null);
				int readunit = (int) ((fileLength + contentLength)/20);
				readunit = readunit + 8 - readunit%8;
				needWorking = !reader.readByBlockSize2(fileLength + contentLength, readunit);
			}
			
			notifyRepaint(true);
			
		} catch (Exception e) {
			e.printStackTrace();
			
			needWorking = true;
			ZThread.sleep(500);
		} finally {
			stop(false);
		}
		
		return needWorking;
	}
	
	/**
	 * 使用本地广播通知UI层重绘;
	 * @param must: 是否强制刷新 (忽略时间间隔);
	 */
	private void notifyRepaint(boolean must) {
		if(reader!=null && reader.getZPercent()!=null){
			boolean b1 = TimeHelper.getInstance().now() - lastRepaintMills >= 1000;
			boolean b2 = reader.getZPercent().getPercent() - lastPercent >= 5.0f;
			
			if ((b1 && b2) || must) {
				lastRepaintMills = TimeHelper.getInstance().now();
				lastPercent = reader.getZPercent().getPercent();
				Log.e(TAG, "notifyRepaint must" + must);
				
				ZDataPool.pushIn(BACKGROUND_DOWNLOADER, this);
				Intent intent = new Intent(ACTION_DOWNLOAD_PROGRESS);
				ZApplication.getInstance().sendLocalBroadcast(intent);
			}
		}

	}



	

}
