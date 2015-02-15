package com.cmmobi.looklook.common.view;

import com.cmmobi.looklook.common.utils.ImageState;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.ImageView;

public class ClipView extends ImageView {
	
	private int leftEdge = 0;
	private int rightEdge = 0;
	private int topEdge = 0;
	private int bottomEdge = 0;
	
	private Paint mOutPaint = null;
	
	private int viewWidth = 0;
	private int viewHeight = 0;
	
	private ImageState imageState = new ImageState();

	public ClipView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		init();
	}
	
	private void init() {
		mOutPaint = new Paint();
		mOutPaint.setColor(0x80000000);
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		// TODO Auto-generated method stub
		super.onDraw(canvas);
		
		getMapState(getImageMatrix(), imageState);
		leftEdge = (int) imageState.getLeft();
		rightEdge = (int) imageState.getRight();
		topEdge = (int) imageState.getTop();
		bottomEdge = (int) imageState.getBottom();
		
		viewWidth  = getMeasuredWidth();
    	viewHeight = getMeasuredHeight();
		
		canvas.drawRect(0,0,viewWidth,topEdge, mOutPaint);
		canvas.drawRect(0,topEdge,leftEdge,bottomEdge, mOutPaint);
		canvas.drawRect(rightEdge, topEdge,viewWidth,bottomEdge,mOutPaint);
		canvas.drawRect(0, bottomEdge,viewWidth,viewHeight,mOutPaint);
		
	}
	
	private void getMapState(Matrix mtx,ImageState imageState) {
		Rect rect = getDrawable().getBounds(); 
		float[] values = new float[9]; 
		mtx.getValues(values); 
		imageState.setLeft(values[2]); 
		imageState.setTop(values[5]); 
		imageState.setRight(imageState.getLeft() + rect.width() * values[0]); 
		imageState.setBottom(imageState.getTop() + rect.height() * values[4]);
		imageState.setScale(values[0]);
	}

}
