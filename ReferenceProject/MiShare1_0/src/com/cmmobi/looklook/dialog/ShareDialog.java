package com.cmmobi.looklook.dialog;

import java.util.ArrayList;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager.LayoutParams;
import cn.zipper.framwork.core.ZActivity;
import cn.zipper.framwork.core.ZApplication;
import cn.zipper.framwork.core.ZDialog;
import cn.zipper.framwork.io.network.ZNetworkStateDetector;
import cn.zipper.framwork.utils.ZByteToSize;

import com.cmmobi.looklook.MainApplication;
import com.cmmobi.looklook.R;
import com.cmmobi.looklook.activity.DiaryPreviewActivity;
import com.cmmobi.looklook.activity.ShareDiaryActivity;
import com.cmmobi.looklook.activity.ShareDiaryActivity.ShareMessage;
import com.cmmobi.looklook.common.constant.Constant;
import com.cmmobi.looklook.common.gson.GsonResponse3;
import com.cmmobi.looklook.common.gson.GsonResponse3.MyBind;
import com.cmmobi.looklook.common.gson.GsonResponse3.MyDiary;
import com.cmmobi.looklook.common.gson.GsonResponse3.MyDiaryList;
import com.cmmobi.looklook.common.gson.GsonResponse3.getDiaryUrlResponse;
import com.cmmobi.looklook.common.gson.GsonResponse3.platformUrls;
import com.cmmobi.looklook.common.gson.Requester3;
import com.cmmobi.looklook.common.gson.WeiboRequester;
import com.cmmobi.looklook.common.gson.WeiboResponse.RWUserInfo;
import com.cmmobi.looklook.common.gson.WeiboResponse.SWUserInfo;
import com.cmmobi.looklook.common.gson.WeiboResponse.TWUserInfo;
//import com.cmmobi.looklook.common.service.CommonService;
import com.cmmobi.looklook.common.utils.CmmobiClickAgentWrapper;
import com.cmmobi.looklook.common.view.Xdialog;
import com.cmmobi.looklook.fragment.SettingFragment;
import com.cmmobi.looklook.info.profile.AccountInfo;
import com.cmmobi.looklook.info.profile.ActiveAccount;
import com.cmmobi.looklook.info.profile.DiaryManager;
import com.cmmobi.looklook.info.profile.LoginSettingManager;
import com.cmmobi.looklook.networktask.INetworkTask;
import com.cmmobi.looklook.networktask.NetworkTaskManager;
import com.cmmobi.looklook.prompt.Prompt;
import com.cmmobi.sns.api.CmmobiSnsLib;
import com.cmmobi.sns.api.QQMobileAuth;
import com.cmmobi.sns.api.QQMobileAuth.QQMobileAccInfo;
import com.cmmobi.sns.api.QQMobileAuth.QQMobleUiListener;
import com.cmmobi.sns.oauthv2.OAuthV2;
import com.cmmobi.sns.utils.ConfigUtil.SHARE_TO;
import com.google.gson.Gson;
import com.weibo.sdk.android.api.WeiboAuthListener;
import com.weibo.sdk.android.api.WeiboDialogError;
import com.weibo.sdk.android.api.WeiboException;

public class ShareDialog extends ZActivity
{
	private final String TAG = this.getClass().getSimpleName();
	public static final String MODE_VSHARE = "type_share_v";
	public static final String TYPY_SHARE = "typy_share";
	//1新浪 2人人 5 QZONE空间 6腾讯 9微信朋友圈 10短信 11邮箱 12微信好友 100站内公开 101朋友圈 102私密分享 103微享
	public static final int TYPE_SHARE_V = 103;
	public static final int TYPE_SHARE_WEIXIN = 12;
	public static final int TYPE_SHARE_WEIXINFRIEND = 9;
	public static final int TYPE_SHARE_QQ = 5;
	public static final int TYPE_SHARE_TENCENT = 6;
	public static final int TYPE_SHARE_SINA = 1;
	public static final int TYPE_SHARE_RENREN = 2;
	public static final int TYPE_SHARE_MAIL = 11;
	public static final int TYPE_SHARE_MSG = 10;

	private String userID = "";
	private AccountInfo accountInfo = null;
	private ActiveAccount acct = null;
	private CmmobiSnsLib csb = null;
	private MyBind myBind = null;
	private int mShareType = 0;
	
	private MyDiaryList diaryList = null;
	private MyDiary[] diarydefault = null;
	private ArrayList<MyDiary> diaryGroup = new ArrayList<MyDiary>();
	
	private ShareMessage mTmpMessage = null;
	
	private LoginSettingManager lsm;
	
	// for UmengclickAgentWrapper
/*	private long begin = 0;
	private long end_sup_sha_bin = 0; // 绑定结束的时间点
//	private long end_sup_sha = 0; // 绑定离开第三方页面的时间点	
*/
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dialog_share);

		LayoutParams p = getWindow().getAttributes();
		p.width = LayoutParams.MATCH_PARENT; // 设置屏幕宽度
		p.alpha = 0.9f; // 增加一点按钮透明
		getWindow().setAttributes(p); // 设置生效
		getWindow().setGravity(Gravity.BOTTOM);

		findViewById(R.id.btn_share_weixiang).setOnClickListener(this);

		findViewById(R.id.btn_share_weixin).setOnClickListener(this);
		findViewById(R.id.btn_share_weixin_friend).setOnClickListener(this);
		findViewById(R.id.btn_share_qzone).setOnClickListener(this);
		findViewById(R.id.btn_share_txwb).setOnClickListener(this);

		findViewById(R.id.btn_share_sinawb).setOnClickListener(this);
		findViewById(R.id.btn_share_renren).setOnClickListener(this);
		findViewById(R.id.btn_share_mail).setOnClickListener(this);
		findViewById(R.id.btn_share_sms).setOnClickListener(this);

		findViewById(R.id.btn_cancel_share).setOnClickListener(this);

		userID = ActiveAccount.getInstance(ZApplication.getInstance()).getUID();
		accountInfo = AccountInfo.getInstance(userID);
		acct = ActiveAccount.getInstance(this);
		csb = CmmobiSnsLib.getInstance(this);
		lsm = accountInfo.setmanager;
		
		if(!getIntent().hasExtra(MODE_VSHARE))
		{
			findViewById(R.id.ll_share_top).setVisibility(View.GONE);
		}
		
		String diaryStr = getIntent().getStringExtra(DiaryPreviewActivity.INTENT_ACTION_DIARY_STRING);
		diarydefault = new Gson().fromJson(diaryStr, MyDiary[].class);
		
		String list = getIntent().getStringExtra(DiaryPreviewActivity.INTENT_ACTION_DIARYLIST_STRING);
		diaryList = new Gson().fromJson(list, MyDiaryList.class);
		
		if(diarydefault == null || diaryList == null)
		{
			Log.v(TAG, "no diary or diarylist");
			finish();
			return;
		}
		
		for (int i = 0; i < diarydefault.length; i ++)
		{
			diaryGroup.add(diarydefault[i]);
		}

	}

	@Override
	public void onClick(View v)
	{
		switch (v.getId())
		{
		case R.id.btn_cancel_share:
			this.finish();
			break;
		case R.id.btn_share_weixiang:
			Intent vIntent = new Intent(this, ShareDiaryActivity.class);
			vIntent.putExtras(getIntent());
			vIntent.putExtra(TYPY_SHARE, TYPE_SHARE_V);
			startActivity(vIntent);
			finish();
			break;
		case R.id.btn_share_weixin:
			if(!showNetworkWarning(new ShareMission()
			{
				
				@Override
				public void doShare()
				{
					shareToWeixin();
				}
			}))
			{
				shareToWeixin();
			}
			break;
		case R.id.btn_share_weixin_friend:
			if(!showNetworkWarning(new ShareMission()
			{
				
				@Override
				public void doShare()
				{
					shareToWeixinFriend();
				}
			}))
			{
				shareToWeixinFriend();
			}
			break;
		case R.id.btn_share_qzone:
			if(!showNetworkWarning(new ShareMission()
			{
				
				@Override
				public void doShare()
				{
					shareToQQ();
				}
			}))
			{
				shareToQQ();
			}
			break;
		case R.id.btn_share_txwb:
			shareToTencent();
			break;
		case R.id.btn_share_sinawb:
			shareToSina();
			break;
		case R.id.btn_share_renren:
			shareToRenren();
			break;
		case R.id.btn_share_mail:
			if(!showNetworkWarning(new ShareMission()
			{
				
				@Override
				public void doShare()
				{
					shareToEmail();
				}
			}))
			{
				shareToEmail();
			}
			break;
		case R.id.btn_share_sms:
			if(!showNetworkWarning(new ShareMission()
			{
				
				@Override
				public void doShare()
				{
					shareToMsg();
				}
			}))
			{
				shareToMsg();
			}
			break;

		default:
			break;
		}

	}
	
	@Override
	public boolean handleMessage(Message msg)
	{
		switch(msg.what)
		{
		case HANDLER_SINA_AUTHOR_SUCCESS:
			String sina_snsid = ActiveAccount.getInstance(this).getSNSID(SHARE_TO.SINA.ordinal());
			if(sina_snsid!=null){
				WeiboRequester.getSinaAccountInfo(getHandler(), sina_snsid);
			}else{
				ZDialog.dismiss();
				Prompt.Alert(this, "新浪授权微博异常");
			}
			break;
		case HANDLER_RENREN_AUTHOR_SUCCESS:
			String renren_snsid = ActiveAccount.getInstance(this).getSNSID(SHARE_TO.RENREN.ordinal());
			if(renren_snsid != null){
				WeiboRequester.getRenrenAccountInfo(getHandler(), renren_snsid);
			}else{
				ZDialog.dismiss();
				Prompt.Alert(this, "人人授权微博异常");
			}
			break;
		case HANDLER_TENCENT_AUTHOR_SUCCESS:
			String tencent_snsid = ActiveAccount.getInstance(this).getSNSID(SHARE_TO.TENC.ordinal());
			if(tencent_snsid != null){
				WeiboRequester.getTencentAccountInfo(getHandler(), tencent_snsid);
			}else{
				ZDialog.dismiss();
				Prompt.Alert(this, "腾讯授权微博异常");
			}
			break;
		case HANDLER_QQMOBILE_AUTHOR_SUCCESS:
			ZDialog.show(R.layout.progressdialog, false, true,
					this);
			String qqmobile_snsid = ActiveAccount.getInstance(this).getSNSID(
					SHARE_TO.QQMOBILE.ordinal());
			if (qqmobile_snsid != null) {
				QQMobileAuth.getQQMobileAccInfo(getHandler(), qqmobile_snsid, HANDLER_QQMOBILE_ACCOUTINFO_SUCCESS);
			} else {
				Prompt.Alert(this, "授权微博异常");
			}
			break;
		case HANDLER_QQMOBILE_ACCOUTINFO_SUCCESS:
			
			QQMobileAccInfo accInfo = (QQMobileAccInfo) msg.obj;
			
			if(accInfo==null || !acct.updateQQMobileAuthor(accInfo)){	
				Prompt.Alert(this, "获取QQ个人信息异常");
				accountInfo.removeAccessToken("3", "3", "13");
				CmmobiSnsLib.getInstance(this).removeOauth(
						SHARE_TO.QQMOBILE.ordinal());
				ZDialog.dismiss();
			}else{
				Log.e(TAG, "qq getaccountinfo ok, binding..");
				OAuthV2 qqm_oa = csb.acquireQQMobileOauth();
				if (qqm_oa != null) {
					Requester3.bindAccount(getHandler(), LoginSettingManager.BINDING_TYPE_SNS, null,
							acct.snsid,
							LoginSettingManager.BINDING_SNS_TYPE_QQMOBILE, null,
							acct.sex, acct.address, acct.birthdate,
							qqm_oa.getAccessToken(),
							qqm_oa.getExpiresIn(),
							qqm_oa.getExpiresTime(),
							qqm_oa.getRefreshToken(), qqm_oa.getNick(),
							qqm_oa.getOpenkey(), qqm_oa.getNick());
					myBind = new MyBind();
					myBind.binding_type = LoginSettingManager.BINDING_TYPE_SNS;
					myBind.snsuid = acct.snsid;
					myBind.snstype = LoginSettingManager.BINDING_SNS_TYPE_QQMOBILE;
					myBind.sns_nickname = qqm_oa.getNick();
					myBind.sns_token = qqm_oa.getAccessToken();
					myBind.sns_expiration_time = qqm_oa.getExpiresTime();
					myBind.sns_effective_time = qqm_oa.getExpiresIn();
					myBind.sns_openkey = qqm_oa.getOpenkey();
					myBind.sns_refresh_token = qqm_oa.getRefreshToken();
					System.out.println("== nickname " + myBind.sns_nickname);
				} else {
					accountInfo.removeAccessToken("3", "3", "13");
					CmmobiSnsLib.getInstance(this).removeOauth(
							SHARE_TO.QQMOBILE.ordinal());
					ZDialog.dismiss();
				}
			}
			break;
		case WeiboRequester.SINA_INTERFACE_GET_ACCOUNTINFO:
			SWUserInfo sn_object  = (SWUserInfo) msg.obj;
			if(sn_object==null || sn_object.screen_name==null || !acct.updateSinaAuthor(sn_object)){
				ZDialog.dismiss();
				Prompt.Alert(this, "获取新浪微博个人信息异常");
				accountInfo.removeAccessToken("3", "3", "1");
				CmmobiSnsLib.getInstance(this).removeOauth(SHARE_TO.SINA.ordinal());
			}else{
				Log.e(TAG, "sina getaccountinfo ok, binding..");
				OAuthV2 sina_oa = csb.acquireSinaOauth();
				if (sina_oa != null) {
					Requester3.bindAccount(getHandler(), LoginSettingManager.BINDING_TYPE_SNS, null,
							acct.snsid, "1", null, acct.sex, acct.address,
							acct.birthdate, sina_oa.getAccessToken(),
							sina_oa.getExpiresIn(), sina_oa.getExpiresTime(),
							sina_oa.getRefreshToken(), sina_oa.getNick(),
							sina_oa.getOpenkey(), sina_oa.getNick());
					myBind = new MyBind();
					myBind.binding_type = LoginSettingManager.BINDING_TYPE_SNS;
					myBind.snsuid = acct.snsid;
					myBind.snstype = LoginSettingManager.BINDING_SNS_TYPE_SINA;
					myBind.sns_nickname =  sina_oa.getNick();
					myBind.sns_token = sina_oa.getAccessToken();
					myBind.sns_expiration_time = sina_oa.getExpiresTime();
					myBind.sns_effective_time = sina_oa.getExpiresIn();
					myBind.sns_openkey = sina_oa.getOpenkey();
					myBind.sns_refresh_token = sina_oa.getRefreshToken();
					System.out.println("== nickname " + myBind.sns_nickname);
				}else {
					accountInfo.removeAccessToken("3", "3", "1");
					CmmobiSnsLib.getInstance(ShareDialog.this).removeOauth(SHARE_TO.SINA.ordinal());
					
				}
				
			}
			break;
		case WeiboRequester.RENREN_INTERFACE_GET_ACCOUNTINFO:
			RWUserInfo sn_renren  = (RWUserInfo) msg.obj;
			if(sn_renren==null || sn_renren.response==null ||  !acct.updateRenrenAuthor(sn_renren)){
				ZDialog.dismiss();
				Prompt.Alert(this, "获取人人微博个人信息异常");	
				accountInfo.removeAccessToken("3", "3", "2");
				CmmobiSnsLib.getInstance(this).removeOauth(SHARE_TO.RENREN.ordinal());
			}else{
				Log.e(TAG, "renren getaccountinfo ok, binding..");
				OAuthV2 renren_oa = csb.acquireRenrenOauth();
				if (renren_oa != null) {
					Requester3.bindAccount(getHandler(), LoginSettingManager.BINDING_TYPE_SNS, null,
							acct.snsid, LoginSettingManager.BINDING_SNS_TYPE_RENREN, null, acct.sex, acct.address,
							acct.birthdate, renren_oa.getAccessToken(),
							renren_oa.getExpiresIn(), renren_oa.getExpiresTime(),
							renren_oa.getRefreshToken(), renren_oa.getNick(),
							renren_oa.getOpenkey(), renren_oa.getNick());
					myBind = new MyBind();
					myBind.binding_type = LoginSettingManager.BINDING_TYPE_SNS;
					myBind.snsuid = acct.snsid;
					myBind.snstype = LoginSettingManager.BINDING_SNS_TYPE_RENREN;
					myBind.sns_nickname =  renren_oa.getNick();
					myBind.sns_token = renren_oa.getAccessToken();
					myBind.sns_expiration_time = renren_oa.getExpiresTime();
					myBind.sns_effective_time = renren_oa.getExpiresIn();
					myBind.sns_openkey = renren_oa.getOpenkey();
					myBind.sns_refresh_token = renren_oa.getRefreshToken();
					System.out.println("== nickname " + myBind.sns_nickname);
				}else{
					accountInfo.removeAccessToken("3", "3", "2");
					CmmobiSnsLib.getInstance(ShareDialog.this).removeOauth(SHARE_TO.RENREN.ordinal());
					
				}
				
			}
			break;
		case WeiboRequester.TENCENT_INTERFACE_GET_ACCOUNTINFO:
			TWUserInfo sn_tencent  = (TWUserInfo) msg.obj;
			if(sn_tencent==null || sn_tencent.data==null || !acct.updateTencentAuthor(sn_tencent)){
				ZDialog.dismiss();
				Prompt.Alert(this, "获取腾讯微博个人信息异常");
				accountInfo.removeAccessToken("3", "3", "6");
				CmmobiSnsLib.getInstance(this).removeOauth(SHARE_TO.TENC.ordinal());
			}else{
				Log.e(TAG, "tencent getaccountinfo ok, binding..");
				OAuthV2 tencent_oa = csb.acquireTencentWeiboOauth();
				if (tencent_oa != null) {
					Requester3.bindAccount(getHandler(), LoginSettingManager.BINDING_TYPE_SNS, null,
							acct.snsid, LoginSettingManager.BINDING_SNS_TYPE_TENCENT, null, acct.sex, acct.address,
							acct.birthdate, tencent_oa.getAccessToken(),
							tencent_oa.getExpiresIn(), tencent_oa.getExpiresTime(),
							tencent_oa.getRefreshToken(), tencent_oa.getNick(),
							tencent_oa.getOpenkey(), tencent_oa.getNick());
					myBind = new MyBind();
					myBind.binding_type = LoginSettingManager.BINDING_TYPE_SNS;
					myBind.snsuid = acct.snsid;
					myBind.snstype = LoginSettingManager.BINDING_SNS_TYPE_TENCENT;
					myBind.sns_nickname =  tencent_oa.getNick();
					myBind.sns_token = tencent_oa.getAccessToken();
					myBind.sns_expiration_time = tencent_oa.getExpiresTime();
					myBind.sns_effective_time = tencent_oa.getExpiresIn();
					myBind.sns_openkey = tencent_oa.getOpenkey();
					myBind.sns_refresh_token = tencent_oa.getRefreshToken();
					System.out.println("== nickname " + myBind.sns_nickname);				
				}else {
					accountInfo.removeAccessToken("3", "3", "6");
					CmmobiSnsLib.getInstance(ShareDialog.this).removeOauth(SHARE_TO.TENC.ordinal());
					
				};
				
			}
			break;
		case Requester3.RESPONSE_TYPE_BINDING:
			ZDialog.dismiss();
			try {
				GsonResponse3.bindingResponse bind_response = (GsonResponse3.bindingResponse) msg.obj;
				if (bind_response != null && bind_response.status != null
						&& bind_response.status.equals("0")) {
					if (bind_response.binding_type
							.equals(LoginSettingManager.BINDING_TYPE_SNS)
							&& myBind != null) {
						if (bind_response.snstype
								.equals(LoginSettingManager.BINDING_SNS_TYPE_SINA)) {
							/*if (begin != 0) {
								end_sup_sha_bin = TimeHelper.getInstance().now()
										- begin;
								CmmobiClickAgentWrapper.onEventDuration(
										this, "sup_sha_bin", "1",
										end_sup_sha_bin);
							}*/
							CmmobiClickAgentWrapper.onEvent(this, "account_binding", "3");
							lsm.addBindingInfo(myBind);
						} else if (bind_response.snstype
								.equals(LoginSettingManager.BINDING_SNS_TYPE_RENREN)) {
							/*if (begin != 0) {
								end_sup_sha_bin = TimeHelper.getInstance().now()
										- begin;
								CmmobiClickAgentWrapper.onEventDuration(
										this, "sup_sha_bin", "2",
										end_sup_sha_bin);
							}*/
							CmmobiClickAgentWrapper.onEvent(this, "account_binding", "6");
							lsm.addBindingInfo(myBind);
						} else if (bind_response.snstype
								.equals(LoginSettingManager.BINDING_SNS_TYPE_TENCENT)) {
						/*	if (begin != 0) {
								end_sup_sha_bin = TimeHelper.getInstance().now()
										- begin;
								CmmobiClickAgentWrapper.onEventDuration(
										this, "sup_sha_bin", "6",
										end_sup_sha_bin);
							}*/
							CmmobiClickAgentWrapper.onEvent(this, "account_binding", "5");
							lsm.addBindingInfo(myBind);
						} else if (bind_response.snstype
								.equals(LoginSettingManager.BINDING_SNS_TYPE_QQMOBILE)) {
						/*	if (begin != 0) {
								end_sup_sha_bin = TimeHelper.getInstance().now()
										- begin;
								CmmobiClickAgentWrapper.onEventDuration(
										this, "sup_sha_bin", "13",
										end_sup_sha_bin);
							}*/
							CmmobiClickAgentWrapper.onEvent(this, "account_binding", "4");
							lsm.addBindingInfo(myBind);
						}
						//startService(new Intent(this, CommonService.class));
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
							.equals(LoginSettingManager.BINDING_TYPE_SNS)) {
						if (bind_response.snstype
								.equals(LoginSettingManager.BINDING_SNS_TYPE_SINA)) {
							accountInfo.removeAccessToken("3", "3", "1");
							CmmobiSnsLib.getInstance(this)
									.removeOauth(SHARE_TO.SINA.ordinal());
							// lsm.deleteBindingInfo(LoginSettingManager.BINDING_TYPE_SNS,
							// LoginSettingManager.BINDING_SNS_TYPE_SINA);
						} else if (bind_response.snstype
								.equals(LoginSettingManager.BINDING_SNS_TYPE_RENREN)) {
							accountInfo.removeAccessToken("3", "3", "2");
							CmmobiSnsLib.getInstance(this)
									.removeOauth(SHARE_TO.RENREN.ordinal());
							// lsm.deleteBindingInfo(LoginSettingManager.BINDING_TYPE_SNS,
							// LoginSettingManager.BINDING_SNS_TYPE_RENREN);
						} else if (bind_response.snstype
								.equals(LoginSettingManager.BINDING_SNS_TYPE_TENCENT)) {
							accountInfo.removeAccessToken("3", "3", "6");
							CmmobiSnsLib.getInstance(this)
									.removeOauth(SHARE_TO.TENC.ordinal());
							// lsm.deleteBindingInfo(LoginSettingManager.BINDING_TYPE_SNS,
							// LoginSettingManager.BINDING_SNS_TYPE_TENCENT);
						} else if (bind_response.snstype
								.equals(LoginSettingManager.BINDING_SNS_TYPE_QQMOBILE)) {
							accountInfo.removeAccessToken("3", "3", "13");
							CmmobiSnsLib.getInstance(this)
									.removeOauth(SHARE_TO.QQMOBILE.ordinal());
							// lsm.deleteBindingInfo(LoginSettingManager.BINDING_TYPE_SNS,
							// LoginSettingManager.BINDING_SNS_TYPE_TENCENT);
						}
					}
				}
				saveAccount();
			} catch (Exception e) {
			}
			break;
		case Requester3.RESPONSE_TYPE_UNBIND:
			ZDialog.dismiss();
			try {
				GsonResponse3.unbindResponse gru = (GsonResponse3.unbindResponse) msg.obj;
				if (gru != null && gru.status != null) {
					System.out.println("====== unbind ====== 1");
					if (gru.status.equals("0")) {
						if (gru.binding_type.equals(LoginSettingManager.BINDING_TYPE_PHONE)) {
							lsm.deleteBindingInfo(
									LoginSettingManager.BINDING_TYPE_PHONE,
									LoginSettingManager.BINDING_INFO_POINTLESS);
							if (lsm.getGesturepassword() != null) {
								Requester3.unSafebox(handler);
							}
							lsm.deleteBindingInfo(
									LoginSettingManager.BINDING_TYPE_PHONE_SEC,
									LoginSettingManager.BINDING_INFO_POINTLESS);
						} else if (gru.binding_type
								.equals(LoginSettingManager.BINDING_TYPE_EMAIL)) {
							lsm.deleteBindingInfo(
									LoginSettingManager.BINDING_TYPE_EMAIL,
									LoginSettingManager.BINDING_INFO_POINTLESS);
						} else if (gru.binding_type
								.equals(LoginSettingManager.BINDING_TYPE_SNS)) {
							System.out.println("====== unbind ====== 2");
							System.out.println(" snstype =" + gru.snstype
									+ "===6==");
							if (gru.snstype
									.equals(LoginSettingManager.BINDING_SNS_TYPE_SINA)) {
								accountInfo.removeAccessToken("3", "3", "1");
								CmmobiSnsLib.getInstance(this)
										.removeOauth(SHARE_TO.SINA.ordinal());
								lsm.deleteBindingInfo(
										LoginSettingManager.BINDING_TYPE_SNS,
										LoginSettingManager.BINDING_SNS_TYPE_SINA);
								System.out.println("====unbind sina ==");
							} else if (gru.snstype
									.equals(LoginSettingManager.BINDING_SNS_TYPE_RENREN)) {
								accountInfo.removeAccessToken("3", "3", "2");
								CmmobiSnsLib.getInstance(this)
										.removeOauth(SHARE_TO.RENREN.ordinal());
								lsm.deleteBindingInfo(
										LoginSettingManager.BINDING_TYPE_SNS,
										LoginSettingManager.BINDING_SNS_TYPE_RENREN);
								System.out.println("====unbind renren ==");
							} else if (gru.snstype
									.equals(LoginSettingManager.BINDING_SNS_TYPE_TENCENT)) {
								System.out.println("====unbind tencent ==");
								accountInfo.removeAccessToken("3", "3", "6");
								CmmobiSnsLib.getInstance(this)
										.removeOauth(SHARE_TO.TENC.ordinal());
								lsm.deleteBindingInfo(
										LoginSettingManager.BINDING_TYPE_SNS,
										LoginSettingManager.BINDING_SNS_TYPE_TENCENT);
								System.out.println("====unbind tencent ==");
							} else if (gru.snstype
									.equals(LoginSettingManager.BINDING_SNS_TYPE_QQMOBILE)) {
								System.out.println("====unbind QQMOBILE ==");
								accountInfo.removeAccessToken("3", "3", "13");
								CmmobiSnsLib.getInstance(this)
										.removeOauth(SHARE_TO.QQMOBILE.ordinal());
								lsm.deleteBindingInfo(
										LoginSettingManager.BINDING_TYPE_SNS,
										LoginSettingManager.BINDING_SNS_TYPE_QQMOBILE);
								System.out.println("====unbind QQMOBILE ==");
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
			}
			break;
		}
		return false;
	}
	
	// 获取分享日记信息
	private ShareMessage getShareMessage()
	{
		ShareMessage msg = new ShareMessage();
		return msg;
	}

	// 分享到微信
	private void shareToWeixin()
	{
		
	}

	// 分享到微信朋友圈
	private void shareToWeixinFriend()
	{
	}

	// 分享到腾讯
	private void shareToTencent()
	{
		if (!isTencentBind())
		{
			// 绑定腾讯
			CmmobiSnsLib.getInstance(this).tencentWeiboAuthorize(tencentlistener);
		}else if(!isTencentBindEffective()){
			Prompt.Dialog(this, true, "提示", "您的绑定已过期，请重新绑定",
					new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog,
								int which) {
							// TODO Auto-generated method stub
							CmmobiSnsLib.getInstance(ShareDialog.this).tencentWeiboAuthorize(tencentlistener);
						}

					});
		}
		else
		{
			// 统计
			CmmobiClickAgentWrapper.onEvent(this, "content_share", diaryList.publishid, mShareType);			
			
			Intent intent = new Intent(this, ShareDiaryActivity.class);
			intent.putExtras(getIntent());
			intent.putExtra(TYPY_SHARE, TYPE_SHARE_TENCENT);
			startActivity(intent);
			finish();
		}
	}

	// 分享到新浪
	private void shareToSina()
	{
		if (!isSinaBind())
		{
			// 绑定新浪微博
			CmmobiSnsLib.getInstance(this).sinaAuthorize(sinalistener);
		}else if (!isSinaBindEffective())
		{
			Prompt.Dialog(this, true, "提示", "您的绑定已过期，请重新绑定",
					new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog,
								int which) {
							// TODO Auto-generated method stub
							// 绑定新浪微博
							CmmobiSnsLib.getInstance(ShareDialog.this).sinaAuthorize(sinalistener);						}

					});
			
		}
		else
		{
			// 统计
			CmmobiClickAgentWrapper.onEvent(this, "content_share", diaryList.publishid, mShareType);			

			Intent intent = new Intent(this, ShareDiaryActivity.class);
			intent.putExtras(getIntent());
			intent.putExtra(TYPY_SHARE, TYPE_SHARE_SINA);
			startActivity(intent);
			finish();
		}
	}

	// 分享到人人
	private void shareToRenren()
	{
		if (!isRenrenBind())
		{
			// 绑定人人
			CmmobiSnsLib.getInstance(this).renrenAuthorize(renrenlistener);
		}else if (!isRenrenBindEffective())
		{
			Prompt.Dialog(this, true, "提示", "您的绑定已过期，请重新绑定",
					new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog,
								int which) {
							// TODO Auto-generated method stub
							// 绑定人人
							CmmobiSnsLib.getInstance(ShareDialog.this).renrenAuthorize(renrenlistener);						
							}
					});
			
		}
		else
		{
			// 统计
			CmmobiClickAgentWrapper.onEvent(this, "content_share", diaryList.publishid, mShareType);			

			Intent intent = new Intent(this, ShareDiaryActivity.class);
			intent.putExtras(getIntent());
			intent.putExtra(TYPY_SHARE, TYPE_SHARE_RENREN);
			startActivity(intent);
			finish();
		}
	}
	
	// 分享到QQ
	private void shareToQQ()
	{
	}

	// 分享到邮件
	private void shareToEmail()
	{
	}

	// 分享到短信
	private void shareToMsg()
	{
	}
	
	// 发送邮件
	private void sendMail(String subject, String content){
		//系统邮件系统的动作为android.content.Intent.ACTION_SEND
		Intent email = new Intent(android.content.Intent.ACTION_SEND);
		email.setType("plain/text");
//		String[] emailReciver = new String[]{"looklook@gmail.com", "looklook@qq.com"};
		String emailSubject = subject;
		String emailBody = content;
		//设置邮件默认地址
//		email.putExtra(android.content.Intent.EXTRA_EMAIL, emailReciver);
		//设置邮件默认标题
		email.putExtra(android.content.Intent.EXTRA_SUBJECT, emailSubject);
		//设置要默认发送的内容
		email.putExtra(android.content.Intent.EXTRA_TEXT, emailBody);
		//调用系统的邮件系统
		startActivity(Intent.createChooser(email, "请选择邮件发送软件"));
	}
	
	// 发送短信
	private boolean sendMsg(String str) {
		Intent it = new Intent(Intent.ACTION_VIEW);

		it.putExtra("sms_body", str);

		it.setType("vnd.android-dir/mms-sms");

		startActivity(it);
		return true;
	}

	// Url拼接
	private String getUrl(String strLink)
	{
		String url = "我和我的小伙伴都惊呆了，快来看吧！ ";
		url += strLink;
		if(mTmpMessage.position != null && !mTmpMessage.position.equals("")
				/*&& tgbShowGps.isShown() && tgbShowGps.isChecked()*/)
		{
			
			if(isMyself())
			{
				url += " #我在这里：";
			}
			else
			{
				url += " #内容所在位置:";
			}
			url += mTmpMessage.position;
			url += "#";
		}
		
		return url;
	}
	
	// 判断自己
	private boolean isMyself()
	{
		return ActiveAccount.getInstance(MainApplication.getAppInstance()).getUID().equals(diarydefault[0].userid);
	}
	
	private boolean isSinaBind()
	{
		if (lsm.getBinding_type(
				LoginSettingManager.BINDING_TYPE_SNS,
				LoginSettingManager.BINDING_SNS_TYPE_SINA) !=null)
			return true;
		return false;
	}

	private boolean isRenrenBind()
	{
		if (lsm.getBinding_type(
				LoginSettingManager.BINDING_TYPE_SNS,
				LoginSettingManager.BINDING_SNS_TYPE_RENREN) !=null)
			return true;
		return false;
	}

	private boolean isTencentBind()
	{
		if (lsm.getBinding_type(
				LoginSettingManager.BINDING_TYPE_SNS,
				LoginSettingManager.BINDING_SNS_TYPE_TENCENT) !=null)
			return true;
		return false;
	}
	
	private boolean isQQBind()
	{
		if (lsm.getBinding_type(
				LoginSettingManager.BINDING_TYPE_SNS,
				LoginSettingManager.BINDING_SNS_TYPE_QQMOBILE) !=null)
			return true;
		return false;
	}

	
	private boolean isSinaBindEffective()
	{
		if (ActiveAccount.getInstance(this).isSNSBind("sina"))
			return true;
		return false;
	}

	private boolean isRenrenBindEffective()
	{
		if (ActiveAccount.getInstance(this).isSNSBind("renren"))
			return true;
		return false;
	}

	private boolean isTencentBindEffective()
	{
		if (ActiveAccount.getInstance(this).isSNSBind("tencent"))
			return true;
		return false;
	}
	
	private boolean isQQBindEffective()
	{
		if (ActiveAccount.getInstance(this).isSNSBind("qqmobile"))
			return true;
		return false;
	}

	private final int HANDLER_SINA_AUTHOR_SUCCESS = 1;
	private final int HANDLER_RENREN_AUTHOR_SUCCESS = 2;
	private final int HANDLER_TENCENT_AUTHOR_SUCCESS = 6;
	private final int HANDLER_QQMOBILE_AUTHOR_SUCCESS = 5;
	private final int HANDLER_QQMOBILE_ACCOUTINFO_SUCCESS = 51;

	private WeiboAuthListener sinalistener = new WeiboAuthListener()
	{

		@Override
		public void onComplete(int weiboIndex)
		{
			// Prompt.Alert("sina授权成功");
			Message message = getHandler().obtainMessage(HANDLER_SINA_AUTHOR_SUCCESS);
			getHandler().sendMessage(message);

		}

		@Override
		public void onCancel(int arg0)
		{
			Prompt.Alert("sina授权取消");
		}

		@Override
		public void onError(WeiboDialogError arg0, int arg1)
		{
			Prompt.Alert("sina授权错误");
		}

		@Override
		public void onWeiboException(WeiboException arg0, int arg1)
		{
			Prompt.Alert("sina授权异常");
		}

	};

	private WeiboAuthListener renrenlistener = new WeiboAuthListener()
	{

		@Override
		public void onComplete(int weiboIndex)
		{
			// Prompt.Alert("renren授权成功");
			Message message = getHandler().obtainMessage(HANDLER_RENREN_AUTHOR_SUCCESS);
			getHandler().sendMessage(message);

		}

		@Override
		public void onCancel(int arg0)
		{
			Prompt.Alert("renren授权取消");
		}

		@Override
		public void onError(WeiboDialogError arg0, int arg1)
		{
			Prompt.Alert("renren授权错误");
		}

		@Override
		public void onWeiboException(WeiboException arg0, int arg1)
		{
			Prompt.Alert("sina授权异常");
		}

	};

	private WeiboAuthListener tencentlistener = new WeiboAuthListener()
	{

		@Override
		public void onComplete(int weiboIndex)
		{
			// Prompt.Alert("登录成功");
			Message message = getHandler().obtainMessage(HANDLER_TENCENT_AUTHOR_SUCCESS);
			getHandler().sendMessage(message);

		}

		@Override
		public void onCancel(int arg0)
		{
			Prompt.Alert("tencent授权取消！");
		}

		@Override
		public void onError(WeiboDialogError arg0, int arg1)
		{
			Prompt.Alert("tencent授权错误！");
		}

		@Override
		public void onWeiboException(WeiboException arg0, int arg1)
		{
			Prompt.Alert("tencent授权异常！");
		}
	};
	
	
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//		super.onActivityResult(requestCode, resultCode, data);
		System.out.println("=====" + TAG  + " onActivityResult =====");
		if (CmmobiSnsLib.getInstance(this).mSsoHandler != null) {
			CmmobiSnsLib.getInstance(this).mSsoHandler.authorizeCallBack(
					requestCode, resultCode, data);
		}
	}
	
	
	/**
	 * 绑定完成后保存
	 */
	private void saveAccount(){
		ActiveAccount.getInstance(this).persist();
		String UID = ActiveAccount.getInstance(this).getUID();
		if (UID != null) {
			AccountInfo.getInstance(UID).persist();
		}
	}
	
	private void updateDiary(MyDiary diary)
	{
		if (isMyself()) {
			INetworkTask t = NetworkTaskManager.getInstance(userID).findTaskByUUID(diary.diaryuuid);
			if (t != null)
			{
				NetworkTaskManager.getInstance(userID).DoShare(t);
			}
			// 开始任务队列
			Prompt.Alert("已加入上传队列");
		}
	}
	
	public static interface ShareMission
	{
		public void doShare();
	}
	
	// 提示网络警告 并返回是否弹出提示了
	private boolean showNetworkWarning(final ShareMission mission)
	{
		if(isMyself() && diaryGroup.size() == 1)
		{
			final MyDiary diary = diaryGroup.get(0);
			final INetworkTask t = NetworkTaskManager.getInstance(userID).findTaskByUUID(diary.diaryuuid);
			LoginSettingManager lsm = AccountInfo.getInstance(userID).setmanager;
			if(!diary.isSychorized() && ZNetworkStateDetector.isConnected()
					&& !ZNetworkStateDetector.isWifi()
					&& SettingFragment.SYNC_TYPE_WIFI.equals(lsm.getSync_type())
					&& t != null
					&& t.getState() == INetworkTask.STATE_PAUSED)
			{
				new Xdialog.Builder(this)
				.setMessage(getString(R.string.share_error_nowifi) + ZByteToSize.smartSize(NetworkTaskManager.getInstance(userID).getUploadRemainSize(t)) + getString(R.string.share_error_flow))
				.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener()
				{
					
					@Override
					public void onClick(DialogInterface dialog, int which)
					{
						updateDiary(diary);
						mission.doShare();
					}
				})
				.setNegativeButton(android.R.string.cancel, null)
				.create().show();
				return true;
			}
			else if(t != null && t.getState() == INetworkTask.STATE_PAUSED)
			{
				updateDiary(diary);
			}
		}
		return false;
	}
	
}
