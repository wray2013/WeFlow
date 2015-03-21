package com.etoc.weflow.dialog;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.TextView;

import com.etoc.weflow.R;
import com.etoc.weflow.utils.StringUtils;
import com.etoc.weflow.utils.ViewUtils;

public class ExchangeFlowDialog implements OnClickListener {
	Context context;
	AlertDialog alertDialog;
	private LayoutInflater inflater;
	private AlertDialog.Builder builder;
	private TextView btnOK;
	private TextView btnCancle;
	private View view;
	private EditText etContent;
	private DialogInterface.OnClickListener okClickListener;
	
	
	public ExchangeFlowDialog(Context context,DialogInterface.OnClickListener listener) {
		okClickListener = listener;
		this.context = context;
		init(context);
	}
	
	private void init(Context context) {
		builder = new AlertDialog.Builder(context); 
		inflater=LayoutInflater.from(context);
		view=inflater.inflate(R.layout.recharge_dialog, null);
		
		etContent = (EditText) view.findViewById(R.id.et_content);
		
		btnOK = (TextView) view.findViewById(R.id.tv_confirm);
		btnCancle = (TextView) view.findViewById(R.id.tv_cancle);
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
					btnOK.setEnabled(false);
				} else {
					btnOK.setEnabled(true);
				}
			}
		});
		
		ViewUtils.setSize(view.findViewById(R.id.rl_content), 658, 280);
		ViewUtils.setHeight(etContent, 142);
		
		ViewUtils.setTextSize(etContent, 40);
		ViewUtils.setTextSize(btnOK, 36);
		ViewUtils.setTextSize(btnCancle, 36);
		
		btnOK.setOnClickListener(this);
		btnCancle.setOnClickListener(this);
		
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
	

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.tv_confirm:
			if (okClickListener != null) {
				okClickListener.onClick(alertDialog, 0);
			}
			break;
		case R.id.tv_cancle:
			dismiss();
			break;

		default:
			break;
		}
	}

}
