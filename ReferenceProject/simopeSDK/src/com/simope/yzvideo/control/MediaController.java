package com.simope.yzvideo.control;


import android.content.Context; 
import android.view.View;
import android.widget.FrameLayout;
import com.simope.yzvideo.control.listener.ControllerPlayListener;



public abstract class MediaController extends FrameLayout 
{

		
	public MediaController(Context context) {
		super(context);	
	}	
	
	public abstract void setControllerPlayListener(
			ControllerPlayListener controllerPlayListener);				//设置监听控制事件监听
	
	public abstract void show();										//显示控制条
	
	public abstract void show(int timeout);								//显示控制条在timeout后自动隐藏
	
	public abstract void hide();										//隐藏控制条
		
	public abstract void setAnchorView(View view);						//设置基于的基础界面
	
	public abstract void setMediaPlayer(MediaPlayerControl player);		//设置可控制的播放器
		
	public interface MediaPlayerControl {
		
		void start();
		
		void pause();

		int getDuration();

		int getCurrentPosition();

		void seekTo(int pos);

		boolean isPlaying();

		int getBufferPercentage();

		boolean canPause();

		boolean canSeekBackward();

		boolean canSeekForward();

		void setQuality(String path, int time);

		void release(boolean cleartargetstate);

		boolean isInPlaybackState();
	}
}