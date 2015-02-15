/**
 * 
 */
package com.cmmobi.looklook.common.cache;

import java.io.FileNotFoundException;

import android.content.Context;
import android.graphics.Bitmap;

import com.cmmobi.looklook.common.utils.MD5;
import com.cmmobi.sns.utils.BitmapHelper;

/**
 * @author wuxiang
 * @email  wuxiang@cmmobi.com
 * @date   2013-4-28
 */
public class ActiveListImageLoader extends AbsImageLoader<Object> {
	
	public ActiveListImageLoader(Context context) {
		super(context);
	}

	@Override
	public void setDefault(Object holder) {
//		holder.pic.setImageResource(R.drawable.huodong_moren);
	}

	@Override
	public void setImageViewFromCache(Object holder,Bitmap bitmap) {
//		holder.pic.setImageBitmap(bitmap);
	}

	@Override
	public Bitmap loadImageFromLocal(String url) {
//		// TODO Auto-generated method stub
//		return super.loadImageFromLocal(url);
		Bitmap bitmap = null;
		try {
			String Key = MD5.encode(CacheManager.CHACHE_PREFIX, url.getBytes());
			bitmap=BitmapHelper.getBitmapFromInputStream(context
					.openFileInput(Key), 160);
//			bitmap = BitmapFactory.decodeStream(context
//					.openFileInput(Key));
			return bitmap;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return null;
		}
	}
	
}
