package cn.zipper.framwork.core;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Handler.Callback;
import android.support.v4.content.LocalBroadcastManager;
import android.view.View.OnClickListener;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import cn.zipper.framwork.R;
import cn.zipper.framwork.core.ZDataExchanger.ZDataExchangerKey;

/**
 * Activity基类, 用来生成默认的Handler和View查找器, 同时自动管理广播接收器;
 * ZActivity已经实现了Callback 和 OnClickListener接口, 子类不用再次实现;
 * @author Sunshine
 *
 */
public abstract class ZActivity extends Activity implements Callback, OnClickListener {
	
	private ZBroadcastReceiverManager receiverManager;
	protected ZViewFinder viewFinder;
	protected Handler handler;
	
	private boolean open_ViewServer = true;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		viewFinder = new ZViewFinder(getWindow());
		handler = new Handler(this);
		getZReceiverManager().unregisterAllZReceiver(true);
		getZReceiverManager().unregisterAllLocalZReceiver(true);
		
		if(open_ViewServer){
	        ViewServer.get(this).addWindow(this);
		}

	}
	
	@Override
	protected void onResume() {
		super.onResume();
		if (receiverManager != null) {
			receiverManager.registerAllZReceiver();
			receiverManager.registerAllLocalZReceiver();
		}
		
		if(open_ViewServer){
	    	ViewServer.get(this).setFocusedWindow(this);
		}

	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (receiverManager != null) {
			receiverManager.unregisterAllZReceiver(true);
			receiverManager.unregisterAllLocalZReceiver(true);
		}
		
		if(open_ViewServer){
	    	ViewServer.get(this).removeWindow(this);
		}

	}
	
	/**
	 * 获取广播接收器的管理器;
	 * @return
	 */
	public final ZBroadcastReceiverManager getZReceiverManager() {
		if (receiverManager == null) {
			receiverManager = new ZBroadcastReceiverManager(this);
		}
		return receiverManager;
	}
	
	/**
	 * 获取View查找器, 方便获取View对象;
	 * @return
	 */
	public ZViewFinder getZViewFinder() {
		return viewFinder;
	}
	
	/**
	 * 获取本Activity的Handler;
	 * @return
	 */
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
	
	/**
	 * 强制横屏;
	 */
	protected final void setToLandscape() {
		this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
	}
	
	/**
	 * 强制竖屏;
	 */
	protected final void setToPortrait() {
		this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
	}
	
//	protected final void measureSize(View view) {
//		view.getViewTreeObserver().addOnGlobalLayoutListener(listener)
//	}
	
	/**
	 * 展示Activity切换动画;
	 * 
	 * 不想使用ZipperFramework自带的动画, 可以使用函数startActivitySwitchAnimation(int inAnimation, int outAnimation);
	 * 
	 * @param inOrOut: 进入到新Activity则传入true, 后退到上一个Activiry则传入false;
	 */
	protected final void startActivitySwitchAnimation(Boolean inOrOut) {
		if (inOrOut != null) {
			if (inOrOut) {
				super.overridePendingTransition(R.anim.z_animation_slide_in_from_right, R.anim.z_animation_slide_out_to_left);
			} else {
				super.overridePendingTransition(R.anim.z_animation_slide_in_from_left, R.anim.z_animation_slide_out_to_right);
			}
		}
	}
	
	/**
	 * 指定Activity切换动画;
	 * @param inAnimation: 将要进入屏幕的Activity使用的动画(R.anim.xxxx);
	 * @param outAnimation: 将要移出屏幕的Activity使用的动画(R.anim.xxxx);
	 */
	protected final void startActivitySwitchAnimation(int inAnimation, int outAnimation) {
		super.overridePendingTransition(inAnimation, outAnimation);
	}
	

}
