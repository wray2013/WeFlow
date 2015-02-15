package com.cmmobi.looklook.common.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Rect;
import android.graphics.drawable.NinePatchDrawable;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.ImageView;

import com.cmmobi.looklook.R;

public class VideoMontageView extends ImageView{
	private final String TAG = this.getClass().getSimpleName();
	private Bitmap leftBorderBmp = BitmapFactory.decodeResource(getResources(),R.drawable.caijian_normal_zuo);
	private Bitmap rightBorderBmp = BitmapFactory.decodeResource(getResources(), R.drawable.caijian_normal_you);
	private Bitmap cursorBmp = BitmapFactory.decodeResource(getResources(), R.drawable.caijian_huakuai);
	private Paint paint = new Paint();
	private boolean isMeasured = false;
	private int thumb1X = 0;
	private int thumb2X = 0;
	private NinePatchDrawable bg;
	private int thumbWidth;
	private int cursorWidth;
	private int thumb1Value, thumb2Value;
	private int cursorPos = 0;
	private VideoMontageChangeListener scl;
	private int selectedThumb;
	private int seekWidth;
	private boolean isMontageDisabled = false;
	
	private double totalTime = 0;
	private int shortestLen = 0;
	
	private final int INIT_STATE = 0;
	private final int MONTAGE_STATE = 1;
	private final int SEEK_STATE = 2;
	private int state = INIT_STATE;
	private int startPos = 0;
	private int endPos = 0;
	private int montageWidth = 0;
	private int lineWidth = 0;
	public static final int SHORTEST_TIME = 8;
	
	public VideoMontageView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}
	
	public int getSelectThumb() {
		return selectedThumb;
	}
	
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		if (getMeasuredHeight() > 0 && !isMeasured) {
			init();
			isMeasured = true;
		}
	}
	
	private void init() {
		thumbWidth = leftBorderBmp.getWidth();
		cursorWidth = cursorBmp.getWidth();
		cursorPos = thumbWidth;
		paint.setStyle(Style.FILL);
		DisplayMetrics dm = getResources().getDisplayMetrics();
		lineWidth = (int) (2 * dm.density);
		Log.d(TAG,"init desity = " + dm.density);
		if (thumb1X <= 0) {
			thumb1X = 0;
		}
		
		if (thumb2X <=0) {
			thumb2X = getMeasuredWidth() - thumbWidth ;
		} else {
			thumb2X += thumbWidth;
		}
		startPos = 0;
		endPos = getMeasuredWidth() - thumbWidth;
		bg =  (NinePatchDrawable) getResources().getDrawable(R.drawable.caijian_normal_zhong);
		montageWidth = getMeasuredWidth() - 2 * thumbWidth;
		seekWidth = montageWidth - cursorWidth;
		shortestLen = (int) ((montageWidth / totalTime) * SHORTEST_TIME);
		if (scl != null) {
			scl.noticeWidth(montageWidth, seekWidth);
		}
		change2Bitmap();
		
		invalidate();
		Log.d(TAG,"width = " + getMeasuredWidth() + " height = " + getMeasuredHeight());
	}
	
	public void setVideoMontageChangeListener(VideoMontageChangeListener l) {
		this.scl = l;
	}
	
	public int getThumbWidth() {
		return thumbWidth;
	}
	
	public int getMontageWidth() {
		return montageWidth;
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
			Log.d(TAG,"onDraw height = " + getHeight() + " left = " + left + " right = " + right);
//		    bg.setBounds(left, 0, right, getHeight());
//		    bg.draw(canvas);
			canvas.drawRect(left, 0, right, lineWidth, paint);
			canvas.drawRect(left, getHeight() - lineWidth, right, getHeight(), paint);
//		    canvas.drawLine(left, 0, right, 0, paint);
//		    canvas.drawLine(left, getHeight(), right, getHeight(), paint);
		}
		
		if (state != MONTAGE_STATE || (scl != null && scl.isMediaPlaying())) {
			canvas.drawBitmap(cursorBmp,cursorPos, 0,paint);
		}
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		int mx = (int) event.getX();
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			selectedThumb = 0;
			if (!isMontageDisabled) {
				if (mx >=  thumb1X && mx <= thumb1X + thumbWidth) {
					selectedThumb = 1;
				} else if (mx >= thumb2X && mx <= thumb2X + thumbWidth){
					selectedThumb = 2;
				}
			}
			if (state != MONTAGE_STATE || (scl != null && scl.isMediaPlaying())) {
				if (mx >= cursorPos && mx <= cursorPos + cursorWidth) {
					selectedThumb = 3;
				}
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
			
			if (selectedThumb == 1 || selectedThumb == 2) {
				state = MONTAGE_STATE;
				change2Bitmap();
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
			} else if (selectedThumb == 3) {
				cursorPos = mx;
				if (cursorPos < thumbWidth) {
					cursorPos = thumbWidth;
				} else if (cursorPos > getWidth() - thumbWidth - cursorWidth) {
					cursorPos = getWidth() - thumbWidth - cursorWidth;
				}
			}
			break;
		case MotionEvent.ACTION_UP:
			state = INIT_STATE;
			if (selectedThumb == 3) {
				scl.seekBarChange(cursorPos - thumbWidth);
			}
			
			if (thumb1X == startPos && thumb2X == endPos) {
				state = INIT_STATE;
				change2Bitmap();
				scl.setMontageEnable(false);
				invalidate();
			} else {
				Log.d(TAG,"seekBarCropUp MotionEvent.ACTION_UP selectedThumb " + selectedThumb);
				if (selectedThumb == 1 || selectedThumb == 2) {
					scl.setMontageEnable(true);
				}
			}
			
			break;
		}
		
		if (selectedThumb == 1 || selectedThumb == 2) {
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
		} else if (selectedThumb == 3) {
			invalidate();
		}
		return true;
	}
	
	private void calculateThumbValue(){
		thumb1Value = (thumb1X);
		thumb2Value = (thumb2X - thumbWidth);
	}
	
	public void reset(double totalTime) {
		this.totalTime = totalTime;
		shortestLen = (int) ((montageWidth / totalTime) * SHORTEST_TIME);
		cursorPos = thumbWidth;
		thumb1X = 0;
		
		thumb2X = getMeasuredWidth() - thumbWidth ;
		
		state = INIT_STATE;
		scl.setMontageEnable(false);
		change2Bitmap();
		invalidate();
	}
	
	public interface VideoMontageChangeListener{
		void videoMontageValueChanged(int Thumb1Value,int Thumb2Value);
		void noticeWidth(int montageWidth,int seekWidth);
		void seekBarCropUp(int thumb1Value,int thumb2Value);
		void setMontageEnable(boolean enable);
		void seekBarChange(int curPos);
		boolean isMediaPlaying();
	}
	
	private void change2Bitmap() {
		if (state == MONTAGE_STATE) {
			leftBorderBmp = BitmapFactory.decodeResource(getResources(),R.drawable.caijian_pressed_zuo);
			rightBorderBmp = BitmapFactory.decodeResource(getResources(), R.drawable.caijian_pressed_you);
			bg =  (NinePatchDrawable) getResources().getDrawable(R.drawable.caijian_pressed_zhong);
			paint.setColor(0xffec4b2e);
		} else {
			leftBorderBmp = BitmapFactory.decodeResource(getResources(),R.drawable.caijian_normal_zuo);
			rightBorderBmp = BitmapFactory.decodeResource(getResources(), R.drawable.caijian_normal_you);
			bg =  (NinePatchDrawable) getResources().getDrawable(R.drawable.caijian_normal_zhong);
			paint.setColor(0xff000000);
		}
	}
	
	public void setTotalTime(double totalTime) {
		this.totalTime = totalTime;
	}
	
	public void setMontageDisabled(boolean flag) {
		isMontageDisabled = flag;
	}
	
	public void setCurTime(double curTime) {
		cursorPos = (int) (curTime * seekWidth / totalTime + thumbWidth);
		Log.d(TAG,"curPos = " + cursorPos + " curTime = " + curTime);
		invalidate();
	}
}
