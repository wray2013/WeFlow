package cn.zipper.framwork.io.cache;

import java.lang.ref.SoftReference;
import java.util.concurrent.ConcurrentMap;

import cn.zipper.framwork.core.ZLog;

import com.googlecode.concurrentlinkedhashmap.ConcurrentLinkedHashMap;
import com.googlecode.concurrentlinkedhashmap.EntryWeigher;
import com.googlecode.concurrentlinkedhashmap.EvictionListener;


public final class ZMemoryCacher<T> {
	
	public static enum CACHE_MODE {HARD, SOFT, MIX};
	
	private CACHE_MODE cacheMode;
	private ConcurrentMap<Object, CacheObject> hardCacher;
	private ConcurrentMap<Object, SoftReference<CacheObject>> softCacher;
	
	
	public ZMemoryCacher(CACHE_MODE cacheMode, EntryWeigher<Object, Object> weigher, long capacity) {
		this.cacheMode = cacheMode;
		
		switch (cacheMode) {
		case HARD:
			
			this.hardCacher = new ConcurrentLinkedHashMap.Builder<Object, CacheObject>()
			.maximumWeightedCapacity(capacity)
			.weigher(weigher)
			.build();
			break;
			
		case SOFT:
			this.softCacher = new ConcurrentLinkedHashMap.Builder<Object, SoftReference<CacheObject>>()
			.maximumWeightedCapacity(capacity)
			.build();
			break;
			
		case MIX:
			this.hardCacher = new ConcurrentLinkedHashMap.Builder<Object, CacheObject>()
			.maximumWeightedCapacity(capacity)
			.listener(new EvictionListener<Object, ZMemoryCacher<T>.CacheObject>() {

				@Override
				public void onEviction(Object key, CacheObject cacheObject) {
					softCacher.put(key, new SoftReference<CacheObject>(cacheObject));
				}
				
			})
			.build();
			
			this.softCacher = new ConcurrentLinkedHashMap.Builder<Object, SoftReference<CacheObject>>()
			.maximumWeightedCapacity(capacity)
			.build();
			break;
		}
	}
	

	public CACHE_MODE getMode() {
		return cacheMode;
	}
	
	public void put(Object key, T type) {
		put(key, type, Integer.MAX_VALUE);
	}
	
	public void put(Object key, T type, int liveSeconds) {
		delete(key);
		
		switch (cacheMode) {
		case HARD:
		case MIX:
			hardCacher.put(key, new CacheObject(type, liveSeconds));
			break;
			
		case SOFT:
			softCacher.put(key, new SoftReference<CacheObject>(new CacheObject(type, liveSeconds)));
			break;
		}
	}
	
	public boolean containsKey(Object key) {
		boolean b = false;
		
		switch (cacheMode) {
		case HARD:
			if (hardCacher.containsKey(key)) {
				b = true;
			}
			break;
			
		case SOFT:
			if (softCacher.containsKey(key)) {
				b = true;
			}
			break;
			
		case MIX:
			if (hardCacher.containsKey(key) || softCacher.containsKey(key)) {
				b = true;
			}
			break;
		}
		
		return b;
	}
	
	public T get(Object key) {
		T type = null;
		
		switch (cacheMode) {
		case HARD:
			if (hardCacher.containsKey(key)) {
				CacheObject object = hardCacher.get(key);
				if (object != null && object.isAlive()) {
					type = object.type;
				} else {
					hardCacher.remove(key);
				}
			}
			break;
			
		case SOFT:
			if (softCacher.containsKey(key)) {
				SoftReference<CacheObject> reference = softCacher.get(key);
				CacheObject object = reference.get();
				if (object != null && object.isAlive()) {
					type = object.type;
				} else {
					reference.clear();
					softCacher.remove(key);
				}
			}
			break;
			
		case MIX:
			if (hardCacher.containsKey(key)) {
				CacheObject object = hardCacher.get(key);
				if (object != null && object.isAlive()) {
					type = object.type;
				} else {
					hardCacher.remove(key);
				}
			} else if (softCacher.containsKey(key)) {
				SoftReference<CacheObject> reference = softCacher.get(key);
				CacheObject object = reference.get();
				if (object != null && object.isAlive()) {
					type = object.type;
				} else {
					reference.clear();
					softCacher.remove(key);
				}
			}
			break;
		}
		
		return type;
	}
	
	public void delete(Object key) {
		switch (cacheMode) {
		case HARD:
			if (hardCacher.containsKey(key)) {
				hardCacher.remove(key);
			}
			break;
			
		case SOFT:
			if (softCacher.containsKey(key)) {
				SoftReference<CacheObject> reference = softCacher.get(key);
				reference.clear();
				softCacher.remove(key);
			}
			break;
			
		case MIX:
			if (hardCacher.containsKey(key)) {
				hardCacher.remove(key);
				
			} else if (softCacher.containsKey(key)) {
				SoftReference<CacheObject> reference = softCacher.get(key);
				reference.clear();
				softCacher.remove(key);
			}
			break;
		}
	}
	
	public void clear() {
		switch (cacheMode) {
		case HARD:
			hardCacher.clear();
			break;
			
		case SOFT:
			softCacher.clear();
			break;
			
		case MIX:
			hardCacher.clear();
			softCacher.clear();
			break;
		}
	}
	
	
	public final class CacheObject {
		private T type;
		private long updateMillis;
		private long liveMillis;
		
		protected CacheObject(T type, int liveSeconds) {
			this.type = type;
			this.liveMillis = Math.abs(liveSeconds) * 1000;
			this.updateMillis = System.currentTimeMillis();
		}
		
		public boolean isAlive() {
			return true;//System.currentTimeMillis() - updateMillis <= liveMillis;
		}
	}

}
