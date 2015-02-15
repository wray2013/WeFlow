package cn.zipper.framwork.core;

import android.app.ActivityGroup;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Handler.Callback;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import cn.zipper.framwork.core.ZDataExchanger.ZDataExchangerKey;

public abstract class ZActivityGroup extends ActivityGroup implements Callback, OnClickListener {
	
	private ZBroadcastReceiverManager receiverManager;
	private ZViewFinder viewFinder;
	private Handler handler;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		viewFinder = new ZViewFinder(getWindow());
		handler = new Handler(this);
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		if (receiverManager != null) {
			receiverManager.registerAllZReceiver();
		}
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (receiverManager != null) {
			receiverManager.unregisterAllZReceiver(true);
		}
	}
	
	public final ZBroadcastReceiverManager getZReceiverManager() {
		if (receiverManager == null) {
			receiverManager = new ZBroadcastReceiverManager(this);
		}
		return receiverManager;
	}
	
	public ZViewFinder getZViewFinder() {
		return viewFinder;
	}
	
	public Handler getHandler() {
		return handler;
	}
	
	protected final Object getSerializableExtra(String key) {
		return getIntent().getSerializableExtra(key);
	}
	
	protected final ZDataExchangerKey getZDataExchangerKey(String key) {
		return (ZDataExchangerKey) getSerializableExtra(key);
	}
	
	protected final void setToNoTitle() {
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
	}
	
	protected final void setToFullscreen() {
		this.getWindow().setFlags(
				WindowManager.LayoutParams.FLAG_FULLSCREEN, 
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
	}
	
	protected final void setToLandscape() {
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
	}
	
	protected final void setToPortrait() {
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
	}
	
	protected final View getActivityDecorView(String id, Intent intent) {
		return getLocalActivityManager().startActivity(id, intent).getDecorView();
	}

}
