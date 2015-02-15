package cn.zipper.framwork.canvas;

import android.graphics.Bitmap;
import android.graphics.Matrix;

public final class Graphics {
	
	
	public static Matrix matrix = new Matrix();
	
	/**
	 * ��ֹ����;
	 */
	private Graphics(){
	}
	
	/**
	 * �õ�int�е�͸��ֵ;
	 * @param pixcel
	 * @return
	 */
	public static int getAlphaComponent(int pixcel) {
		return (pixcel >> 24) & 0xff;
	}
	
	
	/**
	 * �õ�int�еĺ�ɫֵ;
	 * @param pixcel
	 * @return
	 */
	public static int getRedComponent(int pixcel)  {
		return (pixcel >> 16) & 0xff;
    }
	
	/**
	 * �õ�int�е���ɫֵ;
	 * @param pixcel
	 * @return
	 */
	public static int getGreenComponent(int pixcel){
		return (pixcel >> 8) & 0xff;
    }
	
	/**
	 * �õ�int�е�6ɫֵ;
	 * @param pixcel
	 * @return
	 */
	public static int getBlueComponent(int pixcel) {
		return pixcel & 0xff;
	}
    
	
	
	/**
	 * ���A, R, G, B��ֵ�õ�int�͵�ARGB;
	 * @param alpha
	 * @param red
	 * @param green
	 * @param blue
	 * @return
	 */
	public static int makeColor(int alpha,int red, int green, int blue) {
		if(red < 0){
			red = 0;
		}else if(red > 255){
			red = 255;
		}
		if(green < 0){
			green = 0;
		}else if(green > 255) {
			green = 255;
		}
		if(blue < 0){
			blue = 0;
		}else if(blue > 255) {
			blue = 255;
		}
		if(alpha < 0){
			alpha = 0;
		}else if(alpha> 255) {
			alpha = 255;
		}
		return (alpha << 24) |(red << 16) | (green << 8) | blue;
    }
	
	/**
	 * ���A, R, G, B��ֵ�õ�int�͵�ARGB;(�������)
	 * @param alpha
	 * @param red
	 * @param green
	 * @param blue
	 * @return
	 */
	public static int makeColorIgnoreOverflow(int alpha,int red, int green, int blue) {
		return (alpha << 24) |(red << 16) | (green << 8) | blue;
    }
	
	
	/**
	 * ���ͼƬ;
	 * @param bitmap
	 * @param newWidth
	 * @param newHeight
	 * @return
	 */
	public static Bitmap resize(Bitmap bitmap, float newWidth, float newHeight, boolean filter){
		Bitmap resizedBitmap = null;
		if(bitmap != null && newWidth > 0 && newHeight > 0){
			int width = bitmap.getWidth();
			int height = bitmap.getHeight();
			float scaleWidth = newWidth / width;
			float scaleHeight = newHeight / height;
			matrix.reset();
			matrix.setScale(scaleWidth, scaleHeight);
			resizedBitmap = Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, filter);
		}
		return resizedBitmap;
	}
	
	/**
	 * 
	 * @param bitmap: ԭʼͼƬ;
	 * @param rotate: ��ת�Ƕ�;
	 * @param filter: �Ƿ񿹾��;
	 * @return : ��ת�����ͼƬ����(ԭbitmap����ı�);
	 */
	public static Bitmap rotate(Bitmap bitmap, float rotate, boolean filter){
		Bitmap resizedBitmap = null;
		if(bitmap != null){
			matrix.reset();
			matrix.setRotate(rotate);
			resizedBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, filter);
		}
		return resizedBitmap;
	}
	
	
	

}
