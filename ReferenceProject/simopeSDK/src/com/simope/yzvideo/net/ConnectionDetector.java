package com.simope.yzvideo.net;


import java.io.IOException;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;


public class ConnectionDetector {
	private Context _context;
	private static ConnectionDetector  connectionDetector;

	private boolean is_wifi=false;
	
	public boolean isIs_wifi() {
		return is_wifi;
	}

	public void setIs_wifi(boolean is_wifi) {
		this.is_wifi = is_wifi;
	}



	public static ConnectionDetector getConnectionDetector(Context context){
		if(connectionDetector==null){
			connectionDetector=new ConnectionDetector(context);
		}
		return connectionDetector;
	}
		
	public ConnectionDetector(Context context) {
		
		this._context = context;
	}
	
	/** 
	* @author 
	* 获取当前的网络状态 -1：没有网络 1：WIFI网络2：wap网络3：net网络 
	* @param context 
	* @return 
	*/ 
	private static final int CMNET=3;
	private static final int CMWAP=2;
	private static final int WIFI=1;
	public int getAPNType(Context context){ 
	int netType = -1; 
	ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE); 
	NetworkInfo networkInfo = connMgr.getActiveNetworkInfo(); 
		if(networkInfo==null){
			return netType; 
		} 
	  int nType = networkInfo.getType(); 
		if(nType==ConnectivityManager.TYPE_MOBILE){ 
			if(networkInfo.getExtraInfo().toLowerCase().equals("cmnet")){ 
				netType = CMNET; 
			} 
			else{ 
				netType = CMWAP; 
			} 
		} 
		else if(nType==ConnectivityManager.TYPE_WIFI){ 
			netType = WIFI; 
		} 
	  return netType; 
	}
	
	
	public  void chooseNet(){
		if(isConnectingToInternet()){
			ConnectivityManager connectivity = (ConnectivityManager) _context.getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo activeNetInfo = connectivity.getActiveNetworkInfo();//获取网络的连接情况
			if(activeNetInfo.getType()==ConnectivityManager.TYPE_WIFI){				
				is_wifi=true;
			}else if(activeNetInfo.getType()==ConnectivityManager.TYPE_MOBILE) {
			//判断3G网				
				is_wifi=false;
			}				
		}else {
			is_wifi=false;
		}
	}
	
	

	public boolean isConnectingToInternet() {
		
		
		ConnectivityManager connectivity = (ConnectivityManager) 
		_context.getSystemService(Context.CONNECTIVITY_SERVICE);
		
		if (connectivity != null)
		{
			NetworkInfo[] info = connectivity.getAllNetworkInfo();

			if (info != null)

				for (int i = 0; i < info.length; i++){

					if (info[i].getState() == NetworkInfo.State.CONNECTED)

					{
						return true;
					}
				}

		}		
		return false;
	}
	
	
	public boolean isWifiConnected(Context context) { 
		if (context != null) { 
			ConnectivityManager mConnectivityManager = (ConnectivityManager) context 
													.getSystemService(Context.CONNECTIVITY_SERVICE); 
			NetworkInfo mWiFiNetworkInfo = mConnectivityManager 
													.getNetworkInfo(ConnectivityManager.TYPE_WIFI); 
				if (mWiFiNetworkInfo != null) { 
					return mWiFiNetworkInfo.isAvailable(); 
				} 
		} 
			return false; 
	}
	
	
	public boolean isMobileConnected(Context context) { 
		if (context != null) { 
			ConnectivityManager mConnectivityManager = (ConnectivityManager) context 
													.getSystemService(Context.CONNECTIVITY_SERVICE); 
			NetworkInfo mMobileNetworkInfo = mConnectivityManager 
													.getNetworkInfo(ConnectivityManager.TYPE_MOBILE); 
			if (mMobileNetworkInfo != null) { 
				return mMobileNetworkInfo.isAvailable(); 
			} 
		} 
			return false; 
	}
	

	public boolean ServiceIsOpen(){
		boolean IsOpen = false;
		int s = 1;
			 try {
				Process p = Runtime.getRuntime().exec("ping -c 1 -w 100 " + "www.simope.com");
				 int status = p.waitFor();
				 s=status;
				 if (status == 0) {
				        IsOpen= true;
				    } else {
				    	IsOpen=  false;
				   }
			} catch (IOException e) {			
				e.printStackTrace();
			} catch (InterruptedException e) {				
				e.printStackTrace();
			}
			 return IsOpen;
	}
	
	 public static String pingIpAddr() {
	        String pingInfo = "ok";
	            try {
	            String ipAddress = "123.123.1.13";
	            Process p = Runtime.getRuntime().exec("ping -c 1 -w 100 " + ipAddress);
	            int status = p.waitFor();
	            if (status == 0) {
	                return pingInfo;
	            } else {
	                    pingInfo = "错误:服务器没开启";
	            }
	        } catch (IOException e) {
	                pingInfo = "错误:服务器IO错误";
	        } catch (InterruptedException e) {
	                pingInfo = "错误:网络拥堵";
	        }
	        return pingInfo;
	    }
}
