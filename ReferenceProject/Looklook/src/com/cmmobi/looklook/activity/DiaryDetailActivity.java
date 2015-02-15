package com.cmmobi.looklook.activity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
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
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.ColorDrawable;
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
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.telephony.TelephonyManager;
import android.text.Html;
import android.text.Html.ImageGetter;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.View.OnKeyListener;
import android.view.View.OnLongClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import cn.zipper.framwork.core.ZActivity;
import cn.zipper.framwork.core.ZApplication;
import cn.zipper.framwork.core.ZDialog;
import cn.zipper.framwork.device.ZSimCardInfo;

import com.cmmobi.looklook.MainApplication;
import com.cmmobi.looklook.R;
import com.cmmobi.looklook.activity.HomepageMyselfDiaryActivity.ViewHolder;
import com.cmmobi.looklook.common.constant.Constant;
import com.cmmobi.looklook.common.gson.GsonRequest2;
import com.cmmobi.looklook.common.gson.GsonResponse2;
import com.cmmobi.looklook.common.gson.GsonResponse2.MyAttachAudio;
import com.cmmobi.looklook.common.gson.GsonResponse2.MyAttachImage;
import com.cmmobi.looklook.common.gson.GsonResponse2.MyAttachVideo;
import com.cmmobi.looklook.common.gson.GsonResponse2.MyBind;
import com.cmmobi.looklook.common.gson.GsonResponse2.MyDiary;
import com.cmmobi.looklook.common.gson.GsonResponse2.ShareSNSTrace;
import com.cmmobi.looklook.common.gson.GsonResponse2.addCollectDiaryResponse;
import com.cmmobi.looklook.common.gson.GsonResponse2.createStructureResponse;
import com.cmmobi.looklook.common.gson.GsonResponse2.deleteDiaryResponse;
import com.cmmobi.looklook.common.gson.GsonResponse2.deletepublishAndEnjoyResponse;
import com.cmmobi.looklook.common.gson.GsonResponse2.diaryAttach;
import com.cmmobi.looklook.common.gson.GsonResponse2.diaryInfoResponse;
import com.cmmobi.looklook.common.gson.GsonResponse2.diaryPublishResponse;
import com.cmmobi.looklook.common.gson.GsonResponse2.enjoyResponse;
import com.cmmobi.looklook.common.gson.GsonResponse2.getDiaryUrlResponse;
import com.cmmobi.looklook.common.gson.GsonResponse2.platformUrls;
import com.cmmobi.looklook.common.gson.GsonResponse2.removeCollectDiaryResponse;
import com.cmmobi.looklook.common.gson.GsonResponse2.reportResponse;
import com.cmmobi.looklook.common.gson.GsonResponse2.safeboxResponse;
import com.cmmobi.looklook.common.gson.GsonResponse2.setMoodResponse;
import com.cmmobi.looklook.common.gson.Requester2;
import com.cmmobi.looklook.common.gson.WeiboRequester;
import com.cmmobi.looklook.common.gson.WeiboResponse.RWUserInfo;
import com.cmmobi.looklook.common.gson.WeiboResponse.SWUserInfo;
import com.cmmobi.looklook.common.gson.WeiboResponse.TWUserInfo;
import com.cmmobi.looklook.common.listener.DiaryPagerTouchInterface;
import com.cmmobi.looklook.common.listener.MulitPointTouchListener;
import com.cmmobi.looklook.common.listview.pulltorefresh.DiaryListView;
import com.cmmobi.looklook.common.storage.GsonHelper;
import com.cmmobi.looklook.common.utils.CmmobiClickAgentWrapper;
import com.cmmobi.looklook.common.utils.DateUtils;
import com.cmmobi.looklook.common.utils.UmengclickAgentWrapper;
import com.cmmobi.looklook.common.view.CountDownView;
import com.cmmobi.looklook.common.view.DiaryDetailMenuView;
import com.cmmobi.looklook.common.view.DiaryDetailPager;
import com.cmmobi.looklook.common.view.Xdialog;
import com.cmmobi.looklook.common.web.TackView;
import com.cmmobi.looklook.common.web.WebImageView;
import com.cmmobi.looklook.info.profile.AccountInfo;
import com.cmmobi.looklook.info.profile.ActiveAccount;
import com.cmmobi.looklook.info.profile.DiaryManager;
import com.cmmobi.looklook.info.profile.DiaryManager.FilterType;
import com.cmmobi.looklook.info.profile.LoginSettingManager;
import com.cmmobi.looklook.info.profile.MediaValue;
import com.cmmobi.looklook.info.profile.NetworkTaskInfo;
import com.cmmobi.looklook.info.profile.TimeHelper;
import com.cmmobi.looklook.networktask.CacheNetworkTask;
import com.cmmobi.looklook.networktask.DownloadNetworkTask;
import com.cmmobi.looklook.networktask.INetworkTask;
import com.cmmobi.looklook.networktask.NetworkTaskManager;
import com.cmmobi.looklook.networktask.UploadNetworkTask;
import com.cmmobi.looklook.offlinetask.OfflineTaskManager;
import com.cmmobi.looklook.prompt.Prompt;
import com.cmmobi.sns.api.CmmobiSnsLib;
import com.cmmobi.sns.oauthv2.OAuthV2;
import com.cmmobi.sns.utils.ConfigUtil.SHARE_TO;
import com.cmmobivideo.utils.PluginUtils;
import com.cmmobivideo.workers.XMediaPlayer;
import com.cmmobivideo.workers.XMediaPlayer.OnInfoListener;
import com.google.gson.Gson;
import com.iflytek.msc.ExtAudioRecorder;
import com.iflytek.msc.QISR_TASK;
import com.nostra13.universalimageloader.api.AnimateFirstDisplayListener;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageLoadingListener;
import com.nostra13.universalimageloader.core.display.SimpleBitmapDisplayer;
import com.tencent.mm.sdk.openapi.BaseReq;
import com.tencent.mm.sdk.openapi.BaseResp;
import com.tencent.mm.sdk.openapi.ConstantsAPI;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;
import com.weibo.sdk.android.api.WeiboAuthListener;
import com.weibo.sdk.android.api.WeiboDialogError;
import com.weibo.sdk.android.api.WeiboException;

import effect.XEffectMediaPlayer;
import effect.XEffects;

/**
 * @author wuxiang
 * @email wuxiang@cmmobi.com
 * @date 2013-6-6
 */
public class DiaryDetailActivity extends ZActivity implements
		OnPageChangeListener, OnItemClickListener, OnTouchListener, OnLongClickListener, IWXAPIEventHandler{

	private static final String TAG = "DiaryDetailActivity";

	public static final String INTENT_ACTION_DIARY_ID = "intent_action_diary_id";
	public static final String INTENT_ACTION_DIARY_UUID = "intent_action_diary_uuid";
	public static final String INTENT_ACTION_DIARY_STRING = "intent_action_diary_string";
	public static final String INTENT_ACTION_SHARE_TYPE = "intent_action_diary_string";
	public static final String INTENT_ACTION_SHOW = "intent_action_show";
	public static final String INTENT_ACTION_HIDDEN = "intent_action_hidden";
	public static final String INTENT_ACTION_SCREEN_MODE = "intent_action_screen_mode";
	public static final String INTENT_ACTION_SHARE_SUCCESS = "intent_action_share_success";
	public static final String INTENT_ACTION_DELETE_SNS = "intent_action_delete_sns";
	public static final String INTENT_ACTION_PRAISE_CHANGE = "intent_action_praise_change";
	
	public static final int HANDLER_UPDATE_VIDEO_PLAYER_PROCESS = 0x0010;
	public static final int HANDLER_UPDATE_VIDEO_PLAYER_COMPLETE = 0x0011;
	public static final int HANDLER_UPDATE_LONG_RECORD_PLAYER_PROCESS = 0x0012;
	public static final int HANDLER_UPDATE_LONG_RECORD_PLAYER_COMPLETE = 0x0013;
//	public static final int HANDLER_PLAY_VIDEO = 0x0012;
//	public static final int HANDLER_PLAY_AUDIO = 0x0013;
	public static final int HANDLER_AUTO_HIDE_CONTROLLER = 0x0014;
	public static final int HANDLER_UPDATE_VIDEO_TIME_STRING = 0x0015;
	public static final int HANDLER_UPDATE_VIDEO_ERROR = 0x0016;
	public static final int HANDLER_UPDATE_VIDEO_PREPARED = 0x0017;
	public static final int HANDLER_SHORT_RECORD_STOP_TIME_DELAY = 0x87654017;
	
	public static final String INTENT_EXTRA_DIARY_FILTER = "intent_extra_diary_filter";
	
//	public static final String INTENT_EXTRA_PLAY_URL = "intent_extra_play_url";
//	public static final String INTENT_EXTRA_PLAY_TOTALTIME = "intent_extra_play_totaltime";
//	public static final String INTENT_EXTRA_PLAY_CURTIME = "intent_extra_play_curtime";
//	public static final String INTENT_EXTRA_PLAY_THUMB = "intent_extra_play_thumb";
	public static final String INTENT_EXTRA_DELETE_SNS = "intent_extra_delete_sns";
	
	private static final int REQUESTCODE_DETAIL = 0x05;
	private static final int REQUESTCODE_SNSDELETE = 0x06;
	
	private FilterType eFilterType = null;
	
	// 当前页面所有日记（未排序）
	private ArrayList<MyDiary> mListDiary;
	
	// 排序后用于显示的日记
	private LinkedList<SortDiary> mListDuplicate = new LinkedList<SortDiary>();
	
	// 当前所在页面索引
	private int mPageIndex;

	private ImageView ivShareOrTransfer;
	private LayoutInflater inflater;
	private View rootView;
	private MyDiary myDiary = new MyDiary();
	private ImageView ivComment;
	private TextView tvCommentNum;
	private TextView tvCollectNum;
	// 录音按钮
	private ImageView ivRecord = null;
	private String userID;
	private AccountInfo accountInfo;
	private DiaryManager mDiaryManager;

	private DiaryDetailPager  mDiarydetail_viewpager;
//	private View picPage, audioPage, videoPage, textPage, yuyinPage;
	private List<View> mDiarydetail_views;

	private ImageView ivActvie, ivEdit;
	private ListView listMoods;// 心情列表
	// 赞按钮动画
	private FrameLayout mFlPraise = null;
	private ImageView mIvPraiseBg = null;
	private ImageView mIvPraisesrc = null;
	private AnimationDrawable mPraiseBgAnim = null;
	private boolean isActivityStoped = false;

	// 赞按钮类型
	private enum EPraiseType
	{
		PRAISE,
		UNPRAISE,
		STARTANIM;
	}
	
	// 视频播报状态
	public enum EPlayStatus
	{
		NON,
		OPENING,
		OPENED,
		PLAY,
		PAUSE;
		
		double seekPosition = 0d;
	}
	
	private class SortDiary
	{
		String diaryID = null;
		String diaryUUID = null;
		String name = null;
	}
	
	private EPlayStatus ePlayStatus = EPlayStatus.NON;
	
	private PopupWindow mPopupWindow;
	public static final int REQUEST_CODE = 1;
	private TextView tvDiaryName;
//	private ImageView ivXiala;// 下拉箭头
	
	private TextView[] tvTags=new TextView[3];
	private View tagsLayout;
	
	private View vPin;//位置图标
	private TextView tvPosition;//位置信息
	
	//长录音播放进度
	private SeekBar playAudioProcess;
	private SeekBar playVideoProcess;
	private ImageView ivMood;
	
	private WebImageView ivWeather;
	private TextView tvTemperature;
	private TextView tvDiaryTime;
	
	private View vTitlebar;
	private View vDiaryInfoTile;//上面天气、位置、赞等信息layout
	private View vBottombar;
	
	// 微信/微信朋友圈标准 1 微信，2 微信朋友圈， 10短信， 11邮件
	private int mBShareType = 0;
	
	// 视频长度
	private double mTotlaTime = 0d;
	
	private LoginSettingManager settingManager;
	
	MyBind myBind;
	
	//分享id
	private int mShareToId = 0;
	
	//下部弹出菜单
	private DiaryDetailMenuView mShareMenuView;
	private PopupWindow mDiaryMenuMore;
	private PopupWindow mDiaryMenuSelect;
	
	// 微博删除任务队列标准 0 未运行，1 运行中, 2 下一次停止
	private volatile int mFlagOfTaskSina = 0;
	private volatile int mFlagOfTaskRenren = 0;
	private volatile int mFlagOfTaskTencent = 0;
	
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
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.d(TAG, "onCreate");
		Bundle bundle = getIntent().getExtras();
		eFilterType = (FilterType) bundle.get(INTENT_EXTRA_DIARY_FILTER);
		
		userID = ActiveAccount.getInstance(ZApplication.getInstance()).getUID();
		// for test
		// userID="5358e7db0646f04a820bcb20ebc2e7818a70";
		accountInfo = AccountInfo.getInstance(userID);
		mDiaryManager = DiaryManager.getInstance();
		settingManager = accountInfo.setmanager;
		setContentView(R.layout.activity_diarydetail);
		
		SharedPreferences sp=getSharedPreferences(GuideActivity.SP_NAME, MODE_PRIVATE);
		if(sp.getInt(GuideActivity.SP_KEY, -1) == 1){
			Intent intent = new Intent(DiaryDetailActivity.this, GuideActivity.class);
			startActivity(intent);
		}

		//weixin
		WeiboRequester.getInstance(this).registerApp(this);
        WeiboRequester.getInstance(this).handleIntent(this, getIntent(), this);
		
		vTitlebar=findViewById(R.id.rl_title);
		vDiaryInfoTile=findViewById(R.id.ri_activity_weather);
//		vDiaryInfoTile.setOnClickListener(null);
		vDiaryInfoTile.setOnClickListener(this);
		vBottombar=findViewById(R.id.rl_activity_diarydetail_button_bar);
		rootView = findViewById(R.id.rl_activity_detail);
		findViewById(R.id.iv_back).setOnClickListener(this);
		findViewById(R.id.iv_more).setOnClickListener(this);
		findViewById(R.id.ll_diaryname).setOnClickListener(this);
//		findViewById(R.id.iv_play_mode).setOnClickListener(this);
		tagsLayout=findViewById(R.id.ll_biaoqian);
		tvTags[0]=(TextView) findViewById(R.id.tv_tag1);
		tvTags[1]=(TextView) findViewById(R.id.tv_tag2);
		tvTags[2]=(TextView) findViewById(R.id.tv_tag3);
		vPin=findViewById(R.id.iv_pin);
		tvPosition=(TextView) findViewById(R.id.tv_position);
		ivWeather=(WebImageView) findViewById(R.id.iv_weather_icon);
		tvTemperature=(TextView) findViewById(R.id.tv_temperature);
		tvDiaryTime=(TextView) findViewById(R.id.tv_diary_time);
//		ivXiala = (ImageView) findViewById(R.id.iv_xiala);
		tvDiaryName = (TextView) findViewById(R.id.tv_diaryname);
		ivActvie = (ImageView) findViewById(R.id.iv_diarydetail_active);
		ivEdit = (ImageView) findViewById(R.id.iv_diarydetail_edit);
		ivActvie.setOnClickListener(this);
		ivEdit.setOnClickListener(this);
		
		mFlPraise = (FrameLayout) findViewById(R.id.fl_praise);
		mIvPraiseBg = (ImageView) findViewById(R.id.iv_praise_bg);
		mIvPraisesrc = (ImageView) findViewById(R.id.iv_praise_src);
		mPraiseBgAnim = (AnimationDrawable) mIvPraiseBg.getDrawable();
		mFlPraise.setOnClickListener(this);
		
		ivComment = (ImageView) findViewById(R.id.iv_comment);
		tvCommentNum = (TextView) findViewById(R.id.tv_commentnum);
		tvCollectNum = (TextView) findViewById(R.id.tv_collectnum);
		ivComment.setOnClickListener(this);
		ivShareOrTransfer = (ImageView) findViewById(R.id.iv_share);
		ivShareOrTransfer.setOnClickListener(this);
		ivRecord = (ImageView) findViewById(R.id.iv_record);
//		ivRecord.setOnClickListener(this);
		ivRecord.setOnLongClickListener(this);
		ivRecord.setOnTouchListener(this);
		inflater = LayoutInflater.from(this);
		mDiarydetail_viewpager = (DiaryDetailPager) findViewById(R.id.diarydetail_viewpager);
		mDiarydetail_viewpager.setOnPageChangeListener(this);
		mShareMenuView = new DiaryDetailMenuView(this);

		mShareMenuView.setOnclickListener(this, this);
		mDiarydetail_views = new ArrayList<View>();
		mDiarydetail_viewpager.setAdapter(new MyPageAdapter(mDiarydetail_views));
		
		String diaryID = getIntent().getStringExtra(INTENT_ACTION_DIARY_ID);
		String diaryUUID = getIntent().getStringExtra(INTENT_ACTION_DIARY_UUID);
		
		mListDiary = (ArrayList<MyDiary>) DiaryManager.getInstance().getDetailDiaryList().clone();
		DiaryManager.getInstance().getDetailDiaryList().clear();
		
		if(diaryID != null)
		{
			Log.d(TAG, "diaryID="  + diaryID);
			initDataWithID(diaryID);
		}
		else if(mListDiary != null && diaryUUID != null)
		{
			Log.d(TAG, "diaryID=" + diaryUUID);
			Log.d(TAG, "diaryList=" + mListDiary);
			initDataWithUUID(diaryUUID);
		}
		else
		{
			Log.e(TAG, "diaryID && diaryUUID both null");
			finish();
		}

		listMoods = new ListView(this);
		listMoods.setVerticalScrollBarEnabled(false);
		listMoods.setBackgroundResource(R.drawable.xinqing_xiala);
		listMoods.setDivider(null);
		listMoods.setSelector(new ColorDrawable(Color.TRANSPARENT));
		listMoods.setAdapter(new myMoodAdapter());
		listMoods.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1,
					int position, long arg3) {
				if (mPopupWindow != null)
					mPopupWindow.dismiss();
				ivMood.setTag(position);
				Requester2.setMood(handler, myDiary.diaryid,position+"");
			}
		});
		
		ear = ExtAudioRecorder.getInstanse(false, 2, 1);
		
		LocalBroadcastManager.getInstance(this).registerReceiver(
				mMessageReceiver,
				new IntentFilter(ExtAudioRecorder.AUDIO_RECORDER_MSG));
		
		LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver,
			      new IntentFilter(QISR_TASK.QISR_RESULT_MSG));

		deleteSNSTask();
//		setSettingsDefault();
		//注册日记更新广播
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(DiaryManager.ACTION_DIARY_SYNCHRONIZED);
		intentFilter.addAction(DiaryManager.DIARY_EDIT_DONE);
		intentFilter.addAction(DiaryManager.DIARY_LIST_EDIT_DONE);
		intentFilter.addAction(HomepageMyselfDiaryActivity.SAVE_BOX_MODE_CHANGE);
		intentFilter.addAction(INTENT_ACTION_SHOW);
		intentFilter.addAction(INTENT_ACTION_HIDDEN);
		intentFilter.addAction(INTENT_ACTION_SCREEN_MODE);
		intentFilter.addAction(INTENT_ACTION_DELETE_SNS);
		intentFilter.addAction(INTENT_ACTION_SHARE_SUCCESS);
		intentFilter.addAction(INTENT_ACTION_PRAISE_CHANGE);
		LocalBroadcastManager.getInstance(this).registerReceiver(mReceiver, intentFilter);
		
		
//		String videoUrl = getVideoUrl();
//		String longRecUrl = getLongRecUrl();
//		if(videoUrl != null && videoUrl.length() > 0)
//		{
//			stopAutoPlay();
//			handler.sendMessageDelayed(Message.obtain(handler, HANDLER_PLAY_VIDEO, videoUrl), 2000);// 延迟2秒开始播放视屏
//		}
//		else if(longRecUrl != null && longRecUrl.length() > 0)
//		{
//			stopAutoPlay();
//			handler.sendMessageDelayed(Message.obtain(handler, HANDLER_PLAY_AUDIO, longRecUrl),	2000);// 延迟2秒开始播放视屏
//		}
	}

	@Override
	public void onPageScrollStateChanged(int arg0) {
	}

	@Override
	public void onPageScrolled(int arg0, float arg1, int arg2) {
		if (mShareMenuView.getExpressionView().isShown())
		{// 让分享菜单响应返回
			mShareMenuView.getExpressionView().setVisibility(View.GONE);
		}
	}

	@Override
	public void onPageSelected(int index) {
		// 统计日记id
		CmmobiClickAgentWrapper.onEvent(this, "details_contentid", myDiary.diaryid);

		AudioPlayer.stop();
		stopAudioPlayAnimation();
		stopVideo();
		
		mDiarydetail_viewpager.setInterceptListener(null);
		mPageIndex = index;
		setCurrent();
		Log.d(TAG, "onPageSelected->pageIndex=" + mPageIndex);
		
		
//		String videoUrl = getVideoUrl();
//		String longRecUrl = getLongRecUrl();
//		if(videoUrl != null && videoUrl.length() > 0)
//		{
//			stopAutoPlay();
//			handler.sendMessageDelayed(Message.obtain(handler, HANDLER_PLAY_VIDEO, videoUrl), 2000);// 延迟2秒开始播放视屏
//		}
//		else if(longRecUrl != null && longRecUrl.length() > 0)
//		{
//			stopAutoPlay();
//			handler.sendMessageDelayed(Message.obtain(handler, HANDLER_PLAY_AUDIO, longRecUrl),	2000);// 延迟2秒开始播放视屏
//		}
		
		if(index == (mDiarydetail_views.size() - 1) && eFilterType != null)
		{
			//最后一页的时候刷新
			// 先请求本地数据，本地未获取到时请求服务器数据
//			ArrayList<MyDiary> current = getMainDiary();
			ArrayList<MyDiary> current = mListDiary;
			ArrayList<MyDiary> newdiary = (ArrayList<MyDiary>) mDiaryManager.getDiary(eFilterType, userID,
					current.size(), saveboxIsOpen());
			if (newdiary != null && newdiary.size() > 0) {
				current.addAll(newdiary);
				mDiaryManager.setDetailDiaryList(current);
				mListDiary = (ArrayList<MyDiary>) mDiaryManager.getDetailDiaryList().clone();
				DiaryManager.getInstance().getDetailDiaryList().clear();
				if(mListDiary != null)
				{
					initDataWithUUID(myDiary.diaryuuid);
//					setCurrent();
				}
			}
	//		else
	//		{
	//			diaryListView.noMoreData(true);
	//			diaryListView.loadDateError();
	//		}
		}
	}

	@Override
	public void onResume() {
		isActivityStoped = false;
		super.onResume();
		UmengclickAgentWrapper.onResume(this);
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
//		stopVideo();
		if(ivVideoThumbnail != null)
		{
//			ivVideoThumbnail.clearAnimation();
			ivVideoThumbnail.setVisibility(View.VISIBLE);
		}
		
		UmengclickAgentWrapper.onPause(this);
		CmmobiClickAgentWrapper.onPause(this);
	}
	
	@Override
	public void onStop(){
		// 避免冲突
//		stopAutoPlay();
		Log.d(TAG,"onStop");
		if (ear != null && ear.isRecording) {
			isLongClick = false;
			endRecTime = TimeHelper.getInstance().now();
			recordDuration = 0;
			ear.stop();
			if (shortRecView != null) {
				shortRecView.dismiss();
			}
		}
		isActivityStoped = true;
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
		LocalBroadcastManager.getInstance(this).unregisterReceiver(mMessageReceiver);

		super.onDestroy();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event)
	{
		if (event.getAction() == KeyEvent.ACTION_DOWN
				&& keyCode == KeyEvent.KEYCODE_BACK
				&& mShareMenuView.getExpressionView().isShown())
		{// 让分享菜单响应返回按钮
			mShareMenuView.getExpressionView().setVisibility(View.GONE);
			return true;
		}
		else
		{
			return super.onKeyDown(keyCode, event);
		}
	}
	
	private ExtAudioRecorder ear;
	private long startRecTime = 0;
	private long endRecTime = 0;
	private long shortAudioDuration = 0;
	private String shortRecPath = "";
	private String soundTextContent = "";
	private long recordDuration = 0;
	private boolean isLongClick = false;
	private boolean isOnRecorderButton = false;
	private CountDownView shortRecView;
	private boolean isRecogniseDone = false;
	private GsonRequest2.createStructureRequest addSoundCreateStructure = null;
	
	@Override
	public boolean onLongClick(View view) {
		switch (view.getId()) {
		case R.id.iv_record:
			// 暂停播放
			if(AudioPlayer.status == 1)
			{
				AudioPlayer.pause();
				pauseAudioPlayAnimation();
			}
			if(ePlayStatus == EPlayStatus.PLAY)
			{
				pauseVideo();
			}
			
			CmmobiClickAgentWrapper.onEvent(this, "recording_sss");
			if (isDiaryEditable(myDiary) == -1) {
				Prompt.Alert(this, "对不起，暂不支持对高清视频的编辑");
				break;
			}
			
			int sync = getSync(myDiary.diaryuuid);
			if(sync == 5 || (sync == -1 && !isDiaryDownload()
					&& NetworkTaskManager.getInstance(userID).getTask(myDiary.diaryuuid) != null))
			{
				Prompt.Alert(this, "内容正在下载，请下载完成后再编辑");
				return true;
			}
			else if(sync == 4 || (sync == -1 && !isDiaryDownload()
					&& NetworkTaskManager.getInstance(userID).getTask(myDiary.diaryuuid) == null))
			{
				new Xdialog.Builder(this)
				.setTitle("无法编辑")
				.setMessage("本地无日记内容,\n请下载内容后再编辑。")
				.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						showDownloadOptions();
					}
				})
				.setNegativeButton(android.R.string.cancel, null)
				.create().show();
				return true;
			}		
			
			long currentTime = TimeHelper.getInstance().now();
			String audioID = String.valueOf(currentTime);
			String path =  Constant.SD_STORAGE_ROOT + "/" + ActiveAccount.getInstance(this).getLookLookID() + "/audio";
			shortRecPath = Environment.getExternalStorageDirectory() + path + "/" + audioID + "/" + audioID + ".mp4";
			handler.sendEmptyMessage(HomeActivity.HANDLER_RECORD_DURATION_UPDATE_MSG);
			Log.d(TAG, "record start");
			ear.start(this, audioID, path, false, 3, true);
			recordDuration = 0;
			startRecTime = TimeHelper.getInstance().now();
			isLongClick = true;
			if (shortRecView == null) {
				shortRecView = new CountDownView(this);
			}
			isOnRecorderButton = true;
			isRecogniseDone = false;
			shortRecView.show();
			return true;
		}
		return false;
	}
	
	private void stopShortRecoder() {
		isLongClick = false;
		recordDuration = 0;
		ear.stop();
		shortRecView.dismiss();
		ivRecord.setVisibility(View.GONE);
	}

	@Override
	public boolean onTouch(View view, MotionEvent event) {
		if (isLongClick) {
			switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN:
				Log.d(TAG,"ACTION_DOWN");
				break;
			case MotionEvent.ACTION_UP:
				Log.d(TAG,"ACTION_UP");
				
				endRecTime = TimeHelper.getInstance().now();
				long stopduration = endRecTime - startRecTime;
				if (stopduration > 2000) {
					stopShortRecoder();
				} else {
					handler.sendEmptyMessageDelayed(HANDLER_SHORT_RECORD_STOP_TIME_DELAY, 2000 - stopduration);
				}
				break;
			case MotionEvent.ACTION_MOVE:
				Log.d(TAG,"ACTION_MOVE");
				if (event.getY() < 0) {
					isOnRecorderButton = false;
				} else {
					isOnRecorderButton = true;
				}
				break;
			}
		}
		return false;
	}
	
	private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			if (isActivityStoped) {
				return;
			}
			int type;
			String content = "";
			Message msg = new Message();
			// Get extra data included in the Intent
			if (ExtAudioRecorder.AUDIO_RECORDER_MSG.equals(intent
					.getAction())) {
				type = intent.getIntExtra("type", 0);
				Log.d(TAG,"onReceive type = " + type);
				if (type == ExtAudioRecorder.HANDLER_AUDIO_RECORDER_DONE) {
					content = intent.getStringExtra("content");
					Log.d(TAG,"AAA if onReceive content = " + content);
					shortAudioDuration = intent.getLongExtra("audioduration",0);
					shortAudioDuration = (shortAudioDuration + 999) / 1000;
				} 
				
				msg.what = type;
				msg.obj = content;
			} else if (QISR_TASK.QISR_RESULT_MSG.equals(intent.getAction())) {
				type = intent.getIntExtra("type", 0);
			    String message = intent.getStringExtra("content");
//			    String audioId = intent.getStringExtra("audioID");
			    Log.d(TAG, "Got message: " + message);
			    msg.what = type;
			    msg.obj = message;
			}

			handler.sendMessage(msg);
		}
	};

	// 设置动画按钮
	private void setPraiseBtn(EPraiseType type)
	{
		switch(type)
		{
		case PRAISE:
			mIvPraiseBg.setImageResource(R.drawable.zan_2);
			mIvPraisesrc.setImageResource(R.drawable.zan2);
			break;
		case UNPRAISE:
			mIvPraiseBg.setImageResource(R.drawable.zan_1);
			mIvPraiseBg.setImageResource(R.anim.praise_btn_bg_animation);
			mIvPraisesrc.setImageResource(R.drawable.zan1);
			mPraiseBgAnim = (AnimationDrawable) mIvPraiseBg.getDrawable();
			break;
		case STARTANIM:
			Animation anim = AnimationUtils.loadAnimation(this, R.anim.praise_btn_src_animation);
			mIvPraisesrc.startAnimation(anim);
			mPraiseBgAnim.stop();
			mPraiseBgAnim.start();
			mIvPraisesrc.setImageResource(R.drawable.zan2);
//			mIvPraiseBg.setImageResource(R.drawable.zan_2);
			break;
		}
	}

	@Override
	public boolean handleMessage(Message msg) {
		ActiveAccount acct = ActiveAccount.getInstance(this);
		// AccountInfo ai = AccountInfo.getInstance(acct.getLookLookID());
		CmmobiSnsLib csb = CmmobiSnsLib.getInstance(this);
		if (null == msg.obj
				&& msg.what != HANDLER_UPDATE_VIDEO_PLAYER_COMPLETE
				&& msg.what != HANDLER_UPDATE_LONG_RECORD_PLAYER_COMPLETE
//				&& msg.what != HANDLER_PLAY_VIDEO
//				&& msg.what != HANDLER_PLAY_AUDIO
				&& msg.what != HANDLER_SINA_AUTHOR_SUCCESS
				&& msg.what != HANDLER_TENCENT_AUTHOR_SUCCESS
				&& msg.what != HANDLER_RENREN_AUTHOR_SUCCESS
				&& msg.what != Requester2.RESPONSE_TYPE_GET_DIARY_URL
				&& msg.what != WeiboRequester.WEIXIN_INTERFACE_SEND
				&& msg.what != HomeActivity.HANDLER_RECORD_DURATION_UPDATE_MSG
				&& msg.what != ExtAudioRecorder.HANDLER_AUDIO_RECORDER_DONE
				&& msg.what != QISR_TASK.HANDLER_QISR_RESULT_CLEAN
				&& msg.what != QISR_TASK.HANDLER_QISR_RESULT_DONE
				&& msg.what != HANDLER_SHORT_RECORD_STOP_TIME_DELAY
				&& msg.what != HANDLER_AUTO_HIDE_CONTROLLER
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
		case HANDLER_AUTO_HIDE_CONTROLLER:
			// 3秒隐藏视频按钮
			setPlayBtnStatus(false);
			break;
		case Requester2.RESPONSE_TYPE_DIARY_SET_MOOD:{
			setMoodResponse response=(setMoodResponse) msg.obj;
			if("0".equals(response.status)){
				int position=(Integer) ivMood.getTag();
				ivMood.setImageResource(moods[position]);
				myDiary.mood=position+"";
				//修改本地缓存日记心情
				mDiaryManager.modifyMood(myDiary.diaryuuid,position+"");
			}
			break;}
		case HANDLER_UPDATE_VIDEO_PLAYER_COMPLETE:
			stopVideo();
			setFullSreen(false);
			setExternVideoLayout();
			break;
		case HANDLER_UPDATE_LONG_RECORD_PLAYER_COMPLETE:
			AudioPlayer.stop();
			stopAudioPlayAnimation();
			break;
		case HANDLER_UPDATE_VIDEO_PLAYER_PROCESS:
			if(playVideoProcess!=null)
			{
				playVideoProcess.setProgress((Integer)msg.obj);
			}
			break;
		case HANDLER_UPDATE_LONG_RECORD_PLAYER_PROCESS:
			if(playAudioProcess!=null)
			{
				playAudioProcess.setProgress((Integer)msg.obj);
			}
			break;
		case HANDLER_UPDATE_VIDEO_TIME_STRING:
			tvPlayTime.setText((String)msg.obj);
			if(ivPlay.isShown())
			{
				tvPlayTime.setVisibility(View.VISIBLE);
			}
			break;
		case HANDLER_UPDATE_VIDEO_PREPARED:
			if((Boolean)msg.obj)
			{
//				ePlayStatus = EPlayStatus.OPENED;
				mMediaPlayer.play();
				// 如果是pause状态被终止播放的情况，从断点开始播放
				if(ePlayStatus == EPlayStatus.PAUSE)
				{
					mMediaPlayer.seek(ePlayStatus.seekPosition);
					ePlayStatus.seekPosition = 0d;
				}
				ePlayStatus = EPlayStatus.PLAY;
//				Animation anim = AnimationUtils.loadAnimation(DiaryDetailActivity.this, R.anim.video_thumb_out);
//				ivVideoThumbnail.startAnimation(anim);
				ivVideoThumbnail.setVisibility(View.INVISIBLE);
				Log.v(TAG, "xmediaplayer play");
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
		case Requester2.RESPONSE_TYPE_DIARY_INFO:
			diaryInfoResponse response = (diaryInfoResponse) msg.obj;
			if ("0".equals(response.status)) {
				mListDiary.add(response.diaries);
				initUI();
				setCurrent();
			}else{
				Prompt.Alert("日记信息获取失败");
				finish();
			}
			ZDialog.dismiss();
			break;
		case Requester2.RESPONSE_TYPE_DELETE_DIARY: {// 删除日记
			deleteDiaryResponse res = (deleteDiaryResponse) msg.obj;
			if ("0".equals(res.status)) {
				if (mDiaryMenuMore != null)
				{
					mDiaryMenuMore.dismiss();
				}
				afterDeleteDiary();
				Prompt.Alert("日记删除成功");
			} else {
				Prompt.Alert("日记删除失败");
				Log.e(TAG, "RESPONSE_TYPE_DELETE_DIARY status is " + res.status);
			}
			ZDialog.dismiss();
			break;
		}
		case Requester2.RESPONSE_TYPE_SAFEBOX: {
			safeboxResponse res = (safeboxResponse) msg.obj;
			if ("0".equals(res.status)) {
				if (isInSaveBox()) {// 如果原来在保险箱中，返回为移出保险箱成功
					afterRemoveSafeBox();
					Prompt.Alert("移出保险箱成功");
				} else {// 加入保险箱成功
					afterAddSafeBox();
					Prompt.Alert("放入保险箱成功");
				}
//				initBtnSaveBox();
			} else {
				if (isInSaveBox()) {
					Prompt.Alert("移除保险箱失败");
				}else{
					Prompt.Alert("加入保险箱失败");
				}
				Log.e(TAG, "RESPONSE_TYPE_SAFEBOX status is " + res.status);
			}
			ZDialog.dismiss();
			break;
		}
		case Requester2.RESPONSE_TYPE_DIARY_ENJOY: {
			enjoyResponse res = (enjoyResponse) msg.obj;
			if ("0".equals(res.status)) {// 赞成功
				// 加入赞日记列表
				Prompt.Alert("赞成功");
				mDiaryManager.addPraiseDiaryID(myDiary.diaryid);
				mDiaryManager.addDiaryToPraise(myDiary);
//				mDiaryManager.myPraiseDataChanged();
				setPraiseBtn(EPraiseType.STARTANIM);
			} else {
				Prompt.Alert("赞失败");
				Log.e(TAG, "RESPONSE_TYPE_DIARY_ENJOY status is " + res.status);
			}
			ZDialog.dismiss();
			break;
		}
		case Requester2.RESPONSE_TYPE_DELETE_AND_ENJOY: {// 取消赞
			deletepublishAndEnjoyResponse res = (deletepublishAndEnjoyResponse) msg.obj;
			if ("0".equals(res.status)) {// 取消赞成功
				// 从赞日记列表移除
				mDiaryManager.removePraiseDiaryID(myDiary.diaryid);
				mDiaryManager.removePraiseDiaryByID(myDiary.diaryid);
//				mDiaryManager.myPraiseDataChanged();
				setPraiseBtn(EPraiseType.UNPRAISE);
				Prompt.Alert("取消赞成功");
			} else {
				Prompt.Alert("取消赞失败");
				Log.e(TAG, "RESPONSE_TYPE_DELETE_AND_ENJOY status is "
						+ res.status);
			}
			ZDialog.dismiss();
			break;
		}
		case Requester2.RESPONSE_TYPE_DIARY_PUBLISH: {// 发布/取消发布
			diaryPublishResponse res = (diaryPublishResponse) msg.obj;
			if ("0".equals(res.status)) {
				afterPublish();
			} else {
				if(publishType.equals("4")){
					Prompt.Alert("取消发布失败");
				}else{
					Prompt.Alert("发布失败");
				}
				Log.e(TAG, "RESPONSE_TYPE_DIARY_PUBLISH status is "
						+ res.status);
			}
			ZDialog.dismiss();
			break;
		}
		case Requester2.RESPONSE_TYPE_ADD_COLLECT_DIARY: {// 收藏日记
			addCollectDiaryResponse res = (addCollectDiaryResponse) msg.obj;
			if ("0".equals(res.status)) {
				afterCollect();
				Prompt.Alert("收藏成功");
			} else {
				Prompt.Alert("收藏失败");
				Log.e(TAG, "RESPONSE_TYPE_ADD_COLLECT_DIARY status is "
						+ res.status);
			}
			if(mDiaryMenuMore!=null)
			{
				mDiaryMenuMore.dismiss();
			}
			ZDialog.dismiss();
			break;
		}
		case Requester2.RESPONSE_TYPE_REMOVE_COLLECT_DIARY: {// 删除收藏
			removeCollectDiaryResponse res = (removeCollectDiaryResponse) msg.obj;
			if ("0".equals(res.status)) {
				afterCancelCollect();
				Prompt.Alert("移除收藏成功");
				ZDialog.dismiss();
			} else {
				Prompt.Alert("移除收藏失败");
				Log.e(TAG, "RESPONSE_TYPE_REMOVE_COLLECT_DIARY status is "
						+ res.status);
			}
			if (mDiaryMenuMore != null)
			{
				mDiaryMenuMore.dismiss();
			}
			ZDialog.dismiss();
			break;
		}
		case Requester2.RESPONSE_TYPE_REPORT: {// 举报
			reportResponse res = (reportResponse) msg.obj;
			if ("0".equals(res.status)) {
				Prompt.Alert("举报成功");
			} else {
				Prompt.Alert("举报失败");
				Log.e(TAG, "RESPONSE_TYPE_REMOVE_COLLECT_DIARY status is "
						+ res.status);
			}
			if (mDiaryMenuMore != null)
			{
				mDiaryMenuMore.dismiss();
			}
			ZDialog.dismiss();
			break;
		}
		case WeiboRequester.SINA_INTERFACE_DEL_WEIBO:
			if ((Boolean) msg.obj) {
				mDiaryManager.getRemoveSinaIdList().remove(0);
			}
			deleteSinaTask();
			break;
		case WeiboRequester.TENCENT_INTERFACE_DEL_WEIBO:
			if ((Boolean) msg.obj) {
				mDiaryManager.getRemoveTencentIdList().remove(0);
			}
			deleteTencentTask();
			break;
		case WeiboRequester.RENREN_INTERFACE_DEL_WEIBO:
			if ((Boolean) msg.obj) {
				mDiaryManager.getRemoveRenrenIdList().remove(0);
			}
			deleteRenrenTask();
			break;
		// 从ShareWithSns移过来的微博授权代码
		case HANDLER_SINA_AUTHOR_SUCCESS:
			String sina_snsid = ActiveAccount.getInstance(this).getSNSID(SHARE_TO.SINA.ordinal());
			if(sina_snsid!=null){
				WeiboRequester.getSinaAccountInfo(getHandler(), sina_snsid);
			}else{
				ZDialog.dismiss();
				Prompt.Alert(this, "新浪授权微博异常");
			}
			break;
		case HANDLER_RENREN_AUTHOR_SUCCESS:
			String renren_snsid = ActiveAccount.getInstance(this).getSNSID(SHARE_TO.RENREN.ordinal());
			if(renren_snsid != null){
				WeiboRequester.getRenrenAccountInfo(getHandler(), renren_snsid);
			}else{
				ZDialog.dismiss();
				Prompt.Alert(this, "人人授权微博异常");
			}
			break;
		case HANDLER_TENCENT_AUTHOR_SUCCESS:
			String tencent_snsid = ActiveAccount.getInstance(this).getSNSID(SHARE_TO.TENC.ordinal());
			if(tencent_snsid != null){
				WeiboRequester.getTencentAccountInfo(getHandler(), tencent_snsid);
			}else{
				ZDialog.dismiss();
				Prompt.Alert(this, "腾讯授权微博异常");
			}
			break;
		case WeiboRequester.SINA_INTERFACE_GET_ACCOUNTINFO:
			SWUserInfo sn_object  = (SWUserInfo) msg.obj;
			if(sn_object==null || sn_object.screen_name==null || !acct.updateSinaAuthor(sn_object)){
				ZDialog.dismiss();
				Prompt.Alert(this, "获取新浪微博个人信息异常");
				accountInfo.removeAccessToken("3", "3", "1");
				CmmobiSnsLib.getInstance(this).removeOauth(SHARE_TO.SINA.ordinal());
			}else{
				Log.e(TAG, "sina getaccountinfo ok, binding..");
				OAuthV2 sina_oa = csb.acquireSinaOauth();
				if (sina_oa != null) {
					Requester2.bindAccount(getHandler(), "3", null, null,
							acct.snsid, "1", null, acct.sex, acct.address,
							acct.birthdate, sina_oa.getAccessToken(),
							sina_oa.getExpiresIn(), sina_oa.getExpiresTime(),
							sina_oa.getRefreshToken(), sina_oa.getNick(),
							sina_oa.getOpenkey(), sina_oa.getNick());
					myBind = new MyBind();
					myBind.binding_type = LoginSettingManager.BINDING_TYPE_SNS;
					myBind.snsuid = acct.snsid;
					myBind.snstype = LoginSettingManager.BINDING_SNS_TYPE_SINA;
					myBind.sns_nickname =  sina_oa.getNick();
					myBind.sns_token = sina_oa.getAccessToken();
					myBind.sns_expiration_time = sina_oa.getExpiresTime();
					myBind.sns_effective_time = sina_oa.getExpiresIn();
					myBind.sns_openkey = sina_oa.getOpenkey();
					myBind.sns_refresh_token = sina_oa.getRefreshToken();
					System.out.println("== nickname " + myBind.sns_nickname);
				}else {
					accountInfo.removeAccessToken("3", "3", "1");
					CmmobiSnsLib.getInstance(DiaryDetailActivity.this).removeOauth(SHARE_TO.SINA.ordinal());
					
				}
				
			}
			break;
		case WeiboRequester.RENREN_INTERFACE_GET_ACCOUNTINFO:
			RWUserInfo sn_renren  = (RWUserInfo) msg.obj;
			if(sn_renren==null || sn_renren.response==null ||  !acct.updateRenrenAuthor(sn_renren)){
				ZDialog.dismiss();
				Prompt.Alert(this, "获取人人微博个人信息异常");	
				accountInfo.removeAccessToken("3", "3", "2");
				CmmobiSnsLib.getInstance(this).removeOauth(SHARE_TO.RENREN.ordinal());
			}else{
				Log.e(TAG, "renren getaccountinfo ok, binding..");
				OAuthV2 renren_oa = csb.acquireRenrenOauth();
				if (renren_oa != null) {
					Requester2.bindAccount(getHandler(), "3", null, null,
							acct.snsid, LoginSettingManager.BINDING_SNS_TYPE_RENREN, null, acct.sex, acct.address,
							acct.birthdate, renren_oa.getAccessToken(),
							renren_oa.getExpiresIn(), renren_oa.getExpiresTime(),
							renren_oa.getRefreshToken(), renren_oa.getNick(),
							renren_oa.getOpenkey(), renren_oa.getNick());
					myBind = new MyBind();
					myBind.binding_type = LoginSettingManager.BINDING_TYPE_SNS;
					myBind.snsuid = acct.snsid;
					myBind.snstype = LoginSettingManager.BINDING_SNS_TYPE_RENREN;
					myBind.sns_nickname =  renren_oa.getNick();
					myBind.sns_token = renren_oa.getAccessToken();
					myBind.sns_expiration_time = renren_oa.getExpiresTime();
					myBind.sns_effective_time = renren_oa.getExpiresIn();
					myBind.sns_openkey = renren_oa.getOpenkey();
					myBind.sns_refresh_token = renren_oa.getRefreshToken();
					System.out.println("== nickname " + myBind.sns_nickname);
				}else{
					accountInfo.removeAccessToken("3", "3", "2");
					CmmobiSnsLib.getInstance(DiaryDetailActivity.this).removeOauth(SHARE_TO.RENREN.ordinal());
					
				}
				
			}
			break;
		case WeiboRequester.TENCENT_INTERFACE_GET_ACCOUNTINFO:
			TWUserInfo sn_tencent  = (TWUserInfo) msg.obj;
			if(sn_tencent==null || sn_tencent.data==null || !acct.updateTencentAuthor(sn_tencent)){
				ZDialog.dismiss();
				Prompt.Alert(this, "获取腾讯微博个人信息异常");
				accountInfo.removeAccessToken("3", "3", "6");
				CmmobiSnsLib.getInstance(this).removeOauth(SHARE_TO.TENC.ordinal());
			}else{
				Log.e(TAG, "tencent getaccountinfo ok, binding..");
				OAuthV2 tencent_oa = csb.acquireTencentWeiboOauth();
				if (tencent_oa != null) {
					Requester2.bindAccount(getHandler(), "3", null, null,
							acct.snsid, LoginSettingManager.BINDING_SNS_TYPE_TENCENT, null, acct.sex, acct.address,
							acct.birthdate, tencent_oa.getAccessToken(),
							tencent_oa.getExpiresIn(), tencent_oa.getExpiresTime(),
							tencent_oa.getRefreshToken(), tencent_oa.getNick(),
							tencent_oa.getOpenkey(), tencent_oa.getNick());
					myBind = new MyBind();
					myBind.binding_type = LoginSettingManager.BINDING_TYPE_SNS;
					myBind.snsuid = acct.snsid;
					myBind.snstype = LoginSettingManager.BINDING_SNS_TYPE_TENCENT;
					myBind.sns_nickname =  tencent_oa.getNick();
					myBind.sns_token = tencent_oa.getAccessToken();
					myBind.sns_expiration_time = tencent_oa.getExpiresTime();
					myBind.sns_effective_time = tencent_oa.getExpiresIn();
					myBind.sns_openkey = tencent_oa.getOpenkey();
					myBind.sns_refresh_token = tencent_oa.getRefreshToken();
					System.out.println("== nickname " + myBind.sns_nickname);				
				}else {
					accountInfo.removeAccessToken("3", "3", "6");
					CmmobiSnsLib.getInstance(DiaryDetailActivity.this).removeOauth(SHARE_TO.TENC.ordinal());
					
				};
				
			}
			break;
		case Requester2.RESPONSE_TYPE_BINDING:
			ZDialog.dismiss();
			GsonResponse2.bindingResponse bind_response = (GsonResponse2.bindingResponse) msg.obj;
			if(bind_response!=null && bind_response.status!=null && bind_response.status.equals("0")){

				if(bind_response.binding_type.equals(LoginSettingManager.BINDING_REQUEST_TYPE_SNS)){
//					if(bind_response.snstype.equals(LoginSettingManager.BINDING_SNS_TYPE_SINA)){
//						intent = new Intent(this, SinaFriendWeiboActivity.class);
//						mBundle = new Bundle();
//						mBundle.putString("weibo_type", "sina");
//						intent.putExtras(mBundle);
//						startActivity(intent);
//					}else if(bind_response.snstype.equals(LoginSettingManager.BINDING_SNS_TYPE_RENREN)){
//						intent = new Intent(this, RenrenFriendWeiboActivity.class);
//						mBundle = new Bundle();
//						mBundle.putString("weibo_type", "renren");
//						intent.putExtras(mBundle);
//						startActivity(intent);
//					}else if(bind_response.snstype.equals(LoginSettingManager.BINDING_SNS_TYPE_TENCENT)){
//						intent = new Intent(this, TencentFriendWeiboActivity.class);
//						mBundle = new Bundle();
//						mBundle.putString("weibo_type", "tencent");
//						intent.putExtras(mBundle);
//						startActivity(intent);
//					}
					if(myBind != null)
					{
						settingManager.addBindingInfo(myBind);
					}
					//initBind();
				}
			}else {
				Prompt.Alert(this, Constant.CRM_STATUS[Integer.parseInt(bind_response.crm_status)]);
				if(bind_response.binding_type.equals(LoginSettingManager.BINDING_REQUEST_TYPE_SNS)){
					if(bind_response.snstype.equals(LoginSettingManager.BINDING_SNS_TYPE_SINA)){
						accountInfo.removeAccessToken("3", "3", "1");
						CmmobiSnsLib.getInstance(this).removeOauth(SHARE_TO.SINA.ordinal());
					}else if(bind_response.snstype.equals(LoginSettingManager.BINDING_SNS_TYPE_RENREN)){
						accountInfo.removeAccessToken("3", "3", "2");
						CmmobiSnsLib.getInstance(this).removeOauth(SHARE_TO.RENREN.ordinal());
					}else if(bind_response.snstype.equals(LoginSettingManager.BINDING_SNS_TYPE_TENCENT)){
						accountInfo.removeAccessToken("3", "3", "6");
						CmmobiSnsLib.getInstance(this).removeOauth(SHARE_TO.TENC.ordinal());

					}
				}
			}
			break;
		case Requester2.RESPONSE_TYPE_GET_DIARY_URL:
			if(msg.obj != null){
				getDiaryUrlResponse urlResponse = (getDiaryUrlResponse) msg.obj;
				if("0".equals(urlResponse.status)){
					String picUrl=	urlResponse.shareimageurl;
					String weixinUrl = "http://www.looklook.com";
					String msgUrl = "http://www.looklook.com";
					String emailUrl = "http://www.looklook.com";
					for(int i=0;i<urlResponse.platformurls.length;i++){
						platformUrls urls=urlResponse.platformurls[i];
						if("9".equals(urls.snstype)){//微信
							weixinUrl=urls.url;
						}
						else if("10".equals(urls.snstype)){//短信
							msgUrl=urls.url;
						}
						else if("11".equals(urls.snstype)){//邮件
							emailUrl=urls.url;
						}
					}
//					String content = getContent();\
					String content = getUrl(weixinUrl);
					if(content == null)
					{
						content = "";
					}
					if(mBShareType == 1)
					{
						WeiboRequester.publishWeiXin(this, getHandler(), content, content, weixinUrl, picUrl, false);
					}
					else if(mBShareType == 2)
					{
						WeiboRequester.publishWeiXin(this, getHandler(), content, content, weixinUrl, picUrl, true);
					}
					else if(mBShareType == 10)
					{
						sendMsg("我和我的小伙伴都惊呆了，快来看吧! "+msgUrl);
						ZDialog.dismiss();
					}
					else if(mBShareType == 11)
					{
						sendMail("来自looklook的分享", "我和我的小伙伴都惊呆了，快来看吧! "+emailUrl);
						ZDialog.dismiss();
					}
					else
					{
						ZDialog.dismiss();
						Prompt.Alert("分享失败");
					}
					mBShareType = 0;
				}else{
					ZDialog.dismiss();
					Prompt.Alert("分享失败");
					Log.e(TAG, "response.status="+urlResponse.status);
				}
			}else{
				ZDialog.dismiss();
				Prompt.Alert("分享失败");
				Log.e(TAG, "msg.obj is null");
			}
			break;
		case WeiboRequester.WEIXIN_INTERFACE_SEND:
			ZDialog.dismiss();
			if(msg.obj!=null){
				// 进入微信界面
//				Prompt.Alert("成功分享到微信");
			}else{
				Prompt.Alert("分享到微信失败");
			}
			break;
		case HomeActivity.HANDLER_RECORD_DURATION_UPDATE_MSG:
			Log.d(TAG,"HANDLER_RECORD_DURATION_UPDATE_MSG isLongClick = " + isLongClick);
			if (isLongClick) {
				shortRecView.updateTime(30 - recordDuration);
				if (recordDuration >= 30) {
					
					Log.d(TAG,"MotionEvent.ACTION_UP isOnRecorderButton = " + isOnRecorderButton);
					if (isOnRecorderButton) {
						endRecTime = TimeHelper.getInstance().now();
					}
					stopShortRecoder();
				}
			}
			recordDuration++;
			
			if (isLongClick) {
				handler.sendEmptyMessageDelayed(HomeActivity.HANDLER_RECORD_DURATION_UPDATE_MSG, 1000);
			} else {
				recordDuration = 0;
			}
			break;
		case ExtAudioRecorder.HANDLER_AUDIO_RECORDER_DONE:
			Log.d(TAG,"isRecogniseDong = " + isRecogniseDone + " isOnRecorderButton = " + isOnRecorderButton);
			if ((!ExtAudioRecorder.CheckPlugin() || isRecogniseDone) && isOnRecorderButton) {
				if (shortAudioDuration > 30) {
					shortAudioDuration = 30;
				}
				String playTime = String.valueOf(shortAudioDuration);
				Log.d("Z_TAG","HANDLER_AUDIO_RECORDER_DONE + playTime " + playTime);
				addSoundCreateStructure = EditMediaDetailActivity.addShortRecordAttach(handler, myDiary, soundTextContent,shortRecPath,playTime);
			}
			break;
		case QISR_TASK.HANDLER_QISR_RESULT_ADD:
			if (msg != null && msg.obj != null) {
				soundTextContent += (String)msg.obj;
			}
			break;
		case QISR_TASK.HANDLER_QISR_RESULT_CLEAN:
			soundTextContent = "";
			break;
		case HANDLER_SHORT_RECORD_STOP_TIME_DELAY:
			stopShortRecoder();
			break;
		case QISR_TASK.HANDLER_QISR_RESULT_DONE:
			isRecogniseDone = true;
			Log.d(TAG,"HANDLER_QISR_RESULT_DONE isLongClick = " + isLongClick + " isOnRecorderButton = " + isOnRecorderButton);
			if (isLongClick) {
				return false;
			}
			if (isOnRecorderButton) {
				if (shortAudioDuration > 30) {
					shortAudioDuration = 30;
				}
				String playTime = String.valueOf(shortAudioDuration);
				Log.d(TAG,"HANDLER_QISR_RESULT_DONE + playTime " + playTime);
				addSoundCreateStructure = EditMediaDetailActivity.addShortRecordAttach(handler, myDiary, soundTextContent,shortRecPath,playTime);
			}
			break;
		case Requester2.RESPONSE_TYPE_CREATE_STRUCTURE:
			GsonResponse2.createStructureResponse structureResponse = (createStructureResponse) msg.obj;
			if (shortAudioDuration > 30) {
				shortAudioDuration = 30;
			}
			String playTime = String.valueOf(shortAudioDuration);
			Log.d("Z_TAG","HANDLER_QISR_RESULT_DONE + playTime " + playTime);
			if (structureResponse != null) {
				EditMediaDetailActivity.addShortRecordDiaryAttach(structureResponse, 
						addSoundCreateStructure, myDiary, shortRecPath,playTime);
			} else {
				Prompt.Alert("您的网络不给力呀");
			}
			break;
		default:
			break;
		}
		return false;
	}
	
	private boolean isSinaBind(){
		if(ActiveAccount.getInstance(this).isSNSBind("sina"))
			return true;
		return false;
	}
	
	private boolean isRenrenBind(){
		if(ActiveAccount.getInstance(this).isSNSBind("renren"))
			return true;
		return false;
	}
	
	private boolean isTencentBind(){
		if(ActiveAccount.getInstance(this).isSNSBind("tencent"))
			return true;
		return false;
	}
	
	private final int HANDLER_SINA_AUTHOR_SUCCESS = 1;
	private final int HANDLER_RENREN_AUTHOR_SUCCESS = 2;
	private final int HANDLER_TENCENT_AUTHOR_SUCCESS = 6;
	
	private WeiboAuthListener sinalistener = new WeiboAuthListener() {

		@Override
		public void onComplete(int weiboIndex) {
//			Prompt.Alert("sina授权成功");
			Message message = getHandler().obtainMessage(
					HANDLER_SINA_AUTHOR_SUCCESS);
			getHandler().sendMessage(message);

		}

		@Override
		public void onCancel(int arg0) {
			Prompt.Alert("sina授权取消");
		}

		@Override
		public void onError(WeiboDialogError arg0, int arg1) {
			Prompt.Alert("sina授权错误");
		}

		@Override
		public void onWeiboException(WeiboException arg0, int arg1) {
			Prompt.Alert("sina授权异常");
		}

	};

	private WeiboAuthListener renrenlistener = new WeiboAuthListener() {

		@Override
		public void onComplete(int weiboIndex) {
//			Prompt.Alert("renren授权成功");
			Message message = getHandler().obtainMessage(
					HANDLER_RENREN_AUTHOR_SUCCESS);
			getHandler().sendMessage(message);

		}

		@Override
		public void onCancel(int arg0) {
			Prompt.Alert("renren授权取消");
		}

		@Override
		public void onError(WeiboDialogError arg0, int arg1) {
			Prompt.Alert("renren授权错误");
		}

		@Override
		public void onWeiboException(WeiboException arg0, int arg1) {
			Prompt.Alert("sina授权异常");
		}

	};
	
	private WeiboAuthListener tencentlistener = new WeiboAuthListener() {

		@Override
		public void onComplete(int weiboIndex) {
//			Prompt.Alert("登陆成功");
			Message message = getHandler().obtainMessage(
					HANDLER_TENCENT_AUTHOR_SUCCESS);
			getHandler().sendMessage(message);

		}

		@Override
		public void onCancel(int arg0) {
			Prompt.Alert("tencent授权取消！");
		}

		@Override
		public void onError(WeiboDialogError arg0, int arg1) {
			Prompt.Alert("tencent授权错误！");
		}

		@Override
		public void onWeiboException(WeiboException arg0, int arg1) {
			Prompt.Alert("tencent授权异常！");
		}
	};
	
	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {

//		String diaryString = null;
		switch((Integer)arg1.getTag())
		{
		case R.drawable.ic_xiazai:
			showDownloadOptions();
			if (mDiaryMenuMore != null)
			{
				mDiaryMenuMore.dismiss();
			}
			break;
		case R.drawable.ic_baoxianxiang:
			//创建、开启、退出保险箱
			if (settingManager.getGesturepassword() == null) {
				// 未创建
				settingManager.setIsFromSetting(false);
				// 标记
				settingManager.setIsFromDetail(true);
				Intent intent = new Intent(this, SettingToCreateGestureActivity.class);
				startActivity(intent);
				/*showSaveBox(false);*/
			} else if (!settingManager.getSafeIsOn()) {
				// 打开保险箱
				settingManager.setIsFromSetting(false);
				// 标记
				settingManager.setIsFromDetail(true);
				Intent in = new Intent(this, SettingGesturePwdActivity.class);
				in.putExtra("count", 0);
				startActivity(in);
				// 关闭保险箱
//				settingManager.setSafeIsOn(false);
//				/*Intent in = new Intent(
//						HomepageMyselfDiaryActivity.SAVE_BOX_MODE_CHANGE);
//				LocalBroadcastManager.getInstance(this).sendBroadcast(in);*/
			}else {
				if (isInSaveBox()) {// 移出保险箱
					ZDialog.show(R.layout.progressdialog, false, true, DiaryDetailActivity.this);
//					Requester2.safebox(handler, myDiary.diaryid, myDiary.diaryuuid, "2");
					// 离线方式
					OfflineTaskManager.getInstance().addSafeboxRemoveTask(myDiary.diaryid, myDiary.diaryuuid);
					afterRemoveSafeBox();
					ZDialog.dismiss();
				} else {// 加入保险箱
					if(DiaryDetailMenuView.getSnsTrace(myDiary).size() > 0)
					{
						new Xdialog.Builder(DiaryDetailActivity.this)
							.setTitle("")
							.setMessage("内容放入保险箱后分享状态不变，在分享界面中可手动修改状态。")
							.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog, int which) {
									ZDialog.show(R.layout.progressdialog, false, true, DiaryDetailActivity.this);
//									Requester2.safebox(handler, myDiary.diaryid, myDiary.diaryuuid, "1");
									OfflineTaskManager.getInstance().addSafeboxAddTask(myDiary.diaryid, myDiary.diaryuuid);
									afterAddSafeBox();
									ZDialog.dismiss();
								}
							})
							.setNegativeButton(android.R.string.cancel, null)
							.create().show();
					}
					else
					{
						ZDialog.show(R.layout.progressdialog, false, true, DiaryDetailActivity.this);
//						Requester2.safebox(handler, myDiary.diaryid, myDiary.diaryuuid, "1");
						OfflineTaskManager.getInstance().addSafeboxAddTask(myDiary.diaryid, myDiary.diaryuuid);
						afterAddSafeBox();
						ZDialog.dismiss();
					}
				}
			}
			if (mDiaryMenuMore != null)
			{
				mDiaryMenuMore.dismiss();
			}
			break;
		case R.drawable.ic_shanchu:
			new Xdialog.Builder(DiaryDetailActivity.this)
			.setTitle("删除日记")
			.setMessage("确定要删除选中日记吗？")
			.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					//判断是否是本地日记
					int sync = getSync(myDiary.diaryuuid);
					if (sync > 0 || sync == -1)
					{
						ZDialog.show(R.layout.progressdialog, false, true, DiaryDetailActivity.this);
						// 2.删除日记协议请求
//						Requester2.deleteDiary(handler, myDiary.diaryid);
						// 改为离线模式
						OfflineTaskManager.getInstance().addDiaryRemoveTask(myDiary.diaryid);
						afterDeleteDiary();
						ZDialog.dismiss();
//						Prompt.Alert("日记删除成功");
					}
					else
					{
						// 3.删除日记
						afterDeleteDiary();
//						Prompt.Alert("日记删除成功");
					}
				}
			})
			.setNegativeButton(android.R.string.cancel, null)
			.create().show();
			if (mDiaryMenuMore != null)
			{
				mDiaryMenuMore.dismiss();
			}
			break;
		case R.drawable.ic_shoucang:
			if (isEnshrine()) {// 取消收藏
				ZDialog.show(R.layout.progressdialog, false, true, DiaryDetailActivity.this);
//				Requester2.removeCollectDiary(handler, myDiary.diaryid);
				// 修改为离线模式
				OfflineTaskManager.getInstance().addCollectRemoveTask(myDiary.diaryid);
				afterCancelCollect();
				ZDialog.dismiss();
			} else {//收藏
				showCollectMenu();
			}
			if (mDiaryMenuMore != null)
			{
				mDiaryMenuMore.dismiss();
			}
			break;
		case R.drawable.ic_jubao:
			new Xdialog.Builder(DiaryDetailActivity.this)
			.setTitle("举报")
			.setMessage("确定要举报该日记吗？")
			.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					ZDialog.show(R.layout.progressdialog, false, true, DiaryDetailActivity.this);
					Requester2.report(handler, myDiary.diaryid, "");
				}
			})
			.setNegativeButton(android.R.string.cancel, null)
			.create().show();
			if (mDiaryMenuMore != null)
			{
				mDiaryMenuMore.dismiss();
			}
			break;
		case R.drawable.btn_diarydetail_looklook:
			showPublishSetting(R.drawable.btn_diarydetail_looklook);
			// 取消下部菜单
			mShareMenuView.getExpressionView().setVisibility(View.GONE);
			break;
		case R.drawable.btn_diarydetail_privatemsg:
			int sync = getSync(myDiary.diaryuuid);
			if(sync == -1 || sync > 3)
			{
				Intent priIntent = new Intent();
				priIntent.putExtra("diaryID", myDiary.diaryid);
				priIntent.putExtra("myDiary", GsonHelper.getInstance().getString(myDiary));
				priIntent.setClass(this, SendDiaryByPrivateMsgActivity.class);
				startActivity(priIntent);
			}
			else
			{
				Prompt.Alert("请等待日记同步完成再重试！");
			}
			// 取消下部菜单
			mShareMenuView.getExpressionView().setVisibility(View.GONE);
			break;
		case R.drawable.btn_diarydetail_active:
			if(isAccessActive())
			{
				if(isJoinActive())
				{
					new Xdialog.Builder(this)
					.setTitle("无法参加活动")
					.setMessage("该日记已参加活动")
					.setNegativeButton(android.R.string.cancel, null)
					.create().show();
					break;
				}
				if(isPublished() && myDiary.publish_status.equals("1"))
				{
					shareToActive();
				}
				else
				{
					
					showPublishSetting(R.drawable.btn_diarydetail_active);
				} 
			}
			else
			{
				new Xdialog.Builder(this)
				.setTitle("")
				.setMessage("非视频类内容无法参加活动")
				.setNegativeButton(android.R.string.cancel, null)
				.create().show();
			}
			// 取消下部菜单
			mShareMenuView.getExpressionView().setVisibility(View.GONE);
			break;
		case R.drawable.btn_diarydetail_sina:
			if(!isSinaBind()){
				//绑定新浪微博
				CmmobiSnsLib.getInstance(this)
				.sinaAuthorize(sinalistener);
				break;
			}
			if(isPublished() && myDiary.publish_status.equals("1"))
			{
				shareToSina();
			}
			else
			{
				if(isMyself())
				{
					showPublishSetting(R.drawable.btn_diarydetail_sina);
				}
				else
				{
					new Xdialog.Builder(this)
					.setMessage("此日记主人未公开内容不能分享。")
					.setNegativeButton(android.R.string.ok, null)
					.create().show();
				}
			}
			// 取消下部菜单
			mShareMenuView.getExpressionView().setVisibility(View.GONE);
			break;
		case R.drawable.btn_diarydetail_weixin:
			int weixinStatus = WeiboRequester.isWXAppSupportAPI(this);
			if(weixinStatus == 1)
			{
				if(isPublished() && myDiary.publish_status.equals("1"))
				{
					shareToWeixin();
				}
				else
				{
					if(isMyself())
					{
						showPublishSetting(R.drawable.btn_diarydetail_weixin);
					}
					else
					{
						new Xdialog.Builder(this)
						.setMessage("此日记主人未公开内容不能分享。")
						.setNegativeButton(android.R.string.ok, null)
						.create().show();
					}
				}
				// 取消下部菜单
				mShareMenuView.getExpressionView().setVisibility(View.GONE);
			}
			else
			{
				String weixinTips = "";
				if(weixinStatus == -1)
				{
					weixinTips = "需要安装微信客户端才能完成本次分享";
				}
				else if(weixinStatus == 0)
				{
					weixinTips = "您的微信客户端版本过低";
				}
				new Xdialog.Builder(this)
				.setMessage(weixinTips)
				.setNegativeButton(android.R.string.ok, null)
				.create().show();
			}
			break;
		case R.drawable.btn_diarydetail_weixinfriend:
			int friendStatus = WeiboRequester.isWXAppSupportAPI(this);
			boolean friendsup = WeiboRequester.isSupportTimeline(this);
			String weixinTips = "";
			if(friendStatus == -1)
			{
				weixinTips = "需要安装微信客户端才能完成本次分享";
			}
			else if(friendStatus == 0)
			{
				weixinTips = "您的微信客户端版本过低";
			}
			else if(!friendsup)
			{
				weixinTips = "您的微信客户端不支持朋友圈";
			}
			if(!weixinTips.equals(""))
			{	
				new Xdialog.Builder(this)
				.setMessage(weixinTips)
				.setNegativeButton(android.R.string.ok, null)
				.create().show();
			}
			else
			{			
				if(isPublished() && myDiary.publish_status.equals("1"))
				{
					shareToWeixinFriend();
				}
				else
				{
					if(isMyself())
					{
						showPublishSetting(R.drawable.btn_diarydetail_weixinfriend);
					}
					else
					{
						new Xdialog.Builder(this)
						.setMessage("此日记主人未公开内容不能分享。")
						.setNegativeButton(android.R.string.ok, null)
						.create().show();
					}
				}
				// 取消下部菜单
				mShareMenuView.getExpressionView().setVisibility(View.GONE);
			}
			break;
		case R.drawable.btn_diarydetail_tencent:
			if(!isTencentBind()){
				//绑定腾讯
				CmmobiSnsLib.getInstance(this)
				.tencentWeiboAuthorize(tencentlistener);
				break;
			}
			if(isPublished() && myDiary.publish_status.equals("1"))
			{
				shareToTencent();
			}
			else
			{
				if(isMyself())
				{
					showPublishSetting(R.drawable.btn_diarydetail_tencent);
				}
				else
				{
					new Xdialog.Builder(this)
					.setMessage("此日记主人未公开内容不能分享。")
					.setNegativeButton(android.R.string.ok, null)
					.create().show();
				}
			}
			// 取消下部菜单
			mShareMenuView.getExpressionView().setVisibility(View.GONE);
			break;
		case R.drawable.btn_diarydetail_renren:
			if(!isRenrenBind()){
				//绑定人人
				CmmobiSnsLib.getInstance(this)
				.renrenAuthorize(renrenlistener);
				break;
			}
			if(isPublished() && myDiary.publish_status.equals("1"))
			{
				shareToRenren();
			}
			else
			{
				if(isMyself())
				{
					showPublishSetting(R.drawable.btn_diarydetail_renren);
				}
				else
				{
					new Xdialog.Builder(this)
					.setMessage("此日记主人未公开内容不能分享。")
					.setNegativeButton(android.R.string.ok, null)
					.create().show();
				}
			}
			// 取消下部菜单
			mShareMenuView.getExpressionView().setVisibility(View.GONE);
			break;
		case R.drawable.btn_diarydetail_email:
			if(isPublished() && myDiary.publish_status.equals("1"))
			{
				shareToEmail();
			}
			else
			{
				if(isMyself())
				{
					showPublishSetting(R.drawable.btn_diarydetail_email);
				}
				else
				{
					new Xdialog.Builder(this)
					.setMessage("此日记主人未公开内容不能分享。")
					.setNegativeButton(android.R.string.ok, null)
					.create().show();
				}
			}
			// 取消下部菜单
			mShareMenuView.getExpressionView().setVisibility(View.GONE);
			break;
		case R.drawable.btn_diarydetail_msg:
			if(isPublished() && myDiary.publish_status.equals("1"))
			{
				shareToMsg();
			}
			else
			{
				if(isMyself())
				{
					showPublishSetting(R.drawable.btn_diarydetail_msg);
				}
				else
				{
					new Xdialog.Builder(this)
					.setMessage("此日记主人未公开内容不能分享。")
					.setNegativeButton(android.R.string.ok, null)
					.create().show();
				}
			}
			// 取消下部菜单
			mShareMenuView.getExpressionView().setVisibility(View.GONE);
			break;
		default:
			break;
		}
		
	}

	private void deleteSinaTask() {
		if (mDiaryManager.getRemoveSinaIdList().size() > 0) {
			String weiboID = mDiaryManager.getRemoveSinaIdList().get(0);
			WeiboRequester.delSinaWeibo(this, handler, weiboID);
			Log.v(TAG, "sina weibo delete id = "+weiboID);
			mFlagOfTaskSina = 1;
		}
		else
		{
			mFlagOfTaskSina = 0;
		}
	}

	private void deleteTencentTask() {
		if (mDiaryManager.getRemoveTencentIdList().size() > 0) {
			String weiboID = mDiaryManager.getRemoveTencentIdList().get(0);
			WeiboRequester.delTencentWeibo(this, handler, weiboID);
			Log.v(TAG, "tencent weibo delete id = "+weiboID);
			mFlagOfTaskTencent = 1;
		}
		else
		{
			mFlagOfTaskTencent = 0;
		}
	}

	private void deleteRenrenTask() {
		if (mDiaryManager.getRemoveRenrenIdList().size() > 0) {
			String weiboID = mDiaryManager.getRemoveRenrenIdList().get(0);
			WeiboRequester.delRenrenWeibo(this, handler, weiboID);
			Log.v(TAG, "renren weibo delete id = "+weiboID);
			mFlagOfTaskRenren = 1;
		}
		else
		{
			mFlagOfTaskRenren = 0;
		}
	}

	private void deleteSNSTask() {
		deleteSinaTask();
		deleteTencentTask();
		deleteRenrenTask();
	}

	boolean isFullScreenMode;
	@Override
	public void onClick(View v) {
		mShareMenuView.getExpressionView().setVisibility(View.GONE);
		switch (v.getId()) {
		case R.id.btn_position_type_all:
			if (!"1".equals(positionType))
			{
				positionType = "1";
				setPositionTypeSetting();
			}
			break;
		case R.id.btn_position_type_attended:
			if (!"2".equals(positionType))
			{
				positionType = "2";
				setPositionTypeSetting();
			}
			break;
		case R.id.btn_position_type_self:
			if (!"4".equals(positionType))
			{
				positionType = "4";
				setPositionTypeSetting();
			}
			break;
		case R.id.iv_diary_type_all:
			if (!"1".equals(publishType))
			{
				publishType = "1";
				setDiaryTypeSetting();
				setPositionTypeSetting();
			}
			break;
		case R.id.iv_diary_type_attended:
			if (!"2".equals(publishType))
			{
				publishType = "2";
				setDiaryTypeSetting();
				setPositionTypeSetting();
			}
			break;
		case R.id.iv_diary_type_all_cancel:
			if (!"1".equals(publishType))
			{
				publishType = "1";
				setDiaryTypeSetting();
				setPositionTypeSetting();
			}
			break;
		case R.id.iv_diary_type_attended_cancel:
			if (!"2".equals(publishType))
			{
				publishType = "2";
				setDiaryTypeSetting();
				setPositionTypeSetting();
			}
			break;
		case R.id.iv_diary_type_self_cancel:
			if (!"4".equals(publishType))
			{
				publishType = "4";
				setDiaryTypeSetting();
				setPositionTypeSetting();
			}
			break;
		case R.id.IB_share_ok:
//			if(!mDiaryManager.getPublishShareStatus(myDiary.diaryuuid).equals(publishType) ||
//					!mDiaryManager.getPositionShareStatus(myDiary.diaryuuid).equals(positionType))
			{
				shareToLooklook((Integer) v.getTag());
			}
			// 取消下部菜单
			if (mDiaryMenuMore != null)
			{
				mDiaryMenuMore.dismiss();
			}
			break;
		case R.id.IB_share_cancel:
			// 取消下部菜单
			if (mDiaryMenuMore != null)
			{
				mDiaryMenuMore.dismiss();
			}
			break;
//		case R.id.iv_publish_setting_wancheng:
//			if(mMenuMore!=null)mMenuMore.dismiss();
//			break;
		case R.id.iv_play://视频播放按钮
			// 避免冲突
//			stopAutoPlay();
			if (null == v.getTag())
			{
				Log.e(TAG, "video url null");
				return;
			}
			else
			{
				String url=v.getTag().toString();
				String newUrl = getVideoPath(url);
				
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
					if(!isNetworkConnected(this, url, 5))
					{
						Prompt.Alert("您的网络不给力呀");
						break;
					}
					if(newUrl.startsWith("http"))
					{
						newUrl += getStatisString(this, myDiary.userid, myDiary.latitude+":"+myDiary.longitude, 
								getMediaId(myDiary, "1"), "1", "4");
					}
					
					if(ePlayStatus == EPlayStatus.NON)
					{
						setFullSreen(true);
						setExternVideoLayout();
					}
					playVideo(newUrl);
				}
				setPlayBtnStatus(ivPlay.isShown());
				if(tvTack != null)
				{
					tvTack.stop();
				}
			}
			break;
		case R.id.rl_video_content://视屏播放布局
			boolean show = ivPlay.isShown();
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
		case R.id.ll_video_top:
			if(!vidoeLock)
			{
				if(isFullScreenMode)
				{
					isFullScreenMode=false;
				}
				else
				{
					isFullScreenMode=true;
				}
				setVideoLayout();
			}
			else
			{
				if(!ivPlay.isShown())
				{
					if(getHandler().hasMessages(HANDLER_AUTO_HIDE_CONTROLLER))
					{
						getHandler().removeMessages(HANDLER_AUTO_HIDE_CONTROLLER);
					}
					getHandler().sendEmptyMessageDelayed(HANDLER_AUTO_HIDE_CONTROLLER, 4000);
				}
				setPlayBtnStatus(!ivPlay.isShown());
			}
			break;
		case R.id.ll_pic_top:
			if(isFullScreenMode)
			{
				isFullScreenMode = false;
			}
			else
			{
				isFullScreenMode = true;
			}
			setPictureLayout();
			break;
		case R.id.ri_activity_weather:
			DiaryType type = (DiaryType)((MyPageAdapter)mDiarydetail_viewpager.getAdapter()).getView(mPageIndex).getTag();
			if(type != null)
			{
				if(type == DiaryType.VEDIO)
				{
					// 视频
					if (isFullScreenMode)
					{
						isFullScreenMode = false;
						// ivExternPlayer.setBackgroundResource(R.drawable.btn_activity_dairydetail_shiping_quanping);
					}
					else
					{
						isFullScreenMode = true;
						// ivExternPlayer.setBackgroundResource(R.drawable.btn_activity_dairydetail_shiping_quanping_restore);
					}
					setVideoLayout();
				}
				else if(type == DiaryType.PICTURE)
				{
					// 图片
					if (isFullScreenMode)
					{
						isFullScreenMode = false;
					}
					else
					{
						isFullScreenMode = true;
					}
					setPictureLayout();
				}
			}
			break;
		case R.id.iv_activity_shiping_quanping:
			//全屏播放
			setFullSreen(!getLockStatus());
			
			setExternVideoLayout();

//			String playUrl = getVideoUrl();
//			String playPath = getVideoPath(playUrl);
////			String videoTime = getVideoPlaytime();
//			String videoTime = null;
//			String thumbUrl = getVideoCover();
//			if(playUrl != null)
//			{
//				sMediaStatus.playPath = playPath;
//				sMediaStatus.videoTime = videoTime;
//				sMediaStatus.thumbUrl = thumbUrl;
//				sMediaStatus.status = ePlayStatus;
//				sMediaStatus.curTime = mMediaPlayer.getCurrentTime();
//				
//				Intent extPlay = new Intent(this, ExtPlayerActivity.class);
//				
////				if(ePlayStatus == EPlayStatus.PAUSE || ePlayStatus == EPlayStatus.PAUSE)
////				{
////					double pos = mMediaPlayer.getCurrentTime();
////					extPlay.putExtra(INTENT_EXTRA_PLAY_CURTIME, pos);
////				}
//				stopVideo();
////				if(mMediaPlayer != null)
////				{
////					mMediaPlayer.release();
////					mMediaPlayer = null;
////				}
//				
////				extPlay.putExtra(INTENT_EXTRA_PLAY_URL, playPath);
////				extPlay.putExtra(INTENT_EXTRA_PLAY_TOTALTIME, videoTime);
////				extPlay.putExtra(INTENT_EXTRA_PLAY_THUMB, thumbUrl);
//				startActivity(extPlay);
//			}
			
			break;
		case R.id.rl_activity_yuantupian:
			//全屏播放
			if(isFullScreenMode){
				isFullScreenMode=false;
			}else{
				isFullScreenMode=true;
			}
			setPictureLayout();
			break;
		case R.id.rl_activity_yuanluyin:
			//全屏播放
			if(isFullScreenMode){
				isFullScreenMode=false;
			}else{
				isFullScreenMode=true;
			}
			setAudioLayout();
			break;
		case R.id.rl_activity_nomain:
			//全屏播放
			if(isFullScreenMode){
				isFullScreenMode=false;
			}else{
				isFullScreenMode=true;
			}
			setNomainLayout();
			break;
		case R.id.iv_diarydetail_video_tack:// 视频界面播放短录音
			// 避免冲突
//			stopAutoPlay();
			if(v.getTag()!=null){
				String url=v.getTag().toString();
				if(!isNetworkConnected(this, url, 3))
				{
					Prompt.Alert("您的网络不给力呀");
					break;
				}
				if(mMediaPlayer != null && mMediaPlayer.getStatus() == XEffectMediaPlayer.STATUS_PALYING)
				{
					pauseVideo();
				}
				TackView tackView=(TackView) v;
//				tackView.setHandler(handler);
				String statisUrl = "";
				if(url.startsWith("http"))
				{
					statisUrl = getStatisString(this, myDiary.userid, myDiary.latitude+":"+myDiary.longitude, 
							getMediaId(myDiary, "2"), "4", "4");
				}
				tackView.setAudio(url, 1, statisUrl);
			}
			break;
		case R.id.iv_diarydetail_audio_tack:// 音频界面播放短录音
			// 避免冲突
//			stopAutoPlay();
			if(v.getTag()!=null){
				
				String url=v.getTag().toString();
				if(!isNetworkConnected(this, url, 3))
				{
					Prompt.Alert("您的网络不给力呀");
					break;
				}
				AudioPlayer.pause();
				pauseAudioPlayAnimation();
				TackView tackView=(TackView) v;
//				tackView.setHandler(handler);
				String statisUrl = "";
				if(url.startsWith("http"))
				{
					statisUrl = getStatisString(this, myDiary.userid, myDiary.latitude+":"+myDiary.longitude, 
							getMediaId(myDiary, "2"), "4", "4");
				}
				tackView.setAudio(url, 1, statisUrl);
			}
			break;
		case R.id.iv_diarydetail_tack:// 音频界面播放短录音
			if(v.getTag()!=null){
				
				String url=v.getTag().toString();
				if(!isNetworkConnected(this, url, 3))
				{
					Prompt.Alert("您的网络不给力呀");
					break;
				}
				AudioPlayer.pause();
				TackView tackView=(TackView) v;
//				tackView.setHandler(handler);
				String statisUrl = "";
				if(url.startsWith("http"))
				{
					statisUrl = getStatisString(this, myDiary.userid, myDiary.latitude+":"+myDiary.longitude, 
							getMediaId(myDiary, "2"), "4", "4");
				}
				tackView.setAudio(url, 1, statisUrl);
			}
			break;
		case R.id.btn_yuanluyin_play://播放长录音
			// 避免冲突
//			stopAutoPlay();
			if (null == v.getTag())
			{
				Log.e(TAG, "audio url null");
				return;
			}
			else
			{
				String url=v.getTag().toString();
				String newUrl = getAudioPath(url);
				if(!isNetworkConnected(this, url, 4))
				{
					Prompt.Alert("您的网络不给力呀");
					break;
				}
				if(newUrl.startsWith("http"))
				{
					newUrl += getStatisString(this, myDiary.userid, myDiary.latitude+":"+myDiary.longitude, 
						getMediaId(myDiary, "2"), "3", "4");
				}
				AudioPlayer.playAudio(newUrl,handler);
				startAudioPlayAnimation();
				if(tvTack != null)
				{
					tvTack.stop();
				}
			}
			break;
		case R.id.btn_yuanluyin_pause://暂停长录音
			AudioPlayer.pause();
			pauseAudioPlayAnimation();
			break;
		case R.id.btn_yuanluyin_stop://停止长录音
			AudioPlayer.stop();
			stopAudioPlayAnimation();
			if(playAudioProcess!=null)
			{
				playAudioProcess.setProgress(0);
			}
			break;
//		case R.id.ll_diaryname:// 顶栏日记名称区域
//			showDuplicateDiaryName();
//			break;
		case R.id.iv_diarydetail_active:
			if(myDiary.active!=null){//已参加活动
				String activeString=new Gson().toJson(myDiary.active);
				startActivity(new Intent(this, ActivitiesDetailActivity.class)
				.putExtra(ActivitiesDetailActivity.ACTION_ACTIVIES, activeString)
				.putExtra("comeDiaryId", myDiary.diaryid).putExtra("diaryuid", myDiary.userid)
				.putExtra("comeDiaryuuid", myDiary.diaryuuid));
				
			}
			break;
		case R.id.iv_diarydetail_edit:// 日记编辑
		{
			int sync = getSync(myDiary.diaryuuid);
			boolean onlyText = isOnlyText();
			if(onlyText){
				AudioPlayer.stop();
				stopAudioPlayAnimation();
				stopVideo();
				Intent intent = new Intent();
				String diaryString = new Gson().toJson(myDiary);
				intent.putExtra(DiaryDetailActivity.INTENT_ACTION_DIARY_UUID,
						myDiary.diaryuuid);
				intent.putExtra(DiaryDetailActivity.INTENT_ACTION_DIARY_STRING,
						diaryString);
				CmmobiClickAgentWrapper.onEvent(this, "my_com_det");
				intent.setClass(DiaryDetailActivity.this, EditDiaryActivity.class);
				startActivityForResult(intent, REQUESTCODE_DETAIL);
			}
			// 评论等界面的日记没有本地缓存 状态始终是0
			else if(sync == 5 || (sync == -1 && !isDiaryDownload()
					&& NetworkTaskManager.getInstance(userID).getTask(myDiary.diaryuuid) != null))
			{
				Prompt.Alert(this, "内容正在下载，请下载完成后再编辑");
			}
			else if(sync == 4 || (sync == -1 && !isDiaryDownload()
					&& NetworkTaskManager.getInstance(userID).getTask(myDiary.diaryuuid) == null))
			{
				new Xdialog.Builder(this)
				.setTitle("无法编辑")
				.setMessage("本地无日记内容,\n请下载内容后再编辑。")
				.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						showDownloadOptions();
					}
				})
				.setNegativeButton(android.R.string.cancel, null)
				.create().show();
			}				
			else
			{
				if (isDiaryEditable(myDiary) == -1) {
					Prompt.Alert(this, "对不起，暂不支持对高清视频的编辑");
					break;
				}
				AudioPlayer.stop();
				stopAudioPlayAnimation();
				stopVideo();
				Intent intent = new Intent();
				String diaryString = new Gson().toJson(myDiary);
				intent.putExtra(DiaryDetailActivity.INTENT_ACTION_DIARY_UUID,
						myDiary.diaryuuid);
				intent.putExtra(DiaryDetailActivity.INTENT_ACTION_DIARY_STRING,
						diaryString);
				intent.setClass(DiaryDetailActivity.this, EditDiaryActivity.class);
				startActivityForResult(intent, REQUESTCODE_DETAIL);
			}
			break;
		}
		case R.id.fl_praise:
			ZDialog.show(R.layout.progressdialog, false, true, this);
			if (isPraise()) {// 赞过
				// 取消赞协议请求
				Requester2.deletepublishAndEnjoy(handler, myDiary.publishid,
						myDiary.diaryid);
			} else {// 未赞过
					// 赞协议请求
				// 统计赞次数
				CmmobiClickAgentWrapper.onEvent(this, "det_pra", myDiary.diaryid);
				Requester2.enjoyandforward(handler, myDiary.diaryid,
						myDiary.publishid);
			}
			break;
		case R.id.iv_comment:
			if ("comment".equals(v.getTag())) {
				Intent intent = new Intent();
				String diaryString = new Gson().toJson(myDiary);
				intent.putExtra(DiaryDetailActivity.INTENT_ACTION_DIARY_ID,
						myDiary.diaryid == null ? myDiary.diaryuuid : myDiary.diaryid);
				// 保证评论页逻辑与之前一样，如果没diaryid时传递diaryuuid，但是日记体内部的diarydi仍然可能为空
				intent.putExtra(DiaryDetailActivity.INTENT_ACTION_DIARY_STRING,
						diaryString);
				intent.setClass(DiaryDetailActivity.this,
						HomepageCommentActivity.class);
				startActivity(intent);
			}
			if ("delete".equals(v.getTag())) {
				new Xdialog.Builder(this)
				.setTitle("删除日记")
				.setMessage("确定要删除选中日记吗？")
				.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						//判断是否是本地日记
						int sync = getSync(myDiary.diaryuuid);
						if (sync > 0 || sync == -1)
						{
							ZDialog.show(R.layout.progressdialog, false, true, DiaryDetailActivity.this);
							// 2.删除日记协议请求
//							Requester2.deleteDiary(handler, myDiary.diaryid);
							// 改为离线模式
							OfflineTaskManager.getInstance().addDiaryRemoveTask(myDiary.diaryid);
							afterDeleteDiary();
							ZDialog.dismiss();
							Prompt.Alert("日记删除成功");
						}
						else
						{
							// 3.删除日记
							afterDeleteDiary();
							Prompt.Alert("日记删除成功");
						}
					}
				})
				.setNegativeButton(android.R.string.cancel, null)
				.create().show();
			}
			break;
		case R.id.iv_share:
			if (isMyself()) {
				if ("share".equals(v.getTag())) {
					showMyShareMenu();
				}
				if ("upload".equals(v.getTag())) {
					 INetworkTask t = NetworkTaskManager.getInstance(userID).getTask(myDiary.diaryuuid);
					 // 如果该任务期间已经被删除
					if (t == null) {
						// 日记上传
						if (0 == myDiary.sync_status) {// 创建日记结构失败
							NetworkTaskInfo networktaskinfo = new NetworkTaskInfo(myDiary, INetworkTask.TASK_TYPE_CACHE);// 设置数据源
							t = new CacheNetworkTask(networktaskinfo);// 创建上传/下载任务
						} else {// 创建日记结构成功
							NetworkTaskInfo networktaskinfo = new NetworkTaskInfo(myDiary, INetworkTask.TASK_TYPE_UPLOAD);// 设置数据源
							t = new UploadNetworkTask(networktaskinfo);// 创建上传/下载任务
						}
						NetworkTaskManager.getInstance(userID).addTask(t);// 添加网络任务
						NetworkTaskManager.getInstance(userID).startNextTask();// 开始任务队列
					// 启动该任务
					} else {
						NetworkTaskManager.getInstance(userID).startTask(myDiary.diaryuuid);
					}
					//开始任务队列
					Prompt.Alert("已加入上传队列");
					ivComment.setBackgroundResource(R.drawable.btn_activity_diarydetail_comment);
					setCommentAndPraise();
					ivShareOrTransfer
							.setBackgroundResource(R.drawable.btn_activity_diarydetail_share);
					ivComment.setTag("comment");
					ivShareOrTransfer.setTag("share");
				}
			} else {
				showOtherShareMenu();
			}
			break;
//		case R.id.iv_record:
//			Toast.makeText(this, "record", Toast.LENGTH_LONG).show();
//			break;
		case R.id.iv_more:
			if (isMyself()) {
				showMyMoreMenu();
			} else {
				showOtherMoreMenu();
			}
			break;
		case R.id.iv_back:
			finish();
			break;
		case R.id.btn_cancel:
			if (mDiaryMenuSelect != null)
			{
				mDiaryMenuSelect.dismiss();
			}
			break;
		case R.id.btn_more_menu_enshrine_only:// 仅收藏
			if (mDiaryMenuSelect != null)
			{
				mDiaryMenuSelect.dismiss();
			}
			ZDialog.show(R.layout.progressdialog, false, true, this);
//			Requester2.addcollectDiary(handler, myDiary.diaryid);
			// 改为离线模式
			OfflineTaskManager.getInstance().addCollectAddTask(myDiary.diaryid);
			afterCollect();
			ZDialog.dismiss();
			break;
		case R.id.btn_more_menu_enshrine_and_download:// 收藏并下载
			if (mDiaryMenuSelect != null)
			{
				mDiaryMenuSelect.dismiss();
			}
			ZDialog.show(R.layout.progressdialog, false, true, this);
//			Requester2.addcollectDiary(handler, myDiary.diaryid);
			// 改为离线模式
			OfflineTaskManager.getInstance().addCollectAddTask(myDiary.diaryid);
			afterCollect();
			ZDialog.dismiss();
			
			// 改为直接下载普清压标的 
			MyAttachVideo[] attachVideos = getVideoUrlPath();
			MyAttachVideo normal = null;
			MyAttachVideo undecode = null;
			MyAttachVideo normaldecode = null;
			if(attachVideos != null && attachVideos.length > 0)
			{
				for (int i = 0; i < attachVideos.length; i++)
				{
					MyAttachVideo attachVideo = attachVideos[i];
					if ("1".equals(attachVideo.videotype))
					{// 原视频普清
						normal = attachVideo;
					}
					if ("2".equals(attachVideo.videotype))
					{// 普清转码未压标
						undecode = attachVideo;
					}
					if ("3".equals(attachVideo.videotype))
					{// 普清转压
						normaldecode = attachVideo;
					}
				}
				if(normaldecode != null)
				{
					downloadVedio(normaldecode);
				}
				else if(normal != null)
				{
					downloadVedio(normal);
				}
				else if(undecode != null)
				{
					downloadVedio(undecode);
				}
			}else{
				//加入到下载队列
				addDownloadTask(myDiary);
			}
//			showDownloadOptions();
			break;
		case R.id.btn_download_option_heigh:
		case R.id.btn_download_option_normal:
//		case R.id.btn_download_option_original:
			if (mDiaryMenuSelect != null)
			{
				mDiaryMenuSelect.dismiss();
			}
			downloadVedio((MyAttachVideo) v.getTag());
			break;
		case R.id.iv_diarydetail_xinqing:
			CmmobiClickAgentWrapper.onEvent(this, "my_mood", 1);
			if(mPopupWindow!=null)mPopupWindow.dismiss();
			
//			measureView(listMoods);
//			Bitmap bitmap = decodeResource(getResources(), R.drawable.xinqing_xiala);
			Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.xinqing_xiala);
			
//			mPopupWindow = new PopupWindow(listMoods,
//					listMoods.getMeasuredWidth(),
//					(listMoods.getMeasuredHeight() * 9), true);
			
			mPopupWindow = new PopupWindow(listMoods,
					bitmap.getWidth(),
					bitmap.getHeight(), true);
			
			mPopupWindow.setBackgroundDrawable(getResources().getDrawable(
					R.drawable.dot_big));
			mPopupWindow.showAsDropDown(ivMood, 0, 0);
			break;
		case R.id.ib_diarydetail_menu_cancel:
			// 取消下部菜单
			mShareMenuView.getExpressionView().setVisibility(View.GONE);
		default:
			break;
		}
	}
	
	// 解决bitmapde density导致变化错误的问题
//	private Bitmap decodeResource(Resources resources, int id) {
//		TypedValue value = new TypedValue();
//		resources.openRawResource(id, value);
//		BitmapFactory.Options opts = new BitmapFactory.Options();
//		opts.inTargetDensity = value.density;
//		return BitmapFactory.decodeResource(resources, id, opts).copy(Bitmap.Config.ARGB_8888, true);
//	}
	
	// 获取当前所有主本数量
//	private ArrayList<MyDiary> getMainDiary()
//	{
//		ArrayList<MyDiary> tmpList = new ArrayList<MyDiary>();
//		for(MyDiary diary : mListDiary)
//		{
//			if(diary.resourceuuid.equals(""))
//			{
//				tmpList.add(diary);
//			}
//		}
//		return tmpList;
//	}
	
	// 播放视频
	private boolean saveboxIsOpen(){
		if (settingManager.getGesturepassword() == null) {
			// 未创建
			return false;
		} else if (settingManager.getSafeIsOn()) {
			// 已创建且打开
			return true;
		} else {
			// 已创建但关闭
			return false;
		}
	}
	
	// 播放视频
	private void playVideo(String url){
//		String videoCover = getVideoCover();
//		if (videoCover != null && videoCover.length() > 0) {
//			ivVideoThumbnail.setImageUrl(0, 1, videoCover, false);
//		}
		if(null == mMediaPlayer && rlVideoContent != null)
		{
			rlVideoContent.removeAllViews();
			XEffects mEffects = null;
			if (PluginUtils.isPluginMounted()) {
				mEffects = new XEffects();
			}
			mMediaPlayer = new XMediaPlayer(this, mEffects, false);
			mMediaPlayer.setListener(new MyOnInfoListener());
			RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(LayoutParams.FILL_PARENT,LayoutParams.FILL_PARENT);
			rlVideoContent.addView((View)mMediaPlayer.getXSurfaceView(), params);
			
			Log.v(TAG, "xmediaplayer create");
		}
		if(mMediaPlayer != null && url != null)
		{
			if (mMediaPlayer.getStatus() == XEffectMediaPlayer.STATUS_UNKNOW && ePlayStatus == EPlayStatus.NON)
			{
				mMediaPlayer.open(url);
				Log.v(TAG, "xmediaplayer open");
				ivPlay.setVisibility(View.INVISIBLE);
				ivPlay.setBackgroundResource(R.drawable.btn_activity_dairydetail_shiping_pause);
				ePlayStatus = EPlayStatus.OPENING;
			} else if (mMediaPlayer.getStatus() == XEffectMediaPlayer.STATUS_PAUSE) {
				mMediaPlayer.resume();
				Log.v(TAG, "xmediaplayer resume");
				ivPlay.setVisibility(View.INVISIBLE);
				ivPlay.setBackgroundResource(R.drawable.btn_activity_dairydetail_shiping_pause);
				ePlayStatus = EPlayStatus.PLAY;
			}
			else if(mMediaPlayer.getStatus() == XEffectMediaPlayer.STATUS_UNKNOW && ePlayStatus == EPlayStatus.PAUSE)
			{ //这种情况是UI控制了状态为暂停 但是实际由于surffaceview销毁导致播放已停止
			  //所以用ePlayStatus做标志，继续播放时从断点开始
				mMediaPlayer.open(url);
				Log.v(TAG, "xmediaplayer restart");
				ivPlay.setVisibility(View.INVISIBLE);
				ivPlay.setBackgroundResource(R.drawable.btn_activity_dairydetail_shiping_pause);
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
		ePlayStatus.seekPosition = 0;
		if(mMediaPlayer != null)
		{
//			ivVideoThumbnail.clearAnimation();
			ivVideoThumbnail.setVisibility(View.VISIBLE);
			mMediaPlayer.stop();
			mMediaPlayer.release();
			mMediaPlayer = null;
			Log.v(TAG, "xmediaplayer null");
			if(ivPlay != null)
			{
				ivPlay.setBackgroundResource(R.drawable.btn_activity_dairydetail_shiping_pause_play);
				ivPlay.setVisibility(View.VISIBLE);
			}
		}else{
			Log.e(TAG, "mMediaPlayer is null");
		}
		
		if (playVideoProcess != null)
		{
			playVideoProcess.setProgress(0);
		}
		
		// 结尾会出现乱码
		if (tvPlayTime != null)
		{
//			tvPlayTime.setText("00:00:00/"+DateUtils.getFormatTime(getVideoPlaytime()));
//			tvPlayTime.setVisibility(View.INVISIBLE);
			tvPlayTime.setText("00:00:00/"+DateUtils.getFormatTime(mTotlaTime));
		}
		// 标记归位
		if(ivPlay != null)
		{
			ivPlay.setBackgroundResource(R.drawable.btn_activity_dairydetail_shiping_pause_play);
		}
		setPlayBtnStatus(true);
		
	}
	
	// 暂停视频
	private void pauseVideo(){
		if(mMediaPlayer != null){
			if (mMediaPlayer.getStatus() == XEffectMediaPlayer.STATUS_PALYING) {
				mMediaPlayer.pause();
				ivPlay.setBackgroundResource(R.drawable.btn_activity_dairydetail_shiping_pause_play);
				ivPlay.setVisibility(View.VISIBLE);
				ePlayStatus = EPlayStatus.PAUSE;
				Log.v(TAG, "xmediaplayer pause");
			}
		}else{
			Log.e(TAG, "mMediaPlayer is null");
		}
	}
	
	// 发送邮件
	private void sendMail(String subject, String content){
		//系统邮件系统的动作为android.content.Intent.ACTION_SEND
		Intent email = new Intent(android.content.Intent.ACTION_SEND);
		email.setType("plain/text");
//		String[] emailReciver = new String[]{"looklook@gmail.com", "looklook@qq.com"};
		String emailSubject = subject;
		String emailBody = content;
		//设置邮件默认地址
//		email.putExtra(android.content.Intent.EXTRA_EMAIL, emailReciver);
		//设置邮件默认标题
		email.putExtra(android.content.Intent.EXTRA_SUBJECT, emailSubject);
		//设置要默认发送的内容
		email.putExtra(android.content.Intent.EXTRA_TEXT, emailBody);
		//调用系统的邮件系统
		startActivity(Intent.createChooser(email, "请选择邮件发送软件"));
	}
	
	// 发送短信
	private boolean sendMsg(String str) {
		Intent it = new Intent(Intent.ACTION_VIEW);

		it.putExtra("sms_body", str);

		it.setType("vnd.android-dir/mms-sms");

		startActivity(it);
		return true;
	}
	
	private void addDownloadTask(MyDiary myDiary) {
		if (mDiaryMenuMore != null)
		{
			mDiaryMenuMore.dismiss();
		}
		Prompt.Alert("已加入下载队列中");
		NetworkTaskInfo networktaskinfo = new NetworkTaskInfo(myDiary,
				INetworkTask.TASK_TYPE_DOWNLOAD);// 设置数据源
		DownloadNetworkTask downloadtask = new DownloadNetworkTask(networktaskinfo);// 创建上传/下载任务
		NetworkTaskManager.getInstance(userID).addTask(downloadtask);// 添加网络任务
		NetworkTaskManager.getInstance(userID).startNextTask(); // 开始任务队列
	}

	// 获取日记可见性设置
	private String getPublishTypeSetting() {
		return publishType;
	}

	// 获取日记位置可见性设置
	private String getPositionSetting() {
		return positionType;
	}

	// 获取日记语音可见性设置
	private String getAudioSetting() {
		return audioType;
	}

	// 判断当前日记是否是自己的日记
	private boolean isMyself() {
		if (myDiary != null) {
			Log.d(TAG, "myDiary.diaryid="+myDiary.diaryid);
			Log.d(TAG, "userID="+userID);
			Log.d(TAG, "myDiary.userid="+myDiary.userid);
			if (userID != null && userID.equals(myDiary.userid))
				return true;
		}
		return false;
	}
	
	// 判断当前日记是否已加入保险箱
	private boolean isInSaveBox() {
		if (myDiary != null) {
			if ("1".equals(myDiary.join_safebox))
				return true;
		}
		return false;
	}

	// 判断当前日记是否发布
	private boolean isPublished() {
		if (myDiary != null) {
			if ("2".equals(myDiary.diary_status))
				return true;
		}
		return false;
	}

	// 判断当前日记是否收藏
	private boolean isEnshrine() {
		return mDiaryManager.isEnshrine(myDiary);
	}

	// 判断是否赞过该日记
	private boolean isPraise() {
		return mDiaryManager.isPraise(myDiary);
	}

	// 判断该日记是否有副本
//	private boolean hasDuplicate() {
//		if ((myDiary.duplicate != null && myDiary.duplicate.length > 0) 
//				|| (myDiary.resourceuuid != null && !myDiary.resourceuuid.equals("")))
//			return true;
//		return false;
//	}
	
	//删除日记后更新数据
	private void updateDuplicate(){
		// 删除数据
		Iterator<MyDiary> ite = mListDiary.iterator();
		while(ite.hasNext())
		{
			MyDiary diary = ite.next();
			if(diary.diaryuuid != null && diary.diaryuuid.equals(myDiary.diaryuuid))
			{
				ite.remove();
				break;
			}
		}
		
		sortByDiaryDuplicate();
		
		if (mPageIndex >= mListDuplicate.size() && mPageIndex > 0)
		{
			--mPageIndex;
		}
		// 当详情页只有一篇日记时,被删除后返回上一页
		if (0 == mListDuplicate.size())
		{
			finish();
		}
		else
		{
			// 更新当前viewpager
			initUI();
			setCurrent();
		}
		Log.d(TAG, "pageIndex=" + mPageIndex);
		Log.d(TAG, "diaryDuplicates.size()=" + mListDuplicate.size());
	}

	// 列表需要刷新
	private void refreshData()
	{
		if(mListDiary != null)
		{
			for(int i = 0; i < mListDiary.size(); i++)
			{
				if(myDiary.diaryuuid != null && myDiary.diaryuuid.equals(mListDiary.get(i).diaryuuid))
				{
//					initData(mListDiary.get(i).diaryid);
					myDiary = mListDiary.get(i);
//					mPageIndex = i;
					sortByDiaryDuplicate();
					
					break;
				}
			}
		}
	}
	
	// 重新初始化数据
	private void initDataWithID(String diaryID) {
		if (mListDiary != null && mListDiary.size() > 0) {
			
			// 根据日记副本做排序
			sortByDiaryDuplicate();
			
			// 获取当前 mPageIndex
			if(mListDuplicate.size() == 0)
			{
				SortDiary sortDiary = new SortDiary();
				sortDiary.diaryID = diaryID;
				sortDiary.name = "原日记";
				mListDuplicate.add(sortDiary);
				ZDialog.show(R.layout.progressdialog, false, true, this);
				// 请求网络 设置日记高度和宽度
				Requester2.getDiaryinfo(handler, diaryID, "", "");
				return;
			}
			for (int i = 0; i < mListDuplicate.size(); i++) {
				if (diaryID != null && diaryID.equals(mListDuplicate.get(i).diaryID)) {
					mPageIndex = i;
					Log.d(TAG, "initData->pageIndex=" + mPageIndex);
					
					break;
				}
			}
			// 设置viewpager没页布局
			initUI();
//			// 设置当前页显示
			setCurrent();
		}else{
			SortDiary sortDiary = new SortDiary();
			sortDiary.diaryID = diaryID;
			sortDiary.name = "原日记";
			mListDuplicate.add(sortDiary);
			ZDialog.show(R.layout.progressdialog, false, true, this);
			// 请求网络 设置日记高度和宽度
			Requester2.getDiaryinfo(handler, diaryID, "", "");
		}
	}
	
	// 重新初始化数据
		private void initDataWithUUID(String diaryUUID) {
			if (mListDiary != null && mListDiary.size() > 0) {
				
				// 根据日记副本做排序
				sortByDiaryDuplicate();
				
				// 获取当前 mPageIndex
				for (int i = 0; i < mListDuplicate.size(); i++) {
					if (diaryUUID != null && diaryUUID.equals(mListDuplicate.get(i).diaryUUID)) {
						mPageIndex = i;
						Log.d(TAG, "initData->pageIndex=" + mPageIndex);
						
						break;
					}
				}
				// 设置viewpager没页布局
				initUI();
//				// 设置当前页显示
				setCurrent();
			}else{
				// 只给UUID不给list的情况下直接退出
				finish();
			}
		}
	
	private String getDiaryName(MyDiary myDiary){
		DiaryType diaryType=getDiaryType(myDiary);
		switch (diaryType) {
		case VEDIO:
			return "视频";
		case AUDIO:
			return "录音";
		case PICTURE:
			return "图片";
		case TEXT:
			return "便签";
		case NOMAIN:
			return "便签";
		default:
			return "便签";
		}
	}

	// 预处理dairymanager传过来的数据
//	private void prepareDiaryList()
//	{
//		Set<String> tmpSet = new HashSet<String>();
//		for(Iterator<MyDiary> ite = mListDiary.iterator(); ite.hasNext(); )
//		{
//			String s = ite.next().diaryid;
//			if(tmpSet.contains(s))
//			{
//				ite.remove();
//			}
//			else
//			{
//				tmpSet.add(s);
//			}
//		}
//	}
	
	// 排序获得当前需要显示的view
//	private void sortByDiaryDuplicate() {
//		prepareDiaryList();
//		
//		mListDuplicate.clear();
//		for(int i = 0; i < mListDiary.size(); i++)
//		{
//			// 基于mListDiary前部的主本为有序，否则必须先对主本排序
//			MyDiary diary = mListDiary.get(i);
//			if(diary.resourceuuid != null && diary.resourceuuid.equals(""))
//			{
//				MyDuplicate mduplicate = new GsonResponse2().new MyDuplicate();
//				mduplicate.diaryid = diary.diaryid;
//				mduplicate.duplicatename = getDiaryName(diary);
//				mListDuplicate.add(mduplicate);
//				if(diary.duplicate != null && diary.duplicate.length > 0)
//				{
//					for(int j = 0 ; j < diary.duplicate.length; j++)
//					{
//						for(int k = 0; k < mListDiary.size(); k++)
//						{//TODO 基于mListDiary前部的主本为有序K可以从i开始，否则k必须从0开始,如果副本顺序 还可以优化
//							MyDiary dupDiary = mListDiary.get(k);
//							if(dupDiary.resourcediaryid != null && !dupDiary.resourcediaryid.equals("") &&
///*									dupDiary.resourcediaryid == diary.diaryid &&*/
//									dupDiary.diaryid.equals(diary.duplicate[j].diaryid))
//							{
//								diary.duplicate[j].duplicatename="副本"+(j+1);
//								MyDuplicate sduplicate = new GsonResponse2().new MyDuplicate();
//								sduplicate.diaryid = dupDiary.diaryid;
//								sduplicate.duplicatename = "副本"+(j+1);;
//								mListDuplicate.add(sduplicate);
//								break;
//							}
//						}
//					}
//				}
//			}
//			else
//			{//TODO 基于mListDiary前部的主本为有序, 否则必须全序查询
////				break;
//			}
//		}
//		
//		Log.d(TAG, "diaryDuplicates.size()=" + mListDuplicate.size());
//		
//	}
	
	// 排序获得当前需要显示的view
	private void sortByDiaryDuplicate() {
		
		mListDuplicate.clear();
		
		for(int i = 0; i < mListDiary.size(); i++)
		{
			SortDiary sortDiary = new SortDiary();
			sortDiary.diaryID = mListDiary.get(i).diaryid;
			sortDiary.diaryUUID = mListDiary.get(i).diaryuuid;
			sortDiary.name = getDiaryName(mListDiary.get(i));
			mListDuplicate.add(sortDiary);
		}
		Log.d(TAG, "diaryDuplicates.size()=" + mListDuplicate.size());
		
	}

//	private boolean isExist(String diaryID) {
//		for (int i = 0; i < mListDuplicate.size(); i++) {
//			if (diaryID.equals(mListDuplicate.get(i).diaryid)) {
//				return true;
//			}
//		}
//		return false;
//	}

//	private void addFirst(MyDiary myDiary) {
//		if (myDiary.duplicate != null && myDiary.duplicate.length > 0) {// 该日记有副本
//			if (!isExist(myDiary.diaryid)) {
//				MyDuplicate duplicate = new GsonResponse2().new MyDuplicate();
//				duplicate.diaryid = myDiary.diaryid;
//				duplicate.duplicatename = getDiaryName(myDiary);
//				ArrayList<MyDuplicate> temp = new ArrayList<MyDuplicate>();
//				temp.add(duplicate);
//				for (int i = 0; i < myDiary.duplicate.length; i++) {
//					temp.add(myDiary.duplicate[i]);
//				}
//				for (int i = 0; i < temp.size(); i++) {// 反序添加到头部
//					mListDuplicates.addFirst(temp.get(temp.size() - i - 1));
//				}
//			}
//		} else {// 该日记无副本
//			for (int i = 0; i < mListDiary.size(); i++) {// 从diaryList中寻找该副本的主本
//				MyDuplicate[] duplicate = mListDiary.get(i).duplicate;
//				if (duplicate != null && duplicate.length > 0) {
//					for (int j = 0; j < duplicate.length; j++) {
//						if (myDiary.diaryid.equals(duplicate[j].diaryid)
//								&& !isExist(myDiary.diaryid)) {// diaryList中找到该副本的主本并且diaryDuplicates中不存在时
//							MyDuplicate mainDuplicate = new GsonResponse2().new MyDuplicate();
//							mainDuplicate.diaryid = mListDiary.get(i).diaryid;
//							mainDuplicate.duplicatename = getDiaryName(mListDiary.get(i));
//							ArrayList<MyDuplicate> temp = new ArrayList<MyDuplicate>();
//							temp.add(mainDuplicate);
//							if (mListDiary.get(i).duplicate != null
//									&& mListDiary.get(i).duplicate.length > 0) {
//								for (int k = 0; k < mListDiary.get(i).duplicate.length; k++) {// 添加副本
//									temp.add(mListDiary.get(i).duplicate[k]);
//								}
//							}
//							for (int k = 0; k < temp.size(); k++) {// 反序添加到头部
//								mListDuplicates.addFirst(temp.get(temp.size()
//										- k - 1));
//							}
//							break;
//						}
//					}
//				} else {
//					if (!isExist(myDiary.diaryid)) {
//						MyDuplicate secDuplicate = new GsonResponse2().new MyDuplicate();
//						secDuplicate.diaryid = myDiary.diaryid;
//						secDuplicate.duplicatename = getDiaryName(myDiary);
//						mListDuplicates.addFirst(secDuplicate);
//					}
//				}
//			}
//		}
//	}
//
//	private void addLast(MyDiary myDiary) {
//		if (myDiary.duplicate != null && myDiary.duplicate.length > 0) {// 该日记有副本
//			if (!isExist(myDiary.diaryid)) {
//				MyDuplicate duplicate = new GsonResponse2().new MyDuplicate();
//				duplicate.diaryid = myDiary.diaryid;
//				duplicate.duplicatename = getDiaryName(myDiary);
//				mListDuplicates.addLast(duplicate);
//				for (int i = 0; i < myDiary.duplicate.length; i++) {// 正序添加到尾部
//					mListDuplicates.addLast(myDiary.duplicate[i]);
//				}
//			}
//		} else {// 该日记无副本
//			for (int i = 0; i < mListDiary.size(); i++) {// 从diaryList中寻找该副本的主本
//				MyDuplicate[] duplicate = mListDiary.get(i).duplicate;
//				if (duplicate != null && duplicate.length > 0) {
//					for (int j = 0; j < duplicate.length; j++) {
//						if (myDiary.diaryid.equals(duplicate[j].diaryid)
//								&& !isExist(myDiary.diaryid)) {// diaryList中找到该副本的主本
//							MyDuplicate mainDuplicate = new GsonResponse2().new MyDuplicate();
//							mainDuplicate.diaryid = mListDiary.get(i).diaryid;
//							mainDuplicate.duplicatename = getDiaryName(myDiary);
//							mListDuplicates.addLast(mainDuplicate);
//							if (mListDiary.get(i).duplicate != null
//									&& mListDiary.get(i).duplicate.length > 0) {
//								for (int k = 0; k < mListDiary.get(i).duplicate.length; k++) {// 添加副本
//																								// 正序添加到尾部
//									mListDuplicates
//											.addLast(mListDiary.get(i).duplicate[i]);
//								}
//							}
//						}
//					}
//				} else {
//					if (!isExist(myDiary.diaryid)) {
//						MyDuplicate secDuplicate = new GsonResponse2().new MyDuplicate();
//						secDuplicate.diaryid = myDiary.diaryid;
//						secDuplicate.duplicatename = getDiaryName(myDiary);
//						mListDuplicates.addLast(secDuplicate);
//					}
//				}
//			}
//		}
//	}

	// 根据mPageIndex初始化页面
	private void setCurrent() {
		Log.d(TAG, "setCurrent->pageIndex=" + mPageIndex);
		// 根据mPageIndex获取myDiary
		if(mPageIndex >= mListDuplicate.size())
		{
			// 避免数据的错误
			mPageIndex = mListDuplicate.size() - 1;
		}
		SortDiary sortDiary = mListDuplicate.get(mPageIndex);
		boolean isFounded = false;
		for (int i = 0; i < mListDiary.size(); i++) {
			if(sortDiary.diaryUUID != null)
			{
				if (sortDiary.diaryUUID.equals(mListDiary.get(i).diaryuuid)) {
					myDiary = mListDiary.get(i);
					
					// 似乎需要重新取一次，myDiary不能更新
	//				myDiary.sns = mDiaryManager.getSnsTrace(myDiary.diaryid);
					isFounded = true;
					break;
				}
			}
			else
			{
				if (sortDiary.diaryID.equals(mListDiary.get(i).diaryid)) {
					myDiary = mListDiary.get(i);
					
					// 似乎需要重新取一次，myDiary不能更新
	//				myDiary.sns = mDiaryManager.getSnsTrace(myDiary.diaryid);
					isFounded = true;
					break;
				}
			}
		}
		if (isFounded && myDiary.diary_status != null) {// 在diaryList中找到日记 切日记有效
			// 设置当前页中的顶部栏和底部栏UI
			if(isMyself()){
				int sync = getSync(myDiary.diaryuuid);
				if(sync > 1 || sync == -1 || NetworkTaskManager.getInstance(userID).getTask(myDiary.diaryuuid) != null)
				{
					ivShareOrTransfer
					.setBackgroundResource(R.drawable.btn_activity_diarydetail_share);
					ivShareOrTransfer.setTag("share");
				} else {// 未上传,显示上传和删除按钮
					ivShareOrTransfer
					.setBackgroundResource(R.drawable.btn_activity_diarydetail_upload);
					ivShareOrTransfer.setTag("upload");
				}
				initBtnComment();
			}else{
				ivComment
				.setBackgroundResource(R.drawable.btn_activity_diarydetail_comment);
				setCommentAndPraise();
				ivShareOrTransfer
				.setBackgroundResource(R.drawable.btn_activity_diarydetail_share);
				ivComment.setTag("comment");
				ivShareOrTransfer.setTag("share");
			}
			
			tvDiaryName.setText(sortDiary.name);
//			if (hasDuplicate()) {
//				ivXiala.setVisibility(View.VISIBLE);
//			} else {
//				ivXiala.setVisibility(View.INVISIBLE);
//			}
			
			//设置活动数据、编辑数据、赞数据
			if (isMyself()) {
				Log.d(TAG, "myDiary.active="+myDiary.active);
				if(isJoinActive()){//已参加活动时才显示活动按钮
					ivActvie.setVisibility(View.VISIBLE);
				}else{
					ivActvie.setVisibility(View.GONE);
				}
				ivEdit.setVisibility(View.VISIBLE);
				mFlPraise.setVisibility(View.GONE);
			} else {
				if(isJoinActive()){//已参加活动时才显示活动按钮
					ivActvie.setVisibility(View.VISIBLE);
				}else{
					ivActvie.setVisibility(View.GONE);
				}
				ivEdit.setVisibility(View.GONE);
				mFlPraise.setVisibility(View.VISIBLE);
				if(isPraise())
				{
					setPraiseBtn(EPraiseType.PRAISE);
				}
				else
				{
					setPraiseBtn(EPraiseType.UNPRAISE);
				}
			}
			// 设置录音按钮
			if (isRecordActive())
			{
				ivRecord.setVisibility(View.VISIBLE);
			}
			else
			{
				ivRecord.setVisibility(View.GONE);
			}
			//设置天气
			setWeather();
			//设置位置
			setPosition();
			//设置标签
			setTags();
			//设置中间可滑动区域数据
			setDiaryData();
			// 尝试设置心情（可能为空）
			setMood();
		} else {// 未在diaryList中找到日记，请求网络
			ivComment.setEnabled(false);
			ivShareOrTransfer.setEnabled(false);
			ZDialog.show(R.layout.progressdialog, false, true, this);
			// 请求网络 设置日记高度和宽度
			Requester2.getDiaryinfo(handler, sortDiary.diaryID, "", "");
		}
	}
	
	// 设置评论数等
	private void setCommentAndPraise()
	{
		if(myDiary.commentcount == null || myDiary.commentcount.equals("0") || !DateUtils.isNum(myDiary.commentcount))
		{
			tvCommentNum.setVisibility(View.GONE);
		}
		else
		{
			tvCommentNum.setVisibility(View.VISIBLE);
			tvCommentNum.setText(formatCommentCount(myDiary.commentcount));
		}
//		if(myDiary.collectcount == null || myDiary.collectcount.equals("0") || !DateUtils.isNum(myDiary.collectcount))
//		{
//			tvCollectNum.setVisibility(View.GONE);
//		}
//		else
//		{
//			tvCollectNum.setVisibility(View.VISIBLE);
//			tvCollectNum.setText(formatCommentCount(myDiary.collectcount));
//		}
		if(myDiary.enjoycount == null || myDiary.enjoycount.equals("0") || !DateUtils.isNum(myDiary.enjoycount))
		{
			tvCollectNum.setVisibility(View.GONE);
		}
		else
		{
			tvCollectNum.setVisibility(View.VISIBLE);
			tvCollectNum.setText(formatCommentCount(myDiary.enjoycount));
		}
	}
	
	// 处理评论数等
	private String formatCommentCount(String count)
	{
		int i = Integer.parseInt(count);
		if (i > 99)
		{
			return "99+";
		}
		return String.valueOf(i);
	}
	
	private void setMood(){
		if(myDiary.mood!=null&&myDiary.mood.length()>0){
			try {
				Log.d(TAG, "myDiary.mood="+myDiary.mood);
				int position=Integer.parseInt(myDiary.mood);
				if(position<moods.length)
					ivMood.setImageResource(moods[position]);
			} catch (NumberFormatException e) {
				e.printStackTrace();
			}
		}else{
			ivMood.setImageResource(moods[0]);
		}
	}
	
	//已发布日记显示评论，未发布日记显示删除按钮
	private void initBtnComment(){
		if(isPublished()){
			ivComment
			.setBackgroundResource(R.drawable.btn_activity_diarydetail_comment);
			setCommentAndPraise();
			ivComment.setTag("comment");
		}else{
			ivComment
			.setBackgroundResource(R.drawable.btn_activity_homepage_remove);
			tvCommentNum.setVisibility(View.GONE);
			tvCollectNum.setVisibility(View.GONE);
			ivComment.setTag("delete");
		}
	}
	
	//设置中间可滑动区域数据
	private void setDiaryData(){
		Log.d(TAG, "setDiaryData");
		View v=((MyPageAdapter)mDiarydetail_viewpager.getAdapter()).getView(mPageIndex);
		if(v.getTag()!=null){
			DiaryType type=(DiaryType) v.getTag();
			switch (type) {
			case VEDIO:
				loadVideoData(v);
				break;
			case AUDIO:
				loadAudioData(v);
				break;
			case PICTURE:
				loadPictureData(v);
				break;
			case TEXT:
				loadTextData(v);
				break;
			case NOMAIN:
				loadNomainData(v);
				break;
			default:
				break;
			}
		}else{
			Log.e(TAG, "setDiaryData view's tag is null");
		}
		
	}
	
	public static class MediaStatus
	{
		EPlayStatus status = EPlayStatus.NON;
		String playPath = null;
		String videoTime = null;
		String thumbUrl = null;
		double curTime = 0d;
	}
	
	public static MediaStatus sMediaStatus = null; 
	
	//播放时间等控件
	private XMediaPlayer mMediaPlayer = null;
	private TextView tvPlayTime;
	private ImageView ivPlay;
	private WebImageView ivVideoThumbnail;
	private TextView tvDesc;
	private TackView tvTack;
	private ImageView ivExternPlayer;
	private RelativeLayout rlVideoContent;
	private boolean vidoeLock = false;
	private RelativeLayout rlVideoControll;
	private int videoPaddingBottom = 0;
	//设置视频布局数据
	private void loadVideoData(View v){
//		sMediaStatus = new MediaStatus();
		CmmobiClickAgentWrapper.onEvent(this, "details_type", 3);
		Log.v(TAG, "load video");
		ivPlay=(ImageView) v.findViewById(R.id.iv_play);
		ivPlay.setOnClickListener(this);
		rlVideoContent = (RelativeLayout) v.findViewById(R.id.rl_video_content);
		rlVideoContent.setOnClickListener(this);
		RelativeLayout.LayoutParams parm = new RelativeLayout.LayoutParams(LayoutParams.FILL_PARENT,LayoutParams.FILL_PARENT);
		parm.setMargins(100, 100, 100, 100);
		rlVideoContent.setLayoutParams(parm);
		tvDesc=(TextView) v.findViewById(R.id.tv_diarydetail_desc);
		ivVideoThumbnail = (WebImageView) v.findViewById(R.id.iv_video_thumbnail);
		LinearLayout top = (LinearLayout) v.findViewById(R.id.ll_video_top);
		top.setOnClickListener(this);
		rlVideoControll = (RelativeLayout) v.findViewById(R.id.rl_diarydetail_parm);
		String videoCover = getVideoCover();
		if (videoCover != null && videoCover.length() > 0) {
			Log.v(TAG, "mThumbUrl = "+videoCover);
			ivVideoThumbnail.setImageUrl(0, 1, videoCover, false);
			ivVideoThumbnail.setVisibility(View.VISIBLE);
		}
		
		try
		{
			stopVideo();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		if(null == mMediaPlayer && rlVideoContent != null)
		{
			rlVideoContent.removeAllViews();
			XEffects mEffects = null;
			if (PluginUtils.isPluginMounted()) {
				mEffects = new XEffects();
			}
			mMediaPlayer = new XMediaPlayer(this, mEffects, false);
			mMediaPlayer.setListener(new MyOnInfoListener());
			RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(LayoutParams.FILL_PARENT,LayoutParams.FILL_PARENT);
			rlVideoContent.addView((View)mMediaPlayer.getXSurfaceView(), params);
			
			Log.v(TAG, "xmediaplayer create");
		}

		String videoUrl=getVideoUrl();
//		videoUrl="http://1s.looklook.cn:8082/pub/looklook/video_pub/original/2013/08/07/154816b087fa8691c44d13b190e11bd6933c00.mp4";
		Log.d(TAG, "getVideoUrl="+videoUrl);
		ivPlay.setTag(videoUrl);
		playVideoProcess=(SeekBar) v.findViewById(R.id.sk_diarydetail_seek);
		playVideoProcess.setMax(100);
		playVideoProcess.setProgress(0);
		playVideoProcess.setOnSeekBarChangeListener(new OnSeekBarChangeListener()
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
		//播放时间
		tvPlayTime=(TextView) v.findViewById(R.id.tv_activity_shiping_shijian);
//		tvPlayTime.setText("00:00:00/"+DateUtils.getFormatTime(getVideoPlaytime()));
		tvPlayTime.setVisibility(View.INVISIBLE);
//		tvPlayTime.setText("00:00:00/"+getVideoPlaytime());
		ivExternPlayer = (ImageView) v.findViewById(R.id.iv_activity_shiping_quanping);
		ivExternPlayer.setOnClickListener(this);
		ivMood=(ImageView) v.findViewById(R.id.iv_diarydetail_xinqing);
		tvTack=(TackView) v.findViewById(R.id.iv_diarydetail_video_tack);
		tvTack.setBackground(R.drawable.btn_activity_yuantupian_bofangluyin);
		//设置短录音播放路径
		String shortRecUrl=getShortRecUrl();
		Log.d(TAG, "getShortRecUrl="+shortRecUrl);
		if(shortRecUrl!=null&&shortRecUrl.length()>0){
			tvTack.setTag(shortRecUrl);
			tvTack.setOnClickListener(this);
			tvTack.setVisibility(View.VISIBLE);
			tvTack.setPlaytime(DateUtils.getPlayTime(getShortRecPlaytime()));
		}else{
			tvTack.setVisibility(View.GONE);
		}
		if(isMyself())//自己的日记才能修改心情
			ivMood.setOnClickListener(this);
//		tvDesc.setText(getTextDes());
		replacedExpressions(getTextDes(), tvDesc);
		setVideoLayout();
		
		videoPaddingBottom = rlVideoControll.getPaddingBottom();
		
		mDiarydetail_viewpager.setInterceptListener(new DiaryPagerTouchInterface()
		{

			@Override
			public void setIntercept(boolean intercept)
			{
			}

			@Override
			public boolean isIntercept()
			{
				return getLockStatus();
			}
		});
	}
	
	// 锁住全屏时其他按钮
	private void lockVideoFrame(boolean lock)
	{
		vidoeLock = lock;
	}
	
	private boolean getLockStatus()
	{
		return vidoeLock;
	}
	
	// 切换视频布局界面
	private void setVideoLayout()
	{
		if(isFullScreenMode){
			ivMood.setVisibility(View.INVISIBLE);
			if(View.GONE!=tvTack.getVisibility()){
				tvTack.setVisibility(View.INVISIBLE);
			}
			tvDesc.setVisibility(View.INVISIBLE);
			vTitlebar.setVisibility(View.INVISIBLE);
			vDiaryInfoTile.setVisibility(View.INVISIBLE);
			vBottombar.setVisibility(View.INVISIBLE);
			ivRecord.setVisibility(View.GONE);
		}else{
			if(View.INVISIBLE==tvTack.getVisibility()){
				tvTack.setVisibility(View.VISIBLE);
			}
			tvDesc.setVisibility(View.VISIBLE);
			// 设置心情 如果没有辅内容的日记不显示心情，与录音按钮正好相反
			if (isRecordActive() && isMyself())
			{
				ivMood.setVisibility(View.INVISIBLE);
				ivRecord.setVisibility(View.VISIBLE);
			}
			else
			{
				ivMood.setVisibility(View.VISIBLE);
				ivRecord.setVisibility(View.GONE);
			}
			vTitlebar.setVisibility(View.VISIBLE);
			vDiaryInfoTile.setVisibility(View.VISIBLE);
			vBottombar.setVisibility(View.VISIBLE);
		}
	}
	
	// 切换视频布局界面
	private void setExternVideoLayout()
	{
		Window a;
		if(isFullScreenMode){
			ivMood.setVisibility(View.GONE);
			if(View.GONE!=tvTack.getVisibility()){
				tvTack.setVisibility(View.INVISIBLE);
			}
			tvDesc.setVisibility(View.GONE);
			vTitlebar.setVisibility(View.INVISIBLE);
			vDiaryInfoTile.setVisibility(View.INVISIBLE);
			vBottombar.setVisibility(View.GONE);
			ivRecord.setVisibility(View.GONE);
			
			rlVideoControll.setPadding(0, 0, 0, 0);
			RelativeLayout.LayoutParams parm = new RelativeLayout.LayoutParams(LayoutParams.FILL_PARENT,LayoutParams.FILL_PARENT);
			parm.setMargins(0, 0, 0, 0);
			rlVideoContent.setLayoutParams(parm);
		}else{
			if(View.INVISIBLE==tvTack.getVisibility()){
				tvTack.setVisibility(View.VISIBLE);
			}
			tvDesc.setVisibility(View.VISIBLE);
			// 设置心情 如果没有辅内容的日记不显示心情，与录音按钮正好相反
			if (isRecordActive() && isMyself())
			{
				ivMood.setVisibility(View.INVISIBLE);
				ivRecord.setVisibility(View.VISIBLE);
			}
			else
			{
				ivMood.setVisibility(View.VISIBLE);
				ivRecord.setVisibility(View.GONE);
			}
			vTitlebar.setVisibility(View.VISIBLE);
			vDiaryInfoTile.setVisibility(View.VISIBLE);
			vBottombar.setVisibility(View.VISIBLE);
			
			rlVideoControll.setPadding(0, 0, 0, videoPaddingBottom);
			RelativeLayout.LayoutParams parm = new RelativeLayout.LayoutParams(LayoutParams.FILL_PARENT,LayoutParams.FILL_PARENT);
			parm.setMargins(100, 100, 100, 100);
			rlVideoContent.setLayoutParams(parm);
		}
	}
	
	// 设置全屏播放
	private void setFullSreen(boolean isFull)
	{
		if(isFull)
		{
			isFullScreenMode=true;
			ivExternPlayer.setBackgroundResource(R.drawable.btn_activity_dairydetail_shiping_quanping_restore);
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
			lockVideoFrame(true);
		}
		else
		{
			isFullScreenMode=false;
			ivExternPlayer.setBackgroundResource(R.drawable.btn_activity_dairydetail_shiping_quanping);
			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
			lockVideoFrame(false);
		}
	}
	
	//获取短录音播放时间
	private String getShortRecPlaytime(){
		for(int i=0;i<myDiary.attachs.length;i++){
			diaryAttach attach=myDiary.attachs[i];
			if("0".equals(attach.attachlevel)&&"2".equals(attach.attachtype)){
				if(attach.playtime!=null&&attach.playtime.length()>0){
					Log.d(TAG, "getShortRecPlaytime->playtime="+attach.playtime);
					return attach.playtime;
				}
			}
		}
		return "";
	}
	
	//获取视频播放时间
//	private double getVideoPlaytime(){
//		for(int i=0;i<myDiary.attachs.length;i++){
//			diaryAttach attach=myDiary.attachs[i];
//			if("1".equals(attach.attachlevel)&&"1".equals(attach.attachtype)){
//				if(attach.playtime!=null&&attach.playtime.length()>0){
//					Log.d(TAG, "getVideoPlaytime->playtime="+attach.playtime);
//					double playtime=0;
//					try {
//						playtime=Double.parseDouble(attach.playtime);
//					} catch (NumberFormatException e) {
//						e.printStackTrace();
//					}
//					return playtime;
//				}
//			}
//		}
//		return 0;
//	}
//	private String getVideoPlaytime()
//	{
//		for (int i = 0; i < myDiary.attachs.length; i++)
//		{
//			diaryAttach attach = myDiary.attachs[i];
//			if ("1".equals(attach.attachlevel) && "1".equals(attach.attachtype))
//			{
//				if (attach.playtime != null && attach.playtime.length() > 0)
//				{
//					Log.d(TAG, "getVideoPlaytime->playtime=" + attach.playtime);
//					return attach.playtime;
//				}
//			}
//		}
//		return "00:00:00";
//	}
	
	//获取视频播放Url
	private String getVideoUrl(){
		for(int i=0;i<myDiary.attachs.length;i++){
			diaryAttach attach=myDiary.attachs[i];
			if("1".equals(attach.attachlevel)&&"1".equals(attach.attachtype)){
				if(attach.attachvideo!=null&&attach.attachvideo.length>0){
//					String url = "";
//					for(int j=0;j<attach.attachvideo.length;j++){
//						if(attach.attachvideo[j].playvideourl!=null&&attach.attachvideo[j].playvideourl.length()>0){
//							url=attach.attachvideo[j].playvideourl;
//						}else{
//							url=attach.attachuuid;
//						}
//						String uid = ActiveAccount
//								.getInstance(MainApplication.getAppInstance()).getUID();
//						MediaValue mv = AccountInfo.getInstance(uid).mediamapping.getMedia(uid,
//								url);
//						if (MediaValue.checkMediaAvailable(mv, 5)) {
//							Log.d(TAG, "getVideoUrl map found url="+url);
//							return Environment.getExternalStorageDirectory()+mv.path;
//						}
//					}
					
					/*	a)	自己看自己视频日记，本地与云端，优先播放本地的，本地没有播放云端的，云端视频优先播放无标视频（即类型1或2），如果没有无标视频，则播放转码视频（即类型3或4）。
						b)	自己看他人视频日记，看云端视频，云端视频优先看转压视频（即类型3或4），如果没有转压视频，则播放无标视频（即1或2）。
						c)	原高清视频（即类型0），只能由视频主人下载，云端的高清视频客户端不能直接播放。
						d)	如果播放地址不存在，或不能播放，则提示“网络出错，请稍后再试”。
						e)	类型说明：0：原视频高清 1:原视频普清 2：普清转码未压标 3普清转压4高清转压*/
					//本地没有视频
					String height = null;
					String normal = null;
					String undecode = null;
					String normaldecode = null;
					String heightdecode = null;
					
					for(int j=0;j<attach.attachvideo.length;j++){
						if("0".equals(attach.attachvideo[j].videotype)&&attach.attachvideo[j].playvideourl!=null&&attach.attachvideo[j].playvideourl.length()>0){
							height=attach.attachvideo[j].playvideourl;
						}
						if("1".equals(attach.attachvideo[j].videotype)&&attach.attachvideo[j].playvideourl!=null&&attach.attachvideo[j].playvideourl.length()>0){
							normal=attach.attachvideo[j].playvideourl;
						}
						if("2".equals(attach.attachvideo[j].videotype)&&attach.attachvideo[j].playvideourl!=null&&attach.attachvideo[j].playvideourl.length()>0){
							undecode=attach.attachvideo[j].playvideourl;
						}
						if("3".equals(attach.attachvideo[j].videotype)&&attach.attachvideo[j].playvideourl!=null&&attach.attachvideo[j].playvideourl.length()>0){
							normaldecode=attach.attachvideo[j].playvideourl;
						}
						if("4".equals(attach.attachvideo[j].videotype)&&attach.attachvideo[j].playvideourl!=null&&attach.attachvideo[j].playvideourl.length()>0){
							heightdecode=attach.attachvideo[j].playvideourl;
						}
					}
					
					if(isMyself()){//自己视频
						if(height != null)
						{
							// 高清视频在本地才使用高清视频
							String uid = ActiveAccount
									.getInstance(MainApplication.getAppInstance()).getUID();
							MediaValue mv = AccountInfo.getInstance(uid).mediamapping.getMedia(uid,
									height);
							if (MediaValue.checkMediaAvailable(mv, 5)) {
								return height;
							}
						}
						if(normal != null)
						{
							return normal;
						}
						else if(undecode != null)
						{
							return undecode;
						}
						else if(normaldecode != null)
						{
							return normaldecode;
						}
						else if(heightdecode != null)
						{
							return heightdecode;
						}
							
					}else{//他人视频
						if(normaldecode != null)
						{
							return normaldecode;
						}
						else if(heightdecode != null)
						{
							return heightdecode;
						}
						else if(normal != null)
						{
							return normal;
						}
						else if(undecode != null)
						{
							return undecode;
						}
					}
					return attach.attachuuid;
				}
			}
		}
		return null;
	}
	
	// 根据日记结构体中的url获取实际播放url
	private String getVideoPath(String url)
	{
		// 自己的日记 优先播放已存在的（高清视频可能刚下完）
		if(isMyself())
		{
			for(int i=0;i<myDiary.attachs.length;i++){
				diaryAttach attach=myDiary.attachs[i];
				if("1".equals(attach.attachlevel)&&"1".equals(attach.attachtype)){
					if(attach.attachvideo!=null&&attach.attachvideo.length>0){
						for(int j=0;j<attach.attachvideo.length;j++){
							String locUrl = "";
							if(attach.attachvideo[j].playvideourl!=null&&attach.attachvideo[j].playvideourl.length()>0){
								locUrl=attach.attachvideo[j].playvideourl;
							}else{
								locUrl=attach.attachuuid;
							}
							String uid = ActiveAccount
									.getInstance(MainApplication.getAppInstance()).getUID();
							MediaValue mv = AccountInfo.getInstance(uid).mediamapping.getMedia(uid,
									locUrl);
							if (MediaValue.checkMediaAvailable(mv, 5)) {
								Log.d(TAG, "getVideoUrl map found url="+locUrl);
								return Environment.getExternalStorageDirectory()+mv.path;
							}
						}
					}
				}
			}
		}
		
		if (null == url || 0 == url.length()) {
			for(int i=0;i<myDiary.attachs.length;i++){
				diaryAttach attach=myDiary.attachs[i];
				if("1".equals(attach.attachlevel)&&"1".equals(attach.attachtype)){
					url=attach.attachuuid;
				}
			}
		}
		if((null == url || 0 == url.length())){
			Log.e(TAG, "url is null");
			return null;
		}
		String uid = ActiveAccount.getInstance(MainApplication.getAppInstance()).getUID();
		MediaValue mv = AccountInfo.getInstance(uid).mediamapping.getMedia(uid, url);
		if (MediaValue.checkMediaAvailable(mv, 5))
		{
			return Environment.getExternalStorageDirectory() + mv.path;
		}
		return url;
	}
	
	//获取视频封面
	private String getVideoCover()
	{
		String url = "";
		for (int i = 0; i < myDiary.attachs.length; i++)
		{
			diaryAttach attach = myDiary.attachs[i];
			if (attach.attachtype.equals("1") && attach.videocover != null)
			{
				url = attach.videocover;
				break;
			}
		}
		return url;
	}

	ImageView ivLeftWheel = null;
	ImageView ivRightWheel = null;
	ImageView ivLeftStaff = null;
	ImageView ivRightStaff = null;
	ImageView ivAudioPlay = null;
	//设置音频布局数据
	private void loadAudioData(View v){
		v.findViewById(R.id.rl_activity_yuanluyin).setOnClickListener(this);
		ivMood=(ImageView) v.findViewById(R.id.iv_diarydetail_xinqing);
		ImageView ivPlay=(ImageView) v.findViewById(R.id.btn_yuanluyin_play);
		ImageView ivPause=(ImageView) v.findViewById(R.id.btn_yuanluyin_pause);
		ImageView ivStop=(ImageView) v.findViewById(R.id.btn_yuanluyin_stop);
		//播放状态图片
		ivAudioPlay = (ImageView) v.findViewById(R.id.iv_audio_play);
		//文字描述
		tvDesc=(TextView) v.findViewById(R.id.tv_diarydetail_desc);
		playAudioProcess = (SeekBar) v.findViewById(R.id.sk_diarydetail_seek);
		playAudioProcess.setMax(100);
		playAudioProcess.setProgress(0);
		playAudioProcess.setOnTouchListener(new OnTouchListener()
		{
			@Override
			public boolean onTouch(View v, MotionEvent event)
			{
				return true;
			}
		});
		 
		tvTack=(TackView) v.findViewById(R.id.iv_diarydetail_audio_tack);
		tvTack.setBackground(R.drawable.btn_activity_yuantupian_bofangluyin);
		
		ivLeftWheel = (ImageView) v.findViewById(R.id.yuanluyin_dalun_zuo);
		ivRightWheel = (ImageView) v.findViewById(R.id.yuanluyin_dalun_you);
		ivLeftStaff = (ImageView) v.findViewById(R.id.yuanluyin_xiaolun_zuo);
		ivRightStaff = (ImageView) v.findViewById(R.id.yuanluyin_xiaolun_you);
		
		//设置短录音播放路径
		String shortRecUrl=getShortRecUrl();
		Log.d(TAG, "getShortRecUrl="+shortRecUrl);
		if(shortRecUrl!=null&&shortRecUrl.length()>0){
			tvTack.setTag(shortRecUrl);
			tvTack.setHandler(null);
			tvTack.setOnClickListener(this);
			tvTack.setVisibility(View.VISIBLE);
			tvTack.setPlaytime(DateUtils.getPlayTime(getShortRecPlaytime()));
		}else{
			tvTack.setVisibility(View.GONE);
		}
		//设置长录音播放路径
		String longRecUrl=getLongRecUrl();
		if(longRecUrl!=null&&longRecUrl.length()>0){
			Log.d(TAG, "longRecUrl="+longRecUrl);
			ivPlay.setTag(longRecUrl);
		}
		ivPlay.setOnClickListener(this);
		ivPause.setOnClickListener(this);
		ivStop.setOnClickListener(this);

		if(isMyself())//自己的日记才能修改心情
			ivMood.setOnClickListener(this);
		//设置日记文字
//		tvText.setText(getTextDes());
		replacedExpressions(getTextDes(), tvDesc);
		
		setAudioLayout();
		
	}
	
	private void setAudioLayout()
	{
		if(isFullScreenMode){
			tvDesc.setVisibility(View.INVISIBLE);
			ivMood.setVisibility(View.INVISIBLE);
			if(View.GONE!=tvTack.getVisibility()){
				tvTack.setVisibility(View.INVISIBLE);
			}
			vTitlebar.setVisibility(View.INVISIBLE);
			vDiaryInfoTile.setVisibility(View.INVISIBLE);
			vBottombar.setVisibility(View.INVISIBLE);
			ivRecord.setVisibility(View.GONE);
		}else{
			if(View.INVISIBLE==tvTack.getVisibility()){
				tvTack.setVisibility(View.VISIBLE);
			}
			tvDesc.setVisibility(View.VISIBLE);
			// 设置心情 如果没有辅内容的日记不显示心情，与录音按钮正好相反
			if (isRecordActive() && isMyself())
			{
				ivMood.setVisibility(View.INVISIBLE);
				ivRecord.setVisibility(View.VISIBLE);
			}
			else
			{
				ivMood.setVisibility(View.VISIBLE);
				ivRecord.setVisibility(View.GONE);
			}
			vTitlebar.setVisibility(View.VISIBLE);
			vDiaryInfoTile.setVisibility(View.VISIBLE);
			vBottombar.setVisibility(View.VISIBLE);
		}
	}
	
	private WebImageView ivPicture;
	private ImageLoader imageLoader = null;
	private DisplayImageOptions imageLoaderOptions = null;
	private ImageLoadingListener imageLoadingListener = null;
	
	//设置图片布局数据
	private void loadPictureData(View v){
		CmmobiClickAgentWrapper.onEvent(this, "details_type", 1);
		
//		if(ivPicture != null)
//		{
//			try
//			{
//				Log.v(TAG, "bitmap recycle");
//				ivPicture.recycle();
//			}
//			catch (Exception e)
//			{
//				e.printStackTrace();
//			}
//		}
		
		v.findViewById(R.id.rl_activity_yuantupian).setOnClickListener(this);
		ivPicture = (WebImageView) v.findViewById(R.id.iv_picture);
		LinearLayout top = (LinearLayout) v.findViewById(R.id.ll_pic_top);
		top.setOnClickListener(this);
		
		// 查找缓存是否有图片 没有使用缩略图
		// 修改使用imageLoader
		imageLoader = ImageLoader.getInstance();
		imageLoader.init(ImageLoaderConfiguration.createDefault(DiaryDetailActivity.this));
		
//		ImageLoaderConfiguration config = new
//				ImageLoaderConfiguration.Builder(DiaryDetailActivity.this)
//				.memoryCacheExtraOptions(2048, 2048)
//				.build();
//		imageLoader.init(config);

		imageLoaderOptions = new DisplayImageOptions.Builder()
				.showStubImage(R.drawable.dot_big)
				.showImageForEmptyUri(R.drawable.dot_big)
				.showImageOnFail(R.drawable.dot_big)
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
				ivPicture.setCenter();
			}
		};
		
		String url = getImageUrl();
		Log.d(TAG, "imageUrl=" + url);
		String uid = ActiveAccount.getInstance(MainApplication.getAppInstance()).getUID();
		MediaValue mv = AccountInfo.getInstance(uid).mediamapping.getMedia(uid, url);
		if (MediaValue.checkMediaAvailable(mv, 2))
		{
//			Log.d(TAG, "use imageUrl=" + url);
//			ivPicture.setImageUrl(0, 1, url, false, true);
			url = "file://" + Environment.getExternalStorageDirectory() + mv.path;
			Log.d(TAG, "use imageLoader=" + url);
			imageLoader.displayImage(url, ivPicture,
					imageLoaderOptions, imageLoadingListener,
					ActiveAccount.getInstance(this).getUID(), 1);
		}
		else
		{
			String thumbUrl = getThumbUrl(myDiary);
			if (thumbUrl != null && thumbUrl.length() > 0)
			{
				Log.d(TAG, "use thumbUrl=" + thumbUrl);
				ivPicture.setImageUrl(0, 1, thumbUrl, false);
//				imageLoader.displayImage(thumbUrl, ivPicture,
//						imageLoaderOptions, imageLoadingListener,
//						ActiveAccount.getInstance(this).getUID(), 1);
			}
			else
			{
//				ivPicture.setImageUrl(0, 1, url, false);
				imageLoader.displayImage(url, ivPicture,
						imageLoaderOptions, imageLoadingListener,
						ActiveAccount.getInstance(this).getUID(), 1);
			}
		}
		
		ivMood=(ImageView) v.findViewById(R.id.iv_diarydetail_xinqing);
//		playProcess=(SeekBar) v.findViewById(R.id.sk_diarydetail_seek);
//		playProcess.setMax(100);
//		playProcess.setProgress(0);
		tvDesc=(TextView) v.findViewById(R.id.tv_diarydetail_desc);
		tvTack=(TackView) v.findViewById(R.id.iv_diarydetail_tack);
		tvTack.setBackground(R.drawable.btn_activity_yuantupian_bofangluyin);
		if(isMyself())//自己的日记才能修改心情
			ivMood.setOnClickListener(this);
		
		//设置短录音播放路径
		String shortRecUrl=getShortRecUrl();
		Log.d(TAG, "getShortRecUrl="+shortRecUrl);
		if(shortRecUrl!=null&&shortRecUrl.length()>0){
			tvTack.setTag(shortRecUrl);
			tvTack.setHandler(handler);
			tvTack.setOnClickListener(this);
			tvTack.setVisibility(View.VISIBLE);
			tvTack.setPlaytime(DateUtils.getPlayTime(getShortRecPlaytime()));
//			playProcess.setVisibility(View.VISIBLE);
//			playProcess.setTag(null);
		}else{
			tvTack.setVisibility(View.GONE);
//			playProcess.setVisibility(View.INVISIBLE);
//			playProcess.setTag("");
		}
//		tvDes.setText(getTextDes());
		replacedExpressions(getTextDes(), tvDesc);
		setPictureLayout();
		
		MulitPointTouchListener listener = new MulitPointTouchListener();
		ivPicture.setOnTouchListener(listener);
		mDiarydetail_viewpager.setInterceptListener(listener);
	}
	
	private void setPictureLayout(){
		if(isFullScreenMode){
			ivMood.setVisibility(View.INVISIBLE);
			tvDesc.setVisibility(View.INVISIBLE);
			if(View.GONE!=tvTack.getVisibility()){
				tvTack.setVisibility(View.INVISIBLE);
//				playProcess.setVisibility(View.INVISIBLE);
			}
			vTitlebar.setVisibility(View.INVISIBLE);
			vDiaryInfoTile.setVisibility(View.INVISIBLE);
			vBottombar.setVisibility(View.INVISIBLE);
			ivRecord.setVisibility(View.GONE);
		}else{
			if(View.INVISIBLE==tvTack.getVisibility()){
				tvTack.setVisibility(View.VISIBLE);
			}
//			if(null==playProcess.getTag())
//				playProcess.setVisibility(View.VISIBLE);
			tvDesc.setVisibility(View.VISIBLE);
			// 设置心情 如果没有辅内容的日记不显示心情，与录音按钮正好相反
			if (isRecordActive() && isMyself())
			{
				ivMood.setVisibility(View.INVISIBLE);
				ivRecord.setVisibility(View.VISIBLE);
			}
			else
			{
				ivMood.setVisibility(View.VISIBLE);
				ivRecord.setVisibility(View.GONE);
			}
			vTitlebar.setVisibility(View.VISIBLE);
			vDiaryInfoTile.setVisibility(View.VISIBLE);
			vBottombar.setVisibility(View.VISIBLE);
		}
	}
	
	//设置文字布局数据
	private void loadTextData(View v){
		
	}
	
	//设置无主内容日记数据
	private void loadNomainData(View v){
		CmmobiClickAgentWrapper.onEvent(this, "details_type", 2);
		v.findViewById(R.id.rl_activity_nomain).setOnClickListener(this);
		ivMood=(ImageView) v.findViewById(R.id.iv_diarydetail_xinqing);
		tvDesc=(TextView) v.findViewById(R.id.tv_main_text_content);
		ImageView ivCidai=(ImageView) v.findViewById(R.id.iv_cidai);
		tvTack=(TackView) v.findViewById(R.id.iv_diarydetail_tack);
		tvTack.setBackground(R.drawable.btn_activity_yuantupian_bofangluyin);
//		TextView tvDes=(TextView) v.findViewById(R.id.tv_activity_yuantupian_xinqingmiaoshu);
		if(isMyself())//自己的日记才能修改心情
			ivMood.setOnClickListener(this);
		//设置短录音播放路径
		String shortRecUrl = getShortRecUrl();
		Log.d(TAG, "="+shortRecUrl);
		if (shortRecUrl != null && shortRecUrl.length() > 0) {
			tvTack.setTag(shortRecUrl);
			tvTack.setHandler(handler);
			tvTack.setOnClickListener(this);
			tvTack.setVisibility(View.VISIBLE);
			tvTack.setPlaytime(DateUtils.getPlayTime(getShortRecPlaytime()));
		} else {
			tvTack.setVisibility(View.GONE);
		}
		String text = getTextDes();
		replacedExpressions(text, tvDesc);
		if(text!=null&&text.length()>0){
//			mianText.setText(getTextDes());
			ivCidai.setVisibility(View.GONE);
		}else{
			ivCidai.setVisibility(View.VISIBLE);
		}
		
		setNomainLayout();
	}
	
	private void setNomainLayout()
	{
		if(isFullScreenMode){
			ivMood.setVisibility(View.INVISIBLE);
//			tvDes.setVisibility(View.INVISIBLE);
			if(View.GONE!=tvTack.getVisibility()){
				tvTack.setVisibility(View.INVISIBLE);
			}
			vTitlebar.setVisibility(View.INVISIBLE);
			vDiaryInfoTile.setVisibility(View.INVISIBLE);
			vBottombar.setVisibility(View.INVISIBLE);
			ivRecord.setVisibility(View.GONE);
		}else{
			if(View.INVISIBLE==tvTack.getVisibility()){
				tvTack.setVisibility(View.VISIBLE);
			}
//			tvDes.setVisibility(View.VISIBLE);
			// 设置心情 如果没有辅内容的日记不显示心情，与录音按钮正好相反
			if (isRecordActive() && isMyself())
			{
				ivMood.setVisibility(View.INVISIBLE);
				ivRecord.setVisibility(View.VISIBLE);
			}
			else
			{
				ivMood.setVisibility(View.VISIBLE);
				ivRecord.setVisibility(View.GONE);
			}
			vTitlebar.setVisibility(View.VISIBLE);
			vDiaryInfoTile.setVisibility(View.VISIBLE);
			vBottombar.setVisibility(View.VISIBLE);
		}
	}
	
	//获取日记主体类型
	private DiaryType getDiaryType(MyDiary myDiary){
		if(myDiary.attachs!=null&&myDiary.attachs.length>0){
			for(int i=0;i<myDiary.attachs.length;i++){
				diaryAttach attach=myDiary.attachs[i];
				if("1".equals(attach.attachtype)&&"1".equals(attach.attachlevel)){//音频+辅内容
					return DiaryType.VEDIO;
				}
				if("2".equals(attach.attachtype)&&"1".equals(attach.attachlevel)){//音频+辅内容
					return DiaryType.AUDIO;
				}
				if("3".equals(attach.attachtype)&&"1".equals(attach.attachlevel)){//音频+辅内容
					return DiaryType.PICTURE;
				}
				if("4".equals(attach.attachtype)&&"1".equals(attach.attachlevel)){//音频+辅内容
					return DiaryType.TEXT;
				}
			}
		}
		return DiaryType.NOMAIN;
	}
	
	//获取图片url
	private String getImageUrl(){
		if(myDiary.attachs!=null&&myDiary.attachs.length>0){
			for(int i=0;i<myDiary.attachs.length;i++){
				diaryAttach attach=myDiary.attachs[i];
				if("3".equals(attach.attachtype)&&"1".equals(attach.attachlevel)){//音频+辅内容
					MyAttachImage[] attachImage=attach.attachimage;
					if(attachImage!=null&&attachImage.length>0){
						if(attachImage.length>1){
							for(int j=0;j<attachImage.length;j++){
								if(isMyself()){//自己的日记显示无标图片
									if("0".equals(attachImage[j].imagetype)){
										if(attachImage[j].imageurl!=null&&attachImage[j].imageurl.length()>0){
											return attachImage[j].imageurl;
										}else{
											return attach.attachuuid;
										}
									}
								}else{//他人日记显示有标图片
									if("1".equals(attachImage[j].imagetype)){
										if(attachImage[j].imageurl!=null&&attachImage[j].imageurl.length()>0){
											return attachImage[j].imageurl;
										}else{
											return attach.attachuuid;
										}
									}
								}
							}
						}
						
						if(attachImage[0].imageurl!=null&&attachImage[0].imageurl.length()>0){
							return attachImage[0].imageurl;
						}else{
							return attach.attachuuid;
						}
					}
				}
			}
		}
		return null;
	}
	
	//获取短录音url
	private String getShortRecUrl(){
		if(myDiary.attachs!=null&&myDiary.attachs.length>0){
			for(int i=0;i<myDiary.attachs.length;i++){
				diaryAttach attach=myDiary.attachs[i];
				if("2".equals(attach.attachtype)&&"0".equals(attach.attachlevel)){//音频+辅内容
					MyAttachAudio[] attachAudio=attach.attachaudio;
					if(attachAudio!=null&&attachAudio.length>0){
						//是否需要根据隐私设置给不同的url
						if(attachAudio[0].audiourl!=null&&attachAudio[0].audiourl.length()>0){
							return attachAudio[0].audiourl;
						}else{
							return attach.attachuuid;
						}
					}
				}
			}
		}
		return null;
	}
	
	//获取长录音url
	private String getLongRecUrl(){
		if(myDiary.attachs!=null&&myDiary.attachs.length>0){
			for(int i=0;i<myDiary.attachs.length;i++){
				diaryAttach attach=myDiary.attachs[i];
				if("2".equals(attach.attachtype)&&"1".equals(attach.attachlevel)){//音频+主内容
					MyAttachAudio[] attachAudio=attach.attachaudio;
					if(attachAudio!=null&&attachAudio.length>0){
						//是否需要根据隐私设置给不同的url
						if(attachAudio[0].audiourl!=null&&attachAudio[0].audiourl.length()>0){
							return attachAudio[0].audiourl;
						}else{
							return attach.attachuuid;
						}
					}
				}
			}
		}
		return null;
	}
	
	//获取文字描述信息
	private String getTextDes(){
		if(myDiary.attachs!=null&&myDiary.attachs.length>0){
			for(int i=0;i<myDiary.attachs.length;i++){
				diaryAttach attach=myDiary.attachs[i];
				if("4".equals(attach.attachtype)){//文字
					return attach.content;
				}
			}
		}
		return null;
	}
	
	// 根据日记结构体中的url获取实际播放url
	private String getAudioPath(String url){
		if (null == url || 0 == url.length()) {
			for(int i=0;i<myDiary.attachs.length;i++){
				diaryAttach attach=myDiary.attachs[i];
				if("1".equals(attach.attachlevel)&&"2".equals(attach.attachtype)){
					url=attach.attachuuid;
				}
			}
		}
		if((null == url || 0 == url.length())){
			Log.e(TAG, "url is null");
			return null;
		}
		String uid = ActiveAccount
				.getInstance(MainApplication.getAppInstance()).getUID();
		MediaValue mv = AccountInfo.getInstance(uid).mediamapping.getMedia(uid,
				url);
		if (MediaValue.checkMediaAvailable(mv, 4)) {
			return Environment.getExternalStorageDirectory()+mv.path;
		}
		return url;
	}
	
	//设置标签
	private void setTags(){
		Log.d(TAG, "setTags");
		if(myDiary.tags!=null&&myDiary.tags.length>0){
			tagsLayout.setVisibility(View.VISIBLE);
			int length = myDiary.tags.length;
			for(int i = 0; i<3; i++)
			{
				if(i < length)
				{
					tvTags[i].setVisibility(View.VISIBLE);
					tvTags[i].setText(myDiary.tags[i].name);
				}
				else
				{
					tvTags[i].setVisibility(View.GONE);
				}
			}
		}else{
			//隐藏标签布局
			tagsLayout.setVisibility(View.INVISIBLE);
		}
	}
	
	//设置位置
	private void setPosition(){
		Log.d(TAG, "setPosition");
		if(myDiary.position!=null&&myDiary.position.length()>0){
			vPin.setVisibility(View.VISIBLE);
			tvPosition.setVisibility(View.VISIBLE);
			tvPosition.setText(myDiary.position);
		}else{
			vPin.setVisibility(View.INVISIBLE);
			tvPosition.setVisibility(View.INVISIBLE);
		}
	}
	
	//设置天气
	private void setWeather(){
		Log.d(TAG, "setWeather");
//		ivWeather.setImageUrl(myDiary.weather, 1, false);
		ivWeather.setImageUrl(R.drawable.tianqi_weizhi, 1, myDiary.weather, false);
		if(myDiary.weather_info!=null&&myDiary.weather_info.length()>0){
			tvTemperature.setText(myDiary.weather_info);
		}else{
			tvTemperature.setText("-°C/-°C");
		}
		
		try {
			tvDiaryTime.setText(DateUtils.dateToString(new Date(Long.parseLong(myDiary.updatetimemilli)), DateUtils.DATE_FORMAT_NORMAL_1));
		} catch (NumberFormatException e) {
			e.printStackTrace();
		}
	}

	private void initUI() {
		mDiarydetail_views.clear();
		for (int i = 0; i < mListDuplicate.size(); i++) {
			SortDiary sortDiary = mListDuplicate.get(i);
			MyDiary myDiary = null;
			int diaryType = 0;
			for (int j = 0; j < mListDiary.size(); j++) {
				myDiary = mListDiary.get(j);
				if(sortDiary.diaryUUID != null)
				{
					if (sortDiary.diaryUUID.equals(myDiary.diaryuuid)) {
						break;
					}
				}
				else if(sortDiary.diaryID != null)
				{
					if (sortDiary.diaryID.equals(myDiary.diaryid)) {
						break;
					}
				}
			}
			if (myDiary != null) {// 第一次的时候一定不为空
									// 主本也不可能为空，如果副本未空，可以将副本类型认为和主本类型一致
				diaryType = DiaryListView.getDiaryType(myDiary.attachs);
				myDiary = null;
			}
			View diaryLayout=null;
			switch (diaryType) {
			case 0x10000000://主体 视频
			case 0x10000100://主体 视频+辅 音频
			case 0x10000101://主体 视频+辅 音频+文字
			case 0x10000001://主体 视频+文字
				Log.d(TAG, "主体为视频");
				diaryLayout = inflater.inflate(
						R.layout.include_activity_diarydetail_shiping, null);
				diaryLayout.setTag(DiaryType.VEDIO);
				break;
			case 0x1000000://主体 音频
			case 0x1000100://主体 音频+辅 音频
			case 0x1000101://主体 音频+辅 音频+文字
			case 0x1000001://主体音频+文字
				Log.d(TAG, "主体为音频");
				diaryLayout = inflater.inflate(
						R.layout.include_activity_diarydetail_luyin, null);
				diaryLayout.setTag(DiaryType.AUDIO);
				break;
			case 0x100000://主体 图片
			case 0x100001://主体 图片 +文字
			case 0x100100://主体 图片+辅 音频
			case 0x100101://主体 图片+辅 音频+文字
				Log.d(TAG, "主体为图片");
				diaryLayout = inflater.inflate(
						R.layout.include_activity_diarydetail_yuantupian, null);
				diaryLayout.setTag(DiaryType.PICTURE);
				break;
			case 0x10000://主体 文字
			case 0x10001://主体 文字+文字
			case 0x10100://主体 文字+辅 音频
			case 0x10101://主体 文字+辅 音频+文字
				Log.d(TAG, "主体为文字");
				diaryLayout = inflater.inflate(
						R.layout.include_activity_diarydetail_wenzi, null);
				diaryLayout.setTag(DiaryType.TEXT);
				break;
			case 0x100://辅 音频
			case 0x101://辅 音频+文字
			case 0x1://辅 文字
				Log.d(TAG, "无主内容 只有辅内容");
				diaryLayout = inflater.inflate(
						R.layout.include_activity_diarydetail_nomain, null);
				diaryLayout.setTag(DiaryType.NOMAIN);
				break;
			default:
				diaryLayout = null;
				break;
			}
			if (diaryLayout != null)
				mDiarydetail_views.add(diaryLayout);
		}
		// 加入到viewpager中
		mDiarydetail_viewpager.setAdapter(new MyPageAdapter(mDiarydetail_views));
		// 设置当前显示的页数 pageIndex
		mDiarydetail_viewpager.setCurrentItem(mPageIndex);

	}
	
	public enum DiaryType{
		AUDIO,
		VEDIO,
		TEXT,
		PICTURE,
		NOMAIN
	}

	// 点击显示副本名称下拉框
//	private void showDuplicateDiaryName() {
//		if (hasDuplicate()) {
//			ArrayList<String> tempDiaryNames = new ArrayList<String>();
//			MyDiary tmpDiary = null;
//			if(!myDiary.resourceuuid.equals(""))
//			{//说明点击的是副本
//				for(MyDiary d : mListDiary)
//				{
//					if(d.diaryuuid.equals(myDiary.resourceuuid))
//					{
//						tmpDiary = d;
//						String firstName = getDiaryName(d);
//						tempDiaryNames.add(firstName);
//					}
//				}
//			}
//			else
//			{
//				tmpDiary = myDiary;
//				String currentName = mListDuplicate.get(mPageIndex).duplicatename;
//				tempDiaryNames.add(currentName);
//			}
//			if(tmpDiary == null || tmpDiary.duplicate == null)
//			{
//				// 异常
//				return;
//			}
//			final MyDiary dupDiary = tmpDiary;
//			
//			for (int i = 0; i < dupDiary.duplicate.length; i++) {
//				tempDiaryNames.add(dupDiary.duplicate[i].duplicatename);
//			}
//			final String[] strDiaryNames = tempDiaryNames
//					.toArray(new String[tempDiaryNames.size()]);
//			View view = inflater.inflate(
//					R.layout.activity_diarydetail_duplicate_menu, null);
//			measureView(view);
//			ListView duplicateList = (ListView) view
//					.findViewById(R.id.lv_duplicate_list);
//			duplicateList.setAdapter(new ArrayAdapter<String>(this,
//					R.layout.activity_activitydetail_duplicate_list_item,
//					R.id.tv_item, strDiaryNames));
//			duplicateList.setSelected(true);
//			duplicateList.setSelector(R.drawable.xiala_xuanzhong);
//			duplicateList.setOnItemClickListener(new OnItemClickListener() {
//
//				@Override
//				public void onItemClick(AdapterView<?> parent, View view,
//						int position, long id) {
//					// 切换到对应的副本日记页
//					String diaryID = null;
//					if (position > 0) {
//						diaryID = dupDiary.duplicate[position-1].diaryid;
//					}
//					else
//					{
//						diaryID = dupDiary.diaryid;
//					}
//					for (int i = 0; i < mListDuplicate.size(); i++) {
//						if (diaryID != null
//								&& diaryID.equals(mListDuplicate.get(i).diaryid)) {
//							mPageIndex = i;
//							diarydetail_viewpager.setCurrentItem(mPageIndex);
//							break;
//						}
//					}
//					if (mDiaryMenuMore != null)
//					{
//						mDiaryMenuMore.dismiss();
//					}
//				}
//			});
//			ivXiala.setVisibility(View.INVISIBLE);
//			mDiaryMenuMore = new PopupWindow(view, vTitlebar.getWidth() / 3,
//					LayoutParams.WRAP_CONTENT, true);
//			mDiaryMenuMore.setBackgroundDrawable(getResources().getDrawable(
//					R.drawable.dot_big));
//			mDiaryMenuMore.showAsDropDown(vTitlebar, vTitlebar.getWidth() / 3,
//					-3);
//			mDiaryMenuMore.setOnDismissListener(new OnDismissListener() {
//
//				@Override
//				public void onDismiss() {
//					ivXiala.setVisibility(View.VISIBLE);
//				}
//			});
//		}
//	}

	// 是否显示录音按钮
	private boolean isRecordActive()
	{
//		String shortRecUrl=getShortRecUrl();
//		if(shortRecUrl!=null&&shortRecUrl.length()>0)
//		{
//			return false;
//		}
//		else
//		{
//			return true;
//		}
		
		if(myDiary.attachs != null && myDiary.attachs.length > 0)
		{
			for(diaryAttach attach: myDiary.attachs)
			{
				if(attach.attachlevel.equals("0"))
				{
					return false;
				}
			}
		}
		return true;
	}
	
	// 仅有文字的日记可以直接编辑
	private boolean isOnlyText()
	{
		if(myDiary.attachs != null && myDiary.attachs.length > 0)
		{
			for(diaryAttach attach: myDiary.attachs)
			{
				if(!attach.attachlevel.equals("0") || !attach.attachtype.equals("4"))
				{
					return false;
				}
			}
			return true;
		}
		else
		{
			return false;
		}
	}
	
//	View btnDelete;
//	//日记已发布时才显示删除按钮
//	private void initBtnDelete(){
//		if(isPublished()){
//			btnDelete.setVisibility(View.VISIBLE);
//		}else{
//			btnDelete.setVisibility(View.GONE);
//		}
//	}
//	
//	View btnDownload;
//	//日记在本地不存在时显示下载按钮
//	private void initBtnDownload(){
//		if(mDiaryManager.isInLocal(myDiary.diaryid)){
//			btnDownload.setVisibility(View.GONE);
//		}else{
//			btnDownload.setVisibility(View.VISIBLE);
//		}
//	}

//	private Button btnPutSaveBox;
//
//	private void initBtnSaveBox() {
//		if (btnPutSaveBox != null) {
//			if (isInSaveBox()) {
//				btnPutSaveBox
//						.setText(R.string.str_activit_detail_putoff_savebox);
//				btnPutSaveBox.setTag(true);
//			} else {
//				btnPutSaveBox
//						.setText(R.string.str_activit_detail_putin_savebox);
//				btnPutSaveBox.setTag(false);
//			}
//		}
//	}
//	
//	private Button btnEnshrine;
//
//	private void initBtnEnshrine() {
//		if (btnEnshrine != null) {
//			if (isEnshrine()) {// 已收藏
//				btnEnshrine
//						.setText(R.string.str_activit_detail_cancel_enshrine);
//				btnEnshrine.setTag(true);
//			} else {// 未收藏
//				btnEnshrine.setText(R.string.str_activit_detail_enshrine);
//				btnEnshrine.setTag(false);
//			}
//		}
//	}

//	private View btnAttendActive;
	//只有内容为视频且未参加活动时才显示”参加活动”选项
//	private void initBtnAttendActive(){
//		if(DiaryType.VEDIO==getDiaryType(myDiary)&&(myDiary.active==null||myDiary.active.activeid==null||myDiary.active.activeid.length()==0)){
//			btnAttendActive.setVisibility(View.VISIBLE);
//		}else{
//			btnAttendActive.setVisibility(View.GONE);
//		}
//	}
	
	// 活动菜单是否显示
	private boolean isAccessActive()
	{
//		if (DiaryType.VEDIO == getDiaryType(myDiary) && 
//				(myDiary.active == null || myDiary.active.activeid == null || 
//				myDiary.active.activeid.length() == 0))
		if (DiaryType.VEDIO == getDiaryType(myDiary)/* &&
				myDiary.publish_status.equals("1")*/) //必须是视频日记并且分享给所有人的才能参加活动
		{
			return true;
		}
		else
		{
			return false;
		}
	}
	
	// 是否参加了活动
	private boolean isJoinActive()
	{
		if(myDiary.active!=null&&myDiary.active.activeid!=null&&myDiary.active.activeid.length()>0)
		{
			return true;
		}
		else
		{
			return false;
		}
		
	}	

	//more弹出菜单
//	private Integer[] icons1 = {R.drawable.ic_shoucang,
//			R.drawable.ic_jubao	
//		};
//
//	private Integer[] icons2 = {R.drawable.ic_xiazai,
//			R.drawable.ic_baoxianxiang, R.drawable.ic_shanchu,
//		};
//		
//	private String[] text1 = {"收藏", "举报"}; 
//		
//	private String[] text2 = {"下载", "放置保险箱", "删除"};
	
	// 初始化我的更多菜单
	private void initMyMoreMenu(ArrayList<String> text, ArrayList<Integer> icon)
	{
		text.clear();
		icon.clear();
//		if (!mDiaryManager.isInLocal(myDiary.diaryid))
		// 评论等界面的日记没有本地缓存 状态始终是0
		int sync = getSync(myDiary.diaryuuid);
		if (((sync > 3 && sync < 5) || sync == -1 && !isDiaryDownload())
				&& NetworkTaskManager.getInstance(userID).getTask(myDiary.diaryuuid) == null
				&& !isOnlyText())
		{
			text.add("下载");
			icon.add(R.drawable.ic_xiazai);
		}
		if (isInSaveBox() && settingManager.getGesturepassword() != null) //保险箱可能被取消了
		{
			text.add(getText(R.string.str_activit_detail_putoff_savebox).toString());
		} 
		else 
		{
			text.add(getText(R.string.str_activit_detail_putin_savebox).toString());
		}
		icon.add(R.drawable.ic_baoxianxiang);
		if(isPublished())
		{
			text.add("删除");
			icon.add(R.drawable.ic_shanchu);
		}
	}
	
	// 初始化其他人的更多菜单
	private void initOtherMoreMenu(ArrayList<String> text, ArrayList<Integer> icon)
	{
		text.clear();
		icon.clear();
		if (isEnshrine())
		{
			text.add(getText(R.string.str_activit_detail_cancel_enshrine).toString());
		}
		else
		{
			text.add(getText(R.string.str_activit_detail_enshrine).toString());
		}
		icon.add(R.drawable.ic_shoucang);
		text.add("举报");
		icon.add(R.drawable.ic_jubao);
	}
	
	// 点击更多按钮后显示
	private void showMyMoreMenu() {
		LinearLayout ll = (LinearLayout) LayoutInflater.from(this).inflate(R.layout.list_diarydetail_more, null);
		//ll.setBackgroundDrawable(drawable);
		ListView lv = (ListView) ll.findViewById(R.id.lv_diarydetail_more);
		//lv.setDivider(null);
//		String [] array = getResources().getStringArray(R.array.diarydetail_more3);
		ArrayList<String> text = new ArrayList<String>();
		ArrayList<Integer> icon = new ArrayList<Integer>();
		initMyMoreMenu(text, icon);
		lv.setAdapter(new MoreAdapter(this, text.toArray(new String[text.size()]), icon.toArray(new Integer[icon.size()])));
//		lv.setAdapter(new MoreAdapter(this, text2, icons2));

		Drawable drawable = getResources().getDrawable(R.drawable.bg_diarydetail_more3);
		if(text.size() == 1)
		{
			drawable = getResources().getDrawable(R.drawable.bg_diarydetail_more1);
			mDiaryMenuMore = new PopupWindow(ll, drawable.getIntrinsicWidth(), LayoutParams.WRAP_CONTENT, true);
		}
		else if(text.size() == 2)
		{
			drawable = getResources().getDrawable(R.drawable.bg_diarydetail_more2);
			mDiaryMenuMore = new PopupWindow(ll, drawable.getIntrinsicWidth(), LayoutParams.WRAP_CONTENT, true);
		}
		else if(text.size() == 3)
		{
			drawable = getResources().getDrawable(R.drawable.bg_diarydetail_more3);
			mDiaryMenuMore = new PopupWindow(ll, drawable.getIntrinsicWidth(), LayoutParams.WRAP_CONTENT, true);
		}
		
		mDiaryMenuMore.setBackgroundDrawable(drawable);
		mDiaryMenuMore.setFocusable(true);
		mDiaryMenuMore.setOutsideTouchable(true);

//		mMenuMore.showAtLocation(findViewById(R.id.text), Gravity.TOP, 0, 0);
		mDiaryMenuMore.showAsDropDown(findViewById(R.id.iv_more), 0, 0);
		
		lv.setOnItemClickListener(this);
		
	}
	
	// 点击更多按钮后显示
	private void showOtherMoreMenu() {
		LinearLayout ll = (LinearLayout) LayoutInflater.from(this).inflate(R.layout.list_diarydetail_more, null);
		//ll.setBackgroundDrawable(drawable);
		ListView lv = (ListView) ll.findViewById(R.id.lv_diarydetail_more);
		//lv.setDivider(null);
//		String [] array = getResources().getStringArray(R.array.diarydetail_more3);
		ArrayList<String> text = new ArrayList<String>();
		ArrayList<Integer> icon = new ArrayList<Integer>();
		initOtherMoreMenu(text, icon);
		lv.setAdapter(new MoreAdapter(this, text.toArray(new String[text.size()]), icon.toArray(new Integer[icon.size()])));
//		lv.setAdapter(new MoreAdapter(this, text1, icons1));

		Drawable drawable = getResources().getDrawable(R.drawable.bg_diarydetail_more2);
		mDiaryMenuMore = new PopupWindow(ll, drawable.getIntrinsicWidth(), LayoutParams.WRAP_CONTENT, true);
		
		mDiaryMenuMore.setBackgroundDrawable(drawable);
		mDiaryMenuMore.setFocusable(true);
		mDiaryMenuMore.setOutsideTouchable(true);

//		mMenuMore.showAtLocation(findViewById(R.id.text), Gravity.TOP, 0, 0);
		mDiaryMenuMore.showAsDropDown(findViewById(R.id.iv_more), 0, 0);
		
		lv.setOnItemClickListener(this);

	}
	
	//设置菜单相关
	private ImageView ivDiaryTypeAttended = null;
	private ImageView ivDiaryTypeAll = null;
	
	private ImageView ivDidryTypeSelfCancel = null;
	private ImageView ivDiaryTypeAllCancel = null;
	private ImageView ivDiaryTypeAttendedCancel = null;
	
	private ImageView positionTypeSelf = null;
	private ImageView positionTypeAttended = null;
	private ImageView positionTypeAll = null;
	private TextView positionTextAll = null;
	
	private ImageButton mIBShareSetOk = null;
	private ImageButton mIBShareSetCancel = null;
	
	private LinearLayout mLLDescribe = null;
	private LinearLayout mLLDiarySet = null;
	private LinearLayout mLLDiarySetCancel = null;
	private LinearLayout mLLPositionSet = null;
	private TextView mTVDescribe = null;
	
	// 显示发布设置菜单
	private void showPublishSetting(int id) {
		View view = LayoutInflater.from(this).inflate(
				R.layout.activity_diarydetail_share_publish_setting, null);
		mDiaryMenuMore = new PopupWindow(view, LayoutParams.FILL_PARENT,
				LayoutParams.WRAP_CONTENT, true);
		//mMenuMore.showAtLocation(rootView, Gravity.BOTTOM, 0, 0);
		// 成为类模态对话框 同时要在onkeydown中响应返回按钮
		mDiaryMenuMore.setFocusable(true);
		mDiaryMenuMore.setOutsideTouchable(false);
		mDiaryMenuMore.showAsDropDown(vTitlebar, 0, 0);
		
		ivDiaryTypeAttended = (ImageView) view.findViewById(R.id.iv_diary_type_attended);
		ivDiaryTypeAttended.setOnClickListener(this);
		ivDiaryTypeAll = (ImageView) view.findViewById(R.id.iv_diary_type_all);
		ivDiaryTypeAll.setOnClickListener(this);
		
		ivDidryTypeSelfCancel = (ImageView) view.findViewById(R.id.iv_diary_type_self_cancel);
		ivDidryTypeSelfCancel.setOnClickListener(this);
		ivDiaryTypeAttendedCancel = (ImageView) view.findViewById(R.id.iv_diary_type_attended_cancel);
		ivDiaryTypeAttendedCancel.setOnClickListener(this);
		ivDiaryTypeAllCancel = (ImageView) view.findViewById(R.id.iv_diary_type_all_cancel);
		ivDiaryTypeAllCancel.setOnClickListener(this);

		positionTypeSelf = (ImageView) view.findViewById(R.id.btn_position_type_self);
		positionTypeSelf.setOnClickListener(this);
		positionTypeAttended = (ImageView) view.findViewById(R.id.btn_position_type_attended);
		positionTypeAttended.setOnClickListener(this);
		positionTypeAll = (ImageView) view.findViewById(R.id.btn_position_type_all);
		positionTypeAll.setOnClickListener(this);
		positionTextAll = (TextView) view.findViewById(R.id.tv_shareposition_all);
		
		mIBShareSetOk = (ImageButton) view.findViewById(R.id.IB_share_ok);
		mIBShareSetCancel = (ImageButton) view.findViewById(R.id.IB_share_cancel);

		mLLDescribe = (LinearLayout) view.findViewById(R.id.ll_diarydetail_describe);
		mLLDiarySet = (LinearLayout) view.findViewById(R.id.ll_diarydetail_diaryset);
		mLLDiarySetCancel = (LinearLayout) view.findViewById(R.id.ll_diarydetail_diaryset_cancel);
		mLLPositionSet = (LinearLayout) view.findViewById(R.id.ll_diarydetail_positionset);
		
		mTVDescribe = (TextView) view.findViewById(R.id.tv_diarydetail_describe);
		
		//  响应返回按键
		LinearLayout llMenu = (LinearLayout) view.findViewById(R.id.ll_diarydetail_sharesettting_menu);
		llMenu.setOnKeyListener(new OnKeyListener()
		{
			
			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event)
			{
				 if (event.getAction() == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_BACK)
				 { 
					 mDiaryMenuMore.dismiss();
				 }
				return true;
			}
		});
		
		// 根据id判断布局样式
		switch(id)
		{
		case R.drawable.btn_diarydetail_looklook:
			if(isPublished())
			{
//				publishType = mDiaryManager.getPublishShareStatus(myDiary.diaryuuid);
//				positionType = mDiaryManager.getPositionShareStatus(myDiary.diaryuuid);
				
				publishType = myDiary.publish_status;
				positionType = myDiary.position_status;
				
				if(publishType == null)
				{
					publishType = "2";
				}
				if(positionType == null)
				{
					positionType = "2";
				}
				mLLDiarySet.setVisibility(View.GONE);
				mLLDiarySetCancel.setVisibility(View.VISIBLE);
			}
			else
			{
				publishType = "2";
				positionType = "2";
				mLLDiarySet.setVisibility(View.VISIBLE);
				mLLDiarySetCancel.setVisibility(View.GONE);
			}
			break;
		case R.drawable.btn_diarydetail_active:
			publishType = "1";
			positionType = "1";
			mTVDescribe.setText("参加活动内容必须公开到looklook");
			mLLDescribe.setVisibility(View.VISIBLE);
			mLLDiarySet.setVisibility(View.GONE);
			mLLDiarySetCancel.setVisibility(View.GONE);
			break;
		case R.drawable.btn_diarydetail_sina:
		case R.drawable.btn_diarydetail_weixin:
		case R.drawable.btn_diarydetail_weixinfriend:
		case R.drawable.btn_diarydetail_tencent:
		case R.drawable.btn_diarydetail_renren:
		case R.drawable.btn_diarydetail_email:
		case R.drawable.btn_diarydetail_msg:
			publishType = "1";
			positionType = "1";
			mTVDescribe.setText("分享第三方必须公开内容到looklook");
			mLLDescribe.setVisibility(View.VISIBLE);
			mLLDiarySet.setVisibility(View.GONE);
			mLLDiarySetCancel.setVisibility(View.GONE);
			break;
		default:
			break;
		}
		
		initSettings();
		// id传给点击事件
		mIBShareSetOk.setTag(id);
		mIBShareSetOk.setOnClickListener(this);
		mIBShareSetCancel.setOnClickListener(this);
		
	}
	
//	//设置发布设置默认值
//	private void setSettingsDefault(){
//		Log.d(TAG, "settingManager.getDiary_type()="+settingManager.getDiary_type());
//		Log.d(TAG, "settingManager.getPosition_type()="+settingManager.getPosition_type());
//		Log.d(TAG, "settingManager.getAudio_type()="+settingManager.getAudio_type());
//		
//		if(settingManager.getDiary_type()!=null&&settingManager.getDiary_type().length()>0)
//			diaryType=settingManager.getDiary_type();
//		if(settingManager.getPosition_type()!=null&&settingManager.getPosition_type().length()>0)
//			positionType=settingManager.getPosition_type();
//		if(settingManager.getAudio_type()!=null&&settingManager.getAudio_type().length()>0)
//			audioType=settingManager.getAudio_type();
//	}
//	
	//谁可以看我内容（日记和评论），1全部人可见（黑名单人除外） 2关注人可见  4仅自己可见
	private String publishType="1";
	//谁可以看我的位置，1全部人可见（黑名单人除外） 2关注人可见  4仅自己可见
	private String positionType="1";
	//谁可以听我的语音，1全部人可见（黑名单人除外） 2关注人可见  4仅自己可见
	private String audioType="1";
	//设置发布设置UI显示
	private void initSettings(){
		setDiaryTypeSetting();
		setPositionTypeSetting();
//		setAudioTypeSetting();
	}
	
	//设置日记内容可见性UI
	private void setDiaryTypeSetting(){
		if("1".equals(publishType)){
			ivDidryTypeSelfCancel.setAlpha(0);
			ivDiaryTypeAttendedCancel.setAlpha(0);
			ivDiaryTypeAllCancel.setAlpha(255);
			ivDiaryTypeAttended.setAlpha(0);
			ivDiaryTypeAll.setAlpha(255);
			
			positionTypeAll.setEnabled(true);
			positionTextAll.setTextColor(getResources().getColor(R.color.white));
			
			if (myDiary.diary_status.equals("2") && !mLLPositionSet.isShown())
			{
				mLLPositionSet.setVisibility(View.VISIBLE);
				positionType = "1";
			}
		}
		if("2".equals(publishType)){
			ivDidryTypeSelfCancel.setAlpha(0);
			ivDiaryTypeAttendedCancel.setAlpha(255);
			ivDiaryTypeAllCancel.setAlpha(0);
			ivDiaryTypeAttended.setAlpha(255);
			ivDiaryTypeAll.setAlpha(0);
			
			if(positionType.equals("1"))
			{
				positionType = "2";
			}
			positionTypeAll.setEnabled(false);
			positionTextAll.setTextColor(getResources().getColor(R.color.gray));
			
			if (myDiary.diary_status.equals("2") && !mLLPositionSet.isShown())
			{
				mLLPositionSet.setVisibility(View.VISIBLE);
				positionType = "2";
			}
		}
		if("4".equals(publishType)){
			ivDidryTypeSelfCancel.setAlpha(255);
			ivDiaryTypeAttendedCancel.setAlpha(0);
			ivDiaryTypeAllCancel.setAlpha(0);
			
			positionType = "1";
			positionTypeAll.setEnabled(false);
			positionTextAll.setTextColor(getResources().getColor(R.color.gray));
			
			if (myDiary.diary_status.equals("2"))
			{
				positionType = "4";
				mLLPositionSet.setVisibility(View.GONE);
			}
		}
	}
	
	//设置日记位置可见性UI
	private void setPositionTypeSetting(){
		if("1".equals(positionType)){
			positionTypeSelf.setAlpha(0);
			positionTypeAttended.setAlpha(0);
			positionTypeAll.setAlpha(255);
		}
		if("2".equals(positionType)){
			positionTypeSelf.setAlpha(0);
			positionTypeAttended.setAlpha(255);
			positionTypeAll.setAlpha(0);
		}
		if("4".equals(positionType)){
			positionTypeSelf.setAlpha(255);
			positionTypeAttended.setAlpha(0);
			positionTypeAll.setAlpha(0);
		}
	}

	public class MyPageAdapter extends PagerAdapter {
		private List<View> mListViews;

		public MyPageAdapter(List<View> mListViews) {
			this.mListViews = mListViews;
		}
		
		public View getView(int position){
			return mListViews.get(position);
		}

		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			if(position<mListViews.size())
			{
				container.removeView(mListViews.get(position));
			}
		}

		@Override
		public Object instantiateItem(ViewGroup container, int position) {
			container.addView(mListViews.get(position), 0);
			return mListViews.get(position);
		}

		@Override
		public int getCount() {
			return mListViews.size();
		}

		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {
			return arg0 == arg1;
		}

	}
	
	// 点击分享按钮后显示
	private void showMyShareMenu() {
		if (isInSaveBox() && settingManager.getGesturepassword() != null) //保险箱可能被取消了
		{
			new Xdialog.Builder(this)
			.setTitle("")
			.setMessage("此内容在保险箱中，如果分享隐私性将有可能降低，确定是否分享？")
			.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					mShareMenuView.getExpressionView().setVisibility(View.VISIBLE);
					mShareMenuView.showMyMenu(myDiary, isAccessActive(), isJoinActive());
				}
			})
			.setNegativeButton(android.R.string.cancel, null)
			.create().show();
		}
		else
		{
			mShareMenuView.getExpressionView().setVisibility(View.VISIBLE);
			mShareMenuView.showMyMenu(myDiary, isAccessActive(), isJoinActive());
		}
		
	}

	// 点击分享按钮后显示
	private void showOtherShareMenu() {
		mShareMenuView.getExpressionView().setVisibility(View.VISIBLE);
		mShareMenuView.showOtherMenu(myDiary);
	}

	// 点击下载后显示高清/普清选项
	private void showDownloadOptions() {
		MyAttachVideo[] attachVideos = getVideoUrlPath();
		MyAttachVideo height = null;
		MyAttachVideo normal = null;
		MyAttachVideo undecode = null;
		MyAttachVideo normaldecode = null;
		if(attachVideos != null && attachVideos.length > 0)
		{
			for (int i = 0; i < attachVideos.length; i++)
			{
				MyAttachVideo attachVideo = attachVideos[i];
				if ("0".equals(attachVideo.videotype))
				{// 原视频高清
					height = attachVideo;
				}
				if ("1".equals(attachVideo.videotype))
				{// 原视频普清
					normal = attachVideo;
				}
				if ("2".equals(attachVideo.videotype))
				{// 普清转码未压标
					undecode = attachVideo;
				}
				if ("3".equals(attachVideo.videotype))
				{// 普清转压
					normaldecode = attachVideo;
				}
			}
			if(height != null) // 有高清 显示菜单
			{
				View view = inflater.inflate(R.layout.activity_diarydetail_download_option,
						null);
				mDiaryMenuSelect = new PopupWindow(view, LayoutParams.FILL_PARENT,
						LayoutParams.WRAP_CONTENT, true);
				mDiaryMenuSelect.setBackgroundDrawable(getResources().getDrawable(
						R.drawable.dot_big));
				mDiaryMenuSelect.showAtLocation(rootView, Gravity.BOTTOM, 0, 0);
				View vHeigh=view.findViewById(R.id.btn_download_option_heigh);
				vHeigh.setOnClickListener(this);
				View vNormal=view.findViewById(R.id.btn_download_option_normal);
				vNormal.setOnClickListener(this);
//				View vOriginal=view.findViewById(R.id.btn_download_option_original);
//				vOriginal.setOnClickListener(this);
				view.findViewById(R.id.btn_cancel).setOnClickListener(this);
				
				vHeigh.setVisibility(View.VISIBLE);
				vHeigh.setTag(height);
				
				if(normal != null)
				{
					vNormal.setVisibility(View.VISIBLE);
					vNormal.setTag(normal);
				}
				else if(undecode != null)
				{
					vNormal.setVisibility(View.VISIBLE);
					vNormal.setTag(undecode);
				}
				else if(normaldecode != null)
				{
					vNormal.setVisibility(View.VISIBLE);
					vNormal.setTag(normaldecode);
				}
				
			}
			else //直接下载普清
			{
				if(normal != null)
				{
					downloadVedio(normal);
				}
				else if(undecode != null)
				{
					downloadVedio(undecode);
				}
				else if(normaldecode != null)
				{
					downloadVedio(normaldecode);
				}
				
			}
			
		}else{
			//加入到下载队列
			addDownloadTask(myDiary);
		}
	}
	
	// 收藏
	private void showCollectMenu(){
		View view = inflater.inflate(
				R.layout.activity_diarydetail_collect_menu, null);
		mDiaryMenuSelect = new PopupWindow(view, LayoutParams.FILL_PARENT,
				LayoutParams.WRAP_CONTENT, true);
		mDiaryMenuSelect.setBackgroundDrawable(getResources().getDrawable(
				R.drawable.dot_big));
		mDiaryMenuSelect.showAtLocation(rootView, Gravity.BOTTOM, 0, 0);
		view.findViewById(R.id.btn_more_menu_enshrine_only).setOnClickListener(this);
		view.findViewById(R.id.btn_more_menu_enshrine_and_download).setOnClickListener(this);
		view.findViewById(R.id.btn_cancel).setOnClickListener(this);
	}
	
	private MyAttachVideo[] getVideoUrlPath(){
		if(myDiary.attachs!=null&&myDiary.attachs.length>0){
			for(int i=0;i<myDiary.attachs.length;i++){
				diaryAttach attach=myDiary.attachs[i];
				if("1".equals(attach.attachtype)&&attach.attachvideo!=null&&attach.attachvideo.length>0){
					return attach.attachvideo;
				}
			}
		}
		return null;
	}

	protected void measureView(View child) {
		ViewGroup.LayoutParams p = child.getLayoutParams();
		if (p == null) {
			p = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT,
					ViewGroup.LayoutParams.WRAP_CONTENT);
		}
		int childWidthSpec = ViewGroup.getChildMeasureSpec(0, 0, p.width);
		int lpHeight = p.height;
		int childHeightSpec = ViewGroup.getChildMeasureSpec(0, 0, p.height);
		if (lpHeight > 0) {
			childHeightSpec = MeasureSpec.makeMeasureSpec(lpHeight,
					MeasureSpec.EXACTLY);
		} else {
			childHeightSpec = MeasureSpec.makeMeasureSpec(0,
					MeasureSpec.UNSPECIFIED);
		}
		child.measure(childWidthSpec, childHeightSpec);
	}

	static int moods[] = { R.drawable.xinqing_chijing,
			R.drawable.xinqing_gaoxing, R.drawable.xinqing_jusang,
			R.drawable.xinqing_ku, R.drawable.xinqing_shengqi,
			R.drawable.xinqing_tiaopi, R.drawable.xinqing_weiqu,
			R.drawable.xinqing_xinhuanufang, R.drawable.xinqing_yanwu };

//	static String moods_id[] = { "", "", "", "", "", "", "", "", "" };

	class myMoodAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			return moods.length;
		}

		@Override
		public Object getItem(int position) {
			return moods[position];
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder;
			if (null == convertView) {
				holder = new ViewHolder();
				convertView = inflater.inflate(
						R.layout.activity_homepage_mood_list_item, null);
				holder.ivMood = (ImageView) convertView
						.findViewById(R.id.iv_mood_item);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			holder.ivMood.setImageResource(moods[position]);
			return convertView;
		}
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
							handler.sendEmptyMessage(HANDLER_UPDATE_LONG_RECORD_PLAYER_COMPLETE);

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
						int current=mp.getCurrentPosition();
						int total=mp.getDuration();
						int process=current*100/total;
						handler.obtainMessage(HANDLER_UPDATE_LONG_RECORD_PLAYER_PROCESS,process).sendToTarget();
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
	
	// 分享状态
	public void shareToLooklook(int platId)
	{
		if(platId == R.drawable.btn_diarydetail_looklook)
		{
			// 不需要继续分享
			mShareToId = -1;
			
			int lastStatus;
			try
			{
				lastStatus = Integer.parseInt(mDiaryManager.getPublishShareStatus(myDiary.diaryuuid));
			}
			catch (Exception e)
			{
				e.printStackTrace();
				lastStatus = 4;
			}
			int curStatus = Integer.parseInt(publishType);
			
			if(curStatus > lastStatus)
			{
				new Xdialog.Builder(this)
				.setTitle("")
				.setMessage("更改内容状态会导致所参加的活动被取消、分享至第三方的内容将无法正常显示，确定是否更改？")
				.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						ZDialog.show(R.layout.progressdialog, false, true, DiaryDetailActivity.this);
						// 删除取消发布
//						if (publishType.equals("4"))
//						{// 取消发布
//							Requester2.diaryPublish(handler, myDiary.diaryid, "2", "", "", "");
//						}
//						else
						{// 发布
							CmmobiClickAgentWrapper.onEvent(DiaryDetailActivity.this, "det_my_forw",
									myDiary.diaryid, 0);
							
							String status = "1";
							if(myDiary.diary_status.equals("2"))
							{
								status = "3";
							}
							
//							Requester2.diaryPublish(handler, myDiary.diaryid, "1", getPublishTypeSetting(),
//									getPositionSetting(), getAudioSetting());
							// 换成任务队列
							afterPublish();
							OfflineTaskManager.getInstance().addShareToLookLook(myDiary.diaryid,myDiary.diaryuuid, getPublishTypeSetting(),
									getPositionSetting(), getAudioSetting(), status);
							if(isJoinActive())
							{
								OfflineTaskManager.getInstance().addActiveCancelTask(myDiary.diaryid,myDiary.diaryuuid, myDiary.active.activeid);
								myDiary.active = new GsonResponse2().new MyActive();
								ivActvie.setVisibility(View.GONE);
							}
							ZDialog.dismiss();
						}
					}
				})
				.setNegativeButton(android.R.string.cancel, null)
				.create().show();
			}
			else
			{// 发布
				ZDialog.show(R.layout.progressdialog, false, true, DiaryDetailActivity.this);
				CmmobiClickAgentWrapper.onEvent(DiaryDetailActivity.this, "det_my_forw",
						myDiary.diaryid, 0);
				
				String status = "1";
				if(myDiary.diary_status.equals("2"))
				{
					status = "3";
				}
				
//				Requester2.diaryPublish(handler, myDiary.diaryid, "1", getPublishTypeSetting(),
//						getPositionSetting(), getAudioSetting());
				// 换成任务队列
				afterPublish();
				OfflineTaskManager.getInstance().addShareToLookLook(myDiary.diaryid,myDiary.diaryuuid, getPublishTypeSetting(),
						getPositionSetting(), getAudioSetting(), status);
				ZDialog.dismiss();
			}
			
//			if (publishType.equals("4")) {// 取消发布
//				// 增加取消活动的判断
//				if(isJoinActive())
//				{
//					new Xdialog.Builder(this)
//					.setTitle("警告")
//					.setMessage("该日记已参加活动，取消发布会导致退出已参加的活动，继续吗？")
//					.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
//						@Override
//						public void onClick(DialogInterface dialog, int which) {
//							// TODO 取消活动
//						}
//					})
//					.setNegativeButton(android.R.string.cancel, null)
//					.create().show();
//				}
//				else
//				{
//					Requester2.diaryPublish(handler, myDiary.diaryid, "2", "", "", "");
//				}
//			} else {// 发布
//				CmmobiClickAgentWrapper.onEvent(this, "det_my_forw", myDiary.diaryid, 0);
//				Requester2.diaryPublish(handler, myDiary.diaryid, "1",
//						getPublishTypeSetting(), getPositionSetting(),
//						getAudioSetting());
//				// TODO 换成任务队列
//				
//			}
			
		}
		else
		{
			ZDialog.show(R.layout.progressdialog, false, true, DiaryDetailActivity.this);
			
			String status = "1";
			if(myDiary.diary_status.equals("2"))
			{
				status = "3";
			}
			
//			Requester2.diaryPublish(handler, myDiary.diaryid, "1",
//					getPublishTypeSetting(), getPositionSetting(),
//					getAudioSetting());
			// 需要继续分享的Id 在handler完毕后shareToPlat中进行
			mShareToId = platId;
			
			// 设置分享状态
			publishType = "1";
			
			// 换成任务队列
			afterPublish();
			OfflineTaskManager.getInstance().addShareToLookLook(myDiary.diaryid,myDiary.diaryuuid, getPublishTypeSetting(),
					getPositionSetting(), getAudioSetting(), status);
			ZDialog.dismiss();
		}
	}
	
	// 完成发布后的动作
	private void shareToPlat(int platId)
	{
		int sync = getSync(myDiary.diaryuuid);
		switch(platId)
		{
		case R.drawable.btn_diarydetail_active:
			if((sync > 3 && myDiary.diary_status.equals("2")) || sync == -1)
			{
				Intent activitesIntent = new Intent(this, ActivitiesActivity.class);
				activitesIntent.putExtra("comeDiaryId", myDiary.diaryid).putExtra("comeDiaryuuid", myDiary.diaryuuid);
				startActivity(activitesIntent);
			}
			else
			{
				Prompt.Alert("请等待上传完成再进行分享！");
			}
			break;
		case R.drawable.btn_diarydetail_sina:
			if(isMyself())
			{
				CmmobiClickAgentWrapper.onEvent(this, "det_my_forw", myDiary.diaryid, 1);
			}
			else
			{
				CmmobiClickAgentWrapper.onEvent(this, "det_forw", myDiary.diaryid, 1);
			}
			shareToSina();
			break;
		case R.drawable.btn_diarydetail_weixin:
			if((sync > 3 && myDiary.diary_status.equals("2")) || sync == -1)
			{
				if(isMyself())
				{
					CmmobiClickAgentWrapper.onEvent(this, "det_my_forw", myDiary.diaryid, 9);
				}
				else
				{
					CmmobiClickAgentWrapper.onEvent(this, "det_forw", myDiary.diaryid, 9);
				}
				shareToWeixin();
			}
			else
			{
				Prompt.Alert("请等待上传完成再进行分享！");
			}
			break;
		case R.drawable.btn_diarydetail_weixinfriend:
			if((sync > 3 && myDiary.diary_status.equals("2")) || sync == -1)
			{
				if(isMyself())
				{
					CmmobiClickAgentWrapper.onEvent(this, "det_my_forw", myDiary.diaryid, 59);
				}
				else
				{
					CmmobiClickAgentWrapper.onEvent(this, "det_forw", myDiary.diaryid, 59);
				}
				shareToWeixinFriend();
			}
			else
			{
				Prompt.Alert("请等待上传完成再进行分享！");
			}
			break;
		case R.drawable.btn_diarydetail_tencent:
			if(isMyself())
			{
				CmmobiClickAgentWrapper.onEvent(this, "det_my_forw", myDiary.diaryid, 6);
			}
			else
			{
				CmmobiClickAgentWrapper.onEvent(this, "det_forw", myDiary.diaryid, 6);
			}
			shareToTencent();
			break;
		case R.drawable.btn_diarydetail_renren:
			if(isMyself())
			{
				CmmobiClickAgentWrapper.onEvent(this, "det_my_forw", myDiary.diaryid, 2);
			}
			else
			{
				CmmobiClickAgentWrapper.onEvent(this, "det_forw", myDiary.diaryid, 2);
			}
			shareToRenren();
			break;
		case R.drawable.btn_diarydetail_email:
			if((sync > 3 && myDiary.diary_status.equals("2")) || sync == -1)
			{
				if(isMyself())
				{
					CmmobiClickAgentWrapper.onEvent(this, "det_my_forw", myDiary.diaryid, 11);
				}
				else
				{
					CmmobiClickAgentWrapper.onEvent(this, "det_forw", myDiary.diaryid, 11);
				}
				shareToEmail();
			}
			else
			{
				Prompt.Alert("请等待上传完成再进行分享！");
			}
			break;
		case R.drawable.btn_diarydetail_msg:
			if((sync > 3 && myDiary.diary_status.equals("2")) || sync == -1)
			{
				if(isMyself())
				{
					CmmobiClickAgentWrapper.onEvent(this, "det_my_forw", myDiary.diaryid, 10);
				}
				else
				{
					CmmobiClickAgentWrapper.onEvent(this, "det_forw", myDiary.diaryid, 10);
				}
				shareToMsg();
			}
			else
			{
				Prompt.Alert("请等待上传完成再进行分享！");
			}
			break;
		default:
			break;
		}
	}
	
	// 分享到活动
	private void shareToActive ()
	{
		int sync = getSync(myDiary.diaryuuid);
		if((sync > 3 && myDiary.diary_status.equals("2")) || sync == -1)
		{
			String activeString=new Gson().toJson(myDiary.active);
			startActivity(new Intent(this, ActivitiesActivity.class)
			.putExtra(ActivitiesDetailActivity.ACTION_ACTIVIES, activeString)
			.putExtra("comeDiaryId", myDiary.diaryid).putExtra("comeDiaryuuid", myDiary.diaryuuid));
			
	//		Intent activitesIntent = new Intent(this, ActivitiesActivity.class);
	//		activitesIntent.putExtra("comeDiaryId", myDiary.diaryid);
	//		startActivity(activitesIntent);
		}
		else
		{
			new Xdialog.Builder(DiaryDetailActivity.this)
			.setTitle("")
			.setMessage("请等待上传完成！")
			.setNegativeButton(android.R.string.ok, null)
			.create().show();
		}
	}
	
	// 分享到新浪
	private void shareToSina ()
	{
//		if(!isSinaBind()){
//			//绑定新浪微博
//			CmmobiSnsLib.getInstance(this)
//			.sinaAuthorize(sinalistener);
//		}
//		else
		{
			String diaryString=new Gson().toJson(myDiary);
			startActivityForResult(new Intent(this, ShareWithSnsActivity.class)
				.putExtra(ShareWithSnsActivity.ACTION_DIARY_STRING, diaryString)
				.putExtra(INTENT_ACTION_SHARE_TYPE, "1"), REQUESTCODE_SNSDELETE);
		}
	}
		
	// 分享到微信
	private void shareToWeixin ()
	{
		int sync = getSync(myDiary.diaryuuid);
		if((sync > 3 && myDiary.diary_status.equals("2")) || sync == -1)
		{
			ZDialog.show(R.layout.progressdialog, false, true, this);
			mBShareType = 1;
			Requester2.getDiaryUrl(handler, myDiary.diaryid);
		}
		else
		{
			new Xdialog.Builder(DiaryDetailActivity.this)
			.setTitle("")
			.setMessage("请等待上传完成！")
			.setNegativeButton(android.R.string.ok, null)
			.create().show();
		}
	}
	
	// 分享到微信朋友圈
	private void shareToWeixinFriend ()
	{
		int sync = getSync(myDiary.diaryuuid);
		if((sync > 3 && myDiary.diary_status.equals("2")) || sync == -1)
		{
			ZDialog.show(R.layout.progressdialog, false, true, this);
			mBShareType = 2;
			Requester2.getDiaryUrl(handler, myDiary.diaryid);
		}
		else
		{
			new Xdialog.Builder(DiaryDetailActivity.this)
			.setTitle("")
			.setMessage("请等待上传完成！")
			.setNegativeButton(android.R.string.ok, null)
			.create().show();
		}
	}
	
	// 分享到腾讯
	private void shareToTencent ()
	{
//		if(!isTencentBind()){
//			//绑定腾讯
//			CmmobiSnsLib.getInstance(this)
//			.tencentWeiboAuthorize(tencentlistener);
//		}
//		else
		{
			String diaryString=new Gson().toJson(myDiary);
			startActivityForResult(new Intent(this, ShareWithSnsActivity.class)
			.putExtra(ShareWithSnsActivity.ACTION_DIARY_STRING, diaryString)
			.putExtra(INTENT_ACTION_SHARE_TYPE, "6"), REQUESTCODE_SNSDELETE);
		}
	}

	// 分享到人人
	private void shareToRenren ()
	{
//		if(!isRenrenBind()){
//			//绑定人人
//			CmmobiSnsLib.getInstance(this)
//			.renrenAuthorize(renrenlistener);
//		}
//		else
		{
			String diaryString=new Gson().toJson(myDiary);
			startActivityForResult(new Intent(this, ShareWithSnsActivity.class)
			.putExtra(ShareWithSnsActivity.ACTION_DIARY_STRING, diaryString)
			.putExtra(INTENT_ACTION_SHARE_TYPE, "2"), REQUESTCODE_SNSDELETE);
		}
	}

	// 分享到邮件
	private void shareToEmail ()
	{
		int sync = getSync(myDiary.diaryuuid);
		if((sync > 3 && myDiary.diary_status.equals("2")) || sync == -1)
		{
			ZDialog.show(R.layout.progressdialog, false, true, this);
			mBShareType = 11;
			Requester2.getDiaryUrl(handler, myDiary.diaryid);
		}
		else
		{
			new Xdialog.Builder(DiaryDetailActivity.this)
			.setTitle("")
			.setMessage("请等待上传完成！")
			.setNegativeButton(android.R.string.ok, null)
			.create().show();
		}
	}
	
	// 分享到短信
	private void shareToMsg ()
	{
		int sync = getSync(myDiary.diaryuuid);
		if((sync > 3 && myDiary.diary_status.equals("2")) || sync == -1)
		{
			ZDialog.show(R.layout.progressdialog, false, true, this);
			mBShareType = 10;
			Requester2.getDiaryUrl(handler, myDiary.diaryid);
		}
		else
		{
			new Xdialog.Builder(DiaryDetailActivity.this)
			.setTitle("")
			.setMessage("请等待上传完成！")
			.setNegativeButton(android.R.string.ok, null)
			.create().show();
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
//			Pattern pattern = Pattern.compile("\\[[\\u4e00-\\u9fa5]*?\\]");
			Pattern pattern = Pattern.compile("\\[[\\u4e00-\\u9fa5a-zA-Z]*?\\]");
			Matcher matcher = pattern.matcher(expressionText);
			while (matcher.find()) {
				list.add(matcher.group());
			}
			return list;
		}
		return null;
	}

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		
		setIntent(intent);
        WeiboRequester.getInstance(this).handleIntent(this, intent, this);
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//		if(requestCode==REQUESTCODE_DETAIL&&resultCode==RESULT_OK){
//			String diaryID=data.getStringExtra(INTENT_ACTION_DIARY_ID);
//			MyDiary myDiary=mDiaryManager.findLocalDiary(diaryID);
//			if(myDiary!=null){
//				for(int i=0;i<mListDiary.size();i++){
//					if(mListDiary.get(i).diaryid.equals(myDiary.diaryid)){
//						mListDiary.remove(i);
//						break;
//					}
//				}
//				mListDiary.add(myDiary);
//				setCurrent();
//			}
//		}
		//  删除微博
//		else if (requestCode == REQUESTCODE_SNSDELETE && requestCode == RESULT_OK)
//		{
//			String weiboType = data.getStringExtra(INTENT_ACTION_SHARE_TYPE);
//			ArrayList<String> deletesns = data.getStringArrayListExtra(INTENT_EXTRA_DELETE_SNS);
//			if(weiboType.equals("1"))
//			{
//				mDiaryManager.getRemoveSinaIdList().addAll(deletesns);
//				if(mFlagOfTaskSina == 1)
//				{
//					deleteSinaTask();
//				}
//			}
//			else if(weiboType.equals("2"))
//			{
//				mDiaryManager.getRemoveTencentIdList().addAll(deletesns);
//				if(mFlagOfTaskRenren == 1)
//				{
//					deleteRenrenTask();
//				}
//			}
//			else if(weiboType.equals("6"))
//			{
//				mDiaryManager.getRemoveRenrenIdList().addAll(deletesns);
//				if(mFlagOfTaskTencent == 1)
//				{
//					deleteTencentTask();
//				}
//			}
//			
//		}
		// 微博绑定必须增加
		if (CmmobiSnsLib.getInstance(this).mSsoHandler != null) {
			CmmobiSnsLib.getInstance(this).mSsoHandler.authorizeCallBack(
					requestCode, resultCode, data);
		}
		super.onActivityResult(requestCode, resultCode, data);
	}
	
	
	private class MyOnInfoListener implements OnInfoListener {

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
			String strTime = DateUtils.getFormatTime(time) + "/" + DateUtils.getFormatTime(mTotlaTime);
			int process = (int) (time * 100 / player.getTotalTime());
			handler.obtainMessage(HANDLER_UPDATE_VIDEO_PLAYER_PROCESS, process).sendToTarget();
			handler.obtainMessage(HANDLER_UPDATE_VIDEO_TIME_STRING, strTime).sendToTarget();
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
		if(ivPlay != null && playVideoProcess != null
				&& tvPlayTime != null
				&& ivExternPlayer != null)
		{
			if(show){
				ivPlay.setVisibility(View.VISIBLE);
				playVideoProcess.setVisibility(View.VISIBLE);
				if(mTotlaTime != 0d)
				{
					tvPlayTime.setVisibility(View.VISIBLE);
				}
				ivExternPlayer.setVisibility(View.VISIBLE);
			}
			else
			{
				ivPlay.setVisibility(View.INVISIBLE);
				playVideoProcess.setVisibility(View.INVISIBLE);
				tvPlayTime.setVisibility(View.INVISIBLE);
				ivExternPlayer.setVisibility(View.INVISIBLE);
			}
		}
	}
	
	private BroadcastReceiver mReceiver = new BroadcastReceiver() {
		
		@Override
		public void onReceive(Context context, Intent intent) {
			String action=intent.getAction();
			Log.d(TAG, "myReceiver->action="+action);
			if(DiaryManager.ACTION_DIARY_SYNCHRONIZED.equals(action))
			{
				// 因为日记id可能变化，所以需要刷新 
				mDiaryManager.resetDetailDiaryList(mListDiary);
				mListDiary = (ArrayList<MyDiary>) mDiaryManager.getDetailDiaryList().clone();
//				Collections.sort(mListDiary, new DiaryComparator());
				if(mListDiary != null)
				{
					refreshData();
//					setCurrent();
				}
			}
			else if(DiaryManager.DIARY_EDIT_DONE.equals(action)){
				String diaryUUID = intent.getStringExtra(INTENT_ACTION_DIARY_UUID);
				Log.d(TAG, "myReceiver->diaryUUID="+diaryUUID);
				MyDiary diary = mDiaryManager.findLocalDiaryByUuid(diaryUUID);
				if(diary == null)
				{
					diary = mDiaryManager.findLocalDiary(diaryUUID);
				}
				if(diary!=null)
				{
					Log.d(TAG, "myReceiver->diary.diaryuuid"+diary.diaryuuid);
					for(int i=0;i<mListDiary.size();i++){
						if(mListDiary.get(i).diaryuuid != null && mListDiary.get(i).diaryuuid.equals(diary.diaryuuid)){
//							mListDiary.remove(i);
							Log.d(TAG, "myReceiver->diary find");
							mListDiary.set(i, diary);
							break;
						}
						
					}
//					mListDiary.add(myDiary);
					// 因为日记id可能变化，所以需要刷新 
					mDiaryManager.resetDetailDiaryList(mListDiary);
					mListDiary = (ArrayList<MyDiary>) mDiaryManager.getDetailDiaryList().clone();
//					Collections.sort(mListDiary, new DiaryComparator());
					if(mListDiary != null)
					{
						initDataWithUUID(diary.diaryuuid);
//						setCurrent();
					}
				}
			}
			else if (DiaryManager.DIARY_LIST_EDIT_DONE.equals(action)){
				// 日记副本增加了
				String diaryUUID = intent.getStringExtra(INTENT_ACTION_DIARY_UUID);
				Log.d(TAG, "myReceiver->diaryUUID="+diaryUUID);
				MyDiary diary = mDiaryManager.findLocalDiaryByUuid(diaryUUID);
				if(diary == null)
				{
					diary = mDiaryManager.findLocalDiary(diaryUUID);
				}
				if(diary != null)
				{
					Log.d(TAG, "myReceiver->diary.diaryuuid"+diary.diaryuuid);
					mListDiary.add(diary);
				}

				// 因为日记已经同步了，所以需要刷新 
				mDiaryManager.resetDetailDiaryList(mListDiary);
				mListDiary = (ArrayList<MyDiary>) mDiaryManager.getDetailDiaryList().clone();
				Collections.sort(mListDiary, new DiaryComparator());
				if(mListDiary != null)
				{
					initDataWithUUID(diary.diaryuuid);
//					setCurrent();
				}
			}
			else if(INTENT_ACTION_SHOW.equals(action)){
				if(isFullScreenMode)return;
				isFullScreenMode=true;
				setPictureLayout();
			}else if(INTENT_ACTION_HIDDEN.equals(action)){
				if(!isFullScreenMode)return;
				isFullScreenMode=false;
				setPictureLayout();
			}else if(INTENT_ACTION_SCREEN_MODE.equals(action)){
				if(isFullScreenMode){
					isFullScreenMode=false;
				}else{
					isFullScreenMode=true;
				}
				setPictureLayout();
			}
			else if(HomepageMyselfDiaryActivity.SAVE_BOX_MODE_CHANGE.equals(action))
			{
				if(DiaryDetailMenuView.getSnsTrace(myDiary).size() > 0)
				{
					new Xdialog.Builder(DiaryDetailActivity.this)
						.setTitle("")
						.setMessage("内容放入保险箱后分享状态不变，在分享界面中可手动修改状态。")
						.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) {
								ZDialog.show(R.layout.progressdialog, false, true, DiaryDetailActivity.this);
//								Requester2.safebox(handler, myDiary.diaryid, myDiary.diaryuuid, "1");
								OfflineTaskManager.getInstance().addSafeboxAddTask(myDiary.diaryid, myDiary.diaryuuid);
								afterAddSafeBox();
								ZDialog.dismiss();
							}
						})
						.setNegativeButton(android.R.string.cancel, null)
						.create().show();
				}
				else
				{
					ZDialog.show(R.layout.progressdialog, false, true, DiaryDetailActivity.this);
//					Requester2.safebox(handler, myDiary.diaryid, myDiary.diaryuuid, "1");
					OfflineTaskManager.getInstance().addSafeboxAddTask(myDiary.diaryid, myDiary.diaryuuid);
					afterAddSafeBox();
					ZDialog.dismiss();
				}
			}
			else if(INTENT_ACTION_DELETE_SNS.equals(action))
			{
				String weiboType = intent.getStringExtra(INTENT_ACTION_SHARE_TYPE);
				if(weiboType.equals("1"))
				{
					if(mFlagOfTaskSina == 0)
					{
						deleteSinaTask();
					}
				}
				else if(weiboType.equals("2"))
				{
					if(mFlagOfTaskRenren == 0)
					{
						deleteRenrenTask();
					}
				}
				else if(weiboType.equals("6"))
				{
					if(mFlagOfTaskTencent == 0)
					{
						deleteTencentTask();
					}
				}
			}
			else if(INTENT_ACTION_SHARE_SUCCESS.equals(action) && !isMyself())
			{
//				String diaryId = intent.getStringExtra(INTENT_ACTION_DIARY_ID);
				
				String diaryString = intent.getStringExtra(ShareWithSnsActivity.ACTION_DIARY_STRING);
				if (diaryString != null && diaryString.length() > 0)
				{
					ShareSNSTrace snsTrace = new Gson().fromJson(diaryString, ShareSNSTrace.class);
					
					if(myDiary.sns == null)
					{
						myDiary.sns = new ShareSNSTrace[0];
					}
					ShareSNSTrace[] copyArray = Arrays.copyOf(myDiary.sns, (myDiary.sns.length+1));
					copyArray[myDiary.sns.length] = snsTrace;
					myDiary.sns = copyArray;
					
//					updateDiary(diaryId);
					setCurrent();
				}
				else
				{
					Log.e(TAG, "diaryString is null");
					finish();
				}
				
			}
			else if(INTENT_ACTION_PRAISE_CHANGE.equals(action))
			{
				if(isPraise())
				{
					setPraiseBtn(EPraiseType.PRAISE);
				}
				else
				{
					setPraiseBtn(EPraiseType.UNPRAISE);
				}
			}
			
		}
	};
	
	// 他人日记分享后 因为没有缓存，需要更新diary结构
//	private void updateDiary(String diaryId)
//	{
//		ZDialog.show(R.layout.progressdialog, false, true, this);
//		// 请求网络 设置日记高度和宽度
//		Requester2.getDiaryinfo(handler, myDiary.diaryid, "", "");
//	}

	class MoreViewHolder
	{
		ImageView pic;
		TextView text;
	}
	
	// More菜单
	private class MoreAdapter extends BaseAdapter
	{
		
		private String [] array;
		private Context context;
		private Integer[] icons;
		
		public MoreAdapter(Context context, String [] array, Integer[] icons) {
			this.context = context;
			this.array = array;
			this.icons = icons;
		}

		@Override
		public int getCount() {
			return array.length;
		}

		@Override
		public Object getItem(int position) {
			return array[position];
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			MoreViewHolder holder;
			//if (convertView == null) {
				holder = new MoreViewHolder();
				convertView = LayoutInflater.from(context).inflate(R.layout.list_item_diarydetail_more,
						null);
				holder.pic = (ImageView) convertView.findViewById(R.id.iv_diarydetail_more);
				holder.text = (TextView) convertView.findViewById(R.id.tv_diarydetail_more);
				holder.pic.setBackgroundResource(icons[position]);
				holder.text.setText(array[position]);
				//convertView.setTag(holder);
				convertView.setTag(icons[position]);
			//} else {
				//holder = (ViewHolder) convertView.getTag();
			//}
			return convertView;
		}
		
	}
	
	public int isDiaryEditable(MyDiary diary) {
		if (diary != null) {
			diaryAttach[] attachs = myDiary.attachs;
			if (attachs == null) {
				return 0;
			}
			for (diaryAttach attach:attachs) {
				if ("1".equals(attach.attachtype)) {
//					String keyStr = null;
//					MediaValue mediaValue = null;
					if (attach.attachvideo != null && attach.attachvideo.length > 0 ) {
						for (MyAttachVideo videoAttach:attach.attachvideo) {
							if (videoAttach.playvideourl != null && !"".equals(videoAttach.playvideourl)) {
								/*keyStr = videoAttach.playvideourl;
								
								mediaValue = AccountInfo.getInstance(userID).mediamapping.getMedia(userID, keyStr);
								if (mediaValue != null) {
									String videoPath = HomeActivity.SDCARD_PATH + mediaValue.path;
									if (PluginUtils.isPluginMounted()) {
										XEffects effect = new XEffects();
										XMediaPlayer mediaPlayer = new XMediaPlayer(this, effect, true);
										boolean isEditable = mediaPlayer.isSupportOpen(videoPath);
										mediaPlayer.release();
										if (isEditable) {
											return 0;
										} else {
											return -1;
										}
									} else {
										if ("0".equals(videoAttach.videotype)||"4".equals(videoAttach.videotype)) {
											return -1;
										} else {
											return 0;
										}
									}
								}*/
								
								if ("0".equals(videoAttach.videotype)||"4".equals(videoAttach.videotype)) {
									return -1;
								}
							}
						}
					}
				}
			}
		}
		return 0;
	}
	
//	// 避免自动播放冲突
//	private void stopAutoPlay()
//	{
//		// 避免冲突
//		if(getHandler().hasMessages(HANDLER_PLAY_VIDEO))
//		{
//			getHandler().removeMessages(HANDLER_PLAY_VIDEO);
//		}
//		if(getHandler().hasMessages(HANDLER_PLAY_AUDIO))
//		{
//			getHandler().removeMessages(HANDLER_PLAY_AUDIO);
//		}
//	}
	
	// 处理下载逻辑
	private void downloadVedio(MyAttachVideo attachVideo)
	{
		MyDiary downloadDiary=new MyDiary();
		downloadDiary.active=myDiary.active;
		downloadDiary.attachs=myDiary.attachs.clone();
		downloadDiary.collectcount=myDiary.collectcount;
		downloadDiary.commentcount=myDiary.commentcount;
		downloadDiary.diary_status=myDiary.diary_status;
		downloadDiary.diaryid=myDiary.diaryid;
		downloadDiary.diarytimemilli=myDiary.diarytimemilli;
		downloadDiary.diaryuuid=myDiary.diaryuuid;
		downloadDiary.duplicate=myDiary.duplicate;
		downloadDiary.enjoycount=myDiary.diary_status;
		downloadDiary.forwardcount=myDiary.forwardcount;
		downloadDiary.headimageurl=myDiary.headimageurl;
		downloadDiary.introduction=myDiary.introduction;
		downloadDiary.iscollect=myDiary.diary_status;
		downloadDiary.join_safebox=myDiary.join_safebox;
		downloadDiary.latitude=myDiary.latitude;
		downloadDiary.position_status=myDiary.position_status;
		downloadDiary.longitude=myDiary.longitude;
		downloadDiary.mood=myDiary.mood;
		downloadDiary.nickname=myDiary.nickname;
		downloadDiary.position=myDiary.position;
		downloadDiary.publish_status=myDiary.publish_status;
		downloadDiary.publishid=myDiary.publishid;
		downloadDiary.sex=myDiary.sex;
		downloadDiary.shorturl=myDiary.shorturl;
		downloadDiary.signature=myDiary.signature;
		downloadDiary.size=myDiary.size;
		downloadDiary.sns=myDiary.sns;
		downloadDiary.snscollect_sina=myDiary.snscollect_sina;
		downloadDiary.snscollect_tencent=myDiary.snscollect_tencent;
		downloadDiary.tags=myDiary.tags;
		downloadDiary.updatetimemilli=myDiary.updatetimemilli;
		downloadDiary.userid=myDiary.userid;
		downloadDiary.weather=myDiary.weather;
		downloadDiary.weather_info=myDiary.weather_info;
		downloadDiary.weight=myDiary.weight;
//		if(myDiary.attachs!=null){
//			for(int i=0;i<myDiary.attachs.length;i++){
//				diaryAttach attach=myDiary.attachs[i];
//				if("1".equals(attach.attachtype)){
//					attach.attachvideo=new MyAttachVideo[1];
//					attach.attachvideo[0]=attachVideo;
//				}
//			}
//		}
		if(downloadDiary.attachs != null)
		{
			for(int i = 0; i < downloadDiary.attachs.length; i++)
			{
				diaryAttach attach = downloadDiary.attachs[i];
				if("1".equals(attach.attachtype))
				{
					downloadDiary.attachs[i] = new diaryAttach();
					downloadDiary.attachs[i].attachid = attach.attachid;
					downloadDiary.attachs[i].attachuuid = attach.attachuuid;
					downloadDiary.attachs[i].attachtype = attach.attachtype;
					downloadDiary.attachs[i].attachlevel = attach.attachlevel;
					downloadDiary.attachs[i].attachimage = attach.attachimage;
					downloadDiary.attachs[i].playtime = attach.playtime;
					downloadDiary.attachs[i].attachaudio = attach.attachaudio;
					downloadDiary.attachs[i].attachtimemilli = attach.attachtimemilli;
					downloadDiary.attachs[i].playtimes = attach.playtimes;
					downloadDiary.attachs[i].orshare = attach.orshare;
					downloadDiary.attachs[i].content = attach.content;
					downloadDiary.attachs[i].videocover = attach.videocover;
					downloadDiary.attachs[i].pic_width = attach.pic_width;
					downloadDiary.attachs[i].pic_height = attach.pic_height;
					downloadDiary.attachs[i].show_width = attach.show_width;
					downloadDiary.attachs[i].show_height = attach.show_height;
					
					downloadDiary.attachs[i].attachvideo = new MyAttachVideo[1];
					downloadDiary.attachs[i].attachvideo[0] = attachVideo;
				}
			}
		}
		addDownloadTask(downloadDiary);
	}
	
	Animation leftanim;
	Animation rightanim;
	Animation staffanim;

	// 开始播放动画
	private void startAudioPlayAnimation()
	{
		if(ivLeftWheel !=null &&
				ivRightWheel !=null &&
				ivLeftStaff !=null &&
				ivRightStaff !=null)
		{
			LinearInterpolator lir = new LinearInterpolator();  
			leftanim = AnimationUtils.loadAnimation(this, R.anim.audio_leftwheel_bg_animation);
			rightanim = AnimationUtils.loadAnimation(this, R.anim.audio_rightwheel_bg_animation);
			staffanim = AnimationUtils.loadAnimation(this, R.anim.audio_staff_bg_animation);
			leftanim.setInterpolator(lir);
			rightanim.setInterpolator(lir);
			staffanim.setInterpolator(lir);
			ivLeftWheel.startAnimation(leftanim);
			ivRightWheel.startAnimation(rightanim);
			ivLeftStaff.startAnimation(staffanim);
			ivRightStaff.startAnimation(staffanim);
		}
		if(ivAudioPlay != null)
		{
			ivAudioPlay.setBackgroundResource(R.drawable.diarydetail_audio_play);
		}
	}
	
	// 停止播放动画
	private void stopAudioPlayAnimation()
	{
		if(ivLeftWheel !=null &&
				ivRightWheel !=null &&
				ivLeftStaff !=null &&
				ivRightStaff !=null)
		{
			ivLeftWheel.clearAnimation();
			ivRightWheel.clearAnimation();
			ivLeftStaff.clearAnimation();
			ivRightStaff.clearAnimation();
		}
		if (ivAudioPlay != null)
		{
			ivAudioPlay.setBackgroundResource(R.drawable.diarydetail_audio_stop);
		}
		if (playAudioProcess != null)
		{
			playAudioProcess.setProgress(0);
		}
	}
	
	// 暂停播放动画
	private void pauseAudioPlayAnimation()
	{
		if(ivLeftWheel !=null &&
				ivRightWheel !=null &&
				ivLeftStaff !=null &&
				ivRightStaff !=null)
		{
			ivLeftWheel.clearAnimation();
			ivRightWheel.clearAnimation();
			ivLeftStaff.clearAnimation();
			ivRightStaff.clearAnimation();
		}
		if(ivAudioPlay != null)
		{
			ivAudioPlay.setBackgroundResource(R.drawable.diarydetail_audio_pause);
		}
	}
	
	// 获取分享文字
//	private String getContent()
//	{
//		for(int i = 0; i < myDiary.attachs.length; i++)
//		{
//			if(myDiary.attachs[i].attachtype.equals("4"))
//			{
//				return myDiary.attachs[i].content;
//			}
//		}
//		return "";
//	}
	
	// 加入保险箱的后续操作
	private void afterAddSafeBox()
	{
		myDiary.join_safebox = "1";
		mDiaryManager.saveDiaries(myDiary, true);
		// 3.删除该日记相关站内和第三方信息
		ArrayList<ArrayList<String>> snsIDs = mDiaryManager
				.getSNSShareId(myDiary);
		if (snsIDs != null && snsIDs.size() > 0) {
			mDiaryManager.getRemoveSinaIdList().clear();
			mDiaryManager.getRemoveSinaIdList()
					.addAll(snsIDs.get(0));
			mDiaryManager.getRemoveTencentIdList().clear();
			mDiaryManager.getRemoveTencentIdList().addAll(
					snsIDs.get(1));
			mDiaryManager.getRemoveRenrenIdList().clear();
			mDiaryManager.getRemoveRenrenIdList().addAll(
					snsIDs.get(2));
			deleteSNSTask();
		}

	}
	
	// 移除保险箱的后续操作
	private void afterRemoveSafeBox()
	{
		// 根据日记ID更新缓存中日记状态
		myDiary.join_safebox = "0";
		mDiaryManager.saveDiaries(myDiary, true);
	}
	
	// 发布后的后续操作
	private void afterPublish()
	{
		INetworkTask t = NetworkTaskManager.getInstance(userID).getTask(myDiary.diaryuuid);
//		if (publishType.equals("4"))
//		{// 如果原来为已发布，返回为取消发布成功
//			Prompt.Alert("已取消发布");
//			myDiary.diary_status = "1";
//			myDiary.publish_status = "4";
//			myDiary.location_status = "4";
//			initBtnComment();
//			mDiaryManager.modifyDiaryPublishStatus(myDiary.diaryuuid, "1");
//			if(t != null) {
//				t.info.isPublish = false;
//			}
//			
//			// 删除分享轨迹
//			mDiaryManager.deleteSnsTraceForLooklook(myDiary.diaryid);
//			myDiary.sns = mDiaryManager.getSnsTrace(myDiary.diaryid);
//			
//			// 保存分享状态
//			mDiaryManager.setShareStatus(myDiary.diaryid, "4", "4");
//			
//		}
//		else
		{
			// 发布成功
//			if(isPublished())
//			{
//				Prompt.Alert("修改发布成功");
//			}
//			else
//			{
//				Prompt.Alert("发布成功");
//			}
			
			myDiary.diary_status = "2";
			myDiary.publish_status = getPublishTypeSetting();
			myDiary.position_status = getPositionSetting();
			// 保存分享状态
			mDiaryManager.modifyDiaryPublishStatus(myDiary.diaryuuid, "2");
			mDiaryManager.setShareStatus(myDiary.diaryuuid, getPublishTypeSetting(), getPositionSetting());
			if(t != null) {
				t.info.isPublish = true;
			}
			initBtnComment();
			
			// 增加分享轨迹
			// 增加到DiaryManager
//			ShareSNSTrace snsTrace = new ShareSNSTrace();
//			
//			snsTrace.shareinfo = new ShareInfo[1];
//			snsTrace.shareinfo[0] = new ShareInfo();
//			snsTrace.shareinfo[0].snstype = "0";
//			
//			SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm", Locale.CHINA);       
//			String date = sDateFormat.format(new java.util.Date()); 
//			long time = TimeHelper.getInstance().now();
//			snsTrace.sharetime = String.valueOf(time);
			
//			mDiaryManager.addSnsTrace(myDiary.diaryid, snsTrace);
			
			// 刷新菜单状态
//			myDiary.sns = mDiaryManager.getSnsTrace(myDiary.diaryid);
			
			shareToPlat(mShareToId);
		}
	}
	
	// 收藏后的操作
	private void afterCollect()
	{
		//加入收藏日记列表
		mDiaryManager.addCollectDiaryID(myDiary.diaryid);
		mDiaryManager.addDiaryToCollect(myDiary);
		mDiaryManager.myCollectDataChanged();
//		initBtnEnshrine();

	}
	
	// 取消收藏后的操作
	private void afterCancelCollect()
	{
		//从收藏日记列表移除
		mDiaryManager.removeCollectDiaryID(myDiary.diaryid);
		mDiaryManager.removeCollectDiaryByID(myDiary.diaryid);
		mDiaryManager.myCollectDataChanged();
//		initBtnEnshrine();

	}
	
	// 删除日记后的操作
	private void afterDeleteDiary()
	{
		// 删除本地缓存日记
		mDiaryManager.removeDiaryByUUID(myDiary.diaryuuid,true);
		updateDuplicate();
	}
	
	// 获取缩略图Url
	private String getThumbUrl(MyDiary diary)
	{
		String thumbUrl = null;
		for(int i = 0; i < diary.attachs.length; i++)
		{
			if(diary.attachs[i].attachtype.equals("3"))
			{
				int show_width = 0;
				int show_heigh = 0;
				if (DateUtils.isNum(diary.attachs[i].show_width))
					show_width = Integer.parseInt(diary.attachs[i].show_width);
				if (DateUtils.isNum(diary.attachs[i].show_height))
					show_heigh = Integer.parseInt(diary.attachs[i].show_height);
//				String imageUrl = DiaryListView.getAttachUrl(diary.attachs[i].attachimage);
				String imageUrl = getImageUrl();
				Log.d(TAG, "show_width=" + show_width);
				Log.d(TAG, "show_heigh=" + show_heigh);
				if (imageUrl != null && imageUrl.length() > 0 && imageUrl.startsWith("http"))
				{
					imageUrl += "&width=" + show_width + "&heigh=" + show_heigh;
				}
				thumbUrl = imageUrl;
			}
		}
		return  thumbUrl;
	}
	
	// Url拼接
	private String getUrl(String strLink)
	{
		String url = "我和我的小伙伴都惊呆了，快来看吧！ ";
		url += strLink;
		if(myDiary.position != null && !myDiary.position.equals("") &&
				myDiary.position_status != null && myDiary.position_status.equals("1"))
		{
			
			if(isMyself())
			{
				url += " #我在这里：";
			}
			else
			{
				url += " #内容所在位置:";
			}
			url += myDiary.position;
			url += "#";
		}
		
		return url;
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
	
	// 按日记时间排序
	public class DiaryComparator implements Comparator<MyDiary> {
        public int compare(MyDiary arg0, MyDiary arg1) {
            try {
				if (Long.parseLong(arg0.diarytimemilli)<Long.parseLong(arg1.diarytimemilli)) {
				    return 1;
				}
				if (Long.parseLong(arg0.diarytimemilli)==Long.parseLong(arg1.diarytimemilli)) {
				    return 0;
				}
			} catch (NumberFormatException e) {
				e.printStackTrace();
			}
            return -1;
        }
    }
	
	// 获取已下载的日记附件大小
	public boolean isDiaryDownload()
	{
		String userID = ActiveAccount.getInstance(ZApplication.getInstance()).getUID();
		
		String videoUrl = getVideoUrl();
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

		String longRecUrl = getLongRecUrl();
		if (longRecUrl != null)
		{
			MediaValue mediaValue = AccountInfo.getInstance(userID).mediamapping.getMedia(userID, longRecUrl);
			if (!MediaValue.checkMediaAvailable(mediaValue, 4))
			{
				return false;
			}
		}

		String imageUrl = getImageUrl();
		if (imageUrl != null)
		{
			MediaValue mediaValue = AccountInfo.getInstance(userID).mediamapping.getMedia(userID, imageUrl);
			if (!MediaValue.checkMediaAvailable(mediaValue, 2))
			{
				return false;
			}
		}

		String shortRecUrl = getShortRecUrl();
		if (shortRecUrl != null)
		{
			MediaValue mediaValue = AccountInfo.getInstance(userID).mediamapping.getMedia(userID, shortRecUrl);
			if (!MediaValue.checkMediaAvailable(mediaValue, 3))
			{
				return false;
			}
		}
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
		url += "?os="+Requester2.VALUE_DEVICE_TYPE;
		url += "&browsetype=";
		url += "&cn=0";
		url += "&source=3";
		url += "&internettype="+getNetWork1(context);
		url += "&clientversion="+getVersionName(context);
		url += "&userid="+userId;
		url += "&gps="+gps;
		url += "&contentid="+contentId;
		url += "&imei="+Requester2.VALUE_IMEI;
		url += "&imsi="+Requester2.VALUE_IMSI;
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
		for(int i = 0; i < diary.attachs.length; i++)
		{
			if(diary.attachs[i].attachtype.equals(type))
			{
				return diary.attachs[i].attachid;
			}
		}
		return "";
	}
	
	// 根据diaryID在DiaryManager中查询是否有缓存，找到放回diary的sync_status，
	// 找不到返回-1，此时sync_status状态应大于3，下载状态需要再行判断
	private int getSync(String diaryUuID)
	{
		MyDiary myDiary = null;
		if (diaryUuID != null)
		{
			myDiary = DiaryManager.getInstance().findLocalDiaryByUuid(diaryUuID);
		}
		if (null == myDiary)
		{
			return -1;
		}
		else
		{
			return myDiary.sync_status;
		}
	}


	@Override
	public void onReq(BaseReq req) {
		Log.e(TAG, "onReq - getType:" + req.getType());
		switch (req.getType()) {
		case ConstantsAPI.COMMAND_GETMESSAGE_FROM_WX:
			Prompt.Alert(this, "COMMAND_GETMESSAGE_FROM_WX");		
			break;
		case ConstantsAPI.COMMAND_SHOWMESSAGE_FROM_WX:
			Prompt.Alert(this, "COMMAND_SHOWMESSAGE_FROM_WX");
			break;
		default:
			break;
		}
	}

	@Override
	public void onResp(BaseResp resp) {
		Log.e(TAG, "onResp - ErrCode:" + resp.errCode);
		switch (resp.errCode) {
		case BaseResp.ErrCode.ERR_OK:
			Prompt.Alert(this, "分享成功");
			break;
		case BaseResp.ErrCode.ERR_USER_CANCEL:
			Prompt.Alert(this, "取消分享");
			break;
		case BaseResp.ErrCode.ERR_AUTH_DENIED:
			Prompt.Alert(this, "分享失败，授权不成功");
			break;
		default:
			Prompt.Alert(this, "分享失败");
			break;
		}
		
	}

}
