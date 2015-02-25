package com.etoc.weflowdemo.activity;

import com.etoc.weflowdemo.R;

import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

public class HomePageActivity extends TitleRootActivity {

	private RelativeLayout makeFlowLayout = null;
	private RelativeLayout useFlowLayout = null;
	private RelativeLayout discoverLayout = null;
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
		
		makeFlowLayout = (RelativeLayout) findViewById(R.id.rl_make_flow);
		useFlowLayout = (RelativeLayout) findViewById(R.id.rl_use_flow);
		discoverLayout = (RelativeLayout) findViewById(R.id.rl_discover);
		
		makeFlowLayout.setOnClickListener(this);
		useFlowLayout.setOnClickListener(this);
		discoverLayout.setOnClickListener(this);
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
	
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.rl_discover:
			break;
		case R.id.rl_make_flow:
			startActivity(new Intent(this, AdvertActivity.class));
			break;
		case R.id.rl_use_flow:
			break;

		default:
			break;
		}
		super.onClick(v);
	}

}
