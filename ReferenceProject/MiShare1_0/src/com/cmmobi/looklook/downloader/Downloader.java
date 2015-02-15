package com.cmmobi.looklook.downloader;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.net.HttpURLConnection;
import java.util.HashMap;

import org.apache.http.protocol.HTTP;

import android.content.Intent;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.HandlerThread;
import android.os.Message;
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

import com.cmmobi.looklook.info.profile.DownloadTaskInfo;

public final class Downloader implements Callback {
	
	public static final String ACTION_REPAINT = "ACTION_DOWNLOAD_REPAINT";
	public static final String KEY_DOWNLOAD_TASK = "KEY_DOWNLOAD_TASK";
	
	
	private static final int ACTION_DOWNLOAD_DOWNLOAD = 0x0effff01;
	private static final int ACTION_DOWNLOAD_QUEUE = 0x0effff02; // 排队, 等待上一个任务完成;
	private static final int ACTION_DOWNLOAD_RETRY = 0x0effff03;
	private static final int ACTION_DOWNLOAD_ERROR = 0x0effff04;
	private static final int ACTION_DOWNLOAD_BREAK = 0x0effff05;
	private static final int ACTION_DOWNLOAD_DONE = 0x0effff06;
	
	public int state;
	private int retryTimes;
	private int willUploadFileNumber;
	private int repaint;
	private boolean isError;
	private Handler handler;
	private DownloadTask task;
	private Runner runner;
	private ZHttp2 http;
	private ZHttpReader reader;
	private ZFileWriter writer;
	private HandlerThread handlerThread;
	private DataInputStream dataInputStream;
	private DataOutputStream dataOutputStream;
	
	public Downloader(DownloadTask task) {
		this.task = task;
		
		if (handlerThread == null) {
			handlerThread = new HandlerThread("DownloadThread");
			handlerThread.start();
			handler = new Handler(handlerThread.getLooper(), this);
		}
	}
	
	public void start() {
		if (!isDownloading()) {
			setStateToDownloading();
			notifyRepaint(false);
		}
	}
	
	/**
	 * 停止上传 (只允许UploadDownloadManager调用这个方法);
	 */
	public void stop() {
		if (runner != null) {
			runner.cancel();
		}
		if (reader != null) {
			reader.stop();
		}
		setStateToBreak();
	}
	
	@Override
	public boolean handleMessage(Message msg) {

		task.state = 0;
		
		switch (msg.what) {
		case ACTION_DOWNLOAD_DOWNLOAD:
			ZLog.e("ACTION_DOWNLOAD_DOWNLOAD");
			download();
			break;
			
		case ACTION_DOWNLOAD_RETRY:
			ZLog.e("ACTION_DOWNLOAD_RETRY");
			if (retryTimes <= 3) {
				retryTimes++;
				ZThread.sleep(1500 * retryTimes + 500);// 500, 2000, 3500, 5000
				closeHttp();
				download();
			} else {
				retryTimes = 0;
				setStateToError();
				closeHttp();
			}
			break;
			
		case ACTION_DOWNLOAD_ERROR:
			ZLog.e("ACTION_DOWNLOAD_ERROR");
			retryTimes = 0;
			task.state = state;
			break;

		case ACTION_DOWNLOAD_BREAK:
			ZLog.e("ACTION_DOWNLOAD_BREAK");
			retryTimes = 0;
			task.state = state;
			closeHttp();
			break;
			
		case ACTION_DOWNLOAD_QUEUE:
			ZLog.e("ACTION_DOWNLOAD_QUEUE");
			task.state = state;
			break;

		case ACTION_DOWNLOAD_DONE:
			ZLog.e("ACTION_DOWNLOAD_DONE");
			if (handlerThread != null) {
				handlerThread.quit();
				handlerThread = null;
			}
			closeHttp();
//			task.listener.OnTaskDone(); ............
			break;
		}
		notifyRepaint(true);
		return false;
	}
	
	private void download() {
		if (runner != null) {
			runner.cancel();
		}
		
		runner = new Runner();
		Thread thread = new Thread(runner);
		thread.start();
	}
	
	private class Runner implements Runnable {
		
		private boolean b;
		
		public Runner() {
			b = true;
		}
		
		public void cancel() {
			b = false;
		}

		@Override
		public void run() {
			try {
				HashMap<String, String> headers = new HashMap<String, String>();
				long fileLength = 0;
				
				File file = new File(task.filePath);
				
				if (file.exists()) {
					fileLength = file.length();
					headers.put("range", "bytes=" + fileLength + "-");
				}
				ZLog.e("fileLength = " + fileLength);
				task.downloadLength = fileLength;
				task.percent.setCurrentValue(fileLength, null);
				
				http = new ZHttp2();
				http.setHeaders(headers);
				ZHttpResponse response = http.get(task.url);
				response.printHeaders();
				
				if (response.getResponseCode() == HttpURLConnection.HTTP_OK) { // 从头下载;
					writer = new ZFileWriter(file, false, null);
					ZLog.e(200);
				} else if (response.getResponseCode() == HttpURLConnection.HTTP_PARTIAL) { // 断点续传;
					writer = new ZFileWriter(file, true, null);
					ZLog.e(206);
				}

				long contentLength = Long.valueOf(response.getHeader(HTTP.CONTENT_LEN));
				if (task.totalLength == 0) {
					task.totalLength = contentLength;
					task.percent.setMaxValue(contentLength);
				}
				
				isError = false;
				
				OnPercentChangedListener listener = new OnPercentChangedListener() {
					@Override
					public void onPercentChanged(ZPercent percent) {
						try {
							Object object = percent.getObject();
							if (object != null) {
								byte[] bytes = (byte[]) object;
								ZLog.e("read length = " + bytes.length);
								writer.writeBlock(0, bytes);
								task.addStep(bytes.length);
								ZThread.sleep(5);
							}
							notifyRepaint(false);
						} catch (Exception e) {
							e.printStackTrace();
							isError = true;
							if (reader != null) {
								reader.stop();
							}
						}
					}
				};
				
				reader = new ZHttpReader(response.getInputStream(), listener);
				reader.readByBlockSize(contentLength, 1024*4);
				reader.close();
				writer.close();
				http.close();
				
				if (b) {
					if (isError) {
						setStateToRetry();
					} else if (reader.isBreak()) {
						setStateToBreak();
					} else {
						setStateToDone();
					}
				}
				
			} catch (Exception e) {
				e.printStackTrace();
				closeHttp();
				if (!isBreak() && b) {
					setStateToRetry();
				}
			}
		}
	}
	
	private void closeHttp() {
		if (http != null) {
			http.close();
		}
		if (reader != null) {
			reader.close();
		}
		if (writer != null) {
			writer.close();
		}
	}
	
	/**
	 * 是否正在下载;
	 * @return
	 */
	public boolean isDownloading() {
		return state == ACTION_DOWNLOAD_DOWNLOAD || state == ACTION_DOWNLOAD_RETRY;
	}
	
	/**
	 * 是否排队等待;
	 * @return
	 */
	public boolean isQueueing() {
		return state == ACTION_DOWNLOAD_QUEUE;
	}
	
	/**
	 * 是否通信错误;
	 * @return
	 */
	public boolean isError() {
		return state == ACTION_DOWNLOAD_ERROR;
	}
	
	/**
	 * 是否用户中断上传;
	 * @return
	 */
	public boolean isBreak() {
		return state == ACTION_DOWNLOAD_BREAK;
	}
	
	
	
	public void setStateToDownloading() {
		state = ACTION_DOWNLOAD_DOWNLOAD;
		handler.sendEmptyMessage(state);
	}
	
	public void setStateToQueueing() {
		state = ACTION_DOWNLOAD_QUEUE;
		handler.sendEmptyMessage(state);
	}
	
	public void setStateToBreak() {
		state = ACTION_DOWNLOAD_BREAK;
		handler.sendEmptyMessage(state);
	}
	
	private void setStateToRetry() {
		state = ACTION_DOWNLOAD_RETRY;
		handler.sendEmptyMessage(state);
	}
	
	public void setStateToError() {
		state = ACTION_DOWNLOAD_ERROR;
		handler.sendEmptyMessage(state);
	}
	
	public void setStateToDone() {
		state = ACTION_DOWNLOAD_DONE;
		handler.sendEmptyMessage(state);
	}
	
	/**
	 * 使用本地广播通知UI层重绘;
	 * @param save: 是否保存数据;
	 */
	private void notifyRepaint(boolean save) {
		if (save) {
			DownloadTaskInfo.getInstance().persist();
		}
		if (save || repaint > 5) {
			repaint = 0;
			ZDataPool.pushIn(KEY_DOWNLOAD_TASK, task);
			Intent intent = new Intent(ACTION_REPAINT);
			ZApplication.getInstance().sendLocalBroadcast(intent);
		} else {
			repaint ++;
		}
	}


}
