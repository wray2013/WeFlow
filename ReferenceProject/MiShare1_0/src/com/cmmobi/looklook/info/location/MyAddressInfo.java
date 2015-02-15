package com.cmmobi.looklook.info.location;

import java.util.ArrayList;
import java.util.HashSet;

import android.content.Context;
import android.util.Log;
import cn.zhugefubin.maptool.ConverterTool;
import cn.zhugefubin.maptool.Point;
import cn.zipper.framwork.core.ZApplication;
import cn.zipper.framwork.utils.ZStringUtils;

import com.baidu.mapapi.BMapManager;
import com.baidu.mapapi.search.MKAddrInfo;
import com.baidu.mapapi.search.MKBusLineResult;
import com.baidu.mapapi.search.MKDrivingRouteResult;
import com.baidu.mapapi.search.MKGeocoderAddressComponent;
import com.baidu.mapapi.search.MKPoiInfo;
import com.baidu.mapapi.search.MKPoiResult;
import com.baidu.mapapi.search.MKSearch;
import com.baidu.mapapi.search.MKSearchListener;
import com.baidu.mapapi.search.MKSuggestionResult;
import com.baidu.mapapi.search.MKTransitRouteResult;
import com.baidu.mapapi.search.MKWalkingRouteResult;
import com.baidu.platform.comapi.basestruct.GeoPoint;
import com.cmmobi.looklook.info.profile.CommonInfo;
import com.cmmobi.looklook.info.weather.AddressCodeParser;

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
    private ArrayList<OnPoiSearchListener> poiSearchListenerList = new ArrayList<OnPoiSearchListener>();
    private String [] poiStrList = {"美食","酒店","休闲娱乐","景点","交通设施","生活服务"};
    private int index = 0;
    private GeoPoint point = null;
    ConverterTool ct;
    private ArrayList<POIAddressInfo> poiList = new ArrayList<POIAddressInfo>();
	
	
	public class MyMKSearchListener implements MKSearchListener {

		@Override
		public void onGetAddrResult(MKAddrInfo res, int error) {
			// TODO Auto-generated method stub
			Log.d(TAG,"onGetAddrResult in error = " + error);
			if (res != null && res.strAddr != null) {
				CommonInfo.getInstance().positionStr = res.strAddr;
				MKGeocoderAddressComponent addr = res.addressComponents;//获取当前城市
				if(addr != null && ZStringUtils.emptyToNull(addr.city) != null
						&& ZStringUtils.emptyToNull(addr.district) != null) {
					String city = addr.city;
					String district = addr.district;
					String addrcode = Convert2Code(city, district);
					CommonInfo.getInstance().addressCode = ZStringUtils.nullToEmpty(addrcode);
				}
			}
			
			if (poiSearchListener != null) {
				poiSearchListener.onAddrSearch(res);
			}
			
			if (poiSearchListenerList.size() > 0) {
				for (OnPoiSearchListener listener:poiSearchListenerList) {
					listener.onAddrSearch(res);
				}
			}
			
			if (error != 0) {
                String str = String.format("错误号：%d", error);
                return;
            }
			
			Log.d(TAG,"onGetAddresResult positionStr = " + res.strAddr);

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
			Log.d(TAG,"onGetPoiResult in point = " + point + " index = " + index);
			if (index == getPoiSize() -1) {
				mkSearch.reverseGeocode(point);
				HashSet h = new HashSet(poiList);  
				poiList.clear();  
				poiList.addAll(h); 
				if (poiSearchListener != null) {
					poiSearchListener.onPoiSearch(poiList);
				}
				if (poiSearchListenerList.size() > 0) {
					for (OnPoiSearchListener listener:poiSearchListenerList) {
						listener.onPoiSearch(poiList);
					}
				}
			}
			if (error != 0 || res == null) {
                Log.d(TAG,"onGetPoiResult 解析失败 + error = " + error);
                if (index < poiStrList.length - 1) {
                	index++;;
                	mkSearch.poiSearchNearBy(poiStrList[index], point, 200);
                }
                return;
            }
//			if (poiSearchListener != null) {
//				poiSearchListener.onPoiSearch(res);
//			}
            if (res != null && res.getCurrentNumPois() > 0) {
//                GeoPoint ptGeo = res.getAllPoi().get(0).pt;
//                
//                String strInfo = String.format("纬度：%f 经度：%f\r\n", ptGeo.getLatitudeE6()/1e6, 
//                        ptGeo.getLongitudeE6()/1e6);
//                strInfo += "\r\n附近有：";
                int size = res.getAllPoi().size();
                for (int i = 0; i < size; i++) {
                	MKPoiInfo poiinfo = res.getAllPoi().get(i);
//                    strInfo += (poiinfo.name + ";");
                    POIAddressInfo addrInfo = new POIAddressInfo();
                    addrInfo.position = poiinfo.name;
                    addrInfo.latitude = String.valueOf(poiinfo.pt.getLatitudeE6() / (float)1e6);
                    addrInfo.longitude = String.valueOf(poiinfo.pt.getLongitudeE6() / (float)1e6);
                    if (!poiList.contains(addrInfo)) {
                    	poiList.add(addrInfo);
                    }
                }
                if (index < poiStrList.length - 1) {
                	index++;;
                	mkSearch.poiSearchNearBy(poiStrList[index], point, 200);
                }
//                Log.d(TAG,"onGetPoiResult = strInfo " + strInfo);
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
		ct = new ConverterTool();
		init();
	}
	
	private void init() {
		BMapManager mapMgr = new BMapManager(mContext);
		mkSearch.init(mapMgr, new MyMKSearchListener());
		MKSearch.setPoiPageCapacity(10);
	}
	
	public void addListener(OnPoiSearchListener listener) {
		poiSearchListenerList.add(listener);
	}
	
	public void removeListener(OnPoiSearchListener listener) {
		poiSearchListenerList.remove(listener);
	}
	
	public void clearListenerList() {
		poiSearchListenerList.clear();
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
		Point p = ct.GG2BD((double) (point.getLongitudeE6() / 1e6), (double) (point.getLatitudeE6() / 1e6));
		GeoPoint pt = new GeoPoint((int)(p.getLatitude() * 1e6), (int)(p.getLongitude() * 1e6));
		if (mkSearch != null) {
		    mkSearch.reverseGeocode(pt);	
		}
	}
	
	public void searchGeo(String addr,String city) {
		if (mkSearch != null) {
		    mkSearch.geocode(addr, city);
		}
	}
	
	public void nearPoiSearch(GeoPoint point,float accuracy) {
		Log.d(TAG,"nearPoiSearch in");
		Point p = ct.GG2BD((double) (point.getLongitudeE6() / 1e6), (double) (point.getLatitudeE6() / 1e6));
		GeoPoint pt = new GeoPoint((int)(p.getLatitude() * 1e6), (int)(p.getLongitude() * 1e6));
		this.point = pt;
		index = 0;
		if (mkSearch != null) {
			poiList.clear();
		    mkSearch.poiSearchNearBy(poiStrList[index], pt, 200/*(int)accuracy*/);	
		}
	}
	
	public void nearPoiSearch(GeoPoint point) {
		this.point = point;
		index = 0;
		if (mkSearch != null) {
			poiList.clear();
		    mkSearch.poiSearchNearBy(poiStrList[index], point, 200/*(int)accuracy*/);	
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
	
	//转换城市定位为国际码
	private String Convert2Code(String city, String district) {
		AddressCodeParser parser = new AddressCodeParser();
		String code = null;
		try {
			code = parser.parse2(mContext, "addresscode.xml",city, district);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			Log.e(TAG, "read citycode config error！");
			e.printStackTrace();
		}
		Log.d(TAG, "City " + city + " Code is " + code);
		return code;
	}
}
