package com.cmmobi.looklook.common.imagecrop;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.ImageView;

public class CropImageView2 extends ImageView {
	private int top = 0;
	private int left = 0;
	private int right = 0;
	private int bottom = 0;
	private Rect mRect = null;
	
	private int viewWidth = 0;
	private int viewHeight = 0;
	
	private double viewRatio = 0;
	
	private int imageWidth = 0;
	private int imageHeight = 0;
	private double imageRatio = 0;
	
	private Paint mInPaint = null;
	private Paint mOutPaint = null;
	private Paint trianglePaint = null;
	private int strokeWidth = 0;
	private int strokeHeight = 0;
	
	private int myPosX = 0;
	private int myPosY = 0;
	private int mLastX = 0;
	private int mLastY = 0;
	
	private int mState = 0;
	private int mDownState = 0;
	private final int UNKNOWN = 0;
	private final int LEFT_UP = 1;
	private final int LEFT_DOWN = 2;
	private final int RIGHT_UP = 3;
	private final int RIGHT_DOWN = 4;
	private final int LEFT_EDGE = 5;
	private final int RIGHT_EDGE = 6;
	private final int TOP_EDGE = 7;
	private final int BOTTOM_EDGE = 8;
	private final int CENTER_INSIDE = 9;
	
	private int IMAGE_LEFT = 0;
	private int IMAGE_RIGHT = 0;
	private int IMAGE_TOP = 0;
	private int IMAGE_BOTTOM = 0;
	
	private int drawWidth = 0;
	private int drawHeight = 0;
	
	private final int MIN_SIDE_LENGTH = 90;
	private final int MAX_ACCURATE_LENGTH = 30;

	private final String TAG = "CropImageView2";
	private boolean isMeasured = false;

	public CropImageView2(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		init();
	}

	public CropImageView2(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
		init();
	}

	public CropImageView2(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		init();
	}
	
	@Override
	public void setImageBitmap(Bitmap bm) {
		// TODO Auto-generated method stub
		super.setImageBitmap(bm);
		
		if (bm != null) {
			imageWidth = bm.getWidth();
			imageHeight = bm.getHeight();
			imageRatio = ((double) imageWidth) / imageHeight;
		}
		
		if (viewWidth > 0) {
			if(imageRatio > viewRatio) {
			  IMAGE_LEFT = 0;
			  drawHeight = (int) ((viewRatio/imageRatio) * viewHeight);
			  drawWidth = viewWidth;
			  IMAGE_TOP = (viewHeight - drawHeight)/2;
			  IMAGE_RIGHT = IMAGE_LEFT + drawWidth;
			  IMAGE_BOTTOM = IMAGE_TOP + drawHeight;
			} else {
			  IMAGE_TOP = 0;
			  drawWidth = (int) ((imageRatio/viewRatio) * viewWidth);
			  drawHeight = viewHeight;
			  IMAGE_LEFT = (viewWidth - drawWidth)/2;
			  IMAGE_RIGHT = IMAGE_LEFT + drawWidth;
			  IMAGE_BOTTOM = IMAGE_TOP + drawHeight;
			}
			
			int sideLength = drawWidth < drawHeight ? drawWidth:drawHeight;
			left = IMAGE_LEFT + (drawWidth - sideLength / 2) / 2;
		    right = left +  sideLength / 2;
		    top = IMAGE_TOP + (drawHeight - sideLength / 2) / 2;
		    bottom = top +  sideLength / 2;
		    
		}
	}
	
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		Log.d(TAG,"CropImageView2 onMeasure in widthMeasureSpec = " + widthMeasureSpec + " heightMeasureSpec = " + heightMeasureSpec + " isMeasured = " + isMeasured);
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		if (getMeasuredHeight() > 0 && !isMeasured) {
			isMeasured = true;
			viewWidth  = getMeasuredWidth();
	    	viewHeight = getMeasuredHeight();
//	    	int sideLength = viewWidth < viewHeight ? viewWidth:viewHeight;
	    	viewRatio = ((double)viewWidth)/viewHeight;
//	    	Log.d(TAG,"CropImageView2 onMeasure width = " + viewWidth + " height = " + viewHeight + " ratio = " + viewRatio);
	    	 
//	    	sideLength /= 2;
//	    	left = (viewWidth - sideLength) / 2;
//	    	right = left + sideLength;
//	    	top = (viewHeight - sideLength) / 2;
//	    	bottom = top + sideLength;
	    	setCropRect(mRect);
	    	
	    	if (imageWidth > 0) {
	    		if(imageRatio > viewRatio) {
	  			  IMAGE_LEFT = 0;
	  			  drawHeight = (int) ((viewRatio/imageRatio) * viewHeight);
	  			  drawWidth = viewWidth;
	  			  IMAGE_TOP = (viewHeight - drawHeight)/2;
	  			  IMAGE_RIGHT = IMAGE_LEFT + drawWidth;
	  			  IMAGE_BOTTOM = IMAGE_TOP + drawHeight;
	  			} else {
	  			  IMAGE_TOP = 0;
	  			  drawWidth = (int) ((imageRatio/viewRatio) * viewWidth);
	  			  drawHeight = viewHeight;
	  			  IMAGE_LEFT = (viewWidth - drawWidth)/2;
	  			  IMAGE_RIGHT = IMAGE_LEFT + drawWidth;
	  			  IMAGE_BOTTOM = IMAGE_TOP + drawHeight;
	  			}
	  			
	  			int sideLength = drawWidth < drawHeight ? drawWidth:drawHeight;
	  			left = IMAGE_LEFT + (drawWidth - sideLength / 2) / 2;
			    right = left +  sideLength / 2;
			    top = IMAGE_TOP + (drawHeight - sideLength / 2) / 2;
			    bottom = top +  sideLength / 2;
	    	}
		}
	}
	
	private void init() {
		mInPaint = new Paint();
		mInPaint.setColor(0xffffffff);
		mInPaint.setStrokeWidth(3);
		mInPaint.setAntiAlias(true);
		mOutPaint = new Paint();
		mOutPaint.setColor(0x80000000);
		trianglePaint = new Paint();
		trianglePaint.setAntiAlias(true);
		trianglePaint.setColor(0x60ffffff);
		/*ViewTreeObserver vto2 = getViewTreeObserver();   
		vto2.addOnGlobalLayoutListener(new OnGlobalLayoutListener() { 
			@Override   
			public void onGlobalLayout() { 
				viewWidth  = getMeasuredWidth();
		    	viewHeight = getMeasuredHeight();
		    	int sideLength = viewWidth < viewHeight ? viewWidth:viewHeight;
		    	sideLength /= 2;
		    	left = (viewWidth - sideLength) / 2;
		    	right = left + sideLength;
		    	top = (viewHeight - sideLength) / 2;
		    	bottom = top + sideLength;
		    	outRect.set(0, 0, viewWidth, viewHeight);
				getViewTreeObserver().removeGlobalOnLayoutListener(this);         
			}   
		});
		inRect.set(left,top,right,bottom);*/
	}
	
	

	@Override
	protected void onDraw(Canvas canvas) {
		// TODO Auto-generated method stub
		super.onDraw(canvas);
		
//		Log.d(TAG,"left = " + left + " right = " + right + " top = " + top + " bottom = " 
//				+ bottom + " width = " + viewWidth + " height = " + viewHeight);
		strokeWidth = (right - left) / 3;
		strokeHeight = (bottom - top) / 3;
		
		canvas.drawRect(0,0,viewWidth,top, mOutPaint);
		canvas.drawRect(0,top,left,bottom, mOutPaint);
		canvas.drawRect(right, top,viewWidth,bottom,mOutPaint);
		canvas.drawRect(0, bottom,viewWidth,viewHeight,mOutPaint);
		
		canvas.drawLine(left, top, right, top, mInPaint);
		canvas.drawLine(left, top, left, bottom, mInPaint);
		canvas.drawLine(left, bottom, right, bottom, mInPaint);
		canvas.drawLine(right, top, right, bottom, mInPaint);
		
		canvas.drawLine(left, top + strokeHeight, right, top + strokeHeight, mInPaint);
		canvas.drawLine(left, top + 2 * strokeHeight, right, top + 2 * strokeHeight, mInPaint);
		canvas .drawLine(left + strokeWidth, top, left + strokeWidth, bottom, mInPaint);
		canvas.drawLine(left + 2 * strokeWidth, top, left + 2 * strokeWidth, bottom, mInPaint);
		
		Path path = new Path();
		path.moveTo(right, bottom);
		path.lineTo(right, bottom - strokeHeight / 3);
		path.lineTo(right - strokeWidth / 3, bottom);
		path.lineTo(right, bottom);
		canvas.drawPath(path, trianglePaint);
		
		Rect outRect = new Rect();
		getDrawingRect(outRect);
		Log.d(TAG,"width = " + (right - left) + " height = " + (bottom - top) + " left = " + getImageMatrix());
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// TODO Auto-generated method stub
		myPosX = (int) event.getX();
		myPosY = (int) event.getY();
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			mLastX = myPosX;
			mLastY = myPosY;
			calcuteState(mLastX, mLastY);
			break;
		case MotionEvent.ACTION_UP:
			mState = UNKNOWN;
			break;
		case MotionEvent.ACTION_MOVE:
			processMoveEvent();
			mLastX = myPosX;
			mLastY = myPosY;
			invalidate();
			break;
		}
		return true;
	}
	
	private void calcuteState(int x,int y) {
		if (left - x > MAX_ACCURATE_LENGTH || x - right > MAX_ACCURATE_LENGTH || top - y > MAX_ACCURATE_LENGTH || y - bottom > MAX_ACCURATE_LENGTH) {
			mState = UNKNOWN;
		} else if (Math.abs(x - left) <= MAX_ACCURATE_LENGTH && Math.abs(y - top) <= MAX_ACCURATE_LENGTH) {
			mState = LEFT_UP;
		} else if (Math.abs(x - right) <= MAX_ACCURATE_LENGTH && Math.abs(y - top) <= MAX_ACCURATE_LENGTH) {
			mState = RIGHT_UP;
		} else if (Math.abs(x - left) <= MAX_ACCURATE_LENGTH && Math.abs(y - bottom) <= MAX_ACCURATE_LENGTH) {
			mState = LEFT_DOWN;
		} else if (Math.abs(x - right) <= MAX_ACCURATE_LENGTH && Math.abs(y - bottom) <= MAX_ACCURATE_LENGTH) {
			mState = RIGHT_DOWN;
		} else if (Math.abs(y - top) <= MAX_ACCURATE_LENGTH && (x - left) > MAX_ACCURATE_LENGTH && (right - x ) > MAX_ACCURATE_LENGTH) {
			mState = TOP_EDGE;
		} else if (Math.abs(x - left) <= MAX_ACCURATE_LENGTH && (y - top) > MAX_ACCURATE_LENGTH && (bottom - y) > MAX_ACCURATE_LENGTH) {
			mState = LEFT_EDGE;
		} else if (Math.abs(x - right) <= MAX_ACCURATE_LENGTH && (y - top) > MAX_ACCURATE_LENGTH && (bottom - y) > MAX_ACCURATE_LENGTH) {
			mState = RIGHT_EDGE;
		} else if (Math.abs(y - bottom) <= MAX_ACCURATE_LENGTH && (x - left) > MAX_ACCURATE_LENGTH && (right - x) > MAX_ACCURATE_LENGTH) {
			mState = BOTTOM_EDGE;
		} else {
			mState = CENTER_INSIDE;
		}
		
		mDownState = mState;
		
		Log.d(TAG,"mState = " + mState + " x = " + x + " y = " + y + " left = " + left 
				+ " right = " + right + " top = " + top + " bottom = " + bottom );
	}
	
	private void processMoveEvent() {
		Log.d(TAG,"processMoveEvent mState = " + mState);
		switch(mState) {
		case UNKNOWN:
			break;
		case LEFT_UP:
			processLeftUpMove();
			break;
		case LEFT_DOWN:
			processLeftDownMove();
			break;
		case RIGHT_UP:
			processRightUpMove();
			break;
		case RIGHT_DOWN:
			processRightDownMove();
			break;
		case LEFT_EDGE:
			processLeftEdgeMove();
			break;
		case RIGHT_EDGE:
			processRightEdgeMove();
			break;
		case TOP_EDGE:
			processTopEdgeMove();
			break;
		case BOTTOM_EDGE:
			processBottomEdgeMove();
			break;
		case CENTER_INSIDE:
			processCenterMove();
			break;
		}
	}
	
	private void processCenterMove() {
		int newLeft = left + myPosX - mLastX;
		int newRight = right + myPosX - mLastX;
		int newTop = top + myPosY - mLastY;
		int newBottom = bottom + myPosY - mLastY;
		if (newLeft < IMAGE_LEFT) {
			newLeft = IMAGE_LEFT;
		}
		
		if (newRight > IMAGE_RIGHT) {
			newRight = IMAGE_RIGHT;
		}
		
		if (newTop < IMAGE_TOP) {
			newTop = IMAGE_TOP;
		}
		
		if (newBottom > IMAGE_BOTTOM) {
			newBottom = IMAGE_BOTTOM;
		}
		
		if (newLeft > IMAGE_LEFT && newRight < IMAGE_RIGHT) {
			left = newLeft;
			right = newRight;
		} 
		
		if (newTop > IMAGE_TOP && newBottom < IMAGE_BOTTOM) {
			top = newTop;
			bottom = newBottom;
		}
	}
	
	private void processTopEdgeMove() {
		int newTop = top + myPosY - mLastY;
		
		if (newTop < IMAGE_TOP) {
			newTop = IMAGE_TOP;
		}
		if (newTop > IMAGE_TOP && bottom - newTop > MIN_SIDE_LENGTH) {
			top = newTop;
		} else if (bottom - newTop <= MIN_SIDE_LENGTH) {
			if (myPosY - top > MAX_ACCURATE_LENGTH) {
				mState = CENTER_INSIDE;
			}
		}
	}
	
	private void processLeftEdgeMove() {
		int newLeft = left + myPosX - mLastX;
		
		if (newLeft < IMAGE_LEFT) {
			newLeft = IMAGE_LEFT;
		}
		if (newLeft > IMAGE_LEFT && right - newLeft > MIN_SIDE_LENGTH) {
			left = newLeft;
		} else if (right - newLeft <= MIN_SIDE_LENGTH) {
			if (myPosX - left > MAX_ACCURATE_LENGTH) {
				mState = CENTER_INSIDE;
			}
		}
	}
	
	private void processRightEdgeMove() {
		int newRight = right + myPosX - mLastX;
		
		if (newRight > IMAGE_RIGHT) {
			newRight = IMAGE_RIGHT;
		}
		
		if (newRight < IMAGE_RIGHT && newRight - left > MIN_SIDE_LENGTH) {
			right = newRight;
		} else if (newRight - left <= MIN_SIDE_LENGTH) {
			if (right - myPosX > MAX_ACCURATE_LENGTH) {
				mState = CENTER_INSIDE;
			}
		}
	}
	
	private void processBottomEdgeMove() {
		int newBottom = bottom + myPosY - mLastY;
		
		if (newBottom > IMAGE_BOTTOM) {
			newBottom = IMAGE_BOTTOM;
		}
		if (newBottom < IMAGE_BOTTOM && newBottom - top > MIN_SIDE_LENGTH) {
			bottom = newBottom;
		} else if (newBottom - top <= MIN_SIDE_LENGTH) {
			if (bottom - myPosY > MAX_ACCURATE_LENGTH) {
				mState = CENTER_INSIDE;
			}
		}
	}
	
	private void processLeftUpMove() {
		int newLeft = left + myPosX - mLastX;
		int newTop = top + myPosY - mLastY;
		
		if (newLeft < IMAGE_LEFT) {
			newLeft = IMAGE_LEFT;
		}
		
		if (newTop < IMAGE_TOP) {
			newTop = IMAGE_TOP;
		}
		
		boolean isFlag1 = false;
		boolean isFlag2 = false;
		
		if (newLeft > IMAGE_LEFT && right - newLeft > MIN_SIDE_LENGTH) {
			left = newLeft;
		} else if (right - newLeft <= MIN_SIDE_LENGTH) {
			isFlag1 = true;
		}
		
		if (newTop > IMAGE_TOP && bottom - newTop > MIN_SIDE_LENGTH) {
			top = newTop;
		} else if (bottom - newTop <= MIN_SIDE_LENGTH) {
			isFlag2 = true;
		}
		
		if (isFlag1 && isFlag2) {
			mState = CENTER_INSIDE;
		} else if (isFlag1) {
			if (right - myPosX < MAX_ACCURATE_LENGTH) {
				mState = RIGHT_UP;
			}
		} else if (isFlag2) {
			if (bottom - myPosY < MAX_ACCURATE_LENGTH) {
				mState = LEFT_DOWN;
			}
		}
	}
	
	private void processRightUpMove() {
		int newRight = right + myPosX - mLastX;
		int newTop = top + myPosY - mLastY;
		
		if (newRight > IMAGE_RIGHT) {
			newRight = IMAGE_RIGHT;
		}
		
		if (newTop < IMAGE_TOP) {
			newTop = IMAGE_TOP;
		}
		
		boolean isFlag1 = false;
		boolean isFlag2 = false;
		
		if (newRight < IMAGE_RIGHT && newRight - left > MIN_SIDE_LENGTH) {
			right = newRight;
		} else if (newRight - left <= MIN_SIDE_LENGTH) {
			isFlag1 = true;
		}
		
		if (newTop > IMAGE_TOP && bottom - newTop > MIN_SIDE_LENGTH) {
			top = newTop;
		} else if (bottom - newTop <= MIN_SIDE_LENGTH) {
			isFlag2 = true;
		}
		
		if (isFlag1 && isFlag2) {
			mState = CENTER_INSIDE;
		} else if (isFlag1) {
			if (myPosX - left < MAX_ACCURATE_LENGTH) {
				mState = LEFT_UP;
			}
		} else if (isFlag2) {
			if (bottom - myPosY < MAX_ACCURATE_LENGTH) {
				mState = RIGHT_DOWN;
			}
		}
	}
	
	private void processLeftDownMove() {
		int newLeft = left + myPosX - mLastX;
		int newBottom = bottom + myPosY - mLastY;
		boolean isFlag1 = false;
		boolean isFlag2 = false;
		
		if (newLeft < IMAGE_LEFT) {
			newLeft = IMAGE_LEFT;
		}
		
		if (newBottom > IMAGE_BOTTOM) {
			newBottom = IMAGE_BOTTOM;
		}
		
		if (newLeft > IMAGE_LEFT && right - newLeft > MIN_SIDE_LENGTH) {
			left = newLeft;
		} else if (right - newLeft <= MIN_SIDE_LENGTH) {
			isFlag1 = true;
		}
		
		if (newBottom < IMAGE_BOTTOM && newBottom - top > MIN_SIDE_LENGTH) {
			bottom = newBottom;
		} else if (newBottom - top <= MIN_SIDE_LENGTH) {
			isFlag2 = true;
		}
		
		if (isFlag1 && isFlag2) {
			mState = CENTER_INSIDE;
		} else if (isFlag1) {
			if (right - myPosX < MAX_ACCURATE_LENGTH) {
				mState = RIGHT_DOWN;
			}
		} else if (isFlag2) {
			if (myPosY - top < MAX_ACCURATE_LENGTH) {
				mState = LEFT_UP;
			}
		}
	}
	
	private void processRightDownMove() {
		int newRight = right + myPosX - mLastX;
		int newBottom = bottom + myPosY - mLastY;
		
		boolean isFlag1 = false;
		boolean isFlag2 = false;
		
		if (newRight > IMAGE_RIGHT) {
			newRight = IMAGE_RIGHT;
		}
		
		if (newBottom > IMAGE_BOTTOM) {
			newBottom = IMAGE_BOTTOM;
		}
		if (newRight < IMAGE_RIGHT && newRight - left > MIN_SIDE_LENGTH) {
			right = newRight;
		} else if (newRight - left <= MIN_SIDE_LENGTH) {
			isFlag1 = true;
		}
		
		if (newBottom < IMAGE_BOTTOM && newBottom - top > MIN_SIDE_LENGTH) {
			bottom = newBottom;
		} else if ( newBottom - top <= MIN_SIDE_LENGTH) {
			isFlag2 = true;
		}
		
		if (isFlag1 && isFlag2) {
			mState = CENTER_INSIDE;
		} else if (isFlag1) {
			if (myPosX - left <= MAX_ACCURATE_LENGTH) {
				mState = LEFT_DOWN;
			}
		} else if (isFlag2) {
			if (myPosY - top <= MAX_ACCURATE_LENGTH) {
				mState = RIGHT_UP;
			}
		}
	}
	
	public Rect getCropRect() {
		return new Rect(left, top, right, bottom);
	}
	
	public void setCropRect(Rect rect) {
		if (rect == null) {
			return;
		}
		mRect = rect;
		if (rect.width() == 0 || rect.height() == 0) {
			return;
		}
		left= rect.left;
		right = rect.right;
		top = rect.top;
		bottom = rect.bottom;
	}
}
