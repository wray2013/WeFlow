package com.cmmobi.sns.utils;

/**
 * ��λ��
 * 
 * @author Crazy24k@gmail.com
 * 
 */
public class Point
{
	public static int STATE_NORMAL = 0; // δѡ��
	public static int STATE_CHECK = 1; // ѡ�� e
	public static int STATE_CHECK_ERROR = 2; // �Ѿ�ѡ��,���������

	public float x;
	public float y;
	public int state = 0;
	public int index = 0;// �±�

	public Point()
	{
		
	}

	public Point(float x, float y)
	{
		this.x = x;
		this.y = y;
	}

}
