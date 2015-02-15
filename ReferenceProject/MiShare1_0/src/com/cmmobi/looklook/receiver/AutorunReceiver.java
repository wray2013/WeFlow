package com.cmmobi.looklook.receiver;

//import com.cmmobi.looklook.common.service.CommonService;
import com.cmmobi.looklook.common.service.CoreService;
import com.cmmobi.looklook.info.profile.ActiveAccount;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.NetworkInfo.State;

public class AutorunReceiver extends BroadcastReceiver{

	@Override
	public void onReceive(Context context, Intent intent) {
		
		String action = intent.getAction();
		
		// 屏幕解锁，电源键
		if(Intent.ACTION_USER_PRESENT.equals(action)){
			
			startService(context);
			
		}else  // 网络状态改变
		if (ConnectivityManager.CONNECTIVITY_ACTION.equals(action)) {
			ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
			if (connectivityManager != null) {
				NetworkInfo[] networkInfos = connectivityManager.getAllNetworkInfo();

				for (int i = 0; i < networkInfos.length; i++) {
					State state = networkInfos[i].getState();
					if (NetworkInfo.State.CONNECTED == state) {
						System.out.println("------------> Network is ok");
						startService(context);
						return;
					}
				}
			}


		}
		
	}

	
	private void startService(Context context){
		Intent heartBeatServiceIntent = new Intent(context,	CoreService.class);
		context.startService(heartBeatServiceIntent);
		//context.startService(new Intent(context, CommonService.class));
		//context.startService(new Intent(context, CoreService.class));
/*		ActiveAccount aa = ActiveAccount.getInstance(context);
		if(aa.isLogin()){
			Intent heartBeatServiceIntent = new Intent(context,	CoreService.class);
			heartBeatServiceIntent.putExtra(CoreService.SYNC_TIMELINE, 1);
			heartBeatServiceIntent.putExtra(CoreService.LOGIN_USERID, aa.getLookLookID());
			context.startService(heartBeatServiceIntent);
		}*/

	}
	
}
