package com.etoc.weflow.view;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

public class MyViewPager extends ViewPager {

	private boolean isScrollEnable = true;

	public MyViewPager(Context context) {
		super(context);
	}

	public MyViewPager(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public void setScrollEnable(boolean isScrollEnable) {
		this.isScrollEnable = isScrollEnable;
	}

//	@Override
//	public void scrollTo(int x, int y) {
//		if (this.isScrollEnable) {
//			super.scrollTo(x, y);
//		}
//	}
	@Override
    public boolean onTouchEvent(MotionEvent event) {
        if (this.isScrollEnable) {
            return super.onTouchEvent(event);
        }
        return false;
    }
    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        if (this.isScrollEnable) {
            return super.onInterceptTouchEvent(event);
        }
        return false;
    }
    
//	@Override
//	public void setCurrentItem(int item) {
//		super.setCurrentItem(item);
//		switch(item) {
//		case MerchantBaseActivity.INDEX_MERCHANT_DETAIL:
//			setScrollEnable(false);
//			break;
//		case MerchantBaseActivity.INDEX_MERCHANT_LIST:
//			setScrollEnable(false);
//			break;
//		}
//	}
	
}
