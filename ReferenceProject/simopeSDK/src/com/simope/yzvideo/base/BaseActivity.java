package com.simope.yzvideo.base;

import nativeInterface.SmoAdMultiPlay.ParseSmoCompleteListener;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;



public class BaseActivity extends Activity implements ParseSmoCompleteListener{

	public boolean is_wifi = true;
	public boolean no_net = false;	
	public boolean mReceiverPlay_isRegister = false;
	public static final String KEY_USER_CURRENT_TIME="KEY_USER_CURRENT_TIME";  
	public static final String KEY_USER_TOTAL_TIME="KEY_USER_TOTAL_TIME"; 
	public static final int RESULT_OK =9; 
	
	public void onCreate(Bundle bundle) {
		super.onCreate(bundle);
		
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN,
				WindowManager.LayoutParams.FLAG_LAYOUT_INSET_DECOR);
		if(android.os.Build.VERSION.SDK_INT>13){
			getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);	
		}
		if(android.os.Build.VERSION.SDK_INT>18){
			View decorView = getWindow().getDecorView();
                decorView.setSystemUiVisibility( View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN);
		}
		IntentFilter mFilter = new IntentFilter();
		mFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
		registerReceiver(mReceiverPlay, mFilter);
		mReceiverPlay_isRegister = true;
	}

	protected void onResume() {
		super.onResume();
	}

	protected void onPause() {
		super.onPause();
	}

	@Override
	protected void onStop() {
		super.onStop();
	}

	protected void onDestroy() {
		super.onDestroy();
		if (mReceiverPlay_isRegister) {
			try {
				unregisterReceiver(mReceiverPlay);
				mReceiverPlay_isRegister = false;
			} catch (Exception e) {
				
			}
		}
	}

	
	public BroadcastReceiver mReceiverPlay = new BroadcastReceiver() {
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (action.equals(ConnectivityManager.CONNECTIVITY_ACTION)) {
				Log.d("mark", "网络状态已经改变");
				ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
				NetworkInfo info = connectivityManager.getActiveNetworkInfo();
				if (info != null && info.isAvailable()) {
					no_net = false;
					String name = info.getTypeName();
					if (name.equals("WIFI")) {
						is_wifi = true;
						Log.d("mark", "当前网络名称：" + "is_wifi");
					}else{
						Log.d("mark", "当前网络名称：" + "mobile");
						is_wifi=false;
					}
				} else {
					no_net = true;
					Log.d("mark", "没有可用网络");
				}
			}
		}
	};

	public int px2dip(Context context, float pxValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (pxValue / scale + 0.5f);
	}


	@Override
	public void parSmoComplete(int result) {
		
	}
	
	public String [] SubNullArrItems(String []arr){
		if(arr==null||arr.length<1){
			return null;
		}
		int index=0;
		String [] tempArr = null;
		for(int i=1;i<arr.length;i++){
			if(arr[i]==null){
				index=i;
				tempArr=new String[index];
				break;
			}
		}
		System.arraycopy(arr, 0, tempArr, 0, index);		
		return tempArr;
	}
}
