package com.cmmobi.looklook.offlinetask;

import java.util.Iterator;

import android.os.Environment;
import android.util.Log;

import com.cmmobi.looklook.common.gson.GsonRequest3;
import com.cmmobi.looklook.common.gson.Requester3.Worker;
import com.cmmobi.looklook.info.profile.AccountInfo;
import com.cmmobi.looklook.info.profile.ActiveAccount;
import com.cmmobi.looklook.info.profile.MessageWrapper;
import com.cmmobi.looklook.info.profile.PrivateCommonMessage;
import com.cmmobi.looklook.info.profile.PrivateMessageManager;
import com.cmmobi.looklook.info.profile.TimeHelper;

/**
 * @author wuxiang
 * @email wuxiang@cmmobi.com
 * @date  2013-8-26
 */
public class SendPrivateMsgTask extends IOfflineTask {
	private static final String TAG="SendPrivateMsgTask";

	private ActiveAccount aa;
	private AccountInfo ai;
	private PrivateMessageManager pmm;
	private MessageWrapper mv;
	
	@Override
	public void start() {
		isRunning=true;
		aa = ActiveAccount.getInstance(context);
		ai = AccountInfo.getInstance(aa.getLookLookID());
		pmm = ai.privateMsgManger;
		//如果在断网情况判断1分钟如果联网，自动发送如果没联网在内容旁边显示一个惊叹号，点击再次发送
		if(createTime!=0&&TimeHelper.getInstance().now()-createTime<60000){
			//发送私信
			Worker worker = new Worker(handler, getResponseTag(taskType),
					getResponseClass(taskType));
			worker.execute(getRIATag(taskType), request);
		}else{
			failedResult="timeout one minutes";
			if(listener!=null)listener.failed(this);
		}
	}
	
	//上传语音文件
	public void uploadAudio(String ip,int port,String uploadPath,String privateMsgId,String uuid){
		if(request instanceof GsonRequest3.sendmessageRequest){
			GsonRequest3.sendmessageRequest msgRequest=(GsonRequest3.sendmessageRequest) request;
			if("2".equals(msgRequest.privatemsgtype)||"4".equals(msgRequest.privatemsgtype)){
				String localFilePath=Environment.getExternalStorageDirectory() +getLocalFilePath(msgRequest.target_userids, msgRequest.uuid);
//				String localFilePath=getLocalFilePath(msgRequest.target_userids, msgRequest.uuid);
				//新建文件上传任务
				OfflineTaskManager.getInstance().removeTask(this);
				OfflineTaskManager.getInstance().addPrivateMsgAudioUploadTask(ip, port, localFilePath, uploadPath,privateMsgId,uuid,msgRequest.target_userids,id);
			}else{
				if(listener!=null)listener.success(this);
			}
		}else{
			Log.e(TAG, "uploadAudio->request cast error");
			if(listener!=null)listener.exception(this);
		}
	}
	
	private String getLocalFilePath(String target_userid,String uuid){
		if(target_userid!=null && aa.isLogin()){
			pmm = ai.privateMsgManger;
			if(pmm!=null){
				mv = pmm.get(target_userid);
				if(mv!=null && mv.msgs!=null){
					Iterator<PrivateCommonMessage> it = mv.msgs.iterator();
					while(it.hasNext()){
						PrivateCommonMessage pcm = it.next();
						if(uuid!=null && uuid.equals(pcm.getUUID())){
							return pcm.getLocalPathFile();
						}
					}
				}
				
			}
		}
		
		return null;
	}
}
