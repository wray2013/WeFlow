package com.cmmobi.looklook.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cmmobi.looklook.R;
import com.cmmobi.looklook.common.utils.CmmobiClickAgentWrapper;
import com.cmmobi.looklook.fragment.OtherZoneVShareFragment;

public class OtherZoneVshareActivity extends FragmentActivity {

	public RelativeLayout rl_contacts;
	
	TextView tv_igore;	
	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);

		setContentView(R.layout.contacts_empty);

		TextView headTextView = (TextView) findViewById(R.id.head_textview);
		headTextView.setText(getResources().getString(R.string.other_zone_vshare));		
		ImageButton backButton = (ImageButton) findViewById(R.id.ib_back);
		backButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				OtherZoneVshareActivity.this.finish();
			}
		});
		
		backButton.setOnLongClickListener(new View.OnLongClickListener() {
			
			@Override
			public boolean onLongClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(OtherZoneVshareActivity.this,LookLookActivity.class);
				intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(intent);
				OtherZoneVshareActivity.this.finish();
				return false;
			}
		});
		rl_contacts = (RelativeLayout) findViewById(R.id.rl_contacts);
		OtherZoneVShareFragment fragment = new OtherZoneVShareFragment();
		getSupportFragmentManager().beginTransaction()
				.replace(R.id.empty, fragment).commit();
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
	public void onStop(){
		super.onStop();
		CmmobiClickAgentWrapper.onStop(this);
	}
}
