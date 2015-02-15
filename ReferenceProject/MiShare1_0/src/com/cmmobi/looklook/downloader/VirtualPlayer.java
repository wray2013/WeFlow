package com.cmmobi.looklook.downloader;

import java.net.HttpURLConnection;
import java.util.HashMap;

import org.apache.http.protocol.HTTP;

import android.app.Activity;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import android.support.v4.content.LocalBroadcastManager;
import cn.zipper.framwork.core.ZApplication;
import cn.zipper.framwork.core.ZBroadcastReceiver;
import cn.zipper.framwork.core.ZDataPool;
import cn.zipper.framwork.core.ZLog;
import cn.zipper.framwork.io.network.ZHttp2;
import cn.zipper.framwork.io.network.ZHttpReader;
import cn.zipper.framwork.io.network.ZHttpResponse;
import cn.zipper.framwork.utils.ZPercent;
import cn.zipper.framwork.utils.ZPercent.OnPercentChangedListener;
import cn.zipper.framwork.utils.ZThread;

import com.cmmobi.looklook.httpproxy.HttpProxy;
import com.cmmobi.looklook.httpproxy.downloader.CacheDownloader;
import com.cmmobi.looklook.info.profile.ActiveAccount;
import com.cmmobi.looklook.info.profile.TimeHelper;

public final class VirtualPlayer {

	public static final String ACTION_VIRTUAL_PLAYER_PROGRESS = "ACTION_VIRTUAL_PLAYER_PROGRESS";
	public static final String VIRTUAL_PLAYER = "VIRTUAL_PLAYER";
	
	private static final int HANDLER_UPDATE_VIDEO_PROXY_PREPARED = 0x0018;
	private static long lastRepaintMills;
	
	private boolean needWorking;
	private float lastPercent;
	private int retryTimes;
	private Activity activity;
	private ZBroadcastReceiver receiver;
	private ZHttpReader reader;
	private Handler handler;
	private Runner runner;
	private ZHttp2 http;
	private String url;
	private String localUrl;
	private ZPercent percent;
	private HttpProxy proxy;
	
	/**
	 * 原始URL.
	 * @param url
	 */
	public VirtualPlayer(Activity activity, ZBroadcastReceiver receiver, String url) {
		this.url = url;
		this.activity = activity;
		this.receiver = receiver;
		this.handler = new Handler(new Callback() {
			
			@Override
			public boolean handleMessage(Message msg) {
				if(msg.obj != null) {
					localUrl = (String) msg.obj;
					
					stop();
					needWorking = true;
					runner = new Runner();
					Thread thread = new Thread(runner);
					thread.start();
				}
				return false;
			}
		});
		proxy = HttpProxy.getInstance(ActiveAccount.getInstance(ZApplication.getInstance()).getUID());
		
//		activity.getZReceiverManager().registerLocalZReceiver(receiver);
		
		IntentFilter intentfilter = new IntentFilter();
		intentfilter.addAction(CacheDownloader.PROXY_ACTION_UPDATE_MAPPING);
		LocalBroadcastManager.getInstance(activity).registerReceiver(receiver, intentfilter);
	}

	public void play() {
		proxy.asynGetProxyUrl(handler, url, HANDLER_UPDATE_VIDEO_PROXY_PREPARED, HttpProxy.MTypeVideo);
	}

	public void stop() {
		if (runner != null) {
			runner.cancel();
		}
		if (reader != null) {
			reader.stop();
			reader.close();
		}
		if (http != null) {
			http.close();
		}
		LocalBroadcastManager.getInstance(activity).unregisterReceiver(receiver);
	}

	public String getURL() {
		return url;
	}
	
	private boolean returnOnce;
	
	public String getFilePath() {
		String path = null;
		
//		if (localUrl.toLowerCase().startsWith("http://")) {
//			MediaMapping mapping = new MediaMapping();
//			MediaValue value = mapping.getMedia(ActiveAccount.getInstance(ZApplication.getInstance()).getUID(), url);
//			if (value != null) {
//				path = value.path;
//			}
//			
//		} else {
//			path = localUrl;
//		}

		return path;
	}

	public ZPercent getZPercent() {
		return percent;
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
				retryTimes++;
				if (retryTimes > 5) {
					break;
				} else {
					needWorking = work();
				}
			}

			if (needWorking) {
//				notifyBroadcast(true);
			}
		}
	}

	private boolean work() {
		try {
			long contentLength = 0;
			
			if (localUrl.toLowerCase().startsWith("http://")) {
				HashMap<String, String> headers = new HashMap<String, String>();
				long fileLength = 0;
				
				http = new ZHttp2();
				http.setHeaders(headers);
				ZHttpResponse response = http.get(localUrl);
				response.printHeaders();

				boolean wasDownloaded = false;

				if (response.getResponseCode() == HttpURLConnection.HTTP_OK) { // 从头下载;
					retryTimes = 0;
					ZLog.e(200);
				} else if (response.getResponseCode() == HttpURLConnection.HTTP_PARTIAL) { // 断点续传;
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
						needWorking = false;
						ZLog.e(percent.getPercentInt());
						ZThread.sleep(5);
					}
				};

				contentLength = Long.valueOf(response.getHeader(HTTP.CONTENT_LEN));

				reader = new ZHttpReader(response.getInputStream(), listener);

				if (wasDownloaded) {
					reader.getZPercent().setMaxValue(contentLength);
					reader.getZPercent().setCurrentValue(contentLength, null);
					
				} else {
					reader.getZPercent().setCurrentValue(fileLength, null);
					int readunit = (int) ((fileLength + contentLength) / 20);
					readunit = readunit + 8 - readunit % 8;
					needWorking = !reader.readByBlockSize2(fileLength + contentLength, readunit);
				}
				percent = reader.getZPercent();
				
			} else {
				percent = new ZPercent(null);
				needWorking = false;
			}
			
			percent.setMaxValue(contentLength);
			percent.setCurrentValue(contentLength, null);
			
//			notifyBroadcast(true);
			proxy.stopProxy();

		} catch (Exception e) {
			e.printStackTrace();

			needWorking = true;
			ZThread.sleep(500);
		} finally {
			stop();
		}

		return needWorking;
	}

	/**
	 * 使用本地广播通知UI层重绘;
	 * 
	 * @param must
	 *            : 是否强制刷新 (忽略时间间隔);
	 */
	private void notifyBroadcast(boolean must) {
		if (percent != null && !returnOnce) {
			
			if (getFilePath() != null) {
				returnOnce = true;
			}

			lastRepaintMills = TimeHelper.getInstance().now();
			lastPercent = percent.getPercent();

			ZDataPool.pushIn(VIRTUAL_PLAYER, this);
			Intent intent = new Intent(ACTION_VIRTUAL_PLAYER_PROGRESS);
			ZApplication.getInstance().sendLocalBroadcast(intent);
		}

	}

}
