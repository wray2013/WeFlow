package com.etoc.weflowdemo.fragment;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
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

import com.etoc.weflowdemo.R;
import com.etoc.weflowdemo.activity.AdDetailActivity;
import com.etoc.weflowdemo.util.DisplayUtil;
import com.etoc.weflowdemo.util.ViewUtils;
import com.nostra13.universalimageloader.api.MyImageLoader;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;


public class BannerFragment extends Fragment implements OnClickListener, Callback {
	private final String TAG = "PlayBillFragment";
	private static final String KEY_URL = "PlayBillFragment:imageUrl";
	private static final String KEY_NAME = "PlayBillFragment:name";
	private static final String KEY_ID = "PlayBillFragment:id";
	private static final String KEY_TYPE = "PlayBillFragment:type";
	private int imageId = 0;
	MyImageLoader imageLoader = null;
	DisplayImageOptions imageLoaderOptions = null;
	ImageView imageView;
	TextView tvRailService;
	TextView tvAmusement;
	private int mDrawable = 0;
	private Handler handler = null;
	
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
//				.displayer(new SimpleBitmapDisplayer())
				// .displayer(new CircularBitmapDisplayer()) 圆形图片
				
				.displayer(new RoundedBitmapDisplayer(12))// 圆角图片
				.build();
		super.onCreate(savedInstanceState);
		
//		EventBus.getDefault().register(this);
		
		if (savedInstanceState != null) {
			if (savedInstanceState.containsKey(KEY_URL)) {
				imageId = savedInstanceState.getInt(KEY_URL);
			}
        }
		
		handler = new Handler(this);
	}
	
	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
//		EventBus.getDefault().unregister(this);
		Log.d("=AAA=","RailServiceBannerFragment onDestroy in");
		super.onDestroy();
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater,
			ViewGroup container,  Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View view = inflater.inflate(R.layout.item_view_pager, null);
		imageView = (ImageView)view.findViewById(R.id.iv_playbill);
		
		imageView.setImageResource(imageId);
		view.setOnClickListener(this);
		return view;
	}
	
	public static BannerFragment newInstance(Integer elem,int drawable) {
		BannerFragment fragment = new BannerFragment();
		fragment.imageId = elem;
		fragment.mDrawable = drawable;
        return fragment;
    }
	
	@Override
	public void onSaveInstanceState(Bundle outState) {
		// TODO Auto-generated method stub
		super.onSaveInstanceState(outState);
		outState.putInt(KEY_URL, imageId);
	}
	
	@Override
	public void onHiddenChanged(boolean hidden) {
		// TODO Auto-generated method stub
		Log.d("=AAA=","RailServiceBanner hidden = " + hidden);
		super.onHiddenChanged(hidden);
	}
	
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		
		switch(v.getId()) {
		case R.id.rl_item_view_pager:	
			Intent i = new Intent(getActivity(), AdDetailActivity.class);
			startActivity(i);
			break;
		}
	}

	@Override
	public boolean handleMessage(Message msg) {
		// TODO Auto-generated method stub
		switch(msg.what){
		}
		return false;
	}
}
