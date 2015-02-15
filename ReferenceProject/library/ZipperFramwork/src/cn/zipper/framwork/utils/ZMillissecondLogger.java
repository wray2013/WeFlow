package cn.zipper.framwork.utils;

import java.util.HashMap;

import cn.zipper.framwork.core.ZLog;

public final class ZMillissecondLogger {
	
	private static final String DEFAULT_NAME = "DEFAULT_NAME"; // 不指定毫秒记录的名称时, 使用默认名称;
	private static HashMap<String, Long> records = new HashMap<String, Long>();
	
	private ZMillissecondLogger() {
	}

	
	public static void begin() {
		begin(DEFAULT_NAME);
	}
	
	public static void begin(String name) {
		records.put(name, System.currentTimeMillis());
	}
	
	public static long end() {
		return end(DEFAULT_NAME);
	}
	
	public static long end(String name) {
		if (isContainsKey(name)) {
			long millis = get(name);
			records.put(name, System.currentTimeMillis() - millis);
			
		} else {
			ZLog.e("name error: have not this name (maybe need call begin() before end()!");
		}
		
		return get(name);
	}
	
	public static long get() {
		return get(DEFAULT_NAME);
	}
	
	public static long get(String name) {
		return records.get(name);
	}
	
	public static boolean isContainsKey(String name) {
		return records.containsKey(name);
	}
	
	public static void release() {
		records.clear();
	}

}
