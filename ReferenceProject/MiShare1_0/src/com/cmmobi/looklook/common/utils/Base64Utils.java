package com.cmmobi.looklook.common.utils;

import it.sauronsoftware.base64.Base64;
import cn.zipper.framwork.utils.ZStringUtils;

public final class Base64Utils {
	
	private Base64Utils(){
	}
	
	
	public static String encode(String string) {
		string = ZStringUtils.nullToEmpty(string);
		String result = Base64.encode(string);
		return result;
	}
	
	public static String decode(String string) {
		String result = ZStringUtils.nullToEmpty(string);
		try {
			if(string.equals("\"\"")){
				return null;
			}
			result = Base64.decode(string);
		} catch (Exception e) {
			//Log.e("ddddssss:", string);
			e.printStackTrace();
			return string;
		}
		return result;
	}

}
