package com.etoc.weflow.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Surface;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;

import com.etoc.weflow.R;
import com.etoc.weflow.activity.ExpenseFlowActivity;
import com.etoc.weflow.event.ExpenseFlowFragmentEvent;
import com.etoc.weflow.event.RechargeEvent;
import com.etoc.weflow.net.Requester;
import com.etoc.weflow.utils.ViewUtils;

import de.greenrobot.event.EventBus;

public class RechargeFragment extends Fragment {
	RadioGroup radioGroup = null;
	private RechargePhoneFragment phoneFragment;
	private RechargeQQFragment qqFragment;
	public Fragment currFragment;
	private final static String TAG = "RechargeFragment";
	private View mView;
	private int index = 0;
	
	public RechargeFragment() {
		super();
		index = 0;
	}
	public RechargeFragment(int index) {
		super();
		index = index;
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		EventBus.getDefault().register(this);
		if(savedInstanceState != null) {
			phoneFragment = (RechargePhoneFragment)getChildFragmentManager().getFragment(
					savedInstanceState, RechargePhoneFragment.class.getName());
			qqFragment = (RechargeQQFragment)getChildFragmentManager().getFragment(
					savedInstanceState, RechargeQQFragment.class.getName());
		}
	}
	
	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		EventBus.getDefault().unregister(this);
		super.onDestroy();
	}
	
	public void onEvent(ExpenseFlowFragmentEvent event) {
		if (event.getIndex() == ExpenseFlowActivity.INDEX_RECHARGE) {
			if (currFragment instanceof RechargePhoneFragment) {
				EventBus.getDefault().post(RechargeEvent.RECHARGE_PHONE);
			} else {
				EventBus.getDefault().post(RechargeEvent.RECHARGE_QQ);
			}
			
		}
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
		View v = inflater.inflate(R.layout.fragment_recharge, null);
		mView = v;
		initView(v);
		
		if (phoneFragment == null) {
			phoneFragment = new RechargePhoneFragment();
		}
		if (qqFragment == null) {
			qqFragment = new RechargeQQFragment();
		}
		if (currFragment == null) {
			if (index == 0) {
				currFragment = phoneFragment;
			} else {
				currFragment = qqFragment;
			}
		}
		if (null != getChildFragmentManager().findFragmentByTag(
				currFragment.getClass().getName())) {
			getChildFragmentManager().beginTransaction().remove(currFragment);
		}
		getChildFragmentManager().beginTransaction().replace(R.id.fl_content, currFragment,currFragment.getClass().getName()).commit();
		if (currFragment instanceof RechargePhoneFragment) {
			radioGroup.check(R.id.rb_phone);
		} else {
			radioGroup.check(R.id.rb_qq);
		}
		
		return v;
	}
	
	@Override
	public void onSaveInstanceState(Bundle outState) {
		// TODO Auto-generated method stub
		if (outState != null && currFragment != null) {
			Log.d(TAG, "onSaveInstanceState currContentFragment="
					+ currFragment);
			outState.putString("content", currFragment.getClass()
					.getName());
			getChildFragmentManager().putFragment(outState, currFragment.getClass()
					.getName(), currFragment);

		}
		super.onSaveInstanceState(outState);
	}
	
	private void initView(View view) {
		radioGroup = (RadioGroup) view.findViewById(R.id.rg_period_radio);
		ViewUtils.setMarginTop(radioGroup, 18);
		ViewUtils.setMarginLeft(radioGroup, 32);
		ViewUtils.setSize(view.findViewById(R.id.rb_phone), 154, 64);
		ViewUtils.setSize(view.findViewById(R.id.rb_qq), 154, 64);
		
		radioGroup.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(RadioGroup root, int checkedId) {
				// TODO Auto-generated method stub
				FragmentManager fm = getChildFragmentManager();
				FragmentTransaction ft = fm.beginTransaction();
				if(currFragment != null){
					ft.hide(currFragment);
				}
				Fragment fragment = null;
				switch(checkedId) {
				case R.id.rb_phone:
					fragment = fm.findFragmentByTag(RechargePhoneFragment.class.getName());
					if (fragment == null) {
						fragment = new RechargePhoneFragment();
						phoneFragment = (RechargePhoneFragment) fragment;
						ft.add(R.id.fl_content, fragment, RechargePhoneFragment.class.getName());
					} else {
						ft.show(fragment);
					}
					EventBus.getDefault().post(RechargeEvent.RECHARGE_PHONE);
					break;
				case R.id.rb_qq:
					Log.d("=AAA=","rb_previous_period");
					fragment = fm.findFragmentByTag(RechargeQQFragment.class.getName());
					if (fragment == null) {
						fragment = new RechargeQQFragment();
						qqFragment = (RechargeQQFragment) fragment;
						ft.add(R.id.fl_content, fragment, RechargeQQFragment.class.getName());
					} else {
						ft.show(fragment);
					}
					EventBus.getDefault().post(RechargeEvent.RECHARGE_QQ);
					break;
				}
				currFragment = fragment;
				ft.commitAllowingStateLoss();
			}
		});
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		Log.d("=AAA=","***********爸爸***************");
		if ((requestCode & 0xffff) == RechargePhoneFragment.REQUEST_CONTACT_PICK) {
			if (phoneFragment != null) {
				phoneFragment.onActivityResult(requestCode, resultCode, data);
			}
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

}
