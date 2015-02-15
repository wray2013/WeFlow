package com.cmmobi.looklook.common.service;

import java.util.Calendar;

import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.Message;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import cn.zipper.framwork.service.ZService;

import com.cmmobi.looklook.common.gson.GsonResponse3.listMessageResponse;
import com.cmmobi.looklook.common.gson.Requester3;
import com.cmmobi.looklook.common.gson.UDPRequester;
import com.cmmobi.looklook.common.utils.MetaUtil;
import com.cmmobi.looklook.info.profile.AccountInfo;
import com.cmmobi.looklook.info.profile.ActiveAccount;
import com.cmmobi.looklook.info.profile.PrivateMessageManager;
import com.cmmobi.looklook.info.profile.PrivateMessageManager.HeartSubscript;
import com.cmmobi.looklook.info.profile.TimeHelper;
import com.cmmobi.looklook.networktask.NetworkTaskManager;
import com.cmmobi.looklook.receiver.CmmobiPushReceiver;
import com.cmmobi.looklook.receiver.PrivateMessageReceiver;
import com.google.gson.Gson;

public class PrivateMessageService extends ZService {
	private final String TAG = "PrivateMessageService"; 
	
	public final static String FLAG_APP_FRONT_NOTIFY  = "FLAG_APP_FRONT_NOTIFY";
	public final static String ACTION_MESSAGE_DATA_UPDATE  = "ACTION_MESSAGE_DATA_UPDATE";

	public final static int HANDLER_FLAG_MESSAGE_DATA_UPDATE = 0x9739171;

	private static final int HANDLER_SEND_BROADCAST = 0x78361813;

	public static final String SYNC_TIMELINE = "SYNC_TIMELINE";

	//private AlarmManager alarmManager;

	private PendingIntent pendingIntent;
	
	private ActiveAccount aa;
	private PrivateMessageManager pmm;
	//private boolean needSyncAccountInfo;

	boolean isAppInFront;
	
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		//alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
		pendingIntent = PendingIntent.getBroadcast(this, 0, new Intent(
				PrivateMessageReceiver.ACTION_HEARTBEAT_PRIVATEMESSAGE_ALARM), PendingIntent.FLAG_UPDATE_CURRENT);
		//needSyncAccountInfo = false;

	    
	    
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Log.e(TAG, "onStartCommand, flags:" + flags + " startId" + startId);

/*		handler.removeMessages(HANDLER_SEND_BROADCAST);
        handler.sendEmptyMessageDelayed(HANDLER_SEND_BROADCAST, Constant.HEARTBEAT_INTERVAL);
		*/
		// 启动心跳定时器

		Calendar calendar = Calendar.getInstance(); 
		calendar.setTimeInMillis(TimeHelper.getInstance().now());
	
/*	    String packageName = this.getPackageName();
	    ActivityManager activityManager = (ActivityManager)this.getSystemService(Context.ACTIVITY_SERVICE);
	    List<RunningTaskInfo> appTask = activityManager.getRunningTasks(1);
	    
	    isAppInFront = false;
	    if(appTask!=null && appTask.size()>0){
            if(appTask.get(0).topActivity.toString().contains(packageName)){
            	isAppInFront = true;
            	Intent frontIntent = new Intent(FLAG_APP_FRONT_NOTIFY);
            	frontIntent.putExtra("appInFront", true);
            	LocalBroadcastManager.getInstance(this).sendBroadcast(frontIntent);
            }else{
            	isAppInFront = false;
            	Intent frontIntent = new Intent(FLAG_APP_FRONT_NOTIFY);
            	frontIntent.putExtra("appInFront", false);
            	LocalBroadcastManager.getInstance(this).sendBroadcast(frontIntent);
            }
            	
	    }*/
		

	    
	    aa = ActiveAccount.getInstance(this);
	    
	    Log.e(TAG, "ActiveAccount in privateMsgService:" + aa);

	    if(aa.isLogin()){
	    	
	    	//只有登录用户才有必要发送udp
			int need_sync = (intent==null)?0:intent.getIntExtra(SYNC_TIMELINE, 0);
			int cmmobi_channel = MetaUtil.getIntValue("CMMOBI_APPKEY");
			
			UDPRequester.sendUDP(this, handler, need_sync, cmmobi_channel+"", aa.getLookLookID());
			NetworkTaskManager.getInstance(aa.getLookLookID()).restartNextTask();

	    }else{
	    	Log.e(TAG, "app not login");
	    }  

		return Service.START_STICKY;
	}

	@Override
	public void onDestroy() {

		super.onDestroy();
	}
	
	@Override
	public boolean handleMessage(Message msg) {
		// TODO Auto-generated method stub
		switch (msg.what) {
/*		case HANDLER_SEND_BROADCAST:
			Intent intent = new Intent(PrivateMessageReceiver.ACTION_HEARTBEAT_PRIVATEMESSAGE_ALARM);
			sendBroadcast(intent);
			break;*/
		case UDPRequester.HANDLER_FLAG_UDP_OP_DONE:
		    aa = ActiveAccount.getInstance(this);
		    if(aa.isLogin()){
		    	//只有登录用户才有必要发送udp
				int cmmobi_channel = MetaUtil.getIntValue("CMMOBI_APPKEY");
				UDPRequester.sendUDP(this, handler, 0, cmmobi_channel + "", aa.getLookLookID());
		    } 
			break;
		case UDPRequester.HANDLER_FLAG_UDP_TIME_SYNC:
			try{
				long server_time = Long.parseLong((String)(msg.obj));
				TimeHelper.getInstance().syncServerTime(server_time);
			}catch(Exception e){
				e.printStackTrace();
			}
			break;
		case UDPRequester.HANDLER_FLAG_UDP_NEW_MSG:
		    if(aa.isLogin()){
		    	//if(isAppInFront){
		    	AccountInfo ai = AccountInfo.getInstance(aa.getLookLookID());
		    	pmm = ai.privateMsgManger;
		    	String commentid = "";
		    	if(ai.myAcceptComments.size()>0){
		    		commentid = ai.myAcceptComments.get(0).commentid;
		    	}
			    Requester3.listMessage(handler, aa.getLookLookID(), pmm.timemill, "20", "0", commentid, 
			    						pmm.hSubScript.t_zone_mic,
			    						pmm.hSubScript.t_friend,
			    						pmm.hSubScript.t_attention,
			    						pmm.hSubScript.t_fans,
			    						pmm.hSubScript.t_recommend,
			    						pmm.hSubScript.t_snsfriend,
			    						pmm.hSubScript.t_friendrequest,
			    						pmm.hSubScript.t_friend_change,
			    						pmm.hSubScript.t_push,
			    						pmm.hSubScript.t_zone_miccomment
			    						);
		    }else{
		    	Log.e(TAG, "app not login");
		    }
			break;
		case Requester3.RESPONSE_TYPE_LIST_MESSAGE:
			listMessageResponse obj = (listMessageResponse) msg.obj;
			
			if(obj == null || !"0".equals(obj.status)){
				return false;
			}
			
        	Intent msgIntent = new Intent(ACTION_MESSAGE_DATA_UPDATE);
        	msgIntent.putExtra("type", HANDLER_FLAG_MESSAGE_DATA_UPDATE);
			
			if(obj.server_time!=null){
				try{
					long server_time = Long.parseLong(obj.server_time);
					TimeHelper.getInstance().syncServerTime(server_time);
				}catch(Exception e){
					e.printStackTrace();
				}

			}
			if(obj.users!=null && obj.users.length>0){
				aa = ActiveAccount.getInstance(this);
				if(aa.isLogin()){
					AccountInfo ai = AccountInfo.getInstance(aa.getLookLookID());
					pmm = ai.privateMsgManger;
					
					//String[] change_friends = new String[obj.users.length];
					for(int i=0; i<obj.users.length; i++){
						if(obj.users[i]!=null){
							pmm.addMessage(obj.users[i]);
						}
					}
					
	            	msgIntent.putExtra("hasStrangerMsg", pmm.hasStrangerMsg());
	            	msgIntent.putExtra("hasFriendMsg", pmm.hasFriendsMsg());
				}
			}
			
			updateHSubscriptBroadcast(msgIntent,pmm.updateHeartSubscript(obj));
			
			pmm.heartUpdateCommentnum(obj.commentnum);
			
//			if(obj.commentid!=null){
//				pmm.commentid = obj.commentid;
//			}

        	msgIntent.putExtra("commentnum", obj.commentnum); //评论数，当评论数返回N时，客户端显示点
        	msgIntent.putExtra("fansnum", obj.fansnum);  //当前用户粉丝数
        	msgIntent.putExtra("attentionnum", obj.attentionnum); //当前用户关注数
        	
        	//通讯录相关
        	AccountInfo accountInfo = AccountInfo.getInstance(ActiveAccount.getInstance(null).getUID());
        	try {
        		int c = 0;
				if(obj.new_fansnum!=null){
					c = Integer.parseInt(obj.new_fansnum);
					if(c >= 0){
						accountInfo.newFansCount = c;
						System.out.println("==fan=== newFansCount =====" + c);
					}
				}
				
				c = 0;
				if(obj.new_snsfriendnum!=null){
					c = Integer.parseInt(obj.new_snsfriendnum);
					if(c >= 0){
						accountInfo.newSNSFriendsCount = c;
						System.out.println("==friend=== newSNSFriendsCount =====" + c + ", " + accountInfo.newSNSFriendsCount) ;
					}
				}
				
				c = 0;
				if(obj.new_recommendnum!=null){
					c = Integer.parseInt(obj.new_recommendnum);
					if(c >= 0){
						accountInfo.newRecommendCount = c;
						System.out.println("==recommend=== newRecommendCount =====" + c + ", " + accountInfo.newRecommendCount) ;
						}
				}
				
				c = 0;
				if(obj.new_friend_change!=null){
					c = Integer.parseInt(obj.new_friend_change);
					if(c >= 0){
						accountInfo.newFriendUpdateCount = c;
						System.out.println("==friend change=== newFriendUpdateCount =====" + c);
					}
				}
				
				c = 0;
				if(obj.new_attention_change!=null){
					c = Integer.parseInt(obj.new_attention_change);
					if(c >= 0){
						accountInfo.newAttentionUpdateCount = c;
						System.out.println("==attention=== newAttentionUpdateCount =====" + c);
					}
				}
				
				c = 0;
				if(obj.new_requestnum!=null){
					c = Integer.parseInt(obj.new_requestnum);
					if(c >= 0){
						accountInfo.newFriendRequestCount = c;
						System.out.println("==friendsRequest=== new_requestnum =====" + c);
					}
				}
				
				c = 0;
				if(obj.new_zonemicnum!=null){
					c = Integer.parseInt(obj.new_zonemicnum);
					if(c >= 0){
						accountInfo.newZoneMicCount = c + accountInfo.newZoneMicCount;
						System.out.println("===== newZoneMicCount =====" + c + ", " + accountInfo.newZoneMicCount);
					}
				}
				
			} catch (Exception e) {
				// TODO: handle exception
			}
        	
        	
        	LocalBroadcastManager.getInstance(this).sendBroadcast(msgIntent);			

        	if(obj.push!=null && obj.push.length>0 ){
        		Intent pushIntent = new Intent(CmmobiPushReceiver.ACTION_CMMOBI_PUSH_RECEIVER);
        		pushIntent.putExtra(CmmobiPushReceiver.PUSH_JSON, new Gson().toJson(obj.push));
//        		LocalBroadcastManager.getInstance(this).sendBroadcast(pushIntent);			
        		sendBroadcast(pushIntent);
        	}
        	
			break;
		}
		return false;
		
	}
	
	private void updateHSubscriptBroadcast(Intent msgIntent,HeartSubscript heart){
		if(heart == null){
			return;
		}
		msgIntent.putExtra("new_zonemicnum", heart.new_zonemicnum);          //微享新动态数时间戳
		msgIntent.putExtra("new_friend_change", heart.new_friend_change);        //好友新动态数
		msgIntent.putExtra("new_attention_change", heart.new_attention_change);       //订阅新动态数
		msgIntent.putExtra("new_fansnum", heart.new_fansnum);         // 新增粉丝数
		msgIntent.putExtra("new_recommendnum", heart.new_recommendnum);        //新增推荐用户数
		msgIntent.putExtra("new_snsfriendnum", heart.new_snsfriendnum);        //新增第三方好友数
		msgIntent.putExtra("new_requestnum", heart.new_requestnum);        //新增好友请求数
		msgIntent.putExtra("friendnum", heart.friendnum);            //当前朋友数
		msgIntent.putExtra("fansnum", heart.fansnum);
		msgIntent.putExtra("attentionnum", heart.attentionnum);
		
	}
	

}
