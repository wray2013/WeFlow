package com.cmmobi.looklook.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager.LayoutParams;
import cn.zipper.framwork.core.ZActivity;
import cn.zipper.framwork.core.ZApplication;

import com.cmmobi.looklook.R;
import com.cmmobi.looklook.activity.ShareDiaryActivity.ShareMessage;
import com.cmmobi.looklook.common.utils.CmmobiClickAgentWrapper;
import com.cmmobi.looklook.fragment.VShareFragment;
import com.cmmobi.looklook.info.profile.AccountInfo;
import com.cmmobi.looklook.info.profile.ActiveAccount;
import com.cmmobi.looklook.info.profile.LoginSettingManager;

public class ShareSelectMenu extends ZActivity
{
	private final String TAG = this.getClass().getSimpleName();

	private String userID = "";
	private AccountInfo accountInfo = null;
	private ActiveAccount acct = null;
	private int mShareType = 0;
	
	private ShareMessage mTmpMessage = null;
	
	private LoginSettingManager lsm;
	private boolean isFromVshare = false;
	
	// for UmengclickAgentWrapper
/*	private long begin = 0;
	private long end_sup_sha_bin = 0; // 绑定结束的时间点
//	private long end_sup_sha = 0; // 绑定离开第三方页面的时间点	
*/
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.shareselect);

		LayoutParams p = getWindow().getAttributes();
		p.width = LayoutParams.MATCH_PARENT; // 设置屏幕宽度
		p.alpha = 0.9f; // 增加一点按钮透明
		getWindow().setAttributes(p); // 设置生效
		getWindow().setGravity(Gravity.BOTTOM);

		findViewById(R.id.btn_share_diary).setOnClickListener(this);
		findViewById(R.id.btn_share_pic).setOnClickListener(this);
		findViewById(R.id.btn_share_audio).setOnClickListener(this);

		findViewById(R.id.btn_cancel_share).setOnClickListener(this);

		userID = ActiveAccount.getInstance(ZApplication.getInstance()).getUID();
		accountInfo = AccountInfo.getInstance(userID);
		acct = ActiveAccount.getInstance(this);
		lsm = accountInfo.setmanager;
		
		String diaryStr = getIntent().getStringExtra(DiaryPreviewActivity.INTENT_ACTION_DIARY_STRING);
		
		String list = getIntent().getStringExtra(DiaryPreviewActivity.INTENT_ACTION_DIARYLIST_STRING);
		
		isFromVshare = getIntent().getBooleanExtra(VShareFragment.IS_FROM_VSHARE, false);
		
	}

	@Override
	public void onClick(View v)
	{
		switch (v.getId())
		{
		case R.id.btn_cancel_share:
			this.finish();
			break;
		case R.id.btn_share_diary:
		{
			// 20140408
			CmmobiClickAgentWrapper.onEvent(this, "sh_group", "" + "3");
			
			Intent intent = new Intent(this, ShareDiarySelectActivity.class);
			intent.putExtra(VShareFragment.IS_FROM_VSHARE, isFromVshare);
			if (!isFromVshare) {
				startActivityForResult(intent, ShareDiaryActivity.REQUEST_CODE_DIARY);
			} else {
				finish();
				startActivity(intent);
			}
			break;
		}
		case R.id.btn_share_pic:
		{
			// 20140408
			CmmobiClickAgentWrapper.onEvent(this, "sh_group", "" + "1");
			
			Intent intent = new Intent();
			intent.setClass(this, MediaScanActivity.class);
			intent.putExtra(VShareFragment.IS_FROM_VSHARE, isFromVshare);
			intent.putExtra(MediaScanActivity.INTENT_SCAN_MODE, MediaScanActivity.MODE_PIC_SHARE);
			if (!isFromVshare) {
				startActivityForResult(intent, ShareDiaryActivity.REQUEST_CODE_DIARY);
			} else {
				finish();
				startActivity(intent);
			}
			break;
		}
		case R.id.btn_share_audio:
		{
			// 20140408
			CmmobiClickAgentWrapper.onEvent(this, "sh_group", "" + "2");
			
			Intent intent = new Intent();
			intent.setClass(this, MediaScanActivity.class);
			intent.putExtra(VShareFragment.IS_FROM_VSHARE, isFromVshare);
			intent.putExtra(MediaScanActivity.INTENT_SCAN_MODE, MediaScanActivity.MODE_AUDIO_SHARE);
			if (!isFromVshare) {
				startActivityForResult(intent, ShareDiaryActivity.REQUEST_CODE_DIARY);
			} else {
				finish();
				startActivity(intent);
			}
			break;
		}
		default:
			break;
		}

	}
	
	@Override
	public boolean handleMessage(Message msg)
	{
		switch(msg.what)
		{
		}
		return false;
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch(requestCode)
		{
		case ShareDiaryActivity.REQUEST_CODE_DIARY:
			if(resultCode == RESULT_OK)
			{
				setResult(RESULT_OK);
				finish();
			}
			else
			{
				finish();
			}
			break;
		}
	}
	
}
