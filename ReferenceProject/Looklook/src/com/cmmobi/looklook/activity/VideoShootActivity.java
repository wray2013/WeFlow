package com.cmmobi.looklook.activity;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.Rect;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.hardware.Camera.CameraInfo;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.ThumbnailUtils;
import android.os.Bundle;
import android.os.Environment;
import android.os.Message;
import android.os.StrictMode;
import android.provider.MediaStore.Video.Thumbnails;
import android.text.TextUtils;
import android.view.Display;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.view.animation.RotateAnimation;
import android.view.animation.ScaleAnimation;
import android.widget.AbsoluteLayout;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ToggleButton;
import cn.zipper.framwork.core.ZActivity;
import cn.zipper.framwork.core.ZDialog;
import cn.zipper.framwork.core.ZLayoutInflater;
import cn.zipper.framwork.core.ZLog;
import cn.zipper.framwork.core.ZToast;
import cn.zipper.framwork.core.ZViewFinder;
import cn.zipper.framwork.device.ZScreen;
import cn.zipper.framwork.io.network.ZNetworkStateDetector;
import cn.zipper.framwork.utils.ZGraphics;
import cn.zipper.framwork.utils.ZSensorManager;
import cn.zipper.framwork.utils.ZStringUtils;
import cn.zipper.framwork.utils.ZThread;
import cn.zipper.framwork.utils.ZUniformScaler;
import cn.zipper.framwork.utils.ZUniformScaler.Model;
import cn.zipper.framwork.utils.ZUniformScaler.ScaleType;

import com.cmmobi.looklook.Config;
import com.cmmobi.looklook.MainApplication;
import com.cmmobi.looklook.R;
import com.cmmobi.looklook.common.constant.Constant;
import com.cmmobi.looklook.common.gson.GsonRequest2;
import com.cmmobi.looklook.common.gson.GsonRequest2.Attachs;
import com.cmmobi.looklook.common.gson.GsonResponse2;
import com.cmmobi.looklook.common.gson.GsonResponse2.MyDiary;
import com.cmmobi.looklook.common.gson.GsonResponse2.createStructureResponse;
import com.cmmobi.looklook.common.gson.GsonResponse2.diaryAttach;
import com.cmmobi.looklook.common.gson.Requester2;
import com.cmmobi.looklook.common.utils.CmmobiClickAgentWrapper;
import com.cmmobi.looklook.common.utils.EffectsDownloadUtil;
import com.cmmobi.looklook.common.utils.LowStorageChecker;
import com.cmmobi.looklook.common.utils.LowStorageChecker.OnChoseListener;
import com.cmmobi.looklook.common.utils.MD5;
import com.cmmobi.looklook.common.utils.MediaActionSoundWrapper;
import com.cmmobi.looklook.common.utils.MediaCoverUtils;
import com.cmmobi.looklook.common.utils.UmengclickAgentWrapper;
import com.cmmobi.looklook.common.view.VerticalSeekBar2;
import com.cmmobi.looklook.common.view.VerticalSeekBar2.OnSeekBarChangeListener;
import com.cmmobi.looklook.info.profile.AccountInfo;
import com.cmmobi.looklook.info.profile.ActiveAccount;
import com.cmmobi.looklook.info.profile.CommonInfo;
import com.cmmobi.looklook.info.profile.DiaryManager;
import com.cmmobi.looklook.info.profile.MediaValue;
import com.cmmobi.looklook.info.profile.NetworkTaskInfo;
import com.cmmobi.looklook.info.profile.TimeHelper;
import com.cmmobi.looklook.info.weather.MyWeather;
import com.cmmobi.looklook.map.MapItemHelper;
import com.cmmobi.looklook.networktask.CacheNetworkTask;
import com.cmmobi.looklook.networktask.INetworkTask;
import com.cmmobi.looklook.networktask.NetworkTaskManager;
import com.cmmobi.looklook.networktask.UploadNetworkTask;
import com.cmmobivideo.utils.EffectBean;
import com.cmmobivideo.utils.EffectUtils;
import com.cmmobivideo.utils.Mp4InfoUtils;
import com.cmmobivideo.utils.PluginUtils;
import com.cmmobivideo.utils.XUtils;
import com.cmmobivideo.workers.XVideoRecorder;
import com.cmmobivideo.workers.XVideoRecorder.XVideoRecorderInfoListener;
import com.cmmobivideo.workers.XVideoRecorder.XVideoSurfaceSizeChangeListener;
import com.cmmobivideo.workers.XVideoRecorder.XVideoTakePicture;

import effect.EffectType;
import effect.EffectType.CamaraMode;
import effect.EffectType.FlashMode;
import effect.EffectType.PreviewMode;
import effect.XEffects;

@SuppressWarnings("deprecation")
// for AbsoluteLayout;
public class VideoShootActivity extends ZActivity implements SensorEventListener, OnSeekBarChangeListener, OnLongClickListener, XVideoRecorderInfoListener<Object>, XVideoSurfaceSizeChangeListener, XVideoTakePicture, OnChoseListener {

	private static final float ORIENTATION_SWITCH_THRESHOLD = 4.2f; // 设备姿态切换阀值;
	
	private static final int ORIENTATION_SWITCH_TIME = 400; // UI元素切换时间阀值;
	private static final int ORIENTATION_ROTATE_SPEED = 400; // UI元素动画持续时间; 200
	private static final int TURN_ON_FLASHLIGHT_DELAY_MILLIS = 350; // 打开闪光灯到拍照直接的间隔时间;
	private static final int SHOOT_REMAIN_MILLIS = 10 * 60 * 1000; // wifi状态下拍摄的最长时间 (10分钟);
	private static final int CONTINUOUS_CAPTURE_GAP_MILLIS = 500; // 连拍最小间隔时间;

	private static final int ORIENTATION_UP = 0;
	private static final int ORIENTATION_RIGHT = 1;
	private static final int ORIENTATION_DOWN = 2;
	private static final int ORIENTATION_LEFT = 3;

	private static final int MESSAGE_SWITCH_EFFECTS = 10; // 消息: 转换马赛克坐标,并显示;
	private static final int MESSAGE_TURN_ON_FLASHLIGHT_TORCH = 20; // 消息: 开关闪光灯 (长亮);
	private static final int MESSAGE_TURN_ON_FLASHLIGHT_ON = 21; // 消息: 开关闪光灯 (必闪);
	private static final int MESSAGE_TURN_ON_FLASHLIGHT_AUTO = 22; // 消息: 开关闪光灯 (自动闪);
	private static final int MESSAGE_TURN_OFF_FLASHLIGHT = 23; // 消息: 开关闪光灯;
	private static final int MESSAGE_TAKE_PICTURE = 30; // 消息: 拍照;
	private static final int MESSAGE_CREATE_PICTURE_DIARY = 40; // 消息: 请求创建日记;
	private static final int MESSAGE_UPDATE_SHOOT_STATE = 50; // 消息: 更新拍摄时长和wifi状态;
	private static final int MESSAGE_PHOTO_ALBUM_UPDATING_ANIMATION = 60; // 消息: 截屏图片进入相册的动画;
	private static final int MESSAGE_CHANGE_CAMERA = 70; // 消息: 切换摄像头;
	private static final int MESSAGE_SWITCH_HIGHT_COMMON = 80; // 消息: 切换高清/普清;
	private static final int MESSAGE_SWITCH_MODE = 90; // 消息: 切换拍摄/拍照模式;

	public static final String DIARYS = "DIARYS";
	
	public static final String CDR_KEY_SHP_EFF = "shp_eff";
	public static final String CDR_KEY_SHP_EFF_CHOO = "shp_eff_choo";
	public static final String CDR_KEY_SHP_TYPE = "shp_type";
	public static final String CDR_KEY_SHP_SCRS = "shp_scrs";
	public static final String CDR_KEY_SHP_SH_BE = "shp_sh_be";
	public static final String CDR_KEY_SHP_SH_SUCC = "shp_sh_succ";
	public static final String CDR_KEY_SHP_SW_CAM = "shp_sw_cam";
	public static final String CDR_KEY_SHP_SCR_SIZ = "shp_scr_siz";
	public static final String CDR_KEY_SHP_SCR_QUA = "shp_scr_qua";
	public static final String CDR_KEY_SHP_COUT = "shp_cout";
	
	public static final String CDR_LABEL_NAME_1 = "label";
	public static final String CDR_LABEL_NAME_2 = "label2";
	public static final String CDR_LABEL_WORK_MODE_TAKE_PICTURE = "1";
	public static final String CDR_LABEL_WORK_MODE_SHOOT = "2";
	public static final String CDR_LABEL_CAMERA_FRONT = "1";
	public static final String CDR_LABEL_CAMERA_BACK = "2";
	public static final String CDR_LABEL_RATIO_4_3 = "1";
	public static final String CDR_LABEL_RATIO_16_9 = "2";
	public static final String CDR_LABEL_SIZE_HD = "1"; // 高清;
	public static final String CDR_LABEL_SIZE_CD = "2"; // 普清;
	
	private boolean isSurfaceCreated;
	private boolean isOrientationSwitching;
	private boolean isTouchInCenter;
	private boolean isTouchAtLeftTop;
	private boolean isTouchAtRightTop;
	private boolean isTouchAtRightBottom;
	private boolean isTouchAtLeftBottom;
	private boolean isMosaicControlShowing;
	private boolean isMosaicControling;
	private boolean isMosaicInitialized;
	private boolean isHighDefinition; // 是否高清;
	private boolean isRecording;
	private boolean isSnapshoting;
	private boolean isAllowFlashlight;
	private boolean isFlashlightOn;
	private boolean isFrontCamera;
	private boolean isFocusLocked;
	private boolean isGridShowing;
	private boolean isGradienterShowing;
	private boolean isEnterLastDiary;
	private boolean isShootPausing;
	private boolean isWifiShooting;
	private boolean isAutoSnapshoting;
	private boolean isCommonModeFocusLocked; //普清模式下焦距是否锁定;
	private boolean isPictureSaved;
	private boolean isAnimationStop;
	private long orientationHoldingMillis;
	private long videoClipStartMillis;
	private long videoShootTotalMillis;
	private long effectsGroupShowMillis;
	private long lastWorkModeStartMillis;
	private long lastCameraModeStartMillis;
	private long lastScreenRatioModeStartMillis;
	private long lastDefinitionModeStartMillis;
	private long videoShootStartMillis;
	private int mosaicControlBlockSize; // px
	private int currentOrientation;
	private int currentDelaySeconds; // 延时拍照倒计时秒数;
	private int needAutoTakeTimes;
	private int lastEffectId;
	private int lastX;
	private int lastY;
	private float mosaicX;
	private float mosaicY;
	private float mosaicWidth;
	private float mosaicHeight;

	private ZViewFinder finder;
	private Rect seekBarRect;
	private Rect effectsGroupRect;
	private Rect effectsControlLayerRect;
	private String videoDiaryUUID;
	private String picturesDiaryUUID;
	private String lastWorkMode; // 上一个工作模式 (拍照 / 录制);
	private String lastCamera; // 上一个摄像机 (前 / 后);
	private String lastRatio; // 上一个预览比例 (4:3 / 16:9);
	private String lastDefinition; // 上一个清晰度 (高清 / 普清);
	private String uid;

	private FrameLayout mainWindow;
	private FrameLayout mainWindowMask;
	private ToggleButton effectsButton;
	private ToggleButton delaySnapshotButton;
	private ToggleButton pauseVideoShoot;
	private ToggleButton modeSwitch;
	private RadioButton currentCheckedEffectButton; // 这个字段有必要么?
	private RadioButton currentCheckedScenesButton; // 这个字段有必要么?
	private ImageView recordButton;
	private ImageView leftTopButton;
	private ImageView middleTopButton;
	private ImageView rightTopButton;
	private ImageView delayShootNumber;
	private ImageView leftButton1;
	private ImageView leftButton2;
	private ImageView leftButton3;
	private ImageView leftButton4;
	private ImageView leftButton5;
	private ImageView latestMedia;
	private ImageView zoominButton;
	private ImageView zoomoutButton;
	private ImageView mask;
	private ImageButton takeSnapshotButton;
	private View tempSnapshotButton;
	private VerticalSeekBar2 seekBar;

	private AbsoluteLayout effectsControlLayer;
	private AbsoluteLayout.LayoutParams mosaicControlParams;
	private RelativeLayout mosaicControlLayer;
	private RelativeLayout bottomButtons;
	private LinearLayout seekBarShell;
	private LinearLayout camreaControlButtons;
	private LinearLayout shootState;
	private RadioGroup effectsLayer;
	private HorizontalScrollView effectsGroup;
	
//	private Drawable pauseButtonDrawable; // for SAMSUNG S3, compatible other device too;
//	private Drawable continueButtonDrawable; // for SAMSUNG S3, compatible other device too;
	
	private List<TempDiaryWrapper> diarys;
	private ArrayList<MyDiary> diaryList;
	private HashMap<String, TempDiaryWrapper> diaryWrappers;
	
	private XVideoRecorder mediaRecorder;
	private EffectUtils effectUtils;
	private List<EffectBean> effectBeans;
	private XEffects effects;
	private PreviewMode currentPreviewMode; //用来保存当前预览模式: 16:9 / 4:3
	
	private TextView onScreenLog;
	private int messageCount;
	private int callbackCount;
	
	
	public static class TempDiaryWrapper {
		public static final int TYPE_VIDEO = 1;
		public static final int TYPE_PICTURE = 2;

		public int type; // TYPE_VIDEO / TYPE_PICTURE;
		public long createTimeMillis;
		public boolean requestDiaryOK; // 上传日记结构的请求得到成功响应;
		public boolean mediaCatchOK; // 媒体捕获完成 (拍摄完成, 拍照完成);
		public boolean diaryCreatedOK;// 日记是否创建完成
		public String attachUUID; // 媒体的uuid;
		public String mediaName; // 媒体存储名称;
		public String mediaPath; // 媒体存储路径;
		public GsonRequest2.createStructureRequest request;
		public GsonResponse2.createStructureResponse response;
		public MediaValue mediaValue;
		public Bitmap thumbnail;
		
		public TempDiaryWrapper(int type) {
			this.type = type;
		}
	}
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setToFullscreen();
		setToPortrait();
		
//		ZLog.e("SO LIB VERSION = " + XEffectJniUtils.getInstance().getJniVersion());
		StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().detectAll().build());
		StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder().penaltyDeath().build());
	}

	private void init() {
		setContentView(R.layout.activity_shoot_new);
		
		finder = getZViewFinder();

		mainWindow = finder.findFrameLayout(R.id.video_view);
		mainWindowMask = finder.findFrameLayout(R.id.video_view_mask);
		
		effectUtils = new EffectUtils(this);
		effectUtils.parseXml("effectcfg/effectlist.xml");
		effectBeans = effectUtils.getEffects(EffectBean.TYPE_EFFECTS_VIDEO);
		
		if (PluginUtils.isPluginMounted()) {
			effects = new XEffects();
			mediaRecorder = new XVideoRecorder(this, effects, this, CamaraMode.DEF_PHOTO_COMMON);
			isHighDefinition = false;
		} else {
			mediaRecorder = new XVideoRecorder(this, effects, this, CamaraMode.DEF_PHOTO_HEIGHT);
			isHighDefinition = true;
		}
		mediaRecorder.setCurrentPreviewMode(PreviewMode.V_SHOW_MODLE_4P3);
		mediaRecorder.setXSurfaceSizeChange(this);
		currentPreviewMode = PreviewMode.V_SHOW_MODLE_4P3;
		mainWindow.addView(mediaRecorder.getXSurfaceView());
		
		effectsControlLayer = finder.findAbsoluteLayout(R.id.effects_control_layer);
		mosaicControlLayer = finder.findRelativeLayout(R.id.mosaic_control);

		effectsLayer = finder.findRadioGroup(R.id.effects_wrap_layout);

		leftTopButton = finder.findImageView(R.id.left_top_button);
		leftTopButton.setOnClickListener(this);
		
		middleTopButton = finder.findImageView(R.id.middle_top_button);
		middleTopButton.setOnClickListener(this);
		
		rightTopButton = finder.findImageView(R.id.right_top_button);
		rightTopButton.setOnClickListener(this);
		if (mediaRecorder.getNumberOfCameras() <= 1) {
			rightTopButton.setVisibility(View.INVISIBLE);
		}
		
		camreaControlButtons = finder.findLinearLayout(R.id.camera_control_buttons);
		shootState = finder.findLinearLayout(R.id.shoot_state);

		leftButton1 = finder.findImageView(R.id.left_button_1);
		leftButton1.setOnClickListener(this);
		leftButton2 = finder.findImageView(R.id.left_button_2);
		leftButton2.setOnClickListener(this);
		leftButton3 = finder.findImageView(R.id.left_button_3);
		leftButton3.setOnClickListener(this);
		leftButton4 = finder.findImageView(R.id.left_button_4);
		leftButton4.setOnClickListener(this);
		leftButton5 = finder.findImageView(R.id.left_button_5);
		leftButton5.setOnClickListener(this);
		
		if (isHighDefinition) {
			leftButton1.setImageResource(R.drawable.hd_open);
		} else {
			leftButton1.setImageResource(R.drawable.hd_close);
		}

		seekBarShell = finder.findLinearLayout(R.id.seek_bar_shell);
		
		seekBar = (VerticalSeekBar2) finder.findView(R.id.seek_bar);
		seekBar.setOnSeekBarChangeListener(this);
		
		LinearLayout seekBarWrapper = finder.findLinearLayout(R.id.seek_bar_wrapper);
		seekBarWrapper.setOnTouchListener(new OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				boolean b = false;
				int y = (int) event.getY();
				seekBar.getGlobalVisibleRect(seekBarRect);
				
				if (y >= seekBarRect.top && y <= seekBarRect.bottom) {
					event.offsetLocation(0, -seekBarRect.top);
					seekBar.onTouchEvent(event);
					b = true;
				}
				return b;
			}
		});

		effectsGroup = finder.findHorizontalScrollView(R.id.effects_group);
		effectsGroup.setVisibility(View.INVISIBLE);
		
		zoominButton = finder.findImageView(R.id.seek_bar_zoom_in);
		zoominButton.setOnClickListener(this);
		zoomoutButton = finder.findImageView(R.id.seek_bar_zoom_out);
		zoomoutButton.setOnClickListener(this);

		shootState = finder.findLinearLayout(R.id.shoot_state);

		effectsButton = (ToggleButton) finder.findView(R.id.effects);
		effectsButton.setOnClickListener(this);
		effectsButton.setTag(false);

		delaySnapshotButton = (ToggleButton) finder.findView(R.id.delay_snapshot);
		delaySnapshotButton.setOnClickListener(this);
		delaySnapshotButton.setVisibility(View.VISIBLE);
		

		takeSnapshotButton = finder.findImageButton(R.id.take_snapshot);
		takeSnapshotButton.setOnClickListener(this);
		if (mediaRecorder.isSupportTakePicuture()) {
			takeSnapshotButton.setVisibility(View.GONE);
		}

		pauseVideoShoot = (ToggleButton) finder.findView(R.id.pause_video_shoot);
		pauseVideoShoot.setOnClickListener(this);
		pauseVideoShoot.setVisibility(View.GONE);

		modeSwitch = (ToggleButton) finder.findView(R.id.mode_switch);
		modeSwitch.setOnClickListener(this);
		modeSwitch.setChecked(true);

		recordButton = finder.findImageView(R.id.record);
		recordButton.setOnClickListener(this);
		recordButton.setTag(false);
		recordButton.setOnLongClickListener(this);

		bottomButtons = finder.findRelativeLayout(R.id.bottom_buttons);
		bottomButtons.bringToFront();
		
		delayShootNumber = finder.findImageView(R.id.delay_shoot_number);
		delayShootNumber.bringToFront();
		
		mask = finder.findImageView(R.id.mask);
		mask.bringToFront();
		
		latestMedia = finder.findImageView(R.id.latest_media);
		latestMedia.setOnClickListener(this);

		setupEffectsIcon();
		
		seekBarRect = new Rect();
		effectsGroupRect = new Rect();
		effectsControlLayerRect = new Rect();
		mosaicControlBlockSize = ZScreen.dipToPixels(25);
		
		diaryWrappers = new HashMap<String, VideoShootActivity.TempDiaryWrapper>();
		diarys = new ArrayList<TempDiaryWrapper>();
		diaryList = new ArrayList<GsonResponse2.MyDiary>();
		
		uid = ActiveAccount.getInstance(MainApplication.getAppInstance()).getUID();
		currentOrientation = ORIENTATION_UP;
		
		lastDefinition = isHighDefinition ? CDR_LABEL_SIZE_HD : CDR_LABEL_SIZE_CD;
		lastWorkMode = CDR_LABEL_WORK_MODE_TAKE_PICTURE;
		lastCamera = CDR_LABEL_CAMERA_BACK;
		lastRatio = CDR_LABEL_RATIO_4_3;
		
		lastWorkModeStartMillis = TimeHelper.getInstance().now();
		lastCameraModeStartMillis = TimeHelper.getInstance().now();
		lastScreenRatioModeStartMillis = TimeHelper.getInstance().now();
		lastDefinitionModeStartMillis = TimeHelper.getInstance().now();
		
		isFocusLocked = true;
		seekBarShell.setEnabled(false);
		seekBar.setEnabled(false);
		leftButton4.setImageResource(R.drawable.focal_distance_locked);
		
		LowStorageChecker.check(this, this);
		
//		onScreenLog = finder.findTextView(R.id.on_screen_log);
		
	}
	
	private void setupEffectsIcon() {
		if(effectBeans != null){
			for (int i = 0; i < effectBeans.size(); i++) {
				
				int drawableId = 0;
				
				switch(i) {
				case 0:
					drawableId = R.drawable.effect_selector_0;
					break;
				case 1:
					drawableId = R.drawable.effect_selector_1;
					break;
				case 2:
					drawableId = R.drawable.effect_selector_2;
					break;
				case 3:
					drawableId = R.drawable.effect_selector_3;
					break;
				case 4:
					drawableId = R.drawable.effect_selector_4;
					break;
				case 5:
					drawableId = R.drawable.effect_selector_5;
					break;
				case 6:
					drawableId = R.drawable.effect_selector_6;
					break;
				case 7:
					drawableId = R.drawable.effect_selector_7;
					break;
				case 8:
					drawableId = R.drawable.effect_selector_8;
					break;
				case 9:
					drawableId = R.drawable.effect_selector_9;
					break;
				case 10:
					drawableId = R.drawable.effect_selector_10;
					break;
				case 11:
					drawableId = R.drawable.effect_selector_11;
					break;
				case 12:
					drawableId = R.drawable.effect_selector_12;
					break;
				case 13:
					drawableId = R.drawable.effect_selector_13;
					break;
				case 14:
					drawableId = R.drawable.effect_selector_14;
					break;
				case 15:
					drawableId = R.drawable.effect_selector_15;
					break;
				case 16:
					drawableId = R.drawable.effect_selector_16;
					break;
				case 17:
					drawableId = R.drawable.effect_selector_17;
					break;
				case 18:
					drawableId = R.drawable.effect_selector_18;
					break;
				case 19:
					drawableId = R.drawable.effect_selector_19;
					break;
				case 20:
					drawableId = R.drawable.effect_selector_20;
					break;
				case 21:
					drawableId = R.drawable.effect_selector_21;
					break;
				}
				Drawable drawable = getResources().getDrawable(drawableId);
				
				EffectBean bean = effectBeans.get(i);
				
				LinearLayout layout = (LinearLayout) ZLayoutInflater.inflate(R.layout.include_effects_and_scenes_buttons_stub);
				RadioButton radioButton = (RadioButton) layout.findViewById(R.id.radio_button);
				radioButton.setId(i);
				radioButton.setText(bean.getZHName());
				radioButton.setCompoundDrawablesWithIntrinsicBounds(null, drawable, null, null);
				radioButton.setOnClickListener(this);
				radioButton.setTag(false);
				radioButton.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
				layout.removeAllViewsInLayout();
				effectsLayer.addView(radioButton);
				if (i == 0) {
					radioButton.setChecked(true);
					currentCheckedEffectButton = radioButton;
				}
			}
		}
	}
	
	/**
	 * 处理马赛克操作界面的触摸逻辑, 并协调其他UI元素的触摸事件;
	 */
	@Override
	public boolean dispatchTouchEvent(MotionEvent event) {

		boolean b = super.dispatchTouchEvent(event);

		int x = (int) event.getX();
		int y = (int) event.getY();

		if (effectsGroup.getVisibility() == View.VISIBLE) {
			effectsGroup.getGlobalVisibleRect(effectsGroupRect);
			if (y < effectsGroupRect.top - ZScreen.dipToPixels(20)) {
//				effectsButtonClicked(); // 保留;
			}
		} else if (camreaControlButtons.getVisibility() == View.VISIBLE) {
			camreaControlButtons.getGlobalVisibleRect(effectsGroupRect);
			if (x > effectsGroupRect.right) {
//				hideCameraControlButtons(); // 保留;
			}
		}

		if (isMosaicControlShowing && !b) {
			switch (event.getAction()) {

			case MotionEvent.ACTION_DOWN:
				mosaicControlLayer.getGlobalVisibleRect(effectsGroupRect);
				effectsGroupRect.left = mosaicControlLayer.getLeft();
				effectsGroupRect.right = mosaicControlLayer.getRight();
				effectsGroupRect.top = mosaicControlLayer.getTop();
				effectsGroupRect.bottom = mosaicControlLayer.getBottom();

				mosaicControlParams.x = effectsGroupRect.left;
				mosaicControlParams.y = effectsGroupRect.top;
				mosaicControlParams.width = effectsGroupRect.width();
				mosaicControlParams.height = effectsGroupRect.height();

				if (Math.abs(effectsGroupRect.left - x) < mosaicControlBlockSize 
						&& Math.abs(effectsGroupRect.top - y) < mosaicControlBlockSize) {
					isTouchInCenter = false;
					isTouchAtLeftTop = true;
					isTouchAtRightTop = false;
					isTouchAtRightBottom = false;
					isTouchAtLeftBottom = false;
					b = true;

				} else if (Math.abs(effectsGroupRect.right - x) < mosaicControlBlockSize 
						&& Math.abs(effectsGroupRect.top - y) < mosaicControlBlockSize) {
					isTouchInCenter = false;
					isTouchAtLeftTop = false;
					isTouchAtRightTop = true;
					isTouchAtRightBottom = false;
					isTouchAtLeftBottom = false;
					b = true;

				} else if (Math.abs(effectsGroupRect.right - x) < mosaicControlBlockSize 
						&& Math.abs(effectsGroupRect.bottom - y) < mosaicControlBlockSize) {
					isTouchInCenter = false;
					isTouchAtLeftTop = false;
					isTouchAtRightTop = false;
					isTouchAtRightBottom = true;
					isTouchAtLeftBottom = false;
					b = true;

				} else if (Math.abs(effectsGroupRect.left - x) < mosaicControlBlockSize 
						&& Math.abs(effectsGroupRect.bottom - y) < mosaicControlBlockSize) {
					isTouchInCenter = false;
					isTouchAtLeftTop = false;
					isTouchAtRightTop = false;
					isTouchAtRightBottom = false;
					isTouchAtLeftBottom = true;
					b = true;

				} else if (effectsGroupRect.contains(x, y)) {
					isTouchInCenter = true;
					isTouchAtLeftTop = false;
					isTouchAtRightTop = false;
					isTouchAtRightBottom = false;
					isTouchAtLeftBottom = false;
					b = true;
				}

				lastX = x;
				lastY = y;
				break;

			case MotionEvent.ACTION_MOVE:
				int offsetX = x - lastX;
				int offsetY = y - lastY;
				effectsControlLayer.getGlobalVisibleRect(effectsControlLayerRect);

				if (isTouchAtLeftTop) {
					mosaicControlParams.width -= 2 * offsetX;
					mosaicControlParams.height -= 2 * offsetY;
					
					if (mosaicControlParams.width < 3 * mosaicControlBlockSize) {
						mosaicControlParams.width = 3 * mosaicControlBlockSize;
						offsetX = 0;
					} else if (mosaicControlParams.width > effectsControlLayerRect.width()) {
						mosaicControlParams.width = effectsControlLayerRect.width();
					}
					if (mosaicControlParams.height < 3 * mosaicControlBlockSize) {
						mosaicControlParams.height = 3 * mosaicControlBlockSize;
						offsetY = 0;
					} else if (mosaicControlParams.height > effectsControlLayerRect.height()) {
						mosaicControlParams.height = effectsControlLayerRect.height();
					}
					mosaicControlParams.x += offsetX;
					mosaicControlParams.y += offsetY;
					b = true;

				} else if (isTouchAtRightTop) {
					mosaicControlParams.width += 2 * offsetX;
					mosaicControlParams.height -= 2 * offsetY;

					if (mosaicControlParams.width < 3 * mosaicControlBlockSize) {
						mosaicControlParams.width = 3 * mosaicControlBlockSize;
						offsetX = 0;
					} else if (mosaicControlParams.width > effectsControlLayerRect.width()) {
						mosaicControlParams.width = effectsControlLayerRect.width();
						offsetX *= -1;
					}
					if (mosaicControlParams.height < 3 * mosaicControlBlockSize) {
						mosaicControlParams.height = 3 * mosaicControlBlockSize;
						offsetY = 0;
					} else if (mosaicControlParams.height > effectsControlLayerRect.height()) {
						mosaicControlParams.height = effectsControlLayerRect.height();
					}
					mosaicControlParams.x -= offsetX;
					mosaicControlParams.y += offsetY;
					b = true;

				} else if (isTouchAtRightBottom) {
					mosaicControlParams.width += 2 * offsetX;
					mosaicControlParams.height += 2 * offsetY;

					if (mosaicControlParams.width < 3 * mosaicControlBlockSize) {
						mosaicControlParams.width = 3 * mosaicControlBlockSize;
						offsetX = 0;
					} else if (mosaicControlParams.width > effectsControlLayerRect.width()) {
						mosaicControlParams.width = effectsControlLayerRect.width();
						offsetX *= -1;
					}
					if (mosaicControlParams.height < 3 * mosaicControlBlockSize) {
						mosaicControlParams.height = 3 * mosaicControlBlockSize;
						offsetY = 0;
					} else if (mosaicControlParams.height > effectsControlLayerRect.height()) {
						mosaicControlParams.height = effectsControlLayerRect.height();
						offsetY *= -1;
					}
					mosaicControlParams.x -= offsetX;
					mosaicControlParams.y -= offsetY;
					b = true;

				} else if (isTouchAtLeftBottom) {
					mosaicControlParams.width -= 2 * offsetX;
					mosaicControlParams.height += 2 * offsetY;

					if (mosaicControlParams.width < 3 * mosaicControlBlockSize) {
						mosaicControlParams.width = 3 * mosaicControlBlockSize;
						offsetX = 0;
					} else if (mosaicControlParams.width > effectsControlLayerRect.width()) {
						mosaicControlParams.width = effectsControlLayerRect.width();
					}
					if (mosaicControlParams.height < 3 * mosaicControlBlockSize) {
						mosaicControlParams.height = 3 * mosaicControlBlockSize;
						offsetY = 0;
					} else if (mosaicControlParams.height > effectsControlLayerRect.height()) {
						mosaicControlParams.height = effectsControlLayerRect.height();
						offsetY *= -1;
					}
					mosaicControlParams.x += offsetX;
					mosaicControlParams.y -= offsetY;
					b = true;

				} else if (isTouchInCenter) {
					mosaicControlParams.x += offsetX;
					mosaicControlParams.y += offsetY;
					b = true;
				}

				if (mosaicControlParams.x <= 0) {
					mosaicControlParams.x = 0;
				}
				if (mosaicControlParams.x + mosaicControlParams.width >= effectsControlLayerRect.width()) {
					mosaicControlParams.x = effectsControlLayerRect.width() - mosaicControlParams.width;
				}
				if (mosaicControlParams.y <= 0) {
					mosaicControlParams.y = 0;
				}
				if (mosaicControlParams.y + mosaicControlParams.height >= effectsControlLayerRect.height()) {
					mosaicControlParams.y = effectsControlLayerRect.height() - mosaicControlParams.height;
				}

				mosaicControlLayer.setLayoutParams(mosaicControlParams);
				
				
				if (b) {
					getHandler().removeMessages(MESSAGE_SWITCH_EFFECTS);
					isMosaicControling = true;
					switchEffect(Config.MOSAIC_INDEX);
				} else {
					isMosaicControling = false;
				}

				lastX = x;
				lastY = y;
				break;

			case MotionEvent.ACTION_UP:
				isTouchInCenter = false;
				isTouchAtLeftTop = false;
				isTouchAtRightTop = false;
				isTouchAtRightBottom = false;
				isTouchAtLeftBottom = false;
				if (b) {
					isMosaicControling = true;
					switchEffect(Config.MOSAIC_INDEX);
				} else {
//					isMosaicControling = false;
				}
				break;
				
			case MotionEvent.ACTION_CANCEL:
				isMosaicControling = false;
				break;
			}
		}

		if (!b) {
			return super.dispatchTouchEvent(event);
		}

		return b;
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
//		if (event.getAction() == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_HOME) {
//			ZThread.sleep(Config.VIDEO_SHOOT_BUTTON_BLOCKING_MILLIS * );
//		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		boolean b = false;
		
		if (!isMosaicControling && !isFocusLocked) {
			b = mediaRecorder.onTouchZoomEvent(event);
			
			if (b) {
				if (mediaRecorder.getCameraZoom() == 0 ) { // 焦距缩小到0后, 隐藏焦距控制条;
					seekBarShell.setVisibility(View.GONE);
				} else {
//					seekBarShell.setAlpha(1);
					seekBarShell.setVisibility(View.VISIBLE);
					float zoomProgress = (float) mediaRecorder.getCameraZoom() / (float) mediaRecorder.getCameraMaxZoom() * 100;
					seekBar.setProgress((int) zoomProgress);
				}
			}
		}
		
		if (!b && !isMosaicControling && !isSnapshoting && !isAutoSnapshoting) {
			b = mediaRecorder.onTouchFocusEvent(event);
		}
		
		if (b) {
			return b;
		} else {
			return super.onTouchEvent(event);
		}
	}

	@Override
	public void onBackPressed() {
		this.overridePendingTransition(R.anim.z_animation_slide_in_from_left, R.anim.z_animation_slide_out_to_right);
		super.onBackPressed();
	}
	
	@Override
	protected void onResume() {
		ZLog.e();
		init();
		UmengclickAgentWrapper.onResume(this);
		CmmobiClickAgentWrapper.onResume(this);
		ZSensorManager.registerListener(this, Sensor.TYPE_ACCELEROMETER, SensorManager.SENSOR_DELAY_UI);
		if (diarys.size() > 0) {
			latestMedia.setImageBitmap(diarys.get(0).thumbnail);
		}
		HomeActivity.isCameraStart=false;
		super.onResume();
	}

	@Override
	protected void onPause() {
		ZLog.e();
		UmengclickAgentWrapper.onPause(this);
		CmmobiClickAgentWrapper.onPause(this);
		
		ZSensorManager.unregister(this, Sensor.TYPE_ACCELEROMETER);
		cancelAutoSnapshot();
		cancelDelaySnapshot(false);
		handler.removeMessages(MESSAGE_TAKE_PICTURE);
		if (mediaRecorder.isRecording()) {
			if (videoShootTotalMillis <= 2000) {
				long millis = Config.VIDEO_SHOOT_BUTTON_BLOCKING_MILLIS * (isHighDefinition ? 1 : 2);
				ZThread.sleep(millis);
			}
			switchRecordState();
		}
		if (mediaRecorder != null) {
			mediaRecorder.release();
		}
		if (PluginUtils.isPluginMounted() && effects != null) {
			effects.release();
		}
		AccountInfo.getInstance(uid).persist();
		
		super.onPause();
	}
	
	@Override
	protected void onStop() {
		ZLog.e();
		CmmobiClickAgentWrapper.onEventDuration(this, CDR_KEY_SHP_TYPE, lastWorkMode, TimeHelper.getInstance().now() - lastWorkModeStartMillis);
		
		HashMap<String, String> hashMap = null;
		
		hashMap = new HashMap<String, String>();
		hashMap.put(CDR_LABEL_NAME_1, lastWorkMode);
		hashMap.put(CDR_LABEL_NAME_2, lastCamera);
		CmmobiClickAgentWrapper.onEventDuration(this, CDR_KEY_SHP_SW_CAM, hashMap, TimeHelper.getInstance().now() - lastCameraModeStartMillis);
		
		hashMap = new HashMap<String, String>();
		hashMap.put(CDR_LABEL_NAME_1, lastWorkMode);
		hashMap.put(CDR_LABEL_NAME_2, lastRatio);
		CmmobiClickAgentWrapper.onEventDuration(this, CDR_KEY_SHP_SCR_SIZ, hashMap, TimeHelper.getInstance().now() - lastScreenRatioModeStartMillis);
		
		hashMap = new HashMap<String, String>();
		hashMap.put(CDR_LABEL_NAME_1, lastWorkMode);
		hashMap.put(CDR_LABEL_NAME_2, lastDefinition);
		CmmobiClickAgentWrapper.onEventDuration(this, CDR_KEY_SHP_SCR_QUA, hashMap, TimeHelper.getInstance().now() - lastDefinitionModeStartMillis);
		
		CmmobiClickAgentWrapper.onStop(this);
		
		super.onStop();
	}
	
	@Override
	protected void onDestroy() {
		ZLog.e();
		if (mediaRecorder != null) {
			mediaRecorder.release();
		}
		super.onDestroy();
	}

	private void resumeCameraSettings(boolean needToaddView) {
		if (isGridShowing) {
			mediaRecorder.changeGliderLine(isGridShowing);
		}
		if (isGradienterShowing) {
			mediaRecorder.changeBalance(isGradienterShowing);
		}
		if (seekBarShell.getVisibility() == View.VISIBLE) {
			seekBar.setProgress(0);
		}
		mediaRecorder.setCurrentPreviewMode(currentPreviewMode);
		if (needToaddView) {
			mainWindow.removeAllViews();
			mainWindow.addView(mediaRecorder.getXSurfaceView());
		}
	}
	
	@Override
	public boolean handleMessage(Message msg) {
		switch (msg.what) {
		
		case MESSAGE_SWITCH_EFFECTS:
			int effectId = msg.arg1;
			
			EffectBean bean = effectBeans.get(effectId);
			
			switch (bean.getType()) {
			
			case EffectType.KEF_TYPE_MOSAIC: // 马赛克特效
				if (!isMosaicInitialized) {
					isMosaicInitialized = true;
					effectsControlLayer.getGlobalVisibleRect(effectsControlLayerRect);
					int width = ZScreen.dipToPixels(150);
					int height = ZScreen.dipToPixels(150);
					int x = (effectsControlLayerRect.width() - width) / 2;
					int y = (effectsControlLayerRect.height() - height) / 2 - ZScreen.dipToPixels(50);
					mosaicControlLayer.setLayoutParams(new AbsoluteLayout.LayoutParams(width, height, x, y));
					mosaicControlParams = new AbsoluteLayout.LayoutParams(
							mosaicControlLayer.getWidth(), mosaicControlLayer.getHeight(),
							mosaicControlLayer.getLeft(), mosaicControlLayer.getTop());
				}
				
				int windowWidth = mainWindow.getWidth();
				int windowHeight = mainWindow.getHeight();
//				int videoWidth = mediaRecorder.getVideoWidth(); // 关于 预览分辨率??
//				int videoHeight = mediaRecorder.getVideoHeight();
				int videoWidth = EffectType.VIDEO_WIDTH;
				int videoHeight = EffectType.VIDEO_HEIGHT;

				mosaicX = mosaicControlLayer.getTop();
				mosaicY = windowWidth - mosaicControlLayer.getRight();
				mosaicWidth = mosaicControlLayer.getHeight();
				mosaicHeight = mosaicControlLayer.getWidth();
				
				if (isFrontCamera) {
					mosaicX = windowHeight - mosaicControlLayer.getBottom();
				}

				mosaicX = mosaicX * videoWidth / windowHeight;
				mosaicY = mosaicY * videoHeight / windowWidth;
				mosaicWidth = mosaicWidth * videoWidth / windowHeight;
				mosaicHeight = mosaicHeight * videoHeight / windowWidth;

				if (bean != null) {
					Rect rect = new Rect((int) mosaicX, (int) mosaicY, (int) (mosaicX + mosaicWidth), (int) (mosaicY + mosaicHeight));
					effectUtils.changeEffectdWithEffectBean(
							effects, 
							bean, 
							mediaRecorder.getVideoWidth(), 
							mediaRecorder.getVideoHeight(), 
							Config.MOSAIC_SIZE, 
							rect,
							mediaRecorder.getPreviewRotation());
				}
				mosaicControlLayer.setVisibility(View.VISIBLE);
				effectsControlLayer.postInvalidate();
				isMosaicControlShowing = true;
				break;

			default:
				mosaicControlLayer.setVisibility(View.GONE);
				isMosaicControlShowing = false;
				isMosaicControling = false;
				if (bean != null && effects != null) {
					effectUtils.changeEffectdWithEffectBean(
							effects, 
							bean, 
							mediaRecorder.getVideoWidth(), 
							mediaRecorder.getVideoHeight(), 
							0, 
							null);
				}
				break;
			}
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
//			messageCount ++;
//			
//			handler.post(new Runnable() {
//				
//				@Override
//				public void run() {
//					String string = "MessageCount : " + messageCount + "\n";
//					string += "CallbackCount : " + callbackCount;
//					onScreenLog.setText(string);
//				}
//			});
			
			CmmobiClickAgentWrapper.onEvent(this, CDR_KEY_SHP_SCRS);
			
			TempDiaryWrapper wrapper = requestUploadPictursDiary();
			mediaRecorder.takePicture(this, wrapper.mediaPath);
			
			if (!isHighDefinition) {
				MediaActionSoundWrapper.takePicture();
			}
			break;

		case Requester2.RESPONSE_TYPE_CREATE_STRUCTURE:
			GsonResponse2.createStructureResponse response = (createStructureResponse) msg.obj;
			
			TempDiaryWrapper wrapper2 = diaryWrappers.get(response.diaryuuid);
			wrapper2.requestDiaryOK = true;
			wrapper2.response = response;
			if (wrapper2.type == TempDiaryWrapper.TYPE_VIDEO) {
				createVideoDiary(wrapper2);
			} else if (wrapper2.type == TempDiaryWrapper.TYPE_PICTURE) {
				createPicturesDiary(wrapper2);
			}
			break;

		case MESSAGE_CREATE_PICTURE_DIARY:
			TempDiaryWrapper wrapper3 = (TempDiaryWrapper) msg.obj;
			wrapper3.mediaCatchOK = true;
			createPicturesDiary(wrapper3);
			break;

		case MESSAGE_UPDATE_SHOOT_STATE:
			handler.sendEmptyMessageDelayed(MESSAGE_UPDATE_SHOOT_STATE, 500);
			timing();
			setShootState();
			break;
			
		case MESSAGE_CHANGE_CAMERA:
			
			HashMap<String, String> hashMap = new HashMap<String, String>();
			hashMap.put(CDR_LABEL_NAME_1, lastWorkMode);
			hashMap.put(CDR_LABEL_NAME_2, lastCamera);
			CmmobiClickAgentWrapper.onEventDuration(this, CDR_KEY_SHP_SW_CAM, hashMap, TimeHelper.getInstance().now() - lastCameraModeStartMillis);
			
			ZThread.sleep(100);
			
			mediaRecorder.changeCamera();
			if (!mediaRecorder.isSupportFlashMode(FlashMode.FLASH_MODE_TORCH) 
					&& !mediaRecorder.isSupportFlashMode(FlashMode.FLASH_MODE_ON)) {
				middleTopButton.setVisibility(View.INVISIBLE);
			} else {
				middleTopButton.setVisibility(View.VISIBLE);
			}
			isFrontCamera = mediaRecorder.isFrontCamera();
			resumeCameraSettings(false);
			if (isFrontCamera) {
				lastCamera = CDR_LABEL_CAMERA_FRONT;
			} else {
				lastCamera = CDR_LABEL_CAMERA_BACK;
			}
			if (!isSupportZoom()) {
				isFocusLocked = true;
				seekBarShell.setVisibility(View.GONE);
				leftButton4.setImageResource(R.drawable.focal_distance_locked);
			} 
//			else if (!isHighDefinition) {
//				isFocusLocked = false;
//				seekBarShell.setVisibility(View.VISIBLE);
//				leftButton4.setImageResource(R.drawable.focal_distance_free);
//			}
			
			lastCameraModeStartMillis = TimeHelper.getInstance().now();
			switchEffect(lastEffectId);
			handler.postDelayed(new Runnable() {
				
				@Override
				public void run() {
					rightTopButton.setEnabled(true);
				}
			}, 500);
			break;
			
		case MESSAGE_SWITCH_HIGHT_COMMON: // 切换高清/普清;
			HashMap<String, String> hashMap2 = new HashMap<String, String>();
			hashMap2.put(CDR_LABEL_NAME_1, lastWorkMode);
			hashMap2.put(CDR_LABEL_NAME_2, lastDefinition);
			CmmobiClickAgentWrapper.onEventDuration(this, CDR_KEY_SHP_SCR_QUA, hashMap2, TimeHelper.getInstance().now() - lastDefinitionModeStartMillis);
			
			if (!isHighDefinition) {// locked;
				isCommonModeFocusLocked = isFocusLocked;
			}
			
			if (mediaRecorder.getCameraMode() == CamaraMode.DEF_VIDEO_HEIGHT) {
				mediaRecorder.setCameraMode(CamaraMode.DEF_VIDEO_COMMON, mainWindow);
				leftButton1.setImageResource(R.drawable.hd_close);
				isHighDefinition = false;
				lastDefinition = CDR_LABEL_SIZE_CD;
				
			} else if (mediaRecorder.getCameraMode() == CamaraMode.DEF_VIDEO_COMMON) {
				mediaRecorder.setCameraMode(CamaraMode.DEF_VIDEO_HEIGHT, mainWindow);
				leftButton1.setImageResource(R.drawable.hd_open);
				isHighDefinition = true;
				lastDefinition = CDR_LABEL_SIZE_HD;
				
			} else if (mediaRecorder.getCameraMode() == CamaraMode.DEF_PHOTO_HEIGHT) {
				mediaRecorder.setCameraMode(CamaraMode.DEF_PHOTO_COMMON, mainWindow);
				leftButton1.setImageResource(R.drawable.hd_close);
				isHighDefinition = false;
				lastDefinition = CDR_LABEL_SIZE_CD;
				
			} else if (mediaRecorder.getCameraMode() == CamaraMode.DEF_PHOTO_COMMON) {
				mediaRecorder.setCameraMode(CamaraMode.DEF_PHOTO_HEIGHT, mainWindow);
				leftButton1.setImageResource(R.drawable.hd_open);
				isHighDefinition = true;
				lastDefinition = CDR_LABEL_SIZE_HD;
			}
			mediaRecorder.setXSurfaceSizeChange(this);
			mosaicControlLayer.setVisibility(View.GONE);
			if (isHighDefinition) {// locked;
				isFocusLocked = true;
				seekBarShell.setVisibility(View.GONE);
				leftButton4.setImageResource(R.drawable.focal_distance_locked);
				effectsButton.setBackgroundResource(R.drawable.effect_group_disable);
				
				effectsGroup.setVisibility(View.INVISIBLE);
				effectsButton.setChecked(false);
				
				mosaicControlLayer.setVisibility(View.GONE);
				effectsControlLayer.postInvalidate();
				isMosaicControlShowing = false;
				isMosaicControling = false;
				
			} else {
				if (!isCommonModeFocusLocked && mediaRecorder.getCameraZoom() != 0) {
					isFocusLocked = false;
					seekBarShell.setVisibility(View.VISIBLE);
					leftButton4.setImageResource(R.drawable.focal_distance_free);
				}
				
				if (lastEffectId == Config.MOSAIC_INDEX) {
					mosaicControlLayer.setVisibility(View.VISIBLE);
					effectsControlLayer.postInvalidate();
					isMosaicControlShowing = true;
				}
				effectsButton.setBackgroundResource(R.drawable.effect_group_button_selector);
			}
			resumeCameraSettings(true);
			lastDefinitionModeStartMillis = TimeHelper.getInstance().now();
			break;
			
		case MESSAGE_SWITCH_MODE:
			CmmobiClickAgentWrapper.onEventDuration(this, CDR_KEY_SHP_TYPE, lastWorkMode, TimeHelper.getInstance().now() - lastWorkModeStartMillis);
			
			if (modeSwitch.isChecked()) { // 切换到拍照模式;
				delaySnapshotButton.setVisibility(View.VISIBLE);
				takeSnapshotButton.clearAnimation();
				takeSnapshotButton.setVisibility(View.GONE);
				if (mediaRecorder.getCameraMode() == CamaraMode.DEF_VIDEO_HEIGHT) {
					mediaRecorder.setCameraMode(CamaraMode.DEF_PHOTO_HEIGHT, mainWindow);
				} else {
					mediaRecorder.setCameraMode(CamaraMode.DEF_PHOTO_COMMON, mainWindow);
				}
				lastWorkMode = CDR_LABEL_WORK_MODE_TAKE_PICTURE;
				
			} else { // 切换到摄像模式;
				delaySnapshotButton.clearAnimation();
				delaySnapshotButton.setVisibility(View.GONE);
				if (mediaRecorder.isSupportTakePicuture()) {
					takeSnapshotButton.setVisibility(View.VISIBLE);
				}
				if (delaySnapshotButton.isChecked()) {
					handler.removeMessages(MESSAGE_TAKE_PICTURE);
				}
				if (mediaRecorder.getCameraMode() == CamaraMode.DEF_PHOTO_HEIGHT) {
					mediaRecorder.setCameraMode(CamaraMode.DEF_VIDEO_HEIGHT, mainWindow);
				} else {
					mediaRecorder.setCameraMode(CamaraMode.DEF_VIDEO_COMMON, mainWindow);
				}
				lastWorkMode = CDR_LABEL_WORK_MODE_SHOOT;
			}
			mediaRecorder.setXSurfaceSizeChange(this);
			resumeCameraSettings(true);
			mainWindow.setVisibility(View.VISIBLE);
			mainWindowMask.setVisibility(View.INVISIBLE);
			lastWorkModeStartMillis = TimeHelper.getInstance().now();
			break;

		case MESSAGE_PHOTO_ALBUM_UPDATING_ANIMATION:
			final Bitmap bitmap = (Bitmap) msg.obj;
			
			Rect toRect = new Rect();
			Rect fromRect = new Rect();
			latestMedia.getGlobalVisibleRect(toRect);
			mask.getGlobalVisibleRect(fromRect);
			
			float scaleX = (float) toRect.width() / (float) mask.getWidth();
			float scaleY = (float) toRect.height() / (float) mask.getHeight();
			
			float anchorX = (float) (toRect.left + 3) / (float) mask.getWidth();
			float anchorY = (float) ((toRect.bottom) + 6) / (float) mask.getHeight();
			
			Animation scaleAnimation = new ScaleAnimation(
					1f, scaleX, 
					1f, scaleY, 
					Animation.RELATIVE_TO_SELF, anchorX,
					Animation.RELATIVE_TO_SELF, anchorY);
			scaleAnimation.setInterpolator(new AccelerateDecelerateInterpolator());
			scaleAnimation.setDuration(ORIENTATION_ROTATE_SPEED + 100);
			scaleAnimation.setAnimationListener(new AnimationListener() {
				@Override
				public void onAnimationStart(Animation animation) {
				}
				@Override
				public void onAnimationRepeat(Animation animation) {
				}
				@Override
				public void onAnimationEnd(Animation animation) {
					AlphaAnimation animation2 = new AlphaAnimation(0.4f, 1.0f);
					animation2.setDuration(400);
					latestMedia.setImageBitmap(ThumbnailUtils.extractThumbnail(bitmap, 200, 200));
					latestMedia.clearAnimation();
					latestMedia.startAnimation(animation2);
					mask.setImageBitmap(null);
					isSnapshoting = false;
					isAnimationStop = true;
				}
			});
			mask.setImageBitmap(bitmap);
			mask.clearAnimation();
			mask.startAnimation(scaleAnimation);
			break;
		}
		return true;
	}

	private void startTimer() {
		if (isWifiShooting) {
			videoShootTotalMillis = SHOOT_REMAIN_MILLIS;
		} else {
			videoShootTotalMillis = 0;
		}
		videoClipStartMillis = TimeHelper.getInstance().now();
		handler.sendEmptyMessage(MESSAGE_UPDATE_SHOOT_STATE);
	}
	
	private void continueTimer() {
		videoClipStartMillis = TimeHelper.getInstance().now();
		handler.sendEmptyMessage(MESSAGE_UPDATE_SHOOT_STATE);
	}

	private void timing() {
		long clipMillis = TimeHelper.getInstance().now() - videoClipStartMillis;
		
		if (isWifiShooting) {
			videoShootTotalMillis -= clipMillis;
			if (videoShootTotalMillis < 0) {
				videoShootTotalMillis = 0;
				switchRecordState();
			}
		} else {
			videoShootTotalMillis += clipMillis;
		}
		videoClipStartMillis = TimeHelper.getInstance().now();
		
		if (!LowStorageChecker.check(this, this) && mediaRecorder.isRecording()) {
			switchRecordState();
		}
	}

	private void stopTimer() {
		handler.removeMessages(MESSAGE_UPDATE_SHOOT_STATE);
	}

	private void setShootState() {
		int totalSeconds = (int) (videoShootTotalMillis / 1000L);
		int minutes = totalSeconds / 60;
		int seconds = totalSeconds % 60;

		String time = "";
		if (minutes < 10) {
			time += "0";
		}
		time += minutes;
		time += ":";

		if (seconds < 10) {
			time += "0";
		}
		time += seconds;

		TextView shootTime = viewFinder.findTextView(R.id.shoot_time);
		shootTime.setText(time);
	}
	
	private boolean isSupportZoom() {
		return mediaRecorder != null && mediaRecorder.getCameraMaxZoom() != 0;
	}

	private synchronized void createVideoDiary(TempDiaryWrapper wrapper) {

		if (wrapper.mediaCatchOK && wrapper.requestDiaryOK) {

			String diaryid = null;
			String mediaURL = wrapper.request.attachs[0].attachuuid;
			String attachid = "";
			String attachuuid = wrapper.request.attachs[0].attachuuid;

			if (wrapper.response != null) {
				if (!TextUtils.isEmpty(wrapper.response.diaryid)) {
					diaryid = wrapper.response.diaryid;
				}
				if (wrapper.response.attachs != null && wrapper.response.attachs.length > 0) {
					mediaURL = wrapper.response.attachs[0].path;
					attachid = wrapper.response.attachs[0].attachid;
					attachuuid = wrapper.response.attachs[0].attachuuid;
				}
			}

			AccountInfo account = AccountInfo.getInstance(uid);
			String temp = wrapper.mediaName;
			int i = 0;
			while (temp.contains("-")) {
				temp = temp.replace("-"+i, "");
			}

			String absolutePath = wrapper.mediaPath + "/" + temp + "/" + wrapper.mediaName + ".mp4";
			String relativePath = absolutePath.replace(Environment.getExternalStorageDirectory().getPath(), "");
			
			MediaValue mediaValue = new MediaValue();
			mediaValue.UID = uid;
			mediaValue.path = relativePath;
			mediaValue.totalSize = new File(absolutePath).length();
			mediaValue.realSize = mediaValue.totalSize;
			mediaValue.MediaType = 5;
			mediaValue.Direction = 2;

			if (wrapper.response != null && "0".equals(wrapper.response.status) && wrapper.response.diaryid != null && wrapper.response.diaryid.length() > 0) {
				mediaValue.url = mediaURL;
			} else {
				mediaValue.url = wrapper.request.attachs[0].attachuuid;
			}

			if (MediaValue.checkMediaAvailable(mediaValue, 5)) {
				AccountInfo.getInstance(uid).mediamapping.setMedia(uid, mediaValue.url, mediaValue);
			}

			mediaURL = mediaValue.url;

			GsonResponse2.MyAttachVideo[] attachVideo = new GsonResponse2.MyAttachVideo[1];
			attachVideo[0] = new GsonResponse2.MyAttachVideo();
			attachVideo[0].playvideourl = mediaURL;
			attachVideo[0].videotype = isHighDefinition ? "0" : "1"; // 0高清 1普清;

			if (isWifiShooting) {
				videoShootTotalMillis = SHOOT_REMAIN_MILLIS - videoShootTotalMillis;
			}
			
			GsonResponse2.diaryAttach[] attachs = new diaryAttach[1];
			attachs[0] = new diaryAttach();
			attachs[0].attachid = attachid;
			attachs[0].attachtype = "1"; // 1视频 2音频 3图片 4文字;
			attachs[0].attachlevel = "1"; // 1主内容 0辅内容;
			attachs[0].attachvideo = attachVideo;
			attachs[0].attachuuid = attachuuid;
			attachs[0].playtime = String.valueOf(new Mp4InfoUtils(absolutePath).totaltime);
			attachs[0].videocover = relativePath + ".vc";
			
			// 获取videocover并登记mapping
			String newVideoCover = newVideoCover = MediaCoverUtils.getMediaCoverUrl(absolutePath);
			if (newVideoCover != null) {

				MediaValue mediaValueCover = new MediaValue();
				mediaValueCover.UID = uid;
				mediaValueCover.path = newVideoCover.replace(Environment.getExternalStorageDirectory().getPath(), "");
				mediaValueCover.totalSize = new File(newVideoCover).length();
				mediaValueCover.realSize = mediaValueCover.totalSize;
				mediaValueCover.MediaType = 2;
				mediaValueCover.url = attachs[0].videocover;
				if (MediaValue.checkMediaAvailable(mediaValueCover, 2)) {
					AccountInfo.getInstance(uid).mediamapping.setMedia(uid, mediaValueCover.url, mediaValueCover);
				}
			}
			// ========================================

			DiaryManager diaryManager = DiaryManager.getInstance();

			GsonResponse2.MyDiary diary = new GsonResponse2.MyDiary();
			diary.diaryid = diaryid;
			diary.diaryuuid = wrapper.request.diaryuuid;
			diary.weather = "";
			diary.weather_info = "";
			MyWeather weather = account.myWeather;
			if (weather != null) {
				if (weather.desc != null && weather.desc.length > 0) {
					diary.weather_info = weather.desc[0].description;
					diary.weather = weather.desc[0].weatherurl;
				}
			}
			diary.mood = "0";
			if (account.mood != null && !"".equals(account.mood)) {
				diary.mood = account.mood;
			}
			diary.userid = uid;
			diary.diarytimemilli = String.valueOf(wrapper.createTimeMillis);
			diary.updatetimemilli = String.valueOf(TimeHelper.getInstance().now());
			diary.diary_status = "1";
			diary.publish_status = account.setmanager.getDiary_type();
			
			if (CommonInfo.getInstance().myLoc != null) {
				diary.latitude = ZStringUtils.nullToEmpty(String.valueOf(CommonInfo.getInstance().myLoc.latitude));
				diary.longitude = ZStringUtils.nullToEmpty(String.valueOf(CommonInfo.getInstance().myLoc.longitude));
			}
			diary.position = "";
			if (MapItemHelper.locateAvailable(diary)) {
				diary.position = CommonInfo.getInstance().positionStr;
			}
			diary.signature = account.signature;
			diary.headimageurl = account.headimageurl;
			diary.attachs = attachs;
			diary.request = wrapper.request;
			diary.nickname = account.nickname;
			diary.sex = account.sex;
			diary.join_safebox = "0";
			diary.resourcediaryid = "";
			diary.resourceuuid = "";
			
			if (wrapper.response != null && "0".equals(wrapper.response.status)) {
				diary.sync_status = 1;
			}
			
			diaryManager.saveDiaries(diary, true);
			diaryList.add(0,diary);
			wrapper.diaryCreatedOK = true;
			if (isEnterLastDiary) {
				enterLastDiary();
			}
			
			INetworkTask task = null;
			
			if(wrapper.response != null && wrapper.response.diaryid != null && !"".equals(wrapper.response.diaryid)) {
				NetworkTaskInfo networktaskinfo = new NetworkTaskInfo(diary, INetworkTask.TASK_TYPE_UPLOAD); // 设置数据源;
				task = new UploadNetworkTask(networktaskinfo); // 创建上传/下载任务;
			} else {
				NetworkTaskInfo networktaskinfo = new NetworkTaskInfo(diary, INetworkTask.TASK_TYPE_CACHE); // 设置数据源;
				task = new CacheNetworkTask(networktaskinfo);// 创建上传/下载任务
			}

			NetworkTaskManager.getInstance(uid).addTask(task); // 添加网络任务;
			NetworkTaskManager.getInstance(uid).startNextTask(); // 开始任务队列;

			wrapper.mediaValue = mediaValue;
			wrapper.thumbnail = tryToGetVideoThumbnail(absolutePath);
			
			updateAlbum(wrapper.thumbnail);
			
			CmmobiClickAgentWrapper.onEventDuration(this, CDR_KEY_SHP_SH_SUCC, TimeHelper.getInstance().now() - videoShootStartMillis);
		}
	}
	
	private synchronized void createPicturesDiary(TempDiaryWrapper wrapper) {
		ZLog.e("createPicturesDiary()");
		ZLog.printObject(wrapper);
		if (wrapper.mediaCatchOK && wrapper.requestDiaryOK) {

			String diaryid = null;
			String mediaURL = wrapper.request.attachs[0].attachuuid;
			String attachid = "";
			String attachuuid = wrapper.request.attachs[0].attachuuid;

			if (wrapper.response != null) {
				if (!TextUtils.isEmpty(wrapper.response.diaryid)) {
					diaryid = wrapper.response.diaryid;
				}
				if (wrapper.response.attachs != null && wrapper.response.attachs.length > 0) {
					mediaURL = wrapper.response.attachs[0].path;
					attachid = wrapper.response.attachs[0].attachid;
					attachuuid = wrapper.response.attachs[0].attachuuid;
				}
			}

			AccountInfo account = AccountInfo.getInstance(uid);

			GsonResponse2.MyAttachImage[] attachImages = new GsonResponse2.MyAttachImage[1];
			attachImages[0] = new GsonResponse2.MyAttachImage();
			attachImages[0].imageurl = mediaURL;
			attachImages[0].imagetype = "0";

			GsonResponse2.diaryAttach[] attachs = new diaryAttach[1];
			attachs[0] = new diaryAttach();
			attachs[0].attachid = attachid;
			attachs[0].attachtype = "3"; // 1视频 2音频 3图片 4文字;
			attachs[0].attachlevel = "1"; // 1主内容 0辅内容;
			attachs[0].attachimage = attachImages;
			attachs[0].attachuuid = attachuuid;

			DiaryManager diaryManager = DiaryManager.getInstance();

			GsonResponse2.MyDiary diary = new GsonResponse2.MyDiary();
			diary.diaryid = diaryid;
			diary.diaryuuid = wrapper.request.diaryuuid;
			diary.weather = "";
			diary.weather_info = "";
			MyWeather weather = account.myWeather;
			
			if (weather != null) {
				if (weather.desc != null && weather.desc.length > 0) {
					diary.weather_info = weather.desc[0].description;
					diary.weather = weather.desc[0].weatherurl;
				}
			}
			ZLog.e("weather:" + diary.weather);
			ZLog.e("weather_info:" + diary.weather_info);
			diary.mood = "0";
			if (account.mood != null && !"".equals(account.mood)) {
				diary.mood = account.mood;
			}
			diary.userid = uid;
			diary.diarytimemilli = String.valueOf(wrapper.createTimeMillis);
			diary.updatetimemilli = String.valueOf(TimeHelper.getInstance().now());
			diary.diary_status = "1";
			diary.publish_status = account.setmanager.getDiary_type();
			
			if (CommonInfo.getInstance().myLoc != null) {
				diary.latitude = ZStringUtils.nullToEmpty(String.valueOf(CommonInfo.getInstance().myLoc.latitude));
				diary.longitude = ZStringUtils.nullToEmpty(String.valueOf(CommonInfo.getInstance().myLoc.longitude));
			}
			diary.position = "";
			if (MapItemHelper.locateAvailable(diary)) {
				diary.position = CommonInfo.getInstance().positionStr;
			}
			diary.signature = account.signature;
			diary.headimageurl = account.headimageurl;
			diary.attachs = attachs;
			diary.request = wrapper.request;
			diary.nickname = account.nickname;
			diary.sex = account.sex;
			diary.join_safebox = "0";
			diary.resourcediaryid = "";
			diary.resourceuuid = "";
			
			if (wrapper.response != null && "0".equals(wrapper.response.status)) {
				diary.sync_status = 1;
			}

			String relativePath = wrapper.mediaPath.replace(Environment.getExternalStorageDirectory().getPath(), "");
			MediaValue mediaValue = new MediaValue();
			mediaValue.UID = uid;
			mediaValue.path = relativePath;
			mediaValue.totalSize = new File(wrapper.mediaPath).length();
			mediaValue.realSize = mediaValue.totalSize;
			mediaValue.MediaType = 2;
			mediaValue.Direction = 2;

			if (wrapper.response != null && "0".equals(wrapper.response.status) && wrapper.response.diaryid != null && wrapper.response.diaryid.length() > 0) {
				mediaValue.url = mediaURL;
			} else {
				mediaValue.url = wrapper.request.attachs[0].attachuuid;
			}

			if (MediaValue.checkMediaAvailable(mediaValue, 2)) {
				AccountInfo.getInstance(uid).mediamapping.setMedia(uid, mediaValue.url, mediaValue);
			}
			
			try { // 获取picture cover并登记mapping;
				Display display =((WindowManager)getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
				int diaryWidth = display.getWidth();
				diaryWidth = diaryWidth/2-20;
				
				String path = wrapper.mediaPath.substring(0, wrapper.mediaPath.lastIndexOf("/") + 1);
				
				File file = new File(path, mediaURL + "&width=" + diaryWidth);
				file.createNewFile();
				
				FileOutputStream stream = new FileOutputStream(file);
				wrapper.thumbnail.compress(CompressFormat.JPEG, 100, stream);
				stream.flush();
				stream.close();
				
				String newPictureCover = path + mediaURL + "&width=" + diaryWidth;
				MediaValue mediaValueCover = new MediaValue();
				mediaValueCover.UID = uid;
				mediaValueCover.path = newPictureCover.replace(Environment.getExternalStorageDirectory().getPath(), "");
				mediaValueCover.totalSize = new File(newPictureCover).length();
				mediaValueCover.realSize = mediaValueCover.totalSize;
				mediaValueCover.MediaType = 2;
				mediaValueCover.url = mediaURL + "&width=" + diaryWidth;
				
				if (MediaValue.checkMediaAvailable(mediaValueCover, 2)) {
					AccountInfo.getInstance(uid).mediamapping.setMedia(uid, mediaValueCover.url, mediaValueCover);
				}
			} catch(Exception e) {
				e.printStackTrace();
			}
			
			diaryManager.saveDiaries(diary, true);
			diaryList.add(0,diary);
			wrapper.diaryCreatedOK = true;
			if (isEnterLastDiary) {
				enterLastDiary();
			}
			
			INetworkTask task = null;

			if(wrapper.response != null && wrapper.response.diaryid != null && !"".equals(wrapper.response.diaryid)) {
				NetworkTaskInfo networktaskinfo = new NetworkTaskInfo(diary, INetworkTask.TASK_TYPE_UPLOAD); // 设置数据源;
				task = new UploadNetworkTask(networktaskinfo); // 创建上传/下载任务;
			} else {
				NetworkTaskInfo networktaskinfo = new NetworkTaskInfo(diary, INetworkTask.TASK_TYPE_CACHE); // 设置数据源;
				task = new CacheNetworkTask(networktaskinfo);// 创建上传/下载任务
			}
			NetworkTaskManager.getInstance(uid).addTask(task); // 添加网络任务;
			NetworkTaskManager.getInstance(uid).startNextTask(); // 开始任务队列;

			wrapper.mediaValue = mediaValue;
		}
	}
	
	/**
	 * 尝试获取视频缩略图, 会阻塞调用者线程 (一般是子线程来执行);
	 * @param Bitmap
	 * @return
	 */
	private Bitmap tryToGetPictureThumbnail(Bitmap resource) {
		Bitmap bitmap = null;
		
		for (int i=0; i<3; i++) {
			bitmap = ThumbnailUtils.extractThumbnail(resource, 384, 512);
			if (bitmap == null) {
				ZThread.sleep(200);
			} else {
				break;
			}
		}
		
		return bitmap;
	}
	
	/**
	 * 尝试获取图片缩略图, 会阻塞调用者线程 (一般是子线程来执行);
	 * @param absolutePath
	 * @return
	 */
	private Bitmap tryToGetVideoThumbnail(String absolutePath) {
		Bitmap bitmap = null;
		
		for (int i=0; i<3; i++) {
			if (isHighDefinition) {
				bitmap = ThumbnailUtils.createVideoThumbnail(absolutePath, Thumbnails.MINI_KIND);
			} else {
				bitmap = Mp4InfoUtils.getVideoCapture(absolutePath, 384, 512);
			}
			
			if (bitmap == null) {
				ZThread.sleep(200);
			} else {
				break;
			}
		}
		
		return bitmap;
	}

	@Override
	public void onClick(final View view) {
		switchOrientation(currentOrientation);// 强制立即转向, 因为在三星手机上, 点击view后, view方向会恢复到旋转前;

		if (isSurfaceCreated) {
			switch (view.getId()) {

			case R.id.effects:
				effectsButtonClicked();
				break;
				
			case R.id.middle_top_button: // 闪光灯开关;
				isAllowFlashlight = !isAllowFlashlight;
				if (isAllowFlashlight) {
					middleTopButton.setImageResource(R.drawable.btn_activity_video_shoot_flashlight_on_button);
				} else {
					middleTopButton.setImageResource(R.drawable.btn_activity_video_shoot_flashlight_off_button);
				}
				break;
				
			case R.id.right_top_button: // 前后摄像头切换;
				rightTopButton.setEnabled(false);
				handler.sendEmptyMessage(MESSAGE_CHANGE_CAMERA);
				mediaRecorder.changeGliderLine(false);
				mediaRecorder.changeBalance(false);
				break;
				
			case R.id.left_top_button: // 摄像机控制按钮开关;
				if (camreaControlButtons.getVisibility() == View.VISIBLE) {
					hideCameraControlButtons();
				} else {
					camreaControlButtons.setVisibility(View.VISIBLE);
					leftTopButton.setBackgroundResource(R.drawable.camera_buttons_background_top);
				}
				break;

			case R.id.left_button_1: // 高清/普清切换;
				if (isRecording) {
					ZToast.showShort(R.string.recording);
					
				} else if (!EffectsDownloadUtil.getInstance(this).checkEffects()) {
					cancelDelaySnapshot(false);
					handler.sendEmptyMessage(MESSAGE_SWITCH_HIGHT_COMMON);
					mediaRecorder.changeGliderLine(false);
					mediaRecorder.changeBalance(false);
				}
				break;

			case R.id.left_button_2: // 16:9 / 4:3切换;
				HashMap<String, String> hashMap = new HashMap<String, String>();
				hashMap.put(CDR_LABEL_NAME_1, lastWorkMode);
				hashMap.put(CDR_LABEL_NAME_2, lastRatio);
				CmmobiClickAgentWrapper.onEventDuration(this, CDR_KEY_SHP_SCR_SIZ, hashMap, TimeHelper.getInstance().now() - lastScreenRatioModeStartMillis);
				
				if (mediaRecorder.getCurrentPreviewMode() == PreviewMode.V_SHOW_MODLE_4P3) {
					mediaRecorder.setCurrentPreviewMode(PreviewMode.V_SHOW_MODLE_16P9);
					leftButton2.setImageResource(R.drawable.ratio_16_9);
					lastRatio = CDR_LABEL_RATIO_16_9;
				} else {
					mediaRecorder.setCurrentPreviewMode(PreviewMode.V_SHOW_MODLE_4P3);
					leftButton2.setImageResource(R.drawable.ratio_4_3);
					lastRatio = CDR_LABEL_RATIO_4_3;
				}
				currentPreviewMode = mediaRecorder.getCurrentPreviewMode();
				lastScreenRatioModeStartMillis = TimeHelper.getInstance().now();
				break;

			case R.id.left_button_3: // 参考线;
				mediaRecorder.changeGliderLine(!mediaRecorder.isShowGliderLine());
				isGridShowing = mediaRecorder.isShowGliderLine();
				break;

			case R.id.left_button_4: // 焦距锁定按钮;
				if (!isFocusLocked) {
					isFocusLocked = true;
					seekBarShell.setVisibility(View.GONE);
					leftButton4.setImageResource(R.drawable.focal_distance_locked);
				} else if (!isHighDefinition && isSupportZoom()) {
					isFocusLocked = false;
					seekBarShell.setEnabled(true);
					seekBar.setEnabled(true);
					if (mediaRecorder.getCameraZoom() != 0) {
//						seekBarShell.setAlpha(1);
						seekBarShell.setVisibility(View.VISIBLE);
					}
					leftButton4.setImageResource(R.drawable.focal_distance_free);
				}
				break;

			case R.id.left_button_5: // 水平仪;
				mediaRecorder.changeBalance(!mediaRecorder.isShowBalance());
				isGradienterShowing = mediaRecorder.isShowBalance();
				break;

			case R.id.mode_switch: // 模式切换按钮(拍摄/拍照);
				mainWindow.setVisibility(View.INVISIBLE);
				mainWindowMask.setVisibility(View.VISIBLE);
				mainWindowMask.invalidate();
				
				cancelDelaySnapshot(false);
				handler.sendEmptyMessage(MESSAGE_SWITCH_MODE);
				mediaRecorder.changeGliderLine(false);
				mediaRecorder.changeBalance(false);
				break;

			case R.id.pause_video_shoot: // 暂停拍摄;
				if (!isShootPausing) {
					isShootPausing = true;
					pauseVideoShoot.setBackgroundResource(R.drawable.continue_button_selector);
					mediaRecorder.pause();
					stopTimer();
				} else {
					isShootPausing = false;
					pauseVideoShoot.setBackgroundResource(R.drawable.pause_button_selector);
					mediaRecorder.resume();
					continueTimer();
				}
				pauseVideoShoot.setChecked(false);
				break;

			case R.id.take_snapshot: // 拍照;
				if (LowStorageChecker.check(this, this)) {
					if (!isSnapshoting) {
						isSnapshoting = true;
						
						tempSnapshotButton = takeSnapshotButton;
						tempSnapshotButton.setEnabled(false);
						
						Message message1 = new Message();
						message1.what = MESSAGE_TAKE_PICTURE;
						handler.sendEmptyMessage(MESSAGE_TURN_ON_FLASHLIGHT_TORCH);
						handler.sendMessageDelayed(message1, TURN_ON_FLASHLIGHT_DELAY_MILLIS);
					}
				}
				break;

			case R.id.delay_snapshot: // 延迟拍照;
				if (LowStorageChecker.check(this, this)) {
					if (delaySnapshotButton.isChecked()) {
						if (!isAutoSnapshoting && !isSnapshoting) {
							isSnapshoting = true;
							cancelAutoSnapshot();
							
							tempSnapshotButton = delaySnapshotButton;
							
							Message message2 = new Message();
							message2.what = MESSAGE_TAKE_PICTURE;
							
							if (mediaRecorder.getCameraMode() == CamaraMode.DEF_PHOTO_COMMON) {
								handler.sendEmptyMessageDelayed(MESSAGE_TURN_ON_FLASHLIGHT_TORCH, 5000 - TURN_ON_FLASHLIGHT_DELAY_MILLIS);
								handler.sendMessageDelayed(message2, 5000);
								
							} else if (mediaRecorder.getCameraMode() == CamaraMode.DEF_PHOTO_HEIGHT) {
								handler.sendEmptyMessage(MESSAGE_TURN_ON_FLASHLIGHT_ON);
								handler.sendMessageDelayed(message2, 5000);
							}
							currentDelaySeconds = 1;
							startAnimation();
							
							CmmobiClickAgentWrapper.onEvent(this, CDR_KEY_SHP_COUT);
						} else {
							delaySnapshotButton.setChecked(false);
						}
					} else {
						cancelDelaySnapshot(true);
					}
				}
				break;
				
			case R.id.record:
				if (LowStorageChecker.check(this, this)) {
					if (modeSwitch.isChecked()) {
						if (!isSnapshoting && !isAutoSnapshoting && needAutoTakeTimes == 0) {
							isSnapshoting = true;
							isAnimationStop = false;
							isPictureSaved = false;
							
							cancelDelaySnapshot(false);
							
							tempSnapshotButton = recordButton;
							tempSnapshotButton.setEnabled(false);
							
							Message message = new Message();
							message.what = MESSAGE_TAKE_PICTURE;
							
							if (mediaRecorder.getCameraMode() == CamaraMode.DEF_PHOTO_COMMON) {
								handler.sendEmptyMessageDelayed(MESSAGE_TURN_ON_FLASHLIGHT_TORCH, 0);
								handler.sendMessageDelayed(message, TURN_ON_FLASHLIGHT_DELAY_MILLIS);
								
							} else if (mediaRecorder.getCameraMode() == CamaraMode.DEF_PHOTO_HEIGHT) {
								handler.sendEmptyMessageDelayed(MESSAGE_TURN_ON_FLASHLIGHT_ON, 0);
								handler.sendMessageDelayed(message, TURN_ON_FLASHLIGHT_DELAY_MILLIS);
							}
						}
					} else {
						switchRecordState();
					}
				}
				break;
				
			case R.id.latest_media:
				if (!isRecording) {
					isEnterLastDiary = true;
					enterLastDiary();
				}
				break;
				
			case R.id.seek_bar_zoom_in:
				int step = mediaRecorder.getCameraMaxZoom() / 10;
				int zoomValue = mediaRecorder.getCameraZoom() + step;
				if (zoomValue > mediaRecorder.getCameraMaxZoom()) {
					zoomValue = mediaRecorder.getCameraMaxZoom();
				}
				mediaRecorder.setCameraZoom(zoomValue);
				
				int zoomProgress = (int) ((float) mediaRecorder.getCameraZoom() / (float) mediaRecorder.getCameraMaxZoom() * 100);
				seekBar.setProgress(zoomProgress);
				break;
				
			case R.id.seek_bar_zoom_out:
				int step2 = mediaRecorder.getCameraMaxZoom() / 10;
				int zoomValue2 = mediaRecorder.getCameraZoom() - step2;
				if (zoomValue2 < 0) {
					zoomValue2 = 0;
				}
				mediaRecorder.setCameraZoom(zoomValue2);
				
				int zoomProgress2 = (int) ((float) mediaRecorder.getCameraZoom() / (float) mediaRecorder.getCameraMaxZoom() * 100);
				seekBar.setProgress(zoomProgress2);
				break;

			default:
				if (view.getId() >= 0 && view.getId() < effectBeans.size()) {
					switchEffect(view.getId());
				}
				break;
			}
			
			handler.postDelayed(new Runnable() {
				
				@Override
				public void run() {
					if (tempSnapshotButton != null) {
						tempSnapshotButton.setEnabled(true);
					}
				}
			}, 2000);
		}
	}
	
	private void cancelDelaySnapshot(boolean ignoreCheckState) {
		if (delaySnapshotButton.isChecked() || ignoreCheckState) {
			isSnapshoting = false;
			
			handler.removeMessages(MESSAGE_TAKE_PICTURE);
			handler.post(new Runnable() {
				
				@Override
				public void run() {
					delaySnapshotButton.setChecked(false);
				}
			});
		}
	}
	
	private void cancelAutoSnapshot() {
		if (needAutoTakeTimes > 0) {
			needAutoTakeTimes = 0;
			isSnapshoting = false;
			handler.removeMessages(MESSAGE_TAKE_PICTURE);
		}
	}
	
	private synchronized void enterLastDiary() {
		int diarySize = diarys.size();
		boolean isAllCreated = true;
		
		if (diarySize == 0) {
			finish();
			
		} else {
			for (int i = 0;i < diarySize;i++) {
				TempDiaryWrapper diaryWrapper = diarys.get(i);
				if (!diaryWrapper.diaryCreatedOK) {
					isAllCreated = false;
					ZDialog.show(R.layout.progressdialog, false, true, this);
					break;
				}
			}
			if (isAllCreated) {
				ZDialog.dismiss();
				if (diaryList.size() > 0) {
					DiaryManager.getInstance().setDetailDiaryList(diaryList);
					MyDiary myDiary = diaryList.get(0);
					String diaryID = myDiary.diaryid;
					Intent intent = new Intent();
					intent.setClass(this, DiaryDetailActivity.class);
					intent.putExtra(DiaryDetailActivity.INTENT_ACTION_DIARY_ID, diaryID);
					startActivity(intent);
					isEnterLastDiary = false;
					finish();
				}
			}
		}
	}
	
	private void switchEffect(final int index) {
		lastEffectId = index;
		
		Message message = new Message();
		message.what = MESSAGE_SWITCH_EFFECTS;
		message.arg1 = index;
		handler.sendMessage(message);
		
		if (!isMosaicInitialized && index == Config.MOSAIC_INDEX) {
			handler.post(new Runnable() {
				
				@Override
				public void run() {
					Message message = new Message();
					message.what = MESSAGE_SWITCH_EFFECTS;
					message.arg1 = index;
					handler.sendMessage(message);
				}
			});
		}
		
		CmmobiClickAgentWrapper.onEvent(this, CDR_KEY_SHP_EFF_CHOO, String.valueOf(index));
	}
	
	private void hideCameraControlButtons() {
		camreaControlButtons.setVisibility(View.INVISIBLE);
		leftTopButton.setBackgroundResource(R.drawable.btn_activity_video_shoot_camera_control_button);
	}

	private void switchRecordState() {
		
		if (!(Boolean) recordButton.getTag()) {
			
			delaySnapshotButton.clearAnimation();
			delaySnapshotButton.setVisibility(View.GONE);
			modeSwitch.setVisibility(View.GONE);
			leftTopButton.setVisibility(View.INVISIBLE);
			middleTopButton.setVisibility(View.INVISIBLE);
			rightTopButton.setVisibility(View.INVISIBLE);
			shootState.setVisibility(View.VISIBLE);
			isWifiShooting = ZNetworkStateDetector.isWifi();
			if (isWifiShooting) {
				shootState.setBackgroundResource(R.drawable.video_shooting_wifi);
			} else {
				shootState.setBackgroundResource(R.drawable.video_shooting);
			}

			recordButton.setTag(true);
			recordButton.setEnabled(false);
			recordButton.setBackgroundResource(R.drawable.recording_animation_drawable);
			AnimationDrawable animationDrawable = (AnimationDrawable) recordButton.getBackground();
			animationDrawable.start();

			// 下面代码的先后顺序不要变;
			if (!isHighDefinition) {
//				MediaActionSoundWrapper.startVideoRecording();
			}
			hideCameraControlButtons();
			handler.sendEmptyMessage(MESSAGE_TURN_ON_FLASHLIGHT_TORCH);
			
			TempDiaryWrapper wrapper = requestUploadVideoDiary();
			
			mediaRecorder.start(wrapper.mediaName, wrapper.mediaPath);
			isRecording = true;
			startTimer();
			if (mediaRecorder.isSupportTakePicuture()) {
				takeSnapshotButton.setVisibility(View.VISIBLE);
			} else {
				takeSnapshotButton.setVisibility(View.INVISIBLE);
			}
			
			if (mediaRecorder.isSupportPauseAndResume()) {
				pauseVideoShoot.setBackgroundResource(R.drawable.pause_button_selector);
				pauseVideoShoot.setVisibility(View.VISIBLE);
				pauseVideoShoot.requestLayout();
			}
			
			if (mediaRecorder.isSupportPauseAndResume() && mediaRecorder.isSupportTakePicuture()) {
				bottomButtons.setBackgroundResource(R.drawable.video_shoot_bottom_background_2);
			}
			
			handler.postDelayed(new Runnable() {
				
				@Override
				public void run() {
					recordButton.setEnabled(true);
				}
			}, Config.VIDEO_SHOOT_BUTTON_BLOCKING_MILLIS);
			
			videoShootStartMillis = TimeHelper.getInstance().now();
			
		} else {
			
			if (mediaRecorder.isSupportPauseAndResume()) {
				pauseVideoShoot.setVisibility(View.GONE);
				pauseVideoShoot.requestLayout();
				bottomButtons.setBackgroundResource(R.drawable.video_shoot_bottom_background);
				if (mediaRecorder.isPause()) {
					mediaRecorder.resume();
					pauseVideoShoot.setChecked(false);
				}
			}
			modeSwitch.setVisibility(View.VISIBLE);
			leftTopButton.setVisibility(View.VISIBLE);
			rightTopButton.setVisibility(View.VISIBLE);
			shootState.setVisibility(View.INVISIBLE);
			
			// 下面代码的先后顺序不要变;
			if (!isHighDefinition) {
//				MediaActionSoundWrapper.stopVideoRecording();
			}
			stopTimer();
			recordButton.setTag(false);
			recordButton.setEnabled(false);
			recordButton.setBackgroundResource(R.drawable.record_button_selector);
			mediaRecorder.stop();
			isRecording = false;
			handler.sendEmptyMessage(MESSAGE_TURN_OFF_FLASHLIGHT);

			if (mediaRecorder.isSupportTakePicuture()) {
				takeSnapshotButton.setVisibility(View.VISIBLE);
			} else {
				takeSnapshotButton.setVisibility(View.INVISIBLE);
			}
			
			if (mediaRecorder.isSupportFlashMode(FlashMode.FLASH_MODE_TORCH) || mediaRecorder.isSupportFlashMode(FlashMode.FLASH_MODE_ON)) {
				middleTopButton.setVisibility(View.VISIBLE);
			}
			
			handler.postDelayed(new Runnable() {
				
				@Override
				public void run() {
					recordButton.setEnabled(true);
				}
			}, Config.VIDEO_SHOOT_BUTTON_BLOCKING_MILLIS);
			
			CmmobiClickAgentWrapper.onEventDuration(this, CDR_KEY_SHP_SH_BE, TimeHelper.getInstance().now() - videoShootStartMillis);
		}
	}
	
//	private Bitmap scaleThumbnail(Bitmap bitmap) {
//		if (bitmap != null) {
//
//			Model originalModel = new Model();
//			originalModel.put(Model.W, bitmap.getWidth());
//			originalModel.put(Model.H, bitmap.getHeight());
//
//			Model standardModel = new Model();
//			standardModel.put(Model.W, 384);
//			standardModel.put(Model.H, 512);
//
//			ZUniformScaler.scale(originalModel, standardModel, ScaleType.OUT);
//			bitmap = ZGraphics.resize(bitmap, (float) originalModel.get(Model.W), (float) originalModel.get(Model.H), true);
//		}
//		
//		return bitmap;
//	}

	private synchronized TempDiaryWrapper requestUploadVideoDiary() {
		videoDiaryUUID = UUID.randomUUID().toString().replace("-", "");

		TempDiaryWrapper wrapper = new TempDiaryWrapper(TempDiaryWrapper.TYPE_VIDEO);
		wrapper.mediaName = MD5.encode((uid + videoDiaryUUID).getBytes());
		wrapper.mediaPath = Environment.getExternalStorageDirectory() + Constant.SD_STORAGE_ROOT + "/" + uid + "/video";
		wrapper.createTimeMillis = TimeHelper.getInstance().now();
		
		String longitude = "";
		String latitude = "";
		Attachs[] attachs = new Attachs[1];
		attachs[0] = new Attachs();
		attachs[0].attachid = "";
		attachs[0].attachuuid = UUID.randomUUID().toString().replace("-", "");
		attachs[0].attach_type = "1"; // 1视频 2音频 3图片 4文字;
		attachs[0].level = "1"; // 1主内容 0辅内容;
		attachs[0].video_type = isHighDefinition ? "0" : "1"; // 0高清 1普清;
		if (CommonInfo.getInstance().myLoc != null) {
			attachs[0].attach_latitude = String.valueOf(CommonInfo.getInstance().myLoc.latitude);
			attachs[0].attach_logitude = String.valueOf(CommonInfo.getInstance().myLoc.longitude);
			longitude = String.valueOf(CommonInfo.getInstance().myLoc.longitude);
			latitude = String.valueOf(CommonInfo.getInstance().myLoc.latitude);
		}
		attachs[0].content = "";
		attachs[0].suffix = ".mp4";
		attachs[0].Operate_type = "1"; // 操作类型: 1增加 2更新 3删除;

		
		String addressCode = "";
		AccountInfo account = AccountInfo.getInstance(uid);
		MyWeather weather = account.myWeather;
		if (weather != null) {
			addressCode = weather.addrCode;
		}
		GsonRequest2.createStructureRequest request = Requester2.createStructure(
				handler, 
				"", 
				videoDiaryUUID, 
				"1", 
				"", 
				"",
				longitude,latitude,
				"", 
				CommonInfo.getInstance().positionStr,longitude,latitude, 
				String.valueOf(TimeHelper.getInstance().now()),
				addressCode,
				attachs);

		wrapper.request = request;
		diaryWrappers.put(videoDiaryUUID, wrapper);
		diarys.add(0, wrapper);
		
		return wrapper;
	}

	private synchronized TempDiaryWrapper requestUploadPictursDiary() {
		picturesDiaryUUID = UUID.randomUUID().toString().replace("-", "");

		TempDiaryWrapper wrapper = new TempDiaryWrapper(TempDiaryWrapper.TYPE_PICTURE);
		wrapper.attachUUID = picturesDiaryUUID;
		wrapper.mediaName = MD5.encode((uid + picturesDiaryUUID).getBytes()) + ".jpg";
		wrapper.mediaPath = Environment.getExternalStorageDirectory() + Constant.SD_STORAGE_ROOT + "/" + uid + "/pic/" + wrapper.mediaName;
		wrapper.createTimeMillis = TimeHelper.getInstance().now();
		
		String longitude = "";
		String latitude = "";
		Attachs[] attachs = new Attachs[1];
		attachs[0] = new Attachs();
		attachs[0].attachid = "";
		attachs[0].attachuuid = UUID.randomUUID().toString().replace("-", "");
		attachs[0].attach_type = "3"; // 1视频 2音频 3图片 4文字;
		attachs[0].level = "1"; // 1主内容 0辅内容;
		attachs[0].photo_type = "0";
		if (CommonInfo.getInstance().myLoc != null) {
			attachs[0].attach_latitude = String.valueOf(CommonInfo.getInstance().myLoc.latitude);
			attachs[0].attach_logitude = String.valueOf(CommonInfo.getInstance().myLoc.longitude);
			
			longitude = String.valueOf(CommonInfo.getInstance().myLoc.longitude);
			latitude = String.valueOf(CommonInfo.getInstance().myLoc.latitude);
		}
		attachs[0].content = "";
		attachs[0].suffix = ".jpg";
		attachs[0].Operate_type = "1"; // 操作类型: 1增加 2更新 3删除;
		
		String addressCode = "";
		AccountInfo account = AccountInfo.getInstance(uid);
		MyWeather weather = account.myWeather;
		if (weather != null) {
			addressCode = weather.addrCode;
		}
		GsonRequest2.createStructureRequest request = Requester2.createStructure(
				handler, 
				"", 
				picturesDiaryUUID, 
				"1", 
				"", 
				"",
				longitude,latitude, 
				"", 
				CommonInfo.getInstance().positionStr,longitude,latitude, 
				String.valueOf(TimeHelper.getInstance().now()),
				addressCode,
				attachs);

		wrapper.request = request;
		diaryWrappers.put(picturesDiaryUUID, wrapper);
		diaryWrappers.put(wrapper.mediaPath, wrapper);
		diarys.add(0, wrapper);

		return wrapper;
	}

	/**
	 * 切换特效选择框的显示和隐藏;
	 */
	private void effectsButtonClicked() {
		if (isRecording && !PluginUtils.isPluginMounted()) {
			ZToast.showLong(R.string.cant_use_effect_when_recording);
			effectsButton.setChecked(false);
			
		} else if (!EffectsDownloadUtil.getInstance(this).checkEffects()) {
			if (!isHighDefinition) {
				if (effectsGroup.getVisibility() == View.VISIBLE) {
					effectsGroup.setVisibility(View.INVISIBLE);
					effectsButton.setChecked(false);
					if (!isFocusLocked) {
						seekBarShell.setVisibility(View.VISIBLE);
					}
					
					CmmobiClickAgentWrapper.onEventDuration(this, CDR_KEY_SHP_EFF, uid, TimeHelper.getInstance().now() - effectsGroupShowMillis);
				} else {
					effectsGroup.setVisibility(View.VISIBLE);
					effectsButton.setChecked(true);
//					seekBarShell.setVisibility(View.INVISIBLE); //保留 ;
//					hideCameraControlButtons(); // 保留;
					
					effectsGroupShowMillis = TimeHelper.getInstance().now();
				}
			} else {
				effectsGroup.setVisibility(View.INVISIBLE);
				effectsButton.setChecked(false);
			}
		} else {
			effectsButton.setChecked(false);
//			EffectLibDownloader downloader = new EffectLibDownloader(this, true);
//			downloader.askForDownloadEffectLib(effectsButton);
		}
	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		// TODO Auto-generated method stub
	}

	/**
	 * 加速度传感器回调函数;
	 */
	@Override
	public void onSensorChanged(SensorEvent event) {
		float x = event.values[SensorManager.DATA_X];
		float y = event.values[SensorManager.DATA_Y];

		if (isOrientationSwitching) {
			if (TimeHelper.getInstance().now() - orientationHoldingMillis > ORIENTATION_SWITCH_TIME) {
				isOrientationSwitching = false;
				orientationHoldingMillis = TimeHelper.getInstance().now();

				if (Math.abs(x) > Math.abs(y)) {
					if (x > 0) {
						switchOrientation(ORIENTATION_RIGHT);
					} else {
						switchOrientation(ORIENTATION_LEFT);
					}
				} else {
					if (y > 0) {
						switchOrientation(ORIENTATION_UP);
					} else {
						switchOrientation(ORIENTATION_DOWN);
					}
				}
			}

		} else if (Math.abs(Math.abs(x) - Math.abs(y)) > ORIENTATION_SWITCH_THRESHOLD
				&& TimeHelper.getInstance().now() - orientationHoldingMillis > ORIENTATION_SWITCH_TIME) {
			isOrientationSwitching = true;
			orientationHoldingMillis = TimeHelper.getInstance().now();
		}
	}

	/**
	 * 通过Animation来切换UI元素的显示方向;
	 * 
	 * @param orientation
	 *            : ORIENTATION_UP, ORIENTATION_RIGHT, ORIENTATION_DOWN,
	 *            ORIENTATION_LEFT;
	 */
	private void switchOrientation(int orientation) {
		int currentAngle = currentOrientation * 90;
		int targetAngle = orientation * 90;
		long rotateMillis = Math.abs(currentOrientation - orientation) * ORIENTATION_ROTATE_SPEED;

		if (currentOrientation == ORIENTATION_LEFT && orientation == ORIENTATION_UP) {
			currentAngle = -90;
			rotateMillis = ORIENTATION_ROTATE_SPEED;
		} else if (currentOrientation == ORIENTATION_UP && orientation == ORIENTATION_LEFT) {
			targetAngle = -90;
			rotateMillis = ORIENTATION_ROTATE_SPEED;
		}

		RotateAnimation animation = new RotateAnimation(currentAngle, targetAngle, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
		animation.setInterpolator(new AccelerateDecelerateInterpolator());
		animation.setDuration(rotateMillis);
		animation.setFillEnabled(true);
		animation.setFillAfter(true);
		animation.setFillBefore(true);
		
		leftButton1.startAnimation(animation);
		leftButton2.startAnimation(animation);
		leftButton3.startAnimation(animation);
		leftButton4.startAnimation(animation);
		leftButton5.startAnimation(animation);
		
		LayoutAnimationController layoutAnimationController = new LayoutAnimationController(animation);
		layoutAnimationController.setOrder(LayoutAnimationController.ORDER_NORMAL);
		layoutAnimationController.setDelay(0);

		effectsLayer.setLayoutAnimation(layoutAnimationController);
		effectsLayer.startLayoutAnimation();
		effectsLayer.postInvalidate();

		currentOrientation = orientation;
	}

	private void updateAlbum(Bitmap bitmap) {
		if (bitmap != null) {
			Model originalModel = new Model();
			originalModel.put(Model.W, bitmap.getWidth());
			originalModel.put(Model.H, bitmap.getHeight());
			
			Model standardModel = new Model();
			standardModel.put(Model.W, mask.getWidth());
			standardModel.put(Model.H, mask.getHeight());
			
			ZUniformScaler.scale(originalModel, standardModel, ScaleType.IN);
			
			Bitmap temp = ZGraphics.resize(
					bitmap, 
					(int) originalModel.get(Model.W), 
					(int) originalModel.get(Model.H), 
					true);
			
			Message message = new Message();
			message.what = MESSAGE_PHOTO_ALBUM_UPDATING_ANIMATION;
			message.obj = temp;
			handler.sendMessage(message);
		} else {
			ZToast.showShort("封面错误");
			ZLog.useOtherTag("Cover");
			ZLog.printStackTrace();
			ZLog.useDefaultTag();
		}
	}

	@Override
	public void onSurfaceCreated() {
		if(!mediaRecorder.isPreview()){
			mediaRecorder.startPreview(isFrontCamera ? CameraInfo.CAMERA_FACING_FRONT : CameraInfo.CAMERA_FACING_BACK);
			isSurfaceCreated = true;
		}
		
		// allow check FlashLight after start-preview;
		if (!mediaRecorder.isSupportFlashMode(FlashMode.FLASH_MODE_TORCH) && !mediaRecorder.isSupportFlashMode(FlashMode.FLASH_MODE_ON)) {
			middleTopButton.setVisibility(View.INVISIBLE);
		} else {
			middleTopButton.setVisibility(View.VISIBLE);
		}
	}

	@Override
	public void onStartRecorder(Object arg0, String path) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onStopRecorder(Object arg0, String path) {
		String temp = path.substring(path.lastIndexOf("/") + 1, path.length());
		temp = temp.replace(".mp4", "");
		
		TempDiaryWrapper wrapper = diaryWrappers.get(videoDiaryUUID);
		wrapper.mediaCatchOK = true;
		wrapper.mediaName = temp;

		createVideoDiary(wrapper);
		AccountInfo.getInstance(uid).persist();
	}
	
	@Override
	public void onSmallBoxComplete(Object arg0, String path) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void onPauseRecorder(Object r) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onResumeRecorder(Object r) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public synchronized void onPictureTakenComplete(String file) {
//		callbackCount ++;
//		
//		handler.post(new Runnable() {
//			
//			@Override
//			public void run() {
//				String string = "MessageCount : " + messageCount + "\n";
//				string += "CallbackCount : " + callbackCount;
//				onScreenLog.setText(string);
//			}
//		});
		
		int inSampleSize = 1;
		if (isHighDefinition) {
			inSampleSize = 3;
		}
		
		Options options = new Options();
		options.inSampleSize = inSampleSize;
		Bitmap bitmap = BitmapFactory.decodeFile(file, options);
		
		int degree = XUtils.getExifOrientation(file);
		bitmap = ZGraphics.rotate(bitmap, degree, true);
		
		TempDiaryWrapper wrapper = diaryWrappers.get(file);
		wrapper.mediaCatchOK = true;
		wrapper.thumbnail = tryToGetPictureThumbnail(bitmap);
		
		createPicturesDiary(wrapper);
		
		if (needAutoTakeTimes > 0) {
			
			long millis = CONTINUOUS_CAPTURE_GAP_MILLIS - (TimeHelper.getInstance().now() - lastCameraModeStartMillis);
			lastCameraModeStartMillis = TimeHelper.getInstance().now();
			if (millis < 0) {
				millis = 0;
			}
			
			needAutoTakeTimes --;
			Message message = new Message();
			message.what = MESSAGE_TAKE_PICTURE;
			
			if (mediaRecorder.getCameraMode() == CamaraMode.DEF_PHOTO_COMMON) {
				handler.sendEmptyMessageDelayed(MESSAGE_TURN_ON_FLASHLIGHT_TORCH, millis);
				handler.sendMessageDelayed(message, TURN_ON_FLASHLIGHT_DELAY_MILLIS + millis);
				
			} else if (mediaRecorder.getCameraMode() == CamaraMode.DEF_PHOTO_HEIGHT) {
				handler.sendEmptyMessageDelayed(MESSAGE_TURN_ON_FLASHLIGHT_ON, millis);
				handler.sendMessageDelayed(message, TURN_ON_FLASHLIGHT_DELAY_MILLIS + millis);
			}
		} else {
			isAutoSnapshoting = false;
		}
		
		isPictureSaved = true;
		reviveTakeSnapshotButton();
	}
	
	@Override
	public synchronized void onPictureTaken(Bitmap bitmap, String filePath) {
		Bitmap temp = null;
		
		if (bitmap != null) {
			temp = bitmap;
			
		} else if (filePath != null) {
			Options options = new Options();
			options.inSampleSize = 2;
			temp = BitmapFactory.decodeFile(filePath, options);
			temp = ZGraphics.rotate(temp, XUtils.getExifOrientation(filePath), true);
		}
		
		if (!isRecording) {
			handler.sendEmptyMessage(MESSAGE_TURN_OFF_FLASHLIGHT);
		}
		
		updateAlbum(temp);
	}
	
	private void reviveTakeSnapshotButton() {
		if (isPictureSaved && isAnimationStop) {
			handler.post(new Runnable() {
				
				@Override
				public void run() {
					tempSnapshotButton.setEnabled(true);
					if (tempSnapshotButton == delaySnapshotButton) {
						delaySnapshotButton.setChecked(false);
					}
				}
			});
		}
	}
	
	private void startAnimation() {
		Animation animation = AnimationUtils.loadAnimation(this, R.anim.anim_delay_shoot);
		animation.setAnimationListener(new AnimationListener() {
			
			@Override
			public void onAnimationStart(Animation animation) {
			}
			
			@Override
			public void onAnimationRepeat(Animation animation) {
			}
			
			@Override
			public void onAnimationEnd(Animation animation) {
				currentDelaySeconds ++;
				if (delaySnapshotButton.isChecked() && currentDelaySeconds <= 5) {
					startAnimation();
				} else {
					delayShootNumber.setVisibility(View.INVISIBLE);
				}
			}
		});
		
		switch (currentDelaySeconds) {
		case 1:
			delayShootNumber.setImageResource(R.drawable.delay_number_5);
			break;
			
		case 2:
			delayShootNumber.setImageResource(R.drawable.delay_number_4);
			break;
			
		case 3:
			delayShootNumber.setImageResource(R.drawable.delay_number_3);
			break;
			
		case 4:
			delayShootNumber.setImageResource(R.drawable.delay_number_2);
			break;
			
		case 5:
			delayShootNumber.setImageResource(R.drawable.delay_number_1);
			break;
		}
		
		delayShootNumber.setVisibility(View.VISIBLE);
		delayShootNumber.startAnimation(animation);
	}

	@Override
	public boolean onLongClick(View v) {
		if (needAutoTakeTimes == 0 && !isRecording && mediaRecorder.getCameraMode() != CamaraMode.DEF_VIDEO_COMMON && mediaRecorder.getCameraMode() != CamaraMode.DEF_VIDEO_HEIGHT) {
			cancelDelaySnapshot(false);
			
			tempSnapshotButton = recordButton;
			
			Message message = new Message();
			message.what = MESSAGE_TAKE_PICTURE;
			
			if (mediaRecorder.getCameraMode() == CamaraMode.DEF_PHOTO_COMMON) {
				handler.sendEmptyMessageDelayed(MESSAGE_TURN_ON_FLASHLIGHT_TORCH, 0);
				handler.sendMessageDelayed(message, TURN_ON_FLASHLIGHT_DELAY_MILLIS);
				
			} else if (mediaRecorder.getCameraMode() == CamaraMode.DEF_PHOTO_HEIGHT) {
				handler.sendEmptyMessageDelayed(MESSAGE_TURN_ON_FLASHLIGHT_ON, 0);
				handler.sendMessageDelayed(message, TURN_ON_FLASHLIGHT_DELAY_MILLIS);
			}
			needAutoTakeTimes = 4;
			isAutoSnapshoting = true;
		}
		return false;
	}

	@Override
	public void onProgressChanged(VerticalSeekBar2 seekBar, int progress, boolean fromUser) {
		if (fromUser) {
			int zoomValue = mediaRecorder.getCameraMaxZoom() * progress / 100;
			mediaRecorder.setCameraZoom(zoomValue);
		}
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
		ZToast.showLong(R.string.camera_error);
	}

	@Override
	public void onSizeChanged(int left, int top, int right, int bottom) {
//		mask.setLeft(left);
//		mask.setTop(top);
//		mask.setRight(right);
//		mask.setBottom(bottom);
//		
//		ZLog.e("preview -> left = " + left + " right = " + right + "top = " + top + "bottom = " + bottom);
//		ZLog.e("mask -> left = " + mask.getLeft() + " right = " + mask.getRight() + "top = " + mask.getTop() + "bottom = " + mask.getBottom());
	}

	
}
