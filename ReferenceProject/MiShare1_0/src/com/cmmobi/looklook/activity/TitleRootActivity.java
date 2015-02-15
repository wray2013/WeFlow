package com.cmmobi.looklook.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Message;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;
import cn.zipper.framwork.core.ZActivity;

import com.cmmobi.looklook.R;
import com.cmmobi.looklook.common.utils.CmmobiClickAgentWrapper;

public abstract class TitleRootActivity extends ZActivity {

	@Override
	public boolean handleMessage(Message msg) {
		return false;
	}

	public abstract int subContentViewId();
	
	private FrameLayout rlyTitle;
	private FrameLayout rlyContent;
	private Button leftButton;
	private Button rightButton;
	private TextView title;
	public static final int TEXT_MARGIN_RIGHT = 10;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.fragment_title_root);
		
		rlyContent = (FrameLayout)findViewById(R.id.rly_fg_content);
		rlyTitle = (FrameLayout)findViewById(R.id.inc_title_bar);
		
		leftButton = (Button)findViewById(R.id.btn_title_left);
		rightButton = (Button)findViewById(R.id.btn_title_right);
		rightButton.setOnClickListener(this);
		leftButton.setOnClickListener(this);
		
		title = (TextView)findViewById(R.id.tv_title);	
		if(subContentViewId() != 0){
			LayoutInflater.from(this).inflate(subContentViewId() , rlyContent);
		}
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
	protected void onResume() {
		super.onResume();
		CmmobiClickAgentWrapper.onResume(this);
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		CmmobiClickAgentWrapper.onPause(this);
	}
	
	@Override
	protected void onStop() {
		super.onStop();
		CmmobiClickAgentWrapper.onStop(this);
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
		rlyTitle.setVisibility(View.GONE);
	}

	/**
	 * 显示titlebar
	 */
	protected void showTitlebar() {
		rlyTitle.setVisibility(View.VISIBLE);
	}
	/**
	 * 获取标题栏
	 * @return
	 */
	protected FrameLayout getTitleBar() {
		return rlyTitle;
	}
	/**
	 * 设置title
	 * @param str
	 */
	public void setTitle(String str) {
		title.setText(str);
	}
	
	public void setTitle(int resid) {
		title.setText(resid);
	}
	
	/**
	 * 隐藏左上角按钮(一般为返回)
	 */
	protected void hideLeftButton(){
		leftButton.setVisibility(View.INVISIBLE);
	}
	
	/**
	 * 隐藏右上角按钮
	 */
	protected void hideRightButton(){
		rightButton.setVisibility(View.INVISIBLE);
	}
	
	/**
	 * 显示左上角按钮
	 */
	protected void showLeftButton(){
		leftButton.setVisibility(View.VISIBLE);
	}
	
	/**
	 * 显示右上角按钮
	 */
	protected void showRightButton(){
		rightButton.setVisibility(View.VISIBLE);
	}
	
	protected void setRightButtonText(int resid){
		setRightButtonNoBackground();
		showRightButton();
		rightButton.setText(resid);
		setRightButtonPadding(TEXT_MARGIN_RIGHT);
	}
	protected void setRightButtonText(String s){
		setRightButtonNoBackground();
		showRightButton();
		rightButton.setText(s);
		setRightButtonPadding(TEXT_MARGIN_RIGHT);
	}
	
	private void setRightButtonPadding(int paddingRight) {
		DisplayMetrics dm = getResources().getDisplayMetrics();
		int marginRight = (int) (dm.density * paddingRight);
		FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT,FrameLayout.LayoutParams.WRAP_CONTENT);
		params.rightMargin = marginRight;
		params.gravity = Gravity.RIGHT | Gravity.CENTER_VERTICAL;
		rightButton.setLayoutParams(params);
	}
	
	protected void setLeftButtonText(String s){
		setLeftButtonNoBackground();
		showLeftButton();
		leftButton.setText(s);
		setLeftButtonPadding(TEXT_MARGIN_RIGHT);
	}
	
	private void setLeftButtonPadding(int paddingRight) {
		DisplayMetrics dm = getResources().getDisplayMetrics();
		int marginRight = (int) (dm.density * paddingRight);
		FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT,FrameLayout.LayoutParams.WRAP_CONTENT);
		params.leftMargin = marginRight;
		params.gravity = Gravity.LEFT | Gravity.CENTER_VERTICAL;
		leftButton.setLayoutParams(params);
	}
	
	protected void setRightButtonNoBackground(){
		rightButton.setBackgroundColor(Color.TRANSPARENT);
	}
	
	protected void setLeftButtonNoBackground(){
		leftButton.setBackgroundColor(Color.TRANSPARENT);
	}
	
	/**
	 * 右上角按钮
	 */
	protected Button getRightButton(){
		return rightButton;
	}
	
	/**
	 * 左上角按钮
	 */
	protected Button getLeftButton(){
		return leftButton;
	}
	
	protected void setLeftLong2Home(){
		leftButton.setOnLongClickListener(new View.OnLongClickListener() {
			@Override
			public boolean onLongClick(View v) {
				Intent intent = new Intent(TitleRootActivity.this,LookLookActivity.class);
				intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(intent);
				TitleRootActivity.this.finish();
				return false;
			}
		});
	}
	
	public void setLeftButton(OnClickListener listener){
		leftButton.setOnClickListener(listener);
	}
}
