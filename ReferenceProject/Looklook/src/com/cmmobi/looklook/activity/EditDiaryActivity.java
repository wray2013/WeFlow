package com.cmmobi.looklook.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.view.View;
import cn.zipper.framwork.core.ZActivity;

import com.cmmobi.looklook.common.gson.GsonResponse2.MyDiary;
import com.cmmobi.looklook.common.listview.pulltorefresh.DiaryListView;
import com.cmmobi.looklook.common.utils.CmmobiClickAgentWrapper;
import com.cmmobi.looklook.common.utils.UmengclickAgentWrapper;
import com.google.gson.Gson;

public class EditDiaryActivity extends ZActivity{

	private String diaryUUID;
	private String diaryString;
	private MyDiary myDiary;
	
	private final String TAG = this.getClass().getSimpleName();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		diaryUUID = getIntent().getStringExtra(
				DiaryDetailActivity.INTENT_ACTION_DIARY_UUID);
		diaryString = getIntent().getStringExtra(
				DiaryDetailActivity.INTENT_ACTION_DIARY_STRING);
		
		if (diaryString != null && diaryUUID != null) {
			myDiary = new Gson().fromJson(diaryString, MyDiary.class);
			Log.d(TAG, "diaryUUID=" + diaryUUID);
		} else {
			Log.e(TAG, "diaryUUID is null");
			Log.e(TAG, "diaryArray is null");
		}
		int type = 0;
		if (myDiary != null) {
			type = DiaryListView.getDiaryType(myDiary.attachs);
			Intent intent = new Intent();
			intent.putExtra(DiaryDetailActivity.INTENT_ACTION_DIARY_UUID, diaryUUID);
			intent.putExtra(DiaryDetailActivity.INTENT_ACTION_DIARY_STRING, diaryString);
			if ((type & 0x10000000) != 0) {
				intent.setClass(this, EditVideoDetailActivity.class);
				startActivity(intent);
			} else if ((type & 0x1000000) != 0) {
				intent.setClass(this, EditVoiceDetailActivity.class);
				startActivity(intent);
			} else if ((type & 0x100000) != 0) {
				intent.setClass(this, EditPhotoDetailActivity.class);
				startActivity(intent);
			} else if ((type & 0x100) != 0) {
				intent.setClass(this, EditVoiceDetailActivity.class);
				startActivity(intent);
			} else if ((type & 0x1) != 0) {
				intent.setClass(this, EditVoiceDetailActivity.class);
				startActivity(intent);
			}
		}
		finish();
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
	
	@Override
	public boolean handleMessage(Message arg0) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		
	}

}
