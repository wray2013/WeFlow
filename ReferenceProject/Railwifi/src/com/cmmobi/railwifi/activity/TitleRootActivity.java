package com.cmmobi.railwifi.activity;

import java.util.Timer;
import java.util.TimerTask;

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

import com.cmmobi.common.tools.SpHelper;
import com.cmmobi.railwifi.R;
import com.cmmobi.railwifi.dialog.PromptDialog;
import com.cmmobi.railwifi.event.DialogEvent;
import com.cmmobi.railwifi.smoothprogressbar.SmoothProgressBar;
import com.cmmobi.railwifi.utils.CmmobiClickAgentWrapper;
import com.cmmobi.railwifi.utils.ConStant;
import com.cmmobi.railwifi.utils.DateUtils;
import com.cmmobi.railwifi.utils.DisplayUtil;
import com.cmmobi.railwifi.utils.ViewUtils;
import com.nostra13.universalimageloader.api.MyImageLoader;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;

import de.greenrobot.event.EventBus;

public abstract class TitleRootActivity extends FragmentActivity implements OnClickListener, Callback {


	public abstract int subContentViewId();
	
	private RelativeLayout rlyTitle;
	private FrameLayout rlyContent;
	protected ImageButton leftButton;
	protected Button rightButton;
	protected ImageView ivRightButton;
	protected TextView title;
	private ImageView iv_title;
	protected Handler handler;
	protected SmoothProgressBar pbProgressBar;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.fragment_title_root);
		
		rlyContent = (FrameLayout)findViewById(R.id.rly_fg_content);
		rlyTitle = (RelativeLayout)findViewById(R.id.inc_title_bar);
		pbProgressBar = (SmoothProgressBar) findViewById(R.id.progressbar);
		
		pbProgressBar.setSmoothProgressDrawableInterpolator(new LinearInterpolator());
		pbProgressBar.setSmoothProgressDrawableColors(getResources().getIntArray(R.array.gplus_colors));
		pbProgressBar.setSmoothProgressDrawableStrokeWidth(DisplayUtil.getSize(this, 6));
		pbProgressBar.setSmoothProgressDrawableMirrorMode(true);
		pbProgressBar.setSmoothProgressDrawableUseGradients(true);
		pbProgressBar.setSmoothProgressDrawableSpeed(2.0f);
		pbProgressBar.setSmoothProgressDrawableProgressiveStartSpeed(2.0f);
		pbProgressBar.setSmoothProgressDrawableProgressiveStopSpeed(2.0f);
		pbProgressBar.setProgressiveStartActivated(false);
		
		int paddingSize = DisplayUtil.getSize(this, 12);
		RelativeLayout.LayoutParams pm = (RelativeLayout.LayoutParams)rlyTitle.getLayoutParams();
		pm.height = DisplayUtil.getSize(this, 96);
		rlyTitle.setLayoutParams(pm);
//		rlyTitle.setPadding(0, 0, paddingSize, 0);

		
		leftButton = (ImageButton)findViewById(R.id.btn_title_left);
		leftButton.setPadding(paddingSize, 0, paddingSize, 0);
		rightButton = (Button)findViewById(R.id.btn_title_right);
		ivRightButton = (ImageView)findViewById(R.id.iv_title_right);
	
		ViewUtils.setMarginRight(rightButton, 12);
		ViewUtils.setMarginRight(ivRightButton, 12);
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

		EventBus.getDefault().register(this);

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
	protected void onStop(){
		super.onStop();
		CmmobiClickAgentWrapper.onStop(this);
	}
	
	@Override
	protected void onDestroy(){
		super.onDestroy();
		EventBus.getDefault().unregister(this);
		PromptDialog.dismissDialog();
		PromptDialog.dimissProgressDialog();
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
		rlyTitle.clearAnimation();
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
		rlyTitle.startAnimation(animation1);
	}
	

	/**
	 * 显示titlebar
	 */
	protected void showTitlebar() {
		rlyTitle.clearAnimation();
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
		rlyTitle.startAnimation(animation1);
	}
	
	protected void showSmoothProgressBar() {
		pbProgressBar.setVisibility(View.VISIBLE);
		pbProgressBar.setProgressiveStartActivated(true);
	}
	
	protected void hideSmoothProgressBar() {
		pbProgressBar.progressiveStop();
		pbProgressBar.setVisibility(View.GONE);
	}
	/**
	 * 获取标题栏
	 * @return
	 */
	protected RelativeLayout getTitleBar() {
		return rlyTitle;
	}
	
	protected SmoothProgressBar getSmoothProgressBar() {
		return pbProgressBar;
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
		ivRightButton.setVisibility(View.GONE);
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
	
	protected void showIvRightButton(){
		ivRightButton.setVisibility(View.VISIBLE);
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
	
	protected void setRightButtonUri(String path){
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
		
		lParams = (RelativeLayout.LayoutParams)title.getLayoutParams();
		lParams.width = DisplayUtil.getScreenWidth(this) - DisplayUtil.getSize(this, 72*4+20);
		title.setLayoutParams(lParams);
	}
	
	protected void setRightButtonText(String s){
		rightButton.setBackgroundColor(Color.TRANSPARENT);
		rightButton.setText(s);
		rightButton.setTextSize(DisplayUtil.textGetSizeSp(this, 33));
		rightButton.setPadding(DisplayUtil.getSize(this, 20), DisplayUtil.getSize(this, 19), DisplayUtil.getSize(this, 19), DisplayUtil.getSize(this, 12));
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
		case CALL_HELP_DIALOG:
			String title = evt.getTitle();
			String content = evt.getContent();
			String ok = evt.getOK();
			final int notifyId = evt.getNotifyID();
			Log.v("======", "titleRoot - KEY_TYPE_DIALOG - title:" + title + ", content:" + content + ", notifyId:" + notifyId);
			if(title!=null && content!=null && !title.equals("") && !content.equals("")){
				PromptDialog.Dialog(context, true, true, false, title,
						content, "求助", "取消",
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog, int which) {
								// TODO Auto-generated method stub
								NotificationManager mNotificationManager =
									    (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
									
								if(notifyId>0){
									mNotificationManager.cancel(notifyId);
								}
								
								Intent intent = new Intent(context,
										CallForHelpActivity.class);
								intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
								context.startActivity(intent);
							}
						}, new DialogInterface.OnClickListener() {
							
							@Override
							public void onClick(DialogInterface dialog, int which) {
								// TODO Auto-generated method stub
								NotificationManager mNotificationManager =
									    (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
									
								if(notifyId>0){
									mNotificationManager.cancel(notifyId);
								}
							}
						}, true, MainActivity.railway_name);
			}
			
			break;
//		case PROMPT:
//			String title2 = evt.getTitle();
//			String content2 = evt.getContent();
//			String ok2 = evt.getOK();
//			PromptDialog.Dialog(context, false, title2, content2, ok2, null, null, null);
//			break;
		case DOWNLOAD:
			final String title3 = evt.getTitle();
			final String content3 = evt.getContent();
			final String url3 = evt.getUrl();
			PromptDialog.Dialog(context, true, true, title3, content3, new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub
					DownloadManager downloadManager = (DownloadManager)context.getSystemService(Activity.DOWNLOAD_SERVICE);
					String apkUrl = url3;
					if(apkUrl==null || apkUrl.equals("")){
						Toast.makeText(context, "下载链接无效", Toast.LENGTH_LONG).show();
						return;
					}
					
					try{
						DownloadManager.Request request = new DownloadManager.Request(Uri.parse(apkUrl));
						request.setDestinationInExternalPublicDir(ConStant.SD_STORAGE_ROOT + "/apks", title3 + ".apk");
						request.setVisibleInDownloadsUi(false);
						request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
						request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI);
						long id = downloadManager.enqueue(request);						
					}catch(Exception e){
						e.printStackTrace();
					}

				}
			});
			break;
		case UPDATE_FORCE_DIALOG_ALWAYS:
			final String title4 = evt.getTitle();
			final String content4 = evt.getContent();
			final String url4 = evt.getUrl();
			PromptDialog.Dialog(context, false, false, true, title4,
					content4, "立即下载", null, 
			new DialogInterface.OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub
					SettingActivity.getApkUrl(context, url4);
				}
			}, null, true, null);
			break;
		case UPDATE_FORCE_DIALOG_DISMISS:
			final String title5 = evt.getTitle();
			final String content5 = evt.getContent();
			final String url5 = evt.getUrl();
			PromptDialog.Dialog(context, false, true, false, title5,
					content5, "立即下载", null, 
			new DialogInterface.OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub
					SettingActivity.getApkUrl(context, url5);
				}
			}, null, true, null);
			break;
		case UPDATE_NORMAL_DIALOG:
			final String title6 = evt.getTitle();
			final String content6 = evt.getContent();
			final String url6 = evt.getUrl();
			PromptDialog.Dialog(context, true, true, false, title6, content6,
			"立即下载", "忽略", new DialogInterface.OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub
					SettingActivity.getApkUrl(context, url6);
				}
			}, null, true, null);
			String curDate = DateUtils.getDayString();
	        SpHelper.setEditor(context, SettingActivity.PROMT_DATE, curDate);
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
