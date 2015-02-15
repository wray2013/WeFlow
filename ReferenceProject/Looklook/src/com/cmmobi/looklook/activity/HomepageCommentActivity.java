package com.cmmobi.looklook.activity;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.NetworkInfo.State;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.FrameLayout.LayoutParams;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import cn.zipper.framwork.core.ZActivity;
import cn.zipper.framwork.core.ZApplication;
import cn.zipper.framwork.core.ZBroadcastReceiver;
import cn.zipper.framwork.core.ZDialog;
import cn.zipper.framwork.core.ZToast;
import cn.zipper.framwork.io.network.ZNetworkStateDetector;

import com.cmmobi.looklook.R;
import com.cmmobi.looklook.common.constant.Constant;
import com.cmmobi.looklook.common.gson.GsonResponse2;
import com.cmmobi.looklook.common.gson.GsonResponse2.MyDiary;
import com.cmmobi.looklook.common.gson.Requester2;
import com.cmmobi.looklook.common.utils.CmmobiClickAgentWrapper;
import com.cmmobi.looklook.common.utils.DateUtils;
import com.cmmobi.looklook.common.utils.EffectsDownloadUtil;
import com.cmmobi.looklook.common.utils.UmengclickAgentWrapper;
import com.cmmobi.looklook.common.view.CountDownView;
import com.cmmobi.looklook.common.view.ExpressionView;
import com.cmmobi.looklook.common.view.Xdialog;
import com.cmmobi.looklook.common.web.TackView;
import com.cmmobi.looklook.common.web.WebImageView;
import com.cmmobi.looklook.info.profile.AccountInfo;
import com.cmmobi.looklook.info.profile.ActiveAccount;
import com.cmmobi.looklook.info.profile.DiaryManager;
import com.cmmobi.looklook.info.profile.MediaValue;
import com.cmmobi.looklook.info.profile.NetworkTaskInfo;
import com.cmmobi.looklook.info.profile.PrivateCommonMessage;
import com.cmmobi.looklook.info.profile.TimeHelper;
import com.cmmobi.looklook.networktask.INetworkTask;
import com.cmmobi.looklook.networktask.UploadNetworkTask;
import com.cmmobi.looklook.offlinetask.OfflineTaskManager;
import com.cmmobi.looklook.offlinetask.OfflineTaskManager.NetConnectedReceiver;
import com.cmmobi.looklook.prompt.Prompt;
import com.cmmobivideo.utils.PluginUtils;
import com.google.gson.Gson;
import com.iflytek.msc.ExtAudioRecorder;
import com.iflytek.msc.QISR_TASK;
import com.nostra13.universalimageloader.api.AnimateFirstDisplayListener;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageLoadingListener;
import com.nostra13.universalimageloader.core.display.CircularBitmapDisplayer;
import com.nostra13.universalimageloader.core.display.SimpleBitmapDisplayer;

import effect.XEffectJniUtils;

/**
 * @评论页面
 * @author jiayunan
 * 
 */
public class HomepageCommentActivity extends ZActivity implements
		OnItemClickListener, OnTouchListener {

	public static final String ACTION_DIARYID = "intent_action_diary_id";
	public static final int SEND_SOUND_RECORD_MSG = 0;

	public static final int STOP_SOUND_RECORD_MSG = 1;

	public static final int UPDATE_COUNT_30_SECONDS = 2;

	private static final String MAIN_ATTACH = "1";
	private static final String VICE_ATTACH = "0";
	private static final String VIDEO = "1";
	private static final String AUDIO = "2";
	private static final String PIC = "3";
	private static final String TEXT = "4";

	public static final int VOLUME_LOW = 25;
	public static final int VOLUME_NORMAL = 50;
	public static final int VOLUME_LARGE = 75;
	public static final int VOLUME_HUGE = 100;
	public static final int VOLUME_UNNORMAL = -1;
	public static final int OUTSIDE = 0x01;
	public static final int ABOVE = 0x02;

	public static final String TAG = "HomePageCommentActivity";

	public static final int[] PUBLIC_TYPE = { 1, 2, 3 };/*
														 * 评论类型：1->发表评论,
														 * 2->回复评论, 3->修改评论文字
														 */

	private int comment_type;
	private long recordDuration = 0;
	private String comment_id;
	private int comment_pos;

	private ImageView back;

	private ListView comment_list;

	private ImageView press_talk;

	private ImageView voice_input;

	private ImageView expression_input;

	private ExpressionView expressionView;

	private EditText input_et;

	private ImageView location_pic;

	private TextView location_name;

	private ImageView comment_finish;

	private ImageView cidai_icon;

	private boolean isTextInputMood = true;

	private boolean isOnRecorderButton = false;
	// 是否有音频
	private boolean haveaudio = false;

	// 我是否赞过此日记
	private boolean isPraise = false;

	// 是否第一次查询评论
	private boolean isFirstQuery = false;

	// 是否显示表情输入图标
	private boolean isShowExpress = false;

	private boolean isLongClick = false;

	private CommentListAdapter commentListAdapter;
	// private ZanListAdapter zAdapter;
	private GdfuListAdapter gdfuAdapter;

	public static Handler handler;

	private InputMethodManager imm;

	private Activity act;

	private ExtAudioRecorder ear;

	// private VolumeStateView vsv;

	private Message msg;

	private String userID;
	private String diaryUserID;
	private MyDiary myDiary;
	private AccountInfo ai;

	private String diaryID;
	private String diaryString;
	private String url;
	// private String diaryComment;
	String replyText;

	private ImageView touxiang;// 头像
	private TextView nick_name;// 昵称
	private TextView mood;// 心情描述
	private TackView moodVoicePlay;// 录音按钮
	private TextView publishTime;
	private ImageView zanIcon;
	private GridView zan_grid;
	private ImageView send;

	private PopupWindow portraitMenu;
	private RelativeLayout rootView;
	private LinearLayout ll_zan_List;

	private String sound_text = "";
	private String sound_path;

	private ImageView bq_delete;
	// private ImageView iv_bq_send;
	private ImageView mood_pic;
	private LinearLayout ll_send_expression;
	private String playtime;
	private String audioID;

	private String commentUserID;
	private String commentuuidTail = "a";

	private ArrayList<GsonResponse2.diarycommentlistItem> commentList;
	private ArrayList<GsonResponse2.getDiaryForwardUsers> gdfuList;
	// 倒计时30秒
	private CountDownView shortRecView;

	// 使用开源的webimageloader
	private DisplayImageOptions options, options1;
	protected ImageLoader imageLoader;
	private ImageLoadingListener animateFirstListener;

	private DiaryManager mDiaryManager;
	private Comparator ccr;
	private OfflineTaskManager otm;
	private boolean ifNotifyUI = false;
	
	private String tempTime;

	private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			int type;
			Message msg = new Message();

			// Get extra data included in the Intent
			if (QISR_TASK.QISR_RESULT_MSG.equals(intent.getAction())) {
				type = intent.getIntExtra("type", 0);
				String message = intent.getStringExtra("content");
				Log.d("receiver", "Got message: " + message);
				msg.what = type;
				msg.obj = message;
			} else if (ExtAudioRecorder.AUDIO_RECORDER_MSG.equals(intent
					.getAction())) {
				type = intent.getIntExtra("type", 0);
				String volume = intent.getStringExtra("content");
				String playtime = intent.getStringExtra("playtime");
				msg.what = type;
				msg.obj = volume;
				if (playtime != null) {
					Bundle b = new Bundle();
					b.putString("playtime", playtime);
					msg.setData(b);
				}
			}

			HomepageCommentActivity.this.getHandler().sendMessage(msg);
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_homepage_comment);

		userID = ActiveAccount.getInstance(ZApplication.getInstance()).getUID();
		ai = AccountInfo.getInstance(userID);

		mDiaryManager = DiaryManager.getInstance();

		diaryID = getIntent().getStringExtra(
				DiaryDetailActivity.INTENT_ACTION_DIARY_ID);

		diaryString = getIntent().getStringExtra(
				DiaryDetailActivity.INTENT_ACTION_DIARY_STRING);

		// diaryComment =
		// getIntent().getStringExtra(DiaryDetailActivity.INTENT_ACTION_COMMENT_STRING);

		// GsonResponse2 gr = new GsonResponse2();
		// myDiary = gr.new MyDiary();
		// 获取日记评论列表

		if (diaryString != null && diaryID != null) {
			myDiary = new Gson().fromJson(diaryString, MyDiary.class);
			Log.d(TAG, "diaryID=" + diaryID);
		}
		if (myDiary == null) {
			// diaryID = getIntent().getStringExtra(ACTION_DIARYID);
			Requester2.getDiaryinfo(getHandler(), diaryID, null, null);
		}
		// 获取赞列表
		// Requester2.getDiaryEnjoyUsers(getHandler(), diaryID, "22322");
		// 67. 获取日记转发人列表
		gdfuList = new ArrayList<GsonResponse2.getDiaryForwardUsers>();
		gdfuAdapter = new GdfuListAdapter(HomepageCommentActivity.this,
				gdfuList);
		Requester2.getDiaryForwardUsers(getHandler(), diaryID, null, null);
		// if(diaryComment != null){
		// diaryCommentList = new Gson().fromJson(diaryComment,
		// new TypeToken<LinkedList<diaryCommentListResponse>>() {
		// }.getType());
		// }
		back = (ImageView) findViewById(R.id.back);
		back.setOnClickListener(this);

		press_talk = (ImageView) findViewById(R.id.press_talk);
		handler = getHandler();

		bq_delete = (ImageView) findViewById(R.id.iv_bq_delete);
		bq_delete.setOnClickListener(this);

		// iv_bq_send = (ImageView) findViewById(R.id.iv_bq_send);
		// iv_bq_send.setOnClickListener(this);

		location_pic = (ImageView) findViewById(R.id.location_pic);
		location_name = (TextView) findViewById(R.id.location_name);
		zan_grid = (GridView) findViewById(R.id.gridview_zanlist);

		ear = ExtAudioRecorder.getInstanse(false, 2, 1);

		act = this;

		// vsv = new VolumeStateView(act);

		imm = ((InputMethodManager) act
				.getSystemService(Activity.INPUT_METHOD_SERVICE));

		input_et = (EditText) findViewById(R.id.et_shuru);
		/*
		 * et.setOnEditorActionListener(new OnEditorActionListener() {
		 * 
		 * @Override public boolean onEditorAction(TextView v, int actionId,
		 * KeyEvent event) { if (actionId == EditorInfo.IME_ACTION_SEND) {
		 * Requester2.comment(handler, et.getText().toString(), "10011", "0",
		 * diaryID, "1", null); } et.setText("");
		 * imm.hideSoftInputFromWindow(et.getWindowToken(),
		 * InputMethodManager.HIDE_NOT_ALWAYS); return false; } });
		 */
		Log.e("this--------------------->", this + "");
		Log.e("et--------------------->", input_et + "");
		expressionView = new ExpressionView(this, input_et);
		expressionView.setOnclickListener(this);

		animateFirstListener = new AnimateFirstDisplayListener();
		imageLoader = ImageLoader.getInstance();

		options = new DisplayImageOptions.Builder()
				.showStubImage(R.drawable.moren_touxiang)
				.showImageForEmptyUri(R.drawable.moren_touxiang)
				.showImageOnFail(R.drawable.moren_touxiang).cacheInMemory(true)
				.cacheOnDisc(true)
				// .displayer(new SimpleBitmapDisplayer())
				.displayer(new CircularBitmapDisplayer()) // 圆形图片
				// .displayer(new RoundedBitmapDisplayer(20)) 圆角图片
				.build();

		options1 = new DisplayImageOptions.Builder().showStubImage(0)
				.showImageForEmptyUri(0).showImageOnFail(0).cacheInMemory(true)
				.cacheOnDisc(true).displayer(new SimpleBitmapDisplayer())
				// .displayer(new CircularBitmapDisplayer()) // 圆形图片
				// .displayer(new RoundedBitmapDisplayer(20)) 圆角图片
				.build();

		voice_input = (ImageView) findViewById(R.id.jianpan);
		voice_input.setOnClickListener(this);

		expression_input = (ImageView) findViewById(R.id.expression_input);
		expression_input.setOnClickListener(this);

		comment_list = (ListView) findViewById(R.id.comment_content);

		if (!ZNetworkStateDetector.isConnected()) {
			ai.commentList.clear();
			commentList = ai.commentList;
		} else {
			commentList = new ArrayList<GsonResponse2.diarycommentlistItem>();
			ai.commentList.clear();
			Requester2.diaryCommentList(getHandler(), null, null, diaryID,
					userID);
			ZDialog.show(R.layout.progressdialog, true, true, this);

		}

		commentListAdapter = new CommentListAdapter(this, commentList);
		comment_list.setAdapter(commentListAdapter);

		if (ocl != null && comment_list != null) {
			comment_list.setOnItemClickListener(ocl);
		}
		press_talk.setOnTouchListener(this);

		LocalBroadcastManager.getInstance(this).registerReceiver(
				mMessageReceiver, new IntentFilter(QISR_TASK.QISR_RESULT_MSG));

		IntentFilter filter = new IntentFilter();
		filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
		filter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
		filter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
		LocalBroadcastManager.getInstance(this).registerReceiver(
				new NetConnectedReceiver_comment(), filter);
	}

	private OnItemClickListener ocl = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int position,
				long arg3) {
			commentUserID = commentList.get(position).userid;
			commentuuidTail = commentList.get(position).nickname;

			if (diaryUserID != null && diaryUserID.equals(userID)) {
				if (userID.equals(commentUserID)) {
					showMDCMenu();
				} else {
					showRDCMenu();
				}
			} else {
				if (userID.equals(commentUserID)) {
					showMDCMenu();
				} else {
					showRCMenu();
				}
			}
			/*
			 * if (!diaryUserID.equals(userID)) { if (!isTextInputMood) {
			 * showRMDCMenu(); } else { showRDCMood(); } }else{ showMDCMenu(); }
			 */
			comment_pos = position;
		}
	};

	// 设置位置
	private void setPosition() {
		Log.d(TAG, "setPosition");
		if (myDiary != null && myDiary.position != null
				&& myDiary.position.length() > 0) {
			location_pic.setVisibility(View.VISIBLE);
			location_pic.setBackgroundResource(R.drawable.pin);
			location_name.setVisibility(View.VISIBLE);
			location_name.setText(myDiary.position);
		} else {
			location_pic.setVisibility(View.INVISIBLE);
			location_name.setVisibility(View.INVISIBLE);
		}
		// Log.e("location is： ", myDiary.position + " ");
	}

	@Override
	public void onResume() {
		super.onResume();
		Log.e("Resume", "******");
		UmengclickAgentWrapper.onResume(this);
		CmmobiClickAgentWrapper.onResume(this);
		initUI();
		handler = getHandler();

		if (ifNotifyUI) {
			Requester2.diaryCommentList(getHandler(), null, null, diaryID,
					userID);
			isFirstQuery = true;
			ifNotifyUI = false;
			Log.e("************ifNotifyUI**********", "ifNotifyUI is true!");
		}

		/*
		 * otm = OfflineTaskManager.getInstance(); otm.start(0); IntentFilter
		 * filter = new IntentFilter();
		 * filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
		 * filter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
		 * filter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
		 * ZApplication
		 * .getInstance().getApplicationContext().registerReceiver(new
		 * NetConnectedReceiver(), filter);
		 */

		back.setOnClickListener(HomepageCommentActivity.this);
		zanIcon.setOnClickListener(HomepageCommentActivity.this);
		ll_zan_List.setOnClickListener(HomepageCommentActivity.this);
		comment_finish.setOnClickListener(HomepageCommentActivity.this);
		voice_input.setOnClickListener(HomepageCommentActivity.this);
		comment_list.setOnItemClickListener(ocl);
		moodVoicePlay.setOnClickListener(HomepageCommentActivity.this);
	}

	public class NetConnectedReceiver_comment extends ZBroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			// 这个监听wifi的连接状态即是否连上了一个有效无线路由，
			// 当上边广播的状态是WifiManager.WIFI_STATE_DISABLING，和WIFI_STATE_DISABLED的时候，根本不会接到这个广播。
			// 在上边广播接到广播是WifiManager.WIFI_STATE_ENABLED状态的同时也会接到这个广播，
			// 当然刚打开wifi肯定还没有连接到有效的无线
			// Log.d(TAG, "NetConnectedReceiver intent" + intent.getAction());
			if (WifiManager.NETWORK_STATE_CHANGED_ACTION.equals(intent
					.getAction())) {
				Parcelable parcelableExtra = intent
						.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
				if (null != parcelableExtra) {
					NetworkInfo networkInfo = (NetworkInfo) parcelableExtra;
					State state = networkInfo.getState();
					boolean isConnected = state == State.CONNECTED;// 当然，这边可以更精确的确定状态
					if (isConnected) {
						Log.d(TAG, "wifi is connected");
						ifNotifyUI = true;
					}
				}
			}
		}
	}

	@Override
	public void onPause() {
		super.onPause();
		UmengclickAgentWrapper.onPause(this);
		CmmobiClickAgentWrapper.onPause(this);
	}

	@Override
	public void onStop() {
		super.onStop();
		CmmobiClickAgentWrapper.onStop(this);

		if (shortRecView != null) {
			shortRecView.dismiss();
		}
		if (ear != null && ear.isRecording) {
			ear.stop();
		}

		TackView tackView = TackView.getTackView();
		if (tackView != null) {
			tackView.stop();
		}
	}

	private void initUI() {
		setPosition();
		zanIcon = (ImageView) findViewById(R.id.praise_icon);
		if (null != myDiary) {
			diaryUserID = myDiary.userid;
			if (diaryUserID != null && !diaryUserID.equals(userID)) {
				if (isPraise) {
					zanIcon.setImageResource(R.drawable.zan_2);
				} else {
					zanIcon.setImageResource(R.drawable.zan_1);
				}
			} else {
				zanIcon.setVisibility(View.GONE);
			}
		}

		comment_type = PUBLIC_TYPE[0];
		touxiang = (ImageView) findViewById(R.id.head_icon);
		touxiang.setOnClickListener(this);
		nick_name = (TextView) findViewById(R.id.nick_name);
		mood = (TextView) findViewById(R.id.mood_describe);
		moodVoicePlay = (TackView) findViewById(R.id.mood_voice);
		publishTime = (TextView) findViewById(R.id.publish_time);

		comment_finish = (ImageView) findViewById(R.id.iv_finish);
		ll_zan_List = (LinearLayout) findViewById(R.id.zan_list);
		mood_pic = (ImageView) findViewById(R.id.mood_pic);
		send = (ImageView) findViewById(R.id.send);
		if (!isLongClick) {
			comment_finish.setOnClickListener(this);
			ll_zan_List.setOnClickListener(this);
		}
		send.setOnClickListener(this);
		cidai_icon = (ImageView) findViewById(R.id.cidai);

		/*
		 * touxiang.setLoadingDrawable(R.drawable.temp_local_icon);
		 * touxiang.setImageUrl(ai.headimageurl, 1, true);
		 */
		if (myDiary != null && myDiary.headimageurl != null) {
			imageLoader.displayImage(myDiary.headimageurl, touxiang, options,
					animateFirstListener, ActiveAccount.getInstance(this)
							.getUID(), 1);
		} else {
			touxiang.setImageResource(R.drawable.moren_touxiang);
		}

		ll_send_expression = (LinearLayout) findViewById(R.id.ll_send_expression);
		if (null != myDiary) {
			diaryUserID = myDiary.userid;
			if (myDiary.attachs != null && myDiary.attachs.length > 0) {
				for (int i = 0; i < myDiary.attachs.length; i++) {
					if (myDiary.attachs[i].attachlevel.equals(MAIN_ATTACH)) {// 主内容
						if (myDiary.attachs[i].attachtype.equals(VIDEO)) {// 视频
							if (myDiary.attachs[i].videocover != null) {
								mood_pic.setVisibility(View.VISIBLE);
								// mood_pic.setImageUrl(0, 1,
								// myDiary.attachs[i].videocover, false);
								imageLoader.displayImage(
										myDiary.attachs[i].videocover,
										mood_pic, options1,
										animateFirstListener, ActiveAccount
												.getInstance(this).getUID(), 1);
							} else {
								mood_pic.setVisibility(View.GONE);
							}
						}
						if (myDiary.attachs[i].attachtype.equals(AUDIO)) {// 音频
							if (myDiary.attachs[i].attachaudio != null
									&& myDiary.attachs[i].attachaudio.length > 0) {
								// mood_pic.setVisibility(View.VISIBLE);
								// mood_pic.setBackgroundResource(R.drawable.cidai_2);
								cidai_icon.setVisibility(View.VISIBLE);
								cidai_icon
										.setBackgroundResource(R.drawable.cidai_2);
							} else {
								// /mood_pic.setVisibility(View.GONE);
								cidai_icon.setVisibility(View.GONE);
							}
						}
						if (myDiary.attachs[i].attachtype.equals(PIC)) {// 图片
							if (myDiary.attachs[i].attachimage != null
									&& myDiary.attachs[i].attachimage.length > 0
									&& myDiary.attachs[i].attachimage[0].imageurl != null) {
								mood_pic.setVisibility(View.VISIBLE);
								// mood_pic.setImageUrl(0, 1,
								// myDiary.attachs[i].attachimage[0].imageurl,
								// false);
								imageLoader
										.displayImage(
												myDiary.attachs[i].attachimage[0].imageurl,
												mood_pic, options1,
												animateFirstListener,
												ActiveAccount.getInstance(this)
														.getUID(), 1);
							} else {
								mood_pic.setVisibility(View.GONE);
							}
						}
						if (myDiary.attachs[i].attachtype.equals(TEXT)) {// 文字
							if (myDiary.attachs[i].content != null) {
								mood.setVisibility(View.VISIBLE);
								mood.setText(myDiary.attachs[i].content);
								expressionView.replacedExpressions2(
										myDiary.attachs[i].content, mood);
							} else {
								mood.setVisibility(View.GONE);
							}
						} else {
							mood.setVisibility(View.GONE);
						}
					}
					if (myDiary.attachs[i].attachlevel.equals(VICE_ATTACH)) {// 辅内容
						if (myDiary.attachs[i].attachtype.equals(AUDIO)) {// 音频
							if (myDiary.attachs[i].attachaudio != null
									&& myDiary.attachs[i].attachaudio.length > 0
									&& myDiary.attachs[i].attachaudio[0].audiourl != null) {
								url = myDiary.attachs[i].attachaudio[0].audiourl;
								moodVoicePlay.setVisibility(View.VISIBLE);
								moodVoicePlay
										.setBackground(R.drawable.btn_activity_homepage_recent_short_record);
								String playtime = myDiary.attachs[i].playtime;
								moodVoicePlay.setPlaytime(DateUtils
										.getPlayTime(playtime));
								moodVoicePlay.setOnClickListener(this);
								haveaudio = true;
							} else {
								moodVoicePlay.setVisibility(View.GONE);
							}
						}
						if (myDiary.attachs[i].attachtype.equals(TEXT)) {// 文字
							if (myDiary.attachs[i].content != null) {
								mood.setVisibility(View.VISIBLE);
								mood.setText(myDiary.attachs[i].content);
								expressionView.replacedExpressions2(
										myDiary.attachs[i].content, mood);
							} else {
								mood.setVisibility(View.GONE);
							}
						}
					}
				}
			}

			if (!haveaudio) {
				moodVoicePlay.setVisibility(View.INVISIBLE);
			}

			Log.e(TAG, "headimageurl = " + ai.headimageurl);
			if (myDiary.nickname != null) {
				nick_name.setText(myDiary.nickname);
			}
			Log.e("myDiary.nickname", myDiary.nickname + "");

			// mood.setText(myDiary.introduction);

			String pTime = myDiary.diarytimemilli;
			publishTime.setText(DateUtils.getMyCommonShowDate(new Date(Long
					.parseLong(pTime))));
		}
		zanIcon.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.back:
			// 按返回箭头，销毁当前activity。
			HomepageCommentActivity.this.finish();
			break;

		case R.id.jianpan:
			if (isTextInputMood) {
				voice_input.setImageResource(R.drawable.mic);
				input_et.setVisibility(View.VISIBLE);
				press_talk.setVisibility(View.GONE);
				ll_send_expression.setVisibility(View.VISIBLE);
				isTextInputMood = false;

				input_et.setFocusable(true);
				input_et.requestFocus();
				InputMethodManager m = (InputMethodManager) input_et
						.getContext().getSystemService(
								Context.INPUT_METHOD_SERVICE);
				m.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
			} else {
				voice_input.setImageResource(R.drawable.jianpan);
				input_et.setVisibility(View.GONE);
				ll_send_expression.setVisibility(View.GONE);
				press_talk.setVisibility(View.VISIBLE);
				press_talk.setBackgroundResource(R.drawable.luyin_1);
				isTextInputMood = true;

				input_et.clearFocus();
				InputMethodManager m = (InputMethodManager) input_et
						.getContext().getSystemService(
								Context.INPUT_METHOD_SERVICE);
				m.hideSoftInputFromWindow(input_et.getWindowToken(), 0);
			}
			break;

		case R.id.expression_input:
			if (!isShowExpress) {
				expression_input.setBackgroundResource(R.drawable.jianpan);
				isShowExpress = true;
			} else {
				expression_input
						.setBackgroundResource(R.drawable.message_biaoqing);
				isShowExpress = false;
			}
			expressionView.load();
			break;
		case R.id.mood_voice:
			((TackView) v).setAudio(url, 0);
			// ((TackView) v).stop();
			break;

		case R.id.praise_icon:
			if (!isPraise) {
				// TODO 赞操作
				Requester2.enjoyandforward(getHandler(), diaryID, null);
			} else {
				// ZToast.showLong("已经赞过此日记！");
				Requester2.deletepublishAndEnjoy(getHandler(),
						myDiary.publishid, diaryID);
				Log.e("diaryid", diaryID + " ");
			}
			break;

		case R.id.zan_list:
			// 点击更多箭头，转向赞过我的人列表页面。
			Intent intent = new Intent();
			intent.putExtra("diaryID", diaryID);
			moodVoicePlay.stop();
			TackView tackView = TackView.getTackView();
			if (tackView != null) {
				tackView.stop();
			}
			intent.setClass(HomepageCommentActivity.this,
					DiaryPraiseActivity.class);
			startActivity(intent);
			break;

		case R.id.iv_bq_delete:
			String etString = input_et.getText().toString();
			if (etString != null && !etString.equals("")
					&& etString.length() > 0) {
				input_et.setText(etString.substring(0, etString.length() - 1));
				input_et.setSelection(input_et.getText().length());
			}
			break;
		case R.id.send:
			// case R.id.iv_bq_send:
			String contentText = input_et.getText().toString();
			if (!contentText.equals("")) {
				if (comment_type == PUBLIC_TYPE[0]) {
					// Requester2.comment(getHandler(), et.getText().toString(),
					// null, "1", diaryID, "1", diaryID + userID
					// + commentuuid);
					// 离线任务
					OfflineTaskManager.getInstance().addCommentAddTask(
							contentText, null, "1", diaryID,
							myDiary == null ? null : myDiary.diaryuuid, "1",
							TimeHelper.getInstance().now() + "");
					input_et.setText("");
					// Prompt.Alert(getString(R.string.prompt_network_error));
				} else if (comment_type == PUBLIC_TYPE[1]) {
					// Requester2.comment(getHandler(), replyText + ":" +
					// et.getText().toString(),
					// commentList.get(comment_pos).commentid, "2",
					// diaryID, "1",
					// commentList.get(comment_pos).commentuuid);
					// 离线任务
					OfflineTaskManager.getInstance().addCommentAddTask(
							contentText,
							commentList.get(comment_pos).commentid,
							"2",
							diaryID,
							myDiary == null ? null : myDiary.diaryuuid,
							"1",
							TimeHelper.getInstance().now() + "@"
									+ commentuuidTail);
					input_et.setHint("");
				} else if (comment_type == PUBLIC_TYPE[2]) {
					// Requester2.comment(getHandler(), et.getText().toString(),
					// commentList.get(comment_pos).commentid, "3",
					// diaryID, "1",
					// commentList.get(comment_pos).commentuuid);
					// 离线任务
					OfflineTaskManager.getInstance().addCommentAddTask(
							contentText,
							commentList.get(comment_pos).commentid, "3",
							diaryID,
							myDiary == null ? null : myDiary.diaryuuid, "1",
							commentList.get(comment_pos).commentuuid);
					input_et.setText("");
				}
				comment_type = PUBLIC_TYPE[0];
				imm.hideSoftInputFromWindow(input_et.getWindowToken(),
						InputMethodManager.HIDE_NOT_ALWAYS);
			} else {
				Prompt.Alert("评论不能为空");
			}

			// if (!ZNetworkStateDetector.isConnected()) {
			String audio_path = "";
			GsonResponse2 gr2 = new GsonResponse2();
			GsonResponse2.diarycommentlistItem dclItem = gr2.new diarycommentlistItem();
			dclItem.audiourl = audio_path;
			dclItem.commentid = "111111111111111111";
			dclItem.commentcontent = contentText;
			if (audio_path != null) {

				dclItem.commentway = "2";
				// dclItem.playtime = playtime;
			} else {
				dclItem.commentway = "1";
			}
			if (comment_type == 1) {
				dclItem.commenttype = "1";
			} else if (comment_type == 2) {
				dclItem.commenttype = "2";
			} else if (comment_type == 3) {
				dclItem.commenttype = "3";
			}
			dclItem.userid = userID;
			dclItem.headimageurl = ai.headimageurl;
			dclItem.nickname = ai.nickname;
			dclItem.diaryid = diaryID;
			dclItem.createtime = String.valueOf(TimeHelper.getInstance().now());
			dclItem.signature = ai.signature;
			dclItem.sex = ai.sex;
			dclItem.isattention = "0";
			commentList.add(dclItem);

			ccr = new Comparator<GsonResponse2.diarycommentlistItem>() {

				@Override
				public int compare(GsonResponse2.diarycommentlistItem lhs,
						GsonResponse2.diarycommentlistItem rhs) {
					// TODO Auto-generated method stub
					long s1 = Long.parseLong(lhs.createtime);
					long s2 = Long.parseLong(rhs.createtime);
					return (int) (s2 - s1);
				}
			};
			Collections.sort(commentList, ccr);
			ai.commentList = commentList;
			commentListAdapter.notifyDataSetChanged();
			// }
			/* 显示键盘、麦克风按钮 */
			voice_input.setVisibility(View.VISIBLE);
			break;
		case R.id.btn_comment_reply:

			voice_input.setImageResource(R.drawable.mic);
			isTextInputMood = false;

			comment_type = PUBLIC_TYPE[1];
			input_et.setVisibility(View.VISIBLE);
			input_et.setFocusable(true);
			input_et.requestFocus();
			input_et.setText("");
			if (comment_pos < commentList.size()) {
				replyText = "回复" + commentList.get(comment_pos).nickname;
				input_et.setHint(replyText);
			}
			press_talk.setVisibility(View.GONE);
			ll_send_expression.setVisibility(View.VISIBLE);

			InputMethodManager m = (InputMethodManager) input_et.getContext()
					.getSystemService(Context.INPUT_METHOD_SERVICE);
			m.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
			portraitMenu.dismiss();
			break;
		case R.id.btn_comment_modify:
			comment_type = PUBLIC_TYPE[2];
			input_et.setVisibility(View.VISIBLE);
			input_et.setFocusable(true);
			input_et.setFocusableInTouchMode(true);
			input_et.requestFocus();
			input_et.setText(commentList.get(comment_pos).commentcontent);
			input_et.setSelection(commentList.get(comment_pos).commentcontent
					.length());

			InputMethodManager mm = (InputMethodManager) input_et.getContext()
					.getSystemService(Context.INPUT_METHOD_SERVICE);
			mm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);

			portraitMenu.dismiss();
			/* 显示键盘、麦克风按钮 */
			voice_input.setVisibility(View.VISIBLE);
			voice_input.setImageResource(R.drawable.mic);
			press_talk.setVisibility(View.GONE);
			ll_send_expression.setVisibility(View.VISIBLE);
			isTextInputMood = false;
			break;
		case R.id.btn_comment_delete:
			new Xdialog.Builder(this)
					.setTitle("删除评论")
					.setMessage("确定删除评论？")
					.setPositiveButton(android.R.string.ok,
							new DialogInterface.OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									// Requester2.deleteComment(getHandler(),
									// diaryID, comment_id,
									// commentList.get(comment_pos).commentuuid);
									// 离线任务
									OfflineTaskManager
											.getInstance()
											.addCommentDeleteTask(
													diaryID,
													myDiary == null ? null
															: myDiary.diaryuuid,
													comment_id,
													commentList
															.get(comment_pos).commentuuid);
									ZDialog.show(R.layout.progressdialog, true,
											true, HomepageCommentActivity.this);

									if (!ZNetworkStateDetector.isConnected()) {
										ZDialog.dismiss();
										commentList.remove(comment_pos);
										ai.commentList.remove(comment_pos);
										commentListAdapter
												.notifyDataSetChanged();
									}
									input_et.setText("");
									input_et.setHint("");
								}
							}).setNegativeButton(android.R.string.cancel, null)
					.create().show();
			portraitMenu.dismiss();
			break;
		case R.id.btn_comment_cancle:
			portraitMenu.dismiss();
			break;
		case R.id.iv_finish:
			voice_input.setImageResource(R.drawable.mic);
			input_et.setVisibility(View.VISIBLE);
			input_et.setFocusable(true);
			input_et.requestFocus();
			press_talk.setVisibility(View.GONE);
			ll_send_expression.setVisibility(View.VISIBLE);
			isTextInputMood = false;
			InputMethodManager im = (InputMethodManager) input_et.getContext()
					.getSystemService(Context.INPUT_METHOD_SERVICE);
			im.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
			break;
		case R.id.head_icon:
			if (!diaryUserID.equals(userID)) {
				Intent inte = new Intent(HomepageCommentActivity.this,
						HomepageOtherDiaryActivity.class);
				inte.putExtra("userid", diaryUserID);
				if (myDiary.nickname != null) {
					nick_name.setText(myDiary.nickname);
					inte.putExtra("nickname", myDiary.nickname);
				}
				startActivity(inte);
			}
			break;
		default:
			break;
		}
	}

	@Override
	public boolean handleMessage(Message msg) {
		int vol = 0;
		switch (msg.what) {
		case SEND_SOUND_RECORD_MSG:
			Log.e("SEND_SOUND_RECORD_MSG", "SEND_SOUND_RECORD_MSG");
			LocalBroadcastManager.getInstance(this).registerReceiver(
					mMessageReceiver,
					new IntentFilter(ExtAudioRecorder.AUDIO_RECORDER_MSG));

			long currentTime = TimeHelper.getInstance().now();
			audioID = String.valueOf(currentTime);
			sound_path = Constant.SD_STORAGE_ROOT + "/"
					+ ActiveAccount.getInstance(this).getLookLookID()
					+ "/audio";
			boolean ear_OK_text = ear.start(this, audioID, sound_path, false,
					3, true);
			handler.sendEmptyMessage(UPDATE_COUNT_30_SECONDS);
			Log.e("ear_OK_text", ear_OK_text + "");
			if (!ear_OK_text) {
				boolean ear_OK = ear.start(this, audioID, sound_path, false, 3);
				Log.e("ear_OK", ear_OK + "");
				handler.removeMessages(UPDATE_COUNT_30_SECONDS);
				handler.sendEmptyMessage(UPDATE_COUNT_30_SECONDS);
				if (!ear_OK) {
					isLongClick = false;
					Prompt.Alert("无法生存录音文件！");
					handler.removeMessages(UPDATE_COUNT_30_SECONDS);
					if (shortRecView != null) {
						shortRecView.dismiss();
					}
				}
			}
			break;

		case UPDATE_COUNT_30_SECONDS:
			if (isLongClick) {
				shortRecView.updateTime(30 - recordDuration);
				if (recordDuration >= 30) {
					isLongClick = false;

					ear.stop();
					Log.d(TAG, "MotionEvent.ACTION_UP isOnRecorderButton = "
							+ isOnRecorderButton);

					msg = handler.obtainMessage();
					msg.what = STOP_SOUND_RECORD_MSG;
					handler.sendMessage(msg);
					shortRecView.dismiss();
				}
			}
			recordDuration++;
			Log.e("recordDuration", recordDuration + " ");

			if (isLongClick) {
				handler.sendEmptyMessageDelayed(UPDATE_COUNT_30_SECONDS, 1000);
			} else {
				recordDuration = 0;
			}
			break;
		case STOP_SOUND_RECORD_MSG:
			Log.e("STOP_SOUND_RECORD_MSG", "STOP_SOUND_RECORD_MSG");
			if (!isOnRecorderButton) {
				/**
				 * 上滑取消录音时删除录音文件
				 */
				String name = Environment.getExternalStorageDirectory()
						+ sound_path + "/" + audioID + "/" + audioID + ".mp4";
				File file = new File(name);
				if (file.exists()) {
					file.delete();
				}
			}
			break;
		case QISR_TASK.HANDLER_QISR_RESULT_CLEAN:
			sound_text = "";
			Log.e("QISR_TASK.HANDLER_QISR_RESULT_CLEAN ",
					"QISR_TASK.HANDLER_QISR_RESULT_CLEAN");
			break;

		case QISR_TASK.HANDLER_QISR_RESULT_DONE:
			Log.e("QISR_TASK.HANDLER_QISR_RESULT_DONE ",
					"QISR_TASK.HANDLER_QISR_RESULT_DONE");
			if (isOnRecorderButton) {
				// ZDialog.show(R.layout.progressdialog, true, true, this);
				if (comment_type == PUBLIC_TYPE[0]) {
					if (sound_text != null && !sound_text.equals("")) {
						// Requester2.comment(getHandler(), sound_text, null,
						// "1",
						// diaryID, "3", commentuuid);
						// Prompt.Alert(getString(R.string.prompt_network_error));
						// 离线任务
						OfflineTaskManager.getInstance().addCommentAddTask(
								sound_text, null, "1", diaryID,
								myDiary == null ? null : myDiary.diaryuuid,
								"3", TimeHelper.getInstance().now() + "");
					} else {
						// Requester2.comment(getHandler(), null, null, "1",
						// diaryID, "2", commentuuid);
						// 离线任务
						OfflineTaskManager.getInstance().addCommentAddTask(
								null, null, "1", diaryID,
								myDiary == null ? null : myDiary.diaryuuid,
								"2", TimeHelper.getInstance().now() + "");
					}
				} else if (comment_type == PUBLIC_TYPE[1]) {
					if (sound_text != null && !sound_text.equals("")) {
						// Requester2.comment(getHandler(), replyText +
						// sound_text, comment_id,
						// "2", diaryID, "3",
						// commentList.get(comment_pos).commentuuid);
						// 离线任务
						OfflineTaskManager.getInstance().addCommentAddTask(
								sound_text,
								comment_id,
								"2",
								diaryID,
								myDiary == null ? null : myDiary.diaryuuid,
								"3",
								TimeHelper.getInstance().now() + "@"
										+ commentuuidTail);
					} else {
						// Requester2.comment(getHandler(), replyText,
						// comment_id, "2",
						// diaryID, "2",
						// commentList.get(comment_pos).commentuuid);
						// 离线任务
						OfflineTaskManager.getInstance().addCommentAddTask(
								null,
								comment_id,
								"2",
								diaryID,
								myDiary == null ? null : myDiary.diaryuuid,
								"2",
								TimeHelper.getInstance().now() + "@"
										+ commentuuidTail);
					}
				}
			} else {
				if (sound_path != null) {
					File file = new File(sound_path);
					if (file.exists()) {
						file.delete();
					}
				}
			}

			// if (!ZNetworkStateDetector.isConnected()) {
			String audio_path = sound_path + "/" + audioID + "/" + audioID + ".mp4";
			GsonResponse2 gr2 = new GsonResponse2();
			GsonResponse2.diarycommentlistItem dclItem = gr2.new diarycommentlistItem();
			dclItem.audiourl = audio_path;
			dclItem.commentid = "111111111111111111";
			dclItem.commentcontent = input_et.getText().toString();
			if (audio_path != null) {

				dclItem.commentway = "2";
				dclItem.playtime = String.valueOf((int) recordDuration);
			} else {
				dclItem.commentway = "1";
			}
			dclItem.commenttype = "1";
			dclItem.userid = userID;
			dclItem.headimageurl = ai.headimageurl;
			dclItem.nickname = ai.nickname;
			dclItem.diaryid = diaryID;
			dclItem.signature = ai.signature;
			dclItem.sex = ai.sex;
			dclItem.isattention = "0";
			dclItem.createtime = String.valueOf(TimeHelper.getInstance().now());
			if(tempTime != null){
				if(Long.parseLong(dclItem.createtime) - Long.parseLong(tempTime) > 1000){
					commentList.add(dclItem);
				}				
			}else{
				commentList.add(dclItem);
			}
			tempTime = dclItem.createtime;	
			ccr = new Comparator<GsonResponse2.diarycommentlistItem>() {

				@Override
				public int compare(GsonResponse2.diarycommentlistItem lhs,
						GsonResponse2.diarycommentlistItem rhs) {
					// TODO Auto-generated method stub
					long s1 = Long.parseLong(lhs.createtime);
					long s2 = Long.parseLong(rhs.createtime);
					return (int) (s2 - s1);
				}
			};
			Collections.sort(commentList, ccr);
			ai.commentList = commentList;
			commentListAdapter.notifyDataSetChanged();
			// }
			break;
		case QISR_TASK.HANDLER_QISR_RESULT_ADD:
			if (msg != null && msg.obj != null) {
				sound_text += (String) msg.obj;
			}
			Log.e("(String)msg.obj = ", (String) msg.obj + "");
			// Toast.makeText(HomepageCommentActivity.this, sound_text,
			// Toast.LENGTH_SHORT).show();
			Log.e("QISR_TASK.HANDLER_QISR_RESULT_ADD ",
					"QISR_TASK.HANDLER_QISR_RESULT_ADD");
			break;
		case ExtAudioRecorder.HANDLER_AUDIO_RECORDER_DONE:
			Log.e("ExtAudioRecorder.HANDLER_AUDIO_RECORDER_DONE ",
					"ExtAudioRecorder.HANDLER_AUDIO_RECORDER_DONE");
			Bundle bundle = msg.getData();
			if (bundle != null && bundle.getString("playtime") != null) {
				playtime = bundle.getString("playtime");
			}
			if (!ZNetworkStateDetector.isConnected() 
					|| !PluginUtils.isPluginMounted()) {
				msg = handler.obtainMessage();
				msg.what = QISR_TASK.HANDLER_QISR_RESULT_DONE;
				handler.sendMessage(msg);
			}
			break;
		case Requester2.RESPONSE_TYPE_COMMENT:// 发表评论成功返回
			Log.e("Requester2.RESPONSE_TYPE_COMMENT ",
					"Requester2.RESPONSE_TYPE_COMMENT");
			ZDialog.dismiss();
			GsonResponse2.commentResponse resp = (GsonResponse2.commentResponse) msg.obj;
			if (resp != null && resp.status.equals("0")) {
				input_et.setText("");
				input_et.setHint("");
				NetworkTaskInfo tt = null;
				UploadNetworkTask uploadtask = null;
				if (sound_path != null && !sound_path.equals("")) {
					/**
					 * 2、注册MediaMapping
					 */
					// 提交评论成功,将录音文件在mediamapping中注册
					MediaValue mv = new MediaValue();
					String audiopath = Environment.getExternalStorageDirectory()
							+ sound_path+ "/" + audioID + "/" + audioID + ".mp4";
					long fileSize = (new File(audiopath)).length();
					mv.path = audiopath;
					mv.Belong = 1;
					mv.MediaType = 1;
					mv.url = resp.audiopath.replace("_", "/");
					mv.UID = userID;
					mv.realSize = fileSize;
					mv.totalSize = fileSize;
					mv.Sync = 1;
					mv.SyncSize = fileSize;
					ai.mediamapping.setMedia(userID, resp.audiopath, mv);

					tt = new NetworkTaskInfo(userID, diaryID,
							resp.commentid/* attachid */, audiopath,
							resp.audiopath, "3", "2");
					uploadtask = new UploadNetworkTask(tt);// 创建上传/下载任务
					uploadtask.start();

					block: if (uploadtask.getState() != INetworkTask.STATE_COMPELETED) {
						// ZDialog.show(R.layout.progressdialog, true, true,
						// this);
						if (uploadtask.getState() == INetworkTask.STATE_ERROR
								|| uploadtask.getState() == INetworkTask.ACTION_RETRY) {
							Prompt.Alert("错误的状态！");
							break block;
						}
						while (uploadtask.getState() == INetworkTask.STATE_RUNNING
								|| uploadtask.getState() == INetworkTask.STATE_PREPARING
								|| uploadtask.getState() == INetworkTask.STATE_WAITING) {
							try {
								new Thread().sleep(100);
								Log.e("sleepinnnnnnnnnnnnnnnnnnnnng",
										"0.1 second!");
								Log.e("uploadtask.getState()",
										uploadtask.getState() + "");
							} catch (InterruptedException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
					} else {
						Prompt.Alert("上传完成！");
					}
					sound_path = "";
				}

				// if (uploadtask != null && uploadtask.getState() == 6) {
				// 查询评论
				ZDialog.dismiss();
				Requester2.diaryCommentList(getHandler(), null, null, diaryID,
						userID);
				isFirstQuery = true;
				// commentList = new
				// ArrayList<GsonResponse2.diarycommentlistItem>();
				// }
			} else {
				Prompt.Alert("添加评论失败！");
			}
			break;
		case Requester2.RESPONSE_TYPE_DIARY_COMMENTLIST:
			Log.e("Requester2.RESPONSE_TYPE_DIARY_COMMENTLIST ",
					"Requester2.RESPONSE_TYPE_DIARY_COMMENTLIST");
			ZDialog.dismiss();
			if (isFirstQuery) {
				commentList.clear();
			}
			// 查询评论成功
			GsonResponse2.diaryCommentListResponse response = (GsonResponse2.diaryCommentListResponse) msg.obj;
			// commentList = new
			// ArrayList<GsonResponse2.diarycommentlistItem>();
			if (response != null) {
				if (null != response.comments) {
					for (int i = 0; i < response.comments.length; i++) {
						commentList.add(response.comments[i]);
					}
					if (response.hasnextpage.equals("1")) {
						Requester2.diaryCommentList(getHandler(),
								response.last_comment_time, "2", diaryID,
								userID);
						isFirstQuery = false;
					}
					ccr = new Comparator<GsonResponse2.diarycommentlistItem>() {

						@Override
						public int compare(
								GsonResponse2.diarycommentlistItem lhs,
								GsonResponse2.diarycommentlistItem rhs) {
							// TODO Auto-generated method stub
							long s1 = Long.parseLong(lhs.createtime);
							long s2 = Long.parseLong(rhs.createtime);
							return (int) (s2 - s1);
						}
					};
				}
				// ai.commentList = commentList;
				Collections.sort(commentList, ccr);
				commentListAdapter.notifyDataSetChanged();
			}

			initUI();
			break;

		case Requester2.RESPONSE_TYPE_DELETE_COMMENT:
			Log.e("Requester2.RESPONSE_TYPE_DELETE_COMMENT ",
					"Requester2.RESPONSE_TYPE_DELETE_COMMENT");
			GsonResponse2.deleteCommentResponse dcRespon = (GsonResponse2.deleteCommentResponse) msg.obj;
			if (dcRespon != null && dcRespon.status.equals("0")) {
				// ai.commentList.remove(comment_pos);
				commentListAdapter.notifyDataSetChanged();
				commentList.clear();
				Requester2.diaryCommentList(getHandler(), null, null, diaryID,
						userID);
				// commentList = new
				// ArrayList<GsonResponse2.diarycommentlistItem>();
			}
			break;
		case Requester2.RESPONSE_TYPE_DIARY_ENJOY:// 赞操作返回
			Log.e("Requester2.RESPONSE_TYPE_DIARY_ENJOY ",
					"Requester2.RESPONSE_TYPE_DIARY_ENJOY");
			GsonResponse2.enjoyResponse eResponse = (GsonResponse2.enjoyResponse) msg.obj;
			if (eResponse != null && eResponse.status.equals("0")) {
				Prompt.Alert("赞成功");

				mDiaryManager.addPraiseDiaryID(myDiary.diaryid);
				mDiaryManager.addDiaryToPraise(myDiary);
				LocalBroadcastManager
						.getInstance(this)
						.sendBroadcast(
								new Intent(
										DiaryDetailActivity.INTENT_ACTION_PRAISE_CHANGE));
				Requester2.getDiaryForwardUsers(getHandler(), diaryID, null,
						"1");
			} else {
				Prompt.Alert("赞失败");
			}
			break;

		// 赞列表
		case Requester2.RESPONSE_TYPE_GET_DIARY_FORWORD:
			GsonResponse2.getDiaryForwardUsersResponse gdfuResponse = (GsonResponse2.getDiaryForwardUsersResponse) msg.obj;
			gdfuList.clear();
			if (gdfuResponse != null && gdfuResponse.forwords.length > 0) {
				for (int j = 0; j < gdfuResponse.forwords.length; j++) {
					if (gdfuList.size() == 0) {
						gdfuList.add(gdfuResponse.forwords[j]);
					}
					for (int i = 0; i < gdfuList.size(); i++) {
						if (!gdfuResponse.forwords[j].userid.equals(gdfuList
								.get(i).userid)) {
							gdfuList.add(gdfuResponse.forwords[j]);
						}
					}
					if (userID.equals(gdfuResponse.forwords[j].userid)) {
						isPraise = true;
					}
				}
			}

			initUI();

			if (gdfuList.size() > 0) {
				ll_zan_List.setVisibility(View.VISIBLE);
				zan_grid.setAdapter(gdfuAdapter);
			} else {
				ll_zan_List.setVisibility(View.GONE);
			}
			if (null != gdfuAdapter) {
				gdfuAdapter.notifyDataSetChanged();
			}
			break;
		// 查询日记详情
		case Requester2.RESPONSE_TYPE_DIARY_INFO:
			GsonResponse2.diaryInfoResponse diResp = (GsonResponse2.diaryInfoResponse) msg.obj;
			if (diResp != null && diResp.status.equals("0")) {
				myDiary = diResp.diaries;
			}
			if (myDiary != null) {
				initUI();
			}
			break;

		// 取消赞
		case Requester2.RESPONSE_TYPE_DELETE_AND_ENJOY:
			GsonResponse2.deletepublishAndEnjoyResponse dpaeResponse = (GsonResponse2.deletepublishAndEnjoyResponse) msg.obj;
			if (dpaeResponse != null && dpaeResponse.status.equals("0")) {

				mDiaryManager.removePraiseDiaryID(myDiary.diaryid);
				mDiaryManager.removePraiseDiaryByID(myDiary.diaryid);
				LocalBroadcastManager
						.getInstance(this)
						.sendBroadcast(
								new Intent(
										DiaryDetailActivity.INTENT_ACTION_PRAISE_CHANGE));

				isPraise = false;
				Requester2.getDiaryForwardUsers(getHandler(), diaryID, null,
						"1");
				Prompt.Alert("取消赞成功");
			}
			initUI();
			break;
		default:
			break;
		}
		return false;
	}

	private class CommentListAdapter extends BaseAdapter {
		ArrayList<GsonResponse2.diarycommentlistItem> cl;
		LayoutInflater lf;
		Context context;

		public CommentListAdapter(Context context,
				ArrayList<GsonResponse2.diarycommentlistItem> dcl) {
			this.cl = dcl;
			lf = LayoutInflater.from(context);
			this.context = context;
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return cl.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return cl.get(position);
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {

			ViewHolder holder;
			final GsonResponse2.diarycommentlistItem dcli = cl.get(position);

			if (convertView == null) {
				holder = new ViewHolder();
				convertView = lf.inflate(R.layout.comment_list, null);
				holder.headIcon = (ImageView) convertView
						.findViewById(R.id.comment_list_head_icon);
				holder.nickName = (TextView) convertView
						.findViewById(R.id.nick_name);
				holder.commentTime = (TextView) convertView
						.findViewById(R.id.comment_time);
				holder.commentContent = (TextView) convertView
						.findViewById(R.id.comment_describe);
				holder.commentVoiceDuration = (TackView) convertView
						.findViewById(R.id.comment_voice);
				holder.tv_reply = (TextView) convertView
						.findViewById(R.id.tv_reply);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}

			if (dcli != null) {
				comment_id = dcli.commentid;

				if (dcli.commenttype.equals("2")
						&& dcli.commentuuid.contains("@")) {

					String commentPos = dcli.commentuuid
							.substring(dcli.commentuuid.indexOf("@") + 1);
					// String replyName =
					// AccountInfo.getInstance(commentPos).nickname;
					if (commentPos != null && !commentPos.equals("")) {
						holder.tv_reply.setVisibility(View.VISIBLE);
						holder.tv_reply.setText("回复：" + commentPos);
					}
					Log.e("=========", commentPos + "");
				} else {
					holder.tv_reply.setVisibility(View.GONE);
				}
				if (dcli.headimageurl != null) {
					imageLoader.displayImage(
							dcli.headimageurl,
							holder.headIcon,
							options,
							animateFirstListener,
							ActiveAccount.getInstance(
									HomepageCommentActivity.this).getUID(), 1);
				} else {
					holder.headIcon.setImageResource(R.drawable.moren_touxiang);
				}

				if (!isLongClick) {
					holder.headIcon.setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View v) {
							if (!userID.equals(dcli.userid)) {
								Intent intent = new Intent(context,
										HomepageOtherDiaryActivity.class);
								intent.putExtra("userid", dcli.userid);
								intent.putExtra("nickname", dcli.nickname);
								context.startActivity(intent);
							}
						}
					});
				}
				if (dcli.nickname != null) {
					holder.nickName.setText(dcli.nickname);
				}
				if (dcli.createtime != null) {
					holder.commentTime.setText(DateUtils
							.getMyCommonShowDate(new Date(Long
									.parseLong(dcli.createtime))));
					Log.e("时间", DateUtils.getMyCommonShowDate(new Date(Long
							.parseLong(dcli.createtime))));
				}
				if (null != dcli.commentcontent) {
					holder.commentContent.setText(dcli.commentcontent);
					expressionView.replacedExpressions2(dcli.commentcontent,
							holder.commentContent);
					Log.e("评论内容----------》", dcli.commentcontent + " ");
				} else {
					holder.commentContent.setVisibility(View.GONE);
				}
				if (dcli.audiourl != null && !dcli.audiourl.equals("")) {
					holder.commentVoiceDuration.setVisibility(View.VISIBLE);
					holder.commentVoiceDuration
							.setBackground(R.drawable.btn_activity_homepage_recent_short_record);
					// holder.commentVoiceDuration.setAudio(dcli.audiourl, 0);
					// holder.commentVoiceDuration.stop();
					String playtime = dcli.playtime;
					if (null != playtime) {
						holder.commentVoiceDuration.setPlaytime(DateUtils
								.getPlayTime(playtime));
					} else {
						holder.commentVoiceDuration.setPlaytime(String
								.valueOf((int) recordDuration) + "''");
					}

					if (!isLongClick) {
						holder.commentVoiceDuration
								.setOnClickListener(new OnClickListener() {
									@Override
									public void onClick(View v) {
										((TackView) v).setAudio(dcli.audiourl,
												0);
									}
								});
						Log.e("audiourl", dcli.audiourl + "");
					}
				} else {
					holder.commentVoiceDuration.setVisibility(View.GONE);
				}
			}

			return convertView;
		}

		class ViewHolder {
			ImageView headIcon;
			TextView nickName;
			TextView commentTime;
			TextView commentContent;
			TackView commentVoiceDuration;
			TextView tv_reply;
		}

	}

	public void showMDCMenu() {
		LayoutInflater inflater = LayoutInflater
				.from(HomepageCommentActivity.this);
		View view = inflater.inflate(R.layout.activity_comment_deal_menu, null);
		portraitMenu = new PopupWindow(view, LayoutParams.FILL_PARENT,
				LayoutParams.WRAP_CONTENT, true);
		rootView = (RelativeLayout) findViewById(R.id.comment_root);
		portraitMenu.setBackgroundDrawable(getResources().getDrawable(
				R.drawable.dot_big));
		portraitMenu.showAtLocation(rootView, Gravity.BOTTOM, 0, 0);
		view.findViewById(R.id.btn_comment_reply).setVisibility(View.GONE);
		view.findViewById(R.id.btn_comment_modify).setOnClickListener(this);
		view.findViewById(R.id.btn_comment_delete).setOnClickListener(this);
		view.findViewById(R.id.btn_comment_cancle).setOnClickListener(this);
	}

	public void showRDCMenu() {
		LayoutInflater inflater = LayoutInflater
				.from(HomepageCommentActivity.this);
		View view = inflater.inflate(R.layout.activity_comment_deal_menu, null);
		portraitMenu = new PopupWindow(view, LayoutParams.FILL_PARENT,
				LayoutParams.WRAP_CONTENT, true);
		rootView = (RelativeLayout) findViewById(R.id.comment_root);
		portraitMenu.setBackgroundDrawable(getResources().getDrawable(
				R.drawable.dot_big));
		portraitMenu.showAtLocation(rootView, Gravity.BOTTOM, 0, 0);
		view.findViewById(R.id.btn_comment_reply).setOnClickListener(this);
		view.findViewById(R.id.btn_comment_modify).setVisibility(View.GONE);
		view.findViewById(R.id.btn_comment_delete).setOnClickListener(this);
		view.findViewById(R.id.btn_comment_cancle).setOnClickListener(this);
	}

	public void showRCMenu() {

		LayoutInflater inflater = LayoutInflater
				.from(HomepageCommentActivity.this);
		View view = inflater.inflate(R.layout.activity_comment_deal_menu, null);
		portraitMenu = new PopupWindow(view, LayoutParams.FILL_PARENT,
				LayoutParams.WRAP_CONTENT, true);
		rootView = (RelativeLayout) findViewById(R.id.comment_root);
		portraitMenu.setBackgroundDrawable(getResources().getDrawable(
				R.drawable.dot_big));
		portraitMenu.showAtLocation(rootView, Gravity.BOTTOM, 0, 0);
		view.findViewById(R.id.btn_comment_reply).setOnClickListener(this);
		view.findViewById(R.id.btn_comment_modify).setVisibility(View.GONE);
		view.findViewById(R.id.btn_comment_delete).setVisibility(View.GONE);
		view.findViewById(R.id.btn_comment_cancle).setOnClickListener(this);
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		// TODO Auto-generated method stub
		input_et.append(arg1.getTag().toString());
		Log.e("MotionEvent", "ACTION_CLICK");
	}

	@Override
	protected void onDestroy() {
		LocalBroadcastManager.getInstance(this).unregisterReceiver(
				mMessageReceiver);
		super.onDestroy();
	}

	/**
	 * 赞列表适配器
	 * 
	 * @author jyn 3.0暂时无此功能
	 */
	/*
	 * private class ZanListAdapter extends BaseAdapter {
	 * ArrayList<GsonResponse2.getDiaryEnjoyUsers> zl; LayoutInflater lf;
	 * 
	 * public ZanListAdapter(Context context,
	 * ArrayList<GsonResponse2.getDiaryEnjoyUsers> zl) { this.zl = zl; lf =
	 * LayoutInflater.from(context); }
	 * 
	 * @Override public int getCount() { // TODO Auto-generated method stub
	 * return zl.size(); }
	 * 
	 * @Override public Object getItem(int position) { // TODO Auto-generated
	 * method stub return zl.get(position); }
	 * 
	 * @Override public long getItemId(int position) { // TODO Auto-generated
	 * method stub return position; }
	 * 
	 * @Override public View getView(int position, View convertView, ViewGroup
	 * parent) {
	 * 
	 * ViewHolder holder; GsonResponse2.getDiaryEnjoyUsers dcli =
	 * zl.get(position);
	 * 
	 * if (convertView == null) { holder = new ViewHolder(); convertView =
	 * lf.inflate(R.layout.activity_comment_zanlist, null); holder.headIcon =
	 * (ImageView) convertView .findViewById(R.id.comment_list_head_icon);
	 * 
	 * holder.headIcon.setImageURI(Uri.parse(dcli.headimageurl)); if (null !=
	 * dcli.headimageurl) { holder.headIcon.setImageUrl(dcli.headimageurl, 1,
	 * true); } } else { holder = (ViewHolder) convertView.getTag(); } return
	 * convertView; }
	 * 
	 * class ViewHolder { ImageView headIcon; } }
	 */

	private class GdfuListAdapter extends BaseAdapter {
		ArrayList<GsonResponse2.getDiaryForwardUsers> zl;
		LayoutInflater lf;

		public GdfuListAdapter(Context context,
				ArrayList<GsonResponse2.getDiaryForwardUsers> zl) {
			this.zl = zl;
			lf = LayoutInflater.from(context);
		}

		@Override
		public int getCount() {
			if (zl.size() > 4) {
				return 4;
			} else {
				return zl.size();
			}
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return zl.get(position);
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {

			ViewHolder holder;
			GsonResponse2.getDiaryForwardUsers dcli = zl.get(position);

			if (convertView == null) {
				holder = new ViewHolder();
				convertView = lf.inflate(R.layout.activity_comment_zanlist,
						null);
				holder.headIcon = (WebImageView) convertView
						.findViewById(R.id.zan_list_head_icon);

				// holder.headIcon.setImageUrl(dcli.headimageurl, 1, true);
				if (null != dcli.headimageurl) {
					holder.headIcon.setImageUrl(R.drawable.moren_touxiang, 1,
							dcli.headimageurl, true);
				}

				holder.headIcon.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						Intent intent = new Intent();
						intent.putExtra("diaryID", diaryID);
						intent.setClass(HomepageCommentActivity.this,
								DiaryPraiseActivity.class);
						startActivity(intent);
					}
				});
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			return convertView;
		}

		class ViewHolder {
			WebImageView headIcon;
		}
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		int action = event.getAction();
		msg = handler.obtainMessage();
		if (action == MotionEvent.ACTION_DOWN) {
			back.setOnClickListener(null);
			zanIcon.setOnClickListener(null);
			ll_zan_List.setOnClickListener(null);
			comment_finish.setOnClickListener(null);
			voice_input.setOnClickListener(null);
			comment_list.setOnItemClickListener(null);
			moodVoicePlay.setOnClickListener(null);

			if (shortRecView == null) {
				shortRecView = new CountDownView(HomepageCommentActivity.this);
			}
			shortRecView.show();

			isOnRecorderButton = true;
			msg.what = SEND_SOUND_RECORD_MSG;
			handler.sendMessage(msg);

			press_talk.setBackgroundResource(R.drawable.luyin_2);
			isLongClick = true;

			Log.e(TAG, "MotionEvent.ACTION_DOWN");
		} else if (action == MotionEvent.ACTION_UP) {

			if (shortRecView != null) {
				shortRecView.dismiss();
			}
			ear.stop();

			imm.hideSoftInputFromWindow(v.getWindowToken(),
					InputMethodManager.HIDE_NOT_ALWAYS);
			isLongClick = false;
			// vsv.dismissView();
			// vsv.dismissCancle();
			msg.what = STOP_SOUND_RECORD_MSG;
			handler.sendMessage(msg);
			press_talk.setBackgroundResource(R.drawable.luyin_1);
			Log.e(TAG, "MotionEvent.ACTION_UP");

			back.setOnClickListener(HomepageCommentActivity.this);
			zanIcon.setOnClickListener(HomepageCommentActivity.this);
			ll_zan_List.setOnClickListener(HomepageCommentActivity.this);
			comment_finish.setOnClickListener(HomepageCommentActivity.this);
			voice_input.setOnClickListener(HomepageCommentActivity.this);
			comment_list.setOnItemClickListener(ocl);
			moodVoicePlay.setOnClickListener(HomepageCommentActivity.this);
		} else if (action == MotionEvent.ACTION_MOVE) {
			if (event.getY() < 0) {
				isOnRecorderButton = false;
			} else {
				isOnRecorderButton = true;
			}
		}
		return true;
	}
}
