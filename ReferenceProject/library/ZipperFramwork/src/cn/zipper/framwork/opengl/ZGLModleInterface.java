package cn.zipper.framwork.opengl;

import javax.microedition.khronos.opengles.GL10;

import android.view.KeyEvent;
import android.view.MotionEvent;

public interface ZGLModleInterface {
	
	public abstract void onInit(GL10 gl, int width, int height);
	public abstract void onLogic(GL10 gl);
	public abstract void onDraw(GL10 gl);
	public abstract void onDestroy(GL10 gl);
	public abstract void onTouch(MotionEvent event);
	public abstract void onKey(int keyCode, KeyEvent event);
	
}
