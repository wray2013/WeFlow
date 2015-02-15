package com.cmmobi.looklook.receiver;



import android.content.Context;
import android.content.Intent;
import android.util.Log;
import cn.zipper.framwork.core.ZBroadcastReceiver;

import com.cmmobi.looklook.common.service.CommonService;

public class DailyReceiver extends ZBroadcastReceiver {
	public static final String ACTION_REFRESH_DAILYSCAN_ALARM = "ACTION_REFRESH_DAILYSTART_ALARM";
		
	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		Log.e("DailyReceiver", "onReceive start common service" );
	    Intent startIntent = new Intent(context, CommonService.class);
	    //context.startService(startIntent);
	}

}


