package com.etoc.weflow.activity;

import com.etoc.weflow.R;
import com.etoc.weflow.utils.ViewUtils;

import android.os.Bundle;
import android.os.Message;
import android.widget.ImageView;

public class AboutActivity extends TitleRootActivity {

	private ImageView ivLogo;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		hideRightButton();
		setTitleText("关于");
		
		initViews();
	}
	
	private void initViews() {
		// TODO Auto-generated method stub
		ivLogo = (ImageView) findViewById(R.id.iv_logo);
		ViewUtils.setWidth(ivLogo, 220);
		ViewUtils.setHeight(ivLogo, 220);
		
		ViewUtils.setTextSize(findViewById(R.id.tv_name), 36);
		ViewUtils.setTextSize(findViewById(R.id.tv_version_hint), 32);
		ViewUtils.setTextSize(findViewById(R.id.tv_version), 32);
	}

	@Override
	protected int graviteType() {
		// TODO Auto-generated method stub
		return GRAVITE_LEFT;
	}
	
	@Override
	public boolean handleMessage(Message msg) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public int subContentViewId() {
		// TODO Auto-generated method stub
		return R.layout.activity_about;
	}

}
