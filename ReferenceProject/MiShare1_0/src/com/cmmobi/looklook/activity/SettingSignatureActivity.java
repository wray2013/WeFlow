package com.cmmobi.looklook.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.style.ImageSpan;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import cn.zipper.framwork.core.ZActivity;

import com.cmmobi.looklook.R;
import com.cmmobi.looklook.common.utils.CmmobiClickAgentWrapper;
import com.cmmobi.looklook.common.view.FriendsExpressionView;
/**
 * 个性签名
 * 
 * @author youtian
 * 
 */
public class SettingSignatureActivity extends ZActivity implements TextWatcher{

	private EditText et_signature;
	private TextView tv_num;
	private final int Num = 30;
	private ImageView iv_back;
	private TextView tv_commit;
	
	private String newsignature;
	private String signature = "";
	
	private Boolean isCreate = true;
	
	private String TAG = "SettingSignatureActivity";
		
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setting_signature);

		et_signature = (EditText) findViewById(R.id.et_signature);
		tv_num = (TextView) findViewById(R.id.tv_num);
		iv_back = (ImageView) findViewById(R.id.iv_back);
		tv_commit = (TextView) findViewById(R.id.tv_commit);

		et_signature.addTextChangedListener(this);
		tv_commit.setOnClickListener(this);
		tv_commit.setEnabled(false);
		iv_back.setOnClickListener(this);
		iv_back.setOnLongClickListener(new View.OnLongClickListener() {
			
			@Override
			public boolean onLongClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(SettingSignatureActivity.this,LookLookActivity.class);
				intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(intent);
				SettingSignatureActivity.this.finish();
				return false;
			}
		});
	}

	@Override
	public void onResume() {
		super.onResume();
		try{
			if(isCreate){	
				signature = this.getIntent().getExtras().getString("signature");
				
				if(signature != null) {
					FriendsExpressionView.replacedExpressions(signature,et_signature);
					tv_num.setText((Num - getTextLength(et_signature.getText().toString())) + "");
					et_signature.setSelection(et_signature.getText().toString().length());
				} else {
					signature = "";
				}
				isCreate = false;
			}
		}catch(Exception e){
			e.printStackTrace();
		}
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

	//输入表情前的光标位置
	private int cursorPos;
		 
	@Override
	public void afterTextChanged(Editable s) {
		et_signature.removeTextChangedListener(this);
		String editNum = et_signature.getText().toString();
		cursorPos = et_signature.getSelectionStart();
		
		FriendsExpressionView.replacedExpressions(et_signature.getText().toString(),et_signature);
		int textLength = getTextLength(editNum);
		
		if (textLength <= Num) {
			tv_num.setText("" + (Num - textLength));
		} else {
			FriendsExpressionView.replacedExpressions(subString(editNum, Num),et_signature);
		}
		
		editNum = et_signature.getText().toString();
		if (cursorPos > editNum.length()) {
			cursorPos = editNum.length();
		}
		et_signature.setSelection(cursorPos);
		if (!signature.equals(et_signature.getText().toString())) {
			tv_commit.setEnabled(true);
		} else {
			tv_commit.setEnabled(false);
		}
		et_signature.addTextChangedListener(this);
	}
	
	/**
	 * 获取字符串长度，输入法表情算一个字符
	 * @param text
	 * @return
	 */
	private int getTextLength(String text) {
		if (text == null || "".equals(text)) {
			return 0;
		}
		
		int size = text.length();
		ImageSpan[] imgSpans = et_signature.getText().getSpans(0, size, ImageSpan.class);
		ImageSpan span = null;
		int startIndex = -1;
		int endIndex = -1;
		int index = 0;
		if (imgSpans != null && imgSpans.length > 0) {
			span = imgSpans[index++];
			startIndex = et_signature.getText().getSpanStart(span);
			endIndex = et_signature.getText().getSpanEnd(span);
		}
		int len = 0;
		for (int i = 0;i < size;i++) {
			len++;
			if (i == startIndex) {
				i = endIndex - 1;
				len++;
				if (index < imgSpans.length) {
					span = imgSpans[index++];
					startIndex = et_signature.getText().getSpanStart(span);
					endIndex = et_signature.getText().getSpanEnd(span);
				}
				continue;
			}
			int c = text.charAt(i);
			if (c == 55356 || c == 55357 || c == 10060 || c == 9749 || c == 9917 || c == 10067 || c == 10024  
                    || c == 11088 || c == 9889 || c == 9729 || c == 11093 || c == 9924) {
				len++;
				i++;
			}
			
		}
		return len;
	}
	
	private String subString(String text,int end) {
		if (text == null || "".equals(text)) {
			return text;
		}
		int size = text.length();
		int len = 0;
		int i = 0;
		ImageSpan[] imgSpans = et_signature.getText().getSpans(0, size, ImageSpan.class);
		ImageSpan span = null;
		int startIndex = -1;
		int endIndex = -1;
		int index = 0;
		if (imgSpans != null && imgSpans.length > 0) {
			span = imgSpans[index++];
			startIndex = et_signature.getText().getSpanStart(span);
			endIndex = et_signature.getText().getSpanEnd(span);
		}
		int state = 0;
		for (i = 0;i < size;i++) {
			state = 1;
			len++;
			if (i == startIndex) {
				state = 2;
				i = endIndex - 1;
				len++;
				if (index < imgSpans.length) {
					span = imgSpans[index++];
					startIndex = et_signature.getText().getSpanStart(span);
					endIndex = et_signature.getText().getSpanEnd(span);
				}
				continue;
			}
			int c = text.charAt(i);
			if (c == 55356 || c == 55357 || c == 10060 || c == 9749 || c == 9917 || c == 10067 || c == 10024  
                    || c == 11088 || c == 9889 || c == 9729 || c == 11093 || c == 9924) {
				state = 3;
				i++;
				len++;
			}
			
			if (len >= end) {
				break;
			}
		}
		if (len > end) {
			if (state == 2) {
				return text.substring(0, startIndex);
			} else if (state == 3) {
				return text.substring(0, i - 1);
			} 	
			return text.substring(0, i + 1);
		} else {
			return text.substring(0, i + 1);
		}
	}

	@Override
	public void beforeTextChanged(CharSequence s, int start, int count,
			int after) {
		// TODO Auto-generated method stub
		Log.d(TAG,"beforeTextChanged s = " + s);
	}

	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) {
		// TODO Auto-generated method stub
		Log.d(TAG,"onTextChanged s = " + s);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.iv_back:
			this.finish();
			break;
		case R.id.tv_commit:
			newsignature = et_signature.getText().toString().trim();
			Intent ret = new Intent();
			ret.putExtra("newsignature", newsignature);
			this.setResult(RESULT_OK, ret);
			this.finish();
			break;
		}

	}

	@Override
	public boolean handleMessage(Message msg) {
		// TODO Auto-generated method stub
		return false;
	}
	
}
