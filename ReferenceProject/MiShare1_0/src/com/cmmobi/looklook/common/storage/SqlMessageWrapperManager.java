package com.cmmobi.looklook.common.storage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;
import android.util.Log;

import cn.zipper.framwork.utils.ZStringUtils;

import com.cmmobi.looklook.MainApplication;
import com.cmmobi.looklook.common.constant.Constant;
import com.cmmobi.looklook.common.gson.GsonResponse3;
import com.cmmobi.looklook.info.profile.MessageWrapper;
import com.cmmobi.looklook.info.profile.PrivateCommonMessage;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.googlecode.concurrentlinkedhashmap.ConcurrentLinkedHashMap;
import com.googlecode.concurrentlinkedhashmap.EntryWeigher;
import com.googlecode.concurrentlinkedhashmap.EvictionListener;

public class SqlMessageWrapperManager {
	public static SqlMessageWrapperManager ins;
	private static final String DATABASENAME = "looklook.db";
	private static final String TABLENAME = "private_messages";
	private static final String TAG = "SqlMessageWrapperManager";
	
	//private HashMap<String,  MessageWrapper> cache;
	private Gson gson;
	private SQLiteDatabase db;
	private Context context;
	
	private ConcurrentLinkedHashMap<String, MessageWrapper> messages_map;
	
	public transient static EntryWeigher<String, MessageWrapper> memoryUsageWeigher = new EntryWeigher<String, MessageWrapper>() {
		  //final MemoryMeter meter = new MemoryMeter();

		  @Override 
		  public int weightOf(String key, MessageWrapper value) {
		    //long bytes = meter.measure(key) + meter.measure(value);

		    return 1;
		  }
	};
	
	public transient static EvictionListener<String, MessageWrapper> listener = new EvictionListener<String, MessageWrapper>() {
		  @Override 
		  public void onEviction(String key, MessageWrapper value) {
			    //rm the key(file name)
			    
			    //context.deleteFile(key);
			    Log.v(TAG, "Evicted nickname=" + value.nickname + ", uuid=" + value.other_userid);
		  }
	};
	
	private SqlMessageWrapperManager(){
		context = MainApplication.getAppInstance();
		gson = new Gson();
		
		//db = context.openOrCreateDatabase(DATABASENAME, Context.MODE_PRIVATE, null);  
		db = SqlHelper.getInstance().getDb();
		
		db.execSQL("CREATE TABLE IF NOT EXISTS " 
		            + TABLENAME 
		            + " (my_userid VARCHAR, other_userid VARCHAR, nickname VARCHAR, markname VARCHAR, headimageurl VARCHAR, isattention INT, sex INT, signature VARCHAR, act INT, privmsg_type INT, content VARCHAR, lastTimeMill INT8, unReadNum INT, toShow VARCHAR, msgs TEXT, primary key (my_userid, other_userid))"); 
		
/*		db.execSQL("CREATE TABLE IF NOT EXISTS " 
	            + TABLENAME 
	            + " (key VARCHAR UNIQUE PRIMARY KEY , my_userid VARCHAR, other_userid VARCHAR, nickname VARCHAR, markname VARCHAR, headimageurl VARCHAR, isattention INT, sex INT, signature VARCHAR, act INT, privmsg_type INT, content VARCHAR, lastTimeMill INT8, unReadNum INT, msgs TEXT)"); 
*/
		
		messages_map = new ConcurrentLinkedHashMap.Builder<String, MessageWrapper>()
				.maximumWeightedCapacity(Constant.DIARY_CACHE_LIMIT) // max num
			    .weigher(memoryUsageWeigher)
			    .listener(listener)
			    .build();
	}
	 
	public synchronized static SqlMessageWrapperManager getInstance(){
		if(ins==null){
			ins = new SqlMessageWrapperManager();
		}
		
		return ins;
	}
	
	public String getKey(String my_userid, String other_userid){
		return my_userid + other_userid;
	}
	
	public synchronized void  removeMessageWrapper(String my_userid, String other_userid){
			if(my_userid!=null && other_userid!=null){
				String key = getKey(my_userid, other_userid);
				messages_map.remove(key);
				try{
					db.delete(TABLENAME, "my_userid==? AND other_userid==?", new String[]{my_userid, other_userid});
					//db.delete(TABLENAME, "key==?", new String[]{key});
				}catch(Exception e){
					e.printStackTrace();
				}

			}
	}
	

	/**
	 * 缓存的ts与sql中的ts需要保持一致
	 * */
	public synchronized void putMessageWrapper(String my_userid, MessageWrapper mw){
		if(my_userid!=null && mw!=null && mw.other_userid!=null){
			try {
				 db.beginTransaction();// 开始事务
				 ContentValues cv = new ContentValues();  
				 //cv.put("key", getKey(my_userid, mw.other_userid));
				 cv.put("my_userid", my_userid);
				 cv.put("other_userid", mw.other_userid);
				 
				 cv.put("unReadNum", mw.getUnreadMsgs());
				 
				 if(!TextUtils.isEmpty(mw.act)){
					 cv.put("act", mw.act);
				 }
				 
				 if(!TextUtils.isEmpty(mw.content)){
					 cv.put("content", mw.content);
				 }

				 cv.put("lastTimeMill", mw.lastTimeMill);
				 
				 if(!TextUtils.isEmpty(mw.headimageurl)){
					 cv.put("headimageurl", mw.headimageurl); 
				 }

				 if(!TextUtils.isEmpty(mw.isattention)){
					 cv.put("isattention", mw.isattention);
				 }
				 
				 if(mw.toShow){
					 cv.put("toShow", "true");
				 }else{
					 cv.put("toShow", "false");
				 }
				 
				 if(!TextUtils.isEmpty(mw.markname)){
					 cv.put("markname", mw.markname);
				 }
				 
				 if(!TextUtils.isEmpty(mw.nickname)){
					 cv.put("nickname", mw.nickname);
				 }
				 
				 if(!TextUtils.isEmpty(mw.privmsg_type)){
					 cv.put("privmsg_type", mw.privmsg_type);
				 }
				
				 if(!TextUtils.isEmpty(mw.sex)){
					 cv.put("sex", mw.sex);
				 }
				 
				 if(!TextUtils.isEmpty(mw.signature)){
					 cv.put("signature", mw.signature);
				 }
				 
				 if(mw.msgs!=null){
					 cv.put("msgs", gson.toJson(mw.msgs));
				 }
				 

				 //插入ContentValues中的数据   
				 //db.insert(TABLENAME, null, cv);  
				 //db.update(TABLENAME, cv, null, null);
				 db.replace(TABLENAME, null, cv);
				 //db.insertOrThrow(TABLENAME, null, cv);
				 db.setTransactionSuccessful(); // 调用此方法会在执行到endTransaction()
												// 时提交当前事务，如果不调用此方法会回滚事务
				 messages_map.put(getKey(my_userid, mw.other_userid), mw);

			}catch(Exception e){
				e.printStackTrace();
			}finally {
				db.endTransaction();// 由事务的标志决定是提交事务，还是回滚事务
			} 
		}
	}
	
	public synchronized  MessageWrapper getMessageWrapper(String my_userid, String other_userid){
		MessageWrapper mw = null;
		if(my_userid!=null && other_userid!=null){
			//Log.v(TAG, "getDiary - diaryuuid:" + diaryuuid);
			mw = messages_map.get(getKey(my_userid, other_userid));
			if(mw==null){
				try{
					Cursor c = db.rawQuery("SELECT * FROM " + TABLENAME + " WHERE my_userid==? AND other_userid==?", new String[]{my_userid, other_userid});  
					//Cursor c = db.rawQuery("SELECT * FROM " + TABLENAME + " WHERE key==?", new String[]{getKey(my_userid, other_userid)});  
			        if(c.moveToFirst()){
						do {
				            //long _id = c.getLong(c.getColumnIndex("_id")); 
				        	mw = new MessageWrapper(my_userid);
				        	mw.act = String.valueOf(c.getInt(c.getColumnIndex("act")));
				        	mw.content = c.getString(c.getColumnIndex("content"));
				        	mw.headimageurl = c.getString(c.getColumnIndex("headimageurl"));
				        	mw.isattention = String.valueOf(c.getInt(c.getColumnIndex("isattention")));
				        	mw.lastTimeMill = c.getLong(c.getColumnIndex("lastTimeMill"));
				        	mw.markname = c.getString(c.getColumnIndex("markname"));
				        	mw.nickname = c.getString(c.getColumnIndex("nickname"));
				        	mw.privmsg_type = String.valueOf(c.getInt(c.getColumnIndex("privmsg_type")));
				        	mw.sex = String.valueOf(c.getInt(c.getColumnIndex("sex")));
				        	mw.signature = c.getString(c.getColumnIndex("signature"));
				        	//mw.unReadNum = c.getInt(c.getColumnIndex("unReadNum"));
				        	mw.other_userid = c.getString(c.getColumnIndex("other_userid"));
				        	mw.toShow = Boolean.valueOf(c.getString(c.getColumnIndex("toShow")));
				            String data = c.getString(c.getColumnIndex("msgs"));  
				            mw.msgs =  gson.fromJson(data, new TypeToken<LinkedList<PrivateCommonMessage>>(){}.getType());
				            //mw.msgs = gson.fromJson(data, PrivateCommonMessageWrap.class);
				            break;
				        }while (c.moveToNext());
			        }

			        c.close();					
				}catch(Exception e){
					e.printStackTrace();
				}

		        
		        if(mw!=null){
		        	messages_map.put(getKey(my_userid, mw.other_userid), mw);
		        }
			}
			

		}
		
		return mw;
	}
	
	
	public synchronized void cleanUp(){
		messages_map.clear();
	}
	
	public synchronized boolean hasExceptStranger(String my_userid){
		boolean hasData = false;
		if(my_userid!=null){
			try{
				Cursor c = db.rawQuery("SELECT * FROM " + TABLENAME + " WHERE my_userid==? AND act!=?", new String[]{my_userid, "5"});  
		        hasData = c.moveToFirst();
		        c.close();				
			}catch(Exception e){
				e.printStackTrace();
			}

		}


		
		return hasData;
	
	}
	
	public synchronized ArrayList<MessageWrapper> getListExceptStranger(String my_userid){
		ArrayList<MessageWrapper> msgs = new ArrayList<MessageWrapper>();
		
		if(my_userid!=null){
			Cursor c = null;
			try{
				c = db.rawQuery("SELECT * FROM " + TABLENAME + " where my_userid == ? AND act!=?", new String[]{my_userid, "5"});  
		        if(c.moveToFirst()){
		    		do{  
		            	MessageWrapper mw = new MessageWrapper(my_userid);
		            	mw.act = String.valueOf(c.getInt(c.getColumnIndex("act")));
		            	mw.content = c.getString(c.getColumnIndex("content"));
		            	mw.headimageurl = c.getString(c.getColumnIndex("headimageurl"));
		            	mw.isattention = String.valueOf(c.getInt(c.getColumnIndex("isattention")));
		            	mw.lastTimeMill = c.getLong(c.getColumnIndex("lastTimeMill"));
		            	mw.markname = c.getString(c.getColumnIndex("markname"));
		            	mw.nickname = c.getString(c.getColumnIndex("nickname"));
		            	mw.privmsg_type = String.valueOf(c.getInt(c.getColumnIndex("privmsg_type")));
		            	mw.sex = String.valueOf(c.getInt(c.getColumnIndex("sex")));
		            	mw.signature = c.getString(c.getColumnIndex("signature"));
		            	//mw.unReadNum = c.getInt(c.getColumnIndex("unReadNum"));
		            	mw.other_userid = c.getString(c.getColumnIndex("other_userid"));
		            	mw.toShow = Boolean.valueOf(c.getString(c.getColumnIndex("toShow")));
			            String data = c.getString(c.getColumnIndex("msgs"));  
			            mw.msgs =  gson.fromJson(data, new TypeToken<LinkedList<PrivateCommonMessage>>(){}.getType());
			            //mw.msgs = gson.fromJson(data, PrivateCommonMessageWrap.class);
			            msgs.add(mw);
		            } while (c.moveToNext()); 
		        }
		        //c.close();				
			}catch(Exception e){
				e.printStackTrace();
			}finally{
				if(c!=null){
					try{
						c.close();
					}catch(Exception e){
					
					}
						
				}
				
			}

		}
		
		return msgs;
	
	}
	
	public synchronized ArrayList<MessageWrapper> getListForStranger(String my_userid){

		ArrayList<MessageWrapper> msgs = new ArrayList<MessageWrapper>();
		if(my_userid!=null){
			try{
				Cursor c = db.rawQuery("SELECT * FROM " + TABLENAME + " WHERE my_userid==? AND act==?", new String[]{my_userid, "5"});  
		        if(c.moveToFirst()){
		    		do {  
		            	MessageWrapper mw = new MessageWrapper(my_userid);
		            	mw.act = String.valueOf(c.getInt(c.getColumnIndex("act")));
		            	mw.content = c.getString(c.getColumnIndex("content"));
		            	mw.headimageurl = c.getString(c.getColumnIndex("headimageurl"));
		            	mw.isattention = String.valueOf(c.getInt(c.getColumnIndex("isattention")));
		            	mw.lastTimeMill = c.getLong(c.getColumnIndex("lastTimeMill"));
		            	mw.markname = c.getString(c.getColumnIndex("markname"));
		            	mw.nickname = c.getString(c.getColumnIndex("nickname"));
		            	mw.privmsg_type = String.valueOf(c.getInt(c.getColumnIndex("privmsg_type")));
		            	mw.sex = String.valueOf(c.getInt(c.getColumnIndex("sex")));
		            	mw.signature = c.getString(c.getColumnIndex("signature"));
		            	//mw.unReadNum = c.getInt(c.getColumnIndex("unReadNum"));
		            	mw.other_userid = c.getString(c.getColumnIndex("other_userid"));
		            	mw.toShow = Boolean.valueOf(c.getString(c.getColumnIndex("toShow")));
			            String data = c.getString(c.getColumnIndex("msgs"));  
			            mw.msgs =  gson.fromJson(data, new TypeToken<LinkedList<PrivateCommonMessage>>(){}.getType());
			            //mw.msgs = gson.fromJson(data, PrivateCommonMessageWrap.class);
			            msgs.add(mw);
		            } while (c.moveToNext()); 
		        }
		        c.close();				
			}catch(Exception e){
				e.printStackTrace();
			}

		}
		
		return msgs;
	
	}
	
	public synchronized  ArrayList<MessageWrapper> getMessages(String my_userid){
		ArrayList<MessageWrapper> msgs = new ArrayList<MessageWrapper>();
		
		if(my_userid!=null){
			try{
				Cursor c = db.rawQuery("SELECT * FROM " + TABLENAME + " WHERE my_userid==?", new String[]{my_userid});  
		        if(c.moveToFirst()){
		    		do {  
		            	MessageWrapper mw = new MessageWrapper(my_userid);
		            	mw.act = String.valueOf(c.getInt(c.getColumnIndex("act")));
		            	mw.content = c.getString(c.getColumnIndex("content"));
		            	mw.headimageurl = c.getString(c.getColumnIndex("headimageurl"));
		            	mw.isattention = String.valueOf(c.getInt(c.getColumnIndex("isattention")));
		            	mw.lastTimeMill = c.getLong(c.getColumnIndex("lastTimeMill"));
		            	mw.markname = c.getString(c.getColumnIndex("markname"));
		            	mw.nickname = c.getString(c.getColumnIndex("nickname"));
		            	mw.privmsg_type = String.valueOf(c.getInt(c.getColumnIndex("privmsg_type")));
		            	mw.sex = String.valueOf(c.getInt(c.getColumnIndex("sex")));
		            	mw.signature = c.getString(c.getColumnIndex("signature"));
		            	//mw.unReadNum = c.getInt(c.getColumnIndex("unReadNum"));
		            	mw.other_userid = c.getString(c.getColumnIndex("other_userid"));
		            	mw.toShow = Boolean.valueOf(c.getString(c.getColumnIndex("toShow")));
			            String data = c.getString(c.getColumnIndex("msgs"));  
			            mw.msgs =  gson.fromJson(data, new TypeToken<LinkedList<PrivateCommonMessage>>(){}.getType());
			            //mw.msgs = gson.fromJson(data, PrivateCommonMessageWrap.class);
			            msgs.add(mw);
		            }  while (c.moveToNext());
		        }
		        c.close();				
			}catch(Exception e){
				e.printStackTrace();
			}

		}
		
		return msgs;
	}

}
