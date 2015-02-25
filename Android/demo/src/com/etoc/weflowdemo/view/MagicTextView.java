package com.etoc.weflowdemo.view;

import android.animation.ObjectAnimator;
import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build.VERSION;
import android.os.Build.VERSION_CODES;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.TextView;

/**
 * * 动画显示数字 * Created by Ray on 15/2/3.
 * */
public class MagicTextView extends TextView {

	float number;

	public MagicTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	@TargetApi(11)
	public void showNumberWithAnimation(float number, int duration) {
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

	public float getNumber() {
		return number;
	}

	public void setNumber(float number) {
		this.number = number;
//		setText(number + "");
		setText(String.format("%1$7.1f", number).trim());
	}
}
