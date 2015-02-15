package com.cmmobi.looklook.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.support.v4.content.LocalBroadcastManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
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
import com.cmmobi.looklook.prompt.Prompt;

/**
 * 昵称修改
 * 
 * @author youtian
 * 
 */
public class SettingNicknameModifiActivity extends ZActivity implements TextWatcher{

	private ImageView iv_back; //返回
	private ImageView iv_commit; //提交
	private EditText et_newnickname; //新昵称输入
	private ImageView iv_clear; //清除et_nickname
	private String newnickname;
	private final int max = 20;
	private TextView tv_textnum; //剩余的输入文字数
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_settting_nickname_modify);

		iv_back = (ImageView) findViewById(R.id.iv_back);
		iv_back.setOnClickListener(this);
		
		iv_commit = (ImageView) findViewById(R.id.iv_commit);
		iv_commit.setOnClickListener(this);
		
		et_newnickname = (EditText) findViewById(R.id.et_newnickname);
		tv_textnum = (TextView) findViewById(R.id.tv_textnum);
		
/*		String userID = ActiveAccount.getInstance(this).getUID();
		AccountInfo accountInfo = AccountInfo.getInstance(userID);
		if (accountInfo != null && accountInfo.nickname != null) {
			et_newnickname.setText(accountInfo.nickname);
			tv_textnum.setText((max - accountInfo.nickname.length()) + "");
		}*/

		try{
			if(getIntent().getExtras() != null){
				String tmpString = getIntent().getExtras().getString("nickname");
				et_newnickname.setText(tmpString);
				tv_textnum.setText((max - tmpString.length()) + "");
				et_newnickname.setSelection(tmpString.length());
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		
		iv_clear = (ImageView) findViewById(R.id.iv_clear);
		iv_clear.setOnClickListener(this);
		
		et_newnickname.addTextChangedListener(this);
		
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
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.iv_back:
			this.finish();
			break;
		case R.id.iv_commit:
			String oldnickname = AccountInfo.getInstance(ActiveAccount.getInstance(this).getUID()).nickname;
			newnickname = et_newnickname.getText().toString().trim();
			if(newnickname == null || newnickname.equals("")){
				Prompt.Dialog(this, false, "提示", "请输入新昵称", null);
			}else if(oldnickname!=null && newnickname.equals(oldnickname)){
				Intent intent = new Intent(); 
				intent.putExtra("newnickname", newnickname);
				SettingNicknameModifiActivity.this.setResult(RESULT_OK, intent);
				SettingNicknameModifiActivity.this.finish();
			}else{
				ZDialog.show(R.layout.progressdialog, false, true, this);
				Requester2.checkNickNameExist(handler, newnickname);
			}
			break;
		case R.id.iv_clear:
			et_newnickname.setText(null);
			tv_textnum.setText(max + "");
			break;
		}
	}

	@Override
	public boolean handleMessage(Message msg) {
		// TODO Auto-generated method stub
		switch (msg.what) {
		case Requester2.RESPONSE_TYPE_CHECK_NICKNAME: 
			ZDialog.dismiss();
			try {
				GsonResponse2.checkNickNameExistResponse res = (GsonResponse2.checkNickNameExistResponse) msg.obj;
				if(res != null){
					if ("0".equals(res.status)) {
						Prompt.Dialog(this, false, "提示", "该昵称可用", new DialogInterface.OnClickListener() {
							
							@Override
							public void onClick(DialogInterface dialog, int which) {
								// TODO Auto-generated method stub
								Intent intent = new Intent(); 
								intent.putExtra("newnickname", newnickname);
								SettingNicknameModifiActivity.this.setResult(RESULT_OK, intent);
								SettingNicknameModifiActivity.this.finish();
							}
						});
					} else {
						if(res.status.equals("200600")){
							Prompt.Dialog(this, false, "提示", Constant.CRM_STATUS[Integer.parseInt(res.crm_status)], null);
						}else if(res.status.equals("1")){
							Prompt.Dialog(this, false, "提示", "昵称已存在，不可用", null);
						}else{
							Prompt.Dialog(this, false, "提示", "操作失败，请稍后再试", null);
						}
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

	@Override
	public void afterTextChanged(Editable s) {
		// TODO Auto-generated method stub
		String editNum = et_newnickname.getText().toString();
		if (editNum.length() <= max) {
			tv_textnum.setText("" + (max - editNum.length()));
		} else {
			et_newnickname.setText(editNum.substring(0, max));
			et_newnickname.setSelection(editNum.substring(0, max).length());
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
}
