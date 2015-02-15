package cn.zipper.framwork.utils;

import android.text.TextUtils;

public final class ZStringUtils {
	
	private ZStringUtils() {
	}
	
	
	public static String nullToEmpty(String string) {
		if (string == null || string.equalsIgnoreCase("null")) {
			string = "";
		}
		return string;
	}
	
	public static String emptyToNull(String string) {
		if (string != null && TextUtils.isEmpty(string)) {
			string = null;
		}
		return string;
	}

}
