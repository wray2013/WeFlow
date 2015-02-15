package cn.zipper.framwork.core;

import android.app.DownloadManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.SensorManager;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.wifi.WifiManager;
import android.telephony.TelephonyManager;
import android.view.LayoutInflater;

public final class ZSystemService {
	
	private ZSystemService() {
	}

	public static SensorManager getSensorManager() {
		return (SensorManager) ZApplication.getInstance().getSystemService(Context.SENSOR_SERVICE);
	}
	
	public static TelephonyManager getTelephonyManager() {
		return (TelephonyManager) ZApplication.getInstance().getSystemService(Context.TELEPHONY_SERVICE);
	}
	
	public static ConnectivityManager getConnectivityManager() {
		return (ConnectivityManager) ZApplication.getInstance().getSystemService(Context.CONNECTIVITY_SERVICE);
	}
	
	public static LocationManager getLocationManager() {
		return (LocationManager) ZApplication.getInstance().getSystemService(Context.LOCATION_SERVICE);
	}
	
	public static WifiManager getWifiManager() {
		return (WifiManager) ZApplication.getInstance().getSystemService(Context.WIFI_SERVICE);
	}
	
	public static LayoutInflater getLayoutInflater() {
		return (LayoutInflater) ZApplication.getInstance().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}
	
	public static DownloadManager getDownloadManager() {
		return (DownloadManager) ZApplication.getInstance().getSystemService(Context.DOWNLOAD_SERVICE);
	}
	
	public static PackageManager getPackageManager() {
		return ZApplication.getInstance().getPackageManager();
	}
	
	

}
