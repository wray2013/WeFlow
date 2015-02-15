package com.cmmobi.railwifi.activity;

import java.util.HashMap;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.LocalBroadcastManager;
import android.text.Html;
import android.text.StaticLayout;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cmmobi.railwifi.R;
import com.cmmobi.railwifi.fragment.RailTravelCheckOneFragment;
import com.cmmobi.railwifi.fragment.RailTravelCheckThreeFragment;
import com.cmmobi.railwifi.fragment.RailTravelCheckTwoFragment;
import com.cmmobi.railwifi.fragment.RailTravelOrderCheckFragment;
import com.cmmobi.railwifi.network.GsonResponseObject;
import com.cmmobi.railwifi.network.GsonResponseObject.LineInfo;
import com.cmmobi.railwifi.network.GsonResponseObject.newsElem;
import com.cmmobi.railwifi.network.Requester;
import com.cmmobi.railwifi.utils.CmmobiClickAgentWrapper;
import com.cmmobi.railwifi.utils.DisplayUtil;
import com.google.gson.Gson;

/**
 * @author youtian
 * @email youtian@cmmobi.com
 * @date  2014-11-18
 */
public class RailTravelOrderHistoryAcitivity extends TitleRootActivity {

	private String TAG = "RailTravelOrderHistoryAcitivity";
	private LinearLayout llCheckTab;
	private Button btnCheckOne;
	private Button btnCheckTwo;
	
	public Fragment currFragment;
	private static String ORDER_TAB_NOT_PAID = "ORDER_TAB_NOT_PAID";
	private static String ORDER_TAB_PAID = "ORDER_TAB_PAID";
	
	@Override
	public int subContentViewId() {
		return R.layout.activity_railtravel_order_history;
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setTitleText("待支付订单");
		hideRightButton();
		
		llCheckTab = (LinearLayout) findViewById(R.id.ll_check_tab);
		llCheckTab.setPadding(DisplayUtil.getSize(this, 12), DisplayUtil.getSize(this, 21), DisplayUtil.getSize(this, 12), DisplayUtil.getSize(this, 21));
		
		btnCheckOne = (Button) findViewById(R.id.btn_check_one);
		btnCheckOne.setOnClickListener(this);
		btnCheckTwo = (Button) findViewById(R.id.btn_check_two);
		btnCheckTwo.setOnClickListener(this);
		
		btnCheckOne.setTextSize(DisplayUtil.textGetSizeSp(this, 30));
		btnCheckTwo.setTextSize(DisplayUtil.textGetSizeSp(this, 30));
		
		onCheckChanged(R.id.btn_check_one);
	}
	
	
	

	
	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		
	}
	
	@Override
	public void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
	}

	
	
	
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		super.onClick(v);
		switch(v.getId()){
		case R.id.btn_check_one:
		case R.id.btn_check_two:
			onCheckChanged(v.getId());
			break;
			default:{
				
			}	
		}
	}	
	
	
	public void onCheckChanged(int id) {
		FragmentManager fm = getSupportFragmentManager();
		FragmentTransaction ft = fm.beginTransaction();
		if(currFragment != null){
			ft.hide(currFragment);
		}
		Fragment fragment = null;
		switch (id) {
		case R.id.btn_check_one:
			setTitleText("待支付订单");
			CmmobiClickAgentWrapper.onEvent(this, "t_tra_fav", "1");
			fragment = fm.findFragmentByTag(RailTravelOrderCheckFragment.class.getName()+"0");
			if (fragment == null) {
				fragment = new RailTravelOrderCheckFragment();
				Bundle bundle = new Bundle();
				bundle.putString("ispaid", "0");
				fragment.setArguments(bundle);
				ft.add(R.id.empty, fragment, RailTravelOrderCheckFragment.class.getName()+"0");
			} else {
				ft.show(fragment);
			}
			currFragment = fragment;		
			btnCheckOne.setTextColor(Color.WHITE);
			btnCheckTwo.setTextColor(Color.parseColor("#212434"));
			btnCheckOne.setBackgroundResource(R.drawable.table_left_selected_3);
			btnCheckTwo.setBackgroundResource(R.drawable.table_right_default_3);
			break;
		case R.id.btn_check_two:
			CmmobiClickAgentWrapper.onEvent(this, "t_tra_fav", "2");
			setTitleText("已支付订单");
			fragment = fm.findFragmentByTag(RailTravelOrderCheckFragment.class.getName()+"1");
			if (fragment == null) {
				fragment = new RailTravelOrderCheckFragment();
				Bundle bundle = new Bundle();
				bundle.putString("ispaid", "1");
				fragment.setArguments(bundle);
				ft.add(R.id.empty, fragment, RailTravelOrderCheckFragment.class.getName()+"1");
			} else {
				ft.show(fragment);
			}
			currFragment = fragment;

			btnCheckOne.setTextColor(Color.parseColor("#212434"));
			btnCheckTwo.setTextColor(Color.WHITE);
			btnCheckOne.setBackgroundResource(R.drawable.table_left_default_3);
			btnCheckTwo.setBackgroundResource(R.drawable.table_right_selected_3);
			break;
		default:
			break;
		}
		ft.commitAllowingStateLoss();
	}
	
	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}

	@Override
	public boolean handleMessage(Message msg) {
		// TODO Auto-generated method stub
		
		return false;
	}

}
