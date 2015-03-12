package com.etoc.weflow.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;

import com.etoc.weflow.R;

public class RechargeFragment extends Fragment {
	RadioGroup radioGroup = null;
	private RechargePhoneFragment phoneFragment;
	private RechargeQQFragment qqFragment;
	public Fragment currFragment;
	private final static String TAG = "RechargeFragment";
	private View mView;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		if(savedInstanceState != null) {
			phoneFragment = (RechargePhoneFragment)getChildFragmentManager().getFragment(
					savedInstanceState, RechargePhoneFragment.class.getName());
			qqFragment = (RechargeQQFragment)getChildFragmentManager().getFragment(
					savedInstanceState, RechargeQQFragment.class.getName());
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
			currFragment = phoneFragment;
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
					break;
				}
				currFragment = fragment;
				ft.commitAllowingStateLoss();
			}
		});
	}

}
