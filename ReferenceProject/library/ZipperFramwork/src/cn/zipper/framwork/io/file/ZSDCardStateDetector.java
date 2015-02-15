package cn.zipper.framwork.io.file;

import android.os.Environment;

public final class ZSDCardStateDetector {
	
	private ZSDCardStateDetector() {
	}
	
	public static boolean isMounted() {
		return Environment.getExternalStorageState().equalsIgnoreCase(Environment.MEDIA_MOUNTED);
	}

}
