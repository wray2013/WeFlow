package cn.zipper.framwork.io.network;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Set;

import org.apache.http.client.methods.HttpPost;

public final class ZHttp2 {
	
	private static final int TIMEOUT = 60000;
	private HttpURLConnection connection = null;
	private HashMap<String, String> requestHeaders;
	
	
	public ZHttp2() {
	}
	
	public ZHttpResponse get(String url) {
		ZHttpResponse response = null;
		
		try {
			
			ZProxy proxy = ZProxy.getZProxy();
			if (proxy == null) {
				connection = (HttpURLConnection) new URL(url).openConnection();
			} else {
				connection = (HttpURLConnection) new URL(url).openConnection(proxy.toProxyObject());
			}
			
			connection.setConnectTimeout(TIMEOUT);
			connection.setReadTimeout(TIMEOUT);
			setHeaders();
			connection.connect();
			
			response = new ZHttpResponse();
			response.setResponseCode(connection.getResponseCode());
			response.setHeaders(connection.getHeaderFields());
			try {
				response.setInputStream(connection.getInputStream());
			} catch(Exception e) {
				e.printStackTrace();
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return response;
	}
	
	public ZHttpResponse post(String url, byte[] bytes) {
		ZHttpResponse response = null;
		
		try {
			
			ZProxy proxy = ZProxy.getZProxy();
			if (proxy == null) {
				connection = (HttpURLConnection) new URL(url).openConnection();
			} else {
				connection = (HttpURLConnection) new URL(url).openConnection(proxy.toProxyObject());
			}
			
			connection.setConnectTimeout(TIMEOUT);
			connection.setReadTimeout(TIMEOUT);
			connection.setDoOutput(true);
			connection.setRequestMethod(HttpPost.METHOD_NAME);
			
			setHeaders();
			connection.connect();
			
			ZHttpWriter writer = new ZHttpWriter(connection.getOutputStream(), null);
			writer.write(bytes, 0, bytes.length);
			
			response = new ZHttpResponse();
			response.setResponseCode(connection.getResponseCode());
			response.setHeaders(connection.getHeaderFields());
			response.setInputStream(connection.getInputStream());
			
		} catch (Exception e) {
			e.printStackTrace();
			response = null;
		}
		return response;
	}
	
	public void setHeaders(HashMap<String, String> headers) {
		this.requestHeaders = headers;
	}
	
	private void setHeaders() {
		if (requestHeaders != null) {
			Set<String> set = requestHeaders.keySet();
			for(String string : set) {
				connection.setRequestProperty(string, requestHeaders.get(string));
			}
		}
	}
	
	public void close() {
		if(connection != null) {
			connection.disconnect();
		}
	}

}
