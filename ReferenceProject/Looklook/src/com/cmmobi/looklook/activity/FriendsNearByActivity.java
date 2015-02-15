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
import com.cmmobi.looklook.common.gson.GsonResponse2.MyDiary;
import com.cmmobi.looklook.common.utils.CmmobiClickAgentWrapper;
import com.cmmobi.looklook.common.utils.UmengclickAgentWrapper;
import com.cmmobi.looklook.fragment.NearByFragment;
import com.cmmobi.looklook.fragment.NearByListFragment;
import com.cmmobi.looklook.info.location.MyLocationInfo;
import com.cmmobi.looklook.map.MapItemHelper.Item;

public class FriendsNearByActivity extends FragmentActivity implements
		OnClickListener {

	private LayoutInflater inflater;
	private NearByListFragment nearByListFragment;
	private NearByFragment nearByFragment;
	private ImageView backButton;
	private ImageView changeButton;
	private ChangeType changeType;

	private Handler frag_handler;
	// map and location
	public MyLocationInfo myLocInfo;
	public LocationData myLoc;

	public int nearby_video_pageno;
	public ArrayList<MyDiary> list_nearby_data;

	public boolean nearby_more = true;
	public Item[] nearby_objs;

	public enum ChangeType {
		NEARBY_MAP, NEARBY_LIST
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_friends_near_by);

		// FriendsCircleFragment friendsCircleFragment = new
		// FriendsCircleFragment();
		// getSupportFragmentManager().beginTransaction()
		// .add(R.id.friend_main_ll, friendsCircleFragment).commit();

		backButton = (ImageView) findViewById(R.id.friends_back_btn);

		changeButton = (ImageView) findViewById(R.id.friends_qiehuan_btn);

		backButton.setOnClickListener(this);
		changeButton.setOnClickListener(this);

		list_nearby_data = new ArrayList<MyDiary>();

		changeType = ChangeType.NEARBY_MAP;

		onClickChanged();

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
		UmengclickAgentWrapper.onResume(this);
		CmmobiClickAgentWrapper.onResume(this);
		CmmobiClickAgentWrapper.onEvent(this, "nearby");
		CmmobiClickAgentWrapper.onEventBegin(this, "nearby");
	}
	
	@Override
	public void onPause() {
		super.onPause();
		UmengclickAgentWrapper.onPause(this);
		CmmobiClickAgentWrapper.onPause(this);
	}
	
	@Override
	public void onStop() {
		super.onStop();
		CmmobiClickAgentWrapper.onStop(this);
		CmmobiClickAgentWrapper.onEventEnd(this, "nearby");
	}
	
	public void setFragHandler(Handler handler) {
		this.frag_handler = handler;
	}

	public void onClickChanged() {

		switch (changeType) {
		case NEARBY_MAP:

			nearByFragment = new NearByFragment();

			getSupportFragmentManager().beginTransaction()
					.replace(R.id.friend_near_by_main_ll, nearByFragment)
					.commit();

			changeButton
					.setBackgroundResource(R.drawable.btn_activity_homepage_pubu);
			changeType = ChangeType.NEARBY_LIST;

			break;
		case NEARBY_LIST:

			nearByListFragment = new NearByListFragment();
			getSupportFragmentManager()
					.beginTransaction()
					.replace(R.id.friend_near_by_main_ll, nearByListFragment)
					.commit();

			changeButton
					.setBackgroundResource(R.drawable.btn_activity_homepage_map);
			changeType = ChangeType.NEARBY_MAP;
			break;
		default:
			break;
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.friends_back_btn:
			this.finish();
			break;
		case R.id.friends_qiehuan_btn:
			onClickChanged();
			break;

		default:
			break;
		}

	}
}
