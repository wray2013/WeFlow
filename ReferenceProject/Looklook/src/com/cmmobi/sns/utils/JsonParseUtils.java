package com.cmmobi.sns.utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.cmmobi.sns.exceptions.WeiboError;
/**
 * 所有分享平台api访问返回值错误解析工具类
 * @author zhangwei
 *
 */
public class JsonParseUtils {

	public static WeiboError parseSinaWeiboError(String response) {
		if(response == null){
			return null;
		}
		try {
			JSONObject json = new JSONObject(response);
			if(json.getInt("error_code") == 0){
				return null;
			}
		} catch (JSONException e) {
			return null;
		}
		return parseSinaWeiboErrorJson(response);
	}
	
	/**
	 * 
	 * @param response
	 * @return
	 */
	public static WeiboError parseTencentWeiboError(String response) {
		if(response == null){
			return null;
		}
		try {
			JSONObject json = new JSONObject(response);
			if(json.getInt("errcode") == 0){
				return null;
			}
		} catch (JSONException e) {
			return null;
		}
		return parseTencentWeiboErrorJson(response);
	}
	
	private static WeiboError parseTencentWeiboErrorJson(String response) {
		try {
			JSONObject json = new JSONObject(response);
			return new WeiboError(json.getInt("error_code"), json.optString(
					"error"), response);
		} catch (JSONException e) {
			return null;
		}
	}
	
	private static WeiboError parseSinaWeiboErrorJson(String response) {
		try {
			JSONObject json = new JSONObject(response);
			return new WeiboError(json.getInt("errcode"), json.optString(
					"msg", ""), json.getInt("ret"), response);
		} catch (JSONException e) {
			return null;
		}
	}
	
	public static JSONArray getJSONObjectArrayValue(String input, String key){
		if(input==null){
			return null;
		}
		
		JSONArray value = null;
		try {
			JSONObject json = new JSONObject(input);
			value = json.getJSONArray(key);

		} catch (JSONException e) {
			e.printStackTrace();
		}

		return value;
	}
	
	public static JSONObject getJSONObjectValue(String input, String key){
		if(input==null){
			return null;
		}
		
		JSONObject value = null;
		try {
			JSONObject json = new JSONObject(input);
			value = json.getJSONObject(key);

		} catch (JSONException e) {
			e.printStackTrace();
		}

		return value;
	}
	
	
	public static String getStringValue(String input, String key) {
		if(input == null){
			return null;
		}
		
		String value = null;
		try {
			JSONObject json = new JSONObject(input);
			value = json.getString(key);

		} catch (JSONException e) {
			e.printStackTrace();
		}

		return value;
	}
	
	public static String getLongValue(String input, String key) {
		String value = null;
		if(input == null){
			return value;
		}
		
		try {
			JSONObject json = new JSONObject(input);
			value = String.valueOf(json.getLong(key));
			

		} catch (JSONException e) {
			e.printStackTrace();
		}

		return value;
	}

}
