package com.cmmobi.looklook.common.utils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.media.MediaRecorder;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

public class SoundRecorderUtil {

	private AudioRecord audioRecord;
	private DataOutputStream dos;
	private File file;
	private Handler handler;
	
	int volume;

	boolean isRecording = false;
	int frequency = 44100;
	int channelConfiguration = AudioFormat.CHANNEL_CONFIGURATION_MONO;
	int audioEncoding = AudioFormat.ENCODING_PCM_16BIT;
	int bufferSize = AudioRecord.getMinBufferSize(frequency,
			channelConfiguration, audioEncoding);
	short[] buffer = new short[bufferSize];

	public SoundRecorderUtil(Handler handler){
		this.handler = handler;
	}
	
	public String getSoundFilePath() {
		if (file.exists()) {
			return file.getAbsolutePath().toString();
		} else {
			return " ";
		}
	}

	public void record() {

		file = new File(Environment.getExternalStorageDirectory()
				.getAbsolutePath() + "/123456789.amr");
        Log.e("path", Environment.getExternalStorageDirectory()
				.getAbsolutePath() + "");
		// Delete any previous recording.
		if (file.exists())
			file.delete();

		try {
			file.createNewFile();
		} catch (IOException e) {
			throw new IllegalStateException("Failed to create "
					+ file.toString());
		}

		try {
			// Create a DataOuputStream to write the audio data into the saved
			// file.
			OutputStream os = new FileOutputStream(file);
			BufferedOutputStream bos = new BufferedOutputStream(os);
			dos = new DataOutputStream(bos);

			// Create a new AudioRecord object to record the audio.

			audioRecord = new AudioRecord(MediaRecorder.AudioSource.MIC,
					frequency, channelConfiguration, audioEncoding, bufferSize);

			audioRecord.startRecording();

			new Thread() {
				public void run() {
					isRecording = true;
					while (isRecording) {
						int bufferReadResult = audioRecord.read(buffer, 0,
								bufferSize);
						int v = 0;
						for (int i = 0; i < bufferReadResult; i++) {
							v = v + buffer[i] * buffer[i];
							 volume = (int) (Math.abs((int)(v /(float)bufferReadResult) / 10000) >> 1);
							try {
								dos.writeShort(buffer[i]);
							} catch (IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
					}
				};
			}.start();
			
			new Thread(){
				public void run() {
					Message msg = handler.obtainMessage();
					 msg.what = getVolumeArea(volume);
					 handler.sendMessage(msg);
					try {
						sleep(200);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				};
			};

		} catch (Throwable t) {
			Log.e("AudioRecord", "Recording Failed");
		}
	}

	private int getVolumeArea(int volume){
		if(volume <= 25 ){
			return 25;
		}else if(volume > 25 && volume <= 50 ){
			return 50;
		}else if(volume > 50 && volume <= 75 ){
			return 75;
		}else if(volume > 75 && volume <=100 ){
			return 100;
		}else{
			return -1;
		}
	}
	
	public void play(String path) {
		// Get the file we want to playback.
		File file = new File(path);
		// Get the length of the audio stored in the file (16 bit so 2 bytes per
		// short)
		// and create a short array to store the recorded audio.
		int musicLength = (int) (file.length() / 2);
		short[] music = new short[musicLength];

		try {
			// Create a DataInputStream to read the audio data back from the
			// saved file.
			InputStream is = new FileInputStream(file);
			BufferedInputStream bis = new BufferedInputStream(is);
			DataInputStream dis = new DataInputStream(bis);

			// Read the file into the music array.
			int i = 0;
			while (dis.available() > 0) {
				music[i] = dis.readShort();
				i++;
			}
			// Close the input streams.
			dis.close();
			// Create a new AudioTrack object using the same parameters as the
			// AudioRecord
			// object used to create the file.
			AudioTrack audioTrack = new AudioTrack(AudioManager.STREAM_MUSIC,
					44100, AudioFormat.CHANNEL_IN_MONO,
					AudioFormat.ENCODING_PCM_16BIT, musicLength * 2,
					AudioTrack.MODE_STREAM);
			// Start playback
			audioTrack.play();
			// Write the music buffer to the AudioTrack object
			audioTrack.write(music, 0, musicLength);
			audioTrack.stop();
		} catch (Throwable t) {
			Log.e("AudioTrack", "Playback Failed");
		}
	}

	public void stop() {
		try {
			audioRecord.stop();
			dos.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
