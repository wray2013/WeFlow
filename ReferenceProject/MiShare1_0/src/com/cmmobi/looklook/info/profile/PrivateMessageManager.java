package com.cmmobi.looklook.info.profile;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import android.R.integer;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import cn.zipper.framwork.core.ZLog;

import com.cmmobi.looklook.MainApplication;
import com.cmmobi.looklook.activity.LookLookActivity;
import com.cmmobi.looklook.common.constant.Constant.SEND_MSG_STATUS;
import com.cmmobi.looklook.common.gson.GsonResponse3;
import com.cmmobi.looklook.common.gson.GsonResponse3.HistoryMessage;
import com.cmmobi.looklook.common.gson.GsonResponse3.MyMessage;
import com.cmmobi.looklook.common.gson.GsonResponse3.listMessageResponse;
import com.cmmobi.looklook.common.storage.GsonHelper;
import com.cmmobi.looklook.common.storage.SqlMessageWrapperManager;
import com.cmmobi.looklook.common.utils.DateUtils;
import com.cmmobi.looklook.jpush.MessageManager.ComparatorMessage;
import com.tencent.mm.sdk.platformtools.Log;

/**
 * @author zhangwei
 * 私信+心跳(角标)+评论数
 * */
public class PrivateMessageManager {

	private static transient final String TAG="PrivateMessageManager";
	public static transient final String FLAG_OFFLINE_MESSAGE_UPDATE = "FLAG_OFFLINE_MESSAGE_UPDATE";
	
	//public HashMap<String, MessageWrapper>  messages;
	public String commentid;
	public String commentid_safebox;
	public String timemill;
	public HeartSubscript hSubScript;
	private String my_userid;
	
	//public int unReadMsg;
	public void updateUser(String my_userid){
		this.my_userid = my_userid;
	}
	
	public PrivateMessageManager(String my_userid){
		//messages = new HashMap<String, MessageWrapper>();
		this.my_userid = my_userid;
		hSubScript = new HeartSubscript();
		timemill = "0";
		//unReadMsg = 0;
	}
	
/*	public void resetUnReadNum(){
		unReadMsg = 0;
	}*/
	private void checkUserid(){
		if(TextUtils.isEmpty(my_userid)){
			my_userid = ActiveAccount.getInstance(MainApplication.getAppInstance()).getLookLookID();
		}
	}
	
	public int getUnReadNum(){
		int unReadMsg = 0;
		checkUserid();
		ArrayList<MessageWrapper> list = SqlMessageWrapperManager.getInstance().getListExceptStranger(my_userid);
		for(MessageWrapper item : list){
			if(item!=null && item.toShow/*  && !"5".equals(item.act)*/){
				unReadMsg += item.getUnreadMsgs();
			}
		}
/*		for(Entry<String, MessageWrapper> m : messages.entrySet()){
			MessageWrapper value = m.getValue();
			if(value!=null  && !"5".equals(value.act)){
				unReadMsg += value.getUnreadMsgs();
			}
		}*/
		return unReadMsg;
	}
	
	
	public void addMessage(GsonResponse3.MessageUser item){
		checkUserid();
		if(item!=null && item.userid!=null){
/*			if(item.message!=null && item.message.length>0){
				unReadMsg += item.message.length;
			}*/
			
			//MessageWrapper mw = messages.get(item.userid);
			
			// 表示是客服
			if("5".equals(item.usertype)){
				String uid = ActiveAccount.getInstance(MainApplication.getAppInstance()).getUID();
				AccountInfo acc = AccountInfo.getInstance(uid);
				acc.serviceUser.userid = item.userid;
				acc.persist();
			}
			
			MessageWrapper mw = SqlMessageWrapperManager.getInstance().getMessageWrapper(my_userid, item.userid);
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
							
							if(!"1".equals(item.message[i].privmsg.privmsg_type) && "1".equals(item.message[i].isread)){
								item.message[i].notReadLocalAudio = true;
							}else{
								item.message[i].notReadLocalAudio = false;
							}
							mw.msgs.add(new PrivateCommonMessage(item.userid, item.message[i], this_time));
							
						}
					}
					
					Collections.sort(mw.msgs, new MyComparator());
					
					//update info
					mw.updateMessageUser(item);
//					mw.addUnreadNum(item.message.length);
					updateTime(item.message);
				}else{
					mw = new MessageWrapper(my_userid, item.userid, item.message);
					//update info
					mw.updateMessageUser(item);
									
					updateTime(item.message);
					//messages.put(item.userid, mw);
				}
				
				// 更新显示状态
				mw.setToShow(true);
				
				SqlMessageWrapperManager.getInstance().putMessageWrapper(my_userid, mw);
				
			}

		}
	}
	
	public void updateReadedMessageById(String messageIds){
		if(messageIds==null || messageIds.isEmpty()) return;
		String[] ids = messageIds.split(",");
		checkUserid();
		ArrayList<MessageWrapper> list = SqlMessageWrapperManager.getInstance().getMessages(my_userid);
		//Log.e(TAG, "==== list.size() " + list.size() + ", my_userid" + my_userid);
		Boolean isFound = false;
		for(int i=0; i<ids.length; i++){
			isFound = false;
			for(MessageWrapper item : list){
				if(item!=null){
					//Log.e(TAG, "==== item.msgs.size() " + item.msgs.size());

					for(int j=0; j< item.msgs.size(); j++){
					//	System.out.println("==== item.msgs " + j + ", id " + item.msgs.get(j).getRMsgId());
						if(ids[i].equals(item.msgs.get(j).getRMsgId())){
						//	Log.e(TAG, "====readedMessage == " + ids[i] + " == isFound" + "== isRead " + item.msgs.get(j).r_msg.isread);
							if("1".equals(item.msgs.get(j).r_msg.isread)){
							item.msgs.get(j).r_msg.isread = "2";
							SqlMessageWrapperManager.getInstance().putMessageWrapper(my_userid, item);
							}
							isFound = true;
							break;
						}
					}
				}
				if(isFound) {
					break;
				}
			}	
		}
	}
	
	public void addHistoryMessage(MyMessage[] message, String other_userid,String my_userid,WrapUser wu) {
		checkUserid();
		//MessageWrapper mw = messages.get(other_userid);
		MessageWrapper mw  = SqlMessageWrapperManager.getInstance().getMessageWrapper(my_userid, other_userid);
		if(mw!=null && mw.msgs!=null){
			//update info
			updateTime(message);
		}else{
			
			mw = new MessageWrapper(my_userid);
			//messages.put(other_userid, mw);
		}
		
		for(int i=0; i<message.length; i++){
			if(!mw.contains(message[i])){
				long this_time = 0;
				try{
					this_time = Long.valueOf(message[i].timemill);
				}catch(NumberFormatException e){
					e.printStackTrace();
				}
				// 自己发
				if("1".equals(message[i].isowner)){
					
					PrivateSendMessage send_msg = new PrivateSendMessage(my_userid, other_userid, message[i].timemill);
					send_msg.setContent(message[i].content);
					send_msg.setPrivateMsgtype(message[i].privmsg.privmsg_type);
					send_msg.setPrivatemsgid(message[i].messageid);
					send_msg.updateStatus(SEND_MSG_STATUS.ALL_DONE);
					send_msg.audiopath = message[i].privmsg.audiourl;
					send_msg.playtime = message[i].privmsg.playtime;
					mw.msgs.addFirst(new PrivateCommonMessage(send_msg, this_time));
				}else{
					mw.msgs.addFirst(new PrivateCommonMessage(other_userid, message[i], this_time));
				}
			}
		}
		mw.updateHistoryMessageUser(wu);
		// 更新显示状态
		mw.setToShow(true);
		
		Collections.sort(mw.msgs, new MyComparator());
		
		SqlMessageWrapperManager.getInstance().putMessageWrapper(my_userid, mw);
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
	
	
	public ArrayList<MessageWrapper> getAllMessageList(){
/*		ArrayList<MessageWrapper> results = new ArrayList<MessageWrapper>();
		for(Entry<String, MessageWrapper> m : messages.entrySet()){
			MessageWrapper value = m.getValue();
			if(value!=null && !("5".equals(value.act))){
				results.add(value);
			}
		}*/
		checkUserid();
		ArrayList<MessageWrapper> results = SqlMessageWrapperManager.getInstance().getMessages(my_userid);
		
		Comparator<MessageWrapper> comparator = new Comparator<MessageWrapper>(){

			@Override
			public int compare(MessageWrapper lhs, MessageWrapper rhs) {
				// TODO Auto-generated method stub
				if (rhs.lastTimeMill > lhs.lastTimeMill){
					return 1;
				}
				if (rhs.lastTimeMill == lhs.lastTimeMill){
					return 0;
				}
				return -1;
				
			}
			
		};
		
		Collections.sort(results, comparator);
		
		return results;
	}
	
	public ArrayList<MessageWrapper> getListExceptStranger(){
		/*		ArrayList<MessageWrapper> results = new ArrayList<MessageWrapper>();
				for(Entry<String, MessageWrapper> m : messages.entrySet()){
					MessageWrapper value = m.getValue();
					if(value!=null && !("5".equals(value.act))){
						results.add(value);
					}
				}*/
				checkUserid();
				ArrayList<MessageWrapper> results = SqlMessageWrapperManager.getInstance().getListExceptStranger(my_userid);
				
				Comparator<MessageWrapper> comparator = new Comparator<MessageWrapper>(){

					@Override
					public int compare(MessageWrapper lhs, MessageWrapper rhs) {
						// TODO Auto-generated method stub
						if (rhs.lastTimeMill > lhs.lastTimeMill){
							return 1;
						}
						if (rhs.lastTimeMill == lhs.lastTimeMill){
							return 0;
						}
						return -1;
						
					}
					
				};
				
				Collections.sort(results, comparator);
				
				return results;
			}
	
	public boolean hasStrangerMsg(){
		boolean hasStrangerMsg = false;
		/*for(Entry<String, MessageWrapper> m : messages.entrySet()){
			MessageWrapper value = m.getValue();
			if(value!=null && "5".equals(value.act)){
				hasStrangerMsg = true;
			}
		}*/
		
		return hasStrangerMsg;
	}
	
	/**
	 * 修改完备注名后更新私信备注名
	 * @param otherUserid
	 * @param markname
	 */
	public void updateMarkName(String otherUserid,String markname){
		if(TextUtils.isEmpty(otherUserid)|| TextUtils.isEmpty(markname)){
			return;
		}
		checkUserid();
		MessageWrapper mw = SqlMessageWrapperManager.getInstance().getMessageWrapper(my_userid, otherUserid);
		mw.markname = markname;
		SqlMessageWrapperManager.getInstance().putMessageWrapper(my_userid, mw);
		/*		for(Entry<String, MessageWrapper> m : messages.entrySet()){
			String key = m.getKey();
			MessageWrapper value = m.getValue();
			if(otherUserid.equals(key)){
				value.markname = markname;
				return;
			}
		}*/
	}
	
	/**
	 * 添加好友后 更新好友消息
	 * @param strangerUserid
	 */
	public void checkStrangerNeed2Friend(Context ctx, String strangerUserid){
		
		if(TextUtils.isEmpty(strangerUserid)){
			return;
		}
		checkUserid();
		AccountInfo accountInfo = AccountInfo.getInstance(ActiveAccount.getInstance(ctx).getUID());
		ContactManager friendsListName = accountInfo.friendsListName;
		if(friendsListName.isMemberByUserid(strangerUserid)){
			MessageWrapper value = SqlMessageWrapperManager.getInstance().getMessageWrapper(my_userid, strangerUserid);
			if(value!=null && "5".equals(value.act)){
				value.act = "1";
			}
			SqlMessageWrapperManager.getInstance().putMessageWrapper(my_userid, value);
			/*			MessageWrapper value = messages.get(strangerUserid);
			if(value!=null && "5".equals(value.act)){
				value.act = "1";
			}*/
		}
	}
	
	/**
	 * 好友变成陌生人
	 * @param ctx
	 * @param fuid
	 */
	public void Friends2Stranger(String fuid){
		if(TextUtils.isEmpty(fuid)){
			return;
		}
		checkUserid();
		MessageWrapper value = SqlMessageWrapperManager.getInstance().getMessageWrapper(my_userid, fuid);
		if(value!=null && "1".equals(value.act)){
			value.act = "5";
		}
		SqlMessageWrapperManager.getInstance().putMessageWrapper(my_userid, value);
	}
	
	/**
	 * 获取所有的陌生人的id
	 * @return
	 */
	public ArrayList<String> getStrangerUserid(){
		checkUserid();
		ArrayList<String> arr = new ArrayList<String>();
		ArrayList<MessageWrapper> mws = SqlMessageWrapperManager.getInstance().getListForStranger(my_userid);
		if(mws!=null && mws.size()>0){
			for(MessageWrapper item : mws){
				arr.add(item.other_userid);
			}
		}
/*		for(Entry<String, MessageWrapper> m : messages.entrySet()){
			String key = m.getKey();
			MessageWrapper value = m.getValue();
			if(value!=null && "5".equals(value.act)){
				arr.add(key);
			}
		}*/
		return arr;
	}
	
	public int getStrangerUnReadMsg(){
		int  num = 0;
		/*for(Entry<String, MessageWrapper> m : messages.entrySet()){
			MessageWrapper value = m.getValue();
			if(value!=null && "5".equals(value.act)){
				num += value.getUnreadMsgs();
			}
		}
		*/
		return num;
	}
	
	public boolean hasFriendsMsg(){
		checkUserid();
/*		boolean hasFriendsMsg = false;
		for(Entry<String, MessageWrapper> m : messages.entrySet()){
			MessageWrapper value = m.getValue();
			if(value!=null && !"5".equals(value.act)){
				hasFriendsMsg = true;
			}
		}
		
		return hasFriendsMsg;*/
		return SqlMessageWrapperManager.getInstance().hasExceptStranger(my_userid);
	}
	
	public void put(String other_userid, MessageWrapper mw) {
		// TODO Auto-generated method stub
		checkUserid();
		SqlMessageWrapperManager.getInstance().putMessageWrapper(my_userid, mw);
	}
	
	public MessageWrapper get(String other_userid){
		checkUserid();
		return SqlMessageWrapperManager.getInstance().getMessageWrapper(my_userid, other_userid);
	}
	
	public void remove(String other_userid){
		checkUserid();
		SqlMessageWrapperManager.getInstance().removeMessageWrapper(my_userid, other_userid);
	}
	
	public void hideMsg(String other_userid){
		checkUserid();
		ArrayList<MessageWrapper> list = SqlMessageWrapperManager.getInstance().getListExceptStranger(my_userid);
		if(list!=null && list.size()>0){
			for(MessageWrapper item : list){
				if(item.other_userid!=null && item.other_userid.equals(other_userid)){
					// 更新消息显示状态  del
					item.setToShow(false);
					SqlMessageWrapperManager.getInstance().putMessageWrapper(my_userid, item);
				}
			}
		}
			
	}
	
	public void cleanMessageByType(int  type){
		checkUserid();
		ArrayList<MessageWrapper> list = SqlMessageWrapperManager.getInstance().getMessages(my_userid);
		if(list!=null && list.size()>0){
			for(MessageWrapper mw : list){
				if(mw!=null){
					mw.cleanMessageByType(type);
					if(mw.msgs.size()==0){
						SqlMessageWrapperManager.getInstance().removeMessageWrapper(my_userid, mw.other_userid);
					}else{
						SqlMessageWrapperManager.getInstance().putMessageWrapper(my_userid, mw);
					}
				}
			}
		}

/*		Iterator<Entry<String, MessageWrapper>> it = messages.entrySet()
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
		}*/
	}
	
	public void cleanUpStrangers(){		
/*		Iterator<Entry<String, MessageWrapper>> it = messages.entrySet()
				.iterator();
		while (it.hasNext()) {
			Entry<String, MessageWrapper> item = it.next();
			if (item != null && item.getValue() != null
					&& "5".equals(item.getValue().act)) {
				it.remove();
			}

		}*/
		checkUserid();
		ArrayList<MessageWrapper> list = SqlMessageWrapperManager.getInstance().getListExceptStranger(my_userid);
	    if(list!=null && list.size()>0){
	    	for(MessageWrapper item : list){
	    		 SqlMessageWrapperManager.getInstance().removeMessageWrapper(my_userid, item.other_userid);
	    	}
	    }
	}
	
	public void cleanUpExceptStrangers(){		
/*		Iterator<Entry<String, MessageWrapper>> it = messages.entrySet()
				.iterator();
		while (it.hasNext()) {
			Entry<String, MessageWrapper> item = it.next();
			if (item != null && item.getValue() != null
					&& !"5".equals(item.getValue().act)) {
				it.remove();
			}
		}*/
		checkUserid();
		ArrayList<MessageWrapper> list = SqlMessageWrapperManager.getInstance().getListExceptStranger(my_userid);
	    if(list!=null && list.size()>0){
	    	for(MessageWrapper item : list){
	    		 SqlMessageWrapperManager.getInstance().removeMessageWrapper(my_userid, item.other_userid);
	    	}
	    }
	    
	}
	
	public void hideUpExceptStrangers(){		
		checkUserid();
		ArrayList<MessageWrapper> list = SqlMessageWrapperManager.getInstance().getListExceptStranger(my_userid);
		if(list!=null && list.size()>0){
			for(MessageWrapper item : list){
				// 更新消息显示状态  del
				item.setToShow(false);
				SqlMessageWrapperManager.getInstance().putMessageWrapper(my_userid, item);
			}
		}
		
		
		
	}
	
	public void updateActByID(String other_userid, String act){	
		//MessageWrapper item = messages.get(userid);
		checkUserid();
		MessageWrapper item = SqlMessageWrapperManager.getInstance().getMessageWrapper(my_userid, other_userid);
		if(item!=null){
			item.act = act;
		}
		SqlMessageWrapperManager.getInstance().putMessageWrapper(my_userid, item);
	}
	
	private void AutoReply(String other_userid, boolean offline) {
		checkUserid();
		// TODO Auto-generated method stub
		MyMessage mm = new GsonResponse3.MyMessage();
		AccountInfo ai = AccountInfo.getInstance(ActiveAccount.getInstance(MainApplication.getAppInstance()).getLookLookID());
		if(ai.serviceUser.userid!=null && !ai.serviceUser.userid.isEmpty() && ai.serviceUser.userid.equals(other_userid)){
			long lastTime = TimeHelper.getInstance().now();
			
			mm.act = "1";
			if(offline){
				mm.privmsg.content = "亲，感谢你对\"" + LookLookActivity.APP_NAME + "app\"的支持……现在您的网络不太给力哦，稍后您再联系我哦~\n";
			}else{
				mm.privmsg.content = "感谢您对\"" + LookLookActivity.APP_NAME + "app\"的支持！您的建议已提交，稍后给您答复。谢谢！\n";
			}
			mm.privmsg.privmsg_type = "1"; //私信类型 --- 1代表纯文字 2代表语音 3代表日记      4语音加文字
			mm.timemill = String.valueOf(lastTime);

			/*MessageWrapper mv = messages.get(userid);*/
			MessageWrapper mv = SqlMessageWrapperManager.getInstance().getMessageWrapper(my_userid, other_userid);
			
			if("[自动回复]".equals(mv.content)){
			}else{
				mv.msgs.add(new PrivateCommonMessage(other_userid, mm, lastTime));
				mv.privmsg_type = "1";
				mv.content = "[自动回复]";
				mv.lastTimeMill = lastTime;
			}
		}

	}
	
	public void setStatusByUUID(String other_userids_str, String uuid,  boolean success){
		checkUserid();
		Log.d(TAG, "this:" + this + " setStatusByUUID->userid="+other_userids_str +" uuid="+uuid+" success="+success);
		boolean hit = false;
		boolean send_ok = false;
		if(other_userids_str==null){
			return;
		}
		
		String[] users = other_userids_str.split(",");
		if(users!=null && users.length>0){
			for(String user: users){
				/*MessageWrapper item = messages.get(user);*/
				MessageWrapper item = SqlMessageWrapperManager.getInstance().getMessageWrapper(my_userid, other_userids_str);
				//Log.d(TAG, "MessageWrapper : " + item + ", str:" + GsonHelper.getInstance().getString(item));
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
					SqlMessageWrapperManager.getInstance().putMessageWrapper(my_userid, item);
					
				}
				
				if(hit){
					AutoReply(other_userids_str, send_ok);
					Intent intent = new Intent(FLAG_OFFLINE_MESSAGE_UPDATE);
					intent.putExtra("userid", user);
					LocalBroadcastManager.getInstance(MainApplication.getAppInstance()).sendBroadcast(intent);
				}
			}
		}


	}
	
	public ArrayList<MessageWrapper> getListForStranger(){
/*		ArrayList<MessageWrapper> results = new ArrayList<MessageWrapper>();
		for(Entry<String, MessageWrapper> m : messages.entrySet()){
			MessageWrapper value = m.getValue();
			if(value!=null && "5".equals(value.act)){
				results.add(value);
			}
		}*/
		checkUserid();
		ArrayList<MessageWrapper> results = SqlMessageWrapperManager.getInstance().getListForStranger(my_userid);
		
		Comparator<MessageWrapper> comparator = new Comparator<MessageWrapper>(){

			@Override
			public int compare(MessageWrapper lhs, MessageWrapper rhs) {
				// TODO Auto-generated method stub
				
				if (rhs.lastTimeMill > lhs.lastTimeMill){
					return 1;
				}
				if (rhs.lastTimeMill == lhs.lastTimeMill){
					return 0;
				}
				return -1;
			}
			
		};
		
		Collections.sort(results, comparator);
		
		return results;
	}
	
	public HeartSubscript updateHeartSubscript(listMessageResponse obj){
		hSubScript.new_zonemicnum = obj.new_zonemicnum;
		hSubScript.new_friend_change = obj.new_friend_change;
		hSubScript.new_requestnum = obj.new_requestnum;
		hSubScript.friendnum = obj.friendnum;
		hSubScript.t_push = obj.t_push;
		return hSubScript;
	}
	
	/**
	 * 角标
	 */
	public class HeartSubscript{
		
		/**  请求时间戳
		 ************/
		public String t_zone_mic;  //微享新动态时间戳
		public String t_zone_miccomment; //微享新评论时间戳
		public String t_safebox_miccomment;  //微享新评论时间戳(保险箱内)
		public String t_friend;  //通讯录朋友列表时间戳
		public String t_friend_change; //通讯录朋友列表动态时间戳
		public String t_friendrequest; //好友请求列表时间戳
		public String t_push;  //push时间戳
		
		/**  返回的角标数
		 ************/
		public String new_zonemicnum; //微享新动态数时间戳
		public String new_safeboxmicnum; //微享是否有新动态数 1有 0没有(保险箱内)
		public String new_friend_change; //好友新动态数
		public String new_friend; //是否有新通讯录好友 1有 0没有
		public String new_requestnum;  //新增好友请求数
		public String friendnum;  
		
	}

	public String commentNum;
	public String commentnum_safebox;
	
	/**
	 * 心跳返回的数据，更新评论数
	 */
	public synchronized void heartUpdateCommentnum(String commentnum){
		commentNum = commentnum;
	}
	
	/**
	 * 获取未读评论数
	 * @return
	 */
	public synchronized int getUnReadCommentNum(){
		int num = 0;
		try {
			num = Integer.parseInt(commentNum);
		} catch (Exception e) {
		}
		return num;
	}
	
	/**
	 * 已读，清除评论数
	 * @param newCommentid
	 */
	public synchronized void cleanCommentNum(){
		commentNum = "0";
	}
	/**
	 * 心跳返回的数据，更新评论数
	 */
	public synchronized void heartUpdateSafeboxCommentnum(String commentnum_safebox){
		this.commentnum_safebox = commentnum_safebox;
	}
	
	/**
	 * 获取未读评论数
	 * @return
	 */
	public synchronized int getUnReadSafeboxCommentNum(){
		int num = 0;
		try {
			num = Integer.parseInt(commentnum_safebox);
		} catch (Exception e) {
		}
		return num;
	}
	
	/**
	 * 已读，清除评论数
	 * @param newCommentid
	 */
	public synchronized void cleanSafeboxCommentNum(){
		commentnum_safebox = "0";
	}


	class MyComparator implements Comparator<PrivateCommonMessage>{

		@Override
		public int compare(PrivateCommonMessage lhs, PrivateCommonMessage rhs) {
			try {
				
				if (lhs.create_time > rhs.create_time){
					return 1;
				}
				if (lhs.create_time == rhs.create_time){
					return 0;
				}
			} catch (Exception e) {
			}
			return -1;
		}
		
	}

}
