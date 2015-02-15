package com.cmmobi.looklook.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import cn.zipper.framwork.core.ZActivity;
import cn.zipper.framwork.core.ZDialog;

import com.cmmobi.looklook.R;
import com.cmmobi.looklook.common.constant.Constant;
import com.cmmobi.looklook.common.gson.GsonResponse3;
import com.cmmobi.looklook.common.gson.Requester3;
import com.cmmobi.looklook.common.utils.CmmobiClickAgentWrapper;
import com.cmmobi.looklook.info.profile.LoginSettingManager;
import com.cmmobi.looklook.prompt.Prompt;

/**
 * 绑定邮箱界面
 * 
 * @author youtian
 * 
 */
public class SettingBindEmailActivity extends ZActivity implements TextWatcher{
	
	private TextView tv_next;// 下一步
	private EditText et_email;
	private ImageView iv_back;
	private ImageView iv_x;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_settings_bind_email);
		tv_next = (TextView) findViewById(R.id.tv_next);
		tv_next.setOnClickListener(this);
		
		et_email = (EditText) findViewById(R.id.et_email);
		
		iv_back = (ImageView) findViewById(R.id.iv_back);
		iv_back.setOnClickListener(this);
		iv_back.setOnLongClickListener(new View.OnLongClickListener() {
			
			@Override
			public boolean onLongClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(SettingBindEmailActivity.this,LookLookActivity.class);
				intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(intent);
				SettingBindEmailActivity.this.finish();
				return false;
			}
		});
		
		iv_x = (ImageView) findViewById(R.id.iv_x);
		iv_x.setOnClickListener(this);
	}
	
	
	@Override
	public void onResume() {
		super.onResume();
		CmmobiClickAgentWrapper.onResume(this);
	}
	
	@Override
	public void onPause() {
		super.onPause();
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
		case R.id.tv_next:
			String email = et_email.getText().toString().trim();
			ZDialog.show(R.layout.progressdialog, false, true, this);
			Requester3.bindAccount(handler, LoginSettingManager.BINDING_TYPE_EMAIL, email, LoginSettingManager.BINDING_INFO_POINTLESS, LoginSettingManager.BINDING_INFO_POINTLESS, LoginSettingManager.BINDING_INFO_POINTLESS, LoginSettingManager.BINDING_INFO_POINTLESS, LoginSettingManager.BINDING_INFO_POINTLESS, LoginSettingManager.BINDING_INFO_POINTLESS, LoginSettingManager.BINDING_INFO_POINTLESS, LoginSettingManager.BINDING_INFO_POINTLESS, LoginSettingManager.BINDING_INFO_POINTLESS, LoginSettingManager.BINDING_INFO_POINTLESS, LoginSettingManager.BINDING_INFO_POINTLESS, LoginSettingManager.BINDING_INFO_POINTLESS, LoginSettingManager.BINDING_INFO_POINTLESS);
			break;
		case R.id.iv_back:
			this.onBackPressed();
			break;
		case R.id.iv_x:
			et_email.setText("");
			break;
		default:
			break;
		}
	}

	@Override
	public boolean handleMessage(Message msg) {

		switch (msg.what) {

		case Requester3.RESPONSE_TYPE_BINDING:
			try {
				ZDialog.dismiss();
				GsonResponse3.bindingResponse gbr = (GsonResponse3.bindingResponse) msg.obj;
				if(gbr != null){
					if (gbr.status.equals("0")) {
						CmmobiClickAgentWrapper.onEvent(this, "account_binding", "2");
						Prompt.Dialog(this, false, "提示", "请进入邮箱激活，然后重新登陆", new DialogInterface.OnClickListener() {
							
							@Override
							public void onClick(DialogInterface dialog, int which) {
								// TODO Auto-generated method stub
								SettingBindEmailActivity.this.finish();
							}
						});
						
					}else if(gbr.status.equals("200600")){
						Prompt.Dialog(this, false, "提示", Constant.CRM_STATUS[Integer.parseInt(gbr.crm_status)], null);
					}else{
						Prompt.Dialog(this, false, "提示", "操作失败，请稍后再试", null);
					}
				}else{
					Prompt.Dialog(this, false, "提示", "操作失败，网络不给力", null);
				}
			} catch (Exception e) {
				// TODO: handle exception
			}
			break;
		}

		return false;
	}


	@Override
	public void afterTextChanged(Editable s) {
		// TODO Auto-generated method stub
		if(Prompt.checkEmail(et_email.getText().toString().trim())){
			tv_next.setEnabled(true);
		}else{
			tv_next.setEnabled(false);
		}
	}


	@Override
	public void beforeTextChanged(CharSequence s, int start, int count,
			int after) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) {
		// TODO Auto-generated method stub
		
	}

}
