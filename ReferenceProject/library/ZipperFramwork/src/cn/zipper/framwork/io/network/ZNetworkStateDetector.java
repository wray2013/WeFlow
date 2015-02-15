package cn.zipper.framwork.io.network;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.List;

import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.telephony.TelephonyManager;
import cn.zipper.framwork.core.ZLog;
import cn.zipper.framwork.core.ZSystemService;

public final class ZNetworkStateDetector {
	
	private ZNetworkStateDetector() {
	}
	
	public static boolean hasActiveNetwork() {
		return getActiveNetworkInfo() != null;
	}
	
	public static NetworkInfo getActiveNetworkInfo() {
		return ZSystemService.getConnectivityManager().getActiveNetworkInfo();
	}
	
	/**
	 * 获取ip地址(ipv4:192.168.0.1);
	 * @return
	 */
	public static String getIpV4Address() {
		String ip = null;
        try {
            for (Enumeration<NetworkInterface> enumeration = NetworkInterface.getNetworkInterfaces(); enumeration.hasMoreElements();) {
                NetworkInterface intf = enumeration.nextElement();
                
                for (Enumeration<InetAddress> enumeration2 = intf.getInetAddresses(); enumeration2.hasMoreElements();) {
                    InetAddress inetAddress = enumeration2.nextElement();
                    if (!inetAddress.isLoopbackAddress()) {
                        ip = inetAddress.getHostAddress();
                        if (ip.contains(".")) {
                        	break;
                        }
                    }
                }
            }
        } catch (Exception e) {
        	e.printStackTrace();
        }
        return ip;
    }
	
	/**
	 * 获取ip地址(ipv6: ffcc:c0de:c900:aa0c:wlan);
	 * @return
	 */
	public static String getIpV6Address() {
		String ip = null;
        try {
            for (Enumeration<NetworkInterface> enumeration = NetworkInterface.getNetworkInterfaces(); enumeration.hasMoreElements();) {
                NetworkInterface intf = enumeration.nextElement();
                
                for (Enumeration<InetAddress> enumeration2 = intf.getInetAddresses(); enumeration2.hasMoreElements();) {
                    InetAddress inetAddress = enumeration2.nextElement();
                    if (!inetAddress.isLoopbackAddress()) {
                        ip = inetAddress.getHostAddress();
                        if (ip.contains(":")) {
                        	break;
                        }
                    }
                }
            }
        } catch (Exception e) {
        	e.printStackTrace();
        }
        return ip;
    }
	
	/**
	 * 获取mac地址(网卡物理地址);
	 * @return
	 */
	public static String getMacAddress() {
		return ZSystemService.getWifiManager().getConnectionInfo().getMacAddress();
	}

	/**
	 * 网络是否可用(非飞行模式 或 数据通信功能打开);
	 * @return
	 */
	public static boolean isAvailable() {
		NetworkInfo networkInfo = getActiveNetworkInfo();
		boolean b = false;
		if (networkInfo != null && networkInfo.isAvailable()) {
			b = true;
		}
		return b;
	}
	
	/**
	 * 网络是否已经连接(是否可以直接通信);
	 * @return
	 */
	public static boolean isConnected() {
		NetworkInfo networkInfo = getActiveNetworkInfo();
		boolean b = false;
		if (networkInfo != null && networkInfo.isConnected()) {
			b = true;
		}
		return b;
	}
	
	/**
	 * 网络是否已经连接或正在连接;
	 * @return
	 */
	public static boolean isConnectedOrConnecting() {
		NetworkInfo networkInfo = getActiveNetworkInfo();
		boolean b = false;
		if (networkInfo != null && networkInfo.isConnectedOrConnecting()) {
			b = true;
		}
		return b;
	}
	
	/**
	 * 是否支持失效备援;
	 * @return
	 */
	public static boolean isFailover() {
		NetworkInfo networkInfo = getActiveNetworkInfo();
		boolean b = false;
		if (networkInfo != null && networkInfo.isFailover()) {
			b = true;
		}
		return b;
	}
	
	/**
	 * 是否正在漫游;
	 * @return
	 */
	public static boolean isRoaming() {
		NetworkInfo networkInfo = getActiveNetworkInfo();
		boolean b = false;
		if (networkInfo != null && networkInfo.isRoaming()) {
			b = true;
		}
		return b;
	}
	
	/**
	 * 当前连接是否是wifi方式;
	 * @return
	 */
	public static boolean isWifi() {
		NetworkInfo networkInfo = getActiveNetworkInfo();
		boolean b = false;
		if (networkInfo != null && networkInfo.getType() == ConnectivityManager.TYPE_WIFI) {  
			b = true;
        }  
		return b;
    }
	
	/**
	 * 当前连接是否是移动网络方式(GPRS, 3G等);
	 * @return
	 */
	public static boolean isMobile() {
		NetworkInfo networkInfo = getActiveNetworkInfo();
		boolean b = false;
		if (networkInfo != null && networkInfo.getType() == ConnectivityManager.TYPE_MOBILE) {  
			b = true;
		}  
		return b;
	}

	/**
	 * 检测卫星网络的类型; 
	 * 
	 * @return
	 * TelephonyManager.NETWORK_TYPE_CDMA
	 * TelephonyManager.NETWORK_TYPE_EDGE
	 * TelephonyManager.NETWORK_TYPE_EVDO_0 
	 * TelephonyManager.NETWORK_TYPE_EVDO_A
	 * TelephonyManager.NETWORK_TYPE_GPRS
	 * TelephonyManager.NETWORK_TYPE_HSDPA
	 * TelephonyManager.NETWORK_TYPE_HSPA
	 * TelephonyManager.NETWORK_TYPE_HSUPA
	 * TelephonyManager.NETWORK_TYPE_UMTS
	 * 
	 * 没验证过: 联通的3G为UMTS或HSDPA，移动和联通的2G为GPRS或EGDE，电信的2G为CDMA，电信的3G为EVDO;
	 */
	public static int getMobileType() {
		NetworkInfo networkInfo = getActiveNetworkInfo();
		int type = 0;
		if (networkInfo != null) {
			type = networkInfo.getSubtype();
		}
		return type;
	}
	
	
	
	/**
	 * GPS功能是否已开启;
	 * @return
	 */
	public static boolean isGpsOpened() {  
		LocationManager locationManager = ZSystemService.getLocationManager();
		List<String> accessibleProviders = locationManager.getProviders(true);  
		return accessibleProviders != null && accessibleProviders.size() > 0;
    }
	
	public static boolean isGpsOpened2() {  
		LocationManager locationManager = ZSystemService.getLocationManager();
		return locationManager.isProviderEnabled(android.location.LocationManager.GPS_PROVIDER);
    }
	
	/**
	 * 是否支持蓝牙;
	 * @return
	 */
	public static boolean isBluetoothDeviceExist() {
		return BluetoothAdapter.getDefaultAdapter() != null;
	}
	
	/**
	 * 蓝牙是否开启(如果设备不支持蓝牙, 则总是返回false)
	 * @return
	 */
	public static boolean isBluetoothEnable() {
		return isBluetoothDeviceExist() && BluetoothAdapter.getDefaultAdapter().isEnabled();
	}
	
}
