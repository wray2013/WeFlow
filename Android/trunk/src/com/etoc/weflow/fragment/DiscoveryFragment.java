package com.etoc.weflow.fragment;

import com.etoc.weflow.R;
import com.etoc.weflow.adapter.FastScrollAdapter;
import com.etoc.weflow.utils.ViewUtils;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.ListView;

public class DiscoveryFragment extends XFragment<Object> implements OnClickListener {

	private final String TAG = "DiscoveryFragment";
	
	private DisplayMetrics dm = new DisplayMetrics();
	
	//UI Component
//	private PullToRefreshListView mPullRefreshListView;
//	private ListView mListView;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreateView(inflater, container, savedInstanceState);
		View v = inflater.inflate(R.layout.fragment_discovery, null);
		initView(v);
		return v;
	}
	
	private void initView(View view) {
		dm = getResources().getDisplayMetrics();
		
		ViewUtils.setHeight(view.findViewById(R.id.rl_top_layout), 202);
		ViewUtils.setSize(view.findViewById(R.id.iv_flow_gift), 168, 168);
		ViewUtils.setSize(view.findViewById(R.id.iv_flow_pkg), 168, 168);
		ViewUtils.setMarginLeft(view.findViewById(R.id.iv_flow_gift), 128);
		ViewUtils.setMarginRight(view.findViewById(R.id.iv_flow_pkg), 128);
		
		ViewUtils.setMarginTop(view.findViewById(R.id.sv_content), 48);
		ViewUtils.setMarginLeft(view.findViewById(R.id.rl_content), 32);
		ViewUtils.setMarginRight(view.findViewById(R.id.rl_content), 32);
		
		ViewUtils.setMarginTop(view.findViewById(R.id.tv_flow_gift), 50);
		ViewUtils.setMarginLeft(view.findViewById(R.id.tv_flow_gift), 40);
		ViewUtils.setTextSize(view.findViewById(R.id.tv_flow_gift),26);
		
		ViewUtils.setHeight(view.findViewById(R.id.iv_flow_gift_activity), 252);
		ViewUtils.setMarginTop(view.findViewById(R.id.iv_flow_gift_activity), 36); 
		ViewUtils.setMarginRight(view.findViewById(R.id.iv_flow_gift_activity), 40);
		
		ViewUtils.setMarginTop(view.findViewById(R.id.tv_flow_pkg), 50);
		ViewUtils.setTextSize(view.findViewById(R.id.tv_flow_pkg),26);
		
		ViewUtils.setHeight(view.findViewById(R.id.iv_flow_pkg_activity), 218);
		ViewUtils.setMarginTop(view.findViewById(R.id.iv_flow_pkg_activity), 36); 
		
		ViewUtils.setMarginTop(view.findViewById(R.id.view_stub), 60); 
		
	}
	
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch(v.getId()) {
		}
	}
	
	@Override
	public int getIndex() {
		return INDEX_DISCOVER;
	}

	@Override
	public void onShow() {
		// TODO Auto-generated method stub
		
	}
	
}
