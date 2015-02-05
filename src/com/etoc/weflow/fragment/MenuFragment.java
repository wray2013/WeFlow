package com.etoc.weflow.fragment;


import com.etoc.weflow.R;
import com.etoc.weflow.activity.CaptureActivity;
import com.etoc.weflow.activity.login.StartAccountActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.ImageView;

public class MenuFragment extends XFragment<Object> implements OnClickListener {

	private final String TAG = "MenuFragment";
	private DisplayMetrics dm = new DisplayMetrics();
	
	private ImageView ivAvatar;
	
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
		ivAvatar = (ImageView) view.findViewById(R.id.iv_avatar);
		ivAvatar.setOnClickListener(this);
	}
	
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch(v.getId()) {
		case R.id.iv_avatar:
			Intent i = new Intent(getActivity(), StartAccountActivity.class);
			startActivity(i);
			break;
		}
	}

	@Override
	public void onShow() {
		// TODO Auto-generated method stub
	}

}
