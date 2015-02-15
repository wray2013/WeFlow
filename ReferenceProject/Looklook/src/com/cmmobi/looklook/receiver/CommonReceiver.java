package com.cmmobi.looklook.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.cmmobi.looklook.common.service.CommonService;

public class CommonReceiver extends BroadcastReceiver {
	private static final String TAG = "CommonReceiver";
	public static final String ACTION_HEARTBEAT_COMMON_ALARM = "ACTION_HEARTBEAT_COMMON_ALARM";
	
	@Override
	public void onReceive(Context context, Intent intent) {
		String action = intent.getAction();
		if (CommonReceiver.ACTION_HEARTBEAT_COMMON_ALARM.equals(action)) {
			Log.d(TAG, "ACTION_HEARTBEAT_COMMON_ALARM");
			Intent regIntent = new Intent(context, CommonService.class);
			context.startService(regIntent);
		} else  {
			Log.d(TAG, "else ACTION_HEARTBEAT_COMMON_ALARM");
		}
	}

}