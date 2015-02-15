package com.cmmobi.looklook.activity;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.cmmobi.looklook.R;
import com.cmmobi.looklook.common.utils.CmmobiClickAgentWrapper;
import com.cmmobi.looklook.common.utils.UmengclickAgentWrapper;
import com.cmmobi.looklook.fragment.FriendsCircleContactsAttentionFragment;
import com.cmmobi.looklook.fragment.FriendsCircleContactsBlacklistFragment;
import com.cmmobi.looklook.fragment.FriendsCircleContactsFunsFragment;
@Deprecated
public class FriendsCircleContactsActivity extends FragmentActivity implements OnClickListener {

	private ImageView contactsFans;
	private ImageView contactsAttention;
	private ImageView contactsBlacklist;
	private ImageButton backButton;
	
	public static final String FANS = "fans";
	public static final String ATTENTION = "attention";
	public static final String ACTION_TYPE = "ACTION_TYPE";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_friends_circle_contacts);

//		FriendsCircleContactsFunsFragment friendsCircleContactsFunsFragment = new FriendsCircleContactsFunsFragment();
//
//		getSupportFragmentManager().beginTransaction().add(
//				R.id.friends_contacts, friendsCircleContactsFunsFragment).commit();
	
		contactsFans = (ImageView) findViewById(R.id.contacts_fans);
		contactsAttention = (ImageView) findViewById(R.id.contacts_attention);
		contactsBlacklist  = (ImageView) findViewById(R.id.contacts_blacklist);
		
		backButton = (ImageButton) findViewById(R.id.contacts_back_btn);
		
		contactsFans.setOnClickListener(this);
		contactsAttention.setOnClickListener(this);
		contactsBlacklist.setOnClickListener(this);
		backButton.setOnClickListener(this);
		
		if (ATTENTION.equals(getIntent().getStringExtra(ACTION_TYPE))) {
			onCheckChanged(R.id.contacts_attention);
		} else {
			onCheckChanged(R.id.contacts_fans);
			
		}
		
		
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
		if (v.getId() == R.id.contacts_back_btn) {
			this.finish();
		} else {
			onCheckChanged(v.getId());
		}
	}

	private void onCheckChanged(int id) {
		switch (id) {
		case R.id.contacts_fans:
			FriendsCircleContactsFunsFragment friendsCircleContactsFunsFragment = new FriendsCircleContactsFunsFragment();

			getSupportFragmentManager().beginTransaction().replace(
					R.id.friends_contacts, friendsCircleContactsFunsFragment).commit();
			contactsFans.setSelected(true);
			contactsAttention.setSelected(false);
			contactsBlacklist.setSelected(false);
			
			break;
		case R.id.contacts_attention:
			
			FriendsCircleContactsAttentionFragment friendsCircleContactsAttentionFragment = new FriendsCircleContactsAttentionFragment();

			getSupportFragmentManager().beginTransaction().replace(
					R.id.friends_contacts, friendsCircleContactsAttentionFragment).commit();
			
			contactsFans.setSelected(false);
			contactsAttention.setSelected(true);
			contactsBlacklist.setSelected(false);
			break;
		case R.id.contacts_blacklist:
			
			FriendsCircleContactsBlacklistFragment friendsCircleContactsBlacklistFragment = new FriendsCircleContactsBlacklistFragment();

			getSupportFragmentManager().beginTransaction().replace(
					R.id.friends_contacts, friendsCircleContactsBlacklistFragment).commit();
			
			
			contactsFans.setSelected(false);
			contactsAttention.setSelected(false);
			contactsBlacklist.setSelected(true);
			break;

		default:
			break;
		}
	}
}
