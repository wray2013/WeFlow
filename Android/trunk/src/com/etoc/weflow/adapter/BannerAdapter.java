package com.etoc.weflow.adapter;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.util.Log;

import com.etoc.weflow.fragment.BannerFragment;
import com.etoc.weflow.net.GsonResponseObject.AdvInfo;
import com.imbryk.viewPager.LoopViewPager;

public class BannerAdapter extends FragmentPagerAdapter {

	private int mDrawable;
	public BannerAdapter(FragmentManager fm,int drawable,List<AdvInfo> list) {
		super(fm);
		// TODO Auto-generated constructor stub
		mDrawable = drawable;
		playBillImageSites = list;
	}

	List<AdvInfo> playBillImageSites = new ArrayList<AdvInfo>();
	Activity act;
	
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return playBillImageSites.size();
	}
	
	@Override
	public int getItemPosition(Object object) {
		// TODO Auto-generated method stub
		return PagerAdapter.POSITION_NONE;
	}
	
	@Override
	public Fragment getItem(int position) {
		// TODO Auto-generated method stub
		Log.d("RailServiceFragment","getItem in");
		position = LoopViewPager.toRealPosition(position, getCount());
//        return TestFragment.newInstance(CONTENT[position % CONTENT.length]);
		return BannerFragment.newInstance(playBillImageSites.get(position),mDrawable);
	}
}
