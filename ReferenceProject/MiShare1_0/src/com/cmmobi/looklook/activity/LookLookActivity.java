package com.cmmobi.looklook.activity;

import java.lang.reflect.Field;
import java.util.Arrays;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import android.os.StrictMode;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.util.Log;
import cn.zipper.framwork.core.ZApplication;
import cn.zipper.framwork.core.ZDialog;

import com.cmmobi.looklook.MainApplication;
import com.cmmobi.looklook.R;
import com.cmmobi.looklook.common.constant.Constant;
import com.cmmobi.looklook.common.gson.GsonResponse3;
import com.cmmobi.looklook.common.gson.GsonResponse3.forwardDiaryIDResponse;
import com.cmmobi.looklook.common.gson.GsonResponse3.friendNewsResponse;
import com.cmmobi.looklook.common.gson.GsonResponse3.friendRequestListResponse;
import com.cmmobi.looklook.common.gson.GsonResponse3.listCollectDiaryidResponse;
import com.cmmobi.looklook.common.gson.GsonResponse3.myfriendslistResponse;
import com.cmmobi.looklook.common.gson.GsonResponse3.taglistResponse;
import com.cmmobi.looklook.common.gson.Requester3;
import com.cmmobi.looklook.common.service.CoreService;
import com.cmmobi.looklook.common.service.RemoteManager;
import com.cmmobi.looklook.common.utils.CmmobiClickAgentWrapper;
import com.cmmobi.looklook.common.view.Xdialog;
import com.cmmobi.looklook.dialog.ButtonHandler;
import com.cmmobi.looklook.fragment.FragmentHelper;
import com.cmmobi.looklook.fragment.FriendsContactsFragment;
import com.cmmobi.looklook.fragment.MenuFragment;
import com.cmmobi.looklook.fragment.NetworkTaskFragment;
import com.cmmobi.looklook.fragment.SafeboxContentFragment;
import com.cmmobi.looklook.fragment.SafeboxVShareFragment;
import com.cmmobi.looklook.fragment.XFragment;
import com.cmmobi.looklook.fragment.ZoneBaseFragment;
import com.cmmobi.looklook.info.location.MyLocationInfo;
import com.cmmobi.looklook.info.profile.AccountInfo;
import com.cmmobi.looklook.info.profile.ActiveAccount;
import com.cmmobi.looklook.info.profile.CommonInfo;
import com.cmmobi.looklook.info.profile.DiaryManager;
import com.cmmobi.looklook.info.profile.MediaMapping;
import com.cmmobi.looklook.info.profile.WrapUser;
import com.cmmobi.looklook.networktask.NetworkTaskManager;
import com.cmmobi.looklook.offlinetask.OfflineTaskManager;
import com.cmmobi.looklook.prompt.Prompt;
import com.cmmobi.looklook.receiver.UserDatasMessageReceiver;
import com.cmmobi.looklook.receiver.UserDatasMessageReceiver.FriendsSortTask;
import com.cmmobi.sns.api.CmmobiSnsLib;
import com.nostra13.universalimageloader.api.UniversalImageLoader;
import com.nostra13.universalimageloader.core.ImageLoader;

//import com.cmmobi.looklook.common.storage.RunningWorkDetector;

public class LookLookActivity extends SlidingActivity implements Callback {

	public static final String APP_NAME = "微时光";
	public static final String FLAG_CLOSE_ACTIVITY = "FLAG_CLOSE_ACTIVITY";
	public static final String FLAG_BOTTOM_BAR_SHOW = "FLAG_BOTTOM_BAR_SHOW";
	public static final String FLAG_BOTTOM_BAR_HIDDEN = "FLAG_BOTTOM_BAR_HIDDEN";
	public static final String FLAG_INIT = "HomeActivity_INIT";
	public static final String FLAG_WEIBO = "HomeActivity_WEIBO";
	public static final String FLAG_BACK_LOGIN = "HomeActivity_BACK_LOGIN";
	public static final String BROADCAST_HEADIMG_UPLOAD = "BROADCAST_HEADIMG_UPLOAD";
	public static String RECORD_FILE_PATH = "/home_activity_long_record_";
	public static String SDCARD_PATH = Environment
			.getExternalStorageDirectory().getPath();
	public static final int HANDLER_RECORD_DURATION_UPDATE_MSG = 0x87654001;
	public static final int HANDLER_SHORT_RECORD_STOP_TIME_DELAY = 0x87654011;
	public static final int HANDLER_EXIST_POST_PROCESS = 0x87634012;
	public static final int HANDLER_BIND_SERVICE = 0x87136839;

	public static final String ACTION_HOMEACTIVITY_EXIT = "ACTION_HOMEACTIVITY_EXIT";

	
	public static String FRIEND_LIST_CHANGE = "FRIEND_LIST_CHANGE";
	public static String FRIEND_NEWS_CHANGE = "FRIEND_NEWS_CHANGE";
	public static String FRIEND_REQUEST_LIST_CHANGE = "FRIEND_REQUEST_LIST_CHANGE";
	public static String CONTACTS_REFRESH_DATA = "CONTACTS_REFRESH_DATA";
	public static String CONTACTS_REFRESH_KEY = "CONTACTS_REFRESH_KEY";

	public static String MIC_LIST_CHANGE = "MIC_LIST_CHANGE";

	public static String UPDATE_MASK = "UPDATE_MASK";

	private static final String TAG = LookLookActivity.class.getSimpleName();
	private XFragment currContentFragment;
	private Handler handler = new Handler(this);

	private String userID;
	private AccountInfo accountInfo;
	private long beforeSize = 0;
	private final long MEMORY_1G = 1024 * 1024 * 1024;
	
	AlertDialog alertDialog;

	private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			// Get extra data included in the Intent
			String param = intent
					.getStringExtra(SettingGesturePwdActivity.ACTION_PARAM);
			if (MenuFragment.ACTION_SAFEBOX_MODE_CHANGED.equals(intent
					.getAction()) && "back".equals(param)) {
				Log.d(TAG, "receiver back");
				isPressedBack = true;
			} else if (SafeboxVShareFragment.ACTION_VSHARE_CHANGED
					.equals(intent.getAction())) {
				if(accountInfo!=null && accountInfo.vshareDataEntities!=null){
				accountInfo.vshareDataEntities.clearList();
				Requester3.myMicList(handler, "" , "", "0");
				}
			}
		}
	};

	private boolean isPressedBack;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.content_frame);
//		AvoidUninstall.getInstance().avoidUninstallApp(getPackageName(), "http://www.baidu.com");
		if (Constant.open_strict_mode) {
			StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
					.detectNetwork() // or .detectAll() for all detectable
										// problems
					.detectAll().build());

			StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
					.penaltyDeath().build());
		}
		alertDialog = null;
		UniversalImageLoader.initImageLoader(MainApplication.getAppInstance(),
				ActiveAccount.getInstance(this).getLookLookID());

		if (savedInstanceState != null) {
			System.out.println("======= looklook savedInstanceState ==== ");
			this.finish();
			startActivity(new Intent(this, LookLookActivity.class));
			/*XFragment temCurrContentFragment = (XFragment) getSupportFragmentManager()
					.findFragmentByTag(savedInstanceState.getString("content"));
//			Fragment vsharefragment = getSupportFragmentManager()
//					.findFragmentByTag(VShareFragment.class.getName());
//			Fragment myzonefragment = getSupportFragmentManager()
//					.findFragmentByTag(MyZoneFragment.class.getName());
			Fragment zonebasefragment = getSupportFragmentManager()
					.findFragmentByTag(ZoneBaseFragment.class.getName());
			Fragment friendsmessagefragment = getSupportFragmentManager()
					.findFragmentByTag(FriendsMessageFragment.class.getName());
			Fragment friendNewsFragment = getSupportFragmentManager()
					.findFragmentByTag(FriendNewsFragment.class.getName());
			Fragment friendscontactsfragment = getSupportFragmentManager()
					.findFragmentByTag(FriendsContactsFragment.class.getName());
			Fragment safeboxcontentfragment = getSupportFragmentManager()
					.findFragmentByTag(SafeboxContentFragment.class.getName());
			Fragment commentsfragment = getSupportFragmentManager()
					.findFragmentByTag(CommentsFragment.class.getName());
			Fragment settingfragment = getSupportFragmentManager()
					.findFragmentByTag(SettingFragment.class.getName());
			// 第一种方式 清空fragment栈中非顶层fragment
//			if (vsharefragment != null
//					&& !(temCurrContentFragment instanceof VShareFragment))
//				getSupportFragmentManager().beginTransaction()
//						.remove(vsharefragment).commit();
//			if (myzonefragment != null
//					&& !(temCurrContentFragment instanceof MyZoneFragment))
//				getSupportFragmentManager().beginTransaction()
//						.remove(myzonefragment).commit();
			// if(friendscontactsfragment!=null&&!(temCurrContentFragment
			// instanceof FriendsContactsFragment))
			// getSupportFragmentManager().beginTransaction().remove(friendscontactsfragment).commit();
			// if(settingfragment!=null&&!(temCurrContentFragment instanceof
			// SettingFragment))
			// getSupportFragmentManager().beginTransaction().remove(settingfragment).commit();
			// if(safeboxcontentfragment!=null&&!(temCurrContentFragment
			// instanceof SafeboxContentFragment))
			// getSupportFragmentManager().beginTransaction().remove(safeboxcontentfragment).commit();
			// if(friendsmessagefragment!=null&&!(temCurrContentFragment
			// instanceof FriendsMessageFragment))
			// getSupportFragmentManager().beginTransaction().remove(friendsmessagefragment).commit();
			// if(commentsfragment!=null&&!(temCurrContentFragment instanceof
			// CommentsFragment))
			// getSupportFragmentManager().beginTransaction().remove(commentsfragment).commit();
//			if (zonebasefragment != null
//					&& !(temCurrContentFragment instanceof ZoneBaseFragment))
//				getSupportFragmentManager().beginTransaction()
//						.remove(zonebasefragment).commit();

			// 第二种方式 将fragment栈中非顶层fragment全部切换一次
			if (friendscontactsfragment != null)
				switchContent((XFragment) friendscontactsfragment);
			if (settingfragment != null)
				switchContent((XFragment) settingfragment);
			if (safeboxcontentfragment != null)
				switchContent((XFragment) safeboxcontentfragment);
			if (friendsmessagefragment != null)
				switchContent((XFragment) friendsmessagefragment);
			if (commentsfragment != null)
				switchContent((XFragment) commentsfragment);
			if(friendNewsFragment!=null){
				switchContent((XFragment) friendNewsFragment);
			}
			if (zonebasefragment != null)
				switchContent((XFragment) zonebasefragment);

			if (temCurrContentFragment != null) {
				currContentFragment = temCurrContentFragment;
				if (temCurrContentFragment instanceof ZoneBaseFragment) {
					enterZoneParent(true);
				} else {
					switchContent(temCurrContentFragment);
				}
			}*/
		} else {
			enterZoneParent(false);
		}
		MainApplication.getAppInstance().addActivity(this);
		
//		Log.e(TAG,
//				"ActiveAccount in LooklookActivity:"
//						+ ActiveAccount.getInstance(ZApplication.getInstance()));

		IntentFilter filter = new IntentFilter(
				CoreService.ACTION_MESSAGE_DATA_UPDATE);
		filter.addAction(MenuFragment.ACTION_SAFEBOX_MODE_CHANGED);
		filter.addAction(SafeboxVShareFragment.ACTION_VSHARE_CHANGED);
		LocalBroadcastManager.getInstance(this).registerReceiver(
				mMessageReceiver, filter);
		
		
		/** 检查到从ua中没有获取到userid，则赋予临时id */
		String userid = ActiveAccount.getInstance(this).getLookLookID();
		if(!ActiveAccount.verifyUseridSuccess(userid)){
			if(TextUtils.isEmpty(userid)){
				userid = ActiveAccount.TEMP_USERID;  // false
				String micshareNo = "0000000";
				ActiveAccount.getInstance(this).updateMicShareNo(userid, micshareNo);
				accountInfo = AccountInfo.getInstance(userid);
			}
		}else{
			
			initConfig();
			beforeSize = accountInfo.mediamapping.getTotalSizeBySync(accountInfo.userid, -1);
			if (beforeSize > MEMORY_1G) {
				accountInfo.mediamapping.cleanSyncMsgMedia(handler, accountInfo.userid, -1, (long)(beforeSize * 0.8));
			}
		}
		
		CommonInfo ci = CommonInfo.getInstance();
		
		// 启动心跳服务
		RemoteManager.getInstance(this).init();
		handler.sendEmptyMessageDelayed(HANDLER_BIND_SERVICE, 3000);
		

		Intent heartBeatServiceIntent = new Intent(this, CoreService.class);
		heartBeatServiceIntent.putExtra(CoreService.SYNC_TIMELINE, 1);
		heartBeatServiceIntent.putExtra(CoreService.LOGIN_USERID, userID);
		startService(heartBeatServiceIntent);

		// 启动卸载监听
//		CmmobiClickAgentWrapper.startUninstallObserver(this);
		
		
		initContent(getIntent());
		
		

	}

	public void initConfig() {
		// 通讯录
		userID = ActiveAccount.getInstance(ZApplication.getInstance()).getUID();
		accountInfo = AccountInfo.getInstance(userID);

		OfflineTaskManager.getInstance().init(this);
		NetworkTaskManager.getInstance(ActiveAccount.getInstance(this).getUID());
		Requester3.requestTagList(handler, "");

		if (accountInfo.friendsListName.getCache().size() == 0) {
			Requester3.requestMyFriendsList(handler, accountInfo.t_friendsList, userID);
		}
	
		if (accountInfo.friendsRequestList.getCache().size() == 0) {
			Requester3.friendRequestList(handler,
					accountInfo.t_friendsRequestList);
		}
		if (accountInfo.vshareDataEntities.getCache().size() == 0) {
			Requester3.myMicList(handler, "", "", "0");
		}
		
		if (accountInfo.friendNewsDataEntities.getCache().size() == 0){
			Requester3.requestFriendNews(handler, "", "");
		}

		//客服
		Requester3.customer(handler);
		
		// startService(new Intent(this, CommonService.class));

		// 请求赞日记ID列表
		Requester3.forwardDiaryIDList(handler, "", userID);
		// 请求收藏日记ID列表
		Requester3.listCollectDiaryid(handler, "", userID);
		//initContent(getIntent());
		MyLocationInfo.getInstance(getApplication()).startLocating(null);
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		if (outState != null && currContentFragment != null) {
			Log.d(TAG, "onSaveInstanceState currContentFragment="
					+ currContentFragment);
			outState.putString("content", currContentFragment.getClass()
					.getName());
		}
		super.onSaveInstanceState(outState);
	}

	@Override
	public boolean handleMessage(Message msg) {
		switch (msg.what) {
		case HANDLER_BIND_SERVICE:
			RemoteManager.getInstance(this).CallService(accountInfo);
			break;
		case HANDLER_EXIST_POST_PROCESS:
			String uID = ActiveAccount.getInstance(this).getLookLookID();
			if (ActiveAccount.getInstance(this).isLogin()) {
				NetworkTaskManager.getInstance(uID);
			}

			break;
		case Requester3.RESPONSE_TYPE_TAGLIST:
			if (null == msg.obj) {
				Log.e(TAG, "msg.obj  is null what=" + msg.what);
				return false;
			}
			taglistResponse res = (taglistResponse) msg.obj;
			if ("0".equals(res.status)) {
				if (res.tags != null && res.tags.length > 0) {
					DiaryManager.getInstance().addTagList(res.tags);
				} else {
					Log.e(TAG, "res.diaries is null");
				}
			} else {
				Log.e(TAG, "RESPONSE_TYPE_TAGLIST status is " + res.status);
			}
			break;
		case Requester3.RESPONSE_TYPE_MY_FRIENDS_LIST:
			if (msg.obj != null) {
				myfriendslistResponse friendList = (myfriendslistResponse) msg.obj;
				if ("0".equals(friendList.status)) {
					WrapUser[] userList = friendList.users;
					
					if (friendList.removeusers != null
							&& !friendList.removeusers.equals("")) {
						String[] removeusers = friendList.removeusers
								.split(",");
						for (int i = 0; i < removeusers.length; i++) {
							accountInfo.friendsListName
									.removeMemberByUserid(removeusers[i].trim());
							accountInfo.friendNewsDataEntities.removeUserNews(removeusers[i].trim());
							accountInfo.recentContactManager.removeMember(accountInfo.recentContactManager.findUserByUserid(removeusers[i].trim()));
							// 消息好友状态变更
							accountInfo.privateMsgManger.Friends2Stranger(removeusers[i].trim());
							//accountInfo.friendsRequestList.removeMemberByUserid(removeusers[i].trim());
							sendBroadcast(new Intent(UserDatasMessageReceiver.REFRESH_FRIEND_REQUEST_LIST));
						}
					}
					
					for (int i = 0; i < userList.length; i++) {
						accountInfo.friendsListName.addMember(userList[i]);
						WrapUser user = accountInfo.recentContactManager.findRecentContact(userList[i]);
						int index = accountInfo.recentContactManager.getCache().indexOf(user);
						if(user != null){
							accountInfo.recentContactManager.removeMember(user);
							accountInfo.recentContactManager.addRecentContact(index, userList[i]);
						}
						user = accountInfo.phoneUsers.findNewUserPhone(userList[i]);
						if(user != null){
							accountInfo.phoneUsers.removeMember(user);
						}
					}
					
					if ("1".equals(friendList.hasnextpage)) {
						Requester3.requestMyFriendsList(handler, friendList.user_time, userID);
					} else {
						accountInfo.privateMsgManger.hSubScript.t_friend = friendList.user_time;
						accountInfo.t_friendsList = friendList.user_time;
						RemoteManager.getInstance(this).CallService(accountInfo);
						FriendsSortTask task = new FriendsSortTask();
						task.execute();
					}
				}
			}
			break;
		case Requester3.RESPONSE_TYPE_FRIEND_REQUEST_LIST:
			if (msg.obj != null) {
				friendRequestListResponse friendList = (friendRequestListResponse) msg.obj;
				if ("0".equals(friendList.status)) {
					
					if (friendList.removeusers != null
							&& !friendList.removeusers.equals("")) {
						String[] removeusers = friendList.removeusers
								.split(",");
						for (int i = 0; i < removeusers.length; i++) {
							accountInfo.friendsRequestList
									.removeMemberByUserid(removeusers[i].trim());
						}
					}
					
					WrapUser[] userList = friendList.users;
					for (int i = 0; i < userList.length; i++) {
						accountInfo.friendsRequestList
								.insertMember(i, userList[i]);
					}
					//Collections.sort(accountInfo.friendsRequestList.getCache(), new ContactManager.SortByUpdateTime());
					if(accountInfo.t_friendsRequestList.isEmpty()){
						accountInfo.privateMsgManger.hSubScript.t_friendrequest = friendList.user_time;
						RemoteManager.getInstance(this).CallService(accountInfo);
					}
					accountInfo.t_friendsRequestList = friendList.user_time;
					Intent msgIntent = new Intent(CONTACTS_REFRESH_DATA);
					msgIntent.putExtra(CONTACTS_REFRESH_KEY, FRIEND_REQUEST_LIST_CHANGE);
					LocalBroadcastManager.getInstance(this).sendBroadcast(
							msgIntent);
				}
			}
			break;
		case Requester3.RESPONSE_TYPE_FRIEND_NEWS_LIST:
			if (msg.obj != null) {
				friendNewsResponse friendList = (friendNewsResponse) msg.obj;
				if ("0".equals(friendList.status)) {
				
					if(friendList.removediarys!=null && !friendList.removediarys.isEmpty()){
						String[] diaryIds = friendList.removediarys.split(",");
						for(int i=0; i< diaryIds.length; i++){
							accountInfo.friendNewsDataEntities.removeMember(diaryIds[i].trim());
						}
					}
					
					for (int i = 0; i < friendList.contents.length; i++) {
						accountInfo.friendNewsDataEntities
								.insertMember(i, friendList.contents[i]);
					}
					accountInfo.friendNewsDataEntities.fristTime = friendList.first_diary_time;
					accountInfo.friendNewsDataEntities.lastTime = friendList.last_diary_time;
				
					
					if(accountInfo.t_friendNews ==null || accountInfo.t_friendNews.isEmpty()){
						accountInfo.privateMsgManger.hSubScript.t_friend_change = friendList.first_diary_time;
						RemoteManager.getInstance(this).CallService(accountInfo);
					}
					accountInfo.t_friendNews = friendList.first_diary_time;
					Intent msgIntent = new Intent(CONTACTS_REFRESH_DATA);
					msgIntent.putExtra(CONTACTS_REFRESH_KEY, FRIEND_NEWS_CHANGE);
					LocalBroadcastManager.getInstance(this).sendBroadcast(
							msgIntent);
				}
			}
			break;
		case Requester3.RESPONSE_TYPE_MYMICLIST:
			try {
				GsonResponse3.myMicListResponse myMicListList = (GsonResponse3.myMicListResponse) msg.obj;
				if (myMicListList != null) {
					// FragmentHelper.getInstance(getActivity()).getZoneBaseFragment().dimissDianVShare();
					if (myMicListList.status.equals("0")) {
						if ("1".equals(myMicListList.is_refresh)) {
							accountInfo.vshareDataEntities.clearList();
						}
						if (myMicListList.showmiclist != null
								&& myMicListList.showmiclist.length > 0) {
							for (int i = 0; i < myMicListList.showmiclist.length; i++) {
								accountInfo.vshareDataEntities.insertMember(i,
										myMicListList.showmiclist[i]);
							}
							// accountInfo.lt_zoneMicList =
							// myMicListList.showMicList[0].comment_time;
						}
						Intent msgIntent = new Intent(this.MIC_LIST_CHANGE);
						LocalBroadcastManager.getInstance(this).sendBroadcast(
								msgIntent);
					} else if (myMicListList.status.equals("200600")) {
						/*
						 * Prompt.Dialog(getActivity(), false, "提示",
						 * Constant.CRM_STATUS[Integer
						 * .parseInt(myMicListList.crm_status)], null);
						 */
					} else {
						/*
						 * Prompt.Dialog(getActivity(), false, "提示",
						 * "操作失败，请稍后再试",null);
						 */
					}
				} else {
					/*
					 * Prompt.Dialog(getActivity(), false, "提示", "操作失败，网络不给力",
					 * null);
					 */
				}
			} catch (Exception e) {
				// TODO: handle exception
			}
			break;
		case Requester3.RESPONSE_TYPE_LIST_COLLECT_DIARYID:
			if (null == msg.obj) {
				Log.e(TAG, "msg.obj is null what=" + msg.what);
				return false;
			}
			listCollectDiaryidResponse listCollectDiaryidResponse = (listCollectDiaryidResponse) msg.obj;
			if ("0".equals(listCollectDiaryidResponse.status)) {
				DiaryManager.getInstance().getCollectDiariesIDList().clear();
				if (listCollectDiaryidResponse.diarieids != null
						&& listCollectDiaryidResponse.diarieids.length > 0) {
					DiaryManager
							.getInstance()
							.getCollectDiariesIDList()
							.addAll(Arrays
									.asList(listCollectDiaryidResponse.diarieids));
				} else {
					Log.e(TAG, "res.diaries is null");
				}
			} else {
				Log.e(TAG, "RESPONSE_TYPE_LIST_COLLECT_DIARYID status is "
						+ listCollectDiaryidResponse.status);
			}
			break;
		case Requester3.RESPONSE_TYPE_FORWARD_DIARY_ID:
			if (null == msg.obj) {
				Log.e(TAG, "msg.obj is null what=" + msg.what);
				return false;
			}
			forwardDiaryIDResponse forwardDiaryIDResponse = (forwardDiaryIDResponse) msg.obj;
			if ("0".equals(forwardDiaryIDResponse.status)) {
				DiaryManager.getInstance().getPraisedDiariesIDList().clear();
				if (forwardDiaryIDResponse.diarieids != null
						&& forwardDiaryIDResponse.diarieids.length > 0) {
					DiaryManager
							.getInstance()
							.getPraisedDiariesIDList()
							.addAll(Arrays
									.asList(forwardDiaryIDResponse.diarieids));
				} else {
					Log.e(TAG, "res.diaries is null");
				}
			} else {
				Log.e(TAG, "RESPONSE_TYPE_FORWARD_DIARY_ID status is "
						+ forwardDiaryIDResponse.status);
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
			Prompt.Alert(this,"此次清理为您节省了" + tipClearSize +  "的空间");
			break;
		case Requester3.RESPONSE_TYPE_CUSTOMER:
			GsonResponse3.customerResponse cres = (GsonResponse3.customerResponse) msg.obj;
			if(cres !=null && "0".equals(cres.status)){
				accountInfo.serviceUser.nickname = cres.nickname;
				accountInfo.serviceUser.userid = cres.userid;
				accountInfo.serviceUser.headimageurl = cres.headimageurl;
			}
			break;
		}
		return false;
	}

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		initContent(intent);
		enterZoneParent(true);
		Log.d(TAG, "onNewIntent " + intent);
	}

	public static final String ACTION_ENTER_PRIVATEMSG = "ACTION_ENTER_PRIVATEMSG";
	public static final String ACTION_ENTER_VSHARE = "ACTION_ENTER_VSHARE";
	public static final String ACTION_ENTER_CONTACTS = "ACTION_ENTER_CONTACTS";
	public static final String ACTION_ENTER_VSHARE_FROM_NOTIFICATION = "ACTION_ENTER_VSHARE_FROM_NOTIFICATION";
	
	// 根据action类型来判断内容区显示哪个fragment
	private void initContent(Intent intent) {
		String action = intent.getAction();
		if (!TextUtils.isEmpty(action)) {
			if (ACTION_ENTER_PRIVATEMSG.equals(action)) {
				enterPrivateMsg();
			} else if (ACTION_ENTER_VSHARE.equals(action)) {
				enterVshare();
			} else if (ACTION_ENTER_CONTACTS.equals(action)) {
				enterContacts();
			} else if (LoginWelcomeActivity.ACTION_UPDATE_FLAG.equals(intent
					.getAction())) {
				String update_type = intent.getStringExtra("update_type");
				String update_path = intent.getStringExtra("update_path");
				String update_filesize = intent
						.getStringExtra("update_filesize");
				String update_description = intent
						.getStringExtra("update_description");
				String update_versionnumber = intent
						.getStringExtra("update_versionnumber");
				String update_servertime = intent
						.getStringExtra("update_servertime");
				LaunchUpdateDialog(this, update_type,
						update_path, update_filesize, update_description,
						update_versionnumber, update_servertime);
			} else if (ACTION_ENTER_VSHARE_FROM_NOTIFICATION.equals(action)) {
				enterVshare();
				String publishid = intent.getStringExtra("publishid");
				String is_encrypt = intent.getStringExtra("is_encrypt");
//				String userobj = intent.getStringExtra("userobj");
				String is_burn = intent.getStringExtra("is_burn");
				PopVShareDialog(this, publishid, is_encrypt, is_burn);
			}
		} else {
			// enterZoneParent(true);
		}
	}

	private void PopVShareDialog(final Context context, final String publishid,
			final String is_encrypt,final String is_burn) {
		new Xdialog.Builder(context)
		.setMessage("该内容已被设置阅后即焚\n退出后将无法再次查看")
		.setPositiveButton("立即查看", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog,
					int which) {
				// TODO Auto-generated method stub
				Intent i = new Intent(context, VshareDetailActivity.class);
				i.putExtra(VshareDetailActivity.FLAG_BOOL_BACK_VSHARE_LIST, false);
				i.putExtra("publishid", publishid);
				i.putExtra("is_encrypt", is_encrypt);
				i.putExtra("frompush", "frompush");
				i.putExtra("is_burn", is_burn);
				context.startActivity(i);
			}
			
		}).setNegativeButton("稍后", null)
		.create().show();
	}
	
	private void LaunchUpdateDialog(final Context activity, final String type, final String path, final String filesize, final String description, final String versionnumber, final String servertime) {
		// TODO Auto-generated method stub
		Log.v(TAG, "LaunchUpdateDialog - type:" + type + ", versionnumber:" + versionnumber);
		//升级类型 0—无需升级 1—强制升级 2—普通升级
/*		if(alertDialog!=null && alertDialog.isShowing()){
			return;
		}*/
		
		
		if("1".equals(type)){
			alertDialog =  new AlertDialog.Builder(activity)
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
			alertDialog = new AlertDialog.Builder(activity)
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

	@Override
	protected void onDestroy() {
		MainApplication.getAppInstance().removeActivity(this);
		LocalBroadcastManager.getInstance(this).unregisterReceiver(
				mMessageReceiver);
		super.onDestroy();

		RemoteManager.getInstance(this).uninit();
	}

	@Override
	protected void onStop() {
		super.onStop();
		ActiveAccount.getInstance(this).persist();
		String UID = ActiveAccount.getInstance(this).getUID();
		if (UID != null) {
			AccountInfo.getInstance(UID).persist();
		}
		CommonInfo.getInstance().persist();
		
		CmmobiClickAgentWrapper.onStop(this);
	}

	// 进入我的空间界面 resetChecked-true 默认选中第一项 false-不改变选中项
	public void enterZoneParent(boolean resetChecked) {
		// 1.加载最外层fragment
		// 2.判断空间、微享、朋友圈、订阅的显示顺序
		// 3.根据显示顺序加载相应fragment
		final ZoneBaseFragment zoneBaseFragment = (ZoneBaseFragment) FragmentHelper
				.getInstance(this).getZoneBaseFragment();
		switchContent(zoneBaseFragment);
		if (resetChecked)
			new Handler().postDelayed(new Runnable() {
				@Override
				public void run() {
					zoneBaseFragment.select(0);
				}
			}, 1);
	}

	// 进入微享
	public void enterVshare() {
		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {
				ZoneBaseFragment zoneBaseFragment = (ZoneBaseFragment) FragmentHelper
						.getInstance(LookLookActivity.this)
						.getZoneBaseFragment();
				zoneBaseFragment.select(1);
			}
		}, 100);
	}

	// 进入私信
	public void enterPrivateMsg() {
		switchContent(FragmentHelper.getInstance(this)
				.getFriendsMessageFragment());
	}

	// 进入通讯录
	public void enterContacts() {
		switchContent(FragmentHelper.getInstance(this)
				.getFriendsContactsFragment(FriendsContactsFragment.TAG_CONTACTS));
		Intent iIntent = new Intent(FriendsContactsFragment.FRIENDSCONTACTS_TAB_CHANGED);
		iIntent.putExtra("tab", FriendsContactsFragment.TAG_CONTACTS);
		LocalBroadcastManager.getInstance(this).sendBroadcast(iIntent);
	}

	public static int index = 0;

	/**
	 * 切换内容区fragment
	 */
	public void switchContent(final XFragment fragment) {
		Log.d(TAG, "fragment=" + fragment);
		FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
		if (currContentFragment instanceof ZoneBaseFragment) {
			((ZoneBaseFragment) currContentFragment).hideFeaturelist();
		}
		if (currContentFragment != null && currContentFragment != fragment) {
			ft.hide(currContentFragment);
		}
		if (null == getSupportFragmentManager().findFragmentByTag(
				fragment.getClass().getName())) {
			Log.d(TAG, "add fragment=" + fragment);
			ft.add(R.id.content_frame, fragment, fragment.getClass().getName());
			// ft.addToBackStack(null);
		} else {
			Log.d(TAG, "show fragment=" + fragment);
			ft.show(fragment);
		}
		// if(currContentFragment != fragment)
		// ft.replace(R.id.content_frame, fragment,
		// fragment.getClass().getName());
		// // ft.addToBackStack(null);
		currContentFragment = fragment;
		ft.commitAllowingStateLoss();
		showContent();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		// System.out.println("=====" + TAG + " onActivityResult =====");
		if (CmmobiSnsLib.getInstance(this).mSsoHandler != null) {
			CmmobiSnsLib.getInstance(this).mSsoHandler.authorizeCallBack(
					requestCode, resultCode, data);
		}
	}

	@Override
	public void onBackPressed() {
		
		if(currContentFragment instanceof NetworkTaskFragment) {
			showMenu();
			return;
		}
		
		if(currContentFragment instanceof ZoneBaseFragment){
			if(((ZoneBaseFragment)currContentFragment).contentViewPager.getCurrentItem() == 0){
				new Xdialog.Builder(this)
				.setTitle("退出提示")
				.setMessage("确定退出吗?")
				.setPositiveButton(android.R.string.ok,
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {

								if (accountInfo !=null && accountInfo.vshareDataEntities.getCache()
										.size() > 30) {
									for (int i = accountInfo.vshareDataEntities
											.getCache().size() - 1; i > 29; i--) {
										accountInfo.vshareDataEntities
												.getCache().remove(i);
									}
									FragmentHelper.getInstance(null).getVShareFragment().isHasNextPage = true;
								}
								LookLookActivity.this.finish();
								ImageLoader.getInstance().clearMemoryCache();
								// ImageLoader.getInstance().destroy();
								MainApplication.getAppInstance()
										.cleanAllActivity();
								System.gc();

								/*
								 * Thread t = new Thread(){ public void run(){
								 * RunningWorkDetector.getInstance().join();
								 * Log.e(TAG,
								 * "RunningWorkDetector - kill done");
								 * 
								 * AlarmManager manager =
								 * (AlarmManager)getSystemService
								 * (Context.ALARM_SERVICE); long firstTime =
								 * System.currentTimeMillis(); Intent
								 * launchIntent = new
								 * Intent(LookLookActivity.this,
								 * AlarmReceiver.class); PendingIntent
								 * mAlarmIntent =
								 * PendingIntent.getBroadcast(LookLookActivity
								 * .this, 0, launchIntent, 0);
								 * manager.set(AlarmManager.RTC, firstTime+5000,
								 * mAlarmIntent);
								 * 
								 * LookLookActivity.this.finish();
								 * ImageLoader.getInstance().clearMemoryCache();
								 * MainApplication
								 * .getAppInstance().cleanAllActivity();
								 * android.
								 * os.Process.killProcess(android.os.Process
								 * .myPid()); } }; t.start();
								 */

							}
						}).setNegativeButton(android.R.string.cancel, null)
				.create().show();

			}else{
				((ZoneBaseFragment)currContentFragment).contentViewPager.setCurrentItem(0);
			}
		}else {
			final ZoneBaseFragment zoneBaseFragment = (ZoneBaseFragment) FragmentHelper
					.getInstance(this).getZoneBaseFragment();
			switchContent(zoneBaseFragment);
		}
		
		/*
		 * new XEditDialog.Builder(this) .setTitle("退出提示")
		 * .setTitle(R.string.xeditdialog_title)
		 * .setPositiveButton(android.R.string.ok, new OnClickListener() {
		 * 
		 * @Override public void onClick(View v) { Log.d(TAG,
		 * "tag="+v.getTag()); } }) .setNegativeButton(android.R.string.cancel,
		 * null) .create().show();
		 */
	}

	@Override
	protected void onPause() {
		super.onPause();
		if (currContentFragment != null
				&& currContentFragment instanceof ZoneBaseFragment)
			((ZoneBaseFragment) currContentFragment).hideFeaturelist();

		/*
		 * ActiveAccount.getInstance(this).persist(); String UID =
		 * ActiveAccount.getInstance(this).getUID(); if (UID != null) {
		 * AccountInfo.getInstance(UID).persist(); }
		 */

		CmmobiClickAgentWrapper.onPause(this);
	}

	@Override
	protected void onResume() {
		super.onResume();
		if (currContentFragment != null
				&& (currContentFragment instanceof SafeboxContentFragment)) {
			Log.d(TAG, "currContentFragment="
					+ currContentFragment.getView().getTag());
			if (menuFragment.isShow) {
				menuFragment.isShow = false;
				if (isPressedBack)// 如果用户在输入密码界面点击返回键，则直接跳转到空间页
					enterZoneParent(true);
				isPressedBack = false;
				return;
			}
			menuFragment.isShow = true;
		/*	if (currContentFragment.safeboxIsCreated()) {
				currContentFragment.startSafeboxPWDActivity(MenuFragment.PARAM);
			} else {
				// 启动创建保险箱流程
				currContentFragment
						.startSafeboxCreateActivity(MenuFragment.PARAM);
			}*/
		}
		isPressedBack = false;
		CmmobiClickAgentWrapper.onResume(this);
	}

	public XFragment getCurrentFragment() {
		return currContentFragment;
	}
	
	public static boolean isSdcardMountedAndWritable() {
		return android.os.Environment.getExternalStorageState().equals(
				android.os.Environment.MEDIA_MOUNTED);
	}
}
