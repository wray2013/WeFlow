package com.etoc.weflow.view;

import android.animation.ObjectAnimator;
import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.util.AttributeSet;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.TextView;

/**
 * * 动画显示数字 * Created by Ray on 15/2/3.
 * */
public class MagicTextView extends TextView {

	float number;
	String numStr;

	public MagicTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	@TargetApi(11)
	public void showNumberWithAnimation(String number, int duration) {
		showNumberWithAnimation(number, duration, false);
	}
	
	@TargetApi(11)
	public void showNumberWithAnimation(String number, int duration, boolean isFloat) {
		float num = 0.0f;
		try {
			num = Float.parseFloat(number);
		} catch(Exception e) {
			
			e.printStackTrace();
		}
		numStr = num + "";
		if(isFloat) {
			showNumberWithAnimation(num, duration);
		} else {
			showNumberWithAnimation((int)num, duration);
		}
	}
	
	@TargetApi(11)
	public void showNumberWithAnimation(int number, int duration) {
		numStr = number + "";
		if (VERSION.SDK_INT >= VERSION_CODES.HONEYCOMB) {
			// 修改number属性，会调用setNumber方法
			ObjectAnimator objectAnimator = ObjectAnimator.ofInt(this,
					"number", 0, number);
			objectAnimator.setDuration(duration);
			// 加速器，从慢到快到再到慢
			objectAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
			objectAnimator.start();
		} else {
		}
	}
	
	@TargetApi(11)
	public void showNumberWithAnimation(float number, int duration) {
		numStr = number + "";
		if (VERSION.SDK_INT >= VERSION_CODES.HONEYCOMB) {
			// 修改number属性，会调用setNumber方法
			ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(this,
					"number", 0, number);
			objectAnimator.setDuration(duration);
			// 加速器，从慢到快到再到慢
			objectAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
			objectAnimator.start();
		} else {
		}
	}

	public String getValue() {
		return numStr;
	}
	
	public float getNumber() {
		return number;
	}
	
	public void setNumber(int number) {
		this.number = number;
		setText(String.format("%d", number));
	}

	public void setNumber(float number) {
		this.number = number;
		setText(String.format("%1$07.2f", number));
	}
}
