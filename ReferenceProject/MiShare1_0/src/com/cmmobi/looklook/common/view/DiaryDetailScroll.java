package com.cmmobi.looklook.common.view;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.ScrollView;

import com.cmmobi.looklook.common.listener.DiaryPagerTouchInterface;

/**
 * Copyright (C) ......
 * All rights reserved.
 * @package com.cmmobi.looklook.common.view
 * @filename DiaryDetailPager.java
 * @summary 详情页自定义ScrollView
 * @author Administrator
 * @date 2013-9-9
 * @version 1.0
 */
public class DiaryDetailScroll extends ScrollView {

	private final String TAG = "DiaryDetailScroll";

	private DiaryPagerTouchInterface multListener;

	public DiaryDetailScroll(Context context, AttributeSet attrs)
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
