package com.nostra13.universalimageloader.api;

import android.graphics.Bitmap;
import android.view.View;
import android.widget.ImageView;

import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

public class FadeInAnimateDisplayListener extends SimpleImageLoadingListener {
	int delay = 2000;
	public FadeInAnimateDisplayListener(int delay){
		this.delay = delay;
	}
	
	@Override
	public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
		if (loadedImage != null) {
			ImageView imageView = (ImageView) view;
			FadeInBitmapDisplayer.animate(imageView, delay);
		}
	}

}
