package com.etoc.weflow.fragment;

import com.etoc.weflow.R;
import com.etoc.weflow.adapter.FastScrollAdapter;
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

public class DiscoveryFragment extends XFragment<Object> implements OnClickListener, OnRefreshListener2<ListView> {

	private final String TAG = "DiscoveryFragment";
	
	private DisplayMetrics dm = new DisplayMetrics();
	
	//UI Component
	private PullToRefreshListView mPullRefreshListView;
	private ListView mListView;
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
		
		mPullRefreshListView = (PullToRefreshListView) view.findViewById(R.id.xlv_discovery_list);
		mPullRefreshListView.setShowIndicator(false);
		mPullRefreshListView.setOnRefreshListener(this);
		
		mListView = mPullRefreshListView.getRefreshableView();
		initializeAdapter();
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

	private void initializeAdapter() {
//		mListView.setAdapter(new FastScrollAdapter(getActivity(), android.R.layout.simple_list_item_1, android.R.id.text1));
    }
	
	@Override
	public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
		// TODO Auto-generated method stub
		handler.postDelayed(new Runnable() {
			@Override
			public void run() {
				mPullRefreshListView.onRefreshComplete();
			}
		}, 500);
	}

	@Override
	public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
		// TODO Auto-generated method stub
		handler.postDelayed(new Runnable() {
			@Override
			public void run() {
				mPullRefreshListView.onRefreshComplete();
			}
		}, 500);
	}

}
