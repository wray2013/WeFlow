package com.etoc.weflow.fragment;

import com.etoc.weflow.R;

import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;

public class MyselfFragment extends XFragment<Object>/*TitleRootFragment*/implements OnClickListener {

	private final String TAG = "MyselfFragment";
	
	private DisplayMetrics dm = new DisplayMetrics();
	
//	private ScratchTextView stvCard;
	/*@Override
	public int subContentViewId() {
		// TODO Auto-generated method stub
		return R.layout.fragment_flowbank;
	}*/
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreateView(inflater, container, savedInstanceState);
		View v = inflater.inflate(R.layout.fragment_myself, null);
		initView(v);
		return v;
	}
	
	private void initView(View view) {
		dm = getResources().getDisplayMetrics();

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
		return INDEX_ME;
	}

	@Override
	public void onShow() {
		Log.d(TAG, "onShow IN!");
	}
	
}
