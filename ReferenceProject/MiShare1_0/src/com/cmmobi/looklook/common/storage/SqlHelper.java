package com.cmmobi.looklook.common.storage;

import com.cmmobi.looklook.MainApplication;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

public class SqlHelper {
	private static final String DATABASENAME = "looklook.db";
	private static SqlHelper ins;
	private SQLiteDatabase db;
	private Context context;
	
	private SqlHelper(){
		context = MainApplication.getAppInstance();
		db = context.openOrCreateDatabase(DATABASENAME, Context.MODE_PRIVATE, null);  
	}
	
	public static SqlHelper getInstance(){
		if(ins==null){
			ins = new SqlHelper();
		}
		
		return ins;
	}
	
	public SQLiteDatabase getDb(){
		return db;
	}
}
