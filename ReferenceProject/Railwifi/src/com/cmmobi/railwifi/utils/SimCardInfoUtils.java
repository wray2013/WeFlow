package com.cmmobi.railwifi.utils;


import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.telephony.TelephonyManager;

/**
 * 需要加入权限<uses-permission android:name="android.permission.READ_PHONE_STATE"/>
 * @author Sunshine
 *
 */
public final class SimCardInfoUtils {

	private static TelephonyManager telephonyManager = SystemServiceUtils.getTelephonyManager();
	private static WifiManager wifiMgr  = SystemServiceUtils.getWifiManager();

	private SimCardInfoUtils() {
	}

	/**
	 * 获取当前设置的电话号码;
	 */
	public static String getNativePhoneNumber() {
		return telephonyManager.getLine1Number();
	}

	/**
	 * 获取手机服务商信息;
	 * @return
	 */
	public static String getProvidersName() {
		String IMSI = getIMSI();
		String ProvidersName = null;
		// IMSI号前面3位460是国家，紧接着后面2位00 02是中国移动，01是中国联通，03是中国电信。
		if (IMSI.startsWith("46000") || IMSI.startsWith("46002")) {
			ProvidersName = "中国移动";
		} else if (IMSI.startsWith("46001")) {
			ProvidersName = "中国联通";
		} else if (IMSI.startsWith("46003")) {
			ProvidersName = "中国电信";
		}
		return ProvidersName;
	}
	
	public static String getSoftwareVersion() {
		return telephonyManager.getDeviceSoftwareVersion();
	}
	
	public static String getIMEI2() {
		return telephonyManager.getSimSerialNumber();
	}
	
	public static String getIMEI() {
		return telephonyManager.getDeviceId();
	}
	
	public static String getIMSI() {
		return telephonyManager.getSubscriberId(); // 国际移动用户识别码(Sim卡唯一编号);
	}
	
	public static String getDeviceBrand() {
		return android.os.Build.BRAND;
	}
	
	public static String getDeviceName() {
		return android.os.Build.MODEL;
	}
	
	public static String getSystemSDKLevel() {
		return android.os.Build.VERSION.SDK;
	}
	
	public static String getSystemReleaseVersion() {
		return android.os.Build.VERSION.RELEASE;
	}

	public static String getDeviceMac(){
		String macAddress = null;
		WifiInfo info = (null == wifiMgr ? null : wifiMgr.getConnectionInfo());
		if (null != info) {
			   macAddress = info.getMacAddress();
		}
		
		return macAddress;
	}
	
	public static String getDeviceIP(){
		String IPAddress = null;
		WifiInfo info = (null == wifiMgr ? null : wifiMgr.getConnectionInfo());
		if (null != info) {
			IPAddress  = Integer.toString(info.getIpAddress());
		}
		
		return IPAddress;
	}
}
