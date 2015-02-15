package com.cmmobi.looklook.activity;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;

import com.baidu.mapapi.map.LocationData;
import com.cmmobi.looklook.R;
import com.cmmobi.looklook.common.gson.GsonResponse3.MyDiary;
import com.cmmobi.looklook.common.utils.CmmobiClickAgentWrapper;
import com.cmmobi.looklook.fragment.FootMarkFragment;
import com.cmmobi.looklook.info.location.MyLocationInfo;
import com.cmmobi.looklook.map.MapItemHelper.Item;

public class FootMarkActivity extends FragmentActivity implements
		OnClickListener {

	private LayoutInflater inflater;
	private FootMarkFragment nearByFragment;
	private ImageView backButton;

	private Handler frag_handler;
	// map and location
	public MyLocationInfo myLocInfo;
	public LocationData myLoc;

	public int nearby_video_pageno;
	public ArrayList<MyDiary> list_nearby_data;

	public boolean nearby_more = true;
	public Item[] nearby_objs;

	private String userID;
	
//	private long start_time;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_friends_near_by);

		backButton = (ImageView) findViewById(R.id.friends_back_btn);

		backButton.setOnClickListener(this);
		backButton.setOnLongClickListener(new View.OnLongClickListener() {
			
			@Override
			public boolean onLongClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(FootMarkActivity.this,LookLookActivity.class);
				intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(intent);
				FootMarkActivity.this.finish();
				return false;
			}
		});

		list_nearby_data = new ArrayList<MyDiary>();

		nearByFragment = new FootMarkFragment();

		userID = getIntent().getStringExtra("uid");
		
		nearByFragment.setUserID(userID);
		
		getSupportFragmentManager().beginTransaction()
				.replace(R.id.friend_near_by_main_ll, nearByFragment)
				.commit();

		// get geoCode
		myLocInfo = MyLocationInfo.getInstance(this);
		myLoc = myLocInfo.getLocation();
		/*
		// 模拟地址，北京天安门
		myLoc.longitude = 116.403299;
		myLoc.latitude = 39.914225;
		*/
		//进入我的足迹，埋点
		//2014-4-8
//		CmmobiClickAgentWrapper.onEvent(this, "foot_print", userID);
//		start_time = SystemClock.elapsedRealtime();
	}

	@Override
	public void onResume() {
		super.onResume();
		//2014-4-8
		CmmobiClickAgentWrapper.onResume(this);
	}
	
	@Override
	public void onPause() {
		super.onPause();
		//2014-4-8
		CmmobiClickAgentWrapper.onPause(this);
	}
	
	@Override
	public void onStop() {
		super.onStop();
		//2014-4-8
		CmmobiClickAgentWrapper.onStop(this);
//		CmmobiClickAgentWrapper.onEventEnd(this, "foot_print");
//		HashMap<String, String> hm = new HashMap<String, String>();
//		hm.put("label", userID);
//		long delta = SystemClock.elapsedRealtime()- start_time;
//		CmmobiClickAgentWrapper.onEventDuration(this, "foot_print", userID, delta);
//		Log.d("==WR==", "足迹埋点【userid:" + userID + " ; time:" + delta + "】");
	}
	
	public void setFragHandler(Handler handler) {
		this.frag_handler = handler;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.friends_back_btn:
			this.finish();
			break;
		default:
			break;
		}

	}
	
}
