/**
 * 
 */
package com.cmmobi.looklook.common.cache;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedList;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.http.AndroidHttpClient;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.BaseAdapter;
import cn.zipper.framwork.io.cache.ZMemoryCacher;
import cn.zipper.framwork.io.cache.ZMemoryCacher.CACHE_MODE;
import cn.zipper.framwork.io.cache.ZMemoryCacherFactory;

import com.cmmobi.looklook.common.utils.MD5;
import com.cmmobi.sns.utils.BitmapHelper;
import com.googlecode.concurrentlinkedhashmap.Weighers;

/**
 * @author wuxiang
 * @email  wuxiang@cmmobi.com
 * @date   2013-4-28
 */
public abstract class AbsImageLoader<T> {

	private static final String TAG = "ImageLoader";
	/*private static final int MAX_CAPACITY = 50;
	private static final long DELAY_BEFORE_PURGE = 30*60*1000;
	private ConcurrentHashMap<String, SoftReference<Bitmap>> mSoftCache = new ConcurrentHashMap<String, SoftReference<Bitmap>>(
			MAX_CAPACITY / 2);;
	private HashMap<String, Bitmap> mHardCache = new LinkedHashMap<String, Bitmap>(
			MAX_CAPACITY / 2, 0.75f, true) {
		private static final long serialVersionUID = 1L;

		protected boolean removeEldestEntry(Entry<String, Bitmap> eldest) {
			if (size() > MAX_CAPACITY) {
				mSoftCache.put(eldest.getKey(), new SoftReference<Bitmap>(
						eldest.getValue()));
				return true;
			}
			return false;
		};
	};*/
	/*private Runnable mClearCache = new Runnable() {
		@Override
		public void run() {
			clear();
		}
	};
	private Handler mPurgeHandler = new Handler();

	private void resetPurgeTimer() {
		mPurgeHandler.removeCallbacks(mClearCache);
		mPurgeHandler.postDelayed(mClearCache, DELAY_BEFORE_PURGE);
	}
	
	*//**
	 * 返回缓存，如果没有则返回null
	 * 
	 * @param url
	 * @return
	 *//*
	public Bitmap getBitmapFromCache(String url) {
		Bitmap bitmap = null;
		synchronized (mHardCache) {
			bitmap = mHardCache.get(url);
			if (bitmap != null) {
				mHardCache.remove(url);
				mHardCache.put(url, bitmap);
				return bitmap;
			}
		}

		SoftReference<Bitmap> softReference = mSoftCache.get(url);
		if (softReference != null) {
			bitmap = softReference.get();
			if (bitmap == null) {// 已经被gc回收了
				mSoftCache.remove(url);
			}
		}
		return bitmap;
	}*/
	
	protected Context context;
	private ZMemoryCacher<Bitmap> cache=ZMemoryCacherFactory.get("bitmap_cache", CACHE_MODE.MIX, Bitmap.class, Weighers.entrySingleton(), 200);
	public AbsImageLoader(Context context){
		this.context=context;
	}
	
	public Bitmap getBitmapFromCache(String url) {
		Bitmap bitmap = null;
		bitmap=cache.get(url);
		return bitmap;
	}
	
	public void addImage2Cache(String url, Bitmap value) {
		cache.put(url, value);
	}

	public static LinkedList<String> list=new LinkedList<String>(); 
	public void loadImage(String url, BaseAdapter adapter, T holder) {
//		resetPurgeTimer();
		Bitmap bitmap = getBitmapFromCache(url);// 从缓存中读取
		if (bitmap == null) {
			if(list.contains(url)){
				return ;
			}
			list.add(url);
			ImageLoadTask imageLoadTask = new ImageLoadTask();
			imageLoadTask.execute(url, adapter);
			setDefault(holder);
		} else {
			setImageViewFromCache(holder,bitmap);
		}
	}
	
	public abstract void setDefault(T holder);
	
	public abstract void setImageViewFromCache(T holder,Bitmap bitmap);

	/*public void addImage2Cache(String url, Bitmap value) {
		if (value == null || url == null) {
			return;
		}
		synchronized (mHardCache) {
			mHardCache.put(url, value);
		}
	}*/

	class ImageLoadTask extends AsyncTask<Object, Void, Bitmap> {
		String url;
		BaseAdapter adapter;

		@Override
		protected Bitmap doInBackground(Object... params) {
			url = (String) params[0];
			adapter = (BaseAdapter) params[1];
			Bitmap drawable=loadImageFromLocal(url);//获取本地图片
			if(null==drawable){
				drawable = loadImageFromInternet(url);// 获取网络图片
			}
			return drawable;
		}

		@Override
		protected void onPostExecute(Bitmap result) {
			list.remove(url);
			if (result == null) {
				return;
			}
			addImage2Cache(url, result);// 放入缓存
			adapter.notifyDataSetChanged();
		}
	}
	
	public Bitmap loadImageFromLocal(String url){
		Bitmap bitmap = null;
		try {
			String Key = MD5.encode(CacheManager.CHACHE_PREFIX, url.getBytes());
			bitmap = BitmapFactory.decodeStream(context
					.openFileInput(Key));
			return bitmap;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	int imageWidth=0;
	public void setImageWidth(int imageWidth){
		this.imageWidth=imageWidth;
	}

	public Bitmap loadImageFromInternet(String url) {
		Bitmap bitmap = null;
		HttpClient client = AndroidHttpClient.newInstance("Android");
		HttpParams params = client.getParams();
		HttpConnectionParams.setConnectionTimeout(params, 3000);
		HttpConnectionParams.setSocketBufferSize(params, 3000);
		HttpResponse response = null;
		InputStream inputStream = null;
		HttpGet httpGet = null;
		try {
			httpGet = new HttpGet(url);
			response = client.execute(httpGet);
			int stateCode = response.getStatusLine().getStatusCode();
			if (stateCode != HttpStatus.SC_OK) {
				Log.d(TAG, "func [loadImage] stateCode=" + stateCode);
				return bitmap;
			}
			HttpEntity entity = response.getEntity();
			String Key = MD5.encode(CacheManager.CHACHE_PREFIX, url.getBytes());
			FileOutputStream mOutput = context.openFileOutput(Key,Activity.MODE_PRIVATE);
			if (entity != null) {
				try {
					inputStream = entity.getContent();
					bitmap=BitmapHelper.getBitmapFromInputStream(inputStream, imageWidth);
					if(bitmap!=null){
						byte[] imageBytes=BitmapHelper.getByteArrayFromBitmap(bitmap);
						mOutput.write(imageBytes);
						mOutput.flush();
					}
					return bitmap;
				} finally {
					if(mOutput!=null){
						mOutput.close();
					}
					if (inputStream != null) {
						inputStream.close();
					}
					entity.consumeContent();
				}
			}
		} catch (ClientProtocolException e) {
			httpGet.abort();
			e.printStackTrace();
		} catch (IOException e) {
			httpGet.abort();
			e.printStackTrace();
		} finally {
			((AndroidHttpClient) client).close();
		}
		return bitmap;
	}

	/*private void clear() {
		mHardCache.clear();
		mSoftCache.clear();
	}*/
}
