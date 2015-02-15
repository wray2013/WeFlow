package cn.zipper.framwork.io.network;

import java.net.InetSocketAddress;
import java.net.Proxy;

import org.apache.http.HttpHost;

import android.database.Cursor;
import android.net.Uri;
import android.text.TextUtils;
import cn.zipper.framwork.core.ZApplication;

public final class ZProxy {
	
	private String host;
	private int port;
	
	public ZProxy(String host, int port) {
		this.host = host;
		this.port = port;
	}
	
	public ZProxy(String host, String port) {
		this(host, Integer.valueOf(port));
	}
	
	public Proxy toProxyObject() {
		return new Proxy(Proxy.Type.HTTP, new InetSocketAddress(host, port));
	}
	
	public HttpHost toHttpHostObject() {
		return new HttpHost(host, port);
	}
	
	public static ZProxy getZProxy() {
		ZProxy proxy = null;
		
		try {
			if (ZNetworkStateDetector.hasActiveNetwork() && ZNetworkStateDetector.isMobile()) {
				String host = null;
				String port = "80";
				
				//直接获取;
				host = android.net.Proxy.getDefaultHost();
		    	port = String.valueOf(android.net.Proxy.getDefaultPort());
		    	
		    	//从数据库获取;
		    	if (host == null || host.length() <= 3) {// 3: IP地址中的三个分隔点;
		        	Uri prefer_apn = Uri.parse("content://telephony/carriers/preferapn");
		            Cursor cursor = ZApplication.getInstance().getContentResolver().query(prefer_apn, null, null, null, null);
		            if (cursor != null) {
		            	if (cursor.moveToFirst()) {
		            		host = cursor.getString(cursor.getColumnIndex("proxy"));
			                port = cursor.getString(cursor.getColumnIndex("port"));
		            	}
		                cursor.close();
		            }
		        }
		        
		    	//从网络信息获取;
		        if (host == null || host.length() <= 3) {
		            String extra = ZNetworkStateDetector.getActiveNetworkInfo().getExtraInfo();
		            if (!TextUtils.isEmpty(extra)) {
		            	extra = extra.toLowerCase();
		                if (extra.contains("cmnet") || extra.contains("ctnet") 
		                        || extra.contains("uninet") || extra.contains("3gnet")
		                        || extra.contains("#777")) {
		                	
		                } else if (extra.contains("cmwap") || extra.contains("uniwap") 
		                        || extra.contains("3gwap")) {
		                	host = "10.0.0.172";
		                } else if (extra.contains("ctwap")) {
		                	host = "10.0.0.200";
		                }
		            }
		        }
		        
		        if (host != null && host.length() > 3 && !TextUtils.isEmpty(port)) {
		        	proxy = new ZProxy(host, port);
		        }
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
        return proxy;
    }

}
