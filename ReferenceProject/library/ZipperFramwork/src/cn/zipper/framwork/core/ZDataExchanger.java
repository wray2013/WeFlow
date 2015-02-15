package cn.zipper.framwork.core;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Set;

/**
 * 复杂数据对象交换器, 用来绕开Intent传递复杂对象的繁琐;
 * @author Administrator
 *
 */
public final class ZDataExchanger {
	
	private static HashMap<ZDataExchangerKey, Object> datas = new HashMap<ZDataExchangerKey, Object>();
	
	private ZDataExchanger() {
	}
	
	/**
	 * 推入数据并获得唯一key对象, 将此key对象通过Intent传递到任何地方后, 取出key, 再用key取出数据;
	 * @param object
	 * @return
	 */
	public static ZDataExchangerKey pushIn(Object object) {
		ZDataExchangerKey key = null;
		if (!datas.containsValue(object)) {
			key = new ZDataExchangerKey();
			datas.put(key, object);
		}
		return key;
	}
	
	/**
	 * 用key取出数据(只能取出一次, 需要再次使用数据时, 应该重新推入一次, 获取到新的key);
	 * @param key
	 * @return
	 */
	public static Object getOut(ZDataExchangerKey key) {
		Object object = null;
		if (key != null) {
			Set<ZDataExchangerKey> set = datas.keySet();
			for (ZDataExchangerKey temp : set) {
				if (key.key == temp.key) {
					object = datas.remove(temp);
					break;
				}
			}
		}
		return object;
	}
	
	@SuppressWarnings("serial")
	public static class ZDataExchangerKey implements Serializable {
		public static final String NAME = "ZDataExchangerKey";
		private static long root = System.currentTimeMillis();
		private long key;
		/**
		 * 不允许手动构造key;
		 */
		private ZDataExchangerKey() {
			synchronized (datas) {
				root ++;
				key = root;
			}
		}
	}

}
