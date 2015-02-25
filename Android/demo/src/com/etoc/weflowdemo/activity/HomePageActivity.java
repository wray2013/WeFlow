package com.etoc.weflowdemo.activity;

import com.etoc.weflowdemo.R;
import com.etoc.weflowdemo.view.MagicTextView;

import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.view.View;
import android.widget.RelativeLayout;

public class HomePageActivity extends TitleRootActivity {

	//UI Component
	private MagicTextView mtvFlow;
	private RelativeLayout rlMakeFlow;
	
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
		
		mtvFlow = (MagicTextView) findViewById(R.id.tv_flow);
		mtvFlow.showNumberWithAnimation(98.5f, 1000);
		
		rlMakeFlow = (RelativeLayout) findViewById(R.id.rl_make_flow);
		rlMakeFlow.setOnClickListener(this);
	}
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		if(mtvFlow != null) {
			mtvFlow.showNumberWithAnimation(98.5f, 1000);
		}
	}
	
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch(v.getId()) {
		case R.id.rl_make_flow:
			Intent i = new Intent(this, AdDetailActivity.class);
			startActivity(i);
			break;
		}
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
