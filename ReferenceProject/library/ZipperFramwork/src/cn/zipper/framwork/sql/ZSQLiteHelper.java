package cn.zipper.framwork.sql;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import cn.zipper.framwork.core.ZApplication;
import cn.zipper.framwork.core.ZLog;

public abstract class ZSQLiteHelper extends SQLiteOpenHelper {
	
	private SQLiteDatabase db;

	
	protected ZSQLiteHelper(String name, int version) {
		super(ZApplication.getInstance(), name, null, version);
		// TODO Auto-generated constructor stub
		db = getWritableDatabase();
	}
	
	@Override
	public abstract void onCreate(SQLiteDatabase db);

	@Override
	public abstract void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion);
	
	
	protected final  void execSQL(String sql) {
		db.execSQL(sql);
	}
	
	protected final  void execSQL(String sql, Object[] bindArgs) {
		db.execSQL(sql, bindArgs);
	}
	
	protected final  Cursor rawQuery(String sql, String[] selectionArgs) {
		return db.rawQuery(sql, selectionArgs);
	}
	
	protected final  Object rawQuery(ZSQLiteCursorProcessor processor , String sql, String[] selectionArgs) {
		Object object = null;
		
		Cursor cursor = db.rawQuery(sql, selectionArgs);
		object = processor.onGetCursor(cursor);
		cursor.close();
		
		return object;
	}
	
	protected final  Cursor query(String table, String[] projection, String selection, String[] selectionArgs, String orderBy) {
        return db.query(table, projection, selection, selectionArgs, null, null, orderBy);
    }
    
	protected final  long insert(String table, ContentValues values) {
        return db.insert(table, null, values);
    }
    
	protected final  int update(String table, ContentValues values, String selection, String[] selectionArgs) {
        return db.update(table, values, selection, selectionArgs);
    }
    
	protected final  int delete(String table, String selection, String[] selectionArgs) {
        return db.delete(table, selection, selectionArgs);
    }
	
    protected final  void createTable(String name) {
		ZLog.alert();
		ZLog.e("this method is null...");
	}
    
	protected final  void deleteTable(String name) {
		db.execSQL("DROP TABLE IF EXISTS " + name);
	}
	
	protected final  void beginTransaction() {
		db.beginTransaction();
	}
	
	protected final  void setTransactionSuccessful() {
		db.setTransactionSuccessful();
	}
	
	protected final  void endTransaction(boolean successful) {
		if (successful) {
			setTransactionSuccessful();
		}
		db.endTransaction();
	}
	
	protected final  void close2() {
		db.close();
	}
	
	protected final String getPath() {
		return db.getPath();
	}
	
	protected final ContentValues getNewContentValues() {
		return new ContentValues();
	}
	
	/**
	 * 
	 * @author Administrator
	 *
	 */
	public static interface ZSQLiteCursorProcessor {
		public abstract Object onGetCursor(Cursor cursor);
	}

}
