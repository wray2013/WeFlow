package cn.zipper.framwork.utils;

public final class ZByteToSize {
	
	public static final float SIZE_1KB = 1024;
	public static final float SIZE_1MB = 1048576; //1KB * 1024;
	public static final float SIZE_1GB = 1073741824; //1MB * 1024;
	
	public static final String STRING_KB = "KB";
	public static final String STRING_MB = "MB";
	public static final String STRING_GB = "GB";
	
	
	private ZByteToSize() {
	}
	
	public static String smartSize(float bytesCount) {
		String size = null;
		
		if (bytesCount < SIZE_1MB) {
			size = String.format("%.1f", toKB(bytesCount)) + STRING_KB;
		} else if (bytesCount < SIZE_1GB) {
			size = String.format("%.1f", toMB(bytesCount)) + STRING_MB;
		} else {
			size = String.format("%.1f", toGB(bytesCount)) + STRING_GB;
		}
		
		return size;
	}
	
	public static float toKB(float bytesCount) {
		float size = bytesCount/SIZE_1KB;
		return size;
	}
	
	public static float toMB(float bytesCount) {
		float size = bytesCount/SIZE_1MB;
		return size;
	}
	
	public static float toGB(float bytesCount) {
		float size = bytesCount/SIZE_1GB;
		return size;
	}
	
	public static long getKBLength(int size) {
		return (long) SIZE_1KB * size;
	}
	
	public static long getMBLength(int size) {
		return (long) SIZE_1MB * size;
	}
	
	public static long getGBLength(int size) {
		return (long) SIZE_1GB * size;
	}
	
}
