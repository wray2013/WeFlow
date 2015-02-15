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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.baidu.mapapi.search.MKAddrInfo;
import com.baidu.platform.comapi.basestruct.GeoPoint;
import com.baidu.platform.comapi.map.Projection;
import com.cmmobi.looklook.R;
import com.cmmobi.looklook.activity.EditMediaDetailActivity;
import com.cmmobi.looklook.activity.FrenquentlyPositionActivity;
import com.cmmobi.looklook.common.adapter.PositionListAdapter;
import com.cmmobi.looklook.common.utils.CmmobiClickAgentWrapper;
import com.cmmobi.looklook.info.location.MyAddressInfo;
import com.cmmobi.looklook.info.location.OnPoiSearchListener;
import com.cmmobi.looklook.info.profile.CommonInfo;
import com.cmmobi.looklook.map.MyMapView;

public class EditMediaPositionFragment  extends Fragment implements OnClickListener,OnPoiSearchListener, Callback{

	private EditMediaDetailActivity mActivity;
	private final String TAG = "EditMediaPositionFragment";
	private ListView listView;
	private List<String> posList = new ArrayList<String>();
	RelativeLayout frequentlyPosLayout = null;
	private ImageView preImgView = null;
	private PositionListAdapter mAdapter;
	private ImageView confirmBtn = null;
	private ImageView cancleBtn = null;
	private Handler handler = null;
	private boolean isMoveEnabled = false;
	public static final int FRRENQUENTLYPOSITION = 0x0013;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		CmmobiClickAgentWrapper.onEvent(mActivity, "my_edit_de", "3");
		CmmobiClickAgentWrapper.onEventBegin(mActivity, "my_edit_de", "3");
		View view = inflater.inflate(R.layout.fragment_edit_photo_detail_position, container, false);
		listView = (ListView) view.findViewById(R.id.lv_pos_listview);
	    
		frequentlyPosLayout = (RelativeLayout) view.findViewById(R.id.rl_activity_edit_photo_detail_frequentlypos);
		frequentlyPosLayout.setOnClickListener(this);
		confirmBtn = (ImageView) view.findViewById(R.id.iv_edit_photo_position_yes);
		cancleBtn = (ImageView) view.findViewById(R.id.iv_edit_photo_position_no);

		confirmBtn.setOnClickListener(this);
		cancleBtn.setOnClickListener(this);
		posList.clear();
		handler = new Handler(this);
		mActivity.mMapView.setHandler(handler);
		mActivity.mMapView.refresh();
		
		if (mActivity.myLoc.accuracy < 200) {
			isMoveEnabled = false;
			mActivity.mMapView.getController().setScrollGesturesEnabled(false);
		} else {
			isMoveEnabled = true;
			mActivity.mMapView.getController().setScrollGesturesEnabled(true);
		}
		Projection projection = mActivity.mMapView.getProjection();  
		int radius = (int)projection.metersToEquatorPixels(mActivity.myLoc.accuracy);
		mActivity.myPositionLayout.setRadius(radius);
		mActivity.myPositionLayout.invalidate();
		
		Log.d(TAG,"mActivity.myLoc.accuracy = " + mActivity.myLoc.accuracy + " pixel distance = " + projection.metersToEquatorPixels(mActivity.myLoc.accuracy));
	    MyAddressInfo.getInstance(mActivity).setOnPoiSearchListener(this);
	    MyAddressInfo.getInstance(mActivity).nearPoiSearch(new GeoPoint((int)(mActivity.myLoc.latitude* 1e6), (int)(mActivity.myLoc.longitude *  1e6)),mActivity.myLoc.accuracy);
	    return view;
	}
	

	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		switch(arg0.getId()) {
		case R.id.rl_activity_edit_photo_detail_frequentlypos:
			startActivityForResult(new Intent(mActivity,FrenquentlyPositionActivity.class), FRRENQUENTLYPOSITION);
			break;
		case R.id.rl_list_item_position:
			Log.d(TAG,"EditMediaPosition rl_list_item_position in");
			if (preImgView != null) {
				preImgView.setImageResource(R.drawable.xuanzhong_1);
			}
			ImageView imgView = (ImageView) arg0.findViewById(R.id.iv_edit_position_selected);
			imgView.setImageResource(R.drawable.xuanzhong_2);
			
			preImgView = imgView;
			
			if (arg0.getTag() != null) {
			    PositionListAdapter.ViewHolder holder = (PositionListAdapter.ViewHolder)arg0.getTag();
			    mAdapter.checkedPosition = holder.position;
			    Log.d(TAG,"EditMediaPosition rl_list_item_position position = " + holder.position);
			}
			break;
		case R.id.iv_edit_photo_position_yes:
			CmmobiClickAgentWrapper.onEventEnd(mActivity, "my_edit_de", "3");
			mActivity.goToPage(EditMediaDetailActivity.FRAGMENT_DETAIL_MAIN_VIEW, false);
			mActivity.setMapViewVisibility(View.GONE);
			mActivity.myPositionLayout.setVisibility(View.GONE);
			mActivity.myPositionTv.setVisibility(View.GONE);
			
			if (mAdapter != null && mAdapter.getCheckPositionStr() != null && !"".equals(mAdapter.getCheckPositionStr())) {
				mActivity.setPositionText(mAdapter.getCheckPositionStr());
				mActivity.isDiaryDetailChanged = true;
				if (mActivity.getMyDiary() != null) {
					mActivity.getMyDiary().position = mAdapter.getCheckPositionStr();
				}
				ArrayList<String> freqPosList = CommonInfo.getInstance().frequentpos;
				Log.d(TAG,"getCheckPositionStr = " + mAdapter.getCheckPositionStr());
				if (freqPosList.contains(mAdapter.getCheckPositionStr())) {
					freqPosList.remove(mAdapter.getCheckPositionStr());
					freqPosList.add(0, mAdapter.getCheckPositionStr());
				} else {
					freqPosList.add(0,mAdapter.getCheckPositionStr());
				}
			}
			break;
		case R.id.iv_edit_photo_position_no:
			CmmobiClickAgentWrapper.onEventEnd(mActivity, "my_edit_de", "3");
			mActivity.goToPage(EditMediaDetailActivity.FRAGMENT_DETAIL_MAIN_VIEW, false);
			mActivity.setMapViewVisibility(View.GONE);
			mActivity.myPositionLayout.setVisibility(View.GONE);
			mActivity.myPositionTv.setVisibility(View.GONE);
			break;
		}
	}
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);

		Log.d(TAG, "EditMediaPositionFragment - onAttach");

		try {
			mActivity = (EditMediaDetailActivity) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString() + " must implement OnGotoPageListener");
		}
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		Log.d(TAG,"onActivityRresult in requestCode = " + requestCode + " resultCode = " + resultCode + " data = " + data);
		if(requestCode==FRRENQUENTLYPOSITION  && data != null){
			String posStr = data.getStringExtra("position");
			Log.d(TAG,"onActivityResult posStr = " + posStr);
			if (posStr != null && !"".equals(posStr)) {
				mActivity.goToPage(EditMediaDetailActivity.FRAGMENT_DETAIL_MAIN_VIEW, false);
				mActivity.setMapViewVisibility(View.GONE);
				
				mActivity.setPositionText(posStr);
				mActivity.isDiaryDetailChanged = true;
				mActivity.myPositionLayout.setVisibility(View.GONE);
				mActivity.myPositionTv.setVisibility(View.GONE);
				if (mActivity.getMyDiary() != null) {
					mActivity.getMyDiary().position = posStr;
				}
				ArrayList<String> freqPosList = CommonInfo.getInstance().frequentpos;
				Log.d(TAG,"freqPosList = " + freqPosList);
				if (freqPosList.contains(posStr)) {
					Log.d(TAG,"onActivityResult if");
					freqPosList.remove(posStr);
					freqPosList.add(0, posStr);
				} else {
					Log.d(TAG,"onActivityResult else " + mAdapter.getCheckPositionStr());
					freqPosList.add(0,posStr);
				}
			}
		}
//		super.onActivityResult(requestCode, resultCode, data);
	}


	@Override
	public void onPoiSearch(List<String> poiList) {
		// TODO Auto-generated method stub
		Log.d(TAG,"onPoiSearch in");
		posList.clear();
		posList.addAll(poiList);
		mAdapter = new PositionListAdapter(mActivity, R.layout.list_item_poi_position, R.id.tv_position, posList);
	    mAdapter.setClickListener(this);
		listView.setAdapter(mAdapter);
		mAdapter.notifyDataSetChanged();
	}

	@Override
	public void onAddrSearch(MKAddrInfo res) {
		// TODO Auto-generated method stub
		if (res == null) {
			return;
		}
		StringBuffer sb = new StringBuffer();  
        // 经纬度所对应的位置   
        sb.append(res.strAddr).append("\n");
        posList.add(0,res.strAddr);
        Log.d(TAG,"onGetAddrResult addr = " + sb.toString() + " size = " + posList.size());
        if (posList.size() == 1) {
			mAdapter = new PositionListAdapter(mActivity, R.layout.list_item_poi_position, R.id.tv_position, posList);
		    mAdapter.setClickListener(this);
			listView.setAdapter(mAdapter);
        }
		mAdapter.notifyDataSetChanged();

        mActivity.myPositionTv.setVisibility(View.VISIBLE);
        mActivity.myPositionTv.setText(res.strAddr);
        
        Log.d(TAG,"********onGetAddrResult addr = " + sb.toString() + " size = " + posList.size());
	}


	@Override
	public boolean handleMessage(Message msg) {
		// TODO Auto-generated method stubs
		switch(msg.what) {
		case MyMapView.HANDLER_FLAG_MAPVIEW_DOWN:
			break;
		case MyMapView.HANDLER_FLAG_MAPVIEW_MOVE:
			if (!isMoveEnabled) {
				break;
			}
			Log.d(TAG,"MyMapView.HANDLER_FLAG_MAPVIEW_MOVE");
			GeoPoint centerPoint = mActivity.mMapView.getMapCenter();
			MyAddressInfo.getInstance(mActivity).nearPoiSearch(centerPoint,(float)mActivity.myLoc.accuracy);
			break;
		case MyMapView.HANDLER_FLAG_MAPVIEW_UP:
			if (!isMoveEnabled) {
				break;
			}
			Log.d(TAG,"MyMapView.HANDLER_FLAG_MAPVIEW_UP");
			GeoPoint centerPointUp = mActivity.mMapView.getMapCenter();
			MyAddressInfo.getInstance(mActivity).nearPoiSearch(centerPointUp,(float)mActivity.myLoc.accuracy);
			break;
		}
		
		return false;
	}
}
