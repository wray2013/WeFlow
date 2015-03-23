package com.etoc.weflow.utils;

public class NumberUtils {
	
	public static String convert2IntStr(String floatStr) {
		Float f = Float.parseFloat(floatStr);
		int ret = f.intValue();
		return Math.abs(ret) + "";
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

}
