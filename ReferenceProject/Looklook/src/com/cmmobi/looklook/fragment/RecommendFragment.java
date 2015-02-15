package com.cmmobi.looklook.fragment;


import java.util.ArrayList;
import java.util.List;

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
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

import cn.zipper.framwork.core.ZApplication;

import com.baidu.mapapi.map.LocationData;
import com.baidu.mapapi.map.MyLocationOverlay;
import com.baidu.mapapi.map.OverlayItem;
import com.baidu.platform.comapi.basestruct.GeoPoint;
import com.cmmobi.looklook.R;
import com.cmmobi.looklook.activity.DiaryDetailActivity;
import com.cmmobi.looklook.activity.DiscoverMainActivity;
import com.cmmobi.looklook.activity.FriendsRecommendActivity;
import com.cmmobi.looklook.animation.AnimationHelper;
import com.cmmobi.looklook.common.adapter.MapRecommondListAdapter;
import com.cmmobi.looklook.common.gson.GsonResponse2;
import com.cmmobi.looklook.common.gson.GsonResponse2.MyDiary;
import com.cmmobi.looklook.common.gson.GsonResponse2.diaryAttach;
import com.cmmobi.looklook.common.gson.Requester2;
import com.cmmobi.looklook.common.listview.pulltorefresh.RecommendDiaryListView;
import com.cmmobi.looklook.info.profile.DiaryManager;
import com.cmmobi.looklook.map.MapItemHelper;
import com.cmmobi.looklook.map.MapItemHelper.Item;
import com.cmmobi.looklook.map.MyItemizedOverlay;
import com.cmmobi.looklook.map.MyMapView;
import com.cmmobi.looklook.map.Span;


public class RecommendFragment extends Fragment implements OnClickListener, Callback {
	private final static String TAG = "RecommondFragment";
	
	private final int HANDLER_FLAG_INIT_VIEW = 0xf1000002;
	
	FriendsRecommendActivity mActivity;
	private Handler handler;

	// UI components
	MyMapView bmv_recommond;
	ListView lv_info;
	ImageView iv_close;
	//ImageView iv_reloc;
	RecommendDiaryListView lv_recommend;
	FrameLayout fl_activity_discover_main_recommend;
	RelativeLayout fl_activity_discover_map_tankuang;
	
	//anim
	AnimationHelper animHelper;
	

	//baidu map
	//private MapController mMapController = null;
	MyLocationOverlay myLocationOverlay = null;
	GeoPoint my_gp; 
	GeoPoint gp_center;
	Span result_span;
	List<OverlayItem> mGeoList;
	//Item[] objs;
	MapRecommondListAdapter list_adapter;
	
	protected DisplayMetrics dm = new DisplayMetrics();
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		dm = getActivity().getApplicationContext().getResources().getDisplayMetrics();
	}

	public RecommendFragment() {
			handler = new Handler(this);
	}

	// Called once the Fragment has been created in order for it to
	// create its user interface.
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		Log.e(TAG, "RecommondFragment - onCreateView");
		// Create, or inflate the Fragment's UI, and return it.
		// If this Fragment has no UI then return null.
		View view = inflater.inflate(R.layout.fragment_discover_recommond, container, false);

		bmv_recommond = (MyMapView)view.findViewById(R.id.bmv_activity_discover_main_recommend);
		lv_info = (ListView)view.findViewById(R.id.lv_activity_discover_main_list);
		//iv_reloc = (ImageView)view.findViewById(R.id.iv_activity_discover_main_reloc);
		iv_close = (ImageView)view.findViewById(R.id.iv_activity_discover_tankuang_close);
		lv_recommend = (RecommendDiaryListView)view.findViewById(R.id.lv_activity_discover_main_recommond);
		fl_activity_discover_main_recommend = (FrameLayout)view.findViewById(R.id.fl__activity_discover_main_recommend);
		fl_activity_discover_map_tankuang = (RelativeLayout)view.findViewById(R.id.fl_activity_discover_map_tankuang);
        // Since we are caching large views, we want to keep their cache
        // between each animation
		iv_close.setOnClickListener(this);
		bmv_recommond.setClickable(false);
		bmv_recommond.setHandler(handler);
		bmv_recommond.setDoubleClickZooming(false);
		//mMapController = bmv_recommond.getController();
		myLocationOverlay = new MyLocationOverlay(bmv_recommond);
	    mGeoList = new ArrayList<OverlayItem>();    

		mActivity.setFragHandler(handler);
		
		/*lv_recommend.setOnRefreshListener(new AbsRefreshView.OnRefreshListener() {
			
			@Override
			public void onRefresh() {
				mActivity.recommend_video_pageno=1;
				mActivity.list_recommend_data.clear();
				Requester2.requestDiaryRecommend(handler, "", "", "10");
//				lv_recommend.reset();
			}
			
			@Override
			public void onMore() {
				if(!mActivity.recommend_more){
					lv_recommend.loadDateError();
					Log.d(TAG, "no more");
					
				}else{
					Requester2.requestDiaryRecommend(handler, "", "", "10");
				}
			}

			@Override
			public void onAutoScroll(int l, int t, int oldl, int oldt) {
//				System.out.println("onAutoScroll");
				
			}
			
			
		});*/
		
		//初始化界面，若已有数据则直接加载，否则发起网络请求
		handler.sendEmptyMessage(HANDLER_FLAG_INIT_VIEW);
		
		return view;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		Log.e(TAG, "RecommondFragment - onActivityCreated");
/*		mNext.setOnClickListener(new OnClickListener() {});*/
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);

		Log.e(TAG, "RecommendFragment - onAttach");

		try {
			mActivity = (FriendsRecommendActivity) activity;
			// onGotoPageListener = (OnGotoPageListener) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString() + " must implement OnGotoPageListener");
		}
	}
	
    @Override
	public void onPause() {

        super.onPause();
        bmv_recommond.onPause();
    }
    
    @Override
    public void onViewStateRestored(Bundle inState){
    	super.onViewStateRestored(inState);
    	if(inState!=null){
        	bmv_recommond.onRestoreInstanceState(inState);
    	}

    }
    
    @Override
    public void onSaveInstanceState(Bundle outState){
    	bmv_recommond.onSaveInstanceState(outState);
    	super.onViewStateRestored(outState);

    }
    
    @Override
	public void onResume() {

        super.onResume();
        bmv_recommond.onResume();
        handler.sendEmptyMessageDelayed(MyMapView.HANDLER_FLAG_MAPVIEW_UPDATE, 500);
    }
	
	
    @Override
	public void onDestroyView() {

        super.onDestroyView();
        bmv_recommond.destroy();
    }

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch(v.getId()){
/*		case R.id.iv_activity_discover_main_reloc:
			result_span = MapItemHelper.calcMyDiarySpan(mActivity.list_recommend_data);
			//mMapController.zoomToSpan(result_span.latSpanE6, result_span.longSpanE6);
	        //mMapController.setCenter(my_gp);
			mMapController.animateTo(my_gp);
			break;*/
		case R.id.iv_activity_discover_tankuang_close:
			fl_activity_discover_map_tankuang.setVisibility(View.INVISIBLE);
			break;
		}
		
	}
	


	@Override
	public boolean handleMessage(Message msg) {
		LocationData myLoc;
		// TODO Auto-generated method stub
		try{
			switch(msg.what){
			case Requester2.RESPONSE_TYPE_DIARY_RECOMMEND:
				if(mActivity.myLoc==null || mActivity.list_recommend_data==null || myLocationOverlay==null){
					break; //myLoc = new LocationData();
				}
				GsonResponse2.diaryrecommendResponse obj_recommend = (GsonResponse2.diaryrecommendResponse) msg.obj;
				if(obj_recommend!=null && "0".equals(obj_recommend.status) && obj_recommend.diaries.length>0){
					//obj.items;
					//模拟数据
					//obj_recommend.diaries = simulateDiaries();
					for(MyDiary item : obj_recommend.diaries){
						if(item.latitude==null || item.latitude.equals("") || item.longitude==null || item.longitude.equals("")){
							Log.e(TAG, "MyDiary latitude or longitude is null");
							continue;
							
						}
						mActivity.list_recommend_data.add(item);
					}
					
					mActivity.recommend_objs = MapItemHelper.showRecommondItems(mActivity, handler, getResources(), bmv_recommond, mActivity.list_recommend_data, true);

					lv_recommend.loadMore(obj_recommend.diaries);
				}else{
					mActivity.recommend_more = false;
					lv_recommend.loadDateError();
				}

				break;
			case DiscoverMainActivity.HANDLER_FLAG_ACTIVITY_SHOWLIST:
				//直接设置Visibility
				lv_recommend.setVisibility(View.VISIBLE);
				fl_activity_discover_main_recommend.setVisibility(View.GONE);	
				//fl_activity_discover_main_recommond.setPersistentDrawingCache(ViewGroup.PERSISTENT_ANIMATION_CACHE);
				//animHelper.applyRotation(1, 0, 90);
/*		        Animation up = AnimationUtils.loadAnimation(mActivity, R.anim.slide_up);
		        AnimationListener listener_up = new AnimationListener(){

					@Override
					public void onAnimationStart(Animation animation) {
						// TODO Auto-generated method stub
						
					}

					@Override
					public void onAnimationEnd(Animation animation) {
						// TODO Auto-generated method stub
				        
				        fl_activity_discover_main_recommond.setVisibility(View.GONE);
					}

					@Override
					public void onAnimationRepeat(Animation animation) {
						// TODO Auto-generated method stub
						
					}
		        	
		        };
				up.setAnimationListener(listener_up );
		        
				lv_recommend.setVisibility(View.VISIBLE);
		        lv_recommend.startAnimation(up);*/

		        
				break;
			case DiscoverMainActivity.HANDLER_FLAG_ACTIVITY_HIDELIST:
/*				lv_recommend.setVisibility(View.GONE);
				fl_activity_discover_main_recommond.setVisibility(View.VISIBLE);*/
				//animHelper.applyRotation(-1, 180, 90);
		        Animation down = AnimationUtils.loadAnimation(mActivity, R.anim.slide_down);
		        AnimationListener listener_down = new AnimationListener(){

					@Override
					public void onAnimationStart(Animation animation) {
						// TODO Auto-generated method stub
						fl_activity_discover_main_recommend.setVisibility(View.VISIBLE);
					}

					@Override
					public void onAnimationEnd(Animation animation) {
						// TODO Auto-generated method stub
//				        lv_recommend.setVisibility(View.GONE);
				        
					}

					@Override
					public void onAnimationRepeat(Animation animation) {
						// TODO Auto-generated method stub
						
					}
		        	
		        };
				down.setAnimationListener(listener_down );
//		        lv_recommend.startAnimation(down);

				break;
			case DiscoverMainActivity.HANDLER_FLAG_ACTIVITY_RESPONSE:
				myLoc = mActivity.myLoc;
				if(myLoc==null || mActivity.list_recommend_data==null){
					break; //myLoc = new LocationData();
				}
				
				Log.e(TAG, "HANDLER_FLAG_ACTIVITY_RESPONSE loc latitude:" + myLoc.latitude + " longitude:" + myLoc.longitude);
		        
				//myLocationOverlay.enableCompass();
		        //myLocationOverlay.setData(mActivity.myLoc);
		        //my_gp = new GeoPoint((int)(myLoc.latitude* 1e6), (int)(myLoc.longitude *  1e6));
		        
			    //bmv_recommond.getOverlays().add(myLocationOverlay);
			    //Log.e(TAG, "latSpanE6:" + result_span.latSpanE6 + " lonSpanE6:" + result_span.longSpanE6);


				mActivity.recommend_objs = MapItemHelper.showRecommondItems(mActivity, handler, getResources(), bmv_recommond, mActivity.list_recommend_data, true);
		        //bmv_recommond.refresh();
				break;
			case HANDLER_FLAG_INIT_VIEW:
				fl_activity_discover_map_tankuang.setVisibility(View.INVISIBLE);
	/*			myLoc = mActivity.myLoc;
				if(myLoc==null || mActivity.list_recommend_data==null){
					break; //myLoc = new LocationData();
				}*/
				
				//Log.e(TAG, "HANDLER_FLAG_INIT_VIEW loc latitude:" + myLoc.latitude + " longitude:" + myLoc.longitude);
		        
							//bmv_recommond.refresh();
				lv_recommend.reset();
				if(mActivity.list_recommend_data.size()>0){
					mActivity.recommend_objs = MapItemHelper.showRecommondItems(mActivity, handler, getResources(), bmv_recommond, mActivity.list_recommend_data, true);
					Log.e(TAG, "list_recommend_data size:" + mActivity.list_recommend_data.size());
					MyDiary[] its = (MyDiary[])(mActivity.list_recommend_data.toArray(new MyDiary[0]));
					lv_recommend.loadMore(its);
				}else{
					bmv_recommond.getOverlays().clear();
					try {
						//中国地图
						GeoPoint gp_center = new GeoPoint((int)(35 * 1e6), (int)(104 * 1e6));
						bmv_recommond.getController().setZoom((float) 4.6);
						bmv_recommond.getController().setCenter(gp_center);
						bmv_recommond.getController().animateTo(gp_center);
					} catch (NullPointerException e) {
						Log.e(TAG, "Baidu Map Internal NullPointer");
						Toast.makeText(mActivity, "百度地图缩放出错，请刷新重试！", Toast.LENGTH_LONG).show();
			    	}
					/*mActivity.nearby_video_pageno = 1;*/
					mActivity.recommend_video_pageno = 1; 
			        Requester2.requestDiaryRecommend(handler, "1", "10", getPicWidth() + "", "");
				}
				break;
			case MyMapView.HANDLER_FLAG_MAPVIEW_CLICK:
				fl_activity_discover_map_tankuang.setVisibility(View.INVISIBLE);
				//lv_info.setVisibility(View.INVISIBLE);
				break;
				
			case MyMapView.HANDLER_FLAG_MAPVIEW_UPDATE:
				Log.e(TAG, "handler update: level:" + bmv_recommond.getZoomLevel());
				mActivity.recommend_objs = MapItemHelper.showRecommondItems(mActivity, handler, getResources(), bmv_recommond, mActivity.list_recommend_data, false);
				break;
			case MyItemizedOverlay.HANDLER_FLAG_MAP_ITEM_TAP:
				int index = (Integer) msg.obj;
				if(mActivity.recommend_objs==null || mActivity.recommend_objs[index]==null){
					break;
				}
				final Item it = mActivity.recommend_objs[index];
				if(it.type==2){
					list_adapter = new MapRecommondListAdapter(mActivity, handler,
							R.layout.row_list_diary_recommend, R.id.tv_row_list_diary_nick,
							it.recommond_list, fl_activity_discover_map_tankuang);
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
								DiaryManager.getInstance().setDetailDiaryList(it.recommond_list);
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
