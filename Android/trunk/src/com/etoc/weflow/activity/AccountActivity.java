package com.etoc.weflow.activity;

import com.etoc.weflow.R;
import com.etoc.weflow.utils.ViewUtils;

import android.os.Bundle;
import android.os.Message;
import android.view.View;
import android.widget.RelativeLayout;

public class AccountActivity extends TitleRootActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		initViews();
	}
	
	private void initViews() {
		
		setTitleText("个人设置");
		hideRightButton();
		RelativeLayout rlPassword = (RelativeLayout) findViewById(R.id.rl_account_password);
		RelativeLayout rlLoginOut = (RelativeLayout) findViewById(R.id.rl_login_out);
		
		rlPassword.setOnClickListener(this);
		rlLoginOut.setOnClickListener(this);
		
		ViewUtils.setHeight(findViewById(R.id.rl_account_phone), 112);
		ViewUtils.setHeight(findViewById(R.id.rl_account_password), 112);
		ViewUtils.setHeight(findViewById(R.id.rl_login_out), 112);
		
		ViewUtils.setMarginLeft(findViewById(R.id.tv_phone_label), 32);
		ViewUtils.setMarginRight(findViewById(R.id.rl_account_phone), 32);
		ViewUtils.setMarginRight(findViewById(R.id.view_password_flag), 32);
		ViewUtils.setMarginLeft(findViewById(R.id.tv_password_label), 32);
		ViewUtils.setMarginLeft(findViewById(R.id.tv_login_out), 32);
		ViewUtils.setMarginTop(findViewById(R.id.rl_login_out), 32);
		ViewUtils.setMarginLeft(findViewById(R.id.view_line), 32);
		
		ViewUtils.setTextSize(findViewById(R.id.tv_phone_label), 30);
		ViewUtils.setTextSize(findViewById(R.id.tv_phone_num), 30);
		ViewUtils.setTextSize(findViewById(R.id.tv_password_label), 30);
		ViewUtils.setTextSize(findViewById(R.id.tv_login_out), 30);
	}
	
	@Override
	public boolean handleMessage(Message msg) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public int subContentViewId() {
		// TODO Auto-generated method stub
		return R.layout.activity_account;
	}
	
	@Override
	protected int graviteType() {
		// TODO Auto-generated method stub
		return GRAVITE_LEFT;
	}
	
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.rl_account_password:
			break;
		case R.id.rl_login_out:
			break;
		}
		super.onClick(v);
	}

}
