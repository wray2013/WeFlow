package com.cmmobi.looklook.map;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;

import com.baidu.mapapi.map.ItemizedOverlay;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.OverlayItem;
import com.baidu.mapapi.map.PopupClickListener;
import com.baidu.mapapi.map.PopupOverlay;
import com.baidu.platform.comapi.basestruct.GeoPoint;

public class MyItemizedOverlay extends ItemizedOverlay<OverlayItem> {
	public final static int HANDLER_FLAG_MAP_ITEM_TAP = 0xc7123401;
	
	public List<OverlayItem> mGeoList = new ArrayList<OverlayItem>();
	private Context mContext = null;
	private Handler handler = null;
	static PopupOverlay pop = null;

	public MyItemizedOverlay(Handler handler, MapView mMapView, Drawable marker, Context context) {
		super(marker, mMapView);
		this.mContext = context;
		this.handler = handler;
		pop = new PopupOverlay(mMapView, new PopupClickListener() {

			/*
			 * @Override public void onClickedPopup() { Log.d("hjtest  ",
			 * "clickpop"); }
			 */
			@Override
			public void onClickedPopup(int arg0) {
				// TODO Auto-generated method stub

			}
		});
		//populate();

	}
	
	public void boundCenterEx(OverlayItem item) {
		boundCenter(item);
	}

	protected boolean onTap(int index) {
/*
		Toast.makeText(this.mContext, mGeoList.get(index).getTitle(),
				Toast.LENGTH_SHORT).show();*/
		Message msg = handler.obtainMessage(HANDLER_FLAG_MAP_ITEM_TAP, index);
		//msg.sendToTarget();
		
		handler.sendMessageDelayed(msg, 100);
		super.onTap(index);
		return false;
	}

	public boolean onTap(GeoPoint pt, MapView mapView) {
		if (pop != null) {
			pop.hidePop();
		}
		super.onTap(pt, mapView);
		return false;
	}

//	@Override
//	protected OverlayItem createItem(int i) {
//		return mGeoList.get(i);
//	}

//	@Override
//	public int size() {
//		return mGeoList.size();
//	}

	public void addItem(OverlayItem item) {
		mGeoList.add(item);
//		populate();
	}

	public void removeItem(int index) {
		mGeoList.remove(index);
//		populate();
	}
	
	public boolean removeAll() {
		mGeoList.clear();
//		populate();
		return true;
	}

}
