package cn.zipper.framwork.opengl;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

public class ZGLRect {
	
	protected float[] vertices = new float[] {
    		-1.0f, -1.0f, 0.0f,
             1.0f, -1.0f, 0.0f,
             1.0f,  1.0f, 0.0f,
            -1.0f,  1.0f, 0.0f};
	
	protected float[] texture = new float[]{
    		0f, 0f,
    		1f, 0f,
    		1f, 1f,
    		0f, 1f};
	
	protected short[] indices = new short[] {0, 1, 2, 3, 2, 0};
	
	protected float width;
	protected float height;
	protected FloatBuffer vertexBuffer;
	protected FloatBuffer textureBuffer;
	protected ShortBuffer indexBuffer;
	
	
	public ZGLRect() {
		init();
	}
	
	public ZGLRect(float width, float height) {
		init();
		setWidth(width);
		setHeight(height);
	}
	
	private void init() {
		width = 2;
		height = 2;
		
		ByteBuffer byteBuffer = ByteBuffer.allocateDirect(vertices.length * 4);
        byteBuffer.order(ByteOrder.nativeOrder());
        vertexBuffer = byteBuffer.asFloatBuffer();
        vertexBuffer.put(vertices);
        vertexBuffer.flip();
        
        ByteBuffer byteBuffer2 = ByteBuffer.allocateDirect(texture.length * 4);
        byteBuffer2.order(ByteOrder.nativeOrder());
        textureBuffer = byteBuffer2.asFloatBuffer();
        textureBuffer.put(texture);
        textureBuffer.flip();
        
        ByteBuffer indicesBuffer = ByteBuffer.allocateDirect(indices.length * 2);
        indicesBuffer.order(ByteOrder.nativeOrder());
        indexBuffer = indicesBuffer.asShortBuffer();
        indexBuffer.put(indices);
        indexBuffer.flip();
	}
	
	public FloatBuffer getVertexBuffer() {
		return vertexBuffer;
	}
	
	public FloatBuffer getTextureBuffer() {
		return textureBuffer;
	}
	
	public ShortBuffer getIndexBuffer() {
		return indexBuffer;
	}
	
	public ZGLRect setWidth(float width) {
		this.width = width;
		float temp = width/2;
		vertexBuffer.put(0, -temp);
		vertexBuffer.put(3, temp);
		vertexBuffer.put(6, temp);
		vertexBuffer.put(9, -temp);
		return this;
	}
	
	public ZGLRect setHeight(float height) {
		this.height = height;
		float temp = height/2;
		vertexBuffer.put(1, -temp);
		vertexBuffer.put(4, -temp);
		vertexBuffer.put(7, temp);
		vertexBuffer.put(10, temp);
		return this;
	}
	
	public float getWidth() {
		return width;//vertices[3] - vertices[0]; 
	}
	
	public float getHeight() {
		return height;//vertices[10] - vertices[1]; 
	}

}
