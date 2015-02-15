package cn.zipper.framwork.canvas;

import java.util.Vector;

public final class SortUtils {
	
	/**
	 * �Ƚ���ӿ�;
	 * @author Administrator
	 */
	public interface Comparator {
		/**
		 * obj1 �� obj2 С, ���ظ���;
		 * obj1 �� obj2 ��ͬ, ����0;
		 * obj1 �� obj2 ��, ��������;
		 * @param obj1
		 * @param obj2
		 * @return
		 */
		public abstract int compare(Object obj1, Object obj2);
	}
	
	/**
	 * �Ӵ�С����;
	 * @param list
	 * @param c
	 */
	public static void sort_FLTS(Vector<Object> list, Comparator c) {
		for (int i = 0; i < list.size(); i++) {
			for (int j = i; j > 0 && c.compare(list.elementAt(j - 1), list.elementAt(j)) < 0; j--) {
				swap(list, j, j - 1);
			}
		}
	}
	
	/**
	 * ��С��������;
	 * @param list
	 * @param c
	 */
	public static void sort_FSTL(Vector<Object> list, Comparator c) {
		for (int i = 0; i < list.size(); i++) {
			for (int j = i; j > 0 && c.compare(list.elementAt(j - 1), list.elementAt(j)) > 0; j--) {
				swap(list, j, j - 1);
			}
		}
	}

	/**
	 * ����list�е�}�����;
	 * @param list
	 * @param index1
	 * @param index2
	 */
	public static void swap(Vector<Object> list, int index1, int index2) {
		Object temp1 = list.elementAt(index1);
		Object temp2 = list.elementAt(index2);
		list.setElementAt(temp1, index2);
		list.setElementAt(temp2, index1);
	}

}
