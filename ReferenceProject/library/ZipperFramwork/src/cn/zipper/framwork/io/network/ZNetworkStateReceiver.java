package cn.zipper.framwork.io.network;

import android.net.ConnectivityManager;
import cn.zipper.framwork.core.ZBroadcastReceiver;

public abstract class ZNetworkStateReceiver extends ZBroadcastReceiver {
	
	
	public ZNetworkStateReceiver() {
		super(ConnectivityManager.CONNECTIVITY_ACTION);
	}
	
}
