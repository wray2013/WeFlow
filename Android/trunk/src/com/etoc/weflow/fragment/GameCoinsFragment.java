package com.etoc.weflow.fragment;

import com.etoc.weflow.R;
import com.etoc.weflow.activity.ExpenseFlowActivity;
import com.etoc.weflow.event.ExpenseFlowFragmentEvent;
import com.etoc.weflow.event.GameCoinEvent;
import com.etoc.weflow.event.RechargeEvent;
import com.etoc.weflow.utils.ViewUtils;

import de.greenrobot.event.EventBus;

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

public class GameCoinsFragment extends Fragment {

	RadioGroup radioGroup = null;
	public Fragment currFragment;
	private final static String TAG = "RechargeFragment";
	private View mView;
	private GameGiftFragment giftFragment;
	private GameRechargeFragment rechargeFragment;
	int index = 0;
	
	public GameCoinsFragment() {
		super();
		this.index = 0;
	}
	public GameCoinsFragment(int index) {
		super();
		this.index = index;
	}
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		EventBus.getDefault().register(this);
		if(savedInstanceState != null) {
			giftFragment = (GameGiftFragment)getChildFragmentManager().getFragment(
					savedInstanceState, GameGiftFragment.class.getName());
			rechargeFragment = (GameRechargeFragment)getChildFragmentManager().getFragment(
					savedInstanceState, GameRechargeFragment.class.getName());
		}
	}
	
	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		EventBus.getDefault().unregister(this);
		super.onDestroy();
	}
	
	public void onEvent(ExpenseFlowFragmentEvent event) {
		if (event.getIndex() == ExpenseFlowActivity.INDEX_GAME) {
			if (currFragment instanceof GameGiftFragment) {
				EventBus.getDefault().post(GameCoinEvent.GAMEGIFT);
			} else {
				EventBus.getDefault().post(GameCoinEvent.GAMERECHARGE);
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
		
		View v = inflater.inflate(R.layout.fragment_game_coins, null);
		mView = v;
		initView(v);
		
		if (giftFragment == null) {
			giftFragment = new GameGiftFragment();
		}
		if (rechargeFragment == null) {
			rechargeFragment = new GameRechargeFragment();
		}
		if (currFragment == null) {
			if (index == 0) {
				currFragment = giftFragment;
			} else {
				currFragment = rechargeFragment;
			}
		}
		if (null != getChildFragmentManager().findFragmentByTag(
				currFragment.getClass().getName())) {
			getChildFragmentManager().beginTransaction().remove(currFragment);
		}
		getChildFragmentManager().beginTransaction().replace(R.id.fl_content, currFragment,currFragment.getClass().getName()).commit();
		if (currFragment instanceof GameGiftFragment) {
			radioGroup.check(R.id.rb_game_gift);
		} else {
			radioGroup.check(R.id.rb_game_recharge);
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
		ViewUtils.setSize(view.findViewById(R.id.rb_game_gift), 154, 64);
		ViewUtils.setSize(view.findViewById(R.id.rb_game_recharge), 154, 64);
		
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
				case R.id.rb_game_gift:
					fragment = fm.findFragmentByTag(GameGiftFragment.class.getName());
					if (fragment == null) {
						fragment = new GameGiftFragment();
						giftFragment = (GameGiftFragment) fragment;
						ft.add(R.id.fl_content, fragment, GameGiftFragment.class.getName());
					} else {
						ft.show(fragment);
					}
					EventBus.getDefault().post(GameCoinEvent.GAMEGIFT);
					break;
				case R.id.rb_game_recharge:
					Log.d("=AAA=","rb_previous_period");
					fragment = fm.findFragmentByTag(GameRechargeFragment.class.getName());
					if (fragment == null) {
						fragment = new GameRechargeFragment();
						rechargeFragment = (GameRechargeFragment) fragment;
						ft.add(R.id.fl_content, fragment, GameRechargeFragment.class.getName());
					} else {
						ft.show(fragment);
					}
					EventBus.getDefault().post(GameCoinEvent.GAMERECHARGE);
					break;
				}
				currFragment = fragment;
				ft.commitAllowingStateLoss();
			}
		});
	}
}
