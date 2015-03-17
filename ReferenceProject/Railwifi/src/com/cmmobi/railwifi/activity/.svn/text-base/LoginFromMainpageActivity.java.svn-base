package com.cmmobi.railwifi.activity;

import java.util.ArrayList;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Message;
import android.text.Editable;
import android.text.Html;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cmmobi.railwifi.R;
import com.cmmobi.railwifi.adapter.MusicMainPageListAdapter;
import com.cmmobi.railwifi.dialog.PromptDialog;
import com.cmmobi.railwifi.music.MusicService;
import com.cmmobi.railwifi.network.GsonResponseObject;
import com.cmmobi.railwifi.network.GsonResponseObject.AlumbElem;
import com.cmmobi.railwifi.network.GsonResponseObject.MusicElem;
import com.cmmobi.railwifi.network.Requester;
import com.cmmobi.railwifi.utils.DisplayUtil;
import com.cmmobi.railwifi.utils.ViewUtils;
import com.cmmobi.railwifi.view.MusicControllerView;
import com.google.gson.Gson;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.nostra13.universalimageloader.api.MyImageLoader;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;

/**
 * @author youtian
 * @email youtian@cmmobi.com
 * @date  2015-03-10
 */
public class LoginFromMainpageActivity extends TitleRootActivity{

	private String TAG = "LoginFromMainpageActivity";

	private ImageView ivAccountError;
	private EditText etAccount;
	private EditText etPassword;
	private TextView tvForgetPwd;
	
	@Override
	public int subContentViewId() {
		return R.layout.activity_login_from_mainpage;
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setTitleText("登录箩筐账号");
		setRightButtonText("完成");
		initView();
	}
	
	private void initView(){
		ViewUtils.setMarginTop(findViewById(R.id.rl_content), 16);
		ViewUtils.setMarginLeft(findViewById(R.id.rl_content), 12);
		ViewUtils.setMarginRight(findViewById(R.id.rl_content), 12);
		
		ViewUtils.setHeight(findViewById(R.id.tv_account), 60);
		ViewUtils.setMarginLeft(findViewById(R.id.tv_account), 12);
		ViewUtils.setTextSize((TextView)findViewById(R.id.tv_account), 30);

		ivAccountError  = (ImageView) findViewById(R.id.iv_account_error);
		RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) ivAccountError.getLayoutParams();

		lp = (RelativeLayout.LayoutParams) ivAccountError.getLayoutParams();
		lp.height = DisplayUtil.getSize(this, 64);
		lp.width = lp.height;
		lp.rightMargin = DisplayUtil.getSize(this, 12);
		ivAccountError.setLayoutParams(lp);
		
		etAccount = (EditText) findViewById(R.id.et_account);
		ViewUtils.setHeight(etAccount, 99);
		ViewUtils.setMarginLeft(etAccount, 12);
		ViewUtils.setMarginRight(etAccount, 12);
		
		etPassword = (EditText) findViewById(R.id.et_password);
		ViewUtils.setHeight(etPassword, 99);
		ViewUtils.setMarginLeft(etPassword, 12);
		ViewUtils.setMarginRight(etPassword, 12);
		ViewUtils.setMarginTop(etPassword, 30);
		
		etAccount.addTextChangedListener(new LoginTextWatcher(etAccount));
		etPassword.addTextChangedListener(new LoginTextWatcher(etPassword));
		
		tvForgetPwd = (TextView)findViewById(R.id.tv_forgetpassword);
		ViewUtils.setTextSize(tvForgetPwd, 25);
		tvForgetPwd.setText(Html.fromHtml("<u>忘记密码？</u>"));
		ViewUtils.setHeight(tvForgetPwd, 90);
		tvForgetPwd.setOnClickListener(this);
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
			}else if(et.getId() == R.id.et_password){
				
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
			}else if(TextUtils.isEmpty(etPassword.getText().toString())){
				etPassword.setHint("请输入密码");
				etPassword.setHintTextColor(0xffc60606);
				etPassword.requestFocus();
				imm.showSoftInput(etPassword, 0);
			}else{
				Intent checkCodeIntent = new Intent(this, CheckCodeActivity.class);
				startActivity(checkCodeIntent);
				this.finish();
			}
			break;
		case R.id.tv_forgetpassword:
			
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
