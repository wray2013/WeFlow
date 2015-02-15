package com.cmmobi.looklook.activity;

import java.util.ArrayList;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageButton;
import android.widget.ListView;
import cn.zipper.framwork.core.ZApplication;

import com.cmmobi.looklook.R;
import com.cmmobi.looklook.common.adapter.FriendsCircleSystemFunctionAdapter;
import com.cmmobi.looklook.common.gson.GsonResponse2;
import com.cmmobi.looklook.common.gson.GsonResponse2.MyUserids;
import com.cmmobi.looklook.common.gson.GsonResponse2.getOfficialUseridsResponse;
import com.cmmobi.looklook.common.gson.Requester2;
import com.cmmobi.looklook.common.utils.CmmobiClickAgentWrapper;
import com.cmmobi.looklook.common.utils.UmengclickAgentWrapper;
import com.cmmobi.looklook.fragment.WrapUser;
import com.cmmobi.looklook.info.profile.AccountInfo;
import com.cmmobi.looklook.info.profile.ActiveAccount;
import com.cmmobi.looklook.info.profile.OfficialUseridsManager;

public class FriendsCircleSystemFunctionActivity extends FragmentActivity
		implements OnClickListener, OnItemClickListener, Callback {

	private ImageButton backButton;
	private ListView listView;

	private Handler handler;
	private FriendsCircleSystemFunctionAdapter friendsCircleSystemFunctionAdapter;
	private MyUserids[] userids;
	private OfficialUseridsManager officialUseridsManager;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_friends_circle_system_function);

		backButton = (ImageButton) findViewById(R.id.system_function_back_btn);

		listView = (ListView) findViewById(R.id.system_function_list);

		backButton.setOnClickListener(this);

		friendsCircleSystemFunctionAdapter = new FriendsCircleSystemFunctionAdapter(
				this);

		handler = new Handler(this);

		listView.setAdapter(friendsCircleSystemFunctionAdapter);
		listView.setOnItemClickListener(this);
		
		String userID = ActiveAccount.getInstance(ZApplication.getInstance()).getUID();
		AccountInfo accountInfo = AccountInfo.getInstance(userID);
		officialUseridsManager = accountInfo.officialUseridsManager;

		
		loadLocalData();
//		Requester2.getOfficialUserids(handler);
	}

	private void loadLocalData() {
		ArrayList<MyUserids> useridList = officialUseridsManager.getCache();
		friendsCircleSystemFunctionAdapter.setData(useridList);
		friendsCircleSystemFunctionAdapter.notifyDataSetChanged();
		
		Requester2.getOfficialUserids(handler);
	}
	@Override
	public boolean handleMessage(Message msg) {
		switch (msg.what) {
		case Requester2.RESPONSE_TYPE_GET_OFFICIAL_USERIDS:
			if (msg.obj != null) {
				getOfficialUseridsResponse officialList = (getOfficialUseridsResponse) msg.obj;
				if ("0".equals(officialList.status)) {
					this.userids = officialList.userids;

					ArrayList<MyUserids> MyUseridsList = new ArrayList<MyUserids>();
					for (int i = 0; i < userids.length; i++) {
						
						MyUserids myUserid = new GsonResponse2().new MyUserids();
						myUserid.userid = userids[i].userid;
						myUserid.nickname = userids[i].nickname;
						myUserid.headimageurl = userids[i].headimageurl;
						myUserid.type = userids[i].type;
						
						MyUseridsList.add(myUserid);
					}
					officialUseridsManager.refreshCache(MyUseridsList);
					friendsCircleSystemFunctionAdapter.setData(MyUseridsList);
					friendsCircleSystemFunctionAdapter.notifyDataSetChanged();
				}
			}
			break;

		default:
			break;
		}
		return false;
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
	public void onStop(){
		super.onStop();
		CmmobiClickAgentWrapper.onStop(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.system_function_back_btn:
			this.finish();
			break;

		default:
			break;
		}
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		
		if (!(null != userids && userids.length > 0)) {
			return;
		}
		if ("活动".equals(userids[position].nickname)) {
			Intent intent = new Intent(this, ActivitiesActivity.class);

			startActivity(intent);
		} else if ("附近".equals(userids[position].nickname)) {
			Intent intent = new Intent(this, FriendsNearByActivity.class);
			startActivity(intent);
		} else if ("推荐".equals(userids[position].nickname)) {
			Intent intent = new Intent(this, FriendsRecommendActivity.class);
			intent.putExtra("label", 2);
			intent.putExtra("type", 2);
			CmmobiClickAgentWrapper.onEvent(this, "recommend", 2);
			startActivity(intent);
		} else {
			Intent intent = new Intent(this,
					FriendsSessionPrivateMessageActivity.class);
			MyUserids userid = userids[position];
			
			WrapUser wrapUser = new WrapUser();
			wrapUser.nickname = userid.nickname;
			wrapUser.userid = userid.userid;
			wrapUser.headimageurl = userid.headimageurl;
			
			intent.putExtra("wrapuser", wrapUser.toString());// 压入数据
			startActivity(intent);
			
		}
	}
}
