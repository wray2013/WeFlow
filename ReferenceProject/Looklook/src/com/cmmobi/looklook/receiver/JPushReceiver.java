package com.cmmobi.looklook.receiver;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import cn.jpush.android.api.JPushInterface;
import cn.zipper.framwork.device.ZSimCardInfo;

import com.cmmobi.looklook.R;
import com.cmmobi.looklook.activity.ActivitiesActivity;
import com.cmmobi.looklook.activity.ActivitiesDetailActivity;
import com.cmmobi.looklook.activity.DiaryDetailActivity;
import com.cmmobi.looklook.activity.FriendsRecommendActivity;
import com.cmmobi.looklook.activity.FriendsSessionPrivateMessageActivity;
import com.cmmobi.looklook.activity.HomeActivity;
import com.cmmobi.looklook.info.profile.CommonInfo;
import com.cmmobi.looklook.info.profile.JPushExtra;
import com.cmmobi.looklook.prompt.Prompt;
import com.google.gson.Gson;

public class JPushReceiver extends BroadcastReceiver {
	private static final String TAG = "JPushReceiver";
	public static final String CUSTOM_MSG_RECEIVERED = "CUSTOM_MSG_RECEIVERED";

	@Override
	public void onReceive(Context context, Intent intent) {
        Bundle bundle = intent.getExtras();
        Gson gson = new Gson();
        JPushExtra jpushExtra = null;
		Log.d(TAG, "onReceive - " + intent.getAction() + ", extras: " + printBundle(bundle));
		String str_extra = intent.getStringExtra("cn.jpush.android.EXTRA");
		
		
		if(str_extra!=null){
			jpushExtra = gson.fromJson(str_extra, JPushExtra.class);
		}
		
        if (JPushInterface.ACTION_REGISTRATION_ID.equals(intent.getAction())) {
            String regId = bundle.getString(JPushInterface.EXTRA_REGISTRATION_ID);
            Log.d(TAG, "JPushInterface.EXTRA_REGISTRATION_ID : " + regId);
            //send the Registration Id to your server...
            CommonInfo.getInstance().jpush_reg_id = ZSimCardInfo.getIMEI();//regId;
            CommonInfo.getInstance().persist();
        }else if (JPushInterface.ACTION_UNREGISTER.equals(intent.getAction())){
        	String regId = bundle.getString(JPushInterface.EXTRA_REGISTRATION_ID);
            Log.d(TAG, "JPushInterface.EXTRA_REGISTRATION_ID :" + regId);
            //MainApplication.getAppInstance().jpush_reg_id = null;
            CommonInfo.getInstance().jpush_reg_id = null;
          //send the UnRegistration Id to your server...
        } else if (JPushInterface.ACTION_MESSAGE_RECEIVED.equals(intent.getAction())) {
        	Log.d(TAG, "JPushInterface.ACTION_MESSAGE_RECEIVED: " + bundle.getString(JPushInterface.EXTRA_MESSAGE));

        	try{

            	String msg = bundle.getString("cn.jpush.android.MESSAGE");
            	String title = bundle.getString("cn.jpush.android.TITLE");
            	String extra  = bundle.getString("cn.jpush.android.EXTRA");
            	Log.e(TAG, "msg:" + msg + " title:" + title);
            	if(extra!=null && !Prompt.isAppOnFront()){
            		//String json_str = x_str.replace("##", "\"");
            		showNotify(context, title, msg, (JPushExtra)(gson.fromJson(extra, JPushExtra.class)));

            	}
        	}catch(Exception e){
        		e.printStackTrace();
        	}

        
        } else if (JPushInterface.ACTION_NOTIFICATION_RECEIVED.equals(intent.getAction())) {
            Log.d(TAG, "JPushInterface.ACTION_NOTIFICATION_RECEIVED");

            int notifactionId = bundle.getInt(JPushInterface.EXTRA_NOTIFICATION_ID);
            Log.d(TAG, "JPushInterface.EXTRA_NOTIFICATION_ID: " + notifactionId);
        	
        } else if (JPushInterface.ACTION_NOTIFICATION_OPENED.equals(intent.getAction())) {
            Log.d(TAG, "JPushInterface.ACTION_NOTIFICATION_OPENED");
            

        	Intent i = null;


        	
        	if(jpushExtra!=null){
            	/**
            	 * push字段：
            	 * a)	Type：类型，1私信，2活动，3推荐，4附近，6官方
            	 * b)	Public_id：ID，发私信用户ID，日记ID，活动ID
            	 * */
            	if("1".equals(jpushExtra.type) || "6".equals(jpushExtra.type)){
            		/**
            		 * a)	如果Type（类型）=1 / 6 
            		 * i.	如果Public_id为空，跳转到消息列表页
            		 * ii.	如果Public_id不为空，Public_id为发私信用户的ID，则先跳转到消息列表页，后自动跳转到私信对话窗口
            		 * */
            		if(jpushExtra.public_id!=null && !jpushExtra.public_id.equals("")){
            			i = new Intent(context, FriendsSessionPrivateMessageActivity.class);
            			i.putExtra("userid", jpushExtra.public_id);
            			if(jpushExtra.nick_name!=null){
                			i.putExtra("nick_name", jpushExtra.nick_name);
            			}

            		}else{
            			i = new Intent(context, HomeActivity.class);
            			i.putExtra("start", "Message");
            		}
            	}else if("2".equals(jpushExtra.type)){
            		/**
            		 * b)	如果Type（类型）=2
            		 * i.	如果Public_id为空，则跳转到活动列表页
            		 * ii.	如果Public_id不为空，Public_id为活动ID，则先跳转到活动列表页，后自动跳转到对应活动的详情页
            		 * 
            		 * */
            		if(jpushExtra.public_id!=null && !jpushExtra.public_id.equals("")){
            			i = new Intent(context, ActivitiesDetailActivity.class); //活动的详情页
            			i.putExtra("activityid", jpushExtra.public_id);
            		}else{
            			i = new Intent(context, ActivitiesActivity.class);  //活动列表页
            		}
            	}else if("3".equals(jpushExtra.type)){
            		/**
            		 * c)	如果Type（类型）=3，
            		 * i.	如果Public_id为空，跳转到消息列表页
            		 * ii.	如果Public_id不为空，Public_id为日记ID，则先跳转到推荐列表，后自动跳转到对应推荐的日记详情页
            		 * */
            		if(jpushExtra.public_id!=null && !jpushExtra.public_id.equals("")){
            			i = new Intent(context, DiaryDetailActivity.class);
            			i.putExtra("dialyid", jpushExtra.public_id);
            		}else{
            			i = new Intent(context, FriendsRecommendActivity.class);
            		}
            	}else if("4".equals(jpushExtra.type)){
            		/**
            		 * d)	如果Type（类型）=4，
            		 * i.	如果Public_id为空，则跳转到消息列表
            		 * ii.	如果Public_id不为空，Public_id为日记ID，则先跳转到消息列表，后自动跳转到对应推荐的日记详情页
            		 * */
            		if(jpushExtra.public_id!=null && !jpushExtra.public_id.equals("")){
            			i = new Intent(context, DiaryDetailActivity.class);
            			i.putExtra("dialyid", jpushExtra.public_id);
            		}else{
            			i = new Intent(context, HomeActivity.class);
            			i.putExtra("notify", "Message");
            		}
            	}else{
            		Log.d(TAG, "Unhandled type - " + jpushExtra.type);
            		i = new Intent(context, HomeActivity.class);
            	}
        	}else{
        		i = new Intent(context, HomeActivity.class);
        	}
        	
        	
        	i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        	
        	context.startActivity(i);
        	
        	
        	
        	
        } else {
        	Log.d(TAG, "Unhandled intent - " + intent.getAction());
        }
	}

	private void showNotify(Context context, String title, String content, JPushExtra mm){

	    Intent intent2 = new Intent(CUSTOM_MSG_RECEIVERED);
	    // You can also include some extra data.
	    //intent.putExtra("pincode", value);
	    LocalBroadcastManager.getInstance(context).sendBroadcast(intent2);
	    
	    if(mm!=null && mm.public_id!=null && mm.nick_name!=null && !mm.public_id.equals("") && !mm.nick_name.equals("")){
			int mId = 0x12345678;
			NotificationCompat.Builder mBuilder =
			        new NotificationCompat.Builder(context)
			        .setSmallIcon(R.drawable.ic_launcher)
			        .setContentTitle(title)
			        .setContentText(content)
			        .setAutoCancel(true);
			
			Intent resultIntent = new Intent(context, FriendsSessionPrivateMessageActivity.class);
			resultIntent.putExtra("userid", mm.public_id);
			resultIntent.putExtra("nick_name", mm.nick_name);
			resultIntent.setFlags(Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
			
			TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
			stackBuilder.addParentStack(FriendsSessionPrivateMessageActivity.class);
			
			// Adds the Intent that starts the Activity to the top of the stack
			stackBuilder.addNextIntent(resultIntent);
			PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT  );
			mBuilder.setContentIntent(resultPendingIntent);
			NotificationManager mNotificationManager =
			    (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
			// mId allows you to update the notification later on.
			Notification notification = mBuilder.getNotification();
			notification.tickerText = content;
			notification.defaults =  Notification.DEFAULT_SOUND | Notification.FLAG_AUTO_CANCEL;
			mNotificationManager.notify(mId, notification);
	    }
	
	}

	private static String printBundle(Bundle bundle) {
		StringBuilder sb = new StringBuilder();
		for (String key : bundle.keySet()) {
			if (key.equals(JPushInterface.EXTRA_NOTIFICATION_ID)) {
				sb.append("\nkey:" + key + ", value:" + bundle.getInt(key));
			} else {
				sb.append("\nkey:" + key + ", value:" + bundle.getString(key));
			}
		}
		return sb.toString();
	}
	

}
