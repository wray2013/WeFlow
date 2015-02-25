package com.etoc.weflowdemo.util;

import com.etoc.weflowdemo.MainApplication;

import android.annotation.SuppressLint;
import android.app.DownloadManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.SensorManager;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.wifi.WifiManager;
import android.telephony.TelephonyManager;
import android.view.LayoutInflater;


@SuppressLint("InlinedApi")
public final class VSystemService {
	
	private VSystemService() {
	}

	public static SensorManager getSensorManager() {
		return (SensorManager) MainApplication.getAppInstance().getSystemService(Context.SENSOR_SERVICE);
	}
	
	public static TelephonyManager getTelephonyManager() {
		return (TelephonyManager) MainApplication.getAppInstance().getSystemService(Context.TELEPHONY_SERVICE);
	}
	
	public static ConnectivityManager getConnectivityManager() {
		return (ConnectivityManager) MainApplication.getAppInstance().getSystemService(Context.CONNECTIVITY_SERVICE);
	}
	
	public static LocationManager getLocationManager() {
		return (LocationManager) MainApplication.getAppInstance().getSystemService(Context.LOCATION_SERVICE);
	}
	
	public static WifiManager getWifiManager() {
		return (WifiManager) MainApplication.getAppInstance().getSystemService(Context.WIFI_SERVICE);
	}
	
	public static LayoutInflater getLayoutInflater() {
		return (LayoutInflater) MainApplication.getAppInstance().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}
	
	public static DownloadManager getDownloadManager() {
		return (DownloadManager) MainApplication.getAppInstance().getSystemService(Context.DOWNLOAD_SERVICE);
	}
	
	public static PackageManager getPackageManager() {
		return MainApplication.getAppInstance().getPackageManager();
	}
	
	

}
