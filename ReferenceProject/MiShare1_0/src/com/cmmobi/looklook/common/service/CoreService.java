package com.cmmobi.looklook.common.service;

import java.text.SimpleDateFormat;
import java.util.Date;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.util.Log;
import cn.zipper.framwork.io.network.ZNetworkStateDetector;
import cn.zipper.framwork.service.ZService;

import com.cmmobi.looklook.common.gson.GsonResponse3;
import com.cmmobi.looklook.common.gson.GsonResponse3.ConfigHeart;
import com.cmmobi.looklook.common.gson.GsonResponse3.listMessageResponse;
import com.cmmobi.looklook.common.gson.Requester3;
import com.cmmobi.looklook.common.gson.UDPRequester;
import com.cmmobi.looklook.common.push.TcpRequester;
import com.cmmobi.looklook.common.service.aidl.Main2ServiceObj;
import com.cmmobi.looklook.common.service.aidl.MainCallBack;
import com.cmmobi.looklook.common.service.aidl.MainUpdate;
import com.cmmobi.looklook.common.service.aidl.Service2Main2Obj;
import com.cmmobi.looklook.common.service.aidl.Service2MainObj;
import com.cmmobi.looklook.common.utils.MetaUtil;
import com.cmmobi.looklook.info.profile.CommonInfo;
import com.cmmobi.looklook.info.profile.HeartBeatInfo;
import com.cmmobi.looklook.info.profile.TimeHelper;
import com.cmmobi.looklook.receiver.CmmobiPushReceiver;
import com.cmmobi.looklook.receiver.CoreReceiver;
import com.google.gson.Gson;

public class CoreService extends ZService {
	private final String TAG = "CoreService"; 
	
	public final static String FLAG_APP_FRONT_NOTIFY  = "FLAG_APP_FRONT_NOTIFY";
	public final static String ACTION_MESSAGE_DATA_UPDATE  = "ACTION_MESSAGE_DATA_UPDATE";

	public final static int HANDLER_FLAG_MESSAGE_DATA_UPDATE = 0x9739171;

	private static final int HANDLER_SEND_BROADCAST = 0x78361813;
	
	public static final String SYNC_TIMELINE = "SYNC_TIMELINE";
	public static final String LOGIN_USERID = "LOGIN_USERID";

	protected static final int HADNLER_UPDATE_SERVER_CONFIG = 0x83719371;

	public static final boolean use_udp = true;

	public static final String BRODCAST_SYNC_TIME = "BRODCAST_SYNC_TIME";
	public static final String BRODCAST_SYNC_TIME_DONE = "BRODCAST_SYNC_TIME_DONE";
	
	public boolean isAppInFront;

	private MainCallBack callback;
	
	public HeartBeatInfo hb;    //消息信息
	public static ConfigHeart[] heart; //心跳配置

	public boolean isCofigDone;
	
	private final MainUpdate.Stub mBinder = new MainUpdate.Stub() {

		@Override
		public void invokCallBack(Main2ServiceObj obj) throws RemoteException {
			Log.v(TAG, "MainUpdate.invokCallBack");
			
			if(obj!=null && obj.userid!=null){
				//更新heartInfo信息
				hb = HeartBeatInfo.getInstance(CoreService.this, obj.userid);
				boolean isNewUser = hb.setMain2ServerObj(obj);
				int now_interval = getInterval();
				if(now_interval>0){
					int cmmobi_channel = MetaUtil.getIntValue("CMMOBI_APPKEY");
					
					if(use_udp){
						UDPRequester.sendUDP(CoreService.this, handler, 0, cmmobi_channel+"", hb.userid, now_interval);
					}else{
						if(isNewUser || true){
							handler.removeMessages(TcpRequester.HANDLER_FLAG_TCP_NEW_LOGIN);
							handler.sendEmptyMessage(TcpRequester.HANDLER_FLAG_TCP_NEW_LOGIN);	
						}

					}


				}

			}else{
				hb=null;
				isCofigDone = false;
		    	Log.e(TAG, "invokCallBack - obj or userid is null");
		    } 
			
		}


		@Override
		public void registerUpdateCall(MainCallBack cb) throws RemoteException {
			Log.v(TAG, "MainUpdate.registerUpdateCall");
			callback = cb;
		}
	};


	@Override
	public IBinder onBind(Intent t) {
		Log.v(TAG, "service on bind");
		return mBinder;
	}
	
	@Override
	public boolean onUnbind(Intent intent) {
		Log.v(TAG, "service on unbind");
		return super.onUnbind(intent);
	}


	public void onRebind(Intent intent) {
		Log.v(TAG, "service on rebind");
		super.onRebind(intent);
	}

	@Override
	public void onCreate() {
		super.onCreate();
		CommonInfo ci = CommonInfo.getInstance();
		ci.reload();
		heart = ci.heart;
		isCofigDone = false;
		registerReceiver(mReseiver, new IntentFilter(BRODCAST_SYNC_TIME));
	}

	private BroadcastReceiver mReseiver = new BroadcastReceiver(){
		 @Override
		  public void onReceive(Context context, Intent intent) {    
			    // Get extra data included in the Intent
				if(BRODCAST_SYNC_TIME.equals(intent.getAction())){
					System.out.println("=== update time new Msg ");
					if(hb!=null && hb.userid!=null){
					    Requester3.listMessage(handler, hb.userid, hb.timemill, "20", "0", hb.commentid, 
					    		hb.commentid_safebox, 
					    		hb.t_zone_mic,
					    		hb.t_friend,
					    		hb.t_safebox_miccomment,
					    		hb.t_friendrequest,
					    		hb.t_friend_change,
					    		hb.t_push,
					    		hb.t_zone_miccomment
								);
				}
		  }
		 }
	};
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Log.e(TAG, "onStartCommand, flags:" + flags + " startId" + startId);
		
		if(hb!=null && hb.isLogin()){
			
	    	//只有登录用户才有必要发送push
			int now_interval = getInterval();
			if(now_interval>0){
				int cmmobi_channel = MetaUtil.getIntValue("CMMOBI_APPKEY");
				
				if(use_udp){
					UDPRequester.sendUDP(this, handler, 0, cmmobi_channel+"", hb.userid, now_interval);
				}else{
					if(!handler.hasMessages(TcpRequester.HANDLER_FLAG_TCP_HEART_BEAT)){
						handler.removeMessages(TcpRequester.HANDLER_FLAG_TCP_HEART_BEAT);
						handler.sendEmptyMessageDelayed(TcpRequester.HANDLER_FLAG_TCP_HEART_BEAT, now_interval*1000);
					}
	
				}


			}
			

			if(!isCofigDone){
				handler.removeMessages(HADNLER_UPDATE_SERVER_CONFIG);
				handler.sendEmptyMessage(HADNLER_UPDATE_SERVER_CONFIG);
			}


		}else{
			Log.e(TAG, "onStartCommand - app not login, userid is null");
			if(use_udp){
		    	handler.removeMessages(UDPRequester.HANDLER_FLAG_UDP_OP_DONE);
		    	handler.sendEmptyMessage(UDPRequester.HANDLER_FLAG_UDP_OP_DONE);
			}

		}

		return Service.START_STICKY;
	}

	@Override
	public void onDestroy() {
		if(mReseiver != null){
			unregisterReceiver(mReseiver);
		}
		super.onDestroy();
	}
	
	@Override
	public boolean handleMessage(Message msg) {
		// TODO Auto-generated method stub
		switch (msg.what) {
		case HADNLER_UPDATE_SERVER_CONFIG:
			if(hb!=null && hb.isLogin()){
				Requester3.requestConfigInfo(handler, hb.userid);
			}
			break;
		case Requester3.RESPONSE_TYPE_CONFIGINFO:
			GsonResponse3.configInfoResponse obj_ci = (GsonResponse3.configInfoResponse) msg.obj;
			if(obj_ci!=null && "0".equals(obj_ci.status)){	
				heart = obj_ci.heart;
				isCofigDone = true;
				
				if(hb==null || !hb.isLogin()){
					return false;
				}
				
	        	Service2Main2Obj s2m2Obj = new Service2Main2Obj();
				
	        	//Intent msgIntent = new Intent(ACTION_MESSAGE_DATA_UPDATE);
				s2m2Obj.userid = hb.userid;
				s2m2Obj.heart = obj_ci.heart;
				s2m2Obj.promptmsg = obj_ci.promptmsg;
				
				try {
					if(callback!=null){
						callback.Update2CallBack(s2m2Obj);
						isCofigDone = true;
					}

				} catch (RemoteException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}
			break;
		case TcpRequester.HANDLER_FLAG_TCP_NEW_LOGIN:
		    if(hb!=null && hb.isLogin()){
		    	hb.persist();
		    	
		    	//只有登录用户才有必要发送tcp
				int now_interval_tcp = getInterval();
				if(now_interval_tcp>0){
					int cmmobi_channel = MetaUtil.getIntValue("CMMOBI_APPKEY");
					TcpRequester.getInstance().sendTCP(this, handler, 0, cmmobi_channel + "", hb.userid, now_interval_tcp);
				}

		    }
			break;
		case TcpRequester.HANDLER_FLAG_TCP_HEART_BEAT:
		    if(hb!=null && hb.isLogin()){
		    	hb.persist();
		    	
		    	//只有登录用户才有必要发送tcp
				int now_interval_tcp = getInterval();
				if(now_interval_tcp>0){
					int cmmobi_channel = MetaUtil.getIntValue("CMMOBI_APPKEY");
					TcpRequester.getInstance().sendTCP(this, handler, 0, cmmobi_channel + "", null, now_interval_tcp*1000);
				}
		    	handler.removeMessages(TcpRequester.HANDLER_FLAG_TCP_HEART_BEAT);
		    	handler.sendEmptyMessageDelayed(TcpRequester.HANDLER_FLAG_TCP_HEART_BEAT, now_interval_tcp*1000);
		    }else{
		    	handler.removeMessages(TcpRequester.HANDLER_FLAG_TCP_HEART_BEAT);
		    	handler.sendEmptyMessageDelayed(TcpRequester.HANDLER_FLAG_TCP_HEART_BEAT, 10000);
		    }
			break;
		case UDPRequester.HANDLER_FLAG_UDP_OP_DONE:
		    if(hb!=null && hb.isLogin()){
		    	hb.persist();
		    	
		    	//只有登录用户才有必要发送udp
				int now_interval_udp = getInterval();
				if(now_interval_udp>0){
					int cmmobi_channel = MetaUtil.getIntValue("CMMOBI_APPKEY");
					UDPRequester.sendUDP(this, handler, 0, cmmobi_channel + "", hb.userid, now_interval_udp);
				}

		    }else{
		    	handler.removeMessages(UDPRequester.HANDLER_FLAG_UDP_OP_DONE);
		    	handler.sendEmptyMessageDelayed(UDPRequester.HANDLER_FLAG_UDP_OP_DONE, 10000);
		    }
			break;
		case UDPRequester.HANDLER_FLAG_UDP_TIME_SYNC:
		case TcpRequester.HANDLER_FLAG_TCP_TIME_SYNC:
/*			try{
				server_time = Long.parseLong((String)(msg.obj));
				//TimeHelper.getInstance().syncServerTime(server_time);
			}catch(Exception e){
				e.printStackTrace();
			}*/
			break;
		case TcpRequester.HANDLER_FLAG_TCP_NEW_MSG:
		case UDPRequester.HANDLER_FLAG_UDP_NEW_MSG:
	    	Log.e(TAG, "######## HANDLER_FLAG_NEW_MSG ##########");
	    	if(hb!=null && hb.userid!=null){
			    Requester3.listMessage(handler, hb.userid, hb.timemill, "20", "0", hb.commentid, 
			    		hb.commentid_safebox, 
			    		hb.t_zone_mic,
			    		hb.t_friend,
			    		hb.t_safebox_miccomment,
			    		hb.t_friendrequest,
			    		hb.t_friend_change,
			    		hb.t_push,
			    		hb.t_zone_miccomment
						);
		    }else{
		    	Log.e(TAG, "HANDLER_FLAG_NEW_MSG m2sObj or m2sObj.userid is null");
		    }
			break;
		case Requester3.RESPONSE_TYPE_LIST_MESSAGE:
			listMessageResponse obj_lm = (listMessageResponse) msg.obj;
			
			if(hb==null || !hb.isLogin()){
				break;
			}
			
			//handler.removeMessages(UDPRequester.HANDLER_FLAG_UDP_OP_DONE);
			//handler.sendEmptyMessageDelayed(UDPRequester.HANDLER_FLAG_UDP_OP_DONE, 10000);
			
			if(obj_lm == null || !"0".equals(obj_lm.status)){
				break;
			}
			
			//Toast.makeText(this, "respone from list msg", Toast.LENGTH_SHORT).show();
        	Service2MainObj s2mObj = new Service2MainObj();
			
        	//Intent msgIntent = new Intent(ACTION_MESSAGE_DATA_UPDATE);
			s2mObj.userid = hb.userid;
			
			if(obj_lm.users!=null && obj_lm.users.length>0){				
				s2mObj.mus = obj_lm.users;
			}
			
			s2mObj.t_push = obj_lm.t_push;
			s2mObj.commentnum = obj_lm.commentnum;
			s2mObj.commentnum_safebox = obj_lm.commentnum_safebox;
			s2mObj.new_zonemicnum = obj_lm.new_zonemicnum;
			s2mObj.new_safeboxmicnum = obj_lm.new_safeboxmicnum;
			s2mObj.new_friend_change = obj_lm.new_friend_change;
			s2mObj.new_friend = obj_lm.new_friend;
			s2mObj.new_requestnum = obj_lm.new_requestnum;
			s2mObj.friendnum = obj_lm.friendnum;
			s2mObj.last_timemilli = obj_lm.last_timemilli;
			s2mObj.readedMessages = obj_lm.readedMessages;
			
			if(obj_lm.server_time!=null){
				try{
					Log.e(TAG, "TimeHelper - CoreService:" + obj_lm.server_time );
					s2mObj.servertime = Long.parseLong(obj_lm.server_time);
					TimeHelper.getInstance().syncServerTime(s2mObj.servertime);
					
				}catch(Exception e){
					//e.printStackTrace();
					s2mObj.servertime = 0;
				}

			}
			
			try {
				if(callback!=null){
					callback.UpdateCallBack(s2mObj);
				}
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				//e.printStackTrace();
			} catch(Exception e) {
				e.printStackTrace();
			}
			
			Intent msgIntent = new Intent(this, CoreReceiver.class);
			
        	msgIntent.putExtra("type", HANDLER_FLAG_MESSAGE_DATA_UPDATE);
			
			msgIntent.putExtra("new_zonemicnum", obj_lm.new_zonemicnum);          //微享新动态数时间戳
			msgIntent.putExtra("new_friend_change", obj_lm.new_friend_change);        //好友新动态数
			msgIntent.putExtra("new_requestnum", obj_lm.new_requestnum);        //新增好友请求数
			msgIntent.putExtra("friendnum", obj_lm.friendnum);            //当前朋友数
        	msgIntent.putExtra("commentnum", obj_lm.commentnum); //评论数，当评论数返回N时，客户端显示点
        	msgIntent.putExtra("new_friend", obj_lm.new_friend);
        	

        	//LocalBroadcastManager.getInstance(this).sendBroadcast(msgIntent);
        	this.sendBroadcast(msgIntent);

        	if(obj_lm.push!=null && obj_lm.push.length>0 ){
        		Intent pushIntent = new Intent(CmmobiPushReceiver.ACTION_CMMOBI_PUSH_RECEIVER);
        		pushIntent.putExtra(CmmobiPushReceiver.PUSH_JSON, new Gson().toJson(obj_lm.push));
        		sendBroadcast(pushIntent);
        	}
        	
			break;
		}
		return false;
		
	}
	
	/**
	 * 0-7(0-420) 点   3g:1800  wifi:180 remind_type:2
	 * 7-22(420-1320)点  3g:600 wifi:60 remind_type:1
	 * 22-0(1320-1439) 3g:1800 wifi:180 remind_type:2
	 * */
	private int getInterval(){
		int current_interval_seconds = UDPRequester.UDP_MIN_INTERVAL_SECONDS;
    	Date now = new Date();
    	SimpleDateFormat sdf_hour = new SimpleDateFormat("H");
    	String hour_str = sdf_hour.format(now);
    	int hour_int = Integer.parseInt(hour_str);
    	
    	SimpleDateFormat sdf_minute = new SimpleDateFormat("m");
    	String minute_str = sdf_minute.format(now);
    	int minute_int = Integer.parseInt(minute_str);
    	
    	int minute_now = hour_int*60+minute_int;
    	ConfigHeart found = null;
    	if(heart!=null && heart.length>0){
    		for(ConfigHeart item : heart){
    			int starttime = 0;
    			int endtime = 0;
    			try{
        			starttime = Integer.parseInt(item.starttime);
        			endtime = Integer.parseInt(item.endtime);
    			}catch(Exception e){
    				
    			}
    			
    			if(minute_now>starttime && minute_now<endtime){
    				found = item;
    				break;
    			}

    		}
    	}
    	
    	if(found!=null){
    		if(ZNetworkStateDetector.isWifi()){
    			try{
    				current_interval_seconds = Integer.parseInt(found.interval_wifi);
    			}catch(Exception e){}
    			
    		}else if(ZNetworkStateDetector.isMobile()){
    			try{
    				current_interval_seconds = Integer.parseInt(found.interval);
    			}catch(Exception e){}
    		}else{
    			current_interval_seconds = UDPRequester.UDP_DEFAULT_INTERVAL_SECONDS;
    		}
    	}else{
    		if(ZNetworkStateDetector.isWifi()){
    			try{
    				current_interval_seconds = UDPRequester.UDP_DEFAULT_INTERVAL_SECONDS;
    			}catch(Exception e){}
    			
    		}else if(ZNetworkStateDetector.isMobile()){
    			try{
    				current_interval_seconds = UDPRequester.UDP_DEFAULT_INTERVAL_SECONDS_GPRS;
    			}catch(Exception e){}
    		}else{
    			current_interval_seconds = UDPRequester.UDP_MIN_INTERVAL_SECONDS;
    		}
    	}
    	
    	//delete if need
    	//current_interval_seconds = UDPRequester.UDP_DEFAULT_INTERVAL_SECONDS;
    	
    	return current_interval_seconds;
	}
	
	

}
