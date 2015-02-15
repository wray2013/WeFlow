package com.cmmobi.looklook.receiver;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.ArrayList;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.util.Log;
import cn.zipper.framwork.core.ZLog;

import com.cmmobi.looklook.MainApplication;
import com.cmmobi.looklook.R;
import com.cmmobi.looklook.activity.FriendsSessionPrivateMessageActivity;
import com.cmmobi.looklook.activity.LookLookActivity;
import com.cmmobi.looklook.activity.VideoShootActivity2;
import com.cmmobi.looklook.activity.VshareDetailActivity;
import com.cmmobi.looklook.common.gson.GsonResponse3.HeartPush;
import com.cmmobi.looklook.common.gson.GsonResponse3.MicListItem;
import com.cmmobi.looklook.common.view.Xdialog;
import com.cmmobi.looklook.fragment.XFragment;
import com.cmmobi.looklook.fragment.ZoneBaseFragment;
import com.cmmobi.looklook.info.profile.AccountInfo;
import com.cmmobi.looklook.info.profile.ActiveAccount;
import com.cmmobi.looklook.info.profile.VshareDataEntities;
import com.cmmobi.looklook.prompt.Prompt;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class CmmobiPushReceiver extends BroadcastReceiver{

	public final static String ACTION_CMMOBI_PUSH_RECEIVER  = "action.cmmobi.push.receiver";
	public final static String PUSH_JSON = "pushjson";
	private Xdialog xdialog = null;
	private boolean isShowing = false;
	// notify的index
	public final static int NOTIFY_INDEX_PRIVATEMSG    = 0x87654321;
	public final static int NOTIFY_INDEX_VSHARE_DETAIL = 0x87654322;
	public final static int NOTIFY_INDEX_VSHARE_LIST   = 0x87654324;
	public final static int NOTIFY_INDEX_CONTACTS      = 0x87654323;
	
	@Override
	public void onReceive(Context context, Intent intent) {
		
		
		if (ACTION_CMMOBI_PUSH_RECEIVER.equals(intent.getAction())) {
			String json = intent.getStringExtra(PUSH_JSON);
			ZLog.e("push = " + json);
			try{
				
				ArrayList<HeartPush> hpushs = new Gson().fromJson(json, new TypeToken<ArrayList<HeartPush>>(){}.getType());

				/*
				 * "type":"1" //类型，1私信，2活动，3推荐，4附近，6官方，7微享，8加好友申请
				 * "public_id":"1234" // 当type=1时为发私信用户id；type=7时为微享id；当type=8为请求人id
				 */
				
				boolean isVideoShoot = false;
				Activity activity = MainApplication.getAppInstance().getTopActivity();
				if(activity instanceof VideoShootActivity2) {
					isVideoShoot = true;
				}
				
				for(HeartPush push : hpushs) {
					if("10".equals(push.type)) {
						Intent msgIntent = new Intent(LookLookActivity.MIC_LIST_CHANGE);
						LocalBroadcastManager.getInstance(MainApplication.getAppInstance()).sendBroadcast(
								msgIntent);
					}
	            	if(Prompt.isAppOnFront()) {
	            		if(/*"7".equals(push.type) || */"10".equals(push.type)) { // 时光胶囊
	            			// 非拍摄页
	            			if(!isVideoShoot) {
	            				// 之前没有推送弹框
	            				if(xdialog == null || !isShowing) {
	            					PopHBDialog(activity, push);
	            				}
	            				return;
	            			}
	            		} else {
	            			return;
	            		}
	            	}
					
					Intent i = null;
					
					int index = NOTIFY_INDEX_PRIVATEMSG;
					
					/**
					 * Type=1 （私信）/ 6(官方消息) 
					 * */
					if("1".equals(push.type) || "6".equals(push.type)){
						
						index = NOTIFY_INDEX_PRIVATEMSG;
						
						if(TextUtils.isEmpty(push.public_id)){
							// 跳转私信列表
	            			i = new Intent(context, LookLookActivity.class);
	            			i.setAction(LookLookActivity.ACTION_ENTER_PRIVATEMSG);
						}else{
							// 私信对话
							i = new Intent(context, FriendsSessionPrivateMessageActivity.class);
							i.putExtra("userid", push.public_id);
							if(push.nick_name!=null){
								i.putExtra("nick_name", push.nick_name);
							}
						}
						
	            	}else if("2".equals(push.type)){ // 活动
	            	}else if("3".equals(push.type)){ // 推荐
	            	}else if("4".equals(push.type)){ // 附近
	            	}else if("7".equals(push.type)){ // 微享列表 
	            		
	            		index = NOTIFY_INDEX_VSHARE_LIST;
	            		
            			// 跳转微享列表
            			i = new Intent(context, LookLookActivity.class);
            			i.setAction(LookLookActivity.ACTION_ENTER_VSHARE);
	            		
	            	}else if("8".equals(push.type)){ // 加好友申请
	            		
	            		index = NOTIFY_INDEX_CONTACTS;
	            		// 进入通讯录
            			i = new Intent(context, LookLookActivity.class);
            			i.setAction(LookLookActivity.ACTION_ENTER_CONTACTS);
            			
	            	}else if("9".equals(push.type)){ // 微享新评论
	            		
	            		index = NOTIFY_INDEX_VSHARE_DETAIL;
	            		
	            		if(TextUtils.isEmpty(push.public_id)){
							// 跳转微享列表
	            			i = new Intent(context, LookLookActivity.class);
	            			i.setAction(LookLookActivity.ACTION_ENTER_VSHARE);
	            			
						}else{
							// 微享详情页
							// 通知   0 表示打扰，1表示免打扰
							if(!"1".equals(push.is_undisturb)){
								i = new Intent(context, VshareDetailActivity.class);
								i.putExtra(VshareDetailActivity.FLAG_BOOL_BACK_VSHARE_LIST, true);
								i.putExtra("publishid", push.public_id);
								i.putExtra("is_encrypt", push.is_encrypt);
								i.putExtra("frompush", "frompush");
								i.putExtra("userobj", push.userobj);
	//							i.putExtra("mic_users", push.mic_users);
	//							i.putExtra("micuserid", push.micuserid);
								//i.putExtra("join_safebox",push.join_safebox);
							}
						}
	            	}else if("10".equals(push.type)) { // 时光胶囊
						boolean isBurn = isBurnAfterRead(context, push);
						// 同时为阅后即焚
						if (isBurn) {
							index = NOTIFY_INDEX_VSHARE_LIST;
							// 跳转微享列表
							i = new Intent(context, LookLookActivity.class);
							i.setAction(LookLookActivity.ACTION_ENTER_VSHARE_FROM_NOTIFICATION);
							i.putExtra("publishid", push.public_id);
							i.putExtra("is_encrypt", push.is_encrypt);
//							i.putExtra("userobj", push.userobj);
							i.putExtra("is_burn", "1");
						} else {
							i = new Intent(context, VshareDetailActivity.class);
							i.putExtra(VshareDetailActivity.FLAG_BOOL_BACK_VSHARE_LIST, true);
							i.putExtra("publishid", push.public_id);
							i.putExtra("is_encrypt", push.is_encrypt);
							i.putExtra("frompush", "frompush");
							i.putExtra("userobj", push.userobj);
							i.putExtra("is_burn", "0");
						}
	            	}else{
	            	}
					
					if(i != null){
						showNotify(index,i,context, push.title, push.content, push);
					}
				}
        	}catch(Exception e){
        		e.printStackTrace();
        	}
		}
		
	}
	
	
	
	private void showNotify(int index,Intent resultIntent,Context context, String title, String content, HeartPush push){

	    
	    if(push!=null && push.public_id!=null && push.nick_name!=null && !push.public_id.equals("") && !push.nick_name.equals("")){
			NotificationCompat.Builder mBuilder =
			        new NotificationCompat.Builder(context)
			        .setSmallIcon(R.drawable.ic_launcher)
			        .setContentTitle(title)
			        .setContentText(content)
			        .setAutoCancel(true);
			
			resultIntent.setFlags(Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
			TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
			stackBuilder.addParentStack(resultIntent.getComponent());
			// Adds the Intent that starts the Activity to the top of the stack
			stackBuilder.addNextIntent(resultIntent);
			PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT  );
			mBuilder.setContentIntent(resultPendingIntent);
			NotificationManager mNotificationManager =
			    (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
			// mId allows you to update the notification later on.
			
			Notification notification = mBuilder.build();
			notification.tickerText = content;
			notification.defaults =  Notification.DEFAULT_SOUND | Notification.FLAG_AUTO_CANCEL;
			mNotificationManager.notify(index, notification);
	    }
	
	}

	
	
	public static void cancelNotification(Context ctx,int index) {
		NotificationManager mNotificationManager =
			    (NotificationManager) ctx.getSystemService(Context.NOTIFICATION_SERVICE);
		mNotificationManager.cancel(index);
	}
	
	private void PopVShareDialog(final Context context, final HeartPush push) {
		new Xdialog.Builder(context)
		.setMessage("该内容已被设置阅后即焚\n退出后将无法再次查看")
		.setPositiveButton("立即查看", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog,
					int which) {
				// TODO Auto-generated method stub
				Intent i = new Intent(context, VshareDetailActivity.class);
				i.putExtra(VshareDetailActivity.FLAG_BOOL_BACK_VSHARE_LIST, true);
				i.putExtra("publishid", push.public_id);
				i.putExtra("is_encrypt", push.is_encrypt);
				i.putExtra("frompush", "frompush");
				i.putExtra("userobj", push.userobj);
				i.putExtra("is_burn", "1");
				context.startActivity(i);
			}
			
		}).setNegativeButton("稍后", null)
		.create().show();
	}
	
	private void PopHBDialog(final Context context, final HeartPush push) {
		isShowing = true;
		xdialog = new Xdialog.Builder(context)
		.setMessage("你有一个时光胶囊可以开启啦")
		.setPositiveButton("立即查看", new DialogInterface.OnClickListener() {
			
			public void onClick(DialogInterface dialog,
					int which) {
				boolean isBurn = isBurnAfterRead(context, push);
				// 同时为阅后即焚
				if(isBurn) {
					if(!isVShareListFragment(context)) {
						// 跳转微享列表
		    			Intent i = new Intent(context, LookLookActivity.class);
		    			i.setAction(LookLookActivity.ACTION_ENTER_VSHARE);
		    			context.startActivity(i);
					}
	    			PopVShareDialog(context, push);
				} else {
					Intent i = new Intent(context, VshareDetailActivity.class);
					i.putExtra(VshareDetailActivity.FLAG_BOOL_BACK_VSHARE_LIST, true);
					i.putExtra("publishid", push.public_id);
					i.putExtra("is_encrypt", push.is_encrypt);
					i.putExtra("frompush", "frompush");
					i.putExtra("userobj", push.userobj);
					i.putExtra("is_burn", "0");
					context.startActivity(i);
				}
				isShowing = false;
			}
		}).setNegativeButton("稍后", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog,
					int which) {
				// TODO Auto-generated method stub
				isShowing = false;
			}
		})
		.create();
		xdialog.show();
	}
	
	private boolean isVShareListFragment(Context context) {
		if(context == null) return false;
		if(context instanceof LookLookActivity) {
			XFragment currContentFragment = ((LookLookActivity) context).getCurrentFragment();
			
			if (currContentFragment != null
					&& currContentFragment instanceof ZoneBaseFragment) {
				int select = ((ZoneBaseFragment)currContentFragment).getCurrentSelect();
				// 当前已经是列表页
				if(select == 1) {
					return true;
				}
			}
		}
		return false;
	}
	
	private boolean isBurnAfterRead(Context context, HeartPush push) {
		boolean isburn = false;
		String uid = ActiveAccount.getInstance(context).getUID();
		AccountInfo ai = AccountInfo.getInstance(uid);
		VshareDataEntities dataEntities = ai.vshareDataEntities;
		if (dataEntities != null) {
			MicListItem micitem = dataEntities.findMember(push.public_id);
			if (micitem != null && "1".equals(micitem.burn_after_reading)) {
				isburn = true;
			}
		}
		return isburn;
	}
	
	public static boolean isNotificationEnable() {
		boolean isEnable = false;
		try {
			Class serviceManager = Class.forName("com.android.server.NotificationManagerService");
			Method method = serviceManager.getMethod("areNotificationsEnabledForPackage", String.class);
			Constructor[]cc = serviceManager.getDeclaredConstructors();
			for(int i =0;i<cc.length;i++) {
				cc[i].setAccessible(true);
			}
			Log.e("xxx", "length = " + cc.length);
			isEnable = (Boolean) method.invoke(cc[0].newInstance(), "com.cmmobi.looklook");
		} catch(Exception e) {
			e.printStackTrace();
			Log.e("xxx", "取错鸟！");
		}
		return isEnable;
		
	}
	
}
