package com.cmmobi.looklook.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Message;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import cn.zipper.framwork.core.ZActivity;
import cn.zipper.framwork.core.ZDialog;
import cn.zipper.framwork.core.ZViewFinder;

import com.cmmobi.looklook.R;
import com.cmmobi.looklook.common.constant.Constant;
import com.cmmobi.looklook.common.gson.GsonResponse3;
import com.cmmobi.looklook.common.gson.GsonResponse3.checkNickNameExistResponse;
import com.cmmobi.looklook.common.gson.GsonResponse3.checkUserNameExistResponse;
import com.cmmobi.looklook.common.gson.Requester3;
import com.cmmobi.looklook.common.utils.CmmobiClickAgentWrapper;
import com.cmmobi.looklook.info.profile.ActiveAccount;
import com.cmmobi.looklook.prompt.Prompt;

public class LoginBindingActivity extends ZActivity {
	
	EditText mEditNickname;  //用户名，昵称
	EditText mEditMailPhone; //手机号 或 邮箱
	EditText mEditPwd; //密码
	/*private int tick;*/
	
	//Button btn_back;
	boolean isPhoneNum = false;
	TextView mTvNickCheckStr;
	
	ImageView mIVNickOk;
	ImageView mIVMailPhoneOk;
	
	String input_nickname ;
	String input_mail_phone;
	String input_passwd;
	String input_yzm;

	private final int HANDLER_FLAG_HIDEPASS = 0x012383732;
	

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login_binding);

		ZViewFinder finder = getZViewFinder();
		findViewById(R.id.rly_login_binding).setOnClickListener(this);
		findViewById(R.id.btn_login_reg_password).setOnClickListener(this);
		
		// 昵称
		mEditNickname = finder.findEditText(R.id.edit_login_reg_nickname);
		mEditNickname.setOnFocusChangeListener(mNickFocusListener);
		mEditNickname.addTextChangedListener(mEditNickListener);
		
		// nick state button
		mIVNickOk = finder.findImageView(R.id.iv_login_reg_nick_ok);
		mIVNickOk.setVisibility(View.INVISIBLE);
		mIVNickOk.setOnClickListener(this);
		
		// nick ckeck result
		mTvNickCheckStr = finder.findTextView(R.id.tv_login_check_name);
		mTvNickCheckStr.setVisibility(View.INVISIBLE);
		
		// 密码
		mEditPwd = finder.findEditText(R.id.edit_login_reg_password);
		
		// 邮箱
		mEditMailPhone = finder.findEditText(R.id.edit_login_reg_mail_ph);
		mEditMailPhone.addTextChangedListener(mEditPhoneMailListener);
		mEditMailPhone.setOnFocusChangeListener(mPhMailFocusListener);
		
		mIVMailPhoneOk = finder.findImageView(R.id.iv_login_reg_mail_ph_ok);
		mIVMailPhoneOk.setVisibility(View.INVISIBLE);
		mIVMailPhoneOk.setOnClickListener(this);
		
		// OK
		findViewById(R.id.btn_login_binding_ok).setOnClickListener(this);
		
		isPhoneNum = false;

		
	}
	
	@Override
	public boolean handleMessage(Message msg) {
		ActiveAccount acct = ActiveAccount.getInstance(getApplicationContext());
		// TODO Auto-generated method stub
		switch (msg.what) {
		case Requester3.RESPONSE_TYPE_CHECK_NICKNAME:
			mIVNickOk.setVisibility(View.VISIBLE);
			if(msg.obj!=null){
				GsonResponse3.checkNickNameExistResponse obj = (checkNickNameExistResponse) msg.obj;
				if("0".equals(obj.status)){
					mIVNickOk.setBackgroundResource(R.drawable.ok);
					mIVNickOk.setTag("ok");
					mTvNickCheckStr.setVisibility(View.INVISIBLE);
				}else{
					Prompt.Dialog(this, false, "提示", "该昵称已注册", null);
					mIVNickOk.setBackgroundResource(R.drawable.not);
					mIVNickOk.setTag("not");
					mTvNickCheckStr.setVisibility(View.VISIBLE);
				}
			}else{
				mIVNickOk.setBackgroundResource(R.drawable.not);
				mIVNickOk.setTag("not");
			}
			break;
		case Requester3.RESPONSE_TYPE_CHECK_USERNAME:
			mIVMailPhoneOk.setVisibility(View.VISIBLE);
			if(msg.obj!=null){
				GsonResponse3.checkUserNameExistResponse obj =  (checkUserNameExistResponse) msg.obj;
				if("0".equals(obj.status)){
					mIVMailPhoneOk.setBackgroundResource(R.drawable.ok);
					mIVMailPhoneOk.setTag("ok");
				}else{
					Prompt.Dialog(this, false, "提示", "该手机或邮箱已注册", null);
					mIVMailPhoneOk.setBackgroundResource(R.drawable.not);
					mIVMailPhoneOk.setTag("not");
				}
			}else{
				mIVMailPhoneOk.setBackgroundResource(R.drawable.not);
				mIVMailPhoneOk.setTag("not");
			}
		case HANDLER_FLAG_HIDEPASS:
			mEditPwd.setTransformationMethod(PasswordTransformationMethod
					.getInstance());
			mEditPwd.postInvalidate();
			break;
		case Requester3.RESPONSE_TYPE_REGISTER : 
			ZDialog.dismiss();
			if(msg.obj!=null){
				GsonResponse3.registerResponse obj = (GsonResponse3.registerResponse)(msg.obj);

				if(obj.status!=null && obj.status.equals("0") && obj.userid!=null){//register success
					CmmobiClickAgentWrapper.onEvent(this, "login_loc_success");					
					if(isPhoneNum){
						acct.snstype = "0";
						acct.snsid = input_mail_phone;
						acct.username = input_mail_phone;
						acct.password = input_passwd;
						Requester3.login(this, getHandler(), acct, "2");
					}else{
						Prompt.Dialog(LoginBindingActivity.this, true, "激活looklook账号", "是否打开相应的邮箱，激活looklook账号", new OnClickListener() {
							
							@Override
							public void onClick(DialogInterface dialog, int which) {
								// TODO Auto-generated method stub
								try{
									String url = input_mail_phone;
									String a[] = url.split("@"); 
									if(a!=null && a.length==2){
										String mail_url = Constant.MAIL_MAP.get(a[1]);
										if(mail_url==null){
											mail_url = "http://www.baidu.com/";
										}
										Uri uri = Uri.parse (mail_url);    
										Intent intent = new Intent (Intent.ACTION_VIEW, uri);    
										LoginBindingActivity.this.startActivity(intent);  
									}
								}catch(android.content.ActivityNotFoundException e){
									e.printStackTrace();
									Prompt.Alert(LoginBindingActivity.this, "没有合适的外部浏览器打开该邮箱");
								}

								Intent mIntent = new Intent();
								mIntent.putExtra("mail", true);
								setResult(RESULT_OK, mIntent);
								finish();

							}
						}, new OnClickListener() {
							
							@Override
							public void onClick(DialogInterface dialog, int which) {
								// TODO Auto-generated method stub
								Intent mIntent = new Intent();
								mIntent.putExtra("mail", true);
								mIntent.putExtra("regname", input_mail_phone);
								mIntent.putExtra("regpass", input_passwd);
								setResult(RESULT_OK, mIntent);
								finish();
							}
						});
					}
					
/*						boolean ret = ActiveAccount.getInstance(this).updateRegister("0", input_mail_phone, obj);
					if(ret){
//						ZLog.e("finish LoginRegisterActivity");
						Intent mIntent = new Intent();
						setResult(RESULT_OK, mIntent);
						finish();

					}*/
				}else if(obj.crm_status!=null){
					//int error_index = Integer.parseInt(obj.crm_status);
					//Prompt.Alert(this, Constant.CRM_STATUS[error_index]);
					Prompt.Dialog(LoginBindingActivity.this, false, "提醒",  Prompt.GetStatus(obj.status, obj.crm_status), null);
				}
			
			}else{
				Prompt.Dialog(LoginBindingActivity.this, false, "注册","网络异常", null);
			}
			break;
		}
		return false;
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.rly_login_binding:
			InputMethodManager imm = (InputMethodManager)getSystemService( Context.INPUT_METHOD_SERVICE);
			imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
			break;
		case R.id.btn_login_binding_ok:
			input_nickname = mEditNickname.getText().toString();
			input_mail_phone = mEditMailPhone.getText().toString();
			input_passwd = mEditPwd.getText().toString();
//			if(checkValid(input_nickname, input_mail_phone, input_passwd)){
//				//ok
//				ZDialog.show(R.layout.progressdialog, false, true, this);
////				ZLog.e("nickname:" +  input_nickname + " mail:" + input_mail + " pass:" + input_passwd);
//				if(CommonInfo.getInstance().equipmentid==null){
//					Requester3.submitUA(handler);
//				}else{
//					if(Prompt.checkEmail(input_mail_phone)){
//						Requester3.register(handler, input_nickname, input_mail_phone, input_passwd, input_yzm, "1");	
//					}else{
//						Requester3.register(handler, input_nickname, input_mail_phone, input_passwd, input_yzm, "2");
//					}
//				}
//			}
			break;
		case R.id.iv_login_reg_nick_ok:
			if("not".equals(mIVNickOk.getTag())){
				mIVNickOk.setVisibility(View.INVISIBLE);
				mEditNickname.setText("");
				mEditNickname.requestFocus();
			}
			break;
		case R.id.btn_login_reg_password:
			mEditPwd.setTransformationMethod(HideReturnsTransformationMethod
					.getInstance());
			mEditPwd.postInvalidate();
			Message message = handler.obtainMessage(HANDLER_FLAG_HIDEPASS);
			handler.sendMessageDelayed(message, 3000);
		break;
		case R.id.iv_login_reg_mail_ph_ok:
			if("not".equals(mIVMailPhoneOk.getTag())){
				mIVMailPhoneOk.setVisibility(View.INVISIBLE);
				mEditMailPhone.setText("");
				mEditMailPhone.requestFocus();
			}
			break;				
		}
	}

	private boolean checkValid(String input_nickname, String input_mail_phone, String input_passwd) {
		// TODO Auto-generated method stub

		if(!(Prompt.checkEmail(input_mail_phone)||Prompt.checkPhoneNum(input_mail_phone))){
			//Prompt.Alert(this, "请输入合法的邮箱或手机号");
			Prompt.Dialog(LoginBindingActivity.this, false, "提醒", "请输入合法的邮箱或手机号", null);
			return false;
		}
		
		if(input_nickname==null || input_nickname.length()<3){
			//Prompt.Alert(this, "请输入合法的用户昵称");
			Prompt.Dialog(LoginBindingActivity.this, false, "提醒", "请输入合法的用户昵称（大于3个字符）", null);
			return false;
		}
		

		
		if(!Prompt.checkPassword(input_passwd)){
			//Prompt.Alert(this, "请输入合法的密码（6-16字符）");
			Prompt.Dialog(LoginBindingActivity.this, false, "提醒", "请输入合法的密码（6-16字符）", null);
			return false;
		}
		
		return true;
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
	public void onBackPressed(){
		super.onBackPressed();
		overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
	}
	
	
	
	private TextWatcher mEditPhoneMailListener = new TextWatcher(){
		@Override
		public void beforeTextChanged(CharSequence s, int start, int count,
				int after) {
		}
		@Override
		public void onTextChanged(CharSequence s, int start, int before,
				int count) {
		}
		@Override
		public void afterTextChanged(Editable s) {
			String a = mEditMailPhone.getText().toString();
			if(!TextUtils.isEmpty(a)){
				mIVMailPhoneOk.setVisibility(View.VISIBLE);
				if(Prompt.checkPhoneNum(a)){
					isPhoneNum = true;
					mIVMailPhoneOk.setBackgroundResource(R.drawable.ok);
					mIVMailPhoneOk.setTag("ok");
				}else if(Prompt.checkEmail(a)){
					isPhoneNum = false;
					mIVMailPhoneOk.setBackgroundResource(R.drawable.ok);
					mIVMailPhoneOk.setTag("ok");
				}else{
					isPhoneNum = false;
					mIVMailPhoneOk.setBackgroundResource(R.drawable.not);
					mIVMailPhoneOk.setTag("not");
				}
			}else{
				mIVMailPhoneOk.setVisibility(View.INVISIBLE);
			}
		}
    };
    
    private View.OnFocusChangeListener mPhMailFocusListener = new View.OnFocusChangeListener(){
  		@Override
  		public void onFocusChange(View v, boolean hasFocus) {
  			if(!hasFocus){
  				String a = mEditMailPhone.getText().toString();
  				if(Prompt.checkPhoneNum(a)){
  					isPhoneNum = true;
  					Requester3.checkUserNameExist(getHandler(), a);
  				}else if(Prompt.checkEmail(a)){
  					isPhoneNum = false;
  					Requester3.checkUserNameExist(getHandler(), a);
  				}else{
  					isPhoneNum = false;
  				}
  			}
  		}
      };
      
      /**
       * 昵称的TextChange
       */
    private TextWatcher mEditNickListener = new TextWatcher(){
		@Override
		public void beforeTextChanged(CharSequence s, int start, int count,
				int after) {
		}
		@Override
		public void onTextChanged(CharSequence s, int start, int before,
				int count) {
		}
		@Override
		public void afterTextChanged(Editable s) {
			// TODO Auto-generated method stub
			String a = mEditNickname.getText().toString();
			if(!TextUtils.isEmpty(a)){
				if(Prompt.checkUserName(a)){
					mIVNickOk.setBackgroundResource(R.drawable.ok);
					mIVNickOk.setTag("ok");
				}else{
					mIVNickOk.setBackgroundResource(R.drawable.not);
					mIVNickOk.setTag("not");
				}
				mIVNickOk.setVisibility(View.VISIBLE);
			}else{
				mIVNickOk.setVisibility(View.INVISIBLE);
			}
			if(mTvNickCheckStr.isShown()){
				mTvNickCheckStr.setVisibility(View.INVISIBLE);
			}
		}
    };
    /**
     * 昵称的焦点监听
     */
    private View.OnFocusChangeListener mNickFocusListener = new View.OnFocusChangeListener(){
		@Override
		public void onFocusChange(View v, boolean hasFocus) {
			if(!hasFocus){
				if(!TextUtils.isEmpty(mEditNickname.getText().toString())){
					Requester3.checkNickNameExist(getHandler(), mEditNickname.getText().toString());
				}
			}
		}
    };
    
    
}
