package com.cmmobi.looklook.common.view;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ScrollView;

import com.cmmobi.looklook.common.listener.DiaryTouchInterface;
import com.cmmobi.looklook.common.view.pulltorefresh.PullToRefreshListView;

/**
 * Copyright (C) ......
 * All rights reserved.
 * @package com.cmmobi.looklook.common.view
 * @filename SquareLayout.java
 * @summary 内部scrollview
 * @author Lanhai
 * @date 2013-12-20
 * @version 1.0
 */
public class InnerScrollView extends ScrollView
{

	private final String TAG = "Touch";
//	public ScrollView parentScrollView;
	public PullToRefreshListView parentScrollView;
	private DiaryTouchInterface listener;

	public InnerScrollView(Context context, AttributeSet attrs)
	{
		super(context, attrs);
		
	}
	
	public void setOnClickListener(DiaryTouchInterface listener)
	{
		this.listener = listener;
	}

//	private int lastScrollDelta = 0;
//
//	public void resume()
//	{
//		overScrollBy(0, -lastScrollDelta, 0, getScrollY(), 0, getScrollRange(), 0, 0, true);
//		lastScrollDelta = 0;
//	}
//
//	int mTop = 10;
//
//	/**
//	 * 将targetView滚到最顶端
//	 */
//	public void scrollTo(View targetView)
//	{
//		int oldScrollY = getScrollY();
//		int top = targetView.getTop() - mTop;
//		int delatY = top - oldScrollY;
//		lastScrollDelta = delatY;
//		overScrollBy(0, delatY, 0, getScrollY(), 0, getScrollRange(), 0, 0, true);
//	}
//
//	private int getScrollRange()
//	{
//		int scrollRange = 0;
//		if (getChildCount() > 0)
//		{
//			View child = getChildAt(0);
//			scrollRange = Math.max(0, child.getHeight() - (getHeight()));
//		}
//		return scrollRange;
//	}

	int currentX;
	int currentY;
	
	long currentTime;

	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev)
	{
		if (parentScrollView == null)
		{
			return super.onInterceptTouchEvent(ev);
		}
		else
		{
			if (ev.getAction() == MotionEvent.ACTION_DOWN)
			{
				// 将父scrollview的滚动事件拦截
				currentX = (int) ev.getX();
				currentY = (int) ev.getY();
				setParentScrollAble(false);
				return super.onInterceptTouchEvent(ev);
			}
			else if (ev.getAction() == MotionEvent.ACTION_UP)
			{
				// 把滚动事件恢复给父Scrollview
				setParentScrollAble(true);
			}
			else if (ev.getAction() == MotionEvent.ACTION_MOVE)
			{
			}
		}
		return super.onInterceptTouchEvent(ev);
	}

	@Override
	public boolean onTouchEvent(MotionEvent ev)
	{
		View child = getChildAt(0);
		if (parentScrollView != null)
		{
			if (ev.getAction() == MotionEvent.ACTION_DOWN)
			{
				currentTime = System.currentTimeMillis();
			}
			else if (ev.getAction() == MotionEvent.ACTION_UP)
			{
				// 点击事件
				long time = System.currentTimeMillis();
				if(time - currentTime < 100)
				{
					if(listener != null)
					{
						listener.onClick();
					}
				}
				currentTime = 0l;
			}
			else if (ev.getAction() == MotionEvent.ACTION_MOVE)
			{
				int height = child.getMeasuredHeight();
				height = height - getMeasuredHeight();
				// System.out.println("height=" + height);
//				int scrollY = getScrollY();
				// System.out.println("scrollY" + scrollY);
				
				int y = (int) ev.getY();
//				// 手指向下滑动
//				if (currentY < y)
//				{
//					if (scrollY <= 0)
//					{
//						// 如果向下滑动到头，就把滚动交给父Scrollview
//						setParentScrollAble(true);
//						return false;
//					}
//					else
//					{
//						setParentScrollAble(false);
//					}
//				}
//				else if (currentY > y)
//				{
//					if (scrollY >= height)
//					{
//						// 如果向上滑动到头，就把滚动交给父Scrollview
//						setParentScrollAble(true);
//						return false;
//					}
//					else
//					{
//						setParentScrollAble(false);
//					}
//				}
				
				int x = (int) ev.getX();
				Log.v(TAG, "Math.abs(currentX - x) = " + Math.abs(currentX - x));
				Log.v(TAG, "Math.abs(currentY - y) = " + Math.abs(currentY - y));
				if (Math.abs(currentX - x) > 10 && Math.abs(currentY - y) < 10)
				{
					setParentScrollAble(true);
				}
				else
				{
					setParentScrollAble(false);
				}
				currentX = x;
				currentY = y;
			}
		}
		return super.onTouchEvent(ev);
	}

	/**
	 * 是否把滚动事件交给父scrollview
	 * 
	 * @param flag
	 */
	private void setParentScrollAble(boolean flag)
	{
		parentScrollView.requestDisallowInterceptTouchEvent(!flag);
	}

}
