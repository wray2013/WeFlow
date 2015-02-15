package cn.zipper.framwork.core;

import java.util.HashMap;

/**
 * 复杂数据对象交换器, 用来绕开Intent传递复杂对象的繁琐;
 * @author Administrator
 *
 */
public final class ZDataPool {
	
	private static HashMap<Object, Object> datas = new HashMap<Object, Object>();
	
	private ZDataPool() {
	}
	
	public static void pushIn(Object key, Object value) {
		if (datas.containsValue(value)) {
			datas.remove(key);
		}
		datas.put(key, value);
	}
	
	
	public static Object getOut(Object key) {
		Object object = null;
		if (key != null) {
			object = datas.get(key);
		}
		return object;
	}

}
