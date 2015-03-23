package com.etoc.weflow.dialog;

import com.etoc.weflow.R;
import com.etoc.weflow.utils.DisplayUtil;
import com.etoc.weflow.utils.StringUtils;
import com.etoc.weflow.utils.ViewUtils;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Configuration;
import android.os.Handler;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * @date  2014-11-24
 */

public class Xdialog /*extends AlertDialog*/ implements android.view.View.OnClickListener,AnimationListener {


	public Xdialog(Context context) {
//		super(context,R.style.CmmobiDialog);
		init(context);
	}

	AlertDialog alertDialog;
	private LayoutInflater inflater;
	private Button btnOK;
	private Button btnCancel;
	private TextView tvTitle;
	private TextView tvContent;
	private View view;
	private ImageView ivIcon;
	private TextView tvInfo;
	private LinearLayout llBtns;
	private boolean noDismiss = false;
	
//	private Animation startAnim;
//	private Animation endAnim;
//	private Animation loopAnim;
	
	private AlertDialog.Builder builder;
	
	private void init(Context context){
//		alertDialog = new AlertDialog.Builder(context).create();
		builder = new AlertDialog.Builder(context); 
		inflater=LayoutInflater.from(context);
		view=inflater.inflate(R.layout.xdialog, null);
//		alertDialog.setContentView(view);
//		alertDialog.setView(view);
		
		
		ViewUtils.setWidth((view.findViewById(R.id.ll_content)), 564);
		
		tvInfo = (TextView) view.findViewById(R.id.tv_info);
		tvInfo.setTextSize(DisplayUtil.textGetSizeSp(context, 24));
		ivIcon = (ImageView) view.findViewById(R.id.iv_icon);
		ivIcon.setVisibility(View.GONE);
		
		tvInfo.setVisibility(View.GONE);
		
		btnOK=(Button) view.findViewById(R.id.btn_ok);
		btnCancel=(Button) view.findViewById(R.id.btn_cancel);
		Configuration mConfiguration = context.getResources().getConfiguration(); //获取设置的配置信息
		int size12 = 0;
		int size30 = 0;
		int size20 = 0;
		int size25 = 0;
		int textsize33 = 0;
		int textsize36 = 0;
		int textsize42 = 0;
		if(mConfiguration.orientation == mConfiguration.ORIENTATION_LANDSCAPE){
		//横屏
			size12 = DisplayUtil.getSizeLandscape(context, 12);
			size30 = DisplayUtil.getSizeLandscape(context, 30);
			size20 = DisplayUtil.getSizeLandscape(context, 20);
			size25 = DisplayUtil.getSizeLandscape(context, 25);
			textsize33 = 17;
			textsize36 = 20;
			textsize42 = 24;
			(view.findViewById(R.id.ll_content)).setMinimumHeight(DisplayUtil.getSizeLandscape(context, 372));
		}else if(mConfiguration.orientation == mConfiguration.ORIENTATION_PORTRAIT){
		//竖屏
			size12 = DisplayUtil.getSize(context, 12);
			size30 = DisplayUtil.getSize(context, 30);
			size20 = DisplayUtil.getSize(context, 20);
			size25 = DisplayUtil.getSize(context, 25);
			textsize33 = DisplayUtil.textGetSizeSp(context, 33);
			textsize36 = DisplayUtil.textGetSizeSp(context, 36);
			textsize42 = DisplayUtil.textGetSizeSp(context, 42);
			(view.findViewById(R.id.ll_content)).setMinimumHeight(DisplayUtil.getSize(context, 372));
		}

		
	
		tvInfo.setPadding(0, 0, 0, size25);
		llBtns = (LinearLayout) view.findViewById(R.id.ll_btns);
		llBtns.setPadding(0, size30, 0, size30);
		
		btnOK.setVisibility(View.GONE);
		btnOK.setTextSize(textsize33);
		btnCancel.setVisibility(View.GONE);
		btnCancel.setTextSize(textsize33);
		btnCancel.setOnClickListener(this);
		btnOK.setOnClickListener(this);
		tvTitle=(TextView) view.findViewById(R.id.tv_title);
		tvTitle.setPadding(size12, size20, size12, size20);
		tvTitle.setVisibility(View.GONE);
		tvTitle.setTextSize(textsize42);
		tvContent=(TextView) view.findViewById(R.id.tv_content);
		tvContent.setPadding(size30, size12, size30, size12*2);
		tvContent.setTextSize(textsize42);
	}
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_cancel:
			if(!noDismiss){
				alertDialog.dismiss();
			}
        	if(cancelClickListener!=null)cancelClickListener.onClick(alertDialog/*Xdialog.this*/, 0);
			break;
		case R.id.btn_ok:
			if(!noDismiss){
				alertDialog.dismiss();
			}
			if(okClickListener!=null)okClickListener.onClick(alertDialog/*Xdialog.this*/, 0);
			break;
		default:
			break;
		}
	}
	
	private DialogInterface.OnClickListener okClickListener;
	private DialogInterface.OnClickListener cancelClickListener;

	
    public Xdialog setCancelable(boolean flag) {
    	builder.setCancelable(flag);
        return this;
    }
    
    public Xdialog setNoDismiss(boolean flag) {
    	this.noDismiss = flag;
        return this;
    }
	
	public Xdialog setTitle(String title){
		tvTitle.setVisibility(View.VISIBLE);
		tvTitle.setText(title);
		return this;
	}
	
	public Xdialog setTitle(Spanned title){
		tvTitle.setVisibility(View.VISIBLE);
		tvTitle.setText(title);
		return this;
	}
	
	public Xdialog setMessage(String msg){
		tvContent.setText(msg);
		return this;
	}
	
	public Xdialog setMessage(Spanned msg){
		tvContent.setText(msg);
		return this;
	}
	
	public Xdialog setMessage(SpannableStringBuilder style){
		tvContent.setText(style);
		return this;
	}
	
	public Xdialog setMessage(int res){
		tvContent.setText(res);
		return this;
	}
	
	public Xdialog setPositiveButton(String s,DialogInterface.OnClickListener listener){
		btnOK.setVisibility(View.VISIBLE);
		btnOK.setText(s);
		okClickListener=listener;
		return this;
	}
	
	public Xdialog setNegativeButton(String s, DialogInterface.OnClickListener listener){
		btnCancel.setVisibility(View.VISIBLE);
		btnCancel.setText(s);
		cancelClickListener=listener;
		return this;
	}
	
	
	public Xdialog setInfo(String msg){
		tvInfo.setVisibility(View.VISIBLE);
		tvInfo.setText(msg);
		return this;
	}
	
	public Xdialog showIcon(boolean isShow){
		if(isShow){
			ivIcon.setVisibility(View.VISIBLE);
		}else{
			ivIcon.setVisibility(View.GONE);
		}
		return this;
	}
	
	public Xdialog createX(){
		if(btnCancel.getVisibility()==View.GONE){
			ViewUtils.setMarginRight(btnOK, 0);
			if (StringUtils.isEmpty(btnOK.getText())) {
				btnOK.setText("确定");
			}
		}else{
			ViewUtils.setMarginRight(btnOK, 24);
		}
		alertDialog = builder.create();
		return this;
	}

	
	@Override
	public void onAnimationEnd(Animation animation) {
		Handler handler = new Handler();
	    handler.postDelayed(new Runnable() {
	        @Override
	        public void run() {
	        	alertDialog.dismiss();
	        	if("ok".equals(view.getTag())){
	        		if(okClickListener!=null)okClickListener.onClick(alertDialog/*Xdialog.this*/, 0);
	        	}else{
	        		if(cancelClickListener!=null)cancelClickListener.onClick(alertDialog/*Xdialog.this*/, 0);
	        	}
	        }
	    }, 10);
	}
	@Override
	public void onAnimationRepeat(Animation animation) {}
	@Override
	public void onAnimationStart(Animation animation) {}
	public void dismiss() {
		// TODO Auto-generated method stub
		if(alertDialog!=null){
			try{
				alertDialog.dismiss();
			}catch(Exception e){
				e.printStackTrace();
			}
			
		}

	}
	public void show() {
		// TODO Auto-generated method stub
		alertDialog.show();
		
		alertDialog.setContentView(view);
	}
	public Window getWindow() {
		// TODO Auto-generated method stub
		return alertDialog.getWindow();
	}

	public void setCanceledOnTouchOutside(boolean isCancelable) {
		// TODO Auto-generated method stub
		alertDialog.setCanceledOnTouchOutside(isCancelable);
	}
	
	public boolean isShowing() {
		return alertDialog.isShowing();
	}
}

