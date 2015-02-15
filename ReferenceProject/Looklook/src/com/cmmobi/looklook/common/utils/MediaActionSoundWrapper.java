package com.cmmobi.looklook.common.utils;

import android.annotation.TargetApi;
import android.media.MediaActionSound;
import android.os.Build;

public class MediaActionSoundWrapper {
	
	
	private MediaActionSoundWrapper() {
	}
	
	@TargetApi(Build.VERSION_CODES.JELLY_BEAN) // 版本号大于等于4.1 则有效.
	public static void takePicture() {
		if (Build.VERSION.SDK_INT > 16) {
			new MediaActionSound().play(MediaActionSound.SHUTTER_CLICK);
		}
	}
	
	@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
	public static void startVideoRecording() {
		if (Build.VERSION.SDK_INT > 16) {
			new MediaActionSound().play(MediaActionSound.START_VIDEO_RECORDING);
		}
	}
	
	@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
	public static void stopVideoRecording() {
		if (Build.VERSION.SDK_INT > 16) {
			new MediaActionSound().play(MediaActionSound.START_VIDEO_RECORDING);
		}
	}

}
