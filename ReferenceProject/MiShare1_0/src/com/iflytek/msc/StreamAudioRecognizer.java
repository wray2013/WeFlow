package com.iflytek.msc;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.LinkedBlockingQueue;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.cmmobi.looklook.MainApplication;
import com.cmmobivideo.workers.XAudioReader;
import com.iflytek.msc.AudioRecognizer.ReadThread;
import com.iflytek.msc.AudioRecognizer.XAudioReaderInfoListener;
import com.iflytek.speech.RecognizerListener;
import com.iflytek.speech.RecognizerResult;
import com.iflytek.speech.SpeechError;
import com.iflytek.speech.SpeechRecognizer;

import effect.XEffectAudioReader;

public class StreamAudioRecognizer implements RecognizerListener{
	private final String TAG = "StreamAudioRecognizer";
	private String mFilePath;
	private Handler handler;
	private final int READ_BYTE_DATA_SIZE = 4800;
	private XAudioReader mAudioReader;
	private boolean isStart = false;
	private SpeechRecognizer recognizer;
	public boolean sendRecDoneFlag = false;
	private String resultStr = "";
	public static final int HANDLER_QISR_RESULT = 0x34900387;
	private boolean reset = false;
	private LinkedBlockingQueue<AudioFrame> fifo;
	private RecognizerThread qisr_task;
	
	public StreamAudioRecognizer(Handler handler,String path) {
		this.handler = handler;
		this.mFilePath = path;
		mAudioReader = new XAudioReader(new XAudioReaderInfoListener());
		fifo = new LinkedBlockingQueue<AudioFrame>();
		this.recognizer  = SpeechRecognizer.createRecognizer(MainApplication.getAppInstance(), "appid=" + Constants.appID);
	}
	
	public void recognizer() {
		sendRecDoneFlag = false;
		isStart = true;
		if (mFilePath != null) {
			if (mAudioReader.start(mFilePath) == 0) {
				new ReadThread().start();
				qisr_task = new RecognizerThread();
				qisr_task.execute();
			} else {
				sendMessage("");
				Log.d(TAG,"EditNoteActivity ...............");
			}
		} else {
			sendMessage("");
			Log.d(TAG,"EditNoteActivity +++++++++++++++++++");
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
					Log.i(TAG, "start readsize:"+readsize);
					try {
						fifo.put(new AudioFrame(readData, readsize, 1));
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}//Normal
				}
			}
			sendRecDoneFlag = true;
			Log.d(TAG,"ReadThread run out");
		}
	}
	
	private class RecognizerThread extends Thread{
		
		private AudioFrame af;

		public void execute() {
			// TODO Auto-generated method stub
			this.start();
		}
		
		public void run() {
			recognizer.recognizeStream(StreamAudioRecognizer.this, "sms", null, null);
			while( !sendRecDoneFlag || !fifo.isEmpty()) {
				af = null;
				af = fifo.poll();
				
				if(af!=null){
					recognizer.writeAudio(af.data ,0, af.len);
				}

				if(reset){
					recognizer.stopListening();
					recognizer.recognizeStream(StreamAudioRecognizer.this, "sms", null, null);
					reset = false;
				}
			}
			Log.d(TAG,"RecognizerThread out");
			recognizer.stopListening();
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
		if(!sendRecDoneFlag || !fifo.isEmpty()){
			reset = true;
		}else{
			reset = false;
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
