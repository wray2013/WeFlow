package com.etoc.weflow.activity;

import com.etoc.weflow.R;

import android.os.Bundle;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;

/**
 * 存流量币
 * @author Ray
 *
 */
public class DepositFlowActivity extends TitleRootActivity {

	private EditText edDeposit;
	private int minValue = 0;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		if(getIntent() != null) {
			minValue = getIntent().getIntExtra("minValue", 0);
		}
		
		initView();
	}
	
	private void initView() {
		// TODO Auto-generated method stub
		setTitleText("存流量币");
		hideRightButton();
		
		edDeposit = (EditText) findViewById(R.id.ed_deposit_center_values_input);
		edDeposit.setText(minValue + "");
		edDeposit.setSelection(edDeposit.getText().length());
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
					if(currValue < minValue) {
						edDeposit.setText(minValue + "");
						edDeposit.setSelection(edDeposit.getText().length());
					}
				} catch(Exception e) {
					e.printStackTrace();
				}
			}
		});
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
