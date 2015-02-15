package com.cmmobi.looklook.activity;

import java.io.UnsupportedEncodingException;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnKeyListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import cn.zipper.framwork.core.ZActivity;
import cn.zipper.framwork.core.ZDialog;

import com.cmmobi.looklook.R;
import com.cmmobi.looklook.common.gson.GsonResponse3;
import com.cmmobi.looklook.common.gson.Requester3;
import com.cmmobi.looklook.common.utils.CmmobiClickAgentWrapper;
import com.cmmobi.looklook.common.view.EmojiPaser;
import com.cmmobi.looklook.info.profile.AccountInfo;
import com.cmmobi.looklook.info.profile.ActiveAccount;
import com.cmmobi.looklook.prompt.Prompt;

/**
 * @author wuxiang
 * @email wuxiang@cmmobi.com
 * @date  2013-12-18
 */
public class BacknameActivity extends ZActivity implements TextWatcher {

	private ImageView iv_back; //返回
	private TextView tv_commit; //提交
	private EditText et_newbackname; //新昵称输入
	private ImageView iv_clear; //清除et_nickname
	private String oldBackName = "";
	private String newbackname;
	private final int max = 16;
	private String otherUserid;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_backname);

		iv_back = (ImageView) findViewById(R.id.iv_back);
		iv_back.setOnClickListener(this);
		
		tv_commit = (TextView) findViewById(R.id.tv_commit);
		tv_commit.setOnClickListener(this);
		
		et_newbackname = (EditText) findViewById(R.id.et_newnickname);
		try{
			if(getIntent().getExtras() != null){
				String oldBackName = getIntent().getExtras().getString("backname");
				otherUserid = getIntent().getExtras().getString("otherUserid");
				if(oldBackName!=null){
					et_newbackname.setText(oldBackName);
					et_newbackname.setSelection(oldBackName.length());
				} else {
					oldBackName = "";
				}
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		iv_clear = (ImageView) findViewById(R.id.iv_clear);
		iv_clear.setOnClickListener(this);
		et_newbackname.addTextChangedListener(this);
		et_newbackname.setOnKeyListener(new OnKeyListener() {
			
			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				if(keyCode == KeyEvent.KEYCODE_ENTER){
	                /*隐藏软键盘*/
	                InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
	                if(inputMethodManager.isActive()){
	                    inputMethodManager.hideSoftInputFromWindow(v.getApplicationWindowToken(), 0);
	                }
	                commit();
	                return true;
	            }
				return false;
			}
		});
		
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
		case R.id.tv_commit:
			commit();
			break;
		case R.id.iv_clear:
			et_newbackname.setText(null);
			break;
		}
	}
	
	private void commit(){
		String oldnickname = AccountInfo.getInstance(ActiveAccount.getInstance(this).getUID()).nickname;
		newbackname = et_newbackname.getText().toString().trim();
		if(newbackname == null || newbackname.equals("")){
			Prompt.Dialog(this, false, "提示", "请输入新备注名", null);
		}else if(oldnickname!=null && newbackname.equals(oldnickname)){
			Intent intent = new Intent(); 
			intent.putExtra("newbackname", newbackname);
			BacknameActivity.this.setResult(RESULT_OK, intent);
			BacknameActivity.this.finish();
		}else{
			ZDialog.show(R.layout.progressdialog, false, true, this);
			newbackname=EmojiPaser.getInstance().format(newbackname);
			Requester3.setUserAlias(handler, otherUserid, newbackname);
		}
	}

	@Override
	public boolean handleMessage(Message msg) {
		switch (msg.what) {
		case Requester3.RESPONSE_TYPE_SET_USERALIAS: 
			ZDialog.dismiss();
			GsonResponse3.setUserAliasResponse res = (GsonResponse3.setUserAliasResponse) msg.obj;
			if(res != null){
				if ("0".equals(res.status)) {
					Intent intent = new Intent(); 
					intent.putExtra("newbackname", newbackname);
					try {
						String uid = ActiveAccount.getInstance(this).getUID();
						AccountInfo.getInstance(uid).privateMsgManger.updateMarkName(otherUserid, newbackname);
					} catch (Exception e) {
					}
					
					BacknameActivity.this.setResult(RESULT_OK, intent);
					BacknameActivity.this.finish();
				} else {
					Prompt.Alert("操作失败，请稍后再试");
				}
			}else{
				Prompt.Alert("操作失败，网络不给力");
			}
			break;
		}
		return false;
	}

	private int cursorPos;
	@Override
	public void afterTextChanged(Editable s) {
		// TODO Auto-generated method stub
		et_newbackname.removeTextChangedListener(this);
		String editNumOri = et_newbackname.getText().toString();
		cursorPos = et_newbackname.getSelectionStart();
		String editNum = SettingNicknameModifiActivity.removeExpression(editNumOri);
		if (!editNum.equals(editNumOri)) {
			
			if (cursorPos > editNum.length()) {
				cursorPos = editNum.length();
			}
			et_newbackname.setText(editNum);
			et_newbackname.setSelection(cursorPos);
		}
		/*if (editNum.length() > max) {
			et_newbackname.setText(editNum.substring(0, max));
			et_newbackname.setSelection(editNum.substring(0, max).length());
		}
		*/
		byte[] strBytes = null;
		try {
			strBytes = editNum.getBytes("UTF-16BE");
		
			if (getCharLength(strBytes) > max) {
				int strLen = editNum.length();
				for (int i = strLen - 1;i > 0;i--) {
					String subString = editNum.substring(0, i);
					byte[] subStrBytes = subString.getBytes("UTF-16BE");
					if (getCharLength(subStrBytes) <= max) {
						et_newbackname.setText(subString);
						et_newbackname.setSelection(subString.length());
						break;
					}
				}
				
				Prompt.Dialog(this, false, "提示", "备注名不合法，长度过长，请重新输入", null);
	
			}
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		if (!"".equals(editNum) && !oldBackName.equals(et_newbackname.getText().toString())) {
			tv_commit.setEnabled(true);
		} else {
			tv_commit.setEnabled(false);
		}
		et_newbackname.addTextChangedListener(this);
	}

	public static int getCharLength(byte[] strBytes) {
		if (strBytes == null) {
			return 0;
		}
		int length = 0;
		for (byte bt:strBytes) {
			if (bt == 0) {
				continue;
			} else {
				length ++;
			}
		}
		return length;
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
