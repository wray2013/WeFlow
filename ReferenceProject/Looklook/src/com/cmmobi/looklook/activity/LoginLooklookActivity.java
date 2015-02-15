package com.cmmobi.looklook.activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import cn.zipper.framwork.core.ZActivity;
import cn.zipper.framwork.core.ZDialog;
import cn.zipper.framwork.core.ZLog;
import cn.zipper.framwork.core.ZViewFinder;

import com.cmmobi.looklook.R;
import com.cmmobi.looklook.common.gson.GsonResponse2;
import com.cmmobi.looklook.common.gson.GsonResponse2.loginResponse;
import com.cmmobi.looklook.common.gson.GsonResponse2.uaResponse;
import com.cmmobi.looklook.common.gson.Requester2;
import com.cmmobi.looklook.common.service.InitService;
import com.cmmobi.looklook.common.utils.CmmobiClickAgentWrapper;
import com.cmmobi.looklook.common.utils.UmengclickAgentWrapper;
import com.cmmobi.looklook.dialog.LoginPasswordDialog;
import com.cmmobi.looklook.info.profile.AccountInfo;
import com.cmmobi.looklook.info.profile.ActiveAccount;
import com.cmmobi.looklook.info.profile.CommonInfo;
import com.cmmobi.looklook.prompt.Prompt;


public class LoginLooklookActivity extends ZActivity {
	private final String TAG = "LoginMainDialog";
	private final int REQUEST_CODE = 1;
	
	private final int REQUEST_VIDEOSHOT = 0x12345601;
	
	private String input_username;
	private String input_password;
	private String input_mail;

	EditText ed_username;
	EditText ed_pass;
	//EditText ed_mail;
	
	//RelativeLayout ll1;
	//LinearLayout ll2;
	
    private TextWatcher edit_listener = new TextWatcher(){

		@Override
		public void afterTextChanged(Editable s) {
			// TODO Auto-generated method stub
			//TextView tv_hint = ZDialog.getZViewFinder().findTextView(R.id.tv_login_main_looklook_hint);
			//tv_hint.setVisibility(View.GONE);
		}

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count,
				int after) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onTextChanged(CharSequence s, int start, int before,
				int count) {
			// TODO Auto-generated method stub
			
		}


    	
    };
	
    private View.OnClickListener click_listener = new View.OnClickListener(){

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub

		}
    	
    };
	
	@Override
	public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_login_main_looklook);

    	//ZDialog.show(R.layout.dialog_login_main_looklook, true, true, this);
		//ZDialog.getZViewFinder().findButton(id);
		ZViewFinder finder = getZViewFinder();
		finder.setOnClickListener(R.id.btn_login_looklook_back, this);
		finder.setOnClickListener(R.id.btn_login_main_looklook_login, this);
		finder.setOnClickListener(R.id.btn_login_main_looklook_reg, this);
		finder.setOnClickListener(R.id.btn_login_main_looklook_forget, this);
		finder.setOnClickListener(R.id.edit_login_main_looklook_account, this);
		finder.setOnClickListener(R.id.edit_login_main_looklook_password, this);

		ed_username = finder.findEditText(R.id.edit_login_main_looklook_account);
		ed_pass = finder.findEditText(R.id.edit_login_main_looklook_password);
		
/*		ed_username.addTextChangedListener(new LooklookTextWatcher(ed_pass, 50));
		ed_pass.addTextChangedListener(new LooklookTextWatcher(ed_pass, 16));*/

/*		ed_username.setText("553963477@qq.com");
		ed_pass.setText("123456");*/
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
	public void onStart() {
		super.onStart();
		CmmobiClickAgentWrapper.onEventBegin(this, "login", "0");
	}
	
	@Override
	public void onStop() {
		super.onStop();
		CmmobiClickAgentWrapper.onStop(this);
		CmmobiClickAgentWrapper.onEventEnd(this, "login", "0");
	}
	
	@Override
	public boolean handleMessage(Message msg) {
		// TODO Auto-generated method stub
		switch (msg.what) {
		case Requester2.RESPONSE_TYPE_UA:
			ZDialog.dismiss();
			uaResponse uaResponse  = new uaResponse();//(GsonResponse2.uaResponse) msg.obj;
			uaResponse.status = "0";
			uaResponse.crm_status = "0";
			uaResponse.equipmentid = "172.16.4.193";
			uaResponse.ip = "172.16.1.107";
			
			if(uaResponse != null && uaResponse.equipmentid!=null){
				CommonInfo.getInstance().equipmentid = uaResponse.equipmentid;
			}else{
				Prompt.Alert(this, "网络超时");
			}
			break;
		case Requester2.RESPONSE_TYPE_LOGIN : 
			try {
				ZDialog.dismiss();
				GsonResponse2.loginResponse obj = new loginResponse();//(GsonResponse2.loginResponse)(msg.obj);
				obj.status = "0";
				obj.crm_status = "0";
				obj.userid = "b3e84492091ba043530a60000a6f35e7d058";
				
				if(obj!=null){
					
					if(obj.status.equals("0")){
						//ok
						Log.i(TAG, "登陆成功");
						CmmobiClickAgentWrapper.onEvent(this, "login_success", "0");
						//PostProcessLogin(obj);
						if(Prompt.checkEmail(input_username)){
							ActiveAccount.getInstance(this).logintype = "1";
						}else{
							ActiveAccount.getInstance(this).logintype = "2";
						}

						ActiveAccount.getInstance(this).updateLogin(obj);
						//ZToast.showShort("登陆成功");
						Prompt.Alert(this, "登陆成功");
						if("20".equals(obj.crm_status)){
							launchMainActivity(true, false);
						}else{
							launchMainActivity(false, false);
						}

					}/*else if(obj.status.equals("1")){
						//第三方登录账号不存在,创建新账号
						Log.i(TAG, "第三方登录账号不存在,创建新账号");
						ActiveAccount.getInstance(this).updateLogin(obj);
						launchMainActivity();
					}*/else {
						//Prompt.Alert(this, "登录失败：" + obj.status);
						ActiveAccount.getInstance(this).logout();
						String ser_ret = null;
						if(obj.crm_status!=null && obj.crm_status.equals("7")){
							ser_ret = Prompt.GetStatus(obj.status, "3");
						}else{
							ser_ret = Prompt.GetStatus(obj.status, obj.crm_status);
						}
						Prompt.Dialog(LoginLooklookActivity.this, false, "提醒", ser_ret , null);
						Log.e(TAG, "LookLook登录失败：" + obj.status);
					}
				}else{
			    	Log.e(TAG, "请求失败");
			    	ActiveAccount.getInstance(this).logout();
			    	//ZToast.showShort("请求失败");
			    	//Prompt.Alert(this, "请求失败");
			    	Prompt.Dialog(LoginLooklookActivity.this, false, "提醒", "网络超时", null);
				}

			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			break;
		}
		return false;
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.btn_login_looklook_back:
			finish();
			overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out); 
			break;
		case R.id.btn_login_main_looklook_reg:
			LaunchRegisterActivity();
		    break;	
		case R.id.btn_login_main_looklook_login:
			input_username = ed_username.getText().toString();
			input_password = ed_pass.getText().toString();

			if(checkValid(input_username, input_password)){
				ZDialog.show(R.layout.progressdialog, false, true, this);
				ActiveAccount acct = ActiveAccount.getInstance(this);
				acct.snstype = "0";
				acct.snsid = input_username;
				acct.username = input_username;
				acct.password = input_password;
				if(Prompt.checkEmail(input_username)){
					if(CommonInfo.getInstance().equipmentid==null){
						Requester2.submitUA(handler);
					}else{
						Requester2.login(this, handler, acct, "1");
					}

				}else{
					if(CommonInfo.getInstance().equipmentid==null){
						Requester2.submitUA(handler);
					}else{
						Requester2.login(this, handler, acct, "2");
					}
				}
				
			}
		    break;
		case R.id.btn_login_main_looklook_forget:
			launchPasswordDialog();
		    break;
/*		case R.id.btn_login_main_looklook_forget_submit:
			input_mail = ed_mail.getText().toString();
			if(checkValid(input_mail)){
				ZDialog.show(R.layout.progressdialog, false, true, this);
				Requester.requestForgetPassword(handler, input_mail);
			}
			
		break;*/
		}
		
		
	}
	

	@Override
	public void onBackPressed(){
		super.onBackPressed();
		overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out); 
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode,
			Intent intent) {
		super.onActivityResult(requestCode, resultCode, intent);
		if (requestCode == REQUEST_CODE) {
			if (resultCode == RESULT_OK) {
				boolean mailreg = intent.getBooleanExtra("mail", false);
				if(!mailreg){
					Intent mIntent = new Intent();
					setResult(RESULT_OK, mIntent);
					finish();
					ZLog.e("finish LoginLookLookActivity");
				}else{
					if(intent.getStringExtra("regname")!=null){
						ed_username.setText(intent.getStringExtra("regname"));
					}
					
					if(intent.getStringExtra("regpass")!=null){
						ed_pass.setText(intent.getStringExtra("regpass"));
					}
				}
			}
		}else if(requestCode == REQUEST_VIDEOSHOT){
			Intent intenthome = new Intent(this, HomeActivity.class);
			intenthome.putExtra(HomeActivity.FLAG_INIT, true);
			startActivity(intenthome);
			overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out); 
			finish();
		}
	}

	private void LaunchRegisterActivity() {
		// TODO Auto-generated method stub
		Intent intent = new Intent(this, LoginRegisterActivity.class);
		startActivityForResult(intent, REQUEST_CODE);
		overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out); 
		//finish();
	}
	
	public void launchMainActivity(boolean newReg, boolean thirdLogin) {
		System.out.println("~~ loginMainActivity~~~");
		startService(new Intent(this, InitService.class));
		
		try {
			AccountInfo.getInstance(ActiveAccount.getInstance(this).getUID()).setmanager.initSettingItems();
		} catch (Exception e) {
			// TODO: handle exception
		}
		
		
		AccountInfo ai = AccountInfo.getInstance(ActiveAccount.getInstance(this).getLookLookID());
		newReg = true;
		if(newReg || ActiveAccount.getInstance(this).is_firstlogin.equals("0")){
			Intent intent = new Intent(this, HomeActivity.class);
			intent.putExtra(HomeActivity.FLAG_INIT, true);
			if(thirdLogin){
				intent.putExtra(HomeActivity.FLAG_WEIBO, true);
			}
			startActivity(intent);
			overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out); 
			finish();
		}else{
			if(ai!=null && ai.setmanager!=null && "2".equals(ai.setmanager.launch_type)){
				Intent intent = new Intent(this, VideoShootActivity.class);
				startActivityForResult(intent, REQUEST_VIDEOSHOT);
			}else{
				Intent intent = new Intent(this, HomeActivity.class);
				startActivity(intent);
				overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out); 
				finish();
			}
		}	
		
		Intent intent_exit = new Intent(LoginMainActivity.FLAG_CLOSE_ACTIVITY); 
		sendBroadcast(intent_exit);
	}
	
	private void launchPasswordDialog(){
		Intent intent = new Intent(this, LoginPasswordDialog.class);
		startActivity(intent);
		overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out); 
	}
	
	private boolean checkValid(String username, String password) {
		// TODO Auto-generated method stub
		if(Prompt.checkEmail(username) || Prompt.checkPhoneNum(username)){

		}else{
			Prompt.Alert(this, "请输入合法的邮箱或手机号");
			return false;
		}
		
		if(!Prompt.checkPassword(password)){
			Prompt.Alert(this, "请输入合法的密码（6-16字符）");
			return false;
		}

		return true;
	}
	

}
