package com.simope.yzvideo.control;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.media.AudioManager;
import android.os.Handler;
import android.os.Message;
import android.text.format.Time;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import com.simope.yzvideo.R;
import com.simope.yzvideo.control.listener.ControllerPlayListener;
import com.simope.yzvideo.util.StringUtils;

public class MobileMediaController extends MediaController {

	public MediaPlayerControl mPlayer;
	public static final int sDefaultTimeout = 6500;
	public static final int FADE_OUT = 0x881;
	public static final int SHOW_PROGRESS = 0x882;
	public static final int PLAY_COMPLETE = 0x883;
	public static final int SHOW_BTNWD = 0X884;
	public Context mContext;
	public int mAnimStyle, mAnimStyle2, mAnimStyle3;
	public View mAnchor;
	public View mRoot;
	public ImageButton mPauseButton;
	public long mDuration;
	public long mCurrent;
	public boolean mShowing;
	public boolean mBtnShowing;
	public boolean mDragging = false;
	public boolean is_complete = false;
	public String currentPath;
	public boolean current_video = true;
	public SeekBar mProgress;
	public TextView mEndTime, mCurrentTime;
	public boolean isRegister = false;
	public PopupWindow completeWindow;
	public long newposition;

	public Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			long pos;
			switch (msg.what) {
			case FADE_OUT:
				hide();
				break;
			case SHOW_PROGRESS:
				pos = setProgress();
				if (/** !mDragging&& **/
				mShowing) {
					msg = obtainMessage(SHOW_PROGRESS);
					sendMessageDelayed(msg, 1000 - (pos % 1000));
					// sendEmptyMessage(SHOW_PROGRESS);
					updatePausePlay();
				}
				break;
			case PLAY_COMPLETE:
				closePlay();
				break;

			}
		}
	};
	private PopupWindow mWindow;
	private ImageButton mDlnaButton;
	private Button button1;
	private ImageButton mobile_controller_layout_button5;
	private PopupWindow mExtraWindow;
	private TextView tvshow;
	private Button backButton;
	private Button aboutButton;
	private TextView showBattery;
	private SeekBar mediacontroller_volume_seekbar;
	private AudioManager audioManager;
	private int maxVolume, currentVolume;
	private TextView current_system_time;
	private boolean dlna_render = false;
	private View mHead;
	private View mfootView;
	private LayoutInflater inflater;
	private View btnView;
	private PopupWindow buttonWindow;
	private boolean sbtnIspressed = false;
	private boolean isLive = false;
	private TextView head_play_live;
	private String[] btn_arr;
	private String[] playAddress_arr;
	private Button[] btn_contain;
	private int btn_hight;
	public long pos;
	public String mTitle;
	public boolean hideShow = false;
	public View completeView;
	public boolean init = false;
	public ControllerPlayListener controllerPlayListener;

	public void initDlnaBtn(boolean dlna_rende) {
		if (mDlnaButton == null) {
			return;
		}
		if (dlna_rende) {
			mDlnaButton.setVisibility(View.VISIBLE);
			mDlnaButton.setOnClickListener(dlnaClickListener);
		} else {
			mDlnaButton.setVisibility(View.INVISIBLE);
		}
		this.dlna_render = dlna_rende;
	}

	private OnClickListener dlnaClickListener = new OnClickListener() {
		@Override
		public void onClick(View v) {
			if (controllerPlayListener != null) {
				controllerPlayListener.showDlnaDial();
			}
		}
	};

	public void setLive(boolean isLive) {
		if (isLive && mProgress != null && mCurrentTime != null
				&& mEndTime != null && head_play_live != null) {
			mProgress.setVisibility(View.GONE);
			mProgress.setEnabled(false);
			mCurrentTime.setVisibility(View.GONE);
			mEndTime.setVisibility(View.GONE);
			head_play_live.setVisibility(View.VISIBLE);
		}
		this.isLive = isLive;
	}

	public MobileMediaController(Context context) {
		super(context);
		if (initController(context))
			initFloatingWindow();
	}

	public boolean initController(Context context) {
		mContext = context;
		inflater = (LayoutInflater) mContext
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		mHead = (View) inflater.inflate(R.layout.mobile_control_head_extra,
				null);
		btnView = (View) inflater.inflate(R.layout.mobile_multibtn_pop, null);
		completeView = (View) inflater.inflate(R.layout.playview_dilg, null);
		return true;
	}

	public void initFloatingWindow() {
		mWindow = new PopupWindow(mContext);

		mWindow.setFocusable(false);
		mWindow.setBackgroundDrawable(null);

		mAnimStyle = R.style.PopupAnimation_From_bottom;
		mAnimStyle2 = R.style.PopupAnimation_From_top;

		mExtraWindow = new PopupWindow(mHead,
				android.view.ViewGroup.LayoutParams.MATCH_PARENT,
				android.view.ViewGroup.LayoutParams.WRAP_CONTENT);
		mExtraWindow.setFocusable(false);
		mExtraWindow.setBackgroundDrawable(null);
		mExtraWindow.setOutsideTouchable(true);

		buttonWindow = new PopupWindow(btnView,
				android.view.ViewGroup.LayoutParams.WRAP_CONTENT,
				android.view.ViewGroup.LayoutParams.WRAP_CONTENT);
		mExtraWindow.setFocusable(false);
		mExtraWindow.setBackgroundDrawable(null);
		mExtraWindow.setOutsideTouchable(true);

		completeWindow = new PopupWindow(completeView,
				android.view.ViewGroup.LayoutParams.WRAP_CONTENT,
				android.view.ViewGroup.LayoutParams.WRAP_CONTENT);
		completeWindow.setFocusable(false);
		completeWindow.setBackgroundDrawable(null);
		completeWindow.setOutsideTouchable(true);

	}

	public void setAnchorView(View view) {
		mAnchor = view;

		removeAllViews();
		mRoot = makeControllerView();
		mWindow.setContentView(mRoot);
		mWindow.setWidth(LayoutParams.MATCH_PARENT);
		mWindow.setHeight(LayoutParams.WRAP_CONTENT);
		initControllerView(mRoot);
		initHeadView(mHead);
	}

	public void initControllerView(View v) {

		mPauseButton = (ImageButton) v
				.findViewById(R.id.mobile_controller_layout_mediacontroller_pause);
		if (mPauseButton != null) {
			mPauseButton.setOnClickListener(mPauseListener);
		}
		mProgress = (SeekBar) v
				.findViewById(R.id.mobile_controller_layout_mediacontroller_progress);
		mEndTime = (TextView) v
				.findViewById(R.id.mobile_controller_layout_mediacontroller_time_total);
		mEndTime.setText(StringUtils.generateTime(mDuration));

		mCurrentTime = (TextView) v
				.findViewById(R.id.mobile_controller_layout_mediacontroller_time_current);
		mProgress.setMax(1000);

		mDlnaButton = (ImageButton) v.findViewById(R.id.controller_dlnabtn);
		mDlnaButton.setOnClickListener(dlnaClickListener);
		if (dlna_render) {
			mDlnaButton.setVisibility(View.VISIBLE);
		}
		if (init && pos != 0 && !is_complete) {
			if (mCurrentTime != null)
				mCurrentTime.setText(StringUtils.generateTime(mCurrent));
			mProgress.setProgress((int) pos);
		}

		if (isLive) {
			mProgress.setVisibility(View.GONE);
			mCurrentTime.setVisibility(View.GONE);
			mProgress.setEnabled(false);
			mEndTime.setVisibility(View.GONE);
		} else {
			if (mProgress != null) {
				mProgress.setOnSeekBarChangeListener(mSeekListener);
			}
		}

		button1 = (Button) v
				.findViewById(R.id.mobile_controller_layout_button1);

		mediacontroller_volume_seekbar = (SeekBar) v
				.findViewById(R.id.mobile_controller_layout_mediacontroller_volume_seekbar);
		audioManager = (AudioManager) mContext.getApplicationContext()
				.getSystemService(Context.AUDIO_SERVICE);
		maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
		mediacontroller_volume_seekbar.setMax(maxVolume);
		currentVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
		mediacontroller_volume_seekbar.setProgress(currentVolume);
		mobile_controller_layout_button5 = (ImageButton) v
				.findViewById(R.id.mobile_controller_layout_button5);
		mobile_controller_layout_button5
				.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
		if (currentVolume == 0) {
			mobile_controller_layout_button5
					.setImageResource(R.drawable.volume_icon_zero);
		} else {
			mobile_controller_layout_button5
					.setImageResource(R.drawable.volume_icon);
		}

		initAddListener();

	}

	public void initHeadView(View v) {
		tvshow = (TextView) v
				.findViewById(R.id.mobile_control_head_extral_tvshow);
		backButton = (Button) v
				.findViewById(R.id.mobile_control_head_extral_back);
		aboutButton = (Button) v
				.findViewById(R.id.mobile_control_head_extral_about);
		showBattery = (TextView) v
				.findViewById(R.id.mobile_control_head_extral_show_battery);
		current_system_time = (TextView) v
				.findViewById(R.id.mobile_control_head_extral_time);
		head_play_live = (Button) v
				.findViewById(R.id.mobile_control_head_extral_play_live);
		if (isLive) {
			head_play_live.setVisibility(View.VISIBLE);
		}
		if (tvshow != null) {
			tvshow.setText(mTitle);

		}
		if (showBattery != null) {
			mContext.registerReceiver(batteryChangedReceiver, new IntentFilter(
					Intent.ACTION_BATTERY_CHANGED));
			isRegister = true;
		}
		initHeadListener();
	}

	public void initBtnView(View v) {
		if (v != null) {
			LinearLayout contain = (LinearLayout) v
					.findViewById(R.id.mobilemultibtn_contains_btn);
			btn_contain = new Button[btn_arr.length];
			btn_hight=btn_arr.length;
			for (int i = 0; i < btn_arr.length; i++) {
				Button btn = new Button(mContext);
				btn.setBackgroundDrawable(mContext.getApplicationContext()
						.getResources()
						.getDrawable(R.drawable.tv_changequantity_btn));
				btn.setTextColor(mContext.getApplicationContext()
						.getResources()
						.getColor(R.color._white_charactor_color));
				if (i != 0) {
					LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
							android.widget.LinearLayout.LayoutParams.MATCH_PARENT,
							android.widget.LinearLayout.LayoutParams.WRAP_CONTENT);
					params.setMargins(0, 5, 0, 0);
					btn.setLayoutParams(params);
				}
				btn.setText(btn_arr[i]);
				btn.setContentDescription(playAddress_arr[i]);
				btn.setOnClickListener(btnonClickListener);
				contain.addView(btn);
				btn_contain[i] = btn;
			}
		}
	}

	private OnClickListener btnonClickListener = new OnClickListener() {
		public void onClick(View v) {
			buttonWindow.dismiss();
			completeWindow.dismiss();
			sbtnIspressed = !sbtnIspressed;
			if (v.getContentDescription().toString().equals(currentPath)) {
				return;
			} else {
				if (is_complete) {
					mCurrent=0;
				}
				changeQuanlity(v.getContentDescription().toString());
			}
		}
	};

	private BroadcastReceiver batteryChangedReceiver = new BroadcastReceiver() {
		public void onReceive(Context context, Intent intent) {
			if (Intent.ACTION_BATTERY_CHANGED.equals(intent.getAction())) {
				int level = intent.getIntExtra("level", 0);
				int scale = intent.getIntExtra("scale", 100);
				if (showBattery != null) {
					showBattery.setText((level * 100 / scale) + "%");
				}
			}
		}
	};

	public void closePlay() {
		int cancle_time=0,cancle_total_time=0;
		if (mPlayer != null) {
			cancle_time=mPlayer.getCurrentPosition();
			cancle_total_time=mPlayer.getDuration();
			if (is_complete && !isLive) {
				hide();
				mPlayer.release(true);
				mCurrent = mDuration;
				mHandler.removeMessages(FADE_OUT);
				mHandler.removeMessages(SHOW_PROGRESS);
				show();
			} else {
				mPlayer.pause();
				mPlayer.release(true);
				if (mWindow != null) {
					mWindow.dismiss();
				}
				if (mExtraWindow != null) {
					mExtraWindow.dismiss();
				}
				if (completeWindow != null) {
					completeWindow.dismiss();
				}
				hide();
				if (isRegister) {
					unregister();
				}
				if (controllerPlayListener != null) {
					controllerPlayListener.canclePlayView(cancle_time,cancle_total_time);
				}
			}
		} else {
			hide();
			if (isRegister) {
				unregister();
			}
			if (controllerPlayListener != null) {
				controllerPlayListener.canclePlayView(cancle_time,cancle_total_time);
			}
		}
	}

	private void initHeadListener() {
		backButton.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				is_complete = false;
				closePlay();
			}

		});

		aboutButton.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				mHandler.removeMessages(FADE_OUT);
				if (controllerPlayListener != null)
					controllerPlayListener.showHideAboutYzDialog(true);
				show();
			}

		});

	}

	public View makeControllerView() {
		mfootView = ((LayoutInflater) mContext
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(
				R.layout.mobile_controller_layout, this);
		return mfootView;
	}

	public void changeQuanlity(String which) {
		if (which == null) {
			return;
		}
		if (mDuration != 0) {
			long pos = 1000L * mCurrent / mDuration;
			mProgress.setProgress((int) pos);
		}
		mHandler.removeMessages(FADE_OUT);
		mHandler.removeMessages(SHOW_PROGRESS);
		controllerPlayListener.changerChannel(which);
		currentPath = which;
		mHandler.sendEmptyMessage(SHOW_PROGRESS);
	}

	public void changeVolumeSeek() {
		if (audioManager != null) {
			currentVolume = audioManager
					.getStreamVolume(AudioManager.STREAM_MUSIC);
			if (currentVolume == 0) {
				mobile_controller_layout_button5
						.setImageResource(R.drawable.volume_icon_zero);
			} else {
				mobile_controller_layout_button5
						.setImageResource(R.drawable.volume_icon);
			}
		}
		if (mediacontroller_volume_seekbar != null) {
			mediacontroller_volume_seekbar.setProgress(currentVolume);
		}
		if (mShowing) {
			mHandler.removeMessages(FADE_OUT);
			mHandler.sendMessageDelayed(mHandler.obtainMessage(FADE_OUT),
					sDefaultTimeout);
		}
	}

	private void initAddListener() {
		mediacontroller_volume_seekbar
				.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

					public void onStopTrackingTouch(SeekBar seekBar) {
						show(sDefaultTimeout);
					}

					public void onStartTrackingTouch(SeekBar seekBar) {

					}

					public void onProgressChanged(SeekBar seekBar,
							int progress, boolean fromUser) {
						mHandler.removeMessages(FADE_OUT);
						audioManager.setStreamVolume(AudioManager.STREAM_MUSIC,
								progress, 0);
						currentVolume = audioManager
								.getStreamVolume(AudioManager.STREAM_MUSIC);
						if (currentVolume == 0) {
							mobile_controller_layout_button5
									.setImageResource(R.drawable.volume_icon_zero);
						} else {
							mobile_controller_layout_button5
									.setImageResource(R.drawable.volume_icon);
						}
						mediacontroller_volume_seekbar
								.setProgress(currentVolume);
					}
				});

		button1.setOnClickListener(new OnClickListener() {
			public void onClick(View arg0) {
				if (btn_contain == null) {
					return;
				}
				mHandler.removeMessages(FADE_OUT);
				int[] location = new int[2];
				arg0.getLocationOnScreen(location);
				if (!sbtnIspressed) {
					int button_Animation = R.style.AnimBottom;
					for (int i = 0; i < btn_contain.length; i++) {
						if (btn_contain[i] != null) {
							if (btn_contain[i].getContentDescription()
									.toString().equals(currentPath)) {
								btn_contain[i].setTextColor(mContext
										.getApplicationContext().getResources()
										.getColor(R.color.red_charactor_show));
							} else {
								btn_contain[i]
										.setTextColor(mContext
												.getApplicationContext()
												.getResources()
												.getColor(
														R.color._white_charactor_color));
							}
						}
					}
					buttonWindow.setAnimationStyle(button_Animation);
					buttonWindow.showAtLocation(mAnchor, Gravity.NO_GRAVITY,
							location[0], location[1] - (btn_hight+1)*arg0.getHeight());

				} else {
					buttonWindow.dismiss();
				}
				sbtnIspressed = !sbtnIspressed;
				show();
			}
		});
	}

	public void setMediaPlayer(MediaPlayerControl player) {
		mPlayer = player;
		updatePausePlay();
	}

	/**
	 * Control the action when the seekbar dragged by user
	 * 
	 * @param seekWhenDragging
	 *            True the media will seek periodically
	 */
	public void setInstantSeeking(boolean seekWhenDragging) {
	}

	public void show() {
		show(sDefaultTimeout);
	}

	/**
	 * Set the content of the file_name TextView
	 * 
	 * @param name
	 */

	public void setPlayName(String name) {
		if (name != null)
			mTitle = name;
		if (tvshow != null)
			tvshow.setText(name);

	}

	/**
	 * Set the View to hold some information when interact with the
	 * MediaController
	 * 
	 * @param v
	 */
	// public void setInfoView(OutlineTextView v) {
	// mInfoView = v;
	// }

	private void disableUnsupportedButtons() {
		try {
			if (mPauseButton != null && mPlayer != null && !mPlayer.canPause())
				mPauseButton.setEnabled(true);
		} catch (IncompatibleClassChangeError ex) {
		}
	}

	/**
	 * <p>
	 * Change the animation style resource for this controller.
	 * </p>
	 * 
	 * <p>
	 * If the controller is showing, calling this method will take effect only
	 * the next time the controller is shown.
	 * </p>
	 * 
	 * @param animationStyle
	 *            animation style to use when the controller appears and
	 *            disappears. Set to -1 for the default animation, 0 for no
	 *            animation, or a resource identifier for an explicit animation.
	 * 
	 */
	// public void setAnimationStyle(int animationStyle) {
	// mAnimStyle = animationStyle;
	// }

	/**
	 * Show the controller on screen. It will go away automatically after
	 * 'timeout' milliseconds of inactivity.
	 * 
	 * @param timeout
	 *            The timeout in milliseconds. Use 0 to show the controller
	 *            until hide() is called.
	 */
	public void freshCurrentTime() {
		Time time = new Time();
		time.setToNow();
		if (time.minute < 10 && current_system_time != null) {
			current_system_time.setText(time.hour + ":0" + time.minute);
		} else {
			current_system_time.setText(time.hour + ":" + time.minute);
		}
	}

	public void show(int timeout) {
		Log.e("UI", "<<<<<<<<<<<<<<<<<<<show(int timeout)>>>>>>>>>>>>>>");
		if (!mShowing && mAnchor != null && mAnchor.getWindowToken() != null) {
			if (mPauseButton != null)
				mPauseButton.requestFocus();
			disableUnsupportedButtons();

			int[] location = new int[2];

			mAnchor.getLocationOnScreen(location);
			Rect anchorRect = new Rect(location[0], location[1], location[0]
					+ mAnchor.getWidth(), location[1] + mAnchor.getHeight());
			freshCurrentTime();

			mWindow.setAnimationStyle(mAnimStyle);
			mWindow.showAtLocation(mAnchor, Gravity.NO_GRAVITY,
					anchorRect.left, anchorRect.bottom);
			mExtraWindow.setAnimationStyle(mAnimStyle2);
			mExtraWindow.showAtLocation(mAnchor, Gravity.TOP, 0, 0);
			if (is_complete && !isLive) {
				mDragging=false;
				mCurrent = mDuration;
				pos = 0;
				mCurrentTime.setText(StringUtils.generateTime(0));
				mProgress.setProgress(0);
				mProgress.setSecondaryProgress(0);
				completeWindow.showAtLocation(mAnchor, Gravity.CENTER, 0, 0);
				if (controllerPlayListener != null) {
					controllerPlayListener.setActTime(0, mDuration);
				}
			}
			freshCurrentTime();
			mShowing = true;

		}
		updatePausePlay();
		setEnabled(true);
		if (timeout != 0 && !is_complete) {
			// if (!isLive) {
			mHandler.sendEmptyMessage(SHOW_PROGRESS);
			// }
			mHandler.removeMessages(FADE_OUT);
			mHandler.sendMessageDelayed(mHandler.obtainMessage(FADE_OUT),
					timeout);
		}
	}

	public void superShow() {
		mHandler.removeMessages(FADE_OUT);

	}

	public boolean isShowing() {
		return mShowing;
	}

	public void hide() {
		Log.e("UI", "<<<<<<<<<<<<<<<<<<<hide()>>>>>>>>>>>>>>");
		if (mAnchor == null)
			return;
		if (buttonWindow.isShowing()) {
			buttonWindow.dismiss();
			sbtnIspressed = !sbtnIspressed;
		}
		if (mShowing) {
			try {
				mWindow.dismiss();
				mExtraWindow.dismiss();
				completeWindow.dismiss();
				if (controllerPlayListener != null) {
					controllerPlayListener.showHideAboutYzDialog(false);
				}

			} catch (IllegalArgumentException ex) {
			}
			mShowing = false;
		}
	}

	public long setProgress() {
		if (mPlayer == null || mDragging)
			return 0;
		long position = mPlayer.getCurrentPosition();
		if (position == 0 || !mPlayer.isPlaying()) {
			return 0;
		}
		if (mProgress != null && !isLive) {
			long duration = mPlayer.getDuration();
			if (mDuration > 0) {
				pos = 1000L * position / mDuration;
				mProgress.setProgress((int) pos);
			}
			int percent = mPlayer.getBufferPercentage();
			mProgress.setSecondaryProgress(percent * 10);
			mDuration = (int) duration;
		}

		mCurrent = (int) position;

		if (mEndTime != null)
			mEndTime.setText(StringUtils.generateTime(mDuration));

		if (mCurrentTime != null)
			mCurrentTime.setText(StringUtils.generateTime(position));

		if (controllerPlayListener != null) {
			controllerPlayListener.setActTime(mCurrent, mDuration);
		}
		init = true;

		return position;
	}

	public boolean onTouchEvent(MotionEvent event) {
		show(sDefaultTimeout);
		return true;
	}


	public boolean dispatchKeyEvent(KeyEvent event) {
		int keyCode = event.getKeyCode();
		if (event.getRepeatCount() == 0
				&& (keyCode == KeyEvent.KEYCODE_HEADSETHOOK
						|| keyCode == KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE || keyCode == KeyEvent.KEYCODE_SPACE)) {
			doPauseResume();
			show(sDefaultTimeout);
			if (mPauseButton != null)
				mPauseButton.requestFocus();
			return true;
		} else if (keyCode == KeyEvent.KEYCODE_MEDIA_STOP) {
			if (mPlayer.isPlaying()) {
				mPlayer.pause();
				updatePausePlay();
			}
			return true;
		} else if (keyCode == KeyEvent.KEYCODE_BACK
				|| keyCode == KeyEvent.KEYCODE_MENU) {
			// hide();
			return true;
		} else {
			show(sDefaultTimeout);
		}
		return super.dispatchKeyEvent(event);
	}

	private View.OnClickListener mPauseListener = new View.OnClickListener() {
		public void onClick(View v) {
			// mHandler.removeMessages(CLICK);
			if (is_complete) {
				mCurrent = 0;
				completeWindow.dismiss();
				changeQuanlity(currentPath);
			} else {
				doPauseResume();
//				show(sDefaultTimeout);
			}
		}
	};

	public void updatePausePlay() {
		if (mRoot == null || mPauseButton == null)
			return;
		if (mPlayer != null && mPlayer.isPlaying()) {
			// mPauseButton.setImageResource(R.drawable.play_controller_pause_btn);
			mPauseButton.setBackgroundDrawable(mContext.getResources()
					.getDrawable(R.drawable.mobile_controller_pause_btn));
		} else {
			// mPauseButton.setImageResource(R.drawable.play_controller_play_btn);
			mPauseButton.setBackgroundDrawable(mContext.getResources()
					.getDrawable(R.drawable.mobile_controller_play_btn));
		}
	}

	public void doPauseResume() {
		if (mPlayer == null) {
			return;
		}
		if (mPlayer.isPlaying()&&mPlayer.canPause()) {
			mPlayer.pause();
			if(controllerPlayListener!=null){
				controllerPlayListener.pauseShowAD();
			}
			if (isLive) {
				mPlayer.release(true);
			}
		} else {
			if (isLive) {
				mCurrent = 0;
				changeQuanlity(currentPath);
			} else {			
				mPlayer.start();
				if(controllerPlayListener!=null){
					controllerPlayListener.startAfterAD();
				}
			}
		}
		updatePausePlay();
	}

	private OnSeekBarChangeListener mSeekListener = new OnSeekBarChangeListener() {
		public void onStartTrackingTouch(SeekBar bar) {
			Log.i("UI", "子类的listener>>>>>>onStartTrackingTouch>>>>>>>>>");

			show(3600000);
			mHandler.removeMessages(SHOW_PROGRESS);
		}

		public void onProgressChanged(SeekBar bar, int progress,
				boolean fromuser) {
			if (!fromuser) {
				return;
			}
			if (mPlayer != null) {
				long duration = mPlayer.getDuration();
				long newposition = (duration * progress) / 1000L;
				if (mCurrentTime != null)
					mCurrentTime.setText(StringUtils.generateTime(newposition));
			}

		}

		public void onStopTrackingTouch(SeekBar bar) {
			if (mPlayer != null) {
				long duration = mPlayer.getDuration();
				newposition = (duration * bar.getProgress()) / 1000L;
				mCurrent = newposition;
				if (duration != 0) {
					pos = 1000L * mCurrent / duration;
				}
				mPlayer.start();
				mPlayer.seekTo((int) newposition);
			}
			show(sDefaultTimeout);
			mHandler.removeMessages(SHOW_PROGRESS);
			mDragging = true;
			mHandler.sendEmptyMessageDelayed(SHOW_PROGRESS, 1000);
			if (controllerPlayListener != null && !is_complete) {
				controllerPlayListener.showBuffText(newposition);
				controllerPlayListener.setActTime(mCurrent, mDuration);
			}
		}
	};

	public void setEnabled(boolean enabled) {
		if (mPauseButton != null)
			mPauseButton.setEnabled(enabled);
		super.setEnabled(enabled);
	}

	public void unregister() {
		mContext.unregisterReceiver(batteryChangedReceiver);
		isRegister = false;
	}

	public void setControllerPlayListener(
			ControllerPlayListener controllerPlayListener) {
		this.controllerPlayListener = controllerPlayListener;
	}

	public String[] getBtn_arr() {
		return btn_arr;
	}

	public void setBtn_arr(String[] btn_arr) {
		this.btn_arr = btn_arr;

	}

	public String[] getPlayAddress_arr() {
		return playAddress_arr;
	}

	public void initBtn() {
		initBtnView(btnView);
	}

	public void setPlayAddress_arr(String[] playAddress_arr) {
		this.playAddress_arr = playAddress_arr;
	}

	public boolean isLive() {
		return isLive;
	}

}