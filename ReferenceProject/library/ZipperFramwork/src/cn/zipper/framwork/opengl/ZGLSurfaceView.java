package cn.zipper.framwork.opengl;

import android.opengl.GLSurfaceView;
import cn.zipper.framwork.core.ZApplication;
import cn.zipper.framwork.core.ZLog;

public final class ZGLSurfaceView extends GLSurfaceView {
	
	private ZGLRenderer zRenderer;
	private boolean isStart;
	
	
	public ZGLSurfaceView() {
		super(ZApplication.getInstance());
	}
	
	
	public void start(ZGLModleInterface zModleInterface) {
		if (!isStart) {
			isStart = true;
			
			ZGLRenderer zRenderer = new ZGLRenderer();
			zRenderer.setZGLModleInterface(zModleInterface);
			
			super.setRenderer(zRenderer);
			super.setOnTouchListener(zRenderer);
			
		} else {
			ZLog.e("Already Start!");
		}
	}
	
	public void onStop() {
		ZLog.e();
		if (isStart) {
			isStart = false;
			zRenderer.onStop();
		}
	}
	
	public void onResume(){
		ZLog.e();
		if (isStart) {
			zRenderer.onResume();
			super.onResume();
		}
	}
	
	public void onPause(){
		ZLog.e();
		if (isStart) {
			zRenderer.onPause();
			super.onPause();
		}
	}
	
	

}
