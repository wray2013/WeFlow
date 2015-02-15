package com.cmmobi.looklook.activity.login;

import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.cmmobi.looklook.R;
import com.cmmobi.looklook.activity.TitleRootActivity;
import com.cmmobi.looklook.common.gson.Requester3;
import com.cmmobi.looklook.prompt.Prompt;

public class RegisterMicShareActivity extends TitleRootActivity {

	private EditText mEditUsername;
	private EditText mEditPwd;
	private TextView mTvUserStateOk;
	private TextView mTvUserStateError;
	private TextView mTvPwdStateOk;
	private TextView mTvPwdStateError;
	private Button mBtnPwdEye;
	
	@Override
	public int subContentViewId() {
		return R.layout.activity_register_micshare;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setTitle("密码设置");
		hideLeftButton();
		setRightButtonText("完成注册");
		setBtnRegEnabled(false);
		
		mBtnPwdEye = (Button) findViewById(R.id.btn_pwd_eye);
		mEditUsername     = (EditText) findViewById(R.id.edit_username);
		mEditPwd 		  = (EditText) findViewById(R.id.edit_pwd);
		mTvUserStateOk    = (TextView) findViewById(R.id.tv_username_ok);
		mTvUserStateError = (TextView) findViewById(R.id.tv_username_error);
		mTvPwdStateOk    = (TextView) findViewById(R.id.tv_pwd_ok);
		mTvPwdStateError = (TextView) findViewById(R.id.tv_pwd_error);
		
		
		mBtnPwdEye.setOnClickListener(this);
		findViewById(R.id.tv_binding_phoneno).setOnClickListener(this);
		
		
		mEditUsername.setOnFocusChangeListener(mUNameFocusListener);
		mEditUsername.addTextChangedListener(mUNameTextWatcher);
		mEditPwd.addTextChangedListener(mPwdTextWatcher);
		
	}
	
	@Override
	public void onClick(View v) {
		Intent intent = null;
		switch (v.getId()) {
		case R.id.btn_title_right:
			
			
			
			break;
		
		case R.id.btn_pwd_eye:
			mEditPwd.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
			mEditPwd.postInvalidate();
			mEditPwd.postDelayed(new Runnable() {
				@Override
				public void run() {
					mEditPwd.setTransformationMethod(PasswordTransformationMethod.getInstance());
					mEditPwd.postInvalidate();
				}
			}, 3000);
			break;
		case R.id.tv_binding_phoneno:
			intent = new Intent(this,BindingMobileNoActivity.class);
			break;
			
		default:
			break;
		}
		if(intent != null){
			startActivity(intent);
		}
		super.onClick(v);
	}
	
	
	@Override
	public boolean handleMessage(Message msg) {
		switch (msg.what) {
//		case value:
//			
//			break;
		default:
			break;
		}
		return false;
	}
	
	
	
	private View.OnFocusChangeListener mUNameFocusListener = new View.OnFocusChangeListener(){
		@Override
		public void onFocusChange(View v, boolean hasFocus) {
			if(!hasFocus){
				if(mEditUsername.getText().toString()!=null && !("".equals(mEditUsername.getText().toString()))){
//					Requester3.checkUserNameExist(getHandler(), a);
				}
			}
		}
	};
	
	
	
	private TextWatcher mUNameTextWatcher = new TextWatcher(){
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
			String username = mEditUsername.getText().toString().trim();
			if(!TextUtils.isEmpty(username)){
				if(Prompt.checkUserName(username) && username.length()>=2){
					showUNameStataOk();
				}else{
					showUNameStataError();
				}
			}else{
				clearUNameStata();
			}
		}
    };
    
    private TextWatcher mPwdTextWatcher = new TextWatcher(){
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
    		String pwd = mEditPwd.getText().toString().trim();
    		if(!TextUtils.isEmpty(pwd)){
    			if(Prompt.checkPassword(pwd) && pwd.length()>=6){
    				showPwdStataOk();
    			}else{
    				showPwdStataError();
    			}
    		}else{
    			clearPwdStata();
    		}
    	}
    };
    
    
    /**
     * 检查注册按钮状态
     */
    private void checkRegisterBtnState(){
    	if(mTvUserStateOk.isShown() && mTvPwdStateOk.isShown()){
    		setBtnRegEnabled(true);
    	}else{
    		setBtnRegEnabled(false);
    	}
    }
    
    /**
     * 更具状态设置注册按钮
     * @param enabled
     */
    private void setBtnRegEnabled(boolean enabled){
    	if(enabled){
    		getRightButton().setTextColor(this.getResources().getColor(R.color.blue));
    	}else{
    		getRightButton().setTextColor(this.getResources().getColor(R.color.gray));
    	}
    	getRightButton().setEnabled(enabled);
    }
    
    /**
     * 显示用户名状态OK
     */
    private void showUNameStataOk(){
    	mTvUserStateOk.setVisibility(View.VISIBLE);
    	mTvUserStateError.setVisibility(View.GONE);
    	checkRegisterBtnState();
    }
    
    /**
     * 清楚用户名状态
     */
    private void clearUNameStata(){
    	mTvUserStateOk.setVisibility(View.GONE);
    	mTvUserStateError.setVisibility(View.GONE);
    	checkRegisterBtnState();
    }
	
    /**
     * 显示用户名状态错误
     */
    private void showUNameStataError(){
    	mTvUserStateOk.setVisibility(View.GONE);
    	mTvUserStateError.setVisibility(View.VISIBLE);
    	mTvUserStateError.setText("账号格式错误");
    	checkRegisterBtnState();
    }
    
    /**
     * 显示用户名已存在状态
     */
    private void showUNameStataExist(){
    	mTvUserStateOk.setVisibility(View.GONE);
    	mTvUserStateError.setVisibility(View.VISIBLE);
    	mTvUserStateError.setText("账号已被占用");
    	checkRegisterBtnState();
    }
    
    
    
    /**
     * 显示密码状态ok
     */
    private void showPwdStataOk(){
    	mBtnPwdEye.setVisibility(View.VISIBLE);
    	mTvPwdStateError.setVisibility(View.GONE);
    	mTvPwdStateOk.setVisibility(View.VISIBLE);
    	checkRegisterBtnState();
    }
    
    /**
     * 清楚密码状态
     */
    private void clearPwdStata(){
    	mBtnPwdEye.setVisibility(View.VISIBLE);
    	mTvPwdStateOk.setVisibility(View.GONE);
    	mTvPwdStateError.setVisibility(View.GONE);
    	checkRegisterBtnState();
    }
    
    /**
     * 显示密码状态错误
     */
    private void showPwdStataError(){
    	mBtnPwdEye.setVisibility(View.INVISIBLE);
    	mTvPwdStateOk.setVisibility(View.GONE);
    	mTvPwdStateError.setVisibility(View.VISIBLE);
    	checkRegisterBtnState();
    }
    
}
