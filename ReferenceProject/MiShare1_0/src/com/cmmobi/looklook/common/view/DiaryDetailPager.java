package com.cmmobi.looklook.common.view;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;

import com.cmmobi.looklook.common.listener.DiaryPagerTouchInterface;

/**
 * Copyright (C) ......
 * All rights reserved.
 * @package com.cmmobi.looklook.common.view
 * @filename DiaryDetailPager.java
 * @summary 详情页自定义ViewPager
 * @author Administrator
 * @date 2013-9-9
 * @version 1.0
 */
public class DiaryDetailPager extends ViewPager {

	private static final String TAG = "Touch";
	
	private DiaryPagerTouchInterface multListener;
	
	public DiaryDetailPager(Context context)
	{
		super(context);
	}
	
	public DiaryDetailPager(Context context, AttributeSet attrs)
	{
		super(context, attrs);
	}
	
	@Override
	public boolean onInterceptTouchEvent(MotionEvent arg0)
	{
		if (multListener == null || !multListener.isIntercept())
		{
			try
			{
				Log.v(TAG, "scroll");
				return super.onInterceptTouchEvent(arg0);
			}
//			catch (IllegalArgumentException ex)
			catch (Exception ex)
			{
				ex.printStackTrace();
				return false;
			}
		}
		else
		{
			Log.v(TAG, "pass");
			return false;
		}

	}

	public void setInterceptListener(DiaryPagerTouchInterface multListener)
	{
		this.multListener = multListener;
	}
}
