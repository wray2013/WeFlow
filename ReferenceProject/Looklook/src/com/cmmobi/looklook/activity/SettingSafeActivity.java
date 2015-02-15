package com.cmmobi.looklook.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import cn.zipper.framwork.core.ZActivity;
import cn.zipper.framwork.core.ZDialog;

import com.cmmobi.looklook.R;
import com.cmmobi.looklook.common.constant.Constant;
import com.cmmobi.looklook.common.gson.GsonResponse2;
import com.cmmobi.looklook.common.gson.Requester2;
import com.cmmobi.looklook.common.utils.CmmobiClickAgentWrapper;
import com.cmmobi.looklook.common.utils.UmengclickAgentWrapper;
import com.cmmobi.looklook.info.profile.LoginSettingManager;
import com.cmmobi.looklook.prompt.Prompt;

/**
 * 手机号绑定（保险箱 ）
 * @author youtian
 *
 */
public class SettingSafeActivity extends ZActivity implements TextWatcher{

		private Button btn_next;// 下一步
		private EditText et_phonenum;	
		private ImageView iv_back;

		@Override
		protected void onCreate(Bundle savedInstanceState) {
			// TODO Auto-generated method stub
			super.onCreate(savedInstanceState);
			setContentView(R.layout.activity_setting_phonesafe);

			btn_next = (Button) findViewById(R.id.btn_next);
			btn_next.setOnClickListener(this);
			
			et_phonenum = (EditText) findViewById(R.id.et_phonenum);
			et_phonenum.addTextChangedListener(this);
			
			iv_back = (ImageView) findViewById(R.id.iv_back);
			iv_back.setOnClickListener(this);
			
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
		
		

		@Override
		public void afterTextChanged(Editable s) {
			// TODO Auto-generated method stub
			if(Prompt.checkPhoneNum(et_phonenum.getText().toString().trim())){
				btn_next.setEnabled(true);
			}else{
				btn_next.setEnabled(false);
			}
		}


		@Override
		public void beforeTextChanged(CharSequence s, int start, int count,
				int after) {
			// TODO Auto-generated method stub
			
		}


		@Override
		public void onTextChanged(CharSequence s, int start, int before, int count) {
			// TODO Auto-generated method stub
			
		}


		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.btn_next:
				if(et_phonenum.getText().toString().trim().equals(this.getIntent().getExtras().get("phonenum"))){
					Prompt.Dialog(this, false, "提示", "请输入不同于手机号账号绑定的手机号码", null);
				}else{
					ZDialog.show(R.layout.progressdialog, false, true, this);
					Requester2.getCheckNo(handler,et_phonenum.getText().toString().trim(), LoginSettingManager.GET_CHECK_NO_PHONE_SEC);
				}
				break;
			case R.id.iv_back:
				this.onBackPressed();
				break;
			default:
				break;
			}
		}

		@Override
		protected void onActivityResult(int requestCode, int resultCode, Intent data) {
			// TODO Auto-generated method stub
			//super.onActivityResult(requestCode, resultCode, data);
			if(requestCode == 0 && resultCode == RESULT_OK){
				this.setResult(RESULT_OK);
				this.finish();
			}
			
		}


		@Override
		public boolean handleMessage(Message msg) {

			switch (msg.what) {

			case Requester2.RESPONSE_TYPE_CHECKNO:
				try {
					ZDialog.dismiss();
					GsonResponse2.checkNoResponse gsn = (GsonResponse2.checkNoResponse) msg.obj;
					if(gsn != null){
					if (gsn.status.equals("0")) {
						Intent intent = new Intent(this,
								SettingSafeCheckNoActivity.class);
						intent.putExtra("phonenum", et_phonenum.getText().toString().trim());
						startActivityForResult(intent, 0);	
					}else{
						if(gsn.status.equals("200600")){
							Prompt.Dialog(this, false, "提示", Constant.CRM_STATUS[Integer.parseInt(gsn.crm_status)], null);
						}else{
							Prompt.Dialog(this, false, "提示", "操作失败，请稍后再试", null);
						}
						btn_next.setEnabled(true);
					}
					}else{
						Prompt.Dialog(this, false, "提示", "操作失败，网络不给力", null);
						btn_next.setEnabled(true);
					}
				} catch (Exception e) {
					// TODO: handle exception
				}
				break;
			}

			return false;
		}

	}
