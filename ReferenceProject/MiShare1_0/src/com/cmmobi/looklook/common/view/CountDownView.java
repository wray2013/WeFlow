package com.cmmobi.looklook.common.view;

import android.app.Activity;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.cmmobi.looklook.R;

public class CountDownView {
	
	private PopupWindow timeShow;
	View popupWindow_view;
	private Activity act;
	private TextView countdownTv;
	
	public CountDownView(Activity act){
		this.act = act;
	}
	
	public void show() {
		// 获取自定义布局文件pop.xml的视图
		popupWindow_view = act.getLayoutInflater().inflate(
				R.layout.del_activity_short_recorder_countdown_popupwindow, null,
				false);

		countdownTv = (TextView) popupWindow_view.findViewById(R.id.tv_short_rec_countdown);
		// 创建PopupWindow实例
		timeShow = new PopupWindow(popupWindow_view,  ViewGroup.LayoutParams.WRAP_CONTENT, 
				ViewGroup.LayoutParams.WRAP_CONTENT, false);
		
		timeShow.setOutsideTouchable(true);

		timeShow.setBackgroundDrawable(act.getResources().getDrawable(
				R.drawable.del_dot_big));
		timeShow.showAtLocation(popupWindow_view, Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL, 0, 0);
	}
	
	/**
	 * 创建PopupWindow
	 */
	public void show(View view) {
		// 获取自定义布局文件pop.xml的视图
		popupWindow_view = act.getLayoutInflater().inflate(
				R.layout.del_activity_short_recorder_countdown_popupwindow, null,
				false);
		
		countdownTv = (TextView) popupWindow_view.findViewById(R.id.tv_short_rec_countdown);
		// 创建PopupWindow实例
		timeShow = new PopupWindow(popupWindow_view,  ViewGroup.LayoutParams.WRAP_CONTENT, 
				ViewGroup.LayoutParams.WRAP_CONTENT, false);
		
		timeShow.setOutsideTouchable(true);

		timeShow.setBackgroundDrawable(act.getResources().getDrawable(
				R.drawable.del_dot_big));
		
		int[] location=new int[2];
    	//保存anchor上部中点
    	int[] anchorCenter=new int[2];
    	//读取位置anchor座标
    	view.getLocationOnScreen(location);
    	//计算anchor中点
    	anchorCenter[1]=location[1];
    	
    	int w = View.MeasureSpec.makeMeasureSpec(0,View.MeasureSpec.UNSPECIFIED);  
    	int h = View.MeasureSpec.makeMeasureSpec(0,View.MeasureSpec.UNSPECIFIED);  
    	popupWindow_view.measure(w, h);  
    	int height =popupWindow_view.getMeasuredHeight(); 
    	Log.d("=BBB=","height = " + height + " y = " + anchorCenter[1]);
    	
    	timeShow.showAtLocation(popupWindow_view, Gravity.TOP|Gravity.CENTER_HORIZONTAL, 0, anchorCenter[1]-height );
    	
	}
	
	public void updateTime(long time) {
		if (countdownTv != null) {
			Log.d("=AAA=","AAA time = " + time);
			String timeStr = String.valueOf(time);
			countdownTv.setText(timeStr + "\"");
		}
	}
	
	public void dismiss(){
		try {
			if (timeShow != null && timeShow.isShowing()) {
				timeShow.dismiss();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
