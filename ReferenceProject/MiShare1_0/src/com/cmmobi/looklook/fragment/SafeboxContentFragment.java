package com.cmmobi.looklook.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Message;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.LocalBroadcastManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;

import com.cmmobi.looklook.R;
import com.cmmobi.looklook.R.color;
import com.cmmobi.looklook.activity.LookLookActivity;
import com.cmmobi.looklook.activity.SettingGesturePwdActivity;
import com.cmmobi.looklook.common.service.CoreService;

/**
 * @author wuxiang
 * @email wuxiang@cmmobi.com
 * @date 2013-12-25
 */
public class SafeboxContentFragment extends XFragment implements
		OnClickListener {

	private View contentView;
	private View vTitle;
	private View vTitleChecked;
	private SafeboxSubFragment currFragment;
	
	private Button btnSafeboxMe;
	private Button btnSafeboxVShare;
	private View pushDianMe;
	private View pushDianVshare;
	
	private View btnDelete;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		contentView = inflater.inflate(R.layout.activity_safebox, null);
		contentView.findViewById(R.id.btn_menu).setOnClickListener(this);
		btnSafeboxMe=(Button) contentView.findViewById(R.id.btn_safebox_me);
		btnSafeboxMe.setOnClickListener(this);
		btnSafeboxVShare=(Button) contentView.findViewById(R.id.btn_safebox_vshare);
		btnSafeboxVShare.setOnClickListener(
				this);
		vTitle = contentView.findViewById(R.id.ll_title);
		vTitleChecked = contentView.findViewById(R.id.ll_title_checked);
		contentView.findViewById(R.id.btn_back_checked)
				.setOnClickListener(this);
		contentView.findViewById(R.id.btn_remove_safebox).setOnClickListener(
				this);
		btnDelete=contentView.findViewById(R.id.btn_delete);
		btnDelete.setOnClickListener(this);
		pushDianMe=contentView.findViewById(R.id.safebox_mask_me);
		pushDianVshare=contentView.findViewById(R.id.safebox_mask_vshare);
		enterSafeboxMe();
		IntentFilter filter=new IntentFilter();
		filter.addAction(CoreService.ACTION_MESSAGE_DATA_UPDATE);
		filter.addAction(LookLookActivity.UPDATE_MASK);
		receiver=new myReceiver();
		LocalBroadcastManager.getInstance(getActivity()).registerReceiver(receiver, filter);
//		updateDianMe(true);
//		updateDianVshare(true);
		updateMasks();
		return contentView;
	}
	
	//显示我 角标
	private void updateDianMe(boolean isShow){
		if(isShow){
			pushDianMe.setVisibility(View.VISIBLE);
		}else{
			pushDianMe.setVisibility(View.GONE);
		}
	}
	
	//显示微享角标
	private void updateDianVshare(boolean isShow){
		if(isShow){
			pushDianVshare.setVisibility(View.VISIBLE);
		}else{
			pushDianVshare.setVisibility(View.GONE);
		}
	}
	
	//更新角标
	private void updateMasks(){
		//TODO 角标显示逻辑
		if(currFragment instanceof SafeboxMeFragment){
			//当前正在我 列表，有角标时，不显示
			int unReadComments=accountInfo.privateMsgManger.getUnReadSafeboxCommentNum();
			if(unReadComments>0){
				updateDianMe(false);
				FragmentHelper.getInstance(getActivity()).getSafeboxMeFragment().updateUnreadMsg(unReadComments);
			}else{
				updateDianMe(false);
				FragmentHelper.getInstance(getActivity()).getSafeboxMeFragment().updateUnreadMsg(0);
				
			}
			if(accountInfo.new_safeboxmicnum>0){
				updateDianVshare(true);
			}else{
				updateDianVshare(false);
			}
		}else{
			//当前正在微享 列表，有角标时，不显示
			int unReadComments=accountInfo.privateMsgManger.getUnReadSafeboxCommentNum();
			if(unReadComments>0){
				updateDianMe(true);
				FragmentHelper.getInstance(getActivity()).getSafeboxMeFragment().updateUnreadMsg(unReadComments);
			}else{
				updateDianMe(false);
				FragmentHelper.getInstance(getActivity()).getSafeboxMeFragment().updateUnreadMsg(0);
				
			}
			if(accountInfo.new_safeboxmicnum>0){
				updateDianVshare(false);
				FragmentHelper.getInstance(getActivity()).getSafeboxVShareFragment().updateViews(null);
			}else{
				updateDianVshare(false);
			}
		}
	}

	private boolean isCheckedShow;

	public void hideNormalTitle() {
		if (vTitle.getVisibility() == View.VISIBLE)
			vTitle.setVisibility(View.GONE);
	}

	public void showNormalTitle() {
		if (vTitle.getVisibility() == View.GONE)
			vTitle.setVisibility(View.VISIBLE);
		if (vTitleChecked.getVisibility() == View.VISIBLE)
			vTitleChecked.setVisibility(View.GONE);
		isCheckedShow = false;
	}

	/**
	 * 显示日记选中后标题栏
	 */
	public void showCheckedTitle() {
		vTitle.setVisibility(View.GONE);
		vTitleChecked.setVisibility(View.VISIBLE);
		isCheckedShow = true;
	}

	/**
	 * 判断当前选中标题栏是否显示 true-显示 false-未显示
	 */
	public boolean isCheckedTitleShow() {
		return isCheckedShow;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_menu:
			showMenu();
			break;
		case R.id.btn_back_checked:
			showNormalTitle();
			currFragment.purgeCheckedView();
			break;
		case R.id.btn_remove_safebox:
			currFragment.removeSafebox();
			break;
		case R.id.btn_delete:
			currFragment.delete();
			break;
		case R.id.btn_safebox_me:
			selectMe();
			btnDelete.setVisibility(View.VISIBLE);
			enterSafeboxMe();
			showNormalTitle();
			currFragment.purgeCheckedView();
			updateDianMe(false);
			break;
		case R.id.btn_safebox_vshare:
			selectVShare();
			btnDelete.setVisibility(View.GONE);
			enterSafeboxVShare();
			showNormalTitle();
			currFragment.purgeCheckedView();
			FragmentHelper.getInstance(getActivity())
			.getSafeboxVShareFragment().updateViews(null);
			accountInfo.new_safeboxmicnum=0;
			updateDianVshare(false);
			break;
		default:
			break;
		}
	}
	
	private void selectMe(){
		btnSafeboxMe.setBackgroundResource(R.drawable.qiehuan1_2);
		btnSafeboxMe.setTextColor(Color.WHITE);
		
		btnSafeboxVShare.setBackgroundResource(0);
		btnSafeboxVShare.setTextColor(Color.parseColor("#0C80FF"));
	}
	
	private void selectVShare(){
		btnSafeboxVShare.setBackgroundResource(R.drawable.qiehuan1_3);
		btnSafeboxVShare.setTextColor(Color.WHITE);
		
		btnSafeboxMe.setBackgroundResource(0);
		btnSafeboxMe.setTextColor(Color.parseColor("#0C80FF"));
	}

	// 进入保险箱-我
	public void enterSafeboxMe() {
		switchContent(FragmentHelper.getInstance(getActivity())
				.getSafeboxMeFragment());
	}

	// 进入保险箱-微享
	public void enterSafeboxVShare() {
		switchContent(FragmentHelper.getInstance(getActivity())
				.getSafeboxVShareFragment());
	}

	/**
	 * 切换内容区fragment
	 */
	public void switchContent(final SafeboxSubFragment fragment) {
		if (null == fragment)
			return;
		FragmentTransaction ft = getActivity().getSupportFragmentManager()
				.beginTransaction();
		if (currFragment != null)
			ft.hide(currFragment);
		currFragment = fragment;
		if (null == getActivity().getSupportFragmentManager()
				.findFragmentByTag(fragment.getClass().getName())) {
			ft.add(R.id.safebox_content, fragment, fragment.getClass()
					.getName());
		} else {
			ft.show(fragment);
		}
		ft.commitAllowingStateLoss();
	}

	@Override
	public boolean handleMessage(Message msg) {
		// TODO Auto-generated method stub
		return false;
	}
	
	private myReceiver receiver;
	private class myReceiver extends BroadcastReceiver{

		@Override
		public void onReceive(Context context, Intent intent) {
			String action=intent.getAction();
			String param=intent.getStringExtra(SettingGesturePwdActivity.ACTION_PARAM);
			if(CoreService.ACTION_MESSAGE_DATA_UPDATE.equals(action)){//角标
				updateMasks();
			}else if(LookLookActivity.UPDATE_MASK.equals(action)){//角标
				updateMasks();
			}else if(LookLookActivity.CONTACTS_REFRESH_DATA.equals(action)){
				updateMasks();
			}
		}
	}

	@Override
	public void onDestroyView() {
		LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(receiver);
		super.onDestroyView();
	}
}
