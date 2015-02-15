package com.cmmobi.looklook.common.view;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Handler;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.TextView;

import com.cmmobi.looklook.R;

/**
 * @author wuxiang
 * @email wuxiang@cmmobi.com
 * @date  2013-7-18
 */
public class Xdialog extends Dialog implements android.view.View.OnClickListener,AnimationListener {


	private Xdialog(Context context) {
		super(context,R.style.CmmobiDialog);
		init();
	}

	private LayoutInflater inflater;
	private Button btnOK;
	private Button btnCancel;
	private TextView tvTitle;
	private TextView tvContent;
	private View vok;
	private View Vcancel;
	private View view;
	private Animation startAnim;
	private Animation endAnim;
	private Animation loopAnim;
	private void init(){
		inflater=LayoutInflater.from(getContext());
		view=inflater.inflate(R.layout.cmmobi_dialog, null);
		setContentView(view);
		btnOK=(Button) view.findViewById(R.id.btn_ok);
		btnCancel=(Button) view.findViewById(R.id.btn_cancel);
		vok=view.findViewById(R.id.ll_ok);
		vok.setVisibility(View.GONE);
		Vcancel= view.findViewById(R.id.ll_cancel);
		Vcancel.setVisibility(View.GONE);
		tvTitle=(TextView) view.findViewById(R.id.tv_title);
		tvTitle.setVisibility(View.GONE);
		tvContent=(TextView) view.findViewById(R.id.tv_content);
		btnOK.setOnClickListener(this);
		btnCancel.setOnClickListener(this);
		WindowManager m = getWindow().getWindowManager();
		Display d = m.getDefaultDisplay();  //为获取屏幕宽、高    
	    LayoutParams p = getWindow().getAttributes();  //获取对话框当前的参数值    
	    p.height = (int) (d.getHeight() * 1.0);   //高度设置为屏幕的1.0   
	    p.width = (int) (d.getWidth() * 1);    //宽度设置为屏幕的0.8   
	    startAnim=AnimationUtils.loadAnimation(getContext(), R.anim.anim_dialog_fade_in);
	    startAnim.setAnimationListener(new AnimationListener() {
			
			@Override
			public void onAnimationStart(Animation animation) {}
			@Override
			public void onAnimationRepeat(Animation animation) {}
			
			@Override
			public void onAnimationEnd(Animation animation) {
				 view.startAnimation(loopAnim);
			}
		});
	    loopAnim=AnimationUtils.loadAnimation(getContext(), R.anim.anim_dialog_loop);
	    endAnim=AnimationUtils.loadAnimation(getContext(), R.anim.anim_dialog_fade_out);
	    endAnim.setAnimationListener(this);
	    endAnim.setFillAfter(true);
	    view.startAnimation(startAnim);
	    
	    
	}
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_cancel:
			view.clearAnimation();
			view.setTag("cancel");
		    view.startAnimation(endAnim);
			break;
		case R.id.btn_ok:
			view.clearAnimation();
			view.setTag("ok");
		    view.startAnimation(endAnim);
			break;
		default:
			break;
		}
	}
	
	private DialogInterface.OnClickListener okClickListener;
	private DialogInterface.OnClickListener cancelClickListener;
	
	public static class Builder{
		private Xdialog xdialog;
		public Builder(Context context){
			xdialog=new Xdialog(context);
		}
		
		public Builder setTitle(String title){
			xdialog.tvTitle.setVisibility(View.VISIBLE);
			xdialog.tvTitle.setText(title);
			return this;
		}
		
		public Builder setTitle(int res){
			xdialog.tvTitle.setVisibility(View.VISIBLE);
			xdialog.tvTitle.setText(res);
			return this;
		}
		
		public Builder setMessage(String msg){
			xdialog.tvContent.setText(msg);
			return this;
		}
		
		public Builder setMessage(int res){
			xdialog.tvContent.setText(res);
			return this;
		}
		
		public Builder setPositiveButton(int res,DialogInterface.OnClickListener listener){
			xdialog.vok.setVisibility(View.VISIBLE);
			xdialog.btnOK.setText(res);
			xdialog.okClickListener=listener;
			return this;
		}
		
		public Builder setNegativeButton(int res,DialogInterface.OnClickListener listener){
			xdialog.Vcancel.setVisibility(View.VISIBLE);
			xdialog.btnCancel.setText(res);
			xdialog.cancelClickListener=listener;
			return this;
		}
		
		public Builder setPositiveButton(String ok,DialogInterface.OnClickListener listener){
			xdialog.vok.setVisibility(View.VISIBLE);
			xdialog.btnOK.setText(ok);
			xdialog.okClickListener=listener;
			return this;
		}
		
		public Builder setNegativeButton(String cancel,DialogInterface.OnClickListener listener){
			xdialog.Vcancel.setVisibility(View.VISIBLE);
			xdialog.btnCancel.setText(cancel);
			xdialog.cancelClickListener=listener;
			return this;
		}
		
		public Xdialog create(){
			return xdialog;
		}
		
	}
	
	@Override
	public void onAnimationEnd(Animation animation) {
		Handler handler = new Handler();
	    handler.postDelayed(new Runnable() {
	        @Override
	        public void run() {
	        	dismiss();
	        	if("ok".equals(view.getTag())){
	        		if(okClickListener!=null)okClickListener.onClick(Xdialog.this, 0);
	        	}else{
	        		if(cancelClickListener!=null)cancelClickListener.onClick(Xdialog.this, 0);
	        	}
	        }
	    }, 10);
	}
	@Override
	public void onAnimationRepeat(Animation animation) {}
	@Override
	public void onAnimationStart(Animation animation) {}
}
