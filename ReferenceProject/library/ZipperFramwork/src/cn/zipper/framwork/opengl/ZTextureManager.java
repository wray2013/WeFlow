package cn.zipper.framwork.opengl;

import java.util.HashMap;
import java.util.Set;

import javax.microedition.khronos.opengles.GL10;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLUtils;
import cn.zipper.framwork.R;
import cn.zipper.framwork.core.ZApplication;
import cn.zipper.framwork.core.ZLog;


public final class ZTextureManager {
	
	private static ZTextureManager zTextureManager;
	
	private GL10 gl;
	private HashMap<Integer, ZTexture> textures;
	private int[] tempIntArray;
	
	private ZTextureManager() {
		textures = new HashMap<Integer, ZTexture>();
		tempIntArray = new int[1];
	}
	
	public static void createInstance(GL10 gl) {
		zTextureManager = new ZTextureManager();
		zTextureManager.gl = gl;
	}
	
	public static ZTextureManager getInstance() {
		return zTextureManager;
	}
	
	public ZTexture createTexture(int resourceID) {
		ZTexture zTexture = null;
		if (textures.containsKey(resourceID)) {
			zTexture = getTexture(resourceID);
			if (!zTexture.isAlive) {
				zTexture.id = createGLTexture(resourceID).id;
				zTexture.isAlive = true;
			}
		} else {
			zTexture = createGLTexture(resourceID);
			textures.put(resourceID, zTexture);
		}
		return zTexture;
	}
	
	public void deleteTexture(int resourceID, boolean softDelete) {
		if (textures.containsKey(resourceID)) {
			tempIntArray[0] = textures.get(resourceID).id;
			gl.glDeleteTextures(1, tempIntArray, 0);
			
			if (!softDelete) {
				textures.remove(resourceID);
			} else {
				getTexture(resourceID).isAlive = false;
			}
		}
	}
	
	public void deleteAllTextures(boolean softDelete) {
		synchronized (textures) {
			Set<Integer> keys = textures.keySet();
			for (Integer id : keys) {
				deleteTexture(id, softDelete);
			}
		}
	}
	
	public void reloadAllTextures() {
		Set<Integer> keys = textures.keySet();
		for (Integer id : keys) {
			createTexture(id);
		}
	}
	
	public ZTexture getTexture(int resourceID) {
		ZTexture zTexture = textures.get(resourceID);
		if (zTexture == null || zTexture.id == 0) {
			zTexture = textures.get(R.drawable.z_default_texture);
		}
		return zTexture;
	}
	
	private ZTexture createGLTexture(int resourceID) {
		ZTexture zTexture = null;
		try {
			Bitmap bitmap = BitmapFactory.decodeResource(ZApplication.getInstance().getResources(), resourceID);
			gl.glGenTextures(1, tempIntArray, 0);
	    	gl.glBindTexture(GL10.GL_TEXTURE_2D, tempIntArray[0]);
	    	gl.glTexParameterx(
	    			GL10.GL_TEXTURE_2D, 
	    			GL10.GL_TEXTURE_MAG_FILTER,  
	    		    GL10.GL_LINEAR);
	    	gl.glTexParameterx(
	    			GL10.GL_TEXTURE_2D, 
	    			GL10.GL_TEXTURE_MIN_FILTER,  
	    		    GL10.GL_LINEAR);
	    	gl.glTexParameterf(
	    			GL10.GL_TEXTURE_2D, 
	    			GL10.GL_TEXTURE_WRAP_S, 
	    			GL10.GL_CLAMP_TO_EDGE);
	    	gl.glTexParameterf(
	    			GL10.GL_TEXTURE_2D, 
	    			GL10.GL_TEXTURE_WRAP_T, 
	    			GL10.GL_CLAMP_TO_EDGE);
	    	GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0, bitmap, 0);
	    	zTexture = new ZTexture();
	    	zTexture.id = tempIntArray[0];
	    	zTexture.size.width = bitmap.getWidth();
	    	zTexture.size.height = bitmap.getHeight();
	    	bitmap.recycle();
	    	bitmap = null;
	    	
		} catch (Exception e) {
			ZLog.e("createGLTexture()::ERROR::resourceID is error : " + resourceID);
			zTexture = getTexture(R.drawable.z_default_texture);
			e.printStackTrace();
		}
		
    	return zTexture;
	}
	
	public static final class ZTexture {
		private boolean isAlive;
		private int id;
		private ZSize2D size;
		
		private ZTexture(){
			isAlive = true;
			size = new ZSize2D();
		}
		
		public int getID() {
			return id;
		}
		
		public ZSize2D getZSize2D() {
			return size;
		}
	}
	
	
}
