package com.cmmobi.railwifi.view.gifview;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import com.cmmobi.railwifi.cache.GifFileCache;
import com.cmmobi.railwifi.cache.GifMemoryCache;

import android.content.Context;

import android.os.AsyncTask;
import android.util.Log;
import cn.trinea.android.common.util.StreamUtils;


public class GifLoader {

	GifMemoryCache memoryCache = new GifMemoryCache();
	GifFileCache fileCache;

	private static final String TAG = "GifLoader";

	public GifLoader(Context context, String cache_folder_name) {
		fileCache = new GifFileCache(context, cache_folder_name, ".gif");
	}

	public void DisplayImage(String url, GifView gifImageView, int stub_id)
    {
		
        //imageViews.put(imageView, url);
    	byte[] movie= memoryCache.get(url);
        if(movie!=null) {
            gifImageView.setGifImage(movie);    
        } else {
               
        	Log.i(TAG, "No memory cache for " + url);
            //queuePhoto(url, imageView);
            gifImageView.setImageResource(stub_id);
                
            DisplayImageTask imageTask = new DisplayImageTask(url, gifImageView);
                
            imageTask.execute(stub_id);
                
        }
        
    }

	private byte[] getMovie(String url) {
		Log.i(TAG, "Try getting file cache for " + url);
		File f = fileCache.getFile(url);

		// from SD cache
		byte[] b = decodeFile(f);
		if (b != null) {
			return b;
		}

		// from web
		try {
			byte[] movie = null;
			URL imageUrl = new URL(url);
			HttpURLConnection conn = (HttpURLConnection) imageUrl
					.openConnection();
			conn.setConnectTimeout(30000);
			conn.setReadTimeout(30000);
			conn.setInstanceFollowRedirects(true);
			InputStream is = conn.getInputStream();
			OutputStream os = new FileOutputStream(f);
			StreamUtils.CopyStream(is, os);
			is.close();
			os.close();
			conn.disconnect();
			movie = decodeFile(f);
			return movie;
		} catch (Throwable ex) {
			ex.printStackTrace();
			if (ex instanceof OutOfMemoryError)
				memoryCache.clear();
			return null;
		}
	}

	// decodes image and scales it to reduce memory consumption
	private byte[] decodeFile(File f) {
		Log.v(TAG, "decodeFile:" + f.getAbsolutePath());
//		m = byte[].decodeFile(f.getAbsolutePath());
//		m = byte[].decodeStream(new FileInputStream(f));
		
		
		byte[] m = null;
		
		try {
			int total_len = (int)f.length();

			FileInputStream fi = new FileInputStream(f);
			m = new byte[total_len];
			for(int index=0; index<total_len; index++){
				int c = fi.read();
				if(c==-1){
					break;
				}
				m[index] = (byte)c;	
			}
			fi.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			m = null;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			m = null;
		}


		
		return m;
	}

	public void clearCache() {
		memoryCache.clear();
		fileCache.clear();
	}

	class DisplayImageTask extends AsyncTask<Integer, Void, byte[]> {

		private String url;
		private GifView imageView;
		private byte[] movie;

		public DisplayImageTask(String url, GifView imageView) {
			this.url = url;
			this.imageView = imageView;
		}

		protected byte[] doInBackground(Integer... params) {
			try {

				movie = getMovie(url);

				if (movie != null) {
					imageView.setGifImage(movie);
					memoryCache.put(url, movie);
				} else {
					imageView.setImageResource(params[0]);
				}

			} catch (Throwable th) {
				th.printStackTrace();
			}

			return movie;
		}

		protected void onPostExecute(byte[] bmp) {
			// if(imageViewReused(url,imageView)) { return null; }
		}
	}

}
