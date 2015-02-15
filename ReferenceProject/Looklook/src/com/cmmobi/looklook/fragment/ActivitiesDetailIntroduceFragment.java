package com.cmmobi.looklook.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.cmmobi.looklook.R;
import com.cmmobi.looklook.activity.ActivitiesDetailActivity;
import com.cmmobi.looklook.common.gson.GsonResponse2.activeListItem;
import com.cmmobi.looklook.common.view.pulltorefresh.PullToRefreshBase.Mode;

public class ActivitiesDetailIntroduceFragment extends Fragment{
	private View contentView;
	
	ActivitiesDetailActivity activitiesDetailActivity;
	activeListItem activeItem;

	private TextView introduceText;

	private TextView wayText;

	private TextView ruleText;

	private TextView awardText;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		contentView = inflater.inflate(
				R.layout.fragment_activities_detail_introduce, null);
		
		activitiesDetailActivity = (ActivitiesDetailActivity)getActivity();
		
		this.activeItem = activitiesDetailActivity.activeItem;
		
		activitiesDetailActivity.scrollView.setMode(Mode.DISABLED);
		
		
		introduceText = (TextView) contentView.findViewById(R.id.introduce_text);
		wayText = (TextView) contentView.findViewById(R.id.way_text);
		
		ruleText = (TextView) contentView.findViewById(R.id.rule_text);
		
		awardText = (TextView) contentView.findViewById(R.id.award_text);
		
		if (null != activeItem) {
			introduceText.setText(activeItem.introduction);
			wayText.setText(activeItem.add_way);
			ruleText.setText(activeItem.rule);
			awardText.setText(activeItem.prize);
			
		}
		return contentView;	
	}
}
