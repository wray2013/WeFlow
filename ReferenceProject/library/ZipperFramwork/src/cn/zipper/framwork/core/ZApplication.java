package cn.zipper.framwork.core;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.support.v4.content.LocalBroadcastManager;
import cn.zipper.framwork.utils.ZDateUtils;

public class ZApplication extends Application {
	
	private static ZApplication application;
	
	private ZBroadcastReceiverManager receiverManager;
	private Resources resources;
	
	
	@Override
	public void onCreate() {
		super.onCreate();
		application = this;
		resources = getResources();
		
		ZLog.printStarLine();
		ZLog.e(this.getPackageName() + " @ "  + ZDateUtils.getFormatDate24());
	}
	
	public final static ZApplication getInstance() {
		return application;
	}
	
	public final ZBroadcastReceiverManager getZReceiverManager() {
		if (receiverManager == null) {
			receiverManager = new ZBroadcastReceiverManager(this);
		}
		return receiverManager;
	}
	
	@Override
	public void onTerminate() {
		super.onTerminate();
		ZLog.e();
		if (receiverManager != null) {
			receiverManager.unregisterAllZReceiver(true);
			receiverManager.unregisterAllLocalZReceiver(true);
		}
	}
	
	public final boolean sendLocalBroadcast(Intent intent) {
		return LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
	}
	
	public final void sendLocalBroadcastSync(Intent intent) {
		LocalBroadcastManager.getInstance(this).sendBroadcastSync(intent);
	}
	
	public final int getColor(int id) {
		return resources.getColor(id);
	}
	
	public final Drawable getDrawable(int id) {
		return resources.getDrawable(id);
	}
	
	public final Context createPackageContext(String pakageName) {
		Context context = null;
		try {
			context = createPackageContext(pakageName, Context.CONTEXT_INCLUDE_CODE|Context.CONTEXT_IGNORE_SECURITY);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return context;
	}
	
	public final void exit() {
		ZLog.alert();
		ZLog.e();
		System.exit(0);
	}
	
	
	
	
}
