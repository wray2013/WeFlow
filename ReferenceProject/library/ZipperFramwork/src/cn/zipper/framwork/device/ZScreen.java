package cn.zipper.framwork.device;

import android.util.DisplayMetrics;
import cn.zipper.framwork.core.ZApplication;

public final class ZScreen {
	
	public static final float CM_PER_INCH = 2.539999918f; // 每英寸对应的厘米数;
	
	private static DisplayMetrics displayMetrics = ZApplication.getInstance().getResources().getDisplayMetrics();
	
	private ZScreen() {
	}
	
	public static DisplayMetrics getDisplayMetrics() {
		return displayMetrics;
	}
	
	public static int getWidth() {
		return displayMetrics.widthPixels;
	}
	
	public static int getHeight() {
		return displayMetrics.heightPixels;
	}
	
	public static float getXDPI() {
		return displayMetrics.xdpi;
	}
	
	public static float getYDPI() {
		return displayMetrics.ydpi;
	}
	
	public static float getDensity() {
		return displayMetrics.density;
	}
	
	public static float getDensityDPI() {
		return displayMetrics.densityDpi;
	}
	
	public static float getScaledDensity() {
		return displayMetrics.scaledDensity;
	}
	
	public static float dipToPixels(float dip) {
		return dip * getDensity();
	}
	
	public static int dipToPixels(int dip) {
		return (int) (dip * getDensity());
	}
	
	public static float pixelsToDip(float pixels) {
		return pixels / getDensity();
	}
	
	public static int pixelsToDip(int pixels) {
		return (int) (pixels / getDensity());
	}
	
	public static float pixelsToInch(float pixels) {
		return pixels / getDensityDPI();
	}
	
	public static int pixelsToInch(int pixels) {
		return (int) (pixels / getDensityDPI());
	}
	
	public static float inchToPixels(float inch) {
		return inch * getDensityDPI();
	}
	
	public static int inchToPixels(int inch) {
		return (int) (inch * getDensityDPI());
	}
	
	/**
	 * 换算: 厘米->英寸;
	 * @param cm: 厘米;
	 * @return
	 */
	public static float cmToInch(float cm) {
		return cm / CM_PER_INCH;
	}
	
	public static int cmToInch(int cm) {
		return (int) (cm / CM_PER_INCH);
	}
	
	public static float cmToPixels(float cm) {
		return inchToPixels(cmToInch((float) cm));
	}
	
	public static int cmToPixels(int cm) {
		return (int) inchToPixels(cmToInch((float) cm));
	}
	
	public static float inchToCm(float inch) {
		return inch * CM_PER_INCH;
	}
	
	public static int inchToCm(int inch) {
		return (int) (inch * CM_PER_INCH);
	}
	
	public static float pixelsToCm(float pixels) {
		return inchToCm(pixelsToInch((float) pixels));
	}
	
	public static int pixelsToCm(int pixels) {
		return (int) inchToCm(pixelsToInch((float) pixels));
	}
	
}
