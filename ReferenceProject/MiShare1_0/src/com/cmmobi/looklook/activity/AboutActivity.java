package com.cmmobi.looklook.activity;


import android.content.Intent;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.os.Message;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import cn.zipper.framwork.core.ZActivity;

import com.cmmobi.looklook.R;
import com.cmmobi.looklook.common.utils.CmmobiClickAgentWrapper;

public class AboutActivity extends ZActivity{

	private ImageButton about_back_btn;
	private TextView version_name;
	
	public static final String APK_VERSION = "com.cmmobi.looklook";
	private static final String TAG = "AboutActivity";
	@Override
	public boolean handleMessage(Message msg) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.about_back_btn:
			AboutActivity.this.finish();
			break;

		default:
			break;
		}
	}
	
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_about);
		about_back_btn = (ImageButton) findViewById(R.id.about_back_btn);
		about_back_btn.setOnClickListener(this);
		
		about_back_btn.setOnLongClickListener(new View.OnLongClickListener() {
			
			@Override
			public boolean onLongClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(AboutActivity.this,LookLookActivity.class);
				intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(intent);
				AboutActivity.this.finish();
				return false;
			}
		});
		
		version_name = (TextView) findViewById(R.id.about_version_code);
		
		try {
			version_name.setText("版本  " + this.getPackageManager().getPackageInfo(
					APK_VERSION, 0).versionName);
		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		//AccountInfo accountInfo = AccountInfo.getInstance(ActiveAccount.getInstance(this).getLookLookID());

	}
	
	@Override
	public void onResume() {
		super.onResume();
		CmmobiClickAgentWrapper.onResume(this);
	}

	@Override
	public void onPause() {
		super.onPause();
		CmmobiClickAgentWrapper.onPause(this);
	}
	

	@Override
	public void onStop(){
		super.onStop();
		CmmobiClickAgentWrapper.onStop(this);
	}

}
