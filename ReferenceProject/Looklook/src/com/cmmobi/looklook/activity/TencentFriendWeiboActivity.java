package com.cmmobi.looklook.activity;

import java.util.ArrayList;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import cn.zipper.framwork.core.ZActivity;
import cn.zipper.framwork.core.ZViewFinder;

import com.cmmobi.looklook.R;
import com.cmmobi.looklook.common.adapter.FriendsTencentAdapter;
import com.cmmobi.looklook.common.gson.WeiboResponse;
import com.cmmobi.looklook.common.utils.CmmobiClickAgentWrapper;
import com.cmmobi.looklook.common.utils.UmengclickAgentWrapper;
import com.cmmobi.looklook.common.view.QuickBarView;
import com.cmmobi.looklook.fragment.WrapSelectedUser;
import com.cmmobi.looklook.fragment.WrapTencentUser;
import com.cmmobi.looklook.info.profile.AccountInfo;
import com.cmmobi.looklook.info.profile.ActiveAccount;
import com.cmmobi.sns.api.WeiboFriendsScanTask;
import com.cmmobi.sns.utils.PinYinUtil;

public class TencentFriendWeiboActivity extends ZActivity implements OnEditorActionListener , OnItemClickListener{
	private ListView lv_weibofriends;
	// private TencentFriendListAdapter list_friend_adapter;
	private ArrayList<WrapTencentUser> list_friend_data;
	private ArrayList<WrapTencentUser> filterUserList = new ArrayList<WrapTencentUser>();
	private Object searchLock = new Object();
	protected SearchTask curSearchTask;
	private ArrayList<String> list_friend_to_invite;
    //private TencentFriendsListView listView;

	// int pageno;
	// boolean allShow;
	private final String TAG = "TencentFriendWeiboActivity";
	public static final int HANDLER_FLAG_DISABLE_INVITE = 0;
	public static final int HANDLER_FLAG_ENABLE_INVITE = 1;
	public static final int HANDLER_FLAG_LISTVIEW_INIT = 2;

	ImageView iv_ok;
	ImageView iv_back;

	String uid;
	AccountInfo uPI;

	private FriendsTencentAdapter friendsTencentAdapter;
	private QuickBarView quickBarView;
	private TextView searchTextView;
	private String searchString;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_friends_weibo_tencent);

		Bundle bundle = getIntent().getExtras();
		String data = bundle.getString("weibo_type");

		ZViewFinder finder = getZViewFinder();

		TextView tv_title = finder
				.findTextView(R.id.tv_activity_friends_weibo_title);
		iv_ok = finder.findImageView(R.id.iv_activity_friends_weibo_ok);
		iv_back = finder.findImageView(R.id.iv_activity_friends_weibo_back);

		searchTextView = finder.findTextView(R.id.search);
		searchTextView.setOnEditorActionListener(this);

		
		if (data.equals("sina")) {
			tv_title.setText("新浪微博好友");
		} else if (data.equals("tencent")) {
			tv_title.setText("腾讯微博好友");
		} else if (data.equals("renren")) {
			tv_title.setText("人人网好友");
		}

		iv_ok.setEnabled(false);
		finder.setOnClickListener(R.id.iv_activity_friends_weibo_ok, this);
		finder.setOnClickListener(R.id.iv_activity_friends_weibo_back, this);


		lv_weibofriends = (ListView) findViewById(R.id.lv_activity_friends_weibo_list_single);
		list_friend_data = new ArrayList<WrapTencentUser>();
		list_friend_to_invite = new ArrayList<String>();
		friendsTencentAdapter = new FriendsTencentAdapter(this, handler, list_friend_to_invite);

		lv_weibofriends.setAdapter(friendsTencentAdapter);
		lv_weibofriends.setOnItemClickListener(this);
		quickBarView = (QuickBarView) findViewById(R.id.quick_bar);

		quickBarView.setListView(lv_weibofriends);

		uid = ActiveAccount.getInstance(this).getUID();
		uPI = AccountInfo.getInstance(uid);
		handler.sendEmptyMessage(HANDLER_FLAG_LISTVIEW_INIT);

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

	@Override
	public boolean handleMessage(Message msg) {
		// TODO Auto-generated method stub
		switch (msg.what) {
		case HANDLER_FLAG_LISTVIEW_INIT:
			list_friend_data.clear();

			if (uPI.tencent_friends != null) {

				for (int i = 0; i < uPI.tencent_friends.size(); i++) {
					WeiboResponse.tencentInfo tencentInfo = uPI.tencent_friends
							.get(i);
					WrapTencentUser wrapTencentUser = new WrapTencentUser();

					wrapTencentUser.headurl = tencentInfo.headurl;
					wrapTencentUser.name = tencentInfo.name;
					wrapTencentUser.nick = tencentInfo.nick;

					String sortKey = PinYinUtil.getPinYin(wrapTencentUser.nick);
					wrapTencentUser.sortKey = sortKey;
					
					list_friend_data.add(wrapTencentUser);
				}

				friendsTencentAdapter.setData(list_friend_data);
				friendsTencentAdapter.notifyDataSetChanged();
			}
			WeiboFriendsScanTask tencentTask = new WeiboFriendsScanTask(this,
					handler);
			tencentTask.execute(uid, "tencent");

			break;
		case WeiboFriendsScanTask.HANDLER_FLAG_WEIBOSCAN_DONE:
            Log.e(TAG, "HANDLER_FLAG_WEIBOSCAN_DONE ");
			if (uPI.tencent_friends != null) {

				list_friend_data.clear();
				for (int i = 0; i < uPI.tencent_friends.size(); i++) {
					WeiboResponse.tencentInfo tencentInfo = uPI.tencent_friends
							.get(i);
					WrapTencentUser wrapTencentUser = new WrapTencentUser();

					wrapTencentUser.headurl = tencentInfo.headurl;
					wrapTencentUser.name = tencentInfo.name;
					wrapTencentUser.nick = tencentInfo.nick;

					String sortKey = PinYinUtil.getPinYin(wrapTencentUser.nick);
					wrapTencentUser.sortKey = sortKey;
					list_friend_data.add(wrapTencentUser);
				}

				friendsTencentAdapter.setData(list_friend_data);
				friendsTencentAdapter.notifyDataSetChanged();


			}
			break;
		case HANDLER_FLAG_DISABLE_INVITE:
			iv_ok.setEnabled(false);
			break;
		case HANDLER_FLAG_ENABLE_INVITE:
			iv_ok.setEnabled(true);
			break;
		}
		return false;
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.iv_activity_friends_weibo_ok:
			// ZToast.showShort("hit ok btn" +
			// list_friend_to_invite.toString());

			Bundle mBundle = new Bundle();
			Intent intent = new Intent();

			intent.setClass(this, FriendAddInviteActivity.class);
			mBundle.putStringArrayList("invite_list", list_friend_to_invite);
			mBundle.putString("weibo_type", "tencent");
			intent.putExtras(mBundle);
			startActivity(intent);

			break;
		case R.id.iv_activity_friends_weibo_back:
			finish();
			break;
		}

	}

	private class SearchTask extends AsyncTask<String, Void, String> {

		boolean inSearchMode = false;

		@Override
		protected String doInBackground(String... params) {
			
			filterUserList.clear();

			String keyword = params[0];

			inSearchMode = (keyword.length() > 0);

			if (inSearchMode) {

				for (WrapTencentUser user : list_friend_data) {
					WrapTencentUser contact = (WrapTencentUser) user;

					if (contact.nick.contains(keyword)) {
						filterUserList.add(user);
					}
				}

			}
			return null;
		}

		protected void onPostExecute(String result) {

			synchronized (searchLock ) {

				if (inSearchMode) {
					friendsTencentAdapter.setData(filterUserList);
					friendsTencentAdapter.notifyDataSetChanged();
				} else {
					friendsTencentAdapter.setData(list_friend_data);
					friendsTencentAdapter.notifyDataSetChanged();
				}
			}

		}
	}

	@Override
	public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
		searchString = searchTextView.getText().toString().trim();

		if (curSearchTask != null
				&& curSearchTask.getStatus() != AsyncTask.Status.FINISHED) {
			try {
				curSearchTask.cancel(true);
			} catch (Exception e) {
			}

		}
		curSearchTask = new SearchTask();
		curSearchTask.execute(searchString);
		return true;
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		// TODO Auto-generated method stub
		friendsTencentAdapter.setItemSelected(view, position);
		friendsTencentAdapter.notifyDataSetChanged();
	}

}
