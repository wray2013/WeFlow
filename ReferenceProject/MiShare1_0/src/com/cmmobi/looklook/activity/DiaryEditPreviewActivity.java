package com.cmmobi.looklook.activity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnErrorListener;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.NetworkInfo.State;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.LocalBroadcastManager;
import android.telephony.TelephonyManager;
import android.text.Html;
import android.text.Html.ImageGetter;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.FrameLayout.LayoutParams;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.ToggleButton;
import cn.zipper.framwork.core.ZActivity;
import cn.zipper.framwork.core.ZApplication;
import cn.zipper.framwork.core.ZDialog;
import cn.zipper.framwork.core.ZLog;
import cn.zipper.framwork.core.ZViewFinder;
import cn.zipper.framwork.device.ZSimCardInfo;
import cn.zipper.framwork.io.file.ZFileSystem;
import cn.zipper.framwork.utils.ZStringUtils;

import com.cmmobi.looklook.MainApplication;
import com.cmmobi.looklook.R;
import com.cmmobi.looklook.common.gson.DiaryController;
import com.cmmobi.looklook.common.gson.DiaryController.DiaryWrapper;
import com.cmmobi.looklook.common.gson.DiaryController.FileOperate;
import com.cmmobi.looklook.common.gson.GsonProtocol;
import com.cmmobi.looklook.common.gson.GsonRequest3.Attachs;
import com.cmmobi.looklook.common.gson.GsonResponse3.MyBind;
import com.cmmobi.looklook.common.gson.GsonResponse3.MyDiary;
import com.cmmobi.looklook.common.gson.GsonResponse3.MyDiaryList;
import com.cmmobi.looklook.common.gson.GsonResponse3.TAG;
import com.cmmobi.looklook.common.gson.Requester3;
import com.cmmobi.looklook.common.gson.WeiboRequester;
import com.cmmobi.looklook.common.utils.CmmobiClickAgentWrapper;
import com.cmmobi.looklook.common.utils.DateUtils;
import com.cmmobi.looklook.common.utils.DiaryEditNote;
import com.cmmobi.looklook.common.view.MultiPointTouchImageView;
import com.cmmobi.looklook.common.view.Xdialog;
import com.cmmobi.looklook.common.web.TackView;
import com.cmmobi.looklook.dialog.ShareDialog;
import com.cmmobi.looklook.info.location.POIAddressInfo;
import com.cmmobi.looklook.info.profile.AccountInfo;
import com.cmmobi.looklook.info.profile.ActiveAccount;
import com.cmmobi.looklook.info.profile.CommonInfo;
import com.cmmobi.looklook.info.profile.DiaryManager;
import com.cmmobi.looklook.info.profile.LoginSettingManager;
import com.cmmobi.looklook.info.profile.MediaValue;
import com.cmmobi.looklook.offlinetask.OfflineTaskManager;
import com.cmmobi.looklook.prompt.Prompt;
import com.cmmobi.statistics.CmmobiClickAgent;
import com.cmmobivideo.utils.PluginUtils;
import com.cmmobivideo.workers.XMediaPlayer;
import com.cmmobivideo.workers.XMediaPlayer.OnInfoListener;
import com.google.gson.Gson;
import com.iflytek.msc.ExtAudioRecorder;
import com.iflytek.msc.QISR_TASK;
import com.nostra13.universalimageloader.api.AnimateFirstDisplayListener;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageLoadingListener;
import com.nostra13.universalimageloader.core.display.SimpleBitmapDisplayer;

import effect.XEffectMediaPlayer;
import effect.XEffects;

/**
 * Copyright (C) ......
 * All rights reserved.
 * @package com.cmmobi.looklook.activity
 * @filename DiaryPreviewActivity.java
 * @summary 预览页
 * @author Lanhai
 * @date 2013-12-18
 * @version 1.0
 */
public class DiaryEditPreviewActivity extends ZActivity implements OnLongClickListener, OnCheckedChangeListener
{

	private static final String TAG = "DiaryEditPreviewActivity";

	public static final String INTENT_ACTION_DIARY_ID = "intent_action_diary_id";
	public static final String INTENT_ACTION_DIARY_UUID = "intent_action_diary_uuid";
	public static final String INTENT_ACTION_DIARY_STRING = "intent_action_diary_string";
	public static final String INTENT_ACTION_ATTACH_UUID = "intent_action_attach_uuid";
	public static final String INTENT_ACTION_SHARE_TYPE = "intent_action_diary_string";
	public static final String INTENT_ACTION_SHOW = "intent_action_show";
	public static final String INTENT_ACTION_HIDDEN = "intent_action_hidden";
	public static final String INTENT_ACTION_SCREEN_MODE = "intent_action_screen_mode";
	public static final String INTENT_ACTION_SHARE_SUCCESS = "intent_action_share_success";
	public static final String INTENT_ACTION_DELETE_SNS = "intent_action_delete_sns";
	public static final String INTENT_ACTION_PRAISE_CHANGE = "intent_action_praise_change";
	public static final String INTENT_ACTION_IS_FROM_SHORT_SHOOT_ACTVITY = "intent_action_is_from_short_shoot_activity";
	public static final String INTENT_ACTION_IS_FROM_LONG_SHOOT_ACTVITY = "intent_action_is_from_long_shoot_activity";
	public static final String INTENT_ACTION_MEDIA_PATH = "intent_action_media_path";
	public static final String INTENT_ACTION_MEDIA_EFFECT = "intent_action_video_effect";
	public static final String INTENT_ACTION_SOUNDTRACK = "intent_action_video_soundtrack";
	public static final String INTENT_ACTION_SOUND_PERCENT = "intent_action_sound_percent";
	public static final String INTENT_ACTION_EDIT_NOTELIST = "intent_action_edit_notelist";
	public static final String DIARY_EDIT_DONE = "DIARY.EDIT.DONG";
	
	public static final int HANDLER_UPDATE_VIDEO_PLAYER_PROCESS = 0x0010;
	public static final int HANDLER_UPDATE_VIDEO_PLAYER_COMPLETE = 0x0011;
	public static final int HANDLER_UPDATE_AUDIO_PLAYER_PROCESS = 0x0012;
	public static final int HANDLER_UPDATE_AUDIO_PLAYER_COMPLETE = 0x0013;
	public static final int HANDLER_UPDATE_TACK_PLAYER_PROCESS = 0x0014;
//	public static final int HANDLER_PLAY_VIDEO = 0x0012;
//	public static final int HANDLER_PLAY_AUDIO = 0x0013;
//	public static final int HANDLER_AUTO_HIDE_CONTROLLER = 0x0015;
	public static final int HANDLER_UPDATE_VIDEO_ERROR = 0x0016;
	public static final int HANDLER_UPDATE_VIDEO_PREPARED = 0x0017;
	public static final int HANDLER_SHORT_RECORD_STOP_TIME_DELAY = 0x87654017;
	
	public static final String INTENT_EXTRA_DIARY_FILTER = "intent_extra_diary_filter";
	
//	public static final String INTENT_EXTRA_PLAY_URL = "intent_extra_play_url";
//	public static final String INTENT_EXTRA_PLAY_TOTALTIME = "intent_extra_play_totaltime";
//	public static final String INTENT_EXTRA_PLAY_CURTIME = "intent_extra_play_curtime";
//	public static final String INTENT_EXTRA_PLAY_THUMB = "intent_extra_play_thumb";
	public static final String INTENT_EXTRA_DELETE_SNS = "intent_extra_delete_sns";
	public static final String INTENT_ATTACH_COVER_CHANGED = "intent_attach_cover_changed";
	
	private static final int REQUESTCODE_DETAIL = 0x05;
	private static final int REQUESTCODE_SNSDELETE = 0x06;
	
	private final int SETTING_PERSONAL_INFO = 0x0039;
	
	private final int FRENPOSITON = 0x0037;
	
	private LinearLayout mDiaryLayout;
	private LinearLayout mMenu;
	
	private LayoutInflater inflater;
	private MyDiary myDiary = new MyDiary();
	private MyDiary originalDiary = new MyDiary();
	private final int REQUEST_TAG = 0x0013;
	private final int REQUEST_POSITION = 0x0014;
	private final int REQUEST_VIDEO = 0x0015;
	private final int REQUEST_VIDEO_EFFECT = 0x0016;
	private final int REQUEST_VIDEO_SOUNDTRACK = 0x0017;
	private final int REQUEST_AUDIO = 0x0018;
	private final int REQUEST_AUDIO_SOUNDTRACK = 0x0019;
	private final int REQUEST_PHOTO = 0x0020;
	private final int REQUEST_PHOTO_EFFECT = 0x0021;
	private final int REQUEST_NOTE = 0x0022;
	private final int REQUEST_OTHER = 0x0023;
	
	private DiaryEditNote diaryEditNote = new DiaryEditNote();
	private static HashSet<String> cachePathSet = new HashSet<String>();
	private static HashSet<String> cacheVideoCoverSet = new HashSet<String>();
	
	private String newDiaryUUID;
	
	public enum EPlayStatus
	{
		NON,
		OPENING,
		OPENED,
		PLAY,
		PAUSE;
		
		double seekPosition = 0d;
	}
	
	// 播放时间
	private static class PlayTime
	{
		int total;
		int current;
	}
	
	private Button mBtnDone = null;
	
	private EPlayStatus ePlayStatus = EPlayStatus.NON;
	
	//播放时间等控件
	private XMediaPlayer mMediaPlayer = null;
	private ImageView mBtnVideoPlay;
	private ImageView mIvVideoThumbnail;
	private TackView tvMainTack;
	private RelativeLayout mVideoContent;
	
	private ImageView mIvLeftWheel = null;
	private ImageView mIvRightWheel = null;
	private TextView mTVVideoTime = null;
	private TextView mTVAudioTime = null;
	private ImageView mBtnAudioPlay = null;
	
	private View vMyTitlebar;
	private View vOtherTitlebar;
	private View vDiaryInfoTile;//上面天气、位置、赞等信息layout
	
	private ImageView mBtnPraise;
	
	private TextView[] tvTags=new TextView[3];
	private View tagsLayout;
	private ImageView vPin;
	private TextView tvPosition;//位置信息
	private ToggleButton tgbShowGps;
	private LinearLayout mTlParam;
	private TextView tvTagsNum;
	
	private MultiPointTouchImageView mIvPicture;
	
	//播放进度
	private SeekBar mPlayProcess;
	
	// 视频长度
	private double mTotlaTime = 0d;
	
	
	MyBind myBind;
	
	// 文字框效果输入
	private static HashMap<String, Integer> EXPHM = new HashMap<String, Integer>();
	private static final Integer[] icons1 = { R.drawable.bq_ai,
			R.drawable.bq_baibai, R.drawable.bq_beishang, R.drawable.bq_bishi,
			R.drawable.bq_bizui, R.drawable.bq_buyao, R.drawable.bq_changzui,
			R.drawable.bq_chijing, R.drawable.bq_daihaqian,
			R.drawable.bq_dangao, R.drawable.bq_good, R.drawable.bq_guzhang,
			R.drawable.bq_haha, R.drawable.bq_haixiao, R.drawable.bq_han,
			R.drawable.bq_hehe, R.drawable.bq_heixiang, R.drawable.bq_huaxin,
			R.drawable.bq_jiyang, R.drawable.bq_keai, };
	private static final Integer[] icons2 = { R.drawable.bq_keling,
			R.drawable.bq_ku, R.drawable.bq_kun, R.drawable.bq_lai,
			R.drawable.bq_landelini, R.drawable.bq_lazhu, R.drawable.bq_lei,
			R.drawable.bq_liwu, R.drawable.bq_lu, R.drawable.bq_nvma,
			R.drawable.bq_ok, R.drawable.bq_paopao, R.drawable.bq_qinqin,
			R.drawable.bq_ruo, R.drawable.bq_shangxin, R.drawable.bq_shiwang,
			R.drawable.bq_shuijiao, R.drawable.bq_taikaixing,
			R.drawable.bq_touxiao, R.drawable.bq_tu, };
	private static final Integer[] icons3 = { R.drawable.bq_wapishi,
			R.drawable.bq_weiqu, R.drawable.bq_xin, R.drawable.bq_xu,
			R.drawable.bq_ye, R.drawable.bq_yinxian, R.drawable.bq_yiwen,
			R.drawable.bq_youhenhen, R.drawable.bq_yun,
			R.drawable.bq_zhuaikuang, R.drawable.bq_zhutou,
			R.drawable.bq_zuohenhen, };
	
	private static final String[] expTextTab1 = { "[衰]", "[拜拜]", "[可怜]",
			"[鄙视]", "[闭嘴]", "[不要]", "[馋嘴]", "[吃惊]", "[打哈气]", "[蛋糕]", "[good]",
			"[鼓掌]", "[哈哈]", "[害羞]", "[汗]", "[呵呵]", "[黑线]", "[花心]", "[鬼脸]",
			"[可爱]", };
	
	private static final String[] expTextTab2 = { "[悲伤]", "[酷]", "[懒得理你]",
			"[来]", "[思考]", "[蜡烛]", "[泪]", "[礼物]", "[怒]", "[怒骂]", "[ok]",
			"[太开心]", "[亲亲]", "[弱]", "[伤心]", "[失望]", "[睡觉]", "[爱你]", "[偷笑]",
			"[生病]", };
	private static final String[] expTextTab3 = { "[挖鼻屎]", "[委屈]", "[心]",
			"[嘘]", "[耶]", "[嘻嘻]", "[疑问]", "[右哼哼]", "[晕]", "[抓狂]", "[猪头]",
			"[左哼哼]", };
	
	static {
		for (int i = 0; i < 20; i++) {
			EXPHM.put(expTextTab1[i], icons1[i]);
		}
		for (int i = 0; i < 20; i++) {
			EXPHM.put(expTextTab2[i], icons2[i]);
		}
		for (int i = 0; i < 12; i++) {
			EXPHM.put(expTextTab3[i], icons3[i]);
		}
	}
	
	private int[] SOUND_ICONS_SMALL = {R.drawable.yuyinbiaoqian_3,R.drawable.yuyinbiaoqian_4,R.drawable.yuyinbiaoqian_2};
	private int[] SOUND_ICONS_BIG = {R.drawable.yuyinbofang_da_2,R.drawable.yuyinbofang_da_3,R.drawable.yuyinbofang_da_1};
	
	private boolean isFromShortShootActivity;
	private boolean isFromLongShootActivity;
	private boolean isToShootActivity;
	private boolean isShowShareDialog;
	public static boolean isFromShootting = false;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.d(TAG, "onCreate");
//		Bundle bundle = getIntent().getExtras();
//		eFilterType = (FilterType) bundle.get(INTENT_EXTRA_DIARY_FILTER);
		
		setContentView(R.layout.activity_diaryeditpreview);
		
		SharedPreferences sp = getSharedPreferences(GuideActivity.SP_NAME, MODE_PRIVATE);
		if (sp.getInt(GuideActivity.SP_KEY, -1) == 1)
		{
			Intent intent = new Intent(DiaryEditPreviewActivity.this, GuideActivity.class);
			startActivity(intent);
		}

		mDiaryLayout = (LinearLayout) findViewById(R.id.ll_diaryeditpreview);
		
		isFromShortShootActivity = getIntent().getBooleanExtra(INTENT_ACTION_IS_FROM_SHORT_SHOOT_ACTVITY, false);
		isFromLongShootActivity = getIntent().getBooleanExtra(INTENT_ACTION_IS_FROM_LONG_SHOOT_ACTVITY, false);
		
		if (isFromLongShootActivity || isFromShortShootActivity) {
			isFromShootting = true;
		} else {
			isFromShootting = false;
		}
		ImageButton back = (ImageButton) findViewById(R.id.ib_title_back);
		ImageButton shoot = (ImageButton) findViewById(R.id.ib_title_shoot);
		ImageButton home = (ImageButton) findViewById(R.id.ib_title_home);
		ImageButton share = (ImageButton) findViewById(R.id.ib_title_share);
		findViewById(R.id.ib_title_back).setOnLongClickListener(this);
		mBtnDone = (Button) findViewById(R.id.btn_title_done);
		
		if (isFromShortShootActivity) {
			back.setVisibility(View.GONE);
			shoot.setVisibility(View.GONE);
			home.setVisibility(View.VISIBLE);
			share.setVisibility(View.VISIBLE);
			mBtnDone.setVisibility(View.GONE);
			
			shoot.setOnClickListener(this);
			home.setOnClickListener(this);
			share.setOnClickListener(this);
			
		} else if (isFromLongShootActivity) {
			back.setVisibility(View.GONE);
			shoot.setVisibility(View.GONE);
			home.setVisibility(View.VISIBLE);
			share.setVisibility(View.VISIBLE);
			mBtnDone.setVisibility(View.GONE);
			
			shoot.setOnClickListener(this);
			home.setOnClickListener(this);
			share.setOnClickListener(this);
			
		} else {
			back.setVisibility(View.VISIBLE);
			shoot.setVisibility(View.GONE);
			home.setVisibility(View.GONE);
			share.setVisibility(View.GONE);
			mBtnDone.setVisibility(View.VISIBLE);
			
			back.setOnClickListener(this);
			mBtnDone.setOnClickListener(this);
			mBtnDone.setEnabled(false);
		}
		
		inflater = LayoutInflater.from(this);
		
		String diary = getIntent().getStringExtra(INTENT_ACTION_DIARY_STRING);
		myDiary = new Gson().fromJson(diary, MyDiary.class);
		originalDiary = new Gson().fromJson(diary, MyDiary.class);
		diaryEditNote.mediaPath = originalDiary.getMainPath();
		
		initUI();
		
		setMenu();
		
		if (tvTagsNum != null) {
			int tagSize = myDiary.getTagSize();
			if (tagSize > 0) {
				tvTagsNum.setVisibility(View.VISIBLE);
				tvTagsNum.setText(String.valueOf(tagSize));
			} else {
				tvTagsNum.setVisibility(View.GONE);
			}
		}
		
		//注册日记更新广播
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(DiaryPreviewActivity.DIARY_EDIT_MOD);
		intentFilter.addAction(DiaryPreviewActivity.DIARY_EDIT_NEW);
		intentFilter.addAction(DiaryPreviewActivity.DIARY_EDIT_REFRESH);
		LocalBroadcastManager.getInstance(this).registerReceiver(mReceiver, intentFilter);
	}

	@Override
	public void onResume() {
		super.onResume();
		CmmobiClickAgentWrapper.onResume(this);
	}

	@Override
	public void onPause() {
		super.onPause();
		TackView tackView = TackView.getTackView();
		if (tackView != null)
		{
			tackView.stop();
		}
		if(AudioPlayer.status == 1)
		{
			AudioPlayer.pause();
			pauseAudioPlayAnimation();
		}
		if(ePlayStatus == EPlayStatus.PLAY)
		{
			pauseVideo();
		}
		// 记录播放位置
		if(ePlayStatus == EPlayStatus.PAUSE)
		{
			ePlayStatus.seekPosition = mMediaPlayer.getCurrentTime();
		}
		if(mIvVideoThumbnail != null)
		{
			mIvVideoThumbnail.clearAnimation();
//			mIvVideoThumbnail.setVisibility(View.VISIBLE);
		}
		
		CmmobiClickAgentWrapper.onPause(this);
	}
	
	@Override
	public void onStop(){
		// 避免冲突
//		stopAutoPlay();
		Log.d(TAG,"onStop");
		super.onStop();
		CmmobiClickAgentWrapper.onStop(this);
	}
	
	@Override
	protected void onDestroy() {

		AudioPlayer.stop();
		stopAudioPlayAnimation();
		stopVideo();
		if(mMediaPlayer != null)
		{
			mMediaPlayer.release();
			mMediaPlayer = null;
		}
		
		LocalBroadcastManager.getInstance(this).unregisterReceiver(mReceiver);
		super.onDestroy();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event)
	{
		return super.onKeyDown(keyCode, event);
	}
	
	@Override
	public boolean handleMessage(Message msg) {
		if (null == msg.obj
				&& msg.what != HANDLER_UPDATE_VIDEO_PLAYER_COMPLETE
				&& msg.what != HANDLER_UPDATE_AUDIO_PLAYER_COMPLETE
//				&& msg.what != HANDLER_PLAY_VIDEO
//				&& msg.what != HANDLER_PLAY_AUDIO
				&& msg.what != Requester3.RESPONSE_TYPE_GET_DIARY_URL
				&& msg.what != WeiboRequester.WEIXIN_INTERFACE_SEND
				&& msg.what != ExtAudioRecorder.HANDLER_AUDIO_RECORDER_DONE
				&& msg.what != QISR_TASK.HANDLER_QISR_RESULT_CLEAN
				&& msg.what != QISR_TASK.HANDLER_QISR_RESULT_DONE
				&& msg.what != HANDLER_SHORT_RECORD_STOP_TIME_DELAY
//				&& msg.what != HANDLER_AUTO_HIDE_CONTROLLER
				&& msg.what != HANDLER_UPDATE_VIDEO_ERROR)
		{
			Log.e(TAG, msg.what + " msg.obj is null");
			Prompt.Alert(getString(R.string.prompt_network_error));
			ZDialog.dismiss();
			return false;
		}
		switch (msg.what)
		{
//		case HANDLER_PLAY_VIDEO:
//			if(tvTack != null)
//			{
//				tvTack.stop();
//			}
//			String url = getVideoUrl();
//			playVideo(url);
//			setPlayBtnStatus(true);
//			break;
//		case HANDLER_PLAY_AUDIO:
//			if(tvTack != null)
//			{
//				tvTack.stop();
//			}
//			AudioPlayer.playAudio(getAudioPath((String) msg.obj), handler);
//			break;
		/*case HANDLER_AUTO_HIDE_CONTROLLER:
			// 3秒隐藏视频按钮
			setPlayBtnStatus(false);
			break;*/
		case HANDLER_UPDATE_VIDEO_PLAYER_COMPLETE:
			stopVideo();
//			setFullSreen(false);
			break;
		case HANDLER_UPDATE_AUDIO_PLAYER_COMPLETE:
			AudioPlayer.stop();
			stopAudioPlayAnimation();
			break;
		case HANDLER_UPDATE_TACK_PLAYER_PROCESS:
			break;
		case HANDLER_UPDATE_VIDEO_PLAYER_PROCESS:
			if(mPlayProcess!=null)
			{
				PlayTime pt = (PlayTime) msg.obj;
//				mTVVideoTime.setText(DateUtils.getFormatTime0000(String.valueOf((pt.current + 500) / 1000)));				if(pt.total != 0)
				{
					mPlayProcess.setProgress(pt.current * 100 / pt.total);
				}
			}
			break;
		case HANDLER_UPDATE_AUDIO_PLAYER_PROCESS:
			if(mPlayProcess != null)
			{
				PlayTime pt = (PlayTime) msg.obj;
				if(pt.total != 0)
				{
					mPlayProcess.setProgress(pt.current * 100 / pt.total);
				}
//				SimpleDateFormat sdf = new SimpleDateFormat("mm:ss");
//				mTVAudioTime.setText(sdf.format(new Date(pt.current)));
				mTVAudioTime.setText(DateUtils.getFormatTime0000(String.valueOf(pt.current/1000)));
			}
			break;
		case HANDLER_UPDATE_VIDEO_PREPARED:
			if((Boolean)msg.obj)
			{
//				ePlayStatus = EPlayStatus.OPENED;
				if (mMediaPlayer != null) {
					mMediaPlayer.play();
					// 如果是pause状态被终止播放的情况，从断点开始播放
					if(ePlayStatus == EPlayStatus.PAUSE)
					{
						mMediaPlayer.seek(ePlayStatus.seekPosition);
						ePlayStatus.seekPosition = 0d;
					}
					ePlayStatus = EPlayStatus.PLAY;
					Animation anim = AnimationUtils.loadAnimation(DiaryEditPreviewActivity.this, R.anim.video_thumb_out);
					mIvVideoThumbnail.startAnimation(anim);
	//				mIvVideoThumbnail.setVisibility(View.INVISIBLE);
					Log.v(TAG, "xmediaplayer play");
				}
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
		case DiaryController.DIARY_REQUEST_DONE:
			MyDiary diary = ((DiaryWrapper) msg.obj).diary;
			MyDiary localDiary = DiaryManager.getInstance().findMyDiaryByUUID(diary.diaryuuid);
			ZDialog.dismiss();
			Intent intent;
			if(diary.diaryuuid.equals(myDiary.diaryuuid))
			{
				intent = new Intent(DiaryPreviewActivity.DIARY_EDIT_MOD);
				DiaryManager.getInstance().notifyMyDiaryChanged();
			}
			else
			{
				intent = new Intent(DiaryPreviewActivity.DIARY_EDIT_NEW);
				myDiary = diary;
			}
			ZLog.e("X DIARY UUID = " + diary.diaryuuid);
			
			intent.putExtra(DiaryPreviewActivity.INTENT_ACTION_DIARY_UUID, diary.diaryuuid);
			LocalBroadcastManager.getInstance(MainApplication.getAppInstance()).sendBroadcast(intent);
			ZDialog.dismiss();
			
			if (isNeedToShareToPublic) {
				Log.d(TAG,"DIARY_REQUEST_DONE isNeedToShareToPublic = " + isNeedToShareToPublic);
				gotoShareActivity();
			}
			
			if (isNeedFinish) {
				finish();
			}
			
			/*if (!isShowShareDialog) {
				finish();
				if (!isToShootActivity) {
					Log.d(TAG,"DIARY_REQUEST_DONE isToShootActivity = " + isToShootActivity);
					gotoShareActivity();
				}
			}*/
			
			if (isNeedToShootActivity) {
				VideoShootActivity2.startOnShortShootMode(DiaryEditPreviewActivity.this, true);
				finish();
			}
			break;
		default:
			break;
		}
		return false;
	}
	
	@Override
	public void onBackPressed() {
		if (isShowShareDialog) {
			isShowShareDialog = false;
//			RelativeLayout layout = viewFinder.findRelativeLayout(R.id.share_layer);
//			layout.setVisibility(View.INVISIBLE);
			
		} else {
			if (isDoneBtnEnabled() && (isMainAttachModifyed || isAuxAttachModifyed)) {
				ZDialog.dismiss();
				ZDialog.show(R.layout.dialog_ask_save_change, false, true, this, true);
				ZDialog.getZViewFinder().findTextView(R.id.yes_save).setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						saveDiaryEdit();                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                       
						ZDialog.dismiss();
						finish();
					}
				});
				
				ZDialog.getZViewFinder().findTextView(R.id.no_cancel).setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						ZDialog.dismiss();
						finish();
					}
				});
				
			} else {
				ZDialog.dismiss();
				finish();
			}
		}
	}
	
	private void saveDiaryEdit() {
		if (isDoneBtnEnabled()) {
			isToShootActivity = true;
			modifyDiary(true);
		}
	}
	
	boolean isFullScreenMode;
	boolean isSaved;
	boolean isNeedToShootActivity;
	boolean isMainAttachModifyed;
	boolean isAuxAttachModifyed;
	boolean isNeedFinish = false;
	
	@Override
	public void onClick(View v) {
		switch (v.getId())
		{
		case R.id.ib_title_back:
			revocateModify();
			finish();
			break;
			
		case R.id.btn_title_done:
			isNeedFinish = true;
			mBtnDone.setEnabled(false);
			modifyDiary(true);
			break;
			
		case R.id.ib_title_home:
			if (isDoneBtnEnabled() && (isMainAttachModifyed || isAuxAttachModifyed)) {
				ZDialog.dismiss();
				ZDialog.show(R.layout.dialog_ask_save_change, false, true, this, true);
				ZDialog.getZViewFinder().findTextView(R.id.yes_save).setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						saveDiaryEdit();
						ZDialog.dismiss();
						finish();
						CmmobiClickAgentWrapper.onEvent(DiaryEditPreviewActivity.this, "space");
					}
				});
				
				ZDialog.getZViewFinder().findTextView(R.id.no_cancel).setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						ZDialog.dismiss();
						finish();
					}
				});
				
			} else {
				ZDialog.dismiss();
				finish();
				CmmobiClickAgentWrapper.onEvent(this, "space");
			}
			break;
			
		case R.id.ib_title_shoot:
		case R.id.shoot_again:
			if (isDoneBtnEnabled() && (isMainAttachModifyed || isAuxAttachModifyed)) {
				ZDialog.dismiss();
				ZDialog.show(R.layout.dialog_ask_save_change, false, true, this, true);
				ZDialog.getZViewFinder().findTextView(R.id.yes_save).setOnClickListener(new OnClickListener() {

					boolean isClicked = false;
					
					@Override
					public void onClick(View v) {
						if (!isClicked) {
							isClicked = true;
							
							saveDiaryEdit();
							isNeedToShootActivity = true;
						}
					}
				});
				
				ZDialog.getZViewFinder().findTextView(R.id.no_cancel).setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						ZDialog.dismiss();
						VideoShootActivity2.startOnShortShootMode(DiaryEditPreviewActivity.this, true);
						finish();
					}
				});
				
			} else {
				VideoShootActivity2.startOnShortShootMode(this, true);
				finish();
			}
			CmmobiClickAgentWrapper.onEvent(this, "again1");
			break;
			
		case R.id.ib_title_share:
			isShowShareDialog = true;
			if (isDoneBtnEnabled() && (isMainAttachModifyed || isAuxAttachModifyed)) {
				modifyDiary(false);
				isMainAttachModifyed = false;
				isAuxAttachModifyed = false;
				isNeedToShareToPublic = true;
			} else {
				gotoShareActivity();
			}
			CmmobiClickAgentWrapper.onEvent(this, "share_button");
			break;
		case R.id.ll_diarypreview_square:
		case R.id.iv_diarypreview_video_play://视频播放按钮
			// 避免冲突
//			stopAutoPlay();
//			if (null == v.getTag())
//			{
//				Log.e(TAG, "video url null");
//				return;
//			}
//			else
			{
//				String url=v.getTag().toString();
				String url = myDiary.getMainUrl();
				String newUrl = getVideoPath(url);
				
				if(ePlayStatus == EPlayStatus.PLAY)
				{
					pauseVideo();
					/*if(getHandler().hasMessages(HANDLER_AUTO_HIDE_CONTROLLER))
					{
						getHandler().removeMessages(HANDLER_AUTO_HIDE_CONTROLLER);
					}*/
				}
				else
				{
					if(!isNetworkConnected(this, url, 5))
					{
						Prompt.Alert("您的网络不给力呀");
						break;
					}
					if(newUrl.startsWith("http"))
					{
						newUrl += getStatisString(this, myDiary.userid, myDiary.latitude_view+":"+myDiary.longitude_view, 
								getMediaId(myDiary, "1"), "1", "4");
					}
					
//					if(ePlayStatus == EPlayStatus.NON)
//					{
//						setFullSreen(true);
//					}
					playVideo(newUrl);
				}
				setPlayBtnStatus(mBtnVideoPlay.isShown());
			}
			break;
		case R.id.iv_diarypreview_audio_play://播放长录音
			// 避免冲突
//			stopAutoPlay();
			if(AudioPlayer.status == 1)
			{
				AudioPlayer.pause();
				pauseAudioPlayAnimation();
			}
//			else if (null == v.getTag())
//			{
//				Log.e(TAG, "audio url null");
//				return;
//			}
//			else
			{
//				String url=v.getTag().toString();
				String url = myDiary.getMainUrl();
				String newUrl = getAudioPath(url);
				if(!isNetworkConnected(this, url, 4))
				{
					Prompt.Alert("您的网络不给力呀");
					break;
				}
				if(newUrl.startsWith("http"))
				{
					newUrl += getStatisString(this, myDiary.userid, myDiary.latitude_view+":"+myDiary.longitude_view, 
						getMediaId(myDiary, "2"), "3", "4");
				}
				AudioPlayer.playAudio(newUrl,handler);
				startAudioPlayAnimation();
			}
			break;
		case R.id.diarypreview_tack_center:
		case R.id.diarypreview_tack_right_bottom:
			if(v.getTag()!=null){
				
				String url=v.getTag().toString();
				if(!isNetworkConnected(this, url, 3))
				{
					Prompt.Alert("您的网络不给力呀");
					break;
				}
				TackView tackView=(TackView) v;
//				tackView.setHandler(handler);
				String statisUrl = "";
				if(url.startsWith("http"))
				{
					statisUrl = getStatisString(this, myDiary.userid, myDiary.latitude_view+":"+myDiary.longitude_view, 
							getMediaId(myDiary, "2"), "4", "4");
				}
				tackView.setAudio(url, 1, statisUrl);
			}
			break;
		/*case R.id.ll_diarypreview_square:
			//全屏播放
			DiaryType diaryType = getDiaryType(myDiary);
			switch (diaryType) {
			case VEDIO://主体视频
				if(ePlayStatus != EPlayStatus.NON || ePlayStatus != EPlayStatus.PAUSE)
				{
					boolean show = mBtnVideoPlay.isShown();
					if(!show)
					{
//						if(getHandler().hasMessages(HANDLER_AUTO_HIDE_CONTROLLER))
//						{
//							getHandler().removeMessages(HANDLER_AUTO_HIDE_CONTROLLER);
//						}
//						getHandler().sendEmptyMessageDelayed(HANDLER_AUTO_HIDE_CONTROLLER, 4000);
					}
					setPlayBtnStatus(!show);
				}
				else
				{
					if (isFullScreenMode)
					{
						isFullScreenMode = false;
					}
					else
					{
						isFullScreenMode = true;
					}
				}
				break;
			case AUDIO://主体音频
			case PICTURE://主体图片
			case TEXT://主体文字
				break;
			}
			break;*/
		case R.id.tv_position:
			break;
		case R.id.tv_diary_position:
		{
			if (!tgbShowGps.isChecked()) {
				return;
			}
			CmmobiClickAgent.onEvent(this, "content_edit", "5");
			Intent posIntent = new Intent();
			posIntent.setClass(this, PositionSelectActivity.class);
			startActivityForResult(posIntent, REQUEST_POSITION);
			break;
		}
		case R.id.iv_jianji:
		{
			if (!isFastDoubleClick()) {
				break;
			}
			CmmobiClickAgent.onEvent(this, "content_edit", "1");
			Intent intent = new Intent();
			String diaryString =  new Gson().toJson(myDiary);
			String diaryEditString = new Gson().toJson(diaryEditNote);
			intent.putExtra("diaryuuid",myDiary.diaryuuid);
			intent.putExtra("diarystring", diaryString);
			intent.putExtra(INTENT_ACTION_EDIT_NOTELIST, diaryEditString);
			intent.setClass(this, EditVideoActivity.class);
			startActivityForResult(intent, REQUEST_VIDEO);
			break;
		}
		case R.id.iv_texiao:
		{
			if (!isFastDoubleClick()) {
				break;
			}
			if (myDiary.isVideoDiary()) {
				CmmobiClickAgent.onEvent(this, "content_edit", "2");
				Intent videoEffectIntent = new Intent();
				String diaryString =  new Gson().toJson(myDiary);
				String diaryEditString = new Gson().toJson(diaryEditNote);
				videoEffectIntent.putExtra("diaryuuid",myDiary.diaryuuid);
				videoEffectIntent.putExtra("diarystring", diaryString);
				videoEffectIntent.putExtra(INTENT_ACTION_EDIT_NOTELIST, diaryEditString);
				videoEffectIntent.setClass(this, EditVideoEffectActivity.class);
				startActivityForResult(videoEffectIntent, REQUEST_VIDEO_EFFECT);
			} else if (myDiary.isPicDiary()) {
				CmmobiClickAgent.onEvent(this, "content_edit", "2");
				Intent photoEffectIntent = new Intent();
				String diaryString = new Gson().toJson(myDiary);
				String diaryEditString = new Gson().toJson(diaryEditNote);
				photoEffectIntent.putExtra("diarystring", diaryString);
				photoEffectIntent.putExtra("diaryuuid",myDiary.diaryuuid);
				photoEffectIntent.putExtra(INTENT_ACTION_EDIT_NOTELIST, diaryEditString);
				photoEffectIntent.setClass(this, EditPhotoEffectActivity.class);
				startActivityForResult(photoEffectIntent, REQUEST_PHOTO_EFFECT);
			}
			break;
		}
		case R.id.iv_peiyue:
		{
			if (!isFastDoubleClick()) {
				break;
			}
			CmmobiClickAgent.onEvent(this, "content_edit", "3");
			Intent videoMusicIntent = new Intent();
			String diaryString = new Gson().toJson(myDiary);
			String diaryEditString = new Gson().toJson(diaryEditNote);
			videoMusicIntent.putExtra("diaryuuid",myDiary.diaryuuid);
			videoMusicIntent.putExtra("diarystring", diaryString);
			videoMusicIntent.putExtra(INTENT_ACTION_EDIT_NOTELIST, diaryEditString);
			videoMusicIntent.setClass(this, EditVideoSoundTrack.class);
			startActivityForResult(videoMusicIntent, REQUEST_VIDEO_SOUNDTRACK);
			break;
		}
		case R.id.iv_jiabiaoqian:
		{
			if (!isFastDoubleClick()) {
				break;
			}
			CmmobiClickAgent.onEvent(this, "content_edit", "4");
			Intent tagIntent = new Intent();
			tagIntent.putStringArrayListExtra("tagids", getDiaryTagStrs(myDiary));
//			String diaryString = new Gson().toJson(myDiary);
//			tagIntent.putExtra("diaryuuid",myDiary.diaryuuid);
//			tagIntent.putExtra("diarystring", diaryString);
			tagIntent.setClass(this, TagSelectedActivity.class);
			startActivityForResult(tagIntent, REQUEST_TAG);
			break;
		}
		case R.id.iv_caijian:
		{
			if (!isFastDoubleClick()) {
				break;
			}
			CmmobiClickAgent.onEvent(this, "content_edit", "6");
			Intent pictureIntent = new Intent();
			String diaryString = new Gson().toJson(myDiary);
			String diaryEditString = new Gson().toJson(diaryEditNote);
			pictureIntent.putExtra("diaryuuid",myDiary.diaryuuid);
			pictureIntent.putExtra("diarystring", diaryString);
			pictureIntent.putExtra(INTENT_ACTION_EDIT_NOTELIST, diaryEditString);
			pictureIntent.setClass(this, EditPhotoActivity.class);
			startActivityForResult(pictureIntent, REQUEST_PHOTO);
			break;
		}
		default:
			break;
		}
	}
	
	private static long lastClickTime;
	boolean isNeedToShareToPublic;

	public static boolean isFastDoubleClick()
	{
		long time = System.currentTimeMillis();
		long timeD = time - lastClickTime;
		if (0 < timeD && timeD < 800)
		{
			return false;
		}
		lastClickTime = time;
		return true;
	}
	
	private void gotoShareActivity(boolean isStatistics) {
		if (isFromShortShootActivity || isFromLongShootActivity) {/*
			
			isShowShareDialog = true;
			showMenu();
			
			final RelativeLayout layout = viewFinder.findRelativeLayout(R.id.share_layer);
			layout.setVisibility(View.VISIBLE);
			layout.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					isShowShareDialog = false;
					layout.setVisibility(View.INVISIBLE);
				}
			});
			
			ZViewFinder finder = new ZViewFinder();
			finder.set(layout);
			
			ImageView share = finder.findImageView(R.id.micro_share);
			share.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					
					// 2014-4-8
					CmmobiClickAgentWrapper.onEvent(DiaryEditPreviewActivity.this, "vshare_button");
					
					isShowShareDialog = false;
					layout.setVisibility(View.INVISIBLE);
					
					MyDiary[] diaries = new MyDiary[1];
					diaries[0] = myDiary;
					MyDiaryList myDiaryList = DiaryManager.getInstance().findDiaryGroupByUUID(myDiary.diaryuuid);
					
					Intent intent = new Intent(DiaryEditPreviewActivity.this, ShareDiaryActivity.class);
					intent.putExtra(ShareDialog.TYPY_SHARE, ShareDialog.TYPE_SHARE_V);
					intent.putExtra(DiaryPreviewActivity.INTENT_ACTION_DIARYLIST_STRING, new Gson().toJson(myDiaryList));
					intent.putExtra(DiaryPreviewActivity.INTENT_ACTION_DIARY_STRING, new Gson().toJson(diaries));
					startActivity(intent);
					
					String uid = ActiveAccount.getInstance(MainApplication.getAppInstance()).getUID();
					AccountInfo accountInfo = AccountInfo.getInstance(uid);
					
					if (accountInfo != null) {
						LoginSettingManager lsm = accountInfo.setmanager;
						MyBind mb = null;
						if (lsm != null) {
							mb = lsm.getBinding_type(LoginSettingManager.BINDING_TYPE_PHONE, "");
						}
						
						if (mb == null && TextUtils.isEmpty(accountInfo.nickname)) {
							startActivity(new Intent(DiaryEditPreviewActivity.this, SettingPersonalInfoActivity.class));
						}
					}
				}
			});
			
			ImageView friend = finder.findImageView(R.id.friend_visible);
			friend.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					isShowShareDialog = false;
					layout.setVisibility(View.INVISIBLE);
					
					if("2".equals(myDiary.publish_status))
					{
						// 2014-4-8
						CmmobiClickAgentWrapper.onEvent(DiaryEditPreviewActivity.this, "private");
						
						new Xdialog.Builder(DiaryEditPreviewActivity.this)
						.setMessage("是否将该内容设置为私密？")
						.setPositiveButton("确定", new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) {
								shareToPublish();
							}
						})
						.setNegativeButton("取消", null)
						.create().show();
					}
					else
					{
						// 2014-4-8
						//CmmobiClickAgentWrapper.onEvent(DiaryEditPreviewActivity.this, "content_public");
						
						new Xdialog.Builder(DiaryEditPreviewActivity.this)
						.setMessage("是否将该内容设置为朋友可见？")
						.setPositiveButton("确定", new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) {
								shareToPublish();
							}
						})
						.setNegativeButton("取消", null)
						.create().show();
					}
				}
			});
			
			TextView cancel = finder.findTextView(R.id.cancel);
			cancel.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					isShowShareDialog = false;
					layout.setVisibility(View.INVISIBLE);
				}
			});
			
			setBottom();
		*/
			// 2014-4-8
			if (isStatistics) {
				CmmobiClickAgentWrapper.onEvent(DiaryEditPreviewActivity.this, "vshare_button");
			}
			String uid = ActiveAccount.getInstance(MainApplication.getAppInstance()).getUID();
			AccountInfo accountInfo = AccountInfo.getInstance(uid);
			
			if (accountInfo != null && TextUtils.isEmpty(accountInfo.nickname)) {
//				LoginSettingManager lsm = accountInfo.setmanager;
//				MyBind mb = null;
//				if (lsm != null) {
//					mb = lsm.getBinding_type(LoginSettingManager.BINDING_TYPE_PHONE, "");
//				}
				
//				if (mb == null && TextUtils.isEmpty(accountInfo.nickname)) {
					startActivityForResult(new Intent(DiaryEditPreviewActivity.this, SettingPersonalInfoActivity.class),SETTING_PERSONAL_INFO);
//				}
			} else {
				isShowShareDialog = true;
				isNeedToShareToPublic = false;
				
				MyDiary[] diaries = new MyDiary[1];
				diaries[0] = myDiary;
				MyDiaryList myDiaryList = DiaryManager.getInstance().findDiaryGroupByUUID(myDiary.diaryuuid);
				
				Intent intent = new Intent(DiaryEditPreviewActivity.this, ShareDiaryActivity.class);
				intent.putExtra(ShareDialog.TYPY_SHARE, ShareDialog.TYPE_SHARE_V);
				intent.putExtra(DiaryPreviewActivity.INTENT_ACTION_DIARYLIST_STRING, new Gson().toJson(myDiaryList));
				intent.putExtra(DiaryPreviewActivity.INTENT_ACTION_DIARY_STRING, new Gson().toJson(diaries));
				startActivity(intent);
			}
		}
	}
	
	private void gotoShareActivity() {
		gotoShareActivity(true);
	}
	
	private void showMenu() {
		isShowShareDialog = true;
		final RelativeLayout layout = viewFinder.findRelativeLayout(R.id.share_layer);
		final LinearLayout buttons = viewFinder.findLinearLayout(R.id.buttons);
		layout.setVisibility(View.VISIBLE);
		
//		TranslateAnimation animation = new TranslateAnimation(0, 0, ZScreen.getHeight(), ZScreen.getHeight() - 200);
//		animation.setDuration(300);
//		animation.setAnimationListener(new AnimationListener() {
//			
//			@Override
//			public void onAnimationStart(Animation animation) {
//				layout.setVisibility(View.VISIBLE);
//			}
//			
//			@Override
//			public void onAnimationRepeat(Animation animation) {
//			}
//			
//			@Override
//			public void onAnimationEnd(Animation animation) {
//			}
//		});
//		buttons.startAnimation(animation);
	}
	
	private void hideMenu() {
	}
	
	private void shareToPublish()
	{
		MyDiary diary = DiaryManager.getInstance().findMyDiaryByUUID(myDiary.diaryuuid);
		
		if("2".equals(myDiary.publish_status))
		{
			diary.publish_status = "1";
			myDiary.publish_status = "1";
			OfflineTaskManager.getInstance().addSetDiarySharePermissionsTask(myDiary.diaryid, myDiary.diaryuuid, "1");
		}
		else
		{
			diary.publish_status = "2";
			myDiary.publish_status = "2";
			OfflineTaskManager.getInstance().addSetDiarySharePermissionsTask(myDiary.diaryid, myDiary.diaryuuid, "2");
		}
		DiaryManager.getInstance().notifyMyDiaryChanged();
		setBottom();
		
		CmmobiClickAgentWrapper.onEvent(this, "content_public");
	}
	
	private void setBottom() {
//		if (isMyself()) {
//			vOtherTitlebar.setVisibility(View.INVISIBLE);
//		} else {
//			vMyTitlebar.setVisibility(View.INVISIBLE);
//		}

		final RelativeLayout layout = viewFinder.findRelativeLayout(R.id.share_layer);

		ZViewFinder finder = new ZViewFinder();
		finder.set(layout);
		
		ImageView friend = finder.findImageView(R.id.friend_visible);
		
		if ("2".equals(myDiary.publish_status)) {
			friend.setImageResource(R.drawable.btn_diarypreview_friend);
			
		} else {
			friend.setImageResource(R.drawable.btn_diarypreview_private);
		}
	}
	
	// 判断当前日记是否是自己的日记
	private boolean isMyself() {
		if (myDiary != null) {
			String uid = ActiveAccount.getInstance(MainApplication.getAppInstance()).getUID();
			if (uid != null && uid.equals(myDiary.userid))
				return true;
		}
		return false;
	}
	
	private void revocateModify() {
		/*if (myDiary.getMainPath() != null && !myDiary.getMainPath().equals(originalDiary.getMainPath())) {
			ZFileSystem.delFile(myDiary.getMainPath());
			String uid = ActiveAccount.getInstance(MainApplication.getAppInstance()).getUID();
			AccountInfo.getInstance(uid).mediamapping.delMedia(uid,myDiary.getMainUrl());
		}*/
		String uid = ActiveAccount.getInstance(MainApplication.getAppInstance()).getUID();
		for (String path:cachePathSet) {
			if (!path.equals(originalDiary.getMainPath())) {
				AccountInfo.getInstance(uid).mediamapping.delMedia(uid,"file://" + path);
				ZFileSystem.delFile(path);
			}
		}
		
		if (originalDiary.isVideoDiary()) {
			for (String path:cacheVideoCoverSet) {
				if (!path.equals(originalDiary.getVideoCoverPath())) {
					AccountInfo.getInstance(uid).mediamapping.delMedia(uid,"file://" + path);
					ZFileSystem.delFile(path);
				}
			}
		}
		cachePathSet.clear();
		cacheVideoCoverSet.clear();
	}
	
	private void modifyDiary(boolean finish) {
		if (!isDoneBtnEnabled()) {
			if (finish) {
				finish();
			}
			return;
		}
		Log.d(TAG,"modifyDiary in");
		
		ZDialog.show(R.layout.progressdialog, false, true, this, false);
		
		String uid = ActiveAccount.getInstance(MainApplication.getAppInstance()).getUID();
		
		if (diaryEditNote.mediaPath != null && !diaryEditNote.mediaPath.equals(originalDiary.getMainPath()) 
				&& !diaryEditNote.mediaPath.equals(myDiary.getMainPath())) {
			ZFileSystem.delFile(diaryEditNote.mediaPath);
			AccountInfo.getInstance(uid).mediamapping.delMedia(uid,"file://" + diaryEditNote.mediaPath);
		}
		
		for (String path:cachePathSet) {
			if (!path.equals(originalDiary.getMainPath()) && !path.equals(myDiary.getMainPath())) {
				ZFileSystem.delFile(path);
				AccountInfo.getInstance(uid).mediamapping.delMedia(uid,"file://" + path);
			}
		}
		if (originalDiary.isVideoDiary()) {
			for (String path:cacheVideoCoverSet) {
				if (!path.equals(originalDiary.getVideoCoverPath()) && !path.equals(myDiary.getVideoCoverPath())) {
					ZFileSystem.delFile(path);
					AccountInfo.getInstance(uid).mediamapping.delMedia(uid,"file://" + path);
				}
			}
		}
		cachePathSet.clear();
		cacheVideoCoverSet.clear();
		
		// 2014-4-8
		if ("0".equals(myDiary.position_status) && !ZStringUtils.nullToEmpty(myDiary.position_status).equals(ZStringUtils.nullToEmpty(originalDiary.position_status))) {
			CmmobiClickAgentWrapper.onEvent(this, "unshow_location",isFromShootting?"2":"1");
			Log.d(TAG,"unshow_location isFromShootting = " + isFromShootting);
		} else if (!ZStringUtils.nullToEmpty(myDiary.position_view).equals(ZStringUtils.nullToEmpty(originalDiary.position_view))
				||!ZStringUtils.nullToEmpty(myDiary.position_status).equals(ZStringUtils.nullToEmpty(originalDiary.position_status))){
			HashMap<String,String> map = new HashMap<String, String>();
//			if (!ZStringUtils.nullToEmpty(myDiary.position_view).equals(ZStringUtils.nullToEmpty(originalDiary.position_view))) {
				map.put("label", "2");
//			} else {
//				map.put("label", "1");
//			}
			
			map.put("label2", CommonInfo.getInstance().addressCode);
			CmmobiClickAgentWrapper.onEvent(this, "show_location",map);
			Log.d(TAG,"show_location label = " + map.get("label") + " label2 = " + map.get("label2"));
		} else {
			HashMap<String,String> map = new HashMap<String, String>();
			map.put("label", "1");
			map.put("label2", CommonInfo.getInstance().addressCode);
			CmmobiClickAgentWrapper.onEvent(this, "show_location",map);
			Log.d(TAG,"show_location label = " + map.get("label") + " label2 = " + map.get("label2"));
		}
					
		if (isMainAttachModifyed && (!ZStringUtils.nullToEmpty(originalDiary.getMainPath()).equals(ZStringUtils.nullToEmpty(myDiary.getMainPath()))
				|| (!ZStringUtils.nullToEmpty(myDiary.getMainTextContent()).equals(ZStringUtils.nullToEmpty(originalDiary.getMainTextContent()))))) {
			
			String attachID = "";
			String attachUUID = DiaryController.getNextUUID();
			Attachs attach = DiaryController.getInstanse().createNewAttach(attachID, attachUUID, myDiary.getDiaryMainType(), DiaryController.getSuffix(myDiary.getMainPath()), GsonProtocol.ATTACH_LEVEL_MAIN, GsonProtocol.ATTACH_OPERATE_TYPE_UPDATE, myDiary.getMainTextContent(),myDiary.getMainPath());
			Attachs[] attachs = new Attachs[1];
			attachs[0] = attach;
			String coverUrl = null;
			if (!ZStringUtils.nullToEmpty(myDiary.getVideoCoverPath()).equals(ZStringUtils.nullToEmpty(originalDiary.getVideoCoverPath()))) {
				coverUrl = ZStringUtils.emptyToNull(myDiary.attachs.videocover);
			}
			FileOperate fileOperate = FileOperate.RENAME;
			if (ZStringUtils.nullToEmpty(originalDiary.getMainPath()).equals(ZStringUtils.nullToEmpty(myDiary.getMainPath()))) {
				fileOperate = FileOperate.COPY;
			} else {
				fileOperate = FileOperate.RENAME;
			}
			DiaryController.getInstanse().savaAsDiary(handler, originalDiary.diaryuuid, attachs,myDiary.getTagIds(),myDiary.longitude_view,myDiary.latitude_view,myDiary.position_view,coverUrl,myDiary.position_status,fileOperate,"");
			AccountInfo.getInstance(uid).mediamapping.delMedia(uid,"file://" + myDiary.getMainPath());
		} else {
			boolean isPosOrTagChanged = false;
			if (!ZStringUtils.nullToEmpty(myDiary.getTagIds()).equals(ZStringUtils.nullToEmpty(originalDiary.getTagIds())) 
					|| !ZStringUtils.nullToEmpty(myDiary.position_view).equals(ZStringUtils.nullToEmpty(originalDiary.position_view))
					|| !ZStringUtils.nullToEmpty(myDiary.position_status).equals(ZStringUtils.nullToEmpty(originalDiary.position_status))) {
				DiaryController.getInstanse().updateDiary(handler, myDiary, null, myDiary.getTagIds(), myDiary.longitude_view,myDiary.latitude_view,myDiary.position_view,myDiary.position_status,"");
				DiaryController.getInstanse().diaryContentIsReady(myDiary.diaryuuid);
				isPosOrTagChanged = true;
			}
			
			if ("1".equals(myDiary.getDiaryMainType()) && !ZStringUtils.nullToEmpty(myDiary.getVideoCoverPath()).equals(ZStringUtils.nullToEmpty(originalDiary.getVideoCoverPath()))) {
				OfflineTaskManager.getInstance().addVideoCoverUploadTask(myDiary.getVideoCoverPath(), myDiary.diaryid, myDiary.attachs.levelattach.attachid,myDiary.diaryuuid);
				MyDiary localDiary = DiaryManager.getInstance().findMyDiaryByUUID(myDiary.diaryuuid);
				AccountInfo.getInstance(uid).mediamapping.delMedia(uid,localDiary.attachs.videocover,true);
				localDiary.attachs.videocover = myDiary.attachs.videocover;
				DiaryManager.getInstance().notifyMyDiaryChanged();
				if (!isPosOrTagChanged) {
					Intent intent;
					if(localDiary.diaryuuid.equals(myDiary.diaryuuid))
					{
						intent = new Intent(DiaryPreviewActivity.DIARY_EDIT_MOD);
					}
					else
					{
						intent = new Intent(DiaryPreviewActivity.DIARY_EDIT_NEW);
					}
					intent.putExtra(DiaryPreviewActivity.INTENT_ACTION_DIARY_UUID, localDiary.diaryuuid);
					LocalBroadcastManager.getInstance(MainApplication.getAppInstance()).sendBroadcast(intent);
					if (!isShowShareDialog) {
						finish();
						if (!isToShootActivity) {
							gotoShareActivity();
						}
					}
				}
			}
		}
	}
	
	// 播放视频
	private void playVideo(String url){
//		String videoCover = getVideoCover();
//		if (videoCover != null && videoCover.length() > 0) {
//			ivVideoThumbnail.setImageUrl(0, 1, videoCover, false);
//		}
		if(null == mMediaPlayer && mVideoContent != null)
		{
			mVideoContent.removeAllViews();
			XEffects mEffects = null;
			if (PluginUtils.isPluginMounted()) {
				mEffects = new XEffects();
			}
			mMediaPlayer = new XMediaPlayer(this, mEffects, false);
			mMediaPlayer.setListener(new VideoOnInfoListener());
			RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(LayoutParams.FILL_PARENT,LayoutParams.FILL_PARENT);
			mVideoContent.addView((View)mMediaPlayer.getXSurfaceView(), params);
			
			Log.v(TAG, "xmediaplayer create");
		}
		if(mMediaPlayer != null && url != null)
		{
			if (mMediaPlayer.getStatus() == XEffectMediaPlayer.STATUS_UNKNOW && ePlayStatus == EPlayStatus.NON)
			{
				ZLog.alert();
				ZLog.e("urL  == " + url);
				mMediaPlayer.open(url);
				
				mBtnVideoPlay.setVisibility(View.INVISIBLE);
//				mBtnVideoPlay.setBackgroundResource(R.drawable.btn_diarypreview_video_pause);
				ePlayStatus = EPlayStatus.OPENING;
			} else if (mMediaPlayer.getStatus() == XEffectMediaPlayer.STATUS_PAUSE) {
				mMediaPlayer.resume();
				Log.v(TAG, "xmediaplayer resume");
				mBtnVideoPlay.setVisibility(View.INVISIBLE);
//				mBtnVideoPlay.setBackgroundResource(R.drawable.btn_diarypreview_video_pause);
				ePlayStatus = EPlayStatus.PLAY;
			}
			else if(mMediaPlayer.getStatus() == XEffectMediaPlayer.STATUS_UNKNOW && ePlayStatus == EPlayStatus.PAUSE)
			{ //这种情况是UI控制了状态为暂停 但是实际由于surffaceview销毁导致播放已停止
			  //所以用ePlayStatus做标志，继续播放时从断点开始
				mMediaPlayer.open(url);
				Log.v(TAG, "xmediaplayer restart");
				mBtnVideoPlay.setVisibility(View.INVISIBLE);
//				mBtnVideoPlay.setBackgroundResource(R.drawable.btn_diarypreview_video_pause);
			}
			else
			{
				// 避免最坏情况
				stopVideo();
			}
		}
		else
		{
			Log.e(TAG, "mMediaPlayer is null");
		}
	}
	
	// 停止视频
	private void stopVideo(){
		ePlayStatus = EPlayStatus.NON;
		if(mMediaPlayer != null)
		{
			mIvVideoThumbnail.clearAnimation();
//			mIvVideoThumbnail.setVisibility(View.VISIBLE);
			mMediaPlayer.stop();
			mMediaPlayer.release();
			mMediaPlayer = null;
			Log.v(TAG, "xmediaplayer null");
			if(mBtnVideoPlay != null)
			{
				mBtnVideoPlay.setBackgroundResource(R.drawable.btn_diarypreview_video_play);
				mBtnVideoPlay.setVisibility(View.VISIBLE);
			}
		}else{
			Log.e(TAG, "mMediaPlayer is null");
		}
		
		if (mPlayProcess != null)
		{
			mPlayProcess.setProgress(0);
		}
		
		// 标记归位
		if(mBtnVideoPlay != null)
		{
			mBtnVideoPlay.setBackgroundResource(R.drawable.btn_diarypreview_video_play);
		}
		setPlayBtnStatus(true);
		
	}
	
	// 暂停视频
	private void pauseVideo(){
		if(mMediaPlayer != null){
			if (mMediaPlayer.getStatus() == XEffectMediaPlayer.STATUS_PALYING) {
				mMediaPlayer.pause();
				mBtnVideoPlay.setBackgroundResource(R.drawable.btn_diarypreview_video_play);
				mBtnVideoPlay.setVisibility(View.VISIBLE);
				ePlayStatus = EPlayStatus.PAUSE;
				Log.v(TAG, "xmediaplayer pause");
			}
		}else{
			Log.e(TAG, "mMediaPlayer is null");
		}
	}
	
	//获取日记主体类型
	private DiaryType getDiaryType(MyDiary myDiary){
		if(myDiary.attachs.levelattach!=null && myDiary.attachs.levelattach!=null){
				String type=myDiary.attachs.levelattach.attachtype;
				if("1".equals(type)){//视频
					return DiaryType.VEDIO;
				}
				if("2".equals(type)){//音频
					return DiaryType.AUDIO;
				}
				if("3".equals(type)){//图片
					return DiaryType.PICTURE;
				}
				if("4".equals(type)){//文字
					return DiaryType.TEXT;
				}
				if("5".equals(type)){//文字
					return DiaryType.TEXT;
				}
				if("6".equals(type)){//文字
					return DiaryType.TEXT;
				}
		}
		return DiaryType.NONE;
	}
	
	//设置视频布局数据
	private void loadVideoData(View v){
		CmmobiClickAgentWrapper.onEvent(this, "details_type", 3);
		
		v.findViewById(R.id.ll_diarypreview_square).setOnClickListener(this);
		
		mVideoContent = (RelativeLayout) v.findViewById(R.id.rl_diarypreview_video_surface);
		
		//文字描述
		mTVVideoTime = (TextView) v.findViewById(R.id.tv_diarypreview_video_time);
		String strTime = myDiary.getMainPlaytime();
		mTVVideoTime.setText(DateUtils.getFormatTime0000(strTime));
		
		// 查找缓存是否有图片 没有使用缩略图
		imageLoader = ImageLoader.getInstance();
		//imageLoader.init(ImageLoaderConfiguration.createDefault(DiaryEditPreviewActivity.this));
		

		imageLoaderOptions = new DisplayImageOptions.Builder()
				.showStubImage(R.drawable.bg_default)
				.showImageForEmptyUri(R.drawable.bg_default)
				.showImageOnFail(R.drawable.bg_default)
				.cacheInMemory(true).cacheOnDisc(true)
				.displayer(new SimpleBitmapDisplayer())
				// .displayer(new CircularBitmapDisplayer()) 圆形图片
				// .displayer(new RoundedBitmapDisplayer(20)) 圆角图片
				.build();
		
		imageLoadingListener = new AnimateFirstDisplayListener();
		
		mIvVideoThumbnail = (ImageView) v.findViewById(R.id.iv_diarydpreview_video_thumbnail);
		String videoCover = myDiary.getVideoCoverUrl();
		String userID = ActiveAccount.getInstance(ZApplication.getInstance()).getUID();
		if (videoCover != null && videoCover.length() > 0)
		{
//		    MediaValue mv = AccountInfo.getInstance(userID).mediamapping.getMedia(userID, videoCover);	
//	        if(MediaValue.checkMediaAvailable(mv)){//load local
//	        	videoCover = "file://" + Environment.getExternalStorageDirectory()  + mv.localpath;
//			}
			Log.v(TAG, "mThumbUrl = " + videoCover);
			imageLoader.displayImageEx(videoCover, mIvVideoThumbnail, imageLoaderOptions, imageLoadingListener,
					ActiveAccount.getInstance(this).getUID(), 1);
		}
		
		try
		{
			stopVideo();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		
		if(null == mMediaPlayer && mVideoContent != null)
		{
			mVideoContent.removeAllViews();
			XEffects mEffects = null;
			if (PluginUtils.isPluginMounted()) {
				mEffects = new XEffects();
			}
			mMediaPlayer = new XMediaPlayer(this, mEffects, false);
			mMediaPlayer.setListener(new VideoOnInfoListener());
			RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(LayoutParams.FILL_PARENT,LayoutParams.FILL_PARENT);
			mVideoContent.addView((View)mMediaPlayer.getXSurfaceView(), params);
			
			Log.v(TAG, "xmediaplayer create");
		}

		mBtnVideoPlay = (ImageView) v.findViewById(R.id.iv_diarypreview_video_play);
		mBtnVideoPlay.setOnClickListener(this);
		String videoUrl = myDiary.getMainUrl();
		if(videoUrl == null)
		{
			videoUrl = myDiary.attachs.levelattach.attachuuid;
		}
//		videoUrl="http://1s.looklook.cn:8082/pub/looklook/video_pub/original/2013/08/07/154816b087fa8691c44d13b190e11bd6933c00.mp4";
		Log.d(TAG, "getVideoUrl="+videoUrl);
		mBtnVideoPlay.setTag(videoUrl);
		
		mPlayProcess=(SeekBar) v.findViewById(R.id.sk_diarypreview_seek);
		mPlayProcess.setMax(100);
		mPlayProcess.setProgress(0);
		mPlayProcess.setOnSeekBarChangeListener(new OnSeekBarChangeListener()
		{
			
			@Override
			public void onStopTrackingTouch(SeekBar seekBar)
			{
				if(mMediaPlayer != null)
				{
					double time = mTotlaTime * (seekBar.getProgress() / 100d);
					if(ePlayStatus == EPlayStatus.PLAY)
					{
						mMediaPlayer.seek(time);
					}
					else if(ePlayStatus == EPlayStatus.PAUSE)
					{
						mMediaPlayer.resume();
						mMediaPlayer.seek(time);
						mMediaPlayer.pause();
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
		
		mBtnPraise = (ImageView) v.findViewById(R.id.iv_praise);
		mBtnPraise.setVisibility(View.GONE);
	
		vDiaryInfoTile = v.findViewById(R.id.rl_diarypreview_info);
		tagsLayout = v.findViewById(R.id.ll_diarypreview_tag);
		tvTags[0] = (TextView) v.findViewById(R.id.tv_tag1);
		tvTags[1] = (TextView) v.findViewById(R.id.tv_tag2);
		tvTags[2] = (TextView) v.findViewById(R.id.tv_tag3);
		vPin = (ImageView) v.findViewById(R.id.iv_pin);
		
		mTlParam = (LinearLayout) v.findViewById(R.id.ll_parm);
		inflater.inflate(R.layout.view_edit_position, mTlParam);
		tvPosition = (TextView) mTlParam.findViewById(R.id.tv_diary_position);
		tvPosition.setOnClickListener(this);
		tgbShowGps = (ToggleButton) mTlParam.findViewById(R.id.tgb_show_gps);
		tgbShowGps.setOnCheckedChangeListener(this);
		
//		RelativeLayout shell = (RelativeLayout) findViewById(R.id.rl_activity_detail);
		
		LinearLayout again = (LinearLayout) v.findViewById(R.id.shoot_again);
		again.setOnClickListener(this);
		again.setVisibility(View.VISIBLE);
		
//		RelativeLayout shell2 = (RelativeLayout) findViewById(R.id.rl_diarypreview_info);
//		shell2.removeView(again);
//		
//		android.widget.RelativeLayout.LayoutParams params = new android.widget.RelativeLayout.LayoutParams(
//				android.widget.RelativeLayout.LayoutParams.WRAP_CONTENT, 
//				android.widget.RelativeLayout.LayoutParams.WRAP_CONTENT);
//		params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, R.id.iv_praise);
//		params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, R.id.iv_praise);
//		params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, R.id.sk_diarypreview_seek);
//		params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, R.id.sk_diarypreview_seek);
//		again.setLayoutParams(params);
//		shell.addView(again, params);
		
		if (!isFromLongShootActivity && !isFromShortShootActivity) {
			v.findViewById(R.id.shoot_again).setVisibility(View.GONE);
		}
		
	}
	
	// 根据日记结构体中的url获取实际播放url
	private String getVideoPath(String url)
	{
		if ((null == url || 0 == url.length()))
		{
			Log.e(TAG, "url is null");
			return null;
		}
		ZLog.e("A1" + url);
		String uid = ActiveAccount.getInstance(MainApplication.getAppInstance()).getUID();
		MediaValue mv = AccountInfo.getInstance(uid).mediamapping.getMedia(uid, url);
		if (MediaValue.checkMediaAvailable(mv, 5))
		{
			ZLog.e("A2" + url);
			return Environment.getExternalStorageDirectory() + mv.localpath;
		}
		ZLog.e("A3" + url);
		return url;
	}
	
	//设置音频布局数据
	private void loadAudioData(View v){
		
		mIvLeftWheel = (ImageView) v.findViewById(R.id.iv_diarypreview_audio_wheel_left);
		mIvRightWheel = (ImageView) v.findViewById(R.id.iv_diarypreview_audio_wheel_right);

		//文字描述
		mTVAudioTime = (TextView) v.findViewById(R.id.tv_diarypreview_audio_time);
		String strTime = myDiary.getMainPlaytime();
//		if(strTime != null)
//		{
//			SimpleDateFormat sdf = new SimpleDateFormat("mm:ss");
//			try
//			{
//				mTVAudioTime.setText(sdf.format(new Date(Long.parseLong(strTime)*1000)));
//			}
//			catch (NumberFormatException e)
//			{
//				e.printStackTrace();
//			}
//		}
		
		mTVAudioTime.setText(DateUtils.getFormatTime0000(strTime));
		
		mBtnAudioPlay = (ImageView) v.findViewById(R.id.iv_diarypreview_audio_play);
		mBtnAudioPlay.setOnClickListener(this);
		//设置长录音播放路径
		String audioUrl = myDiary.getMainUrl();
		if(audioUrl == null)
		{
			audioUrl = myDiary.attachs.levelattach.attachuuid;
		}
		Log.d(TAG, "longRecUrl="+audioUrl);
		mBtnAudioPlay.setTag(audioUrl);
		
		mPlayProcess=(SeekBar) v.findViewById(R.id.sk_diarypreview_seek);
		mPlayProcess.setMax(100);
		mPlayProcess.setProgress(0);
		mPlayProcess.setOnSeekBarChangeListener(new OnSeekBarChangeListener()
		{
			
			@Override
			public void onStopTrackingTouch(SeekBar seekBar)
			{
				if(mp != null)
				{
					double time = mp.getDuration() * (seekBar.getProgress() / 100d);
					mp.seekTo((int) (time));
				}
				
			}
			
			@Override
			public void onStartTrackingTouch(SeekBar seekBar)
			{
				
			}
			
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser)
			{
				
			}
		});
		
		mBtnPraise = (ImageView) v.findViewById(R.id.iv_praise);
		mBtnPraise.setVisibility(View.GONE);
		
		vDiaryInfoTile = v.findViewById(R.id.rl_diarypreview_info);
		tagsLayout = v.findViewById(R.id.ll_diarypreview_tag);
		tvTags[0] = (TextView) v.findViewById(R.id.tv_tag1);
		tvTags[1] = (TextView) v.findViewById(R.id.tv_tag2);
		tvTags[2] = (TextView) v.findViewById(R.id.tv_tag3);
		vPin = (ImageView) v.findViewById(R.id.iv_pin);
		
		mTlParam = (LinearLayout) v.findViewById(R.id.ll_parm);
		inflater.inflate(R.layout.view_edit_position, mTlParam);
		tvPosition = (TextView) mTlParam.findViewById(R.id.tv_diary_position);
		tvPosition.setOnClickListener(this);
		tgbShowGps = (ToggleButton) mTlParam.findViewById(R.id.tgb_show_gps);
		tgbShowGps.setOnCheckedChangeListener(this);
		
	}
	
	// 根据日记结构体中的url获取实际播放url
	private String getAudioPath(String url)
	{
		if ((null == url || 0 == url.length()))
		{
			Log.e(TAG, "url is null");
			return null;
		}
		String uid = ActiveAccount.getInstance(MainApplication.getAppInstance()).getUID();
		MediaValue mv = AccountInfo.getInstance(uid).mediamapping.getMedia(uid, url);
		if (MediaValue.checkMediaAvailable(mv, 4))
		{
			return Environment.getExternalStorageDirectory() + mv.localpath;
		}
		return url;
	}
	
	private ImageLoader imageLoader = null;
	private DisplayImageOptions imageLoaderOptions = null;
	private ImageLoadingListener imageLoadingListener = null;
	//设置图片布局数据
	private void loadPictureData(View v){
		CmmobiClickAgentWrapper.onEvent(this, "details_type", 1);
		
		mIvPicture = (MultiPointTouchImageView) v.findViewById(R.id.iv_diarypreview_picture);
		mIvPicture.setBackgroundResource(R.drawable.bg_default);
		// 查找缓存是否有图片 没有使用缩略图
		imageLoader = ImageLoader.getInstance();
		//imageLoader.init(ImageLoaderConfiguration.createDefault(DiaryEditPreviewActivity.this));
		

		imageLoaderOptions = new DisplayImageOptions.Builder()
				.cacheInMemory(true).cacheOnDisc(true)
				.displayer(new SimpleBitmapDisplayer())
				// .displayer(new CircularBitmapDisplayer()) 圆形图片
				// .displayer(new RoundedBitmapDisplayer(20)) 圆角图片
				.build();
		
		imageLoadingListener = new AnimateFirstDisplayListener()
		{
			@Override
			public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage)
			{
				mIvPicture.setCenter(getWindowManager().getDefaultDisplay().getWidth(),
						getWindowManager().getDefaultDisplay().getWidth());
				mIvPicture.setBackgroundResource(R.color.black); //去掉背景
			}
		};
		
		String url = getPicturePath(myDiary.getMainUrl());
		if(url == null)
		{
			url = myDiary.attachs.levelattach.attachuuid;
		}
		Log.d(TAG, "imageUrl=" + url);
		imageLoader.displayImageEx(url, mIvPicture, imageLoaderOptions, imageLoadingListener,
				ActiveAccount.getInstance(this).getUID(), 1);
		
		mBtnPraise = (ImageView) v.findViewById(R.id.iv_praise);
		mBtnPraise.setVisibility(View.GONE);
		
		vDiaryInfoTile = v.findViewById(R.id.rl_diarypreview_info);
		tagsLayout = v.findViewById(R.id.ll_diarypreview_tag);
		tvTags[0] = (TextView) v.findViewById(R.id.tv_tag1);
		tvTags[1] = (TextView) v.findViewById(R.id.tv_tag2);
		tvTags[2] = (TextView) v.findViewById(R.id.tv_tag3);
		vPin = (ImageView) v.findViewById(R.id.iv_pin);

		mTlParam = (LinearLayout) v.findViewById(R.id.ll_parm);
		inflater.inflate(R.layout.view_edit_position, mTlParam);
		tvPosition = (TextView) mTlParam.findViewById(R.id.tv_diary_position);
		tvPosition.setOnClickListener(this);
		tgbShowGps = (ToggleButton) mTlParam.findViewById(R.id.tgb_show_gps);
		tgbShowGps.setOnCheckedChangeListener(this);
		
	}
	
	// 根据日记结构体中的url获取实际播放url
	private String getPicturePath(String imageUrl)
	{
		String url = imageUrl;
		if ((null == imageUrl || 0 == imageUrl.length()))
		{
			Log.e(TAG, "imageUrl is null");
			return null;
		}
		// 本地有原图片
		String uid = ActiveAccount.getInstance(MainApplication.getAppInstance()).getUID();
		MediaValue mv = AccountInfo.getInstance(uid).mediamapping.getMedia(uid, imageUrl);
		if (MediaValue.checkMediaAvailable(mv, 2))
		{
//			url = "file://" + Environment.getExternalStorageDirectory() + mv.path;
			url = imageUrl;
			Log.d(TAG, "use local url=" + url);
		}
		else
		{// 本地无原图片
			String thumbUrl = getThumbUrl();
			if (thumbUrl != null && thumbUrl.length() > 0)
			{// 先查缩略图缓存
				mv = AccountInfo.getInstance(uid).mediamapping.getMedia(uid, thumbUrl);
				if (MediaValue.checkMediaAvailable(mv, 2))
				{
//					url = "file://" + Environment.getExternalStorageDirectory() + mv.path;
					url = thumbUrl;
					Log.d(TAG, "use local thumbUrl=" + url);
				}
			}
			url = thumbUrl;
			Log.d(TAG, "use thumbUrl=" + url);
		}
		
		return url;
		
	}
	
	//设置文字布局数据
	private void loadTextData(View v){
		CmmobiClickAgentWrapper.onEvent(this, "details_type", 2);
		
		v.findViewById(R.id.diarypreview_tack_right_bottom).setVisibility(View.INVISIBLE);
		v.findViewById(R.id.diarypreview_tack_center).setVisibility(View.INVISIBLE);
		
		TextView tv = (TextView) v.findViewById(R.id.tv_diarypreview_text_content);
		tv.setTextSize(12);
		String content = myDiary.getMainTextContent();
		if(content == null || content.equals(""))
		{//无文字情况
			tvMainTack = (TackView) v.findViewById(R.id.diarypreview_tack_center);
			tvMainTack.setSoundIcons(SOUND_ICONS_BIG, false);
		}
		else
		{//有文字情况
			replacedExpressions(content, tv);
			tvMainTack = (TackView) v.findViewById(R.id.diarypreview_tack_right_bottom);
			tvMainTack.setSoundIcons(SOUND_ICONS_SMALL, true);
		}
		
		// 此处可能出现只有文字，但是url不为空；加上类型判断
		String url = myDiary.getMainUrl();
		if (url != null && url.length() > 0 
				&& !myDiary.attachs.levelattach.attachtype.equals("4"))
		{ 
			tvMainTack.setTag(url);
			tvMainTack.setHandler(handler);
			tvMainTack.setOnClickListener(this);
			tvMainTack.setVisibility(View.VISIBLE);
			tvMainTack.setPlaytime(DateUtils.getPlayTime(myDiary.getMainPlaytime()), false);
		}
		else
		{
			tvMainTack.setVisibility(View.GONE);
		}
		
		mBtnPraise = (ImageView) v.findViewById(R.id.iv_praise);
		mBtnPraise.setVisibility(View.GONE);
		
		vDiaryInfoTile = v.findViewById(R.id.rl_diarypreview_info);
		tagsLayout = v.findViewById(R.id.ll_diarypreview_tag);
		tvTags[0] = (TextView) v.findViewById(R.id.tv_tag1);
		tvTags[1] = (TextView) v.findViewById(R.id.tv_tag2);
		tvTags[2] = (TextView) v.findViewById(R.id.tv_tag3);
		vPin = (ImageView) v.findViewById(R.id.iv_pin);
		tvPosition = (TextView) mTlParam.findViewById(R.id.tv_diary_position);
		tvPosition.setOnClickListener(this);
		tgbShowGps = (ToggleButton) mTlParam.findViewById(R.id.tgb_show_gps);
		tgbShowGps.setOnCheckedChangeListener(this);

	}
	
	private void initUI() {
		DiaryType diaryType = getDiaryType(myDiary);
		mDiaryLayout.removeAllViews();
		View v = null;
		switch (diaryType) {
		case VEDIO://主体视频
			Log.d(TAG, "主体为视频");
			v = inflater.inflate(
					R.layout.view_diarypreview_video_, mDiaryLayout);
			loadVideoData(v);
			break;
		case AUDIO://主体音频
			Log.d(TAG, "主体为音频");
			v = inflater.inflate(
					R.layout.view_diarypreview_audio_, mDiaryLayout);
			loadAudioData(v);
			break;
		case PICTURE://主体图片
			Log.d(TAG, "主体为图片");
			v = inflater.inflate(
					R.layout.view_diarypreview_picture_, mDiaryLayout);
			loadPictureData(v);
			break;
		case TEXT://主体文字
			Log.d(TAG, "主体为文字");
			v = inflater.inflate(
					R.layout.view_diarypreview_text_, mDiaryLayout);
			loadTextData(v);
			break;
		default:
			break;
		}
		
		// 设置标签
		setTags();
		// 设置位置
		if ("1".equals(myDiary.position_status)) {
			setPosition(true);
			tgbShowGps.setChecked(true);
		} else {
			setPosition(false);
			tgbShowGps.setChecked(false);
		}
	}
	
	public enum DiaryType{
		AUDIO,
		VEDIO,
		PICTURE,
		TEXT,
		NONE
	}

	private static MediaPlayer mp;
	public static class AudioPlayer{
		public static int status = 3;//1-播放中 2-暂停 3-停止
		public static void playAudio(String path,final Handler handler){
			Log.d(TAG, "path="+path);
			if(null==path||0==path.length())return;
			if(2==status)
			{
				if(mp!=null)mp.start();
				status = 1;
			}
			else if (3==status)
			{
				try {
					mp=new MediaPlayer();
					mp.setOnCompletionListener(new OnCompletionListener() {
						
						@Override
						public void onCompletion(MediaPlayer mp) {
							handler.sendEmptyMessage(HANDLER_UPDATE_AUDIO_PLAYER_COMPLETE);

							stopGetPorcessTask();
						}
					});
					mp.setOnErrorListener(new OnErrorListener() {
						
						@Override
						public boolean onError(MediaPlayer mp, int what, int extra) {
							stopGetPorcessTask();
							return false;
						}
					});
					mp.setDataSource(path);
					mp.prepare();
					mp.start();
					status=1;
				} catch (Exception e) {
					stop();
					Prompt.Alert("您的网络不给力呀");
					e.printStackTrace();
				}
			}
			startGetPorcessTask(handler);
		}
		
		public static void pause(){
			if(mp!=null){
				mp.pause();
				status=2;
				stopGetPorcessTask();
			}
		}
		
		public static int getDuration() {
			return mp.getDuration();
		}
		
		public static void stop(){
			if(mp!=null){
				mp.release();
				mp=null;
				stopGetPorcessTask();
			}
			status=3;
		}
	}
	
	private static Timer mTimer;
	private static TimerTask mTimerTask;
	private static void startGetPorcessTask(final Handler handler){
		stopGetPorcessTask();
		mTimer = new Timer();    
		mTimerTask = new TimerTask() {
            @Override    
            public void run() {     
            	try {
					if(mp!=null&&mp.isPlaying()){
						PlayTime pt = new PlayTime();
						pt.total=mp.getDuration();
						pt.current=mp.getCurrentPosition();

						handler.obtainMessage(HANDLER_UPDATE_AUDIO_PLAYER_PROCESS, pt).sendToTarget();
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
            }    
        };   
        mTimer.schedule(mTimerTask, 0, 100);
	}
	
	private static void stopGetPorcessTask(){
		if(mTimer!=null){
			mTimer.cancel();
			mTimer.purge();
			mTimer=null;
		}
	}
	
	// textview中显示文字+表情
		public void replacedExpressions(String expressionText, TextView tv) {

			if(tv == null)
			{
				return;
			}
			
			if(expressionText == null)
			{
				tv.setText("");
				return;
			}
			
			tv.setText(null);
			
			ArrayList<String> list = getTextExpressions(expressionText);
			Log.d("replacedExpressions", "list="+list);
			if (list != null && list.size() > 0) {
				int len = list.size();
				int expStart = 0;
				int expEnd = 0;
				tv.setText(null);
				for (int i = 0; i < len; i++) {
					String exp = list.get(i);
					if(EXPHM.get(exp)!=null){
						int expSrc=EXPHM.get(exp);
						expEnd = expressionText.indexOf(exp, expStart);
						exp=exp.replace("[", "");
						exp=exp.replace("]", "");
						expressionText = expressionText.replaceFirst("\\[" + exp
								+ "\\]", "");
						tv.append(expressionText, expStart, expEnd);
						tv.append(Html.fromHtml("<img src='" +expSrc + "'/>",
								imageGetter, null));
						expStart = expEnd;
					}else{
						expStart = 0;
						expEnd = 0;
					}
				}
				tv.append(expressionText, expStart, expressionText.length());
			} else {
				Log.d("replacedExpressions", "expressionText="+expressionText);
				Log.d("replacedExpressions", "tv.getText()="+tv.getText());
				tv.append(expressionText);
			}
		}
		
		// TextView的getter
		private ImageGetter imageGetter = new ImageGetter() {

			@Override
			public Drawable getDrawable(String source) {
				int id = Integer.parseInt(source);
				Drawable drawable = getResources().getDrawable(id);
				drawable.setBounds(0, 0, drawable.getIntrinsicWidth() / 2,
						drawable.getIntrinsicHeight() / 2);
				return drawable;
			}
		};
		
		// 取出edittext字符串中的表情字段
		private ArrayList<String> getTextExpressions(String expressionText) {
			if (expressionText != null && expressionText.length() > 0) {
				ArrayList<String> list = new ArrayList<String>();
//				Pattern pattern = Pattern.compile("\\[[\\u4e00-\\u9fa5]*?\\]");
				Pattern pattern = Pattern.compile("\\[[\\u4e00-\\u9fa5a-zA-Z]*?\\]");
				Matcher matcher = pattern.matcher(expressionText);
				while (matcher.find()) {
					list.add(matcher.group());
				}
				return list;
			}
			return null;
		}
	
	private class VideoOnInfoListener implements OnInfoListener {

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
			handler.obtainMessage(HANDLER_UPDATE_VIDEO_PLAYER_COMPLETE).sendToTarget();
		}

		@Override
		public void onUpdateTime(XMediaPlayer player, double time) {
			Log.i(TAG, "[onUpdateTime] time:"+time);
			PlayTime pt = new PlayTime();
			pt.total= (int) (player.getTotalTime() * 1000);
			pt.current= (int) (time * 1000);
			
			handler.obtainMessage(HANDLER_UPDATE_VIDEO_PLAYER_PROCESS, pt).sendToTarget();
		}

		@Override
		public void onSurfaceCreated(XMediaPlayer player) {
			
		}

		@Override
		public void onPreparedPlayer(XMediaPlayer player) {
			if(mMediaPlayer.getStatus() == XEffectMediaPlayer.STATUS_OPENED)
			{
				mTotlaTime = player.getTotalTime();
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
	
	//设置播放按钮状态
	private void setPlayBtnStatus(boolean show){
		if(mBtnVideoPlay != null && mPlayProcess != null)
		{
			if(show){
				mBtnVideoPlay.setVisibility(View.VISIBLE);
//				mPlayProcess.setVisibility(View.VISIBLE);
//				if(mTotlaTime != 0d)
//				{
//					tvPlayTime.setVisibility(View.VISIBLE);
//				}
			}
			else
			{
				mBtnVideoPlay.setVisibility(View.INVISIBLE);
//				mPlayProcess.setVisibility(View.INVISIBLE);
//				tvPlayTime.setVisibility(View.INVISIBLE);
			}
		}
	}
	
	Animation leftanim;
	Animation rightanim;

	// 开始播放动画
	private void startAudioPlayAnimation()
	{
		if(mIvLeftWheel !=null &&
				mIvRightWheel !=null)
		{
			LinearInterpolator lir = new LinearInterpolator();  
			leftanim = AnimationUtils.loadAnimation(this, R.anim.audio_leftwheel_bg_animation);
			rightanim = AnimationUtils.loadAnimation(this, R.anim.audio_rightwheel_bg_animation);
			leftanim.setInterpolator(lir);
			rightanim.setInterpolator(lir);
			mIvLeftWheel.startAnimation(leftanim);
			mIvRightWheel.startAnimation(rightanim);
			mBtnAudioPlay.setImageResource(R.drawable.btn_diarypreview_audio_pause_small);
		}
	}
	
	// 停止播放动画
	private void stopAudioPlayAnimation()
	{
		if(mIvLeftWheel !=null &&
				mIvRightWheel !=null)
		{
			mIvLeftWheel.clearAnimation();
			mIvRightWheel.clearAnimation();
			mBtnAudioPlay.setImageResource(R.drawable.btn_diarypreview_audio_play_small);
		}
		if(mPlayProcess!=null)
		{
			mPlayProcess.setProgress(0);
		}
	}
	
	// 暂停播放动画
	private void pauseAudioPlayAnimation()
	{
		if(mIvLeftWheel !=null &&
				mIvRightWheel !=null)
		{
			mIvLeftWheel.clearAnimation();
			mIvRightWheel.clearAnimation();
			mBtnAudioPlay.setImageResource(R.drawable.btn_diarypreview_audio_play_small);
		}
	}
	
	// 获取缩略图Url
	private String getThumbUrl()
	{
		int show_width = 0;
		int show_height = 0;
		String imageUrl = null;
		
		String type = myDiary.getDiaryMainType();
		if ("3".equals(type))
		{ // 只有图片
			imageUrl = myDiary.getMainUrl();
		}
		else if ("1".equals(type))
		{ // 视频
			imageUrl = myDiary.getVideoCoverUrl();
		}

//		if (imageUrl != null && imageUrl.length() > 0 && imageUrl.startsWith("http"))
//		{
//			if (DateUtils.isNum(myDiary.attachs.show_width))
//				show_width = Integer.parseInt(myDiary.attachs.show_width);
//			if (DateUtils.isNum(myDiary.attachs.show_height))
//				show_height = Integer.parseInt(myDiary.attachs.show_height);
//			imageUrl += "&width=" + show_width + "&height=" + show_height;
//		}
		/*
		 * else if(imageUrl!=null&&imageUrl.length()>0){ imageUrl =
		 * diary.attachs.videocover;
		 * 
		 * }
		 */
//		Log.d(TAG, "show_width=" + show_width);
//		Log.d(TAG, "show_heigh=" + show_height);
		Log.d(TAG, "imageUrl=" + imageUrl);
		return imageUrl;
	}
	
	// 判断是否有网络
	public static boolean isNetworkConnected(Context context, String url, int type)
	{
		String uid = ActiveAccount.getInstance(MainApplication.getAppInstance()).getUID();
		MediaValue mv = AccountInfo.getInstance(uid).mediamapping.getMedia(uid, url);
		if (MediaValue.checkMediaAvailable(mv, type))
		{
			return true;
		}
		else
		{
			ConnectivityManager mConnectivityManager = (ConnectivityManager) context
					.getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();
			if (mNetworkInfo != null)
			{
				return mNetworkInfo.isAvailable();
			}
			return false;
		}
	}
	
	// 获取已下载的日记附件大小
	public boolean isDiaryDownload()
	{
		String userID = ActiveAccount.getInstance(ZApplication.getInstance()).getUID();
		
		String videoUrl = myDiary.getMainUrl();
//		if (videoUrl != null)
//		{
//			MediaValue mediaValue = AccountInfo.getInstance(userID).mediamapping.getMedia(userID, videoUrl);
//			if (!MediaValue.checkMediaAvailable(mediaValue, 5))
//			{
//				return false;
//			}
//		}
		String videoPath = getVideoPath(videoUrl);
		if(videoUrl != null && videoPath.startsWith("http"))
		{
			return false;
		}

		String longRecUrl = myDiary.getMainUrl();
		if (longRecUrl != null)
		{
			MediaValue mediaValue = AccountInfo.getInstance(userID).mediamapping.getMedia(userID, longRecUrl);
			if (!MediaValue.checkMediaAvailable(mediaValue, 4))
			{
				return false;
			}
		}

		String imageUrl = myDiary.getMainUrl();
		if (imageUrl != null)
		{
			MediaValue mediaValue = AccountInfo.getInstance(userID).mediamapping.getMedia(userID, imageUrl);
			if (!MediaValue.checkMediaAvailable(mediaValue, 2))
			{
				return false;
			}
		}

//		String shortRecUrl = getShortRecUrl();
//		if (shortRecUrl != null)
//		{
//			MediaValue mediaValue = AccountInfo.getInstance(userID).mediamapping.getMedia(userID, shortRecUrl);
//			if (!MediaValue.checkMediaAvailable(mediaValue, 3))
//			{
//				return false;
//			}
//		}
		return true;
	}
	
//	// 日记附件存在时 MediaValue中的方法长度始终未0始终返回false
//	public static boolean checkMediaAvailable(MediaValue mv, int type){
//		if(mv == null/* || mv.UID==null ||mv.path==null || mv.totalSize<=0|| mv.realSize < mv.totalSize || mv.MediaType!=type*/){
//			return false;
//		}
//		
//		File file = new File(Environment.getExternalStorageDirectory() + mv.path);
//
//		return file.exists() && file.isFile()/* && (file.length()==mv.realSize)*/;
//
//	}

	/**
	 * @description 获取统计字符串
	 * @param context 
	 * @param userId 
	 * @param gps 纬度:经度
	 * @param contentId 文件的ID
	 * @param contentType 1:视频2.广告 3.语音 4.语音评论
	 * @param pageType 1.首页 2.分类列表页 3.频道列表页 4.内容详情页
	 * @return
	 */
	public static String getStatisString(Context context, String userId, String gps,
			String contentId, String contentType, String pageType)
	{
		String url = "";
		url += "?os="+Requester3.VALUE_DEVICE_TYPE;
		url += "&browsetype=";
		url += "&cn=0";
		url += "&source=3";
		url += "&internettype="+getNetWork1(context);
		url += "&clientversion="+getVersionName(context);
		url += "&userid="+userId;
		url += "&gps="+gps;
		url += "&contentid="+contentId;
		url += "&imei="+Requester3.VALUE_IMEI;
		url += "&imsi="+Requester3.VALUE_IMSI;
		url += "&mobiletype="+ZSimCardInfo.getDeviceName();
		url += "&channelid=";
		url += "&ip="+ZSimCardInfo.getDeviceIP();
		url += "&contenttype="+contentType;
		url += "&pagetype="+pageType;
		return url;
	}
	
	
	// NETWORK_TYPE_EVDO_A是中国电信3G的getNetworkType
	// 电信2G是 NETWORK_TYPE_CDMA
	// 移动2G卡 2 NETWORK_TYPE_EDGE
	// 联通的2G 1 NETWORK_TYPE_GPRS
//	private static String getNetWork(Context context)
//	{
//		ConnectivityManager conMan = (ConnectivityManager) context
//				.getSystemService(Context.CONNECTIVITY_SERVICE);
//		TelephonyManager mTelephony = (TelephonyManager) context.getSystemService(context.TELEPHONY_SERVICE); // 检查网络连接，如果无网络可用，就不需要进行连网操作等
//		NetworkInfo info = conMan.getActiveNetworkInfo();
//		if (info == null || !conMan.getBackgroundDataSetting())
//		{
//			return "none";
//		}
//		// 判断网络连接类型，只有在2G/3G/wifi里进行一些数据更新。
//		int netType = info.getType();
//		int netSubtype = info.getSubtype();
//		if (netType == ConnectivityManager.TYPE_WIFI)
//		{
//			return "wifi";
//		}
//		else if (netType == ConnectivityManager.TYPE_MOBILE && netSubtype == TelephonyManager.NETWORK_TYPE_UMTS
//				&& !mTelephony.isNetworkRoaming())
//		{
//			return "3g";
//		}
//		else if (netSubtype == TelephonyManager.NETWORK_TYPE_GPRS || netSubtype == TelephonyManager.NETWORK_TYPE_CDMA
//				|| netSubtype == TelephonyManager.NETWORK_TYPE_EDGE)
//		{
//			return "2g";
//		}
//		else
//		{
//			return "unknow";
//		}
//	}
	private static String getNetWork1(Context context)
	{
		ConnectivityManager conMan = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		//wifi
		State wifi = conMan.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState();
		 //mobile 3G Data Network
        State mobile = conMan.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState();
        
        if(wifi==State.CONNECTED||wifi==State.CONNECTING)
        {
        	return "wifi";
        }
        if(mobile==State.CONNECTED||mobile==State.CONNECTING)
        {
        	int sub = conMan.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getSubtype();
        	switch(sub)
        	{
        	case TelephonyManager.NETWORK_TYPE_UMTS:
        		return "3g";
    		default:
    			return "2g";
        	}
        }
        return "none";
        
//        startActivity(new Intent(Settings.ACTION_WIRELESS_SETTINGS));//进入无线网络配置界面
//        startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS)); //进入手机中的wifi网络设置界面
	}
	
	private static String getVersionName(Context context)
	{
		// 获取packagemanager的实例
		PackageManager packageManager = context.getPackageManager();
		// getPackageName()是你当前类的包名，0代表是获取版本信息
		PackageInfo packInfo;
		String version = "";
		try
		{
			packInfo = packageManager.getPackageInfo(context.getPackageName(),0);
			version = packInfo.versionName;
			if (version != null && version.length() > 0)
			{
				return version;
			}
		}
		catch (NameNotFoundException e)
		{
			e.printStackTrace();
		}
		return version;
	}
	
	// 根据类型获取diary的attachid 1视频、2音频、3图片、4文字
	private String getMediaId(MyDiary diary, String type)
	{
		return diary.attachs.levelattach.attachid;
	}
	
	private void setMenu()
	{
    	LayoutInflater inflater = LayoutInflater.from(this);
    	View v = null;
    	
		DiaryType diaryType = getDiaryType(myDiary);
		switch (diaryType) {
		case VEDIO:
    		v = inflater.inflate(R.layout.view_diaryedit_video, null);
    		if (isFromLongShootActivity) {
    			v.findViewById(R.id.iv_jianji).setOnClickListener(this);
    		} else {
    			
    			String str = myDiary.getMainPlaytime();
    			long l = 0l;
    			try
				{
    				l = Long.parseLong(str);
				}
				catch (Exception e)
				{
					e.printStackTrace();
				}
    			
    			if (l <= 8)
    			{
    				v.findViewById(R.id.iv_jianji).setVisibility(View.GONE);
    			}
    			else
    			{
    				v.findViewById(R.id.iv_jianji).setOnClickListener(this);
    			}
    		}
    		
    		v.findViewById(R.id.iv_texiao).setOnClickListener(this);
    		v.findViewById(R.id.iv_peiyue).setOnClickListener(this);
    		v.findViewById(R.id.iv_jiabiaoqian).setOnClickListener(this);
    		tvTagsNum = (TextView)v.findViewById(R.id.tv_jiabiaoqian);
			break;
		case AUDIO:
			break;
		case PICTURE:
    		v = inflater.inflate(R.layout.view_diaryedit_picture, null);
    		v.findViewById(R.id.iv_caijian).setOnClickListener(this);
    		v.findViewById(R.id.iv_texiao).setOnClickListener(this);
    		v.findViewById(R.id.iv_jiabiaoqian).setOnClickListener(this);
    		tvTagsNum = (TextView)v.findViewById(R.id.tv_jiabiaoqian);
			break;
		case TEXT:
			break;
		default:
			break;
		}
		
		mMenu = (LinearLayout) findViewById(R.id.ll_diaryedit_menu);
		mMenu.removeAllViews();
		mMenu.addView(v);
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		if (resultCode == RESULT_OK) {
			
			boolean isMainAttachModify = false;
			
			switch (requestCode) {
			case REQUEST_TAG:
				isAuxAttachModifyed = true;
				List<String> tagIds = data.getStringArrayListExtra("tagids");
				myDiary.tags = TagSelectedActivity.getDiaryTag(tagIds);
				break;
			case REQUEST_POSITION:
				if (data != null) {
					isAuxAttachModifyed = true;
					String positionStr = data.getStringExtra("position");
					String longitude = data.getStringExtra("longitude");
					String latitude = data.getStringExtra("latitude");
					POIAddressInfo addInfo = new POIAddressInfo();
					addInfo.position = positionStr;
					addInfo.longitude = longitude;
					addInfo.latitude = latitude;
					Log.d(TAG,"freqPosList = " + positionStr + " longitude = " + longitude + " latitude = " + latitude);
					
					if (positionStr == null) {
						break;
					}
					
					if (positionStr.equals(getString(R.string.position_not_visiable))) {
						tgbShowGps.setChecked(false);
						setPosition(false);
						myDiary.position_status = "0";
						break;
					}
					
					myDiary.position_view = positionStr;
					myDiary.longitude_view = longitude;
					myDiary.latitude_view = latitude;
					
					tgbShowGps.setChecked(true);
					setPosition(true);
					
					List<POIAddressInfo> freqPosList = CommonInfo.getInstance().frequentpos;
					
					if (freqPosList.contains(addInfo)) {
						Log.d(TAG,"onActivityResult if");
						freqPosList.remove(addInfo);
						freqPosList.add(0, addInfo);
					} else {
						Log.d(TAG,"onActivityResult else " + positionStr);
						freqPosList.add(0,addInfo);
					}
				}
				break;
			case REQUEST_PHOTO:
			case REQUEST_AUDIO:
			case REQUEST_VIDEO:
				String mediaPath = data.getStringExtra(INTENT_ACTION_MEDIA_PATH);
				if (mediaPath != null) {
					if (!mediaPath.equals(diaryEditNote.mediaPath)) {
						if (!diaryEditNote.mediaPath.equals(originalDiary.getMainPath())) {
							ZFileSystem.delFile(diaryEditNote.mediaPath);
						}
						diaryEditNote.isMontaged = true;
						diaryEditNote.mediaPath = mediaPath;
					}
				}
				isMainAttachModify = true;
				isMainAttachModifyed = true;
				break;
			case REQUEST_NOTE:
			case REQUEST_PHOTO_EFFECT:
			case REQUEST_VIDEO_EFFECT:
				int effectId = data.getIntExtra(INTENT_ACTION_MEDIA_EFFECT, 0);
				if (effectId != 0) {
					diaryEditNote.isEffect = true;
					diaryEditNote.effectIndex = effectId;
				} else {
					diaryEditNote.isEffect = false;
					diaryEditNote.effectIndex = effectId;
				}
				isMainAttachModify = true;
				isMainAttachModifyed = true;
				break;
			case REQUEST_VIDEO_SOUNDTRACK:
			case REQUEST_AUDIO_SOUNDTRACK:
				String soundTrackPath = data.getStringExtra(INTENT_ACTION_SOUNDTRACK);
				double percent = data.getDoubleExtra(INTENT_ACTION_SOUND_PERCENT, 0.5);
				if (soundTrackPath != null && !"原音".equals(soundTrackPath)) {
					diaryEditNote.isAddSoundTrack = true;
					diaryEditNote.soundtrackPath = soundTrackPath;
					diaryEditNote.percent = percent;
				} else {
					diaryEditNote.isAddSoundTrack = false;
					diaryEditNote.soundtrackPath = null;
				}
				isMainAttachModify = true;
				isMainAttachModifyed = true;
				break;
			case REQUEST_OTHER:
				break;
			case SETTING_PERSONAL_INFO:
				gotoShareActivity(false);
				return;
			}
			if (requestCode != REQUEST_POSITION && requestCode != REQUEST_TAG) {
				String diary = data.getStringExtra(INTENT_ACTION_DIARY_STRING);
				myDiary = new Gson().fromJson(diary, MyDiary.class);
				if (myDiary.getMainPath() != null) {
					cachePathSet.add(myDiary.getMainPath());
				}
				
				if (myDiary.getVideoCoverPath() != null) {
					cacheVideoCoverSet.add(myDiary.getVideoCoverPath());
				}
			}
			
			if (tvTagsNum != null) {
				int tagSize = myDiary.getTagSize();
				if (tagSize > 0) {
					tvTagsNum.setVisibility(View.VISIBLE);
					tvTagsNum.setText(String.valueOf(tagSize));
				} else {
					tvTagsNum.setVisibility(View.GONE);
				}
			} 
			
			/*if (isTagOrPosChanged() || isCoverChanged()) {
				mBtnDone.setEnabled(true);
			} else {
				if (myDiary.isNoteDiary()) {
					if (diaryEditNote.isEffect || (myDiary.getMainTextContent() != null && !myDiary.getMainTextContent().equals(originalDiary.getMainTextContent()))) {
						mBtnDone.setEnabled(true);
					} else {
						mBtnDone.setEnabled(false);
					}
				} else {
					if (diaryEditNote.isAddSoundTrack || diaryEditNote.isEffect || diaryEditNote.isMontaged) {
						mBtnDone.setEnabled(true);
					} else {
						mBtnDone.setEnabled(false);
					}
				}
			}*/
			mBtnDone.setEnabled(isDoneBtnEnabled());
			
			if (isMainAttachModify) {
				myDiary.publish_status = "1";
			}
			
			initUI();
			
		}
		super.onActivityResult(requestCode, resultCode, data);
	}
	
	private ArrayList<String> getDiaryTagStrs(MyDiary diary) {
		ArrayList<String> tagIds = new ArrayList<String>();
		if (diary.tags != null) {
			List<TAG> tagList = Arrays.asList(diary.tags);
			for (TAG tag:tagList) {
				tagIds.add(tag.id);
			}
		}
		return tagIds;
	}
	
	public boolean isDoneBtnEnabled() {
		boolean ret = false;
		if (isTagOrPosChanged() || isCoverChanged()) {
			ret = true;
		} else {
			if (myDiary.isNoteDiary()) {
				if (diaryEditNote.isEffect || (myDiary.getMainTextContent() != null && !myDiary.getMainTextContent().equals(originalDiary.getMainTextContent()))) {
					ret = true;
				} else {
					ret = false;
				}
			} else {
				if (diaryEditNote.isAddSoundTrack || diaryEditNote.isEffect || diaryEditNote.isMontaged) {
					ret = true;
				} else {
					ret = false;
				}
			}
		}
		return ret;
	}
	
	private boolean isTagOrPosChanged() {
		if (!ZStringUtils.nullToEmpty(myDiary.getTagIds()).equals(ZStringUtils.nullToEmpty(originalDiary.getTagIds())) 
				|| !ZStringUtils.nullToEmpty(myDiary.position_view).equals(ZStringUtils.nullToEmpty(originalDiary.position_view))
				|| !ZStringUtils.nullToEmpty(myDiary.position_status).equals(ZStringUtils.nullToEmpty(originalDiary.position_status))) {
			return true;
		}
		return false;
	}
	
	private boolean isCoverChanged() {
		if ("1".equals(myDiary.getDiaryMainType()) && !ZStringUtils.nullToEmpty(myDiary.getVideoCoverPath()).equals(ZStringUtils.nullToEmpty(originalDiary.getVideoCoverPath()))) {
			return true;
		}
		return false;
	}
	
	@Override
	public boolean onLongClick(View v)
	{
		switch(v.getId()) {
		case R.id.ib_title_back:
			Intent intent = new Intent(this,LookLookActivity.class);
			intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(intent);
			finish();
			break;
		}
		return false;
	}
	
	//设置位置
	private void setPosition(boolean isChecked){
		Log.d(TAG, "setPosition isChecked = " + isChecked);
		if(isChecked)
		{
			tvPosition.setText(myDiary.position_view);
		}
		else
		{
			tvPosition.setText("");
		}
	}
	
	//设置标签
	private void setTags()
	{
		Log.d(TAG, "setTags");
		if (myDiary.tags != null && myDiary.tags.length > 0)
		{
			tagsLayout.setVisibility(View.VISIBLE);
			int length = myDiary.tags.length;
			for (int i = 0; i < 3; i++)
			{
				if (i < length)
				{
					tvTags[i].setVisibility(View.VISIBLE);
					tvTags[i].setText(myDiary.tags[i].name);
				}
				else
				{
					tvTags[i].setVisibility(View.GONE);
				}
			}
		}
		else
		{
			// 隐藏标签布局
			tagsLayout.setVisibility(View.INVISIBLE);
		}
	}

	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
	{
		Log.d(TAG,"onCheckedChanged isChecked = " + isChecked);
		
		setPosition(isChecked);
		// 2014-4-8
		if (isChecked) {
			myDiary.position_status = "1";
		} else {
			myDiary.position_status = "0";
		}
		mBtnDone.setEnabled(isDoneBtnEnabled());
		if (isDoneBtnEnabled()) {
			isAuxAttachModifyed = true;
		} else {
			isAuxAttachModifyed = false;
		}
	}
	
	private BroadcastReceiver mReceiver = new BroadcastReceiver()
	{

		@Override
		public void onReceive(Context context, Intent intent)
		{
			String action = intent.getAction();
			Log.d("mydiary", "myReceiver->action=" + action);
			if (DiaryPreviewActivity.DIARY_EDIT_REFRESH.equals(action))
			{
				String UUID = intent.getStringExtra(INTENT_ACTION_DIARY_UUID);
				MyDiary diary = DiaryManager.getInstance().findMyDiaryByUUID(UUID);
				if (myDiary.getMainPath() == null) {
					myDiary.setMainUrl(diary.getMainUrl());
				}
				
				if (originalDiary.getMainPath() == null) {
					originalDiary.setMainUrl(diary.getMainUrl());
				}
				
				if (!ZFileSystem.isFileExists(diaryEditNote.mediaPath)) {
					diaryEditNote.mediaPath = originalDiary.getMainPath();
				}
				
				if (myDiary.isVideoDiary() && myDiary.getVideoCoverPath() == null) {
					myDiary.setVideoCoverUrl(diary.getVideoCoverUrl());
				}
				
				if (originalDiary.isVideoDiary() && originalDiary.getVideoCoverPath() == null) {
					originalDiary.setVideoCoverUrl(diary.getVideoCoverUrl());
				}
//				originalDiary = new Gson().fromJson(myDiary, MyDiary.class);
//				String originalDiaryStr = new Gson().toJson(myDiary);
//				originalDiary = new Gson().fromJson(originalDiaryStr, MyDiary.class);
//				
//				myDiary = new Gson().fromJson(originalDiaryStr, MyDiary.class); 
			}
		}
	};
	
}
