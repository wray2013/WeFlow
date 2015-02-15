package com.cmmobi.looklook.networktask;

import java.io.File;
import java.util.ArrayList;

import android.os.Message;
import android.util.Log;
import cn.zipper.framwork.core.ZLog;
import cn.zipper.framwork.io.network.ZNetworkStateDetector;
import cn.zipper.framwork.utils.ZThread;

import com.cmmobi.looklook.common.gson.GsonResponse3;
import com.cmmobi.looklook.common.gson.GsonResponse3.AuxAttach;
import com.cmmobi.looklook.common.gson.GsonResponse3.MainAttach;
import com.cmmobi.looklook.common.gson.GsonResponse3.MyDiaryList;
import com.cmmobi.looklook.common.gson.Requester3;
import com.cmmobi.looklook.common.gson.GsonRequest3.Attachs;
import com.cmmobi.looklook.common.gson.GsonRequest3.createStructureRequest;
import com.cmmobi.looklook.common.gson.GsonResponse3.MyDiary;
import com.cmmobi.looklook.common.gson.GsonResponse3.DiaryAttach;
import com.cmmobi.looklook.info.profile.AccountInfo;
import com.cmmobi.looklook.info.profile.DiaryManager;
import com.cmmobi.looklook.info.profile.MediaValue;
import com.cmmobi.looklook.info.profile.NetworkTaskInfo;
import com.cmmobi.looklook.info.profile.NetworkTaskInfo.MediaFile;
import com.cmmobi.looklook.info.profile.NetworkTaskInfo.upMedia;
import com.cmmobi.looklook.info.profile.TimeHelper;

/**
 * 日记任务 上传 下载
 * 
 * 通过状态来驱动任务
 * 
 * 
 */
public class CacheNetworkTask extends INetworkTask {

	private int retryTimes;
	
	private static final String TAG = "CacheNetworkTask";
	
	public CacheNetworkTask(NetworkTaskInfo info) {
		super(info);
		this.info.taskType = TASK_TYPE_CACHE;
		if(info.upRequest != null) {
			this.info.upRequest = info.upRequest;
			this.info.caMedias = info.caMedias;
		}
	}
	
	public CacheNetworkTask(NetworkTaskInfo info, createStructureRequest upRequest, ArrayList<upMedia> cachemedias) {
		super(info);
		this.info.taskType = TASK_TYPE_CACHE;
		if(upRequest != null) {
			this.info.upRequest = upRequest;
			cacheAttach(upRequest, cachemedias);
		}
		
	}
	
	public void cacheAttach(createStructureRequest upRequest, ArrayList<upMedia> cachemedias) {
		if(upRequest != null && upRequest.attachs != null
				&& upRequest.attachs.length > 0
				&& cachemedias != null
				&& cachemedias.size() > 0
				&& upRequest.attachs.length <= cachemedias.size()) {
			Attachs[] attachs = upRequest.attachs;
			Log.d(TAG, "cacheAttach length = " + attachs.length);
			//遍历请求附件
			for(int i = 0; i < attachs.length; i++) {
				String attachuuid = attachs[i].attachuuid;
				Log.d(TAG, "cacheAttach uuid = " + attachuuid);
				String operate = attachs[i].Operate_type;
				//追加
				if (operate.equals("1")) {
					for(upMedia tmpMedia : cachemedias) {
						if(getIndexOfMedia(attachuuid) != -1) {
							continue;
						}
						if(attachuuid.equals(tmpMedia.attachuuid)) {
							for(MediaFile mf : tmpMedia.attachMedia) { //文件片段
								File file = new File(mf.localpath);
								if (file.exists()) {
									this.info.totalTaskSize += file.length();
									tmpMedia.uptotalSize = file.length();
								}
							}
							this.info.caMedias.add(tmpMedia);
						}
					}
					//更新
				} else if (operate.equals("2")) {
					for(upMedia tmpMedia : cachemedias) {
						if(attachuuid.equals(tmpMedia.attachuuid)) {
							int rmIndex = getIndexOfMedia(attachuuid);
							if(rmIndex != -1) {
								this.info.caMedias.remove(rmIndex);
								this.info.caMedias.add(tmpMedia);
							}
						}
					}
					//删除
				} else if (operate.equals("3")){
					for(upMedia tmpMedia : cachemedias) {
						if(attachuuid.equals(tmpMedia.attachuuid)) {
							int rmIndex = getIndexOfMedia(attachuuid);
							if(rmIndex != -1) {
								this.info.caMedias.remove(rmIndex);
							}
						}
					}
				}

			}
		}
	}
	
	private int getIndexOfMedia(String uuid) {
		int index = -1;
		for(upMedia tmp : this.info.caMedias) {
			if(uuid.equals(tmp.attachuuid)) {
				index = this.info.caMedias.indexOf(tmp);
			}
		}
		return index;
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
				Log.d(TAG,"tmpFile is not exists!");
				return;
			}
			MediaFile tmpMedia = new MediaFile();
			//找到对应附件id的附件结构
			for (int i = 0; i < info.caMedias.size(); i++) { // 检查id对应附件
				upMedia upmedia = info.caMedias.get(i);
				if (upmedia.attachid.equals(attachid) && upmedia.type.equals("n")) {
					int size = info.caMedias.get(i).attachMedia.size();
					//待添加的附件片段
					tmpMedia.remotepath = upmedia.attachMedia.get(0).remotepath;//第一段文件
					tmpMedia.type = upmedia.attachMedia.get(0).type;
					tmpMedia.localpath = tmpFile;
					//构建新结构
					info.caMedias.get(i).attachMedia.add(size, tmpMedia);
					info.caMedias.get(i).sliceCompleted = isCompleted;
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
			//网络状态判断
			if (ZNetworkStateDetector.isAvailable()
					&& ZNetworkStateDetector.isConnected() && this.info != null
					&& this.info.upRequest != null) {
				//请求日记结构
				Requester3.createStructure(handler,
						this.info.upRequest.diaryid,
						this.info.upRequest.diaryuuid,
						this.info.upRequest.operate_diarytype,
						this.info.upRequest.resourcediaryid,
						this.info.upRequest.resourcediaryuuid,
						 this.info.upRequest.tags,
						String.valueOf(TimeHelper.getInstance().now()),
						this.info.upRequest.addresscode,
						this.info.upRequest.position_status,
						this.info.upRequest.isonlymic,
						this.info.upRequest.longitude_real,
						this.info.upRequest.latitude_real,
						this.info.upRequest.position_real,
						this.info.upRequest.longitude,
						this.info.upRequest.latitude,
						this.info.upRequest.position,
						this.info.upRequest.longitude_view,
						this.info.upRequest.latitude_view,
						this.info.upRequest.position_view,
						this.info.upRequest.shoottime,
						this.info.upRequest.attachs);
			} else {
//				Toast.makeText(ZApplication.getInstance().getApplicationContext(), "网络异常，无法启动该任务！", Toast.LENGTH_LONG).show();
				translateToState(STATE_ERROR);
			}
			break;

		case Requester3.RESPONSE_TYPE_CREATE_STRUCTURE:
			GsonResponse3.createStructureResponse response = (GsonResponse3.createStructureResponse) msg.obj;
			if (msg.obj != null && response != null
					&& response.attachs != null && response.status.equals("0")) {
				this.info.diaryid = response.diaryid; // 取得diaryid
				DiaryManager diarymanager = DiaryManager.getInstance();
				MyDiary myLocalDiary = diarymanager.findMyDiaryByUUID(response.diaryuuid);
				MyDiaryList diarylist = diarymanager.findDiaryGroupByUUID(response.diaryuuid);
				if (myLocalDiary != null && diarylist != null) {
					myLocalDiary.diaryid = response.diaryid;
					myLocalDiary.sync_status = 1;
					Log.d(TAG,"diaryid = " + myLocalDiary.diaryid);
					diarylist.diaryid = response.diaryid;
				} else if (this.info.source != 1) {
					Log.d(TAG, "Should Not Be Here!");
					translateToState(STATE_REMOVED);
					return false;
				}
				
				if(this.info == null || this.info.caMedias == null /*|| this.info.caMedias.size() <= 0*/) {
					Log.e(TAG,"Do not click the control button rapidly!");
					return false;
				}
				
				for (int i = 0; i < response.attachs.length; i++) {
					for (int j = 0; j < this.info.caMedias.size(); j++) {
						Log.d(TAG, "caMedias size = " + this.info.caMedias.size() + "i = " + i + ",j = " + j);
						upMedia tmpmedia = this.info.caMedias.get(j);
						if(tmpmedia == null) {
							Log.d(TAG, "tmpmedia is null");
						}
						if (response.attachs[i] != null
								&& tmpmedia.attachuuid.equals(response.attachs[i].attachuuid)) {
							this.info.caMedias.get(j).attachid = response.attachs[i].attachid;// 取得attachid
							
							// tmpmf.remotepath = response.attachs[i].path;
							for (int k = 0; k < this.info.caMedias.get(j).attachMedia.size(); k++) {
								String remoteUrl = this.info.caMedias.get(j).attachMedia.get(k).remotepath;
								this.info.caMedias.get(j).attachMedia.get(k).remotepath = response.attachs[i].path;// 取得服务器地址
								// 写入mapping
								if (response.attachs[i].path != null) {
									MediaValue tmMV = new MediaValue();
									tmMV = AccountInfo.getInstance(this.info.userid).mediamapping.getMedia(this.info.userid, remoteUrl);
									if (tmMV != null) {
										Log.d(TAG, "get MediaValue.url=" + tmMV.url);
										tmMV.url = response.attachs[i].path;
										AccountInfo.getInstance(this.info.userid).mediamapping
												.setMedia(this.info.userid, response.attachs[i].path, tmMV);
										AccountInfo.getInstance(this.info.userid).mediamapping
												.delMedia(this.info.userid, remoteUrl);
										Log.d(TAG, "set MediaValue.url=" + tmMV.url);
									}
								}
							}
						}
					}
					if(this.info.source != 1) {
						// 修改日记结构
						if (myLocalDiary != null && myLocalDiary.attachs != null) {
							DiaryAttach diaryattach = myLocalDiary.attachs;
							//主
							MainAttach mainattach = diaryattach.levelattach;
							if(mainattach != null) {
								if(mainattach.attachuuid.equals(response.attachs[i].attachuuid)) {
									mainattach.attachid = response.attachs[i].attachid;
									mainattach.attachurl = response.attachs[i].path;
								}
							}
							//辅
							AuxAttach[] auxattach = diaryattach.attach;
							if (auxattach != null) {
								for (int k = 0; k < auxattach.length; k++) {
									if(auxattach[k].attachuuid.equals(response.attachs[i].attachuuid)) {
										auxattach[k].attachid = response.attachs[i].attachid;
										auxattach[k].attachurl = response.attachs[i].path;
									}
								}
							}
						}
					}
				}
				if(this.info.source != 1)
					DiaryManager.getInstance().notifyMyDiaryChanged();
				
				// 转变正常上传任务
				translateToState(STATE_COMPELETED);
			} else {
				translateToState(ACTION_RETRY);
				return false;
			}
			break;		

		// 转变正常上传任务
		case STATE_RUNNING:
			retryTimes = 0;
			break;
			
		case STATE_PAUSED:
			retryTimes = 0;
			stopThread();
			break;

		case STATE_REMOVED:
			retryTimes = 0;
			stopThread();
			if (taskmanagerListener != null) {
				taskmanagerListener.OnTaskRemoved(this);
			}
			if (mitaskmanagerListener != null) {
				mitaskmanagerListener.OnTaskRemoved(this);
			}
			break;

		case ACTION_RETRY:
			if(retryTimes < 2) {
				retryTimes++;
				ZThread.sleep(500);// 500, 2000, 3500, 5000
				if(getState() == STATE_PAUSED) {
					ZLog.e("Status changed to [" + getState() + "] when retrying!");
					translateToState(STATE_PAUSED);
					break;
				} else if(getState() == STATE_WAITING) {
					ZLog.e("Status changed to [" + getState() + "] when retrying, ignore it!");
					break;
				}
				translateToState(STATE_PREPARING);
				// return是为了不执行 notifyStateChange
				return false;
			} else {
				translateToState(STATE_ERROR);
			}
			break;

		// 转变正常上传任务
		case STATE_COMPELETED:
			retryTimes = 0;
			stopThread();
			Log.d(TAG, "离线任务成功转换成上传任务");
			break;

		case STATE_ERROR:
			break;
		}

		return false;
	}

}
