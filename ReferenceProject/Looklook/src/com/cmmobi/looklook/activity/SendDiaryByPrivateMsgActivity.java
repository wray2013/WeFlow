package com.cmmobi.looklook.activity;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import cn.zipper.framwork.core.ZActivity;
import cn.zipper.framwork.core.ZApplication;

import com.cmmobi.looklook.R;
import com.cmmobi.looklook.common.adapter.FriendsCircleContactsAdapter;
import com.cmmobi.looklook.common.adapter.FriendsSendPrivateMsgContactsAdapter;
import com.cmmobi.looklook.common.constant.Constant.SEND_MSG_STATUS;
import com.cmmobi.looklook.common.gson.GsonResponse2;
import com.cmmobi.looklook.common.gson.GsonResponse2.MyDiary;
import com.cmmobi.looklook.common.gson.GsonResponse2.myattentionlistResponse;
import com.cmmobi.looklook.common.gson.GsonResponse2.myattentionlistUsers;
import com.cmmobi.looklook.common.gson.Requester2;
import com.cmmobi.looklook.common.storage.GsonHelper;
import com.cmmobi.looklook.common.utils.CmmobiClickAgentWrapper;
import com.cmmobi.looklook.common.utils.UmengclickAgentWrapper;
import com.cmmobi.looklook.common.view.QuickBarView;
import com.cmmobi.looklook.fragment.WrapSelectedUser;
import com.cmmobi.looklook.fragment.WrapUser;
import com.cmmobi.looklook.info.profile.AccountInfo;
import com.cmmobi.looklook.info.profile.ActiveAccount;
import com.cmmobi.looklook.info.profile.FriendsAttentionManager;
import com.cmmobi.looklook.info.profile.MessageWrapper;
import com.cmmobi.looklook.info.profile.PrivateCommonMessage;
import com.cmmobi.looklook.info.profile.PrivateMessageManager;
import com.cmmobi.looklook.info.profile.PrivateSendMessage;
import com.cmmobi.looklook.info.profile.TimeHelper;
import com.cmmobi.looklook.offlinetask.OfflineTaskManager;
import com.cmmobi.looklook.prompt.Prompt;
import com.cmmobi.sns.utils.PinYinUtil;

public class SendDiaryByPrivateMsgActivity extends ZActivity implements OnItemClickListener{

	private static final String TAG = "SendDiaryByPrivateMsgActivity";
	private final int HANDLER_OFFLINE_UPDATE_MSG = 0x71625178;
	private String userID;
	
	private ActiveAccount activeAcct;
	private AccountInfo accountInfo;
	FriendsAttentionManager friendsAttentionManager;
	ListView friendsCircleContactsListView;
	ListView headerList = null;
	QuickBarView quickBarView;
	private ImageView ivDoneBtn;
	private ImageView ivBackBtn;
	private myattentionlistUsers[] userList;
	private ArrayList<WrapSelectedUser> wrapUserList = new ArrayList<WrapSelectedUser>();
	private ArrayList<WrapUser> wrapList = new ArrayList<WrapUser>();
	private FriendsSendPrivateMsgContactsAdapter friendsCircleContactsAdapter;
	private String diaryID = "";
	private MyDiary myDiary;
	private boolean isClean = true;
	private PrivateMessageManager pmm;
	
	private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
		  @Override
		  public void onReceive(Context context, Intent intent) {

			    Message msg = new Message();
			    
			    // Get extra data included in the Intent
				if(PrivateMessageManager.FLAG_OFFLINE_MESSAGE_UPDATE.equals(intent.getAction())){
					String userid = intent.getStringExtra("userid");
				    msg.what = HANDLER_OFFLINE_UPDATE_MSG;
				    msg.obj = userid;
				}

				SendDiaryByPrivateMsgActivity.this.handler.sendMessage(msg);
				
		  }
		
  };
  
  
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_send_privatemsg);
		activeAcct = ActiveAccount.getInstance(this);
		userID = activeAcct.getUID();
		accountInfo = AccountInfo.getInstance(userID);
		pmm = accountInfo.privateMsgManger;
		
		friendsAttentionManager = accountInfo.friendsAttentionManager;
		
		friendsCircleContactsListView = (ListView) findViewById(R.id.friends_attention_contacts_list);

		
		quickBarView = (QuickBarView) findViewById(R.id.quick_bar);

		quickBarView.setListView(friendsCircleContactsListView);
		ivDoneBtn = (ImageView) findViewById(R.id.iv_privatemsg_done);
		ivDoneBtn.setOnClickListener(this);
		ivBackBtn = (ImageView) findViewById(R.id.iv_privatemsg_back);
		ivBackBtn.setOnClickListener(this);
		

		handler = new Handler(this);
		Log.e(TAG, "myDiary:" + getIntent().getStringExtra("myDiary"));
		diaryID = getIntent().getStringExtra("diaryID");
		myDiary = (MyDiary) GsonHelper.getInstance().getObject(getIntent().getStringExtra("myDiary"), MyDiary.class);

		friendsCircleContactsAdapter = new FriendsSendPrivateMsgContactsAdapter(
				this, FriendsCircleContactsAdapter.TAB_ATTENTION);

		friendsCircleContactsListView.setAdapter(friendsCircleContactsAdapter);

		loadLoacalData();
		Requester2.requestAttentionList(handler, "",
				ActiveAccount.getInstance(ZApplication.getInstance())
						.getLookLookID(), "50");
		
		friendsCircleContactsListView.setOnItemClickListener(this);
	}
	
	@Override
	public void onResume() {
		super.onResume();
		UmengclickAgentWrapper.onResume(this);
		CmmobiClickAgentWrapper.onResume(this);
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
	}
	
	private void loadLoacalData() {
		ArrayList<WrapUser> cache = new ArrayList<WrapUser>(friendsAttentionManager.getCache());
		int size = cache.size();
		if (size > 0) { 
			wrapUserList.clear();
			for(int i = 0; i < size; i++) {
				WrapSelectedUser user = new WrapSelectedUser(cache.get(i),false);
				wrapUserList.add(user);
			}
			
			friendsCircleContactsAdapter.setData(wrapUserList);
		}
	}
	
	private void loadRecentContactsList() {
		String userId = ActiveAccount.getInstance(ZApplication.getInstance()).getUID();
		ArrayList<WrapUser> recentContactsList = AccountInfo.getInstance(userId).recentContactsList;
		LinkedList<WrapSelectedUser> lastUserList = new LinkedList<WrapSelectedUser>();
		int listSize = recentContactsList.size();
		for (int i = 0;i < listSize;i++) {
			WrapSelectedUser user = new WrapSelectedUser();
			WrapUser wrapUser = recentContactsList.get(i);
			user.attentioncount = wrapUser.attentioncount;
			user.diarycount = wrapUser.diarycount;
			user.fanscount = wrapUser.fanscount;
			user.headimageurl = wrapUser.headimageurl;
			user.nickname = wrapUser.nickname;
			user.sex = wrapUser.sex;
			user.signature = wrapUser.signature;
			user.userid = wrapUser.userid;
			user.sortKey = "最近联系人";
			user.isSelected = false;
			lastUserList.add(user);
		}
		friendsCircleContactsAdapter.setHeaderList(lastUserList, "最近联系人");
	}

	@Override
	public boolean handleMessage(Message msg) {
		// TODO Auto-generated method stub
		switch (msg.what) {
		case Requester2.RESPONSE_TYPE_MY_ATTENTIONLIST:
			if (msg.obj != null) {
				myattentionlistResponse attentionList = (myattentionlistResponse) msg.obj;

				if ("0".equals(attentionList.status)) {
					userList = attentionList.users;
					
					if (isClean) {
						wrapUserList.clear();
						friendsCircleContactsAdapter.notifyDataSetChanged();
						isClean = false;
					}
					for (int i = 0; i < userList.length; i++) {

						WrapUser wrapUser = new WrapUser();

						wrapUser.userid = userList[i].userid;
						wrapUser.headimageurl = userList[i].headimageurl;
						wrapUser.nickname = userList[i].nickname;
						wrapUser.diarycount = userList[i].diarycount;
						wrapUser.attentioncount = userList[i].attentioncount;
						wrapUser.fanscount = userList[i].fanscount;
						wrapUser.sex = userList[i].sex;
						wrapUser.signature = userList[i].signature;
						String sortKey = PinYinUtil
								.getPinYin(userList[i].nickname);
						wrapUser.sortKey = sortKey;
						if (!wrapList.contains(wrapUser)) {
							wrapList.add(wrapUser);
							wrapUserList.add(new WrapSelectedUser(wrapUser,false));
						}
					}

					if ("1".equals(attentionList.hasnextpage )) {
						Requester2.requestAttentionList(handler, attentionList.user_time,
								ActiveAccount.getInstance(ZApplication.getInstance())
										.getLookLookID(), "50");
					} else {
						Log.d(TAG,"attentionList all are read");
						friendsAttentionManager.refreshCache(new LinkedList<WrapUser>(wrapList));
						friendsCircleContactsAdapter.setData(wrapUserList);
						loadRecentContactsList();
						friendsCircleContactsAdapter.notifyDataSetChanged();
					}

				} 
			}
			
			break;
		case Requester2.RESPONSE_TYPE_SEND_MESSAGE:
			//step3若为null则UI更新为失败，若status为0则继续判断是否需要上传语音
			try {
				GsonResponse2.sendmessageResponse obj = (GsonResponse2.sendmessageResponse) (msg.obj);
				if(obj!=null && obj.status.equals("0")){
					Prompt.Alert(this, "发送成功");
					String userId = ActiveAccount.getInstance(ZApplication.getInstance()).getUID();
					ArrayList<WrapUser> recentContactsList = AccountInfo.getInstance(userId).recentContactsList;
					for(WrapSelectedUser user:wrapUserList) {
						if (user.isSelected) {
							WrapUser wrapUser = new WrapUser();
							wrapUser.attentioncount = user.attentioncount;
							wrapUser.diarycount = user.diarycount;
							wrapUser.fanscount = user.fanscount;
							wrapUser.headimageurl = user.headimageurl;
							wrapUser.nickname = user.nickname;
							wrapUser.sex = user.sex;
							wrapUser.signature = user.signature;
							wrapUser.userid = user.userid;
							wrapUser.sortKey = user.sortKey;
							
							updateStatusByUUID(wrapUser.userid, obj.uuid, SEND_MSG_STATUS.ALL_DONE);
							
							if (recentContactsList.contains(wrapUser)) {
								recentContactsList.remove(wrapUser);
							}
							recentContactsList.add(0, wrapUser);
							int listSize = recentContactsList.size();
							if (listSize > 5) {
								for (int i = listSize - 1;i > 4; i--) {
									recentContactsList.remove(i);
								}
							}
							
						}
					}
					finish();
				}else{
					for(WrapSelectedUser user:wrapUserList) {
						if (user.isSelected) {
							WrapUser wrapUser = new WrapUser();
							wrapUser.attentioncount = user.attentioncount;
							wrapUser.diarycount = user.diarycount;
							wrapUser.fanscount = user.fanscount;
							wrapUser.headimageurl = user.headimageurl;
							wrapUser.nickname = user.nickname;
							wrapUser.sex = user.sex;
							wrapUser.signature = user.signature;
							wrapUser.userid = user.userid;
							wrapUser.sortKey = user.sortKey;
							
							updateStatusByUUID(wrapUser.userid, null, SEND_MSG_STATUS.RIA_FAIL);
						}
						
					}
					Prompt.Alert(this, "发送失败！");
					finish();
				}
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				finish();
			}
			break;
		default:
			break;
		}
		return false;
	}

	private boolean updateStatusByUUID(String other_userid, String uuid, SEND_MSG_STATUS new_status) {
		// TODO Auto-generated method stub
		boolean ret = false;
		if(activeAcct.isLogin()){
			if(pmm!=null && pmm.messages!=null){
				MessageWrapper mv = pmm.messages.get(other_userid);
				if(mv!=null && mv.msgs!=null){
					Iterator<PrivateCommonMessage> it = mv.msgs.iterator();
					while(it.hasNext()){
						PrivateCommonMessage pcm = it.next();
						if(uuid!=null){
							if( uuid.equals(pcm.getUUID())){
								pcm.updateSendMsgStatus(new_status);
								ret = true;
							}
						}else{
							pcm.updateSendMsgStatus(new_status);
						}

						
					}

				}
				
			}
		}
		
		return ret;
	}

	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		switch(arg0.getId()) {
		case R.id.iv_privatemsg_done:
			String ids = "";
			ArrayList<WrapSelectedUser> userList = new ArrayList<WrapSelectedUser>();
			for(WrapSelectedUser user:wrapUserList) {
				if (user.isSelected) {
					if (!userList.contains(user)) {
						ids += user.userid + ",";
						userList.add(user);
					}
				}
			}
			
			if (ids.endsWith(",")) {
				ids = ids.substring(0, ids.length() - 1);
			}
			
			if (!"".equals(ids) && myDiary!=null) {
				long lastTimeMill = TimeHelper.getInstance().now();
				String pendingMsgID =  String.valueOf(lastTimeMill);
				for(WrapSelectedUser user : userList){
					PrivateSendMessage send_msg = new PrivateSendMessage(userID, user.userid, pendingMsgID);
					send_msg.setContent("日记私信");
					send_msg.setPrivateMsgtype("3");
					send_msg.diaryid = myDiary.diaryid;
					send_msg.diaries = myDiary;
					Log.e(TAG, "OnClick - diaries:" + GsonHelper.getInstance().getString(myDiary));
					send_msg.updateStatus(SEND_MSG_STATUS.REG_DONE);
					MessageWrapper mv = pmm.messages.get(user.userid);
					if(mv==null){
						mv = new MessageWrapper();
						mv.userid = user.userid;
						mv.nickname = user.nickname;
						mv.headimageurl = user.headimageurl;
						mv.sex = user.sex;
						mv.signature = user.signature;
						pmm.messages.put(mv.userid, mv);
					}
					mv.msgs.add(new PrivateCommonMessage(send_msg));
					mv.privmsg_type = "3";
					mv.act = "1";
					mv.content = "日记私信";//"来自looklook的分享,http://" + myDiary.shorturl;
					mv.lastTimeMill = lastTimeMill;
				}

				//Requester2.sendMessage(getHandler(), diaryID, "日记分享", ids, "3", pendingMsgID);
				OfflineTaskManager.getInstance().addSendPrivateMsgTask(diaryID,myDiary.diaryuuid, "日记分享", ids, "3", pendingMsgID,myDiary.userid);
			} else {
				
			}
			finish();
			break;
		case R.id.iv_privatemsg_back:
			finish();
			break;
		}
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		// TODO Auto-generated method stub
		friendsCircleContactsAdapter.setItemSelected(view, position);
		WrapSelectedUser wrapSelectUser = wrapUserList.get(position);
		boolean isSelected = wrapSelectUser.isSelected;
		int listSize = wrapUserList.size();
		for(int i = 0;i < listSize;i++) {
			WrapSelectedUser user = wrapUserList.get(i);
			if (user.equals(wrapSelectUser)) {
				user.isSelected = isSelected;
			}
		}
		friendsCircleContactsAdapter.notifyDataSetChanged();
	}

}
