package com.cmmobi.looklook.activity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;

import javax.crypto.spec.PSource;

import android.R.integer;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.PopupWindow.OnDismissListener;
import cn.zipper.framwork.core.ZActivity;
import cn.zipper.framwork.core.ZDialog;

import com.cmmobi.looklook.R;
import com.cmmobi.looklook.common.adapter.MyZoneListAdapter;
import com.cmmobi.looklook.common.adapter.VshareGroupAdapter;
import com.cmmobi.looklook.common.constant.Constant;
import com.cmmobi.looklook.common.gson.GsonResponse3;
import com.cmmobi.looklook.common.gson.GsonResponse3.MicListItem;
import com.cmmobi.looklook.common.gson.GsonResponse3.MyDiaryList;
import com.cmmobi.looklook.common.gson.GsonResponse3.UserList;
import com.cmmobi.looklook.common.gson.GsonResponse3.UserObj;
import com.cmmobi.looklook.common.gson.GsonResponse3.VshareDiary;
import com.cmmobi.looklook.common.gson.GsonResponse3.addfriendResponse;
import com.cmmobi.looklook.common.gson.GsonResponse3.myMicInfoResponse;
import com.cmmobi.looklook.common.gson.Requester3;
import com.cmmobi.looklook.common.service.CoreService;
import com.cmmobi.looklook.common.utils.CmmobiClickAgentWrapper;
import com.cmmobi.looklook.common.utils.DensityUtil;
import com.cmmobi.looklook.common.view.FriendsExpressionView;
import com.cmmobi.looklook.common.view.VshareContentThumbnailView;
import com.cmmobi.looklook.common.view.VshareThumbnailViewOneItem;
import com.cmmobi.looklook.common.view.XEditDialog;
import com.cmmobi.looklook.common.view.pulltorefresh.PullToRefreshBase;
import com.cmmobi.looklook.common.view.pulltorefresh.PullToRefreshListView;
import com.cmmobi.looklook.common.view.pulltorefresh.PullToRefreshBase.OnRefreshListener2;
import com.cmmobi.looklook.dialog.ShareDialog;
import com.cmmobi.looklook.fragment.FriendsContactsFragment;
import com.cmmobi.looklook.fragment.MenuFragment;
import com.cmmobi.looklook.fragment.MyZoneFragment.DiariesItem;
import com.cmmobi.looklook.fragment.MyZoneFragment.MyZoneItem;
import com.cmmobi.looklook.info.profile.AccountInfo;
import com.cmmobi.looklook.info.profile.ActiveAccount;
import com.cmmobi.looklook.info.profile.ContactManager;
import com.cmmobi.looklook.info.profile.LoginSettingManager;
import com.cmmobi.looklook.info.profile.TimeHelper;
import com.cmmobi.looklook.info.profile.WrapUser;
import com.cmmobi.looklook.prompt.Prompt;
import com.google.gson.Gson;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageLoadingListener;
import com.nostra13.universalimageloader.core.display.CircularBitmapDisplayer;
import com.nostra13.universalimageloader.core.display.SimpleBitmapDisplayer;
import com.tencent.stat.common.User;

/**
 * 微享组成员及内容页
 * @author youtian
 * @email youtian@cmmobi.com
 * @date  2013-11－01
 * 
 */
public class VshareGroupActivity extends ZActivity implements OnRefreshListener2<ListView> {

	private ImageView iv_back;
	private GridView gv_members;
	private ImageView iv_new;
	
	private PullToRefreshListView xlvMyVshare;
	private ListView lvMyVshare;
	private VshareGroupAdapter vshareGroupAdapter;
	private ArrayList<MicListItem> allMicListItems = new ArrayList<GsonResponse3.MicListItem>();
		
	private DisplayImageOptions options;
	protected ImageLoader imageLoader;
	private ImageLoadingListener animateFirstListener;
	
	private String is_encrypt; 
	private myMicInfoResponse mMicInfo;
	
	public RelativeLayout rl_vsharegroup;
	private Boolean isPullDown = true;
	
	private UserObj[] users = null;
	
	public static String VSHAREGROUP = "VSHAREGROUP";
	
	private BroadcastReceiver mReseiver = new BroadcastReceiver(){
		 @Override
		  public void onReceive(Context context, Intent intent) {    
			    // Get extra data included in the Intent
			LoginSettingManager lsm = AccountInfo.getInstance(ActiveAccount.getInstance(context).getUID()).setmanager;
			/*if(MenuFragment.ACTION_SAFEBOX_MODE_CHANGED.equals(intent.getAction())&& VSHAREGROUP.equals(intent.getStringExtra(SettingGesturePwdActivity.ACTION_PARAM))&& lsm.getSafeIsOn()){
				vshareGroupAdapter.sendRequest();
			}*/
		  }
	};
	
	private BroadcastReceiver mSyncTimeReseiver = new BroadcastReceiver(){
		 @Override
		  public void onReceive(Context context, Intent intentr) {    
			    // Get extra data included in the Intent
				if(CoreService.BRODCAST_SYNC_TIME_DONE.equals(intentr.getAction())){
					vshareGroupAdapter.jumpToDetailAfterSyncTime();
				}
		  }
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_vshare_group);
		//inflater = LayoutInflater.from(this);
		rl_vsharegroup = (RelativeLayout) findViewById(R.id.rl_vsharegroup);
		iv_back = (ImageView) findViewById(R.id.iv_back);
		iv_back.setOnClickListener(this);
		iv_back.setOnLongClickListener(new View.OnLongClickListener() {
			
			@Override
			public boolean onLongClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(VshareGroupActivity.this,LookLookActivity.class);
				intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(intent);
				VshareGroupActivity.this.finish();
				return false;
			}
		});
		
		iv_new = (ImageView) findViewById(R.id.iv_new);
		iv_new.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent shareIntent = new Intent(VshareGroupActivity.this, ShareDiaryActivity.class);
				shareIntent.putExtra(ShareDialog.TYPY_SHARE, ShareDialog.TYPE_SHARE_V);
				if(users != null){
					shareIntent.putExtra(ShareDiaryActivity.INTENT_ACTION_SHARE_DIARY_LIST, new Gson().toJson(users));
				}
				startActivity(shareIntent);
			}
		});
		
		gv_members = (GridView) findViewById(R.id.gv_members);
		gv_members.setColumnWidth(this.getResources().getDisplayMetrics().widthPixels/8*9/10);
		LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) gv_members.getLayoutParams();
		
		int margin = (this.getResources().getDisplayMetrics().widthPixels - this.getResources().getDisplayMetrics().widthPixels*9/10 - DensityUtil.dip2px(this, 2)*7)/2;
		params.leftMargin = margin;
		params.rightMargin = margin;
		params.height = this.getResources().getDisplayMetrics().widthPixels/8*9/10 + DensityUtil.dip2px(this, 15*2);
		gv_members.setLayoutParams(params);
		gv_members.setSelector(new ColorDrawable(Color.TRANSPARENT));
		
		xlvMyVshare = (PullToRefreshListView) findViewById(R.id.xlv_my_vshare);
		lvMyVshare = xlvMyVshare.getRefreshableView();
		xlvMyVshare.setShowIndicator(false);
		xlvMyVshare.setOnRefreshListener(this);
		
		imageLoader = ImageLoader.getInstance();
		//imageLoader.init(ImageLoaderConfiguration.createDefault(VshareGroupActivity.this));
		options = new DisplayImageOptions.Builder()
		.showStubImage(R.drawable.moren_touxiang)
		.showImageForEmptyUri(R.drawable.moren_touxiang)
		.showImageOnFail(R.drawable.moren_touxiang)
		.cacheInMemory(true)
		.cacheOnDisc(true)
		.displayer(new SimpleBitmapDisplayer())
		.build();
		
		
		vshareGroupAdapter = new VshareGroupAdapter(this, is_encrypt);
		lvMyVshare.setAdapter(vshareGroupAdapter);	
		registerReceiver(mSyncTimeReseiver, new IntentFilter(CoreService.BRODCAST_SYNC_TIME_DONE));
		LocalBroadcastManager.getInstance(this).registerReceiver(mReseiver, new IntentFilter(MenuFragment.ACTION_SAFEBOX_MODE_CHANGED));
	}


	@Override
	public boolean handleMessage(Message msg) {
		// TODO Auto-generated method stub
		switch(msg.what){
		case Requester3.RESPONSE_TYPE_MYSUBMICLIST:
			try {			
				GsonResponse3.mySubMicListResponse mySubMicList = (GsonResponse3.mySubMicListResponse) msg.obj;
				if (mySubMicList != null) {
					if (mySubMicList.status.equals("0")) {
						if("1".equals(mySubMicList.is_refresh)){
							allMicListItems.clear();
						}
						
						users = mySubMicList.userobj;
						
						if(mySubMicList.userobj != null){
							gv_members.setAdapter(new ImageAdapter(this, mySubMicList.userobj));
						}
						
						for(int i=0; i< mySubMicList.showmiclist.length; i++){
							for(int j=0; j< allMicListItems.size(); j++){
								if(mySubMicList.showmiclist[i].publishid.equals(allMicListItems.get(j).publishid)){
									allMicListItems.remove(j);
								}
							}
						}
						if(isPullDown){
							for(int i=0; i< mySubMicList.showmiclist.length; i++){
								allMicListItems.add(i, mySubMicList.showmiclist[i]);
							}
						}else{
							for(int i=0; i< mySubMicList.showmiclist.length; i++){
								allMicListItems.add(mySubMicList.showmiclist[i]);
							}
						}
					//	Collections.sort(allMicListItems,new SortByUpdate());
						vshareGroupAdapter.setData(allMicListItems);
						vshareGroupAdapter.notifyDataSetChanged();
					} else if (mySubMicList.status.equals("200600")) {
						Prompt.Dialog(this, false, "提示",
								Constant.CRM_STATUS[Integer
										.parseInt(mySubMicList.crm_status)], null);
					} else {
						Prompt.Dialog(this, false, "提示", "操作失败，请稍后再试",null);
					}
				} else {
					Prompt.Dialog(this, false, "提示", "操作失败，网络不给力", null);
				}
			} catch (Exception e) {
				// TODO: handle exception
			}
			ZDialog.dismiss();
			xlvMyVshare.onRefreshComplete();
			break;
		case Requester3.RESPONSE_TYPE_ADD_FRIEND:
			if (msg.obj != null) {
				addfriendResponse response = (addfriendResponse) msg.obj;
				if ("0".equals(response.status)) {
					if(response.target_userid == null){
						break;
					}
					Prompt.Alert(this, "好友申请已发送");
				}
			}
			break;
		default:
			break;
		}
		return false;
	}


	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.iv_back:
			this.finish();
			break;
		default:
			break;
		}
	}
	
	private class ImageAdapter extends BaseAdapter
	{
		// 定义Context
		private Context	mContext;
		// 定义String数组 即图片源
		private ArrayList<UserObj> muserList = new ArrayList<UserObj>();
		private LayoutInflater inflater;
		public ImageAdapter(Context c, UserObj...userLists)
		{
			mContext = c;
			for(int i=0; i< userLists.length; i++){
				if(!ActiveAccount.getInstance(c).getUID().equals(userLists[i].userid)){
					muserList.add(userLists[i]);
				}
			}
			this.inflater = LayoutInflater.from(mContext);
		}

		// 获取图片的个数
		public int getCount()
		{
			return muserList.size();
		}

		// 获取图片在库中的位置
		public Object getItem(int position)
		{
			return position;
		}

		// 获取图片ID
		public long getItemId(int position)
		{
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			ViewHolder viewHolder = null;
			if (convertView == null)
			{
				convertView = inflater
						.inflate(R.layout.include_vshare_member_portrait_view_item, null);
				viewHolder = new ViewHolder();
				viewHolder.iv_portrait = (ImageView) convertView
						.findViewById(R.id.iv_portrait);
				RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams)viewHolder.iv_portrait.getLayoutParams();
				params.width = VshareGroupActivity.this.getResources().getDisplayMetrics().widthPixels/8*9/10;
				params.height = VshareGroupActivity.this.getResources().getDisplayMetrics().widthPixels/8*9/10;
				viewHolder.iv_portrait.setLayoutParams(params);
				viewHolder.tv_nickname = (TextView) convertView.findViewById(R.id.tv_nickname);
				convertView.setTag(viewHolder);
			}else{
				viewHolder = (ViewHolder) convertView.getTag();
			}
			if(position < muserList.size() && muserList.get(position) != null && muserList.get(position).user_telname != null){
				//viewHolder.tv_nickname.setText(muserList.get(position).user_telname);
				FriendsExpressionView.replacedExpressions(muserList.get(position).user_telname, viewHolder.tv_nickname);
			}else{
				viewHolder.tv_nickname.setText("");
			}
			if(position < muserList.size() && muserList.get(position) != null && muserList.get(position).headimageurl != null && !muserList.get(position).headimageurl.isEmpty()){
				imageLoader.displayImageEx(muserList.get(position).headimageurl, viewHolder.iv_portrait, options, animateFirstListener, ActiveAccount.getInstance(mContext).getUID(), 0);
			}else{
				viewHolder.iv_portrait.setImageResource(R.drawable.moren_touxiang);
			}

			viewHolder.iv_portrait.setTag(viewHolder.iv_portrait.getId(),muserList.get(position));
			viewHolder.iv_portrait.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					
					String myuid = ActiveAccount.getInstance(VshareGroupActivity.this).getUID();
					final UserObj other_userid = (UserObj) v.getTag(v.getId());
					
					// 验证是否是自己的好友
					AccountInfo accinfo = AccountInfo.getInstance(myuid);
					ContactManager friendsListContactManager=accinfo.friendsListName;
					WrapUser currUserInfo=friendsListContactManager.findUserByUserid(other_userid.userid);
					
					if(other_userid.userid == null || other_userid.userid.isEmpty()){
						
					}else if(currUserInfo == null){
						if(other_userid.userid.equals(accinfo.serviceUser.userid)){
							// 是客服，并跳转
//							Intent intent = new Intent(VshareGroupActivity.this, OtherZoneActivity.class);
//							intent.putExtra(OtherZoneActivity.OTHER_ZONE_USERID, other_userid.userid);
//							VshareGroupActivity.this.startActivity(intent);
						}else{
						
						// 不在好友列表中
						new XEditDialog.Builder(VshareGroupActivity.this)
						.setTitle(R.string.xeditdialog_title)
						.setPositiveButton(R.string.send, new OnClickListener() {
							@Override
							public void onClick(View v) {
								//加好友
								Requester3.addFriend(handler, other_userid.userid, v.getTag().toString());
							}
						})
						.setNegativeButton(android.R.string.cancel, null)
						.create().show();
						}
					}else{
						// 是好友，并跳转
						Intent intent = new Intent(VshareGroupActivity.this, OtherZoneActivity.class);
						intent.putExtra(OtherZoneActivity.OTHER_ZONE_USERID, other_userid.userid);
						VshareGroupActivity.this.startActivity(intent);
					}
						
					
				}
			});
			return convertView;
		}

		class ViewHolder {
			ImageView iv_portrait;
			TextView tv_nickname;
		}
	}

	
	public static class SortByUpdate implements Comparator {
		 public int compare(Object o1, Object o2) {
			 MicListItem s1 = (MicListItem) o1;
			 MicListItem s2 = (MicListItem) o2;
			 
		  try {
			  if(TextUtils.isEmpty(s1.update_time)&&TextUtils.isEmpty(s2.update_time)){
				  return 0;
			  }else if(TextUtils.isEmpty(s1.update_time)){
				  return 1;
			  }else if(TextUtils.isEmpty(s2.update_time)){
				  return -1;
			  }else {
				  return s2.update_time.compareTo(s1.update_time);
			  }
			 
			} catch (Exception e) {
				// TODO: handle exception
			}
		 return 0;
		 }
	}

	@Override
	public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
		// TODO Auto-generated method stub
		isPullDown = true;
		String time = "";
		if(allMicListItems.size()>0){
			time = allMicListItems.get(0).update_time;
		}
		Requester3.mySubMicList(handler, mMicInfo.userobj, is_encrypt, time, "1");
	}

	@Override
	public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
		// TODO Auto-generated method stub
		isPullDown = false;
		String time = "";
		if(allMicListItems.size()>0){
			time = allMicListItems.get(allMicListItems.size()-1).update_time;
		}
		Requester3.mySubMicList(handler, mMicInfo.userobj, is_encrypt, time, "2");
	}	
	
	
	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		LocalBroadcastManager.getInstance(this).unregisterReceiver(mReseiver);
		if(mSyncTimeReseiver!=null){
			unregisterReceiver(mSyncTimeReseiver);
		}
		super.onDestroy();
	}
	
	@Override
	public void onResume() {
		super.onResume();
		CmmobiClickAgentWrapper.onResume(this);
		Intent intent = getIntent();
		if(intent != null){
			is_encrypt = intent.getStringExtra("is_encrypt");
			mMicInfo = new Gson().fromJson(intent.getStringExtra("detailinfo"), myMicInfoResponse.class);
			Requester3.mySubMicList(handler, mMicInfo.userobj, is_encrypt, "", "");
			ZDialog.show(R.layout.progressdialog, false, true, this);
		}	
	}

	@Override
	public void onPause() {
		super.onPause();
		CmmobiClickAgentWrapper.onPause(this);
	}

	@Override
	public void onStop(){
		super.onStop();
		CmmobiClickAgentWrapper.onStop(this);
	}
	

}
