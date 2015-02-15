package com.cmmobi.looklook.activity;

import java.util.ArrayList;

import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Looper;
import android.os.Message;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import cn.zipper.framwork.core.ZActivity;
import cn.zipper.framwork.core.ZApplication;
import cn.zipper.framwork.core.ZDialog;
import cn.zipper.framwork.core.ZViewFinder;

import com.cmmobi.looklook.R;
import com.cmmobi.looklook.common.adapter.FriendListAdapter;
import com.cmmobi.looklook.common.adapter.FriendsAddAdapter;
import com.cmmobi.looklook.common.constant.Constant;
import com.cmmobi.looklook.common.gson.GsonRequest2;
import com.cmmobi.looklook.common.gson.GsonRequest2.AddrBook;
import com.cmmobi.looklook.common.gson.GsonResponse2;
import com.cmmobi.looklook.common.gson.GsonResponse2.MyBind;
import com.cmmobi.looklook.common.gson.GsonResponse2.User;
import com.cmmobi.looklook.common.gson.GsonResponse2.listThirdPlatformUserItem;
import com.cmmobi.looklook.common.gson.GsonResponse2.listThirdPlatformUserResponse;
import com.cmmobi.looklook.common.gson.Requester2;
import com.cmmobi.looklook.common.gson.WeiboRequester;
import com.cmmobi.looklook.common.gson.WeiboResponse.RWUserInfo;
import com.cmmobi.looklook.common.gson.WeiboResponse.SWUserInfo;
import com.cmmobi.looklook.common.gson.WeiboResponse.TWUserInfo;
import com.cmmobi.looklook.common.utils.CmmobiClickAgentWrapper;
import com.cmmobi.looklook.common.utils.UmengclickAgentWrapper;
import com.cmmobi.looklook.fragment.WrapRecommendUser;
import com.cmmobi.looklook.info.profile.AccountInfo;
import com.cmmobi.looklook.info.profile.ActiveAccount;
import com.cmmobi.looklook.info.profile.FriendsAddManager;
import com.cmmobi.looklook.info.profile.LoginSettingManager;
import com.cmmobi.looklook.prompt.Prompt;
import com.cmmobi.sns.api.CmmobiSnsLib;
import com.cmmobi.sns.oauthv2.OAuthV2;
import com.cmmobi.sns.utils.ConfigUtil.SHARE_TO;
import com.cmmobi.sns.utils.PinYinUtil;
import com.weibo.sdk.android.api.WeiboAuthListener;
import com.weibo.sdk.android.api.WeiboDialogError;
import com.weibo.sdk.android.api.WeiboException;

public class FriendAddActivity extends ZActivity implements
		OnItemClickListener, OnScrollListener {
	private static final String TAG = "FriendAddActivity";
	//private ListView lv_weibo;
	private ListView lv_lookfriends;
	//private String[] weibo_type;
	// WeiboListAdapter weibo_adapter;
	FriendListAdapter list_friend_adapter;
	private FriendsAddAdapter friendsAddAdapter;
	private View header;
	private EditText searchEditText;

	private TextView section;

	//private ArrayList<User> list_friend_data;
//	private ListView realListView;

	private RelativeLayout sinaAdd;
	private RelativeLayout tencentAdd;
	private RelativeLayout renrenAdd;
	private RelativeLayout phoneAdd;
	private RelativeLayout recommendAdd;

	//private FriendsAddManager friendsAddManager;
	//private Object searchLock = new Object();

/*	private int pageno = 1;
	private boolean allShow;*/
	//private ArrayList<WrapRecommendUser> wrapUserList = new ArrayList<WrapRecommendUser>();
	//private ArrayList<WrapRecommendUser> filterUserList = new ArrayList<WrapRecommendUser>();

	private final int HANDLER_SINA_AUTHOR_SUCCESS = 1;
	private final int HANDLER_RENREN_AUTHOR_SUCCESS = 2;
	private final int HANDLER_TENCENT_AUTHOR_SUCCESS = 6;

	private final int REQUEST_CODE = 0x87652291;
	
	MyBind myBind;
	private LoginSettingManager lsm;
	
	private WeiboAuthListener sinalistener = new WeiboAuthListener() {

		@Override
		public void onComplete(int weiboIndex) {
			//Prompt.Alert("sina授权成功");
			Message message = getHandler().obtainMessage(
					HANDLER_SINA_AUTHOR_SUCCESS);
			getHandler().sendMessage(message);

		}

		@Override
		public void onCancel(int arg0) {
			// TODO Auto-generated method stub
			Prompt.Alert("sina授权取消");
		}

		@Override
		public void onError(WeiboDialogError arg0, int arg1) {
			// TODO Auto-generated method stub
			Prompt.Alert("sina授权错误");
		}

		@Override
		public void onWeiboException(WeiboException arg0, int arg1) {
			// TODO Auto-generated method stub
			// ZToast.showShort("sina授权异常！");
			Prompt.Alert("sina授权异常");
		}

	};

	private WeiboAuthListener renrenlistener = new WeiboAuthListener() {

		@Override
		public void onComplete(int weiboIndex) {
			//Prompt.Alert("renren授权成功");
			Message message = getHandler().obtainMessage(
					HANDLER_RENREN_AUTHOR_SUCCESS);
			getHandler().sendMessage(message);

		}

		@Override
		public void onCancel(int arg0) {
			Prompt.Alert("renren授权取消");
		}

		@Override
		public void onError(WeiboDialogError arg0, int arg1) {
			// TODO Auto-generated method stub
			Prompt.Alert("renren授权错误");
		}

		@Override
		public void onWeiboException(WeiboException arg0, int arg1) {
			// TODO Auto-generated method stub
			// ZToast.showShort("sina授权异常！");
			Prompt.Alert("renren授权异常");
		}

	};

	private WeiboAuthListener tencentlistener = new WeiboAuthListener() {

		@Override
		public void onComplete(int weiboIndex) {
			// ZToast.showShort("tencent授权成功！");
			//Prompt.Alert("登陆成功");

			Message message = getHandler().obtainMessage(
					HANDLER_TENCENT_AUTHOR_SUCCESS);
			getHandler().sendMessage(message);

		}

		@Override
		public void onCancel(int arg0) {
			// TODO Auto-generated method stub
			Prompt.Alert("tencent授权取消！");
		}

		@Override
		public void onError(WeiboDialogError arg0, int arg1) {
			// TODO Auto-generated method stub
			Prompt.Alert("tencent授权错误！");
		}

		@Override
		public void onWeiboException(WeiboException arg0, int arg1) {
			// TODO Auto-generated method stub
			Prompt.Alert("tencent授权异常！");
		}

	};
	//private listThirdPlatformUserItem[] userList;
	private String userID;
	private AccountInfo accountInfo;
	//private listThirdPlatformUserResponse thirdUserList;
	protected String searchString;
	//protected SearchTask curSearchTask;

	@Override
	public boolean handleMessage(Message msg) {
		// TODO Auto-generated method stub
		Intent intent;
		Bundle mBundle;
		ActiveAccount acct = ActiveAccount.getInstance(this);
		CmmobiSnsLib csb = CmmobiSnsLib.getInstance(this);
		switch (msg.what) {
		case Constant.HANDLER_FLAG_LISTVIEW_UPDATE:
			list_friend_adapter.notifyDataSetChanged();
			break;
		case HANDLER_SINA_AUTHOR_SUCCESS:
			ZDialog.show(R.layout.progressdialog, false, true, this);
			String sina_snsid = ActiveAccount.getInstance(this).getSNSID(SHARE_TO.SINA.ordinal());
			if(sina_snsid!=null){
				WeiboRequester.getSinaAccountInfo(getHandler(), sina_snsid);
			}else{
				ZDialog.dismiss();
				Prompt.Alert(this, "新浪授权微博异常");
			}
			break;
/*			intent = new Intent(this, SinaFriendWeiboActivity.class);
			mBundle = new Bundle();
			mBundle.putString("weibo_type", "sina");
			intent.putExtras(mBundle);
			startActivity(intent);
			break;*/
		case HANDLER_RENREN_AUTHOR_SUCCESS:
			ZDialog.show(R.layout.progressdialog, false, true, this);
			String renren_snsid = ActiveAccount.getInstance(this).getSNSID(SHARE_TO.RENREN.ordinal());
			if(renren_snsid != null){
				WeiboRequester.getRenrenAccountInfo(getHandler(), renren_snsid);
			}else{
				ZDialog.dismiss();
				Prompt.Alert(this, "人人授权微博异常");
			}
			break;
/*			intent = new Intent(this, RenrenFriendWeiboActivity.class);
			mBundle = new Bundle();
			mBundle.putString("weibo_type", "renren");
			intent.putExtras(mBundle);
			startActivity(intent);
			break;*/
		case HANDLER_TENCENT_AUTHOR_SUCCESS:
			ZDialog.show(R.layout.progressdialog, false, true, this);
			String tencent_snsid = ActiveAccount.getInstance(this).getSNSID(SHARE_TO.TENC.ordinal());
			if(tencent_snsid != null){
				WeiboRequester.getTencentAccountInfo(getHandler(), tencent_snsid);
			}else{
				ZDialog.dismiss();
				Prompt.Alert(this, "腾讯授权微博异常");
			}
			break;
/*			intent = new Intent(this, TencentFriendWeiboActivity.class);
			mBundle = new Bundle();
			mBundle.putString("weibo_type", "tencent");
			intent.putExtras(mBundle);
			startActivity(intent);
			break;*/
		case WeiboRequester.SINA_INTERFACE_GET_ACCOUNTINFO:
			SWUserInfo sn_object  = (SWUserInfo) msg.obj;
			if(sn_object==null || sn_object.screen_name==null || !acct.updateSinaAuthor(sn_object)){
				ZDialog.dismiss();
				Prompt.Alert(this, "获取新浪微博个人信息异常");
				accountInfo.removeAccessToken("3", "3", "1");
				CmmobiSnsLib.getInstance(FriendAddActivity.this).removeOauth(SHARE_TO.SINA.ordinal());
				
			}else{
				Log.e(TAG, "sina getaccountinfo ok, binding..");
				OAuthV2 sina_oa = csb.acquireSinaOauth();
				if (sina_oa != null) {
					Requester2.bindAccount(getHandler(), "3", null, null,
							acct.snsid, "1", null, acct.sex, acct.address,
							acct.birthdate, sina_oa.getAccessToken(),
							sina_oa.getExpiresIn(), sina_oa.getExpiresTime(),
							sina_oa.getRefreshToken(), sina_oa.getNick(),
							sina_oa.getOpenkey(),sina_oa.getNick());
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
				}else {
					accountInfo.removeAccessToken("3", "3", "1");
					CmmobiSnsLib.getInstance(FriendAddActivity.this).removeOauth(SHARE_TO.SINA.ordinal());		
				}
			}
			break;
		case WeiboRequester.RENREN_INTERFACE_GET_ACCOUNTINFO:
			RWUserInfo sn_renren  = (RWUserInfo) msg.obj;
			if(sn_renren==null || sn_renren.response==null ||  !acct.updateRenrenAuthor(sn_renren)){
				ZDialog.dismiss();
				Prompt.Alert(this, "获取人人微博个人信息异常");	
				accountInfo.removeAccessToken("3", "3", "2");
				CmmobiSnsLib.getInstance(FriendAddActivity.this).removeOauth(SHARE_TO.RENREN.ordinal());
			}else{
				Log.e(TAG, "renren getaccountinfo ok, binding..");
				OAuthV2 renren_oa = csb.acquireRenrenOauth();
				if (renren_oa != null) {
					Requester2.bindAccount(getHandler(), "3", null, null,
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
					CmmobiSnsLib.getInstance(FriendAddActivity.this).removeOauth(SHARE_TO.RENREN.ordinal());
					
				}
				
			}
			break;
		case WeiboRequester.TENCENT_INTERFACE_GET_ACCOUNTINFO:
			TWUserInfo sn_tencent  = (TWUserInfo) msg.obj;
			if(sn_tencent==null || sn_tencent.data==null || !acct.updateTencentAuthor(sn_tencent)){
				ZDialog.dismiss();
				Prompt.Alert(this, "获取腾讯微博个人信息异常");
				accountInfo.removeAccessToken("3", "3", "6");
				CmmobiSnsLib.getInstance(FriendAddActivity.this).removeOauth(SHARE_TO.TENC.ordinal());
				
			}else{
				Log.e(TAG, "tencent getaccountinfo ok, binding..");
				OAuthV2 tencent_oa = csb.acquireTencentWeiboOauth();
				if (tencent_oa != null) {
					Requester2.bindAccount(getHandler(), "3", null, null,
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
						CmmobiSnsLib.getInstance(FriendAddActivity.this).removeOauth(SHARE_TO.TENC.ordinal());
					}
			}
			break;
		case Requester2.RESPONSE_TYPE_BINDING:
			ZDialog.dismiss();
			GsonResponse2.bindingResponse bind_response = (GsonResponse2.bindingResponse) msg.obj;
			if(bind_response!=null && bind_response.status!=null && bind_response.status.equals("0")){
				if(bind_response.binding_type.equals(LoginSettingManager.BINDING_REQUEST_TYPE_SNS)){
					if(bind_response.snstype.equals(LoginSettingManager.BINDING_SNS_TYPE_SINA)){
						lsm.addBindingInfo(myBind);
						intent = new Intent(this, SinaFriendWeiboActivity.class);
						mBundle = new Bundle();
						mBundle.putString("weibo_type", "sina");
						intent.putExtras(mBundle);
						startActivity(intent);
					}else if(bind_response.snstype.equals(LoginSettingManager.BINDING_SNS_TYPE_RENREN)){
						lsm.addBindingInfo(myBind);
						intent = new Intent(this, RenrenFriendWeiboActivity.class);
						mBundle = new Bundle();
						mBundle.putString("weibo_type", "renren");
						intent.putExtras(mBundle);
						startActivity(intent);
					}else if(bind_response.snstype.equals(LoginSettingManager.BINDING_SNS_TYPE_TENCENT)){
						lsm.addBindingInfo(myBind);
						intent = new Intent(this, TencentFriendWeiboActivity.class);
						mBundle = new Bundle();
						mBundle.putString("weibo_type", "tencent");
						intent.putExtras(mBundle);
						startActivity(intent);
					}
				}
			}else {
				if (bind_response != null
						&& bind_response.status.equals("200600")) {
					Prompt.Dialog(this, false, "提示",
							Constant.CRM_STATUS[Integer
									.parseInt(bind_response.crm_status)],
							null);
				} else {
					Prompt.Dialog(this, false, "提示", "操作失败，请稍后再试", null);
				}
				if(bind_response.binding_type.equals(LoginSettingManager.BINDING_REQUEST_TYPE_SNS)){
					if(bind_response.snstype.equals(LoginSettingManager.BINDING_SNS_TYPE_SINA)){
						accountInfo.removeAccessToken("3", "3", "1");
						CmmobiSnsLib.getInstance(FriendAddActivity.this).removeOauth(SHARE_TO.SINA.ordinal());
					}else if(bind_response.snstype.equals(LoginSettingManager.BINDING_SNS_TYPE_RENREN)){
						accountInfo.removeAccessToken("3", "3", "2");
						CmmobiSnsLib.getInstance(FriendAddActivity.this).removeOauth(SHARE_TO.RENREN.ordinal());
					}else if(bind_response.snstype.equals(LoginSettingManager.BINDING_SNS_TYPE_TENCENT)){
						accountInfo.removeAccessToken("3", "3", "6");
						CmmobiSnsLib.getInstance(FriendAddActivity.this).removeOauth(SHARE_TO.TENC.ordinal());

					}
				}
			}			
			break;
		case Requester2.RESPONSE_TYPE_LIST_USER_SNS:
			if(msg.obj != null){
				GsonResponse2.listUserSNSResponse lusnsResponse = (GsonResponse2.listUserSNSResponse) msg.obj;
				if(lusnsResponse!=null && lusnsResponse.status!=null && lusnsResponse.status.equals("0")){
					friendsAddAdapter.addData(lusnsResponse.users);
					friendsAddAdapter.notifyDataSetChanged();
				}else{
					if (lusnsResponse != null
							&& lusnsResponse.status.equals("200600")) {
						Prompt.Dialog(this, false, "提示",
								Constant.CRM_STATUS[Integer
										.parseInt(lusnsResponse.crm_status)],
								null);
					} else {
						Prompt.Dialog(this, false, "提示", "操作失败，请稍后再试", null);
					}
				}
			}
			break;
		case Requester2.RESPONSE_TYPE_POST_ADDRESSBOOK:
			ZDialog.dismiss();
			if(msg.obj != null){
				GsonResponse2.postAddressBookResponse response = (GsonResponse2.postAddressBookResponse)msg.obj;
				if(response.status!= null && response.status.equals("0")){
					Intent intentPhone = new Intent(FriendAddActivity.this, FriendAddPhoneActivity.class);
					startActivity(intentPhone);
				}else{
					if (response != null
							&& response.status.equals("200600")) {
						Prompt.Dialog(this, false, "提示",
								Constant.CRM_STATUS[Integer
										.parseInt(response.crm_status)],
								null);
					} else {
						Prompt.Dialog(this, false, "提示", "操作失败，请稍后再试", null);
					}
				}
			}
			break;
/*		case Requester2.RESPONSE_TYPE_LIST_THIRDPLATFORM:
			if (msg.obj != null) {
				thirdUserList = (listThirdPlatformUserResponse) msg.obj;

				if ("0".equals(thirdUserList.status)) {
					userList = thirdUserList.users;

					for (int i = 0; i < userList.length; i++) {

						WrapRecommendUser wrapUser = new WrapRecommendUser();

						wrapUser.userid = userList[i].userid;
						wrapUser.headimageurl = userList[i].headimageurl;
						wrapUser.nickname = userList[i].nickname;
						wrapUser.diarycount = userList[i].diarycount;
						wrapUser.attentioncount = userList[i].attentioncount;
						wrapUser.fanscount = userList[i].fanscount;
						wrapUser.sex = userList[i].sex;
						wrapUser.signature = userList[i].signature;
						wrapUser.isattention = userList[i].isattention;
						wrapUser.isattentionme = userList[i].isattentionme;
//						String sortKey = PinYinUtil
//								.getPinYin(userList[i].nickname);
//						wrapUser.sortKey = sortKey;

						wrapUserList.add(wrapUser);

					}
					
					if ("1".equals(thirdUserList.hasnextpage)) {
						pageno ++;
						Requester2.listRecommendUser(handler, pageno + "", "");
					} else {
						friendsAddAdapter.setData(wrapUserList);

						friendsAddAdapter.notifyDataSetChanged();
					}
					
				}
			}
			
			break;*/
		}
		return false;
	}

	@Override
	public void onClick(View v) {

		Bundle mBundle = new Bundle();

		Intent intent = null;
		switch (v.getId()) {
		case R.id.lv_activity_friends_back:
			finish();
			break;
		case R.id.btn_sina_add:
			mBundle.putString("weibo_type", "sina");// 压入数据
			if (!ActiveAccount.getInstance(this).isSNSBind("sina")) {
				launchBindWeiboDialog("sina", "您还没有绑定新浪微博" , "现在就去绑定，与好友一起互动吧");
				return;
			} else {
				intent = new Intent(this, SinaFriendWeiboActivity.class);
				// intent = new Intent(this, FriendsSNSActivity.class);
				intent.putExtras(mBundle);
				startActivity(intent);
			}
			break;
		case R.id.btn_tencent_add:
			mBundle.putString("weibo_type", "tencent");// 压入数据
			if (!ActiveAccount.getInstance(this).isSNSBind("tencent")) {
				launchBindWeiboDialog("tencent", "您还没有绑定腾讯微博" , "现在就去绑定，与好友一起互动吧");
				return;
			} else {
				intent = new Intent(this, TencentFriendWeiboActivity.class);
				intent.putExtras(mBundle);
				startActivity(intent);
			}
			break;
		case R.id.btn_renren_add:
			mBundle.putString("weibo_type", "renren");// 压入数据
			if (!ActiveAccount.getInstance(this).isSNSBind("renren")) {
				launchBindWeiboDialog("renren", "您还没有绑定人人网" , "现在就去绑定，与好友一起互动吧");
				return;
			} else {
				intent = new Intent(this, RenrenFriendWeiboActivity.class);
				intent.putExtras(mBundle);
				startActivity(intent);
			}
			break;
		case R.id.btn_phone_add:
			MyBind phonebindstate = lsm.getBinding_type(
					LoginSettingManager.BINDING_TYPE_PHONE,
					LoginSettingManager.BINDING_INFO_POINTLESS);
			if (phonebindstate == null || phonebindstate.binding_info == null) {
				Intent phonebind = new Intent(this,
						SettingBindPhoneActivity.class);
				startActivity(phonebind);
			}else{
				String msg = "为了让你更容易找到好友，looklook需要你的联系人信息访问权限，为了保障个人隐私，联系人信息只用于好友匹配";
				Prompt.Dialog(this, true, "提示", msg,
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								// TODO Auto-generated method stub
								new Thread(new Runnable() {
									
									@Override
									public void run() {
										// TODO Auto-generated method stub
										Looper.prepare();
										getContactInfo();
									}
								}).start();
							}

						});
			}
			break;
		case R.id.btn_recommend_add:
			intent = new Intent(this, FriendAddRecommendActivity.class);
			startActivity(intent);
			break;
		default:
			break;
		}
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_friends_add);

		userID = ActiveAccount.getInstance(ZApplication.getInstance()).getUID();
		accountInfo = AccountInfo.getInstance(userID);
		if (accountInfo != null) {
			lsm = accountInfo.setmanager;
		}
		//friendsAddManager = accountInfo.friendsAddManager;

		header = LayoutInflater.from(this).inflate(
				R.layout.activity_friends_add_listview_header, null);

		//allShow = false;

		//weibo_type = getResources().getStringArray(R.array.weibo_type);

		ZViewFinder finder = getZViewFinder();
		finder.setOnClickListener(R.id.lv_activity_friends_back, this);

		sinaAdd = (RelativeLayout) header.findViewById(R.id.btn_sina_add);
		tencentAdd = (RelativeLayout) header.findViewById(R.id.btn_tencent_add);
		renrenAdd = (RelativeLayout) header.findViewById(R.id.btn_renren_add);
		phoneAdd = (RelativeLayout) header.findViewById(R.id.btn_phone_add);
		recommendAdd = (RelativeLayout) header.findViewById(R.id.btn_recommend_add);
		searchEditText = (EditText) header
				.findViewById(R.id.friend_seach_edittext);
		searchEditText.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				
				if (MotionEvent.ACTION_UP == event.getAction()) {
					startActivity(new Intent(FriendAddActivity.this, FriendsSeacherActivity.class));
				}
				return true;
			}
		});


		// Intent intent = new Intent(FriendAddActivity.this,
		// FriendsSearchActivity.class);
		// startActivity(intent);

		sinaAdd.setOnClickListener(this);
		tencentAdd.setOnClickListener(this);
		renrenAdd.setOnClickListener(this);
		phoneAdd.setOnClickListener(this);
		recommendAdd.setOnClickListener(this);

		// lv_weibo = finder.findListView(R.id.lv_activity_friends_add_list1);
		// weibo_adapter = new WeiboListAdapter(this, R.layout.row_friends_add,
		// R.id.tv_row_friends_add_weibo, weibo_type);
		// lv_weibo.setAdapter(weibo_adapter);
		// lv_weibo.setOnItemClickListener(this);

		section = (TextView) findViewById(R.id.friend_add_section);
		lv_lookfriends = (ListView) findViewById(R.id.lv_activity_friends_add_list2);

		lv_lookfriends.addHeaderView(header);
		// lv_lookfriends.setOnRefreshListener(this);
		// list_friend_data = new ArrayList<User>();

		// GsonResponse gsonResponse = new GsonResponse();
		// for (int i = 0; i < 20; i++) {
		// User user = gsonResponse.new User();
		// user.nickname = "测试用户" + i;
		// list_friend_data.add(user);
		// }

		friendsAddAdapter = new FriendsAddAdapter(this);
		// list_friend_adapter = new FriendListAdapter(this, handler,
		// R.layout.row_list_friend, R.id.tv_row_list_friend_nick,
		// list_friend_data);
		// ListView actualListView = lv_lookfriends.getRefreshableView();
		// actualListView.setCacheColorHint(0);
		// actualListView.setAdapter(list_friend_adapter);

		lv_lookfriends.setAdapter(friendsAddAdapter);
		lv_lookfriends.setOnScrollListener(this);
		lv_lookfriends.setOnItemClickListener(this);

		//Requester2.listRecommendUser(handler, pageno + "", "");
		Requester2.listUserSNS(handler);
		
//		loadLocalData();
	}
	
	
	/**
	 * 		searchEditText.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {

			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {

			}

			@Override
			public void afterTextChanged(Editable s) {
				searchString = searchEditText.getText().toString().trim();

				if (curSearchTask != null
						&& curSearchTask.getStatus() != AsyncTask.Status.FINISHED) {
					try {
						curSearchTask.cancel(true);
					} catch (Exception e) {
					}

				}
				curSearchTask = new SearchTask();
				curSearchTask.execute(searchString);
			}
		});
	 */
	/*private class SearchTask extends AsyncTask<String, Void, String> {

		boolean inSearchMode = false;

		@Override
		protected String doInBackground(String... params) {
			filterUserList.clear();

			String keyword = params[0];

			inSearchMode = (keyword.length() > 0);

			if (inSearchMode) {

				for (WrapRecommendUser user : wrapUserList) {
					WrapRecommendUser contact = (WrapRecommendUser) user;

					if (contact.nickname.contains(keyword)) {
						filterUserList.add(user);
					}
				}

			}
			return null;
		}

		protected void onPostExecute(String result) {

			synchronized (searchLock) {

				if (inSearchMode) {
					friendsAddAdapter.setData(filterUserList);
					friendsAddAdapter.notifyDataSetChanged();
				} else {
					friendsAddAdapter.setData(wrapUserList);
					friendsAddAdapter.notifyDataSetChanged();
				}
			}

		}
	}*/


	
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
	public void onStop(){
		super.onStop();
		CmmobiClickAgentWrapper.onStop(this);
	}

	// @Override
	// public void onRefresh() {
	// // TODO Auto-generated method stub
	// if (allShow) {
	// // lv_lookfriends.onRefreshComplete();
	// return;
	// }
	// Requester.requestFriendList(handler, 2, Requester.VALUE_SEX_BOY,
	// pageno, 5);
	// }

//	private void loadLocalData() {
//
//		ArrayList<WrapRecommendUser> cache = friendsAddManager.getCache();
//
//		if (cache.size() > 0) {
//			wrapUserList.clear();
//			wrapUserList.addAll(cache);
//
//			friendsAddAdapter.setData(wrapUserList);
//
//			friendsAddAdapter.notifyDataSetChanged();
//
//		}
//
//	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode,
			Intent intent) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, intent);
		if (requestCode == REQUEST_CODE) {
			if (resultCode == RESULT_OK) {
				Bundle extras = intent.getExtras();
				String snstype = extras.getString("weibo_type");

				if ("sina".equals(snstype)) {
					CmmobiSnsLib.getInstance(this).sinaAuthorize(sinalistener);
				} else if ("renren".equals(snstype)) {
					CmmobiSnsLib.getInstance(this).renrenAuthorize(
							renrenlistener);
				} else if ("tencent".equals(snstype)) {
					CmmobiSnsLib.getInstance(this).tencentWeiboAuthorize(
							tencentlistener);
				}
			}
		} else {
			if (CmmobiSnsLib.getInstance(this).mSsoHandler != null) {
				CmmobiSnsLib.getInstance(this).mSsoHandler.authorizeCallBack(
						requestCode, resultCode, intent);
			}
		}
	}

	private void launchBindWeiboDialog(final String snstype, String title, String msg) {
		/*
		 * Intent intent = new Intent(this, FriendAddDialog.class); Bundle
		 * mBundle = new Bundle(); mBundle.putString("weibo_type", snstype);//
		 * 压入数据 intent.putExtras(mBundle); // startActivity(intent);
		 * startActivityForResult(intent, REQUEST_CODE);
		 */
		// 添加退出提示对话框
		Prompt.Dialog(this, true, title, msg, new OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				if ("sina".equals(snstype)) {
					CmmobiSnsLib.getInstance(FriendAddActivity.this)
							.sinaAuthorize(sinalistener);
				} else if ("renren".equals(snstype)) {
					CmmobiSnsLib.getInstance(FriendAddActivity.this)
							.renrenAuthorize(renrenlistener);
				} else if ("tencent".equals(snstype)) {
					CmmobiSnsLib.getInstance(FriendAddActivity.this)
							.tencentWeiboAuthorize(tencentlistener);
				}
			}
		}, null);
		
/*		new AlertDialog.Builder(this)
				.setTitle("绑定微博")
				.setCancelable(true)
				// 设置不能通过“后退”按钮关闭对话框
				.setMessage("确定绑定吗?")
				.setPositiveButton("是的", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialoginterface, int i) {
						if ("sina".equals(snstype)) {
							CmmobiSnsLib.getInstance(FriendAddActivity.this)
									.sinaAuthorize(sinalistener);
						} else if ("renren".equals(snstype)) {
							CmmobiSnsLib.getInstance(FriendAddActivity.this)
									.renrenAuthorize(renrenlistener);
						} else if ("tencent".equals(snstype)) {
							CmmobiSnsLib.getInstance(FriendAddActivity.this)
									.tencentWeiboAuthorize(tencentlistener);
						}

					}
				})
				.setNegativeButton("不是", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						dialog.cancel();
					}
				}).show();*/
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {

		Intent intent = new Intent(this, HomepageOtherDiaryActivity.class);
		intent.putExtra("userid", friendsAddAdapter.getItem(position-1).userid);
		/*intent.putExtra("userid", userList[(int)id].userid);*/
		intent.putExtra("nickname", friendsAddAdapter.getItem(position-1).nickname);
		this.startActivity(intent);
	}

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {

	}

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem,
			int visibleItemCount, int totalItemCount) {
		if (firstVisibleItem < 1) {
			section.setVisibility(View.GONE);
		} else {
			section.setVisibility(View.VISIBLE);

		}
	}



	private static final String[] PHONES_PROJECTION = new String[]{ContactsContract.Contacts._ID, ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME, ContactsContract.CommonDataKinds.Phone.NUMBER};
	protected void getContactInfo() {
		ArrayList<GsonRequest2.AddrBook> addrBooks = new ArrayList<GsonRequest2.AddrBook>();
		try {
			// 从本机中取号
			// 得到ContentResolver对象
			ContentResolver cr = getContentResolver();
			Cursor cursor = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, PHONES_PROJECTION, null, null,
					null);
			// 让activity管理游标
			startManagingCursor(cursor);

			while (cursor.moveToNext()) {

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
					contactName = "未知" ;
				}

				String phoneRegex = getNumber(contactNumber);
				if (phoneRegex == null || phoneRegex.equals("")) continue ;
				
	  			AddrBook oneAddrBook = new AddrBook();
				oneAddrBook.phone_name = contactName;
				oneAddrBook.phone_num = phoneRegex;
				if(!addrBooks.contains(oneAddrBook))
				addrBooks.add(oneAddrBook);
			}
			
			//System.out.println("==== addrBooks.isEmpty() ==== " + addrBooks.isEmpty());
			if(addrBooks.isEmpty()){
				runOnUiThread(new Runnable() {
					
					@Override
					public void run() {
						// TODO Auto-generated method stub
						Prompt.Dialog(FriendAddActivity.this, false, "提示", "通讯录里没有有效的手机号码",null);
					}
				});
				
			}else{
				AddrBook[] addrBooksArray;
				addrBooksArray = addrBooks.toArray(new AddrBook[addrBooks.size()]);
				runOnUiThread(new Runnable() {
					
					@Override
					public void run() {
						// TODO Auto-generated method stub
						ZDialog.show(R.layout.progressdialog, false, true, FriendAddActivity.this);
					}
				});
				Requester2.postAddressBook(handler, addrBooksArray);
/*				System.out.println("===" + addrBooksArray.length + "===");
				String persons = null;
				for(int i=0; i< addrBooksArray.length; i++){
					persons = persons + addrBooksArray[i].phone_name + "," + addrBooksArray[i].phone_num + "=";
				}
				System.out.println(persons);*/
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
//			if (num.startsWith("+86")) {
//				num = num.substring(3);
//			} else 
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


}