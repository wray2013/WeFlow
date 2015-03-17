package com.cmmobi.railwifi.activity;

import android.content.Context;
import android.os.Bundle;
import android.os.Message;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cmmobi.railwifi.R;
import com.cmmobi.railwifi.dialog.PromptDialog;
import com.cmmobi.railwifi.utils.DisplayUtil;
import com.cmmobi.railwifi.utils.ViewUtils;

/**
 * @author youtian
 * @email youtian@cmmobi.com
 * @date  2015-03-13
 */
public class AccountChangeActivity extends TitleRootActivity{

	private String TAG = "AccountChangeActivity";

	private ImageView ivAccountError;
	private EditText etAccount;
	private EditText etNewAccount;
	private ImageView ivNewAccountError;
	private EditText etCode;
	private Button btnGetCode;
	
	@Override
	public int subContentViewId() {
		return R.layout.activity_account_change;
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setTitleText("箩筐账号");
		setRightButtonText("完成");
		initView();
	}
	
	private void initView(){
		ViewUtils.setMarginTop(findViewById(R.id.rl_content), 16);
		ViewUtils.setMarginLeft(findViewById(R.id.rl_content), 12);
		ViewUtils.setMarginRight(findViewById(R.id.rl_content), 12);
		
		ivAccountError  = (ImageView) findViewById(R.id.iv_account_error);
		RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) ivAccountError.getLayoutParams();
		lp = (RelativeLayout.LayoutParams) ivAccountError.getLayoutParams();
		lp.height = DisplayUtil.getSize(this, 64);
		lp.width = lp.height;
		lp.rightMargin = DisplayUtil.getSize(this, 12);
		ivAccountError.setLayoutParams(lp);
		
		ivNewAccountError  = (ImageView) findViewById(R.id.iv_newaccount_error);
		lp = (RelativeLayout.LayoutParams) ivNewAccountError.getLayoutParams();
		lp = (RelativeLayout.LayoutParams) ivAccountError.getLayoutParams();
		lp.height = DisplayUtil.getSize(this, 64);
		lp.width = lp.height;
		lp.rightMargin = DisplayUtil.getSize(this, 12);
		ivNewAccountError.setLayoutParams(lp);
		
		etAccount = (EditText) findViewById(R.id.et_account);
		ViewUtils.setHeight(etAccount, 99);
		ViewUtils.setMarginLeft(etAccount, 12);
		ViewUtils.setMarginRight(etAccount, 12);
		ViewUtils.setMarginTop(etAccount, 12);
		
		etNewAccount = (EditText) findViewById(R.id.et_newaccount);
		ViewUtils.setHeight(etNewAccount, 99);
		ViewUtils.setMarginLeft(etNewAccount, 12);
		ViewUtils.setMarginRight(etNewAccount, 12);
		ViewUtils.setMarginTop(etNewAccount, 30);
				
		etAccount.addTextChangedListener(new LoginTextWatcher(etAccount));
		etNewAccount.addTextChangedListener(new LoginTextWatcher(etNewAccount));
		
		etCode = (EditText) findViewById(R.id.et_code);
		ViewUtils.setHeight(etCode, 99);
		ViewUtils.setMarginLeft(etCode, 12);
		ViewUtils.setMarginRight(etCode, 12);
		ViewUtils.setMarginTop(etCode, 50);
		ViewUtils.setMarginBottom(etCode, 70);
		
		etCode.addTextChangedListener(new LoginTextWatcher(etCode));
		
		btnGetCode = (Button)findViewById(R.id.btn_get_code);
		ViewUtils.setSize(btnGetCode, 350, 84);
		ViewUtils.setMarginTop(btnGetCode, 50);
		btnGetCode.setTextSize(DisplayUtil.textGetSizeSp(this, 33));
	}
		
	private class LoginTextWatcher implements TextWatcher{
		private EditText et;
		public LoginTextWatcher(EditText et) {
			// TODO Auto-generated constructor stub
			this.et = et;
		}
		
		@Override
		public void onTextChanged(CharSequence s, int start, int before, int count) {
			// TODO Auto-generated method stub
			et.setHintTextColor(0xff888888);
			if(et.getId() == R.id.et_account){
				ivAccountError.setVisibility(View.GONE);
			}else if(et.getId() == R.id.et_code){
				
			}
		}
		
		@Override
		public void beforeTextChanged(CharSequence s, int start, int count,
				int after) {
			// TODO Auto-generated method stub
			
		}
		
		@Override
		public void afterTextChanged(Editable s) {
			// TODO Auto-generated method stub
			
		}
	};
	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();	
	}
	
	@Override
	public void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
	}

	
	
	
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		super.onClick(v);
		switch(v.getId()){
		case R.id.btn_title_right:
			InputMethodManager imm = (InputMethodManager) this
			  .getSystemService(Context.INPUT_METHOD_SERVICE);
			if(TextUtils.isEmpty(etAccount.getText().toString())){
				etAccount.setHintTextColor(0xffc60606);
				etAccount.setFocusable(true);
				etAccount.requestFocus();
				imm.showSoftInput(etAccount, 0);
			}else if(!PromptDialog.checkPhoneNum(etAccount.getText().toString())){
				ivAccountError.setVisibility(View.VISIBLE);
				ivAccountError.requestFocus();
				imm.showSoftInput(ivAccountError, 0);
			}else if(TextUtils.isEmpty(etNewAccount.getText().toString())){
				etNewAccount.setHintTextColor(0xffc60606);
				etNewAccount.requestFocus();
				imm.showSoftInput(etNewAccount, 0);
			}else{
				
				this.finish();
			}
			break;
			default:{
				
			}	
		}
	}	
	
	
	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}

	@Override
	public boolean handleMessage(Message msg) {
		// TODO Auto-generated method stub
		switch (msg.what) {
		default:
			break;
		}
		return false;
	}

}
