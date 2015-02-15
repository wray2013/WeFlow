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
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import cn.zipper.framwork.core.ZApplication;

import com.cmmobi.looklook.R;
import com.cmmobi.looklook.activity.FriendsSessionPrivateMessageActivity;
import com.cmmobi.looklook.activity.HomeActivity;
import com.cmmobi.looklook.common.adapter.FriendsCircleContactsAdapter;
import com.cmmobi.looklook.common.gson.GsonResponse2;
import com.cmmobi.looklook.common.gson.Requester2;
import com.cmmobi.looklook.common.view.QuickBarView;
import com.cmmobi.looklook.info.profile.AccountInfo;
import com.cmmobi.looklook.info.profile.ActiveAccount;
import com.cmmobi.looklook.info.profile.ContactManager;
import com.cmmobi.looklook.offlinetask.OfflineTaskManager;
import com.cmmobi.looklook.prompt.Prompt;
import com.cmmobi.sns.utils.PinYinUtil;

public class FriendsCircleContactsAttentionFragment extends Fragment implements
		OnItemClickListener, Callback, OnLongClickListener {

	public static final int MSG_WHAT = 0;

	private View contentView;

	ListView friendsCircleContactsListView;
	QuickBarView quickBarView;

	private String userID;
	private AccountInfo accountInfo;

	private Handler handler;

	// FriendsAttentionManager friendsAttentionManager;
	private ContactManager attentionContactManager;

	// private myattentionlistUsers[] userList;
	// private LinkedList<WrapUser> wrapUserList = new LinkedList<WrapUser>();
	private ArrayList<WrapUser> wrapUserList = new ArrayList<WrapUser>();

	private FriendsCircleContactsAdapter friendsCircleContactsAdapter;

	private boolean receive = false;

	// private LinkedList<WrapUser> tempWrapUserList = new
	// LinkedList<WrapUser>();
	// private boolean isClean = true;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		LocalBroadcastManager.getInstance(getActivity()).registerReceiver(
				dataChangeReceiver,
				new IntentFilter(HomeActivity.ATTENTION_LIST_CHANGE));
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		contentView = inflater.inflate(
				R.layout.fragment_friends_circle_contacts_list, null);
		return contentView;
	}

	private void loadData() {

		attentionContactManager = accountInfo.attentionContactManager;

		wrapUserList = (ArrayList<WrapUser>) attentionContactManager.getCache()
				.clone();

		for (int i = 0; i < wrapUserList.size(); i++) {

			WrapUser wrapUser = wrapUserList.get(i);
			if (!(null != wrapUser.sortKey && wrapUser.sortKey.length() > 0)) {
				String sortKey = PinYinUtil.getPinYin(wrapUser.nickname);
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
			friendsCircleContactsAdapter.setData(wrapUserList, "系统功能");
			friendsCircleContactsAdapter.notifyDataSetChanged();
		} else if (Requester2.RESPONSE_TYPE_CANCEL_ATTENTION == msg.what) {
			
			GsonResponse2.cancelattentionResponse cancelListResponse = (GsonResponse2.cancelattentionResponse) msg.obj;
			if (cancelListResponse != null
					&& "0".equals(cancelListResponse.status)) {

				Prompt.Alert(this.getActivity(), "取消关注成功！");
				
				for (int i = 0; i < wrapUserList.size(); i++) {
					if (null == cancelListResponse.targer_userid) {
						break;
					}
					if (cancelListResponse.targer_userid.equals(wrapUserList.get(i).userid)) {
						
						userID = ActiveAccount.getInstance(ZApplication.getInstance()).getUID();
						accountInfo = AccountInfo.getInstance(userID);
						ContactManager attentionContactManager = accountInfo.attentionContactManager;
						
						attentionContactManager.removeMember(wrapUserList.get(i).userid);
						wrapUserList.remove(i);
//						friendsBlackListManager = accountInfo.friendsBlackListManager;
//						friendsBlackListManager.refreshCache(wrapUsers);
						friendsCircleContactsAdapter.notifyDataSetChanged();
//						dataChange.dataChaged();
					}
				}
			} else if (cancelListResponse != null
					&& "138110".equals(cancelListResponse.status)) {
				Prompt.Alert(this.getActivity(), "官方账户不能取消！");
			} else {
				
				Prompt.Alert(this.getActivity(), "取消关注失败！");
			}
			 
		}
		return false;
	}

	// private void loadLoacalData() {
	// LinkedList<WrapUser> cache = friendsAttentionManager.getCache();
	// if (cache.size() > 0) {
	//
	// // diariesList.clear();
	// // partsList.clear();
	// wrapUserList.clear();
	// wrapUserList.addAll(cache);
	// friendsCircleContactsAdapter.setData(wrapUserList, "系统功能");
	// friendsCircleContactsAdapter.notifyDataSetChanged();
	//
	// }
	// Requester2.requestAttentionList(handler, "",
	// ActiveAccount.getInstance(ZApplication.getInstance())
	// .getLookLookID(), "10");
	// }

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		userID = ActiveAccount.getInstance(ZApplication.getInstance()).getUID();
		accountInfo = AccountInfo.getInstance(userID);
		// friendsAttentionManager = accountInfo.friendsAttentionManager;

		friendsCircleContactsListView = (ListView) contentView
				.findViewById(R.id.friends_circle_contacts_list);

		quickBarView = (QuickBarView) contentView.findViewById(R.id.quick_bar);

		quickBarView.setListView(friendsCircleContactsListView);

		handler = new Handler(this);
		friendsCircleContactsAdapter = new FriendsCircleContactsAdapter(
				getActivity(), FriendsCircleContactsAdapter.TAB_ATTENTION);

		friendsCircleContactsListView.setAdapter(friendsCircleContactsAdapter);
		// loadLoacalData();

		friendsCircleContactsListView.setOnItemClickListener(this);
		
//		friendsCircleContactsListView.setOnLongClickListener(this);
		
		this.registerForContextMenu(friendsCircleContactsListView);
		
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {

		AdapterContextMenuInfo info = (AdapterContextMenuInfo) item
				.getMenuInfo();
		switch (item.getItemId()) {
		case 1:
//			Requester2.cancelAttention(handler, wrapUserList.get(info.position).userid, "1");
			OfflineTaskManager.getInstance().addAttendedRemoveTask(wrapUserList.get(info.position).userid);
			updateUI(wrapUserList.get(info.position).userid);

			break;
		default:
			break;
		}
		return super.onContextItemSelected(item);
	}
	
	private void updateUI(String userid) {
		for (int i = 0; i < wrapUserList.size(); i++) {
			if (null == userid) {
				break;
			}
			if (userid.equals(wrapUserList.get(i).userid)) {
				
				userID = ActiveAccount.getInstance(ZApplication.getInstance()).getUID();
				accountInfo = AccountInfo.getInstance(userID);
				ContactManager attentionContactManager = accountInfo.attentionContactManager;
				
				attentionContactManager.removeMember(wrapUserList.get(i).userid);
				wrapUserList.remove(i);
//				friendsBlackListManager = accountInfo.friendsBlackListManager;
//				friendsBlackListManager.refreshCache(wrapUsers);
				friendsCircleContactsAdapter.notifyDataSetChanged();
//				dataChange.dataChaged();
			}
		}
	}
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {

		AdapterView.AdapterContextMenuInfo info;
        try {
            info = (AdapterView.AdapterContextMenuInfo) menuInfo;     
            if(! accountInfo.officialUseridsManager.ContainUser(wrapUserList.get(info.position).userid)){			
         		menu.add(Menu.NONE, 1, 1, "删除");
         	}
        } catch (ClassCastException e) {
            return;
        }
		super.onCreateContextMenu(menu, v, menuInfo);
	}
	@Override
	public boolean onLongClick(View v) {
		
		return false;
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

	private BroadcastReceiver dataChangeReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {

			receive = true;

			attentionContactManager = accountInfo.attentionContactManager;

			wrapUserList = (ArrayList<WrapUser>) attentionContactManager
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

//		if (0 == position) {
//			Intent intent = new Intent(this.getActivity(),
//					FriendsCircleSystemFunctionActivity.class);
//
//			startActivity(intent);
//		} else {
			Intent intent = new Intent(this.getActivity(),
					FriendsSessionPrivateMessageActivity.class);
			intent.putExtra("wrapuser", wrapUserList.get(position).toString());// 压入数据
			startActivity(intent);
//		}
	}

	// @Override
	// public boolean handleMessage(Message msg) {
	//
	// switch (msg.what) {
	// case Requester2.RESPONSE_TYPE_MY_ATTENTIONLIST:
	// if (msg.obj != null) {
	// myattentionlistResponse attentionList = (myattentionlistResponse)
	// msg.obj;
	//
	// if ("0".equals(attentionList.status)) {
	// userList = attentionList.users;
	// if (isClean) {
	// tempWrapUserList.clear();
	// isClean = false;
	// }
	// for (int i = 0; i < userList.length; i++) {
	//
	// WrapUser wrapUser = new WrapUser();
	//
	// wrapUser.userid = userList[i].userid;
	// wrapUser.headimageurl = userList[i].headimageurl;
	// wrapUser.nickname = userList[i].nickname;
	// wrapUser.diarycount = userList[i].diarycount;
	// wrapUser.attentioncount = userList[i].attentioncount;
	// wrapUser.fanscount = userList[i].fanscount;
	// wrapUser.sex = userList[i].sex;
	// wrapUser.signature = userList[i].signature;
	// String sortKey = PinYinUtil
	// .getPinYin(userList[i].nickname);
	// wrapUser.sortKey = sortKey;
	// tempWrapUserList.add(wrapUser);
	// }
	//
	// if ("1".equals(attentionList.hasnextpage)) {
	// Requester2.requestAttentionList(
	// handler,
	// attentionList.user_time,
	// ActiveAccount.getInstance(
	// ZApplication.getInstance())
	// .getLookLookID(), "10");
	// } else {
	//
	// if (wrapUserList != null) {
	// wrapUserList.clear();
	// friendsCircleContactsAdapter.notifyDataSetChanged();
	// }
	// wrapUserList.addAll(tempWrapUserList);
	//
	// friendsAttentionManager.refreshCache(wrapUserList);
	// friendsCircleContactsAdapter.setData(wrapUserList,
	// "系统功能");
	// friendsCircleContactsAdapter.notifyDataSetChanged();
	// }
	//
	// }
	// }
	//
	// break;
	//
	// default:
	// break;
	// }
	// return false;
	// }
}