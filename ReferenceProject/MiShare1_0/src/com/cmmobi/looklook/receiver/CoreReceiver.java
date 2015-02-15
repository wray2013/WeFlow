package com.cmmobi.looklook.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class CoreReceiver extends BroadcastReceiver {
	private static final String TAG = "CoreReceiver";
	public static final String ACTION_CORE_ALARM = "ACTION_CORE_ALARM";
	
	@Override
	public void onReceive(Context context, Intent intent) {
		String action = intent.getAction();
		if (CoreReceiver.ACTION_CORE_ALARM.equals(action)) {
			Log.d(TAG, "ACTION_CORE_ALARM");
			//Intent regIntent = new Intent(context, CommonService.class);
			//context.startService(regIntent);
		} else  {
			Log.d(TAG, "else ACTION_CORE_ALARM");
		}
	}

}
