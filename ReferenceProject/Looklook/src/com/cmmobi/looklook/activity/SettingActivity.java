package com.cmmobi.looklook.activity;

import java.util.ArrayList;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Message;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import cn.jpush.android.api.JPushInterface;
import cn.zipper.framwork.core.ZActivity;
import cn.zipper.framwork.core.ZDialog;
import cn.zipper.framwork.core.ZToast;

import com.cmmobi.looklook.MainApplication;
import com.cmmobi.looklook.R;
import com.cmmobi.looklook.common.constant.Constant;
import com.cmmobi.looklook.common.gson.GsonResponse2;
import com.cmmobi.looklook.common.gson.CRM_Object.recItem;
import com.cmmobi.looklook.common.gson.GsonResponse2.MyBind;
import com.cmmobi.looklook.common.gson.CRMRequester;
import com.cmmobi.looklook.common.gson.CRM_Object;
import com.cmmobi.looklook.common.gson.Requester2;
import com.cmmobi.looklook.common.gson.WeiboRequester;
import com.cmmobi.looklook.common.gson.WeiboResponse.RWUserInfo;
import com.cmmobi.looklook.common.gson.WeiboResponse.SWUserInfo;
import com.cmmobi.looklook.common.gson.WeiboResponse.TWUserInfo;
import com.cmmobi.looklook.common.utils.CmmobiClickAgentWrapper;
import com.cmmobi.looklook.common.utils.UmengclickAgentWrapper;
import com.cmmobi.looklook.info.profile.AccountInfo;
import com.cmmobi.looklook.info.profile.ActiveAccount;
import com.cmmobi.looklook.info.profile.DiaryManager;
import com.cmmobi.looklook.info.profile.LoginSettingManager;
import com.cmmobi.looklook.info.profile.TimeHelper;
import com.cmmobi.looklook.prompt.Prompt;
import com.cmmobi.sns.api.CmmobiSnsLib;
import com.cmmobi.sns.oauthv2.OAuthV2;
import com.cmmobi.sns.utils.ConfigUtil.SHARE_TO;
import com.nostra13.universalimageloader.api.AnimateFirstDisplayListener;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageLoadingListener;
import com.nostra13.universalimageloader.core.display.SimpleBitmapDisplayer;
import com.weibo.sdk.android.api.WeiboAuthListener;
import com.weibo.sdk.android.api.WeiboDialogError;
import com.weibo.sdk.android.api.WeiboException;

/**
 * 设置界面
 * 
 * @author youtian
 * 
 */

public class SettingActivity extends ZActivity implements AnimationListener {

	private ImageView iv_back;

	private ImageView iv_feedBack;// 意见反馈

	// sync_type setting
	private ImageView iv_type_wifi;
	private ImageView iv_type_any;
	private String sysc_type;
	public static final String SYSC_TYPE_WIFI = "2";
	public static final String SYSC_TYPE_ANY = "3";

	// launch_type setting
	private ImageView iv_type_watch;// 观看模式
	private ImageView iv_type_shoot;// 拍摄模式
	private String launch_type;
	public static final String LAUNCH_TYPE_WATCH = "1";
	public static final String LAUNCH_TYPE_SHOOT = "2";

/*	private String privmsg;// 标示 私信
	private String friends;// 标示 朋友
*/	/*
	 * private String content;// 内容 private String location;// 位置 private String
	 * voice;// 声音
	 */

	private static final String TAG = "SettingActivity";

	// 手机账号绑定
	private RelativeLayout rl_phonebind;
	private TextView tv_phonebindstate;
	private MyBind phonebindstate = null;

	/*
	 * // 手机号(保险箱)绑定 private RelativeLayout rl_safe; private TextView
	 * tv_safestate; private MyBind safestate = null;
	 */

	// 邮箱绑定
	private RelativeLayout rl_emailbind;
	private TextView tv_emailbindstate;
	private MyBind emailbindstate = null;

	private RelativeLayout rl_loginpwd; // 登录密码

	// 新浪微博绑定
	private RelativeLayout rl_sinaweibo;
	private TextView tv_sinaweibostate;
	private MyBind sinaweibostate = null;

	// 腾讯微博绑定
	private RelativeLayout rl_tencentweibo;
	private TextView tv_tencentweibostate;
	private MyBind tencentweibostate = null;

	// 人人网绑定
	private RelativeLayout rl_renren;
	private TextView tv_renrenstate;
	private MyBind renrenstate = null;

	// 账号设置
	private RelativeLayout rl_personalinfo;
	private RelativeLayout rl_privacyset;
	private RelativeLayout rl_storagemanage;

	private RelativeLayout rl_about;

	private Button btn_exit;

	private ImageView iv_recommend;
	private PopupWindow pop_recommend;
	private LayoutInflater recommendinflater;
	private ImageView iv_dismiss;
	private ImageView iv_more;

	private LocalBroadcastManager lbc;
	private ActiveAccount activeAccount;
	private AccountInfo accountInfo;
	private LoginSettingManager lsm;
	private Context context;

	private BroadcastReceiver mBroadcastReceiver_exit;

	// =====================绑定微博

	private final int HANDLER_SINA_AUTHOR_SUCCESS = 0x12384811;
	private final int HANDLER_RENREN_AUTHOR_SUCCESS = 0x12384812;
	private final int HANDLER_TENCENT_AUTHOR_SUCCESS = 0x12384816;

	private final int REQUEST_PRIVACY = 0x12341;

	public static final String BROADCAST_PORTRAIT_CHANGED = "PORTRAIT_CHANGED";
	public static final String BROADCAST_PERSONALINFO_CHANGED = "PERSONALINFO_CHANGED";
	public static final String BROADCAST_PRIVACY_CHANGED = "PRIVACY_CHANGED";

	private boolean isClose = false;

	MyBind myBind;

	private TranslateAnimation translate_WifiToAny; // 动画效果
	private TranslateAnimation translate_AnyToWifi; // 动画效果
	private TranslateAnimation translate_WatchToShoot;
	private TranslateAnimation translate_ShootToWatch;
	private int duration = 300;

	// for UmengclickAgentWrapper
	private long begin = 0;
	private long end_sup_sha_bin = 0; // 绑定结束的时间点
	private long end_sup_sha = 0; // 绑定离开第三方页面的时间点

	private ImageView iv_recommend01, iv_recommend02, iv_recommend03,
			iv_recommend04;
	
	ArrayList<CRM_Object.recItem> recItem = new ArrayList<CRM_Object.recItem>();
	private View view_recommend;

	// 使用开源的webimageloader
	private DisplayImageOptions options;
	protected ImageLoader imageLoader;
	private ImageLoadingListener animateFirstListener;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setting_main);
		
		recommendinflater = LayoutInflater.from(this);
		view_recommend = recommendinflater.inflate(
				R.layout.activity_setting_recommend, null);

		animateFirstListener = new AnimateFirstDisplayListener();
		imageLoader = ImageLoader.getInstance();
		imageLoader.init(ImageLoaderConfiguration.createDefault(SettingActivity.this));
		options = new DisplayImageOptions.Builder()
				.showStubImage(R.drawable.sina)
				.showImageForEmptyUri(R.drawable.sina)
				.showImageOnFail(R.drawable.sina).cacheInMemory(true)
				.cacheOnDisc(true).displayer(new SimpleBitmapDisplayer())
				// .displayer(new CircularBitmapDisplayer()) 圆形图片
				// .displayer(new RoundedBitmapDisplayer(20)) 圆角图片
				.build();

		// back
		iv_back = (ImageView) findViewById(R.id.iv_back);
		iv_back.setOnClickListener(this);

		// 意见反馈
		iv_feedBack = (ImageView) findViewById(R.id.iv_feedback);
		iv_feedBack.setOnClickListener(this);

		// launch_type setting
		iv_type_watch = (ImageView) findViewById(R.id.iv_type_watch);
		iv_type_shoot = (ImageView) findViewById(R.id.iv_type_shoot);
		iv_type_watch.setAlpha(0);
		iv_type_watch.setOnClickListener(this);
		iv_type_shoot.setOnClickListener(this);

		// sync_type setting
		iv_type_wifi = (ImageView) findViewById(R.id.iv_type_wifi);
		iv_type_any = (ImageView) findViewById(R.id.iv_type_any);
		iv_type_any.setAlpha(0);
		iv_type_wifi.setOnClickListener(this);
		iv_type_any.setOnClickListener(this);

		// 手机账号绑定
		rl_phonebind = (RelativeLayout) findViewById(R.id.rl_phonebind);
		rl_phonebind.setOnClickListener(this);
		tv_phonebindstate = (TextView) findViewById(R.id.tv_phonebindstate);

		/*
		 * // 手机号（保险箱）绑定 rl_safe = (RelativeLayout) findViewById(R.id.rl_safe);
		 * rl_safe.setOnClickListener(this); tv_safestate = (TextView)
		 * findViewById(R.id.tv_safestate);
		 */

		// 邮箱绑定
		rl_emailbind = (RelativeLayout) findViewById(R.id.rl_emailbind);
		rl_emailbind.setOnClickListener(this);
		tv_emailbindstate = (TextView) findViewById(R.id.tv_emailbindstate);

		// 修改密码
		rl_loginpwd = (RelativeLayout) findViewById(R.id.rl_loginpwd);
		rl_loginpwd.setOnClickListener(this);

		// 新浪微博绑定
		rl_sinaweibo = (RelativeLayout) findViewById(R.id.rl_sinaweibo);
		rl_sinaweibo.setOnClickListener(this);
		tv_sinaweibostate = (TextView) findViewById(R.id.tv_sinabindstate);

		// 腾讯微博绑定
		rl_tencentweibo = (RelativeLayout) findViewById(R.id.rl_tencentweibo);
		rl_tencentweibo.setOnClickListener(this);
		tv_tencentweibostate = (TextView) findViewById(R.id.tv_tencentbindstate);

		// 人人网绑定
		rl_renren = (RelativeLayout) findViewById(R.id.rl_renren);
		rl_renren.setOnClickListener(this);
		tv_renrenstate = (TextView) findViewById(R.id.tv_renrenstate);

		// 账号设置
		rl_personalinfo = (RelativeLayout) findViewById(R.id.rl_personalinfo);
		rl_personalinfo.setOnClickListener(this);
		rl_privacyset = (RelativeLayout) findViewById(R.id.rl_privacyset);
		rl_privacyset.setOnClickListener(this);
		rl_storagemanage = (RelativeLayout) findViewById(R.id.rl_storagemanage);
		rl_storagemanage.setOnClickListener(this);

		// about
		rl_about = (RelativeLayout) findViewById(R.id.rl_about);
		rl_about.setOnClickListener(this);

		// exit
		btn_exit = (Button) findViewById(R.id.btn_exit);
		btn_exit.setOnClickListener(this);

		CRMRequester.getRecommend(handler, "101", "3");
		// 推荐
		iv_recommend = (ImageView) findViewById(R.id.iv_recommend);
		iv_recommend.setOnClickListener(this);


		lbc = LocalBroadcastManager.getInstance(this);
		context = this;

		activeAccount = ActiveAccount.getInstance(this);
		String userID = activeAccount.getUID();
		accountInfo = AccountInfo.getInstance(userID);
		if (accountInfo != null) {
			lsm = accountInfo.setmanager;
			// launch_type setting
			launch_type = lsm.getLaunch_type();
			if (launch_type != null && !launch_type.equals("")) {
				if (launch_type.equals(LAUNCH_TYPE_WATCH)) {
					iv_type_watch.setAlpha(255);
					iv_type_shoot.setAlpha(0);
				} else if (launch_type.equals(LAUNCH_TYPE_SHOOT)) {
					iv_type_watch.setAlpha(0);
					iv_type_shoot.setAlpha(255);
				}
			} else {
				launch_type = LAUNCH_TYPE_SHOOT;
				iv_type_watch.setAlpha(0);
				iv_type_shoot.setAlpha(255);
			}
			// sync_type setting
			sysc_type = lsm.getSysc_type();
			if (sysc_type != null && !sysc_type.equals("")) {
				if (sysc_type.equals(SYSC_TYPE_WIFI)) {
					iv_type_any.setAlpha(0);
					iv_type_wifi.setAlpha(255);
				} else if (sysc_type.equals(SYSC_TYPE_ANY)) {
					iv_type_any.setAlpha(255);
					iv_type_wifi.setAlpha(0);
				}
			} else {
				sysc_type = SYSC_TYPE_ANY;
				iv_type_any.setAlpha(255);
				iv_type_wifi.setAlpha(0);
			}

			/*privmsg = lsm.getPrivmsg_type();// 私信
			if (privmsg == null || privmsg.equals("")) {
				privmsg = LoginSettingManager.GROUP_ALL;
			}

			friends = lsm.getFriends_type();// 我的朋友
			if (friends == null || friends.equals("")) {
				friends = LoginSettingManager.GROUP_SOME;
			}
			
			 Log.e(TAG, "==0" + lsm.getPrivmsg_type() + lsm.getFriends_type() +lsm.getSysc_type() + lsm.getLaunch_type());
			 Log.e(TAG, "==1" + privmsg + friends +sysc_type + launch_type);
			*/
			/*
			 * content = lsm.getDiary_type();// 我的内容 if(content == null ||
			 * content.equals("")){ content = LoginSettingManager.GROUP_ALL; }
			 * location = lsm.getPosition_type();// 我的位置 if(location == null ||
			 * location.equals("")){ location = LoginSettingManager.GROUP_ALL; }
			 * voice = lsm.getAudio_type();// 我的语音 if(voice == null ||
			 * voice.equals("")){ voice = LoginSettingManager.GROUP_ALL; }
			 */

			// 新浪微博绑定
			sinaweibostate = lsm.getBinding_type(
					LoginSettingManager.BINDING_TYPE_SNS,
					LoginSettingManager.BINDING_SNS_TYPE_SINA);
			if (ActiveAccount.getInstance(this).isSNSBind("sina")) {
				if (sinaweibostate != null) {
					tv_sinaweibostate.setText(sinaweibostate.sns_nickname);
					System.out.println("== sina == "
							+ sinaweibostate.sns_nickname
							+ " = "
							+ ActiveAccount.getInstance(this).getSNSNick(
									SHARE_TO.SINA.ordinal()));
				} else {
					tv_sinaweibostate.setText(ActiveAccount.getInstance(this)
							.getSNSNick(SHARE_TO.SINA.ordinal()));
				}
			}

			// 腾讯微博绑定
			tencentweibostate = lsm.getBinding_type(
					LoginSettingManager.BINDING_TYPE_SNS,
					LoginSettingManager.BINDING_SNS_TYPE_TENCENT);
			if (ActiveAccount.getInstance(this).isSNSBind("tencent")) {
				if (tencentweibostate != null) {
					System.out.println("== tencent == "
							+ tencentweibostate.sns_nickname
							+ " = "
							+ ActiveAccount.getInstance(this).getSNSNick(
									SHARE_TO.TENC.ordinal()));
					tv_tencentweibostate
							.setText(tencentweibostate.sns_nickname);
				} else {
					tv_tencentweibostate.setText(ActiveAccount
							.getInstance(this).getSNSNick(
									SHARE_TO.TENC.ordinal()));

				}
			}

			// 人人网绑定
			renrenstate = lsm.getBinding_type(
					LoginSettingManager.BINDING_TYPE_SNS,
					LoginSettingManager.BINDING_SNS_TYPE_RENREN);
			if (ActiveAccount.getInstance(this).isSNSBind("renren")) {
				if (renrenstate != null) {
					System.out.println("== renren == "
							+ renrenstate.sns_nickname
							+ " = "
							+ ActiveAccount.getInstance(this).getSNSNick(
									SHARE_TO.RENREN.ordinal()));
					tv_renrenstate.setText(renrenstate.sns_nickname);
				} else {
					tv_renrenstate.setText(ActiveAccount.getInstance(this)
							.getSNSNick(SHARE_TO.RENREN.ordinal()));
				}
			}
		}

		IntentFilter filter_exit = new IntentFilter(
				HomeActivity.FLAG_CLOSE_ACTIVITY);
		mBroadcastReceiver_exit = new BroadcastReceiver() {

			@Override
			public void onReceive(Context context, Intent intent) {
				// HomeActivity.this.unregisterReceiver(this);
				SettingActivity.this.finish();
			}
		};
		registerReceiver(mBroadcastReceiver_exit, filter_exit);

	}

	@Override
	public void onResume() {
		super.onResume();
		UmengclickAgentWrapper.onResume(this);
		CmmobiClickAgentWrapper.onResume(this);
		// 读取账户原设置
		if (accountInfo != null) {
			lsm = accountInfo.setmanager;

			// 手机账号绑定
			phonebindstate = lsm.getBinding_type(
					LoginSettingManager.BINDING_TYPE_PHONE,
					LoginSettingManager.BINDING_INFO_POINTLESS);
			if (phonebindstate != null && phonebindstate.binding_info != null) {
				tv_phonebindstate.setText(phonebindstate.binding_info);
			}
			/*
			 * //手机号（保险箱）绑定 safestate =
			 * lsm.getBinding_type(LoginSettingManager.BINDING_TYPE_PHONE_SEC,
			 * LoginSettingManager.BINDING_INFO_POINTLESS); if (safestate !=
			 * null && safestate.binding_info != null) {
			 * tv_safestate.setText(safestate.binding_info); }
			 */
			// 邮箱绑定
			emailbindstate = lsm.getBinding_type(
					LoginSettingManager.BINDING_TYPE_EMAIL,
					LoginSettingManager.BINDING_INFO_POINTLESS);
			if (emailbindstate != null && emailbindstate.binding_info != null
					&& emailbindstate.email_status.equals("1")) {
				tv_emailbindstate.setText(emailbindstate.binding_info);
			} else {
				emailbindstate = null;
			}

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
		super.onDestroy();
		unregisterReceiver(mBroadcastReceiver_exit);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.iv_back:// 返回
			accountInfo.persist();
			activeAccount.persist();
			// Log.e(TAG, "==3" + privmsg + friends +sysc_type + launch_type);
			if (/*privmsg.equals(lsm.getPrivmsg_type())
					&& friends.equals(lsm.getFriends_type())
					&& 
						 * content.equals(lsm.getDiary_type()) &&
						 * location.equals(lsm.getPosition_type()) &&
						 * voice.equals(lsm.getAudio_type()) &&
						 */launch_type.equals(lsm.getLaunch_type())
					&& sysc_type.equals(lsm.getSysc_type())) {
				this.finish();
			} else {
				ZDialog.show(R.layout.progressdialog, false, true, this);
				Requester2.setPrivacy(handler, null, null, null, null,
						null, null, launch_type, sysc_type);
			}
			break;
		case R.id.iv_feedback:
			Intent feedback = new Intent(this, SettingFeedBackActivity.class);
			startActivity(feedback);
			break;
		case R.id.iv_type_wifi:
			if (translate_AnyToWifi == null) {
				int[] wifilocations = new int[2];
				iv_type_wifi.getLocationOnScreen(wifilocations);
				int[] anylocations = new int[2];
				iv_type_any.getLocationOnScreen(anylocations);
				translate_AnyToWifi = new TranslateAnimation(0,
						wifilocations[0] - anylocations[0], 0, 0);
				translate_AnyToWifi.setDuration(duration);
				translate_AnyToWifi.setAnimationListener(this);
			}
			iv_type_any.startAnimation(translate_AnyToWifi);
			break;
		case R.id.iv_type_any:
			if (translate_WifiToAny == null) {
				int[] wifilocations = new int[2];
				iv_type_wifi.getLocationOnScreen(wifilocations);
				int[] anylocations = new int[2];
				iv_type_any.getLocationOnScreen(anylocations);
				translate_WifiToAny = new TranslateAnimation(0, anylocations[0]
						- wifilocations[0], 0, 0);
				translate_WifiToAny.setDuration(duration);
				translate_WifiToAny.setAnimationListener(this);
			}
			iv_type_wifi.startAnimation(translate_WifiToAny);
			break;
		case R.id.iv_type_shoot:// 拍摄模式
			if (translate_WatchToShoot == null) {
				int[] watchlocations = new int[2];
				iv_type_watch.getLocationOnScreen(watchlocations);
				int[] shootlocations = new int[2];
				iv_type_shoot.getLocationOnScreen(shootlocations);
				translate_WatchToShoot = new TranslateAnimation(0,
						shootlocations[0] - watchlocations[0], 0, 0);
				translate_WatchToShoot.setDuration(duration);
				translate_WatchToShoot.setAnimationListener(this);
			}
			iv_type_watch.startAnimation(translate_WatchToShoot);
			break;
		case R.id.iv_type_watch:// 观看模式
			if (translate_ShootToWatch == null) {
				int[] watchlocations = new int[2];
				iv_type_watch.getLocationOnScreen(watchlocations);
				int[] shootlocations = new int[2];
				iv_type_shoot.getLocationOnScreen(shootlocations);
				translate_ShootToWatch = new TranslateAnimation(0,
						watchlocations[0] - shootlocations[0], 0, 0);
				translate_ShootToWatch.setDuration(duration);
				translate_ShootToWatch.setAnimationListener(this);
			}
			iv_type_shoot.startAnimation(translate_ShootToWatch);
			break;
		case R.id.rl_phonebind:
			if (phonebindstate != null
					&& !(activeAccount.snstype.equals("0") && activeAccount.logintype
							.equals("2"))) {
				String msg = null;
				if (lsm.getGesturepassword() != null
						&& null != lsm.getBinding_type(
								LoginSettingManager.BINDING_TYPE_PHONE_SEC,
								LoginSettingManager.BINDING_INFO_POINTLESS)) {
					msg = "解绑后将同时解除第二个手机号绑定并注销保险箱，是否继续？";
				} else if (lsm.getGesturepassword() != null) {
					msg = "解绑后将同时注销保险箱，是否继续？";
				} else if (null != lsm.getBinding_type(
						LoginSettingManager.BINDING_TYPE_PHONE_SEC,
						LoginSettingManager.BINDING_INFO_POINTLESS)) {
					msg = "解绑后将同时解除第二个手机号绑定，是否继续？";
				} else {
					msg = "解除手机帐号绑定，是否继续？";
				}
				Prompt.Dialog(this, true, "提示", msg,
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								// TODO Auto-generated method stub
								ZDialog.show(R.layout.progressdialog, false,
										true, SettingActivity.this);
								Requester2
										.unbind(handler,
												LoginSettingManager.BINDING_REQUEST_TYPE_PHONE,
												LoginSettingManager.PHONE_TYPE_MAIN,
												phonebindstate.binding_info,
												LoginSettingManager.BINDING_INFO_POINTLESS,
												LoginSettingManager.BINDING_INFO_POINTLESS);
							}

						});
			} else if (phonebindstate != null
					&& (activeAccount.snstype.equals("0") && activeAccount.logintype
							.equals("2"))) {
				Prompt.Dialog(this, false, "提示", "抱歉，当前登录的账号不可解绑定", null);
			} else {
				Intent phonebind = new Intent(this,
						SettingBindPhoneActivity.class);
				startActivity(phonebind);
			}
			break;
		/*
		 * case R.id.rl_safe: if(safestate != null){ showDailog(R.string.safe);
		 * }else if(phonebindstate == null){ Prompt.Alert(this, "请先进行手机账号绑定");
		 * }else{ Intent safe = new Intent(this, SettingSafeActivity.class);
		 * safe.putExtra("phonenum", phonebindstate.binding_info);
		 * startActivity(safe); } break;
		 */
		case R.id.rl_emailbind:
			if (emailbindstate != null
					&& !(activeAccount.snstype.equals("0") && activeAccount.logintype
							.equals("1"))) {
				Prompt.Dialog(this, true, "提示", "解除邮箱绑定",
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								// TODO Auto-generated method stub
								ZDialog.show(R.layout.progressdialog, false,
										true, SettingActivity.this);
								Requester2
										.unbind(handler,
												LoginSettingManager.BINDING_REQUEST_TYPE_EMAIL,
												LoginSettingManager.BINDING_INFO_POINTLESS,
												emailbindstate.binding_info,
												LoginSettingManager.BINDING_INFO_POINTLESS,
												LoginSettingManager.BINDING_INFO_POINTLESS);
							}

						});
			} else if (emailbindstate != null
					&& (activeAccount.snstype.equals("0") && activeAccount.logintype
							.equals("1"))) {
				Prompt.Dialog(this, false, "提示", "抱歉，当前登录的账号不可解绑定", null);
			} else {
				Intent intent = new Intent(this, SettingBindEmailActivity.class);
				startActivity(intent);
			}
			break;
		case R.id.rl_loginpwd:
			if (ActiveAccount.getInstance(this).snstype.equals("0")) {
				Intent intent = new Intent(this, SettingLoginPwdActivity.class);
				startActivity(intent);
			} else {
				Prompt.Dialog(this, false, "提示", "第三方登录，无法进行此操作", null);
			}
			break;
		case R.id.rl_sinaweibo:
			if (ActiveAccount.getInstance(context).isSNSBind("sina")
					&& !activeAccount.snstype.equals("1")) {
				Prompt.Dialog(this, true, "提示", "解绑后就无法对第三方分享的日记进行管理，是否继续",
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								// TODO Auto-generated method stub
								ZDialog.show(R.layout.progressdialog, false,
										true, SettingActivity.this);
								Requester2
										.unbind(handler,
												LoginSettingManager.BINDING_REQUEST_TYPE_SNS,
												LoginSettingManager.BINDING_INFO_POINTLESS,
												LoginSettingManager.BINDING_INFO_POINTLESS,
												LoginSettingManager.BINDING_SNS_TYPE_SINA,
												ActiveAccount
														.getInstance(context)
														.getSNSID(
																SHARE_TO.SINA
																		.ordinal()));
							}
						});
			} else if (ActiveAccount.getInstance(context).isSNSBind("sina")
					&& activeAccount.snstype.equals("1")) {
				Prompt.Dialog(this, false, "提示", "抱歉，当前登录的账号不可解绑定", null);
			} else {
				begin = TimeHelper.getInstance().now();
				CmmobiSnsLib.getInstance(SettingActivity.this).sinaAuthorize(
						sinalistener);
			}
			break;
		case R.id.rl_tencentweibo:
			if (ActiveAccount.getInstance(context).isSNSBind("tencent")
					&& !activeAccount.snstype.equals("6")) {
				Prompt.Dialog(this, true, "提示", "解绑后就无法对第三方分享的日记进行管理，是否继续",
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								// TODO Auto-generated method stub
								ZDialog.show(R.layout.progressdialog, false,
										true, SettingActivity.this);
								Requester2
										.unbind(handler,
												LoginSettingManager.BINDING_REQUEST_TYPE_SNS,
												LoginSettingManager.BINDING_INFO_POINTLESS,
												LoginSettingManager.BINDING_INFO_POINTLESS,
												LoginSettingManager.BINDING_SNS_TYPE_TENCENT,
												ActiveAccount
														.getInstance(context)
														.getSNSID(
																SHARE_TO.TENC
																		.ordinal()));
							}
						});
			} else if (ActiveAccount.getInstance(context).isSNSBind("tencent")
					&& activeAccount.snstype.equals("6")) {
				Prompt.Dialog(this, false, "提示", "抱歉，当前登录的账号不可解绑定", null);
			} else {
				begin = TimeHelper.getInstance().now();
				CmmobiSnsLib.getInstance(SettingActivity.this)
						.tencentWeiboAuthorize(tencentlistener);
			}
			break;
		case R.id.rl_renren:
			if (ActiveAccount.getInstance(context).isSNSBind("renren")
					&& !activeAccount.snstype.equals("2")) {
				Prompt.Dialog(this, true, "提示", "解绑后就无法对第三方分享的日记进行管理，是否继续",
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								// TODO Auto-generated method stub
								ZDialog.show(R.layout.progressdialog, false,
										true, SettingActivity.this);
								Requester2
										.unbind(handler,
												LoginSettingManager.BINDING_REQUEST_TYPE_SNS,
												LoginSettingManager.BINDING_INFO_POINTLESS,
												LoginSettingManager.BINDING_INFO_POINTLESS,
												LoginSettingManager.BINDING_SNS_TYPE_RENREN,
												ActiveAccount.getInstance(
														context).getSNSID(
														SHARE_TO.RENREN
																.ordinal()));
							}
						});
			} else if (ActiveAccount.getInstance(context).isSNSBind("renren")
					&& activeAccount.snstype.equals("2")) {
				Prompt.Dialog(this, false, "提示", "抱歉，当前登录的账号不可解绑定", null);
			} else {
				begin = TimeHelper.getInstance().now();
				CmmobiSnsLib.getInstance(SettingActivity.this).renrenAuthorize(
						renrenlistener);
			}
			break;
		case R.id.rl_personalinfo:
			Intent personalinfo = new Intent(SettingActivity.this,
					SettingPersonalInfoActivity.class);
			startActivity(personalinfo);
			break;
		case R.id.rl_privacyset:
			Intent privacyset = new Intent(this, SettingPrivacyActivity.class);
			/*privacyset.putExtra("privmsg", privmsg);
			privacyset.putExtra("friends", friends);*/
			/*
			 * privacyset.putExtra("content", content);
			 * privacyset.putExtra("location", location);
			 * privacyset.putExtra("voice", voice);
			 */
			/*startActivityForResult(privacyset, REQUEST_PRIVACY);*/
			startActivity(privacyset);
			break;
		case R.id.rl_storagemanage:
			Intent storagemanage = new Intent(this,
					SettingStorageManagerActivity.class);
			startActivity(storagemanage);
			break;
		case R.id.rl_about:
			Intent intent = new Intent(this, AboutActivity.class);
			startActivity(intent);
			break;
		case R.id.btn_exit:
			Prompt.Dialog(this, true, "提示", "退出当前账号",
					new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							// TODO Auto-generated method stub
							 //Log.e(TAG, "==4" + privmsg + friends +sysc_type + launch_type);
							 lsm.setSafeIsOn(false);
							 if (/*privmsg.equals(lsm.getPrivmsg_type())
									&& friends.equals(lsm.getFriends_type())
									&& 
										 * content.equals(lsm.getDiary_type())
										 * &&
										 * location.equals(lsm.getPosition_type
										 * ()) &&
										 * voice.equals(lsm.getAudio_type()) &&
										 */launch_type.equals(lsm
											.getLaunch_type())
									&& sysc_type.equals(lsm.getSysc_type())) {
							    accountInfo.persist();
								ActiveAccount currentaccount = ActiveAccount.getInstance(SettingActivity.this);
								currentaccount.logout();

								Intent in = new Intent(SettingActivity.this, LoginMainActivity.class);
								startActivity(in);
								finish();
								Intent intent = new Intent(HomeActivity.FLAG_CLOSE_ACTIVITY);
								/*LocalBroadcastManager.getInstance(SettingActivity.this).*/sendBroadcast(intent);

							} else {
								ZDialog.show(R.layout.progressdialog, false,
										true, context);
								isClose = true;
								Requester2.setPrivacy(handler, null,
										null, null, null, null, null,
										launch_type, sysc_type);
							}
							JPushInterface.stopPush(MainApplication.getAppInstance());
						}
					});
			break;
		case R.id.iv_recommend:

			pop_recommend = new PopupWindow(view_recommend,
					LayoutParams.FILL_PARENT, 120, true);
			pop_recommend.setAnimationStyle(R.style.pop_commend_style);
			pop_recommend.setBackgroundDrawable(new BitmapDrawable());
			pop_recommend.setFocusable(true);
			pop_recommend.setTouchable(true);
			pop_recommend.setOutsideTouchable(true);
			
			if (pop_recommend != null) {
				pop_recommend
						.showAtLocation(iv_recommend, Gravity.BOTTOM, 0, 0);
			}
			break;
		case R.id.iv_dismiss:
			pop_recommend.dismiss();
			break;
		case R.id.iv_more:
			pop_recommend.dismiss();
			Intent morerecommend = new Intent(this, AppCommendActivity.class);
			startActivity(morerecommend);
			break;
		case R.id.iv_recommend01:
			iv_recommend01.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					String url = recItem.get(0).loadpath;
					Intent intent = new Intent(Intent.ACTION_VIEW, Uri
							.parse(url));
					startActivity(intent);
				}
			});
			break;
		case R.id.iv_recommend02:
			iv_recommend02.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					String url = recItem.get(1).loadpath;
					Intent intent = new Intent(Intent.ACTION_VIEW, Uri
							.parse(url));
					startActivity(intent);
				}
			});
			break;
		case R.id.iv_recommend03:
			iv_recommend03.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					String url = recItem.get(2).loadpath;
					Intent intent = new Intent(Intent.ACTION_VIEW, Uri
							.parse(url));
					startActivity(intent);
				}
			});
			break;
		case R.id.iv_recommend04:
			iv_recommend04.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					String url = recItem.get(3).loadpath;
					Intent intent = new Intent(Intent.ACTION_VIEW, Uri
							.parse(url));
					startActivity(intent);
				}
			});
			break;
		}
	}

	/*
	 * private void launchBindWeiboDialog(final String snstype) { // 添加退出提示对话框
	 * new AlertDialog.Builder(this) .setTitle("绑定微博") .setCancelable(true) //
	 * 设置不能通过“后退”按钮关闭对话框 .setMessage("确定绑定吗?") .setPositiveButton("是的", new
	 * DialogInterface.OnClickListener() { public void onClick(DialogInterface
	 * dialoginterface, int i) { if ("sina".equals(snstype)) {
	 * CmmobiSnsLib.getInstance(SettingActivity.this)
	 * .sinaAuthorize(sinalistener); } else if ("renren".equals(snstype)) {
	 * CmmobiSnsLib.getInstance(SettingActivity.this)
	 * .renrenAuthorize(renrenlistener); } else if ("tencent".equals(snstype)) {
	 * CmmobiSnsLib.getInstance(SettingActivity.this)
	 * .tencentWeiboAuthorize(tencentlistener); }
	 * 
	 * } }) .setNegativeButton("不是", new DialogInterface.OnClickListener() {
	 * public void onClick(DialogInterface dialog, int id) { dialog.cancel(); }
	 * }).show();// 显示对话框 }
	 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == REQUEST_PRIVACY && resultCode == RESULT_OK) {
			/*privmsg = data.getExtras().getString("privmsg");
			friends = data.getExtras().getString("friends");*/
			/*
			 * content = data.getExtras().getString("content"); location =
			 * data.getExtras().getString("location"); voice =
			 * data.getExtras().getString("voice");
			 */
		} else {
			if (CmmobiSnsLib.getInstance(this).mSsoHandler != null) {
				CmmobiSnsLib.getInstance(this).mSsoHandler.authorizeCallBack(
						requestCode, resultCode, data);
			}
		}
	}

	private WeiboAuthListener sinalistener = new WeiboAuthListener() {

		@Override
		public void onComplete(int weiboIndex) {
			// ZToast.showShort("sina授权成功！");
			Message message = getHandler().obtainMessage(
					HANDLER_SINA_AUTHOR_SUCCESS);
			getHandler().sendMessage(message);
			if (begin != 0) {
				end_sup_sha = TimeHelper.getInstance().now() - begin;
				CmmobiClickAgentWrapper.onEventDuration(context, "sup_sha",
						"1", end_sup_sha);
			}
		}

		@Override
		public void onCancel(int arg0) {
			ZToast.showShort("sina授权取消！");
		}

		@Override
		public void onError(WeiboDialogError arg0, int arg1) {
			ZToast.showShort("sina授权错误！");
		}

		@Override
		public void onWeiboException(WeiboException arg0, int arg1) {
			Log.e(TAG, "sina授权异常！");
		}

	};
	private WeiboAuthListener tencentlistener = new WeiboAuthListener() {

		@Override
		public void onComplete(int weiboIndex) {
			// ZToast.showShort("tencent授权成功！");
			Message message = getHandler().obtainMessage(
					HANDLER_TENCENT_AUTHOR_SUCCESS);
			getHandler().sendMessage(message);
			if (begin != 0) {
				end_sup_sha = TimeHelper.getInstance().now() - begin;
				CmmobiClickAgentWrapper.onEventDuration(context, "sup_sha",
						"6", end_sup_sha);
			}
		}

		@Override
		public void onCancel(int arg0) {
			// TODO Auto-generated method stub
			ZToast.showShort("tencent授权取消！");
		}

		@Override
		public void onError(WeiboDialogError arg0, int arg1) {
			// TODO Auto-generated method stub
			ZToast.showShort("tencent授权错误！");
		}

		@Override
		public void onWeiboException(WeiboException arg0, int arg1) {
			// TODO Auto-generated method stub
			ZToast.showShort("tencent授权异常！");
		}

	};

	private WeiboAuthListener renrenlistener = new WeiboAuthListener() {

		@Override
		public void onComplete(int weiboIndex) {
			// ZToast.showShort("renren授权成功！");
			Message message = getHandler().obtainMessage(
					HANDLER_RENREN_AUTHOR_SUCCESS);
			getHandler().sendMessage(message);
			if (begin != 0) {
				end_sup_sha = TimeHelper.getInstance().now() - begin;
				CmmobiClickAgentWrapper.onEventDuration(context, "sup_sha",
						"2", end_sup_sha);
			}
		}

		@Override
		public void onCancel(int arg0) {
			ZToast.showShort("renren授权取消！");
		}

		@Override
		public void onError(WeiboDialogError arg0, int arg1) {
			ZToast.showShort("renren授权错误！");
		}

		@Override
		public void onWeiboException(WeiboException arg0, int arg1) {
			ZToast.showShort("renren授权异常！");
		}

	};

	/*
	 * 弹出对话框，提示用户操作，包括：退出、各种绑定的解除 rid：当前点击的条目对应的名称的string资源id
	 */
	/*
	 * public void showDailog(final int rid) { AlertDialog.Builder builder = new
	 * Builder(this); if(rid == R.string.exit){
	 * builder.setTitle(this.getString(rid)); builder.setMessage("确定"+
	 * this.getString(rid)+"？"); }else{ builder.setTitle("解除绑定"); if(rid ==
	 * R.string.sina_bind || rid == R.string.renren ||rid ==
	 * R.string.tencent_bind ){ builder.setMessage("解绑后就无法对第三方分享的日记进行管理，是否继续？");
	 * }else if(rid == R.string.phone_bind && lsm.getGesturepassword() != null
	 * && null !=
	 * lsm.getBinding_type(LoginSettingManager.BINDING_TYPE_PHONE_SEC,
	 * LoginSettingManager.BINDING_INFO_POINTLESS)){
	 * builder.setMessage("解绑后将同时解除手机号绑定（保险箱）和手势密码，是否继续？"); }else if(rid ==
	 * R.string.phone_bind && lsm.getGesturepassword() != null){
	 * builder.setMessage("解绑后将同时解除手势密码，是否继续？"); }else if(rid ==
	 * R.string.phone_bind && null !=
	 * lsm.getBinding_type(LoginSettingManager.BINDING_TYPE_PHONE_SEC,
	 * LoginSettingManager.BINDING_INFO_POINTLESS)){
	 * builder.setMessage("解绑后将同时解除手机号绑定（保险箱），是否继续？"); }else{
	 * builder.setMessage("确定解除"+ this.getString(rid)+"？"); } }
	 * builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
	 * 
	 * @Override public void onClick(DialogInterface dialog, int which) { if(rid
	 * == R.string.exit){ if(privmsg.equals(lsm.getPrivmsg_type()) &&
	 * friends.equals(lsm.getFriends_type()) &&
	 * content.equals(lsm.getDiary_type()) &&
	 * location.equals(lsm.getPosition_type()) &&
	 * voice.equals(lsm.getAudio_type()) &&
	 * launch_type.equals(lsm.getLaunch_type()) &&
	 * sysc_type.equals(lsm.getSysc_type())){ ActiveAccount currentaccount =
	 * ActiveAccount.getInstance(SettingActivity.this); currentaccount.logout();
	 * Intent in = new Intent(SettingActivity.this, LoginMainActivity.class);
	 * startActivity(in); finish(); Intent intent = new
	 * Intent(HomeActivity.FLAG_CLOSE_ACTIVITY); sendBroadcast(intent); }else{
	 * ZDialog.show(R.layout.progressdialog, false, true, context); isClose =
	 * true; Requester2.setPrivacy(handler, privmsg, friends, content, location,
	 * voice, "2", launch_type, sysc_type); }
	 * JPushInterface.stopPush(MainApplication.getAppInstance()); }else{ String
	 * unbind_type; String unbind_phone_type; String unbind_sns_type; switch
	 * (rid) { case R.string.phone_bind: //解除绑定 unbind_type =
	 * LoginSettingManager.BINDING_REQUEST_TYPE_PHONE; unbind_sns_type =
	 * LoginSettingManager.BINDING_INFO_POINTLESS; unbind_phone_type =
	 * LoginSettingManager.PHONE_TYPE_MAIN; Requester2.unbind(handler,
	 * unbind_type, unbind_phone_type, phonebindstate.binding_info,
	 * unbind_sns_type, LoginSettingManager.BINDING_INFO_POINTLESS); break; case
	 * R.string.safe: //解除绑定 unbind_type =
	 * LoginSettingManager.BINDING_REQUEST_TYPE_PHONE; unbind_sns_type =
	 * LoginSettingManager.BINDING_INFO_POINTLESS; unbind_phone_type =
	 * LoginSettingManager.PHONE_TYPE_SEC; Requester2.unbind(handler,
	 * unbind_type, unbind_phone_type, safestate.binding_info, unbind_sns_type,
	 * LoginSettingManager.BINDING_INFO_POINTLESS); break; case
	 * R.string.email_bind: //解除绑定 unbind_type =
	 * LoginSettingManager.BINDING_REQUEST_TYPE_EMAIL; unbind_sns_type =
	 * LoginSettingManager.BINDING_INFO_POINTLESS; unbind_phone_type =
	 * LoginSettingManager.BINDING_INFO_POINTLESS; Requester2.unbind(handler,
	 * unbind_type, unbind_phone_type, emailbindstate.binding_info,
	 * unbind_sns_type, LoginSettingManager.BINDING_INFO_POINTLESS); break; case
	 * R.string.sina_bind: unbind_type =
	 * LoginSettingManager.BINDING_REQUEST_TYPE_SNS; unbind_sns_type =
	 * LoginSettingManager.BINDING_SNS_TYPE_SINA; unbind_phone_type =
	 * LoginSettingManager.BINDING_INFO_POINTLESS;
	 * //System.out.println(sinaweibostate + "====");
	 * //System.out.println(sinaweibostate.binding_info + "====" +
	 * sinaweibostate.snsuid); Requester2.unbind(handler, unbind_type,
	 * unbind_phone_type, LoginSettingManager.BINDING_INFO_POINTLESS,
	 * unbind_sns_type,
	 * ActiveAccount.getInstance(context).getSNSID(SHARE_TO.SINA.ordinal()));
	 * accountInfo.removeAccessToken("1");
	 * CmmobiSnsLib.getInstance(SettingActivity
	 * .this).removeOauth(SHARE_TO.SINA.ordinal()); break; case
	 * R.string.tencent_bind: unbind_type =
	 * LoginSettingManager.BINDING_REQUEST_TYPE_SNS; unbind_sns_type =
	 * LoginSettingManager.BINDING_SNS_TYPE_TENCENT; unbind_phone_type =
	 * LoginSettingManager.BINDING_INFO_POINTLESS; Requester2.unbind(handler,
	 * unbind_type, unbind_phone_type,
	 * LoginSettingManager.BINDING_INFO_POINTLESS, unbind_sns_type,
	 * ActiveAccount.getInstance(context).getSNSID(SHARE_TO.TENC.ordinal()));
	 * break; case R.string.renren_bind: unbind_type =
	 * LoginSettingManager.BINDING_REQUEST_TYPE_SNS; unbind_sns_type =
	 * LoginSettingManager.BINDING_SNS_TYPE_RENREN; unbind_phone_type =
	 * LoginSettingManager.BINDING_INFO_POINTLESS; Requester2.unbind(handler,
	 * unbind_type, unbind_phone_type,
	 * LoginSettingManager.BINDING_INFO_POINTLESS, unbind_sns_type,
	 * ActiveAccount.getInstance(context).getSNSID(SHARE_TO.RENREN.ordinal()));
	 * break; default: break; }
	 * 
	 * }
	 * 
	 * } }); builder.setNegativeButton("取消", new
	 * DialogInterface.OnClickListener() {
	 * 
	 * @Override public void onClick(DialogInterface dialog, int which) {
	 * 
	 * } });
	 * 
	 * builder.create().show(); }
	 */

	@Override
	public boolean handleMessage(Message msg) {
		// TODO Auto-generated method stub
		ActiveAccount acct = ActiveAccount.getInstance(this);
		CmmobiSnsLib csb = CmmobiSnsLib.getInstance(this);
		switch (msg.what) {
		case Requester2.RESPONSE_TYPE_DIARY_PRIVACY:
			accountInfo.persist();
			activeAccount.persist();
			ZDialog.dismiss();
			try {
				GsonResponse2.diaryPrivacyResponse dp = (GsonResponse2.diaryPrivacyResponse) msg.obj;
				if (dp != null) {
					if (dp.status.equals("0")) {
						if (lsm.getLaunch_type() == null
								|| !lsm.getLaunch_type().equals(launch_type)) {
							if (launch_type.equals(LAUNCH_TYPE_SHOOT)) {
								CmmobiClickAgentWrapper.onEvent(this,
										"sup_start", "2");
							} else {
								CmmobiClickAgentWrapper.onEvent(this,
										"sup_start", "1");
							}
						}

						if (lsm.getSysc_type() == null
								|| !lsm.getSysc_type().equals(sysc_type)) {
							if (sysc_type.equals(SYSC_TYPE_WIFI)) {
								CmmobiClickAgentWrapper.onEvent(this,
										"pa_and_sh", "1");
							} else {
								CmmobiClickAgentWrapper.onEvent(this,
										"pa_and_sh", "2");
							}
						}

						lsm.setSysc_type(sysc_type);
						lsm.setLaunch_type(launch_type);
						/*lsm.setPrivmsg_type(privmsg);
						lsm.setFriends_type(friends);*/
						/*
						 * lsm.setDiary_type(content);
						 * lsm.setPosition_type(location);
						 * lsm.setAudio_type(voice);
						 */
						Intent intent = new Intent(
								SettingActivity.BROADCAST_PRIVACY_CHANGED);
						lbc.sendBroadcast(intent);
						Prompt.Dialog(this, false, "提示", "设置更改成功",
								new DialogInterface.OnClickListener() {

									@Override
									public void onClick(DialogInterface dialog,
											int which) {
										// TODO Auto-generated method stub
										if (isClose) {
											ActiveAccount currentaccount = ActiveAccount
													.getInstance(SettingActivity.this);
											currentaccount.logout();
											Intent in = new Intent(
													SettingActivity.this,
													LoginMainActivity.class);
											startActivity(in);
											SettingActivity.this.finish();
											Intent intent = new Intent(
													HomeActivity.FLAG_CLOSE_ACTIVITY);
											sendBroadcast(intent);
										} else {
											SettingActivity.this.finish();
										}
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
										if (isClose) {
											ActiveAccount currentaccount = ActiveAccount
													.getInstance(SettingActivity.this);
											currentaccount.logout();
											Intent in = new Intent(
													SettingActivity.this,
													LoginMainActivity.class);
											startActivity(in);
											SettingActivity.this.finish();
											Intent intent = new Intent(
													HomeActivity.FLAG_CLOSE_ACTIVITY);
											sendBroadcast(intent);
										} else {
											SettingActivity.this.finish();
										}
									}
								});
					} else {
						Prompt.Dialog(this, false, "提示", "操作失败，请稍后再试",
								new DialogInterface.OnClickListener() {

									@Override
									public void onClick(DialogInterface dialog,
											int which) {
										// TODO Auto-generated method stub
										if (isClose) {
											ActiveAccount currentaccount = ActiveAccount
													.getInstance(SettingActivity.this);
											currentaccount.logout();
											Intent in = new Intent(
													SettingActivity.this,
													LoginMainActivity.class);
											startActivity(in);
											SettingActivity.this.finish();
											Intent intent = new Intent(
													HomeActivity.FLAG_CLOSE_ACTIVITY);
											sendBroadcast(intent);
										} else {
											SettingActivity.this.finish();
										}
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
									if (isClose) {
										ActiveAccount currentaccount = ActiveAccount
												.getInstance(SettingActivity.this);
										currentaccount.logout();
										Intent in = new Intent(
												SettingActivity.this,
												LoginMainActivity.class);
										startActivity(in);
										SettingActivity.this.finish();
										Intent intent = new Intent(
												HomeActivity.FLAG_CLOSE_ACTIVITY);
										sendBroadcast(intent);
									} else {
										SettingActivity.this.finish();
									}
								}
							});
				}
			} catch (Exception e) {
				// TODO: handle exception
			}
			break;
		case HANDLER_SINA_AUTHOR_SUCCESS:
			ZDialog.show(R.layout.progressdialog, false, true, this);
			String sina_snsid = ActiveAccount.getInstance(this).getSNSID(
					SHARE_TO.SINA.ordinal());
			if (sina_snsid != null) {
				WeiboRequester.getSinaAccountInfo(getHandler(), sina_snsid);
			} else {
				Prompt.Alert(this, "授权微博异常");
			}
			break;
		case WeiboRequester.SINA_INTERFACE_GET_ACCOUNTINFO:
			SWUserInfo sn_object = (SWUserInfo) msg.obj;
			if (sn_object == null || sn_object.screen_name == null
					|| !acct.updateSinaAuthor(sn_object)) {
				ZDialog.dismiss();
				Prompt.Alert(this, "获取微博个人信息异常");
				accountInfo.removeAccessToken("3", "3", "1");
				CmmobiSnsLib.getInstance(SettingActivity.this).removeOauth(
						SHARE_TO.SINA.ordinal());
				ZDialog.dismiss();
			} else {
				Log.e(TAG, "sina getaccountinfo ok, binding..");
				OAuthV2 sina_oa = csb.acquireSinaOauth();
				if (sina_oa != null) {
					Requester2.bindAccount(getHandler(), "3", null, null,
							acct.snsid, "1", null, acct.sex, acct.address,
							acct.birthdate, sina_oa.getAccessToken(),
							sina_oa.getExpiresIn(), sina_oa.getExpiresTime(),
							sina_oa.getRefreshToken(), sina_oa.getNick(),
							sina_oa.getOpenkey(), sina_oa.getNick());
					myBind = new MyBind();
					myBind.binding_type = LoginSettingManager.BINDING_TYPE_SNS;
					myBind.snsuid = acct.snsid;
					myBind.snstype = LoginSettingManager.BINDING_SNS_TYPE_SINA;
					myBind.sns_nickname = sina_oa.getNick();
					myBind.sns_token = sina_oa.getAccessToken();
					myBind.sns_expiration_time = sina_oa.getExpiresTime();
					myBind.sns_effective_time = sina_oa.getExpiresIn();
					myBind.sns_openkey = sina_oa.getOpenkey();
					myBind.sns_refresh_token = sina_oa.getRefreshToken();
					System.out.println("== nickname " + myBind.sns_nickname);
				} else {
					accountInfo.removeAccessToken("3", "3", "1");
					CmmobiSnsLib.getInstance(SettingActivity.this).removeOauth(
							SHARE_TO.SINA.ordinal());
					ZDialog.dismiss();
				}

			}
			break;
		case HANDLER_TENCENT_AUTHOR_SUCCESS:
			ZDialog.show(R.layout.progressdialog, false, true,
					SettingActivity.this);
			String tencent_snsid = ActiveAccount.getInstance(this).getSNSID(
					SHARE_TO.TENC.ordinal());
			if (tencent_snsid != null) {
				WeiboRequester.getTencentAccountInfo(getHandler(),
						tencent_snsid);
			} else {
				Prompt.Alert(this, "授权微博异常");
			}
			break;
		case WeiboRequester.TENCENT_INTERFACE_GET_ACCOUNTINFO:
			TWUserInfo sn_tencent = (TWUserInfo) msg.obj;
			if (sn_tencent == null || sn_tencent.data == null
					|| !acct.updateTencentAuthor(sn_tencent)) {
				Prompt.Alert(this, "获取微博个人信息异常");
				accountInfo.removeAccessToken("3", "3", "6");
				CmmobiSnsLib.getInstance(SettingActivity.this).removeOauth(
						SHARE_TO.TENC.ordinal());
				ZDialog.dismiss();

			} else {
				Log.e(TAG, "tencent getaccountinfo ok, binding..");
				OAuthV2 tencent_oa = csb.acquireTencentWeiboOauth();
				if (tencent_oa != null) {
					Requester2.bindAccount(getHandler(), "3", null, null,
							acct.snsid,
							LoginSettingManager.BINDING_SNS_TYPE_TENCENT, null,
							acct.sex, acct.address, acct.birthdate,
							tencent_oa.getAccessToken(),
							tencent_oa.getExpiresIn(),
							tencent_oa.getExpiresTime(),
							tencent_oa.getRefreshToken(), tencent_oa.getNick(),
							tencent_oa.getOpenkey(), tencent_oa.getNick());
					myBind = new MyBind();
					myBind.binding_type = LoginSettingManager.BINDING_TYPE_SNS;
					myBind.snsuid = acct.snsid;
					myBind.snstype = LoginSettingManager.BINDING_SNS_TYPE_TENCENT;
					myBind.sns_nickname = tencent_oa.getNick();
					myBind.sns_token = tencent_oa.getAccessToken();
					myBind.sns_expiration_time = tencent_oa.getExpiresTime();
					myBind.sns_effective_time = tencent_oa.getExpiresIn();
					myBind.sns_openkey = tencent_oa.getOpenkey();
					myBind.sns_refresh_token = tencent_oa.getRefreshToken();
					System.out.println("== nickname " + myBind.sns_nickname);
				} else {
					accountInfo.removeAccessToken("3", "3", "6");
					CmmobiSnsLib.getInstance(SettingActivity.this).removeOauth(
							SHARE_TO.TENC.ordinal());
					ZDialog.dismiss();
				}
			}
			break;
		case HANDLER_RENREN_AUTHOR_SUCCESS:
			ZDialog.show(R.layout.progressdialog, false, true,
					SettingActivity.this);
			String renren_snsid = ActiveAccount.getInstance(this).getSNSID(
					SHARE_TO.RENREN.ordinal());
			if (renren_snsid != null) {
				WeiboRequester.getRenrenAccountInfo(getHandler(), renren_snsid);
			} else {
				Prompt.Alert(this, "授权微博异常");
			}
			break;
		case WeiboRequester.RENREN_INTERFACE_GET_ACCOUNTINFO:
			RWUserInfo sn_renren = (RWUserInfo) msg.obj;
			if (sn_renren == null || sn_renren.response == null
					|| !acct.updateRenrenAuthor(sn_renren)) {
				Prompt.Alert(this, "获取微博个人信息异常");
				accountInfo.removeAccessToken("3", "3", "2");
				CmmobiSnsLib.getInstance(SettingActivity.this).removeOauth(
						SHARE_TO.RENREN.ordinal());
				ZDialog.dismiss();
			} else {
				Log.e(TAG, "renren getaccountinfo ok, binding..");
				OAuthV2 renren_oa = csb.acquireRenrenOauth();
				if (renren_oa != null) {
					Requester2.bindAccount(getHandler(), "3", null, null,
							acct.snsid,
							LoginSettingManager.BINDING_SNS_TYPE_RENREN, null,
							acct.sex, acct.address, acct.birthdate,
							renren_oa.getAccessToken(),
							renren_oa.getExpiresIn(),
							renren_oa.getExpiresTime(),
							renren_oa.getRefreshToken(), renren_oa.getNick(),
							renren_oa.getOpenkey(), renren_oa.getNick());
					myBind = new MyBind();
					myBind.binding_type = LoginSettingManager.BINDING_TYPE_SNS;
					myBind.snsuid = acct.snsid;
					myBind.snstype = LoginSettingManager.BINDING_SNS_TYPE_RENREN;
					myBind.sns_nickname = renren_oa.getNick();
					myBind.sns_token = renren_oa.getAccessToken();
					myBind.sns_expiration_time = renren_oa.getExpiresTime();
					myBind.sns_effective_time = renren_oa.getExpiresIn();
					myBind.sns_openkey = renren_oa.getOpenkey();
					myBind.sns_refresh_token = renren_oa.getRefreshToken();
					System.out.println("== nickname " + myBind.sns_nickname);
				} else {
					accountInfo.removeAccessToken("3", "3", "2");
					CmmobiSnsLib.getInstance(SettingActivity.this).removeOauth(
							SHARE_TO.RENREN.ordinal());
					ZDialog.dismiss();
				}
			}
			break;
		case Requester2.RESPONSE_TYPE_BINDING:
			ZDialog.dismiss();
			try {
				GsonResponse2.bindingResponse bind_response = (GsonResponse2.bindingResponse) msg.obj;
				if (bind_response != null && bind_response.status != null
						&& bind_response.status.equals("0")) {
					if (bind_response.binding_type
							.equals(LoginSettingManager.BINDING_REQUEST_TYPE_SNS)
							&& myBind != null) {
						if (bind_response.snstype
								.equals(LoginSettingManager.BINDING_SNS_TYPE_SINA)) {
							if (begin != 0) {
								end_sup_sha_bin = TimeHelper.getInstance().now()
										- begin;
								CmmobiClickAgentWrapper.onEventDuration(
										context, "sup_sha_bin", "1",
										end_sup_sha_bin);
							}
							lsm.addBindingInfo(myBind);
							tv_sinaweibostate.setText(myBind.sns_nickname);
						} else if (bind_response.snstype
								.equals(LoginSettingManager.BINDING_SNS_TYPE_RENREN)) {
							if (begin != 0) {
								end_sup_sha_bin = TimeHelper.getInstance().now()
										- begin;
								CmmobiClickAgentWrapper.onEventDuration(
										context, "sup_sha_bin", "2",
										end_sup_sha_bin);
							}
							lsm.addBindingInfo(myBind);
							tv_renrenstate.setText(myBind.sns_nickname);
						} else if (bind_response.snstype
								.equals(LoginSettingManager.BINDING_SNS_TYPE_TENCENT)) {
							if (begin != 0) {
								end_sup_sha_bin = TimeHelper.getInstance().now()
										- begin;
								CmmobiClickAgentWrapper.onEventDuration(
										context, "sup_sha_bin", "6",
										end_sup_sha_bin);
							}
							lsm.addBindingInfo(myBind);
							tv_tencentweibostate.setText(myBind.sns_nickname);
						}
						Prompt.Dialog(this, false, "提示", "绑定成功", null);
					}
				} else {
					if (bind_response != null
							&& bind_response.status.equals("200600")) {
						Prompt.Dialog(this, false, "提示",
								Constant.CRM_STATUS[Integer
										.parseInt(bind_response.crm_status)],
								null);
					} else {
						Prompt.Dialog(this, false, "提示", "操作失败，请稍后再试", null);
					}
					if (bind_response.binding_type
							.equals(LoginSettingManager.BINDING_REQUEST_TYPE_SNS)) {
						if (bind_response.snstype
								.equals(LoginSettingManager.BINDING_SNS_TYPE_SINA)) {
							accountInfo.removeAccessToken("3", "3", "1");
							CmmobiSnsLib.getInstance(SettingActivity.this)
									.removeOauth(SHARE_TO.SINA.ordinal());
							// lsm.deleteBindingInfo(LoginSettingManager.BINDING_TYPE_SNS,
							// LoginSettingManager.BINDING_SNS_TYPE_SINA);
							tv_sinaweibostate.setText(R.string.not_bind);
						} else if (bind_response.snstype
								.equals(LoginSettingManager.BINDING_SNS_TYPE_RENREN)) {
							accountInfo.removeAccessToken("3", "3", "2");
							CmmobiSnsLib.getInstance(SettingActivity.this)
									.removeOauth(SHARE_TO.RENREN.ordinal());
							// lsm.deleteBindingInfo(LoginSettingManager.BINDING_TYPE_SNS,
							// LoginSettingManager.BINDING_SNS_TYPE_RENREN);
							tv_renrenstate.setText(R.string.not_bind);
						} else if (bind_response.snstype
								.equals(LoginSettingManager.BINDING_SNS_TYPE_TENCENT)) {
							accountInfo.removeAccessToken("3", "3", "6");
							CmmobiSnsLib.getInstance(SettingActivity.this)
									.removeOauth(SHARE_TO.TENC.ordinal());
							// lsm.deleteBindingInfo(LoginSettingManager.BINDING_TYPE_SNS,
							// LoginSettingManager.BINDING_SNS_TYPE_TENCENT);
							tv_tencentweibostate.setText(R.string.not_bind);
						}
					}
				}
			} catch (Exception e) {
				// TODO: handle exception
			}
			break;
		case Requester2.RESPONSE_TYPE_UNSAFEBOX:
			ZDialog.dismiss();
			try {
				GsonResponse2.unSafeboxResponse res = (GsonResponse2.unSafeboxResponse) msg.obj;
				if (res != null) {
					if (res.status.equals("0")) {
						lsm.setGesturepassword(null);
						lsm.setSafeIsOn(false);
						//DiaryManager.getInstance().moveSaveboxDiaryToNormal(ActiveAccount.getInstance(this).getUID());
						/*
						 * Intent in = new Intent(
						 * HomepageMyselfDiaryActivity.SAVE_BOX_MODE_CHANGE);
						 * LocalBroadcastManager
						 * .getInstance(this).sendBroadcast(in);
						 */
					} else if (res.status.equals("200600")) {
						Prompt.Alert(this, Constant.CRM_STATUS[Integer
								.parseInt(res.crm_status)]);
					} else {
						Prompt.Alert("注销保险箱失败");
					}
				} else {
					Prompt.Alert(this, "注销保险箱失败，网络不给力");
				}
			} catch (Exception e) {
				// TODO: handle exception
			}
			break;
		case Requester2.RESPONSE_TYPE_UNBIND:
			ZDialog.dismiss();
			try {
				GsonResponse2.unbindResponse gru = (GsonResponse2.unbindResponse) msg.obj;
				if (gru != null && gru.status != null) {
					System.out.println("====== unbind ====== 1");
					if (gru.status.equals("0")) {
						if (gru.binding_type
								.equals(LoginSettingManager.BINDING_REQUEST_TYPE_PHONE)) {
							if (gru.phone_type
									.equals(LoginSettingManager.PHONE_TYPE_MAIN)) {
								phonebindstate = null;
								tv_phonebindstate.setText(R.string.not_bind);
								lsm.deleteBindingInfo(
										LoginSettingManager.BINDING_TYPE_PHONE,
										LoginSettingManager.BINDING_INFO_POINTLESS);
								if (lsm.getGesturepassword() != null) {
									Requester2.unSafebox(handler);
								}
								/*
								 * safestate = null;
								 * tv_safestate.setText(R.string.not_bind);
								 */
								lsm.deleteBindingInfo(
										LoginSettingManager.BINDING_TYPE_PHONE_SEC,
										LoginSettingManager.BINDING_INFO_POINTLESS);
							}/*
							 * else
							 * if(gru.phone_type.equals(LoginSettingManager.
							 * PHONE_TYPE_SEC)){ safestate = null;
							 * tv_safestate.setText(R.string.not_bind);
							 * lsm.deleteBindingInfo
							 * (LoginSettingManager.BINDING_TYPE_PHONE_SEC,
							 * LoginSettingManager.BINDING_INFO_POINTLESS); }
							 */
						} else if (gru.binding_type
								.equals(LoginSettingManager.BINDING_REQUEST_TYPE_EMAIL)) {
							emailbindstate = null;
							tv_emailbindstate.setText(R.string.not_bind);
							lsm.deleteBindingInfo(
									LoginSettingManager.BINDING_TYPE_EMAIL,
									LoginSettingManager.BINDING_INFO_POINTLESS);
						} else if (gru.binding_type
								.equals(LoginSettingManager.BINDING_REQUEST_TYPE_SNS)) {
							System.out.println("====== unbind ====== 2");
							System.out.println(" snstype =" + gru.snstype
									+ "===6==");
							if (gru.snstype
									.equals(LoginSettingManager.BINDING_SNS_TYPE_SINA)) {
								accountInfo.removeAccessToken("3", "3", "1");
								CmmobiSnsLib.getInstance(SettingActivity.this)
										.removeOauth(SHARE_TO.SINA.ordinal());
								lsm.deleteBindingInfo(
										LoginSettingManager.BINDING_TYPE_SNS,
										LoginSettingManager.BINDING_SNS_TYPE_SINA);
								DiaryManager
										.getInstance()
										.removeShareStatus(
												LoginSettingManager.BINDING_SNS_TYPE_SINA);
								System.out.println("====unbind sina ==");
								tv_sinaweibostate.setText(R.string.not_bind);
							} else if (gru.snstype
									.equals(LoginSettingManager.BINDING_SNS_TYPE_RENREN)) {
								accountInfo.removeAccessToken("3", "3", "2");
								CmmobiSnsLib.getInstance(SettingActivity.this)
										.removeOauth(SHARE_TO.RENREN.ordinal());
								lsm.deleteBindingInfo(
										LoginSettingManager.BINDING_TYPE_SNS,
										LoginSettingManager.BINDING_SNS_TYPE_RENREN);
								DiaryManager
										.getInstance()
										.removeShareStatus(
												LoginSettingManager.BINDING_SNS_TYPE_RENREN);
								System.out.println("====unbind renren ==");
								tv_renrenstate.setText(R.string.not_bind);
							} else if (gru.snstype
									.equals(LoginSettingManager.BINDING_SNS_TYPE_TENCENT)) {
								System.out.println("====unbind tencent ==");
								accountInfo.removeAccessToken("3", "3", "6");
								CmmobiSnsLib.getInstance(SettingActivity.this)
										.removeOauth(SHARE_TO.TENC.ordinal());
								lsm.deleteBindingInfo(
										LoginSettingManager.BINDING_TYPE_SNS,
										LoginSettingManager.BINDING_SNS_TYPE_TENCENT);
								DiaryManager
										.getInstance()
										.removeShareStatus(
												LoginSettingManager.BINDING_SNS_TYPE_TENCENT);
								tv_tencentweibostate.setText(R.string.not_bind);
								System.out.println("====unbind tencent ==");
							}
						} else {

						}
						Prompt.Dialog(this, false, "提示", "解绑定成功", null);
					} else if (gru.status.equals("200600")) {
						Prompt.Dialog(this, false, "提示",
								Constant.CRM_STATUS[Integer
										.parseInt(gru.crm_status)], null);
					} else {
						Prompt.Dialog(this, false, "提示", "操作失败，请稍后再试", null);
					}
				} else {
					Prompt.Dialog(this, false, "提示", "操作失败，网络不给力", null);
				}
			} catch (Exception e) {
				// TODO: handle exception
			}
			break;

		case CRMRequester.RESPONSE_TYPE_GET_RECOMMEND:
			ZDialog.dismiss();
			CRM_Object.getRecommendResponse aur = (CRM_Object.getRecommendResponse) msg.obj;
			if (aur != null) {
				CRM_Object.recItem item[] = aur.items;
				for (int i = 0; i < item.length; i++) {
					if (item[i].ismain.equals("1")
							&& Integer.parseInt(item[i].appsort) <= 4
							&& recItem.size() < 4) {
						recItem.add(item[i]);
					}
				}
				
				iv_dismiss = (ImageView) view_recommend
						.findViewById(R.id.iv_dismiss);
				iv_more = (ImageView) view_recommend.findViewById(R.id.iv_more);
				iv_dismiss.setOnClickListener(this);
				iv_more.setOnClickListener(this);

				iv_recommend01 = (ImageView) view_recommend
						.findViewById(R.id.iv_recommend01);
				iv_recommend02 = (ImageView) view_recommend
						.findViewById(R.id.iv_recommend02);
				iv_recommend03 = (ImageView) view_recommend
						.findViewById(R.id.iv_recommend03);
				iv_recommend04 = (ImageView) view_recommend
						.findViewById(R.id.iv_recommend04);
				
				iv_recommend01.setOnClickListener(this);
				iv_recommend02.setOnClickListener(this);
				iv_recommend03.setOnClickListener(this);
				iv_recommend04.setOnClickListener(this);

				switch (recItem.size()) {
				case 0:
					iv_recommend01.setVisibility(View.GONE);
					iv_recommend02.setVisibility(View.GONE);
					iv_recommend03.setVisibility(View.GONE);
					iv_recommend04.setVisibility(View.GONE);
					break;
				case 1:
					iv_recommend01.setVisibility(View.VISIBLE);
					iv_recommend02.setVisibility(View.GONE);
					iv_recommend03.setVisibility(View.GONE);
					iv_recommend04.setVisibility(View.GONE);
					imageLoader.displayImage(recItem.get(0).appimage,
							iv_recommend01, options, animateFirstListener,
							ActiveAccount.getInstance(this).getUID(), 1);
					break;
				case 2:
					iv_recommend01.setVisibility(View.VISIBLE);
					iv_recommend02.setVisibility(View.VISIBLE);
					iv_recommend03.setVisibility(View.GONE);
					iv_recommend04.setVisibility(View.GONE);
					imageLoader.displayImage(recItem.get(0).appimage,
							iv_recommend01, options, animateFirstListener,
							ActiveAccount.getInstance(this).getUID(), 1);
					imageLoader.displayImage(recItem.get(1).appimage,
							iv_recommend02, options, animateFirstListener,
							ActiveAccount.getInstance(this).getUID(), 1);
					break;
				case 3:
					iv_recommend01.setVisibility(View.VISIBLE);
					iv_recommend02.setVisibility(View.VISIBLE);
					iv_recommend03.setVisibility(View.VISIBLE);
					iv_recommend04.setVisibility(View.GONE);
					imageLoader.displayImage(recItem.get(0).appimage,
							iv_recommend01, options, animateFirstListener,
							ActiveAccount.getInstance(this).getUID(), 1);
					imageLoader.displayImage(recItem.get(1).appimage,
							iv_recommend02, options, animateFirstListener,
							ActiveAccount.getInstance(this).getUID(), 1);
					imageLoader.displayImage(recItem.get(2).appimage,
							iv_recommend03, options, animateFirstListener,
							ActiveAccount.getInstance(this).getUID(), 1);
					break;
				case 4:
					iv_recommend01.setVisibility(View.VISIBLE);
					iv_recommend02.setVisibility(View.VISIBLE);
					iv_recommend03.setVisibility(View.VISIBLE);
					iv_recommend04.setVisibility(View.VISIBLE);
					imageLoader.displayImage(recItem.get(0).appimage,
							iv_recommend01, options, animateFirstListener,
							ActiveAccount.getInstance(this).getUID(), 1);
					imageLoader.displayImage(recItem.get(1).appimage,
							iv_recommend02, options, animateFirstListener,
							ActiveAccount.getInstance(this).getUID(), 1);
					imageLoader.displayImage(recItem.get(2).appimage,
							iv_recommend03, options, animateFirstListener,
							ActiveAccount.getInstance(this).getUID(), 1);
					imageLoader.displayImage(recItem.get(3).appimage,
							iv_recommend04, options, animateFirstListener,
							ActiveAccount.getInstance(this).getUID(), 1);
					
					break;

				default:
					break;
				}
			}
			break;
		}

		return false;
	}

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		// Log.e(TAG, "==2" + privmsg + friends +sysc_type + launch_type);
		if (/*privmsg.equals(lsm.getPrivmsg_type())
				&& friends.equals(lsm.getFriends_type())
				&& 
					 * content.equals(lsm.getDiary_type()) &&
					 * location.equals(lsm.getPosition_type()) &&
					 * voice.equals(lsm.getAudio_type()) &&
					 */launch_type.equals(lsm.getLaunch_type())
				&& sysc_type.equals(lsm.getSysc_type())) {
			this.finish();
		} else {
			ZDialog.show(R.layout.progressdialog, false, true, this);
			Requester2.setPrivacy(handler, null, null, null, null, null,
					null, launch_type, sysc_type);
		}

	}

	@Override
	public void onAnimationEnd(Animation animation) {
		// TODO Auto-generated method stub
		if (animation.equals(translate_WifiToAny)) {
			iv_type_wifi.setAlpha(0);
			iv_type_any.setAlpha(255);
			sysc_type = SYSC_TYPE_ANY;
		} else if (animation.equals(translate_AnyToWifi)) {
			iv_type_wifi.setAlpha(255);
			iv_type_any.setAlpha(0);
			sysc_type = SYSC_TYPE_WIFI;
		} else if (animation.equals(translate_WatchToShoot)) {
			launch_type = LAUNCH_TYPE_SHOOT;
			iv_type_shoot.setAlpha(255);
			iv_type_watch.setAlpha(0);
		} else if (animation.equals(translate_ShootToWatch)) {
			launch_type = LAUNCH_TYPE_WATCH;
			iv_type_shoot.setAlpha(0);
			iv_type_watch.setAlpha(255);
		}
	}

	@Override
	public void onAnimationRepeat(Animation animation) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onAnimationStart(Animation animation) {
		// TODO Auto-generated method stub
		if (animation.equals(translate_WifiToAny)) {

		}
	}

}
