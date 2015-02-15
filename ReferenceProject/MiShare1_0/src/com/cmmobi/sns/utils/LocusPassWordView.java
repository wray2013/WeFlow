package com.cmmobi.sns.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import com.cmmobi.looklook.R;

public class LocusPassWordView extends View {
	private float w = 0;
	private float h = 0;

	private boolean isCache = false;
	private Paint mPaint = new Paint();

	private Point[][] mPoints = new Point[3][3];
	private float r = 0;
	private float roundr = 0;
	private List<Point> sPoints = new ArrayList<Point>();
	private boolean checking = false;
	private Bitmap locus_round_1;
	private Bitmap locus_round_2;
	private Bitmap locus_round_3;
	private Bitmap locus_round_4;
	private Bitmap locus_round_5;
	private Bitmap locus_round_6;
	private Bitmap locus_round_7;
	private Bitmap locus_round_8;
	private Bitmap locus_round_9;
	private Bitmap locus_round_click;
	private Bitmap locus_round_click_error;
	private long CLEAR_TIME = 800;
	private int passwordMinLength = 3;
	private boolean isTouch = true;
	private int strokeWidth = 15;
	
	public boolean isChecking = false;
	
	public LocusPassWordView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		initLinePaint();
	}

	public LocusPassWordView(Context context, AttributeSet attrs) {
		super(context, attrs);
		initLinePaint();
	}

	public LocusPassWordView(Context context) {
		super(context);
		initLinePaint();
	}
	
	private void initLinePaint() {
		mPaint.setAntiAlias(true);
		mPaint.setStrokeWidth(strokeWidth);
		mPaint.setStrokeCap(Paint.Cap.ROUND);
	}


	@Override
	public void onDraw(Canvas canvas) {
		if (!isCache) {
			initCache();
		}
		drawToCanvas(canvas);
	}

	private void drawToCanvas(Canvas canvas) {
	
		for (int i = 0; i < mPoints.length; i++) {
			for (int j = 0; j < mPoints[i].length; j++) {
				Point p = mPoints[i][j];
				if (p.state == Point.STATE_CHECK) {
					canvas.drawBitmap(locus_round_click, p.x - r, p.y - r,
							mPaint);
				} else if (p.state == Point.STATE_CHECK_ERROR) {
					canvas.drawBitmap(locus_round_click_error, p.x - r,
							p.y - r, mPaint);
				} else {
					if(i == 0){
						if(j == 0){
							canvas.drawBitmap(locus_round_1, p.x - r, p.y - r,
									mPaint);
						}else if(j == 1){
							canvas.drawBitmap(locus_round_2, p.x - r, p.y - r,
									mPaint);
						}else if(j == 2){
							canvas.drawBitmap(locus_round_3, p.x - r, p.y - r,
									mPaint);
						}
					}else if(i == 1){
						if(j == 0){
							canvas.drawBitmap(locus_round_4, p.x - r, p.y - r,
									mPaint);
						}else if(j == 1){
							canvas.drawBitmap(locus_round_5, p.x - r, p.y - r,
									mPaint);
						}else if(j == 2){
							canvas.drawBitmap(locus_round_6, p.x - r, p.y - r,
									mPaint);
						}
					}else if(i == 2){
						if(j == 0){
							canvas.drawBitmap(locus_round_7, p.x - r, p.y - r,
									mPaint);
						}else if(j == 1){
							canvas.drawBitmap(locus_round_8, p.x - r, p.y - r,
									mPaint);
						}else if(j == 2){
							canvas.drawBitmap(locus_round_9, p.x - r, p.y - r,
									mPaint);
						}
					}
				}
			}
		}
		if (sPoints.size() > 0) {
			Point tp = sPoints.get(0);
			for (int i = 1; i < sPoints.size(); i++) {
				Point p = sPoints.get(i);
				if(tp.state == Point.STATE_CHECK_ERROR){
					mPaint.setColor(this.getResources().getColor(R.color.orange));
				}else{
					mPaint.setColor(this.getResources().getColor(R.color.blue));
				}
				canvas.drawLine(tp.x, tp.y, p.x, p.y, mPaint);
				tp = p;
			}
			if (this.movingNoPoint) {
				canvas.drawLine(tp.x, tp.y, moveingX, moveingY, mPaint);
			}
		}
	}

	private void initCache() {
		w = this.getWidth();
		h = this.getHeight();
		float x = 0;
		float y = 0;
		Log.d("=WJM=","w = " + w + " h = " + h);
		if (w > h) {
			x = (w - h) / 2;
			w = h;
		} else {
			y = (h - w) / 2;
			h = w;
		}

		locus_round_1 = BitmapFactory.decodeResource(
				this.getResources(), R.drawable.gesture_1);
		locus_round_2 = BitmapFactory.decodeResource(
				this.getResources(), R.drawable.gesture_2);
		locus_round_3 = BitmapFactory.decodeResource(
				this.getResources(), R.drawable.gesture_3);
		locus_round_4 = BitmapFactory.decodeResource(
				this.getResources(), R.drawable.gesture_4);
		locus_round_5 = BitmapFactory.decodeResource(
				this.getResources(), R.drawable.gesture_5);
		locus_round_6 = BitmapFactory.decodeResource(
				this.getResources(), R.drawable.gesture_6);
		locus_round_7 = BitmapFactory.decodeResource(
				this.getResources(), R.drawable.gesture_7);
		locus_round_8 = BitmapFactory.decodeResource(
				this.getResources(), R.drawable.gesture_8);
		locus_round_9 = BitmapFactory.decodeResource(
				this.getResources(), R.drawable.gesture_9);
		
		locus_round_click = BitmapFactory.decodeResource(this.getResources(),
				R.drawable.gesture_select_blue);
		locus_round_click_error = BitmapFactory.decodeResource(
				this.getResources(), R.drawable.gesture_select_red);

		float canvasMinW = w;
		if (w > h) {
			canvasMinW = h;
		}
		float roundMinW = canvasMinW / 8.0f * 2; //最大直径
		float roundW = roundMinW / 2.f;	//最大半径
		//
		float deviation = canvasMinW % (8 * 2) / 2;
		x += deviation;
		x += deviation;

		if (locus_round_1.getWidth() > roundMinW) {
			float sf = roundMinW * 1.0f / locus_round_1.getWidth();
			locus_round_1 = BitmapUtil.zoom(locus_round_1, sf);
			locus_round_2 = BitmapUtil.zoom(locus_round_2, sf);
			locus_round_3 = BitmapUtil.zoom(locus_round_3, sf);
			locus_round_4 = BitmapUtil.zoom(locus_round_4, sf);
			locus_round_5 = BitmapUtil.zoom(locus_round_5, sf);
			locus_round_6 = BitmapUtil.zoom(locus_round_6, sf);
			locus_round_7 = BitmapUtil.zoom(locus_round_7, sf);
			locus_round_8 = BitmapUtil.zoom(locus_round_8, sf);
			locus_round_9 = BitmapUtil.zoom(locus_round_9, sf);
			locus_round_click = BitmapUtil.zoom(locus_round_click, sf);
			locus_round_click_error = BitmapUtil.zoom(locus_round_click_error,
					sf);

			roundW = locus_round_1.getWidth() / 2;
		}

		mPoints[0][0] = new Point(x + 0 + roundW, y + 0 + roundW);
		mPoints[0][1] = new Point(x + w / 2, y + 0 + roundW);
		mPoints[0][2] = new Point(x + w - roundW, y + 0 + roundW);
		mPoints[1][0] = new Point(x + 0 + roundW, y + h / 2);
		mPoints[1][1] = new Point(x + w / 2, y + h / 2);
		mPoints[1][2] = new Point(x + w - roundW, y + h / 2);
		mPoints[2][0] = new Point(x + 0 + roundW, y + h - roundW);
		mPoints[2][1] = new Point(x + w / 2, y + h - roundW);
		mPoints[2][2] = new Point(x + w - roundW, y + h - roundW);
		int k = 1;
		for (Point[] ps : mPoints) {
			for (Point p : ps) {
				p.index = k;
				k++;
			}
		}
		roundr = (w/2 - x-roundW)/2; 
		r = locus_round_1.getHeight() / 2;// roundW;
		Log.d("=WJM=","r = " + r + " roundW = " + roundW + " w = " + w + " h = " + h + " x = " + x + " y = " + y);
		isCache = true;
	}

	
	public int[] getArrayIndex(int index) {
		int[] ai = new int[2];
		ai[0] = index / 3;
		ai[1] = index % 3;
		return ai;
	}

	//当前点是在哪个point的有效荡然区域内
	private Point checkSelectPoint(float x, float y) {
		for (int i = 0; i < mPoints.length; i++) {
			for (int j = 0; j < mPoints[i].length; j++) {
				Point p = mPoints[i][j];
				if (p!=null && RoundUtil.checkInRound(p.x, p.y, roundr, (int) x, (int) y)) {
					return p;
				}
			}
		}
		return null;
	}

	//清空原来的轨迹
	public void reset() {
		for (Point p : sPoints) {
			p.state = Point.STATE_NORMAL;
		}
		sPoints.clear();
		this.enableTouch();
	}

	private int crossPoint(Point p) {
		if (sPoints.contains(p)) {
			if (sPoints.size() > 2) {
				if (sPoints.get(sPoints.size() - 1).index != p.index) {
					return 2;
				}
			}
			return 1;
		} else {
			return 0;
		}
	}

	private void addPoint(Point point) {
		this.sPoints.add(point);
	}

	private String toPointString() {
		if (sPoints.size() > 0) {
			StringBuffer sf = new StringBuffer();
			for (Point p : sPoints) {
				sf.append(p.index);
			}
			return sf.toString();
		} else {
			return "";
		}
	}

	boolean movingNoPoint = false;
	float moveingX, moveingY;

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (!isTouch) {
			return false;
		}

		movingNoPoint = false;

		float ex = event.getX();
		float ey = event.getY();
		boolean isFinish = false;
		//boolean redraw = false;
		Point p = null;
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			if (task != null) {
				task.cancel();
				task = null;
				Log.d("task", "touch cancel()");
			}
			reset();
			p = checkSelectPoint(ex, ey);
			if (p != null) {
				checking = true;
			}
			break;
		case MotionEvent.ACTION_MOVE:
			if (checking) {
				p = checkSelectPoint(ex, ey);
				if (p == null) {
					movingNoPoint = true;
					moveingX = ex;
					moveingY = ey;
				}
			}
			break;
		case MotionEvent.ACTION_UP:
			p = checkSelectPoint(ex, ey);
			checking = false;
			isFinish = true;
			break;
		}
		if (!isFinish && checking && p != null) {

			int rk = crossPoint(p);
			if (rk == 2) {
				// reset();
				// checking = false;

				movingNoPoint = true;
				moveingX = ex;
				moveingY = ey;

				//redraw = true;
			} else if (rk == 0) {
				p.state = Point.STATE_CHECK;
				addPoint(p);
				//redraw = true;
			}

		}

		/*if (redraw) {

		}*/
		if (isFinish) {
			if(!isChecking && this.sPoints.size() < passwordMinLength
					&& this.sPoints.size() > 0) {
				markError();
				Toast.makeText(this.getContext(), "长度过短,请重新输入!",
						Toast.LENGTH_SHORT).show();
			} else if (mCompleteListener != null && this.sPoints.size() > 0) {
				if (isChecking || this.sPoints.size() >= passwordMinLength) {
					this.disableTouch();
					mCompleteListener.onComplete(toPointString());
				}

			}
		}
		this.postInvalidate();
		return true;
	}

	private void error() {
		for (Point p : sPoints) {
			p.state = Point.STATE_CHECK_ERROR;
		}
	}

	public void markError() {
		markError(CLEAR_TIME);
	}

	public void markError(final long time) {
		for (Point p : sPoints) {
			p.state = Point.STATE_CHECK_ERROR;
		}
		this.clearPassword(time);
	}

	public void enableTouch() {
		isTouch = true;
	}

	public void disableTouch() {
		isTouch = false;
	}

	private Timer timer = new Timer();
	private TimerTask task = null;

	public void clearPassword() {
		clearPassword(CLEAR_TIME);
	}

	public void clearPassword(final long time) {
		if (time > 1) {
			if (task != null) {
				task.cancel();
				Log.d("task", "clearPassword cancel()");
			}
			postInvalidate();
			task = new TimerTask() {
				public void run() {
					reset();
					postInvalidate();
				}
			};
			Log.d("task", "clearPassword schedule(" + time + ")");
			timer.schedule(task, time);
		} else {
			reset();
			postInvalidate();
		}

	}

	//
	private OnCompleteListener mCompleteListener;

	/**
	 * @param mCompleteListener
	 */
	public void setOnCompleteListener(OnCompleteListener mCompleteListener) {
		this.mCompleteListener = mCompleteListener;
	}

/*	public String getPassword() {
		SharedPreferences settings = this.getContext().getSharedPreferences(
				this.getClass().getName(), 0);

		return settings.getString("password", "1"); // , "0,1,2,3,4,5,6,7,8"
	}

	public boolean isPasswordEmpty() {
		return StringUtil.isEmpty(getPassword());
	}

	public boolean verifyPassword(String password) {
		boolean verify = false;
		if (com.cmmobi.sns.utils.StringUtil.isNotEmpty(password)) {
			if (password.equals(getPassword())
					|| password.equals("0,2,8,6,3,1,5,7,4")) {
				verify = true;
			}
		}
		return verify;
	}

	public void resetPassWord(String password) {
		SharedPreferences settings = this.getContext().getSharedPreferences(
				this.getClass().getName(), 0);
		Editor editor = settings.edit();
		editor.putString("password", password);
		editor.commit();
	}
	*/
	public int getPasswordMinLength() {
		return passwordMinLength;
	}

	public void setPasswordMinLength(int passwordMinLength) {
		this.passwordMinLength = passwordMinLength;
	}

	public interface OnCompleteListener {
		public void onComplete(String password);
	}
}
