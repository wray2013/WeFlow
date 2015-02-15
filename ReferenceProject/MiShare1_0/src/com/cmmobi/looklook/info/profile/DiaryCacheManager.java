package com.cmmobi.looklook.info.profile;

import android.util.Log;

import com.cmmobi.looklook.common.constant.Constant;
import com.cmmobi.looklook.common.gson.GsonResponse3.MyDiary;
import com.cmmobi.looklook.common.storage.SqliteDairyManager;
import com.googlecode.concurrentlinkedhashmap.ConcurrentLinkedHashMap;
import com.googlecode.concurrentlinkedhashmap.EntryWeigher;
import com.googlecode.concurrentlinkedhashmap.EvictionListener;

public class DiaryCacheManager {
	protected static final String TAG = "DiaryCacheManager";

	private static DiaryCacheManager ins;
	
	private ConcurrentLinkedHashMap<String, MyDiary> diaryid_map;
	private ConcurrentLinkedHashMap<String, MyDiary> uuid_map;
	
	
	public static DiaryCacheManager getInstance(){
		if(ins==null){
			ins = new DiaryCacheManager();
		}
		
		return ins;
	}
	
	public transient static EntryWeigher<String, MyDiary> memoryUsageWeigher = new EntryWeigher<String, MyDiary>() {
		  //final MemoryMeter meter = new MemoryMeter();

		  @Override 
		  public int weightOf(String key, MyDiary value) {
		    //long bytes = meter.measure(key) + meter.measure(value);

		    return 1;
		  }
	};
	
	public transient static EvictionListener<String, MyDiary> listener = new EvictionListener<String, MyDiary>() {
		  @Override 
		  public void onEviction(String key, MyDiary value) {
			    //rm the key(file name)
			    
			    //context.deleteFile(key);
			    Log.v(TAG, "Evicted diaryid=" + value.diaryid + ", uuid=" + value.diaryuuid);
		  }
	};
	
	private DiaryCacheManager(){
		diaryid_map = new ConcurrentLinkedHashMap.Builder<String, MyDiary>()
				.maximumWeightedCapacity(Constant.DIARY_CACHE_LIMIT) // max num
			    .weigher(memoryUsageWeigher)
			    .listener(listener)
			    .build();
		
		uuid_map = new ConcurrentLinkedHashMap.Builder<String, MyDiary>()
				.maximumWeightedCapacity(Constant.DIARY_CACHE_LIMIT) // max num
			    .weigher(memoryUsageWeigher)
			    .listener(listener)
			    .build();
	}
	
	/**
	 * 把日记放到lru map和sqlite中
	 * */
	public void putDiary(MyDiary diary){
		if(diary.diaryid!=null){
			diaryid_map.put(diary.diaryid, diary);
		}

		if(diary.diaryuuid!=null){
			uuid_map.put(diary.diaryuuid, diary);
		}
		
		SqliteDairyManager.getInstance().putDiary(diary);
	}

}
