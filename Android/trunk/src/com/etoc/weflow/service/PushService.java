package com.etoc.weflow.service;

import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import com.etoc.weflow.R;
import com.etoc.weflow.WeFlowApplication;
import com.etoc.weflow.dao.AccountInfo;
import com.etoc.weflow.dao.MyMessage;
import com.etoc.weflow.net.GsonResponseObject.MessageList;
import com.etoc.weflow.net.GsonResponseObject.PushMsgResp;
import com.etoc.weflow.receiver.PushHeatbeatReceiver;
import com.etoc.weflow.utils.NotificationUtil;
import com.google.gson.Gson;


import android.app.ActivityManager;
import android.app.Service;
import android.app.ActivityManager.RunningTaskInfo;
import android.content.Context;
import android.content.Intent;
import android.os.Handler.Callback;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;

public class PushService extends Service implements Callback {
	public static final String TAG = "PushService";
	private final int HANDLER_FLAG_HEARTBEAT =  0x19861214;
	
	public final static int HANDLER_FLAG_LOCATING_RESPONSE =  0x19861215;
	public final static int HANDLER_FLAG_LOCATING_AGREE =  0x19861216;
	public final static int HANDLER_FLAG_LOCATING_REQ_AGREE =  0x19861217;
	public final static int HANDLER_FLAG_LOCATING_RESPONSE_REFUSE =  0x19861218;
	
	public final static int HANDLER_FLAG_SENDSMS_RESPONSE =  0x19861219;
	
	public static final int APK_DOWNLOADED_MSG = 0xff861214;
	public static String UPDATE_SERVERAPK = "weflow.apk";
	
	public static String SP_NAME = "installinfo";
	public static String SP_KEY_SERVICE_TEL = "servicetel";
	public static String SP_KEY_FIRSTOPEN   = "firstopen";
	
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onCreate() {
		Log.d(TAG, "onCreate !");
		super.onCreate();
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		if(intent != null) {
			String msg = intent.getStringExtra("ExtraMsg");
			//有消息收到
			if(msg != null && !"".equals(msg)) {
				Log.d(TAG, "msg received ==>" + msg + "<==");
				PushMsgResp resp = null;
				try {
					/*JSONTokener jsonParser = new JSONTokener(msg);
					JSONObject result = (JSONObject) jsonParser.nextValue();
					String msgtype = result.getString("msgtype");
					// 收到消息推送
					if ("1".equals(msgtype)) {
						String pushcontent = emptyToNull(result.getString("msgcontent"));
						String pushhint = emptyToNull(result.getString("msghint"));
						String pushtitle = emptyToNull(result.getString("msgtitle"));
						if (!isAppInFront()) {
							NotificationUtil.PopNotification(this,
									R.drawable.ic_launcher,
									pushtitle == null ? "有新消息" : pushtitle, pushcontent,
									pushhint == null ? "点击查看详情" : pushhint);
						}
					}*/
					Gson gson = new Gson();
					resp = gson.fromJson(msg, PushMsgResp.class);
					
				} catch (Exception ex) {
					ex.printStackTrace();
				}
				
				AccountInfo info = WeFlowApplication.getAppInstance().getAccountInfo();
				boolean isLogin = (info != null && info.getUserid() != null && !info.getUserid().equals(""));
				
				if (resp != null) {
					// 收到通知
					if("1".equals(resp.msgtype)) {
						String pushcontent = emptyToNull(resp.msgcontent);
						String pushhint = emptyToNull(resp.msghint);
						String pushtitle = emptyToNull(resp.msgtitle);
						if (!isAppInFront()) {
							NotificationUtil.PopNotification(this,
									R.drawable.ic_launcher,
									pushtitle == null ? "有新消息" : pushtitle,
									pushcontent,
									pushhint == null ? "点击查看详情" : pushhint);
						}
					// 收到消息
					} else if("2".equals(resp.msgtype) && isLogin) {
						if(resp.msglist != null && resp.msglist instanceof MessageList) {
							MessageList list = resp.msglist;
							List<MyMessage> l = WeFlowApplication.getAppInstance().getMyMessage();
							MyMessage msgitem = new MyMessage();
							msgitem.setMsgid(list.msgid);
							msgitem.setUserid(info.getUserid());
							msgitem.setType(list.type);
							msgitem.setPicurl(list.picurl);
							msgitem.setTitle(list.title);
							msgitem.setContent(list.content);
							msgitem.setFlowcoins(list.flowcoins);
							msgitem.setTime(list.time);
							msgitem.setPageurl(list.pageurl);
							msgitem.setProductid(list.productid);
							msgitem.setExtradata(list.extradata);
							l.add(msgitem);
							WeFlowApplication.getAppInstance().PersistMyMessage(l);
						}
					}
				} else {
					try {
						JSONTokener jsonParser = new JSONTokener(msg);
						JSONObject result = (JSONObject) jsonParser.nextValue();
						String msgtype = result.getString("msgtype");
						// 收到消息推送
						if ("1".equals(msgtype)) {
							String pushcontent = emptyToNull(result.getString("msgcontent"));
							String pushhint = emptyToNull(result.getString("msghint"));
							String pushtitle = emptyToNull(result.getString("msgtitle"));
							if (!isAppInFront()) {
								NotificationUtil.PopNotification(this,
										R.drawable.ic_launcher,
										pushtitle == null ? "有新消息" : pushtitle,
										pushcontent,
										pushhint == null ? "点击查看详情" : pushhint);
							}
						}
					} catch (JSONException e) {
						e.printStackTrace();
					}
				}
			}
		}
		
//		handler.sendEmptyMessageDelayed(HANDLER_FLAG_HEARTBEAT, heartbeat_interval);
		return Service.START_STICKY;
	}
	
	private String emptyToNull(String s) {
		String ret = null;
		if(s != null && !s.equals("")) {
			ret = s;
		}
		return ret;
	}
	
	@Override
	public boolean handleMessage(Message msg) {
		switch (msg.what) {
		case HANDLER_FLAG_HEARTBEAT:
			Intent intent = new Intent(PushHeatbeatReceiver.ACTION_HEARTBEAT_PUSH_MSG);
			sendBroadcast(intent);
			break;
		}
		return false;
	}
	
	private boolean isAppInFront() {
	    String packageName = this.getPackageName();
	    ActivityManager activityManager = (ActivityManager)this.getSystemService(Context.ACTIVITY_SERVICE);
	    List<RunningTaskInfo> appTask = activityManager.getRunningTasks(1);
	    
	    boolean isAppInFront = false;
	    if(appTask!=null && appTask.size()>0){
            if(appTask.get(0).topActivity.toString().contains(packageName)){
            	isAppInFront = true;
            }
	    }
	    return isAppInFront;
	}
    
}
