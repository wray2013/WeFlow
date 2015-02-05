package com.etoc.weflow.activity.login;

import com.etoc.weflow.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class RegisterActivity extends Activity implements Callback, OnClickListener {

	private final String TAG = "RegisterActivity";
	
	private DisplayMetrics dm = new DisplayMetrics();
	
	private Button btnSendSMS;
	private TextView tvTitle;
	private EditText etTel;
	
	private RelativeLayout rlSendSMS, rlVerifySMS;
	private RelativeLayout rlBack;
	
	private Activity activity;
	private Handler myHandler;
	
	private int jtype = JUMP_TYPE_REGISTER;
	
	public static final int JUMP_TYPE_REGISTER   = 0x1200001;
	public static final int JUMP_TYPE_PASSFORGET = 0x1200002;
	public static final int JUMP_TYPE_SEND_SMS   = 0x1200003;
	
	public static final int HANDLER_MSG_SEND_SMS_OK = 0x1300001;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		dm = getResources().getDisplayMetrics();
		activity = this;
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		
		setContentView(R.layout.activity_register);
		
		myHandler = new Handler(this);
		
		Intent i = getIntent();
		if(i != null) {
			jtype = i.getIntExtra("jumptype", JUMP_TYPE_REGISTER);
		}
		
		rlSendSMS = (RelativeLayout) findViewById(R.id.rl_register_forgot);
		rlVerifySMS = (RelativeLayout) findViewById(R.id.rl_verify_sms);
		rlBack = (RelativeLayout) findViewById(R.id.rl_register_title);
		rlBack.setOnClickListener(this);
		
		btnSendSMS = (Button) findViewById(R.id.btn_register);
		btnSendSMS.setOnClickListener(this);
		
		tvTitle = (TextView) findViewById(R.id.tv_title);
		
		etTel = (EditText) findViewById(R.id.et_account);
		
		switchView(jtype);
		
	}
	
	private void switchView(int type) {
		switch(type) {
		case JUMP_TYPE_REGISTER:
			tvTitle.setText("注册账号");
			rlSendSMS.setVisibility(View.VISIBLE);
			rlVerifySMS.setVisibility(View.GONE);
			break;
		case JUMP_TYPE_PASSFORGET:
			tvTitle.setText("忘记密码");
			rlSendSMS.setVisibility(View.VISIBLE);
			rlVerifySMS.setVisibility(View.GONE);
			break;
		case JUMP_TYPE_SEND_SMS:
			rlSendSMS.setVisibility(View.GONE);
			rlVerifySMS.setVisibility(View.VISIBLE);
			break;
		}
	}
	
	private void sendSMS(String phoneNum, int type) {
		//TODO:
		//
		
		myHandler.sendEmptyMessage(HANDLER_MSG_SEND_SMS_OK);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch(v.getId()){
		case R.id.btn_register:
			String tel = etTel.getText().toString();
			sendSMS(tel, jtype);
			break;
		case R.id.rl_register_title:
			finish();
			break;
		}
	}

	@Override
	public boolean handleMessage(Message msg) {
		// TODO Auto-generated method stub
		switch(msg.what) {
		case HANDLER_MSG_SEND_SMS_OK:
			switchView(JUMP_TYPE_SEND_SMS);
			break;
		}
		return false;
	}
}
