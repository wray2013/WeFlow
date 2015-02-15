package cn.zipper.framwork.core;


public final class ZConfig {
	
	private static boolean isDebug = true;
	private static boolean isUseRealTextureSize;
	
	private ZConfig() {
	}
	
	public static void setDebugState(boolean b) {
		ZConfig.isDebug = b;
	}
	
	public static boolean isDebug() {
		return isDebug;
	}
	
	public static void setTextureSizeState(boolean b) {
		ZConfig.isUseRealTextureSize = b;
	}
	
	public static boolean isUseRealTextureSize() {
		return isUseRealTextureSize;
	}

	

}
