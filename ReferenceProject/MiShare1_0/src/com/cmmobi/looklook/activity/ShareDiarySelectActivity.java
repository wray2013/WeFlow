package com.cmmobi.looklook.activity;

import java.util.ArrayList;
import java.util.Collections;

import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import cn.zipper.framwork.core.ZActivity;

import com.cmmobi.looklook.R;
import com.cmmobi.looklook.common.adapter.ShareDiarySelectAdapter;
import com.cmmobi.looklook.common.adapter.ShareDiarySelectAdapter.ItemClick;
import com.cmmobi.looklook.common.gson.GsonResponse3.MyDiary;
import com.cmmobi.looklook.common.view.pulltorefresh.PullToRefreshBase;
import com.cmmobi.looklook.common.view.pulltorefresh.PullToRefreshBase.OnRefreshListener2;
import com.cmmobi.looklook.fragment.MyZoneFragment;
import com.cmmobi.looklook.fragment.VShareFragment;
import com.cmmobi.looklook.fragment.MyZoneFragment.MyZoneItem;
import com.cmmobi.looklook.info.profile.DiaryManager;
import com.google.gson.Gson;

/**
 * Copyright (C) ......
 * All rights reserved.
 * @package com.cmmobi.looklook.activity
 * @filename ChoiceDiaryListActivity.java
 * @summary 分享界面日记选择，参考VShareDiaryListActivity类
 * @author Lanhai
 * @date 2013-12-4
 * @version 1.0
 */
public class ShareDiarySelectActivity extends ZActivity implements OnRefreshListener2<ListView>, OnLongClickListener,ItemClick{

	private ImageView iv_back;
	public TextView tv_commit;
	private ListView lv_DiaryList;
	private ShareDiarySelectAdapter diaryListAdapter;
	private ArrayList<MyZoneItem> myZoneItems=new ArrayList<MyZoneFragment.MyZoneItem>();
	protected DiaryManager diaryManager;
	private boolean isFromVshare = false;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_vshare_diarylist);
		//inflater = LayoutInflater.from(this);
		iv_back = (ImageView) findViewById(R.id.iv_back);
		iv_back.setOnClickListener(this);
		iv_back.setOnLongClickListener(this);
		tv_commit = (TextView) findViewById(R.id.tv_commit);
		tv_commit.setOnClickListener(this);
		
		lv_DiaryList = (ListView) findViewById(R.id.lv_diaries);
		lv_DiaryList.setStackFromBottom(false);
		
		TextView tv = (TextView) findViewById(R.id.tv_title);
		tv.setText("我的内容");
		
		diaryManager = DiaryManager.getInstance();
		loadLocalData();
		diaryListAdapter = new ShareDiarySelectAdapter(this, myZoneItems);
		diaryListAdapter.setSelected(ShareDiaryActivity.diaryGroup);
		lv_DiaryList.setAdapter(diaryListAdapter);
		
		isFromVshare = getIntent().getBooleanExtra(VShareFragment.IS_FROM_VSHARE, false);
		
		if (isFromVshare) {
			diaryListAdapter.setItemClick(this);
			tv_commit.setEnabled(false);
		}
	}

	//加载本地数据
	private boolean loadLocalData(){
		myZoneItems.clear();
		ArrayList<MyZoneItem> localList = diaryManager.getShareZoneItems(0);
		myZoneItems.addAll(localList);
		
		return myZoneItems.size()>0;
	}
	
	@Override
	public boolean handleMessage(Message msg) {
		return false;
	}


	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.iv_back:
			this.setResult(RESULT_CANCELED);
			this.finish();
			break;
		case R.id.tv_commit:
			if (isFromVshare) {
				ShareDiaryActivity.diaryGroup.clear();
			}
			if(diaryListAdapter.getCheckedDiary()!=null){
				Intent ret = new Intent();
				ArrayList<MyDiary> diarys = diaryListAdapter.getCheckedDiary();
				ShareDiaryActivity.diaryGroup.addAll(diarys);
				this.setResult(RESULT_OK, ret);
			}
			if (isFromVshare) {
				Intent intent = new Intent(this, ShareLookLookFriendsActivity.class);
				intent.putExtra(VShareFragment.IS_FROM_VSHARE, true);
				startActivityForResult(intent, ShareDiaryActivity.REQUEST_CODE_USER);
			} else {
				this.finish();
			}
			break;
		default:
			break;
		}
	}


	@Override
	public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
		
	}


	@Override
	public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
		
	}

	@Override
	public boolean onLongClick(View v) {
		switch(v.getId()) {
		case R.id.iv_back:
			Intent intent = new Intent(this,LookLookActivity.class);
			intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(intent);
			finish();
			break;
		}
		
		return false;
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		switch(requestCode)
		{
		case ShareDiaryActivity.REQUEST_CODE_USER:
			if(resultCode == RESULT_OK) {
				finish();
			}
			break;
		}
	}

	@Override
	public void itemClick() {
		// TODO Auto-generated method stub
		if (isFromVshare) {
			if(diaryListAdapter.getCheckedDiary()!=null && diaryListAdapter.getCheckedDiary().size() > 0) {
				tv_commit.setEnabled(true);
			} else {
				tv_commit.setEnabled(false);
			}
		}
	}
	

	
}
