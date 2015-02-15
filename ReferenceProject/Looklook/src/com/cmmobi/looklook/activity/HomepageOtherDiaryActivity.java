package com.cmmobi.looklook.activity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Message;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationSet;
import android.view.animation.RotateAnimation;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;
import cn.zipper.framwork.core.ZActivity;
import cn.zipper.framwork.core.ZApplication;
import cn.zipper.framwork.core.ZDialog;

import com.baidu.mapapi.map.LocationData;
import com.baidu.platform.comapi.basestruct.GeoPoint;
import com.cmmobi.looklook.R;
import com.cmmobi.looklook.common.adapter.MapNearbyListAdapter;
import com.cmmobi.looklook.common.gson.GsonResponse2;
import com.cmmobi.looklook.common.gson.GsonResponse2.MyDiary;
import com.cmmobi.looklook.common.gson.GsonResponse2.attentionResponse;
import com.cmmobi.looklook.common.gson.GsonResponse2.diaryAttach;
import com.cmmobi.looklook.common.gson.GsonResponse2.forwardDiaryListResponse;
import com.cmmobi.looklook.common.gson.GsonResponse2.homeResponse;
import com.cmmobi.looklook.common.gson.GsonResponse2.shareEnjoyDiaryResponse;
import com.cmmobi.looklook.common.gson.Requester2;
import com.cmmobi.looklook.common.listview.pulltorefresh.AbsRefreshView.OnRefreshListener;
import com.cmmobi.looklook.common.listview.pulltorefresh.OtherDiaryListView;
import com.cmmobi.looklook.common.listview.pulltorefresh.OtherRecentListView;
import com.cmmobi.looklook.common.utils.CmmobiClickAgentWrapper;
import com.cmmobi.looklook.common.utils.DateUtils;
import com.cmmobi.looklook.common.utils.UmengclickAgentWrapper;
import com.cmmobi.looklook.common.view.Xdialog;
import com.cmmobi.looklook.common.web.WebImageView;
import com.cmmobi.looklook.fragment.WrapUser;
import com.cmmobi.looklook.info.location.MyLocationInfo;
import com.cmmobi.looklook.info.profile.AccountInfo;
import com.cmmobi.looklook.info.profile.ActiveAccount;
import com.cmmobi.looklook.info.profile.DiaryManager;
import com.cmmobi.looklook.info.profile.DiaryManager.FilterType;
import com.cmmobi.looklook.info.profile.OtherUserInfo;
import com.cmmobi.looklook.info.profile.TimeHelper;
import com.cmmobi.looklook.info.weather.MyWeatherInfo;
import com.cmmobi.looklook.map.MapItemHelper;
import com.cmmobi.looklook.map.MapItemHelper.Item;
import com.cmmobi.looklook.map.MyItemizedOverlay;
import com.cmmobi.looklook.map.MyMapView;
import com.cmmobi.looklook.map.Span;
import com.cmmobi.looklook.prompt.Prompt;
import com.nostra13.universalimageloader.api.AnimateFirstDisplayListener;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageLoadingListener;
import com.nostra13.universalimageloader.core.display.CircularBitmapDisplayer;
import com.nostra13.universalimageloader.core.display.SimpleBitmapDisplayer;

public class HomepageOtherDiaryActivity extends ZActivity{

	private static final String TAG="HomepageOtherDiaryActivity";
	private static final boolean ISDEBUG=true;
	
	private ImageView ivHour;
	private ImageView ivSecond;
	private TextView tvHourAndMinutes;
	private TextView tvDay;
	
	private Bitmap bmHour;
	private Bitmap bmSecond;
	
	private int hWidth;
	private int hHeight;
	private int sWidth;
	private int sHeight;
	
	private OtherDiaryListView diaryListView;
	private OtherRecentListView recentListView;
	
	// UI components
	private MyMapView mv_diaryMap;
	private ListView  lv_info;
	private ImageView iv_close;
	private FrameLayout flDiaryMap;
	private RelativeLayout rltankuang;
	private boolean isRecent = false;
	private DiaryChangeType diarychangeType;
	public enum DiaryChangeType {
		MYSELF_DIARY_MAP, MYSELF_DIARY_LIST
	}
	// map and location
	private MyLocationInfo myLocInfo;
	private LocationData myLoc;
	//GeoPoint gp_center;
	private Span result_span;
	//List<OverlayItem> mGeoList;
	private Item[] objs;
	private MapNearbyListAdapter list_adapter;
	
	private ImageView ivMood;//心情图片
	private ListView listMoods;//心情列表
	private LayoutInflater inflater;
	
	private WebImageView ivPortrait;//头像
	private TextView tvNickname;//昵称
	private TextView tvSignature;//签名
	private ImageView ivSex;//性别
	
//	private UpdateWeatherReceiver updateWeatherReceiver;
	
//	private WebImageView ivWeatherIcon;//天气图标
//	private TextView tvWeatherTemp;//天气温度
//	private TextView tvWeatherRegion;//天气区域
	
	private WebImageView ivZoneBackground;//空间背景
	
//	private ImageView ivSettings;//设置按钮
	
	private View rlTitleBar;//默认状态栏
	private ImageView ivBack;//删除状态栏返回按钮
	private TextView tvCheckedNum;//选中条数
//	private ImageView ivRemove;//删除按钮
	private ImageView ivMore;// 更多按钮
	private View vTitleChoice;//日记筛选layout
	private TextView tvChoiceName;//筛选对应文字
	
	
	private AccountInfo accountInfo;
	private String userID;
	private String otherUserId;
	private String otherNickName;
	
	private List<LinearLayout> mList = new ArrayList<LinearLayout>();
	private int RADIUS = 120;
	private int duratime = 500;
	
	private boolean isIn = true; // 判断目前图标状态是扩散还是收缩  true:收缩    false:扩散 
	private boolean isEnd = false; // 判断动画是否结束
	private boolean isFirstInitAnim = true;// 是否第一次点击扩展按钮
	private ImageView ivExtend;// 扩展菜单按钮，点击弹出"关注","备注名","黑名单","私信"按钮
	private ImageView ivExtendInvisible;
	private ImageView ivAttention;// 关注按钮
	private ImageView ivPrivateLetter;// 私信按钮
//	private ImageView ivRemarksName;// 备注名按钮
	private ImageView ivBlacklist;// 黑名单
	private LinearLayout mB1;
	private LinearLayout mB2;
	private LinearLayout mB3;
	private LinearLayout mBoss;
	
	private List<AnimationSet> mOutAnimatinSets = new ArrayList<AnimationSet>();
	private List<AnimationSet> mInAnimatinSets = new ArrayList<AnimationSet>();
	private final String ATTENTION_SUCCESSED = "0";
	private final String CANCLEATTENTION_SUCCESSED = "0";
	private final String MARKATTENTION_SUCCESSED = "0";
	private final int HANDLER_FLAG_INIT_MAP_VIEW = 0xfff0002;
	private boolean isFriend = false;
	private boolean isBlackList = false;
	public static final int REQUEST_CODE = 1;
	private int diaryPageIndex;
	private DiaryManager diaryManager;
	private ArrayList<MyDiary> diaries;
	
	private TextView tvFansCount;
	private TextView tvAttentionCount;
	private LinearLayout fansCountLayout = null;
	private LinearLayout attentionCountLayout = null;
	
	private WrapUser wrapUser = new WrapUser();
	
	private Rect[] rects = new Rect[4];
	
	private int tabType = 0;
	private final int DIARY_TYPE = 0;
	private final int RECENT_TYPE = 1;
	
	String diaryWidth;//日记页中日记的宽度
	String diaryHeight;//日记页中日记的高度
	String backgroundWidth;//个人空间背景宽度
	String backgroundHeight;//个人空间背景高度
	
	String recentWidth;//分享中日记的宽度
	String recentHeight;//分享中日记的高度
	
	private int isSlipUP;//滑动方向，1-上滑 2-下滑
	private long timeOffset;//隐藏和显示的时间频率
	private View vTime;//时钟layout
	private View vTimeReplace;//上滑时替代显示center
	
//	private WebImageView ivWeatherReplace;
//	private TextView tvWeatherTempReplace;
//	private TextView tvWeatherRegionReplace;
	
	private ImageView ivHoursReplace;
	private ImageView ivMinutesReplace;
	private TextView tvHoursReplace;
	private TextView tvMinutesReplace;
	
	private View vMoodReplace;
	private ImageView ivMoodReplace;
	
	public static final String INTENT_ACTION_USERID="userid";
	public static final String INTENT_ACTION_NICKNAME="nickname";
	
	//2倍屏幕高度
	private static int pScreenHeight2;
	private static int nScreenHeight2;
	
	//判断动态页是下拉刷新还是上拉加载
	private boolean recentFreshMode;
	//判断主页是下拉刷新还是上拉加载
	private boolean diaryFreshMode;
	
	private int screenMode = 0;
	private final int ALBUMS_MODE = 0;
	private final int RECENT_MODE = 1;
	private final int MAP_MODE = 2;
	
	private ImageView iv_photo;
	private DisplayImageOptions options;
	protected ImageLoader imageLoader;
	private ImageLoadingListener animateFirstListener;
	String headUrl="";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		CmmobiClickAgentWrapper.onEvent(this, "fa_albums", otherUserId);
		Bundle bundle = getIntent().getExtras();
		otherUserId = bundle.getString(INTENT_ACTION_USERID);
		otherNickName = bundle.getString(INTENT_ACTION_NICKNAME);

		userID=ActiveAccount.getInstance(ZApplication.getInstance()).getUID();
		accountInfo=AccountInfo.getInstance(userID);
		diaryManager = diaryManager.getInstance();
		setContentView(R.layout.activity_homepage_other_diary);
		inflater=LayoutInflater.from(this);
		diaryListView=(OtherDiaryListView) findViewById(R.id.dlv_list);
		
		vTime = findViewById(R.id.ll_time);
		vTimeReplace = findViewById(R.id.rl_time_replace);
		vTimeReplace.setVisibility(View.GONE);
		
		initReplace();
		
		wrapUser.userid = otherUserId;
		wrapUser.nickname = otherNickName;
		
		DisplayMetrics metric = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(metric);
        float density = metric.density;  // 屏幕密度（0.75 / 1.0 / 1.5）
        RADIUS = (int) (RADIUS * density / 2);
        isFriend = accountInfo.attentionContactManager.isMember(otherUserId);
        isBlackList = accountInfo.blackListContactManager.isMember(otherUserId);
        
        Log.d(TAG,"userID = " + userID + " otherUserId = " + otherUserId + " density = " + density + " isFriend = " + isFriend + " nickname = " + otherNickName);
		
		
		// 请求新数据
		pScreenHeight2=diaryListView.getScreenHight();
		nScreenHeight2=0-pScreenHeight2;
		diaryWidth=diaryListView.getDiaryWidth()+"";
		diaryHeight=diaryListView.getDiaryHeight()+"";
		backgroundWidth=diaryListView.getBackgroundWidth()+"";
		backgroundHeight=diaryListView.getBackgroundHeight()+"";
		
		Requester2.homePage(handler, otherUserId,
				"", "1",diaryWidth,diaryHeight,backgroundWidth,backgroundHeight);
		
		diaryListView.setOnRefreshListener(new OnRefreshListener() {

			@Override
			public void onRefresh() {
				refreshDiary();
			}

			@Override
			public void onMore() {
				loadMoreDiary();
			}

			@Override
			public void onAutoScroll(int l, int t, int oldl, int oldt) {
				scrollOperate(t,oldt);
			}
		});
		
		recentListView=(OtherRecentListView) findViewById(R.id.dlv_recentlist);
		recentListView.setOnInitLinstener(new OtherRecentListView.OnInitLinstener() {

			@Override
			public void init() {// 第一次进入选项时，加载该选项本地数据同时请求对应的网络数据
//				getLocalRecentDiary();
//				recentFreshMode=false;
				getServerRecentDiary();
			}
		},otherUserId);
		recentListView.setOnRefreshListener(new OnRefreshListener() {

			@Override
			public void onRefresh() {
				refreshRecent();
			}

			@Override
			public void onMore() {
				loadMoreRecent();
			}

			@Override
			public void onAutoScroll(int l, int t, int oldl, int oldt) {
				
			}
			
		});
		
		recentWidth=recentListView.getDiaryWidth()+"";
		recentHeight=recentListView.getDiaryHeight()+"";
		
		//baidu map
		flDiaryMap = (FrameLayout) findViewById(R.id.fl_activity_homepage_otherdiary_map);
		mv_diaryMap = (MyMapView) findViewById(R.id.bmv_activity_homepage_otherdiary_map);
		lv_info = (ListView) findViewById(R.id.lv_activity_homepage_otherdiary_map_list);
		iv_close = (ImageView) findViewById(R.id.iv_activity_homepage_otherdiary_map_tankuang_close);
		rltankuang = (RelativeLayout) findViewById(R.id.fl_activity_homepage_otherdiary_map_tankuang);
		ivMore=(ImageView) findViewById(R.id.iv_more);
		
		ivMore.setOnClickListener(this);
		iv_close.setOnClickListener(this);
		mv_diaryMap.setClickable(false);
		mv_diaryMap.setHandler(handler);
		mv_diaryMap.setDoubleClickZooming(false);
//		if (handler != null) {
//			Message message = handler.obtainMessage(HANDLER_FLAG_INIT_MAP_VIEW, "mapview");
//			handler.sendMessage(message);
//		}
		if (handler != null) {
			handler.sendEmptyMessage(HANDLER_FLAG_INIT_MAP_VIEW);
		}
		diarychangeType = DiaryChangeType.MYSELF_DIARY_LIST;
		// get geoCode
		myLocInfo = MyLocationInfo.getInstance(this);
		myLoc = myLocInfo.getLocation();
		
		vTitleChoice=findViewById(R.id.ll_title_choice);
		vTitleChoice.setOnClickListener(this);
		tvChoiceName=(TextView) findViewById(R.id.tv_my_album);
		
		tvFansCount=(TextView) findViewById(R.id.tv_fans_count);
		tvAttentionCount=(TextView) findViewById(R.id.tv_attention_count);
		fansCountLayout = (LinearLayout) findViewById(R.id.ll_fans_count);
		attentionCountLayout = (LinearLayout) findViewById(R.id.ll_attention_count);
		
		fansCountLayout.setOnClickListener(this);
		attentionCountLayout.setOnClickListener(this);
		
		ivZoneBackground=(WebImageView) findViewById(R.id.iv_homepage_zone_bg);
		
		ivSex=(ImageView) findViewById(R.id.iv_sex);
		ivPortrait=(WebImageView) findViewById(R.id.iv_homepage_portrait);
		tvNickname=(TextView) findViewById(R.id.tv_nickname);
		tvSignature=(TextView) findViewById(R.id.tv_signature);
		
		rlTitleBar=findViewById(R.id.rl_title);
		ivBack=(ImageView) findViewById(R.id.iv_back);
		tvCheckedNum=(TextView) findViewById(R.id.tv_checked_remove_num);
		
		ivBack.setOnClickListener(this);
		ivPortrait.setOnClickListener(this);
		
		ivHour=(ImageView) findViewById(R.id.iv_clock_h);
		ivSecond=(ImageView) findViewById(R.id.iv_clock_s);
		tvHourAndMinutes=(TextView) findViewById(R.id.tv_hour_minutes);
		tvDay=(TextView) findViewById(R.id.tv_day);
		ivMood=(ImageView) findViewById(R.id.iv_mood);
		
		iv_photo = (ImageView) findViewById(R.id.wiv_homepage_portrait);
		animateFirstListener = new AnimateFirstDisplayListener();
		imageLoader = ImageLoader.getInstance();
		imageLoader.init(ImageLoaderConfiguration.createDefault(this));
		options = new DisplayImageOptions.Builder()
			.showStubImage(R.drawable.moren_touxiang)
			.showImageForEmptyUri(R.drawable.moren_touxiang)
			.showImageOnFail(R.drawable.moren_touxiang)
			.cacheInMemory(true)
			.cacheOnDisc(true)
			.displayer(new SimpleBitmapDisplayer())
			.displayer(new CircularBitmapDisplayer())
			.build();
		iv_photo.setOnClickListener(this);
		
		initUserInfo();
		initView(diaryListView);
		
		bmHour=BitmapFactory.decodeResource(getResources(), R.drawable.shijian_clock_h);
		bmSecond=BitmapFactory.decodeResource(getResources(), R.drawable.shijian_clock_s);
		hWidth=bmHour.getWidth();
		hHeight=bmHour.getHeight();
		sWidth=bmSecond.getWidth();
		sHeight=bmSecond.getHeight();
		handler.postDelayed(timeTask, 0);
		
		tabType = DIARY_TYPE;
		// 注册天气receiver
//		updateWeatherReceiver = new UpdateWeatherReceiver();
//		getZReceiverManager().registerLocalZReceiver(updateWeatherReceiver);
	}
	
	@Override
	public void onResume() {
		super.onResume();
		UmengclickAgentWrapper.onResume(this);
		CmmobiClickAgentWrapper.onResume(this);
        mv_diaryMap.onResume();
        handler.sendEmptyMessageDelayed(MyMapView.HANDLER_FLAG_MAPVIEW_UPDATE, 500);
	}
	
	@Override
	public void onPause() {
		super.onPause();
		UmengclickAgentWrapper.onPause(this);
		CmmobiClickAgentWrapper.onPause(this);
		mv_diaryMap.onPause();
	}
	
	@Override
	public void onStop() {
		super.onStop();
		CmmobiClickAgentWrapper.onStop(this);
	}
	
	private void refreshDiary() {
		Log.d(TAG, "refreshDiary");
		diaryFreshMode=false;
		diaryListView.noMoreData(false);
		diaryListView.reset();
		Requester2.homePage(handler, otherUserId,
				"", "1",diaryWidth,diaryHeight,backgroundWidth,backgroundHeight);
	}
	
	private void loadMoreDiary() {
		Log.d(TAG, "loadMoreDiary");
		diaryFreshMode=true;
		Requester2.homePage(handler, otherUserId,
				diaryListView.getLastTime(), "2",diaryWidth,diaryHeight,backgroundWidth,backgroundHeight);
		
	}
	
	// 动态下拉刷新
	private void refreshRecent() {
		recentFreshMode=false;
		getServerRecentDiary();
	}
	
	// 动态上拉加载
	private void loadMoreRecent() {
		recentFreshMode=true;
		getServerRecentDiary();
	}
	
	
	// 加载初始本地数据
	private void getLocalRecentDiary() {
		Log.d(TAG, "getLocalRecentDiary");
		FilterType filterType = recentListView.getRecentType();
		ArrayList list=new ArrayList();
		switch (filterType) {
		case INTERACT_SHARE:
			list=diaryManager.getShareDiary(otherUserId);
			break;
		case INTERACT_PRAISE:
			list=diaryManager.getPraiseDiary(otherUserId);
			break;
		default:
			break;
		}
		if (list != null&&((ArrayList)list).size()>0) {
			recentListView.loadMore(list);
		}
	}
	
	// 请求服务器动态页日记数据
	private void getServerRecentDiary() {
		FilterType filterType = recentListView.getRecentType();
		Log.d(TAG,"getServerRecentDiary recentFreshMode = " + recentFreshMode);
		switch (filterType) {
		case INTERACT_SHARE:// 分享
			if (!recentFreshMode) {
				Requester2.listEnjoyDiary(handler, otherUserId,
						"", "1",recentWidth,recentHeight);
			} else {
				Log.d(TAG,"getServerRecentDiary loadmore endtime = " + diaryManager.getReponseEndtime(filterType));
				Requester2.listEnjoyDiary(handler, otherUserId,
						recentListView.getLastTime(), "2",recentWidth,recentHeight);
			}
			break;
		case INTERACT_PRAISE:// 赞
			// 获取赞日记列表
			if (!recentFreshMode) {
				Requester2.forwardDiaryList(handler, "1",
						recentListView.getFirstTime(), otherUserId,recentWidth,recentHeight);
			} else {
				Requester2.forwardDiaryList(handler, "2",
						recentListView.getLastTime(), otherUserId,recentWidth,recentHeight);
			}
			break;
		default:
			break;
		}
	}

	private String lastDiaryTime;
	private String firstDiaryTime;
	private static final int HANDLER_UPDATE_TIME=0x0011;
	private static final int HANDLER_UPDATE_TIME_REPLACE = 0x0012;
	GsonResponse2.timelineResponse response;
	@Override
	public boolean handleMessage(Message msg) {
		if (null == msg.obj && msg.what != HANDLER_FLAG_INIT_MAP_VIEW
				&& msg.what != MyMapView.HANDLER_FLAG_MAPVIEW_CLICK
				&& msg.what != MyMapView.HANDLER_FLAG_MAPVIEW_UPDATE
				&& msg.what != MyItemizedOverlay.HANDLER_FLAG_MAP_ITEM_TAP
				&& msg.what != MyMapView.HANDLER_FLAG_MAPVIEW_DOWN
				&& msg.what != MyMapView.HANDLER_FLAG_MAPVIEW_MOVE
				&& msg.what != MyMapView.HANDLER_FLAG_MAPVIEW_UP) {
			Log.e(TAG, msg.what + " response is null");
			diaryListView.loadDateError();
			recentListView.loadDateError();
			ZDialog.dismiss();
			Prompt.Alert(getString(R.string.prompt_network_error));
			return false;
		}
		switch (msg.what) {
		case HANDLER_UPDATE_TIME://更新时间
			if(msg.obj instanceof Object[]){
				Object[] obj = (Object[]) msg.obj;
				String timeHHmm = (String) obj[0];
				String timeDay = (String) obj[1];
				Bitmap bHour = (Bitmap) obj[2];
				Bitmap bSecond = (Bitmap) obj[3];
				tvHourAndMinutes.setText(timeHHmm);
//				tvMinutesReplace.setText(timeHHmm);
				tvDay.setText(timeDay);
//				tvHoursReplace.setText(timeDay);
				ivHour.setImageBitmap(bHour);
//				ivHoursReplace.setImageBitmap(bHour);
				ivSecond.setImageBitmap(bSecond);
//				ivMinutesReplace.setImageBitmap(bSecond);
			}
			break;
		case HANDLER_UPDATE_TIME_REPLACE:
			if (msg.obj instanceof Object[]) {
				Object[] obj = (Object[]) msg.obj;
				String timeHHmm = (String) obj[0];
				String timeDay = (String) obj[1];
				Bitmap bHour = (Bitmap) obj[2];
				Bitmap bSecond = (Bitmap) obj[3];
				tvMinutesReplace.setText(timeHHmm);
				tvHoursReplace.setText(timeDay);
				ivHoursReplace.setImageBitmap(bHour);
				ivMinutesReplace.setImageBitmap(bSecond);
			}
			break;
		case Requester2.RESPONSE_TYPE_HOME:{
			GsonResponse2.homeResponse res = (homeResponse) msg.obj;
			if("0".equals(res.status)){
				if("0".equals(res.hasnextpage)){//没有下一页
					diaryListView.noMoreData(true);
				}else{//有下一页
					diaryListView.noMoreData(false);
				}
			}
			
			if ("0".equals(res.status)) {
				if (!diaryFreshMode){
					diaryListView.reset();
					diaryListView.loadMore(null);
					diaryManager.updateUserInfo(otherUserId, res);
					setZoneBackground();
					setMood();
					setAttentionAndFans();
					
					setUserInfo();
				}
				if (res.diaries!=null&&res.diaries.length>0) {
					ArrayList<MyDiary> items=new ArrayList<MyDiary>();
					items.addAll(Arrays.asList(res.diaries));
					diaryListView.loadMore(items);
				}
			} else {
				diaryListView.noMoreData(true);
				diaryListView.loadDateError();
				Log.e(TAG, "RESPONSE_TYPE_LIST_SHARE_DIARY status is "
						+ res.status);
			}

			break;
		}
		case Requester2.RESPONSE_TYPE_ATTENTION:
			Log.d(TAG,"RESPONSE_TYPE_ATTENTION");
			GsonResponse2.attentionResponse attentionRes = (attentionResponse) msg.obj;
			if (attentionRes!=null && ATTENTION_SUCCESSED.equals(attentionRes.status)) {
				isFriend = true;
				ivAttention.setImageResource(R.drawable.btn_activity_homepage_other_diary_removefriends);
				accountInfo.attentionContactManager.addMember(wrapUser);
				changeLayoutWhenAttention();
				Prompt.Alert(this, "已关注");
				accountInfo.privateMsgManger.updateActByID(wrapUser.userid, "1");
			} else if (attentionRes!=null && "138117".equals(attentionRes.status)) {
				Prompt.Alert(this, "对方粉丝数达到上限");
			} else if (attentionRes!=null && "138116".equals(attentionRes.status)) {
				Prompt.Alert(this, "关注数达到上限");
			}
			break;
		case Requester2.RESPONSE_TYPE_CANCEL_ATTENTION:
			Log.d(TAG,"RESPONSE_TYPE_CANCEL_ATTENTION");
			
			GsonResponse2.cancelattentionResponse cancleAttentionRes = (GsonResponse2.cancelattentionResponse) msg.obj;
			if (cancleAttentionRes!=null && CANCLEATTENTION_SUCCESSED.equals(cancleAttentionRes.status)) {
				isFriend = false;
				ivAttention.setImageResource(R.drawable.btn_activity_homepage_other_diary_addfriends);
				accountInfo.attentionContactManager.removeMember(otherUserId);
				accountInfo.privateMsgManger.updateActByID(wrapUser.userid, "5");
				changeLayoutWhenCancleAttention();
				Prompt.Alert(this, "已取消关注");
			}
			break;
		case Requester2.RESPONSE_TYPE_BAKEUP_ATTENTION:
			Log.d(TAG,"RESPONSE_TYPE_BAKEUP_ATTENTION");
			GsonResponse2.markattentionResponse markAttentionRes = (GsonResponse2.markattentionResponse) msg.obj;
			if (markAttentionRes!=null && MARKATTENTION_SUCCESSED.equals(markAttentionRes.status)) {
				Log.d(TAG,"set remarkname successed");
			}
			break;
		case Requester2.RESPONSE_TYPE_SET_BLACKLIST:
			Log.d(TAG,"RESPONSE_TYPE_BAKEUP_ATTENTION");
			GsonResponse2.operateblacklistResponse blacklistResponse = (GsonResponse2.operateblacklistResponse) msg.obj;
			if (blacklistResponse!=null && MARKATTENTION_SUCCESSED.equals(blacklistResponse.status)) {
				Log.d(TAG,"set remarkname successed");
				isBlackList = !isBlackList;
				if (isBlackList) {
					Prompt.Alert(this, "已加入黑名单");
					isFriend = false;
					accountInfo.blackListContactManager.addMember(wrapUser);
					accountInfo.attentionContactManager.removeMember(otherUserId);
					ivBlacklist.setImageResource(R.drawable.btn_activity_homepage_other_diary_blackliston);
					changeLayoutWhenAddBlackList();
				} else {
					Prompt.Alert(this, "已移出黑名单");
					accountInfo.blackListContactManager.removeMember(otherUserId);
					ivBlacklist.setImageResource(R.drawable.btn_activity_homepage_other_diary_blacklistoff);
					changeLayoutWhenRemoveBlackList();
				}
			} else {
				if (blacklistResponse != null && "200500".equals(blacklistResponse.status)) {
					Prompt.Alert(this,"黑名单达到上限");
				} else {
					if (!isBlackList) {
						Prompt.Alert(this, "无法加入黑名单");
					} else {
						Prompt.Alert(this, "移出黑名单失败");
					}
				}
			}
			break;
		case Requester2.RESPONSE_TYPE_LIST_SHARE_DIARY: {
			shareEnjoyDiaryResponse res = (shareEnjoyDiaryResponse) msg.obj;
			if("0".equals(res.status)){
				if("0".equals(res.hasnextpage)){//没有下一页
					recentListView.noMoreData(true);
				}else{//有下一页
					recentListView.noMoreData(false);
				}
			}
			if(res!=null){
				if ( "0".equals(res.status)&&res.diaries!=null&&res.diaries.length>0) {
					if (!recentFreshMode) {
//						diaryManager.addShareDiaryResponse(otherUserId, res);
						recentListView.reset();
					}
					ArrayList<MyDiary> items=new ArrayList<MyDiary>();
					items.addAll(Arrays.asList(res.diaries));
					recentListView.loadMore(items);
				} else {
					recentListView.noMoreData(true);
					recentListView.loadDateError();
					Log.e(TAG, "RESPONSE_TYPE_LIST_SHARE_DIARY status is "
							+ res.status);
				}
			}

			break;
		}
		case Requester2.RESPONSE_TYPE_FORWARD_DIARY_LIST: {
			forwardDiaryListResponse res = (forwardDiaryListResponse) msg.obj;
			if ("0".equals(res.status)&&res.diaries!=null&&res.diaries.length>0) {
				if (!recentFreshMode) {
//					diaryManager.addPraiseDiaryResponse(userID, res);
					recentListView.reset();
				}
				ArrayList<MyDiary> items=new ArrayList<MyDiary>();
				items.addAll(Arrays.asList(res.diaries));
				recentListView.loadMore(items);
			} else {
				recentListView.loadDateError();
				Log.e(TAG, "RESPONSE_TYPE_LIST_SHARE_DIARY status is "
						+ res.status);
			}

			break;
		}
		case HANDLER_FLAG_INIT_MAP_VIEW:
			Log.e("==WR==", "======= HANDLER_FLAG_INIT_MAP_VIEW IN ! =======");
			rltankuang.setVisibility(View.INVISIBLE);
			if(/*myLoc==null || */diaryListView == null){
				break; //myLoc = new LocationData();
			}
			//模拟数据
			if(false){
				ArrayList<MyDiary> testdata = new ArrayList<MyDiary>();
				MyDiary[] tmpdata = simulateDiaries();
				for (MyDiary item : tmpdata) {
					if (item.latitude == null || item.latitude.equals("")
							|| item.longitude == null
							|| item.longitude.equals("")) {
						Log.e(TAG, "MyDiary latitude or longitude is null");
						continue;

					}
					testdata.add(item);
				}
			}
			if(myLoc != null) {
				Log.e(TAG, "loc latitude:" + myLoc.latitude + " longitude:" + myLoc.longitude);
			}
			if(/*testdata != null && testdata.size() > 0*/diaryListView.getDiaryList() != null && diaryListView.getDiaryList().size() > 0){
				objs = MapItemHelper.showNearByItems(this, handler, getResources(), mv_diaryMap, null, /*testdata*/diaryListView.getDiaryList(), true);
				Log.e(TAG, "diaryListView.getDiaryList() size:" + diaryListView.getDiaryList().size());
			}else{
				mv_diaryMap.getOverlays().clear();
				try {
					//中国地图
					GeoPoint gp_center = new GeoPoint((int)(35 * 1e6), (int)(104 * 1e6));
					mv_diaryMap.getController().setZoom((float) 4.6);
					mv_diaryMap.getController().setCenter(gp_center);
					mv_diaryMap.getController().animateTo(gp_center);
				} catch (NullPointerException e) {
					Log.e(TAG, "Baidu Map Internal NullPointer");
					Toast.makeText(this, "百度地图缩放出错，请刷新重试！", Toast.LENGTH_LONG).show();
		    	}
				Requester2.homePage(handler, otherUserId,
						diaryManager.getReponseFirsttime(FilterType.OTHERPAGE), "1",diaryWidth,diaryHeight,backgroundWidth,backgroundHeight);
			}
			break;
		case MyMapView.HANDLER_FLAG_MAPVIEW_CLICK:
			rltankuang.setVisibility(View.INVISIBLE);
			//lv_info.setVisibility(View.INVISIBLE);
			break;
			
		case MyMapView.HANDLER_FLAG_MAPVIEW_UPDATE:
//			Log.e(TAG, "handler update: level:" + mv_diaryMap.getZoomLevel());
			//模拟数据
			if (false) {
				ArrayList<MyDiary> testdata2 = new ArrayList<MyDiary>();
				MyDiary[] tmpdata2 = simulateDiaries();
				for (MyDiary item : tmpdata2) {
					if (item.latitude == null || item.latitude.equals("")
							|| item.longitude == null
							|| item.longitude.equals("")) {
						Log.e(TAG, "MyDiary latitude or longitude is null");
						continue;

					}
					testdata2.add(item);
				}
			}
			if(/*testdata2 != null && testdata2.size() > 0*/diaryListView != null && diaryListView.getDiaryList() != null && diaryListView.getDiaryList().size() > 0) {
				Log.e(TAG, "diaryListView.getDiaryList() size = " + diaryListView.getDiaryList().size());
				objs = MapItemHelper.showNearByItems(this, handler, getResources(), mv_diaryMap, null, /*testdata2*/diaryListView.getDiaryList(), false);
			}
			break;
		case MyItemizedOverlay.HANDLER_FLAG_MAP_ITEM_TAP:
			int index = (Integer) msg.obj;
			if(objs==null || objs[index]==null){
				break;
			}
			final Item it = objs[index];
			if(it.type==1){
				list_adapter = new MapNearbyListAdapter(this, handler,
						R.layout.row_list_diary_nearby, R.id.tv_row_list_diary_nick,
						it.nearby_list, rltankuang);
				lv_info.setCacheColorHint(0);
				lv_info.setAdapter(list_adapter);
				lv_info.setOnItemClickListener(new OnItemClickListener() {

					@Override
					public void onItemClick(AdapterView<?> parent, View view, int position,
							long id) {
						// TODO Auto-generated method stub
						Object object = view.getTag();
						if(object instanceof MyDiary) {
							MyDiary tempDiary = (MyDiary) object;
							String diaryuuid=tempDiary.diaryuuid;
							DiaryManager.getInstance().setDetailDiaryList(it.nearby_list);
							startActivity(new Intent(ZApplication.getInstance(), DiaryDetailActivity.class)
							.putExtra(DiaryDetailActivity.INTENT_ACTION_DIARY_UUID, diaryuuid));
							rltankuang.setVisibility(View.INVISIBLE);
						}
					}
					
				});
				list_adapter.notifyDataSetChanged();
				rltankuang.setVisibility(View.VISIBLE);
			}
			break;
		default:
			break;
		}
		return false;
	}
	
	private void initView(View view) {
		Log.d(TAG,"initView isBlackList = " + isBlackList + " isFriend = " + isFriend);
		mB1 = (LinearLayout) view.findViewById(R.id.b1);
		mB2 = (LinearLayout) view.findViewById(R.id.b2);
		mB3 = (LinearLayout) view.findViewById(R.id.b3);
		mBoss = (LinearLayout) view.findViewById(R.id.b5);
		ivExtend = (ImageView) view.findViewById(R.id.iv_extend);
		ivAttention = (ImageView) view.findViewById(R.id.iv_add_attention);
		ivPrivateLetter = (ImageView) view.findViewById(R.id.iv_privateletter);
		ivBlacklist = (ImageView) view.findViewById(R.id.iv_blacklist);
//		ivRemarksName = (ImageView) view.findViewById(R.id.iv_remarksname);
		ivExtendInvisible = (ImageView) view.findViewById(R.id.iv_extend_invisible);
		
		if (isBlackList) {
			ivBlacklist.setImageResource(R.drawable.btn_activity_homepage_other_diary_blackliston);
		} else {
			ivBlacklist.setImageResource(R.drawable.btn_activity_homepage_other_diary_blacklistoff);
		}
		
		if (isFriend) {
			ivAttention.setImageResource(R.drawable.btn_activity_homepage_other_diary_removefriends);
		} else {
			ivAttention.setImageResource(R.drawable.btn_activity_homepage_other_diary_addfriends);
		}
		
		ivExtend.setOnClickListener(this);
		ivAttention.setOnClickListener(this);
		ivPrivateLetter.setOnClickListener(this);
		ivBlacklist.setOnClickListener(this);
//		ivRemarksName.setOnClickListener(this);
		
		ivExtend.setImageResource(R.drawable.btn_activity_homepage_other_diary_extend);
		mList.clear();
		mList.add(mB1);
		mList.add(mB2);
		mList.add(mB3);
//		mList.add(mB4);
		
		isFirstInitAnim = true;
		isIn = true;
		isEnd = false;
		mOutAnimatinSets.clear();
		mInAnimatinSets.clear();
	}
	
	private void changeLayoutWhenAttention() {
		if (mList.size() < 3) {
			return;
		}
		
		if (isIn) {
			
		} else {
			mB3.setVisibility(View.VISIBLE);
			LayoutParams params2 = new LayoutParams(LayoutParams.WRAP_CONTENT,
					LayoutParams.WRAP_CONTENT);
			params2.setMargins(rects[2].left, rects[2].top, 0, 0);
			mB3.setLayoutParams(params2);
			mB3.requestLayout();
		}
	}
	
	private void changeLayoutWhenCancleAttention() {
		if (mList.size() < 3) {
			return;
		}
		
		if (isIn) {
			
		} else {
			LayoutParams params2 = new LayoutParams(LayoutParams.WRAP_CONTENT,
					LayoutParams.WRAP_CONTENT);
			params2.setMargins(rects[2].left, rects[2].top, 0, 0);
			mB3.setLayoutParams(params2);
			mB3.requestLayout();
			mB3.setVisibility(View.INVISIBLE);
		}
	}
	
	private void changeLayoutWhenAddBlackList() {
		ivAttention.setImageResource(R.drawable.btn_activity_homepage_other_diary_addfriends);
	}
	
	private void changeLayoutWhenRemoveBlackList() {
		if (isIn) {
			
		} else {
			LayoutParams params2 = new LayoutParams(LayoutParams.WRAP_CONTENT,
					LayoutParams.WRAP_CONTENT);
			params2.setMargins(rects[2].left, rects[2].top, 0, 0);
			mB3.setLayoutParams(params2);
			mB3.requestLayout();
			mB3.setVisibility(View.INVISIBLE);
		}
	}
	
	private void resetLayout() {
		int size = mList.size();
		for (int i = 0; i < size; i++) {
			LinearLayout layout = mList.get(i);
			layout.clearAnimation();
			LayoutParams leftParams = new LayoutParams(LayoutParams.WRAP_CONTENT,
					LayoutParams.WRAP_CONTENT);
			leftParams.setMargins(mBoss.getLeft(), mBoss.getTop(), 0, 0);
			layout.setLayoutParams(leftParams);
			layout.requestLayout();
			layout.setVisibility(View.INVISIBLE);
		}
	}
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.ll_fans_count:
			startActivity(new Intent(this,PersonFansActivity.class)
			.putExtra(PersonFansActivity.INTENT_ACTION_USERID, otherUserId));
			break;
		case R.id.ll_attention_count:
			startActivity(new Intent(this,MyAttentionMembersActivity.class)
			.putExtra(MyAttentionMembersActivity.INTENT_ACTION_USERID, otherUserId));
			break;
		case R.id.iv_activity_homepage_otherdiary_map_tankuang_close:
			rltankuang.setVisibility(View.INVISIBLE);
			break;
		case R.id.iv_back:
			if (screenMode == ALBUMS_MODE) {
				diaryListView.cancelRemove();
				onBackPressed();
			} else {
				diaryListView.setVisibility(View.VISIBLE);
				recentListView.setVisibility(View.GONE);
				flDiaryMap.setVisibility(View.GONE);
				screenMode = ALBUMS_MODE;
				ivMore.setVisibility(View.VISIBLE);
				tvChoiceName.setText(R.string.homepage_ta_album);
			}
			break;
		case R.id.iv_weather_icon:
			MyWeatherInfo.getInstance(this).updateWeather(true);
			break;
		case R.id.iv_homepage_zone_bg:
			Log.d(TAG, "iv_homepage_zone_bg");
			break;
		case R.id.iv_homepage_portrait:
			Log.d(TAG, "iv_homepage_portrait");
			iv_photo.setVisibility(View.VISIBLE);
			imageLoader.displayImage(headUrl, iv_photo, options, animateFirstListener, ActiveAccount.getInstance(this).getUID(), 0);
			break;
		case R.id.wiv_homepage_portrait:
			iv_photo.setVisibility(View.GONE);
			break;
		case R.id.iv_extend:
			if (isFirstInitAnim) {
				initAnimation();
				isFirstInitAnim = false;
			}
			getFocus(v);
			if (!isEnd) {
				if (v.getAnimation() == null) {
					final float degree;
					if (isIn) {
						degree = 360;
					} else {
						degree = -360;
					}
					Log.d(TAG,"onClick ******************");
					RotateAnimation rotateAnimation = new RotateAnimation(
							0f, degree, Animation.RELATIVE_TO_SELF, 0.5f,
							Animation.RELATIVE_TO_SELF, 0.5f);
					rotateAnimation.setDuration(500);
					v.startAnimation(rotateAnimation);
					if (isIn) {
						isIn = false;
						isEnd = true;
						startOutAnimation();
					} else {
						isIn = true;
						startInAnimation();
					}
				}
			}
			break;
		case R.id.iv_add_attention:
			Log.d(TAG,"iv_add_attention isFriend = " + isFriend);
			if (!isFriend) {
				if (isBlackList) {
					new Xdialog.Builder(this)
					.setTitle("警告")
					.setMessage("请先解除黑名单再添加好友")
					.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
						}
					})
					.create().show();
				} else {
					CmmobiClickAgentWrapper.onEvent(this, "fa_add_fr", otherUserId);
					Requester2.attention(handler, otherUserId);
				}
			} else {
				CmmobiClickAgentWrapper.onEvent(this, "fa_cel_fr", otherUserId);
				Requester2.cancelAttention(handler, otherUserId, "1");
			}
			break;
		case R.id.iv_privateletter:
			if (wrapUser != null && wrapUser.userid != null && wrapUser.nickname != null) {
				Intent intent = new Intent(this, FriendsSessionPrivateMessageActivity.class);
				
				intent.putExtra("wrapuser", wrapUser.toString());// 压入数据
				CmmobiClickAgentWrapper.onEvent(this, "fa_pr_letter", otherUserId);
				startActivity(intent);
			} else {
				Prompt.Alert(this, "非法用户");
			}
			Log.d(TAG,"iv_add_attention");
			break;
		case R.id.iv_blacklist:
			if (!isBlackList) {
			    Requester2.operateBlacklist(handler, otherUserId, "1");
			    CmmobiClickAgentWrapper.onEvent(this, "fa_add_bk", otherUserId);
			} else {
				Requester2.operateBlacklist(handler, otherUserId, "2");
				CmmobiClickAgentWrapper.onEvent(this, "fa_cel_bk", otherUserId);
			}
			break;
		/*case R.id.iv_remarksname:
			startActivityForResult(new Intent(this,SettingRemarkNameActivity.class), REQUEST_CODE);
			break;*/
		case R.id.iv_more:
			showMoreChoice();
			break;
		case R.id.ll_ta_interact:
			if(portraitMenu!=null)portraitMenu.dismiss();
			diaryListView.setVisibility(View.GONE);
			recentListView.setVisibility(View.VISIBLE);
			vTitleChoice.setClickable(false);
			tvChoiceName.setText("Ta的互动");
			flDiaryMap.setVisibility(View.GONE);
			vTimeReplace.setVisibility(View.GONE);
			
			if (null==recentListView.getRecentType()) {
				recentListView.setDefaultChecked();
			}
			ivMore.setVisibility(View.GONE);
			screenMode = RECENT_MODE;
			break;
		case R.id.ll_map_mode:
			if(portraitMenu!=null)portraitMenu.dismiss();
			diaryListView.setVisibility(View.GONE);
			vTitleChoice.setClickable(false);
			recentListView.setVisibility(View.GONE);
			flDiaryMap.setVisibility(View.VISIBLE);
			CmmobiClickAgentWrapper.onEvent(this, "fa_map_m");
			CmmobiClickAgentWrapper.onEventBegin(this, "fa_map_m");
			tvChoiceName.setText(R.string.homepage_my_map_mode);
			vTimeReplace.setVisibility(View.GONE);
			if (handler != null) {
				handler.sendEmptyMessage(HANDLER_FLAG_INIT_MAP_VIEW);
			}
			ivMore.setVisibility(View.GONE);
			screenMode = MAP_MODE;
			break;
		default:
			break;
		}
	}
	
	private PopupWindow portraitMenu;
	//显示更多选项
	private void showMoreChoice() {
		View view = inflater.inflate(R.layout.activity_homepage_other_more_menu,
				null);
		portraitMenu = new PopupWindow(view, LayoutParams.WRAP_CONTENT,
				LayoutParams.WRAP_CONTENT, true);
		portraitMenu.setBackgroundDrawable(getResources().getDrawable(
				R.drawable.dot_big));
		portraitMenu.showAsDropDown(ivMore,0,8);
		view.findViewById(R.id.ll_ta_interact).setOnClickListener(this);
		view.findViewById(R.id.ll_map_mode).setOnClickListener(this);
		ImageView ivInteract=(ImageView) view.findViewById(R.id.iv_my_interact);
		TextView tvInteract=(TextView) view.findViewById(R.id.tv_my_interact);
		ImageView ivMap=(ImageView) view.findViewById(R.id.iv_map_mode);
		TextView tvMap=(TextView) view.findViewById(R.id.tv_map_mode);
		ivInteract.setImageResource(R.drawable.gengduo_hudong);
		tvInteract.setText(R.string.homepage_ta_interact);
		ivMap.setImageResource(R.drawable.gengduo_map);
		tvMap.setText(R.string.homepage_my_map_mode);
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		if (requestCode==REQUEST_CODE) {  
            switch (resultCode) {  
            case RESULT_OK:  
                String dataString=data.getExtras().getString("remarkname");  
                if (dataString != null && !"".equals(dataString)) {
                	Requester2.markAttention(handler,dataString,otherUserId);
                    Log.d(TAG, dataString);
                }
                break;  
            default:  
                break;  
            }  
        }
		super.onActivityResult(requestCode, resultCode, data);
	}

	private void getFocus(View view) {
		if (view != null) {
			if (!view.requestFocus()) {
				view.setFocusable(true);
				view.setFocusableInTouchMode(true);
				view.requestFocus();
			}
		}
	}
	
	private void startInAnimation() {
		for (int i = 0; i < mList.size(); i++) {
			LinearLayout layout = mList.get(i);
			if (layout == mB3) {
				Log.d(TAG,"layout blackList");
				if (isBlackList || isFriend) {
					layout.setVisibility(View.VISIBLE);
					layout.startAnimation(mInAnimatinSets.get(i));
				} else {
					layout.setVisibility(View.GONE);
				}
			} else {
				layout.setVisibility(View.VISIBLE);
				layout.startAnimation(mInAnimatinSets.get(i));
			}
		}
	}

	private void startOutAnimation() {
		Log.d(TAG,"startOutAnimation ");
		for (int i = 0; i < mList.size(); i++) {
			LinearLayout layout = mList.get(i);
			if (layout == mB3) {
				Log.d(TAG,"layout blackList");
				if (isBlackList || isFriend) {
					layout.setVisibility(View.VISIBLE);
					layout.startAnimation(mOutAnimatinSets.get(i));
				} else {
					layout.setVisibility(View.GONE);
				}
			} else {
				layout.setVisibility(View.VISIBLE);
				layout.startAnimation(mOutAnimatinSets.get(i));
			}
			
		}
		
	}
	
	private void initAnimation() {
		int size = mList.size();
		for (int i = 0; i < size; i++) {
			final LinearLayout layout = mList.get(i);
			int x = 0;
			int y = 0;
			double angle = 100 + 50 * i;
			
			x = (int) (RADIUS * Math.cos(Math.toRadians(angle)));
			y = -(int) (RADIUS * Math.sin(Math.toRadians(angle)));
			
			System.out.println("===============================");
			System.out.println(i + "x ====>" + x);
			System.out.println(i + "y ====>" + y);
			System.out.println(i + "angle ====>" + (angle));
			final int left = mBoss.getLeft() + x;
			final int top = mBoss.getTop() + y;
			final int right = mBoss.getRight() + x;
			final int bottom = mBoss.getBottom() + y;
			rects[i] = new Rect(left, top, right, bottom);
			
			Log.d(TAG,"ivleft = " + mBoss.getLeft() + " ivtop = " + mBoss.getTop() 
					+ " ivright = " + mBoss.getRight() + " ivbottom = " + mBoss.getBottom());
			Log.d(TAG,"left = " + left + " top = " + top + " right = " + right + " bottom = " + bottom);

			TranslateAnimation outTranAni = new TranslateAnimation(0, x, 0, y);
			outTranAni.setFillAfter(true);
			outTranAni.setDuration(duratime);

			AnimationSet outSet = new AnimationSet(true);
			outSet.setFillAfter(true);
			outSet.addAnimation(outTranAni);
			mOutAnimatinSets.add(outSet);
			
			outSet.setAnimationListener(new AnimationListener() {
				@Override
				public void onAnimationEnd(Animation animation) {
					Log.d(TAG,"outSet onAnimationEnd in");
					if (layout != null) {
						isEnd = false;
					}
					layout.clearAnimation();
//					Log.d(TAG,"afterLeft = " + layout.getLeft());
					LayoutParams leftParams = new LayoutParams(LayoutParams.WRAP_CONTENT,
							LayoutParams.WRAP_CONTENT);
					leftParams.setMargins(left, top, 0, 0);
					layout.setLayoutParams(leftParams);
					layout.requestLayout();
//					layout.layout(left, top, right, bottom);
//					Log.d(TAG,"left = " + left + " top = " + top + " right = " + right + " bottom = " + bottom);
				}

				@Override
				public void onAnimationRepeat(Animation animation) {
				}

				@Override
				public void onAnimationStart(Animation animation) {
					Log.d(TAG,"outSet onAnimationStart in");
					LayoutParams leftParams = new LayoutParams(LayoutParams.WRAP_CONTENT,
							LayoutParams.WRAP_CONTENT);
					leftParams.setMargins(mBoss.getLeft(), mBoss.getTop(), 0, 0);
					layout.setLayoutParams(leftParams);
					layout.requestLayout();
				
					ivExtend.setImageResource(R.drawable.btn_activity_homepage_other_diary_shrink);
				}

			});
			TranslateAnimation inTranAni = new TranslateAnimation(0,
					-x, 0, -y);
			
			inTranAni.setFillAfter(true);
			inTranAni.setDuration(duratime);
			AnimationSet inSet = new AnimationSet(true);
			inSet.setFillAfter(true);
			inSet.addAnimation(inTranAni);
			mInAnimatinSets.add(inSet);
			inSet.setAnimationListener(new AnimationListener() {

				@Override
				public void onAnimationStart(Animation animation) {
					ivExtend.setImageResource(R.drawable.btn_activity_homepage_other_diary_extend);
					LayoutParams leftParams = new LayoutParams(LayoutParams.WRAP_CONTENT,
							LayoutParams.WRAP_CONTENT);
					leftParams.setMargins(left, top, 0, 0);
					layout.setLayoutParams(leftParams);
					layout.requestLayout();
				}

				@Override
				public void onAnimationRepeat(Animation animation) {
				}

				@Override
				public void onAnimationEnd(Animation animation) {
					layout.clearAnimation();
					LayoutParams leftParams = new LayoutParams(LayoutParams.WRAP_CONTENT,
							LayoutParams.WRAP_CONTENT);
					leftParams.setMargins(mBoss.getLeft(), mBoss.getTop(), 0, 0);
					layout.setLayoutParams(leftParams);
					layout.requestLayout();
					layout.setVisibility(View.INVISIBLE);
				}
			});
		}
	}
	
	private static final int LOOP_PERIOD=30000;//毫秒
	Runnable timeTask=new Runnable() {
		
		@Override
		public void run() {
			Calendar c=Calendar.getInstance();
			String timeHHmm=DateUtils.dateToString(c.getTime(), DateUtils.DATE_FORMAT_TODAY);
			String timeDay=DateUtils.dateToString(c.getTime(), DateUtils.DATE_FORMAT_NORMAL_1);
			if(timeDay.startsWith("0"))timeDay=timeDay.substring(1, timeDay.length());
			float hour=c.get(Calendar.HOUR_OF_DAY);
//			float seconds=c.get(Calendar.SECOND);
			float seconds=c.get(Calendar.MINUTE);//分
			float hAngle=(hour/12f)*360f;
			float hSecond=(seconds/60f)*360f;
			Matrix m=new Matrix();
			m.setRotate(hAngle);
			Bitmap bHour=Bitmap.createBitmap(bmHour, 0, 0, hWidth, hHeight, m, true);
			m.setRotate(hSecond);
			Bitmap bSecond=Bitmap.createBitmap(bmSecond, 0, 0, sWidth, sHeight, m, true);
			handler.obtainMessage(HANDLER_UPDATE_TIME,new Object[]{timeHHmm,timeDay,bHour,bSecond}).sendToTarget();
			handler.postDelayed(this, LOOP_PERIOD);
		}
	};
	
	class updateTimeThread implements Runnable{

    	private String timemilli;
    	public updateTimeThread(String timemilli){
    		this.timemilli=timemilli;
    	}
    	
		@Override
		public void run() {
			if(timemilli!=null){
				long time=Long.parseLong(timemilli);
				Calendar c=Calendar.getInstance();
				c.setTimeInMillis(time);
				String timeHHmm = DateUtils.dateToString(c.getTime(),
						DateUtils.DATE_FORMAT_TODAY);
				String timeDay = DateUtils.dateToString(c.getTime(),
						DateUtils.DATE_FORMAT_NORMAL_1);
				if (timeDay.startsWith("0"))
					timeDay = timeDay.substring(1, timeDay.length());
				float hour = c.get(Calendar.HOUR_OF_DAY);
				float seconds = c.get(Calendar.MINUTE);// 分
				float hAngle = (hour / 12f) * 360f;
				float hSecond = (seconds / 60f) * 360f;
				Matrix m = new Matrix();
				m.setRotate(hAngle);
				Bitmap bHour = Bitmap.createBitmap(bmHour, 0, 0, hWidth, hHeight,
						m, true);
				m.setRotate(hSecond);
				Bitmap bSecond = Bitmap.createBitmap(bmSecond, 0, 0, sWidth,
						sHeight, m, true);
				handler.obtainMessage(HANDLER_UPDATE_TIME_REPLACE,
						new Object[] { timeHHmm, timeDay, bHour, bSecond })
						.sendToTarget();
			}
			
		}
    } 
    
	
	@Override
	protected void onDestroy() {
		handler.removeCallbacks(timeTask);
//		getZReceiverManager().unregisterLocalZReceiver(updateWeatherReceiver);
		mv_diaryMap.destroy();
		super.onDestroy();
	}

	protected void measureView(View child) {
		ViewGroup.LayoutParams p = child.getLayoutParams();
		if (p == null) {
			p = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT,
					ViewGroup.LayoutParams.WRAP_CONTENT);
		}
		int childWidthSpec = ViewGroup.getChildMeasureSpec(0, 0, p.width);
		int lpHeight = p.height;
		int childHeightSpec;
		if (lpHeight > 0) {
			childHeightSpec = MeasureSpec.makeMeasureSpec(lpHeight,
					MeasureSpec.EXACTLY);
		} else {
			childHeightSpec = MeasureSpec.makeMeasureSpec(0,
					MeasureSpec.UNSPECIFIED);
		}
		child.measure(childWidthSpec, childHeightSpec);
	}
	
	static int moods[]={
		R.drawable.xinqing_chijing,
		R.drawable.xinqing_gaoxing,
		R.drawable.xinqing_jusang,
		R.drawable.xinqing_ku,
		R.drawable.xinqing_shengqi,
		R.drawable.xinqing_tiaopi,
		R.drawable.xinqing_weiqu,
		R.drawable.xinqing_xinhuanufang,
		R.drawable.xinqing_yanwu
	};
	
	
	static class FilterViewHolder{
		TextView tvFilter;
	}
	
	static class ViewHolder{
		ImageView ivMood;
	}
	
	private void initUserInfo(){
		setUserInfo();
//		setWeather(false);
		setMood();
		setZoneBackground();
		setAttentionAndFans();
	}
	
	private void setUserInfo(){
		Log.d(TAG,"setUserInfo in");
		
		String nickname=otherNickName;
		String signature="";
		String sex="";
		OtherUserInfo res= diaryManager.getOtherUserInfo(otherUserId);
		wrapUser.userid = otherUserId;
		wrapUser.nickname = otherNickName;
		
		if (res != null) {
			wrapUser.attentioncount = res.attendedCount;
			wrapUser.fanscount = res.fansCount;
			wrapUser.headimageurl = res.headimageurl;
			wrapUser.nickname = res.nickname;
			wrapUser.sex = res.sex;
			wrapUser.signature = res.signature;
			headUrl = res.headimageurl;
			nickname = res.nickname;
			signature = res.signature;
			sex=res.sex;
		}
		
		if("0".equals(sex)){
			ivSex.setVisibility(View.VISIBLE);
			ivSex.setImageResource(R.drawable.mapnan);
		}else if("1".equals(sex)){
			ivSex.setVisibility(View.VISIBLE);
			ivSex.setImageResource(R.drawable.mapnv);
		}else{
			ivSex.setVisibility(View.INVISIBLE);
		}
		
		Log.d(TAG, "headUrl=" + headUrl);
		Log.d(TAG, "nickname=" + nickname);
		Log.d(TAG, "signature=" + signature);
		ivPortrait.setImageUrl(0, 1,headUrl, true);
		tvNickname.setText(nickname);
		tvSignature.setText(signature);
	}
	
	/*private void setWeather(boolean isActive) {
		Log.d(TAG, "UI weather updating!");
		MyWeather weather = CommonInfo.getInstance().myWeather;
		if (weather != null) {
			String city = weather.city;
			String district = weather.district;
			String code = weather.addrCode;
			String description = null;
			String weatherurl = null;
			if(weather.desc != null && weather.desc.length > 0) {
				description = weather.desc[0].description;
				weatherurl = weather.desc[0].weatherurl;
			}
			String region = district + "(" + city + ")";
			if (district != null && city != null) {
				tvWeatherRegion.setText(region);
				tvWeatherRegionReplace.setText(region);
			} else {
				if(isActive) {
					//Prompt.Alert(getString(R.string.prompt_network_error));
				}
			}
			if(description != null) {
				tvWeatherTemp.setText(description);
				tvWeatherTempReplace.setText(description);
			} else {
				tvWeatherTemp.setText("-°C/-°C");
				tvWeatherTempReplace.setText("-°C/-°C");
				if(isActive) {
					Prompt.Alert(getString(R.string.prompt_weather_error));
				}
			}
			ivWeatherIcon.setImageUrl(R.drawable.tianqi_weizhi, 1, weatherurl,false);
			ivWeatherReplace.setImageUrl(R.drawable.tianqi_weizhi, 1, weatherurl,false);
		}
	}*/
	
	// 心情数组中的序号
	private int moodPosition;
	private void setMood() {
		String mood="0";
		OtherUserInfo otherUserInfo=diaryManager.getOtherUserInfo(otherUserId);
		if(otherUserInfo!=null){
			try {
				if (otherUserInfo.mood != null) {
					mood = otherUserInfo.mood;
				}
				moodPosition=Integer.parseInt(mood);
				ivMood.setImageResource(moods[moodPosition]);
			} catch (NumberFormatException e) {
				e.printStackTrace();
			}
		}
	}
	
	private void setZoneBackground() {
		Log.d(TAG,"setZoneBackground in");
		String url="";
		OtherUserInfo otherUserInfo=diaryManager.getOtherUserInfo(otherUserId);
		if(otherUserInfo!=null){
			url=otherUserInfo.zoneBackGround;
			Log.d(TAG, "setZoneBackground->url="+url);
			ivZoneBackground.setImageUrl(R.drawable.homepage_zone_moren, 1,url, false);
			ViewGroup.LayoutParams params=ivZoneBackground.getLayoutParams();
			params.height=diaryListView.getBackgroundHeight();
			ivZoneBackground.setLayoutParams(params);
		}
	}
	
	// 设置关注数和粉丝数
	private void setAttentionAndFans() {
		Log.d(TAG,"setAttentionAndFans in");
		OtherUserInfo otherUserInfo=diaryManager.getOtherUserInfo(otherUserId);
		if(otherUserInfo!=null){
			String fansCount=otherUserInfo.fansCount;
			String attentioncount=otherUserInfo.attendedCount;
			tvFansCount.setText(fansCount);
			tvAttentionCount.setText(attentioncount);
			Log.d(TAG,"setAttentionAndFans tvFansCount = " + fansCount + " tvAttentionCount = " + attentioncount);
		}
	}
	
	// 接收天气更新广播
	/*private class UpdateWeatherReceiver extends ZBroadcastReceiver {

		private UpdateWeatherReceiver() {
			addAction(MyWeatherInfo.ACTION_UPDATE_WEATHERINFO);
		}

		@Override
		public void onReceive(Context context, Intent intent) {
			Log.d(TAG, "intent.getAction() = " + intent.getAction());
			if (intent.getAction().equals(
					MyWeatherInfo.ACTION_UPDATE_WEATHERINFO)) {
				Log.d(TAG, "need to update UI weather");
				boolean isactive = intent.getBooleanExtra("isActive", false);
//				setWeather(isactive);
			}
		}
	}*/

	//模拟数据
	private String[][] loc = {
			{ "114.275071", "30.5616", "1", "Lucy", "103876" },
			{ "114.319802", "30.559769", "2", "Jack", "103883" },
			{ "114.323521", "30.534615", "1", "Fuck", "103663" },
			{ "114.405734", "30.511968", "1", "Jane", "103884" },
			{ "114.271204", "30.484087", "2", "John", "103885" },
			{ "114.280816", "30.714145", "2", "Yao", "103886" },
			{ "114.269461", "30.545673", "2", "Mick", "103887" },
			{ "114.301944", "30.54362", "1", "Doris", "103888" },
			{ "114.424401", "30.55606", "1", "Catherine", "103889" },
			{ "114.35577", "30.571123", "2", "Wang", "103890" } };
	
    private MyDiary[] simulateDiaries() {
    	MyDiary[] diaries = new MyDiary[10];
    	for(int i = 0; i < diaries.length; i++) {
    		MyDiary d = new MyDiary();
    		d.longitude  = loc[i][0];
    		d.latitude = loc[i][1];
    		d.sex = loc[i][2];
    		d.nickname = loc[i][3];
    		d.attachs = new diaryAttach[1];
    		d.diaryid = loc[i][4];
    		diaryAttach da = new diaryAttach();
    		da.attachlevel = "1";
    		da.attachtype = "2";
    		d.attachs[0] = da;
    	    diaries[i] = d;
    	}
    	return diaries;
    }
    
    private void scrollOperate(int t,int oldt){
    	int[] location=new int[2];
    	vTime.getLocationInWindow(location);
		int height=0;
		int y=location[1];
		if(View.GONE!=rlTitleBar.getVisibility()){
			height=rlTitleBar.getHeight();
		}
		if(y<height){
			//显示replace
			if(View.VISIBLE!=vTimeReplace.getVisibility())
				vTimeReplace.setVisibility(View.VISIBLE);
			handler.post(new Runnable() {
				@Override
				public void run() {
					checkChildsPosition();
				}
			});
			
		}else{
			//隐藏replace
			if(View.GONE!=vTimeReplace.getVisibility())
				vTimeReplace.setVisibility(View.GONE);
		}
		int offset=t-oldt;
		if(offset>10){//上滑 隐藏
			if(0==isSlipUP||1!=isSlipUP){
				if(TimeHelper.getInstance().now()-timeOffset<300)return;
				timeOffset=TimeHelper.getInstance().now();
				isSlipUP=1;
				rlTitleBar.setVisibility(View.GONE);
			}
		}else if(offset<-10){//下滑 显示
			if(0==isSlipUP||2!=isSlipUP){
				if(TimeHelper.getInstance().now()-timeOffset<300)return;
				timeOffset=TimeHelper.getInstance().now();
				isSlipUP=2;
				rlTitleBar.setVisibility(View.VISIBLE);
			}
		}
//		else{
//			if(TimeHelper.getInstance().now()-slidOffset<20)return;
//			slidOffset=TimeHelper.getInstance().now();
//			Log.d(TAG, "停止滑动");
//			handler.post(new Runnable() {
//				@Override
//				public void run() {
//					checkChildsPosition();
//				}
//			});
//		}
    }
    
    private void initReplace(){
//    	ivWeatherReplace=(WebImageView) vCenterReplace.findViewById(R.id.iv_weather_icon);
//    	ivWeatherReplace.setOnClickListener(this);
//    	tvWeatherTempReplace=(TextView) vCenterReplace.findViewById(R.id.tv_weather_temp);
//    	tvWeatherRegionReplace=(TextView) vCenterReplace.findViewById(R.id.tv_weather_region);
    	
    	ivHoursReplace= (ImageView) vTimeReplace.findViewById(R.id.iv_clock_h);
    	ivMinutesReplace=(ImageView) vTimeReplace.findViewById(R.id.iv_clock_s);
    	tvHoursReplace= (TextView) vTimeReplace.findViewById(R.id.tv_day);
    	tvMinutesReplace= (TextView) vTimeReplace.findViewById(R.id.tv_hour_minutes);
    	
//    	ivHoursReplace= (ImageView) vCenterReplace.findViewById(R.id.iv_clock_h);
//    	ivMinutesReplace=(ImageView) vCenterReplace.findViewById(R.id.iv_clock_s);
//    	tvHoursReplace= (TextView) vCenterReplace.findViewById(R.id.tv_day);
//    	tvMinutesReplace= (TextView) vCenterReplace.findViewById(R.id.tv_hour_minutes);
//    	
//    	vMoodReplace=vCenterReplace.findViewById(R.id.rl_mood);
//    	vMoodReplace.setOnClickListener(this);
//    	ivMoodReplace= (ImageView) vCenterReplace.findViewById(R.id.iv_mood);
    }
    
    private void checkChildsPosition(){
    	int height=vTimeReplace.getHeight();
    	if(View.GONE!=rlTitleBar.getVisibility()){
    		height=rlTitleBar.getHeight();
    	}
    	RelativeLayout relativeLayout=diaryListView.getContentLayout();
    	if(relativeLayout!=null){
    		for(int i=0;i<relativeLayout.getChildCount();i++){
    			View view=relativeLayout.getChildAt(i);
    			if(view.getTag()!=null){
    				MyDiary myDiary=(MyDiary) view.getTag();
    				int[] location=new int[2];
    				view.getLocationInWindow(location);
    				int y=location[1];
    				if(y>0&&y<height){
    					String time=myDiary.updatetimemilli;
    					new Thread(new updateTimeThread(time)).start();
    				}
    				WebImageView ivPic=(WebImageView) view.findViewById(R.id.iv_pic);//图片类型
    				if(View.VISIBLE==ivPic.getVisibility()){
    					if(y>pScreenHeight2||y<nScreenHeight2){//超出屏幕高度的2倍
    						//释放图片资源
//    						ivPic.recycle();
    					}else if(y<=pScreenHeight2&&y>=nScreenHeight2){//屏幕2倍高度范围内
    						//重载图片资源
//    						ivPic.reload();
    					}
    				}
    			}
    		}
    	}
    }
}
