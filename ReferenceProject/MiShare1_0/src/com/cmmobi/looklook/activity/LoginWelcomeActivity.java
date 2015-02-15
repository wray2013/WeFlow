package com.cmmobi.looklook.activity;

import java.lang.reflect.Field;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import cn.zipper.framwork.core.ZActivity;
import cn.zipper.framwork.device.ZSimCardInfo;

import com.cmmobi.common.AppLogger;
import com.cmmobi.looklook.R;
import com.cmmobi.looklook.common.gson.GsonResponse3;
import com.cmmobi.looklook.common.gson.Requester3;
import com.cmmobi.looklook.common.utils.CmmobiClickAgentWrapper;
import com.cmmobi.looklook.dialog.ButtonHandler;
import com.cmmobi.looklook.info.profile.AccountInfo;
import com.cmmobi.looklook.info.profile.ActiveAccount;
import com.cmmobi.looklook.info.profile.CommonInfo;
import com.cmmobi.looklook.prompt.Prompt;

public class LoginWelcomeActivity extends ZActivity {
	
	private final int LOADING_PERIOD_INMS = 3000;
	
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

	public final static int LOGIN_THREE_TIMEOUT = 0x7329000B; // 3s超时
	
	private static final String TAG = "LoginWelcomeActivity";

	public static final String ACTION_UPDATE_FLAG = "ACTION_UPDATE_FLAG";
	
	private final int REQUEST_VIDEOSHOT = 0x12345602;
	
	public static int app_status_x;
	
	private String userid;
	private GsonResponse3.uaResponse uaResponse;
	private GsonResponse3.autoLoginResponse alResponse;
	
	private long start_time;
	private boolean hasOut = false; // 是否已经跳转其他页面
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login_welcome);
		hasOut = false;
		userid = null;
		uaResponse = null;
		app_status_x = LOGIN_STATUS_INIT;
		
		start_time = System.currentTimeMillis();
		
		//加载activeAccount
		userid = ActiveAccount.getInstance(LoginWelcomeActivity.this).getLookLookID();
		if(userid!=null){
			app_status_x = LOGIN_STATUS_A;
			AccountInfoTask at = new AccountInfoTask(getHandler());
			at.execute();
		}else{
			app_status_x = LOGIN_STATUS_B;
		}
		
		Requester3.submitUA(getHandler());
		
/*		WelcomeTask wt = new WelcomeTask(getHandler());
		wt.execute();*/

		
		handler.sendEmptyMessageDelayed(LOGIN_THREE_TIMEOUT, LOADING_PERIOD_INMS);
	}
	
	
	@Override
	public void onResume() {
		super.onResume();
		CmmobiClickAgentWrapper.onResume(this);
	}
	
	@Override
	public void onPause() {
		super.onPause();
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
					Requester3.autoLogin(getHandler(), CommonInfo.getInstance().jpush_reg_id); //记得赋值devicetoken
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
		case Requester3.RESPONSE_TYPE_UA:
			uaResponse = (GsonResponse3.uaResponse) msg.obj;
			if(app_status_x==LOGIN_STATUS_D){
				app_status_x = LOGIN_STATUS_E;
				
				if(uaResponse!=null && userid!=null){
					if(uaResponse.equipmentid!=null){
						CommonInfo.getInstance().equipmentid = uaResponse.equipmentid;
					}
					
					//自动登录
					app_status_x = LOGIN_STATUS_F;
					Requester3.autoLogin(getHandler(), ZSimCardInfo.getIMEI()); //记得赋值devicetoken
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
		case Requester3.RESPONSE_TYPE_AUTOLOGIN:
			alResponse = (GsonResponse3.autoLoginResponse) msg.obj;
			if(alResponse==null){
				app_status_x = LOGIN_STATUS_M;
				//launchMainActivity();
				waitUntilPeriod(2);
			}else if(alResponse.status.equals("0")){
				app_status_x = LOGIN_STATUS_M;
				ActiveAccount aa = ActiveAccount.getInstance(this);
				// 3.1 登陆无mood参数
				if(aa.isLogin() /*&& alResponse.mood!=null*/){
					AccountInfo ai = AccountInfo.getInstance(aa.getLookLookID());
//					ai.mood = alResponse.mood;
					if(alResponse.headimageurl!=null&&alResponse.headimageurl.length()>0)
						ai.headimageurl=alResponse.headimageurl;
					ai.address=alResponse.address;
					ai.app_downloadurl=alResponse.app_downloadurl;
					ai.birthdate=alResponse.birthdate;
					ai.nickname=alResponse.nickname;
					ai.sex=alResponse.sex;
					ai.signature=alResponse.signature;
					aa.updateBinding(alResponse.binding);
					ai.setmanager.binding = alResponse.binding;
					ai.setmanager.gesturepassword = alResponse.gesturepassword;
					ai.setmanager.sync_type = alResponse.sync_type;
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
		case LOGIN_THREE_TIMEOUT:
			if(!hasOut){
				synchronized (this) {
					AppLogger.e("login>> three timeout。。in.syn.hasOut=" + hasOut);
					if(!hasOut){
						if(userid == null){
							waitUntilPeriod(1);
						}else{
							waitUntilPeriod(2);
						}
					}
				}
			}
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
		
		saveAccount();
		
		Intent intent = new Intent(this, LookLookActivity.class);
		intent.setAction(ACTION_UPDATE_FLAG);
		if(uaResponse!=null){
			intent.putExtra("update_type", uaResponse.type);
			intent.putExtra("update_path", uaResponse.path);
			intent.putExtra("update_filesize", uaResponse.filesize);
			intent.putExtra("update_description", uaResponse.description);
			intent.putExtra("update_versionnumber", uaResponse.versionnumber);
			intent.putExtra("update_servertime", uaResponse.servertime);
		}
		startActivity(intent);
		overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out); 
		finish();
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		//System.out.println("~~ loginWelcomeActivity~~~ shot 2 ~~~" + (requestCode== REQUEST_VIDEOSHOT) +  resultCode);
		if(requestCode == REQUEST_VIDEOSHOT){
			//System.out.println("~~ loginWelcomeActivity~~~ shot 3");
			Intent intenthome = new Intent(this, LookLookActivity.class);
			startActivity(intenthome);
			overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out); 
			finish();
		}
	}


	private void launchLoginActivity() {
		
		saveAccount();
		
//		Intent intent = new Intent(this, LoginMainActivity.class);
		Intent intent = new Intent(this, LoginLooklookActivity.class);
//		Intent intent = new Intent(this, MicShareUserEntryActivity.class);
		intent.setAction(ACTION_UPDATE_FLAG);
		if(uaResponse!=null){
			intent.putExtra("update_type", uaResponse.type);
			intent.putExtra("update_path", uaResponse.path);
			intent.putExtra("update_filesize", uaResponse.filesize);
			intent.putExtra("update_description", uaResponse.description);
			intent.putExtra("update_versionnumber", uaResponse.versionnumber);
			intent.putExtra("update_servertime", uaResponse.servertime);
		}
		startActivity(intent);
		overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out); 
		finish();
	}
	
	private void waitUntilPeriod(int type){
		synchronized (this) {
			AppLogger.e("login>> waitUntilPeriod。。hasOut=" + hasOut);
			if(!hasOut){
				hasOut = true;
				long cur_time = System.currentTimeMillis();
				
				long wait_time = LOADING_PERIOD_INMS - (cur_time-start_time);
				if(wait_time<0){
					wait_time = 0;
				}
				if(wait_time > LOADING_PERIOD_INMS){
					wait_time = LOADING_PERIOD_INMS;
				}
		
				if(type==1){ //login
					getHandler().sendEmptyMessageDelayed(HANDLER_FLAG_LOADING_LOGIN, wait_time);
				}else if(type==2){
					getHandler().sendEmptyMessageDelayed(HANDLER_FLAG_LOADING_MAIN, wait_time);
				}
			}
		}
	}
	
	
	public static void LaunchUpdateDialog(final Context activity, final String type, final String path, final String filesize, final String description, final String versionnumber, final String servertime) {
		// TODO Auto-generated method stub

		//升级类型 0—无需升级 1—强制升级 2—普通升级
		
		if("1".equals(type)){
			AlertDialog alertDialog =  new AlertDialog.Builder(activity)
			.setTitle("发现新版本")
			.setCancelable(false)
			// 设置不能通过“后退”按钮关闭对话框
			.setMessage(description)
			.setPositiveButton("立即下载",
					new DialogInterface.OnClickListener() {
						public void onClick(
								DialogInterface dialoginterface, int i) {
							LoginWelcomeActivity.launchWebBrower(activity, path);

						}
					}).create();
			
			try {

				Field field = alertDialog.getClass().getDeclaredField("mAlert");
				field.setAccessible(true);
				// 获得mAlert变量的值
				Object obj = field.get(alertDialog);
				field = obj.getClass().getDeclaredField("mHandler");
				field.setAccessible(true);
				// 修改mHandler变量的值，使用新的ButtonHandler类
				field.set(obj, new ButtonHandler(alertDialog));
			} catch (Exception e) {
			}
			// 显示对话框
			alertDialog.show();

			
		}else if("2".equals(type)){
			new AlertDialog.Builder(activity)
			.setTitle("发现新版本")
			.setCancelable(true)
			// 设置不能通过“后退”按钮关闭对话框
			.setMessage(description)
			.setPositiveButton("立即下载",
					new DialogInterface.OnClickListener() {
						public void onClick(
								DialogInterface dialoginterface, int i) {
							launchWebBrower(activity, path);

						}
					})
			.setNegativeButton("暂时不",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog,
								int id) {
							dialog.cancel();
						}
					}).show();// 显示对话框
		}
	
		
	}
	
	public static void launchWebBrower(Context activity, String url){

		if(url!=null){
			Uri uri = Uri.parse(url.replace("\"", ""));
			Log.e(TAG, "launchWebBrower url:" + url + ", uri:" + uri.toString());
			Intent intent = new Intent(Intent.ACTION_VIEW);
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK); 
			intent.setData(uri); 
			try{
				activity.startActivity(intent);
			}catch(android.content.ActivityNotFoundException e){
				e.printStackTrace();
			}

		}else{
			Prompt.Dialog(activity, false, "提醒", "网络超时", null);
		}

	}
	
	
	
	protected void saveAccount(){
		ActiveAccount.getInstance(this).persist();
		String UID = ActiveAccount.getInstance(this).getUID();
		if (UID != null) {
			AccountInfo.getInstance(UID).persist();
		}
	}

}
