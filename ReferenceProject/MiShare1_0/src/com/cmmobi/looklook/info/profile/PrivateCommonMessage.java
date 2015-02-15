package com.cmmobi.looklook.info.profile;

import android.util.Log;

import com.cmmobi.looklook.common.constant.Constant.SEND_MSG_STATUS;
import com.cmmobi.looklook.common.gson.GsonResponse3.MyMessage;
import com.cmmobi.looklook.common.gson.GsonResponse3.sendmessageResponse;

public class PrivateCommonMessage {

	private transient static final String TAG = "PrivateCommonMessage";
	public boolean send; //false接收，true 发送
	public MyMessage r_msg; //当send==false有效
	public PrivateSendMessage s_msg; //当send==true有效
	
	public long create_time; //生成时间
	public String userid; //（对方）用户ID, 当send==false有效
	
	//日记私信，收端有效
	public boolean onDownloading;
	public int percent;
	
	/**
	 * 构造接收类型的msg
	 * 
	 * */
	public PrivateCommonMessage(String _userid, MyMessage myMsg, long time){
		userid = _userid;
		send = false;
		r_msg = myMsg;
		s_msg = null;
		create_time = time;
		
		onDownloading = false;
		percent = 0;
	}
	
	/**
	 * 构造接收类型的msg
	 * 
	 * */
	public PrivateCommonMessage(String _userid, MyMessage myMsg){
		this(_userid, myMsg, TimeHelper.getInstance().now());
	}
	
	/**
	 * 构造发送类型的msg
	 * 
	 * */
	public PrivateCommonMessage(PrivateSendMessage myMsg, long time){
		send = true;
		s_msg = myMsg;
		r_msg = null;
		create_time = time;
	}
	
	/**
	 * 构造发送类型的msg
	 * 
	 * */
	public PrivateCommonMessage(PrivateSendMessage myMsg){
		this(myMsg, TimeHelper.getInstance().now());
	}

	
	public synchronized SEND_MSG_STATUS getSendMsgStatus(){
		if(s_msg!=null){
			return s_msg.status;
		}
		
		return null;
	}
	
	public synchronized void updateSendMsgStatus(SEND_MSG_STATUS s){
		Log.i(TAG, "pcm:" + this + "updateSendMsgStatus - userid:" + userid + ", status:" + s + ", send:" + send);
		if(s_msg!=null && s_msg.uuid!=null){
			Log.i(TAG, "updateSendMsgStatus - s_msg.uuid:" + s_msg.uuid);
			s_msg.updateStatus(s);
		}
	}
	
	public void updateSendMsgPlayTime(String playtime){
		if(s_msg!=null){
			s_msg.updatePlaytime(playtime);
		}
	}
	
	public void updateSendMsgType(String privatemsgtype){
		if(s_msg!=null){
			s_msg.updateType(privatemsgtype);
		}
	}
	
	public void updateContent(String content){
		if(s_msg!=null){
			s_msg.updateContent(content);
		}
	}
	
	
	
	public void updateSendMsg(sendmessageResponse resp){
		if(s_msg!=null){
			s_msg.update(resp);
		}
	}
	
	public String getUUID(){
		if(s_msg!=null){
			return s_msg.uuid;
		}else{
			return null;
		}
	}
	
	public String getPrivateMSGID(){
		if(s_msg!=null){
			return s_msg.privatemsgid;
		}else{
			return null;
		}
	}
	
	public String getLocalPathFile(){
		if(s_msg!=null){
			return s_msg.localaudiopath;
		}else{
			return null;
		}
	}
	
	public String getMsgType(){
		if(s_msg!=null){
			return s_msg.privatemsgtype;
		}else{
			return null;
		}
	}
	
	
	public String getContent(){
		if(send){
			if(s_msg!=null){
				return s_msg.content;
			}else{
				return null;
			}
		}else{
			if(r_msg!=null){
				return r_msg.content;
			}else{
				return null;
			}
			
		}
	}
	
	public String getRMsgId(){
		if(!send){
			if(r_msg!=null){
				return r_msg.messageid;
			}else{
				return null;
			}
		}
		return null;
	}
}
