package com.cmmobi.looklook.common.listener;

import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;

import cn.zipper.framwork.core.ZDialog;

import com.cmmobivideo.workers.XMediaPlayer;
import com.cmmobivideo.workers.XMediaPlayer.XPlayerWaveformListener;


public class MyWaveformListener implements XPlayerWaveformListener {

	private final String TAG = "MyWaveformListener";
	private boolean isAddWaveformView = false;
	private boolean isNeedWait = false;
	private XMediaPlayer mMediaPlayer = null;
	private RelativeLayout waveLayout = null;
	
	public MyWaveformListener(XMediaPlayer player, RelativeLayout layout) {
		mMediaPlayer = player;
		waveLayout =layout;
	}
	

	public boolean isNeedWait() {
		return isNeedWait;
	}


	public void setNeedWait(boolean isNeedWait) {
		this.isNeedWait = isNeedWait;
	}


	@Override
	public void onWaveformDataPrepared() {
		// TODO Auto-generated method stub
		Log.e(TAG, "[onWaveformDataPrepared] in isNeedWait = " + isNeedWait);
		if(isAddWaveformView){
			mMediaPlayer.reloadWaveform();
			if (isNeedWait) {
				ZDialog.dismiss();
			}
			return;
		}
		isAddWaveformView = true;
		View view = mMediaPlayer.getWaveformView();
		if(view == null){
			Log.e(TAG, "[addWaveformView]getWaveformView is null");
			return;
		}
		RelativeLayout.LayoutParams ViewParams = new RelativeLayout.LayoutParams(LayoutParams.FILL_PARENT,LayoutParams.FILL_PARENT);
		view.setLayoutParams(ViewParams);
		
		waveLayout.addView(view);
		
		if (isNeedWait) {
			ZDialog.dismiss();
		}
	}

}
