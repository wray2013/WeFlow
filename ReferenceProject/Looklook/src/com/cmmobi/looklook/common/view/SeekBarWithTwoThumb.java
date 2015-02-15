package com.cmmobi.looklook.common.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.NinePatchDrawable;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.ImageView;

import com.cmmobi.looklook.R;

public class SeekBarWithTwoThumb extends ImageView {

	private String TAG = this.getClass().getSimpleName();
	private Bitmap thumbLeft = BitmapFactory.decodeResource(getResources(),R.drawable.yinpin_jianji_zuo);
	private Bitmap thumbRight = BitmapFactory.decodeResource(getResources(),R.drawable.yinpin_jianji_you);
	private int thumb1X, thumb2X;
	private int thumb1Value, thumb2Value;
	private int thumbY;
	private Paint paint = new Paint();
	private int selectedThumb;
	private int thumbWidth;
	private int totalWidth;
	private int marginLeft = 0;
	private int marginRight = 0;
	private NinePatchDrawable bg;
	private boolean isMeasured = false;
	private double totalTime = 0;
	private int shortestLen = 0;
	
	private SeekBarChangeListener scl;
	
	
	public SeekBarWithTwoThumb(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public SeekBarWithTwoThumb(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public SeekBarWithTwoThumb(Context context) {
		super(context);
	}
	
	public int getSelectThumb() {
		return selectedThumb;
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		Log.d("=AAA=","SeekBarWithTwoThumb onMeasure in widthMeasureSpec = " + widthMeasureSpec + " heightMeasureSpec = " + heightMeasureSpec);
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		if (getMeasuredHeight() > 0 && !isMeasured) {
			init();
			isMeasured = true;
		}
	}
	
	

	@Override
	protected void onLayout(boolean changed, int left, int top, int right,
			int bottom) {
		// TODO Auto-generated method stub
		super.onLayout(changed, left, top, right, bottom);
		
		Log.d("=AAA=","SeekBarWithTwoThumb onLayout in + left = " + getLeft());
	}

	@Override
	public void onWindowFocusChanged(boolean hasWindowFocus) {
		// TODO Auto-generated method stub
		super.onWindowFocusChanged(hasWindowFocus);
		Log.d("=AAA=","SeekBarWithTwoThumb onWindowFocusChanged in");
	}

	private void init() {
		if (thumbLeft.getHeight() > getMeasuredHeight())
			getLayoutParams().height = thumbLeft.getHeight();

		thumbY = (getMeasuredHeight() / 2) - (thumbLeft.getHeight() / 2);
		
		thumbWidth = thumbLeft.getWidth();
		totalWidth = getMeasuredWidth() - 2 * thumbWidth;
		shortestLen = (int) ((totalWidth / totalTime) * 5);
		if (thumb1X <= 0) {
			thumb1X = 0;
		}
		if (thumb2X <=0) {
			thumb2X = getMeasuredWidth() - thumbWidth ;
			scl.setThumbValues(thumb1X, thumb2X - thumbWidth);
		} else {
			thumb2X += thumbWidth;
		}
		marginLeft = getLeft();
		Log.d("=AAA=","View Height =" + getMeasuredHeight() + " Thumb Height :"+ thumbLeft.getHeight() + " thumbY = " + thumbY + " totalWidth = " + totalWidth);
		Log.d("=AAA=","marginLeft = " + marginLeft);
		bg =  (NinePatchDrawable) getResources().getDrawable(R.drawable.yinpin_jianji_2);
		
		
		invalidate();
	}
	public void setSeekBarChangeListener(SeekBarChangeListener scl,int x1,int x2){
		this.scl = scl;
		thumb1X = x1;
		if (x2 > 0) {
			thumb2X = x2 + thumbWidth;
		}
	}
	
	public int getMarginLeft() {
		return getLeft();
	}
	
	public int getMarginRight() {
		return marginRight;
	}
	
	public int getThumbWidth() {
		return thumbWidth;
	}
	
	public int getTotalWidth() {
		return totalWidth;
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		if (thumb1X <= 0) {
			thumb1X = 0;
		}
		if (thumb2X <= 0) {
			thumb2X = getMeasuredWidth() - thumbWidth ;
		}
		canvas.drawBitmap(thumbLeft, thumb1X, thumbY,paint);
		canvas.drawBitmap(thumbRight, thumb2X, thumbY,paint);
		if (bg != null) {
			int left = thumb1X + thumbWidth;
			int right = thumb2X;
		    bg.setBounds(left, 0, right, getHeight());
		    Log.d("=AAA=","left = " + left + " right = " + right + " thumb1X = " + thumb1X + " thumbWidth = " + thumbWidth + " thumb2X = " + thumb2X);
		    bg.draw(canvas);
		}
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (totalTime < 6) {
			return true;
		}
		int mx = (int) event.getX();
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			if (mx <=  thumb1X || Math.abs(mx - thumb1X) < Math.abs(thumb2X - mx)) {
				selectedThumb = 1;
			} else {
				selectedThumb = 2;
			}
			if (selectedThumb == 1) {
				thumb1X = mx;
				if (thumb1X + thumbWidth + shortestLen> thumb2X) {
					thumb1X = thumb2X - thumbWidth - shortestLen;
				}
			} else if (selectedThumb == 2) {
				thumb2X = mx;
				if (thumb2X < thumb1X + thumbWidth + shortestLen) {
					thumb2X = thumb1X + thumbWidth + shortestLen;
				}
			}
			break;
		case MotionEvent.ACTION_MOVE:
			if (selectedThumb == 1) {
				thumb1X = mx;
				if (thumb1X + thumbWidth + shortestLen> thumb2X) {
					thumb1X = thumb2X - thumbWidth - shortestLen;
				}
			} else if (selectedThumb == 2) {
				thumb2X = mx;
				if (thumb2X < thumb1X + thumbWidth + shortestLen) {
					thumb2X = thumb1X + thumbWidth + shortestLen;
				}
			}
			break;
		case MotionEvent.ACTION_UP:
			selectedThumb = 0;
			break;
		}
		
		if(thumb1X < 0)
			thumb1X = 0;
		
		if(thumb2X < 0)
			thumb2X = 0;
		
		if(thumb1X > getWidth() )
			thumb1X =getWidth() ;
		
		if(thumb2X > getWidth() - thumbWidth)
			thumb2X =getWidth() - thumbWidth;
		
		invalidate();
		if(scl !=null){
			calculateThumbValue();
			scl.SeekBarValueChanged(thumb1Value,thumb2Value);
			if (MotionEvent.ACTION_UP == event.getAction()) {
				scl.seekBarCropUp(thumb1Value, thumb2Value);
			}
		}
		return true;
	}
	
	private void calculateThumbValue(){
		thumb1Value = (thumb1X);
		thumb2Value = (thumb2X - thumbWidth);
	}
	
	public interface SeekBarChangeListener{
		void SeekBarValueChanged(int Thumb1Value,int Thumb2Value);
		void setThumbValues(int thumb1Value,int thumb2Value);
		void seekBarCropUp(int thumb1Value,int thumb2Value);
	}
	
	public void setTotalTime(double totalTime) {
		this.totalTime = totalTime;
	}
}
