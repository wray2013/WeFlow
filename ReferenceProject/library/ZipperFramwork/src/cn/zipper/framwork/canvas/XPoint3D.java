package cn.zipper.framwork.canvas;

public class XPoint3D {
	
	private float x;
	private float y;
	private float z;
	
	public XPoint3D(){
		
	}
	
	public XPoint3D moveX(float x){
		this.x += x;
		return this;
	}
	
	public XPoint3D moveY(float y){
		this.y += y;
		return this;
	}
	
	public XPoint3D moveZ(float z){
		this.z += z;
		return this;
	}
	
	
	public XPoint3D setX(float x){
		this.x = x;
		return this;
	}
	
	public XPoint3D setY(float y){
		this.y = y;
		return this;
	}
	
	public XPoint3D setZ(float z){
		this.z = z;
		return this;
	}
	
	public float getX(){
		return x;
	}
	
	public float getY(){
		return y;
	}
	
	public float getZ(){
		return z;
	}
	
}
