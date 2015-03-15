package com.etoc.weflow.activity;

import com.etoc.weflow.R;
import com.etoc.weflow.utils.ViewUtils;

import android.os.Bundle;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

/**
 * 存流量币
 * @author Ray
 *
 */
public class DepositFlowActivity extends TitleRootActivity {

	private TextView tvBtnDeposit, tvTotal;
	private EditText edDeposit;
	private int minValue = 0;
	private int total = 0;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		if(getIntent() != null) {
			minValue = getIntent().getIntExtra("minValue", 0);
			total = getIntent().getIntExtra("total", 0);
		}
		
		initView();
	}
	
	private void initView() {
		// TODO Auto-generated method stub
		setTitleText("存流量币");
		hideRightButton();
		
		
		tvTotal = (TextView) findViewById(R.id.tv_deposit_top_total);
		tvBtnDeposit = (TextView) findViewById(R.id.tv_btn_deposit);
		
		tvTotal.setText(total + "");
		
		edDeposit = (EditText) findViewById(R.id.ed_deposit_center_values_input);
		edDeposit.setText(minValue + "");
		edDeposit.setSelection(edDeposit.getText().length());
		refreshBtnStatus(minValue);
		edDeposit.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence s, int arg1, int arg2, int arg3) {
				// TODO Auto-generated method stub
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int arg1, int arg2,
					int arg3) {
				// TODO Auto-generated method stub
			}
			
			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub
				try {
					int currValue = Integer.valueOf(s.toString());
					
					if(currValue > total) {
						edDeposit.setText(total + "");
						edDeposit.setSelection(edDeposit.getText().length());
						currValue = total;
					}
					
					refreshBtnStatus(currValue);
					/*if(currValue < minValue) {
						edDeposit.setText(minValue + "");
						edDeposit.setSelection(edDeposit.getText().length());
					}*/
				} catch(Exception e) {
					e.printStackTrace();
				}
			}
		});
		
		ViewUtils.setHeight(findViewById(R.id.tv_deposit_top_tel_hint), 96);
		ViewUtils.setMarginLeft(findViewById(R.id.tv_deposit_top_tel_hint), 32);
		
		ViewUtils.setMarginLeft(findViewById(R.id.v_divider_h), 32);
		ViewUtils.setMarginRight(findViewById(R.id.v_divider_h), 32);
		
		ViewUtils.setMarginTop(findViewById(R.id.tv_deposit_top_total_hint), 38);
		ViewUtils.setMarginTop(findViewById(R.id.tv_deposit_top_total), 50);
		ViewUtils.setMarginTop(findViewById(R.id.v_divider_top), 50);
		
		ViewUtils.setTextSize(findViewById(R.id.tv_deposit_top_tel_hint), 32);
		ViewUtils.setTextSize(findViewById(R.id.tv_deposit_top_tel), 32);
		ViewUtils.setTextSize(findViewById(R.id.tv_deposit_top_total_hint), 32);
		ViewUtils.setTextSize(findViewById(R.id.tv_deposit_top_total), 85);
		
		ViewUtils.setTextSize(findViewById(R.id.tv_deposit_center_values_input_hint), 32);
		ViewUtils.setTextSize(findViewById(R.id.tv_deposit_center_values_min_hint), 32);
		ViewUtils.setTextSize(findViewById(R.id.tv_deposit_center_values_min), 32);
		ViewUtils.setTextSize(findViewById(R.id.tv_deposit_center_values_min_hint2), 32);
		ViewUtils.setTextSize(findViewById(R.id.ed_deposit_center_values_input), 38);
		
		ViewUtils.setHeight(findViewById(R.id.ed_deposit_center_values_input), 100);
	}
	
	private void refreshBtnStatus(int currValue) {
		if(currValue < minValue) {
			tvBtnDeposit.setClickable(false);
			tvBtnDeposit.setBackgroundResource(R.drawable.shape_corner_recentage_grey);
			edDeposit.setBackgroundResource(R.drawable.shape_square_recentage_grey);
		} else {
			tvBtnDeposit.setClickable(true);
			tvBtnDeposit.setBackgroundResource(R.drawable.shape_corner_recentage_orange);
			edDeposit.setBackgroundResource(R.drawable.shape_square_recentage_orange);
		}
	}
	
	@Override
	protected int graviteType() {
		// TODO Auto-generated method stub
		return GRAVITE_LEFT;
	}

	@Override
	public boolean handleMessage(Message msg) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public int subContentViewId() {
		// TODO Auto-generated method stub
		return R.layout.activity_deposit_flow;
	}

}
