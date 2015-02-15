package cn.zipper.framwork.opengl;

public class ZVector3D {
	
	public float x;
	public float y;
	public float z;
	
	public ZVector3D() {
		
	}
	
	public ZVector3D(float x, float y, float z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	public void copyFrom(ZVector3D zVector3D) {
		this.x = zVector3D.x;
		this.y = zVector3D.y;
		this.z = zVector3D.z;
	}
	
	public ZVector3D getCopy() {
		return new ZVector3D(x, y, z);
	}

}
