package com.cmmobi.railwifi.activity;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.http.client.RequestDirector;

import android.R.integer;
import android.content.Context;
import android.content.res.ColorStateList;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
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
import android.widget.Toast;

import com.cmmobi.railwifi.R;
import com.cmmobi.railwifi.dao.DaoMaster;
import com.cmmobi.railwifi.dao.DaoSession;
import com.cmmobi.railwifi.dao.Passenger;
import com.cmmobi.railwifi.dao.PassengerDao;
import com.cmmobi.railwifi.dao.DaoMaster.DevOpenHelper;
import com.cmmobi.railwifi.dialog.PromptDialog;
import com.cmmobi.railwifi.network.GsonRequestObject.UserAddress;
import com.cmmobi.railwifi.network.GsonResponseObject.newsElem;
import com.cmmobi.railwifi.network.GsonResponseObject;
import com.cmmobi.railwifi.network.Requester;
import com.cmmobi.railwifi.utils.DisplayUtil;
import com.cmmobi.railwifi.utils.ViewUtils;
import com.google.gson.Gson;

/**
 * @author youtian
 * @email youtian@cmmobi.com
 * @date  2015-03-13
 */
public class RegisterActivity extends TitleRootActivity{

	private String TAG = "RegisterActivity";

	private ImageView ivAccountError;
	private EditText etAccount;
	private EditText etPwdFirst;
	private EditText etPwdSecond;
	private EditText etCode;
	private Button btnGetCode;
	
	private final int  MSG_UPDATE_UI = 0x0012;
	
	private String code;
	public int time = 60;
	
	class MyTimerTask extends TimerTask{

		@Override
		public void run() {
			// TODO Auto-generated method stub
			runOnUiThread(new Runnable() {		// UI thread
				@Override
				public void run() {
					time--;
				    Message message = new Message();         
				    message.what = MSG_UPDATE_UI;         
				    handler.sendMessage(message);       
				}
			});
		}
	}
		
	private Timer timer;
	private MyTimerTask task;
	
	@Override
	public int subContentViewId() {
		return R.layout.activity_account_in_userinfo;
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setTitleText("注册箩筐账号");
		setRightButtonText("完成");
		initView();
	}
	
	private void initView(){
		ViewUtils.setMarginTop(findViewById(R.id.rl_content), 16);
		ViewUtils.setMarginLeft(findViewById(R.id.rl_content), 12);
		ViewUtils.setMarginRight(findViewById(R.id.rl_content), 12);
		
		ViewUtils.setHeight(findViewById(R.id.tv_account), 84);
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
		
		etPwdFirst = (EditText) findViewById(R.id.et_pwdfirst);
		ViewUtils.setHeight(etPwdFirst, 99);
		ViewUtils.setMarginLeft(etPwdFirst, 12);
		ViewUtils.setMarginRight(etPwdFirst, 12);
		ViewUtils.setMarginTop(etPwdFirst, 30);
		
		etPwdSecond = (EditText) findViewById(R.id.et_pwdsecond);
		ViewUtils.setHeight(etPwdSecond, 99);
		ViewUtils.setMarginLeft(etPwdSecond, 12);
		ViewUtils.setMarginRight(etPwdSecond, 12);
		ViewUtils.setMarginTop(etPwdSecond, 30);
		ViewUtils.setMarginBottom(etPwdSecond, 60);
		
		etAccount.addTextChangedListener(new LoginTextWatcher(etAccount));
		etPwdFirst.addTextChangedListener(new LoginTextWatcher(etPwdFirst));
		etPwdSecond.addTextChangedListener(new LoginTextWatcher(etPwdSecond));
		
		etCode = (EditText) findViewById(R.id.et_code);
		ViewUtils.setHeight(etCode, 99);
		ViewUtils.setMarginLeft(etCode, 12);
		ViewUtils.setMarginRight(etCode, 12);
		ViewUtils.setMarginTop(etCode, 50);
		
		etCode.addTextChangedListener(new LoginTextWatcher(etCode));
		
		btnGetCode = (Button)findViewById(R.id.btn_get_code);
		ViewUtils.setSize(btnGetCode, 350, 84);
		ViewUtils.setMarginTop(btnGetCode, 50);
		btnGetCode.setTextSize(DisplayUtil.textGetSizeSp(this, 33));
		btnGetCode.setOnClickListener(this);
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

		InputMethodManager imm = (InputMethodManager) this
		  .getSystemService(Context.INPUT_METHOD_SERVICE);
		switch(v.getId()){
		case R.id.btn_title_right:
			if(TextUtils.isEmpty(etAccount.getText().toString())){
				etAccount.setHintTextColor(0xffc60606);
				etAccount.setFocusable(true);
				etAccount.requestFocus();
				imm.showSoftInput(etAccount, 0);
			}else if(!PromptDialog.checkPhoneNum(etAccount.getText().toString())){
				ivAccountError.setVisibility(View.VISIBLE);
				ivAccountError.requestFocus();
				imm.showSoftInput(ivAccountError, 0);
			}else if(TextUtils.isEmpty(etPwdFirst.getText().toString())){
				etPwdFirst.setHintTextColor(0xffc60606);
				etPwdFirst.requestFocus();
				imm.showSoftInput(etPwdFirst, 0);
			}else if(TextUtils.isEmpty(etPwdSecond.getText().toString())){
				etPwdSecond.setHintTextColor(0xffc60606);
				etPwdSecond.requestFocus();
				imm.showSoftInput(etPwdSecond, 0);
			}else if(!etPwdFirst.getText().toString().equals(etPwdSecond.getText().toString())){
				Toast.makeText(this, "两次输入的密码不一致，请重新输入！", Toast.LENGTH_LONG).show();
				etPwdFirst.setText("");
				etPwdSecond.setText("");
				etPwdFirst.requestFocus();
				imm.showSoftInput(etPwdFirst, 0);
			}else if(TextUtils.isEmpty(etCode.getText().toString())){
				etCode.setHintTextColor(0xffc60606);
				etCode.requestFocus();
				imm.showSoftInput(etCode, 0);
			}else if(!etCode.getText().toString().equals(code)){
				Toast.makeText(this, "验证错误，请重新输入！", Toast.LENGTH_LONG).show();
				etCode.setText("");
				etCode.requestFocus();
				imm.showSoftInput(etPwdFirst, 0);
			}else{
				DaoMaster daoMaster;
				DaoSession daoSession;
				PassengerDao passengerDao;
				SQLiteDatabase db;
				DevOpenHelper helper = new DaoMaster.DevOpenHelper(this, "railwifidb", null);
		        db = helper.getWritableDatabase();
		        daoMaster = new DaoMaster(db);
		        daoSession = daoMaster.newSession();
		        passengerDao = daoSession.getPassengerDao();
		        if(passengerDao.count()!=0){
			        List<Passenger> passList = passengerDao.loadAll();
			        Passenger passenger = passList.get(0);
			        UserAddress[] userAddresses = new Gson().fromJson(passenger.getAddress(), UserAddress[].class);
					Requester.requestUserRegister(handler, etAccount.getText().toString(), etPwdFirst.getText().toString(), etCode.getText().toString(), passenger.getUuid(), passenger.getSex(), passenger.getNick_name(), passenger.getTelephone(), passenger.getHometown(), passenger.getIdcard(), userAddresses);
		        }
		        db.close();
			}
			break;
		case R.id.btn_get_code:
			if(TextUtils.isEmpty(etAccount.getText().toString())){
				etAccount.setHintTextColor(0xffc60606);
				etAccount.setFocusable(true);
				etAccount.requestFocus();
				imm.showSoftInput(etAccount, 0);
			}else if(!PromptDialog.checkPhoneNum(etAccount.getText().toString())){
				ivAccountError.setVisibility(View.VISIBLE);
				ivAccountError.requestFocus();
				imm.showSoftInput(ivAccountError, 0);
			}else{
				btnGetCode.setEnabled(false);
				time = 60;
				if(timer == null){
					timer = new Timer();
				}
				if(task != null){
					task.cancel();
				}
				task = new MyTimerTask();
				timer.schedule(task, 0, 1000);
				Requester.requestGetCheckno(handler, "", etAccount.getText().toString(), "1");
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
		case Requester.RESPONSE_TYPE_USER_REGISTER:
			if (msg.obj != null) {
				GsonResponseObject.UserRegisterResp r1 = (GsonResponseObject.UserRegisterResp)(msg.obj);
				if(r1!=null && r1.status.equals("0")){
					DaoMaster daoMaster;
					DaoSession daoSession;
					PassengerDao passengerDao;
					SQLiteDatabase db;
					DevOpenHelper helper = new DaoMaster.DevOpenHelper(this, "railwifidb", null);
			        db = helper.getWritableDatabase();
			        daoMaster = new DaoMaster(db);
			        daoSession = daoMaster.newSession();
			        passengerDao = daoSession.getPassengerDao();
			        if(passengerDao.count()!=0){
			        	List<Passenger> passList = passengerDao.loadAll();
				        Passenger passenger = passList.get(0);
				        passenger.setUser_id(r1.userid);
				        passenger.setIslogin(true);
				        MainActivity.setUserId(r1.userid);
				        passengerDao.update(passenger);
				     }
			        db.close();
			        this.finish();
				} else {
					
				}
			} else {
				
			}
			break;
		case Requester.RESPONSE_TYPE_GET_CHECKNO:
			if (msg.obj != null) {
				GsonResponseObject.GetChecknoResp r2 = (GsonResponseObject.GetChecknoResp)(msg.obj);
				if(r2!=null && r2.status.equals("0")){
					code = r2.security_code;
					
				}
			}
			break;
		case MSG_UPDATE_UI:
			if(time > 0){
				btnGetCode.setText("重新发送（"+ time + "s）");
				btnGetCode.setTextColor(Color.GRAY);
			}else if(time == 0){
				btnGetCode.setEnabled(true);
				btnGetCode.setText("重新发送");
				btnGetCode.setTextColor(Color.WHITE);
			}
			break;
		default:
			break;
		}
		return false;
	}

}
