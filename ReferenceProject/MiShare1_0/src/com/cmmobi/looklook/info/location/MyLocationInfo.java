package com.cmmobi.looklook.info.location;

import android.content.Context;
import android.util.Log;
import cn.zipper.framwork.core.ZApplication;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.LocationData;
import com.baidu.platform.comapi.basestruct.GeoPoint;
import com.cmmobi.looklook.info.profile.CommonInfo;
import com.cmmobi.looklook.prompt.Prompt;


/**
 *  {@literal 用于获取地理坐标的独类，从CommonInfo中读取坐标（在网络获取失败的情况下） 
 *  ， 同时采用百度locSDK定位，更新CommonInfo中的数据} 
 *  
 *  @author zhangwei
 * 
 * */
public class MyLocationInfo {
	private final String TAG = "MyLocationInfo";
	private static MyLocationInfo ins=null;
	private Context context;
	CommonInfo ci;
	private OnLocationUpdateListener LocListener;
	
	
	//定位相关
	private  LocationClientOption option;
	private  LocationClient mLocClient;
	private  MyLocationListenner myListener;;
/*	private  MyLocation locData = null;*/
	private LocationData locData = null;
	private boolean isNeedSearchPosition  = true;
	
	/**
     * 监听函数，有新位置的时候，格式化成字符串，输出到屏幕中
     */
	public class  MyLocationListenner implements BDLocationListener {
        @Override
        public void onReceiveLocation(BDLocation location) {
            if (location == null)
                return ;
            
/*            locData.latitude = location.getLatitude();
            locData.longitude = location.getLongitude();
            locData.lastModify = TimeHelper.getInstance().now();*/
            
            locData.latitude = location.getLatitude();
            locData.longitude = location.getLongitude();
            locData.direction = 2.0f;
            locData.accuracy = location.getRadius();
            locData.direction = location.getDerect();
            
            if (isNeedSearchPosition) {
            	GeoPoint pt = new GeoPoint((int)(locData.latitude* 1e6), (int)(locData.longitude *  1e6));
            	MyAddressInfo.getInstance(context).reverseGeocode(pt);
            }
            isNeedSearchPosition = !isNeedSearchPosition;
            
            ci.myLoc = locData;
            Log.d(TAG, "latitude:" + locData.latitude + " longitude:" + locData.longitude);
            
            if(LocListener!=null){
            	LocListener.OnLocationUpdate(locData);
            }
            
            /*if (option.getScanSpan() != 60000) {
	            option.setOpenGps(true);          //打开gps
	            option.setCoorType("gcj02");     //设置坐标类型
	            option.setScanSpan(60000);
	            mLocClient.setLocOption(option);
            }*/
        }
        
        public void onReceivePoi(BDLocation poiLocation) {
            if (poiLocation == null){
                return ;
            }
        }
    }
    
	
	private MyLocationInfo() {
		context = ZApplication.getInstance();
		locData = new LocationData();

		myListener = new MyLocationListenner();

		mLocClient = new LocationClient(context);
		option = new LocationClientOption();
		mLocClient.registerLocationListener( myListener );  
		init();
		
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				while(true) {
					try {
						if (Prompt.isAppOnFront()) {
							startLocating(null);
						} else {
							stopLocating();
						}
						Thread.sleep(60000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		}).start();
		Log.d(TAG,"MyLocationInfo in");
	}
	
	private void init(){
		ci = CommonInfo.getInstance();

        option.setOpenGps(true);          //打开gps
        option.setCoorType("gcj02");     //设置坐标类型
        option.setScanSpan(60000);
        Log.d(TAG,"setCoorType gcj02");
        
        mLocClient.setLocOption(option);
        
//    	mLocClient.start();
	}
	
	public static MyLocationInfo getInstance(Context c){
		if(ins==null){
			ins = new MyLocationInfo();
		}
		
		if(c!=null){
			ins.context = c;
		}

		return ins;
	}
	
	public LocationData getLocation(){
		ci = CommonInfo.getInstance();
		return ci.myLoc;
	}
	
	public void saveLocation(){
		ci = CommonInfo.getInstance();
		//ci.persist();
	}
	
	/**
	 *  停止定位服务, activity的onPause中取消监听
	 * 
	 * */
	public void stopLocating(){
		if(mLocClient!=null && mLocClient.isStarted()){
			mLocClient.stop();
		}
	}

	/**
	 *  开始定位服务, activity的onResume中设置监听，用于回调
	 * 
	 * */
	public void startLocating(OnLocationUpdateListener loclistener){
		LocListener = loclistener;
		if (!mLocClient.isStarted()) {
			mLocClient.start();
		}
	}
	
	/**
	 *  开始定位服务, activity的onResume中设置监听，用于回调
	 * 
	 * */
	public void startLocatingActive(OnLocationUpdateListener loclistener, int wait){
		ci = CommonInfo.getInstance();

        option.setOpenGps(true);          //打开gps
        option.setCoorType("gcj02");     //设置坐标类型
        option.setScanSpan(wait);
        
//        mLocClient.registerLocationListener( myListener );  
        mLocClient.setLocOption(option);
    	mLocClient.start();
        mLocClient.requestLocation();
		LocListener = loclistener;
	}
}
