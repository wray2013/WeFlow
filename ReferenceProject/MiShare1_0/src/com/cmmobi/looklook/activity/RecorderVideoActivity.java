package com.cmmobi.looklook.activity;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.hardware.Camera.CameraInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.cmmobivideo.utils.ContextHolder;
import com.cmmobivideo.utils.EffectBean;
import com.cmmobivideo.utils.EffectList.EffectAdapter;
import com.cmmobivideo.utils.EffectUtils;
import com.cmmobivideo.utils.XUtils;
import com.cmmobivideo.workers.XVideoRecorder;
import com.cmmobivideo.workers.XVideoRecorder.XVideoSurfaceSizeChangeListener;
import com.cmmobivideo.workers.XVideoRecorder.XVideoTakePicture;

import effect.EffectType;
import effect.EffectType.LOGLevel;
import effect.XEffectJniUtils;
import effect.XEffects;
import effect.XMediaRecorderInterface;

public class RecorderVideoActivity extends Activity implements View.OnClickListener {
	private static final String TAG = "ZC_JAVA_RecorderVideoActivity";
//	private static final boolean TEST = false;
//	private static final boolean USE_JNI_TAKE_PICTURE = true;
	private static final boolean USE_CAMERA_VIDEO = true;
//	private static final boolean USE_PREVIEW_PICTURE = true;
	
	
//	private XMediaRecorder mMediaRecorder;
	private XVideoRecorder mXVideoRecorder;
	
	private XEffects mXEffect;
	private final int mStartRecorderId = 100;
	private final int mRotationId = 101;
	private final int mChangeCameraId = 102;
	private final int mStartPreviewId = 103;
	private final int mTakePictureId = 105;
	private final int mChangeEffectId = 106;
	private final int mChangeGuildId = 107;
	private final int mChangeBalanceId = 108;
	private final int mPauseRecorderId = 109;
	
	private final int mSizeChangeId = 110;
	private final int mChangeH2LId = 111;
	private final int mChangeP2VId = 112;
//	private boolean isToStop = false;
	
//	private MyTakePicture mMyTakePicture = null;
	private EffectUtils mEffectUtils = null;
	private List<EffectBean> mEffects ;
	RelativeLayout mBgloyout;
	private EffectType.CamaraMode mCameraMode = EffectType.CamaraMode.DEF_VIDEO_AUTO;
	
	private int mCameraId = CameraInfo.CAMERA_FACING_BACK;
	boolean isStart = false;
	private String mH2LText;
	private String mP2VText;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		ContextHolder.setContext(this);
		
		XEffectJniUtils.getInstance().setLogLevel(LOGLevel.LOG_DEBUG);
		
		mEffectUtils = new EffectUtils(this);
		mEffectUtils.parseXml("effectcfg/effectlist.xml");
		
		
		
		mBgloyout = new RelativeLayout(this);
	    RelativeLayout.LayoutParams fill_params =
	                		new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT,
	                        						ViewGroup.LayoutParams.FILL_PARENT);
	        
	    mBgloyout.setLayoutParams(fill_params);
	    
		LinearLayout btnLayout = new LinearLayout(this);
		btnLayout.setGravity(Gravity.BOTTOM);
        ViewGroup.LayoutParams btnlayout_params =
                		new ViewGroup.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT,
                					ViewGroup.LayoutParams.FILL_PARENT);
        btnLayout.setLayoutParams(btnlayout_params);
        btnLayout.setOrientation(LinearLayout.VERTICAL);
        
        
        
//		RelativeLayout shell = new RelativeLayout(this);
//		shell.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT,LayoutParams.FILL_PARENT));
//		 shell.setBackgroundColor(0xff000099);

		setContentView(mBgloyout);

		// 创建效果对象
		mXEffect = new XEffects();
//		XEffects.XEffect teffect = mXEffect.newEffect(EffectType.KEF_TYPE_NOTHING);
//		teffect.setEffectTag(mEffectUtils.getCurrentEffectTag());
//		mXEffect.addEffectsInFrontWithTag(teffect, -1);
//		XMediaRecorder mediaRecorder = new XMediaRecorder(this, mXEffect,new MyMediaInfo());
		
//		if(USE_CAMERA_VIDEO){
//			mMediaRecorder = new XMediaRecorder(this, mXEffect,new MyMediaInfo(),mVideoMode);
//		}else{
//			mMediaRecorder = new XMediaPicture(this, mXEffect,new MyMediaInfo(),mVideoMode);
//		}
		mXVideoRecorder = new XVideoRecorder(this, mXEffect,new MyMediaInfo(),mCameraMode);
		mXVideoRecorder.setXSurfaceSizeChange(new XVideoSurfaceSizeChangeListener() {
			@Override
			public void onSizeChanged(int left, int top, int right, int bottom) {
				// TODO Auto-generated method stub
				Log.i(TAG, "[onSizeChanged] left:"+left+",top:"+top+",right:"+right+",bottom:"+bottom);
			}
		});
//		mediaRecorder.release();
		
//		ViewGroup.LayoutParams CamLayer_params = new ViewGroup.LayoutParams(1, 1);
//		mMediaRecorder.getCamLayer().setLayoutParams(CamLayer_params);
//		bgloyout.addView(mMediaRecorder.getCamLayer());
//		ViewGroup.LayoutParams CamLayer_params = new ViewGroup.LayoutParams(200, 200);
//		mMediaRecorder.getCamLayer().setLayoutParams(CamLayer_params);
//		bgloyout.addView(mMediaRecorder.getGLLayer());
		mBgloyout.addView(mXVideoRecorder.getXSurfaceView());
		
		ViewGroup.LayoutParams  button_params =
        		new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
        					ViewGroup.LayoutParams.WRAP_CONTENT);

		Button button4 = new Button(this);
		button4.setText("1预览/停止");
		button4.setId(mStartPreviewId);
		button4.setOnClickListener(this);

		btnLayout.addView(button4, button_params);
		
		Button button = new Button(this);
		button.setText("2录制/停止");
		button.setId(mStartRecorderId);
		button.setOnClickListener(this);
		btnLayout.addView(button, button_params);
		
		Button button1 = new Button(this);
		button1.setText("3暂停/恢复");
		button1.setId(mPauseRecorderId);
		button1.setOnClickListener(this);

		btnLayout.addView(button1, button_params);
		
//		Button button2 = new Button(this);
//		button2.setText("旋转方向");
//		button2.setId(mRotationId);
//		button2.setOnClickListener(this);
//		btnLayout.addView(button2, 150, 50);
		
		Button button3 = new Button(this);
		button3.setText("4切换摄像头");
		button3.setId(mChangeCameraId);
		button3.setOnClickListener(this);
		button3.bringToFront();
		btnLayout.addView(button3, button_params);
		
		Button button5 = new Button(this);
		button5.setText("5拍照");
		button5.setId(mTakePictureId);
		button5.setOnClickListener(this);
		button5.bringToFront();
		btnLayout.addView(button5, button_params);
		
//		Button button6 = new Button(this);
//		button6.setText("效果切换");
//		button6.setId(mChangeEffectId);
//		button6.setOnClickListener(this);
//		button6.bringToFront();
//		btnLayout.addView(button6, 150, 50);
		
		Button button7 = new Button(this);
		button7.setText("6网格");
		button7.setId(mChangeGuildId);
		button7.setOnClickListener(this);
		button7.bringToFront();
		btnLayout.addView(button7,button_params);
		
		Button button8 = new Button(this);
		button8.setText("7平衡");
		button8.setId(mChangeBalanceId);
		button8.setOnClickListener(this);
		button8.bringToFront();
		btnLayout.addView(button8, button_params);
		
		Button button9 = new Button(this);
		button9.setText("8预览尺寸");
		button9.setId(mSizeChangeId);
		button9.setOnClickListener(this);
		button9.bringToFront();
		btnLayout.addView(button9, button_params);
		
		Button chengH2L = new Button(this);
		chengH2L.setText("9高清/普清");
		chengH2L.setId(mChangeH2LId);
		chengH2L.setOnClickListener(this);
		btnLayout.addView(chengH2L, button_params);
		
		Button chengP2V = new Button(this);
		chengP2V.setText("10拍照/拍摄");
		chengP2V.setId(mChangeP2VId);
		chengP2V.setOnClickListener(this);

		btnLayout.addView(chengP2V, button_params);
		
		
		
		//创建下拉列表
		Spinner effectSpinner = new Spinner(this);
		mEffects = mEffectUtils.getEffects(EffectBean.TYPE_EFFECTS_VIDEO);
		mEffectUtils.removeEffectBean(mEffects,EffectType.KEF_TYPE_MOSAIC);
//		ArrayAdapter effectAdapter = new ArrayAdapter<EffectBean>(this, android.R.layout.simple_list_item_single_choice,effects);
		EffectAdapter effectAdapter = mEffectUtils.createEffectAdapter(this,mEffects,0);
		
//		effectAdapter.setDropDownViewResource(android.R.layout.simple_spinner_item);
		effectSpinner.setAdapter(effectAdapter);
		effectSpinner.setOnItemSelectedListener(new ItemSelectedListener());
		btnLayout.addView(effectSpinner, 150, 80);
		
		
		mBgloyout.addView(btnLayout,button_params);

		Log.i(TAG, "jni version:"+XEffectJniUtils.getInstance().getJniVersion());
		
		mH2LText = "普通模式";
		if(mCameraMode == EffectType.CamaraMode.DEF_PHOTO_HEIGHT
				||mCameraMode == EffectType.CamaraMode.DEF_VIDEO_HEIGHT){
			mH2LText = "高清模式";
		}
		mP2VText = "视频录制";
		if(mCameraMode == EffectType.CamaraMode.DEF_PHOTO_HEIGHT
				||mCameraMode == EffectType.CamaraMode.DEF_PHOTO_COMMON){
			mP2VText = "拍照拍摄";
		}
		setTitle(mH2LText+" "+mP2VText);
		
		
	    try {
	        String[] list = getAssets().list("");
	        for (int i = 0; i < list.length; i++) {
	            Log.i(TAG, list[i]);
	        }
	    } catch (IOException e) {
	        e.printStackTrace();
	    }
	}

	@Override
	protected void onResume() {
		super.onResume();
//		if (mMediaRecorder != null)
//			mMediaRecorder.startPreview();
	}

	@Override
	protected void onPause() {
		super.onPause();
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case mStartPreviewId:
			testPreview();
			break;
		case mStartRecorderId:
			testRecorder();
			break;
		case mPauseRecorderId:
			testRecorderPause();
			break;
		case mRotationId:
//			changeRotation(mMediaRecorder.getGLLayer());
			break;
		case mChangeCameraId:
			mCameraId = mXVideoRecorder.changeCamera();
			break;
		case mTakePictureId:
			Log.i(TAG, "takePicture");
//			if(!mSaveComplete)return;
//			if(mMyTakePicture == null){
//				mMyTakePicture = new MyTakePicture();
//				mMediaRecorder.setPictureCallback(mMyTakePicture);
//			}
//			if(mVideoMode == EffectType.CamaraMode.DEF_VIDEO_COMMON){
//				mMediaRecorder.takePictureJNI(mMyTakePicture);
//			}else{
//				Log.i(TAG, "takePicture22");
//				mMediaRecorder.getCamLayer().getCamera().takePicture(null,null, mMyTakePicture);
//				if(USE_CAMERA_PICTURE){
				SimpleDateFormat format=new SimpleDateFormat("yyyyMMddHHmmss");
		    	String nowTime=format.format(new Date(System.currentTimeMillis()));	
				String filePath = "/sdcard/video/takepic_"+nowTime+".jpg";
				mXVideoRecorder.takePicture(new MyXVideoTakePicture(),filePath);
//				}
//			}
			break;
		case mChangeEffectId:
			//mEffectUtils.changeEffect(mXEffect, EffectType.VIDEO_WIDTH, EffectType.VIDEO_HEIGHT);
//			mEffectUtils.changeMosaicEffect(mXEffect, "effectcfg/video/mosaic.cfg", 10,
//					new Rect(0,0,100,100), 1);
			EffectBean effectBean = new EffectBean(1, EffectBean.TYPE_EFFECTS_VIDEO, EffectType.KEF_TYPE_MOSAIC, "effectcfg/video/mosaic.cfg", "", "");
//			mEffectUtils.changeEffectdWithAssetsFile(mXEffect, "effectcfg/video/mosaic.cfg", 1, EffectType.KEF_TYPE_MOSAIC, 0, 0, 10, new Rect(0,0,100,100));
			mEffectUtils.changeEffectdWithEffectBean(mXEffect, effectBean, 0, 0, 10, new Rect(0,0,100,100));
			
			break;
		case mChangeGuildId:
//			mMediaRecorder.getXSurfaceView().changeGliderLine(!mMediaRecorder.getXSurfaceView().isShowGliderLine());
			mXVideoRecorder.changeGliderLine(!mXVideoRecorder.isShowGliderLine());
			break;
		case mChangeBalanceId:
//			mMediaRecorder.getXSurfaceView().enableBalanceView(!mMediaRecorder.getXSurfaceView().isEnableBalanView());
			mXVideoRecorder.changeBalance(!mXVideoRecorder.isShowBalance());
			break;
		case mSizeChangeId:
			EffectType.PreviewMode previewMode = mXVideoRecorder.getCurrentPreviewMode();
			if(previewMode == EffectType.PreviewMode.V_SHOW_MODLE_16P9){
				previewMode = EffectType.PreviewMode.V_SHOW_MODLE_4P3;
			}else{
				previewMode = EffectType.PreviewMode.V_SHOW_MODLE_16P9;
			}
//			mMediaRecorder.getXSurfaceView().setSurfaceViewMode(previewMode);
			mXVideoRecorder.setCurrentPreviewMode(previewMode);
			break;
		case mChangeH2LId:
			if(mXVideoRecorder != null && mXVideoRecorder.getStatus() == XMediaRecorderInterface.STATUS_UNKNOW){
				mCameraMode = mXVideoRecorder.getCameraMode();
				if(mCameraMode == EffectType.CamaraMode.DEF_VIDEO_HEIGHT){
					mCameraMode = EffectType.CamaraMode.DEF_VIDEO_COMMON;
				}else if(mCameraMode == EffectType.CamaraMode.DEF_VIDEO_COMMON){
					mCameraMode = EffectType.CamaraMode.DEF_VIDEO_HEIGHT;
				}else if(mCameraMode == EffectType.CamaraMode.DEF_PHOTO_COMMON){
					mCameraMode = EffectType.CamaraMode.DEF_PHOTO_HEIGHT;
				}else if(mCameraMode == EffectType.CamaraMode.DEF_PHOTO_HEIGHT){
					mCameraMode = EffectType.CamaraMode.DEF_PHOTO_COMMON;
				}
				mXVideoRecorder.setCameraMode(mCameraMode, mBgloyout);
				mH2LText = "普通模式";
				if(mCameraMode == EffectType.CamaraMode.DEF_PHOTO_HEIGHT
						||mCameraMode == EffectType.CamaraMode.DEF_VIDEO_HEIGHT){
					mH2LText = "高清模式";
				}
				setTitle(mH2LText+" "+mP2VText);
//				mBgloyout.removeView(mMediaRecorder.getXSurfaceView());
//				mMediaRecorder.release();
//				mMediaRecorder = null;
//				mMediaRecorder = new XMediaRecorder(this, mXEffect,new MyMediaInfo(),mVideoMode);
//				if(USE_CAMERA_VIDEO){
//					mMediaRecorder = new XMediaRecorder(this, mXEffect,new MyMediaInfo(),mVideoMode);
//				}else{
//					mMediaRecorder = new XMediaPicture(this, mXEffect,new MyMediaInfo(),mVideoMode);
//				}
				mBgloyout.addView(mXVideoRecorder.getXSurfaceView(),mBgloyout.getChildCount()-1);
//				mBgloyout.re
			}
			break;
		case mChangeP2VId:
			if(mXVideoRecorder != null && mXVideoRecorder.getStatus() == XMediaRecorderInterface.STATUS_UNKNOW){
				mCameraMode = mXVideoRecorder.getCameraMode();
				if(mCameraMode == EffectType.CamaraMode.DEF_VIDEO_HEIGHT
						||mCameraMode == EffectType.CamaraMode.DEF_VIDEO_COMMON_API){
					mCameraMode = EffectType.CamaraMode.DEF_PHOTO_HEIGHT;
				}else if(mCameraMode == EffectType.CamaraMode.DEF_VIDEO_COMMON
						||mCameraMode == EffectType.CamaraMode.DEF_VIDEO_240_240
						||mCameraMode == EffectType.CamaraMode.DEF_VIDEO_AUTO){
					mCameraMode = EffectType.CamaraMode.DEF_PHOTO_COMMON;
				}else if(mCameraMode == EffectType.CamaraMode.DEF_PHOTO_COMMON){
					mCameraMode = EffectType.CamaraMode.DEF_VIDEO_AUTO;
				}else if(mCameraMode == EffectType.CamaraMode.DEF_PHOTO_HEIGHT){
					mCameraMode = EffectType.CamaraMode.DEF_VIDEO_COMMON_API;
				}
				mXVideoRecorder.setCameraMode(mCameraMode, mBgloyout);
				mP2VText = "视频录制";
				if(mCameraMode == EffectType.CamaraMode.DEF_PHOTO_HEIGHT
						||mCameraMode == EffectType.CamaraMode.DEF_PHOTO_COMMON){
					mP2VText = "拍照拍摄";
				}
				setTitle(mH2LText+" "+mP2VText);
//				mBgloyout.removeView(mMediaRecorder.getXSurfaceView());
//				mMediaRecorder.release();
//				mMediaRecorder = null;
//				mMediaRecorder = new XMediaRecorder(this, mXEffect,new MyMediaInfo(),mVideoMode);
//				if(USE_CAMERA_VIDEO){
//					mMediaRecorder = new XMediaRecorder(this, mXEffect,new MyMediaInfo(),mVideoMode);
//				}else{
//					mMediaRecorder = new XMediaPicture(this, mXEffect,new MyMediaInfo(),mVideoMode);
//				}
				mBgloyout.addView(mXVideoRecorder.getXSurfaceView(),mBgloyout.getChildCount()-1);
//				mBgloyout.re
			}
			break;
		}
	}
	
	protected void test_click_getcpuinfo()
	{
		Log.i(TAG, "cpu num:" + XUtils.getNumCores());
		Log.i(TAG, "cpu max:" + XUtils.getCpuFrequence());
		Log.i(TAG, "cpu min:" + XUtils.getMinCpuFreq());
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		Log.i(TAG, "event:"+event.getAction());
		if(mXVideoRecorder != null){
			boolean zoom = mXVideoRecorder.onTouchZoomEvent(event);
			if(!zoom){
				boolean focus = mXVideoRecorder.onTouchFocusEvent(event);
			}
			Log.i(TAG, "onTouchZoomEvent:"+zoom);
		}
		return super.onTouchEvent(event);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (mXVideoRecorder != null) {
			mXVideoRecorder.release();
		}
	}
	
//	public void changeRotation(GLLayer glLayer){
//		if(glLayer == null){
//			return;
//		}
//   	 	int oritation = glLayer.getOrientation();
//	   	Log.i(TAG, "oritation:"+oritation);
//	   	int newOritation = oritation;
//	   	if(oritation ==  GLLayer.ORIENTATION_0){
//	   		newOritation = GLLayer.ORIENTATION_270;
//	   	}else if(oritation ==  GLLayer.ORIENTATION_90){
//	   		newOritation = GLLayer.ORIENTATION_0;
//		}else if(oritation ==  GLLayer.ORIENTATION_180){
//			newOritation = GLLayer.ORIENTATION_90;
//		}else if(oritation ==  GLLayer.ORIENTATION_270){
//			newOritation = GLLayer.ORIENTATION_180;
//		}
//	   	mMediaRecorder.changePreviewRotation(newOritation);
//	   	mMediaRecorder.saveCameraRotation();
//   }
	
	private void testPreview() {
		
		Log.i(TAG, "[testRecorder] mMediaRecorder.getStatus():"+mXVideoRecorder.getStatus()+",mCameraId:"+mCameraId);
		if(mXVideoRecorder.getStatus() != XMediaRecorderInterface.STATUS_UNKNOW){
			Log.e(TAG, "mediaRecorder is run");
			return ;
		}
		if(!mXVideoRecorder.isPreview()){
			mXVideoRecorder.startPreview(mCameraId);
		}else{
			mXVideoRecorder.stopPreview();
		}
//		mMediaRecorder.turnFlashlight();
	}
	
//	private void testZoom(){
//		Camera camera = mMediaRecorder.getCamLayer().getCamera();
//		if(camera != null){
//			int maxZoom = camera.getParameters().getMaxZoom();
//			int zoom = camera.getParameters().getZoom();
//			List<Integer> lists = camera.getParameters().getZoomRatios();
//			for(int i = 0; i < lists.size(); i++){
//				Log.i(TAG, "[testZoom] "+i+":"+lists.get(i));
//			}
//			Log.i(TAG, "[testZoom] maxZoom:"+maxZoom+",zoom:"+zoom);
//			mMediaRecorder.getXSurfaceView().changeZoom(maxZoom, zoom+10);
//		}
//		
//	}
	
//	private void testBalance(){
//		mMediaRecorder.getXSurfaceView().enableBalanceView(true);
//	}

	private void testRecorder() {
		Log.i(TAG, "[testRecorder] mMediaRecorder.getStatus():"+mXVideoRecorder.getStatus());
		if (mXVideoRecorder.isUnknow() || mXVideoRecorder.isStop()) {
			SimpleDateFormat format=new SimpleDateFormat("yyyyMMddHHmmss");
	    	String nowTime=format.format(new Date(System.currentTimeMillis()));	
			String path = "/mnt/sdcard/video";
			String name = nowTime;
//			mMediaRecorder.startPreview();
			mXVideoRecorder.start(name, path);
//			isToStop = false;
			isStart = true;
//			(new ReadWaveformThread()).start();
		}else{
			isStart = true;
			mXVideoRecorder.stop();
		}
//		mMediaRecorder.turnFlashlight();
	}
	
	private void testRecorderPause() {
		Log.i(TAG, "[testRecorder] mMediaRecorder.getStatus():"+mXVideoRecorder.getStatus());
		if(!mXVideoRecorder.isSupportPauseAndResume()){
			Log.e(TAG, "[] not support pause and resume");
			return;
		}
		if (mXVideoRecorder.getStatus() == XMediaRecorderInterface.STATUS_RECORDING) {
			mXVideoRecorder.pause();
//			 isToStop = true;
		}else if (mXVideoRecorder.getStatus() == XMediaRecorderInterface.STATUS_PAUSE) {
			mXVideoRecorder.resume();
		}
//		mMediaRecorder.turnFlashlight();
	}
	
	class MyMediaInfo implements XVideoRecorder.XVideoRecorderInfoListener<Object>{

		@Override
		public void onSurfaceCreated() {
			// TODO Auto-generated method stub
			Log.i(TAG, "[onSurfaceCreated] ");
			testPreview();
		}
		
		@Override
		public void onStartRecorder(Object r, String path) {
			// TODO Auto-generated method stub
			Log.i(TAG, "[onStartRecorder] path:"+path);
		}

		@Override
		public void onStopRecorder(Object r, String path) {
			// TODO Auto-generated method stub
			Log.i(TAG, "[onStopRecorder] path:"+path);
		}

		@Override
		public void onSmallBoxComplete(Object r, String path) {
			// TODO Auto-generated method stub
			Log.i(TAG, "[onSmallBoxComplete] path:"+path);
		}

		@Override
		public void onPauseRecorder(Object r) {
			// TODO Auto-generated method stub
			Log.i(TAG, "[onPauseRecorder] Object:"+r);
		}

		@Override
		public void onResumeRecorder(Object r) {
			// TODO Auto-generated method stub
			Log.i(TAG, "[onResumeRecorder] ");
		}

		@Override
		public void onCameraOpenFailed(String msg) {
			// TODO Auto-generated method stub
			Log.i(TAG, "[onCameraOpenFailed] msg:"+msg);
		}

//		@Override
//		public void onFrameDraw(Object r, int index) {
////			Log.i(TAG, "[onFrameDraw] index:"+index);
//			// TODO Auto-generated method stub
//			if(mXVideoRecorder != null)mXVideoRecorder.requestRenderView(); 
//		}
		
	} 
	
//	private boolean mSaveComplete = true;
//	class MyTakePicture implements Camera.PictureCallback{
//
//		@Override
//		public void onPictureTaken(byte[] data, Camera camera) {
//			Log.i(TAG, "onPictureTaken");
////			camera.stopPreview();
//			//mMediaRecorder.stopPreview();
//			// TODO Auto-generated method stub
////			mMediaRecorder.stopPreview();
////			mMediaRecorder.startPreview();
////			camera.stopPreview();
//			if(mVideoMode == EffectType.CamaraMode.DEF_VIDEO_COMMON){
//				
//			}else if(USE_CAMERA_VIDEO){
//				
//			}else{
//				camera.startPreview();
//			}
////			mHandler.sendEmptyMessageDelayed(HANDLER_EVENT_START_PREVIEW, 10);
//			if(mSaveComplete){
//				if(mVideoMode == EffectType.CamaraMode.DEF_VIDEO_COMMON){
//					//来自底层RGBA数据
//					(new SaveJNIIMGThread(data)).start();
//				}else{
//					(new SaveIMGThread(data)).start();
//				}
//			}
////				else{
//				Size size = camera.getParameters().getPictureSize();
////				Log.i(TAG, "丢弃 防止图片太多内存溢出"+size.width+"x"+size.height);
////			}
//		}
//	}
//	
//	class SaveIMGThread extends Thread{
//		private byte[] mSaveData = null;
//		public SaveIMGThread(byte[] data){
//			mSaveData = data;
//		}
//		@Override
//		public void run() {
//			mSaveComplete = false;
//			
//			SimpleDateFormat format=new SimpleDateFormat("yyyyMMddHHmmss");
//	    	String nowTime=format.format(new Date(System.currentTimeMillis()));	
//			File f = new File("/sdcard/videos/takepic_"+nowTime+".png");
//			if(!f.getParentFile().exists()){
//				f.getParentFile().mkdirs();
//			}
//			if(f.exists())f.delete();
//			
//			Bitmap bitmap = null;
//			FileOutputStream fout = null;
//			if(USE_CAMERA_VIDEO){//mVideoMode == EffectType.CamaraMode.DEF_VIDEO_COMMON){
//				//来自录制的预览数据
//				try{
//	//				TypedValue value = new TypedValue();
////					BitmapFactory.Options opts = new BitmapFactory.Options();
////					opts.inPreferredConfig = Config.ARGB_8888;
//	//				opts.inTargetDensity = value.density;
////					bitmap = BitmapFactory.decodeByteArray(mSaveData, 0, mSaveData.length,opts);
////					mXEffect.processBitmap(bitmap);//特效处理
//					
//					int[] colors = XUtils.convertByteRGBAToARGBInt(mSaveData);
//					Bitmap tmpbitmap = Bitmap.createBitmap(colors, mMediaRecorder.getVideoWidth(), mMediaRecorder.getVideoHeight(), Config.ARGB_8888);
//					int rotation = mMediaRecorder.getCamLayer().getCameraRotation();
//					if(rotation != 0){
//						Matrix m = new Matrix();
//						m.setRotate(rotation, tmpbitmap.getWidth()/2, tmpbitmap.getHeight()/2);
//						bitmap = Bitmap.createBitmap(tmpbitmap, 0, 0, tmpbitmap.getWidth(), tmpbitmap.getHeight(),m,true);
//						if(tmpbitmap != null){
//							tmpbitmap.recycle();
//						}
//					}else{
//						bitmap = tmpbitmap;
//					}
//				}catch(Exception e){
//					e.printStackTrace();
//					return;
//				}
//			}
//			try{
//				f.createNewFile();
//				fout = new FileOutputStream(f);
//				if(bitmap != null){//录制预览数据
//					bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fout);
//				}else{
//					fout.write(mSaveData);//Camera拍照数据
//				}
//				fout.flush();
//			}catch(Exception e){
//				e.printStackTrace();
//			}
//			finally{
//				try {
//					if(fout != null)fout.close();
//				} catch (Exception e2) {
//					e2.printStackTrace();
//				}
//			}
//			if(bitmap != null){
//				bitmap.recycle();
//			}
//			mSaveData = null;
//			mSaveComplete = true;
//		}
//	}
//	
//	
//	
//	/**
//	 * 保存返回的RGBA图片
//	 * @author Administrator
//	 *
//	 */
//	class SaveJNIIMGThread extends Thread{
//		private byte[] mSaveData = null;
//		public SaveJNIIMGThread(byte[] data){
//			mSaveData = data;
//		}
//		@Override
//		public void run() {
//			if(mSaveData == null)return;
//			mSaveComplete = false;
//			Bitmap bitmap = null;
//			try{
//				
//				int[] colors = XUtils.convertByteRGBAToARGBInt(mSaveData);
//				Bitmap tmpbitmap = Bitmap.createBitmap(colors, mMediaRecorder.getVideoWidth(), mMediaRecorder.getVideoHeight(), Config.ARGB_8888);
//				int rotation = mMediaRecorder.getCamLayer().getCameraRotation();
//				if(rotation != 0){
//					Matrix m = new Matrix();
//					m.setRotate(rotation, tmpbitmap.getWidth()/2, tmpbitmap.getHeight()/2);
//					bitmap = Bitmap.createBitmap(tmpbitmap, 0, 0, tmpbitmap.getWidth(), tmpbitmap.getHeight(),m,true);
//					if(tmpbitmap != null){
//						tmpbitmap.recycle();
//					}
//				}else{
//					bitmap = tmpbitmap;
//				}
////				bitmap = BitmapFactory.decodeByteArray(array, 0, array.length);
//				Log.i(TAG, "[SaveJNIIMGThread] length:"+mSaveData.length);
//				Log.i(TAG, "[SaveJNIIMGThread] getHeight:"+bitmap.getHeight()+",getWidth:"+bitmap.getWidth());
//			}catch(Exception e){
//				e.printStackTrace();
//				return;
//			}
//			SimpleDateFormat format=new SimpleDateFormat("yyyyMMddHHmmss");
//	    	String nowTime=format.format(new Date(System.currentTimeMillis()));	
//			File f = new File("/sdcard/videos/takeJNIpic_"+nowTime+".png");
//			if(!f.getParentFile().exists()){
//				f.getParentFile().mkdirs();
//			}
//			if(f.exists())f.delete();
//			FileOutputStream fout = null;
//			try{
//				f.createNewFile();
//				fout = new FileOutputStream(f);
//				bitmap.compress(Bitmap.CompressFormat.PNG, 100, fout);
//				fout.flush();
//			}catch(Exception e){
//				e.printStackTrace();
//			}
//			finally{
//				try {
//					if(fout != null)fout.close();
//				} catch (Exception e2) {
//					e2.printStackTrace();
//				}
//			}
//			if(bitmap != null){
//				bitmap.recycle();
//			}
//			mSaveData = null;
//			mSaveComplete = true;
//		}
//	}
	
	class MyXVideoTakePicture implements XVideoTakePicture{

		@Override
		public void onPictureTaken(Bitmap bitmap,String filePath) {
		// TODO Auto-generated method stub
			Log.i(TAG, "[onPictureTaken] bitmap:"+bitmap+",filePath:"+filePath);
		}
		@Override
		public void onPictureTakenComplete(String filePath) {
			// TODO Auto-generated method stub
			Log.i(TAG, "[onPictureTakenComplete] filePath:"+filePath);
		}
		
	}
	
	class ItemSelectedListener implements OnItemSelectedListener{

		@Override
		public void onItemSelected(AdapterView<?> arg0, View arg1, int position,
				long id) {
			EffectBean effectBean = mEffects.get(position);
			if(mXEffect != null && mEffectUtils != null && effectBean != null){
				int fineness = 0;
				Rect rect = null;
				if(effectBean.getType() == EffectType.KEF_TYPE_MOSAIC){
					fineness = 10;
					rect = new Rect(0,0,100,100);
				}
				mEffectUtils.changeEffectdWithEffectBean(mXEffect,effectBean,
						mXVideoRecorder.getVideoWidth(), mXVideoRecorder.getVideoHeight(),fineness,rect,
						mXVideoRecorder.getPreviewRotation());
			}
			
			// TODO Auto-generated method stub
			Toast.makeText(RecorderVideoActivity.this, effectBean.getFilePath()+","+effectBean.getZHName(), Toast.LENGTH_SHORT).show();
			Log.i(TAG, "[onItemSelected] getZHName:"+effectBean.getZHName()+",getFilePath:"+effectBean.getFilePath()+",getPicName:"+effectBean.getPicName()+",getTag:"+effectBean.getEffectsTag()+",getType:"+effectBean.getType());
			
		}

		@Override
		public void onNothingSelected(AdapterView<?> arg0) {
			// TODO Auto-generated method stub
			
		}
		
	}
	
	
//	class ReadWaveformThread extends Thread{
//		@Override
//		public void run() {
//			while(isStart){
//				Log.i(TAG, "[ReadWaveformThread] ****************************");
//				float waveforms[] = mXVideoRecorder.readWaveform(15, 100);
//				if(waveforms != null){
//					for(int i = 0; i < waveforms.length;i++){
//						Log.i(TAG, "[ReadWaveformThread] i "+i+","+waveforms[i]);
//					}
//				}
//			}
//		}
//	}
}