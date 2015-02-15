package cn.zipper.framwork.io.cache;

import java.util.concurrent.ConcurrentHashMap;

import cn.zipper.framwork.io.cache.ZMemoryCacher.CACHE_MODE;
import cn.zipper.framwork.io.cache.ZMemoryCacher.CacheObject;

import com.googlecode.concurrentlinkedhashmap.EntryWeigher;

public final class ZMemoryCacherFactory {
	
	private static ConcurrentHashMap<Object, Object> pool = new ConcurrentHashMap<Object, Object>();
	
	
	private ZMemoryCacherFactory() {
	}
	
	/**
	 * 创建新的缓存器 (如果以key命名的缓存器已经存在, 则不重新创建);
	 * @param <T>: 缓存器存储的对象类型;
	 * @param key: 缓存器名称;
	 * @param cacheMode: 缓存模式;
	 * @param clazz: 缓存器存储的对象类型;
	 * @param weigher: 权重计算器;
	 * @param capacity: 容量 (所有缓存内容的总权重上限);
	 * @return: 新创建的 或 已经存在的缓存器;
	 */
	@SuppressWarnings("unchecked")
	public static <T> ZMemoryCacher<T> get(Object key, 
			CACHE_MODE cacheMode, 
			Class<T> clazz, 
			EntryWeigher<Object, Object> weigher, 
			long capacity) {
		
		ZMemoryCacher<T> cacher = null;
		
		if (pool.containsKey(key)) {
			cacher = (ZMemoryCacher<T>) pool.get(key);
		} else {
			cacher = new ZMemoryCacher<T>(cacheMode, weigher, capacity);
			pool.put(key, cacher);
		}
		
		return cacher;
	}
	
	/**
	 * 获取缓存器;
	 * @param <T>
	 * @param key
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static <T> ZMemoryCacher<T> get(Object key) {
		ZMemoryCacher<T> cacher = null;
		
		if (pool.containsKey(key)) {
			cacher = (ZMemoryCacher<T>) pool.get(key);
		}
		return cacher;
	}
	
	/**
	 * 清空并摧毁指定的缓存器;
	 * @param <T>
	 * @param key
	 */
	@SuppressWarnings("unchecked")
	public static <T> void delete(Object key) {
		if (pool.containsKey(key)) {
			ZMemoryCacher<T> cacher = (ZMemoryCacher<T>) pool.get(key);
			cacher.clear();
			pool.remove(cacher);
		}
	}

}
