package com.cmmobi.looklook.receiver;

import java.util.Collections;
import java.util.List;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import android.support.v4.content.LocalBroadcastManager;
import cn.zipper.framwork.core.ZApplication;
import cn.zipper.framwork.core.ZLog;

import com.cmmobi.looklook.MainApplication;
import com.cmmobi.looklook.activity.LookLookActivity;
import com.cmmobi.looklook.common.gson.GsonResponse3;
import com.cmmobi.looklook.common.gson.GsonResponse3.MyUserids;
import com.cmmobi.looklook.common.gson.GsonResponse3.friendNewsResponse;
import com.cmmobi.looklook.common.gson.GsonResponse3.friendRequestListResponse;
import com.cmmobi.looklook.common.gson.GsonResponse3.getOfficialUseridsResponse;
import com.cmmobi.looklook.common.gson.GsonResponse3.myattentionlistResponse;
import com.cmmobi.looklook.common.gson.GsonResponse3.myblacklistResponse;
import com.cmmobi.looklook.common.gson.GsonResponse3.myfanslistResponse;
import com.cmmobi.looklook.common.gson.GsonResponse3.myfriendslistResponse;
import com.cmmobi.looklook.common.gson.Requester3;
import com.cmmobi.looklook.common.service.CoreService;
import com.cmmobi.looklook.common.service.RemoteManager;
import com.cmmobi.looklook.common.view.ContactsComparator;
import com.cmmobi.looklook.info.profile.AccountInfo;
import com.cmmobi.looklook.info.profile.ActiveAccount;
import com.cmmobi.looklook.info.profile.ContactManager;
import com.cmmobi.looklook.info.profile.WrapUser;
import com.cmmobi.sns.utils.PinYinUtil;

public class UserDatasMessageReceiver extends BroadcastReceiver implements Callback {
	private static final String TAG = "UserDatasMessageReceiver";
	public static String REFRESH_FRIEND_LIST = "REFRESH_FRIEND_LIST";
	public static String REFRESH_FRIEND_REQUEST_LIST = "REFRESH_FRIEND_REQUEST_LIST";
	public Handler handler;

	private String userID;
	private AccountInfo accountInfo;
	
	private Boolean isFriendsRefresh = false;

	@Override
	public void onReceive(Context context, Intent intent) {
		userID = ActiveAccount.getInstance(ZApplication.getInstance()).getUID();
		accountInfo = AccountInfo.getInstance(userID);
		handler = new Handler(this);
		if (CoreService.ACTION_MESSAGE_DATA_UPDATE.equals(intent
				.getAction()) && intent.getExtras() != null) {
			ZLog.printObject("====" + intent);

			String new_requestnum = intent.getExtras().getString(
					"new_requestnum"); // 新增好友请求数
			if (new_requestnum !=null && ! new_requestnum.isEmpty() && !"0".equals(new_requestnum)) {
				Requester3.friendRequestList(handler,
						accountInfo.t_friendsRequestList);
			}
			
			String new_friend = intent.getExtras().getString(
					"new_friend"); // 是否有新好友new_friend
			String friendnum = intent.getExtras().getString("friendnum");
			if ("1".equals(new_friend) || (friendnum != null && !"".equals(friendnum) && !friendnum
							.equals(accountInfo.friendsListName.getCache()
									.size() + ""))) {
				isFriendsRefresh = false;
				Requester3.requestMyFriendsList(handler,
						accountInfo.t_friendsList, userID);
			}
			
			String new_zonemicnum = intent.getExtras().getString(
					"new_zonemicnum"); // 微享新动态数时间戳
			if ("1".equals(new_zonemicnum)) {
				String time = "";
				String refresh_type = "";
				if (accountInfo.vshareDataEntities.getCache().size() > 0) {
					time = accountInfo.vshareDataEntities.getCache().get(0).update_time;
					refresh_type = "1";
				}
				Requester3.myMicList(handler, time, refresh_type, "0");
			}



			String new_friend_change = intent.getExtras().getString(
					"new_friend_change"); // 好友动态数
			if ("1".equals(new_friend_change)) {
				Requester3.requestFriendNews(handler, "", "");
			}
			
		
		}else if(REFRESH_FRIEND_LIST.equals(intent.getAction())){
			isFriendsRefresh = false;
			Requester3.requestMyFriendsList(handler,
					accountInfo.t_friendsList, userID);
		}else if(REFRESH_FRIEND_REQUEST_LIST.equals(intent.getAction())){
			Requester3.friendRequestList(handler,
					accountInfo.t_friendsRequestList);
		}
	}

	@Override
	public boolean handleMessage(Message msg) {
		// TODO Auto-generated method stub
		switch (msg.what) {
		case Requester3.RESPONSE_TYPE_MY_FRIENDS_LIST:
			if (msg.obj != null) {
				myfriendslistResponse friendList = (myfriendslistResponse) msg.obj;
				if ("0".equals(friendList.status)) {
					WrapUser[] userList = friendList.users;

					if (friendList.removeusers != null
							&& !friendList.removeusers.equals("")) {
						String[] removeusers = friendList.removeusers
								.split(",");
						if(removeusers.length>0){
							isFriendsRefresh = true;
							for (int i = 0; i < removeusers.length; i++) {
								accountInfo.friendsListName
										.removeMemberByUserid(removeusers[i].trim());
								accountInfo.friendNewsDataEntities.removeUserNews(removeusers[i].trim());
								//accountInfo.friendsRequestList.removeMemberByUserid(removeusers[i].trim());
								accountInfo.recentContactManager.removeMember(accountInfo.recentContactManager.findUserByUserid(removeusers[i].trim()));
								Requester3.friendRequestList(handler,
										accountInfo.t_friendsRequestList);
								// 消息好友状态变更
								accountInfo.privateMsgManger.Friends2Stranger(removeusers[i].trim());
								//accountInfo.recentContactManager.removeMemberByUserid(removeusers[i].trim());
							}
						}
					}
					
					if(userList.length>0){
						isFriendsRefresh = true;
						for (int i = 0; i < userList.length; i++) {
							accountInfo.friendsListName.addMember(userList[i]);
							WrapUser user = accountInfo.recentContactManager.findRecentContact(userList[i]);
							int index = accountInfo.recentContactManager.getCache().indexOf(user);
							if(user != null){
								accountInfo.recentContactManager.removeMember(user);
								accountInfo.recentContactManager.addRecentContact(index, userList[i]);
							}
							user = accountInfo.phoneUsers.findNewUserPhone(userList[i]);
							if(user != null){
								accountInfo.phoneUsers.removeMember(user);
							}	
						}
					}
					if ("1".equals(friendList.hasnextpage)) {
						Requester3.requestMyFriendsList(handler,
								friendList.user_time, userID);
					} else {
						/*List<WrapUser> wrapUserList = accountInfo.friendsListName.getCache();
						for (int i = 0; i < wrapUserList.size(); i++) {
							WrapUser wrapUser = wrapUserList.get(i);
								if(wrapUser.markname != null && !wrapUser.markname.equals("")){
									wrapUser.sortKey = PinYinUtil.getPinYin(wrapUser.markname);
								}else if(wrapUser.nickname != null && !wrapUser.nickname.equals("")){
									wrapUser.sortKey = PinYinUtil.getPinYin(wrapUser.nickname);
								}else{
									wrapUser.sortKey = "";
								}
						}
						ContactsComparator cmp = new ContactsComparator();
						Collections.sort(wrapUserList, cmp);
						accountInfo.privateMsgManger.hSubScript.t_friend = friendList.user_time;
						accountInfo.t_friendsList = friendList.user_time;
						RemoteManager.getInstance(MainApplication.getAppInstance()).CallService(accountInfo);
						Intent msgIntent = new Intent(
								LookLookActivity.CONTACTS_REFRESH_DATA);
						msgIntent.putExtra(LookLookActivity.CONTACTS_REFRESH_KEY,
								LookLookActivity.FRIEND_LIST_CHANGE);
						LocalBroadcastManager.getInstance(MainApplication.getAppInstance()).sendBroadcast(
								msgIntent);*/
						accountInfo.privateMsgManger.hSubScript.t_friend = friendList.user_time;
						accountInfo.t_friendsList = friendList.user_time;
						if(isFriendsRefresh){
							RemoteManager.getInstance(MainApplication.getAppInstance()).CallService(accountInfo);
							FriendsSortTask task = new FriendsSortTask();
							task.execute();
						}
					}
				}
			}
			break;
		case Requester3.RESPONSE_TYPE_FRIEND_REQUEST_LIST:
			if (msg.obj != null) {
				friendRequestListResponse friendList = (friendRequestListResponse) msg.obj;

				if ("0".equals(friendList.status)) {
					if (friendList.removeusers != null
							&& !friendList.removeusers.equals("")) {
						String[] removeusers = friendList.removeusers
								.split(",");
						for (int i = 0; i < removeusers.length; i++) {
							accountInfo.friendsRequestList
									.removeMemberByUserid(removeusers[i].trim());
						}
						
					}
					
					WrapUser[] userList = friendList.users;
					for (int i = 0; i < userList.length; i++) {
						accountInfo.friendsRequestList
								.insertMember(i, userList[i]);
					}
					accountInfo.t_friendsRequestList = friendList.user_time;
					Intent msgIntent = new Intent(
							LookLookActivity.CONTACTS_REFRESH_DATA);
					msgIntent.putExtra(LookLookActivity.CONTACTS_REFRESH_KEY,
							LookLookActivity.FRIEND_REQUEST_LIST_CHANGE);
					LocalBroadcastManager.getInstance(MainApplication.getAppInstance()).sendBroadcast(
							msgIntent);
				}
			}
			break;
		case Requester3.RESPONSE_TYPE_FRIEND_NEWS_LIST:
			if (msg.obj != null) {
				friendNewsResponse friendList = (friendNewsResponse) msg.obj;
				if ("0".equals(friendList.status)) {
					accountInfo.friendNewsDataEntities.clearList();
					/*if(friendList.removediarys!=null && !friendList.removediarys.isEmpty()){
						String[] diaryIds = friendList.removediarys.split(",");
						for(int i=0; i< diaryIds.length; i++){
							accountInfo.friendNewsDataEntities.removeMember(diaryIds[i].trim());
						}
					}*/
					
					for (int i = 0; i < friendList.contents.length; i++) {
						accountInfo.friendNewsDataEntities
								.insertMember(i, friendList.contents[i]);
					}
					accountInfo.friendNewsDataEntities.fristTime = friendList.first_diary_time;
					if(accountInfo.friendNewsDataEntities.lastTime.isEmpty()){
						accountInfo.friendNewsDataEntities.lastTime = friendList.last_diary_time;
					}
					accountInfo.t_friendNews = friendList.first_diary_time;
					Intent msgIntent = new Intent(LookLookActivity.CONTACTS_REFRESH_DATA);
					msgIntent.putExtra(LookLookActivity.CONTACTS_REFRESH_KEY, LookLookActivity.FRIEND_NEWS_CHANGE);
					LocalBroadcastManager.getInstance(MainApplication.getAppInstance()).sendBroadcast(
							msgIntent);
				}
			}
			break;
		case Requester3.RESPONSE_TYPE_MYMICLIST:
			try {
				GsonResponse3.myMicListResponse myMicListList = (GsonResponse3.myMicListResponse) msg.obj;
				if (myMicListList != null) {
					// FragmentHelper.getInstance(getActivity()).getZoneBaseFragment().dimissDianVShare();
					if (myMicListList.status.equals("0")) {
						if ("1".equals(myMicListList.is_refresh)) {
							accountInfo.vshareDataEntities.clearList();
						}
						if (myMicListList.showmiclist != null
								&& myMicListList.showmiclist.length > 0) {
							for (int i = 0; i < myMicListList.showmiclist.length; i++) {
								accountInfo.vshareDataEntities.insertMember(i,
										myMicListList.showmiclist[i]);
							}
							// accountInfo.lt_zoneMicList =
							// myMicListList.showMicList[0].comment_time;
							if (accountInfo.vshareDataEntities.getCache()
									.size() > 0) {
								accountInfo.privateMsgManger.hSubScript.t_zone_mic = accountInfo.vshareDataEntities
										.getCache().get(0).update_time;
								accountInfo.privateMsgManger.hSubScript.t_zone_miccomment = accountInfo.vshareDataEntities
										.getCache().get(0).update_time;
								RemoteManager.getInstance(MainApplication.getAppInstance()).CallService(accountInfo);
							}
						}
						Intent msgIntent = new Intent(LookLookActivity.MIC_LIST_CHANGE);
						LocalBroadcastManager.getInstance(MainApplication.getAppInstance()).sendBroadcast(
								msgIntent);
					} else if (myMicListList.status.equals("200600")) {
						/*
						 * Prompt.Dialog(getActivity(), false, "提示",
						 * Constant.CRM_STATUS[Integer
						 * .parseInt(myMicListList.crm_status)], null);
						 */
					} else {
						/*
						 * Prompt.Dialog(getActivity(), false, "提示",
						 * "操作失败，请稍后再试",null);
						 */
					}
				} else {
					/*
					 * Prompt.Dialog(getActivity(), false, "提示", "操作失败，网络不给力",
					 * null);
					 */
				}
			} catch (Exception e) {
				// TODO: handle exception
			}
			break;
		default:
			break;
		}
		return false;
	}
	
	
	// 好友列表排序
    public static class FriendsSortTask extends AsyncTask<Void, Integer, Void> {  
        // 可变长的输入参数，与AsyncTask.exucute()对应  
        @Override  
        protected Void doInBackground(Void... param) {  
            try {  
            	System.out.println("=====refresh friends list======");
            	AccountInfo accountInfo = AccountInfo.getInstance(ActiveAccount.getInstance(MainApplication.getAppInstance()).getUID());
            	List<WrapUser> wrapUserList = accountInfo.friendsListName.getCache();
				for (int i = 0; i < wrapUserList.size(); i++) {
					WrapUser wrapUser = wrapUserList.get(i);
						if(wrapUser.markname != null && !wrapUser.markname.equals("")){
							wrapUser.sortKey = PinYinUtil.getPinYin(wrapUser.markname);
						}else if(wrapUser.nickname != null && !wrapUser.nickname.equals("")){
							wrapUser.sortKey = PinYinUtil.getPinYin(wrapUser.nickname);
						}else if(wrapUser.telname !=null && !wrapUser.telname.isEmpty()){
							wrapUser.sortKey = PinYinUtil.getPinYin(wrapUser.telname);
						}else{
							wrapUser.sortKey = "";
						}
				}
				ContactsComparator cmp = new ContactsComparator();
				Collections.sort(wrapUserList, cmp);
				Intent msgIntent = new Intent(
						LookLookActivity.CONTACTS_REFRESH_DATA);
				msgIntent.putExtra(LookLookActivity.CONTACTS_REFRESH_KEY,
						LookLookActivity.FRIEND_LIST_CHANGE);
				LocalBroadcastManager.getInstance(MainApplication.getAppInstance()).sendBroadcast(
						msgIntent);
            } catch (Exception e) {  
                e.printStackTrace();  
            }  
            
            return null;
        }  
        
        
        @Override  
        protected void onPostExecute(Void result) {  
            // 返回HTML页面的内容   
        }  
        @Override  
        protected void onProgressUpdate(Integer... values) {  
            // 更新进度  
        }  
    }
	
}