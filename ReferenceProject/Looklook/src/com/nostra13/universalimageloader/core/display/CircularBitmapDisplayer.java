package com.nostra13.universalimageloader.core.display;

import android.graphics.Bitmap;
import android.widget.ImageView;

import com.cmmobi.looklook.common.utils.BitmapUtils;
import com.nostra13.universalimageloader.core.assist.LoadedFrom;



public class CircularBitmapDisplayer implements BitmapDisplayer {

	@Override
	public Bitmap display(Bitmap bitmap, ImageView imageView, LoadedFrom loadedFrom) {
		// TODO Auto-generated method stub
		Bitmap ret = null;
		if(bitmap!=null){
			ret = BitmapUtils.getPortraitBitmap(bitmap);	
			imageView.setImageBitmap(ret);
		}

		return ret;
	}

}
