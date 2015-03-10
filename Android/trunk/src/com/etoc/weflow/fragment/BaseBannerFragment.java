package com.etoc.weflow.fragment;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.etoc.weflow.R;
import com.etoc.weflow.net.GsonResponseObject.AdvInfo;
import com.nostra13.universalimageloader.api.MyImageLoader;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;


public abstract class BaseBannerFragment extends Fragment implements OnClickListener {
	
	private final String TAG = "BaseBannerFragment";
	private static final String KEY_URL = "PlayBillFragment:imageUrl";
	MyImageLoader imageLoader = null;
	DisplayImageOptions imageLoaderOptions = null;
	ImageView imageView;
	private int mDrawable = 0;
	private String picUrl= null;
	
	private View mView;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		imageLoader = MyImageLoader.getInstance();

		imageLoaderOptions = new DisplayImageOptions.Builder()
				.cacheInMemory(true)
				.cacheOnDisc(true)
				.imageScaleType(ImageScaleType.IN_SAMPLE_POWER_OF_2)
				.bitmapConfig(Bitmap.Config.RGB_565)
				.showImageForEmptyUri(mDrawable)
				.showImageOnFail(mDrawable)
				.showImageOnLoading(mDrawable)
				.build();
		super.onCreate(savedInstanceState);
		
		
		if (savedInstanceState != null) {
			if (savedInstanceState.containsKey(KEY_URL)) {
				picUrl = savedInstanceState.getString(KEY_URL);
			}
        }
		
	}
	
	
	public BaseBannerFragment(String url,int drawable) {
		// TODO Auto-generated constructor stub
		picUrl = url;
		mDrawable = drawable;
		Log.d("=AAA=","picUrl = " + picUrl);
	}
	
	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		Log.d("=AAA=","RailServiceBannerFragment onDestroy in");
		super.onDestroy();
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater,
			ViewGroup container,  Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		/*if(mView != null){
		    ViewGroup parent = (ViewGroup) mView.getParent();  
		    if (parent != null) {  
		        parent.removeView(mView);  
		    }   
		    return mView;
		}*/
		
		View view = inflater.inflate(R.layout.item_view_pager, null);
		
		imageView = (ImageView)view.findViewById(R.id.iv_playbill);
		
		imageLoader.displayImage(picUrl, imageView, imageLoaderOptions);
		view.setOnClickListener(this);
		mView = view;
		return view;
	}
	
	@Override
	public void onSaveInstanceState(Bundle outState) {
		// TODO Auto-generated method stub
		super.onSaveInstanceState(outState);
		outState.putString(KEY_URL, picUrl);
	}

}
