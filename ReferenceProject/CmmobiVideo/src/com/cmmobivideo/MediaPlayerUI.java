package com.cmmobivideo;

import xMediaPlayer.XMediaPlayer;
import xMediaPlayer.XMediaPlayer.OnXMediaPlayerStateChangedListener;
import android.content.Context;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

import com.cmmobivideo.R;

import effect.EffectType;


public class MediaPlayerUI implements XMediaPlayer.OnXMediaPlayerStateChangedListener, OnClickListener, OnSeekBarChangeListener, AnimationListener {
	
	
	private static final int BUTTONS_AUTO_HIDE_MILLIS = 3500; // 3.5秒;
	
	private XMediaPlayer player;
	private RelativeLayout mBgLayout;
	private LinearLayout mButtonsWrapLayout;
	private ImageView mBtnPlaying;
	private TextView mPlayerTimer;
	private SeekBar mPlayerSeek;
	
	private Handler handler;
	private Runnable autoHideButtonsRunnable;
	private Animation hideAnimation;
	private Animation showAnimation;
	private boolean buttonsIsVisible;
	
	
	public MediaPlayerUI(Context context, XMediaPlayer player) {
		this.player = player;
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View view = inflater.inflate(R.layout.x_media_player, null);
		
		mBgLayout = (RelativeLayout) view.findViewById(R.id.shell);
		mBgLayout.setOnClickListener(this);
		mBgLayout.addView(player.getGLLayer(), RelativeLayout.LayoutParams.FILL_PARENT, RelativeLayout.LayoutParams.FILL_PARENT);

		mButtonsWrapLayout = (LinearLayout) view.findViewById(R.id.buttons);
		mButtonsWrapLayout.setOnClickListener(this);
		mButtonsWrapLayout.bringToFront();

		mBtnPlaying = (ImageView) view.findViewById(R.id.play_button);
		mBtnPlaying.setOnClickListener(this);

		mPlayerSeek = (SeekBar) view.findViewById(R.id.seek_bar);
		mPlayerSeek.setOnSeekBarChangeListener(this);

		mPlayerTimer = (TextView) view.findViewById(R.id.timer);

		hideAnimation = AnimationUtils.loadAnimation(context, R.anim.x_media_player_buttons_hide_animation);
		hideAnimation.setAnimationListener(this);
		showAnimation = AnimationUtils.loadAnimation(context, R.anim.x_media_player_buttons_show_animation);
		showAnimation.setAnimationListener(this);

		buttonsIsVisible = true;
		mBgLayout.requestLayout();
		
		autoHideButtonsRunnable = new Runnable() {
			@Override
			public void run() {
				switchButtonsShowState();
			}
		};
		
		handler = new Handler();
		handler.postDelayed(autoHideButtonsRunnable, BUTTONS_AUTO_HIDE_MILLIS);
	}

	public RelativeLayout getLayout() {
		return mBgLayout;
	}

	private void switchButtonsShowState() {
		if (buttonsIsVisible) {
			mButtonsWrapLayout.startAnimation(hideAnimation);
			
			mBtnPlaying.setClickable(false);
			mPlayerSeek.setClickable(false);
			
			buttonsIsVisible = false;
			handler.removeCallbacks(autoHideButtonsRunnable);
			
		} else {
			mButtonsWrapLayout.setVisibility(View.VISIBLE);
			mButtonsWrapLayout.scrollTo(0, 0);
			mButtonsWrapLayout.startAnimation(showAnimation);
			
			mBtnPlaying.setClickable(true);
			mPlayerSeek.setClickable(true);
			
			buttonsIsVisible = true;
			timingToAutoHideButtons();
		}
	}
	
	private void timingToAutoHideButtons() {
		handler.removeCallbacks(autoHideButtonsRunnable);
		handler.postDelayed(autoHideButtonsRunnable, BUTTONS_AUTO_HIDE_MILLIS);
	}
	
	@Override
	public void onClick(View v) {

		switch(v.getId()) {
		
		case R.id.play_button:
			if (!player.isOpen()) {
				player.open("/mnt/sdcard/rrrr1.mp4");
			}
			if (player.isPlaying()) {
				player.pause();
				v.setBackgroundResource(R.drawable.x_media_player_play_selector);
				
			} else if (player.isOpen() || player.isPause()) {
				player.play();
				v.setBackgroundResource(R.drawable.x_media_player_stop_selector);
			}
			timingToAutoHideButtons();
			break;
			
		case R.id.shell:
			switchButtonsShowState();
			break;
			
		case R.id.buttons:
			if (buttonsIsVisible) {
				timingToAutoHideButtons();
			} else {
				switchButtonsShowState();
			}
			break;
		}
	}
	
	/**
	 * 当MediaPlayer的播放状态发生变化时, 会回调此方法, 用来更新UI;
	 */
	@Override
	public void OnPlayStateChanged(XMediaPlayer player) {
		mPlayerTimer.setText(player.getProgressTimeText() + "/" + player.getTotalTimeText());
		mPlayerSeek.setProgress(player.getPercent());
		
		if (player.isPlaying()) {
			mBtnPlaying.setBackgroundResource(R.drawable.x_media_player_stop_selector);
		} else {
			mBtnPlaying.setBackgroundResource(R.drawable.x_media_player_play_selector);
		}
	}
	
	@Override
	public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
		if (fromUser) {
			player.onDragSeekBar(seekBar);
			timingToAutoHideButtons();
		}
	}

	@Override
	public void onStartTrackingTouch(SeekBar seekBar) {
	}

	@Override
	public void onStopTrackingTouch(SeekBar seekBar) {
		player.seek(seekBar);
		player.play();
	}
	
	@Override
	public void onAnimationEnd(Animation animation) {
		if (!buttonsIsVisible) {
			mButtonsWrapLayout.setVisibility(View.INVISIBLE);
			mButtonsWrapLayout.scrollTo(0, mButtonsWrapLayout.getHeight());
		}
	}

	@Override
	public void onAnimationRepeat(Animation animation) {
		// TODO Auto-generated method stub
	}

	@Override
	public void onAnimationStart(Animation animation) {
		// TODO Auto-generated method stub
	}


}
