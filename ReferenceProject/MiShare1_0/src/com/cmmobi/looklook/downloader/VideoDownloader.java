package com.cmmobi.looklook.downloader;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;

import org.apache.http.protocol.HTTP;

import com.cmmobi.looklook.MainApplication;
import com.cmmobi.looklook.R;
import com.cmmobi.looklook.common.gson.DiaryController;
import com.cmmobi.looklook.common.utils.MD5;
import com.cmmobi.looklook.info.profile.TimeHelper;

import android.content.Intent;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.HandlerThread;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import cn.zipper.framwork.core.ZApplication;
import cn.zipper.framwork.core.ZDialog;
import cn.zipper.framwork.core.ZLog;
import cn.zipper.framwork.io.file.ZFileReader;
import cn.zipper.framwork.io.file.ZFileWriter;
import cn.zipper.framwork.io.network.ZHttp2;
import cn.zipper.framwork.io.network.ZHttpReader;
import cn.zipper.framwork.io.network.ZHttpResponse;
import cn.zipper.framwork.utils.ZPercent;
import cn.zipper.framwork.utils.ZThread;
import cn.zipper.framwork.utils.ZPercent.OnPercentChangedListener;

public class VideoDownloader  implements Callback {
	
	private HandlerThread thread;
	private Handler handler;
	private String videoUrl;
	private Handler outHandler;
	
	private static final int STATE_INIT = 0x0effff00;
	private static final int STATE_PREPARE = 0x0effff01;
	private static final int STATE_DOWNLOAD = 0x0effff02;
	private static final int STATE_RETRY = 0x0effff03;
	private static final int STATE_ERROR = 0x0effff04;
	private static final int STATE_BREAK = 0x0effff05;
	private static final int STATE_NEXT = 0x0effff06;
	private static final int STATE_COMPLETE = 0x0effff07;
	
	private int state = STATE_INIT;
	private final String TAG = "VideoDownloader";
	private LinkedList<String> videoList = new LinkedList<String>();
	private Iterator<String> iter;
	private String videoDir = "";
	
	private int retryTimes = 0;
	private String curVideoUrl = null;
	
	private ZHttp2 http;
	private ZHttpReader reader;
	private ZFileWriter writer;
	
	public VideoDownloader(String url,Handler outHandler) {
		thread = new HandlerThread("VideoDownloader");
		thread.start();
		handler = new Handler(thread.getLooper(), this);
		videoUrl = url;
		this.outHandler = outHandler;
		videoDir = DiaryController.getAbsolutePathUnderUserDir() + "/video/" + MD5.encode(videoUrl.getBytes());
		Log.d(TAG,"videoDir = " + videoDir);
	}
	
	public void start() {
		setState(STATE_PREPARE);
	}
	
	public void setState(int state) {
		this.state = state;
		handler.sendEmptyMessage(state);
	}
	
	private void parserHsmIndex() {
		try {
			ZHttp2 http = new ZHttp2();
			ZHttpResponse response = http.get(videoUrl);
			InputStream inputStream = response.getInputStream();
			InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
			BufferedReader bufferReader = new BufferedReader(inputStreamReader);
			String readLine = "";
			while((readLine = bufferReader.readLine()) != null) {
				Log.d(TAG,"readLine = " + readLine);
				if (readLine.endsWith(".sm")) {
					int index = readLine.indexOf(",");
					if (index != -1) {
						String subStr = readLine.substring(index + 1);
						String newStr = videoUrl.replace("index.hsm", subStr);
						Log.d(TAG,"newStr = " + newStr);
						videoList.add(newStr);
					}
				}
			}
			if (videoList.size() > 0) {
				iter = videoList.iterator();
				setState(STATE_DOWNLOAD);
			} else {
				setState(STATE_ERROR);
				Log.d(TAG,"index文件错误");
			}
		} catch (Exception e) {
			e.printStackTrace();
			setState(STATE_ERROR);
			Log.d(TAG,"index解析失败");
		}
	}
	
	private void sendOutMessage(int state) {
		if (outHandler != null) {
			outHandler.sendEmptyMessage(state);
		}
	}
	
	private String getNextUrl(Iterator<String> it) {
		if (it != null && it.hasNext()) {
			return it.next();
		}
		return null;
	}
	
	private void download() {
		if (curVideoUrl != null) {
			try {
				HashMap<String, String> headers = new HashMap<String, String>();
				int index = curVideoUrl.lastIndexOf("/");
				String fileName = null;
				if (index > 0) {
					fileName = curVideoUrl.substring(index + 1);
				}
				File file = null;
				if (fileName != null) {
					file = new File(videoDir, fileName);
				} else {
					Log.d(TAG,"download index解析错误");
					setState(STATE_ERROR);
					return;
				}
				
				long fileLength = 0;
				
				if (file.exists()) {
					fileLength = file.length();
					headers.put("range", "bytes=" + fileLength + "-");
				} else {
					file.getParentFile().mkdirs();
					file.createNewFile();
				}
				
				http = new ZHttp2();
				http.setHeaders(headers);
				ZHttpResponse response = http.get(curVideoUrl);
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
						if (reader.getZPercent().isOneHundredPercent()) {
							Log.d(TAG,"download over ");
//							stop();
						}
						ZThread.sleep(5);
					}
				};
				
				long contentLength = Long.valueOf(response.getHeader(HTTP.CONTENT_LEN));
				
				reader = new ZHttpReader(response.getInputStream(), listener);
				boolean isOk = true;
				Log.d(TAG,"wasDownloaded = " + wasDownloaded);
				if (wasDownloaded) {
					reader.getZPercent().setMaxValue(1);
					reader.getZPercent().setCurrentValue(1, null);
				} else {
					reader.getZPercent().setCurrentValue(fileLength, null);
					int readunit = (int) ((fileLength + contentLength) / 5);
					isOk = reader.readByBlockSize2(fileLength + contentLength, readunit);
				}
				
				stop();
				if (!isOk) {
					setState(STATE_RETRY);
				} else if (wasDownloaded) {
					setState(STATE_NEXT);
				} else if (reader.isEnding()) {
					setState(STATE_NEXT);
				}
				
			} catch (Exception e) {
				stop();
				e.printStackTrace();
				setState(STATE_RETRY);
			} 
		} else {
			setState(STATE_ERROR);
			Log.d(TAG,"curVideoUrl = null");
		}
	}
	
	public void stop() {
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
	
	@Override
	public boolean handleMessage(Message msg) {
		// TODO Auto-generated method stub
		switch(msg.what) {
		case STATE_PREPARE:
			parserHsmIndex();
			break;
		case STATE_DOWNLOAD:
			Log.d(TAG,"STATE_DOWNLOAD ");
			String downloadUrl = getNextUrl(iter);
			if (downloadUrl != null) {
				curVideoUrl = downloadUrl;
				download();
			} else {
				setState(STATE_ERROR);
			}
			break;
		case STATE_RETRY:
			Log.d(TAG,"STATE_RETRY ");
			retryTimes++;
			if (retryTimes < 3) {
				download();
			} else {
				Log.d(TAG,"重试三次下载失败！！！ curVideoUrl = " + curVideoUrl);
				setState(STATE_ERROR);
			}
			break;
		case STATE_BREAK:
			break;
		case STATE_ERROR:
			Log.d(TAG,"STATE_ERROR ");
			sendOutMessage(STATE_ERROR);
			break;
		case STATE_NEXT:
			Log.d(TAG,"STATE_NEXT ");
			retryTimes = 0;
			String nextUrl = getNextUrl(iter);
			if (nextUrl != null) {
				curVideoUrl = nextUrl;
				download();
			} else {
				setState(STATE_COMPLETE);
			}
			break;
		case STATE_COMPLETE:
			Log.d(TAG,"STATE_COMPLETE ");
			videoList.clear();
			sendOutMessage(STATE_COMPLETE);
			break;
		}
		return false;
	}
}
