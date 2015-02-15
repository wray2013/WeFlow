package cn.zipper.framwork.canvas;

import android.graphics.Bitmap;
import android.util.Log;

public class PixelsBitmap {
	
	private Bitmap bitmap;
	private int[] pixels;
	private int width;
	private int height;
	
	/**
	 * ����bitmap����ݿ���ʹ��System.arraycopy();
	 * @param bitmap
	 * @param cleanBitmap: ��ʼ��int[]�Ϳ�ߺ�,�Ƿ����bitmap�����Խ�ʡ�ڴ�;
	 */
	public PixelsBitmap(Bitmap bitmap, boolean cleanBitmap){
		if(bitmap != null){
			this.bitmap = bitmap;
			width = this.bitmap.getWidth();
			height = this.bitmap.getHeight();
			pixels = new int[width * height];
			this.bitmap.getPixels(pixels, 0, width, 0, 0, width, height);
			if(cleanBitmap){
				this.bitmap = null;
			}
		}else{
			Log.e("PixelsBitmap.PixelsBitmap()", "reason: bitmap is null !");
		}
	}
	
	public Bitmap bitmap(){
		return bitmap;
	}
	
	public int width(){
		return width;
	}
	
	public int height(){
		return height;
	}
	
	public int[] pixels(){
		return pixels;
	}
	
	public boolean isLogicalIndex(int index){
		return index >=0 && index < pixels.length;
	}
	
	public int pixel(int x, int y){
		int pixel = 0x00000000;
		int index = y * width + x;
		if(isLogicalIndex(index)){
			pixel = pixels[index];
		}
		return pixel;
	}
	
	/**
	 * ʹ��ָ����PixelsBitmap����4�ڿձ�����;
	 * (�ڿշ�������)չ�ɽӿ�)
	 * @param mask
	 * @param x
	 * @param y
	 */
	public void mask(PixelsBitmap mask, int x, int y){
		if(mask != null){
			for(int i=0; i<mask.height(); i++){
				for(int j=0; j<mask.width(); j++){
					if(isLogicalIndex((y+i) * width + x + j) 
							&& Graphics.getAlphaComponent(mask.pixels()[i * mask.width() + j]) == 0xff){
						pixels()[(y+i) * width + x + j] = 0x00ffffff;//�ڿ�;(�ڿշ�������)չ�ɽӿ�)
					}
				}
			}
		}
	}

}
