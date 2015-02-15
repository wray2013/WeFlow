package com.cmmobi.looklook.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import cn.zipper.framwork.core.ZActivity;
import cn.zipper.framwork.core.ZDialog;

import com.cmmobi.looklook.R;
import com.cmmobi.looklook.common.constant.Constant;
import com.cmmobi.looklook.common.gson.GsonResponse2;
import com.cmmobi.looklook.common.gson.Requester2;
import com.cmmobi.looklook.common.utils.CmmobiClickAgentWrapper;
import com.cmmobi.looklook.common.utils.UmengclickAgentWrapper;
import com.cmmobi.looklook.info.profile.AccountInfo;
import com.cmmobi.looklook.info.profile.ActiveAccount;
import com.cmmobi.looklook.info.profile.LoginSettingManager;
import com.cmmobi.looklook.prompt.Prompt;
import com.cmmobi.sns.utils.LocusPassWordView;
import com.cmmobi.sns.utils.LocusPassWordView.OnCompleteListener;

/**
 * 手势密码界面,新创建
 * 
 * @author youtian
 * 
 */
public class SettingGesturePwdActivity extends ZActivity implements
		OnCompleteListener {

	private GridView gv_thumb;
	private List<Map<String, Integer>> list;
	private GridViewThumbAdaper thumbAdaper;
	private ImageView iv_item;

	private LocusPassWordView lpwv_pwd;

	private Handler handler;

	private String first_pwd;

	private int count;// 0,验证密码5次；1，重新设置密码；2，再次确认新密码；
	private int num = 5;

	private ImageView iv_back;

	private AccountInfo accountInfo;
	private LoginSettingManager lsm;

	private TextView tv_prompt;
	private TextView tv_forgetpwd;
	
	private int sendType = 0; //找回密码的方式 1.邮箱 2.手机
	
	private Context context;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setting_private_gesture);

		context = this;
		
		list = new ArrayList<Map<String, Integer>>();
		gv_thumb = (GridView) findViewById(R.id.gv_thumb);
		lpwv_pwd = (LocusPassWordView) findViewById(R.id.lpwv_pwd);
		tv_prompt = (TextView) findViewById(R.id.tv_prompt);
		tv_forgetpwd = (TextView) findViewById(R.id.tv_forgetpwd);
		tv_forgetpwd.setOnClickListener(this);
		
		iv_back = (ImageView) findViewById(R.id.iv_back);
		iv_back.setOnClickListener(this);
		count = this.getIntent().getExtras().getInt("count");

		if(count == 0){
			tv_prompt.setText(R.string.create_gesture_pwd_prompt0);
			tv_forgetpwd.setVisibility(View.VISIBLE);
			lpwv_pwd.isChecking = true;
		}else if(count == 1){
			tv_prompt.setText(R.string.create_gesture_pwd_prompt1);
			tv_forgetpwd.setVisibility(View.GONE);
			lpwv_pwd.isChecking = false;
		}
		
		String uid = ActiveAccount.getInstance(this).getUID();
		accountInfo = AccountInfo.getInstance(uid);
		lsm = accountInfo.setmanager;

		for (int i = 0; i < 9; i++) {
			Map<String, Integer> map = new HashMap<String, Integer>();
			map.put("img", R.drawable.fangkuai_1);
			list.add(map);
		}
		thumbAdaper = new GridViewThumbAdaper(this, list);
		gv_thumb.setAdapter(thumbAdaper);
		handler = new Handler(this.getMainLooper());
		lpwv_pwd.setOnCompleteListener(this);
		gv_thumb.setOnItemClickListener(null);
		gv_thumb.setFocusable(false);
		gv_thumb.setClickable(false);
		gv_thumb.setEnabled(false);
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

	public void updateGridViewThumb(final String password) {
		if (password != null) {
			for (int i = 0; i < password.length(); i++) {
				iv_item = (ImageView) (gv_thumb.getChildAt(Integer.parseInt(password.charAt(i)+"")-1)
						.findViewById(R.id.img01));
				iv_item.setBackgroundResource(R.drawable.fangkuai_2);
			}
			handler.postDelayed(new Runnable() {
				public void run() {
					for (int i = 0; i < password.length(); i++) {
						ImageView iv = (ImageView) (gv_thumb.getChildAt(Integer
								.parseInt(password.charAt(i)+"")-1).findViewById(R.id.img01));
						iv.setBackgroundResource(R.drawable.fangkuai_1);
					}
				}

			}, 2000);

		}
	}

	
	class GridViewThumbAdaper extends BaseAdapter {

		private LayoutInflater inflater;
		private Context context;
		private List<Map<String, Integer>> list;

		public GridViewThumbAdaper(Context context, List<Map<String, Integer>> list) {
			this.context = context;
			this.list = list;
			inflater = LayoutInflater.from(context);
		}

		@Override
		public int getCount() {
			return list.size();
		}

		@Override
		public Object getItem(int position) {
			return list.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {

			ViewHolder holder;

			if (convertView == null) {
				convertView = inflater.inflate(
						R.layout.activity_activity_gestrue_item, null);
				holder = new ViewHolder();
				holder.img = (ImageView) convertView.findViewById(R.id.img01);

				holder.img.setBackgroundResource(list.get(position).get("img"));
				convertView.setTag(holder);

			} else {
				holder = (ViewHolder) convertView.getTag();
			}

			return convertView;
		}

		class ViewHolder {
			ImageView img;
		}

	}

	@Override
	public void onComplete(final String password) {
		if (count == 0) {	//原来有密码，确认原密码
			if (password.equals(lsm.getGesturepassword())) {
				handler.postDelayed(new Runnable() {
					public void run() {
						lpwv_pwd.reset();
						lpwv_pwd.postInvalidate();
						tv_prompt.setText(R.string.create_gesture_pwd_prompt1);
					}
				}, 2000);
				updateGridViewThumb(password);
				if(lsm.getIsFromSetting()){
					Intent intent = new Intent(SettingGesturePwdActivity.this, SettingSetupGestureActivity.class);
					SettingGesturePwdActivity.this.startActivity(intent);
				}else{
					lsm.setSafeIsOn(true);
					/*Intent intent = new Intent(
						HomepageMyselfDiaryActivity.SAVE_BOX_MODE_CHANGE);
					LocalBroadcastManager.getInstance(this).sendBroadcast(intent);*/
				}
				SettingGesturePwdActivity.this.finish();
			} else {
				for (int i = 0; i < password.length(); i++) {
					iv_item = (ImageView) (gv_thumb.getChildAt(Integer
							.parseInt(password.charAt(i)+"")-1).findViewById(R.id.img01));
					iv_item.setBackgroundResource(R.drawable.fangkuai_2);

				}
				handler.postDelayed(new Runnable() {
					public void run() {
						for (int i = 0; i < password.length(); i++) {
							iv_item = (ImageView) (gv_thumb.getChildAt(Integer
									.parseInt(password.charAt(i)+"")-1)
									.findViewById(R.id.img01));
							iv_item.setBackgroundResource(R.drawable.fangkuai_1);
							lpwv_pwd.reset();
							lpwv_pwd.postInvalidate();
						}
					}

				}, 2000);
				count = 0;
				num --;
				if(num > 0){
					Prompt.Dialog(this, false, "提示", "密码错误，还可以输入"+ num + "次", null);
				}else{
					ActiveAccount currentaccount = ActiveAccount.getInstance(SettingGesturePwdActivity.this);
					currentaccount.logout();
					currentaccount.isForceLogin = true;
					Intent in = new Intent(SettingGesturePwdActivity.this, LoginMainActivity.class);
					startActivity(in);
					Intent intent = new Intent(HomeActivity.FLAG_CLOSE_ACTIVITY); 
					sendBroadcast(intent);
					finish();		
				}
			}
		} else if (count == 1){ //原来没有密码，直接输入新密码
			first_pwd = password;
			handler.postDelayed(new Runnable() {
				public void run() {
					lpwv_pwd.reset();
					lpwv_pwd.postInvalidate();
					tv_prompt.setText(R.string.create_gesture_pwd_prompt2);
				}
			}, 2000);
			updateGridViewThumb(first_pwd);
			count = 2;
		}else if (count == 2){ //再次输入新密码
			if (password.equals(first_pwd)) {
				handler.postDelayed(new Runnable() {
					public void run() {
						lpwv_pwd.reset();
						lpwv_pwd.postInvalidate();
						// 将正确的密码报出服务器
						ZDialog.show(R.layout.progressdialog, false, true, SettingGesturePwdActivity.this);
						Requester2.addGesturePassword(getHandler(), password);		
					}
				}, 2000);
				updateGridViewThumb(password);	
			} else {
				Prompt.Dialog(context, false, "提示", "两次输入的密码不同，请重新输入", null);
				tv_prompt.setText(R.string.create_gesture_pwd_prompt1);
				for (int i = 0; i < password.length(); i++) {
					iv_item = (ImageView) (gv_thumb.getChildAt(Integer
							.parseInt(password.charAt(i)+"")-1).findViewById(R.id.img01));
					iv_item.setBackgroundResource(R.drawable.fangkuai_2);

				}
				handler.postDelayed(new Runnable() {
					public void run() {
						for (int i = 0; i < password.length(); i++) {
							iv_item = (ImageView) (gv_thumb.getChildAt(Integer
									.parseInt(password.charAt(i)+"")-1)
									.findViewById(R.id.img01));
							iv_item.setBackgroundResource(R.drawable.fangkuai_1);
							lpwv_pwd.reset();
							lpwv_pwd.postInvalidate();
						}
					}

				}, 2000);
				count = 1;
				first_pwd = null;
			}
		}
	}

	@Override
	public boolean handleMessage(Message msg) {
		// TODO Auto-generated method stub
		switch (msg.what) {
		case Requester2.RESPONSE_TYPE_ADD_GESTURE_PASSWORD:
			ZDialog.dismiss();
			try {
				GsonResponse2.addGesturePasswordResponse res = (GsonResponse2.addGesturePasswordResponse) msg.obj;
				if(res!=null && res.status !=null){
					if(res.status.equals("0")){
						lsm.setGesturepassword(first_pwd);
						if(this.getIntent().getExtras().getInt("count") == 1){
							CmmobiClickAgentWrapper.onEvent(this, "ma_cr_suc");
						}
						Prompt.Dialog(SettingGesturePwdActivity.this, false, "提示", "操作成功", new DialogInterface.OnClickListener() {
							
							@Override
							public void onClick(DialogInterface dialog, int which) {
								// TODO Auto-generated method stub
								//if(!lsm.getIsFromSetting()){
									lsm.setSafeIsOn(true);
								//}
								if(lsm.getIsFromSetting() || null ==lsm.getBinding_type(LoginSettingManager.BINDING_TYPE_PHONE_SEC, LoginSettingManager.BINDING_INFO_POINTLESS)){
									Intent intent = new Intent(SettingGesturePwdActivity.this, SettingSetupGestureActivity.class);
									startActivity(intent);
								}							
								SettingGesturePwdActivity.this.finish();
								
							}
						});
					}else if(res.status.equals("200600")){
						Prompt.Dialog(this, false, "提示", Constant.CRM_STATUS[Integer.parseInt(res.crm_status)], new DialogInterface.OnClickListener() {
							
							@Override
							public void onClick(DialogInterface dialog, int which) {
								// TODO Auto-generated method stub
								SettingGesturePwdActivity.this.finish();
							}
						});
					}else{
						Prompt.Dialog(this, false, "提示", "操作失败，请稍后再试", new DialogInterface.OnClickListener() {
							
							@Override
							public void onClick(DialogInterface dialog, int which) {
								// TODO Auto-generated method stub
								SettingGesturePwdActivity.this.finish();
							}
						});
					}
				}else{
					Prompt.Dialog(this, false, "提示", "操作失败，网络不给力", new DialogInterface.OnClickListener() {
						
						@Override
						public void onClick(DialogInterface dialog, int which) {
							// TODO Auto-generated method stub
							SettingGesturePwdActivity.this.finish();
						}
					});
				}
			} catch (Exception e) {
				// TODO: handle exception
			}
			break;
		case Requester2.RESPONSE_TYPE_FORGET_PASSWORD:
			ZDialog.dismiss();
			tv_forgetpwd.setClickable(true);
			try {
				GsonResponse2.forgetPasswordResponse res = (GsonResponse2.forgetPasswordResponse) msg.obj;
				if(res!=null){
					if(res.status.equals("0")){
						Prompt.Dialog(this, false, "提示", "密码已通过短信发送到手机", new DialogInterface.OnClickListener() {
							
							@Override
							public void onClick(DialogInterface dialog, int which) {
								// TODO Auto-generated method stub
								SettingGesturePwdActivity.this.finish();
							}
						});
					}else if(res.status.equals("2")){
						Prompt.Dialog(this, false, "提示", "操作失败，手势密码没有启用", new DialogInterface.OnClickListener() {
							
							@Override
							public void onClick(DialogInterface dialog, int which) {
								// TODO Auto-generated method stub
								SettingGesturePwdActivity.this.finish();
							}
						});
					}else if(res.status.equals("200600")){
						Prompt.Dialog(this, false, "提示", Constant.CRM_STATUS[Integer.parseInt(res.crm_status)], new DialogInterface.OnClickListener() {
							
							@Override
							public void onClick(DialogInterface dialog, int which) {
								// TODO Auto-generated method stub
								SettingGesturePwdActivity.this.finish();
							}
						});
					}else{
						Prompt.Dialog(this, false, "提示", "操作失败，请稍后再试", new DialogInterface.OnClickListener() {
							
							@Override
							public void onClick(DialogInterface dialog, int which) {
								// TODO Auto-generated method stub
								SettingGesturePwdActivity.this.finish();
							}
						});
					}
				}else{
					Prompt.Dialog(this, false, "提示", "操作失败，网络不给力", new DialogInterface.OnClickListener() {
						
						@Override
						public void onClick(DialogInterface dialog, int which) {
							// TODO Auto-generated method stub
							SettingGesturePwdActivity.this.finish();
						}
					});
				}
			} catch (Exception e) {
				// TODO: handle exception
			}
			break;
		}
		return false;
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.iv_back:
			this.finish();
			break;
		case R.id.tv_forgetpwd:
			tv_forgetpwd.setClickable(false);
			ZDialog.show(R.layout.progressdialog, false, true, SettingGesturePwdActivity.this);
			Requester2.requestForgetPassword(getHandler(), null, "2", "2");
			System.out.println("====clicked==== post password");
			break;
		default:
			break;
		}
	}
	
}
