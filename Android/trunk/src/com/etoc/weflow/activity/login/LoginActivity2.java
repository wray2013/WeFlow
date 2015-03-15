package com.etoc.weflow.activity.login;

import com.etoc.weflow.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class LoginActivity2 extends Activity implements OnClickListener {
	
	private final String TAG = "LoginActivity";
	
	private DisplayMetrics dm = new DisplayMetrics();
	
	private TextView tvForgetPass;
	private Button btnRegister;
	
	private RelativeLayout rlBack;
	
	private Activity activity;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		dm = getResources().getDisplayMetrics();
		activity = this;
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		
		setContentView(R.layout.activity_login2);
		
		tvForgetPass = (TextView) findViewById(R.id.tv_login_pass_forget);
		tvForgetPass.setOnClickListener(this);
		
		btnRegister = (Button) findViewById(R.id.btn_register);
		btnRegister.setOnClickListener(this);
		
		rlBack = (RelativeLayout) findViewById(R.id.rl_login_title);
		rlBack.setOnClickListener(this);
		
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch(v.getId()) {
		case R.id.tv_login_pass_forget:
			Intent iforget = new Intent(this, RegisterActivity.class);
			iforget.putExtra("jumptype", RegisterActivity.JUMP_TYPE_PASSFORGET);
			startActivity(iforget);
			overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
			break;
		case R.id.btn_register:
			Intent iregister = new Intent(this, RegisterActivity.class);
			iregister.putExtra("jumptype", RegisterActivity.JUMP_TYPE_REGISTER);
			startActivity(iregister);
			overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
			break;
		case R.id.rl_login_title:
			finish();
			overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
			break;
		}
	}
}
