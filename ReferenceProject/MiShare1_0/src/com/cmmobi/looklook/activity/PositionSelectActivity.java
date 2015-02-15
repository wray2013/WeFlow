package com.cmmobi.looklook.activity;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import cn.zhugefubin.maptool.ConverterTool;
import cn.zhugefubin.maptool.Point;
import cn.zipper.framwork.core.ZActivity;
import cn.zipper.framwork.utils.ZStringUtils;

import com.baidu.mapapi.map.LocationData;
import com.baidu.mapapi.search.MKAddrInfo;
import com.baidu.platform.comapi.basestruct.GeoPoint;
import com.baidu.platform.comapi.map.Projection;
import com.cmmobi.looklook.R;
import com.cmmobi.looklook.common.adapter.PositionListAdapter;
import com.cmmobi.looklook.common.view.MyPositionLayout;
import com.cmmobi.looklook.info.location.MyAddressInfo;
import com.cmmobi.looklook.info.location.MyLocationInfo;
import com.cmmobi.looklook.info.location.OnPoiSearchListener;
import com.cmmobi.looklook.info.location.POIAddressInfo;
import com.cmmobi.looklook.info.profile.CommonInfo;
import com.cmmobi.looklook.map.MyMapView;
import com.cmmobi.looklook.prompt.Prompt;

public class PositionSelectActivity extends ZActivity implements OnPoiSearchListener {

	private final String TAG = "PositionSelectActivity";
	public MyMapView mMapView = null;
	public MyLocationInfo myLocInfo;
	public LocationData myLoc;
	private double defLongitude = 0.0;
	private double defLatitude = 0.0;
	public MyPositionLayout myPositionLayout;
	RelativeLayout frequentlyPosLayout = null;
	private ImageView ivBack = null;
	private ImageView ivDone = null;
	
	private List<POIAddressInfo> posList = new ArrayList<POIAddressInfo>();
	private PositionListAdapter mAdapter;
	private ListView listView;
	public TextView myPositionTv;
	private ImageView preImgView = null;
	private final int FRENPOSITON = 0x0031;
	private DisplayMetrics dm = new DisplayMetrics();
	private String defaultPositionStr = "";
	public static final String INTENT_LONGITUDE = "longitude";
	public static final String INTENT_LATITUDE = "latitude";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.acitivty_position_select);
		
		dm = getResources().getDisplayMetrics();
		FrameLayout flMap = (FrameLayout) findViewById(R.id.fl_map);
		LinearLayout.LayoutParams params = new LayoutParams(LayoutParams.FILL_PARENT,dm.widthPixels * 5 / 8);
		flMap.setLayoutParams(params);
		ivBack = (ImageView) findViewById(R.id.iv_back);
		ivDone = (ImageView) findViewById(R.id.iv_edit_diary_save);
		ivBack.setOnClickListener(this);
		ivDone.setOnClickListener(this);
		ivDone.setEnabled(false);
		
		listView = (ListView) findViewById(R.id.lv_pos_listview);
		
		myLocInfo = MyLocationInfo.getInstance(this);
		myLoc =  myLocInfo.getLocation();
		
		mMapView = (MyMapView) findViewById(R.id.bmv_edit_position);
		
		myPositionLayout = (MyPositionLayout) findViewById(R.id.rl_edit_media_my_position);
		myPositionLayout.setWillNotDraw(false);
		myPositionTv = (TextView) findViewById(R.id.tv_edit_media_myposition);
		myPositionTv.setVisibility(View.GONE);
		
		frequentlyPosLayout = (RelativeLayout) findViewById(R.id.rl_activity_edit_frequentlypos);
		frequentlyPosLayout.setOnClickListener(this);
		
		defaultPositionStr = new String(CommonInfo.getInstance().positionStr);
		
		String latitudeStr = getIntent().getStringExtra(INTENT_LATITUDE);
		String longitudeStr = getIntent().getStringExtra(INTENT_LONGITUDE); 
		if (latitudeStr == null || longitudeStr == null) {
			if (myLoc != null) {
				defLongitude = myLoc.longitude;
				defLatitude = myLoc.latitude;
			} else {
				Prompt.Alert("无法定位到位置，请稍候再试!");
				finish();
			}
		} else {
			try {
				defLatitude = Double.parseDouble(latitudeStr);
				defLongitude = Double.parseDouble(longitudeStr);
			} catch (Exception e) {
				if (myLoc != null) {
					defLongitude = myLoc.longitude;
					defLatitude = myLoc.latitude;
				} else {
					Prompt.Alert("无法定位到位置，请稍候再试!");
					finish();
				}
			}	
		}
		
		mMapView.setVisibility(View.VISIBLE);
		myPositionLayout.setVisibility(View.VISIBLE);
		mMapView.getController().setZoom(16);
		ConverterTool ct=new ConverterTool();
		Point p = ct.GG2BD(defLongitude, defLatitude);
		GeoPoint pt = new GeoPoint((int)(p.getLatitude() * 1e6), (int)(p.getLongitude() * 1e6));
		Log.d(TAG,"myLoc.longitude = " + defLongitude + " myLoc.latitude = " + defLatitude + "latitude = " + pt.getLatitudeE6() + " longitude = " + pt.getLongitudeE6());
		
		mMapView.getController().setCenter(pt);
		mMapView.refresh();
		
//		if (myLoc.accuracy < 200) {
//			mMapView.getController().setScrollGesturesEnabled(false);
//		} else {
			mMapView.getController().setScrollGesturesEnabled(true);
			mMapView.setHandler(handler);
//		}
		
		Projection projection = mMapView.getProjection();  
//		int radius = (int)projection.metersToEquatorPixels(myLoc.accuracy);
		myPositionLayout.setRadius(0);
		myPositionLayout.invalidate();
		
		MyAddressInfo.getInstance(this).addListener(this);
	    MyAddressInfo.getInstance(this).nearPoiSearch(new GeoPoint((int)(defLatitude * 1e6), (int)(defLongitude *  1e6)),myLoc.accuracy);
	}
	
	@Override
	public boolean handleMessage(Message msg) {
		// TODO Auto-generated method stub
		switch(msg.what) {
		case MyMapView.HANDLER_FLAG_MAPVIEW_DOWN:
			break;
		case MyMapView.HANDLER_FLAG_MAPVIEW_MOVE:
			Log.d(TAG,"MyMapView.HANDLER_FLAG_MAPVIEW_MOVE");
			GeoPoint centerPoint = mMapView.getMapCenter();
			MyAddressInfo.getInstance(this).nearPoiSearch(centerPoint);
			break;
		case MyMapView.HANDLER_FLAG_MAPVIEW_UP:
			Log.d(TAG,"MyMapView.HANDLER_FLAG_MAPVIEW_UP");
			GeoPoint centerPointUp = mMapView.getMapCenter();
			MyAddressInfo.getInstance(this).nearPoiSearch(centerPointUp);
			break;
		}
		return false;
	}
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		mMapView.destroy();
		MyAddressInfo.getInstance(this).removeListener(this);
		super.onDestroy();
	}

	@Override
	public void onClick(View view) {
		// TODO Auto-generated method stub
		switch(view.getId()) {
		case R.id.iv_back:
			finish();
			break;
		case R.id.iv_edit_diary_save:
			if (mAdapter != null) {
				POIAddressInfo posInfo = mAdapter.getCheckPositionStr();
				if (posInfo != null) {
					Intent intent = new Intent();
					intent.putExtra("position", posInfo.position);
					if (!getString(R.string.position_not_visiable).equals(posInfo.position))
					{
						ConverterTool ct = new ConverterTool();
						Point p =  ct.BD2GG(Double.parseDouble(posInfo.latitude), Double.parseDouble(posInfo.longitude));
						intent.putExtra("longitude", String.valueOf(p.getLongitude()));
						intent.putExtra("latitude", String.valueOf(p.getLatitude()));
					}
					
					setResult(RESULT_OK, intent);
				}
			}
			finish();
			break;
		case R.id.rl_list_item_position:
			if (preImgView != null) {
//				preImgView.setVisibility(View.GONE);
			}
			ImageView imgView = (ImageView) view.findViewById(R.id.iv_edit_position_selected);
//			imgView.setVisibility(View.VISIBLE);
			ivDone.setEnabled(true);
			
			preImgView = imgView;
			
			if (view.getTag() != null) {
			    PositionListAdapter.ViewHolder holder = (PositionListAdapter.ViewHolder)view.getTag();
			    mAdapter.checkedPosition = holder.position;
			    Log.d(TAG,"EditMediaPosition rl_list_item_position position = " + holder.position);
			}
			mAdapter.notifyDataSetChanged();
			break;
		case R.id.rl_activity_edit_frequentlypos:
			Intent freIntent = new Intent();
			freIntent.setClass(this, FrenquentlyPositionActivity.class);
			startActivityForResult(freIntent, FRENPOSITON);
			break;
		}
	}

	@Override
	public void onPoiSearch(List<POIAddressInfo> poiList) {
		// TODO Auto-generated method stub
		posList.clear();
		POIAddressInfo inVisiablePos = new POIAddressInfo();
		inVisiablePos.position = getString(R.string.position_not_visiable);
		inVisiablePos.latitude = "";
		inVisiablePos.longitude = "";
		posList.add(inVisiablePos);
		posList.addAll(poiList);
		mAdapter = new PositionListAdapter(this, R.layout.list_item_poi_position, R.id.tv_position, posList);
	    mAdapter.setClickListener(this);
		listView.setAdapter(mAdapter);
		mAdapter.notifyDataSetChanged();
		Log.d(TAG,"onPoiSearch poiListSize = " + poiList.size());
	}

	@Override
	public void onAddrSearch(MKAddrInfo res) {
		// TODO Auto-generated method stub
		if (res == null || posList.contains(res.strAddr) || ZStringUtils.emptyToNull(res.strAddr) == null) {
			return;
		}
		StringBuffer sb = new StringBuffer();  
        // 经纬度所对应的位置   
        sb.append(res.strAddr).append("\n");
        POIAddressInfo myAddr = new POIAddressInfo();
        myAddr.position = res.strAddr;
        myAddr.latitude = String.valueOf(res.geoPt.getLatitudeE6() / (float) 1e6);
        myAddr.longitude = String.valueOf(res.geoPt.getLongitudeE6() / (float) 1e6);
        if (posList.size() > 0) {
        	posList.add(1,myAddr);
        } else {
        	posList.add(myAddr);
        }
        
        Log.d(TAG,"onGetAddrResult addr = " + sb.toString() + " size = " + posList.size());
        if (posList.size() == 1) {
			mAdapter = new PositionListAdapter(this, R.layout.list_item_poi_position, R.id.tv_position, posList);
		    mAdapter.setClickListener(this);
			listView.setAdapter(mAdapter);
        }
		mAdapter.notifyDataSetChanged();

        myPositionTv.setVisibility(View.VISIBLE);
        myPositionTv.setText(res.strAddr);
        
        CommonInfo.getInstance().positionStr = defaultPositionStr;
        
        Log.d(TAG,"********onGetAddrResult addr = " + sb.toString() + " size = " + posList.size());
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == FRENPOSITON && data != null) {
			
			String positionStr = data.getStringExtra("position");
			String longitudeStr = data.getStringExtra("longitude");
			String latitudeStr = data.getStringExtra("latitude");
			
			Intent intent = new Intent();
			intent.putExtra("position", positionStr);
			intent.putExtra("longitude", longitudeStr);
			intent.putExtra("latitude", latitudeStr);
			setResult(RESULT_OK, intent);
			finish();
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

}
