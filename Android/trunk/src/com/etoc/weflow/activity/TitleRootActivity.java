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
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;

import com.etoc.weflow.R;
import com.etoc.weflow.utils.DisplayUtil;
import com.etoc.weflow.utils.ViewUtils;
import com.nostra13.universalimageloader.api.MyImageLoader;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;

public abstract class TitleRootActivity extends BaseActivity implements OnClickListener, Callback {

	private RelativeLayout rlyTitle;
	private FrameLayout rlyContent;
	protected ImageButton leftButton;
	protected Button tvLeftBtn;
	protected Button rightButton;
	protected ImageView ivRightButton;
	protected TextView title;
	private ImageView iv_title;
	protected Handler handler;
	public final static int GRAVITE_CENTER = 0;
	public final static int GRAVITE_LEFT = 1;
	public final static int GRAVITE_RIGHT = 2;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
//		setContentView(R.layout.fragment_title_root);
		
		rlyContent = (FrameLayout)findViewById(R.id.rly_fg_content);
		rlyTitle = (RelativeLayout)findViewById(R.id.inc_title_bar);
		
		int paddingSize = DisplayUtil.getSize(this, 12);
		RelativeLayout.LayoutParams pm = (RelativeLayout.LayoutParams)rlyTitle.getLayoutParams();
		pm.height = DisplayUtil.getSize(this, 112);
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
		
		RelativeLayout.LayoutParams rlParams = (LayoutParams) title.getLayoutParams();
		switch (graviteType()) {
		case GRAVITE_CENTER:
			rlParams.addRule(RelativeLayout.CENTER_IN_PARENT);
			title.setLayoutParams(rlParams);
			break;
		case GRAVITE_LEFT:
			rlParams.addRule(RelativeLayout.CENTER_VERTICAL | RelativeLayout.ALIGN_PARENT_LEFT);
			rlParams.addRule(RelativeLayout.RIGHT_OF, R.id.btn_title_left);
			title.setLayoutParams(rlParams);
			ViewUtils.setMarginLeft(title, 42);
			break;
		case GRAVITE_RIGHT:
			rlParams.addRule(RelativeLayout.CENTER_VERTICAL | RelativeLayout.ALIGN_PARENT_RIGHT);
			rlParams.addRule(RelativeLayout.LEFT_OF, R.id.btn_title_left);
			title.setLayoutParams(rlParams);
			ViewUtils.setMarginRight(title, 42);
			break;

		}
		
		
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
	
	protected int graviteType() {
		return GRAVITE_CENTER;
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
