package com.cmmobi.looklook.activity;


import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.os.Message;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import cn.zipper.framwork.core.ZActivity;

import com.cmmobi.looklook.R;
import com.cmmobi.looklook.common.utils.CmmobiClickAgentWrapper;
import com.cmmobi.looklook.common.utils.UmengclickAgentWrapper;

public class AboutActivity extends ZActivity{

	private ImageButton about_back_btn;
	private TextView version_name;
	
	public static final String APK_VERSION = "com.cmmobi.looklook";
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
		
		version_name = (TextView) findViewById(R.id.about_version_code);
		
		try {
			version_name.setText(this.getPackageManager().getPackageInfo(
					APK_VERSION, 0).versionName);
		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Override
	public void onResume() {
		super.onResume();
		UmengclickAgentWrapper.onResume(this);
		CmmobiClickAgentWrapper.onResume(this);
	}

	@Override
	public void onPause() {
		super.onPause();
		UmengclickAgentWrapper.onPause(this);
		CmmobiClickAgentWrapper.onPause(this);
	}
	

	@Override
	public void onStop(){
		super.onStop();
		CmmobiClickAgentWrapper.onStop(this);
	}

}
