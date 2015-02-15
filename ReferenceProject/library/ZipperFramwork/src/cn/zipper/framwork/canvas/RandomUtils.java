package cn.zipper.framwork.canvas;

import java.util.Random;

public final class RandomUtils {
	
	private static Random random = new Random();
	
	private RandomUtils(){
	}
	
	/**
	 * �õ����int;
	 * @return
	 */
	public static int rand(){
		return random.nextInt();
	}
	
	
	/**
	 * ��ɴ� start �� end ���������. (�� start �� end)
	 * 
	 * start ӦС�ڵ��� end, ���������Զ������ǵ�ֵ�Ե�.
	 * @param start
	 * @param end
	 * @return
	 */
	public static int rand(int start, int end){
		if (start > end){
			int temp = start;
			start = end;
			end = temp;
		}
		return Math.abs(random.nextInt() % (end - start + 1)) + start;
	}
	
	
	/**
	 * ���ö��. ��ָ���� int[] �����ѡ��һ��λ��, ���������λ�ö�Ӧ����ֵ.
	 * @param menu
	 * @return
	 */
	public static int rand(int[] menu) {
		return menu[rand(0, menu.length - 1)];
	}

	
	/**
	 * ���. �� start �� end ֮��������, ����ֵ >= sub ʱ, ������, ���򷵻ؼ�.
	 * @param start
	 * @param sub: ��ֵ�������ڵ��ڷ�ֵʱ������
	 * @param end
	 * @return
	 */
	public static boolean rand(int start, int sub, int end) {
		return rand(start, end) >= sub;
	}
	
}
