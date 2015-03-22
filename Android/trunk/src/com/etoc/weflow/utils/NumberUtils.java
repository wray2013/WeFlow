package com.etoc.weflow.utils;

public class NumberUtils {
	
	public static String convert2IntStr(String floatStr) {
		Float f = Float.parseFloat(floatStr);
		int ret = f.intValue();
		return Math.abs(ret) + "";
	}

}
