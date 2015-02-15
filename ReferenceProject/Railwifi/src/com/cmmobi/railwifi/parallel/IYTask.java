package com.cmmobi.railwifi.parallel;

import android.util.Log;

import com.cmmobi.railwifi.event.ParallelEvent;

import de.greenrobot.event.EventBus;

public abstract class IYTask implements Runnable {
	private static final String TAG = "IYTask";
	protected ParallelEvent event;
	protected long event_id;
	
	public IYTask(ParallelEvent event, long event_id){
		this.event = event;
		this.event_id = event_id;
	}
	
	public void processTask(String result){
		Log.v(TAG, "processTask - result:" + result);
		event.setValue(result);
		EventBus.getDefault().post(event);
	}
	
	public boolean canRun(){
		return !ParallelManager.getInstance().hasEventRunning(event_id);
	}
	
	public void beginRun(){
		ParallelManager.getInstance().beginEventRunning(event_id);
	}
	
	public void endRun(){
		ParallelManager.getInstance().endEventRunning(event_id);
	}
}
