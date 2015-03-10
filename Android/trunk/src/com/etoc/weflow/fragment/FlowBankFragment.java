package com.etoc.weflow.fragment;

import com.etoc.weflow.R;
import com.etoc.weflow.activity.DrawFlowActivity;
import com.etoc.weflow.view.MagicTextView;

import android.content.Intent;
import android.os.Bundle;
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
			startActivity(new Intent(getActivity(), DrawFlowActivity.class));
			break;
		}
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
