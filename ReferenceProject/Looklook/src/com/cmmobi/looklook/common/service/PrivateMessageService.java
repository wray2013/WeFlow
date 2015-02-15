package com.cmmobi.looklook.common.service;

import java.util.Calendar;
import java.util.List;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.os.Message;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.Toast;
import cn.zipper.framwork.service.ZService;

import com.cmmobi.looklook.activity.FriendsSessionPrivateMessageActivity;
import com.cmmobi.looklook.common.gson.GsonResponse2.listMessageResponse;
import com.cmmobi.looklook.common.gson.Requester2;
import com.cmmobi.looklook.fragment.FriendsMessageFragment;
import com.cmmobi.looklook.info.profile.AccountInfo;
import com.cmmobi.looklook.info.profile.ActiveAccount;
import com.cmmobi.looklook.info.profile.PrivateMessageManager;
import com.cmmobi.looklook.info.profile.TimeHelper;
import com.cmmobi.looklook.receiver.PrivateMessageReceiver;

public class PrivateMessageService extends ZService {
	private final String TAG = "PrivateMessageService"; 
	
	public final static String FLAG_APP_FRONT_NOTIFY  = "FLAG_APP_FRONT_NOTIFY";
	public final static String FLAG_MESSAGE_DATA_UPDATE  = "FLAG_MESSAGE_DATA_UPDATE";

	public final static int HANDLER_FLAG_MESSAGE_DATA_UPDATE = 0x9739171;
	
	/**
	 * 心跳间隔一分钟
	 */
	private static final long HEARTBEAT_INTERVAL = 30 * 1000;

	private static final int HANDLER_SEND_BROADCAST = 0x78361813;

	//private AlarmManager alarmManager;

	private PendingIntent pendingIntent;
	
	private AccountInfo ai;
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

		handler.removeMessages(HANDLER_SEND_BROADCAST);
        handler.sendEmptyMessageDelayed(HANDLER_SEND_BROADCAST, getHeartBeatInterval());
		
		// 启动心跳定时器

		Calendar calendar = Calendar.getInstance(); 
		calendar.setTimeInMillis(TimeHelper.getInstance().now());
		//alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent); 

		/*long triggerAtTime = SystemClock.elapsedRealtime() + HEARTBEAT_INTERVAL;*/
		//long triggerAtTime = TimeHelper.getInstance().now() + getHeartBeatInterval();
	    //alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, triggerAtTime, HEARTBEAT_INTERVAL, pendingIntent); 
	  	
	
	    //Toast.makeText(this, "PrivateMessageService onStartCommand", Toast.LENGTH_SHORT).show();
		
	    String packageName = this.getPackageName();
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
            	
	    }
	    
	    aa = ActiveAccount.getInstance(this);

	    if(aa.isLogin()){
	    	ai = AccountInfo.getInstance(aa.getLookLookID());
	    	pmm = ai.privateMsgManger;
	    	if(isAppInFront){
			    Requester2.listMessage(handler, pmm.timemill, "20", "0", pmm.commentid, "","");
	    	}else{
	    		Log.e(TAG, "app not in front");			    		
	    	}

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
		case HANDLER_SEND_BROADCAST:
			Intent intent = new Intent("ACTION_HEARTBEAT_PRIVATEMESSAGE_ALARM");
			sendBroadcast(intent);
			break;
		case Requester2.RESPONSE_TYPE_LIST_MESSAGE:
			listMessageResponse obj = (listMessageResponse) msg.obj;
        	Intent msgIntent = new Intent(FLAG_MESSAGE_DATA_UPDATE);
        	msgIntent.putExtra("type", HANDLER_FLAG_MESSAGE_DATA_UPDATE);
			
			if(obj!=null){
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
						ai = AccountInfo.getInstance(aa.getLookLookID());
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
				
				if(obj.commentid!=null){
					pmm.commentid = obj.commentid;
				}
//				Toast.makeText(this, "commentnum="+obj.commentnum, Toast.LENGTH_SHORT).show();
            	msgIntent.putExtra("commentnum", obj.commentnum); //评论数，当评论数返回N时，客户端显示点
            	msgIntent.putExtra("fansnum", obj.fansnum);  //当前用户粉丝数
            	msgIntent.putExtra("attentionnum", obj.attentionnum); //当前用户关注数
            	

			}

			LocalBroadcastManager.getInstance(this).sendBroadcast(msgIntent);			
			break;
		}
		return false;
		
	}
	
	
	private int getHeartBeatInterval(){
		if(FriendsSessionPrivateMessageActivity.heartBeatInterval || FriendsMessageFragment.heartBeatInterval){
			return 5000;
		}else{
			return 30000;
		}
	}
}
