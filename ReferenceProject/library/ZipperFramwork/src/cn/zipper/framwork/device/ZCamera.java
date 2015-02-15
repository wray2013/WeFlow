package cn.zipper.framwork.device;

import android.content.pm.PackageManager;
import android.hardware.Camera;

public final class ZCamera {
	
	private ZCamera() {
	}
	
	public static boolean hasCamera() {
		return ZSystemFeature.hasFeature(PackageManager.FEATURE_CAMERA);
	}
	
	public static Camera getCamera() {
		return Camera.open();
	}
	
}
