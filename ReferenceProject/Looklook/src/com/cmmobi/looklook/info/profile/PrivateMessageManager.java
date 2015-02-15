package com.cmmobi.looklook.info.profile;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;

import com.cmmobi.looklook.MainApplication;
import com.cmmobi.looklook.common.constant.Constant.SEND_MSG_STATUS;
import com.cmmobi.looklook.common.gson.GsonResponse2;
import com.cmmobi.looklook.common.gson.GsonResponse2.MyMessage;
import com.cmmobi.looklook.common.storage.GsonHelper;
import com.tencent.mm.sdk.platformtools.Log;

/**
 * @author zhangwei
 * 
 * */
public class PrivateMessageManager {

	private static transient final String TAG="PrivateMessageManager";
	public static transient final String FLAG_OFFLINE_MESSAGE_UPDATE = "FLAG_OFFLINE_MESSAGE_UPDATE";
	private transient Comparator<PrivateCommonMessage> comparator;
	
	public HashMap<String, MessageWrapper>  messages;
	public String commentid;
	public String timemill;

	
	//public int unReadMsg;
	
	
	public PrivateMessageManager(){
		messages = new HashMap<String, MessageWrapper>();
		timemill = "0";
		comparator = new Comparator<PrivateCommonMessage>() {

			@Override
			public int compare(PrivateCommonMessage lhs,
					PrivateCommonMessage rhs) {
				// TODO Auto-generated method stub
				
				return (int) (lhs.create_time - rhs.create_time);
			}
		};
		//unReadMsg = 0;
	}
	
/*	public void resetUnReadNum(){
		unReadMsg = 0;
	}*/
	
	public int getUnReadNum(){
		int unReadMsg = 0;
		for(Entry<String, MessageWrapper> m : messages.entrySet()){
			MessageWrapper value = m.getValue();
			if(value!=null){
				unReadMsg += value.getUnreadMsgs();
			}
		}
		
		return unReadMsg;
		//return unReadMsg;
	}
	
	
	public void addMessage(GsonResponse2.MessageUser item){
		if(item!=null && item.userid!=null){
/*			if(item.message!=null && item.message.length>0){
				unReadMsg += item.message.length;
			}*/
			
			MessageWrapper mw = messages.get(item.userid);
			if(item.message!=null && item.message.length>0){
				if(mw!=null && mw.msgs!=null){
					for(int i=0; i<item.message.length; i++){
						if(!mw.contains(item.message[i])){
							long this_time = 0;
							try{
								this_time = Long.valueOf(item.message[i].timemill);
							}catch(NumberFormatException e){
								e.printStackTrace();
							}
							mw.msgs.add(new PrivateCommonMessage(item.userid, item.message[i], this_time));
						}
					}
					
					Collections.sort(mw.msgs, comparator);
					
					//update info
					mw.updateMessageUser(item);
					mw.addUnreadNum(item.message.length);
					updateTime(item.message);
				}else{
					mw = new MessageWrapper(item.userid, item.message);
					//update info
					mw.updateMessageUser(item);
					mw.addUnreadNum(item.message.length);
					updateTime(item.message);
					messages.put(item.userid, mw);
				}
				
			}

		}
	}
	
	public void updateTime(MyMessage[] myMsgs){
		if(myMsgs!=null && myMsgs.length>0){

			for(int i=0; i<myMsgs.length; i++){

				try{
					long msg_time = Long.valueOf(myMsgs[i].timemill);
					long this_time = Long.valueOf(this.timemill);
					if(myMsgs[i].timemill!=null && msg_time>this_time){
						this.timemill = myMsgs[i].timemill;
					}
				}catch(NumberFormatException e){
					e.printStackTrace();
				}

			
			}
		
		}
		
	}
	
	
	public ArrayList<MessageWrapper> getListExceptStranger(){
		ArrayList<MessageWrapper> results = new ArrayList<MessageWrapper>();
		for(Entry<String, MessageWrapper> m : messages.entrySet()){
			MessageWrapper value = m.getValue();
			if(value!=null && !("5".equals(value.act))){
				results.add(value);
			}
		}
		
		Comparator<MessageWrapper> comparator = new Comparator<MessageWrapper>(){

			@Override
			public int compare(MessageWrapper lhs, MessageWrapper rhs) {
				// TODO Auto-generated method stub
				return (int) (rhs.lastTimeMill-lhs.lastTimeMill);
			}
			
		};
		
		Collections.sort(results, comparator);
		
		return results;
	}
	
	public boolean hasStrangerMsg(){
		boolean hasStrangerMsg = false;
		for(Entry<String, MessageWrapper> m : messages.entrySet()){
			MessageWrapper value = m.getValue();
			if(value!=null && "5".equals(value.act)){
				hasStrangerMsg = true;
			}
		}
		
		return hasStrangerMsg;
	}
	
	public int getStrangerUnReadMsg(){
		int  num = 0;
		for(Entry<String, MessageWrapper> m : messages.entrySet()){
			MessageWrapper value = m.getValue();
			if(value!=null && "5".equals(value.act)){
				num += value.getUnreadMsgs();
			}
		}
		
		return num;
	}
	
	public boolean hasFriendsMsg(){
		boolean hasFriendsMsg = false;
		for(Entry<String, MessageWrapper> m : messages.entrySet()){
			MessageWrapper value = m.getValue();
			if(value!=null && !"5".equals(value.act)){
				hasFriendsMsg = true;
			}
		}
		
		return hasFriendsMsg;
	}
	
	public void cleanMessageByType(int  type){
		Iterator<Entry<String, MessageWrapper>> it = messages.entrySet()
				.iterator();
		while (it.hasNext()) {
			Entry<String, MessageWrapper> item = it.next();
			MessageWrapper mw = item.getValue();
			if(mw!=null){
				mw.cleanMessageByType(type);
				if(mw.msgs.size()==0){
					it.remove();
				}
			}
		}
	}
	
	public void cleanUpStrangers(){		
		Iterator<Entry<String, MessageWrapper>> it = messages.entrySet()
				.iterator();
		while (it.hasNext()) {
			Entry<String, MessageWrapper> item = it.next();
			if (item != null && item.getValue() != null
					&& "5".equals(item.getValue().act)) {
				it.remove();
			}

		}

	}
	
	public void updateActByID(String userid, String act){	
		MessageWrapper item = messages.get(userid);
		if(item!=null){
			item.act = act;
		}

	}
	
	private void AutoReply(String userid, boolean offline) {
		// TODO Auto-generated method stub
		MyMessage mm = new GsonResponse2.MyMessage();
		AccountInfo ai = AccountInfo.getInstance(ActiveAccount.getInstance(MainApplication.getAppInstance()).getLookLookID());
		if(ai.officialUseridsManager!=null && ai.officialUseridsManager.ContainUser(userid)){
			long lastTime = TimeHelper.getInstance().now();
			
			mm.act = "1";
			if(offline){
				mm.privmsg.content = "亲，感谢你对looklook的支持……现在您的网络不太给力哦，稍后您再联系我哦~\n";
			}else{
				mm.privmsg.content = "感谢您对looklook的支持！您的建议已提交，稍后给您答复。谢谢！\n";
			}
			mm.privmsg.privmsg_type = "1"; //私信类型 --- 1代表纯文字 2代表语音 3代表日记      4语音加文字
			mm.timemill = String.valueOf(lastTime);

			MessageWrapper mv = messages.get(userid);
			mv.msgs.add(new PrivateCommonMessage(userid, mm, lastTime));
			mv.privmsg_type = "1";
			mv.content = "[自动回复]";
			mv.lastTimeMill = lastTime;
		}

	}
	
	public void setStatusByUUID(String userid, String uuid,  boolean success){
		Log.d(TAG, "this:" + this + " setStatusByUUID->userid="+userid +" uuid="+uuid+" success="+success);
		boolean hit = false;
		boolean send_ok = false;
		if(userid==null){
			return;
		}
		
		String[] users = userid.split(",");
		if(users!=null && users.length>0){
			for(String user: users){
				MessageWrapper item = messages.get(user);
				Log.d(TAG, "MessageWrapper : " + item + ", str:" + GsonHelper.getInstance().getString(item));
				if(item!=null && item.msgs!=null && item.msgs.size()>0){
					Iterator<PrivateCommonMessage> it = item.msgs.iterator();
					while(it.hasNext()){
						PrivateCommonMessage pcm = it.next();
						if(uuid!=null && uuid.equals(pcm.getUUID())){
							if(success){
								Log.d(TAG, "this:" + this + " setStatusByUUID success");
								pcm.updateSendMsgStatus(SEND_MSG_STATUS.ALL_DONE);
								send_ok = false;
							}else{
								Log.d(TAG, "this:" + this + " setStatusByUUID fail");
								pcm.updateSendMsgStatus(SEND_MSG_STATUS.RIA_FAIL);
								send_ok = true;
							}

							hit = true;
						}
					}
				

				}
				
				if(hit){
					AutoReply(userid, send_ok);
					Intent intent = new Intent(FLAG_OFFLINE_MESSAGE_UPDATE);
					intent.putExtra("userid", user);
					LocalBroadcastManager.getInstance(MainApplication.getAppInstance()).sendBroadcast(intent);
				}
			}
		}


	}
	
	public ArrayList<MessageWrapper> getListForStranger(){
		ArrayList<MessageWrapper> results = new ArrayList<MessageWrapper>();
		for(Entry<String, MessageWrapper> m : messages.entrySet()){
			MessageWrapper value = m.getValue();
			if(value!=null && "5".equals(value.act)){
				results.add(value);
			}
		}
		
		Comparator<MessageWrapper> comparator = new Comparator<MessageWrapper>(){

			@Override
			public int compare(MessageWrapper lhs, MessageWrapper rhs) {
				// TODO Auto-generated method stub
				return (int) (rhs.lastTimeMill-lhs.lastTimeMill);
			}
			
		};
		
		Collections.sort(results, comparator);
		
		return results;
	}
	
	

}
