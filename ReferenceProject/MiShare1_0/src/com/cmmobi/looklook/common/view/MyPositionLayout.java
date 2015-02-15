package com.cmmobi.looklook.common.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

public class MyPositionLayout extends RelativeLayout {

	private int radius = 0;
	private Paint paint = null;
	public MyPositionLayout(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		paint = new Paint();
		paint.setColor(0x8000a0e9);
		// TODO Auto-generated constructor stub
	}

	public MyPositionLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		paint = new Paint();
		paint.setColor(0x8000a0e9);
		// TODO Auto-generated constructor stub
	}

	public MyPositionLayout(Context context) {
		super(context);
		paint = new Paint();
		paint.setColor(0x8000a0e9);
		// TODO Auto-generated constructor stub
	}

	public void setRadius(int radius) {
		this.radius = radius;
	}

	@Override
	protected void onDraw(Canvas canvas) {
		// TODO Auto-generated method stub
		if (radius > 12) {
			int width = getWidth();
			int height = getHeight();
			canvas.drawCircle(width / 2, height / 2, radius, paint);
		}

		super.onDraw(canvas);
	}
}
