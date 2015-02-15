package com.iflytek.msc;

import java.io.File;
import java.util.ArrayList;
import java.util.concurrent.LinkedBlockingQueue;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.AudioFormat;
import android.media.MediaRecorder;
import android.media.MediaRecorder.AudioSource;
import android.os.Environment;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import cn.zipper.framwork.core.ZApplication;

import com.cmmobi.looklook.MainApplication;
import com.cmmobi.looklook.common.utils.EffectsDownloadUtil;
import com.cmmobi.looklook.info.profile.AccountInfo;
import com.cmmobi.looklook.info.profile.ActiveAccount;
import com.cmmobi.looklook.info.profile.MediaValue;
import com.cmmobivideo.utils.Mp4InfoUtils;
import com.cmmobivideo.utils.PluginUtils;
import com.cmmobivideo.utils.XUtils;
import com.cmmobivideo.workers.XAudioRecorder;
import com.iflytek.Setting;
import com.iflytek.speech.RecognizerListener;
import com.iflytek.speech.RecognizerResult;
import com.iflytek.speech.SpeechError;
import com.iflytek.speech.SpeechRecognizer;

/**
 *  带语音录制和识别的录音控件
 * 
 *  @author zhangwei
 * 
 * */
public class ExtAudioRecorder implements Callback , RecognizerListener{
	public static final String AUDIO_RECORDER_MSG = "EXT.AUDIO.RECORDER.MSG";
	public static final int HANDLER_AUDIO_RECORDER_VOLUME = 0x23710384;
	public static final int HANDLER_AUDIO_RECORDER_DONE = 0x23710385;
	
	private final int READ_BYTE_DATA_SIZE = 4800;
	
	private final static String TAG = "ExtAudioRecorder";
	private final static int[] sampleRates = { 44100, 22050, 16000, 11025, 8000 };
	//private int frequency_index;
	private static ExtAudioRecorder ins = null;
	//private Thread recordingThread;
	private RecordAudio_TASK recordingTask;
	
	private SpeechRecognizer recognizer;


	/**
	 * INITIALIZING : recorder is initializing; READY : recorder has been
	 * initialized, recorder not yet started RECORDING : recording ERROR :
	 * reconstruction needed STOPPED: reset needed
	 */
	public enum State {
		INITIALIZING, READY, RECORDING, PAUSE, ERROR, STOPPED
	};
	
	public enum RecordType{
		DEFAULT, XRECORDER
	};

	public static final boolean RECORDING_UNCOMPRESSED = true;
	public static final boolean RECORDING_COMPRESSED = false;

	// The interval in which the recorded samples are output to the file
	// Used only in uncompressed mode
	private static final int TIMER_INTERVAL = 120;

	// Toggles uncompressed recording on/off; RECORDING_UNCOMPRESSED /
	// RECORDING_COMPRESSED
	//private boolean rUncompressed;

	// Recorder used for uncompressed recording
	//private static AudioRecord audioRecorder = null;

	// Recorder used for compressed recording
	private MediaRecorder mediaRecorder = null;

	//AudioRecord audioRecord;
	XAudioRecorder audioRecord;
	
	// Stores current amplitude (only in uncompressed mode)
	private int cAmplitude = 0;



	// Recorder state; see State
	private State state;
	private RecordType recordtype;

	// File writer (only in uncompressed mode)
	//private static RandomAccessFile randomAccessWriter;

	// Number of channels, sample rate, sample size(size in bits), buffer size,
	// audio source, sample size(see AudioFormat)
	//private short nChannels;
	//private short bSamples;
	private int bufferSize;

	
	// Number of bytes written to file after header(only in uncompressed mode)
	// after stop() is called, this size is written to the header/data chunk in
	// the wave file
	private int payloadSize;

	// Number of frames written to file on each output(only in uncompressed
	// mode)
	private int framePeriod;

	// Number of bytes written to file after header(only in uncompressed mode)
	// after stop() is called, this size is written to the header/data chunk in
	// the wave file

	
	private int sampleRate;
	private int channelConfig;
	private int audioFormat;
	private short bSamples;
	// Output file path
	//private String filePath = null;
	
	private LinkedBlockingQueue<AudioFrame> fifo;
	
	//private QISR_TASK qisr_task;
	public boolean isRecording;
	private boolean reset = false;
	private String shortPath;
	private String fullPath;
	private boolean longAudio;
	private int Belong;
	private long playtime;
	private long last_time;
	private String audioID;
	private Handler handler;
	public boolean sendRecDoneFlag;
	private RecognizerThread qisr_task;
	


	public static ExtAudioRecorder getInstanse(Boolean recordingCompressed, int freq_index, int outFormat) {
		if(ins!=null){
			return ins;
		}else{
			ins = new ExtAudioRecorder(!recordingCompressed, AudioSource.MIC,
					freq_index,
				    AudioFormat.CHANNEL_CONFIGURATION_MONO,
					AudioFormat.ENCODING_PCM_16BIT, outFormat);
		}
		
		//ins.setOutputFile(path);

		return ins;
	}
	
	/**
	 * 
	 * 
	 * Default constructor
	 * 
	 * Instantiates a new recorder, in case of compressed recording the
	 * parameters can be left as 0. In case of errors, no exception is thrown,
	 * but the state is set to ERROR
	 * 
	 */
	private ExtAudioRecorder(boolean uncompressed, int audioSource, int freq_index, int channelConfig, int audioFormat, int outFormat) {

		//this.outFormat = outFormat; 
		this.channelConfig = channelConfig;
		this.audioFormat = audioFormat;
		//this.rUncompressed = uncompressed;
		
		this.handler = new Handler(this);
		this.recognizer  = SpeechRecognizer.createRecognizer(MainApplication.getAppInstance(), "appid=" + Constants.appID);
		Setting.showLogcat(false);
		MSC.DebugLog(false);
		fifo = new LinkedBlockingQueue<AudioFrame>(); 
		//qisr_task = new QISR_TASK(sampleRate, channelConfig);	
		state = State.INITIALIZING;
		
		//
		audioRecord = null;
		do{
			if(freq_index>=sampleRates.length){
				Log.e(TAG, "AudioRecord init failed - no sampleRates can fit!!");
				break;
			}
			
			try{
				framePeriod = sampleRates[freq_index] * TIMER_INTERVAL / 1000;
				if(this.audioFormat==AudioFormat.ENCODING_PCM_16BIT){
					bSamples = 16;
					bufferSize = framePeriod * 2 * 16 * (channelConfig-1) / 8;
				}else if(this.audioFormat==AudioFormat.ENCODING_PCM_8BIT){
					bSamples = 8;
					bufferSize = framePeriod * 2 * 8 * (channelConfig-1) / 8;
				}else{
					bSamples = 16;
					bufferSize = framePeriod * 2 * 16 * (channelConfig-1) / 8;
				}
				
				
				//audioRecord = new AudioRecord(MediaRecorder.AudioSource.MIC, sampleRates[freq_index], channelConfig, audioFormat, bufferSize);
				//audioRecord = new XAudioRecorder(new MyMediaInfo());
				break;
/*				if(audioRecord.getState()!=AudioRecord.STATE_INITIALIZED){
					freq_index++;
					continue;

				}else{
					break;
				}*/
			}catch(Exception e){
				freq_index++;
			}

			
		}while(true);



/*		int bufferSize_min = AudioRecord.getMinBufferSize( sampleRates[freq_index],
				channelConfig, audioFormat);
		
		if(bufferSize<bufferSize_min){
			bufferSize = bufferSize_min;
		}*/
		
		bufferSize = 4800;
		sampleRate = 16000;
		//sampleRate = sampleRates[freq_index];
		
		Log.e(TAG, "RecordAudio - init - sampleRates:"
				+ sampleRates[freq_index]
				+ ", channelConfig:" + channelConfig
				+ ", audioFormat:" + audioFormat
				+ ", bufferSize:" + bufferSize);
	}


	
	/**
	 *   重新设置ExtAudioRecorder的参数
	 *   @param recordingCompressed 是否压缩
	 *   @param sampeIndex 采样率的起始索引，由高到低
	 *   @param outFormat 若不压缩模式，则输出格式为0：PCM  1：WAV
	 * 
	 * */
/*	public  void setAudioRecorder(Boolean recordingCompressed, int freq_index, int outFormat) {
		//int i = sampeIndex;
		ins = new ExtAudioRecorder(recordingCompressed, AudioSource.MIC,
				freq_index,
				AudioFormat.CHANNEL_CONFIGURATION_MONO,
				AudioFormat.ENCODING_PCM_16BIT, outFormat);

	}*/



	/**
	 * 
	 * Returns the state of the recorder in a RehearsalAudioRecord.State typed
	 * object. Useful, as no exceptions are thrown.
	 * 
	 * @return recorder state
	 */
	public State getRecorderState() {
		return state;
	}


	/**
	 * Sets output file path, call directly after construction/reset.
	 * 
	 * @param output file path
	 * 
	 */
/*	private void setOutputFile(String argPath) {
		filePath = argPath;
		//recordFile = new File(filePath);
	}*/

	/**
	 * 
	 * Returns the largest amplitude sampled since the last call to this method.
	 * 
	 * @return returns the largest amplitude since the last call, or 0 when not in recording state.
	 * 
	 */
/*	public int getMaxAmplitude() {
		if (state == State.RECORDING) {

			int result = cAmplitude;
			cAmplitude = 0;
			return result;
		
		} else {
			return 0;
		}
	}*/

	/**
	 * 
	 * 
	 * 带语音识别参数启动录音,保存文件路径为/path/audioID/audioID.mp4
	 * 
	 * @param audioID 文件名，不带.mp4
	 * @param path 父级目录,不带末级'/'
	 * @param useReg 是否使用语音识别
	 * 
	 */
	public boolean start(Activity activity , String audioID, String path, boolean longAudio, int Belong, boolean useReg) {
		if(state==State.RECORDING){
			return false;
		}
		
		sendRecDoneFlag = false;
		isRecording = true;
		shortPath = path + "/" + audioID + "/" + audioID + ".mp4";
		fullPath = Environment.getExternalStorageDirectory() + shortPath;
		Log.e(TAG, "test begin filePath:" + fullPath + " useReg = " + useReg);
		

		this.audioID = audioID;
		
		this.longAudio = longAudio;
		this.Belong = Belong;
		
		if(!useReg){
			recordtype = RecordType.DEFAULT;

		}else{
			if(CheckPlugin()){
				recordtype = RecordType.XRECORDER;
			}else{
				recordtype = RecordType.DEFAULT;
			}
		}
		
		if(recordtype == RecordType.XRECORDER){
			recordingTask = new RecordAudio_TASK(longAudio, Belong, useReg);
			recordingTask.execute(audioID, path);
			
			
			if(useReg){
				sendMessage(QISR_TASK.HANDLER_QISR_RESULT_CLEAN, audioID, null); 
				qisr_task = new RecognizerThread();
				qisr_task.execute();
			}

		}else{
			//使用系统的mediaRecoder
//			handler.obtainMessage(HANDLER_AUDIO_RECORDER_VOLUME).sendToTarget();
			
			try {

				File file = new File(fullPath);
				if(!file.getParentFile().exists()) {
					file.getParentFile().mkdirs();
				}
				
				playtime = 0;
				last_time = System.currentTimeMillis();
				mediaRecorder =  new MediaRecorder();// 
				mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
				mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
				mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
				mediaRecorder.setAudioSamplingRate(16000);
				mediaRecorder.setAudioChannels(1);
				mediaRecorder.setOutputFile(file.getAbsolutePath());
				
				mediaRecorder.prepare();

				mediaRecorder.start();
				synchronized (state) {
					state = State.RECORDING;
				}


			} catch (IllegalStateException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return false;
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return false;
			}
		}
		return true;
	}

	/**
	 * 
	 * 
	 * 不用语音识别启动录音,保存文件路径为/path/audioID/audioID.mp4
	 * 
	 * @param audioID 文件名，不带.mp4
	 * @param path 父级目录,不带末级'/'
	 * 
	 */
	public boolean start(Activity activity, String audioID, String path, boolean longAudio, int Belong) {
		return start(activity, audioID, path, longAudio, Belong, false);
	}
	
	public  boolean resume() {
		synchronized (state) {
			if(state==State.RECORDING){
				return true;
			}
		}

		isRecording = true; //录音的生命周期没有结束
		last_time = System.currentTimeMillis();

		if(recordtype==RecordType.XRECORDER){
			synchronized (state) {
				state = State.RECORDING;
			}

			return true;
		}else{
			return false;
		}
		
	}
	
	public  boolean pause() {
		synchronized (state) {
			if(state==State.PAUSE){
				return true;
			}
		}

		
		isRecording = true; //录音的生命周期没有结束
		playtime = playtime + System.currentTimeMillis() - last_time;
		if(recordtype==RecordType.XRECORDER){
			synchronized (state) {
				state = State.PAUSE;
			}

			return true;
		}else{
			return false;
		}
		
	}
	
	public boolean canRecord(){
		synchronized (state) {
			if(state==State.RECORDING){
				return false;
			}else{
				return true;
			}
		}

	}
	
	public State getStatus(){
		return state;
	}

	/**
	 * 
	 * 
	 * Stops the recording, and sets the state to STOPPED. In case of further
	 * usage, a reset is needed. Also finalizes the wave file in case of
	 * uncompressed recording.
	 * 
	 */
	public  void stop() {
		synchronized (state) {
			if(state==State.STOPPED){
				return;
			}
		}

		isRecording = false;

/*		Mp4InfoUtils tmp4info = new Mp4InfoUtils(fullPath);
		long audioduration = (long)(tmp4info.totaltime * 1000);
		playtime = RoundPalyTime(audioduration);*/
		//long audioduration = System.currentTimeMillis() - last_time;
		//playtime = RoundPalyTime(playtime +  System.currentTimeMillis() - last_time);
		

		if(recordtype==RecordType.XRECORDER){
			
		}else{
			try {
				mediaRecorder.stop();
				mediaRecorder.release();
				mediaRecorder = null;
				synchronized (state) {
					state = State.STOPPED;
				}
				
				Mp4InfoUtils tmp4info = new Mp4InfoUtils(fullPath);
				long audioduration = (long)(tmp4info.totaltime * 1000);
				playtime = RoundPalyTime(audioduration);
				
				Intent intent = new Intent(AUDIO_RECORDER_MSG);
				// You can also include some extra data.
				intent.putExtra("type", HANDLER_AUDIO_RECORDER_DONE);
				intent.putExtra("content", audioID);
				intent.putExtra("useReg", false);
				Log.d(TAG,"playtime = " + playtime);
				if(playtime>0 && playtime <60000){
					intent.putExtra("playtime", playtime/1000 + "''");
				}else if(playtime>=60000 && playtime<3600000){
					intent.putExtra("playtime", playtime/60000 + "'" + (playtime/1000)%60 + "''");
				}

				Log.e(TAG, "ExtAudioRecord - OnStop - audioduration:" + audioduration);
				intent.putExtra("audioduration", audioduration);
				
				LocalBroadcastManager.getInstance(MainApplication.getAppInstance()).sendBroadcast(intent);
				
				long len = GetFileLen(fullPath);
				MediaValue result = new MediaValue();
				result.UID = ActiveAccount.getInstance(ZApplication.getInstance()).getLookLookID();
				result.Belong = Belong;
				result.Direction = 0;
				if(longAudio){
					result.MediaType = 4;
				}else{
					result.MediaType = 3;
				}
				result.url = null;
				result.path = shortPath;
				result.realSize = len;
				result.Sync = 2;
				result.SyncSize = 0;
				result.totalSize = len;
				AccountInfo.getInstance(result.UID).mediamapping.setMedia(result.UID, shortPath, result);
				
			} catch (IllegalStateException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}catch(Exception e){
				e.printStackTrace();
			}
		}
	}
	
	public static long GetFileLen(String filePath)  {
		long ret = 0;
		File file = new File(filePath);
		if(file.exists()){
			ret =  file.length();
		}else{
			ret = -1;
		}
		return ret;

	}

	 public  static boolean CheckPlugin(){
		return PluginUtils.isPluginMounted();
		//return false;
		//return EffectsDownloadUtil.getInstance(a).checkEffects();
		
/*		if(PluginUtils.isPluginMounted()){
			try{
				XAudioRecorder audioRecord = new XAudioRecorder(null);
				audioRecord = null;
				return true;
			}catch(Throwable e){
				e.printStackTrace();
			}
			
		}
		
		return false;*/
	}
	
	private long RoundPalyTime(long playtime){
		if(playtime%1000!=0){
			playtime+=1000;
		}
		
		playtime = playtime / 1000 * 1000;
		
		return playtime;
	}
	 
/*	private void sendMessage(int type, int result) {
		  Log.d(TAG, "sendMessage - type:" + type + ", result:" + result);
		  Intent intent = new Intent(AUDIO_RECORDER_MSG);
		  // You can also include some extra data.
		  intent.putExtra("type", type);
		  intent.putExtra("content", String.valueOf(result));

		  LocalBroadcastManager.getInstance(MainApplication.getAppInstance()).sendBroadcast(intent);
	}*/
	
/*	class MyMediaInfo implements XAudioRecorder.XAudioReaderInfoListener<Object>{
		
		@Override
		public void onStartRecorder(Object r, String path) {
			Log.i(TAG, "[onStartRecorder] path:"+path);
		}

		@Override
		public void onStopRecorder(Object r, String path) {
			Log.i(TAG, "[onStopRecorder] path:"+path);
		}

		@Override
		public void onSmallBoxComplete(Object r, String path) {
			Log.i(TAG, "[onSmallBoxComplete] path:"+path);
		}

		@Override
		public void onPauseRecorder(Object r) {
			Log.i(TAG, "[onPauseRecorder] path.");
		}

		@Override
		public void onResumeRecorder(Object r) {
			Log.i(TAG, "[onResumeRecorder] path.");
		}
		
	}*/

	
	private class RecordAudio_TASK extends Thread implements XAudioRecorder.XAudioReaderInfoListener {		
		private String audioID = null;
		private String recordPath = null;
		private String fullPath = null;
		private boolean useReg = false;
		/*private long playtime;*/
		private boolean longAudio;
		private int belong;
		private String path;
		private long localplaytime;
		
		public RecordAudio_TASK(boolean longAudio, int belong, boolean useReg){
			this.useReg = useReg;
			this.longAudio = longAudio;
			this.belong = belong;
			this.localplaytime = 0;
		}
		
		public void execute(String audioID, String path) {
			// TODO Auto-generated method stub
			this.audioID = audioID;
			this.path = path;
			this.start();
		}

		@Override
		public void run(){


			isRecording = true;
			
			shortPath = path + "/" + audioID + "/" + audioID + ".mp4";
			
			recordPath = Environment.getExternalStorageDirectory() + path;
			fullPath = Environment.getExternalStorageDirectory() + shortPath;
			
			//nio
			File file = new File( recordPath + "/" + audioID);
			if(!file.getParentFile().exists()) {
				file.getParentFile().mkdirs();
			}
						
			//audioRecord = new XAudioRecorder(new MyMediaInfo());
			audioRecord = new XAudioRecorder(this);
			int ret = audioRecord.start(audioID , recordPath);
			Log.e(TAG, "audioRecord.start -  ret:" + ret +  ", Status:" + audioRecord.getStatus() +  ", audioID:" + audioID + " fullPath:" + fullPath);

/*			localplaytime = System.currentTimeMillis();
			int volume = 0;
			int last_volume = 0;
			
			long cur_time = 0;
			long last_time =0 ;*/
			
			while (isRecording ) {
				if(state==ExtAudioRecorder.State.PAUSE){
					try{
						Thread.sleep(50);
					}catch(InterruptedException e){
						e.printStackTrace();
					}
					continue;
				}else if(state==ExtAudioRecorder.State.RECORDING){
					
				}
				
				//byte[] buffer = new byte[2*bufferSize];
				byte[] buffer = new byte[READ_BYTE_DATA_SIZE];
				
				//int bufferReadResult = audioRecord.read(buffer, 0, bufferSize);
				int bufferReadResult = audioRecord.read(buffer, READ_BYTE_DATA_SIZE);
				
				if(useReg){
					try {
						fifo.put(new AudioFrame(buffer, bufferReadResult, 1));//Normal
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} 
				}
				
				payloadSize += bufferReadResult;
				
				
				//calc 
				/*if(cur_time-last_time>500){
					int v = 0;
					for (int i = 0; i < bufferReadResult; i++) {
						v = v + buffer[i] * buffer[i];
						//volume = (int) (Math.abs((int)(v /(float)bufferReadResult) / 10000) >> 1);
						volume = Math.abs((int)(v /(float)bufferReadResult));
					}
					
					if(last_volume!=volume){
						sendMessage(HANDLER_AUDIO_RECORDER_VOLUME, volume);
						last_volume = volume;
					}
					
					last_time = cur_time;
				}*/
			}
			
/*			Intent intent = new Intent(AUDIO_RECORDER_MSG);
			// You can also include some extra data.
			intent.putExtra("type", HANDLER_AUDIO_RECORDER_DONE);
			intent.putExtra("content", audioID);
			intent.putExtra("useReg", useReg);
			
			long audioduration = System.currentTimeMillis() - localplaytime;
			localplaytime = RoundPalyTime(System.currentTimeMillis() - localplaytime);
			
			if(localplaytime>0 && localplaytime <60000){
				intent.putExtra("playtime", localplaytime/1000 + "''");
			}else if(localplaytime>=60000 && localplaytime<3600000){
				intent.putExtra("playtime", localplaytime/60000 + "'" + (localplaytime/1000)%60 + "''");
			}
			intent.putExtra("audioduration", audioduration);
			
			LocalBroadcastManager.getInstance(MainApplication.getAppInstance()).sendBroadcast(intent);

			*/
			audioRecord.stop();
			audioRecord.release();
			
	        sendRecDoneFlag = true;
			qisr_task.interrupt();
		
			//update mediaMapping
/*			long len = GetFileLen(fullPath);
			MediaValue mv = new MediaValue();
			mv.UID = ActiveAccount.getInstance(ZApplication.getInstance()).getLookLookID();
			mv.Belong = belong;
			mv.Direction = 0;
			if(longAudio){
				mv.MediaType = 4;
			}else{
				mv.MediaType = 3;
			}
			
			mv.url = null;
			mv.path = shortPath;
			mv.realSize = len;
			mv.Sync = 2;
			mv.SyncSize = 0;
			mv.totalSize = len;
			AccountInfo.getInstance(mv.UID).mediamapping.setMedia(mv.UID, shortPath, mv);*/
		
		}

		@Override
		public void onStartRecorder(Object r, String path) {
			// TODO Auto-generated method stub
			Log.e(TAG, "onStartRecorder - path:" + path);
			synchronized (state) {
				state = ExtAudioRecorder.State.RECORDING;
			}
			
			localplaytime = System.currentTimeMillis();

		}

		@Override
		public void onStopRecorder(Object r, String path) {
			// TODO Auto-generated method stub
			Log.e(TAG, "onStopRecorder - path:" + path);
			synchronized (state) {
				state = ExtAudioRecorder.State.STOPPED;
			}
			
			Intent intent = new Intent(AUDIO_RECORDER_MSG);
			// You can also include some extra data.
			intent.putExtra("type", HANDLER_AUDIO_RECORDER_DONE);
			intent.putExtra("content", audioID);
			intent.putExtra("useReg", useReg);
			
/*			long audioduration = System.currentTimeMillis() - localplaytime;
			localplaytime = RoundPalyTime(System.currentTimeMillis() - localplaytime);
		*/	
			Mp4InfoUtils tmp4info = new Mp4InfoUtils(fullPath);
			long audioduration = (long)(tmp4info.totaltime * 1000);
			localplaytime = RoundPalyTime(audioduration);
			
			if(localplaytime>0 && localplaytime <60000){
				intent.putExtra("playtime", localplaytime/1000 + "''");
			}else if(localplaytime>=60000 && localplaytime<3600000){
				intent.putExtra("playtime", localplaytime/60000 + "'" + (localplaytime/1000)%60 + "''");
			}
			intent.putExtra("audioduration", audioduration);
			
			LocalBroadcastManager.getInstance(MainApplication.getAppInstance()).sendBroadcast(intent);

			
			//update mediaMapping
			long len = GetFileLen(fullPath);
			MediaValue mv = new MediaValue();
			mv.UID = ActiveAccount.getInstance(ZApplication.getInstance()).getLookLookID();
			mv.Belong = belong;
			mv.Direction = 0;
			if(longAudio){
				mv.MediaType = 4;
			}else{
				mv.MediaType = 3;
			}
			
			mv.url = null;
			mv.path = shortPath;
			mv.realSize = len;
			mv.Sync = 2;
			mv.SyncSize = 0;
			mv.totalSize = len;
			AccountInfo.getInstance(mv.UID).mediamapping.setMedia(mv.UID, shortPath, mv);

		}

		@Override
		public void onSmallBoxComplete(Object r, String path) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onPauseRecorder(Object r) {
			// TODO Auto-generated method stub
			Log.e(TAG, "onStartRecorder - path:" + path);
		}

		@Override
		public void onResumeRecorder(Object r) {
			// TODO Auto-generated method stub
			
		}
		
		
	}
	
	private class RecognizerThread extends Thread{
		
		private AudioFrame af;

		public void execute() {
			// TODO Auto-generated method stub
			this.start();
		}
		
		public void run() {
			recognizer.recognizeStream(ExtAudioRecorder.this, "sms", null, null);
			
			while( !sendRecDoneFlag || !fifo.isEmpty()) {
				try {
					af = null;
					af = fifo.take();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				if(af!=null){
					recognizer.writeAudio(af.data ,0, af.len);
				}

				
				if(reset){
					recognizer.stopListening();
					recognizer.recognizeStream(ExtAudioRecorder.this, "sms", null, null);
					reset = false;
				}
			}
			
			recognizer.stopListening();
		}
		
	}


	@Override
	public boolean handleMessage(Message msg) {
		// TODO Auto-generated method stub
		switch(msg.what){
/*		case HANDLER_AUDIO_RECORDER_VOLUME:
			if(isRecording){
				sendMessage(HANDLER_AUDIO_RECORDER_VOLUME, (int) (2000 + 1000*Math.random()));
				this.handler.sendEmptyMessageDelayed(HANDLER_AUDIO_RECORDER_VOLUME,  200);
			}
			break;*/
		}
		return false;
	}

	@Override
	public void onBeginOfSpeech() {
		// TODO Auto-generated method stub
		Log.e(TAG, "onBeginOfSpeech:" + audioID);
	}

	@Override
	public void onCancel() {
		// TODO Auto-generated method stub
		Log.e(TAG, "onCancel:" + audioID);
		if(!sendRecDoneFlag || !fifo.isEmpty()){
			
		}else{
			sendMessage(QISR_TASK.HANDLER_QISR_RESULT_DONE, audioID, null); 
		}

	}

	@Override
	public void onEnd(SpeechError arg0) {
		// TODO Auto-generated method stub
		Log.e(TAG, "onEnd:" + audioID + ", sendRecDoneFlag:" + sendRecDoneFlag + ", fifo.isEmpty():" + fifo.isEmpty());
		if(!sendRecDoneFlag || !fifo.isEmpty()){
			reset = true;
		}else{
			reset = false;
			sendMessage(QISR_TASK.HANDLER_QISR_RESULT_DONE, audioID, null); 

		}

	}

	@Override
	public void onEndOfSpeech() {
		// TODO Auto-generated method stub
		Log.e(TAG, "onEndOfSpeech:" + audioID);
		//sendMessage(QISR_TASK.HANDLER_QISR_RESULT_DONE, audioID, null); 
	}

	@Override
	public void onResults(ArrayList<RecognizerResult> results, boolean isLast) {
		// TODO Auto-generated method stub
		Log.e(TAG, "onResults:" + audioID);
		String text = "";
		for(int i = 0; i < results.size(); i++)
		{
			RecognizerResult result = (RecognizerResult)results.get(i);
			text += result.text;
		}
		
		sendMessage(QISR_TASK.HANDLER_QISR_RESULT_ADD, audioID, text); 
		
	}

	private void sendMessage(int type, String audioID , String result) {
		// TODO Auto-generated method stub
		  Log.d(TAG, "sendMessage - type:" + type + ", audioID:" + audioID + ", result:" + result);
		  Intent intent = new Intent(QISR_TASK.QISR_RESULT_MSG);
		  // You can also include some extra data.
		  intent.putExtra("type", type);
		  intent.putExtra("audioID", audioID);
		  
		  if(result!=null){
			  intent.putExtra("content", result);
		  }

		  LocalBroadcastManager.getInstance(MainApplication.getAppInstance()).sendBroadcast(intent);
	
	}

	@Override
	public void onVolumeChanged(int arg0) {
		// TODO Auto-generated method stub
		
	}


}