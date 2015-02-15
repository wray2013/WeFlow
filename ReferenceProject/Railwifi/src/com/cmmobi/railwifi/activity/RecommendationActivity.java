package com.cmmobi.railwifi.activity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.os.Bundle;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;

import com.cmmobi.railwifi.R;
import com.cmmobi.railwifi.adapter.RecommendationAdapter;
import com.cmmobi.railwifi.fragment.PeriodRecommendationFragment;
import com.cmmobi.railwifi.fragment.PreviousRecommendationFragment;
import com.cmmobi.railwifi.network.GsonResponseObject;
import com.cmmobi.railwifi.network.Requester;
import com.cmmobi.railwifi.network.GsonResponseObject.SubAlumbElem;
import com.cmmobi.railwifi.utils.CmmobiClickAgentWrapper;
import com.cmmobi.railwifi.utils.ViewUtils;

import de.greenrobot.event.EventBus;
import dev.dworks.libs.astickyheader.SimpleSectionedListAdapter;
import dev.dworks.libs.astickyheader.SimpleSectionedListAdapter.Section;

public class RecommendationActivity extends TitleRootActivity implements OnClickListener, Callback {
	
	private PeriodRecommendationFragment periodRecommendationFragment;
	private PreviousRecommendationFragment previousRecommendationFragment;
	public static final String INTENT_RECOMMENDATION_TAB = "intent_recommendation_tab";
	public Fragment currFragment;
	private RadioGroup rg;
	private Handler handler = null;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		handler = new Handler(this);
		Requester.requestRecommendList(handler);
		hideTitlebar();
		
		initViews();
		
		String tab = getIntent().getStringExtra(INTENT_RECOMMENDATION_TAB);
		
		if ("period".equals(tab)) {
			if (periodRecommendationFragment == null) {
				periodRecommendationFragment = new PeriodRecommendationFragment();
			}
			
			currFragment = periodRecommendationFragment;
			getSupportFragmentManager().beginTransaction()
			.replace(R.id.fl_content, periodRecommendationFragment,periodRecommendationFragment.getClass().getName()).commit();
			rg.check(R.id.rb_this_period);
		} else {
			if (previousRecommendationFragment == null) {
				previousRecommendationFragment = new PreviousRecommendationFragment();
			}
			
			currFragment = previousRecommendationFragment;
			getSupportFragmentManager().beginTransaction()
			.replace(R.id.fl_content, previousRecommendationFragment,previousRecommendationFragment.getClass().getName()).commit();
			rg.check(R.id.rb_previous_period);
		}
		
	}
	
	private void initViews() {
		rg = (RadioGroup) findViewById(R.id.rg_period_radio);
		
		rg.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				// TODO Auto-generated method stub
				FragmentManager fm = getSupportFragmentManager();
				FragmentTransaction ft = fm.beginTransaction();
				if(currFragment != null){
					ft.hide(currFragment);
				}
				Fragment fragment = null;
				switch(checkedId) {
				case R.id.rb_this_period:
					Log.d("=AAA=","rb_this_period");
					if (previousRecommendationFragment != null) {
						previousRecommendationFragment.collapseGroup();
					}
					CmmobiClickAgentWrapper.onEvent(RecommendationActivity.this, "av_switch","1");
					fragment = fm.findFragmentByTag(PeriodRecommendationFragment.class.getName());
					if (fragment == null) {
						fragment = new PeriodRecommendationFragment();
						periodRecommendationFragment = (PeriodRecommendationFragment) fragment;
						ft.add(R.id.fl_content, fragment, PeriodRecommendationFragment.class.getName());
					} else {
						ft.show(fragment);
					}
					break;
				case R.id.rb_previous_period:
					Log.d("=AAA=","rb_previous_period");
					CmmobiClickAgentWrapper.onEvent(RecommendationActivity.this, "av_switch","2");
					fragment = fm.findFragmentByTag(PreviousRecommendationFragment.class.getName());
					if (fragment == null) {
						fragment = new PreviousRecommendationFragment();
						previousRecommendationFragment = (PreviousRecommendationFragment) fragment;
						ft.add(R.id.fl_content, fragment, PreviousRecommendationFragment.class.getName());
					} else {
						ft.show(fragment);
					}
					break;
				}
				currFragment = fragment;
				ft.commitAllowingStateLoss();
			}
		});
		
		Button btnBack = (Button) findViewById(R.id.btn_title_back);
		ViewUtils.setMarginLeft(btnBack, 12);
		btnBack.setOnClickListener(this);
		ViewUtils.setHeight(findViewById(R.id.rl_top), 96);
		ViewUtils.setSize(findViewById(R.id.rb_this_period), 256, 64);
		ViewUtils.setSize(findViewById(R.id.rb_previous_period), 256, 64);
	}
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		EventBus.getDefault().removeAllStickyEvents();
		super.onDestroy();
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch(v.getId()) {
		case R.id.btn_title_back:
			finish();
			break;
		}
	}

	@Override
	public boolean handleMessage(Message msg) {
		// TODO Auto-generated method stub
		switch(msg.what) {
		case Requester.RESPONSE_TYPE_MEDIA_RECOMMANDLIST:
			if (msg.obj != null) {
				GsonResponseObject.recmmandListResp resp = (GsonResponseObject.recmmandListResp) msg.obj;
				if ("0".equals(resp.status)) {
					GsonResponseObject.AlumbElem[] recommArray = resp.list;
					EventBus.getDefault().postSticky(recommArray);
				}
			}
			break;
		}
		return false;
	}

	@Override
	public int subContentViewId() {
		// TODO Auto-generated method stub
		return R.layout.activity_recommendation;
	}

}
