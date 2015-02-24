package com.etoc.weflowdemo.activity;

import com.etoc.weflowdemo.R;

import android.os.Bundle;
import android.os.Message;

public class HomePageActivity extends TitleRootActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		initViews();
	}
	
	private void initViews() {
		setTitleText("微流量");
		setLeftButtonText("宝典");
		setRightButtonText("消息");
	}
	@Override
	public boolean handleMessage(Message arg0) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public int subContentViewId() {
		// TODO Auto-generated method stub
		return R.layout.activity_homepage;
	}

}
