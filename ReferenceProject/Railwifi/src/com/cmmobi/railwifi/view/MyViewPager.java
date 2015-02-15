package com.cmmobi.railwifi.view;

import com.cmmobi.railwifi.view.autoscrollviewpager.AutoScrollViewPager;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

public class MyViewPager extends AutoScrollViewPager {

	public MyViewPager(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	public MyViewPager(Context context, AttributeSet attrs) {       
		super(context, attrs);        // TODO Auto-generated constructor stub        init(context);    
	
	}
	
	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
		// TODO Auto-generated method stub
		getParent().requestDisallowInterceptTouchEvent(true);//重点
        return super.dispatchTouchEvent(ev);  
	}
}
