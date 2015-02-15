package com.cmmobi.looklook.info.profile;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.os.Environment;
import android.util.Log;
import cn.zipper.framwork.core.ZApplication;
import cn.zipper.framwork.core.ZLog;

import com.cmmobi.looklook.common.constant.Constant;
import com.cmmobi.looklook.common.gson.GsonRequest2.createStructureRequest;
import com.cmmobi.looklook.common.gson.GsonResponse2.MyAttachAudio;
import com.cmmobi.looklook.common.gson.GsonResponse2.MyAttachImage;
import com.cmmobi.looklook.common.gson.GsonResponse2.MyDiary;
import com.cmmobi.looklook.common.gson.GsonResponse2.diaryAttach;
import com.cmmobi.looklook.common.listview.pulltorefresh.DiaryListView;
import com.cmmobi.looklook.common.utils.MD5;
import com.cmmobi.looklook.networktask.INetworkTask;
import com.cmmobivideo.utils.Mp4InfoUtils;
import com.cmmobivideo.utils.XUtils;

import effect.EffectType;

/**
 * 需要持久化的信息 
 *
 */
public final class NetworkTaskInfo {
	private transient static final String TAG = "NetworkTaskInfo";
	//=============共同字段列在此处========================
	public String taskid;
	public String taskType;  //任务类型：上传/下载/离线
    public String taskUrl;   //任务封面图片
    public String coverid;   //任务封面对应的视频附件attachid
    public boolean coveruploaded; //任务封面已上传完
    public int diaryType;    //对应日记任务类型，主文件类型  0： 未定义 ，1： 文本， 2：图片  3： 短音频， 4：长音频， 5：视频
	public String diaryid;   //任务对应操作的日记id
	public String diaryuuid;
	public String nickname;
    public String userid;
    public String diaryuserid;
    public String audioID; //私信语音专用

    //专为非日记类型任务预留
    public String otherAttachid; //附件id
    
	public int state; //任务当前状态
	public boolean isPrepareDone;
	public boolean isPriority = false; //是否优先
	
	public ArrayList<upMedia>   upMedias;
	public ArrayList<upMedia>   caMedias;
	public ArrayList<downMedia> downMedias;
	
	public int finishedFileIndex = -1; // 已经完成过的文件编号;
	public long totalTaskSize; //任务含媒体文件总大小
	public long curTaskSize;   //当前已完成(上传/下载)媒体文件大小
	
	public float percentValue;
	
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
	public long uploadedLength;
	public long uploadedFileBlock;
	public boolean isPublish = false; //上传完成后是否发布
	
	public static class upMedia {
		public String attachid;//根据businesstype的不同对于不同表中的id
		public String attachuuid;//根据businesstype的不同对于不同表中的id
		public String attachtype; //附件类型，1视频、2音频、3图片、4文字
		public ArrayList<MediaFile> attachMedia;
		public String type = "1"; //1 单文件上传 n 边拍边传
		public boolean sliceCompleted = false;//分段是否结束，type=n时有效
		public int rotation;//视频旋转角度
		public String filetype;//1 普清视频 2 高清视频 3 录音 4 图片 5语音描述
		public String businesstype;//1 日记 2 评论3 私信 4 陌生人消息
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
		public String attachtype; //附件类型，1视频、2音频、3图片、4文字
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
				copyAttach(d.attachs, taskType);
				int tmptype = DiaryListView.getDiaryType(d.attachs);// 获取日记类型
				this.taskUrl = getTaskUrl(d.attachs, tmptype);
				this.coveruploaded= false; 
			}
			curTaskSize = 0;
			//totalTaskSize = 0;
//			if(taskType.equalsIgnoreCase(INetworkTask.TASK_TYPE_DOWNLOAD)) {
//				if(d.size == null || d.size.equals("")) d.size = "0";
//				this.totalTaskSize = Integer.parseInt(d.size);
//			}
		}
	}
	
	private void copyAttach(diaryAttach[] att, String taskType) {
		for(int i=0;i<att.length;i++) {
			if(taskType.equalsIgnoreCase(INetworkTask.TASK_TYPE_UPLOAD) ||
					taskType.equalsIgnoreCase(INetworkTask.TASK_TYPE_CACHE)){
				//上传日记
				upMedia upmedia = new upMedia();
				MediaFile mf = new MediaFile();
				upmedia.attachid   = att[i].attachid;
				upmedia.attachuuid = att[i].attachuuid;
				upmedia.attachtype = att[i].attachtype;
				upmedia.businesstype = "1"; //日记业务
				if ("1".equals(upmedia.attachtype)) { // 视频
					if (att[i].attachvideo != null) { // 只存在一个文件，为扩展使用list
						mf.remotepath = att[i].attachvideo[0].playvideourl;
						if (taskType.equalsIgnoreCase(INetworkTask.TASK_TYPE_CACHE)) {
//							Log.d("==WR==", "video attachuuid = " + att[i].attachuuid);
							mf.localpath = getLocalpath(att[i].attachuuid);
						} else {
							mf.localpath = getLocalpath(att[i].attachvideo[0].playvideourl);
						}
						mf.type = att[i].attachvideo[0].videotype;
						if(mf.type != null) {
							if(mf.type.equals("1")){
								upmedia.filetype = "1";//普清
							} else {
								upmedia.filetype = "2";//高清
							}
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
					}
				} else if ("2".equals(upmedia.attachtype)) {// 音频
					if (att[i].attachaudio != null) {
						mf.remotepath = att[i].attachaudio[0].audiourl;
						if (taskType.equalsIgnoreCase(INetworkTask.TASK_TYPE_CACHE)) {
//							Log.d("==WR==", "audio attachuuid = " + att[i].attachuuid);
							mf.localpath = getLocalpath(att[i].attachuuid);
						} else {
							mf.localpath = getLocalpath(att[i].attachaudio[0].audiourl);
						}
						mf.type = att[i].attachaudio[0].audiotype;
						upmedia.filetype = "3";
					}
				} else if ("3".equals(upmedia.attachtype)) {// 图片
					if (att[i].attachimage != null) {
						mf.remotepath = att[i].attachimage[0].imageurl;
						if (taskType.equalsIgnoreCase(INetworkTask.TASK_TYPE_CACHE)) {
//							Log.d("==WR==", "picture attachuuid = " + att[i].attachuuid);
							mf.localpath = getLocalpath(att[i].attachuuid);
						} else {
							mf.localpath = getLocalpath(att[i].attachimage[0].imageurl);
						}
						mf.type = att[i].attachimage[0].imagetype;
						upmedia.filetype = "4";
					}
				}
				if (!"4".equals(upmedia.attachtype) && mf.localpath != null) {
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
				downmedia.attachid   = att[i].attachid;
				downmedia.attachtype = att[i].attachtype;
				downmedia.attachlevel = att[i].attachlevel;
				if ("1".equals(downmedia.attachtype)) { // 视频
					if (att[i].attachvideo != null) { // 只存在一个文件，为扩展使用list
						mf.remotepath = att[i].attachvideo[0].playvideourl;
						mf.type = att[i].attachvideo[0].videotype;
						mf.localpath = getLocalStoragePath(mf.remotepath, "video"); ///sdcard/looklook/uid/video/key
						mf.filesize = att[i].attachvideo[0].videosize;
						this.totalTaskSize += Long.parseLong((mf.filesize == null || mf.filesize.equals("")) ? "0" : mf.filesize);
					}

				} else if ("2".equals(downmedia.attachtype)) {// 音频
					if (att[i].attachaudio != null) {
						mf.remotepath = att[i].attachaudio[0].audiourl;
						if(att[i].attachlevel.equals("1"))
						{// 长音频
							mf.localpath = getLocalStoragePath(mf.remotepath, "audio");
						}
						else
						{// 短音频
							mf.localpath = getLocalStoragePath(mf.remotepath, "shortaudio"); ///sdcard/looklook/uid/shortaudio/key
						}
						mf.type = att[i].attachaudio[0].audiotype;
						mf.filesize = att[i].attachaudio[0].audiosize;
						this.totalTaskSize += Long.parseLong((mf.filesize == null || mf.filesize.equals("")) ? "0" : mf.filesize);
					}
				} else if ("3".equals(downmedia.attachtype)) {// 图片
					if (att[i].attachimage != null) {
						MyAttachImage imageattach = att[i].attachimage[0];
						if (userid.equals(diaryuserid)) {
							for (MyAttachImage imageAttach:att[i].attachimage) {
								if ("0".equals(imageAttach.imagetype)) {
									imageattach = imageAttach;
									break;
								}
							}
						} else {
							for (MyAttachImage imageAttach:att[i].attachimage) {
								if ("1".equals(imageAttach.imagetype)) {
									imageattach = imageAttach;
									break;
								}
							}
						}
						mf.remotepath = imageattach.imageurl;
						mf.type = imageattach.imagetype;
						mf.localpath = getLocalStoragePath(mf.remotepath, "pic"); ///sdcard/looklook/uid/pic/key
						mf.filesize = imageattach.imagesize;
						this.totalTaskSize += Long.parseLong((mf.filesize == null || mf.filesize.equals("")) ? "0" : mf.filesize);
					}
				}
				if(!"4".equals(downmedia.attachtype) && mf.localpath != null) {
					MediaValue tmpMV = AccountInfo.getInstance(userid).mediamapping.getMedia(userid, mf.remotepath);
					//未登记过，文件大小尚未登记
					if (tmpMV == null || !MediaValue.checkMediaAvailable(tmpMV)) {
						Log.d("==WR==", "Put in MediaMapping");
						tmpMV = new MediaValue();
						tmpMV.UID = userid;
						tmpMV.path = mf.localpath.replace(Environment.getExternalStorageDirectory().getPath(), "");
						tmpMV.url = mf.remotepath;
						tmpMV.MediaType = ConvertType(downmedia.attachtype, downmedia.attachlevel);//文件类型  0： 未定义 ，1： 文本， 2：图片  3： 短音频， 4：长音频， 5：视频
						tmpMV.Direction = 1;
						Log.d(TAG, "mappingKey " + tmpMV.url + " path = " + tmpMV.path);
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
	
	//=============从私信、评论创建=====================
	/*
	 *@param userid:       用户id
	 *@param diaryid:      日记id
	 *@param attachid:     附件id
	 *@param localpath:    本地文件路径
	 *@param remotepath:   服务器存放路径
	 *@param filetype:     文件类型 ： 1 普清视频 2 高清视频 3 录音 4 图片 5语音描述
	 *@param businesstype: 业务类型 ： 1 日记  2 评论  3 私信  4 陌生人消息
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
	 *@param businesstype: 业务类型 ： 1 日记  2 评论  3 私信  4 陌生人消息
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
	
	private String getLocalpath(String remotepath) {
		String ret = null;
		MediaValue mv = AccountInfo.getInstance(userid).mediamapping.getMedia(userid, remotepath);
		if(mv != null && mv.path != null && !mv.path.equals("")) {
			ret = Environment.getExternalStorageDirectory() + mv.path;
		}
//		Log.d("==WR==", "remotepath = " + remotepath + "getLocalpath = " + ret);
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
	//附件类型，1视频、2音频、3图片、4文字
	private int ConvertType(String attachtype, String attachlevel) {
		int ret = 0;
		if(attachtype.equals("1")) {
			ret = 5;
		} else if(attachtype.equals("2")) {
			if(attachlevel.equals("0"))
			{
				ret = 3;
			}
			else
			{
				ret = 4;
			}
		} else if(attachtype.equals("3")) {
			ret = 2;
		}
		return ret;
	}
	
	//文件类型  0： 未定义 ，1： 文本， 2：图片  3： 短音频， 4：长音频， 5：视频
    private String getTaskUrl(diaryAttach[] attachs, int type) {
		if(null==attachs||0==attachs.length){
			return null;
		}
		String url = null;
    	switch(type) {
		case 0x10000000://主体 视频
		case 0x10000100://主体 视频+辅 音频
		case 0x10000101://主体 视频+辅 音频+文字
		case 0x10000001://主体 视频+文字
			this.diaryType = 5;
			url = getCoverImage(1, attachs);
			break;
		case 0x1000000://主体 音频
		case 0x1000100://主体 音频+辅 音频
		case 0x1000101://主体 音频+辅 音频+文字
		case 0x1000001://主体音频+文字
		case 0x100://辅 音频
		case 0x101://辅 音频+文字
			this.diaryType = 4;
			break;
		case 0x100000://主体 图片
		case 0x100100://主体 图片+辅 音频
		case 0x100101://主体 图片+辅 音频+文字
		case 0x100001://主体 图片 +文字
			this.diaryType = 2;
			url = getCoverImage(3, attachs);
			break;
		case 0x10000://主体 文字
		case 0x10001://主体 文字+文字
		case 0x10100://主体 文字+辅 音频
		case 0x10101://主体 文字+辅 音频+文字
		case 0x1://辅 文字
			this.diaryType = 1;
			break;
		default:
			break;
		}
    	return url;
    }

    //1:视频 2:音频 3:图片
    private String getCoverImage(int type, diaryAttach[] attachs) {
    	List<diaryAttach> attachList=Arrays.asList(attachs);
    	String coverUrl = null;
    	diaryAttach attach = null;
    	for(int i = 0; i < attachList.size(); i++) {
    		attach=attachList.get(i);
    		
    		if (attach.attachimage != null && attach.attachimage.length > 0) {
    			coverUrl = attach.attachimage[0].imageurl;
    			break;
    		}
    		
    		if (attach.attachvideo != null) {
    			coverid = attach.attachid;
    			coverUrl = attach.videocover;
    			break;
    		}
    	}
    	return coverUrl;
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
}
