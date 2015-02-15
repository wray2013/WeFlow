package com.cmmobi.looklook.dialog;


import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Message;
import android.view.View;
import android.widget.EditText;
import cn.zipper.framwork.core.ZActivity;
import cn.zipper.framwork.core.ZDialog;
import cn.zipper.framwork.core.ZViewFinder;

import com.cmmobi.looklook.R;
import com.cmmobi.looklook.common.constant.Constant;
import com.cmmobi.looklook.common.gson.GsonResponse3;
import com.cmmobi.looklook.common.gson.Requester3;
import com.cmmobi.looklook.prompt.Prompt;

public class LoginPasswordDialog extends ZActivity {
	private static final String  TAG = "LoginPasswordDialog";
	protected static final int HANDLER_FLAG_EXIT_ACTIVITY = 0x73971391;
	EditText content;
	String username;
	private int send_type = 0;
	
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_login_password);
        
		ZViewFinder finder = getZViewFinder();
		finder.setOnClickListener(R.id.btn_login_looklook_password_back, this);
		finder.setOnClickListener(R.id.btn_login_looklook_password_ok, this);
		finder.setOnClickListener(R.id.btn_login_looklook_clean_edit, this);
		finder.setOnClickListener(R.id.rly_dialog_lp, this);
		finder.setOnClickListener(R.id.rly_dialog_container, this);
		content = finder.findEditText(R.id.et_login_looklook_password_content);
		
	}

	@Override
	public boolean handleMessage(Message msg) {
		// TODO Auto-generated method stub
		switch(msg.what){
		case HANDLER_FLAG_EXIT_ACTIVITY:
			finish();
			break;
		case Requester3.RESPONSE_TYPE_FORGET_PASSWORD:
			try {
				ZDialog.dismiss();
				GsonResponse3.forgetPasswordResponse obj = (GsonResponse3.forgetPasswordResponse) (msg.obj);
			    if(obj!=null){
			    	if(obj.status.equals("0")){
			    		//Log.i(TAG, "处理成功");
			    		if(send_type==1){
				    		//Prompt.Alert(this, "密码已发送到邮箱");
/*			    			Prompt.Dialog(LoginPasswordDialog.this, false, "提示", "密码已发送到邮箱" , null);*/
			    			Prompt.Dialog(LoginPasswordDialog.this, true, "提示", "密码已发送到邮箱,是否要打开相应的网站查看", new OnClickListener() {
								
								@Override
								public void onClick(DialogInterface dialog, int which) {
									// TODO Auto-generated method stub
									try{
										String url = username;
										String a[] = url.split("@"); 
										if(a!=null && a.length==2){
											String mail_url = Constant.MAIL_MAP.get(a[1]);
											if(mail_url==null){
												mail_url = "http://www.baidu.com/";
											}
											Uri uri = Uri.parse (mail_url);  
											Intent intent = new Intent (Intent.ACTION_VIEW, uri);    
											LoginPasswordDialog.this.startActivity(intent);  
										}
									}catch(android.content.ActivityNotFoundException e){
										e.printStackTrace();
										Prompt.Alert(LoginPasswordDialog.this, "没有合适的外部浏览器打开该邮箱");
									}
									
									//finish();
									handler.sendEmptyMessageDelayed(HANDLER_FLAG_EXIT_ACTIVITY, 500);


								}
							}, new OnClickListener() {
								
								@Override
								public void onClick(DialogInterface dialog, int which) {
									// TODO Auto-generated method stub
									//finish();
									handler.sendEmptyMessageDelayed(HANDLER_FLAG_EXIT_ACTIVITY, 500);
								}
							});
			    		}else{
			    			//Prompt.Alert(this, "密码已通过短信发送到该手机");
			    			Prompt.Dialog(LoginPasswordDialog.this, false, "提示", "密码已通过短信发送到该手机，请注意查收" , new OnClickListener() {
								
								@Override
								public void onClick(DialogInterface dialog, int which) {
									// TODO Auto-generated method stub
									finish();
								}
							});
			    		}
			    		;

			    	}else if(obj.crm_status!=null){
						//int error_index = Integer.parseInt(obj.crm_status);
						//Prompt.Alert(this, Constant.CRM_STATUS[error_index]);
						Prompt.Dialog(LoginPasswordDialog.this, false, "提醒", Prompt.GetStatus(obj.status, obj.crm_status), null);
					}
			    } else{
			    	//Log.e(TAG, "请求失败");
			    	Prompt.Dialog(LoginPasswordDialog.this, false, "提醒", "网络超时", null);
			    }
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			break;
		}
		return false;
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch(v.getId()){
		case R.id.btn_login_looklook_clean_edit:
			content.setText("");
			break;
		case R.id.rly_dialog_lp:
			break;
		case R.id.rly_dialog_container:
			finish();
			break;
		case R.id.btn_login_looklook_password_back:
			finish();
			break;
		case R.id.btn_login_looklook_password_ok:
			username = content.getText().toString();
			/*if(Prompt.checkEmail(username)){
				send_type = 1;
				ZDialog.show(R.layout.progressdialog, false, true, this);
				Requester3.requestForgetPassword(getHandler(), username, "1", "1");
			}else */if(Prompt.checkPhoneNum(username)){
				send_type = 2;
				ZDialog.show(R.layout.progressdialog, false, true, this);
				Requester3.requestForgetPassword(getHandler(), username, "2", "1");
			}else{
				send_type = 0;
				Prompt.Dialog(LoginPasswordDialog.this, false, "错误", "请输入合法的手机号", null);
				/*Prompt.Alert(this, "请输入合法的手机号或邮箱");*/
			}
			break;
		}


	}

}
