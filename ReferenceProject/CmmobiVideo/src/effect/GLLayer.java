package effect;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.R;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.opengl.GLSurfaceView;
import android.opengl.GLSurfaceView.Renderer;
import android.opengl.GLUtils;
import android.util.Log;
import android.view.SurfaceHolder;

/**
 * This class uses OpenGL ES to render the camera's viewfinder image on the
 * screen. Unfortunately I don't know much about OpenGL (ES). The code is mostly
 * copied from some examples. The only interesting stuff happens in the main
 * loop (the run method) and the onPreviewFrame method.
 */
public class GLLayer extends GLSurfaceView implements SurfaceHolder.Callback, Camera.PreviewCallback, Renderer {

	private static final String tag = "GLLayer";
	
	private static final int WORK_MODE_BACK = 0; // 后摄像头;
	private static final int WORK_MODE_FRONT = 1; // 前摄像头;
	private static final int WORK_MODE_PLAY = 2; // 播放视频;
	
	private static final int PHONE_MODEL_SAMSUNG_GALAXY_NEXUS = 1;
	private static final String PHONE_MODEL_SAMSUNG_GALAXY_NEXUS_STRING = "Galaxy Nexus";
	
	private int mPhoneModel;
	private int onDrawFrameCounter = 1;
	private int[] cameraTexture = null;
	
	private int layerWidth = 1;
	private int layerHeight = 1;
	private int mVideoWidth;
	private int mVideoHeight;
	private int mTextureWidth;
	private int mTextureHeight;
	
	private float mTextureCoordScaleX = 1.0f;
	private float mTextureCoordScaleY = 1.0f;
	
	private FloatBuffer cubeBuff;
	private FloatBuffer texBuff;
	
	private boolean needResetTexture;
	private int workMode;
	
	public byte[] glCameraFrame = null; // size of a texture must be a power of 2
	
	
	
	public GLLayer(Context c, int w, int h) {
		super(c);
		
		this.setEGLConfigChooser(8, 8, 8, 8, 0, 0);
		this.getHolder().setFormat(PixelFormat.TRANSLUCENT);
		this.setRenderer(this);
		this.setRenderMode(RENDERMODE_WHEN_DIRTY);
		
		mVideoWidth = w;
		mVideoHeight = h;
		resetVideoSize(mVideoWidth, mVideoHeight);
	}

	/**
	 * 规范化贴图尺寸;
	 * 
	 * @param size
	 * @return
	 */
	private int normalizeTexSize(int size) {
		int texSize = 1;
		while (true) {
			texSize <<= 1;
			if (texSize >= size)
				break;
		}
		return texSize;
	}

	// 该函数在用的时候一定要注意线程安全。
	public void resetVideoSize(int w, int h) {
		needResetTexture = true;
		mVideoWidth = w;
		mVideoHeight = h;
		
		glCameraFrame = new byte[(mVideoWidth * mVideoHeight * 4)];
		Log.e("X", "GLLayer : width = " + mVideoWidth + ", height = " + mVideoHeight);
//		String model = android.os.Build.MODEL;
//		if (model.equalsIgnoreCase(PHONE_MODEL_SAMSUNG_GALAXY_NEXUS_STRING)) {
			mPhoneModel = PHONE_MODEL_SAMSUNG_GALAXY_NEXUS;
			mTextureWidth = normalizeTexSize(mVideoWidth);
			mTextureHeight = normalizeTexSize(mVideoHeight);
			mTextureCoordScaleX = (float) mVideoWidth / mTextureWidth;
			mTextureCoordScaleY = (float) mVideoHeight / mTextureHeight;
			
			switch (workMode) {
			case WORK_MODE_BACK:
				changeBackCoords();
				break;
				
			case WORK_MODE_FRONT:
				changeFrontCoords();
				break;
				
			case WORK_MODE_PLAY:
				changePlayCoords();
				break;
			}
//		}
		computeScaleRectCoord();
	}
	
	private void computeScaleRectCoord() {
		if (workMode == WORK_MODE_PLAY) {
			scaleRectCoord(1.0f, 1.0f);
			
			float videoRate = (float) mVideoWidth / mVideoHeight;
			float screenRate = (float) layerWidth / layerHeight;
			
			Log.e("X", "* : videoRate = " + videoRate + ", screenRate = " + screenRate);
			
			if (videoRate > screenRate) { // 宽约束;
				float newVideoHeight = (float) layerWidth / mVideoWidth * mVideoHeight;
				float scale = (float) newVideoHeight / layerHeight;
				Log.e("X", "A : newVideoHeight = " + newVideoHeight + " = " + scale);
				scaleRectCoord(1.0f, scale);
			} else { // 高约束;
				float newVideoWidth = (float) layerHeight / mVideoHeight * mVideoWidth;
				float scale = (float) newVideoWidth / layerWidth;
				Log.e("X", "B : newVideoWidth = " + newVideoWidth + " = " + scale);
				scaleRectCoord(scale, 1.0f);
			}
		}
	}
	
	public int getVideoWidth() {
		return mVideoWidth;
	}
	
	public int getVideoHeight() {
		return mVideoHeight;
	}
	
	public int getLayerWidth() {
		return getWidth();
	}
	
	public int getLayerHeight() {
		return getHeight();
	}
	
	public void onDrawFrame(GL10 gl) {
		if (needResetTexture) {
			needResetTexture = false;
			bindCameraTexture(gl);
		} else {
			onDrawFrameCounter++;
		}
		
		gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);
		
		switch (mPhoneModel) {
		case PHONE_MODEL_SAMSUNG_GALAXY_NEXUS:
//			gl.glColor4f(0.2f, 1, 0.2f, 1);
			gl.glColor4f(1, 1, 1, 1);
			gl.glTexSubImage2D(GL10.GL_TEXTURE_2D, 0, 0, 0, mVideoWidth, mVideoHeight, GL10.GL_RGBA, GL10.GL_UNSIGNED_BYTE, ByteBuffer.wrap(glCameraFrame));
			break;
			
		default:
			gl.glColor4f(1, 0.2f, 0.2f, 1);
			gl.glTexImage2D(GL10.GL_TEXTURE_2D, 0, GL10.GL_RGBA, mVideoWidth, mVideoHeight, 0, GL10.GL_RGBA, GL10.GL_UNSIGNED_BYTE, ByteBuffer.wrap(glCameraFrame));
			break;
		}
		gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP, 0, 4);
	}

	public void onSurfaceChanged(GL10 gl, int width, int height) {
		Log.i(tag, "onSurfaceChanged:" + width + "||" + height + " :" + this.mVideoWidth + "||" + this.mVideoHeight);
		layerWidth = width;
		layerHeight = height;
		
		computeScaleRectCoord();
		
		gl.glViewport(0, 0, width, height);
		
		gl.glMatrixMode(GL10.GL_PROJECTION);
		gl.glLoadIdentity();
		gl.glFrustumf(-1, 1, -1, 1, 1, 100);
		
		gl.glMatrixMode(GL10.GL_MODELVIEW);
		gl.glLoadIdentity();
		
		gl.glTranslatef(0.0f, 0.0f, -1.0f);
		gl.glNormal3f(0, 0, 0);
		
		bindCameraTexture(gl);
	}
	
	/**
	 * private float camObjCoord[] = new float[] {
			// FRONT
			-1.0f, -1.0f, 0.0f, 
			 1.0f, -1.0f, 0.0f, 
			-1.0f,  1.0f, 0.0f, 
			 1.0f,  1.0f, 0.0f };
	 */
	private void scaleRectCoord(float xValue, float yValue) {
		camObjCoord[0] = -xValue;
		camObjCoord[1] = -yValue;
		camObjCoord[3] =  xValue;
		camObjCoord[4] = -yValue;
		camObjCoord[6] = -xValue;
		camObjCoord[7] =  yValue;
		camObjCoord[9] =  xValue;
		camObjCoord[10] = yValue;
	}

	public void onSurfaceCreated(GL10 gl, EGLConfig config) {
		gl.glHint(GL10.GL_PERSPECTIVE_CORRECTION_HINT, GL10.GL_FASTEST);
		gl.glShadeModel(GL10.GL_SMOOTH);
		gl.glClearColor(0, 0, 0, 0);
		gl.glDisable(GL10.GL_CULL_FACE);
		gl.glEnable(GL10.GL_TEXTURE_2D);
		gl.glEnable(GL10.GL_DEPTH_TEST);
	}

	/**
	 * Generates a texture from the black and white array filled by the
	 * onPreviewFrame method.
	 */
	private void bindCameraTexture(GL10 gl) {
		synchronized (this) {
			if (cameraTexture == null) {
				setupCameraTexture(gl);
			}

			cubeBuff = makeFloatBuffer(camObjCoord);
			texBuff = makeFloatBuffer(camTexCoords);
			
			gl.glVertexPointer(3, GL10.GL_FLOAT, 0, cubeBuff);
			gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
			gl.glTexCoordPointer(2, GL10.GL_FLOAT, 0, texBuff);
			gl.glEnableClientState(GL10.GL_TEXTURE_COORD_ARRAY);

			gl.glTexImage2D(GL10.GL_TEXTURE_2D, 0, GL10.GL_RGBA, mTextureWidth, mTextureHeight, 0, GL10.GL_RGBA, GL10.GL_UNSIGNED_BYTE, null);
		}
	}

	private void setupCameraTexture(GL10 gl) {
		synchronized (this) {
			if (cameraTexture == null) {
				cameraTexture = new int[1];
			}

			gl.glGenTextures(1, cameraTexture, 0);
			gl.glBindTexture(GL10.GL_TEXTURE_2D, cameraTexture[0]);
			gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER, GL10.GL_LINEAR);
			gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MAG_FILTER, GL10.GL_LINEAR);
			gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_S, GL10.GL_CLAMP_TO_EDGE);
			gl.glTexParameterf(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_T, GL10.GL_CLAMP_TO_EDGE);
		}
	}

	private void deleteCameraTexture(GL10 gl) {
		if (cameraTexture != null) {
			gl.glDeleteTextures(1, cameraTexture, 0);
		}
	}

	/**
	 * This method is called if a new image from the camera arrived. The camera
	 * delivers images in a yuv color format. It is converted to a black and
	 * white image with a size of 256x256 pixels (only a fraction of the
	 * resulting image is used). Afterwards Rendering the frame (in the main
	 * loop thread) is started by setting the newFrameLock to true.
	 */
	public void onPreviewFrame(byte[] yuvs, Camera camera) {
	}

	FloatBuffer makeFloatBuffer(float[] arr) {
		ByteBuffer bb = ByteBuffer.allocateDirect(arr.length * 4);
		bb.order(ByteOrder.nativeOrder());
		FloatBuffer fb = bb.asFloatBuffer();
		fb.put(arr);
		fb.position(0);
		return fb;
	}

	private float camObjCoord[] = new float[] {
			// FRONT
			-1.0f, -1.0f, 0.0f, 
			 1.0f, -1.0f, 0.0f, 
			-1.0f,  1.0f, 0.0f, 
			 1.0f,  1.0f, 0.0f };
	private float camTexCoords[] = new float[] {
			// Camera preview
			1.0f, 1.0f, 1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 0.0f };

	// 前置摄像头坐标
	public void changeFrontCoords() {
		workMode = WORK_MODE_FRONT;
		camTexCoords[0] = 0.0f * mTextureCoordScaleX;
		camTexCoords[1] = 1.0f * mTextureCoordScaleY;
		camTexCoords[2] = 0.0f * mTextureCoordScaleX;
		camTexCoords[3] = 0.0f * mTextureCoordScaleY;
		camTexCoords[4] = 1.0f * mTextureCoordScaleX;
		camTexCoords[5] = 1.0f * mTextureCoordScaleY;
		camTexCoords[6] = 1.0f * mTextureCoordScaleX;
		camTexCoords[7] = 0.0f * mTextureCoordScaleY;
	}

	// 后置摄像头坐标
	public void changeBackCoords() {
		workMode = WORK_MODE_BACK;
		camTexCoords[0] = 1.0f * mTextureCoordScaleX;
		camTexCoords[1] = 1.0f * mTextureCoordScaleY;
		camTexCoords[2] = 1.0f * mTextureCoordScaleX;
		camTexCoords[3] = 0.0f * mTextureCoordScaleY;
		camTexCoords[4] = 0.0f * mTextureCoordScaleX;
		camTexCoords[5] = 1.0f * mTextureCoordScaleY;
		camTexCoords[6] = 0.0f * mTextureCoordScaleX;
		camTexCoords[7] = 0.0f * mTextureCoordScaleY;
	}

	// 播放文件时坐标
	public void changePlayCoords() {
		workMode = WORK_MODE_PLAY;
		camTexCoords[0] = 0.0f * mTextureCoordScaleX;
		camTexCoords[1] = 1.0f * mTextureCoordScaleY;
		camTexCoords[2] = 1.0f * mTextureCoordScaleX;
		camTexCoords[3] = 1.0f * mTextureCoordScaleY;
		camTexCoords[4] = 0.0f * mTextureCoordScaleX;
		camTexCoords[5] = 0.0f * mTextureCoordScaleY;
		camTexCoords[6] = 1.0f * mTextureCoordScaleX;
		camTexCoords[7] = 0.0f * mTextureCoordScaleY;
	}
}