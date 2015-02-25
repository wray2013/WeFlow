package com.etoc.weflowdemo.activity;

import android.os.Bundle;
import android.os.Message;

import com.etoc.weflowdemo.R;
import com.etoc.weflowdemo.view.autoscrollviewpager.AutoScrollViewPager;
import com.viewpagerindicator.PageIndicator;

public class AdvertActivity extends TitleRootActivity {

	private AutoScrollViewPager viewPager = null;
	private PageIndicator mIndicator;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		initViews();
	}
	
	private void initViews() {
		setTitleText("看视频");
		setRightButtonText("记录");
		
		viewPager = (AutoScrollViewPager) findViewById(R.id.vp_pager_service);
		
        viewPager.setInterval(3000);
        viewPager.startAutoScroll();
        viewPager.setCycle(true);
        viewPager.setSlideBorderMode(AutoScrollViewPager.SLIDE_BORDER_MODE_CYCLE);
        mIndicator = (PageIndicator) findViewById(R.id.indicator_service);
	}
	
	@Override
	public boolean handleMessage(Message arg0) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public int subContentViewId() {
		// TODO Auto-generated method stub
		return R.layout.activity_advert;
	}

}
