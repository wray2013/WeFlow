package com.cmmobi.looklook.common.utils;

import android.text.Editable;
import android.widget.EditText;

public class LooklookTextWatcher implements  android.text.TextWatcher{
	private int limitLen = 0;
	private EditText content;
	
	private CharSequence temp;
	private int selectionStart;
	private int selectionEnd;
	
	public LooklookTextWatcher(EditText content,  int limitLen){
		this.limitLen = limitLen;
		this.content = content;
	}

	@Override
	public void beforeTextChanged(CharSequence s, int start, int count,
			int after) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) {
		// TODO Auto-generated method stub
		temp = s;
	}

	@Override
	public void afterTextChanged(Editable s) {
		// TODO Auto-generated method stub
		selectionStart = content.getSelectionStart();
		selectionEnd = content.getSelectionEnd();
		if (temp.length() >= limitLen) {
			s.delete(selectionStart - 1, selectionEnd);
			int tempSelection = selectionEnd;
			content.setText(s);
			content.setSelection(tempSelection);// 设置光标在最后
		}
	}
	

}
