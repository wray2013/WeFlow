package com.cmmobi.looklook.activity_test;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Date;
import java.text.SimpleDateFormat;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;

import com.cmmobivideo.utils.ContextHolder;
import com.cmmobivideo.workers.XAudioRecorder;
import com.cmmobivideo.workers.XMediaPlayer;
import com.cmmobivideo.workers.XMediaRecorder;
import com.cmmobivideo.workers.XTransCode;
import com.cmmobivideo.workers.XTransCode.TransCodeInfoListener;

import effect.CamLayer;
import effect.EffectType;
import effect.XEffectMediaRecorder;
import effect.XEffectTransCode;
import effect.XEffects;
import effect.XEffectMediaRecorder.OnInfoListener;
import effect.XEffects.XEffect;

public class TransCodeActivity extends Activity implements View.OnClickListener {

	private static final String TAG = "ZC_JAVA_TransCodeActivity";
	
	private XTransCode mTranscode = null;
	
	private boolean isStart = false;
	
	private final int mStartTranscodeId = 101;
	private XEffects mEffects;
	private String mInfilename = "/sdcard/videos/test0001_split.mp4";
	private String mOutfilename = "/sdcard/videos/test0001_transcode.mp4";
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		ContextHolder.setContext(this);

		RelativeLayout shell = new RelativeLayout(this);
		shell.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT,LayoutParams.FILL_PARENT));
//		 shell.setBackgroundColor(0xff000099);

		setContentView(shell);
		
		mEffects = new XEffects();
		XEffects.XEffect teffect = null;
         /* */
        teffect = mEffects.newEffect(EffectType.KEF_TYPE_OLDPAPER);//KEF_TYPE_OLDPAPER);//KEF_TYPE_NOTHING);//KEF_TYPE_SKETCH);
        teffect.setEffectTag(2);
        mEffects.addEffectsInFrontWithTag(
         		        		teffect, -1);

		
		mTranscode = new XTransCode(mEffects,new MyTranCodeInfo());


		Button button = new Button(this);
		button.setText("开始/停止");
		button.setId(mStartTranscodeId);
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
		case mStartTranscodeId:
			testTransCode();
			break;
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (mTranscode != null) {
			mTranscode.release();
		}
	}

	private void testTransCode() {
		if (mTranscode.getStatus() == XEffectTransCode.STATUS_UNKNOW || mTranscode.getStatus() == XEffectTransCode.STATUS_STOP) {
			mTranscode.open(mInfilename, mOutfilename);
			Log.i(TAG, "[play] mTotalTime:"+mTranscode.getTotalTime()+",width:"+mTranscode.getVideoWith()+",height:"+mTranscode.getVideoHeight());
			mTranscode.start();
		}else if (mTranscode.getStatus() == XEffectTransCode.STATUS_START) {
			mTranscode.stop();
		}
		
		// mMediaRecorder.turnFlashlight();
	}
	
	class MyTranCodeInfo implements TransCodeInfoListener{

		@Override
		public void OnSchedule(double percent) {
			// TODO Auto-generated method stub
			Log.i(TAG, "[OnSchedule] percent:"+percent);
		}

		@Override
		public void OnFinish() {
			// TODO Auto-generated method stub
			Log.i(TAG, "[OnFinish]");
		}
		
	}
}