package com.cmmobi.looklook.common.cache;

import java.io.File;
import java.io.FilenameFilter;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;

import android.content.Context;
import android.util.Log;

import com.googlecode.concurrentlinkedhashmap.ConcurrentLinkedHashMap;
import com.googlecode.concurrentlinkedhashmap.EntryWeigher;
import com.googlecode.concurrentlinkedhashmap.EvictionListener;

/**
 *  @author zhangwei 
 */
public class CacheManager{
	public static final String CHACHE_PREFIX = "cache_";
	
	private static final String TAG = "CacheManager";
	private static CacheManager instance;
	private ConcurrentLinkedHashMap<String, CacheResult> cache;
	private static Context context;
	
	private EntryWeigher<String, CacheResult> memoryUsageWeigher = new EntryWeigher<String, CacheResult>() {
		  //final MemoryMeter meter = new MemoryMeter();

		  @Override 
		  public int weightOf(String key, CacheResult value) {
		    //long bytes = meter.measure(key) + meter.measure(value);
			  long bytes = value.fileSize;//

		    return (int) Math.min(bytes, Integer.MAX_VALUE);
		  }
	};
	
	private EvictionListener<String, CacheResult> listener = new EvictionListener<String, CacheResult>() {
		  @Override 
		  public void onEviction(String key, CacheResult value) {
			    //rm the key(file name)
			    
			    context.deleteFile(key);
			    Log.e(TAG, "Evicted(delete) key=" + key);
		  }
	};
	
	private class CacheFilter  implements FilenameFilter{    
		   
		  public boolean isCache(String file) {    
		    if (file.toLowerCase(Locale.ENGLISH).startsWith(CHACHE_PREFIX)){    
		      return true;    
		    }else{    
		      return false;    
		    }    
		  }    
 
		  public boolean accept(File dir,String fname){    
		    return (isCache(fname));    
		   
		  }    
		   
	} 
	
	private CacheManager(){
		cache = new ConcurrentLinkedHashMap.Builder<String, CacheResult>()
				.maximumWeightedCapacity(10 * 1024 * 1024) // 10 MB, internal storage, not memory
			    .weigher(memoryUsageWeigher)
			    .listener(listener)
			    .build();
		
		load();
		
	}
	
	private synchronized void load(){
		Log.e(TAG, "CacheManager load");
		FilenameFilter filter = new CacheFilter();
		File[] filelist = context.getFilesDir().listFiles(filter);
		for(File f: filelist){
			Log.e(TAG, "f.name:" + f.getName());
			if(f.length()==0){
				f.delete();
				continue;
			}
			cache.put(f.getName(), new CacheResult(f.getName(),(int)f.length()));
		}
	}
	
	
	public static CacheManager getInstance(Context c){
		context = c;
		if(instance==null){
			instance = new CacheManager();
		}
		return instance;
	}
	
	/**
	 *  @return 被Cache的元数据信息
	 */
	public synchronized CacheResult  getItem(String key){
		if(cache.containsKey(key)){
			return cache.get(key);
		}else{
			return null;
		}
	}
	
	public synchronized CacheResult  putItem(String key, CacheResult value){
		return cache.put(key, value);
	}
	
	
	public synchronized void cleanAll(){
		Iterator<Entry<String, CacheResult>> iter = cache.entrySet().iterator();
		
		while (iter.hasNext()) {
			Map.Entry<String,CacheResult> entry = (Map.Entry<String,CacheResult>) iter.next();
			String key = entry.getKey();
			context.deleteFile(key);
		}
		
		cache.clear();

	}
	


	
}