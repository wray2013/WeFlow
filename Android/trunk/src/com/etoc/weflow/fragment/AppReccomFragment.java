package com.etoc.weflow.fragment;

import com.etoc.weflow.R;
import com.etoc.weflow.view.autoscrollviewpager.AutoScrollViewPager;
import com.viewpagerindicator.PageIndicator;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class AppReccomFragment extends Fragment {
	private View mView;
	private AutoScrollViewPager viewPager = null;
	private PageIndicator mIndicator;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		if(mView != null){
		    ViewGroup parent = (ViewGroup) mView.getParent();  
		    if (parent != null) {  
		        parent.removeView(mView);  
		    }   
		    return mView;
		}
		super.onCreateView(inflater, container, savedInstanceState);
		
		View v = inflater.inflate(R.layout.fragment_app_reccom, null);
		mView = v;
		initView(v);
		return v;
	}
	
	private void initView(View view) {
		viewPager = (AutoScrollViewPager) view.findViewById(R.id.vp_pager_service);
		
        viewPager.setInterval(3000);
        viewPager.startAutoScroll();
        viewPager.setCycle(true);
        viewPager.setSlideBorderMode(AutoScrollViewPager.SLIDE_BORDER_MODE_CYCLE);
        mIndicator = (PageIndicator) view.findViewById(R.id.indicator_service);
        
        
	}
}
