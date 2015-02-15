package com.cmmobi.looklook.activity;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.LinkedBlockingQueue;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.DialogInterface.OnKeyListener;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnErrorListener;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.NetworkInfo.State;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.FrameLayout.LayoutParams;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import cn.zipper.framwork.core.ZApplication;
import cn.zipper.framwork.core.ZBroadcastReceiver;
import cn.zipper.framwork.core.ZDialog;
import cn.zipper.framwork.core.ZLog;
import cn.zipper.framwork.device.ZSimCardInfo;
import cn.zipper.framwork.io.network.ZNetworkStateDetector;
import cn.zipper.framwork.utils.ZStringUtils;

import com.cmmobi.looklook.MainApplication;
import com.cmmobi.looklook.R;
import com.cmmobi.looklook.common.adapter.DiaryCommentsAdapter;
import com.cmmobi.looklook.common.gson.DiaryController;
import com.cmmobi.looklook.common.gson.DiaryController.DiaryWrapper;
import com.cmmobi.looklook.common.gson.DiaryController.FileOperate;
import com.cmmobi.looklook.common.gson.DiaryDownloader;
import com.cmmobi.looklook.common.gson.GsonProtocol;
import com.cmmobi.looklook.common.gson.GsonRequest3.Attachs;
import com.cmmobi.looklook.common.gson.GsonResponse3;
import com.cmmobi.looklook.common.gson.GsonResponse3.AuxAttach;
import com.cmmobi.looklook.common.gson.GsonResponse3.DiaryDetailComment;
import com.cmmobi.looklook.common.gson.GsonResponse3.EnjoyHead;
import com.cmmobi.looklook.common.gson.GsonResponse3.MyBind;
import com.cmmobi.looklook.common.gson.GsonResponse3.MyDiary;
import com.cmmobi.looklook.common.gson.GsonResponse3.MyDiaryList;
import com.cmmobi.looklook.common.gson.GsonResponse3.addfriendResponse;
import com.cmmobi.looklook.common.gson.GsonResponse3.deleteEnjoyResponse;
import com.cmmobi.looklook.common.gson.GsonResponse3.diaryInfoResponse;
import com.cmmobi.looklook.common.gson.GsonResponse3.diaryShareInfoResponse;
import com.cmmobi.looklook.common.gson.GsonResponse3.diarySharePermissionsResponse;
import com.cmmobi.looklook.common.gson.GsonResponse3.enjoyResponse;
import com.cmmobi.looklook.common.gson.Requester3;
import com.cmmobi.looklook.common.gson.WeiboRequester;
import com.cmmobi.looklook.common.listener.DiaryPagerTouchInterface;
import com.cmmobi.looklook.common.listener.DiaryTouchInterface;
import com.cmmobi.looklook.common.utils.CmmobiClickAgentWrapper;
import com.cmmobi.looklook.common.utils.DateUtils;
import com.cmmobi.looklook.common.utils.DensityUtil;
import com.cmmobi.looklook.common.utils.EffectsDownloadUtil;
import com.cmmobi.looklook.common.view.DiaryDetailPager;
import com.cmmobi.looklook.common.view.FriendsExpressionView;
import com.cmmobi.looklook.common.view.InnerScrollView;
import com.cmmobi.looklook.common.view.InputRecoderView;
import com.cmmobi.looklook.common.view.InputRecoderView.AudioRecoderBean;
import com.cmmobi.looklook.common.view.InputRecoderView.InputStrType;
import com.cmmobi.looklook.common.view.InputRecoderView.OnSendListener;
import com.cmmobi.looklook.common.view.MultiPointTouchImageView;
import com.cmmobi.looklook.common.view.Xdialog;
import com.cmmobi.looklook.common.view.pulltorefresh.PullToRefreshBase;
import com.cmmobi.looklook.common.view.pulltorefresh.PullToRefreshBase.OnRefreshListener2;
import com.cmmobi.looklook.common.view.pulltorefresh.PullToRefreshListView;
import com.cmmobi.looklook.common.web.TackView;
import com.cmmobi.looklook.dialog.ShareDialog;
import com.cmmobi.looklook.downloader.VirtualPlayer;
import com.cmmobi.looklook.httpproxy.HttpProxy;
import com.cmmobi.looklook.httpproxy.downloader.CacheDownloader;
import com.cmmobi.looklook.info.location.POIAddressInfo;
import com.cmmobi.looklook.info.profile.AccountInfo;
import com.cmmobi.looklook.info.profile.ActiveAccount;
import com.cmmobi.looklook.info.profile.CommonInfo;
import com.cmmobi.looklook.info.profile.DiaryManager;
import com.cmmobi.looklook.info.profile.LoginSettingManager;
import com.cmmobi.looklook.info.profile.MediaValue;
import com.cmmobi.looklook.info.profile.NetworkTaskInfo;
import com.cmmobi.looklook.info.profile.TimeHelper;
import com.cmmobi.looklook.networktask.CacheNetworkTask;
import com.cmmobi.looklook.networktask.INetworkTask;
import com.cmmobi.looklook.networktask.NetworkTaskManager;
import com.cmmobi.looklook.networktask.UploadNetworkTask;
import com.cmmobi.looklook.offlinetask.OfflineTaskManager;
import com.cmmobi.looklook.prompt.Prompt;
import com.cmmobivideo.utils.PluginUtils;
import com.cmmobivideo.workers.XHsmMediaPlayer;
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
import com.tencent.mm.sdk.openapi.BaseReq;
import com.tencent.mm.sdk.openapi.BaseResp;
import com.tencent.mm.sdk.openapi.ConstantsAPI;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;

import effect.XEffectMediaPlayer;
import effect.XEffects;
import effect.XHsmVideoPlayer;

/**
 * Copyright (C) ......
 * All rights reserved.
 * @package com.cmmobi.looklook.activity
 * @filename DiaryPreviewActivity.java
 * @summary 预览页
 * @author Lanhai
 * @date 2013-11-18
 * @version 1.0
 */
public class DiaryPreviewActivity extends FragmentActivity implements OnClickListener,
		OnPageChangeListener,OnLongClickListener, IWXAPIEventHandler, OnRefreshListener2<ListView>, OnSendListener, Callback{

	private static final String TAG = "DiaryPreviewActivity";

	public static final String INTENT_ACTION_DIARY_ID = "intent_action_diary_id";
	public static final String INTENT_ACTION_DIARY_UUID = "intent_action_diary_uuid";
	public static final String INTENT_ACTION_DIARYLIST_STRING = "intent_action_diarylist_string";
	public static final String INTENT_ACTION_DIARY_STRING = "intent_action_diary_string";
	public static final String INTENT_ACTION_ATTACH_UUID = "intent_action_attach_uuid";
	public static final String INTENT_ACTION_SHARE_TYPE = "intent_action_diary_string";
	public static final String INTENT_ACTION_SHOW = "intent_action_show";
	public static final String INTENT_ACTION_HIDDEN = "intent_action_hidden";
	public static final String INTENT_ACTION_SCREEN_MODE = "intent_action_screen_mode";
	public static final String INTENT_ACTION_SHARE_SUCCESS = "intent_action_share_success";
	public static final String INTENT_ACTION_DELETE_SNS = "intent_action_delete_sns";
	public static final String INTENT_ACTION_PRAISE_CHANGE = "intent_action_praise_change";
	public static final String DIARY_EDIT_MOD = "diary_edit_mod";
	public static final String DIARY_EDIT_NEW = "diary_edit_new";
	public static final String DIARY_EDIT_REFRESH = "diary_edit_refresh";
	
	// 状态
	public static final String INTENT_ACTION_SHOW_MODE = "intent_action_show_mode";
	public static final int SHOW_MODE_NORMAL = 1;
	public static final int SHOW_MODE_SIMPLE = 2;
	public static final int SHOW_MODE_ONEDIARY = 3;
	public static final int SHOW_MODE_NOCACHE = 4;
	public static final int SHOW_MODE_DELETE = 5;
	
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
	public static final int HANDLER_UPDATE_VIDEO_PROXY_PREPARED = 0x0018;
	public static final int HANDLER_UPDATE_AUDIO_PROXY_PREPARED = 0x0019;
	public static final int HANDLER_PICTURE_DOUBLE_CLICK = 0x0020;
	
	public static final String INTENT_EXTRA_DIARY_FILTER = "intent_extra_diary_filter";
	
//	public static final String INTENT_EXTRA_PLAY_URL = "intent_extra_play_url";
//	public static final String INTENT_EXTRA_PLAY_TOTALTIME = "intent_extra_play_totaltime";
//	public static final String INTENT_EXTRA_PLAY_CURTIME = "intent_extra_play_curtime";
//	public static final String INTENT_EXTRA_PLAY_THUMB = "intent_extra_play_thumb";
	public static final String INTENT_EXTRA_DELETE_SNS = "intent_extra_delete_sns";
	
//	private static final int REQUESTCODE_DETAIL = 0x05;
//	private static final int REQUESTCODE_SNSDELETE = 0x06;
	
	private static final int DIALOG_EDIT = 0;
	private static final int DIALOG_MORE = 1;
	// 当前页面所有日记（未排序）
	private int mMode = SHOW_MODE_NORMAL;
	private ArrayList<MyDiaryList> mListDiaryGroup;
	private ArrayList<MyDiary> mListDiary = new ArrayList<MyDiary>();
	private ArrayList<MyDiary> mListDiaryNoCach = new ArrayList<MyDiary>();
	
	private PullToRefreshListView mCurrentPullList;
	
	// 排序后用于显示的日记
	private LinkedList<SortDiary> mListDuplicate = new LinkedList<SortDiary>();
	
	private Handler handler;
	
	// 当前所在页面索引
	private int mPageIndex;
	
	private ImageView mBtnPraise;
	
	private LayoutInflater inflater;
	private MyDiary myDiary = new MyDiary();
	
//	private ImageButton mBtnMyShowDetail = null;
//	private ImageButton mBtnOtherShowDetail = null;
	
	private InputRecoderView inpRecoderView;
	private AudioRecoderBean mCurrSendBean;
	
	private DiaryDetailComment mCurrCCmment;
	private DiaryDetailComment mCurrDelCmm;
	// 录音按钮
	private ImageView mBtnRecord = null;
	private ImageView mBtnComment = null;
	private String userID;
	private DiaryManager mDiaryManager;

	private DiaryDetailPager  mDiaryPreview_viewpager;
//	private View picPage, audioPage, videoPage, textPage, yuyinPage;
//	private List<View> mDiarydetail_views;
	private List<Fragment> mDiarydetail_fragments;
	
	// 中间背景
	private LinearLayout mLLDiaryBackgroud = null;

	private boolean isActivityStoped = false;
	private final int FRENPOSITON = 0x0037;
	private final int SETTING_PERSONAL_INFO = 0x0038;
	
	private ImageView ivAdditionalView = null;
	
	private RelativeLayout squareLayout = null;
	public Boolean isHasNextPage = true;
	private boolean isViewCreated = false;
	
	// 视频播报状态
	public enum EPlayStatus
	{
		NON,
		PROXY,
		OPENING,
		OPENED,
		PLAY,
		PAUSE;
		
		double seekPosition = 0d;
	}
	
	public class SortDiary
	{
		String diaryID = null;
		String diaryUUID = null;
		String groupUUID = null;
		String name = null;
		
		boolean isLoad = false;
		ArrayList<EnjoyHead> enjoyHeads = new ArrayList<EnjoyHead>();
		ArrayList<DiaryDetailComment> comments = new ArrayList<DiaryDetailComment>();
		
		String first_time = "";
		String last_time = "";
		
	}
	
	// 播放时间
	private static class PlayTime
	{
		int total;
		int current;
	}
	
	private class VirtualPlayerReceiver extends ZBroadcastReceiver {
		
		private boolean isCallbacked;
		
		public VirtualPlayerReceiver() {
			addAction(CacheDownloader.PROXY_ACTION_UPDATE_MAPPING);
		}
		
		@Override
		public void onReceive(Context context, Intent intent) {
			String url = intent.getStringExtra("remoteUrl");
			if (!isCallbacked && url != null) {
				MediaValue value = AccountInfo.getInstance(userID).mediamapping.getMedia(userID, url);
				if (value != null && value.localpath != null) {
					isCallbacked = true;
					String playPath = Environment.getExternalStorageDirectory() + value.localpath;
					playVideo(playPath);
					ZDialog.dismiss();
					Log.d(TAG,"onReceive ZDialog.dismiss()");
				}
			}
		}
		
	}
	
	private EPlayStatus ePlayStatus = EPlayStatus.NON;
	
	public static final int REQUEST_CODE = 1;
	
	private TextView[] tvTags=new TextView[3];
	private View tagsLayout;
	private ImageView vPin;
	private TextView tvPosition;//位置信息
	
	//播放时间等控件
	private HttpProxy mHttpProxy = null;
	private double seekTime = 0;
	private boolean isCacheSeek = false;
	private XMediaPlayer mMediaPlayer = null;
	private XHsmMediaPlayer mHsmPlayer = null;
	private VirtualPlayer vplayer;
	private ImageView mBtnVideoPlay;
	private ImageView mIvVideoThumbnail;
	private TackView tvMainTack;
	private RelativeLayout mVideoContent;
	
	private ImageView mIvLeftWheel = null;
	private ImageView mIvRightWheel = null;
	private TextView mTVVideoTime = null;
	private TextView mTVAudioTime = null;
	private ImageView mBtnAudioPlay = null;
	
	private MultiPointTouchImageView mIvPicture;
	
	//播放进度
	private SeekBar mPlayProcess;
	
	private LinearLayout mTlParam;
	
//	private ImageView ivWeather;
//	private TextView tvTemperature;
	
	private View vMyTitlebar;
	private View vOtherTitlebar;
	private View vDiaryInfoTile;//上面天气、位置、赞等信息layout
	private View vMyBottombar;
//	private View vOtherBottombar;
	private ImageView mBtnFriend;
	private ImageView ivVirtureTop = null;
	
	private View vDeleteTitlebar;
	
	private TextView tvMyDiaryName;
	private TextView tvOtherDiaryName;
//	private ImageView ivXiala;// 下拉箭头
	// 视频长度
	private double mTotlaTime = 0d;
	
	MyBind myBind;
	
	//弹出菜单
	private PopupWindow mChangan;
	
	//多个日记时的滑动点点
	private LinearLayout mNumLayout;
	private View mPreSelectedBt;
	
	private LinkedBlockingQueue<String> mTask = new LinkedBlockingQueue<String>();
	
	// 黑屏时盖住下部的布局
	private ImageView rlcover;
	
	// 跳到默认的评论
	private String mDefaultCommentId = null;
	
	private AccountInfo accountInfo;
	private RelativeLayout mRootLayout = null;
	private int mOffset = 0;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.d(TAG, "onCreate");
		
		userID = ActiveAccount.getInstance(ZApplication.getInstance()).getUID();
		accountInfo = AccountInfo.getInstance(userID);
		mDiaryManager = DiaryManager.getInstance();
//		mHttpProxy = HttpProxy.getInstance(userID);
		
		setContentView(R.layout.activity_diarypreview);
		
		mRootLayout = (RelativeLayout) findViewById(R.id.rl_root);
		mRootLayout.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {

            @Override
            public void onGlobalLayout() {
                // TODO Auto-generated method stub
                Rect r = new Rect();
                mRootLayout.getWindowVisibleDisplayFrame(r);

                int screenHeight = mRootLayout.getHeight();
                int heightDifference = screenHeight - r.height();
                Log.d(TAG, "Size: " + heightDifference + " screenHeight = " + screenHeight + " height = " + r.height());
                if (commentViewRect != null && heightDifference > 0) {// 键盘弹起时
                	Log.d(TAG,"commentViewRect = " + commentViewRect + " commentheight = " + commentViewRect.height() + " rheight = " + r.height());
                	if (screenHeight - commentViewRect.top + inpRecoderView.getHeight() > r.height()) {
                		mOffset = r.height() - (screenHeight - commentViewRect.top + inpRecoderView.getHeight());
                		Log.d(TAG,"offset = " + mOffset);
                		if (mCurrentPullList != null) {
                			mCurrentPullList.getRefreshableView().scrollBy(0, mOffset);
                		}
                	}
                	commentViewRect = null;
                } else if (commentViewRect == null && heightDifference == 0) {
                	if (mCurrentPullList != null && mOffset != 0) {
            			mCurrentPullList.getRefreshableView().scrollBy(0, mOffset * -1);
            		}
                	mOffset = 0;
                }
            }
        });
		
		handler = new Handler(this);
		
//		SharedPreferences sp = getSharedPreferences(GuideActivity.SP_NAME, MODE_PRIVATE);
//		if (sp.getInt(GuideActivity.SP_KEY, -1) == 1)
//		{
//			Intent intent = new Intent(DiaryPreviewActivity.this, GuideActivity.class);
//			startActivity(intent);
//		}
		
		inflater = LayoutInflater.from(this);
		
		rlcover = (ImageView) findViewById(R.id.rl_preview_bg);
		rlcover.setOnClickListener(this);

		vMyTitlebar=findViewById(R.id.ll_diarypreview_mytitle);
		vOtherTitlebar=findViewById(R.id.ll_diarypreview_othertitle);

		vMyBottombar=findViewById(R.id.ll_diarypreview_mybottom);
		vDeleteTitlebar=findViewById(R.id.ll_diarypreview_deletetitle);
		findViewById(R.id.ib_title_back).setOnClickListener(this);
		findViewById(R.id.ib_title_delete).setOnClickListener(this);
		findViewById(R.id.ib_title_myback).setOnClickListener(this);
		findViewById(R.id.ib_title_otherback).setOnClickListener(this);
		findViewById(R.id.ib_title_myback).setOnLongClickListener(this);
		findViewById(R.id.ib_title_otherback).setOnLongClickListener(this);
		findViewById(R.id.ib_title_more).setOnClickListener(this);
//		findViewById(R.id.ib_title_report).setOnClickListener(this);

		tvMyDiaryName = (TextView) findViewById(R.id.tv_title_mytext);
		tvOtherDiaryName = (TextView) findViewById(R.id.tv_title_othertext);
		
		findViewById(R.id.ib_diarypreview_myvshare).setOnClickListener(this);
		mBtnFriend = (ImageView) findViewById(R.id.ib_diarypreview_friend);
		mBtnFriend.setOnClickListener(this);
		mDiaryPreview_viewpager = (DiaryDetailPager) findViewById(R.id.diarypreview_viewpager);
		mDiaryPreview_viewpager.setOnPageChangeListener(this);

//		mDiarydetail_views = new ArrayList<View>();
//		mDiaryPreview_viewpager.setAdapter(new MyPageAdapter(mDiarydetail_views));
		
		mDiarydetail_fragments = new ArrayList<Fragment>();
		mDiaryPreview_viewpager.setAdapter(new MyFragmentPageAdapter(mDiarydetail_fragments, getSupportFragmentManager()));
		
		inpRecoderView = (InputRecoderView) findViewById(R.id.inp_recoder_view);
		inpRecoderView.setOnSendListener(this);
		hideInputView();
		
		mMode = getIntent().getIntExtra(INTENT_ACTION_SHOW_MODE, SHOW_MODE_NORMAL);
		
//		String diaryID = getIntent().getStringExtra(INTENT_ACTION_DIARY_ID);
		String diaryUUID = getIntent().getStringExtra(INTENT_ACTION_DIARY_UUID);
		mListDiaryGroup = (ArrayList<MyDiaryList>) mDiaryManager.getDetailDiaryList().clone();
		if(mDiaryManager.getDetailDiaryListType() == 2)
		{
			mMode = SHOW_MODE_NOCACHE;
			mListDiaryNoCach = (ArrayList<MyDiary>) mDiaryManager.getDiaryList().clone();
			mDiaryManager.setDetailDiaryList(new ArrayList<MyDiaryList>(), 0);
		}
		Log.d(TAG,"mMode = " + mMode);
		mListDiary = (ArrayList<MyDiary>) mDiaryManager.getmMyDiaryBuf().clone();
		mDiaryManager.setmMyDiaryBuf(new ArrayList<MyDiary>());
		
		switch(mMode)
		{
		case SHOW_MODE_NORMAL:
		case SHOW_MODE_NOCACHE:
			if(mListDiaryGroup == null || mListDiaryGroup.size() == 0)
			{
				Log.e(TAG, "mListDiaryGroup is null");
				finish();
				return;
			}
			else
			{
				Log.d(TAG, "diaryUUID=" + diaryUUID);
				Log.d(TAG, "mListDiaryGroup=" + mListDiaryGroup);
				initDataWithUUID(diaryUUID);
			}
			break;
		case SHOW_MODE_SIMPLE:
			if(mListDiary == null || mListDiary.size() == 0)
			{
				Log.e(TAG, "mListDiary is null");
				finish();
				return;
			}
			else
			{
				Log.d(TAG, "diaryUUID=" + diaryUUID);
				Log.d(TAG, "mListDiary=" + mListDiaryGroup);
				
				isFullScreenMode = true;
				vMyTitlebar.setVisibility(View.INVISIBLE);
				vMyBottombar.setVisibility(View.INVISIBLE);
				vOtherTitlebar.setVisibility(View.INVISIBLE);
				initDataWithUUID(diaryUUID);
				
				// 显示下部小点
				DisplayMetrics dm = getResources().getDisplayMetrics();
				mNumLayout = (LinearLayout) findViewById(R.id.ll_pager_num);
			    RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams)mNumLayout.getLayoutParams();
			    params.bottomMargin = DensityUtil.dip2px(this, (DensityUtil.px2dip(this, dm.heightPixels - dm.widthPixels)/2 - 10));
			    mNumLayout.setLayoutParams(params);
				initImageView(mListDiary.size());
			}
			break;
		case SHOW_MODE_DELETE:
			if(mListDiary == null || mListDiary.size() == 0)
			{
				Log.e(TAG, "mListDiary is null");
				finish();
				return;
			}
			else
			{
				Log.d(TAG, "diaryUUID=" + diaryUUID);
				Log.d(TAG, "mListDiary=" + mListDiaryGroup);
				
				isFullScreenMode = true;
				vMyTitlebar.setVisibility(View.INVISIBLE);
				vMyBottombar.setVisibility(View.INVISIBLE);
				vOtherTitlebar.setVisibility(View.INVISIBLE);
				initDataWithUUID(diaryUUID);
				
				mDiaryPreview_viewpager.setInterceptListener(new DiaryPagerTouchInterface()
				{
					
					@Override
					public void setIntercept(boolean intercept)
					{
					}
					
					@Override
					public boolean isIntercept()
					{
						return false;
					}

					@Override
					public boolean isForbidMove() {
						// TODO Auto-generated method stub
						return false;
					}

					@Override
					public void setForbidMovable(boolean movable) {
						// TODO Auto-generated method stub
						
					}
				});
				// 显示删除title
				vDeleteTitlebar.setVisibility(View.VISIBLE);
			}
			break;
		case SHOW_MODE_ONEDIARY:
			break;
		}
		
		if (isFullScreenMode) {
			ViewTreeObserver vto1 = vMyTitlebar.getViewTreeObserver();
			vto1.addOnGlobalLayoutListener(new OnGlobalLayoutListener() {

				@Override
				public void onGlobalLayout() {
					// TODO Auto-generated method stub
					if (isViewCreated) {
						return;
					}
					isViewCreated = true;
					int c = getWindowManager().getDefaultDisplay().getWidth();
					int h = getWindowManager().getDefaultDisplay().getHeight();
					
					int addHeight = (h - c) / 2 - vMyTitlebar.getHeight();
					Log.d(TAG,"onGlobalLayout addHeight = " + addHeight);
					if (addHeight > 0) {
						LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT,addHeight);
						ivAdditionalView.setLayoutParams(params);
						ivAdditionalView.requestLayout();
					}
				} 
			});
		}

		//注册日记更新广播
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(DIARY_EDIT_MOD);
		intentFilter.addAction(DIARY_EDIT_NEW);
		intentFilter.addAction(DIARY_EDIT_REFRESH);
		LocalBroadcastManager.getInstance(this).registerReceiver(mReceiver, intentFilter);
		
		if(!isMyself())
		{
			vMyBottombar.setVisibility(View.GONE);
		}
		
		mDefaultCommentId = getIntent().getStringExtra("commentid");
	}
	
	private void createDefalutDiary() {
		String diaryUUID1 = DiaryController.getNextUUID();
		String attachUUID = DiaryController.getNextUUID();
		DiaryController.getInstanse().includeDiary(handler,
				diaryUUID1,
				attachUUID,
				"/sdcard/Sequence01_2.mp4",
				GsonProtocol.ATTACH_TYPE_VIDEO,
				GsonProtocol.SUFFIX_MP4,
				"",
				"",
				GsonProtocol.EMPTY_VALUE,
				CommonInfo.getInstance().getLongitude(),
				CommonInfo.getInstance().getLatitude(),
				DiaryController.getPositionString1(),
				"",
				false,
				FileOperate.COPY,
				"");
		Log.d(TAG,"createDiary1 uuid = " + diaryUUID1);
		
		String diaryUUID2 = DiaryController.getNextUUID();
		String attachUUID2 = DiaryController.getNextUUID();
		DiaryController.getInstanse().includeDiary(handler,
				diaryUUID2,
				attachUUID2,
				"/sdcard/Sequence01_3.mp4",
				GsonProtocol.ATTACH_TYPE_VIDEO,
				GsonProtocol.SUFFIX_MP4,
				"",
				"",
				GsonProtocol.EMPTY_VALUE,
				CommonInfo.getInstance().getLongitude(),
				CommonInfo.getInstance().getLatitude(),
				DiaryController.getPositionString1(),
				"",
				false,
				FileOperate.COPY,
				"");
		
		Log.d(TAG,"createDiary2 uuid = " + diaryUUID2);
	}

	@Override
	public void onPageScrollStateChanged(int arg0) {
//		Log.d(TAG,"onPageScrollStateChanged arg0 = " + arg0);
	}

	@Override
	public void onPageScrolled(int arg0, float arg1, int arg2) {
//		Log.d(TAG,"onPageScrolled arg0 = " + arg0 + " arg1 = " + arg1 + " arg2 = " + arg2);
	}

	@Override
	public void onPageSelected(int index) {
		// 统计日记id
//		CmmobiClickAgentWrapper.onEvent(this, "details_contentid", myDiary.diaryid);

		AudioPlayer.stop();
		stopAudioPlayAnimation();
		stopVideo();
		
		// 简易模式下的小点
		if(mNumLayout != null && mNumLayout.isShown())
		{
			if(mNumLayout.getChildCount() > 0){
				//设置滑动的点点的显示
				if (mPreSelectedBt != null) {
					mPreSelectedBt.setBackgroundResource(R.drawable.home_icon1);
				} else {
					View preButton = mNumLayout.getChildAt(mPageIndex);
					preButton.setBackgroundResource(R.drawable.home_icon1);
				}
				View currentBt = mNumLayout.getChildAt(index);
				currentBt.setBackgroundResource(R.drawable.home_icon2);
				mPreSelectedBt = currentBt;
			}
		}
		
		mDiaryPreview_viewpager.setInterceptListener(null);
		mPageIndex = index;
		Log.d(TAG, "onPageSelected->pageIndex=" + mPageIndex);
		
		if (((SingleDiaryFragment)mDiarydetail_fragments.get(mPageIndex)).getContentView() != null) {
			setCurrent();
		}
		Log.d(TAG, "onPageSelected->pageIndex=" + mPageIndex);
		
		
		if(index == (mDiarydetail_fragments.size() - 1))
		{
		}
		
	}
	
	@Override
	public void onResume() {
		isActivityStoped = false;
		super.onResume();
		// 2014-04-23;
		CmmobiClickAgentWrapper.onResume(this);
		inpRecoderView.mRegisterReceiver();
		
		// 统计
		CmmobiClickAgentWrapper.onEventBegin(this, "preview_page");
	}
	
	private double seekPosition = 0.0f;

	@Override
	public void onPause() {
		Log.d(TAG,"onPause in");
		super.onPause();
		TackView tackView = TackView.getTackView();
		if (tackView != null)
		{
			tackView.stop();
		}
		if (vplayer != null) {
			vplayer.stop();
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
			ePlayStatus.seekPosition = getCurrentTime();
			seekPosition = ePlayStatus.seekPosition;
		}
		if(mIvVideoThumbnail != null)
		{
			mIvVideoThumbnail.clearAnimation();
//			mIvVideoThumbnail.setVisibility(View.VISIBLE);
		}
		
		CmmobiClickAgentWrapper.onPause(this);
		
		// 统计
		CmmobiClickAgentWrapper.onEventEnd(this, "preview_page");
	}
	
	private boolean isMediaStop = false;
	@Override
	public void onStop(){
		// 避免冲突
//		stopAutoPlay();
		Log.d(TAG,"onStop");
		if(!isPlayerNull())
		{
			mIvVideoThumbnail.clearAnimation();
			stop();
			
			if(mBtnVideoPlay != null)
			{
//				if(handler.hasMessages(HANDLER_AUTO_HIDE_CONTROLLER))
//				{
//					handler.removeMessages(HANDLER_AUTO_HIDE_CONTROLLER);
//				}
				mBtnVideoPlay.setBackgroundResource(R.drawable.btn_diarypreview_video_play);
				mBtnVideoPlay.setVisibility(View.VISIBLE);
			}
			isMediaStop = true;
		}
		
		if (mPlayProcess != null)
		{
//			mPlayProcess.setProgress(0);
		}
		
		// 标记归位
		if(mBtnVideoPlay != null)
		{
			mBtnVideoPlay.setBackgroundResource(R.drawable.btn_diarypreview_video_play);
			mBtnVideoPlay.setVisibility(View.VISIBLE);
		}
		
		if(mHttpProxy != null) {
			mHttpProxy.stopProxy();
		}
		
		setPlayBtnStatus(true);
		
		isActivityStoped = true;
		super.onStop();
		CmmobiClickAgentWrapper.onStop(this);
		inpRecoderView.mUnRegisterReceiver();
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
		
		if(mHttpProxy != null) {
			mHttpProxy.stopProxy();
			mHttpProxy = null;
		}
		
		LocalBroadcastManager.getInstance(this).unregisterReceiver(mReceiver);

		super.onDestroy();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event)
	{
		return super.onKeyDown(keyCode, event);
	}
	
//	private final int HANDLER_RECORD_DURATION_UPDATE_MSG = 0x87654002;
	
	@Override
	public boolean onLongClick(View view) {
		switch (view.getId()) {
		case R.id.ib_title_myback:
		case R.id.ib_title_otherback:
			Intent intent = new Intent(this,LookLookActivity.class);
			intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(intent);
			finish();
			break;
		}
		return false;
	}
	
	@Override
	public boolean handleMessage(Message msg) {
		if (null == msg.obj
				&& msg.what != HANDLER_UPDATE_VIDEO_PLAYER_COMPLETE
				&& msg.what != HANDLER_UPDATE_AUDIO_PLAYER_COMPLETE
//				&& msg.what != HANDLER_PLAY_VIDEO
//				&& msg.what != HANDLER_PLAY_AUDIO
				&& msg.what != WeiboRequester.WEIXIN_INTERFACE_SEND
				&& msg.what != ExtAudioRecorder.HANDLER_AUDIO_RECORDER_DONE
				&& msg.what != QISR_TASK.HANDLER_QISR_RESULT_CLEAN
				&& msg.what != QISR_TASK.HANDLER_QISR_RESULT_DONE
//				&& msg.what != HANDLER_AUTO_HIDE_CONTROLLER
				&& msg.what != HANDLER_UPDATE_VIDEO_ERROR
				&& msg.what != Requester3.RESPONSE_TYPE_DIARY_COMMENTLIST
				&& msg.what != HANDLER_PICTURE_DOUBLE_CLICK)
		{
			Log.e(TAG, msg.what + " msg.obj is null");
			Prompt.Alert(getString(R.string.prompt_network_error));
			ZDialog.dismiss();
			return false;
		}
		switch (msg.what)
		{
		case HANDLER_PICTURE_DOUBLE_CLICK:
			Log.d(TAG,"HANDLER_PICTURE_DOUBLE_CLICK ");
			if(mMode != SHOW_MODE_SIMPLE && mMode != SHOW_MODE_DELETE)
			{
				if (isFullScreenMode)
				{
					isFullScreenMode = false;
					mIvPicture.setMove(false);
				}
				else
				{
					isFullScreenMode = true;
					mIvPicture.setMove(true);
				}
				setFullLayout();
			} else {
				finish();
			}
			break;
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
//		case HANDLER_AUTO_HIDE_CONTROLLER:
			// 3秒隐藏视频按钮
//			setPlayBtnStatus(false);
//			break;
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
				mTVVideoTime.setText(DateUtils.getFormatTime0000(String.valueOf((pt.current + 500) / 1000)) + " / " + DateUtils.getFormatTime0000(myDiary.getMainPlaytime()));
				if(pt.total != 0)
				{
					mPlayProcess.setProgress(pt.current * 100 / pt.total);
				}
				if(mHttpProxy != null && mHttpProxy.getTotalSize() != 0)
					mPlayProcess.setSecondaryProgress(mHttpProxy.getPercent());
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
				/*
				if(mHttpProxy != null && mHttpProxy.getTotalSize() != 0)
					mPlayProcess.setSecondaryProgress(mHttpProxy.getPercent());
				*/
//				SimpleDateFormat sdf = new SimpleDateFormat("mm:ss");
//				mTVAudioTime.setText(sdf.format(new Date(pt.current)));
				mTVAudioTime.setText(DateUtils.getFormatTime0000(String.valueOf((pt.current+500)/1000)));
			}
			break;
		case HANDLER_UPDATE_VIDEO_PROXY_PREPARED:
			if((String)msg.obj != null) {
				String localurl = (String)msg.obj;
				playVideo(localurl);
			}
			break;
		case HANDLER_UPDATE_AUDIO_PROXY_PREPARED:
			if((String)msg.obj != null) {
				String localurl = (String)msg.obj;
				AudioPlayer.playAudio(localurl, handler);
			}
			startAudioPlayAnimation();
			isFullScreenMode = true;
			setFullLayout();
			Log.d(TAG,"handlemessage HANDLER_UPDATE_AUDIO_PROXY_PREPARED");
			break;
		case HANDLER_UPDATE_VIDEO_PREPARED:
			if((Boolean)msg.obj)
			{
//				ePlayStatus = EPlayStatus.OPENED;
				play();
				// 如果是pause状态被终止播放的情况，从断点开始播放
				if(ePlayStatus == EPlayStatus.PAUSE)
				{
					Log.d(TAG,"seekPosition = " + ePlayStatus.seekPosition + " isHsmFormat = " + isHsmFormat);
					if (isHsmFormat) {
//						seek(ePlayStatus.seekPosition / 1000);
					} else {
						seek(ePlayStatus.seekPosition);
						ePlayStatus.seekPosition = 0d;
					}
				}
				ePlayStatus = EPlayStatus.PLAY;
				if(isCacheSeek) {
					seek(seekTime);
					isCacheSeek = false;
					break;
				}
				Animation anim = AnimationUtils.loadAnimation(DiaryPreviewActivity.this, R.anim.video_thumb_out);
				mIvVideoThumbnail.startAnimation(anim);
//				mIvVideoThumbnail.setVisibility(View.INVISIBLE);
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
		case Requester3.RESPONSE_TYPE_DIARY_INFO:
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
		case DiaryController.DIARY_REQUEST_DONE:
			if (msg != null) {
				Log.d(TAG,"DiaryController.DIARY_REQUEST_DONE in");
				MyDiary diary = ((DiaryWrapper) msg.obj).diary;
				String diaryString = new Gson().toJson(diary);
				Log.d("==WJM==","diaryString = " + diaryString);
//				setCurrent();

				initDataWithUUID(diary.diaryuuid);
			}
			break;
		case Requester3.RESPONSE_TYPE_DIARY_ENJOY://赞
			if(msg.obj != null)
			{
				enjoyResponse res = (enjoyResponse) msg.obj;
				if ("0".equals(res.status)) {// 赞成功
					// 加入赞日记列表
					Prompt.Alert("赞成功");
					DiaryManager.getInstance().addPraiseDiaryID(myDiary.diaryid, myDiary.publishid);
	//				mDiaryManager.myPraiseDataChanged();
					
					SortDiary sort = mListDuplicate.get(mPageIndex);
					// 如果自己赞 要把自己加入到列表
					
					
					boolean flag = false;
					for(EnjoyHead head : sort.enjoyHeads)
					{
						if(userID.equals(head.userid))
						{
							flag = true;
						}
					}
					if(!flag)
					{
						EnjoyHead eh = new EnjoyHead();
						eh.userid = userID;
						eh.headimageurl = AccountInfo.getInstance(userID).headimageurl;
						sort.enjoyHeads.add(0, eh);
						setList();
					}
					
					setPraiseBtn();
					LocalBroadcastManager.getInstance(this).sendBroadcast(new Intent(DiaryPreviewActivity.DIARY_EDIT_REFRESH));
				} else if ("138119".equals(res.status)){
					Prompt.Alert("不可赞");
				}
				else {
					Prompt.Alert("赞失败");
					Log.e(TAG, "RESPONSE_TYPE_DIARY_ENJOY status is " + res.status);
				}
			}
			else
			{
				Prompt.Alert("赞失败");
				Log.e(TAG, "response null");
			}
			ZDialog.dismiss();
			break;
		case Requester3.RESPONSE_TYPE_DELETE_ENJOY_DIARY:// 取消赞
			if(msg.obj != null)
			{
				deleteEnjoyResponse res = (deleteEnjoyResponse) msg.obj;
				if ("0".equals(res.status)) {// 取消赞成功
					// 加入赞日记列表
					Prompt.Alert("取消赞成功");
					DiaryManager.getInstance().removePraiseDiaryID(myDiary.diaryid, myDiary.publishid);
	//				mDiaryManager.myPraiseDataChanged();
					
					SortDiary sort = mListDuplicate.get(mPageIndex);
					// 如果自己赞 要把自己加入到列表
					
					
					boolean flag = false;
					Iterator<EnjoyHead> ite = sort.enjoyHeads.iterator();
					while(ite.hasNext())
					{
						EnjoyHead head = ite.next();
						if(userID.equals(head.userid))
						{
							flag = true;
							ite.remove();
						}
					}
					if(flag)
					{
						setList();
					}
					
					LocalBroadcastManager.getInstance(this).sendBroadcast(new Intent(DiaryPreviewActivity.DIARY_EDIT_REFRESH));

					setPraiseBtn();
				} else {
					Prompt.Alert("取消赞失败");
					Log.e(TAG, "RESPONSE_TYPE_DELETE_ENJOY_DIARY status is " + res.status);
				}
			}
			else
			{
				Prompt.Alert("取消赞失败");
				Log.e(TAG, "response null");
			}
			ZDialog.dismiss();
			break;
		case Requester3.RESPONSE_TYPE_DIARY_SHARE_PERMISSIONS:
			if(msg.obj != null)
			{
				diarySharePermissionsResponse res = (diarySharePermissionsResponse) msg.obj;
				if ("0".equals(res.status)) {
					Prompt.Alert("设置成功");
					
					if("2".equals(myDiary.publish_status))
					{
						myDiary.publish_status = "1";
//						removeShareInfo(101, myDiary.diaryuuid , myDiary.publishid);
						DiaryManager.getInstance().notifyMyDiaryChanged();
						//DiaryManager.getInstance().notifyMySafeboxChanged();
					}
					else
					{
						myDiary.publish_status = "2";
//						addShareInfo(101, myDiary.diaryuuid , myDiary.publishid);
						DiaryManager.getInstance().notifyMyDiaryChanged();
						//DiaryManager.getInstance().notifyMySafeboxChanged();
					}
					
					setBottom();
				} else {
					Prompt.Alert("设置失败");
				}
			}
			else
			{
				Prompt.Alert("设置失败");
			}
			ZDialog.dismiss();
			break;
		case Requester3.RESPONSE_TYPE_DIARY_SHARE_INFO:
			isHasNextPage = true;
			PullToRefreshListView vw = (PullToRefreshListView) ((SingleDiaryFragment)mDiarydetail_fragments.get(mPageIndex)).getContentView();
			if (vw != null) {
				vw.onRefreshComplete();
			}
			if(msg.obj != null)
			{
				diaryShareInfoResponse res = (diaryShareInfoResponse) msg.obj;
				for(int i = 0; i < mListDuplicate.size(); i ++)
				{
					SortDiary sort = mListDuplicate.get(i);
					if(res.diaryid != null && res.diaryid.equals(sort.diaryID))
					{
						sort.isLoad = true;
						sort.enjoyHeads.clear();
						Collections.addAll(sort.enjoyHeads, res.enjoyheadurl);
						
						// 去重
						Iterator<EnjoyHead> itehead = sort.enjoyHeads.iterator();
						Set<String> stmphead = new HashSet<String>();
						boolean isMePraised = false;
						while(itehead.hasNext())
						{
							EnjoyHead head = itehead.next();
							if (userID.equals(head.userid)) {
								isMePraised = true;
							}
							if(stmphead.contains(head.userid))
							{
								itehead.remove();
							}
							else
							{
								stmphead.add(head.userid);
							}
						}
						if (myDiary.publishid != null && myDiary.diaryid != null) {
							if (isMePraised) {
								DiaryManager.getInstance().addPraiseDiaryID(myDiary.diaryid, myDiary.publishid);
							} else {
								DiaryManager.getInstance().removePraiseDiaryID(myDiary.diaryid, myDiary.publishid);
							}
							setPraiseBtn();
						}
						
						sort.comments.clear();
						sort.first_time = "";
						sort.last_time = "";
						
						if(res.commentlist != null && res.commentlist.length > 0)
						{
							sort.first_time = res.commentlist[0].createtime;
							sort.last_time = res.commentlist[res.commentlist.length - 1].createtime;
							Collections.addAll(sort.comments, res.commentlist);
							
							// 去重
							Iterator<DiaryDetailComment> itecom = sort.comments.iterator();
							Set<String> stmpcom = new HashSet<String>();
							while(itecom.hasNext())
							{
								DiaryDetailComment com = itecom.next();
								if(stmpcom.contains(com.commentid))
								{
									itecom.remove();
								}
								else
								{
									stmpcom.add(com.commentid);
								}
							}
						}
						
						
						if(res.diaryid.equals(myDiary.diaryid))
						{
							setList();
						}
					}
				}
			}
			
//			for(int i = 0; i < mListDuplicate.size(); i ++)
//			{
//				SortDiary sort = mListDuplicate.get(i);
//				sort.isLoad = true;
//				EnjoyHead eh = new EnjoyHead();
//				eh.headimageurl = "http://news.xinhuanet.com/local/2013-11/27/125766428_11n.jpg";
//				sort.enjoyHeads.add(eh);
//				
//				DiaryDetailComment cm = new DiaryDetailComment();
//				sort.comments.add(cm);
//				
//
//			}
			
			setList();
			
			String id = mTask.poll();
			if(id != null)
			{
				Requester3.getDiaryShareInfo(handler, myDiary.diaryid, myDiary.publishid, myDiary.userid);
			}
			
			if (mDefaultCommentId != null)
			{
				setCurrentRow();
				mDefaultCommentId = null;
			}
			
			break;
		case Requester3.RESPONSE_TYPE_COMMENT:
			
			ZDialog.dismiss();
			GsonResponse3.commentResponse cmmresp = (GsonResponse3.commentResponse)msg.obj;
			if(cmmresp!=null){
				if("0".equals(cmmresp.status)){
					Prompt.Alert("评论成功");	
//					LocalBroadcastManager.getInstance(this).sendBroadcast(new Intent(SafeboxVShareFragment.ACTION_VSHARE_CHANGED));  
					if(mCurrSendBean != null ){
						String userID = ActiveAccount.getInstance(ZApplication.getInstance()).getUID();
						String commenttype = "";
						
						// 回复
						if(mCurrCCmment!=null){
							commenttype = "2";
						}else{ // 直接评论 
							commenttype = "1";
						}
						
						// 语音加文字
						if("2".equals(mCurrSendBean.commenttype) ||  
						    "3".equals(mCurrSendBean.commenttype) ){
							// 上传语音
							try {
								//参数有问题
								NetworkTaskInfo tt = new NetworkTaskInfo(userID, "", cmmresp.commentid, 
										Environment.getExternalStorageDirectory() + mCurrSendBean.localFilePath,
										cmmresp.audiopath, "3", "2");
								
								UploadNetworkTask uploadtask = new UploadNetworkTask(tt);// 创建上传/下载任务
								uploadtask.start();
								
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
						
						// 保存评论
						AccountInfo accinfo = AccountInfo.getInstance(userID);
						
						DiaryDetailComment addCmm = new DiaryDetailComment();
						
						addCmm.userid       = userID;   //评论用户ID
						addCmm.headimageurl = accinfo.headimageurl;  //头像URL
						addCmm.nickname     = accinfo.nickname;   //昵称
						addCmm.sex          = accinfo.sex;        // 0男，1女， 2未知 
						
						addCmm.commentid   = cmmresp.commentid;   //评论ID，当前评论id
						addCmm.commentuuid = cmmresp.commentuuid;  //评论UUID，当前评论uuid
						addCmm.createtime  = TimeHelper.getInstance().now() + "";  // 时间戳
						
						addCmm.publishid = myDiary.diaryid;   //日记ID
						addCmm.commentcontent = mCurrSendBean.content;  //评论
						addCmm.audiourl = mCurrSendBean.localFilePath;     // 语音地址 
						addCmm.playtime = mCurrSendBean.playtime;    //语音播放时长
						addCmm.commentway = mCurrSendBean.commenttype;  // 评论方式1、文字 2、声音3、声音加文字
						addCmm.commenttype = commenttype;  //评论类型：1、评论 2回复
						if(mCurrCCmment!=null){
							addCmm.replynickname = mCurrCCmment.nickname;  //被回复人昵称
							addCmm.replymarkname = mCurrCCmment.nickmarkname;
						}
						
						SortDiary sort = mListDuplicate.get(mPageIndex);
						sort.comments.add(0, addCmm);
						
						// 去重
						Iterator<DiaryDetailComment> itecom = sort.comments.iterator();
						Set<String> stmpcom = new HashSet<String>();
						while(itecom.hasNext())
						{
							DiaryDetailComment com = itecom.next();
							if(stmpcom.contains(com.commentid))
							{
								itecom.remove();
							}
							else
							{
								stmpcom.add(com.commentid);
							}
						}
						
						setList();
						
						inpRecoderView.clearView();
						cleanCmmData();
						
						if(isMyself())
						{
							vMyBottombar.setVisibility(View.VISIBLE);
						}
					}
				} else if ("138119".equals(cmmresp.status)){
					Prompt.Alert("不可评论");
				}else{
					Prompt.Alert("服务器返回错误，错误码：" + cmmresp.status);
				}
			}else{
				Prompt.Alert("网络不给力");
			}		
			break;
		case Requester3.RESPONSE_TYPE_DELETE_COMMENT:
			ZDialog.dismiss();
			GsonResponse3.deleteCommentResponse delCmmResp = (GsonResponse3.deleteCommentResponse)msg.obj;
			if(delCmmResp!=null){
				if("0".equals(delCmmResp.status)){
					Prompt.Alert("删除评论成功");
//					try {
//						DiaryDetailComment[] newComments = new DiaryDetailComment[mCurrDelCmmList.comments.length -1];
//						int n=0;
//						for(n=0; n< mCurrDiaryInfo.commentlistr.length; n++){
//							if(mCurrDiaryInfo.commentlistr[n].share_status.equals(mCurrDelCmmList.share_status)){
//								break;
//							}
//						}
//						int j = 0;
//						for(int i=0; i< mCurrDelCmmList.comments.length; i++, j++){
//							if(mCurrDelCmmList.comments[i].commentid.equals(mCurrDelCmm.commentid)){
//								j--;
//							}else{
//								newComments[j] = mCurrDelCmmList.comments[i];
//							}
//						}
//						mCurrDiaryInfo.commentlistr[n].comments = newComments;
//						mCurrDiaryInfo.commentlistr[n].comment_count = "" + (Integer.parseInt(mCurrDelCmmList.comment_count) -1);
//						loadViewDataComments();
//					} catch (Exception e) {
//						e.printStackTrace();
//					}
					
					SortDiary sort = mListDuplicate.get(mPageIndex);
					sort.comments.remove(mCurrDelCmm);
					
					setList();
					
					inpRecoderView.clearView();
					cleanCmmData();
				} else if ("138119".equals(delCmmResp.status)){
					Prompt.Alert("权限不足");
				}else{
					Prompt.Alert("服务器返回错误，错误码：" + delCmmResp.status);
				}
			}
			mCurrDelCmm = null;
			break;
		case Requester3.RESPONSE_TYPE_DIARY_COMMENTLIST:
			ZDialog.dismiss();
			PullToRefreshListView v = (PullToRefreshListView) ((SingleDiaryFragment)mDiarydetail_fragments.get(mPageIndex)).getContentView();
			v.onRefreshComplete();
			
			GsonResponse3.diaryCommentListResponse resp = (GsonResponse3.diaryCommentListResponse)msg.obj;
			
			if(resp != null){
				
				if (resp.status.equals("0") ) {
					if ("1".equals(resp.hasnextpage)) {
						isHasNextPage = true;
					} else {
						isHasNextPage = false;
						if(v != null) {
							v.setNoMoreData(DiaryPreviewActivity.this, false);
						}
					}
				
					if(resp.comments != null && resp.comments.length > 0)
					{
//						SortDiary sort = null;
//						for (int i = 0; i < mListDuplicate.size(); i ++)
//						{
//							sort = mListDuplicate.get(i);
//							if(resp.comments[0].publishid.equals(sort.diaryID))
//							{
//								break;
//							}
//						}
						
						SortDiary sort = mListDuplicate.get(mPageIndex);
						if ("1".equals(resp.is_refresh)) {
							sort.comments.clear();
						}
						
						sort.first_time = resp.first_comment_time;
						sort.last_time = resp.last_comment_time;
						
						Collections.addAll(sort.comments, resp.comments);
						
						// 去重
						Iterator<DiaryDetailComment> itecom = sort.comments.iterator();
						Set<String> stmpcom = new HashSet<String>();
						while(itecom.hasNext())
						{
							DiaryDetailComment com = itecom.next();
							if(stmpcom.contains(com.commentid))
							{
								itecom.remove();
							}
							else
							{
								stmpcom.add(com.commentid);
							}
						}
						
					}
				}else{
					Prompt.Alert("服务器返回错误，错误码：" + resp.status );
				}
				setList();
			}
			
			break;
		case Requester3.RESPONSE_TYPE_ADD_FRIEND:
			if (msg.obj != null)
			{
				addfriendResponse aresponse = (addfriendResponse) msg.obj;
				if ("0".equals(aresponse.status))
				{
					if (aresponse.target_userid == null)
					{
						break;
					}
					Prompt.Alert(this, "好友申请已发送");
				}
			}
			break;
		default:
			break;
		}
		return false;
	}
	
	/**
	 * 删除语音描述
	 * @param attachUUID
	 */
	private void removeVoiceDescription(String attachUUID) {
		AuxAttach attach = myDiary.getAuxAttachByUUID(attachUUID);
		String attachType = attach.attachtype;
		String suffix = "";
		if (GsonProtocol.ATTACH_TYPE_VOICE.equals(attachType)) {
			suffix = GsonProtocol.SUFFIX_MP4;
		} else if (GsonProtocol.ATTACH_TYPE_TEXT.equals(attachType)) {
			suffix = "";
		}
		Attachs reqAttach = DiaryController.getInstanse().createNewAttach(ZStringUtils.nullToEmpty(attach.attachid),
				attachUUID,
				attachType,
				suffix,
				GsonProtocol.ATTACH_LEVEL_SUB, 
				GsonProtocol.ATTACH_OPERATE_TYPE_DELETE, 
				attach.content,
				"");
		Attachs attachs[] = new Attachs[1];
		attachs[0] = reqAttach;
		
		DiaryController.getInstanse().updateDiary(handler, myDiary, attachs,GsonProtocol.TAG_UNCHANGED,myDiary.longitude_view,myDiary.latitude_view,myDiary.position_view,myDiary.position_status,myDiary.shoottime);
		DiaryController.getInstanse().diaryContentIsReady(myDiary.diaryuuid);
	}
	
	boolean isFullScreenMode;
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.iv_diarydpreview_video_thumbnail:
		case R.id.rl_diarypreview_video_surface:
		case R.id.iv_diarypreview_video_play://视频播放按钮
			// 避免冲突
			{
				if (mMode == SHOW_MODE_NORMAL && !myDiary.getMainUrl().startsWith("http://") && myDiary.getMainPath() == null) {
					String UUID = myDiary.diaryuuid;
					if(UUID != null)
					{
						refreshData(UUID);
					}
					Log.d(TAG,"onClick SHOW_MODE_NORMAL");
				}
				
				
				String url = myDiary.getMainUrl();
				
				Log.v(TAG, "getMainUrl = " + url);
				if(TextUtils.isEmpty(url))
				{
					Log.e(TAG, "audio url null");
					return;
				}
				
				String newUrl = getVideoPath(url);
				
				if(TextUtils.isEmpty(newUrl))
				{
					Log.e(TAG, "video url null");
					return;
				}
				if(ePlayStatus == EPlayStatus.PLAY)
				{
					pauseVideo();
					/*if(handler.hasMessages(HANDLER_AUTO_HIDE_CONTROLLER))
					{
						handler.removeMessages(HANDLER_AUTO_HIDE_CONTROLLER);
					}*/
				}
				else
				{
					Log.i(TAG, "use Video Url = " + newUrl + " isHsmFormat = " + isHsmFormat);
					if(!newUrl.startsWith("http")) {
						playVideo(newUrl);
						
					} else {
						if (!ZNetworkStateDetector.isAvailable()) {
							Prompt.Alert("您的网络不给力呀");
							break;
						}
						Log.e(TAG, "phone type = " + ZSimCardInfo.getDeviceName());
						String phone = ZSimCardInfo.getDeviceName();
						
						/*if(phone != null && phone.toLowerCase().contains("gt-i9500") && ePlayStatus == EPlayStatus.NON)
						{
							Log.e(TAG, "use VirturlPlayer");
							
							ZDialog.show(R.layout.progressdialog, false, true, this);
							VirtualPlayerReceiver vReceiver = new VirtualPlayerReceiver();
							vplayer = new VirtualPlayer(this, vReceiver, newUrl);
							
							vplayer.play();
						}
						else
						{*/
							if (!TextUtils.isEmpty(myDiary.getMainHsmUrl())) {
								newUrl = myDiary.getMainHsmUrl();
								ZLog.e("IS HSM URL : " + newUrl);
							} else {
								ZLog.e("NOT is HSM");
							}
							
							
							
							isHsmFormat(newUrl);
							if (isHsmFormat) {
								/*VideoDownloader videoDownloader = new VideoDownloader(newUrl, handler);
								videoDownloader.start();*/
								playVideo(newUrl);
								handler.post(dialogRunnable);
								Log.d(TAG,"onclick handler.post(dialogRunnable)");
								
							} else {
								mHttpProxy = HttpProxy.getInstance(userID);
								if (mHttpProxy != null) {
									mHttpProxy.asynGetProxyUrl(handler, newUrl, HANDLER_UPDATE_VIDEO_PROXY_PREPARED, HttpProxy.MTypeVideo);
//									mBtnVideoPlay.setBackgroundResource(R.drawable.btn_diarypreview_video_pause);
									mBtnVideoPlay.setVisibility(View.INVISIBLE);
									ePlayStatus = EPlayStatus.PROXY;
								}
							}
//						}
					}
					isFullScreenMode = true;
					setFullLayout();
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
//				Log.e(TAG, "audio url null")
//				return;
//			}
			else
			{
//				String url=v.getTag().toString();
				String url = myDiary.getMainUrl();
				
				Log.v(TAG, "getMainUrl = " + url);
				if(TextUtils.isEmpty(url))
				{
					Log.e(TAG, "audio url null");
					return;
				}
				
				String newUrl = getAudioPath(url);
				
				if(TextUtils.isEmpty(newUrl))
				{
					Log.e(TAG, "audio url null");
					return;
				}
				
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
				/*
				Log.i(TAG, "Audio newUrl = " + newUrl);
				if(!newUrl.startsWith("http")) {
					AudioPlayer.playAudio(newUrl,handler);
				} else {
					mHttpProxy = HttpProxy.getInstance(userID);
					if (mHttpProxy != null)
						mHttpProxy.asynGetProxyUrl(handler, newUrl, HANDLER_UPDATE_AUDIO_PROXY_PREPARED, HttpProxy.MTypeAudio);
					break;
				}
				*/
				startAudioPlayAnimation();
				isFullScreenMode = true;
				setFullLayout();
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
				if (TackView.getTackView() != null)
				{
					if(!TackView.getTackView().isPlaying)
					{
						isFullScreenMode = true;
						setFullLayout();
					}
				}
				tackView.setAudio(url, 1, statisUrl);
			}
			break;
		case R.id.tv_diarypreview_text_content:
		case R.id.ll_diarypreview_square:

			DiaryType diaryType = getDiaryType(myDiary);
			switch (diaryType) {
			case VEDIO://主体视频
				if(ePlayStatus != EPlayStatus.NON)
				{
					boolean show = mBtnVideoPlay.isShown();
					if(!show)
					{
//						if(handler.hasMessages(HANDLER_AUTO_HIDE_CONTROLLER))
//						{
//							handler.removeMessages(HANDLER_AUTO_HIDE_CONTROLLER);
//						}
//						handler.sendEmptyMessageDelayed(HANDLER_AUTO_HIDE_CONTROLLER, 3000);
					}
					setPlayBtnStatus(!show);
				}
				else
				{
					//全屏播放
					if(mMode != SHOW_MODE_SIMPLE && mMode != SHOW_MODE_DELETE)
					{
						if (isFullScreenMode)
						{
							isFullScreenMode = false;
						}
						else
						{
							isFullScreenMode = true;
						}
						setFullLayout();
						showShootPromtDialog();
					}
				}
				break;
			case AUDIO://主体音频
				//全屏播放
				if(mMode != SHOW_MODE_SIMPLE && mMode != SHOW_MODE_DELETE)
				{
					if (isFullScreenMode)
					{
						isFullScreenMode = false;
					}
					else
					{
						isFullScreenMode = true;
					}
					setFullLayout();
				}
				break;
			case PICTURE://主体图片
				Log.d(TAG,"ll_diarypreview_square PICTURE");
				//全屏播放
				if(mMode != SHOW_MODE_SIMPLE && mMode != SHOW_MODE_DELETE)
				{
					if (isFullScreenMode)
					{
						isFullScreenMode = false;
						mIvPicture.setMove(false);
					}
					else
					{
						isFullScreenMode = true;
						mIvPicture.setMove(true);
					}
					setFullLayout();
				}
				break;
			case TEXT://主体文字
				if (isFullScreenMode)
				{
					isFullScreenMode = false;
				}
				else
				{
					isFullScreenMode = true;
				}
				setFullLayout();
				break;
			}
			break;
		case R.id.ll_diary_bg:
			DiaryType diaryType1 = getDiaryType(myDiary);
			switch (diaryType1) {
			case VEDIO://主体视频
				pauseVideo();
				break;
			case AUDIO://主体音频
				AudioPlayer.pause();
				pauseAudioPlayAnimation();
				break;
			case PICTURE://主体图片
				break;
			case TEXT://主体文字
				if (TackView.getTackView() != null)
				{
					TackView.getTackView().stop();
				}
				break;
			}
			//全屏播放
			if(mMode != SHOW_MODE_SIMPLE && mMode != SHOW_MODE_DELETE)
			{
				if (isFullScreenMode)
				{
					isFullScreenMode = false;
					setFullLayout();
					showShootPromtDialog();
				}
			}
			else
			{
				finish();
			}
			break;
		case R.id.iv_additional_space:
		case R.id.rl_preview_bg:
			DiaryType diaryType2 = getDiaryType(myDiary);
			switch (diaryType2) {
			case VEDIO://主体视频
				pauseVideo();
				break;
			case AUDIO://主体音频
				AudioPlayer.pause();
				pauseAudioPlayAnimation();
				break;
			case PICTURE://主体图片
				mIvPicture.setMove(false);
				break;
			case TEXT://主体文字
				if (TackView.getTackView() != null)
				{
					TackView.getTackView().stop();
				}
				break;
			}
			//全屏播放
			if(mMode != SHOW_MODE_SIMPLE && mMode != SHOW_MODE_DELETE)
			{
				if (isFullScreenMode)
				{
					isFullScreenMode = false;
					setFullLayout();
					showShootPromtDialog();
				}
			}
			else
			{
				finish();
			}
			break;
		case R.id.diarypreview_param_tack:
//			if (v.getTag() != null)
			{
//				String url = v.getTag().toString();
				String url = myDiary.attachs.attach[0].attachurl;
				if (url != null && !url.startsWith("http://") && myDiary.getAuxAttachPath() == null){
					String UUID = myDiary.diaryuuid;
					if(UUID != null)
					{
						refreshData(UUID);
					}
					Log.d(TAG,"onClick SHOW_MODE_NORMAL");
				}
				if (!isNetworkConnected(this, url, 3))
				{
					Prompt.Alert("您的网络不给力呀");
					break;
				}
				
				if(mMediaPlayer != null && mMediaPlayer.getStatus() == XEffectMediaPlayer.STATUS_PALYING)
				{
					pauseVideo();
				}
				AudioPlayer.pause();
				
				TackView tackView = (TackView) v;
				// tackView.setHandler(handler);
				String statisUrl = "";
				if (url.startsWith("http"))
				{
					statisUrl = getStatisString(this, myDiary.userid, myDiary.latitude_view + ":" + myDiary.longitude_view,
							getMediaId(myDiary, "2"), "4", "4");
				}
				tackView.setAudio(url, 1, statisUrl);
			}
			break;
		case R.id.btn_more_update:
			updateDiary();
			removeDialog(DIALOG_MORE);
			break;
		case R.id.btn_more_edit:
			if (!EffectsDownloadUtil.getInstance(this).checkEffects())
			{
				if(isDiaryDownload())
				{
					Intent edit = new Intent(this, DiaryEditPreviewActivity.class);
					String diary = new Gson().toJson(myDiary);
					edit.putExtra(INTENT_ACTION_DIARY_STRING, diary);
					startActivity(edit);
				}
				else
				{
					if(DiaryDownloader.isDownloading(myDiary))
					{
						new Xdialog.Builder(DiaryPreviewActivity.this)
						.setMessage("请等待下载完成")
						.setNegativeButton("确定", null)
						.create().show();
					}
					else
					{
						new Xdialog.Builder(DiaryPreviewActivity.this)
						.setMessage("本地无内容，请下载内容后再编辑")
						.setPositiveButton("下载", new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) {
								new DiaryDownloader(myDiary).excute();
							}
						})
						.setNegativeButton("取消", null)
						.create().show();
					}
				}
			}
			removeDialog(DIALOG_MORE);
			break;
		case R.id.btn_more_delete:
			deleteDiary();
			removeDialog(DIALOG_MORE);
			
			// 统计
			CmmobiClickAgentWrapper.onEvent(this, "delete_content");
			break;
		case R.id.ib_diarypreview_myvshare:
		case R.id.ib_diarypreview_othervshare:
		{
			//如果昵称或手机号未填写，则弹出选项，否则直接跳转个人信息页
			AccountInfo accountInfo = AccountInfo.getInstance(userID);
			if(accountInfo!=null){
//				LoginSettingManager lsm = accountInfo.setmanager;
//				MyBind mb=null;
//				if(lsm!=null)
//					mb= lsm.getBinding_type(LoginSettingManager.BINDING_TYPE_PHONE, "");
				if(/*mb!=null||*/!TextUtils.isEmpty(accountInfo.nickname)){
					shareToV();
				}else{
//					shareToV();
					startActivityForResult(new Intent(this,SettingPersonalInfoActivity.class), SETTING_PERSONAL_INFO);
//					startActivity(new Intent(this,SettingPersonalInfoActivity.class));
				}
			}else{
				Log.e(TAG, "account info exception!!!");
			}
			break;
		}
		case R.id.ib_diarypreview_friend:
//			if(myDiary.diaryid == null || myDiary.diaryid.equals(""))
//			{
//				new Xdialog.Builder(this)
//				.setMessage("请等待日记同步完成再重试！")
//				.setNegativeButton("确定", null)
//				.create().show();
//				break;
//			}
			
			
			if("2".equals(myDiary.publish_status))
			{
				// 统计
				// 2014-04-23;
				CmmobiClickAgentWrapper.onEvent(this, "content_public1", "2");
				
				new Xdialog.Builder(this)
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
				// 统计
				// 2014-04-23;
				CmmobiClickAgentWrapper.onEvent(this, "content_public1", "1");
				
				new Xdialog.Builder(this)
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
			break;
		case R.id.iv_praise:
			if(accountInfo!=null){
				LoginSettingManager lsm = accountInfo.setmanager;
				MyBind mb=null;
				if(lsm!=null)
					mb= lsm.getBinding_type(LoginSettingManager.BINDING_TYPE_PHONE, "");
				if(mb!=null||!TextUtils.isEmpty(accountInfo.nickname)){
				}else{
					startActivity(new Intent(this,SettingPersonalInfoActivity.class));
					break;
				}
			}else{
				Log.e(TAG, "account info exception!!!");
			}
			ZDialog.show(R.layout.progressdialog, false, true, this);
			if (!isMyself()) {
				// 统计
				CmmobiClickAgentWrapper.onEvent(this, "sh_thrumbup", myDiary.publishid);
				Log.d(TAG,"sh_thrumbup isMicroShare = true");
			}
			if (getPraiseStatus())
			{
				// 取消赞
				// 统计
				CmmobiClickAgentWrapper.onEvent(this, "cancel_thrumb_up", myDiary.publishid);
				Requester3.deleteEnjoy(handler, myDiary.diaryid, myDiary.publishid);
			}
			else if (!myDiary.isMicroShare)
			{
				if (isMyself()) {
					CmmobiClickAgentWrapper.onEvent(this, "content_thrumb_up", myDiary.publishid);
				}
				Requester3.enjoy(handler, myDiary.diaryid, myDiary.publishid);
				Log.d(TAG,"sh_thrumbup isMicroShare = flase");
			}
			break;
		case R.id.ib_title_myback:
		case R.id.ib_title_otherback:
			finish();
			break;
		case R.id.ib_title_more:
//			createDefalutDiary();
			removeDialog(DIALOG_MORE);
			showDialog(DIALOG_MORE);
			
			// 统计
			CmmobiClickAgentWrapper.onEvent(this, "ect_botton", myDiary.publishid);
			break;
		case R.id.btn_changan_left:
			if(v.getTag()!=null)
			{
				AudioPlayer.stop();
				stopAudioPlayAnimation();
				stopVideo();
				
				AuxAttach attach = (AuxAttach) v.getTag();
				removeVoiceDescription(attach.attachuuid);
				mChangan.dismiss();
			}
			break;
		case R.id.btn_changan_right:
			if(v.getTag()!=null)
			{
				AudioPlayer.stop();
				stopAudioPlayAnimation();
				stopVideo();
				
				AuxAttach attach = (AuxAttach) v.getTag();
				Intent modIntent = new Intent();
				modIntent.putExtra(INTENT_ACTION_DIARY_UUID, myDiary.diaryuuid);
				modIntent.putExtra(INTENT_ACTION_ATTACH_UUID, attach.attachuuid);
				modIntent.setClass(this, CreateDescriptionActivity.class);
				
				startActivity(modIntent);
				
				CmmobiClickAgentWrapper.onEvent(this, "edit_voice_descr", myDiary.diaryid);
				mChangan.dismiss();
			}
			break;
		case R.id.ib_title_back:
			finish();
			break;
		case R.id.ib_title_delete:
			Intent intent = new Intent();
			intent.putExtra(ShareDiaryActivity.BUNDLE_DELELE_UUID, myDiary.diaryuuid);
			intent.putExtra(ShareDiaryActivity.BUNDLE_DELELE_INDEX, mPageIndex);
			setResult(RESULT_OK, intent);
			finish();
			break;
		case R.id.iv_record:
			Intent desIntent = new Intent();
			desIntent.putExtra(INTENT_ACTION_DIARY_UUID, myDiary.diaryuuid);
			desIntent.setClass(this, CreateDescriptionActivity.class);
			
			startActivity(desIntent);
			break;
		case R.id.iv_comment:
			if(accountInfo!=null){
				LoginSettingManager lsm = accountInfo.setmanager;
				MyBind mb=null;
				if(lsm!=null)
					mb= lsm.getBinding_type(LoginSettingManager.BINDING_TYPE_PHONE, "");
				if(mb!=null||!TextUtils.isEmpty(accountInfo.nickname)){
					// 统计
					CmmobiClickAgentWrapper.onEvent(this, "content_comment");
					if(inpRecoderView.isShown()){
//						hideCommentInputView();
						hideInputView();
					} else {
						showCommentInputView();
					}
				}else{
					startActivity(new Intent(this,SettingPersonalInfoActivity.class));
					break;
				}
			}else{
				Log.e(TAG, "account info exception!!!");
			}
			
			break;
		case R.id.btn_comment_delete:  // dialog 评论删除
			
			if(dialogFragment!=null ){
				dialogFragment.dismiss();
			}
			
			mCurrDelCmm = (DiaryDetailComment) v.getTag();
			
			ZDialog.show(R.layout.progressdialog, true, true, this);
			Requester3.deleteComment(handler,mCurrDelCmm.publishid, mCurrDelCmm.commentid,mCurrDelCmm.commentuuid);
			
			// 统计
			CmmobiClickAgentWrapper.onEvent(this, "delete_comment", mCurrDelCmm.publishid);
			
			break;
		case R.id.btn_comment_reply: // dialog 评论回复
			// 统计
			CmmobiClickAgentWrapper.onEvent(this, "content_comment");
			
			if(dialogFragment!=null ){
				dialogFragment.getDialog().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
				dialogFragment.dismiss(); 
				mCurrentPullList.getRefreshableView().requestFocus();
			}
			
//			Requester3.comment(handler,mDelItem.publishid, mDelItem.commentid,mDelItem.commentuuid);
			inpRecoderView.clearView();
			// 清除直接回复
			mCurrCCmment = (DiaryDetailComment) v.getTag();
			
			if(accountInfo.friendsListName.findUserByUserid(mCurrCCmment.userid) == null)
			{
//				new XEditDialog.Builder(this)
//				.setTitle(R.string.xeditdialog_title)
//				.setPositiveButton(android.R.string.ok, new OnClickListener() {
//					
//					@Override
//					public void onClick(View v) {
//						//加好友
//						Requester3.addFriend(handler, mCurrCCmment.userid, v.getTag().toString());
//					}
//				})
//				.setNegativeButton(android.R.string.cancel, null)
//				.create().show();
				
				Prompt.Alert(this, "不可评论");
			}
			
			//设置recoder按钮状态
//			inpRecoderView.setRecordBtnEnabled(mCurrCCmment instanceof DiaryDetailComment);
			inpRecoderView.clearView();
			String name = "";
			if (TextUtils.isEmpty(mCurrCCmment.nickmarkname))
			{
				name = mCurrCCmment.nickname;
			}
			else
			{
				name = mCurrCCmment.nickmarkname;
			}
			inpRecoderView.setReplyName(name);
			inpRecoderView.setInputStrKey(InputStrType.COMMENT, mCurrCCmment.commentid);
			inpRecoderView.setVisibility(View.VISIBLE); 
			vMyBottombar.setVisibility(View.INVISIBLE);
			handler.postDelayed(new Runnable() {
				
				@Override
				public void run() {
					// TODO Auto-generated method stub
					inpRecoderView.showSoftKeyBoard();
				}
			}, 100);
			
			break;
		default:
			break;
		}
	}
	
	private void deleteDiary()
	{
		new Xdialog.Builder(this)
		.setTitle("删除日记")
		.setMessage("确定要删除选中日记吗？")
		.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				//判断是否是本地日记
//				int sync = getSync(myDiary.diaryuuid);
//				if (sync > 0 || sync == -1)
				MyDiaryList listGroup = findMyDiaryList(mListDuplicate.get(mPageIndex).groupUUID);
				if(!TextUtils.isEmpty(listGroup.diaryid))
				{
					OfflineTaskManager.getInstance().addDiaryRemoveTask(listGroup.diaryid);
					
					afterDeleteDiary(listGroup);
				}
				else
				{
					Prompt.Alert("日记删除成功");
					afterDeleteDiary(listGroup);
				}
			}
		})
		.setNegativeButton(android.R.string.cancel, null)
		.create().show();
	}
	
	private void showShootPromtDialog() {
		Log.d(TAG,"showShootPromtDialog in");
		if (isFullScreenMode) {
			return;
		}
		Log.d(TAG,"isMicroShare = " + myDiary.isMicroShare + " diary_source_type = " + myDiary.diary_source_type + " isShootPromt = " + CommonInfo.getInstance().isShootPromt);
		if (myDiary != null && myDiary.isVideoDiary() && !myDiary.isMicroShare && "4".equals(myDiary.diary_source_type) && !CommonInfo.getInstance().isShootPromt) {
			CommonInfo.getInstance().isShootPromt = true;
			new Xdialog.Builder(this)
			.setMessage("是否现在记录你的点滴")
			.setPositiveButton("是", new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					VideoShootActivity2.startOnShortShootMode(DiaryPreviewActivity.this, false);
				}
			})
			.setNegativeButton("否", null)
			.create().show();
		}
	}
	
	// 上传当前日记（可能新建任务）
	private void updateDiary()
	{
		//CmmobiClickAgentWrapper.onEvent(this, "upload_content");
		if (isMyself()) {
			INetworkTask t = NetworkTaskManager.getInstance(userID).findTaskByUUID(myDiary.diaryuuid);
			// 如果该任务期间已经被删除
			if (t == null)
			{
				// 日记上传
				if (!myDiary.isSychorized())
				{// 创建日记结构失败
					NetworkTaskInfo networktaskinfo = new NetworkTaskInfo(myDiary, INetworkTask.TASK_TYPE_CACHE);// 设置数据源
					t = new CacheNetworkTask(networktaskinfo);// 创建上传/下载任务
				}
				else
				{// 创建日记结构成功
					NetworkTaskInfo networktaskinfo = new NetworkTaskInfo(myDiary, INetworkTask.TASK_TYPE_UPLOAD);// 设置数据源
					t = new UploadNetworkTask(networktaskinfo);// 创建上传/下载任务
				}
				NetworkTaskManager.getInstance(userID).addTask(t);// 添加网络任务
				NetworkTaskManager.getInstance(userID).startNextTask();// 开始任务队列
				// 启动该任务
			}
			else
			{
				NetworkTaskManager.getInstance(userID).startTask(myDiary.diaryuuid);
			}
			// 开始任务队列
			Prompt.Alert("已加入上传队列");
		}
		else
		{
		}
	}
	
	// 播放视频
	private void playVideo(String url){
//		String videoCover = getVideoCover();
//		if (videoCover != null && videoCover.length() > 0) {
//			ivVideoThumbnail.setImageUrl(0, 1, videoCover, false);
//		}
		if(isPlayerNull() && mVideoContent != null)
		{
			
			mVideoContent.removeAllViews();
			RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(LayoutParams.FILL_PARENT,LayoutParams.FILL_PARENT);
			mVideoContent.addView(initPlayer(), params);
			
			Log.v(TAG, "xmediaplayer create");
		}
		if(!isPlayerNull() && url != null)
		{
			if (getStatus() == XEffectMediaPlayer.STATUS_UNKNOW && (ePlayStatus == EPlayStatus.NON || ePlayStatus == EPlayStatus.PROXY))
			{
				// 统计时长
				CmmobiClickAgentWrapper.onEventBegin(this, "play_time", myDiary.publishid);
				open(url);
				Log.v(TAG, "xmediaplayer open url = " + url + "isHsmFormat = " + isHsmFormat);
//				mBtnVideoPlay.setVisibility(View.INVISIBLE);
//				if(handler.hasMessages(HANDLER_AUTO_HIDE_CONTROLLER))
//				{
//					handler.removeMessages(HANDLER_AUTO_HIDE_CONTROLLER);
//				}
//				handler.sendEmptyMessageDelayed(HANDLER_AUTO_HIDE_CONTROLLER, 2000);
//				mBtnVideoPlay.setBackgroundResource(R.drawable.btn_diarypreview_video_pause);
				mBtnVideoPlay.setVisibility(View.INVISIBLE);
				ePlayStatus = EPlayStatus.OPENING;
				
			} else if ((!isHsmFormat && getStatus() == XEffectMediaPlayer.STATUS_PAUSE) 
					|| (isHsmFormat && getStatus() == XHsmVideoPlayer.STATUS_PAUSE)) {
				resume();
				Log.v(TAG, "xmediaplayer resume isHsmFormat = " + isHsmFormat);
//				mBtnVideoPlay.setVisibility(View.INVISIBLE);
//				if(handler.hasMessages(HANDLER_AUTO_HIDE_CONTROLLER))
//				{
//					handler.removeMessages(HANDLER_AUTO_HIDE_CONTROLLER);
//				}
//				handler.sendEmptyMessageDelayed(HANDLER_AUTO_HIDE_CONTROLLER, 2000);
//				mBtnVideoPlay.setBackgroundResource(R.drawable.btn_diarypreview_video_pause);
				mBtnVideoPlay.setVisibility(View.INVISIBLE);
				ePlayStatus = EPlayStatus.PLAY;
			}
			else if(getStatus() == XEffectMediaPlayer.STATUS_UNKNOW && ePlayStatus == EPlayStatus.PAUSE)
			{ //这种情况是UI控制了状态为暂停 但是实际由于surffaceview销毁导致播放已停止
			  //所以用ePlayStatus做标志，继续播放时从断点开始
				if(isHsmFormat && mVideoContent != null)
				{
					
					mVideoContent.removeAllViews();
					RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(LayoutParams.FILL_PARENT,LayoutParams.FILL_PARENT);
					mVideoContent.addView(initPlayer(), params);
					
					Log.v(TAG, "xmediaplayer create");
				}
				open(url);
				Log.v(TAG, "xmediaplayer restart");
//				mBtnVideoPlay.setVisibility(View.INVISIBLE);
//				if(handler.hasMessages(HANDLER_AUTO_HIDE_CONTROLLER))
//				{
//					handler.removeMessages(HANDLER_AUTO_HIDE_CONTROLLER);
//				}
//				handler.sendEmptyMessageDelayed(HANDLER_AUTO_HIDE_CONTROLLER, 2000);
//				mBtnVideoPlay.setBackgroundResource(R.drawable.btn_diarypreview_video_pause);
				mBtnVideoPlay.setVisibility(View.INVISIBLE);
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
		if(!isPlayerNull())
		{
			// 统计时长
			CmmobiClickAgentWrapper.onEventEnd(this, "play_time", myDiary.publishid);
			
			mIvVideoThumbnail.clearAnimation();
//			mIvVideoThumbnail.setVisibility(View.VISIBLE);
			stop();
			release();
			toNull();
			Log.v(TAG, "stopvideo xmediaplayer null");
			if(mBtnVideoPlay != null)
			{
//				if(handler.hasMessages(HANDLER_AUTO_HIDE_CONTROLLER))
//				{
//					handler.removeMessages(HANDLER_AUTO_HIDE_CONTROLLER);
//				}
				mBtnVideoPlay.setBackgroundResource(R.drawable.btn_diarypreview_video_play);
				mBtnVideoPlay.setVisibility(View.VISIBLE);
			}
		}else{
			Log.e(TAG, "mMediaPlayer is null isHsmFormat = " + isHsmFormat);
		}
		
		if (mPlayProcess != null)
		{
			mPlayProcess.setProgress(0);
		}
		
		// 标记归位
		if(mBtnVideoPlay != null)
		{
			mBtnVideoPlay.setBackgroundResource(R.drawable.btn_diarypreview_video_play);
			mBtnVideoPlay.setVisibility(View.VISIBLE);
		}
		
		if(mHttpProxy != null) {
			mHttpProxy.stopProxy();
//			mHttpProxy = null;
		}
		
		setPlayBtnStatus(true);
		
	}
	
	// 暂停视频
	private void pauseVideo(){
		if(!isPlayerNull()){
			
			if (isHsmFormat && getStatus() == XHsmVideoPlayer.STATUS_PALYING) {
				pause();
//				if(handler.hasMessages(HANDLER_AUTO_HIDE_CONTROLLER))
//				{
//					handler.removeMessages(HANDLER_AUTO_HIDE_CONTROLLER);
//				}
				mBtnVideoPlay.setBackgroundResource(R.drawable.btn_diarypreview_video_play);
				mBtnVideoPlay.setVisibility(View.VISIBLE);
				ePlayStatus = EPlayStatus.PAUSE;
				
			} else if (!isHsmFormat && getStatus() == XEffectMediaPlayer.STATUS_PALYING) {
				pause();
//				if(handler.hasMessages(HANDLER_AUTO_HIDE_CONTROLLER))
//				{
//					handler.removeMessages(HANDLER_AUTO_HIDE_CONTROLLER);
//				}
				mBtnVideoPlay.setBackgroundResource(R.drawable.btn_diarypreview_video_play);
				mBtnVideoPlay.setVisibility(View.VISIBLE);
				ePlayStatus = EPlayStatus.PAUSE;
				Log.v(TAG, "xmediaplayer pause");
			}
			
		}else{
			Log.e(TAG, "mMediaPlayer is null");
		}
	}
	
	// 判断当前日记是否是自己的日记
	private boolean isMyself() {
		if (myDiary != null) {
//			Log.d(TAG, "myDiary.diaryid="+myDiary.diaryid);
//			Log.d(TAG, "userID="+userID);
//			Log.d(TAG, "myDiary.userid="+myDiary.userid);
			if (userID != null && userID.equals(myDiary.userid))
				return true;
		}
		return false;
	}
	
	//更新一篇日记
	private void refreshData(String UUID){

		MyDiary diary = findMyDiaryByUUIDInManager(UUID);
		if (diary == null) {
			return;
		}
		for(int i = 0; i < mListDiary.size(); i++)
		{
			if (mListDiary.get(i).diaryuuid.equals(diary.diaryuuid))
			{
				mListDiary.set(i, diary);
				SortDiary sortDiary = mListDuplicate.get(i);
				sortDiary.diaryID = diary.diaryid;
				sortDiary.diaryUUID = diary.diaryuuid;
				sortDiary.groupUUID = diary.diaryuuid;
				sortDiary.name = getDiaryName(diary);
				mListDuplicate.set(i, sortDiary);
				
				myDiary = diary;
			}
		}
		
	}

	// 列表需要刷新
	private void insertNewDiary(String UUID)
	{
		MyDiaryList diaryList = mDiaryManager.findDiaryGroupByUUID(UUID);
		mListDiaryGroup.add(0, diaryList);
		
//		initDataWithUUID(UUID);

		MyDiary diary = findMyDiaryByUUIDInManager(UUID);
		Log.v(TAG, "diary.position_status = " + diary.position_status);
		mListDiary.add(0, diary);
		
		SortDiary sortDiary = new SortDiary();
		sortDiary.diaryID = diary.diaryid;
		sortDiary.diaryUUID = diary.diaryuuid;
		sortDiary.groupUUID = diary.diaryuuid;
		sortDiary.name = getDiaryName(diary);
		mListDuplicate.add(0, sortDiary);
		
		initUI();
		mPageIndex = 0;
		setCurrent();
		mDiaryPreview_viewpager.setCurrentItem(0, true);
	}
	
	// 重新初始化数据
//	private void initDataWithID(String diaryID) {
//		if (mListDiaryGroup != null && mListDiaryGroup.size() > 0) {
//			
//			// 根据日记副本做排序
//			sortByDiaryDuplicate();
//			
//			// 获取当前 mPageIndex
//			if(mListDuplicate.size() == 0)
//			{
//				SortDiary sortDiary = new SortDiary();
//				sortDiary.diaryID = diaryID;
//				sortDiary.name = "原日记";
//				mListDuplicate.add(sortDiary);
//				ZDialog.show(R.layout.del_progressdialog, false, true, this);
//				// 请求网络 设置日记高度和宽度
//				Requester2.getDiaryinfo(handler, diaryID, "", "");
//				return;
//			}
//			for (int i = 0; i < mListDuplicate.size(); i++)
//			{
//				if (diaryID != null && diaryID.equals(mListDuplicate.get(i).diaryID))
//				{
//					mPageIndex = i;
//					Log.d(TAG, "initData->pageIndex=" + mPageIndex);
//
//					break;
//				}
//			}
//			// 设置viewpager没页布局
//			initUI();
////			// 设置当前页显示
//			setCurrent();
//		}else{
//			SortDiary sortDiary = new SortDiary();
//			sortDiary.diaryID = diaryID;
//			sortDiary.name = "原日记";
//			mListDuplicate.add(sortDiary);
//			ZDialog.show(R.layout.del_progressdialog, false, true, this);
//			// 请求网络 设置日记高度和宽度
//			Requester2.getDiaryinfo(handler, diaryID, "", "");
//		}
//	}
	
	// 重新初始化数据
	private void initDataWithUUID(String diaryUUID) {
		long startTime = System.currentTimeMillis();
		if(diaryUUID == null)
		{
			Log.e(TAG, "diaryUUID is null");
			finish();
			return;
		}
		
		// 根据日记副本做排序
		sortByDiaryDuplicate();
		
		// 因为日记是从DiaryManager中取的 所以可能出现找不到diary的情况
		if(mListDuplicate.size() == 0)
		{
			Log.e(TAG, "0 diary find from diarymanager");
			finish();
			return;
		}
		
		// 获取当前 mPageIndex
		for (int i = 0; i < mListDuplicate.size(); i++)
		{
			if (diaryUUID != null && diaryUUID.equals(mListDuplicate.get(i).groupUUID))
			{
				mPageIndex = i;
				Log.d(TAG, "initData->pageIndex=" + mPageIndex);
				break;
			}
		}
		// 设置viewpager没页布局
		initUI();
		// 设置当前页显示
//		setCurrent();
		long end = System.currentTimeMillis();
		Log.d(TAG,"initDataWithUUID time = " + (end - startTime));
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
		case NONE:
			return "便签";
		default:
			return "便签";
		}
	}

	// 排序获得当前需要显示的view
	private void sortByDiaryDuplicate() {
		
		switch(mMode)
		{
		case SHOW_MODE_NOCACHE:
		case SHOW_MODE_NORMAL:
			mListDuplicate.clear();
			mListDiary.clear();
			
			for(int i = 0; i < mListDiaryGroup.size(); i++)
			{
				MyDiaryList myDiaryList = mListDiaryGroup.get(i);
				if (myDiaryList == null)
				{
					continue;
				}
				if("1".equals(myDiaryList.isgroup))
				{
					String[] list = myDiaryList.contain.split(",");
					for (int j = 0; j < list.length; j++)
					{
						MyDiary diary = findMyDiaryByIDInManager(list[j]);
						if(diary != null)
						{
							SortDiary sortDiary = new SortDiary();
							sortDiary.diaryID = diary.diaryid;
							sortDiary.diaryUUID = diary.diaryuuid;
							sortDiary.groupUUID = myDiaryList.diaryuuid;
							sortDiary.name = getDiaryName(diary);
							mListDuplicate.add(sortDiary);
							mListDiary.add(diary);
						}
					}
				}
				else
				{
					MyDiary diary = findMyDiaryByUUIDInManager(mListDiaryGroup.get(i).diaryuuid);
					if(diary != null)
					{
						SortDiary sortDiary = new SortDiary();
						sortDiary.diaryID = diary.diaryid;
						sortDiary.diaryUUID = diary.diaryuuid;
						sortDiary.groupUUID = diary.diaryuuid;
						sortDiary.name = getDiaryName(diary);
						mListDuplicate.add(sortDiary);
						mListDiary.add(diary);
					}
				}
			}
			Log.d(TAG, "diaryDuplicates.size()=" + mListDuplicate.size());
			break;
		case SHOW_MODE_SIMPLE:
		case SHOW_MODE_DELETE:
			mListDuplicate.clear();
			
			for(int i = 0; i < mListDiary.size(); i++)
			{
				MyDiary diary = mListDiary.get(i);
				if(diary != null)
				{
					SortDiary sortDiary = new SortDiary();
					sortDiary.diaryID = diary.diaryid;
					sortDiary.diaryUUID = diary.diaryuuid;
					sortDiary.groupUUID = diary.diaryuuid;
					sortDiary.name = getDiaryName(diary);
					mListDuplicate.add(sortDiary);
				}
			}
			Log.d(TAG, "diaryDuplicates.size()=" + mListDuplicate.size());
			break;
		}
		
	}

	// 根据mPageIndex初始化页面
	private void setCurrent()
	{
		isHasNextPage = true;
		Log.d(TAG, "setCurrent->pageIndex=" + mPageIndex);
		// 根据mPageIndex获取myDiary
		if (mPageIndex >= mListDuplicate.size())
		{
			// 避免数据的错误
			mPageIndex = mListDuplicate.size() - 1;
		}
		SortDiary sortDiary = mListDuplicate.get(mPageIndex);
		boolean isFounded = false;
		for (int i = 0; i < mListDiary.size(); i++)
		{
			if (sortDiary.diaryUUID != null)
			{
				if (sortDiary.diaryUUID.equals(mListDiary.get(i).diaryuuid))
				{
					myDiary = mListDiary.get(i);

					// 似乎需要重新取一次，myDiary不能更新
					// myDiary.sns = mDiaryManager.getSnsTrace(myDiary.diaryid);
					isFounded = true;
					break;
				}
			}
			else
			{
				if (sortDiary.diaryID.equals(mListDiary.get(i).diaryid))
				{
					myDiary = mListDiary.get(i);

					// 似乎需要重新取一次，myDiary不能更新
					// myDiary.sns = mDiaryManager.getSnsTrace(myDiary.diaryid);
					isFounded = true;
					break;
				}
			}
		}
		if (isFounded)
		{// 在diaryList中找到日记 切日记有效
			// 设置当前页中的顶部栏和底部栏UI
			if (mMode != SHOW_MODE_SIMPLE && mMode != SHOW_MODE_DELETE)
			{
				setBottom();
				
				// 设置标题
				try
				{
					SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd HH:mm");
					
					if(isGroup())
					{ // 组日记
						MyDiaryList diaryList = findMyDiaryList(mListDuplicate.get(mPageIndex).groupUUID);
						String [] strs = diaryList.contain.split(",");
//						int i;
//						for(i = 0; i < strs.length; i ++)
//						{
//							MyDiary diary = findMyDiaryByID(strs[i]);
//							if(diary != null && diary.diaryuuid.equals(myDiary.diaryuuid))
//							{
//								break;
//							}
//						}
						
						MyDiaryList listGroup = findMyDiaryList(mListDuplicate.get(mPageIndex).groupUUID);
						int i = 0;
						for(; i < mListDuplicate.size(); i++)
						{
							if(mListDuplicate.get(i).groupUUID.equals(listGroup.diaryuuid))
							{
								break;
							}
						}
						
						String str = sdf.format(new Date(Long.parseLong(myDiary.diarytimemilli)));
						str += "\r\n";
						str += mPageIndex - i + 1;
						str += "/";
						str += strs.length;
						tvMyDiaryName.setText(str);
						tvOtherDiaryName.setText(str);
					}
					else
					{// 单个日记
						if(!TextUtils.isEmpty(myDiary.shoottime) && getDiaryType(myDiary) == DiaryType.PICTURE)
						{
							tvMyDiaryName.setText(sdf.format(new Date(Long.parseLong(myDiary.shoottime))));
							tvOtherDiaryName.setText(sdf.format(new Date(Long.parseLong(myDiary.shoottime))));
						}
						else
						{
							tvMyDiaryName.setText(sdf.format(new Date(Long.parseLong(myDiary.diarytimemilli))));
							tvOtherDiaryName.setText(sdf.format(new Date(Long.parseLong(myDiary.diarytimemilli))));
						}
					}
					
	//				tvDiaryName.setText(DateUtils.dateToString(new Date(Long.parseLong(myDiary.updatetimemilli)),
	//						DateUtils.DATE_FORMAT_NORMAL));
				}
				catch (NumberFormatException e)
				{
					e.printStackTrace();
					tvMyDiaryName.setText(sortDiary.name);
					tvOtherDiaryName.setText(sortDiary.name);
				}
			}
			// 设置中间可滑动区域数据
			setDiaryData();
			
			if(!TextUtils.isEmpty(myDiary.diaryid) && !mListDuplicate.get(mPageIndex).isLoad && ZNetworkStateDetector.isConnected())
			{
				if(mTask.size() != 0)
				{
					mTask.offer(myDiary.publishid);
				}
				else
				{
					Requester3.getDiaryShareInfo(handler, myDiary.diaryid, myDiary.publishid, myDiary.userid);
				}
			}
			
		}
		else
		{// 未在diaryList中找到日记，请求网络
			ZDialog.show(R.layout.progressdialog, false, true, this);
			// 请求网络 设置日记高度和宽度
			Requester3.getDiaryinfo(handler, sortDiary.diaryID, "", "");
		}
	}
	
	//设置中间可滑动区域数据
	private void setDiaryData(){
		Log.d(TAG, "setDiaryData");
//		View v=((MyPageAdapter)mDiaryPreview_viewpager.getAdapter()).getView(mPageIndex);
		View v = ((SingleDiaryFragment)mDiarydetail_fragments.get(mPageIndex)).getContentView();
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
			case NONE:
				Log.e(TAG, "diaryType none");;
				break;
			default:
				break;
			}
			
		}else{
			Log.e(TAG, "setDiaryData view's tag is null");
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
	
	// 切换布局界面
	private void setFullLayout()
	{
		setPicCenter(mPageIndex + 1);
		setPicCenter(mPageIndex - 1);
		if (isFullScreenMode)
		{
			if(isMyself())
			{
				vMyTitlebar.setVisibility(View.INVISIBLE);
				vMyBottombar.setVisibility(View.INVISIBLE);
			}
			else
			{
				vOtherTitlebar.setVisibility(View.INVISIBLE);
//				vOtherBottombar.setVisibility(View.INVISIBLE);
			}
			vDiaryInfoTile.setVisibility(View.INVISIBLE);
			mTlParam.setVisibility(View.GONE);
			mDiaryPreview_viewpager.setBackgroundColor(Color.BLACK);
			mCurrentPullList.setMode(PullToRefreshBase.Mode.DISABLED);
			
			mBtnPraise.setVisibility(View.INVISIBLE);
			if(mTVVideoTime != null)
			{
//				mTVVideoTime.setVisibility(View.INVISIBLE);
			}
			
			
			hideList();
			
			
			int c = getWindowManager().getDefaultDisplay().getWidth();
			int h = getWindowManager().getDefaultDisplay().getHeight();
			
			int height = squareLayout.getHeight() > 0 ? squareLayout.getHeight() : c;
			
			int addHeight = (h - height) / 2 - vMyTitlebar.getHeight();
			Log.d(TAG,"addHeight = " + addHeight + " h = " + h + " height = " + height);
			if (addHeight > 0 && (myDiary == null || !myDiary.isPicDiary())) {
				LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT,addHeight);
				ivAdditionalView.setLayoutParams(params);
				ivAdditionalView.requestLayout();
			}
			
			if (myDiary != null && myDiary.isPicDiary()) {
				if(isMyself())
				{
					vMyTitlebar.setVisibility(View.GONE);
					vMyBottombar.setVisibility(View.GONE);
				}
				else
				{
					vOtherTitlebar.setVisibility(View.GONE);
				}
				mIvPicture.setForbidMovable(true);
				mCurrentPullList.getRefreshableView().setEnabled(false);
				mCurrentPullList.getRefreshableView().setVerticalScrollBarEnabled(false);
				ivVirtureTop.setVisibility(View.GONE);
				LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(c,h);
				mIvPicture.setLayoutParams(params);
				mIvPicture.requestLayout();
				mIvPicture.setMove(true);
				mIvPicture.setCenter(c,h);
				Log.d(TAG,"setFullLayout in if mIvPicture != null c = " + c + " h = " + h + " listHeight = " + mCurrentPullList.getHeight());
			}
			
			int t = h - addHeight - vMyTitlebar.getHeight() - height - DensityUtil.dip2px(this, 35);
			t = t > 0 ? t : 0;
			Log.d(TAG,"t = " + t + " height = " + height);
			
			
			if (myDiary == null || !myDiary.isPicDiary()) {
				RelativeLayout.LayoutParams rlparm = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, t);
				rlparm.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
				rlcover.setLayoutParams(rlparm);
			} else {
				RelativeLayout.LayoutParams rlparm = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, 0);
				rlparm.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
				rlcover.setLayoutParams(rlparm);
			}
		}
		else
		{
			if(isMyself())
			{
				vMyTitlebar.setVisibility(View.VISIBLE);
				vMyBottombar.setVisibility(View.VISIBLE);
			}
			else
			{
				vOtherTitlebar.setVisibility(View.VISIBLE);
//				vOtherBottombar.setVisibility(View.VISIBLE);
			}
			vDiaryInfoTile.setVisibility(View.VISIBLE);
			mTlParam.setVisibility(View.VISIBLE);
			mDiaryPreview_viewpager.setBackgroundColor(Color.WHITE);
			mCurrentPullList.setMode(PullToRefreshBase.Mode.BOTH);
			
			if(myDiary.isSychorized())
			{
				mBtnPraise.setVisibility(View.VISIBLE);
			}
			if(mTVVideoTime != null)
			{
				mTVVideoTime.setVisibility(View.VISIBLE);
			}
			
			if (myDiary != null && myDiary.isPicDiary()) {
				Log.d(TAG,"setFullLayout in else mIvPicture != null");
				mIvPicture.setForbidMovable(false);
				mCurrentPullList.getRefreshableView().setEnabled(true);
				mCurrentPullList.getRefreshableView().setVerticalScrollBarEnabled(true);
				ivVirtureTop.setVisibility(View.INVISIBLE);
				LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT,getWindowManager().getDefaultDisplay().getWidth());
				mIvPicture.setLayoutParams(params);
				mIvPicture.requestLayout();
				mIvPicture.setCenter(getWindowManager().getDefaultDisplay().getWidth(),getWindowManager().getDefaultDisplay().getWidth());
			}
			ShowList();
			
			RelativeLayout.LayoutParams rlparm = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, 0);
			rlcover.setLayoutParams(rlparm);
			LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT,0);
			ivAdditionalView.setLayoutParams(params);
			ivAdditionalView.requestLayout();
			
//			mIvPicture.setCenter(getWindowManager().getDefaultDisplay().getWidth(),
//					getWindowManager().getDefaultDisplay().getWidth());
			
		}
	}
	
	private void setPicCenter(int index) {
		if (index >= mListDuplicate.size() || index < 0) {
			return;
		} 
		SortDiary sortDiary = mListDuplicate.get(index);
		boolean isFounded = false;
		MyDiary diary = null;
		for (int i = 0; i < mListDiary.size(); i++) {
			if (sortDiary.diaryUUID != null) {
				if (sortDiary.diaryUUID.equals(mListDiary.get(i).diaryuuid)) {
					diary = mListDiary.get(i);
					isFounded = true;
					break;
				}
			} else {
				if (sortDiary.diaryID.equals(mListDiary.get(i).diaryid)) {
					diary = mListDiary.get(i);
					isFounded = true;
					break;
				}
			}
		}
		if (isFounded) {
			View v = ((SingleDiaryFragment)mDiarydetail_fragments.get(index)).getContentView();
			if (v == null) {
				return;
			}
			ImageView btnPraise = (ImageView) v.findViewById(R.id.iv_praise);
			ImageView additionalView = (ImageView) v.findViewById(R.id.iv_additional_space);
			if (diary != null && diary.isPicDiary()) {
				MultiPointTouchImageView ivPicture = (MultiPointTouchImageView) v.findViewById(R.id.iv_diarypreview_picture);
				if (isFullScreenMode) {
					hideList(index);
					btnPraise.setVisibility(View.INVISIBLE);
					int c = getWindowManager().getDefaultDisplay().getWidth();
					int h = getWindowManager().getDefaultDisplay().getHeight();
					
					ivPicture.setForbidMovable(true);
					v.findViewById(R.id.iv_virturl_top).setVisibility(View.GONE);
					LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(c,h);
					ivPicture.setLayoutParams(params);
					ivPicture.requestLayout();
					ivPicture.setMove(true);
					Log.d(TAG,"setPicCenter setCenter in before c = " + c + " h = " + h);
					ivPicture.setCenter(c,h);
					Log.d(TAG,"setPicCenter setCenter in after c = " + c + " h = " + h);
				} else {
					showList(index);
					if(myDiary.isSychorized()) {
						btnPraise.setVisibility(View.VISIBLE);
					}
					v.findViewById(R.id.iv_virturl_top).setVisibility(View.INVISIBLE);
					LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT,getWindowManager().getDefaultDisplay().getWidth());
					ivPicture.setLayoutParams(params);
					ivPicture.requestLayout();
					Log.d(TAG,"setPicCenter setCenter in before");
//					ivPicture.setScaleType(ScaleType.FIT_XY);
					ivPicture.setCenter(getWindowManager().getDefaultDisplay().getWidth(), getWindowManager().getDefaultDisplay().getWidth());
					Log.d(TAG,"setPicCenter setCenter in after");
				}
			} else {
				if (isFullScreenMode) {
					hideList(index);
					btnPraise.setVisibility(View.INVISIBLE);
					
					int c = getWindowManager().getDefaultDisplay().getWidth();
					int h = getWindowManager().getDefaultDisplay().getHeight();
					
					int height = v.findViewById(R.id.rl_diarypreview_content).getHeight() > 0 ? v.findViewById(R.id.rl_diarypreview_content).getHeight() : c;
					
					int addHeight = (h - height) / 2 - vMyTitlebar.getHeight();
					
					if (addHeight > 0 ) {
						LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT,addHeight);
						additionalView.setLayoutParams(params);
						additionalView.requestLayout();
					}
				} else {
					showList(index);
					if(myDiary.isSychorized()) {
						btnPraise.setVisibility(View.VISIBLE);
					}
					
					LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT,0);
					additionalView.setLayoutParams(params);
					additionalView.requestLayout();
				}
			}
		}
		
	}
	
	//设置视频布局数据
	private void loadVideoData(View v){
		mCurrentPullList = (PullToRefreshListView) v.findViewById(R.id.diarypreview_list);
		v.findViewById(R.id.ll_diarypreview_square).setOnClickListener(this);
		ivAdditionalView = (ImageView) v.findViewById(R.id.iv_additional_space);
		ivAdditionalView.setOnClickListener(this);
		ivVirtureTop = (ImageView) v.findViewById(R.id.iv_virturl_top);
		mLLDiaryBackgroud = (LinearLayout) v.findViewById(R.id.ll_diary_bg);
		mLLDiaryBackgroud.setOnClickListener(this);
		
		mVideoContent = (RelativeLayout) v.findViewById(R.id.rl_diarypreview_video_surface);
		
		squareLayout = (RelativeLayout) v.findViewById(R.id.rl_diarypreview_content);
		//文字描述
		mTVVideoTime = (TextView) v.findViewById(R.id.tv_diarypreview_video_time);
		String strTime = myDiary.getMainPlaytime();
		mTVVideoTime.setText("00:00 / " + DateUtils.getFormatTime0000(strTime));
		
		// 查找缓存是否有图片 没有使用缩略图
		imageLoader = ImageLoader.getInstance();

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
		String videoCover = getThumbUrl();
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
		
//		if(null == mMediaPlayer && mVideoContent != null)
//		{
//			mVideoContent.removeAllViews();
//			XEffects mEffects = null;
//			if (PluginUtils.isPluginMounted()) {
//				mEffects = new XEffects();
//			}
//			mMediaPlayer = new XMediaPlayer(this, mEffects, false);
//			mMediaPlayer.setListener(new VideoOnInfoListener());
//			RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(LayoutParams.FILL_PARENT,LayoutParams.FILL_PARENT);
//			mVideoContent.addView((View)mMediaPlayer.getXSurfaceView(), params);
//			
//			Log.v(TAG, "xmediaplayer create");
//		}
		mBtnVideoPlay = (ImageView) v.findViewById(R.id.iv_diarypreview_video_play);
		v.findViewById(R.id.rl_diarypreview_video_surface).setOnClickListener(this);
		v.findViewById(R.id.iv_diarydpreview_video_thumbnail).setOnClickListener(this);
		mBtnVideoPlay.setOnClickListener(this);
		String videoUrl = myDiary.getMainUrl();
		if(videoUrl == null)
		{
			videoUrl = myDiary.attachs.levelattach.attachuuid;
		}
//		videoUrl="http://1s.looklook.cn:8082/pub/looklook/video_pub/original/2013/08/07/154816b087fa8691c44d13b190e11bd6933c00.mp4";
		Log.d(TAG, "VideoUrl  = " + videoUrl);
		isHsmFormat = false;
		mBtnVideoPlay.setTag(videoUrl);
		
		mPlayProcess=(SeekBar) v.findViewById(R.id.sk_diarypreview_seek);
		mPlayProcess.setMax(100);
		mPlayProcess.setProgress(0);
		mPlayProcess.setOnSeekBarChangeListener(new OnSeekBarChangeListener()
		{
			private int startProgress;
			@Override
			public void onStopTrackingTouch(SeekBar seekBar)
			{
//				if(mMediaPlayer != null)
//				{
					if(seekBar.getSecondaryProgress() != 0
							&& seekBar.getProgress() > seekBar.getSecondaryProgress() - 10) {
						return;
					}
					
					double time = mTotlaTime * (seekBar.getProgress() / 100d);
//					double range = mHttpProxy.getTotalSize() * (seekBar.getProgress() / 100d);
					int delta = seekBar.getSecondaryProgress() - startProgress;
					Log.d(TAG, "Seek mTotlaTime = " + mTotlaTime + " delta " + delta + ";startProgress " + startProgress + ";getSecondaryProgress " + seekBar.getSecondaryProgress());
					if(mHttpProxy != null  && mHttpProxy.getStatus() == HttpProxy.STATE_WAITING
							&& seekBar.getSecondaryProgress() > 0
							&& delta < 12) { //仅仅为经验值
						Log.d(TAG, "seek to " + time);
						isCacheSeek = true;
						mMediaPlayer.stop();
						ePlayStatus = EPlayStatus.NON;
						mHttpProxy.resetProxy();
						mMediaPlayer.open(mHttpProxy.getCurLocalUrl());
						seekTime = time;
						Log.d(TAG, "mMediaPlayer reOpen url = " + mHttpProxy.getCurLocalUrl());
						return;
					}
					Log.d(TAG,"ePlayStatus = " + ePlayStatus);
					if(ePlayStatus == EPlayStatus.PLAY)
					{
						Log.d(TAG,"onStopTrackingTouch play time = " + time);
//						mMediaPlayer.seek(time);
						seek(time);
					}
					else if(ePlayStatus == EPlayStatus.PAUSE)
					{
						Log.d(TAG,"onStopTrackingTouch pause time = " + time);
						/*mMediaPlayer.resume();
						mMediaPlayer.seek(time);
						mMediaPlayer.pause();*/
						resume();
						seek(time);
						pause();
					}
//				}
			}
			
			@Override
			public void onStartTrackingTouch(SeekBar seekBar)
			{
				startProgress = seekBar.getProgress();
			}
			
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser)
			{
			}
		});
		
		mDiaryPreview_viewpager.setInterceptListener(new DiaryPagerTouchInterface()
		{

			@Override
			public void setIntercept(boolean intercept)
			{
			}

			@Override
			public boolean isIntercept()
			{
				return false;
			}

			@Override
			public boolean isForbidMove() {
				// TODO Auto-generated method stub
				return false;
			}

			@Override
			public void setForbidMovable(boolean movable) {
				// TODO Auto-generated method stub
				
			}
		});
		
		mBtnPraise = (ImageView) v.findViewById(R.id.iv_praise);
		if(myDiary.isSychorized())
		{
			setPraiseBtn();
		}
		else
		{
			mBtnPraise.setVisibility(View.GONE);
		}
		mBtnPraise.setOnClickListener(this);
		
		vDiaryInfoTile = v.findViewById(R.id.rl_diarypreview_info);
		tagsLayout = v.findViewById(R.id.ll_diarypreview_tag);
		tvTags[0] = (TextView) v.findViewById(R.id.tv_tag1);
		tvTags[1] = (TextView) v.findViewById(R.id.tv_tag2);
		tvTags[2] = (TextView) v.findViewById(R.id.tv_tag3);
		vPin = (ImageView) v.findViewById(R.id.iv_pin);
		tvPosition = (TextView) v.findViewById(R.id.tv_position);
		
		mTlParam = (LinearLayout) v.findViewById(R.id.ll_parm);
		setParm(mTlParam, myDiary);
		
		v.findViewById(R.id.shoot_again).setVisibility(View.GONE);
		
		setFullLayout();
		
		// 设置标签
		setTags();
		// 设置位置
		setPosition();
		
		setPosTagBackground();
	}
	
	// 根据日记结构体中的url获取实际播放url
	private String getVideoPath(String url)
	{
		if ((null == url || 0 == url.length()))
		{
			Log.e(TAG, "url is null");
			return null;
		}
		String uid = ActiveAccount.getInstance(MainApplication.getAppInstance()).getUID();
		MediaValue mv = AccountInfo.getInstance(uid).mediamapping.getMedia(uid, url);
		if (MediaValue.checkMediaAvailable(mv, 5))
		{
			return Environment.getExternalStorageDirectory() + mv.localpath;
		}
		return url;
	}
	
	//设置音频布局数据
	private void loadAudioData(View v){
		mCurrentPullList = (PullToRefreshListView) v.findViewById(R.id.diarypreview_list);
		v.findViewById(R.id.ll_diarypreview_square).setOnClickListener(this);
		ivVirtureTop = (ImageView) v.findViewById(R.id.iv_virturl_top);
		ivAdditionalView = (ImageView) v.findViewById(R.id.iv_additional_space);
		ivAdditionalView.setOnClickListener(this);
		mLLDiaryBackgroud = (LinearLayout) v.findViewById(R.id.ll_diary_bg);
		mLLDiaryBackgroud.setOnClickListener(this);
		
		mIvLeftWheel = (ImageView) v.findViewById(R.id.iv_diarypreview_audio_wheel_left);
		mIvRightWheel = (ImageView) v.findViewById(R.id.iv_diarypreview_audio_wheel_right);

		squareLayout = (RelativeLayout) v.findViewById(R.id.rl_diarypreview_content);
		//文字描述
		mTVAudioTime = (TextView) v.findViewById(R.id.tv_diarypreview_audio_time);
		String strTime = myDiary.getMainPlaytime();
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
		if(myDiary.isSychorized())
		{
			setPraiseBtn();
		}
		else
		{
			mBtnPraise.setVisibility(View.GONE);
		}
		mBtnPraise.setOnClickListener(this);
		
		vDiaryInfoTile = v.findViewById(R.id.rl_diarypreview_info);
		tagsLayout = v.findViewById(R.id.ll_diarypreview_tag);
		tvTags[0] = (TextView) v.findViewById(R.id.tv_tag1);
		tvTags[1] = (TextView) v.findViewById(R.id.tv_tag2);
		tvTags[2] = (TextView) v.findViewById(R.id.tv_tag3);
		vPin = (ImageView) v.findViewById(R.id.iv_pin);
		tvPosition = (TextView) v.findViewById(R.id.tv_position);
		
		mTlParam = (LinearLayout) v.findViewById(R.id.ll_parm);
		setParm(mTlParam, myDiary);
		
		setFullLayout();
		
		// 设置标签
		setTags();
		// 设置位置
		setPosition();
		
		setPosTagBackground();
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
		mCurrentPullList = (PullToRefreshListView) v.findViewById(R.id.diarypreview_list);
		v.findViewById(R.id.ll_diarypreview_square).setOnClickListener(this);
		mLLDiaryBackgroud = (LinearLayout) v.findViewById(R.id.ll_diary_bg);
		mLLDiaryBackgroud.setOnClickListener(this);
		ivAdditionalView = (ImageView) v.findViewById(R.id.iv_additional_space);
		ivAdditionalView.setOnClickListener(this);
		ivVirtureTop = (ImageView) v.findViewById(R.id.iv_virturl_top);
		mIvPicture = (MultiPointTouchImageView) v.findViewById(R.id.iv_diarypreview_picture);
		
		squareLayout = (RelativeLayout) v.findViewById(R.id.rl_diarypreview_content);
		// 查找缓存是否有图片 没有使用缩略图
		imageLoader = ImageLoader.getInstance();
		//imageLoader.init(ImageLoaderConfiguration.createDefault(DiaryPreviewActivity.this));
		

		imageLoaderOptions = new DisplayImageOptions.Builder()
//				.showStubImage(R.drawable.bg_default) // 图片不居中
//				.showImageForEmptyUri(R.drawable.bg_default)
//				.showImageOnFail(R.drawable.bg_default)
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
				int width = getWindowManager().getDefaultDisplay().getWidth();
				int height = getWindowManager().getDefaultDisplay().getHeight();
				int h = width;
				if (isFullScreenMode) {
					h = height;
				}
				mIvPicture.setCenter(width,h);
				mIvPicture.setBackgroundResource(R.color.black); //去掉背景
				Log.d(TAG,"onLoadingComplete in setCenter");
			}
		};
		// 日记结构创建成功，url为2014_xxxx开头的，刚好这个时候还没收到广播，但是附件又上传成功了。这种情况2014_xxx开头的映射被删除。找不到映射，这时需要重新获取日记结构。
		if (mMode == SHOW_MODE_NORMAL && !myDiary.getMainUrl().startsWith("http://") && myDiary.getMainPath() == null) {
			String UUID = myDiary.diaryuuid;
			if(UUID != null)
			{
				refreshData(UUID);
			}
			Log.d(TAG,"onClick SHOW_MODE_NORMAL");
		}
		
		String url = getPicturePath(myDiary.getMainUrl());
		
		if(url == null)
		{
			url = myDiary.attachs.levelattach.attachuuid;
		}
		Log.d(TAG, "use imageUrl=" + url);
		imageLoader.displayImageEx(url, mIvPicture, imageLoaderOptions, imageLoadingListener,
				ActiveAccount.getInstance(this).getUID(), 1);
		
		// 多点触摸时 屏蔽上层事件
		mDiaryPreview_viewpager.setInterceptListener(mIvPicture);
//		DiaryDetailScroll scroll = (DiaryDetailScroll) v.findViewById(R.id.diarypreview_scrollview);
		mCurrentPullList.setInterceptListener(mIvPicture);
		mIvPicture.setOnClickListener(new DiaryTouchInterface()
		{
			
			@Override
			public void onClick()
			{
				Log.d(TAG,"onClick in");
				handler.sendEmptyMessageDelayed(HANDLER_PICTURE_DOUBLE_CLICK, 250);
			}

			@Override
			public void onDoubleClick()
			{
				Log.d(TAG,"onDoubleClick in");
				if(handler.hasMessages(HANDLER_PICTURE_DOUBLE_CLICK))
				{
					handler.removeMessages(HANDLER_PICTURE_DOUBLE_CLICK);
				}
				
			}
		});
		
		if(mMode != SHOW_MODE_SIMPLE && mMode != SHOW_MODE_DELETE && !isFullScreenMode)
		{
			mIvPicture.setMove(false);
		}
		else
		{
			mIvPicture.setMove(true);
		}
		
		mBtnPraise = (ImageView) v.findViewById(R.id.iv_praise);
		if(myDiary.isSychorized())
		{
			setPraiseBtn();
		}
		else
		{
			mBtnPraise.setVisibility(View.GONE);
		}
		mBtnPraise.setOnClickListener(this);
		
		vDiaryInfoTile = v.findViewById(R.id.rl_diarypreview_info);
		tagsLayout = v.findViewById(R.id.ll_diarypreview_tag);
		tvTags[0] = (TextView) v.findViewById(R.id.tv_tag1);
		tvTags[1] = (TextView) v.findViewById(R.id.tv_tag2);
		tvTags[2] = (TextView) v.findViewById(R.id.tv_tag3);
		vPin = (ImageView) v.findViewById(R.id.iv_pin);
		tvPosition = (TextView) v.findViewById(R.id.tv_position);
		
		mTlParam = (LinearLayout) v.findViewById(R.id.ll_parm);
		setParm(mTlParam, myDiary);
		
		if (isFullScreenMode) {
			mIvPicture.setMove(true);
		} else {
			mIvPicture.setMove(false);
		}
		setFullLayout();
		
		// 设置标签
		setTags();
		// 设置位置
		setPosition();
		
		setPosTagBackground();
	}
	
	// 根据日记结构体中的url获取实际播放url
	private String getPicturePath(String imageUrl)
	{
		Log.v(TAG, "imageUrl =" + imageUrl);
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
//			url = "file://" + Environment.getExternalStorageDirectory() + mv.localpath;
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
//					url = "file://" + Environment.getExternalStorageDirectory() + mv.localpath;
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
		mCurrentPullList = (PullToRefreshListView) v.findViewById(R.id.diarypreview_list);
		// 利用rl保证text最小高度为正方形
//		RelativeLayout rl = (RelativeLayout) v.findViewById(R.id.rl_diarypreview_text_bg);
//		DisplayMetrics dm = this.getResources().getDisplayMetrics();
//		int screenWidth = dm.widthPixels;
//		rl.setLayoutParams(new RelativeLayout.LayoutParams(screenWidth, screenWidth));
		
		v.findViewById(R.id.ll_diarypreview_square).setOnClickListener(this);
		mLLDiaryBackgroud = (LinearLayout) v.findViewById(R.id.ll_diary_bg);
		mLLDiaryBackgroud.setOnClickListener(this);
		ivVirtureTop = (ImageView) v.findViewById(R.id.iv_virturl_top);
		ivAdditionalView = (ImageView) v.findViewById(R.id.iv_additional_space);
		ivAdditionalView.setOnClickListener(this);
		squareLayout = (RelativeLayout) v.findViewById(R.id.rl_diarypreview_content);
		
		v.findViewById(R.id.diarypreview_tack_right_bottom).setVisibility(View.INVISIBLE);
		v.findViewById(R.id.diarypreview_tack_center).setVisibility(View.INVISIBLE);
		
		TextView tv = (TextView) v.findViewById(R.id.tv_diarypreview_text_content);
		String content = myDiary.getMainTextContent();
		if(content == null || content.equals(""))
		{//无文字情况
			tvMainTack = (TackView) v.findViewById(R.id.diarypreview_tack_center);
//			tvMainTack.setSoundIcons(SOUND_ICONS_BIG, false);
		}
		else
		{//有文字情况
			FriendsExpressionView.replacedExpressions(content, tv);
			tvMainTack = (TackView) v.findViewById(R.id.diarypreview_tack_right_bottom);
//			tvMainTack.setSoundIcons(SOUND_ICONS_SMALL, true);
		}
		
		// 此处可能出现只有文字，但是url不为空；加上类型判断
		String url = myDiary.getMainUrl();
		if (url != null && url.length() > 0 
				&& !"4".equals(myDiary.attachs.levelattach.attachtype))
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

//		ScrollView outer = (ScrollView) v.findViewById(R.id.sl_outer);
		InnerScrollView inner = (InnerScrollView) v.findViewById(R.id.sl_inner);
		inner.setOnClickListener(new DiaryTouchInterface()
		{
			
			@Override
			public void onClick()
			{
				if(mMode != SHOW_MODE_SIMPLE && mMode != SHOW_MODE_DELETE)
				{
					if (isFullScreenMode)
					{
						isFullScreenMode = false;
						
					}
					else
					{
						isFullScreenMode = true;
					}
					setFullLayout();
					Log.d(TAG,"InnerScrollView onclick in");
				}
			}

			@Override
			public void onDoubleClick()
			{
				
			}
		});
		
		inner.parentScrollView = mCurrentPullList;
		
		mBtnPraise = (ImageView) v.findViewById(R.id.iv_praise);
		if(myDiary.isSychorized())
		{
			setPraiseBtn();
		}
		else
		{
			mBtnPraise.setVisibility(View.GONE);
		}
		mBtnPraise.setOnClickListener(this);
		
		vDiaryInfoTile = v.findViewById(R.id.rl_diarypreview_info);
		tagsLayout = v.findViewById(R.id.ll_diarypreview_tag);
		tvTags[0] = (TextView) v.findViewById(R.id.tv_tag1);
		tvTags[1] = (TextView) v.findViewById(R.id.tv_tag2);
		tvTags[2] = (TextView) v.findViewById(R.id.tv_tag3);
		vPin = (ImageView) v.findViewById(R.id.iv_pin);
		tvPosition = (TextView) v.findViewById(R.id.tv_position);
		
		mTlParam = (LinearLayout) v.findViewById(R.id.ll_parm);
		setParm(mTlParam, myDiary);
		
		setFullLayout();
		
		// 设置标签
		setTags();
		// 设置位置
		setPosition();
		
		setPosTagBackground();
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
	
	// 按日记时间排序
	public class AttachComparator implements Comparator<AuxAttach> {
        public int compare(AuxAttach arg0, AuxAttach arg1) {
        	
        	long l0 = 0;
        	long l1 = 0;
        	try
			{
				l0 = Long.parseLong(arg0.attachtimemilli);
			}
			catch (NumberFormatException e)
			{
				e.printStackTrace();
			}
        	try
			{
        		l1 = Long.parseLong(arg1.attachtimemilli);
			}
			catch (NumberFormatException e)
			{
				e.printStackTrace();
			}
        	
			if (l0 < l1)
			{
				return 1;
			}
			if (l0 == l1)
			{
				return 0;
			}
			return -1;
        }
    }
	
	// 设置附件
	private void setParm(LinearLayout ll, MyDiary diary)
	{
		Log.d(TAG,"setParm in");
		ll.removeAllViews();
		View v = inflater.inflate(R.layout.view_diarypreview_describe, ll, true);
		
		mBtnComment = (ImageView) v.findViewById(R.id.iv_comment);
		mBtnComment.setOnClickListener(this);

		setCommentBtn();
		
		if(diary.attachs.attach != null && diary.attachs.attach.length > 0)
		{
			mBtnRecord = (ImageView) v.findViewById(R.id.iv_record);
			mBtnRecord.setVisibility(View.INVISIBLE);
			
			if(isMyself())
			{
				v.setOnLongClickListener(new OnLongClickListener()
				{
					
					@Override
					public boolean onLongClick(View v)
					{
						View changan = inflater.inflate(R.layout.popmenu_miaoshu, null);
						Button del = (Button) changan.findViewById(R.id.btn_changan_left);
						del.setOnClickListener(DiaryPreviewActivity.this);
						del.setTag(myDiary.attachs.attach[0]);
						Button mod = (Button) changan.findViewById(R.id.btn_changan_right);
						mod.setOnClickListener(DiaryPreviewActivity.this);
						mod.setTag(myDiary.attachs.attach[0]);
						mChangan = new PopupWindow(changan, LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, true);
						mChangan.setBackgroundDrawable(new BitmapDrawable());
						mChangan.setFocusable(true);
						mChangan.setTouchable(true);
						mChangan.setOutsideTouchable(true);
						
						int[] location = new int[2];
						v.getLocationInWindow(location);
						
						int w = View.MeasureSpec.makeMeasureSpec(0,View.MeasureSpec.UNSPECIFIED);  
				    	int h = View.MeasureSpec.makeMeasureSpec(0,View.MeasureSpec.UNSPECIFIED);  
				    	changan.measure(w, h);  
				    	int popWidth = changan.getMeasuredWidth();
				    	int popHeight = changan.getMeasuredHeight();
				    	
				    	int vw = View.MeasureSpec.makeMeasureSpec(0,View.MeasureSpec.UNSPECIFIED);  
				    	int vh = View.MeasureSpec.makeMeasureSpec(0,View.MeasureSpec.UNSPECIFIED);
				    	v.measure(vw, vh);
				    	int viewWidth = v.getMeasuredWidth();
				    	
				    	mChangan.showAtLocation(v, 0, location[0] + 25, location[1] - popHeight + 5);
						return false;
					}
				});
			}
			
			final TackView tack = (TackView) v.findViewById(R.id.diarypreview_param_tack);
			tack.setVisibility(View.VISIBLE);
			int[] icons = { R.drawable.wave1,
					R.drawable.wave2, R.drawable.wave3, };
			tack.setSoundIcons(icons);
			
			if(diary.attachs.attach[0].playtime != null)
			{
				int time = 0;
				try
				{
					time = Integer.parseInt(diary.attachs.attach[0].playtime);
					Log.d(TAG,"setParm in time = " + time);
					if(time != 0 && time <= 30)
					{
						tack.setPlaytime(DateUtils.getPlayTime(diary.attachs.attach[0].playtime), false);
	//					float level = ((time - 1) / 10 + 1) / 3f;
	////				int end = dip2px(this, 110);
	//					tack.setBackgroudLevel(level, 110);
						
					}
				}
				catch (NumberFormatException e)
				{
					tack.setVisibility(View.GONE);
					e.printStackTrace();
				}
			}
			SimpleDateFormat sDateFormat = new SimpleDateFormat("MM"+getResources().getString(R.string.month)
					+"dd"+getResources().getString(R.string.day)
					+" HH:mm");
			try
			{
				((TextView) v.findViewById(R.id.tv_param_time)).setText(sDateFormat.format(new Date(Long.parseLong(diary.attachs.attach[0].attachtimemilli))));
				v.findViewById(R.id.tv_param_time).setVisibility(View.VISIBLE);
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
			FriendsExpressionView.replacedExpressions(diary.attachs.attach[0].content, (TextView) v.findViewById(R.id.tv_diary_param_text));
			v.findViewById(R.id.tv_diary_param_text).setVisibility(View.VISIBLE);
			tack.setTag(diary.attachs.attach[0].attachurl);
			tack.setOnClickListener(this);
			
			if(isMyself())
			{
				tack.setOnLongClickListener(new OnLongClickListener()
				{
					
					@Override
					public boolean onLongClick(View v)
					{
						View changan = inflater.inflate(R.layout.popmenu_miaoshu, null);
						Button del = (Button) changan.findViewById(R.id.btn_changan_left);
						del.setOnClickListener(DiaryPreviewActivity.this);
						del.setTag(myDiary.attachs.attach[0]);
						Button mod = (Button) changan.findViewById(R.id.btn_changan_right);
						mod.setOnClickListener(DiaryPreviewActivity.this);
						mod.setTag(myDiary.attachs.attach[0]);
						mChangan = new PopupWindow(changan, LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, true);
						mChangan.setBackgroundDrawable(new BitmapDrawable());
						mChangan.setFocusable(true);
						mChangan.setTouchable(true);
						mChangan.setOutsideTouchable(true);
						
						int[] location = new int[2];
						v.getLocationInWindow(location);
						
						int w = View.MeasureSpec.makeMeasureSpec(0,View.MeasureSpec.UNSPECIFIED);  
				    	int h = View.MeasureSpec.makeMeasureSpec(0,View.MeasureSpec.UNSPECIFIED);  
				    	changan.measure(w, h);  
				    	int popWidth = changan.getMeasuredWidth();
				    	int popHeight = changan.getMeasuredHeight();
				    	
				    	int vw = View.MeasureSpec.makeMeasureSpec(0,View.MeasureSpec.UNSPECIFIED);  
				    	int vh = View.MeasureSpec.makeMeasureSpec(0,View.MeasureSpec.UNSPECIFIED);
				    	v.measure(vw, vh);
				    	int viewWidth = v.getMeasuredWidth();
				    	
				    	mChangan.showAtLocation(v, 0, location[0] + 25, location[1] - popHeight + 5);
						return false;
					}
				});
			}
		}
		else
		{
			if(isMyself())
			{
				mBtnRecord = (ImageView) v.findViewById(R.id.iv_record);
				mBtnRecord.setVisibility(View.VISIBLE);
			
				mBtnRecord.setOnClickListener(this);
			}
			else
			{
				mBtnRecord = (ImageView) v.findViewById(R.id.iv_record);
				mBtnRecord.setVisibility(View.GONE);
			}
			
		}
	}
	
	// 刷新列表
	private void setList()
	{
		PullToRefreshListView v = (PullToRefreshListView) ((SingleDiaryFragment)mDiarydetail_fragments.get(mPageIndex)).getContentView();;
		
		ListView mListView = v.getRefreshableView();
		
		((DiaryCommentsAdapter)mListView.getTag()).notifyDataSetChanged();
		
	}
	
	// 隐藏列表
	private void hideList()
	{
		PullToRefreshListView v = (PullToRefreshListView) ((SingleDiaryFragment)mDiarydetail_fragments.get(mPageIndex)).getContentView();
		
		ListView mListView = v.getRefreshableView();
		
		((DiaryCommentsAdapter)mListView.getTag()).hideComment(true);
		
	}
	
	// 隐藏列表
	private void ShowList()
	{
		PullToRefreshListView v = (PullToRefreshListView) ((SingleDiaryFragment)mDiarydetail_fragments.get(mPageIndex)).getContentView();;
		
		ListView mListView = v.getRefreshableView();
		
		((DiaryCommentsAdapter)mListView.getTag()).hideComment(false);
		
	}
	
	private void hideList(int index) {
		PullToRefreshListView v = (PullToRefreshListView) ((SingleDiaryFragment)mDiarydetail_fragments.get(index)).getContentView();
		
		ListView mListView = v.getRefreshableView();
		
		((DiaryCommentsAdapter)mListView.getTag()).hideComment(true);
	}
	
	private void showList(int index) {
		PullToRefreshListView v = (PullToRefreshListView) ((SingleDiaryFragment)mDiarydetail_fragments.get(index)).getContentView();;
		
		ListView mListView = v.getRefreshableView();
		
		((DiaryCommentsAdapter)mListView.getTag()).hideComment(false);
	}
	
	// 当前评论处
	private void setCurrentRow()
	{
		PullToRefreshListView v = (PullToRefreshListView) ((SingleDiaryFragment)mDiarydetail_fragments.get(mPageIndex)).getContentView();;
		
		ListView mListView = v.getRefreshableView();
		
		SortDiary sortDiary = mListDuplicate.get(mPageIndex);
		for (int i = 0; i < mListDuplicate.get(mPageIndex).comments.size(); i ++)
		{
			if (mDefaultCommentId.equals(sortDiary.comments.get(i).commentid))
			{
				mListView.setSelection(i + 2);
				mCurrCCmment = sortDiary.comments.get(i);
				inpRecoderView.clearView();
				String name = "";
				if (TextUtils.isEmpty(sortDiary.comments.get(i).nickmarkname))
				{
					name = sortDiary.comments.get(i).nickname;
				}
				else
				{
					name = sortDiary.comments.get(i).nickmarkname;
				}
				inpRecoderView.setReplyName(name);
				inpRecoderView.setInputStrKey(InputStrType.COMMENT,mCurrCCmment.commentid);
				inpRecoderView.setVisibility(View.VISIBLE);
				vMyBottombar.setVisibility(View.INVISIBLE);
				inpRecoderView.showSoftKeyBoard();
				/*handler.postDelayed(new Runnable() {
					
					@Override
					public void run() {
						// TODO Auto-generated method stub
						inpRecoderView.showSoftKeyBoard();
					}
				}, 100);*/
			}
		}
		
	}
	
	//设置位置
	private void setPosition(){
		Log.d(TAG, "setPosition");
		if(myDiary.position_status != null && myDiary.position_status.equals("1"))
		{
			if(myDiary.position_view!=null&&myDiary.position_view.length()>0&&!myDiary.position_view.equals("不显示位置")){
				vPin.setVisibility(View.VISIBLE);
				tvPosition.setVisibility(View.VISIBLE);
				tvPosition.setText(myDiary.position_view);
			}else{
				vPin.setVisibility(View.INVISIBLE);
				tvPosition.setVisibility(View.INVISIBLE);
			}
		}
		else
		{
			vPin.setVisibility(View.INVISIBLE);
			tvPosition.setVisibility(View.INVISIBLE);
		}
	}
	
	private void setPosTagBackground() {
		if (tvPosition.getVisibility() == View.VISIBLE || tagsLayout.getVisibility() == View.VISIBLE) {
			vDiaryInfoTile.setBackgroundResource(R.drawable.position_tag_background);
		} else {
			vDiaryInfoTile.setBackgroundResource(0);
		}
	}
	
	private void setPraiseBtn()
	{
		if (mBtnPraise == null) {
			return;
		}
		if(!myDiary.isSychorized())
		{
			mBtnPraise.setVisibility(View.GONE);
		}
		else
		{
			mBtnPraise.setVisibility(View.VISIBLE);
		}
		if(getPraiseStatus())
		{
			mBtnPraise.setBackgroundResource(R.drawable.zan_2);
		}
		else
		{
			mBtnPraise.setBackgroundResource(R.drawable.zan_1);
		}
		if (isFullScreenMode) {
			mBtnPraise.setVisibility(View.INVISIBLE);
		}
	}
	
	private void setCommentBtn()
	{
		if(myDiary.isSychorized())
		{
			mBtnComment.setVisibility(View.VISIBLE);
		}
		else
		{
			mBtnComment.setVisibility(View.GONE);
		}
	}
	
	class SingleDiaryFragment extends Fragment {
		private SortDiary sortDiary;
		private View contentView;
		private int index = 0;
		public  SingleDiaryFragment(SortDiary sortDiary,int index) {
			this.sortDiary = sortDiary;
			this.index = index;
		}
		
		@Override
		public void onCreate(Bundle savedInstanceState) {
			// TODO Auto-generated method stub
			super.onCreate(savedInstanceState);
		}
		
		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			// TODO Auto-generated method stub
			Log.d(TAG,"onCreateView in index = " + index + " mPageIndex = " + mPageIndex);
			View view = initDiaryViews(inflater,sortDiary);
			contentView = view;
			if (index == mPageIndex) {
				setCurrent();
			} else {
				setPicCenter(index);
			}
			return view;
		}
		
		public View getContentView() {
			return contentView;
		}
	}
	
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		// TODO Auto-generated method stub
	}
	
	private void initUI() {
		mDiarydetail_fragments.clear();
		long startTime = System.currentTimeMillis();
		for (int i = 0; i < mListDuplicate.size(); i++) {
			final SortDiary sortDiary = mListDuplicate.get(i);
//			initDiaryViews(inflater,sortDiary);
			SingleDiaryFragment fragment = new SingleDiaryFragment(sortDiary,i);
			mDiarydetail_fragments.add(fragment);
		}
		long endTime = System.currentTimeMillis();
		Log.d(TAG,"initUI out  time = " + (endTime - startTime));
		// 加入到viewpager中
		mDiaryPreview_viewpager.setAdapter(new MyFragmentPageAdapter(mDiarydetail_fragments,getSupportFragmentManager()));
		// 设置当前显示的页数 pageIndex
		mDiaryPreview_viewpager.setCurrentItem(mPageIndex);

	}
	
	Rect commentViewRect = null;
	
	private View initDiaryViews(LayoutInflater inflater,final SortDiary sortDiary) {
		MyDiary diary = null;
		DiaryType diaryType = DiaryType.NONE;
		for (int j = 0; j < mListDiary.size(); j++) {
			diary = mListDiary.get(j);
			if(sortDiary.diaryUUID != null)
			{
				if (sortDiary.diaryUUID.equals(diary.diaryuuid)) {
					break;
				}
			}
			else if(sortDiary.diaryID != null)
			{
				if (sortDiary.diaryID.equals(diary.diaryid)) {
					break;
				}
			}
		}
		long startTime0 = System.currentTimeMillis();
		if (diary != null) {
			diaryType = getDiaryType(diary);
		}
		View diaryLayout=null;
		
		PullToRefreshListView mPullListView;
		ListView mListView;
		
		mPullListView = (PullToRefreshListView) inflater.inflate(R.layout.view_diarypreview_list, null, false);
		mPullListView.setShowIndicator(false);
		mPullListView.setOnRefreshListener(this);
		
		
		switch (diaryType) {
		case VEDIO://主体视频
			Log.d(TAG, "主体为视频");
			diaryLayout = inflater.inflate(
					R.layout.view_diarypreview_video_, null);
			mPullListView.setTag(DiaryType.VEDIO);
			break;
		case AUDIO://主体音频
			Log.d(TAG, "主体为音频");
			diaryLayout = inflater.inflate(
					R.layout.view_diarypreview_audio_, null);
			mPullListView.setTag(DiaryType.AUDIO);
			break;
		case PICTURE://主体图片
			Log.d(TAG, "主体为图片");
			diaryLayout = inflater.inflate(
					R.layout.view_diarypreview_picture_, null);
			mPullListView.setTag(DiaryType.PICTURE);
			break;
		case TEXT://主体文字
			Log.d(TAG, "主体为文字");
			diaryLayout = inflater.inflate(
					R.layout.view_diarypreview_text_, null);
			mPullListView.setTag(DiaryType.TEXT);
			break;
		default:
			diaryLayout = new View(this);
			break;
		}
		
		mListView = mPullListView.getRefreshableView();
		mListView.setOnTouchListener(new OnTouchListener()
		{
			float f = 0;
			@Override
			public boolean onTouch(View v, MotionEvent event)
			{
				if(mMode != SHOW_MODE_SIMPLE && mMode != SHOW_MODE_DELETE)
				{
					switch (event.getAction())
					{
					case MotionEvent.ACTION_DOWN:
						f = event.getY();
						Log.d(TAG,"ACTION_DOWN");
						break;
					case MotionEvent.ACTION_UP:
						f = 0f;
						Log.d(TAG,"ACTION_UP");
						break;
					case MotionEvent.ACTION_MOVE:
						Log.d(TAG,"ACTION_MOVE + event.getY() = " + event.getY());
						
						if (event.getY() < f - 20)
						{
							if(isMyself() && mIvPicture == null) // 图片模式底部一栏始终隐藏
							{
								Log.d(TAG,"ACTION_MOVE vMyBottombar invisible");
								vMyBottombar.setVisibility(View.INVISIBLE);
							}
						}
						else if (event.getY() > f + 20)
						{
							if(isMyself() && !isFullScreenMode)
							{
								Log.d(TAG,"ACTION_MOVE vMyBottombar visible");
								vMyBottombar.setVisibility(View.VISIBLE);
							}
						}
						f = event.getY();
						break;
					}
				}
				return false;
			}
		});
		mListView.addHeaderView(diaryLayout);
		// 底部补一条白色
		View foot = inflater.inflate(R.layout.include_diarypreview_mybottom, null);
		foot.setVisibility(View.INVISIBLE);
		mListView.addFooterView(foot);
//		mListView.setDivider(getResources().getDrawable(R.drawable.bg_diarydetail_list_line));
		DiaryCommentsAdapter adapter = new DiaryCommentsAdapter(this, sortDiary.enjoyHeads, sortDiary.comments, diary);
		mListView.setAdapter(adapter);
		mListView.setTag(adapter);
		mListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				
				if (arg2 > 2 && arg2 < sortDiary.comments.size() + 3)
				{
					if(isMyself())
					{
						if(userID.equals(sortDiary.comments.get(arg2 - 3).userid))
						{
							// 仅删除
//							mCurrDelCmm = sortDiary.comments.get(arg2 - 3);
							dialogFragment = CommentDialogFragment.newInstance(DiaryPreviewActivity.this,sortDiary.comments.get(arg2 - 3),false,true);
							dialogFragment.show(DiaryPreviewActivity.this.getSupportFragmentManager(), "dialog");
						}
						else
						{
							// 删除+回复
//							mCurrCCmment = sortDiary.comments.get(arg2 - 3);
//							mCurrDelCmm = sortDiary.comments.get(arg2 - 3);
							commentViewRect = new Rect();
							arg1.getGlobalVisibleRect(commentViewRect);
							Log.d(TAG,"commentViewRect = " + commentViewRect);
							dialogFragment = CommentDialogFragment.newInstance(DiaryPreviewActivity.this,sortDiary.comments.get(arg2 - 3),true,true);
							dialogFragment.show(DiaryPreviewActivity.this.getSupportFragmentManager(), "dialog");
						}
					}
					else
					{
						if (userID.equals(sortDiary.comments.get(arg2 - 3).userid))
						{
							// 删除
//							mCurrDelCmm = sortDiary.comments.get(arg2 - 3);
							dialogFragment = CommentDialogFragment.newInstance(DiaryPreviewActivity.this,sortDiary.comments.get(arg2 - 3),false,true);
							dialogFragment.show(DiaryPreviewActivity.this.getSupportFragmentManager(), "dialog");
						}
						else
						{
							// 直接回复
							commentViewRect = new Rect();
							arg1.getGlobalVisibleRect(commentViewRect);
							
							mCurrCCmment = sortDiary.comments.get(arg2 - 3);
							inpRecoderView.clearView();
							String name = "";
							if (TextUtils.isEmpty(sortDiary.comments.get(arg2 - 3).nickmarkname))
							{
								name = sortDiary.comments.get(arg2 - 3).nickname;
							}
							else
							{
								name = sortDiary.comments.get(arg2 - 3).nickmarkname;
							}
							inpRecoderView.setReplyName(name);
							inpRecoderView.setInputStrKey(InputStrType.COMMENT, mCurrCCmment.commentid);
							inpRecoderView.setVisibility(View.VISIBLE);
							vMyBottombar.setVisibility(View.INVISIBLE);
							inpRecoderView.showSoftKeyBoard();
						}
					}
				}
			}
		});
		
//		mPullListView.setMode(PullToRefreshBase.Mode.DISABLED);
		
//		mDiarydetail_views.add(mPullListView);
		long endTime = System.currentTimeMillis();
		Log.d(TAG,"initUI time = " + (endTime - startTime0));
		return mPullListView;
	}
	
	public enum DiaryType{
		AUDIO,
		VEDIO,
		PICTURE,
		TEXT,
		NONE
	}

	// 公开
	private void shareToPublish()
	{
//		String diaryids = "";
//		String diaryuuid = "";
//		String longitude = "";
//		String latitude = "";
//		String position = "";
//		String diary_type = "2"; // 朋友
//		
//		MyDiaryList diaryList = findMyDiaryList(mListDuplicate.get(mPageIndex).groupUUID);
//		
//		if(diaryList != null)
//		{//统计
//			CmmobiClickAgentWrapper.onEvent(this, "content_public", diaryList.publishid);
//		}
//		
//		if(isGroup())
//		{ // 组日记 一定都是上传了
//			if(diaryList != null)
//			{
//				diaryids = diaryList.diaryid;
//				diaryuuid = diaryList.diaryuuid;
//			}
//		}
//		else
//		{ // 单个日记
//			diaryids = myDiary.diaryid;
//			diaryuuid = myDiary.diaryuuid;
//			longitude = myDiary.longitude;
//			latitude = myDiary.latitude;
//			position = myDiary.position;
//			
//			// 上传任务中需要显示
//			INetworkTask t = NetworkTaskManager.getInstance(userID).findTaskByUUID(myDiary.diaryuuid);
//			if(t != null) {
//				t.info.isPublish = true;
//			}
//		}
		
//		Requester3.diaryPublish(handler, diaryids, diaryuuid, "", diary_type, "", longitude, latitude, position);
//		OfflineTaskManager.getInstance().addShareToLookLook(diaryids, diaryuuid, "", diary_type, "", longitude, latitude, position);
		
		if("2".equals(myDiary.publish_status))
		{
//			Requester3.setDiarySharePermissions(handler, myDiary.diaryid, "1");
			myDiary.publish_status = "1";
//			removeShareInfo(101, myDiary.diaryuuid , myDiary.publishid);
			OfflineTaskManager.getInstance().addSetDiarySharePermissionsTask(myDiary.diaryid, myDiary.diaryuuid, "1");
			
			// 统计
			CmmobiClickAgentWrapper.onEvent(this, "edit_state", "2");
		}
		else
		{
			myDiary.publish_status = "2";
//			Requester3.setDiarySharePermissions(handler, myDiary.diaryid, "2");
			OfflineTaskManager.getInstance().addSetDiarySharePermissionsTask(myDiary.diaryid, myDiary.diaryuuid, "2");
			
			// 统计
			CmmobiClickAgentWrapper.onEvent(this, "edit_state", "1");
		}
		DiaryManager.getInstance().notifyMyDiaryChanged();
		setBottom();
		
	}
	
	// 微享
	private void shareToV()
	{
		MyDiary[] diarys;
		MyDiaryList diaryList = null;
		
		diaryList = findMyDiaryList(mListDuplicate.get(mPageIndex).groupUUID);
		
		if(diaryList != null)
		{//统计
			CmmobiClickAgentWrapper.onEvent(this, "micro_share", diaryList.publishid);
		}
		
		if(isGroup())
		{ // 组日记 一定都是上传了
			String [] strs = diaryList.contain.split(",");
			ArrayList<MyDiary> arrayDiary = new ArrayList<MyDiary>();
			for(int i = 0; i < strs.length; i ++)
			{
				MyDiary diary = findMyDiaryByID(strs[i]);
				arrayDiary.add(diary);
			}
			diarys = arrayDiary.toArray(new MyDiary[arrayDiary.size()]);
			
		}
		else
		{ // 单个日记
			diarys = new MyDiary[1];
			diarys[0] = myDiary;
		}
		
		CmmobiClickAgentWrapper.onEvent(this, "vshare_button1");
		Intent intent = new Intent(this, ShareDiaryActivity.class);
		intent.putExtra(ShareDialog.TYPY_SHARE, ShareDialog.TYPE_SHARE_V);
		intent.putExtra(DiaryPreviewActivity.INTENT_ACTION_DIARYLIST_STRING, new Gson().toJson(diaryList));
		intent.putExtra(DiaryPreviewActivity.INTENT_ACTION_DIARY_STRING, new Gson().toJson(diarys));
		startActivity(intent);
		
	}
	
	// 第三方分享
	private void shareToSns()
	{
		MyDiary[] diarys;
		MyDiaryList diaryList = null;

		diaryList = findMyDiaryList(mListDuplicate.get(mPageIndex).groupUUID);
		
		if(isGroup())
		{ // 组日记 一定都是上传了
			String [] strs = diaryList.contain.split(",");
			ArrayList<MyDiary> arrayDiary = new ArrayList<MyDiary>();
			for(int i = 0; i < strs.length; i ++)
			{
				MyDiary diary = findMyDiaryByID(strs[i]);
				arrayDiary.add(diary);
			}
			diarys = arrayDiary.toArray(new MyDiary[arrayDiary.size()]);
			
		}
		else
		{ // 单个日记
			diarys = new MyDiary[1];
			diarys[0] = myDiary;
		}
		
		Intent intent = new Intent(this, ShareDialog.class);
		intent.putExtra(ShareDialog.TYPY_SHARE, ShareDialog.TYPE_SHARE_V);
		intent.putExtra(DiaryPreviewActivity.INTENT_ACTION_DIARYLIST_STRING, new Gson().toJson(diaryList));
		intent.putExtra(DiaryPreviewActivity.INTENT_ACTION_DIARY_STRING, new Gson().toJson(diarys));
		startActivity(intent);
		
	}
	
	// 根据duplicate里面的uuid查找原diarylist
	private MyDiaryList findMyDiaryList(String UUID)
	{
		for(MyDiaryList diaryList : mListDiaryGroup)
		{
			if(diaryList.diaryuuid != null && diaryList.diaryuuid.equals(UUID))
			{
				return diaryList;
			}
		}
		return null;
	}
	
	// 根据duplicate里面的id查找原diarylist
	private MyDiary findMyDiaryByID(String ID)
	{
		for(MyDiary diary : mListDiary)
		{
			if(diary.diaryid != null && diary.diaryid.equals(ID))
			{
				return diary;
			}
		}
		return null;
	}
	
	// 根据diarymanager里面的id查找原diarylist
	private MyDiary findMyDiaryByIDInManager(String ID)
	{
		if(mMode == SHOW_MODE_NOCACHE)
		{
			for(MyDiary diary : mListDiaryNoCach)
			{
				if(diary.diaryid != null && diary.diaryid.equals(ID))
				{
					return diary;
				}
			}
		}
		else
		{
			return mDiaryManager.findMyDiaryByDiaryID(ID);
		}
		
		return null;
	}
	
	// 根据diarymanager里面的id查找原diarylist
	private MyDiary findMyDiaryByUUIDInManager(String UUID)
	{
		if(mMode == SHOW_MODE_NOCACHE)
		{
			for(MyDiary diary : mListDiaryNoCach)
			{
				if(diary.diaryuuid != null && diary.diaryuuid.equals(UUID))
				{
					return diary;
				}
			}
		}
		else
		{
			return mDiaryManager.findMyDiaryByUUID(UUID);
		}
		return null;
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
//				if(ivPicture != null)
//				{
//					Log.v(TAG, "bitmap recycle");
//					ivPicture.recycle();
//				}
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
	
	public class MyFragmentPageAdapter extends PagerAdapter {
		private List<Fragment> mListFragments;
		private FragmentManager fragmentManager;

		public MyFragmentPageAdapter(List<Fragment> mListFragments,FragmentManager fragmentManager) {
			this.mListFragments = mListFragments;
			this.fragmentManager = fragmentManager;
		}
		
		public Fragment getView(int position){
			return mListFragments.get(position);
		}

		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			if(position<mListFragments.size())
			{
//				if(ivPicture != null)
//				{
//					Log.v(TAG, "bitmap recycle");
//					ivPicture.recycle();
//				}
				container.removeView(mListFragments.get(position).getView());
			}
		}

		@Override
		public Object instantiateItem(ViewGroup container, int position) {
			 Fragment fragment = mListFragments.get(position);
			 if(!fragment.isAdded()){ // 如果fragment还没有added
				 FragmentTransaction ft = fragmentManager.beginTransaction();
				 ft.add(fragment, fragment.getClass().getSimpleName());
				 ft.commitAllowingStateLoss();
				 /**
				 * 在用FragmentTransaction.commit()方法提交FragmentTransaction对象后
				 * 会在进程的主线程中，用异步的方式来执行。
				 * 如果想要立即执行这个等待中的操作，就要调用这个方法（只能在主线程中调用）。
				 * 要注意的是，所有的回调和相关的行为都会在这个调用中被执行完成，因此要仔细确认这个方法的调用位置。
				 */
				 fragmentManager.executePendingTransactions();
			 }
			 
			 if(fragment.getView().getParent() == null){
				 container.addView(fragment.getView()); // 为viewpager增加布局
			 }
			 
			 return fragment.getView();
		}

		@Override
		public int getCount() {
			return mListFragments.size();
		}

		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {
			return arg0 == arg1;
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
	
	//显示下面的滑动点点
	private void initImageView(int length) {
		mNumLayout.setVisibility(View.VISIBLE);
		if (length < 2)
			return;
		Bitmap bitmap = BitmapFactory.decodeResource(getResources(),
				R.drawable.home_icon1);
		if (mNumLayout != null)
			mNumLayout.removeAllViews();
		for (int i = 0; i < length; i++) {
			View view = new View(this);
			LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
					bitmap.getWidth(), bitmap.getHeight());
			params.setMargins(5, 10, 0, 10);
			view.setLayoutParams(params);
			if (i == mPageIndex) {
				view.setBackgroundResource(R.drawable.home_icon2);
			} else {
				view.setBackgroundResource(R.drawable.home_icon1);
			}
			mNumLayout.addView(view);
		}

	}

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		
		setIntent(intent);
        WeiboRequester.getInstance(this).handleIntent(this, intent, this);
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
//			Log.i(TAG, "[onUpdateTime] time:"+time);
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
			Log.d(TAG, "xMediaPlayer onPreparedPlayer");
			if(player.getStatus() == XEffectMediaPlayer.STATUS_OPENED)
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
	
	private BroadcastReceiver mReceiver = new BroadcastReceiver() {
		
		@Override
		public void onReceive(Context context, Intent intent) {
			String action=intent.getAction();
			Log.d(TAG, "myReceiver->action="+action);
			if(DIARY_EDIT_NEW.equals(action))
			{
				String UUID = intent.getStringExtra(INTENT_ACTION_DIARY_UUID);
				if(UUID != null)
				{
					insertNewDiary(UUID);
				}
			}
			else if(DIARY_EDIT_MOD.equals(action))
			{
				Log.d(TAG,"onReceive DIARY_EDIT_MOD in");
				String UUID = intent.getStringExtra(INTENT_ACTION_DIARY_UUID);
				if(UUID != null)
				{
					refreshData(UUID);
					setCurrent();
				}
			}
			else if(DIARY_EDIT_REFRESH.equals(action))
			{
				Log.d(TAG,"onReceive DIARY_EDIT_REFRESH in");
				String UUID = intent.getStringExtra(INTENT_ACTION_DIARY_UUID);
				if(UUID != null)
				{
					refreshData(UUID);
				}
				setBottom();
				setPraiseBtn();
				setCommentBtn();
			}
//			else if(INTENT_ACTION_SHOW.equals(action)){
//				if(isFullScreenMode)return;
//				isFullScreenMode=true;
//				setFullLayout();
//			}else if(INTENT_ACTION_HIDDEN.equals(action)){
//				if(!isFullScreenMode)return;
//				isFullScreenMode=false;
//				setFullLayout();
//			}else if(INTENT_ACTION_SCREEN_MODE.equals(action)){
//				if(isFullScreenMode){
//					isFullScreenMode=false;
//				}else{
//					isFullScreenMode=true;
//				}
//				setFullLayout();
//			}
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
	
	public int isDiaryEditable(MyDiary diary) {
//		if (diary != null) {
//			DiaryAttach[] attachs = myDiary.attachs;
//			if (attachs == null) {
//				return 0;
//			}
//			for (DiaryAttach attach:attachs) {
//				if ("1".equals(attach.attachtype)) {
//					String keyStr = null;
//					MediaValue mediaValue = null;
//					if (attach.attachvideo != null && attach.attachvideo.length > 0 ) {
//						for (MyAttachVideo videoAttach:attach.attachvideo) {
//							if (videoAttach.playvideourl != null && !"".equals(videoAttach.playvideourl)) {
//								keyStr = videoAttach.playvideourl;
//								
//								mediaValue = AccountInfo.getInstance(userID).mediamapping.getMedia(userID, keyStr);
//								if (mediaValue != null) {
//									String videoPath = HomeActivity.SDCARD_PATH + mediaValue.path;
//									if (PluginUtils.isPluginMounted()) {
//										XEffects effect = new XEffects();
//										XMediaPlayer mediaPlayer = new XMediaPlayer(this, effect, true);
//										boolean isEditable = mediaPlayer.isSupportOpen(videoPath);
//										mediaPlayer.release();
//										if (isEditable) {
//											return 0;
//										} else {
//											return -1;
//										}
//									} else {
//										return -1;
//									}
//								}
//							}
//						}
//					}
//				}
//			}
//		}
		return 0;
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
			mBtnAudioPlay.setImageResource(R.drawable.btn_diarypreview_audio_pause);
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
			mBtnAudioPlay.setImageResource(R.drawable.btn_diarypreview_audio_play);
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
			mBtnAudioPlay.setImageResource(R.drawable.btn_diarypreview_audio_play);
		}
	}
	
	// 删除日记后的操作
	private void afterDeleteDiary(MyDiaryList listGroup)
	{
		// 删除本地缓存日记
		ArrayList<MyDiaryList> tmpList = new ArrayList<MyDiaryList>();
		tmpList.add(listGroup);
		mDiaryManager.removeDiaryGroupByDiaryGroupList(tmpList);
		mDiaryManager.notifyMyDiaryChanged();
		
		if(mListDiaryGroup.size() == 1)
		{
//			Prompt.Alert("日记删除成功");
			finish();
			return;
		}
		
		// 删除当前日记
		int i = 0;
		for (; i < mListDiaryGroup.size(); i++)
		{
			if(listGroup.diaryuuid.equals(mListDiaryGroup.get(i).diaryuuid))
			{
				break;
			}
		}
		
		mListDiaryGroup.remove(i);
		
		// i = 0 删除的第一个
		if(i > 0)
		{
			listGroup = mListDiaryGroup.get(i-1);
		}
		else
		{
			listGroup = mListDiaryGroup.get(0);
		}
		
		sortByDiaryDuplicate();
		
		// 因为日记是从DiaryManager中取的 所以可能出现找不到diary的情况
		if(mListDuplicate.size() == 0)
		{
			Log.e(TAG, "0 diary find from diarymanager");
			finish();
			return;
		}
		
		String diaryUUID = listGroup.diaryuuid;
		// 获取当前 mPageIndex
		for (int j = 0; j < mListDuplicate.size(); j++)
		{
			if (diaryUUID != null && diaryUUID.equals(mListDuplicate.get(j).groupUUID))
			{
				mPageIndex = j;
				Log.d(TAG, "initData->pageIndex=" + mPageIndex);
				break;
			}
		}
		
		// 如果是组 往后移page 不能是删除的第一个
		if("1".equals(listGroup.isgroup) && i > 0)
		{
			String [] tmp = listGroup.contain.split(",");
			mPageIndex += tmp.length - 1;
		}
		
		// 设置viewpager没页布局
		initUI();
		// 设置当前页显示
		setCurrent();
		
//		Prompt.Alert("日记删除成功");
		Log.d(TAG, "pageIndex=" + mPageIndex);
		Log.d(TAG, "diaryDuplicates.size()=" + mListDuplicate.size());
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
	private boolean isDiaryDownload()
	{
		String imageUrl = myDiary.getMainPath();
		if (TextUtils.isEmpty(imageUrl) 
				&& !"4".equals(myDiary.attachs.levelattach.attachtype)
/*				&& !"5".equals(myDiary.attachs.levelattach.attachtype)
				&& !"6".equals(myDiary.attachs.levelattach.attachtype)*/)
		{
			return false;
		}
		else
		{
			File file = new File(imageUrl);
			return file.exists() && file.isFile();
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
	}
	
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
	
	//返回当前日记是否是一个组 如果是组 返回组id
	private boolean isGroup()
	{
		return !mListDuplicate.get(mPageIndex).groupUUID.equals(mListDuplicate.get(mPageIndex).diaryUUID);
	}
	
    @Override
	protected Dialog onCreateDialog(int id)
	{
    	switch(id)
    	{
    	case DIALOG_EDIT:
    	{
	    	final Dialog d = new Dialog(this, R.style.DiaryDetailDialog); 
	    	
	    	LayoutInflater inflater = LayoutInflater.from(this);
	    	View v = null;
	    	
			View tagView=((MyPageAdapter)mDiaryPreview_viewpager.getAdapter()).getView(mPageIndex);
			if(tagView.getTag()!=null){
				DiaryType type=(DiaryType) tagView.getTag();
				switch (type) {
				case VEDIO:
		    		v = inflater.inflate(R.layout.view_menu_diarypreview_video_edit, null);
		    		v.findViewById(R.id.btn_edit_video).setOnClickListener(this);
		    		v.findViewById(R.id.btn_edit_video_effect).setOnClickListener(this);
		    		v.findViewById(R.id.btn_edit_video_music).setOnClickListener(this);
					break;
				case AUDIO:
		    		v = inflater.inflate(R.layout.view_menu_diarypreview_audio_edit, null);
		    		v.findViewById(R.id.btn_edit_audio).setOnClickListener(this);
		    		v.findViewById(R.id.btn_edit_audio_music).setOnClickListener(this);
					break;
				case PICTURE:
					v = inflater.inflate(R.layout.view_menu_diarypreview_picture_edit, null);
		    		v.findViewById(R.id.btn_edit_picture).setOnClickListener(this);
		    		v.findViewById(R.id.btn_edit_picture_effect).setOnClickListener(this);
					break;
				case TEXT:
					v = inflater.inflate(R.layout.view_menu_diarypreview_text_edit, null);
		    		v.findViewById(R.id.btn_edit_text).setOnClickListener(this);
					break;
				default:
					break;
				}
			}
	    	
	    	LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT); 
	    	d.addContentView(v, params);
	    	
			Window window = d.getWindow();
			window.setGravity(Gravity.BOTTOM);
			WindowManager.LayoutParams p = window.getAttributes();
			p.width = LayoutParams.MATCH_PARENT; // 设置屏幕宽度
	//		p.alpha = 0.9f; // 增加一点按钮透明
			window.setAttributes(p);      //设置生效
			return d;
    	}
    	case DIALOG_MORE:
    	{
    		final Dialog d = new Dialog(this, R.style.DiaryDetailDialog); 
	    	
	    	LayoutInflater inflater = LayoutInflater.from(this);
	    	View v = null;
	    	
	    	
//	    	if (isGroup())
//	    	{
//				v = inflater.inflate(R.layout.view_menu_diarypreview_more1, null);
//				v.findViewById(R.id.btn_more_delete).setOnClickListener(this);
//	    	}
//	    	else if (isUpdate())
			{
				v = inflater.inflate(R.layout.view_menu_diarypreview_more2, null);
				v.findViewById(R.id.btn_more_edit).setOnClickListener(this);
				v.findViewById(R.id.btn_more_delete).setOnClickListener(this);
			}
//			else
//			{
//				v = inflater.inflate(R.layout.view_menu_diarypreview_more3, null);
//				v.findViewById(R.id.btn_more_update).setOnClickListener(this);
//				v.findViewById(R.id.btn_more_edit).setOnClickListener(this);
//				v.findViewById(R.id.btn_more_delete).setOnClickListener(this);
//			}
	    	
	    	Button btnCancel = (Button) v.findViewById(R.id.btn_more_cancel);
	    	btnCancel.setOnClickListener(new OnClickListener()
	    	{
	    		@Override
	    		public void onClick(View v)
	    		{
	    			removeDialog(DIALOG_MORE);
	    		}
	    	});
	    	
	    	LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT); 
	    	d.addContentView(v, params);
	    	
			Window window = d.getWindow();
			window.setGravity(Gravity.BOTTOM);
			WindowManager.LayoutParams p = window.getAttributes();
			p.width = LayoutParams.MATCH_PARENT; // 设置屏幕宽度
	//		p.alpha = 0.9f; // 增加一点按钮透明
			window.setAttributes(p);      //设置生效
			return d;
    	}
    	default:
    		return null;
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
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == FRENPOSITON && data != null) {
			
			String positionStr = data.getStringExtra("position");
			String longitude = data.getStringExtra("longitude");
			String latitude = data.getStringExtra("latitude");
			POIAddressInfo addInfo = new POIAddressInfo();
			addInfo.position = positionStr;
			addInfo.longitude = longitude;
			addInfo.latitude = latitude;
			Log.d(TAG,"freqPosList = " + positionStr);
			
			if (positionStr.equals(getString(R.string.position_not_visiable))) {
				return;
			}
			List<POIAddressInfo> freqPosList = CommonInfo.getInstance().frequentpos;
			
			if (freqPosList.contains(addInfo)) {
				Log.d(TAG,"onActivityResult if");
				freqPosList.remove(addInfo);
				freqPosList.add(0, addInfo);
			} else {
				Log.d(TAG,"onActivityResult else " + positionStr);
				freqPosList.add(0,addInfo);
			}
			finish();
		} else if (requestCode == SETTING_PERSONAL_INFO && resultCode == RESULT_OK) {
			if(!TextUtils.isEmpty(accountInfo.nickname)){
				shareToV();
			}
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	private boolean getStoreStatus()
	{
		MyDiaryList mdl = findMyDiaryList(mListDuplicate.get(mPageIndex).groupUUID);
		if(mdl == null || mdl.diaryid == null)
		{
			return false;
		}
		return DiaryManager.getInstance().isEnshrine(mdl.diaryid);
	}
	
	private boolean getPraiseStatus()
	{
//		MyDiaryList mdl = findMyDiaryList(mListDuplicate.get(mPageIndex).groupUUID);
//		if(mdl == null || mdl.diaryid == null)
//		{
//			return false;
//		}
		if(TextUtils.isEmpty(myDiary.publishid))
		{
			return false;
		}
		return DiaryManager.getInstance().isPraise(myDiary.diaryid, myDiary.publishid);
	}
	
	private void setBottom()
	{
		if (isMyself())
		{
//				vMyTitlebar.setVisibility(View.VISIBLE);
//				vMyBottombar.setVisibility(View.VISIBLE);
			vOtherTitlebar.setVisibility(View.INVISIBLE);
//			vOtherBottombar.setVisibility(View.INVISIBLE); 
		}
		else
		{
			vMyTitlebar.setVisibility(View.INVISIBLE);
//			vMyBottombar.setVisibility(View.INVISIBLE);
//				vOtherTitlebar.setVisibility(View.VISIBLE);
//				vOtherBottombar.setVisibility(View.VISIBLE);
		}
		
		
		if("2".equals(myDiary.publish_status))
		{
			mBtnFriend.setImageResource(R.drawable.btn_diarypreview_friend);
		}
		else
		{
			mBtnFriend.setImageResource(R.drawable.btn_diarypreview_private);
		}
			
	}
	
	
	
	private Runnable dialogRunnable = new Runnable() {
		
		@Override
		public void run() {
			if (ZDialog.getDialog() == null || !ZDialog.getDialog().isShowing()) {
				ZDialog.show(R.layout.hsm_player_buffering_progress_dialog, false, false, DiaryPreviewActivity.this, true);
				ZDialog.getDialog().setOnDismissListener(new OnDismissListener() {
					
					@Override
					public void onDismiss(DialogInterface dialog) {
						ZDialog.dismiss();
						Log.d(TAG,"onDismiss in");
					}
				});
				ZDialog.getDialog().setOnKeyListener(new OnKeyListener() {
					
					@Override
					public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
						if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_UP) {
							DiaryPreviewActivity.this.finish();
							return true;
						}
						return false;
					}
				});
				
			}
		}
	};
	
	private class HsmPlayerListener implements XHsmMediaPlayer.OnInfoListener {

		
		public void onLoadTimeout(XHsmMediaPlayer player) {
		}
		
		@Override
		public void onPrepareingPlayer(XHsmMediaPlayer player) {
//			ZLog.alert();
//			ZLog.e(player.getCurrentTime() + " / " + player.getTotalTime() + " = " + (player.getCurrentTime() / player.getTotalTime()));
//			ZLog.e(player.getBufferPercentage());
			Log.d(TAG,"Hsm onPrepareingPlayer in");
		}

		@Override
		public void onBufferingingPlayer(XHsmMediaPlayer player) {
//			ZLog.alert();
//			ZLog.e(player.getCurrentTime() + " / " + player.getTotalTime() + " = " + (player.getCurrentTime() / player.getTotalTime()));
//			ZLog.e(player.getBufferPercentage());
			Log.d(TAG,"Hsm onBufferingingPlayer percentage = " + player.getBufferPercentage());
			handler.postDelayed(dialogRunnable, 700);
		}

		@Override
		public void onPreparedPlayer(XHsmMediaPlayer player) {
			Log.d(TAG, "Hsm onPreparedPlayer over");
			ZLog.alert();
			ZLog.e();
			if(player.getStatus() == XEffectMediaPlayer.STATUS_OPENED)
			{
				mTotlaTime = player.getTotalTime()  / 1000.0;
				Log.d(TAG,"Hsm onPreparedPlayer mTotlaTime = " + mTotlaTime);
				handler.obtainMessage(HANDLER_UPDATE_VIDEO_PREPARED, true).sendToTarget();
			}
			else
			{
				handler.obtainMessage(HANDLER_UPDATE_VIDEO_PREPARED, false).sendToTarget();
			}
		}

		@Override
		public void onStartPlayer(XHsmMediaPlayer player) {
			ZLog.alert();
			ZLog.e();
			
			mTotlaTime = player.getTotalTime() / 1000.0;
			if (ePlayStatus == EPlayStatus.PAUSE) {
				pauseVideo();
			} else if (isMediaStop) {
				Log.d(TAG,"onStartPlayer isMediaStop seekPosition = " + seekPosition);
				seek(seekPosition / 1000.0);
				seekPosition = 0;
				isMediaStop = false;
			}
			/*mBtnVideoPlay.setVisibility(View.INVISIBLE);
			ePlayStatus = EPlayStatus.PLAY;*/
			Log.d(TAG,"Hsm onStartPlayer mTotlaTime = " + mTotlaTime);
			if (ZDialog.getDialog() != null) {
				if (ZDialog.getDialog().isShowing()) {
					ZDialog.dismiss();
				}
			}
			handler.removeCallbacks(dialogRunnable);
			Log.d(TAG,"Hsm onStartPlayer in removeCallbacks");
		}

		@Override
		public void onStopPlayer(XHsmMediaPlayer player) {
			ZLog.alert();
			ZLog.e();
			if (ZDialog.getDialog() != null) {
				if (ZDialog.getDialog().isShowing()) {
					ZDialog.dismiss();
				}
			}
			Log.d(TAG,"Hsm onStopPlayer in");
		}

		@Override
		public void onUpdateTime(XHsmMediaPlayer player, double time) {
//			ZLog.alert();
//			ZLog.e();
			Log.d(TAG,"onUpdateTime time = " + time);
			PlayTime pt = new PlayTime();
			pt.total= (int) player.getTotalTime();
			pt.current= (int) time;
			
			handler.obtainMessage(HANDLER_UPDATE_VIDEO_PLAYER_PROCESS, pt).sendToTarget();
		}

		@Override
		public void OnFinishPlayer(XHsmMediaPlayer player) {
			ZLog.alert();
			ZLog.e();
			handler.obtainMessage(HANDLER_UPDATE_VIDEO_PLAYER_COMPLETE).sendToTarget();
		}

		@Override
		public void onError(XHsmMediaPlayer player) {
			Log.d(TAG,"Hsm onError");
			ZLog.alert();
			ZLog.e();
			if (ZDialog.getDialog() != null) {
				if (ZDialog.getDialog().isShowing()) {
					ZDialog.dismiss();
				}
			}
			handler.obtainMessage(HANDLER_UPDATE_VIDEO_ERROR).sendToTarget();
			if(player != null){
				player.release();
			}
		}
		
	}
	
	//--- player actions --
	private boolean isHsmFormat;
	
	private boolean isHsmFormat(String uri) {
		isHsmFormat = uri.toLowerCase().endsWith(".hsm");
		return isHsmFormat;
	}
	
	private View initPlayer() {
		View view = null;
		if (isHsmFormat) {
			mHsmPlayer = new XHsmMediaPlayer(this);
			mHsmPlayer.setListener(new HsmPlayerListener());
			mHsmPlayer.setUpdateTimePeriod(0.2f);
			view = mHsmPlayer.getXSurfaceView();
			
		} else {
			XEffects mEffects = null;
			if (PluginUtils.isPluginMounted()) {
				mEffects = new XEffects();
			}
			mMediaPlayer = new XMediaPlayer(this, mEffects, false);
			mMediaPlayer.setListener(new VideoOnInfoListener());
			view = mMediaPlayer.getXSurfaceView();
		}
		
		return view;
	}
	
	private boolean isPlayerNull() {
		if (isHsmFormat) {
			return mHsmPlayer == null;
			
		} else {
			return mMediaPlayer == null;
		}
	}
	
	private int getStatus() {
		if (isHsmFormat) {
			ZLog.e("hsm");
			return mHsmPlayer.getStatus();
			
		} else {
			return mMediaPlayer.getStatus();
		}
	}
	
	private double getCurrentTime() {
		if (isHsmFormat) {
			return mHsmPlayer.getCurrentTime();
			
		} else {
			return mMediaPlayer.getCurrentTime();
		}
	}

	private void open(String uri) {
		if (isHsmFormat) {
			mHsmPlayer.open(uri);
			
		} else {
			mMediaPlayer.open(uri);
		}
	}
	
	private void play() {
		if (isHsmFormat) {
			Log.d(TAG,"mHsmPlayer play");
			try {
				mHsmPlayer.play();
			} catch (Exception e) {
				e.printStackTrace();
			}
			
		} else {
			Log.d(TAG,"mMediaPlayer play");
			mMediaPlayer.play();
		}
	}
	
	private void seek(double seek) {
		if (isHsmFormat) {
			mHsmPlayer.seek(seek);
			
		} else {
			mMediaPlayer.seek(seek);
		}
	}
	
	private void pause() {
		if (isHsmFormat) {
			mHsmPlayer.pause();
			
		} else {
			mMediaPlayer.pause();
		}
	}
	
	private void resume() {
		if (isHsmFormat) {
			mHsmPlayer.resume();
			
		} else {
			mMediaPlayer.resume();
		}
	}
	
	private void stop() {
		if (isHsmFormat) {
			mHsmPlayer.stop();
			
		} else {
			mMediaPlayer.stop();
		}
	}
	
	private void release() {
		if (isHsmFormat) {
			mHsmPlayer.release();
			
		} else {
			mMediaPlayer.release();
		}
	}
	
	private void toNull() {
		if (isHsmFormat) {
			mHsmPlayer = null;
			
		} else {
			mMediaPlayer = null;
		}
	}
	
	@Override
	public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView)
	{
		Log.d(TAG,"onPullDownToRefresh in");
		Requester3.getDiaryShareInfo(handler, myDiary.diaryid, myDiary.publishid, myDiary.userid);
	}
	
	@Override
	public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
		
		if(myDiary.isSychorized() && ZNetworkStateDetector.isConnected())
		{
			ZDialog.show(R.layout.progressdialog, false, true, this);
			hideCommentInputView();
			
			PullToRefreshListView v = (PullToRefreshListView) ((SingleDiaryFragment)mDiarydetail_fragments.get(mPageIndex)).getContentView();
//			v.setRefreshing();
			
			SortDiary sort = mListDuplicate.get(mPageIndex);
			if(isHasNextPage){
				if(v != null) {
					v.setNoMoreData(DiaryPreviewActivity.this, true);
				}
				Requester3.diaryCommentList(handler, sort.last_time, "2", myDiary.publishid);
			}else{
				if(v != null) {
					v.setNoMoreData(DiaryPreviewActivity.this, false);
				}
				handler.sendEmptyMessageDelayed(Requester3.RESPONSE_TYPE_DIARY_COMMENTLIST, 200);
			}
			
			Log.d(TAG,"diarypublishid = " + myDiary.publishid + " uuid = " + myDiary.diaryuuid + " id = " + myDiary.diaryid);
			
			/*handler.post(new Runnable() {
				@Override
				public void run() {
					PullToRefreshListView v = (PullToRefreshListView) ((SingleDiaryFragment)mDiarydetail_fragments.get(mPageIndex)).getContentView();
//					v.setRefreshing();
					
					SortDiary sort = mListDuplicate.get(mPageIndex);
					if(isHasNextPage){
						if(v != null) {
							v.setNoMoreData(DiaryPreviewActivity.this, true);
						}
						Requester3.diaryCommentList(handler, sort.last_time, "2", myDiary.publishid);
					}else{
						if(v != null) {
							v.setNoMoreData(DiaryPreviewActivity.this, false);
						}
						handler.sendEmptyMessageDelayed(Requester3.RESPONSE_TYPE_DIARY_COMMENTLIST, 200);
					}
					
					Log.d(TAG,"diarypublishid = " + myDiary.publishid + " uuid = " + myDiary.diaryuuid + " id = " + myDiary.diaryid);
				}
			});*/
		}
		else
		{
//			refreshView.onRefreshComplete();
			handler.sendEmptyMessageDelayed(Requester3.RESPONSE_TYPE_DIARY_COMMENTLIST, 200);
		}
	}

	@Override
	public void onSend(AudioRecoderBean bean)
	{
		mCurrSendBean = bean;
		// 直接回复
		if(mCurrCCmment == null/* && mMicinfo.mishareinfo != null*/){
			ZDialog.show(R.layout.progressdialog, true, true, this);
			Requester3.comment(handler, bean.content, "" , "1" , myDiary.publishid, bean.commenttype,bean.commentuuid, "", "1");
			// 统计
			HashMap<String, String> label = new HashMap<String, String>();
			label.put("label", myDiary.publishid);
			label.put("label2", "1".equals(bean.commenttype) ? "2" : "1");
			CmmobiClickAgentWrapper.onEvent(this, "con_com_success", label);
		}else if(mCurrCCmment != null){   // 评论回复
			ZDialog.show(R.layout.progressdialog, true, true, this);
			Requester3.comment(handler, bean.content,mCurrCCmment.commentid,"2",myDiary.publishid,bean.commenttype,bean.commentuuid, "", "1");
			// 统计
			// 统计
			HashMap<String, String> label = new HashMap<String, String>();
			label.put("label", myDiary.publishid);
			label.put("label2", "1".equals(bean.commenttype) ? "2" : "1");
			CmmobiClickAgentWrapper.onEvent(this, "content_reply", label);
		}else{
			ZLog.e("send error。。。。");
		}
	}
	
	/**
	 * 隐藏输入框时清楚缓存
	 * 
	 */
	public void cleanCmmData(){
		mCurrCCmment = null;
	}
	
	 //点击EditText以外的任何区域隐藏键盘
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {  
            View v = inpRecoderView;
            if (isShouldHideInput(v, ev)) {
                if(hideInputView()) {
                    return true; //隐藏键盘时，其他控件不响应点击事件==》注释则不拦截点击事件
                }
            }
        }
        return super.dispatchTouchEvent(ev);   
    }     
    
    public static boolean isShouldHideInput(View v, MotionEvent event) {
        if (v != null && (v instanceof InputRecoderView)) {
            int[] leftTop = { 0, 0 };
            v.getLocationInWindow(leftTop);
            int left = leftTop[0], top = leftTop[1], bottom = top + v.getHeight(), right = left
                    + v.getWidth();
//            if (event.getX() > left && event.getX() < right
//                    && event.getY() > top && event.getY() < bottom) {
                // 保留点击EditText的事件
        	if (event.getY() > top) {
                return false;
            } else {
                return true;
            }
        }
        return false;
    }    
    
    private Boolean hideInputView(){
    	if(inpRecoderView.getVisibility() == View.VISIBLE){
	        inpRecoderView.clearView();
			cleanCmmData();
			inpRecoderView.setVisibility(View.GONE);
			if(isMyself())
			{
				vMyBottombar.setVisibility(View.VISIBLE);
			}
	    	return true;
    	}
    	return false;
    }
    
	public void showCommentInputView(){
		// 添加评论操作点击事件请求处理
		inpRecoderView.clearView();
		cleanCmmData();
		inpRecoderView.setNoReply();
		inpRecoderView.setInputStrKey(InputStrType.COMMENT, myDiary.diaryid);
		inpRecoderView.setVisibility(View.VISIBLE);
		vMyBottombar.setVisibility(View.INVISIBLE);
		inpRecoderView.showSoftKeyBoard();
	}
	
	public void hideCommentInputView(){
		inpRecoderView.clearView();
		cleanCmmData();
	}
	
	private DialogFragment dialogFragment;
	
	public static class CommentDialogFragment extends DialogFragment {

		private static CommentDialogFragment dialog;
		private OnClickListener listener;
		private DiaryDetailComment ccmm;
		private boolean hasReply;
		private boolean replyShowDel;
		
		public static CommentDialogFragment newInstance(OnClickListener listener,DiaryDetailComment ccmm,boolean hasReply,boolean replyShowDel){
			if(dialog == null){
				dialog = new CommentDialogFragment();
			}
			dialog.listener = listener;
			dialog.hasReply = hasReply;
			dialog.ccmm = ccmm;
			dialog.replyShowDel = replyShowDel;
			return dialog;
		}
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
        	
        	final Dialog d = new Dialog(getActivity(), R.style.dialog_theme); 
        	
        	LayoutInflater inflater = LayoutInflater.from(getActivity());
        	
        	View v = null;
        	Button delete;
        	Button cancel;
        	Button reply;
        	
        	if(!hasReply){
        		v = inflater.inflate(R.layout.dialogfragment_comment_operate_two, null);
        		delete = (Button) v.findViewById(R.id.btn_comment_delete);
        		delete.setTag(ccmm);
        		cancel = (Button) v.findViewById(R.id.btn_comment_cancel);
        		delete.setOnClickListener(listener);
        		cancel.setOnClickListener(new OnClickListener(){
					@Override
					public void onClick(View v){
						d.dismiss();
					}
				});
        	} else {
        		v = inflater.inflate(R.layout.dialogfragment_comment_operate_three, null);
        		reply = (Button) v.findViewById(R.id.btn_comment_reply);
        		reply.setTag(ccmm);
        		reply.setOnClickListener(listener);
        		
        		delete = (Button) v.findViewById(R.id.btn_comment_delete);
        		delete.setTag(ccmm);
        		delete.setOnClickListener(listener);
        		
        		if(!replyShowDel){
        			delete.setVisibility(View.GONE);
        			reply.setBackgroundResource(R.drawable.btn_menu_one);
        		}
        		
        		cancel = (Button) v.findViewById(R.id.btn_comment_cancel);
        		cancel.setOnClickListener(new OnClickListener(){
					@Override
					public void onClick(View v){
						d.dismiss();
					}
				});
        	}        	
        	
        	LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT); 
        	d.addContentView(v, params);
        	
    		Window window = d.getWindow();
    		window.setGravity(Gravity.BOTTOM);
    		android.view.WindowManager.LayoutParams p = window.getAttributes();
			p.width = LayoutParams.MATCH_PARENT; // 设置屏幕宽度
//			p.alpha = 0.9f; // 增加一点按钮透明
			window.setAttributes(p);      //设置生效
    		return d;
        }
    }

}
