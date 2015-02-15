package com.cmmobi.looklook.activity;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import cn.zipper.framwork.core.ZActivity;

import com.cmmobi.looklook.R;
import com.cmmobi.looklook.common.adapter.PositionListAdapter;
import com.cmmobi.looklook.common.utils.CmmobiClickAgentWrapper;
import com.cmmobi.looklook.info.location.POIAddressInfo;
import com.cmmobi.looklook.info.profile.CommonInfo;

public class FrenquentlyPositionActivity extends ZActivity{

	private List<POIAddressInfo> posList = new ArrayList<POIAddressInfo>();
	private ListView listView;
	private ImageView ivBack;
	private ImageView ivDone;
	private ImageView preImgView = null;
	private PositionListAdapter mAdapter = null;
	private final String TAG = this.getClass().getSimpleName();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_frenquently_position);
		
		initPosList();
		listView = (ListView) findViewById(R.id.lv_pos_listview);
		mAdapter = new PositionListAdapter(this, R.layout.list_item_poi_position, R.id.tv_position, posList);
		mAdapter.setClickListener(this);
		listView.setAdapter(mAdapter);
		ivBack = (ImageView) findViewById(R.id.iv_back);
		ivBack.setOnClickListener(this);
		ivDone = (ImageView) findViewById(R.id.iv_edit_diary_save);
		ivDone.setOnClickListener(this);
		ivDone.setEnabled(false);
		Log.d("EditMediaPositionFragment","FrenquentlyPositionActivity onCreate size = " + mAdapter.getCount());
	}
	
	@Override
	public void onResume() {
		super.onResume();
		CmmobiClickAgentWrapper.onResume(this);
	}

	@Override
	public void onPause() {
		super.onPause();
		CmmobiClickAgentWrapper.onPause(this);
	}

	@Override
	public void onStop(){
		super.onStop();
		CmmobiClickAgentWrapper.onStop(this);
	}
	
	private void initPosList() {
		List<POIAddressInfo> freqPosList = CommonInfo.getInstance().frequentpos;
		Log.d("EditMediaPositionFragment","freqPosListSize = " + freqPosList.size());
		if (freqPosList != null && freqPosList.size() > 0) {
			posList.addAll(freqPosList);
		}
	}

	@Override
	public boolean handleMessage(Message arg0) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void onClick(View view) {
		// TODO Auto-generated method stub
		switch(view.getId()) {
		case R.id.rl_list_item_position:
			if (preImgView != null) {
				preImgView.setVisibility(View.GONE);
			}
			ImageView imgView = (ImageView) view.findViewById(R.id.iv_edit_position_selected);
			imgView.setVisibility(View.VISIBLE);
			
			preImgView = imgView;
			
			if (view.getTag() != null) {
			    PositionListAdapter.ViewHolder holder = (PositionListAdapter.ViewHolder)view.getTag();
			    mAdapter.checkedPosition = holder.position;
			    Log.d(TAG,"EditMediaPosition rl_list_item_position position = " + holder.position);
			}
			ivDone.setEnabled(true);
			mAdapter.notifyDataSetChanged();
			break;
		case R.id.iv_back:
			finish();
			break;
		case R.id.iv_edit_diary_save:
			POIAddressInfo posInfo = mAdapter.getCheckPositionStr();
			if (posInfo != null) {
				Intent intent = new Intent();
				intent.putExtra("position", posInfo.position);
				intent.putExtra("longitude", posInfo.longitude);
				intent.putExtra("latitude", posInfo.latitude);
				setResult(RESULT_OK,intent);
			}
			finish();
			break;
		}
	}

}
