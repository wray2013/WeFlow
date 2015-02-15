package com.cmmobi.looklook.activity;

import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout.LayoutParams;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import cn.zipper.framwork.core.ZActivity;

import com.cmmobi.looklook.R;
import com.cmmobi.looklook.activity.DiaryDetailActivity.EPlayStatus;
import com.cmmobi.looklook.common.utils.DateUtils;
import com.cmmobi.looklook.common.web.WebImageView;
import com.cmmobi.looklook.prompt.Prompt;
import com.cmmobivideo.workers.XMediaPlayer;
import com.cmmobivideo.workers.XMediaPlayer.OnInfoListener;

import effect.XEffectMediaPlayer;
import effect.XEffects;

/**
 * Copyright (C) ......
 * All rights reserved.
 * @package com.cmmobi.looklook.activity
 * @filename ExtPlayerActivity.java
 * @summary 全屏播放Activity
 * @author lanhai
 * @date 2013-9-5
 * @version 1.0
 */
public class ExtPlayerActivity extends ZActivity
{
	
	private final static String TAG = "ExtPlayerActivity";
	
	private final boolean systemOrPlug = true; // system true plug false 
	
	public static final int HANDLER_UPDATE_LONG_RECORD_PLAYER_PROCESS = 0x0010;
	public static final int HANDLER_UPDATE_LONG_RECORD_PLAYER_COMPLETE = 0x0011;
	public static final int HANDLER_AUTO_HIDE_CONTROLLER = 0x0014;
	public static final int HANDLER_UPDATE_VIDEO_TIME_STRING = 0x0015;
	public static final int HANDLER_UPDATE_VIDEO_ERROR = 0x0016;
	public static final int HANDLER_UPDATE_VIDEO_PREPARED = 0x0017;
	public static final int HANDLER_SET_VIDEO_POSITION = 0x0018;

	private ImageView mIvPlay = null;
	private RelativeLayout mRlVideoContent = null;
	private WebImageView mVideoThumbnail = null;
	private SeekBar mSkProcess = null;
	private TextView mTvVideoTime = null;
	private ImageView mIvRestore = null;
	
	String mStrVideoUrl = null;
	double mDVideoTime = 0d;
	String mStringThumbUrl = null;
	
	//播放时间等控件
	private XMediaPlayer mMediaPlayer = null;
	
	private EPlayStatus ePlayStatus = EPlayStatus.NON;
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_extplayer);
		
		setContent();
		
		setListener();
		
//		Intent intent = getIntent();
//		mStrVideoUrl = intent.getStringExtra(DiaryDetailActivity.INTENT_EXTRA_PLAY_URL);
//		String totalTime = intent.getStringExtra(DiaryDetailActivity.INTENT_EXTRA_PLAY_TOTALTIME);
//		mStringThumbUrl = intent.getStringExtra(DiaryDetailActivity.INTENT_EXTRA_PLAY_THUMB);
//		
//		double pos = 0d;
//		if(intent.hasExtra(DiaryDetailActivity.INTENT_EXTRA_PLAY_CURTIME))
//		{
//			pos = intent.getDoubleExtra(DiaryDetailActivity.INTENT_EXTRA_PLAY_CURTIME, 0d);
//		}
		
//		Log.v(TAG, "pos = "+pos);
//		Log.v(TAG, "mStrVideoUrl = "+mStrVideoUrl);
//		Log.v(TAG, "totalTime = "+totalTime);
//		Log.v(TAG, "mThumbUrl = "+mStringThumbUrl);
		
		mStrVideoUrl = DiaryDetailActivity.sMediaStatus.playPath;
		mStringThumbUrl = DiaryDetailActivity.sMediaStatus.thumbUrl;
		
		//播放时间
//		mTvVideoTime.setText("00:00:00/"+DateUtils.getFormatTime(mDVideoTime));
		mTvVideoTime.setVisibility(View.INVISIBLE);
//		mTvVideoTime.setText("00:00:00/"+totalTime);
		if (mStrVideoUrl != null && mStrVideoUrl.length() > 0)
		{
			mVideoThumbnail.setImageUrl(0, 1, mStringThumbUrl, false);
			mVideoThumbnail.setVisibility(View.VISIBLE);
		}
		
		if(null == mMediaPlayer && mRlVideoContent != null)
		{
			mRlVideoContent.removeAllViews();
			XEffects mEffects = new XEffects();
			mMediaPlayer = new XMediaPlayer(this, mEffects, !systemOrPlug);
			mMediaPlayer.setListener(new PlayOnInfoListener());
			RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(LayoutParams.FILL_PARENT,LayoutParams.FILL_PARENT);
			mRlVideoContent.addView((View)mMediaPlayer.getXSurfaceView(), params);
		}
		
		getHandler().sendEmptyMessage(HANDLER_SET_VIDEO_POSITION);
		
	}
	
	private void setContent()
	{
		mIvPlay=(ImageView) findViewById(R.id.iv_play);
		mRlVideoContent = (RelativeLayout) findViewById(R.id.rl_video_content);
		mVideoThumbnail = (WebImageView) findViewById(R.id.iv_video_thumbnail);
		mSkProcess=(SeekBar) findViewById(R.id.sk_diarydetail_seek);
		mSkProcess.setMax(100);
		mSkProcess.setProgress(0);
		mTvVideoTime=(TextView) findViewById(R.id.tv_activity_shiping_shijian);
		mIvRestore = (ImageView) findViewById(R.id.iv_activity_shiping_quanping);
	}
	
	private void setListener()
	{
		mIvPlay.setOnClickListener(this);
		mRlVideoContent.setOnClickListener(this);
		mIvRestore.setOnClickListener(this);
		
		mSkProcess.setOnSeekBarChangeListener(new OnSeekBarChangeListener()
		{
			
			@Override
			public void onStopTrackingTouch(SeekBar seekBar)
			{
				if(mMediaPlayer != null)
				{
					double time = mDVideoTime * (seekBar.getProgress() / 100d);
					if(ePlayStatus == EPlayStatus.PLAY)
					{
						mMediaPlayer.seek(time);
					}
					else if(ePlayStatus == EPlayStatus.PAUSE)
					{
						if(systemOrPlug)
						{
							mMediaPlayer.resume();
						}
						mMediaPlayer.seek(time);
						if(systemOrPlug)
						{
							mMediaPlayer.pause();
						}
					}
				}
			}
			
			@Override
			public void onStartTrackingTouch(SeekBar seekBar)
			{
			}
			
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser)
			{
			}
		});
		
	}
	
	@Override
	protected void onStop()
	{
		if(ePlayStatus == EPlayStatus.PLAY)
		{
			pauseVideo();
			if(getHandler().hasMessages(HANDLER_AUTO_HIDE_CONTROLLER))
			{
				getHandler().removeMessages(HANDLER_AUTO_HIDE_CONTROLLER);
			}
		}
		setPlayBtnStatus(mIvPlay.isShown());
		super.onStop();
	}

	@Override
	protected void onDestroy()
	{
		if(mMediaPlayer != null /*&& systemOrPlug*/)
		{
			mMediaPlayer.stop();
			mMediaPlayer.release();
		}
		super.onDestroy();
	}

	@Override
	public boolean handleMessage(Message msg)
	{
		switch (msg.what)
		{
		case HANDLER_UPDATE_LONG_RECORD_PLAYER_COMPLETE:
			stopVideo();
			break;
		case HANDLER_UPDATE_LONG_RECORD_PLAYER_PROCESS:
			if (mSkProcess != null)
				mSkProcess.setProgress((Integer) msg.obj);
			break;
		case HANDLER_UPDATE_VIDEO_TIME_STRING:
			mTvVideoTime.setText((String)msg.obj);
			if(mIvPlay.isShown())
			{
				mTvVideoTime.setVisibility(View.VISIBLE);
			}
			break;
		case HANDLER_UPDATE_VIDEO_PREPARED:
			if((Boolean)msg.obj)
			{
				
				ePlayStatus = EPlayStatus.OPENED;
				mMediaPlayer.play();
				ePlayStatus = EPlayStatus.PLAY;
				if(DiaryDetailActivity.sMediaStatus.status == EPlayStatus.PLAY
						|| DiaryDetailActivity.sMediaStatus.status == EPlayStatus.PAUSE)
				{
					mMediaPlayer.seek(DiaryDetailActivity.sMediaStatus.curTime);
				}
				if(DiaryDetailActivity.sMediaStatus.status == EPlayStatus.PAUSE)
				{
					pauseVideo();
				}
				DiaryDetailActivity.sMediaStatus = new DiaryDetailActivity.MediaStatus();
				
//				Animation anim = AnimationUtils.loadAnimation(DiaryDetailActivity.this, R.anim.video_thumb_out);
//				mVideoThumbnail.startAnimation(anim);
				mVideoThumbnail.setVisibility(View.INVISIBLE);
			}
			else
			{
				Prompt.Alert("您的网络不给力呀");
				stopVideo();
			}
			break;
		case HANDLER_UPDATE_VIDEO_ERROR:
			Prompt.Alert("视频出错");
			stopVideo();
			break;
		case HANDLER_AUTO_HIDE_CONTROLLER:
			setPlayBtnStatus(false);
			break;
		case HANDLER_SET_VIDEO_POSITION:
			if(mStrVideoUrl.startsWith("http") && !DiaryDetailActivity.isNetworkConnected(this, mStrVideoUrl, 5))
			{
				Prompt.Alert("您的网络不给力呀");
				break;
			}
			playVideo();
			break;
		default :
			break;
		}
		return false;
	}

	@Override
	public void onClick(View v)
	{
		switch(v.getId())
		{
		case R.id.iv_play:
			if(ePlayStatus == EPlayStatus.PLAY)
			{
				pauseVideo();
				if(getHandler().hasMessages(HANDLER_AUTO_HIDE_CONTROLLER))
				{
					getHandler().removeMessages(HANDLER_AUTO_HIDE_CONTROLLER);
				}
			}
			else
			{
				if(mStrVideoUrl.startsWith("http") && !DiaryDetailActivity.isNetworkConnected(this, mStrVideoUrl, 5))
				{
					Prompt.Alert("您的网络不给力呀");
					break;
				}
				playVideo();
			}
			setPlayBtnStatus(mIvPlay.isShown());
			break;
		case R.id.iv_activity_shiping_quanping:
			finish();
			break;
		case R.id.rl_video_content:
			boolean show = mIvPlay.isShown();
			if(!show)
			{
				if(getHandler().hasMessages(HANDLER_AUTO_HIDE_CONTROLLER))
				{
					getHandler().removeMessages(HANDLER_AUTO_HIDE_CONTROLLER);
				}
				getHandler().sendEmptyMessageDelayed(HANDLER_AUTO_HIDE_CONTROLLER, 4000);
			}
			setPlayBtnStatus(!show);
			break;
		default :
			break;
		}
		
	}

	// 播放视频
	private void playVideo(){
//		String videoCover = getVideoCover();
//		if (videoCover != null && videoCover.length() > 0) {
//			ivVideoThumbnail.setImageUrl(0, 1, videoCover, false);
//		}
		if(null == mMediaPlayer && mRlVideoContent != null)
		{
			mRlVideoContent.removeAllViews();
			XEffects mEffects = new XEffects();
			mMediaPlayer = new XMediaPlayer(this, mEffects, !systemOrPlug);
			mMediaPlayer.setListener(new PlayOnInfoListener());
			RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(LayoutParams.FILL_PARENT,LayoutParams.FILL_PARENT);
			mRlVideoContent.addView((View)mMediaPlayer.getXSurfaceView(), params);
		}
		if(mMediaPlayer != null && mStrVideoUrl != null)
		{
			if (mMediaPlayer.getStatus() == XEffectMediaPlayer.STATUS_UNKNOW && ePlayStatus == EPlayStatus.NON)
			{
				mMediaPlayer.open(mStrVideoUrl);
				mIvPlay.setVisibility(View.INVISIBLE);
				mIvPlay.setBackgroundResource(R.drawable.btn_activity_dairydetail_shiping_pause);
				ePlayStatus = EPlayStatus.OPENING;
			} else if (mMediaPlayer.getStatus() == XEffectMediaPlayer.STATUS_PAUSE) {
				mMediaPlayer.resume();
				mIvPlay.setVisibility(View.INVISIBLE);
				mIvPlay.setBackgroundResource(R.drawable.btn_activity_dairydetail_shiping_pause);
				ePlayStatus = EPlayStatus.PLAY;
			}
		}
		else
		{
			Log.e(TAG, "mMediaPlayer is null");
		}
	}
	
	// 停止视频
	private void stopVideo(){
		if(mMediaPlayer != null)
		{
//			mVideoThumbnail.clearAnimation();
			mVideoThumbnail.setVisibility(View.VISIBLE);
//			mMediaPlayer.stop();
//			mMediaPlayer.release();
			mMediaPlayer = null;
			if(mIvPlay != null)
			{
				mIvPlay.setBackgroundResource(R.drawable.btn_activity_dairydetail_shiping_pause_play);
				mIvPlay.setVisibility(View.VISIBLE);
			}
			ePlayStatus = EPlayStatus.NON;
		}else{
			Log.e(TAG, "mMediaPlayer is null");
		}
		
		if (mSkProcess != null)
		{
			mSkProcess.setProgress(0);
		}
		
		// 结尾会出现乱码
		if (mTvVideoTime != null)
		{
//			mTvVideoTime.setText("00:00:00/"+DateUtils.getFormatTime(mDVideoTime));
//			mTvVideoTime.setVisibility(View.INVISIBLE);
			mTvVideoTime.setText("00:00:00/"+DateUtils.getFormatTime(mDVideoTime));
		}
		// 标记归位
		if(mIvPlay != null)
		{
			mIvPlay.setBackgroundResource(R.drawable.btn_activity_dairydetail_shiping_pause_play);
		}
		setPlayBtnStatus(true);
		
	}
	
	// 暂停视频
	private void pauseVideo(){
		if(mMediaPlayer != null){
			if (mMediaPlayer.getStatus() == XEffectMediaPlayer.STATUS_PALYING) {
				mMediaPlayer.pause();
				mIvPlay.setBackgroundResource(R.drawable.btn_activity_dairydetail_shiping_pause_play);
				mIvPlay.setVisibility(View.VISIBLE);
				ePlayStatus = EPlayStatus.PAUSE;
			}
		}else{
			Log.e(TAG, "mMediaPlayer is null");
		}
	}
	
	//设置播放按钮状态
	private void setPlayBtnStatus(boolean show){
		if (mIvPlay != null && mSkProcess != null && mTvVideoTime != null &&
				mIvRestore != null)
		{
			if (show)
			{
				mIvPlay.setVisibility(View.VISIBLE);
				mSkProcess.setVisibility(View.VISIBLE);
//				if(mDVideoTime != 0d)
//				{
					mTvVideoTime.setVisibility(View.VISIBLE);
//				}
				mIvRestore.setVisibility(View.VISIBLE);
			}
			else
			{
				mIvPlay.setVisibility(View.INVISIBLE);
				mSkProcess.setVisibility(View.INVISIBLE);
				mTvVideoTime.setVisibility(View.INVISIBLE);
				mIvRestore.setVisibility(View.INVISIBLE);
			}
		}
	}
	
	private class PlayOnInfoListener implements OnInfoListener {

		@Override
		public void onStartPlayer(XMediaPlayer player) {
			Log.e(TAG, "onStartPlayer");
		}

		@Override
		public void onStopPlayer(XMediaPlayer player) {
			Log.e(TAG, "onStopPlayer");
		}

		@Override
		public void OnFinishPlayer(XMediaPlayer player) {
			Log.e(TAG, "OnFinishPlayer");
			handler.obtainMessage(HANDLER_UPDATE_LONG_RECORD_PLAYER_COMPLETE).sendToTarget();
		}

		@Override
		public void onUpdateTime(XMediaPlayer player, double time)
		{
			String strTime = DateUtils.getFormatTime(time) + "/" + DateUtils.getFormatTime(mDVideoTime);
			int process = (int) (time * 100 / player.getTotalTime());
			handler.obtainMessage(HANDLER_UPDATE_LONG_RECORD_PLAYER_PROCESS, process).sendToTarget();
			handler.obtainMessage(HANDLER_UPDATE_VIDEO_TIME_STRING, strTime).sendToTarget();
		}

		@Override
		public void onSurfaceCreated(XMediaPlayer player)
		{

		}

		@Override
		public void onPreparedPlayer(XMediaPlayer player)
		{
			if(mMediaPlayer.getStatus() == XEffectMediaPlayer.STATUS_OPENED)
			{
				mDVideoTime = player.getTotalTime();
				handler.obtainMessage(HANDLER_UPDATE_VIDEO_PREPARED, true).sendToTarget();
			}
			else
			{
				handler.obtainMessage(HANDLER_UPDATE_VIDEO_PREPARED, false).sendToTarget();
			}
		}

		@Override
		public void onVideoSizeChanged(XMediaPlayer player, int w, int h) {
			Log.e(TAG, "onVideoSizeChanged");			
		}

		@Override
		public void onError(XMediaPlayer player, int what, int extra) {
			Log.e(TAG, "onError");
			handler.obtainMessage(HANDLER_UPDATE_VIDEO_ERROR).sendToTarget();
		}
	}
	
}
