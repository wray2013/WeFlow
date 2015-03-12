package com.etoc.weflow.fragment;

import com.etoc.weflow.R;
import com.etoc.weflow.activity.DepositFlowActivity;
import com.etoc.weflow.net.Requester;
import com.etoc.weflow.net.GsonResponseObject.testResponse;
import com.etoc.weflow.utils.ViewUtils;
import com.etoc.weflow.view.MagicTextView;

import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.TextView;

public class FlowBankFragment extends XFragment<Object>/*TitleRootFragment*/implements OnClickListener {

	private final String TAG = "FlowBankFragment";
	
	private DisplayMetrics dm = new DisplayMetrics();
	
	private MagicTextView mtvMoney;
	
	private TextView tvDrawFlow, tvSaveFlow;
	
	/*@Override
	public int subContentViewId() {
		// TODO Auto-generated method stub
		return R.layout.fragment_flowbank;
	}*/
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreateView(inflater, container, savedInstanceState);
		View v = inflater.inflate(R.layout.fragment_flowbank, null);
		initView(v);
		return v;
	}
	
	private void initView(View view) {
		dm = getResources().getDisplayMetrics();
		
		mtvMoney = (MagicTextView) view.findViewById(R.id.mtv_total_money);
		mtvMoney.setNumber(2600);
//		mtvMoney.showNumberWithAnimation(21985.2f, 1000);
		
		tvDrawFlow = (TextView) view.findViewById(R.id.tv_pop);
		tvSaveFlow = (TextView) view.findViewById(R.id.tv_save);
		
		tvDrawFlow.setOnClickListener(this);
		tvSaveFlow.setOnClickListener(this);
		
		viewAdapter(view);
	}
	
	
	
	private void viewAdapter(View view) {
		// TODO Auto-generated method stub
		ViewUtils.setHeight(view.findViewById(R.id.rl_bank_top), 275);
		ViewUtils.setWidth(view.findViewById(R.id.ll_bank_bottom), 658);
		ViewUtils.setHeight(view.findViewById(R.id.ll_bank_bottom), 112);
		ViewUtils.setHeight(view.findViewById(R.id.v_divider), 72);
		
		ViewUtils.setWidth(mtvMoney, 380);
		
		ViewUtils.setTextSize((TextView) view.findViewById(R.id.tv_yest_income), 105);
		ViewUtils.setTextSize(mtvMoney, 70);
		ViewUtils.setTextSize((TextView) view.findViewById(R.id.tv_pop),  38);
		ViewUtils.setTextSize((TextView) view.findViewById(R.id.tv_save), 38);
		
		
	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		Log.d(TAG, "onResume");
		if(mtvMoney != null)
			mtvMoney.showNumberWithAnimation(2600, 1000);
	}
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_title_left:
			break;
		case R.id.tv_pop:
			Requester.test(handler);
//			startActivity(new Intent(getActivity(), DrawFlowActivity.class));
//			getActivity().overridePendingTransition(R.anim.slide_in_from_right, R.anim.slide_out_to_left);
			break;
		case R.id.tv_save:
			Intent depositIntent = new Intent(getActivity(), DepositFlowActivity.class);
			depositIntent.putExtra("minValue", 10000);
			startActivity(depositIntent);
			getActivity().overridePendingTransition(R.anim.slide_in_from_right, R.anim.slide_out_to_left);
			break;
		}
	}
	
	@Override
	public boolean handleMessage(Message msg) {
		// TODO Auto-generated method stub
		switch(msg.what) {
		case Requester.RESPONSE_TYPE_TEST:
			if(msg.obj != null) {
				testResponse response = (testResponse) msg.obj;
				Log.d(TAG, "response = " + response.status);
				if(response.status.equals("0")) {
					
				}
			}
			break;
		}
		return false;
	}
	
	@Override
	public int getIndex() {
		return INDEX_BANK;
	}

	@Override
	public void onShow() {
		Log.d(TAG, "onShow IN!");
		if(mtvMoney != null)
			mtvMoney.showNumberWithAnimation(2600, 1000);
	}
	
}
