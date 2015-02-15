package com.cmmobi.looklook.info.weather;

import java.util.Date;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.NetworkInfo.State;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.HandlerThread;
import android.os.Message;
import android.os.Parcelable;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import cn.zipper.framwork.core.ZApplication;
import cn.zipper.framwork.core.ZBroadcastReceiver;
import cn.zipper.framwork.io.network.ZNetworkStateDetector;

import com.baidu.mapapi.map.LocationData;
import com.baidu.mapapi.search.MKAddrInfo;
import com.baidu.mapapi.search.MKGeocoderAddressComponent;
import com.baidu.platform.comapi.basestruct.GeoPoint;
import com.cmmobi.looklook.R;
import com.cmmobi.looklook.common.gson.GsonResponse3;
import com.cmmobi.looklook.common.gson.Requester3;
import com.cmmobi.looklook.common.service.CoreService;
import com.cmmobi.looklook.info.location.MyAddressInfo;
import com.cmmobi.looklook.info.location.MyLocationInfo;
import com.cmmobi.looklook.info.location.OnLocationUpdateListener;
import com.cmmobi.looklook.info.location.OnPoiSearchListener;
import com.cmmobi.looklook.info.location.POIAddressInfo;
import com.cmmobi.looklook.info.profile.AccountInfo;
import com.cmmobi.looklook.info.profile.ActiveAccount;
import com.cmmobi.looklook.info.profile.CommonInfo;
import com.cmmobi.looklook.info.profile.TimeHelper;
import com.cmmobi.looklook.prompt.Prompt;

/**
 *  {@literal 用于获取天气信息的独类，从CommonInfo中读取天气（在网络获取失败的情况下） 
 *  ， 同时根据定位更改，更新CommonInfo中的数据} 
 *  
 *  @author wangrui
 * 
 * */
public class MyWeatherInfo implements Callback,OnPoiSearchListener,OnLocationUpdateListener {
	private final String TAG = "MyWeatherInfo";
	//private final int HANDLER_WIFI_CONNECTED = 0x19861214;
	public static final String ACTION_UPDATE_WEATHERINFO = "action_update_weatherinfo";
	private static MyWeatherInfo ins = null;
	private MyWeather myWeather; //天气信息
	private Context mContext;
	private boolean isActive = false; //是否来自主动更新请求
	CommonInfo ci;
	AccountInfo ai;
	protected Handler handler;
	protected HandlerThread handlerThread;
	private long DURATION_ONE_MINUTE = 60*1000; //a minute in ms
	private long DURATION_ONE_DAY = 24*60*60*1000; //one day in ms
//	private boolean isFailedLastTime = false;
	private WifiConnectedReceiver myBroadcastReceiver;
	private AppFrontReceiver appFrontReceiver;
	
	private MyWeatherInfo(Context context) {
		if (myWeather == null) {
			myWeather = new MyWeather();
		}
		if(context!=null){
			mContext = context;
		}
		handlerThread = new HandlerThread("MyWeatherThread");
		handlerThread.start();
		handler = new Handler(handlerThread.getLooper(), this);
		init();
	}
	
	private void init() {
		
		//weather service deleted
		/*MyAddressInfo.getInstance(mContext).setOnPoiSearchListener(this);
		MyLocationInfo.getInstance(mContext).startLocating(this);*/

	    myBroadcastReceiver = new WifiConnectedReceiver();
	    IntentFilter filter = new IntentFilter();
	    filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
	    filter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
	    filter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
	    mContext.registerReceiver(myBroadcastReceiver, filter);
	    
	    appFrontReceiver = new AppFrontReceiver();
	    IntentFilter appFrontfilter = new IntentFilter();
	    appFrontfilter.addAction(CoreService.FLAG_APP_FRONT_NOTIFY);
	    LocalBroadcastManager.getInstance(mContext).registerReceiver(appFrontReceiver, appFrontfilter);

	}
	
	public static MyWeatherInfo getInstance(Context context) {
		if(ins == null){
			ins = new MyWeatherInfo(context);
		}
		return ins;
	}
	
	public MyWeather getWeather() {
//		ci = CommonInfo.getInstance();
		ai = AccountInfo.getInstance(ActiveAccount.getInstance(mContext).getLookLookID());
		return ai.myWeather;
	}
	
	public void setWeather() {
		ci = CommonInfo.getInstance();
		ai = AccountInfo.getInstance(ActiveAccount.getInstance(mContext).getLookLookID());
		ci.myWeather = myWeather;
		ai.myWeather = myWeather;
		ci.persist();
		ai.persist();
	}
	
	
	//转换城市定位为国际码
	private String Convert2Code(String city, String district) {
		// TODO:需要与RIA请求国际码参数对应
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
	
	//刷新天气信息
	public void updateWeather(boolean isactive) {
		this.isActive = isactive;
		if(isactive) {
			Log.d(TAG, "Active start locating");
			MyLocationInfo.getInstance(mContext).startLocatingActive(ins, 1000);
		}
		if(myWeather != null) {
			Requester3.getWeather(handler, myWeather.addrCode);
		}
	}
	
	@Override
	public boolean handleMessage(Message msg) {
		// TODO Auto-generated method stub
		switch (msg.what) {
		case Requester3.RESPONSE_TYPE_GET_WEATHER:
			long now = TimeHelper.getInstance().now();
//			boolean isNetError = false;
//			int lastdays  = (int) (myWeather.lastModify / DURATION_ONE_DAY);
//			int today     = (int) (now / DURATION_ONE_DAY);
//			int delta = today - lastdays;
			
//			Log.d(TAG, "lastdays = " + lastdays + ";today = " + today + ";delta = " + delta);
			Date thisDate = new Date(now);
			Date lastDate = new Date(myWeather.lastModify);
			int today = thisDate.getDate();
			int thisyear = thisDate.getYear();
			int thismouth = thisDate.getMonth();
			int lastday = lastDate.getDate();
			int lastyear = lastDate.getYear();
			int lastmouth = lastDate.getMonth();
			
			if(msg.obj!=null){
				GsonResponse3.getWeatherResponse response = (GsonResponse3.getWeatherResponse) msg.obj;
				if(response.status !=null && response.status.equals("0") 
						&& (ZNetworkStateDetector.isGpsOpened2() || ZNetworkStateDetector.isConnected())) {
					myWeather.desc = response.weather;
					myWeather.lastModify = now;
//					isFailedLastTime = false;
					setWeather();
					//发送更新天气广播
					Intent intent = new Intent(ACTION_UPDATE_WEATHERINFO);
					intent.putExtra("isActive", this.isActive);
					//LocalBroadcastManager.getInstance(mContext).sendBroadcast(intent);
					ZApplication.getInstance().sendLocalBroadcast(intent);
					break;
					//weatherUpdateListener.OnWeatherUpdate(myWeather);
				} else {
//					isNetError = true;
				}
			} else {
//				isNetError = true;
				if(this.isActive) {
					Prompt.Alert(ZApplication.getInstance().getString(R.string.prompt_network_error));
				}
			}
			Log.d(TAG, "now = " + now + ";today = " + today + ";lastday = " + lastday);
			if(thisyear != lastyear || thismouth != lastmouth || today != lastday) {
				//Toast.makeText(mContext, "改变了一天", Toast.LENGTH_LONG).show();
				myWeather.desc = null;
				myWeather.addrCode = null;
//				isFailedLastTime = true;
				setWeather();
			}
			
			//发送更新天气广播
			Intent intent = new Intent(ACTION_UPDATE_WEATHERINFO);
			intent.putExtra("isActive", this.isActive);
			ZApplication.getInstance().sendLocalBroadcast(intent);
			this.isActive = false;
			break;
		}
		return false;
	}

	private boolean hasChanged(String l, String c) {
		if(l == null) {
			return true;
		}
		return !l.equals(c);
	}	
	
	
	public class WifiConnectedReceiver extends ZBroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			// 这个监听wifi的连接状态即是否连上了一个有效无线路由，
			// 当上边广播的状态是WifiManager.WIFI_STATE_DISABLING，和WIFI_STATE_DISABLED的时候，根本不会接到这个广播。
			// 在上边广播接到广播是WifiManager.WIFI_STATE_ENABLED状态的同时也会接到这个广播，
			// 当然刚打开wifi肯定还没有连接到有效的无线
			if (WifiManager.NETWORK_STATE_CHANGED_ACTION.equals(intent.getAction())) {
				Parcelable parcelableExtra = intent
						.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
				if (null != parcelableExtra) {
					NetworkInfo networkInfo = (NetworkInfo) parcelableExtra;
					State state = networkInfo.getState();
					boolean isConnected = state == State.CONNECTED;// 当然，这边可以更精确的确定状态
					if (isConnected) {
						Log.d(TAG, "WIFI connected, update weather!");
//						updateWeather(false);
					}
				}
			}
		}
	}
	
	// 接收APP状态变更广播
	private class AppFrontReceiver extends ZBroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			Log.d(TAG, "intent.getAction() = " + intent.getAction());
			if (CoreService.FLAG_APP_FRONT_NOTIFY.equals(intent.getAction())) {
				boolean isFront = intent.getBooleanExtra("appInFront", false);
				if(isFront) {
					Log.d(TAG, "app is in front, start locating");
					MyLocationInfo.getInstance(mContext).startLocating(ins);
				} else {
					Log.d(TAG, "app is not in front, stop locating");
					MyLocationInfo.getInstance(mContext).stopLocating();
				}
			}
		}
	}
	
	@Override
	public void onPoiSearch(List<POIAddressInfo> res) {
		// TODO Auto-generated method stub
		
		
	}

	@Override
	public void onAddrSearch(MKAddrInfo res) {
		// TODO Auto-generated method stub
		MKGeocoderAddressComponent addr = res.addressComponents;//获取当前城市
		String city = addr.city;
		String district = addr.district;
		String addrcode = Convert2Code(city,district);
		if (/* isFailedLastTime || */hasChanged(myWeather.addrCode, addrcode)) { //城市改变,尝试更新天气
			myWeather.addrCode = addrcode;
			myWeather.city = city;
			myWeather.district = district;
			updateWeather(false);
			Log.d(TAG, "get current city "+ city + ", district "+ addr.district +", code " + myWeather.addrCode);
		}
	}

	@Override
	public void OnLocationUpdate(LocationData locData) { //1分钟刷新一次地理位置
		long now = TimeHelper.getInstance().now();
		if(myWeather.lastModify + DURATION_ONE_MINUTE > now && !isActive) {
			return;
		}
//		Log.d(TAG, "Need to update the weather, last[" + myWeather.lastModify
//				+ "] now[" + now + "]");
		myWeather.lastModify = now;
		LocationData loc = MyLocationInfo.getInstance(mContext).getLocation();
		MyAddressInfo.getInstance(mContext).reverseGeocode(
				new GeoPoint((int) (loc.latitude * 1e6),
						(int) (loc.longitude * 1e6)));
	}
	
}
