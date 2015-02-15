package com.cmmobi.looklook.activity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import android.os.Parcelable;
import android.os.SystemClock;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import cn.zipper.framwork.core.ZApplication;
import cn.zipper.framwork.core.ZToast;
import cn.zipper.framwork.io.network.ZNetworkStateDetector;

import com.baidu.mapapi.map.MyLocationOverlay;
import com.baidu.platform.comapi.basestruct.GeoPoint;
import com.cmmobi.looklook.R;
import com.cmmobi.looklook.common.adapter.MapNearbyListAdapter;
import com.cmmobi.looklook.common.gson.GsonResponse3.MyDiary;
import com.cmmobi.looklook.common.gson.GsonResponse3.MyDiaryList;
import com.cmmobi.looklook.common.gson.GsonResponse3.homeResponse;
import com.cmmobi.looklook.common.gson.GsonResponse3.listMyDiaryResponse;
import com.cmmobi.looklook.common.gson.Requester3;
import com.cmmobi.looklook.common.utils.CmmobiClickAgentWrapper;
import com.cmmobi.looklook.common.utils.DateUtils;
import com.cmmobi.looklook.common.utils.DisplayUtil;
import com.cmmobi.looklook.common.view.ContentThumbnailView;
import com.cmmobi.looklook.common.view.FriendsExpressionView;
import com.cmmobi.looklook.common.view.pulltorefresh.PullToRefreshBase;
import com.cmmobi.looklook.common.view.pulltorefresh.PullToRefreshBase.Mode;
import com.cmmobi.looklook.common.view.pulltorefresh.PullToRefreshBase.OnHeaderScrolledListener;
import com.cmmobi.looklook.common.view.pulltorefresh.PullToRefreshBase.OnPullEventListener;
import com.cmmobi.looklook.common.view.pulltorefresh.PullToRefreshBase.OnRefreshListener2;
import com.cmmobi.looklook.common.view.pulltorefresh.PullToRefreshBase.State;
import com.cmmobi.looklook.common.view.pulltorefresh.PullToRefreshListView;
import com.cmmobi.looklook.fragment.MyZoneFragment.DiaryShareItem;
import com.cmmobi.looklook.fragment.MyZoneFragment.MyZoneItem;
import com.cmmobi.looklook.fragment.MyZoneFragment.UserInfo;
import com.cmmobi.looklook.info.profile.AccountInfo;
import com.cmmobi.looklook.info.profile.ActiveAccount;
import com.cmmobi.looklook.info.profile.ContactManager;
import com.cmmobi.looklook.info.profile.DiaryManager;
import com.cmmobi.looklook.info.profile.WrapUser;
import com.cmmobi.looklook.map.MapItemHelper;
import com.cmmobi.looklook.map.MapItemHelper.Item;
import com.cmmobi.looklook.map.MyItemizedOverlay;
import com.cmmobi.looklook.map.MyMapView;
import com.cmmobi.looklook.prompt.Prompt;
import com.cmmobi.statistics.CmmobiClickAgent;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.nostra13.universalimageloader.core.display.SimpleBitmapDisplayer;

/**
 * @author wuxiang
 * @email wuxiang@cmmobi.com
 * @date 2013-12-16
 */
public class OtherZoneActivity extends Activity implements
		OnPageChangeListener,OnClickListener,Callback {

	private static final String TAG=OtherZoneActivity.class.getName();
	private ViewPager pagerContainer;
	private MyPagerAdapter myPagerAdapter;
	private ArrayList<zoneLayout> listViews = new ArrayList<zoneLayout>();
	
	private Handler handler=new Handler(this);
	private TextView tvTitle; 
	
	private String userID;
	private AccountInfo accountInfo;
	private WrapUser currUserInfo;
	private List<WrapUser> wrapUsers=null;
	private int currIndex;
	
	
	private Button menu;
	
	// UI components
	private MyMapView bmv_nearby;
	private ListView lv_info;
	private Button btn_cancel;
	private Animation waitanim;
	private ImageView iv_wait;
	private RelativeLayout fl_activity_discover_map_tankuang;
	private FrameLayout fl_activity_homepage_otherdiary_map, fl_bg_shadow;
	private ArrayList<MyDiary> mapdiary;
	private ArrayList<MyDiaryList> mapdiarylist;
	
	private int screenMode = 0;
	private final int ALBUMS_MODE = 0;
	private final int MAP_MODE = 1;
	
	//baidu map
	private MyLocationOverlay myLocationOverlay = null;
	//GeoPoint gp_center;
	//List<OverlayItem> mGeoList;
	private Item[] objs;
	private MapNearbyListAdapter list_adapter;
	
	private final int HANDLER_FLAG_INIT_VIEW = 0xfff00001;
	
	public static final String OTHER_ZONE_USERID="OTHER_ZONE_USERID";
	public static final String OTHER_ZONE_TYPE="OTHER_ZONE_TYPE";
	
	public static final String OTHER_ZONE_TYPE_FRIEND="OTHER_ZONE_TYPE_FRIEND";
	public static final String OTHER_ZONE_TYPE_ATTENTION="OTHER_ZONE_TYPE_ATTENTION";
	public static final String OTHER_ZONE_TYPE_FANS="OTHER_ZONE_TYPE_FANS";
	public static final String OTHER_ZONE_TYPE_RECOMMAND="OTHER_ZONE_TYPE_RECOMMAND";
	public static final String OTHER_ZONE_TYPE_SNS="OTHER_ZONE_TYPE_SNS";
	public static final String OTHER_ZONE_TYPE_PHONE="OTHER_ZONE_TYPE_PHONE";
	
	//for test
	public static final String mytestuserid="ab1a92e40b4fc04f0c0b3ad0aca4335216e6";
	public static final String othertestuserid="9dea106a07dba0429a0acec0e06bb796c71a";
	
	private LayoutInflater inflater;
	
	private String tempUserid;

	private boolean isServiceUser;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_other_zone);
		initViews(null);
	}
	
	@Override
	protected void onNewIntent(Intent intent) {
		initViews(intent);
		super.onNewIntent(intent);
	}
	
	private void initViews(Intent intent){
		inflater=LayoutInflater.from(this);
		userID = ActiveAccount.getInstance(ZApplication.getInstance()).getUID();
		accountInfo = AccountInfo.getInstance(userID);
		findViewById(R.id.btn_title_left).setOnClickListener(this);
		findViewById(R.id.btn_title_left).setOnLongClickListener(new View.OnLongClickListener() {
			
			@Override
			public boolean onLongClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(OtherZoneActivity.this,LookLookActivity.class);
				intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(intent);
				OtherZoneActivity.this.finish();
				return false;
			}
		});
		
		/*Button */menu=(Button) findViewById(R.id.btn_title_right);
		menu.setOnClickListener(this);
		menu.setBackgroundResource(R.drawable.btn_menu_more);
		tvTitle=(TextView) findViewById(R.id.tv_title);
		
		bmv_nearby = (MyMapView) findViewById(R.id.bmv_activity_discover_main_nearby);
		lv_info = (ListView) findViewById(R.id.lv_activity_discover_main_list);
		btn_cancel = (Button) findViewById(R.id.btn_map_cancel);
		mapdiary = new ArrayList<MyDiary>();
		mapdiarylist = new ArrayList<MyDiaryList>();
		fl_activity_discover_map_tankuang = (RelativeLayout) findViewById(R.id.fl_activity_discover_map_tankuang);
		fl_activity_homepage_otherdiary_map = (FrameLayout) findViewById(R.id.fl_activity_homepage_otherdiary_map);
		fl_bg_shadow = (FrameLayout) findViewById(R.id.fl_translucent_layout);
		LinearInterpolator lir = new LinearInterpolator();  
		waitanim = AnimationUtils.loadAnimation(this, R.anim.map_waiting_animation);
		waitanim.setInterpolator(lir);
		iv_wait = (ImageView) findViewById(R.id.iv_waiting);
		iv_wait.setVisibility(View.GONE);
		btn_cancel.setOnClickListener(this);
		bmv_nearby.setClickable(false);
		bmv_nearby.setHandler(handler);
		bmv_nearby.setDoubleClickZooming(false);
		
		pagerContainer = (ViewPager) findViewById(R.id.pager_container);
		pagerContainer.setOnPageChangeListener(this);
		listViews.clear();
		currUserInfo=null;
		wrapUsers=null;
		currIndex=0;
		tempUserid="";
		myPagerAdapter=new MyPagerAdapter(listViews);
//		initListView();
		pagerContainer.setAdapter(myPagerAdapter);
		initUserInfo(intent);
		handler.sendEmptyMessage(HANDLER_FLAG_INIT_VIEW);
	}



	@Override
	protected void onPause() {
		super.onPause();
		bmv_nearby.onPause();
	}
	
    @Override
	public void onResume() {
        super.onResume();
        bmv_nearby.onResume();
        handler.sendEmptyMessageDelayed(MyMapView.HANDLER_FLAG_MAPVIEW_UPDATE, 500);
    }
    
    @Override
	public void onDestroy() {
        super.onDestroy();
        bmv_nearby.destroy();
    }
    
	private void initUserInfo(Intent intent){
		String type="";
		if(intent!=null){
			tempUserid=intent.getStringExtra(OTHER_ZONE_USERID);
			type=intent.getStringExtra(OTHER_ZONE_TYPE);
		}else{
			tempUserid=getIntent().getStringExtra(OTHER_ZONE_USERID);
			type=getIntent().getStringExtra(OTHER_ZONE_TYPE);
		}
		if(null==tempUserid){
			Prompt.Alert("userid is null");
			finish();
		}
		
		if(accountInfo.serviceUser!=null){
			if(tempUserid.equals(accountInfo.serviceUser.userid)){
				Log.d(TAG, "此用户为客服");
				currUserInfo=accountInfo.serviceUser;
				//隐藏右上角... 按钮
				menu.setVisibility(View.INVISIBLE);
				//隐藏我们的微享数按钮 在myadapter getview中操作
				isServiceUser=true;
			}
		}
		if(null==currUserInfo){
			ContactManager friendsListContactManager=accountInfo.friendsListName;
			currUserInfo=friendsListContactManager.findUserByUserid(tempUserid);
			if(null==currUserInfo){
				Log.e(TAG, "朋友列表中未找到用户");
				finish();
				return;
			}
		}
		
		initPagerUserInfo();
		if (!(ZNetworkStateDetector.isAvailable()
				&& ZNetworkStateDetector.isConnected())){
			Prompt.Alert(getString(R.string.prompt_network_error));
			return;
		}
//		Requester3.otherHome(handler, "", "", "", "", "", "", currUserInfo.userid);
		Requester3.homePage(handler,currUserInfo.userid, "", "", "", "", "", "");
//		wrapUsers=friendsListContactManager.getCache();
		/*ContactManager attentionContactManager=accountInfo.attentionListName;
		ContactManager fansListContactManager=accountInfo.fansList;
		ContactManager recommendContactManager = accountInfo.recommendList;
		ContactManager snsContactManager = accountInfo.snsList;
		ContactManager phoneContactManager = accountInfo.phoneUserList;
		if(null==type){
			currUserInfo=friendsListContactManager.findUserByUserid(tempUserid);
			if(null==currUserInfo)
				currUserInfo=attentionContactManager.findUserByUserid(tempUserid);
			if(null==currUserInfo){
				currUserInfo=fansListContactManager.findUserByUserid(tempUserid);
			}else{
				isShowMarkNameBtn=true;
			}
			if(null!=currUserInfo){
				Log.d(TAG, "初始化用户信息");
				initPagerUserInfo();
				Prompt.showProgressDialog(this);
				//请求用户空间数据
				Requester3.otherHome(handler, "", "", "", "", "", "", currUserInfo.userid);
			}else{
				Log.d(TAG, "通讯录中未找到该用户,请求网络数据");
				UserInfo userInfo=new UserInfo();
				userInfo.userid=tempUserid;
				listViews.clear();
				initUserName();
				zoneLayout zoneLayout = new zoneLayout(this);
				zoneLayout.initUserInfo(userInfo);
				listViews.add(zoneLayout);
				myPagerAdapter.notifyDataSetChanged();
				Prompt.showProgressDialog(this);
				Requester3.otherHome(handler, "", "", "", "", "", "", tempUserid);
			}
			return;
		}else if(OTHER_ZONE_TYPE_FRIEND.equals(type)){
			currUserInfo=friendsListContactManager.findUserByUserid(tempUserid);
			if(null==currUserInfo){
				Log.d(TAG, "朋友列表中未找到用户");
				finish();
				return;
			}
			isShowMarkNameBtn=true;
			wrapUsers=friendsListContactManager.getCache();
		}else if(OTHER_ZONE_TYPE_ATTENTION.equals(type)){
			currUserInfo=attentionContactManager.findUserByUserid(tempUserid);
			if(null==currUserInfo){
				Log.d(TAG, "关注列表中未找到用户");
				finish();
				return;
			}
			isShowMarkNameBtn=true;
			wrapUsers=attentionContactManager.getCache();
		}else if(OTHER_ZONE_TYPE_FANS.equals(type)){
			currUserInfo=fansListContactManager.findUserByUserid(tempUserid);
			if(null==currUserInfo){
				Log.d(TAG, "粉丝列表中未找到用户");
				finish();
				return;
			}
			wrapUsers=fansListContactManager.getCache();
		}else if(OTHER_ZONE_TYPE_RECOMMAND.equals(type)){
			currUserInfo=recommendContactManager.findUserByUserid(tempUserid);
			if(null==currUserInfo){
				Log.d(TAG, "推荐列表中未找到用户");
				finish();
				return;
			}
			wrapUsers=recommendContactManager.getCache();
		}else if(OTHER_ZONE_TYPE_SNS.equals(type)){
			currUserInfo=snsContactManager.findUserByUserid(tempUserid);
			if(null==currUserInfo){
				Log.d(TAG, "第三方加入列表中未找到用户");
				finish();
				return;
			}
			wrapUsers=snsContactManager.getCache();
		}else if(OTHER_ZONE_TYPE_PHONE.equals(type)){
			currUserInfo=phoneContactManager.findUserByUserid(tempUserid);
			if(null==currUserInfo){
				Log.d(TAG, "手机通讯录列表中未找到用户");
				finish();
				return;
			}
			wrapUsers=phoneContactManager.getCache();
		}
		initPagerUserInfo(wrapUsers);*/
	}
	
	private void initUserName(){
		if(null==currUserInfo)return;
		if(!TextUtils.isEmpty(currUserInfo.markname)){
			FriendsExpressionView.replacedExpressions(currUserInfo.markname + "的空间", tvTitle);
		}else if(!TextUtils.isEmpty(currUserInfo.nickname)){
			FriendsExpressionView.replacedExpressions(currUserInfo.nickname + "的空间", tvTitle);
		}else if(!TextUtils.isEmpty(currUserInfo.telname)){
			FriendsExpressionView.replacedExpressions(currUserInfo.telname + "的空间", tvTitle);
		}else{
			tvTitle.setText(currUserInfo.micnum);
		}
	}
	
	//初始化多个用户信息
	private void initPagerUserInfo(List<WrapUser> wrapUsers){
		initUserName();
		if(wrapUsers!=null){
			listViews.clear();
			for(int i=0;i<wrapUsers.size();i++){
				WrapUser wrapUser=wrapUsers.get(i);
				if(wrapUser.userid.equals(currUserInfo.userid))
					currIndex=i;
				UserInfo userInfo=new UserInfo();
				userInfo.headUrl=wrapUser.headimageurl;
				userInfo.backname=wrapUser.markname;
				userInfo.nickname=wrapUser.nickname;
				userInfo.backgroundUrl="";
				userInfo.sex=wrapUser.sex;
				userInfo.signature=wrapUser.signature;
				userInfo.userid=wrapUser.userid;
				zoneLayout zoneLayout = new zoneLayout(this);
				zoneLayout.initUserInfo(userInfo);
				listViews.add(zoneLayout);
			}
			myPagerAdapter.notifyDataSetChanged();
			if(currIndex!=0){
				pagerContainer.setCurrentItem(currIndex);
			}else{
				//请求用户空间数据
				Prompt.showProgressDialog(this);
				Requester3.homePage(handler, currUserInfo.userid, "", "", "", "", "", "");
			}
		}
	}
	
	//初始化单个用户信息
	private void initPagerUserInfo(){
		UserInfo userInfo=new UserInfo();
		userInfo.headUrl=currUserInfo.headimageurl;
		userInfo.backname=currUserInfo.markname;
		userInfo.nickname=currUserInfo.nickname;
		userInfo.backgroundUrl="";
		userInfo.sex=currUserInfo.sex;
		userInfo.signature=currUserInfo.signature;
		userInfo.userid=currUserInfo.userid;
		listViews.clear();
		initUserName();
		zoneLayout zoneLayout = new zoneLayout(this);
		zoneLayout.initUserInfo(userInfo);
		listViews.add(zoneLayout);
		myPagerAdapter.notifyDataSetChanged();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_more_menu_bakname:
			String backName=currUserInfo.markname;
			String nickname=currUserInfo.nickname;
			String userid=currUserInfo.userid;
			Intent intent=new Intent(OtherZoneActivity.this,BacknameActivity.class);
			if(!TextUtils.isEmpty(backName)){
				intent.putExtra("backname", backName);
			}else{
				intent.putExtra("backname", nickname);
			}
			intent.putExtra("otherUserid", userid);
			startActivityForResult(intent, REQUESTCODE);
			if(mPopupWindow!=null)
				mPopupWindow.dismiss();
			break;
		case R.id.btn_more_menu_cancel:
			if(mPopupWindow!=null)
				mPopupWindow.dismiss();
			break;
		case R.id.btn_title_right:
			showMenu();
			break;
		case R.id.btn_title_left:
			if(screenMode == ALBUMS_MODE) {
				finish();
			} else if(screenMode == MAP_MODE) {
				pagerContainer.setVisibility(View.VISIBLE);
				fl_activity_homepage_otherdiary_map.setVisibility(View.GONE);
				//2014-4-8
//				HashMap<String, String> hm = new HashMap<String, String>();
//				hm.put("label", currUserInfo.userid);
//				CmmobiClickAgentWrapper.onEventDuration(OtherZoneActivity.this, "foot_print", currUserInfo.userid, delta);
//				Log.d("==WR==", "他人足迹埋点【userid:" + currUserInfo.userid + "】");
				initUserName();
				screenMode = ALBUMS_MODE;
				menu.setVisibility(View.VISIBLE);
			}
			break;
		case R.id.btn_map_cancel:
			fl_activity_discover_map_tankuang.setVisibility(View.INVISIBLE);
			fl_bg_shadow.setVisibility(View.INVISIBLE);
			break;
		case R.id.btn_cancel://取消popupwindow
			if(popupWindow.isShowing()){
				popupWindow.dismiss();
			}
			break;
//		case R.id.btn_add_friend://加好友
//			if(popupWindow.isShowing()){
//				popupWindow.dismiss();
//			}
//			new XEditDialog.Builder(this)
//			.setTitle(R.string.xeditdialog_title)
//			.setPositiveButton(android.R.string.ok, new OnClickListener() {
//				
//				@Override
//				public void onClick(View v) {
//					Log.d(TAG, "tag="+v.getTag());
//					//加好友
//					if(currUserInfo!=null)
//						Requester3.addFriend(handler, currUserInfo.userid, v.getTag().toString());
//				}
//			})
//			.setNegativeButton(android.R.string.cancel, null)
//			.create().show();
//			break;
//		case R.id.btn_subscribe://订阅
//			if(popupWindow.isShowing()){
//				popupWindow.dismiss();
//			}
//			if(getCurrZoneLayout().isAttention()){
//				//取消关注
//				if(currUserInfo!=null)
//					Requester3.cancelAttention(handler, currUserInfo.userid, "1");
//			}else{
//				//加关注
//				if(currUserInfo!=null)
//					Requester3.attention(handler, currUserInfo.userid);
//			}
//			break;
		default:
			break;
		}
		
	}
	
	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		if(screenMode == ALBUMS_MODE) {
			super.onBackPressed();
		} else if(screenMode == MAP_MODE) {
			pagerContainer.setVisibility(View.VISIBLE);
			fl_activity_homepage_otherdiary_map.setVisibility(View.GONE);
			//2014-4-8
//			HashMap<String, String> hm = new HashMap<String, String>();
//			hm.put("label", currUserInfo.userid);
//			CmmobiClickAgentWrapper.onEventDuration(OtherZoneActivity.this, "foot_print", currUserInfo.userid, delta);
//			Log.d("==WR==", "他人足迹埋点【userid:" + currUserInfo.userid + "】");
			initUserName();
			screenMode = ALBUMS_MODE;
			menu.setVisibility(View.VISIBLE);
		}
	}
	
	private PopupWindow mPopupWindow;
	private void showMenu(){
		View view = inflater.inflate(R.layout.activity_otherzone_menu ,
				null);
		mPopupWindow = new PopupWindow(view, LayoutParams.FILL_PARENT,
				LayoutParams.FILL_PARENT, true);
		mPopupWindow.setBackgroundDrawable(getResources().getDrawable(
				R.color.transparent));
		view.findViewById(R.id.btn_more_menu_cancel).setOnClickListener(this);
		view.findViewById(R.id.btn_more_menu_bakname).setOnClickListener(this);
		mPopupWindow.showAtLocation(findViewById(R.id.fl_container), Gravity.BOTTOM, 0, 0);
	}
	
	private zoneLayout getCurrZoneLayout(){
		return listViews.get(currIndex);
	}

	/*private void initListView() {
		listViews.clear();
		for (int i = 0; i < 50; i++) {
			zoneLayout zoneLayout = new zoneLayout(this);
			listViews.add(zoneLayout);
		}
		myPagerAdapter.notifyDataSetChanged();
	}*/

	@Override
	public void onPageScrollStateChanged(int arg0) {}
	@Override
	public void onPageScrolled(int arg0, float arg1, int arg2) {}
	@Override
	public void onPageSelected(int position) {
		currIndex=position;
		currUserInfo=wrapUsers.get(currIndex);
		initUserName();
		if(getCurrZoneLayout().isNeedRefresh()){
			Prompt.showProgressDialog(this);
			Requester3.homePage(handler, currUserInfo.userid, "", "", "", "", "", "");
		}
	}

	class zoneLayout extends LinearLayout implements
			OnRefreshListener2<ListView>, OnHeaderScrolledListener<ListView>, OnPullEventListener<ListView> {
		private PullToRefreshListView xlvMyZone;
		private ListView lvMyZoneList;
		private myAdapter myAdapter;
		private ArrayList<MyZoneItem> myZoneItems = new ArrayList<MyZoneItem>();
		private LayoutInflater inflater;

		public zoneLayout(Context context) {
			super(context);
			inflater = LayoutInflater.from(context);
			setupViews();
		}

		private void setupViews() {
			View view = inflater.inflate(R.layout.fragment_my_zone, null);
			addView(view);
			ViewGroup.LayoutParams params=view.getLayoutParams();
			params.height=LayoutParams.MATCH_PARENT;
			params.width=LayoutParams.MATCH_PARENT;
			view.setLayoutParams(params);
			xlvMyZone = (PullToRefreshListView) findViewById(R.id.xlv_my_zone);
			xlvMyZone.setShowIndicator(false);
			xlvMyZone.setOnRefreshListener(this);
			xlvMyZone.setOnHeaderScrolledListener(this);
			xlvMyZone.setOnPullEventListener(this);
			//xlvMyZone.setOverScrollMode(View.OVER_SCROLL_ALWAYS);
			xlvMyZone.setPullDownSilenceEnabled(true);
			xlvMyZone.setScrollEmptyView(false);
			xlvMyZone.setPullToRefreshOverScrollEnabled(false);
	
			lvMyZoneList = xlvMyZone.getRefreshableView();
			lvMyZoneList.setStackFromBottom(false);
			myAdapter = new myAdapter();
//			myZoneItems.add(null); 
//			myZoneItems.add(null);
//			myZoneItems.add(null);
//			myZoneItems.add(null);
//			myZoneItems.add(null);
//			myZoneItems.add(null);
			lvMyZoneList.setAdapter(myAdapter);
		}
		
		public PullToRefreshListView getRefreshListView() {
			return xlvMyZone;
		}
		//当滑动到此页时，判断是否需要刷新页面 true-需要刷新 false-不需要刷新
		public boolean isNeedRefresh(){
			return myZoneItems.size()<2;
		}
		
		public boolean isBlackList(){
			if(userInfo!=null)
				return "1".equals(userInfo.isblacklist);
			return false;
		}
		
		public boolean isFriend(){
			WrapUser user=accountInfo.friendsListName.findUserByUserid(userInfo.userid);
			if(user!=null)return true;
			return false;
		}
		
		public boolean isAttention(){
			if(userInfo!=null)
				return "1".equals(userInfo.isattention);
			return false;
		}
		
		public String getUserName(){
			if(userInfo!=null&&!TextUtils.isEmpty(userInfo.backname))
				return userInfo.backname;
			return userInfo.nickname;
		}
		
		private UserInfo userInfo;
		public void initUserInfo(UserInfo userInfo){
			myZoneItems.clear();
			myZoneItems.add(userInfo);
			myAdapter.notifyDataSetChanged();
			this.userInfo=userInfo;
		}
		
		private String lastDate="";
		public String getLastDate(){
			return lastDate;
		}
		
		public String last_time="";
		
		public void addDiaryShareItem(ArrayList<DiaryShareItem> shareItems){
			myZoneItems.addAll(shareItems);
			updateMapDiaries();
		}
		
		public UserInfo getUserInfo(){
			return userInfo;
		}
		
		public void refreshComplete(){
			xlvMyZone.onRefreshComplete();
		}
		
		public void stopLoading(){
			myAdapter.stopLoading();
		}
		
		public void clear(){
			myZoneItems.clear();
		}
		
		public void updateMapDiaries() {
			mapdiary.clear();
			mapdiarylist.clear();
			for(MyZoneItem item : myZoneItems) {
				if(item instanceof DiaryShareItem) {
					MyDiary[] diaries = ((DiaryShareItem) item).diaries;
					MyDiaryList diarylist = ((DiaryShareItem) item).diaryGroup;
					// 不是组日记（单篇日记）
					if("0".equals(diarylist.isgroup)) {
						mapdiarylist.add(diarylist);
						for(MyDiary d : diaries) {
							if((d.diaryid.equals(diarylist.diaryid) ||
									d.diaryid.equals(diarylist.contain)) &&
									!d.isDuplicated()) {
								// 找到了
								mapdiary.add(d);
								break;
							}
						}
					}
				}
			}
		}
		
		public void updateAttention(boolean isAttention){
			if(isAttention){
				userInfo.isattention="1";
			}else{
				userInfo.isattention="0";
			}
			myAdapter.notifyDataSetChanged();
		}
		
		public void updateBlacklist(boolean isBlacklist){
			if(isBlacklist){
				userInfo.isblacklist="1";
			}else{
				userInfo.isblacklist="0";
			}
			myAdapter.notifyDataSetChanged();
		}
		
		public void update(){
			myAdapter.notifyDataSetChanged();
		}

		@Override
		public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
			myAdapter.setRefreshing(true);
			myAdapter.startLoading();
			/*handler.post(new Runnable() {
				
				@Override
				public void run() {
					refreshComplete();
					
				}
			});*/
			Requester3.homePage(handler,currUserInfo.userid, "", "", "", "", "", "");
		}

		@Override
		public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
			Requester3.requestMyDiary(handler, userInfo.userid, last_time, "2", "", "");
			handler.post(new Runnable() {
				
				@Override
				public void run() {
					refreshComplete();
					
				}
			});
		}

		private int layoutWidth;
		private int margin=5;
		private DisplayMetrics dm = new DisplayMetrics();
		class myAdapter extends BaseAdapter implements OnClickListener {
			
			
			private DisplayImageOptions options;
			private DisplayImageOptions bgOptions;
			protected ImageLoader imageLoader;
			private String userID;
			private int headHeight;
			private String lastBackgroundUrl;
			private boolean isActiveRefreshing = false;

			public myAdapter(){
				userID= ActiveAccount.getInstance(ZApplication.getInstance()).getUID();
				getWindowManager().getDefaultDisplay()
				.getMetrics(dm);
				layoutWidth=(dm.widthPixels-margin*4)/3;
				imageLoader = ImageLoader.getInstance();
				//if(!imageLoader.isInited())
				//	imageLoader.init(ImageLoaderConfiguration.createDefault(OtherZoneActivity.this));
				int roundpx = dm.widthPixels * 3 / 640;
				
				options = new DisplayImageOptions.Builder()
//				.showStubImage(R.drawable.kongjian_morentouxiang)
				.showImageForEmptyUri(R.drawable.kongjian_morentouxiang)
				.showImageOnFail(R.drawable.kongjian_morentouxiang)
				.cacheInMemory(true)
				.cacheOnDisc(true)
				.displayer(new RoundedBitmapDisplayer(roundpx <= 0 ? 3 : roundpx))
//				.displayer(new CircularBitmapDisplayer())
				.build();
				
				headHeight=BitmapFactory.decodeResource(getContext().getResources(), R.drawable.kongjian_morentouxiang).getHeight();
				
				bgOptions = new DisplayImageOptions.Builder()
				.showStubImage(/*R.drawable.moren_kongjianfengmian*/0)
				/*.showImageForEmptyUri(R.drawable.moren_kongjianfengmian)
				.showImageOnFail(R.drawable.moren_kongjianfengmian)*/
				.cacheInMemory(true)
				.cacheOnDisc(true)
				.displayer(new SimpleBitmapDisplayer())
//				.displayer(new CircularBitmapDisplayer())
				.build();
			}
			
			public void setRefreshing(boolean isrefresh) {
				isActiveRefreshing = isrefresh;
			}
			
			@Override
			public int getCount() {
				return myZoneItems.size();
			}

			@Override
			public Object getItem(int position) {
				return myZoneItems.get(position);
			}

			@Override
			public long getItemId(int position) {
				return position;
			}

//			private View vSendMsg;
//			private View vAttention;
//			private View vBlacklist;
//			private View vExtend;
//			private View vExtendContent;
			@Override
			public View getView(int position, View convertView, ViewGroup parent) {
				if (0 == position) {// 空间背景布局
					viewHolderBG holderBG;
					if(null==convertView||null==convertView.getTag(R.layout.include_other_zone_background)){
						holderBG=new viewHolderBG();
						convertView = inflater.inflate(
								R.layout.include_other_zone_background, null);
						convertView.setTag(R.layout.include_other_zone_background, holderBG);
						holderBG.llUserinfo=convertView.findViewById(R.id.ll_userinfo);
						holderBG.ivBackground=(ImageView) convertView.findViewById(R.id.iv_background);
						holderBG.ivHeadBackground = (ImageView) convertView.findViewById(R.id.iv_head_background);
//						holderBG.ivAttention=(ImageView) convertView.findViewById(R.id.iv_btn_attention);
//						holderBG.ivBackname=(ImageView) convertView.findViewById(R.id.iv_back_name);
//						holderBG.ivBlacklist=(ImageView) convertView.findViewById(R.id.iv_btn_blacklist);
//						holderBG.ivFeature=(ImageView) convertView.findViewById(R.id.iv_btn_feature);
						holderBG.ivFootmark=(ImageView) convertView.findViewById(R.id.iv_footmark);
						holderBG.ivHead=(ImageView) convertView.findViewById(R.id.iv_head);
						holderBG.ivSendMsg=(ImageView) convertView.findViewById(R.id.iv_btn_send_msg);
						holderBG.vVshareNum=convertView.findViewById(R.id.rl_vshare_num);
						holderBG.ivVshareNum=(ImageView) convertView.findViewById(R.id.iv_vshare_num);
						holderBG.tvVshareNum=(TextView) convertView.findViewById(R.id.tv_share_num);
						holderBG.ivSex=(ImageView) convertView.findViewById(R.id.iv_sex);
						holderBG.tvNickname=(TextView) convertView.findViewById(R.id.tv_nickname);
						holderBG.tvSignature=(TextView) convertView.findViewById(R.id.tv_signature);
						holderBG.tvToday=(TextView) convertView.findViewById(R.id.tv_today);
						holderBG.llSignature = (LinearLayout) convertView.findViewById(R.id.ll_signature);
						holderBG.ivLoading=(ImageView) convertView.findViewById(R.id.iv_waiting);
//						holderBG.llDiaryItem = (LinearLayout) convertView.findViewById(R.id.ll_diary_item);
						//holderBG.vExtend=(ImageView) convertView.findViewById(R.id.iv_extend);
//						holderBG.vExtendClose=(ImageView) convertView.findViewById(R.id.iv_extend_1);
//						holderBG.vExtendContent=(View) convertView.findViewById(R.id.ll_extend_content);
						android.view.ViewGroup.LayoutParams params=holderBG.ivHeadBackground.getLayoutParams();
						params.height=dm.widthPixels * 15 / 64;
						params.width=params.height;
						holderBG.ivHeadBackground.setLayoutParams(params);
					}else{
						holderBG=(viewHolderBG) convertView.getTag(R.layout.include_other_zone_background);
					}
					
					int nicknamesize = DisplayUtil.px2sp(OtherZoneActivity.this, dm.widthPixels * 34 / 640);
					int signsize = DisplayUtil.px2sp(OtherZoneActivity.this, dm.widthPixels * 24 / 640);
					
					holderBG.tvNickname.setTextSize(nicknamesize);
					holderBG.tvSignature.setTextSize(signsize);
//					holderBG.ivFeature.setOnClickListener(this);
//					holderBG.ivAttention.setOnClickListener(this);
//					holderBG.ivBlacklist.setOnClickListener(this);
//					holderBG.vExtend.setOnClickListener(this);
//					holderBG.vExtendClose.setOnClickListener(this);
					holderBG.ivSendMsg.setOnClickListener(this);
					holderBG.ivVshareNum.setOnClickListener(this);
					if(isServiceUser)
						holderBG.vVshareNum.setVisibility(View.INVISIBLE);
					holderBG.ivHead.setOnClickListener(this);
					holderBG.ivFootmark.setOnClickListener(this);
//					holderBG.ivBackname.setOnClickListener(this);
					
					ViewGroup.LayoutParams backgroundParams=holderBG.ivBackground.getLayoutParams();
					if(h < 0) {
						h = /*backgroundParams.height*/dm.widthPixels * 372 / 640;
//						Log.d(TAG, "gBackgroundParams is given value = " + h);
					}
					backgroundParams.width=dm.widthPixels;
					backgroundParams.height=(int)(backgroundParams.width*0.6f);
					if(currentHeight > 0) {
						backgroundParams.height = currentHeight;
					}
					holderBG.ivBackground.setLayoutParams(backgroundParams);
					android.widget.RelativeLayout.LayoutParams userinfoParams=(android.widget.RelativeLayout.LayoutParams) holderBG.llUserinfo.getLayoutParams();
//					userinfoParams.topMargin=(int)(backgroundParams.height-(headHeight*0.74f));
					userinfoParams.topMargin = backgroundParams.height - (int)(dm.widthPixels * 114 / 640);
					holderBG.llUserinfo.setLayoutParams(userinfoParams);
					
					
					
					MyZoneItem userinfo=myZoneItems.get(position);
					if(userinfo instanceof UserInfo){
						setUserInfo((UserInfo)userinfo,holderBG);
						final UserInfo otherInfo = (UserInfo)userinfo;
						final viewHolderBG holder = holderBG;
						ViewTreeObserver vto2 = holderBG.ivHead.getViewTreeObserver();   
						vto2.addOnGlobalLayoutListener(new OnGlobalLayoutListener() { 
							@Override   
							public void onGlobalLayout() {
								if(userInfo.headUrl!=null)
									imageLoader.displayImageEx(otherInfo.headUrl, holder.ivHead, options, null, userID, 1);
							}   
						});
						
						if(myZoneItems.size()>1){
							MyZoneItem myZoneItem=myZoneItems.get(1);
							if(myZoneItem instanceof DiaryShareItem){
								if("今天".equals(((DiaryShareItem) myZoneItem).strDate)){
									holderBG.tvToday.setVisibility(View.GONE);
								}else{
//									android.widget.LinearLayout.LayoutParams itemlp=(android.widget.LinearLayout.LayoutParams) holderBG.llDiaryItem.getLayoutParams();
//									if(itemlp != null) {
//										itemlp.topMargin=0;
//										holderBG.llDiaryItem.setLayoutParams(itemlp);
//									}
									
									MyDiaryList myDiaryList =((DiaryShareItem) myZoneItem).diaryGroup;
									if(DateUtils.isNum(myDiaryList.create_time)){
										long createTime=Long.parseLong(myDiaryList.create_time);
										long dtTime=System.currentTimeMillis()-createTime;
										long days=dtTime/(3600*1000*24);
										if(days<14){//超过一个星期
											holderBG.tvToday.setVisibility(View.VISIBLE);
											holderBG.tvToday.setHint(R.string.other_zone_hint_2);
										}else{//超过两个星期
											holderBG.tvToday.setVisibility(View.VISIBLE);
											holderBG.tvToday.setHint(R.string.other_zone_hint_3);
										}
									}
								}
							}
						}else{
							holderBG.tvToday.setVisibility(View.VISIBLE);
							holderBG.tvToday.setHint(R.string.other_zone_hint_1);
						}
					}
				} else {
					viewHolder holder;
					if(null==convertView||null==convertView.getTag()){
						holder=new viewHolder();
						convertView = inflater.inflate(
								R.layout.include_other_zone_item, null);
						holder.tvDate=(TextView) convertView.findViewById(R.id.tv_date);
						holder.tvGap=(TextView) convertView.findViewById(R.id.tv_gap);
						holder.ctvDiary=(ContentThumbnailView) convertView.findViewById(R.id.ctv_diary);
						holder.container=convertView.findViewById(R.id.ll_container);
						holder.tvShareContent=(TextView) convertView.findViewById(R.id.tv_share_content);
						ViewGroup.LayoutParams params=holder.ctvDiary.getLayoutParams();
						params.height=layoutWidth;
						params.width=layoutWidth;
						holder.ctvDiary.setLayoutParams(params);
						holder.ctvDiary.setPadding(margin, margin, margin, margin);
						convertView.setTag(holder);
					}else{
						holder=(viewHolder) convertView.getTag();
					}
					
					android.view.ViewGroup.LayoutParams datelp = holder.tvDate.getLayoutParams();
					datelp.width = dm.widthPixels * 143 / 640;
					holder.tvDate.setLayoutParams(datelp);
					
					MyZoneItem myZoneItem=myZoneItems.get(position);
					if(myZoneItem instanceof DiaryShareItem){
						holder.container.setOnClickListener(this);
						holder.container.setTag(R.id.ll_container,myZoneItem);
						if("今天".equals(((DiaryShareItem)myZoneItem).strDate)){
							holder.tvDate.setVisibility(View.VISIBLE);
							holder.tvDate.setText(null);
							holder.tvGap.setVisibility(View.GONE);
						}else{
							if(position-1>0){
								MyZoneItem last=myZoneItems.get(position-1);
								if(last instanceof DiaryShareItem){
									if(!((DiaryShareItem) last).strDate.equals(((DiaryShareItem)myZoneItem).strDate)){
										holder.tvDate.setVisibility(View.VISIBLE);
//										holder.tvDate.setText(((DiaryShareItem)myZoneItem).strDate);
										holder.tvDate.setText(((DiaryShareItem)myZoneItem).textStyle);
										holder.tvGap.setVisibility(View.VISIBLE);
									}else{
										holder.tvDate.setVisibility(View.VISIBLE);
										holder.tvDate.setText(null);
										holder.tvGap.setVisibility(View.GONE);
									}
								}
							}else{
								holder.tvDate.setVisibility(View.VISIBLE);
//								holder.tvDate.setText(((DiaryShareItem)myZoneItem).strDate);
								holder.tvDate.setText(((DiaryShareItem)myZoneItem).textStyle);
								holder.tvGap.setVisibility(View.VISIBLE);
							}
						}
						setShareDiaryInfo((DiaryShareItem)myZoneItem,holder);
					}
				}
				return convertView;
			}
			
			private String lastDate="";
			private void setShareDiaryInfo(DiaryShareItem shareItem, viewHolder holder){
//				if(TextUtils.isEmpty(shareItem.strDate)){
//					holder.tvDate.setVisibility(View.GONE);
//				}else{
//					holder.tvDate.setVisibility(View.VISIBLE);
//					holder.tvDate.setText(shareItem.strDate);
//				}
				
//				holder.tvShareContent.setText(shareItem.shareContent);
				FriendsExpressionView.replacedExpressions(shareItem.shareContent, holder.tvShareContent);
				holder.ctvDiary.setContentDiaries("0", shareItem.diaries);
			}
			
			//设置用户信息
			private void setUserInfo(UserInfo userInfo, viewHolderBG holderBG){
				gHolderUserInfo = holderBG;
				if(userInfo.headUrl!=null)
					imageLoader.displayImageEx(userInfo.headUrl, holderBG.ivHead, options, null, userID, 1);
				if(needRefreshBg(userInfo)) {
					imageLoader.displayImageEx(userInfo.backgroundUrl, holderBG.ivBackground, bgOptions, null, userID, 1);
					lastBackgroundUrl = userInfo.backgroundUrl;
				}
				isActiveRefreshing = false;
				if(userInfo.signature!=null && !userInfo.signature.isEmpty()){
					holderBG.llSignature.setVisibility(View.VISIBLE);
					FriendsExpressionView.replacedExpressions(userInfo.signature, holderBG.tvSignature);
				}else{
					holderBG.llSignature.setVisibility(View.GONE);
					holderBG.tvSignature.setText(null);
				}
//				holderBG.tvSignature.setText("今天太起饭疯掉了开始就范德萨佛挡杀佛飞是非得失法定是第三方是否神顶峰是否是否是否是发送发送到是发送电风扇发送电风扇发送到收到发送到发送对方收到发送到发送到发送到发生范德萨范德萨发送对方收到发送到发是非得失发送到发送电风扇的发生地方");
				if(!TextUtils.isEmpty(userInfo.backname)){
					FriendsExpressionView.replacedExpressions(userInfo.backname, holderBG.tvNickname);
				}else if(!TextUtils.isEmpty(userInfo.nickname)) {
					FriendsExpressionView.replacedExpressions(userInfo.nickname, holderBG.tvNickname);
				}else if(!TextUtils.isEmpty(currUserInfo.telname)){
					holderBG.tvNickname.setText(currUserInfo.telname);
				}else{
					holderBG.tvNickname.setText(null);
				}
				if("0".equals(userInfo.sex)){
					holderBG.ivSex.setVisibility(VISIBLE);
					holderBG.ivSex.setImageResource(R.drawable.nan);
				}else if("1".equals(userInfo.sex)){
					holderBG.ivSex.setVisibility(VISIBLE);
					holderBG.ivSex.setImageResource(R.drawable.nv);
				}else{
					holderBG.ivSex.setVisibility(View.GONE);
				}
				holderBG.tvVshareNum.setText(getString(R.string.otherzone_vsharenum, (TextUtils.isEmpty(userInfo.misharecount))?"0":userInfo.misharecount));
//				if(getCurrZoneLayout().isFriend()){
//					holderBG.ivAttention.setImageResource(R.drawable.btn_attention);
//				}else{
//					holderBG.ivAttention.setImageResource(R.drawable.btn_add_attention);
//				}
//				if("1".equals(userInfo.isattention)){//已关注
//					holderBG.ivAttention.setImageResource(R.drawable.btn_attention);
//				}else{//未关注
//					holderBG.ivAttention.setImageResource(R.drawable.btn_add_attention);
//				}
//				if("1".equals(userInfo.isblacklist)){//黑名单
//					holderBG.ivBlacklist.setImageResource(R.drawable.btn_add_blacklist);
//				}else{//不是我的黑名单
//					holderBG.ivBlacklist.setImageResource(R.drawable.btn_blacklist);
//				}
//				
//				if(isShowMarkNameBtn){
//					holderBG.ivBackname.setVisibility(View.VISIBLE);
//					holderBG.vExtendContent.setBackgroundResource(R.drawable.chouti_other_2);
//				}else{
//					holderBG.vExtendContent.setBackgroundResource(R.drawable.chouti_other_2_small);
//					holderBG.ivBackname.setVisibility(View.GONE);
//				}
			}

			private boolean needRefreshBg(UserInfo userInfo) {
				boolean need = false;
				if(userInfo.backgroundUrl!=null) {
					//主动刷新
					if(isActiveRefreshing) {
						//url和上次一样
						if(lastBackgroundUrl != null && lastBackgroundUrl.equalsIgnoreCase(userInfo.backgroundUrl)) {
//							ZToast.showLong("url和上次一样，不刷新！");
						} else {
							need = true;
						}
					} else {
						need = true;
					}
				}
				return need;
			}
			
			private viewHolderBG gHolderUserInfo = null;
			private int h = -1, currentHeight = -1;
			public void enlargeBackgroudView(int value) {
//				Log.d(TAG, "enlargeBackgroudView gBackgroundParams height value " + value + ";h = " + h);
				
				float delta = Math.abs(value);
				
				if (null != gHolderUserInfo && null != gHolderUserInfo.ivBackground) {
					android.view.ViewGroup.LayoutParams backgroundParams=gHolderUserInfo.ivBackground.getLayoutParams();
					currentHeight = (int) (h + delta * 2);
					int critical = dm.heightPixels * 16 / 25;
					currentHeight = currentHeight > critical ? critical : currentHeight;
					backgroundParams.height = currentHeight;
//					Log.d(TAG, "backgroundParams.height value " + backgroundParams.height);
					gHolderUserInfo.ivBackground.setLayoutParams(backgroundParams);
					
					android.widget.RelativeLayout.LayoutParams userinfoParams=(android.widget.RelativeLayout.LayoutParams) gHolderUserInfo.llUserinfo.getLayoutParams();
					userinfoParams.topMargin = backgroundParams.height - (int)(dm.widthPixels * 114 / 640);
					gHolderUserInfo.llUserinfo.setLayoutParams(userinfoParams);
				}
			}
			
			public void startLoading() {
				Log.d(TAG, "startLoading");
				if(gHolderUserInfo != null && gHolderUserInfo.ivLoading != null) {
					gHolderUserInfo.ivLoading.setVisibility(View.VISIBLE);
					gHolderUserInfo.ivLoading.startAnimation(waitanim);
				}
			}
			
			public void stopLoading() {
				Log.d(TAG, "stopLoading");
				if(gHolderUserInfo != null && gHolderUserInfo.ivLoading != null) {
					gHolderUserInfo.ivLoading.clearAnimation();
					gHolderUserInfo.ivLoading.setVisibility(View.GONE);
				}
			}
			
			@Override
			public void onClick(View v) {
				switch (v.getId()) {
				case R.id.ll_container:
					if(v.getTag(R.id.ll_container)!=null){
						DiaryShareItem shareItem=(DiaryShareItem) v.getTag(R.id.ll_container);
						
						String groupUUID=shareItem.diaryGroup.diaryuuid;
						ArrayList<MyDiaryList> diaryGroups=(ArrayList<MyDiaryList>) getDiaryGroup().clone();
//						ArrayList<MyDiary> diaries=(ArrayList<MyDiary>) getDiaries().clone();
						DiaryManager.getInstance().setDetailDiaryList(diaryGroups, 2);
						DiaryManager.getInstance().setDetailDiary(diaryList);
						
						Intent intent = new Intent(OtherZoneActivity.this, DiaryPreviewActivity.class);
						intent.putExtra(DiaryPreviewActivity.INTENT_ACTION_DIARY_UUID, groupUUID);
						OtherZoneActivity.this.startActivity(intent);
					}
					break;
//				case R.id.iv_back_name:{
//					zoneLayout layout=getCurrZoneLayout();
//					String backName=layout.getUserInfo().backname;
//					String nickname=layout.getUserInfo().nickname;
//					String userid=layout.getUserInfo().userid;
//					Intent intent=new Intent(OtherZoneActivity.this,BacknameActivity.class);
//					if(!TextUtils.isEmpty(backName)){
//						intent.putExtra("backname", backName);
//					}else{
//						intent.putExtra("backname", nickname);
//					}
//					intent.putExtra("otherUserid", userid);
//					startActivityForResult(intent, REQUESTCODE);
//					break;}
				case R.id.iv_footmark:
					//进入他人足迹，埋点
					//2014-4-8
					Log.d("==WR==", "他人足迹埋点【userid:" + userInfo.userid + "】");
					CmmobiClickAgentWrapper.onEvent(OtherZoneActivity.this, "foot_print", userInfo.userid);
//					start_time = SystemClock.elapsedRealtime();
//					HashMap<String, String> hm = new HashMap<String, String>();
//					hm.put("label", userInfo.userid);
//					CmmobiClickAgentWrapper.onEventDuration(OtherZoneActivity.this, "foot_print", hm, SystemClock.elapsedRealtime());
//					CmmobiClickAgentWrapper.onEventBegin(OtherZoneActivity.this, "foot_print");
					pagerContainer.setVisibility(View.INVISIBLE);
					fl_activity_homepage_otherdiary_map.setVisibility(View.VISIBLE);
					tvTitle.setText("足迹");
					screenMode = MAP_MODE;
					menu.setVisibility(View.GONE);
					if (handler != null) {
						handler.sendEmptyMessage(HANDLER_FLAG_INIT_VIEW);
					}
					Log.d(TAG, "iv_footmark");
					break;
				case R.id.iv_head:
					Log.d(TAG, "iv_head");
					startActivity(new Intent(OtherZoneActivity.this,SettingPortraitShowActivity.class).putExtra("imageUrl", currUserInfo.headimageurl));
					overridePendingTransition(R.anim.zoomin, R.anim.del_zoomout);
					break;
//				case R.id.iv_extend_1:
//					vExtend.setVisibility(View.VISIBLE);
//					vExtendContent.setVisibility(View.GONE);
//					break;
//				case R.id.iv_extend:
//					vExtend.setVisibility(View.GONE);
//					vExtendContent.setVisibility(View.VISIBLE);
//					break;
//				case R.id.iv_btn_feature:
//					if(null==v.getTag()){//显示
//						v.setTag("");
//						((ImageView)v).setImageResource(R.drawable.btn_feature_2);
//						vSendMsg.setVisibility(View.VISIBLE);
//						vAttention.setVisibility(View.VISIBLE);
//						vBlacklist.setVisibility(View.VISIBLE);
//					}else{//隐藏
//						v.setTag(null);
//						((ImageView)v).setImageResource(R.drawable.btn_feature_1);
//						vSendMsg.setVisibility(View.GONE);
//						vAttention.setVisibility(View.GONE);
//						vBlacklist.setVisibility(View.GONE);
//					}
//					break;
				case R.id.iv_btn_send_msg:{
					Log.d(TAG, "iv_btn_send_msg");
					if(currUserInfo!=null){
						Intent intent=new Intent(OtherZoneActivity.this,FriendsSessionPrivateMessageActivity.class);
						intent.putExtra("wrapuser", currUserInfo.toString());
						startActivity(intent);
					}
					break;}
				case R.id.iv_vshare_num:
					Log.d(TAG, "iv_vshare_num");
					//进入我和他的微享页面
					Intent intent=new Intent(OtherZoneActivity.this,OtherZoneVshareActivity.class);
					intent.putExtra("wrapuser", currUserInfo.toString());
					startActivity(intent);
					break;
//				case R.id.iv_btn_attention:
//					Log.d(TAG, "iv_btn_attention");
//					if(getCurrZoneLayout().isBlackList())return;
//					//好友关系时：解除好友，同时取消订阅（不弹提示框）
//					//非好友关系时：弹提示框 
//					if(getCurrZoneLayout().isFriend()){
//						//删除好友
//						if(currUserInfo!=null){
//							final String user = currUserInfo.userid;
//							Prompt.Dialog(getContext(), true, "提示", "确认删除好友？", new DialogInterface.OnClickListener() {
//								
//								@Override
//								public void onClick(DialogInterface dialog, int which) {
//									// TODO Auto-generated method stub
//									Prompt.showProgressDialog(getContext());
//									Requester3.deleteFriendRequest(handler, user);
//								}
//							});
//							
//						}
//					}else{
//						showPopupWindow();
//					}
////					if(getCurrZoneLayout().isAttention()){
////						//取消关注
////						Requester3.cancelAttention(handler, currUserInfo.userid, "1");
////					}else{
////						//加关注
////						Requester3.attention(handler, currUserInfo.userid);
////					}
//					break;
//				case R.id.iv_btn_blacklist:
//					Log.d(TAG, "iv_btn_blacklist");
//					if(currUserInfo!=null){
//						if(getCurrZoneLayout().isBlackList()){
//							//取消黑名单
//							Prompt.Dialog(getContext(), true, "提示", "确认从黑名单删除？", new DialogInterface.OnClickListener() {
//								
//								@Override
//								public void onClick(DialogInterface dialog, int which) {
//									// TODO Auto-generated method stub
//									Prompt.showProgressDialog(getContext());
//									Requester3.operateBlacklist(handler, currUserInfo.userid, "2");
//								}
//							});
//						}else{
//							//加黑名单
//							Prompt.Dialog(getContext(), true, "提示", "确认加入黑名单？", new DialogInterface.OnClickListener() {
//								
//								@Override
//								public void onClick(DialogInterface dialog, int which) {
//									// TODO Auto-generated method stub
//									Requester3.operateBlacklist(handler, currUserInfo.userid, "1");
//								}
//							});
//						}
//					}
//					break;
				default:
					break;
				}
				
			}
			
			private ArrayList<MyDiaryList> getDiaryGroup(){
				ArrayList<MyDiaryList> diaryGroups=new ArrayList<MyDiaryList>();
				if(myZoneItems!=null){
					for(int i=0;i<myZoneItems.size();i++){
						MyZoneItem item=myZoneItems.get(i);
						if(item instanceof DiaryShareItem){
							diaryGroups.add(((DiaryShareItem) item).diaryGroup);
						}
					}
				}
				return diaryGroups;
				
			}
			
			private ArrayList<MyDiary> getDiaries(){
				ArrayList<MyDiary> diaries=new ArrayList<MyDiary>();
				if(myZoneItems!=null){
					for(int i=0;i<myZoneItems.size();i++){
						MyZoneItem item=myZoneItems.get(i);
						if(item instanceof DiaryShareItem){
							diaries.addAll(Arrays.asList(((DiaryShareItem) item).diaries));
						}
					}
				}
				return diaries;
				
			}
		}
		
		@Override
		public void onPullEvent(PullToRefreshBase<ListView> refreshView,
				State state, Mode direction) {
			// TODO Auto-generated method stub
			Log.d(TAG, "Current State: " + state.name() + ";Direction: " + direction.name());
			if(direction == Mode.PULL_FROM_START) {
				if(state == State.RESET) {
//					myAdapter.enlargeBackgroudView(0);
					myAdapter.setRefreshing(false);
				}
			}
		}

		@Override
		public void onHeaderScrolled(PullToRefreshBase<ListView> refreshView,
				int value, Mode direction, State state) {
			// TODO Auto-generated method stub
			
			if(direction == Mode.PULL_FROM_START) {
//				Log.d("xxx", "onHeaderScrolled value = " + value + "; State = " + state.name());
				
				if(state == State.REFRESHING) {
					myAdapter.enlargeBackgroudView(value);
//					myAdapter.enlargeBackgroudView(Math.max(Math.abs(value), 50));
					return;
				}
				if(value <= 0 || state == State.RESET) {
					myAdapter.enlargeBackgroudView(value);
					return;
				}
			}
		}
	}
	
	//更新备注名
	private void updateUserBackName(String newBackName){
		zoneLayout layout=getCurrZoneLayout();
		layout.getUserInfo().backname=newBackName;
		layout.update();
		if(currUserInfo!=null){
			currUserInfo.markname=newBackName;
			WrapUser wrapUser=accountInfo.friendsListName.findUserByUserid(currUserInfo.userid);
			if(wrapUser!=null)
				wrapUser=currUserInfo;
		}
		FriendsExpressionView.replacedExpressions(newBackName + "的空间", tvTitle);
	}
	
	private static final int REQUESTCODE=0x0010;
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if(REQUESTCODE==requestCode&&resultCode==RESULT_OK){
			String backName=data.getStringExtra("newbackname");
			updateUserBackName(backName);
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	static class viewHolder{
		TextView tvDate;
		TextView tvGap;//tv_gap
		ContentThumbnailView ctvDiary;
		TextView tvShareContent;
		View container;
	}
	
	static class viewHolderBG{
		View llUserinfo;//iv_background
		ImageView ivBackground, ivHeadBackground;//iv_background
//		ImageView ivFeature;//iv_btn_feature
		ImageView ivSendMsg;//iv_btn_send_msg
//		ImageView ivAttention;//iv_btn_attention
//		ImageView ivBlacklist;//iv_btn_blacklist
		ImageView ivHead;//iv_head
//		View vExtend;//iv_extend
//		View vExtendContent;//ll_extend_content
//		ImageView ivBackname;//iv_back_name
		ImageView ivFootmark;//iv_footmark
		View vExtendClose;//iv_extend_1
		TextView tvNickname;//tv_nickname
		ImageView ivSex;//iv_sex
		TextView tvSignature;//tv_signature
		TextView tvToday;//tv_today
		View vVshareNum;//
		ImageView ivVshareNum;//
		TextView tvVshareNum;//
		LinearLayout llSignature;
		ImageView ivLoading;
	}

	class MyPagerAdapter extends PagerAdapter {
		public List<zoneLayout> mListViews;

		public MyPagerAdapter(List<zoneLayout> mListViews) {
			this.mListViews = mListViews;
		}

		@Override
		public void destroyItem(View arg0, int arg1, Object arg2) {
			try {
				((ViewPager) arg0).removeView(mListViews.get(arg1));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		@Override
		public void finishUpdate(View arg0) {

		}

		@Override
		public int getCount() {
			return mListViews.size();
		}

		@Override
		public Object instantiateItem(View arg0, int arg1) {
			((ViewPager) arg0).addView(mListViews.get(arg1), 0);
			return mListViews.get(arg1);
		}

		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {
			return arg0 == (arg1);
		}

		@Override
		public void restoreState(Parcelable arg0, ClassLoader arg1) {
		}

		@Override
		public Parcelable saveState() {
			return null;
		}

		@Override
		public void startUpdate(View arg0) {
		}
	}

	/**
	 * 	已订阅：
			添加黑名单：点击黑名单按钮，添加黑名单，提示“已加入”同时在通讯录黑名单中显示；同时取消我对你的订阅关系
			解除黑名单：点击【解除】按钮，从通讯录列表移除，订阅关系不恢复
				未订阅：
			添加黑名单：点加黑名单按钮，添加黑名单，提示“已加入”同时在通讯录黑名单中显示
			解除黑名单：点击【解除】按钮，从通讯录列表移除，订阅关系不恢复
				已互粉：
			添加黑名单：点加黑名单按钮，添加黑名单，提示“已加入”同时在通讯录黑名单中显示；同时双方解除互粉关系
			解除黑名单：点击【解除】按钮，从通讯录列表移除，关注关系不恢复
			A将B加入黑名单.A不能加B为订阅.同时B也不能加A为订阅

	 */
	@Override
	public boolean handleMessage(Message msg) {
		switch (msg.what) {
//		case Requester3.RESPONSE_TYPE_ADD_FRIEND:{
//			Prompt.dimissProgressDialog();
//			addfriendResponse response=(addfriendResponse) msg.obj;
//			if(response!=null&&"0".equals(response.status)){
////				ContactManager friendsContactManager = accountInfo.friendsListName;
////				ContactManager attentionContactManager = accountInfo.attentionListName;
////				friendsContactManager.addMember(currUserInfo);
////				attentionContactManager.addMember(currUserInfo);
//				Prompt.Alert("已发送好友申请");
//				currUserInfo.isattention = "1";
//				accountInfo.attentionListName.addMember(currUserInfo);
//				accountInfo.attentionListTime.insertMember(currUserInfo);
//				Collections.sort(accountInfo.attentionListTime.getCache(), new ContactManager.SortByTChange());
//				//getCurrZoneLayout().updateAttention(true);
//			}
//			break;}
//		case Requester3.RESPONSE_TYPE_DELETE_FRIEND:{
//			Prompt.dimissProgressDialog();
//			deleteFriendResponse response=(deleteFriendResponse) msg.obj;
//			if(response!=null&&"0".equals(response.status)){
//				accountInfo.friendsListName.removeMember(currUserInfo.userid);
//				accountInfo.friendsListTime.removeMember(currUserInfo.userid);
//				getCurrZoneLayout().updateAttention(false);
//			}
//			break;}
		case Requester3.RESPONSE_TYPE_LIST_MY_DIARY:{
			listMyDiaryResponse response=(listMyDiaryResponse) msg.obj;
			if(response!=null&&"0".equals(response.status)){
				if(response.diaryids!=null&&response.diaries!=null&&response.diaries.length>0&&(currUserInfo!=null&&!currUserInfo.userid.equals(response.diaries[0].userid))){
					getCurrZoneLayout().refreshComplete();
					return false;//他人空间日记可能会和自己空间有冲突，用userid加以区分
				}
				setOtherZoneUserInfo(response);
			}
			getCurrZoneLayout().refreshComplete();
			break;
		}
		case Requester3.RESPONSE_TYPE_HOME:{
			Prompt.dimissProgressDialog();
			homeResponse response=(homeResponse) msg.obj;
			if(response!=null&&"0".equals(response.status)){
				if((currUserInfo!=null&&currUserInfo.userid.equals(response.userid))){
					setOtherZoneUserInfo(response);
				}else{
					Log.d(TAG, "userid error");
				}
			}
			getCurrZoneLayout().refreshComplete();
			getCurrZoneLayout().stopLoading();
			break;
		}
//		case Requester3.RESPONSE_TYPE_ATTENTION:{
//			Prompt.dimissProgressDialog();
//			attentionResponse response=(attentionResponse) msg.obj;
//			if(response!=null&&"0".equals(response.status)){
//				Prompt.Alert("已订阅");
//				//更新图标
//				getCurrZoneLayout().updateAttention(true);
//				//加入订阅列表
//				accountInfo.attentionListName.addMember(currUserInfo);
//				accountInfo.attentionListTime.insertMember(currUserInfo);
//				Collections.sort(accountInfo.attentionListTime.getCache(), new ContactManager.SortByTChange());
//			}
//			break;}
//		case Requester3.RESPONSE_TYPE_CANCEL_ATTENTION:{
//			Prompt.dimissProgressDialog();
//			cancelattentionResponse response=(cancelattentionResponse) msg.obj;
//			if(response!=null&&"0".equals(response.status)){
//				Prompt.Alert("已取消");
//				//更新图标
//				getCurrZoneLayout().updateAttention(false);
//				//移出订阅列表
//				accountInfo.attentionListName.removeMember(currUserInfo.userid);
//				accountInfo.attentionListTime.removeMember(currUserInfo.userid);
//			}
//			break;}
//		case Requester3.RESPONSE_TYPE_SET_BLACKLIST:{
//			Prompt.dimissProgressDialog();
//			operateblacklistResponse response=(operateblacklistResponse) msg.obj;
//			if(response!=null&&"0".equals(response.status)){
//				if(getCurrZoneLayout().isBlackList()){
//					Prompt.Alert("已移出");
//					//更新图标
//					getCurrZoneLayout().updateBlacklist(false);
//					//从黑名单列表移出
//					ContactManager blackListContactManager = accountInfo.blackList;
//					blackListContactManager.removeMember(currUserInfo.userid);
//				}else{
//					Prompt.Alert("已加入");
//					//更新图标
//					getCurrZoneLayout().updateBlacklist(true);
//					//加入黑名单列表
//					ContactManager blackListContactManager = accountInfo.blackList;
//					blackListContactManager.addMember(currUserInfo);
//					//从粉丝列表移出
//					//从关注列表移出
//					//从朋友列表移出
//					ContactManager friendsContactManager = accountInfo.friendsListName;
//					ContactManager fansContactManager = accountInfo.fansList;
//					ContactManager attentionContactManager = accountInfo.attentionListName;
//					fansContactManager.removeMember(currUserInfo.userid);
//					friendsContactManager.removeMember(currUserInfo.userid);
//					attentionContactManager.removeMember(currUserInfo.userid);
//					accountInfo.friendsListTime.removeMember(currUserInfo.userid);
//					accountInfo.attentionListTime.removeMember(currUserInfo.userid);
//					accountInfo.snsList.removeMember(currUserInfo.userid);
//					accountInfo.phoneUserList.removeMember(currUserInfo.userid);
//				}
//			}
//			break;
//		}
		
		case HANDLER_FLAG_INIT_VIEW:
			fl_activity_discover_map_tankuang.setVisibility(View.INVISIBLE);
			fl_bg_shadow.setVisibility(View.INVISIBLE);
			if(mapdiary==null ){
				break; //myLoc = new LocationData();
			}
//			mapdiary = simulateDiaries();
//				lv_nearby.reset();
			if (mapdiary != null && mapdiary.size() > 0 && MapItemHelper.needToShowFootmark(mapdiary)) {
				objs = MapItemHelper.showNearByItems(this, handler, getResources(), bmv_nearby, myLocationOverlay,  mapdiary, true);
				Log.e(TAG, "list_nearby_data size:" + mapdiary.size());
//					MyDiary[] its = (MyDiary[])(mActivity.list_nearby_data.toArray(new MyDiary[0]));
//					lv_nearby.loadMore(its);
			} else {
				bmv_nearby.getOverlays().clear();
				try {
					//中国地图
					GeoPoint gp_center = new GeoPoint((int)(35 * 1e6), (int)(104 * 1e6));
					bmv_nearby.getController().setZoom((float) 4.6);
					bmv_nearby.getController().setCenter(gp_center);
					bmv_nearby.getController().animateTo(gp_center);
				} catch (NullPointerException e) {
					Log.e(TAG, "Baidu Map Internal NullPointer");
					Toast.makeText(this, "百度地图缩放出错，请刷新重试！", Toast.LENGTH_LONG).show();
		    	}
			}
			break;
		case MyMapView.HANDLER_FLAG_MAPVIEW_CLICK:
			fl_activity_discover_map_tankuang.setVisibility(View.INVISIBLE);
			fl_bg_shadow.setVisibility(View.INVISIBLE);
			//lv_info.setVisibility(View.INVISIBLE);
			break;
			
		case MyMapView.HANDLER_FLAG_MAPVIEW_UPDATE:
			if(bmv_nearby != null) {
				try {
					Log.e(TAG, "handler update: level:" + bmv_nearby.getZoomLevel());
					objs = MapItemHelper.showNearByItems(this, handler, getResources(), bmv_nearby, myLocationOverlay, mapdiary, false);
				} catch (NullPointerException e) {
					Log.e(TAG, "Baidu Map Internal NullPointer");
				}
			}
			break;
		case MyItemizedOverlay.HANDLER_FLAG_MAP_ITEM_TAP:
			int index = (Integer) msg.obj;
			if(objs==null || objs[index]==null){
				break;
			}
			final Item it = objs[index];
			if(it.type==1){
				sortDiary(it.nearby_list);
				list_adapter = new MapNearbyListAdapter(this, handler,
						R.layout.row_list_diary_nearby, R.id.tv_row_list_diary_nick,
						it.nearby_list, fl_activity_discover_map_tankuang);
				lv_info.setCacheColorHint(0);
				lv_info.setAdapter(list_adapter);
				lv_info.setOnItemClickListener(new OnItemClickListener() {

					@Override
					public void onItemClick(AdapterView<?> parent, View view, int position,
							long id) {
						Object object = view.getTag();
						if(object instanceof MyDiary) {
							MyDiary tempDiary = (MyDiary) object;
							String diaryuuid=tempDiary.diaryuuid;
							/*
							ArrayList<MyDiaryList> diarylist = new ArrayList<MyDiaryList>();
							for(MyDiary diaryitem : it.nearby_list) {
								diarylist.add(DiaryManager.getInstance().findDiaryGroupByUUID(diaryitem.diaryuuid));
							}
							*/
							DiaryManager.getInstance().setDetailDiaryList(mapdiarylist, 2);
//							DiaryManager.getInstance().setDetailDiary(mapdiary);
							DiaryManager.getInstance().setDetailDiary(diaryList);
							startActivity(new Intent(ZApplication.getInstance(), DiaryPreviewActivity.class)
							.putExtra(DiaryPreviewActivity.INTENT_ACTION_DIARY_UUID, diaryuuid));
							fl_activity_discover_map_tankuang.setVisibility(View.INVISIBLE);
							fl_bg_shadow.setVisibility(View.INVISIBLE);
						}
					}
					
				});
				list_adapter.notifyDataSetChanged();
				fl_activity_discover_map_tankuang.setVisibility(View.VISIBLE);
				fl_bg_shadow.setVisibility(View.VISIBLE);
				//lv_info.setVisibility(View.VISIBLE);
			}
			break;
			
		default:
			break;
		}
		return false;
	}
	
	// 日记排序
	private void sortDiary(ArrayList<MyDiary> mydiaries) {
		if(mydiaries != null && mydiaries.size() > 0) {
			Collections.sort(mydiaries, new DiaryComparator());
		}
	}

	public class DiaryComparator implements Comparator<MyDiary> {
		public int compare(MyDiary arg0, MyDiary arg1) {
			try {
				if (Long.parseLong(arg0.shoottime) < Long
						.parseLong(arg1.shoottime)) {
					return 1;
				}
				if (Long.parseLong(arg0.shoottime) == Long
						.parseLong(arg1.shoottime)) {
					return 0;
				}
			} catch (NumberFormatException e) {
				e.printStackTrace();
			}
			return -1;
		}
	}
	
	private void setOtherZoneUserInfo(listMyDiaryResponse response){
		if(null==response)return;
		zoneLayout layout=getCurrZoneLayout();
		if(response.diaryids!=null&&response.diaryids.length>0){
			layout.last_time=response.last_diary_time;
			diaryList.addAll(Arrays.asList(response.diaries));
			ArrayList<DiaryShareItem> shareItems=getDiaryShareList(response.diaryids,response.diaries);
			layout.addDiaryShareItem(shareItems);
			PullToRefreshListView rlview = getCurrZoneLayout().getRefreshListView();
			if("1".equals(response.hasnextpage)) {
				if(rlview != null) {
					rlview.setNoMoreData(this, true);
				}
			} else {
				if(rlview != null) {
					rlview.setNoMoreData(this, false);
				}
			}
		}
		layout.update();
	}
	
	private ArrayList<MyDiary> diaryList=new ArrayList<MyDiary>();
	private void setOtherZoneUserInfo(homeResponse response){
		if(null==response)return;
		zoneLayout layout=getCurrZoneLayout();
		layout.clear();
		if(null==currUserInfo){
			currUserInfo=new WrapUser();
		}
		UserInfo userInfo=layout.getUserInfo();
		userInfo.backgroundUrl=response.background;
		userInfo.headUrl=response.headimageurl;
		userInfo.isattention=response.isattention;
		userInfo.isblacklist=response.isblacklist;
		userInfo.nickname=response.nickname;
		userInfo.sex=response.sex;
		userInfo.misharecount=response.misharecount;
		userInfo.signature=response.signature;
		userInfo.backname=currUserInfo.markname;
		
		currUserInfo.headimageurl=userInfo.headUrl;
		currUserInfo.markname=userInfo.backname;
		currUserInfo.nickname=userInfo.nickname;
		currUserInfo.sex=userInfo.sex;
		currUserInfo.signature=userInfo.signature;
		currUserInfo.userid=userInfo.userid;
		initUserName();
		layout.initUserInfo(userInfo);
		diaryList.clear();
		if(response.diaryids!=null&&response.diaryids.length>0){
			layout.last_time=response.last_diary_time;
			lastCreateTime="";
			diaryList.addAll(Arrays.asList(response.diaries));
			ArrayList<DiaryShareItem> shareItems=getDiaryShareList(response.diaryids,response.diaries);
			layout.addDiaryShareItem(shareItems);
			PullToRefreshListView rlview = getCurrZoneLayout().getRefreshListView();
			if("1".equals(response.hasnextpage)) {
				if(rlview != null) {
					rlview.setRefreshingLabel(getString(R.string.pull_to_refresh_refreshing_label), Mode.PULL_FROM_END);
					rlview.setReleaseLabel(getString(R.string.pull_to_refresh_footer_release_label), Mode.PULL_FROM_END);
				}
			} else {
				if(rlview != null) {
					rlview.setRefreshingLabel(getString(R.string.no_more_date), Mode.PULL_FROM_END);
					rlview.setReleaseLabel(getString(R.string.no_more_date), Mode.PULL_FROM_END);
				}
			}
		}
		layout.update();
	}
	
	private String lastCreateTime="";
	private ArrayList<DiaryShareItem> getDiaryShareList(MyDiaryList[] myDiaryLists,MyDiary[] diaries){
		if(null==myDiaryLists||null==diaries)return null;
		Date today=new Date();
		ArrayList<DiaryShareItem> shareItems=new ArrayList<DiaryShareItem>();
		for(int i=0;i<myDiaryLists.length;i++){
			DiaryShareItem shareItem=new DiaryShareItem();
			shareItem.diaryGroup=myDiaryLists[i];
			MyDiary[] myDiaries=getDiariesByDiaryids(myDiaryLists[i].diaryid,diaries);
			if(myDiaries!=null&&myDiaries.length>0)
				shareItem.shareContent=myDiaries[0].getAssistAttachTexTContent();
			shareItem.strDate=DiaryManager.getInstance().getMyZoneShowDate(myDiaryLists[i].update_time,today);
			shareItem.textStyle=DiaryManager.getInstance().getMyZoneShowDateStyle(lastCreateTime, myDiaryLists[i].update_time, today);
			if("1".equals(myDiaryLists[i].isgroup)){
				shareItem.diaries=getDiariesByDiaryids(myDiaryLists[i].contain,diaries);
			}else{
				shareItem.diaries=getDiariesByDiaryids(myDiaryLists[i].diaryid,diaries);
			}
			shareItems.add(shareItem);
			lastCreateTime=myDiaryLists[i].update_time;
		}
		return shareItems;
	}
	
	//
	private MyDiary[] getDiariesByDiaryids(String diaryids,MyDiary[] myDiaries){
		if(!TextUtils.isEmpty(diaryids)&&myDiaries!=null){
			ArrayList<MyDiary> diaries=new ArrayList<MyDiary>();
			String[] arrDiaryid=diaryids.split(",");
			for(int i=0;i<myDiaries.length;i++){
				for(int j=0;j<arrDiaryid.length;j++){
					if(myDiaries[i]!=null&&myDiaries[i].diaryid.equals(arrDiaryid[j]))
						diaries.add(myDiaries[i]);
				}
			}
			return diaries.toArray(new MyDiary[diaries.size()]);
		}
		return null;
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
	
	private ArrayList<MyDiary> simulateDiaries() {
		ArrayList<MyDiary> diaries = new ArrayList<MyDiary>();
    	for(int i = 0; i < loc.length; i++) {
    		MyDiary d = new MyDiary();
    		d.longitude_view  = loc[i][0];
    		d.latitude_view = loc[i][1];
    		d.sex = loc[i][2];
    		d.nickname = loc[i][3];
    		d.diaryid = loc[i][4];
//    		DiaryAttach da = new DiaryAttach();
    		diaries.add(d);
    	}
    	return diaries;
    }
	
	private void showPopupWindow(){
		View view = inflater.inflate(R.layout.activity_other_zone_menu ,
				null);
		popupWindow = new PopupWindow(view, LayoutParams.FILL_PARENT,
				LayoutParams.FILL_PARENT, true);
		popupWindow.setBackgroundDrawable(getResources().getDrawable(
				R.color.transparent));
		Button btnCancel=(Button) view.findViewById(R.id.btn_cancel);
		btnCancel.setOnClickListener(this);
		Button btnAddFriend=(Button) view.findViewById(R.id.btn_add_friend);
		btnAddFriend.setOnClickListener(this);
		Button btnSubscribe=(Button) view.findViewById(R.id.btn_subscribe);
		btnSubscribe.setOnClickListener(this);
		if(getCurrZoneLayout().isAttention()){
			//取消关注
			btnSubscribe.setText(R.string.str_cancel_subscribe);
//			Requester3.cancelAttention(handler, currUserInfo.userid, "1");
		}else{
			btnSubscribe.setText(R.string.str_add_subscribe);
			//加关注
//			Requester3.attention(handler, currUserInfo.userid);
		}
		popupWindow.showAtLocation(findViewById(R.id.fl_container), Gravity.BOTTOM, 0, 0);
	}
	
	private PopupWindow popupWindow;
	/*//显示清除选择界面
		private void initClearChoice(){
//			View view = inflater.inflate(R.layout.activity_vshare_list_clear_menu ,
			View view = inflater.inflate(R.layout.activity_other_zone_menu ,
					null);
			popupWindow = new PopupWindow(view, LayoutParams.FILL_PARENT,
					LayoutParams.FILL_PARENT, true);
			popupWindow.setBackgroundDrawable(getResources().getDrawable(
					R.color.transparent));
			popupWindow.setOnDismissListener(new OnDismissListener() {
				
				@Override
				public void onDismiss() {
					vContainer.setBackgroundColor(Color.TRANSPARENT);
				}
			});
			view.findViewById(R.id.btn_cancel).setOnClickListener(this);
			view.findViewById(R.id.btn_add_friend).setOnClickListener(this);
			view.findViewById(R.id.btn_subscribe).setOnClickListener(this);
		}*/
	
}
