package com.cmmobi.looklook.info.profile;

import android.util.Log;

public class TimeHelper {
	private static final String TAG = "TimeHelper";
	private static long delta = 0; //server - local
	private static TimeHelper ins = null;
	private TimeHelper(){
		delta = 0;
	}
	
	public static TimeHelper getInstance(){
		if(ins==null){
			ins = new TimeHelper();
		}
		
		return ins;
	}
	
	public synchronized void syncServerTime(long serverTime){
		//Log.e(TAG, "TimeHelper - syncServerTime:" + serverTime + " ins " + ins.hashCode());
		delta = serverTime - System.currentTimeMillis();
		//Log.e(TAG, "TimeHelper - syncServerTime - delta:" + delta);
	}
	
	public long now(){
		//Log.e(TAG, "TimeHelper - delta:" + delta + " ins " + ins.hashCode());
		//Log.e(TAG, "TimeHelper - now:" + (System.currentTimeMillis() + delta));
		return System.currentTimeMillis() + delta;
	}
}
