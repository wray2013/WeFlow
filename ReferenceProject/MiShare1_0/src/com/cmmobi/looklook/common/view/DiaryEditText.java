package com.cmmobi.looklook.common.view;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.cmmobi.looklook.activity_del.EditMediaDetailActivity;

public class DiaryEditText extends EditText {
	private EditMediaDetailActivity mActivity;
	
	public DiaryEditText(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		mActivity = (EditMediaDetailActivity) context;
		// TODO Auto-generated constructor stub
		Log.d("WJM","DiaryEditText Create");
	}

	public DiaryEditText(Context context) {
		super(context);
		mActivity = (EditMediaDetailActivity) context;
		// TODO Auto-generated constructor stub
		Log.d("WJM","DiaryEditText Create");
	}

	public DiaryEditText(Context context, AttributeSet attrs) {
		super(context, attrs);
		mActivity = (EditMediaDetailActivity) context;
		// TODO Auto-generated constructor stub
		Log.d("WJM","DiaryEditText Create");
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// TODO Auto-generated method stub
		Log.d("WJM","DiaryEditText onTouchEvent");
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			break;
		case MotionEvent.ACTION_UP:
			if (mActivity.isLongPressed) {
				return false;
			}
			if (mActivity.fragmentType != EditMediaDetailActivity.FRAGMENT_TEXT_INPUT) {
				mActivity.lastFragmentType = mActivity.fragmentType;
				mActivity.goToPage(EditMediaDetailActivity.FRAGMENT_TEXT_INPUT, false);
			}
			break;
		}
		return super.onTouchEvent(event);
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if (keyCode == KeyEvent.KEYCODE_ENTER ) {
		    Log.d("WJM","DiaryEditText --------------------+onKeyDown---------软键盘弹出");
		}
		return super.onKeyDown(keyCode, event);
	}
	
	@Override
	public boolean onKeyPreIme(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN ) {
	        Log.d("WJM","DiaryEditText --------------------+onKeyPreIme---------软键盘退出");
	    }
		return super.onKeyPreIme(keyCode, event);
	}
	
	public static boolean isImeShow(Context context){
        InputMethodManager imm = (InputMethodManager) context.getSystemService(context.INPUT_METHOD_SERVICE);
        return imm.isActive();
    }

}
