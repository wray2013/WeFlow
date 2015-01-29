package com.etoc.weflow;

import java.util.LinkedList;

import com.nostra13.universalimageloader.cache.memory.impl.WeakMemoryCache;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;

import android.app.Activity;
import android.app.Application;

public class WeFlowApplication extends Application {
	
	private static WeFlowApplication appinstance;
	private LinkedList<Activity> activityList = new LinkedList<Activity>(); 
	
	@Override
	public void onCreate() {
		super.onCreate();
		appinstance = this;
		
		/**
		 * CrashHandler Initalizing
		 */
		CrashHandler crashHandler = CrashHandler.getInstance();
		crashHandler.init(getApplicationContext());
		
		/**
		 * ImageLoader Initalizing
		 */
		ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(this)
		.threadPriority(Thread.NORM_PRIORITY - 2)
		.memoryCache(new WeakMemoryCache())
		.denyCacheImageMultipleSizesInMemory()
		.tasksProcessingOrder(QueueProcessingType.LIFO)
//		.discCacheFileNameGenerator(new Md5FileNameGenerator())
//		.enableLogging() // Not necessary in common
		.build();

		ImageLoader.getInstance().init(config);
		
	}

	public final static WeFlowApplication getAppInstance() {
		return appinstance;
	}
	
    // 添加Activity到容器中 
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
    
    // 遍历所有Activity并finish 
    public void cleanAllActivity() { 
	    for (Activity activity : activityList) { 
	    	if(activity!=null){
	    		activity.finish(); 
	    	}
	        
	    } 
    }
}
