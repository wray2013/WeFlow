package com.cmmobi.looklook.fragment;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.cmmobi.looklook.R;
import com.cmmobi.looklook.activity.LookLookActivity;
import com.cmmobi.looklook.activity.TitleRootActivity;
import com.cmmobi.looklook.activity.TitleRootFragmentActivity;
import com.cmmobi.looklook.common.view.FriendsExpressionView;
import com.cmmobi.looklook.info.profile.AccountInfo;
import com.cmmobi.looklook.info.profile.ActiveAccount;


/**
 * fragment的父类，统一处理了title
 * @author guoyang
 */
public abstract class TitleRootFragment extends XFragment implements View.OnClickListener{

	private FrameLayout rlyTitle;
	private FrameLayout rlyContent;
	private Button leftButton;
	private Button rightButton;
	private TextView title;
	
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
	}
	
	public abstract int subContentViewId();
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		
		View v = inflater.inflate(R.layout.fragment_title_root, null);
		rlyContent = (FrameLayout) v.findViewById(R.id.rly_fg_content);
		rlyTitle = (FrameLayout) v.findViewById(R.id.inc_title_bar);
		
		leftButton = (Button) v.findViewById(R.id.btn_title_left);
		rightButton = (Button) v.findViewById(R.id.btn_title_right);
		rightButton.setOnClickListener(this);
		leftButton.setOnClickListener(this);
		
		title = (TextView) v.findViewById(R.id.tv_title);	
		if(subContentViewId() != 0){
			inflater.inflate(subContentViewId() , rlyContent);
		}
		
		return v;
	}
	
	@Override
	public void onClick(View v) {
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
	protected void setTitle(String str) {
		//title.setText(str);
		FriendsExpressionView.replacedExpressions(str, title);
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
		rightButton.setText(resid);
		setRightButtonPadding(TitleRootActivity.TEXT_MARGIN_RIGHT);
	}
	protected void setRightButtonText(String s){
		setRightButtonNoBackground();
		rightButton.setText(s);
		setRightButtonPadding(TitleRootActivity.TEXT_MARGIN_RIGHT);
	}
	protected void setRightButtonNoBackground(){
		rightButton.setBackgroundColor(Color.TRANSPARENT);
	}
	
	protected void setLeftButtonText(int resid){
		setLeftButtonNoBackground();
		leftButton.setText(resid);
		setLeftButtonPadding(TitleRootActivity.TEXT_MARGIN_RIGHT);
	}
	protected void setLeftButtonText(String s){
		setLeftButtonNoBackground();
		leftButton.setText(s);
		setLeftButtonPadding(TitleRootActivity.TEXT_MARGIN_RIGHT);
	}
	protected void setLeftButtonNoBackground(){
		leftButton.setBackgroundColor(Color.TRANSPARENT);
	}
	
	private void setRightButtonPadding(int paddingRight) {
		DisplayMetrics dm = getResources().getDisplayMetrics();
		int marginRight = (int) (dm.density * paddingRight);
		FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT,FrameLayout.LayoutParams.WRAP_CONTENT);
		params.rightMargin = marginRight;
		params.gravity = Gravity.RIGHT | Gravity.CENTER_VERTICAL;
		rightButton.setLayoutParams(params);
	}
	
	private void setLeftButtonPadding(int paddingRight) {
		DisplayMetrics dm = getResources().getDisplayMetrics();
		int marginRight = (int) (dm.density * paddingRight);
		FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT,FrameLayout.LayoutParams.WRAP_CONTENT);
		params.leftMargin = marginRight;
		params.gravity = Gravity.LEFT | Gravity.CENTER_VERTICAL;
		leftButton.setLayoutParams(params);
	}
	
	
	/**
	 * 绑定完成后保存
	 */
	protected void saveAccount(){
		ActiveAccount.getInstance(this.getActivity()).persist();
		String UID = ActiveAccount.getInstance(this.getActivity()).getUID();
		if (UID != null) {
			AccountInfo.getInstance(UID).persist();
		}
	}
	
	protected void setLeftLong2Home(){
		leftButton.setOnLongClickListener(new View.OnLongClickListener() {
			@Override
			public boolean onLongClick(View v) {
				Intent intent = new Intent(TitleRootFragment.this.getActivity(),LookLookActivity.class);
				intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(intent);
				TitleRootFragment.this.getActivity().finish();
				return false;
			}
		});
	}
	
}
