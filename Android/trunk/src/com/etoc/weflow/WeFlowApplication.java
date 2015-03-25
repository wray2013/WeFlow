package com.etoc.weflow;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import android.app.Activity;
import android.app.Application;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ResolveInfo;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.Toast;
import cn.jpush.android.api.JPushInterface;

import com.etoc.weflow.dao.AccountInfo;
import com.etoc.weflow.dao.AccountInfoDao;
import com.etoc.weflow.dao.DaoMaster;
import com.etoc.weflow.dao.DaoMaster.DevOpenHelper;
import com.etoc.weflow.dao.DaoSession;
import com.etoc.weflow.dao.MyMessage;
import com.etoc.weflow.dao.MyMessageDao;
import com.etoc.weflow.event.DialogUtils;
import com.etoc.weflow.event.RequestEvent;
import com.etoc.weflow.utils.ConStant;
import com.nostra13.universalimageloader.api.MyImageLoader;
import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiscCache;
import com.nostra13.universalimageloader.cache.memory.impl.LRULimitedMemoryCache;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.utils.StorageUtils;

import de.greenrobot.event.EventBus;

public class WeFlowApplication extends Application {
	
	private static WeFlowApplication appinstance;
	private long lastRespNullTs = 0;
	private LinkedList<Activity> activityList = new LinkedList<Activity>(); 
	public static int totalFlow = 0;
	private  Set<String> tags = new HashSet<String>();
	
	private DaoMaster daoMaster;
	private DaoSession daoSession;
	private SQLiteDatabase db;
	private AccountInfoDao accountInfoDao;
	private MyMessageDao myMessageDao;
	private static AccountInfo accountInfo;
	
	@Override
	public void onCreate() {
		super.onCreate();
		JPushInterface.setDebugMode(true); 	//设置开启日志,发布时请关闭日志
        JPushInterface.init(this);     		// 初始化 JPush
        
//        Set<String> tags = new HashSet<String>();
        tags.add("weflow");
//		tags.add("");
		JPushInterface.setTags(this, tags, null);
//		Log.i(TAG, "JPushInterface setTags " + tags.toString());
		
		appinstance = this;
		
		/**
		 * CrashHandler Initalizing
		 */
		CrashHandler crashHandler = CrashHandler.getInstance();
		crashHandler.init(getApplicationContext());
		
		/**
		 * ImageLoader Initalizing
		 */
		File cacheDir = StorageUtils.getOwnCacheDirectory(getApplicationContext(), ConStant.getImageLoaderCachePath());
		ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(this)
			.threadPriority(Thread.NORM_PRIORITY - 2)
//			.memoryCache(new WeakMemoryCache())
			.memoryCache(new LRULimitedMemoryCache(5*1024*1024))
			.denyCacheImageMultipleSizesInMemory()
			.diskCache(new UnlimitedDiscCache(cacheDir))
			.tasksProcessingOrder(QueueProcessingType.FIFO)
			.build();

		ImageLoader.getInstance().init(config);
		MyImageLoader.getInstance();
		EventBus.getDefault().register(this);
        
	}

	public final static WeFlowApplication getAppInstance() {
		return appinstance;
	}
	
	public List<MyMessage> getMyMessage() {
		List<MyMessage> list = new ArrayList<MyMessage>();
		if(daoSession == null || !db.isOpen()) {
			DevOpenHelper helper = new DaoMaster.DevOpenHelper(this, "weflowdb", null);
	        db = helper.getWritableDatabase();
	        daoMaster = new DaoMaster(db);
	        daoSession = daoMaster.newSession();
		}
        
		myMessageDao = daoSession.getMyMessageDao();
		if(myMessageDao.count() > 0) {
			list = myMessageDao.loadAll();
		}
		db.close();
		return list;
	}
	
	public AccountInfo getAccountInfo() {
		if(daoSession == null || !db.isOpen()) {
			DevOpenHelper helper = new DaoMaster.DevOpenHelper(this, "weflowdb", null);
	        db = helper.getWritableDatabase();
	        daoMaster = new DaoMaster(db);
	        daoSession = daoMaster.newSession();
		}
        if(accountInfo == null) {
        	accountInfo = new AccountInfo();
        }
        
		accountInfoDao = daoSession.getAccountInfoDao();
		if(accountInfoDao.count() > 0) {
			List<AccountInfo> list = accountInfoDao.loadAll();
			accountInfo = list.get(0);
		}
		db.close();
		return accountInfo;
	}
	
	public void PersistAccountInfo(AccountInfo acc) {
		if(acc != null) {
			DevOpenHelper helper = new DaoMaster.DevOpenHelper(this, "weflowdb", null);
	        db = helper.getWritableDatabase();
	        daoMaster = new DaoMaster(db);
	        daoSession = daoMaster.newSession();
	        
			accountInfoDao = daoSession.getAccountInfoDao();
			accountInfoDao.deleteAll();
			accountInfoDao.insertOrReplace(acc);
			db.close();
		}
	}
	
	public void addJPushTag(String tag) {
		tags.add(tag);
		JPushInterface.setTags(this, tags, null);
	}
	
    // ���Activity�������� 
    public void addActivity(Activity activity) { 
         activityList.add(activity); 
    } 
    
    public void removeActivity(Activity activity){
    	activityList.remove(activity); 
    }
    
    public Activity getTopActivity() {
    	if(activityList != null && activityList.size() > 0) {
    		return activityList.getLast();
    	}
    	return null;
    }
    
    // ��������Activity��finish 
    public void cleanAllActivity() { 
	    for (Activity activity : activityList) { 
	    	if(activity!=null){
	    		activity.finish(); 
	    	}
	        
	    } 
    }
    
    public void onEventMainThread(RequestEvent event) {
		switch(event) {
		case RESP_NULL:
	    	ConnectivityManager manager = (ConnectivityManager) WeFlowApplication.getAppInstance().getSystemService(Context.CONNECTIVITY_SERVICE);  
	        NetworkInfo mobileInfo = manager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);  
	        NetworkInfo wifiInfo = manager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);  
	        
	        boolean isConnected = wifiInfo.isConnected()|mobileInfo.isConnected();

	        long curRespNullTs = System.currentTimeMillis();
	        if(curRespNullTs - lastRespNullTs > 3000){
		        if(isConnected){
			    	Toast.makeText(WeFlowApplication.getAppInstance(), "网络不佳，请检查网络连接", Toast.LENGTH_LONG).show();
		        }else{
		        	Toast.makeText(WeFlowApplication.getAppInstance(), "没有网络，请检查网络连接", Toast.LENGTH_LONG).show();
		        }
		        lastRespNullTs = curRespNullTs;
	        }

	        break;
		case LOADING_START:
			DialogUtils.SendLoadingDialogStart(this);
			break;
		case LOADING_END:
			DialogUtils.SendLoadingDialogEnd(this);
			break;
		default:
			break;
		}
	}
    
    public void setFlowCoins(String flowcoins) {
		accountInfo = WeFlowApplication.getAppInstance().getAccountInfo();
		if (accountInfo != null && flowcoins != null) {
			accountInfo.setFlowcoins(flowcoins);
			PersistAccountInfo(accountInfo);
		}
	}
    
    public static void openApk(String packageName,Context context) { 
        PackageManager packageManager = context.getPackageManager(); 
        PackageInfo pi = null;
        try { 
             
            pi = packageManager.getPackageInfo("cld.navi.mainframe", 0); 
        } catch (NameNotFoundException e) { 
             
        } 
        Intent resolveIntent = new Intent(Intent.ACTION_MAIN, null); 
        resolveIntent.addCategory(Intent.CATEGORY_LAUNCHER); 
        resolveIntent.setPackage(pi.packageName); 
 
        List<ResolveInfo> apps = packageManager.queryIntentActivities(resolveIntent, 0); 
 
        ResolveInfo ri = apps.iterator().next(); 
        if (ri != null ) { 
            String className = ri.activityInfo.name; 
 
            Intent intent = new Intent(Intent.ACTION_MAIN); 
            intent.addCategory(Intent.CATEGORY_LAUNCHER); 
 
            ComponentName cn = new ComponentName(packageName, className); 
 
            intent.setComponent(cn); 
            context.startActivity(intent); 
        } 
    } 
}
