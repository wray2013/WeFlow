package com.cmmobi.looklook.activity;

import java.util.ArrayList;

import android.os.Bundle;
import android.os.Message;
import android.view.View;
import cn.zipper.framwork.core.ZActivity;

import com.cmmobi.looklook.R;
import com.cmmobi.looklook.common.adapter.SinaFriendListAdapter;
import com.cmmobi.looklook.common.gson.WeiboResponse.sinaUser;
import com.cmmobi.looklook.common.listview.pulltorefresh.PullToRefreshBase.OnRefreshListener;
import com.cmmobi.looklook.common.listview.pulltorefresh.PullToRefreshListView;
import com.cmmobi.looklook.common.utils.CmmobiClickAgentWrapper;
import com.cmmobi.looklook.common.utils.UmengclickAgentWrapper;

public class AttentionActivity extends ZActivity implements OnRefreshListener{
	private PullToRefreshListView lv_attention_friends;
	private SinaFriendListAdapter list_friend_adapter;
	private ArrayList<sinaUser> list_friend_data;
	
	@Override
	public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends_weibo_renren);
        
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
	public boolean handleMessage(Message msg) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onRefresh() {
		// TODO Auto-generated method stub
		
	}

}
