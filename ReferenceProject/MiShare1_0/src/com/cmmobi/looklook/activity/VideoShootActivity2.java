
package com.cmmobi.looklook.activity;

import java.util.ArrayList;
import java.util.Vector;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.Rect;
import android.hardware.Camera.CameraInfo;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.media.ThumbnailUtils;
import android.os.Bundle;
import android.os.Environment;
import android.os.Message;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationSet;
import android.view.animation.LinearInterpolator;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.AbsoluteLayout;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.Scroller;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;
import cn.zipper.framwork.core.ZActivity;
import cn.zipper.framwork.core.ZBroadcastReceiver;
import cn.zipper.framwork.core.ZDialog;
import cn.zipper.framwork.core.ZLog;
import cn.zipper.framwork.core.ZToast;
import cn.zipper.framwork.core.ZViewFinder;
import cn.zipper.framwork.device.ZScreen;
import cn.zipper.framwork.io.file.ZFileSystem;
import cn.zipper.framwork.utils.ZBooleanBox;
import cn.zipper.framwork.utils.ZGraphics;
import cn.zipper.framwork.utils.ZMillissecondLogger;
import cn.zipper.framwork.utils.ZThread;

import com.cmmobi.looklook.MainApplication;
import com.cmmobi.looklook.R;
import com.cmmobi.looklook.common.gson.DiaryController;
import com.cmmobi.looklook.common.gson.DiaryController.DiaryWrapper;
import com.cmmobi.looklook.common.gson.GsonProtocol;
import com.cmmobi.looklook.common.gson.GsonResponse3.MyDiary;
import com.cmmobi.looklook.common.gson.GsonResponse3.MyDiaryList;
import com.cmmobi.looklook.common.utils.CmmobiClickAgentWrapper;
import com.cmmobi.looklook.common.utils.LowStorageChecker;
import com.cmmobi.looklook.common.utils.LowStorageChecker.OnChoseListener;
import com.cmmobi.looklook.info.profile.AccountInfo;
import com.cmmobi.looklook.info.profile.ActiveAccount;
import com.cmmobi.looklook.info.profile.CommonInfo;
import com.cmmobi.looklook.info.profile.DiaryManager;
import com.cmmobi.looklook.info.profile.MediaValue;
import com.cmmobivideo.utils.EffectUtils;
import com.cmmobivideo.utils.PluginUtils;
import com.cmmobivideo.utils.XUtils;
import com.cmmobivideo.workers.XVideoRecorder;
import com.cmmobivideo.workers.XVideoRecorder.XVideoRecorderInfoListener;
import com.cmmobivideo.workers.XVideoRecorder.XVideoSurfaceSizeChangeListener;
import com.cmmobivideo.workers.XVideoRecorder.XVideoTakePicture;
import com.google.gson.Gson;

import effect.EffectType.CamaraMode;
import effect.EffectType.FlashMode;
import effect.EffectType.PreviewMode;
import effect.XEffects;

@SuppressWarnings("deprecation")
// for AbsoluteLayout;
public class VideoShootActivity2 extends ZActivity implements SensorEventListener, OnSeekBarChangeListener, OnGestureListener, OnTouchListener,
		XVideoRecorderInfoListener<Object>, XVideoSurfaceSizeChangeListener, XVideoTakePicture, OnChoseListener, OnCheckedChangeListener {

	private static final float DRAG_EVENT_MIN_MOVE_DISTANCE = 0.50f; // 拖动/滑动最小距离
																		// (厘米)
																		// (用来识别模式切换动作);
	private static final float PROGRESS_BAR_HEIGHT = 0.15f; // 拍摄进度条的高度(厘米);

	private static final int SMALL_GAP_MILLIS = 90; // 通用小间隔;
	private static final int THUMBNAIL_ANIMATION_DURATION = 350; // 缩略图动画持续时间;
	private static final int TURN_ON_FLASHLIGHT_DELAY_MILLIS = 270; // 从打开闪光灯到拍照的间隔时间;
	private static final int SHOOT_REMAIN_MILLIS = 10 * 60 * 1000; // wifi状态下拍摄的最长时间(10分钟);
	private static final int MODE_SWITCH_ANIMATION_SPEED = 300; // 拍照/拍摄模式切换动画的持续时间;
	private static final int CONTINUOUS_CAPTURE_GAP_MILLIS = 100; // 连拍最小间隔时间;
	private static final int SHOOTING_GAP_MILLIS = 95; // 开始和暂停之间的最小间隔时间;
	private static final int SHOOT_TIME_UPDATE_DURATION = 35; // 统计拍摄时长的周期
																// (每隔一会儿就统计一下时长);
	private static final int CONTINUE_SHOOT_HOLD_MILLIS = 240; // 想继续拍摄时,
																// 需按住视频预览窗的时间;
	private static final int BUTTON_BLOCKING_MILLIS = 300; // 拍摄/拍照/延迟拍照等按钮被点击后的阻塞时间
															// (阻塞时间过后才允许再次点击);
	private static final int CURSOR_COLOR_CHANGE_GAP_MILLIS = 400; // 光标闪烁频率;
	private static final int ALPHA_ANIMATIONG_MILLIS = 320; // 界面元素Alpha动画时长;
	private static final int MIN_RECORD_MILLIS = 2150; // 最小录制时间 (2秒);
	private static final int MAX_SHORT_VIDEO_MILLIS = 8150; // 段视频最长录制时间 (8秒);
	private static final int LOOP_MESSAGE_GAP_MILLIS = 500; // 循环消息的通用间隔时间;
	private static final int SLIDE_TO_LONG_VIDEO_ANIMATION_MILLIS = 170; // 横滑切换长视频动画时间;
	private static final int TOP_TOAST_HOLDING_MILLIS = 2700; // 顶部通知持续显示时间;
	private static final int CAMERA_OPEN_ANIMATION_MILLIS = 900; // 开启摄像机动画时间;
	private static final int CAMERA_OPEN_ANIMATION_HOLD_MILLIS = 800; // 开启摄像机动画之前的冻结时间;
	

	private static final int CAMERA_ZOOM_STEPS = 20; // 摄像机变焦步数 (从最小到最大需要的步数);
	private static final int THUMBNAIL_ANIMATION_PICTURE = 0xf1ffffff; // 当前动画是拍照后的动画;
	private static final int THUMBNAIL_ANIMATION_VIDEO = 0xf2ffffff; // 当时动画是拍摄后的动画;

	private static final int CURSOR_COLOR_A = 0xFF017AFF/*0xFF000000*/; // 拍摄进度光标颜色A;
	private static final int CURSOR_COLOR_B = 0xFF7FE80E/*0xFF00BBFF*/; // 拍摄进度光标颜色B;
	private static final int PAUSE_POINT_COLOR = 0xFFBBBBCC; // 拍摄断点颜色;

	private static final int WORK_MODE_CAPTURE = 1;
	private static final int WORK_MODE_SHOOT = 2;

	private static final int MESSAGE_TURN_ON_FLASHLIGHT_TORCH = 20; // 消息: 开关闪光灯
	private static final int MESSAGE_TURN_ON_FLASHLIGHT_ON = 21; // 消息: 开关闪光灯
	private static final int MESSAGE_TURN_ON_FLASHLIGHT_AUTO = 22; // 消息: 开关闪光灯
	private static final int MESSAGE_TURN_OFF_FLASHLIGHT = 23; // 消息: 开关闪光灯;
	private static final int MESSAGE_TAKE_PICTURE = 30; // 消息: 拍照;
	private static final int MESSAGE_START_SHOOTING = 31; // 消息: 拍摄;
	private static final int MESSAGE_PAUSE_SHOOTING = 32; // 消息: 暂停拍摄;
	private static final int MESSAGE_CONTINUE_SHOOTING = 33; // 消息: 继续拍摄;
	private static final int MESSAGE_UPDATE_SHOOT_STATE = 50; // 消息:更新拍摄时长和wifi状态;
	private static final int MESSAGE_PHOTO_ALBUM_UPDATING_ANIMATION = 60; // 消息:
																			// 截屏图片进入相册的动画;
	private static final int MESSAGE_CHANGE_CAMERA = 70; // 消息: 切换摄像头;
	// private static final int MESSAGE_SWITCH_HIGHT_COMMON = 80; // 消息:
	// 切换高清/普清;
	private static final int MESSAGE_SWITCH_WORK_MODE = 90; // 消息: 切换拍照/拍摄模式;
	private static final int MESSAGE_SWITCH_MODE_ANIMATION = 100; // 消息:
																	// 切换拍摄/拍照模式时的动画消息;
	private static final int MESSAGE_SLIDE_TO_LONG_VIDEO_ANIMATION = 101; // 消息:
																			// 横滑切换到长视频后,
																			// 拍摄按钮回弹动画;
	private static final int MESSAGE_CHANGE_CURSOR_COLOR = 110; // 消息: 拍摄进度光标闪烁;
	private static final int MESSAGE_LEAVE_SHOOT_PAGE_WHEN_DIARY_OK = 120; // 消息:
																			// 检测日记是否已与服务器同步,
																			// 若已同步,
																			// 则进入日记页或日记详情页.
	private static final int MESSAGE_GOTO_EDIT_SHARE_PAGE = 130; // 消息: 进入编辑分享页.
	private static final int MESSAGE_SEEK_BAR_ZOOM_IN = 140; // 消息: 焦距放大.
	private static final int MESSAGE_SEEK_BAR_ZOOM_OUT = 141; // 消息: 焦距缩小.

	private static final String WORK_MODE = "WORK_MODE";

	private static final String CDR_KEY_VIDEO_TIME = "video_time";
	private static final String CDR_KEY_AGAIN = "again";
	private static final String CDR_KEY_RIGHT_BROWSE = "right_browse";
	private static final String CDR_KEY_VIDEO_BUTTON = "video_button";
	private static final String CDR_KEY_LONG_VIDEO = "video_long";
	private static final String CDR_KEY_SHORT_VIDEO = "video_short";
	private static final String CDR_KEY_FLASHLIGHT = "flash";
	private static final String CDR_KEY_UP_AREA = "up_area";
	private static final String CDR_KEY_UPLOAD_CONTENT = "upload_content";
	private static final String CDR_KEY_SHP_SW_CAM = "shp_sw_cam";
	private static final String CDR_KEY_FOCAL_DISTANCE = "focal_distance";
	private static final String CDR_KEY_SWITCH_MODE = "switch_mode";
	private static final String CDR_KEY_VIDEO_CHANGE = "video_change";

	private static final String CDR_LABEL_CAMERA_FRONT = "1"; // 前摄像头;
	private static final String CDR_LABEL_CAMERA_BACK = "2"; // 后摄像头;
	private static final String CDR_LABEL_SHORT_TO_LONG_VIDEO = "1"; // 短到长视频;
	private static final String CDR_LABEL_LONG_TO_SHORT_VIDEO = "2"; // 长到短视频;

	private static boolean isNeedShowLastCover;
	private static String lastDiaryUUID;
	private static long videoShootTotalMillis;
	private static Vector<DiaryWrapper> wrappers = new Vector<DiaryWrapper>();

	private boolean isSurfaceCreated;
	private boolean isRecording;
	private boolean isSnapshoting;
	private boolean isAllowFlashlight;
	private boolean isFlashlightOn;
	private boolean isFrontCamera;
	private boolean isShootPausing;
	private boolean isWifiShooting;
	private boolean isAutoSnapshoting;
	private boolean isAnimationStop;
	private boolean isLongVideo;
	private boolean isTouching;
	private boolean isAllowTouching;
	private boolean isNeedGotoPreviewActivity;
	private boolean isThrowAwayThisVideo; // 拍摄小于2秒, 按返回键, 这个值为真: 抛弃这段视频;
	private boolean isNeedToEditPage;
	private static boolean isHasLastMedia;
	private long videoClipStartMillis;
	private long lastCameraModeStartMillis;
	private long delayCaptureStartMillis;
	private long lastUpdateMillis; // 上一次更新进度条的时间, 用来计算实际耗时, 保持帧速稳定;
	private int needAutoTakeTimes;
	private int currentWorkMode;
	private int nextWorkMode;
	private int currentCursorColor;
	private int thumbnailAnimationType;
	private int currentCameraZoom;

	private ZViewFinder finder;
	private Rect rootWindowRect;
	private Rect modeSwitchLayoutRect;
	private Rect captureButtonRect;
	private Rect shootButtonRect;
	private Rect mainWindowRect;
	private Rect recordButtonRect;
	private Rect longVideoAreaRect;
	private Rect shortShootLeftSlideRect; // 段视频拍摄时左滑切换到长视频拍摄, 所需要的区域检测矩形;
	private Rect previewRect;
	private String videoDiaryUUID;
	private String pictureDiaryUUID;

	private RelativeLayout rootWindow;
	private FrameLayout mainWindow;
	private LinearLayout modeSwitchLayout;
	private LinearLayout tipsLayout;
	private LinearLayout progressShell;
	private RelativeLayout previewLayout;
	private AbsoluteLayout progress;
	private AbsoluteLayout longVideoSlideArea;
	private RadioButton modeSwitchShortVideoButton;
	private RadioButton modeSwitchLongVideoButton;
	private TextView tip;
	private TextView shootTime;
	private TextView shootDone;
	private TextView shootAgain;
	private TextView cancelOrAgainSnapshot;
	private TextView useThisSnapshot;
	private ImageView recordButton;
	private ImageView recordShadowButton;
	private ImageView longVideoArea;
	private ImageView middleTopButton;
	private ImageView rightTopButton;
	private ImageView latestMedia;
	private ImageView back;
	private ImageView zoominButton;
	private ImageView zoomoutButton;
	private ImageView mask;
	private ImageView cursorView;
	private SeekBar seekBar;

	private AbsoluteLayout.LayoutParams coverAnimationParams;
	private RelativeLayout topButtons;
	private RelativeLayout bottomButtons;
	private RelativeLayout mainArea;
	private RelativeLayout seekBarShell;
	private RelativeLayout topShell;

	private GestureDetector gestureDetector;
	private Scroller scroller;
	private Bitmap tempCallbackBitmap;
	private Bitmap small;
	private Bitmap maskBitmap;
	private CamaraMode cameraModeMask;

	private PreviewMode currentPreviewMode; // 用来保存当前预览模式: 16:9 / 4:3
	private XVideoRecorder mediaRecorder;
	private EffectUtils effectUtils;
	private XEffects effects;
	private Vector<View> reenableViewsBuffer;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setToFullscreen();
		setToPortrait();
		MainApplication.getAppInstance().addActivity(this);
		// StrictMode.setThreadPolicy(new
		// StrictMode.ThreadPolicy.Builder().detectAll().build());
		// StrictMode.setVmPolicy(new
		// StrictMode.VmPolicy.Builder().penaltyDeath().build());

		// Debug.startMethodTracing();
	}

	private void init() {
		setContentView(R.layout.activity_shoot);

		finder = getZViewFinder();

		nextWorkMode = getIntent().getIntExtra(WORK_MODE, WORK_MODE_SHOOT);

		rootWindowRect = new Rect();
		mainWindowRect = new Rect();
		modeSwitchLayoutRect = new Rect();
		captureButtonRect = new Rect();
		shootButtonRect = new Rect();
		recordButtonRect = new Rect();
		longVideoAreaRect = new Rect();
		shortShootLeftSlideRect = new Rect();
		previewRect = new Rect();

		mainArea = finder.findRelativeLayout(R.id.main_area);

		topShell = finder.findRelativeLayout(R.id.top_shell);
		topShell.getLayoutParams().height = ZScreen.getWidth() * 109 / 640;
		
		previewLayout = finder.findRelativeLayout(R.id.preview);
		previewLayout.getViewTreeObserver().addOnGlobalLayoutListener(new OnGlobalLayoutListener() {

			private boolean isTipsOK = false;

			@Override
			public void onGlobalLayout() {
				previewLayout.getGlobalVisibleRect(previewRect);

				coverAnimationParams.x = previewLayout.getLeft();
				coverAnimationParams.y = previewLayout.getTop();
				coverAnimationParams.width = previewLayout.getWidth();
				coverAnimationParams.height = previewLayout.getHeight();
				mask.setLayoutParams(coverAnimationParams);

				if (!isTipsOK && isShortShootMode()) {
					isTipsOK = true;

//					RelativeLayout preview = finder.findRelativeLayout(R.id.preview);
//
//					if (preview.getHeight() - mainWindowRect.height() >= ZScreen.dipToPixels(32) + (22 + 10)) {
//						tipsLayout = finder.findLinearLayout(R.id.tips_a);
//						tip = finder.findTextView(R.id.tip_a);
//					} else {
						tipsLayout = finder.findLinearLayout(R.id.tips_b);
						tip = finder.findTextView(R.id.tip_b);
//					}
					showPressToShootingTip();
					tipsLayout.bringToFront();
					tip.bringToFront();
				}
			}
		});
		
		rootWindow = finder.findRelativeLayout(R.id.root);
		rootWindow.getViewTreeObserver().addOnGlobalLayoutListener(new OnGlobalLayoutListener() {

			@Override
			public void onGlobalLayout() {
				rootWindow.getGlobalVisibleRect(rootWindowRect);
			}
		});
		
		mainWindow = finder.findFrameLayout(R.id.video_view);
		mainWindow.getLayoutParams().width = ZScreen.getWidth();
		mainWindow.getLayoutParams().height = ZScreen.getWidth();
		mainWindow.getViewTreeObserver().addOnGlobalLayoutListener(new OnGlobalLayoutListener() {

			private boolean isTipsOK = false;

			@Override
			public void onGlobalLayout() {
				mainWindow.getGlobalVisibleRect(mainWindowRect);

				coverAnimationParams.x = mainWindowRect.left;
				coverAnimationParams.y = mainWindowRect.top;
				coverAnimationParams.width = mainWindowRect.width();
				coverAnimationParams.height = mainWindowRect.height();
				mask.setLayoutParams(coverAnimationParams);

				if (!isTipsOK && isShortShootMode()) {
					isTipsOK = true;

//					RelativeLayout preview = finder.findRelativeLayout(R.id.preview);
//
//					if (preview.getHeight() - mainWindowRect.height() >= ZScreen.dipToPixels(32) + (22 + 10)) {
//						tipsLayout = finder.findLinearLayout(R.id.tips_a);
//						tip = finder.findTextView(R.id.tip_a);
//					} else {
						tipsLayout = finder.findLinearLayout(R.id.tips_b);
						tip = finder.findTextView(R.id.tip_b);
//					}
					showPressToShootingTip();
					tipsLayout.bringToFront();
					tip.bringToFront();
				}
			}
		});

		initMediaRecoder();

		currentPreviewMode = PreviewMode.V_SHOW_MODLE_4P3;
		mainWindow.addView(mediaRecorder.getXSurfaceView());

		middleTopButton = finder.findImageView(R.id.middle_top_button);
		middleTopButton.setOnClickListener(this);

		rightTopButton = finder.findImageView(R.id.right_top_button);
		rightTopButton.setOnClickListener(this);
		if (mediaRecorder.getNumberOfCameras() <= 1) {
			rightTopButton.setVisibility(View.INVISIBLE);
		}

		seekBarShell = finder.findRelativeLayout(R.id.seek_bar_shell);

		zoominButton = finder.findImageView(R.id.seek_bar_zoom_in);
		zoominButton.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				boolean b = false;

				switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN:
					handler.sendEmptyMessage(MESSAGE_SEEK_BAR_ZOOM_IN);
					b = true;
					break;

				case MotionEvent.ACTION_UP:
					handler.removeMessages(MESSAGE_SEEK_BAR_ZOOM_IN);
					b = true;
					break;
				}

				return b;
			}
		});
		zoomoutButton = finder.findImageView(R.id.seek_bar_zoom_out);
		zoomoutButton.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				boolean b = false;

				switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN:
					handler.sendEmptyMessage(MESSAGE_SEEK_BAR_ZOOM_OUT);
					b = true;
					break;

				case MotionEvent.ACTION_UP:
					handler.removeMessages(MESSAGE_SEEK_BAR_ZOOM_OUT);
					b = true;
					break;
				}

				return b;
			}
		});

		seekBar = finder.findSeekBar(R.id.seek_bar);
		seekBar.setOnSeekBarChangeListener(this);
		seekBar.setEnabled(true);

		modeSwitchLayout = finder.findLinearLayout(R.id.mode_switch);
		modeSwitchLayout.getViewTreeObserver().addOnGlobalLayoutListener(new OnGlobalLayoutListener() {

			private int counter = 0;

			@Override
			public void onGlobalLayout() {
				readModeSwitchViewsRect();

				int distance = 0;
				if (isShortShootMode()) {
					distance = getCaptureModeDistance(0);
					modeSwitchShortVideoButton.setTag("");
					modeSwitchShortVideoButton.setChecked(true);

				} else if (isLongShootMode()) {
					distance = getShootModeDistance(0);
					modeSwitchLongVideoButton.setTag("");
					modeSwitchLongVideoButton.setChecked(true);
				}
				modeSwitchLayout.scrollTo(distance, 0);
				setViewsByWorkMode();
				counter++;
				if (counter == 5) {
					modeSwitchLayout.getViewTreeObserver().removeGlobalOnLayoutListener(this);
				}
			}
		});

		modeSwitchShortVideoButton = finder.findRadioButton(R.id.mode_switch_short_video_button);
		modeSwitchShortVideoButton.setOnCheckedChangeListener(this);
		modeSwitchShortVideoButton.setClickable(false);

		modeSwitchLongVideoButton = finder.findRadioButton(R.id.mode_switch_long_video_button);
		modeSwitchLongVideoButton.setOnCheckedChangeListener(this);
		modeSwitchLongVideoButton.setClickable(false);

		recordButton = finder.findImageView(R.id.record);
		recordButton.setTag(false);

		recordShadowButton = finder.findImageView(R.id.record_shadow);

		longVideoArea = finder.findImageView(R.id.long_video_area);

		topButtons = finder.findRelativeLayout(R.id.top_buttons);
		bottomButtons = finder.findRelativeLayout(R.id.bottom_buttons);

		mask = finder.findImageView(R.id.mask);

		latestMedia = finder.findImageView(R.id.latest_media);
		latestMedia.setOnClickListener(this);
		if (small != null) {
			latestMedia.setImageBitmap(small);
		}

		back = finder.findImageView(R.id.back);
		back.setOnClickListener(this);
		back.setImageResource(R.drawable.btn_activity_video_shoot_back_selector);

		shootTime = finder.findTextView(R.id.shoot_time);
		shootDone = finder.findTextView(R.id.shooting_done);
		shootDone.setOnClickListener(this);
		shootAgain = finder.findTextView(R.id.shooting_again);
		shootAgain.setOnClickListener(this);

		cancelOrAgainSnapshot = finder.findTextView(R.id.cancle_or_again);
		cancelOrAgainSnapshot.setOnClickListener(this);
		useThisSnapshot = finder.findTextView(R.id.use_this_photo);
		useThisSnapshot.setOnClickListener(this);

		progressShell = finder.findLinearLayout(R.id.progress_shell);
		progressShell.getLayoutParams().height = (int) ZScreen.cmToPixels(PROGRESS_BAR_HEIGHT);
		progress = finder.findAbsoluteLayout(R.id.progress);

		longVideoSlideArea = finder.findAbsoluteLayout(R.id.long_video_slide_area);

		bottomButtons.bringToFront();
		mainArea.bringToFront();
		finder.findAbsoluteLayout(R.id.mask_shell).bringToFront();

		mask.bringToFront();

		LinearLayout animationShell = finder.findLinearLayout(R.id.shoot_animation_shell);
		animationShell.bringToFront();
		ImageView animationCenter = finder.findImageView(R.id.animation_center);
		animationCenter.bringToFront();

		coverAnimationParams = new AbsoluteLayout.LayoutParams(0, 0, 0, 0);

		lastCameraModeStartMillis = SystemClock.elapsedRealtime();

		isAnimationStop = true;
		isAllowTouching = true;

		LowStorageChecker.check(this, this);

		nextWorkMode = getIntent().getIntExtra(WORK_MODE, WORK_MODE_SHOOT);

		cursorView = new ImageView(this);
		gestureDetector = new GestureDetector(this, this);
		scroller = new Scroller(this);
		reenableViewsBuffer = new Vector<View>();

		if (isNeedShowLastCover) {
//			isNeedShowLastCover = false;
			wrappers.clear();
			setLastDiaryImage();

		} else {
			isHasLastMedia = false;
			wrappers.clear();
		}

		checkSplash();
	}

	private void setLastDiaryImage() {
		MyDiary diary = DiaryManager.getInstance().findResentOneDiary();
		
		if (diary != null) {
			
			lastDiaryUUID = diary.diaryuuid;
			String imageURL = null;

			if (diary.attachs.levelattach.attachtype.equals(GsonProtocol.ATTACH_TYPE_VIDEO)) {
				imageURL = diary.attachs.videocover;

			} else { // GsonProtocol.ATTACH_TYPE_PICTURE;
				imageURL = diary.shareimageurl;
			}
			String uid = ActiveAccount.getInstance(MainApplication.getAppInstance()).getUID();

			MediaValue value = AccountInfo.getInstance(uid).mediamapping.getMedia(uid, imageURL);
			if (value != null) {
				String imagePath = Environment.getExternalStorageDirectory() + value.localpath;
				Bitmap bitmap = BitmapFactory.decodeFile(imagePath);

				if (bitmap != null) {
					latestMedia.setImageBitmap(bitmap);
					isHasLastMedia = true;
					isNeedShowLastCover = true;
				}
			}
		}
	}

	private void checkSplash() {
		final String KEY = "IS_FIRST";
		SharedPreferences perferences = PreferenceManager.getDefaultSharedPreferences(this);
		boolean isFirst = perferences.getBoolean(KEY, true);

		final ImageView imageView = finder.findImageView(R.id.cover);

		if (isFirst) {
			SharedPreferences.Editor editor = perferences.edit();
			editor.putBoolean(KEY, false);
			editor.commit();

			imageView.setBackgroundResource(R.drawable.shoot_dir);
			imageView.setVisibility(View.VISIBLE);
			imageView.setOnClickListener(new OnClickListener() {

				AlphaAnimation animation;

				@Override
				public void onClick(View v) {
					if (animation == null) {
						animation = new AlphaAnimation(1, 0);
						animation.setDuration(ALPHA_ANIMATIONG_MILLIS);
						animation.setAnimationListener(new AnimationListener() {

							@Override
							public void onAnimationStart(Animation animation) {
							}

							@Override
							public void onAnimationRepeat(Animation animation) {
							}

							@Override
							public void onAnimationEnd(Animation animation) {
								imageView.setBackgroundResource(0);
								imageView.setVisibility(View.INVISIBLE);

								handler.postDelayed(new Runnable() {

									@Override
									public void run() {
										startCameraAnimation();
									}
								}, CAMERA_OPEN_ANIMATION_HOLD_MILLIS);
							}
						});
						imageView.startAnimation(animation);
					}
				}
			});
			imageView.bringToFront();

		} else {
			handler.postDelayed(new Runnable() {

				@Override
				public void run() {
					startCameraAnimation();
				}
			}, CAMERA_OPEN_ANIMATION_HOLD_MILLIS);
		}
	}

	private void startCameraAnimation() {
		final LinearLayout animationShell = finder.findLinearLayout(R.id.shoot_animation_shell);

		final ImageView animationUp = finder.findImageView(R.id.animation_up);
		final ImageView animationDown = finder.findImageView(R.id.animation_down);
		final ImageView animationCenter = finder.findImageView(R.id.animation_center);
		animationCenter.bringToFront();

		TranslateAnimation upAnimation = new TranslateAnimation(
				Animation.RELATIVE_TO_SELF, 0, 
				Animation.RELATIVE_TO_SELF, 0, 
				Animation.RELATIVE_TO_SELF, 0,
				Animation.RELATIVE_TO_SELF, -1);
		upAnimation.setDuration(CAMERA_OPEN_ANIMATION_MILLIS);
		upAnimation.setFillAfter(true);
		
		TranslateAnimation downAnimation = new TranslateAnimation(
				Animation.RELATIVE_TO_SELF, 0, 
				Animation.RELATIVE_TO_SELF, 0, 
				Animation.RELATIVE_TO_SELF, 0,
				Animation.RELATIVE_TO_SELF, 1);
		downAnimation.setDuration(CAMERA_OPEN_ANIMATION_MILLIS);
		downAnimation.setFillAfter(true);
		
		AlphaAnimation alphaAnimation = new AlphaAnimation(1, 0);
		ScaleAnimation scaleAnimation = new ScaleAnimation(1, 0.2f, 1, 0.2f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
		
		AnimationSet set = new AnimationSet(true);
		set.setDuration(CAMERA_OPEN_ANIMATION_MILLIS);
		set.setFillAfter(true);
		set.addAnimation(alphaAnimation);
		set.addAnimation(scaleAnimation);
		set.setAnimationListener(new AnimationListener() {
			
			@Override
			public void onAnimationStart(Animation animation) {
			}
			
			@Override
			public void onAnimationRepeat(Animation animation) {
			}
			
			@Override
			public void onAnimationEnd(Animation animation) {
				animationShell.setVisibility(View.INVISIBLE);
				animationUp.setVisibility(View.INVISIBLE);
				animationDown.setVisibility(View.INVISIBLE);
				animationCenter.setVisibility(View.INVISIBLE);
			}
		});
		
		animationUp.startAnimation(upAnimation);
		animationDown.startAnimation(downAnimation);
		animationCenter.startAnimation(set);
	}

	private void initMediaRecoder() {
		effectUtils = new EffectUtils(this);
		effectUtils.parseXml("effectcfg/effectlist.xml");

		if (PluginUtils.isPluginMounted()) {
			CamaraMode mode = null;
			if (nextWorkMode == WORK_MODE_SHOOT) {
				mode = CamaraMode.DEF_VIDEO_AUTO;

			} else if (nextWorkMode == WORK_MODE_CAPTURE) {
				mode = CamaraMode.DEF_PHOTO_COMMON;
			}

			effects = new XEffects();
			mediaRecorder = new XVideoRecorder(this, effects, this, mode);
			currentWorkMode = nextWorkMode;
//			setCameraMode(mode, null);
		} else {
			mediaRecorder = new XVideoRecorder(this, effects, this, CamaraMode.DEF_PHOTO_HEIGHT);
		}
		mediaRecorder.setCurrentPreviewMode(PreviewMode.V_SHOW_MODLE_4P3);
		mediaRecorder.setXSurfaceSizeChange(this);
	}

	public static void startOnShortShootMode(Activity activity, boolean isNeedShowLastCover) {
		VideoShootActivity2.isNeedShowLastCover = isNeedShowLastCover;

		Intent intent = new Intent(activity, VideoShootActivity2.class);
		intent.putExtra(WORK_MODE, WORK_MODE_SHOOT);
		activity.startActivity(intent);
		activity.overridePendingTransition(R.anim.animation_slide_in_from_right, R.anim.animation_slide_out_to_left);
	}

	public static void startOnCaptureMode(Activity activity) {
		Intent intent = new Intent(activity, VideoShootActivity2.class);
		intent.putExtra(WORK_MODE, WORK_MODE_CAPTURE);
		activity.startActivity(intent);
		activity.overridePendingTransition(R.anim.animation_slide_in_from_right, R.anim.animation_slide_out_to_left);
	}
	
	@Override
	public boolean dispatchTouchEvent(MotionEvent event) {
		rootWindow.getGlobalVisibleRect(rootWindowRect);
		if (!isSnapshoting && !isAutoSnapshoting) {
			checkFlingGesture(event);
		}
		
		return super.dispatchTouchEvent(event);
	}

	private boolean isTwoPointer; // 双指触摸;

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		boolean b = false;
		int x = (int) event.getRawX();
		int y = (int) event.getRawY();
		ZLog.e();
		
		boolean up = event.getAction() == MotionEvent.ACTION_UP 
				|| event.getAction() == MotionEvent.ACTION_POINTER_UP
				|| event.getAction() == MotionEvent.ACTION_POINTER_1_UP 
				|| event.getAction() == MotionEvent.ACTION_POINTER_2_UP;

		if (isTwoPointer && up && event.getPointerCount() == 1) {
			isTwoPointer = false;
			isAllowTouching = true;

			if (isRecording) {
				b = true;
				handler.removeCallbacks(shootingRunnable1);
				handler.removeCallbacks(shootingRunnable2);
				delayToPauseShooting();
			}
		}

		previewLayout.getGlobalVisibleRect(previewRect);
		mainWindow.getGlobalVisibleRect(mainWindowRect);
		rootWindow.getGlobalVisibleRect(rootWindowRect);
		
		if (rootWindowRect.contains(x, y)) {//mainWindowRect.contains(x, y) || previewRect.contains(x, y)
			
			if (event.getPointerCount() == 1 && !isTwoPointer) {
				
				if (!b && isShortShootMode()) {
					b = onTouchShooting(event, true);
				}
				
//				if (!b && !isSnapshoting && !isAutoSnapshoting) {
//					b = checkFlingGesture(event);
//				}
				
				if (!b && !isSnapshoting && !isAutoSnapshoting) {
					int newY = y - mainWindowRect.top;
					event.setLocation(x, newY);
					b = mediaRecorder.onTouchFocusEvent(event);
				}

			} else {
				isTwoPointer = true;
				handler.removeCallbacks(shootingRunnable1);
				handler.removeCallbacks(shootingRunnable2);
				if (!b) {
					b = checkZoomEvent(event);
				}
			}
		}

		return b;
	}

	private boolean checkFlingGesture(MotionEvent event) {
		boolean b = false;

//		if (mainWindowRect.contains((int) event.getRawX(), (int) event.getRawY()) ||
//				previewRect.contains((int) event.getRawX(), (int) event.getRawY())) {
		if (rootWindowRect.contains((int) event.getRawX(), (int) event.getRawY())) {
			b = gestureDetector.onTouchEvent(event);
		}

		return b;
	}

	private boolean checkZoomEvent(MotionEvent event) {
		boolean b = false;

		if (!isRecording || isShootPausing || isLongShootMode()) {
			b = mediaRecorder.onTouchZoomEvent(event);
			currentCameraZoom = mediaRecorder.getCameraZoom();
			if (currentCameraZoom == 0 || currentCameraZoom == mediaRecorder.getCameraMaxZoom()) { // 条件达成时,
																						// b是false,
																						// 为了解决这个问题,
																						// 强制设置成true;
				b = true;
			}

			if (b) {
				if (mediaRecorder.getCameraZoom() == 0) { // 焦距缩小到0后, 隐藏焦距控制条;
					seekBar.setProgress(0);
					handler.postDelayed(new Runnable() {

						@Override
						public void run() {
							if (mediaRecorder.getCameraZoom() == 0) {
								seekBarShell.setVisibility(View.GONE);
							}
						}
					}, LOOP_MESSAGE_GAP_MILLIS * 2);

				} else {
					if (seekBarShell.getVisibility() == View.GONE) {
						seekBarShell.setVisibility(View.VISIBLE);
					}
					float zoomProgress = (float) mediaRecorder.getCameraZoom() / (float) mediaRecorder.getCameraMaxZoom() * 100;
					seekBar.setProgress((int) zoomProgress);
				}
			}
		}

		return b;
	}

	private void release() {
		ZLog.alert();
		ZLog.e();
		if (mediaRecorder != null) {
			mediaRecorder.release();
		}
		if (PluginUtils.isPluginMounted() && effects != null) {
			effects.release();
		}
	}

	@Override
	public void onBackPressed() {
		if (isRecording) {
			switchRecordState();

		} else {
			super.onBackPressed();
			this.overridePendingTransition(R.anim.animation_slide_in_from_left, R.anim.animation_slide_out_to_right);
		}
		release();
	}

	@Override
	protected void onResume() {
		ZLog.e();
		if (!isRecording) {
			init();
		}
		handler.sendEmptyMessage(MESSAGE_CHANGE_CURSOR_COLOR);
		// ZSensorManager.registerListener(this, Sensor.TYPE_ACCELEROMETER,
		// SensorManager.SENSOR_DELAY_NORMAL);
		getZReceiverManager().registerZReceiver(new HomeKeyPressedReceiver());

		// 2014-04-23;
		CmmobiClickAgentWrapper.onResume(this);

		super.onResume();
	}

	@Override
	protected void onPause() {
		ZLog.e();
		
		if (isRecording) {
			isThrowAwayThisVideo = true;
			if (isAllowStopShooting()) {
				isThrowAwayThisVideo = false;
			}
			
			if (isThrowAwayThisVideo) {
				stopShooting(!isThrowAwayThisVideo, false, false);
				finish();
				this.overridePendingTransition(R.anim.animation_slide_in_from_left, R.anim.animation_slide_out_to_right);
				release();
				
			} else {
				isBackButtonPressed = true;
				stopShooting(true, false, false);
			}
		}
		
		getZReceiverManager().unregisterAllZReceiver(true);

		cancelAutoSnapshot();

		handler.removeMessages(MESSAGE_TAKE_PICTURE);
		handler.removeMessages(MESSAGE_CHANGE_CURSOR_COLOR);
		handler.removeMessages(MESSAGE_UPDATE_SHOOT_STATE);
		// 2014-04-23;
		CmmobiClickAgentWrapper.onPause(this);

		super.onPause();
	}

	private boolean isAllowStopShooting() {
		return isRecording && videoShootTotalMillis >= MIN_RECORD_MILLIS;
	}

	private void delayStopShooting(boolean toEditPage) {
		if (mediaRecorder.isRecording()) {

			if (isAllowStopShooting()) {
				stopShooting(true, false, toEditPage);

			} else {
				continueShooting();
				long millis = MIN_RECORD_MILLIS - videoShootTotalMillis + SMALL_GAP_MILLIS * 4;
				ZThread.sleep(millis);
				stopShooting(true, false, toEditPage);
			}
		}
	}

	@Override
	protected void onStop() {
		ZLog.e();
		
		super.onStop();
	}

	@Override
	protected void onDestroy() {
		ZLog.e();
		delayStopShooting(true);
		release();
		MainApplication.getAppInstance().removeActivity(this);
		super.onDestroy();
	}

	private void resumeCameraSettings(boolean needToaddView) {
		if (seekBarShell.getVisibility() == View.VISIBLE) {
			seekBar.setProgress(0);
		}
		mediaRecorder.setCurrentPreviewMode(currentPreviewMode);
		if (needToaddView) {
			mainWindow.removeAllViews();
			mainWindow.addView(mediaRecorder.getXSurfaceView());
		}
	}

	private boolean isSliding;
	private boolean isSlideEnough;
	private int realX;

	@Override
	public boolean onTouch(View view, MotionEvent event) {
		if (isShortShootMode()) {
			int x = (int) event.getRawX();
			int y = (int) event.getRawY();

			recordButton.getGlobalVisibleRect(recordButtonRect);
			longVideoArea.getGlobalVisibleRect(longVideoAreaRect);

			switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN:
				return onTouchShooting(event, false);

			case MotionEvent.ACTION_MOVE:
				if (!isSliding && isRecording && videoShootTotalMillis >= 200 && recordButtonRect.contains(x, y)) {
					isSliding = true;
					recordButton.setBackgroundResource(R.drawable.short_video_pressed_empty);
					recordShadowButton.setImageResource(R.drawable.short_video_pressed_dragging);
					recordShadowButton.setVisibility(View.VISIBLE);
				}

				if (isSliding) { // && Math.abs(x - firstMoveX) >
									// ZScreen.cmToPixels(1.0f)
					realX = x;

					if (realX <= longVideoAreaRect.left - 10) {
						isSlideEnough = true;
						if (isRecording) {
							recordShadowButton.setImageResource(R.drawable.btn_record_continue_button_selector);
						} else {
							recordShadowButton.setImageResource(R.drawable.btn_record_start_button_selector);
						}
						// 2014-04-23;
						CmmobiClickAgentWrapper.onEvent(this, CDR_KEY_VIDEO_CHANGE);
					}

					if (realX < longVideoAreaRect.left - 15) {
						realX = longVideoAreaRect.left - 15;
					}

					if (realX + recordButtonRect.width() > recordButtonRect.right) {
						realX = recordButtonRect.right - recordButtonRect.width();
					}

					longVideoSlideArea.scrollTo(-realX, 0);
				}
				break;

			case MotionEvent.ACTION_UP:
			case MotionEvent.ACTION_CANCEL:
			case MotionEvent.ACTION_OUTSIDE:
			case MotionEvent.ACTION_POINTER_UP:
				if (isSliding) {
					scroller.startScroll(-realX, 0, -(recordButtonRect.left - realX), 0, SLIDE_TO_LONG_VIDEO_ANIMATION_MILLIS);
					handler.sendEmptyMessage(MESSAGE_SLIDE_TO_LONG_VIDEO_ANIMATION);

					if (isSlideEnough) {
						return true;
					}
				}
				if (isShortShootMode()) {
					return onTouchShooting(event, false);
				}
			}
		}

		return true;
	}

	private boolean recordButtonTouchActionBoolean = false;
	private long downMillis;

	/**
	 * 开始或继续拍摄的Runnable对象;
	 */
	private Runnable shootingRunnable1 = new Runnable() {

		@Override
		public void run() {
			downMillis = System.currentTimeMillis();
			isTouching = true;
			recordButtonTouchActionBoolean = true;
			if (!isRecording) {
				handler.sendEmptyMessage(MESSAGE_START_SHOOTING);

			} else if (isShootPausing) {
				handler.removeMessages(MESSAGE_PAUSE_SHOOTING);
				handler.sendEmptyMessage(MESSAGE_CONTINUE_SHOOTING);

			} else if (isRecording) {
				handler.removeMessages(MESSAGE_PAUSE_SHOOTING);
			}
		}
	};

	private Runnable shootingRunnable2 = new Runnable() {

		@Override
		public void run() {
			downMillis = System.currentTimeMillis();
			recordButtonTouchActionBoolean = true;
			if (!isRecording) {
				handler.sendEmptyMessage(MESSAGE_START_SHOOTING);

			} else if (isShootPausing) {
				handler.removeMessages(MESSAGE_PAUSE_SHOOTING);
				handler.sendEmptyMessage(MESSAGE_CONTINUE_SHOOTING);

			} else if (isRecording) {
				handler.removeMessages(MESSAGE_PAUSE_SHOOTING);
			}
		}
	};

	private boolean onTouchShooting(MotionEvent event, boolean holding) {
		recordButtonTouchActionBoolean = false;
		int x = (int) event.getRawX();
		int y = (int) event.getRawY();

		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			
			if (isAllowTouching) {
				handler.removeCallbacks(shootingRunnable1);
				handler.removeCallbacks(shootingRunnable2);
				if (holding) {
					int pixels = (int) ZScreen.cmToPixels(1.5f);
					shortShootLeftSlideRect.left = x - pixels;
					shortShootLeftSlideRect.right = x + pixels;
					shortShootLeftSlideRect.top = y - pixels;
					shortShootLeftSlideRect.bottom = y + pixels;

					handler.postDelayed(shootingRunnable1, CONTINUE_SHOOT_HOLD_MILLIS);

				} else {
					handler.post(shootingRunnable2);
				}
			}
			break;
			
		case MotionEvent.ACTION_MOVE:
			if (isShortShootMode() && x <= shortShootLeftSlideRect.left - 10) {
				modeSwitchLongVideoButton.setChecked(true);
				setToLongShootMode();
				setViewsByWorkMode();
				recordButtonTouchActionBoolean = true;
				
				if (!isRecording) {
					handler.removeCallbacks(shootingRunnable1);
					handler.removeCallbacks(shootingRunnable2);
				}
				
				// 2014-04-23;
				CmmobiClickAgentWrapper.onEvent(this, CDR_KEY_VIDEO_CHANGE);
			}
			
//			if (isRecording) {
//				
//				if (isShortShootMode() && x <= shortShootLeftSlideRect.left - 10) {
//					modeSwitchLongVideoButton.setChecked(true);
//					setToLongShootMode();
//					setViewsByWorkMode();
//					// 2014-04-23;
//					CmmobiClickAgentWrapper.onEvent(this, CDR_KEY_VIDEO_CHANGE);
//				}
//				recordButtonTouchActionBoolean = true;
//				
//			} else {
//				recordButtonTouchActionBoolean = false;
//			}
			break;

		case MotionEvent.ACTION_UP:
		case MotionEvent.ACTION_CANCEL:
		case MotionEvent.ACTION_OUTSIDE:
		case MotionEvent.ACTION_POINTER_UP:
			handler.removeCallbacks(shootingRunnable1);
			handler.removeCallbacks(shootingRunnable2);
			
			if (holding) {
				if (isTouching) {
					isTouching = false;
					
					recordButtonTouchActionBoolean = true;
					shortShootLeftSlideRect.setEmpty();
					
					if (isRecording) {
						delayToPauseShooting();
					}
				}
				
			} else {
				recordButtonTouchActionBoolean = true;
				delayToPauseShooting();
			}
			handler.removeMessages(MESSAGE_START_SHOOTING);
			isAllowTouching = true;
			break;
		}

		return recordButtonTouchActionBoolean;
	}

	private void delayToPauseShooting() {
		long gap = SHOOTING_GAP_MILLIS - (System.currentTimeMillis() - downMillis);
		if (gap < 0) {
			gap = 0;
		}
		handler.removeMessages(MESSAGE_PAUSE_SHOOTING);
		handler.sendEmptyMessageDelayed(MESSAGE_PAUSE_SHOOTING, gap);
	}

	@Override
	public boolean handleMessage(final Message message) {
		switch (message.what) {
		case MESSAGE_CHANGE_CURSOR_COLOR:
			setCurrentCursorColor();
			break;

		case MESSAGE_LEAVE_SHOOT_PAGE_WHEN_DIARY_OK:
			enterLastDiary();
			break;

		case MESSAGE_GOTO_EDIT_SHARE_PAGE:
			MyDiary diary = DiaryManager.getInstance().findMyDiaryByUUID(lastDiaryUUID);

			if (diary != null && diary.attachs != null) {
				handler.removeMessages(MESSAGE_GOTO_EDIT_SHARE_PAGE);
				gotoEditShareActivity();
				release();

			} else {
				handler.sendEmptyMessageDelayed(MESSAGE_GOTO_EDIT_SHARE_PAGE, LOOP_MESSAGE_GAP_MILLIS);
			}
			break;

		case MESSAGE_SEEK_BAR_ZOOM_IN:
			zoomIn();
			handler.sendEmptyMessageDelayed(MESSAGE_SEEK_BAR_ZOOM_IN, 20);
			break;

		case MESSAGE_SEEK_BAR_ZOOM_OUT:
			zoomOut();
			handler.sendEmptyMessageDelayed(MESSAGE_SEEK_BAR_ZOOM_OUT, 20);
			break;

		case MESSAGE_SWITCH_MODE_ANIMATION:
			if (scroller.computeScrollOffset()) {
				int position = scroller.getCurrX();
				int minLimit = -(modeSwitchLayoutRect.width() - captureButtonRect.width()) / 2;
				int maxLimit = -((modeSwitchLayoutRect.width() - shootButtonRect.width()) / 2 - captureButtonRect.width());

				if (position < minLimit) {
					position = minLimit;
				} else if (position > maxLimit) {
					position = maxLimit;
				}

				modeSwitchLayout.scrollTo(position, 0);
				handler.sendEmptyMessage(MESSAGE_SWITCH_MODE_ANIMATION);

			} else {
				setViewsByWorkMode();
				handler.sendEmptyMessage(MESSAGE_SWITCH_WORK_MODE);
			}
			break;

		case MESSAGE_SLIDE_TO_LONG_VIDEO_ANIMATION:
			boolean b2 = true;
			if (scroller.computeScrollOffset()) {
				int position = scroller.getCurrX();

				if (Math.abs(position) >= 1.0f) {
					longVideoSlideArea.scrollTo(position, 0);
					handler.sendEmptyMessage(MESSAGE_SLIDE_TO_LONG_VIDEO_ANIMATION);
					b2 = false;
				}
			}

			if (b2) {
				isSliding = false;

				if (isSlideEnough) {
					isSlideEnough = false;
					modeSwitchLongVideoButton.setChecked(true);
					setToLongShootMode();
					setViewsByWorkMode();

				} else {
					recordButton.setBackgroundResource(R.drawable.btn_activity_short_shoot_button_selector);
				}
				recordShadowButton.setVisibility(View.INVISIBLE);
			}
			break;

		case MESSAGE_SWITCH_WORK_MODE:
			switchWorkMode();
			setViewsByWorkMode();
			if (isRecording && !isShootPausing) {
				modeSwitchShortVideoButton.setVisibility(View.INVISIBLE);
			}
			break;

		case DiaryController.DIARY_REQUEST_DONE:
			if (isBackButtonPressed) {
				finish();
				this.overridePendingTransition(R.anim.animation_slide_in_from_left, R.anim.animation_slide_out_to_right);
				release();
			}
			
			if (isNeedGotoPreviewActivity) {
				isNeedGotoPreviewActivity = false;
				gotoPreviewActivity();
				release();
			}
			break;

		case MESSAGE_START_SHOOTING:
			startShooting();
			break;

		case MESSAGE_PAUSE_SHOOTING:
			boolean b = true;
			if (message.obj != null) {
				b = (Boolean) message.obj;
			}
			pauseShooting(b);
			break;

		case MESSAGE_CONTINUE_SHOOTING:
			continueShooting();
			break;

		case MESSAGE_TURN_ON_FLASHLIGHT_TORCH:
			if (!isFlashlightOn && isAllowFlashlight && mediaRecorder.isSupportFlashMode(FlashMode.FLASH_MODE_TORCH)) {
				mediaRecorder.setFlashMode(FlashMode.FLASH_MODE_TORCH);
				isFlashlightOn = true;
			}
			break;

		case MESSAGE_TURN_ON_FLASHLIGHT_ON:
			if (!isFlashlightOn && isAllowFlashlight && mediaRecorder.isSupportFlashMode(FlashMode.FLASH_MODE_ON)) {
				mediaRecorder.setFlashMode(FlashMode.FLASH_MODE_ON);
				isFlashlightOn = true;
			}
			break;

		case MESSAGE_TURN_ON_FLASHLIGHT_AUTO:
			if (!isFlashlightOn && isAllowFlashlight && mediaRecorder.isSupportFlashMode(FlashMode.FLASH_MODE_AUTO)) {
				mediaRecorder.setFlashMode(FlashMode.FLASH_MODE_AUTO);
				isFlashlightOn = true;
			}
			break;

		case MESSAGE_TURN_OFF_FLASHLIGHT:
			if (isFlashlightOn) {
				mediaRecorder.setFlashMode(FlashMode.FLASH_MODE_OFF);
				isFlashlightOn = false;
			}
			break;

		case MESSAGE_TAKE_PICTURE:
			pictureDiaryUUID = DiaryController.getNextUUID();
			String attachUUID = DiaryController.getNextUUID();

			DiaryWrapper wrapper = DiaryController.getInstanse().requestNewDiary(handler, pictureDiaryUUID, attachUUID, GsonProtocol.ATTACH_TYPE_PICTURE,
					GsonProtocol.SUFFIX_JPG, GsonProtocol.EMPTY_VALUE, GsonProtocol.EMPTY_VALUE, GsonProtocol.EMPTY_VALUE,
					CommonInfo.getInstance().getLongitude(), CommonInfo.getInstance().getLatitude(), DiaryController.getPositionString1(),
					GsonProtocol.EMPTY_VALUE, String.valueOf(System.currentTimeMillis()));

			String path = DiaryController.getInstanse().getFullPathByType(GsonProtocol.ATTACH_TYPE_PICTURE, attachUUID, GsonProtocol.SUFFIX_JPG);

			mediaRecorder.takePicture(this, path);

			addToDiaryWrappers(wrapper);
			break;

		case MESSAGE_UPDATE_SHOOT_STATE:
			timing();
			setShootState();
			
			long delay = SHOOT_TIME_UPDATE_DURATION;
			long gap = System.currentTimeMillis() - lastUpdateMillis;
			
			lastUpdateMillis = System.currentTimeMillis();
			
			if (gap >= SHOOT_TIME_UPDATE_DURATION) {
				delay = 0;
			} else {
				delay = SHOOT_TIME_UPDATE_DURATION - gap;
			}
			handler.sendEmptyMessageDelayed(MESSAGE_UPDATE_SHOOT_STATE, delay);
			break;

		case MESSAGE_CHANGE_CAMERA:
			ZThread.sleep(20);

			ZMillissecondLogger.begin();

			mediaRecorder.changeCamera();

			ZMillissecondLogger.end();
			ZLog.e(ZMillissecondLogger.get());

			checkFlashlight();
			isFlashlightOn = false;
			isFrontCamera = mediaRecorder.isFrontCamera();

			resumeCameraSettings(false);

			if (!isFrontCamera) {
				controlFlashlight();
			}

			if (!isSupportZoom()) {
				seekBarShell.setVisibility(View.GONE);
			}
			restoreZoomState();

			handler.postDelayed(new Runnable() {

				@Override
				public void run() {
					rightTopButton.setEnabled(true);
				}
			}, 200);

			String camera = isFrontCamera ? CDR_LABEL_CAMERA_FRONT : CDR_LABEL_CAMERA_BACK;
			// 2014-04-23;
			CmmobiClickAgentWrapper.onEventDuration(
					this, CDR_KEY_SHP_SW_CAM, 
					camera, 
					SystemClock.elapsedRealtime() - lastCameraModeStartMillis);
			
			lastCameraModeStartMillis = SystemClock.elapsedRealtime();
			break;

		case MESSAGE_PHOTO_ALBUM_UPDATING_ANIMATION:
			Rect toRect = new Rect();
			Rect fromRect = new Rect();
			latestMedia.getGlobalVisibleRect(toRect);
			mask.getGlobalVisibleRect(fromRect);

			float scaleX = (float) toRect.width() / (float) mask.getWidth() - 0.01f;
			float scaleY = (float) toRect.height() / (float) mask.getHeight() - 0.01f;

			final Animation transAnimation = new TranslateAnimation(0, toRect.left - fromRect.left, 0, toRect.top - fromRect.top);
			final Animation scaleAnimation = new ScaleAnimation(1f, scaleX, 1f, scaleY, Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF, 0.0f);

			final AnimationSet animationSet = new AnimationSet(true);
			animationSet.addAnimation(scaleAnimation);
			animationSet.addAnimation(transAnimation);
			animationSet.setInterpolator(new LinearInterpolator());
			animationSet.setDuration(THUMBNAIL_ANIMATION_DURATION);
			animationSet.setAnimationListener(new AnimationListener() {
				@Override
				public void onAnimationStart(Animation animation) {
				}

				@Override
				public void onAnimationRepeat(Animation animation) {
				}

				@Override
				public void onAnimationEnd(Animation anim) {
					mask.setImageResource(R.drawable.empty_bitmap);
					maskBitmap.recycle();

					AlphaAnimation animation = new AlphaAnimation(0.4f, 1.0f);
					animation.setDuration(400);
					animation.setAnimationListener(new AnimationListener() {

						@Override
						public void onAnimationStart(Animation animation) {
						}

						@Override
						public void onAnimationRepeat(Animation animation) {
						}

						@Override
						public void onAnimationEnd(Animation animation) {
							cancelReenableView();
							isSnapshoting = false;
							isAnimationStop = true;

							if (needAutoTakeTimes > 0) {

								long millis = CONTINUOUS_CAPTURE_GAP_MILLIS - (SystemClock.elapsedRealtime() - lastCameraModeStartMillis);
								lastCameraModeStartMillis = SystemClock.elapsedRealtime();
								if (millis < 0) {
									millis = 0;
								}

								needAutoTakeTimes--;
								Message message = new Message();
								message.what = MESSAGE_TAKE_PICTURE;

								if (getCameraMode() == CamaraMode.DEF_PHOTO_COMMON) {
									handler.sendEmptyMessageDelayed(MESSAGE_TURN_ON_FLASHLIGHT_TORCH, millis);
									handler.sendMessageDelayed(message, TURN_ON_FLASHLIGHT_DELAY_MILLIS + millis);

								} else if (getCameraMode() == CamaraMode.DEF_PHOTO_HEIGHT) {
									handler.sendEmptyMessageDelayed(MESSAGE_TURN_ON_FLASHLIGHT_ON, millis);
									handler.sendMessageDelayed(message, TURN_ON_FLASHLIGHT_DELAY_MILLIS + millis);
								}
							}

							if (thumbnailAnimationType == THUMBNAIL_ANIMATION_VIDEO) {
								thumbnailAnimationType = 0;
								// handler.sendEmptyMessage(MESSAGE_GOTO_EDIT_SHARE_PAGE);
							}
						}
					});
					latestMedia.setImageBitmap(small);
					latestMedia.clearAnimation();
					latestMedia.startAnimation(animation);
				}
			});

			mask.setImageBitmap(maskBitmap);
			mask.clearAnimation();
			mask.invalidate();
			handler.postDelayed(new Runnable() {

				@Override
				public void run() {
					mask.startAnimation(animationSet);
				}
			}, SMALL_GAP_MILLIS);
			break;
		}
		return true;
	}

	private void addToDiaryWrappers(DiaryWrapper wrapper) {
		if (wrapper != null) {
			wrappers.add(wrapper);
		}
	}

	private void switchWorkMode() {
		ZLog.e(currentWorkMode);
		ZLog.e(nextWorkMode);

		if (currentWorkMode != nextWorkMode) {
			currentWorkMode = nextWorkMode;

			if (isCaptureMode()) {
				if (getCameraMode() == CamaraMode.DEF_VIDEO_HEIGHT) {
					setCameraMode(CamaraMode.DEF_PHOTO_HEIGHT, mainWindow);
				} else {
					setCameraMode(CamaraMode.DEF_PHOTO_COMMON, mainWindow);
				}

			} else if (isShortShootMode()) {
				if (getCameraMode() == CamaraMode.DEF_PHOTO_HEIGHT) {
					setCameraMode(CamaraMode.DEF_VIDEO_HEIGHT, mainWindow);
				} else {
					setCameraMode(CamaraMode.DEF_VIDEO_AUTO, mainWindow);
				}
			}
			mediaRecorder.setXSurfaceSizeChange(this);
			resumeCameraSettings(true);
		}
	}

	private void setViewsByWorkMode() {
		if (isCaptureMode()) {
			modeSwitchShortVideoButton.setVisibility(View.INVISIBLE);
			modeSwitchLongVideoButton.setVisibility(View.INVISIBLE);

			longVideoArea.setVisibility(View.GONE);
			progressShell.setVisibility(View.INVISIBLE);

			recordButton.setBackgroundResource(R.drawable.btn_activity_shoot_capture_selector);
			recordButton.setOnClickListener(this);
			recordButton.setOnTouchListener(null);

			shootTime.setVisibility(View.GONE);
			shootDone.setVisibility(View.GONE);
			shootAgain.setVisibility(View.GONE);
			cancelOrAgainSnapshot.setVisibility(View.VISIBLE);
			useThisSnapshot.setVisibility(View.VISIBLE);

			hideTip(false);

		} else if (isShortShootMode()) {
			modeSwitchShortVideoButton.setVisibility(View.VISIBLE);
			modeSwitchLongVideoButton.setVisibility(View.VISIBLE);

			longVideoArea.setVisibility(View.INVISIBLE);
			progressShell.setVisibility(View.VISIBLE);

			recordButton.setBackgroundResource(R.drawable.btn_activity_short_shoot_button_selector);
			recordButton.setOnClickListener(null);
			recordButton.setOnLongClickListener(null);
			recordButton.setOnTouchListener(this);

			shootTime.setVisibility(View.GONE);
			shootDone.setVisibility(View.GONE);
			shootAgain.setVisibility(View.GONE);
			cancelOrAgainSnapshot.setVisibility(View.INVISIBLE);
			useThisSnapshot.setVisibility(View.INVISIBLE);

			showPressToShootingTip();
			addCurrentCursor();

		} else if (isLongShootMode()) {
			modeSwitchShortVideoButton.setVisibility(View.VISIBLE);
			modeSwitchLongVideoButton.setVisibility(View.VISIBLE);

			longVideoArea.setVisibility(View.GONE);
			progressShell.setVisibility(View.INVISIBLE);

			if (isShootPausing) {
				recordButton.setBackgroundResource(R.drawable.btn_record_start_button_selector);

			} else if (isRecording) {
				recordButton.setBackgroundResource(R.drawable.btn_record_continue_button_selector);

			} else {
				recordButton.setBackgroundResource(R.drawable.btn_record_start_button_selector);
			}

			recordButton.setOnClickListener(this);
			recordButton.setOnLongClickListener(null);
			recordButton.setOnTouchListener(null);

			shootTime.setVisibility(View.VISIBLE);
			shootTime.setText("00:00:00");
			shootDone.setVisibility(View.GONE);
			shootAgain.setVisibility(View.GONE);
			cancelOrAgainSnapshot.setVisibility(View.INVISIBLE);
			useThisSnapshot.setVisibility(View.INVISIBLE);
			LinearLayout animationShell = finder.findLinearLayout(R.id.shoot_animation_shell);
			animationShell.setVisibility(View.INVISIBLE);

			hideTip(false);
		}

		handler.postDelayed(new Runnable() {

			@Override
			public void run() {
				mainWindow.setVisibility(View.VISIBLE);
			}
		}, SMALL_GAP_MILLIS);
	}

	private void startTimer() {
		if (isWifiShooting) {
			videoShootTotalMillis = SHOOT_REMAIN_MILLIS;
		} else {
			videoShootTotalMillis = 0;
		}
		videoClipStartMillis = System.currentTimeMillis();
		handler.sendEmptyMessage(MESSAGE_UPDATE_SHOOT_STATE);
	}

	private void continueTimer() {
		videoClipStartMillis = System.currentTimeMillis();
		handler.sendEmptyMessage(MESSAGE_UPDATE_SHOOT_STATE);
	}

	private void timing() {
		long clipMillis = System.currentTimeMillis() - videoClipStartMillis;

		if (isWifiShooting) {
			videoShootTotalMillis -= clipMillis;
			if (videoShootTotalMillis < 0) {
				videoShootTotalMillis = 0;
				switchRecordState();
			}
		} else {
			videoShootTotalMillis += clipMillis;
		}
		videoClipStartMillis = System.currentTimeMillis();

		showProgress();

		if(videoShootTotalMillis <= 0) {
			ZLog.e("videoShootTotalMillis is 0 or get invalid value from framework layer! (" + videoShootTotalMillis + ")"
					+ "; getStatus = " + mediaRecorder.getStatus());
		}

		videoShootTotalMillis = mediaRecorder.getRecordingTime();
		if (isShortShootMode() && videoShootTotalMillis >= MAX_SHORT_VIDEO_MILLIS) {
			isAllowTouching = false;
			stopShooting(true, false, true);

		} else if (videoShootTotalMillis >= MIN_RECORD_MILLIS) {
			shootDone.setEnabled(true);
			shootDone.setVisibility(View.VISIBLE);
			if (isShortShootMode()) {
				shootAgain.setEnabled(true);
				shootAgain.setVisibility(View.VISIBLE);
			}
		}

		if (!LowStorageChecker.check(this, this) && mediaRecorder.isRecording()) {
			switchRecordState();
		}
	}

	private void showProgress() {
		LayoutParams params = progress.getLayoutParams();
		params.width = (int) (videoShootTotalMillis / (float) MAX_SHORT_VIDEO_MILLIS * ZScreen.getWidth());
		progress.setLayoutParams(params);
		addCurrentCursor();
	}

	private void addPausePointToProgress() {
		int blockWidth = (int) ZScreen.cmToPixels(0.03f);
		ImageView view = new ImageView(this);
		view.setBackgroundColor(PAUSE_POINT_COLOR);

		AbsoluteLayout.LayoutParams params = new AbsoluteLayout.LayoutParams(
				blockWidth, 
				LayoutParams.FILL_PARENT, 
				progress.getLayoutParams().width - blockWidth, 
				0);
		progress.addView(view, params);
		addCurrentCursor();
	}

	private void addCurrentCursor() {
		int blockWidth = (int) ZScreen.cmToPixels(0.07f);
		if (progress.getLayoutParams().width < blockWidth) {
			progress.getLayoutParams().width = blockWidth;
		}
		AbsoluteLayout.LayoutParams params = new AbsoluteLayout.LayoutParams(
				blockWidth, 
				LayoutParams.FILL_PARENT, 
				progress.getLayoutParams().width - blockWidth, 
				0);
		progress.removeView(cursorView);
		progress.addView(cursorView, params);
		progressShell.invalidate();
	}

	private void setCurrentCursorColor() {
		if (isRecording && !isShootPausing) {
			currentCursorColor = CURSOR_COLOR_B;
		} else {
			currentCursorColor = (currentCursorColor == CURSOR_COLOR_A) ? CURSOR_COLOR_B : CURSOR_COLOR_A;
		}

		cursorView.setBackgroundColor(currentCursorColor);
		topButtons.invalidate();
		handler.sendEmptyMessageDelayed(MESSAGE_CHANGE_CURSOR_COLOR, CURSOR_COLOR_CHANGE_GAP_MILLIS);
	}

	private void hideProgress() {
		LayoutParams params = progress.getLayoutParams();
		params.width = 0;
		progress.setLayoutParams(params);
		progress.removeAllViews();
		topButtons.invalidate();
	}

	private void stopTimer() {
		handler.removeMessages(MESSAGE_UPDATE_SHOOT_STATE);
	}

	private void showPressToShootingTip() {
		if (tipsLayout != null) {
			tipsLayout.setVisibility(View.VISIBLE);
			tipsLayout.setBackgroundResource(R.drawable.tips_background);
			tip.setVisibility(View.VISIBLE);

			String string = this.getResources().getString(R.string.press_to_shooting);
			// int color = this.getResources().getColor(R.color.white);

			SpannableStringBuilder style = new SpannableStringBuilder(string);
			// style.setSpan(new ForegroundColorSpan(color), 0, 2,
			// Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
			style.setSpan(new RelativeSizeSpan(1.15f), 0, 2, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
			style.setSpan(new RelativeSizeSpan(1.15f), 6, 10, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

			tip.setText(style);
			tipsLayout.invalidate();
			topButtons.invalidate();
			startShowAnimation();
		}
	}

	private void showPressToContinueTip() {
		if (tipsLayout != null) {
			tipsLayout.setVisibility(View.VISIBLE);
			tipsLayout.setBackgroundResource(R.drawable.tips_background);
			tip.setVisibility(View.VISIBLE);

			String string = this.getResources().getString(R.string.press_to_continue_shooting);
			// int color = this.getResources().getColor(R.color.white);

			SpannableStringBuilder style = new SpannableStringBuilder(string);
			// style.setSpan(new ForegroundColorSpan(color), 0, 2,
			// Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
			style.setSpan(new RelativeSizeSpan(1.15f), 0, 2, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

			tip.setText(style);
			tipsLayout.invalidate();
			topButtons.invalidate();
			startShowAnimation();
		}
	}

	private void showPressToDoneTip() {
		if (tipsLayout != null) {
			tipsLayout.setVisibility(View.VISIBLE);
			tipsLayout.setBackgroundResource(R.drawable.tips_background);
			tip.setVisibility(View.VISIBLE);

			String string = this.getResources().getString(R.string.press_to_done_shooting);
			int color = this.getResources().getColor(R.color.blue);

			SpannableStringBuilder style = new SpannableStringBuilder(string);
			style.setSpan(new ForegroundColorSpan(color), 2, 4, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
			style.setSpan(new RelativeSizeSpan(1.15f), 2, 4, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
			style.setSpan(new ForegroundColorSpan(color), 13, 15, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
			style.setSpan(new RelativeSizeSpan(1.15f), 13, 15, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

			tip.setText(style);
			tipsLayout.invalidate();
			topButtons.invalidate();
			startShowAnimation();
		}
	}

	private void hideTip(boolean isGone) {
		if (tipsLayout != null) {
			if (isGone || isLongShootMode()) {
				tipsLayout.setVisibility(View.GONE);
				tipsLayout.setBackgroundResource(0);
				tip.setText(null);
				tip.setVisibility(View.GONE);
				tipsLayout.invalidate();
				topButtons.invalidate();
			} else {
				startHideAnimation();
			}
		}
	}

	private void startShowAnimation() {
		Animation animation = new AlphaAnimation(0.0f, 1.0f);
		animation.setDuration(ALPHA_ANIMATIONG_MILLIS);
		animation.setFillAfter(true);
		animation.setAnimationListener(new AnimationListener() {

			@Override
			public void onAnimationStart(Animation animation) {
			}

			@Override
			public void onAnimationRepeat(Animation animation) {
			}

			@Override
			public void onAnimationEnd(Animation animation) {
			}
		});
		tipsLayout.clearAnimation();
		tipsLayout.startAnimation(animation);
	}

	private void startHideAnimation() {
		Animation animation = new AlphaAnimation(1.0f, 0.0f);
		animation.setDuration(ALPHA_ANIMATIONG_MILLIS);
		animation.setFillAfter(true);
		animation.setAnimationListener(new AnimationListener() {

			@Override
			public void onAnimationStart(Animation animation) {
			}

			@Override
			public void onAnimationRepeat(Animation animation) {
			}

			@Override
			public void onAnimationEnd(Animation animation) {
			}
		});
		tipsLayout.clearAnimation();
		tipsLayout.startAnimation(animation);
	}

	private void setShootState() {
		int totalSeconds = (int) (videoShootTotalMillis / 1000L);
		int seconds = totalSeconds % 60;
		int minutes = totalSeconds / 60;
		int hours = minutes / 60;

		String time = "";

		if (hours < 10) {
			time += "0";
		}
		time += hours;
		time += ":";

		if (minutes < 10) {
			time += "0";
		}
		time += minutes;
		time += ":";

		if (seconds < 10) {
			time += "0";
		}
		time += seconds;

		shootTime.setText(time);
	}

	private boolean isSupportZoom() {
		return mediaRecorder != null && mediaRecorder.getCameraMaxZoom() != 0;
	}

	private boolean isCaptureMode() {
		return nextWorkMode == WORK_MODE_CAPTURE;
	}

	private boolean isShortShootMode() {
		return nextWorkMode == WORK_MODE_SHOOT && isLongVideo == false;
	}

	private boolean isLongShootMode() {
		return nextWorkMode == WORK_MODE_SHOOT && isLongVideo == true;
	}

	private void setToShortShootMode() {
		nextWorkMode = WORK_MODE_SHOOT;
		isLongVideo = false;
	}

	private void setToLongShootMode() {
		nextWorkMode = WORK_MODE_SHOOT;
		isLongVideo = true;
	}
	
	private boolean isBackButtonPressed;

	@Override
	public void onClick(final View view) {

		if (isSurfaceCreated) {
			switch (view.getId()) {

			case R.id.shooting_done:
				delayStopShooting(true);
				break;

			case R.id.shooting_again:
				stopShooting(false, true, false);
				isHasLastMedia = true;
				isNeedShowLastCover = true;
				// 2014-04-23;
				CmmobiClickAgentWrapper.onEvent(this, CDR_KEY_AGAIN);
				break;

			case R.id.middle_top_button: // 闪光灯开关;
				isAllowFlashlight = !isAllowFlashlight;
				if (isAllowFlashlight) {
					middleTopButton.setImageResource(R.drawable.btn_activity_video_shoot_flashlight_on_button);
				} else {
					middleTopButton.setImageResource(R.drawable.btn_activity_video_shoot_flashlight_off_button);
				}
//				if (isLongShootMode() || isShortShootMode()) {
					controlFlashlight();
//				}
				// 2014-04-23;
				CmmobiClickAgentWrapper.onEvent(this, CDR_KEY_FLASHLIGHT);
				break;

			case R.id.right_top_button: // 前后摄像头切换;
				rightTopButton.setEnabled(false);
				handler.sendEmptyMessage(MESSAGE_CHANGE_CAMERA);
				mediaRecorder.changeGliderLine(false);
				mediaRecorder.changeBalance(false);
				break;

			case R.id.record:
				if (LowStorageChecker.check(this, this)) {
					if (isCaptureMode()) {
						takeCapture();
						
					} else if (isShootPausing) {
						continueShooting();
						
					} else if (isRecording) {
						pauseShooting(true);
						
					} else {
						startShooting();
					}
				}
				break;

			case R.id.latest_media:
				if (isHasLastMedia) {
					if (isRecording) {
						showShortToastAtCenter(R.string.stop_shooting_please);

					} else {
						handler.removeMessages(MESSAGE_LEAVE_SHOOT_PAGE_WHEN_DIARY_OK);
						handler.sendEmptyMessage(MESSAGE_LEAVE_SHOOT_PAGE_WHEN_DIARY_OK);
						// 2014-04-23;
						CmmobiClickAgentWrapper.onEvent(this, CDR_KEY_RIGHT_BROWSE);
					}
				}
				break;

			case R.id.back:
				if (isRecording) {
					isBackButtonPressed = true;
					isThrowAwayThisVideo = true;
					if (isAllowStopShooting()) {
						isThrowAwayThisVideo = false;
					}
					
					if (isThrowAwayThisVideo) {
						stopShooting(!isThrowAwayThisVideo, false, false);
						finish();
						this.overridePendingTransition(R.anim.animation_slide_in_from_left, R.anim.animation_slide_out_to_right);
						release();
						
					} else {
						delayStopShooting(false);
					}
					
				} else {
					finish();
					this.overridePendingTransition(R.anim.animation_slide_in_from_left, R.anim.animation_slide_out_to_right);
					release();
				}
				break;
			}
		}
	}

	private void zoomIn() {
		int step = mediaRecorder.getCameraMaxZoom() / CAMERA_ZOOM_STEPS;
		int zoomValue = mediaRecorder.getCameraZoom() + step;
		if (zoomValue > mediaRecorder.getCameraMaxZoom()) {
			zoomValue = mediaRecorder.getCameraMaxZoom();
		}
		mediaRecorder.setCameraZoom(zoomValue);

		int zoomProgress = (int) ((float) mediaRecorder.getCameraZoom() / (float) mediaRecorder.getCameraMaxZoom() * 100);
		seekBar.setProgress(zoomProgress);
	}

	private void zoomOut() {
		int step2 = mediaRecorder.getCameraMaxZoom() / CAMERA_ZOOM_STEPS;
		int zoomValue2 = mediaRecorder.getCameraZoom() - step2;
		if (zoomValue2 < 0) {
			zoomValue2 = 0;
		}
		mediaRecorder.setCameraZoom(zoomValue2);

		int zoomProgress2 = (int) ((float) mediaRecorder.getCameraZoom() / (float) mediaRecorder.getCameraMaxZoom() * 100);
		seekBar.setProgress(zoomProgress2);
	}

	private void takeCapture() {
		if (!isSnapshoting && !isAutoSnapshoting && needAutoTakeTimes == 0) {
			isSnapshoting = true;
			isAnimationStop = false;
			delayReenableByAutoDisable(recordButton, BUTTON_BLOCKING_MILLIS);
			reenableViewAfterThumbnailAnimation(modeSwitchLongVideoButton);
			reenableViewAfterThumbnailAnimation(rightTopButton);

			Message message = new Message();
			message.what = MESSAGE_TAKE_PICTURE;

			if (getCameraMode() == CamaraMode.DEF_PHOTO_COMMON) {
				handler.sendEmptyMessageDelayed(MESSAGE_TURN_ON_FLASHLIGHT_TORCH, 0);
				handler.sendMessageDelayed(message, TURN_ON_FLASHLIGHT_DELAY_MILLIS);

			} else if (getCameraMode() == CamaraMode.DEF_PHOTO_HEIGHT) {
				handler.sendEmptyMessageDelayed(MESSAGE_TURN_ON_FLASHLIGHT_ON, 0);
				handler.sendMessageDelayed(message, TURN_ON_FLASHLIGHT_DELAY_MILLIS);
			}
		}
	}

	private void checkFlashlight() {
		if (!mediaRecorder.isSupportFlashMode(FlashMode.FLASH_MODE_TORCH) && !mediaRecorder.isSupportFlashMode(FlashMode.FLASH_MODE_ON)) {
			middleTopButton.setVisibility(View.INVISIBLE);
		} else {
			middleTopButton.setVisibility(View.VISIBLE);
		}
	}

	private void setViewsStateOnPauseing(boolean isStopShoot, boolean needChangeTips) {
		if (isShortShootMode()) {
			middleTopButton.setEnabled(true);
			rightTopButton.setEnabled(true);
			recordButton.setPressed(false);

			if (needChangeTips) {
				if (videoShootTotalMillis < MIN_RECORD_MILLIS) {
					showPressToContinueTip();
				} else {
					showPressToDoneTip();
				}
				addPausePointToProgress();
			}

			if (isStopShoot) {
				reenableViewAfterThumbnailAnimation(modeSwitchShortVideoButton);
				shootDone.setEnabled(false);
				shootAgain.setEnabled(false);
			}

		} else if (isLongShootMode()) {
			middleTopButton.setEnabled(true);
			rightTopButton.setEnabled(true);
			recordButton.setPressed(false);
			recordButton.setBackgroundResource(R.drawable.btn_record_start_button_selector);

			if (isStopShoot) {
				reenableViewAfterThumbnailAnimation(modeSwitchShortVideoButton);
				shootDone.setEnabled(false);
				shootAgain.setEnabled(false);
			} else {
				modeSwitchShortVideoButton.setEnabled(false);
			}
		}
	}

	private void setViewsStateOnShooting() {
		if (isShortShootMode()) {
			middleTopButton.setEnabled(true/*false*/);
			rightTopButton.setEnabled(true/*false*/);
			recordButton.setPressed(true);

		} else if (isLongShootMode()) {
			middleTopButton.setEnabled(true/*false*/);
			rightTopButton.setEnabled(true/*false*/);
			modeSwitchShortVideoButton.setVisibility(View.INVISIBLE);
			modeSwitchShortVideoButton.setEnabled(false);
			recordButton.setPressed(false);
			recordButton.setBackgroundResource(R.drawable.btn_record_continue_button_selector);
		}
	}

	/**
	 * delay Re-Enable;
	 * 
	 * @param view
	 * @param delay
	 */
	private void delayReenableByAutoDisable(View view, long delay) {
		if (view != null) {
			view.setEnabled(false);
			delayReenable(view, delay);
		}
	}

	private void delayReenable(final View view, final long delay) {
		if (view != null) {

			new Thread(new Runnable() {

				@Override
				public void run() {
					handler.postDelayed(new Runnable() {
						@Override
						public void run() {
							view.setEnabled(true);
						}
					}, delay);
				}
			}).start();
		}
	}

	private void reenableViewAfterThumbnailAnimation(View view) {
		if (view != null) {
			view.setEnabled(false);
			reenableViewsBuffer.add(view);
		}
	}

	private void cancelReenableView() {
		for (View view : reenableViewsBuffer) {
			view.setVisibility(View.VISIBLE);
			view.setEnabled(true);
		}
		reenableViewsBuffer.clear();
	}

	private void cancelAutoSnapshot() {
		if (needAutoTakeTimes > 0) {
			needAutoTakeTimes = 0;
			isSnapshoting = false;
			isAutoSnapshoting = false;
			handler.removeMessages(MESSAGE_TAKE_PICTURE);
		}
	}

	private void gotoEditShareActivity() {
		dismissProgressDialog();

		MyDiary diary = DiaryManager.getInstance().findMyDiaryByUUID(lastDiaryUUID);
		String string = new Gson().toJson(diary);

		Intent intent = new Intent(this, DiaryEditPreviewActivity.class);
		if (isShortShootMode()) {
			intent.putExtra(DiaryEditPreviewActivity.INTENT_ACTION_IS_FROM_SHORT_SHOOT_ACTVITY, true);
		} else {
			intent.putExtra(DiaryEditPreviewActivity.INTENT_ACTION_IS_FROM_LONG_SHOOT_ACTVITY, true);
		}

		intent.putExtra(DiaryEditPreviewActivity.INTENT_ACTION_DIARY_STRING, string);
		startActivity(intent);
		finish();
		this.overridePendingTransition(R.anim.animation_slide_in_from_left, R.anim.animation_slide_out_to_right);
	}

	private void gotoPreviewActivity() {
		dismissProgressDialog();

		ArrayList<MyDiaryList> list = new ArrayList<MyDiaryList>();
		for (DiaryWrapper wrapper : wrappers) {
			list.add(0, DiaryManager.getInstance().findDiaryGroupByUUID(wrapper.diary.diaryuuid));
		}
		DiaryManager.getInstance().setDetailDiaryList(list, 0);

		Intent intent = new Intent(this, DiaryPreviewActivity.class);
		intent.putExtra(DiaryPreviewActivity.INTENT_ACTION_DIARY_UUID, lastDiaryUUID);
		startActivity(intent);
		finish();
		this.overridePendingTransition(R.anim.animation_slide_in_from_left, R.anim.animation_slide_out_to_right);
	}

	private synchronized void enterLastDiary() {
		boolean isAllOK = true;

		for (DiaryWrapper wrapper : wrappers) {
			if (!wrapper.isDiaryOK) {
				isAllOK = false;
				break;
			}
		}

		if (isAllOK && isAnimationStop) {
			if (!TextUtils.isEmpty(lastDiaryUUID)) {

				MyDiary diary = DiaryManager.getInstance().findResentOneDiary();
				ArrayList<MyDiaryList> list = new ArrayList<MyDiaryList>();
				
				if(diary != null) {
					if (wrappers.size() == 0) {
						list.add(0, DiaryManager.getInstance().findDiaryGroupByUUID(diary.diaryuuid));
						
					} else {
						for (DiaryWrapper wrapper : wrappers) {
							list.add(0, DiaryManager.getInstance().findDiaryGroupByUUID(wrapper.diary.diaryuuid));
						}
					}
					
					DiaryManager.getInstance().setDetailDiaryList(list, 0);
	
					Intent intent = new Intent(this, DiaryPreviewActivity.class);
					intent.putExtra(DiaryPreviewActivity.INTENT_ACTION_DIARY_UUID, diary.diaryuuid);
					startActivity(intent);
				} else {
					Log.e("cmmobi", "Should Not Be Here! Recent diary is null!");
				}
				handler.removeMessages(MESSAGE_LEAVE_SHOOT_PAGE_WHEN_DIARY_OK);
			}
			dismissProgressDialog();

		} else {
			if (ZDialog.getDialog() == null || !ZDialog.getDialog().isShowing()) {
				ZDialog.show(R.layout.progressdialog, true, true, VideoShootActivity2.this, true);
			}
			handler.removeMessages(MESSAGE_LEAVE_SHOOT_PAGE_WHEN_DIARY_OK);
			handler.sendEmptyMessageDelayed(MESSAGE_LEAVE_SHOOT_PAGE_WHEN_DIARY_OK, LOOP_MESSAGE_GAP_MILLIS);
		}
	}

	private void switchRecordState() {
		if (!isRecording) {
			startShooting();

		} else if (isAllowStopShooting()) {
			stopShooting(true, false, true);

		} else {
			isThrowAwayThisVideo = true;
			stopShooting(false, false, false);

			super.onBackPressed();
			this.overridePendingTransition(R.anim.animation_slide_in_from_left, R.anim.animation_slide_out_to_right);
		}
	}

	private void startShooting() {
		ZLog.printStackTrace();
		if (!isRecording && isAnimationStop) {
			isRecording = true;
			isShootPausing = false;

			handler.post(new Runnable() {

				@Override
				public void run() {

					setViewsByWorkMode();
					setViewsStateOnShooting();

					// 下面代码的先后顺序不要变;
					handler.sendEmptyMessage(MESSAGE_TURN_ON_FLASHLIGHT_TORCH);

					final String attachUUID = DiaryController.getNextUUID();
					final String path = DiaryController.getInstanse().getFolderByType(GsonProtocol.ATTACH_TYPE_VIDEO);

					delayRequestVideoDiary(attachUUID);

					mediaRecorder.start(attachUUID, path);
					startTimer();

					hideTip(false);
				}
			});
		}
	}

	private void stopShooting(boolean showProgressDialog, final boolean resetStates, final boolean toEditPage) {
		if (isRecording) {
			isRecording = false;

			if (showProgressDialog) {
				showProgressDialog();
			}

			isNeedToEditPage = toEditPage;
			
			handler.postDelayed(new Runnable() {

				@Override
				public void run() {
					isShootPausing = false;

					if (resetStates) {
						if (mediaRecorder.isSupportPauseAndResume()) {
							if (mediaRecorder.isPause()) {
								mediaRecorder.resume();
							}
						}

						setViewsStateOnPauseing(true, false);
					}

					new Thread(new Runnable() {

						@Override
						public void run() {
							stopTimer();
							mediaRecorder.stop();
						}
					}).start();

					handler.sendEmptyMessage(MESSAGE_TURN_OFF_FLASHLIGHT);
					shortShootLeftSlideRect.setEmpty();

					if (resetStates) {
						if (!isFrontCamera) {
							controlFlashlight();
						}
						checkFlashlight();
						hideProgress();

						setToShortShootMode();
						setViewsByWorkMode();

						delayReenableByAutoDisable(recordButton, BUTTON_BLOCKING_MILLIS);
						
						restoreZoomState();
						// setLastDiaryImage();
					}
				}
			}, 300);

		}
	}
	
	private void restoreZoomState() {
		float zoomProgress = (float) currentCameraZoom / (float) mediaRecorder.getCameraMaxZoom() * 100;
		seekBar.setProgress((int) zoomProgress);
		try {
			mediaRecorder.setCameraZoom((int) zoomProgress);
		} catch(Exception e) {
			e.printStackTrace();
			ZToast.showLong("照相机焦距错误");
		}
	}

	private void pauseShooting(boolean needChangeTips) {
		if (!isShootPausing) {
			isShootPausing = true;

			new Thread(new Runnable() {

				@Override
				public void run() {
					mediaRecorder.pause();
				}
			}).start();

			stopTimer();
			setViewsStateOnPauseing(false, needChangeTips);
		}
	}

	private void continueShooting() {
		if (isShootPausing) {
			isShootPausing = false;
			new Thread(new Runnable() {

				@Override
				public void run() {
					mediaRecorder.resume();
				}
			}).start();
			currentCursorColor = CURSOR_COLOR_B;
			addCurrentCursor();
			continueTimer();
			if (isShortShootMode()) {
				hideTip(false);
			}
			setViewsStateOnShooting();
			controlFlashlight();
			
			// 2014-04-23;
			CmmobiClickAgentWrapper.onEvent(this, CDR_KEY_VIDEO_BUTTON);
		}
	}

	private void controlFlashlight() {
		if (isAllowFlashlight) {
			handler.sendEmptyMessage(MESSAGE_TURN_ON_FLASHLIGHT_TORCH);
		} else {
			handler.sendEmptyMessage(MESSAGE_TURN_OFF_FLASHLIGHT);
		}
	}

	private void delayRequestVideoDiary(final String attachUUID) {
		videoDiaryUUID = DiaryController.getNextUUID();

		new Thread(new Runnable() {

			@Override
			public void run() {
				DiaryWrapper wrapper = DiaryController.getInstanse().requestNewDiary(handler, videoDiaryUUID, attachUUID, GsonProtocol.ATTACH_TYPE_VIDEO,
						GsonProtocol.SUFFIX_MP4, null, GsonProtocol.EMPTY_VALUE, GsonProtocol.EMPTY_VALUE, CommonInfo.getInstance().getLongitude(),
						CommonInfo.getInstance().getLatitude(), DiaryController.getPositionString1(), GsonProtocol.EMPTY_VALUE,
						String.valueOf(System.currentTimeMillis()));

				addToDiaryWrappers(wrapper);
			}
		}).start();
	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
	}

	/**
	 * 加速度传感器回调函数;
	 */
	@Override
	public void onSensorChanged(SensorEvent event) { // ▉暂时保留, 不要删除;
	// float x = event.values[SensorManager.DATA_X];
	// float y = event.values[SensorManager.DATA_Y];
	//
	// if (isOrientationSwitching) {
	// if (TimeHelper.getInstance().now() - orientationHoldingMillis >
	// ORIENTATION_SWITCH_TIME) {
	// isOrientationSwitching = false;
	// orientationHoldingMillis = TimeHelper.getInstance().now();
	//
	// if (Math.abs(x) > Math.abs(y)) {
	// if (x > 0) {
	// switchOrientation(ORIENTATION_RIGHT);
	// } else {
	// switchOrientation(ORIENTATION_LEFT);
	// }
	// } else {
	// if (y > 0) {
	// switchOrientation(ORIENTATION_UP);
	// } else {
	// switchOrientation(ORIENTATION_DOWN);
	// }
	// }
	// }
	//
	// } else if (Math.abs(Math.abs(x) - Math.abs(y)) >
	// ORIENTATION_SWITCH_THRESHOLD
	// && TimeHelper.getInstance().now() - orientationHoldingMillis >
	// ORIENTATION_SWITCH_TIME) {
	// isOrientationSwitching = true;
	// orientationHoldingMillis = TimeHelper.getInstance().now();
	// }
	}

	private void updateAlbum(Bitmap bitmap, int thumbnailAnimationType) {
		if (bitmap != null) {
			isAnimationStop = false;

			int[] colors = new int[bitmap.getWidth() * bitmap.getHeight()];

			bitmap.getPixels(colors, 0, bitmap.getWidth(), 0, 0, bitmap.getWidth(), bitmap.getHeight());

			maskBitmap = Bitmap.createBitmap(colors, bitmap.getWidth(), bitmap.getHeight(), android.graphics.Bitmap.Config.ARGB_8888);
			small = ThumbnailUtils.extractThumbnail(bitmap, 96, 96);

			this.thumbnailAnimationType = thumbnailAnimationType;

			Message message = new Message();
			message.what = MESSAGE_PHOTO_ALBUM_UPDATING_ANIMATION;
			handler.sendMessage(message);

			// if (tempCallbackBitmap != null &&
			// !tempCallbackBitmap.isRecycled()) {
			// tempCallbackBitmap.recycle();
			// }

		} else {
			showShortToastAtCenter(R.string.get_cover_error);
		}
	}

	private void updateLastDiaryUUID(String uuid) {
		lastDiaryUUID = uuid;
	}

	private CamaraMode getCameraMode() {
		return cameraModeMask;
	}

	private void setCameraMode(CamaraMode mode, ViewGroup viewGroup) {
		cameraModeMask = mode;
		mediaRecorder.setCameraMode(mode, viewGroup);
	}

	@Override
	public void onSurfaceCreated() {
		if (mediaRecorder != null) {
			if (!mediaRecorder.isPreview()) {
				mediaRecorder.startPreview(isFrontCamera ? CameraInfo.CAMERA_FACING_FRONT : CameraInfo.CAMERA_FACING_BACK);
				isSurfaceCreated = true;
			}

			checkFlashlight();
		}
	}

	@Override
	public void onStartRecorder(Object arg0, String path) {
		// 2014-04-23;
		CmmobiClickAgentWrapper.onEvent(this, CDR_KEY_VIDEO_BUTTON);
	}

	@Override
	public void onStopRecorder(Object arg0, String path) {
		
		if (isThrowAwayThisVideo) {
			// 放弃并删除本段视频;
			ZFileSystem.delFile(path);

		} else {
			updateLastDiaryUUID(videoDiaryUUID);
			DiaryWrapper wrapper = DiaryController.getInstanse().diaryContentIsReady(videoDiaryUUID);

			if (isNeedToEditPage) {
				handler.sendEmptyMessage(MESSAGE_GOTO_EDIT_SHARE_PAGE);
				
			} else if (!isBackButtonPressed) {
				showTopToast(R.string.last_video_saved);
				if(wrapper != null && wrapper.cover != null) {
					updateAlbum(wrapper.cover.bm, THUMBNAIL_ANIMATION_VIDEO);
				}
			}
		}
		// 2014-04-23;
		CmmobiClickAgentWrapper.onEventDuration(this, CDR_KEY_VIDEO_TIME, videoShootTotalMillis);
		if (isLongShootMode()) {
			// 2014-04-23;
			CmmobiClickAgentWrapper.onEventDuration(this, CDR_KEY_LONG_VIDEO, videoShootTotalMillis);
		} else if (isShortShootMode()) {
			// 2014-04-23;
			CmmobiClickAgentWrapper.onEventDuration(this, CDR_KEY_SHORT_VIDEO, videoShootTotalMillis);
		}
		
		Log.d("cmmobi", "video time = " + videoShootTotalMillis);
		videoShootTotalMillis = 0;
	}

	@Override
	public void onSmallBoxComplete(Object arg0, String path) {
	}

	@Override
	public void onPauseRecorder(Object r) {
	}

	@Override
	public void onResumeRecorder(Object r) {
	}

	@Override
	public synchronized void onPictureTakenComplete(String file) {
		updateLastDiaryUUID(pictureDiaryUUID);
		DiaryController.getInstanse().diaryContentIsReady(pictureDiaryUUID);

		if (needAutoTakeTimes == 0 && isAutoSnapshoting) {
			isAutoSnapshoting = false;
			reenableViewAfterThumbnailAnimation(modeSwitchLongVideoButton);
			reenableViewAfterThumbnailAnimation(rightTopButton);
		}
	}

	@Override
	public synchronized void onPictureTaken(Bitmap bitmap, String filePath) {

		if (bitmap != null) {
			tempCallbackBitmap = bitmap;

		} else if (filePath != null) {
			Options options = new Options();
			options.inSampleSize = 2;
			tempCallbackBitmap = BitmapFactory.decodeFile(filePath, options);
			tempCallbackBitmap = ZGraphics.rotate(tempCallbackBitmap, XUtils.getExifOrientation(filePath), true);
		}

		if (!isRecording) {
			handler.sendEmptyMessage(MESSAGE_TURN_OFF_FLASHLIGHT);
		}
		updateAlbum(tempCallbackBitmap, THUMBNAIL_ANIMATION_PICTURE);
	}

	@Override
	public void onYes() {

	}

	@Override
	public void onNo() {
		this.finish();
	}

	@Override
	public void onCameraOpenFailed(String msg) {
		showShortToastAtCenter(R.string.camera_error);
	}

	@Override
	public void onSizeChanged(int left, int top, int right, int bottom) {
	}

	@Override
	public boolean onDown(MotionEvent e) {
		return false;
	}

	@Override
	public void onShowPress(MotionEvent e) {
	}

	@Override
	public boolean onSingleTapUp(MotionEvent e) {
		return false;
	}

	@Override
	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
		return false;
	}

	@Override
	public void onLongPress(MotionEvent e) {
	}

	@Override
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
		if (e1 != null && e2 != null) {
			float offsetX = e2.getRawX() - e1.getRawX();
			float offsetY = e2.getRawY() - e1.getRawY();
			return checkTouchOffset(offsetX, offsetY, velocityX);
		}
		return false;
	}

	private boolean checkTouchOffset(float offsetX, float offsetY, float direction) {
		boolean b = false;
		
		if (!isRecording && !isAutoSnapshoting && !isSnapshoting && Math.abs(offsetX / offsetY) > 1.2
				&& Math.abs(offsetX) > ZScreen.cmToPixels(DRAG_EVENT_MIN_MOVE_DISTANCE)) {
			b = true;

			if (direction > 0) {
				modeSwitchShortVideoButton.setChecked(true);
				// 2014-04-23;
				CmmobiClickAgentWrapper.onEvent(this, CDR_KEY_SWITCH_MODE, CDR_LABEL_LONG_TO_SHORT_VIDEO);

			} else if (direction < 0) {
				modeSwitchLongVideoButton.setChecked(true);
				// 2014-04-23;
				CmmobiClickAgentWrapper.onEvent(this, CDR_KEY_SWITCH_MODE, CDR_LABEL_SHORT_TO_LONG_VIDEO);
			}
		}

		return b;
	}

	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		switch (buttonView.getId()) {
		case R.id.mode_switch_short_video_button:
			if (isChecked && buttonView.getTag() == null) {
				scrollToShortVideoMode(MODE_SWITCH_ANIMATION_SPEED);
			}
			break;

		case R.id.mode_switch_long_video_button:
			if (isChecked && buttonView.getTag() == null) {
				scrollToLongVideoMode(MODE_SWITCH_ANIMATION_SPEED);
			}
			break;
		}

		buttonView.setTag(null);
	}

	private void scrollToShortVideoMode(int duration) {
		readModeSwitchViewsRect();

		int current = modeSwitchLayout.getScrollX();
		int distance = getCaptureModeDistance(current);

		scroller.startScroll(current, 0, distance, 0, duration);
		setToShortShootMode();
		handler.sendEmptyMessage(MESSAGE_SWITCH_MODE_ANIMATION);

		modeSwitchShortVideoButton.setTextSize(16);
		modeSwitchLongVideoButton.setTextSize(14);
	}

	private void scrollToLongVideoMode(int duration) {
		if (handler.hasMessages(MESSAGE_TAKE_PICTURE) && System.currentTimeMillis() - delayCaptureStartMillis < 500) {
			delayReenableByAutoDisable(modeSwitchLongVideoButton, BUTTON_BLOCKING_MILLIS);
		} else {
			readModeSwitchViewsRect();

			int current = modeSwitchLayout.getScrollX();
			int distance = getShootModeDistance(current);

			scroller.startScroll(current, 0, distance, 0, duration);
			setToLongShootMode();
			handler.sendEmptyMessage(MESSAGE_SWITCH_MODE_ANIMATION);
		}

		modeSwitchShortVideoButton.setTextSize(14);
		modeSwitchLongVideoButton.setTextSize(16);
	}

	private int getCaptureModeDistance(int current) {
		int distance = -(modeSwitchLayoutRect.width() - captureButtonRect.width()) / 2 - current;
		return distance;
	}

	private int getShootModeDistance(int current) {
		int distance = -((modeSwitchLayoutRect.width() - shootButtonRect.width()) / 2 - captureButtonRect.width()) - current;
		return distance;
	}

	private void readModeSwitchViewsRect() {
		modeSwitchLayout.getGlobalVisibleRect(modeSwitchLayoutRect);
		modeSwitchShortVideoButton.getGlobalVisibleRect(captureButtonRect);
		modeSwitchLongVideoButton.getGlobalVisibleRect(shootButtonRect);
	}

	private int lastProgress;
	private ZBooleanBox lastDirection = new ZBooleanBox();
	private ZBooleanBox direction = new ZBooleanBox();
	
	@Override
	public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
		if (fromUser) {
			int zoomValue = mediaRecorder.getCameraMaxZoom() * progress / 100;
			mediaRecorder.setCameraZoom(zoomValue);
		}
		
		if (progress - lastProgress > 0) {
			direction.setStateToTrue();
			
		} else if (progress - lastProgress < 0) {
			direction.setStateToFalse();
		}
		
		if ((direction.isTrue() && !lastDirection.isTrue())
			|| (direction.isFalse() && !lastDirection.isFalse())) {
			// 2014-04-23;
			CmmobiClickAgentWrapper.onEvent(this, CDR_KEY_FOCAL_DISTANCE);
		}
		
		lastProgress = progress;
		if (direction.isTrue()) {
			lastDirection.setStateToTrue();
		} else {
			lastDirection.setStateToFalse();
		}
	}

	@Override
	public void onStartTrackingTouch(SeekBar seekBar) {
	}

	@Override
	public void onStopTrackingTouch(SeekBar seekBar) {
	}

	private void showTopToast(int id) {
		String string = getString(id);
		final float maxAlpha = 0.85f;
		final TextView textView = viewFinder.findTextView(R.id.top_toast);

		textView.setText(string);

		AlphaAnimation showAnimation = new AlphaAnimation(0, maxAlpha);
		showAnimation.setDuration(ALPHA_ANIMATIONG_MILLIS * 2);
		showAnimation.setFillAfter(true);
		showAnimation.setAnimationListener(new AnimationListener() {

			@Override
			public void onAnimationStart(Animation animation) {
			}

			@Override
			public void onAnimationRepeat(Animation animation) {
			}

			@Override
			public void onAnimationEnd(Animation animation) {
				handler.postDelayed(new Runnable() {

					@Override
					public void run() {
						AlphaAnimation hideAnimation = new AlphaAnimation(maxAlpha, 0);
						hideAnimation.setDuration(ALPHA_ANIMATIONG_MILLIS * 2);
						textView.clearAnimation();
						textView.startAnimation(hideAnimation);
					}
				}, TOP_TOAST_HOLDING_MILLIS);
			}
		});

		textView.startAnimation(showAnimation);
	}

	private void showShortToastAtCenter(int id) {
		String string = getString(id);
		ZToast.show(string, Toast.LENGTH_SHORT, Gravity.CENTER, 0, 0);
	}

	private void showProgressDialog() {
		dismissProgressDialog();
		ZDialog.show(R.layout.progressdialog, false, true, VideoShootActivity2.this, true);
	}

	private void dismissProgressDialog() {
		if (ZDialog.getDialog() != null) {
			if (ZDialog.getDialog().isShowing()) {
				ZDialog.dismiss();
			}
		}
	}

	private class HomeKeyPressedReceiver extends ZBroadcastReceiver {

		public HomeKeyPressedReceiver() {
			super(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
		}

		@Override
		public void onReceive(Context context, Intent intent) {
			if (intent.getAction().equals(Intent.ACTION_CLOSE_SYSTEM_DIALOGS)) {
				String reason = intent.getStringExtra("reason");
				if (reason != null && reason.equals("homekey")) {
				}
			}
		}
	}

}
