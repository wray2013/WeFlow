package com.cmmobi.looklook.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageButton;
import android.widget.ListView;
import cn.zipper.framwork.core.ZApplication;

import com.cmmobi.looklook.R;
import com.cmmobi.looklook.common.adapter.ActivitiesAdapter;
import com.cmmobi.looklook.common.gson.GsonResponse2.activeListItem;
import com.cmmobi.looklook.common.gson.GsonResponse2.activeListResponse;
import com.cmmobi.looklook.common.gson.Requester2;
import com.cmmobi.looklook.common.utils.CmmobiClickAgentWrapper;
import com.cmmobi.looklook.common.utils.UmengclickAgentWrapper;
import com.cmmobi.looklook.info.profile.AccountInfo;
import com.cmmobi.looklook.info.profile.ActiveAccount;
import com.cmmobi.looklook.info.profile.ActivityListManager;

public class ActivitiesActivity extends FragmentActivity implements
		OnItemClickListener, OnClickListener, Callback {

	private ListView activitesList;
	private ImageButton backButton;

	private Handler handler;
	private ActivitiesAdapter activitiesAdapter;
	private ActivityListManager activityListManager;
	private String userID;
	private AccountInfo accountInfo;
	private activeListItem[] activeListItem;
	public String comeDiaryId;
	public String comeDiaryuuid;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_activities);

		
		comeDiaryId = getIntent().getStringExtra("comeDiaryId");
		comeDiaryuuid = getIntent().getStringExtra("comeDiaryuuid");
		
		userID = ActiveAccount.getInstance(ZApplication.getInstance()).getUID();
		accountInfo = AccountInfo.getInstance(userID);
		activityListManager = accountInfo.activityListManager;

		activitesList = (ListView) findViewById(R.id.activites_list);
		backButton = (ImageButton) findViewById(R.id.contacts_back_btn);

		activitiesAdapter = new ActivitiesAdapter(this);
		activitesList.setAdapter(activitiesAdapter);

		activitesList.setOnItemClickListener(this);

		backButton.setOnClickListener(this);

		handler = new Handler(this);
		
		loadLoacalData();
		Requester2.requestActiveList(handler, "0", "");
		
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

	private void loadLoacalData() {

		activeListItem = activityListManager.getCache();
		
		if (null != activeListItem && activeListItem.length > 0) {
			activitiesAdapter.setData(activeListItem);
			activitiesAdapter.notifyDataSetChanged();
		}
		// LinkedList<WrapUser> cache = activityListManager.getCache();
		// if (cache.size() > 0) {
		//
		// // diariesList.clear();
		// // partsList.clear();
		// wrapUserList.clear();
		// wrapUserList.addAll(cache);
		// friendsCircleContactsAdapter.setData(wrapUserList);
		// friendsCircleContactsAdapter.notifyDataSetChanged();

	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {

		Intent intent = new Intent(this, ActivitiesDetailActivity.class);
		intent.putExtra("activeItem", activeListItem[position]);
		if (null != comeDiaryId && comeDiaryId.length() > 0) {
			intent.putExtra("comeDiaryId", comeDiaryId);
		} else {
			intent.putExtra("comeDiaryId", "");
		}
		
		if (null != comeDiaryuuid && comeDiaryuuid.length() > 0) {
			intent.putExtra("comeDiaryuuid", comeDiaryuuid);
		} else {
			intent.putExtra("comeDiaryuuid", "");
		}
		startActivity(intent);
	}

	@Override
	public void onClick(View v) {

		this.finish();
	}

	@Override
	public boolean handleMessage(Message msg) {
		switch (msg.what) {
		case Requester2.RESPONSE_TYPE_GET_ACTIVELIST:
			if (msg.obj != null) {
				activeListResponse activeList = (activeListResponse) msg.obj;

				if ("0".equals(activeList.status)) {
					activeListItem = null;
					activeListItem = activeList.active;

					activitiesAdapter.setData(activeListItem);
					activitiesAdapter.notifyDataSetChanged();

					activityListManager.refreshCache(activeListItem);
				}
			}

			break;

		default:
			break;
		}
		return false;
	}

}
