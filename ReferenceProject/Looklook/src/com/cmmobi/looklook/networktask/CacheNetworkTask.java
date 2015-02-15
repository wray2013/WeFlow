package com.cmmobi.looklook.networktask;

import java.io.File;
import java.util.ArrayList;

import android.os.Message;
import android.util.Log;
import android.widget.Toast;
import cn.zipper.framwork.core.ZApplication;
import cn.zipper.framwork.io.network.ZNetworkStateDetector;

import com.cmmobi.looklook.common.gson.GsonRequest2.Attachs;
import com.cmmobi.looklook.common.gson.GsonRequest2.createStructureRequest;
import com.cmmobi.looklook.common.gson.GsonResponse2;
import com.cmmobi.looklook.common.gson.GsonResponse2.MyDiary;
import com.cmmobi.looklook.common.gson.GsonResponse2.diaryAttach;
import com.cmmobi.looklook.common.gson.Requester2;
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
			Log.d("==WR==", "cacheAttach length = " + attachs.length);
			//遍历请求附件
			for(int i = 0; i < attachs.length; i++) {
				String attachuuid = attachs[i].attachuuid;
				Log.d("==WR==", "cacheAttach uuid = " + attachuuid);
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
				Log.d("==WR==","tmpFile is not exists!");
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
				Requester2.createStructure(handler,
						this.info.upRequest.diaryid,
						this.info.upRequest.diaryuuid,
						this.info.upRequest.operate_diarytype,
						this.info.upRequest.resourcediaryid,
						this.info.upRequest.resourcediaryuuid,
						this.info.upRequest.logitude,
						this.info.upRequest.latitude, this.info.upRequest.tags,
						this.info.upRequest.userselectposition,
						this.info.upRequest.userselectlogitude,
						this.info.upRequest.userselectlatitude,
						String.valueOf(TimeHelper.getInstance().now()),
						this.info.upRequest.addresscode,
						this.info.upRequest.attachs);
			} else {
//				Toast.makeText(ZApplication.getInstance().getApplicationContext(), "网络异常，无法启动该任务！", Toast.LENGTH_LONG).show();
				translateToState(STATE_ERROR);
			}
			break;

		case Requester2.RESPONSE_TYPE_CREATE_STRUCTURE:
			GsonResponse2.createStructureResponse response2 = (GsonResponse2.createStructureResponse) msg.obj;
			if (msg.obj != null && response2 != null
					&& response2.attachs != null && response2.status.equals("0")) {
				this.info.diaryid = response2.diaryid; // 取得diaryid
				DiaryManager diarymanager = DiaryManager.getInstance();
				MyDiary myLocalDiary = diarymanager.findLocalDiaryByUuid(response2.diaryuuid);
				if (myLocalDiary != null) {
					myLocalDiary.diaryid = response2.diaryid;
					myLocalDiary.sync_status = 1;
					Log.d("==WR==","diaryid = " + myLocalDiary.diaryid);
				} else {
					return false;
				}
				
				if(this.info == null || this.info.caMedias == null || this.info.caMedias.size() <= 0) {
					Log.e("==WR==","Do not click the control button rapidly!");
					return false;
				}
				
				for (int i = 0; i < response2.attachs.length; i++) {
					for (int j = 0; j < this.info.caMedias.size(); j++) {
						Log.d("==WR==", "caMedias size = " + this.info.caMedias.size() + "i = " + i + ",j = " + j);
						upMedia tmpmedia = this.info.caMedias.get(j);
						if(tmpmedia == null) {
							Log.d("==WR==", "tmpmedia is null");
						}
						if (response2.attachs[i] != null
								&& tmpmedia.attachuuid.equals(response2.attachs[i].attachuuid)) {
							this.info.caMedias.get(j).attachid = response2.attachs[i].attachid;// 取得attachid
							
							// tmpmf.remotepath = response2.attachs[i].path;
							for (int k = 0; k < this.info.caMedias.get(j).attachMedia.size(); k++) {
								this.info.caMedias.get(j).attachMedia.get(k).remotepath = response2.attachs[i].path;// 取得服务器地址
								// 写入mapping
								if (response2.attachs[i].path != null) {
									MediaValue tmMV = new MediaValue();
									tmMV = AccountInfo
											.getInstance(this.info.userid).mediamapping
											.getMedia(this.info.userid,
													tmpmedia.attachuuid);
									Log.d("==WR==", "get MediaValue.url="
											+ tmMV.url);
									tmMV.url = response2.attachs[i].path;
									AccountInfo.getInstance(this.info.userid).mediamapping
											.setMedia(this.info.userid,
													response2.attachs[i].path,
													tmMV);
//									AccountInfo.getInstance(this.info.userid).mediamapping
//											.delMedia(this.info.userid,
//													tmpmedia.attachuuid);
									Log.d("==WR==", "set MediaValue.url=" + tmMV.url);
								}
							}
						}
					}
					
					// 修改日记结构
					if (myLocalDiary != null && myLocalDiary.attachs!= null) {
						for (int k = 0; k < myLocalDiary.attachs.length;k++) {
							diaryAttach attach = myLocalDiary.attachs[k];
							if (response2.attachs[i] != null && attach.attachuuid.equals(response2.attachs[i].attachuuid)) {
								attach.attachid = response2.attachs[i].attachid;
								if (attach.attachaudio != null && "2".equals(attach.attachtype) && attach.attachaudio.length > 0) {
									attach.attachaudio[0].audiourl = response2.attachs[i].path;
								} else if (attach.attachvideo != null && "1".equals(attach.attachtype) && attach.attachvideo.length > 0) {
									attach.attachvideo[0].playvideourl = response2.attachs[i].path;
									this.info.coverid = response2.attachs[i].attachid;
								} else if (attach.attachimage != null && "3".equals(attach.attachtype) && attach.attachimage.length > 0) {
									attach.attachimage[0].imageurl = response2.attachs[i].path;
								}
							}
						}
					}
				}
				
				DiaryManager.getInstance().diaryDataChanged();
				
				// 转变正常上传任务
				translateToState(STATE_COMPELETED);
			} else {
				translateToState(STATE_ERROR);
			}
			break;		

		// 转变正常上传任务
		case STATE_RUNNING:
			break;
			
		case STATE_PAUSED:
			break;

		case STATE_REMOVED:
			if (taskmanagerListener != null) {
				taskmanagerListener.OnTaskRemoved(this);
			}
			break;

		case ACTION_RETRY:
			break;

		// 转变正常上传任务
		case STATE_COMPELETED:
			break;

		case STATE_ERROR:
			break;
		}

		return false;
	}

}
