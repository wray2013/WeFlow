package com.etoc.weflowdemo.activity;

import com.etoc.weflowdemo.R;

import android.os.Bundle;
import android.os.Message;

public class PayPhoneBillActivity extends TitleRootActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		initViews();
	}
	
	private void initViews() {
		setTitleText("充话费");
		setRightButtonText("已购");
	}
	
	@Override
	public boolean handleMessage(Message msg) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public int subContentViewId() {
		// TODO Auto-generated method stub
		return R.layout.activity_pay_phone_bill;
	}

}
