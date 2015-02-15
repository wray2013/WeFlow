package com.cmmobi.looklook.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.cmmobi.looklook.common.service.PrivateMessageService;

public class PrivateMessageReceiver extends BroadcastReceiver {
	private static final String TAG = "PrivateMessageReceiver";
	public static final String ACTION_HEARTBEAT_PRIVATEMESSAGE_ALARM = "ACTION_HEARTBEAT_PRIVATEMESSAGE_ALARM";
	
	@Override
	public void onReceive(Context context, Intent intent) {
		String action = intent.getAction();
		if (PrivateMessageReceiver.ACTION_HEARTBEAT_PRIVATEMESSAGE_ALARM.equals(action)) {
			Log.d(TAG, "ACTION_HEARTBEAT");
			Intent regIntent = new Intent(context, PrivateMessageService.class);
			context.startService(regIntent);
		} else  {
			Log.d(TAG, "else ACTION_HEARTBEAT_PRIVATEMESSAGE_ALARM");
		}
	}

}
