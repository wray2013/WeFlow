package com.cmmobi.looklook.activity_test;

import java.text.SimpleDateFormat;
import java.util.Date;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;

import com.cmmobivideo.utils.ContextHolder;
import com.cmmobivideo.workers.XMediaRecorder;

import effect.XEffectMediaRecorder;
import effect.XEffects;

public class MediaRecorderActivity extends Activity implements View.OnClickListener {

	private static final String TAG = "ZC_JAVA_MediaRecorderActivity";
	
	private XMediaRecorder mMediaRecorder;
	
	private XEffects mXEffect;
	private final int mStartRecorderId = 100;
	
	private boolean isToStop = false;
	
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		ContextHolder.setContext(this);

		RelativeLayout shell = new RelativeLayout(this);
		shell.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT,LayoutParams.FILL_PARENT));
//		 shell.setBackgroundColor(0xff000099);

		setContentView(shell);

		// 创建效果对象
		mXEffect = new XEffects();
//		XEffects.XEffect teffect = mXEffect.newEffect(EffectType.KEF_TYPE_NOTHING);
//		teffect.setEffectTag(1);
//		mXEffect.addEffectsInFrontWithTag(teffect, -1);

		mMediaRecorder = new XMediaRecorder(this, mXEffect,new MyMediaInfo());

		ViewGroup.LayoutParams CamLayer_params = new ViewGroup.LayoutParams(200, 200);
		mMediaRecorder.getCamLayer().setLayoutParams(CamLayer_params);
		shell.addView(mMediaRecorder.getCamLayer());
		shell.addView(mMediaRecorder.getGLLayer(), 555, 555);

		Button button = new Button(this);
		button.setText("录制/停止");
		button.setId(mStartRecorderId);
		button.setOnClickListener(this);

		shell.addView(button, 211, 100);
		
		

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
		case mStartRecorderId:
			testRecorder();
			break;
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (mMediaRecorder != null) {
			mMediaRecorder.release();
		}
	}

	private void testRecorder() {
		Log.i(TAG, "[testRecorder] mMediaRecorder.getStatus():"+mMediaRecorder.getStatus());
		if (mMediaRecorder.getStatus() == XEffectMediaRecorder.STATUS_UNKNOW || mMediaRecorder.getStatus() == XEffectMediaRecorder.STATUS_STOP) {
			SimpleDateFormat format=new SimpleDateFormat("yyyyMMddHHmmss");
	    	String nowTime=format.format(new Date(System.currentTimeMillis()));	
			String path = "/mnt/sdcard/a4a/";
			String name = nowTime;
			mMediaRecorder.startPreview();
			mMediaRecorder.start(name, path);
			isToStop = false;
		} else if (mMediaRecorder.getStatus() == XEffectMediaRecorder.STATUS_RECORDING && isToStop == false) {
			 mMediaRecorder.pause();
			 isToStop = true;
		}else if (mMediaRecorder.getStatus() == XEffectMediaRecorder.STATUS_PAUSE) {
			 mMediaRecorder.resume();
		}else if (mMediaRecorder.getStatus() == XEffectMediaRecorder.STATUS_RECORDING && isToStop) {
			 mMediaRecorder.stop();
		}
		mMediaRecorder.turnFlashlight();
	}
	
	
	class MyMediaInfo implements XMediaRecorder.XMediaRecorderInfoListener{

		@Override
		public void onStartRecorder(XEffectMediaRecorder r, String path) {
			// TODO Auto-generated method stub
			Log.i(TAG, "[onStartRecorder] path:"+path);
		}

		@Override
		public void onStopRecorder(XEffectMediaRecorder r, String path) {
			// TODO Auto-generated method stub
			Log.i(TAG, "[onStopRecorder] path:"+path);
		}

		@Override
		public void onSmallBoxComplete(XEffectMediaRecorder r, String path) {
			// TODO Auto-generated method stub
			Log.i(TAG, "[onSmallBoxComplete] path:"+path);
		}

		@Override
		public void onFrameDraw(XEffectMediaRecorder r, int index) {
			Log.i(TAG, "[onFrameDraw] index:"+index);
			// TODO Auto-generated method stub
			if(r != null)r.getGLLayer().requestRender(); 
		}
		
	} 
}