package cn.zipper.framwork.canvas;

import java.util.Vector;

import android.graphics.Bitmap;
import android.graphics.RectF;

public class ZRect {
	
	private RectF rect;
	
	
	public ZRect(){
		init();
	}
	
	public ZRect(float x, float y, float width, float height){
		init();
		rect.set(x, y, x + width, y + height);
	}
	
	private void init(){
		rect = new RectF();
	}
	
	public ZRect setX(float x){
		float w = rect.right - rect.left;
		rect.left = x;
		rect.right = rect.left + w;
		return this;
	}
	
	public ZRect setY(float y){
		float h = rect.bottom - rect.top;
		rect.top = y;
		rect.bottom = rect.top + h;
		return this;
	}
	
	public ZRect setWidth(float width){
		rect.right = rect.left + width;
		return this;
	}
	
	public ZRect setHeight(float height){
		rect.bottom = rect.top + height;
		return this;
	}
	
	public float getX(){
		return rect.left;
	}
	
	public float getY(){
		return rect.top;
	}
	
	public float getWidth(){
		return rect.right - rect.left;
	}
	
	public float getHeight(){
		return rect.bottom - rect.top;
	}
	
	public float getRightX(){
		return rect.right;
	}
	
	public float getBottomY(){
		return rect.bottom;
	}
	
	public float getCenterX(){
		return rect.centerX();
	}
	
	public float getCenterY(){
		return rect.centerY();
	}
	
	
	/**
	 * ���ĳ����Ƿ��ڱ�������;
	 * @param x
	 * @param y
	 * @return
	 */
	public boolean contains(float x, float y){
		return rect.contains(x, y);
	}
	
	
	/**
	 * ���ָ���ľ����Ƿ��ڱ�������(�������ȫ�ص�Ҳ����true);
	 * @param rect
	 * @return
	 */
	public boolean contains(ZRect rect){
		return this.rect.contains(rect.getRectObject());
	}
	
	
	/**
	 * ���}������Ƿ��ཻ;
	 */
	public boolean intersects(ZRect rect){
		RectF temp = rect.getRectObject();
		return this.rect.intersects(temp.left, temp.top, temp.right, temp.bottom);
	}
	
	
	public RectF getRectObject(){
		return rect;
	}
	
	public ZRect getCopy(){
		return new ZRect(getX(), getY(), getWidth(), getHeight());
	}
	
	/**
	 * ��ָ���ľ��ο������, ʹ�����ε�������ָ���ľ���һ��;
	 * @param rect
	 */
	public void copyDataFrom(ZRect rect){
		setX(rect.getX()).setY(rect.getY()).setWidth(rect.getWidth()).setHeight(rect.getHeight());
	}
	
	
	/**
	 * ��rect�ſ��Լ�;
	 * @param rect
	 */
	public void expandBy(ZRect rect){
		if(rect.getX() < getX()){
			setX(rect.getX());
		}
		if(rect.getY() < getY()){
			setY(rect.getY());
		}
		if(rect.getRightX() > getRightX()){
			this.rect.right = rect.getRectObject().right;
		}
		if(rect.getBottomY() > getBottomY()){
			this.rect.bottom = rect.getRectObject().bottom;
		}
	}
	
	public void expandBy(ZRect[] rects){
		for(int i=0; i<rects.length; i++){
			expandBy(rects[i]);
		}
	}
	
	public void expandBy(Vector<ZRect> rects){
		for(int i=0; i<rects.size(); i++){
			expandBy(rects.elementAt(i));
		}
	}
	
	

}
