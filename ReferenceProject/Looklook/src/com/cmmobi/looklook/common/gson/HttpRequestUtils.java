package com.cmmobi.looklook.common.gson;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import android.util.Log;

public class HttpRequestUtils {

	private static final String TAG = "HTTPManageer";

	
	public String sendPostRequest(String url, String json){
		HttpResponse httpResponse = null;
		HttpPost httpPost = new HttpPost(url);
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("requestapp", json));
		try {
			httpPost.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));
			httpResponse = new DefaultHttpClient().execute(httpPost);
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}


		if (httpResponse!=null && httpResponse.getStatusLine().getStatusCode() == 200) {
			// 第3步：使用getEntity方法获得返回结果
			String result = null;
			try {
				result = EntityUtils.toString(httpResponse.getEntity());
				return result;
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			// 去掉返回结果中的"\r"字符，否则会在结果字符串后面显示一个小方格
			//tvQueryResult.setText(result.replaceAll("\r", ""));
		}else if(httpResponse==null){
			Log.e(TAG, "httpResponse error: null");
		}else{
			Log.e(TAG, "httpResponse error:" + httpResponse.getStatusLine().getStatusCode());
		}
		
		
		return null;
	}
	
/*	public <T> T  sendPostRequest(String url, String json,
			Class<T> classOfT) {
		HttpResponse httpResponse = null;
		
		HttpPost httpPost = new HttpPost(url);
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("request", json));
		try {
			httpPost.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));
			httpResponse = new DefaultHttpClient().execute(httpPost);
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}


		if (httpResponse.getStatusLine().getStatusCode() == 200) {
			// 第3步：使用getEntity方法获得返回结果
			String result = null;
			try {
				result = EntityUtils.toString(httpResponse.getEntity());
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			// 去掉返回结果中的"\r"字符，否则会在结果字符串后面显示一个小方格
			//tvQueryResult.setText(result.replaceAll("\r", ""));
			Gson gson = new Gson();
			T target = gson.fromJson(result, classOfT);
			return target;
		}else{
			Log.e(TAG, "httpResponse error:" + httpResponse.getStatusLine().getStatusCode());
		}
		
		return null;
	}*/

}
