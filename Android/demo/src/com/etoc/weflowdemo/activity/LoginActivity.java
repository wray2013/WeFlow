package com.etoc.weflowdemo.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.etoc.weflowdemo.R;
import com.etoc.weflowdemo.dialog.PromptDialog;
import com.etoc.weflowdemo.net.GsonResponseObject.loginResponse;
import com.etoc.weflowdemo.net.GsonResponseObject.sendSMSResponse;
import com.etoc.weflowdemo.net.Requester;
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
			
		case Requester.RESPONSE_TYPE_LOGIN:
			loginResponse loginresponse = (loginResponse) msg.obj;
			if(loginresponse != null) {
				if(loginresponse.status == null || loginresponse.status.equals("0")) {
					PromptDialog.Alert(LoginActivity.class, "登录成功");
					finish();
					startActivity(new Intent(this, HomePageActivity.class));
				} else {
					PromptDialog.Alert(LoginActivity.class, "登录失败");
				}
			}
			PromptDialog.Alert(LoginActivity.class, "请求失败");
			break;
			
		case Requester.RESPONSE_TYPE_SENDSMS:
			sendSMSResponse smsresponse = (sendSMSResponse) msg.obj;
			if(smsresponse != null) {
				if(smsresponse.status == null || smsresponse.status.equals("0")) {
					PromptDialog.Alert(LoginActivity.class, "短信发送成功");
				} else {
					PromptDialog.Alert(LoginActivity.class, "短信发送失败");
				}
			}
			PromptDialog.Alert(LoginActivity.class, "请求失败");
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
				Requester.sendSMS(handler, etPhone.getText().toString());
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
				login();
//				finish();
//				startActivity(new Intent(this, HomePageActivity.class));
			}
			break;
		}
		super.onClick(v);
	}

	private void login() {
		String tel = etPhone.getText().toString();
		if(PromptDialog.checkPhoneNum(tel)) {
			Requester.login(handler, tel, etValidCode.getText().toString());
		} else {
			PromptDialog.Dialog(this, "温馨提示", "手机号格式错误", "确定");
		}
	}
	
	@Override
	public int subContentViewId() {
		// TODO Auto-generated method stub
		return R.layout.activity_login;
	}

}
