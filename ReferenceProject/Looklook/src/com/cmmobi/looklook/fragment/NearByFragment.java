package com.cmmobi.looklook.fragment;


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
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

import cn.zipper.framwork.core.ZApplication;

import com.baidu.mapapi.map.MapController;
import com.baidu.mapapi.map.MyLocationOverlay;
import com.baidu.platform.comapi.basestruct.GeoPoint;
import com.cmmobi.looklook.R;
import com.cmmobi.looklook.activity.DiaryDetailActivity;
import com.cmmobi.looklook.activity.DiscoverMainActivity;
import com.cmmobi.looklook.activity.FriendsNearByActivity;
import com.cmmobi.looklook.common.adapter.MapNearbyListAdapter;
import com.cmmobi.looklook.common.gson.GsonResponse2;
import com.cmmobi.looklook.common.gson.GsonResponse2.MyDiary;
import com.cmmobi.looklook.common.gson.GsonResponse2.diaryAttach;
import com.cmmobi.looklook.common.gson.Requester2;
import com.cmmobi.looklook.common.listview.pulltorefresh.AbsRefreshView;
import com.cmmobi.looklook.common.listview.pulltorefresh.NearbyDiaryListView;
import com.cmmobi.looklook.info.profile.DiaryManager;
import com.cmmobi.looklook.map.MapItemHelper;
import com.cmmobi.looklook.map.MapItemHelper.Item;
import com.cmmobi.looklook.map.MyItemizedOverlay;
import com.cmmobi.looklook.map.MyMapView;
import com.cmmobi.looklook.map.Span;


public class NearByFragment extends Fragment implements OnClickListener, Callback {
	private final static String TAG = "NearByFragment";
	
	private final int HANDLER_FLAG_INIT_VIEW = 0xf1000001;
	
	FriendsNearByActivity mActivity;
	private Handler handler;

	// UI components
	MyMapView bmv_nearby;
	ListView lv_info;
	ImageView iv_reloc;
	ImageView iv_close;
	NearbyDiaryListView lv_nearby;
	FrameLayout fl_activity_discover_main_nearby;
	RelativeLayout fl_activity_discover_map_tankuang;
	
	//baidu map
	private MapController mMapController = null;
	MyLocationOverlay myLocationOverlay = null;
	GeoPoint my_gp = null; 
	//GeoPoint gp_center;
	Span result_span;
	//List<OverlayItem> mGeoList;
	Item[] objs;
	MapNearbyListAdapter list_adapter;
	protected DisplayMetrics dm = new DisplayMetrics();
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		dm = getActivity().getApplicationContext().getResources().getDisplayMetrics();
	}

	public NearByFragment() {
		handler = new Handler(this);
	}

	// Called once the Fragment has been created in order for it to
	// create its user interface.
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		Log.e(TAG, "NearByFragment - onCreateView");
		// Create, or inflate the Fragment's UI, and return it.
		// If this Fragment has no UI then return null.
		View view = inflater.inflate(R.layout.fragment_discover_nearby, container, false);

		bmv_nearby = (MyMapView)view.findViewById(R.id.bmv_activity_discover_main_nearby);
		lv_info = (ListView)view.findViewById(R.id.lv_activity_discover_main_list);
		iv_reloc = (ImageView)view.findViewById(R.id.iv_activity_discover_main_reloc);
		iv_close = (ImageView)view.findViewById(R.id.iv_activity_discover_tankuang_close);
		lv_nearby = (NearbyDiaryListView)view.findViewById(R.id.lv_activity_discover_main_nearby);
		fl_activity_discover_main_nearby = (FrameLayout)view.findViewById(R.id.fl__activity_discover_main_nearby);
		fl_activity_discover_map_tankuang = (RelativeLayout)view.findViewById(R.id.fl_activity_discover_map_tankuang);
		
		iv_reloc.setOnClickListener(this);
		iv_close.setOnClickListener(this);
		bmv_nearby.setClickable(false);
		bmv_nearby.setHandler(handler);
		bmv_nearby.setDoubleClickZooming(false);
		mMapController = bmv_nearby.getController();
		
	    //mGeoList = new ArrayList<OverlayItem>();
		

	    
		mActivity.setFragHandler(handler);
				lv_nearby.setOnRefreshListener(new AbsRefreshView.OnRefreshListener() {
			
			@Override
			public void onRefresh() {
				if(mActivity.myLoc!=null){
					mActivity.nearby_video_pageno=1;
					mActivity.list_nearby_data.clear();
					Requester2.requestNearDiary(handler, mActivity.myLoc,  ""+mActivity.nearby_video_pageno++, "10", getPicWidth() + "", "");
					lv_nearby.reset();
				}

			}
			
			@Override
			public void onMore() {
				if(!mActivity.nearby_more){
					lv_nearby.loadDateError();
					Log.d(TAG, "no more");
					
				}else{
					if(mActivity.myLoc!=null){
						Requester2.requestNearDiary(handler, mActivity.myLoc,  ""+mActivity.nearby_video_pageno++, "10", getPicWidth() + "", "");
					}else{
						lv_nearby.loadDateError();
					}
					
				}
			}

			@Override
			public void onAutoScroll(int l, int t, int oldl, int oldt) {
//				System.out.println("onAutoScroll");
				
			}
			
			
		});

		//
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
			mActivity = (FriendsNearByActivity) activity;
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
		case R.id.iv_activity_discover_tankuang_close:
			fl_activity_discover_map_tankuang.setVisibility(View.INVISIBLE);
			break;
		}
		
	}
	


	@Override
	public boolean handleMessage(Message msg) {
		//LocationData myLoc;
		// TODO Auto-generated method stub
		try{
			switch(msg.what){
			case Requester2.RESPONSE_TYPE_NEAR_DIARY:
				if(mActivity.myLoc==null || mActivity.list_nearby_data==null || myLocationOverlay==null){
					break; //myLoc = new LocationData();
				}
				GsonResponse2.nearDiaryResponse obj_nearby = (GsonResponse2.nearDiaryResponse) msg.obj;
				if(obj_nearby!=null && "0".equals(obj_nearby.status) && obj_nearby.diaries.length>0){
					//obj.items;
					//模拟数据
//					if(obj_nearby == null) {
//						obj_nearby = new GsonResponse2.nearDiaryResponse();
//					}
//					obj_nearby.diaries = simulateDiaries();
					for(MyDiary item : obj_nearby.diaries){
						if(item.latitude==null || item.latitude.equals("") || item.longitude==null || item.longitude.equals("")){
							Log.e(TAG, "MyDiary latitude or longitude is null");
							continue;
							
						}
						mActivity.list_nearby_data.add(item);
					}
					
					mActivity.nearby_objs = MapItemHelper.showNearByItems(mActivity, handler, getResources(), bmv_nearby, myLocationOverlay, mActivity.list_nearby_data, true);

					lv_nearby.loadMore(obj_nearby.diaries);
				}else{
					mActivity.nearby_more = false;
					lv_nearby.loadDateError();
				}

				break;
			case DiscoverMainActivity.HANDLER_FLAG_ACTIVITY_SHOWLIST:
				//
				lv_nearby.setVisibility(View.VISIBLE);
				fl_activity_discover_main_nearby.setVisibility(View.GONE);
				break;
			case DiscoverMainActivity.HANDLER_FLAG_ACTIVITY_HIDELIST:
				//
				fl_activity_discover_main_nearby.setVisibility(View.VISIBLE);
				lv_nearby.setVisibility(View.GONE);
				break;
			case DiscoverMainActivity.HANDLER_FLAG_ACTIVITY_RESPONSE:
				
				if(mActivity.myLoc==null || mActivity.list_nearby_data==null || myLocationOverlay==null){
					break; //myLoc = new LocationData();
				}
				
				Log.e(TAG, "loc latitude:" + mActivity.myLoc.latitude + " longitude:" + mActivity.myLoc.longitude);
		        
		        //my_gp = new GeoPoint((int)(myLoc.latitude* 1e6), (int)(myLoc.longitude *  1e6));
		        
			    //bmv_nearby.getOverlays().add(myLocationOverlay);
			    //Log.e(TAG, "latSpanE6:" + result_span.latSpanE6 + " lonSpanE6:" + result_span.longSpanE6);


		        objs = MapItemHelper.showNearByItems(mActivity, handler, getResources(), bmv_nearby, myLocationOverlay, mActivity.list_nearby_data, true);
				break;
			case HANDLER_FLAG_INIT_VIEW:
			case DiscoverMainActivity.HANDLER_FLAG_ACTIVITY_LOCATION:
				fl_activity_discover_map_tankuang.setVisibility(View.INVISIBLE);
				if(mActivity.myLoc==null ){
					break; //myLoc = new LocationData();
				}
				
				Log.e(TAG, "loc latitude:" + mActivity.myLoc.latitude + " longitude:" + mActivity.myLoc.longitude);
				myLocationOverlay = new MyLocationOverlay(bmv_nearby);
				myLocationOverlay.enableCompass();
				myLocationOverlay.setMarker(getResources().getDrawable(R.drawable.wodeweizhi));
		        myLocationOverlay.setData(mActivity.myLoc);
		        my_gp = new GeoPoint((int)(mActivity.myLoc.latitude* 1e6), (int)(mActivity.myLoc.longitude *  1e6));
		        
			    bmv_nearby.getOverlays().add(myLocationOverlay);
			    //Log.e(TAG, "latSpanE6:" + result_span.latSpanE6 + " lonSpanE6:" + result_span.longSpanE6);

				lv_nearby.reset();
				if(mActivity.list_nearby_data.size()>0){
					mActivity.nearby_objs = MapItemHelper.showNearByItems(mActivity, handler, getResources(), bmv_nearby, myLocationOverlay,  mActivity.list_nearby_data, true);
					Log.e(TAG, "list_nearby_data size:" + mActivity.list_nearby_data.size());
					MyDiary[] its = (MyDiary[])(mActivity.list_nearby_data.toArray(new MyDiary[0]));
					lv_nearby.loadMore(its);
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
			        Requester2.requestNearDiary(handler, mActivity.myLoc, ""+mActivity.nearby_video_pageno, "50", getPicWidth() + "", "");
				}
				break;
			case MyMapView.HANDLER_FLAG_MAPVIEW_CLICK:
				fl_activity_discover_map_tankuang.setVisibility(View.INVISIBLE);
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
								DiaryManager.getInstance().setDetailDiaryList(it.nearby_list);
								startActivity(new Intent(ZApplication.getInstance(), DiaryDetailActivity.class)
								.putExtra(DiaryDetailActivity.INTENT_ACTION_DIARY_UUID, diaryuuid));
								fl_activity_discover_map_tankuang.setVisibility(View.INVISIBLE);
							}
						}
						
					});
					list_adapter.notifyDataSetChanged();
					fl_activity_discover_map_tankuang.setVisibility(View.VISIBLE);
					//lv_info.setVisibility(View.VISIBLE);
				}
				break;
				
			}
		}catch(Exception e){
			e.printStackTrace();
		}

		return false;
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


	private int getPicWidth() {
		return (int) (0.85 * dm.widthPixels);
	}

}
