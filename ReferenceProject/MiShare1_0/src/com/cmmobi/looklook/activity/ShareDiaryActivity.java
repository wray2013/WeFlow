package com.cmmobi.looklook.activity;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.v4.content.LocalBroadcastManager;
import android.telephony.TelephonyManager;
import android.text.Editable;
import android.text.Selection;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.View.OnLongClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.ToggleButton;
import cn.zipper.framwork.core.ZActivity;
import cn.zipper.framwork.core.ZApplication;
import cn.zipper.framwork.core.ZDialog;
import cn.zipper.framwork.io.network.ZNetworkStateDetector;

import com.baidu.mapapi.map.LocationData;
import com.baidu.mapapi.search.MKAddrInfo;
import com.baidu.platform.comapi.basestruct.GeoPoint;
import com.baidu.platform.comapi.map.l;
import com.cmmobi.looklook.MainApplication;
import com.cmmobi.looklook.R;
import com.cmmobi.looklook.activity.OtherZoneActivity.zoneLayout.myAdapter;
import com.cmmobi.looklook.common.constant.Constant;
import com.cmmobi.looklook.common.gson.DiaryController;
import com.cmmobi.looklook.common.gson.GsonRequest3.SNS;
import com.cmmobi.looklook.common.gson.GsonResponse3.MicListItem;
import com.cmmobi.looklook.common.gson.GsonResponse3.MyDiary;
import com.cmmobi.looklook.common.gson.GsonResponse3.UserObj;
import com.cmmobi.looklook.common.gson.GsonResponse3.VshareDiary;
import com.cmmobi.looklook.common.gson.GsonResponse3.createMicResponse;
import com.cmmobi.looklook.common.gson.GsonResponse3.shareImageUrl;
import com.cmmobi.looklook.common.gson.Requester3;
import com.cmmobi.looklook.common.utils.CmmobiClickAgentWrapper;
import com.cmmobi.looklook.common.utils.MergeTool;
import com.cmmobi.looklook.common.utils.MergeTool.DiaryImage;
import com.cmmobi.looklook.common.utils.MergeTool.ENUM_TYPE;
import com.cmmobi.looklook.common.view.AddExpressionView;
import com.cmmobi.looklook.common.view.ContentThumbnailView;
import com.cmmobi.looklook.common.view.DiaryImportShareView;
import com.cmmobi.looklook.common.view.DiaryImportShareView.OnImportClickListener;
import com.cmmobi.looklook.common.view.EmojiPaser;
import com.cmmobi.looklook.common.view.FriendsExpressionView;
import com.cmmobi.looklook.common.view.VolumeStateView;
import com.cmmobi.looklook.common.view.Xdialog;
import com.cmmobi.looklook.dialog.ShareDialog;
import com.cmmobi.looklook.info.location.MyAddressInfo;
import com.cmmobi.looklook.info.location.MyLocationInfo;
import com.cmmobi.looklook.info.location.OnPoiSearchListener;
import com.cmmobi.looklook.info.location.POIAddressInfo;
import com.cmmobi.looklook.info.profile.AccountInfo;
import com.cmmobi.looklook.info.profile.ActiveAccount;
import com.cmmobi.looklook.info.profile.CommonInfo;
import com.cmmobi.looklook.info.profile.DiaryManager;
import com.cmmobi.looklook.info.profile.MediaValue;
import com.cmmobi.looklook.info.profile.MiShareTaskInfo;
import com.cmmobi.looklook.info.profile.TimeHelper;
import com.cmmobi.looklook.info.profile.WrapUser;
import com.cmmobi.looklook.misharetask.MiShareTask;
import com.cmmobi.looklook.misharetask.MiShareTaskManager;
import com.cmmobi.looklook.prompt.Prompt;
import com.cmmobi.looklook.prompt.TickUpHelper;
import com.cmmobi.looklook.wheeltime.WheelCapsuleTime;
import com.cmmobi.statistics.CmmobiClickAgent;
import com.google.gson.Gson;
import com.iflytek.msc.ExtAudioRecorder;
import com.iflytek.msc.QISR_TASK;

public class ShareDiaryActivity extends ZActivity implements OnImportClickListener, OnLongClickListener, OnTouchListener, OnPoiSearchListener{

	private static final String TAG = ShareDiaryActivity.class.getSimpleName();
	
	public static final String INTENT_ACTION_SHARE_DIARY_LIST = "intent_action_share_diary_list";
	
//	public static final String BUNDLE_USER_LIST = "bundle_user_list";
	
	private final int HANDLER_RECORD_STOP_TIME_DELAY = 0x87654008;
	private final int HANDLER_SHOW_SOFT_INPUT = 0x87654009;
	private final int HANDLER_SHOW_SCROLL_TOP = 0x87654010;
	private final int HANDLER_SHOW_SCROLL_BOTTOM = 0x87654012;
	private final int HANDLER_SEND_EVENT = 0x87654013;
	private HashMap<String, StringBuilder> audioMap = new HashMap<String, StringBuilder>();
	private String RECORD_FILE_DIR = "";
	
	/*@Override
	public int subContentViewId() {
		return R.layout.activity_share_diary;
	}*/

	public static final int REQUEST_CODE_DIARY = 0;
	public static final int REQUEST_CODE_USER = 1;
	public static final int REQUEST_CODE_TENCENT = 2;
	public static final int REQUEST_CODE_SINA = 3;
	public static final int REQUEST_CODE_RENREN = 4;
	public static final int REQUEST_CODE_DELETE = 5;
	
	public static final String BUNDLE_DELELE_UUID = "bundle_delele_uuid";
	public static final String BUNDLE_DELELE_INDEX = "bundle_delele_index";
	
	private int vsharetitlemaxLen = 14;
	private int vsharemaxLen = 200;
	private DiaryImportShareView importShareView;
	
	private EditText editShareTitleStr;
	private EditText editShareStr;
	private TextView tvAtFriends;
	private TextView tvShareTitleLength;
	private TextView tvShareLength;
	private LinearLayout llyShareGps;
	private TextView tvGpsStreet;
	private ToggleButton tgbShowGps;
	private ToggleButton tgbTimeCapsule;
	private AddExpressionView expressionView;
	private ImageView ivExpressionBtn;
	private ImageView ivBurnAfterRead;
	private TextView tvBurnAfterRead;
	private LinearLayout llBurnAfterRead;
	//private ImageView ivSaveAsForever;
	private PopupWindow pw_timecapsure; //设置时光胶囊
	private TextView tvCommit;// 完成
	private TextView tvTimeCapsure;//时光胶囊时间
	private String strTimeCapsure;
	private WheelCapsuleTime timeCapsureWheel;
	
	private ScrollView mScrollView = null;
	
	private MyDiary[] diarydefault = null;
	public static ArrayList<MyDiary> diaryGroup = new ArrayList<MyDiary>();
	// 标记非本地日记
	public static Set<MyDiary> diaryuuid = new HashSet<MyDiary>();
	
//	private Set<String> userGroup = new HashSet<String>();
	
	private Set<UserObj> userGroup = new HashSet<UserObj>();
	
	private String mUserFrom = "2"; // 1点击加号 2@通讯录
	private Button btnBack = null;
	private Button btnPublish = null;
	
//	private View curView;
//	private int curDiary;
	
	private ExtAudioRecorder ear;
	private boolean isLongPressed = false;
	private boolean isOnRecorderButton = false;
//	private CountDownPopupWindow shortRecView;
	private VolumeStateView shortRecView;
	
	private ImageView mBtnRecorder;
	
	private int mShareType = 0;
	ArrayList<SNS> snsListNew = null;
	
	private MicListItem mMicItem = null;

	private String userID = null;
	
	private int mCurrentEdit = 0; // 0 titel 1 content
	
	private final int STATE_BURN_AFTER_READ = 1;
	private final int STATE_SAVE_AS_FOREVER = 0;
	private int state_delete = STATE_SAVE_AS_FOREVER;
	private ImageView ivCover = null;
	private int coverIndex = 0;
	
	public static class ShareMessage
	{
		public String diary_id = ""; // 日记ID，如果是多个日记新建组微享多个日记id以“4433121,4433121”形式
		public String diaryuuids = ""; // diaryids不存在 依赖uuid查缓存
		public String uuid = "";
		public String mic_title = "";
		public String content = ""; // 微享内容
		public UserObj[] userobj; // 微享用户组IDS AAA|BBB|CCC|DDD
		public String longitude = "";
		public String latitude = "";
		public String position = "";
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
//		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_share_diary);
		
		userID  = ActiveAccount.getInstance(ZApplication.getInstance()).getUID();
		
		// 默认好友
		UserObj[] userstr = new Gson().fromJson(getIntent().getStringExtra(INTENT_ACTION_SHARE_DIARY_LIST), UserObj[].class);
		
		userGroup.clear();
		if(userstr != null)
		{
			mUserFrom = "1";
			for(UserObj s : userstr)
			{
				if (s != null && !userID.equals(s.userid)) //过滤自己
				{
					userGroup.add(s);
				}
			}
		}
		
		ivCover = (ImageView) findViewById(R.id.cover);
		btnBack = (Button) findViewById(R.id.btn_title_left);
		btnPublish = (Button) findViewById(R.id.btn_title_right);
		btnPublish.setOnClickListener(this);
		
		final String KEY = "IS_VSHARE_FIRST";
		SharedPreferences perferences = PreferenceManager.getDefaultSharedPreferences(this);
		boolean isFirst = perferences.getBoolean(KEY, true);
		
		final String VERSION_KEY = "VERSION_NUM";
		String oldVersionCode = perferences.getString(VERSION_KEY, "");
		String newVersionCode = "";
		
		try {
			newVersionCode = this.getPackageManager().getPackageInfo(AboutActivity.APK_VERSION, 0).versionName;
		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		if (isFirst || !oldVersionCode.equals(newVersionCode)) {
			SharedPreferences.Editor editor = perferences.edit();
			editor.putBoolean(KEY, false);
			editor.putString(VERSION_KEY, newVersionCode);
			editor.commit();
			ivCover.setVisibility(View.VISIBLE);
			ivCover.setBackgroundResource(R.drawable.android_zy_sgjn);
			ivCover.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					if (coverIndex == 0) {
						ivCover.setBackgroundResource(R.drawable.android_zy_yhjf);
					} else {
						ivCover.setVisibility(View.GONE);
					}
					coverIndex = 1;
				}
			});
		} else {
			ivCover.setVisibility(View.GONE);
		}
		
//		showRightButton();
//		setTitle("微享");
//		setRightButtonText("发送");
//		getRightButton().setBackgroundResource(R.drawable.transparent);
		
		btnBack.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Prompt.Dialog(ShareDiaryActivity.this, true, "提示", "是否放弃本次编辑", new DialogInterface.OnClickListener(){

					@Override
					public void onClick(DialogInterface dialog,
							int which) {
						// TODO Auto-generated method stub
						handler.postDelayed(new Runnable() {
							public void run() {
								ShareDiaryActivity.this.finish();
							}

						}, 50);
					}		
				});
			}
		});
		
		// 分享的文字内容
		editShareTitleStr = (EditText)findViewById(R.id.edit_share_str_title);
		editShareStr = (EditText)findViewById(R.id.edit_share_str);
		// 分享的文字长度
		tvShareTitleLength = (TextView)findViewById(R.id.tv_share_str_title_lenght);
		tvShareLength = (TextView)findViewById(R.id.tv_share_str_lenght);
		
		// 导入按钮
		importShareView = (DiaryImportShareView) findViewById(R.id.lly_share_diarys);
		importShareView.setOnImportClickListener(this);
		
		// @好友 edit
		tvAtFriends = (TextView) findViewById(R.id.tv_at_friends);
		tvAtFriends.setOnClickListener(this);
		
		//tvAtFriends.setText(getUserText());
		FriendsExpressionView.replacedExpressions(getUserText(), tvAtFriends);
		
		findViewById(R.id.btn_at_friends).setOnClickListener(this);
		findViewById(R.id.btn_clear).setOnClickListener(this);
		
		mScrollView = (ScrollView) findViewById(R.id.sl_scroll);
		
		// gps
		llyShareGps = (LinearLayout)findViewById(R.id.lly_share_gps);
		tvGpsStreet = (TextView)findViewById(R.id.tv_gps_street);
		tgbShowGps = (ToggleButton)findViewById(R.id.tgb_show_gps);
		tgbShowGps.setChecked(false);
		tgbShowGps.setOnCheckedChangeListener(new OnCheckedChangeListener()
		{
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
			{
				if(isChecked) {
					tvGpsStreet.setText(CommonInfo.getInstance().positionStr);
				}
				else
				{
					tvGpsStreet.setText(null);
				}
			}
		});
		
		tvTimeCapsure = (TextView) findViewById(R.id.tv_time_capsure_time);
		tvTimeCapsure.setOnClickListener(this);
		initTimeCapsureChoice();
		tgbTimeCapsule = (ToggleButton) findViewById(R.id.tgb_show_time_capsule);
		tgbTimeCapsule.setChecked(false);
		tgbTimeCapsule.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				// TODO Auto-generated method stub
				if (isChecked) {
					pw_timecapsure.showAtLocation(findViewById(R.id.rl_share_diary), Gravity.BOTTOM, 0, 0);
				} else {
					tvTimeCapsure.setVisibility(View.GONE);
					tvTimeCapsure.setText(null);
				}
			}
		});
		
		editShareStr.setOnFocusChangeListener(new OnFocusChangeListener()
		{
			
			@Override
			public void onFocusChange(View v, boolean hasFocus)
			{
				if(hasFocus)
				{
					mCurrentEdit = 1;
					// 表情
					expressionView = new AddExpressionView(ShareDiaryActivity.this,(EditText)v);
					expressionView.setOnclickListener(new OnItemClickListener() {
						@Override
						public void onItemClick(AdapterView<?> parent, View view, int position,
								long id) {
							editShareStr.append(view.getTag().toString());
	//						FriendsExpressionView.replacedExpressions(editShareStr.getText().toString(), editShareStr);
						}
					});
					handler.sendEmptyMessageDelayed(HANDLER_SHOW_SCROLL_TOP, 200);
				}
				expressionView.hideExpressionView();
				ivExpressionBtn.setImageResource(R.drawable.btn_biaoqing_input);
			}
		});
		
		editShareStr.setOnClickListener(new OnClickListener()
		{
			
			@Override
			public void onClick(View v)
			{
				if(expressionView != null)
				{
					expressionView.hideExpressionView();
					ivExpressionBtn.setImageResource(R.drawable.btn_biaoqing_input);
				}
				handler.sendEmptyMessageDelayed(HANDLER_SHOW_SCROLL_TOP, 200);
			}
		});
		
		editShareTitleStr.setOnFocusChangeListener(new OnFocusChangeListener()
		{
			
			@Override
			public void onFocusChange(View v, boolean hasFocus)
			{
				if(hasFocus)
				{
					mCurrentEdit = 0;
					// 表情
					expressionView = new AddExpressionView(ShareDiaryActivity.this,(EditText)v);
					expressionView.setOnclickListener(new OnItemClickListener() {
						@Override
						public void onItemClick(AdapterView<?> parent, View view, int position,
								long id) {
							editShareTitleStr.append(view.getTag().toString());
	//						FriendsExpressionView.replacedExpressions(editShareTitleStr.getText().toString(), editShareTitleStr);
						}
					});
					handler.sendEmptyMessageDelayed(HANDLER_SHOW_SCROLL_BOTTOM, 200);
				}
				expressionView.hideExpressionView();
				ivExpressionBtn.setImageResource(R.drawable.btn_biaoqing_input);
			}
		});
		
		editShareTitleStr.setOnClickListener(new OnClickListener()
		{
			
			@Override
			public void onClick(View v)
			{
				if(expressionView != null)
				{
					expressionView.hideExpressionView();
					ivExpressionBtn.setImageResource(R.drawable.btn_biaoqing_input);
				}
				handler.sendEmptyMessageDelayed(HANDLER_SHOW_SCROLL_BOTTOM, 200);
			}
		});
		
		editShareTitleStr.addTextChangedListener(new TextWatcher()
		{
			
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count)
			{
				
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after)
			{
				
			}
			
			@Override
			public void afterTextChanged(Editable s)
			{
				String str = s.toString();
				if(str.contains("\r"))
				{
					str = str.replaceAll("\r", "");
					editShareTitleStr.setText(str);
					editShareTitleStr.setSelection(str.length());
				}
				if(str.contains("\n"))
				{
					str = str.replaceAll("\n", "");
					editShareTitleStr.setText(str);
					editShareTitleStr.setSelection(str.length());
				}
				if(str.contains(" "))
				{
					str = str.replaceAll(" ", "");
					editShareTitleStr.setText(str);
					editShareTitleStr.setSelection(str.length());
				}
			}
		});
		
		// 表情文字切换
		ivExpressionBtn = (ImageView) findViewById(R.id.iv_biaoqing_input);
		ivExpressionBtn.setOnClickListener(this);
		
		findViewById(R.id.btn_title_left).setOnLongClickListener(this);
		
		mBtnRecorder = (ImageView) findViewById(R.id.iv_recorder);
		mBtnRecorder.setOnLongClickListener(this);
		mBtnRecorder.setOnTouchListener(this);
		// 录音
		LocalBroadcastManager.getInstance(this).registerReceiver(
				mMessageReceiver,
				new IntentFilter(ExtAudioRecorder.AUDIO_RECORDER_MSG));
		LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver,
			      new IntentFilter(QISR_TASK.QISR_RESULT_MSG));
		ear = ExtAudioRecorder.getInstanse(false, 2, 1);
		
		// 获取分享类型
		mShareType = getIntent().getIntExtra(ShareDialog.TYPY_SHARE, 0);
		
		String str = getIntent().getStringExtra(DiaryPreviewActivity.INTENT_ACTION_DIARY_STRING);
		diarydefault = new Gson().fromJson(str, MyDiary[].class);
		
		ivBurnAfterRead = (ImageView) findViewById(R.id.iv_burn_after_read);
		tvBurnAfterRead = (TextView) findViewById(R.id.tv_burn_after_read);
		llBurnAfterRead = (LinearLayout) findViewById(R.id.ll_burn_after_read);
		llBurnAfterRead.setOnClickListener(this);
		
		/*ivSaveAsForever = (ImageView) findViewById(R.id.iv_save_as_forever);
		ivSaveAsForever.setOnClickListener(this);
		*/
//		if(diarydefault == null || diaryList == null)
//		{
//			Log.v(TAG, "no diary or diarylist");
//			finish();
//			return;
//		}
		
		// 不是自己的日记或者是组日记 不能编辑日记数量
//		if(!isMyself() || "1".equals(diaryList.isgroup))
//		{
//			importShareView.setImportBtn(false);
//		}
		
		diaryGroup.clear();
		// 标记非本地日记
		//diaryuuid.clear();
		if (diarydefault != null)
		{
			for (int i = 0; i < diarydefault.length; i ++)
			{
				diaryGroup.add(diarydefault[i]);
				
				ContentThumbnailView view = new ContentThumbnailView(this);
				LayoutParams params = new LayoutParams(importShareView.getInnerLlyHeight(this), importShareView.getInnerLlyHeight(this));
				view.setLayoutParams(params);
				view.setContentDiaries("0", diarydefault[i]);
				view.setTag(diaryGroup.get(i));
				view.setOnClickListener(new OnClickListener()
				{
					
					@Override
					public void onClick(View v)
					{
	//					importShareView.removieDiaryView(v);
	//					diaryGroup.remove(v.getTag());
	//					setPosition();
						String uuid = ((MyDiary) v.getTag()).diaryuuid;
						
						Intent intent = new Intent(ShareDiaryActivity.this, DiaryPreviewActivity.class);
						intent.putExtra(DiaryPreviewActivity.INTENT_ACTION_DIARY_UUID, uuid);
						DiaryManager.getInstance().setmMyDiaryBuf(diaryGroup);
						intent.putExtra(DiaryPreviewActivity.INTENT_ACTION_SHOW_MODE, DiaryPreviewActivity.SHOW_MODE_DELETE);
						startActivityForResult(intent, REQUEST_CODE_DELETE);
					}
				});
				importShareView.addElemView(view);
			}
		}
		
		Set<MyDiary> diaryuuidNew = new HashSet<MyDiary>();
		if(diaryuuid!=null && !diaryuuid.isEmpty()){
			Iterator<MyDiary> iterator = diaryuuid.iterator();
			while(iterator.hasNext()){
				MyDiary diary = iterator.next();
				if(diaryGroup.contains(diary)){
					diaryuuidNew.add(diary);
				}
			}
			diaryuuid.clear();
			diaryuuid.addAll(diaryuuidNew);
			System.out.println("  is ==== contain ==" + diaryuuid.contains(diaryGroup.get(0)));
		}
		
		
		
		setPosition();
		
		Spannable WordtoSpan = new SpannableString(getString(R.string.share_friend_tips));
		WordtoSpan.setSpan(new AbsoluteSizeSpan(18, true), 0, 3, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		WordtoSpan.setSpan(new AbsoluteSizeSpan(12, true), 3, 17, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		tvAtFriends.setHint(WordtoSpan);
		setTitle("微享");
		switch(mShareType)
		{
		case ShareDialog.TYPE_SHARE_V:
			tvShareLength.setText("0"+ "/" + vsharemaxLen);
			tvShareTitleLength.setText("0"+ "/" + vsharetitlemaxLen);
			
			editShareStr.addTextChangedListener(
					new TextNumWatcher(editShareStr,tvShareLength,vsharemaxLen));
			
			editShareTitleStr.addTextChangedListener(
					new TextNumWatcher(editShareTitleStr,tvShareTitleLength,vsharetitlemaxLen));
			
			
			break;
		}
		
	}
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.tv_at_friends:
		case R.id.btn_at_friends:  // 跳转联系人
			
//			ArrayList<String> listStr = new ArrayList<String>();
//			listStr.addAll(userGroup);
			
			ArrayList<UserObj> listStr = new ArrayList<UserObj>();
			listStr.addAll(userGroup);
			
			Intent intent = new Intent(this, ShareLookLookFriendsActivity.class);
//			intent.putStringArrayListExtra(INTENT_ACTION_SHARE_DIARY_LIST, listStr);
			intent.putParcelableArrayListExtra(INTENT_ACTION_SHARE_DIARY_LIST, listStr);
			startActivityForResult(intent, REQUEST_CODE_USER);
			break;
		case R.id.btn_clear:
			tvAtFriends.setText("");
			userGroup.clear();
			break;
		case R.id.btn_title_right:  // 发送
			if (!isFastDoubleClick()) {
				break;
			}
			Log.v(TAG, "send click");
			
			/*if (editShareTitleStr.getText().toString().equals(""))
			{
				new Xdialog.Builder(this)
				.setMessage("您还未输入主题")
				.setPositiveButton("输入主题", new DialogInterface.OnClickListener()
				{
					
					@Override
					public void onClick(DialogInterface dialog, int which)
					{
						editShareTitleStr.requestFocus();
						
						handler.sendEmptyMessageDelayed(HANDLER_SHOW_SOFT_INPUT, 100);

					}
				})
				.create().show();

				ZDialog.dismiss();
				break;
			}*/
			
			if (tvAtFriends.getText().toString().equals(""))
			{
				new Xdialog.Builder(this)
//				.setMessage(getString(R.string.share_error_nofriend))
				.setMessage("请至少@一位好友")
				.setNegativeButton(android.R.string.ok, null)
				.create().show();
				
				ZDialog.dismiss();
				break;
			}
			
			if (diaryGroup.size() == 0)
			{
				new Xdialog.Builder(this)
				.setMessage(getString(R.string.share_error_nodiary))
				.setNegativeButton(android.R.string.ok, null)
				.create().show();
				
				ZDialog.dismiss();
				break;
			}
			
			
			if (tgbTimeCapsule.isChecked()) {
				Date date = timeCapsureWheel.getTimeCapsure();
				long nowTime = TimeHelper.getInstance().now();
				Date now = new Date(nowTime);
				Log.d(TAG,"date = " + date + " now = " + now);
				if (now.after(date)) {
					new Xdialog.Builder(this)
					.setMessage(getString(R.string.share_error_time_capsure))
					.setNegativeButton(android.R.string.ok, null)
					.create().show();
					ZDialog.dismiss();
					break;
				}
			}
			
			ZDialog.show(R.layout.progressdialog, false, true, this);
			handler.sendEmptyMessage(HANDLER_SEND_EVENT);
			
			break;
		case R.id.iv_biaoqing_input:
			View exview = expressionView.getExpressionView();
			if (View.GONE != exview.getVisibility()) {
				expressionView.hideExpressionView();
				ivExpressionBtn.setImageResource(R.drawable.btn_biaoqing_input);
			} else {
				expressionView.showExpressionView(200);
				ivExpressionBtn.setImageResource(R.drawable.btn_wenzi_input);
			}
			if (mCurrentEdit == 0)
			{
				handler.sendEmptyMessageDelayed(HANDLER_SHOW_SCROLL_BOTTOM, 200);
			}
			else
			{
				handler.sendEmptyMessageDelayed(HANDLER_SHOW_SCROLL_TOP, 200);
			}
			break;
		case R.id.ll_burn_after_read:
			if (state_delete != STATE_BURN_AFTER_READ) {
				ivBurnAfterRead.setImageResource(R.drawable.yhjf_fb_2);
				tvBurnAfterRead.setTextColor(Color.RED);
				state_delete = STATE_BURN_AFTER_READ;
			}else{
				ivBurnAfterRead.setImageResource(R.drawable.yhjf_fb_1);
				tvBurnAfterRead.setTextColor(Color.BLACK);
				state_delete = STATE_SAVE_AS_FOREVER;
			}
			
			break;
		/*case R.id.iv_save_as_forever:
			if (state_delete != STATE_SAVE_AS_FOREVER) {
				ivBurnAfterRead.setImageResource(R.drawable.xuanzhong_1);
				ivSaveAsForever.setImageResource(R.drawable.xuanzhong_2);
			}
			state_delete = STATE_SAVE_AS_FOREVER;
			break;*/
		case R.id.tv_commit_time:
			Date now = new Date();
			if (timeCapsureWheel.getTimeCapsure().before(now)) {
				new Xdialog.Builder(this)
				.setMessage(getString(R.string.share_error_time_capsure))
				.setNegativeButton(android.R.string.ok, null)
				.create().show();
			} else {
				tvTimeCapsure.setVisibility(View.VISIBLE);
				tvTimeCapsure.setText(timeCapsureWheel.getTimeDescription());
				pw_timecapsure.dismiss();
			}
			break;
		case R.id.tv_back_time:
			pw_timecapsure.dismiss();
			tgbTimeCapsule.setChecked(false);
			break;
		case R.id.tv_time_capsure_time:
			pw_timecapsure.showAtLocation(findViewById(R.id.rl_share_diary), Gravity.BOTTOM, 0, 0);
			break;
		default:
			break;
		}
	}
	
	//初始化时光胶囊时间选择界面
	private void initTimeCapsureChoice() {
		LayoutInflater inflater = LayoutInflater.from(this);
		View view = inflater.inflate(R.layout.popupwindow_setting_time_capsule, null);
		pw_timecapsure = new PopupWindow(view, LayoutParams.FILL_PARENT,
				LayoutParams.FILL_PARENT, true);
		pw_timecapsure.setBackgroundDrawable(getResources().getDrawable(
				R.color.transparent));
		timeCapsureWheel = new WheelCapsuleTime(view);
		timeCapsureWheel.initDateTimePicker();
		
		tvCommit = (TextView) view.findViewById(R.id.tv_commit_time);
		tvCommit.setOnClickListener(this);
		view.findViewById(R.id.tv_back_time).setOnClickListener(this);
	}
	
	private static long lastClickTime;

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
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		switch(requestCode)
		{
		case REQUEST_CODE_DIARY:
			if(resultCode == RESULT_OK)
			{
//				diaryGroup.clear();
				importShareView.removieAllDiaryView();
				for(int i = 0; i < diaryGroup.size(); i++)
				{
					ContentThumbnailView view = new ContentThumbnailView(this);
					LayoutParams params = new LayoutParams(importShareView.getInnerLlyHeight(this), importShareView.getInnerLlyHeight(this));
					view.setLayoutParams(params);
					view.setContentDiaries("0", diaryGroup.get(i));
					view.setTag(diaryGroup.get(i));
					view.setOnClickListener(new OnClickListener()
					{
						
						@Override
						public void onClick(View v)
						{
//							importShareView.removieDiaryView(v);
//							diaryGroup.remove(v.getTag());
//							setPosition();
							String uuid = ((MyDiary) v.getTag()).diaryuuid;
							
							Intent intent = new Intent(ShareDiaryActivity.this, DiaryPreviewActivity.class);
							intent.putExtra(DiaryPreviewActivity.INTENT_ACTION_DIARY_UUID, uuid);
							DiaryManager.getInstance().setmMyDiaryBuf(diaryGroup);
							intent.putExtra(DiaryPreviewActivity.INTENT_ACTION_SHOW_MODE, DiaryPreviewActivity.SHOW_MODE_DELETE);
							startActivityForResult(intent, REQUEST_CODE_DELETE);
						}
					});
					importShareView.addElemView(view);
				}
				
//				setPosition();
				
			}
			break;
		case REQUEST_CODE_USER:
			if(resultCode == RESULT_OK)
			{
				userGroup.clear();
				ArrayList<UserObj> nameList = data.getParcelableArrayListExtra("invite_list");
				if (nameList != null && nameList.size() > 0)
				{
					for (int i = 0; i < nameList.size(); i++)
					{
						UserObj id = nameList.get(i);
						userGroup.add(id);
					}
				}
				//tvAtFriends.setText(getUserText());
				FriendsExpressionView.replacedExpressions(getUserText(), tvAtFriends);
				
				// 统计
				CmmobiClickAgentWrapper.onEvent(this, "micro_contact", "" + nameList.size());
			}
			break;
		case REQUEST_CODE_DELETE:
			if(resultCode == RESULT_OK)
			{
//				String uuid = data.getStringExtra(BUNDLE_DELELE_UUID);
				int index = data.getIntExtra(BUNDLE_DELELE_INDEX, 0);
				// 删除
				Iterator<MyDiary> ite = diaryGroup.iterator();
				int i = 0;
				while(ite.hasNext())
				{
//					if(ite.next().diaryuuid.equals(uuid))
//					{
//						ite.remove();
//					}
					ite.next();
					if(i == index)
					{
						ite.remove();
					}
					i ++;
				}
				
				importShareView.removieAllDiaryView();
				for(i = 0; i < diaryGroup.size(); i++)
				{
					ContentThumbnailView view = new ContentThumbnailView(this);
					LayoutParams params = new LayoutParams(importShareView.getInnerLlyHeight(this), importShareView.getInnerLlyHeight(this));
					view.setLayoutParams(params);
					view.setContentDiaries("0", diaryGroup.get(i));
					view.setTag(diaryGroup.get(i));
					view.setOnClickListener(new OnClickListener()
					{
						
						@Override
						public void onClick(View v)
						{
//							importShareView.removieDiaryView(v);
//							diaryGroup.remove(v.getTag());
//							setPosition();
							String uuid = ((MyDiary) v.getTag()).diaryuuid;
							
							Intent intent = new Intent(ShareDiaryActivity.this, DiaryPreviewActivity.class);
							intent.putExtra(DiaryPreviewActivity.INTENT_ACTION_DIARY_UUID, uuid);
							DiaryManager.getInstance().setmMyDiaryBuf(diaryGroup);
							intent.putExtra(DiaryPreviewActivity.INTENT_ACTION_SHOW_MODE, DiaryPreviewActivity.SHOW_MODE_DELETE);
							startActivityForResult(intent, REQUEST_CODE_DELETE);
						}
					});
					importShareView.addElemView(view);
				}
				
//				setPosition();
			}
			break;
		}
		super.onActivityResult(requestCode, resultCode, data);
	}
	
	@Override
	public boolean onLongClick(View v) {
		switch(v.getId()) {
		case R.id.iv_recorder:
			String audioID = DiaryController.getNextUUID();
			audioMap.put(audioID, new StringBuilder(""));
			RECORD_FILE_DIR =  Constant.SD_STORAGE_ROOT + "/" + ActiveAccount.getInstance(this).getLookLookID() + "/audio";
			
			ear.setHandler(handler);
			ear.start(audioID,RECORD_FILE_DIR, false, 3, true);
//			isRecording = true;
			isLongPressed = true;
			isOnRecorderButton = true;
//			handler.sendEmptyMessage(HANDLER_RECORD_DURATION_UPDATE_MSG);
			if (shortRecView == null) {
				shortRecView = new VolumeStateView(this);
			}
			shortRecView.show();
			
//			mBtnRecorder.setImageResource(R.drawable.btn_press_speek_press);
			mBtnRecorder.setImageResource(R.drawable.skjs);
			break;
		case R.id.btn_title_left:
			Intent intent = new Intent(this,LookLookActivity.class);
			intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(intent);
			finish();
			break;
		}
		
		return false;
	}
	
	@Override
	public boolean onTouch(View view, MotionEvent event) {
		if (isLongPressed) {
			int action = event.getAction();
			if (action == MotionEvent.ACTION_DOWN) {
				Log.d(TAG,"ACTION_DOWN");
			} else if (action == MotionEvent.ACTION_UP) {
				Log.d(TAG,"ACTION_UP");
				TickUpHelper.getInstance(handler).stop(2);
				mBtnRecorder.setImageResource(R.drawable.btn_press_speek_selector);

				CmmobiClickAgent.onEventEnd(this, "record_voice");
			} else if(action == MotionEvent.ACTION_MOVE){
				if (event.getY() < 0) {
					if (isOnRecorderButton) {
						shortRecView.showCancle();
					}
					isOnRecorderButton = false;
				} else {
					if (!isOnRecorderButton) {
						shortRecView.showVolume();
					}
					isOnRecorderButton = true;
				}
				Log.d(TAG,"Y = " + event.getY() + " isOnRecorderButton = " + isOnRecorderButton);
			}
		}
		return false;
	}
	
	private void stopShortRecoder() {
		isLongPressed = false;
		if (ear != null && ear.isRecording) {
			isLongPressed = false;
			ear.stop();
			if (shortRecView != null) {
				shortRecView.dismiss();
			}
		}
//		ivRecord.setVisibility(View.GONE);
	}
	
	@Override
	public boolean handleMessage(Message msg) {
		switch(msg.what)
		{
		case HANDLER_SHOW_SOFT_INPUT:
			editShareTitleStr.requestFocus();
			// 表情
			expressionView = new AddExpressionView(ShareDiaryActivity.this,editShareTitleStr);
			expressionView.setOnclickListener(new OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> parent, View view, int position,
						long id) {
					editShareTitleStr.append(view.getTag().toString());
//					FriendsExpressionView.replacedExpressions(editShareTitleStr.getText().toString(), editShareTitleStr);
				}
			});
			expressionView.hideExpressionView();
			break;
		case Requester3.RESPONSE_TYPE_CREATEMIC:
			if(msg.obj != null)
			{
				Log.v(TAG, "1" + ((createMicResponse)msg.obj).crm_status);
				Log.v(TAG, "2" + ((createMicResponse)msg.obj).status);
				Log.v(TAG, "3" + ((createMicResponse)msg.obj).publishid);
				Log.v(TAG, "4" + ((createMicResponse)msg.obj).uuid);
			}
			break;
		case HANDLER_RECORD_STOP_TIME_DELAY:
			stopShortRecord();
			break;
		case ExtAudioRecorder.HANDLER_AUDIO_RECORDER_DONE:
			String audioID = (String)msg.obj;
			Log.d(TAG,"audio " + audioID + " done");
			String shortPath = RECORD_FILE_DIR + "/" + audioID + ".mp4";
			AccountInfo.getInstance(userID).mediamapping.delMedia(userID, shortPath,true);
			break;
		case QISR_TASK.HANDLER_QISR_RESULT_ADD:
			if (msg != null && msg.obj != null) {
				Bundle data = msg.getData();
				String audioid = data.getString("audioid");
				StringBuilder str = audioMap.get(audioid);
				Log.v(TAG, "text = " + (String)msg.obj);
				if(str != null)
				{
					str.append((String)msg.obj);
				}
			}
			break;
		case QISR_TASK.HANDLER_QISR_RESULT_CLEAN:
			if (msg != null && msg.obj != null) {
				Bundle data = msg.getData();
				String audioid = data.getString("audioid");
				StringBuilder str  = audioMap.get(audioid);
				str.delete(0, str.length());
				Log.v(TAG, "text clear");
			}
			break;
		case QISR_TASK.HANDLER_QISR_RESULT_DONE:
		{
			try {
				Bundle data = msg.getData();
				String audioid = data.getString("audioid");
				StringBuilder str = audioMap.get(audioid);
				
	//			editShareStr.setText(str);
				
				EditText et  = null;
				if(mCurrentEdit == 0)
				{
					et = editShareTitleStr;
				}
				else
				{
					et = editShareStr;
				}
				
				Editable editable = et.getText();
				int start = et.getSelectionStart();
				int end = et.getSelectionEnd();
				if(start == end)
				{
					editable.insert(start, str);
				}
				else
				{
					editable.replace(start, end, str);
				}
				Log.v(TAG, "text = " + str);
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			if (isLongPressed) {
				return false;
			}
			break;
		}
		case TickUpHelper.HANDLER_FLAG_TICK_UP:
			int curTime = (Integer)msg.obj;
			Log.d(TAG,"TickUpHelper.HANDLER_FLAG_TICK_UP + curTime = " + curTime);
			if (isLongPressed) {
				shortRecView.updateTime(30 - curTime);
			}
			
			break;
		case TickUpHelper.HANDLER_FLAG_TICK_STOP:
			Log.d(TAG,"TickUpHelper.HANDLER_FLAG_TICK_STOP");
			stopShortRecoder();
			break;
		case HANDLER_SHOW_SCROLL_TOP:
			/*Log.v(TAG, "mScrollView.getHeight() = " + mScrollView.getHeight());
			mScrollView.scrollTo(0, mScrollView.getHeight() + 500);
			break;*/
		case HANDLER_SHOW_SCROLL_BOTTOM:
			mScrollView.scrollTo(0, 0);
			break;
		case HANDLER_SEND_EVENT:
		{
			mMicItem = getShareMessage();

			AccountInfo.getInstance(userID).vshareLocalDataEntities.insertMemberUuid(0, mMicItem);
			mMicItem.setUpload_status("0");
			
			MiShareTaskInfo info = new MiShareTaskInfo(mMicItem);
			final MiShareTask task = new MiShareTask(info);
			long size = task.getMiShareSize();
//			long size = 1024 * 1024;
			
			if(!ZNetworkStateDetector.isConnected()){
				Prompt.Alert("网络不给力！");
				updateRecentContacts(mMicItem.userobj);
				MiShareTaskManager.getInstance(userID).addMiShareTask(task);
//				MiShareTaskManager.getInstance(userID).startMiShareTask(task);
				MiShareTaskManager.getInstance(userID).startMiShareTaskFromShareDiary(task, true);
				sendMsg();
				ZDialog.dismiss();
				finish();
			}else if (size > 500 * 1024l && ZNetworkStateDetector.isMobile())
			{
				String str = getShareSize(size);
				
				Spannable WordtoSpan = new SpannableString("发送该内容需要" + str + "流量");
				WordtoSpan.setSpan(new ForegroundColorSpan(Color.BLACK), 0, 7, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
				WordtoSpan.setSpan(new ForegroundColorSpan(Color.rgb(0x00, 0x7a, 0xff)), 7, 7 + str.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
				WordtoSpan.setSpan(new ForegroundColorSpan(Color.BLACK), 7 + str.length(), 8 + str.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
				new Xdialog.Builder(this)
				.setMessage(SpannableStringBuilder.valueOf(WordtoSpan))
				.setPositiveButton("立即上传", new DialogInterface.OnClickListener()
				{
					
					@Override
					public void onClick(DialogInterface dialog, int which)
					{
						updateRecentContacts(mMicItem.userobj);
						MiShareTaskManager.getInstance(userID).addMiShareTask(task);
//						MiShareTaskManager.getInstance(userID).startMiShareTask(task);
						MiShareTaskManager.getInstance(userID).startMiShareTaskFromShareDiary(task, true);
						
						sendMsg();
						ZDialog.dismiss();
						finish();
						
					}
				})
				.setNegativeButton("WIFI状态时上传", new DialogInterface.OnClickListener()
				{
					
					@Override
					public void onClick(DialogInterface dialog, int which)
					{
						updateRecentContacts(mMicItem.userobj);
						MiShareTaskManager.getInstance(userID).addMiShareTask(task);
//						MiShareTaskManager.getInstance(userID).startMiShareTask(task);
						MiShareTaskManager.getInstance(userID).startMiShareTaskFromShareDiary(task, false);
						
						sendMsg();
						ZDialog.dismiss();
						finish();
						
					}
				})
				.create().show();
				ZDialog.dismiss();
				break;
			}
			else
			{
				updateRecentContacts(mMicItem.userobj);
				MiShareTaskManager.getInstance(userID).addMiShareTask(task);
				MiShareTaskManager.getInstance(userID).startMiShareTaskFromShareDiary(task, true);
				
				sendMsg();
				ZDialog.dismiss();
				finish();
			}
			
			// 统计
			HashMap<String,String> map = new HashMap<String, String>();
			String capsureFlag = tgbTimeCapsule.isChecked() ? "1":"0";
			String burnFlag = "" + state_delete;
			map.put("label", getNetWork(this));
			map.put("label2", burnFlag);
			map.put("label3", capsureFlag);
			CmmobiClickAgentWrapper.onEvent(this, "mic_sh_send", map);
			
			break;
		}
		}
		return false;
	}
	
	private void updateRecentContacts(UserObj[] userobj){
		AccountInfo accountInfo = null;
		try {
			accountInfo = AccountInfo.getInstance(ActiveAccount.getInstance(this).getUID());		
			if(accountInfo!=null){
				for(int i=0 ; i< userobj.length; i++){
					if(userobj[i].userid!=null && !userobj[i].userid.isEmpty()){
						accountInfo.recentContactManager.addRecentContact(accountInfo.friendsListName.findUserByUserid(userobj[i].userid));
					}else{
						for(int j=0; j< accountInfo.phoneUsers.getCache().size(); j++){
							WrapUser wrapUser = accountInfo.phoneUsers.getCache().get(j);
							if(userobj[i].user_telname.equals(wrapUser.phonename)&& userobj[i].user_tel.equals(wrapUser.phonenum)){
								accountInfo.recentContactManager.addRecentContact(wrapUser);
								break;
							}
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	@Override
	protected void onDestroy()
	{
		diaryGroup.clear();
		// 标记非本地日记
		diaryuuid.clear();
		MyAddressInfo.getInstance(this).removeListener(this);
		LocalBroadcastManager.getInstance(this).unregisterReceiver(mMessageReceiver);
		super.onDestroy();
	}

	private void stopShortRecord() {
		Log.v(TAG, "end record");
		ear.stop();
		isLongPressed = false;
	}
	
	// 获取分享日记信息
	private MicListItem getShareMessage()
	{
		MicListItem msg = new MicListItem();
		
		List<VshareDiary> listshare = new ArrayList<VshareDiary>();
		List<MyDiary> listlocal = new ArrayList<MyDiary>();
		for(MyDiary diary : diaryGroup)
		{
			VshareDiary v = new VshareDiary();
			v.diaryid = diary.diaryid;
			v.diaryuuid = diary.diaryuuid; //日记UUID
			v.type = diary.attachs.levelattach.attachtype; // 1视频、2音频 3图片(如果是单内容有效)
			if ("3".equals(v.type))
			{ // 只有图片
				String userId = ActiveAccount.getInstance(MainApplication.getAppInstance()).getUID();
				MediaValue mv = AccountInfo.getInstance(userId).mediamapping.getMedia(userId, diary.getMainUrl());
				if(MediaValue.checkMediaAvailable(mv))
				{
					v.imageurl = "file://"  + Environment.getExternalStorageDirectory()  + mv.localpath;
				}
				else
				{
					v.imageurl = diary.getMainUrl();
				}
			}
			else if ("1".equals(v.type))
			{ // 视频
				v.imageurl = diary.getVideoCoverUrl();
			}
			if("1".equals(v.type) || "2".equals(v.type))
			{
				v.playtime = diary.getMainPlaytime();
			}
			listshare.add(v);
			
			if(diaryuuid!=null && !diaryuuid.isEmpty()){
				Iterator<MyDiary> iterator = diaryuuid.iterator();
				while(iterator.hasNext()){
					MyDiary d = iterator.next();
					if(d.equals(diary)){
						listlocal.add(diary);
						System.out.println("  is ==0== contain ==" + diaryuuid.contains(diary));
						break;
					}
			}
			}
			/*if(diaryuuid.contains(diary))
			{
				listlocal.add(diary);
			}*/
		}
		msg.diarys = listshare.toArray(new VshareDiary[listshare.size()]);
		msg.diarysLocal = listlocal.toArray(new MyDiary[listlocal.size()]);
		
		msg.publishid = "";
		msg.uuid = UUID.randomUUID().toString().replace("-", "");
		msg.micuserid = ActiveAccount.getInstance(MainApplication.getAppInstance()).getUID();
		msg.headimageurl = AccountInfo.getInstance(msg.micuserid).headimageurl;
		msg.mic_safebox = "0";
//		msg.mic_title = editShareTitleStr.getText().toString();
//		msg.content = editShareStr.getText().toString();
		
		msg.mic_title = EmojiPaser.getInstance().format(editShareTitleStr.getText().toString());
		msg.content = EmojiPaser.getInstance().format(editShareStr.getText().toString());
		
		msg.commentnum = "";
		msg.is_undisturb = "0";
		msg.create_time = "" + TimeHelper.getInstance().now();
		msg.update_time = "" + TimeHelper.getInstance().now();
		msg.newcomment = getUserTextWithA();
		msg.micusernames = getUserTextWithA();
		msg.capsule = tgbTimeCapsule.isChecked() ? "1":"0";
		msg.burn_after_reading = "" + state_delete;
		msg.is_clear = "0";
//		msg.capsule_time = "" + (System.currentTimeMillis() + 2000*60);
		msg.capsule_time = "" + timeCapsureWheel.getTimeCapsure().getTime();
		Log.d(TAG,"capsure = " + msg.capsule + " burn_after_reading = " + msg.burn_after_reading + " capsule_time = " + msg.capsule_time);
		
		
		msg.userobj = new UserObj[userGroup.size() + 1];
		msg.userobj[0] = new UserObj();
		msg.userobj[0].userid = ActiveAccount.getInstance(MainApplication.getAppInstance()).getUID();
		msg.userobj[0].mic_source = "2"; //1点击加号 2@通讯录
		int i = 1;
//		for(String id : userGroup)
//		{
//			msg.userobj[i] = new UserObj();
//			msg.userobj[i].userid = id;
//			msg.userobj[i].mic_source = "2"; //1点击加号 2@通讯录
//			i++;
//		}
		
		for(UserObj id : userGroup)
		{
			msg.userobj[i] = id;
			msg.userobj[i].mic_source = mUserFrom;
			i++;
		}
		
		if(tgbShowGps.isShown() && tgbShowGps.isChecked())
		{
			msg.latitude = String.valueOf(CommonInfo.getInstance().myLoc.latitude * 1e6);
			msg.longitude = String.valueOf(CommonInfo.getInstance().myLoc.longitude * 1e6);
			msg.position = CommonInfo.getInstance().positionStr;
			msg.position_status = "1";
		}
		else
		{
			msg.position_status = "0";
		}
		
		msg.setUpload_status("0");
		
		return msg;
	}
	
	// 设置位置显示
	private void setPosition()
	{
//		if(mShareType == ShareDialog.TYPE_SHARE_V)
//		{
//			if(diaryGroup.size() == 1)
			{
				llyShareGps.setVisibility(View.VISIBLE);
				
//				tvGpsStreet.setText(CommonInfo.getInstance().positionStr);
				
				LocationData loc = MyLocationInfo.getInstance(this).getLocation();
				if (loc != null) {
					MyAddressInfo.getInstance(this).addListener(this);
			    	MyAddressInfo.getInstance(this).reverseGeocode((new GeoPoint((int)(loc.latitude*1e6), (int)(loc.longitude*1e6))));
				}

			}
//			else
//			{
//				llyShareGps.setVisibility(View.INVISIBLE);
//				tvGpsStreet.setText("");
//			}
//		}
//		else
//		{
//			llyShareGps.setVisibility(View.INVISIBLE);
//		}
		
//		llyShareGps.setVisibility(View.GONE);
	}
	
	/**
	 * 导入按钮
	 * 添加日记
	 */
	@Override
	public void onImportClick(View v) {
		
		Intent intent = new Intent(this, ShareSelectMenu.class);
		startActivityForResult(intent, REQUEST_CODE_DIARY);
	}
	
	// 获取looklook好友nickname
	private String getNickName(String id)
	{
		List<WrapUser> userList = AccountInfo.getInstance("").friendsListName.getCache();
		for(WrapUser wrap : userList)
		{
			if (wrap.userid != null && wrap.userid.equals(id))
			{
				if (wrap.nickname != null && "".equals(wrap.nickname.trim())) {
					return wrap.nickname;
				} else {
					return wrap.micnum;
				}
			}
		}
		return id;
	}
	
	/**
	 * EditText文字变化显示对应文字长度
	 * @author guoyang
	 */
	class TextNumWatcher implements TextWatcher{

		private EditText editText;
		private TextView tvNum;
		private int max;
		private String maxStr ;
		
		public TextNumWatcher(EditText editText, TextView tvNum, int max) {
			this.editText = editText;
			this.tvNum = tvNum;
			this.max = max;
			maxStr = "/" + max;
		}
		
		@Override
		public void beforeTextChanged(CharSequence s, int start, int count,
				int after) {
		}

		@Override
		public void onTextChanged(CharSequence s, int start, int before,
				int count) {
			//大于最大长度  
	        if(s.length() > max){  
	            CharSequence newStr = s.subSequence(0, max);  
	            editText.setText(newStr);
	            Selection.setSelection(editText.getEditableText(), max);  
	            tvNum.setText(max + maxStr);
	        }  
		}

		@Override
		public void afterTextChanged(Editable s) {
			tvNum.setText(editText.getEditableText().length() + maxStr);
		}
	}
	
	private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			int type;
			Message msg = new Message();
			String content = "";
			// Get extra data included in the Intent
			if (ExtAudioRecorder.AUDIO_RECORDER_MSG.equals(intent
					.getAction())) {
				type = intent.getIntExtra("type", 0);
				if (type == ExtAudioRecorder.HANDLER_AUDIO_RECORDER_DONE) {
					long duration = intent.getLongExtra("audioduration",0);
					Bundle b = new  Bundle();
				    b.putLong("audioduration", duration);
				    msg.setData(b);
				    content = intent.getStringExtra("content");
					Log.d(TAG,"onReceive content = " + duration);
				}
				msg.what = type;
				msg.obj = content;
			} else if (QISR_TASK.QISR_RESULT_MSG.equals(intent.getAction())){
				type = intent.getIntExtra("type", 0);
			    String message = intent.getStringExtra("content");
			    String audioID = intent.getStringExtra("audioID");
			    Bundle b = new  Bundle();
			    b.putString("msg", message);
			    b.putString("audioid", audioID);
			    msg.setData(b);
			    Log.d(TAG, "Got message: " + message);
			    msg.what = type;
			    msg.obj = message;
			}

			handler.sendMessage(msg);
		}
	};
	

	@Override
	public void onPoiSearch(List<POIAddressInfo> poiList)
	{
//		tvGpsStreet.setText(CommonInfo.getInstance().positionStr);
	}

	@Override
	public void onAddrSearch(MKAddrInfo res)
	{
//		tvGpsStreet.setText(CommonInfo.getInstance().positionStr);
	}
	
//	 //点击EditText以外的任何区域隐藏键盘
//    @Override
//    public boolean dispatchTouchEvent(MotionEvent ev) {
//        if (ev.getAction() == MotionEvent.ACTION_DOWN) {  
////            View v = inpRecoderView;
//        	View v = ivExpressionBtn;
//            if (isShouldHideInput(v, ev)) {
//                if(hideInputView()) {
//                    return true; //隐藏键盘时，其他控件不响应点击事件==》注释则不拦截点击事件
//                }
//            }
//        }
//        return super.dispatchTouchEvent(ev);   
//    }     
//    
//    public static boolean isShouldHideInput(View v, MotionEvent event) {
//        if (v != null/* && (v instanceof EditText)*/) {
//            int[] leftTop = { 0, 0 };
//            v.getLocationInWindow(leftTop);
//            int left = leftTop[0], top = leftTop[1], bottom = top + v.getHeight(), right = left
//                    + v.getWidth();
////            if (event.getX() > left && event.getX() < right
////                    && event.getY() > top && event.getY() < bottom) {
//            if (event.getY() > top) {
//                // 保留点击EditText的事件
//                return false;
//            } else {
//                return true;
//            }
//        }
//        return false;
//    }    
    
    private Boolean hideInputView(){
//		View exview = expressionView.getExpressionView();
//		if (View.GONE != exview.getVisibility()) {
			expressionView.hideExpressionViewOnly();
			expressionView.hideKeyboard();
			ivExpressionBtn.setImageResource(R.drawable.btn_biaoqing_input);
//		} else {
//			expressionView.showExpressionView();
//			ivExpressionBtn.setImageResource(R.drawable.btn_wenzi_input);
//		}
    	
//    	if(inpRecoderView.getVisibility() == View.VISIBLE){
//	        inpRecoderView.clearView();
////			cleanCmmData();
//			inpRecoderView.setVisibility(View.GONE);
//	    	return true;
//    	}
    	return false;
    }
    
    private String getShareSize(long size)
    {
    	if (size < 1024 * 1024)
    	{
    		return String.format("%d", size / 1024l) + "K";
    	}
    	else if(size < 10 * 1024 * 1024)
    	{
    		return String.format("%.1f", ((double)size) / 1024d / 1024d) + "M";
    	}
    	else
    	{
    		return String.format("%d", size / 1024l / 1024l) + "M";
    	}
    }
    
    private String getUserText()
    {
    	String names = "";
		for(UserObj u : userGroup)
		{
//			String nickName = getNickName(u.userid);
			String nickName = null;
			if(!TextUtils.isEmpty(u.user_telname))
			{
				nickName = u.user_telname;
			}
			else if(!TextUtils.isEmpty(u.userid))
			{
				String nick = getNickName(u.userid);
				if(!TextUtils.isEmpty(nick))
				{
					nickName = nick;
				}
				else
				{
					nickName = u.userid;
				}
			}
			else
			{
				nickName = u.user_tel;
			}
			
			if(!names.equals(""))
			{
				names += ", " + nickName;
			}
			else
			{
				names = nickName;
			}
		}
		return names;
    }
    
    private String getUserTextWithA()
    {
    	String names = "";
		for(UserObj u : userGroup)
		{
//			String nickName = getNickName(u.userid);
			String nickName = null;
			if(!TextUtils.isEmpty(u.user_telname))
			{
				nickName = u.user_telname;
			}
			else if(!TextUtils.isEmpty(u.userid))
			{
				String nick = getNickName(u.userid);
				if(TextUtils.isEmpty(u.userid))
				{
					nickName = nick;
				}
				else
				{
					nickName = u.userid;
				}
			}
			else
			{
				nickName = u.user_tel;
			}
			
			if(!names.equals(""))
			{
				names += ", " + "@" + nickName;
			}
			else
			{
				names = "@" + nickName;
			}
		}
		return names;
    }
    
	@Override
	public void finish()
	{
		if(expressionView != null)
		{
			expressionView.hideExpressionView();
			expressionView.hideKeyboard();
			expressionView.hideSoftKeyboard();
		}
		super.finish();
	}
	
	// 发送短信
	private boolean sendMsg() {
		
		String str = "";
		AccountInfo accountInfo = AccountInfo.getInstance(ActiveAccount.getInstance(this).getUID());
		
		Iterator<UserObj> ite = userGroup.iterator();
		while (ite.hasNext())
		{
			UserObj obj = ite.next();
			if (!TextUtils.isEmpty(obj.user_tel) && accountInfo.phoneUsers.isContainByPhone(obj.user_tel))
			{
				str += obj.user_tel + ";";
			}
		}
		
		if (!TextUtils.isEmpty(str))
		{
			Uri uri = Uri.parse("smsto:" + str);
			
			Intent it = new Intent(Intent.ACTION_SENDTO, uri);
			
			it.putExtra("sms_body", "我微享了一条“" + editShareTitleStr.getText().toString()
					+ "”内容，你不来看一看吗，下载安装“" + LookLookActivity.APP_NAME + "”APP后，绑定手机号直接参加讨论。下载地址： mishare.cn");
	
//			it.setType("vnd.android-dir/mms-sms");
			
			startActivity(it);
			return true;
			
		}
		
		return false;
		
	}
	
	public static boolean getSharePicFile(Context context, shareImageUrl[] shareimageurl, String path)
	{
		boolean ret = false;
		if(shareimageurl != null && shareimageurl.length != 0)
		{
//			picUrl = response.shareimageurl[0].imageurl;// + "?width=300&heigh=400";
			
			ArrayList<DiaryImage> diarys = new ArrayList<MergeTool.DiaryImage>();
			MergeTool merge = new MergeTool(context);
			for(int i = 0; i < shareimageurl.length; i ++)
			{
				DiaryImage di = new DiaryImage();
				if("1".equals(shareimageurl[i].imagetype))
				{// 视频
					di.type = ENUM_TYPE.TYPE_URL;
					di.url = shareimageurl[i].imageurl + "?width=200&heigh=200";
					di.id = R.drawable.suolvetu_shipin_share;
					Log.v(TAG, "url" + i + "=" + di.url);
				}
				else if("3".equals(shareimageurl[i].imagetype))
				{//图片
					di.type = ENUM_TYPE.TYPE_URL;
					di.url = shareimageurl[i].imageurl + "?width=200&heigh=200";
					Log.v(TAG, "url" + i + "=" + di.url);
				}
				else
				{//文字
					di.type = ENUM_TYPE.TYPE_ID;
					
					if("2".equals(shareimageurl[i].imagetype))
					{// 长音频
						di.id = R.drawable.suolvetu_luyin;
						Log.v(TAG, "url" + i + "= 长音频");
					}
					else if("4".equals(shareimageurl[i].imagetype))
					{// 纯文字
						di.id = R.drawable.suolvetu_wenzi;
						di.text = shareimageurl[i].content;
						Log.v(TAG, "url" + i + "= 纯文字");
					}
					else if("5".equals(shareimageurl[i].imagetype))
					{// 纯语音
						di.id = R.drawable.suolvetu_chunyuyin;
						Log.v(TAG, "url" + i + "= 纯语音");
					}
					else if("6".equals(shareimageurl[i].imagetype))
					{// 语言+文字
						di.id = R.drawable.suolvetu_wenziyuyin;
						di.text = shareimageurl[i].content;
						Log.v(TAG, "url" + i + "= 语言+文字");
					}
				}
				diarys.add(di);
				
			}
			Bitmap bitmap = merge.getBitmapSync(diarys);
			
			if(bitmap != null)
			{
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
				baos.toByteArray();
				
				File f = new File(path);
				try
				{
					FileOutputStream fos = new FileOutputStream(f);
					fos.write(baos.toByteArray());
					fos.flush();
					fos.close();
					ret = true;
				}
				catch (Exception e)
				{
					e.printStackTrace();
					Log.e(TAG, "file write error");
				}
			}
			
		}
		return ret;
	}
	
	// 获取网络类型 1.wifi 2.2G 3.3G 4.4G 0 无网
	private static String getNetWork(Context context)
	{
		ConnectivityManager conMan = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		TelephonyManager mTelephony = (TelephonyManager) context.getSystemService(context.TELEPHONY_SERVICE); // 检查网络连接，如果无网络可用，就不需要进行连网操作等
		NetworkInfo info = conMan.getActiveNetworkInfo();
		if (info == null || !conMan.getBackgroundDataSetting())
		{
			return "0";
		}
		// 判断网络连接类型，只有在2G/3G/wifi里进行一些数据更新。
		int netType = info.getType();
		int netSubtype = info.getSubtype();
		if (netType == ConnectivityManager.TYPE_WIFI)
		{
			return "1";
		}
		else if (netType == ConnectivityManager.TYPE_MOBILE && netSubtype == TelephonyManager.NETWORK_TYPE_UMTS
				&& !mTelephony.isNetworkRoaming())
		{
			return "3";
		}
		else if (netSubtype == TelephonyManager.NETWORK_TYPE_GPRS || netSubtype == TelephonyManager.NETWORK_TYPE_CDMA
				|| netSubtype == TelephonyManager.NETWORK_TYPE_EDGE)
		{
			return "2";
		}
		else if (netSubtype == TelephonyManager.NETWORK_TYPE_LTE)
		{
			return "4";
		}
		else
		{
			return "0";
		}
	}
	
}
