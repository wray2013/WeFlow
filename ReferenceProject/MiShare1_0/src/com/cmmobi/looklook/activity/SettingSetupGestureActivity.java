package com.cmmobi.looklook.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.support.v4.content.LocalBroadcastManager;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import cn.zipper.framwork.core.ZActivity;
import cn.zipper.framwork.core.ZDialog;

import com.cmmobi.looklook.R;
import com.cmmobi.looklook.common.constant.Constant;
import com.cmmobi.looklook.common.gson.GsonResponse3;
import com.cmmobi.looklook.common.gson.GsonResponse3.MyBind;
import com.cmmobi.looklook.common.gson.Requester3;
import com.cmmobi.looklook.common.utils.CmmobiClickAgentWrapper;
import com.cmmobi.looklook.fragment.SafeboxVShareFragment;
import com.cmmobi.looklook.info.profile.AccountInfo;
import com.cmmobi.looklook.info.profile.ActiveAccount;
import com.cmmobi.looklook.info.profile.DiaryManager;
import com.cmmobi.looklook.info.profile.LoginSettingManager;
import com.cmmobi.looklook.prompt.Prompt;

/**
 * 开启和重置手势密码
 * 
 * @author youtian
 * 
 */
public class SettingSetupGestureActivity extends ZActivity {

	private ImageButton ib_back; //回退
	
	
	private TextView tv_reset; //重置手势密码
	private Button btn_cancelsafemode; //注销保险箱功能
	
	private MyBind safestate = null;
	private MyBind phonebindstate = null;
	
	private AccountInfo accountInfo;
	private LoginSettingManager lsm;
	private boolean isCreatedResume = false;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setting_setup_gesture);

		ib_back = (ImageButton) findViewById(R.id.ib_back);
		btn_cancelsafemode = (Button) findViewById(R.id.btn_cancelsafemode);
		tv_reset = (TextView) findViewById(R.id.tv_reset);
		
		ib_back.setOnClickListener(this);
		ib_back.setOnLongClickListener(new View.OnLongClickListener() {
			
			@Override
			public boolean onLongClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(SettingSetupGestureActivity.this,LookLookActivity.class);
				intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(intent);
				SettingSetupGestureActivity.this.finish();
				return false;
			}
		});
		btn_cancelsafemode.setOnClickListener(this);
		tv_reset.setOnClickListener(this);
		
		String uid = ActiveAccount.getInstance(this).getUID();
		accountInfo = AccountInfo.getInstance(uid);
		if(accountInfo != null){
			lsm = accountInfo.setmanager;
			phonebindstate = lsm.getBinding_type(LoginSettingManager.BINDING_TYPE_PHONE, LoginSettingManager.BINDING_INFO_POINTLESS);
		}
		isCreatedResume = false;
	}
	
	@Override
	public void onResume() {
		super.onResume();
		CmmobiClickAgentWrapper.onResume(this);
		if (isCreatedResume) {
//			this.finish();
			Intent safebox = new Intent(this, SettingGesturePwdActivity.class);
			safebox.putExtra("count", 0);
			startActivity(safebox);
			this.finish();
		}
		isCreatedResume = true;
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
		switch (v.getId()) {
		case R.id.ib_back:
			this.finish();
			break;
		case R.id.btn_cancelsafemode:
			Prompt.Dialog(SettingSetupGestureActivity.this, true, "提示", "保险箱内的内容展示在日记页并取消保险箱功能", new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub
					ZDialog.show(R.layout.progressdialog, false, true, SettingSetupGestureActivity.this);
					Requester3.unSafebox(handler);
				}
			});
			break;
		case R.id.tv_reset:
			Intent in = new Intent(this, SettingGesturePwdActivity.class);
			in.putExtra("count", 1);
			in.putExtra("from_setup", "from_setup");
			startActivity(in);
			break;
		}
	}

	@Override
	public boolean handleMessage(Message msg) {
		// TODO Auto-generated method stub
		switch (msg.what) {
		case Requester3.RESPONSE_TYPE_UNSAFEBOX:
			ZDialog.dismiss();
			try {
				GsonResponse3.unSafeboxResponse res = (GsonResponse3.unSafeboxResponse) msg.obj;
				if(res!=null){
					if(res.status.equals("0")){
						lsm.setGesturepassword(null);
						DiaryManager.getInstance().removeAllSafebox();
						LocalBroadcastManager.getInstance(this).sendBroadcast(new Intent(SafeboxVShareFragment.ACTION_VSHARE_CHANGED));
						Prompt.Dialog(this, false, "提示", "操作成功", new DialogInterface.OnClickListener() {
							
							@Override
							public void onClick(DialogInterface dialog, int which) {
								// TODO Auto-generated method stub
								SettingSetupGestureActivity.this.finish();
							}
						});
					}else if(res.status.equals("200600")){
						Prompt.Dialog(this, false, "提示", Constant.CRM_STATUS[Integer.parseInt(res.crm_status)], null);
					}else{
						Prompt.Dialog(this, false, "提示", "操作失败，请稍后再试", null);
					}
				}else{
					Prompt.Dialog(this, false, "提示", "操作失败，网络不给力", null);
				}
			} catch (Exception e) {
				// TODO: handle exception
			}
			break;
		}
		return false;
	}
}
