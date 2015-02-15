package com.cmmobi.looklook.fragment;

import java.util.ArrayList;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.Toast;
import cn.zipper.framwork.core.ZApplication;

import com.cmmobi.looklook.R;
import com.cmmobi.looklook.activity.FriendsSessionPrivateMessageActivity;
import com.cmmobi.looklook.common.adapter.FriendsCircleContactsAdapter;
import com.cmmobi.looklook.common.gson.GsonResponse2;
import com.cmmobi.looklook.common.gson.GsonResponse2.myfanslistResponse;
import com.cmmobi.looklook.common.gson.GsonResponse2.myfanslistUsers;
import com.cmmobi.looklook.common.gson.Requester2;
import com.cmmobi.looklook.common.view.QuickBarView;
import com.cmmobi.looklook.info.profile.AccountInfo;
import com.cmmobi.looklook.info.profile.ActiveAccount;
import com.cmmobi.looklook.info.profile.ContactManager;
import com.cmmobi.looklook.info.profile.FriendsFunsManager;
import com.cmmobi.looklook.offlinetask.OfflineTaskManager;
import com.cmmobi.looklook.prompt.Prompt;
import com.cmmobi.sns.utils.PinYinUtil;

public class FriendsCircleContactsFunsFragment extends Fragment implements
		OnItemClickListener, Callback {

	private View contentView;

	ListView friendsCircleContactsListView;
	QuickBarView quickBarView;

	private String userID;
	private AccountInfo accountInfo;

	private Handler handler;

	private myfanslistUsers[] userList;
	private ArrayList<WrapUser> wrapUserList = new ArrayList<WrapUser>();
	ArrayList<WrapUser> tempWrapUserList = new ArrayList<WrapUser>();
	private FriendsCircleContactsAdapter friendsCircleContactsAdapter;

	private FriendsFunsManager friendsFunsManager;

	private boolean isClean = false;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		contentView = inflater.inflate(
				R.layout.fragment_friends_circle_contacts_list, null);
		return contentView;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		userID = ActiveAccount.getInstance(ZApplication.getInstance()).getUID();
		accountInfo = AccountInfo.getInstance(userID);
		friendsFunsManager = accountInfo.friendsFunsManager;

		friendsCircleContactsListView = (ListView) contentView
				.findViewById(R.id.friends_circle_contacts_list);

		quickBarView = (QuickBarView) contentView.findViewById(R.id.quick_bar);

		quickBarView.setListView(friendsCircleContactsListView);

		handler = new Handler(this);
		friendsCircleContactsAdapter = new FriendsCircleContactsAdapter(
				getActivity(), FriendsCircleContactsAdapter.TAB_FUNS);

		friendsCircleContactsListView.setAdapter(friendsCircleContactsAdapter);
		isClean = true;

		loadLoacalData();

		friendsCircleContactsListView.setOnItemClickListener(this);

		this.registerForContextMenu(friendsCircleContactsListView);
	}

	private void loadLoacalData() {
		ArrayList<WrapUser> cache = friendsFunsManager.getCache();
		if (cache.size() > 0) {

			// diariesList.clear();
			// partsList.clear();
			wrapUserList.clear();
			wrapUserList.addAll(cache);
			friendsCircleContactsAdapter.setData(wrapUserList);
			friendsCircleContactsAdapter.notifyDataSetChanged();
		}
		Requester2.requestMyFansList(handler, "", userID);
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		
		Intent intent = new Intent(this.getActivity(), FriendsSessionPrivateMessageActivity.class);
/*		intent.putExtra("userid", wrapUserList.get(position).userid);// 压入数据
		intent.putExtra("nickname", wrapUserList.get(position).nickname);// 压入数据
*/		intent.putExtra("wrapuser", wrapUserList.get(position).toString());// 压入数据
		startActivity(intent);
	}
	@Override
	public boolean onContextItemSelected(MenuItem item) {

		AdapterContextMenuInfo info = (AdapterContextMenuInfo) item
				.getMenuInfo();
		switch (item.getItemId()) {
		case 1:
//			Requester2.cancelAttention(handler, wrapUserList.get(info.position).userid, "2");			
		
			OfflineTaskManager.getInstance().addfansRemoveTask(wrapUserList.get(info.position).userid);
			updateUI(wrapUserList.get(info.position).userid);
			break;
		default:
			break;
		}
		return super.onContextItemSelected(item);
	}
	private void updateUI (String userid) {
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
				friendsFunsManager.refreshCache(wrapUserList);
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
	public boolean handleMessage(Message msg) {
		switch (msg.what) {
		case Requester2.RESPONSE_TYPE_MY_FANS_LIST:
			if (msg.obj != null) {
				myfanslistResponse funsList = (myfanslistResponse) msg.obj;

				if ("0".equals(funsList.status)) {
					userList = funsList.users;
					if (isClean) {
						tempWrapUserList.clear();
						isClean = false;
					}
					for (int i = 0; i < userList.length; i++) {

						WrapFunUser wrapUser = new WrapFunUser();

						wrapUser.userid = userList[i].userid;
						wrapUser.headimageurl = userList[i].headimageurl;
						wrapUser.nickname = userList[i].nickname;
						wrapUser.diarycount = userList[i].diarycount;
						wrapUser.attentioncount = userList[i].attentioncount;
						wrapUser.fanscount = userList[i].fanscount;
						wrapUser.sex = userList[i].sex;
						wrapUser.signature = userList[i].signature;
						wrapUser.isattention = userList[i].isattention;
						String sortKey = PinYinUtil
								.getPinYin(userList[i].nickname);
						wrapUser.sortKey = sortKey;
						if(!tempWrapUserList.contains(wrapUser)){
							tempWrapUserList.add(wrapUser);
						}
						
					}

					if ("1".equals(funsList.hasnextpage)) {
						Requester2.requestMyFansList(handler,
								funsList.user_time, userID);
					} else {
						
						if (wrapUserList != null) {
							wrapUserList.clear();
							friendsCircleContactsAdapter.notifyDataSetChanged();
						}
						wrapUserList.addAll(tempWrapUserList);
						
						friendsFunsManager.refreshCache(wrapUserList);

						friendsCircleContactsAdapter.setData(wrapUserList);
						friendsCircleContactsAdapter.notifyDataSetChanged();
					}

				} else {

				}
			}

			break;

		case Requester2.RESPONSE_TYPE_CANCEL_ATTENTION:
			GsonResponse2.cancelattentionResponse cancelListResponse = (GsonResponse2.cancelattentionResponse) msg.obj;
			if (cancelListResponse != null
					&& "0".equals(cancelListResponse.status)) {

				Prompt.Alert(this.getActivity(), "删除粉丝成功！");
				
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
						friendsFunsManager.refreshCache(wrapUserList);
						friendsCircleContactsAdapter.notifyDataSetChanged();
//						dataChange.dataChaged();
					}
				}
			} else if (cancelListResponse != null
					&& "138110".equals(cancelListResponse.status)) {
				
				Prompt.Alert(this.getActivity(), "对官方用户违规操作！");
			} else {
				Prompt.Alert(this.getActivity(), "删除粉丝失败！");
			}
			break;
		default:
			break;
		}
		return false;

	}
}
