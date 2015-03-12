package com.etoc.weflow.fragment;

import com.etoc.weflow.R;
import com.etoc.weflow.activity.SignInActivity;
import com.etoc.weflow.activity.ExpenseFlowActivity;
import com.etoc.weflow.activity.MakeFlowActivity;
import com.etoc.weflow.activity.MyBillListActivity;
import com.etoc.weflow.utils.ViewUtils;

import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class MyselfFragment extends XFragment<Object>/*TitleRootFragment*/implements OnClickListener {

	private final String TAG = "MyselfFragment";
	
	private RelativeLayout rlMyBill;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreateView(inflater, container, savedInstanceState);
		View v = inflater.inflate(R.layout.fragment_myself, null);
		initView(v);
		return v;
	}
	
	private void initView(View view) {
		rlMyBill = (RelativeLayout) view.findViewById(R.id.rl_me_bill);
		rlMyBill.setOnClickListener(this);
		
		
		view.findViewById(R.id.rl_me_sign).setOnClickListener(this);
		view.findViewById(R.id.rl_me_invite).setOnClickListener(this);
		view.findViewById(R.id.rl_me_feedback).setOnClickListener(this);
		
		ViewUtils.setHeight(view.findViewById(R.id.rl_me_top), 222);
		ViewUtils.setHeight(view.findViewById(R.id.tv_account_hint), 141);
		
		ViewUtils.setHeight(view.findViewById(R.id.rl_me_msg), 112);
		ViewUtils.setHeight(view.findViewById(R.id.rl_me_bill), 112);
		ViewUtils.setHeight(view.findViewById(R.id.rl_me_sign), 112);
		ViewUtils.setHeight(view.findViewById(R.id.rl_me_invite), 112);
		ViewUtils.setHeight(view.findViewById(R.id.rl_me_feedback), 112);
		ViewUtils.setHeight(view.findViewById(R.id.rl_me_settings), 112);
		
		ViewUtils.setTextSize((TextView) view.findViewById(R.id.tv_account_hint), 35);
		ViewUtils.setTextSize((TextView) view.findViewById(R.id.tv_account_tel), 35);
		
		ViewUtils.setTextSize((TextView) view.findViewById(R.id.tv_flow_hint), 35);
		ViewUtils.setTextSize((TextView) view.findViewById(R.id.tv_flow), 35);
		ViewUtils.setTextSize((TextView) view.findViewById(R.id.tv_flow_paper_hint), 35);
		ViewUtils.setTextSize((TextView) view.findViewById(R.id.tv_flow_paper), 35);
		
		ViewUtils.setTextSize((TextView) view.findViewById(R.id.tv_msg), 35);
		ViewUtils.setTextSize((TextView) view.findViewById(R.id.tv_bill), 35);
		ViewUtils.setTextSize((TextView) view.findViewById(R.id.tv_sign), 35);
		ViewUtils.setTextSize((TextView) view.findViewById(R.id.tv_invite), 35);
		ViewUtils.setTextSize((TextView) view.findViewById(R.id.tv_feedback), 35);
		ViewUtils.setTextSize((TextView) view.findViewById(R.id.tv_settings), 35);
		
		ViewUtils.setMarginLeft((TextView) view.findViewById(R.id.tv_account_hint), 52);
		ViewUtils.setMarginLeft((TextView) view.findViewById(R.id.tv_flow_hint), 52);
		ViewUtils.setMarginLeft((TextView) view.findViewById(R.id.tv_flow_paper_hint), 52);
		
		ViewUtils.setMarginRight((TextView) view.findViewById(R.id.tv_flow), 25);
		ViewUtils.setMarginRight((TextView) view.findViewById(R.id.tv_flow_paper), 25);
		
		ViewUtils.setMarginLeft((TextView) view.findViewById(R.id.tv_msg), 10);
		ViewUtils.setMarginLeft((TextView) view.findViewById(R.id.tv_bill), 10);
		ViewUtils.setMarginLeft((TextView) view.findViewById(R.id.tv_sign), 10);
		ViewUtils.setMarginLeft((TextView) view.findViewById(R.id.tv_invite), 10);
		ViewUtils.setMarginLeft((TextView) view.findViewById(R.id.tv_feedback), 10);
		ViewUtils.setMarginLeft((TextView) view.findViewById(R.id.tv_settings), 10);
	}
	
	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		Log.d(TAG, "onResume");
	}
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_title_left:
			
			break;
		case R.id.rl_me_bill:
			startActivity(new Intent(getActivity(), MyBillListActivity.class));
			break;
		case R.id.rl_me_sign:
			startActivity(new Intent(getActivity(),SignInActivity.class));
			break;
		case R.id.rl_me_invite:
			startActivity(new Intent(getActivity(),ExpenseFlowActivity.class));
			break;
		case R.id.rl_me_feedback:
			startActivity(new Intent(getActivity(),MakeFlowActivity.class));
			break;
		}
	}
	
	@Override
	public int getIndex() {
		return INDEX_ME;
	}

	@Override
	public void onShow() {
		Log.d(TAG, "onShow IN!");
	}
	
}
