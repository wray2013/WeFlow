package com.cmmobi.looklook.uploader;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.UUID;

import android.content.Intent;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.HandlerThread;
import android.os.Message;
import android.text.TextUtils;
import cn.zipper.framwork.core.ZApplication;
import cn.zipper.framwork.core.ZDataPool;
import cn.zipper.framwork.core.ZLog;
import cn.zipper.framwork.io.file.ZFileReader;
import cn.zipper.framwork.utils.ZPercent;
import cn.zipper.framwork.utils.ZPercent.OnPercentChangedListener;
import cn.zipper.framwork.utils.ZThread;

import com.cmmobi.looklook.info.profile.ActiveAccount;
import com.cmmobi.looklook.info.profile.UploadTaskInfo;

/**
 * socket上传器;
 * 
 * @author Sunshine
 * 
 */
public class Uploader implements Callback {


	public static final String ACTION_REPAINT = "ACTION_UPLOAD_REPAINT";
	public static final String KEY_UPLOAD_TASK = "KEY_UPLOAD_TASK";

	private static final int ACTION_UPLOAD_NEXT = 0x0effff01;
	private static final int ACTION_UPLOAD_WAIT = 0x0effff02; // 等待, 等待下一个小视频文件;
	private static final int ACTION_UPLOAD_QUEUE = 0x0effff03; // 排队, 等待上一个任务完成;
	private static final int ACTION_UPLOAD_RETRY = 0x0effff04;
	private static final int ACTION_UPLOAD_ERROR = 0x0effff05;
	private static final int ACTION_UPLOAD_BREAK = 0x0effff06;
	private static final int ACTION_UPLOAD_DONE = 0x0effff07;
	
	public int state;
	private int retryTimes;
	private int willUploadFileNumber;
	private int repaint;
	private boolean isError;
	private Handler handler;
	private Socket socket;
	private UploadTask task;
	private Runner runner;
	private ZFileReader reader;
	private HandlerThread handlerThread;
	private DataInputStream dataInputStream;
	private DataOutputStream dataOutputStream;

	public Uploader(UploadTask task) {
		this.task = task;
		this.task.uuid = UUID.randomUUID().toString();
		this.willUploadFileNumber = this.task.uploadedFileNumber;
		
		if (handlerThread == null) {
			handlerThread = new HandlerThread("UploadThread");
			handlerThread.start();
			handler = new Handler(handlerThread.getLooper(), this);
		}
	}
	
	/**
	 * 开始上传 (只允许UploadDownloadManager调用这个方法);
	 */
	public void start() {
		if (!isUploading() && !isWaiting()) {
			setStateToUploading();
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
/*		case XXX_Requester.RESPONSE_TYPE_GET_SOCKET:
			ZLog.e("RESPONSE_TYPE_GET_SOCKET");
			XXX_GsonResponse.getSocketResponse response = (XXX_GsonResponse.getSocketResponse) msg.obj;
			if (response != null && !TextUtils.isEmpty(response.ip)) {
				task.ip = response.ip;
				task.port = response.port;
				task.uploadFileName = response.videopath;
				setStateToUploading();
				UploadTaskInfo.getInstance().persist();
				
			} else if (isBreak()) {
				setStateToBreak();
			} else {
				setStateToError();
			}
			break;*/

/*		case ACTION_UPLOAD_NEXT:
			ZLog.e("ACTION_UPLOAD_NEXT");
			if (TextUtils.isEmpty(task.ip)) {
				XXX_Requester.requestSocketIp(handler);
			} else if (willUploadFileNumber < task.filePaths.size()) {
				upload();
			} else {
				setStateToWaiting();
			}
			break;*/
			
		case ACTION_UPLOAD_RETRY:
			ZLog.e("ACTION_UPLOAD_RETRY");
			if (retryTimes <= 3) {
				retryTimes++;
				ZThread.sleep(1500 * retryTimes + 500);// 500, 2000, 3500, 5000
				closeSocket();
				upload();
			} else {
				retryTimes = 0;
				setStateToError();
				closeSocket();
			}
			break;
			
		case ACTION_UPLOAD_ERROR:
			ZLog.e("ACTION_UPLOAD_ERROR");
			retryTimes = 0;
			task.state = state;
			break;

		case ACTION_UPLOAD_BREAK:
			ZLog.e("ACTION_UPLOAD_BREAK");
			retryTimes = 0;
			task.state = state;
			if (reader != null) {
				reader.stop();
			}
			closeSocket();
			break;
			
		case ACTION_UPLOAD_WAIT:
			ZLog.e("ACTION_UPLOAD_WAIT");
			ZLog.e("ACTION_UPLOAD_WAIT : Waiting...");
			task.listener.OnUploadTaskDone();
			break;
			
		case ACTION_UPLOAD_QUEUE:
			ZLog.e("ACTION_UPLOAD_QUEUE");
			task.state = state;
			break;

		case ACTION_UPLOAD_DONE:
			ZLog.e("ACTION_UPLOAD_DONE");
			if (handlerThread != null) {
				handlerThread.quit();
				handlerThread = null;
			}
			closeSocket();
//			task.listener.OnTaskDone(); ............
			break;
		}
		notifyRepaint(true);
		return false;
	}

	/**
	 * 上传;
	 * @param index: 指定本次上传的文件索引;
	 */
	private void upload() {
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
//				 task.ip = "192.168.2.87";
//				 task.port = 7885;
//				 task.uploadFileName = "temp_2013_04_16_1107063d37ed931bb64970bcc05bd51153c8c5.mp4";
				
				if (socket == null) {
					ZLog.e("Socket Connect : " + task.ip);
					socket = new Socket();
					socket.setKeepAlive(true);
					socket.setSoTimeout(10 * 1000);
					socket.connect(new InetSocketAddress(task.ip, task.port), 10 * 1000);

					dataOutputStream = new DataOutputStream(socket.getOutputStream());
					dataInputStream = new DataInputStream(socket.getInputStream());
				}

				File file = new File(task.filePaths.get(willUploadFileNumber));
				String requestHeader = makeHeader(file, task.uuid, willUploadFileNumber, 0, task.uploadFileName, task.type, task.rotation);

				dataOutputStream.write(requestHeader.getBytes());
				ZLog.e(">> Socket Request Header (file:" + file.getAbsolutePath() + "): " + requestHeader);

				String line = dataInputStream.readLine() + ";";
				ZLog.e("<< Socket Response Header: " + line);

				String ip = getStringValue(line, "ip");
				int port = getIntValue(line, "port");
				
				if (!TextUtils.isEmpty(ip) && port > -1) {
					task.ip = ip;
					task.port = port;
					UploadTaskInfo.getInstance().persist();
				}
				
				int dataover = getIntValue(line, "dataover");

				if (dataover == 1) {

					long skip = Long.valueOf(getStringValue(line, "position"));
					long offset = task.uploadedLength - task.uploadedFileBlock;
					task.addStep(skip - offset);
					isError = false;
					
					OnPercentChangedListener listener = new OnPercentChangedListener() {
						
						@Override
						public void onPercentChanged(ZPercent percent) {
							try {
								Object object = percent.getObject();
								if (object != null) {
									byte[] bytes = (byte[]) object;
									dataOutputStream.write(bytes);
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
					
					reader = new ZFileReader(file, listener);
					reader.skip(skip);
					reader.readByBlockSize(file.length(), 1024 * 4);
					reader.close();
					
					if (isError && b) {
						setStateToRetry();
						
					} else if (reader.isEnding()) {

						String line2 = dataInputStream.readLine() + ";";
						dataover = getIntValue(line2, "dataover");
						ZLog.e("<< Socket Write-Finish : " + line2);
						
						if (dataover == 2) {
							retryTimes = 0;
							
							if (task.isAllFileDone && task.uploadedFileNumber == task.filePaths.size() - 1) {
								String notify = makeHeader(null, task.uuid, 0, 1, task.uploadFileName, task.type, task.rotation);
								dataOutputStream.write(notify.getBytes());
								ZLog.e(">> Socket Upload-Finish Notify: " + notify);
								if (b) {
									setStateToDone();
								}

							} else {
								task.uploadedFileBlock += file.length();
								task.uploadedFileNumber = willUploadFileNumber;
								willUploadFileNumber++;
								if (b) {
									setStateToUploading();
								}
							}
						} else if (b) {
							setStateToRetry();
						}
						
					} else if (reader.isBreak() && b) {
						setStateToBreak();
					}

				} else if (dataover == 2) {
					ZLog.alert();
					ZLog.e("File: " + file.getAbsolutePath() + " Already Uploaded.");
					willUploadFileNumber++;
					if (b) {
						setStateToUploading();
					}
				}

			} catch (Exception e) {
				e.printStackTrace();
				closeSocket();
				if (!isBreak() && b) {
					setStateToRetry();
				}
			}
		}
	}
	
	private void closeSocket() {
		try {
			if (dataInputStream != null) {
				dataInputStream.close();
				dataInputStream = null;
			}
			if (dataOutputStream != null) {
				dataOutputStream.close();
				dataOutputStream = null;
			}
			if (socket != null) {
				socket.close();
				socket = null;
			}
			if (reader != null) {
				reader.close();
				reader = null;
			}
		} catch (Exception e1) {
			e1.printStackTrace();
		}
	}
	
	/**
	 * 使用本地广播通知UI层重绘;
	 * @param save: 是否保存数据;
	 */
	private void notifyRepaint(boolean save) {
		if (save) {
			UploadTaskInfo.getInstance().persist();
		}
		if (save || repaint > 5) {
			repaint = 0;
			ZDataPool.pushIn(KEY_UPLOAD_TASK, task);
			Intent intent = new Intent(ACTION_REPAINT);
			ZApplication.getInstance().sendLocalBroadcast(intent);
		} else {
			repaint ++;
		}
	}
	

	
	/**
	 * 是否正在上传;
	 * @return
	 */
	public boolean isUploading() {
		return state == ACTION_UPLOAD_NEXT || state == ACTION_UPLOAD_RETRY;
	}
	
	/**
	 * 是否等待追加新的视频文件 (之前给的视频文件已经上传完成);
	 * @return
	 */
	public boolean isWaiting() {
		return state == ACTION_UPLOAD_WAIT;
	}
	
	/**
	 * 是否排队等待;
	 * @return
	 */
	public boolean isQueueing() {
		return state == ACTION_UPLOAD_QUEUE;
	}
	
	/**
	 * 是否通信错误;
	 * @return
	 */
	public boolean isError() {
		return state == ACTION_UPLOAD_ERROR;
	}
	
	/**
	 * 是否用户中断上传;
	 * @return
	 */
	public boolean isBreak() {
		return state == ACTION_UPLOAD_BREAK;
	}
	
	
	
	public void setStateToUploading() {
		state = ACTION_UPLOAD_NEXT;
		handler.sendEmptyMessage(state);
	}
	
	public void setStateToQueueing() {
		state = ACTION_UPLOAD_QUEUE;
		handler.sendEmptyMessage(state);
	}
	
	public void setStateToWaiting() {
		state = ACTION_UPLOAD_WAIT;
		handler.sendEmptyMessage(state);
	}
	
	public void setStateToBreak() {
		state = ACTION_UPLOAD_BREAK;
		handler.sendEmptyMessage(state);
	}
	
	private void setStateToRetry() {
		state = ACTION_UPLOAD_RETRY;
		handler.sendEmptyMessage(state);
	}
	
	public void setStateToError() {
		state = ACTION_UPLOAD_ERROR;
		handler.sendEmptyMessage(state);
	}
	
	public void setStateToDone() {
		state = ACTION_UPLOAD_DONE;
		handler.sendEmptyMessage(state);
	}

	/**
	 * 组装请求头;
	 * @param file: 要上传的文件;
	 * @param uuid: sourceId;
	 * @param nuid: 子文件序号 (从0开始);
	 * @param over: 是否已经完成上传 (0: 未结束. 1: 已结束);
	 * @param fileName: 文件名;
	 * @param type: 文件个数 (1: 只有一个文件. n: 有多个文件);
	 * @param rotation: 旋转角度 (0/90/180/270);
	 * @return
	 */
	private String makeHeader(File file, String uuid, int nuid, int over, String fileName, String type, int rotation) {
		long fileLength = 0;

		if (file != null) {
			fileLength = file.length();
		}
		
		String string = "Content-Length=" + fileLength
		+ ";userid=" + ActiveAccount.getInstance(ZApplication.getInstance()).getLookLookID()
		+ ";sourceid=" + uuid
		+ ";nuid=" + nuid
		+ ";over=" + over
		+ ";filename=" + fileName
		+ ";type=" + type
		+ ";rotation=" + rotation
		+ "\r\n";
		
		String head = String.format("%05d", string.length());

		return head + string;
	}

	/**
	 * 从socket响应中获取字段值;
	 * 
	 * @param response
	 * @param key
	 * @return
	 */
	private String getStringValue(String response, String key) {
		String value = null;
		int index = response.indexOf(key);
		if (index > -1) {
			value = response.substring(index + key.length() + 1, response.indexOf(";", index + key.length() + 1));
		}
		return value;
	}

	/**
	 * 从socket响应中获取字段值;
	 * 
	 * @param response
	 * @param key
	 * @return
	 */
	private int getIntValue(String response, String key) {
		int value = 0;
		String string = getStringValue(response, key);
		if (string != null) {
			value = Integer.valueOf(string);
		}
		return value;
	}

}
