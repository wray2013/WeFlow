package com.cmmobi.railwifi.view;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Region;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;

import com.cmmobi.railwifi.utils.DisplayUtil;
import com.cmmobi.railwifi.utils.ImageState;

public class ClipView extends ImageView {
	
	private int leftEdge = 0;
	private int rightEdge = 0;
	private int topEdge = 0;
	private int bottomEdge = 0;
	
	private Paint mOutPaint = null;
	private Paint mInPaint = null;
	private Path mPath;
	
	private int viewWidth = 0;
	private int viewHeight = 0;
	private int edgeRadius = 0;
	private int startX = 0;
	private int startY = 0;
	private Bitmap destBmp = null;
	
	private ImageState imageState = new ImageState();
	private Canvas cv = null;

	public ClipView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		init();
	}
	
	private void init() {
		/*mOutPaint = new Paint();
		mOutPaint.setColor(0x80000000);*/
		mInPaint = new Paint(); 
		mInPaint.setAntiAlias(true); //去锯齿 
		mInPaint.setColor(0xfff65c00); 
		mInPaint.setStyle(Paint.Style.STROKE);
		mInPaint.setStrokeWidth(DisplayUtil.getSize(getContext(), 3));
		mPath = new Path();
		
//		setLayerType(View.LAYER_TYPE_SOFTWARE, null);
	}
	
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		// TODO Auto-generated method stub
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		
		viewWidth  = getMeasuredWidth();
    	viewHeight = getMeasuredHeight();
    	
    	if (destBmp != null && !destBmp.isRecycled()) {
    		destBmp.recycle();
    		destBmp = null;
    	}
    	destBmp = Bitmap.createBitmap(viewWidth, viewHeight, Bitmap.Config.ARGB_8888); 
    	cv = new Canvas(destBmp);
	}
	
	
	@Override
	protected void onDraw(Canvas canvas) {
		// TODO Auto-generated method stub
		super.onDraw(canvas);
		
		
    	getMapState(imageState);
    	
    	
		/*leftEdge = (int) imageState.getLeft();
		rightEdge = (int) imageState.getRight();
		topEdge = (int) imageState.getTop();
		bottomEdge = (int) imageState.getBottom();*/
		
		/*canvas.drawRect(0,0,viewWidth,topEdge, mOutPaint);
		canvas.drawRect(0,topEdge,leftEdge,bottomEdge, mOutPaint);
		canvas.drawRect(rightEdge, topEdge,viewWidth,bottomEdge,mOutPaint);
		canvas.drawRect(0, bottomEdge,viewWidth,viewHeight,mOutPaint);*/
		/*canvas.save();
		mPath.reset();
		canvas.clipRect(0,0,viewWidth,viewHeight);
        mPath.addCircle(startX, startY, edgeRadius, Path.Direction.CCW);
        canvas.clipPath(mPath,Region.Op.DIFFERENCE);
        canvas.drawColor(0x80000000);
        canvas.drawCircle(startX, startY, edgeRadius, mInPaint);
        canvas.restore();*/
		/*canvas.drawColor(0x80000000);
		mInPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
		canvas.drawCircle(startX, startY, edgeRadius, mInPaint);*/
		
		cv.save();
		mPath.reset();
		mPath.addCircle(startX, startY, edgeRadius, Path.Direction.CCW); 
		cv.clipPath(mPath,Region.Op.DIFFERENCE); //裁剪区域 
		cv.drawColor(0x80000000);
		canvas.drawBitmap(destBmp, 0, 0, null);
		cv.restore();
		canvas.drawCircle(startX, startY, edgeRadius, mInPaint);
	}
	
	private void getMapState(ImageState imageState) {
		int length = DisplayUtil.getSize(getContext(), 652);
		int left = (viewWidth - length) / 2;
		int right = left + length;
		int top = (viewHeight - length) / 2;
		int bottom = top + length;
		imageState.setLeft(left); 
		imageState.setTop(top); 
		imageState.setRight(right); 
		imageState.setBottom(bottom);
		edgeRadius = length / 2;
		startX = left + edgeRadius;
		startY = top + edgeRadius;
	}

}
