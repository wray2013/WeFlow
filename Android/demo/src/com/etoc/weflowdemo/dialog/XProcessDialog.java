package com.etoc.weflowdemo.dialog;

import com.etoc.weflowdemo.R;
import com.etoc.weflowdemo.util.ConStant;
import com.etoc.weflowdemo.util.DisplayUtil;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;


@SuppressLint("NewApi") public class XProcessDialog implements Callback{

	private static final int DISSMISS_DIALOG = 0x7468167;
	private LayoutInflater inflater;
	private View view;
	private ImageView ivRotate;
	
	private AlertDialog.Builder builder;
	AlertDialog alertDialog;
	private Context context;
	
	private Handler handler;
	
	public XProcessDialog(Context context) {
		this.context = context;
		builder = new AlertDialog.Builder(context, R.style.CmmobiDialog); 
		alertDialog = builder.create();
//		super(context,R.style.CmmobiDialog);
		// TODO Auto-generated constructor stub
		init();
		
		handler = new Handler(this);
	}
	
	private void init() {
		inflater=LayoutInflater.from(context);
		view=inflater.inflate(R.layout.process_dialog, null);
		
		Window dialogWindow = getWindow();
		
		dialogWindow.setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
		
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        dialogWindow.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL);
        lp.y = -1 * DisplayUtil.getSize(context, 120);
        dialogWindow.setAttributes(lp);
		
		ivRotate = (ImageView) view.findViewById(R.id.iv_process_down);
		
		Animation operatingAnim = AnimationUtils.loadAnimation(context, R.anim.loading_rotate);  
		LinearInterpolator lin = new LinearInterpolator();  
		operatingAnim.setInterpolator(lin); 
		
		ivRotate.startAnimation(operatingAnim);
	}

	private Window getWindow() {
		// TODO Auto-generated method stub
		return alertDialog.getWindow();
	}

	public void show() {
		// TODO Auto-generated method stub
		alertDialog.show();
		alertDialog.setContentView(view);
		handler.removeMessages(DISSMISS_DIALOG);
		handler.sendEmptyMessageDelayed(DISSMISS_DIALOG, ConStant.LOAIND_DISSMISS_DALAY);
	}
	
	public void dismiss() {
		// TODO Auto-generated method stub
		try{
			if(alertDialog!=null){
				alertDialog.dismiss();
			}
			
		}catch(Exception e){
			e.printStackTrace();
		}
		
	}
	
	public boolean isShowing(){
		return 	alertDialog.isShowing();
	}

	public void setCancelable(boolean b) {
		// TODO Auto-generated method stub
		alertDialog.setCancelable(b);
	}

	public void setCanceledOnTouchOutside(boolean b) {
		// TODO Auto-generated method stub
		alertDialog.setCanceledOnTouchOutside(b);
	}

	@Override
	public boolean handleMessage(Message msg) {
		// TODO Auto-generated method stub
		switch(msg.what){
		case DISSMISS_DIALOG:
			dismiss();
			break;
		}
		return false;
	}

}
