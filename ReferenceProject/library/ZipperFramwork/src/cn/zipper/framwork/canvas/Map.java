package cn.zipper.framwork.canvas;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.view.KeyEvent;
import cxx.oxy.engine.core.ModuleInterface;

public class Map implements ModuleInterface{
	
	private Scene scene;
	private Bitmap bitmap;
	private int[][] data;
	private int[][] data2;
	private int frameIndex;
	private int blockWidth;//�����
	private int blockHeight;//�����
	private int pixelWidth;//���ؿ�
	private int pixelHeight;//���ظ�
	private int blockSize;
	
	public Map(Scene scene){
		this.scene = scene;
	}
	
	/**
	 * 
	 * @param bitmap
	 * @param data
	 * @param blockSize: �����Ŀ�͸���һ�µ�.
	 */
	public void setMap(Bitmap bitmap, int[][] data, int[][] data2, int blockSize){
		this.bitmap = bitmap;
		this.data = data;
		this.data2 = data2;
		this.blockSize = blockSize;
		if(data != null){
//			for(int i=0; i<data.length; i++){//���Դ���(��4����������ʵ�������ȷ��)
//				for(int j=0; j<data[0].length; j++){
//					System.out.println("data[" + i + "][" + j + "] = " + data[i][j]);
//				}
//			}
			blockWidth = data[0].length;
			blockHeight = data.length;
			pixelWidth = blockWidth * blockSize;
			pixelHeight = blockHeight * blockSize;
		}else{
			blockWidth = 0;
			blockHeight = 0;
			pixelWidth = 0;
			pixelHeight = 0;
		}	
	}
	
	public Bitmap getBitmap(){
		return bitmap;
	}
	
	public int getBlockSize(){
		return blockSize;
	}
	
	public int[][] getData(){
		return data;
	}
	
	/**
	 * 
	 * @param x: ��!!
	 * @param y: ��!!
	 * @return
	 */
	public boolean isLogicalIndex(int x, int y){
		return x >= 0 && x < data[0].length && y >= 0 && y < data.length;
	}
	
	/**
	 * 
	 * @param x: ��
	 * @param y: ��
	 * @return
	 */
	public int getValue(int x, int y){
		if(isLogicalIndex(x, y)){
			return data[y][x];
		}else{
			return 0;
		}
	}
	
	public void setValue(int x, int y, int value){
		data[y][x] = value;
	}
	
	public int getValue2(int x, int y){
		if(isLogicalIndex(x, y)){
			return data2[y][x];
		}else{
			return 0;
		}
	}
	
	public void setValue2(int x, int y, int value){
		data2[y][x] = value;
	}
	
	public int getFrameIndex(){
		return frameIndex;
	}
	
	
	public int getBlockWidth(){
		return blockWidth;
	}
	
	public int getBlockHeight(){
		return blockHeight;
	}
	
	public int getPixelWidth(){
		return pixelWidth;
	}
	
	public int getPixelHeight(){
		return pixelHeight;
	}

	public void init() {
		// TODO Auto-generated method stub
		
	}
	
	public void onDraw(Canvas canvas, Paint paint) {
		// TODO Auto-generated method stub
		
	}
	
	public boolean onKey(int keyCode, KeyEvent keyEvent) {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean onTouch(int event, int x, int y) {
		// TODO Auto-generated method stub
		return false;
	}

	public void onUpdate() {
		// TODO Auto-generated method stub
		frameIndex ++;
	}
	

}
