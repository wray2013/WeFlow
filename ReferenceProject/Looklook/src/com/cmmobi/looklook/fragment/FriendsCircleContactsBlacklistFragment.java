package com.cmmobi.looklook.fragment;

import java.util.ArrayList;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.Toast;
import cn.zipper.framwork.core.ZApplication;

import com.cmmobi.looklook.R;
import com.cmmobi.looklook.activity.HomeActivity;
import com.cmmobi.looklook.common.adapter.FriendsCircleContactsAdapter;
import com.cmmobi.looklook.common.gson.GsonResponse2.myblacklistUsers;
import com.cmmobi.looklook.common.view.QuickBarView;
import com.cmmobi.looklook.info.profile.AccountInfo;
import com.cmmobi.looklook.info.profile.ActiveAccount;
import com.cmmobi.looklook.info.profile.ContactManager;
import com.cmmobi.looklook.info.profile.FriendsBlackListManager;
import com.cmmobi.sns.utils.PinYinUtil;

public class FriendsCircleContactsBlacklistFragment extends Fragment implements
		OnItemClickListener, Callback {

	private View contentView;

	ListView friendsCircleContactsListView;
	QuickBarView quickBarView;

	private String userID;
	private AccountInfo accountInfo;

	private Handler handler;

	private myblacklistUsers[] userList;
	private ArrayList<WrapUser> wrapUserList = new ArrayList<WrapUser>();

	private FriendsCircleContactsAdapter friendsCircleContactsAdapter;
	private ArrayList<WrapUser> tempWrapUserList = new ArrayList<WrapUser>();;

	private FriendsBlackListManager friendsBlackListManager;
	private boolean isClean = true;

	protected boolean receive = false;
	private ContactManager blacklistContactManager;

	protected int MSG_WHAT;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		LocalBroadcastManager.getInstance(getActivity()).registerReceiver(
				dataChangeReceiver,
				new IntentFilter(HomeActivity.BLACK_LIST_CHANGE));

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		contentView = inflater.inflate(
				R.layout.fragment_friends_circle_contacts_list, null);
		return contentView;
	}

	private void loadData() {
		blacklistContactManager = accountInfo.blackListContactManager;

		wrapUserList = (ArrayList<WrapUser>) blacklistContactManager.getCache()
				.clone();
		for (int i = 0; i < wrapUserList.size(); i++) {

			WrapUser wrapUser = wrapUserList.get(i);
			if (!(null != wrapUser.sortKey && wrapUser.sortKey.length() > 0)) {
				String sortKey = PinYinUtil.getPinYin(null == wrapUser.nickname ? "" : wrapUser.nickname);
				wrapUser.sortKey = sortKey;
			}
			if (receive) {
				break;
			}
		}
		if (!receive) {
			handler.sendEmptyMessage(MSG_WHAT);
		}
	}
	@Override
	public boolean handleMessage(Message msg) {
		if (MSG_WHAT == msg.what) {
			friendsCircleContactsAdapter.setData(wrapUserList);
			friendsCircleContactsAdapter.notifyDataSetChanged();
		}
		return false;
	}
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		userID = ActiveAccount.getInstance(ZApplication.getInstance()).getUID();
		accountInfo = AccountInfo.getInstance(userID);
		friendsBlackListManager = accountInfo.friendsBlackListManager;

		friendsCircleContactsListView = (ListView) contentView
				.findViewById(R.id.friends_circle_contacts_list);

		quickBarView = (QuickBarView) contentView.findViewById(R.id.quick_bar);

		quickBarView.setListView(friendsCircleContactsListView);

		handler = new Handler(this);

		friendsCircleContactsAdapter = new FriendsCircleContactsAdapter(
				getActivity(), FriendsCircleContactsAdapter.TAB_BLACK_LIST);

		friendsCircleContactsListView.setAdapter(friendsCircleContactsAdapter);

//		loadLoacalData();
		
		friendsCircleContactsListView.setOnItemClickListener(this);
	}
	@Override
	public void onResume() {
		loadData();
		super.onResume();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(
				dataChangeReceiver);
	}
//	private void loadLoacalData() {
//		ArrayList<WrapUser> cache = friendsBlackListManager.getCache();
//		if (cache.size() > 0) {
//
//			// diariesList.clear();
//			// partsList.clear();
//			wrapUserList.clear();
//			wrapUserList.addAll(cache);
//			friendsCircleContactsAdapter.setData(wrapUserList);
//			friendsCircleContactsAdapter.notifyDataSetChanged();
//
//		}
//		Requester2.myBlacklist(handler, "",
//				ActiveAccount.getInstance(ZApplication.getInstance())
//						.getLookLookID(),"10");
//	}

	private BroadcastReceiver dataChangeReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {

			receive  = true;

			blacklistContactManager = accountInfo.blackListContactManager;

			wrapUserList = (ArrayList<WrapUser>) blacklistContactManager
					.getCache().clone();

			for (int i = 0; i < wrapUserList.size(); i++) {

				WrapUser wrapUser = wrapUserList.get(i);
				if (!(null != wrapUser.sortKey && wrapUser.sortKey.length() > 0)) {
					String sortKey = PinYinUtil.getPinYin(wrapUser.nickname);
					wrapUser.sortKey = sortKey;
				}
			}
			handler.sendEmptyMessage(MSG_WHAT);
		}
	};
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		Toast.makeText(getActivity(), "position= " + position + ": id = " + id,
				Toast.LENGTH_SHORT).show();
	}

//	@Override
//	public boolean handleMessage(Message msg) {
//
//		switch (msg.what) {
//		case Requester2.RESPONSE_TYPE_MY_BLACK_LIST:
//			if (msg.obj != null) {
//				myblacklistResponse attentionList = (myblacklistResponse) msg.obj;
//
//				if ("0".equals(attentionList.status)) {
//					userList = attentionList.users;
//
//					if (isClean) {
//						tempWrapUserList.clear();
//						isClean = false;
//					}
//
//					for (int i = 0; i < userList.length; i++) {
//
//						WrapUser wrapUser = new WrapUser();
//
//						wrapUser.userid = userList[i].userid;
//						wrapUser.headimageurl = userList[i].headimageurl;
//						wrapUser.nickname = userList[i].nickname;
//						wrapUser.diarycount = userList[i].diarycount;
//						wrapUser.sex = userList[i].sex;
//						wrapUser.signature = userList[i].signature;
//						String sortKey = PinYinUtil
//								.getPinYin(userList[i].nickname);
//						wrapUser.sortKey = sortKey;
//						tempWrapUserList.add(wrapUser);
//					}
//					if ("1".equals(attentionList.hasnextpage)) {
//						Requester2.myBlacklist(handler, attentionList.user_time, ActiveAccount
//								.getInstance(ZApplication.getInstance())
//								.getLookLookID(),"10");
//					} else {
//						if (wrapUserList != null) {
//							wrapUserList.clear();
//							friendsCircleContactsAdapter.notifyDataSetChanged();
//						}
//						wrapUserList.addAll(tempWrapUserList);
//
//						friendsBlackListManager.refreshCache(wrapUserList);
//						friendsCircleContactsAdapter.setData(wrapUserList);
//						friendsCircleContactsAdapter.notifyDataSetChanged();
//					}
//
//				}
//			}
//
//			break;
//
//		default:
//			break;
//		}
//		return false;
//	}

}
