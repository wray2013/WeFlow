package com.cmmobi.looklook.activity.login;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.cmmobi.looklook.R;
import com.cmmobi.looklook.activity.TitleRootActivity;

public class MicShareUserEntryActivity extends TitleRootActivity {

	@Override
	public int subContentViewId() {
		return R.layout.activity_micshare_user_entry;
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		hideTitlebar();
		
		findViewById(R.id.btn_register).setOnClickListener(this);
		findViewById(R.id.btn_login).setOnClickListener(this);
		
	}
	
	
	@Override
	public void onClick(View v) {
		Intent intent = null;
		switch (v.getId()) {
		case R.id.btn_register:
			intent = new Intent(this,RegisterMicShareActivity.class);
			break;
		case R.id.btn_login:
			intent = new Intent(this,LoginMicShareActivity.class);
			break;
		default:
			break;
		}
		if(intent != null){
			startActivity(intent);
		}
		super.onClick(v);
	}
}
