package com.cmmobi.sns.utils;

/**
 * 
 * @author Crazy24k@gmail.com
 * 
 */
public class StringUtil
{
	/**
	 * �Ƿ�Ϊ��
	 * @param s
	 * @return
	 */
	public static boolean isNotEmpty(String s)
	{
		return s != null && !"".equals(s.trim());
	}

	/**
	 * �Ƿ�Ϊ��
	 * @param s
	 * @return
	 */
	public static boolean isEmpty(String s)
	{
		return s == null || "".equals(s.trim());
	}
	
	/**
	 * ͨ��{n},��ʽ��.
	 * @param src
	 * @param objects
	 * @return
	 */
	public static String format(String src,Object... objects)
	{
		int k = 0;
		for(Object obj : objects)
		{
			src = src.replace("{" + k + "}", obj.toString());
			k++;
		}
		return src;
	}
}
