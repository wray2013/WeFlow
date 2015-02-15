package cn.zipper.framwork.opengl;

public class ZVector2D {
	
	public float x;
	public float y;
	
	public ZVector2D() {
		
	}
	
	public ZVector2D(float x, float y) {
		this.x = x;
		this.y = y;
	}
	
	public void copyFrom(ZVector2D zVector2D) {
		this.x = zVector2D.x;
		this.y = zVector2D.y;
	}
	
	public ZVector2D getCopy() {
		return new ZVector2D(x, y);
	}

}
