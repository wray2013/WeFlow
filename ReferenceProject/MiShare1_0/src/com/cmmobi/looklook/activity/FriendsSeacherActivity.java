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
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import cn.zipper.framwork.core.ZDialog;

import com.cmmobi.looklook.R;
import com.cmmobi.looklook.common.adapter.FriendsSeacherAdapter;
import com.cmmobi.looklook.common.gson.GsonResponse3.searchUserResponse;
import com.cmmobi.looklook.common.gson.Requester3;
import com.cmmobi.looklook.common.utils.CmmobiClickAgentWrapper;
import com.cmmobi.looklook.info.profile.ActiveAccount;
import com.cmmobi.looklook.info.profile.WrapUser;

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
	private WrapUser[] users;
	
	public RelativeLayout rl_search;
	
	public RelativeLayout relativeLayout;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_friends_seacher);

		rl_search = (RelativeLayout) findViewById(R.id.rl_search);
		searchEditText = (EditText) findViewById(R.id.et_search);
		searchTextView = (TextView) findViewById(R.id.tv_cancel);
		listView = (ListView) findViewById(R.id.activites_list);
		relativeLayout = (RelativeLayout) findViewById(R.id.relative);

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
		CmmobiClickAgentWrapper.onResume(this);
	}

	@Override
	public void onPause() {
		super.onPause();
		CmmobiClickAgentWrapper.onPause(this);
	}

	@Override
	public void onStop() {
		super.onStop();
		CmmobiClickAgentWrapper.onStop(this);
	}

	@Override
	public void onClick(View v) {

		if (R.id.tv_cancel == v.getId()) {
			hideSystemKeyBoard(searchEditText);
			handler.postDelayed(new Runnable() {
				
				@Override
				public void run() {
					// TODO Auto-generated method stub
					finish();
				}
			}, 100);
			
		}
	}

	@Override
	public void beforeTextChanged(CharSequence s, int start, int count,
			int after) {
	}

	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) {

	}

	@Override
	public void afterTextChanged(Editable s) {

	}

	@Override
	public boolean handleMessage(Message msg) {

		switch (msg.what) {
		case Requester3.RESPONSE_TYPE_SEARCH_USER:
			ZDialog.dismiss();
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
			Requester3.searchUser(handler, v.getText().toString().trim(), "2");
			ZDialog.show(R.layout.progressdialog, false, true, this);
		}

		return false;
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		if (null != users && users.length > 0) {
			WrapUser user = users[position];
			if (null != user && !(user.userid.equals(ActiveAccount.getInstance(this).getUID()))) {
				Intent intent = new Intent(this,
						OtherZoneActivity.class);
				intent.putExtra(OtherZoneActivity.OTHER_ZONE_USERID, user.userid);
				//intent.putExtra("nickname", user.nickname);
				this.startActivity(intent);
			}
		}
	}

	 //点击EditText以外的任何区域隐藏键盘
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {  
            View v = relativeLayout;
            if (isShouldHideInput(v, ev)) {
                if(hideInputMethod(this, v)) {
                    return true; //隐藏键盘时，其他控件不响应点击事件==》注释则不拦截点击事件
                }
            }
        }
        return super.dispatchTouchEvent(ev);   
    }     
    
    public static boolean isShouldHideInput(View v, MotionEvent event) {
        if (v != null/* && (v instanceof EditText)*/) {
            int[] leftTop = { 0, 0 };
            v.getLocationInWindow(leftTop);
            int left = leftTop[0], top = leftTop[1], bottom = top + v.getHeight(), right = left
                    + v.getWidth();
            if (event.getX() > left && event.getX() < right
                    && event.getY() > top && event.getY() < bottom) {
                // 保留点击EditText的事件
                return false;
            } else {
                return true;
            }
        }
        return false;
    }    
    
    public static Boolean hideInputMethod(Context context, View v) {
	InputMethodManager imm = (InputMethodManager) context
		.getSystemService(Context.INPUT_METHOD_SERVICE);
	if (imm != null) {
		return imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
	}
	return false;
    }
}
