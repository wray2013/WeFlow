package com.cmmobi.looklook.common.utils;

import android.app.Activity;
import android.content.Intent;
import android.os.Environment;
import android.view.View;
import android.view.View.OnClickListener;
import cn.zipper.framwork.core.ZDialog;
import cn.zipper.framwork.core.ZLog;
import cn.zipper.framwork.io.file.ZSDCardStateDetector;
import cn.zipper.framwork.utils.ZByteToSize;

import com.cmmobi.looklook.R;
import com.cmmobi.looklook.activity.SettingStorageManagerActivity;

public class LowStorageChecker {
	
	public static interface OnChoseListener {
		public void onYes();
		public void onNo();
	}
	
	private LowStorageChecker() {
	}
	
	public static boolean check(final Activity activity) {
		return check(activity, null);
	}
	
	public static boolean check(Activity activity, OnChoseListener listener) {
		return check(activity, listener, R.string.cancel_shoot);
	}
	
	public static boolean check(final Activity activity, final OnChoseListener listener, int stringId) {
		
		boolean isOK = false;
		
		if (ZSDCardStateDetector.isMounted()) {
			long space = Environment.getExternalStorageDirectory().getFreeSpace();
//			ZLog.e("Free = " + space + " -> " + ZByteToSize.toMB(space));
//			ZLog.e("100MB = " + ZByteToSize.getMBLength(100));
			isOK = space > ZByteToSize.getMBLength(100);
			
			if (!isOK) {
				ZDialog.dismiss();
				ZDialog.show(R.layout.dialog_alert_low_storage_space, false, true, activity);
				ZDialog.getZViewFinder().findTextView(R.id.yes_clean_cache)
						.setOnClickListener(new OnClickListener() {

							@Override
							public void onClick(View v) {
//								activity.finish();
								Intent storagemanage = new Intent(activity, SettingStorageManagerActivity.class);
								activity.startActivity(storagemanage);
								ZDialog.dismiss();
							}
						});
				ZDialog.getZViewFinder().findTextView(R.id.no_cancel).setText(stringId);
				
				ZDialog.getZViewFinder().findTextView(R.id.no_cancel)
						.setOnClickListener(new OnClickListener() {

							@Override
							public void onClick(View arg0) {
								ZDialog.dismiss();
								if (listener != null) {
									listener.onNo();
								}
							}
						});
			}
			
		} else {
			ZDialog.dismiss();
			ZDialog.show(R.layout.dialog_alert_no_sdcard, true, true, activity);
			ZDialog.getZViewFinder().findTextView(R.id.ok)
					.setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View arg0) {
							ZDialog.dismiss();
						}
					});
		}
		
		return isOK;
	}

}
