package cn.zipper.framwork.device;

import cn.zipper.framwork.core.ZApplication;

public class ZSystemFeature {
	
	private ZSystemFeature() {
	}
	
	public static boolean hasFeature(String feature) {
		return ZApplication.getInstance().getPackageManager().hasSystemFeature(feature);
	}
	
	public static String getPhoneModel() {
		return android.os.Build.MODEL;
	}
}
