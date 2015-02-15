package com.cmmobi.looklook.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

import cn.zipper.framwork.core.ZApplication;

import com.cmmobi.looklook.R;
import com.cmmobi.looklook.common.adapter.FriendsSeacherAdapter;
import com.cmmobi.looklook.common.gson.GsonResponse2.searchUserResponse;
import com.cmmobi.looklook.common.gson.GsonResponse2.searchUsers;
import com.cmmobi.looklook.common.gson.Requester2;
import com.cmmobi.looklook.common.utils.CmmobiClickAgentWrapper;
import com.cmmobi.looklook.common.utils.UmengclickAgentWrapper;
import com.cmmobi.looklook.info.profile.ActiveAccount;

public class FriendsSeacherActivity extends FragmentActivity implements
		OnClickListener, TextWatcher, Callback, OnEditorActionListener,
		OnItemClickListener {

	protected static final String STATUS_HAS = "STATUS_HAS";
	protected static final String STATUS_HASNOT = "STATUS_HASNOT";
	private EditText searchEditText;
	private TextView searchTextView;

	private Handler handler;
	private ListView listView;
	private FriendsSeacherAdapter adapter;
	private searchUsers[] users;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_friends_seacher);

		searchEditText = (EditText) findViewById(R.id.friend_seach_edittext);
		searchTextView = (TextView) findViewById(R.id.search_btn);
		listView = (ListView) findViewById(R.id.activites_list);

		handler = new Handler(this);

		searchTextView.setOnClickListener(this);

		searchEditText.addTextChangedListener(this);
		searchEditText.setOnEditorActionListener(this);

		adapter = new FriendsSeacherAdapter(this);
		listView.setAdapter(adapter);
		listView.setOnItemClickListener(this);
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
	public void onClick(View v) {

		if (R.id.search_btn == v.getId()) {
			// if (STATUS_HAS.equals(v.getTag())) {
			//
			// hideSystemKeyBoard(searchEditText);
			// Requester2.searchUser(handler, searchEditText.getText()
			// .toString(), "1", "100");
			// } else {
			this.finish();
			// }
		}
	}

	@Override
	public void beforeTextChanged(CharSequence s, int start, int count,
			int after) {
	}

	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) {

		// if (count > 0) {
		// searchTextView.setTag(STATUS_HAS);
		// searchTextView.setText("搜索");
		// } else {
		// searchTextView.setTag(STATUS_HASNOT);
		// searchTextView.setText("取消");
		// }

	}

	@Override
	public void afterTextChanged(Editable s) {

	}

	@Override
	public boolean handleMessage(Message msg) {

		switch (msg.what) {
		case Requester2.RESPONSE_TYPE_SEARCH_USER:

			if (msg.obj != null) {
				searchUserResponse response = (searchUserResponse) msg.obj;

				if ("0".equals(response.status)) {
					users = response.users;
					adapter.setData(users);
					adapter.notifyDataSetChanged();
				}
			}
			break;

		default:
			break;
		}

		return false;
	}

	public void hideSystemKeyBoard(View v) {
		InputMethodManager imm = (InputMethodManager) this
				.getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
	}

	@Override
	public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {

		if (null != v.getText() && v.getText().length() > 0) {
			hideSystemKeyBoard(searchEditText);
			Requester2.searchUser(handler, v.getText().toString(), "1", "100");
		}

		return false;
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		if (null != users && users.length > 0) {
			searchUsers user = users[position];
			if (null != user && !(user.userid.equals(ActiveAccount.getInstance(this).getUID()))) {
				Intent intent = new Intent(this,
						HomepageOtherDiaryActivity.class);
				intent.putExtra("userid", user.userid);
				intent.putExtra("nickname", user.nickname);
				this.startActivity(intent);
			}
		}
	}
}
