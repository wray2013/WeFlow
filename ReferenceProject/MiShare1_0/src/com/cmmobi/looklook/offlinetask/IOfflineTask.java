package com.cmmobi.looklook.offlinetask;

import java.util.Iterator;

import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.util.Log;

import com.cmmobi.looklook.MainApplication;
import com.cmmobi.looklook.activity.DiaryPreviewActivity;
import com.cmmobi.looklook.activity.ShareDiaryActivity;
import com.cmmobi.looklook.common.constant.Constant;
import com.cmmobi.looklook.common.gson.GsonRequest3;
import com.cmmobi.looklook.common.gson.GsonRequest3.SNS;
import com.cmmobi.looklook.common.gson.GsonRequest3.createMicRequest;
import com.cmmobi.looklook.common.gson.GsonRequest3.sendmessageRequest;
import com.cmmobi.looklook.common.gson.GsonResponse3;
import com.cmmobi.looklook.common.gson.GsonResponse3.MyDiary;
import com.cmmobi.looklook.common.gson.GsonResponse3.MyDiaryList;
import com.cmmobi.looklook.common.gson.GsonResponse3.createMicResponse;
import com.cmmobi.looklook.common.gson.GsonResponse3.deleteDiaryResponse;
import com.cmmobi.looklook.common.gson.GsonResponse3.diaryPublishResponse;
import com.cmmobi.looklook.common.gson.GsonResponse3.diarySharePermissionsResponse;
import com.cmmobi.looklook.common.gson.GsonResponse3.getDiaryUrlResponse;
import com.cmmobi.looklook.common.gson.GsonResponse3.mergerAccountResponse;
import com.cmmobi.looklook.common.gson.GsonResponse3.modTagsOrPositionResponse;
import com.cmmobi.looklook.common.gson.GsonResponse3.platformUrls;
import com.cmmobi.looklook.common.gson.GsonResponse3.safeboxResponse;
import com.cmmobi.looklook.common.gson.GsonResponse3.sendmessageResponse;
import com.cmmobi.looklook.common.gson.GsonResponse3.shareDiaryResponse;
import com.cmmobi.looklook.common.gson.GsonResponse3.shareInfo;
import com.cmmobi.looklook.common.gson.Requester3;
import com.cmmobi.looklook.common.gson.WeiboRequester;
import com.cmmobi.looklook.common.utils.DateUtils;
import com.cmmobi.looklook.info.profile.AccountInfo;
import com.cmmobi.looklook.info.profile.ActiveAccount;
import com.cmmobi.looklook.info.profile.DiaryManager;
import com.cmmobi.looklook.info.profile.MessageWrapper;
import com.cmmobi.looklook.info.profile.PrivateCommonMessage;
import com.cmmobi.looklook.info.profile.PrivateMessageManager;
import com.cmmobi.looklook.info.profile.TimeHelper;
import com.cmmobi.looklook.info.profile.VshareDataEntities;


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
	
	protected String successResult;
	
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
			case Requester3.RESPONSE_TYPE_MODIFY_TAG:{
				GsonResponse3.modTagsOrPositionResponse response=(modTagsOrPositionResponse) msg.obj;
				if("0".equals(response.status)){
					if(listener!=null)listener.success(IOfflineTask.this);
				}else{
					failedResult=response.status;
					if(listener!=null)listener.failed(IOfflineTask.this);
				}
				break;}
				
			/*case Requester2.RESPONSE_TYPE_ADD_COLLECT_DIARY:{
				GsonResponse2.addCollectDiaryResponse response=(addCollectDiaryResponse) msg.obj;
				if("0".equals(response.status)){
					if(listener!=null)listener.success(IOfflineTask.this);
				}else{
					failedResult=response.status;
					if(listener!=null)listener.failed(IOfflineTask.this);
				}
				break;}*/
				
			/*case Requester2.RESPONSE_TYPE_REMOVE_COLLECT_DIARY:{
				GsonResponse2.removeCollectDiaryResponse response=(removeCollectDiaryResponse) msg.obj;
				if("0".equals(response.status)){
					if(listener!=null)listener.success(IOfflineTask.this);
				}else{
					failedResult=response.status;
					if(listener!=null)listener.failed(IOfflineTask.this);
				}
				break;}*/
			/*case Requester2.RESPONSE_TYPE_COMMENT:{
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
				break;}*/
			/*case Requester2.RESPONSE_TYPE_DELETE_COMMENT:{
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
				break;}*/
			/*case Requester2.RESPONSE_TYPE_JOIN_ACTIVE:{
				GsonResponse2.joinActiveResponse response=(joinActiveResponse) msg.obj;
				if("0".equals(response.status)){
					if(listener!=null)listener.success(IOfflineTask.this);
				}else{
					failedResult=response.status;
					if(listener!=null)listener.failed(IOfflineTask.this);
				}
				break;}*/
			/*case Requester2.RESPONSE_TYPE_CANCEL_ACTIVE:{
				GsonResponse2.cancleActiveResponse response=(cancleActiveResponse) msg.obj;
				if("0".equals(response.status)){
					if(listener!=null)listener.success(IOfflineTask.this);
				}else{
					failedResult=response.status;
					if(listener!=null)listener.failed(IOfflineTask.this);
				}
				break;}*/
			case Requester3.RESPONSE_TYPE_DELETE_DIARY:{
				GsonResponse3.deleteDiaryResponse response=(deleteDiaryResponse) msg.obj;
				if("0".equals(response.status)){
					if(listener!=null)listener.success(IOfflineTask.this);
				}else{
					failedResult=response.status;
					if(listener!=null)listener.failed(IOfflineTask.this);
				}
				break;}
			/*case Requester2.RESPONSE_TYPE_CANCEL_ATTENTION:{
				GsonResponse2.cancelattentionResponse response=(cancelattentionResponse) msg.obj;
				if("0".equals(response.status)){
					if(listener!=null)listener.success(IOfflineTask.this);
				}else{
					failedResult=response.status;
					if(listener!=null)listener.failed(IOfflineTask.this);
				}
				break;}*/
			/*case Requester2.RESPONSE_TYPE_SET_BLACKLIST:{
				GsonResponse2.operateblacklistResponse response=(operateblacklistResponse) msg.obj;
				if("0".equals(response.status)){
					if(listener!=null)listener.success(IOfflineTask.this);
				}else{
					failedResult=response.status;
					if(listener!=null)listener.failed(IOfflineTask.this);
				}
				break;}*/
			case Requester3.RESPONSE_TYPE_SEND_MESSAGE:{
				GsonResponse3.sendmessageResponse response=(sendmessageResponse) msg.obj;
				//状态：0 成功 138107 黑名单 138115 未关注，遇到后两种状态直接提示成功，不用再连socket发送语音附件
				if("0".equals(response.status)){
					//私信发送成功后上传音频或日记物理文件
					if(IOfflineTask.this instanceof SendPrivateMsgTask){
						SendPrivateMsgTask sendPrivateMsgTask=((SendPrivateMsgTask)IOfflineTask.this);
						if(sendPrivateMsgTask.request instanceof sendmessageRequest){
							sendmessageRequest request=(sendmessageRequest) sendPrivateMsgTask.request;
							updatePrivatemsgid(response, request);
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
			case Requester3.RESPONSE_TYPE_DIARY_PUBLISH:{
				GsonResponse3.diaryPublishResponse response=(diaryPublishResponse) msg.obj;
				if("0".equals(response.status)){
					addShareInfo(101,response.diaryuuid,response.publishid);
					if(listener!=null)listener.success(IOfflineTask.this);
				}else{
					failedResult=response.status;
					if(listener!=null)listener.failed(IOfflineTask.this);
				}
				break;}
			case WeiboRequester.SINA_INTERFACE_PUBLISH_WEIBO:{
				if(msg.obj!=null){
					String sinaID=msg.obj.toString();
					SNS sns =new GsonRequest3.SNS();
					sns.snsid=msg.getData().getString("snsid");
					sns.snstype="1";
					sns.weiboid=sinaID;
					//添加发送分享轨迹任务
					/*//更新本地日记分享轨迹
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
					
					DiaryManager_del.getInstance().addSnsTrace(diaryID, snsTrace);*/
					addShareTraceUploadTask(sns);
					if(listener!=null)listener.success(IOfflineTask.this);
				}else{
					if(listener!=null)listener.failed(IOfflineTask.this);
				}
				break;}
			case WeiboRequester.TENCENT_INTERFACE_PUBLISH_WEIBO:{
				if(msg.obj!=null){
					String tencentID=msg.obj.toString();
					SNS sns =new GsonRequest3.SNS();
					sns.snsid=msg.getData().getString("snsid");
					sns.snstype="6";
					sns.weiboid=tencentID;
					/*//添加发送分享轨迹任务
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
					
					DiaryManager_del.getInstance().addSnsTrace(diaryID, snsTrace);*/
					addShareTraceUploadTask(sns);
					if(listener!=null)listener.success(IOfflineTask.this);
				}else{
					if(listener!=null)listener.failed(IOfflineTask.this);
				}
				break;}
			case WeiboRequester.RENREN_INTERFACE_PUBLISH_WEIBO:{
				if(msg.obj!=null){
					String renrenID=msg.obj.toString();
					SNS sns =new GsonRequest3.SNS();
					sns.snsid=msg.getData().getString("snsid");
					sns.snstype="2";
					sns.weiboid=renrenID;
					//添加发送分享轨迹任务
					/*ShareSNSTrace snsTrace = new ShareSNSTrace();
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
					
					DiaryManager_del.getInstance().addSnsTrace(diaryID, snsTrace);*/
					addShareTraceUploadTask(sns);
					if(listener!=null)listener.success(IOfflineTask.this);
				}else{
					if(listener!=null)listener.failed(IOfflineTask.this);
				}
				break;}
			case Requester3.RESPONSE_TYPE_SHARE_DIARY:{
				GsonResponse3.shareDiaryResponse response=(shareDiaryResponse) msg.obj;
				if("0".equals(response.status)){
					if(listener!=null)listener.success(IOfflineTask.this);
				}else{
					failedResult=response.status;
					if(listener!=null)listener.failed(IOfflineTask.this);
				}
				break;}
			case Requester3.RESPONSE_TYPE_CREATEMIC:{
				GsonResponse3.createMicResponse response=(createMicResponse) msg.obj;
				String userid=ActiveAccount.getInstance(context).getLookLookID();
				VshareDataEntities localVShareData=AccountInfo.getInstance(userid).vshareLocalDataEntities;
				if("0".equals(response.status)){
					if(localVShareData!=null){
						localVShareData.updatePublishidByuuid(response.uuid,response.publishid);
						localVShareData.updateStutas(response.uuid,"3");
					}
					if(listener!=null)listener.success(IOfflineTask.this);
				}else{
					if(localVShareData!=null){
						if(!TextUtils.isEmpty(response.uuid)){
							localVShareData.updateStutas(response.uuid,"1");
						}else{
							TaskData taskData=OfflineTaskManager.getInstance().getTaskData(id);
							if(taskData.request instanceof createMicRequest){
								localVShareData.updateStutas(taskData.diaryuuid,"1");
							}
						}
					}
					failedResult=response.status;
					if(listener!=null)listener.failed(IOfflineTask.this);
				}
				break;
			}
			case Requester3.RESPONSE_TYPE_DIARY_SHARE_PERMISSIONS:{
				GsonResponse3.diarySharePermissionsResponse response=(diarySharePermissionsResponse) msg.obj;
				if("0".equals(response.status)){
					if(listener!=null)listener.success(IOfflineTask.this);
				}else{
					failedResult=response.status;
					if(listener!=null)listener.failed(IOfflineTask.this);
				}
				break;
			}
			case Requester3.RESPONSE_TYPE_SAFEBOX:{
				GsonResponse3.safeboxResponse response=(safeboxResponse) msg.obj;
				if("0".equals(response.status)){
					if(listener!=null)listener.success(IOfflineTask.this);
				}else{
					failedResult=response.status;
					if(listener!=null)listener.failed(IOfflineTask.this);
				}
				break;}
			case Requester3.RESPONSE_TYPE_MERGER_ACCOUNT:{
				GsonResponse3.mergerAccountResponse response=(mergerAccountResponse) msg.obj;
				if("0".equals(response.status)){
					if(listener!=null)listener.success(IOfflineTask.this);
				}else{
					failedResult=response.status;
					if(listener!=null)listener.failed(IOfflineTask.this);
				}
				break;
				}
			case Requester3.RESPONSE_TYPE_GET_DIARY_URL:
				if(msg.obj!=null){
					getDiaryUrlResponse response=(getDiaryUrlResponse) msg.obj;
					if("0".equals(response.status)){
						DiaryManager.getInstance().updateDiaryGroupIDByUUID(response.diaryid, response.diaryuuid);
						String sinaUrl = "";
						String renrenUrl = "";
						String tencentUrl = "";
						String qqUrl = "";
						String lng="";
						String lat="";
						String userList ="";
						String shareContent = "";
						String positionInfo="";
						String position="";
						String publishid=response.publishid;
						int shareType=0;
						String diaryUserID="";
						TaskData taskData=OfflineTaskManager.getInstance().getTaskData(id);
						if(taskData!=null&&taskData.taskType==TaskType.GET_DIARY_SHARE_URL){
							if(taskData.request instanceof ThirdPartyRequest){
								ThirdPartyRequest request=(ThirdPartyRequest) taskData.request;
								shareContent=request.content;
								shareType=request.shareType;
								positionInfo=request.positionInfo;
								userList=request.userList;
								position=request.position;
								lng=request.longitude;
								lat=request.latitude;
								diaryUserID=taskData.diaryUserID;
								//添加分享记录
								if(!TextUtils.isEmpty(response.diaryuuid)){
									addShareInfo(shareType,response.diaryuuid,publishid);
								}else{
									addShareInfo(shareType,request.diaryuuid,publishid);
								}
							}else{
								Log.e(TAG, "Requester3.RESPONSE_TYPE_GET_DIARY_URL-> taskData.request cast error");
								if(listener!=null)listener.exception(IOfflineTask.this);
								return;
							}
						}
						String picUrl = Environment.getExternalStorageDirectory()
								+ Constant.SD_STORAGE_ROOT + "/" 
								+ ActiveAccount.getInstance(context).getLookLookID()
								+ "/tmp_share_image_file.jpeg";
						
						if(!ShareDiaryActivity.getSharePicFile(context, response.shareimageurl, picUrl))
						{
							Log.e(TAG, "get share pic error");
							if(listener!=null)listener.exception(IOfflineTask.this);
							break;
						}
						for(int i=0;i<response.platformurls.length;i++){
							platformUrls urls=response.platformurls[i];
							if("1".equals(urls.snstype)&&urls.url!=null&&urls.url.length()>0){//新浪
								sinaUrl = positionInfo+urls.url+userList;
							}
							if("2".equals(urls.snstype)&&urls.url!=null&&urls.url.length()>0){//人人
								renrenUrl = positionInfo+urls.url+userList;
							}
							if("6".equals(urls.snstype)&&urls.url!=null&&urls.url.length()>0){//腾讯
								tencentUrl = positionInfo+urls.url+userList;
							}
							if("5".equals(urls.snstype)&&urls.url!=null&&urls.url.length()>0){//腾讯
								qqUrl = positionInfo+urls.url+userList;
							}
						}
						Log.d(TAG, "sinaUrl="+sinaUrl);
						Log.d(TAG, "renrenUrl="+renrenUrl);
						Log.d(TAG, "tencentUrl="+tencentUrl);
						Log.d(TAG, "picUrl="+picUrl);
						if(listener!=null)listener.success(IOfflineTask.this);
						String diary=(diaryID!=null&&diaryID.split(",").length>1)?response.diaryid:diaryID;
						if(1==shareType){//新浪
							OfflineTaskManager.getInstance().addShareToSina(publishid,shareContent, sinaUrl, picUrl, diary,diaryuuid, position, lng, lat, diaryUserID);
						}else if(2==shareType){//人人
							OfflineTaskManager.getInstance().addShareToRenren(publishid,shareContent, renrenUrl, picUrl, diary,diaryuuid, position, lng, lat, diaryUserID);
						}else if(6==shareType){//腾讯
							OfflineTaskManager.getInstance().addShareToTencent(publishid,shareContent, tencentUrl, picUrl, diary,diaryuuid, position, lng, lat, diaryUserID);
						}else if(5==shareType){//qq
							
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
		
		/**
		 * 发送消息后更新消息id
		 * @param response
		 * @param request
		 */
		private void updatePrivatemsgid(GsonResponse3.sendmessageResponse response,
										sendmessageRequest request) {
			try {
				String uuid = request.uuid;
				AccountInfo ai = AccountInfo.getInstance(ActiveAccount.getInstance(context).getLookLookID());
				PrivateMessageManager pmm = ai.privateMsgManger;
				if(pmm!=null){
					MessageWrapper mv = pmm.get(request.target_userids);
					if(mv!=null && mv.msgs!=null){
						Iterator<PrivateCommonMessage> it = mv.msgs.iterator();
						while(it.hasNext()){
							PrivateCommonMessage pcm = it.next();
							if(uuid!=null && uuid.equals(pcm.getUUID())){
								if(!TextUtils.isEmpty(response.privatemsgid)){
									pcm.s_msg.privatemsgid = response.privatemsgid;
								}
								break;
							}
						}
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	private void addShareInfo(int shareType,String uuid,String publishid){
		MyDiaryList myDiaryList=DiaryManager.getInstance().findDiaryGroupByUUID(uuid);
		if(null==myDiaryList)//传进来的uuid有可能是日记id
			myDiaryList=DiaryManager.getInstance().findDiaryGroupByID(uuid);
		if(myDiaryList!=null){
			shareInfo info=new shareInfo();
			info.publishid=publishid;
			info.share_time=String.valueOf(TimeHelper.getInstance().now());
			info.share_status=""+shareType;
			myDiaryList.addShareInfo(info);
			DiaryManager.getInstance().notifyMyDiaryChanged();
			DiaryManager.getInstance().notifyMySafeboxChanged();
			Intent intent;
			intent = new Intent(DiaryPreviewActivity.DIARY_EDIT_REFRESH);
			intent.putExtra(DiaryPreviewActivity.INTENT_ACTION_DIARY_UUID, uuid);
			LocalBroadcastManager.getInstance(MainApplication.getAppInstance()).sendBroadcast(intent);
		}
		MyDiary myDiary=DiaryManager.getInstance().findMyDiaryByUUID(uuid);
		if(myDiary!=null){
			shareInfo info=new shareInfo();
			info.publishid=publishid;
			info.share_time=String.valueOf(TimeHelper.getInstance().now());
			info.share_status=""+shareType;
			myDiary.addShareInfo(info);
			DiaryManager.getInstance().notifyMyDiaryChanged();
		}
	}
	
	//新建分享轨迹上传任务
	private void addShareTraceUploadTask(SNS sns){
		if(IOfflineTask.this instanceof ThirdPartyShareTask){
			if(IOfflineTask.this.request instanceof ThirdPartyRequest){
				OfflineTaskManager.getInstance().removeTask(this);
				ThirdPartyRequest thirdPartyRequest=(ThirdPartyRequest) IOfflineTask.this.request;
				String diaryid=thirdPartyRequest.diaryid;
				String publishid=thirdPartyRequest.publishid;
				String snsContent=thirdPartyRequest.content;
				OfflineTaskManager.getInstance().addShareTraceUploadTask(diaryid, publishid, snsContent,new SNS[]{sns});
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
			return Requester3.RESPONSE_TYPE_MODIFY_TAG;
		case COLLECT_ADD:
			return Requester3.RESPONSE_TYPE_ADD_COLLECT_DIARY;
		case COLLECT_CANCEL:
			return Requester3.RESPONSE_TYPE_REMOVE_COLLECT_DIARY;
		case COMMENT_ADD:
			return Requester3.RESPONSE_TYPE_COMMENT;
		case COMMENT_DELETE:
			return Requester3.RESPONSE_TYPE_DELETE_COMMENT;
		case ACTIVE_ATTEND:
			return Requester3.RESPONSE_TYPE_JOIN_ACTIVE;
		case ACTIVE_CANCEL:
			return Requester3.RESPONSE_TYPE_CANCEL_ACTIVE;
		case DIARY_REMOVE:
			return Requester3.RESPONSE_TYPE_DELETE_DIARY;
		case ATTENDED_REMOVE:
		case FANS_REMOVE:
			return Requester3.RESPONSE_TYPE_CANCEL_ATTENTION;
		case BLACK_REMOVE:
			return Requester3.RESPONSE_TYPE_SET_BLACKLIST;
		case SEND_PRIVATE_MSG:
			return Requester3.RESPONSE_TYPE_SEND_MESSAGE;
		case SHARE_TO_LOOKLOOK:
			return Requester3.RESPONSE_TYPE_DIARY_PUBLISH;
		case V_SHARE:
			return Requester3.RESPONSE_TYPE_CREATEMIC;
		case SHARE_TRACE_UPLOAD:
			return Requester3.RESPONSE_TYPE_SHARE_DIARY;
		case SAFEBOX_REMOVE:
		case SAFEBOX_ADD:
			return Requester3.RESPONSE_TYPE_SAFEBOX;
		case SET_DIARY_SHARE_PERMISSIONS:
			return Requester3.RESPONSE_TYPE_DIARY_SHARE_PERMISSIONS;
		case GET_DIARY_SHARE_URL:
			return Requester3.RESPONSE_TYPE_GET_DIARY_URL;
		case MERGER_ACCOUNT:
			return Requester3.RESPONSE_TYPE_MERGER_ACCOUNT;
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
			return Requester3.RIA_INTERFACE_MODIFY_TAG;
		case COLLECT_ADD:
			return Requester3.RIA_INTERFACE_ADD_COLLECT_DIARY;
		case COLLECT_CANCEL:
			return Requester3.RIA_INTERFACE_REMOVE_COLLECT_DIARY;
		case COMMENT_ADD:
			return Requester3.RIA_INTERFACE_COMMENT;
		case COMMENT_DELETE:
			return Requester3.RIA_INTERFACE_DELETE_COMMENT;
		case ACTIVE_ATTEND:
			return Requester3.RIA_INTERFACE_JOIN_ACTIVE;
		case ACTIVE_CANCEL:
			return Requester3.RIA_INTERFACE_CANCEL_ACTIVE;
		case DIARY_REMOVE:
			return Requester3.RIA_INTERFACE_DELETE_DIARY;
		case ATTENDED_REMOVE:
		case FANS_REMOVE:
			return Requester3.RIA_INTERFACE_CANCEL_ATTENTION;
		case BLACK_REMOVE:
			return Requester3.RIA_INTERFACE_SET_BLACKLIST;
		case SEND_PRIVATE_MSG:
			return Requester3.RIA_INTERFACE_SEND_MESSAGE;
		case V_SHARE:
			return Requester3.RIA_INTERFACE_CREATEMIC;
		case SHARE_TO_LOOKLOOK:
			return Requester3.RIA_INTERFACE_DIARY_PUBLISH;
		case SHARE_TRACE_UPLOAD:
			return Requester3.RIA_INTERFACE_SHARE_DIARY;
		case SAFEBOX_REMOVE:
		case SAFEBOX_ADD:
			return Requester3.RIA_INTERFACE_SAFEBOX;
		case SET_DIARY_SHARE_PERMISSIONS:
			return Requester3.RIA_INTERFACE_DAIRY_SHARE_PERMISSIONS;
		case GET_DIARY_SHARE_URL:
			return Requester3.RIA_INTERFACE_GET_DIARY_URL;
		case MERGER_ACCOUNT:
			return Requester3.RIA_INTERFACE_MERGER_ACCOUNT;
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
			return GsonResponse3.modTagsOrPositionResponse.class;
		case COLLECT_ADD:
			return GsonResponse3.addCollectDiaryResponse.class;
		case COLLECT_CANCEL:
			return GsonResponse3.removeCollectDiaryResponse.class;
		case COMMENT_ADD:
			return GsonResponse3.commentResponse.class;
		case COMMENT_DELETE:
			return GsonResponse3.deleteCommentResponse.class;
		case ACTIVE_ATTEND:
			return GsonResponse3.joinActiveResponse.class;
		case DIARY_REMOVE:
			return GsonResponse3.deleteDiaryResponse.class;
		case ATTENDED_REMOVE:
		case FANS_REMOVE:
			return GsonResponse3.cancelattentionResponse.class;
		case BLACK_REMOVE:
			return GsonResponse3.operateblacklistResponse.class;
		case SEND_PRIVATE_MSG:
			return GsonResponse3.sendmessageResponse.class;
		case V_SHARE:
			return GsonResponse3.createMicResponse.class;
		case SHARE_TO_LOOKLOOK:
			return GsonResponse3.diaryPublishResponse.class;
		case SHARE_TRACE_UPLOAD:
			return GsonResponse3.shareDiaryResponse.class;
		case SAFEBOX_REMOVE:
		case SAFEBOX_ADD:
			return GsonResponse3.safeboxResponse.class;
		case GET_DIARY_SHARE_URL:
			return getDiaryUrlResponse.class;
		case SET_DIARY_SHARE_PERMISSIONS:
			return diarySharePermissionsResponse.class;
		case MERGER_ACCOUNT:
			return mergerAccountResponse.class;
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
