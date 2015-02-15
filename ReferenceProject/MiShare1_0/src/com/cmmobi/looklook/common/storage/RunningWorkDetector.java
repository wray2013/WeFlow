package com.cmmobi.looklook.common.storage;

import android.util.Log;

public class RunningWorkDetector {
	private static final String TAG = "RunningWorkDetector";
	private String flag[] = { "true" };
	private int workNum;
	
	private static RunningWorkDetector ins;
	private RunningWorkDetector(){
		workNum = 0;
	}
	
	public static RunningWorkDetector getInstance(){
		if(ins==null){
			ins = new RunningWorkDetector();
		}
		
		return ins;
	}
	
	
	public synchronized void lock(){
		synchronized (flag) {
			workNum++;
			Log.v(TAG, "RunningWorkDetector - lock - workNum:" + workNum);
			flag.notify();
		}
	}
	
	public synchronized void unlock(){
		synchronized (flag) {
			workNum--;
			Log.v(TAG, "RunningWorkDetector - unlock - workNum:" + workNum);
			flag.notify();
		}
	}
	
	public synchronized void join(){
		long pre_now = System.currentTimeMillis();
		while(workNum>0){
			Log.v(TAG, "RunningWorkDetector - join - workNum:" + workNum);
			synchronized (flag) {
				try {
					flag.wait(1000);
					//Thread.sleep(100);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
			
			long cur_now = System.currentTimeMillis();
			if(cur_now - pre_now > 2000){
				workNum = 0;
				break;
			}

		}
	}

}
