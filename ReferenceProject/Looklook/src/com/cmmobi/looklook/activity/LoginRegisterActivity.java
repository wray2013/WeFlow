package com.cmmobi.looklook.activity;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import cn.zipper.framwork.core.ZActivity;
import cn.zipper.framwork.core.ZDialog;
import cn.zipper.framwork.core.ZViewFinder;
import cn.zipper.framwork.device.ZSimCardInfo;

import com.cmmobi.looklook.R;
import com.cmmobi.looklook.common.constant.Constant;
import com.cmmobi.looklook.common.gson.GsonResponse2;
import com.cmmobi.looklook.common.gson.GsonResponse2.checkNickNameExistResponse;
import com.cmmobi.looklook.common.gson.GsonResponse2.checkUserNameExistResponse;
import com.cmmobi.looklook.common.gson.GsonResponse2.uaResponse;
import com.cmmobi.looklook.common.gson.Requester2;
import com.cmmobi.looklook.common.utils.CmmobiClickAgentWrapper;
import com.cmmobi.looklook.common.utils.UmengclickAgentWrapper;
import com.cmmobi.looklook.info.profile.ActiveAccount;
import com.cmmobi.looklook.info.profile.CommonInfo;
import com.cmmobi.looklook.prompt.Prompt;
import com.cmmobi.looklook.prompt.TickDownHelper;
import com.google.gson.Gson;

public class LoginRegisterActivity extends ZActivity {
	Gson gson;
	EditText ed_mail_phone; //手机号 或 邮箱
	EditText ed_yzm; //验证码
	EditText ed_nick;  //用户名，昵称
	EditText ed_pass; //密码
	/*private int tick;*/
	
	//Button btn_back;
	boolean isPhoneNum = false;
	Button btn_yzm;
	Button btn_show_pass;
	Button ck_agreement;
	Button btn_login_looklook_reg_back;
	TextView tv_agreement;
	Button btn_ok;
	
	ImageView iv_login_reg_mail_ph_ok;
	ImageView iv_login_reg_yzm_ok;
	ImageView iv_login_reg_nick_ok;
	
	boolean mbDisplayFlg;
	String input_nickname ;
	String input_mail_phone;
	String input_passwd;
	String input_yzm;

	private final int HANDLER_FLAG_HIDEPASS = 0x012383732;
	private final int HANDLER_FLAG_ENABLE_YZM = 0x012383733;
	/*private final int HANDLER_FLAG_YZM_TICK = 0x012383734;*/
	
    private TextWatcher edit_phonenum_listener = new TextWatcher(){
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
		
		@Override
		public void afterTextChanged(Editable s) {
			// TODO Auto-generated method stub
			String a = ed_mail_phone.getText().toString();
			iv_login_reg_mail_ph_ok.setVisibility(View.VISIBLE);
			if(a!=null && !a.equals("")){
				if(Prompt.checkPhoneNum(a)){
					isPhoneNum = true;
					btn_yzm.setEnabled(true);
					ed_yzm.setEnabled(true);
					ShowYZM(true);
					iv_login_reg_mail_ph_ok.setBackgroundResource(R.drawable.ok);
					//Requester2.checkUserNameExist(getHandler(), a);
				}else if(Prompt.checkEmail(a)){
					isPhoneNum = false;
					btn_yzm.setEnabled(false);
					ed_yzm.setEnabled(false);
					ShowYZM(false);
					iv_login_reg_mail_ph_ok.setBackgroundResource(R.drawable.ok);
					//Requester2.checkUserNameExist(getHandler(), a);
				}else{
					isPhoneNum = false;
					btn_yzm.setEnabled(false);
					ed_yzm.setEnabled(false);
					ShowYZM(true);
					iv_login_reg_mail_ph_ok.setBackgroundResource(R.drawable.not);
				}
				iv_login_reg_mail_ph_ok.setVisibility(View.VISIBLE);
			}else{
				btn_yzm.setEnabled(true);
				ed_yzm.setEnabled(true);
				ShowYZM(true);
				iv_login_reg_mail_ph_ok.setVisibility(View.INVISIBLE);
			}

		}
		
    };
    
    private TextWatcher edit_nick_listener = new TextWatcher(){
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
		
		@Override
		public void afterTextChanged(Editable s) {
			// TODO Auto-generated method stub
			String a = ed_nick.getText().toString();
			if(a!=null && !a.equals("")){
				if(Prompt.checkUserName(a)){
					iv_login_reg_nick_ok.setBackgroundResource(R.drawable.ok);
				}else{
					iv_login_reg_nick_ok.setBackgroundResource(R.drawable.not);
				}
				iv_login_reg_nick_ok.setVisibility(View.VISIBLE);
			}else{
				iv_login_reg_nick_ok.setVisibility(View.INVISIBLE);
			}

		}
		
    };
    
    private TextWatcher edit_yzm_listener = new TextWatcher(){
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
		
		@Override
		public void afterTextChanged(Editable s) {
			// TODO Auto-generated method stub
			String a = ed_yzm.getText().toString();
			if(a!=null && !a.equals("")){
				if(Prompt.checkYZM(a)){
					iv_login_reg_yzm_ok.setTag("ok");
					iv_login_reg_yzm_ok.setBackgroundResource(R.drawable.ok);
				}else{
					iv_login_reg_yzm_ok.setTag("not");
					iv_login_reg_yzm_ok.setBackgroundResource(R.drawable.not);
				}
				iv_login_reg_yzm_ok.setVisibility(View.VISIBLE);
			}else{
				iv_login_reg_yzm_ok.setVisibility(View.INVISIBLE);
			}

		}
		
    };
    
    private View.OnFocusChangeListener yzm_focus_listener = new View.OnFocusChangeListener(){

		@Override
		public void onFocusChange(View v, boolean hasFocus) {
			// TODO Auto-generated method stub
			if(!hasFocus){
				String a = ed_yzm.getText().toString();
				if(a!=null && !a.equals("") && !Prompt.checkYZM(a)){
					Prompt.Dialog(LoginRegisterActivity.this, false, "提示", "验证码为6位数字", null);
				}

			}

		}
    	
    };
    
    private void ShowYZM(boolean show){
    	if(show){
    		rl_login_reg_yzm.setVisibility(View.VISIBLE);
    		tv_login_yzm_note.setVisibility(View.VISIBLE);
    		btn_yzm.setVisibility(View.VISIBLE);
    	}else{
    		rl_login_reg_yzm.setVisibility(View.INVISIBLE);
    		tv_login_yzm_note.setVisibility(View.INVISIBLE);
    		btn_yzm.setVisibility(View.INVISIBLE);
    	}
    }
    
    private View.OnFocusChangeListener phmail_focus_listener = new View.OnFocusChangeListener(){

		@Override
		public void onFocusChange(View v, boolean hasFocus) {
			// TODO Auto-generated method stub
			if(!hasFocus){
				String a = ed_mail_phone.getText().toString();
				if(Prompt.checkPhoneNum(a)){
					isPhoneNum = true;
					btn_yzm.setEnabled(true);
					ed_yzm.setEnabled(true);
					ShowYZM(true);
					Requester2.checkUserNameExist(getHandler(), a);
				}else if(Prompt.checkEmail(a)){
					isPhoneNum = false;
					btn_yzm.setEnabled(false);
					ed_yzm.setEnabled(false);
					ShowYZM(false);
					Requester2.checkUserNameExist(getHandler(), a);
				}else{
					isPhoneNum = false;
					btn_yzm.setEnabled(false);
					ed_yzm.setEnabled(false);
					ShowYZM(true);
					iv_login_reg_mail_ph_ok.setBackgroundResource(R.drawable.not);
				}
				

			}

		}
    	
    };
    
    private View.OnFocusChangeListener nick_focus_listener = new View.OnFocusChangeListener(){

		@Override
		public void onFocusChange(View v, boolean hasFocus) {
			// TODO Auto-generated method stub
			if(!hasFocus){
				if(ed_nick.getText().toString()!=null && !("".equals(ed_nick.getText().toString()))){
					Requester2.checkNickNameExist(getHandler(), ed_nick.getText().toString());
				}

			}

		}
    	
    };
	private FrameLayout rl_login_reg_yzm;
	private TextView tv_login_yzm_note;
	private LinearLayout ll_login_reg;


	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login_register);

		mbDisplayFlg = false;

		gson = new Gson();
		ZViewFinder finder = getZViewFinder();
		ll_login_reg = finder.findLinearLayout(R.id.ll_login_reg);
		
		ed_mail_phone = finder.findEditText(R.id.edit_login_reg_mail_ph);
		ed_yzm = finder.findEditText(R.id.et_login_reg_yzm);
		ed_nick = finder.findEditText(R.id.edit_login_reg_nickname);
		ed_pass = finder.findEditText(R.id.edit_login_reg_password);
		
		rl_login_reg_yzm = finder.findFrameLayout(R.id.rl_login_reg_yzm);
		tv_login_yzm_note = finder.findTextView(R.id.tv_login_yzm_note);

		//btn_back = finder.findButton(R.id.btn_login_reg_back);
		btn_yzm = finder.findButton(R.id.btn_login_reg_yzm);
		btn_yzm.setText(R.string.str_hqyzm);
		
		btn_show_pass = finder.findButton(R.id.btn_login_reg_password);
		ck_agreement = finder.findButton(R.id.ck_login_reg_agreement);
		tv_agreement = finder.findTextView(R.id.tv_login_reg_agreement);
		btn_ok = finder.findButton(R.id.btn_login_reg_ok);
		btn_login_looklook_reg_back = finder.findButton(R.id.btn_login_looklook_reg_back);
		
		iv_login_reg_mail_ph_ok = finder.findImageView(R.id.iv_login_reg_mail_ph_ok);
		iv_login_reg_yzm_ok = finder.findImageView(R.id.iv_login_reg_yzm_ok);
		iv_login_reg_nick_ok = finder.findImageView(R.id.iv_login_reg_nick_ok);
		
		iv_login_reg_mail_ph_ok.setVisibility(View.INVISIBLE);
		iv_login_reg_yzm_ok.setVisibility(View.INVISIBLE);
		iv_login_reg_nick_ok.setVisibility(View.INVISIBLE);

		//btn_back.setOnClickListener(this);
		ll_login_reg.setOnClickListener(this);
		
		btn_login_looklook_reg_back.setOnClickListener(this);
		btn_yzm.setOnClickListener(this);
		btn_show_pass.setOnClickListener(this);
		ck_agreement.setOnClickListener(this);
		tv_agreement.setOnClickListener(this);
		btn_ok.setOnClickListener(this);
		
		iv_login_reg_mail_ph_ok.setOnClickListener(this);
		iv_login_reg_nick_ok.setOnClickListener(this);
		iv_login_reg_yzm_ok.setOnClickListener(this);
			
		isPhoneNum = false;
		btn_yzm.setEnabled(false);
		ed_yzm.setEnabled(false);
		ck_agreement.setBackgroundResource(R.drawable.xieyi_pressed);
		ck_agreement.setTag("checked");
		btn_ok.setEnabled(true);
		//Requester.submitUA(getHandler());

		ed_mail_phone.addTextChangedListener(edit_phonenum_listener);
		ed_mail_phone.setOnFocusChangeListener(phmail_focus_listener);
		
		ed_yzm.addTextChangedListener(edit_yzm_listener);
		ed_yzm.setOnFocusChangeListener(yzm_focus_listener);
		
		ed_nick.setOnFocusChangeListener(nick_focus_listener);
		ed_nick.addTextChangedListener(edit_nick_listener);
		
		if(ZSimCardInfo.getNativePhoneNumber()!=null){
			//ed_mail_phone.setText(ZSimCardInfo.getNativePhoneNumber());
		}
		
/*		ed_mail_phone.addTextChangedListener(new LooklookTextWatcher(ed_pass, 50));
		ed_yzm.addTextChangedListener(new LooklookTextWatcher(ed_pass, 8));
		ed_nick.addTextChangedListener(new LooklookTextWatcher(ed_pass, 20));*/
		//TickDownHelper.getInstance(getHandler());
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
		CmmobiClickAgentWrapper.onEventBegin(this, "login_loc");
	}
	
	@Override
	public void onStop() {
		super.onStop();
		CmmobiClickAgentWrapper.onStop(this);
		CmmobiClickAgentWrapper.onEventEnd(this, "login_loc");
	}

	@Override
	public void onBackPressed(){
		super.onBackPressed();
		overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
	}
	
	@Override
	public boolean handleMessage(Message msg) {
		ActiveAccount acct = ActiveAccount.getInstance(getApplicationContext());
		// TODO Auto-generated method stub
		switch (msg.what) {
		case Requester2.RESPONSE_TYPE_UA:
			ZDialog.dismiss();
			uaResponse uaResponse  = (GsonResponse2.uaResponse) msg.obj;
			if(uaResponse != null && uaResponse.equipmentid!=null){
				CommonInfo.getInstance().equipmentid = uaResponse.equipmentid;
			}else{
				Prompt.Alert(this, "网络异常");
			}
			break;
		case Requester2.RESPONSE_TYPE_CHECK_USERNAME:
			iv_login_reg_mail_ph_ok.setVisibility(View.VISIBLE);
			if(msg.obj!=null){
				GsonResponse2.checkUserNameExistResponse obj =  (checkUserNameExistResponse) msg.obj;
				if("0".equals(obj.status)){
					iv_login_reg_mail_ph_ok.setBackgroundResource(R.drawable.ok);
					iv_login_reg_mail_ph_ok.setTag("ok");
				}else{
					Prompt.Dialog(this, false, "提示", "该用户名已注册", null);
					iv_login_reg_mail_ph_ok.setBackgroundResource(R.drawable.not);
					iv_login_reg_mail_ph_ok.setTag("not");
				}
			}else{
				iv_login_reg_mail_ph_ok.setBackgroundResource(R.drawable.not);
				iv_login_reg_mail_ph_ok.setTag("not");
			}
			break;
		case Requester2.RESPONSE_TYPE_CHECK_NICKNAME:
			iv_login_reg_nick_ok.setVisibility(View.VISIBLE);
			if(msg.obj!=null){
				GsonResponse2.checkNickNameExistResponse obj = (checkNickNameExistResponse) msg.obj;
				if("0".equals(obj.status)){
					iv_login_reg_nick_ok.setBackgroundResource(R.drawable.ok);
					iv_login_reg_nick_ok.setTag("ok");
				}else{
					Prompt.Dialog(this, false, "提示", "该昵称已注册", null);
					iv_login_reg_nick_ok.setBackgroundResource(R.drawable.not);
					iv_login_reg_nick_ok.setTag("not");
				}
			}else{
				iv_login_reg_nick_ok.setBackgroundResource(R.drawable.not);
				iv_login_reg_nick_ok.setTag("not");
			}
			break;
		case Requester2.RESPONSE_TYPE_CHECKNO:
			if(msg.obj!=null){
				GsonResponse2.checkNoResponse obj = (GsonResponse2.checkNoResponse)(msg.obj);
				if(obj!=null && obj.status!=null && obj.status.equals("0")){
					getHandler().sendEmptyMessage(HANDLER_FLAG_ENABLE_YZM);
					//Prompt.Alert(this, "验证码已发出，请注意查收");
					btn_yzm.setEnabled(false);
					ed_yzm.setEnabled(false);
					ed_mail_phone.setEnabled(false);
					Prompt.Dialog(LoginRegisterActivity.this, false, "提醒", "验证码已发出，请注意查收" , null);
				}else{
					btn_yzm.setEnabled(true);
					ed_yzm.setEnabled(true);
					Prompt.Dialog(LoginRegisterActivity.this, false, "提醒", Prompt.GetStatus(obj.status, obj.crm_status), null);
				}					
				
/*				btn_yzm.setEnabled(true);
				ed_yzm.setEnabled(true);*/

			}else{
				Prompt.Dialog(LoginRegisterActivity.this, false, "提醒", "网络异常" , null);
				btn_yzm.setEnabled(true);
				ed_yzm.setEnabled(true);
			}

			break;
		case HANDLER_FLAG_ENABLE_YZM:
			if(isPhoneNum){
				//tick = 90;
				btn_yzm.setEnabled(false);
				ed_yzm.setEnabled(true);
			    //getHandler().obtainMessage(HANDLER_FLAG_YZM_TICK).sendToTarget();
				TickDownHelper.getInstance(getHandler()).start(90);
			}
			break;
/*		case HANDLER_FLAG_YZM_TICK:
			btn_yzm.setBackgroundResource(R.drawable.hqyzm_wait);
			btn_yzm.setText("重新发送 " + tick + "秒");
			//tick--;
			
			
			if(tick==0){
				btn_yzm.setEnabled(true);
				btn_yzm.setText("");
				btn_yzm.setBackgroundResource(R.drawable.btn_activity_login_looklook_yzm);
			}else{
				getHandler().sendEmptyMessageDelayed(HANDLER_FLAG_YZM_TICK, 1000);
			}
			break;*/
		case TickDownHelper.HANDLER_FLAG_TICK_DOWN:
			int tick = (Integer)msg.obj;
			if(tick==0){
				//btn_yzm.setText("");
				//btn_yzm.setBackgroundResource(R.drawable.btn_activity_login_looklook_yzm);
				btn_yzm.setText(R.string.str_cxfs);
				ed_mail_phone.setEnabled(true);
				String a = ed_mail_phone.getText().toString();	
				if(Prompt.checkPhoneNum(a)){
					btn_yzm.setEnabled(true);
					Requester2.checkUserNameExist(getHandler(), a);
				}
			}else{
				btn_yzm.setText("重新发送( " + tick + "秒)");
				//btn_yzm.setBackgroundResource(R.drawable.hqyzm_wait);

			}
			break;
		case HANDLER_FLAG_HIDEPASS:
			ed_pass.setTransformationMethod(PasswordTransformationMethod
					.getInstance());
			ed_pass.postInvalidate();
			break;
		case Requester2.RESPONSE_TYPE_REGISTER : 
			ZDialog.dismiss();
			if(msg.obj!=null){
				GsonResponse2.registerResponse obj = (GsonResponse2.registerResponse)(msg.obj);

				if(obj.status!=null && obj.status.equals("0") && obj.userid!=null){//register success
					//ZToast.showShort("用户注册成功");
					//Prompt.Alert(this, "用户注册成功");
					CmmobiClickAgentWrapper.onEvent(this, "login_loc_success");					
					if(isPhoneNum){
						acct.snstype = "0";
						acct.snsid = input_mail_phone;
						acct.username = input_mail_phone;
						acct.password = input_passwd;
						Requester2.login(this, getHandler(), acct, "2");
					}else{
						Prompt.Dialog(LoginRegisterActivity.this, true, "激活looklook账号", "是否打开相应的邮箱，激活looklook账号", new OnClickListener() {
							
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
										LoginRegisterActivity.this.startActivity(intent);  
									}
								}catch(android.content.ActivityNotFoundException e){
									e.printStackTrace();
									Prompt.Alert(LoginRegisterActivity.this, "没有合适的外部浏览器打开该邮箱");
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
					Prompt.Dialog(LoginRegisterActivity.this, false, "提醒",  Prompt.GetStatus(obj.status, obj.crm_status), null);
				}

			

					
				
			}else{
				Prompt.Dialog(LoginRegisterActivity.this, false, "注册","网络异常", null);
			}
			break;
		case Requester2.RESPONSE_TYPE_LOGIN: // 登陆返回信息
			try {

				GsonResponse2.loginResponse obj = (GsonResponse2.loginResponse) (msg.obj);
				if(obj==null){
					//Prompt.Alert(this, "LookLook登陆，网络异常！");
					Prompt.Dialog(LoginRegisterActivity.this, false, "提醒", "网络异常", null);
					ActiveAccount.getInstance(this).logout();
				}else{
					if (obj.status.equals("0")) {
						if(Prompt.checkEmail(input_mail_phone)){
							ActiveAccount.getInstance(this).logintype = "1";
						}else{
							ActiveAccount.getInstance(this).logintype = "2";
						}
						
						if(ActiveAccount.getInstance(this).updateLogin(obj)){
							//Prompt.Alert(this, "LookLook登陆，登陆成功");
							//launchMainActivity();
							Intent mIntent = new Intent();
							setResult(RESULT_OK, mIntent);
							finish();
							ZDialog.dismiss();
							
						}else{
							ActiveAccount.getInstance(this).logout();
							//Prompt.Alert(this, "LookLook登陆，服务器返回失败");
							Prompt.Dialog(LoginRegisterActivity.this, false, "提醒", Prompt.GetStatus(obj.status, obj.crm_status), null);
						}

					} else{
						Prompt.Dialog(LoginRegisterActivity.this, false, "提醒", Prompt.GetStatus(obj.status, obj.crm_status), null);
						ActiveAccount.getInstance(this).logout();
					}
				}

			} catch (Exception e) {
				e.printStackTrace();
				//Prompt.Alert(this, "LookLook登陆，异常！");
				Prompt.Dialog(LoginRegisterActivity.this, false, "提醒", "LookLook登陆异常!" , null);
			}
			ZDialog.dismiss();
			break;
		}
		return false;
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.ll_login_reg:
			InputMethodManager imm = (InputMethodManager)getSystemService( Context.INPUT_METHOD_SERVICE);
			imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
			break;
		case R.id.btn_login_looklook_reg_back:
			Intent mIntent = new Intent();
			setResult(Activity.RESULT_CANCELED, mIntent);
			finish();
			overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
			break;
		case R.id.btn_login_reg_yzm:
			Requester2.getCheckNo(getHandler(), ed_mail_phone.getText().toString(), "1");
/*			ZDialog.show(R.layout.progressdialog, false, true, this);*/
			btn_yzm.setEnabled(false);
			ed_yzm.setEnabled(false);
			break;
		case R.id.btn_login_reg_ok:
			input_nickname = ed_nick.getText().toString();
			input_mail_phone = ed_mail_phone.getText().toString();
			input_passwd = ed_pass.getText().toString();
			input_yzm = ed_yzm.getText().toString();
			if(checkValid(input_nickname, input_mail_phone, input_passwd)){
				//ok
				ZDialog.show(R.layout.progressdialog, false, true, this);
//				ZLog.e("nickname:" +  input_nickname + " mail:" + input_mail + " pass:" + input_passwd);
				if(CommonInfo.getInstance().equipmentid==null){
					Requester2.submitUA(handler);
				}else{
					if(Prompt.checkEmail(input_mail_phone)){
						Requester2.register(handler, input_nickname, input_mail_phone, input_passwd, input_yzm, "1");	
					}else{
						Requester2.register(handler, input_nickname, input_mail_phone, input_passwd, input_yzm, "2");
					}
				}

				
			}

			break;
		case R.id.btn_login_reg_password:
/*			if (!mbDisplayFlg) {
				ed_pass.setTransformationMethod(HideReturnsTransformationMethod
						.getInstance());

			} else {
				ed_pass.setTransformationMethod(PasswordTransformationMethod
						.getInstance());
			}
			mbDisplayFlg = !mbDisplayFlg;*/
			ed_pass.setTransformationMethod(HideReturnsTransformationMethod
					.getInstance());
			ed_pass.postInvalidate();

			Message message = handler.obtainMessage(HANDLER_FLAG_HIDEPASS);
			handler.sendMessageDelayed(message, 3000);

			break;
		case R.id.ck_login_reg_agreement:
			if(((String)ck_agreement.getTag()).equals("normal")){
				ck_agreement.setBackgroundResource(R.drawable.xieyi_pressed);
				ck_agreement.setTag("checked");
				btn_ok.setEnabled(true);
				btn_ok.getBackground().setAlpha(255);
			}else{
				ck_agreement.setBackgroundResource(R.drawable.xieyi_normal);
				ck_agreement.setTag("normal");
				btn_ok.setEnabled(false);
				btn_ok.getBackground().setAlpha(180);
			}
			break;
		case R.id.tv_login_reg_agreement:
			launchAgreement();
			break;
		case R.id.iv_login_reg_mail_ph_ok:
			if("not".equals(iv_login_reg_mail_ph_ok.getTag())){
				iv_login_reg_mail_ph_ok.setVisibility(View.INVISIBLE);
				ed_mail_phone.requestFocus();
			}
			break;
		case R.id.iv_login_reg_nick_ok:
			if("not".equals(iv_login_reg_nick_ok.getTag())){
				iv_login_reg_nick_ok.setVisibility(View.INVISIBLE);
				ed_nick.requestFocus();
			}
			break;
		case R.id.iv_login_reg_yzm_ok:
			if("not".equals(iv_login_reg_yzm_ok.getTag())){
				iv_login_reg_yzm_ok.setVisibility(View.INVISIBLE);
				ed_yzm.requestFocus();
			}
			break;
		}
	}

	private void launchAgreement() {
		// TODO Auto-generated method stub

		Intent intent = new Intent(this, LoginAgreementActivity.class);
		startActivity(intent);
	}

	private boolean checkValid(String input_nickname, String input_mail_phone, String input_passwd) {
		// TODO Auto-generated method stub

		if(!(Prompt.checkEmail(input_mail_phone)||Prompt.checkPhoneNum(input_mail_phone))){
			//Prompt.Alert(this, "请输入合法的邮箱或手机号");
			Prompt.Dialog(LoginRegisterActivity.this, false, "提醒", "请输入合法的邮箱或手机号", null);
			return false;
		}
		
		if(input_nickname==null || input_nickname.length()<3){
			//Prompt.Alert(this, "请输入合法的用户昵称");
			Prompt.Dialog(LoginRegisterActivity.this, false, "提醒", "请输入合法的用户昵称（大于3个字符）", null);
			return false;
		}
		

		
		if(!Prompt.checkPassword(input_passwd)){
			//Prompt.Alert(this, "请输入合法的密码（6-16字符）");
			Prompt.Dialog(LoginRegisterActivity.this, false, "提醒", "请输入合法的密码（6-16字符）", null);
			return false;
		}
		
		return true;
	}

}
