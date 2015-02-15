package com.cmmobi.looklook.activity;

import java.lang.reflect.Field;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Message;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.widget.Toast;
import cn.jpush.android.api.JPushInterface;
import cn.zipper.framwork.core.ZActivity;
import cn.zipper.framwork.core.ZDialog;
import cn.zipper.framwork.core.ZViewFinder;

import com.cmmobi.looklook.MainApplication;
import com.cmmobi.looklook.R;
import com.cmmobi.looklook.common.constant.Constant;
import com.cmmobi.looklook.common.gson.GsonResponse2;
import com.cmmobi.looklook.common.gson.CRM_Object.versionCheckResponse;
import com.cmmobi.looklook.common.gson.GsonResponse2.uaResponse;
import com.cmmobi.looklook.common.gson.CRMRequester;
import com.cmmobi.looklook.common.gson.CRM_Object;
import com.cmmobi.looklook.common.gson.Requester2;
import com.cmmobi.looklook.common.gson.WeiboRequester;
import com.cmmobi.looklook.common.gson.WeiboResponse.RWUserInfo;
import com.cmmobi.looklook.common.gson.WeiboResponse.SWUserInfo;
import com.cmmobi.looklook.common.gson.WeiboResponse.TWUserInfo;
import com.cmmobi.looklook.common.service.InitService;
import com.cmmobi.looklook.common.utils.CmmobiClickAgentWrapper;
import com.cmmobi.looklook.common.utils.UmengclickAgentWrapper;
import com.cmmobi.looklook.dialog.ButtonHandler;
import com.cmmobi.looklook.info.profile.AccountInfo;
import com.cmmobi.looklook.info.profile.ActiveAccount;
import com.cmmobi.looklook.info.profile.CommonInfo;
import com.cmmobi.looklook.prompt.Prompt;
import com.cmmobi.sns.api.CmmobiSnsLib;
import com.google.gson.Gson;
import com.weibo.sdk.android.api.WeiboAuthListener;
import com.weibo.sdk.android.api.WeiboDialogError;
import com.weibo.sdk.android.api.WeiboException;

public class LoginMainActivity extends ZActivity {
	private static final String TAG = "LoginMainActivity";

	public static final String FLAG_CLOSE_ACTIVITY = "FLAG_CLOSE_ACTIVITY";
	private final int REQUEST_VIDEOSHOT = 0x12345604;
	private final int HANDLER_SINA_AUTHOR_SUCCESS = 0;
	private final int HANDLER_TENCENT_AUTHOR_SUCCESS = 1;
	private final int HANDLER_RENREN_AUTHOR_SUCCESS = 2;

	private final int REQUEST_CODE = 0x73916396;

	private Gson gosn;
	
	private BroadcastReceiver mBroadcastReceiver_exit;

	private WeiboAuthListener tencentlistener = new WeiboAuthListener() {

		@Override
		public void onComplete(int weiboIndex) {
			//Prompt.Alert("tencent授权成功！");
			//CookieSyncManager.getInstance().sync();
			Message message = getHandler().obtainMessage(
					HANDLER_TENCENT_AUTHOR_SUCCESS);
			getHandler().sendMessage(message);
			CmmobiClickAgentWrapper.onEvent(LoginMainActivity.this, "login_success", "6");
			CmmobiClickAgentWrapper.onEventEnd(LoginMainActivity.this, "login", "6");

		}

		@Override
		public void onCancel(int arg0) {
			//Prompt.Alert("tencent授权取消！");
			Prompt.Dialog(LoginMainActivity.this, false, "授权错误", "腾讯授权取消！", null);
			ZDialog.dismiss();
			CmmobiClickAgentWrapper.onEventEnd(LoginMainActivity.this, "login", "6");
		}

		@Override
		public void onError(WeiboDialogError arg0, int arg1) {
			//Prompt.Alert("tencent授权错误！");
			Prompt.Dialog(LoginMainActivity.this, false, "授权错误", "腾讯授权错误！", null);
			ZDialog.dismiss();
			CmmobiClickAgentWrapper.onEventEnd(LoginMainActivity.this, "login", "6");
		}

		@Override
		public void onWeiboException(WeiboException arg0, int arg1) {
			//Prompt.Alert("tencent授权异常！");
			Prompt.Dialog(LoginMainActivity.this, false, "授权错误", "腾讯授权异常！", null);
			ZDialog.dismiss();
			CmmobiClickAgentWrapper.onEventEnd(LoginMainActivity.this, "login", "6");
		}

	};

	private WeiboAuthListener sinalistener = new WeiboAuthListener() {

		@Override
		public void onComplete(int weiboIndex) {
			//Prompt.Alert("sina授权成功！");
			//CookieSyncManager.getInstance().sync();
			Message message = getHandler().obtainMessage(
					HANDLER_SINA_AUTHOR_SUCCESS);
			getHandler().sendMessage(message);
			CmmobiClickAgentWrapper.onEvent(LoginMainActivity.this, "login_success", "1");
			CmmobiClickAgentWrapper.onEventEnd(LoginMainActivity.this, "login", "1");

		}

		@Override
		public void onCancel(int arg0) {
			//Prompt.Alert("sina授权取消！");
			Prompt.Dialog(LoginMainActivity.this, false, "授权错误", "新浪授权取消！", null);
			ZDialog.dismiss();
			CmmobiClickAgentWrapper.onEventEnd(LoginMainActivity.this, "login", "1");
		}

		@Override
		public void onError(WeiboDialogError arg0, int arg1) {
			//Prompt.Alert("sina授权错误！");
			Prompt.Dialog(LoginMainActivity.this, false, "授权错误", "新浪授权错误！", null);
			ZDialog.dismiss();
			CmmobiClickAgentWrapper.onEventEnd(LoginMainActivity.this, "login", "1");
		}

		@Override
		public void onWeiboException(WeiboException arg0, int arg1) {
			//Prompt.Alert("sina授权异常！");
			Prompt.Dialog(LoginMainActivity.this, false, "授权错误", "新浪授权异常！", null);
			ZDialog.dismiss();
			CmmobiClickAgentWrapper.onEventEnd(LoginMainActivity.this, "login", "1");
		}

	};
	
	private WeiboAuthListener renrenlistener = new WeiboAuthListener() {

		@Override
		public void onComplete(int weiboIndex) {
			//Prompt.Alert("renren授权成功！");
			//CookieSyncManager.getInstance().sync();
			Message message = getHandler().obtainMessage(
					HANDLER_RENREN_AUTHOR_SUCCESS);
			getHandler().sendMessage(message);
			CmmobiClickAgentWrapper.onEvent(LoginMainActivity.this, "login_success", "2");
			CmmobiClickAgentWrapper.onEventEnd(LoginMainActivity.this, "login", "2");

		}

		@Override
		public void onCancel(int arg0) {
			//Prompt.Alert("renren授权取消！");
			Prompt.Dialog(LoginMainActivity.this, false, "授权错误", "人人授权取消！", null);
			ZDialog.dismiss();
			CmmobiClickAgentWrapper.onEventEnd(LoginMainActivity.this, "login", "2");
		}

		@Override
		public void onError(WeiboDialogError arg0, int arg1) {
			//Prompt.Alert("renren授权错误！");
			Prompt.Dialog(LoginMainActivity.this, false, "授权错误", "人人授权错误！", null);
			ZDialog.dismiss();
			CmmobiClickAgentWrapper.onEventEnd(LoginMainActivity.this, "login", "2");
		}

		@Override
		public void onWeiboException(WeiboException arg0, int arg1) {
			//Prompt.Alert("renren授权异常！");
			Prompt.Dialog(LoginMainActivity.this, false, "授权错误", "人人授权异常！", null);
			ZDialog.dismiss();
			CmmobiClickAgentWrapper.onEventEnd(LoginMainActivity.this, "login", "2");
		}

	};

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login_main);
		
/*		if(Constant.open_strict_mode){
			StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()  
	        .detectNetwork()   // or .detectAll() for all detectable problems 
	        .detectAll()
	        .build());  

			StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()  
	        .penaltyDeath()  
	        .build());
		}*/
		
/*		AlertDialog alertDialog =  new AlertDialog.Builder(this)
		.setTitle("发现新版本")
		.setCancelable(false)
		// 设置不能通过“后退”按钮关闭对话框
		.setMessage("resp.description")
		.setPositiveButton("立即下载",
				new DialogInterface.OnClickListener() {
					public void onClick(
							DialogInterface dialoginterface, int i) {
						Toast.makeText(LoginMainActivity.this, "text - onclick", Toast.LENGTH_LONG).show();

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
		alertDialog.show();*/

		
		
		String userid = ActiveAccount.getInstance(this).getLookLookID();

		if (userid != null) {
			launchMainActivity(false, false);
		} else {
	        JPushInterface.stopPush(MainApplication.getAppInstance());
			gosn = new Gson();
			ZViewFinder finder = getZViewFinder();
			finder.setOnClickListener(R.id.btn_activity_login_main_sina, this);
			finder.setOnClickListener(R.id.btn_activity_login_main_tencent, this);
			finder.setOnClickListener(R.id.btn_activity_login_main_renren, this);
			finder.setOnClickListener(R.id.btn_activity_login_main_looklook, this);
			//finder.setOnClickListener(R.id.btn_activity_login_main_try, this);

			if(LoginWelcomeActivity.app_status_x==LoginWelcomeActivity.LOGIN_STATUS_G){
				Requester2.submitUA(getHandler());
			}
			
			CRMRequester.checkVersion(handler);
			
		}
		
		IntentFilter filter_exit = new IntentFilter(FLAG_CLOSE_ACTIVITY);
		mBroadcastReceiver_exit = new BroadcastReceiver() {

			@Override
			public void onReceive(Context context, Intent intent) {
				//HomeActivity.this.unregisterReceiver(this);
				LoginMainActivity.this.finish();
			}
		};
		
		registerReceiver(mBroadcastReceiver_exit, filter_exit);
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
	protected void onDestroy() {
		unregisterReceiver(mBroadcastReceiver_exit);

		super.onDestroy();
	}
	
	/***
	 *  step1: XXX_AUTHOR_SUCCESS 微博授权成功检查
	 *  
	 *  step2: XXX_INTERFACE_GET_ACCOUNTINFO 微博账号信息成功检查
	 *  
	 *  step3: RESPONSE_TYPE_LOGIN  登录、注册 成功检查
	 *  
	 * */

	@Override
	public boolean handleMessage(Message msg) {
		ActiveAccount acct = ActiveAccount.getInstance(getApplicationContext());
		AccountInfo uAI = null;

		uaResponse uaResponse = null;

		switch (msg.what) {
		case Requester2.RESPONSE_TYPE_UA:
			//AppState.setUaResponse(msg.obj);
			ZDialog.dismiss();
			uaResponse  = (GsonResponse2.uaResponse) msg.obj;
			if(uaResponse != null && uaResponse.equipmentid!=null){
				CommonInfo.getInstance().equipmentid = uaResponse.equipmentid;
			}else{
				Prompt.Alert(this, "网络超时");
			}
			break;
		case CRMRequester.RESPONSE_TYPE_VERSION_CHECK:
			CRM_Object.versionCheckResponse versionResp = (versionCheckResponse) msg.obj;
			doingCheckVersion(versionResp);
			break;
		case CRMRequester.RESPONSE_TYPE_APK_URL:
			launchWebBrower((String) msg.obj);
			break;
		case HANDLER_SINA_AUTHOR_SUCCESS: // sina授权成功，获取微博信息并写入账号，开启loading，开始登陆;
			ZDialog.show(R.layout.progressdialog, false, true, this);
			acct.snstype = "1";
			if(acct.snsid!=null){
				WeiboRequester.getSinaAccountInfo(getHandler(), acct.snsid);

			}else{
				acct.logout();
				ZDialog.dismiss();
				//Prompt.Alert(this, "授权微博异常");
				Prompt.Dialog(LoginMainActivity.this, false, "授权错误", "新浪授权异常！", null);
			}
			break;
		case HANDLER_RENREN_AUTHOR_SUCCESS:// renren授权成功，获取微博信息并写入账号，开启loading，开始登陆;
			ZDialog.show(R.layout.progressdialog, false, true, this);
			acct.snstype = "2";
			if(acct.snsid!=null){
				WeiboRequester.getRenrenAccountInfo(getHandler(), acct.snsid);

			}else{
				acct.logout();
				ZDialog.dismiss();
				//Prompt.Alert(this, "授权微博异常");
				Prompt.Dialog(LoginMainActivity.this, false, "授权错误", "人人授权异常！", null);
			}

			break;
		case HANDLER_TENCENT_AUTHOR_SUCCESS:// tencent授权成功，获取微博信息并写入账号，开启loading，开始登陆;
			ZDialog.show(R.layout.progressdialog, false, true, this);
			acct.snstype = "6";
			if(acct.snsid!=null){
				WeiboRequester.getTencentAccountInfo(getHandler(), acct.snsid);

			}else{
				acct.logout();
				ZDialog.dismiss();
				//Prompt.Alert(this, "授权微博异常");
				Prompt.Dialog(LoginMainActivity.this, false, "授权错误", "腾讯授权异常！", null);
			}

			break;

		case WeiboRequester.SINA_INTERFACE_GET_ACCOUNTINFO:
			ZDialog.show(R.layout.progressdialog, false, true, this);
			SWUserInfo sn_object  = (SWUserInfo) msg.obj;
			if(sn_object==null || sn_object.screen_name==null || !acct.updateSinaAuthor(sn_object)){
				acct.logout();
				ZDialog.dismiss();
				//Prompt.Alert(this, "获取微博个人信息异常");
				Prompt.Dialog(LoginMainActivity.this, false, "错误", "获取微博个人信息异常！", null);
				
			}else{
				if(CommonInfo.getInstance().equipmentid==null){
					Requester2.submitUA(getHandler());
				}else{
					Log.e(TAG, "sina updateAuthor ok, login..");
					Requester2.login(this, getHandler(), acct, null);
				}

			}
			
			break;
		case WeiboRequester.RENREN_INTERFACE_GET_ACCOUNTINFO:
			ZDialog.show(R.layout.progressdialog, false, true, this);
			//RWUserInfo[] rr_objects  = (RWUserInfo[]) msg.obj;
			RWUserInfo rr_object = (RWUserInfo) msg.obj;
			//if(rr_objects==null || rr_objects.length<1 ||rr_objects[0]==null ||rr_objects[0].name==null || !acct.updateRenrenAuthor(rr_objects[0])){
			if(rr_object==null || rr_object.response==null || rr_object.response.id==0 || !acct.updateRenrenAuthor(rr_object)){	
				acct.logout();
				ZDialog.dismiss();
				//Prompt.Alert(this, "获取微博个人信息异常");
				Prompt.Dialog(LoginMainActivity.this, false, "错误", "获取微博个人信息异常！", null);
				
			}else{
				if(CommonInfo.getInstance().equipmentid==null){
					Requester2.submitUA(getHandler());
				}else{
					Log.e(TAG, "renren updateAuthor ok, login..");
					Requester2.login(this, getHandler(), acct, null);
				}
			}
			break;
		case WeiboRequester.TENCENT_INTERFACE_GET_ACCOUNTINFO:
			ZDialog.show(R.layout.progressdialog, false, true, this);
			TWUserInfo tx_object  = (TWUserInfo) msg.obj;
			if(tx_object==null || tx_object.data==null || tx_object.data.nick==null || !acct.updateTencentAuthor(tx_object)){
				acct.logout();
				ZDialog.dismiss();
				//Prompt.Alert(this, "获取微博个人信息异常");
				Prompt.Dialog(LoginMainActivity.this, false, "错误", "获取微博个人信息异常！", null);
				
			}else{
				if(CommonInfo.getInstance().equipmentid==null){
					Requester2.submitUA(getHandler());
				}else{
					Log.e(TAG, "tencent updateAuthor ok, login..");
					Requester2.login(this, getHandler(), acct, null);
				}
			}
			break;

		case Requester2.RESPONSE_TYPE_LOGIN: // 登陆返回信息
			try {

				GsonResponse2.loginResponse obj = (GsonResponse2.loginResponse) (msg.obj);
				if(obj==null){
					Log.e(TAG, "LookLook登陆，obj is null");
					//Prompt.Alert(this, "LookLook登陆，网络超时！");
					Prompt.Dialog(LoginMainActivity.this, false, "错误", "网络超时", null);
					ActiveAccount.getInstance(this).logout();
				}else{
					if (obj.status.equals("0")) {

						if(ActiveAccount.getInstance(this).updateLogin(obj)){
							Log.i(TAG, "登陆成功");
							Prompt.Alert(this, "looklook登陆，登陆成功");
							if("20".equals(obj.crm_status)){
								launchMainActivity(true, true);
							}else{
								launchMainActivity(false, true);
							}
							
						}else{
							Log.e(TAG, "服务器返回失败");
							ActiveAccount.getInstance(this).logout();
							//Prompt.Alert(this, "LookLook登陆，服务器返回失败");
							Prompt.Dialog(LoginMainActivity.this, false, "错误", "登录失败：" + Prompt.GetRIAStatus(obj.status) + "/" + Prompt.GetCrmStatus(obj.crm_status), null);
						}

					} /*else if (obj.status.equals("1")) {
						// 第三方登录账号不存在,创建新账号
						if(ActiveAccount.getInstance(this).updateLogin(obj)){
							Log.i(TAG, "第三方登录账号不存在,创建新账号");
							Prompt.Alert(this, "LookLook登陆，创建新账号");
							launchMainActivity();
						}else{
							Log.e(TAG, "服务器返回失败");
							ActiveAccount.getInstance(this).logout();
							Prompt.Alert(this, "LookLook登陆，服务器返回失败");
						}

					} else if (obj.status.equals("2")) {
						// 密码错误
						Log.e(TAG, "LookLook登陆，密码错误");
						Prompt.Alert(this, "LookLook登陆，密码错误");
						ActiveAccount.getInstance(this).logout();

					} else if (obj.status.equals("3")) {
						// 用户不存在
						Log.e(TAG, "LookLook登陆，用户不存在");
						Prompt.Alert(this, "LookLook登陆，用户不存在");
						ActiveAccount.getInstance(this).logout();

					}*/else{
						Prompt.Dialog(LoginMainActivity.this, false, "错误", "登录失败：" + Prompt.GetRIAStatus(obj.status) + "/" + Prompt.GetCrmStatus(obj.crm_status), null);
						ActiveAccount.getInstance(this).logout();
					}
				}

			} catch (Exception e) {
				e.printStackTrace();
				acct.logout();
				//Prompt.Alert(this, "LookLook登陆，异常！");
				Prompt.Dialog(LoginMainActivity.this, false, "错误", "looklook登陆异常!" , null);
			}
			ZDialog.dismiss();
			break;

		}
		return false;
	}

	@Override
	public void onClick(View v) {

		switch (v.getId()) {
		case R.id.btn_activity_login_main_sina:

			CmmobiSnsLib.getInstance(this).sinaAuthorize(sinalistener);
			CmmobiClickAgentWrapper.onEventBegin(this, "login", "1");
			break;

		case R.id.btn_activity_login_main_tencent:
			CmmobiSnsLib.getInstance(this).tencentWeiboAuthorize(tencentlistener);
			CmmobiClickAgentWrapper.onEventBegin(this, "login", "6");
			break;
			
		case R.id.btn_activity_login_main_renren:
			CmmobiSnsLib.getInstance(this).renrenAuthorize(renrenlistener);
			CmmobiClickAgentWrapper.onEventBegin(this, "login", "2");
			break;

		case R.id.btn_activity_login_main_looklook:
			launchLoginDialog();
			break;

/*		case R.id.btn_activity_login_main_try:
			launchMainActivity();
			break;*/

		}

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode,
			Intent intent) {
		super.onActivityResult(requestCode, resultCode, intent);
		if (requestCode == REQUEST_CODE) {
			if (resultCode == RESULT_OK) {//用手机号方式注册，一路回退到loginmain，自动登录
				launchMainActivity(true, false);
			}
		}else if(requestCode == REQUEST_VIDEOSHOT){
			Intent intenthome = new Intent(this, HomeActivity.class);
			//intenthome.putExtra(HomeActivity.FLAG_INIT, true);
			startActivity(intenthome);
			overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out); 
			finish();
		}else {
	        if (CmmobiSnsLib.getInstance(this).mSsoHandler != null) {
	        	CmmobiSnsLib.getInstance(this).mSsoHandler.authorizeCallBack(requestCode, resultCode, intent);
	        }
			
		}
	}
	
	private void doingCheckVersion(final versionCheckResponse resp){
		//升级类型 0—无需升级 1—强制升级 2—普通升级
		if(resp==null){
			return;
		}
		
		if("1".equals(resp.type)){
			AlertDialog alertDialog =  new AlertDialog.Builder(this)
			.setTitle("发现新版本")
			.setCancelable(false)
			// 设置不能通过“后退”按钮关闭对话框
			.setMessage(resp.description)
			.setPositiveButton("立即下载",
					new DialogInterface.OnClickListener() {
						public void onClick(
								DialogInterface dialoginterface, int i) {
							getApkUrl(resp.path);

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

			
		}else if("2".equals(resp.type)){
			new AlertDialog.Builder(this)
			.setTitle("发现新版本")
			.setCancelable(true)
			// 设置不能通过“后退”按钮关闭对话框
			.setMessage(resp.description)
			.setPositiveButton("立即下载",
					new DialogInterface.OnClickListener() {
						public void onClick(
								DialogInterface dialoginterface, int i) {
							getApkUrl(resp.path);

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
	
	private void getApkUrl(String url){
		CRMRequester.getApkUrl(handler, url);
	}
	
	private void launchWebBrower(String url){

		if(url!=null){
			Uri uri = Uri.parse(url.replace("\"", ""));
			Log.e(TAG, "launchWebBrower url:" + url + ", uri:" + uri.toString());
			Intent intent = new Intent(Intent.ACTION_VIEW);
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK); 
			intent.setData(uri); 
			try{
				this.startActivity(intent);
			}catch(android.content.ActivityNotFoundException e){
				e.printStackTrace();
			}

		}else{
			Prompt.Dialog(this, false, "提醒", "网络超时", null);
		}

	}

	public void launchMainActivity(boolean newReg, boolean thirdLogin) {
		System.out.println("~~ loginMainActivity~~~");
		startService(new Intent(this, InitService.class));
		
		try {
			AccountInfo.getInstance(ActiveAccount.getInstance(this).getUID()).setmanager.initSettingItems();
		} catch (Exception e) {
			// TODO: handle exception
		}
		
		
		AccountInfo ai = AccountInfo.getInstance(ActiveAccount.getInstance(this).getLookLookID());

		if(newReg || ActiveAccount.getInstance(this).is_firstlogin.equals("0")){
			Intent intent = new Intent(this, HomeActivity.class);
			intent.putExtra(HomeActivity.FLAG_INIT, true);
			if(thirdLogin){
				intent.putExtra(HomeActivity.FLAG_WEIBO, true);
			}
			startActivity(intent);
			overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out); 
			finish();
		}else{
			if(ai!=null && ai.setmanager!=null && "2".equals(ai.setmanager.launch_type)){
				Intent intent = new Intent(this, VideoShootActivity.class);
				startActivityForResult(intent, REQUEST_VIDEOSHOT);
			}else{
				Intent intent = new Intent(this, HomeActivity.class);
				startActivity(intent);
				overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out); 
				finish();
			}
		}
		

		
/*		if(!newReg && (!ActiveAccount.getInstance(this).is_firstlogin.equals("0")) && ai!=null && ai.setmanager!=null && "2".equals(ai.setmanager.launch_type)){
			Intent intent = new Intent(this, VideoShootActivity.class);
			startActivityForResult(intent, REQUEST_VIDEOSHOT);
		}else{
			Intent intent = new Intent(this, HomeActivity.class);
			intent.putExtra(HomeActivity.FLAG_INIT, true);
			if(thirdLogin){
				intent.putExtra(HomeActivity.FLAG_WEIBO, true);
			}

			startActivity(intent);
			overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out); 
			finish();
		}*/
	}

	private void launchLoginDialog() {
		Intent intent = new Intent(this, LoginLooklookActivity.class);
		startActivityForResult(intent, REQUEST_CODE);
		overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out); 

	}

}
