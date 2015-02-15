package com.cmmobi.looklook.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.cmmobi.looklook.common.service.DiarySyncService;

public class DiarySyncHeartbeatReceiver extends BroadcastReceiver{

	private static final String TAG = "DiarySyncHeartbeatReceiver";
	public static final String ACTION_HEARTBEAT_DIARY_SYNC = "ACTION_HEARTBEAT_DIARY_SYNC";
	
	@Override
	public void onReceive(Context context, Intent intent) {
		String action = intent.getAction();
		if(ACTION_HEARTBEAT_DIARY_SYNC.equals(action)) {
//			Log.d(TAG, "DIARY SYNC HEARTBEAT");
			Intent regIntent = new Intent(context, DiarySyncService.class);
			context.startService(regIntent);
		}
	}

}
