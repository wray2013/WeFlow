package com.cmmobi.looklook.activity_test;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;

import com.cmmobivideo.utils.ContextHolder;
import com.cmmobivideo.workers.XAudioReader;

import effect.XEffectAudioReader;
public class AudioReaderActivity extends Activity implements View.OnClickListener{
	private static final String TAG = "ZC_JAVA_AudioReaderActivity";
	
	private static final int READ_BYTE_DATA_SIZE = 4*1024;
	private XAudioReader mAudioReader;
	private boolean isStart = false;
	private boolean isToStop = false;
	
//	private String mFilePath = "/mnt/sdcard/videos/test0001.mp4";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		ContextHolder.setContext(this);
		
		RelativeLayout shell = new RelativeLayout(this);
		shell.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
		shell.setBackgroundColor(0xff000099);
		
//		shell.addView(shell);
		setContentView(shell);
		
		mAudioReader = new XAudioReader(new XAudioReaderInfoListener());
		
		
		Button button = new Button(this);
		button.setText("开始/停止");
		
		
		button.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
//				if(mAudioReader == null)mAudioReader = new XAudioReader(new XAudioReaderInfoListener());
				if (mAudioReader.getStatus() == XEffectAudioReader.STATUS_UNKNOW || mAudioReader.getStatus() == XEffectAudioReader.STATUS_STOP) {
			    	mAudioReader.start("/mnt/sdcard/videos/20130707184824/20130707184824.mp4");//test.mp4");//;test0001.mp4");
			    	(new ReadThread()).start();
			    	isStart= true;
					isToStop = false;
				} else if (mAudioReader.getStatus() == XEffectAudioReader.STATUS_START && isToStop == false) {
					mAudioReader.pause();
					isToStop = true;
				}else if (mAudioReader.getStatus() == XEffectAudioReader.STATUS_PAUSE) {
					mAudioReader.resume();
				}else if (mAudioReader.getStatus() == XEffectAudioReader.STATUS_START && isToStop) {
					isStart = false;
					mAudioReader.stop();
				}
//				else if(mAudioReader.getStatus() == XEffectAudioReader.STATUS_STOP) {
//					mAudioReader.release();
//					mAudioReader= null;
//				}
			}
		});
		shell.addView(button, 211, 100);
		
	}
	
	@Override
	protected void onPause() {
		super.onPause();
	}

	@Override
	public void onClick(View view) {
		
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		if(mAudioReader != null){
			mAudioReader.release();
		}
	}
	
	
	public class XAudioReaderInfoListener implements XAudioReader.XAudioReaderInfoListener{

		@Override
		public void OnFinishRead(XEffectAudioReader r) {
			// TODO Auto-generated method stub
			Log.i(TAG, "[OnFinishRead]");
			isStart = false;
		}
	}
	
	class ReadThread extends Thread{
		@Override
		public void run() {
			Log.i(TAG, "ReadThread->run");
			DataOutputStream outs = null;
			try {
				outs = new DataOutputStream(new FileOutputStream(openFile())) ;
			} catch (Exception e) {
				e.printStackTrace();
			}
			int readsize = 0;
			while(isStart){
				byte[] readData = new byte[READ_BYTE_DATA_SIZE];
				Log.i(TAG, "start read");
				readsize = mAudioReader.read(readData, READ_BYTE_DATA_SIZE);
				Log.i(TAG, "start readsize:"+readsize);
				if(readsize > 0){
					Log.i(TAG, "ReadThread read data size "+readsize);
					writeFile(outs, readData);
				}
				if(mAudioReader.getStatus() == XEffectAudioReader.STATUS_STOP){
					break;
				}
			}
			closeFile(outs);
		}
		
		public File openFile() throws IOException{
			Log.i(TAG, "openFile");
			SimpleDateFormat format=new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
	    	String nowTime=format.format(new Date(System.currentTimeMillis()));	
	    	File f = new File("/mnt/sdcard/video/audioReader_"+nowTime);
	    	if(!f.exists()){
	    		f.createNewFile();
	    	}
	    	return f;
		}
		public void writeFile(DataOutputStream outs,byte[] readData){
			try {
				Log.i(TAG, "writeFile");
				if(outs != null)outs.write(readData);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		public void closeFile(DataOutputStream outs){
			Log.i(TAG, "closeFile");
			try {
				outs.flush();
				outs.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			
		}
	}
}