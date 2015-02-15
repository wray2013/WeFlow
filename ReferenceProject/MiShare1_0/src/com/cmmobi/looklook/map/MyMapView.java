package com.cmmobi.looklook.map;

import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;

import com.baidu.mapapi.map.MapView;
import com.cmmobi.looklook.info.profile.TimeHelper;

public class MyMapView extends MapView {
	public final static int HANDLER_FLAG_MAPVIEW_UPDATE = 0xbd123001;
	public final static int HANDLER_FLAG_MAPVIEW_CLICK = 0xbd123002;
	public final static int HANDLER_FLAG_MAPVIEW_DOWN = 0xbd123003;
	public final static int HANDLER_FLAG_MAPVIEW_MOVE = 0xbd123004;
	public final static int HANDLER_FLAG_MAPVIEW_UP = 0xbd123005;
	
	private final String TAG = "MyMapView";
	private long clicktime ;
	private Handler handler;

	public MyMapView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}
	
	public MyMapView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}
	
	public MyMapView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
	}

	protected  void	onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		Log.e(TAG, "MyMapView - onMeasure");
	}
	
	
	protected  void	onLayout(boolean flag, int l, int t, int r, int b) {
		super.onLayout(flag, l, t, r, b);
		Log.e(TAG, "MyMapView - onLayout");
	}
	
	public boolean onTouchEvent(MotionEvent event){
		if(event.getAction()==MotionEvent.ACTION_DOWN ){
			long now = TimeHelper.getInstance().now();
			if(now-clicktime<500){//<500ms: double click
				return true;
			}
			clicktime = now;
		}

		if(handler!=null){
			if(event.getAction()==261 ){
				/*			Message msg = handler.obtainMessage(HANDLER_FLAG_MAPVIEW_UPDATE);
							msg.sendToTarget();*/
				handler.sendEmptyMessageDelayed(HANDLER_FLAG_MAPVIEW_UPDATE, 500);
			}else if(event.getAction() == MotionEvent.ACTION_DOWN){
				handler.sendEmptyMessage(HANDLER_FLAG_MAPVIEW_CLICK);
			}
		}
		
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			if (handler != null) {
				handler.sendEmptyMessage(HANDLER_FLAG_MAPVIEW_DOWN);
			}
			break;
		case MotionEvent.ACTION_MOVE:
			if (handler != null) {
				handler.sendEmptyMessage(HANDLER_FLAG_MAPVIEW_MOVE);
			}
			break;
		case MotionEvent.ACTION_UP:
			if (handler != null) {
				handler.sendEmptyMessage(HANDLER_FLAG_MAPVIEW_UP);
			}
			break;
		}


		super.onTouchEvent(event);
		Log.e(TAG, "onTouchEvent - type:" + event.getAction() + " size:" + event.getSize());
		return true;
		
	}
	
	public void setHandler(Handler handler){
		this.handler = handler;
	}
	 


}
