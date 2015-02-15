package com.iflytek.msc;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentLinkedQueue;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.cmmobi.looklook.MainApplication;
import com.cmmobivideo.workers.XAudioReader;
import com.iflytek.speech.RecognizerListener;
import com.iflytek.speech.RecognizerResult;
import com.iflytek.speech.SpeechError;
import com.iflytek.speech.SpeechRecognizer;

import effect.XEffectAudioReader;

public class AudioRecognizer implements RecognizerListener{
	private final String TAG = "AudioRecognizer";
	private String mFilePath;
	private Handler handler;
	private final int READ_BYTE_DATA_SIZE = 4800;
	private XAudioReader mAudioReader;
	private boolean isStart = false;
	private SpeechRecognizer recognizer;
	public boolean sendRecDoneFlag = false;
	private ConcurrentLinkedQueue<byte[]> pcmDataQueue;
	private String resultStr = "";
	public static final int HANDLER_QISR_RESULT = 0x34900386;
	
	public AudioRecognizer(Handler handler,String path) {
		this.handler = handler;
		this.mFilePath = path;
		mAudioReader = new XAudioReader(new XAudioReaderInfoListener());
		pcmDataQueue = new ConcurrentLinkedQueue<byte[]>();
		this.recognizer  = SpeechRecognizer.createRecognizer(MainApplication.getAppInstance(), "appid=" + Constants.appID);
	}
	
	public void recognizer() {
		sendRecDoneFlag = false;
		isStart = true;
		if (mFilePath != null) {
			if (mAudioReader.start(mFilePath) == 0) {
				new ReadThread().start();
			} else {
				sendMessage("");
			}
		} else {
			sendMessage("");
		}
	}
	
	public void stop() {
		handler = null;
		isStart = false;
		recognizer.cancel();
	}
	
	class ReadThread extends Thread{
		@Override
		public void run() {
			int readsize = 0;
			
			Log.d(TAG,"ReadThread run in");
			while(isStart) {
				byte[] readData = new byte[READ_BYTE_DATA_SIZE];
				readsize = mAudioReader.read(readData, READ_BYTE_DATA_SIZE);
				if(readsize > 0){
					pcmDataQueue.add(readData);
				}
			}
			recognizer.recognizeAudio(AudioRecognizer.this, pcmDataQueue,  "sms", null, null);
			sendRecDoneFlag = true;
		}
	}
	
	private void sendMessage(String str) {
		if (handler != null) {
			Message msg = new Message();
			msg.what = HANDLER_QISR_RESULT;
			msg.obj = resultStr;
			handler.sendMessage(msg);
			Log.d(TAG,"sendMessage " + this + " handler = " + handler);
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

	@Override
	public void onBeginOfSpeech() {
		// TODO Auto-generated method stub
		Log.d(TAG,"onBeginOfSpeech in");
	}

	@Override
	public void onCancel() {
		// TODO Auto-generated method stub
		Log.d(TAG,"onCancel in");
	}

	@Override
	public void onEnd(SpeechError arg0) {
		// TODO Auto-generated method stub
		Log.d(TAG,"onEnd in");
		Log.d(TAG,"EditNoteActivity ####################");
		if (isStart) {
			recognizer.stopListening();
			recognizer.recognizeAudio(AudioRecognizer.this, pcmDataQueue,  "sms", null, null);
		} else {
			sendMessage(resultStr);
		}
	}
	
	@Override
	public void onEndOfSpeech() {
		// TODO Auto-generated method stub
		Log.d(TAG, "onEndOfSpeech:");
	}

	@Override
	public void onResults(ArrayList<RecognizerResult> results, boolean isLast) {
		// TODO Auto-generated method stub
		String text = "";
		for(int i = 0; i < results.size(); i++)
		{
			RecognizerResult result = (RecognizerResult)results.get(i);
			text += result.text;
		}
		resultStr += text;
		Log.d(TAG,"onResults text = " + text);
		
	}

	@Override
	public void onVolumeChanged(int arg0) {
		// TODO Auto-generated method stub
//		Log.d(TAG,"onVolumeChanged arg0 = " + arg0);
	}

}
