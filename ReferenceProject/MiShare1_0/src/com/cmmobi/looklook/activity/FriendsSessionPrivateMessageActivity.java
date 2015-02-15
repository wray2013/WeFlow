package com.cmmobi.looklook.activity;

import java.io.File;
import java.util.Iterator;
import java.util.LinkedList;

import android.app.Activity;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Environment;
import android.os.Message;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.PopupWindow;
import cn.zipper.framwork.core.ZApplication;
import cn.zipper.framwork.core.ZDataPool;
import cn.zipper.framwork.core.ZLog;
import cn.zipper.framwork.utils.ZPercent;

import com.cmmobi.looklook.MainApplication;
import com.cmmobi.looklook.R;
import com.cmmobi.looklook.activity.login.MicShareWelcomeActivity;
import com.cmmobi.looklook.common.adapter.FriendsSessionPrivateMessageAdapter;
import com.cmmobi.looklook.common.constant.Constant.SEND_MSG_STATUS;
import com.cmmobi.looklook.common.gson.GsonResponse3;
import com.cmmobi.looklook.common.gson.GsonResponse3.MyMessage;
import com.cmmobi.looklook.common.gson.GsonResponse3.addfriendResponse;
import com.cmmobi.looklook.common.gson.Requester3;
import com.cmmobi.looklook.common.service.CoreService;
import com.cmmobi.looklook.common.storage.GsonHelper;
import com.cmmobi.looklook.common.view.InputRecoderView;
import com.cmmobi.looklook.common.view.InputRecoderView.AudioRecoderBean;
import com.cmmobi.looklook.common.view.InputRecoderView.InputStrType;
import com.cmmobi.looklook.common.view.InputRecoderView.OnSendListener;
import com.cmmobi.looklook.common.view.XEditDialog;
import com.cmmobi.looklook.common.view.pulltorefresh.PullToRefreshBase;
import com.cmmobi.looklook.common.view.pulltorefresh.PullToRefreshBase.OnRefreshListener2;
import com.cmmobi.looklook.common.view.pulltorefresh.PullToRefreshListView;
import com.cmmobi.looklook.downloader.BackgroundDownloader;
import com.cmmobi.looklook.info.profile.AccountInfo;
import com.cmmobi.looklook.info.profile.ActiveAccount;
import com.cmmobi.looklook.info.profile.ContactManager;
import com.cmmobi.looklook.info.profile.MediaValue;
import com.cmmobi.looklook.info.profile.MessageWrapper;
import com.cmmobi.looklook.info.profile.NetworkTaskInfo;
import com.cmmobi.looklook.info.profile.PrivateCommonMessage;
import com.cmmobi.looklook.info.profile.PrivateMessageManager;
import com.cmmobi.looklook.info.profile.PrivateSendMessage;
import com.cmmobi.looklook.info.profile.TimeHelper;
import com.cmmobi.looklook.info.profile.WrapUser;
import com.cmmobi.looklook.networktask.INetworkTask;
import com.cmmobi.looklook.networktask.UploadNetworkTask;
import com.cmmobi.looklook.offlinetask.OfflineTaskManager;
import com.cmmobi.looklook.prompt.Prompt;
import com.cmmobi.looklook.receiver.AttachNetworkTaskReceiver;
import com.cmmobi.looklook.receiver.CmmobiPushReceiver;
import com.google.gson.Gson;
import com.nostra13.universalimageloader.api.UniversalImageLoader;
/**
 *  
 *  与某个人的会话界面
 * */
public class FriendsSessionPrivateMessageActivity extends TitleRootFragmentActivity implements
		OnItemLongClickListener, OnScrollListener, OnRefreshListener2<ListView>,OnSendListener,OnTouchListener{

	public static final String BROADCAST_OFFLINE_NO_FRIENDS = "BROADCAST_OFFLINE_NO_FRIENDS";
	
	private static final String TAG = "FriendsSessionPrivateMessageActivity";
	private final int HANDLER_RECORD_DURATION_UPDATE_MSG = 0x81654001;
	private final int HANDLER_OFFLINE_UPDATE_MSG = 0x81625178;
	
	private PullToRefreshListView lv_lookfriends;
	private ListView chatListView;
	FriendsSessionPrivateMessageAdapter friendSessionMsgAdapter;
	
	private LinkedList<PrivateCommonMessage> data = new LinkedList<PrivateCommonMessage>();
	
	private String input_text;
	
	private String other_userid;
	private String my_userid;
	private String other_nick;
	private String mark_name;
	
	PopupWindow popupWindow;
	
	private ActiveAccount aa;
	private AccountInfo ai;
	private PrivateMessageManager pmm;
	private MessageWrapper mv;
	
	private Gson gson;
	private WrapUser wu;
		
	public static boolean heartBeatInterval = false;
	
	private InputRecoderView inpRecoderView;
	
	int[] icons = { R.drawable.wave1,
			R.drawable.wave2, R.drawable.wave3, };
	
	public static final int PUSH_LIST_REFRESH_COMPLETE = 0x7296487;
	
	private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
		  @Override
		  public void onReceive(Context context, Intent intent) {
				int type;
			    Message msg = new Message();
			    
			    boolean needSendHandler = true;
			    
				if(INetworkTask.ACTION_TASK_STATE_CHANGE.equals(intent.getAction())){
					/*type = intent.getIntExtra("type", 0);*/
					
					int state = intent.getIntExtra("taskState", 0);
				    String attachid = intent.getStringExtra("attachid");
					msg.what = AttachNetworkTaskReceiver.ATTACH_TASK_TYPE;
					msg.obj =  attachid;
				    Bundle b = new  Bundle();
				    b.putInt("state", state);
				    msg.setData(b);
				}else if(CoreService.ACTION_MESSAGE_DATA_UPDATE.equals(intent.getAction())){
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
							mediaValue.localpath = file.getAbsolutePath().replace(Environment.getExternalStorageDirectory().getPath(), "");
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
				}else if(LookLookActivity.CONTACTS_REFRESH_DATA.equals(intent.getAction()) 
						&& LookLookActivity.FRIEND_LIST_CHANGE.equals(intent.getExtras().get(LookLookActivity.CONTACTS_REFRESH_KEY))){			
					// 联系人变更
					 isFriendsInAdapter(true);
					
					
				}else if(BROADCAST_OFFLINE_NO_FRIENDS.equals(intent.getAction())){			
					// 发送离线任务，返回138119 发现不是好友· 
					// 联系人变更
					isFriendsInAdapter(false);
				}

				if(needSendHandler){
					FriendsSessionPrivateMessageActivity.this.handler.sendMessage(msg);
				}
				
		  }
		
    };
    

    
    @Override
	public int subContentViewId() {
		return R.layout.activity_friends_private_message;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		if(!ActiveAccount.getInstance(this).isLogin()){
			//loop to login page
			Intent jump_intent = new Intent(this, MicShareWelcomeActivity.class);
			startActivity(jump_intent);
			finish();
			return;
		}
		
		setLeftLong2Home();
		
		UniversalImageLoader.initImageLoader(MainApplication.getAppInstance(), ActiveAccount.getInstance(this).getLookLookID());
		gson = new Gson();
		other_userid = null;
		other_nick = null;
		mark_name = null;
		wu = null;
		mv = null;
		
		Bundle bundle = getIntent().getExtras();
		if(bundle!=null){
			other_userid = bundle.getString( "userid" );
			other_nick = bundle.getString( "nick_name" );
			mark_name = bundle.getString( "mark_name" );
			String wrapuser_str = bundle.getString( "wrapuser" );
			wu = gson.fromJson(wrapuser_str, WrapUser.class) ;
			Log.e(TAG, "onCreate - other_userid:" + other_userid + ", other_nick:" + other_nick + ", wrapuser_str:" + wrapuser_str);
		}else{
			Log.e(TAG, "userid is null");
			return;
		}

		inpRecoderView = (InputRecoderView) findViewById(R.id.inp_recoder_view);
		inpRecoderView.setOnSendListener(this);
		
		aa = ActiveAccount.getInstance(this);
		ai = AccountInfo.getInstance(aa.getLookLookID());
		pmm = ai.privateMsgManger;
				
		lv_lookfriends = (PullToRefreshListView) findViewById(R.id.chat_list);
		lv_lookfriends.setShowIndicator(false);
		lv_lookfriends.setOnRefreshListener(this);

		chatListView = lv_lookfriends.getRefreshableView();
		chatListView.setOnScrollListener(this);
		//chatListView.setOnItemClickListener(this);
		chatListView.setOnItemLongClickListener(this);
		chatListView.setOnTouchListener(this);
		
		
		
		if(aa.isLogin()){
			my_userid = aa.getLookLookID();
		}else{
			my_userid = null;
		}
		
		//不考虑未登录状态，若未登录消息通知不能发出来
		if(other_userid!=null){
			mv = pmm.get(other_userid);
			if(mv!=null && mv.msgs!=null && mv.nickname!=null){
				if(!TextUtils.isEmpty(mv.markname)){
					setTitle(mv.markname);
				}else{
					setTitle(mv.nickname);
				}
				other_nick = mv.nickname;
				mark_name = mv.markname;
				
				data.clear();
				data.addAll(mv.msgs);
				
				friendSessionMsgAdapter = new FriendsSessionPrivateMessageAdapter(chatListView, this, handler, data, other_nick,other_userid);
				chatListView.setAdapter(friendSessionMsgAdapter);
				chatListView.setSelection(chatListView.getCount());

			}else if(other_nick!=null){
				if(!TextUtils.isEmpty(mark_name)){
					setTitle(mark_name);
				}else{
					setTitle(other_nick);
				}
				MessageWrapper empty = new MessageWrapper(my_userid);
				empty.nickname = other_nick;
				empty.markname = mark_name;
				empty.other_userid = other_userid;
				pmm.put(other_userid, empty);
				mv = pmm.get(other_userid);
				
				data.clear();
				data.addAll(mv.msgs);
				
				friendSessionMsgAdapter = new FriendsSessionPrivateMessageAdapter(chatListView, this, handler, data, other_nick,other_userid);
				chatListView.setAdapter(friendSessionMsgAdapter);
				chatListView.setSelection(chatListView.getCount());
			}
		}

		if(wu!=null){
			other_userid = wu.userid;
			if(wu.nickname!=null){
				
				if(!TextUtils.isEmpty(wu.markname)){
					setTitle(wu.markname);
				}else{
					setTitle(wu.nickname);
				}
				mark_name = wu.markname;
				other_nick = wu.nickname;
			}
			
			mv = pmm.get(other_userid);
			if(mv!=null && mv.msgs!=null){
			}else{
				//发送私信时才创建MessageWrapper
				MessageWrapper mv = new MessageWrapper(my_userid);
				mv.other_userid = wu.userid;
				mv.nickname = wu.nickname;
				mv.markname = wu.markname;
				mv.headimageurl = wu.headimageurl;
				mv.sex = wu.sex;
				mv.signature = wu.signature;
				
				pmm.put(other_userid, mv);
				this.mv = pmm.get(other_userid);
			}

			data.clear();
			data.addAll(mv.msgs);
			
			friendSessionMsgAdapter = new FriendsSessionPrivateMessageAdapter(chatListView, this, handler, data, other_nick,other_userid);
			chatListView.setAdapter(friendSessionMsgAdapter);
			chatListView.setSelection(chatListView.getCount());
		}
		
		if(mv!=null){
			Log.d(TAG, "MessageWrapper :" + mv +  "str:" + GsonHelper.getInstance().getString(mv));
		}
		
		if(other_userid.equals(ai.serviceUser.userid)){
			hideRightButton();
		}else{
			showRightButton();
			getRightButton().setBackgroundResource(R.drawable.btn_friends_msg_space);	
		}
		
//		autoDownload();
		
		LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver,
			      new IntentFilter(INetworkTask.ACTION_TASK_STATE_CHANGE));
		LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver,
			      new IntentFilter(CoreService.ACTION_MESSAGE_DATA_UPDATE));
		LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver,
			      new IntentFilter(BackgroundDownloader.ACTION_DOWNLOAD_PROGRESS));
		LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver,
			      new IntentFilter(PrivateMessageManager.FLAG_OFFLINE_MESSAGE_UPDATE));
		LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver,
				new IntentFilter(FriendsSessionPrivateMessageActivity.BROADCAST_OFFLINE_NO_FRIENDS));
		LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver,
				new IntentFilter(LookLookActivity.CONTACTS_REFRESH_DATA));
		
		Intent msgIntent = new Intent(LookLookActivity.UPDATE_MASK);
		LocalBroadcastManager.getInstance(this).sendBroadcast(msgIntent);
		
		
		Requester3.reportReadMsg(handler, other_userid);
		
		inpRecoderView.setInputStrKey(InputStrType.PRIVATE_MSG, other_userid);
		
	}

	
	@Override
	public void onResume() {
		super.onResume();
		if(mv!=null){
			mv.consumeUnreadMsgs();
		}
		inpRecoderView.mRegisterReceiver();
		CmmobiPushReceiver.cancelNotification(this, CmmobiPushReceiver.NOTIFY_INDEX_PRIVATEMSG);
		heartBeatInterval = true; //5 sec
	}

	@Override
	public void onPause() {
		super.onPause();
		heartBeatInterval = false; //30 sec
	}

	@Override
	public void onStop(){
		super.onStop();
		inpRecoderView.mUnRegisterReceiver();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_title_left:
			this.finish();
			hideSoftInputFromWindow();
			break;
		case R.id.btn_title_right:
			
			ContactManager friendsListContactManager=ai.friendsListName;
			WrapUser currUserInfo = friendsListContactManager.findUserByUserid(other_userid);
			
			if(currUserInfo == null){
				// 不是好友
				if(other_userid.equals(ai.serviceUser.userid)){
					//launchOtherUserHomepage(other_userid);
				}else{
					new XEditDialog.Builder(v.getContext())
					.setTitle(R.string.xeditdialog_title)
					.setPositiveButton(R.string.send, new OnClickListener() {
						@Override
						public void onClick(View v) {
							//加好友
							Requester3.addFriend(handler, other_userid, v.getTag().toString());
						}
					})
					.setNegativeButton(android.R.string.cancel, null)
					.create().show();
				}
			}else{
				// 是好友
				launchOtherUserHomepage(other_userid);
			}
			break;
		default:
			break;
		}
	}

	private void launchOtherUserHomepage(String userid) {
		
		if(userid!=null){
			Intent intent = new Intent(this, OtherZoneActivity.class);
			intent.putExtra(OtherZoneActivity.OTHER_ZONE_USERID, userid);
			startActivity(intent);
		}else{
			Prompt.Alert(this, "非法的用户");
		}
		
	}
	
	/**
	 * flag true  拦截 不是好友 情况 并不显示， 其他三种情况不拦截
	 */
	private void isFriendsInAdapter(boolean flag) {
		boolean isFriends = isFriends();
		// 是好友
		if(!isFriends){
			if(flag){
				return;
			}
		}
		friendSessionMsgAdapter.setFriendsState(isFriends);
		friendSessionMsgAdapter.notifyDataSetChanged();
	}

	/**
	 * 判断是否是好友
	 * @return
	 */
	private boolean isFriends() {
		ContactManager friendsListContactManager=ai.friendsListName;
		WrapUser currUserInfo = friendsListContactManager.findUserByUserid(other_userid);
		
		boolean isFriends = false;
		if(currUserInfo == null){
			// 不是好友
			if(other_userid.equals(ai.serviceUser.userid)){
				isFriends = true;
			}else{
				isFriends = false;
			}
		}else{
			// 是好友
			isFriends = true;
		}
		return isFriends;
	}


	@Override
	public boolean handleMessage(Message msg) {
		switch(msg.what){
		case HANDLER_OFFLINE_UPDATE_MSG:
			
			data.clear();
			data.addAll(mv.msgs);
			
			if(friendSessionMsgAdapter!=null){
				friendSessionMsgAdapter.notifyDataSetChanged();
			}
			break;
		case Requester3.RESPONSE_TYPE_ADD_FRIEND:
			if (msg.obj != null) {
				addfriendResponse response = (addfriendResponse) msg.obj;
				if ("0".equals(response.status)) {
					if(response.target_userid == null){
						break;
					}
					Prompt.Alert(this, "好友申请已发送");
				}
			}
			break;
		case FriendsSessionPrivateMessageAdapter.HANDLER_DIARLY_PROGRESS_UPDATE:
			if(friendSessionMsgAdapter!=null && msg.obj!=null && msg.obj.equals(other_userid)){
				friendSessionMsgAdapter.notifyDataSetChanged();
			}
			break;
		case Requester3.RESPONSE_TYPE_SEND_MESSAGE:
			Log.e(TAG, "Requester2.RESPONSE_TYPE_SEND_MESSAGE:");
			//step3若为null则UI更新为失败，若status为0则继续判断是否需要上传语音
			try {

				GsonResponse3.sendmessageResponse obj = (GsonResponse3.sendmessageResponse) (msg.obj);
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
							updateStatusByUUID(obj.uuid, SEND_MSG_STATUS.ALL_DONE,obj.privatemsgid);
						}else if("3".equals(getMsgType(obj.uuid))){ //日记
							updateStatusByUUID(obj.uuid, SEND_MSG_STATUS.ALL_DONE,obj.privatemsgid);
						}else{//2,4语音或语音+文字
							if( "138115".equals(obj.status) || "138107".equals(obj.status)){
								updateStatusByUUID(obj.uuid, SEND_MSG_STATUS.ALL_DONE,obj.privatemsgid);
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
						updateStatusByUUID(obj.uuid, SEND_MSG_STATUS.RIA_FAIL,"");
					}


				}
				
				if(friendSessionMsgAdapter!=null){
					friendSessionMsgAdapter.notifyDataSetChanged();
				}

			} catch (Exception e) {
				e.printStackTrace();
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
		case CoreService.HANDLER_FLAG_MESSAGE_DATA_UPDATE:
			if((Boolean) msg.obj){
				
				data.clear();
				data.addAll(mv.msgs);
				
				if(friendSessionMsgAdapter!=null){
					friendSessionMsgAdapter.notifyDataSetChanged();
				}
				if(mv!=null){
					mv.consumeUnreadMsgs();
				}
				
			}			
			break;
		case FriendsSessionPrivateMessageAdapter.HANDLER_FLAG_MSG_RETRY:
			
			if(other_userid!=null && aa.isLogin() && msg.obj!=null){
				ai = AccountInfo.getInstance(aa.getLookLookID());
				pmm = ai.privateMsgManger;
				if(pmm!=null){
					mv = pmm.get(other_userid);
					if(mv!=null && mv.msgs!=null){
						int pos = (Integer)(msg.obj);
						PrivateCommonMessage  pcm_retry = mv.msgs.get(pos);
						pcm_retry.updateSendMsgStatus(SEND_MSG_STATUS.REG_DONE);
						// 文字私信 重复
						try {
							if("1".equals(pcm_retry.s_msg.privatemsgtype)){
								OfflineTaskManager.getInstance().addSendPrivateMsgTask(pcm_retry.s_msg.content, other_userid, "1", pcm_retry.s_msg.uuid);
							}else{
								OfflineTaskManager.getInstance().addSendPrivateMsgTask(pcm_retry.s_msg.content, other_userid, pcm_retry.s_msg.privatemsgtype, pcm_retry.getUUID());
							}
						} catch (Exception e) {
							e.printStackTrace();
						}
						
						data.clear();
						data.addAll(mv.msgs);
						
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
				if(pmm!=null){
					mv = pmm.get(other_userid);
					if(mv!=null && mv.msgs!=null){
						PrivateCommonMessage item = (PrivateCommonMessage)(msg.obj);
						mv.msgs.remove(item);
						if(mv.msgs.size() == 0){
							mv.content = "";
							mv.lastTimeMill = 0;
						}else{
							PrivateCommonMessage lastCommonMessage = mv.msgs.getLast();
							if(lastCommonMessage.send){
								mv.content = lastCommonMessage.s_msg.content;
								mv.lastTimeMill = lastCommonMessage.create_time;
							}else{
								mv.content = lastCommonMessage.r_msg.content;
								mv.lastTimeMill = lastCommonMessage.create_time;
							}
						}
						
						data.clear();
						data.addAll(mv.msgs);
						
						pmm.put(mv.other_userid, mv);
						
						String messageid = "";
						String messagetype = "";
						String target_userid = "";
						if(item.send){
							messageid = item.s_msg.privatemsgid;
							messagetype = "1"; //1 删除自己发送的消息 2 删除收到的消息
							target_userid = aa.getLookLookID();
						}else{
							messageid = item.r_msg.messageid;
							messagetype = "2";
							target_userid = item.userid;
						}
						
						if(TextUtils.isEmpty(messageid)||(item.s_msg!=null&& SEND_MSG_STATUS.ALL_DONE != item.s_msg.status)){
							Prompt.Alert("删除消息成功");
						}else{
							Requester3.deleteMessage(handler, messageid, messagetype, target_userid);
						}
						
						if(friendSessionMsgAdapter!=null){
							friendSessionMsgAdapter.notifyDataSetChanged();
						}
					}
					
				}
				
			}
			break;
		case Requester3.RESPONSE_TYPE_DELETE_MESSAGE:
			GsonResponse3.deleteMessageResponse delmsg = (GsonResponse3.deleteMessageResponse) (msg.obj);
			if(delmsg!=null && "0".equals(delmsg.status)){
				Prompt.Alert("删除消息成功");
			}else{
				Prompt.Alert("删除消息失败");
			}
			break;
		case Requester3.RESPONSE_TYPE_LIST_HISTORY_MESSAGE:
			
			try {
				
				GsonResponse3.listHistoryMessageResponse obj = (GsonResponse3.listHistoryMessageResponse) (msg.obj);
				if(obj!=null && "0".equals(obj.status)){
					//1有下一页，0没有下一页
					String hasnextpage = obj.hasnextpage;
					String last_timemilli = obj.last_timemilli;
					MyMessage[] arrs = obj.message;
					
//					if(arrs!=null && arrs.length>0){
					pmm.addHistoryMessage(arrs,other_userid,my_userid,wu);
//					}
					
				}
				
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			lv_lookfriends.onRefreshComplete();
			
			mv = pmm.get(other_userid);
			if(mv == null){
//				Toast.makeText(this, "mv is null ", 0).show();
			}else{
				
				data.clear();
				data.addAll(mv.msgs);
				
				friendSessionMsgAdapter = new FriendsSessionPrivateMessageAdapter(chatListView, this, handler, data, other_nick,other_userid);
				chatListView.setAdapter(friendSessionMsgAdapter);
				friendSessionMsgAdapter.notifyDataSetChanged();
			}
//			isFriendsInAdapter(false);
			break;
		case PUSH_LIST_REFRESH_COMPLETE:
			lv_lookfriends.onRefreshComplete();
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
		  LocalBroadcastManager.getInstance(this).unregisterReceiver(mMessageReceiver);
		  super.onDestroy();
		  Requester3.reportReadMsg(handler, other_userid);
	}
	
	
	
	private void updateStatus(SEND_MSG_STATUS old_status, SEND_MSG_STATUS new_status){
		if(other_userid!=null && aa.isLogin()){
			pmm = ai.privateMsgManger;
			if(pmm!=null){
				mv = pmm.get(other_userid);
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

					data.clear();
					data.addAll(mv.msgs);
					
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
			if(pmm!=null){
				mv = pmm.get(other_userid);
				if(mv!=null && mv.msgs!=null){
					Iterator<PrivateCommonMessage> it = mv.msgs.iterator();
					while(it.hasNext()){
						PrivateCommonMessage pcm = it.next();
						if(attachid!=null && attachid.equals(pcm.getPrivateMSGID())){
							pcm.updateSendMsgStatus(new_status);
						}
					}
					

					data.clear();
					data.addAll(mv.msgs);
					
					if(friendSessionMsgAdapter!=null){
						friendSessionMsgAdapter.notifyDataSetChanged();
					}
				}
				
			}
		}
	}
	
	private PrivateCommonMessage updateStatusByUUID(String uuid, SEND_MSG_STATUS new_status,String privatemsgid){
		if(other_userid!=null && aa.isLogin()){
			pmm = ai.privateMsgManger;
			if(pmm!=null){
				mv = pmm.get(other_userid);
				if(mv!=null && mv.msgs!=null){
					Iterator<PrivateCommonMessage> it = mv.msgs.iterator();
					while(it.hasNext()){
						PrivateCommonMessage pcm = it.next();
						if(uuid!=null && uuid.equals(pcm.getUUID())){
							pcm.updateSendMsgStatus(new_status);
							if(!TextUtils.isEmpty(privatemsgid)){
								pcm.s_msg.privatemsgid = privatemsgid;
							}
							return pcm;
						}
					}

					data.clear();
					data.addAll(mv.msgs);
				}
				
			}
		}
		
		return null;
	}
	
/*	private PrivateCommonMessage updateStatusByUUIDInPcm(String uuid, SEND_MSG_STATUS new_status){
		if(audiopcm!= null && uuid!=null && uuid.equals(audiopcm.getUUID())){
			audiopcm.updateSendMsgStatus(new_status);
			return audiopcm;
		}
		return null;
	}
*/	
	private void removeMsgByUUID(String uuid){
		if(other_userid!=null && aa.isLogin()){
			pmm = ai.privateMsgManger;
			if(pmm!=null){
				mv = pmm.get(other_userid);
				if(mv!=null && mv.msgs!=null){
					Iterator<PrivateCommonMessage> it = mv.msgs.iterator();
					while(it.hasNext()){
						PrivateCommonMessage pcm = it.next();
						if(uuid!=null && uuid.equals(pcm.getUUID())){
							it.remove();
						}
					}

					data.clear();
					data.addAll(mv.msgs);
				}
				
			}
		}
	}
	
	
	private String findFirstItemTimemilli(){
		String timemilli = "";
		if(other_userid!=null && aa.isLogin()){
			pmm = ai.privateMsgManger;
			if(pmm!=null){
				MessageWrapper mv = pmm.get(other_userid);
				if(mv!=null && mv.msgs!=null){
					Iterator<PrivateCommonMessage> it = mv.msgs.iterator();
					while(it.hasNext()){
						try {
							PrivateCommonMessage pcm = it.next();
							if(pcm.send){
								if(SEND_MSG_STATUS.ALL_DONE != pcm.s_msg.status){
									continue;
								}
							}
							timemilli = pcm.create_time + "";
							break;
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}
			}
		}
		
		if(TextUtils.isEmpty(timemilli)){
			timemilli = "0";
		}
		return timemilli;
	}
	
	
	private void updateSendMsgByUUID(String uuid, GsonResponse3.sendmessageResponse obj){
		if(other_userid!=null && aa.isLogin()){
			pmm = ai.privateMsgManger;
			if(pmm!=null){
				mv = pmm.get(other_userid);
				if(mv!=null && mv.msgs!=null){
					Iterator<PrivateCommonMessage> it = mv.msgs.iterator();
					while(it.hasNext()){
						PrivateCommonMessage pcm = it.next();
						if(uuid!=null && uuid.equals(pcm.getUUID())){
							pcm.updateSendMsg(obj);
						}
					}

					data.clear();
					data.addAll(mv.msgs);
				}
				
			}
		}
	}
	
	private String getLocalFilePath(String uuid){
		if(other_userid!=null && aa.isLogin()){
			pmm = ai.privateMsgManger;
			if(pmm!=null){
				mv = pmm.get(other_userid);
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
			if(pmm!=null){
				mv = pmm.get(other_userid);
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
		inpRecoderView.hideSoftInputFromWindow();
		inpRecoderView.hideExpressionView();
	}

	@Override
	public boolean onTouch(View view, MotionEvent event) {
	  if (event.getAction() == MotionEvent.ACTION_DOWN) {  
            View v = inpRecoderView;
            if (isShouldHideInput(v, event)) {
                inpRecoderView.hideExpressionView();
                inpRecoderView.hideSoftInputFromWindow();
                return false; //隐藏键盘时，其他控件不响应点击事件
            }
        }
        return false;  
	}
    
    public static boolean isShouldHideInput(View v, MotionEvent event) {
        if (v != null) {
            int[] leftTop = { 0, 0 };
            v.getLocationInWindow(leftTop);
            int left = leftTop[0], top = leftTop[1], bottom = top + v.getHeight(), right = left
                    + v.getWidth();
            if (event.getX() > left && event.getX() < right
                    && event.getY() > top && event.getY() < bottom) {
                return false;
            } else {
                return true;
            }
        }
        return false;
    }   
    
	@Override
	public void onScroll(AbsListView view, int firstVisibleItem,
			int visibleItemCount, int totalItemCount) {
	}

	@Override
	public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
		Requester3.listHistoryMessage(handler, other_userid, findFirstItemTimemilli(), "20");
		
	}

	@Override
	public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
		
		handler.sendEmptyMessage(PUSH_LIST_REFRESH_COMPLETE);
	}


	/**
	 * 此处已经废弃
	 */
	@Override
	public boolean onItemLongClick(AdapterView<?> parent, View v,
			int position, long id) {
		
		final PrivateCommonMessage item = (PrivateCommonMessage) parent.getAdapter().getItem(position);
		if(item == null){
			return false;
		}
		dialog = CommentDialogFragment.newInstance(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(dialog!= null){
					dialog.dismiss();
				}
				switch (v.getId()) {
				case R.id.btn_comment_reply:
					
					String privatemsgtype = "2";   //私信类型，1文字 2语音，3日记，4语音加文字
					String content = "";
		    		if(item.send){
		    			privatemsgtype = item.s_msg.privatemsgtype;
		    			content = item.s_msg.content;
					}else{
						privatemsgtype = item.r_msg.privmsg.privmsg_type;
						content = item.r_msg.privmsg.content;
					}
		    		
		    		if("1".equals(privatemsgtype)){
		    			ClipboardManager copy = (ClipboardManager) FriendsSessionPrivateMessageActivity.this 
		    					.getSystemService(Context.CLIPBOARD_SERVICE); 
		    			copy.setPrimaryClip(ClipData.newPlainText(null, content)); 
		    			Prompt.Alert(FriendsSessionPrivateMessageActivity.this, "复制成功");
		    		}
					
					break;
				case R.id.btn_comment_delete:
					handler.obtainMessage(FriendsSessionPrivateMessageAdapter.HANDLER_FLAG_MSG_DELETE, item).sendToTarget();
					break;
				default:
					break;
				}
			}
		}, item);
		dialog.show(this.getSupportFragmentManager(), "dialog");
		return false;
	}
	CommentDialogFragment dialog= null;
	
	private void EnableInput(boolean flag){
		getLeftButton().setClickable(flag);
		getLeftButton().setLongClickable(flag);
		getRightButton().setClickable(flag);
		getRightButton().setLongClickable(flag);
		chatListView.setClickable(flag);
	}
	
	
	public static class CommentDialogFragment extends DialogFragment {

		private static CommentDialogFragment dialog;
		private OnClickListener listener;
		private PrivateCommonMessage data;
		
		public static CommentDialogFragment newInstance(OnClickListener listener,PrivateCommonMessage data){
			if(dialog == null){
				dialog = new CommentDialogFragment();
			}
			dialog.listener = listener;
			dialog.data = data;
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
        	
    		v = inflater.inflate(R.layout.dialogfragment_comment_operate_three, null);
    		
    		
    		String privatemsgtype = "2";   //私信类型，1文字 2语音，3日记，4语音加文字
    		if(data.send){
    			privatemsgtype = data.s_msg.privatemsgtype;
			}else{
				privatemsgtype = data.r_msg.privmsg.privmsg_type;
			}
    		
    		reply = (Button) v.findViewById(R.id.btn_comment_reply);
    		if("1".equals(privatemsgtype)){
    			reply.setText("复制");
    			reply.setOnClickListener(listener);
    		}else{
    			reply.setVisibility(View.GONE);
    		}
    		
    		delete = (Button) v.findViewById(R.id.btn_comment_delete);
    		delete.setOnClickListener(listener);
    		delete.setBackgroundResource(R.drawable.btn_menu_one);
    		delete.setTag(data);
    		
//			delete.setVisibility(View.GONE);
//			reply.setBackgroundResource(R.drawable.btn_menu_one);
    		
    		cancel = (Button) v.findViewById(R.id.btn_comment_cancel);
    		cancel.setOnClickListener(new OnClickListener(){
				@Override
				public void onClick(View v){
					d.dismiss();
				}
			});
        	
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


	@Override
	public void onSend(AudioRecoderBean bean) {
		
		
		if(mv!=null && mv.msgs!=null){

		}else if(wu!=null && wu.userid!=null && wu.nickname!=null){
			//发送私信时才创建MessageWrapper
			mv = new MessageWrapper(my_userid);
			mv.other_userid = wu.userid;
			mv.nickname = wu.nickname;
			mv.markname = wu.markname;
			mv.headimageurl = wu.headimageurl;
			mv.sex = wu.sex;
			mv.signature = wu.signature;
			/*pmm.put(mv.other_userid, mv);*/
		}else if(other_userid!=null){
			//发送私信时才创建MessageWrapper
			mv = new MessageWrapper(my_userid);
			mv.other_userid = other_userid;
			mv.nickname = other_nick;
			mv.markname = mark_name;
			if(wu!=null){
				mv.headimageurl = wu.headimageurl;
				mv.sex = wu.sex;
				mv.signature = wu.signature;
			}
			/*pmm.put(mv.other_userid, mv);*/
		}else{
			Prompt.Alert(this, "非法的用户");
			return;
		}
		
		mv.setToShow(true);
		
		ai.recentContactManager.addRecentContact(wu);
		// 文字
		if("1".equals(bean.commenttype)){
			
			PrivateSendMessage send_msg = new PrivateSendMessage(my_userid, other_userid, bean.commentuuid);
			send_msg.setContent(bean.content);
			send_msg.setPrivateMsgtype("1");
			send_msg.updateStatus(SEND_MSG_STATUS.REG_DONE);

			mv.msgs.add(new PrivateCommonMessage(send_msg));
			mv.privmsg_type = "1";
			mv.content = bean.content;
			mv.act = "1";
			mv.lastTimeMill = TimeHelper.getInstance().now();

			OfflineTaskManager.getInstance().addSendPrivateMsgTask(bean.content, other_userid, "1", bean.commentuuid);
			pmm.put(mv.other_userid, mv);
			//saveAccount();
			
		}else if("2".equals(bean.commenttype) || "3".equals(bean.commenttype)){
			
			PrivateSendMessage send_msg = new PrivateSendMessage(my_userid, other_userid, bean.commentuuid);
			send_msg.setLocalFilePath(bean.localFilePath);
			send_msg.updateStatus(SEND_MSG_STATUS.REC_DONE);
			send_msg.content = "";
			send_msg.setPrivateMsgtype("2");
			send_msg.playtime = bean.playtime;
			
			PrivateCommonMessage audiopcm = new PrivateCommonMessage(send_msg);
			
			mv.privmsg_type = "2";
			mv.act = "1";
			mv.content = "[语音]";
			mv.lastTimeMill = TimeHelper.getInstance().now();
			mv.msgs.add(audiopcm);
			
			OfflineTaskManager.getInstance().addSendPrivateMsgTask(audiopcm.getContent(), other_userid, mv.privmsg_type, bean.commentuuid);
			pmm.put(mv.other_userid, mv);
			//saveAccount();
			
		}

		data.clear();
		data.addAll(mv.msgs);

		if(friendSessionMsgAdapter!=null){
			friendSessionMsgAdapter.notifyDataSetChanged();
		}else{
			friendSessionMsgAdapter = new FriendsSessionPrivateMessageAdapter(chatListView, this, handler, data, other_nick,other_userid);
			chatListView.setAdapter(friendSessionMsgAdapter);
			chatListView.setSelection(chatListView.getCount());
		}
//		isFriendsInAdapter(false);
		
	}
	
	
	
	/**
	 * 隐藏键盘
	 */
	private void hideSoftInputFromWindow(){
		try {
			InputMethodManager	inputMethodManager = ((InputMethodManager) this.getSystemService(Activity.INPUT_METHOD_SERVICE));
			if(this.getCurrentFocus()!=null){
				inputMethodManager.hideSoftInputFromWindow(this.getCurrentFocus().getWindowToken(),
						InputMethodManager.HIDE_NOT_ALWAYS);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
