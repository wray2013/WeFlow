package com.etoc.weflow.utils;

import com.etoc.weflow.activity.MainActivity;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

public class NotificationUtil {
	
	private NotificationUtil() {
		
	}
	
	public static void PopNotification(Context ctx, int iconId, String maintitle,
			String contentTitle, String contentText) {
		// 消息通知栏
		// 定义NotificationManager
		String ns = Context.NOTIFICATION_SERVICE;
		NotificationManager mNotificationManager = (NotificationManager) ctx
				.getSystemService(ns);

		// 定义通知栏展现的内容信息
		long when = System.currentTimeMillis();
		Notification notification = new Notification(iconId, maintitle, when);

		// 定义下拉通知栏时要展现的内容信息
		Context context = ctx.getApplicationContext();
		Intent notificationIntent = new Intent(ctx, MainActivity.class);
		PendingIntent contentIntent = PendingIntent.getActivity(ctx, 0,
				notificationIntent, 0);
		notification.setLatestEventInfo(context, contentTitle, contentText,
				contentIntent);
		notification.flags = Notification.FLAG_AUTO_CANCEL;
		notification.defaults |= Notification.DEFAULT_SOUND;//设置通知栏来通知的提示音
		// 用mNotificationManager的notify方法通知用户生成标题栏消息通知
		mNotificationManager.notify(1, notification);
	}
}
