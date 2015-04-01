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
import com.etoc.weflow.event.MakeFlowBillFragmentEvent;
import com.etoc.weflow.fragment.MakeFlowBillFragment;
import com.etoc.weflow.utils.ConStant;
import com.etoc.weflow.utils.DisplayUtil;
import com.etoc.weflow.view.MyViewPager;

import de.greenrobot.event.EventBus;

public class MakeBillListActivity extends TitleRootActivity {

//	private PagerSlidingTabStrip titleTab;
	private MyViewPager viewPage;
	private MakeBillPagerAdapter adapter;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		initViews();
	}
	
	private void initViews() {
		// TODO Auto-generated method stub
//		setTitleText("赚币记录");
		hideRightButton();
		
//		titleTab = (PagerSlidingTabStrip) findViewById(R.id.mybill_tabs);
		viewPage = (MyViewPager) findViewById(R.id.mybill_pager);
		viewPage.setOffscreenPageLimit(0);
		viewPage.setScrollEnable(false);
//		titleTab.setTabPaddingLeftRight(DisplayUtil.getSize(this, 50));
		
		adapter = new MakeBillPagerAdapter(getSupportFragmentManager());
		viewPage.setAdapter(adapter);
		
//		titleTab.setViewPager(viewPage);
		
		int index = getIntent().getIntExtra(ConStant.INTENT_MAKE_FLOW, 0);
		index = index <= 0 ? 0 : (index >= adapter.getCount() ? adapter.getCount() - 1 : index);
		viewPage.setCurrentItem(index);
		final int indexTemp = index;
		handler.postDelayed(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				MakeFlowBillFragmentEvent event  = new MakeFlowBillFragmentEvent();
				event.setIndex(indexTemp);
				EventBus.getDefault().post(event);
			}
		}, 100);
		
		switch (index) {
		case 0:
			setTitleText("看视频记录");
			break;
		case 1:
			setTitleText("下软件记录");
			break;
		case 2:
			setTitleText("玩游戏记录");
			break;
		}
		
		/*titleTab.setOnPageChangeListener(new OnPageChangeListener() {
			
			@Override
			public void onPageSelected(int arg0) {
				// TODO Auto-generated method stub
				MakeFlowBillFragmentEvent event  = new MakeFlowBillFragmentEvent();
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
		return R.layout.activity_makebilllist;
	}

	public class MakeBillPagerAdapter extends FragmentPagerAdapter {

		private final String[] TITLES = { "看视频", "下软件", "玩游戏"/*, "交友"*/};

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
