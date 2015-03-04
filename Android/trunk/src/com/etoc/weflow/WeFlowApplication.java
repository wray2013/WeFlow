package com.etoc.weflow;

import java.util.LinkedList;

import com.etoc.weflow.event.DialogUtils;
import com.etoc.weflow.event.RequestEvent;
import com.nostra13.universalimageloader.api.MyImageLoader;
import com.nostra13.universalimageloader.cache.memory.impl.WeakMemoryCache;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;

import de.greenrobot.event.EventBus;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.Toast;

public class WeFlowApplication extends Application {
	
	private static WeFlowApplication appinstance;
	private long lastRespNullTs = 0;
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
		MyImageLoader.getInstance();
		EventBus.getDefault().register(this);
	}

	public final static WeFlowApplication getAppInstance() {
		return appinstance;
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
