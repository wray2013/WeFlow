package cn.zipper.framwork.utils;

import cn.zipper.framwork.core.ZLog;

public final class ZThread extends Thread {
	
	public static void sleep(long time) {
		try {
			Thread.sleep(Math.abs(time));
		} catch (Exception e) {
			ZLog.printStackTrace();
		}
	}
	
	public static void yield() {
		try {
			Thread.yield();
		} catch (Exception e) {
			ZLog.printStackTrace();
		}
	}
	
	public static void setToMaxPriority() {
		int priority = Thread.currentThread().getThreadGroup().getMaxPriority();
		Thread.currentThread().setPriority(priority);
	}

}
