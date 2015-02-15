package com.cmmobi.looklook.activity;

import java.util.ArrayList;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;

import com.baidu.mapapi.map.LocationData;
import com.cmmobi.looklook.R;
import com.cmmobi.looklook.common.gson.GsonResponse3.MyDiary;
import com.cmmobi.looklook.fragment.NearByFragment;
import com.cmmobi.looklook.info.location.MyLocationInfo;
import com.cmmobi.looklook.map.MapItemHelper.Item;

public class FriendsNearByActivity extends FragmentActivity implements
		OnClickListener {

	private LayoutInflater inflater;
	private NearByFragment nearByFragment;
	private ImageView backButton;

	private Handler frag_handler;
	// map and location
	public MyLocationInfo myLocInfo;
	public LocationData myLoc;

	public int nearby_video_pageno;
	public ArrayList<MyDiary> list_nearby_data;

	public boolean nearby_more = true;
	public Item[] nearby_objs;

	private String uerID;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_friends_near_by);

		backButton = (ImageView) findViewById(R.id.friends_back_btn);

		backButton.setOnClickListener(this);

		list_nearby_data = new ArrayList<MyDiary>();

		nearByFragment = new NearByFragment();

		uerID = getIntent().getStringExtra("uid");
		
		nearByFragment.setUserID(uerID);
		
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
	}

	@Override
	public void onResume() {
		super.onResume();
//		CmmobiClickAgentWrapper.onResume(this);
//		CmmobiClickAgentWrapper.onEvent(this, "foot_print");
//		CmmobiClickAgentWrapper.onEventBegin(this, "foot_print");
	}
	
	@Override
	public void onPause() {
		super.onPause();
//		CmmobiClickAgentWrapper.onPause(this);
	}
	
	@Override
	public void onStop() {
		super.onStop();
//		CmmobiClickAgentWrapper.onStop(this);
//		CmmobiClickAgentWrapper.onEventEnd(this, "foot_print");
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
