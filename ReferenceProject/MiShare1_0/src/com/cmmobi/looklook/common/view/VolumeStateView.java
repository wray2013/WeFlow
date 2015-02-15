package com.cmmobi.looklook.common.view;

import android.app.Activity;
import android.graphics.drawable.AnimationDrawable;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cmmobi.looklook.R;

public class VolumeStateView {
	
	private PopupWindow volumeshow;	

	private ImageView volumeImage;
	
	private Activity act;
	
	View popupWindow_view;
	RelativeLayout soundShow;
	RelativeLayout soundCancle;
	private TextView countdownTv;
	private AnimationDrawable frameAnim;
	
	public VolumeStateView(Activity act){
		this.act = act;
	}
	
	/**
	 * 更新音量PopupWindow
	 */
	public void updateVolume(int vol){
		if (null != volumeImage) {
			if (2650 < vol && vol < 2750) {
				volumeImage.setImageResource(R.drawable.del_yuyin_2);
			} else if (2750 < vol && vol < 2850) {
				volumeImage.setImageResource(R.drawable.del_yuyin_3);
			} else if (2850 < vol) {
				volumeImage.setImageResource(R.drawable.del_yuyin_4);
			} else {
				volumeImage.setImageResource(R.drawable.del_yuyin_1);
			}
		}
	}
	
	public void show() {
		// 获取自定义布局文件pop.xml的视图
		popupWindow_view = act.getLayoutInflater().inflate(
				R.layout.recorder_popupwindow, null,
				false);

		soundShow = (RelativeLayout)popupWindow_view.findViewById(R.id.sound_show);
		soundCancle = (RelativeLayout)popupWindow_view.findViewById(R.id.sound_cancle);
		// 创建PopupWindow实例
		volumeshow = new PopupWindow(popupWindow_view,  ViewGroup.LayoutParams.WRAP_CONTENT, 
				ViewGroup.LayoutParams.WRAP_CONTENT, false);
		
		volumeshow.setOutsideTouchable(false);
		
		countdownTv = (TextView) popupWindow_view.findViewById(R.id.tv_short_rec_countdown);

		volumeImage = (ImageView) popupWindow_view
				.findViewById(R.id.iv_voice);
		volumeshow.setBackgroundDrawable(act.getResources().getDrawable(
				R.drawable.del_dot_big));
		volumeshow.showAtLocation(popupWindow_view, Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL, 0, 0);
		frameAnim=(AnimationDrawable) act.getResources().getDrawable(R.drawable.voice_frame_animation);
		// 把AnimationDrawable设置为ImageView的背景
		volumeImage.setBackgroundDrawable(frameAnim);
		frameAnim.start();
		//volumeshow.showAsDropDown(act.findViewById(R.id.im_mood_voice_play), 0, 0);
	}
	
	/**
	 * 创建PopupWindow
	 */
	public void show(View view) {
		// 获取自定义布局文件pop.xml的视图
		popupWindow_view = act.getLayoutInflater().inflate(
				R.layout.recorder_popupwindow, null,
				false);
		
		soundShow = (RelativeLayout)popupWindow_view.findViewById(R.id.sound_show);
		soundCancle = (RelativeLayout)popupWindow_view.findViewById(R.id.sound_cancle);

		// 创建PopupWindow实例
		volumeshow = new PopupWindow(popupWindow_view,  ViewGroup.LayoutParams.WRAP_CONTENT, 
				ViewGroup.LayoutParams.WRAP_CONTENT, false);
		
		volumeshow.setOutsideTouchable(true);

		volumeImage = (ImageView) popupWindow_view
				.findViewById(R.id.volume_show);
		volumeshow.setBackgroundDrawable(act.getResources().getDrawable(
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
    	
    	volumeshow.showAtLocation(popupWindow_view, Gravity.TOP|Gravity.CENTER_HORIZONTAL, 0, anchorCenter[1]-height );
    	
	}
	
	public void showCancle() {
		soundShow.setVisibility(View.GONE);
		soundCancle.setVisibility(View.VISIBLE);
		frameAnim.stop();
	}
	
	public void showVolume() {
		soundShow.setVisibility(View.VISIBLE);
		soundCancle.setVisibility(View.GONE);
		frameAnim.start();
	}
	
	public void updateTime(long time) {
		if (countdownTv != null) {
			Log.d("=AAA=","AAA time = " + time);
			String timeStr = String.valueOf(time);
			countdownTv.setText(timeStr + "\"");
		}
	}
	
	public void updateView(int vol, boolean isOnBtn){
			if (isOnBtn) {
				if (null != volumeImage) {
					if (2650 < vol && vol < 2750) {
						volumeImage.setImageResource(R.drawable.del_yuyin_2);
					} else if (2750 < vol && vol < 2850) {
						volumeImage.setImageResource(R.drawable.del_yuyin_3);
					} else if (2850 < vol) {
						volumeImage.setImageResource(R.drawable.del_yuyin_4);
					} else {
						volumeImage.setImageResource(R.drawable.del_yuyin_1);
					}
				}
			}else{
				soundShow.setVisibility(View.GONE);
				soundCancle.setVisibility(View.VISIBLE);
			}
	}
	
	
	public void dismiss(){
		if (volumeshow != null && volumeshow.isShowing()) {
			volumeshow.dismiss();
		}
		frameAnim.stop();
	}
	
	public boolean isShowing(){
		return (volumeshow != null && volumeshow.isShowing());
	}
	
}
