package com.cmmobi.looklook.common.view;

import java.util.Calendar;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.cmmobi.looklook.R;

/**
 * @author wuxiang
 * @email wuxiang@cmmobi.com
 * @date  2013-7-5
 */
public class ClockView extends RelativeLayout {

	public ClockView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	public ClockView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public ClockView(Context context) {
		super(context);
		init();
	}

	private LayoutInflater inflater;
	private ImageView ivHour;
	private ImageView ivMinutes;
	private Bitmap bmHour;
	private Bitmap bmMinutes;
	
	private int hWidth;
	private int hHeight;
	private int sWidth;
	private int sHeight;
	private void init() {
		inflater = LayoutInflater.from(getContext());
		View v = inflater.inflate(R.layout.include_clock_view, null);
		ivHour = (ImageView) v.findViewById(R.id.iv_clock_h);
		ivMinutes = (ImageView) v.findViewById(R.id.iv_clock_s);
		addView(v);
		bmHour = BitmapFactory.decodeResource(getResources(),
				R.drawable.shijian_clock_h);
		bmMinutes = BitmapFactory.decodeResource(getResources(),
				R.drawable.shijian_clock_s);
		hWidth = bmHour.getWidth();
		hHeight = bmHour.getHeight();
		sWidth = bmMinutes.getWidth();
		sHeight = bmMinutes.getHeight();
	}
	
	public void setTime(String timemilli){
		if(timemilli!=null&&timemilli.length()>0){
			Calendar c = Calendar.getInstance();
			c.setTimeInMillis(Long.parseLong(timemilli));
			float hour = c.get(Calendar.HOUR_OF_DAY);
			float minutes = c.get(Calendar.MINUTE);// 分
			float hAngle = (hour / 12f) * 360f;
			float hMinutes = (minutes / 60f) * 360f;
			Matrix m = new Matrix();
			m.setRotate(hAngle);
			Bitmap bHour = Bitmap.createBitmap(bmHour, 0, 0, hWidth, hHeight,
					m, true);
			m.setRotate(hMinutes);
			Bitmap bMinutes = Bitmap.createBitmap(bmMinutes, 0, 0, sWidth,
					sHeight, m, true);
			ivHour.setImageBitmap(bHour);
			ivMinutes.setImageBitmap(bMinutes);
		}
	}
	
}
