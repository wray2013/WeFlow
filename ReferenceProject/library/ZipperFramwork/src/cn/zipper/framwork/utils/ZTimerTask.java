package cn.zipper.framwork.utils;

import java.util.Timer;
import java.util.TimerTask;

public abstract class ZTimerTask extends TimerTask {
	
	private Timer timer;
	
	public ZTimerTask() {
		timer = new Timer();
	}

	public void schedule(long delay) {
		timer.schedule(this, delay);
	}
	
	public void schedule(long delay, long period) {
		timer.schedule(this, delay, period);
	}
	
	public void stop() {
		this.cancel();
		timer.purge();
		timer.cancel();
	}

}
