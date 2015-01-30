package com.etoc.weflow.activity.login;

import com.etoc.weflow.R;

import android.annotation.TargetApi;
import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Window;

public class StartAccountActivity extends Activity {

	private final String TAG = "StartAccountActivity";
	
	private DisplayMetrics dm = new DisplayMetrics();
	
	@TargetApi(Build.VERSION_CODES.HONEYCOMB)
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		dm = getResources().getDisplayMetrics();
		
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setFinishOnTouchOutside(false);
		
		setContentView(R.layout.activity_startaccount);
		
	}
}
