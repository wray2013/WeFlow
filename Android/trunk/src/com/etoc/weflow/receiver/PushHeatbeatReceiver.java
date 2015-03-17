package com.etoc.weflow.receiver;


import com.etoc.weflow.service.PushService;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class PushHeatbeatReceiver extends BroadcastReceiver {

	public static final String ACTION_HEARTBEAT_PUSH_MSG = "ACTION_HEARTBEAT_PUSH_MSG";
	
	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		String action = intent.getAction();
		if(ACTION_HEARTBEAT_PUSH_MSG.equals(action)) {
			Log.d("PushHeatbeatReceiver", "New Push Message Received.");
			Intent regIntent = new Intent(context, PushService.class);
//			context.startService(regIntent);
		}
	}

}
