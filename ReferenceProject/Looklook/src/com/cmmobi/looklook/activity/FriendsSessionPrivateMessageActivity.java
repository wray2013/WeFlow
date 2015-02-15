package com.cmmobi.looklook.activity;

import java.io.File;
import java.util.Iterator;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.LocalBroadcastManager;
import android.text.InputType;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import cn.zipper.framwork.core.ZApplication;
import cn.zipper.framwork.core.ZDataPool;
import cn.zipper.framwork.core.ZLog;
import cn.zipper.framwork.utils.ZPercent;

import com.cmmobi.looklook.MainApplication;
import com.cmmobi.looklook.R;
import com.cmmobi.looklook.activity.DiaryDetailActivity.DiaryType;
import com.cmmobi.looklook.common.adapter.FriendsSessionPrivateMessageAdapter;
import com.cmmobi.looklook.common.constant.Constant;
import com.cmmobi.looklook.common.constant.Constant.SEND_MSG_STATUS;
import com.cmmobi.looklook.common.gson.GsonResponse2;
import com.cmmobi.looklook.common.gson.Requester2;
import com.cmmobi.looklook.common.gson.GsonResponse2.MyDiary;
import com.cmmobi.looklook.common.service.PrivateMessageService;
import com.cmmobi.looklook.common.storage.GsonHelper;
import com.cmmobi.looklook.common.utils.CmmobiClickAgentWrapper;
import com.cmmobi.looklook.common.utils.MD5;
import com.cmmobi.looklook.common.utils.UmengclickAgentWrapper;
import com.cmmobi.looklook.common.view.CountDownView;
import com.cmmobi.looklook.common.view.FriendsExpressionView;
import com.cmmobi.looklook.common.view.pulltorefresh.PullToRefreshBase;
import com.cmmobi.looklook.common.view.pulltorefresh.PullToRefreshBase.OnRefreshListener2;
import com.cmmobi.looklook.common.view.pulltorefresh.PullToRefreshListView;
import com.cmmobi.looklook.downloader.BackgroundDownloader;
import com.cmmobi.looklook.fragment.WrapUser;
import com.cmmobi.looklook.info.profile.AccountInfo;
import com.cmmobi.looklook.info.profile.ActiveAccount;
import com.cmmobi.looklook.info.profile.MediaValue;
import com.cmmobi.looklook.info.profile.MessageWrapper;
import com.cmmobi.looklook.info.profile.NetworkTaskInfo;
import com.cmmobi.looklook.info.profile.OfficialUseridsManager;
import com.cmmobi.looklook.info.profile.PrivateCommonMessage;
import com.cmmobi.looklook.info.profile.PrivateMessageManager;
import com.cmmobi.looklook.info.profile.PrivateSendMessage;
import com.cmmobi.looklook.info.profile.TimeHelper;
import com.cmmobi.looklook.networktask.INetworkTask;
import com.cmmobi.looklook.networktask.UploadNetworkTask;
import com.cmmobi.looklook.offlinetask.OfflineTaskManager;
import com.cmmobi.looklook.prompt.Prompt;
import com.cmmobi.looklook.prompt.TickDownHelper;
import com.cmmobi.looklook.receiver.AttachNetworkTaskReceiver;
import com.google.gson.Gson;
import com.iflytek.msc.ExtAudioRecorder;
import com.iflytek.msc.ExtAudioRecorder.State;
import com.iflytek.msc.QISR_TASK;
import com.nostra13.universalimageloader.api.UniversalImageLoader;
/**
 *  
 *  与某个人的会话界面
 * */
public class FriendsSessionPrivateMessageActivity extends FragmentActivity implements
		OnItemClickListener, OnItemLongClickListener, OnClickListener, OnTouchListener, OnLongClickListener, Callback, OnScrollListener, OnRefreshListener2<ListView>{

	private static final String TAG = "FriendsSessionPrivateMessageActivity";
	private final int HANDLER_RECORD_DURATION_UPDATE_MSG = 0x81654001;
	private final int HANDLER_OFFLINE_UPDATE_MSG = 0x81625178;
	
	//private PrivateMessageSessionListView chatListView;
	private PullToRefreshListView lv_lookfriends;
	private ListView chatListView;
	FriendsSessionPrivateMessageAdapter friendSessionMsgAdapter;
	
	private TextView tv_friends_nick;
	private LinearLayout ll_shuru_text;
	private LinearLayout ll_shuru_mic;
	private EditText inputEditText;
	private FriendsExpressionView expressionView;
	//private ExpressionView expressionView;
	private ImageView expressionButton;
	private InputMethodManager inputMethodManager;
	private ImageView keyboardButton;
	private ImageView recordImageview;
	private ImageView backButton;
	private ImageView friends_qiehuan_btn;
	private ImageView send;
	
	private String input_text;
	private StringBuilder reg_text;
	
	private Handler handler;
	private String other_userid;
	private String my_userid;
	private String other_nick;
	PopupWindow popupWindow;
	
	
	boolean isLongPressed = false;
	//private VolumeStateView vs;
	private CountDownView shortRecView;
	private ExtAudioRecorder ear;
	
	private ActiveAccount aa;
	private AccountInfo ai;
	private PrivateMessageManager pmm;
	private OfficialUseridsManager officialUseridsManager;
	private MessageWrapper mv;
	
	private Gson gson;
	private WrapUser wu;
		
	private boolean supportAudioReg = false;
	
	public static boolean heartBeatInterval = false;
	
	private boolean isOnRecorderButton = false;
	
	private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
		  @Override
		  public void onReceive(Context context, Intent intent) {
				int type;
			    Message msg = new Message();
			    
			    boolean needSendHandler = true;
			    
			    // Get extra data included in the Intent
				if(QISR_TASK.QISR_RESULT_MSG.equals(intent.getAction())){
					type = intent.getIntExtra("type", 0);
				    String content = intent.getStringExtra("content");
				    String audioID = intent.getStringExtra("audioID");
				    boolean useReg = intent.getBooleanExtra("useReg", false);
				    Log.d("receiver", "Got message: " + content);
				    msg.what = type;
				    msg.obj = audioID;
				    if(content!=null){
					    Bundle b = new  Bundle();
					    b.putString("content", content);
					    b.putBoolean("useReg",  useReg);
					    msg.setData(b);
				    }
				}else if(ExtAudioRecorder.AUDIO_RECORDER_MSG.equals(intent.getAction())){
					type = intent.getIntExtra("type", 0);
				    String volume = intent.getStringExtra("content");
				    String playtime = intent.getStringExtra("playtime");
				    msg.what = type;
				    msg.obj = volume;
				    if(playtime!=null){
					    Bundle b = new  Bundle();
					    b.putString("playtime", playtime);
					    msg.setData(b);
				    }
				}else if(INetworkTask.ACTION_TASK_STATE_CHANGE.equals(intent.getAction())){
					/*type = intent.getIntExtra("type", 0);*/
					
					int state = intent.getIntExtra("taskState", 0);
				    String attachid = intent.getStringExtra("attachid");
					msg.what = AttachNetworkTaskReceiver.ATTACH_TASK_TYPE;
					msg.obj =  attachid;
				    Bundle b = new  Bundle();
				    b.putInt("state", state);
				    msg.setData(b);
				}else if(PrivateMessageService.FLAG_MESSAGE_DATA_UPDATE.equals(intent.getAction())){
					boolean hasStrangerMsg = intent.getBooleanExtra("hasStrangerMsg", false);
					boolean hasFriendMsg = intent.getBooleanExtra("hasFriendMsg", false);
					type = intent.getIntExtra("type", -1);


				    msg.what = type;
				    msg.obj = (hasStrangerMsg || hasFriendMsg);

				}else if(PrivateMessageManager.FLAG_OFFLINE_MESSAGE_UPDATE.equals(intent.getAction())){
					String userid = intent.getStringExtra("userid");
				    msg.what = HANDLER_OFFLINE_UPDATE_MSG;
				    msg.obj = userid;

				}else if(BackgroundDownloader.ACTION_DOWNLOAD_PROGRESS.equals(intent.getAction())){
					BackgroundDownloader downloader = (BackgroundDownloader) ZDataPool.getOut(BackgroundDownloader.BACKGROUND_DOWNLOADER);
					ZPercent percent = downloader.getZPercent();
					File file = downloader.getFile();
					String url = downloader.getURL();
					int mediaType = downloader.getMediaType();
					PrivateCommonMessage message = downloader.getPrivateCommonMessage();
					if(message!=null){
						if (percent.isOneHundredPercent()) {
							Log.e(TAG, "ACTION_DOWNLOAD_PROGRESS: percent:100");
							String uid = ActiveAccount.getInstance(ZApplication.getInstance()).getUID();
							message.onDownloading = false;
							message.percent = 100;
							
							MediaValue mediaValue = new MediaValue();
							mediaValue.url = url;
							mediaValue.UID = uid;
							mediaValue.path = file.getAbsolutePath().replace(Environment.getExternalStorageDirectory().getPath(), "");
							mediaValue.totalSize = file.length();
							mediaValue.realSize = mediaValue.totalSize;
							mediaValue.MediaType = mediaType;
							mediaValue.Direction = 1;
							mediaValue.Belong = 4;

							if (MediaValue.checkMediaAvailable(mediaValue, mediaType)) {
								Log.e(TAG, "ACTION_DOWNLOAD_PROGRESS: mediamapping:setMedia");
								AccountInfo.getInstance(uid).mediamapping.setMedia(uid, mediaValue.url, mediaValue);
							}
						}else{
							message.onDownloading = true;
							message.percent = percent.getPercentInt();
						}
						ZLog.alert();
						ZLog.e("percent = " + percent.getCurrentValue());
						String target_userid = null;
						if(message.send && message.s_msg!=null){
							target_userid = message.s_msg.target_userid;
						}else if(!message.send){
							target_userid = message.userid;
						}
						msg.what = FriendsSessionPrivateMessageAdapter.HANDLER_DIARLY_PROGRESS_UPDATE;
						msg.obj = target_userid;
					}else{
						needSendHandler = false;
					}
					

				}

				if(needSendHandler){
					FriendsSessionPrivateMessageActivity.this.handler.sendMessage(msg);
				}
				
		  }
		
    };
    

    
    

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_friends_private_message);
		
		if(!ActiveAccount.getInstance(this).isLogin()){
			//loop to login page
			Intent jump_intent = new Intent(this, LoginWelcomeActivity.class);
			startActivity(jump_intent);
			finish();
		}
		
		UniversalImageLoader.initImageLoader(MainApplication.getAppInstance(), ActiveAccount.getInstance(this).getLookLookID());
		gson = new Gson();
		other_userid = null;
		other_nick = null;
		wu = null;
		mv = null;
		
		Bundle bundle = getIntent().getExtras();
		if(bundle!=null){
			other_userid = bundle.getString( "userid" );
			other_nick = bundle.getString( "nick_name" );
			String wrapuser_str = bundle.getString( "wrapuser" );
			wu = gson.fromJson(wrapuser_str, WrapUser.class) ;
			Log.e(TAG, "onCreate - other_userid:" + other_userid + ", other_nick:" + other_nick + ", wrapuser_str:" + wrapuser_str);
		}else{
			Log.e(TAG, "userid is null");
			return;
		}


		

		
		//debug
		//other_userid = "8d90f2da088760401f0b24203b24689c210d";
		supportAudioReg = ExtAudioRecorder.CheckPlugin();
		isLongPressed = false;
		/*vs = new VolumeStateView(this);*/

		reg_text = new StringBuilder();
		


		handler = new Handler(this);
		
		tv_friends_nick = (TextView) findViewById(R.id.tv_friends_nick);
		ll_shuru_text = (LinearLayout) findViewById(R.id.ll_shuru_text);
		ll_shuru_mic = (LinearLayout) findViewById(R.id.ll_shuru_mic);
		
		inputMethodManager = ((InputMethodManager) this
				.getSystemService(Activity.INPUT_METHOD_SERVICE));
		
		lv_lookfriends = (PullToRefreshListView) findViewById(R.id.chat_list);
		lv_lookfriends.setShowIndicator(false);
		lv_lookfriends.setOnRefreshListener(this);

		chatListView = lv_lookfriends.getRefreshableView();
		chatListView.setOnScrollListener(this);
		//chatListView.setOnItemClickListener(this);
		chatListView.setOnItemLongClickListener(this);
		//chatListView = (ListView) findViewById(R.id.chat_list);


		inputEditText = (EditText) findViewById(R.id.shuru_textview);
		recordImageview = (ImageView) findViewById(R.id.record_imageview);

		backButton = (ImageView) findViewById(R.id.friends_back_btn);

		expressionButton = (ImageView) findViewById(R.id.biaoqing);
		keyboardButton = (ImageView) findViewById(R.id.jianpan);
		friends_qiehuan_btn = (ImageView) findViewById(R.id.friends_qiehuan_btn);
		send  =  (ImageView) findViewById(R.id.send);
		//chatListView.setAdapter(new FriendsPrivateMessageAdapter(this));

		//chatListView.setSelection(chatListView.getCount() - 1);
		// inputEditText.setOnEditorActionListener(this);
		inputEditText.setOnClickListener(this);

		expressionView = new FriendsExpressionView(this, inputEditText);

		expressionView.setOnclickListener(this);
		/*expressionView.show(false);*/
		
		recordImageview.setOnLongClickListener(this);
		recordImageview.setOnTouchListener(this);
		
		expressionButton.setOnClickListener(this);
		keyboardButton.setOnClickListener(this);
		backButton.setOnClickListener(this);
		friends_qiehuan_btn.setOnClickListener(this);
		send.setOnClickListener(this);
		
		aa = ActiveAccount.getInstance(this);
		ai = AccountInfo.getInstance(aa.getLookLookID());
		pmm = ai.privateMsgManger;
		officialUseridsManager = ai.officialUseridsManager;
		
		if(aa.isLogin()){
			my_userid = aa.getLookLookID();
		}else{
			my_userid = null;
		}
		
		//不考虑未登录状态，若未登录消息通知不能发出来
		if(other_userid!=null){
			mv = pmm.messages.get(other_userid);
			if(mv!=null && mv.msgs!=null && mv.nickname!=null){
				tv_friends_nick.setText(mv.nickname);
				other_nick = mv.nickname;
				friendSessionMsgAdapter = new FriendsSessionPrivateMessageAdapter(chatListView, this, handler, mv.msgs, expressionView, other_nick);
				chatListView.setAdapter(friendSessionMsgAdapter);
				chatListView.setSelection(chatListView.getCount());

			}else if(other_nick!=null){
				tv_friends_nick.setText(other_nick);
				MessageWrapper empty = new MessageWrapper();
				empty.nickname = other_nick;
				empty.userid = other_userid;
				pmm.messages.put(other_userid, empty);
				friendSessionMsgAdapter = new FriendsSessionPrivateMessageAdapter(chatListView, this, handler, empty.msgs, expressionView, other_nick);
				chatListView.setAdapter(friendSessionMsgAdapter);
				chatListView.setSelection(chatListView.getCount());
			}
		}

		
		if(wu!=null){
			other_userid = wu.userid;
			if(wu.nickname!=null){
				tv_friends_nick.setText(wu.nickname);
				other_nick = wu.nickname;
			}
			
			mv = pmm.messages.get(other_userid);
			if(mv!=null && mv.msgs!=null){
				/*Gson gson = new Gson();
				Log.e(TAG, "mv:" + gson.toJson(mv, MessageWrapper.class));*/
				friendSessionMsgAdapter = new FriendsSessionPrivateMessageAdapter(chatListView, this, handler, mv.msgs, expressionView, other_nick);
				chatListView.setAdapter(friendSessionMsgAdapter);
				chatListView.setSelection(chatListView.getCount());
			}

		}
		
		if(mv!=null){
			Log.d(TAG, "MessageWrapper :" + mv +  "str:" + GsonHelper.getInstance().getString(mv));
		}
		
		autoDownload();
		
		LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver,
			      new IntentFilter(QISR_TASK.QISR_RESULT_MSG));
		LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver,
			      new IntentFilter(ExtAudioRecorder.AUDIO_RECORDER_MSG));
		LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver,
			      new IntentFilter(INetworkTask.ACTION_TASK_STATE_CHANGE));
		LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver,
			      new IntentFilter(PrivateMessageService.FLAG_MESSAGE_DATA_UPDATE));
		LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver,
			      new IntentFilter(BackgroundDownloader.ACTION_DOWNLOAD_PROGRESS));
		LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver,
			      new IntentFilter(PrivateMessageManager.FLAG_OFFLINE_MESSAGE_UPDATE));
	}
	
	@Override
	public void onResume() {
		super.onResume();
		UmengclickAgentWrapper.onResume(this);
		CmmobiClickAgentWrapper.onResume(this);
		
		if(mv!=null){
			mv.consumeUnreadMsgs();
		}
		
		heartBeatInterval = true; //5 sec
	}

	@Override
	public void onPause() {
		super.onPause();
		UmengclickAgentWrapper.onPause(this);
		CmmobiClickAgentWrapper.onPause(this);
		
		heartBeatInterval = false; //30 sec
	}

	@Override
	public void onStop(){
		super.onStop();
		TickDownHelper.getInstance(handler).stop(2);
		CmmobiClickAgentWrapper.onStop(this);
	}


	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		inputEditText.append(arg1.getTag().toString());
	}

	@Override
	public void onClick(View v) {
		View view = expressionView.getExpressionView();
		//int visible = expressionView.getVisibility();
		switch (v.getId()) {
/*		case R.id.pop_item_friends:
			Toast.makeText(FriendsSessionPrivateMessageActivity.this, "del it position：" + del_pos, Toast.LENGTH_SHORT).show();
			handler.obtainMessage(FriendsSessionPrivateMessageAdapter.HANDLER_FLAG_MSG_DELETE, del_pos).sendToTarget();
			if(popupWindow!=null){
				popupWindow.dismiss();
			}

			break;*/
		case R.id.biaoqing:
			keyboardButton.setBackgroundResource(R.drawable.mic);
			inputEditText.setVisibility(View.VISIBLE);
			recordImageview.setVisibility(View.GONE);
			if (View.GONE != view.getVisibility()) {
				inputEditText.setFocusable(true);
				inputEditText.requestFocus();
				inputEditText.setInputType(InputType.TYPE_CLASS_TEXT); // disable soft input 
				expressionView.hideExpressionView();
				//expressionView.show(false);
				expressionButton.setBackgroundResource(R.drawable.message_biaoqing);
			} else {
				inputEditText.setOnTouchListener(new OnTouchListener() {
	                public boolean onTouch(View v, MotionEvent event) {  
	                    // TODO Auto-generated method stub  
	                    int inType = inputEditText.getInputType(); // backup the input type  
	                    inputEditText.setInputType(InputType.TYPE_NULL); // disable soft input      
	                    inputEditText.onTouchEvent(event); // call native handler      
	                    inputEditText.setInputType(inType); // restore input type     
	                    return true;                      
	                }  
	            });
				expressionView.showExpressionView();
				//expressionView.show(true);
				expressionButton.setBackgroundResource(R.drawable.jianpan);
			}
			break;

		case R.id.jianpan:
			if (View.VISIBLE == ll_shuru_text.getVisibility()) {
				//txt - > mic
				ll_shuru_text.setVisibility(View.GONE);
				ll_shuru_mic.setVisibility(View.VISIBLE);
				keyboardButton.setBackgroundResource(R.drawable.jianpan);
				view.setVisibility(View.GONE);
				//expressionView.show(false);
				if(this.getCurrentFocus()!=null){
					inputMethodManager.hideSoftInputFromWindow(this
							.getCurrentFocus().getWindowToken(),
							InputMethodManager.HIDE_NOT_ALWAYS);
				}

				inputEditText.setVisibility(View.GONE);
				recordImageview.setVisibility(View.VISIBLE);
			} else {
				ll_shuru_text.setVisibility(View.VISIBLE);
				ll_shuru_mic.setVisibility(View.GONE);
				keyboardButton.setBackgroundResource(R.drawable.mic);

				inputMethodManager.showSoftInput(this.getCurrentFocus(), 0);
				inputEditText.setVisibility(View.VISIBLE);
				inputEditText.setFocusable(true);
				inputEditText.requestFocus();
				recordImageview.setVisibility(View.GONE);
			}
			break;
		case R.id.friends_back_btn:
			this.finish();
			break;
		case R.id.friends_qiehuan_btn:
			launchOtherUserHomepage(other_userid);
			break;
		case R.id.send://发表文本私信
			input_text = inputEditText.getText().toString();
			if(input_text!=null && !input_text.equals("")){
				String pendingMsgID =  String.valueOf(TimeHelper.getInstance().now());
				PrivateSendMessage send_msg = new PrivateSendMessage(my_userid, other_userid, pendingMsgID);
				send_msg.setContent(input_text);
				send_msg.setPrivateMsgtype("1");
				send_msg.updateStatus(SEND_MSG_STATUS.REG_DONE);

				if(mv!=null && mv.msgs!=null){

				}else if(wu!=null && wu.userid!=null && wu.nickname!=null){
					//发送私信时才创建MessageWrapper
					mv = new MessageWrapper();
					mv.userid = wu.userid;
					mv.nickname = wu.nickname;
					mv.headimageurl = wu.headimageurl;
					mv.sex = wu.sex;
					mv.signature = wu.signature;
					pmm.messages.put(mv.userid, mv);
				}else if(other_userid!=null){
					//发送私信时才创建MessageWrapper
					mv = new MessageWrapper();
					mv.userid = other_userid;
					mv.nickname = other_nick;
					mv.act = "1";
					if(wu!=null){
						mv.headimageurl = wu.headimageurl;
						mv.sex = wu.sex;
						mv.signature = wu.signature;
					}
					
					pmm.messages.put(mv.userid, mv);
				} else{
					Prompt.Alert(this, "非法的用户");
					break;
				}
				
				mv.msgs.add(new PrivateCommonMessage(send_msg));
				mv.privmsg_type = "1";
				mv.content = input_text;
				mv.act = "1";
				mv.lastTimeMill = TimeHelper.getInstance().now();

				if(friendSessionMsgAdapter!=null){
					friendSessionMsgAdapter.notifyDataSetChanged();
				}else{
					friendSessionMsgAdapter = new FriendsSessionPrivateMessageAdapter(chatListView, this, handler, mv.msgs, expressionView, other_nick);
					chatListView.setAdapter(friendSessionMsgAdapter);
					chatListView.setSelection(chatListView.getCount());
				}
				
				inputEditText.setText("");
				OfflineTaskManager.getInstance().addSendPrivateMsgTask(null,null, input_text, other_userid, "1", pendingMsgID,null);
				//Requester2.sendMessage(handler, null, input_text, other_userid, "1", pendingMsgID);
			}else{
				Prompt.Alert(this, "请输入消息内容");
			}

			break;
		default:
			break;
		}
	}
	
/*	@Override
	public boolean dispatchTouchEvent(MotionEvent event) {
		switch (event.getAction()) {
		case MotionEvent.ACTION_UP:
			if(isLongPressed){
				isLongPressed = false;
				Log.e(TAG, "isLongPressed is false");
				vs.dismissView();
				TickDownHelper.getInstance(handler).stop(2);
				//ear.stop();
			}
			break;
		case MotionEvent.ACTION_DOWN:
			break;
		case MotionEvent.ACTION_MOVE:
			if (event.getY() > 0) {
				Log.e(TAG, "MotionEvent.ACTION_MOVE up");
				isOnRecorderButton = false;
			} else {
				Log.e(TAG, "MotionEvent.ACTION_MOVE down");
				isOnRecorderButton = true;
			}
		}
		return super.dispatchTouchEvent(event);

	}*/
	
	@Override
	public boolean onTouch(View v, MotionEvent event) {
		switch (event.getAction()) {
		case MotionEvent.ACTION_UP:
			if(isLongPressed){
				isLongPressed = false;
				//Log.e(TAG, "isLongPressed is false");
				/*vs.dismissView();*/
				TickDownHelper.getInstance(handler).stop(2);
				//ear.stop();
			}
			break;
		case MotionEvent.ACTION_DOWN:
			break;
		case MotionEvent.ACTION_MOVE:
			if (event.getY() < 0) {
				//Log.e(TAG, "MotionEvent.ACTION_MOVE up");
				isOnRecorderButton = false;
			} else {
				//Log.e(TAG, "MotionEvent.ACTION_MOVE down");
				//isOnRecorderButton = true;
			}
		}
		return false;
	}

	private void launchOtherUserHomepage(String userid) {
		// TODO Auto-generated method stub
		
		if(userid!=null){
			Intent intent = new Intent();
			intent.setClass(this, HomepageOtherDiaryActivity.class);
			Bundle mBundle = new Bundle();
			mBundle.putString("userid", userid);// 压入数据
			mBundle.putString("nickname",  other_nick);// 压入数据
			intent.putExtras(mBundle);
			startActivity(intent);
		}else{
			Prompt.Alert(this, "非法的用户");
		}


		
	}


	@Override
	public boolean onLongClick(View v) {
		switch(v.getId()){
		case R.id.record_imageview:
			
			if(ear!=null){
				State state = ear.getRecorderState();
				if(state == State.RECORDING || state == State.PAUSE){
					Prompt.Alert(this, "请稍等，上次录音还没结束!");
					return false;
				}
			}
			
			if(mv!=null && mv.msgs!=null){

			}else if(wu!=null && wu.userid!=null && wu.nickname!=null){
				//发送私信时才创建MessageWrapper
				mv = new MessageWrapper();
				mv.userid = wu.userid;
				mv.nickname = wu.nickname;
				mv.headimageurl = wu.headimageurl;
				mv.sex = wu.sex;
				mv.signature = wu.signature;
				pmm.messages.put(mv.userid, mv);
			}else if(other_userid!=null){
				//发送私信时才创建MessageWrapper
				mv = new MessageWrapper();
				mv.userid = other_userid;
				mv.nickname = other_nick;
				if(wu!=null){
					mv.headimageurl = wu.headimageurl;
					mv.sex = wu.sex;
					mv.signature = wu.signature;
				}

				pmm.messages.put(mv.userid, mv);
			}else{
				Prompt.Alert(this, "非法的用户");
				break;
			}
			
			isLongPressed = true; 
			Log.e(TAG, "isLongPressed is true");
			/*vs.showVolume(0);*/
			
			
			ear = ExtAudioRecorder.getInstanse(false, 2, 1);
			long lastTimeMill = TimeHelper.getInstance().now();
			String pendingMsgID = String.valueOf(lastTimeMill);
			String shortPath = Constant.SD_STORAGE_ROOT + "/" + my_userid + "/shortaudio";
			
			ear.start(this, pendingMsgID, shortPath, false, 4, supportAudioReg);
			
			PrivateSendMessage send_msg = new PrivateSendMessage(my_userid, other_userid, pendingMsgID);
			send_msg.setLocalFilePath(shortPath + "/" + pendingMsgID + "/" + pendingMsgID + ".mp4");
			send_msg.updateStatus(SEND_MSG_STATUS.REC_DONE);
			send_msg.content = "";
			isOnRecorderButton = true;
			
			mv.msgs.add(new PrivateCommonMessage(send_msg));
			mv.privmsg_type = "2";
			mv.act = "1";
			mv.content = "[语音]";
			mv.lastTimeMill = lastTimeMill;
			
			TickDownHelper.getInstance(handler).start(30);
			if (shortRecView == null) {
				shortRecView = new CountDownView(this);
			}
			EnableInput(false);
			shortRecView.show();
			shortRecView.updateTime(30);
			
			if(friendSessionMsgAdapter!=null){
				friendSessionMsgAdapter.notifyDataSetChanged();
			}else{
				friendSessionMsgAdapter = new FriendsSessionPrivateMessageAdapter(chatListView, this, handler, mv.msgs, expressionView, other_nick);
				chatListView.setAdapter(friendSessionMsgAdapter);
				chatListView.setSelection(chatListView.getCount());
			}
			break;
		}
		return true;
	}


	@Override
	public boolean handleMessage(Message msg) {
		PrivateCommonMessage pcm;
		// TODO Auto-generated method stub
		switch(msg.what){
		case HANDLER_OFFLINE_UPDATE_MSG:
			if(friendSessionMsgAdapter!=null){
				friendSessionMsgAdapter.notifyDataSetChanged();
			}
			break;
		case FriendsSessionPrivateMessageAdapter.HANDLER_DIARLY_PROGRESS_UPDATE:
			if(friendSessionMsgAdapter!=null && msg.obj!=null && msg.obj.equals(other_userid)){
				friendSessionMsgAdapter.notifyDataSetChanged();
			}
			break;
		case Requester2.RESPONSE_TYPE_SEND_MESSAGE:
			Log.e(TAG, "Requester2.RESPONSE_TYPE_SEND_MESSAGE:");
			//step3若为null则UI更新为失败，若status为0则继续判断是否需要上传语音
			try {

				GsonResponse2.sendmessageResponse obj = (GsonResponse2.sendmessageResponse) (msg.obj);
				if(obj==null){
					Log.e(TAG, "sendmessageResponse obj is null");
					//update listview
					updateStatus(SEND_MSG_STATUS.REC_DONE, SEND_MSG_STATUS.RIA_FAIL);
					updateStatus(SEND_MSG_STATUS.REG_DONE, SEND_MSG_STATUS.RIA_FAIL);
					
/*					if(officialUseridsManager!=null && officialUseridsManager.ContainUser(other_userid)){
						//离线断网，官方提示
						AutoReply(other_userid , true);
					}*/
					
				}else{
/*					if(officialUseridsManager!=null && officialUseridsManager.ContainUser(other_userid)){
						//离线断网，官方提示
						AutoReply(other_userid , false);
					}*/
					updateSendMsgByUUID(obj.uuid, obj);
					
					if("0".equals(obj.status) || "138107".equals(obj.status) || "138115".equals(obj.status)){
						if( "138115".equals(obj.status) ){
							Prompt.Dialog(this, false, "提醒", "对方开启隐私设置，无法进行私信", null);
						} 
						
						if("1".equals(getMsgType(obj.uuid))){//文本
							updateStatusByUUID(obj.uuid, SEND_MSG_STATUS.ALL_DONE);
						}else if("3".equals(getMsgType(obj.uuid))){ //日记
							updateStatusByUUID(obj.uuid, SEND_MSG_STATUS.ALL_DONE);
						}else{//2,4语音或语音+文字
							if( "138115".equals(obj.status) || "138107".equals(obj.status)){
								updateStatusByUUID(obj.uuid, SEND_MSG_STATUS.ALL_DONE);
							} else{
								String filepath = getLocalFilePath(obj.uuid);
								if(filepath!=null){
									NetworkTaskInfo tt = new NetworkTaskInfo(my_userid, null, obj.privatemsgid, Environment.getExternalStorageDirectory() + filepath, obj.audiopath, "5", "3");
									UploadNetworkTask uploadtask = new UploadNetworkTask(tt);//创建上传/下载任务
									uploadtask.start();
								}
							}


						}
					}else{
						updateStatusByUUID(obj.uuid, SEND_MSG_STATUS.RIA_FAIL);
					}


				}
				
				if(friendSessionMsgAdapter!=null){
					friendSessionMsgAdapter.notifyDataSetChanged();
				}

			} catch (Exception e) {
				e.printStackTrace();
			}
			break;
		case QISR_TASK.HANDLER_QISR_RESULT_CLEAN:
			reg_text = new StringBuilder();
			break;
		case QISR_TASK.HANDLER_QISR_RESULT_ADD:

			
			if(msg.obj!=null){
				Bundle b = msg.getData();
				if(b!=null && b.getString("content")!=null){
					reg_text.append(b.getString("content"));
				}
				
			}
			break;
		case QISR_TASK.HANDLER_QISR_RESULT_DONE:
			Log.e(TAG, "QISR_TASK.HANDLER_QISR_RESULT_DONE:");
			//step2 识别流程结束，发起sendmessage请求，UI更新为有文字的附加
			//update elem to RIA_PENDING
			if(isOnRecorderButton){
				pcm = updateStatusByUUID((String)msg.obj, SEND_MSG_STATUS.REG_DONE);
				
				if(reg_text!=null && reg_text.toString() != null && !reg_text.toString().equals("")){
					//send_msg.content = reg_text.toString();
					//send_msg.privatemsgtype="4";
/*					StringBuilder sb = new StringBuilder();
					sb.append(" ");
					sb.append(reg_text.toString());*/
					if(pcm!=null){
						pcm.updateSendMsgType("4");
						pcm.updateContent(reg_text.toString());
					}

					//Requester2.sendMessage(handler, null, reg_text.toString(), other_userid, "4", (String)(msg.obj));
					OfflineTaskManager.getInstance().addSendPrivateMsgTask(null,null, reg_text.toString(), other_userid, "4", (String)(msg.obj),null);
				}else{
					//send_msg.privatemsgtype="2";
					if(pcm!=null){
						pcm.updateSendMsgType("2");
					}

					//Requester2.sendMessage(handler, null, null, other_userid, "2", (String)(msg.obj));
					OfflineTaskManager.getInstance().addSendPrivateMsgTask(null,null, null, other_userid, "2", (String)(msg.obj),null);
				}
			}else{
				removeMsgByUUID((String)msg.obj);
			}

			
			if(friendSessionMsgAdapter!=null){
				friendSessionMsgAdapter.notifyDataSetChanged();
			}
			break;
/*		case ExtAudioRecorder.HANDLER_AUDIO_RECORDER_VOLUME:
			//txt_volume.setText((String)msg.obj);
			try{
				vs.updateVolume(Integer.valueOf((String)msg.obj));
			}catch(NumberFormatException e){
				e.printStackTrace();
			}
			
			break;*/
		case TickDownHelper.HANDLER_FLAG_TICK_DOWN:
			int secordsLeft = (Integer) msg.obj;
/*			if(secordsLeft<=0){
				TickDownHelper.getInstance(handler).stop();
                ear.stop();
			}else{
				shortRecView.updateTime(secordsLeft);
			}*/
			if(shortRecView!=null){
				shortRecView.updateTime(secordsLeft);
			}

			break;
		case TickDownHelper.HANDLER_FLAG_TICK_STOP:
			EnableInput(true);
			ear.stop();
			shortRecView.dismiss();
			break;
		case ExtAudioRecorder.HANDLER_AUDIO_RECORDER_DONE:
			Log.e(TAG, "ExtAudioRecorder.HANDLER_AUDIO_RECORDER_DONE:");
			//step1 语音文件生成，将其加入到UI界面，UI条目为loading
			//txt_volume.setText("recorder DONE");
			
			//update listview
			/*vs.dismissView();*/
			EnableInput(true);
			if(shortRecView!=null){
				shortRecView.dismiss();
			}

			if(isOnRecorderButton){
				pcm = updateStatusByUUID((String)msg.obj, SEND_MSG_STATUS.REC_DONE);
				
				Bundle b = msg.getData();
				if(b!=null && b.getString("playtime")!=null){
					Log.i(TAG, "playtime:" + b.getString("playtime"));
					if(pcm!=null){
						pcm.updateSendMsgPlayTime(b.getString("playtime"));
					}
					
				}
			}else{
				removeMsgByUUID((String)msg.obj);
			}
			
			if(friendSessionMsgAdapter!=null){
				friendSessionMsgAdapter.notifyDataSetChanged();
			}
			
			if(!supportAudioReg){
				handler.obtainMessage(QISR_TASK.HANDLER_QISR_RESULT_DONE, (String)msg.obj).sendToTarget();
			}
			
			break;
		case AttachNetworkTaskReceiver.ATTACH_TASK_TYPE:
			Log.e(TAG, "ATTACH_TASK_TYPE");
			Bundle ba = msg.getData();
			int state = ba.getInt("state", -1);
			if(state==5){
				updateStatusByAttachID((String)msg.obj, SEND_MSG_STATUS.ALL_DONE);
				if(friendSessionMsgAdapter!=null){
					friendSessionMsgAdapter.notifyDataSetChanged();
				}
			}else if(state==7 || state==3){
				updateStatusByAttachID((String)msg.obj, SEND_MSG_STATUS.UPLOAD_FAIL);
				if(friendSessionMsgAdapter!=null){
					friendSessionMsgAdapter.notifyDataSetChanged();
				}
			}else{

			}
			break;
		case PrivateMessageService.HANDLER_FLAG_MESSAGE_DATA_UPDATE:
			if((Boolean) msg.obj){
				if(friendSessionMsgAdapter!=null){
					friendSessionMsgAdapter.notifyDataSetChanged();
				}
				if(mv!=null){
					mv.consumeUnreadMsgs();
				}
			}			
			lv_lookfriends.onRefreshComplete();
			break;
		case FriendsSessionPrivateMessageAdapter.HANDLER_FLAG_MSG_RETRY:
			if(other_userid!=null && aa.isLogin() && msg.obj!=null){
				ai = AccountInfo.getInstance(aa.getLookLookID());
				pmm = ai.privateMsgManger;
				if(pmm!=null && pmm.messages!=null){
					mv = pmm.messages.get(other_userid);
					if(mv!=null && mv.msgs!=null){
						int pos = (Integer)(msg.obj);
						PrivateCommonMessage  pcm_retry = mv.msgs.get(pos);
						pcm_retry.updateSendMsgStatus(SEND_MSG_STATUS.REG_DONE);
						if(pcm_retry.send && pcm_retry.s_msg!=null && pcm_retry.s_msg.diaryid!=null){
							Requester2.sendMessage(handler, pcm_retry.s_msg.diaryid, pcm_retry.getContent(), other_userid, pcm_retry.getMsgType(), pcm_retry.getUUID()); 
						}else{
							Requester2.sendMessage(handler, null, pcm_retry.getContent(), other_userid, pcm_retry.getMsgType(), pcm_retry.getUUID()); 
						}
						
						if(friendSessionMsgAdapter!=null){
							friendSessionMsgAdapter.notifyDataSetChanged();
						}
					}
					
				}
				
			}
			break;
		case FriendsSessionPrivateMessageAdapter.HANDLER_FLAG_MSG_DELETE:
			if(other_userid!=null && aa.isLogin() && msg.obj!=null){
				ai = AccountInfo.getInstance(aa.getLookLookID());
				pmm = ai.privateMsgManger;
				if(pmm!=null && pmm.messages!=null){
					mv = pmm.messages.get(other_userid);
					if(mv!=null && mv.msgs!=null){
						PrivateCommonMessage item = (PrivateCommonMessage)(msg.obj);
						mv.msgs.remove(item);
						if(friendSessionMsgAdapter!=null){
							friendSessionMsgAdapter.notifyDataSetChanged();
						}
					}
					
				}
				
			}
			break;
		}
		return false;
	}
	
/*	private void AutoReply(String userid, boolean offline) {
		// TODO Auto-generated method stub
		MyMessage mm = new GsonResponse2.MyMessage();
		long lastTime = TimeHelper.getInstance().now();
		
		mm.act = "1";
		if(offline){
			mm.privmsg.content = "亲，感谢你对looklook的支持……现在您的网络不太给力哦，稍后您再联系我哦~\n";
		}else{
			mm.privmsg.content = "感谢您对looklook的支持！您的建议已提交，稍后给您答复。谢谢！\n";
		}
		mm.privmsg.privmsg_type = "1"; //私信类型 --- 1代表纯文字 2代表语音 3代表日记      4语音加文字
		mm.timemill = String.valueOf(lastTime);

		mv.msgs.add(new PrivateCommonMessage(userid, mm, lastTime));
		mv.privmsg_type = "1";
		mv.content = "[自动回复]";
		mv.lastTimeMill = lastTime;
	}*/

	@Override
	protected void onDestroy() {
		  // Unregister since the activity is about to be closed.
		  LocalBroadcastManager.getInstance(this).unregisterReceiver(mMessageReceiver);
		  super.onDestroy();
		
	}
	
	private void autoDownload(){
		if(other_userid!=null && aa.isLogin()){
			pmm = ai.privateMsgManger;
			if(pmm!=null && pmm.messages!=null){
				mv = pmm.messages.get(other_userid);
				if(mv!=null && mv.msgs!=null){
					Iterator<PrivateCommonMessage> it = mv.msgs.iterator();
					while(it.hasNext()){
						PrivateCommonMessage item = it.next();
						MyDiary diaries = null;
						if(!item.send && item.r_msg!=null && item.r_msg.privmsg!=null && item.r_msg.privmsg.diaries!=null && item.r_msg.privmsg.diaries.diaryid!=null){
							diaries = item.r_msg.privmsg.diaries;
						}
						
						if(item.send && item.s_msg!=null && item.s_msg.diaries!=null && item.s_msg.diaries.diaryid!=null){
							diaries = item.s_msg.diaries;
						}
						
						if(diaries!=null){

							String uid = ActiveAccount.getInstance(MainApplication.getAppInstance()).getUID();
							MediaValue mv = null;
							DiaryType dt = diaries.getDiaryType();
							//DiaryType dt = DiaryType.TEXT;
							if(DiaryType.AUDIO.equals(dt)){
								String audioUrl = diaries.getLongRecUrl();
								//String audioUrl = "http://192.168.100.111:8080/looklook/audio_pub/audio_source/2013/07/15/115350802b67ce848a46ffa0a2f19aaeb7ddc6.mp4";
								
								mv  = AccountInfo.getInstance(uid).mediamapping.getMedia(uid, audioUrl);
								if (!MediaValue.checkMediaAvailable(mv)) {
									String Key = MD5.encode((uid+audioUrl).getBytes());
									String path = Environment.getExternalStorageDirectory() + Constant.SD_STORAGE_ROOT + "/" + uid + "/audio/" + Key;
									File file = new File(path);
									if(!file.getParentFile().exists()){
										file.getParentFile().mkdirs();
									}
									BackgroundDownloader downloader = new BackgroundDownloader(
											item, 
						        			audioUrl, 
						        			file, //★★★
						        			4);
						        	downloader.start();
								}
							}
							
						}
					}
				}
				
			}
		}
	}
	
	
	private void updateStatus(SEND_MSG_STATUS old_status, SEND_MSG_STATUS new_status){
		if(other_userid!=null && aa.isLogin()){
			pmm = ai.privateMsgManger;
			if(pmm!=null && pmm.messages!=null){
				mv = pmm.messages.get(other_userid);
				if(mv!=null && mv.msgs!=null){
					Iterator<PrivateCommonMessage> it = mv.msgs.iterator();
					while(it.hasNext()){
						PrivateCommonMessage pcm = it.next();
						if(pcm.getSendMsgStatus()==old_status){
							pcm.updateSendMsgStatus(new_status);
						}
					}
/*					chatListView.reset();
					chatListView.loadMore(mv.msgs);*/
					if(friendSessionMsgAdapter!=null){
						friendSessionMsgAdapter.notifyDataSetChanged();
					}
				}
				
			}
		}
	}
	
	private void updateStatusByAttachID(String attachid, SEND_MSG_STATUS new_status){
		if(other_userid!=null && aa.isLogin()){
			pmm = ai.privateMsgManger;
			if(pmm!=null && pmm.messages!=null){
				mv = pmm.messages.get(other_userid);
				if(mv!=null && mv.msgs!=null){
					Iterator<PrivateCommonMessage> it = mv.msgs.iterator();
					while(it.hasNext()){
						PrivateCommonMessage pcm = it.next();
						if(attachid!=null && attachid.equals(pcm.getPrivateMSGID())){
							pcm.updateSendMsgStatus(new_status);
						}
					}
					
					if(friendSessionMsgAdapter!=null){
						friendSessionMsgAdapter.notifyDataSetChanged();
					}
				}
				
			}
		}
	}
	
	private PrivateCommonMessage updateStatusByUUID(String uuid, SEND_MSG_STATUS new_status){
		if(other_userid!=null && aa.isLogin()){
			pmm = ai.privateMsgManger;
			if(pmm!=null && pmm.messages!=null){
				mv = pmm.messages.get(other_userid);
				if(mv!=null && mv.msgs!=null){
					Iterator<PrivateCommonMessage> it = mv.msgs.iterator();
					while(it.hasNext()){
						PrivateCommonMessage pcm = it.next();
						if(uuid!=null && uuid.equals(pcm.getUUID())){
							pcm.updateSendMsgStatus(new_status);
							return pcm;
						}
					}

				}
				
			}
		}
		
		return null;
	}
	
	private void removeMsgByUUID(String uuid){
		if(other_userid!=null && aa.isLogin()){
			pmm = ai.privateMsgManger;
			if(pmm!=null && pmm.messages!=null){
				mv = pmm.messages.get(other_userid);
				if(mv!=null && mv.msgs!=null){
					Iterator<PrivateCommonMessage> it = mv.msgs.iterator();
					while(it.hasNext()){
						PrivateCommonMessage pcm = it.next();
						if(uuid!=null && uuid.equals(pcm.getUUID())){
							it.remove();
						}
					}

				}
				
			}
		}
	}
	
	
	private void updateSendMsgByUUID(String uuid, GsonResponse2.sendmessageResponse obj){
		if(other_userid!=null && aa.isLogin()){
			pmm = ai.privateMsgManger;
			if(pmm!=null && pmm.messages!=null){
				mv = pmm.messages.get(other_userid);
				if(mv!=null && mv.msgs!=null){
					Iterator<PrivateCommonMessage> it = mv.msgs.iterator();
					while(it.hasNext()){
						PrivateCommonMessage pcm = it.next();
						if(uuid!=null && uuid.equals(pcm.getUUID())){
							pcm.updateSendMsg(obj);
						}
					}

				}
				
			}
		}
	}
	
	private String getLocalFilePath(String uuid){
		if(other_userid!=null && aa.isLogin()){
			pmm = ai.privateMsgManger;
			if(pmm!=null && pmm.messages!=null){
				mv = pmm.messages.get(other_userid);
				if(mv!=null && mv.msgs!=null){
					Iterator<PrivateCommonMessage> it = mv.msgs.iterator();
					while(it.hasNext()){
						PrivateCommonMessage pcm = it.next();
						if(uuid!=null && uuid.equals(pcm.getUUID())){
							return pcm.getLocalPathFile();
						}
					}
				}
				
			}
		}
		
		return null;
	}
	
	private String getMsgType(String uuid){
		if(other_userid!=null && aa.isLogin()){
			pmm = ai.privateMsgManger;
			if(pmm!=null && pmm.messages!=null){
				mv = pmm.messages.get(other_userid);
				if(mv!=null && mv.msgs!=null){
					Iterator<PrivateCommonMessage> it = mv.msgs.iterator();
					while(it.hasNext()){
						PrivateCommonMessage pcm = it.next();
						if(uuid!=null && uuid.equals(pcm.getUUID())){
							return pcm.getMsgType();
						}
					}
				}
				
			}
		}
		
		return null;
	}


	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem,
			int visibleItemCount, int totalItemCount) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
		// TODO Auto-generated method stub
		startService(new Intent(this, PrivateMessageService.class));
	}

	@Override
	public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
		// TODO Auto-generated method stub
		startService(new Intent(this, PrivateMessageService.class));
	}


	/**
	 * 此处已经废弃
	 */
	@Override
	public boolean onItemLongClick(AdapterView<?> parent, View v,
			int position, long id) {
		Log.e(TAG, "onItemLongClick position:" + position);
		//del_pos = position - 1;
		//if(del_pos<0){
		//	return false;
		//}
		final PrivateCommonMessage item = (PrivateCommonMessage) parent.getAdapter().getItem(position);
		View view = LayoutInflater.from(this).inflate(
				R.layout.pup_list_item_friends_private_message,
				null);
		//view.findViewById(R.id.pop_item_friends).setOnClickListener(this);
		
		popupWindow = new PopupWindow(view,
					LayoutParams.WRAP_CONTENT,
					LayoutParams.WRAP_CONTENT){
				
			};
		popupWindow.setBackgroundDrawable(new BitmapDrawable());
		popupWindow.setFocusable(true);
		popupWindow.setTouchable(true);
		popupWindow.setOutsideTouchable(true);

		// popupWindow.showAsDropDown(viewHolder.vedioRelativeLayout,
		// -40, 0);
		int[] location = new int[2];
		RelativeLayout rl_all = (RelativeLayout) v.findViewById(R.id.rl_all);
		if(rl_all!=null){
			//out
			rl_all.getLocationInWindow(location);
			Log.i("dan", location[0] + ":" + location[1]);
			if(item.send){
				popupWindow.showAtLocation(rl_all, 0 , location[0] + rl_all.getWidth() - 100  , location[1] - 80);
			}else{
				popupWindow.showAtLocation(rl_all, 0, location[0]  + 100 , location[1] - 80);
			}
		}else{
			//in
			v.getLocationInWindow(location);
			Log.i("dan", location[0] + ":" + location[1]);
			if(item.send){
				popupWindow.showAtLocation(v, 0 , location[0] + v.getWidth() - 100  , location[1] - 80);
			}else{
				popupWindow.showAtLocation(v, 0, location[0]  + 100 , location[1] - 80);
			}
		}

		//v.getLocationInWindow(location);



		view.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				//Toast.makeText(FriendsSessionPrivateMessageActivity.this, "del it position：" + del_pos, Toast.LENGTH_SHORT).show();
				handler.obtainMessage(FriendsSessionPrivateMessageAdapter.HANDLER_FLAG_MSG_DELETE, item).sendToTarget();
				popupWindow.dismiss();
			}
			
		});
		
		return false;
	}

	private void EnableInput(boolean flag){
		expressionButton.setClickable(flag);
		expressionButton.setLongClickable(flag);
		keyboardButton.setClickable(flag);
		keyboardButton.setLongClickable(flag);
		backButton.setClickable(flag);
		backButton.setLongClickable(flag);
		friends_qiehuan_btn.setClickable(flag);
		friends_qiehuan_btn.setLongClickable(flag);
		chatListView.setClickable(flag);
	}

}
