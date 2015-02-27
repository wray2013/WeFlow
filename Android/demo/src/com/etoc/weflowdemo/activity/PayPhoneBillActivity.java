package com.etoc.weflowdemo.activity;

import android.os.Bundle;
import android.os.Message;
import android.view.View;
import android.widget.TextView;

import com.etoc.weflowdemo.R;
import com.etoc.weflowdemo.dialog.PromptDialog;

public class PayPhoneBillActivity extends TitleRootActivity {

	TextView [] textViewArray = null;
	TextView tvPhoneNum = null;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		initViews();
	}
	
	private void initViews() {
		setTitleText("充话费");
		setRightButtonText("已购");
		
		tvPhoneNum = (TextView) findViewById(R.id.tv_account_phone);
		String phoneNum = getIntent().getStringExtra("phone");
		if (phoneNum != null) {
			tvPhoneNum.setText(phoneNum);
		}
		textViewArray = new TextView[6];
		textViewArray[0] = (TextView) findViewById(R.id.tv_pay_1);
		textViewArray[1] = (TextView) findViewById(R.id.tv_pay_2);
		textViewArray[2] = (TextView) findViewById(R.id.tv_pay_3);
		textViewArray[3] = (TextView) findViewById(R.id.tv_pay_4);
		textViewArray[4] = (TextView) findViewById(R.id.tv_pay_5);
		textViewArray[5] = (TextView) findViewById(R.id.tv_pay_6);
		
		for (int i = 0;i < 6; i++) {
			textViewArray[i].setOnClickListener(this);
			textViewArray[i].setTag(false);
		}
		
		findViewById(R.id.tv_pay_btn).setOnClickListener(this);
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
	
	private void changeStatus(View view) {
		for (int i = 0;i < 6;i++) {
			TextView tv = textViewArray[i];
			if (tv.getId() == view.getId()) {
				Boolean flag = (Boolean) view.getTag();
				if (flag) {
					tv.setBackgroundResource(R.drawable.bg_bill_item);
				} else {
					tv.setBackgroundResource(R.drawable.bg_bill_item_checked);
				}
				view.setTag(!flag);
			} else {
				tv.setBackgroundResource(R.drawable.bg_bill_item);
				tv.setTag(false);
			}
		}
	}
	
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.tv_pay_1:
		case R.id.tv_pay_2:
		case R.id.tv_pay_3:
		case R.id.tv_pay_4:
		case R.id.tv_pay_5:
		case R.id.tv_pay_6:
			changeStatus(v);
			break;
		case R.id.tv_pay_btn:
			PromptDialog.Alert(PayPhoneBillActivity.class, "请求失败");
			finish();
		default:
			break;
		}
		super.onClick(v);
	}

}
