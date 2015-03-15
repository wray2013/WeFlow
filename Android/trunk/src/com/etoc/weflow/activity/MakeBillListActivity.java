package com.etoc.weflow.activity;

import android.os.Bundle;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;

import com.astuetz.PagerSlidingTabStrip;
import com.etoc.weflow.R;
import com.etoc.weflow.fragment.MakeFlowBillFragment;
import com.etoc.weflow.utils.ConStant;
import com.etoc.weflow.utils.DisplayUtil;

public class MakeBillListActivity extends TitleRootActivity {

	private PagerSlidingTabStrip titleTab;
	private ViewPager viewPage;
	private MakeBillPagerAdapter adapter;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		initViews();
	}
	
	private void initViews() {
		// TODO Auto-generated method stub
		setTitleText("赚币记录");
		hideRightButton();
		
		titleTab = (PagerSlidingTabStrip) findViewById(R.id.mybill_tabs);
		viewPage = (ViewPager) findViewById(R.id.mybill_pager);
		
		titleTab.setTabPaddingLeftRight(DisplayUtil.getSize(this, 50));
		
		adapter = new MakeBillPagerAdapter(getSupportFragmentManager());
		viewPage.setAdapter(adapter);
		
		titleTab.setViewPager(viewPage);
		
		int index = getIntent().getIntExtra(ConStant.INTENT_MAKE_FLOW, 0);
		index = index <= 0 ? 0 : (index >= adapter.getCount() ? adapter.getCount() - 1 : index);
		viewPage.setCurrentItem(index);
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
		return R.layout.activity_makebilllist;
	}

	public class MakeBillPagerAdapter extends FragmentPagerAdapter {

		private final String[] TITLES = { "看广告", "下软件", "玩游戏"/*, "交友"*/};

		public MakeBillPagerAdapter(FragmentManager fm) {
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
			return MakeFlowBillFragment.newInstance(position);
		}

	}
}
