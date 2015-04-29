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

public class OrderDialog implements OnClickListener {
	Context context;
	AlertDialog alertDialog;
	private LayoutInflater inflater;
	private AlertDialog.Builder builder;
	private TextView btnConfirm;
	private View view;
	private TextView tvContent;
	private DialogInterface.OnClickListener okClickListener;
	private static OrderDialog orderDialog = null;
	
	
	public OrderDialog(Context context,DialogInterface.OnClickListener listener) {
		okClickListener = listener;
		this.context = context;
		init(context);
	}
	
	private void init(Context context) {
		builder = new AlertDialog.Builder(context); 
		inflater=LayoutInflater.from(context);
		view=inflater.inflate(R.layout.order_dialog, null);
		
		tvContent = (TextView) view.findViewById(R.id.tv_desc);
		
		btnConfirm = (TextView) view.findViewById(R.id.tv_confirm);
		
		ViewUtils.setSize(view.findViewById(R.id.rl_content), 658, 428);
		
		ViewUtils.setTextSize(tvContent, 40);
		
		btnConfirm.setOnClickListener(this);
		
		alertDialog = builder.create();
	}
	
	private void setDescText(String desc) {
		if (tvContent != null) {
			tvContent.setText(desc);
		}
	}
	
	
	private void setBackgroundRes(int res) {
		if (view != null) {
			view.setBackgroundResource(res);
		}
	}
	
	public void show() {
		// TODO Auto-generated method stub
		alertDialog.setView(((Activity)(context)).getLayoutInflater().inflate(R.layout.order_dialog, null));
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
			dismiss();
			break;
		default:
			break;
		}
	}
	
	public static void Dialog(Context context,String content) {
		Dialog(context, content, false);
	}
	
	public static void Dialog(Context context,String content,DialogInterface.OnClickListener listener) {
		Dialog(context, content, false,listener);
	}
	
	public static void Dialog(Context context,String content,boolean failed) {
		Dialog(context, content, failed, null);
	}
	
	public static void Dialog(Context context,String content,boolean failed,DialogInterface.OnClickListener listener) {
		orderDialog = new OrderDialog(context, listener);
		if (listener != null) {
			orderDialog.btnConfirm.setText("复制");
		}
		if (failed) {
			orderDialog.setBackgroundRes(R.drawable.bg_order_dialog_fail);
		}
		orderDialog.setDescText(content);
		orderDialog.show();
	}

}
