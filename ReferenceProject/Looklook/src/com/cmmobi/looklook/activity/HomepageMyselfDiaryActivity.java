/**
 * 
 */
package com.cmmobi.looklook.activity;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.PopupWindow.OnDismissListener;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import cn.zipper.framwork.core.ZActivity;
import cn.zipper.framwork.core.ZApplication;
import cn.zipper.framwork.core.ZBroadcastReceiver;
import cn.zipper.framwork.core.ZDialog;
import cn.zipper.framwork.io.network.ZNetworkStateDetector;

import com.baidu.mapapi.map.LocationData;
import com.baidu.platform.comapi.basestruct.GeoPoint;
import com.cmmobi.looklook.R;
import com.cmmobi.looklook.common.adapter.MapNearbyListAdapter;
import com.cmmobi.looklook.common.constant.Constant;
import com.cmmobi.looklook.common.gson.GsonResponse2;
import com.cmmobi.looklook.common.gson.GsonResponse2.MyDiary;
import com.cmmobi.looklook.common.gson.GsonResponse2.diaryAttach;
import com.cmmobi.looklook.common.gson.GsonResponse2.diaryCommentListResponse;
import com.cmmobi.looklook.common.gson.GsonResponse2.diarycommentlistItem;
import com.cmmobi.looklook.common.gson.GsonResponse2.forwardDiaryListResponse;
import com.cmmobi.looklook.common.gson.GsonResponse2.homeResponse;
import com.cmmobi.looklook.common.gson.GsonResponse2.listCollectDiaryResponse;
import com.cmmobi.looklook.common.gson.GsonResponse2.listMyDiaryResponse;
import com.cmmobi.looklook.common.gson.GsonResponse2.moodResponse;
import com.cmmobi.looklook.common.gson.GsonResponse2.shareEnjoyDiaryResponse;
import com.cmmobi.looklook.common.gson.GsonResponse2.taglistItem;
import com.cmmobi.looklook.common.gson.Requester2;
import com.cmmobi.looklook.common.listview.pulltorefresh.AbsRefreshView.OnRefreshListener;
import com.cmmobi.looklook.common.listview.pulltorefresh.DiaryListView;
import com.cmmobi.looklook.common.listview.pulltorefresh.DiaryListView.StatusBarListener;
import com.cmmobi.looklook.common.listview.pulltorefresh.RecentListView;
import com.cmmobi.looklook.common.listview.pulltorefresh.RecentListView.OnInitLinstener;
import com.cmmobi.looklook.common.service.PrivateMessageService;
import com.cmmobi.looklook.common.utils.CmmobiClickAgentWrapper;
import com.cmmobi.looklook.common.utils.DateUtils;
import com.cmmobi.looklook.common.utils.UmengclickAgentWrapper;
import com.cmmobi.looklook.common.view.TagsView;
import com.cmmobi.looklook.common.view.Xdialog;
import com.cmmobi.looklook.common.web.TackView;
import com.cmmobi.looklook.common.web.WebImageView;
import com.cmmobi.looklook.info.location.MyLocationInfo;
import com.cmmobi.looklook.info.profile.AccountInfo;
import com.cmmobi.looklook.info.profile.ActiveAccount;
import com.cmmobi.looklook.info.profile.DiaryManager;
import com.cmmobi.looklook.info.profile.DiaryManager.FilterType;
import com.cmmobi.looklook.info.profile.DiaryManager.MydiaryDataChangedListener;
import com.cmmobi.looklook.info.profile.LoginSettingManager;
import com.cmmobi.looklook.info.profile.TimeHelper;
import com.cmmobi.looklook.info.weather.MyWeather;
import com.cmmobi.looklook.info.weather.MyWeatherInfo;
import com.cmmobi.looklook.map.MapItemHelper;
import com.cmmobi.looklook.map.MapItemHelper.Item;
import com.cmmobi.looklook.map.MyItemizedOverlay;
import com.cmmobi.looklook.map.MyMapView;
import com.cmmobi.looklook.map.Span;
import com.cmmobi.looklook.networktask.NetworkTaskManager;
import com.cmmobi.looklook.offlinetask.OfflineTaskManager;
import com.cmmobi.looklook.prompt.Prompt;
import com.nostra13.universalimageloader.api.AnimateFirstDisplayListener;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageLoadingListener;
import com.nostra13.universalimageloader.core.display.CircularBitmapDisplayer;
import com.nostra13.universalimageloader.core.display.SimpleBitmapDisplayer;

/**
 * @author wuxiang
 * 
 * @create 2013-4-18
 */
public class HomepageMyselfDiaryActivity extends ZActivity {
	private static final String TAG = "HomepageActivity";
	private static final boolean ISDEBUG = true;

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

	private DiaryListView diaryListView;
	private RecentListView recentListView;

	// UI components
	private MyMapView mv_diaryMap;
	private ListView  lv_info;
	private ImageView iv_close;
	private FrameLayout flDiaryMap;
	private RelativeLayout rltankuang;
	private boolean isfromRefresh = false;
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
	
	private ImageView ivMood;// 心情图片
	private LayoutInflater inflater;

	private ListView listFilter;// 日记种类选择
	private TextView tvFilter;
	private ArrayList<String> filterList = new ArrayList<String>();
	private myFilterAdapter filterAdapter;

	private ImageView ivPortrait;// 头像
	private DisplayImageOptions options;
	private DisplayImageOptions backgroundoptions;
	protected ImageLoader imageLoader;
	private ImageLoadingListener animateFirstListener;
	private TextView tvNickname;// 昵称
	private TextView tvSignature;// 签名

	private WebImageView ivWeatherIcon;// 天气图标
	private TextView tvWeatherTemp;// 天气温度
	private TextView tvWeatherRegion;// 天气区域
    private boolean isFirstOpen = true;
	
	private TextView tvTasks;//正在进行的任务
	private ImageView ivDiaryBack;

	private WebImageView ivZoneBackground;// 空间背景
	private ImageView ivSex;

	private ImageView ivMore;

	private View rlTitleBar;// 默认状态栏
	private View rlRemoveBar;// 删除时显示的状态栏layout
	private ImageView ivBack;// 删除状态栏返回按钮
	private TextView tvCheckedNum;// 选中条数
	private ImageView ivRemove;// 删除按钮

	private View vTitleChoice;//日记筛选layout
	private TextView tvChoiceName;//筛选对应文字
	private View vXiala;//筛选下拉箭头

	private AccountInfo accountInfo;
	private ArrayList<MyDiary> diaries;
	private String userID;
	private DiaryManager diaryManager;
	public FilterType diaryFilterType;// 日记页筛选类型

	private TagsView tagsView;
	private View anchorView;

	private UpdateWeatherReceiver updateWeatherReceiver;
	private UpdateNetworkTaskReceiver updateNetworkTaskReceiver;
	public static final String SAVE_BOX_MODE_CHANGE = "CHANGE.SAVEBOX.MODE.MSG";
	private final int HANDLER_FLAG_INIT_MAP_VIEW = 0xfff0001;
	private LoginSettingManager lsm;
	
	String diaryWidth;//日记页中日记的宽度
	String diaryHeight="";//日记页中日记的高度
	String backgroundWidth;//个人空间背景宽度
	String backgroundHeight="";//个人空间背景高度
	
	String recentWidth;//分享中日记的宽度
	String recentHeight="";//分享中日记的高度
	
	private int isSlipUP;//滑动方向，1-上滑 2-下滑
	private long timeOffset;//隐藏和显示的时间频率
	private View vTime;//时钟layout
	private View vTimeReplace;//上滑时替代显示center
	
	private ImageView ivHoursReplace;
	private ImageView ivMinutesReplace;
	private TextView tvHoursReplace;
	private TextView tvMinutesReplace;
	//2倍屏幕高度
	private static int pScreenHeight2;
	private static int nScreenHeight2;
	//判断动态页是下拉刷新还是上拉加载
	private boolean recentFreshMode;
	
	private View VTitleBarJiaobiao;
	private TextView tvTitleBarJiaobiao;
	
	private View VCommentJiaobiao;
	private TextView tvCommentJiaobiao;
	
	//当前是否是在长按删除模式
	private boolean isDeleteMode;
	
	private ArrayList<MyDiary> mapDiaryList=new ArrayList<GsonResponse2.MyDiary>();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initFilterText();
		userID = ActiveAccount.getInstance(ZApplication.getInstance()).getUID();
		Log.d(TAG, "userID="+userID);
		//for test
//		userID="5358e7db0646f04a820bcb20ebc2e7818a70";
		accountInfo = AccountInfo.getInstance(userID);
		diaryManager =DiaryManager.getInstance();
		diaryManager.addDataChangedListener(new mydiaryDataChangedListener());
		lsm = accountInfo.setmanager;
		setContentView(R.layout.activity_homepage_myself_diary);
		anchorView = findViewById(R.id.iv_anchor);
		inflater = LayoutInflater.from(this);
		vTime=findViewById(R.id.ll_time);
		vTimeReplace=findViewById(R.id.rl_time_replace);
		vTimeReplace.setVisibility(View.GONE);
		initReplace();
		diaryListView = (DiaryListView) findViewById(R.id.dlv_list);
		recentListView = (RecentListView) findViewById(R.id.dlv_recentlist);
		diaryFilterType = FilterType.ALL;
		// 请求新数据
		pScreenHeight2=diaryListView.getScreenHight();
		nScreenHeight2=0-pScreenHeight2;
		diaryWidth=diaryListView.getDiaryWidth()+"";
		backgroundWidth=diaryListView.getBackgroundWidth()+"";
		recentWidth=recentListView.getDiaryWidth()+"";
//		recentHeight=recentListView.getDiaryHeight()+"";
		//日记页加载逻辑：如果是第一次请求，先判断本地是否有缓存，如果没有缓存，
		//先请求最新一页数据，如果有缓存，根据缓存请新时间和最旧时间
		//分别请求最新数据和历史数据
		initLoadLocalDiary();
		diaryListView.setStatusBarListener(new StatusBarListener() {
			public void showStatusBar() {
				Log.d(TAG, "showStatusBar");
				rlRemoveBar.setVisibility(View.VISIBLE);
				isDeleteMode=true;
				dimissTitleAndBottomBar();
			}

			public void dimissStatusBar() {
				Log.d(TAG, "dimissStatusBar");
				rlRemoveBar.setVisibility(View.GONE);
				isDeleteMode=false;
				showTitleAndBottomBar();
			}

			public void checkedNum(int count) {
				Log.d(TAG, "checkedNum=" + count);
				String checked = String.format(
						getString(R.string.homepage_checked_remove_num), count
								+ "");
				tvCheckedNum.setText(checked);
			}
		});
		diaryListView.setOnRefreshListener(new OnRefreshListener() {
			@Override
			public void onRefresh() {
				refreshDiary();
				isfromRefresh = true;
				MyWeatherInfo.getInstance(ZApplication.getInstance()).updateWeather(true);
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

		recentListView.setOnInitLinstener(new OnInitLinstener() {

			@Override
			public void init() {// 第一次进入选项时，加载该选项本地数据同时请求对应的网络数据
//				getLocalRecentDiary();
//				recentFreshMode=false;
//				getServerRecentDiary();
				refreshRecent();
			}
		});
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

		//baidu map
		flDiaryMap = (FrameLayout) findViewById(R.id.fl_activity_homepage_myselfdiary_map);
		mv_diaryMap = (MyMapView) findViewById(R.id.bmv_activity_homepage_myselfdiary_map);
		lv_info = (ListView) findViewById(R.id.lv_activity_homepage_myselfdiary_map_list);
		iv_close = (ImageView) findViewById(R.id.iv_activity_homepage_myselfdiary_map_tankuang_close);
		rltankuang = (RelativeLayout) findViewById(R.id.fl_activity_homepage_myselfdiary_map_tankuang);
		
		VTitleBarJiaobiao=findViewById(R.id.rl_title_jiaobiao);
		tvTitleBarJiaobiao=(TextView) findViewById(R.id.tv_title_jiaobiao);
		
		VCommentJiaobiao=findViewById(R.id.rl_comment_jiaobiao);
		tvCommentJiaobiao=(TextView) findViewById(R.id.tv_comment_jiaobiao);
		
		recentListView.setJiaoBiaoClearLayout(VTitleBarJiaobiao,VCommentJiaobiao);
		iv_close.setOnClickListener(this);
		mv_diaryMap.setClickable(false);
		mv_diaryMap.setHandler(handler);
		mv_diaryMap.setDoubleClickZooming(false);
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
		vXiala=findViewById(R.id.iv_xiala);
		ivMore=(ImageView) findViewById(R.id.iv_more);
		ivMore.setOnClickListener(this);
		ivZoneBackground = (WebImageView) findViewById(R.id.iv_homepage_zone_bg);
		ivSex=(ImageView) findViewById(R.id.iv_sex);
		ivPortrait = (ImageView) findViewById(R.id.iv_homepage_portrait);
		animateFirstListener = new AnimateFirstDisplayListener();
		imageLoader = ImageLoader.getInstance();
		imageLoader.init(ImageLoaderConfiguration.createDefault(HomepageMyselfDiaryActivity.this));
		options = new DisplayImageOptions.Builder()
		.showStubImage(R.drawable.moren_touxiang)
		.showImageForEmptyUri(R.drawable.moren_touxiang)
		.showImageOnFail(R.drawable.moren_touxiang)
		.cacheInMemory(true)
		.cacheOnDisc(true)
		.displayer(new SimpleBitmapDisplayer())
		.displayer(new CircularBitmapDisplayer())
		.build();
		backgroundoptions = new DisplayImageOptions.Builder()
		.showStubImage(R.drawable.homepage_zone_moren)
		.showImageForEmptyUri(R.drawable.homepage_zone_moren)
		.showImageOnFail(R.drawable.homepage_zone_moren)
		.cacheInMemory(true)
		.cacheOnDisc(true)
		.displayer(new SimpleBitmapDisplayer())
		.build();
		
		findViewById(R.id.ll_userinfo).setOnClickListener(this);
		tvNickname = (TextView) findViewById(R.id.tv_nickname);
		tvSignature = (TextView) findViewById(R.id.tv_signature);

		ivWeatherIcon = (WebImageView) findViewById(R.id.iv_weather_icon);
		tvWeatherTemp = (TextView) findViewById(R.id.tv_weather_temp);
		tvWeatherRegion = (TextView) findViewById(R.id.tv_weather_region);

		tvTasks=(TextView) findViewById(R.id.tv_tasks);
		ivDiaryBack=(ImageView) findViewById(R.id.iv_diary_back);
		ivDiaryBack.setOnClickListener(this);
		tvTasks.setOnClickListener(this);
		rlTitleBar = findViewById(R.id.rl_title);
		rlRemoveBar = findViewById(R.id.rl_removie_title);
		ivBack = (ImageView) findViewById(R.id.iv_back);
		tvCheckedNum = (TextView) findViewById(R.id.tv_checked_remove_num);
		ivRemove = (ImageView) findViewById(R.id.iv_delete);

		tagsView = new TagsView(this);
		tagsView.setTag(getString(R.string.homepage_diary_tags));
		ivRemove.setOnClickListener(this);
		ivBack.setOnClickListener(this);
		ivZoneBackground.setOnClickListener(this);
		ivPortrait.setOnClickListener(this);
		ivWeatherIcon.setOnClickListener(this);

		ivHour = (ImageView) findViewById(R.id.iv_clock_h);
		ivSecond = (ImageView) findViewById(R.id.iv_clock_s);
		tvHourAndMinutes = (TextView) findViewById(R.id.tv_hour_minutes);
		tvDay = (TextView) findViewById(R.id.tv_day);
		ivMood = (ImageView) findViewById(R.id.iv_mood);
		bmHour = BitmapFactory.decodeResource(getResources(),
				R.drawable.shijian_clock_h);
		bmSecond = BitmapFactory.decodeResource(getResources(),
				R.drawable.shijian_clock_s);
		hWidth = bmHour.getWidth();
		hHeight = bmHour.getHeight();
		sWidth = bmSecond.getWidth();
		sHeight = bmSecond.getHeight();
		handler.postDelayed(timeTask, 0);

		// 注册天气receiver
		updateWeatherReceiver = new UpdateWeatherReceiver();
		getZReceiverManager().registerLocalZReceiver(updateWeatherReceiver);
		updateNetworkTaskReceiver = new UpdateNetworkTaskReceiver();
		getZReceiverManager().registerLocalZReceiver(updateNetworkTaskReceiver);

		initUserInfo();

		LocalBroadcastManager.getInstance(this).registerReceiver(
				mSaveBoxReceiver, new IntentFilter(SAVE_BOX_MODE_CHANGE));
		IntentFilter intentFilter=new IntentFilter();
		intentFilter.addAction(SettingActivity.BROADCAST_PORTRAIT_CHANGED);
		intentFilter.addAction(SettingActivity.BROADCAST_PERSONALINFO_CHANGED);
		intentFilter.addAction(PrivateMessageService.FLAG_MESSAGE_DATA_UPDATE);
		intentFilter.addAction(HomeActivity.BROADCAST_HEADIMG_UPLOAD);
		LocalBroadcastManager.getInstance(this).registerReceiver(userInfoChangedRecever, intentFilter);
		if(0==DiaryManager.commentCount){
			VTitleBarJiaobiao.setVisibility(View.INVISIBLE);
			VCommentJiaobiao.setVisibility(View.INVISIBLE);
		}else{
			VTitleBarJiaobiao.setVisibility(View.VISIBLE);
			VCommentJiaobiao.setVisibility(View.VISIBLE);
			setJiaobiao(tvTitleBarJiaobiao);
			setJiaobiao(tvCommentJiaobiao);
		}
	}
	
	@Override
	public void onResume() {
		super.onResume();
		isStop=false;
		UmengclickAgentWrapper.onResume(this);
		CmmobiClickAgentWrapper.onResume(this);
        mv_diaryMap.onResume();
        reloadDiary();
        handler.sendEmptyMessageDelayed(MyMapView.HANDLER_FLAG_MAPVIEW_UPDATE, 500);
        refreshRecent();
	}

	@Override
	public void onPause() {
		super.onPause();
		UmengclickAgentWrapper.onPause(this);
		CmmobiClickAgentWrapper.onPause(this);
		mv_diaryMap.onPause();
	}
	
	private boolean isStop;
	private ArrayList<MyDiary> reloadDiaryList;
	@Override
	public void onStop() {
		super.onStop();
		isStop=true;
		CmmobiClickAgentWrapper.onStop(this);
		stopTackViewAudioPlayer();
	}
	
	//使正在播放中的短录音停止
	public static void stopTackViewAudioPlayer(){
		TackView tackView=TackView.getTackView();
		if(tackView!=null)
			tackView.stop();
	}

	private static final int HANDLER_UPDATE_TIME = 0x0011;
	private static final int HANDLER_UPDATE_TIME_REPLACE = 0x0012;
	// 删除第三方分享
	private static final int HANDLER_PROCESS_REMOVE_SHARED_IDS = 0x0013;
	
	private static final int HANDLER_MYHOMEPAGEDATACHANGED = 0x0014;
	private static final int HANDLER_MYPUBLISHDATACHANGED = 0x0015;
	private static final int HANDLER_MYSAVEBOXDATACHANGED = 0x0016;
	private static final int HANDLER_MYUNPUBLISHDATACHANGED = 0x0017;
	private static final int HANDLER_MYTAGDATACHANGED = 0x0018;
	private static final int HANDLER_MYSHAREDATACHANGED = 0x0019;
	private static final int HANDLER_MYENSHRINEDATACHANGED = 0x0020;
	private static final int HANDLER_MYPRAISEDATACHANGED = 0x0021;
	
	@Override
	public boolean handleMessage(Message msg) {
		if (null == msg.obj && msg.what != HANDLER_FLAG_INIT_MAP_VIEW
				&& msg.what != MyMapView.HANDLER_FLAG_MAPVIEW_CLICK
				&& msg.what != MyMapView.HANDLER_FLAG_MAPVIEW_UPDATE
				&& msg.what != MyMapView.HANDLER_FLAG_MAPVIEW_DOWN
				&& msg.what != MyMapView.HANDLER_FLAG_MAPVIEW_MOVE
				&& msg.what != MyMapView.HANDLER_FLAG_MAPVIEW_UP
				&& msg.what != HANDLER_MYHOMEPAGEDATACHANGED
				&& msg.what != HANDLER_MYPUBLISHDATACHANGED
				&& msg.what != HANDLER_MYSAVEBOXDATACHANGED
				&& msg.what != HANDLER_MYUNPUBLISHDATACHANGED
				&& msg.what != HANDLER_MYTAGDATACHANGED
				&& msg.what != HANDLER_MYSHAREDATACHANGED
				&& msg.what != HANDLER_MYENSHRINEDATACHANGED
				&& msg.what != HANDLER_MYPRAISEDATACHANGED
				&& msg.what != MyItemizedOverlay.HANDLER_FLAG_MAP_ITEM_TAP) {
			Log.e(TAG, msg.what + " response is null");
			diaryListView.loadDateError();
			recentListView.loadDateError();
			ZDialog.dismiss();
			Prompt.dimissProgressDialog();
			Prompt.Alert(getString(R.string.prompt_network_error));
			return false;
		}
		switch (msg.what) {
		case HANDLER_MYSHAREDATACHANGED:{
			if(diaryFilterType==FilterType.SHARED){
				ArrayList<MyDiary> tempDiary =diaryManager.getMySharedDiaryByList((ArrayList<MyDiary>)diaryListView.getDiaryList().clone(),saveboxIsOpen());
				if(isStop){
					reloadDiaryList=tempDiary;
				}else{
					diaryListView.reset();
					diaryListView.loadMore(tempDiary);
					if(tempDiary.size()<10){
						diaryListView.noMoreData(true);
					}else{
						diaryListView.noMoreData(false);
					}
				}
			}
			break;}
		case HANDLER_MYTAGDATACHANGED:
			if(diaryFilterType==FilterType.TAG){
				ArrayList<MyDiary> tempDiary =diaryManager.getmyTagDiaryByList((ArrayList<MyDiary>)diaryListView.getDiaryList().clone(),saveboxIsOpen());
				if(isStop){
					reloadDiaryList=tempDiary;
				}else{
					diaryListView.reset();
					diaryListView.loadMore(tempDiary);
					if(tempDiary.size()<10){
						diaryListView.noMoreData(true);
					}else{
						diaryListView.noMoreData(false);
					}
				}
			}
			break;
		case HANDLER_MYUNPUBLISHDATACHANGED:
			if(diaryFilterType==FilterType.UNSYNCHRONIZED){
				ArrayList<MyDiary> tempDiary =diaryManager.getUnsynchronizeDiaryByList((ArrayList<MyDiary>)diaryListView.getDiaryList().clone(),saveboxIsOpen());
				if(isStop){
					reloadDiaryList=tempDiary;
				}else{
					diaryListView.reset();
					diaryListView.loadMore(tempDiary);
					if(tempDiary.size()<10){
						diaryListView.noMoreData(true);
					}else{
						diaryListView.noMoreData(false);
					}
				}
			}
			break;
		case HANDLER_MYSAVEBOXDATACHANGED:
			if(diaryFilterType==FilterType.SAVEBOX){
				ArrayList<MyDiary> tempDiary =diaryManager.getmySaveBoxDiaryByList((ArrayList<MyDiary>)diaryListView.getDiaryList().clone());
				if(isStop){
					reloadDiaryList=tempDiary;
				}else{
					diaryListView.reset();
					diaryListView.loadMore(tempDiary);
					if(tempDiary.size()<10){
						diaryListView.noMoreData(true);
					}else{
						diaryListView.noMoreData(false);
					}
				}
			}
			break;
		case HANDLER_MYHOMEPAGEDATACHANGED:
			if(diaryFilterType==FilterType.ALL){
				ArrayList<MyDiary> tempDiary =diaryManager.getMyhomepageDiaryByList((ArrayList<MyDiary>)diaryListView.getDiaryList().clone(),saveboxIsOpen());
				if(isStop){
					reloadDiaryList=tempDiary;
				}else{
					diaryListView.reset();
					diaryListView.loadMore(tempDiary);
					if(tempDiary.size()<10){
						diaryListView.noMoreData(true);
					}else{
						diaryListView.noMoreData(false);
					}
				}
				setMapDiaryList();
			}
			break;
		case Requester2.RESPONSE_TYPE_UPLOAD_PICTURE:{//上传头像
			ZDialog.dismiss();
			GsonResponse2.uploadPictrue res = (GsonResponse2.uploadPictrue) msg.obj;
			if ("0".equals(res.status)) {
				Log.d(TAG, "res.imageurl="+res.imageurl);
				setPortraitUrl(res.imageurl);
				setUserInfo();
				CmmobiClickAgentWrapper.onEvent(this, "my_ avatar", "1");
			} else {
				Log.e(TAG, "RESPONSE_TYPE_UPLOAD_PICTURE status is "
						+ res.status);
			}
			break;}
		/*case Requester2.RESPONSE_TYPE_DELETE_DIARY: {// 删除日记
			deleteDiaryResponse res = (deleteDiaryResponse) msg.obj;
			if ("0".equals(res.status)) {
				Prompt.Alert("删除成功");
				ArrayList<String> local=(ArrayList<String>) diaryListView.getLocalRemoveItem().clone();
				ArrayList<String> server=(ArrayList<String>) diaryListView.getServerRemoveItem().clone();
				// 3.删除本地缓存日记
				Log.d(TAG, "local="+local);
				Log.d(TAG, "server="+server);
				diaryManager.removeDiaryByIDs(local);
				diaryManager.removeDiaryByIDs(server);
				diaryListView.cancelRemove();
				rlRemoveBar.setVisibility(View.GONE);
				rlTitleBar.setVisibility(View.VISIBLE);
			} else {
				Prompt.Alert("删除失败");
				Log.e(TAG, "RESPONSE_TYPE_DELETE_DIARY status is "
						+ res.status);
			}
			ZDialog.dismiss();
			break;
		}*/
		case Requester2.RESPONSE_TYPE_DIARY_COMMENTLIST: {
			Prompt.dimissProgressDialog();
			diaryCommentListResponse res = (diaryCommentListResponse) msg.obj;
			if("0".equals(res.status)){
				if("0".equals(res.hasnextpage)){//没有下一页
					recentListView.noMoreData(true);
				}else{//有下一页
					recentListView.noMoreData(false);
				}
				lastTimeComment=res.last_comment_time;
			}
			if ("0".equals(res.status)) {
				if (!recentFreshMode) {// 为刷新数据，添加至首部
//					diaryManager.addCommentDiaryResponse(res);
					recentListView.reset();
					recentListView.loadMore(null);
				}
				if(res.comments!=null&&res.comments.length>0){
					ArrayList<diarycommentlistItem> items=new ArrayList<diarycommentlistItem>();
					items.addAll(Arrays.asList(res.comments));
					recentListView.loadMore(items);
				}
			} else {
				recentListView.noMoreData(true);
				recentListView.loadDateError();
				Log.e(TAG, "RESPONSE_TYPE_DIARY_COMMENTLIST status is "
						+ res.status);
			}
			break;
		}
		case Requester2.RESPONSE_TYPE_FORWARD_DIARY_LIST: {
			Prompt.dimissProgressDialog();
			forwardDiaryListResponse res = (forwardDiaryListResponse) msg.obj;
			if("0".equals(res.status)){
				if("0".equals(res.hasnextpage)){//没有下一页
					recentListView.noMoreData(true);
				}else{//有下一页
					recentListView.noMoreData(false);
				}
				lastTimePraise=res.last_diary_time;
			}
			if ("0".equals(res.status)) {
				if (!recentFreshMode) {
//					diaryManager.addPraiseDiaryResponse(userID, res);
					recentListView.reset();
					recentListView.loadMore(null);
				}
				if(res.diaries!=null&&res.diaries.length>0){
					ArrayList<MyDiary> items=new ArrayList<MyDiary>();
					items.addAll(Arrays.asList(res.diaries));
					recentListView.loadMore(items);
				}
			} else {
				recentListView.noMoreData(true);
				recentListView.loadDateError();
				Log.e(TAG, "RESPONSE_TYPE_FORWARD_DIARY_LIST status is "
						+ res.status);
			}
			break;
		}
		case Requester2.RESPONSE_TYPE_LIST_COLLECT_DIARY: {
			Prompt.dimissProgressDialog();
			listCollectDiaryResponse res = (listCollectDiaryResponse) msg.obj;
			if("0".equals(res.status)){
				if("0".equals(res.hasnextpage)){//没有下一页
					recentListView.noMoreData(true);
				}else{//有下一页
					recentListView.noMoreData(false);
				}
				lastTimeCollect=res.last_diary_time;
			}
			if ("0".equals(res.status)) {
				if (!recentFreshMode) {
//					diaryManager.addCollectDiaryResponse(res);
					//刷新掉旧数据
					recentListView.reset();
					recentListView.loadMore(null);
				}
				if(res.diaries!=null&&res.diaries.length>0){
					ArrayList<MyDiary> items=new ArrayList<MyDiary>();
					items.addAll(Arrays.asList(res.diaries));
					recentListView.loadMore(items);
				}
			} else {
				recentListView.noMoreData(true);
				recentListView.loadDateError();
				Log.e(TAG, "RESPONSE_TYPE_LIST_COLLECT_DIARY status is "
						+ res.status);
			}
			break;
		}
		case Requester2.RESPONSE_TYPE_LIST_SHARE_DIARY: {
			Prompt.dimissProgressDialog();
			shareEnjoyDiaryResponse res = (shareEnjoyDiaryResponse) msg.obj;
			if("0".equals(res.status)){
				if("0".equals(res.hasnextpage)){//没有下一页
					recentListView.noMoreData(true);
				}else{//有下一页
					recentListView.noMoreData(false);
				}
				lastTimeShared=res.last_diary_time;
			}
			if ("0".equals(res.status)) {
				if (!recentFreshMode){
//					diaryManager.addShareDiaryResponse(userID, res);
					recentListView.reset();
					recentListView.loadMore(null);
				}
				if(res.diaries!=null&&res.diaries.length>0){
					ArrayList<MyDiary> items=new ArrayList<MyDiary>();
//					diaryManager.setDiarySync(res.diaries, 4);
					items.addAll(Arrays.asList(res.diaries));
					recentListView.loadMore(items);
				}
			} else {
				recentListView.noMoreData(true);
				recentListView.loadDateError();
				Log.e(TAG, "RESPONSE_TYPE_LIST_SHARE_DIARY status is "
						+ res.status);
			}
			break;
		}
		case Requester2.RESPONSE_TYPE_LIST_MY_DIARY:{
			GsonResponse2.listMyDiaryResponse res = (listMyDiaryResponse) msg.obj;
			if ("0".equals(res.status)) {
				diaryManager.setMyDiaryFirstTime(res.first_diary_time);
				diaryManager.setMyDiaryLastTime(res.last_diary_time);
				if("1".equals(res.is_refresh)){//强制刷新
					diaryManager.clearMydiaries();
				}
				if(res.diaries!=null&&res.diaries.length>0){
					diaryManager.saveDiaries(res, false);
				}
				if("0".equals(res.hasnextpage)){//没有下一页
					if("new".equals(diaryListView.getTag())){
						//最新数据请求完成，开始请求历史数据
						diaryListView.setTag("old");
						String lastTime=diaryManager.getMyDiaryLastTime();
//						Requester2.homePage(handler, userID,
//								lastTime, "2",diaryWidth,diaryHeight,backgroundWidth,backgroundHeight);
						Requester2.requestMyDiary(handler, userID, lastTime, "2", "2", "0",diaryWidth);
					}else{
						//获取本地最新数据
						diaries = (ArrayList<MyDiary>) diaryManager.getDiary(
								diaryFilterType,userID, 0,saveboxIsOpen());
						if(diaries!=null&&diaries.size()>0){
							//加载最新数据
							diaryListView.reset();
							diaryListView.loadMore(diaries);
							if(diaries.size()<10){
								diaryListView.noMoreData(true);
							}else{
								diaryListView.noMoreData(false);
							}
						}else{
							//本地最新数据可能全部为副本
							Log.d(TAG, "没有获取到本地数据");
							diaryListView.noMoreData(true);
							diaryListView.loadDateError();
						}
						setMapDiaryList();
					}
				}else{
					if("new".equals(diaryListView.getTag())){
						//继续请求最新数据
						String firstTime=diaryManager.getMyDiaryFirstTime();
						Requester2.requestMyDiary(handler, userID, firstTime, "1", "2", "0",diaryWidth);
					}else if("old".equals(diaryListView.getTag())){
						//继续请求历史数据
						String lastTime=diaryManager.getMyDiaryLastTime();
						Log.d(TAG, "lastTime="+lastTime);
						Requester2.requestMyDiary(handler, userID, lastTime, "2", "2", "0",diaryWidth);
					}else{
						Log.e(TAG, "diaryListView.getTag() is null");
					}
				}
			} else {
				diaryListView.loadDateError();
				Log.e(TAG, "RESPONSE_TYPE_LIST_MY_DIARY status is "
						+ res.status);
			}
			break;}
		case Requester2.RESPONSE_TYPE_HOME: {
			GsonResponse2.homeResponse res = (homeResponse) msg.obj;
			if ("0".equals(res.status)) {
				diaryManager.setMyDiaryFirstTime(res.first_diary_time);
				diaryManager.setMyDiaryLastTime(res.last_diary_time);
				if("new".equals(diaryListView.getTag())){
					diaryManager.updateUserInfo(userID, res);
					setUserInfo();
					setZoneBackground();
					setMood();
				}
				if("1".equals(res.is_refresh)){//强制刷新
					diaryManager.clearMydiaries();
				}
				if(res.diaries!=null&&res.diaries.length>0){
					diaryManager.saveDiaries(res, false);
					//获取本地最新数据
					diaries = (ArrayList<MyDiary>) diaryManager.getDiary(
							diaryFilterType,userID, 0,saveboxIsOpen());
					if(diaries!=null&&diaries.size()>0){
						//加载最新数据
						diaryListView.reset();
						diaryListView.loadMore(diaries);
						if(diaries.size()<10){
							diaryListView.noMoreData(true);
						}else{
							diaryListView.noMoreData(false);
						}
					}else{
						//本地最新数据可能全部为副本
						Log.d(TAG, "没有获取到本地数据");
						diaryListView.noMoreData(true);
						diaryListView.loadDateError();
					}
					setMapDiaryList();
				}
				if("0".equals(res.hasnextpage)){//没有下一页
					if("new".equals(diaryListView.getTag())){
						//最新数据请求完成，开始请求历史数据
						diaryListView.setTag("old");
						String lastTime=diaryManager.getMyDiaryLastTime();
//						Requester2.homePage(handler, userID,
//								lastTime, "2",diaryWidth,diaryHeight,backgroundWidth,backgroundHeight);
						Requester2.requestMyDiary(handler, userID, lastTime, "2", "2", "0",diaryWidth);
					}else{
						/*//获取本地最新数据
						diaries = (ArrayList<MyDiary>) diaryManager.getDiary(
								diaryFilterType,userID, 0,saveboxIsOpen());
						if(diaries!=null&&diaries.size()>0){
							//加载最新数据
							diaryListView.reset();
							diaryListView.loadMore(diaries);
							if(diaries.size()<10){
								diaryListView.noMoreData(true);
							}else{
								diaryListView.noMoreData(false);
							}
						}else{
							//本地最新数据可能全部为副本
							Log.d(TAG, "没有获取到本地数据");
							diaryListView.noMoreData(true);
							diaryListView.loadDateError();
						}
						setMapDiaryList();*/
					}
				}else{
					if("new".equals(diaryListView.getTag())){
						//继续请求最新数据
						String firstTime=diaryManager.getMyDiaryFirstTime();
//						Requester2.homePage(handler, userID,
//								firstTime, "1",diaryWidth,diaryHeight,backgroundWidth,backgroundHeight);
						Requester2.requestMyDiary(handler, userID, firstTime, "1", "2", "0",diaryWidth);
					}else if("old".equals(diaryListView.getTag())){
						//继续请求历史数据
						String lastTime=diaryManager.getMyDiaryLastTime();
//						Requester2.homePage(handler, userID,
//								lastTime, "2",diaryWidth,diaryHeight,backgroundWidth,backgroundHeight);
						Requester2.requestMyDiary(handler, userID, lastTime, "2", "2", "0",diaryWidth);
					}else{
						Log.e(TAG, "diaryListView.getTag() is null");
					}
				}
			} else {
				diaryListView.loadDateError();
				Log.e(TAG, "RESPONSE_TYPE_LIST_MY_DIARY status is "
						+ res.status);
			}
			break;
		}
		case Requester2.RESPONSE_TYPE_MOOD: {
			GsonResponse2.moodResponse res = (moodResponse) msg.obj;
			if ("0".equals(res.status)) {
				setMood(moodPosition);
				setMood();
			} else {
				Log.e(TAG, "RESPONSE_TYPE_MOOD status is " + res.status);
			}
			break;
		}
		case HANDLER_UPDATE_TIME:// 更新时间
			if (msg.obj instanceof Object[]) {
				Object[] obj = (Object[]) msg.obj;
				String timeHHmm = (String) obj[0];
				String timeDay = (String) obj[1];
				Bitmap bHour = (Bitmap) obj[2];
				Bitmap bSecond = (Bitmap) obj[3];
				tvHourAndMinutes.setText(timeHHmm);
				tvDay.setText(timeDay);
				ivHour.setImageBitmap(bHour);
				ivSecond.setImageBitmap(bSecond);
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
		case HANDLER_FLAG_INIT_MAP_VIEW:
			Log.e("==WR==", "======= HANDLER_FLAG_INIT_MAP_VIEW IN ! =======");
			rltankuang.setVisibility(View.INVISIBLE);
			if(/*myLoc==null || */mapDiaryList == null){
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
			if(/*testdata != null && testdata.size() > 0*/mapDiaryList != null && mapDiaryList.size() > 0){
				objs = MapItemHelper.showNearByItems(this, handler, getResources(), mv_diaryMap, null, /*testdata*/mapDiaryList, true);
				Log.e(TAG, "mapDiaryList size:" + mapDiaryList.size());
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
				Requester2.homePage(handler, userID,
						diaryManager.getReponseFirsttime(diaryFilterType), "1",diaryWidth,diaryHeight,backgroundWidth,backgroundHeight);
			}
			break;
		case MyMapView.HANDLER_FLAG_MAPVIEW_CLICK:
			rltankuang.setVisibility(View.INVISIBLE);
			//lv_info.setVisibility(View.INVISIBLE);
			break;
			
		case MyMapView.HANDLER_FLAG_MAPVIEW_UPDATE:
			try{
				Log.e(TAG, "handler update: level:" + mv_diaryMap.getZoomLevel());
				//模拟数据
/*				if (false) {
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
				}*/
				if(/*testdata2 != null && testdata2.size() > 0*/mapDiaryList != null && mapDiaryList.size() > 0) {
					Log.e(TAG, "mapDiaryList size = " + mapDiaryList.size());
					objs = MapItemHelper.showNearByItems(this, handler, getResources(), mv_diaryMap, null, /*testdata2*/mapDiaryList, false);
				}
			}catch(NullPointerException e){
				e.printStackTrace();
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
	
	//当其他页面有做日记变更操作时，如果我的相册页不在前台显示，记录该操作，等显示到前台是刷新界面
	private void reloadDiary(){
		if(reloadDiaryList!=null){
			reloadDiaryList=diaryManager.syncDiaryList((ArrayList<MyDiary>)reloadDiaryList.clone());
			diaryListView.reset();
			diaryListView.loadMore((ArrayList<GsonResponse2.MyDiary>)reloadDiaryList.clone());
			if(reloadDiaryList.size()<10){
				diaryListView.noMoreData(true);
			}else{
				diaryListView.noMoreData(false);
			}
		}
		reloadDiaryList=null;
	}

	private PopupWindow mPopupWindow;

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.ll_my_all:
			if(portraitMenu!=null)portraitMenu.dismiss();
			if(diaryFilterType==FilterType.ALL)return;
			diaryFilterType=FilterType.ALL;
			tvChoiceName.setText(getDiaryChoiceName());
			filterClick();
			CmmobiClickAgentWrapper.onEvent(this, "ma_back_p", "1");
			break;
		case R.id.ll_unsync:
			if(portraitMenu!=null)portraitMenu.dismiss();
			if(diaryFilterType==FilterType.UNSYNCHRONIZED)return;
			diaryFilterType=FilterType.UNSYNCHRONIZED;
			tvChoiceName.setText(getDiaryChoiceName());
			filterClick();
			CmmobiClickAgentWrapper.onEvent(this, "ma_back_p", "2");
			break;
		case R.id.ll_shared:
			if(portraitMenu!=null)portraitMenu.dismiss();
			if(diaryFilterType==FilterType.SHARED)return;
			diaryFilterType=FilterType.SHARED;
			tvChoiceName.setText(getDiaryChoiceName());
			filterClick();
			CmmobiClickAgentWrapper.onEvent(this, "ma_back_p", "3");
			break;
		case R.id.ll_savebox:
			if(portraitMenu!=null)portraitMenu.dismiss();
			if(diaryFilterType==FilterType.SAVEBOX)return;
			diaryFilterType=FilterType.SAVEBOX;
			tvChoiceName.setText(getDiaryChoiceName());
			filterClick();
			break;
		case R.id.ll_tags:
			if(portraitMenu!=null)portraitMenu.dismiss();
			diaryFilterType=FilterType.TAG;
			tvChoiceName.setText(tagsView.getTag().toString());
			filterClick();
			CmmobiClickAgentWrapper.onEvent(this, "ma_back_p", "4");
			break;
		case R.id.iv_activity_homepage_myselfdiary_map_tankuang_close:
			rltankuang.setVisibility(View.INVISIBLE);
			break;
		case R.id.ll_title_choice:
			showDiaryTitleChoce();
			break;
		case R.id.ll_map_mode:
			if(portraitMenu!=null)portraitMenu.dismiss();
			diaryListView.setVisibility(View.GONE);
			vTitleChoice.setClickable(false);
			vXiala.setVisibility(View.INVISIBLE);
			recentListView.setVisibility(View.GONE);
			flDiaryMap.setVisibility(View.VISIBLE);
			mv_diaryMap.setVisibility(View.VISIBLE);
			CmmobiClickAgentWrapper.onEvent(this, "ma_map_m");
			CmmobiClickAgentWrapper.onEventBegin(this, "ma_map_m");
			tvChoiceName.setText(R.string.homepage_my_map_mode);
			vTimeReplace.setVisibility(View.GONE);
			ivMore.setVisibility(View.GONE);
			VTitleBarJiaobiao.setVisibility(View.GONE);
			ivDiaryBack.setVisibility(View.VISIBLE);
			tvTasks.setVisibility(View.GONE);
			if (handler != null) {
				handler.sendEmptyMessage(HANDLER_FLAG_INIT_MAP_VIEW);
			}
			sendBroadcast(new Intent(HomeActivity.FLAG_BOTTOM_BAR_HIDDEN));
			CmmobiClickAgentWrapper.onEventBegin(this, "ma_map_m");
			break;
		case R.id.ll_my_savebox://保险箱选项
			if(portraitMenu!=null)portraitMenu.dismiss();
			//创建、开启、退出保险箱
			if (lsm.getGesturepassword() == null) {
				// 未创建
				//创建保险箱
				lsm.setIsFromSetting(false);
				Intent intent = new Intent(this, SettingToCreateGestureActivity.class);
				startActivity(intent);
			} else if (lsm.getSafeIsOn()) {
				// 已创建且打开
				//关闭保险箱
				lsm.setSafeIsOn(false);
			} else {
				// 已创建但关闭
				//打开保险箱
				lsm.setIsFromSetting(false);
				Intent in = new Intent(this, SettingGesturePwdActivity.class);
				in.putExtra("count", 0);
				startActivity(in);
			}
			break;
		case R.id.ll_my_interact:
			if(portraitMenu!=null)portraitMenu.dismiss();
			diaryListView.setVisibility(View.GONE);
			recentListView.setVisibility(View.VISIBLE);
			vTitleChoice.setClickable(false);
			tvChoiceName.setText(R.string.homepage_my_interact);
			vXiala.setVisibility(View.INVISIBLE);
			flDiaryMap.setVisibility(View.GONE);
			vTimeReplace.setVisibility(View.GONE);
			ivMore.setVisibility(View.GONE);
			VTitleBarJiaobiao.setVisibility(View.GONE);
			ivDiaryBack.setVisibility(View.VISIBLE);
			tvTasks.setVisibility(View.GONE);
			recentListView.clearType();
			recentListView.setDefaultChecked();
			CmmobiClickAgentWrapper.onEventBegin(this, "ma_intetr", "0");
			sendBroadcast(new Intent(HomeActivity.FLAG_BOTTOM_BAR_HIDDEN));
			break;
		case R.id.iv_delete:
			
			new Xdialog.Builder(this)
			.setTitle("删除日记")
			.setMessage("确定要删除选中日记吗？")
			.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					ArrayList<String> localList = diaryListView.getLocalRemoveItem();
					ArrayList<String> serverList = diaryListView.getServerRemoveItem();
					int count=localList.size()+serverList.size();
					CmmobiClickAgentWrapper.onEvent(HomepageMyselfDiaryActivity.this, "ma_ba_de", count+"");
					Log.d(TAG,"localList = " + localList);
					Log.d(TAG,"serverList = " + serverList);
					if(serverList.size()>0){
//						ZDialog.show(R.layout.progressdialog, false, true, HomepageMyselfDiaryActivity.this);
						String serverDiaryIDs="";
						for(int i=0;i<serverList.size();i++){
							if(0==serverDiaryIDs.length()){
								serverDiaryIDs=serverList.get(i);
							}else{
								serverDiaryIDs=serverDiaryIDs+","+serverList.get(i);
							}
						}
						Log.d(TAG,"iv_delete in serverDiaryIDs = " + serverDiaryIDs);
//						Requester2.deleteDiary(handler, serverDiaryIDs);
						OfflineTaskManager.getInstance().addDiaryRemoveTask(serverDiaryIDs);
						diaryManager.removeDiaryByUUIDs(localList);
						diaryManager.removeDiaryByIDs(serverList);
						diaryListView.cancelRemove();
						rlRemoveBar.setVisibility(View.GONE);
						rlTitleBar.setVisibility(View.VISIBLE);
					}else{
						diaryManager.removeDiaryByUUIDs(localList);
						Prompt.Alert("删除成功");
						diaryListView.cancelRemove();
						rlRemoveBar.setVisibility(View.GONE);
						rlTitleBar.setVisibility(View.VISIBLE);
					}	
				}
			})
			.setNegativeButton(android.R.string.cancel, null)
			.create().show();
			break;
		case R.id.iv_back:
			diaryListView.cancelRemove();
			break;
		case R.id.ll_setting:
			if(portraitMenu!=null)portraitMenu.dismiss();
			startActivity(new Intent(this, SettingActivity.class));
			CmmobiClickAgentWrapper.onEvent(this, "ma_set_up");
			break;
		case R.id.iv_more:
			stopTackViewAudioPlayer();
			CmmobiClickAgentWrapper.onEvent(this, "ma_more_b");
			if(HomeActivity.isCameraStart)return;
			if(System.currentTimeMillis()-HomeActivity.lastClickTime<1000)return;
			HomeActivity.lastClickTime=System.currentTimeMillis();
			showMoreChoice();
			break;
		case R.id.iv_weather_icon:
			MyWeatherInfo.getInstance(this).updateWeather(true);
			break;
		case R.id.iv_homepage_zone_bg:
			if(HomeActivity.isCameraStart)return;
			if(System.currentTimeMillis()-HomeActivity.lastClickTime<1000)return;
			HomeActivity.lastClickTime=System.currentTimeMillis();
			showChangeZoneBackgroundChoice();
			break;
		case R.id.btn_change_background:
			if(portraitMenu!=null)portraitMenu.dismiss();
			startActivityForResult(new Intent(this, SpaceCoverActivity.class),BACKGROUND_REQUEST);
			break;
		case R.id.ll_userinfo:
		case R.id.iv_homepage_portrait:
			if(HomeActivity.isCameraStart)return;
			if(System.currentTimeMillis()-HomeActivity.lastClickTime<1000)return;
			HomeActivity.lastClickTime=System.currentTimeMillis();
			Log.d(TAG, "iv_homepage_portrait");
			showPortraitChoice();
			break;
		case R.id.iv_diary_back:
			if(View.GONE==recentListView.getVisibility()){//之前显示的是地图
				CmmobiClickAgentWrapper.onEventEnd(this, "ma_map_m");
			}else{//之前显示的是互动
				CmmobiClickAgentWrapper.onEventEnd(this, "ma_intetr", "0");
				//记录退出互动时之后显示的界面停留的时间
				FilterType type=recentListView.getRecentType();
				switch (type) {
				case INTERACT_SHARE:
					if(recentListView.startTime!=0){
						long duration=TimeHelper.getInstance().now()-recentListView.startTime;
						Log.d(TAG, "INTERACT_SHARE->duration="+duration);
						CmmobiClickAgentWrapper.onEventDuration(this,"ma_intetr", "1", duration);
					}
					break;
				case INTERACT_COLLECT:
					if(recentListView.startTime!=0){
						long duration=TimeHelper.getInstance().now()-recentListView.startTime;
						Log.d(TAG, "INTERACT_COLLECT->duration="+duration);
						CmmobiClickAgentWrapper.onEventDuration(this,"ma_intetr", "2", duration);
					}
					break;
				case INTERACT_PRAISE:
					if(recentListView.startTime!=0){
						long duration=TimeHelper.getInstance().now()-recentListView.startTime;
						Log.d(TAG, "INTERACT_PRAISE->duration="+duration);
						CmmobiClickAgentWrapper.onEventDuration(this,"ma_intetr", "3", duration);
					}
					break;
				case INTERACT_COMMENT:
					if(recentListView.startTime!=0){
						long duration=TimeHelper.getInstance().now()-recentListView.startTime;
						Log.d(TAG, "INTERACT_COMMENT->duration="+duration);
						CmmobiClickAgentWrapper.onEventDuration(this,"ma_intetr", "4", duration);
					}
					break;

				default:
					break;
				}
				recentListView.startTime=0;
			}
			diaryListView.setVisibility(View.VISIBLE);
			vTitleChoice.setClickable(true);
			tvChoiceName.setText(getDiaryChoiceName());
			vXiala.setVisibility(View.VISIBLE);
			recentListView.setVisibility(View.GONE);
			if(flDiaryMap.getVisibility() == View.VISIBLE) {
				CmmobiClickAgentWrapper.onEventEnd(this, "ma_map_m");
			}
			flDiaryMap.setVisibility(View.GONE);
			mv_diaryMap.setVisibility(View.GONE);
			vTimeReplace.setVisibility(View.GONE);
			setTask();
			ivDiaryBack.setVisibility(View.GONE);
			ivMore.setVisibility(View.VISIBLE);
			sendBroadcast(new Intent(HomeActivity.FLAG_BOTTOM_BAR_SHOW));
			if(DiaryManager.commentCount!=0){
				VTitleBarJiaobiao.setVisibility(View.VISIBLE);
			}else{
				VTitleBarJiaobiao.setVisibility(View.INVISIBLE);
			}
			break;
		case R.id.tv_tasks:
			CmmobiClickAgentWrapper.onEvent(this, "ma_task_m");
			startActivity(new Intent(this, NetworkTaskActivity.class));
			break;
		case R.id.btn_from_camera:// 拍照
			if (portraitMenu != null)
				portraitMenu.dismiss();
			path = Environment.getExternalStorageDirectory() + Constant.SD_STORAGE_ROOT + "/" + ActiveAccount.getInstance(this).getUID() + "/pic/" + "portrait.jpg";
			webimage_file = new File(path);
			if(!webimage_file.getParentFile().exists()) {
				webimage_file.getParentFile().mkdirs();
			}
			Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
			cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(webimage_file));
			startActivityForResult(cameraIntent, CAMERA_REQUEST);
			break;
		case R.id.btn_from_pictures:// 从相册中选择
			if (portraitMenu != null)
				portraitMenu.dismiss();
			Intent intent = new Intent();
			path = Environment.getExternalStorageDirectory() + Constant.SD_STORAGE_ROOT + "/" + ActiveAccount.getInstance(this).getUID() + "/pic/" + "portrait.jpg";
			webimage_file = new File(path);
			if(!webimage_file.getParentFile().exists()) {
				webimage_file.getParentFile().mkdirs();
			}
			intent.setType("image/*");
			intent.putExtra("output", Uri.fromFile(webimage_file));
			intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
			intent.putExtra("crop", "true");
			intent.putExtra("aspectX", 1);// 裁剪框比例
			intent.putExtra("aspectY", 1);
			intent.putExtra("outputX", crop);// 输出图片大小
			intent.putExtra("outputY", crop);
			intent.putExtra("scale", true);
			intent.putExtra("return-data", false);
			intent.setAction(Intent.ACTION_GET_CONTENT);
			startActivityForResult(intent, PICTURE_REQUEST);
			break;
		case R.id.btn_set_mood://设置心情短语
			if(portraitMenu!=null)portraitMenu.dismiss();
			startActivity(new Intent(this,SettingSignatureActivity.class));
			break;
		case R.id.btn_cancel:// 取消
			if (portraitMenu != null)
				portraitMenu.dismiss();
			break;
		default:
			break;
		}
	}
	
	private String getDiaryChoiceName(){
		switch (diaryFilterType) {
		case ALL:
			return getString(R.string.homepage_my_album);
		case UNSYNCHRONIZED:
			return getString(R.string.homepage_diary_unsync);
		case SHARED:
			return getString(R.string.homepage_diary_shared);
		case SAVEBOX:
			return getString(R.string.homepage_diary_savebox);
		case TAG:
			return getString(R.string.homepage_diary_tags);
		default:
			return null;
		}
	}
	
	private static final int LOOP_PERIOD = 30000;// 毫秒
	Runnable timeTask = new Runnable() {

		@Override
		public void run() {
			Calendar c = Calendar.getInstance();
			String timeHHmm = DateUtils.dateToString(c.getTime(),
					DateUtils.DATE_FORMAT_TODAY);
			String timeDay = DateUtils.dateToString(c.getTime(),
					DateUtils.DATE_FORMAT_NORMAL_1);
			if (timeDay.startsWith("0"))
				timeDay = timeDay.substring(1, timeDay.length());
			float hour = c.get(Calendar.HOUR_OF_DAY);
			// float seconds=c.get(Calendar.SECOND);
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
			handler.obtainMessage(HANDLER_UPDATE_TIME,
					new Object[] { timeHHmm, timeDay, bHour, bSecond })
					.sendToTarget();
			handler.postDelayed(this, LOOP_PERIOD);
		}
	};

	@Override
	protected void onDestroy() {
		handler.removeCallbacks(timeTask);
		getZReceiverManager().unregisterLocalZReceiver(updateWeatherReceiver);
		getZReceiverManager().unregisterLocalZReceiver(
				updateNetworkTaskReceiver);
		mv_diaryMap.destroy();
		LocalBroadcastManager.getInstance(this).unregisterReceiver(userInfoChangedRecever);
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

	public static int moods[] = { R.drawable.xinqing_chijing,
			R.drawable.xinqing_gaoxing, R.drawable.xinqing_jusang,
			R.drawable.xinqing_ku, R.drawable.xinqing_shengqi,
			R.drawable.xinqing_tiaopi, R.drawable.xinqing_weiqu,
			R.drawable.xinqing_xinhuanufang, R.drawable.xinqing_yanwu };

//	static String moods_id[] = { "", "", "", "", "", "", "", "", "" };

	class myMoodAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			return moods.length;
		}

		@Override
		public Object getItem(int position) {
			return moods[position];
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder;
			if (null == convertView) {
				holder = new ViewHolder();
				convertView = inflater.inflate(
						R.layout.activity_homepage_mood_list_item, null);
				holder.ivMood = (ImageView) convertView
						.findViewById(R.id.iv_mood_item);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			holder.ivMood.setImageResource(moods[position]);
			return convertView;
		}

	}

	private static String[] FILTER_TEXT;

	private void initFilterText() {
		FILTER_TEXT = getResources().getStringArray(
				R.array.homepage_filter_text);
	}

	
	private void filterClick(){
		switch (diaryFilterType) {
		case ALL:
		case UNSYNCHRONIZED:
		case SHARED:
		case SAVEBOX:
			initLoadLocalDiary();
			break;
		case TAG:
			if(mPopupTagsWindow!=null&&mPopupTagsWindow.isShowing())return;
			tagsView.setTag(getString(R.string.homepage_diary_tags));
			tagsView.loadTags(diaryManager,saveboxIsOpen());
			mPopupTagsWindow = new PopupWindow(tagsView,
					LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT, true);
			mPopupTagsWindow.setBackgroundDrawable(new BitmapDrawable());
			mPopupTagsWindow.showAtLocation(findViewById(R.id.rl_homepage), Gravity.TOP, 0, 0);
			mPopupTagsWindow.setOnDismissListener(new OnDismissListener() {
				
				@Override
				public void onDismiss() {
					if(getString(R.string.homepage_diary_tags).equals(tagsView.getTag())){
						diaryFilterType=FilterType.ALL;
						tvChoiceName.setText(getDiaryChoiceName());
						filterClick();
					}
				}
			});
			tagsView.setCloseListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					if (mPopupTagsWindow != null)
						mPopupTagsWindow.dismiss();
//					diaryListView.reset();
//					diaryListView.loadMore(null);
//					tvChoiceName.setText(getString(R.string.homepage_diary_tags));
					diaryFilterType=FilterType.ALL;
					tvChoiceName.setText(getDiaryChoiceName());
					filterClick();
				}
			});
			tagsView.setOnItemClickListener(new OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> parent, View view,
						int position, long id) {
					final taglistItem tag = (taglistItem) parent
							.getItemAtPosition(position);
					if(tagsView.isValid(tag)){//判断选中的是否是高亮标签
						String text=tagsView.getTag().toString();
						tagsView.setTag(tag.name);
						tagsView.setCheckedTag(tag.name);
						tvChoiceName.setText(tagsView.getTag().toString());
						handler.postDelayed(new Runnable() {
							@Override
							public void run() {
								if (mPopupTagsWindow != null)
									mPopupTagsWindow.dismiss();
							}
						}, 50);
						diaryManager.setTag(tag);
						diaryListView.getItemsList().clear();
						diaryListView.reset();
						diaryListView.loadMore(null);
						initLoadLocalDiary();
//						// 重复点一个tag时，不做操作
//						if(!text.equals(tag.name)){
//						}
					}
				}
			});
			break;

		default:
			break;
		}
	}
	
	//设置地图模式日记数据
	private void setMapDiaryList(){
		if(diaryFilterType==FilterType.ALL){
			mapDiaryList=(ArrayList<MyDiary>) diaryListView.getDiaryList().clone();
		}
	}
	
	PopupWindow mPopupTagsWindow;

	// 加载初始本地数据
	private void initLoadLocalDiary() {
		diaryListView.noMoreData(false);
		diaryListView.reset();
		diaries = (ArrayList<MyDiary>) diaryManager.getDiary(diaryFilterType,userID,
				0,saveboxIsOpen());
		if (diaries != null && diaries.size() > 0) {
			diaryListView.loadMore(diaries);
			setMapDiaryList();
			if(diaries.size()<10){
				diaryListView.noMoreData(true);
			}
		} else {
			if(diaryFilterType!=FilterType.ALL){
				diaryListView.loadMore(null);
				diaryListView.noMoreData(true);
				Log.d(TAG, "initLoadLocalDiary:"+diaryFilterType + " diary not found in local");
			}
		}
		
		if(diaryFilterType==FilterType.ALL&&0==diaries.size()){
			//load到本地数据时，请求最新数据
			Log.d(TAG, "request new diaries from server");
			diaryListView.setTag("new");
			String firstTime=diaryManager.getMyDiaryFirstTime();
			Requester2.homePage(handler, userID,
					firstTime, "1",diaryWidth,diaryHeight,backgroundWidth,backgroundHeight);
		}
	}

	private void refreshDiary() {
		Log.d(TAG, "refreshDiary");
		diaryListView.noMoreData(false);
		switch (diaryFilterType) {
		case ALL:// 全部日记 处理同已发布日记
			// 请求新数据
			diaryListView.setTag("new");
			String firstTime=diaryManager.getMyDiaryFirstTime();
			Requester2.homePage(handler, userID,
					firstTime, "1",diaryWidth,diaryHeight,backgroundWidth,backgroundHeight);
			break;
		case SHARED:// 已分享
		case SAVEBOX:// 保险箱
		case UNSYNCHRONIZED:// 未同步
		case TAG:// 标签
			diaries = (ArrayList<MyDiary>) diaryManager.getDiary(
					diaryFilterType, userID,0,saveboxIsOpen());
			if (diaries != null && diaries.size() > 0) {
				diaryListView.reset();
				diaryListView.loadMore(diaries);
			} else {
				diaryListView.reset();
				diaryListView.loadMore(null);
				diaryListView.noMoreData(true);
				Log.d(TAG, "refreshDiary:"+diaryFilterType + " diary not found in local");
			}
			break;
		default:
			break;
		}
	}
	
	private boolean saveboxIsOpen(){
		if (lsm.getGesturepassword() == null) {
			// 未创建
			return true;
		} else if (lsm.getSafeIsOn()) {
			// 已创建且打开
			return true;
		} else {
			// 已创建但关闭
			return false;
		}
	}

	private void loadMoreDiary() {
		Log.d(TAG, "loadMoreDiary");
		// 先请求本地数据，本地未获取到时请求服务器数据
		diaries = (ArrayList<MyDiary>) diaryManager.getDiary(diaryFilterType,userID,
				diaryListView.getDiaryNum(),saveboxIsOpen());
		Log.d(TAG, "diaryFilterType=" + diaryFilterType);
		if (diaries != null && diaries.size() > 0) {
			diaryListView.loadMore(diaries);
			setMapDiaryList();
		} else {
			Log.d(TAG, diaryFilterType+" no more data");
			diaryListView.noMoreData(true);
			diaryListView.loadDateError();
		}
	}

	// 动态下拉刷新
	private void refreshRecent() {
		recentFreshMode=false;
		recentListView.noMoreData(false);
		getServerRecentDiary();
	}

	// 动态上拉加载
	private void loadMoreRecent() {
		recentFreshMode=true;
		getServerRecentDiary();
	}

	// 请求本地动态页日记数据 如果本地没有数据则返回false
	private void getLocalRecentDiary() {
		Log.d(TAG, "getLocalRecentDiary");
		FilterType filterType = recentListView.getRecentType();
		ArrayList list=new ArrayList();
		switch (filterType) {
		case INTERACT_SHARE:
			list=diaryManager.getShareDiary(userID);
			break;
		case INTERACT_COLLECT:
			list=diaryManager.getCollectDiary();
			break;
		case INTERACT_PRAISE:
			list=diaryManager.getPraiseDiary(userID);
			break;
		case INTERACT_COMMENT:
			list=diaryManager.getCommentDiary();
			break;
		default:
			break;
		}
		if (list != null&&((ArrayList)list).size()>0) {
			recentListView.loadMore(list);
		}
	}

	private String lastTimeShared="";
	private String lastTimeCollect="";
	private String lastTimePraise="";
	private String lastTimeComment="";
	// 请求服务器动态页日记数据
	private void getServerRecentDiary() {
		FilterType filterType = recentListView.getRecentType();
		if(null==filterType)return;
		switch (filterType) {
		case INTERACT_SHARE:// 分享
			if (!recentFreshMode) {
				Requester2.listEnjoyDiary(handler, userID,
						"", "1",recentWidth,recentHeight);
			} else {
				Requester2.listEnjoyDiary(handler, userID,
						lastTimeShared, "2",recentWidth,recentHeight);
			}
			break;
		case INTERACT_COLLECT:// 收藏
			// 获取收藏日记列表
			if (!recentFreshMode) {
				Requester2.listCollectDiary(handler, userID,
						"", "1",recentWidth,recentHeight);
			} else {
				Requester2
						.listCollectDiary(handler, userID,
								lastTimeCollect,
								"2",recentWidth,recentHeight);
			}
			break;
		case INTERACT_PRAISE:// 赞
			// 获取赞日记列表
			if (!recentFreshMode) {
				Requester2.forwardDiaryList(handler,"1",
						"", userID,recentWidth,recentHeight);
			} else {
				Requester2.forwardDiaryList(handler, "2",
						lastTimePraise, userID,recentWidth,recentHeight);
			}
			break;
		case INTERACT_COMMENT:// 评论
			// 获取评论日记列表
			if (!recentFreshMode) {
				Requester2.diaryCommentList(handler,
						"", "1", "",userID);
			} else {
				Requester2.diaryCommentList(handler,
						lastTimeComment, "2", "",userID);
			}
			break;
		default:
			break;
		}
	}

	class myFilterAdapter extends BaseAdapter {

		private ArrayList<String> list;
		private String checkedPos;

		public myFilterAdapter(ArrayList<String> list) {
			this.list = list;
		}

		public void setChecked(String position) {
			this.checkedPos = position;
			notifyDataSetInvalidated();
		}

		@Override
		public int getCount() {
			return list.size();
		}

		@Override
		public Object getItem(int position) {
			return list.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			FilterViewHolder holder;
			String text = list.get(position);
			if (null == convertView) {
				holder = new FilterViewHolder();
				convertView = inflater.inflate(
						R.layout.activity_homepage_filter_list_item, null);
				holder.tvFilter = (TextView) convertView
						.findViewById(R.id.iv_filter_item);
				holder.ivArrow = (ImageView) convertView
						.findViewById(R.id.iv_arrow);
				convertView.setTag(holder);
			} else {
				holder = (FilterViewHolder) convertView.getTag();
			}
			if (text.equals(checkedPos)) {
				holder.tvFilter.setTextColor(Color.WHITE);
			} else {
				holder.tvFilter.setTextColor(Color.parseColor("#9EC649"));
			}
			if (text.equals(FILTER_TEXT[3])
					|| tagsView.getTags().contains(text)) {// 文字为标签及标签内容的选项
				holder.ivArrow.setVisibility(View.VISIBLE);
			} else {
				holder.ivArrow.setVisibility(View.GONE);
			}
			holder.tvFilter.setText(text);
			return convertView;
		}

	}

	static class FilterViewHolder {
		TextView tvFilter;
		ImageView ivArrow;
	}

	static class ViewHolder {
		ImageView ivMood;
	}

	private void initUserInfo() {
		setUserInfo();
		MyWeatherInfo.getInstance(ZApplication.getInstance()).updateWeather(false);
//		setWeather(false, true);
		isFirstOpen = true;
		setTask();
		setZoneBackground();
		setMood();
	}

/*	private boolean isShowSavebox;
	private void showSaveBox(boolean isShow) {
		isShowSavebox=isShow;
	}
*/
	private void setUserInfo() {
		String headUrl = "";
		String nickname = "";
		String signature = "";
		String sex="";
		headUrl = accountInfo.headimageurl;
		nickname = accountInfo.nickname;
		signature = accountInfo.signature;
		sex=accountInfo.sex;
		Log.d(TAG, "headUrl=" + headUrl);
		Log.d(TAG, "nickname=" + nickname);
		Log.d(TAG, "signature=" + signature);
		imageLoader.displayImage(headUrl, ivPortrait, options, animateFirstListener, ActiveAccount.getInstance(this).getUID(), 0);
		tvNickname.setText(nickname);
		tvSignature.setText(signature);
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
	}

	private void setZoneBackground() {
		String url = accountInfo.zoneBackGround;
//		if(url!=null&&url.endsWith(".jpg"))
//			url=url+"&width="+backgroundWidth;
		Log.d(TAG, "setZoneBackground->url=" + url);
//		ivZoneBackground.setImageUrl(0, 1,url, false);
		imageLoader.displayImageEx(url, ivZoneBackground, backgroundoptions, animateFirstListener, ActiveAccount.getInstance(this).getUID(), 0);
		//设置空间背景高度
		ViewGroup.LayoutParams params=ivZoneBackground.getLayoutParams();
		params.height=diaryListView.getBackgroundHeight();
		ivZoneBackground.setLayoutParams(params);
	}
	
	private void setPortraitUrl(String headurl){
		accountInfo.headimageurl=headurl;
	}
	
	private void setNickname(String nickName){
		accountInfo.nickname=nickName;
	}
	
	private void setSignature(String signature){
		accountInfo.signature=signature;
	}

	private boolean hasWeatherCache(MyWeather weather) {
		boolean bRet = false;
		if(weather == null) {
			weather = accountInfo.myWeather;
		}
		if (weather != null && weather.desc != null && weather.desc.length > 0
				&& !"".equals(weather.desc[0].description)) {
			bRet = true;
		}
		return bRet;
	}
	
	/*
	 * type 
	 *  1.??? 
	 *  2.未能获取位置 /天气 
	 *  3.27°C/36°C 汉阳区(武汉市)
	 */
	private void showWeather(int type, MyWeather weather) {
		switch(type) {
		case 0:
			Log.d(TAG, "无位置信息，无法获取天气");
//			tvWeatherRegion.setText("???");
//			tvWeatherTemp.setTextSize(11);
//			tvWeatherTemp.setText("? °C/? °C");
//			ivWeatherIcon.setImageResource(R.drawable.tianqi_weizhi);
			tvWeatherRegion.setVisibility(View.GONE);
			tvWeatherTemp.setVisibility(View.GONE);
			ivWeatherIcon.setVisibility(View.GONE);
			break;
		case 1:
			Log.d(TAG, "未打开GPS与基站(wifi)定位");
			tvWeatherRegion.setVisibility(View.VISIBLE);
			tvWeatherTemp.setVisibility(View.VISIBLE);
			ivWeatherIcon.setVisibility(View.VISIBLE);
			tvWeatherRegion.setText("未能获取");
			tvWeatherTemp.setTextSize(10);
			tvWeatherTemp.setText("位置信息");
			ivWeatherIcon.setImageResource(R.drawable.tianqi_sorry);
			break;
		case 2:
			tvWeatherRegion.setVisibility(View.VISIBLE);
			tvWeatherTemp.setVisibility(View.VISIBLE);
			ivWeatherIcon.setVisibility(View.VISIBLE);
			if(hasWeatherCache(weather)) {
				Log.d(TAG, "有天气缓存");
				String description = weather.desc[0].description;
				String weatherurl = weather.desc[0].weatherurl;
				String city = weather.city;
				String district = weather.district;
				if(district != null && city != null) {
					Log.d(TAG, "有地理位置");
					String region = district + "(" + city + ")";
					
					tvWeatherRegion.setText(region);
					tvWeatherTemp.setText(description);
					ivWeatherIcon.setImageUrl(R.drawable.tianqi_sorry, 1, weatherurl, false);
				} else {
					Log.d(TAG, "无地理位置");
					tvWeatherRegion.setVisibility(View.GONE);
					tvWeatherTemp.setVisibility(View.GONE);
					ivWeatherIcon.setVisibility(View.GONE);
//					//真实未获取到位置信息
//					tvWeatherRegion.setText("未能获取");
//					tvWeatherTemp.setTextSize(10);
//					tvWeatherTemp.setText("位置信息");
//					ivWeatherIcon.setImageResource(R.drawable.tianqi_sorry);
				}
			} else {
				Log.d(TAG, "无天气信息");
				tvWeatherRegion.setVisibility(View.GONE);
				tvWeatherTemp.setVisibility(View.GONE);
				ivWeatherIcon.setVisibility(View.GONE);
			}
			break;
		default:
			break;
		}
	}
	
	@Override
	public boolean dispatchKeyEvent(KeyEvent event) {
		if ((event.getAction() == KeyEvent.ACTION_DOWN && event.getKeyCode() == KeyEvent.KEYCODE_BACK)) {
			
		}
		return super.dispatchKeyEvent(event);
	}
	
	private boolean isXdlgShowing = false;
	
	private void setWeather(boolean isActive, boolean isFirstOpen) {
		Xdialog xdlg = new Xdialog.Builder(this)
		.setTitle("提醒")
		.setMessage(getString(R.string.prompt_weather_nogps))
		.setPositiveButton(android.R.string.ok,
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog,
							int which) {
						ivWeatherIcon.setClickable(true);
						isXdlgShowing = false;
					}
				}).create();
		MyWeather weather = accountInfo.myWeather;
		// I.无GPS和基站
		if (!ZNetworkStateDetector.isGpsOpened2() && !ZNetworkStateDetector.isConnected()) {
			// 1.每次进入app
			if (isFirstOpen) {
				Log.d(TAG, "每次进入app");
				if(!isXdlgShowing) {
					ivWeatherIcon.setClickable(false);
					xdlg.show();
					isXdlgShowing = true;
				}
			}
			//2.无GPS，有天气缓存
			if(hasWeatherCache(weather)) {
				Log.d(TAG, "无GPS,有天气缓存");
				showWeather(2, weather);
			//3.无GPS，无天气缓存
			} else {
				Log.d(TAG, "无GPS,无天气缓存");
				showWeather(1, weather);
				if(!isXdlgShowing && isActive && !isfromRefresh) {
					ivWeatherIcon.setClickable(false);
					xdlg.show();
					isXdlgShowing = true;
				}
			}
			
		// II.开启GPS或基站(WIFI)
		} else {
			Log.d(TAG, "开启了GPS或网络");
			showWeather(2, weather);
		}
		isfromRefresh = false;
	}

	// 心情数组中的序号
	private int moodPosition;

	//设置心情
	private void setMood() {
		String mood = "0";
		mood=accountInfo.mood;
		Log.d(TAG, "setMood->mood="+mood);
		moodPosition=0;
		if(mood!=null&&DateUtils.isNum(mood)){
			moodPosition=Integer.parseInt(mood);
		}
		if(moodPosition>=moods.length)moodPosition=0;
		ivMood.setImageResource(moods[moodPosition]);
	}
	
	private void setMood(int id){
		Log.d(TAG, "setMood(int id)->id="+id);
		accountInfo.mood=id+"";
	}

	// 设置正在进行的任务数
	private void setTask() {
		// setTask
		if(userID!=null){
			int taskNum = NetworkTaskManager.getInstance(userID).getTaskNum();
			if(0==taskNum){
				tvTasks.setVisibility(View.GONE);
			}else{
				tvTasks.setVisibility(View.VISIBLE);
			}
			tvTasks.setText(taskNum + "");
		}
	}

	// 接收天气更新广播
	private class UpdateWeatherReceiver extends ZBroadcastReceiver {

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
				setWeather(isactive, isFirstOpen);
				isFirstOpen = false;
			}
		}
	}

	// 接收天气更新广播
	private class UpdateNetworkTaskReceiver extends ZBroadcastReceiver {

		private UpdateNetworkTaskReceiver() {
			addAction(NetworkTaskManager.ACTION_UPDATE_NETWORKTASK);
		}

		@Override
		public void onReceive(Context context, Intent intent) {
			Log.d(TAG, "intent.getAction() = " + intent.getAction());
			if (intent.getAction().equals(
					NetworkTaskManager.ACTION_UPDATE_NETWORKTASK)) {
				Log.d(TAG, "need to update NetworkTask number");
				setTask();
			}
		}
	}

	private PopupWindow portraitMenu;
	private static final int CAMERA_REQUEST = 1111;
	private static final int PICTURE_REQUEST = 1112;
	private static final int CROP_REQUEST = 1115;
	private static final int BACKGROUND_REQUEST = 1113;
	private static final int SAVEBOX_REQUEST = 1114;

	private String path ;
	private File webimage_file ;
	private int crop = 480;
	
	// 显示头像设置选择界面
	private void showPortraitChoice() {
		if(portraitMenu!=null)portraitMenu.dismiss();
		View view = inflater.inflate(R.layout.activity_homepage_portrait_menu,
				null);
		portraitMenu = new PopupWindow(view, LayoutParams.FILL_PARENT,
				LayoutParams.WRAP_CONTENT, true);
		portraitMenu.setBackgroundDrawable(getResources().getDrawable(
				R.drawable.dot_big));
		portraitMenu.showAtLocation(findViewById(R.id.rl_homepage),
				Gravity.BOTTOM, 0, 0);
		view.findViewById(R.id.btn_from_camera).setOnClickListener(this);
		view.findViewById(R.id.btn_from_pictures).setOnClickListener(this);
		view.findViewById(R.id.btn_set_mood).setOnClickListener(this);
		view.findViewById(R.id.btn_cancel).setOnClickListener(this);
	}
	
	private void showDiaryTitleChoce(){
		if(portraitMenu!=null)portraitMenu.dismiss();
		vXiala.setVisibility(View.INVISIBLE);
		View view = inflater.inflate(R.layout.activity_homepage_diary_title_menu,
				null);
		View vShaixuan=view.findViewById(R.id.ll_shaixuan);
		view.findViewById(R.id.ll_my_all).setOnClickListener(this);
		view.findViewById(R.id.ll_unsync).setOnClickListener(this);
		view.findViewById(R.id.ll_shared).setOnClickListener(this);
		View vSavebox=view.findViewById(R.id.ll_savebox);
		vSavebox.setOnClickListener(this);
		view.findViewById(R.id.ll_tags).setOnClickListener(this);
		if(lsm.getGesturepassword() != null && lsm.getSafeIsOn()){
			vSavebox.setVisibility(View.VISIBLE);
			vShaixuan.setBackgroundResource(R.drawable.shaixuan);
		}else{
			vSavebox.setVisibility(View.GONE);
			vShaixuan.setBackgroundResource(R.drawable.shaixuan_1);
		}
		measureView(view);
		int x=(diaryListView.getScreenWidth()-view.getMeasuredWidth())/2;
		portraitMenu = new PopupWindow(view, LayoutParams.WRAP_CONTENT,
				LayoutParams.WRAP_CONTENT, true);
		portraitMenu.setBackgroundDrawable(getResources().getDrawable(
				R.drawable.dot_big));
		portraitMenu.showAsDropDown(rlTitleBar,x,-10);
		portraitMenu.setOnDismissListener(new OnDismissListener() {
			@Override
			public void onDismiss() {
				vXiala.setVisibility(View.VISIBLE);
			}
		});
	}
	
	private boolean isInteractMode;
	private boolean isMapMode;
	private TextView tvHudongJiaobiao;
	private View rlHudonJiaobiao;
	//显示更多选项
	private void showMoreChoice(){
		if(portraitMenu!=null)portraitMenu.dismiss();
		View view = inflater.inflate(R.layout.activity_homepage_more_menu,
				null);
		portraitMenu = new PopupWindow(view, LayoutParams.WRAP_CONTENT,
				LayoutParams.WRAP_CONTENT, true);
		portraitMenu.setBackgroundDrawable(getResources().getDrawable(
				R.drawable.dot_big));
		portraitMenu.showAsDropDown(ivMore,0,8);
		view.findViewById(R.id.ll_my_interact).setOnClickListener(this);
		view.findViewById(R.id.ll_map_mode).setOnClickListener(this);
		view.findViewById(R.id.ll_my_savebox).setOnClickListener(this);
		view.findViewById(R.id.ll_setting).setOnClickListener(this);
		ImageView ivInteract=(ImageView) view.findViewById(R.id.iv_my_interact);
		TextView tvInteract=(TextView) view.findViewById(R.id.tv_my_interact);
		ImageView ivMap=(ImageView) view.findViewById(R.id.iv_map_mode);
		TextView tvMap=(TextView) view.findViewById(R.id.tv_map_mode);
		TextView tvSavebox=(TextView) view.findViewById(R.id.tv_my_savebox);
		rlHudonJiaobiao=view.findViewById(R.id.rl_hudong_jiaobiao);
		tvHudongJiaobiao=(TextView) view.findViewById(R.id.tv_hudong_jiaobiao);
		if(0==DiaryManager.commentCount){
			rlHudonJiaobiao.setVisibility(View.INVISIBLE);
		}else{
			rlHudonJiaobiao.setVisibility(View.VISIBLE);
			setJiaobiao(tvHudongJiaobiao);
		}
		//保险箱文字显示
		if (lsm.getGesturepassword() == null) {
			// 未创建
			tvSavebox.setText(R.string.homepage_my_create_savebox);
		} else if (lsm.getSafeIsOn()) {
			// 已创建且打开
			tvSavebox.setText(R.string.homepage_my_close_savebox);
		} else {
			// 已创建但关闭
			tvSavebox.setText(R.string.homepage_my_open_savebox);
		}
	}
	
	//显示更换空间背景选项
	private void showChangeZoneBackgroundChoice() {
		if(portraitMenu!=null)portraitMenu.dismiss();
		View view = inflater.inflate(R.layout.activity_homepage_zone_background_menu,
				null);
		portraitMenu = new PopupWindow(view, LayoutParams.FILL_PARENT,
				LayoutParams.WRAP_CONTENT, true);
		portraitMenu.setBackgroundDrawable(getResources().getDrawable(
				R.drawable.dot_big));
		portraitMenu.showAtLocation(findViewById(R.id.rl_homepage),
				Gravity.BOTTOM, 0, 0);
		view.findViewById(R.id.btn_change_background).setOnClickListener(this);
		view.findViewById(R.id.btn_cancel).setOnClickListener(this);
	}
	
	/**
	 * 裁剪图片方法实现
	 * @param uri
	 */
	public void startPhotoZoom(Uri uri) {
		Intent intent = new Intent("com.android.camera.action.CROP");
		intent.setDataAndType(uri, "image/*");
		intent.putExtra("crop", "true");
		// aspectX aspectY 是宽高的比例
		intent.putExtra("aspectX", 1);
		intent.putExtra("aspectY", 1);
		// outputX outputY 是裁剪图片宽高
		intent.putExtra("outputX", crop);
		intent.putExtra("outputY", crop);
		intent.putExtra("return-data", false);
		intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
		intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
		intent.putExtra("noFaceDetection", true); // no face detection
		startActivityForResult(intent, CROP_REQUEST);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == CAMERA_REQUEST && resultCode == RESULT_OK) {
			//ZDialog.show(R.layout.progressdialog, false, true, this);
			startPhotoZoom(Uri.fromFile(webimage_file));
		}
		if (requestCode == CROP_REQUEST && resultCode == RESULT_OK){
			Requester2.uploadPicture(handler, path,"1","");
		}
		if (requestCode == PICTURE_REQUEST && resultCode == RESULT_OK) {
			ZDialog.show(R.layout.progressdialog, false, true, this);
			Requester2.uploadPicture(handler,  path,"1","");	
		}
		if(requestCode==BACKGROUND_REQUEST&&resultCode==RESULT_OK){
			setZoneBackground();
		}
		if(requestCode==SAVEBOX_REQUEST&&resultCode==RESULT_OK){
			if (FilterType.SAVEBOX == diaryFilterType)
				return;
			diaryFilterType = FilterType.SAVEBOX;
			initLoadLocalDiary();// 加载本地数据
		}
		
		
		super.onActivityResult(requestCode, resultCode, data);
	}

	private BroadcastReceiver mSaveBoxReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			if (lsm.getSafeIsOn()) {
				boolean isFromDetail=intent.getBooleanExtra("isfromdetail", false);
				if(isFromDetail)return;
				if(lsm.getIsFromSetting()) return;
				// 已创建且打开
				//保险箱打开后，筛选条件自动跳到保险箱
				if(diaryFilterType==FilterType.SAVEBOX)return;
				diaryFilterType=FilterType.SAVEBOX;
				tvChoiceName.setText(getDiaryChoiceName());
				filterClick();
			} else{
				// 已创建但关闭
				if(diaryFilterType==FilterType.SAVEBOX){
					diaryFilterType=FilterType.ALL;
					diaryListView.reset();
					diaryListView.loadMore(null);
					tvChoiceName.setText(getDiaryChoiceName());
					filterClick();
				}else{
					handler.sendEmptyMessage(HANDLER_MYHOMEPAGEDATACHANGED);
					handler.sendEmptyMessage(HANDLER_MYSAVEBOXDATACHANGED);
					handler.sendEmptyMessage(HANDLER_MYUNPUBLISHDATACHANGED);
					handler.sendEmptyMessage(HANDLER_MYTAGDATACHANGED);
					handler.sendEmptyMessage(HANDLER_MYSHAREDATACHANGED);
				}
			}
		}
	};
	
	private BroadcastReceiver userInfoChangedRecever = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			String action=intent.getAction();
			if(PrivateMessageService.FLAG_MESSAGE_DATA_UPDATE.equals(action)){
				String fansCount=intent.getStringExtra("fansnum");
				String attendedCount=intent.getStringExtra("attentionnum");
				String commentCount=intent.getStringExtra("commentnum");
				Log.d(TAG, "fansnum="+fansCount);
				Log.d(TAG, "attentionnum="+attendedCount);
				Log.d(TAG, "commentCount="+commentCount);
				if(commentCount!=null&&DateUtils.isNum(commentCount)){
					DiaryManager.commentCount+=Integer.parseInt(commentCount);
				}
				if(View.VISIBLE==diaryListView.getVisibility()){
					if(DiaryManager.commentCount!=0){
						VTitleBarJiaobiao.setVisibility(View.VISIBLE);
						VCommentJiaobiao.setVisibility(View.VISIBLE);
						if(rlHudonJiaobiao!=null)rlHudonJiaobiao.setVisibility(View.VISIBLE);
					}else{
						VCommentJiaobiao.setVisibility(View.INVISIBLE);
						VTitleBarJiaobiao.setVisibility(View.INVISIBLE);
						if(rlHudonJiaobiao!=null)rlHudonJiaobiao.setVisibility(View.INVISIBLE);
					}
					setJiaobiao(tvTitleBarJiaobiao);
					setJiaobiao(tvCommentJiaobiao);
					setJiaobiao(tvHudongJiaobiao);
				}
			}else{
				//设置头像 心情 签名
				System.out.println("receive portrait changed ===");
				setUserInfo();
				setMood();
			}
		}
	};
	
	private void setJiaobiao(TextView textView){
		String text=""+DiaryManager.commentCount;
		if(DiaryManager.commentCount>99){
			text="99+";
		}
		if(textView!=null)
			textView.setText(text);
	}


	class mydiaryDataChangedListener implements MydiaryDataChangedListener{

		@Override
		public void myHomepageDataChanged() {
			Log.d(TAG, "myHomepageDataChanged");
			handler.sendEmptyMessage(HANDLER_MYHOMEPAGEDATACHANGED);
		}
		
		@Override
		public void myPublishDataChanged() {
			Log.d(TAG, "myPublishDataChanged");
			handler.sendEmptyMessage(HANDLER_MYPUBLISHDATACHANGED);
			
		}
		
		@Override
		public void mySaveBoxDataChanged() {
			Log.d(TAG, "mySaveBoxDataChanged");
			handler.sendEmptyMessage(HANDLER_MYSAVEBOXDATACHANGED);
		}
		
		@Override
		public void myUnuploadDataChanged() {
			Log.d(TAG, "myUnuploadDataChanged");
			handler.sendEmptyMessage(HANDLER_MYUNPUBLISHDATACHANGED);
		}
		
		@Override
		public void myTagDataChanged() {
			Log.d(TAG, "myTagDataChanged");
			handler.sendEmptyMessage(HANDLER_MYTAGDATACHANGED);
		}
		
		@Override
		public void myShareDataChanged() {
			Log.d(TAG, "myShareDataChanged");
			handler.sendEmptyMessage(HANDLER_MYSHAREDATACHANGED);
		}
		
		@Override
		public void myCollectDataChanged() {
			Log.d(TAG, "myCollectDataChanged");
			handler.sendEmptyMessage(HANDLER_MYENSHRINEDATACHANGED);
		}
		
		@Override
		public void myPraiseDataChanged() {
			Log.d(TAG, "myPraiseDataChanged");
			handler.sendEmptyMessage(HANDLER_MYPRAISEDATACHANGED);
		}
	}
	
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
    updateTimeThread1 thread1=new updateTimeThread1();
    private void scrollOperate(int t,int oldt){
    	int[] location=new int[2];
    	vTime.getLocationInWindow(location);
		int height=0;
		int y=location[1];
		if(View.GONE!=rlTitleBar.getVisibility()){
			height=rlTitleBar.getHeight();
		}
//		if(y<height){
		if(y<0){
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
//				rlTitleBar.setVisibility(View.GONE);
//				sendBroadcast(new Intent(HomeActivity.FLAG_BOTTOM_BAR_HIDDEN));
				dimissTitleAndBottomBar();
			}
		}else if(offset<-10){//下滑 显示
			if(0==isSlipUP||2!=isSlipUP){
				if(TimeHelper.getInstance().now()-timeOffset<300)return;
				timeOffset=TimeHelper.getInstance().now();
				isSlipUP=2;
//				rlTitleBar.setVisibility(View.VISIBLE);
//				sendBroadcast(new Intent(HomeActivity.FLAG_BOTTOM_BAR_SHOW));
				isShowTitleAndBottomBar();
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
    
    private void isShowTitleAndBottomBar(){
    	if(!isDeleteMode)
    		showTitleAndBottomBar();
    }
    
    private void showTitleAndBottomBar(){
    	rlTitleBar.setVisibility(View.VISIBLE);
		sendBroadcast(new Intent(HomeActivity.FLAG_BOTTOM_BAR_SHOW));
    }
    
    private void dimissTitleAndBottomBar(){
    	rlTitleBar.setVisibility(View.GONE);
		sendBroadcast(new Intent(HomeActivity.FLAG_BOTTOM_BAR_HIDDEN));
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
    	
//    	vMoodReplace=vCenterReplace.findViewById(R.id.rl_mood);
//    	vMoodReplace.setOnClickListener(this);
//    	ivMoodReplace= (ImageView) vCenterReplace.findViewById(R.id.iv_mood);
//    	vTaskReplace=vCenterReplace.findViewById(R.id.rl_task_num);
//    	vTaskReplace.setOnClickListener(this);
//    	tvTaskReplace= (TextView) vCenterReplace.findViewById(R.id.tv_task_num);
//    	rlFilterReplace= vCenterReplace.findViewById(R.id.rl_filter);
//    	rlFilterReplace.setOnClickListener(this);
//    	rlFilterReplace.setTag("replace");
//    	tvFilterReplace= (TextView) vCenterReplace.findViewById(R.id.tv_filter);
    }
    
    private String lastTime;
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
    					if(!time.equals(lastTime)){
//    						new Thread(new updateTimeThread(time)).start();
    						thread1.setTimemilli(time);
    						thread1.run();
    					}
    					lastTime=time;
    				}
//    				WebImageView ivPic=(WebImageView) view.findViewById(R.id.iv_pic);//图片类型
//    				if(View.VISIBLE==ivPic.getVisibility()){
//    					if(y>pScreenHeight2||y<nScreenHeight2){//超出屏幕高度的2倍
//    						//释放图片资源
////    						ivPic.recycle();
//    					}else if(y<=pScreenHeight2&&y>=nScreenHeight2){//屏幕2倍高度范围内
//    						//重载图片资源
////    						ivPic.reload();
//    					}
//    				}
    			}
    		}
    	}
    }
    
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
    
    class updateTimeThread1 extends Thread{

    	private String timemilli;
    	public updateTimeThread1(){
    	}
    	
    	public void setTimemilli(String timemilli){
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
    
/*    private String writePortraitToSDcard(byte[] data){
    	try {
			String path = Environment.getExternalStorageDirectory() + Constant.SD_STORAGE_ROOT + "/" + userID + "/pic/" + "portrait.jpg";
			File webimage_file = new File(path);
			if(!webimage_file.getParentFile().exists()) {
				webimage_file.getParentFile().mkdirs();
			}
			FileOutputStream mOutput = new FileOutputStream(webimage_file, false);
			mOutput.write(data);
			mOutput.close();
			return path;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
    	return null;
    }*/
}
