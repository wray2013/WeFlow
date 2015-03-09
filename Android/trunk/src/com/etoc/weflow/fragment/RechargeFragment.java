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
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreateView(inflater, container, savedInstanceState);
		View v = inflater.inflate(R.layout.fragment_recharge, null);
		initView(v);
		
		if (phoneFragment == null) {
			phoneFragment = new RechargePhoneFragment();
		}
		currFragment = phoneFragment;
		getChildFragmentManager().beginTransaction().replace(R.id.fl_content, phoneFragment,phoneFragment.getClass().getName()).commit();
		radioGroup.check(R.id.rb_phone);
		return v;
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
