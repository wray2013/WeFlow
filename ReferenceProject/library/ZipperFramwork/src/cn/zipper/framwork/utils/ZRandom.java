package cn.zipper.framwork.utils;

import java.util.Random;

import cn.zipper.framwork.core.ZLog;

public final class ZRandom {
	
	private static Random random = new Random();
	
	private ZRandom(){
	}
	
	public static int randomGet(int[] menu) {
		return menu[nextInt(menu.length)];
	}
	
	public static float randomGet(float[] menu) {
		return menu[nextInt(menu.length)];
	}
	
	public static int nextInt() {
		return random.nextInt();
	}
	
	public static int nextInt(int range) {
		return random.nextInt(range);
	}
	
	/**
	 * [start, end] 闭区间;
	 * @param start
	 * @param end
	 * @return
	 */
	public static int nextInt(int start, int end) {
		if (start > end) {
			int temp = start;
			start = end;
			end = temp;
			ZLog.alert();
			ZLog.e("Auto swap , caused by : start > end !");
		}
		return random.nextInt(end - start + 1) + start;
	}
	
	public static float nextFloat() {
		return random.nextFloat();
	}
	
	public static float nextFloat(int range) {
		return random.nextFloat() * range;
	}
	
	public static float nextFloat(float range) {
		return random.nextFloat() * range;
	}
	
	public static double nextDouble() {
		return random.nextDouble();
	}
	
	public static double nextDouble(int range) {
		return random.nextDouble() * range;
	}
	
	public static double nextDouble(float range) {
		return random.nextDouble() * range;
	}
	
	public static double nextLong() {
		return random.nextLong();
	}
	
	public static boolean nextBoolean() {
		return random.nextBoolean();
	}
	
	public static boolean nextBoolean(int start, int sub, int end) {
		return nextInt(start, end) >= sub;
	}
	
	public static ZBooleanBox nextZBooleanBox() {
		ZBooleanBox booleanBox = new ZBooleanBox();
		int r = random.nextInt(3);
		switch (r) {
		case 0:
			booleanBox.setStateToNull();
			break;
			
		case 1:
			booleanBox.setStateToFalse();
			break;
					
		case 2:
			booleanBox.setStateToTrue();
			break;
		}
		return booleanBox;
	}
	
	
}
