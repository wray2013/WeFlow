package com.cmmobi.looklook.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Message;
import android.support.v4.content.LocalBroadcastManager;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import cn.zipper.framwork.core.ZActivity;
import cn.zipper.framwork.core.ZDialog;

import com.cmmobi.looklook.MainApplication;
import com.cmmobi.looklook.R;
import com.cmmobi.looklook.common.constant.Constant;
import com.cmmobi.looklook.common.gson.GsonResponse2;
import com.cmmobi.looklook.common.gson.Requester2;
import com.cmmobi.looklook.common.utils.CmmobiClickAgentWrapper;
import com.cmmobi.looklook.common.utils.UmengclickAgentWrapper;
import com.cmmobi.looklook.info.profile.AccountInfo;
import com.cmmobi.looklook.info.profile.ActiveAccount;
import com.cmmobi.looklook.info.profile.LoginSettingManager;
import com.cmmobi.looklook.prompt.Prompt;

/**
 * 隐私设置界面
 * 
 * @author youtian
 * 
 */
public class SettingPrivacyActivity extends ZActivity implements AnimationListener{

	//private RelativeLayout rl_loginpwd; //登录密码

	private RelativeLayout rl_gesturepwd; //手势密码
	private TextView tv_gesturepwdison; //是否开启手势密码

	//私信
	private ImageView iv_privmsgsome;// 我关注的
	private ImageView iv_privmsgall;// 所有人

	//我的朋友
	private ImageView iv_friendsmyself;// 我的朋友 仅自己
	private ImageView iv_friendssome;// 我的朋友 我关注的
	private ImageView iv_friendsall;// 我的朋友 所有人

	//内容
	private ImageView iv_contentsome;// 内容 我关注的
	private ImageView iv_contentall;// 内容 所有人

	//位置
	private ImageView iv_locationmyself;// 位置 仅自己
	private ImageView iv_locationsome;// 位置 我关注
	private ImageView iv_locationall;// 位置 所有人

	//语音
	private ImageView iv_voicemyself;// 语音 仅自己
	private ImageView iv_voicesome;// 语音 我关注
	private ImageView iv_voiceall;// 语音 所有人

	private AccountInfo accountInfo;
	private LoginSettingManager lsm;
	private String privmsg;// 标示 私信
	private String friends;// 标示 朋友
/*	private String content;// 内容
	private String location;// 位置
	private String voice;// 声音
*/	
	private ImageView iv_back;// 返回
	
	private BroadcastReceiver mBroadcastReceiver_exit;

	private static String TAG = "SettingPrivacyActivity";
/*	private LinearLayout changevoice;
	private RelativeLayout changevoice_progress;*/
	
	private TranslateAnimation translate_primsgSomeToAll;
	private TranslateAnimation translate_primsgAllToSome;
	private TranslateAnimation translate_friendsMyselfToSome;
	private TranslateAnimation translate_friendsMyselfToAll;
	private TranslateAnimation translate_friendsSomeToMyself;
	private TranslateAnimation translate_friendsSomeToAll;
	private TranslateAnimation translate_friendsAllToMyself;
	private TranslateAnimation translate_friendsAllToSome;
	private int duration = 300;
	private int duration_short = 150;
	
	private Context context;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_setting_private);
		context = this;
		String uid = ActiveAccount.getInstance(this).getUID();
		accountInfo = AccountInfo.getInstance(uid);
		if(accountInfo != null){
			lsm = accountInfo.setmanager;
		}

		/*changevoice = (LinearLayout) findViewById(R.id.changevoice);
		changevoice_progress = (RelativeLayout) findViewById(R.id.changevoice_progress);
*/
		iv_back = (ImageView) findViewById(R.id.iv_back);
		iv_back.setOnClickListener(this);
		
		//rl_loginpwd = (RelativeLayout) findViewById(R.id.rl_loginpwd);
		//rl_loginpwd.setOnClickListener(this);
		
		rl_gesturepwd = (RelativeLayout) findViewById(R.id.rl_gesturepwd);
		rl_gesturepwd.setOnClickListener(this);
		tv_gesturepwdison = (TextView) findViewById(R.id.tv_gesturepwdison);
		
		iv_privmsgsome = (ImageView) findViewById(R.id.iv_privmsgsome);
		iv_privmsgsome.setOnClickListener(this);
		iv_privmsgall = (ImageView) findViewById(R.id.iv_privmsgall);
		iv_privmsgall.setOnClickListener(this);

		iv_friendsmyself = (ImageView) findViewById(R.id.iv_friendsmyself);
		iv_friendsmyself.setOnClickListener(this);
		iv_friendssome = (ImageView) findViewById(R.id.iv_friendssome);
		iv_friendssome.setOnClickListener(this);
		iv_friendsall = (ImageView) findViewById(R.id.iv_friendsall);
		iv_friendsall.setOnClickListener(this);

		/*iv_contentsome = (ImageView) findViewById(R.id.iv_contentsome);
		iv_contentsome.setOnClickListener(this);
		iv_contentall = (ImageView) findViewById(R.id.iv_contentall);
		iv_contentall.setOnClickListener(this);

		iv_locationmyself = (ImageView) findViewById(R.id.iv_locationmyself);
		iv_locationmyself.setOnClickListener(this);
		iv_locationsome = (ImageView) findViewById(R.id.iv_locationsome);
		iv_locationsome.setOnClickListener(this);
		iv_locationall = (ImageView) findViewById(R.id.iv_locationall);
		iv_locationall.setOnClickListener(this);

	
		iv_voicemyself = (ImageView) findViewById(R.id.iv_voicemyself);
		iv_voicemyself.setOnClickListener(this);
		iv_voicesome = (ImageView) findViewById(R.id.iv_voicesome);
		iv_voicesome.setOnClickListener(this);
		iv_voiceall = (ImageView) findViewById(R.id.iv_voiceall);
		iv_voiceall.setOnClickListener(this);
		*/
		privmsg = lsm.getPrivmsg_type();//getIntent().getExtras().getString("privmsg");// 私信
		friends = lsm.getFriends_type();//getIntent().getExtras().getString("friends");// 我的朋友
		/*content = getIntent().getExtras().getString("content");// 我的内容
		location = getIntent().getExtras().getString("location");// 我的位置
		voice = getIntent().getExtras().getString("voice");// 我的语音
		*/
		if(privmsg != null && !privmsg.equals("")){
			if (privmsg.equals(LoginSettingManager.GROUP_ALL)) {// 所有人
				iv_privmsgall.setAlpha(255);
				iv_privmsgsome.setAlpha(0);
			} else if (privmsg.equals(LoginSettingManager.GROUP_SOME)) {// 我关注的
				iv_privmsgall.setAlpha(0);
				iv_privmsgsome.setAlpha(255);
			}
		}else{
			privmsg = LoginSettingManager.GROUP_ALL;
			iv_privmsgall.setAlpha(255);
			iv_privmsgsome.setAlpha(0);
		}

		if(friends != null && !friends.equals("")){
			if (friends.equals(LoginSettingManager.GROUP_ALL)) {// 全部人可见
				iv_friendsall.setAlpha(255);
				iv_friendssome.setAlpha(0);
				iv_friendsmyself.setAlpha(0);
			} else if (friends.equals(LoginSettingManager.GROUP_SOME)) {// 关注人可见
				iv_friendsall.setAlpha(0);
				iv_friendssome.setAlpha(255);
				iv_friendsmyself.setAlpha(0);
			} else if (friends.equals(LoginSettingManager.GROUP_MYSELF)) {// 尽自己可见
				iv_friendsall.setAlpha(0);
				iv_friendssome.setAlpha(0);
				iv_friendsmyself.setAlpha(255);
			}
		}else{
			friends = LoginSettingManager.GROUP_SOME;
			iv_friendsall.setAlpha(0);
			iv_friendssome.setAlpha(255);
			iv_friendsmyself.setAlpha(0);
		}
		
		/*// ===============我的内容
		if(content != null && !content.equals("")){
			if (content.equals(LoginSettingManager.GROUP_ALL)) {
				iv_contentall.setAlpha(255);
				iv_contentsome.setAlpha(0);
			} else if (content.equals(LoginSettingManager.GROUP_SOME)) {
				iv_contentall.setAlpha(0);
				iv_contentsome.setAlpha(255);
			}
		}else{
			content = LoginSettingManager.GROUP_ALL;
			iv_contentall.setAlpha(255);
			iv_contentsome.setAlpha(0);
		}
		
		// ===============地理位置
		if(location != null && !location.equals("")){
			if (location.equals(LoginSettingManager.GROUP_ALL)) {
				iv_locationall.setAlpha(255);
				iv_locationsome.setAlpha(0);
				iv_locationmyself.setAlpha(0);
			} else if (location.equals(LoginSettingManager.GROUP_SOME)) {
				iv_locationall.setAlpha(0);
				iv_locationsome.setAlpha(255);  
				iv_locationmyself.setAlpha(0);
			} else if (location.equals(LoginSettingManager.GROUP_MYSELF)) {
				iv_locationall.setAlpha(0);
				iv_locationsome.setAlpha(0);
				iv_locationmyself.setAlpha(255);
			}
		}else{
			location = LoginSettingManager.GROUP_ALL;
			iv_locationall.setAlpha(255);
			iv_locationsome.setAlpha(0);
			iv_locationmyself.setAlpha(0);
		}
		
		// ===================语音
		if(voice != null && !voice.equals("")){
			if (voice.equals(LoginSettingManager.GROUP_ALL)) {
				iv_voiceall.setAlpha(255);
				iv_voicesome.setAlpha(0);
				iv_voicemyself.setAlpha(0);
			} else if (voice.equals(LoginSettingManager.GROUP_SOME)) {
				iv_voiceall.setAlpha(0);
				iv_voicesome.setAlpha(255);
				iv_voicemyself.setAlpha(0);
			} else if (voice.equals(LoginSettingManager.GROUP_MYSELF)) {
				iv_voiceall.setAlpha(0);
				iv_voicesome.setAlpha(0);
				iv_voicemyself.setAlpha(255);
			}
		}else{
			voice = LoginSettingManager.GROUP_ALL;
			iv_voiceall.setAlpha(255);
			iv_voicesome.setAlpha(0);
			iv_voicemyself.setAlpha(0);
		}
		*/
		IntentFilter filter_exit = new IntentFilter(HomeActivity.FLAG_CLOSE_ACTIVITY);
		mBroadcastReceiver_exit = new BroadcastReceiver() {

			@Override
			public void onReceive(Context context, Intent intent) {
				//HomeActivity.this.unregisterReceiver(this);
				SettingPrivacyActivity.this.finish();
			}
		};
		registerReceiver(mBroadcastReceiver_exit, filter_exit);
	}
	
	@Override
	public void onResume() {
		super.onResume();
		UmengclickAgentWrapper.onResume(this);
		CmmobiClickAgentWrapper.onResume(this);
		if(lsm.getSafeIsOn()){
			tv_gesturepwdison.setText(R.string.is_on);
		}else{
			tv_gesturepwdison.setText(R.string.is_off);
		}
		
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
		// TODO Auto-generated method stub
		super.onDestroy();
		unregisterReceiver(mBroadcastReceiver_exit); 
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.iv_back:
			/*Intent data=new Intent();  
			data.putExtra("privmsg", privmsg);  
			data.putExtra("friends", friends);
			data.putExtra("content", content);
			data.putExtra("location", location);
			data.putExtra("voice", voice);
			Log.e(TAG, privmsg + friends + content + location + voice);
			setResult(RESULT_OK, data);  
			this.finish();  */
			if (privmsg.equals(lsm.getPrivmsg_type())
					&& friends.equals(lsm.getFriends_type())){
			   this.finish();
			}else{
			ZDialog.show(R.layout.progressdialog, false, true, this);
			Requester2.setPrivacy(handler, privmsg, friends, null, null, null,
					null, null, null);
			}
			break;
/*		case R.id.rl_loginpwd:
			if(ActiveAccount.getInstance(this).snstype.equals("0")){
				Intent intent = new Intent(this, SettingLoginPwdActivity.class);
				startActivity(intent);
			}else{
				Prompt.Dialog(this, false, "提示", "第三方登录，无法进行此操作", null);
			}
			break;*/
		case R.id.rl_gesturepwd:
			Intent in;
			lsm.setIsFromSetting(true);
			if(lsm.getGesturepassword() == null){
					in = new Intent(this, SettingToCreateGestureActivity.class);
					startActivity(in);
			}else{
				in = new Intent(this, SettingGesturePwdActivity.class);
				in.putExtra("count", 0);
				startActivity(in);
			}
			break;
		case R.id.iv_privmsgsome:// 私信 我关注的
			if(translate_primsgAllToSome == null){
				 int[] alllocations = new int[2];
			     iv_privmsgall.getLocationOnScreen(alllocations);
			     int[] somelocations = new int[2];
			     iv_privmsgsome.getLocationOnScreen(somelocations);
			     translate_primsgAllToSome = new TranslateAnimation(0, somelocations[0] - alllocations[0], 0, 0);
			     translate_primsgAllToSome.setDuration(duration);
			     translate_primsgAllToSome.setAnimationListener(this);
			}		
			iv_privmsgall.startAnimation(translate_primsgAllToSome);
			break;
		case R.id.iv_privmsgall:// 私信 所有人
			if(translate_primsgSomeToAll == null){
				 int[] alllocations = new int[2];
			     iv_privmsgall.getLocationOnScreen(alllocations);
			     int[] somelocations = new int[2];
			     iv_privmsgsome.getLocationOnScreen(somelocations);
			     translate_primsgSomeToAll = new TranslateAnimation(0, alllocations[0] - somelocations[0], 0, 0);
			     translate_primsgSomeToAll.setDuration(duration);
			     translate_primsgSomeToAll.setAnimationListener(this);
			}		
			iv_privmsgsome.startAnimation(translate_primsgSomeToAll);
			break;
		case R.id.iv_friendsmyself:
			if(friends.equals(LoginSettingManager.GROUP_ALL)){
				if(translate_friendsAllToMyself == null){
					 int[] alllocations = new int[2];
				     iv_friendsall.getLocationOnScreen(alllocations);
				     int[] myselflocations = new int[2];
				     iv_friendsmyself.getLocationOnScreen(myselflocations);
				     translate_friendsAllToMyself = new TranslateAnimation(0, myselflocations[0] - alllocations[0], 0, 0);
				     translate_friendsAllToMyself.setDuration(duration);
				     translate_friendsAllToMyself.setAnimationListener(this);
				}	
				iv_friendsall.startAnimation(translate_friendsAllToMyself);
			}else if(friends.equals(LoginSettingManager.GROUP_SOME)){
				if(translate_friendsSomeToMyself == null){
					 int[] somelocations = new int[2];
				     iv_friendssome.getLocationOnScreen(somelocations);
				     int[] myselflocations = new int[2];
				     iv_friendsmyself.getLocationOnScreen(myselflocations);
				     translate_friendsSomeToMyself = new TranslateAnimation(0, myselflocations[0] - somelocations[0], 0, 0);
				     translate_friendsSomeToMyself.setDuration(duration_short);
				     translate_friendsSomeToMyself.setAnimationListener(this);
				}	
				iv_friendssome.startAnimation(translate_friendsSomeToMyself);
			}
			break;
		case R.id.iv_friendssome:
			if(friends.equals(LoginSettingManager.GROUP_ALL)){
				if(translate_friendsAllToSome == null){
					 int[] alllocations = new int[2];
				     iv_friendsall.getLocationOnScreen(alllocations);
				     int[] somelocations = new int[2];
				     iv_friendssome.getLocationOnScreen(somelocations);
				     translate_friendsAllToSome = new TranslateAnimation(0, somelocations[0] - alllocations[0], 0, 0);
				     translate_friendsAllToSome.setDuration(duration_short);
				     translate_friendsAllToSome.setAnimationListener(this);
				}	
				iv_friendsall.startAnimation(translate_friendsAllToSome);
			}else if(friends.equals(LoginSettingManager.GROUP_MYSELF)){
				if(translate_friendsMyselfToSome == null){
					 int[] somelocations = new int[2];
				     iv_friendssome.getLocationOnScreen(somelocations);
				     int[] myselflocations = new int[2];
				     iv_friendsmyself.getLocationOnScreen(myselflocations);
				     translate_friendsMyselfToSome = new TranslateAnimation(0, somelocations[0] - myselflocations[0], 0, 0);
				     translate_friendsMyselfToSome.setDuration(duration_short);
				     translate_friendsMyselfToSome.setAnimationListener(this);
				}	
				iv_friendsmyself.startAnimation(translate_friendsMyselfToSome);
			}
			break;
		case R.id.iv_friendsall:
			if(friends.equals(LoginSettingManager.GROUP_SOME)){
				if(translate_friendsSomeToAll == null){
					 int[] alllocations = new int[2];
				     iv_friendsall.getLocationOnScreen(alllocations);
				     int[] somelocations = new int[2];
				     iv_friendssome.getLocationOnScreen(somelocations);
				     translate_friendsSomeToAll = new TranslateAnimation(0, alllocations[0] - somelocations[0], 0, 0);
				     translate_friendsSomeToAll.setDuration(duration_short);
				     translate_friendsSomeToAll.setAnimationListener(this);
				}	
				iv_friendssome.startAnimation(translate_friendsSomeToAll);
			}else if(friends.equals(LoginSettingManager.GROUP_MYSELF)){
				if(translate_friendsMyselfToAll == null){
					 int[] alllocations = new int[2];
				     iv_friendsall.getLocationOnScreen(alllocations);
				     int[] myselflocations = new int[2];
				     iv_friendsmyself.getLocationOnScreen(myselflocations);
				     translate_friendsMyselfToAll = new TranslateAnimation(0, alllocations[0] - myselflocations[0], 0, 0);
				     translate_friendsMyselfToAll.setDuration(duration);
				     translate_friendsMyselfToAll.setAnimationListener(this);
				}	
				iv_friendsmyself.startAnimation(translate_friendsMyselfToAll);
			}
			break;
		/*case R.id.iv_contentsome:
			iv_contentall.setAlpha(0);
			iv_contentsome.setAlpha(255);
			content = LoginSettingManager.GROUP_SOME;
			break;
		case R.id.iv_contentall:
			iv_contentall.setAlpha(255);
			iv_contentsome.setAlpha(0);
			content = LoginSettingManager.GROUP_ALL;
			break;
		case R.id.iv_locationmyself:
			iv_locationall.setAlpha(0);
			iv_locationsome.setAlpha(0);
			iv_locationmyself.setAlpha(255);
			location = LoginSettingManager.GROUP_MYSELF;
			break;
		case R.id.iv_locationsome:
			iv_locationall.setAlpha(0);
			iv_locationsome.setAlpha(255);
			iv_locationmyself.setAlpha(0);
			location = LoginSettingManager.GROUP_SOME;
			break;
		case R.id.iv_locationall:
			iv_locationall.setAlpha(255);
			iv_locationsome.setAlpha(0);
			iv_locationmyself.setAlpha(0);
			location = LoginSettingManager.GROUP_ALL;
			break;
		case R.id.iv_voicemyself:
			iv_voiceall.setAlpha(0);
			iv_voicesome.setAlpha(0);
			iv_voicemyself.setAlpha(255);
			voice = LoginSettingManager.GROUP_MYSELF;
			changevoice.setVisibility(View.GONE);
			changevoice_progress.setVisibility(View.GONE);
			break;
		case R.id.iv_voicesome:
			iv_voiceall.setAlpha(0);
			iv_voicesome.setAlpha(255);
			iv_voicemyself.setAlpha(0);
			voice = LoginSettingManager.GROUP_SOME;
			changevoice.setVisibility(View.VISIBLE);
			changevoice_progress.setVisibility(View.VISIBLE);
			break;
		case R.id.iv_voiceall:
			iv_voiceall.setAlpha(255);
			iv_voicesome.setAlpha(0);
			iv_voicemyself.setAlpha(0);
			voice = LoginSettingManager.GROUP_ALL;
			changevoice.setVisibility(View.GONE);
			changevoice_progress.setVisibility(View.GONE);
			break;*/
		}
	}

	@Override
	public boolean handleMessage(Message msg) {
		// TODO Auto-generated method stub
		switch (msg.what) {
		case Requester2.RESPONSE_TYPE_DIARY_PRIVACY:
			ZDialog.dismiss();
			try {
				GsonResponse2.diaryPrivacyResponse dp = (GsonResponse2.diaryPrivacyResponse) msg.obj;
				if (dp != null) {
					if (dp.status.equals("0")) {
						lsm.setPrivmsg_type(privmsg);
						lsm.setFriends_type(friends);
						/*
						 * lsm.setDiary_type(content);
						 * lsm.setPosition_type(location);
						 * lsm.setAudio_type(voice);
						 */
						Intent intent = new Intent(
								SettingActivity.BROADCAST_PRIVACY_CHANGED);
						LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
						Prompt.Dialog(this, false, "提示", "设置更改成功",
								new DialogInterface.OnClickListener() {

									@Override
									public void onClick(DialogInterface dialog,
											int which) {
											SettingPrivacyActivity.this.finish();
									}
								});
					} else if (dp.status.equals("200600")) {
						Prompt.Dialog(this, false, "提示",
								Constant.CRM_STATUS[Integer
										.parseInt(dp.crm_status)],
								new DialogInterface.OnClickListener() {

									@Override
									public void onClick(DialogInterface dialog,
											int which) {
										// TODO Auto-generated method stub
										SettingPrivacyActivity.this.finish();
									}
								});
					} else {
						Prompt.Dialog(this, false, "提示", "操作失败，请稍后再试",
								new DialogInterface.OnClickListener() {

									@Override
									public void onClick(DialogInterface dialog,
											int which) {
										// TODO Auto-generated method stub
										 SettingPrivacyActivity.this.finish();
									}
								});
					}
				} else {
					Prompt.Dialog(this, false, "提示", "操作失败，网络不给力",
							new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									// TODO Auto-generated method stub
									SettingPrivacyActivity.this.finish();
								}
							});
				}
			} catch (Exception e) {
				// TODO: handle exception
			}
			break;
		}
		return false;
	}

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		/*Intent data=new Intent();  
		data.putExtra("privmsg", privmsg);  
		data.putExtra("friends", friends);
		data.putExtra("content", content);
		data.putExtra("location", location);
		data.putExtra("voice", voice);
		Log.e(TAG, "=2=" + privmsg + friends + content + location + voice);
		setResult(RESULT_OK, data);  */
		if (privmsg.equals(lsm.getPrivmsg_type())
				&& friends.equals(lsm.getFriends_type())){
			super.onBackPressed();
		}else{
		ZDialog.show(R.layout.progressdialog, false, true, this);
		Requester2.setPrivacy(handler, privmsg, friends, null, null, null,
				null, null, null);
		}
		
	}

	@Override
	public void onAnimationEnd(Animation animation) {
		// TODO Auto-generated method stub
		if(animation.equals(translate_primsgAllToSome)){
			iv_privmsgall.setAlpha(0);
			iv_privmsgsome.setAlpha(255);
			privmsg = LoginSettingManager.GROUP_SOME;
		}else if(animation.equals(translate_primsgSomeToAll)){
			iv_privmsgall.setAlpha(255);
			iv_privmsgsome.setAlpha(0);
			privmsg = LoginSettingManager.GROUP_ALL;
		}else if(animation.equals(translate_friendsAllToMyself) || animation.equals(translate_friendsSomeToMyself)){
			iv_friendsall.setAlpha(0);
			iv_friendssome.setAlpha(0);
			iv_friendsmyself.setAlpha(255);
			friends = LoginSettingManager.GROUP_MYSELF;
		}else if(animation.equals(translate_friendsAllToSome) || animation.equals(translate_friendsMyselfToSome)){
			iv_friendsall.setAlpha(0);
			iv_friendssome.setAlpha(255);
			iv_friendsmyself.setAlpha(0);
			friends = LoginSettingManager.GROUP_SOME;
		}else if(animation.equals(translate_friendsMyselfToAll) || animation.equals(translate_friendsSomeToAll)){
			iv_friendsall.setAlpha(255);
			iv_friendssome.setAlpha(0);
			iv_friendsmyself.setAlpha(0);
			friends = LoginSettingManager.GROUP_ALL;
		}
	}

	@Override
	public void onAnimationRepeat(Animation animation) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onAnimationStart(Animation animation) {
		// TODO Auto-generated method stub
		
	}


	
}
