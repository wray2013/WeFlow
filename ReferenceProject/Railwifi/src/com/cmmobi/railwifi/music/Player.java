package com.cmmobi.railwifi.music;

import java.io.IOException;
import java.util.Timer;

import com.cmmobi.railwifi.MainApplication;
import com.cmmobi.railwifi.view.MusicControllerView;

import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnBufferingUpdateListener;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnErrorListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.util.Log;

public class Player implements OnBufferingUpdateListener, /*OnCompletionListener,*/
		OnPreparedListener/* , OnErrorListener*/{

	private static final String TAG = "Player";
	public MediaPlayer mediaPlayer; // 媒体播放器

	// 初始化播放器
	public Player() {
		super();

		try {
			mediaPlayer = new MediaPlayer();
			mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);// 设置媒体流类型
//			mediaPlayer.setOnBufferingUpdateListener(this);
			mediaPlayer.setOnPreparedListener(this);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	
	public void resume() {
		// TODO Auto-generated method stub
		mediaPlayer.start();
	}

	/**
	 * 
	 * @param url url地址
	 */
	public void playUrl(String url) {
//		Log.v(TAG, "playUrl - url:" + url);
		try {
			mediaPlayer.reset();
//			Log.v(TAG, "setDataSource - url:" + url);
			mediaPlayer.setDataSource(url); // 设置数据源
//			Log.v(TAG, "prepare - url:" + url);
//			mediaPlayer.prepare(); // prepare自动播放
			mediaPlayer.prepareAsync();
//			Log.v(TAG, "start - url:" + url);
//			mediaPlayer.start();
//			Log.v(TAG, "start - done:" + url);
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (IllegalStateException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// 暂停
	public void pause() {
		mediaPlayer.pause();
	}

	// 停止
	public void stop() {
		if (mediaPlayer != null) {
			mediaPlayer.stop();
			mediaPlayer.reset();
//			mediaPlayer.release();
//			mediaPlayer = null;
		}
	}

	// 播放准备
	@Override
	public void onPrepared(MediaPlayer mp) {
		mp.start();
//		Log.e("mediaPlayer", "onPrepared");
		Intent intent = new Intent(MusicControllerView.BROADCAST_MUSIC_PLAY);
		MainApplication.getAppInstance().sendBroadcast(intent);		
		mediaPlayer.start();
	}

//	// 播放完成
//	@Override
//	public void onCompletion(MediaPlayer mp) {
////		Log.e("mediaPlayer", "onCompletion");
//	}

	/**
	 * 缓冲更新
	 */
	@Override
	public void onBufferingUpdate(MediaPlayer mp, int percent) {
//		Log.e("onBufferingUpdate :", percent + " buffer");
	}



	public Boolean isPlaying() {
		// TODO Auto-generated method stub
		return mediaPlayer.isPlaying();
	}



	public void setOnCompletionListener(
			OnCompletionListener onCompletionListener) {
		// TODO Auto-generated method stub
		mediaPlayer.setOnCompletionListener(onCompletionListener);
	}
	
	public void setOnErrorListener(
			OnErrorListener onErrorListener) {
		// TODO Auto-generated method stub
		mediaPlayer.setOnErrorListener(onErrorListener);
	}


//	@Override
//	public boolean onError(MediaPlayer mp, int what, int extra) {
//		// TODO Auto-generated method stub
//		return true;
//	}





}
