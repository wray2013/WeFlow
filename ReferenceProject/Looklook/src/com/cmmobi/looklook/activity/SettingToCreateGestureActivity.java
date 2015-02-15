package com.cmmobi.looklook.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import cn.zipper.framwork.core.ZActivity;

import com.cmmobi.looklook.R;
import com.cmmobi.looklook.common.utils.CmmobiClickAgentWrapper;
import com.cmmobi.looklook.common.utils.UmengclickAgentWrapper;
import com.cmmobi.looklook.info.profile.AccountInfo;
import com.cmmobi.looklook.info.profile.ActiveAccount;
import com.cmmobi.looklook.info.profile.LoginSettingManager;

/**
 * 当用户没有创建过手势密码时，提示用户去创建手势密码
 * 
 * @author youtian
 * 
 */
public class SettingToCreateGestureActivity extends ZActivity {

	private Button btn_toCreate;// 立即创建
	private ImageView iv_back;
	private LoginSettingManager lsm;
	
	private final int REQUEST_BIND_PHONE = 0x12341;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setting_to_create_gesture);

		btn_toCreate = (Button) findViewById(R.id.btn_create);
		btn_toCreate.setOnClickListener(this);
		
		iv_back = (ImageView) findViewById(R.id.iv_back);
		iv_back.setOnClickListener(this);	
		
		String uid = ActiveAccount.getInstance(this).getUID();
		AccountInfo accountInfo = AccountInfo.getInstance(uid);
		if(accountInfo != null){
			lsm = accountInfo.setmanager;
		}
		
		if(null == lsm.getBinding_type(LoginSettingManager.BINDING_TYPE_PHONE, LoginSettingManager.BINDING_INFO_POINTLESS)){
			Intent intent = new Intent(this, SettingBindPhoneActivity.class);
			startActivityForResult(intent, REQUEST_BIND_PHONE);
		}
		
		CmmobiClickAgentWrapper.onEvent(this, "ma_cr_safe");
	}
	
	@Override
	public void onResume() {
		super.onResume();
		UmengclickAgentWrapper.onResume(this);
		CmmobiClickAgentWrapper.onResume(this);
	}
	
	@Override
	public void onPause() {
		super.onPause();
		UmengclickAgentWrapper.onPause(this);
		CmmobiClickAgentWrapper.onPause(this);
	}
	
	@Override
	public void onStop() {
		super.onStop();
		CmmobiClickAgentWrapper.onStop(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_create:
			Intent intent = new Intent(this, SettingGesturePwdActivity.class);
			intent.putExtra("count", 1);
			startActivity(intent);
			this.finish();
			break;
		case R.id.iv_back:
			this.onBackPressed();
			break;
		}
	}

	@Override
	public boolean handleMessage(Message msg) {
		// TODO Auto-generated method stub
		return false;
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		//super.onActivityResult(requestCode, resultCode, data);
		if(requestCode == REQUEST_BIND_PHONE && resultCode != RESULT_OK){
			this.finish();		
		}
	}
}
