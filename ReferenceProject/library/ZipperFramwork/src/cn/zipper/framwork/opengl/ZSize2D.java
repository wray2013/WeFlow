package cn.zipper.framwork.opengl;

public class ZSize2D {
	
	public float width;
	public float height;
	
	public ZSize2D(){
		
	}
	
	public ZSize2D(float width, float height) {
		this.width = width;
		this.height = height;
	}
	
	public void copyFrom(ZSize2D zSize2D) {
		this.width = zSize2D.width;
		this.height = zSize2D.height;
	}
	
	public ZSize2D getCopy() {
		return new ZSize2D(width, height);
	}

}
