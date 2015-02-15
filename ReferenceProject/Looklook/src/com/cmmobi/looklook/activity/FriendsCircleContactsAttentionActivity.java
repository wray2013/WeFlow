package com.cmmobi.looklook.activity;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.TextView;

import com.cmmobi.looklook.R;
import com.cmmobi.looklook.common.utils.CmmobiClickAgentWrapper;
import com.cmmobi.looklook.common.utils.UmengclickAgentWrapper;
import com.cmmobi.looklook.fragment.FriendsCircleContactsAttentionFragment;

public class FriendsCircleContactsAttentionActivity extends FragmentActivity {

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);

		setContentView(R.layout.empty);

		TextView headTextView = (TextView) findViewById(R.id.head_textview);
		headTextView.setText("关注");
		
		ImageButton backButton = (ImageButton) findViewById(R.id.system_function_back_btn);
		backButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				FriendsCircleContactsAttentionActivity.this.finish();
			}
		});
		FriendsCircleContactsAttentionFragment fragment = new FriendsCircleContactsAttentionFragment();
		getSupportFragmentManager().beginTransaction()
				.replace(R.id.empty, fragment).commit();
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
}
