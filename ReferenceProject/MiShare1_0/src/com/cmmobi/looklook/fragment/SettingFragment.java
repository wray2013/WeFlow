package com.cmmobi.looklook.fragment;

import java.util.ArrayList;

import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.provider.ContactsContract;
import android.support.v4.content.LocalBroadcastManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import cn.zipper.framwork.core.ZDialog;

import com.cmmobi.looklook.R;
import com.cmmobi.looklook.activity.AboutActivity;
import com.cmmobi.looklook.activity.FeedbackActivity;
import com.cmmobi.looklook.activity.GuideActivity;
import com.cmmobi.looklook.activity.SettingGesturePwdActivity;
import com.cmmobi.looklook.activity.SettingPersonalInfoActivity;
import com.cmmobi.looklook.activity.SettingToCreateGestureActivity;
import com.cmmobi.looklook.activity.UnBindingMobileActivity;
import com.cmmobi.looklook.activity.login.BindingMobileNoActivity;
import com.cmmobi.looklook.activity.login.MicShareUserLoginActivity;
import com.cmmobi.looklook.common.constant.Constant;
import com.cmmobi.looklook.common.gson.GsonRequest3.AddrBook;
import com.cmmobi.looklook.common.gson.GsonResponse3;
import com.cmmobi.looklook.common.gson.GsonResponse3.MyBind;
import com.cmmobi.looklook.common.gson.Requester3;
import com.cmmobi.looklook.common.utils.CmmobiClickAgentWrapper;
import com.cmmobi.looklook.common.utils.DensityUtil;
import com.cmmobi.looklook.common.view.Xdialog;
import com.cmmobi.looklook.info.profile.AccountInfo;
import com.cmmobi.looklook.info.profile.ActiveAccount;
import com.cmmobi.looklook.info.profile.DiaryManager;
import com.cmmobi.looklook.info.profile.LoginSettingManager;
import com.cmmobi.looklook.info.profile.MediaMapping;
import com.cmmobi.looklook.info.profile.WrapUser;
import com.cmmobi.looklook.prompt.Prompt;
import com.cmmobi.sns.api.CmmobiSnsLib;
//import com.cmmobi.looklook.common.service.CommonService;

/**
 * 设置界面
 * 
 * @author youtian
 * 
 */

public class SettingFragment extends TitleRootFragment {

	//private ImageView iv_back;

	// sync_type setting
	private TextView tv_type_wifi;
	private TextView tv_type_any;
	
	// contact_type setting
	private TextView tv_type_auto;
	private TextView tv_type_manual;
	
	private String sync_type;
	private String accept_friend_type;
	public static final String SYNC_TYPE_WIFI = "1";
	public static final String SYNC_TYPE_ANY = "2";
	
	public static final String ACCEPT_FRIEND_AUTO = "1";
	public static final String ACCEPT_FRIEND_MANUAL = "2";
	
	public static final String OPERATE_UPDATE = "1";
	public static final String OPERATE_FRIEND = "2";
	Drawable drawableCheck;
	Drawable drawableNotCheck;

	private static final String TAG = "SettingActivity";

	// 手机账号绑定
	private RelativeLayout rl_phonebind;
	private TextView tv_phonebindstate;
	private MyBind phonebindstate = null;
	
	// 账号设置
	private RelativeLayout rl_personalinfo;
	private RelativeLayout rl_safebox;
	
	private Button btn_cleanCache;
	
    private RelativeLayout rl_help;
	private RelativeLayout rl_about;
	private RelativeLayout rl_feedback;
	private Button btn_exit;


	private LocalBroadcastManager lbc;
	private ActiveAccount activeAccount;
	private static AccountInfo accountInfo;
	private LoginSettingManager lsm;
	private Context context;

	private BroadcastReceiver mBroadcastReceiver_exit;

	private final int REQUEST_PRIVACY = 0x12341;
	private long beforeSize = 0;

	public static final String BROADCAST_PRIVACY_CHANGED = "PRIVACY_CHANGED";
	private static final String[] PHONES_PROJECTION = new String[]{ContactsContract.Contacts._ID, ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME, ContactsContract.CommonDataKinds.Phone.NUMBER};

	MyBind myBind;


	// for UmengclickAgentWrapper
/*	private long begin = 0;
	private long end_sup_sha_bin = 0; // 绑定结束的时间点
	private long end_sup_sha = 0; // 绑定离开第三方页面的时间点	
*/	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View view = super.onCreateView(inflater, container, savedInstanceState);
		setTitle(getResources().getString(R.string.setting));
		hideLeftButton();
		showRightButton();
		context = this.getActivity();

		// back
		/*iv_back = (ImageView) view.findViewById(R.id.iv_back);
		iv_back.setOnClickListener(this);*/


		// sync_type setting
		tv_type_wifi = (TextView) view.findViewById(R.id.tv_type_wifi);
		tv_type_any = (TextView) view.findViewById(R.id.tv_type_any);
		tv_type_wifi.setOnClickListener(this);
		tv_type_any.setOnClickListener(this);
		
		tv_type_auto = (TextView) view.findViewById(R.id.tv_type_auto);
		tv_type_manual = (TextView) view.findViewById(R.id.tv_type_manual);
		tv_type_auto.setOnClickListener(this);
		tv_type_manual.setOnClickListener(this);
		
		drawableCheck = getResources().getDrawable(R.drawable.xuanzhong_2); 
		drawableNotCheck = getResources().getDrawable(R.drawable.xuanzhong_1);
		drawableCheck.setBounds(0, 0, DensityUtil.dip2px(context, 35), DensityUtil.dip2px(context, 35));
		drawableNotCheck.setBounds(0, 0, DensityUtil.dip2px(context, 35), DensityUtil.dip2px(context, 35));
		
		// 手机账号绑定
		rl_phonebind = (RelativeLayout) view.findViewById(R.id.rl_phonebind);
		rl_phonebind.setOnClickListener(this);
		tv_phonebindstate = (TextView) view.findViewById(R.id.tv_phonebindstate);

		// 账号设置
		rl_personalinfo = (RelativeLayout) view.findViewById(R.id.rl_personalinfo);
		rl_personalinfo.setOnClickListener(this);
		rl_safebox = (RelativeLayout) view.findViewById(R.id.rl_safebox);
		rl_safebox.setOnClickListener(this);
		btn_cleanCache = (Button) view.findViewById(R.id.btn_cleancache);
		btn_cleanCache.setOnClickListener(this);
		
		//帮助
		rl_help = (RelativeLayout) view.findViewById(R.id.rl_help);
		rl_help.setOnClickListener(this);
		
		// about
		rl_about = (RelativeLayout) view.findViewById(R.id.rl_about);
		rl_about.setOnClickListener(this);

		//feedback
		rl_feedback = (RelativeLayout) view.findViewById(R.id.rl_feedback);
		rl_feedback.setOnClickListener(this);
		
		// exit
		btn_exit = (Button) view.findViewById(R.id.btn_exit);
		btn_exit.setOnClickListener(this);
		
		lbc = LocalBroadcastManager.getInstance(context);
		return view;
	}

	@Override
	public void onResume() {
		super.onResume();
		activeAccount = ActiveAccount.getInstance(context);
		String userID = activeAccount.getUID();
		accountInfo = AccountInfo.getInstance(userID);
		if (accountInfo != null) {
			lsm = accountInfo.setmanager;
			// 手机账号绑定
			phonebindstate = lsm.getBinding_type(
					LoginSettingManager.BINDING_TYPE_PHONE,
					LoginSettingManager.BINDING_INFO_POINTLESS);
			if (phonebindstate != null && phonebindstate.binding_info != null) {
				tv_phonebindstate.setText(phonebindstate.binding_info + " 已绑定");
//				btn_exit.setVisibility(View.VISIBLE);
			} else {
//				btn_exit.setVisibility(View.GONE);
			}
			
			// sync_type setting
			sync_type = lsm.getSync_type();
			if (sync_type != null && !sync_type.equals("")) {
				if (sync_type.equals(SYNC_TYPE_WIFI)) {	 
					tv_type_wifi.setCompoundDrawables(null, null, null, drawableCheck);
					tv_type_any.setCompoundDrawables(null, null, null, drawableNotCheck);
				} else if (sync_type.equals(SYNC_TYPE_ANY)) {
					tv_type_any.setCompoundDrawables(null, null, null, drawableCheck);
					tv_type_wifi.setCompoundDrawables(null, null, null, drawableNotCheck);
				}
			} else {
				sync_type = SYNC_TYPE_WIFI;
				tv_type_wifi.setCompoundDrawables(null, null, null, drawableCheck);
				tv_type_any.setCompoundDrawables(null, null, null, drawableNotCheck);
			}
			
			accept_friend_type = lsm.getAcceptFriendType();
			if (accept_friend_type != null && !accept_friend_type.equals("")) {
				if (accept_friend_type.equals(ACCEPT_FRIEND_AUTO)) {	 
					tv_type_auto.setCompoundDrawables(null, null, null, drawableCheck);
					tv_type_manual.setCompoundDrawables(null, null, null, drawableNotCheck);
				} else if (accept_friend_type.equals(ACCEPT_FRIEND_MANUAL)) {
					tv_type_manual.setCompoundDrawables(null, null, null, drawableCheck);
					tv_type_auto.setCompoundDrawables(null, null, null, drawableNotCheck);
				}
			} else {
				accept_friend_type = ACCEPT_FRIEND_AUTO;
				tv_type_auto.setCompoundDrawables(null, null, null, drawableCheck);
				tv_type_manual.setCompoundDrawables(null, null, null, drawableNotCheck);
			}
		}
		
	}

	@Override
	public void onPause() {
		super.onPause();
	}

	@Override
	public void onStop() {
		super.onStop();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_title_right:
			showMenu();
			break;
		case R.id.tv_type_wifi:
			sync_type = SYNC_TYPE_WIFI;
			if (!sync_type.equals(lsm.getSync_type())) {
				ZDialog.show(R.layout.progressdialog, false, true, context);
				Requester3.setPrivacy(handler,sync_type);
			}
			// 2014-4-8
			CmmobiClickAgentWrapper.onEvent(context,
					"pa_and_sh", "1");
			break;
		case R.id.tv_type_any:
			sync_type = SYNC_TYPE_ANY;
			// 2014-4-8
			CmmobiClickAgentWrapper.onEvent(context,
					"pa_and_sh", "2");
			if (!sync_type.equals(lsm.getSync_type())) {
				new Xdialog.Builder(context)
				.setMessage("网络状态改为“任何网络”，可能会消耗您的手机流量，是否继续？")
				.setPositiveButton("是", new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						ZDialog.show(R.layout.progressdialog, false, true, context);
						Requester3.setPrivacy(handler,sync_type);
					}
				})
				.setNegativeButton("否", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface arg0, int arg1) {
						// TODO Auto-generated method stub
						sync_type = SYNC_TYPE_WIFI;
					}
				})
				.create().show();
				
			}
			break;
		case R.id.tv_type_auto:
			accept_friend_type = ACCEPT_FRIEND_AUTO;
			if (!accept_friend_type.equals(lsm.getAcceptFriendType())){
				ZDialog.show(R.layout.progressdialog, false, true, context);
				Requester3.setAutoFriend(handler, accept_friend_type);
			}
			break;
		case R.id.tv_type_manual:
			accept_friend_type = ACCEPT_FRIEND_MANUAL;
			if (!accept_friend_type.equals(lsm.getAcceptFriendType())){
				ZDialog.show(R.layout.progressdialog, false, true, context);
				Requester3.setAutoFriend(handler, accept_friend_type);
			}
			break;
		case R.id.rl_phonebind:
			if (phonebindstate != null
					&& phonebindstate.binding_info != null) {
//				Prompt.Dialog(context, false, "提示", "抱歉，当前登录的账号不可解绑定", null);
				showContactUpload(context, UnBindingMobileActivity.class, handler,null);
			} else {
				showContactUpload(context, BindingMobileNoActivity.class, handler,null);
			}
			break;
		case R.id.rl_personalinfo:
			Intent personalinfo = new Intent(context,
					SettingPersonalInfoActivity.class);
			startActivity(personalinfo);
			break;
		case R.id.rl_safebox:
			lsm.setIsFromSetting(true);
			Intent safebox;
			if(lsm.getSafeIsOn()){
				safebox = new Intent(context, SettingGesturePwdActivity.class);
				safebox.putExtra("count", 0);
				startActivity(safebox);
			}else{
				safebox = new Intent(context, SettingToCreateGestureActivity.class);
				startActivity(safebox);
			}
			break;
		case R.id.btn_cleancache:
			CmmobiClickAgentWrapper.onEvent(SettingFragment.this.getActivity(), "clear_cache");
			ZDialog.show(R.layout.progressdialog, false,
					true, context);
			//MediaMapping mm = new MediaMapping();
			beforeSize = accountInfo.mediamapping.getTotalSizeBySync(accountInfo.userid, -1);
			accountInfo.mediamapping.cleanSyncMsgMedia(handler, accountInfo.userid, -1, 0);
			break;
		case R.id.rl_about:
			CmmobiClickAgentWrapper.onEvent(SettingFragment.this.getActivity(), "about_look");
			Intent intent = new Intent(context, AboutActivity.class);
			startActivity(intent);
			break;
		case R.id.rl_feedback:
			Intent intentFeedback = new Intent(context, FeedbackActivity.class);
			startActivity(intentFeedback);
			break;
		case R.id.rl_help:
			// 2014-4-8
			CmmobiClickAgentWrapper.onEvent(SettingFragment.this.getActivity(), "help");
			Intent intentHelp = new Intent(context, GuideActivity.class);
			intentHelp.putExtra(GuideActivity.FROM_SETTING, true);
			startActivity(intentHelp);
			break;
		case R.id.btn_exit:
			
			/*Prompt.Dialog(context, true, "提示", "退出当前账号",
					new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							// TODO Auto-generated method stub
							CmmobiClickAgentWrapper.onEvent(SettingFragment.this.getActivity(), "log_out");
							if(accountInfo.vshareDataEntities.getCache().size()>30){
								for(int i=accountInfo.vshareDataEntities.getCache().size()-1; i>29; i--){
									accountInfo.vshareDataEntities.getCache().remove(i);
								}	
							}
							accountInfo.persist();
						    try {
						    	ActiveAccount currentaccount = ActiveAccount.getInstance(context);
								NetworkTaskManager.getInstance(currentaccount.getLookLookID()).shutdown();
								currentaccount.logout();
							} catch (Exception e) {
								// TODO: handle exception
							}
							MainApplication.getAppInstance().cleanAllActivity();
							Intent intent = new Intent(LookLookActivity.FLAG_CLOSE_ACTIVITY);
							LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
							Intent in = new Intent(context, LoginLooklookActivity.class);
							startActivity(in);
						    getActivity().finish();
//							JPushInterface.stopPush(MainApplication.getAppInstance());
						}
					});*/
			
			context.startActivity(new Intent(context,MicShareUserLoginActivity.class));
			break;
		default:
			break;
		}
	}
	
	public static void showContactUpload(final Context context, final Class<?> cls,final Handler handler,final String isFromPrompt) {
		showContactUpload(context, cls, handler, isFromPrompt,false);
	}
	
	public static void showContactUpload(final Context context, final Class<?> cls,final Handler handler,final String isFromPrompt,final boolean isFinish) {
		new Xdialog.Builder(context)
		.setMessage("需要访问通讯录")
		.setPositiveButton("访问", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				new Thread(new Runnable() {
					
					@Override
					public void run() {
						// TODO Auto-generated method stub
						AccountInfo ac = AccountInfo.getInstance(ActiveAccount.getInstance(null).getUID());
						ac.isPhonePromt = true;
						Looper.prepare();
						getContactInfo(context,handler);
					}
				}).start();
				Intent phonebind = new Intent(context,cls);
				if (isFromPrompt != null) {
					phonebind.putExtra(MyZoneFragment.EXTRA_FROM_PROMPT, isFromPrompt);
				}
				
				if (isFinish) {
					phonebind.putExtra("isfinish", true);
				}
				context.startActivity(phonebind);
			}
		})
		.setNegativeButton("只绑定手机号", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				Intent phonebind = new Intent(context,cls);
				if (isFromPrompt != null) {
					phonebind.putExtra(MyZoneFragment.EXTRA_FROM_PROMPT, isFromPrompt);
				}
				if (isFinish) {
					phonebind.putExtra("isfinish", true);
				}
				context.startActivity(phonebind);
			}
		})
		.create().show();
	}
	
	public static void getContactInfo(Context context,Handler handler) {
		ArrayList<AddrBook> addrBooks = new ArrayList<AddrBook>();
		try {
			// 从本机中取号
			// 得到ContentResolver对象
			ContentResolver cr = context.getContentResolver();
			Cursor cursor = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, PHONES_PROJECTION, null, null,
					null);
			int count=0;
			while (cursor.moveToNext()) {
				count++;
				// 获取联系人姓名在表的中列的位置
				int phoneName = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME);
				// 获取联系人号码在表的中列的位置
				int phoneNumber = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
				// 取得联系人名字
				String contactName = cursor.getString(phoneName).trim();
				String contactNumber = cursor.getString(phoneNumber).trim();

				// 如果缺省为null 跳出当前循环
				if (contactNumber == null) {
					continue ;
				}
				if (contactName == null ) {
					continue ;
				}

				String phoneRegex = getNumber(contactNumber);
				if (phoneRegex == null || phoneRegex.equals("")) continue ;
				
	  			AddrBook oneAddrBook = new AddrBook();
				oneAddrBook.phone_name = contactName;
				oneAddrBook.phone_num = phoneRegex;
				if(!addrBooks.contains(oneAddrBook)){
					addrBooks.add(oneAddrBook);
				}else{
					for(int i=0; i< addrBooks.size();i++){
						if(addrBooks.get(i).phone_name.equals(oneAddrBook.phone_name)){
							addrBooks.get(i).phone_num = addrBooks.get(i).phone_num + "," + oneAddrBook.phone_num;
							break;
						}
					}
				}
			}			
			
			cursor.close();
			if(addrBooks.isEmpty()){
				/*this.runOnUiThread(new Runnable() {
					
					@Override
					public void run() {
						// TODO Auto-generated method stub
						Prompt.Dialog(ShareLookLookFriendsActivity.this, false, "提示", "通讯录里没有有效的手机号码",null);
					}
				});*/
				Requester3.phoneBook(handler);
			}else{
				AddrBook[] addrBooksArray;
				addrBooksArray = addrBooks.toArray(new AddrBook[addrBooks.size()]);
				Requester3.postAddressBook(handler,"", addrBooksArray);
			}
		} catch (Exception e) {
			e.printStackTrace();// TODO just for debug
		}
	}

	//还原11位手机号 包括去除“-”
		public static String getNumber(String num2) {
			String num;
			if (num2 != null) {
			
				num = num2.replaceAll("\\D", "");
//				if (num.startsWith("+86")) {
//					num = num.substring(3);
//				} else 
				if (num.startsWith("86")) {
					num = num.substring(2);
				} else if (num.startsWith("17951")) {
					num = num.substring(5);
				} else if (!num.startsWith("1") && num.length()>11) {
						num = num.substring(num.length()-11, num.length());
				}
				if(!Prompt.checkPhoneNum(num)) {
					num = "";
				}
			} else {
				num = "";
			}
			return num;
		}
	
	@Override
	public boolean handleMessage(Message msg) {
		// TODO Auto-generated method stub
		ActiveAccount acct = ActiveAccount.getInstance(context);
		CmmobiSnsLib csb = CmmobiSnsLib.getInstance(context);
		switch (msg.what) {
		case Requester3.RESPONSE_TYPE_DIARY_PRIVACY:
			accountInfo.persist();
			activeAccount.persist();
			ZDialog.dismiss();
			try {
				GsonResponse3.diaryPrivacyResponse dp = (GsonResponse3.diaryPrivacyResponse) msg.obj;
				if (dp != null) {
					if (dp.status.equals("0")) {
						if (lsm.getSync_type() == null || !lsm.getSync_type().equals(sync_type)) {
							
							if (sync_type.equals(SYNC_TYPE_WIFI)) {
								tv_type_wifi.setCompoundDrawables(null, null, null, drawableCheck);
								tv_type_any.setCompoundDrawables(null, null, null, drawableNotCheck);
								
							} else {
								tv_type_any.setCompoundDrawables(null, null, null, drawableCheck);
								tv_type_wifi.setCompoundDrawables(null, null, null, drawableNotCheck);
							}
						}

						lsm.setSync_type(sync_type);
						Intent intent = new Intent(
								SettingFragment.BROADCAST_PRIVACY_CHANGED);
						lbc.sendBroadcast(intent);
//						Prompt.Dialog(context, false, "提示", "设置更改成功", null);
						Prompt.Alert("设置更改成功");
					} else if (dp.status.equals("200600")) {
						/*Prompt.Dialog(context, false, "提示",
								Constant.CRM_STATUS[Integer
										.parseInt(dp.crm_status)], null);*/
						Prompt.Alert(Constant.CRM_STATUS[Integer
															.parseInt(dp.crm_status)]);
						
					} else {
						Prompt.Alert("操作失败，请稍后再试");
//						Prompt.Dialog(context, false, "提示", "操作失败，请稍后再试",null);
					}
				} else {
					Prompt.Alert("操作失败，网络不给力");
//					Prompt.Dialog(context, false, "提示", "操作失败，网络不给力", null);
				}
			} catch (Exception e) {
				// TODO: handle exception
			}
			sync_type = lsm.getSync_type();
			break;
		case Requester3.RESPONSE_TYPE_AUTO_FRIEND:
			accountInfo.persist();
			activeAccount.persist();
			ZDialog.dismiss();
			try {
				GsonResponse3.autoFriendResponse dp = (GsonResponse3.autoFriendResponse) msg.obj;
				if (dp != null) {
					if (dp.status.equals("0")) {
						if (lsm.getAcceptFriendType() == null || !lsm.getAcceptFriendType().equals(accept_friend_type)) {
							
							if (accept_friend_type.equals(ACCEPT_FRIEND_AUTO)) {
								tv_type_auto.setCompoundDrawables(null, null, null, drawableCheck);
								tv_type_manual.setCompoundDrawables(null, null, null, drawableNotCheck);
								
							} else {
								tv_type_manual.setCompoundDrawables(null, null, null, drawableCheck);
								tv_type_auto.setCompoundDrawables(null, null, null, drawableNotCheck);
							}
						}
						
						lsm.setAcceptFriendType(accept_friend_type);

						Prompt.Alert("设置更改成功");
					} else if (dp.status.equals("200600")) {
						Prompt.Alert(Constant.CRM_STATUS[Integer
															.parseInt(dp.crm_status)]);
					} else {
						Prompt.Alert("操作失败，请稍后再试");
					}
				} else {
					Prompt.Alert("操作失败，网络不给力");
				}
			} catch (Exception e) {
				// TODO: handle exception
			}
			accept_friend_type = lsm.getAcceptFriendType();
			break;
		case Requester3.RESPONSE_TYPE_UNSAFEBOX:
			ZDialog.dismiss();
			try {
				GsonResponse3.unSafeboxResponse res = (GsonResponse3.unSafeboxResponse) msg.obj;
				if (res != null) {
					if (res.status.equals("0")) {
						lsm.setGesturepassword(null);
						DiaryManager.getInstance().removeAllSafebox();
					} else if (res.status.equals("200600")) {
						Prompt.Alert(context, Constant.CRM_STATUS[Integer
								.parseInt(res.crm_status)]);
					} else {
						Prompt.Alert("注销保险箱失败");
					}
				} else {
					Prompt.Alert(context, "注销保险箱失败，网络不给力");
				}
			} catch (Exception e) {
				// TODO: handle exception
			}
			break;
		case Requester3.RESPONSE_TYPE_UNBIND:
			ZDialog.dismiss();
			try {
				GsonResponse3.unbindResponse gru = (GsonResponse3.unbindResponse) msg.obj;
				if (gru != null && gru.status != null) {
					//System.out.println("====== unbind ====== 1");
					if (gru.status.equals("0")) {
						if (gru.binding_type.equals(LoginSettingManager.BINDING_TYPE_PHONE)) {
							phonebindstate = null;
							tv_phonebindstate.setText(R.string.not_bind);
							lsm.deleteBindingInfo(
									LoginSettingManager.BINDING_TYPE_PHONE,
									LoginSettingManager.BINDING_INFO_POINTLESS);
							if (lsm.getGesturepassword() != null) {
								Requester3.unSafebox(handler);
							}
							lsm.deleteBindingInfo(
									LoginSettingManager.BINDING_TYPE_PHONE_SEC,
									LoginSettingManager.BINDING_INFO_POINTLESS);
						} 
						Prompt.Dialog(context, false, "提示", "解绑定成功", null);
					} else if (gru.status.equals("200600")) {
						Prompt.Dialog(context, false, "提示",
								Constant.CRM_STATUS[Integer
										.parseInt(gru.crm_status)], null);
					} else {
						Prompt.Dialog(context, false, "提示", "操作失败，请稍后再试", null);
					}
				} else {
					Prompt.Dialog(context, false, "提示", "操作失败，网络不给力", null);
				}
				
				saveAccount();
				
			} catch (Exception e) {
				// TODO: handle exception
			}
			break;
		case MediaMapping.HANDLER_FLAG_SYNC_CLEANUP:
			ZDialog.dismiss();
			long afterSize = accountInfo.mediamapping.getTotalSizeBySync(accountInfo.userid, -1);
			long clearSize = beforeSize - afterSize;
			String tipClearSize;
			if (clearSize / 1024 /1024 > 0) {
				tipClearSize = clearSize / 1024 / 1024 + " M";
			} else {
				tipClearSize = clearSize / 1024 + " K";
			}
			Prompt.Dialog(context, false, "提示", "此次清理为您节省了" + tipClearSize +  "的空间",null);
			break;
		case MediaMapping.HANDLER_FLAG_LOCAL_CLEANUP:
			ZDialog.dismiss();
			break;
		case Requester3.RESPONSE_TYPE_POST_ADDRESSBOOK:
			ZDialog.dismiss();
			if(msg.obj != null){
				GsonResponse3.postAddressBookResponse response = (GsonResponse3.postAddressBookResponse)msg.obj;
				if(response.status!= null && response.status.equals("0")){
					Requester3.phoneBook(handler);
				}else{
					if (response != null
							&& response.status.equals("200600")) {
						Prompt.Dialog(getActivity(), false, "提示",
								Constant.CRM_STATUS[Integer
										.parseInt(response.crm_status)], null);
					} else {
						Prompt.Dialog(getActivity(), false, "提示", "操作失败，请稍后再试", null);
					}
				}
			}else{
				Prompt.Dialog(getActivity(), false, "提示", "网络不给力",null);
			}
			break;
		case Requester3.RESPONSE_TYPE_PHONE_BOOK:
			ZDialog.dismiss();
			if(msg.obj != null){
				accountInfo.phoneUsers.clearList();
				ArrayList<WrapUser> wrapUsersPhone = new ArrayList<WrapUser>();
				GsonResponse3.phoneBookResponse response = (GsonResponse3.phoneBookResponse) msg.obj;
				if(response!=null && response.status!=null && response.status.equals("0")){
					for(int i=0; i< response.users.length; i++){
						wrapUsersPhone.add(response.users[i]);
					}
					accountInfo.phoneUsers.addMembers(wrapUsersPhone);
				}else{
					if (response != null
							&& response.status.equals("200600")) {
						Prompt.Dialog(getActivity(), false, "提示",
								Constant.CRM_STATUS[Integer
										.parseInt(response.crm_status)],
								null);
					} else {
						Prompt.Dialog(getActivity(), false, "提示", "操作失败，请稍后再试", null);
					}
				}
			}
			break;
			default:
				break;
		}

		return false;
	}

	@Override
	public int subContentViewId() {
		// TODO Auto-generated method stub
		return R.layout.activity_setting_main;
	}

	
}
