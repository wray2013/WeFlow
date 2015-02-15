package com.cmmobi.looklook.fragment;


import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import cn.zhugefubin.maptool.ConverterTool;
import cn.zhugefubin.maptool.Point;
import cn.zipper.framwork.core.ZApplication;

import com.baidu.mapapi.map.LocationData;
import com.baidu.mapapi.map.MapController;
import com.baidu.mapapi.map.MyLocationOverlay;
import com.baidu.platform.comapi.basestruct.GeoPoint;
import com.cmmobi.looklook.R;
import com.cmmobi.looklook.activity.DiaryPreviewActivity;
import com.cmmobi.looklook.activity.FootMarkActivity;
import com.cmmobi.looklook.common.adapter.MapNearbyListAdapter;
import com.cmmobi.looklook.common.gson.GsonResponse3.DiaryAttach;
import com.cmmobi.looklook.common.gson.GsonResponse3.MyDiary;
import com.cmmobi.looklook.common.gson.GsonResponse3.MyDiaryList;
import com.cmmobi.looklook.common.utils.CmmobiClickAgentWrapper;
import com.cmmobi.looklook.info.profile.DiaryManager;
import com.cmmobi.looklook.map.MapItemHelper;
import com.cmmobi.looklook.map.MapItemHelper.Item;
import com.cmmobi.looklook.map.MyItemizedOverlay;
import com.cmmobi.looklook.map.MyMapView;
import com.cmmobi.looklook.map.Span;


public class FootMarkFragment extends Fragment implements OnClickListener, Callback {
	private final static String TAG = "NearByFragment";
	
	private final int HANDLER_FLAG_INIT_VIEW = 0xf1000001;
	private final int HANDLER_FLAG_GET_DIARY = 0xf1000002;
	
	private String userID;
	private String lasttime = "";
	FootMarkActivity mActivity;
	private Handler handler;

	DiaryManager diarymanager;
	// UI components
	MyMapView bmv_nearby;
	ListView lv_info;
	ImageView iv_reloc;
	Button btn_cancel;
	TextView tv_today;
	TextView tv_week;
	TextView tv_month;
	TextView tv_halfyear;
	TextView tv_year;
	TextView tv_all;
	
	TextView[] timefilter = {tv_today, tv_week, tv_month, tv_halfyear, tv_year, tv_all};
	
	int selectTime = 1;
	boolean autoSelect;
	
	FrameLayout fl_activity_discover_main_nearby, fl_bg_shadow;
	RelativeLayout fl_activity_discover_map_tankuang;
	LinearLayout ll_activity_discover_main_selector;
	Animation waitanim;
	ImageView iv_wait;
	
	//baidu map
	private MapController mMapController = null;
	MyLocationOverlay myLocationOverlay = null;
	GeoPoint my_gp = null; 
	//GeoPoint gp_center;
	Span result_span;
	//List<OverlayItem> mGeoList;
	MapNearbyListAdapter list_adapter;
	protected DisplayMetrics dm = new DisplayMetrics();
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		dm = getActivity().getApplicationContext().getResources().getDisplayMetrics();
		diarymanager = DiaryManager.getInstance();
	}

	public FootMarkFragment() {
		handler = new Handler(this);
	}
	
	public void setUserID(String uid) {
		if(uid != null && !"".equals(uid)) {
			userID = uid;
		}
	}

	// Called once the Fragment has been created in order for it to
	// create its user interface.
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		Log.e(TAG, "NearByFragment - onCreateView");
		// Create, or inflate the Fragment's UI, and return it.
		// If this Fragment has no UI then return null.
		View view = inflater.inflate(R.layout.fragment_footmark, container, false);

		bmv_nearby = (MyMapView)view.findViewById(R.id.bmv_activity_discover_main_nearby);
		lv_info = (ListView)view.findViewById(R.id.lv_activity_discover_main_list);
		iv_reloc = (ImageView)view.findViewById(R.id.iv_activity_discover_main_reloc);
		btn_cancel = (Button) view.findViewById(R.id.btn_map_cancel);
		timefilter[0] = tv_today = (TextView) view.findViewById(R.id.tv_time_today);
		timefilter[1] = tv_week = (TextView) view.findViewById(R.id.tv_time_week);
		timefilter[2] = tv_month = (TextView) view.findViewById(R.id.tv_time_month);
		timefilter[3] = tv_halfyear = (TextView) view.findViewById(R.id.tv_time_half_year);
		timefilter[4] = tv_year = (TextView) view.findViewById(R.id.tv_time_year);
		timefilter[5] = tv_all = (TextView) view.findViewById(R.id.tv_time_all);
//		changeTextColor(selectTime - 1);
		autoSelect = true;
		
		ll_activity_discover_main_selector = (LinearLayout) view.findViewById(R.id.ll_activity_discover_main_selector);
		fl_activity_discover_main_nearby = (FrameLayout)view.findViewById(R.id.fl__activity_discover_main_nearby);
		fl_bg_shadow = (FrameLayout)view.findViewById(R.id.fl_translucent_layout);
		fl_activity_discover_map_tankuang = (RelativeLayout)view.findViewById(R.id.fl_activity_discover_map_tankuang);
		LinearInterpolator lir = new LinearInterpolator();  
		waitanim = AnimationUtils.loadAnimation(mActivity, R.anim.map_waiting_animation);
		waitanim.setInterpolator(lir);
		iv_wait = (ImageView) view.findViewById(R.id.iv_waiting);
		iv_wait.setVisibility(View.GONE);
		
		ll_activity_discover_main_selector.setVisibility(View.VISIBLE);
		iv_reloc.setOnClickListener(this);
		btn_cancel.setOnClickListener(this);
		tv_today.setOnClickListener(this);
		tv_week.setOnClickListener(this);
		tv_month.setOnClickListener(this);
		tv_halfyear.setOnClickListener(this);
		tv_year.setOnClickListener(this);
		tv_all.setOnClickListener(this);
		
		bmv_nearby.setClickable(false);
		bmv_nearby.setHandler(handler);
		bmv_nearby.setDoubleClickZooming(false);
		mMapController = bmv_nearby.getController();
		
	    //mGeoList = new ArrayList<OverlayItem>();
	    
		mActivity.setFragHandler(handler);

		handler.sendEmptyMessage(HANDLER_FLAG_INIT_VIEW);
		return view;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		Log.e(TAG, "NearByFragment - onActivityCreated");
/*		mNext.setOnClickListener(new OnClickListener() {});*/
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);

		Log.e(TAG, "NearByFragment - onAttach");

		try {
			mActivity = (FootMarkActivity) activity;
			// onGotoPageListener = (OnGotoPageListener) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString() + " must implement OnGotoPageListener");
		}
	}
	
    @Override
	public void onPause() {

        super.onPause();
        bmv_nearby.onPause();
    }
    
    @Override
    public void onViewStateRestored(Bundle inState){
    	super.onViewStateRestored(inState);
    	if(inState!=null){
    		bmv_nearby.onRestoreInstanceState(inState);
    	}

    }
    
    @Override
    public void onSaveInstanceState(Bundle outState){
    	bmv_nearby.onSaveInstanceState(outState);
    	super.onViewStateRestored(outState);

    }
    
    @Override
	public void onResume() {

        super.onResume();
        bmv_nearby.onResume();
        handler.sendEmptyMessageDelayed(MyMapView.HANDLER_FLAG_MAPVIEW_UPDATE, 500);
    }

    
    @Override
	public void onDestroyView() {

        super.onDestroyView();
        bmv_nearby.destroy();
    }

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch(v.getId()){
		case R.id.iv_activity_discover_main_reloc:
			if(my_gp!=null){
				mMapController.animateTo(my_gp);
			}
			//result_span = MapItemHelper.calcNearVideoItemSpan(mActivity.list_nearby_data);
			//mMapController.zoomToSpan(result_span.latSpanE6, result_span.longSpanE6);
	        //mMapController.setCenter(my_gp);

			break;
		case R.id.btn_map_cancel:
			iv_reloc.setVisibility(View.VISIBLE);
			ll_activity_discover_main_selector.setVisibility(View.VISIBLE);
			fl_activity_discover_map_tankuang.setVisibility(View.INVISIBLE);
			fl_bg_shadow.setVisibility(View.INVISIBLE);
			break;
		
		case R.id.tv_time_today:
//			changeTextColor(0);
			selectTime = 1;
			// 2014-4-8
			CmmobiClickAgentWrapper.onEvent(getActivity(), "time_print");
			requestDiaries(handler, selectTime);
			break;
		case R.id.tv_time_week:
//			changeTextColor(1);
			selectTime = 2;
			//2014-4-8
			CmmobiClickAgentWrapper.onEvent(getActivity(), "time_print");
			requestDiaries(handler, selectTime);
			break;
		case R.id.tv_time_month:
//			changeTextColor(2);
			selectTime = 3;
			//2014-4-8
			CmmobiClickAgentWrapper.onEvent(getActivity(), "time_print");
			requestDiaries(handler, selectTime);
			break;
		case R.id.tv_time_half_year:
//			changeTextColor(3);
			selectTime = 4;
			//2014-4-8
			CmmobiClickAgentWrapper.onEvent(getActivity(), "time_print");
			requestDiaries(handler, selectTime);
			break;
		case R.id.tv_time_year:
//			changeTextColor(4);
			selectTime = 5;
			//2014-4-8
			CmmobiClickAgentWrapper.onEvent(getActivity(), "time_print");
			requestDiaries(handler, selectTime);
			break;
		case R.id.tv_time_all:
//			changeTextColor(5);
			selectTime = 6;
			//2014-4-8
			CmmobiClickAgentWrapper.onEvent(getActivity(), "time_print");
			requestDiaries(handler, selectTime);
			break;
		}
	}
	
	private void changeTextColor(int id) {
		if (id >= 0 && id <= 5) {
			for (int i = 0; i < timefilter.length; i++) {
				if(i == id) {
					timefilter[i].setTextColor(getResources().getColor(R.color.orange));
				} else {
					timefilter[i].setTextColor(getResources().getColor(R.color.blue));
				}
			}
		}
	}

	@Override
	public boolean handleMessage(Message msg) {
		//LocationData myLoc;
		// TODO Auto-generated method stub
		try{
			switch(msg.what){
//			case Requester3.RESPONSE_TYPE_LIST_MY_DIARY:
			case HANDLER_FLAG_GET_DIARY:
				iv_wait.clearAnimation();
				iv_wait.setVisibility(View.GONE);
				if(mActivity.myLoc==null || mActivity.list_nearby_data==null || myLocationOverlay==null){
					break; //myLoc = new LocationData();
				}
//				GsonResponse3.listMyDiaryResponse obj_nearby = (GsonResponse3.listMyDiaryResponse) msg.obj;
				mActivity.nearby_objs = null;
				mActivity.list_nearby_data.clear();
				
				MyDiary[] obj_nearby = (MyDiary[]) msg.obj;
				
				if(obj_nearby!=null/* && "0".equals(obj_nearby.status) && obj_nearby.diaries.length>0*/){
					//obj.items;
					//模拟数据
//					if(obj_nearby == null) {
//						obj_nearby = new GsonResponse2.nearDiaryResponse();
//					}
//					obj_nearby.diaries = simulateDiaries();
					for(MyDiary item : obj_nearby){
						if(item.latitude_view==null || item.latitude_view.equals("") || item.longitude_view==null || item.longitude_view.equals("")){
							Log.e(TAG, "MyDiary latitude or longitude is null");
							continue;
							
						}
						mActivity.list_nearby_data.add(item);
					}
					
					mActivity.nearby_objs = MapItemHelper.showNearByItems(mActivity, handler, getResources(), bmv_nearby, myLocationOverlay, mActivity.list_nearby_data, true);
				}
				
				//没有可显示的日记
				if((mActivity.nearby_objs == null || mActivity.nearby_objs.length <= 0)
						&& selectTime < 6 && autoSelect) {
					selectTime ++;
					requestDiaries(handler, selectTime);
					break;
				}
				autoSelect = false;
				changeTextColor(selectTime - 1);
				
				break;
			case HANDLER_FLAG_INIT_VIEW:
				fl_activity_discover_map_tankuang.setVisibility(View.INVISIBLE);
				fl_bg_shadow.setVisibility(View.INVISIBLE);
				if(mActivity.myLoc==null ){
					break; //myLoc = new LocationData();
				}
				
				Point p = new Point();
				ConverterTool ct=new ConverterTool();
				p = ct.GG2BD((double) (mActivity.myLoc.longitude), (double) (mActivity.myLoc.latitude));
				
				my_gp = new GeoPoint((int)(p.getLatitude()*1e6), (int)(p.getLongitude()*1e6));
				LocationData bdLocData = mActivity.myLoc;
				bdLocData.latitude = p.getLatitude();
				bdLocData.longitude = p.getLongitude();
				bdLocData.accuracy = 0;
				
				Log.e(TAG, "loc latitude:" + mActivity.myLoc.latitude + " longitude:" + mActivity.myLoc.longitude);
				myLocationOverlay = new MyLocationOverlay(bmv_nearby);
				myLocationOverlay.enableCompass();
				myLocationOverlay.setMarker(getResources().getDrawable(R.drawable.wodeweizhi_1));
		        myLocationOverlay.setData(/*mActivity.myLoc*/bdLocData);
		        
			    bmv_nearby.getOverlays().add(myLocationOverlay);
			    //Log.e(TAG, "latSpanE6:" + result_span.latSpanE6 + " lonSpanE6:" + result_span.longSpanE6);

//				lv_nearby.reset();
				if(mActivity.list_nearby_data.size()>0){
					mActivity.nearby_objs = MapItemHelper.showNearByItems(mActivity, handler, getResources(), bmv_nearby, myLocationOverlay,  mActivity.list_nearby_data, true);
					Log.e(TAG, "list_nearby_data size:" + mActivity.list_nearby_data.size());
//					MyDiary[] its = (MyDiary[])(mActivity.list_nearby_data.toArray(new MyDiary[0]));
//					lv_nearby.loadMore(its);
				}else{
					bmv_nearby.getOverlays().clear();
					try {
						//中国地图
						GeoPoint gp_center = new GeoPoint((int)(35 * 1e6), (int)(104 * 1e6));
						bmv_nearby.getController().setZoom((float) 4.6);
						bmv_nearby.getController().setCenter(gp_center);
						bmv_nearby.getController().animateTo(gp_center);
					} catch (NullPointerException e) {
						Log.e(TAG, "Baidu Map Internal NullPointer");
						Toast.makeText(mActivity, "百度地图缩放出错，请刷新重试！", Toast.LENGTH_LONG).show();
			    	}
					/*mActivity.nearby_video_pageno = 1;*/
					mActivity.nearby_video_pageno = 1; 
//			        Requester3.requestNearDiary(handler, mActivity.myLoc, ""+mActivity.nearby_video_pageno, "50", getPicWidth() + "", "");
//			        Requester3.requestMyDiary(handler, userID, lasttime, "2", "480", "480");
			        iv_wait.setVisibility(View.VISIBLE);
			        iv_wait.startAnimation(waitanim);
			        requestDiaries(handler, selectTime);
				}
				break;
			case MyMapView.HANDLER_FLAG_MAPVIEW_CLICK:
				iv_reloc.setVisibility(View.VISIBLE);
				ll_activity_discover_main_selector.setVisibility(View.VISIBLE);
				fl_activity_discover_map_tankuang.setVisibility(View.INVISIBLE);
				fl_bg_shadow.setVisibility(View.INVISIBLE);
				//lv_info.setVisibility(View.INVISIBLE);
				break;
				
			case MyMapView.HANDLER_FLAG_MAPVIEW_UPDATE:
				Log.e(TAG, "handler update: level:" + bmv_nearby.getZoomLevel());
				mActivity.nearby_objs = MapItemHelper.showNearByItems(mActivity, handler, getResources(), bmv_nearby, myLocationOverlay, mActivity.list_nearby_data, false);
				break;
			case MyItemizedOverlay.HANDLER_FLAG_MAP_ITEM_TAP:
				int index = (Integer) msg.obj;
				if(mActivity.nearby_objs==null || mActivity.nearby_objs[index]==null){
					break;
				}
				final Item it = mActivity.nearby_objs[index];
				if(it.type==1){
					sortDiary(it.nearby_list);
					list_adapter = new MapNearbyListAdapter(mActivity, handler,
							R.layout.row_list_diary_nearby, R.id.tv_row_list_diary_nick,
							it.nearby_list, fl_activity_discover_map_tankuang);
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
								ArrayList<MyDiaryList> diarylist = new ArrayList<MyDiaryList>();
								for(MyDiary diaryitem : it.nearby_list) {
									diarylist.add(DiaryManager.getInstance().findDiaryGroupByUUID(diaryitem.diaryuuid));
								}
								DiaryManager.getInstance().setDetailDiaryList(diarylist, 0);
								startActivity(new Intent(ZApplication.getInstance(), DiaryPreviewActivity.class)
								.putExtra(DiaryPreviewActivity.INTENT_ACTION_DIARY_UUID, diaryuuid));
								fl_activity_discover_map_tankuang.setVisibility(View.INVISIBLE);
								fl_bg_shadow.setVisibility(View.INVISIBLE);
							}
						}
						
					});
					list_adapter.notifyDataSetChanged();
					iv_reloc.setVisibility(View.GONE);
					ll_activity_discover_main_selector.setVisibility(View.GONE);
					fl_activity_discover_map_tankuang.setVisibility(View.VISIBLE);
					fl_bg_shadow.setVisibility(View.VISIBLE);
					//lv_info.setVisibility(View.VISIBLE);
				}
				break;
				
			}
		}catch(Exception e){
			e.printStackTrace();
		}

		return false;
	}
	
	/**
	 * 根据时间请求日记列表
	 * @param timeType
	 * 1.今天   2.一周   3.一月   4.半年   5.一年   6.全部
	 */
	private void requestDiaries(final Handler handler, final int timeType) {
		new Thread() {
			@Override
			public void run() {
				long now = System.currentTimeMillis();
				long filtertime = now;
				MyDiary[] myDiaries = null;
				Calendar ca = Calendar.getInstance();
				ca.setTimeInMillis(now);
				ca.set(Calendar.HOUR, 0);
				ca.set(Calendar.MINUTE, 0);
				ca.set(Calendar.SECOND, 0);
				ca.set(Calendar.MILLISECOND, 0);
				
				switch(timeType) {
				case 1:
					break;
				case 2:
					ca.add(Calendar.DATE, -7);
					break;
				case 3:
					ca.add(Calendar.MONTH, -1);
					break;
				case 4:
					ca.add(Calendar.MONTH, -6);
					break;
				case 5:
					ca.add(Calendar.YEAR, -1);
					break;
				case 6:
					ca.set(Calendar.YEAR, 1970);
					break;
				}
				filtertime = ca.getTimeInMillis();
				
				myDiaries = diarymanager.getDiaries(filtertime);
				if(handler != null) {
					Message message = handler.obtainMessage(HANDLER_FLAG_GET_DIARY, myDiaries);
					handler.sendMessage(message);
				}
			}
		}.start();
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
/*	private MyDiary[] getDiaries(long ms) {
		Map<String, MyDiary> diaryMap = diarymanager.getDiaryMap();
		ArrayList<MyDiary> diaries = new ArrayList<MyDiary>();
		if (diaryMap != null && diaryMap.size() > 0) {
			Iterator<Entry<String, MyDiary>> iter = diaryMap.entrySet().iterator();
			while (iter.hasNext()) {
				Map.Entry<String, MyDiary> entry = (Map.Entry<String, MyDiary>) iter.next();
				MyDiary myDiary = entry.getValue();
				if(myDiary != null) {
//					Log.d(TAG, "filter time = " + ms + ";diaries millitime = " + myDiary.diarytimemilli);
					if(myDiary.join_safebox.equals("1") || myDiary.isModified()
							|| Long.parseLong(myDiary.diarytimemilli) < ms) {
						continue;
					}
					diaries.add(myDiary);
				}
			}
		}
		Log.d(TAG, "Got " + diaries.size() + " diaries");
		return (MyDiary[]) diaries.toArray(new MyDiary[diaries.size()]);
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
    		d.longitude_view  = loc[i][0];
    		d.latitude_view = loc[i][1];
    		d.sex = loc[i][2];
    		d.nickname = loc[i][3];
//    		d.attachs = new DiaryAttach[1];
    		d.diaryid = loc[i][4];
    		DiaryAttach da = new DiaryAttach();
//    		da.attachlevel = "1";
//    		da.attachtype = "2";
//    		d.attachs[0] = da;
    	    diaries[i] = d;
    	}
    	return diaries;
    }

	private int getPicWidth() {
		return (int) (0.85 * dm.widthPixels);
	}

}
