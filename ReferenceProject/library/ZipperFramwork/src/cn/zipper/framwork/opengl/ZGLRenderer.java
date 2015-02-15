package cn.zipper.framwork.opengl;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.opengl.GLSurfaceView.Renderer;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import cn.zipper.framwork.R;
import cn.zipper.framwork.core.ZLog;


public final class ZGLRenderer implements Renderer, OnTouchListener, Callback {
	
	public static final int ACTION_RESUME = 0;
	public static final int ACTION_PAUSE = 1;
	public static final int ACTION_STOP = 2;
	
	private ZGLModleInterface zModleInterface;
	private GL10 gl;
	private Handler handler;
	private int screenWidth;
	private int screenHeight;
	
	public ZGLRenderer() {
		handler = new Handler(this);
	}
	
	public void setZGLModleInterface(ZGLModleInterface zModleInterface) {
		this.zModleInterface = zModleInterface;
	}
	
	public GL10 getGL() {
		return gl;
	}
	
	public int getScreenWidth() {
		return screenWidth;
	}
	
	public int getScreenHeight() {
		return screenHeight;
	}
	
	public void onStop() {
		if (zModleInterface != null) {
			zModleInterface.onDestroy(gl);
		}
		handler.sendEmptyMessage(ACTION_STOP);
//		if (ZTextureManager.getInstance() != null) {
//			ZTextureManager.getInstance().deleteAllTextures(true);
//		}
	}
	
	public void onPause() {
		handler.sendEmptyMessage(ACTION_PAUSE);
//		if (ZTextureManager.getInstance() != null) {
//			ZTextureManager.getInstance().deleteAllTextures(false);
//		}
	}
	
	public void onResume() {
		handler.sendEmptyMessage(ACTION_RESUME);
//		if (ZTextureManager.getInstance() != null) {
//			ZTextureManager.getInstance().reloadAllTextures();
//		}
	}
	
	@Override
	public void onDrawFrame(GL10 gl) {
		if (zModleInterface != null) {
			zModleInterface.onLogic(gl);
			zModleInterface.onDraw(gl);
		}
	}

	@Override
	public void onSurfaceChanged(GL10 gl, int width, int height) {
		ZLog.e();
		this.screenWidth = width;
		this.screenHeight = height;
		if (zModleInterface != null) {
			zModleInterface.onInit(gl, width, height);
		}
	}

	@Override
	public void onSurfaceCreated(GL10 gl, EGLConfig config) { // EGLConfig************************#&#&#&#&&#####
		ZLog.e();
		this.gl = gl;
		ZTextureManager.createInstance(gl);
		ZTextureManager.getInstance().createTexture(R.drawable.z_default_texture);
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		if (zModleInterface != null) {
			zModleInterface.onTouch(event);
		}
		return true;
	}

	@Override
	public boolean handleMessage(Message message) {
		if (ZTextureManager.getInstance() != null) {
			switch (message.what) {
			case ACTION_RESUME:
				ZTextureManager.getInstance().reloadAllTextures();
				break;
				
			case ACTION_PAUSE:
				ZTextureManager.getInstance().deleteAllTextures(true);
				break;
				
			case ACTION_STOP:
				ZTextureManager.getInstance().deleteAllTextures(false);
				break;
			}
		}
		
		return false;
	}

}
