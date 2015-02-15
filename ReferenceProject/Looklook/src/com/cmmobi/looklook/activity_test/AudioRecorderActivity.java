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

import effect.CamLayer;
import effect.EffectType;
import effect.XEffectMediaRecorder;
import effect.XEffects;
import effect.XEffectMediaRecorder.OnInfoListener;
import effect.XEffects.XEffect;

public class AudioRecorderActivity extends Activity implements View.OnClickListener {
	private static final int READ_BYTE_DATA_SIZE = 4*1024;

	private static final String TAG = "ZC_JAVA_AudioRecorderActivity";
	
	private XAudioRecorder mAudioRecorder;
	
	private boolean isStart = false;
	private boolean isToStop = false;
	
	private final int mStartRecorderId = 101;
	
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		ContextHolder.setContext(this);

		RelativeLayout shell = new RelativeLayout(this);
		shell.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT,LayoutParams.FILL_PARENT));
//		 shell.setBackgroundColor(0xff000099);

		setContentView(shell);

		mAudioRecorder = new XAudioRecorder(new MyMediaInfo());


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
		if (mAudioRecorder != null) {
			mAudioRecorder.release();
		}
	}

	private void testRecorder() {
		if(mAudioRecorder == null) mAudioRecorder = new XAudioRecorder(new MyMediaInfo());
		Log.i(TAG, "[testRecorder] mMediaRecorder.getStatus():"+mAudioRecorder.getStatus());
		if (mAudioRecorder.getStatus() == XEffectMediaRecorder.STATUS_UNKNOW) {
			SimpleDateFormat format=new SimpleDateFormat("yyyyMMddHHmmss");
	    	String nowTime=format.format(new Date(System.currentTimeMillis()));	
			String path = "/mnt/sdcard/video/";
			String name = nowTime;
			mAudioRecorder.start(name, path);
			isToStop = false;
			isStart = true;
			(new ReadThread()).start();
		} else if (mAudioRecorder.getStatus() == XEffectMediaRecorder.STATUS_RECORDING && isToStop == false) {
			mAudioRecorder.pause();
			isToStop = true;
		}else if (mAudioRecorder.getStatus() == XEffectMediaRecorder.STATUS_PAUSE) {
			mAudioRecorder.resume();
		}else if (mAudioRecorder.getStatus() == XEffectMediaRecorder.STATUS_RECORDING && isToStop) {
			isStart = false;
			mAudioRecorder.stop();
		}else if (mAudioRecorder.getStatus() == XEffectMediaRecorder.STATUS_STOP) {
			mAudioRecorder.release();
			mAudioRecorder = null;
		}
		
		// mMediaRecorder.turnFlashlight();
	}
	
	
	class MyMediaInfo implements XAudioRecorder.XAudioReaderInfoListener{

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
			// TODO Auto-generated method stub
			Log.i(TAG, "[onFrameDraw] index:"+index);
		}
		
	}
	
	
	
	class ReadThread extends Thread{
		@Override
		public void run() {
			DataOutputStream outs = null;
			try {
				outs = new DataOutputStream(new FileOutputStream(openFile())) ;
			} catch (Exception e) {
				e.printStackTrace();
			}
			int readsize = 0;
			while(isStart){
				byte[] readData = new byte[READ_BYTE_DATA_SIZE];
				if(mAudioRecorder == null)break;
				readsize = mAudioRecorder.read(readData, READ_BYTE_DATA_SIZE);
				if(readsize > 0){
					Log.i(TAG, "ReadThread read data size "+readsize);
					writeFile(outs, readData);
				}
			}
			closeFile(outs);
		}
		
		public File openFile() throws IOException{
			SimpleDateFormat format=new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
	    	String nowTime=format.format(new Date(System.currentTimeMillis()));	
	    	File f = new File("/mnt/sdcard/video/audioRecorder_"+nowTime);
	    	if(!f.exists()){
	    		f.createNewFile();
	    	}
	    	return f;
		}
		public void writeFile(DataOutputStream outs,byte[] readData){
			try {
				if(outs != null)outs.write(readData);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		public void closeFile(DataOutputStream outs){
			try {
				outs.flush();
				outs.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			
		}
	}
}