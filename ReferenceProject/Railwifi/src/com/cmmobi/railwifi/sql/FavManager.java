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
import com.cmmobi.railwifi.dao.Fav;
import com.cmmobi.railwifi.dao.FavDao;
import com.cmmobi.railwifi.dao.FavDao.Properties;
import com.cmmobi.railwifi.utils.ConStant;
import com.googlecode.concurrentlinkedhashmap.ConcurrentLinkedHashMap;
import com.googlecode.concurrentlinkedhashmap.EntryWeigher;
import com.googlecode.concurrentlinkedhashmap.EvictionListener;

import de.greenrobot.dao.query.WhereCondition;

public class FavManager {
	protected static final String TAG = "FavManager";
	private ConcurrentLinkedHashMap<String, Fav> media_map;
	private static FavManager ins = null;
	
	private Context context;
	
	private DaoMaster daoMaster;
	private DaoSession daoSession;
	private FavDao favDao;
	private SQLiteDatabase db;
	
	public transient static EntryWeigher<String, Fav> memoryUsageWeigher = new EntryWeigher<String, Fav>() {
		  //final MemoryMeter meter = new MemoryMeter();

		  @Override 
		  public int weightOf(String key, Fav value) {
		    //long bytes = meter.measure(key) + meter.measure(value);

		    return 1;
		  }
	};
	
	public transient static EvictionListener<String, Fav> listener = new EvictionListener<String, Fav>() {
		  @Override 
		  public void onEviction(String key, Fav value) {
			    Log.v(TAG, "Evicted name=" + value.getName() + ", type=" + value.getMedia_type());
		  }
	};
	
	private FavManager(){
		context = MainApplication.getAppInstance();
		media_map = new ConcurrentLinkedHashMap.Builder<String, Fav>()
				.maximumWeightedCapacity(ConStant.MEDIA_CACHE_LIMIT) // max num
			    .weigher(memoryUsageWeigher)
			    .listener(listener)
			    .build();
		
		DevOpenHelper helper = new DaoMaster.DevOpenHelper(context, "railwifidb", null);
        db = helper.getWritableDatabase();
        daoMaster = new DaoMaster(db);
        daoSession = daoMaster.newSession();
        favDao = daoSession.getFavDao();
	}
	
	public synchronized static FavManager getInstance(){
		if(ins==null){
			ins = new FavManager();
		}
		
		return ins;
	}
	
	public synchronized boolean hasFavItem(String media_id){
		Fav item = getFavItem(media_id);
		return item!=null;
	}
	
	public synchronized Fav getFavItem(String media_id){
		Fav elem = media_map.get(media_id);
		if(elem==null){
//			List<Fav> list = favDao.queryRaw("WHERE media_id == ?", new String[]{media_id});
			List<Fav> list = favDao.queryBuilder().where(Properties.Media_id.eq(media_id), new WhereCondition[]{}).list();
			if(list!=null && list.size()>0){
				elem = list.get(0);
				media_map.put(media_id, elem);
			}
		}
		
		return elem;
	}
	
	public synchronized void putFavItem(Fav elem){
		media_map.put(elem.getMedia_id(), elem);
		favDao.insertOrReplace(elem);
	}
	
	public synchronized void removeFavItem(String media_id){
		media_map.remove(media_id);
		favDao.deleteByKey(media_id);
	}
	
	public synchronized List<Fav> getAllFavList(){
		List<Fav> list = favDao.loadAll();
		Collections.reverse(list);
		return list;
	}
	
}
