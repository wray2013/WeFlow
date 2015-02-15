package com.cmmobi.looklook.common.service;



import android.app.AlarmManager;
import android.app.DownloadManager.Request;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.IBinder;
import android.os.Message;
import android.os.SystemClock;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import cn.zipper.framwork.service.ZService;

import com.cmmobi.looklook.common.cache.CacheManager;
import com.cmmobi.looklook.common.gson.GsonRequest2.postFollowItem;
import com.cmmobi.looklook.common.gson.GsonResponse2;
import com.cmmobi.looklook.common.gson.Requester2;
import com.cmmobi.looklook.common.gson.WeiboResponse.RWUser;
import com.cmmobi.looklook.common.gson.WeiboResponse.sinaUser;
import com.cmmobi.looklook.common.gson.WeiboResponse.tencentInfo;
import com.cmmobi.looklook.info.profile.AccountInfo;
import com.cmmobi.looklook.info.profile.ActiveAccount;
import com.cmmobi.looklook.info.profile.TimeHelper;
import com.cmmobi.looklook.info.weather.MyWeatherInfo;
import com.cmmobi.looklook.receiver.CommonReceiver;
import com.cmmobi.looklook.receiver.DailyReceiver;
import com.cmmobi.looklook.receiver.NetworkConnectChangedReceiver;
import com.cmmobi.sns.api.WeiboFriendsScanTask;

/**
 *  每天启动一次，看是否有任务需要执行，若有则启动asyncTask执行。
 *  执行过程中若没有wifi则等待，如果wifi连接则继续任务。
 *  若所有任务执行完，则退出service
 *  
 *  @author zhangwei
 * */
public class CommonService extends ZService {

	private final String TAG = "CommonService";
	private AlarmManager alarms;
	private PendingIntent alarmIntent;
	private BroadcastReceiver mWifiStatusReceiver;
	private NetworkConnectChangedReceiver myBroadcastReceiver;
	
	private static final int HANDLER_SEND_BROADCAST = 0x12345677;
	private final int HANDLER_FLAG_TASK_COMPLETE =  0x12345678;
	private final int HANDLER_FLAG_WIFI_CONNECTED = 0x12345679;
	
	private final long alarm_interval = 10*60*1000;//*60*1000;  //10 min
	
	//scan weibo friends interval
	private long interval = 1*24*60*60*1000; //a week in ms
	
	private WeiboFriendsScanTask sinaTask;  
	private WeiboFriendsScanTask tencentTask;  
	private WeiboFriendsScanTask renrenTask;  

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public void onCreate() {
		super.onCreate();
		CacheManager.getInstance(getApplicationContext());
		MyWeatherInfo.getInstance(getApplicationContext());
		Log.e(TAG, "CommonService onCreate");
		
	    alarms = (AlarmManager)getSystemService(Context.ALARM_SERVICE);

	    String ALARM_ACTION = DailyReceiver.ACTION_REFRESH_DAILYSCAN_ALARM; 
	    Intent intentToFire = new Intent(ALARM_ACTION);
	    alarmIntent = PendingIntent.getBroadcast(this, 0, intentToFire, 0);
	    
	    
	    myBroadcastReceiver = new NetworkConnectChangedReceiver();
	    IntentFilter filter = new IntentFilter();
	    filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
	    filter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
	    filter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
	    this.registerReceiver(myBroadcastReceiver, filter);
	    
	    LocalBroadcastManager.getInstance(this).registerReceiver(mWifiStatusReceiver,
	    	      new IntentFilter(NetworkConnectChangedReceiver.ACTION_WIFI_CONNECTED));
	    
		mWifiStatusReceiver = new BroadcastReceiver() {
			  @Override
			  public void onReceive(Context context, Intent intent) {
			    // Get extra data included in the Intent
			    //String message = intent.getStringExtra("message");
			    Log.d("mWifiStatusReceiver", "Got message HANDLER_FLAG_WIFI_CONNECTED" );
			    
			    //Message msg = handler.obtainMessage(HANDLER_FLAG_WIFI_CONNECTED);
			    handler.sendEmptyMessageDelayed(HANDLER_FLAG_WIFI_CONNECTED, 10000);
			  }
			};

	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) { 
		Log.e(TAG, "onStartCommand, flags:" + flags + " startId" + startId);
		
/*	    int alarmType = AlarmManager.ELAPSED_REALTIME_WAKEUP;
	    long timeToRefresh = SystemClock.elapsedRealtime() + alarm_interval;
	    alarms.setRepeating(alarmType, timeToRefresh, alarm_interval, alarmIntent); 
	    */
		
		handler.removeMessages(HANDLER_SEND_BROADCAST);
        handler.sendEmptyMessageDelayed(HANDLER_SEND_BROADCAST, alarm_interval);

		
    	String uid = ActiveAccount.getInstance(this).getUID();
    	
    	if(uid==null){
    		//user is logout, no-op
    		return Service.START_NOT_STICKY;
    		
    	}
    	
		AccountInfo uPI = AccountInfo.getInstance(uid);
		if(uPI!=null){
			//uPI.persist();
		}

		long now = TimeHelper.getInstance().now();

		
		if(uPI.sina_scan_time+interval<now){

		    if (sinaTask==null ||
		    		sinaTask.getStatus().equals(AsyncTask.Status.FINISHED)) {
				sinaTask = new WeiboFriendsScanTask(this, handler);
				sinaTask.execute(uid, "sina");

		    }	
			
		}
		
		
		if(uPI.tencent_scan_time+interval<now){
		    if (tencentTask==null ||
		    		tencentTask.getStatus().equals(AsyncTask.Status.FINISHED)) {
				tencentTask = new WeiboFriendsScanTask(this, handler);
				tencentTask.execute(uid, "tencent");

		    }			
		}
		
		if(uPI.renren_scan_time+interval<now){
		    if (renrenTask==null ||
		    		renrenTask.getStatus().equals(AsyncTask.Status.FINISHED)) {
		    	renrenTask = new WeiboFriendsScanTask(this, handler);
		    	renrenTask.execute(uid, "renren");

		    }			
		}
	    

		return Service.START_STICKY;
	}		
	
	@Override
	public void onDestroy() {  
	    super.onDestroy();  
		Log.e(TAG, "onDestroy");
		
		this.unregisterReceiver(myBroadcastReceiver);
		
		LocalBroadcastManager.getInstance(this).unregisterReceiver(mWifiStatusReceiver);

	}  
	
	@Override
	public boolean handleMessage(Message msg) {
		// TODO Auto-generated method stub
		switch(msg.what){
/*		case HANDLER_FLAG_TASK_COMPLETE:
			Log.e(TAG, "handle task complete, stopSelf");
			this.stopSelf();
			break;*/
		case WeiboFriendsScanTask.HANDLER_FLAG_WEIBOSCAN_DONE:
			AccountInfo uPI = AccountInfo.getInstance(ActiveAccount.getInstance(this).getLookLookID());
			
			if(uPI.sina_friends!=null && uPI.sina_friends.size()>0){
				postFollowItem[] friends = new postFollowItem[uPI.sina_friends.size()];
				for(int i=0; i<uPI.sina_friends.size(); i++){
					sinaUser user = uPI.sina_friends.get(i);
					////性别，0男 1女 2未知
					String sex = "2";
					if("m".equals(user.gender)){
						sex = "0";
					}else if("f".equals(user.gender)){
						sex = "1";
					}else{
						sex = "2";
					}
					friends[i] = new postFollowItem(String.valueOf(user.id), user.profile_image_url, sex, null, user.screen_name);
				}
				Requester2.postSNSFriend(handler, "1", uPI.sina_scan_bool>1?"2":"1", friends); 
			}else if(uPI.renren_friends!=null && uPI.renren_friends.size()>0){
				postFollowItem[] friends = new postFollowItem[uPI.renren_friends.size()];
				for(int i=0; i<uPI.renren_friends.size(); i++){
					RWUser user = uPI.renren_friends.get(i);
					String head_url = null;
					if(user.avatar!=null && user.avatar.length>0 && user.avatar[0].url!=null){
						head_url = user.avatar[0].url;
					}
					friends[i] = new postFollowItem(String.valueOf(user.id), head_url, "2", null, user.name);
				}
				Requester2.postSNSFriend(handler, "2", uPI.renren_scan_bool>1?"2":"1", friends); 
			}else if(uPI.tencent_friends!=null && uPI.tencent_friends.size()>0){
				postFollowItem[] friends = new postFollowItem[uPI.tencent_friends.size()];
				for(int i=0; i<uPI.tencent_friends.size(); i++){
					tencentInfo user = uPI.tencent_friends.get(i);
					////性别，0男 1女 2未知
					friends[i] = new postFollowItem(user.openid, user.headurl + "/50", "2", null, user.nick);
				}
				Requester2.postSNSFriend(handler, "6", uPI.tencent_scan_bool>1?"2":"1", friends); 
			}
			
			break;
		case HANDLER_SEND_BROADCAST:
			Intent intent = new Intent(CommonReceiver.ACTION_HEARTBEAT_COMMON_ALARM);
			sendBroadcast(intent);
			break;
			
		case HANDLER_FLAG_WIFI_CONNECTED:
			//只有在service活着的时候才能接收局部广播并重启service
			//应用在一次异步任务已退出，但没有完成（没有发出HANDLER_FLAG_TASK_COMPLETE）
			//这时的service还没有结束，等待网络状态的改变
			Log.e(TAG, "handle wifi connected, re scan");
			Intent startIntent = new Intent(this, CommonService.class);
		    this.startService(startIntent);
		    break;
		case Requester2.RESPONSE_TYPE_POST_SNSFRIEND:
			Log.e(TAG, "RESPONSE_TYPE_POST_SNSFRIEND");
			break;
		}
		return false;
	}
	
	
	


}
