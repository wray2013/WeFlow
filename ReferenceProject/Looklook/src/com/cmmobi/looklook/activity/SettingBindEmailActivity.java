package com.cmmobi.looklook.activity;

import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import cn.zipper.framwork.core.ZActivity;
import cn.zipper.framwork.core.ZDialog;

import com.cmmobi.looklook.R;
import com.cmmobi.looklook.common.constant.Constant;
import com.cmmobi.looklook.common.gson.GsonResponse2;
import com.cmmobi.looklook.common.gson.Requester2;
import com.cmmobi.looklook.common.utils.CmmobiClickAgentWrapper;
import com.cmmobi.looklook.common.utils.UmengclickAgentWrapper;
import com.cmmobi.looklook.info.profile.LoginSettingManager;
import com.cmmobi.looklook.prompt.Prompt;

/**
 * 绑定邮箱界面
 * 
 * @author youtian
 * 
 */
public class SettingBindEmailActivity extends ZActivity {
	
	private Button btn_next;// 下一步
	private EditText et_email;
	private ImageView iv_back;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_settings_bind_email);
		btn_next = (Button) findViewById(R.id.btn_next);
		btn_next.setOnClickListener(this);
		
		et_email = (EditText) findViewById(R.id.et_email);
		
		iv_back = (ImageView) findViewById(R.id.iv_back);
		iv_back.setOnClickListener(this);
		
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
		case R.id.btn_next:
			String email = et_email.getText().toString().trim();
			if(Prompt.checkEmail(email)){
			ZDialog.show(R.layout.progressdialog, false, true, this);
			Requester2.bindAccount(handler, LoginSettingManager.BINDING_REQUEST_TYPE_EMAIL, LoginSettingManager.BINDING_INFO_POINTLESS, email, LoginSettingManager.BINDING_INFO_POINTLESS, LoginSettingManager.BINDING_INFO_POINTLESS, LoginSettingManager.BINDING_INFO_POINTLESS, LoginSettingManager.BINDING_INFO_POINTLESS, LoginSettingManager.BINDING_INFO_POINTLESS, LoginSettingManager.BINDING_INFO_POINTLESS, LoginSettingManager.BINDING_INFO_POINTLESS, LoginSettingManager.BINDING_INFO_POINTLESS, LoginSettingManager.BINDING_INFO_POINTLESS, LoginSettingManager.BINDING_INFO_POINTLESS, LoginSettingManager.BINDING_INFO_POINTLESS, LoginSettingManager.BINDING_INFO_POINTLESS, LoginSettingManager.BINDING_INFO_POINTLESS);
			}else{
				Prompt.Dialog(this, false, "提示", "请输入正确的邮箱地址", null);
			}
			break;
		case R.id.iv_back:
			this.onBackPressed();
			break;
		default:
			break;
		}
	}

	@Override
	public boolean handleMessage(Message msg) {

		switch (msg.what) {

		case Requester2.RESPONSE_TYPE_BINDING:
			try {
				ZDialog.dismiss();
				GsonResponse2.bindingResponse gbr = (GsonResponse2.bindingResponse) msg.obj;
				if(gbr != null){
					if (gbr.status.equals("0")) {
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

}
