package com.etoc.weflowdemo.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.etoc.weflowdemo.R;
import com.etoc.weflowdemo.dialog.PromptDialog;
import com.etoc.weflowdemo.util.StringUtils;
import com.etoc.weflowdemo.util.TickDownHelper;

public class LoginActivity extends TitleRootActivity {
	
	private TextView tvValidCode = null;
	private EditText etPhone = null;
	private EditText etValidCode = null;
	private TextView tvLogin = null;
	private TickDownHelper tickDown = null;
	private boolean hasGetValidCode = false;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		initViews();
		
	}
	
	public void initViews() {
		hideLeftButton();
		hideRightButton();
		setTitleText("微流量");
		
		etPhone = (EditText) findViewById(R.id.et_phone);
		etValidCode = (EditText) findViewById(R.id.et_valid);
		tvValidCode = (TextView) findViewById(R.id.tv_valid_code);
		tvLogin = (TextView) findViewById(R.id.tv_login);
		
		tvValidCode.setOnClickListener(this);
		tvLogin.setOnClickListener(this);
		tickDown = new TickDownHelper(handler);
	}
	

	@Override
	public boolean handleMessage(Message msg) {
		// TODO Auto-generated method stub
		switch (msg.what) {
		case TickDownHelper.HANDLER_FLAG_TICK_DOWN:
			Integer sec = (Integer)msg.obj;
			tvValidCode.setText("重新发送(" + sec + ")");
			break;
		case TickDownHelper.HANDLER_FLAG_TICK_STOP:
			hasGetValidCode = false;
			Integer secStop = (Integer)msg.obj;
			tvValidCode.setEnabled(true);
			tvValidCode.setText("获取验证码");
			break;
		}
		return false;
	}
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		tickDown.stop(0);
		super.onDestroy();
	}
	
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.tv_valid_code:
			if (StringUtils.isEmpty(etPhone.getText().toString())) {
				PromptDialog.Dialog(this, "温馨提示", "请填写手机号", "确定");
			} else if (PromptDialog.checkPhoneNum(etPhone.getText().toString())) {
				hasGetValidCode = true;
				tvValidCode.setEnabled(false);
				tvValidCode.setText("重新发送(60)");
				tickDown.start(60);
			} else {
				PromptDialog.Dialog(this, "温馨提示", "手机号格式错误", "确定");
			}
			
			break;
		case R.id.tv_login:
			if (StringUtils.isEmpty(etPhone.getText().toString())) {
				PromptDialog.Dialog(this, "温馨提示", "请填写手机号", "确定");
			} else if (StringUtils.isEmpty(etValidCode.getText().toString())) {
				PromptDialog.Dialog(this, "温馨提示", "请输入验证码", "确定");
			} else {
				finish();
				startActivity(new Intent(this, HomePageActivity.class));
			}
			break;
		}
		super.onClick(v);
	}

	@Override
	public int subContentViewId() {
		// TODO Auto-generated method stub
		return R.layout.activity_login;
	}

}
