package com.cmmobi.looklook.common.view;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.cmmobi.looklook.R;

/**
 * @author wuxiang
 * @email wuxiang@cmmobi.com
 * @date  2014-1-3
 */
public class XEditDialog extends Dialog implements android.view.View.OnClickListener {

	private static final String TAG=XEditDialog.class.getName();
	
	private InputMethodManager inputMethodManager;
	private XEditDialog(Context context) {
		super(context,R.style.CmmobiDialog);
		init();
	}
	private LayoutInflater inflater;
	private TextView tvTitle;
	private EditText etContent;
	private Button btnCancel;
	private Button btnOk;
	private View view;
	private android.view.View.OnClickListener okClickListener;
	private android.view.View.OnClickListener cancelClickListener;
	private void init(){
		inflater=LayoutInflater.from(getContext());
		view=inflater.inflate(R.layout.x_edit_dialog, null);
		setContentView(view);
		inputMethodManager = ((InputMethodManager)getContext().getSystemService(Activity.INPUT_METHOD_SERVICE));
		getWindow().setWindowAnimations(0);
		WindowManager m = getWindow().getWindowManager();
		Display d = m.getDefaultDisplay();  //为获取屏幕宽、高    
	    LayoutParams p = getWindow().getAttributes();  //获取对话框当前的参数值    
	    p.height = (int) (d.getHeight() * 1.0);   //高度设置为屏幕的1.0   
	    p.width = (int) (d.getWidth() * 1);    //宽度设置为屏幕的1.0  
	    tvTitle=(TextView) view.findViewById(R.id.tv_login_looklook_password_title);
	    etContent=(EditText) view.findViewById(R.id.et_login_looklook_password_content);
	    etContent.setHint(R.string.xeditdialog_hit);
	    etContent.postDelayed(new Runnable() {
			
			@Override
			public void run() {
//			    ((InputMethodManager) etContent.getContext().getSystemService(Activity.INPUT_METHOD_SERVICE)).showSoftInput(etContent,InputMethodManager.SHOW_FORCED);;
			    inputMethodManager.showSoftInput(etContent, 0);
			}
		}, 500);
	    btnOk=(Button) view.findViewById(R.id.btn_login_looklook_password_ok);
	    btnCancel=(Button) view.findViewById(R.id.btn_login_looklook_password_back);
	    btnCancel.setOnClickListener(this);
	    btnOk.setOnClickListener(this);
	    view.findViewById(R.id.btn_login_looklook_clean_edit).setOnClickListener(this);
	}
	
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_login_looklook_password_ok:
			inputMethodManager.hideSoftInputFromWindow(etContent.getWindowToken(),
					InputMethodManager.HIDE_NOT_ALWAYS);
			dismiss();
			v.setTag(etContent.getText().toString());
			if(okClickListener!=null)okClickListener.onClick(v);
			break;
		case R.id.btn_login_looklook_password_back:
			inputMethodManager.hideSoftInputFromWindow(etContent.getWindowToken(),
					InputMethodManager.HIDE_NOT_ALWAYS);
			dismiss();
			v.setTag(etContent.getText().toString());
			if(cancelClickListener!=null)cancelClickListener.onClick(v);
			break;
		case R.id.btn_login_looklook_clean_edit:
			etContent.setText(null);
			break;
		default:
			break;
		}
	}
	
	
	public static class Builder{
		private XEditDialog xEditDialog;
		public Builder(Context context){
			xEditDialog=new XEditDialog(context);
		}
		
		public Builder setTitle(String title){
			xEditDialog.tvTitle.setVisibility(View.VISIBLE);
			xEditDialog.tvTitle.setText(title);
			return this;
		}
		
		public Builder setTitle(int res){
			xEditDialog.tvTitle.setVisibility(View.VISIBLE);
			xEditDialog.tvTitle.setText(res);
			return this;
		}
		
		public Builder setPositiveButton(int res,android.view.View.OnClickListener listener){
			xEditDialog.btnOk.setText(res);
			xEditDialog.okClickListener=listener;
			return this;
		}
		
		public Builder setNegativeButton(int res,android.view.View.OnClickListener listener){
			xEditDialog.btnCancel.setText(res);
			xEditDialog.cancelClickListener=listener;
			return this;
		}
		
		public Builder setPositiveButton(String ok,android.view.View.OnClickListener listener){
			xEditDialog.btnOk.setText(ok);
			xEditDialog.okClickListener=listener;
			return this;
		}
		
		public Builder setNegativeButton(String cancel,android.view.View.OnClickListener listener){
			xEditDialog.btnCancel.setText(cancel);
			xEditDialog.cancelClickListener=listener;
			return this;
		}
		
		public XEditDialog create(){
			return xEditDialog;
		}
		
	}
}
