package com.etoc.weflow.activity.login;

import java.util.List;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.etoc.weflow.R;
import com.etoc.weflow.activity.MainActivity;
import com.etoc.weflow.activity.TitleRootActivity;
import com.etoc.weflow.dao.AccountInfo;
import com.etoc.weflow.dao.AccountInfoDao;
import com.etoc.weflow.dao.DaoMaster;
import com.etoc.weflow.dao.DaoSession;
import com.etoc.weflow.dao.DaoMaster.DevOpenHelper;
import com.etoc.weflow.dialog.PromptDialog;
import com.etoc.weflow.net.GsonResponseObject.loginResponse;
import com.etoc.weflow.net.Requester;
import com.etoc.weflow.utils.ViewUtils;

public class LoginActivity extends TitleRootActivity {

	private DaoMaster daoMaster;
	private DaoSession daoSession;
	private SQLiteDatabase db;
	private AccountInfoDao accountInfoDao;
	
	private AccountInfo accountinfo;
	
	private EditText edAccount, edPass;
	private TextView tvForget, tvBtnLogin, tvRegister;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setTitleText("登录");
		hideRightButton();
		
		initView();
		
		if(getIntent() != null) {
			String tel = getIntent().getStringExtra("tel");
			if(tel != null && !tel.equals("")) {
				edAccount.setText(tel);
			}
		}
		
		DevOpenHelper helper = new DaoMaster.DevOpenHelper(this, "weflowdb", null);
        db = helper.getWritableDatabase();
        daoMaster = new DaoMaster(db);
        daoSession = daoMaster.newSession();
        
        accountInfoDao = daoSession.getAccountInfoDao();
		if(accountInfoDao.count() > 0) {
			List<AccountInfo> list = accountInfoDao.loadAll();
			accountinfo = list.get(0);
		} else {
			accountinfo = new AccountInfo();
		}
	}
	
	
	private void initView() {
		// TODO Auto-generated method stub
		edAccount = (EditText) findViewById(R.id.et_account);
		edPass = (EditText) findViewById(R.id.et_password);
		edAccount.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence s, int arg1, int arg2, int arg3) {
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int arg1, int arg2,
					int arg3) {
			}
			
			@Override
			public void afterTextChanged(Editable s) {
				refreshBtnStatus();
			}
		});
		
		edPass.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence s, int arg1, int arg2, int arg3) {
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int arg1, int arg2,
					int arg3) {
			}
			
			@Override
			public void afterTextChanged(Editable s) {
				refreshBtnStatus();
			}
		});
		
		tvForget   = (TextView) findViewById(R.id.tv_forget_pass);
		tvBtnLogin = (TextView) findViewById(R.id.tv_login_btn);
		tvRegister = (TextView) findViewById(R.id.tv_register);
		tvBtnLogin.setEnabled(false);
		
		tvForget.setOnClickListener(this);
		tvBtnLogin.setOnClickListener(this);
		tvRegister.setOnClickListener(this);
		
		ViewUtils.setHeight(findViewById(R.id.et_account), 113);
		ViewUtils.setHeight(findViewById(R.id.et_password), 113);
		ViewUtils.setHeight(findViewById(R.id.tv_login_btn), 113);
		
		ViewUtils.setMarginTop(findViewById(R.id.et_account), 32);
		ViewUtils.setMarginLeft(findViewById(R.id.et_account), 32);
		ViewUtils.setMarginRight(findViewById(R.id.et_account), 32);
		
		ViewUtils.setMarginTop(findViewById(R.id.et_password), 36);
		ViewUtils.setMarginLeft(findViewById(R.id.et_password), 32);
		ViewUtils.setMarginRight(findViewById(R.id.et_password), 32);
		
		ViewUtils.setMarginTop(findViewById(R.id.rl_login_bottom), 36);
		ViewUtils.setMarginLeft(findViewById(R.id.tv_forget_pass), 32);
		ViewUtils.setMarginRight(findViewById(R.id.tv_forget_pass), 32);
		
		ViewUtils.setMarginTop(findViewById(R.id.tv_login_btn), 36);
		ViewUtils.setMarginLeft(findViewById(R.id.tv_login_btn), 32);
		ViewUtils.setMarginRight(findViewById(R.id.tv_login_btn), 32);
		
		ViewUtils.setMarginTop(findViewById(R.id.tv_register), 36);
		ViewUtils.setMarginLeft(findViewById(R.id.tv_register), 32);
		ViewUtils.setMarginRight(findViewById(R.id.tv_register), 32);
		
		ViewUtils.setTextSize(edAccount, 32);
		ViewUtils.setTextSize(edPass, 32);
		ViewUtils.setTextSize(tvBtnLogin, 32);
		ViewUtils.setTextSize(tvForget, 28);
		ViewUtils.setTextSize(tvRegister, 28);
		
		refreshBtnStatus();
	}
	
	private void refreshBtnStatus() {
		String a = edAccount.getText().toString();
		String p = edPass.getText().toString();
		if(a != null && p != null
				&& a.length() > 0 && p.length() > 0) {
			tvBtnLogin.setEnabled(true);
		} else {
			tvBtnLogin.setEnabled(false);
		}
	}
	
	@Override
	protected int graviteType() {
		// TODO Auto-generated method stub
		return GRAVITE_LEFT;
	}
	
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch(v.getId()) {
		case R.id.tv_forget_pass:
			Intent forgetIntent = new Intent(this, RegisterResetActivity.class);
			forgetIntent.putExtra("type", RegisterResetActivity.TYPE_RESET);
			startActivity(forgetIntent);
			break;
		case R.id.tv_login_btn:
			Requester.login(handler, edAccount.getText().toString(), edPass.getText().toString());
			break;
		case R.id.tv_register:
			Intent registerIntent = new Intent(this, RegisterResetActivity.class);
			registerIntent.putExtra("type", RegisterResetActivity.TYPE_REGIST);
			startActivity(registerIntent);
			break;
		}
		super.onClick(v);
	}


	@Override
	public boolean handleMessage(Message msg) {
		// TODO Auto-generated method stub
		switch(msg.what) {
		case Requester.RESPONSE_TYPE_LOGIN:
			loginResponse loginResp = (loginResponse) msg.obj;
			if(loginResp != null) {
				if("0".equals(loginResp.status) || "0000".equals(loginResp.status)) { //登录成功
					PromptDialog.Alert(LoginActivity.class, "登录成功");
					accountinfo.setIsregistration(loginResp.isregistration);
					accountinfo.setFlowcoins(loginResp.flowcoins);
					accountinfo.setMakeflow(loginResp.makeflow);
					accountinfo.setUseflow(loginResp.useflow);
					accountinfo.setUserid(loginResp.userid);
					accountinfo.setTel(loginResp.tel);
//					accountInfoDao.deleteAll();
					accountInfoDao.insertOrReplace(accountinfo);
					
					Intent intent = new Intent();
					intent.setClass(this, MainActivity.class);
					intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
					startActivity(intent);
					finish();
				}
			} else {
				PromptDialog.Alert(LoginActivity.class, "您的网络不给力啊！");
			}
			break;
		}
		return false;
	}

	@Override
	public int subContentViewId() {
		// TODO Auto-generated method stub
		return R.layout.activity_login;
	}
	
	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		if(db != null) db.close();
	}

}
