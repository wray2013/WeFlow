package com.cmmobi.looklook.receiver;

import com.cmmobi.looklook.common.service.CoreService;
import com.cmmobi.looklook.info.profile.ActiveAccount;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class BootCompletedReceiver extends BroadcastReceiver {
	private static final String TAG = "BootCompletedReceiver";

	@Override
	public void onReceive(Context context, Intent intent) {
		if (intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)) {
			Log.v(TAG, "onReceive - ACTION_BOOT_COMPLETED");
/*			ActiveAccount aa = ActiveAccount.getInstance(context);
			if (aa.isLogin()) {
				Intent heartBeatServiceIntent = new Intent(context,
						CoreService.class);
				heartBeatServiceIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				heartBeatServiceIntent.putExtra(CoreService.SYNC_TIMELINE, 1);
				heartBeatServiceIntent.putExtra(CoreService.LOGIN_USERID,
						aa.getLookLookID());
				context.startService(heartBeatServiceIntent);
			}*/
		}
	}
}