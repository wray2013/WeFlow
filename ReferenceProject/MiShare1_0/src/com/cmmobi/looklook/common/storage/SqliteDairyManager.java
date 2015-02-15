package com.cmmobi.looklook.common.storage;

import java.util.ArrayList;
import java.util.HashMap;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.cmmobi.looklook.MainApplication;
import com.cmmobi.looklook.common.constant.Constant;
import com.cmmobi.looklook.common.gson.GsonResponse3;
import com.cmmobi.looklook.common.gson.GsonResponse3.MyDiary;
import com.google.gson.Gson;
import com.googlecode.concurrentlinkedhashmap.ConcurrentLinkedHashMap;
import com.googlecode.concurrentlinkedhashmap.EntryWeigher;
import com.googlecode.concurrentlinkedhashmap.EvictionListener;

public class SqliteDairyManager {
	public static SqliteDairyManager ins;
	private static final String DATABASENAME = "looklook.db";
	private static final String TABLENAME = "diaries";
	private static final String TAG = "SqliteDairyManager";
	
	//private HashMap<String, GsonResponse3.MyDiary> cache;
	private Gson gson;
	private SQLiteDatabase db;
	private Context context;
	
	private ConcurrentLinkedHashMap<String, MyDiary> diaryid_map;
	private ConcurrentLinkedHashMap<String, MyDiary> uuid_map;
	
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
	
	private SqliteDairyManager(){
		context = MainApplication.getAppInstance();
		gson = new Gson();
/*		cache = new HashMap<String, GsonResponse3.MyDiary>();*/
		db = SqlHelper.getInstance().getDb();
		//db = context.openOrCreateDatabase(DATABASENAME, Context.MODE_PRIVATE, null);  

		db.execSQL("CREATE TABLE IF NOT EXISTS " + TABLENAME + " (uuid VARCHAR UNIQUE PRIMARY KEY , diaryid VARCHAR, updatetimemilli INT8, diarytimemilli INT8, shoottime INT8, data TEXT)"); 
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
	 
	public synchronized static SqliteDairyManager getInstance(){
		if(ins==null){
			ins = new SqliteDairyManager();
		}
		
		return ins;
	}
	
	public synchronized void  removeDiary(MyDiary diary){
		if(diary!=null){
			if(diary.diaryuuid!=null){
				uuid_map.remove(diary.diaryuuid);
			}

			if(diary.diaryid!=null){
				diaryid_map.remove(diary.diaryid);
			}
			
			try{
				db.delete(TABLENAME, "uuid == ?", new String[]{diary.diaryuuid});
			}catch(Exception e){
				e.printStackTrace();
			}


		}

	}
	
	public synchronized void removeDiaryByUUID(String diaryuuid){
		if(uuid_map.containsKey(diaryuuid)){
			MyDiary diary = uuid_map.get(diaryuuid);
			if(diary!=null && diary.diaryid!=null){
				diaryid_map.remove(diary.diaryid);
			}
			
			uuid_map.remove(diary.diaryuuid);
			
		}
		
		try{
			db.delete(TABLENAME, "uuid == ?", new String[]{diaryuuid});
		}catch(Exception e){
			e.printStackTrace();
		}

 
	}
	
	public synchronized void removeDiaryByID(String diaryid){
		if(diaryid_map.containsKey(diaryid)){
			MyDiary diary = diaryid_map.get(diaryid);
			if(diary!=null && diary.diaryuuid!=null){
				uuid_map.remove(diary.diaryuuid);
			}
			
			diaryid_map.remove(diaryid);
		}
		
		try{
			db.delete(TABLENAME, "diaryid == ?", new String[]{diaryid});
		}catch(Exception e){
			e.printStackTrace();
		}

 
	}
	
	public synchronized void putDiary(GsonResponse3.MyDiary diary){
		if(diary!=null && diary.diaryuuid!=null){
			//Log.v(TAG, "putDiary - diaryuuid:" + diray.diaryuuid);
			GsonResponse3.MyDiary tmpDiray = uuid_map.get(diary.diaryuuid);
			long time1 = 0;
			long time2 = 0;
			try{
				time1 = Long.valueOf(diary.updatetimemilli);
			}catch(Exception e){
				//e.printStackTrace();
			}
			
			try{
				time2 = Long.valueOf(tmpDiray.updatetimemilli);
			}catch(Exception e){
				//e.printStackTrace();
			}
			
			tmpDiray=null;
			
			if(tmpDiray==null || time1 > time2){ //need persist to sqlite
				try {
					 db.beginTransaction();// 开始事务
					 ContentValues cv = new ContentValues();  
					 cv.put("diaryid", diary.diaryid);
					 cv.put("uuid", diary.diaryuuid);  
					 cv.put("diarytimemilli", diary.diarytimemilli);
					 cv.put("updatetimemilli", diary.updatetimemilli);
					 cv.put("shoottime", diary.shoottime);
					 cv.put("data", gson.toJson(diary));  
					 //插入ContentValues中的数据   
					 //db.insert(TABLENAME, null, cv);  
					 //db.update(TABLENAME, cv, null, null);
					 db.replace(TABLENAME, null, cv);
					 //db.insertOrThrow(TABLENAME, null, cv);
					 db.setTransactionSuccessful();// 调用此方法会在执行到endTransaction()
													// 时提交当前事务，如果不调用此方法会回滚事务
					 if(diary.diaryuuid!=null){
						 uuid_map.put(diary.diaryuuid, diary);
					 }
					 
					 if(diary.diaryid!=null){
						 diaryid_map.put(diary.diaryid, diary);
					 }

				}catch(Exception e){
					e.printStackTrace();
				}finally {
					db.endTransaction();// 由事务的标志决定是提交事务，还是回滚事务
				} 

			}
		}
	}
	
	public synchronized GsonResponse3.MyDiary getDiaryByUUID(String diaryuuid){
		GsonResponse3.MyDiary diary = null;
		if(diaryuuid!=null){
			//Log.v(TAG, "getDiary - diaryuuid:" + diaryuuid);
			diary = uuid_map.get(diaryuuid);
			if(diary==null){
				try{
					Cursor c = db.rawQuery("SELECT * FROM " + TABLENAME + " WHERE uuid == ?", new String[]{diaryuuid});  
			        if(c.moveToFirst()){
						do {  
				            //long _id = c.getLong(c.getColumnIndex("_id"));  
				            String data = c.getString(c.getColumnIndex("data"));  
				            diary =  gson.fromJson(data, GsonResponse3.MyDiary.class);
				            break;
				        }  while (c.moveToNext());
			        }
			        c.close();					
				}catch(Exception e){
					e.printStackTrace();
				}
		        
		        if(diary!=null){
		        	uuid_map.put(diaryuuid, diary);
		        	if(diary.diaryid!=null){
		        		diaryid_map.put(diary.diaryid, diary);
		        	}
		        }
			}
			

		}
		
		return diary;
	}
	
	public synchronized GsonResponse3.MyDiary getDiaryByID(String diaryid){
		GsonResponse3.MyDiary diary = null;
		if(diaryid!=null){
			//Log.v(TAG, "getDiary - diaryuuid:" + diaryuuid);
			diary = diaryid_map.get(diaryid);
			if(diary==null){
				try{
					Cursor c = db.rawQuery("SELECT * FROM " + TABLENAME + " WHERE diaryid == ?", new String[]{diaryid});  
			        if(c.moveToFirst()){
						do {  
				            //long _id = c.getLong(c.getColumnIndex("_id"));  
				            String data = c.getString(c.getColumnIndex("data"));  
				            diary =  gson.fromJson(data, GsonResponse3.MyDiary.class);
				            break;
				        } while (c.moveToNext()); 
			        }

			        c.close();
					
				}catch(Exception e){
					e.printStackTrace();
				}
		        
		        if(diary!=null){
		        	diaryid_map.put(diaryid, diary);
		        	if(diary.diaryuuid!=null){
		        		uuid_map.put(diary.diaryuuid, diary);
		        	}
		        }
			}
			

		}
		
		return diary;
	}
	
	public synchronized void cleanUp(){
		diaryid_map.clear();
		uuid_map.clear();
	}
	
	public synchronized GsonResponse3.MyDiary[] getDiarys(long ms){
		ArrayList<MyDiary> diaries = new ArrayList<MyDiary>();
		try{
			Cursor c = db.rawQuery("SELECT * FROM " + TABLENAME + " WHERE ( shoottime is null or shoottime = '' and diarytimemilli > ? ) or shoottime > ?", new String[]{String.valueOf(ms), String.valueOf(ms)});  
	        if(c.moveToFirst()){
	    		do {  
	                //long _id = c.getLong(c.getColumnIndex("_id"));  
	                String data = c.getString(c.getColumnIndex("data"));  
	                MyDiary diary =  gson.fromJson(data, GsonResponse3.MyDiary.class);
	                if(diary != null) {
//	    				Log.d(TAG, "filter time = " + ms + ";diaries millitime = " + myDiary.diarytimemilli);
	    				if(diary.join_safebox.equals("1") || diary.isDuplicated()
	    						|| Long.parseLong(diary.shoottime) < ms || diary.isMiShareDiary()) {
	    					continue;
	    				}
	    				diaries.add(diary);
	    			}
	            } while (c.moveToNext());
	        }
	 
	        c.close();			
		}catch(Exception e){
			e.printStackTrace();
		}

		
        Log.d(TAG, "Got " + diaries.size() + " diaries");
		return (MyDiary[]) diaries.toArray(new MyDiary[diaries.size()]);
	}

}
