package com.cmmobi.looklook.fragment;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Message;
import android.support.v4.content.LocalBroadcastManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.View.OnClickListener;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.RelativeLayout;
import android.widget.TextView;
import cn.zipper.framwork.core.ZBroadcastReceiver;

import com.cmmobi.looklook.R;
import com.cmmobi.looklook.activity.SettingGesturePwdActivity;
import com.cmmobi.looklook.common.utils.BitmapUtils;
import com.cmmobi.looklook.info.profile.ActiveAccount;
import com.cmmobi.looklook.networktask.NetworkTaskManager;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;

/**
 * @author wuxiang
 * @email wuxiang@cmmobi.com
 * @date  2013-11-6
 */
public class MenuFragment extends XFragment<String> implements OnClickListener {
	
	private static final String TAG=MyZoneFragment.class.getSimpleName();
	private GridView gv;
	private View contentView;
	private TextView tvTasks;//正在进行的任务
	private RelativeLayout rlTasks;
	private ImageView[] ivBtnImg=new ImageView[6];//按钮图片
	private View[] vButtons=new View[6];//按钮
	private View[] ivMarks=new View[6];//角标
	private TextView[] tvMarksNum=new TextView[6];//角标显示数字
	private ImageView ivHead;
	
	private DisplayImageOptions options;
	protected ImageLoader imageLoader;
	
	private UpdateNetworkTaskReceiver updateNetworkTaskReceiver;
	private FragmentAutoSwitchReceiver fragmentAutoSwitchReceiver;
	private DisplayMetrics dm = null;
	private boolean isHeadShow = false;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		contentView=inflater.inflate(R.layout.fragment_menu, null);
		rlTasks = (RelativeLayout) contentView.findViewById(R.id.rl_enterTask);//.setOnClickListener(this);
		tvTasks = (TextView) contentView.findViewById(R.id.tv_enterTask);
		rlTasks.setOnClickListener(this);
		rlTasks.setVisibility(View.GONE);
		ivHead=(ImageView) contentView.findViewById(R.id.iv_head);
		ivHead.setOnClickListener(this);
		ImageView bghead = (ImageView) contentView.findViewById(R.id.iv_head_background);
		dm = getResources().getDisplayMetrics();
		int width = 152 * dm.widthPixels / 640;
		RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(width,width);
		bghead.setLayoutParams(params);
        initButtons();
        setTask();
//        gv=(GridView)contentView.findViewById(R.id.feature_list);
//        gv.setOnItemClickListener(this);
//        gv.setAdapter(new GridViewAdapter(getActivity().getApplicationContext()));
        
        IntentFilter intentfilter = new IntentFilter();
        intentfilter.addAction(NetworkTaskManager.ACTION_UPDATE_NETWORKTASK);
		intentfilter.addAction(ACTION_SAFEBOX_MODE_CHANGED);
        updateNetworkTaskReceiver = new UpdateNetworkTaskReceiver();
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(updateNetworkTaskReceiver, intentfilter);
        
        IntentFilter intentfilter2 = new IntentFilter();
        intentfilter2.addAction(ACTION_GOTO_SETTINGS_FRAGMENT);
        fragmentAutoSwitchReceiver = new FragmentAutoSwitchReceiver();
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(fragmentAutoSwitchReceiver, intentfilter2);
        
        int roundpx = dm.widthPixels * 3 / 640;
        
        imageLoader = ImageLoader.getInstance();
		//if(!imageLoader.isInited())
		//	imageLoader.init(ImageLoaderConfiguration.createDefault(getActivity()));
		options = new DisplayImageOptions.Builder()
		.showStubImage(R.drawable.kongjian_morentouxiang)
		.showImageForEmptyUri(R.drawable.kongjian_morentouxiang)
		.showImageOnFail(R.drawable.kongjian_morentouxiang)
		.cacheInMemory(true)
		.cacheOnDisc(true)
		.displayer(new RoundedBitmapDisplayer(roundpx <= 0 ? 3 : roundpx))
//		.displayer(new CircularBitmapDisplayer())
		.build();
		
		ViewTreeObserver vto2 = ivHead.getViewTreeObserver();   
		vto2.addOnGlobalLayoutListener(new OnGlobalLayoutListener() { 
			@Override   
			public void onGlobalLayout() {
				if (!isHeadShow) {
					updateHead();
					isHeadShow = true;
				}
			}   
		});
		
		updateHead();
		updatePrivateMsgMark(0);
		updateContactMark(0);
		updateFriendsRecentMark(0);
		//updateContactMark(15);
		return contentView;
	}
	
	/**
	 * 更新头像
	 */
	public void updateHead(){
		if(null==contentView)return;
		if(accountInfo.headimageurl!=null)
			imageLoader.displayImageEx(accountInfo.headimageurl, ivHead, options, null, userID, 0);
	}
	
	/**
	 * 显示私信角标
	 */
	public void updatePrivateMsgMark(int num){
		if(null==contentView)return;
		Log.d(TAG, "updatePrivateMsgMark num="+num);
		if(num>0){
			ivMarks[0].setVisibility(View.VISIBLE);
			tvMarksNum[0].setText(num+"");
			if(num>9){
				ivMarks[0].setBackgroundResource(R.drawable.jiaobiao_2);
			}else{
				ivMarks[0].setBackgroundResource(R.drawable.jiaobiao_1);
			}
			if(num>99){
				tvMarksNum[0].setText("99+");
			}
		}else{
			ivMarks[0].setVisibility(View.GONE);
		}
	}
	
	/**
	 * 显示好友动态角标
	 */
	public void updateFriendsRecentMark(int num){
		if(null==contentView)return;
		Log.d(TAG, "updateFriendsRecentMark num=="+num);
		if(num>0){
			ivMarks[1].setVisibility(View.VISIBLE);
			ivMarks[1].setBackgroundResource(R.drawable.push_dian);
			tvMarksNum[1].setVisibility(View.GONE);
//			tvMarksNum[1].setText(num+"");
//			if(num>9){
//				ivMarks[1].setBackgroundResource(R.drawable.jiaobiao_2);
//			}else{
//				ivMarks[1].setBackgroundResource(R.drawable.jiaobiao_1);
//			}
//			if(num>99){
//				tvMarksNum[1].setText("99+");
//			}
		}else{
			ivMarks[1].setVisibility(View.GONE);
		}
	}
	
	/**
	 * 显示通讯录角标
	 */
	public void updateContactMark(int num){
		if(null==contentView)return;
		Log.d(TAG, "updateContactMark num="+num);
		if(num>0){
			ivMarks[3].setVisibility(View.VISIBLE);
			tvMarksNum[3].setVisibility(View.VISIBLE);
			tvMarksNum[3].setText(num+"");
			if(num>9){
				ivMarks[3].setBackgroundResource(R.drawable.jiaobiao_2);
			}else{
				ivMarks[3].setBackgroundResource(R.drawable.jiaobiao_1);
			}
			if(num>99){
				tvMarksNum[3].setText("99+");
			}
		}else{
			ivMarks[3].setVisibility(View.GONE);
		}
	}
	
	private void initButtons(){
		int height = 205 * dm.heightPixels / 1920;
		LinearLayout.LayoutParams params = new LayoutParams(LayoutParams.FILL_PARENT,height);
		vButtons[0]=contentView.findViewById(R.id.btn_private_msg);
		vButtons[0].setLayoutParams(params);
		vButtons[0].setOnClickListener(this);
		ivBtnImg[0]=(ImageView) vButtons[0].findViewById(R.id.iv_pic);
		ivBtnImg[0].setImageResource(images[0]);
		ivMarks[0]=vButtons[0].findViewById(R.id.ll_menu_mask);
		tvMarksNum[0]=(TextView) vButtons[0].findViewById(R.id.tv_menu_mask);
		
		vButtons[1]=contentView.findViewById(R.id.btn_friends_recent);
		vButtons[1].setLayoutParams(params);
		vButtons[1].setOnClickListener(this);
		ivBtnImg[1]=(ImageView) vButtons[1].findViewById(R.id.iv_pic);
		ivBtnImg[1].setImageResource(images[1]);
		ivMarks[1]=vButtons[1].findViewById(R.id.ll_menu_mask);
		tvMarksNum[1]=(TextView) vButtons[1].findViewById(R.id.tv_menu_mask);
		
		vButtons[2]=contentView.findViewById(R.id.btn_invite);
		vButtons[2].setLayoutParams(params);
		vButtons[2].setOnClickListener(this);
		ivBtnImg[2]=(ImageView) vButtons[2].findViewById(R.id.iv_pic);
		ivBtnImg[2].setImageResource(images[2]);
		ivMarks[2]=vButtons[2].findViewById(R.id.ll_menu_mask);
		ivMarks[2].setVisibility(View.GONE);
		tvMarksNum[2]=(TextView) vButtons[2].findViewById(R.id.tv_menu_mask);
		
		vButtons[3]=contentView.findViewById(R.id.btn_contact);
		vButtons[3].setLayoutParams(params);
		vButtons[3].setOnClickListener(this);
		ivBtnImg[3]=(ImageView) vButtons[3].findViewById(R.id.iv_pic);
		ivBtnImg[3].setImageResource(images[3]);
		ivMarks[3]=vButtons[3].findViewById(R.id.ll_menu_mask);
		tvMarksNum[3]=(TextView) vButtons[3].findViewById(R.id.tv_menu_mask);
		
		vButtons[4]=contentView.findViewById(R.id.btn_safebox);
		vButtons[4].setLayoutParams(params);
		vButtons[4].setOnClickListener(this);
		ivBtnImg[4]=(ImageView) vButtons[4].findViewById(R.id.iv_pic);
		ivBtnImg[4].setImageResource(images[4]);
		ivMarks[4]=vButtons[4].findViewById(R.id.ll_menu_mask);
		ivMarks[4].setVisibility(View.GONE);
		tvMarksNum[4]=(TextView) vButtons[4].findViewById(R.id.tv_menu_mask);
		
		vButtons[5]=contentView.findViewById(R.id.btn_settings);
		vButtons[5].setLayoutParams(params);
		vButtons[5].setOnClickListener(this);
		ivBtnImg[5]=(ImageView) vButtons[5].findViewById(R.id.iv_pic);
		ivBtnImg[5].setImageResource(images[5]);
		ivMarks[5]=vButtons[5].findViewById(R.id.ll_menu_mask);
		ivMarks[5].setVisibility(View.GONE);
		tvMarksNum[5]=(TextView) vButtons[5].findViewById(R.id.tv_menu_mask);
	}
	
	@Override
	public void updateViews(String data) {
		super.updateViews(data);
		//TODO 
	}
	
	/*@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int position, long id) {
		switch (position) {
		case 0://首页
			Log.d(TAG, "首页");
			switchContent(FragmentHelper.getInstance(getActivity()).getZoneBaseFragment());
			break;
		case 1://评论
			Log.d(TAG, "评论");
			switchContent(FragmentHelper.getInstance(getActivity()).getCommentsFragment());
			break;
		case 2://通讯录
			Log.d(TAG, "通讯录");
			startActivity(new Intent(getActivity(), FriendsActivity.class));
			break;
		case 3://邀请好友
			Log.d(TAG, "邀请好友");
			switchContent(FragmentHelper.getInstance(getActivity()).getFriendsContactsFragment());
			break;
		case 4://收藏
			Log.d(TAG, "收藏");
			switchContent(FragmentHelper.getInstance(getActivity()).getCollectionsFragment());
			break;
		case 5://活动
			Log.d(TAG, "活动");
			switchContent(FragmentHelper.getInstance(getActivity()).getActivityListFragment());
			break;
		case 6://私信
			Log.d(TAG, "私信");
			switchContent(FragmentHelper.getInstance(getActivity()).getFriendsMessageFragment());
			break;
		case 7://保险箱
			Log.d(TAG, "保险箱");
			if(safeboxIsCreated()){
				startSafeboxPWDActivity(PARAM);
			}else{
				//TODO 启动创建保险箱流程
				startSafeboxCreateActivity(PARAM);
			}
			break;
		case 8://设置
			Log.d(TAG, "设置");
			switchContent(FragmentHelper.getInstance(getActivity()).getSettingFragment());
			//startActivity(new Intent(getActivity(), SettingActivity.class));
			break;
		case 9://帮助
			Log.d(TAG, "帮助");
			break;

		default:
			break;
		}
		
	}*/

	public boolean isShow;
    @Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_private_msg:
			Log.d(TAG, "消息");
			switchContent(FragmentHelper.getInstance(getActivity()).getFriendsMessageFragment());
			break;
		case R.id.btn_friends_recent:
			Log.d(TAG, "好友动态");
			switchContent(FragmentHelper.getInstance(getActivity()).getFriendNewsFragment());
			break;
		case R.id.btn_invite:
			Log.d(TAG, "==邀请好友");
			FriendsContactsFragment ifragment = FragmentHelper.getInstance(getActivity()).getFriendsContactsFragment(FriendsContactsFragment.TAG_INVITE);
			switchContent(ifragment);
			Intent iIntent = new Intent(FriendsContactsFragment.FRIENDSCONTACTS_TAB_CHANGED);
			iIntent.putExtra("tab", FriendsContactsFragment.TAG_INVITE);
			LocalBroadcastManager.getInstance(getActivity()).sendBroadcast(iIntent);
			break;
		case R.id.btn_contact:
			Log.d(TAG, "==通讯录");
			FriendsContactsFragment cfragment = FragmentHelper.getInstance(getActivity()).getFriendsContactsFragment(FriendsContactsFragment.TAG_CONTACTS);
			switchContent(cfragment);
			Intent cIntent = new Intent(FriendsContactsFragment.FRIENDSCONTACTS_TAB_CHANGED);
			cIntent.putExtra("tab", FriendsContactsFragment.TAG_CONTACTS);
			LocalBroadcastManager.getInstance(getActivity()).sendBroadcast(cIntent);
			break;
		case R.id.btn_safebox:
			Log.d(TAG, "保险箱");
			isShow=true;
			if(safeboxIsCreated()){
				startSafeboxPWDActivity(PARAM);
			}else{
				//启动创建保险箱流程
				startSafeboxCreateActivity(PARAM);
			}
			break;
		case R.id.btn_settings:
			Log.d(TAG, "设置");
			switchContent(FragmentHelper.getInstance(getActivity()).getSettingFragment());
			break;
		case R.id.iv_head:
			Log.d(TAG, "首页");
			switchContent(FragmentHelper.getInstance(getActivity()).getZoneBaseFragment());
			break;
		case R.id.rl_enterTask:
			Log.d(TAG, "进入任务页");
			switchContent(FragmentHelper.getInstance(getActivity()).getNetworkTaskFragment());
			break;
		default:
			break;
		}
	}
    
   /* private Fragment getFragment(Class<Fragment> fragmentcls){
    	Fragment fragment=getActivity().getSupportFragmentManager().findFragmentByTag(fragmentcls.getName());
    	if(null==fragment){
    		try {
    			fragment=fragmentcls.newInstance();
    		} catch (java.lang.InstantiationException e) {
    			e.printStackTrace();
    		} catch (IllegalAccessException e) {
    			e.printStackTrace();
    		}
    	}
    	return fragment;
    }*/
    
    private static int[] images={
    	R.drawable.btn_menu_message,
    	R.drawable.btn_menu_friends_recent,
    	R.drawable.btn_menu_invite,
    	R.drawable.btn_menu_contact,
    	R.drawable.btn_menu_safebox,
    	R.drawable.btn_menu_settings,
	};
	
	/*class GridViewAdapter extends BaseAdapter{
		private LayoutInflater inflater;
		public GridViewAdapter(Context context){
			inflater=LayoutInflater.from(context);
		}
		
		@Override
		public int getCount() {
			return images.length;
		}

		@Override
		public Object getItem(int position) {
			return images[position];
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			viewHolder holder;
			if(null==convertView){
				holder=new viewHolder();
				convertView=inflater.inflate(R.layout.include_btn_slidingbar, null);
				holder.ivPic=(ImageView) convertView.findViewById(R.id.iv_pic);
				holder.tvDes= (TextView) convertView.findViewById(R.id.tv_des);
				convertView.setTag(holder);
			}else{
				holder=(viewHolder) convertView.getTag();
			}
			holder.ivPic.setImageResource(images[position]);
			holder.tvDes.setText(descriptions[position]);
			return convertView;
		}
	}
	
	static class viewHolder{
		TextView tvDes;
		ImageView ivPic;
	}*/

	@Override
	public boolean handleMessage(Message msg) {
		// TODO Auto-generated method stub
		return false;
	}
	
	public static final String ACTION_SAFEBOX_MODE_CHANGED="ACTION_SAFEBOX_MODE_CHANGED";
	public static final String PARAM="from_menu";
	// 接收任务更新广播
	private class UpdateNetworkTaskReceiver extends ZBroadcastReceiver {

		private UpdateNetworkTaskReceiver() {
			addAction(NetworkTaskManager.ACTION_UPDATE_NETWORKTASK);
			addAction(ACTION_SAFEBOX_MODE_CHANGED);
		}

		@Override
		public void onReceive(Context context, Intent intent) {
			String action=intent.getAction();
			String param=intent.getStringExtra(SettingGesturePwdActivity.ACTION_PARAM);
			if (NetworkTaskManager.ACTION_UPDATE_NETWORKTASK.equals(action)) {
				setTask();
			}else if(ACTION_SAFEBOX_MODE_CHANGED.equals(action)&&PARAM.equals(param)){
				switchContent(FragmentHelper.getInstance(getActivity()).getSafeboxContentFragment());
				FragmentHelper.getInstance(getActivity()).getSafeboxVShareFragment().updateViews(null);
			}
		}
	}
	
	public static final String ACTION_GOTO_SETTINGS_FRAGMENT = "ACTION_GOTO_SETTINGS_FRAGMENT";
	/**
	 * 接收页面切换广播 (通过广播来切换显示主页面的各个Fragment);
	 */
	private class FragmentAutoSwitchReceiver extends ZBroadcastReceiver {
		
		private FragmentAutoSwitchReceiver() {
			addAction(ACTION_GOTO_SETTINGS_FRAGMENT);
		}
		
		@Override
		public void onReceive(Context context, Intent intent) {
			if (intent.getAction().equals(ACTION_GOTO_SETTINGS_FRAGMENT)) {
				switchContent(FragmentHelper.getInstance(getActivity()).getSettingFragment());
			}
		}
	}
	
	@Override
	public void onDestroy() {
		if(updateNetworkTaskReceiver!=null)
			LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(updateNetworkTaskReceiver);
		
		if(fragmentAutoSwitchReceiver != null) {
			LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(fragmentAutoSwitchReceiver);
		}
		super.onDestroy();
	}

	// 设置正在进行的任务数
	private void setTask() {
		String uid = ActiveAccount.getInstance(getActivity()).getLookLookID();
		Log.e(TAG, "uid = " + uid);
		// setTask
		if(uid!=null){
			int taskNum = NetworkTaskManager.getInstance(uid).getActiveTaskNum();
			if(0==taskNum){
				rlTasks.setVisibility(View.GONE);
			}else{
				rlTasks.setVisibility(View.VISIBLE);
			}
			tvTasks.setText((taskNum > 99 ? 99 : taskNum) + "个任务上传中");
		}
	}
	
}
