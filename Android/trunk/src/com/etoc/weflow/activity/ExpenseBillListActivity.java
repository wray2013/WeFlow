package com.etoc.weflow.activity;

import android.os.Bundle;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;

import com.astuetz.PagerSlidingTabStrip;
import com.etoc.weflow.R;
import com.etoc.weflow.event.ExpenseFlowBillFragmentEvent;
import com.etoc.weflow.fragment.ExpenseFlowBillFragment;
import com.etoc.weflow.utils.ConStant;
import com.etoc.weflow.utils.DisplayUtil;
import com.etoc.weflow.view.MyViewPager;

import de.greenrobot.event.EventBus;

public class ExpenseBillListActivity extends TitleRootActivity {
//	private PagerSlidingTabStrip titleTab;
	private MyViewPager viewPage;
	private ExpenseBillPagerAdapter adapter;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		initViews();
	}
	
	private void initViews() {
		// TODO Auto-generated method stub
//		setTitleText("花币记录");
		hideRightButton();
		
//		titleTab = (PagerSlidingTabStrip) findViewById(R.id.mybill_tabs);
		viewPage = (MyViewPager) findViewById(R.id.mybill_pager);
		viewPage.setOffscreenPageLimit(0);
		viewPage.setScrollEnable(false);
//		titleTab.setTextColorResource(R.color.pagertab_color_green);
//		titleTab.setTabPaddingLeftRight(DisplayUtil.getSize(this, 40));
		
		adapter = new ExpenseBillPagerAdapter(getSupportFragmentManager());
		viewPage.setAdapter(adapter);
		
//		titleTab.setViewPager(viewPage);
		
		int index = getIntent().getIntExtra(ConStant.INTENT_EXPENSE_FLOW, 0);
		index = index <= 0 ? 0 : (index >= adapter.getCount() ? adapter.getCount() - 1 : index);
		viewPage.setCurrentItem(index);
		final int indexTemp = index;
		handler.postDelayed(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				ExpenseFlowBillFragmentEvent event  = new ExpenseFlowBillFragmentEvent();
				event.setIndex(indexTemp);
				EventBus.getDefault().post(event);
			}
		}, 100);
		
		switch (index) {
		case 0:
			setTitleText("充话费记录");
			break;
		case 1:
			setTitleText("定流量包记录");
			break;
		case 2:
			setTitleText("换游戏币记录");
			break;
		case 3:
			setTitleText("换礼券记录");
			break;
		}
		
		/*titleTab.setOnPageChangeListener(new OnPageChangeListener() {
			
			@Override
			public void onPageSelected(int arg0) {
				// TODO Auto-generated method stub
				ExpenseFlowBillFragmentEvent event  = new ExpenseFlowBillFragmentEvent();
				event.setIndex(arg0);
				EventBus.getDefault().post(event);
			}
			
			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onPageScrollStateChanged(int arg0) {
				// TODO Auto-generated method stub
				
			}
		});*/
		
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
		return R.layout.activity_expensebilllist;
	}

	public class ExpenseBillPagerAdapter extends FragmentPagerAdapter {

		private final String[] TITLES = { "充话费", "流量包", "游戏币", "礼品券"};

		public ExpenseBillPagerAdapter(FragmentManager fm) {
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
			return ExpenseFlowBillFragment.newInstance(position);
		}

	}
}
