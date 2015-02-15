package com.cmmobi.looklook.common.view;

import android.content.Context;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

public class DiaryDetailCoverGroup extends ViewGroup
{
	private final String TAG = "DiaryDetailCoverGroup";
	
	public static final int INTERVAL = 6;// 间距
	
//	private Context mContext = null;
	private View mView[] = null;
	
//	private OnClickListener mListener = null;
	
	public DiaryDetailCoverGroup(Context context, AttributeSet attrs)
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
		 DisplayMetrics dm = getContext().getResources().getDisplayMetrics();
		Log.d(TAG, "widthMeasureSpec = " + widthMeasureSpec + " heightMeasureSpec" + heightMeasureSpec);
		int n = getChildCount();
		
//        DisplayMetrics dm = mContext.getResources().getDisplayMetrics();
//        int screenWidth = dm.widthPixels;
		int screenWidth = MeasureSpec.getSize(widthMeasureSpec);
		int height = 0;

		if(screenWidth > 0)
		{
			if(n == 0)
			{
				super.onMeasure(widthMeasureSpec, heightMeasureSpec);
				return;
			}
			
			int imageWidth = 0;
			if(n == 1)
			{
				//Log.d(TAG, "dm w = " + dm.widthPixels + ", h = " + dm.heightPixels + ", imagewidth = " + (int)(dm.widthPixels * 508/1080));
				imageWidth = (int)(dm.widthPixels * 508/1080);
				height = imageWidth;
			}else if(n < 4)
			{
				imageWidth = (screenWidth + INTERVAL) / 3 - INTERVAL;
				height = imageWidth;
			}
			else if(n < 7)
			{
				imageWidth = (screenWidth + INTERVAL) / 3 - INTERVAL;
				height = (imageWidth + INTERVAL) * 2 - INTERVAL;
			}
			else
			{
				imageWidth = (screenWidth + INTERVAL) / 3 - INTERVAL;
				height = (imageWidth + INTERVAL) * 3 - INTERVAL;
			}
			
			for (int index = 0; index < n; index++)
			{
				final View child =  getChildAt(index);
				// measure
				int childWidth = MeasureSpec.makeMeasureSpec(imageWidth, MeasureSpec.EXACTLY);
				int childHeight = MeasureSpec.makeMeasureSpec(imageWidth, MeasureSpec.EXACTLY);
				
				child.measure(childWidth, childHeight);
				
				ViewGroup.LayoutParams params = child.getLayoutParams();
				params.width = imageWidth;
				params.height = imageWidth;
				child.setLayoutParams(params);
				
			}
		}
		
		// 定值才有效
		setMeasuredDimension(screenWidth, height);
		
	}
	
	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b)
	{
		Log.d(TAG, "changed = "+changed+" left = "+l+" top = "+t+" right = "+r+" botom = "+b);
//		if (changed)
//		{
			int n = getChildCount();
			
			if(n == 0)
			{
				return;
			}
			DisplayMetrics dm = getContext().getResources().getDisplayMetrics();

			mView = new View[n];
			
			if(n == 1)
			{
				int imageWidth =  (int)(dm.widthPixels * 508/1080);//(getWidth() + INTERVAL) / 2 - INTERVAL;
				mView[0] = getChildAt(0);
//				mView[0].measure(imageWidth, imageWidth);
				mView[0].layout(0, 0, imageWidth, imageWidth); 
//				mView[0].setOnClickListener(mListener);
//				mView[0].setTag(n);
			}
			else if(n < 4)
			{
				int imageWidth = (getWidth() + INTERVAL) / 3 - INTERVAL;
				for(int i = 0; i < n; i++)
				{
					mView[i] = getChildAt(i);
//					mView[i].measure(imageWidth, imageWidth);
					mView[i].layout((imageWidth + INTERVAL) * i,
							0,
							(imageWidth + INTERVAL) * i + imageWidth,
							imageWidth); 
//					mView[i].setOnClickListener(mListener);
//					mView[i].setTag(n);
				}
			}
			else
			{
				int imageWidth = (int)(getWidth() + INTERVAL) / 3 - INTERVAL;
				for(int i = 0; i < n; i++)
				{
					mView[i] = getChildAt(i);
//					mView[i].measure(imageWidth, imageWidth);
					mView[i].layout((imageWidth + INTERVAL) * (i % 3),
							(imageWidth + INTERVAL) * (i / 3),
							(imageWidth + INTERVAL) * (i % 3) + imageWidth,
							(imageWidth + INTERVAL) * (i / 3) + imageWidth); 
//					mView[i].setOnClickListener(mListener);
//					mView[i].setTag(n);
				}
			}
//		}

	}

}
