package com.cmmobi.looklook.info.profile;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.UUID;

import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;
import cn.zipper.framwork.core.ZApplication;
import cn.zipper.framwork.core.ZLog;
import cn.zipper.framwork.utils.ZStringUtils;

import com.cmmobi.looklook.activity.LookLookActivity;
import com.cmmobi.looklook.common.constant.Constant;
import com.cmmobi.looklook.common.gson.GsonProtocol;
import com.cmmobi.looklook.common.gson.GsonRequest3.Attachs;
import com.cmmobi.looklook.common.gson.GsonRequest3.createStructureRequest;
import com.cmmobi.looklook.common.gson.GsonResponse3.AuxAttach;
import com.cmmobi.looklook.common.gson.GsonResponse3.MainAttach;
import com.cmmobi.looklook.common.gson.GsonResponse3.MyDiary;
import com.cmmobi.looklook.common.gson.GsonResponse3.DiaryAttach;
import com.cmmobi.looklook.common.utils.MD5;
import com.cmmobi.looklook.networktask.INetworkTask;
import com.cmmobivideo.utils.Mp4InfoUtils;

import effect.EffectType;

/**
 * 需要持久化的信息 
 *
 */
public final class NetworkTaskInfo {
	private transient static final String TAG = "NetworkTaskInfo2";
	//=============共同字段列在此处========================
	public String taskid;
	public String taskType;  //任务类型：上传/下载/离线
    public String taskCover;   //任务封面图片
    public String coverid;   //任务封面对应的视频附件attachid
    public boolean coveruploaded; //任务封面已上传完
    public int diaryType;    //对应日记任务类型，主文件类型  0： 未定义 ，1视频、2音频、3图片、4文字
	public String diaryid;   //任务对应操作的日记id
	public String diaryuuid;
	public String nickname;
    public String userid;
    public String diaryuserid;
    public String audioID; //私信语音专用
    public int source = 0;//0-普通来源 1-来自微享任务
    
    //专为非日记类型任务预留
    public String otherAttachid; //附件id
    
	public int state; //任务当前状态
	public boolean isPrepareDone;
	public boolean isActive = false; //是否主动开始任务
	public boolean isPriority = false; //是否优先
	public boolean isFromShare = false; //是否来自分享触发
	public int miShareSettings = 1; //0-任何网络 1-仅wifi
	
	public ArrayList<upMedia>   upMedias;
	public ArrayList<upMedia>   caMedias;
	public ArrayList<downMedia> downMedias;
	
	public int finishedFileIndex = -1; // 已经完成过的文件编号;
	public long totalTaskSize; //任务含媒体文件总大小
	public long curTaskSize;   //当前已完成(上传/下载)媒体文件大小
	
	public float percentValue, percentValueUI;
	
	public long currentUploadingLength;      //当前正在上传子任务完成大小
	public long currentUploadingTotalLength; //当前正在上传子任务完成大小
	
	public long currentDownloaingLength;      //当前正在下载子任务大小
	public long currentDownloaingTotalLength; //当前正在上传子任务大小
	
	public static class MediaFile {
		public String localpath;   //媒体文件本地路径
		public String remotepath;  //媒体文件服务器路径
		public String filesize;    //文件大小(Byte)
        /* 媒体文件类型
         * 图片：1有标 0无标
         * 视频：1原音 2加密音
		 * 音频：1高清、2普清、0原视频*/
		public String type;

	}
	//=============上传所需字段列在此处=====================
	public String ip;
	public int port;
	public long uploadedLength, uploadedLengthUI;
	public long uploadedFileBlock;
	public boolean isPublish = false; //上传完成后是否发布
	
	public static class upMedia {
		public String attachid;//根据businesstype的不同对于不同表中的id
		public String attachuuid;//根据businesstype的不同对于不同表中的id
		public String attachtype; //附件类型，1视频、2音频、3图片、4文字
		public List<MediaFile> attachMedia;
		public String type = "1"; //1 单文件上传 n 边拍边传
		public boolean sliceCompleted = false;//分段是否结束，type=n时有效
		public int rotation;//视频旋转角度
		public String filetype;//1 普清视频 2 高清视频 3 录音 4 图片 5语音描述
		public String businesstype;//1 日记  2 评论  3 私信  4头像 5用户空间封面 6日记封面
		public int isencrypt;
		public long uptotalSize;
		public long upcurSize;
	}
	
	public String diary_type;
	public String position_type;
	public String audio_type;
	//=============下载所需字段列在此处=====================
	public boolean isCollect = true; //下载完成后是否收藏

	public static class downMedia {
		public String attachid;   //根据businesstype的不同对于不同表中的id
		public String attachtype; //附件类型，1视频、2音频、3图片、4文字、5短录音、6文字+短录音
		public String attachlevel;
		public MediaFile attachMedia;
		public long downtotalSize;
		public long downcurSize;
	}
	
	//=============离线所需字段列在此处=====================
	public createStructureRequest upRequest; //构建日记请求
	
	//=============从日记创建=====================
	public NetworkTaskInfo(MyDiary d, String taskType) {
		if(d != null) {
			this.diaryid = d.diaryid;
			this.diaryuuid = d.diaryuuid;
			this.nickname = d.nickname;
//			this.userid = d.userid;
			this.diaryuserid = d.userid;
			this.userid = ActiveAccount.getInstance(ZApplication.getInstance()).getUID();
			this.upRequest = d.request;
			this.upMedias = new ArrayList<upMedia>();
			this.caMedias = new ArrayList<upMedia>();
			this.downMedias = new ArrayList<downMedia>();
			if(d.attachs != null) {
				this.taskCover = getTaskUrl(d.attachs);
				this.coveruploaded= false; 
				this.taskType = taskType;
				copyAttach(d.attachs, taskType);
				Log.d("==WR==", "NetworkTaskInfo upMedias size = " + upMedias.size() + "; caMedias size = " + caMedias.size());
			}
			curTaskSize = 0;
		}
	}
	
	private void copyAttach(DiaryAttach att, String taskType) {
		if (att == null || att.levelattach == null) {
			Log.e(TAG, "附件为空或主附件為空");
			return;
		}
		MainAttach  mAttach = att.levelattach;
		AuxAttach[] aAttach = att.attach;
		
		// 先提取主内容
		if (mAttach != null) {
			if (taskType.equalsIgnoreCase(INetworkTask.TASK_TYPE_UPLOAD)
					|| taskType.equalsIgnoreCase(INetworkTask.TASK_TYPE_CACHE)) {
				// 上传日记
				upMedia upmedia = new upMedia();
				MediaFile mf = new MediaFile();
				upmedia.attachid   = mAttach.attachid;
				upmedia.attachuuid = mAttach.attachuuid;
				upmedia.attachtype = mAttach.attachtype;
				upmedia.businesstype = "1"; //日记业务
				
				mf.remotepath = mAttach.attachurl;
				if (taskType.equalsIgnoreCase(INetworkTask.TASK_TYPE_CACHE)) {
//						Log.d("==WR==", "audio attachuuid = " + aAttach[i].attachuuid);
					mf.localpath = getLocalpath(mAttach.attachurl);
					// 当合并账号时，可能原任务是正常上传任务，映射关系已改变
					if (mf.localpath == null) {
						mf.localpath = getLocalpath(mAttach.attachuuid);
					}
				} else {
					mf.localpath = getLocalpath(mAttach.attachurl);
				}
				if (GsonProtocol.ATTACH_TYPE_VIDEO.equals(upmedia.attachtype)) { // 视频
					// 若为视频日记，提取封面先
					//TODO:
					if(needUploadVideoCover()) {
						addVideoCoverAttach(att, taskType);
					}
					switch (new Mp4InfoUtils(mf.localpath).angle) {
					case EffectType.MY_PI_ZERO:
						upmedia.rotation = 0;
						break;
					case EffectType.MY_PI_1P2:
						upmedia.rotation = 90;
						break;
					case EffectType.MY_PI_:
						upmedia.rotation = 180;
						break;
					case EffectType.MY_PI_3P2:
						upmedia.rotation = 270;
						break;
					default:
						upmedia.rotation = 0;
						break;
					}
					upmedia.filetype = "1";
				} else if (GsonProtocol.ATTACH_TYPE_AUDIO.equals(upmedia.attachtype)) {// 音频
					upmedia.filetype = "3";
				} else if (GsonProtocol.ATTACH_TYPE_PICTURE.equals(upmedia.attachtype)) {// 图片
					upmedia.filetype = "4";
					mf.remotepath = mAttach.attachurl;
					mf.localpath = getLocalpath(mAttach.attachurl);
				} else if (GsonProtocol.ATTACH_TYPE_VOICE.equals(upmedia.attachtype)
						|| GsonProtocol.ATTACH_TYPE_VOICE_TEXT.equals(upmedia.attachtype)) {//短语音
					upmedia.filetype = "5";
				}
					
				if (!GsonProtocol.ATTACH_TYPE_TEXT.equals(upmedia.attachtype) && mf.localpath != null) {
					upmedia.attachMedia = new ArrayList<MediaFile>();
					upmedia.attachMedia.add(mf);
	//				Log.d("==WR==", "localpath = " + mf.localpath);
					File file = new File(mf.localpath);
					if (file.exists()) {
						this.totalTaskSize += file.length();
						upmedia.uptotalSize = file.length();
					}
					if (taskType.equalsIgnoreCase(INetworkTask.TASK_TYPE_CACHE)) {
						caMedias.add(upmedia);
					} else {
						upMedias.add(upmedia);
					}
				} else {
					Log.e(TAG, "附件为文字类型，或者附件未找到路径 + attachType = " + upmedia.attachtype + " localpath = " + mf.localpath);
				}
				
			} else {
				// 下载日记
				downMedia downmedia = new downMedia();
				MediaFile mf = new MediaFile();
				downmedia.attachid   = mAttach.attachid;
				downmedia.attachtype = mAttach.attachtype;//1视频、2音频、3图片、4文字、5短录音、6文字+短录音
				
				mf.remotepath = mAttach.attachurl;
				mf.filesize = mAttach.attachsize;
				this.totalTaskSize += Long.parseLong((mf.filesize == null || mf.filesize.equals("")) ? "0" : mf.filesize);
				
				if (GsonProtocol.ATTACH_TYPE_VIDEO.equals(downmedia.attachtype)) { // 视频
					mf.localpath = getLocalStoragePath(mf.remotepath, "video"); // /sdcard/looklook/uid/video/key
				} else if (GsonProtocol.ATTACH_TYPE_AUDIO.equals(downmedia.attachtype)) {// 音频
					mf.localpath = getLocalStoragePath(mf.remotepath, "audio");
				} else if (GsonProtocol.ATTACH_TYPE_PICTURE.equals(downmedia.attachtype)) {// 图片
					mf.remotepath = mAttach.attachurl;
					mf.localpath = getLocalStoragePath(mf.remotepath, "pic"); // /sdcard/looklook/uid/pic/key
				}
				
				if(!GsonProtocol.ATTACH_TYPE_TEXT.equals(downmedia.attachtype) && mf.localpath != null) {
					MediaValue tmpMV = AccountInfo.getInstance(userid).mediamapping.getMedia(userid, mf.remotepath);
					//未登记过，文件大小尚未登记
					if (tmpMV == null || !MediaValue.checkMediaAvailable(tmpMV)) {
						Log.d("==WR==", "Put in MediaMapping");
						tmpMV = new MediaValue();
						tmpMV.UID = userid;
						tmpMV.localpath = mf.localpath.replace(Environment.getExternalStorageDirectory().getPath(), "");
						tmpMV.url = mf.remotepath;
						tmpMV.MediaType = ConvertType(downmedia.attachtype);//文件类型  0： 未定义 ，1： 文本， 2：图片  3： 短音频， 4：长音频， 5：视频
						tmpMV.Direction = 1;
						Log.d(TAG, "mappingKey " + tmpMV.url + " path = " + tmpMV.localpath);
						AccountInfo.getInstance(userid).mediamapping.setMedia(userid, tmpMV.url, tmpMV);
						
						downmedia.attachMedia = new MediaFile();
						downmedia.attachMedia = mf;
						downMedias.add(downmedia);
					//已经在本地存在,不再重复下载
					} else {
						Log.e("TAG", "已经在本地存在,不再重复下载");
//						continue;
					}
				} else {
					Log.e("TAG", "附件为文字类型，或者附件未找到路径 + attachType = " + downmedia.attachtype + " localpath = " + mf.localpath);
				}
				
			}
		}
		//遍历提取辅内容
		if(aAttach != null) {
			for(int i=0;i<aAttach.length;i++) {
				if(taskType.equalsIgnoreCase(INetworkTask.TASK_TYPE_UPLOAD) ||
						taskType.equalsIgnoreCase(INetworkTask.TASK_TYPE_CACHE)){
					//上传日记
					upMedia upmedia = new upMedia();
					MediaFile mf = new MediaFile();
					upmedia.attachid   = aAttach[i].attachid;
					upmedia.attachuuid = aAttach[i].attachuuid;
					upmedia.attachtype = aAttach[i].attachtype;
					upmedia.businesstype = "1"; //日记业务
					mf.remotepath = aAttach[i].attachurl;
					if (taskType.equalsIgnoreCase(INetworkTask.TASK_TYPE_CACHE)) {
//							Log.d("==WR==", "audio attachuuid = " + aAttach[i].attachuuid);
						mf.localpath = getLocalpath(aAttach[i].attachuuid);
						// 当合并账号时，可能原任务是正常上传任务，映射关系已改变
						if (mf.localpath == null) {
							mf.localpath = getLocalpath(aAttach[i].attachurl);
						}
					} else {
						mf.localpath = getLocalpath(aAttach[i].attachurl);
					}
					if (GsonProtocol.ATTACH_TYPE_VIDEO.equals(upmedia.attachtype)) { // 视频
						switch (new Mp4InfoUtils(mf.localpath).angle) {
						case EffectType.MY_PI_ZERO:
							upmedia.rotation = 0;
							break;
						case EffectType.MY_PI_1P2:
							upmedia.rotation = 90;
							break;
						case EffectType.MY_PI_:
							upmedia.rotation = 180;
							break;
						case EffectType.MY_PI_3P2:
							upmedia.rotation = 270;
							break;
						default:
							upmedia.rotation = 0;
							break;
						}
						upmedia.filetype = "1";
					} else if (GsonProtocol.ATTACH_TYPE_AUDIO.equals(upmedia.attachtype)) {// 音频
						upmedia.filetype = "3";
					} else if (GsonProtocol.ATTACH_TYPE_PICTURE.equals(upmedia.attachtype)) {// 图片
						upmedia.filetype = "4";
					} else if (GsonProtocol.ATTACH_TYPE_VOICE.equals(upmedia.attachtype)
							|| GsonProtocol.ATTACH_TYPE_VOICE_TEXT.equals(upmedia.attachtype)) {//短语音
						upmedia.filetype = "5";
					}
					if (!GsonProtocol.ATTACH_TYPE_TEXT.equals(upmedia.attachtype) && mf.localpath != null) {
						upmedia.attachMedia = new ArrayList<MediaFile>();
						upmedia.attachMedia.add(mf);
		//				Log.d("==WR==", "localpath = " + mf.localpath);
						File file = new File(mf.localpath);
						if (file.exists()) {
							this.totalTaskSize += file.length();
							upmedia.uptotalSize = file.length();
						}
						if (taskType.equalsIgnoreCase(INetworkTask.TASK_TYPE_CACHE)) {
							caMedias.add(upmedia);
						} else {
							upMedias.add(upmedia);
						}
					} else {
						Log.e("TAG", "附件为文字类型，或者附件未找到路径 + attachType = " + upmedia.attachtype + " localpath = " + mf.localpath);
					}
					
				} else {
					//下载日记
					// e.g. /sdcard/looklook/uid/shortaudio/key
					downMedia downmedia = new downMedia();
					MediaFile mf = new MediaFile();
					downmedia.attachid   = aAttach[i].attachid;
					downmedia.attachtype = aAttach[i].attachtype;
					mf.remotepath = aAttach[i].attachurl;
					mf.filesize = aAttach[i].attachsize;
					this.totalTaskSize += Long.parseLong((mf.filesize == null || mf.filesize.equals("")) ? "0" : mf.filesize);
					
					if (GsonProtocol.ATTACH_TYPE_VIDEO.equals(downmedia.attachtype)) { // 视频
						mf.localpath = getLocalStoragePath(mf.remotepath, "video"); // /sdcard/looklook/uid/video/key
					} else if (GsonProtocol.ATTACH_TYPE_AUDIO.equals(downmedia.attachtype)) {// 音频
						mf.localpath = getLocalStoragePath(mf.remotepath, "audio");
					} else if (GsonProtocol.ATTACH_TYPE_PICTURE.equals(downmedia.attachtype)) {// 图片
						mf.remotepath = mAttach.attachurl;
						mf.localpath = getLocalStoragePath(mf.remotepath, "pic"); // /sdcard/looklook/uid/pic/key
					}
					
					if(!GsonProtocol.ATTACH_TYPE_TEXT.equals(downmedia.attachtype) && mf.localpath != null) {
						MediaValue tmpMV = AccountInfo.getInstance(userid).mediamapping.getMedia(userid, mf.remotepath);
						//未登记过，文件大小尚未登记
						if (tmpMV == null || !MediaValue.checkMediaAvailable(tmpMV)) {
							Log.d("==WR==", "Put in MediaMapping");
							tmpMV = new MediaValue();
							tmpMV.UID = userid;
							tmpMV.localpath = mf.localpath.replace(Environment.getExternalStorageDirectory().getPath(), "");
							tmpMV.url = mf.remotepath;
							tmpMV.MediaType = ConvertType(downmedia.attachtype);//文件类型  0： 未定义 ，1： 文本， 2：图片  3： 短音频， 4：长音频， 5：视频
							tmpMV.Direction = 1;
							Log.d(TAG, "mappingKey " + tmpMV.url + " path = " + tmpMV.localpath);
							AccountInfo.getInstance(userid).mediamapping.setMedia(userid, tmpMV.url, tmpMV);
						//已经在本地存在,不再重复下载
						} else {
							continue;
						}
						downmedia.attachMedia = new MediaFile();
						downmedia.attachMedia = mf;
						downMedias.add(downmedia);
					} else {
						Log.e("TAG", "附件为文字类型，或者附件未找到路径 + attachType = " + downmedia.attachtype + " localpath = " + mf.localpath);
					}
				}
			}
		}
	}
	
	
	private boolean needUploadVideoCover() {
		boolean ret = true;
		if (upRequest != null && upRequest.attachs != null) {
			for (Attachs attach : upRequest.attachs) {
				if (attach.attach_type.equals(GsonProtocol.ATTACH_TYPE_VIDEO)) {
					return true;
				}
			}
			ret = false;
		}
		return ret;
	}
	
	//=============从私信、评论创建=====================
	/*
	 *@param userid:       用户id
	 *@param diaryid:      日记id
	 *@param attachid:     附件id
	 *@param localpath:    本地文件路径
	 *@param remotepath:   服务器存放路径
	 *@param filetype:     文件类型 ： 1 普清视频 2 高清视频 3 录音 4 图片 5语音描述
	 *@param businesstype: 业务类型 ： 1 日记  2 评论  3 私信  4头像 5用户空间封面 6日记封面
	 */
	public NetworkTaskInfo(String userid, String diaryid, String attachid,
			String localpath, String remotepath, 
			String filetype, String businesstype){
		this(userid, diaryid, attachid, localpath, remotepath, 
				filetype, businesstype, null);
		
	}
	
	//=============从私信、评论创建=====================
	/*
	 *@param userid:       用户id
	 *@param diaryid:      日记id
	 *@param attachid:     附件id
	 *@param localpath:    本地文件路径
	 *@param remotepath:   服务器存放路径
	 *@param filetype:     文件类型 ： 1 普清视频 2 高清视频 3 录音 4 图片 5语音描述
	 *@param businesstype: 业务类型 ： 1 日记  2 评论  3 私信  4头像 5用户空间封面 6日记封面
	 */
	public NetworkTaskInfo(String userid, String diaryid, String attachid,
			String localpath, String remotepath, 
			String filetype, String businesstype, String audioID) { //仅上传
		Log.e(TAG, "userid:" + userid + ", diaryid:" + diaryid + ", attachid:" + 
			attachid + ", localpath:" + localpath + ", remotepath:" + remotepath +  
			"filetype:" + filetype + ", businesstype:" + businesstype);
		this.userid = userid;
		this.diaryid = diaryid;
		this.taskType = INetworkTask.TASK_TYPE_UPLOAD;
		this.upMedias = new ArrayList<upMedia>();
		this.totalTaskSize = 0;
		this.curTaskSize = 0;
		this.otherAttachid = attachid;
		this.audioID = audioID;
		upMedia upmedia = new upMedia();
		upmedia.attachid = attachid;
		upmedia.attachtype = Convert2AttachType(filetype);
		upmedia.filetype = filetype;
		upmedia.businesstype = businesstype;
		
		MediaFile mf = new MediaFile();
		mf.localpath = localpath;
		mf.remotepath = remotepath;

		upmedia.attachMedia = new ArrayList<MediaFile>();
		upmedia.attachMedia.add(mf);
		File file = new File(mf.localpath);
		if (file.exists()) {
			this.totalTaskSize += file.length();
			upmedia.uptotalSize = file.length();
		}
		upMedias.add(upmedia);
	}

	private String Convert2AttachType(String filetype) {
		//filetype:     文件类型 ： 1 普清视频 2 高清视频 3 录音 4 图片 5语音描述
		//attachtype:   附件类型 ： 1视频、2音频、3图片、4文字
		String attachtype = null;
		int inttype = Integer.parseInt(filetype);
		if(filetype != null) {
			switch (inttype) {
			case 1:
			case 2:
				attachtype = "1";
				break;
			case 3:
				attachtype = "2";
				break;
			case 4:
				attachtype = "3";
				break;
			case 5:
				attachtype = "4";
				break;
			default:
				break;
			}
		}
		return attachtype;
	}
	
	//=============设置上传补充信息=====================
	public void setUpExtraInfo() {
		//TODO:
	}
	
	//=============设置下载补充信息=====================
	public void setDownExtraInfo() {
		//TODO:
	}
	
	// 将封面作为图片附件添加到上传文件结构
	private void addVideoCoverAttach(DiaryAttach attach, String taskType) {
    	if(attach != null && attach.levelattach != null) {
    		MainAttach mAttach = attach.levelattach;
    		if(mAttach != null) {
				if (GsonProtocol.ATTACH_TYPE_VIDEO.equals(mAttach.attachtype)
						&& this.taskCover != null
						&& !this.taskCover.equals("")) {
					upMedia upmedia = new upMedia();
					MediaFile mf = new MediaFile();
					upmedia.attachid = mAttach.attachid;
					upmedia.attachuuid = mAttach.attachuuid;
					upmedia.attachtype = mAttach.attachtype;
					upmedia.businesstype = "6"; // 日记封面
					upmedia.filetype = "4";
					mf.localpath = getLocalpath(this.taskCover);
					mf.remotepath = getVideoCoverUpPath();
					upmedia.attachMedia = new ArrayList<MediaFile>();
					upmedia.attachMedia.add(mf);
	//				Log.d("==WR==", "localpath = " + mf.localpath);
					if (mf.localpath != null) {
						File file = new File(mf.localpath);
						if (file.exists()) {
							this.totalTaskSize += file.length();
							upmedia.uptotalSize = file.length();

							if (taskType.equalsIgnoreCase(INetworkTask.TASK_TYPE_CACHE)) {
								caMedias.add(upmedia);
							} else {
								upMedias.add(upmedia);
							}
						}
					} else {
						Log.e("==WR==", "localpath = " + mf.localpath + ";获取封面映射失败！");
					}
				}
    		}
    	}
	}
	
	private String getLocalpath(String remotepath) {
		String ret = null;
		MediaValue mv = AccountInfo.getInstance(userid).mediamapping.getMedia(userid, remotepath);
		if(mv != null && mv.localpath != null && !mv.localpath.equals("")) {
			ret = Environment.getExternalStorageDirectory() + mv.localpath;
		}
//		Log.d("==WR==", "remotepath = " + remotepath + "getLocalpath = " + ret);
		return ret;
	}
	
	/**
	 * 当businesstype=6 即视频封面时，路径类似：
	 * cover/201307/25/0942370917379243459f9a6cf80f016dcc1d.jpg
	 * 其中cover部分固定不变，201307是“年和月”，25是“日”，
	 * 0942370917379243459f9a6cf80f016dcc1d.jpg是“时分”+不带“-”的uuid
	 * 
	 * @param uuid
	 * @return
	 */
	private String getVideoCoverUpPath() {
		String ret = null;
		String uuid = UUID.randomUUID().toString().replace("-", "");
		if (uuid != null && !"".equals(uuid)) {
			Calendar ca = Calendar.getInstance();
			int year = ca.get(Calendar.YEAR);// 获取年份
			int month = ca.get(Calendar.MONTH);// 获取月份
			int day = ca.get(Calendar.DATE);// 获取日
			int minute = ca.get(Calendar.MINUTE);// 分
			int hour = ca.get(Calendar.HOUR);// 小时

			String Prefix  = "cover_";
			String YMPart  = year + String.format("%02d", month) + "_";
			String DayPart = String.format("%02d", day) + "_";
			String NamePart= String.format("%02d", hour)
					+ String.format("%02d", minute) + uuid;
			String Suffix  = ".jpg";
			ret = Prefix + YMPart + DayPart + NamePart + Suffix;
			Log.d("==WR==", "视频封面上传服务器地址:" + ret);
		}
		return ret;
	}
	
	private String getLocalStoragePath(String remotepath, String type) {
		String ret = null;
		String path = Constant.SD_STORAGE_ROOT + "/" + userid + "/" + type;
		String Key = MD5.encode((userid + remotepath).getBytes());
		if (remotepath == null || remotepath.equals("") || type == null
				|| type.equals("") || userid == null | userid.equals("")) {
			return ret;
		}
		
		String lpath = getLocalpath(remotepath);
		//已找到以前下载任务登记的path
		Log.d("==WR==", "prefix (" + Environment.getExternalStorageDirectory() + path + ")");
		if (lpath != null && lpath.startsWith(Environment.getExternalStorageDirectory() + path)) {
			Log.d("==WR==", "local path found (" + lpath + ")");
			return lpath;
		}
		ret = Environment.getExternalStorageDirectory() + path + "/" + Key;
		Log.d("==WR==", "local path = " + ret);
		return ret;
	}
	
	//文件类型  0： 未定义 ，1： 文本， 2：图片  3： 短音频， 4：长音频， 5：视频
	//附件类型，1视频、2音频、3图片、4文字、5短录音、6文字+短录音
	private int ConvertType(String attachtype) {
		int ret = 0;
		if(GsonProtocol.ATTACH_TYPE_VIDEO.equals(attachtype)) {
			ret = 5;
		} else if(GsonProtocol.ATTACH_TYPE_AUDIO.equals(attachtype)) {
			ret = 4;
		} else if(GsonProtocol.ATTACH_TYPE_VOICE.equals(attachtype)
				|| GsonProtocol.ATTACH_TYPE_VOICE_TEXT.equals(attachtype)) {
			ret = 3;
		} else if(GsonProtocol.ATTACH_TYPE_PICTURE.equals(attachtype)) {
			ret = 2;
		} else if(GsonProtocol.ATTACH_TYPE_TEXT.equals(attachtype)) {
			ret = 1;
		}
		return ret;
	}
	
	//附件类型，1视频、2音频、3图片、4文字
    private String getTaskUrl(DiaryAttach attach) {
    	if(attach == null || attach.levelattach == null) {
    		return null;
    	}
		String url = null;
		MainAttach mAttach = attach.levelattach;
		try {
			this.diaryType = Integer.parseInt(mAttach.attachtype);//主附件类型，1视频、2音频、3图片、4文字
		} catch (Exception e) {
			this.diaryType = 0;//未定义
		}
		if(GsonProtocol.ATTACH_TYPE_VIDEO.equals(mAttach.attachtype)) {
			url = attach.videocover;
		} else if(GsonProtocol.ATTACH_TYPE_PICTURE.equals(mAttach.attachtype)) {
			url = mAttach.attachurl;
		}
		ZLog.printObject(attach);
		Log.d("==WR==", "提取封面 : " + url);
    	return url;
    }

    public void clearDownloadData() {
    	if(this.downMedias != null && this.downMedias.size() > 0) {
    		for (downMedia dm : this.downMedias) {
    			MediaFile mf = dm.attachMedia;
    			//clear download data
    			if(mf.localpath == null || mf.localpath.equals("")) {
    				continue;
    			}
    			File file = new File(mf.localpath);
    			if(file.exists()) {
    				Log.d(TAG, "file(" + file.getAbsolutePath() + ") deleted.");
    				file.delete();
    				if(file.getParentFile() != null) {
    					file.getParentFile().delete();//若为空文件夹，删除；非空，保留
    				}
    				//去除登记
    				if(AccountInfo.getInstance(userid).mediamapping.getMedia(userid, mf.remotepath) != null) {
        				AccountInfo.getInstance(userid).mediamapping.delMedia(userid, mf.remotepath);
    				}
    			}
    		}
    	}
    }
    
    // 合并账号中未上传的任务，全部置为离线任务以重新创建日记结构
	public void mergeNetworkTaskInfo(AccountInfo oldAI, AccountInfo newAI, String newuid) {
		if (ZStringUtils.emptyToNull(newuid) != null) {
			this.userid = newuid;
			if (this.upRequest != null
					&& ZStringUtils.emptyToNull(this.upRequest.userid) != null) {
				this.upRequest.userid = newuid;
			}
			this.uploadedLengthUI = 0;
			this.percentValueUI = 0;
			
			try {
				//替换新的附件物理路径
				//1.taskCover
				if(this.taskCover != null && !this.taskCover.equals("")) {
					String newLocalpath = this.taskCover.replace(
							oldAI.userid, newAI.userid);
					// 替换文件名
					String oldFileName = MD5
							.encode((oldAI.userid + this.taskCover).getBytes());
					String newFileName = MD5
							.encode((newAI.userid + this.taskCover).getBytes());
					newLocalpath = newLocalpath.replace(oldFileName,
							newFileName);
					// 移动文件
					MediaMapping.renameFile(this.taskCover, newLocalpath);
					this.taskCover = newLocalpath;
				}
				//2.caMedias
				if(this.caMedias != null) {
					for(upMedia tmp : caMedias) {
						if(tmp.attachMedia != null) {
							for(MediaFile mf : tmp.attachMedia) {
								if (!TextUtils.isEmpty(mf.remotepath)) {
									MyDiary.relpaceMediaMapping(mf.remotepath, oldAI, newAI);
									MediaValue mv = oldAI.mediamapping.getMedia(newAI.userid, mf.remotepath);
									if (mv != null) {
										// mv.localpath:/.looklook/ba770ab30aa2304fad08b3c0a583affbba60/video/14d91c7fb65d410aa86094707a34ab69.mp4
										mf.localpath = LookLookActivity.SDCARD_PATH + mv.localpath;
									// 封面文件
									} else if(mf.localpath.endsWith(".vc")) {
										String newLocalpath = mf.localpath.replace(oldAI.userid, newAI.userid);
										MediaMapping.renameFile(mf.localpath, newLocalpath);
										mf.localpath = newLocalpath;
									}
								}
							}
						}
					}
				}
				//3.upMedias
				if(this.upMedias != null) {
					for(upMedia tmp : upMedias) {
						if(tmp.attachMedia != null) {
							for(MediaFile mf : tmp.attachMedia) {
								if (!TextUtils.isEmpty(mf.remotepath)) {
									MyDiary.relpaceMediaMapping(mf.remotepath, oldAI, newAI);
									MediaValue mv = oldAI.mediamapping.getMedia(newAI.userid, mf.remotepath);
									if (mv != null) {
										// mv.localpath:/.looklook/ba770ab30aa2304fad08b3c0a583affbba60/video/14d91c7fb65d410aa86094707a34ab69.mp4
										mf.localpath = LookLookActivity.SDCARD_PATH + mv.localpath;
									// 封面文件
									} else if(mf.localpath.endsWith(".vc")) {
										String newLocalpath = mf.localpath.replace(oldAI.userid, newAI.userid);
										MediaMapping.renameFile(mf.localpath, newLocalpath);
										mf.localpath = newLocalpath;
									}
								}
							}
						}
					}
				}
				//4.downMedias
//				if (this.downMedias != null) {
//					for (downMedia tmp : downMedias) {
//						if (tmp.attachMedia != null) {
//							MediaFile mf = tmp.attachMedia;
//							if (mf != null && !TextUtils.isEmpty(mf.remotepath)) {
//								MyDiary.relpaceMediaMapping(mf.remotepath, oldAI, newAI);
//								MediaValue mv = oldAI.mediamapping.getMedia(newAI.userid, mf.remotepath);
//								if (mv != null) {
//									mf.localpath = mv.localpath;
//								}
//							}
//						}
//					}
//				}
			}catch(IOException e) {
				e.printStackTrace();
			}
		}
	}
}