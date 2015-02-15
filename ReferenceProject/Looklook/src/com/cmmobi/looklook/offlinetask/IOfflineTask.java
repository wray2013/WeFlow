package com.cmmobi.looklook.offlinetask;

import android.content.Context;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import com.cmmobi.looklook.activity.HomepageCommentActivity;
import com.cmmobi.looklook.common.gson.GsonRequest2;
import com.cmmobi.looklook.common.gson.GsonRequest2.SNS;
import com.cmmobi.looklook.common.gson.GsonRequest2.sendmessageRequest;
import com.cmmobi.looklook.common.gson.GsonResponse2;
import com.cmmobi.looklook.common.gson.GsonResponse2.ShareInfo;
import com.cmmobi.looklook.common.gson.GsonResponse2.ShareSNSTrace;
import com.cmmobi.looklook.common.gson.GsonResponse2.addCollectDiaryResponse;
import com.cmmobi.looklook.common.gson.GsonResponse2.cancelattentionResponse;
import com.cmmobi.looklook.common.gson.GsonResponse2.cancleActiveResponse;
import com.cmmobi.looklook.common.gson.GsonResponse2.commentResponse;
import com.cmmobi.looklook.common.gson.GsonResponse2.deleteCommentResponse;
import com.cmmobi.looklook.common.gson.GsonResponse2.deleteDiaryResponse;
import com.cmmobi.looklook.common.gson.GsonResponse2.diaryPublishResponse;
import com.cmmobi.looklook.common.gson.GsonResponse2.getDiaryUrlResponse;
import com.cmmobi.looklook.common.gson.GsonResponse2.joinActiveResponse;
import com.cmmobi.looklook.common.gson.GsonResponse2.modTagsOrPositionResponse;
import com.cmmobi.looklook.common.gson.GsonResponse2.operateblacklistResponse;
import com.cmmobi.looklook.common.gson.GsonResponse2.platformUrls;
import com.cmmobi.looklook.common.gson.GsonResponse2.removeCollectDiaryResponse;
import com.cmmobi.looklook.common.gson.GsonResponse2.safeboxResponse;
import com.cmmobi.looklook.common.gson.GsonResponse2.sendmessageResponse;
import com.cmmobi.looklook.common.gson.GsonResponse2.shareDiaryResponse;
import com.cmmobi.looklook.common.gson.Requester2;
import com.cmmobi.looklook.common.gson.WeiboRequester;
import com.cmmobi.looklook.common.utils.DateUtils;
import com.cmmobi.looklook.info.profile.CommonInfo;
import com.cmmobi.looklook.info.profile.DiaryManager;
import com.cmmobi.looklook.info.profile.TimeHelper;


/**
 * @author wuxiang
 * @email wuxiang@cmmobi.com
 * @date  2013-8-19
 */
public abstract class IOfflineTask {
	
	private static final String TAG="IofflineTask";
	/**
	 * 任务执行结果监听
	 */
	protected TaskListener listener;
	
	private HandlerThread handlerThread;
	
	protected String id;
	/**
	 * 请求元数据
	 */
	protected Object request;
	
	protected String diaryID;
	
	protected String diaryuuid;
	/**
	 * 任务创建时间
	 */
	protected long createTime;
	
	protected Context context;
	/**
	 * 服务器请求数据回传
	 */
	protected Handler handler;
	
	/**
	 * 记录失败状态值
	 */
	protected String failedResult;
	
	/**
	 * 任务类型
	 */
	protected TaskType taskType;
	
	/**
	 * 当前任务是否在运行中  true-运行中  false-未运行
	 */
	protected boolean isRunning;
	
	/**
	 * 执行任务
	 */
	public abstract void start();
	
	/**
	 * 延迟执行
	 */
	public void startDelay(long delay){
		handler.postDelayed(new Runnable() {
			
			@Override
			public void run() {
				start();
			}
		}, delay);
	}
	
	public IOfflineTask(){
		handlerThread=new HandlerThread("IofflineTask");
		handlerThread.start();
		handler=new MyHandler(handlerThread.getLooper());
	}
	
	class MyHandler extends Handler{
		public MyHandler(Looper looper){
			super(looper);
		}
		@Override
		public void handleMessage(Message msg) {
			if(null==msg.obj){
				if(listener!=null)listener.exception(IOfflineTask.this);
				return;
			}
			switch (msg.what) {
			case Requester2.RESPONSE_TYPE_MODIFY_TAG:{
				GsonResponse2.modTagsOrPositionResponse response=(modTagsOrPositionResponse) msg.obj;
				if("0".equals(response.status)){
					if(listener!=null)listener.success(IOfflineTask.this);
				}else{
					failedResult=response.status;
					if(listener!=null)listener.failed(IOfflineTask.this);
				}
				break;}
				
			case Requester2.RESPONSE_TYPE_ADD_COLLECT_DIARY:{
				GsonResponse2.addCollectDiaryResponse response=(addCollectDiaryResponse) msg.obj;
				if("0".equals(response.status)){
					if(listener!=null)listener.success(IOfflineTask.this);
				}else{
					failedResult=response.status;
					if(listener!=null)listener.failed(IOfflineTask.this);
				}
				break;}
				
			case Requester2.RESPONSE_TYPE_REMOVE_COLLECT_DIARY:{
				GsonResponse2.removeCollectDiaryResponse response=(removeCollectDiaryResponse) msg.obj;
				if("0".equals(response.status)){
					if(listener!=null)listener.success(IOfflineTask.this);
				}else{
					failedResult=response.status;
					if(listener!=null)listener.failed(IOfflineTask.this);
				}
				break;}
			case Requester2.RESPONSE_TYPE_COMMENT:{
				GsonResponse2.commentResponse response=(commentResponse) msg.obj;
				if("0".equals(response.status)){
					if(listener!=null)listener.success(IOfflineTask.this);
					Message hcMessage = this.obtainMessage();
					hcMessage.obj = msg.obj;
					hcMessage.what = Requester2.RESPONSE_TYPE_COMMENT;
					if (HomepageCommentActivity.handler != null) {
						HomepageCommentActivity.handler.sendMessage(hcMessage);
						Log.e("HomepageCommentActivity.handler", "HomepageCommentActivity.handler != null");
					}else{
						Log.e("HomepageCommentActivity.handler", "HomepageCommentActivity.handler == null");
					}
				}else{
					failedResult=response.status;
					if(listener!=null)listener.failed(IOfflineTask.this);
				}
				break;}
			case Requester2.RESPONSE_TYPE_DELETE_COMMENT:{
				GsonResponse2.deleteCommentResponse response=(deleteCommentResponse) msg.obj;
				if("0".equals(response.status)){
					if(listener!=null)listener.success(IOfflineTask.this);
					Message hcMessage = this.obtainMessage();
					hcMessage.obj = msg.obj;
					hcMessage.what = Requester2.RESPONSE_TYPE_DELETE_COMMENT;
					if (HomepageCommentActivity.handler != null) {
						HomepageCommentActivity.handler.sendMessage(hcMessage);
						Log.e("HomepageCommentActivity.handler", "HomepageCommentActivity.handler != null");
					}else{
						Log.e("HomepageCommentActivity.handler", "HomepageCommentActivity.handler == null");
					}
				}else{
					failedResult=response.status;
					if(listener!=null)listener.failed(IOfflineTask.this);
				}
				break;}
			case Requester2.RESPONSE_TYPE_JOIN_ACTIVE:{
				GsonResponse2.joinActiveResponse response=(joinActiveResponse) msg.obj;
				if("0".equals(response.status)){
					if(listener!=null)listener.success(IOfflineTask.this);
				}else{
					failedResult=response.status;
					if(listener!=null)listener.failed(IOfflineTask.this);
				}
				break;}
			case Requester2.RESPONSE_TYPE_CANCEL_ACTIVE:{
				GsonResponse2.cancleActiveResponse response=(cancleActiveResponse) msg.obj;
				if("0".equals(response.status)){
					if(listener!=null)listener.success(IOfflineTask.this);
				}else{
					failedResult=response.status;
					if(listener!=null)listener.failed(IOfflineTask.this);
				}
				break;}
			case Requester2.RESPONSE_TYPE_DELETE_DIARY:{
				GsonResponse2.deleteDiaryResponse response=(deleteDiaryResponse) msg.obj;
				if("0".equals(response.status)){
					if(listener!=null)listener.success(IOfflineTask.this);
				}else{
					failedResult=response.status;
					if(listener!=null)listener.failed(IOfflineTask.this);
				}
				break;}
			case Requester2.RESPONSE_TYPE_CANCEL_ATTENTION:{
				GsonResponse2.cancelattentionResponse response=(cancelattentionResponse) msg.obj;
				if("0".equals(response.status)){
					if(listener!=null)listener.success(IOfflineTask.this);
				}else{
					failedResult=response.status;
					if(listener!=null)listener.failed(IOfflineTask.this);
				}
				break;}
			case Requester2.RESPONSE_TYPE_SET_BLACKLIST:{
				GsonResponse2.operateblacklistResponse response=(operateblacklistResponse) msg.obj;
				if("0".equals(response.status)){
					if(listener!=null)listener.success(IOfflineTask.this);
				}else{
					failedResult=response.status;
					if(listener!=null)listener.failed(IOfflineTask.this);
				}
				break;}
			case Requester2.RESPONSE_TYPE_SEND_MESSAGE:{
				GsonResponse2.sendmessageResponse response=(sendmessageResponse) msg.obj;
				//状态：0 成功 138107 黑名单 138115 未关注，遇到后两种状态直接提示成功，不用再连socket发送语音附件
				if("0".equals(response.status)){
					//私信发送成功后上传音频或日记物理文件
					if(IOfflineTask.this instanceof SendPrivateMsgTask){
						SendPrivateMsgTask sendPrivateMsgTask=((SendPrivateMsgTask)IOfflineTask.this);
						if(sendPrivateMsgTask.request instanceof sendmessageRequest){
							sendmessageRequest request=(sendmessageRequest) sendPrivateMsgTask.request;
							//语音和语音加文字类型私信需要上传语音文件，其他类型直接发送成功
							if("2".equals(request.privatemsgtype)||"4".equals(request.privatemsgtype)){
								String ip=response.ip;
								int port=0;
								if(response.port!=null&&DateUtils.isNum(response.port)){
									port=Integer.parseInt(response.port);
								}else{
									Log.e(TAG, "RESPONSE_TYPE_SEND_MESSAGE->port is error port="+port);
									break;
								}
								String uploadPath=response.audiopath;
								String privateMsgId=response.privatemsgid;
								String uuid=response.uuid;
								sendPrivateMsgTask.uploadAudio(ip,port,uploadPath,privateMsgId,uuid);
							}else{
								if(listener!=null)listener.success(IOfflineTask.this);
							}
						}else{
							Log.e(TAG, "sendPrivateMsgTask.request can not cast sendmessageRequest");
							failedResult="sendPrivateMsgTask.request can not cast sendmessageRequest";
							if(listener!=null)listener.failed(IOfflineTask.this);
						}
					}else{
						Log.e(TAG, "RESPONSE_TYPE_SEND_MESSAGE->IofflineTask cast error");
						if(listener!=null)listener.exception(IOfflineTask.this);
					}
				}else if("138107".equals(response.status)||"138115".equals(response.status)){
					failedResult=response.status;
					if(listener!=null)listener.success(IOfflineTask.this);
				}else{
					failedResult=response.status;
					if(listener!=null)listener.failed(IOfflineTask.this);
				}
				break;}
			case Requester2.RESPONSE_TYPE_DIARY_PUBLISH:{
				GsonResponse2.diaryPublishResponse response=(diaryPublishResponse) msg.obj;
				if("0".equals(response.status)){
					if(listener!=null)listener.success(IOfflineTask.this);
				}else{
					failedResult=response.status;
					if(listener!=null)listener.failed(IOfflineTask.this);
				}
				break;}
			case WeiboRequester.SINA_INTERFACE_PUBLISH_WEIBO:{
				if(msg.obj!=null){
					String sinaID=msg.obj.toString();
					SNS sns =new GsonRequest2.SNS();
					sns.snsid=msg.getData().getString("snsid");
					sns.snstype="1";
					sns.weiboid=sinaID;
					//添加发送分享轨迹任务
					//更新本地日记分享轨迹
					ShareSNSTrace snsTrace = new ShareSNSTrace();
					if(request instanceof ThirdPartyRequest){
						snsTrace.snscontent = ((ThirdPartyRequest) request).content;
					}
					snsTrace.shareinfo = new ShareInfo[1];
					snsTrace.shareinfo[0] = new ShareInfo();
					snsTrace.shareinfo[0].snsid = sns.snsid;
					snsTrace.shareinfo[0].snstype = sns.snstype;
					snsTrace.shareinfo[0].weiboid = sns.weiboid;
					
					long time = TimeHelper.getInstance().now();
					snsTrace.sharetime = String.valueOf(time);
					
					DiaryManager.getInstance().addSnsTrace(diaryID, snsTrace);
					addShareTraceUploadTask(sns);
					if(listener!=null)listener.success(IOfflineTask.this);
				}else{
					if(listener!=null)listener.failed(IOfflineTask.this);
				}
				break;}
			case WeiboRequester.TENCENT_INTERFACE_PUBLISH_WEIBO:{
				if(msg.obj!=null){
					String tencentID=msg.obj.toString();
					SNS sns =new GsonRequest2.SNS();
					sns.snsid=msg.getData().getString("snsid");
					sns.snstype="6";
					sns.weiboid=tencentID;
					//添加发送分享轨迹任务
					ShareSNSTrace snsTrace = new ShareSNSTrace();
					if(request instanceof ThirdPartyRequest){
						snsTrace.snscontent = ((ThirdPartyRequest) request).content;
					}
					snsTrace.shareinfo = new ShareInfo[1];
					snsTrace.shareinfo[0] = new ShareInfo();
					snsTrace.shareinfo[0].snsid = sns.snsid;
					snsTrace.shareinfo[0].snstype = sns.snstype;
					snsTrace.shareinfo[0].weiboid = sns.weiboid;
					
					long time = TimeHelper.getInstance().now();
					snsTrace.sharetime = String.valueOf(time);
					
					DiaryManager.getInstance().addSnsTrace(diaryID, snsTrace);
					addShareTraceUploadTask(sns);
					if(listener!=null)listener.success(IOfflineTask.this);
				}else{
					if(listener!=null)listener.failed(IOfflineTask.this);
				}
				break;}
			case WeiboRequester.RENREN_INTERFACE_PUBLISH_WEIBO:{
				if(msg.obj!=null){
					String renrenID=msg.obj.toString();
					SNS sns =new GsonRequest2.SNS();
					sns.snsid=msg.getData().getString("snsid");
					sns.snstype="2";
					sns.weiboid=renrenID;
					//添加发送分享轨迹任务
					ShareSNSTrace snsTrace = new ShareSNSTrace();
					if(request instanceof ThirdPartyRequest){
						snsTrace.snscontent = ((ThirdPartyRequest) request).content;
					}
					snsTrace.shareinfo = new ShareInfo[1];
					snsTrace.shareinfo[0] = new ShareInfo();
					snsTrace.shareinfo[0].snsid = sns.snsid;
					snsTrace.shareinfo[0].snstype = sns.snstype;
					snsTrace.shareinfo[0].weiboid = sns.weiboid;
					
					long time = TimeHelper.getInstance().now();
					snsTrace.sharetime = String.valueOf(time);
					
					DiaryManager.getInstance().addSnsTrace(diaryID, snsTrace);
					addShareTraceUploadTask(sns);
					if(listener!=null)listener.success(IOfflineTask.this);
				}else{
					if(listener!=null)listener.failed(IOfflineTask.this);
				}
				break;}
			case Requester2.RESPONSE_TYPE_SHARE_DIARY:{
				GsonResponse2.shareDiaryResponse response=(shareDiaryResponse) msg.obj;
				if("0".equals(response.status)){
					if(listener!=null)listener.success(IOfflineTask.this);
				}else{
					failedResult=response.status;
					if(listener!=null)listener.failed(IOfflineTask.this);
				}
				break;}
			case Requester2.RESPONSE_TYPE_SAFEBOX:{
				GsonResponse2.safeboxResponse response=(safeboxResponse) msg.obj;
				if("0".equals(response.status)){
					if(listener!=null)listener.success(IOfflineTask.this);
				}else{
					failedResult=response.status;
					if(listener!=null)listener.failed(IOfflineTask.this);
				}
				break;}
			case Requester2.RESPONSE_TYPE_GET_DIARY_URL:
				if(msg.obj!=null){
					getDiaryUrlResponse response=(getDiaryUrlResponse) msg.obj;
					if("0".equals(response.status)){
						String sinaUrl = "";
						String renrenUrl = "";
						String tencentUrl = "";
						String lng="";
						String lat="";
						String userList ="";
						String shareContent = "";
						String positionInfo="";
						String position="";
						int shareType=0;
						String diaryUserID="";
						TaskData taskData=OfflineTaskManager.getInstance().getTaskData(id);
						if(taskData!=null&&taskData.taskType==TaskType.GET_DIARY_SHARE_URL){
							if(taskData.request instanceof ThirdPartyRequest){
								ThirdPartyRequest request=(ThirdPartyRequest) taskData.request;
								userList=request.userList;
								shareContent=request.content;
								positionInfo=request.positionInfo;
								lng=request.longitude;
								lat=request.latitrde;
								shareType=request.shareType;
								position=request.position;
								diaryUserID=taskData.diaryUserID;
							}else{
								Log.e(TAG, "Requester2.RESPONSE_TYPE_GET_DIARY_URL-> taskData.request cast error");
								if(listener!=null)listener.exception(IOfflineTask.this);
								return;
							}
						}
						
						String picUrl=response.shareimageurl + "?width=300&heigh=400";
						for(int i=0;i<response.platformurls.length;i++){
							platformUrls urls=response.platformurls[i];
//							if("0".equals(urls.snstype)){//looklook站内
//								
//							}
							if("1".equals(urls.snstype)&&urls.url!=null&&urls.url.length()>0){//新浪
								sinaUrl = positionInfo+urls.url+userList;
							}
							if("2".equals(urls.snstype)&&urls.url!=null&&urls.url.length()>0){//人人
								renrenUrl = positionInfo+urls.url+userList;
							}
							if("6".equals(urls.snstype)&&urls.url!=null&&urls.url.length()>0){//腾讯
								tencentUrl = positionInfo+urls.url+userList;
							}
//							if("9".equals(urls.snstype)){//微信
//								weixinUrl=urls.url;
//							}
						}
						
						if(CommonInfo.getInstance().myLoc!=null){
							lng=((int)(CommonInfo.getInstance().myLoc.longitude*1E6))/1E6d+"";
							lat=((int)(CommonInfo.getInstance().myLoc.latitude*1E6))/1E6d+"";
						}
						Log.d(TAG, "sinaUrl="+sinaUrl);
						Log.d(TAG, "renrenUrl="+renrenUrl);
						Log.d(TAG, "tencentUrl="+tencentUrl);
						Log.d(TAG, "picUrl="+picUrl);
						if(listener!=null)listener.success(IOfflineTask.this);
						
						if(1==shareType){//新浪
							OfflineTaskManager.getInstance().addShareToSina(shareContent, sinaUrl, picUrl, diaryID,diaryuuid, position, lng, lat, diaryUserID);
						}else if(2==shareType){//人人
							OfflineTaskManager.getInstance().addShareToRenren(shareContent, renrenUrl, picUrl, diaryID,diaryuuid, position, lng, lat, diaryUserID);
						}else if(6==shareType){//腾讯
							OfflineTaskManager.getInstance().addShareToTencent(shareContent, tencentUrl, picUrl, diaryID,diaryuuid, position, lng, lat, diaryUserID);
						}
					}else{
						Log.e(TAG, "response.status="+response.status);
						if(listener!=null)listener.exception(IOfflineTask.this);
					}
				}else{
					Log.e(TAG, "msg.obj is null");
					if(listener!=null)listener.exception(IOfflineTask.this);
				}
				break;
			default:
				Log.e(TAG, "not found msg.what="+msg.what);
				break;
			}
			super.handleMessage(msg);
		}
	}
	
	//新建分享轨迹上传任务
	private void addShareTraceUploadTask(SNS sns){
		if(IOfflineTask.this instanceof ThirdPartyShareTask){
			if(IOfflineTask.this.request instanceof ThirdPartyRequest){
				OfflineTaskManager.getInstance().removeTask(this);
				ThirdPartyRequest thirdPartyRequest=(ThirdPartyRequest) IOfflineTask.this.request;
				String diaryid=thirdPartyRequest.diaryid;
				String position=thirdPartyRequest.position;
				String longitude=thirdPartyRequest.longitude;
				String latitrde=thirdPartyRequest.latitrde;
				String snsContent=thirdPartyRequest.content;
				OfflineTaskManager.getInstance().addShareTraceUploadTask(diaryid, position, snsContent, longitude, latitrde, new SNS[]{sns});
			}
		}
	}
	
	public void setTaskLister(TaskListener listener){
		this.listener=listener;
	}
	
	protected interface TaskListener{
		/**
		 * 当前任务执行成功
		 */
		void success(IOfflineTask task);
		/**
		 * 当前任务执行失败
		 */
		void failed(IOfflineTask task);
		/**
		 * 当前任务执行异常
		 */
		void exception(IOfflineTask task);
		
		/**
		 * 当前任务执行超时
		 */
		void timeout(TaskData taskData);
		
		/**
		 * 定时触发，检查是否有任务需要执行
		 */
		void hasTask();
		
	}
	
	/**
	 * 根据任务类型获取请求响应标记
	 */
	protected int getResponseTag(TaskType taskType){
		switch (taskType) {
		case POSITION_AND_TAG:
			return Requester2.RESPONSE_TYPE_MODIFY_TAG;
		case COLLECT_ADD:
			return Requester2.RESPONSE_TYPE_ADD_COLLECT_DIARY;
		case COLLECT_CANCEL:
			return Requester2.RESPONSE_TYPE_REMOVE_COLLECT_DIARY;
		case COMMENT_ADD:
			return Requester2.RESPONSE_TYPE_COMMENT;
		case COMMENT_DELETE:
			return Requester2.RESPONSE_TYPE_DELETE_COMMENT;
		case ACTIVE_ATTEND:
			return Requester2.RESPONSE_TYPE_JOIN_ACTIVE;
		case ACTIVE_CANCEL:
			return Requester2.RESPONSE_TYPE_CANCEL_ACTIVE;
		case DIARY_REMOVE:
			return Requester2.RESPONSE_TYPE_DELETE_DIARY;
		case ATTENDED_REMOVE:
		case FANS_REMOVE:
			return Requester2.RESPONSE_TYPE_CANCEL_ATTENTION;
		case BLACK_REMOVE:
			return Requester2.RESPONSE_TYPE_SET_BLACKLIST;
		case SEND_PRIVATE_MSG:
			return Requester2.RESPONSE_TYPE_SEND_MESSAGE;
		case SHARE_TO_LOOKLOOK:
			return Requester2.RESPONSE_TYPE_DIARY_PUBLISH;
		case SHARE_TRACE_UPLOAD:
			return Requester2.RESPONSE_TYPE_SHARE_DIARY;
		case SAFEBOX_REMOVE:
		case SAFEBOX_ADD:
			return Requester2.RESPONSE_TYPE_SAFEBOX;
		case GET_DIARY_SHARE_URL:
			return Requester2.RESPONSE_TYPE_GET_DIARY_URL;
		default:
			Log.e(TAG, "getResponseTag->unknow taskType:"+taskType);
			return 0;
		}
	}
	
	/**
	 * 根据任务类型获取RIA请求相对路劲
	 */
	protected String getRIATag(TaskType taskType){
		switch (taskType) {
		case POSITION_AND_TAG:
			return Requester2.RIA_INTERFACE_MODIFY_TAG;
		case COLLECT_ADD:
			return Requester2.RIA_INTERFACE_ADD_COLLECT_DIARY;
		case COLLECT_CANCEL:
			return Requester2.RIA_INTERFACE_REMOVE_COLLECT_DIARY;
		case COMMENT_ADD:
			return Requester2.RIA_INTERFACE_COMMENT;
		case COMMENT_DELETE:
			return Requester2.RIA_INTERFACE_DELETE_COMMENT;
		case ACTIVE_ATTEND:
			return Requester2.RIA_INTERFACE_JOIN_ACTIVE;
		case ACTIVE_CANCEL:
			return Requester2.RIA_INTERFACE_CANCEL_ACTIVE;
		case DIARY_REMOVE:
			return Requester2.RIA_INTERFACE_DELETE_DIARY;
		case ATTENDED_REMOVE:
		case FANS_REMOVE:
			return Requester2.RIA_INTERFACE_CANCEL_ATTENTION;
		case BLACK_REMOVE:
			return Requester2.RIA_INTERFACE_SET_BLACKLIST;
		case SEND_PRIVATE_MSG:
			return Requester2.RIA_INTERFACE_SEND_MESSAGE;
		case SHARE_TO_LOOKLOOK:
			return Requester2.RIA_INTERFACE_DIARY_PUBLISH;
		case SHARE_TRACE_UPLOAD:
			return Requester2.RIA_INTERFACE_SHARE_DIARY;
		case SAFEBOX_REMOVE:
		case SAFEBOX_ADD:
			return Requester2.RIA_INTERFACE_SAFEBOX;
		case GET_DIARY_SHARE_URL:
			return Requester2.RIA_INTERFACE_GET_DIARY_URL;
		default:
			Log.e(TAG, "getRIATag->unknow taskType:"+taskType);
			return "";
		}
	}
	
	/**
	 * 根据任务类型获取请求响应类
	 */
	protected Class<?> getResponseClass(TaskType taskType){
		switch (taskType) {
		case POSITION_AND_TAG:
			return GsonResponse2.modTagsOrPositionResponse.class;
		case COLLECT_ADD:
			return GsonResponse2.addCollectDiaryResponse.class;
		case COLLECT_CANCEL:
			return GsonResponse2.removeCollectDiaryResponse.class;
		case COMMENT_ADD:
			return GsonResponse2.commentResponse.class;
		case COMMENT_DELETE:
			return GsonResponse2.deleteCommentResponse.class;
		case ACTIVE_ATTEND:
			return GsonResponse2.joinActiveResponse.class;
		case ACTIVE_CANCEL:
			return GsonResponse2.cancleActiveResponse.class;
		case DIARY_REMOVE:
			return GsonResponse2.deleteDiaryResponse.class;
		case ATTENDED_REMOVE:
		case FANS_REMOVE:
			return GsonResponse2.cancelattentionResponse.class;
		case BLACK_REMOVE:
			return GsonResponse2.operateblacklistResponse.class;
		case SEND_PRIVATE_MSG:
			return GsonResponse2.sendmessageResponse.class;
		case SHARE_TO_LOOKLOOK:
			return GsonResponse2.diaryPublishResponse.class;
		case SHARE_TRACE_UPLOAD:
			return GsonResponse2.shareDiaryResponse.class;
		case SAFEBOX_REMOVE:
		case SAFEBOX_ADD:
			return GsonResponse2.safeboxResponse.class;
		case GET_DIARY_SHARE_URL:
			return getDiaryUrlResponse.class;
		default:
			Log.e(TAG, "getResponseClass->unknow taskType:"+taskType);
			return null;
		}
	}
	
	public String toString(){
		return this.getClass().getSimpleName()+":\n" +
				"id="+id+"\n"+
				"taskType="+taskType+"\n"+
				"failedResult="+failedResult+"\n";
	}
}
