package com.etoc.weflow.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.etoc.weflow.R;
import com.etoc.weflow.WeFlowApplication;
import com.etoc.weflow.dao.AccountInfo;
import com.etoc.weflow.net.GsonResponseObject.NickNameResp;
import com.etoc.weflow.net.Requester;
import com.etoc.weflow.utils.DisplayUtil;
import com.etoc.weflow.utils.ViewUtils;

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
		setRightButtonText("提交");
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
			finish();
			break;
		case R.id.btn_title_right:
			InputMethodManager imm = (InputMethodManager) this
			  .getSystemService(Context.INPUT_METHOD_SERVICE);
			if(TextUtils.isEmpty(etNickname.getText().toString().trim())){
				etNickname.setHintTextColor(0xffc60606);
				etNickname.setFocusable(true);
				etNickname.requestFocus();
				imm.showSoftInput(etNickname, 0);
			}else{
				Requester.changeNickName(true, handler, WeFlowApplication.getAppInstance().getAccountInfo().getUserid(), etNickname.getText().toString());
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
		case Requester.RESPONSE_TYPE_NICKNAME_CHANGE:
			if (msg.obj != null) {
				NickNameResp resp = (NickNameResp) msg.obj;
				if (Requester.isSuccessed(resp.status)) {
					Intent intent = new Intent();
					intent.putExtra("nickname", etNickname.getText().toString().trim());
					AccountInfo accountInfo = WeFlowApplication.getAppInstance().getAccountInfo();
					accountInfo.setNickname(etNickname.getText().toString().trim());
					WeFlowApplication.getAppInstance().PersistAccountInfo(accountInfo);
					this.setResult(RESULT_OK, intent);
					this.finish();
				}
			}
			break;
		default:
			break;
		}
		return false;
	}

}
