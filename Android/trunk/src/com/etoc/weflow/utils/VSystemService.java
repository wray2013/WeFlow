package com.etoc.weflow.utils;

import com.etoc.weflow.WeFlowApplication;

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
		return (SensorManager) WeFlowApplication.getAppInstance().getSystemService(Context.SENSOR_SERVICE);
	}
	
	public static TelephonyManager getTelephonyManager() {
		return (TelephonyManager) WeFlowApplication.getAppInstance().getSystemService(Context.TELEPHONY_SERVICE);
	}
	
	public static ConnectivityManager getConnectivityManager() {
		return (ConnectivityManager) WeFlowApplication.getAppInstance().getSystemService(Context.CONNECTIVITY_SERVICE);
	}
	
	public static LocationManager getLocationManager() {
		return (LocationManager) WeFlowApplication.getAppInstance().getSystemService(Context.LOCATION_SERVICE);
	}
	
	public static WifiManager getWifiManager() {
		return (WifiManager) WeFlowApplication.getAppInstance().getSystemService(Context.WIFI_SERVICE);
	}
	
	public static LayoutInflater getLayoutInflater() {
		return (LayoutInflater) WeFlowApplication.getAppInstance().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}
	
	public static DownloadManager getDownloadManager() {
		return (DownloadManager) WeFlowApplication.getAppInstance().getSystemService(Context.DOWNLOAD_SERVICE);
	}
	
	public static PackageManager getPackageManager() {
		return WeFlowApplication.getAppInstance().getPackageManager();
	}
	
	

}
