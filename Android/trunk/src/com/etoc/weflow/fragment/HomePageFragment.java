package com.etoc.weflow.fragment;

import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ListView;

import com.etoc.weflow.R;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;

public class HomePageFragment extends XFragment<Object>/*TitleRootFragment*/implements OnClickListener, OnRefreshListener2<ListView> {

	private final String TAG = "HomePageFragment";
	
	private DisplayMetrics dm = new DisplayMetrics();
	
	//UI Component
	private PullToRefreshListView mPullRefreshListView;
	private ListView mListView;
	
	/*@Override
	public int subContentViewId() {
		// TODO Auto-generated method stub
		return R.layout.fragment_homepage;
	}*/
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreateView(inflater, container, savedInstanceState);
		View v = inflater.inflate(R.layout.fragment_homepage, null);
		initView(v);
		return v;
	}
	
	private void initView(View view) {
		dm = getResources().getDisplayMetrics();
		
		mPullRefreshListView = (PullToRefreshListView) view.findViewById(R.id.xlv_homepage_list);
		mPullRefreshListView.setShowIndicator(false);
		mPullRefreshListView.setOnRefreshListener(this);
		
		mListView = mPullRefreshListView.getRefreshableView();
	}
	
	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		Log.d(TAG, "onResume");
	}
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_title_left:
			
			break;
		}
	}

	@Override
	public int getIndex() {
		return INDEX_HOMEPAGE;
	}
	
	@Override
	public void onShow() {
		Log.d(TAG, "onShow IN!");
	}

	@Override
	public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
		// TODO Auto-generated method stub
		
	}

}
