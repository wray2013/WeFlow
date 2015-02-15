package com.cmmobi.looklook.offlinetask;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.NetworkInfo.State;
import android.net.wifi.WifiManager;
import android.os.Parcelable;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;
import cn.zipper.framwork.core.ZApplication;
import cn.zipper.framwork.core.ZBroadcastReceiver;
import cn.zipper.framwork.device.ZSimCardInfo;
import cn.zipper.framwork.io.network.ZNetworkStateDetector;

import com.cmmobi.looklook.MainApplication;
import com.cmmobi.looklook.activity.FriendsSessionPrivateMessageActivity;
import com.cmmobi.looklook.common.gson.DiaryController;
import com.cmmobi.looklook.common.gson.GsonProtocol;
import com.cmmobi.looklook.common.gson.GsonRequest3;
import com.cmmobi.looklook.common.gson.GsonRequest3.SNS;
import com.cmmobi.looklook.common.gson.GsonRequest3.createMicRequest;
import com.cmmobi.looklook.common.gson.GsonRequest3.deleteDiaryRequest;
import com.cmmobi.looklook.common.gson.GsonRequest3.diaryPublishRequest;
import com.cmmobi.looklook.common.gson.GsonRequest3.diarySharePermissionsRequest;
import com.cmmobi.looklook.common.gson.GsonRequest3.mergerAccountRequest;
import com.cmmobi.looklook.common.gson.GsonRequest3.modTagsOrPositionRequest;
import com.cmmobi.looklook.common.gson.GsonRequest3.safeboxRequest;
import com.cmmobi.looklook.common.gson.GsonRequest3.sendmessageRequest;
import com.cmmobi.looklook.common.gson.GsonRequest3.shareDiaryRequest;
import com.cmmobi.looklook.common.gson.GsonResponse3.MyDiary;
import com.cmmobi.looklook.common.gson.GsonResponse3.MyDiaryList;
import com.cmmobi.looklook.common.gson.GsonResponse3.UserObj;
import com.cmmobi.looklook.common.gson.Requester3;
import com.cmmobi.looklook.info.profile.AccountInfo;
import com.cmmobi.looklook.info.profile.ActiveAccount;
import com.cmmobi.looklook.info.profile.DiaryManager;
import com.cmmobi.looklook.info.profile.PrivateMessageManager;
import com.cmmobi.looklook.info.profile.TimeHelper;
import com.cmmobi.looklook.info.profile.VshareDataEntities;
import com.cmmobi.looklook.networktask.INetworkTask;
import com.cmmobi.looklook.networktask.NetworkTaskManager;
import com.cmmobi.looklook.offlinetask.IOfflineTask.TaskListener;
import com.cmmobi.looklook.prompt.Prompt;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

/**
 * @author wuxiang
 * @email wuxiang@cmmobi.com
 * @date 2013-8-19
 */
public class OfflineTaskManager implements TaskListener {

	private static final String TAG = "OfflineTaskManager";
	private List<TaskData> taskList = Collections
			.synchronizedList(new LinkedList<TaskData>());
	private IOfflineTask runningTask;
	public static int MAX_RETRY_COUNT = 1;

	// 当Gson反序列request是，可能会转换出LinkedHashTreeMap类型
	private void updateTaskRequest() {
		for (int i = 0; i < taskList.size(); i++) {
			TaskData taskData = taskList.get(i);
			switch (taskData.taskType) {
			case SAFEBOX_ADD:
				taskData.request=new Gson().fromJson(taskData.requestStr, safeboxRequest.class);
				break;
			case SAFEBOX_REMOVE:
				taskData.request=new Gson().fromJson(taskData.requestStr, safeboxRequest.class);
				break;
			case DIARY_MEDIA_UPLOAD:
				taskData.request=new Gson().fromJson(taskData.requestStr, new TypeToken<ArrayList<FileUploadInfo>>(){}.getType());
				break;
			case PRIVATE_MSG_AUDIO_UPLOAD:
				taskData.request=new Gson().fromJson(taskData.requestStr, FileUploadInfo.class);
				break;
			case VIDEO_COVER_UPLOAD:
				taskData.request=new Gson().fromJson(taskData.requestStr, VideoCoverUploaderInfo.class);
				break;
			case SHARE_TRACE_UPLOAD:
				taskData.request=new Gson().fromJson(taskData.requestStr, shareDiaryRequest.class);
				break;
			case GET_DIARY_SHARE_URL:
				taskData.request=new Gson().fromJson(taskData.requestStr, ThirdPartyRequest.class);
				break;
			case SHARE_TO_RENREN:
				taskData.request=new Gson().fromJson(taskData.requestStr, ThirdPartyRequest.class);
				break;
			case SHARE_TO_TENCENT:
				taskData.request=new Gson().fromJson(taskData.requestStr, ThirdPartyRequest.class);
				break;
			case SHARE_TO_SINA:
				taskData.request=new Gson().fromJson(taskData.requestStr, ThirdPartyRequest.class);
				break;
			case SHARE_TO_LOOKLOOK:
				taskData.request=new Gson().fromJson(taskData.requestStr, diaryPublishRequest.class);
				break;
			case SET_DIARY_SHARE_PERMISSIONS:
				taskData.request=new Gson().fromJson(taskData.requestStr, diarySharePermissionsRequest.class);
				break;
			case V_SHARE:
				taskData.request=new Gson().fromJson(taskData.requestStr, createMicRequest.class);
				break;
			case SEND_PRIVATE_MSG:
				taskData.request=new Gson().fromJson(taskData.requestStr, sendmessageRequest.class);
				break;
			case MERGER_ACCOUNT:
				taskData.request=new Gson().fromJson(taskData.requestStr, mergerAccountRequest.class);
				break;
			case DIARY_REMOVE:
				taskData.request=new Gson().fromJson(taskData.requestStr, deleteDiaryRequest.class);
			case POSITION_AND_TAG:
				taskData.request=new Gson().fromJson(taskData.requestStr, modTagsOrPositionRequest.class);
				break;
			default:
				break;
			}
		}
	}

	/**
	 * 添加 加入保险箱 任务
	 */
	public void addSafeboxAddTask(String diaryids, String diaryuuid) {
		//Prompt.Alert("已加入保险箱任务队列");
		System.out.println("已加入保险箱任务队列");
		GsonRequest3.safeboxRequest request = new GsonRequest3.safeboxRequest();
		request.userid = ActiveAccount.getInstance(ZApplication.getInstance())
				.getLookLookID();
		request.diaryids = diaryids;
		request.diaryuuid = diaryuuid;
		request.type = "1";
		request.mac = Requester3.VALUE_MAC;
		request.imei = Requester3.VALUE_IMEI;
		TaskData taskData = new TaskData();
		taskData.taskType = TaskType.SAFEBOX_ADD;
		taskData.diaryuuid = diaryuuid;
		taskData.request = request;
		taskData.requestStr=new Gson().toJson(request);
		judgeContains(taskData);
		start(0);
	}

	/**
	 * 添加 移出保险箱 任务
	 */
	public void addSafeboxRemoveTask(String diaryids, String diaryuuids) {
//		Prompt.Alert("已加入移除保险箱任务队列");
		GsonRequest3.safeboxRequest request = new GsonRequest3.safeboxRequest();
		request.userid = ActiveAccount.getInstance(ZApplication.getInstance())
				.getLookLookID();
		request.diaryids = diaryids;
		request.diaryuuid = diaryuuids;
		request.type = "2";
		request.mac = Requester3.VALUE_MAC;
		request.imei = Requester3.VALUE_IMEI;
		TaskData taskData = new TaskData();
		taskData.taskType = TaskType.SAFEBOX_REMOVE;
		taskData.diaryuuid = diaryuuids;
		taskData.request = request;
		taskData.requestStr=new Gson().toJson(request);
		judgeContains(taskData);
		start(0);
	}

	/**
	 * 添加 日记文件上传 任务
	 */
	public void addDiaryMediaUploadTask(ArrayList<FileUploadInfo> uploadInfos) {
		TaskData taskData = new TaskData();
		taskData.taskType = TaskType.DIARY_MEDIA_UPLOAD;
		taskData.request = uploadInfos.toArray(new FileUploadInfo[uploadInfos
				.size()]);
		taskData.requestStr=new Gson().toJson(uploadInfos,new TypeToken<ArrayList<FileUploadInfo>>(){}.getType());
		judgeContains(taskData);
		start(0);
	}

	/**
	 * 添加 私信语音文件上传 任务
	 */
	protected void addPrivateMsgAudioUploadTask(String ip, int port,
			String localFilePath, String uploadPath, String privateMsgId,
			String uuid, String target_userids, String sourceID) {
		FileUploadInfo uploadInfo = new FileUploadInfo();
		uploadInfo.id = privateMsgId;
		uploadInfo.uuid = uuid;
		uploadInfo.ip = ip;
		uploadInfo.port = port;
		uploadInfo.localFilePath = localFilePath;
		uploadInfo.uploadPath = uploadPath;
		uploadInfo.target_userids = target_userids;
		TaskData taskData = new TaskData();
		taskData.taskType = TaskType.PRIVATE_MSG_AUDIO_UPLOAD;
		taskData.request = uploadInfo;
		taskData.requestStr=new Gson().toJson(uploadInfo);
		taskData.sourceID = sourceID;
		judgeContains(taskData);
		start(0);
	}

	public void addVideoCoverUploadTask(String filePath, String diaryid,
			String attachmentid, String diaryuuid) {
		VideoCoverUploaderInfo uploadInfo = new VideoCoverUploaderInfo();
		uploadInfo.mFilePath = filePath;
		uploadInfo.diaryid = diaryid;
		uploadInfo.attachmentid = attachmentid;
		TaskData taskData = new TaskData();
		taskData.taskType = TaskType.VIDEO_COVER_UPLOAD;
		taskData.request = uploadInfo;
		taskData.requestStr=new Gson().toJson(uploadInfo);
		taskData.diaryuuid = diaryuuid;
		judgeContains(taskData);
		start(0);
	}

	/**
	 * 添加 分享轨迹上传 任务
	 */
	protected void addShareTraceUploadTask(String diaryid, String publishid,
			String snscontent, SNS[] sns) {
		GsonRequest3.shareDiaryRequest request = new GsonRequest3.shareDiaryRequest();
		request.userid = ActiveAccount.getInstance(ZApplication.getInstance())
				.getLookLookID();
		request.diaryid = diaryid;
		request.publishid = publishid;
		request.snscontent = snscontent;
		request.sns = sns;
		request.snsdes = new Gson().toJson(sns);
		request.mac = Requester3.VALUE_MAC;
		request.imei = Requester3.VALUE_IMEI;
		TaskData taskData = new TaskData();
		taskData.taskType = TaskType.SHARE_TRACE_UPLOAD;
		taskData.request = request;
		taskData.requestStr=new Gson().toJson(request);
		judgeContains(taskData);
		start(0);
	}

	/**
	 * 添加 获取日记分享URL 任务
	 */
	/*
	 * public void addGetDiaryShareUrlTask(String shareContent,String
	 * positionInfo,String userList,String diaryid,String diaryuuid, String
	 * position, String longitude, String latitrde,String diaryUserID,int
	 * shareType){ Prompt.Alert("已加入日记分享任务队列"); ThirdPartyRequest request=new
	 * ThirdPartyRequest(); request.content=shareContent;
	 * request.positionInfo=positionInfo; request.userList=userList;
	 * request.diaryid=diaryid; request.shareType=shareType;
	 * request.position=position; request.diaryid=diaryid;
	 * request.longitude=longitude; request.latitrde=latitrde; TaskData
	 * taskData=new TaskData(); taskData.taskType=TaskType.GET_DIARY_SHARE_URL;
	 * taskData.diaryuuid=diaryuuid; taskData.diaryUserID=diaryUserID;
	 * taskData.request=request; judgeContains(taskData); start(0); }
	 */

	public void addGetDiaryShareUrlTask(String positionInfo, String userList,
			String diaryids, String diaryuuid, String content, int shareType,
			String weather, String weather_info, String longitude,
			String latitude, String position, String isForward, String publishid) {
//		Prompt.Alert("已加入日记分享任务队列");
		ThirdPartyRequest request = new ThirdPartyRequest();
		request.diaryid = diaryids;
		request.diaryuuid = diaryuuid;
		request.positionInfo = positionInfo;
		request.userList = userList;
		request.content = content;
		request.isForward = isForward;
		request.publishid = publishid;
		request.shareType = shareType;
		request.weather = weather;
		request.weather_info = weather_info;
		request.position = position;
		request.longitude = longitude;
		request.latitude = latitude;
		TaskData taskData = new TaskData();
		taskData.taskType = TaskType.GET_DIARY_SHARE_URL;
		taskData.diaryuuid = diaryuuid;
		taskData.diaryID = diaryids;
		taskData.request = request;
		taskData.requestStr=new Gson().toJson(request);
		judgeContains(taskData);
		start(0);
	}

	/**
	 * 添加 人人分享 任务
	 */
	public void addShareToRenren(String publishid, String content,
			String urlContent, String picUrl, String diaryid, String diaryuuid,
			String position, String longitude, String latitrde,
			String diaryUserID) {
//		Prompt.Alert("已加入人人分享任务队列");
		ThirdPartyRequest request = new ThirdPartyRequest();
		request.content = content;
		request.urlContent = urlContent;
		request.picUrl = picUrl;
		request.diaryid = diaryid;
		request.publishid = publishid;
		request.position = position;
		request.diaryid = diaryid;
		request.longitude = longitude;
		request.latitude = latitrde;
		TaskData taskData = new TaskData();
		taskData.taskType = TaskType.SHARE_TO_RENREN;
		taskData.diaryuuid = diaryuuid;
		taskData.diaryUserID = diaryUserID;
		taskData.request = request;
		taskData.requestStr=new Gson().toJson(request);
		judgeContains(taskData);
		start(0);
	}

	/**
	 * 添加 腾讯分享 任务
	 */
	public void addShareToTencent(String publishid, String content,
			String urlContent, String picUrl, String diaryid, String diaryuuid,
			String position, String longitude, String latitrde,
			String diaryUserID) {
//		Prompt.Alert("已加入腾讯分享任务队列");
		ThirdPartyRequest request = new ThirdPartyRequest();
		request.content = content;
		request.urlContent = urlContent;
		request.picUrl = picUrl;
		request.diaryid = diaryid;
		request.publishid = publishid;
		request.position = position;
		request.diaryid = diaryid;
		request.longitude = longitude;
		request.latitude = latitrde;
		TaskData taskData = new TaskData();
		taskData.taskType = TaskType.SHARE_TO_TENCENT;
		taskData.diaryuuid = diaryuuid;
		taskData.diaryUserID = diaryUserID;
		taskData.request = request;
		taskData.requestStr=new Gson().toJson(request);
		judgeContains(taskData);
		start(0);
	}

	/**
	 * 添加 新浪分享 任务
	 */
	public void addShareToSina(String publishid, String content,
			String urlContent, String picUrl, String diaryid, String diaryuuid,
			String position, String longitude, String latitrde,
			String diaryUserID) {
//		Prompt.Alert("已加入新浪分享任务队列");
		ThirdPartyRequest request = new ThirdPartyRequest();
		request.content = content;
		request.urlContent = urlContent;
		request.picUrl = picUrl;
		request.diaryid = diaryid;
		request.position = position;
		request.publishid = publishid;
		request.diaryid = diaryid;
		request.longitude = longitude;
		request.latitude = latitrde;
		TaskData taskData = new TaskData();
		taskData.taskType = TaskType.SHARE_TO_SINA;
		taskData.diaryuuid = diaryuuid;
		taskData.diaryUserID = diaryUserID;
		taskData.request = request;
		taskData.requestStr=new Gson().toJson(request);
		judgeContains(taskData);
		start(0);
	}

	/**
	 * 添加 站内分享 任务
	 */
	public void addShareToLookLook(String diaryids, String diaryuuid,
			String content, String diary_type, String position_type,
			String longitude, String latitude, String postion) {
//		Prompt.Alert("已加入站内分享任务队列");
		GsonRequest3.diaryPublishRequest request = new GsonRequest3.diaryPublishRequest();
		request.diaryids = diaryids;
		request.diaryuuid = diaryuuid;
		request.content = content;
		request.diary_type = diary_type;
		request.position_type = position_type;
		request.iscreategroup = "0"; // 0是不创建 1是创建 客户端传递0即可 后台视情况核定
		request.isofficial = "0"; // 1代表 官方推荐的 0代表普通的
		request.longitude = longitude; // 自定义经度
		request.latitude = latitude; // 自定义纬度
		request.postion = postion; // 位置
		
		request.userid = ActiveAccount.getInstance(ZApplication.getInstance())
				.getLookLookID();
		request.mac = Requester3.VALUE_MAC;
		request.imei = Requester3.VALUE_IMEI;
		TaskData taskData = new TaskData();
		taskData.taskType = TaskType.SHARE_TO_LOOKLOOK;
		taskData.diaryuuid = diaryuuid;
		taskData.request = request;
		taskData.requestStr=new Gson().toJson(request);
		judgeContains(taskData);
		start(0);
	}
	
	/**
	 * 添加设置日记权限任务
	 */
	public void addSetDiarySharePermissionsTask(String diaryid,String diaryuuid, String publish_status) {
//		Prompt.Alert("已加入站内分享任务队列");
		GsonRequest3.diarySharePermissionsRequest request = new GsonRequest3.diarySharePermissionsRequest();
		request.userid = ActiveAccount.getInstance(ZApplication.getInstance())
				.getLookLookID();
		request.diaryid = diaryid;
		request.publish_status = publish_status;
		
		request.userid = ActiveAccount.getInstance(ZApplication.getInstance())
				.getLookLookID();
		request.mac = Requester3.VALUE_MAC;
		request.imei = Requester3.VALUE_IMEI;
		
		TaskData taskData = new TaskData();
		taskData.taskType = TaskType.SET_DIARY_SHARE_PERMISSIONS;
		taskData.diaryuuid = diaryuuid;
		taskData.request = request;
		taskData.requestStr=new Gson().toJson(request);
		judgeContains(taskData);
		start(0);
	}

	/**
	 * 添加微享任务
	 */
	public void addVShareTask(String diary_id,
			String uuid, String content, String mic_title,UserObj[] userobj, String longitude,
			String latitude, String position,String position_status, String capsule, String burn_after_reading, String capsule_time) {
		GsonRequest3.createMicRequest request = new GsonRequest3.createMicRequest();
		request.userid = ActiveAccount.getInstance(ZApplication.getInstance())
				.getLookLookID();
		request.diary_id = diary_id;
		request.content=content;
		request.latitude=latitude;
		request.longitude = longitude;
		request.mic_title=mic_title;
		request.userobj=userobj;
		request.uuid=uuid;
		request.position = position;
		request.position_status = position_status;
		request.burn_after_reading = burn_after_reading;
		request.capsule = capsule;
		request.capsule_time = capsule_time;
		request.os = "android";
		request.phone_model = ZSimCardInfo.getDeviceName();
		request.mac = Requester3.VALUE_MAC;
		request.imei = Requester3.VALUE_IMEI;

		TaskData taskData = new TaskData();
		taskData.taskType = TaskType.V_SHARE;
		taskData.diaryuuid = uuid;
		taskData.diaryID = diary_id;
		taskData.request = request;
		taskData.requestStr=new Gson().toJson(request);
		judgeContains(taskData);
		start(0);
		//如果当前没有网络，则通知界面感叹号
		if(!(ZNetworkStateDetector.isAvailable()
				&& ZNetworkStateDetector.isConnected())){
			String userid=ActiveAccount.getInstance(context).getLookLookID();
			VshareDataEntities localVShareData=AccountInfo.getInstance(userid).vshareLocalDataEntities;
			if(localVShareData!=null){
				localVShareData.updateStutas(uuid,"1");
			}
		}
	}

	/**
	 * 添加 发送私信 任务
	 */
	public void addSendPrivateMsgTask(/* String diaryid,String diaryuuid, */
	String content, String targetUserIds, String privatemsgtype, String uuid/*
																			 * ,
																			 * String
																			 * diaryUserID
																			 */) {
//		Prompt.Alert("已加入发送私信任务队列");
		GsonRequest3.sendmessageRequest request = new GsonRequest3.sendmessageRequest();
		request.uuid = uuid;
		request.content = content;
		request.target_userids = targetUserIds;
		// request.diaryid = diaryid;
		request.privatemsgtype = privatemsgtype;
		request.mac = Requester3.VALUE_MAC;
		request.imei = Requester3.VALUE_IMEI;
		request.userid = ActiveAccount.getInstance(ZApplication.getInstance())
				.getLookLookID();
		TaskData taskData = new TaskData();
		taskData.taskType = TaskType.SEND_PRIVATE_MSG;
		// taskData.diaryuuid=diaryuuid;
		// taskData.diaryUserID=diaryUserID;
		taskData.request = request;
		taskData.requestStr=new Gson().toJson(request);
		taskData.createTime = TimeHelper.getInstance().now();
		judgeContains(taskData);
		start(0);
	}
	
	
	/**
	 * 添加 合并请求 任务
	 */
	public void addMergerAccountTask(String newUserid,String userid) {
//		Prompt.Alert("已加入发送私信任务队列");
		GsonRequest3.mergerAccountRequest request = new GsonRequest3.mergerAccountRequest();
		request.userid = newUserid;
		request.userid_beMerged = userid;
		request.mac = Requester3.VALUE_MAC;
		request.imei = Requester3.VALUE_IMEI;
		
		TaskData taskData = new TaskData();
		taskData.taskType = TaskType.MERGER_ACCOUNT;
		taskData.request = request;
		taskData.requestStr=new Gson().toJson(request);
		judgeContains(taskData);
		start(0);
	}

	/**
	 * 添加 删除黑名单 任务
	 *//*
	public void addBlackRemoveTask(String target_userid, String operatetype) {
//		Prompt.Alert("已加入黑名单删除任务队列");
		operateblacklistRequest request = new operateblacklistRequest();
		request.target_userid = target_userid;
		request.operatetype = "2";
		request.mac = Requester3.VALUE_MAC;
		request.imei = Requester3.VALUE_IMEI;
		request.userid = ActiveAccount.getInstance(ZApplication.getInstance())
				.getLookLookID();
		TaskData taskData = new TaskData();
		taskData.taskType = TaskType.BLACK_REMOVE;
		taskData.request = request;
		taskData.requestStr=new Gson().toJson(request);
		judgeContains(taskData);
		start(0);
	}*/

	/**
	 * 添加 删除关注 任务
	 */
	/*
	 * public void addAttendedRemoveTask(String target_userid) {
	 * Prompt.Alert("已加入关注删除任务队列"); cancelattentionRequest request = new
	 * cancelattentionRequest(); request.target_userid = target_userid;
	 * request.attention_type = "1"; request.mac = Requester3.VALUE_MAC;
	 * request.imei = Requester3.VALUE_IMEI; request.userid =
	 * ActiveAccount.getInstance(ZApplication.getInstance()) .getLookLookID();
	 * TaskData taskData = new TaskData(); taskData.taskType =
	 * TaskType.ATTENDED_REMOVE; taskData.request = request;
	 * judgeContains(taskData); start(0); }
	 */

	/**
	 * 添加 删除粉丝 任务
	 */
	/*
	 * public void addfansRemoveTask(String target_userid) {
	 * Prompt.Alert("已加入粉丝删除任务队列"); cancelattentionRequest request = new
	 * cancelattentionRequest(); request.target_userid = target_userid;
	 * request.attention_type = "2"; request.mac = Requester3.VALUE_MAC;
	 * request.imei = Requester3.VALUE_IMEI; request.userid =
	 * ActiveAccount.getInstance(ZApplication.getInstance()) .getLookLookID();
	 * TaskData taskData = new TaskData(); taskData.taskType =
	 * TaskType.FANS_REMOVE; taskData.request = request;
	 * judgeContains(taskData); start(0); }
	 */

	/**
	 * 添加 删除日记 任务
	 */
	public void addDiaryRemoveTask(String diaryIDs) {
		Prompt.Alert("已加入删除队列");
		GsonRequest3.deleteDiaryRequest request = new GsonRequest3.deleteDiaryRequest();
		request.changetomaindiaryuuids = "-1";// 已此通知服务器查询副本顶主本
		request.mac = Requester3.VALUE_MAC;
		request.imei = Requester3.VALUE_IMEI;
		request.diaryids = diaryIDs;
		request.userid = ActiveAccount.getInstance(ZApplication.getInstance())
				.getLookLookID();
		// request.userid = MyZoneFragment.userID;
		TaskData taskData = new TaskData();
		taskData.taskType = TaskType.DIARY_REMOVE;
		taskData.request = request;
		taskData.requestStr=new Gson().toJson(request);
		judgeContains(taskData);
		start(0);
	}

	/**
	 * 添加 修改位置或（和）标签任务
	 */
	public void addPositionOrTagTask(String diaryids, String diaryuuids,
			String tags, String position) {
		GsonRequest3.modTagsOrPositionRequest request = new GsonRequest3.modTagsOrPositionRequest();
		request.diaryids = diaryids;
		request.tags = tags;
		request.position = position;
		request.userid = ActiveAccount.getInstance(ZApplication.getInstance())
				.getLookLookID();
		request.mac = Requester3.VALUE_MAC;
		request.imei = Requester3.VALUE_IMEI;
		TaskData taskData = new TaskData();
		taskData.taskType = TaskType.POSITION_AND_TAG;
		taskData.diaryuuid = diaryuuids;
		taskData.request = request;
		taskData.requestStr=new Gson().toJson(request);
		judgeContains(taskData);
		start(0);
	}

	/**
	 * 添加 加入收藏任务
	 */
	/*
	 * public void addCollectAddTask(String diaryid) {
	 * Prompt.Alert("已加入收藏任务队列"); GsonRequest2.addCollectDiaryRequest request =
	 * new GsonRequest2.addCollectDiaryRequest(); request.diaryid = diaryid;
	 * request.userid = ActiveAccount.getInstance(ZApplication.getInstance())
	 * .getLookLookID(); request.mac = Requester3.VALUE_MAC; request.imei =
	 * Requester3.VALUE_IMEI; TaskData taskData = new TaskData();
	 * taskData.taskType = TaskType.COLLECT_ADD; taskData.request = request;
	 * judgeContains(taskData); start(0); }
	 */

	/**
	 * 添加 移除收藏任务
	 */
	/*
	 * public void addCollectRemoveTask(String diaryid) {
	 * Prompt.Alert("已加入收藏移除任务队列"); GsonRequest2.removeCollectDiaryRequest
	 * request = new GsonRequest2.removeCollectDiaryRequest(); request.diaryids
	 * = diaryid; request.userid =
	 * ActiveAccount.getInstance(ZApplication.getInstance()) .getLookLookID();
	 * request.mac = Requester3.VALUE_MAC; request.imei = Requester3.VALUE_IMEI;
	 * TaskData taskData = new TaskData(); taskData.taskType =
	 * TaskType.COLLECT_CANCEL; taskData.request = request;
	 * judgeContains(taskData); start(0); }
	 */

	/**
	 * 添加 增加评论任务
	 */
	/*
	 * public void addCommentAddTask(String commentContent, String commentId,
	 * String isReply, String diaryid, String diaryuuid, String commentType,
	 * String commentuuid) { Prompt.Alert("已加入评论任务队列");
	 * GsonRequest2.commentRequest request = new GsonRequest2.commentRequest();
	 * request.commentcontent = commentContent; request.commentid = commentId;
	 * request.isreply = isReply; request.userid =
	 * ActiveAccount.getInstance(ZApplication.getInstance()) .getLookLookID();
	 * request.diaryid = diaryid; request.commenttype = commentType;
	 * request.commentuuid = commentuuid; request.mac = Requester3.VALUE_MAC;
	 * request.imei = Requester3.VALUE_IMEI; TaskData taskData = new TaskData();
	 * taskData.taskType = TaskType.COMMENT_ADD; taskData.diaryuuid = diaryuuid;
	 * taskData.request = request; judgeContains(taskData); start(0); }
	 */

	/*
	 * public void addCommentDeleteTask(String diaryid, String diaryuuid, String
	 * commentid, String commentuuid) { Prompt.Alert("已加入评论删除任务队列");
	 * GsonRequest2.deleteCommentRequest request = new
	 * GsonRequest2.deleteCommentRequest(); request.diaryid = diaryuuid;
	 * request.commentid = commentid; request.commentuuid = commentuuid;
	 * request.mac = Requester3.VALUE_MAC; request.imei = Requester3.VALUE_IMEI;
	 * request.userid = ActiveAccount.getInstance(ZApplication.getInstance())
	 * .getLookLookID(); TaskData taskData = new TaskData(); taskData.taskType =
	 * TaskType.COMMENT_DELETE; taskData.diaryuuid = diaryuuid; taskData.diaryID
	 * = diaryuuid; taskData.request = request; judgeContains(taskData);
	 * start(0); }
	 */

	/**
	 * 添加 参加活动任务
	 */
	/*
	 * public void addActiveAttendTask(String diaryid, String diaryuuid, String
	 * activeid) { GsonRequest2.joinActiveRequest request = new
	 * GsonRequest2.joinActiveRequest(); request.userid =
	 * ActiveAccount.getInstance(ZApplication.getInstance()) .getLookLookID();
	 * request.diaryid = diaryid; request.activeid = activeid; request.mac =
	 * Requester3.VALUE_MAC; request.imei = Requester3.VALUE_IMEI; TaskData
	 * taskData = new TaskData(); taskData.taskType = TaskType.ACTIVE_ATTEND;
	 * taskData.diaryuuid = diaryuuid; taskData.request = request;
	 * activeAddJudgeContains(taskData); start(0); }
	 */

	/**
	 * 添加 取消活动任务
	 */
	/*
	 * public void addActiveCancelTask(String diaryid, String diaryuuid, String
	 * activeid) { GsonRequest2.cancleActiveRequest request = new
	 * GsonRequest2.cancleActiveRequest(); request.userid =
	 * ActiveAccount.getInstance(ZApplication.getInstance()) .getLookLookID();
	 * request.diaryid = diaryid; request.activeid = activeid; request.mac =
	 * Requester3.VALUE_MAC; request.imei = Requester3.VALUE_IMEI; TaskData
	 * taskData = new TaskData(); taskData.taskType = TaskType.ACTIVE_CANCEL;
	 * taskData.diaryuuid = diaryuuid; taskData.request = request;
	 * activeCancelJudgeContains(taskData); start(0); }
	 */

	/*private boolean activeCancelJudgeContains(TaskData taskData) {
		if (taskList.contains(taskData)) {
			Prompt.Alert("当前视频正在取消活动，请稍后!");
		} else {
			Prompt.Alert("已加入取消活动任务队列");
			taskList.add(taskData);
		}
		return false;
	}

	private boolean activeAddJudgeContains(TaskData taskData) {
		if (taskList.contains(taskData)) {
			Prompt.Alert("当前视频正在参加活动，请稍后!");
		} else {
			Prompt.Alert("已加入参加活动任务队列");
			taskList.add(taskData);
		}
		return false;
	}*/

	private boolean judgeContains(TaskData taskData) {
		// TODO 判断是否存在相同任务,可根据任务类型具体到与该任务逻辑相关的操作
		taskList.add(taskData);
		return false;
	}

	/**
	 * 判断任务队列中是否存在根据diaryID指定的日记的站内分享任务 true-存在 false 不存在
	 */
	public boolean hasShareToLooklookTask(String diaryUUID) {
		MyDiary myDiary = DiaryManager.getInstance().findMyDiaryByUUID(
				diaryUUID);
		if (myDiary != null) {
			for (int i = 0; i < taskList.size(); i++) {
				TaskData taskData = taskList.get(i);
				if (taskData.taskType == TaskType.SHARE_TO_LOOKLOOK
						&& taskData.request instanceof diaryPublishRequest
						&& diaryUUID.equals(taskData.diaryuuid)) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * 根据日记uuid删除该日记站内分享任务
	 */
	public void removeShareToLooklookTask(String diaryUUID) {
		MyDiary myDiary = DiaryManager.getInstance().findMyDiaryByUUID(
				diaryUUID);
		if (myDiary != null) {
			for (int i = 0; i < taskList.size(); i++) {
				TaskData taskData = taskList.get(i);
				if ((taskData.taskType == TaskType.SHARE_TO_LOOKLOOK
						|| taskData.taskType == TaskType.V_SHARE
						|| taskData.taskType == TaskType.GET_DIARY_SHARE_URL
						|| taskData.taskType == TaskType.SHARE_TO_SINA
						|| taskData.taskType == TaskType.SHARE_TO_RENREN || taskData.taskType == TaskType.SHARE_TO_TENCENT)
						&& diaryUUID.equals(taskData.diaryuuid)) {
					removeByID(taskData.id);
				}
			}
			myDiary.diary_status = "1";
		}
	}

	/**
	 * 启动任务管理器
	 */
	public synchronized void start(long delay) {
		if (!hasTaskRunning() && ZNetworkStateDetector.isAvailable()
				&& ZNetworkStateDetector.isConnected()) {
			runningTask = getTask();
			if (runningTask != null) {
				runningTask.isRunning = true;
				runningTask.setTaskLister(this);
				runningTask.startDelay(delay);
			}
		}
	}

	// 获取任务列表中的第一个任务
	private IOfflineTask getTask() {
		synchronized (taskList) {
			if (taskList.size() > 0) {
				TaskData taskData = taskList.get(0);
				if (isTaskCanRun(taskData)) {
					// 可根据任务类型定义不同类型任务
					IOfflineTask task = null;
					switch (taskData.taskType) {
					case SEND_PRIVATE_MSG:
						task = new SendPrivateMsgTask();
						break;
					case PRIVATE_MSG_AUDIO_UPLOAD:
						task = new FileUploadTask();
						break;
					case VIDEO_COVER_UPLOAD:
						task = new VideoCoverUploadTask();
						break;
					case ACTIVE_ATTEND:
						break;
					case GET_DIARY_SHARE_URL: {
						task = new SimpleCMDTask();
						ThirdPartyRequest thirdPartyRequest = (ThirdPartyRequest) taskData.request;
						GsonRequest3.getDiaryUrlRequest request = new GsonRequest3.getDiaryUrlRequest();
						request.userid = ActiveAccount.getInstance(
								ZApplication.getInstance()).getLookLookID(); // 用户id
						request.diaryids = thirdPartyRequest.diaryid; // 日记ID //
																		// 如果是多个日记则会创建日记组
						request.diaryuuid = thirdPartyRequest.groupid; // 日记组时上传UUID
						request.content = thirdPartyRequest.content; // 分享内容
						request.share_type = thirdPartyRequest.shareType + ""; // 1新浪//
																				// 2人人//
																				// 5//
																				// QZONE空间//
																				// 6腾讯//
																				// 9微信朋友圈//
																				// 10短信//
																				// 11邮箱//
																				// 12微信好友//
																				// 100站内公开//
																				// 101朋友圈//
																				// 102私密分享//
																				// 103微享
						request.type = thirdPartyRequest.isForward;
						request.weather = thirdPartyRequest.weather;
						request.weather_info = thirdPartyRequest.weather_info;
						request.os = "android";
						request.publishid = thirdPartyRequest.publishid;
						request.phone_model = ZSimCardInfo.getDeviceName();
						request.longitude = thirdPartyRequest.longitude; // 自定义经度
						request.latitude = thirdPartyRequest.latitude; // 自定义纬度
						request.postion = thirdPartyRequest.position; // 位置
						request.imei = Requester3.VALUE_IMEI;
						request.mac = Requester3.VALUE_MAC;

						task.id = taskData.id;
						task.taskType = taskData.taskType;
						task.request = request;
						task.diaryID = taskData.diaryID;
						task.diaryuuid = taskData.diaryuuid;
						task.createTime = taskData.createTime;
						task.context = context;
						return task;
					}
					case SHARE_TO_RENREN:
					case SHARE_TO_TENCENT:
					case SHARE_TO_SINA:
						task = new ThirdPartyShareTask();
						break;
					default:// 默认使用简单命令任务类型
						task = new SimpleCMDTask();
						break;
					}
					if (task != null) {
						task.id = taskData.id;
						task.taskType = taskData.taskType;
						task.request = taskData.request;
						task.diaryID = taskData.diaryID;
						task.diaryuuid = taskData.diaryuuid;
						task.createTime = taskData.createTime;
						task.context = context;
					}
					return task;
				}
			}
			Log.d(TAG, "getTask->taskList is empty");
			return null;
		}
	}

	// 检测taskData是否已经满足可执行条件
	private boolean isTaskCanRun(TaskData taskData) {
		if (null == taskData)
			return false;
		switch (taskData.taskType) {
		case SAFEBOX_ADD: {
			// 根据给定的日记组uuid找到日记组id
			if (null == taskData.diaryuuid || 0 == taskData.diaryuuid.length()) {
				Log.e(TAG, "SAFEBOX_ADD taskData.diaryuuid error");
				removeByID(taskData.id);
				return false;
			}
			String diaryIDs = DiaryManager.getInstance()
					.getDiaryGroupIDsByUUIDs(taskData.diaryuuid);
			if (null == diaryIDs || 0 == diaryIDs.length()) {
				Log.d(TAG, "SAFEBOX_ADD diaryIDs 未同步完成");
				adjustTaskToEnd(taskData);
				return false;
			}
			if (taskData.diaryuuid.split(",").length == diaryIDs.split(",").length) {
				((safeboxRequest) taskData.request).diaryids = diaryIDs;
				return true;
			} else {
				Log.d(TAG, "SAFEBOX_ADD 未同步完成");
				adjustTaskToEnd(taskData);
				return false;
			}
		}
		case SAFEBOX_REMOVE: {
			if (null == taskData.diaryuuid || 0 == taskData.diaryuuid.length()) {
				removeByID(taskData.id);
				Log.e(TAG, "SAFEBOX_REMOVE taskData.diaryuuid error");
				return false;
			}
			String diaryIDs = DiaryManager.getInstance()
					.getDiaryGroupIDsByUUIDs(taskData.diaryuuid);
			if (null == diaryIDs || 0 == diaryIDs.length()) {// 日记已被删除
				removeByID(taskData.id);
				Log.e(TAG, "SAFEBOX_REMOVE diaryIDs error");
				return false;
			}
			((safeboxRequest) taskData.request).diaryids = diaryIDs;
			return true;
		}
		case DIARY_REMOVE: {
			return true;
		}
		case MERGER_ACCOUNT: {
			return true;
		}
		case ACTIVE_ATTEND: {
			// 检测该日记是否已经同步成功、是否已站内分享，如果没有，将该任务放到队列尾部并跳出
			if (isSync(taskData) && isSharedToLooklook(taskData)) {
				return true;
			} else {
				adjustTaskToEnd(taskData);
				return false;
			}
		}
		/*
		 * case ACTIVE_CANCEL: { if (null == taskData.diaryuuid || 0 ==
		 * taskData.diaryuuid.length()) { removeByID(taskData.id); Log.e(TAG,
		 * "ACTIVE_CANCEL taskData.diaryuuid error"); return false; } String
		 * diaryIDs = DiaryManager.getInstance()
		 * .getDiaryGroupIDsByUUIDs(taskData.diaryuuid); if (null == diaryIDs ||
		 * 0 == diaryIDs.length()) {// 日记已被删除 removeByID(taskData.id);
		 * Log.e(TAG, "ACTIVE_CANCEL diaryIDs error"); return false; }
		 * ((cancleActiveRequest) taskData.request).diaryid = diaryIDs; return
		 * true; }
		 */
		case ATTENDED_REMOVE:
			return true;
		case BLACK_REMOVE:
			return true;
		case COLLECT_ADD:
			return true;
		case COLLECT_CANCEL:
			return true;
			/*
			 * case COMMENT_ADD: { if (null == taskData.diaryuuid || 0 ==
			 * taskData.diaryuuid.length()) { removeByID(taskData.id);
			 * Log.e(TAG, "COMMENT_ADD taskData.diaryuuid error"); return false;
			 * } String diaryIDs = DiaryManager.getInstance()
			 * .getDiaryGroupIDsByUUIDs(taskData.diaryuuid); if (null ==
			 * diaryIDs || 0 == diaryIDs.length()) {// 日记已被删除
			 * removeByID(taskData.id); Log.e(TAG,
			 * "COMMENT_ADD diaryIDs error"); return false; } ((commentRequest)
			 * taskData.request).diaryid = diaryIDs; return true; }
			 */
			/*
			 * case COMMENT_DELETE: { if (null == taskData.diaryuuid || 0 ==
			 * taskData.diaryuuid.length()) { removeByID(taskData.id);
			 * Log.e(TAG, "COMMENT_ADD taskData.diaryuuid error"); return false;
			 * } String diaryIDs = DiaryManager.getInstance()
			 * .getDiaryGroupIDsByUUIDs(taskData.diaryuuid); if (null ==
			 * diaryIDs || 0 == diaryIDs.length()) {// 日记已被删除
			 * removeByID(taskData.id); Log.e(TAG,
			 * "COMMENT_ADD diaryIDs error"); return false; }
			 * ((deleteCommentRequest) taskData.request).diaryid = diaryIDs;
			 * return true; }
			 */
		case DIARY_MEDIA_UPLOAD:
			return true;
		case FANS_REMOVE:
			return true;
		case GET_DIARY_SHARE_URL: {
			// diaryID为空时，如果uuid为多个，则表示需要创建组，如果uuid一个，表示该日记未上传
			if (!TextUtils.isEmpty(taskData.diaryID))
				return true;
			if (TextUtils.isEmpty(taskData.diaryuuid)) {
				Log.e(TAG, "GET_DIARY_SHARE_URL diaryuuid is null");
				removeByID(taskData.id);
				return false;
			}
			String[] uuids = taskData.diaryuuid.split(",");
			if (uuids.length > 1) {
				String diaryIDs = DiaryManager.getInstance()
						.getDiaryGroupIDsByUUIDs(taskData.diaryuuid);
				if (TextUtils.isEmpty(diaryIDs)) {
					adjustTaskToEnd(taskData);
					return false;
				}
				if (taskData.diaryuuid.split(",").length == diaryIDs.split(",").length) {
					taskData.diaryID = diaryIDs;
					((ThirdPartyRequest) taskData.request).diaryid = diaryIDs;
					((ThirdPartyRequest) taskData.request).diaryuuid = createDiaryGroup(
							(ThirdPartyRequest) taskData.request, diaryIDs);
					return true;
				} else {
					Log.d(TAG, "GET_DIARY_SHARE_URL 未同步完成");
					adjustTaskToEnd(taskData);
					return false;
				}
			} else {// 日记未上传，查询日记同步状态
				MyDiary myDiary = DiaryManager.getInstance().findMyDiaryByUUID(
						taskData.diaryuuid);
				if (myDiary != null && myDiary.isSychorized()) {
					taskData.diaryID = myDiary.diaryid;
					((ThirdPartyRequest) taskData.request).diaryid = myDiary.diaryid;
					return true;
				}
				return false;
			}
		}
		case POSITION_AND_TAG: {
			if (null == taskData.diaryuuid || 0 == taskData.diaryuuid.length()) {
				removeByID(taskData.id);
				Log.e(TAG, "POSITION_AND_TAG taskData.diaryuuid error");
				return false;
			}
			String diaryIDs = DiaryManager.getInstance().getDiaryIDsByUUIDs(
					taskData.diaryuuid);
			if (null == diaryIDs || 0 == diaryIDs.length()) {// 日记已被删除
				removeByID(taskData.id);
				Log.e(TAG, "POSITION_AND_TAG diaryIDs error");
				return false;
			}
			((modTagsOrPositionRequest) taskData.request).diaryids = diaryIDs;
			return true;
		}
		case PRIVATE_MSG_AUDIO_UPLOAD:
			return true;
		case VIDEO_COVER_UPLOAD:
			INetworkTask networkTask = NetworkTaskManager.getInstance(userID)
					.findTaskByUUID(taskData.diaryuuid);
			if (null != networkTask) {
				Log.d(TAG, "VIDEO_COVER_UPLOAD diaryIDs 未同步完成");
				adjustTaskToEnd(taskData);
				return false;
			}

			if (taskData.request instanceof VideoCoverUploaderInfo) {
				MyDiary myDiary = DiaryManager.getInstance().findMyDiaryByUUID(
						taskData.diaryuuid);
				if (myDiary != null) {
					((VideoCoverUploaderInfo) taskData.request).diaryid = myDiary.diaryid;
					((VideoCoverUploaderInfo) taskData.request).attachmentid = myDiary.attachs.levelattach.attachid;
					Log.d(TAG,
							"VIDEO_COVER_UPLOAD diaryId = "
									+ ((VideoCoverUploaderInfo) taskData.request).diaryid
									+ " attachmentid = "
									+ ((VideoCoverUploaderInfo) taskData.request).attachmentid);
				}
			}
			return true;
		case SEND_PRIVATE_MSG: {
			// if(null==taskData.diaryuuid||0==taskData.diaryuuid.length()){
			// removeByID(taskData.id);
			// Log.e(TAG, "SEND_PRIVATE_MSG taskData.diaryuuid error");
			// return false;
			// }
			// String
			// diaryIDs=DiaryManager.getInstance().getDiaryGroupIDsByUUIDs(taskData.diaryuuid);
			// if(null==diaryIDs||0==diaryIDs.length()){//日记已被删除
			// removeByID(taskData.id);
			// Log.e(TAG, "SEND_PRIVATE_MSG diaryIDs error");
			// return false;
			// }
			// ((sendmessageRequest)taskData.request).diaryid=diaryIDs;
			// 当私信类型为日记私信时，判断日记是否已同步，如果没有，将该任务放到队列尾部并跳出
			// if("3".equals(((sendmessageRequest)taskData.request).privatemsgtype)){
			// //如果是自己的日记，判断是否已同步，如果是别人的日记,不用判断
			// if(taskData.diaryUserID!=null&&taskData.diaryUserID.length()>0&&!taskData.diaryUserID.equals(DiaryManager_del.getInstance().getMyUserID())){
			// return true;
			// }else if(isSync(taskData)){
			// return true;
			// }else{
			// adjustTaskToEnd(taskData);
			// return false;
			// }
			// }
			return true;
		}
		case V_SHARE: {
			return true;
			/*if (!TextUtils.isEmpty(taskData.diaryID))
				return true;
			if (TextUtils.isEmpty(taskData.diaryuuid)) {
				Log.e(TAG, "V_SHARE diaryuuid is null");
				removeByID(taskData.id);
				return false;
			}
			String diaryIDs = DiaryManager.getInstance()
					.getDiaryGroupIDsByUUIDs(taskData.diaryuuid);
			if (TextUtils.isEmpty(diaryIDs)) {
				adjustTaskToEnd(taskData);
				return false;
			}
			if (taskData.diaryuuid.split(",").length == diaryIDs.split(",").length) {
				taskData.diaryID = diaryIDs;
				((createMicRequest) taskData.request).diary_id = diaryIDs;
//				if (diaryIDs.split(",").length > 1) {// 创建组
//					((createMicRequest) taskData.request).diaryuuid = createDiaryGroup(
//							(createMicRequest) taskData.request, diaryIDs);
//				} else {// 不创建组
//					((createMicRequest) taskData.request).diaryuuid = "";
//				}
//				return true;
			} else {
				Log.d(TAG, "V_SHARE 未同步完成");
				adjustTaskToEnd(taskData);
				return false;
			}*/
		}
		case SHARE_TO_LOOKLOOK: {
			if (!TextUtils.isEmpty(taskData.diaryID))
				return true;
			if (TextUtils.isEmpty(taskData.diaryuuid)) {
				Log.e(TAG, "SHARE_TO_LOOKLOOK diaryuuid is null");
				removeByID(taskData.id);
				return false;
			}
			if (null == taskData.diaryuuid || 0 == taskData.diaryuuid.length()) {
				removeByID(taskData.id);
				Log.e(TAG, "SHARE_TO_LOOKLOOK taskData.diaryuuid error");
				return false;
			}
			String diaryIDs = DiaryManager.getInstance()
					.getDiaryGroupIDsByUUIDs(taskData.diaryuuid);
			if (null == diaryIDs || 0 == diaryIDs.length()) {// 日记已被删除
				adjustTaskToEnd(taskData);
				Log.e(TAG, "SHARE_TO_LOOKLOOK diaryIDs error");
				return false;
			}
			((diaryPublishRequest) taskData.request).diaryids = diaryIDs;
			return true;
		}
		case SET_DIARY_SHARE_PERMISSIONS:{
			if (!TextUtils.isEmpty(taskData.diaryID))
				return true;
			if (TextUtils.isEmpty(taskData.diaryuuid)) {
				Log.e(TAG, "SET_DIARY_SHARE_PERMISSIONS diaryuuid is null");
				removeByID(taskData.id);
				return false;
			}
			if (null == taskData.diaryuuid || 0 == taskData.diaryuuid.length()) {
				removeByID(taskData.id);
				Log.e(TAG, "SET_DIARY_SHARE_PERMISSIONS taskData.diaryuuid error");
				return false;
			}
			String diaryIDs = DiaryManager.getInstance()
					.getDiaryGroupIDsByUUIDs(taskData.diaryuuid);
			if (null == diaryIDs || 0 == diaryIDs.length()) {// 日记已被删除
				adjustTaskToEnd(taskData);
				Log.e(TAG, "SET_DIARY_SHARE_PERMISSIONS diaryIDs error");
				return false;
			}
			((diarySharePermissionsRequest) taskData.request).diaryid = diaryIDs;
			return true;
		}
		case SHARE_TO_RENREN:
		case SHARE_TO_SINA:
		case SHARE_TO_TENCENT: {
			// if (null == taskData.diaryuuid || 0 ==
			// taskData.diaryuuid.length()) {
			// removeByID(taskData.id);
			// Log.e(TAG, "SHARE_TO_TENCENT taskData.diaryuuid error");
			// return false;
			// }
			// String diaryIDs = DiaryManager.getInstance()
			// .getDiaryGroupIDsByUUIDs(taskData.diaryuuid);
			// if (null == diaryIDs || 0 == diaryIDs.length()) {// 日记已被删除
			// removeByID(taskData.id);
			// Log.e(TAG, "SHARE_TO_TENCENT diaryIDs error");
			// return false;
			// }
			// ((ThirdPartyRequest) taskData.request).diaryid = diaryIDs;
			// // 检测该日记是否已经同步成功、是否已站内分享，如果没有，将该任务放到队列尾部并跳出
			// if (taskData.diaryUserID != null
			// && taskData.diaryUserID.length() > 0
			// && !taskData.diaryUserID.equals(DiaryManager_del
			// .getInstance().getMyUserID())) {
			// return true;
			// } else if (isSync(taskData) && isSharedToLooklook(taskData)) {
			// return true;
			// } else {
			// adjustTaskToEnd(taskData);
			// return false;
			// }
			return true;
		}
		case SHARE_TRACE_UPLOAD: {
			/*
			 * if (null == taskData.diaryuuid || 0 ==
			 * taskData.diaryuuid.length()) { removeByID(taskData.id);
			 * Log.e(TAG, "SHARE_TRACE_UPLOAD taskData.diaryuuid error"); return
			 * false; } String diaryIDs = DiaryManager.getInstance()
			 * .getDiaryGroupIDsByUUIDs(taskData.diaryuuid); if (null ==
			 * diaryIDs || 0 == diaryIDs.length()) {// 日记已被删除
			 * removeByID(taskData.id); Log.e(TAG,
			 * "SHARE_TRACE_UPLOAD diaryIDs error"); return false; }
			 * ((shareDiaryRequest) taskData.request).diaryid = diaryIDs;
			 */
			return true;
		}
		default:
			Log.e(TAG, "unknow tasktype " + taskData.taskType);
			break;
		}
		return false;
	}

	// 创建一个日记组并返回uuid
	private static String createDiaryGroup(ThirdPartyRequest request,
			String diaryids) {
		MyDiaryList diaryList = new MyDiaryList();
		diaryList.diaryuuid = UUID.randomUUID().toString().replace("-", "");
		request.groupid = diaryList.diaryuuid;
		diaryList.isgroup = "1";
		diaryList.join_safebox = "0";
		diaryList.create_time = String.valueOf(TimeHelper.getInstance().now());
		diaryList.update_time = String.valueOf(TimeHelper.getInstance().now());
		diaryList.contain = diaryids;
		DiaryManager.getInstance().saveDiaryGroup(diaryList);
		return diaryList.diaryuuid;
	}

	/*// 创建一个日记组并返回uuid
	private static String createDiaryGroup(createMicRequest request,
			String diaryids) {
		MyDiaryList diaryList = new MyDiaryList();
		diaryList.diaryuuid = UUID.randomUUID().toString().replace("-", "");
		diaryList.isgroup = "1";
		diaryList.join_safebox = "0";
		diaryList.create_time = String.valueOf(TimeHelper.getInstance().now());
		diaryList.update_time = String.valueOf(TimeHelper.getInstance().now());
		diaryList.contain = diaryids;
		DiaryManager.getInstance().saveDiaryGroup(diaryList);
		return diaryList.diaryuuid;
	}*/

	// 将任务放到队列尾部
	private void adjustTaskToEnd(TaskData taskData) {
		if (null == taskData)
			return;
		taskList.remove(taskData);
		taskList.add(taskData);
		Log.d(TAG, "adjustTaskToEnd " + taskData.toString());
	}

	/*
	 * //当任务日记ID与实际日记ID不一致时，更新任务日记ID private boolean
	 * updateTaskDataDiaryID(TaskData taskData){
	 * if(taskData!=null&&taskData.diaryuuid
	 * !=null&&taskData.diaryuuid.length()>0){ MyDiary
	 * myDiary=DiaryManager_del.getInstance
	 * ().findLocalDiaryByUuid(taskData.diaryuuid); if(null==myDiary){
	 * Log.e(TAG, "updateTaskDataDiaryID->日记未找到，应该删除此条任务"); // return true;
	 * }else{ //当任务日记id等于日记uuid并且日记uuid与日记id不相等时，更新任务日记id
	 * if(myDiary.diaryuuid.equals
	 * (taskData.diaryuuid)&&!myDiary.diaryuuid.equals(myDiary.diaryid)){
	 * taskData.diaryID=myDiary.diaryid; switch (taskData.taskType) { case
	 * ACTIVE_ATTEND:
	 * ((joinActiveRequest)taskData.request).diaryid=myDiary.diaryid; break;
	 * case ACTIVE_CANCEL:
	 * ((cancleActiveRequest)taskData.request).diaryid=myDiary.diaryid; break;
	 * case SAFEBOX_ADD:
	 * ((safeboxRequest)taskData.request).diaryid=myDiary.diaryid; break; case
	 * SAFEBOX_REMOVE:
	 * ((safeboxRequest)taskData.request).diaryid=myDiary.diaryid; break; case
	 * SHARE_TRACE_UPLOAD:
	 * ((shareDiaryRequest)taskData.request).diaryid=myDiary.diaryid; break;
	 * case SHARE_TO_RENREN: case SHARE_TO_SINA: case SHARE_TO_TENCENT:
	 * ((ThirdPartyRequest)taskData.request).diaryid=myDiary.diaryid; break;
	 * case SHARE_TO_LOOKLOOK:
	 * ((diaryPublishRequest)taskData.request).diaryid=myDiary.diaryid; break;
	 * case SEND_PRIVATE_MSG:
	 * ((sendmessageRequest)taskData.request).diaryid=myDiary.diaryid; break;
	 * case POSITION_AND_TAG:
	 * ((modTagsOrPositionRequest)taskData.request).diaryid=myDiary.diaryid;
	 * break; case COMMENT_ADD:
	 * ((commentRequest)taskData.request).diaryid=myDiary.diaryid; break; case
	 * COMMENT_DELETE:
	 * ((deleteCommentRequest)taskData.request).diaryid=myDiary.diaryid; break;
	 * default: Log.e(TAG, "unknow tasktype "+taskData.taskType); break; } } } }
	 * return false; }
	 */

	// 删除活动任务
	private void removeActiveAttendTask(TaskData taskData) {
		if (taskData != null) {
			MyDiary myDiary = DiaryManager.getInstance().findMyDiaryByUUID(
					taskData.diaryuuid);
			if (null == myDiary) {
				Log.e(TAG, "removeActiveAttendTask->日记未找到，应该删除此条任务");
			} else {
				ArrayList<String> taskIDList = new ArrayList<String>();
				for (int i = 0; i < taskList.size(); i++) {
					TaskData data = taskList.get(i);
					if (data.taskType == TaskType.ACTIVE_ATTEND
							&& (myDiary.diaryuuid.equals(data.diaryuuid))) {
						taskIDList.add(data.id);
					}
				}
				for (int i = 0; i < taskIDList.size(); i++) {
					removeByID(taskIDList.get(i));
				}
			}
		}
	}

	// 删除第三方分享任务
	private void removeSharedToThirdPartyTask(TaskData taskData) {
		if (taskData != null) {
			MyDiary myDiary = DiaryManager.getInstance().findMyDiaryByUUID(
					taskData.diaryID);
			if (null == myDiary) {
				Log.e(TAG, "removeSharedToThirdPartyTask->日记未找到，应该删除此条任务");
			} else {
				ArrayList<String> taskIDList = new ArrayList<String>();
				for (int i = 0; i < taskList.size(); i++) {
					TaskData data = taskList.get(i);
					if ((data.taskType == TaskType.SHARE_TO_RENREN
							|| data.taskType == TaskType.SHARE_TO_SINA || data.taskType == TaskType.SHARE_TO_TENCENT)
							&& (myDiary.diaryuuid.equals(data.diaryuuid))) {
						taskIDList.add(data.id);
					}
				}
				for (int i = 0; i < taskIDList.size(); i++) {
					removeByID(taskIDList.get(i));
				}
			}
		}
	}

	// 判断日记是否是所有人可见
	private boolean isPublic(TaskData taskData) {
		if (taskData != null) {
			MyDiary myDiary = DiaryManager.getInstance().findMyDiaryByUUID(
					taskData.diaryuuid);
			if (null == myDiary) {
				Log.e(TAG, "isPublic->日记未找到，应该删除此条任务");
			} else {
				// if("4".equals(myDiary.publish_status)||"2".equals(myDiary.publish_status))
				// return false;
			}
		}
		return true;
	}

	// 判断日记是否已同步
	private boolean isSync(TaskData taskData) {
		if (taskData != null) {
			MyDiary myDiary = DiaryManager.getInstance().findMyDiaryByUUID(
					taskData.diaryuuid);
			if (null == myDiary) {
				// 当在本地没有找到时，说明该日记可能是云端的
				return true;
			} else {
				if (myDiary.sync_status > 3)
					return true;
			}
		}
		return false;
	}

	// 判断日记是否已发布
	private boolean isSharedToLooklook(TaskData taskData) {
		if (taskData != null) {
			MyDiary myDiary = DiaryManager.getInstance().findMyDiaryByUUID(
					taskData.diaryuuid);
			if (null == myDiary) {
				Log.e(TAG, "isSharedToLooklook->日记未找到，应该删除此条任务");
			} else {
				// 如果日记已发布，检测任务队列是中否存在该日记的发布任务，如果没有，表明该日记已经发布，如果有，说明还没发布成功
				// 可能存在的bug 如果该日记已经发布过一次，继续发布第二次，第二次发布任务还未执行，就会出问题
				if ("2".equals(myDiary.diary_status)) {
					if (!hasShareToLooklookTask(myDiary.diaryuuid))
						return true;
				}
			}
		}
		return false;
	}

	// 判断当前是否有任务在运行
	private boolean hasTaskRunning() {
		if (runningTask != null && runningTask.isRunning) {
			Log.d(TAG, "runningtask is " + runningTask.toString());
			return true;
		}
		return false;
	}

	private OfflineTaskManager() {
	}

	private static OfflineTaskManager offlineTaskManager;

	public static OfflineTaskManager getInstance() {
		if (null == offlineTaskManager)
			offlineTaskManager = new OfflineTaskManager();
		return offlineTaskManager;
	}

	private ActiveAccount aa;
	private AccountInfo ai;
	private PrivateMessageManager pmm;
	private String userID;
	private TimeoutTask timeoutTask;

	public void init(Context context) {
		this.context = context;
		aa = ActiveAccount.getInstance(context);
		ai = AccountInfo.getInstance(aa.getLookLookID());
		userID = ActiveAccount.getInstance(ZApplication.getInstance()).getUID();
		pmm = ai.privateMsgManger;
		String userid = ActiveAccount.getInstance(ZApplication.getInstance())
				.getUID();
		AccountInfo accountInfo = AccountInfo.getInstance(userid);
		taskList = accountInfo.taskDataList;
		// 持久化以后，taskdata.request某些情况下会变成linkedhashmap类
		updateTaskRequest();
		IntentFilter filter = new IntentFilter();
		filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
		filter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
		filter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
		ZApplication.getInstance().getApplicationContext()
				.registerReceiver(new NetConnectedReceiver(), filter);
		// 检测一分钟超时移除任务
		if (timeoutTask != null) {
			timeoutTask.cancel();
		}
		timeoutTask = new TimeoutTask(this);
		timeoutTask.start();
	}

	@Override
	public void hasTask() {
		start(0);
	}

	@Override
	public void success(IOfflineTask task) {
		Log.d(TAG, "task success\n" + task.toString());
		task.isRunning = false;
		switch (task.taskType) {
		case SAFEBOX_REMOVE:
			if (isAppOnFront(context))
				//Prompt.Dialog(context, false, "提示", "成功移出保险箱",null);
				Toast.makeText(context, "成功移出保险箱",
					     1000).show();
			break;
		case SAFEBOX_ADD:
			if (isAppOnFront(context))
				//Prompt.Dialog(context, false, "提示", "成功加入保险箱",null);
				Toast.makeText(context, "成功加入保险箱",
					     1000).show();
			break;
		case SHARE_TO_TENCENT:
			if (isAppOnFront(context))
				Prompt.Alert("分享到腾讯成功");
			break;
		case SHARE_TO_SINA:
			if (isAppOnFront(context))
				Prompt.Alert("分享到新浪成功");
			break;
		case SHARE_TO_RENREN:
			if (isAppOnFront(context))
				Prompt.Alert("分享到人人成功");
			break;
		case V_SHARE:
			if (isAppOnFront(context))
				Prompt.Alert("微享发布成功");
//			LocalBroadcastManager.getInstance(context).sendBroadcast(new Intent(SafeboxVShareFragment.ACTION_VSHARE_CHANGED));
			break;
		case SHARE_TO_LOOKLOOK:
			if (isAppOnFront(context))
				Prompt.Alert("分享到looklook成功");
			break;
		case SEND_PRIVATE_MSG:
			setPrivateMsgRes(task, true);
			if ("138115".equals(task.failedResult)) {
				if (isAppOnFront(context))
					Prompt.Alert("对方开启隐私设置，无法进行私信");
			} else if ("138107".equals(task.failedResult)) {
				if (isAppOnFront(context))
					Prompt.Alert("消息发送成功");
			} else {
				if (isAppOnFront(context))
					Prompt.Alert("消息发送成功");
			}
			break;
		case PRIVATE_MSG_AUDIO_UPLOAD:
			setPrivateMsgRes(task, true);
			if (isAppOnFront(context))
				Prompt.Alert("消息发送成功");
			break;
		case VIDEO_COVER_UPLOAD:
			Log.d(TAG, "success VIDEO_COVER_UPLOAD");
			mappingVideoCover(task);
			break;
		case BLACK_REMOVE:
			if (isAppOnFront(context))
				Prompt.Alert("已移出黑名单");
			break;
		case ATTENDED_REMOVE:
			if (isAppOnFront(context))
				Prompt.Alert("关注取消成功");
			break;
		case FANS_REMOVE:
			if (isAppOnFront(context))
				Prompt.Alert("粉丝删除成功");
			break;
		case DIARY_REMOVE:
			if (isAppOnFront(context))
				Prompt.Alert("日记删除成功");
			break;
		/*
		 * case ACTIVE_ATTEND: if (isAppOnFront(context))
		 * Prompt.Alert("参加活动成功");
		 * 
		 * Intent msgIntent = new Intent(
		 * ActivitiesDetailActivity.FLAG_ACTIVITY_DATA_UPDATE);
		 * msgIntent.putExtra("activity_update_type",
		 * ActivitiesDetailActivity.FLAG_ACTIVITY_SUCESS);
		 * msgIntent.putExtra("activity_add_cancel", "add");
		 * 
		 * LocalBroadcastManager.getInstance(context).sendBroadcast(msgIntent);
		 * 
		 * break;
		 */
		/*
		 * case ACTIVE_CANCEL: if (isAppOnFront(context))
		 * Prompt.Alert("活动取消成功");
		 * 
		 * Intent cancelIntent = new Intent(
		 * ActivitiesDetailActivity.FLAG_ACTIVITY_DATA_UPDATE);
		 * cancelIntent.putExtra("activity_update_type",
		 * ActivitiesDetailActivity.FLAG_ACTIVITY_SUCESS);
		 * cancelIntent.putExtra("activity_add_cancel", "cancel");
		 * 
		 * LocalBroadcastManager.getInstance(context).sendBroadcast(
		 * cancelIntent); break;
		 */
		case COLLECT_ADD:
			if (isAppOnFront(context))
				Prompt.Alert("收藏日记成功");
			break;
		case COLLECT_CANCEL:
			if (isAppOnFront(context))
				Prompt.Alert("移除收藏成功");
			break;
		case COMMENT_ADD:
			if (isAppOnFront(context))
				Prompt.Alert("评论添加成功");
			break;
		case COMMENT_DELETE:
			if (isAppOnFront(context))
				Prompt.Alert("评论删除成功");
			break;
		case POSITION_AND_TAG:
			if (isAppOnFront(context))
				Prompt.Alert("标签添加成功");
			// DiaryManager_del diarymanager = DiaryManager_del.getInstance();
			// MyDiary myLocalDiary = diarymanager.findLocalDiary(task.diaryID);
			// if (myLocalDiary != null) {
			// Log.d(TAG,"save myLocalDiary not null");
			// GsonRequest2.modTagsOrPositionRequest request =
			// (GsonRequest2.modTagsOrPositionRequest)task.request;
			// myLocalDiary.position = request.position;
			// myLocalDiary.setTags(request.tags);
			// DiaryManager_del.getInstance().diaryDataChanged(myLocalDiary.diaryuuid);
			// }
			break;
		default:
			break;
		}
		removeByID(task.id);
		start(0);
	}

	private void mappingVideoCover(IOfflineTask task) {
		if (task.request instanceof VideoCoverUploaderInfo) {
			String url = task.successResult;
			String path = ((VideoCoverUploaderInfo) task.request).mFilePath;
			String diaryUUID = task.diaryuuid;
			MyDiary myDiary = DiaryManager.getInstance().findMyDiaryByUUID(
					diaryUUID);
			
			if (myDiary != null) {
				String uid = ActiveAccount.getInstance(MainApplication.getAppInstance()).getUID();
				AccountInfo.getInstance(uid).mediamapping.delMedia(uid,myDiary.attachs.videocover,true);
				myDiary.attachs.videocover = url;
			}
			
			Log.d(TAG, "mappingVideoCover url = " + url + " path = " + path
					+ " videoCover = " + myDiary.attachs.videocover);
			DiaryController.saveToMediaMapping(
					GsonProtocol.ATTACH_TYPE_PICTURE, path, url);
		}
	}

	@Override
	public void failed(IOfflineTask task) {
		Log.e(TAG, "task failed");
		runningTask = null;
		TaskData taskData = getTaskData(task.id);
		adjustTaskToEnd(taskData);
		if (taskData != null) {
			Log.e(TAG, taskData.toString());
			if (taskData.retryCount >= MAX_RETRY_COUNT) {
				task.isRunning = false;
				removeByID(task.id);
				switch (task.taskType) {
				case SAFEBOX_REMOVE:
					break;
				case SAFEBOX_ADD:
					break;
				case SHARE_TO_TENCENT:
					break;
				case SHARE_TO_SINA:
					break;
				case SHARE_TO_RENREN:
					break;
				case SHARE_TO_LOOKLOOK:
					break;
				case SEND_PRIVATE_MSG:
					if ("138119".equals(task.failedResult)) {
						LocalBroadcastManager.getInstance(MainApplication.getAppInstance())
						.sendBroadcast(new Intent(FriendsSessionPrivateMessageActivity.BROADCAST_OFFLINE_NO_FRIENDS));
					}
				case PRIVATE_MSG_AUDIO_UPLOAD:
					setPrivateMsgRes(task, false);
					break;
				case VIDEO_COVER_UPLOAD:
					break;
				case BLACK_REMOVE:
					break;
				case ATTENDED_REMOVE:
					break;
				case FANS_REMOVE:
					break;
				case DIARY_REMOVE:
					break;
				/*
				 * case ACTIVE_ATTEND: if (isAppOnFront(context))
				 * Prompt.Alert("参加活动失败"); Intent msgIntent = new Intent(
				 * ActivitiesDetailActivity.FLAG_ACTIVITY_DATA_UPDATE);
				 * msgIntent.putExtra("activity_update_type",
				 * ActivitiesDetailActivity.FLAG_ACTIVITY_FAIL);
				 * msgIntent.putExtra("activity_add_cancel", "add");
				 * LocalBroadcastManager.getInstance(context).sendBroadcast(
				 * msgIntent); break;
				 */
				/*
				 * case ACTIVE_CANCEL:
				 * 
				 * if (isAppOnFront(context)) Prompt.Alert("活动取消失败");
				 * 
				 * Intent cancelIntent = new Intent(
				 * ActivitiesDetailActivity.FLAG_ACTIVITY_DATA_UPDATE);
				 * cancelIntent.putExtra("activity_update_type",
				 * ActivitiesDetailActivity.FLAG_ACTIVITY_SUCESS);
				 * cancelIntent.putExtra("activity_add_cancel", "cancel");
				 * LocalBroadcastManager.getInstance(context).sendBroadcast(
				 * cancelIntent); break;
				 */
				case COLLECT_ADD:
					break;
				case COLLECT_CANCEL:
					break;
				case COMMENT_ADD:
					if (isAppOnFront(context))
						Prompt.Alert("添加评论失败");
					break;
				case COMMENT_DELETE:
					if (isAppOnFront(context))
						Prompt.Alert("删除评论失败");
					break;
				case POSITION_AND_TAG:
					break;
				default:
					break;
				}
			} else {
				taskData.retryCount++;
			}
		} else {
			Log.e(TAG, "task does not exit");
		}
	}

	@Override
	public void exception(IOfflineTask task) {
		Log.e(TAG, "task exception");
		task.isRunning = false;
		runningTask = null;
		TaskData taskData = getTaskData(task.id);
		adjustTaskToEnd(taskData);
		if (taskData != null) {
			Log.e(TAG, taskData.toString());
			if (taskData.retryCount >= MAX_RETRY_COUNT) {
				removeByID(task.id);
			} else {
				taskData.retryCount++;
			}
			addLast(task);
		} else {
			Log.e(TAG, "task does not exit");
		}
	}

	// 获取任务
	public TaskData getTaskData(String taskID) {
		if (taskID != null) {
			for (int i = 0; i < taskList.size(); i++) {
				TaskData taskData = taskList.get(i);
				if (taskID.equals(taskData.id))
					return taskData;
			}
		}
		return null;
	}

	private void addLast(IOfflineTask task) {
		synchronized (taskList) {
			if (task != null) {
				TaskData taskData = null;
				for (int i = 0; i < taskList.size(); i++) {
					taskData = taskList.get(i);
					if (taskData.id.equals(task.id)) {
						break;
					}
				}
				if (taskData != null) {
					taskList.remove(taskData);
					taskList.add(taskData);
				}
			}
		}
	}

	@Override
	public void timeout(TaskData taskData) {
		Log.e(TAG, "task timeout\n" + taskData.toString());
		switch (taskData.taskType) {
		case SEND_PRIVATE_MSG:
			if (taskData.request instanceof GsonRequest3.sendmessageRequest) {
				GsonRequest3.sendmessageRequest temp = (sendmessageRequest) taskData.request;
				String target_userid = temp.target_userids;
				String uuid = temp.uuid;
				setPrivateMsgRes(target_userid, uuid, false);
			}
			break;
		case PRIVATE_MSG_AUDIO_UPLOAD:
			if (taskData.request instanceof FileUploadInfo[]) {
				FileUploadInfo[] temp = (FileUploadInfo[]) taskData.request;
				if (temp != null && 1 == temp.length) {
					String target_userid = temp[0].target_userids;
					String uuid = temp[0].uuid;
					setPrivateMsgRes(target_userid, uuid, false);
				}
			}
			break;
		case VIDEO_COVER_UPLOAD:
			break;
		default:
			break;
		}
		removeByID(taskData.id);
		if (runningTask != null && runningTask.id.equals(taskData.id)) {
			runningTask = null;
			start(0);
		}
	}

	// 设置私信发送结果
	private void setPrivateMsgRes(IOfflineTask task, boolean success) {
		if (task.request instanceof FileUploadInfo) {
			FileUploadInfo uploadInfos = (FileUploadInfo) task.request;
			if (pmm != null)
				pmm.setStatusByUUID(uploadInfos.target_userids,
						uploadInfos.uuid, success);
		} else if (task.request instanceof GsonRequest3.sendmessageRequest) {
			GsonRequest3.sendmessageRequest request = (sendmessageRequest) task.request;
			if (pmm != null)
				pmm.setStatusByUUID(request.target_userids, request.uuid,
						success);
		} else {
			Log.e(TAG,
					"setPrivateMsgRes->task.request can not cast FileUploadInfo");
			removeTask(task);
			start(0);
		}
	}

	// 设置私信发送结果
	private void setPrivateMsgRes(String target_userids, String uuid,
			boolean success) {
		if (pmm != null)
			pmm.setStatusByUUID(target_userids, uuid, success);
	}

	public void removeTask(IOfflineTask task) {
		if (task != null)
			removeByID(task.id);
	}

	private void removeByID(String id) {
		// 删除任务时，如果正在运行的任务未将要删除的任务时，重置正在运行任务状态
		if (runningTask != null && runningTask.id.equals(id)) {
			runningTask = null;
		}
		for (int i = 0; i < taskList.size(); i++) {
			TaskData data = taskList.get(i);
			if (id.equals(data.id)) {
				taskList.remove(i);
				return;
			}
		}
	}

	public class NetConnectedReceiver extends ZBroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			// 这个监听wifi的连接状态即是否连上了一个有效无线路由，
			// 当上边广播的状态是WifiManager.WIFI_STATE_DISABLING，和WIFI_STATE_DISABLED的时候，根本不会接到这个广播。
			// 在上边广播接到广播是WifiManager.WIFI_STATE_ENABLED状态的同时也会接到这个广播，
			// 当然刚打开wifi肯定还没有连接到有效的无线
			// Log.d(TAG, "NetConnectedReceiver intent" + intent.getAction());
			if (WifiManager.NETWORK_STATE_CHANGED_ACTION.equals(intent
					.getAction())) {
				Parcelable parcelableExtra = intent
						.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
				if (null != parcelableExtra) {
					NetworkInfo networkInfo = (NetworkInfo) parcelableExtra;
					State state = networkInfo.getState();
					boolean isConnected = state == State.CONNECTED;// 当然，这边可以更精确的确定状态
					if (isConnected) {
						Log.d(TAG, "wifi is connected");
						start(0);
					}
				}
			}
		}
	}

	private Context context;

	private boolean isAppOnFront(Context context) {
		if (context != null) {
			String packageName = context.getPackageName();
			ActivityManager activityManager = (ActivityManager) context
					.getSystemService(Context.ACTIVITY_SERVICE);
			List<RunningTaskInfo> appTask = activityManager.getRunningTasks(1);
			if (appTask != null && appTask.size() > 0) {
				if (appTask.get(0).topActivity.toString().contains(packageName)) {
					return true;
				}
			}
		}
		return false;
	}
}
