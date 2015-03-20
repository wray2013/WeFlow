package com.etoc.weflow.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ViewGroup.LayoutParams;
import android.widget.TextView;

/**
 * 刮刮卡类
 * @author Ray 2015-2-11
 *
 */
public class ScratchTextView extends TextView {

	private float TOUCH_TOLERANCE;
	private Bitmap mBitmap;
	private Canvas mCanvas;
	private Paint mPaint;
	private Path  mPath;
	private float mX,mY;
	
	private static int percent = 70;
	private OnCompletedListener listener = null;

	private boolean isDraw = false;
	private boolean isComplete;
	
	public ScratchTextView(Context context) {
		super(context);

	}
	public ScratchTextView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public ScratchTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		if (isDraw && !isComplete) {
			mCanvas.drawPath(mPath, mPaint);
//			mCanvas.drawPoint(mX, mY, mPaint);
			canvas.drawBitmap(mBitmap, 0, 0, null);
		}
	}
	
	public void setOnCompletedListener(OnCompletedListener l) {
		listener = l;
	}
	
	public interface OnCompletedListener {
		void OnCompleted();
	}

	/**
	 * 设置完成最小百分比
	 * @param p
	 */
	public void setCompletePercent(int p) {
		if (p < 0) {
			p = 0;
		} else if (p > 100) {
			p = 100;
		}
		ScratchTextView.percent = p;
	}
	
	public void resetScratchCard(int width, int height, final int bgDrawable, final int bgColor) {
		clearCanvas();
		if(bgDrawable != 0) {
			Drawable drawable = getResources().getDrawable(bgDrawable);
			Bitmap bm = zoomDrawable(drawable, width, height);//((BitmapDrawable) drawable).getBitmap();
			Paint p = new Paint();
			mCanvas.drawBitmap(bm, 0, 0, p);
		} else {
			mCanvas.drawColor(bgColor);
		}
		isDraw = true;
		isComplete = false;
		invalidate();
	}
	
	public void clearCanvas() {
		Paint paint = new Paint();
		paint.setXfermode(new PorterDuffXfermode(Mode.CLEAR));
		mCanvas.drawPaint(paint);
		paint.setXfermode(new PorterDuffXfermode(Mode.SRC));
	}
	
	/**
	 * 初始化刮刮卡
	 * @param bgDrawable 刮刮卡背景图
	 * @param bgColor 刮刮卡背景色
	 * @param paintStrokeWidth 擦除线宽
	 * @param touchTolerance 画线容差
	 */
	public void initScratchCard(final int bgDrawable, final int bgColor,final int paintStrokeWidth,float touchTolerance) {
		TOUCH_TOLERANCE = touchTolerance;
		mPaint = new Paint();
//		mPaint.setAlpha(0);
//		mPaint.setColor(Color.BLACK);
//		mPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_OUT));
		
		mPaint.setAlpha(0);
		mPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));

		mPaint.setAntiAlias(true);
		mPaint.setDither(true);
		mPaint.setStyle(Paint.Style.STROKE);
		mPaint.setStrokeJoin(Paint.Join.ROUND);
		mPaint.setStrokeCap(Paint.Cap.ROUND);
		mPaint.setStrokeWidth(paintStrokeWidth);

		mPath =  new Path();
		
		LayoutParams lp = getLayoutParams();
		int w = lp.width;
		int h = lp.height;
		
		if(w <= 0 || h <= 0) {
			System.out.println("Have you forgotten to set the w/h in xml? Will use the default size.");
			w = 25;
			h = 20;
		}
		
		mBitmap = Bitmap.createBitmap(w, h, Config.ARGB_8888);
		mCanvas = new Canvas(mBitmap);

		if(bgDrawable != 0) {
			Drawable drawable = getResources().getDrawable(bgDrawable);
			Bitmap bm = zoomDrawable(drawable, w, h);//((BitmapDrawable) drawable).getBitmap();
			Paint p = new Paint();
			mCanvas.drawBitmap(bm, 0, 0, p);
		} else {
			mCanvas.drawColor(bgColor);
		}
		
		isDraw = true;
		isComplete = false;
	}
	
	private static Bitmap/*Drawable*/ zoomDrawable(Drawable drawable, int w, int h) {
        int width = drawable.getIntrinsicWidth();
        int height = drawable.getIntrinsicHeight();
        Bitmap oldbmp = drawableToBitmap(drawable);
        Matrix matrix = new Matrix();
        float scaleWidth = ((float) w / width);
        float scaleHeight = ((float) h / height);
        matrix.postScale(scaleWidth, scaleHeight);
        Bitmap newbmp = Bitmap.createBitmap(oldbmp, 0, 0, width, height,matrix, true);
        return newbmp;
//        return new BitmapDrawable(null, newbmp);
    }   

    private static Bitmap drawableToBitmap(Drawable drawable) {
        int width = drawable.getIntrinsicWidth();
        int height = drawable.getIntrinsicHeight();
        Bitmap.Config config = drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888 : Bitmap.Config.RGB_565;
        Bitmap bitmap = Bitmap.createBitmap(width, height, config);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, width, height);
        drawable.draw(canvas);
        return bitmap;
    }

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (!isDraw) {
			return true;
		}
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			touchDown(event.getX(), event.getY());
			invalidate();
			break;
		case MotionEvent.ACTION_MOVE:
			touchMove(event.getX(), event.getY());
			invalidate();
			break;
		case MotionEvent.ACTION_UP:
			touchUp(event.getX(), event.getY());
			invalidate();
			new Thread(mRunnable).start();
			break;
		default:
			break;
		}
		return true;
	}


	private void touchDown(float x,float y){
		mPath.reset();
		mPath.moveTo(x, y);
		mX = x;
		mY = y;
	}

	private void touchMove(float x,float y){
		float dx = Math.abs(x - mX);
		float dy = Math.abs(y - mY);
		if (dx >= TOUCH_TOLERANCE || dy >= TOUCH_TOLERANCE) {
			mPath.quadTo(mX, mY, (x + mX)/2, (y + mY)/2);
			mX = x;
			mY = y;
		}

	}

	private void touchUp(float x,float y){
		mPath.lineTo(x, y);
		mCanvas.drawPath(mPath, mPaint);
		mPath.reset();
	}

	/**
	 * 统计擦除区域任务
	 */
	private Runnable mRunnable = new Runnable()
	{
		private int[] mPixels;

		@Override
		public void run()
		{

			int w = getWidth();
			int h = getHeight();

			float wipeArea = 0;
			float totalArea = w * h;

			Bitmap bitmap = mBitmap;

			mPixels = new int[w * h];

			/**
			 * 拿到所有的像素信息
			 */
			bitmap.getPixels(mPixels, 0, w, 0, 0, w, h);

			/**
			 * 遍历统计擦除的区域
			 */
			for (int i = 0; i < w; i++)
			{
				for (int j = 0; j < h; j++)
				{
					int index = i + j * w;
					if (mPixels[index] == 0)
					{
						wipeArea++;
					}
				}
			}

			/**
			 * 根据所占百分比，进行一些操作
			 */
			if (wipeArea > 0 && totalArea > 0)
			{
				int percent = (int) (wipeArea * 100 / totalArea);
				Log.e("TAG", percent + "");

				if (percent > ScratchTextView.percent)
				{
					Log.e("TAG", "清除区域达到" + ScratchTextView.percent + "%，下面自动清除");
					isComplete = true;
					if(listener != null) {
						listener.OnCompleted();
					}
					postInvalidate();
				}
			}
		}

	};
}
