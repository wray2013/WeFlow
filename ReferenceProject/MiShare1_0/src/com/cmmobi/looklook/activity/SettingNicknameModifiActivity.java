package com.cmmobi.looklook.activity;

import java.io.UnsupportedEncodingException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import cn.zipper.framwork.core.ZActivity;
import cn.zipper.framwork.core.ZDialog;

import com.cmmobi.looklook.R;
import com.cmmobi.looklook.common.constant.Constant;
import com.cmmobi.looklook.common.gson.GsonResponse3;
import com.cmmobi.looklook.common.gson.Requester3;
import com.cmmobi.looklook.common.utils.CmmobiClickAgentWrapper;
import com.cmmobi.looklook.prompt.Prompt;

/**
 * 昵称修改
 * 
 * @author youtian
 * 
 */
public class SettingNicknameModifiActivity extends ZActivity implements TextWatcher{

	private ImageView iv_back; //返回
	private TextView tv_commit; //提交
	private EditText et_newnickname; //新昵称输入
	private ImageView iv_clear; //清除et_nickname
	private String newnickname;
	private final int max = 16;
	private String originalNickName = "";
	
//	private final String reg ="^([a-z]|[A-Z]|[0-9]|[\u2E80-\u9FFF]){3,}|@(?:[\\w](?:[\\w-]*[\\w])?\\.)+[\\w](?:[\\w-]*[\\w])?|[wap.]{4}|[www.]{4}|[blog.]{5}|[bbs.]{4}|[.com]{4}|[.cn]{3}|[.net]{4}|[.org]{4}|[http://]{7}|[ftp://]{6}$";  
//
//	private Pattern pattern = Pattern.compile(reg);
	private String TAG = "SettingNicknameModifiActivity";
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_settting_nickname_modify);

		iv_back = (ImageView) findViewById(R.id.iv_back);
		iv_back.setOnClickListener(this);
		iv_back.setOnLongClickListener(new View.OnLongClickListener() {
			
			@Override
			public boolean onLongClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(SettingNicknameModifiActivity.this,LookLookActivity.class);
				intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(intent);
				SettingNicknameModifiActivity.this.finish();
				return false;
			}
		});
		tv_commit = (TextView) findViewById(R.id.tv_commit);
		tv_commit.setOnClickListener(this);
		tv_commit.setEnabled(false);
		
		et_newnickname = (EditText) findViewById(R.id.et_newnickname);
		
/*		String userID = ActiveAccount.getInstance(this).getUID();
		AccountInfo accountInfo = AccountInfo.getInstance(userID);
		if (accountInfo != null && accountInfo.nickname != null) {
			et_newnickname.setText(accountInfo.nickname);
			tv_textnum.setText((max - accountInfo.nickname.length()) + "");
		}*/
		try{
			if(getIntent().getExtras() != null){
				originalNickName = getIntent().getExtras().getString("nickname");
				if (originalNickName == null) {
					originalNickName = "";
				}
				et_newnickname.setText(originalNickName);
				et_newnickname.setSelection(originalNickName.length());
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
			newnickname = et_newnickname.getText().toString().trim();
			if(newnickname == null || newnickname.equals("")){
				Prompt.Dialog(this, false, "提示", "昵称为必填项", null);
			}else {
				Intent intent = new Intent(); 
				intent.putExtra("newnickname", newnickname);
				SettingNicknameModifiActivity.this.setResult(RESULT_OK, intent);
				SettingNicknameModifiActivity.this.finish();
			} 
			
			break;
		case R.id.iv_clear:
			et_newnickname.setText(null);
			break;
		}
	}

	@Override
	public boolean handleMessage(Message msg) {
		// TODO Auto-generated method stub
		switch (msg.what) {
		case Requester3.RESPONSE_TYPE_CHECK_NICKNAME: 
			ZDialog.dismiss();
			try {
				GsonResponse3.checkNickNameExistResponse res = (GsonResponse3.checkNickNameExistResponse) msg.obj;
				if(res != null){
					if ("0".equals(res.status)) {
//						Prompt.Dialog(this, false, "提示", "该昵称可用", new DialogInterface.OnClickListener() {
//							
//							@Override
//							public void onClick(DialogInterface dialog, int which) {
								// TODO Auto-generated method stub
								Intent intent = new Intent(); 
								intent.putExtra("newnickname", newnickname);
								SettingNicknameModifiActivity.this.setResult(RESULT_OK, intent);
								SettingNicknameModifiActivity.this.finish();
//							}
//						});
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
	
	 //输入表情前的光标位置
	 private int cursorPos;

	@Override
	public void afterTextChanged(Editable s) {
		// TODO Auto-generated method stub
		et_newnickname.removeTextChangedListener(this);
		String editNumOri = et_newnickname.getText().toString();
		cursorPos = et_newnickname.getSelectionStart();
		Log.d(TAG,"cursorPos = " + cursorPos);
		String editNum = removeExpression(editNumOri);
		if (!editNum.equals(editNumOri)) {
			
			if (cursorPos > editNum.length()) {
				cursorPos = editNum.length();
			}
			Log.d(TAG,"editNum = " + editNum + "editNumOri = " + editNumOri + " cursorPos = " + cursorPos);
			et_newnickname.setText(editNum);
			et_newnickname.setSelection(cursorPos);
			
		}
		
		byte[] strBytes = null;
		try {
			strBytes = editNum.getBytes("UTF-16BE");
		
			if (getCharLength(strBytes) > max) {
				int strLen = editNum.length();
				for (int i = strLen - 1;i > 0;i--) {
					String subString = editNum.substring(0, i);
					byte[] subStrBytes = subString.getBytes("UTF-16BE");
					if (getCharLength(subStrBytes) <= max) {
						et_newnickname.setText(subString);
						et_newnickname.setSelection(subString.length());
						break;
					}
				}
				
				Prompt.Dialog(this, false, "提示", "昵称不合法，长度过长，请重新输入", null);
	
			}
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		if (!"".equals(editNum) && !originalNickName.equals(et_newnickname.getText().toString())) {
			tv_commit.setEnabled(true);
		} else {
			tv_commit.setEnabled(false);
		}
		et_newnickname.addTextChangedListener(this);
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
	
	public static String removeExpression(String str) {
		if (TextUtils.isEmpty(str)) {
			return str;
		}
		int strLen = str.length();
		StringBuffer buffer = new StringBuffer();  
        for (int i = 0; i < strLen; i++) {  
            char c = str.charAt(i);  
            if (c == '@') {
            	continue;
            }
            // 第一个字符为以下时，过滤掉  
            if (c == 55356 || c == 55357 || c == 10060 || c == 9749 || c == 9917 || c == 10067 || c == 10024  
                    || c == 11088 || c == 9889 || c == 9729 || c == 11093 || c == 9924) {  
            	
            	i++;
            	continue;  
            } else {  
                buffer.append(c);  
            }  
        }
        return buffer.toString();
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
