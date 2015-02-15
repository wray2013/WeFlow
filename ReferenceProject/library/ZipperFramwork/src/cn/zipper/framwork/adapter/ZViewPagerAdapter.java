package cn.zipper.framwork.adapter;

import java.util.ArrayList;
import java.util.List;

import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;

public final class ZViewPagerAdapter extends PagerAdapter {
	
	private List<View> list;
	
	public ZViewPagerAdapter() {
		list = new ArrayList<View>();
	}
	
	public void setList(List<View> list) {
		this.list = list;
		this.notifyDataSetChanged();
	}
	
	public List<View> getList() {
		return list;
	}

	@Override
	public void destroyItem(ViewGroup view, int location, Object arg2) {
		view.removeView(list.get(location));
	}

	@Override
	public void finishUpdate(View arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public int getCount() {
		return list.size();
	}

	@Override
	public Object instantiateItem(ViewGroup view, int location) {
		View temp = list.get(location);
		view.addView(temp);
		return temp;
	}

	@Override
	public boolean isViewFromObject(View view, Object object) {
		return view == object;
	}

	@Override
	public void restoreState(Parcelable arg0, ClassLoader arg1) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public Parcelable saveState() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void startUpdate(View arg0) {
		// TODO Auto-generated method stub
		
	}

}
