package com.etoc.weflow.activity;

import com.etoc.weflow.R;
import com.etoc.weflow.WeFlowApplication;
import com.etoc.weflow.activity.login.LoginActivity;
import com.etoc.weflow.activity.login.RegisterResetActivity;
import com.etoc.weflow.dao.AccountInfoDao;
import com.etoc.weflow.dao.DaoMaster;
import com.etoc.weflow.dao.DaoSession;
import com.etoc.weflow.dao.DaoMaster.DevOpenHelper;
import com.etoc.weflow.utils.ViewUtils;

import android.app.Activity;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Message;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class AccountActivity extends TitleRootActivity {

	private String tel = "";
	private String nickName = "";
	
	private TextView tvTel;
	private TextView tvNick;
	
	private DaoMaster daoMaster;
	private DaoSession daoSession;
	private SQLiteDatabase db;
	private AccountInfoDao accountInfoDao;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		Intent i = getIntent();
		if(i != null) {
			tel = i.getStringExtra("tel");
		}
		
		DevOpenHelper helper = new DaoMaster.DevOpenHelper(this, "weflowdb", null);
        db = helper.getWritableDatabase();
        daoMaster = new DaoMaster(db);
        daoSession = daoMaster.newSession();
        
        accountInfoDao = daoSession.getAccountInfoDao();
        nickName = WeFlowApplication.getAppInstance().getAccountInfo().getNickname();
		initViews();
	}
	
	private void initViews() {
		
		setTitleText("个人设置");
		hideRightButton();
		RelativeLayout rlPassword = (RelativeLayout) findViewById(R.id.rl_account_password);
		RelativeLayout rlLoginOut = (RelativeLayout) findViewById(R.id.rl_login_out);
		RelativeLayout rlNickName = (RelativeLayout) findViewById(R.id.rl_account_nickname);
		
		rlPassword.setOnClickListener(this);
		rlLoginOut.setOnClickListener(this);
		rlNickName.setOnClickListener(this);
		
		ViewUtils.setHeight(findViewById(R.id.rl_account_phone), 112);
		ViewUtils.setHeight(findViewById(R.id.rl_account_password), 112);
		ViewUtils.setHeight(findViewById(R.id.rl_account_nickname), 112);
		ViewUtils.setHeight(findViewById(R.id.rl_login_out), 112);
		
		
		ViewUtils.setMarginLeft(findViewById(R.id.tv_phone_label), 32);
		ViewUtils.setMarginRight(findViewById(R.id.rl_account_phone), 32);
		ViewUtils.setMarginRight(findViewById(R.id.view_password_flag), 32);
		ViewUtils.setMarginLeft(findViewById(R.id.tv_password_label), 32);
		ViewUtils.setMarginLeft(findViewById(R.id.tv_nickname_label), 32);
		ViewUtils.setMarginLeft(findViewById(R.id.tv_login_out), 32);
		ViewUtils.setMarginTop(findViewById(R.id.rl_login_out), 32);
		ViewUtils.setMarginLeft(findViewById(R.id.view_line), 32);
		ViewUtils.setMarginLeft(findViewById(R.id.view_line2), 32);
		
		ViewUtils.setTextSize(findViewById(R.id.tv_phone_label), 30);
		ViewUtils.setTextSize(findViewById(R.id.tv_phone_num), 30);
		ViewUtils.setTextSize(findViewById(R.id.tv_nickname_label), 30);
		ViewUtils.setTextSize(findViewById(R.id.tv_nickname), 30);
		ViewUtils.setTextSize(findViewById(R.id.tv_password_label), 30);
		ViewUtils.setTextSize(findViewById(R.id.tv_login_out), 30);
		
		tvTel = (TextView) findViewById(R.id.tv_phone_number);
		tvTel.setText(tel);
		
		tvNick = (TextView) findViewById(R.id.tv_nickname);
		tvNick.setText(nickName);
	}
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		if(db != null) {
			db.close();
		}
	}
	
	@Override
	public boolean handleMessage(Message msg) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public int subContentViewId() {
		// TODO Auto-generated method stub
		return R.layout.activity_account;
	}
	
	@Override
	protected int graviteType() {
		// TODO Auto-generated method stub
		return GRAVITE_LEFT;
	}
	
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.rl_account_password:
			Intent resetIntent = new Intent(this, RegisterResetActivity.class);
			resetIntent.putExtra("tel", tel);
			resetIntent.putExtra("type", RegisterResetActivity.TYPE_MODIFY);
			startActivity(resetIntent);
			break;
		case R.id.rl_login_out:
//			accountInfoDao.deleteByKey(tel);
			WeFlowApplication.getAppInstance().logout();
			Intent i = new Intent(this, LoginActivity.class);
			i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(i);
			finish();
			break;
		case R.id.rl_account_nickname:
			Intent nickNameIntent = new Intent(this,NicknameActivity.class);
			nickNameIntent.putExtra("nickname", nickName);
			startActivityForResult(nickNameIntent, 0x1234);
			break;
		}
		super.onClick(v);
	}
	
	
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == Activity.RESULT_OK) {
			if (data !=null  && data.getStringExtra("nickname")!=null) {
				tvNick.setText(data.getStringExtra("nickname"));
			}
		}
	}

}
