package cn.zipper.framwork.core;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.support.v4.content.LocalBroadcastManager;

public final class ZBroadcastReceiverManager {
	
	private Context context;
	private List<ZBroadcastReceiver> list;
	private List<ZBroadcastReceiver> localList;
	
	
	public ZBroadcastReceiverManager(Context context) {
		this.context = context;
		list = new ArrayList<ZBroadcastReceiver>();
		localList = new ArrayList<ZBroadcastReceiver>();
	}
	
	public final synchronized void registerZReceiver(ZBroadcastReceiver receiver) {
		if (!list.contains(receiver)) {
			list.add(receiver);
			context.registerReceiver(receiver, receiver.getIntentFilter());
		}
	}
	
	public final synchronized void registerLocalZReceiver(ZBroadcastReceiver receiver) {
		if (!localList.contains(receiver)) {
			localList.add(receiver);
			LocalBroadcastManager.getInstance(context).registerReceiver(receiver, receiver.getIntentFilter());
		}
	}
	
	public final synchronized void unregisterZReceiver(ZBroadcastReceiver receiver) {
		if (list.contains(receiver)) {
			list.remove(receiver);
			context.unregisterReceiver(receiver);
		}
	}
	
	public final synchronized void unregisterLocalZReceiver(ZBroadcastReceiver receiver) {
		if (localList.contains(receiver)) {
			localList.remove(receiver);
			LocalBroadcastManager.getInstance(context).unregisterReceiver(receiver);
		}
	}
	
	public final synchronized void registerAllZReceiver() {
		for (ZBroadcastReceiver receiver : list) {
			registerZReceiver(receiver);
		}
	}
	
	public final synchronized void registerAllLocalZReceiver() {
		for (ZBroadcastReceiver receiver : localList) {
			registerLocalZReceiver(receiver);
		}
	}
	
	public final synchronized void unregisterAllZReceiver(boolean clearCacheList) {
		for (ZBroadcastReceiver receiver : list) {
			unregisterZReceiver(receiver);
		}
		if (clearCacheList) {
			list.clear();
		}
	}
	
	public final synchronized void unregisterAllLocalZReceiver(boolean clearCacheList) {
		for (ZBroadcastReceiver receiver : localList) {
			unregisterLocalZReceiver(receiver);
		}
		if (clearCacheList) {
			localList.clear();
		}
	}

}
