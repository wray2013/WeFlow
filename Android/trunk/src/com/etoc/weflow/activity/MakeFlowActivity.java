package com.etoc.weflow.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;

import com.astuetz.PagerSlidingTabStrip;
import com.etoc.weflow.R;
import com.etoc.weflow.activity.login.LoginActivity;
import com.etoc.weflow.event.MakeFlowFragmentEvent;
import com.etoc.weflow.fragment.AdvertisementFragment;
import com.etoc.weflow.fragment.AppReccomFragment;
import com.etoc.weflow.fragment.PlayGameFragment;
import com.etoc.weflow.utils.ConStant;
import com.etoc.weflow.utils.DisplayUtil;

import de.greenrobot.event.EventBus;

public class MakeFlowActivity extends TitleRootActivity {

	private PagerSlidingTabStrip titleTab;
	private ViewPager viewPage;
	private MyPagerAdapter adapter;
	
	private boolean isLogin = false;
	public final static int INDEX_ADVERTISEMENT = 0;
	public final static int INDEX_APPRECOMM = 1;
	public final static int INDEX_PLAYGAME = 2;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		initViews();
		
		titleTab = (PagerSlidingTabStrip) findViewById(R.id.tabs);
		viewPage = (ViewPager) findViewById(R.id.pager);
		
		titleTab.setTabPaddingLeftRight(DisplayUtil.getSize(this, 50));
		
		adapter = new MyPagerAdapter(getSupportFragmentManager());
		viewPage.setAdapter(adapter);
		
		titleTab.setViewPager(viewPage);
		
		int index = getIntent().getIntExtra(ConStant.INTENT_MAKE_FLOW, 0);
		index = index == 0?0:index - 1;
		viewPage.setCurrentItem(index);
		final int indexTemp = index;
		handler.postDelayed(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				MakeFlowFragmentEvent event  = new MakeFlowFragmentEvent();
				event.setIndex(indexTemp);
				EventBus.getDefault().post(event);
			}
		}, 100);
		
		
		isLogin = getIntent().getBooleanExtra("isLogin", false);
		
		titleTab.setOnPageChangeListener(new OnPageChangeListener() {
			
			@Override
			public void onPageSelected(int arg0) {
				// TODO Auto-generated method stub
				MakeFlowFragmentEvent event  = new MakeFlowFragmentEvent();
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
		});
	}
	
	private void initViews() {
		setTitleText("赚流量币");
		setRightButtonText("记录");
	}
	
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch(v.getId()) {
		case R.id.btn_title_right:
			if(isLogin) {
				Intent makeIntent = new Intent(this, MakeBillListActivity.class);
				makeIntent.putExtra(ConStant.INTENT_MAKE_FLOW, viewPage.getCurrentItem());
				startActivity(makeIntent);
			} else {
				startActivity(new Intent(this, LoginActivity.class));
				finish();
			}
			break;
		}
		super.onClick(v);
	}
	
	@Override
	public boolean handleMessage(Message msg) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public int subContentViewId() {
		// TODO Auto-generated method stub
		return R.layout.activity_make_flow;
	}
	
	@Override
	protected int graviteType() {
		// TODO Auto-generated method stub
		return GRAVITE_LEFT;
	}
	
	public class MyPagerAdapter extends FragmentPagerAdapter {

		private final String[] TITLES = { "看广告", "下软件", "玩游戏"/*, "交友"*/};

		public MyPagerAdapter(FragmentManager fm) {
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
			Fragment frag = null;
			switch (position) {
			case 0:
				frag = new AdvertisementFragment();
				break;
			case 1:
				frag = new AppReccomFragment();
				break;
			default:
				frag = new PlayGameFragment(); //SuperAwesomeCardFragment.newInstance(position);
				break;
			}
			return frag;
		}

	}
	
}
