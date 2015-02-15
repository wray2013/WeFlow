package com.cmmobi.railwifi.fragment;

import com.cmmobi.railwifi.R;
import com.cmmobi.railwifi.activity.FeedBackActivity;
import com.cmmobi.railwifi.activity.GameActivity;
import com.cmmobi.railwifi.utils.DisplayUtil;
import com.cmmobi.railwifi.utils.ViewUtils;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

public class PopularAppFragment extends TitleRootFragment {

	@Override
	public int subContentViewId() {
		// TODO Auto-generated method stub
		return R.layout.fragment_city_life;
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View view = super.onCreateView(inflater, container, savedInstanceState);
		initViews(view);
		return view;
	}
	
	private void initViews(View view) {
		hideRightButton();
		setTitleText("热门应用");
		setLeftButtonBackground(R.drawable.btn_navigation);
		
		Button btnGame = (Button) view.findViewById(R.id.btn_game);
		Button btnReport = (Button) view.findViewById(R.id.btn_report);
		TextView tvTips = (TextView) view.findViewById(R.id.tv_empty_tips);
		TextView tvBusniss = (TextView) view.findViewById(R.id.tv_business);
		
		btnGame.setOnClickListener(this);
		btnReport.setOnClickListener(this);
		
		ViewUtils.setHeight(view.findViewById(R.id.rl_bottom), 176);
		ViewUtils.setHeight(view.findViewById(R.id.rl_business), 76);
		
		btnGame.setTextSize(DisplayUtil.textGetSizeSp(getActivity(), 33));
		btnReport.setTextSize(DisplayUtil.textGetSizeSp(getActivity(), 33));
		tvTips.setTextSize(DisplayUtil.textGetSizeSp(getActivity(), 48));
		tvBusniss.setTextSize(DisplayUtil.textGetSizeSp(getActivity(), 30));
		ViewUtils.setMarginTop(tvTips, 24);
		
		ViewUtils.setSize(btnGame, 321, 125);
		ViewUtils.setSize(btnReport, 321, 125);
	}
	
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.btn_title_left:
			getSlidingMenu().toggle();
			break;
		case R.id.btn_game:
			startActivity(new Intent(getActivity(),GameActivity.class));
			break;
		case R.id.btn_report:
			startActivity(new Intent(getActivity(),FeedBackActivity.class));
			break;
		}
		super.onClick(v);
	}

}
