package com.cmmobi.looklook.networktask;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.List;

import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import cn.zipper.framwork.core.ZApplication;
import cn.zipper.framwork.core.ZLog;
import cn.zipper.framwork.device.ZSimCardInfo;
import cn.zipper.framwork.io.file.ZFileReader;
import cn.zipper.framwork.utils.ZPercent;
import cn.zipper.framwork.utils.ZStringUtils;
import cn.zipper.framwork.utils.ZPercent.OnPercentChangedListener;
import cn.zipper.framwork.utils.ZThread;

import com.cmmobi.looklook.common.gson.GsonResponse3;
import com.cmmobi.looklook.common.gson.GsonResponse3.MyDiary;
import com.cmmobi.looklook.common.gson.Requester3;
import com.cmmobi.looklook.common.utils.Base64Utils;
import com.cmmobi.looklook.info.profile.AccountInfo;
import com.cmmobi.looklook.info.profile.ActiveAccount;
import com.cmmobi.looklook.info.profile.DiaryManager;
import com.cmmobi.looklook.info.profile.MediaValue;
import com.cmmobi.looklook.info.profile.NetworkTaskInfo;
import com.cmmobi.looklook.info.profile.NetworkTaskInfo.MediaFile;
import com.cmmobi.looklook.info.profile.NetworkTaskInfo.upMedia;
import com.cmmobi.looklook.info.profile.TimeHelper;
import com.google.gson.Gson;

/**
 * 日记任务 上传 下载
 * 
 * 通过状态来驱动任务
 * 
 * 
 */
public class UploadNetworkTask extends INetworkTask {

	private transient Socket socket;
	private transient ZFileReader reader;
//	private transient DataInputStream dataInputStream;
	private transient BufferedReader dataInputStream;
	private transient DataOutputStream dataOutputStream;

	private int retryTimes;
	private long updateTime;
	private int willUploadFileIndex;
	
	private int sliceIndex = 0; //子文件索引
	private int sliceFinished;  //子文件完成号
	
	private boolean isError;


	public UploadNetworkTask(NetworkTaskInfo info) {
		super(info);
		info.taskType = TASK_TYPE_UPLOAD;
	}
	public UploadNetworkTask(NetworkTaskInfo info, List<String> attachids) {
		super(info);
		info.taskType = TASK_TYPE_UPLOAD;
		for (String id : attachids) {
			for (upMedia upmedia : info.upMedias) { // 检查id对应附件
				if (upmedia.attachid.equals(id)) { //置为多段上传附件
					upmedia.type = "n";
				}
			}
		}
	}
	
	/*
	 *  String attachid     : 附件id
	 *  String tmpFile      : 附加片段文件uri
	 *  boolean isCompleted : 片段是否已经结束
	 */
	public void setExtraAttach(String attachid, String tmpFile, boolean isCompleted) {
		//该文件是否存在
		//File file = new File(attachfile.attachMedia[sliceIndex].localpath);
		if(tmpFile != null) {
			File file = new File(tmpFile);
			if(!file.exists()) { //附件片段不存在
				Log.d("==WR==","tmpFile is not exists!");
				return;
			}
			MediaFile tmpMedia = new MediaFile();
			//找到对应附件id的附件结构
			for (int i = 0; i < info.upMedias.size(); i++) { // 检查id对应附件
				upMedia upmedia = info.upMedias.get(i);
				if (upmedia.attachid.equals(attachid) && upmedia.type.equals("n")) {
					int size = info.upMedias.get(i).attachMedia.size();
					//待添加的附件片段
					tmpMedia.remotepath = upmedia.attachMedia.get(0).remotepath;//第一段文件
					tmpMedia.type = upmedia.attachMedia.get(0).type;
					tmpMedia.localpath = tmpFile;
					//构建新结构
					info.upMedias.get(i).attachMedia.add(size, tmpMedia);
					info.upMedias.get(i).sliceCompleted = isCompleted;
					break;
				}
			}
		}
	}

	public void setPublish(boolean b) {
		info.isPublish = b;
	}

	@Override
	public boolean handleMessage(Message msg) {

		switch (msg.what) {
		case STATE_WAITING:
			Log.d("debug", "I am waiting!");
			break;
			
		case STATE_PREPARING:
			if (TextUtils.isEmpty(info.ip)) {
				//创建上传通道
				Requester3.requestSocketIp(handler);
			} else if (willUploadFileIndex < info.upMedias.size()) {
				if(getState() == STATE_PAUSED) {
					ZLog.e("Status changed to [" + getState() + "] when preparing!");
					translateToState(STATE_PAUSED);
					break;
				}
				translateToState(STATE_RUNNING, 2);
			} else {
				translateToState(STATE_COMPELETED);
			}
			break;
		
		// Requester.requestSocketIp(handler) will triger this case;
		case Requester3.RESPONSE_TYPE_GET_SOCKET:
			if (getState() == STATE_PREPARING) {
				GsonResponse3.getSocketResponse response = (GsonResponse3.getSocketResponse) msg.obj;
				if (response != null && !TextUtils.isEmpty(response.ip)) {
					info.ip = response.ip;
					String port = ZStringUtils.nullToEmpty(response.port);
					try{
						info.port = Integer.parseInt(port.trim());
					}catch(Exception e) {
						ZLog.e("Invalid socket port!" + response.port);
						e.printStackTrace();
						translateToState(STATE_PAUSED);
						break;
					}
					if(getState() == STATE_PAUSED) {
						ZLog.e("Status changed to [" + getState() + "] when geting socket!");
						translateToState(STATE_PAUSED);
						break;
					}
					/*
					if(hasCover() == null) {
						translateToState(STATE_RUNNING, 3);
					} else if(canTaskRun()){
						uploadCover();
					} else {
						errcode = TASK_ERROR_TIMEOUT;
						translateToState(STATE_ERROR);
					}
					*/
					translateToState(STATE_RUNNING, 5);
					
				} else {
					errcode = TASK_ERROR_NOT_GETSOCKET;
					translateToState(STATE_ERROR);
				}
			}
			break;
		case Requester3.RESPONSE_TYPE_DIARY_PUBLISH:
			if (getState() == STATE_COMPELETED) {
				GsonResponse3.diaryPublishResponse response = (GsonResponse3.diaryPublishResponse) msg.obj;
				if(response == null) {
					errcode = TASK_ERROR_SERVER_ERROR;
					translateToState(STATE_ERROR);
				}else if (!response.status.equals("0")) {
					ZLog.e("diary publish error(" + response.status + ")");
					errcode = TASK_ERROR_NOT_PUBLISHED;
					translateToState(STATE_ERROR);
				}
			}
			break;

		case STATE_RUNNING:
			closeSocket();
			/*判断网络*/
			if(!canTaskRun()) {
				errcode = TASK_ERROR_TIMEOUT;
				translateToState(STATE_ERROR);
				break;
			}
			
			if(getState() == STATE_PAUSED) {
				ZLog.e("Status changed to [" + getState() + "] when begin to run!");
				translateToState(STATE_PAUSED);
				break;
			} else if (getState() == STATE_WAITING) {
				ZLog.e("Status changed to [" + getState() + "] when begin to run, ignore it!");
				break;
			}
			upload();
			/*
			if(info.coveruploaded || hasCover() == null) {
				upload();
			} else {
				uploadCover();
			}
			*/
			break;

		case STATE_EDITING:
			if(retryTimes <= 3) {
				retryTimes ++;
				if(getState() == STATE_PAUSED) {
					ZLog.e("Status changed to [" + getState() + "] when editing!");
					translateToState(STATE_PAUSED);
					break;
				}
				if(sliceIndex >= info.upMedias.get(willUploadFileIndex).attachMedia.size()) {
					translateToState(STATE_EDITING);
					break;
				} else {
					ZThread.sleep(5000); //5s查询一次是否准备好片段
					upload();
				}
			}else {
				errcode = TASK_ERROR_TIMEOUT;
				translateToState(STATE_ERROR);
			}
			break;

		case STATE_PAUSED:
			stopThread();
			retryTimes = 0;
			closeSocket();
			break;

		case STATE_REMOVED:
			stopThread();
			retryTimes = 0;
			closeSocket();
			if (taskmanagerListener != null) {
				taskmanagerListener.OnTaskRemoved(this);
			}
			if (mitaskmanagerListener != null) {
				mitaskmanagerListener.OnTaskRemoved(this);
			}
			break;

		case ACTION_RETRY:
			if (retryTimes <= 3) {
				retryTimes++;
				ZThread.sleep(1500 * retryTimes + 500);// 500, 2000, 3500, 5000
				if(getState() == STATE_PAUSED) {
					ZLog.e("Status changed to [" + getState() + "] when retrying!");
					translateToState(STATE_PAUSED);
					break;
				} else if(getState() == STATE_WAITING) {
					ZLog.e("Status changed to [" + getState() + "] when retrying, ignore it!");
					break;
				}
				translateToState(STATE_RUNNING, 4);
				// return是为了不执行 notifyStateChange
				return false;
			} else {
				errcode = TASK_ERROR_TIMEOUT;
				translateToState(STATE_ERROR);
			}
			break;

		case STATE_COMPELETED:
			stopThread();
			retryTimes = 0;
			closeSocket();
			//暂时不做任务后发布
			if (false && info.isPublish) {
//				Requester3.diaryPublish(handler, info.diaryid, "1","1","1","1");
			}
			break;

		case STATE_ERROR:
			retryTimes = 0;
			closeSocket();
			break;
/**
		case Requester3.RESPONSE_TYPE_UPLOAD_PICTURE:
			if(getState() != STATE_RUNNING && 
					getState() != STATE_PREPARING) {
				ZLog.e("Unexpected--Status changed to [" + getState() + "] when uploaded picture!");
				break;
			} else {
				ZLog.e("Status changed to [" + getState() + "] when uploaded picture!");
			}
			if (msg.obj != null) {
				GsonResponse3.uploadPictrue res = (GsonResponse3.uploadPictrue) msg.obj;
				if ("0".equals(res.status)) {
					Log.d("==WR==", "视频封面上传成功！ ");
					info.coveruploaded = true;
					translateToState(STATE_RUNNING, 5);
				} else {
					Log.d("==WR==", "视频封面上传失败！ response = " + res.status);
					errcode = TASK_ERROR_COVER_UPLOAD_FAIL;
					translateToState(STATE_ERROR);
				}
			} else {
				Log.d("==WR==", "视频封面上传失败！RIA返回null");
				errcode = TASK_ERROR_TIMEOUT;
				translateToState(STATE_ERROR);
			}
			break;
**/
		}

		return false;
	}

	/**
	 * 上传;
	 */
	private synchronized void upload() {

		try {
			// task.ip = "192.168.2.87";
			// task.port = 7885;
			// task.uploadFileName =
			// "temp_2013_04_16_1107063d37ed931bb64970bcc05bd51153c8c5.mp4";

			if(info.upMedias == null || info.upMedias.size() <= 0) {
				Log.d("==WR==", "上传文件列表为空，不需要启动上传任务！");
				translateToState(STATE_COMPELETED);
				return;
			}
			Log.d("==WR==", "上传文件数[" + (willUploadFileIndex + 1) + "/" + info.upMedias.size() + "]");
			if (socket == null) {
				ZLog.e("Socket Connect : " + info.ip + "; port " + info.port);
				socket = new Socket();
				socket.setKeepAlive(true);
				socket.setSoTimeout(10 * 1000);
				socket.connect(new InetSocketAddress(info.ip, info.port),
						10 * 1000);

				dataOutputStream = new DataOutputStream(
						socket.getOutputStream());
//				dataInputStream = new DataInputStream(socket.getInputStream());
				dataInputStream = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			}
			
			upMedia attachfile = info.upMedias.get(willUploadFileIndex);
			File file = new File(attachfile.attachMedia.get(sliceIndex).localpath);
			
			if(!isValidUploadPath(attachfile.attachMedia.get(sliceIndex).remotepath)) {
				Log.d("==WR==", "不是合法socket上传地址，跳过！");
				uploadNext(file);
				return;
			}
			
			String requestHeader = makeHeader(file, sliceIndex, 0,
					attachfile.attachMedia.get(sliceIndex).remotepath, attachfile.type,
					attachfile.rotation, attachfile.filetype,
					attachfile.businesstype, info.diaryid, attachfile.attachid,
					attachfile.isencrypt, 480, 640);
			if(requestHeader == null) {
				isError = true;
				closeSocket();
				translateToState(ACTION_RETRY);
				return;
			}
			
			dataOutputStream.write(requestHeader.getBytes());
			ZLog.e(">> Socket Request Header (file:" + file.getAbsolutePath()
					+ "): " + requestHeader);

			String line = dataInputStream.readLine() + ";";
			ZLog.e("<< Socket Response Header: " + line);
			if(line.equals("null;")) {
				isError = true;
				closeSocket();
				translateToState(ACTION_RETRY);
				return;
			}
			String ip = getStringValue(line, "ip");
			int port = getIntValue(line, "port");

//			if (!TextUtils.isEmpty(ip) && port > -1) {
//				info.ip = ip;
//				info.port = port;
//			}

			int dataover = getIntValue(line, "dataover");

			if (dataover == 0) {
				Log.d("==WR==", "头信息错误，重试！");
				isError = true;
				closeSocket();
				translateToState(ACTION_RETRY);
				return;
				
			} else if(dataover == 4) { 
				Log.d("==WR==", "文件传输错误，重试！");
				isError = true;
				closeSocket();
				translateToState(ACTION_RETRY);
				return;
			} else if (dataover == 1) {

				long skip = Long.valueOf(getStringValue(line, "position"));
				long offset = info.uploadedLength - info.uploadedFileBlock;
				Log.d("==WR==", "文件续传 skip = " + skip + ";上次上传到 " + info.uploadedLength
						+ ";offset = " + offset);
				addStep(skip - offset);
				isError = false;

				OnPercentChangedListener listener = new OnPercentChangedListener() {

					@Override
					public void onPercentChanged(ZPercent percent) {
						try {
							// if (getState() == STATE_PAUSED
							// || getState() == STATE_REMOVED) {
							if (getState() != STATE_RUNNING) {
								Log.d("==WR==", "上传过程暂停");
								if (reader != null) {
									reader.stop();
								}
							}
							Object object = percent.getObject();
							if (object != null) {
								byte[] bytes = (byte[]) object;
								dataOutputStream.write(bytes);
								addStep(bytes.length);
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

				reader = new ZFileReader(file, listener);
				reader.skip(skip);
				reader.readByBlockSize2(file.length(), 1024 * 4);
				reader.close();

				if (isError && getState() == STATE_RUNNING) {
					// Retry
					closeSocket();
					translateToState(ACTION_RETRY);
					return;
				} else if (reader.isEnding()) {

					String line2 = dataInputStream.readLine() + ";";
					dataover = getIntValue(line2, "dataover");
					ZLog.e("<< Socket Write-Finish : " + line2);

					int dataover2 = -1;
					String diarysync = "";
					if (dataover == 2) {
						int tmpLastTimes = retryTimes;
						retryTimes = 0;
						//多段上传模式，检查片段是否全部上传完毕
						if(attachfile.type.equals("n")) {
						//如果所有小文件都传完
							Log.d("==WR==", "多段上传模式");
							if (sliceIndex == info.upMedias.get(willUploadFileIndex).attachMedia.size() - 1) {
								Log.d("==WR==", "当前小文件都传完");
								if (attachfile.sliceCompleted) {
									Log.d("==WR==", "所有小文件都已传完");
									String notify = makeHeader(
											file,
											sliceIndex,
											1,
											attachfile.attachMedia.get(sliceIndex).remotepath,
											attachfile.type,
											attachfile.rotation,
											attachfile.filetype,
											attachfile.businesstype,
											info.diaryid, attachfile.attachid,
											attachfile.isencrypt,
											480, 640);
									if(notify == null) {
										isError = true;
										closeSocket();
										translateToState(ACTION_RETRY);
										return;
									}
									dataOutputStream.write(notify.getBytes());
									ZLog.e(">> Socket Upload-Finish(Multiple files task) Notify: "
											+ notify);

									String line3 = dataInputStream.readLine()
											+ ";";
									dataover2 = getIntValue(line3, "dataover");
									diarysync = getStringValue(line3, "diarysync");
									ZLog.e("<< Socket Write-Finish : " + line3);

									// 检查是否有新片段还在拍摄中
									// 拍摄中,等待
								} else if (!attachfile.sliceCompleted) {
									Log.d("==WR==", "拍摄中,等待");
									retryTimes = 0;
									info.uploadedFileBlock += file.length();
									sliceFinished = sliceIndex;
									sliceIndex++;
									translateToState(STATE_EDITING);
									return;
								}
							}
							//小文件未传完
							else { //文件片段未上传完，继续下一片段
								Log.d("==WR==", "文件片段未上传完，继续下一片段");
								retryTimes = 0;
								info.uploadedFileBlock += file.length();
								sliceFinished = sliceIndex;
								sliceIndex++;
								if (getState() == STATE_RUNNING) {
									// upload next slice file
									translateToState(STATE_RUNNING, 6);
									return;
								}
							}
						
						//单文件上传，直接通知服务器over=1
						} else if(attachfile.type.equals("1")) {
							Log.d("==WR==", "单文件上传");
							String notify = makeHeader(file,
									sliceIndex, 1,
									attachfile.attachMedia.get(sliceIndex).remotepath, attachfile.type,
									attachfile.rotation, attachfile.filetype,
									attachfile.businesstype, info.diaryid,
									attachfile.attachid, attachfile.isencrypt,
									480, 640);
							if(notify == null) {
								isError = true;
								closeSocket();
								translateToState(ACTION_RETRY);
								return;
							}
							dataOutputStream.write(notify.getBytes());
							ZLog.e(">> Socket Upload-Finish(Single file task) Notify: " + notify);
							
							String line3 = dataInputStream.readLine() + ";";
							dataover2 = getIntValue(line3, "dataover");
							diarysync = getStringValue(line3, "diarysync");
							ZLog.e("<< Socket Write-Finish : " + line3);
							
						}
						//服务器回传完成，开始下一个文件
						if (dataover2 == 3) {
							//已经是最后一个附件且日记同步失败
							if(info.upMedias != null && info.upMedias.size() > willUploadFileIndex
									&& info.upMedias.get(willUploadFileIndex) != null
									&& info.upMedias.get(willUploadFileIndex).attachMedia != null
									&& info.upMedias.get(willUploadFileIndex).attachMedia.size() > sliceIndex) {
								updateMediaMapping(attachfile, file, info.upMedias.get(willUploadFileIndex).attachMedia.get(sliceIndex));
							}
							if (willUploadFileIndex == info.upMedias.size() - 1
									&& !syncDiary(diarysync)) {
								retryTimes = tmpLastTimes;
								closeSocket();
								translateToState(ACTION_RETRY);
								return;
							}
							uploadNext(file);
							return;
						} else {
							retryTimes = tmpLastTimes;
							closeSocket();
							translateToState(ACTION_RETRY);
							return;
						}
					} else if (getState() == STATE_RUNNING) {
						closeSocket();
						translateToState(ACTION_RETRY);
						return;
					}
				} else if (getState() == STATE_PAUSED
						|| getState() == STATE_REMOVED) {
					retryTimes = 0;
					closeSocket();
					return;
				}

			} else if (dataover == 2) {
				ZLog.alert();
				ZLog.e("File: " + file.getAbsolutePath() + " Already Uploaded.");
				// 已经全部上传完毕,再做一次同步
				if(willUploadFileIndex == info.upMedias.size() - 1) {
					int tmpLastTimes = retryTimes;
					String notify = makeHeader(file,
							sliceIndex, 1,
							attachfile.attachMedia.get(sliceIndex).remotepath, attachfile.type,
							attachfile.rotation, attachfile.filetype,
							attachfile.businesstype, info.diaryid,
							attachfile.attachid, attachfile.isencrypt,
							480, 640);
					if(notify == null) {
						isError = true;
						closeSocket();
						translateToState(ACTION_RETRY);
						return;
					}
					dataOutputStream.write(notify.getBytes());
					ZLog.e(">> Retry Socket Upload-Finish(Single file task) Notify: " + notify);
					
					String rline = dataInputStream.readLine() + ";";
					int rdataover = getIntValue(rline, "dataover");
					String diarysync = getStringValue(rline, "diarysync");
					ZLog.e("<< Retry Socket Write-Finish : " + rline);
					//服务器回传完成，开始下一个文件
					if (rdataover == 3) {
						if(info.upMedias != null && info.upMedias.size() > willUploadFileIndex
								&& info.upMedias.get(willUploadFileIndex) != null
								&& info.upMedias.get(willUploadFileIndex).attachMedia != null
								&& info.upMedias.get(willUploadFileIndex).attachMedia.size() > sliceIndex) {
							updateMediaMapping(attachfile, file, info.upMedias.get(willUploadFileIndex).attachMedia.get(sliceIndex));
						}
						if (willUploadFileIndex == info.upMedias.size() - 1
								&& !syncDiary(diarysync)) {
							retryTimes = tmpLastTimes;
							closeSocket();
							translateToState(ACTION_RETRY);
							return;
						}
						uploadNext(file);
						return;
					} else {
						retryTimes = tmpLastTimes;
						closeSocket();
						translateToState(ACTION_RETRY);
						return;
					}
				}
				uploadNext(file);
				return;
			}

		} catch (Exception e) {
			e.printStackTrace();
			closeSocket();
			ZLog.e("Exception occured in State" + getState());
//			translateToState(ACTION_RETRY);// Testing
			if (getState() != STATE_PAUSED) {
				ZLog.e("Upload retry=" + retryTimes);
				translateToState(ACTION_RETRY);
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

	private void uploadNext(File file) {
		Log.d("==WR==", "服务器回传完成");
		
/*		if(info.upMedias != null && info.upMedias.size() > willUploadFileIndex
				&& info.upMedias.get(willUploadFileIndex) != null
				&& info.upMedias.get(willUploadFileIndex).attachMedia != null
				&& info.upMedias.get(willUploadFileIndex).attachMedia.size() > sliceIndex) {
			updateMediaMapping(file, info.upMedias.get(willUploadFileIndex).attachMedia.get(sliceIndex));
		}*/
		
		retryTimes = 0;
		info.uploadedFileBlock += file.length();
		info.finishedFileIndex = willUploadFileIndex;

		sliceIndex = 0;
		sliceFinished = -1;

		willUploadFileIndex++;

		// 已经全部上传完毕
		if (info.finishedFileIndex == info.upMedias.size() - 1) {
			Log.d("==WR==", "已经全部上传完毕, state = " + getState());
			ZLog.alert();
			ZLog.e("Files were all Uploaded.");
			willUploadFileIndex++;
			if (getState() == STATE_RUNNING) {
				translateToState(STATE_COMPELETED);
				return;
			}
		}
		// 还有其他文件待上传
		if (getState() == STATE_RUNNING) {
			Log.d("==WR==", "还有其他文件待上传");
			// upload next file
			ZLog.alert();
			ZLog.e("File: " + file.getAbsolutePath()
					+ " Uploaded, begin the next one");
			translateToState(STATE_RUNNING, 7);
			return;
		}

	}
	
	private void updateMediaMapping(upMedia upmedia, File file, MediaFile mediafile) {
		long fileLength = 0;
		if (file.exists()) {
			fileLength = file.length();
		}
		if(upmedia != null && ZStringUtils.emptyToNull(upmedia.businesstype) != null) {
			MediaValue mediaValue = new MediaValue();
			if("6".equals(upmedia.businesstype)) {
				mediaValue = AccountInfo.getInstance(info.userid).mediamapping.getMedia(info.userid, mediafile.localpath);
			} else {
				mediaValue = AccountInfo.getInstance(info.userid).mediamapping.getMedia(info.userid, mediafile.remotepath);
			}
			if (mediaValue != null) {
				Log.d("==WR==", "upload mappingKey " + mediaValue.url + " path = " + mediaValue.localpath);
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
	}
	
	private boolean syncDiary(String sync) {
		/*
		String enc = getEncoding(sync);
		try {
			Log.d("==WR==", "socket回传字符串编码转换[ " + enc + " --> "  + "utf-8 ]");
			sync = new String(sync.getBytes(enc), "utf-8");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
		if(sync == null || "".equals(sync)) {
			Log.d("==WR==", "服务器返回diarysync[" + sync + "],日记同步失败");
			return false;
		}
		sync = Base64Utils.decode(sync);
		Log.d("==WR==", "服务器返回diarysync[" + sync + "]");
		DiaryManager diaryManager = DiaryManager.getInstance();
		Gson gson = new Gson();
		Object obj = gson.fromJson(sync, MyDiary.class);
		if(obj instanceof MyDiary) {
			MyDiary mydiary = (MyDiary) obj;
			if(diaryManager.findMyDiaryByUUID(mydiary.diaryuuid) != null) {
				Log.d("==WR==", "日记[" + mydiary.diaryuuid + "]同步成功");
				mydiary.sync_status = 6;
				diaryManager.updateDiary((MyDiary)obj);
				DiaryManager.getInstance().notifyMyDiaryChanged();
			} else {
				Log.d("==WR==", "该日记本地不存在，可能已经移除, diaryuuid = [" + mydiary.diaryuuid + "]");
			}
		}
		return true;
	}
	
	/**
	 * 组装请求头;
	 * 
	 * @param file
	 *            : 要上传的文件;
	 * @param nuid
	 *            : 子文件序号 (从0开始);
	 * @param over
	 *            : 是否已经完成上传 (0: 未结束. 1: 已结束);
	 * @param fileName
	 *            : 文件物理路径;
	 * @param type
	 *            : 文件个数 (1: 只有一个文件. n: 有多个文件);
	 * @param rotation
	 *            : 旋转角度 (0/90/180/270);
	 * @param filetype
	 *            : 旋转角度 (0/90/180/270);
	 * @param businesstype
	 *            : 业务类型 (1 日记 2 评论3 私信 4 陌生人消息);
	 * @param diaryid
	 *            : 日记表id;
	 * @param attachmentid
	 *            : 附件表id(根据businesstype的不同对于不同表中的id);
	 * @param isencrypt
	 *            : 音频文件是否加密 (0未加密 1已加密);
	 * @return
	 */
	private String makeHeader(File file, int nuid, int over, String fileName,
			String type, int rotation, String filetype, String businesstype,
			String diaryid, String attachmentid, int isencrypt, int height,
			int width) {
		long fileLength = 0;

		if (file != null) {
			fileLength = file.length();
		}

		String uid = ActiveAccount.getInstance(ZApplication.getInstance())
				.getLookLookID();
		if(uid == null || uid.equals("")) {
			ZLog.e("USERID IS NULL!");
			return null;
		}
		String os = "android" + ZSimCardInfo.getSystemReleaseVersion();
		String string = "appkey=11;" + "Content-Length=" + fileLength
				+ ";userid=" + uid + ";nuid=" + nuid + ";over=" + over
				+ ";attachpath=" + fileName + ";type=" + type + ";rotation="
				+ rotation + ";filetype=" + filetype + ";businesstype="
				+ businesstype + ";diaryid=" + diaryid + ";attachmentid="
				+ attachmentid + ";isencrypt=" + isencrypt + ";nickname=" 
				+ Base64Utils.encode(info.nickname) + ";height=" + height + ";width="
				+ width + ";os=" + os + "\r\n";

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
			value = response.substring(index + key.length() + 1,
					response.indexOf(";", index + key.length() + 1));
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

	private float getPercent(long c,long t) {
		float per = 0;
		if(t > 0) {
			per = c * 100 / t ;
		}
		return per;
	}
	private void addStep(long step) {
		info.uploadedLength += step;
		info.percentValue = getPercent(info.uploadedLength, info.totalTaskSize);
		
		if(info.uploadedLengthUI < info.uploadedLength) {
			info.uploadedLengthUI = info.uploadedLength;
		}
		info.percentValueUI = getPercent(info.uploadedLengthUI, info.totalTaskSize);
		
		long current = TimeHelper.getInstance().now();
		if (current - updateTime > 500) {
			if (taskmanagerListener != null) {
				taskmanagerListener.notifyPrecentChange(
						UploadNetworkTask.this, info.totalTaskSize,
						info.uploadedLength);
			}
			updateTime = current;
		}

	}
	
	private boolean isValidUploadPath(String url) {
		if(url != null) {
			url = url.toLowerCase();
			if(!url.startsWith("http://")) {
				return true;
			}
		}
		return false;
	}

	 public static String getEncoding(String str) {    
         String encode = "GB2312";    
        try {    
            if (str.equals(new String(str.getBytes(encode), encode))) {    
                 String s = encode;    
                return s;    
             }    
         } catch (Exception exception) {    
         }    
         encode = "ISO-8859-1";    
        try {    
            if (str.equals(new String(str.getBytes(encode), encode))) {    
                 String s1 = encode;    
                return s1;    
             }    
         } catch (Exception exception1) {    
         }    
         encode = "UTF-8";    
        try {    
            if (str.equals(new String(str.getBytes(encode), encode))) {    
                 String s2 = encode;    
                return s2;    
             }    
         } catch (Exception exception2) {    
         }    
         encode = "GBK";    
        try {    
            if (str.equals(new String(str.getBytes(encode), encode))) {    
                 String s3 = encode;    
                return s3;    
             }    
         } catch (Exception exception3) {    
         }    
        return "";    
     }    
	
}
