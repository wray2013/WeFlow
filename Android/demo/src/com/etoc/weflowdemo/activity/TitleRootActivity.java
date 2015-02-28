package com.etoc.weflowdemo.activity;

import java.util.Timer;
import java.util.TimerTask;

import com.etoc.weflowdemo.R;
import com.etoc.weflowdemo.dialog.PromptDialog;
import com.etoc.weflowdemo.event.DialogEvent;
import com.etoc.weflowdemo.util.DisplayUtil;
import com.etoc.weflowdemo.util.ViewUtils;

import de.greenrobot.event.EventBus;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.DownloadManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Handler.Callback;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.view.animation.Animation.AnimationListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public abstract class TitleRootActivity extends FragmentActivity implements OnClickListener, Callback {


	public abstract int subContentViewId();
	
	private RelativeLayout rlyTitle;
	private FrameLayout rlyContent;
	protected ImageButton leftButton;
	protected Button tvLeftBtn;
	protected Button rightButton;
	protected ImageView ivRightButton;
	protected TextView title;
	private ImageView iv_title;
	protected Handler handler;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.fragment_title_root);
		
		rlyContent = (FrameLayout)findViewById(R.id.rly_fg_content);
		rlyTitle = (RelativeLayout)findViewById(R.id.inc_title_bar);
		
		int paddingSize = DisplayUtil.getSize(this, 12);
		RelativeLayout.LayoutParams pm = (RelativeLayout.LayoutParams)rlyTitle.getLayoutParams();
		pm.height = DisplayUtil.getSize(this, 112);
		rlyTitle.setLayoutParams(pm);
//		rlyTitle.setPadding(0, 0, paddingSize, 0);

		
		leftButton = (ImageButton)findViewById(R.id.ib_title_left);
		leftButton.setPadding(paddingSize, 0, 0, 0);
		tvLeftBtn = (Button) findViewById(R.id.btn_title_left_tv);
		tvLeftBtn.setPadding(paddingSize, 0, paddingSize, 0);
		rightButton = (Button)findViewById(R.id.btn_title_right);
		ivRightButton = (ImageView)findViewById(R.id.iv_title_right);
	
		ViewUtils.setMarginRight(rightButton, 12);
		ViewUtils.setMarginRight(ivRightButton, 12);
		ViewUtils.setMarginLeft(tvLeftBtn, 12);
		rightButton.setOnClickListener(this);
		ivRightButton.setOnClickListener(this);
		leftButton.setOnClickListener(this);
		
		title = (TextView)findViewById(R.id.tv_title);	
		title.setTextSize(DisplayUtil.textGetSizeSp(this, 45));
		title.setMaxWidth(DisplayUtil.getSize(this,480));
		iv_title = (ImageView)findViewById(R.id.iv_title);
		if(subContentViewId() != 0){
			LayoutInflater.from(this).inflate(subContentViewId() , rlyContent);
		}
		
		handler = new Handler(this);
		
		overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out); 
		
		EventBus.getDefault().register(this);
	}
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.ib_title_left:
			this.finish();
			break;
		default:
			break;
		}
	}
	
	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
		if (ev.getAction() == MotionEvent.ACTION_DOWN) {
			View v = getCurrentFocus();
			if (isShouldHideInputScreen(v, ev)) {
				InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
				if (imm != null) {
					imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
				}
			}
			return super.dispatchTouchEvent(ev);
		}
		// 必不可少，否则所有的组件都不会有TouchEvent�?
		if (getWindow().superDispatchTouchEvent(ev)) {
			return true;
		}
		return onTouchEvent(ev);
	}

	
	public  boolean isShouldHideInputScreen(View v, MotionEvent event) {
		if (v != null && (v instanceof EditText)) {
			int[] leftTop = { 0, 0 };
			//获取输入框当前的location位置
			v.getLocationOnScreen(leftTop);
			int left = leftTop[0];
			int top = leftTop[1];
			int bottom = top + v.getHeight();
			int right = left + v.getWidth();
			if (event.getRawX() > left && event.getRawX() < right
					&& event.getRawY() > top && event.getRawY() < bottom) {
				// 点击的是输入框区域，保留点击EditText的事�?
				return false;
			} else {
				return true;
			}
		}
		return false;
	}
	
	@Override
	protected void onResume() {
		super.onResume();
	}

	@Override
	protected void onPause() {
		super.onPause();
	}
	

	@Override
	protected void onStop(){
		super.onStop();
	}
	
	@Override
	protected void onDestroy(){
		super.onDestroy();
		
		EventBus.getDefault().unregister(this);
	}
	
	/**
	 * 返回rlyContent
	 */
	protected FrameLayout getSubContentView() {
		return rlyContent;
	}

	/**
	 * 隐藏titlebar
	 */
	protected void hideTitlebar() {
		/*rlyTitle.clearAnimation();
		Animation animation1 = AnimationUtils.loadAnimation(this,
				R.anim.option_leave_from_top);
		animation1.setAnimationListener(new AnimationListener() {
			@Override
			public void onAnimationEnd(Animation animation) {
				rlyTitle.setVisibility(View.GONE);
			}

			@Override
			public void onAnimationStart(Animation animation) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onAnimationRepeat(Animation animation) {
				// TODO Auto-generated method stub
				
			}
		});
		rlyTitle.startAnimation(animation1);*/
		rlyTitle.setVisibility(View.GONE);
	}
	

	/**
	 * 显示titlebar
	 */
	protected void showTitlebar() {
		/*rlyTitle.clearAnimation();
		Animation animation1 = AnimationUtils.loadAnimation(this,
				R.anim.option_entry_from_top);
		animation1.setAnimationListener(new AnimationListener() {
			@Override
			public void onAnimationEnd(Animation animation) {
				rlyTitle.setVisibility(View.VISIBLE);
			}

			@Override
			public void onAnimationStart(Animation animation) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onAnimationRepeat(Animation animation) {
				// TODO Auto-generated method stub
				
			}
		});
		rlyTitle.startAnimation(animation1);*/
		rlyTitle.setVisibility(View.VISIBLE);
	}
	
	/**
	 * 获取标题�?
	 * @return
	 */
	protected RelativeLayout getTitleBar() {
		return rlyTitle;
	}
	/**
	 * 隐藏左上角按�?(�?般为返回)
	 */
	protected void hideLeftButton(){
		leftButton.setVisibility(View.INVISIBLE);
	}
	
	/**
	 * 隐藏右上角按�?
	 */
	protected void hideRightButton(){
		rightButton.setVisibility(View.INVISIBLE);
		ivRightButton.setVisibility(View.GONE);
	}
	
	/**
	 * 显示左上角按�?
	 */
	protected void showLeftButton(){
		leftButton.setVisibility(View.VISIBLE);
	}
	
	/**
	 * 显示右上角按�?
	 */
	protected void showRightButton(){
		rightButton.setVisibility(View.VISIBLE);
	}
	
	protected void showIvRightButton(){
		ivRightButton.setVisibility(View.VISIBLE);
	}
	
	/**
	 * 右上角按�?
	 */
	public Button getRightButton(){
		return rightButton;
	}
	/**
	 * 左上角按�?
	 */
	protected ImageButton getLeftButton(){
		return leftButton;
	}
	
	protected TextView getTvTitle(){
		return title;
	}
	
	protected void setLeftButtonBackground(int resId){
		leftButton.setImageResource(resId);
		
	}
	
	protected void setRightButtonBackground(int resId){
		rightButton.setBackgroundResource(resId);
		rightButton.setText(" ");
		RelativeLayout.LayoutParams lParams = (RelativeLayout.LayoutParams) rightButton.getLayoutParams();
		lParams.height = DisplayUtil.getSize(this, 72);
		lParams.width = lParams.height;
		rightButton.setLayoutParams(lParams);
	}
	
	
	protected void setRightButtonText(String s){
		rightButton.setBackgroundColor(Color.TRANSPARENT);
		rightButton.setText(s);
		rightButton.setTextSize(DisplayUtil.textGetSizeSp(this, 33));
		rightButton.setPadding(DisplayUtil.getSize(this, 20), DisplayUtil.getSize(this, 19), DisplayUtil.getSize(this, 19), DisplayUtil.getSize(this, 12));
	}
	
	protected void setLeftButtonText(String s) {
		hideLeftButton();
		tvLeftBtn.setVisibility(View.VISIBLE);
		tvLeftBtn.setBackgroundColor(Color.TRANSPARENT);
		tvLeftBtn.setText(s);
		tvLeftBtn.setTextSize(DisplayUtil.textGetSizeSp(this, 33));
	}
	
	protected void setTitleBackground(int resId){
		title.setBackgroundResource(resId);
		title.setText("");
		title.setCompoundDrawables(null, null, null, null); 
	}
	
	protected void setTitleText(String str){
		title.setBackgroundColor(Color.TRANSPARENT);
		title.setText(str);
		iv_title.setVisibility(View.GONE); 
	}
	
	protected void setTitleTextAndDrawable(String str, int resId){
		setTitleText(str);
		title.setPadding(DisplayUtil.getSize(this, 6), DisplayUtil.getSize(this, 17), DisplayUtil.getSize(this, 6), DisplayUtil.getSize(this, 17));
		iv_title.setPadding(0, DisplayUtil.getSize(this, 17), DisplayUtil.getSize(this, 17), DisplayUtil.getSize(this, 17));
		iv_title.setVisibility(View.VISIBLE);
		iv_title.setImageResource(resId);
		title.setOnClickListener(this);
		iv_title.setOnClickListener(this);
	}
	
	public static void ProcessDialogEvent(final Context context, final DialogEvent evt, String className){
		if(!isTopActivity(context, className)){
			return;
		}
		switch(evt){
		case LOADING_START:
			PromptDialog.showProgressDialog(context);
			break;
		case LOADING_END:
			PromptDialog.dimissProgressDialog();
			break;
		}
	
	}
	
	public void onEventMainThread(Object event) {
		if(event instanceof DialogEvent){
			DialogEvent evt  =(DialogEvent) event;
			ProcessDialogEvent(this, evt, this.getClass().getName());
		}
	}
	
	
	public static boolean isTopActivity(Context context, String className)  
    {  
        boolean isTop = false;  
        ActivityManager am = (ActivityManager)context.getSystemService(ACTIVITY_SERVICE);  
        ComponentName cn = am.getRunningTasks(1).get(0).topActivity;  
        Log.d("TAG", "isTopActivity = " + cn.getClassName() + ", cur:" + className);  
        if (cn.getClassName().contains(className))  
        {  
            isTop = true;  
        }  
        Log.d("TAG", "isTop = " + isTop);  
        return isTop;  
    } 
	
}
