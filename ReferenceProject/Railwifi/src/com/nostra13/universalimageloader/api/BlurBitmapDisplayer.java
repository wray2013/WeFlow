package com.nostra13.universalimageloader.api;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Build.VERSION;
import android.util.Log;

import com.cmmobi.railwifi.MainApplication;
import com.googlecode.concurrentlinkedhashmap.ConcurrentLinkedHashMap;
import com.nostra13.universalimageloader.core.assist.LoadedFrom;
import com.nostra13.universalimageloader.core.display.BitmapDisplayer;
import com.nostra13.universalimageloader.core.imageaware.ImageAware;

public class BlurBitmapDisplayer implements BitmapDisplayer{
	
	public static enum BlurType{
		Top, Bottom,Both
	}

	private static final String TAG = "BlurBitmapDisplayer";
	
	private static Map<String, WeakReference<Bitmap>> _cache = new HashMap<String, WeakReference<Bitmap>>();
	
	BlurType type;
	double headratio;
	double bottomratio;

	private static Bitmap lastBitMap;
	
	/**
	 * @param type 目前支持从顶部和从底部开始毛玻璃化
	 * @param headratio 0-1  小于0则全部毛玻璃化，大于0则根据type截取height像素高度的区域毛玻璃化
	 * @param bottomratio 0-1  小于0则全部毛玻璃化，大于0则根据type截取height像素高度的区域毛玻璃化
	 * */
	public BlurBitmapDisplayer(BlurType type, double headratio, double bottomratio){
		this.type = type;
		this.headratio = headratio;
		this.bottomratio = bottomratio;
	}

	@Override
	public void display(Bitmap bitmap, ImageAware imageAware, LoadedFrom loadedFrom) {
		imageAware.setImageBitmap(bitmap);
		
//		String key = String.valueOf(bitmap.hashCode()); 
//		if(_cache.containsKey(key)){
//			Bitmap b = _cache.get(key).get();
//			if(b!=null){
//				Log.v(TAG, "cache it");
//				imageAware.setImageBitmap(b);
//				return;
//			}
//			
//		}
//		Log.v(TAG, "need bitmap.copy");
//		Bitmap copy_bitmap = bitmap.copy(bitmap.getConfig(), true);
//		
//		boolean open = false;
//		
//		if(open){
//			Bitmap jiequ_bottom_bitmap = null;
//			Bitmap jiequ_head_bitmap = null;
//			
//			int w = bitmap.getWidth();
//			int h = bitmap.getHeight();
//			
//			int w_head_start = 0;
//			int h_head_start = 0;
//			int height_head = (int) (h*headratio);
//			
//			int w_bottom_start = 0;
//			int h_bottom_start = 0;
//			int height_bottom = (int) (h*bottomratio);
//
//			if(bottomratio<0||bottomratio>1){
//				jiequ_bottom_bitmap = bitmap;
//			}else{
//				if(type==BlurType.Bottom){
//					h_bottom_start = (int) (h*(1-bottomratio));
//					jiequ_bottom_bitmap = Bitmap.createBitmap(copy_bitmap, 0, h_bottom_start, w, height_bottom);  
//				}else if(type==BlurType.Top){
//					jiequ_bottom_bitmap = Bitmap.createBitmap(copy_bitmap, 0, 0, w, height_head);  
//				}else{
//					h_bottom_start = (int) (h*(1-bottomratio));
//					jiequ_bottom_bitmap = Bitmap.createBitmap(copy_bitmap, 0, h_bottom_start, w, height_bottom);
//					jiequ_head_bitmap = Bitmap.createBitmap(copy_bitmap, 0, h_head_start, w, height_head);  
//				}
//			}
//
//			Paint paint = new Paint();
//
//			Canvas canvas = new Canvas(copy_bitmap); 
//			
//			if(jiequ_bottom_bitmap!=null){
//				Bitmap blurBottomBitmap = Blur.fastblur(MainApplication.getAppInstance(), jiequ_bottom_bitmap, 12);
//				canvas.drawBitmap(blurBottomBitmap, w_bottom_start, h_bottom_start, paint);
//				blurBottomBitmap.recycle();
//				blurBottomBitmap = null;
//			}
//
//			if(jiequ_head_bitmap!=null){
//				Bitmap blurTopBitmap = Blur.fastblur(MainApplication.getAppInstance(), jiequ_head_bitmap, 12);
//				canvas.drawBitmap(blurTopBitmap, w_head_start, h_head_start, paint); 
//				blurTopBitmap.recycle();
//				blurTopBitmap = null;
//			}
//			
//			canvas.save(Canvas.ALL_SAVE_FLAG);
//			canvas.restore();
//			
//			if(jiequ_head_bitmap!=null){
//				jiequ_head_bitmap.recycle();
//			}
//
//			if(jiequ_bottom_bitmap!=null){
//				jiequ_bottom_bitmap.recycle();
//			}
//
//			
//			jiequ_head_bitmap = null;
//			jiequ_bottom_bitmap = null;
//			canvas = null;
//		}else{
//			copy_bitmap = Blur.fastblurRatio(MainApplication.getAppInstance(), bitmap, 0.8, 1.0f, 12);
//		}
//
//		imageAware.setImageBitmap(copy_bitmap);
//		_cache.put(key, new WeakReference<Bitmap>(copy_bitmap));
	
	}
	

}
