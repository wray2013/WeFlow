package com.cmmobi.looklook.common.storage;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;
import android.util.Log;

import com.cmmobi.looklook.MainApplication;
import com.cmmobi.looklook.common.constant.Constant;
import com.cmmobi.looklook.common.utils.MD5;
import com.cmmobi.looklook.info.profile.MediaValue;
import com.google.gson.Gson;
import com.googlecode.concurrentlinkedhashmap.ConcurrentLinkedHashMap;
import com.googlecode.concurrentlinkedhashmap.EntryWeigher;
import com.googlecode.concurrentlinkedhashmap.EvictionListener;

public class SqlMediaManager {
	public static SqlMediaManager ins;
	private static final String DATABASENAME = "looklook.db";
	private static final String TABLENAME = "media";
	private static final String TAG = "SqlMediaManager";
	
	//private HashMap<String, GsonResponse3.MediaValue> cache;
	private Gson gson;
	private SQLiteDatabase db;
	private Context context;
	
	private  ConcurrentLinkedHashMap<String, MediaValue> media_map;
	
	public  static EntryWeigher<String, MediaValue> memoryUsageWeigher = new EntryWeigher<String, MediaValue>() {
		  //final MemoryMeter meter = new MemoryMeter();

		  @Override 
		  public int weightOf(String key, MediaValue value) {
			  return 1;
		  }
	};
	
	public static EvictionListener<String, MediaValue> listener = new EvictionListener<String, MediaValue>() {
		  @Override 
		  public void onEviction(String key, MediaValue value) {
			    //rm the key(file name)
			    
			    //context.deleteFile(key);
			    Log.v(TAG, "Evicted url=" + value.url);
		  }
	};
	
	private SqlMediaManager(){
		context = MainApplication.getAppInstance();
		gson = new Gson();
/*		cache = new HashMap<String, GsonResponse3.MediaValue>();*/
		File f = new File(Environment.getExternalStorageDirectory().getPath() + Constant.SD_STORAGE_ROOT + "/" + DATABASENAME);
		if(!f.exists()){
			f.getParentFile().mkdirs();
		}

		try{
			db = SQLiteDatabase.openOrCreateDatabase(f, null); 
		}catch(Exception e){
			e.printStackTrace();
			db = SqlHelper.getInstance().getDb();
		}
		db.execSQL("CREATE TABLE IF NOT EXISTS " 
		    + TABLENAME 
		    + " (key VARCHAR UNIQUE PRIMARY KEY , UID VARCHAR, localpath VARCHAR, url VARCHAR, totalSize INT8, realSize INT8, MediaType INT, Belong INT, Sync INT, SyncSize INT8, Direction INT)"); 
		media_map = new ConcurrentLinkedHashMap.Builder<String, MediaValue>()
				.maximumWeightedCapacity(Constant.MEDIA_CACHE_LIMIT) // max num
			    .weigher(memoryUsageWeigher)
			    .listener(listener)
			    .build();
	}
	 
	public synchronized static SqlMediaManager getInstance(){
		if(ins==null){
			ins = new SqlMediaManager();
		}
		
		return ins;
	}
	
	/**
	 *  
	 *  从map和sqlite中删除mediavalue
	 * */
	public synchronized void delMedia(String key, boolean delDiskFile){
		if(key!=null){
			MediaValue ret = media_map.remove(key);
			
			if(delDiskFile && ret!=null){
				deleteDiskFile(ret);
			}
			
			try{
				db.delete(TABLENAME, "key == ?", new String[]{key});
			}catch(Exception e){
				e.printStackTrace();
			}
			
			
		}
		
	}
	
	
	public synchronized MediaValue getMedia(String key){
		MediaValue mv = null;
		if(key!=null){
			 mv = media_map.get(key);
			if(mv==null){
				try{
					Cursor c = db.rawQuery("SELECT * FROM " + TABLENAME + " WHERE key == ?", new String[]{key});  
			        if(c.moveToFirst()){
						do {  
				            //long _id = c.getLong(c.getColumnIndex("_id")); 
				        	mv = new MediaValue();
				        	mv.Belong = c.getInt(c.getColumnIndex("Belong"));
				        	mv.Direction = c.getInt(c.getColumnIndex("Direction"));
				        	mv.MediaType = c.getInt(c.getColumnIndex("MediaType"));
				        	mv.localpath = c.getString(c.getColumnIndex("localpath"));
				        	mv.realSize = c.getLong(c.getColumnIndex("realSize"));
				        	mv.Sync = c.getInt(c.getColumnIndex("Sync"));
				        	mv.SyncSize = c.getLong(c.getColumnIndex("SyncSize"));
				        	mv.totalSize = c.getLong(c.getColumnIndex("totalSize"));
				        	mv.UID = c.getString(c.getColumnIndex("UID"));
				        	mv.url = c.getString(c.getColumnIndex("url"));
				            break;
				        } while (c.moveToNext());
			        }
	 
			        c.close();
				}catch(Exception e){
					e.printStackTrace();
				}
		        
		        if(mv!=null){
		        	media_map.put(key, mv);
		        }
			}
			
		}
			
		return mv;
	}
	
	public synchronized void setMedia(String key, MediaValue value){
		if(key!=null && value!=null){
			try {
				 db.beginTransaction();// 开始事务
				 ContentValues cv = new ContentValues();  
				 cv.put("key", key);
				 cv.put("localpath", value.localpath);
				 cv.put("UID", value.UID);
				 cv.put("url", value.url);
				 cv.put("Belong", String.valueOf(value.Belong));
				 cv.put("Direction", String.valueOf(value.Direction));
				 cv.put("MediaType", String.valueOf(value.MediaType));
				 cv.put("realSize", String.valueOf(value.realSize));
				 cv.put("Sync", String.valueOf(value.Sync));
				 cv.put("SyncSize", String.valueOf(value.SyncSize));
				 cv.put("totalSize", String.valueOf(value.totalSize)); 
				 //插入ContentValues中的数据   
				 //db.insert(TABLENAME, null, cv);  
				 //db.update(TABLENAME, cv, null, null);
				 db.replace(TABLENAME, null, cv);
				 //db.insertOrThrow(TABLENAME, null, cv);
				 db.setTransactionSuccessful();// 调用此方法会在执行到endTransaction()
												// 时提交当前事务，如果不调用此方法会回滚事务
				 media_map.put(key, value);

			}catch(Exception e){
				e.printStackTrace();
			}finally {
				db.endTransaction();// 由事务的标志决定是提交事务，还是回滚事务
			} 
		}
	}
	
	/**
	 * 根据来源获取当前来源的文件总大小，单位 byte
	 * @param send 1：浏览数据 2：收藏数据 3：自拍数据 4：私信数据
	 *             -1： all
	 * */
	public synchronized long getTotalSizeBySrc(String UID, int src){
		long ret = 0;
		Cursor c = null;
		StringBuilder sb = new StringBuilder();
		sb.append("SELECT SUM(realSize) FROM " );
		sb.append(TABLENAME);
		try{
			if(src<0){
				sb.append(" WHERE UID == ?");
				c = db.rawQuery(sb.toString(), new String[]{UID}); 
			}else{
				sb.append(" WHERE Belong == ? AND UID == ?");
				c = db.rawQuery(sb.toString(), new String[]{String.valueOf(src), UID}); 
			}
			 
			if(c.moveToFirst()) {
				ret =  c.getLong(0);
			}  
			
	        c.close();
		}catch(Exception e){
			e.printStackTrace();
		}

		
		return ret; //其它，暂时给0	
	}
	
	/**
	 * 根据是否同步获取对应的文件总大小，单位 byte
	 * @param send  1：是  2：不是
	 * */
	public synchronized long getTotalSizeBySync(String UID, int Sync){		
		long ret = 0;
		Cursor c = null;
		StringBuilder sb = new StringBuilder();
		sb.append("SELECT SUM(realSize) FROM " );
		sb.append(TABLENAME);
		try{
			if(Sync<0){
				sb.append(" WHERE UID == ?");
				c = db.rawQuery(sb.toString(), new String[]{UID}); 
			}else{
				sb.append(" WHERE Sync == ? AND UID == ?");
				c = db.rawQuery(sb.toString(), new String[]{String.valueOf(Sync), UID}); 
			}
			
			if(c.moveToFirst()) {
				ret =  c.getLong(0);
			}  
			
	        c.close();
		}catch(Exception e){
			e.printStackTrace();
		}

		
		return ret; //其它，暂时给0	
	}
	
	/**
	 * 根据文件类型获取当该类型文件总大小，单位 byte
	 * @param MediaType 1： 文本， 2：图片  3： 短音频， 4：长音频， 5：视频
	 * */
	public synchronized long getTotalSizeByType(String UID, int MediaType){
		long ret = 0;
		Cursor c = null;
		StringBuilder sb = new StringBuilder();
		sb.append("SELECT SUM(realSize) FROM " );
		sb.append(TABLENAME);
		try{
			if(MediaType<0){
				sb.append(" WHERE UID == ?");
				c = db.rawQuery(sb.toString(), new String[]{UID}); 
			}else{
				sb.append(" WHERE MediaType == ? AND UID == ?");
				c = db.rawQuery(sb.toString(), new String[]{String.valueOf(MediaType), UID}); 
			}
			 
			if(c.moveToFirst()) {
				ret =  c.getLong(0);
			}  
			
	        c.close();
		}catch(Exception e){
			e.printStackTrace();
		}

		return ret; //其它，暂时给0	
	}
	
	/**
	 * @param category 1 belong  2 sync , 
	 *                 <0 all
	 * @param category_value 
	 *                 -- belong: 1：浏览数据 2：收藏数据 3：自拍数据 4：私信数据
	 *                 -- sync:   1：是  2：不是       
	 * @param fileType MediaType 1： 文本， 2：图片  3： 短音频， 4：长音频， 5：视频
	 *                           <0 all
	 * @param limit            
	 * */
	public synchronized void delMediaBat(String UID, int category, int category_value, int fileType, long limit){
		Cursor c = null;
		long delDoneSize = 0;
		StringBuilder sb = new StringBuilder();
		sb.append("SELECT key, realSize, localpath FROM " );
		sb.append(TABLENAME + " WHERE ");
		sb.append(" UID==?");
		
		try{
			if(category>0){
				if(category==1){
					sb.append(" AND Belong==?");	 
				}else{
					sb.append(" AND Sync==?");
				}
				
				if(fileType<0){ //all file format
					c = db.rawQuery(sb.toString(), new String[]{UID, String.valueOf(category_value)});
				}else{
					sb.append(" AND MediaType==?");
					c = db.rawQuery(sb.toString(), new String[]{UID, String.valueOf(category_value), String.valueOf(fileType)});
				}
			}else{
				if(fileType<0){ //all file format
					c = db.rawQuery(sb.toString(), new String[]{UID});
				}else{
					sb.append(" AND MediaType == ?");
					c = db.rawQuery(sb.toString(), new String[]{UID, String.valueOf(fileType)});
				}
			}

			if(c.moveToFirst()){
		         do {  
		            //long _id = c.getLong(c.getColumnIndex("_id"));  
		            String key = c.getString(c.getColumnIndex("key"));
		            long realSize = c.getLong(c.getColumnIndex("realSize"));
		            String localpath = c.getString(c.getColumnIndex("localpath"));
		            
		            if(limit>0 && delDoneSize>limit){
		            	break;
		            }
		            
		            delMedia(key, false);
		            
		            deleteDiskFile(localpath);
		            
		            delDoneSize+=realSize;
		        } while (c.moveToNext());
			} 

	        c.close();
		}catch(Exception e){
			e.printStackTrace();
		}

	}
	
	
	public static void deleteDiskFile(final String localpath) {
		new Thread() {
			public void run() {
				if (localpath != null) {
					File file = new File(
							Environment.getExternalStorageDirectory()
									+ localpath);

					if (file.exists() && file.isFile()) {
						Log.e(TAG, "del file:" + file.getAbsolutePath());
						file.delete();
					}
				}
			}
		}.start();

	}

	public static void deleteDiskFile(final MediaValue value) {
		new Thread() {
			public void run() {
				if (value != null && value.localpath != null) {
					File file = new File(
							Environment.getExternalStorageDirectory()
									+ value.localpath);

					if (file.exists() && file.isFile()) {
						Log.e(TAG, "del file:" + file.getAbsolutePath());
						file.delete();
					}
				}
			}
		}.start();

	}
	
	public synchronized void cleanUp(){
		media_map.clear();
	}

}
