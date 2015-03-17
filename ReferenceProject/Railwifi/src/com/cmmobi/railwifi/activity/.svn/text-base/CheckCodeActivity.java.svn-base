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
public class CheckCodeActivity extends TitleRootActivity{

	private String TAG = "CheckCodeActivity";

	private EditText etCode;
	private Button btnGetCode;
	
	@Override
	public int subContentViewId() {
		return R.layout.activity_check_code;
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setTitleText("完成注册");
		setRightButtonText("完成");
		initView();
	}
	
	private void initView(){
		ViewUtils.setMarginTop(findViewById(R.id.rl_content), 16);
		ViewUtils.setMarginLeft(findViewById(R.id.rl_content), 12);
		ViewUtils.setMarginRight(findViewById(R.id.rl_content), 12);
		
		
		
		etCode = (EditText) findViewById(R.id.et_code);
		ViewUtils.setHeight(etCode, 99);
		ViewUtils.setMarginLeft(etCode, 12);
		ViewUtils.setMarginRight(etCode, 12);
		ViewUtils.setMarginTop(etCode, 30);
		
		etCode.addTextChangedListener(new LoginTextWatcher(etCode));
		
		btnGetCode = (Button)findViewById(R.id.btn_get_code);
		ViewUtils.setSize(btnGetCode, 350, 84);
		ViewUtils.setMarginTop(btnGetCode, 50);
		ViewUtils.setMarginBottom(btnGetCode, 60);
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
			if(et.getId() == R.id.et_code){
				etCode.setHint("请输入短信验证码");
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
			if(TextUtils.isEmpty(etCode.getText().toString().trim())){
				etCode.setHintTextColor(0xffc60606);
				etCode.setFocusable(true);
				etCode.requestFocus();
				imm.showSoftInput(etCode, 0);
			}else if(!checkCode(etCode.getText().toString().trim())){
				etCode.setText("");
				etCode.setHint("验证码错误");
				etCode.setHintTextColor(0xffc60606);
				etCode.requestFocus();
				imm.showSoftInput(etCode, 0);
			}else{
				this.finish();
			}
			break;
			default:{
				
			}	
		}
	}	
	
	private Boolean checkCode(String code){
		if("1234".equals(code)){
			return true;
		}
		return false;
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
