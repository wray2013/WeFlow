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
 * @date  2015-03-13
 */
public class ChangePasswordActivity extends TitleRootActivity{

	private String TAG = "ChangePasswordActivity";

	private EditText etOldPwd;
	private EditText etNewPwd;
	private TextView tvForgetPwd;
	
	@Override
	public int subContentViewId() {
		return R.layout.activity_change_password;
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setTitleText("箩筐密码");
		setRightButtonText("完成");
		initView();
	}
	
	private void initView(){
		ViewUtils.setMarginTop(findViewById(R.id.rl_content), 16);
		ViewUtils.setMarginLeft(findViewById(R.id.rl_content), 12);
		ViewUtils.setMarginRight(findViewById(R.id.rl_content), 12);
		
	
		etOldPwd = (EditText) findViewById(R.id.et_oldpwd);
		ViewUtils.setHeight(etOldPwd, 99);
		ViewUtils.setMarginLeft(etOldPwd, 12);
		ViewUtils.setMarginRight(etOldPwd, 12);
		ViewUtils.setMarginTop(etOldPwd, 30);
		
		etNewPwd = (EditText) findViewById(R.id.et_newpwd);
		ViewUtils.setHeight(etNewPwd, 99);
		ViewUtils.setMarginLeft(etNewPwd, 12);
		ViewUtils.setMarginRight(etNewPwd, 12);
		ViewUtils.setMarginTop(etNewPwd, 30);
		
		etNewPwd.addTextChangedListener(new LoginTextWatcher(etNewPwd));
		etOldPwd.addTextChangedListener(new LoginTextWatcher(etOldPwd));
		
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
			if(et.getId() == R.id.et_oldpwd){
				
			}else if(et.getId() == R.id.et_newpwd){
				
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
			if(TextUtils.isEmpty(etOldPwd.getText().toString())){
				etOldPwd.setHintTextColor(0xffc60606);
				etOldPwd.setFocusable(true);
				etOldPwd.requestFocus();
				imm.showSoftInput(etOldPwd, 0);
			}else if(TextUtils.isEmpty(etNewPwd.getText().toString())){
				etNewPwd.setHintTextColor(0xffc60606);
				etNewPwd.requestFocus();
				imm.showSoftInput(etNewPwd, 0);
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
