package com.cmmobi.looklook.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import cn.zipper.framwork.core.ZActivity;
import cn.zipper.framwork.core.ZDialog;

import com.cmmobi.looklook.R;
import com.cmmobi.looklook.common.constant.Constant;
import com.cmmobi.looklook.common.gson.GsonResponse2;
import com.cmmobi.looklook.common.gson.GsonResponse2.MyBind;
import com.cmmobi.looklook.common.gson.Requester2;
import com.cmmobi.looklook.common.utils.CmmobiClickAgentWrapper;
import com.cmmobi.looklook.common.utils.UmengclickAgentWrapper;
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
	
	private ImageView iv_onoff;	//打开关闭手势密码
	
	private RelativeLayout rl_resetgesturepwd; //重置手势密码
	private Button btn_cancelsafemode; //关闭保险箱功能
	
	// 手机号(保险箱)绑定
	private RelativeLayout rl_safe;
	private TextView tv_safestate;
	private MyBind safestate = null;
	private MyBind phonebindstate = null;
	
	private ImageView iv_line;
	private AccountInfo accountInfo;
	private LoginSettingManager lsm;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setting_setup_gesture);

		ib_back = (ImageButton) findViewById(R.id.ib_back);
		iv_onoff = (ImageView) findViewById(R.id.iv_onoff);
		btn_cancelsafemode = (Button) findViewById(R.id.btn_cancelsafemode);
		rl_resetgesturepwd = (RelativeLayout) findViewById(R.id.rl_resetgesturepwd);
		
		ib_back.setOnClickListener(this);
		iv_onoff.setOnClickListener(this);
		btn_cancelsafemode.setOnClickListener(this);
		rl_resetgesturepwd.setOnClickListener(this);
		
		// 手机号（保险箱）绑定
		rl_safe = (RelativeLayout) findViewById(R.id.rl_safe);
		rl_safe.setOnClickListener(this);
		tv_safestate = (TextView) findViewById(R.id.tv_safestate);
		
		iv_line = (ImageView) findViewById(R.id.iv_line);
		String uid = ActiveAccount.getInstance(this).getUID();
		accountInfo = AccountInfo.getInstance(uid);
		if(accountInfo != null){
			lsm = accountInfo.setmanager;
			if(lsm.getSafeIsOn()){
				iv_onoff.setBackgroundResource(R.drawable.on);
				rl_resetgesturepwd.setVisibility(View.VISIBLE);
				btn_cancelsafemode.setVisibility(View.VISIBLE);
				iv_line.setVisibility(View.VISIBLE);
			}else{
				iv_onoff.setBackgroundResource(R.drawable.off);
				rl_resetgesturepwd.setVisibility(View.GONE);
				btn_cancelsafemode.setVisibility(View.GONE);
				iv_line.setVisibility(View.GONE);
			}
			
			phonebindstate = lsm.getBinding_type(LoginSettingManager.BINDING_TYPE_PHONE, LoginSettingManager.BINDING_INFO_POINTLESS);
			if(null != phonebindstate && null != phonebindstate.binding_info && null ==lsm.getBinding_type(LoginSettingManager.BINDING_TYPE_PHONE_SEC, LoginSettingManager.BINDING_INFO_POINTLESS)){
				Prompt.Dialog(this, true, "提示", "是否绑定第二个手机号", new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						Intent intent = new Intent(SettingSetupGestureActivity.this, SettingSafeActivity.class);
						intent.putExtra("phonenum", phonebindstate.binding_info);
						startActivity(intent);
						if(!lsm.getIsFromSetting()){
							SettingSetupGestureActivity.this.finish();
						}
					}
				}, new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						if(!lsm.getIsFromSetting()){
							SettingSetupGestureActivity.this.finish();
						}
					}
				});
			}
			
			/*if (lsm.getSafeIsOn()) {
				iv_onoff.setBackgroundResource(R.drawable.on);
				rl_resetgesturepwd.setVisibility(View.VISIBLE);
				btn_cancelsafemode.setVisibility(View.VISIBLE);
			}else{
				iv_onoff.setBackgroundResource(R.drawable.off);
				rl_resetgesturepwd.setVisibility(View.GONE);
				btn_cancelsafemode.setVisibility(View.GONE);
			}*/
		}
			
		
	}
	
	@Override
	public void onResume() {
		super.onResume();
		UmengclickAgentWrapper.onResume(this);
		CmmobiClickAgentWrapper.onResume(this);
		if(lsm != null){
		//手机号（保险箱）绑定
		safestate = lsm.getBinding_type(LoginSettingManager.BINDING_TYPE_PHONE_SEC, LoginSettingManager.BINDING_INFO_POINTLESS);
		if (safestate != null && safestate.binding_info != null) {
			tv_safestate.setText(safestate.binding_info);
		}
		}
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
		switch (v.getId()) {
		case R.id.iv_onoff:
			if (lsm.getSafeIsOn()) {
				iv_onoff.setBackgroundResource(R.drawable.off);
				rl_resetgesturepwd.setVisibility(View.GONE);
				btn_cancelsafemode.setVisibility(View.GONE);
				iv_line.setVisibility(View.GONE);
				lsm.setSafeIsOn(false);
				/*Intent in = new Intent(
						HomepageMyselfDiaryActivity.SAVE_BOX_MODE_CHANGE);
				LocalBroadcastManager.getInstance(this).sendBroadcast(in);*/
			} else {
				iv_onoff.setBackgroundResource(R.drawable.on);
				rl_resetgesturepwd.setVisibility(View.VISIBLE);
				btn_cancelsafemode.setVisibility(View.VISIBLE);
				iv_line.setVisibility(View.VISIBLE);
				lsm.setSafeIsOn(true);
				/*Intent in = new Intent(
						HomepageMyselfDiaryActivity.SAVE_BOX_MODE_CHANGE);
				LocalBroadcastManager.getInstance(this).sendBroadcast(in);*/
			}
			break;
		case R.id.ib_back:
			this.finish();
			break;
		case R.id.btn_cancelsafemode:
			Prompt.Dialog(SettingSetupGestureActivity.this, true, "提示", "保险箱内的内容展示在日记页并取消保险箱功能", new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub
					ZDialog.show(R.layout.progressdialog, false, true, SettingSetupGestureActivity.this);
					Requester2.unSafebox(handler);
				}
			});
			break;
		case R.id.rl_resetgesturepwd:
			Intent in = new Intent(this, SettingGesturePwdActivity.class);
			in.putExtra("count", 1);
			startActivity(in);
			break;
		case R.id.rl_safe:
			if(safestate != null){
				Prompt.Dialog(SettingSetupGestureActivity.this, true, "提示", "取消手机号(保险箱)绑定？", new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						ZDialog.show(R.layout.progressdialog, false, true, SettingSetupGestureActivity.this);
						Requester2.unbind(handler, LoginSettingManager.BINDING_REQUEST_TYPE_PHONE, LoginSettingManager.PHONE_TYPE_SEC, safestate.binding_info, LoginSettingManager.BINDING_INFO_POINTLESS, LoginSettingManager.BINDING_INFO_POINTLESS);
					}
				});
			}else{
				Intent safe = new Intent(this, SettingSafeActivity.class);
				safe.putExtra("phonenum", phonebindstate.binding_info);
				startActivity(safe);
			}
			break;
		}
	}

	@Override
	public boolean handleMessage(Message msg) {
		// TODO Auto-generated method stub
		switch (msg.what) {
		case Requester2.RESPONSE_TYPE_UNSAFEBOX:
			ZDialog.dismiss();
			try {
				GsonResponse2.unSafeboxResponse res = (GsonResponse2.unSafeboxResponse) msg.obj;
				if(res!=null){
					if(res.status.equals("0")){
						lsm.setGesturepassword(null);
						lsm.setSafeIsOn(false);
						//DiaryManager.getInstance().moveSaveboxDiaryToNormal(ActiveAccount.getInstance(this).getUID());
						/*Intent in = new Intent(
								HomepageMyselfDiaryActivity.SAVE_BOX_MODE_CHANGE);
						LocalBroadcastManager.getInstance(this).sendBroadcast(in);*/
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
		case Requester2.RESPONSE_TYPE_UNBIND:
			try {
				ZDialog.dismiss();
				GsonResponse2.unbindResponse gru = (GsonResponse2.unbindResponse) msg.obj;
				if(gru != null && gru.status!=null){
					if(gru.status.equals("0")){			
						if(gru.binding_type.equals(LoginSettingManager.BINDING_REQUEST_TYPE_PHONE)){
							if(gru.phone_type.equals(LoginSettingManager.PHONE_TYPE_SEC)){
								safestate = null;
								tv_safestate.setText(R.string.not_bind);
								lsm.deleteBindingInfo(LoginSettingManager.BINDING_TYPE_PHONE_SEC, LoginSettingManager.BINDING_INFO_POINTLESS);
								Prompt.Dialog(this, false, "提示", "解绑定成功", null);
							}
						}
					}else if(gru.status.equals("200600")){
						Prompt.Dialog(this, false, "提示", Constant.CRM_STATUS[Integer.parseInt(gru.crm_status)], null);
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
