package com.cmmobi.looklook.activity;

import java.io.File;
import java.io.FileOutputStream;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.LocalBroadcastManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.TextView;
import cn.zipper.framwork.core.ZApplication;

import com.cmmobi.looklook.R;
import com.cmmobi.looklook.common.service.PrivateMessageService;
import com.cmmobi.looklook.common.utils.CmmobiClickAgentWrapper;
import com.cmmobi.looklook.common.utils.UmengclickAgentWrapper;
import com.cmmobi.looklook.fragment.FriendsCircleFragment;
import com.cmmobi.looklook.fragment.FriendsContactsFragment;
import com.cmmobi.looklook.fragment.FriendsMessageFragment;
import com.cmmobi.looklook.info.profile.AccountInfo;
import com.cmmobi.looklook.info.profile.ActiveAccount;

public class FriendsActivity extends FragmentActivity implements Callback,
		 OnClickListener  {
/*	public final static int HANDLER_FLAG_PRIMSG_STRANGER = 0x36196971;
	public final static int HANDLER_FLAG_PRIMSG_USER =     0x36196972;*/
	
	public Handler handler;
//	private ImageButton radioGroup;
	private ImageButton circleRadio;
	private ImageButton messageRadio;
	private ImageButton contactsRadio;

	private LayoutInflater inflater;
//	private ImageView friendsContactsButton;
//	private ImageView friendsFindButton;
	private FriendsCircleFragment friendsCircleFragment;
	private FriendsMessageFragment friendsMessageFragment;
	private FriendsContactsFragment friendsContactsFragment;
	
	private TextView messageCountTextView;
	private TextView contactsCountTextView;
	private String userID;
	private AccountInfo accountInfo;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_friends);

		// FriendsCircleFragment friendsCircleFragment = new
		// FriendsCircleFragment();
		// getSupportFragmentManager().beginTransaction()
		// .add(R.id.friend_main_ll, friendsCircleFragment).commit();

		handler = new Handler(this);
		
		userID = ActiveAccount.getInstance(ZApplication.getInstance()).getUID();
		accountInfo = AccountInfo.getInstance(userID);
		
		LocalBroadcastManager.getInstance(this).registerReceiver(myReceiver,
			      new IntentFilter(PrivateMessageService.FLAG_MESSAGE_DATA_UPDATE));
		
		
//		radioGroup = (ImageButton) findViewById(R.id.activity_friends_rg);

		circleRadio = (ImageButton) findViewById(R.id.radio_circle);
		messageRadio = (ImageButton) findViewById(R.id.radio_message);
		contactsRadio = (ImageButton) findViewById(R.id.radio_contacts);
		
		messageCountTextView = (TextView) findViewById(R.id.message_count); 
		contactsCountTextView = (TextView)findViewById(R.id.contacts_count);
		
		circleRadio.setOnClickListener(this);
		messageRadio.setOnClickListener(this);
		contactsRadio.setOnClickListener(this);

//		friendsContactsButton = (ImageView) findViewById(R.id.friends_contacts_btn);
//		friendsFindButton = (ImageView) findViewById(R.id.friends_find_btn);

//		radioGroup.setOnCheckedChangeListener(this);
//		friendsContactsButton.setOnClickListener(this);
//		friendsFindButton.setOnClickListener(this);

//		radioGroup.check(R.id.radio_circle);
		
		
		if (null != accountInfo && null != accountInfo.privateMsgManger) {
			setMessageCount(accountInfo.privateMsgManger.getUnReadNum());
			if(accountInfo.privateMsgManger.getUnReadNum() > 0){
				onCheckedChanged(R.id.radio_message);
			}else{
				onCheckedChanged(R.id.radio_circle);
			}
		}else {
			onCheckedChanged(R.id.radio_circle);
		}
		
	}

	/**
	 * 设置消息数目气泡
	 * @param count
	 */
	public void setMessageCount(int count) {
		
		if (count > 0) {
			messageCountTextView.setVisibility(View.VISIBLE);
			messageCountTextView.setText(count + "");
		} else {
			messageCountTextView.setVisibility(View.GONE);
		}
	}	
	
	/**
	 * 设置通讯录数目气泡
	 * @param count
	 */
	private void setContactsCount(int count) {
		
		if (count > 0) {
			contactsCountTextView.setVisibility(View.VISIBLE);
			contactsCountTextView.setText(count + "");
		} else {
			contactsCountTextView.setVisibility(View.GONE);
		}
	}
	public void onCheckedChanged(int checkedId) {

		switch (checkedId) {
		case R.id.radio_circle:
			
			circleRadio.setSelected(true);
			messageRadio.setSelected(false);
			contactsRadio.setSelected(false);
			
			friendsCircleFragment = new FriendsCircleFragment();

			getSupportFragmentManager().beginTransaction()
					.replace(R.id.friend_main_ll, friendsCircleFragment)
					.commitAllowingStateLoss();

			break;
		case R.id.radio_message:

			circleRadio.setSelected(false);
			messageRadio.setSelected(true);
			contactsRadio.setSelected(false);
			
			friendsMessageFragment = new FriendsMessageFragment();
			getSupportFragmentManager().beginTransaction()
					.replace(R.id.friend_main_ll, friendsMessageFragment)
					.commitAllowingStateLoss();
			
/*			if (null != accountInfo && null != accountInfo.privateMsgManger) {
				setMessageCount(accountInfo.privateMsgManger.getUnReadNum());
			}*/
			break;
		case R.id.radio_contacts:

			circleRadio.setSelected(false);
			messageRadio.setSelected(false);
			contactsRadio.setSelected(true);
			
			friendsContactsFragment = new FriendsContactsFragment();
			getSupportFragmentManager().beginTransaction()
					.replace(R.id.friend_main_ll, friendsContactsFragment)
					.commitAllowingStateLoss();

			break;
		default:
			break;
		}
	}

//	@Override
//	public void onClick(View v) {
//		Intent intent;
//		switch (v.getId()) {
//		case R.id.friends_contacts_btn:
//			intent = new Intent(this, FriendsCircleContactsActivity.class);
//
//			startActivity(intent);
//			break;
//		case R.id.friends_find_btn:
//			intent = new Intent(this, FriendAddActivity.class);
//
//			startActivity(intent);
//			break;
//
//		default:
//			break;
//		}

//	}
	
	@Override
	public void onResume() {
		super.onResume();
		UmengclickAgentWrapper.onResume(this);
		CmmobiClickAgentWrapper.onResume(this);
		Intent msgIntent = new Intent(PrivateMessageService.FLAG_MESSAGE_DATA_UPDATE);
		LocalBroadcastManager.getInstance(this).sendBroadcast(msgIntent);					
		if (null != accountInfo && null != accountInfo.privateMsgManger) {
			setMessageCount(accountInfo.privateMsgManger.getUnReadNum());
		}
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

	public static void saveLog(String string) {
		File logFile = new File(Environment.getExternalStorageDirectory()
				+ "/log1.txt");

		FileOutputStream outStream;
		try {
			outStream = new FileOutputStream(logFile);
			outStream.write(string.getBytes());
			outStream.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public boolean handleMessage(Message msg) {
		// TODO Auto-generated method stub
		switch(msg.what){
/*		case HANDLER_FLAG_PRIMSG_STRANGER:
			startActivity(new Intent(this, FriendsStrangerActivity.class));
			break;
		case HANDLER_FLAG_PRIMSG_USER:
			if (msg.obj != null) {
				startActivity((new Intent(this,
						FriendsSessionPrivateMessageActivity.class)).putExtra(
						"userid", (String) (msg.obj)));
			}
			
			break;*/
		}
		return false;
	}

	private BroadcastReceiver myReceiver=new BroadcastReceiver() {
		
		@Override
		public void onReceive(Context context, Intent intent) {
			
			if (PrivateMessageService.FLAG_MESSAGE_DATA_UPDATE.equals(intent.getAction())) {
				if (null != accountInfo && null != accountInfo.privateMsgManger) {
					setMessageCount(accountInfo.privateMsgManger.getUnReadNum());
				}
			}
		}
	};
	@Override
	public void onClick(View v) {
		
		switch (v.getId()) {
		case R.id.radio_circle:

			onCheckedChanged(R.id.radio_circle);
			break;
		case R.id.radio_message:

			onCheckedChanged(R.id.radio_message);
			break;
		case R.id.radio_contacts:

			onCheckedChanged(R.id.radio_contacts);
			break;
		default:
			break;
		}
	}
}
