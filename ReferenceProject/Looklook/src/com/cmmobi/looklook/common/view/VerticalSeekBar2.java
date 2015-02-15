package com.cmmobi.looklook.common.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import cn.zipper.framwork.core.ZApplication;
import cn.zipper.framwork.core.ZLog;
import cn.zipper.framwork.utils.ZGraphics;
import cn.zipper.framwork.utils.ZUniformScaler;
import cn.zipper.framwork.utils.ZUniformScaler.Model;
import cn.zipper.framwork.utils.ZUniformScaler.ScaleType;
import cn.zipper.framwork.view.ZBaseView;

import com.cmmobi.looklook.R;

public class VerticalSeekBar2 extends ZBaseView {
	
	public static interface OnSeekBarChangeListener {
		public void onProgressChanged(VerticalSeekBar2 seekBar, int progress, boolean fromUser);
	}
	
	private Rect temp;
	private Rect rect;
	private Rect knobRect;
	private Rect backgroundRect;
	private Bitmap knob;
	private Bitmap background;
	private OnSeekBarChangeListener listener;
	
	private int progress;
	private int availableHeight;
	private int lastY;
	private int initTimes;
	private boolean isDrag;
	
	
	public VerticalSeekBar2(Context context, AttributeSet attrs) {
		super(context, attrs);
		background = BitmapFactory.decodeResource(ZApplication.getInstance().getResources(), R.drawable.seekbar_bgd);
		knob = BitmapFactory.decodeResource(ZApplication.getInstance().getResources(), R.drawable.seek_bar_knob2);
		width = knob.getWidth();
		temp = new Rect();
	}

	@Override
	protected void onInit() {
		if (initTimes < 50) {
			initTimes ++;
			
			this.getGlobalVisibleRect(temp);
			width = temp.width();
			height = temp.height();
			
			rect = new Rect(0, 0, width, height);
			knobRect = new Rect(0, 0, knob.getWidth(), knob.getHeight());
			backgroundRect = new Rect(3, 0, width, height);
			
			Model standardModel = new Model(); // 标准模型;
			standardModel.put(Model.W, rect.width()); // 添加参数W;
			standardModel.put(Model.H, rect.height()); // 添加参数H;
			
			Model knobModel = new Model(); // 原始模型;
			knobModel.put(Model.W, knobRect.width()); // 添加参数W;
			knobModel.put(Model.H, knobRect.height()); // 添加参数H;
			
			ZUniformScaler.scale(knobModel, standardModel, ScaleType.IN);
			
			knobRect.set(0, 0, (int) knobModel.get(Model.W), (int) knobModel.get(Model.H));
			knobRect.offsetTo(0, lastY);
			
			Bitmap bitmap = ZGraphics.resize(knob, knobRect.width(), knobRect.height(), true);
			if (bitmap != null) {
				knob = bitmap;
				availableHeight = rect.height() - knobRect.height();
			}
			
			invalidate();
		}
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		boolean b = false;
		isDrag = false;
		
		if (this.isEnabled() && this.getVisibility() == View.VISIBLE) {
			int x = (int) event.getX();
			int y = (int) event.getY();
			
			switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN:
				b = true;
				break;
				
			case MotionEvent.ACTION_MOVE:
				b = true;
				break;
				
			case MotionEvent.ACTION_UP:
				b = true;
				break;
			}
			
			if (!b) {
				b = super.onTouchEvent(event);
			} else {
				isDrag = true;
				setY(y);
				notifyListener(true);
			}
		}
		
		return b;
	}
	
	public boolean isDrag() {
		return isDrag;
	}
	
	private void setY(int y) {
		if (y < 0) {
			y = 0;
		} else if (y > availableHeight) {
			y = availableHeight;
		}
		lastY = y;
		knobRect.offsetTo(0, y);
	}
	
	public void setProgress(int progress) {
		this.progress = progress;
		setY(progress * availableHeight / 100);
		notifyListener(false);
	}
	
	public int getProgress() {
		return progress;
	}
	
	private void notifyListener(boolean fromUser) {
		progress = (int) ((float) knobRect.top / (float) availableHeight * 100);
		invalidate();
		if (listener != null) {
			listener.onProgressChanged(this, progress, fromUser);
		}
	}

	@Override
	protected void onStart() {
		
	}

	@Override
	protected void onStop() {
		
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		ZGraphics.drawNinepath(canvas, background, backgroundRect);
		canvas.drawBitmap(knob, knobRect.left, knobRect.top, paint);
		super.onDraw(canvas);
	}
	
	public void setOnSeekBarChangeListener(OnSeekBarChangeListener listener) {
		this.listener = listener;
	}
	
}