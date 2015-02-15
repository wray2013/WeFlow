package com.cmmobi.looklook.networktask;

import java.io.File;
import java.net.HttpURLConnection;
import java.util.HashMap;

import org.apache.http.protocol.HTTP;

import android.annotation.SuppressLint;
import android.os.Message;
import android.util.Log;
import cn.zipper.framwork.core.ZApplication;
import cn.zipper.framwork.core.ZLog;
import cn.zipper.framwork.io.file.ZFileWriter;
import cn.zipper.framwork.io.network.ZHttp2;
import cn.zipper.framwork.io.network.ZHttpReader;
import cn.zipper.framwork.io.network.ZHttpResponse;
import cn.zipper.framwork.utils.ZPercent;
import cn.zipper.framwork.utils.ZPercent.OnPercentChangedListener;
import cn.zipper.framwork.utils.ZThread;

import com.cmmobi.looklook.common.gson.GsonResponse3;
import com.cmmobi.looklook.common.gson.Requester3;
import com.cmmobi.looklook.info.profile.AccountInfo;
import com.cmmobi.looklook.info.profile.ActiveAccount;
import com.cmmobi.looklook.info.profile.MediaValue;
import com.cmmobi.looklook.info.profile.NetworkTaskInfo;
import com.cmmobi.looklook.info.profile.NetworkTaskInfo.MediaFile;
import com.cmmobi.looklook.info.profile.NetworkTaskInfo.downMedia;
import com.cmmobi.looklook.info.profile.TimeHelper;

/**
 * 日记任务 上传 下载
 * 
 * 通过状态来驱动任务
 * 
 * 
 */
public class DownloadNetworkTask extends INetworkTask {

	private boolean isError;
	private boolean isResumeTrans; //是否断点续传
	private long updateTime;
	private int retryTimes;
	int currentDownloadIndex;

	private ZHttp2 http;
	private ZHttpReader reader;
	private ZFileWriter writer;

	public DownloadNetworkTask(NetworkTaskInfo info) {
		super(info);
		info.taskType = TASK_TYPE_DOWNLOAD;
		info.isPrepareDone = true;//debug
//		info.downfiles = new ArrayList<MediaValue>();
		currentDownloadIndex = info.finishedFileIndex;
		currentDownloadIndex++;
	}

	@Override
	public boolean handleMessage(Message msg) {

		switch (msg.what) {
		case STATE_WAITING:
			Log.d("debug", "I am waiting!");
			break;
			
		case STATE_PREPARING:
			//收藏
			//Requester2.addcollectDiary(handler, info.diaryid);
			//handler.sendEmptyMessageDelayed(Requester2.RESPONSE_TYPE_ADD_COLLECT_DIARY, 5000);
			break;
			
		case Requester3.RESPONSE_TYPE_ADD_COLLECT_DIARY:
			// 收藏的响应
			GsonResponse3.addCollectDiaryResponse response = (GsonResponse3.addCollectDiaryResponse) msg.obj;
			if (msg.obj != null) {
				if (response == null) {
					errcode = TASK_ERROR_SERVER_ERROR;
//					translateToState(STATE_ERROR);
				} else if ("0".equals(response.status)) {
					info.isCollect = false;
				} else {
					ZLog.e("diary publish error(" + response.status + ")");
					errcode = TASK_ERROR_NOT_COLLECTED;
//					translateToState(STATE_ERROR);
				}
			}
			if(getState() == STATE_PAUSED) {
				ZLog.e("Status changed to [" + getState() + "] when RESPONSE_TYPE_ADD_COLLECT_DIARY");
				translateToState(STATE_PAUSED);
				break;
			}
			translateToState(STATE_COMPELETED);
			break;

		case STATE_RUNNING:
			if(!canTaskRun()) {
				errcode = TASK_ERROR_TIMEOUT;
				translateToState(STATE_ERROR);
				break;
			}
			download();
			break;

		// case STATE_EDITORING:
		// download();
		// break;
		//
		case STATE_PAUSED:
			closeHttp();
			retryTimes = 0;
			stopThread();
			break;

		case STATE_REMOVED:
			retryTimes = 0;
			stopThread();
			if (taskmanagerListener != null) {
				taskmanagerListener.OnTaskRemoved(this);
			}
			break;

		case ACTION_RETRY:
			ZLog.e(" handleMessage ACTION_RETRY");
			if (retryTimes <= 3) {
				retryTimes++;
				ZThread.sleep(1500 * retryTimes + 500);// 500, 2000, 3500, 5000
				if(getState() == STATE_PAUSED) {
					ZLog.e("Status changed to [" + getState() + "] when download retrying!");
					translateToState(STATE_PAUSED);
					break;
				} else if(getState() == STATE_WAITING) {
					ZLog.e("Status changed to [" + getState() + "] when download retrying, ignore it!");
					break;
				}
				if (info.isPrepareDone) {
					translateToState(STATE_RUNNING);
				} else {
					translateToState(STATE_PREPARING);
				}
				// return是为了不执行 notifyStateChange
//				return false;
			} else {
				errcode = TASK_ERROR_TIMEOUT;
				translateToState(STATE_ERROR);
			}
			break;
			
		case STATE_COLLECT:
			if(info.isCollect) {
//				Requester3.addcollectDiary(handler, info.diaryid);
			} else {
				if(getState() == STATE_PAUSED) {
					ZLog.e("Status changed to [" + getState() + "] when STATE_COLLECT");
					translateToState(STATE_PAUSED);
					break;
				}
				translateToState(STATE_COMPELETED);
			}
			break;
			
		case STATE_COMPELETED:
			retryTimes = 0;
			stopThread();
			break;

		case STATE_ERROR:
			retryTimes = 0;
			break;

		}

		return false;
	}
	
	@SuppressLint("SdCardPath")
	private void download() {
		
		try {
//			synchronized (Thread.currentThread()) {
//				Thread.currentThread().wait(10000);				
//			}
			isResumeTrans = false;
			HashMap<String, String> headers = new HashMap<String, String>();
			long fileLength = 0;
			if (info.downMedias == null || info.downMedias.size() <= 0
					|| currentDownloadIndex > info.downMedias.size() - 1) {
				Log.d("==WR==", "下载文件列表为空，不需要启动下载任务！");
				if(getState() == STATE_PAUSED) {
					ZLog.e("Status changed to [" + getState() + "] before task download completed!");
					translateToState(STATE_PAUSED);
					return;
				}
				translateToState(STATE_COMPELETED);
				return;
			}
			
			downMedia df = new downMedia();
			df = info.downMedias.get(currentDownloadIndex);
//			df.attachMedia.localpath = Environment.getExternalStorageDirectory() + Constant.SD_STORAGE_ROOT + "/" + info.userid + "/downtask/" +
//					MD5.encode((df.attachMedia.remotepath).getBytes());
			ZLog.e("df.path = " + df.attachMedia.localpath);
			File file = new File(df.attachMedia.localpath);
			if(!file.getParentFile().exists()) {
				file.getParentFile().mkdirs();
			}
			if (file.exists()) {
				fileLength = file.length();
				headers.put("range", "bytes=" + fileLength + "-");
			}
			ZLog.e("fileLength = " + fileLength + " path="
					+ file.getAbsolutePath());
			info.currentDownloaingLength = fileLength;

			http = new ZHttp2();
			http.setHeaders(headers);
			ZLog.e("url=" + df.attachMedia.remotepath);
			ZHttpResponse response = http.get(df.attachMedia.remotepath);
//			 response.printHeaders();
			
			ZLog.e(response==null?"response is null":"response is not null");
			if (response != null && response.getResponseCode() == HttpURLConnection.HTTP_OK) { // 从头下载;
				Log.d("==WR==", "http回传[200]，从头开始下载");
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
				Log.d("==WR==", "http回传[206]，断点续传模式下载");
				isResumeTrans = true;
				writer = new ZFileWriter(file, true, null);
				ZLog.e(206);
			} else if(response == null) {
				if (getState() == STATE_RUNNING || getState() == ACTION_RETRY) {
					Log.d("==WR==", "http响应为空，任务重试！");
					translateToState(ACTION_RETRY);
				} else {
					Log.d("==WR==", "http响应为空，任务失败！");
					errcode = TASK_ERROR_SERVER_ERROR;
					translateToState(STATE_ERROR);
				}
				return;
			}
			ZLog.e("content length = " + response.getHeader(HTTP.CONTENT_LEN));
			long contentLength = 0;
			if(response.getHeader(HTTP.CONTENT_LEN) != null) {
				contentLength = Long.valueOf(response.getHeader(HTTP.CONTENT_LEN));
			}
//			if (info.currentDownloaingTotalLength == 0) {
//				info.currentDownloaingTotalLength = contentLength;
//			}

			if (contentLength != 0 && contentLength <= info.currentDownloaingLength && !isResumeTrans) {
				currentDownloadIndex++;
				info.currentDownloaingTotalLength += info.currentDownloaingLength;
				updateMediaMapping(file, df.attachMedia);
				//开始下载下一个文件
				ZLog.e("开始下载下一个文件");
				translateToState(STATE_RUNNING);
				return;
			}
			
			isError = false;

			OnPercentChangedListener listener = new OnPercentChangedListener() {
				@Override
				public void onPercentChanged(ZPercent percent) {
					try {
						if (getState() == STATE_PAUSED
								|| getState() == STATE_REMOVED) {
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
						isError = true;
						if (reader != null) {
							reader.stop();
						}
					}
				}
			};

			reader = new ZHttpReader(response.getInputStream(), listener);
			if(!reader.readByBlockSize2(contentLength, 1024 * 4)) {
				Log.d("==WR==", "下载读取数据时出错，重试！");
				ZLog.e(" retry=" + retryTimes);
				translateToState(ACTION_RETRY);
				return;
			}
			reader.close();
			writer.close();
			http.close();

			if (isError && getState() == STATE_RUNNING) {
				translateToState(ACTION_RETRY);
			} else if (reader.isEnding() && getState() == STATE_RUNNING) {
				info.finishedFileIndex = currentDownloadIndex;
				//TODO:登记到MediaMapping
				//……
				updateMediaMapping(file, df.attachMedia);
				
				if (info.finishedFileIndex == info.downMedias.size() - 1) {
					if(getState() == STATE_PAUSED) {
						ZLog.e("Status changed to [" + getState() + "] before task download all completed!");
						Log.d("==WR==", "已经全部下载完毕，但之前已暂停！");
						translateToState(STATE_PAUSED);
						return;
					}
					Log.d("==WR==", "已经全部下载完毕！");
					translateToState(STATE_COMPELETED);
					return;
				}
				Log.d("==WR==", "还有其他文件待下载");
				currentDownloadIndex++;
				info.currentDownloaingTotalLength += info.currentDownloaingLength;
				//开始下载下一个文件
				ZLog.e("开始下载下一个文件");
				translateToState(STATE_RUNNING);
				return;
			}

		} catch (Exception e) {
			e.printStackTrace();
			closeHttp();
			ZLog.e("Exception occured in State" + getState());
			if (getState()!=STATE_PAUSED && getState() != STATE_ERROR) {
				Log.d("==WR==", "下载过程发生异常，任务重试！");
				ZLog.e(" retry=" + retryTimes);
				translateToState(ACTION_RETRY);
			}
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
		long current = TimeHelper.getInstance().now();
		if (current - updateTime > 500) {
			if (taskmanagerListener != null) {
				taskmanagerListener.notifyPrecentChange(
						DownloadNetworkTask.this,
						info.totalTaskSize,
						info.currentDownloaingTotalLength);
			}
			updateTime = current;
		}
	}

	private void updateMediaMapping(File file, MediaFile mediafile) {
		long fileLength = 0;
		if (file.exists()) {
			fileLength = file.length();
		}
		MediaValue mediaValue = new MediaValue();
		mediaValue = AccountInfo.getInstance(info.userid).mediamapping.getMedia(info.userid, mediafile.remotepath);
		if (mediaValue != null) {
			Log.d("==WR==", "download mappingKey " + mediaValue.url + " path = " + mediaValue.localpath);
			mediaValue.Belong = 1;
			String uid = ActiveAccount.getInstance(ZApplication.getInstance()).getLookLookID();
			if(uid != null && uid.equals(info.userid)) {
				mediaValue.Belong = 2;
			}
			mediaValue.totalSize = fileLength;
			mediaValue.realSize = fileLength;
			AccountInfo.getInstance(info.userid).mediamapping.delMedia(info.userid, mediaValue.url);
			AccountInfo.getInstance(info.userid).mediamapping.setMedia(info.userid, mediaValue.url, mediaValue);
		}
	}
}
