package cn.zipper.framwork.core;

import android.content.BroadcastReceiver;
import android.content.IntentFilter;

public abstract class ZBroadcastReceiver extends BroadcastReceiver {
	
	protected IntentFilter intentFilter;
	
	public ZBroadcastReceiver() {
		
	}
	
	public ZBroadcastReceiver(String action) {
		addAction(action);
	}
	
	protected IntentFilter getIntentFilter() {
		if (intentFilter == null) {
			intentFilter = new IntentFilter();
		}
		return intentFilter;
	}
	
	protected void addAction(String action) {
		if (action != null) {
			getIntentFilter().addAction(action);
			getIntentFilter().setPriority(Integer.MAX_VALUE);
		}
	}
	

}
