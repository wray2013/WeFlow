package com.cmmobi.looklook.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.LocalBroadcastManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageView;

import com.cmmobi.looklook.MainApplication;
import com.cmmobi.looklook.R;
import com.cmmobi.looklook.activity.LookLookActivity;
import com.cmmobi.looklook.info.profile.AccountInfo;
import com.cmmobi.looklook.info.profile.ActiveAccount;
import com.cmmobi.looklook.receiver.CmmobiPushReceiver;

public class FriendsContactsFragment extends TitleRootFragment{
	private static final String TAG = "FriendsContactsFragment";
	private Button btnContacts;
	private Button btnInvite;
	public Fragment currFragment;
	private ImageView iv_jiaobiao_friends;
	private ImageView iv_jiaobiao_invite;
	
	public static final String FRIENDSCONTACTS_TAB_CHANGED = "FRIENDSCONTACTS_TAB_CHANGED";
	public static final int TAG_CONTACTS = R.id.btn_contacts;
	public static final int TAG_INVITE = R.id.btn_invite;
	private int mTab = TAG_CONTACTS;
	
	private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
		  @Override
		  public void onReceive(Context context, Intent intent) {    
			    // Get extra data included in the Intent
				if(LookLookActivity.CONTACTS_REFRESH_DATA.equals(intent.getAction())){		
					updateJiaobiao();
				}else if(FRIENDSCONTACTS_TAB_CHANGED.equals(intent.getAction())){
					int tab = intent.getIntExtra("tab", 0);
					if(tab != 0){
						onCheckChanged(tab);
					}
				}
				}
		  };
		  
    public FriendsContactsFragment(){

    }
				  
	public FriendsContactsFragment(int mTab) {
		super();
		this.mTab = mTab;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View view = super.onCreateView(inflater, container, savedInstanceState);
		setTitle(getResources().getString(R.string.contacts));
		hideLeftButton();
		showRightButton();

		btnContacts = (Button) view.findViewById(R.id.btn_contacts);
		btnInvite = (Button) view.findViewById(R.id.btn_invite);
		btnContacts.setOnClickListener(this);
		btnInvite.setOnClickListener(this);
		
		iv_jiaobiao_friends = (ImageView) view.findViewById(R.id.iv_jiaobao_friends);
		iv_jiaobiao_invite = (ImageView) view.findViewById(R.id.iv_jiaobao_invite);
		
		updateJiaobiao();
		onCheckChanged(mTab);
		IntentFilter filter = new IntentFilter();
		filter.addAction(LookLookActivity.CONTACTS_REFRESH_DATA);
		filter.addAction(FRIENDSCONTACTS_TAB_CHANGED);
		LocalBroadcastManager.getInstance(getActivity()).registerReceiver(mMessageReceiver, filter);
		
		return view;
	}
	
	private void onCheckChanged(int id) {
		FragmentManager fm = getChildFragmentManager();
		FragmentTransaction ft = fm.beginTransaction();
		if(currFragment != null){
			ft.hide(currFragment);
		}
		Fragment fragment = null;
		switch (id) {
		case R.id.btn_contacts:
			fragment = fm.findFragmentByTag(ContactsFragment.class.getName());
			if (fragment == null) {
				fragment = new ContactsFragment();
				ft.add(R.id.empty, fragment, ContactsFragment.class.getName());
			} else {
				ft.show(fragment);
			}
			currFragment = fragment;
			
			btnContacts.setTextColor(Color.WHITE);
			btnInvite.setTextColor(Color.parseColor("#007aff"));
			btnContacts.setBackgroundResource(R.drawable.qiehuan1_2);
			btnInvite.setBackgroundColor(Color.TRANSPARENT);
			accountInfo.newFriend = 0;
			updateJiaobiao();
			Intent msgIntent = new Intent(LookLookActivity.UPDATE_MASK);
			LocalBroadcastManager.getInstance(getActivity()).sendBroadcast(msgIntent);
			break;
		case R.id.btn_invite:

			fragment = fm.findFragmentByTag(FriendAddFragment.class.getName());
			if (fragment == null) {
				fragment = new FriendAddFragment();
				ft.add(R.id.empty, fragment, FriendAddFragment.class.getName());
			} else {
				ft.show(fragment);
			}
			currFragment = fragment;

			btnInvite.setTextColor(Color.WHITE);
			btnContacts.setTextColor(Color.parseColor("#007aff"));
			btnInvite.setBackgroundResource(R.drawable.qiehuan1_3);
			btnContacts.setBackgroundColor(Color.TRANSPARENT);
			break;
		default:
			break;
		}
		hideSystemKeyBoard(btnInvite);
		ft.commitAllowingStateLoss();
	}
	
	public void hideSystemKeyBoard(View v) {
		InputMethodManager imm = (InputMethodManager) getActivity()
				.getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
	}

	public void updateJiaobiao(){
		if(accountInfo.newFriendRequestCount>0){
			iv_jiaobiao_friends.setVisibility(View.VISIBLE);
		}else{
			iv_jiaobiao_friends.setVisibility(View.GONE);
		}
		
		iv_jiaobiao_invite.setVisibility(View.GONE);

	}
	
	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		
		if(!this.isHidden()){
			CmmobiPushReceiver.cancelNotification(this.getActivity(), CmmobiPushReceiver.NOTIFY_INDEX_CONTACTS);
		}
	}

	@Override
	public void onHiddenChanged(boolean hidden) {
		// TODO Auto-generated method stub
		if(!hidden){
			CmmobiPushReceiver.cancelNotification(this.getActivity(), CmmobiPushReceiver.NOTIFY_INDEX_CONTACTS);
		}
		super.onHiddenChanged(hidden);
	}
	
	@Override
	public void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
	}
	
	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(mMessageReceiver);
	}
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_title_right:
			showMenu();
			hideSystemKeyBoard(btnInvite);
			break;
		case R.id.btn_contacts:
		case R.id.btn_invite:
			onCheckChanged(v.getId());
			break;
		
		default:
			break;
		}
	}

	

	@Override
	public int subContentViewId() {
		// TODO Auto-generated method stub
		return R.layout.activity_friends_contacts;
	}

	@Override
	public boolean handleMessage(Message msg) {
		// TODO Auto-generated method stub
		return false;
	}
	
}