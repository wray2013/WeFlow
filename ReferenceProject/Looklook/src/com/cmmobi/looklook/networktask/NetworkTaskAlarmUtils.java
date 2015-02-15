package com.cmmobi.looklook.networktask;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.cmmobi.looklook.info.profile.TimeHelper;
import com.cmmobi.looklook.receiver.NetworkTaskAlarmReceiver;

public class NetworkTaskAlarmUtils {
	
	public static final int TIME_10_MINUTES = 10 * 60 * 1000;

	private static AlarmManager gAlarmManager = null;
	
	public static AlarmManager getAlarmManager(Context ctx) {
		if(gAlarmManager == null) {
			gAlarmManager = (AlarmManager) ctx.getSystemService(Context.ALARM_SERVICE);
		}
		return gAlarmManager;
	}

	public boolean isAlarmExist() {
		return gAlarmManager != null;
	}
	
	//开始计时
	public void startAlarm(Context ctx) {
		Log.i("NetworkTaskAlarmUtils", "Start timing, delay time :" + TIME_10_MINUTES);

		gAlarmManager = getAlarmManager(ctx);
		// 一分钟后将产生广播,触发UpdateReceiver的执行
		Intent i = new Intent(NetworkTaskAlarmReceiver.ACTION_NETWORKTASK_ALARM);
		PendingIntent pendingIntent = PendingIntent.getBroadcast(ctx, 0, i, 0);
		gAlarmManager.set(AlarmManager.RTC, TimeHelper.getInstance().now() + TIME_10_MINUTES, pendingIntent);
	}

	//取消计时
	public void cancelAlarm(Context ctx){
		gAlarmManager = getAlarmManager(ctx);
	    Intent i = new Intent(NetworkTaskAlarmReceiver.ACTION_NETWORKTASK_ALARM);
	    PendingIntent pendingIntent = PendingIntent.getBroadcast(ctx, 0, i, 0);
	    gAlarmManager.cancel(pendingIntent);
	    gAlarmManager = null;
	}
}
