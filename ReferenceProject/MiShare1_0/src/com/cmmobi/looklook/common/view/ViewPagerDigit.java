package com.cmmobi.looklook.common.view;


import java.util.ArrayList;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cmmobi.looklook.R;

public class ViewPagerDigit extends LinearLayout implements
		ViewPager.OnPageChangeListener {

	private int currentPage = 0;
	private Context context;
	private Drawable normalDrawable;
	private Drawable currentDrawable;
	
    private int scrollState;
	private int size;
	private ViewPager viewPager;
	private ArrayList<TextView> digitViewList;
	public ViewPagerDigit(Context context) {
		super(context);
		this.context = context;
	}

	public ViewPagerDigit(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.context = context;
		loadDefaultDrawable();
		}

	private void loadDefaultDrawable() {
		Resources resources = getResources();

		normalDrawable = resources.getDrawable(R.drawable.del_shuzi_1);
		currentDrawable = resources.getDrawable(R.drawable.del_shuzi_2);

	}

	public void setViewPager(ViewPager view, int size) {
        if (view.getAdapter() == null) {
            throw new IllegalStateException("not have adapter instance.");
        }
        this.size = size; 
        this.viewPager = view;
        viewPager.setOnPageChangeListener(this);
        
        init();
        invalidate();
	}

	private void init() {
		digitViewList = new ArrayList<TextView>();
		removeAllViews();
		for (int i = 0; i < size; i++) {
			TextView textView = new TextView(context);
			LayoutParams params = new LayoutParams(20, 20);
			params.leftMargin = 2;
			params.rightMargin = 2;
			textView.setLayoutParams(params);
			textView.setGravity(Gravity.CENTER);
			textView.setText((i + 1) + "");
			textView.setTextSize(8);
			addView(textView);
			if(i==0) {
				textView.setBackgroundDrawable(currentDrawable);
				digitViewList.add(textView);
			} else {
				textView.setBackgroundDrawable(normalDrawable);
				digitViewList.add(textView);
			}
		}
	}
	public void onPageScrolled(int position, float positionOffset,
			int positionOffsetPixels) {
        invalidate();
	}

	public void onPageSelected(int position) {
        if (scrollState == ViewPager.SCROLL_STATE_IDLE) {
            invalidate();
        }

        setPage(position % size);
	}

	private void setPage(int curPage) {
		if(curPage>=size || curPage<0 || curPage==this.currentPage) {
			return;
		}
		digitViewList.get(curPage).setBackgroundDrawable(currentDrawable);
		digitViewList.get(currentPage < digitViewList.size() ? currentPage : 0).setBackgroundDrawable(normalDrawable);
		currentPage = curPage;
	}
	public void onPageScrollStateChanged(int state) {
        scrollState = state;
	}

}
