package cn.zipper.framwork.view;

import android.content.Context;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

public abstract class ZBaseView extends View {
	
	protected int width;
	protected int height;
	protected Paint paint;
	

	public ZBaseView(Context context, AttributeSet attrs) {
		super(context, attrs);
		paint = new Paint();
		paint.setAntiAlias(true);
	}
	
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int widthSpecMode = MeasureSpec.getMode(widthMeasureSpec);
		int heightSpecMode = MeasureSpec.getMode(heightMeasureSpec);

		int widthSize = MeasureSpec.getSize(widthMeasureSpec);
		int heightSize = MeasureSpec.getSize(heightMeasureSpec);

		switch (widthSpecMode) {
		case MeasureSpec.EXACTLY:
			width = widthSize;
			break;
		case MeasureSpec.AT_MOST:
			width = widthSize;
			break;
		case MeasureSpec.UNSPECIFIED:
			width = widthSize;
			break;
		}

		switch (heightSpecMode) {
		case MeasureSpec.EXACTLY:
			height = heightSize;
			break;
		case MeasureSpec.AT_MOST:
			height = heightSize;
			break;
		case MeasureSpec.UNSPECIFIED:
			height = heightSize;
			break;
		}
		setMeasuredDimension(width, height);
//		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		onInit();
	}
	
	protected abstract void onInit();
	protected abstract void onStart();
	protected abstract void onStop();

}