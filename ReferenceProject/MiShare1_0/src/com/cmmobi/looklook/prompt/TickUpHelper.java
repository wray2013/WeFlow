package com.cmmobi.looklook.prompt;

import com.cmmobivideo.utils.PluginUtils;

import android.os.Handler;
import android.util.Log;

public class TickUpHelper {
	
	public final String TAG = "TickUpHelper";
	public static final int HANDLER_FLAG_TICK_UP = 0x0817381;
	public static final int HANDLER_FLAG_TICK_STOP = 0x0817382;
	//private Handler handler;
	//private int timetick;
	
	private static TickUpHelper ins = null;
	private Handler outHandler;
	private LocalTick localTask;
	
	private boolean stopThread;
	private int delay_sec;
	private boolean isStarted;
	
	private TickUpHelper(){
		//handler = new Handler(this);
	}
	
	public static TickUpHelper getInstance(Handler handler){
		if(ins==null){
			ins = new TickUpHelper();
		}
		
		ins.outHandler = handler;
		
		return ins;
	}
	
	public void init() {
		stopThread = false;
	}
	
	public void start(int secords){
		/*if (delay_sec == 0 && stopThread) {
			Log.d(TAG,"start delay_sec == 0 ");
			stopThread = false;
			return;
		}*/
		isStarted = true;
		localTask = new LocalTick();
		localTask.execute(secords);
		Log.d(TAG,"start normal");
	}
	
	public void stop(int delay_sec){
		Log.d(TAG,"stop in delay_sec = " + delay_sec);
		
		stopThread = true;
		this.delay_sec = delay_sec;
		if(delay_sec==0){
			if (!isStarted) {
				if (outHandler != null) {
					Log.d(TAG,"isStarted false");
		    		outHandler.obtainMessage(HANDLER_FLAG_TICK_STOP, 0).sendToTarget();
		    	}
			}
			if(localTask!=null){
				localTask.interrupt();
				Log.d(TAG,"stop thread interrupt stopThread = " + stopThread);
				localTask = null;
			} 
		}
		isStarted = false;
		
	}
			
	private class LocalTick extends Thread{
		int timetick = 0;
		int curTime = 0;
		
		public void execute(int secords) {
			// TODO Auto-generated method stub
//			stopThread = false;
			this.timetick = secords;
			this.start();
		}
		
	    public void run() {
	    	
	    	boolean canStop = delay_sec>0?false:true;
	    	
	    	while((curTime < timetick * 10 && !stopThread) || !canStop){
	    		try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					break;
				}
	    		
	    		curTime++;
	    		
	    		canStop = (curTime / 10 >=delay_sec)?true:false; 
	    		if (curTime %10 == 0 && outHandler != null) {
	    			outHandler.obtainMessage(HANDLER_FLAG_TICK_UP, (Integer)(curTime / 10)).sendToTarget();
	    		}
//	    		Log.d(TAG,"LocalTick curTime = " + curTime);
	    	}
	    	Log.d(TAG,"LocalTick HANDLER_FLAG_TICK_STOP stopThread = " + stopThread + "canStop = " + canStop);
	    	stopThread = false;
	    	delay_sec = 0;
	    	
	    	if (outHandler != null) {
	    		outHandler.obtainMessage(HANDLER_FLAG_TICK_STOP, (Integer)timetick).sendToTarget();
	    	}
	    }
	}
}
