package com.etoc.weflow;

import java.io.File;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

import cn.jpush.android.api.JPushInterface;

import com.etoc.weflow.event.DialogUtils;
import com.etoc.weflow.event.RequestEvent;
import com.etoc.weflow.utils.ConStant;
import com.nostra13.universalimageloader.api.MyImageLoader;
import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiscCache;
import com.nostra13.universalimageloader.cache.memory.impl.LRULimitedMemoryCache;
import com.nostra13.universalimageloader.cache.memory.impl.WeakMemoryCache;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.utils.StorageUtils;

import de.greenrobot.event.EventBus;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import android.widget.Toast;

public class WeFlowApplication extends Application {
	
	private static WeFlowApplication appinstance;
	private long lastRespNullTs = 0;
	private LinkedList<Activity> activityList = new LinkedList<Activity>(); 
	public static int totalFlow = 0;
	private  Set<String> tags = new HashSet<String>();
	
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
}
