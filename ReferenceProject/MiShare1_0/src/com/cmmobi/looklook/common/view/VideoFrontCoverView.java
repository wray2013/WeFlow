package com.cmmobi.looklook.common.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.ImageView;

import com.cmmobi.looklook.R;
import com.cmmobi.looklook.fragment_del.EditVideoFrontCoverFragment;

public class VideoFrontCoverView extends ImageView{
	private Bitmap thumbBmp = BitmapFactory.decodeResource(getResources(),R.drawable.del_fengmian_2);
	private Paint paint = new Paint();
	private boolean isMeasured = false;
	private int thumbWidth;
	private int thumbX;
	private int totalWidth;
	long duration = 0;// 单位为s
	private Handler handler = null;
	
	public VideoFrontCoverView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	public VideoFrontCoverView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}
	
	public void setHandler(Handler handler) {
		this.handler = handler;
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
		thumbWidth = thumbBmp.getWidth();
		thumbX = 0;
		totalWidth = getMeasuredWidth() - thumbWidth;
		invalidate();
	}

	@Override
	protected void onDraw(Canvas canvas) {
		// TODO Auto-generated method stub
		super.onDraw(canvas);
		canvas.drawBitmap(thumbBmp, thumbX, 0, paint);
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		int mx = (int) event.getX();
		thumbX = mx;
		if(thumbX < 0) {
			thumbX = 0;
		}
		
		if (thumbX > getWidth() - thumbWidth) {
			thumbX = getWidth() - thumbWidth;
		}
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			
			break;
		case MotionEvent.ACTION_MOVE:
			break;
		case MotionEvent.ACTION_UP:
			if (handler != null) {
				Log.d("EditVideoDetailActivity","sendmessage in");
				Message msg = new Message();
				msg.what = EditVideoFrontCoverFragment.HANDLER_UPDATE_FRONT_COVER;
				msg.arg1 = thumbX;
				msg.arg2 = totalWidth;
				handler.sendMessage(msg);
			}
			break;
		}
		
		invalidate();
		return true;
	}

}
