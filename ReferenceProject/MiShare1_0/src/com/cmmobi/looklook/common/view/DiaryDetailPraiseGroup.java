package com.cmmobi.looklook.common.view;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

public class DiaryDetailPraiseGroup extends ViewGroup
{
	private final String TAG = "DiaryDetailPraiseGroup";
	
	private final int INTERVAL = 6;// 间距
	
//	private Context mContext = null;
	private View mView[] = null;
	
//	private OnClickListener mListener = null;
	
	public DiaryDetailPraiseGroup(Context context, AttributeSet attrs)
	{
		super(context, attrs);
	}
	
	public void initCoverView(Context context, OnClickListener listener)
	{
//		mContext = context;
//		mListener = listener;
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
	{
//		Log.d(TAG, "widthMeasureSpec = " + widthMeasureSpec + " heightMeasureSpec" + heightMeasureSpec);
//		
//		int n = getChildCount();
//		
//        DisplayMetrics dm = mContext.getResources().getDisplayMetrics();
//        int screenWidth = dm.widthPixels;
//		
//		if(n == 0)
//		{
//			return;
//		}
//		
//		for (int index = 0; index < n; index++)
//		{
//			final View child = getChildAt(index);
//			// measure
//			child.measure(MeasureSpec.UNSPECIFIED, MeasureSpec.UNSPECIFIED);
//		}
//		
//		int height = 0;
//		if(n < 2)
//		{
//			int imageWidth = (screenWidth - INTERVAL) / 2 - INTERVAL;
//			height = INTERVAL + imageWidth + INTERVAL;
//		}
//		else if(n < 3)
//		{
//			int imageWidth = (screenWidth - INTERVAL) / 3 - INTERVAL;
//			height = INTERVAL + imageWidth + INTERVAL;
//		}
//		else if(n < 6)
//		{
//			int imageWidth = (screenWidth - INTERVAL) / 3 - INTERVAL;
//			height = INTERVAL + (imageWidth + INTERVAL) * 2;
//		}
//		else
//		{
//			int imageWidth = (screenWidth - INTERVAL) / 3 - INTERVAL;
//			height = INTERVAL + (imageWidth + INTERVAL) * 3;
//		}
//		
//		// 定值才有效
//		setMeasuredDimension(screenWidth, height);
		
		int realWidth = MeasureSpec.getSize(widthMeasureSpec);
		
		int realHeight = MeasureSpec.getSize(heightMeasureSpec);
//		int realModel = MeasureSpec.getMode(heightMeasureSpec);
		
		if(realWidth != 0)
		{
			int h = (realWidth + INTERVAL) / 6 + INTERVAL;
			if(realHeight < h)
			{
				realHeight = h;
			}
//			heightMeasureSpec = MeasureSpec.makeMeasureSpec(realHeight, realModel);
		}
		
		// 定值才有效
		setMeasuredDimension(realWidth, realHeight);
//		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		
	}
	
	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b)
	{
		Log.d(TAG, "changed = "+changed+" left = "+l+" top = "+t+" right = "+r+" botom = "+b);
		int n = getChildCount();
		
		mView = new View[n];
		int totalWidth = getWidth();
		int totalHeight = getHeight();
		
		int imageWidth = (totalWidth + INTERVAL) / 6 - INTERVAL;
		
		for(int i = 0; i < (n <= 6 ? n : 6); i++)
		{
			mView[i] = getChildAt(i);
//			mView[i].measure(0, 0);
			mView[i].layout((imageWidth + INTERVAL) * i,
					(totalHeight - imageWidth) / 2,
					(imageWidth + INTERVAL) * i + imageWidth,
					(totalHeight - imageWidth) / 2 + imageWidth); 
//			mView[i].setOnClickListener(mListener);
//			mView[i].setTag(n);
		}
	}

}
