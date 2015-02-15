package com.cmmobi.looklook.common.listener;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.graphics.drawable.BitmapDrawable;
import android.support.v4.content.LocalBroadcastManager;
import android.util.FloatMath;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;

import com.cmmobi.looklook.activity.DiaryDetailActivity;

/**
 * @author wuxiang
 * @email wuxiang@cmmobi.com
 * @date 2013-8-5
 */
public class MulitPointTouchListener implements OnTouchListener, DiaryPagerTouchInterface
{

	private static final String TAG = "Touch";
	// These matrices will be used to move and zoom image
	private Matrix matrix = new Matrix();
	private Matrix savedMatrix = new Matrix();
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

	private final static float MAX_SCALE = 3f;
	// private final static float NOMAL_SCALSE = 1f;
	private final static float MIN_SCALE = 0.5f;

	// 自动恢复标志
	private boolean mAutoCenterFlag = false;
	// 图片还原标志
	private boolean mBeOriginal = true;

	// ViewPager移动标志
	private boolean intercept = true;
	

	@Override
	public boolean onTouch(View v, MotionEvent event)
	{

		ImageView view = (ImageView) v;
		dumpEvent(event);

		switch (event.getAction() & MotionEvent.ACTION_MASK)
		{
		case MotionEvent.ACTION_DOWN:
			Log.v(TAG, "ACTION_DOWN");
			matrix.set(view.getImageMatrix());
			savedMatrix.set(matrix);
			mPtStart.set(event.getX(), event.getY());
			mode = DRAG;
			setIntercept(false);
			break;
		case MotionEvent.ACTION_POINTER_DOWN:
			Log.v(TAG, "ACTION_POINTER_DOWN");
			// 手指距离大于10 开启缩放模式
			startDist = calcDistacne(event);
			if (startDist > 10f)
			{
				savedMatrix.set(matrix);
				setMidPoint(event, mPCenter);
				mode = ZOOM;
			}
			setIntercept(false);
			break;
		case MotionEvent.ACTION_UP:
			Log.v(TAG, "ACTION_UP");
			mode = NONE;
			setIntercept(false);
			
			if (System.currentTimeMillis() - lastDoubleClickTime < 200 && mode != ZOOM && 1 == event.getPointerCount())
			{
				// 如果是双击恢复最初状态
				// matrix.reset();
				// if(v instanceof ImageView)
				// setCenter((ImageView)v,matrix);
				// matrix.postScale(1, 1);
				if(mBeOriginal)
				{
					setMax(view, matrix, event.getX(), event.getY());
				}
				else
				{
					setCenter(view, matrix);
				}
				break;
			}
			lastDoubleClickTime = System.currentTimeMillis();
			if (mAutoCenterFlag)
			{
				mAutoCenterFlag = false;
				setCenter(view, matrix);
			}
			break;
		case MotionEvent.ACTION_POINTER_UP:
			Log.v(TAG, "ACTION_POINTER_UP");
			if (mAutoCenterFlag)
			{
				mAutoCenterFlag = false;
				setCenter(view, matrix);
			}
			setIntercept(false);
			break;
		case MotionEvent.ACTION_MOVE:
			Log.v(TAG, "ACTION_MOVE");
			if (mode == DRAG)
			{
				Log.v(TAG, "DRAG");
				matrix.set(savedMatrix);
				float base = getBaseScale(view);
				float current = getScale(matrix);

				if (current <= base)
				{
					return true;
				}
				
				setIntercept(true);

				// 移动后过滤
				int flag = isIntercept(view, matrix);

				if (flag < 0)
				{
					// 图片只能左移
					Log.v(TAG, "move x:" + (event.getX() - mPtStart.x));
					if (event.getX() - mPtStart.x <= 0)
					{
						Log.v(TAG, "move left");
						setIntercept(true);
						matrix.postTranslate(event.getX() - mPtStart.x, event.getY() - mPtStart.y);
					}
					else
					{
						Log.v(TAG, "no move");
						matrix.postTranslate(0, event.getY() - mPtStart.y);
						setIntercept(false);
					}
				}
				else if (flag > 0)
				{
					// 图片只能右移
					Log.v(TAG, "move x:" + (event.getX() - mPtStart.x));
					if (event.getX() - mPtStart.x >= 0)
					{
						Log.v(TAG, "move right");
						setIntercept(true);
						matrix.postTranslate(event.getX() - mPtStart.x, event.getY() - mPtStart.y);
					}
					else
					{
						Log.v(TAG, "no move");
						matrix.postTranslate(0, event.getY() - mPtStart.y);
						setIntercept(false);
					}
				}
				else
				{
					// 图片移动
					Log.v(TAG, "move every where");
					setIntercept(true);
					matrix.postTranslate(event.getX() - mPtStart.x, event.getY() - mPtStart.y);
				}
				
				// 移动中过滤
//				Bitmap bitmap = ((BitmapDrawable) view.getDrawable()).getBitmap();
//				if(bitmap == null)
//				{
//					return true;
//				}
//				int width = bitmap.getWidth();
//				int disWidth = ((Activity) view.getContext()).getWindowManager().getDefaultDisplay().getWidth();
//				// int disWidth = imageView.getMeasuredWidth();
//
//				// 移动距离
//				float x = getX(matrix);
//				// 当前图像宽度
//				float x1 = width * getScale(matrix) - disWidth;
//				
//				if(event.getX() - mPtStart.x < 0)
//				{//左移
//					if(Math.abs(event.getX() - mPtStart.x) < x1+x)
//					{
//						Log.v(TAG, "move left");
//						setIntercept(true);
//						matrix.postTranslate(event.getX() - mPtStart.x, event.getY() - mPtStart.y);
//					}
//					else
//					{
//						Log.v(TAG, "no move");
//						matrix.postTranslate(-x1-x, event.getY() - mPtStart.y);
//						setIntercept(false);
//					}
//				}
//				else if(event.getX() - mPtStart.x > 0)
//				{//右移
//					if(Math.abs(event.getX() - mPtStart.x) < Math.abs(x))
//					{
//						Log.v(TAG, "move right");
//						setIntercept(true);
//						matrix.postTranslate(event.getX() - mPtStart.x, event.getY() - mPtStart.y);
//					}
//					else
//					{
//						Log.v(TAG, "no move");
//						matrix.postTranslate(-x, event.getY() - mPtStart.y);
//						setIntercept(false);
//					}
//				}
//				else
//				{
//					// 图片移动
//					Log.v(TAG, "move every where");
//					setIntercept(true);
//					matrix.postTranslate(event.getX() - mPtStart.x, event.getY() - mPtStart.y);
//				}
				
			}
			else if (mode == ZOOM)
			{
				Log.v(TAG, "ZOOM");
				setIntercept(true);
				mBeOriginal = false;
				float newDist = calcDistacne(event);
				if (newDist > 10f)
				{
					matrix.set(savedMatrix);
					float scale = newDist / startDist;
					float base = getBaseScale(view);
					float current = getScale(matrix);

					if (scale > 1f)
					{
						// 放大
						if ((current * scale) > (MAX_SCALE * base))
						{
							scale = MAX_SCALE * base / current;
							matrix.postScale(scale, scale, mPCenter.x, mPCenter.y);
							break;
						}
						// 大于1倍时全屏
						if ((current * scale) > (base))
						{
							lastZoomOutMoveTime = System.currentTimeMillis();
							LocalBroadcastManager.getInstance(v.getContext()).sendBroadcast(
									new Intent(DiaryDetailActivity.INTENT_ACTION_SHOW));
						}

						if (lastScale > 1f && System.currentTimeMillis() - lastZoomOutMoveTime < 200)
						{
							lastZoomOutMoveTime = System.currentTimeMillis();
							lastScale = scale;
							matrix.postScale(scale, scale, mPCenter.x, mPCenter.y);
							break;
						}
					}
					else if (scale < 1f)
					{
						// 缩小
						if ((current * scale) < (MIN_SCALE * base))
						{
							scale = MIN_SCALE * base / current;
							matrix.postScale(scale, scale, mPCenter.x, mPCenter.y);
							mAutoCenterFlag = true;
							break;
						}
						// 小于1倍时恢复
						if ((current * scale) <= (base))
						{
							mAutoCenterFlag = true;
							lastZoomInMoveTime = System.currentTimeMillis();
							LocalBroadcastManager.getInstance(v.getContext()).sendBroadcast(
									new Intent(DiaryDetailActivity.INTENT_ACTION_HIDDEN));
						}
						if (lastScale < 1f && System.currentTimeMillis() - lastZoomInMoveTime < 200)
						{
							lastZoomInMoveTime = System.currentTimeMillis();
							lastScale = scale;
							matrix.postScale(scale, scale, mPCenter.x, mPCenter.y);
							break;
						}
					}
					lastScale = scale;
					matrix.postScale(scale, scale, mPCenter.x, mPCenter.y);
				}
			}
			break;
		}

		view.setImageMatrix(matrix);
		return true;
	}

	@Override
	public boolean isIntercept()
	{
		return intercept;
	}

	@Override
	public void setIntercept(boolean intercept)
	{
		this.intercept = intercept;
	}

	private void dumpEvent(MotionEvent event)
	{
		String names[] = { "DOWN", "UP", "MOVE", "CANCEL", "OUTSIDE", "POINTER_DOWN", "POINTER_UP", "7?", "8?", "9?" };
		StringBuilder sb = new StringBuilder();
		int action = event.getAction();
		int actionCode = action & MotionEvent.ACTION_MASK;
		sb.append("event ACTION_").append(names[actionCode]);
		if (actionCode == MotionEvent.ACTION_POINTER_DOWN || actionCode == MotionEvent.ACTION_POINTER_UP)
		{
			sb.append("(pid ").append(action >> MotionEvent.ACTION_POINTER_ID_SHIFT);
			sb.append(")");
		}
		sb.append("[");
		for (int i = 0; i < event.getPointerCount(); i++)
		{
			sb.append("#").append(i);
			sb.append("(pid ").append(event.getPointerId(i));
			sb.append(")=").append((int) event.getX(i));
			sb.append(",").append((int) event.getY(i));
			if (i + 1 < event.getPointerCount())
				sb.append(";");
		}
		sb.append("]");
	}

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

	// 恢复最初状态，与webimageview保持一致
	private void setCenter(ImageView imageView, Matrix matrix)
	{
		mBeOriginal = true;
		Bitmap bitmap = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
		if (ScaleType.MATRIX == imageView.getScaleType() && bitmap != null)
		{
			matrix.reset();
			int width = bitmap.getWidth();
			int height = bitmap.getHeight();
			int disWidth = ((Activity) imageView.getContext()).getWindowManager().getDefaultDisplay().getWidth();
			// int disWidth = imageView.getMeasuredWidth();
			int disHeight = ((Activity) imageView.getContext()).getWindowManager().getDefaultDisplay().getHeight();
			// int disHeight = imageView.getMeasuredHeight();
			float scaleWidth = ((float) disWidth) / ((float) width);
			float scaleHeight = ((float) disHeight) / ((float) height);
			float scale = scaleWidth > scaleHeight ? scaleHeight : scaleWidth;
			matrix.postScale(scale, scale);
			float scaleX = (disWidth - width * scale) / 2;
			float scaleY = (disHeight - height * scale) / 2;
			matrix.postTranslate(scaleX, scaleY);
		}
	}

	// 放到最大比例
	private void setMax(ImageView imageView, Matrix matrix, float eventX, float eventY)
	{
		mBeOriginal = false;
		Bitmap bitmap = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
		if (ScaleType.MATRIX == imageView.getScaleType() && bitmap != null)
		{
			matrix.reset();
			int width = bitmap.getWidth();
			int height = bitmap.getHeight();
			int disWidth = ((Activity) imageView.getContext()).getWindowManager().getDefaultDisplay().getWidth();
			// int disWidth = imageView.getMeasuredWidth();
			int disHeight = ((Activity) imageView.getContext()).getWindowManager().getDefaultDisplay().getHeight();
			// int disHeight = imageView.getMeasuredHeight();
			float scaleWidth = ((float) disWidth) / ((float) width);
			float scaleHeight = ((float) disHeight) / ((float) height);
			float scale = (scaleWidth > scaleHeight ? scaleHeight : scaleWidth);
			matrix.postScale(scale, scale);
			float scaleX = (disWidth - width * scale) / 2;
			float scaleY = (disHeight - height * scale) / 2;
			matrix.postTranslate(scaleX, scaleY);
			matrix.postScale(MAX_SCALE, MAX_SCALE, eventX, eventY);
		}
	}
	
	// 获取基本放大比例
	private float getBaseScale(ImageView imageView)
	{
		Bitmap bitmap = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
		if (ScaleType.MATRIX == imageView.getScaleType() && bitmap != null)
		{
			int width = bitmap.getWidth();
			int height = bitmap.getHeight();
			int disWidth = ((Activity) imageView.getContext()).getWindowManager().getDefaultDisplay().getWidth();
			// int disWidth = imageView.getMeasuredWidth();
			int disHeight = ((Activity) imageView.getContext()).getWindowManager().getDefaultDisplay().getHeight();
			// int disHeight = imageView.getMeasuredHeight();
			float scaleWidth = ((float) disWidth) / ((float) width);
			float scaleHeight = ((float) disHeight) / ((float) height);
			float scale = scaleWidth > scaleHeight ? scaleHeight : scaleWidth;
			return scale;
		}
		return 1f;
	}

	// 计算图像的移动情况 <0 只能左移，>0 只能右移，0 可以随意移动
	private int isIntercept(ImageView imageView, Matrix matrix)
	{
		Bitmap bitmap = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
		if(bitmap != null)
		{
			int width = bitmap.getWidth();
			int disWidth = ((Activity) imageView.getContext()).getWindowManager().getDefaultDisplay().getWidth();
			// int disWidth = imageView.getMeasuredWidth();
	
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

}
