package com.cmmobi.railwifi.sql;

import java.util.Collections;
import java.util.List;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.cmmobi.railwifi.MainApplication;
import com.cmmobi.railwifi.dao.DaoMaster;
import com.cmmobi.railwifi.dao.DaoMaster.DevOpenHelper;
import com.cmmobi.railwifi.dao.DaoSession;
import com.cmmobi.railwifi.dao.PlayHistory;
import com.cmmobi.railwifi.dao.PlayHistoryDao;
import com.cmmobi.railwifi.dao.PlayHistoryDao.Properties;
import com.cmmobi.railwifi.dao.PlayHistory;
import com.cmmobi.railwifi.utils.ConStant;
import com.googlecode.concurrentlinkedhashmap.ConcurrentLinkedHashMap;
import com.googlecode.concurrentlinkedhashmap.EntryWeigher;
import com.googlecode.concurrentlinkedhashmap.EvictionListener;

import de.greenrobot.dao.query.WhereCondition;

public class HistoryManager {
	protected static final String TAG = "HistoryManager";
	private ConcurrentLinkedHashMap<String, PlayHistory> history_map;
	private static HistoryManager ins = null;
	
	private Context context;
	
	private DaoMaster daoMaster;
	private DaoSession daoSession;
	private PlayHistoryDao PlayHistoryDao;
	private SQLiteDatabase db;
	
	public transient static EntryWeigher<String, PlayHistory> memoryUsageWeigher = new EntryWeigher<String, PlayHistory>() {
		  //final MemoryMeter meter = new MemoryMeter();

		  @Override 
		  public int weightOf(String key, PlayHistory value) {
		    //long bytes = meter.measure(key) + meter.measure(value);

		    return 1;
		  }
	};
	
	public transient static EvictionListener<String, PlayHistory> listener = new EvictionListener<String, PlayHistory>() {
		  @Override 
		  public void onEviction(String key, PlayHistory value) {
			    Log.v(TAG, "Evicted name=" + value.getName() + ", type=" + value.getMedia_type());
		  }
	};
	
	private HistoryManager(){
		context = MainApplication.getAppInstance();
		history_map = new ConcurrentLinkedHashMap.Builder<String, PlayHistory>()
				.maximumWeightedCapacity(ConStant.MEDIA_CACHE_LIMIT) // max num
			    .weigher(memoryUsageWeigher)
			    .listener(listener)
			    .build();
		
		DevOpenHelper helper = new DaoMaster.DevOpenHelper(context, "railwifidb", null);
        db = helper.getWritableDatabase();
        daoMaster = new DaoMaster(db);
        daoSession = daoMaster.newSession();
        PlayHistoryDao = daoSession.getPlayHistoryDao();
	}
	
	public synchronized static HistoryManager getInstance(){
		if(ins==null){
			ins = new HistoryManager();
		}
		
		return ins;
	}
	
	public synchronized boolean hasPlayHistoryItem(String media_id){
		PlayHistory item = getPlayHistoryItem(media_id);
		return item!=null;
	}
	
	public synchronized PlayHistory getPlayHistoryItem(String media_id){
		if(media_id==null){
			return null;
		}
		PlayHistory elem = history_map.get(media_id);
		if(elem==null){
//			List<PlayHistory> list = PlayHistoryDao.queryRaw("WHERE media_id == ?", new String[]{media_id});
			List<PlayHistory> list = PlayHistoryDao.queryBuilder().where(Properties.Media_id.eq(media_id), new WhereCondition[]{}).list();
			if(list!=null && list.size()>0){
				elem = list.get(0);
				history_map.put(media_id, elem);
			}
		}
		
		return elem;
	}
	
	public synchronized void putPlayHistoryItem(PlayHistory elem){
		history_map.put(elem.getMedia_id(), elem);
		try{
			PlayHistoryDao.insertOrReplace(elem);
		}catch(Exception e){
			e.printStackTrace();
		}
		

	}
	
	public synchronized void removePlayHistoryItem(String media_id){
		history_map.remove(media_id);
		PlayHistoryDao.deleteByKey(media_id);
	}
	
	public synchronized void removeAll(){
		history_map.clear();
		PlayHistoryDao.deleteAll();
	}
	
	public synchronized List<PlayHistory> getAllPlayHistoryList(){
		List<PlayHistory> list = PlayHistoryDao.loadAll();
		Collections.reverse(list);
		return list;
	}
	
}
