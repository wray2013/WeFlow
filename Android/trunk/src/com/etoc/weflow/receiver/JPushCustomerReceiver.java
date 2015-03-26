package com.etoc.weflow.receiver;


import com.etoc.weflow.R;
import com.etoc.weflow.service.PushService;
import com.etoc.weflow.utils.NotificationUtil;

import cn.jpush.android.api.JPushInterface;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class JPushCustomerReceiver extends BroadcastReceiver {
	public static final String TAG = "JPushCustomerReceiver";
	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		String action = intent.getAction();
		Log.d(TAG, "received " + action);
		if(JPushInterface.ACTION_MESSAGE_RECEIVED.equals(action)) {
			String message = intent.getStringExtra(JPushInterface.EXTRA_MESSAGE);
			Log.d(TAG, "received ACTION_MESSAGE_RECEIVED msg = [" + message + "]");
			/*NotificationUtil.PopNotification(context,
					R.drawable.ic_launcher, "有新消息", "", message);*/
			Intent regIntent = new Intent(context, PushService.class);
			regIntent.putExtra("ExtraMsg", message);
			context.startService(regIntent);
		} else if(JPushInterface.ACTION_NOTIFICATION_RECEIVED.equals(action)) {
			String message = intent.getStringExtra(JPushInterface.EXTRA_EXTRA);
			Log.d(TAG, "received ACTION_NOTIFICATION_RECEIVED msg = [" + message + "]");
			Intent regIntent = new Intent(context, PushService.class);
			regIntent.putExtra("ExtraMsg", message);
			context.startService(regIntent);
		}
	}

}
