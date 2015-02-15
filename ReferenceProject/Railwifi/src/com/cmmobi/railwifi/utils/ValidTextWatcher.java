package com.cmmobi.railwifi.utils;

import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

public class ValidTextWatcher extends MyTextWatcher {

	private ImageView ivError;
	public ValidTextWatcher(EditText et,ImageView iv) {
		super(et);
		// TODO Auto-generated constructor stub
		this.ivError = iv;
	}
	
	@Override
	public void onTextChanged(CharSequence s, int start, int before,
			int count) {
		// TODO Auto-generated method stub
		ivError.setVisibility(View.GONE);
		super.onTextChanged(s, start, before, count);
	}

}
