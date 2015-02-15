package com.cmmobi.looklook.common.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

public class ImageAdapter extends BaseAdapter{

	private Context mContext;
	
	private Bitmap[] bmps;
	public ImageAdapter(Context c) {  
        mContext = c;  
    }  
	
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return 16;
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}
	
	public void setBitmaps(Bitmap [] bitmaps) {
		bmps = bitmaps;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		ImageView iv = new ImageView (mContext);
        //从imgList取得图片ID
		if (bmps != null && position < bmps.length && bmps[position] != null) {
			iv.setImageBitmap(bmps[position]);
		}
        iv.setScaleType(ImageView.ScaleType.FIT_XY);
		return iv;
	}

}
