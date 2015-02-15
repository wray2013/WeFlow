package com.cmmobi.looklook.common.view;

import android.content.Context;
import android.util.AttributeSet;

import com.cmmobi.looklook.common.view.pulltorefresh.PullToRefreshListView;

public class ConViewPagerListView extends PullToRefreshListView {

	public ConViewPagerListView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public ConViewPagerListView(Context context, Mode mode, AnimationStyle style) {
		super(context, mode, style);
	}

	public ConViewPagerListView(Context context, Mode mode) {
		super(context, mode);
	}

	public ConViewPagerListView(Context context) {
		super(context);
	}

}
