package com.cmmobi.looklook.info.profile;

public class TimeHelper {
	private long delta = 0; //server - local
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
		delta = serverTime - System.currentTimeMillis();
	}
	
	public synchronized long now(){
		return System.currentTimeMillis() + delta;
	}

}
