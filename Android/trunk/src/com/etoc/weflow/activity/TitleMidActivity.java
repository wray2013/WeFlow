package com.etoc.weflow.activity;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Handler.Callback;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.etoc.weflow.R;
import com.etoc.weflow.utils.DisplayUtil;
import com.etoc.weflow.utils.ViewUtils;
import com.nostra13.universalimageloader.api.MyImageLoader;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;

public abstract class TitleMidActivity extends BaseActivity implements OnClickListener, Callback {


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
//		setContentView(R.layout.fragment_title_root);
		
		rlyContent = (FrameLayout)findViewById(R.id.rly_fg_content);
		rlyTitle = (RelativeLayout)findViewById(R.id.inc_title_bar);
		
		int paddingSize = DisplayUtil.getSize(this, 12);
		RelativeLayout.LayoutParams pm = (RelativeLayout.LayoutParams)rlyTitle.getLayoutParams();
		pm.height = DisplayUtil.getSize(this, 96);
		rlyTitle.setLayoutParams(pm);
		
		leftButton = (ImageButton)findViewById(R.id.btn_title_left);
		tvLeftBtn = (Button) findViewById(R.id.btn_title_left_tv);
		tvLeftBtn.setPadding(paddingSize, 0, paddingSize, 0);
		leftButton.setPadding(paddingSize, 0, paddingSize, 0);
		rightButton = (Button)findViewById(R.id.btn_title_right);
		ivRightButton = (ImageView)findViewById(R.id.iv_title_right);
	
		ViewUtils.setMarginRight(rightButton, 12);
		ViewUtils.setMarginRight(ivRightButton, 12);
		ViewUtils.setMarginLeft(tvLeftBtn, 12);
		rightButton.setOnClickListener(this);
		ivRightButton.setOnClickListener(this);
		leftButton.setOnClickListener(this);
		tvLeftBtn.setOnClickListener(this);
		
		title = (TextView)findViewById(R.id.tv_title);	
		title.setTextSize(DisplayUtil.textGetSizeSp(this, 45));
		title.setMaxWidth(DisplayUtil.getSize(this,480));
		iv_title = (ImageView)findViewById(R.id.iv_title);
		if(subContentViewId() != 0){
			LayoutInflater.from(this).inflate(subContentViewId() , rlyContent);
		}
		
		handler = new Handler(this);
	}
	

	@Override
	public int rootViewId() {
		// TODO Auto-generated method stub
		return R.layout.fragment_title_root;
	}
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_title_left:
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
		// 必不可少，否则所有的组件都不会有TouchEvent了
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
				// 点击的是输入框区域，保留点击EditText的事件
				return false;
			} else {
				return true;
			}
		}
		return false;
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
		if(rlyTitle!=null){
			rlyTitle.setVisibility(View.GONE);
		}

	}
	

	/**
	 * 显示titlebar
	 */
	protected void showTitlebar() {
		if(rlyTitle!=null){
			rlyTitle.setVisibility(View.VISIBLE);
		}

	}
	
	protected void setLeftButtonText(String s) {
		hideLeftButton();
		tvLeftBtn.setVisibility(View.VISIBLE);
		tvLeftBtn.setBackgroundColor(Color.TRANSPARENT);
		tvLeftBtn.setText(s);
		tvLeftBtn.setTextSize(DisplayUtil.textGetSizeSp(this, 33));
	}
	
	/**
	 * 获取标题栏
	 * @return
	 */
	protected RelativeLayout getTitleBar() {
		return rlyTitle;
	}
	
	/**
	 * 隐藏左上角按钮(一般为返回)
	 */
	protected void hideLeftButton(){
		if(leftButton!=null){
			leftButton.setVisibility(View.INVISIBLE);
		}

	}
	
	/**
	 * 隐藏右上角按钮
	 */
	protected void hideRightButton(){
		if(rightButton!=null){
			rightButton.setVisibility(View.INVISIBLE);
		}

		if(ivRightButton!=null){
			ivRightButton.setVisibility(View.GONE);
		}

	}
	
	/**
	 * 显示左上角按钮
	 */
	protected void showLeftButton(){
		if(leftButton!=null){
			leftButton.setVisibility(View.VISIBLE);
		}

	}
	
	/**
	 * 显示右上角按钮
	 */
	protected void showRightButton(){
		if(rightButton!=null){
			rightButton.setVisibility(View.VISIBLE);
		}

	}
	
	protected void showIvRightButton(){
		if(ivRightButton!=null){
			ivRightButton.setVisibility(View.VISIBLE);
		}

	}
	
	/**
	 * 右上角按钮
	 */
	public Button getRightButton(){
		return rightButton;
	}
	/**
	 * 左上角按钮
	 */
	protected ImageButton getLeftButton(){
		return leftButton;
	}
	
	protected TextView getTvTitle(){
		return title;
	}
	
	protected void setLeftButtonBackground(int resId){
		if(leftButton!=null){
			leftButton.setImageResource(resId);
		}

		
	}
	
	protected void setRightButtonBackground(int resId){
		if(rightButton!=null){
			rightButton.setBackgroundResource(resId);
			rightButton.setText(" ");
			RelativeLayout.LayoutParams lParams = (RelativeLayout.LayoutParams) rightButton.getLayoutParams();
			lParams.height = DisplayUtil.getSize(this, 72);
			lParams.width = lParams.height;
			rightButton.setLayoutParams(lParams);
		}

	}
	
	protected void setRightButtonUri(String path){
		if(ivRightButton!=null){
			MyImageLoader imageLoader = null;
			DisplayImageOptions imageLoaderOptions = null;
			imageLoader = MyImageLoader.getInstance();
			imageLoaderOptions = new DisplayImageOptions.Builder()
					.cacheInMemory(true)
					.cacheOnDisc()
					.imageScaleType(ImageScaleType.IN_SAMPLE_POWER_OF_2)
					.bitmapConfig(Bitmap.Config.RGB_565)
					.build();
			imageLoader.displayImage(path, ivRightButton, imageLoaderOptions);
			RelativeLayout.LayoutParams lParams = (RelativeLayout.LayoutParams) ivRightButton.getLayoutParams();
			lParams.height = DisplayUtil.getSize(this, 72);
			lParams.width = lParams.height*2;
			ivRightButton.setLayoutParams(lParams);
		}

		if(title!=null){
			RelativeLayout.LayoutParams lParams = (RelativeLayout.LayoutParams)title.getLayoutParams();
			lParams.width = DisplayUtil.getScreenWidth(this) - DisplayUtil.getSize(this, 72*4+20);
			title.setLayoutParams(lParams);
		}

	}
	
	protected void setRightButtonText(String s){
		if(rightButton!=null){
			rightButton.setBackgroundColor(Color.TRANSPARENT);
			rightButton.setText(s);
			rightButton.setTextSize(DisplayUtil.textGetSizeSp(this, 33));
			rightButton.setPadding(DisplayUtil.getSize(this, 20), DisplayUtil.getSize(this, 19), DisplayUtil.getSize(this, 19), DisplayUtil.getSize(this, 12));
		}

	}
	
	protected void setTitleBackground(int resId){
		if(title!=null){
			title.setBackgroundResource(resId);
			title.setText("");
			title.setCompoundDrawables(null, null, null, null); 
		}

	}
	
	protected void setTitleText(String str){
		if(title!=null){
			title.setBackgroundColor(Color.TRANSPARENT);
			title.setText(str);
		}

		if(iv_title!=null){
			iv_title.setVisibility(View.GONE); 
		}

	}
	
	protected void setTitleTextAndDrawable(String str, int resId){
		setTitleText(str);
		if(title!=null){
			title.setPadding(DisplayUtil.getSize(this, 6), DisplayUtil.getSize(this, 17), DisplayUtil.getSize(this, 6), DisplayUtil.getSize(this, 17));
			title.setOnClickListener(this);
		}
		
		if(iv_title!=null){
			iv_title.setPadding(0, DisplayUtil.getSize(this, 17), DisplayUtil.getSize(this, 17), DisplayUtil.getSize(this, 17));
			iv_title.setVisibility(View.VISIBLE);
			iv_title.setImageResource(resId);
			iv_title.setOnClickListener(this);	
		}

	}
	
}
