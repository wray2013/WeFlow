package com.etoc.weflow.activity;

import com.astuetz.PagerSlidingTabStrip;
import com.etoc.weflow.R;
import com.etoc.weflow.fragment.SuperBillFragment;

import android.os.Bundle;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;

public class MyBillListActivity extends TitleRootActivity {

	private PagerSlidingTabStrip titleTab;
	private ViewPager viewPage;
	private MyBillPagerAdapter adapter;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		initViews();
	}
	
	private void initViews() {
		// TODO Auto-generated method stub
		setTitleText("我的账单");
		hideRightButton();
		
		titleTab = (PagerSlidingTabStrip) findViewById(R.id.mybill_tabs);
		viewPage = (ViewPager) findViewById(R.id.mybill_pager);
		
		adapter = new MyBillPagerAdapter(getSupportFragmentManager());
		viewPage.setAdapter(adapter);
		
		titleTab.setViewPager(viewPage);
	}

	@Override
	protected int graviteType() {
		// TODO Auto-generated method stub
		return GRAVITE_LEFT;
	}
	
	@Override
	public boolean handleMessage(Message msg) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public int subContentViewId() {
		// TODO Auto-generated method stub
		return R.layout.activity_mybilllist;
	}

	public class MyBillPagerAdapter extends FragmentPagerAdapter {

		private final String[] TITLES = { "全部", "收入", "支出"/*, "交友"*/};

		public MyBillPagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public CharSequence getPageTitle(int position) {
			return TITLES[position];
		}

		@Override
		public int getCount() {
			return TITLES.length;
		}

		@Override
		public Fragment getItem(int position) {
			return SuperBillFragment.newInstance(position);
		}

	}
	
}
