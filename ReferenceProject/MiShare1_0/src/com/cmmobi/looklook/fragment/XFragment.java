package com.cmmobi.looklook.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import cn.zipper.framwork.core.ZApplication;

import com.cmmobi.looklook.activity.LookLookActivity;
import com.cmmobi.looklook.activity.SettingGesturePwdActivity;
import com.cmmobi.looklook.activity.SettingToCreateGestureActivity;
import com.cmmobi.looklook.activity.SlidingActivity;
import com.cmmobi.looklook.info.profile.AccountInfo;
import com.cmmobi.looklook.info.profile.ActiveAccount;
import com.cmmobi.looklook.info.profile.DiaryManager;
import com.cmmobi.looklook.info.profile.LoginSettingManager;

/**
 * @author wuxiang
 * @email wuxiang@cmmobi.com
 * @date  2013-11-6
 */
public abstract class XFragment<T> extends Fragment implements Callback{

	private static final String TAG=XFragment.class.getSimpleName();
	protected Handler handler;
	protected String userID;
	protected AccountInfo accountInfo;
	protected DiaryManager diaryManager;
	private LoginSettingManager lsm;
	
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		handler = new Handler(this);
		initUserInfo();
	}
	
	public void initUserInfo(){
		userID = ActiveAccount.getInstance(ZApplication.getInstance()).getUID();
		accountInfo = AccountInfo.getInstance(userID);
		diaryManager =DiaryManager.getInstance();
		lsm = accountInfo.setmanager;
	}

	public Handler getHandler() {
		return handler;
	}
	
	/**
	 * 当fragment有数据需要更新时,子类可重载此函数
	 */
	public void updateViews(T data){
		
	}
	
	/**
	 * 判断保险箱是否创建 true-已创建 false 未创建
	 */
	public boolean safeboxIsCreated(){
		//判断保险箱是否创建
		return lsm.getGesturepassword() != null;
	}
	
	/**
	 * 进入创建保险箱界面
	 */
	public void startSafeboxCreateActivity(String param){
		Intent intent = new Intent(getActivity(), SettingToCreateGestureActivity.class);
		if(param!=null)
			intent.putExtra(SettingGesturePwdActivity.ACTION_PARAM, param);
		lsm.setIsFromSetting(false);
		startActivity(intent);
	}
	
	/**
	 * 进入输入保险箱密码界面
	 */
	public void startSafeboxPWDActivity(String param){
		Intent intent = new Intent(getActivity(), SettingGesturePwdActivity.class);
		if(param!=null)
			intent.putExtra(SettingGesturePwdActivity.ACTION_PARAM, param);
		lsm.setIsFromSetting(false);
		intent.putExtra("count", 0);
		startActivity(intent);
	}
	
	private boolean saveboxIsOpen(){
		if (lsm.getGesturepassword() == null) {
			// 未创建
			return true;
		} else if (lsm.getSafeIsOn()) {
			// 已创建且打开
			return true;
		} else {
			// 已创建但关闭
			return false;
		}
	}
	
	/**
	 * 当fragment有数据需要更新时,子类可重载此函数
	 */
	public void stopCallBack(T data){
		
	}
	
	/**
	 * 切换内容区fragment
	 */
	public void switchContent(final XFragment fragment) {
		if (getActivity() == null)
			return;
		if(getActivity() instanceof LookLookActivity){
			LookLookActivity myZoneActivity = (LookLookActivity) getActivity();
			myZoneActivity.switchContent(fragment);
		}else{
			Log.e(TAG, "switchContent error getActivity="+getActivity());
		}
	}	
	
	/**
	 * 显示菜单栏
	 */
	protected void showMenu(){
		if (getActivity() == null)
			return;
		SlidingActivity slidingActivity = (SlidingActivity) getActivity();
		slidingActivity.showMenu();
	}
	
	/**
	 * 显示内容区
	 */
	protected void showContent(){
		if (getActivity() == null)
			return;
		SlidingActivity slidingActivity = (SlidingActivity) getActivity();
		slidingActivity.showContent();
	}

	@Override
	public boolean handleMessage(Message msg) {
		// TODO Auto-generated method stub
		return false;
	}
	
}
