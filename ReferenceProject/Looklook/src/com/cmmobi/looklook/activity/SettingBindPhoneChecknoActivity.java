package com.cmmobi.looklook.activity;

import java.util.Timer;
import java.util.TimerTask;

import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import cn.zipper.framwork.core.ZActivity;
import cn.zipper.framwork.core.ZDialog;

import com.cmmobi.looklook.R;
import com.cmmobi.looklook.common.constant.Constant;
import com.cmmobi.looklook.common.gson.GsonResponse2;
import com.cmmobi.looklook.common.gson.GsonResponse2.MyBind;
import com.cmmobi.looklook.common.gson.Requester2;
import com.cmmobi.looklook.common.utils.CmmobiClickAgentWrapper;
import com.cmmobi.looklook.common.utils.UmengclickAgentWrapper;
import com.cmmobi.looklook.info.profile.AccountInfo;
import com.cmmobi.looklook.info.profile.ActiveAccount;
import com.cmmobi.looklook.info.profile.LoginSettingManager;
import com.cmmobi.looklook.prompt.Prompt;

/**
 * 发送短信界面
 * 
 * @author youtian
 * 
 */
public class SettingBindPhoneChecknoActivity extends ZActivity{

	private String phonenum;
	private EditText et_checkno;
	private TextView tv_checknoexplain;
	private Button btn_next;
	private Button btn_back;
	private Button btn_newcheckno;
	public int time = 90;
	private LoginSettingManager lsm;
	private int snys;
	
	private Timer timer = new Timer();

	TimerTask task = new TimerTask() {
			@Override
			public void run() {

				runOnUiThread(new Runnable() {		// UI thread
					@Override
					public void run() {
						time--;
						setBtnNewCheckNoString();
						if(time == 0){
							btn_newcheckno.setEnabled(true);
						}
					}
				});
			}
		};
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setting_bind_phone_check_no);
		phonenum = this.getIntent().getStringExtra("phonenum");
		
		btn_back = (Button) findViewById(R.id.btn_back);
		btn_back.setOnClickListener(this);
		
		et_checkno = (EditText) findViewById(R.id.et_checkno);
		
		tv_checknoexplain = (TextView) findViewById(R.id.tv_checknoexplain);
		tv_checknoexplain.setText("已给手机" + phonenum + "发了一条信息，请输入短信中的数字验证码");
		
		btn_next = (Button) findViewById(R.id.btn_next);
		btn_next.setOnClickListener(this);
		
		btn_newcheckno = (Button) findViewById(R.id.btn_newcheckno);
		btn_newcheckno.setOnClickListener(this);
		setBtnNewCheckNoString();
		
		String userID = ActiveAccount.getInstance(this).getUID();
		AccountInfo accountInfo = AccountInfo.getInstance(userID);
		if (accountInfo != null) {
			lsm = accountInfo.setmanager;
		}
		
		 timer.schedule(task, 1000, 1000);       // timeTask 
	}

	public void setBtnNewCheckNoString() {
		if(time > 0){
			btn_newcheckno.setText("重新发送（"+ time + "）");
		}else if(time == 0){
			btn_newcheckno.setText("重新发送");
		}
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
	
/*	
	@Override

    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if(keyCode == KeyEvent.KEYCODE_BACK){
        	Intent intent = new Intent(this, SettingBindPhoneActivity.class);
        	startActivity(intent);
            return true;
        }
        return super.onKeyDown(keyCode, event);

    }
	*/

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_back:
			this.onBackPressed();
			break;
		case R.id.btn_next:
			String checkno = et_checkno.getText().toString().trim();
			if(checkno != null && ! checkno.isEmpty()){
				ZDialog.show(R.layout.progressdialog, false, true, this);
				Requester2.bindAccount(handler, LoginSettingManager.BINDING_REQUEST_TYPE_PHONE, LoginSettingManager.PHONE_TYPE_MAIN, phonenum, LoginSettingManager.BINDING_INFO_POINTLESS, LoginSettingManager.BINDING_INFO_POINTLESS, checkno,  LoginSettingManager.BINDING_INFO_POINTLESS, LoginSettingManager.BINDING_INFO_POINTLESS, LoginSettingManager.BINDING_INFO_POINTLESS, LoginSettingManager.BINDING_INFO_POINTLESS, LoginSettingManager.BINDING_INFO_POINTLESS, LoginSettingManager.BINDING_INFO_POINTLESS, LoginSettingManager.BINDING_INFO_POINTLESS, LoginSettingManager.BINDING_INFO_POINTLESS, LoginSettingManager.BINDING_INFO_POINTLESS, LoginSettingManager.BINDING_INFO_POINTLESS);
			}else{
				Prompt.Dialog(this, false, "提示", "请输入验证码", null);	
			}
			break;
		case R.id.btn_newcheckno:
			ZDialog.show(R.layout.progressdialog, false, true, this);
			Requester2.getCheckNo(handler, phonenum, LoginSettingManager.GET_CHECK_NO_PHONE);
			break;
		}
	}

	@Override
	public boolean handleMessage(Message msg) {
		// TODO Auto-generated method stub
		switch (msg.what) {
			case Requester2.RESPONSE_TYPE_BINDING:
				ZDialog.dismiss();
				try{
					GsonResponse2.bindingResponse grb = (GsonResponse2.bindingResponse) msg.obj;
					if(grb != null){
						if(grb.status.equals("0")){
							 MyBind phoneBind = new MyBind();
							 phoneBind.binding_type = LoginSettingManager.BINDING_TYPE_PHONE;
							 phoneBind.binding_info = phonenum;
							 lsm.addBindingInfo(phoneBind);
							this.setResult(RESULT_OK);
							Prompt.Dialog(this, false, "提示", "绑定成功", new DialogInterface.OnClickListener() {
								
								@Override
								public void onClick(DialogInterface dialog, int which) {
									// TODO Auto-generated method stub
									SettingBindPhoneChecknoActivity.this.finish();
								}
							});
						}else if(grb.status.equals("200600")){
							Prompt.Dialog(this, false, "提示", Constant.CRM_STATUS[Integer.parseInt(grb.crm_status)], null);
						}else{
							Prompt.Dialog(this, false, "提示", "操作失败，请稍后再试", null);
						}
					}else{
						Prompt.Dialog(this, false, "提示", "操作失败，网络不给力", null);
						btn_newcheckno.setEnabled(true);
					}
					
				}catch (Exception e) {
					// TODO: handle exception
				}
				break;
			case Requester2.RESPONSE_TYPE_CHECKNO:
				try {
					ZDialog.dismiss();
					GsonResponse2.checkNoResponse gsn = (GsonResponse2.checkNoResponse) msg.obj;
					if(gsn != null){
						if (gsn.status.equals("0")) {
							btn_newcheckno.setEnabled(false);
							time = 90;
							setBtnNewCheckNoString();
							Prompt.Dialog(this, false, "提示", "已重新发送验证码", null);
						}else if(gsn.status.equals("200600")){
							Prompt.Dialog(this, false, "提示", Constant.CRM_STATUS[Integer.parseInt(gsn.crm_status)], null);
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
			default:
				break;
		}
		return false;
	}

	
	
}
