package cn.zipper.framwork.opengl;

import java.util.Random;

import javax.microedition.khronos.opengles.GL10;

import cn.zipper.framwork.opengl.ZTextureManager.ZTexture;
import cn.zipper.framwork.utils.TrendGroup;
import cn.zipper.framwork.utils.ZRandom;

public class ZGLObject {
	
	private ZGLRect rect;
	private ZVector2D position;
	private ZVector2D moveSpeed;
	private float angleSpeed = (ZRandom.nextFloat() - 0.5f) * 10;
	private float red = ZRandom.nextFloat(0.5f) + 0.5f;
	private float green = ZRandom.nextFloat(0.5f) + 0.5f;
	private float blue = ZRandom.nextFloat(0.5f) + 0.5f;
	
	private float a = 0.9f;//new Random().nextFloat() + 0.5f;
	private float size = 1.0f + new Random().nextFloat();
	private Random random = new Random();
	private int bitmapID;
	private ZTexture zTexture;
	private float r = 0;
	private TrendGroup trendGroup;
	private TrendGroup moveTrendGroupX;
	private TrendGroup moveTrendGroupY;
	
	
	
	public ZGLObject(int bitmapID) {
		init(bitmapID, 0, 0);
	}
	
	public ZGLObject(int bitmapID, int x, int y) {
		init(bitmapID, x, y);
	}
	
	public void setSize(float size) {
		this.size = size;
	}
	
	private void init(int bitmapID, int x, int y) {
		this.rect = new ZGLRect(28,52);
		this.bitmapID = bitmapID;
		this.position = new ZVector2D(x, y);
		zTexture = ZTextureManager.getInstance().createTexture(bitmapID);
		if(zTexture.getZSize2D().width > 150 || zTexture.getZSize2D().height > 150) {
			rect.setWidth(zTexture.getZSize2D().width/2).setHeight(zTexture.getZSize2D().height/2);
		} else {
			rect.setWidth(zTexture.getZSize2D().width).setHeight(zTexture.getZSize2D().height);
		}
		
		
		trendGroup = new TrendGroup(TrendGroup.MODE_AVERAGE, TrendGroup.LOOP_MODE_FOREVER, null);
		trendGroup.appendNode(0.5f, 100);
		trendGroup.appendNode(1, 100);
		trendGroup.appendNode(1, 100);
		trendGroup.appendNode(0.5f, 100);
		trendGroup.setLastNode(0.5f);
		
		moveTrendGroupX = new TrendGroup(TrendGroup.MODE_AVERAGE, TrendGroup.LOOP_MODE_FOREVER, null);
		moveTrendGroupX.appendNode(0, 400);
		moveTrendGroupX.appendNode(0, 10);
		moveTrendGroupX.appendNode(35, 10);
		moveTrendGroupX.appendNode(20, 10);
		moveTrendGroupX.appendNode(0, 10);
		moveTrendGroupX.appendNode(0, 400);
		moveTrendGroupX.appendNode(-20, 10);
		moveTrendGroupX.appendNode(-35, 10);
		moveTrendGroupX.setLastNode(0);
		
		moveTrendGroupY = new TrendGroup(TrendGroup.MODE_AVERAGE, TrendGroup.LOOP_MODE_FOREVER, null);
		moveTrendGroupY.appendNode(0, 400);
		moveTrendGroupY.appendNode(0, 10);
		moveTrendGroupY.appendNode(35, 10);
		moveTrendGroupY.appendNode(20, 10);
		moveTrendGroupY.appendNode(0, 10);
		moveTrendGroupY.appendNode(0, 400);
		moveTrendGroupY.appendNode(-20, 10);
		moveTrendGroupY.appendNode(-35, 10);
		moveTrendGroupY.setLastNode(0);
		
	}
	
	public void onLogic(){
		
	}
	
	public ZVector2D getPosition() {
		return position;
	}
	
	public void onDraw(GL10 gl){
		
//		gl.glTranslatef(0.04f, 0, 0);
//		float c = 0;
//		for (int i=0; i<100; i++) {
//			c += i;
//			c /= i-1;
//			if (c%5==1){
//				int b = 32324;
//				float cc = c/12*9.22f/18;
//				c += cc;
//			} else {
//				c += 1212.2f;
//			}
//			if (c%5==2){
//				int b = 32324;
//				float cc = c/12*9.22f/18;
//				c += cc;
//			} else {
//				c += 1212.2f;
//			}
//			if (c%5==3){
//				int b = 32324;
//				float cc = c/12*9.22f/18;
//				c += cc;
//			} else {
//				c += 1212.2f;
//			}
//			if (c%5==4){
//				int b = 32324;
//				float cc = c/12*9.22f/18;
//				c += cc;
//			} else {
//				c += 1212.2f;
//			}
//			
//		}
		
		r += angleSpeed;
		gl.glPushMatrix();
		gl.glEnable(GL10.GL_TEXTURE_2D);
		gl.glBindTexture(GL10.GL_TEXTURE_2D, ZTextureManager.getInstance().getTexture(bitmapID).getID());
		gl.glTranslatef(position.x, position.y, 0);
		gl.glRotatef(r, 0, 0, 1);
		float f = trendGroup.runToNextValue();
		gl.glScalef(f, f, 0);
		
//		red += (ZRandom.nextFloat()-0.5f)/10;
//		green += (ZRandom.nextFloat()-0.5f)/10;
//		blue += (ZRandom.nextFloat()-0.5f)/10;
//		gl.glColor4f(red, green, blue, 0.9f);
//		gl.glColor4f(random.nextFloat(), random.nextFloat(), random.nextFloat(), 1);
		
        
        
        gl.glVertexPointer(3, GL10.GL_FLOAT, 0, rect.vertexBuffer);
        gl.glTexCoordPointer(2, GL10.GL_FLOAT, 0, rect.textureBuffer);
      
        gl.glDrawElements(
        		GL10.GL_TRIANGLES, 
        		rect.indexBuffer.limit(), 
        		GL10.GL_UNSIGNED_SHORT, 
        		rect.indexBuffer);
        gl.glPopMatrix();
        
        
	}
	
	public static int makeColorIgnoreOverflow(int alpha,int red, int green, int blue) {
		return (alpha << 24) |(red << 16) | (green << 8) | blue;
    }
	

}
