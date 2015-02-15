package com.cmmobi.looklook.common.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.NinePatchDrawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.ImageView;

import com.cmmobi.looklook.R;

public class VideoMontageView extends ImageView{
	private final String TAG = this.getClass().getSimpleName();
	private Bitmap leftBorderBmp = BitmapFactory.decodeResource(getResources(),R.drawable.jianji_kuang_normal_zuo);
	private Bitmap rightBorderBmp = BitmapFactory.decodeResource(getResources(), R.drawable.jianji_kuang_normal_you);
	private Paint paint = new Paint();
	private boolean isMeasured = false;
	private int thumb1X = 0;
	private int thumb2X = 0;
	private int marginLeft = 0;
	private NinePatchDrawable bg;
	private int thumbWidth;
	private int thumb1Value, thumb2Value;
	private VideoMontageChangeListener scl;
	private int selectedThumb;
	private int totalWidth;
	
	private double totalTime = 0;
	private int shortestLen = 0;
	
	public VideoMontageView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	public VideoMontageView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
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
	
	private void init() {
		marginLeft = getLeft();
		thumbWidth = leftBorderBmp.getWidth();
		if (thumb1X <= 0) {
			thumb1X = 0;
		}
		if (thumb2X <=0) {
			thumb2X = getMeasuredWidth() - thumbWidth ;
			scl.setThumbValues(thumb1X, thumb2X - thumbWidth);
		} else {
			thumb2X += thumbWidth;
		}
		Log.d("=AAA=","marginLeft = " + marginLeft);
		bg =  (NinePatchDrawable) getResources().getDrawable(R.drawable.jianji_kuang_normal_zhong);
		totalWidth = getMeasuredWidth() - 2 * thumbWidth;
		shortestLen = (int) ((totalWidth / totalTime) * 5);
		change2Bitmap();
		invalidate();
	}
	
	public void setVideoMontageChangeListener(VideoMontageChangeListener l,int x1,int x2) {
		this.scl = l;
		thumb1X = x1;
		if (x2 > 0) {
			thumb2X = x2 + thumbWidth;
		}
	}
	
	public int getThumbWidth() {
		return thumbWidth;
	}
	
	public int getTotalWidth() {
		return totalWidth;
	}

	@Override
	protected void onDraw(Canvas canvas) {
		// TODO Auto-generated method stub
		super.onDraw(canvas);
		if (thumb1X <= 0) {
			thumb1X = 0;
		}
		if (thumb2X <= 0) {
			thumb2X = getMeasuredWidth() - thumbWidth ;
		}
		canvas.drawBitmap(leftBorderBmp, thumb1X, 0, paint);
		canvas.drawBitmap(rightBorderBmp, thumb2X, 0,paint);
		if (bg != null) {
			int left = thumb1X + thumbWidth;
			int right = thumb2X;
		    bg.setBounds(left, 0, right, getHeight());
		    Log.d(TAG,"left = " + left + " right = " + right + " thumb1X = " + thumb1X + " thumbWidth = " + thumbWidth + " thumb2X = " + thumb2X);
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
		
		if(thumb1X > getWidth() ) {
			thumb1X = getWidth();
		}
		
		if(thumb2X > getWidth() - thumbWidth) {
			thumb2X = getWidth() - thumbWidth;
		}
		
		invalidate();
		if(scl !=null){
			calculateThumbValue();
			scl.videoMontageValueChanged(thumb1Value,thumb2Value);
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
	
	public interface VideoMontageChangeListener{
		void videoMontageValueChanged(int Thumb1Value,int Thumb2Value);
		void setThumbValues(int thumb1Value,int thumb2Value);
		void seekBarCropUp(int thumb1Value,int thumb2Value);
	}
	
	private void change2Bitmap() {
		leftBorderBmp = BitmapFactory.decodeResource(getResources(),R.drawable.jianji_kuang_pressed_zuo);
		rightBorderBmp = BitmapFactory.decodeResource(getResources(), R.drawable.jianji_kuang_pressed_you);
		bg =  (NinePatchDrawable) getResources().getDrawable(R.drawable.jianji_kuang_pressed_zhong);
	}
	
	public void setTotalTime(double totalTime) {
		this.totalTime = totalTime;
	}
}
