package com.etoc.weflow.fragment;


import com.etoc.weflow.R;

import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;

public class MenuFragment extends XFragment<Object> implements OnClickListener {

	private final String TAG = "MenuFragment";
	private DisplayMetrics dm = new DisplayMetrics();
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup vg,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_menu, null);
		if (view != null) {
			initViews(view);
		}
		return view;
	}
	
	private void initViews(View view) {
		
	}
	
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		
	}

}
