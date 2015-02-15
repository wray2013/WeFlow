package com.cmmobi.looklook.activity;

import java.io.File;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.app.DownloadManager.Request;
import android.app.AlertDialog;
import android.app.TabActivity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.AnimationDrawable;
import android.hardware.Camera.CameraInfo;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import android.provider.MediaStore.Video.Thumbnails;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;
import cn.jpush.android.api.JPushInterface;
import cn.zipper.framwork.core.ZApplication;
import cn.zipper.framwork.core.ZDialog;
import cn.zipper.framwork.core.ZLog;
import cn.zipper.framwork.core.ZToast;
import cn.zipper.framwork.utils.ZGraphics;
import cn.zipper.framwork.utils.ZThread;
import cn.zipper.framwork.utils.ZTimerTask;
import cn.zipper.framwork.utils.ZUniformScaler;
import cn.zipper.framwork.utils.ZUniformScaler.Model;
import cn.zipper.framwork.utils.ZUniformScaler.ScaleType;

import com.cmmobi.looklook.Config;
import com.cmmobi.looklook.MainApplication;
import com.cmmobi.looklook.R;
import com.cmmobi.looklook.activity.VideoShootActivity.TempDiaryWrapper;
import com.cmmobi.looklook.common.constant.Constant;
import com.cmmobi.looklook.common.gson.CRMRequester;
import com.cmmobi.looklook.common.gson.CRM_Object;
import com.cmmobi.looklook.common.gson.CRM_Object.versionCheckResponse;
import com.cmmobi.looklook.common.gson.GsonRequest2;
import com.cmmobi.looklook.common.gson.GsonRequest2.Attachs;
import com.cmmobi.looklook.common.gson.GsonResponse2;
import com.cmmobi.looklook.common.gson.GsonResponse2.MyAttach;
import com.cmmobi.looklook.common.gson.GsonResponse2.MyAttachAudio;
import com.cmmobi.looklook.common.gson.GsonResponse2.MyUserids;
import com.cmmobi.looklook.common.gson.GsonResponse2.createStructureResponse;
import com.cmmobi.looklook.common.gson.GsonResponse2.diaryAttach;
import com.cmmobi.looklook.common.gson.GsonResponse2.forwardDiaryIDResponse;
import com.cmmobi.looklook.common.gson.GsonResponse2.getOfficialUseridsResponse;
import com.cmmobi.looklook.common.gson.GsonResponse2.listCollectDiaryidResponse;
import com.cmmobi.looklook.common.gson.GsonResponse2.myattentionlistResponse;
import com.cmmobi.looklook.common.gson.GsonResponse2.myattentionlistUsers;
import com.cmmobi.looklook.common.gson.GsonResponse2.myblacklistResponse;
import com.cmmobi.looklook.common.gson.GsonResponse2.myblacklistUsers;
import com.cmmobi.looklook.common.gson.GsonResponse2.myfanslistResponse;
import com.cmmobi.looklook.common.gson.GsonResponse2.myfanslistUsers;
import com.cmmobi.looklook.common.gson.GsonResponse2.taglistResponse;
import com.cmmobi.looklook.common.gson.Requester2;
import com.cmmobi.looklook.common.service.CommonService;
import com.cmmobi.looklook.common.service.DiarySyncService;
import com.cmmobi.looklook.common.service.InitService;
import com.cmmobi.looklook.common.service.PrivateMessageService;
import com.cmmobi.looklook.common.utils.CmmobiClickAgentWrapper;
import com.cmmobi.looklook.common.utils.LowStorageChecker;
import com.cmmobi.looklook.common.utils.LowStorageChecker.OnChoseListener;
import com.cmmobi.looklook.common.utils.MD5;
import com.cmmobi.looklook.common.utils.MediaCoverUtils;
import com.cmmobi.looklook.common.utils.UmengclickAgentWrapper;
import com.cmmobi.looklook.common.view.CountDownView;
import com.cmmobi.looklook.dialog.ButtonHandler;
import com.cmmobi.looklook.fragment.WrapFunUser;
import com.cmmobi.looklook.fragment.WrapUser;
import com.cmmobi.looklook.info.profile.AccountInfo;
import com.cmmobi.looklook.info.profile.ActiveAccount;
import com.cmmobi.looklook.info.profile.CommonInfo;
import com.cmmobi.looklook.info.profile.ContactManager;
import com.cmmobi.looklook.info.profile.DiaryManager;
import com.cmmobi.looklook.info.profile.FriendsFunsManager;
import com.cmmobi.looklook.info.profile.LoginSettingManager;
import com.cmmobi.looklook.info.profile.MediaValue;
import com.cmmobi.looklook.info.profile.NetworkTaskInfo;
import com.cmmobi.looklook.info.profile.OfficialUseridsManager;
import com.cmmobi.looklook.info.profile.TimeHelper;
import com.cmmobi.looklook.info.weather.MyWeather;
import com.cmmobi.looklook.map.MapItemHelper;
import com.cmmobi.looklook.networktask.CacheNetworkTask;
import com.cmmobi.looklook.networktask.INetworkTask;
import com.cmmobi.looklook.networktask.NetworkTaskManager;
import com.cmmobi.looklook.networktask.UploadNetworkTask;
import com.cmmobi.looklook.offlinetask.OfflineTaskManager;
import com.cmmobi.looklook.prompt.Prompt;
import com.cmmobi.looklook.prompt.TickDownHelper;
import com.cmmobivideo.utils.Mp4InfoUtils;
import com.cmmobivideo.utils.PluginUtils;
import com.cmmobivideo.utils.XUtils;
import com.cmmobivideo.workers.XVideoRecorder;
import com.cmmobivideo.workers.XVideoRecorder.XVideoRecorderInfoListener;
import com.iflytek.msc.ExtAudioRecorder;
import com.iflytek.msc.QISR_TASK;
import com.nostra13.universalimageloader.api.UniversalImageLoader;

import effect.EffectType.CamaraMode;
import effect.XEffects;

public class HomeActivity extends TabActivity implements OnClickListener,
		OnLongClickListener, Callback, OnTouchListener, XVideoRecorderInfoListener<Object> {
	public static final String FLAG_CLOSE_ACTIVITY = "FLAG_CLOSE_ACTIVITY";
	public static final String FLAG_BOTTOM_BAR_SHOW = "FLAG_BOTTOM_BAR_SHOW";
	public static final String FLAG_BOTTOM_BAR_HIDDEN = "FLAG_BOTTOM_BAR_HIDDEN";
	public static final String FLAG_INIT = "HomeActivity_INIT";
	public static final String FLAG_WEIBO = "HomeActivity_WEIBO";
	public static final String FLAG_BACK_LOGIN = "HomeActivity_BACK_LOGIN";
	public static final String  BROADCAST_HEADIMG_UPLOAD = "BROADCAST_HEADIMG_UPLOAD";
	private static final String TAG = "HomeActivity";
	private static final String CDR_KEY_SHOOTING_CAN = "shooting_can";
	private static final boolean ISDEBUG = true;
	private static final int START_VIDEO_RECORDING = 0xff000001;
	private boolean isOnRecorderButton = false;

	private Menu myMenu;
	private int myMenuSettingTag;

	private TabHost mHost;
	private RelativeLayout bottomBar;

	private Intent mMBlogIntent;
	private Intent mMoreIntent;
	private Intent mDiscoverIntent; // 发现标签
	private Intent mSearchIntent;
	private Intent mFriendsIntent;

	private TextView unreadMsgView;
	private TextView unreadZoneView;

	private Button mHomeDiaryButton;
	private Button mHomeCameraButton;
	private Button mHomeMicButton;
	private Button mHomeFriendsButton;
	private FrameLayout commontMidFrameLayout;
	private FrameLayout recordMidFrameLayout;
	private FrameLayout cameraMidFrameLayout;

	private Button homePirvatePic;
	private Button homeRecordStart;
//	private Button homeRecordPause;
	private TextView homeRecordTime;

	private AccountInfo accountInfo;
	private LoginSettingManager lsm;
	private ContactManager attentionContactManager;
	private ContactManager blacklistContactManager;
	private FriendsFunsManager friendsFunsManager;
	private OfficialUseridsManager officialUseridsManager;
	private boolean isLongClick = false;
	private BroadcastReceiver mBroadcastReceiver_exit;
	private Boolean isSafeOn = false;
	private ArrayList<WrapUser> wrapUserList = new ArrayList<WrapUser>();
	/*private static final int myMenuResources[] = {
			R.menu.homeactivity_tab1_menu, R.menu.homeactivity_tab2_menu,
			R.menu.homeactivity_tab3_menu };*/
	public static final String ACTION_HOMEACTIVITY_EXIT = "ACTION_HOMEACTIVITY_EXIT";
	public static String ATTENTION_LIST_CHANGE = "ATTENTION_LIST_CHANGE";
	public static String BLACK_LIST_CHANGE = "BLACK_LIST_CHANGE";

	private String userID;
	private String videoDiaryUUID;
	private DiaryManager diaryManager;
	private Handler handler;
	private ZTimerTask task;
	
    public boolean isRecogniseDone = false;
    private boolean isRecording = false;
    private boolean isVideoRecording = false;
    private ExtAudioRecorder ear;
    private XVideoRecorder mediaRecorder;
    public static String RECORD_FILE_PATH = "/home_activity_long_record_";
    public static String SDCARD_PATH = Environment.getExternalStorageDirectory().getPath();
    
    private long recordDuration = 0;
    public static final int HANDLER_RECORD_DURATION_UPDATE_MSG = 0x87654001;
    public static final int HANDLER_SHORT_RECORD_STOP_TIME_DELAY = 0x87654011;
//    private VolumeStateView vsv;
    private CountDownView shortRecView;
    private long recordStartTime = 0;
    private long currentTime = 0;// 录音时的当前时间
    private long pauseTime = 0;// 暂停录音时的时间
    private long videoShootTotalMillis;
    
    private HashMap<String, String> recordMap = new HashMap<String, String>();// diaryUuid--path
    private HashMap<String, String> recordDurationMap = new HashMap<String, String>();// diaryUuid--duration
    private HashMap<String, String> diaryAttachMap = new HashMap<String, String>();// diaryUUID--attachUUID
    private HashMap<String, String> audioDiaryMap = new HashMap<String, String>();// audioID--diaryUUID
    private HashMap<String, String> attachLevelMap = new HashMap<String, String>();// diaryUUID--attachLevle
    private HashMap<String, String> audioTextMap = new HashMap<String, String>();// audioID--soundText
    private HashMap<String, String> diaryAudioMap = new HashMap<String, String>();// diaryUUID--audioID
    private HashMap<String, String> diaryTextAttachMap = new HashMap<String, String>();//diaryUUID--TextAttachUUID
    private HashMap<String, TempDiaryWrapper> diaryWrappers;
    private HashMap<String,GsonRequest2.createStructureRequest> recordCreateStructureMap = new HashMap<String, GsonRequest2.createStructureRequest>();
    private List<TempDiaryWrapper> diarys;
	private LinearLayout nomalBottom;
	private RelativeLayout traslateFullCover;
	private FrameLayout recordButton;
	
	private ImageView message_count; //私信提醒
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_home_main);
			
		// ~~~~~~~~~~~~ 初始化
		this.mMBlogIntent = new Intent(this, HomepageMyselfDiaryActivity.class);
		//this.mSearchIntent = new Intent(this, VideoEditActivity.class);
		this.mDiscoverIntent = new Intent(this, DiscoverMainActivity.class);
		this.mMoreIntent = new Intent(this, DiscoverMainActivity.class);
		this.mFriendsIntent = new Intent(this, FriendsActivity.class);
		// this.mMBlogIntent = new Intent(this, HomepageActivity.class);
		// this.mSearchIntent = new Intent(this, HomepageActivity.class);
		// this.mDiscoverIntent = new Intent(this, HomepageActivity.class);
		// this.mMoreIntent = new Intent(this, HomepageActivity.class);
				
		initRadios();
		initView();
		setupIntent();
		//
		Bundle bundle = getIntent().getExtras();
		if (bundle != null) {
	        JPushInterface.resumePush(MainApplication.getAppInstance());
	        
			boolean isNewLogin = bundle.getBoolean(FLAG_INIT, false);
			boolean isWeiboLogin = bundle.getBoolean(FLAG_WEIBO, false);
			if(isNewLogin){
				//检查accountInfo中的各种字段是否从服务器同步过来，若为空则需要发送request2获取
				//check value
				//then  send request to fetch data from server
				//Toast.makeText(this, "这里加代码！\n先检查accountInfo中的各种字段是否从服务器同步过来，若为空则需要发送request2获取", Toast.LENGTH_LONG).show();
				ActiveAccount aa = ActiveAccount.getInstance(this);
				aa.isForceLogin = false;
				aa.persist();
				String uid = aa.getLookLookID();
				AccountInfo ai = AccountInfo.getInstance(uid);
				ai.persist();
				if(isWeiboLogin){
					//第三方微博首次登录（注册）
					//Toast.makeText(this, "第三方微博首次登录（注册）", Toast.LENGTH_LONG).show();
					if(aa.sns_head_pic!=null && !aa.sns_head_pic.equals("")){
						Requester2.uploadPicture(handler, aa.sns_head_pic, "1", null);
					}
				}
			}
			
			if("Message".equals(bundle.getString("start"))){
				onCheckedChanged(R.id.home_friends);
			}/*else if("Shot".equals(bundle.getString("start"))){
				onCheckedChanged(R.id.home_camera);
			}*/else{
				onCheckedChanged(R.id.home_diary);
			}
			
		}else{
			onCheckedChanged(R.id.home_diary);
		}
		
		SharedPreferences sp=getSharedPreferences(GuideActivity.SP_NAME, MODE_PRIVATE);
		if(sp.getInt(GuideActivity.SP_KEY, 0) == 0){
			Intent intent = new Intent(HomeActivity.this, GuideActivity.class);
			startActivity(intent);
		}
		
		userID = ActiveAccount.getInstance(ZApplication.getInstance()).getUID();
		accountInfo = AccountInfo.getInstance(userID);
		diaryManager = DiaryManager.getInstance();
		lsm = accountInfo.setmanager;

		bottomBar = (RelativeLayout) findViewById(R.id.rl_bottom);
		unreadMsgView = (TextView) findViewById(R.id.tv_unread_msg);
		unreadZoneView = (TextView) findViewById(R.id.tv_unread_zone);

		commontMidFrameLayout = (FrameLayout) findViewById(R.id.commont_mid_fl);
		recordMidFrameLayout = (FrameLayout) findViewById(R.id.record_mid_fl);
		cameraMidFrameLayout = (FrameLayout) findViewById(R.id.camera_mid_fl);
		
		recordButton = (FrameLayout) findViewById(R.id.record_mid_fl);
		traslateFullCover = (RelativeLayout) findViewById(R.id.traslate_full_cover);
		traslateFullCover.setOnTouchListener(new OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				return true;
			}
		});
		
		nomalBottom = (LinearLayout) findViewById(R.id.nomal_bottom);
		handler = new Handler(this);
		try {
			// Requester.requestMessageCount(myHandler);
		} catch (Exception e) {
			e.printStackTrace();
		}

		//设置uid到imageLoader
		//ImageLoader.updateUID();
		
		UniversalImageLoader.initImageLoader(MainApplication.getAppInstance(), ActiveAccount.getInstance(this).getLookLookID());
	
		//ZToast.showShort("Account:" + ActiveAccount.getInstance(this).toString());
		Log.e(TAG, ActiveAccount.getInstance(this).toString());

		IntentFilter filter_exit = new IntentFilter(FLAG_CLOSE_ACTIVITY);
		// for test
		// setMsgUnreadCount("30");
		// setZoneUnreadCount("99");
		IntentFilter filter = new IntentFilter();
		filter.addAction(FLAG_BOTTOM_BAR_SHOW);
		filter.addAction(FLAG_BOTTOM_BAR_HIDDEN);
		registerReceiver(mBroadcastReceiver, filter);
		
		mBroadcastReceiver_exit = new BroadcastReceiver() {

			@Override
			public void onReceive(Context context, Intent intent) {
				//HomeActivity.this.unregisterReceiver(this);
				exitHomeActivity();
			}
		};
		
	
		ear = ExtAudioRecorder.getInstanse(false, 2, 1);
		
		registerReceiver(mBroadcastReceiver_exit, filter_exit);
		
		LocalBroadcastManager.getInstance(this).registerReceiver(
				mMessageReceiver,
				new IntentFilter(ExtAudioRecorder.AUDIO_RECORDER_MSG));
		
		LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver,
			      new IntentFilter(QISR_TASK.QISR_RESULT_MSG));
		
		Log.d(TAG,"************* isInitService =" + InitService.ISINIT);
		if (!InitService.ISINIT) {
//			startService(new Intent(this, CommonService.class));
//			startService(new Intent(this, PrivateMessageService.class));
//			startService(new Intent(this, DiarySyncService.class));
//			//请求标签列表
//			Requester2.requestTagList(handler, "");
			startService(new Intent(this,InitService.class));
		}
		
		
		OfflineTaskManager.getInstance().init(getApplicationContext());
		//请求赞日记ID列表
		Requester2.forwardDiaryIDList(handler, "", userID);
		//请求收藏日记ID列表
		Requester2.listCollectDiaryid(handler, "", userID);
		
		
		//获取官方用户列表
		Requester2.getOfficialUserids(handler);
		
		attentionContactManager = accountInfo.attentionContactManager;
		blacklistContactManager = accountInfo.blackListContactManager;
		officialUseridsManager = accountInfo.officialUseridsManager;
		friendsFunsManager = accountInfo.friendsFunsManager;
		
		Requester2.requestAttentionList(handler, "",
				ActiveAccount.getInstance(ZApplication.getInstance())
						.getLookLookID(), "50");
		Requester2.requestMyFansList(handler, "", userID);
		
		Requester2.myBlacklist(handler,"",ActiveAccount.getInstance(ZApplication.getInstance())
						.getLookLookID(),"50");
		
		CRMRequester.checkVersion(handler);
		
		//umeng
		UmengclickAgentWrapper.setDebugMode(true);
		UmengclickAgentWrapper.updateOnlineConfig(this);
		diaryWrappers = new HashMap<String, VideoShootActivity.TempDiaryWrapper>();
		
		diarys = new ArrayList<TempDiaryWrapper>();
		
		message_count = (ImageView) findViewById(R.id.message_count);
		LocalBroadcastManager.getInstance(this).registerReceiver(myReceiver,
			      new IntentFilter(PrivateMessageService.FLAG_MESSAGE_DATA_UPDATE));
		if (null != accountInfo && null != accountInfo.privateMsgManger) {
			if(accountInfo.privateMsgManger.getUnReadNum() != 0){
				message_count.setVisibility(View.VISIBLE);
			}else if(message_count.getVisibility() == View.VISIBLE){
				message_count.setVisibility(View.GONE);
			}
		}
	}
	
	

/*	private Handler myHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			if(null==msg.obj){
				Log.e(TAG, "msg.obj is null what="+msg.what);
				return;
			}
			switch (msg.what) {
			case Requester2.RESPONSE_TYPE_TAGLIST:{
				taglistResponse res=(taglistResponse) msg.obj;
				if("0".equals(res.status)){
					if(res.tags!=null&&res.tags.length>0){
						diaryManager.addTagList(res.tags);
					}else{
						Log.e(TAG, "res.diaries is null");
					}
				}else{
					Log.e(TAG, "RESPONSE_TYPE_TAGLIST status is "
							+ res.status);
				}
				break;}
			case Requester2.RESPONSE_TYPE_LIST_COLLECT_DIARYID:{
				listCollectDiaryidResponse res=(listCollectDiaryidResponse) msg.obj;
				if("0".equals(res.status)){
					if(res.diarieids!=null&&res.diarieids.length>0){
						diaryManager.getCollectDiariesIDList().clear();
						diaryManager.getCollectDiariesIDList().addAll(Arrays.asList(res.diarieids));
					}else{
						Log.e(TAG, "res.diaries is null");
					}
				}else{
					Log.e(TAG, "RESPONSE_TYPE_LIST_COLLECT_DIARYID status is "
							+ res.status);
				}
				break;}
			case Requester2.RESPONSE_TYPE_FORWARD_DIARY_ID:
				forwardDiaryIDResponse res=(forwardDiaryIDResponse) msg.obj;
				if("0".equals(res.status)){
					if(res.diaries!=null&&res.diaries.length>0){
						diaryManager.getPraisedDiariesIDList().clear();
						diaryManager.getPraisedDiariesIDList().addAll(Arrays.asList(res.diaries));
					}else{
						Log.e(TAG, "res.diaries is null");
					}
				}else{
					Log.e(TAG, "RESPONSE_TYPE_FORWARD_DIARY_ID status is "
							+ res.status);
				}
				break;
			default:
				break;
			}
			super.handleMessage(msg);
		}
	};*/

	// 然后onCreateOptionsMenu(Menu menu) 方法中通过MenuInflater过滤器动态加入MENU
	/*@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
		// Hold on to this
		myMenu = menu;
		myMenu.clear();// 清空MENU菜单
		// Inflate the currently selected menu XML resource.
		MenuInflater inflater = getMenuInflater();
		// protected void onCreate(Bundle savedInstanceState) {
		// // TODO Auto-generated method stub
		// super.onCreate(savedInstanceState);
		//
		// setContentView(R.layout.activity_home_main);
		// // ~~~~~~~~~~~~ 初始化
		// this.mMBlogIntent = new Intent(this, TestActivity.class);
		// this.mSearchIntent = new Intent(this, VideoEditActivity.class);
		// this.mInfoIntent = new Intent(this, TestSNSActivity.class);
		// this.mUserInfoIntent = new Intent(this, VideoEditActivity.class);
		// this.mMoreIntent = new Intent(this, TestActivity.class);

		// 从TabActivity这里获取一个MENU过滤器
		switch (myMenuSettingTag) {
		case 1:
			inflater.inflate(myMenuResources[0], menu);
			// 动态加入数组中对应的XML MENU菜单
			break;
		case 2:
			inflater.inflate(myMenuResources[1], menu);
			break;
		case 3:
			inflater.inflate(myMenuResources[2], menu);
			break;
		case 4:
			inflater.inflate(myMenuResources[3], menu);
			break;
		default:
			break;
		}
		return super.onCreateOptionsMenu(menu);
	}*/

	private void initView() {

		homePirvatePic = (Button) findViewById(R.id.home_private_pic);
		homeRecordStart = (Button) findViewById(R.id.home_record_start);
//		homeRecordPause = (Button) findViewById(R.id.home_record_pause);
		homeRecordTime = (TextView) findViewById(R.id.home_record_time);

		homePirvatePic.setOnClickListener(this);
//		homeRecordPause.setOnClickListener(this);
		homeRecordStart.setOnClickListener(this);
	}

	/**
	 * 初始化底部按钮
	 */
	private void initRadios() {

		// ((Button) findViewById(R.id.radio_button0))
		// .setOnCheckedChangeListener(this);

		mHomeDiaryButton = (Button) findViewById(R.id.home_diary);
		mHomeCameraButton = (Button) findViewById(R.id.home_camera);
		mHomeMicButton = (Button) findViewById(R.id.home_mic);
		mHomeFriendsButton = (Button) findViewById(R.id.home_friends);

		mHomeDiaryButton.setOnClickListener(this);
		mHomeCameraButton.setOnClickListener(this);
		mHomeMicButton.setOnClickListener(this);
		mHomeFriendsButton.setOnClickListener(this);

		mHomeDiaryButton.setOnLongClickListener(this);
		mHomeCameraButton.setOnLongClickListener(this);
		mHomeMicButton.setOnLongClickListener(this);
		mHomeFriendsButton.setOnLongClickListener(this);
		
		mHomeMicButton.setOnTouchListener(this);
	}

	@Override
	public boolean onLongClick(View v) {

		switch (v.getId()) {
	/*	case R.id.home_diary:
			if (lsm.getGesturepassword() == null || !lsm.getSafeIsOn()) {
				Toast.makeText(this, "长按处理保险箱", Toast.LENGTH_LONG).show();
				mHomeDiaryButton.setSelected(true);
				mHomeCameraButton.setSelected(false);
				mHomeMicButton.setSelected(false);
				mHomeFriendsButton.setSelected(false);

				commontMidFrameLayout.setVisibility(View.VISIBLE);
				recordMidFrameLayout.setVisibility(View.GONE);
				cameraMidFrameLayout.setVisibility(View.GONE);
				Intent in = null;
				if(lsm.getBinding_type(LoginSettingManager.BINDING_TYPE_PHONE, LoginSettingManager.BINDING_INFO_POINTLESS) != null){
					if(lsm.getGesturepassword() == null || lsm.getGesturepassword().equals("")){
						in = new Intent(this, SettingToCreateGestureActivity.class);
						startActivity(in);
					}else if(!lsm.getSafeIsOn()){
						in = new Intent(this, SettingGesturePwdActivity.class);
						in.putExtra("count", 0);
						startActivity(in);
					}
				}else{
					Prompt.Alert(this, "请先进行手机帐号绑定");
				}
			} else {
				Intent intent = new Intent(
						HomepageMyselfDiaryActivity.SAVE_BOX_MODE_CHANGE);
				LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
				if(!isSafeOn){
					isSafeOn = true;
					Prompt.Alert(this, "已经开启保险箱模式");
				}else{
					isSafeOn = false;
					Prompt.Alert(this, "已经关闭保险箱模式");
				}
			}
			return true;*/
		case R.id.home_camera:
			if (LowStorageChecker.check(this)) {
				HomepageMyselfDiaryActivity.stopTackViewAudioPlayer();
				
				commontMidFrameLayout.setVisibility(View.GONE);
				recordMidFrameLayout.setVisibility(View.GONE);
				cameraMidFrameLayout.setVisibility(View.VISIBLE);
				
//				mHomeDiaryButton.setSelected(false);
//				mHomeCameraButton.setSelected(true);
//				mHomeMicButton.setSelected(false);
//				mHomeFriendsButton.setSelected(false);
				
				homePirvatePic.setBackgroundResource(R.drawable.recording_animation_drawable);
				homePirvatePic.setClickable(false); // 禁止再次点击(需要等待3秒后再允许点击)
				AnimationDrawable animationDrawable = (AnimationDrawable) homePirvatePic.getBackground();
				animationDrawable.start();
				
				XEffects effects = null;
				if (PluginUtils.isPluginMounted()) {
					effects = new XEffects();
					mediaRecorder = new XVideoRecorder(this, effects, this, CamaraMode.DEF_VIDEO_COMMON);
				} else {
					mediaRecorder = new XVideoRecorder(this, effects, this, CamaraMode.DEF_VIDEO_HEIGHT);
				}
				
				LinearLayout privateVideoShell = (LinearLayout) findViewById(R.id.private_video_shell);
				privateVideoShell.removeAllViews();
				LayoutParams layoutParams = new ViewGroup.LayoutParams(64, 91);//LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT
				privateVideoShell.addView(mediaRecorder.getXSurfaceView(), layoutParams);
				privateVideoShell.invalidate();
				handler.sendEmptyMessage(START_VIDEO_RECORDING);
				handler.postDelayed(new Runnable() {
					@Override
					public void run() {
						homePirvatePic.setClickable(true);
					}
				}, 3000);
			}
			return true;
		case R.id.home_mic:
			HomepageMyselfDiaryActivity.stopTackViewAudioPlayer();
			if (LowStorageChecker.check(this, null, R.string.cancel_record_audio)) {
//				Toast.makeText(this, "长按区分短录音", Toast.LENGTH_LONG).show();
				CmmobiClickAgentWrapper.onEvent(this, "rec_or_can", "1");
				Log.d(TAG,"LongClick recorder");
				
				if(!isSdcardMountedAndWritable()) {
					Prompt.Alert(this, "Sdcard不可用，无法录音");
					break;
				}
	
				isLongClick  = true;
				isRecording = true;
				isRecogniseDone = false;
				isOnRecorderButton = true;
				recordDuration = 0;
				long currentTime = TimeHelper.getInstance().now();
				String audioID = String.valueOf(currentTime);
				String currentDiaryUUID = UUID.randomUUID().toString().replace("-", "");
				currentUUID = currentDiaryUUID;
				//String path = SDCARD_PATH + RECORD_FILE_PATH ;
				String path =  Constant.SD_STORAGE_ROOT + "/" + ActiveAccount.getInstance(this).getLookLookID() + "/audio";
				String filePath = Environment.getExternalStorageDirectory() + path + "/" + audioID + "/" + audioID + ".mp4";
				recordMap.put(currentDiaryUUID, filePath);
				audioDiaryMap.put(audioID, currentDiaryUUID);
				diaryAudioMap.put(currentDiaryUUID, audioID);
				handler.sendEmptyMessage(HANDLER_RECORD_DURATION_UPDATE_MSG);
				attachLevelMap.put(currentDiaryUUID, "0");
				
				if (!ear.start(this, audioID, path, false, 3, true)) {
					isRecording = false;
					isLongClick = false;
					Prompt.Alert(this, "无法生成录音文件");
					handler.removeMessages(HANDLER_RECORD_DURATION_UPDATE_MSG);
					break;
				}
				recordStartTime = TimeHelper.getInstance().now();
				
				if (shortRecView == null) {
					shortRecView = new CountDownView(this);
				}
				shortRecView.show();
				
				
//				mHomeDiaryButton.setSelected(false);
				mHomeDiaryButton.setClickable(false);
				mHomeDiaryButton.setLongClickable(false);
//				mHomeCameraButton.setSelected(false);
				mHomeCameraButton.setClickable(false);
				mHomeCameraButton.setLongClickable(false);
//				mHomeMicButton.setSelected(true);
				mHomeMicButton.setClickable(false);
				mHomeMicButton.setLongClickable(false);
//				mHomeFriendsButton.setSelected(false);
				mHomeFriendsButton.setClickable(false);
				mHomeFriendsButton.setLongClickable(false);
				
				traslateFullCover.setVisibility(View.VISIBLE);
			}
			return true;
		}
		return false;
	}
	
	
	
	private void startRecording() {
		long currentTime = TimeHelper.getInstance().now();
		String audioID = String.valueOf(currentTime);
		
		if (audioDiaryMap.containsKey(audioID) || isRecording) {
			return;
		}
		String currentDiaryUUID = UUID.randomUUID().toString().replace("-", "");
		isRecording = true;
		recordDuration = 0;
		currentUUID = currentDiaryUUID;
		//String path = SDCARD_PATH + RECORD_FILE_PATH ;
		String path =  Constant.SD_STORAGE_ROOT + "/" + ActiveAccount.getInstance(this).getLookLookID() + "/audio";
		String filePath = Environment.getExternalStorageDirectory() + path + "/" + audioID + "/" + audioID + ".mp4";
		recordMap.put(currentDiaryUUID, filePath);
		audioDiaryMap.put(audioID, currentDiaryUUID);
		diaryAudioMap.put(currentDiaryUUID, audioID);
		attachLevelMap.put(currentDiaryUUID, "1");
		handler.sendEmptyMessage(HANDLER_RECORD_DURATION_UPDATE_MSG);
		
		if (!ear.start(this, audioID, path, true, 3)) {
			Prompt.Alert(this, "无法生成录音文件");
			isRecording = false;
			handler.removeMessages(HANDLER_RECORD_DURATION_UPDATE_MSG);
			recordButton.setVisibility(View.GONE);
			commontMidFrameLayout.setVisibility(View.VISIBLE);
		}
		recordStartTime = TimeHelper.getInstance().now();
		Log.d(TAG,"AAA startRecording audioID = " + audioID + " currentUUID = " + currentUUID + "recordStartTime = " + recordStartTime);
		
	}

	private String currentUUID = "";
	
	/**
	 * 底部菜单栏，仅仅是UI恢复常态
	 */
	private void regainBottomView() {
		commontMidFrameLayout.setVisibility(View.VISIBLE);
		recordMidFrameLayout.setVisibility(View.GONE);
		cameraMidFrameLayout.setVisibility(View.GONE);
	} 
	
	private void stopVideoRecording() {
		if (isVideoRecording) {
			if (task != null) {
				task.stop();
				task = null;
			}
			isVideoRecording = false;
			mediaRecorder.stop();
			mediaRecorder = null;
			videoShootTotalMillis = TimeHelper.getInstance().now() - videoShootTotalMillis;
			
			homePirvatePic.setBackgroundResource(R.drawable.btn_activity_home_main_private_pic);
			
			if (!PluginUtils.isPluginMounted()) {
				TempDiaryWrapper wrapper = diaryWrappers.get(videoDiaryUUID);
				wrapper.mediaCatchOK = true;
				createVideoDiary(wrapper);
			}
			
			regainBottomView();
			mHomeCameraButton.setEnabled(false);
			handler.postDelayed(new Runnable() {
				
				@Override
				public void run() {
					mHomeCameraButton.setEnabled(true);
				}
			}, Config.VIDEO_SHOOT_BUTTON_BLOCKING_MILLIS);
		}
	}
	
	public static long lastClickTime=0;
	@Override
	public void onClick(View v) {

		switch (v.getId()) {
		case R.id.home_private_pic:
			stopVideoRecording();
			break;
		case R.id.home_record_start:
			if (!isRecording || recordDurationMap.containsKey(currentUUID)) {
				return;
			}
			recordButton.setVisibility(View.GONE);
			commontMidFrameLayout.setVisibility(View.VISIBLE);
			LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
			params.topMargin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
					-26, this.getResources().getDisplayMetrics());
			bottomBar.setLayoutParams(params);
			Log.d(TAG,"AAA home_record_start diaryId = " + currentUUID + " duration = " + recordDuration);
			
			isRecording = false;
			recordDuration = 0;
			handler.removeMessages(HANDLER_RECORD_DURATION_UPDATE_MSG);
			long recordEndTime = TimeHelper.getInstance().now();
			long stopduration = recordEndTime - recordStartTime;
			ear.stop();
			
			recordDurationMap.put(currentUUID, String.valueOf((recordEndTime - recordStartTime + 999) / 1000));
			Log.d(TAG,"recordStartTime = " + recordStartTime + " recordEndTime = " + recordEndTime);
//			recordMidFrameLayout.setVisibility(View.GONE);
			break;
//		case R.id.home_record_pause:
//			/*if (ear.getState() == State.RECORDING) {
//				Log.d(TAG,"home_record_pause in PAUSE");
//				handler.removeMessages(HANDLER_RECORD_DURATION_UPDATE_MSG);
//				pauseTime = TimeHelper.getInstance().now();
//				ear.pause();
//			} else if(ear.getState() == State.PAUSE) {
//				Log.d(TAG,"home_record_pause in RESUME");
//				ear.resume();
//				long delayTime = pauseTime - currentTime;
//				Log.d(TAG,"delayTime = " + delayTime);
//				handler.sendEmptyMessageDelayed(HANDLER_RECORD_DURATION_UPDATE_MSG, delayTime);
//			}*/
//			Toast.makeText(getApplicationContext(), "不提供暂停功能", Toast.LENGTH_SHORT).show();
//			break;
		case R.id.btn_recorder:
			CmmobiClickAgentWrapper.onEvent(this, "rec_or_can", "1");
			if(!isSdcardMountedAndWritable()) {
				Prompt.Alert(this, "Sdcard不可用，无法录音");
				return;
			}
//			mHomeDiaryButton.setSelected(false);
//			mHomeCameraButton.setSelected(false);
//			mHomeMicButton.setSelected(true);
//			mHomeFriendsButton.setSelected(false);

			LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
			param.topMargin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
					-40, this.getResources().getDisplayMetrics());
			bottomBar.setLayoutParams(param);
			commontMidFrameLayout.setVisibility(View.GONE);
//			recordMidFrameLayout.setVisibility(View.VISIBLE);
			recordButton.setVisibility(View.VISIBLE);
			cameraMidFrameLayout.setVisibility(View.GONE);
			
			startRecording();
			if(portraitMenu!=null) {
				portraitMenu.dismiss();
			}
			break;
		case R.id.btn_cancel:
			CmmobiClickAgentWrapper.onEvent(this, "rec_or_can", "2");
			if(portraitMenu!=null) {
				portraitMenu.dismiss();
			}
			break;

		}
		onCheckedChanged(v.getId());
	}
	
	private String convertNum2TimeFormat(long time) {
		int minute = (int) (time / 60);
		int second = (int) (time % 60);
		String minuteStr = "";
		String secStr = "";
		if (minute < 10) {
			minuteStr = "0" + minute;
		} else {
			minuteStr = "" + minute;
		}
		
		if (second < 10) {
			secStr = "0" + second;
		} else {
			secStr = "" + second;
		}
		return "Recording " + minuteStr + " : " + secStr;
	}
	
	public static String convertNum2TimeFormat2(long time) {
		if (time > 0) {
			time = time - 1;
		}
		String durationStr = "";
		int second = (int) (time);
		int hour = second / 3600;
		int minute = (second % 3600) / 60;
		second = second - hour * 3600 - minute * 60;
		if (hour != 0) {
			durationStr = "" + hour + "h" + minute + "′" + second + "″";
		} else if (minute != 0){
			durationStr = "" + minute + "′" + second + "″";
		} else {
			durationStr = "" + second + "″";
		}
		return durationStr;
	}
	
	private void generateLocalRecordDiary(final String diaryUUID,final String attachLevel) {
		Attachs[] attachs = new Attachs[1];
		attachs[0] = new Attachs();
		attachs[0].attachid = "";
		attachs[0].attachuuid = UUID.randomUUID().toString().replace("-", "");
		attachs[0].attach_type = "2";
		attachs[0].level = attachLevel;
		attachs[0].audio_type = "1";
		if (CommonInfo.getInstance().myLoc != null) {
			attachs[0].attach_latitude = String.valueOf(CommonInfo.getInstance().myLoc.latitude);
			attachs[0].attach_logitude = String.valueOf(CommonInfo.getInstance().myLoc.longitude);
		}
		attachs[0].content = "";
		attachs[0].suffix = ".mp4";
		attachs[0].Operate_type = "1";
		
		diaryAttachMap.put(diaryUUID, attachs[0].attachuuid);
		Log.d(TAG,"AAA  diaryUUID = " + diaryUUID);
		String longitude = "";
		String latitude = "";
		if (CommonInfo.getInstance().myLoc != null) {
			longitude = String.valueOf(CommonInfo.getInstance().myLoc.longitude);
			latitude = String.valueOf(CommonInfo.getInstance().myLoc.latitude);
		}
		String addressCode = "";
		AccountInfo account = AccountInfo.getInstance(userID);
		MyWeather weather = account.myWeather;
		if (weather != null) {
			addressCode = weather.addrCode;
		}
		
		GsonRequest2.createStructureRequest createStructure = Requester2.createStructure(handler, "", diaryUUID, "1", "", "",
				longitude,latitude,
				"",CommonInfo.getInstance().positionStr,longitude,latitude,String.valueOf(TimeHelper.getInstance().now()),
				addressCode,attachs);
		recordCreateStructureMap.put(diaryUUID, createStructure);
		
	}

	public static boolean isCameraStart;
	/**
	 * 切换模块
	 */
	public void onCheckedChanged(int id) {
		if(isCameraStart)return;
		if(System.currentTimeMillis()-lastClickTime<1000)return;
		Log.d(TAG, "System.currentTimeMillis()-lastClickTime="+(System.currentTimeMillis()-lastClickTime));
		lastClickTime=System.currentTimeMillis();
		switch (id) {
		case R.id.home_diary:
			HomepageMyselfDiaryActivity.stopTackViewAudioPlayer();
			this.mHost.setCurrentTabByTag("mblog_tab");

			mHomeDiaryButton.setSelected(true);
			mHomeCameraButton.setSelected(false);
			mHomeMicButton.setSelected(false);
			mHomeFriendsButton.setSelected(false);
			CmmobiClickAgentWrapper.onEvent(this, "ma_toggle_b");
			break;
		case R.id.home_camera:
			if (LowStorageChecker.check(this)) {
				HomepageMyselfDiaryActivity.stopTackViewAudioPlayer();
				if (mediaRecorder != null) {
					mediaRecorder.release();
				}
				mHomeCameraButton.setEnabled(false);
				isCameraStart=true;
				
				Intent intent = new Intent();
				intent.setClass(HomeActivity.this, VideoShootActivity.class);
				startActivity(intent);
				handler.postDelayed(new Runnable() {
					
					@Override
					public void run() {
						mHomeCameraButton.setEnabled(true);
					}
				}, 2000);
			}
			break;
		case R.id.home_mic:
//			this.mHost.setCurrentTabByTag("search_tab");
//			clearMsgIcon();
//			Toast.makeText(this, "长按区分长录音", Toast.LENGTH_LONG).show();
			
			/*mHomeDiaryButton.setSelected(false);
			mHomeCameraButton.setSelected(false);
			mHomeMicButton.setSelected(true);
			mHomeFriendsButton.setSelected(false);

			commontMidFrameLayout.setVisibility(View.INVISIBLE);
//			recordMidFrameLayout.setVisibility(View.VISIBLE);
			traslateFullCover.setVisibility(View.VISIBLE);
			cameraMidFrameLayout.setVisibility(View.GONE);
			
			startRecording();*/
			HomepageMyselfDiaryActivity.stopTackViewAudioPlayer();
			if (LowStorageChecker.check(this, null, R.string.cancel_record_audio)) {
				showRecorderChoice();
			}
			break;
		case R.id.home_friends:
			HomepageMyselfDiaryActivity.stopTackViewAudioPlayer();
			this.mHost.setCurrentTabByTag("friends_tab");
			clearZoneIcon();

			mHomeDiaryButton.setSelected(false);
			mHomeCameraButton.setSelected(false);
			mHomeMicButton.setSelected(false);
			mHomeFriendsButton.setSelected(true);
			break;
		// case R.id.radio_button4:
		// break;
		}

	}
	
	@Override
	public boolean onTouch(View v, MotionEvent event) {
		if (isLongClick) {
			switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN:
				break;
			case MotionEvent.ACTION_UP:
//				Toast.makeText(getApplicationContext(), "这里处理长按抬起事件", Toast.LENGTH_SHORT).show();
				if (!isLongClick) {
					break;
				}
				
				long shortRecordEndTime = TimeHelper.getInstance().now();
				long stopduration = shortRecordEndTime - recordStartTime;
				if (stopduration >= 2000) {
					/*ear.stop();
					handler.removeMessages(HANDLER_RECORD_DURATION_UPDATE_MSG);
					Log.d(TAG,"MotionEvent.ACTION_UP isOnRecorderButton = " + isOnRecorderButton);
					if (isOnRecorderButton) {
	//					long shortRecordEndTime = TimeHelper.getInstance().now();
						recordDurationMap.put(currentUUID, String.valueOf((shortRecordEndTime - recordStartTime + 999) / 1000));
						Log.d(TAG,"shortRecordEndTime = " + shortRecordEndTime + " recordStartTime = " + recordStartTime);
					} else {
						CmmobiClickAgentWrapper.onEvent(this, "rec_or_can", "2");
					}
	//				vsv.dismissView();
					shortRecView.dismiss();
					mHomeDiaryButton.setSelected(false);
					mHomeDiaryButton.setClickable(true);
					mHomeDiaryButton.setLongClickable(true);
					mHomeCameraButton.setSelected(false);
					mHomeCameraButton.setClickable(true);
					mHomeCameraButton.setLongClickable(true);
					mHomeMicButton.setSelected(false);
					mHomeMicButton.setClickable(true);
					mHomeMicButton.setLongClickable(true);
					mHomeFriendsButton.setSelected(false);
					mHomeFriendsButton.setClickable(true);
					mHomeFriendsButton.setLongClickable(true);
					traslateFullCover.setVisibility(View.GONE);*/
					stopShortRecord();
				} else {
					handler.sendEmptyMessageDelayed(HANDLER_SHORT_RECORD_STOP_TIME_DELAY, 2000 - stopduration);
				}
				
				break;
			case MotionEvent.ACTION_MOVE:
				if (event.getY() < 0) {
					isOnRecorderButton = false;
				} else {
					isOnRecorderButton = true;
				}
				break;
			}
		}
		
		return false;
	}
	
	private void stopShortRecord() {
		isLongClick = false;
		isRecording = false;
		recordDuration = 0;
		ear.stop();
		handler.removeMessages(HANDLER_RECORD_DURATION_UPDATE_MSG);
		Log.d(TAG,"MotionEvent.ACTION_UP isOnRecorderButton = " + isOnRecorderButton);
		if (isOnRecorderButton) {
		} else {
			CmmobiClickAgentWrapper.onEvent(this, "rec_or_can", "2");
		}
		shortRecView.dismiss();
		runOnUiThread(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
//				mHomeDiaryButton.setSelected(false);
				mHomeDiaryButton.setClickable(true);
				mHomeDiaryButton.setLongClickable(true);
//				mHomeCameraButton.setSelected(false);
				mHomeCameraButton.setClickable(true);
				mHomeCameraButton.setLongClickable(true);
				
				mHomeMicButton.setClickable(true);
				mHomeMicButton.setLongClickable(true);
				mHomeMicButton.setSelected(false);
				mHomeMicButton.setPressed(false);
				mHomeMicButton.clearFocus();
//				mHomeFriendsButton.setSelected(false);
				mHomeFriendsButton.setClickable(true);
				mHomeFriendsButton.setLongClickable(true);
				traslateFullCover.setVisibility(View.GONE);		
			}
		});

	}

	private void setupIntent() {
		this.mHost = getTabHost();
		TabHost localTabHost = this.mHost;

		localTabHost.addTab(buildTabSpec("mblog_tab", "home",
				R.drawable.effect_dksg, this.mMBlogIntent));

		localTabHost.addTab(buildTabSpec("message_tab", "news",
				R.drawable.effect_fydz, this.mDiscoverIntent));

		localTabHost.addTab(buildTabSpec("search_tab", "search",
				R.drawable.effect_gtf, this.mSearchIntent));

		// localTabHost.addTab(buildTabSpec("more_tab", "more",
		// R.drawable.effect_tujing, this.mMoreIntent));

		localTabHost.addTab(buildTabSpec("friends_tab", "more",
				R.drawable.jiao_pengyouquan_1, this.mFriendsIntent));

	}

	private TabHost.TabSpec buildTabSpec(String tag, String Label, int resIcon,
			final Intent content) {
		return this.mHost.newTabSpec(tag)
				.setIndicator(Label, getResources().getDrawable(resIcon))
				.setContent(content);
	}

	private void setMsgUnreadCount(String count) {
		unreadMsgView.setText(count);
		unreadMsgView.setVisibility(View.VISIBLE);
	}

	private void clearMsgIcon() {
		unreadMsgView.setVisibility(View.INVISIBLE);
	}

	private void setZoneUnreadCount(String count) {
		unreadZoneView.setText(count);
		unreadZoneView.setVisibility(View.VISIBLE);
	}

	private void clearZoneIcon() {
		unreadZoneView.setVisibility(View.INVISIBLE);
	}

	BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			String action=intent.getAction();
			if(FLAG_BOTTOM_BAR_SHOW.equals(action)){//显示底栏
				bottomBar.setVisibility(View.VISIBLE);
			}
			if(FLAG_BOTTOM_BAR_HIDDEN.equals(action)){//隐藏底栏
				bottomBar.setVisibility(View.GONE);
			}
		}
	};


	@Override
	public boolean dispatchKeyEvent(KeyEvent event) {
		// TODO Auto-generated method stub

		if ((event.getAction() == KeyEvent.ACTION_DOWN && event.getKeyCode() == KeyEvent.KEYCODE_BACK)) {
			// 添加退出提示对话框
			Prompt.Dialog(this, true, "退出提示", "确定退出吗?", new DialogInterface.OnClickListener() {
								public void onClick(
										DialogInterface dialoginterface, int i) {
									exitHomeActivity();
									InitService.ISINIT = false;

								}
							}, new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int id) {
									dialog.cancel();
								}
							});
/*			new AlertDialog.Builder(this)
					.setTitle("退出提示")
					.setCancelable(true)
					// 设置不能通过“后退”按钮关闭对话框
					.setMessage("确定退出吗?")
					.setPositiveButton("是的",
							new DialogInterface.OnClickListener() {
								public void onClick(
										DialogInterface dialoginterface, int i) {
									exitHomeActivity();

								}
							})
					.setNegativeButton("不是",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int id) {
									dialog.cancel();
								}
							}).show();// 显示对话框
*/			return false;
		}

		return super.dispatchKeyEvent(event);
	}
	
	@Override
	public void onResume() {
		super.onResume();
		UmengclickAgentWrapper.onResume(this);
		ActiveAccount.getInstance(this).persist();
    	String UID = ActiveAccount.getInstance(this).getUID();
		if (UID != null) {
			AccountInfo.getInstance(UID).persist();
		}
	}
	
	@Override
	public void onPause() {
		super.onPause();
		if (mediaRecorder != null && mediaRecorder.isRecording()) {
			ZThread.sleep(Config.VIDEO_SHOOT_BUTTON_BLOCKING_MILLIS);
			homePirvatePic.setClickable(true);
			homePirvatePic.performClick();
		}
		
		UmengclickAgentWrapper.onPause(this);
		ActiveAccount.getInstance(this).persist();
    	String UID = ActiveAccount.getInstance(this).getUID();
		if (UID != null) {

			AccountInfo.getInstance(UID).persist();
		}
	}

	@Override
	protected void onStop() {
		Log.d(TAG,"onStop in");
		if (ear != null && ear.isRecording) {
			if (isLongClick) {
				stopShortRecord();
			} else {
				recordButton.setVisibility(View.GONE);
				commontMidFrameLayout.setVisibility(View.VISIBLE);
				LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
				params.topMargin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
						-26, this.getResources().getDisplayMetrics());
				bottomBar.setLayoutParams(params);
				Log.d(TAG,"onStop AAA home_record_start diaryId = " + currentUUID + " duration = " + recordDuration);
				
				isRecording = false;
				recordDuration = 0;
				handler.removeMessages(HANDLER_RECORD_DURATION_UPDATE_MSG);
				ear.stop();
				long recordEndTime = TimeHelper.getInstance().now();
				recordDurationMap.put(currentUUID, String.valueOf((recordEndTime - recordStartTime + 999) / 1000));
			}
		}
		
		super.onStop();
	    String packageName = this.getPackageName();
	    ActivityManager activityManager = (ActivityManager)this.getSystemService(Context.ACTIVITY_SERVICE);
	    List<RunningTaskInfo> appTask = activityManager.getRunningTasks(1);
	    String topActivity = appTask.get(0).topActivity.toString();
	    /*String UID = ActiveAccount.getInstance(this).getUID();
		if (UID != null) {
			AccountInfo.getInstance(UID).persist();
		}*/
	    boolean needWriteInfo = false;
	    if(appTask!=null && appTask.size()>0){
            if(topActivity.contains(packageName) && !topActivity.contains(HomeActivity.this.getClass().getName())){
            	needWriteInfo = false;
            	
            }else{
            	needWriteInfo = true;

            }
            
	    }
	    
	    if(needWriteInfo){
        	String UID = ActiveAccount.getInstance(this).getUID();
    		if (UID != null) {
    			//AccountInfo.getInstance(UID).persist();
    		}
    		CommonInfo ci = CommonInfo.getInstance();
    		ci.persist();
	    }
	}

	@Override
	protected void onDestroy() {
		unregisterReceiver(mBroadcastReceiver);
		unregisterReceiver(mBroadcastReceiver_exit);
		LocalBroadcastManager.getInstance(this).unregisterReceiver(mMessageReceiver);

		super.onDestroy();
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

	private void exitHomeActivity() {
		Intent intent = new Intent(ACTION_HOMEACTIVITY_EXIT);
		ZApplication.getInstance().sendLocalBroadcast(intent);
		finish();
	}
	
/*	private void backToLoginActivity() {
		Intent intent = new Intent(this, LoginMainActivity.class);
		startActivity(intent);
		overridePendingTransition(android.R.anim.fade_in,
				android.R.anim.fade_out);

		finish();
	}*/

	@Override
	public boolean handleMessage(Message msg) {
		switch (msg.what) {
		case Requester2.RESPONSE_TYPE_CREATE_STRUCTURE: // 创建日记管理结构成功，则创建日记结构，添加至本地日记链表并上传日记
			Log.d(TAG,"HomeActivity RESPONSE_TYPE_CREATE_STRUCTURE");
			GsonResponse2.createStructureResponse structureResponse = (createStructureResponse) msg.obj;
			
			TempDiaryWrapper wrapper = diaryWrappers.get(structureResponse.diaryuuid);
			if (wrapper != null) {
				wrapper.requestDiaryOK = true;
				wrapper.response = structureResponse;
				createVideoDiary(wrapper);
			} else {
				createDiaryStructure(structureResponse);
			}
			break;
		case HANDLER_RECORD_DURATION_UPDATE_MSG:
			homeRecordTime.setText(convertNum2TimeFormat(recordDuration));
			currentTime = TimeHelper.getInstance().now();
			if (isLongClick) {
				shortRecView.updateTime(30 - recordDuration);
				if (recordDuration >= 30) {
					Log.d(TAG,"MotionEvent.ACTION_UP isOnRecorderButton = " + isOnRecorderButton);
					isLongClick = false;
					isRecording = false;
					ear.stop();
					if (isOnRecorderButton) {
						long shortRecordEndTime = TimeHelper.getInstance().now();
						recordDurationMap.put(currentUUID, String.valueOf((shortRecordEndTime - recordStartTime + 999) / 1000));
					}
//					vsv.dismissView();
					shortRecView.dismiss();
					
//					mHomeDiaryButton.setSelected(false);
					mHomeDiaryButton.setClickable(true);
					mHomeDiaryButton.setLongClickable(true);
//					mHomeCameraButton.setSelected(false);
					mHomeCameraButton.setClickable(true);
					mHomeCameraButton.setLongClickable(true);
//					mHomeMicButton.setSelected(false);
					mHomeMicButton.setClickable(true);
					mHomeMicButton.setLongClickable(true);
//					mHomeFriendsButton.setSelected(false);
					mHomeFriendsButton.setClickable(true);
					mHomeFriendsButton.setLongClickable(true);
					traslateFullCover.setVisibility(View.GONE);
				}
			}
			recordDuration++;
			
			if (isRecording) {
				handler.sendEmptyMessageDelayed(HANDLER_RECORD_DURATION_UPDATE_MSG, 1000);
			} else {
				recordDuration = 0;
			}
			break;
		case ExtAudioRecorder.HANDLER_AUDIO_RECORDER_DONE:
			Log.d(TAG," ExtAudioRecorder.HANDLER_AUDIO_RECORDER_DONE");
			if(null==msg.obj){
//				LocalBroadcastManager.getInstance(this).unregisterReceiver(mMessageReceiver);
				Log.e(TAG, "msg.obj is null what="+msg.what);
				return false;
			}
			
			String audioID = (String) msg.obj;
			String diaryUUID = audioDiaryMap.get(audioID);
			String attachType = attachLevelMap.get(diaryUUID);
			if (msg.arg1 == 0) {
				return false;
			}
			recordDurationMap.put(diaryUUID,String.valueOf((msg.arg1 + 999) / 1000)); 
			Log.d(TAG,"AAA HANDLER_AUDIO_RECORDER_DONE auidoID = " + audioID + " diaryUUID = " + diaryUUID);
			Log.d(TAG,"attachType = " + attachType + " plugin = " + ExtAudioRecorder.CheckPlugin() 
					+ " isRecogniseDone = " + isRecogniseDone + " isOnRecorderButton = " + isOnRecorderButton);
			if ("1".equals(attachType)) {
//				LocalBroadcastManager.getInstance(this).unregisterReceiver(mMessageReceiver);
				if (diaryUUID != null && !"".equals(diaryUUID)) {
					generateLocalRecordDiary(diaryUUID,attachType);
				}
			} else if ((!ExtAudioRecorder.CheckPlugin() || isRecogniseDone) && isOnRecorderButton) {
//				LocalBroadcastManager.getInstance(this).unregisterReceiver(mMessageReceiver);
				String diaryQisrUUID = audioDiaryMap.get(audioID);
				String attachQisrLevel = attachLevelMap.get(diaryQisrUUID);
				if ("0".equals(attachQisrLevel)) {
					if (diaryQisrUUID != null && !"".equals(diaryQisrUUID)) {
						generateLocalShortRecordDiary(diaryQisrUUID,attachQisrLevel,audioID);
					}
				}
			} 
			break;
		case HANDLER_SHORT_RECORD_STOP_TIME_DELAY:
			stopShortRecord();
			break;
		case Requester2.RESPONSE_TYPE_TAGLIST:
			if(null==msg.obj){
				Log.e(TAG, "msg.obj  is null what="+msg.what);
				return false;
			}
			taglistResponse res=(taglistResponse) msg.obj;
			if("0".equals(res.status)){
				if(res.tags!=null&&res.tags.length>0){
					diaryManager.addTagList(res.tags);
				}else{
					Log.e(TAG, "res.diaries is null");
				}
			}else{
				Log.e(TAG, "RESPONSE_TYPE_TAGLIST status is "
						+ res.status);
			}
			break;
		case Requester2.RESPONSE_TYPE_LIST_COLLECT_DIARYID:
			if(null==msg.obj){
				Log.e(TAG, "msg.obj is null what="+msg.what);
				return false;
			}
			listCollectDiaryidResponse listCollectDiaryidResponse =(listCollectDiaryidResponse) msg.obj;
			if("0".equals(listCollectDiaryidResponse.status)){
				if(listCollectDiaryidResponse.diarieids!=null&&listCollectDiaryidResponse.diarieids.length>0){
					diaryManager.getCollectDiariesIDList().clear();
					diaryManager.getCollectDiariesIDList().addAll(Arrays.asList(listCollectDiaryidResponse.diarieids));
				}else{
					Log.e(TAG, "res.diaries is null");
				}
			}else{
				Log.e(TAG, "RESPONSE_TYPE_LIST_COLLECT_DIARYID status is "
						+ listCollectDiaryidResponse.status);
			}
			break;
		case Requester2.RESPONSE_TYPE_FORWARD_DIARY_ID:
			if(null==msg.obj){
				Log.e(TAG, "msg.obj is null what="+msg.what);
				return false;
			}
			forwardDiaryIDResponse forwardDiaryIDResponse=(forwardDiaryIDResponse) msg.obj;
			if("0".equals(forwardDiaryIDResponse.status)){
				if(forwardDiaryIDResponse.diarieids!=null&&forwardDiaryIDResponse.diarieids.length>0){
					diaryManager.getPraisedDiariesIDList().clear();
					diaryManager.getPraisedDiariesIDList().addAll(Arrays.asList(forwardDiaryIDResponse.diarieids));
				}else{
					Log.e(TAG, "res.diaries is null");
				}
			}else{
				Log.e(TAG, "RESPONSE_TYPE_FORWARD_DIARY_ID status is "
						+ forwardDiaryIDResponse.status);
			}
			break;
		/*case ExtAudioRecorder.HANDLER_AUDIO_RECORDER_VOLUME:
			if (msg.obj != null && isLongClick) {
				int vol = 0;
				if (isOnRecorderButton) {
					vol = Integer.parseInt((String) msg.obj);
				} else {
					vol = -1;
				}
				Log.d(TAG,"vol = " + vol + "content = " + msg.obj + "isOnRecorderButton = " + isOnRecorderButton);
//				vsv.updateView(vol, isOnRecorderButton);
			}
			break;*/
		case QISR_TASK.HANDLER_QISR_RESULT_CLEAN:
			Log.d(TAG,"HANDLER_QISR_RESULT_CLEAN");
			QISR_MESSAGE qisrMsg = (QISR_MESSAGE) msg.obj;
			audioTextMap.put(qisrMsg.audioID, "");
			break;
		case QISR_TASK.HANDLER_QISR_RESULT_ADD:
			QISR_MESSAGE qisrMsgAdd = (QISR_MESSAGE) msg.obj;
			String soundText = audioTextMap.get(qisrMsgAdd.audioID);
			if (soundText != null) {
				audioTextMap.put(qisrMsgAdd.audioID, soundText + qisrMsgAdd.message);
			}
			Log.d(TAG,"HANDLER_QISR_RESULT_ADD");
			break;
		case QISR_TASK.HANDLER_QISR_RESULT_DONE:
			Log.d(TAG,"HANDLER_QISR_RESULT_DONE");
			isRecogniseDone = true;
			if (isLongClick) {
				return false;
			}
			if(null==msg.obj || !isOnRecorderButton){
				Log.e(TAG, "msg.obj is null what="+msg.what);
//				LocalBroadcastManager.getInstance(this).unregisterReceiver(mMessageReceiver);
				return false;
			}
			QISR_MESSAGE qisrMsgDone = (QISR_MESSAGE) msg.obj;
			String soundTextDone = audioTextMap.get(qisrMsgDone.audioID);
			Log.d(TAG,"HANDLER_QISR_RESULT_DONE soundTextDone = " + soundTextDone);
			
			String audioQisrID = qisrMsgDone.audioID;
			String diaryQisrUUID = audioDiaryMap.get(audioQisrID);
			String attachQisrLevel = attachLevelMap.get(diaryQisrUUID);
			if ("0".equals(attachQisrLevel)) {
//				LocalBroadcastManager.getInstance(this).unregisterReceiver(mMessageReceiver);
				if (diaryQisrUUID != null && !"".equals(diaryQisrUUID)) {
					generateLocalShortRecordDiary(diaryQisrUUID,attachQisrLevel,audioQisrID);
				}
			}
			break;
		case CRMRequester.RESPONSE_TYPE_VERSION_CHECK:
			CRM_Object.versionCheckResponse versionResp = (versionCheckResponse) msg.obj;
			doingCheckVersion(versionResp);
			break;
		case CRMRequester.RESPONSE_TYPE_APK_URL:
			launchWebBrower((String) msg.obj);
			break;
		case Requester2.RESPONSE_TYPE_MY_BLACK_LIST:
			if (msg.obj != null) {
				myblacklistResponse blackList = (myblacklistResponse) msg.obj;

				if ("0".equals(blackList.status)) {
					myblacklistUsers[] userList = blackList.users;
					for (int i = 0; i < userList.length; i++) {
						WrapUser wrapUser = new WrapUser();
						wrapUser.userid = userList[i].userid;
						wrapUser.headimageurl = userList[i].headimageurl;
						wrapUser.nickname = userList[i].nickname;
						wrapUser.diarycount = userList[i].diarycount;
						wrapUser.attentioncount = userList[i].friendcount;
						wrapUser.sex = userList[i].sex;
						wrapUser.signature = userList[i].signature;
						
						blacklistContactManager.addMember(wrapUser);
					}

					if ("1".equals(blackList.hasnextpage )) {
						Requester2.requestAttentionList(handler, blackList.user_time,
								ActiveAccount.getInstance(ZApplication.getInstance())
										.getLookLookID(), "50");
					} else {
						Intent intent = new Intent(
								HomeActivity.BLACK_LIST_CHANGE);
						LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
					}
				} 
			}
			break;
		case Requester2.RESPONSE_TYPE_MY_ATTENTIONLIST:
			if (msg.obj != null) {
				myattentionlistResponse attentionList = (myattentionlistResponse) msg.obj;

				if ("0".equals(attentionList.status)) {
					myattentionlistUsers[] userList = attentionList.users;
					for (int i = 0; i < userList.length; i++) {
						WrapUser wrapUser = new WrapUser();
						wrapUser.userid = userList[i].userid;
						wrapUser.headimageurl = userList[i].headimageurl;
						wrapUser.nickname = userList[i].nickname;
						wrapUser.diarycount = userList[i].diarycount;
						wrapUser.attentioncount = userList[i].attentioncount;
						wrapUser.fanscount = userList[i].fanscount;
						wrapUser.sex = userList[i].sex;
						wrapUser.signature = userList[i].signature;
/*						String sortKey = PinYinUtil
								.getPinYin(userList[i].nickname);
						wrapUser.sortKey = sortKey;*/
						
						attentionContactManager.addMember(wrapUser);
					}

					if ("1".equals(attentionList.hasnextpage )) {
						Requester2.requestAttentionList(handler, attentionList.user_time,
								ActiveAccount.getInstance(ZApplication.getInstance())
										.getLookLookID(), "50");
					} else {
						Intent intent = new Intent(
								HomeActivity.ATTENTION_LIST_CHANGE);
						LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
					}
				} 
			}
			break;
		case Requester2.RESPONSE_TYPE_MY_FANS_LIST:
			if (msg.obj != null) {
				myfanslistResponse funsList = (myfanslistResponse) msg.obj;

				if ("0".equals(funsList.status)) {
					myfanslistUsers[] userList = funsList.users;
					for (int i = 0; i < userList.length; i++) {

						WrapFunUser wrapUser = new WrapFunUser();

						wrapUser.userid = userList[i].userid;
						wrapUser.headimageurl = userList[i].headimageurl;
						wrapUser.nickname = userList[i].nickname;
						wrapUser.diarycount = userList[i].diarycount;
						wrapUser.attentioncount = userList[i].attentioncount;
						wrapUser.fanscount = userList[i].fanscount;
						wrapUser.sex = userList[i].sex;
						wrapUser.signature = userList[i].signature;
						wrapUser.isattention = userList[i].isattention;
//						String sortKey = PinYinUtil
//								.getPinYin(userList[i].nickname);
//						wrapUser.sortKey = sortKey;
						friendsFunsManager.addMember(wrapUser);
					}

					if ("1".equals(funsList.hasnextpage)) {
						Requester2.requestMyFansList(handler,
								funsList.user_time, userID);
					} 
//					else {
//						
//						Intent intent = new Intent(
//								HomeActivity.ATTENTION_LIST_CHANGE);
//						LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
//
//					}

				}
			}

			break;	
		case START_VIDEO_RECORDING:
			isVideoRecording = true;
			videoShootTotalMillis = TimeHelper.getInstance().now();
			final TempDiaryWrapper wrapper2 = requestUploadVideoDiary();
			mediaRecorder.startPreview(CameraInfo.CAMERA_FACING_BACK);
			handler.postDelayed(new Runnable() {
				
				@Override
				public void run() {
					mediaRecorder.start(wrapper2.mediaName, wrapper2.mediaPath);
					task = new ZTimerTask() {
						
						@Override
						public void run() {
							LowStorageChecker.OnChoseListener listener = new OnChoseListener() {
								
								@Override
								public void onYes() {
								}
								
								@Override
								public void onNo() {
								}
							};
							if (!LowStorageChecker.check(HomeActivity.this, listener)) {
								stopVideoRecording();
							}
						}
					};
					task.schedule(5000, 5000);
				}
			}, 200);
			
			break;
		case Requester2.RESPONSE_TYPE_UPLOAD_PICTURE:
			if (msg.obj != null) {
				GsonResponse2.uploadPictrue resp = (GsonResponse2.uploadPictrue) msg.obj;
				Prompt.Alert(this, "上传头像完成： status：" + resp.status);
				if(resp.status.equals("0") && resp.imageurl!=null){
					accountInfo.headimageurl = resp.imageurl;
				}
				Intent intent = new Intent(BROADCAST_HEADIMG_UPLOAD);
				LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
				Log.e(TAG, "RESPONSE_TYPE_UPLOAD_PICTURE: status" + resp.status + ", url:" + resp.imageurl);
			}
			break;
		case Requester2.RESPONSE_TYPE_GET_OFFICIAL_USERIDS:
			if (msg.obj != null) {
				getOfficialUseridsResponse officialList = (getOfficialUseridsResponse) msg.obj;
				if ("0".equals(officialList.status)) {
					MyUserids[] userids = officialList.userids;
					
					ArrayList<MyUserids> MyUseridsList = new ArrayList<MyUserids>();
					for (int i = 0; i < userids.length; i++) {
						
						MyUserids myUserid = new GsonResponse2().new MyUserids();
						myUserid.userid = userids[i].userid;
						myUserid.nickname = userids[i].nickname;
						myUserid.headimageurl = userids[i].headimageurl;
						myUserid.type = userids[i].type;
						
						MyUseridsList.add(myUserid);
					}
					officialUseridsManager.refreshCache(MyUseridsList);
				}
			}
			break;
		}
		return false;
	}
	
	private TempDiaryWrapper requestUploadVideoDiary() {
		videoDiaryUUID = UUID.randomUUID().toString().replace("-", "");
		String uid = ActiveAccount.getInstance(MainApplication.getAppInstance()).getUID();
		
		TempDiaryWrapper wrapper = new TempDiaryWrapper(TempDiaryWrapper.TYPE_VIDEO);
		wrapper.mediaName = MD5.encode((uid + videoDiaryUUID).getBytes());
		wrapper.mediaPath = Environment.getExternalStorageDirectory() + Constant.SD_STORAGE_ROOT + "/" + uid + "/video";
		
		String longitude = "";
		String latitude = "";
		Attachs[] attachs = new Attachs[1];
		attachs[0] = new Attachs();
		attachs[0].attachid = "";
		attachs[0].attachuuid = UUID.randomUUID().toString().replace("-", "");
		attachs[0].attach_type = "1"; // 1视频 2音频 3图片 4文字;
		attachs[0].level = "1"; // 1主内容 0辅内容;
		if (PluginUtils.isPluginMounted()) {
			attachs[0].video_type = "1"; // 1普清;
		} else {
			attachs[0].video_type = "0"; // 0高清;
		}
		if (CommonInfo.getInstance().myLoc != null) {
			attachs[0].attach_latitude = String.valueOf(CommonInfo.getInstance().myLoc.latitude);
			attachs[0].attach_logitude = String.valueOf(CommonInfo.getInstance().myLoc.longitude);
			longitude = String.valueOf(CommonInfo.getInstance().myLoc.longitude);
			latitude = String.valueOf(CommonInfo.getInstance().myLoc.latitude);
		}
		attachs[0].content = "";
		attachs[0].suffix = ".mp4";
		attachs[0].Operate_type = "1"; //操作类型: 1增加 2更新 3删除;
		
		
		String addressCode = "";
		AccountInfo account = AccountInfo.getInstance(userID);
		MyWeather weather = account.myWeather;
		if (weather != null) {
			addressCode = weather.addrCode;
		}
		GsonRequest2.createStructureRequest request = Requester2.createStructure(
				handler, 
				"", 
				videoDiaryUUID, 
				"1", 
				"", 
				"", 
				longitude,
				latitude, 
				"", 
				CommonInfo.getInstance().positionStr,longitude,latitude,
				String.valueOf(TimeHelper.getInstance().now()),
				addressCode,attachs);
		
		wrapper.request = request;
		diaryWrappers.put(videoDiaryUUID, wrapper);
		
		return wrapper;
	}
	
	private void createVideoDiary(TempDiaryWrapper wrapper) {

		if (wrapper.mediaCatchOK && wrapper.requestDiaryOK) {

			String diaryid = null;
			String mediaURL = wrapper.request.attachs[0].attachuuid;
			String attachid = "";
			String attachuuid = wrapper.request.attachs[0].attachuuid;

			if (wrapper.response != null) {
				if (!TextUtils.isEmpty(wrapper.response.diaryid)) {
					diaryid = wrapper.response.diaryid;
				}
				if (wrapper.response.attachs != null && wrapper.response.attachs.length > 0) {
					mediaURL = wrapper.response.attachs[0].path;
					attachid = wrapper.response.attachs[0].attachid;
					attachuuid = wrapper.response.attachs[0].attachuuid;
				}
			}

			String uid = ActiveAccount.getInstance(ZApplication.getInstance()).getUID();
			AccountInfo account = AccountInfo.getInstance(uid);
			String temp = wrapper.mediaName;
			int i = 0;
			while (temp.contains("-")) {
				temp = temp.replace("-"+i, "");
			}

			String absolutePath = wrapper.mediaPath + "/" + temp + "/" + wrapper.mediaName + ".mp4";
			String relativePath = absolutePath.replace(Environment.getExternalStorageDirectory().getPath(), "");
			
			MediaValue mediaValue = new MediaValue();
			mediaValue.UID = uid;
			mediaValue.path = relativePath;
			mediaValue.totalSize = new File(absolutePath).length();
			mediaValue.realSize = mediaValue.totalSize;
			mediaValue.MediaType = 5;
			mediaValue.Direction = 2;

			if (wrapper.response != null && "0".equals(wrapper.response.status) && wrapper.response.diaryid != null && wrapper.response.diaryid.length() > 0) {
				mediaValue.url = mediaURL;
			} else {
				mediaValue.url = wrapper.request.attachs[0].attachuuid;
			}

			if (MediaValue.checkMediaAvailable(mediaValue, 5)) {
				AccountInfo.getInstance(uid).mediamapping.setMedia(uid, mediaValue.url, mediaValue);
			}

			mediaURL = mediaValue.url;

			GsonResponse2.MyAttachVideo[] attachVideo = new GsonResponse2.MyAttachVideo[1];
			attachVideo[0] = new GsonResponse2.MyAttachVideo();
			attachVideo[0].playvideourl = mediaURL;
			if (PluginUtils.isPluginMounted()) {
				attachVideo[0].videotype = "1"; // 1普清;
			} else {
				attachVideo[0].videotype = "0"; // 0高清;
			}

			GsonResponse2.diaryAttach[] attachs = new diaryAttach[1];
			attachs[0] = new diaryAttach();
			attachs[0].attachid = attachid;
			attachs[0].attachtype = "1"; // 1视频 2音频 3图片 4文字;
			attachs[0].attachlevel = "1"; // 1主内容 0辅内容;
			attachs[0].attachvideo = attachVideo;
			attachs[0].attachuuid = attachuuid;
			attachs[0].playtime = String.valueOf(new Mp4InfoUtils(absolutePath).totaltime);
			attachs[0].videocover = relativePath + ".vc";
			
			// 获取videocover并登记mapping
			String newVideoCover = MediaCoverUtils.getMediaCoverUrl(absolutePath);
			if (newVideoCover != null) {

				MediaValue mediaValueCover = new MediaValue();
				mediaValueCover.UID = uid;
				mediaValueCover.path = newVideoCover.replace(Environment.getExternalStorageDirectory().getPath(), "");
				mediaValueCover.totalSize = new File(newVideoCover).length();
				mediaValueCover.realSize = mediaValueCover.totalSize;
				mediaValueCover.MediaType = 2;
				mediaValueCover.url = attachs[0].videocover;
				ZLog.printObject(mediaValueCover);
				if (MediaValue.checkMediaAvailable(mediaValueCover, 2)) {
					AccountInfo.getInstance(uid).mediamapping.setMedia(uid, mediaValueCover.url, mediaValueCover);
				}
			}
			// ========================================

			DiaryManager diaryManager = DiaryManager.getInstance();

			GsonResponse2.MyDiary diary = new GsonResponse2.MyDiary();
			diary.diaryid = diaryid;
			diary.diaryuuid = wrapper.request.diaryuuid;
			diary.weather = "";
			diary.weather_info = "";
			MyWeather weather = account.myWeather;
			if (weather != null) {
				if (weather.desc != null && weather.desc.length > 0) {
					diary.weather_info = weather.desc[0].description;
					diary.weather = weather.desc[0].weatherurl;
				}
			}
			diary.mood = "0";
			if (account.mood != null && !"".equals(account.mood)) {
				diary.mood = account.mood;
			}
			diary.userid = uid;
			diary.diarytimemilli = String.valueOf(TimeHelper.getInstance().now());
			diary.updatetimemilli = String.valueOf(TimeHelper.getInstance().now());
			diary.diary_status = "1";
			diary.publish_status = account.setmanager.getDiary_type();
			
			if (CommonInfo.getInstance().myLoc != null) {
				diary.latitude = String.valueOf(CommonInfo.getInstance().myLoc.latitude);
				diary.longitude = String.valueOf(CommonInfo.getInstance().myLoc.longitude);
			}
			diary.position = "";
			if (MapItemHelper.locateAvailable(diary)) {
				diary.position = CommonInfo.getInstance().positionStr;
			}
			diary.signature = account.signature;
			diary.headimageurl = account.headimageurl;
			diary.attachs = attachs;
			diary.request = wrapper.request;
			diary.nickname = account.nickname;
			diary.sex = account.sex;
			diary.join_safebox = "0";
			diary.resourcediaryid = "";
			diary.resourceuuid = "";
			
			if (wrapper.response != null && "0".equals(wrapper.response.status)) {
				diary.sync_status = 1;
			}
			
			diaryManager.saveDiaries(diary, true);

			INetworkTask task = null;
			
			if(wrapper.response != null && wrapper.response.diaryid != null && !"".equals(wrapper.response.diaryid)) {
				NetworkTaskInfo networktaskinfo = new NetworkTaskInfo(diary, INetworkTask.TASK_TYPE_UPLOAD); // 设置数据源;
				task = new UploadNetworkTask(networktaskinfo); // 创建上传/下载任务;
			} else {
				NetworkTaskInfo networktaskinfo = new NetworkTaskInfo(diary, INetworkTask.TASK_TYPE_CACHE); // 设置数据源;
				task = new CacheNetworkTask(networktaskinfo);// 创建上传/下载任务
			}

			NetworkTaskManager.getInstance(uid).addTask(task); // 添加网络任务;
			NetworkTaskManager.getInstance(uid).startNextTask(); // 开始任务队列;

			wrapper.mediaValue = mediaValue;
			wrapper.thumbnail = scaleThumbnail(ThumbnailUtils.createVideoThumbnail(absolutePath, Thumbnails.MINI_KIND));
			diarys.add(0, wrapper);
			
			CmmobiClickAgentWrapper.onEventDuration(this, CDR_KEY_SHOOTING_CAN, uid, videoShootTotalMillis);
		}
	}
	
	private Bitmap scaleThumbnail(Bitmap bitmap) {
		if (bitmap != null) {
			String width = "width";
			String height = "height";

			Model originalModel = new Model();
			originalModel.put(width, bitmap.getWidth());
			originalModel.put(height, bitmap.getHeight());

			Model standardModel = new Model();
			standardModel.put(width, 384);
			standardModel.put(height, 512);

			ZUniformScaler.scale(originalModel, standardModel, ScaleType.OUT);
			bitmap = ZGraphics.resize(bitmap, (float) originalModel.get(width), (float) originalModel.get(height), true);
		}
		
		return bitmap;
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
	
	private void generateLocalShortRecordDiary(String diaryUUID,
			String attachLevel,String audioID) {
		Attachs audioAttachs = new Attachs();
		audioAttachs.attachid = "";
		audioAttachs.attachuuid = UUID.randomUUID().toString().replace("-", "");
		audioAttachs.attach_type = "2";
		audioAttachs.level = attachLevel;
		audioAttachs.audio_type = "1";
		if (CommonInfo.getInstance().myLoc != null) {
			audioAttachs.attach_latitude = String.valueOf(CommonInfo.getInstance().myLoc.latitude);
			audioAttachs.attach_logitude = String.valueOf(CommonInfo.getInstance().myLoc.longitude);
		}
		audioAttachs.content = "";
		audioAttachs.suffix = ".mp4";
		audioAttachs.Operate_type = "1";
		diaryAttachMap.put(diaryUUID,audioAttachs.attachuuid);
		
		Attachs[] attachs;
		Attachs textAttachs = new Attachs();
		if (audioTextMap.get(audioID) != null && !"".equals(audioTextMap.get(audioID))) {
			textAttachs = new Attachs();
			textAttachs.attachid = "";
			textAttachs.attachuuid = UUID.randomUUID().toString().replace("-", "");
			textAttachs.attach_type = "4";
			textAttachs.level = attachLevel;
			textAttachs.content = audioTextMap.get(audioID);
			
			diaryTextAttachMap.put(diaryUUID, textAttachs.attachuuid);
			
			attachs = new Attachs[2];
			attachs[0] = audioAttachs;
			attachs[1] = textAttachs;
		} else {
			attachs = new Attachs[1];
			attachs[0] = audioAttachs;
		}
		
		String longitude = "";
		String latitude = "";
		if (CommonInfo.getInstance().myLoc != null) {
			longitude = String.valueOf(CommonInfo.getInstance().myLoc.longitude);
			latitude = String.valueOf(CommonInfo.getInstance().myLoc.latitude);
		}
		
		String addressCode = "";
		AccountInfo account = AccountInfo.getInstance(userID);
		MyWeather weather = account.myWeather;
		if (weather != null) {
			addressCode = weather.addrCode;
		}
		Log.d(TAG,"generateLocalShortRecordDiary userselectposition = " + CommonInfo.getInstance().positionStr);
		GsonRequest2.createStructureRequest createStructure = Requester2.createStructure(handler, "", diaryUUID, "1", "", "",
				longitude,latitude,
				"",CommonInfo.getInstance().positionStr,longitude,latitude, 
				String.valueOf(TimeHelper.getInstance().now()),
				addressCode,attachs);
		recordCreateStructureMap.put(diaryUUID, createStructure);
		
		
	}

	private GsonResponse2.MyDiary createDiaryStructure(GsonResponse2.createStructureResponse structureResponse) {
		if (structureResponse == null) {
			return null;
		}
		
		Log.d(TAG,"createDiaryStructure in");
		AccountInfo account = AccountInfo.getInstance(userID);
		GsonResponse2.MyDiary diary = new GsonResponse2.MyDiary();
		diary.weather_info = "";
		diary.weather = "";
		MyWeather weather = account.myWeather;
		if (weather != null) {
			if(weather.desc != null && weather.desc.length > 0) {
				diary.weather_info = weather.desc[0].description;
				diary.weather = weather.desc[0].weatherurl;
			}
		}
		diary.mood = "0";
		if (account.mood != null && !"".equals(account.mood)) {
			diary.mood = account.mood;
		}
		diary.userid = userID;
		diary.diaryuuid = structureResponse.diaryuuid;
		if (structureResponse.diaryid != null && !"".equals(structureResponse.diaryid)) {
			diary.diaryid = structureResponse.diaryid;
		} 
		/*else {
			diary.diaryid = diary.diaryuuid;// diaryID为空时将diaryuuid赋值给diaryID
		}*/
		diary.diarytimemilli = String.valueOf(TimeHelper.getInstance().now());
		diary.updatetimemilli = String.valueOf(TimeHelper.getInstance().now());
		diary.diary_status = "1";
		diary.nickname = account.nickname;
		diary.publish_status = account.setmanager.getDiary_type();
		if (CommonInfo.getInstance().myLoc != null) {
			diary.latitude = String.valueOf(CommonInfo.getInstance().myLoc.latitude);
			diary.longitude = String.valueOf(CommonInfo.getInstance().myLoc.longitude);
		}
		diary.position = "";
		if (MapItemHelper.locateAvailable(diary)) {
			diary.position = CommonInfo.getInstance().positionStr;
		}
		
		diary.sex = account.sex;
		diary.signature = account.signature;
		diary.headimageurl = account.headimageurl;
		diary.join_safebox = "0";
		diary.resourcediaryid = "";
		diary.resourceuuid = "";
		Log.d(TAG,"createDiaryStructure position = " + diary.position + " sex = " + diary.sex);
		
		String absolutePath = recordMap.get(structureResponse.diaryuuid);
		if (absolutePath == null) {
			return null;
		}
		String relativePath = absolutePath.replace(SDCARD_PATH, "");
		
		MediaValue mediaValue = new MediaValue();
		mediaValue.UID = userID;
		mediaValue.path = relativePath;
		mediaValue.totalSize = new File(absolutePath).length();
		mediaValue.realSize = mediaValue.totalSize;
		mediaValue.MediaType = "0".equals(attachLevelMap.get(diary.diaryuuid))?3:4;// 长录音是4短录音是3
		mediaValue.Direction = 2;
		
		// 音频附件
		diaryAttach audioAttach = new diaryAttach();
		audioAttach.attachtype = "2";
		audioAttach.attachlevel = attachLevelMap.get(diary.diaryuuid);
		audioAttach.playtime = recordDurationMap.get(structureResponse.diaryuuid);
		if (audioAttach.playtime == null) {
			audioAttach.playtime = "1";
		}
		audioAttach.attachuuid = diaryAttachMap.get(diary.diaryuuid);
		GsonResponse2.MyAttachAudio[] audioAttachs = new MyAttachAudio[1];
		audioAttachs[0] = new MyAttachAudio();
		audioAttachs[0].audiotype = "1";
		String mappingKey = "";
		int attachsSize = 0;
		if (structureResponse.attachs != null) {
			attachsSize = structureResponse.attachs.length;
		}
		
		// 文本附件
		diaryAttach textAttach = null;
		String audioID = diaryAudioMap.get(diary.diaryuuid);
		
		if ("0".equals(structureResponse.status)) {
			if (structureResponse.attachs != null && attachsSize > 0) {// 创建日记结构成功
				for (int i = 0;i < attachsSize; i++) {
					MyAttach myAttach = structureResponse.attachs[i];
					if (diaryAttachMap.get(diary.diaryuuid).equals(myAttach.attachuuid)) {
						audioAttach.attachid = myAttach.attachid;
						audioAttachs[0].audiourl = myAttach.path;
						
						mediaValue.url = structureResponse.attachs[0].path;
						mappingKey = audioAttachs[0].audiourl;
					} else if (myAttach.attachuuid != null && myAttach.attachuuid.equals(diaryTextAttachMap.get(diary.diaryuuid))) {
						textAttach = new diaryAttach();
						textAttach.attachid = myAttach.attachid;
						textAttach.attachuuid = myAttach.attachuuid;
						textAttach.content = audioTextMap.get(audioID);
						textAttach.attachtype = "4";
						textAttach.attachlevel = attachLevelMap.get(diary.diaryuuid);
					}
				}
			} 
			if (textAttach == null && diaryTextAttachMap.get(diary.diaryuuid) != null) {
				textAttach = new diaryAttach();
				textAttach.attachid = "";
				textAttach.attachuuid = diaryTextAttachMap.get(diary.diaryuuid);
				textAttach.content = audioTextMap.get(audioID);
				textAttach.attachtype = "4";
				textAttach.attachlevel = attachLevelMap.get(diary.diaryuuid);
			}
			diary.sync_status = 1;
		}else {// 创建日记结构失败
			mappingKey = audioAttach.attachuuid;
			if (audioTextMap.get(audioID) != null && !"".equals(audioTextMap.get(audioID))) {
				textAttach = new diaryAttach();
				textAttach.attachuuid = diaryTextAttachMap.get(diary.diaryuuid);
				textAttach.content = audioTextMap.get(audioID);
				textAttach.attachtype = "4";
				textAttach.attachlevel = attachLevelMap.get(diary.diaryuuid);
			}
		}
		
		audioAttach.attachaudio = audioAttachs;
		GsonResponse2.diaryAttach[] attachs;
		if (audioTextMap.get(audioID) != null && !"".equals(audioTextMap.get(audioID)) && textAttach != null) {
			attachs = new diaryAttach[2];
			attachs[0] = audioAttach;
			attachs[1] = textAttach;
		} else {
			attachs = new diaryAttach[1];
			attachs[0] = audioAttach;
		}
		
		diary.attachs = attachs;
		
		if (MediaValue.checkMediaAvailable(mediaValue, mediaValue.MediaType)) {// 登记音频文件
			Log.d(TAG,"AAA mappingKey" + mappingKey + " path = " + mediaValue.path);
			AccountInfo.getInstance(userID).mediamapping.setMedia(userID, mappingKey, mediaValue);
			if (AccountInfo.getInstance(userID).mediamapping.getMedia(userID, mediaValue.path) != null) {
				AccountInfo.getInstance(userID).mediamapping.delMedia(userID,mediaValue.path);
			}
		}
		
		Log.d(TAG,"AAA diaryuuid = " + diary.diaryuuid + " playtime = " + audioAttach.playtime + " recordDurationMap = " + recordDurationMap);
		
		/*// 音频附件
		Attachs attachsAudio = null;
		// 文本附件
		Attachs attachsText = null;
		
		ArrayList<Attachs> upAttachList = new ArrayList<Attachs>();
		if (audioAttach != null) {
			attachsAudio = new Attachs();
			fillRequestAttach(attachsAudio, audioAttach);
			upAttachList.add(attachsAudio);
		}
		
		if (textAttach != null) {
			attachsText = new Attachs();
			fillRequestAttach(attachsText, textAttach);
			upAttachList.add(attachsText);
		}
		Attachs[] attachsUpload = upAttachList.toArray(new Attachs[upAttachList.size()]);*/
		
		GsonRequest2.createStructureRequest upRequest = recordCreateStructureMap.get(diary.diaryuuid);
		/*upRequest.attachs = attachsUpload;
		upRequest.diaryid = "";
		upRequest.diaryuuid = diary.diaryuuid;
		upRequest.operate_diarytype = "1";
		upRequest.logitude = String.valueOf(CommonInfo.getInstance().myLoc.longitude);
		upRequest.latitude = String.valueOf(CommonInfo.getInstance().myLoc.latitude);
		upRequest.userselectlogitude = upRequest.logitude;
		upRequest.userselectlatitude = upRequest.latitude;*/
		
		diary.request = upRequest;
		
//		DiaryWrapper diaryWrapper = new DiaryWrapper();
//		diaryWrapper.myDiary = diary;
//		diaryWrapper.status = 1;
//		diaryManager.addDiaryWrapper(diaryWrapper);
		diaryManager.saveDiaries(diary, true);
		Log.d("==WR==","uuid = " + diary.diaryuuid);
		
		// 日记上传
		if (diary.diaryid == null || "".equals(diary.diaryid)) {// 创建日记结构失败
//			Log.d("==WR==", "gAttachs size = " + attachsUpload.length);
			NetworkTaskInfo networktaskinfo = new NetworkTaskInfo(diary, INetworkTask.TASK_TYPE_CACHE);//设置数据源
			CacheNetworkTask cachetask = new CacheNetworkTask(networktaskinfo, upRequest, networktaskinfo.caMedias);//创建上传/下载任务
			NetworkTaskManager.getInstance(userID).addTask(cachetask);//添加网络任务
			NetworkTaskManager.getInstance(userID).startNextTask();    //开始任务队列
		} else {// 创建日记结构成功
			NetworkTaskInfo networktaskinfo = new NetworkTaskInfo(diary, INetworkTask.TASK_TYPE_UPLOAD);//设置数据源
			UploadNetworkTask uploadtask = new UploadNetworkTask(networktaskinfo);//创建上传/下载任务
			NetworkTaskManager.getInstance(userID).addTask(uploadtask);//添加网络任务
			NetworkTaskManager.getInstance(userID).startNextTask();    //开始任务队列
		}
		
		return diary;
	}
	
	// 填充请求附件结构
	private void fillRequestAttach(Attachs attach,diaryAttach srcAttach ) {
		attach.attachid = "";
		attach.attachuuid = srcAttach.attachuuid;
		attach.attach_type = srcAttach.attachtype;
		attach.level = srcAttach.attachlevel;
		if (CommonInfo.getInstance().myLoc != null) {
			attach.attach_latitude = String.valueOf(CommonInfo.getInstance().myLoc.latitude);
			attach.attach_logitude = String.valueOf(CommonInfo.getInstance().myLoc.longitude);
		}
		attach.content = srcAttach.content;
		if ("2".equals(srcAttach.attachtype)) {
			attach.suffix = ".mp4";
			attach.audio_type = "1";
		}
		attach.Operate_type = "1";
	}
	
	private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			int type;
			String content = "";
			Message msg = new Message();
			// Get extra data included in the Intent
			if (ExtAudioRecorder.AUDIO_RECORDER_MSG.equals(intent
					.getAction())) {
				type = intent.getIntExtra("type", 0);
				Log.d(TAG,"onReceive type = " + type);
				if (type == ExtAudioRecorder.HANDLER_AUDIO_RECORDER_DONE) {
					content = intent.getStringExtra("content");
					long duration = intent.getLongExtra("audioduration",0);
					msg.arg1 = (int)duration;
					Log.d(TAG,"AAA if onReceive content = " + content + " duration = " + duration);
				} else if (type == ExtAudioRecorder.HANDLER_AUDIO_RECORDER_VOLUME){
					content =intent.getStringExtra("content");
					Log.d(TAG,"else onReceive content = " + content);
				}
				
				msg.what = type;
				msg.obj = content;
			} else if (QISR_TASK.QISR_RESULT_MSG.equals(intent.getAction())) {
				type = intent.getIntExtra("type", 0);
			    String message = intent.getStringExtra("content");
			    String audioId = intent.getStringExtra("audioID");
			    QISR_MESSAGE qisrMsg = new QISR_MESSAGE(audioId, message);
			    Log.d(TAG, "Got message: " + message);
			    msg.what = type;
			    msg.obj = qisrMsg;
			}

			handler.sendMessage(msg);
		}
	};
	
	class QISR_MESSAGE {
		String audioID;
		String message;
		
		public QISR_MESSAGE(String id,String msg) {
			audioID = id;
			message = msg;
		}
	}
	
	private PopupWindow portraitMenu;
	
	//显示更换空间背景选项
	private void showRecorderChoice() {
		View view = LayoutInflater.from(this).inflate(R.layout.activity_homepage_recorder_menu,
				null);
		if (portraitMenu == null) {
			portraitMenu = new PopupWindow(view, LayoutParams.FILL_PARENT,
					LayoutParams.WRAP_CONTENT, true);
			portraitMenu.setBackgroundDrawable(getResources().getDrawable(
					R.drawable.dot_big));
			view.findViewById(R.id.btn_recorder).setOnClickListener(this);
			view.findViewById(R.id.btn_cancel).setOnClickListener(this);
		}
		
		if (portraitMenu.isShowing()) {
			portraitMenu.dismiss();
		}
		
		portraitMenu.showAtLocation(findViewById(android.R.id.tabcontent),
				Gravity.BOTTOM, 0, 0);
	}

	@Override
	public void onSurfaceCreated() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onStartRecorder(Object r, String path) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onStopRecorder(Object r, String path) {
		String temp = path.substring(path.lastIndexOf("/") + 1, path.length());
		temp = temp.replace(".mp4", "");

		TempDiaryWrapper wrapper = diaryWrappers.get(videoDiaryUUID);
		wrapper.mediaCatchOK = true;
		wrapper.mediaName = temp;

		createVideoDiary(wrapper);
	}
	
	@Override
	public void onSmallBoxComplete(Object r, String path) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onPauseRecorder(Object r) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onResumeRecorder(Object r) {
		// TODO Auto-generated method stub
		
	}
	
	public static boolean isSdcardMountedAndWritable() {
		return android.os.Environment.getExternalStorageState().equals( 
				android.os.Environment.MEDIA_MOUNTED);
	}
	
	private BroadcastReceiver myReceiver=new BroadcastReceiver() {
		
		@Override
		public void onReceive(Context context, Intent intent) {
			
			if (PrivateMessageService.FLAG_MESSAGE_DATA_UPDATE.equals(intent.getAction())) {
				if (null != accountInfo && null != accountInfo.privateMsgManger) {
					if(accountInfo.privateMsgManger.getUnReadNum() != 0){
						message_count.setVisibility(View.VISIBLE);
					}else if(message_count.getVisibility() == View.VISIBLE){
						message_count.setVisibility(View.GONE);
					}
				}
			}
		}
	};
	@Override
	public void onCameraOpenFailed(String msg) {
		ZToast.showLong(R.string.camera_error);
	}
	
	
}