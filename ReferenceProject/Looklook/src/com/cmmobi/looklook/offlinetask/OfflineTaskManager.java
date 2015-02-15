package com.cmmobi.looklook.offlinetask;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

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
import android.util.Log;
import cn.zipper.framwork.core.ZApplication;
import cn.zipper.framwork.core.ZBroadcastReceiver;
import cn.zipper.framwork.io.network.ZNetworkStateDetector;

import com.cmmobi.looklook.activity.ActivitiesDetailActivity;
import com.cmmobi.looklook.common.gson.GsonRequest2;
import com.cmmobi.looklook.common.gson.GsonRequest2.SNS;
import com.cmmobi.looklook.common.gson.GsonRequest2.addCollectDiaryRequest;
import com.cmmobi.looklook.common.gson.GsonRequest2.cancelattentionRequest;
import com.cmmobi.looklook.common.gson.GsonRequest2.cancleActiveRequest;
import com.cmmobi.looklook.common.gson.GsonRequest2.commentRequest;
import com.cmmobi.looklook.common.gson.GsonRequest2.deleteCommentRequest;
import com.cmmobi.looklook.common.gson.GsonRequest2.deleteDiaryRequest;
import com.cmmobi.looklook.common.gson.GsonRequest2.diaryPublishRequest;
import com.cmmobi.looklook.common.gson.GsonRequest2.getDiaryUrlRequest;
import com.cmmobi.looklook.common.gson.GsonRequest2.joinActiveRequest;
import com.cmmobi.looklook.common.gson.GsonRequest2.modTagsOrPositionRequest;
import com.cmmobi.looklook.common.gson.GsonRequest2.operateblacklistRequest;
import com.cmmobi.looklook.common.gson.GsonRequest2.removeCollectDiaryRequest;
import com.cmmobi.looklook.common.gson.GsonRequest2.safeboxRequest;
import com.cmmobi.looklook.common.gson.GsonRequest2.sendmessageRequest;
import com.cmmobi.looklook.common.gson.GsonRequest2.shareDiaryRequest;
import com.cmmobi.looklook.common.gson.GsonResponse2.MyDiary;
import com.cmmobi.looklook.common.gson.Requester2;
import com.cmmobi.looklook.info.profile.AccountInfo;
import com.cmmobi.looklook.info.profile.ActiveAccount;
import com.cmmobi.looklook.info.profile.DiaryManager;
import com.cmmobi.looklook.info.profile.PrivateMessageManager;
import com.cmmobi.looklook.info.profile.TimeHelper;
import com.cmmobi.looklook.offlinetask.IOfflineTask.TaskListener;
import com.cmmobi.looklook.prompt.Prompt;
import com.google.gson.internal.LinkedHashTreeMap;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.tencent.weibo.sdk.android.api.util.ImageLoaderAsync;

/**
 * @author wuxiang
 * @email wuxiang@cmmobi.com
 * @date  2013-8-19
 */
public class OfflineTaskManager implements TaskListener {

	private static final String TAG="OfflineTaskManager";
	private List<TaskData> taskList=Collections.synchronizedList(new LinkedList<TaskData>());
	private IOfflineTask runningTask;
	public static int MAX_RETRY_COUNT=10;
	
	//当Gson反序列request是，可能会转换出LinkedHashTreeMap类型
		private void updateTaskRequest(){
			for(int i=0;i<taskList.size();i++){
				TaskData taskData=taskList.get(i);
				if(taskData.request instanceof LinkedHashTreeMap){
					LinkedHashTreeMap map=(LinkedHashTreeMap)taskData.request;
					switch (taskData.taskType) {
					case ACTIVE_ATTEND:{
						joinActiveRequest request=new joinActiveRequest();
						request.userid = map.get("userid")==null?"":map.get("userid").toString();
						request.diaryid = map.get("diaryid")==null?"":map.get("diaryid").toString();
						request.activeid = map.get("activeid")==null?"":map.get("activeid").toString();
						request.mac = Requester2.VALUE_MAC;
						request.imei = Requester2.VALUE_IMEI;
						taskData.request=request;
						break;}
					case ATTENDED_REMOVE:{
						cancelattentionRequest request=new cancelattentionRequest();
						request.target_userid = map.get("target_userid")==null?"":map.get("target_userid").toString();
						request.attention_type = map.get("attention_type")==null?"":map.get("attention_type").toString();
						request.mac = Requester2.VALUE_MAC;
						request.imei = Requester2.VALUE_IMEI;
						request.userid = ActiveAccount.getInstance(ZApplication.getInstance())
								.getLookLookID();
						taskData.request=request;
						break;}
					case ACTIVE_CANCEL:{
						cancleActiveRequest request=new cancleActiveRequest();
						request.userid = map.get("userid")==null?"":map.get("userid").toString();
						request.diaryid = map.get("diaryid")==null?"":map.get("diaryid").toString();
						request.activeid = map.get("activeid")==null?"":map.get("activeid").toString();
						request.mac = Requester2.VALUE_MAC;
						request.imei = Requester2.VALUE_IMEI;
						taskData.request=request;
						break;}
					case BLACK_REMOVE:{
						operateblacklistRequest request=new operateblacklistRequest();
						request.target_userid = map.get("target_userid")==null?"":map.get("target_userid").toString();
						request.operatetype = map.get("operatetype")==null?"":map.get("operatetype").toString();
						request.mac = Requester2.VALUE_MAC;
						request.imei = Requester2.VALUE_IMEI;
						request.userid = map.get("userid")==null?"":map.get("userid").toString();
						taskData.request=request;
						break;}
					case COLLECT_ADD:{
						addCollectDiaryRequest request=new addCollectDiaryRequest();
						request.diaryid = map.get("diaryid")==null?"":map.get("diaryid").toString();
						request.userid = map.get("userid")==null?"":map.get("userid").toString();
						request.mac = Requester2.VALUE_MAC;
						request.imei = Requester2.VALUE_IMEI;
						taskData.request=request;
						break;}
					case COLLECT_CANCEL:{
						removeCollectDiaryRequest request=new removeCollectDiaryRequest();
						request.diaryids = map.get("diaryids")==null?"":map.get("diaryids").toString();
						request.userid = map.get("userid")==null?"":map.get("userid").toString();
						request.mac = Requester2.VALUE_MAC;
						request.imei = Requester2.VALUE_IMEI;
						taskData.request=request;
						break;}
					case COMMENT_ADD:{
						commentRequest request=new commentRequest();
						request.commentcontent = map.get("commentcontent")==null?"":map.get("commentcontent").toString();
						request.commentid = map.get("commentid")==null?"":map.get("commentid").toString();
						request.isreply = map.get("isreply")==null?"":map.get("isreply").toString();
						request.userid = map.get("userid")==null?"":map.get("userid").toString();
						request.diaryid = map.get("diaryid")==null?"":map.get("diaryid").toString();
						request.commenttype = map.get("commenttype")==null?"":map.get("commenttype").toString();
						request.commentuuid = map.get("commentuuid")==null?"":map.get("commentuuid").toString();
						request.mac = Requester2.VALUE_MAC;
						request.imei = Requester2.VALUE_IMEI;
						taskData.request=request;
						break;}
					case COMMENT_DELETE:{
						deleteCommentRequest request=new deleteCommentRequest();
						request.diaryid = map.get("diaryid")==null?"":map.get("diaryid").toString();
						request.commentid = map.get("commentid")==null?"":map.get("commentid").toString();
						request.commentuuid = map.get("commentuuid")==null?"":map.get("commentuuid").toString();
						request.userid = map.get("userid")==null?"":map.get("userid").toString();
						request.mac = Requester2.VALUE_MAC;
						request.imei = Requester2.VALUE_IMEI;
						taskData.request=request;
						break;}
					case DIARY_MEDIA_UPLOAD:{
						FileUploadInfo request=new FileUploadInfo();
						//TODO 类型带定
						
						break;}
					case DIARY_REMOVE:{
						deleteDiaryRequest request=new deleteDiaryRequest();
						request.diaryids = map.get("diaryids")==null?"":map.get("diaryids").toString();
						request.userid = map.get("userid")==null?"":map.get("userid").toString();
						request.mac = Requester2.VALUE_MAC;
						request.imei = Requester2.VALUE_IMEI;
						taskData.request=request;
						break;}
					case FANS_REMOVE:{
						cancelattentionRequest request=new cancelattentionRequest();
						request.target_userid = map.get("target_userid")==null?"":map.get("target_userid").toString();
						request.attention_type = map.get("attention_type")==null?"":map.get("attention_type").toString();
						request.userid = map.get("userid")==null?"":map.get("userid").toString();
						request.mac = Requester2.VALUE_MAC;
						request.imei = Requester2.VALUE_IMEI;
						taskData.request=request;
						break;}
					case GET_DIARY_SHARE_URL:{
						ThirdPartyRequest request=new ThirdPartyRequest();
						request.content=map.get("content")==null?"":map.get("content").toString();
						request.positionInfo=map.get("positionInfo")==null?"":map.get("positionInfo").toString();
						request.userList=map.get("userList")==null?"":map.get("userList").toString();
						request.diaryid=map.get("diaryid")==null?"":map.get("diaryid").toString();
						request.shareType=map.get("shareType")==null?1:(int)Float.parseFloat(map.get("shareType").toString());
						request.position=map.get("position")==null?"":map.get("position").toString();
						request.diaryid=map.get("diaryid")==null?"":map.get("diaryid").toString();
						request.longitude=map.get("longitude")==null?"":map.get("longitude").toString();
						request.latitrde=map.get("latitrde")==null?"":map.get("latitrde").toString();
						taskData.request=request;
						break;}
					case POSITION_AND_TAG:{
						modTagsOrPositionRequest request=new modTagsOrPositionRequest();
						request.diaryid = map.get("diaryid")==null?"":map.get("diaryid").toString();
						request.tags = map.get("tags")==null?"":map.get("tags").toString();
						request.position = map.get("position")==null?"":map.get("position").toString();
						request.userid = map.get("userid")==null?"":map.get("userid").toString();
						request.mac = Requester2.VALUE_MAC;
						request.imei = Requester2.VALUE_IMEI;
						taskData.request=request;
						break;}
					case PRIVATE_MSG_AUDIO_UPLOAD:{
						FileUploadInfo request=new FileUploadInfo();
						request.id=map.get("id")==null?"":map.get("id").toString();
						request.uuid=map.get("uuid")==null?"":map.get("uuid").toString();
						request.ip=map.get("ip")==null?"":map.get("ip").toString();
						request.port=map.get("port")==null?0:(int)Float.parseFloat(map.get("port").toString());
						request.localFilePath=map.get("localFilePath")==null?"":map.get("localFilePath").toString();
						request.uploadPath=map.get("uploadPath")==null?"":map.get("uploadPath").toString();
						request.target_userids=map.get("target_userids")==null?"":map.get("target_userids").toString();
						taskData.request=request;
						break;}
					case SAFEBOX_ADD:{
						safeboxRequest request=new safeboxRequest();
						request.userid = map.get("userid")==null?"":map.get("userid").toString();
						request.diaryid = map.get("diaryid")==null?"":map.get("diaryid").toString();
						request.diaryuuid = map.get("diaryuuid")==null?"":map.get("diaryuuid").toString();
						request.type = map.get("type")==null?"":map.get("type").toString();
						request.mac = Requester2.VALUE_MAC;
						request.imei = Requester2.VALUE_IMEI;
						taskData.request=request;
						break;}
					case SAFEBOX_REMOVE:{
						safeboxRequest request=new safeboxRequest();
						request.userid = map.get("userid")==null?"":map.get("userid").toString();
						request.diaryid = map.get("diaryid")==null?"":map.get("diaryid").toString();
						request.diaryuuid = map.get("diaryuuid")==null?"":map.get("diaryuuid").toString();
						request.type = map.get("type")==null?"":map.get("type").toString();
						request.mac = Requester2.VALUE_MAC;
						request.imei = Requester2.VALUE_IMEI;
						taskData.request=request;
						break;}
					case SEND_PRIVATE_MSG:{
						sendmessageRequest request=new sendmessageRequest();
						request.uuid=map.get("uuid")==null?"":map.get("uuid").toString();
						request.content = map.get("content")==null?"":map.get("content").toString();
						request.target_userids = map.get("target_userids")==null?"":map.get("target_userids").toString();
						request.diaryid =map.get("diaryid")==null?"":map.get("diaryid").toString();
						request.privatemsgtype =map.get("privatemsgtype")==null?"":map.get("privatemsgtype").toString();
						request.userid = map.get("userid")==null?"":map.get("userid").toString();
						request.mac = Requester2.VALUE_MAC;
						request.imei = Requester2.VALUE_IMEI;
						taskData.request=request;
						break;}
					case SHARE_TO_LOOKLOOK:{
						diaryPublishRequest request=new diaryPublishRequest();
						request.diaryid =map.get("diaryid")==null?"":map.get("diaryid").toString();
						request.publish_type = map.get("publish_type")==null?"":map.get("publish_type").toString();
						request.diary_type = map.get("diary_type")==null?"":map.get("diary_type").toString();
						request.position_type = map.get("position_type")==null?"":map.get("position_type").toString();
						request.audio_type = map.get("audio_type")==null?"":map.get("audio_type").toString();
						request.userid = map.get("userid")==null?"":map.get("userid").toString();
						request.mac = Requester2.VALUE_MAC;
						request.imei = Requester2.VALUE_IMEI;
						taskData.request=request;
						break;}
					case SHARE_TO_RENREN:{
						ThirdPartyRequest request=new ThirdPartyRequest();
						request.content=map.get("content")==null?"":map.get("content").toString();
						request.urlContent=map.get("urlContent")==null?"":map.get("urlContent").toString();
						request.picUrl=map.get("picUrl")==null?"":map.get("picUrl").toString();
						request.diaryid=map.get("diaryid")==null?"":map.get("diaryid").toString();
						request.position=map.get("position")==null?"":map.get("position").toString();
						request.diaryid=map.get("diaryid")==null?"":map.get("diaryid").toString();
						request.longitude=map.get("longitude")==null?"":map.get("longitude").toString();
						request.latitrde=map.get("latitrde")==null?"":map.get("latitrde").toString();
						taskData.request=request;
						break;}
					case SHARE_TO_SINA:{
						ThirdPartyRequest request=new ThirdPartyRequest();
						request.content=map.get("content")==null?"":map.get("content").toString();
						request.urlContent=map.get("urlContent")==null?"":map.get("urlContent").toString();
						request.picUrl=map.get("picUrl")==null?"":map.get("picUrl").toString();
						request.diaryid=map.get("diaryid")==null?"":map.get("diaryid").toString();
						request.position=map.get("position")==null?"":map.get("userid").toString();
						request.diaryid=map.get("diaryid")==null?"":map.get("diaryid").toString();
						request.longitude=map.get("longitude")==null?"":map.get("userid").toString();
						request.latitrde=map.get("latitrde")==null?"":map.get("latitrde").toString();
						taskData.request=request;
						break;}
					case SHARE_TO_TENCENT:{
						ThirdPartyRequest request=new ThirdPartyRequest();
						request.content=map.get("content")==null?"":map.get("content").toString();
						request.urlContent=map.get("urlContent")==null?"":map.get("urlContent").toString();
						request.picUrl=map.get("picUrl")==null?"":map.get("picUrl").toString();
						request.diaryid=map.get("diaryid")==null?"":map.get("diaryid").toString();
						request.position=map.get("position")==null?"":map.get("userid").toString();
						request.diaryid=map.get("diaryid")==null?"":map.get("diaryid").toString();
						request.longitude=map.get("longitude")==null?"":map.get("userid").toString();
						request.latitrde=map.get("latitrde")==null?"":map.get("latitrde").toString();
						taskData.request=request;
						break;}
					case SHARE_TRACE_UPLOAD:{
						shareDiaryRequest request=new shareDiaryRequest();
						request.userid = ActiveAccount.getInstance(ZApplication.getInstance())
								.getLookLookID();
						request.diaryid = map.get("diaryid")==null?"":map.get("diaryid").toString();
						request.position = map.get("position")==null?"":map.get("position").toString();
						request.longitude = map.get("longitude")==null?"":map.get("longitude").toString();
						request.latitude = map.get("latitude")==null?"":map.get("latitude").toString();
						request.snscontent = map.get("snscontent")==null?"":map.get("snscontent").toString();
						request.sns = map.get("sns")==null?null:(SNS[])map.get("sns");
						request.mac = Requester2.VALUE_MAC;
						request.imei = Requester2.VALUE_IMEI;
						taskData.request=request;
						break;}
						
					default:
						break;
					}
				}
			}
		}
	
	/**
	 * 添加 加入保险箱 任务
	 */
	public void addSafeboxAddTask(String diaryid, String diaryuuid){
		Prompt.Alert("已加入保险箱任务队列");
		GsonRequest2.safeboxRequest request = new GsonRequest2.safeboxRequest();
		request.userid = ActiveAccount.getInstance(ZApplication.getInstance())
				.getLookLookID();
		request.diaryid = diaryid;
		request.diaryuuid = diaryuuid;
		request.type = "1";
		request.mac = Requester2.VALUE_MAC;
		request.imei = Requester2.VALUE_IMEI;
		TaskData taskData=new TaskData();
		taskData.taskType=TaskType.SAFEBOX_ADD;
		taskData.diaryuuid=diaryuuid;
		taskData.request=request;
		judgeContains(taskData);
		start(0);
	}
	
	/**
	 * 添加 移出保险箱 任务
	 */
	public void addSafeboxRemoveTask(String diaryid, String diaryuuid){
		Prompt.Alert("已加入移除保险箱任务队列");
		GsonRequest2.safeboxRequest request = new GsonRequest2.safeboxRequest();
		request.userid = ActiveAccount.getInstance(ZApplication.getInstance())
				.getLookLookID();
		request.diaryid = diaryid;
		request.diaryuuid = diaryuuid;
		request.type = "2";
		request.mac = Requester2.VALUE_MAC;
		request.imei = Requester2.VALUE_IMEI;
		TaskData taskData=new TaskData();
		taskData.taskType=TaskType.SAFEBOX_REMOVE;
		taskData.diaryuuid=diaryuuid;
		taskData.request=request;
		judgeContains(taskData);
		start(0);
	}
	
	/**
	 * 添加 日记文件上传 任务
	 */
	public void addDiaryMediaUploadTask(ArrayList<FileUploadInfo> uploadInfos){
		TaskData taskData=new TaskData();
		taskData.taskType=TaskType.DIARY_MEDIA_UPLOAD;
		taskData.request=uploadInfos.toArray(new FileUploadInfo[uploadInfos.size()]);
		judgeContains(taskData);
		start(0);
	}
	
	/**
	 * 添加 私信语音文件上传 任务
	 */
	protected void addPrivateMsgAudioUploadTask(String ip,
			int port, String localFilePath,String uploadPath,String privateMsgId,String uuid,String target_userids,String sourceID){
		FileUploadInfo uploadInfo=new FileUploadInfo();
		uploadInfo.id=privateMsgId;
		uploadInfo.uuid=uuid;
		uploadInfo.ip=ip;
		uploadInfo.port=port;
		uploadInfo.localFilePath=localFilePath;
		uploadInfo.uploadPath=uploadPath;
		uploadInfo.target_userids=target_userids;
		TaskData taskData=new TaskData();
		taskData.taskType=TaskType.PRIVATE_MSG_AUDIO_UPLOAD;
		taskData.request=uploadInfo;
		taskData.sourceID=sourceID;
		judgeContains(taskData);
		start(0);
	}
	
	/**
	 * 添加 分享轨迹上传 任务
	 */
	protected void addShareTraceUploadTask(String diaryid,
			String position, String snscontent,String longitude,String latitrde, SNS[] sns){
		GsonRequest2.shareDiaryRequest request = new GsonRequest2.shareDiaryRequest();
		request.userid = ActiveAccount.getInstance(ZApplication.getInstance())
				.getLookLookID();
		request.diaryid = diaryid;
		request.position = position;
		request.longitude = longitude;
		request.latitude = latitrde;
		request.snscontent = snscontent;
		request.sns = sns;
		request.mac = Requester2.VALUE_MAC;
		request.imei = Requester2.VALUE_IMEI;
		TaskData taskData=new TaskData();
		taskData.taskType=TaskType.SHARE_TRACE_UPLOAD;
		taskData.request=request;
		judgeContains(taskData);
		start(0);
	}
	
	/**
	 * 添加 获取日记分享URL 任务
	 */
	public void addGetDiaryShareUrlTask(String shareContent,String positionInfo,String userList,String diaryid,String diaryuuid, String position, String longitude, String latitrde,String diaryUserID,int shareType){
		Prompt.Alert("已加入日记分享任务队列");
		ThirdPartyRequest request=new ThirdPartyRequest();
		request.content=shareContent;
		request.positionInfo=positionInfo;
		request.userList=userList;
		request.diaryid=diaryid;
		request.shareType=shareType;
		request.position=position;
		request.diaryid=diaryid;
		request.longitude=longitude;
		request.latitrde=latitrde;
		TaskData taskData=new TaskData();
		taskData.taskType=TaskType.GET_DIARY_SHARE_URL;
		taskData.diaryuuid=diaryuuid;
		taskData.diaryUserID=diaryUserID;
		taskData.request=request;
		judgeContains(taskData);
		start(0);
	}
	
	/**
	 * 添加 人人分享 任务
	 */
	public void addShareToRenren(String content,String urlContent,String picUrl,String diaryid,String diaryuuid, String position, String longitude, String latitrde,String diaryUserID){
		Prompt.Alert("已加入人人分享任务队列");
		ThirdPartyRequest request=new ThirdPartyRequest();
		request.content=content;
		request.urlContent=urlContent;
		request.picUrl=picUrl;
		request.diaryid=diaryid;
		request.position=position;
		request.diaryid=diaryid;
		request.longitude=longitude;
		request.latitrde=latitrde;
		TaskData taskData=new TaskData();
		taskData.taskType=TaskType.SHARE_TO_RENREN;
		taskData.diaryuuid=diaryuuid;
		taskData.diaryUserID=diaryUserID;
		taskData.request=request;
		judgeContains(taskData);
		start(0);
	}
	/**
	 * 添加 腾讯分享 任务
	 */
	public void addShareToTencent(String content,String urlContent,String picUrl,String diaryid,String diaryuuid, String position, String longitude, String latitrde,String diaryUserID){
		Prompt.Alert("已加入腾讯分享任务队列");
		ThirdPartyRequest request=new ThirdPartyRequest();
		request.content=content;
		request.urlContent=urlContent;
		request.picUrl=picUrl;
		request.diaryid=diaryid;
		request.position=position;
		request.diaryid=diaryid;
		request.longitude=longitude;
		request.latitrde=latitrde;
		TaskData taskData=new TaskData();
		taskData.taskType=TaskType.SHARE_TO_TENCENT;
		taskData.diaryuuid=diaryuuid;
		taskData.diaryUserID=diaryUserID;
		taskData.request=request;
		judgeContains(taskData);
		start(0);
	}
	/**
	 * 添加 新浪分享 任务
	 */
	public void addShareToSina(String content,String urlContent,String picUrl,String diaryid,String diaryuuid, String position, String longitude, String latitrde,String diaryUserID){
		Prompt.Alert("已加入新浪分享任务队列");
		ThirdPartyRequest request=new ThirdPartyRequest();
		request.content=content;
		request.urlContent=urlContent;
		request.picUrl=picUrl;
		request.diaryid=diaryid;
		request.position=position;
		request.diaryid=diaryid;
		request.longitude=longitude;
		request.latitrde=latitrde;
		TaskData taskData=new TaskData();
		taskData.taskType=TaskType.SHARE_TO_SINA;
		taskData.diaryuuid=diaryuuid;
		taskData.diaryUserID=diaryUserID;
		taskData.request=request;
		judgeContains(taskData);
		start(0);
	}
	/**
	 * 添加 站内分享 任务
	 */
	public void addShareToLookLook(String diaryid,String diaryuuid,String diary_type, String position_type,
			String audio_type,String publish_type){
		Prompt.Alert("已加入站内分享任务队列");
		GsonRequest2.diaryPublishRequest request = new GsonRequest2.diaryPublishRequest();
		request.diaryid = diaryid;// "3390";
		request.publish_type = publish_type;// 发布类型，1发布，2取消发布   （3.0没有取消发布类型）
		request.diary_type = diary_type;// "1";
		request.position_type = position_type;// "1";
		request.audio_type = audio_type;// "1";
		request.userid = ActiveAccount.getInstance(ZApplication.getInstance())
				.getLookLookID();
		request.mac = Requester2.VALUE_MAC;
		request.imei = Requester2.VALUE_IMEI;
		TaskData taskData=new TaskData();
		taskData.taskType=TaskType.SHARE_TO_LOOKLOOK;
		taskData.diaryuuid=diaryuuid;
		taskData.request=request;
		judgeContains(taskData);
		start(0);
	}
	/**
	 * 添加 发送私信 任务
	 */
	public void addSendPrivateMsgTask(String diaryid,String diaryuuid,
			String content, String targetUserIds, String privatemsgtype, String uuid,String diaryUserID){
		Prompt.Alert("已加入发送私信任务队列");
		GsonRequest2.sendmessageRequest request = new GsonRequest2.sendmessageRequest();
		request.uuid=uuid;
		request.content = content;
		request.target_userids = targetUserIds;
		request.diaryid = diaryid;
		request.privatemsgtype = privatemsgtype;
		request.mac = Requester2.VALUE_MAC;
		request.imei = Requester2.VALUE_IMEI;
		request.userid = ActiveAccount.getInstance(ZApplication.getInstance())
				.getLookLookID();
		TaskData taskData=new TaskData();
		taskData.taskType=TaskType.SEND_PRIVATE_MSG;
		taskData.diaryuuid=diaryuuid;
		taskData.diaryUserID=diaryUserID;
		taskData.request=request;
		taskData.createTime=TimeHelper.getInstance().now();
		judgeContains(taskData);
		start(0);
	}
	/**
	 * 添加 删除黑名单 任务
	 */
	public void addBlackRemoveTask(String target_userid,
			String operatetype){
		Prompt.Alert("已加入黑名单删除任务队列");
		GsonRequest2.operateblacklistRequest request = new GsonRequest2.operateblacklistRequest();
		request.target_userid = target_userid;
		request.operatetype = "2";
		request.mac = Requester2.VALUE_MAC;
		request.imei = Requester2.VALUE_IMEI;
		request.userid = ActiveAccount.getInstance(ZApplication.getInstance())
				.getLookLookID();
		TaskData taskData=new TaskData();
		taskData.taskType=TaskType.BLACK_REMOVE;
		taskData.request=request;
		judgeContains(taskData);
		start(0);
	}
	/**
	 * 添加 删除关注 任务
	 */
	public void addAttendedRemoveTask(String target_userid){
		Prompt.Alert("已加入关注删除任务队列");
		GsonRequest2.cancelattentionRequest request = new GsonRequest2.cancelattentionRequest();
		request.target_userid = target_userid;
		request.attention_type = "1";
		request.mac = Requester2.VALUE_MAC;
		request.imei = Requester2.VALUE_IMEI;
		request.userid = ActiveAccount.getInstance(ZApplication.getInstance())
				.getLookLookID();
		TaskData taskData=new TaskData();
		taskData.taskType=TaskType.ATTENDED_REMOVE;
		taskData.request=request;
		judgeContains(taskData);
		start(0);
	}
	/**
	 * 添加 删除粉丝 任务
	 */
	public void addfansRemoveTask(String target_userid){
		Prompt.Alert("已加入粉丝删除任务队列");
		GsonRequest2.cancelattentionRequest request = new GsonRequest2.cancelattentionRequest();
		request.target_userid = target_userid;
		request.attention_type = "2";
		request.mac = Requester2.VALUE_MAC;
		request.imei = Requester2.VALUE_IMEI;
		request.userid = ActiveAccount.getInstance(ZApplication.getInstance())
				.getLookLookID();
		TaskData taskData=new TaskData();
		taskData.taskType=TaskType.FANS_REMOVE;
		taskData.request=request;
		judgeContains(taskData);
		start(0);
	}
	/**
	 * 添加 删除日记 任务
	 */
	public void addDiaryRemoveTask(String diaryids){
		Prompt.Alert("已加入日记删除任务队列");
		GsonRequest2.deleteDiaryRequest request = new GsonRequest2.deleteDiaryRequest();
		request.diaryids = diaryids;
		request.changetomaindiaryuuids="-1";//已此通知服务器查询副本顶主本
		request.mac = Requester2.VALUE_MAC;
		request.imei = Requester2.VALUE_IMEI;
		request.userid = ActiveAccount.getInstance(ZApplication.getInstance())
				.getLookLookID();
		TaskData taskData=new TaskData();
		taskData.taskType=TaskType.DIARY_REMOVE;
		taskData.request=request;
		judgeContains(taskData);
		start(0);
	}
	/**
	 * 添加 修改位置或（和）标签任务
	 */
	public void addPositionOrTagTask(String diaryid,String diaryuuid,
			String tags, String position){
		GsonRequest2.modTagsOrPositionRequest request = new GsonRequest2.modTagsOrPositionRequest();
		request.diaryid = diaryid;
		request.tags = tags;
		request.position = position;
		request.userid = ActiveAccount.getInstance(ZApplication.getInstance())
				.getLookLookID();
		request.mac = Requester2.VALUE_MAC;
		request.imei = Requester2.VALUE_IMEI;
		TaskData taskData=new TaskData();
		taskData.taskType=TaskType.POSITION_AND_TAG;
		taskData.diaryuuid=diaryuuid;
		taskData.request=request;
		judgeContains(taskData);
		start(0);
	}
	
	/**
	 * 添加 加入收藏任务
	 */
	public void addCollectAddTask(String diaryid){
		Prompt.Alert("已加入收藏任务队列");
		GsonRequest2.addCollectDiaryRequest request = new GsonRequest2.addCollectDiaryRequest();
		request.diaryid = diaryid;
		request.userid = ActiveAccount.getInstance(ZApplication.getInstance())
				.getLookLookID();
		request.mac = Requester2.VALUE_MAC;
		request.imei = Requester2.VALUE_IMEI;
		TaskData taskData=new TaskData();
		taskData.taskType=TaskType.COLLECT_ADD;
		taskData.request=request;
		judgeContains(taskData);
		start(0);
	}
	
	/**
	 * 添加 移除收藏任务
	 */
	public void addCollectRemoveTask(String diaryid){
		Prompt.Alert("已加入收藏移除任务队列");
		GsonRequest2.removeCollectDiaryRequest request = new GsonRequest2.removeCollectDiaryRequest();
		request.diaryids = diaryid;
		request.userid = ActiveAccount.getInstance(ZApplication.getInstance())
				.getLookLookID();
		request.mac = Requester2.VALUE_MAC;
		request.imei = Requester2.VALUE_IMEI;
		TaskData taskData=new TaskData();
		taskData.taskType=TaskType.COLLECT_CANCEL;
		taskData.request=request;
		judgeContains(taskData);
		start(0);
	}
	
	/**
	 * 添加 增加评论任务
	 */
	public void addCommentAddTask(String commentContent,
			String commentId, String isReply, String diaryid,String diaryuuid, String commentType, String commentuuid){
		Prompt.Alert("已加入评论任务队列");
		GsonRequest2.commentRequest request = new GsonRequest2.commentRequest();
		request.commentcontent = commentContent;
		request.commentid = commentId;
		request.isreply = isReply;
		request.userid = ActiveAccount.getInstance(ZApplication.getInstance())
				.getLookLookID();
		request.diaryid = diaryid;
		request.commenttype = commentType;
		request.commentuuid = commentuuid;
		request.mac = Requester2.VALUE_MAC;
		request.imei = Requester2.VALUE_IMEI;
		TaskData taskData=new TaskData();
		taskData.taskType=TaskType.COMMENT_ADD;
		taskData.diaryuuid=diaryuuid;
		taskData.request=request;
		judgeContains(taskData);
		start(0);
	}
	
	public void addCommentDeleteTask(String diaryid,String diaryuuid,
			String commentid, String commentuuid){
		Prompt.Alert("已加入评论删除任务队列");
		GsonRequest2.deleteCommentRequest request = new GsonRequest2.deleteCommentRequest();
		request.diaryid = diaryuuid;
		request.commentid = commentid;
		request.commentuuid = commentuuid;
		request.mac = Requester2.VALUE_MAC;
		request.imei = Requester2.VALUE_IMEI;
		request.userid = ActiveAccount.getInstance(ZApplication.getInstance())
				.getLookLookID();
		TaskData taskData=new TaskData();
		taskData.taskType=TaskType.COMMENT_DELETE;
		taskData.diaryuuid=diaryuuid;
		taskData.diaryID=diaryuuid;
		taskData.request=request;
		judgeContains(taskData);
		start(0);
	}
	
	/**
	 * 添加 参加活动任务
	 */
	public void addActiveAttendTask(String diaryid,String diaryuuid,
			String activeid){
		GsonRequest2.joinActiveRequest request = new GsonRequest2.joinActiveRequest();
		request.userid = ActiveAccount.getInstance(ZApplication.getInstance())
				.getLookLookID();
		request.diaryid = diaryid;
		request.activeid = activeid;
		request.mac = Requester2.VALUE_MAC;
		request.imei = Requester2.VALUE_IMEI;
		TaskData taskData=new TaskData();
		taskData.taskType=TaskType.ACTIVE_ATTEND;
		taskData.diaryuuid=diaryuuid;
		taskData.request=request;
		activeAddJudgeContains(taskData);
		start(0);
	}
	
	/**
	 * 添加 取消活动任务
	 */
	public void addActiveCancelTask(String diaryid,String diaryuuid, String activeid){
		GsonRequest2.cancleActiveRequest request = new GsonRequest2.cancleActiveRequest();
		request.userid = ActiveAccount.getInstance(ZApplication.getInstance()).getLookLookID();
		request.diaryid = diaryid;
		request.activeid = activeid;
		request.mac = Requester2.VALUE_MAC;
		request.imei = Requester2.VALUE_IMEI;
		TaskData taskData=new TaskData();
		taskData.taskType=TaskType.ACTIVE_CANCEL;
		taskData.diaryuuid=diaryuuid;
		taskData.request=request;
		activeCancelJudgeContains(taskData);
		start(0);
	}
	private boolean activeCancelJudgeContains(TaskData taskData) {
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
	}
	private boolean judgeContains(TaskData taskData){
		//TODO 判断是否存在相同任务,可根据任务类型具体到与该任务逻辑相关的操作
		taskList.add(taskData);
		return false;
	}
	
	/**
	 * 判断任务队列中是否存在根据diaryID指定的日记的站内分享任务
	 * true-存在
	 * false 不存在
	 */
	public boolean hasShareToLooklookTask(String diaryUUID){
		MyDiary myDiary=DiaryManager.getInstance().findLocalDiaryByUuid(diaryUUID);
		if(myDiary!=null){
			for(int i=0;i<taskList.size();i++){
				TaskData taskData =taskList.get(i);
				if(taskData.taskType==TaskType.SHARE_TO_LOOKLOOK&& taskData.request instanceof diaryPublishRequest&&diaryUUID.equals(taskData.diaryuuid)){
					return true;
				}
			}
		}
		return false;
	}
	
	/**
	 * 根据日记uuid删除该日记站内分享任务
	 */
	public void removeShareToLooklookTask(String diaryUUID){
		MyDiary myDiary=DiaryManager.getInstance().findLocalDiaryByUuid(diaryUUID);
		if(myDiary!=null){
			for(int i=0;i<taskList.size();i++){
				TaskData taskData =taskList.get(i);
				if(taskData.taskType==TaskType.SHARE_TO_LOOKLOOK&& taskData.request instanceof diaryPublishRequest&&diaryUUID.equals(taskData.diaryuuid)){
					removeByID(taskData.id);
				}
			}
			myDiary.diary_status="1";
		}
	}
	
	/**
	 * 启动任务管理器
	 */
	public void start(long delay){
		if(!hasTaskRunning()&&ZNetworkStateDetector.isAvailable()
				&& ZNetworkStateDetector.isConnected()){
			runningTask=getTask();
			if(runningTask!=null){
				runningTask.isRunning=true;
				runningTask.setTaskLister(this);
				runningTask.startDelay(delay);
			}
		}
	}
	
	//获取任务列表中的第一个任务
	private IOfflineTask getTask(){
		synchronized (taskList) {
			if(taskList.size()>0){
				TaskData taskData=taskList.get(0);
				//更新任务的日记ID
				if(updateTaskDataDiaryID(taskData)){
					removeByID(taskData.id);
					return null;
				}
				//可根据任务类型定义不同类型任务
				IOfflineTask task=null;
				switch (taskData.taskType) {
				case SEND_PRIVATE_MSG:
					//当私信类型为日记私信时，判断日记是否已同步，如果没有，将该任务放到队列尾部并跳出
					if("3".equals(((sendmessageRequest)taskData.request).privatemsgtype)){
						//如果是自己的日记，判断是否已同步，如果是别人的日记,不用判断
						if(taskData.diaryUserID!=null&&taskData.diaryUserID.length()>0&&!taskData.diaryUserID.equals(DiaryManager.getInstance().getMyUserID())){
							task=new SendPrivateMsgTask();
						}else if(isSync(taskData)){
							task=new SendPrivateMsgTask();
						}else{
							taskList.remove(taskData);
							taskList.add(taskData);
							Log.d(TAG, "SEND_PRIVATE_MSG 日记还未同步成功 日记私信排后");
							Log.d(TAG, "SEND_PRIVATE_MSG 将该任务放到队列尾部并跳出 "+taskData.toString());
						}
					}else{
						task=new SendPrivateMsgTask();
					}
					break;
				case PRIVATE_MSG_AUDIO_UPLOAD:
					task=new FileUploadTask();
					break;
				case SHARE_TO_LOOKLOOK:
					//检测该日记是否已经同步成功，如果没有同步，将该任务放到队列尾部并跳出
					if(isSync(taskData)){
						task=new SimpleCMDTask();
						//如果该任务设置日记权限为不公开，则取消任务队列中第三方分享、参加活动任务
						if(isPublic(taskData)){
							removeSharedToThirdPartyTask(taskData);
							removeActiveAttendTask(taskData);
						}
					}else{
						taskList.remove(taskData);
						taskList.add(taskData);
						Log.d(TAG, "SHARE_TO_LOOKLOOK 将该任务放到队列尾部并跳出 "+taskData.toString());
					}
					break;
				case ACTIVE_ATTEND:
					//检测该日记是否已经同步成功、是否已站内分享，如果没有，将该任务放到队列尾部并跳出
					if(isSync(taskData)&&isSharedToLooklook(taskData)){
						task=new SimpleCMDTask();
					}else{
						taskList.remove(taskData);
						taskList.add(taskData);
						Log.d(TAG, "ACTIVE_ATTEND 将该任务放到队列尾部并跳出 "+taskData.toString());
					}
					break;
				case GET_DIARY_SHARE_URL:
					//检测该日记是否已经同步成功、是否已站内分享，如果没有，将该任务放到队列尾部并跳出
					if(taskData.diaryUserID!=null&&taskData.diaryUserID.length()>0&&!taskData.diaryUserID.equals(DiaryManager.getInstance().getMyUserID())){
						task=new SimpleCMDTask();
						getDiaryUrlRequest request=new getDiaryUrlRequest();
						request.diaryid=taskData.diaryID;
						request.imei=Requester2.VALUE_IMEI;
						request.mac=Requester2.VALUE_MAC;
						task.id=taskData.id;
						task.taskType=taskData.taskType;
						task.request=request;
						task.diaryID=taskData.diaryID;
						task.diaryuuid=taskData.diaryuuid;
						task.createTime=taskData.createTime;
						task.context=context;
						return task;
					}else if(isSync(taskData)&&isSharedToLooklook(taskData)){
						task=new SimpleCMDTask();
						getDiaryUrlRequest request=new getDiaryUrlRequest();
						request.diaryid=taskData.diaryID;
						request.imei=Requester2.VALUE_IMEI;
						request.mac=Requester2.VALUE_MAC;
						task.id=taskData.id;
						task.taskType=taskData.taskType;
						task.request=request;
						task.diaryID=taskData.diaryID;
						task.diaryuuid=taskData.diaryuuid;
						task.createTime=taskData.createTime;
						task.context=context;
						return task;
					}else{
						taskList.remove(taskData);
						taskList.add(taskData);
						Log.d(TAG, "GET_DIARY_SHARE_URL 将该任务放到队列尾部并跳出 "+taskData.toString());
					}
				case SHARE_TO_RENREN:
				case SHARE_TO_TENCENT:
				case SHARE_TO_SINA:
					//检测该日记是否已经同步成功、是否已站内分享，如果没有，将该任务放到队列尾部并跳出
					if(taskData.diaryUserID!=null&&taskData.diaryUserID.length()>0&&!taskData.diaryUserID.equals(DiaryManager.getInstance().getMyUserID())){
						task=new ThirdPartyShareTask();
					}else if(isSync(taskData)&&isSharedToLooklook(taskData)){
						task=new ThirdPartyShareTask();
					}else{
						taskList.remove(taskData);
						taskList.add(taskData);
						Log.d(TAG, "SHARE_TO_THIRDPARTY 将该任务放到队列尾部并跳出 "+taskData.toString());
					}
					break;
				/*case COMMENT_ADD:
					task=new SimpleCMDTask();
					commentRequest request=new commentRequest();
					request.diaryid=taskData.diaryID;
					request.imei=Requester2.VALUE_IMEI;
					request.mac=Requester2.VALUE_MAC;
					task.id=taskData.id;
					task.taskType=taskData.taskType;
					task.request=request;
					task.diaryID=taskData.diaryID;
					task.createTime=taskData.createTime;
					task.context=context;
					break;
				case COMMENT_DELETE:
					task=new SimpleCMDTask();
					deleteCommentRequest drequest=new deleteCommentRequest();
					drequest.diaryid=taskData.diaryID;
					drequest.imei=Requester2.VALUE_IMEI;
					drequest.mac=Requester2.VALUE_MAC;
					task.id=taskData.id;
					task.taskType=taskData.taskType;
					task.request=drequest;
					task.diaryID=taskData.diaryID;
					task.createTime=taskData.createTime;
					task.context=context;
					break;*/
				default://默认使用简单命令任务类型
					task=new SimpleCMDTask();
					break;
				}
				if(task!=null){
					task.id=taskData.id;
					task.taskType=taskData.taskType;
					task.request=taskData.request;
					task.diaryID=taskData.diaryID;
					task.diaryuuid=taskData.diaryuuid;
					task.createTime=taskData.createTime;
					task.context=context;
				}
				return task;
			}
			Log.d(TAG, "getTask->taskList is empty");
			return null;
		}
	}
	
	//当任务日记ID与实际日记ID不一致时，更新任务日记ID
	private boolean updateTaskDataDiaryID(TaskData taskData){
		if(taskData!=null&&taskData.diaryuuid!=null&&taskData.diaryuuid.length()>0){
			MyDiary myDiary=DiaryManager.getInstance().findLocalDiaryByUuid(taskData.diaryuuid);
			if(null==myDiary){
				Log.e(TAG, "updateTaskDataDiaryID->日记未找到，应该删除此条任务");
//				return true;
			}else{
				//当任务日记id等于日记uuid并且日记uuid与日记id不相等时，更新任务日记id
				if(myDiary.diaryuuid.equals(taskData.diaryuuid)&&!myDiary.diaryuuid.equals(myDiary.diaryid)){
					taskData.diaryID=myDiary.diaryid;
					switch (taskData.taskType) {
					case ACTIVE_ATTEND:
						((joinActiveRequest)taskData.request).diaryid=myDiary.diaryid;
						break;
					case ACTIVE_CANCEL:
						((cancleActiveRequest)taskData.request).diaryid=myDiary.diaryid;
						break;
					case SAFEBOX_ADD:
						((safeboxRequest)taskData.request).diaryid=myDiary.diaryid;
						break;
					case SAFEBOX_REMOVE:
						((safeboxRequest)taskData.request).diaryid=myDiary.diaryid;
						break;
					case SHARE_TRACE_UPLOAD:
						((shareDiaryRequest)taskData.request).diaryid=myDiary.diaryid;
						break;
					case SHARE_TO_RENREN:
					case SHARE_TO_SINA:
					case SHARE_TO_TENCENT:
						((ThirdPartyRequest)taskData.request).diaryid=myDiary.diaryid;
						break;
					case SHARE_TO_LOOKLOOK:
						((diaryPublishRequest)taskData.request).diaryid=myDiary.diaryid;
						break;
					case SEND_PRIVATE_MSG:
						((sendmessageRequest)taskData.request).diaryid=myDiary.diaryid;
						break;
					case POSITION_AND_TAG:
						((modTagsOrPositionRequest)taskData.request).diaryid=myDiary.diaryid;
						break;
					case COMMENT_ADD:
						((commentRequest)taskData.request).diaryid=myDiary.diaryid;
						break;
					case COMMENT_DELETE:
						((deleteCommentRequest)taskData.request).diaryid=myDiary.diaryid;
						break;
					default:
						Log.e(TAG, "unknow tasktype "+taskData.taskType);
						break;
					}
				}
			}
		}
		return false;
	}
	
	//删除活动任务
	private void removeActiveAttendTask(TaskData taskData){
		if(taskData!=null){
			MyDiary myDiary=DiaryManager.getInstance().findLocalDiaryByUuid(taskData.diaryuuid);
			if(null==myDiary){
				Log.e(TAG, "removeActiveAttendTask->日记未找到，应该删除此条任务");
			}else{
				ArrayList<String> taskIDList=new ArrayList<String>();
				for(int i=0;i<taskList.size();i++){
					TaskData data=taskList.get(i);
					if(data.taskType==TaskType.ACTIVE_ATTEND&&(myDiary.diaryuuid.equals(data.diaryuuid))){
						taskIDList.add(data.id);
					}
				}
				for(int i=0;i<taskIDList.size();i++){
					removeByID(taskIDList.get(i));
				}
			}
		}
	}
	
	//删除第三方分享任务
	private void removeSharedToThirdPartyTask(TaskData taskData){
		if(taskData!=null){
			MyDiary myDiary=DiaryManager.getInstance().findLocalDiaryByUuid(taskData.diaryID);
			if(null==myDiary){
				Log.e(TAG, "removeSharedToThirdPartyTask->日记未找到，应该删除此条任务");
			}else{
				ArrayList<String> taskIDList=new ArrayList<String>();
				for(int i=0;i<taskList.size();i++){
					TaskData data=taskList.get(i);
					if((data.taskType==TaskType.SHARE_TO_RENREN||data.taskType==TaskType.SHARE_TO_SINA||data.taskType==TaskType.SHARE_TO_TENCENT)&&(myDiary.diaryuuid.equals(data.diaryuuid))){
						taskIDList.add(data.id);
					}
				}
				for(int i=0;i<taskIDList.size();i++){
					removeByID(taskIDList.get(i));
				}
			}
		}
	}
	
	//判断日记是否是所有人可见
	private boolean isPublic(TaskData taskData){
		if(taskData!=null){
			MyDiary myDiary=DiaryManager.getInstance().findLocalDiaryByUuid(taskData.diaryuuid);
			if(null==myDiary){
				Log.e(TAG, "isPublic->日记未找到，应该删除此条任务");
			}else{
				if("4".equals(myDiary.publish_status)||"2".equals(myDiary.publish_status))
					return false;
			}
		}
		return true;
	}
	
	//判断日记是否已同步
	private boolean isSync(TaskData taskData){
		if(taskData!=null){
			MyDiary myDiary=DiaryManager.getInstance().findLocalDiaryByUuid(taskData.diaryuuid);
			if(null==myDiary){
				//当在本地没有找到时，说明该日记可能是云端的
				return true;
			}else{
				if(myDiary.sync_status>3)
					return true;
			}
		}
		return false;
	}
	
	//判断日记是否已发布
	private boolean isSharedToLooklook(TaskData taskData){
		if(taskData!=null){
			MyDiary myDiary=DiaryManager.getInstance().findLocalDiaryByUuid(taskData.diaryuuid);
			if(null==myDiary){
				Log.e(TAG, "isSharedToLooklook->日记未找到，应该删除此条任务");
			}else{
				//如果日记已发布，检测任务队列是中否存在该日记的发布任务，如果没有，表明该日记已经发布，如果有，说明还没发布成功
				//可能存在的bug 如果该日记已经发布过一次，继续发布第二次，第二次发布任务还未执行，就会出问题
				if("2".equals(myDiary.diary_status)){
					if(!hasShareToLooklookTask(myDiary.diaryuuid))
						return true;
				}
			}
		}
		return false;
	}
	
	//判断当前是否有任务在运行
	private boolean hasTaskRunning(){
		if(runningTask!=null&&runningTask.isRunning){
			Log.d(TAG, "runningtask is "+runningTask.toString());
			return true;
		}
		return false;
	}
	
	private OfflineTaskManager(){}
	
	private static OfflineTaskManager offlineTaskManager;
	public static OfflineTaskManager getInstance(){
		if(null==offlineTaskManager)
			offlineTaskManager=new OfflineTaskManager();
		return offlineTaskManager;
	}
	
	private ActiveAccount aa;
	private AccountInfo ai;
	private PrivateMessageManager pmm;
	private String userID;
	private TimeoutTask timeoutTask; 
	public void init(Context context){
		this.context=context;
		aa = ActiveAccount.getInstance(context);
		ai = AccountInfo.getInstance(aa.getLookLookID());
		userID= ActiveAccount.getInstance(ZApplication.getInstance()).getUID();
		pmm = ai.privateMsgManger;
		String userid=ActiveAccount.getInstance(ZApplication.getInstance()).getUID();
		AccountInfo accountInfo = AccountInfo.getInstance(userid);
		taskList= accountInfo.taskDataList;
		//持久化以后，taskdata.request某些情况下会变成linkedhashmap类
		updateTaskRequest();
		IntentFilter filter = new IntentFilter();
		filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
		filter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
		filter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
		ZApplication.getInstance().getApplicationContext().registerReceiver(new NetConnectedReceiver(), filter);
		//检测一分钟超时移除任务
		if(timeoutTask!=null){
			timeoutTask.cancel();
		}
		timeoutTask=new TimeoutTask(this);
		timeoutTask.start();
	}
	
	
	
	@Override
	public void hasTask() {
		start(0);
	}

	@Override
	public void success(IOfflineTask task) {
		Log.d(TAG, "task success\n"+task.toString());
		task.isRunning=false;
		switch (task.taskType) {
		case SAFEBOX_REMOVE:
			if(isAppOnFront(context))Prompt.Alert("移出保险箱成功");
			break;
		case SAFEBOX_ADD:
			if(isAppOnFront(context))Prompt.Alert("放入保险箱成功");
			break;
		case SHARE_TO_TENCENT:
			if(isAppOnFront(context))Prompt.Alert("分享到腾讯成功");
			break;
		case SHARE_TO_SINA:
			if(isAppOnFront(context))Prompt.Alert("分享到新浪成功");
			break;
		case SHARE_TO_RENREN:
			if(isAppOnFront(context))Prompt.Alert("分享到人人成功");
			break;
		case SHARE_TO_LOOKLOOK:
			if(isAppOnFront(context))Prompt.Alert("分享到looklook成功");
			break;
		case SEND_PRIVATE_MSG:
			setPrivateMsgRes(task,true);
			if("138115".equals(task.failedResult)){
				if(isAppOnFront(context))Prompt.Alert("对方开启隐私设置，无法进行私信");
			}else if("138107".equals(task.failedResult)){
				if(isAppOnFront(context))Prompt.Alert("私信发送成功");
			}else{
				if(isAppOnFront(context))Prompt.Alert("私信发送成功");
			}
			break;
		case PRIVATE_MSG_AUDIO_UPLOAD:
			setPrivateMsgRes(task,true);
			if(isAppOnFront(context))Prompt.Alert("私信发送成功");
			break;
		case BLACK_REMOVE:
			if(isAppOnFront(context))Prompt.Alert("已移出黑名单");
			break;
		case ATTENDED_REMOVE:
			if(isAppOnFront(context))Prompt.Alert("关注取消成功");
			break;
		case FANS_REMOVE:
			if(isAppOnFront(context))Prompt.Alert("粉丝删除成功");
			break;
		case DIARY_REMOVE:
			if(isAppOnFront(context))Prompt.Alert("日记删除成功");
			break;
		case ACTIVE_ATTEND:
			if(isAppOnFront(context))Prompt.Alert("参加活动成功");
			
			Intent msgIntent = new Intent(ActivitiesDetailActivity.FLAG_ACTIVITY_DATA_UPDATE);
			msgIntent.putExtra("activity_update_type", ActivitiesDetailActivity.FLAG_ACTIVITY_SUCESS);
			msgIntent.putExtra("activity_add_cancel", "add");
			
			LocalBroadcastManager.getInstance(context).sendBroadcast(msgIntent);
			
			break;
		case ACTIVE_CANCEL:
			if(isAppOnFront(context))Prompt.Alert("活动取消成功");
			
			Intent cancelIntent = new Intent(ActivitiesDetailActivity.FLAG_ACTIVITY_DATA_UPDATE);
			cancelIntent.putExtra("activity_update_type", ActivitiesDetailActivity.FLAG_ACTIVITY_SUCESS);
			cancelIntent.putExtra("activity_add_cancel", "cancel");
			
			LocalBroadcastManager.getInstance(context).sendBroadcast(cancelIntent);
			break;
		case COLLECT_ADD:
			if(isAppOnFront(context))Prompt.Alert("收藏日记成功");
			break;
		case COLLECT_CANCEL:
			if(isAppOnFront(context))Prompt.Alert("移除收藏成功");
			break;
		case COMMENT_ADD:
			if(isAppOnFront(context))Prompt.Alert("评论添加成功");
			break;
		case COMMENT_DELETE:
			if(isAppOnFront(context))Prompt.Alert("评论删除成功");
			break;
		case POSITION_AND_TAG:
			if(isAppOnFront(context))Prompt.Alert("标签(位置)修改成功");
			DiaryManager diarymanager = DiaryManager.getInstance();
			MyDiary myLocalDiary = diarymanager.findLocalDiary(task.diaryID);
			if (myLocalDiary !=  null) {
				Log.d(TAG,"save myLocalDiary not null");
				GsonRequest2.modTagsOrPositionRequest request = (GsonRequest2.modTagsOrPositionRequest)task.request;
				myLocalDiary.position = request.position;
				myLocalDiary.setTags(request.tags);
				DiaryManager.getInstance().diaryDataChanged(myLocalDiary.diaryuuid);
			}
			break;
		default:
			break;
		}
		removeByID(task.id);
		start(0);
	}

	@Override
	public void failed(IOfflineTask task) {
		Log.e(TAG, "task failed");
		runningTask=null;
		TaskData taskData=getTaskData(task.id);
		if(taskData!=null){
			Log.e(TAG, taskData.toString());
			if(taskData.retryCount>=MAX_RETRY_COUNT){
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
				case PRIVATE_MSG_AUDIO_UPLOAD:
					setPrivateMsgRes(task,false);
					break;
				case BLACK_REMOVE:
					break;
				case ATTENDED_REMOVE:
					break;
				case FANS_REMOVE:
					break;
				case DIARY_REMOVE:
					break;
				case ACTIVE_ATTEND:
					if(isAppOnFront(context))Prompt.Alert("参加活动失败");
					Intent msgIntent = new Intent(ActivitiesDetailActivity.FLAG_ACTIVITY_DATA_UPDATE);
					msgIntent.putExtra("activity_update_type", ActivitiesDetailActivity.FLAG_ACTIVITY_FAIL);
					msgIntent.putExtra("activity_add_cancel", "add");
					LocalBroadcastManager.getInstance(context).sendBroadcast(msgIntent);
					break;
				case ACTIVE_CANCEL:
					
					if(isAppOnFront(context))Prompt.Alert("活动取消失败");
					
					Intent cancelIntent = new Intent(ActivitiesDetailActivity.FLAG_ACTIVITY_DATA_UPDATE);
					cancelIntent.putExtra("activity_update_type", ActivitiesDetailActivity.FLAG_ACTIVITY_SUCESS);
					cancelIntent.putExtra("activity_add_cancel", "cancel");
					LocalBroadcastManager.getInstance(context).sendBroadcast(cancelIntent);
					break;
				case COLLECT_ADD:
					break;
				case COLLECT_CANCEL:
					break;
				case COMMENT_ADD:
					if(isAppOnFront(context))Prompt.Alert("添加评论失败");
					break;
				case COMMENT_DELETE:					
					if(isAppOnFront(context))Prompt.Alert("删除评论失败");
					break;
				case POSITION_AND_TAG:
					break;
				default:
					break;
				}
				task.isRunning=false;
				removeByID(task.id);
			}else{
				taskData.retryCount++;
			}
			addLast(task);
		}else{
			Log.e(TAG, "task does not exit");
		}
	}

	@Override
	public void exception(IOfflineTask task) {
		Log.e(TAG, "task exception");
		task.isRunning=false;
		runningTask=null;
		TaskData taskData=getTaskData(task.id);
		if(taskData!=null){
			Log.e(TAG, taskData.toString());
			if(taskData.retryCount>=MAX_RETRY_COUNT){
				removeByID(task.id);
			}else{
				taskData.retryCount++;
			}
			addLast(task);
		}else{
			Log.e(TAG, "task does not exit");
		}
	}
	
	//获取任务
	public TaskData getTaskData(String taskID){
		if(taskID!=null){
			for(int i=0;i<taskList.size();i++){
				TaskData taskData=taskList.get(i);
				if(taskID.equals(taskData.id))
					return taskData;
			}
		}
		return null;
	}
	
	private void addLast(IOfflineTask task){
		synchronized (taskList) {
			if(task!=null){
				TaskData taskData=null;
				for(int i=0;i<taskList.size();i++){
					taskData=taskList.get(i);
					if(taskData.id.equals(task.id)){
						break;
					}
				}
				if(taskData!=null){
					taskList.remove(taskData);
					taskList.add(taskData);
				}
			}
		}
	}
	
	@Override
	public void timeout(TaskData taskData) {
		Log.e(TAG, "task timeout\n"+taskData.toString());
		switch (taskData.taskType) {
		case SEND_PRIVATE_MSG:
			if(taskData.request instanceof GsonRequest2.sendmessageRequest){
				GsonRequest2.sendmessageRequest temp=(sendmessageRequest) taskData.request;
				String target_userid=temp.target_userids;
				String uuid=temp.uuid;
				setPrivateMsgRes(target_userid, uuid, false);
			}
			break;
		case PRIVATE_MSG_AUDIO_UPLOAD:
			if(taskData.request instanceof FileUploadInfo[]){
				FileUploadInfo[] temp=(FileUploadInfo[]) taskData.request;
				if(temp!=null&&1==temp.length){
					String target_userid=temp[0].target_userids;
					String uuid=temp[0].uuid;
					setPrivateMsgRes(target_userid, uuid, false);
				}
			}
			break;
		default:
			break;
		}
		removeByID(taskData.id);
		if(runningTask!=null&&runningTask.id.equals(taskData.id)){
			runningTask=null;
			start(0);
		}
	}

	//设置私信发送结果
	private void setPrivateMsgRes(IOfflineTask task, boolean success){
		if(task.request instanceof FileUploadInfo){
			FileUploadInfo uploadInfos=(FileUploadInfo) task.request;
			if(pmm!=null)pmm.setStatusByUUID(uploadInfos.target_userids, uploadInfos.uuid, success);
		}else if(task.request instanceof GsonRequest2.sendmessageRequest){
			GsonRequest2.sendmessageRequest request=(sendmessageRequest) task.request;
			if(pmm!=null)pmm.setStatusByUUID(request.target_userids, request.uuid, success);
		}else{
			Log.e(TAG, "setPrivateMsgRes->task.request can not cast FileUploadInfo");
			removeTask(task);
			start(0);
		}
	}
	//设置私信发送结果
	private void setPrivateMsgRes(String target_userids,String uuid,boolean success){
		if(pmm!=null)pmm.setStatusByUUID(target_userids,uuid, success);
	}
	
	public void removeTask(IOfflineTask task){
		if(task!=null)
			removeByID(task.id);
	}
	
	private void removeByID(String id){
		//删除任务时，如果正在运行的任务未将要删除的任务时，重置正在运行任务状态
		if(runningTask!=null&&runningTask.id.equals(id)){
			runningTask=null;
		}
		for(int i=0;i<taskList.size();i++){
			TaskData data=taskList.get(i);
			if(id.equals(data.id)){
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
//			Log.d(TAG, "NetConnectedReceiver intent" + intent.getAction());
			if (WifiManager.NETWORK_STATE_CHANGED_ACTION.equals(intent.getAction())) {
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
	private boolean isAppOnFront(Context context){
		if(context!=null){
			String packageName = context.getPackageName();
			ActivityManager activityManager = (ActivityManager)context.getSystemService(Context.ACTIVITY_SERVICE);
			List<RunningTaskInfo> appTask = activityManager.getRunningTasks(1);
			if(appTask!=null && appTask.size()>0){
				if(appTask.get(0).topActivity.toString().contains(packageName)){
					return true;
				}
			}
		}
	    return false;
	}
}
