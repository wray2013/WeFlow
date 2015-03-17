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
public class NicknameActivity extends TitleRootActivity{

	private String TAG = "NicknameActivity";

	private ImageView ivInputCancel;
	private EditText etNickname;
	
	@Override
	public int subContentViewId() {
		return R.layout.activity_nickname;
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setTitleText("昵称");
		hideRightButton();
		initView();
	}
	
	private void initView(){
		ViewUtils.setMarginTop(findViewById(R.id.rl_content), 16);
		ViewUtils.setMarginLeft(findViewById(R.id.rl_content), 12);
		ViewUtils.setMarginRight(findViewById(R.id.rl_content), 12);
		
		ivInputCancel  = (ImageView) findViewById(R.id.iv_input_cancel);
		RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) ivInputCancel.getLayoutParams();

		lp = (RelativeLayout.LayoutParams) ivInputCancel.getLayoutParams();
		lp.height = DisplayUtil.getSize(this, 60);
		lp.width = lp.height;
		lp.rightMargin = DisplayUtil.getSize(this, 12);
		ivInputCancel.setLayoutParams(lp);
		ivInputCancel.setOnClickListener(this);
		
		etNickname = (EditText) findViewById(R.id.et_nickname);
		ViewUtils.setHeight(etNickname, 99);
		ViewUtils.setMarginTop(etNickname, 26);
		ViewUtils.setMarginLeft(etNickname, 12);
		ViewUtils.setMarginRight(etNickname, 12);
		
		if(getIntent().getStringExtra("nickname")!= null){
			etNickname.setText(getIntent().getStringExtra("nickname"));
			etNickname.setSelection(etNickname.getText().length());
		}
		etNickname.addTextChangedListener(new LoginTextWatcher(etNickname));
		
	
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
			if(et.getId() == R.id.et_nickname){
				
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
		switch(v.getId()){
		case R.id.btn_title_left:
			InputMethodManager imm = (InputMethodManager) this
			  .getSystemService(Context.INPUT_METHOD_SERVICE);
			if(TextUtils.isEmpty(etNickname.getText().toString().trim())){
				etNickname.setHintTextColor(0xffc60606);
				etNickname.setFocusable(true);
				etNickname.requestFocus();
				imm.showSoftInput(etNickname, 0);
			}else{
				Intent intent = new Intent();
				intent.putExtra("nickname", etNickname.getText().toString().trim());
				this.setResult(RESULT_OK, intent);
				this.finish();
			}
			break;
		case R.id.iv_input_cancel:
			etNickname.setText("");
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
