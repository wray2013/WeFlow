package cn.zipper.framwork.core;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Looper;
import android.os.Message;
import android.view.View;
import cn.zipper.framwork.R;

public final class ZDialog{
	
	private static Dialog dialog;
	private static ZViewFinder viewFinder = new ZViewFinder();
	private static Handler handler = new Handler(Looper.getMainLooper());
	private static Runnable runner = new Runnable() {
		
		@Override
		public void run() {
			ZDialog.dismiss();
		}
	};
	
	
	public static Dialog getDialog() {
		return dialog;
	}
	
	public static Dialog show(String title, String message, boolean cancelable, Context context) {
		dismiss();
		
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setTitle(title);
		builder.setMessage(message);
		builder.setCancelable(cancelable);
		dialog = builder.create();
		dialog.show();
		
		viewFinder.set(dialog.getWindow());
		
		return dialog;
	}
	
	public static Dialog show(int id, boolean cancelable, boolean backgroundDim, Context context) {
		View view = ZLayoutInflater.inflate(id, null);
		return show(view, cancelable, backgroundDim, context);
	}
	
	public static Dialog show(View view, boolean cancelable, boolean backgroundDim, Context context) {
		//dismiss();
		try{
			if(dialog==null){
				
				if (backgroundDim) {
					dialog = new Dialog(context, R.style.z_dialog_style_none_background);
				} else {
					dialog = new Dialog(context, R.style.z_dialog_style_none_background_dim);
				}
				
				dialog.setContentView(view);
				dialog.setCancelable(cancelable);
				dialog.show();
				handler.postDelayed(runner, 60000);
				
				viewFinder.set(dialog.getWindow());
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	
		return dialog;
	}
	
	public static Dialog show(View view, boolean cancelable, boolean backgroundDim, Context context, boolean  notimeout) {
		//dismiss();
		try{
			if(dialog==null){
				
				if (backgroundDim) {
					dialog = new Dialog(context, R.style.z_dialog_style_none_background);
				} else {
					dialog = new Dialog(context, R.style.z_dialog_style_none_background_dim);
				}
				
				dialog.setContentView(view);
				dialog.setCancelable(cancelable);
				dialog.show();
				if(!notimeout){
					handler.postDelayed(runner, 60000);
				}
				
				viewFinder.set(dialog.getWindow());
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	
		return dialog;
	}
	
	public static Dialog show(int id, boolean cancelable, boolean backgroundDim, Context context, boolean  notimeout) {
		View view = ZLayoutInflater.inflate(id, null);
		return show(view, cancelable, backgroundDim, context,notimeout);
	}
	
	public static void runAtMainUIThread(Runnable r) {
		handler.post(r);
	}
	
	public static void autoDelayDismissAt(long time) {
		handler.postDelayed(runner, time);
	}
	
	public static void dismiss() {
		try {
			if (dialog != null) {
				dialog.dismiss();
				dialog = null;
			}
			handler.removeCallbacks(runner);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static ZViewFinder getZViewFinder() {
		return viewFinder;
	}

	
}
