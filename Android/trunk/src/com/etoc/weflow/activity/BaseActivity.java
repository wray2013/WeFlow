package com.etoc.weflow.activity;

import com.etoc.weflow.dialog.PromptDialog;
import com.etoc.weflow.event.DialogEvent;

import de.greenrobot.event.EventBus;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.DownloadManager;
import android.app.NotificationManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Handler.Callback;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

public abstract class BaseActivity extends FragmentActivity implements Callback {

	public abstract int rootViewId();
	public abstract int subContentViewId();
	
	protected Handler handler;
	@Override
	protected void onCreate(Bundle arg0) {
		// TODO Auto-generated method stub
		super.onCreate(arg0);
		
		if(rootViewId() != 0){
			setContentView(rootViewId());
		}
		
		EventBus.getDefault().register(this);
		
		handler = new Handler(this);
	}
	
	@Override
	protected void onResume() {
		super.onResume();
	}

	@Override
	protected void onPause() {
		super.onPause();
	}
	

	@Override
	protected void onStop(){
		super.onStop();
	}
	
	@Override
	protected void onDestroy(){
		super.onDestroy();
		EventBus.getDefault().unregister(this);
	}
	
	public static void ProcessDialogEvent(final Context context, final DialogEvent evt, String className){
		if(!isTopActivity(context, className)){
			return;
		}
		
		switch(evt){
		case LOADING_START:
			PromptDialog.showProgressDialog(context);
			break;
		case LOADING_END:
			PromptDialog.dimissProgressDialog();
			break;
		}
	
	}
	
	public void onEventMainThread(Object event) {
		if(event instanceof DialogEvent){
			DialogEvent evt  =(DialogEvent) event;
			ProcessDialogEvent(this, evt, this.getClass().getName());
		}
	}
	
	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
		if (ev.getAction() == MotionEvent.ACTION_DOWN) {
			View v = getCurrentFocus();
			if (isShouldHideInputScreen(v, ev)) {
				InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
				if (imm != null) {
					imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
				}
			}
			return super.dispatchTouchEvent(ev);
		}
		// 必不可少，否则所有的组件都不会有TouchEvent了
		if (getWindow().superDispatchTouchEvent(ev)) {
			return true;
		}
		return onTouchEvent(ev);
	}

	
	public  boolean isShouldHideInputScreen(View v, MotionEvent event) {
		if (v != null && (v instanceof EditText)) {
			int[] leftTop = { 0, 0 };
			//获取输入框当前的location位置
			v.getLocationOnScreen(leftTop);
			int left = leftTop[0];
			int top = leftTop[1];
			int bottom = top + v.getHeight();
			int right = left + v.getWidth();
			if (event.getRawX() > left && event.getRawX() < right
					&& event.getRawY() > top && event.getRawY() < bottom) {
				// 点击的是输入框区域，保留点击EditText的事件
				return false;
			} else {
				return true;
			}
		}
		return false;
	}
	
	public static boolean isTopActivity(Context context, String className)  
    {  
        boolean isTop = false;  
        ActivityManager am = (ActivityManager)context.getSystemService(ACTIVITY_SERVICE);  
        ComponentName cn = am.getRunningTasks(1).get(0).topActivity;  
        Log.d("TAG", "isTopActivity = " + cn.getClassName() + ", cur:" + className);  
        if (cn.getClassName().contains(className))  
        {  
            isTop = true;  
        }  
        Log.d("TAG", "isTop = " + isTop);  
        return isTop;  
    } 
}
