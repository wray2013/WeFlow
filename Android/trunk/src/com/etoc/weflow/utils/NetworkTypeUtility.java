package com.etoc.weflow.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

/**
 * @author gyang
 * 2013-4-15
 */
public class NetworkTypeUtility {

	/** Network type is unknown */
    public static final int NETWORK_TYPE_UNKNOWN = 0;
    /** Current network is GPRS */
    public static final int NETWORK_TYPE_GPRS = 1;
    /** Current network is EDGE */
    public static final int NETWORK_TYPE_EDGE = 2;
    /** Current network is UMTS */
    public static final int NETWORK_TYPE_UMTS = 3;
    /** Current network is CDMA: Either IS95A or IS95B*/
    public static final int NETWORK_TYPE_CDMA = 4;
    /** Current network is EVDO revision 0*/
    public static final int NETWORK_TYPE_EVDO_0 = 5;
    /** Current network is EVDO revision A*/
    public static final int NETWORK_TYPE_EVDO_A = 6;
    /** Current network is 1xRTT*/
    public static final int NETWORK_TYPE_1xRTT = 7;
    /** Current network is HSDPA */
    public static final int NETWORK_TYPE_HSDPA = 8;
    /** Current network is HSUPA */
    public static final int NETWORK_TYPE_HSUPA = 9;
    /** Current network is HSPA */
    public static final int NETWORK_TYPE_HSPA = 10;
    /** Current network is iDen */
    public static final int NETWORK_TYPE_IDEN = 11;
    /** Current network is EVDO revision B*/
    public static final int NETWORK_TYPE_EVDO_B = 12;
    /** Current network is LTE */
    public static final int NETWORK_TYPE_LTE = 13;
    /** Current network is eHRPD */
    public static final int NETWORK_TYPE_EHRPD = 14;
    /** Current network is HSPA+ */
    public static final int NETWORK_TYPE_HSPAP = 15;
    /** Current network is wifi */
    public static final int NETWORK_TYPE_WIFI = 16;
    
    
    /**
     * 检查当前网络是否是wifi
     * @param context
     * @return
     */
    public static boolean isWifi(Context context) {
    	TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
    	String net = getNetworkClass(tm, context);
    	if("wifi".equalsIgnoreCase(net)){
    		return true;
    	}
		return false;
    }
    
    
    /**
     * 返回网络类型 wifi 2g 3g 4g
     * @param context
     * @return
     */
    public static String getNetwork(Context context) {
    	TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
    	String net = getNetworkClass(tm, context);
    	return net;
    }
    
    /**
     * 返回网络类型对应的label值.
     * wifi : 1
     * 2g   : 2
     * 3g   : 3
     * 4g   : 4
     * 未知        : ""
     * @param network
     * @return
     */
    public static String getNetworkLabel(String network){
		String label = "";
		if(!TextUtils.isEmpty(network)){
			if("WIFI".equalsIgnoreCase(network)){
				label = "1";
			}else if("2G".equalsIgnoreCase(network)){
				label = "2";
			}else if("3G".equalsIgnoreCase(network)){
				label = "3";
			}else if("4G".equalsIgnoreCase(network)){
				label = "4";
			}
		}
		return label;
	}
    
    
	/**
     * Return general class of network type
     *
     */
    public static String getNetworkName(TelephonyManager tm,Context context) {
    	
    	int networkType = 0;
    	
    	ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo nInfo = cm.getActiveNetworkInfo();
		
		if(nInfo != null){
			
			String typeName = nInfo.getTypeName();
			
			if("wifi".equalsIgnoreCase(typeName)){
				networkType = NETWORK_TYPE_WIFI;
			}else{
				networkType = tm.getNetworkType();
			}
		}
		
		return _getNetworkName(networkType);
    }
    
    
    /**
     * Return general class of network type, such as "3G" or "4G". In cases
     * where classification is contentious, this method is conservative.
     *
     */
    public static String getNetworkClass(TelephonyManager tm,Context context) {
    	
    	int networkType = 0;
    	
    	ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo nInfo = cm.getActiveNetworkInfo();
		
		if(nInfo != null){
			
			String typeName = nInfo.getTypeName();
			
			if("wifi".equalsIgnoreCase(typeName)){
				networkType = NETWORK_TYPE_WIFI;
			}else{
				networkType = tm.getNetworkType();
			}
		}
		
		return _getNetworkClass(networkType);
    }
    
    
    private static String _getNetworkName(int networkType) {
        switch (networkType) {
            case NETWORK_TYPE_WIFI:
            	return "WIFI";
            case NETWORK_TYPE_GPRS:
            	return "GPRS";
            case NETWORK_TYPE_EDGE:
            	return "EDGE";
            case NETWORK_TYPE_CDMA:
            	return "CDMA";
            case NETWORK_TYPE_1xRTT:
            	return "1xRTT";
            case NETWORK_TYPE_IDEN:
            	return "IDEN";
            case NETWORK_TYPE_UMTS:
            	return "UMTS";
            case NETWORK_TYPE_EVDO_0:
            	return "EVDO_0";
            case NETWORK_TYPE_EVDO_A:
            	return "EVDO_A";
            case NETWORK_TYPE_HSDPA:
            	return "HSDPA";
            case NETWORK_TYPE_HSUPA:
            	return "HSUPA";
            case NETWORK_TYPE_HSPA:
            	return "HSPA";
            case NETWORK_TYPE_EVDO_B:
            	return "EVDO_B";
            case NETWORK_TYPE_EHRPD:
            	return "EHRPD";
            case NETWORK_TYPE_HSPAP:
            	return "HSPAP";
            case NETWORK_TYPE_LTE:
            	return "4G";
            default:
                return "UNKNOWN";
        }
    }
    
    
    private static String _getNetworkClass(int networkType) {
    	switch (networkType) {
    	case NETWORK_TYPE_WIFI:
        	return "WIFI";
    	case NETWORK_TYPE_GPRS:
    	case NETWORK_TYPE_EDGE:
    	case NETWORK_TYPE_CDMA:
    	case NETWORK_TYPE_1xRTT:
    	case NETWORK_TYPE_IDEN:
    		return "2G";
    	case NETWORK_TYPE_UMTS:
    	case NETWORK_TYPE_EVDO_0:
    	case NETWORK_TYPE_EVDO_A:
    	case NETWORK_TYPE_HSDPA:
    	case NETWORK_TYPE_HSUPA:
    	case NETWORK_TYPE_HSPA:
    	case NETWORK_TYPE_EVDO_B:
    	case NETWORK_TYPE_EHRPD:
    	case NETWORK_TYPE_HSPAP:
    		return "3G";
    	case NETWORK_TYPE_LTE:
    		return "4G";
    	default:
    		return "UNKNOWN";
    	}
    }
    
    
}
