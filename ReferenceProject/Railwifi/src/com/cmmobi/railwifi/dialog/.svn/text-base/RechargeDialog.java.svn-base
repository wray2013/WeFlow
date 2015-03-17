package com.cmmobi.railwifi.dialog;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.webkit.WebView.FindListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cmmobi.railwifi.R;
import com.cmmobi.railwifi.utils.StringUtils;
import com.cmmobi.railwifi.utils.ViewUtils;

public class RechargeDialog implements OnClickListener {
	Context context;
	AlertDialog alertDialog;
	private LayoutInflater inflater;
	private AlertDialog.Builder builder;
	private Button btnOK;
	private View view;
	private TextView tvCost;
	private EditText etContent;
	private RelativeLayout rlCostTip;
	private DialogInterface.OnClickListener okClickListener;
	
	
	public RechargeDialog(Context context,DialogInterface.OnClickListener listener) {
		okClickListener = listener;
		this.context = context;
		init(context);
	}
	
	private void init(Context context) {
		builder = new AlertDialog.Builder(context); 
		inflater=LayoutInflater.from(context);
		view=inflater.inflate(R.layout.recharge_dialog, null);
		
		etContent = (EditText) view.findViewById(R.id.et_content);
		tvCost = (TextView) view.findViewById(R.id.tv_cost_num);
		
		rlCostTip = (RelativeLayout) view.findViewById(R.id.rl_cost_tip);
		btnOK = (Button) view.findViewById(R.id.btn_ok);
		btnOK.setText("请输入数量");
		btnOK.setEnabled(false);
		etContent.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub
				if (StringUtils.isEmpty(etContent.getText().toString())) {
					rlCostTip.setVisibility(View.GONE);
					btnOK.setText("请输入数量");
					btnOK.setEnabled(false);
				} else {
					rlCostTip.setVisibility(View.VISIBLE);
					btnOK.setText("立即支付");
					btnOK.setEnabled(true);
				}
				tvCost.setText(etContent.getText().toString() + "元");
			}
		});
		
		ViewUtils.setSize(view.findViewById(R.id.rl_content), 564, 426);
		ViewUtils.setSize(btnOK, 326, 84);
		ViewUtils.setHeight(view.findViewById(R.id.rl_dialog_top), 88);
		ViewUtils.setTextSize(view.findViewById(R.id.tv_title), 40);
		ViewUtils.setSize(view.findViewById(R.id.rl_input), 540, 100);
		ViewUtils.setMarginRight(view.findViewById(R.id.rl_input), 16);
		ViewUtils.setMarginBottom(btnOK, 40);
		
		ViewUtils.setTextSize(etContent, 34);
		ViewUtils.setTextSize(tvCost, 28);
		ViewUtils.setTextSize(view.findViewById(R.id.tv_cost_label), 28);
		ViewUtils.setTextSize(btnOK, 30);
		
		btnOK.setOnClickListener(this);
		
		alertDialog = builder.create();
	}
	
	
	public void show() {
		// TODO Auto-generated method stub
		alertDialog.setView(((Activity)(context)).getLayoutInflater().inflate(R.layout.recharge_dialog, null));
		alertDialog.show();
		alertDialog.getWindow().setContentView(view);
	}
	
	public void dismiss() {
		if(alertDialog!=null){
			try{
				alertDialog.dismiss();
			}catch(Exception e){
				e.printStackTrace();
			}
		}
	}
	
	public String getMoney() {
		return tvCost.getText().toString();
	}
	

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.btn_ok:
			if (okClickListener != null) {
				okClickListener.onClick(alertDialog, 0);
			}
			
			break;

		default:
			break;
		}
	}

}
