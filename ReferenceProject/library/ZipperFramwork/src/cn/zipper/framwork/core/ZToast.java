package cn.zipper.framwork.core;

import android.os.Handler;
import android.os.Looper;
import android.view.Gravity;
import android.view.View;
import android.widget.Toast;

public final class ZToast {
	
	private static Toast toast;
	private static ZViewFinder viewFinder = new ZViewFinder();
	private static Handler handler = new Handler(Looper.getMainLooper());
	
	
	private ZToast() {
	}
	
	public static Toast getToast() {
		return toast;
	}

	public static void showLong(String string) {
		show(string, Toast.LENGTH_LONG);
	}
	
	public static void showLong(int id) {
		show(ZApplication.getInstance().getString(id), Toast.LENGTH_LONG);
	}
	
	public static void showShort(String string) {
		show(string, Toast.LENGTH_SHORT);
	}
	
	public static void showShort(int id) {
		show(ZApplication.getInstance().getString(id), Toast.LENGTH_SHORT);
	}
	
	
	public static void showLongAtCenter(View view) {
		show(view, Toast.LENGTH_LONG, Gravity.CENTER, 0, 0);
	}
	
	public static void showLongAtCenter(int id) {
		show(id, Toast.LENGTH_LONG, Gravity.CENTER, 0, 0);
	}
	
	public static void showShortAtCenter(View view) {
		show(view, Toast.LENGTH_SHORT, Gravity.CENTER, 0, 0);
	}
	
	public static void showShortAtCenter(int id) {
		show(id, Toast.LENGTH_SHORT, Gravity.CENTER, 0, 0);
	}
	
	public static void show(int id, int duration, int gravity, int offsetX, int offsetY) {
		View view = ZLayoutInflater.inflate(id, null);
		show(view, duration, gravity, offsetX, offsetY);
	}
	
	public static void show(final View view, final int duration, final int gravity, final int offsetX, final int offsetY) {
		handler.post(new Runnable() {
			
			@Override
			public void run() {
				toast = new Toast(ZApplication.getInstance());
				toast.setGravity(gravity, offsetX, offsetY);
				toast.setDuration(duration);
				toast.setView(view);
				toast.show();
				
				viewFinder.set(view);
			}
		});
	}
	
	public static void show(final String string, final int duration, final int gravity, final int offsetX, final int offsetY) {
		handler.post(new Runnable() {
			
			@Override
			public void run() {
				toast = Toast.makeText(ZApplication.getInstance(), string, duration);
				toast.setGravity(gravity, offsetX, offsetY);
				toast.show();
				
				viewFinder.set(toast.getView());
			}
		});
	}
	
	private static void show(final String string, final int duration) {
		handler.post(new Runnable() {
			
			@Override
			public void run() {
				toast = Toast.makeText(ZApplication.getInstance(), string, duration);
				toast.show();
				
				viewFinder.set(toast.getView());
			}
		});
	}
	
	public static ZViewFinder getZViewFinder() {
		return viewFinder;
	}
}
