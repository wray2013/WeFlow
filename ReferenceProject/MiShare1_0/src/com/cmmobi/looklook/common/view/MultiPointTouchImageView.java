package com.cmmobi.looklook.common.view;

import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.FloatMath;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.LinearInterpolator;
import android.view.animation.Transformation;
import android.widget.ImageView;

import com.baidu.mapapi.utils.DistanceUtil;
import com.cmmobi.looklook.common.listener.DiaryPagerTouchInterface;
import com.cmmobi.looklook.common.listener.DiaryTouchInterface;

/**
 * Copyright (C) ......
 * All rights reserved.
 * @package com.cmmobi.looklook.common.view
 * @filename SquareView.java
 * @summary 支持手势的ImageView
 * @author Lanhai
 * @date 2013-11-28
 * @version 1.0
 */
public class MultiPointTouchImageView extends ImageView implements OnTouchListener, DiaryPagerTouchInterface {
	
	private static final String TAG = "Touch";
	
//	private final static int MAX_WIDTH = 2048;
//	private final static int MAX_HEIGHT = 2048;
	
	// These matrices will be used to move and zoom image
	private Matrix mMatrix = new Matrix();
	private Matrix mSavedMatrix = new Matrix();
	// We can be in one of these 3 states
	static final int NONE = 0;
	static final int DRAG = 1;
	static final int ZOOM = 2;
	private int mode = NONE;

	private long lastZoomOutMoveTime;
	private long lastZoomInMoveTime;

	private PointF mPtStart = new PointF();
	private PointF mPCenter = new PointF();
	private float startDist = 1f;
	private float lastScale = 1f;
	// private float totalScale = 1f;
	private long lastDoubleClickTime;
	// 点击事件
	private long clickDownTime;
	// 变为全屏的时间戳
	private long fullScreenTime;

	private final static float MAX_SCALE = 3f;
	// private final static float NOMAL_SCALSE = 1f;
	private final static float MIN_SCALE = 0.5f;

	// 自动恢复标志
	private boolean mAutoCenterFlag = false;
	// 图片还原标志
	private boolean mBeOriginal = true;
	
	// 双击标志
	private boolean isDoubleClicked = false;

	// ViewPager移动标志
	private boolean intercept = false;
	
	// 是否是在全屏界面
	private boolean isFullScreen = false;
	
	private DiaryTouchInterface listener;
	
    public MultiPointTouchImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }
 
    public MultiPointTouchImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
 
    public MultiPointTouchImageView(Context context) {
        super(context);
    }
 
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
	{
//		Log.d(TAG,"onMeasure isFullScreen = " + isFullScreen + " this = " + this.toString());
		if (!isFullScreen) {
			setMeasuredDimension(getDefaultSize(0, widthMeasureSpec), getDefaultSize(0, heightMeasureSpec));
			 
	        int childWidthSize = getMeasuredWidth();
	        
	        heightMeasureSpec = widthMeasureSpec = MeasureSpec.makeMeasureSpec(childWidthSize, MeasureSpec.EXACTLY);
	        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
	        setCenter(null);
//	        Log.v(TAG, "childWidthSize = " + childWidthSize);
//	        Log.v(TAG, "widthMeasureSpec = " + MeasureSpec.getSize(widthMeasureSpec));
		} else {
			setMeasuredDimension(getDefaultSize(0, widthMeasureSpec), getDefaultSize(0, heightMeasureSpec));
			super.onMeasure(widthMeasureSpec, heightMeasureSpec);
			setCenter(null);
		}
        
	}

	@Override
	public void setImageDrawable(Drawable drawable)
	{
		super.setImageDrawable(drawable);
	}
	
	public void setOnClickListener(DiaryTouchInterface listener)
	{
		setIntercept(false);
		this.listener = listener;
	}
	
	public void setMove(boolean b)
	{
		Log.v(TAG, "set intercept move = " + b);
//		intercept = b;
		isFullScreen = b;
		if(b)
		{
			fullScreenTime = System.currentTimeMillis();
			setOnTouchListener(this);
			setCenter(null);
		}
		else
		{
			setCenter(mMatrix);
			setOnTouchListener(new OnTouchListener()
			{
				
				@Override
				public boolean onTouch(View v, MotionEvent event)
				{
					return false;
				}
			});
		}
	}
	
	private void setTouchable(boolean b) {
		if (b) {
			setOnTouchListener(this);
		} else {
			setCenter(mMatrix);
			setOnTouchListener(new OnTouchListener()
			{
				
				@Override
				public boolean onTouch(View v, MotionEvent event)
				{
					return false;
				}
			});
		}
	}
	
	/**
	 * 回收内存
	 */
	public void recycle() {
		if (getDrawable() == null)
		{
			return;
		}
		Bitmap bitmap = ((BitmapDrawable) getDrawable()).getBitmap();
		setImageBitmap(null);
		if (!bitmap.isRecycled())
		{
			bitmap.recycle();
			bitmap = null;
		}
	}
	
	public void setCenter(int width, int height)
	{
		int measureWidth = MeasureSpec.makeMeasureSpec(width, MeasureSpec.EXACTLY);
		int measureheight = MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY);
		Log.d(TAG,"setCenter width = " + width + " height = " + height + " measurewidth = " + measureWidth + " measureheight = " + measureheight);
		measure(measureWidth, measureheight);
		setCenter(null);
	}
	
	// 恢复最初状态
	private void setCenter(Matrix matrix) {
		Log.d(TAG,"setCenter in matrix = " + matrix + " scaleType = " + getScaleType());
		if(ScaleType.MATRIX==getScaleType())
		{
			mBeOriginal = true;
			if(matrix != null)
			{
				matrix.reset();
			}
			Bitmap bitmap = null;
			Drawable d = getDrawable();
			if (d != null)
			{
				bitmap = ((BitmapDrawable)d).getBitmap();
			}
			Log.d(TAG,"setCenter bitmap = " + bitmap);
			if(bitmap != null)
			{
				int width = bitmap.getWidth();
				int height = bitmap.getHeight();
//				int disWidth = ((Activity) getContext()).getWindowManager().getDefaultDisplay().getWidth();
				int disWidth = getMeasuredWidth();
				
	//			int disHeight = ((Activity) getContext()).getWindowManager().getDefaultDisplay().getHeight();
				int disHeight = getMeasuredHeight();
				Log.v(TAG, "setCenter bitmap width = " + width);
				Log.v(TAG, "setCenter bitmap height = " + height);
				Log.v(TAG, "setCenter dis Width = " + disWidth);
				Log.v(TAG, "setCenter dis Height = " + disHeight);
				
				float scaleWidth = ((float)disWidth) / ((float)width);
				float scaleHeight = ((float)disHeight) / ((float)height);
				float scale = scaleWidth > scaleHeight ? scaleHeight : scaleWidth;
				
				if (disWidth == 0 || disHeight == 0) {
					scale = ((float)(((Activity) getContext()).getWindowManager().getDefaultDisplay().getWidth())/(float)width);
					disWidth = ((Activity) getContext()).getWindowManager().getDefaultDisplay().getWidth();
					disHeight = ((Activity) getContext()).getWindowManager().getDefaultDisplay().getWidth();
				}
				
				if(matrix == null)
				{
					matrix = new Matrix();
				}
				matrix.postScale(scale, scale);
				float scaleX = (disWidth - width * scale) / 2;
				float scaleY = (disHeight - height * scale) / 2;
				matrix.postTranslate(scaleX, scaleY);
				Log.v(TAG, "setCenter bitmap height = " + height);
				
				Log.v(TAG, "setCenter scale = " + scale + " scaleX = " + scaleX + " scaleY = " + scaleY);
				setImageMatrix(matrix);
				
			}
		}

	}
	
	// 对bigmap处理 保证不超过MAX_WIDTH和MAX_HEIGHT
//	private Bitmap modifyImage(Bitmap bitmap)
//	{
//		if(bitmap == null)
//		{
//			return null;
//		}
//		int width = bitmap.getWidth();
//		int height = bitmap.getHeight();
//		
//		int disWidth = ((Activity) getContext()).getWindowManager().getDefaultDisplay().getWidth();
//		int disHeight = ((Activity) getContext()).getWindowManager().getDefaultDisplay().getHeight();
//		
//		int maxWidth = MAX_WIDTH > disWidth ? disWidth : MAX_WIDTH;
//		int maxHeight = MAX_HEIGHT > disHeight ? disHeight : MAX_HEIGHT;
//		
//		Log.v(TAG, "bitmap width = " + width);
//		Log.v(TAG, "bitmap height = " + height);
//		float scaleWidth = ((float)maxWidth) / ((float)width);
//		float scaleHeight = ((float)maxHeight) / ((float)height);
//		float scale = scaleWidth > scaleHeight ? scaleHeight : scaleWidth;
//		
//		if(scale < 1f)
//		{
//			Matrix matrix = new Matrix();
//			matrix.postScale(scale, scale);
//			bitmap = Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, true); 
//		}
//		return bitmap;
//	}
	
	@Override
	protected void onDraw(Canvas canvas)
	{
		try
		{
			super.onDraw(canvas);
		}
		catch (Exception e)
		{
			Log.v(TAG, "onDraw() Canvas: trying to use a recycled bitmap");
		}
	}
	
//	这个bug是Android系统原因   所以第一种方式是：
//	 
//	修改frameworks\base\core\jni\android_view_MotionEvent.cpp的android_view_MotionEvent_nativeGetAxisValue方法
//	 
//	注释掉
//	[java]
//	if (!validatePointerIndex(env, pointerIndex, pointerCount)) {return 0;｝ 
//	改完后需重新编译整个系统，然后替换lib库，重新编译整个系统一般需要半个多小时，这个方法就比较麻烦了
//	 
//	第二种方法是：捕获IllegalArgumentException（非法参数异常）异常 即如
//	[java] 
//	private float spacing(MotionEvent event) { 
//	try { 
//	        x = event.getX(0) - event.getX(1); 
//	        y = event.getY(0) - event.getY(1); 
//	    } catch (IllegalArgumentException e) { 
//	        e.printStackTrace(); 
//	    } 
//	}
	
	@Override
	public boolean onTouch(View v, MotionEvent event)
	{
		Log.v(TAG, "ontouch = " + event.getAction());
		try{
		ImageView view = (ImageView) v;
//		dumpEvent(event);

		switch (event.getAction() & MotionEvent.ACTION_MASK)
		{
		case MotionEvent.ACTION_DOWN:
			Log.v(TAG, "ACTION_DOWN");
			mMatrix.set(view.getImageMatrix());
			mSavedMatrix.set(mMatrix);
			mPtStart.set(event.getX(), event.getY());
			mode = DRAG;
			clickDownTime = System.currentTimeMillis();
			setIntercept(false);
			break;
		case MotionEvent.ACTION_POINTER_DOWN:
			// 手指距离大于10 开启缩放模式
			startDist = calcDistacne(event);
			Log.d(TAG,"ACTION_POINTER_DOWN startDist = " + startDist + " count = " + event.getPointerCount());
//			if (startDist > 10f)
			{
				mSavedMatrix.set(mMatrix);
				setMidPoint(event, mPCenter);
				mode = ZOOM;
			}
			setIntercept(false);
			break;
		case MotionEvent.ACTION_UP:
			Log.v(TAG, "ACTION_UP");
			mode = NONE;
			setIntercept(false);
			
			long curTime = System.currentTimeMillis();
			if (curTime - fullScreenTime < 250) {
				break;
			}
			
			if (curTime - lastDoubleClickTime < 250 && mode != ZOOM && 1 == event.getPointerCount())
			{
				// 如果是双击恢复最初状态
				// matrix.reset();
				// if(v instanceof ImageView)
				// setCenter((ImageView)v,matrix);
				// matrix.postScale(1, 1);
				if(mBeOriginal)
				{
					isDoubleClicked = true;
					startAnimation(new ExtAnimation(event.getX(),event.getY()));
					setMax(mMatrix, event.getX(), event.getY());
//					setTouchable(false);
				}
				else
				{
					setCenter(mMatrix);
				}
				if(listener != null)
				{
					listener.onDoubleClick();
				}
				break;
			}
			lastDoubleClickTime = System.currentTimeMillis();
			if (mAutoCenterFlag)
			{
				mAutoCenterFlag = false;
				setCenter(mMatrix);
			}
			// 点击事件
			long time = System.currentTimeMillis();
			if(time - clickDownTime < 150)
			{
				if(listener != null)
				{
					listener.onClick();
				}
			}
			clickDownTime = 0l;
			break;
		case MotionEvent.ACTION_POINTER_UP:
			Log.v(TAG, "ACTION_POINTER_UP");
			if (mAutoCenterFlag)
			{
				mAutoCenterFlag = false;
				setCenter(mMatrix);
			}
			setIntercept(false);
			break;
		case MotionEvent.ACTION_MOVE:
			Log.v(TAG, "ACTION_MOVE");
			if (mode == DRAG)
			{
				Log.v(TAG, "DRAG");
				mMatrix.set(mSavedMatrix);
				float base = getBaseScale();
				float current = getScale(mMatrix);

				if (current <= (base + Float.MIN_VALUE * 2))
				{
					Log.d(TAG,"current = " + current + " base = " + base);
					return true;
				}
				
				setIntercept(true);

				// 移动后过滤
//				int flag = isIntercept(mMatrix);
//
//				if (flag < 0)
//				{
//					// 图片只能左移
//					Log.v(TAG, "move x:" + (event.getX() - mPtStart.x));
//					if (event.getX() - mPtStart.x <= 0)
//					{
//						Log.v(TAG, "move left");
//						setIntercept(true);
//						mMatrix.postTranslate(event.getX() - mPtStart.x, event.getY() - mPtStart.y);
//					}
//					else
//					{
//						Log.v(TAG, "no move");
//						mMatrix.postTranslate(0, event.getY() - mPtStart.y);
//						setIntercept(false);
//					}
//				}
//				else if (flag > 0)
//				{
//					// 图片只能右移
//					Log.v(TAG, "move x:" + (event.getX() - mPtStart.x));
//					if (event.getX() - mPtStart.x >= 0)
//					{
//						Log.v(TAG, "move right");
//						setIntercept(true);
//						mMatrix.postTranslate(event.getX() - mPtStart.x, event.getY() - mPtStart.y);
//					}
//					else
//					{
//						Log.v(TAG, "no move");
//						mMatrix.postTranslate(0, event.getY() - mPtStart.y);
//						setIntercept(false);
//					}
//				}
//				else
//				{
//					// 图片移动
//					Log.v(TAG, "move every where");
//					setIntercept(true);
//					mMatrix.postTranslate(event.getX() - mPtStart.x, event.getY() - mPtStart.y);
//				}
				
				// 移动中过滤
				Bitmap bitmap = ((BitmapDrawable) view.getDrawable()).getBitmap();
				if(bitmap == null)
				{
					return true;
				}
				int width = bitmap.getWidth();
				int disWidth = ((Activity) view.getContext()).getWindowManager().getDefaultDisplay().getWidth();
				// int disWidth = imageView.getMeasuredWidth();
				int disHeight = getHeight();
				float yoffset = 0;
				float yPos = getY(mMatrix);
				if (yPos >= 0 && yPos + width * getScale(mMatrix) <= disHeight) {
					yoffset = 0;
				} else {
					yoffset = event.getY() - mPtStart.y;
					if (yoffset > 0) {
						if (yPos + yoffset > 0) {
							yoffset = -1 * yPos;
						}
					} else {
						if (yPos + yoffset + width * getScale(mMatrix) < disHeight) {
							yoffset = disHeight - width * getScale(mMatrix) - yPos;
						}
					}
				}

				// 移动距离
				float x = getX(mMatrix);
				// 当前图像宽度
				float x1 = width * getScale(mMatrix) - disWidth;
				
				if(event.getX() - mPtStart.x < 0)
				{//左移
					if(Math.abs(event.getX() - mPtStart.x) < x1+x)
					{
						Log.v(TAG, "move left");
						setIntercept(true);
						mMatrix.postTranslate(event.getX() - mPtStart.x, yoffset);
					}
					else
					{
						Log.v(TAG, "no move");
						mMatrix.postTranslate(-x1-x, yoffset);
						setIntercept(false);
					}
				}
				else if(event.getX() - mPtStart.x > 0)
				{//右移
					if(Math.abs(event.getX() - mPtStart.x) < Math.abs(x))
					{
						Log.v(TAG, "move right");
						setIntercept(true);
						mMatrix.postTranslate(event.getX() - mPtStart.x, yoffset);
					}
					else
					{
						Log.v(TAG, "no move");
						mMatrix.postTranslate(-x, yoffset);
						setIntercept(false);
					}
				}
				else
				{
					// 图片移动
					Log.v(TAG, "move every where");
					setIntercept(true);
					mMatrix.postTranslate(event.getX() - mPtStart.x, yoffset/*event.getY() - mPtStart.y*/);
				}
				
				Log.d(TAG,"matrix " + mMatrix + " width = " + width * getScale(mMatrix));
				
			}
			else if (mode == ZOOM)
			{
				Log.v(TAG, "ZOOM");
				setIntercept(true);
				mBeOriginal = false;
				float newDist = calcDistacne(event);
				if (newDist > 10f)
				{
					mMatrix.set(mSavedMatrix);
					float scale = newDist / startDist;
					float base = getBaseScale();
					float current = getScale(mMatrix);

					if (scale > 1f)
					{
						// 放大
						if ((current * scale) > (MAX_SCALE * base))
						{
							scale = MAX_SCALE * base / current;
							Log.v(TAG, "scale = " + scale);
							mMatrix.postScale(scale, scale, mPCenter.x, mPCenter.y);
							break;
						}
						// 大于1倍时全屏
						if ((current * scale) > (base))
						{
							lastZoomOutMoveTime = System.currentTimeMillis();
//							LocalBroadcastManager.getInstance(v.getContext()).sendBroadcast(
//									new Intent(DiaryPreviewActivity.INTENT_ACTION_SHOW));
						}

						if (lastScale > 1f && System.currentTimeMillis() - lastZoomOutMoveTime < 200)
						{
							lastZoomOutMoveTime = System.currentTimeMillis();
							lastScale = scale;
							Log.v(TAG, "scale = " + scale);
							mMatrix.postScale(scale, scale, mPCenter.x, mPCenter.y);
							break;
						}
					}
					else if (scale < 1f)
					{
						// 缩小
						if ((current * scale) < (MIN_SCALE * base))
						{
							scale = MIN_SCALE * base / current;
							Log.v(TAG, "scale = " + scale);
							mMatrix.postScale(scale, scale, mPCenter.x, mPCenter.y);
							mAutoCenterFlag = true;
							break;
						}
						// 小于1倍时恢复
						if ((current * scale) <= (base))
						{
							mAutoCenterFlag = true;
							lastZoomInMoveTime = System.currentTimeMillis();
//							LocalBroadcastManager.getInstance(v.getContext()).sendBroadcast(
//									new Intent(DiaryPreviewActivity.INTENT_ACTION_HIDDEN));
						}
						if (lastScale < 1f && System.currentTimeMillis() - lastZoomInMoveTime < 200)
						{
							lastZoomInMoveTime = System.currentTimeMillis();
							lastScale = scale;
							Log.v(TAG, "scale = " + scale);
							mMatrix.postScale(scale, scale, mPCenter.x, mPCenter.y);
							break;
						}
					}
					lastScale = scale;
					Log.v(TAG, "scale = " + scale);
					mMatrix.postScale(scale, scale, mPCenter.x, mPCenter.y);
				}
			}
			break;
//		case MotionEvent.ACTION_CANCEL:
//			Log.v(TAG, "ACTION_CANCEL");
//			mAutoCenterFlag = false;
//			setCenter(mMatrix);
//			setIntercept(false);
//			break;
		}

		Log.d(TAG,"isDoubleCliked = " + isDoubleClicked);
		if (!isDoubleClicked) {
			view.setImageMatrix(mMatrix);
		} else {
			isDoubleClicked = false;
		}
		
//		} catch (IllegalArgumentException e) { 
		} catch (Exception e) {
//			setIntercept(false);
	        e.printStackTrace();
//	        intercept = false;
	    } 
		
		return true;
	}

	@Override
	public boolean isIntercept()
	{
//		Log.v(TAG, "intercept = " + intercept);
		return intercept;
	}

	@Override
	public void setIntercept(boolean intercept)
	{
//		Log.v(TAG, "set intercept = " + intercept);
		this.intercept = intercept;
	}

//	private void dumpEvent(MotionEvent event)
//	{
//		String names[] = { "DOWN", "UP", "MOVE", "CANCEL", "OUTSIDE", "POINTER_DOWN", "POINTER_UP", "7?", "8?", "9?" };
//		StringBuilder sb = new StringBuilder();
//		int action = event.getAction();
//		int actionCode = action & MotionEvent.ACTION_MASK;
//		sb.append("event ACTION_").append(names[actionCode]);
//		if (actionCode == MotionEvent.ACTION_POINTER_DOWN || actionCode == MotionEvent.ACTION_POINTER_UP)
//		{
//			sb.append("(pid ").append(action >> MotionEvent.ACTION_POINTER_ID_SHIFT);
//			sb.append(")");
//		}
//		sb.append("[");
//		for (int i = 0; i < event.getPointerCount(); i++)
//		{
//			sb.append("#").append(i);
//			sb.append("(pid ").append(event.getPointerId(i));
//			sb.append(")=").append((int) event.getX(i));
//			sb.append(",").append((int) event.getY(i));
//			if (i + 1 < event.getPointerCount())
//				sb.append(";");
//		}
//		sb.append("]");
//	}

	// 计算两指的距离
	private float calcDistacne(MotionEvent event)
	{
		if (event.getPointerCount() > 1)
		{
			float x = event.getX(0) - event.getX(1);
			float y = event.getY(0) - event.getY(1);
			return FloatMath.sqrt(x * x + y * y);
		}
		return 0;
	}

	// 计算中点
	private void setMidPoint(MotionEvent event, PointF point)
	{
		float x = event.getX(0) + event.getX(1);
		float y = event.getY(0) + event.getY(1);
		point.set(x / 2, y / 2);
	}

	// 返回scale
	private float getScale(Matrix matrix)
	{
		float p[] = new float[9];
		matrix.getValues(p);
		return p[Matrix.MSCALE_X];
	}

	// 返回横坐标
	private float getX(Matrix matrix)
	{
		float p[] = new float[9];
		matrix.getValues(p);
		return p[Matrix.MTRANS_X];
	}
	
	private float getY(Matrix matrix) {
		float p[] = new float[9];
		matrix.getValues(p);
		return p[Matrix.MTRANS_Y];
	}
	
	private class ExtAnimation extends Animation
	{
		private float posX;
		private float posY;
		public ExtAnimation(float posX , float posY) {
			this.posX = posX;
			this.posY = posY;
		}
	 
	 
		@Override
        public void initialize(int width, int height, int parentWidth, int parentHeight) {
            super.initialize(width, height, parentWidth, parentHeight);
            setDuration(500);
            setFillAfter(true);
            setInterpolator(new LinearInterpolator());
            setAnimationListener(new AnimationListener() {
				
				@Override
				public void onAnimationStart(Animation arg0) {
					// TODO Auto-generated method stub
					
				}
				
				@Override
				public void onAnimationRepeat(Animation arg0) {
					// TODO Auto-generated method stub
					
				}
				
				@Override
				public void onAnimationEnd(Animation arg0) {
					// TODO Auto-generated method stub
//					setTouchable(true);
//					setImageMatrix(mMatrix);
					MultiPointTouchImageView.this.clearAnimation();
					if (isFullScreen) {
						setImageMatrix(mMatrix);
						Log.d(TAG,"onAnimationEnd in mMatrix = " + mMatrix);
					}
				}
			});
        }
	 
		// 最重要的方法
		@Override
	    protected void applyTransformation(float interpolatedTime, Transformation t) {
			if (isFullScreen) {
				Matrix matrix = t.getMatrix();
				float scale = (MAX_SCALE - 1) * interpolatedTime + 1;
				matrix.postScale(scale, scale,posX ,posY);
				Log.d(TAG,"interpolatedTime = " + interpolatedTime + " matrix = " + matrix + " posY = " + posY);
			}
		}
		
		
	}

	// 放到最大比例
	private void setMax(Matrix matrix, float eventX, float eventY)
	{
		mBeOriginal = false;
		Bitmap bitmap = ((BitmapDrawable) getDrawable()).getBitmap();
		if (ScaleType.MATRIX == getScaleType() && bitmap != null)
		{
			matrix.reset();
			int width = bitmap.getWidth();
			int height = bitmap.getHeight();
//			int disWidth = ((Activity) getContext()).getWindowManager().getDefaultDisplay().getWidth();
			int disWidth = getMeasuredWidth();
//			int disHeight = ((Activity) getContext()).getWindowManager().getDefaultDisplay().getHeight();
			int disHeight = getMeasuredHeight();
			float scaleWidth = ((float) disWidth) / ((float) width);
			float scaleHeight = ((float) disHeight) / ((float) height);
			float scale = (scaleWidth > scaleHeight ? scaleHeight : scaleWidth);
			Log.d(TAG,"setMax scale = " + scale);
			matrix.postScale(scale, scale);
			float scaleX = (disWidth - width * scale) / 2;
			float scaleY = (disHeight - height * scale) / 2;
			matrix.postTranslate(scaleX, scaleY);
			matrix.postScale(MAX_SCALE, MAX_SCALE, eventX, eventY);
		}
		
	}
	
	// 获取基本放大比例
	private float getBaseScale()
	{
		Bitmap bitmap = ((BitmapDrawable) getDrawable()).getBitmap();
		if (ScaleType.MATRIX == getScaleType() && bitmap != null)
		{
			int width = bitmap.getWidth();
			int height = bitmap.getHeight();
//			int disWidth = ((Activity) getContext()).getWindowManager().getDefaultDisplay().getWidth();
			int disWidth = getMeasuredWidth();
//			int disHeight = ((Activity) getContext()).getWindowManager().getDefaultDisplay().getHeight();
			int disHeight = getMeasuredHeight();
			float scaleWidth = ((float) disWidth) / ((float) width);
			float scaleHeight = ((float) disHeight) / ((float) height);
			float scale = scaleWidth > scaleHeight ? scaleHeight : scaleWidth;
			return scale;
		}
		return 1f;
	}

	// 计算图像的移动情况 <0 只能左移，>0 只能右移，0 可以随意移动
	private int isIntercept(Matrix matrix)
	{
		Bitmap bitmap = ((BitmapDrawable) getDrawable()).getBitmap();
		if(bitmap != null)
		{
			int width = bitmap.getWidth();
//			int disWidth = ((Activity) getContext()).getWindowManager().getDefaultDisplay().getWidth();
			int disWidth = getMeasuredWidth();
	
			// 移动距离
			float x = getX(matrix);
			// 当前图像宽度
			float x1 = width * getScale(matrix) - disWidth;
	
			Log.v(TAG, "x = " + x + "x1 = " + x1);
	
			if (x > 0)
			{
				// 只能左移
				return -1;
			}
			else if (Math.abs(x) > Math.abs(x1))
			{
				// 只能右移
				return 1;
			}
			else
			{
				return 0;
			}
		}
		return 0;
	}
	
	private boolean isForbidMove = false;

	@Override
	public boolean isForbidMove() {
		// TODO Auto-generated method stub
		return isForbidMove/* && isViewOutOfScreen()*/;
	}

	@Override
	public void setForbidMovable(boolean movable) {
		// TODO Auto-generated method stub
		isForbidMove = movable;
		
	}
	
	private boolean isViewOutOfScreen() {
		int[] location = new int[2];
		getLocationOnScreen(location);
		int y = location[1];
		Log.d(TAG,"isViewOutOfScreen y = " + y);
		return y < 50;
	}
}