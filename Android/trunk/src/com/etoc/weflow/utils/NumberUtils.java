package com.etoc.weflow.utils;

public class NumberUtils {
	
	public static String convert2IntStr(String floatStr) {
		if (floatStr == null) {
			return "0";
		}
		try {
			Float f = Float.parseFloat(floatStr);
			int ret = f.intValue();
			return Math.abs(ret) + "";
		} catch (Exception e) {
			
		}
		return "0";
	}
	
	public static int Str2Int(String intStr) {
		float f = 0;
		try {
			f = Float.parseFloat(intStr);
		} catch (Exception e) {
			e.printStackTrace();
		}
		int ret = (int)f;
		return ret;
	}
	
	public static float Str2Float(String intStr) {
		float f = 0.0f;
		try {
			f = Float.parseFloat(intStr);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return f;
	}

}
