package com.cmmobi.looklook.receiver;



import android.content.Context;
import android.content.Intent;
import android.util.Log;
import cn.zipper.framwork.core.ZBroadcastReceiver;

import com.cmmobi.looklook.common.service.CoreService;
//import com.cmmobi.looklook.common.service.CommonService;

public class AlarmReceiver extends ZBroadcastReceiver {	
	public static final String ACTION_SERVICE_RESTART_ALARM = "ACTION_SERVICE_RESTART_ALARM";
	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
/*		Log.e("AlarmReceiver", "onReceive start common service" );
	    Intent startIntent = new Intent(context, CommonService.class);
	    context.startService(startIntent);*/
	    
		//Log.e("AlarmReceiver", "onReceive start CoreService service" );
	    //Intent startIntent2 = new Intent(context, CoreService.class);
	    //context.startService(startIntent2);
	}

}


