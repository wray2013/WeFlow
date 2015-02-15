package com.cmmobi.looklook.common.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.GridView;

public class VideoMontageGridView extends GridView{

	private final String TAG = this.getClass().getSimpleName();
	public VideoMontageGridView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}
	
	public VideoMontageGridView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}
	
	@Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        // TODO Auto-generated method stub
        if(ev.getAction() == MotionEvent.ACTION_MOVE){
            return true;//forbid its child(gridview) to scroll
        }
        return super.dispatchTouchEvent(ev);
    }

}
