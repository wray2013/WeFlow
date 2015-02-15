package com.cmmobi.looklook.prompt;

import android.os.Handler;


public class TickDownHelper /*implements Callback */{
	public static final int HANDLER_FLAG_TICK_DOWN = 0x0817381;
	public static final int HANDLER_FLAG_TICK_STOP = 0x0817382;
	//private Handler handler;
	//private int timetick;
	
	private static TickDownHelper ins = null;
	private Handler outHandler;
	private LocalTick localTask;
	
	private boolean stopThread;
	private int delay_sec;
	
	private TickDownHelper(){
		//handler = new Handler(this);
	}
	
	public static TickDownHelper getInstance(Handler handler){
		if(ins==null){
			ins = new TickDownHelper();
		}
		
		ins.outHandler = handler;
		
		return ins;
	}
	
	public void start(int secords){
/*		timetick = secords;
		handler.sendEmptyMessageDelayed(HANDLER_FLAG_TICK_DOWN, 1000);*/
		stop(0);
		
		localTask = new LocalTick();
		localTask.execute(secords);
		
	}
	
	public void stop(int delay_sec){
/*		handler.removeMessages(HANDLER_FLAG_TICK_DOWN);*/
		stopThread = true;
		this.delay_sec = delay_sec;
/*		if(localTask!=null){
			localTask.interrupt();
			localTask = null;
		}*/
	}
	

/*	@Override
	public boolean handleMessage(Message msg) {
		// TODO Auto-generated method stub
		switch(msg.what){
		case HANDLER_FLAG_TICK_DOWN:
			timetick--;
			handler.sendEmptyMessageDelayed(HANDLER_FLAG_TICK_DOWN, 1000);
			outHandler.obtainMessage(HANDLER_FLAG_TICK_DOWN, (Integer)timetick);
			break;
		}
		return false;
	}*/
	
	private class LocalTick extends Thread{
		int timetick = 0;
		
		
		public void execute(int secords) {
			// TODO Auto-generated method stub
			this.timetick = secords;
			this.start();
		}
		
	    public void run() {
	    	stopThread = false;
	    	int start_time = timetick;
	    	boolean canStop = delay_sec>0?false:true;
	    	
	    	while((timetick>0 && !stopThread) || !canStop){
	    		try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
	    		
	    		timetick--;
	    		canStop = (start_time-timetick>=delay_sec)?true:false; 
	    		
	    		outHandler.obtainMessage(HANDLER_FLAG_TICK_DOWN, (Integer)timetick).sendToTarget();
	    	}
	    	
	    	outHandler.obtainMessage(HANDLER_FLAG_TICK_STOP, (Integer)timetick).sendToTarget();

	    }
		
	}
	
	

}
