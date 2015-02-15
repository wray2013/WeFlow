package com.cmmobi.looklook.info.location;

import java.util.ArrayList;

import android.content.Context;
import android.util.Log;
import cn.zipper.framwork.core.ZApplication;

import com.baidu.mapapi.BMapManager;
import com.baidu.mapapi.search.MKAddrInfo;
import com.baidu.mapapi.search.MKBusLineResult;
import com.baidu.mapapi.search.MKDrivingRouteResult;
import com.baidu.mapapi.search.MKPoiResult;
import com.baidu.mapapi.search.MKSearch;
import com.baidu.mapapi.search.MKSearchListener;
import com.baidu.mapapi.search.MKSuggestionResult;
import com.baidu.mapapi.search.MKTransitRouteResult;
import com.baidu.mapapi.search.MKWalkingRouteResult;
import com.baidu.platform.comapi.basestruct.GeoPoint;
import com.cmmobi.looklook.info.profile.CommonInfo;

/**
 * 获取地址信息
 * @author wangjiaming
 * @email wangjm@cmmobi.com
 */
public class MyAddressInfo {
	private final String TAG = "MyAddressInfo";
	private static MyAddressInfo ins=null;
	private Context mContext;
	private MKSearch mkSearch = null;
    private OnPoiSearchListener poiSearchListener = null;
    private String [] poiStrList = {"美食","酒店","休闲娱乐","景点","交通设施","生活服务"};
    private int index = 0;
    private GeoPoint point = null;
    private ArrayList<String> poiList = new ArrayList<String>();
	
	
	public class MyMKSearchListener implements MKSearchListener {

		@Override
		public void onGetAddrResult(MKAddrInfo res, int error) {
			// TODO Auto-generated method stub
			Log.d(TAG,"onGetAddresResult in");
			if (error != 0) {
                String str = String.format("错误号：%d", error);
                return;
            }
			
			if (poiSearchListener != null) {
				poiSearchListener.onAddrSearch(res);
			}
			
			if (res != null) {
				CommonInfo.getInstance().positionStr = res.strAddr;
			}

            /*StringBuffer sb = new StringBuffer();  
            // 经纬度所对应的位置   
            sb.append(res.strAddr).append("\n");  
  
            // 判断该地址附近是否有POI（Point of Interest,即兴趣点）   
            if (null != res.poiList) {  
                // 遍历所有的兴趣点信息   
                for (MKPoiInfo poiInfo : res.poiList) {  
                    sb.append("----------------------------------------").append("\n");  
                    sb.append("名称：").append(poiInfo.name).append("\n");  
                    sb.append("地址：").append(poiInfo.address).append("\n");  
                    sb.append("经度：").append(poiInfo.pt.getLongitudeE6() / 1000000.0f).append("\n");  
                    sb.append("纬度：").append(poiInfo.pt.getLatitudeE6() / 1000000.0f).append("\n");  
                    sb.append("电话：").append(poiInfo.phoneNum).append("\n");  
                    sb.append("邮编：").append(poiInfo.postCode).append("\n");  
                    // poi类型，0：普通点，1：公交站，2：公交线路，3：地铁站，4：地铁线路   
                    sb.append("类型：").append(poiInfo.ePoiType).append("\n");  
                }  
            }  
            Log.d(TAG,"onGetAddrResult addr = " + sb.toString());*/
		}

		@Override
		public void onGetBusDetailResult(MKBusLineResult arg0, int arg1) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onGetDrivingRouteResult(MKDrivingRouteResult arg0, int arg1) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onGetPoiDetailSearchResult(int arg0, int arg1) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onGetPoiResult(MKPoiResult res,  int type, int error) {
			// TODO Auto-generated method stub
			Log.d(TAG,"onGetPoiResult in");
			if (index == getPoiSize() -1) {
				mkSearch.reverseGeocode(point);	
				poiSearchListener.onPoiSearch(poiList);
			}
			if (error != 0 || res == null) {
                Log.d(TAG,"onGetPoiResult 解析失败 + error = " + error);
                if (index < poiStrList.length - 1) {
                	index++;;
                	mkSearch.poiSearchNearBy(poiStrList[index], point, 500);
                }
                return;
            }
//			if (poiSearchListener != null) {
//				poiSearchListener.onPoiSearch(res);
//			}
            if (res != null && res.getCurrentNumPois() > 0) {
                GeoPoint ptGeo = res.getAllPoi().get(0).pt;
                
                String strInfo = String.format("纬度：%f 经度：%f\r\n", ptGeo.getLatitudeE6()/1e6, 
                        ptGeo.getLongitudeE6()/1e6);
                strInfo += "\r\n附近有：";
                for (int i = 0; i < res.getAllPoi().size(); i++) {
                    strInfo += (res.getAllPoi().get(i).name + ";");
                    poiList.add(res.getAllPoi().get(i).name);
                }
                if (index < poiStrList.length - 1) {
                	index++;;
                	mkSearch.poiSearchNearBy(poiStrList[index], point, 500);
                }
                Log.d(TAG,"onGetPoiResult = strInfo " + strInfo);
            }
		}

		@Override
		public void onGetSuggestionResult(MKSuggestionResult arg0, int arg1) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onGetTransitRouteResult(MKTransitRouteResult arg0, int arg1) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onGetWalkingRouteResult(MKWalkingRouteResult arg0, int arg1) {
			// TODO Auto-generated method stub
			
		}
	}
	
	private MyAddressInfo() {
		mContext = ZApplication.getInstance();
		mkSearch = new MKSearch();
		init();
	}
	
	private void init() {
		BMapManager mapMgr = new BMapManager(mContext);
		mkSearch.init(mapMgr, new MyMKSearchListener());
		MKSearch.setPoiPageCapacity(10);
	}
	
	public static MyAddressInfo getInstance(Context context) {
		if(ins==null){
			ins = new MyAddressInfo();
		}
		
		if(context!=null){
			ins.mContext = context;
		}

		return ins;
	}
	
	public void reverseGeocode(GeoPoint point) {
		if (mkSearch != null) {
		    mkSearch.reverseGeocode(point);	
		}
	}
	
	public void nearPoiSearch(GeoPoint point,float accuracy) {
		Log.d(TAG,"nearPoiSearch in");
		this.point = point;
		index = 0;
		if (mkSearch != null) {
			poiList.clear();
		    mkSearch.poiSearchNearBy(poiStrList[index], point, (int)accuracy);	
		}
	}
	
	public void setOnPoiSearchListener(OnPoiSearchListener listener) {
		poiSearchListener = listener;
	}
	
	public int getIndex() {
		return index;
	}
	
	public int getPoiSize() {
		return poiStrList.length;
	}
	
}
