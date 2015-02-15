package com.cmmobi.looklook.common.view;

import android.app.Activity;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;

import com.cmmobi.looklook.R;

public class VolumeStateView {
	
	private PopupWindow volumeshow;	
	private PopupWindow popupCancle;

	private ImageView volumeImage;
	
	private Activity act;
	
	View popupWindow_view;
	FrameLayout soundShow;
	RelativeLayout soundCancle;
	
	public VolumeStateView(Activity act){
		this.act = act;
	}
	
	/**
	 * 创建PopupWindow
	 */
	public void showVolume(int vol) {
		// 获取自定义布局文件pop.xml的视图
		popupWindow_view = act.getLayoutInflater().inflate(
				R.layout.activity_homepage_sound_recorder_popupwindow, null,
				false);
		// 创建PopupWindow实例
		volumeshow = new PopupWindow(popupWindow_view,  ViewGroup.LayoutParams.WRAP_CONTENT, 
				ViewGroup.LayoutParams.WRAP_CONTENT, false);
		
		volumeshow.setOutsideTouchable(true);

		volumeImage = (ImageView) popupWindow_view
				.findViewById(R.id.volume_show);
		volumeshow.setBackgroundDrawable(act.getResources().getDrawable(
				R.drawable.dot_big));
		volumeshow.showAtLocation(popupWindow_view, Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL, 0, 0);
		//volumeshow.showAsDropDown(act.findViewById(R.id.im_mood_voice_play), 0, 0);
	}
	
	/**
	 * 更新音量PopupWindow
	 */
	public void updateVolume(int vol){
		if (null != volumeImage) {
			if (2650 < vol && vol < 2750) {
				volumeImage.setImageResource(R.drawable.yuyin_2);
			} else if (2750 < vol && vol < 2850) {
				volumeImage.setImageResource(R.drawable.yuyin_3);
			} else if (2850 < vol) {
				volumeImage.setImageResource(R.drawable.yuyin_4);
			} else {
				volumeImage.setImageResource(R.drawable.yuyin_1);
			}
		}
	}
	
	public void showVolume() {
		// 获取自定义布局文件pop.xml的视图
		popupWindow_view = act.getLayoutInflater().inflate(
				R.layout.activity_homepage_sound_recorder_and_canncle_popupwindow, null,
				false);

		soundShow = (FrameLayout)popupWindow_view.findViewById(R.id.sound_show);
		soundCancle = (RelativeLayout)popupWindow_view.findViewById(R.id.sound_cancle);
		// 创建PopupWindow实例
		volumeshow = new PopupWindow(popupWindow_view,  ViewGroup.LayoutParams.WRAP_CONTENT, 
				ViewGroup.LayoutParams.WRAP_CONTENT, false);
		
		volumeshow.setOutsideTouchable(true);

		volumeImage = (ImageView) popupWindow_view
				.findViewById(R.id.volume_show);
		volumeshow.setBackgroundDrawable(act.getResources().getDrawable(
				R.drawable.dot_big));
		volumeshow.showAtLocation(popupWindow_view, Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL, 0, 0);
		//volumeshow.showAsDropDown(act.findViewById(R.id.im_mood_voice_play), 0, 0);
	}
	
	public void updateView(int vol, boolean isOnBtn){
			if (isOnBtn) {
				if (null != volumeImage) {
					if (2650 < vol && vol < 2750) {
						volumeImage.setImageResource(R.drawable.yuyin_2);
					} else if (2750 < vol && vol < 2850) {
						volumeImage.setImageResource(R.drawable.yuyin_3);
					} else if (2850 < vol) {
						volumeImage.setImageResource(R.drawable.yuyin_4);
					} else {
						volumeImage.setImageResource(R.drawable.yuyin_1);
					}
				}
			}else{
				soundShow.setVisibility(View.GONE);
				soundCancle.setVisibility(View.VISIBLE);
			}
	}
	
	/**
	 * 创建PopupWindow
	 */
	public void showVolume(View view) {
		// 获取自定义布局文件pop.xml的视图
		popupWindow_view = act.getLayoutInflater().inflate(
				R.layout.activity_editmedia_sound_recorder_and_canncle_popupwindow, null,
				false);
		
		soundShow = (FrameLayout)popupWindow_view.findViewById(R.id.sound_show);
		soundCancle = (RelativeLayout)popupWindow_view.findViewById(R.id.sound_cancle);

		// 创建PopupWindow实例
		volumeshow = new PopupWindow(popupWindow_view,  ViewGroup.LayoutParams.WRAP_CONTENT, 
				ViewGroup.LayoutParams.WRAP_CONTENT, false);
		
		volumeshow.setOutsideTouchable(true);

		volumeImage = (ImageView) popupWindow_view
				.findViewById(R.id.volume_show);
		volumeshow.setBackgroundDrawable(act.getResources().getDrawable(
				R.drawable.dot_big));
		
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
    	
    	volumeshow.showAtLocation(popupWindow_view, Gravity.TOP|Gravity.CENTER_HORIZONTAL, 0, anchorCenter[1]-height );
    	
		/*if (null != volumeImage) {
			if (2650 < vol && vol < 2750) {
				volumeImage.setImageResource(R.drawable.yuyin_2);
			} else if (2750 < vol && vol < 2850) {
				volumeImage.setImageResource(R.drawable.yuyin_3);
			} else if (2850 < vol) {
				volumeImage.setImageResource(R.drawable.yuyin_4);
			} else {
				volumeImage.setImageResource(R.drawable.yuyin_1);
			}
		}*/
	}
	
	public void showCancle(View view){
		popupWindow_view = act.getLayoutInflater().inflate(
				R.layout.activity_editphoto_sound_recorder_popupwindow_cancle, null,
				false);
		// 创建PopupWindow实例
		popupCancle = new PopupWindow(popupWindow_view,  ViewGroup.LayoutParams.WRAP_CONTENT, 
				ViewGroup.LayoutParams.WRAP_CONTENT, false);
		popupCancle.setBackgroundDrawable(act.getResources().getDrawable(
				R.drawable.dot_big));
		popupCancle.setOutsideTouchable(true); 
		
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
    	
		popupCancle.showAtLocation(popupWindow_view, Gravity.CENTER_HORIZONTAL | Gravity.TOP, 0, anchorCenter[1]-height - 30);
	}
	
	public void showCancle(){
		popupWindow_view = act.getLayoutInflater().inflate(
				R.layout.activity_homepage_sound_recorder_popupwindow_cancle, null,
				false);
		// 创建PopupWindow实例
		popupCancle = new PopupWindow(popupWindow_view,  ViewGroup.LayoutParams.WRAP_CONTENT, 
				ViewGroup.LayoutParams.WRAP_CONTENT, false);
		popupCancle.setOutsideTouchable(true); 
		popupCancle.setBackgroundDrawable(act.getResources().getDrawable(
				R.drawable.dot_big));
		popupCancle.showAtLocation(popupWindow_view, Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL, 0, 0);
	}
	
	public void dismissView(){
		if (volumeshow != null && volumeshow.isShowing()) {
			volumeshow.dismiss();
		}
	}
	
	public void dismissCancle(){
		if (popupCancle != null && popupCancle.isShowing()) {
			popupCancle.dismiss();
		}
	}
}
