package com.cmmobi.looklook.receiver;

import android.content.Context;
import android.content.Intent;
import android.net.NetworkInfo;
import android.net.NetworkInfo.State;
import android.net.wifi.WifiManager;
import android.os.Parcelable;
import android.util.Log;
import cn.zipper.framwork.core.ZBroadcastReceiver;

public class NetworkConnectChangedReceiver extends ZBroadcastReceiver {
	public static final String ACTION_WIFI_CONNECTED = "ACTION_WIFI_CONNECTED";

	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		
		// 这个监听wifi的连接状态即是否连上了一个有效无线路由，
		// 当上边广播的状态是WifiManager.WIFI_STATE_DISABLING，和WIFI_STATE_DISABLED的时候，根本不会接到这个广播。
		// 在上边广播接到广播是WifiManager.WIFI_STATE_ENABLED状态的同时也会接到这个广播，
		// 当然刚打开wifi肯定还没有连接到有效的无线
		if (WifiManager.NETWORK_STATE_CHANGED_ACTION.equals(intent.getAction())) {
			Parcelable parcelableExtra = intent
					.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
			if (null != parcelableExtra) {
				NetworkInfo networkInfo = (NetworkInfo) parcelableExtra;
				State state = networkInfo.getState();
				boolean isConnected = state == State.CONNECTED;// 当然，这边可以更精确的确定状态
				if (isConnected) {
					Intent intent_to_send = new Intent(ACTION_WIFI_CONNECTED);
					// You can also include some extra data.
					//intent_to_send.putExtra("message", "This is my message!");
					Log.e("NetworkConnectChangedReceiver", "onReceive send ACTION_WIFI_CONNECTED" );
					//LocalBroadcastManager.getInstance(context).sendBroadcast(intent_to_send);
					
					

				} 
			}
		}
	}

}
