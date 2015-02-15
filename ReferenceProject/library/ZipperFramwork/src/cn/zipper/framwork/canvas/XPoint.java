package cn.zipper.framwork.canvas;


public class XPoint {
	
	private float x;
	private float y;
	
	public XPoint(){
		
	}
	
	public XPoint moveX(float x){
		this.x += x;
		return this;
	}
	
	public XPoint moveY(float y){
		this.y += y;
		return this;
	}
	
	
	public XPoint setX(float x){
		this.x = x;
		return this;
	}
	
	public XPoint setY(float y){
		this.y = y;
		return this;
	}
	
	public float getX(){
		return x;
	}
	
	public float getY(){
		return y;
	}
	
	public XPoint getCopy(XPoint point){
		XPoint temp = null;
		temp = new XPoint();
		temp.setX(point.getX()).setY(point.getY());
		return temp;
	}
	
	public void copyDataFrom(XPoint point){
		setX(point.getX()).setY(point.getY());
	}
	
}
