package com.cmmobi.looklook.activity.login;

import java.lang.reflect.Field;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import cn.zipper.framwork.core.ZActivity;
import cn.zipper.framwork.device.ZSimCardInfo;

import com.cmmobi.looklook.R;
import com.cmmobi.looklook.activity.GuideActivity;
import com.cmmobi.looklook.activity.LookLookActivity;
import com.cmmobi.looklook.common.gson.GsonResponse3;
import com.cmmobi.looklook.common.gson.Requester3;
import com.cmmobi.looklook.common.utils.CmmobiClickAgentWrapper;
import com.cmmobi.looklook.dialog.ButtonHandler;
import com.cmmobi.looklook.info.profile.AccountInfo;
import com.cmmobi.looklook.info.profile.ActiveAccount;
import com.cmmobi.looklook.info.profile.CommonInfo;
import com.cmmobi.looklook.prompt.Prompt;

public class MicShareWelcomeActivity extends ZActivity {
	
	private final int LOADING_PERIOD_INMS = 3000;
	
	private final int HANDLER_FLAG_LOADING_END = 0x24529584;
	
	
	public final static int LOGIN_THREE_TIMEOUT = 0x7329000B; // 3s超时
	
	private static final String TAG = "MicShareWelcomeActivity";

	public static final String ACTION_UPDATE_FLAG = "ACTION_UPDATE_FLAG";
	
	private final int REQUEST_VIDEOSHOT = 0x12345602;
	
	public static final String SP_KEY = "welcome_guide";
	
	private String userid;
	private GsonResponse3.uaResponse uaResponse;
	
	private long start_time;
	
	public static boolean UaLoadEnd = true;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login_welcome);
		
		start_time = System.currentTimeMillis();
		
		//加载activeAccount
		userid = ActiveAccount.getInstance(MicShareWelcomeActivity.this).getLookLookID();
		if(!TextUtils.isEmpty(userid)){
			AccountInfo.getInstance(userid);
		}
		
		UaLoadEnd = false;
		Requester3.submitUA(getHandler());
		
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
		switch(msg.what){
		case Requester3.RESPONSE_TYPE_UA:
			uaResponse = (GsonResponse3.uaResponse) msg.obj;
			
			// 之前登陆过 需完成自动登陆
			if(ActiveAccount.verifyUseridSuccess(userid)){
				if(uaResponse!= null ){
					if(uaResponse.equipmentid!=null){
						CommonInfo.getInstance().equipmentid = uaResponse.equipmentid;
					}
					Requester3.autoLogin(getHandler(), ZSimCardInfo.getIMEI()); //记得赋值devicetoken
				}
			}else{
				if(uaResponse!=null){
					if(uaResponse.equipmentid!=null){
						CommonInfo.getInstance().equipmentid = uaResponse.equipmentid;
					}
					
					/**test**/
//					uaResponse.userid = "a484757806dad04154097460f4a03c9f31d6"; // true
//					uaResponse.userid = "a484757806dad04154097460f4a03c9f31d5";  // false
//					uaResponse.mishare_no = "100100";
					/**test**/

					// 完成用户数据更新
					String micshare = uaResponse.mishare_no;
					userid = uaResponse.userid;
					CmmobiClickAgentWrapper.setUserid(this, userid);
					ActiveAccount.getInstance(MicShareWelcomeActivity.this).updateMicShareNo(userid, micshare);
					
				}
			}
			
			UaLoadEnd = true;
			CommonInfo.getInstance().persist();
			break;
		case Requester3.RESPONSE_TYPE_AUTOLOGIN:
			GsonResponse3.autoLoginResponse alResponse = (GsonResponse3.autoLoginResponse) msg.obj;
			if(alResponse==null){
			}else if(alResponse.status.equals("0")){
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
			}else{
			}
			break;
		case HANDLER_FLAG_LOADING_END:
			
			SharedPreferences sp = getSharedPreferences(GuideActivity.SP_NAME, MODE_PRIVATE);
			if (sp.getInt(SP_KEY, 0) == 0){
				sp.edit().putInt(SP_KEY, 1).commit();
				launchWelcomeActivity();
			}else{
				launchMainActivity();
			}
			
			break;
		case LOGIN_THREE_TIMEOUT:
			waitUntilPeriod();
			break;
		}
		return false;
	}
	
	@Override
	public void onBackPressed(){
	}

	@Override
	public void onClick(View v) {
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
		if(requestCode == REQUEST_VIDEOSHOT){
			Intent intenthome = new Intent(this, LookLookActivity.class);
			startActivity(intenthome);
			overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out); 
			finish();
		}
	}


	private void launchWelcomeActivity() {
		
		saveAccount();
		
		Intent intent = new Intent(this, GuideActivity.class);
		intent.putExtra(GuideActivity.FROM_WELCOME, true);
		startActivity(intent);
		overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out); 
		finish();
	}
	
	private void waitUntilPeriod(){
		
		long cur_time = System.currentTimeMillis();
		
		long wait_time = LOADING_PERIOD_INMS - (cur_time-start_time);
		if(wait_time<0){
			wait_time = 0;
		}
		if(wait_time > LOADING_PERIOD_INMS){
			wait_time = LOADING_PERIOD_INMS;
		}

		getHandler().sendEmptyMessageDelayed(HANDLER_FLAG_LOADING_END, wait_time);
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
							MicShareWelcomeActivity.launchWebBrower(activity, path);

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
