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
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.widget.Toast;

public abstract class BaseActivity extends FragmentActivity {

	public abstract int rootViewId();
	
	@Override
	protected void onCreate(Bundle arg0) {
		// TODO Auto-generated method stub
		super.onCreate(arg0);
		
		if(rootViewId() != 0){
			setContentView(rootViewId());
		}
		
		EventBus.getDefault().register(this);
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
