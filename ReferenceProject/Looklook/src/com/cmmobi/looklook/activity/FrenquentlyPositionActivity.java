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
import com.cmmobi.looklook.common.utils.UmengclickAgentWrapper;
import com.cmmobi.looklook.info.profile.CommonInfo;

public class FrenquentlyPositionActivity extends ZActivity{

	private List<String> posList = new ArrayList<String>();
	private ListView listView;
	private ImageView ivBack;
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
		Log.d("EditMediaPositionFragment","FrenquentlyPositionActivity onCreate size = " + mAdapter.getCount());
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
	
	private void initPosList() {
		ArrayList<String> freqPosList = CommonInfo.getInstance().frequentpos;
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
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		switch(arg0.getId()) {
		case R.id.rl_list_item_position:
			if (preImgView != null) {
				preImgView.setImageResource(R.drawable.xuanzhong_1);
			}
			ImageView imgView = (ImageView) arg0.findViewById(R.id.iv_edit_position_selected);
			imgView.setImageResource(R.drawable.xuanzhong_2);
			
			preImgView = imgView;
			
			if (arg0.getTag() != null) {
			    PositionListAdapter.ViewHolder holder = (PositionListAdapter.ViewHolder)arg0.getTag();
			    mAdapter.checkedPosition = holder.position;
			    Log.d(TAG,"EditMediaPosition rl_list_item_position position = " + holder.position);
			}
			break;
		case R.id.iv_back:
			Log.d("EditMediaPositionFragment","iv_back size = " + mAdapter.getCount());
			Intent intent = new Intent();
			intent.putExtra("position", mAdapter.getCheckPositionStr());
			setResult(RESULT_OK,intent);
			finish();
			break;
		}
	}

}
