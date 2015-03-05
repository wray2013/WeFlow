package com.nostra13.universalimageloader.api;

import android.widget.ImageView;

import com.nostra13.universalimageloader.cache.disc.DiskCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.imageaware.ImageAware;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.nostra13.universalimageloader.core.listener.ImageLoadingProgressListener;

public class MyImageLoader {
	private static MyImageLoader ins = null;
	private ImageLoader member = null;
	public static MyImageLoader getInstance(){
		if(ins==null){
			ins = new MyImageLoader();
		}
		
		return ins;
	}
	
	private MyImageLoader(){
		member = ImageLoader.getInstance();
	}
	
	public void displayImage(String uri, ImageAware imageAware){
		try{
			member.displayImage(uri, imageAware);
		}catch(Error e){
			e.printStackTrace();
//			if(e instanceof java.lang.OutOfMemoryError){
//				ImageLoader.getInstance().clearMemoryCache();
//			}
		}
	}
	
	public void displayImage(String uri, ImageView imageView){
		try{
			member.displayImage(uri, imageView);
		}catch(Error e){
			e.printStackTrace();
//			if(e instanceof java.lang.OutOfMemoryError){
//				ImageLoader.getInstance().clearMemoryCache();
//			}
		}
	}
	
	public void displayImage(String uri, ImageAware imageAware, DisplayImageOptions options){
		try{
			member.displayImage(uri, imageAware, options);
		}catch(Error e){
			e.printStackTrace();
//			if(e instanceof java.lang.OutOfMemoryError){
//				ImageLoader.getInstance().clearMemoryCache();
//			}
		}
	}

	public void displayImage(String uri, ImageAware imageAware, ImageLoadingListener listener){
		try{
			member.displayImage(uri, imageAware, listener);
		}catch(Error e){
			e.printStackTrace();
//			if(e instanceof java.lang.OutOfMemoryError){
//				ImageLoader.getInstance().clearMemoryCache();
//			}
		}
	}
	
	public void displayImage(String uri, ImageView imageView, DisplayImageOptions options){
		try{
			member.displayImage(uri, imageView, options);
		}catch(Error e){
			e.printStackTrace();
//			if(e instanceof java.lang.OutOfMemoryError){
//				ImageLoader.getInstance().clearMemoryCache();
//			}
		}
	}
	
	public void displayImage(String uri, ImageView imageView, ImageLoadingListener listener){
		try{
			member.displayImage(uri, imageView, listener);
		}catch(Error e){
			e.printStackTrace();
//			if(e instanceof java.lang.OutOfMemoryError){
//				ImageLoader.getInstance().clearMemoryCache();
//			}
		}
	}
	
	public void displayImage(String uri, ImageAware imageAware, DisplayImageOptions options, ImageLoadingListener listener){
		try{
			member.displayImage(uri, imageAware, options, listener);
		}catch(Error e){
			e.printStackTrace();
//			if(e instanceof java.lang.OutOfMemoryError){
//				ImageLoader.getInstance().clearMemoryCache();
//			}
		}
	}
	
	public void displayImage(String uri, ImageView imageView, DisplayImageOptions options, ImageLoadingListener listener){
		try{
			member.displayImage(uri, imageView, options, listener);
		}catch(Error e){
			e.printStackTrace();
//			if(e instanceof java.lang.OutOfMemoryError){
//				ImageLoader.getInstance().clearMemoryCache();
//			}

		}
	}
	
	public void displayImage(String uri, ImageView imageView, DisplayImageOptions options, ImageLoadingListener listener, ImageLoadingProgressListener progressListener){
		try{
			member.displayImage(uri, imageView, options, listener, progressListener);
		}catch(Error e){
			e.printStackTrace();
//			if(e instanceof java.lang.OutOfMemoryError){
//				ImageLoader.getInstance().clearMemoryCache();
//			}
		}
	}

	public DiskCache getDiskCache() {
		// TODO Auto-generated method stub
		return member.getDiskCache();
	}

}
