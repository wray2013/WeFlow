package com.cmmobi.looklook.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import cn.zipper.framwork.core.ZActivity;
import cn.zipper.framwork.device.ZSimCardInfo;

import com.cmmobi.looklook.R;
import com.cmmobi.looklook.common.gson.GsonResponse2;
import com.cmmobi.looklook.common.gson.Requester2;
import com.cmmobi.looklook.common.service.InitService;
import com.cmmobi.looklook.common.utils.CmmobiClickAgentWrapper;
import com.cmmobi.looklook.common.utils.UmengclickAgentWrapper;
import com.cmmobi.looklook.info.profile.AccountInfo;
import com.cmmobi.looklook.info.profile.ActiveAccount;
import com.cmmobi.looklook.info.profile.CommonInfo;
import com.cmmobi.looklook.info.profile.TimeHelper;

public class LoginWelcomeActivity extends ZActivity {
	
	private final int LOADING_PERIOD_INMS = 1000;
	
	private final int HANDLER_FLAG_LOADING_LOGIN = 0x24529583;
	private final int HANDLER_FLAG_LOADING_MAIN = 0x24529584;
	
	private final int HANDLER_FLAG_ACCOUNTINFO_DONE = 0x24529585;
	
	public final static int LOGIN_STATUS_INIT = 0x73290000; //init
	public final static int LOGIN_STATUS_A = 0x73290001; //A,  有userid
	public final static int LOGIN_STATUS_B = 0x73290002; //B,  无userid
	public final static int LOGIN_STATUS_C = 0x73290003; //C,  仅收到UA响应（包括超时）
	public final static int LOGIN_STATUS_D = 0x73290004; //D,  仅完成AccountIfno（AI）加载（包括未加载到）
	public final static int LOGIN_STATUS_E = 0x73290005; //E,  CD两者都完成
	public final static int LOGIN_STATUS_F = 0x73290006; //F,  自动登录
	public final static int LOGIN_STATUS_G = 0x73290007; //G,  登录页（未完成UA）
	public final static int LOGIN_STATUS_H = 0x73290008; //H,  G之后完成UA
	public final static int LOGIN_STATUS_L = 0x73290009; //L,  登录页（已完成UA）
	public final static int LOGIN_STATUS_M = 0x7329000a; //M,  主界面

	private static final String TAG = "LoginWelcomeActivity";
	
	private final int REQUEST_VIDEOSHOT = 0x12345602;
	
	public static int app_status_x;
	
	private String userid;
	private GsonResponse2.uaResponse uaResponse;
	private GsonResponse2.autoLoginResponse alResponse;
	
	private long start_time;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login_welcome);
		userid = null;
		uaResponse = null;
		app_status_x = LOGIN_STATUS_INIT;
		
		start_time = TimeHelper.getInstance().now();
		
		//加载activeAccount
		userid = ActiveAccount.getInstance(LoginWelcomeActivity.this).getLookLookID();
		if(userid!=null){
			app_status_x = LOGIN_STATUS_A;
			AccountInfoTask at = new AccountInfoTask(getHandler());
			at.execute();
		}else{
			app_status_x = LOGIN_STATUS_B;
		}
		
		Requester2.submitUA(getHandler());
		
/*		WelcomeTask wt = new WelcomeTask(getHandler());
		wt.execute();*/

	}
	
	
	@Override
	public void onResume() {
		super.onResume();
		UmengclickAgentWrapper.onResume(this);
		CmmobiClickAgentWrapper.onResume(this);
	}
	
	@Override
	public void onPause() {
		super.onPause();
		UmengclickAgentWrapper.onPause(this);
		CmmobiClickAgentWrapper.onPause(this);
	}
	
	@Override
	public void onStop() {
		super.onStop();
		CmmobiClickAgentWrapper.onStop(this);
	}
	
	@Override
	public boolean handleMessage(Message msg) {
		// TODO Auto-generated method stub
		switch(msg.what){
		case HANDLER_FLAG_ACCOUNTINFO_DONE:
			if(app_status_x==LOGIN_STATUS_C){
				app_status_x = LOGIN_STATUS_E;
				if(uaResponse!=null && userid!=null){
					//自动登录
					app_status_x = LOGIN_STATUS_F;
					Requester2.autoLogin(getHandler(), CommonInfo.getInstance().jpush_reg_id); //记得赋值devicetoken
				}else if(uaResponse!=null && userid==null){
					app_status_x = LOGIN_STATUS_L;
					waitUntilPeriod(1);
				}else if(uaResponse==null && userid!=null){
					app_status_x = LOGIN_STATUS_M;
					waitUntilPeriod(2);
				}else{
					app_status_x = LOGIN_STATUS_G;
					waitUntilPeriod(1);
				}
			}else if(app_status_x==LOGIN_STATUS_A){
				app_status_x = LOGIN_STATUS_D;
			}else{
				Log.e(TAG, "HANDLER_FLAG_ACCOUNTINFO_DONE - app_status_x:" + app_status_x);
				app_status_x = LOGIN_STATUS_D;
			}
			break;
		case Requester2.RESPONSE_TYPE_UA:
			uaResponse = (GsonResponse2.uaResponse) msg.obj;
			if(app_status_x==LOGIN_STATUS_D){
				app_status_x = LOGIN_STATUS_E;
				
				if(uaResponse!=null && userid!=null){
					if(uaResponse.equipmentid!=null){
						CommonInfo.getInstance().equipmentid = uaResponse.equipmentid;
					}
					
					//自动登录
					app_status_x = LOGIN_STATUS_F;
					Requester2.autoLogin(getHandler(), ZSimCardInfo.getIMEI()); //记得赋值devicetoken
					if(uaResponse.equipmentid!=null){
						CommonInfo.getInstance().equipmentid = uaResponse.equipmentid;
					}
				}else if(uaResponse!=null && userid==null){
                    //del autologin requester
					app_status_x = LOGIN_STATUS_L;
					//launchLoginActivity();
					waitUntilPeriod(1);
				}else if(uaResponse==null && userid!=null){
					app_status_x = LOGIN_STATUS_M;
					//launchMainActivity();
					waitUntilPeriod(2);
				}else{
					app_status_x = LOGIN_STATUS_G;
					//launchLoginActivity();
					waitUntilPeriod(1);
				}
			}else if(app_status_x==LOGIN_STATUS_A){
				app_status_x = LOGIN_STATUS_C;
				waitUntilPeriod(2);
			}else if(app_status_x==LOGIN_STATUS_B){
				if(uaResponse!=null){
					app_status_x = LOGIN_STATUS_L;
					
				}else{
					app_status_x = LOGIN_STATUS_G;
				}
				
				//launchLoginActivity();
				waitUntilPeriod(1);
			}else{ //其它状态直接强制登录
				app_status_x = LOGIN_STATUS_G;
				//launchLoginActivity();
				waitUntilPeriod(1);
			}
			break;
		case Requester2.RESPONSE_TYPE_AUTOLOGIN:
			alResponse = (GsonResponse2.autoLoginResponse) msg.obj;
			if(alResponse==null){
				app_status_x = LOGIN_STATUS_M;
				//launchMainActivity();
				waitUntilPeriod(2);
			}else if(alResponse.status.equals("0")){
				app_status_x = LOGIN_STATUS_M;
				ActiveAccount aa = ActiveAccount.getInstance(this);
				if(aa.isLogin() && alResponse.mood!=null){
					AccountInfo ai = AccountInfo.getInstance(aa.getLookLookID());
					ai.mood = alResponse.mood;
					if(alResponse.headimageurl!=null&&alResponse.headimageurl.length()>0)
						ai.headimageurl=alResponse.headimageurl;
					ai.address=alResponse.address;
					ai.app_downloadurl=alResponse.app_downloadurl;
					ai.birthdate=alResponse.birthdate;
					ai.nickname=alResponse.nickname;
					ai.sex=alResponse.sex;
					ai.signature=alResponse.signature;
				}
				//launchMainActivity();
				waitUntilPeriod(2);
			}else{
				app_status_x = LOGIN_STATUS_L;
				//launchLoginActivity();
				ActiveAccount.getInstance(this).logout();
				waitUntilPeriod(1);
			}
			break;
		case HANDLER_FLAG_LOADING_LOGIN:
			launchLoginActivity();
			break;
		case HANDLER_FLAG_LOADING_MAIN:
			launchMainActivity();
			break;
		}

		return false;
	}
	
	@Override
	public void onBackPressed(){
		//nop
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub

	}

	
	
	private class AccountInfoTask extends AsyncTask<Void, Void, Boolean>{
		private Handler handler;
		
		public AccountInfoTask(Handler handler){
			this.handler = handler;
		}

		@Override
		protected Boolean doInBackground(Void... params) {
			// TODO Auto-generated method stub
			
			String userid = ActiveAccount.getInstance(LoginWelcomeActivity.this).getLookLookID();

			if (userid != null) {
				AccountInfo.getInstance(userid);		
				return true;
			}else{
				return false;
			}
		}
		
		@Override
	    protected void onPostExecute(Boolean result) {
			if(handler!=null){
				handler.obtainMessage(HANDLER_FLAG_ACCOUNTINFO_DONE, result).sendToTarget();
			}
	    }
		
	}
	
	private void launchMainActivity() {
		System.out.println("~~ loginWelcomeActivity~~~");
		startService(new Intent(this,InitService.class));
		try {
			AccountInfo.getInstance(ActiveAccount.getInstance(this).getUID()).setmanager.initSettingItems();
		} catch (Exception e) {
			// TODO: handle exception
		}
		AccountInfo ai = AccountInfo.getInstance(ActiveAccount.getInstance(this).getLookLookID());
		if((ActiveAccount.getInstance(this).is_firstlogin == null || !ActiveAccount.getInstance(this).is_firstlogin.equals("0")) && ai!=null && ai.setmanager!=null && "2".equals(ai.setmanager.launch_type)){
			System.out.println("~~ loginWelcomeActivity~~~ shot 1");
			Intent intent = new Intent(this, VideoShootActivity.class);
			startActivityForResult(intent, REQUEST_VIDEOSHOT);
		}else{
			Intent intent = new Intent(this, HomeActivity.class);
			startActivity(intent);
			overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out); 
			finish();
		}
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		System.out.println("~~ loginWelcomeActivity~~~ shot 2 ~~~" + (requestCode== REQUEST_VIDEOSHOT) +  resultCode);
		if(requestCode == REQUEST_VIDEOSHOT){
			System.out.println("~~ loginWelcomeActivity~~~ shot 3");
			Intent intenthome = new Intent(this, HomeActivity.class);
			startActivity(intenthome);
			overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out); 
			finish();
		}
	}


	private void launchLoginActivity() {
		Intent intent = new Intent(this, LoginMainActivity.class);
		startActivity(intent);
		overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out); 
		finish();
	}
	
	private void waitUntilPeriod(int type){
		long cur_time = TimeHelper.getInstance().now();
		
		long wait_time = LOADING_PERIOD_INMS - (cur_time-start_time);
		if(wait_time<0){
			wait_time = 0;
		}

		if(type==1){ //login
			getHandler().sendEmptyMessageDelayed(HANDLER_FLAG_LOADING_LOGIN, wait_time);
			//getHandler().sendEmptyMessage(HANDLER_FLAG_LOADING_LOGIN);
		}else if(type==2){
			getHandler().sendEmptyMessageDelayed(HANDLER_FLAG_LOADING_MAIN, wait_time);
			//getHandler().sendEmptyMessage(HANDLER_FLAG_LOADING_MAIN);
		}


	}
}
