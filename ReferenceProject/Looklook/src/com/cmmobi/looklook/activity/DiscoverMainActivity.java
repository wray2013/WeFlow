 package com.cmmobi.looklook.activity;


import java.util.ArrayList;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import cn.zipper.framwork.core.ZBroadcastReceiverManager;
import cn.zipper.framwork.core.ZDataExchanger.ZDataExchangerKey;
import cn.zipper.framwork.core.ZViewFinder;

import com.baidu.mapapi.map.LocationData;
import com.baidu.mapapi.map.MyLocationOverlay;
import com.cmmobi.looklook.R;
import com.cmmobi.looklook.common.gson.GsonResponse2.nearVideoItem;
import com.cmmobi.looklook.common.gson.GsonResponse2.videorecommendItem;
import com.cmmobi.looklook.common.utils.CmmobiClickAgentWrapper;
import com.cmmobi.looklook.common.utils.UmengclickAgentWrapper;
import com.cmmobi.looklook.fragment.NearByFragment;
import com.cmmobi.looklook.fragment.RecommendFragment;
import com.cmmobi.looklook.fragment.VideoListFragment;
import com.cmmobi.looklook.info.location.MyLocationInfo;
import com.cmmobi.looklook.info.location.OnLocationUpdateListener;
import com.cmmobi.looklook.map.MapItemHelper.Item;

public class DiscoverMainActivity extends FragmentActivity implements Callback, OnClickListener, OnLocationUpdateListener {
	//Constants
	private final static int FRAGMENT_RECOMMOND_VIEW = 0;
	private final static int FRAGMENT_NEARBY_VIEW = 1;
	private final static int FRAGMENT_LIST_VIEW = 2;

	private final String TAG = "DiscoverMainActivity";
	public final static int HANDLER_FLAG_ACTIVITY_RESPONSE = 0xf7130101;
	public final static int HANDLER_FLAG_ACTIVITY_SHOWLIST = 0xf7130102;
	public final static int HANDLER_FLAG_ACTIVITY_HIDELIST = 0xf7130103;
	public final static int HANDLER_FLAG_ACTIVITY_LOCATION = 0xf7130104;
	
	//Activity
	protected ZViewFinder viewFinder;
	public Handler handler;
	private Handler frag_handler;
	private ZBroadcastReceiverManager receiverManager;
	
	//Fragment
	private FragmentManager fm;
	private NearByFragment frag_nearby;
	private RecommendFragment frag_recommond;
	private VideoListFragment frag_list;

	
	//widget
	private ImageView iv_nearby;
	private ImageView iv_recommond;
	private ImageView iv_list;
	private ImageView iv_map;
	public int show_view_type = 0; //0 uninit 1 recommond 2 nearby
/*	private ImageView iv_reloc;
	private ListView lv_list;
	private MapView bmv_nearby;
	private MapView bmv_recommond;*/
	
	//map and location
	public MyLocationInfo myLocInfo;
	public LocationData myLoc;
	public MyLocationOverlay myLocationOverlay = null;
	private boolean shouldReqNearby;
	
	//list
/*	private ListView lv_weibofriends;
	public NearbyListAdapter list_nearby_adapter;*/
	public ArrayList<nearVideoItem> list_nearby_data;	
	public Item[] nearby_objs;
	public ArrayList<videorecommendItem> list_recommond_data;
	public Item[] recommond_objs;
	public int nearby_video_pageno;
	public int recommond_video_pageno;
	public boolean recommond_more = true;
	public boolean nearby_more = true;
	
	//假如得到的数据没有地理位置信息，模拟主要大中城市的：	
	private final String[] cityNameList = {"北京",    "上海",     "天津",   "香港",     "广州",    "珠海",    "深圳",     "杭州",     "重庆",     "青岛",    "厦门",    "福州",     "兰州",   "贵阳",    "长沙",     "南京",    "南昌",     "沈阳",    "太原",    "成都",    "拉萨",   "乌鲁木齐", "昆明",    "西安",    "西宁",    "银川",     "哈尔滨",  "长春",    "武汉",    "郑州",     "石家庄",   "三亚",    "海口",    "澳门" };
	private final int[] cityWeightList =   {1020,     1360,       570,      700,        680,      100,       295,        440,       400,        315,       155,       260,       310,      185,       383,        525,       195,       492,       345,      1257,      51,       180,       360,       450,       220,       200,       315,       240,       660,       436,        441,       53,        76,        46};	
	private final double[] cityLatList = {39.91667,   34.50000,  39.13333, 22.20000,   23.16667,  22.30000,  22.61667,  30.26667,   29.56667,  36.06667,  24.46667,  26.08333,  36.03333, 26.56667,   28.21667,  32.05000,  28.68333,  41.80000,  37.86667,  30.66667, 29.60000, 43.76667,   25.05000,  34.26667,  36.56667,  38.46667,  45.75000,  43.88333,  30.51667,  34.76667,   38.03333,  18.20000,  20.01667,  22.20000};
	private final double[] cityLonList = {116.41667, 121.43333, 117.20000, 114.10000, 113.23333, 113.51667, 114.06667, 120.20000,  106.45000, 120.33333, 118.10000, 119.30000, 103.73333, 106.71667, 113.00000, 118.78333, 115.90000, 123.38333, 112.53333, 104.06667, 91.00000, 87.68333,  102.73333, 108.95000, 101.75000, 106.26667, 126.63333, 125.35000, 114.31667, 113.65000,  114.48333, 109.50000, 110.35000, 113.50000};
	
	@Override
	public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_discover_main);
		viewFinder = new ZViewFinder(getWindow());
		handler = new Handler(this);
        
		//fragment
		frag_nearby = new NearByFragment();
		frag_recommond = new RecommendFragment();
		frag_list = new VideoListFragment();
		fm = getSupportFragmentManager();
		
		
        //UI widget
        ZViewFinder finder = getZViewFinder();
        iv_nearby = finder.findImageView(R.id.iv_activity_discover_main_nearby);
        iv_recommond = finder.findImageView(R.id.iv_activity_discover_main_recommond);
        iv_list = finder.findImageView(R.id.iv_activity_discover_main_list);
        iv_map = finder.findImageView(R.id.iv_activity_discover_main_map);
/*        iv_reloc = finder.findImageView(R.id.iv_activity_discover_main_reloc);
        lv_list = finder.findListView(R.id.lv_activity_discover_main_list);
        bmv_nearby = (MapView)findViewById(R.id.bmv_activity_discover_main_nearby);
        bmv_recommond = (MapView)findViewById(R.id.bmv_activity_discover_main_recommond);*/
        
        iv_nearby.setOnClickListener(this);
        iv_recommond.setOnClickListener(this);
        iv_list.setOnClickListener(this);
        iv_map.setOnClickListener(this);
/*        iv_reloc.setOnClickListener(this);*/
        
        //data
        list_recommond_data = new ArrayList<videorecommendItem>();
        list_nearby_data = new ArrayList<nearVideoItem>();
        
        //init
        goToPage(FRAGMENT_RECOMMOND_VIEW, false);
        
        //get geoCode
        myLocInfo = MyLocationInfo.getInstance(this);
        myLoc =  myLocInfo.getLocation();

        shouldReqNearby = true;
        myLocInfo.startLocating(this);
        
        //request recommond list
        //Requester.requestNearVideo(handler);
/*        nearby_video_pageno = 1;
        recommond_video_pageno = 1; 
        Requester.requestVideoRecommend(handler, recommond_video_pageno, 10);*/
        
	}
	
	public void goToPage(int type, boolean record) {
		Fragment dst;
		String mViewName = null;
		dst = null;
		if (type == FRAGMENT_RECOMMOND_VIEW) {

			dst = frag_recommond;
			mViewName = "recommond";
			show_view_type = 1;

		} else if (type == FRAGMENT_NEARBY_VIEW) {

			dst = frag_nearby;
			mViewName = "nearby";
			show_view_type = 2;

		} else if (type == FRAGMENT_LIST_VIEW) {

			dst = frag_list;
			mViewName = "list";

		}  else {

			Log.e(TAG, "unknow fragment - type:" + type);
			return;
		}

		FragmentTransaction ft = fm.beginTransaction();
		
		

		// check login_container if null
		if (fm.findFragmentById(R.id.container_discover) != null) {
			Log.e(TAG, "replace login_container dst:" + dst.toString());
/*			ft.setCustomAnimations(R.anim.fragment_list_slide_in_from_bottom,  
                    android.R.anim.fade_out);  */
			ft.replace(R.id.container_discover, dst, mViewName);
			//ft.hide(arg0);

		} else {
			Log.e(TAG, "add login_container dst:" + dst.toString());
			ft.add(R.id.container_discover, dst, mViewName);
		}

		if (record) {
			ft.addToBackStack(null);
		}

		ft.commit();
		// ft.commitAllowingStateLoss();

	}
	
	@Override
	public boolean handleMessage(Message msg) {
		// TODO Auto-generated method stub
		switch(msg.what){
/*		case Requester.RESPONSE_TYPE_VIDEO_RECOMMEND:
			GsonResponse.videorecommendResponse obj_recommond = (GsonResponse.videorecommendResponse) msg.obj;
			if(obj_recommond!=null && "0".equals(obj_recommond.status)){
				//obj.items;
				for(videorecommendItem item : obj_recommond.items){
					if(item.latitude==null || item.latitude.equals("") || item.longitude==null || item.longitude.equals("")){
						Log.e(TAG, "videorecommendItem latitude or longitude is null");
						continue;
						
					}
					list_recommond_data.add(item);
				}
				if(frag_handler!=null && fm.findFragmentByTag("recommond")!=null){
					Message msg_to_send = frag_handler.obtainMessage(HANDLER_FLAG_ACTIVITY_RESPONSE);
					msg_to_send.sendToTarget();
				}
				
			}
			break;*/
/*		case Requester.RESPONSE_TYPE_NEAR_VIDEO:
			GsonResponse.nearVideoResponse obj_nearby = (GsonResponse.nearVideoResponse)msg.obj;
			if(obj_nearby!=null && "0".equals(obj_nearby.status)){
				for(nearVideoItem item : obj_nearby.items){
					if(item.latitude==null || item.latitude.equals("") || item.longitude==null || item.longitude.equals("")){
						Log.e(TAG, "nearVideoResponse latitude or longitude is null");
						continue;
						
					}
					list_nearby_data.add(item);
				}
				if(frag_handler!=null && fm.findFragmentByTag("nearby")!=null){
					Message msg_to_send = frag_handler.obtainMessage(HANDLER_FLAG_ACTIVITY_RESPONSE);
					msg_to_send.sendToTarget();
				}

			}
			break;*/
		}
		return false;
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch(v.getId()){
		case R.id.iv_activity_discover_main_nearby:
			iv_nearby.setVisibility(View.GONE);
			iv_recommond.setVisibility(View.VISIBLE);
			goToPage(FRAGMENT_NEARBY_VIEW, false);
			iv_list.setVisibility(View.VISIBLE);
			iv_map.setVisibility(View.GONE);
/*			bmv_nearby.setVisibility(View.VISIBLE);
			bmv_recommond.setVisibility(View.GONE);
			bmv_nearby.refresh();*/
			break;
		case R.id.iv_activity_discover_main_recommond:
			iv_nearby.setVisibility(View.VISIBLE);
			iv_recommond.setVisibility(View.GONE);
			goToPage(FRAGMENT_RECOMMOND_VIEW, false);
			iv_list.setVisibility(View.VISIBLE);
			iv_map.setVisibility(View.GONE);
/*			bmv_nearby.setVisibility(View.GONE);
			bmv_recommond.setVisibility(View.VISIBLE);
			bmv_recommond.refresh();*/
			break;
		case R.id.iv_activity_discover_main_list:
			iv_map.setVisibility(View.VISIBLE);
			iv_list.setVisibility(View.GONE);
			if(frag_handler!=null ){
				Message msg_to_send = frag_handler.obtainMessage(HANDLER_FLAG_ACTIVITY_SHOWLIST);
				msg_to_send.sendToTarget();
			}
			break;
		case R.id.iv_activity_discover_main_map:
			iv_list.setVisibility(View.VISIBLE);
			iv_map.setVisibility(View.GONE);
			if(frag_handler!=null ){
				Message msg_to_send = frag_handler.obtainMessage(HANDLER_FLAG_ACTIVITY_HIDELIST);
				msg_to_send.sendToTarget();
			}
			break;
		//case R.id.iv_activity_discover_main_reloc:
		//	break;

		}

	}
	
	@Override
	protected void onResume() {
		super.onResume();
		if (receiverManager != null) {
			receiverManager.registerAllZReceiver();
		}
		
		UmengclickAgentWrapper.onResume(this);
		CmmobiClickAgentWrapper.onResume(this);

	}
	
/*	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {

			return false;
		}
		return false;
	}
	*/
	
	@Override
	protected void onPause() {
		super.onPause();
		if (receiverManager != null) {
			receiverManager.unregisterAllZReceiver(true);
		}
		myLocInfo.saveLocation();
		UmengclickAgentWrapper.onPause(this);
		CmmobiClickAgentWrapper.onPause(this);
	}
	
	@Override
	public void onStop(){
		super.onStop();
		CmmobiClickAgentWrapper.onStop(this);
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
        myLocInfo.stopLocating();
	}
	

	
	/**
	 * 获取广播接收器的管理器;
	 * @return
	 */
	protected final ZBroadcastReceiverManager getZReceiverManager() {
		if (receiverManager == null) {
			receiverManager = new ZBroadcastReceiverManager(this);
		}
		return receiverManager;
	}
	
	/**
	 * 获取View查找器, 方便获取View对象;
	 * @return
	 */
	public ZViewFinder getZViewFinder() {
		return viewFinder;
	}
	
	/**
	 * 获取本Activity的Handler;
	 * @return
	 */
	public Handler getHandler() {
		return handler;
	}
	
	public void setFragHandler(Handler handler){
		this.frag_handler = handler;
	}
	
	protected final Object getSerializableExtra(String key) {
		return getIntent().getSerializableExtra(key);
	}
	
	protected final ZDataExchangerKey getZDataExchangerKey(String key) {
		return (ZDataExchangerKey) getSerializableExtra(key);
	}
	
	protected final void setToNoTitle() {
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
	}
	
	protected final void setToFullscreen() {
		this.getWindow().setFlags(
				WindowManager.LayoutParams.FLAG_FULLSCREEN, 
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
	}
	
	/**
	 * 强制横屏;
	 */
	protected final void setToLandscape() {
		this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
	}
	
	/**
	 * 强制竖屏;
	 */
	protected final void setToPortrait() {
		this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
	}
	
//	protected final void measureSize(View view) {
//		view.getViewTreeObserver().addOnGlobalLayoutListener(listener)
//	}
	
	/**
	 * 展示Activity切换动画;
	 * 
	 * 不想使用ZipperFramework自带的动画, 可以使用函数startActivitySwitchAnimation(int inAnimation, int outAnimation);
	 * 
	 * @param inOrOut: 进入到新Activity则传入true, 后退到上一个Activiry则传入false;
	 */
	protected final void startActivitySwitchAnimation(Boolean inOrOut) {
		if (inOrOut != null) {
			if (inOrOut) {
				super.overridePendingTransition(R.anim.z_animation_slide_in_from_right, R.anim.z_animation_slide_out_to_left);
			} else {
				super.overridePendingTransition(R.anim.z_animation_slide_in_from_left, R.anim.z_animation_slide_out_to_right);
			}
		}
	}
	
	/**
	 * 指定Activity切换动画;
	 * @param inAnimation: 将要进入屏幕的Activity使用的动画(R.anim.xxxx);
	 * @param outAnimation: 将要移出屏幕的Activity使用的动画(R.anim.xxxx);
	 */
	protected final void startActivitySwitchAnimation(int inAnimation, int outAnimation) {
		super.overridePendingTransition(inAnimation, outAnimation);
	}

	@Override
	public void OnLocationUpdate(LocationData myLoc) {
		// TODO Auto-generated method stub
		//ZToast.showShort("latitude:" + myLoc.latitude + " longitude" + myLoc.longitude );
        if(shouldReqNearby){//only once
        	shouldReqNearby = false;
        	
        	
    		//Requester.requestNearVideo(handler, myLoc, 1, 10); 
			if(frag_handler!=null && fm.findFragmentByTag("nearby")!=null && this.myLoc==null){
				Message msg_to_send = frag_handler.obtainMessage(HANDLER_FLAG_ACTIVITY_LOCATION);
				msg_to_send.sendToTarget();
			}
        }
        
        this.myLoc = myLoc;

        myLocInfo.stopLocating();
	}
	
	
	private int GetRandomIndexFromCity(int[] cityWeightList){
		double r = Math.random();
		int totalWeight = 0;
		for(int i : cityWeightList){
			totalWeight += i;
		}
		int index = 0;
		int rank = (int) (totalWeight*r);
		while(rank>cityWeightList[index]){
			rank -= cityWeightList[index];
			index++;
		}
		return index;
		
	}

}
