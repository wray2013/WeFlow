package com.cmmobi.looklook.fragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.LocalBroadcastManager;
import android.text.Html;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.PopupWindow.OnDismissListener;
import cn.zipper.framwork.core.ZDialog;
import cn.zipper.framwork.io.network.ZNetworkStateDetector;

import com.baidu.platform.comapi.map.l;
import com.cmmobi.looklook.R;
import com.cmmobi.looklook.activity.LookLookActivity;
import com.cmmobi.looklook.activity.SettingGesturePwdActivity;
import com.cmmobi.looklook.activity.SettingPersonalInfoActivity;
import com.cmmobi.looklook.activity.ShareDiaryActivity;
import com.cmmobi.looklook.activity.VshareDetailActivity;
import com.cmmobi.looklook.activity.VshareGroupActivity;
import com.cmmobi.looklook.common.adapter.VshareListAdapter;
import com.cmmobi.looklook.common.constant.Constant;
import com.cmmobi.looklook.common.gson.GsonResponse3;
import com.cmmobi.looklook.common.gson.GsonResponse3.MicListItem;
import com.cmmobi.looklook.common.gson.GsonResponse3.UserObj;
import com.cmmobi.looklook.common.gson.GsonResponse3.VshareDiary;
import com.cmmobi.looklook.common.gson.Requester3;
import com.cmmobi.looklook.common.service.CoreService;
import com.cmmobi.looklook.common.utils.CmmobiClickAgentWrapper;
import com.cmmobi.looklook.common.utils.DateUtils;
import com.cmmobi.looklook.common.utils.DensityUtil;
import com.cmmobi.looklook.common.view.VshareThumbnailViewOneItem;
import com.cmmobi.looklook.common.view.pulltorefresh.PullToRefreshBase;
import com.cmmobi.looklook.common.view.pulltorefresh.PullToRefreshBase.Mode;
import com.cmmobi.looklook.common.view.pulltorefresh.PullToRefreshBase.OnRefreshListener2;
import com.cmmobi.looklook.common.view.pulltorefresh.PullToRefreshListView;
import com.cmmobi.looklook.dialog.ShareDialog;
import com.cmmobi.looklook.info.profile.AccountInfo;
import com.cmmobi.looklook.info.profile.ActiveAccount;
import com.cmmobi.looklook.info.profile.TimeHelper;
import com.cmmobi.looklook.info.profile.VshareDataEntities;
import com.cmmobi.looklook.info.profile.WrapUser;
import com.cmmobi.looklook.misharetask.MiShareTaskManager;
import com.cmmobi.looklook.prompt.Prompt;
import com.cmmobi.statistics.CmmobiClickAgent;
import com.google.gson.Gson;

/**
 * @author youtian
 * @email youtian@cmmobi.com
 * @date  2014-03-20
 */
public class OtherZoneVShareFragment extends XFragment implements OnRefreshListener2<ListView>, OnClickListener{

	private View contentView;
	private PullToRefreshListView xlv_vshareData;
	private ListView lv_vshareData;
	private VshareListAdapter vshareListAdapter;
	private ArrayList<MicListItem> listItems = new ArrayList<GsonResponse3.MicListItem>();
	private VshareDataEntities datasFromServer = new VshareDataEntities();
	
	private PopupWindow pw_clear; //清除
	private LayoutInflater inflater;
	private String publishid;
	private String publishuuid;
	private String is_encrypt = "0";
	
	private View currentView = null;
	
	private AccountInfo accountInfo;
	
	private Boolean isPullDown = true;
	
	private RelativeLayout rl_newvshare;
	private Button btn_menuFirst;
	private Button btn_menuSecond;
	
	private static String VSHARE_EXTRA = "VSHARE_EXTRA";
	
	private WrapUser otherUser;
	private TextView tv_newVshare;
	
	public Boolean isHasNextPage = true;
	private static final int REFRESH_COMPLETE = 0xffcbcb;
	
	private String onClickedPublishId;
	
	private BroadcastReceiver mReseiver = new BroadcastReceiver(){
		 @Override
		  public void onReceive(Context context, Intent intent) {    
			    // Get extra data included in the Intent
				if(MenuFragment.ACTION_SAFEBOX_MODE_CHANGED.equals(intent.getAction())&& VSHARE_EXTRA.equals(intent.getStringExtra(SettingGesturePwdActivity.ACTION_PARAM))&& accountInfo.setmanager.getSafeIsOn()){
					if(pw_clear.isShowing()){
						pw_clear.dismiss();
					}
					if(safeboxIsCreated()){
						Requester3.safeboxmic(handler, publishid,"1");
					}
				}else if(VShareFragment.OTHERZONE_MIC_UPDATE.equals(intent.getAction())) {
					isPullDown = true;
					String time = "";
					String request_type = "";
					if(datasFromServer.getCache().size()>0){
						time = datasFromServer.getCache().get(0).update_time;
						request_type = "1";
					}
					Requester3.MicList(handler, otherUser.userid, time, request_type);
				}
		  }
	};
	
	private BroadcastReceiver mSyncTimeReseiver = new BroadcastReceiver(){
		 @Override
		  public void onReceive(Context context, Intent intentr) {    
			    // Get extra data included in the Intent
				if(CoreService.BRODCAST_SYNC_TIME_DONE.equals(intentr.getAction())){
					loadLocaldataForTimer();
					if(onClickedPublishId == null) return;
					if(ZNetworkStateDetector.isConnected()){
						final MicListItem item = vshareListAdapter.findItemByPublishId(onClickedPublishId);
						onClickedPublishId = null;
						if(item == null) return;
						if(TimeHelper.getInstance().now()>= Long.parseLong(item.capsule_time)){
							if("1".equals(item.burn_after_reading)){
								Prompt.Dialog(getActivity(), true, "提示", "该内容已被设置阅后即焚，退出后将无法再次查看", new DialogInterface.OnClickListener(){

									@Override
									public void onClick(DialogInterface dialog,
											int which) {
										// TODO Auto-generated method stub
										jumpToDetail(item);
										vshareListAdapter.getDatas().remove(item);
										vshareListAdapter.isForTimer = false;
										vshareListAdapter.notifyDataSetChanged();
									}		
								}, R.string.promt_immediately, R.string.promt_later);
							}else{
								jumpToDetail(item);
							}
						}else{
							Prompt.Dialog(getActivity(), false, "提示", Html.fromHtml("亲，不要太心急，时光胶囊再过" + "<font color=\"" + context.getResources().getColor(R.color.blue)+ "\">" + DateUtils.getCountdown(item.capsule_time) + "</font>" + "就能开启啦"), null, R.string.promt_iknow, R.string.promt_iknow);
						}
					}else{
						Prompt.Dialog(getActivity(), false, "提示", "操作失败，网络不给力", null);
					}	
				}
				
		  }
	};
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		contentView=inflater.inflate(R.layout.fragment_vshare, null);
		
		this.inflater = inflater;
		
		String wrapuser_str = getActivity().getIntent().getExtras().getString( "wrapuser" );
		otherUser = new Gson().fromJson(wrapuser_str, WrapUser.class) ;
		
		rl_newvshare =(RelativeLayout) contentView.findViewById(R.id.rl_newvshare);
		
		DisplayMetrics dm = this.getActivity().getApplicationContext().getResources().getDisplayMetrics();
		RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) rl_newvshare.getLayoutParams();
		params.height = dm.widthPixels/5;
		rl_newvshare.setLayoutParams(params);
		
		tv_newVshare = (TextView) contentView.findViewById(R.id.tv_newvshare);
		tv_newVshare.setText(getResources().getString(R.string.other_zone_new));
		
		rl_newvshare.setOnClickListener(this);
		
		xlv_vshareData = (PullToRefreshListView) contentView.findViewById(R.id.xlv_vshare_data);
		xlv_vshareData.setShowIndicator(false);
		lv_vshareData = xlv_vshareData.getRefreshableView();
		xlv_vshareData.setOnRefreshListener(this);
		xlv_vshareData.setIsVshare();
		vshareListAdapter = new VshareListAdapter(this.getActivity());
		lv_vshareData.setAdapter(vshareListAdapter);
		
		lv_vshareData.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub
				if("0".equals(vshareListAdapter.getDatas().get(arg2-1).getUpload_status()) || "1".equals(vshareListAdapter.getDatas().get(arg2-1).getUpload_status())){
					return;
				}		
				if(ZNetworkStateDetector.isConnected()){
					if("1".equals(vshareListAdapter.getDatas().get(arg2-1).capsule) && "0".equals(vshareListAdapter.getDatas().get(arg2-1).is_clear)){
						onClickedPublishId = vshareListAdapter.getDatas().get(arg2-1).publishid;
						getActivity().sendBroadcast(new Intent(CoreService.BRODCAST_SYNC_TIME));	
					}else if("1".equals(vshareListAdapter.getDatas().get(arg2-1).burn_after_reading)){
						final int p = arg2-1;
						Prompt.Dialog(getActivity(), true, "提示", "该内容已被设置阅后即焚，退出后将无法再次查看", new DialogInterface.OnClickListener(){

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								// TODO Auto-generated method stub
								MicListItem item = vshareListAdapter.getDatas().get(p);
								jumpToDetail(item);
								vshareListAdapter.getDatas().remove(p);
								vshareListAdapter.isForTimer = false;
								vshareListAdapter.notifyDataSetChanged();
							}		
						}, R.string.promt_immediately, R.string.promt_later);

					}else{
						jumpToDetail(vshareListAdapter.getDatas().get(arg2-1));
					}
					
				}else{
					Prompt.Dialog(getActivity(), false, "提示", "操作失败，网络不给力", null);
				}
			}
		});
		
		lv_vshareData.setOnItemLongClickListener(new OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				// TODO Auto-generated method stub
				currentView = arg1;
				if("0".equals(vshareListAdapter.getDatas().get(arg2-1).getUpload_status())){
					
				}else if("1".equals(vshareListAdapter.getDatas().get(arg2-1).getUpload_status())){
					arg1.setBackgroundColor(getResources().getColor(R.color.light_gray));
					btn_menuFirst.setText("继续发布");
					btn_menuSecond.setText("删除");
					btn_menuSecond.setTextColor(Color.RED);
					publishuuid = vshareListAdapter.getDatas().get(arg2-1).uuid;
					pw_clear.showAtLocation(getActivity().findViewById(R.id.rl_vsharelist),
							Gravity.BOTTOM, 0, 0);
				}else {
					arg1.setBackgroundColor(getResources().getColor(R.color.light_gray));
					btn_menuFirst.setText("加入保险箱");
					if("1".equals(vshareListAdapter.getDatas().get(arg2-1).is_undisturb)){
						btn_menuSecond.setText("新消息提醒：关");
					}else{
						btn_menuSecond.setText("新消息提醒：开");
					}
					btn_menuSecond.setTextColor(getResources().getColor(R.color.blue));
					pw_clear.showAtLocation(getActivity().findViewById(R.id.rl_vsharelist),
							Gravity.BOTTOM, 0, 0);
					publishid = vshareListAdapter.getDatas().get(arg2-1).publishid;
				}
				
				
				return true;
			}
		});
				
		
		accountInfo = AccountInfo.getInstance(ActiveAccount.getInstance(getActivity()).getUID());
		
		initClearChoice();
		
		IntentFilter filter = new IntentFilter();
		filter.addAction(MenuFragment.ACTION_SAFEBOX_MODE_CHANGED);
		filter.addAction(VShareFragment.OTHERZONE_MIC_UPDATE);
		LocalBroadcastManager.getInstance(getActivity()).registerReceiver(mReseiver, filter);
		
		ZDialog.show(R.layout.progressdialog, false, true, getActivity());
		Requester3.MicList(handler, otherUser.userid, "", "");
		
		getActivity().registerReceiver(mSyncTimeReseiver, new IntentFilter(CoreService.BRODCAST_SYNC_TIME_DONE));
		
		System.out.println("==== oncreateview ====");
		
		
		return contentView;
	}


	private Timer timer;
   
	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		System.out.println("==== onResume ====");
		loadLocalData();
		if(timer == null){
			timer = new Timer(true);
			TimerTask task = new TimerTask(){  
			      public void run() {  
			      Message message = new Message();      
			      message.what = VShareFragment.MSG_UPDATE_DATA;      
			      handler.sendMessage(message);    
			   }  
			};
			timer.schedule(task,60000, 60000); //延时1000ms后执行，1000ms执行一次	
		}
	}
	
	@Override
	public void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
		System.out.println("==== onstop ====");
		if(timer!=null){
			timer.cancel();
			timer = null;
		}
	}
	
	private void jumpToDetail(final MicListItem item){
		Intent intent = new Intent(getActivity(), VshareDetailActivity.class);
		intent.putExtra("publishid", item.publishid);
		intent.putExtra("is_encrypt", is_encrypt);
		intent.putExtra("micuserid", item.micuserid);
		intent.putExtra("create_time", item.create_time);
		intent.putExtra("is_burn", item.burn_after_reading);
		item.is_clear = "1";
		if(item.commentnum != null && !item.commentnum.isEmpty() && !"0".equals(item.commentnum)){
			item.commentnum = "0";
			vshareListAdapter.isForTimer = false;
			vshareListAdapter.notifyDataSetChanged();
		}
		
		MicListItem itemLocal = accountInfo.vshareDataEntities.findMember(item.publishid);
		if(itemLocal !=null){
			itemLocal.commentnum = "0";
			itemLocal.is_clear = "1";
		}
		startActivity(intent);
	}
	

	private void loadLocaldataForTimer(){
		vshareListAdapter.isForTimer = true;
		vshareListAdapter.notifyDataSetChanged();
	}
	
	private void loadLocalData(){
		listItems.clear();
		Boolean hasData = false;
		List<MicListItem> micLocal = accountInfo.vshareLocalDataEntities.getCache();
		if(micLocal !=null && micLocal.size()>0){
			for(int i=0; i<micLocal.size(); i++){
				for(int j=0; j<micLocal.get(i).userobj.length; j++){
					if(otherUser.userid.equals(micLocal.get(i).userobj[j].userid) && datasFromServer.findMemberuuid(micLocal.get(i).uuid)==null){
						listItems.add(micLocal.get(i));
						hasData = true;
					}
				}
			}
		}
		
		List<MicListItem> micServer = datasFromServer.getCache();
	
		if(micServer != null && micServer.size()>0){
			listItems.addAll(micServer);
			hasData = true;
		}
		
		vshareListAdapter.setData(listItems);
		
		vshareListAdapter.isForTimer = false;
		vshareListAdapter.notifyDataSetChanged();
	}
	
	//显示清除选择界面
	private void initClearChoice(){
		View view = inflater.inflate(R.layout.activity_vshare_list_clear_menu ,
				null);
		pw_clear = new PopupWindow(view, LayoutParams.FILL_PARENT,
				LayoutParams.FILL_PARENT, true);
		pw_clear.setBackgroundDrawable(getResources().getDrawable(
				R.color.transparent));

		pw_clear.setOnDismissListener(new OnDismissListener() {
			
			@Override
			public void onDismiss() {
				// TODO Auto-generated method stub
				currentView.setBackgroundResource(R.drawable.bg_listview_item);
			}
		});
		btn_menuFirst = (Button)view.findViewById(R.id.btn_joinsafe);
		btn_menuSecond = (Button)view.findViewById(R.id.btn_clear);
		view.findViewById(R.id.btn_cancel).setOnClickListener(this);
		btn_menuFirst.setOnClickListener(this);
		btn_menuSecond.setOnClickListener(this);
	}
	
	@Override
	public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
		// TODO Auto-generated method stub
		isPullDown = true;
		String time = "";
		String request_type = "";
		if(datasFromServer.getCache().size()>0){
			time = datasFromServer.getCache().get(0).update_time;
			request_type = "1";
		}
		Requester3.MicList(handler, otherUser.userid, time, request_type);
	}

	@Override
	public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
		// TODO Auto-generated method stub
		isPullDown = false;
		String time = "";
		String request_type = "";
		int size = datasFromServer.getCache().size();
		if(size>0){
			time = datasFromServer.getCache().get(size-1).update_time;
			request_type = "2";
		}
		if(isHasNextPage){
			if(xlv_vshareData != null) {
				xlv_vshareData.setNoMoreData(getActivity(), true);
			}
			Requester3.MicList(handler, otherUser.userid, time, request_type);
		}else{
			if(xlv_vshareData != null) {
				xlv_vshareData.setNoMoreData(getActivity(), false);
			}
			handler.sendEmptyMessage(REFRESH_COMPLETE);
		}
	}

	@Override
	public boolean handleMessage(Message msg) {
		// TODO Auto-generated method stub
		switch(msg.what){
		case Requester3.RESPONSE_TYPE_MICLIST:
			try {
				ZDialog.dismiss();
				GsonResponse3.MicListResponse myMicListList = (GsonResponse3.MicListResponse) msg.obj;
				if (myMicListList != null) {
					if("1".equals(myMicListList.is_refresh)){
						datasFromServer.clearList();
					}
					
					if (myMicListList.status.equals("0")) {
						if(myMicListList.showmiclist !=null && myMicListList.showmiclist.length>0){
							for(int i=0; i< myMicListList.showmiclist.length;i++){								
								if(isPullDown){
									datasFromServer.insertMember(i, myMicListList.showmiclist[i]);
								}else{
									datasFromServer.addMember(myMicListList.showmiclist[i]);
								}
								
							}
						}
						
						loadLocalData();
						if(isPullDown){
							
						}else{
							if("1".equals(myMicListList.hasnextpage)){
								isHasNextPage = true;
							}else{
								isHasNextPage = false;
							}
						}
					} else if (myMicListList.status.equals("200600")) {
						/*Prompt.Dialog(getActivity(), false, "提示",
								Constant.CRM_STATUS[Integer
										.parseInt(myMicListList.crm_status)], null);*/
					} else {
						/*Prompt.Dialog(getActivity(), false, "提示", "操作失败，请稍后再试",null);*/
					}
				} else {
					/*Prompt.Dialog(getActivity(), false, "提示", "操作失败，网络不给力", null);*/
				}
			} catch (Exception e) {
				// TODO: handle exception
			}
			xlv_vshareData.onRefreshComplete();
			break;
		case REFRESH_COMPLETE:
			xlv_vshareData.onRefreshComplete();
			break;
		case Requester3.RESPONSE_TYPE_SAFEBOXMIC:
			try {
				GsonResponse3.safeboxmicResponse safeResponse = (GsonResponse3.safeboxmicResponse) msg.obj;
				if (safeResponse != null) {
					if (safeResponse.status.equals("0")) {
						accountInfo.vshareDataEntities.removeMember(publishid);
						datasFromServer.removeMember(publishid);
						loadLocalData();
						HashMap<String, String> hs=new HashMap<String, String>();
						hs.put("label", "2");
						hs.put("labe12", "1");
						//2014-4-8
						CmmobiClickAgentWrapper.onEvent(getActivity(), "save_in_safebox", hs);
					} else if (safeResponse.status.equals("200600")) {
						Prompt.Dialog(getActivity(), false, "提示",
								Constant.CRM_STATUS[Integer
										.parseInt(safeResponse.crm_status)], null);
					} else {
						Prompt.Dialog(getActivity(), false, "提示", "操作失败，请稍后再试",null);
					}
				} else {
					Prompt.Dialog(getActivity(), false, "提示", "操作失败，网络不给力", null);
				}
			} catch (Exception e) {
				// TODO: handle exception
			}
			break;
		case Requester3.RESPONSE_TYPE_SETUNDISTURB:
			try {
				GsonResponse3.setundisturbResponse undisturbResponse = (GsonResponse3.setundisturbResponse) msg.obj;
				if (undisturbResponse != null) {
					if (undisturbResponse.status.equals("0")) {
						
						if("1".equals(accountInfo.vshareDataEntities.findMember(publishid).is_undisturb)){
							accountInfo.vshareDataEntities.findMember(publishid).is_undisturb = "0";
						}else{
							accountInfo.vshareDataEntities.findMember(publishid).is_undisturb = "1";
						}
						
						if("1".equals(datasFromServer.findMember(publishid).is_undisturb)){
							datasFromServer.findMember(publishid).is_undisturb = "0";
						}else{
							datasFromServer.findMember(publishid).is_undisturb = "1";
						}
						
						loadLocalData();
					} else if (undisturbResponse.status.equals("200600")) {
						Prompt.Dialog(getActivity(), false, "提示",
								Constant.CRM_STATUS[Integer
										.parseInt(undisturbResponse.crm_status)], null);
					} else {
						Prompt.Dialog(getActivity(), false, "提示", "操作失败，请稍后再试",null);
					}
				} else {
					Prompt.Dialog(getActivity(), false, "提示", "操作失败，网络不给力", null);
				}
			} catch (Exception e) {
				// TODO: handle exception
			}
			break;
		case VShareFragment.MSG_UPDATE_DATA:
			loadLocaldataForTimer();
			break;
		default:
			
		}
		return false;
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch(v.getId()){
			case R.id.rl_newvshare:
				if(TextUtils.isEmpty(accountInfo.nickname)){
					Prompt.Dialog(getActivity(), true, "提示", "先填写个人资料才可以微享",new DialogInterface.OnClickListener() {
						
						@Override
						public void onClick(DialogInterface dialog, int which) {
							// TODO Auto-generated method stub
							//修改个信息
							Intent shareIntent = new Intent(OtherZoneVShareFragment.this.getActivity(), SettingPersonalInfoActivity.class);
							shareIntent.putExtra("createvshare", "createvshare");
							startActivity(shareIntent);	
						}
					});
					
				}else{
					//新微享
					Intent shareIntent = new Intent(OtherZoneVShareFragment.this.getActivity(), ShareDiaryActivity.class);
					shareIntent.putExtra(ShareDialog.TYPY_SHARE, ShareDialog.TYPE_SHARE_V);
					UserObj[] users = new UserObj[1];
					users[0] = new UserObj();
					users[0].headimageurl = otherUser.headimageurl;
					users[0].mic_source = "1";
					users[0].user_tel = "";
					if(otherUser.markname!=null && !otherUser.markname.isEmpty()){
						users[0].user_telname = otherUser.markname;
					}else{
						users[0].user_telname = otherUser.nickname;
					}
					users[0].userid = otherUser.userid;
					if(users != null){
						shareIntent.putExtra(ShareDiaryActivity.INTENT_ACTION_SHARE_DIARY_LIST, new Gson().toJson(users));
					}
					startActivity(shareIntent);
				}
				break;
			case R.id.btn_cancel:
				if(pw_clear.isShowing()){
					pw_clear.dismiss();
				}
				break;
			case R.id.btn_clear:
				if(pw_clear.isShowing()){
					pw_clear.dismiss();
				}
				if(publishuuid!=null && datasFromServer.findMemberuuid(publishuuid) != null){
					MiShareTaskManager.getInstance(accountInfo.userid).removeMiShareTask(publishuuid);
					accountInfo.vshareLocalDataEntities.removeMemberuuid(publishuuid);
					datasFromServer.removeMemberuuid(publishuuid);
					loadLocalData();
				}else{
					if("1".equals(datasFromServer.findMember(publishid).is_undisturb)){
						Requester3.setundisturb(handler, publishid, "0");
					}else{
						Requester3.setundisturb(handler, publishid, "1");
					}
				}
				break;
			case R.id.btn_joinsafe:
				if(pw_clear.isShowing()){
					pw_clear.dismiss();
				}
				if(publishuuid!=null && datasFromServer.findMemberuuid(publishuuid) != null){
					MiShareTaskManager.getInstance(accountInfo.userid).startMiShareTask(publishuuid, true);
				}else{
					if(safeboxIsCreated()){
						Requester3.safeboxmic(handler, publishid,"1");
					}else{
						//启动创建保险箱流程
						startSafeboxCreateActivity(VSHARE_EXTRA);
					}
				}
				
				break;
			default:{
				
			}	
		}
	}	
	
	
	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(mReseiver);
		if(mSyncTimeReseiver!=null){
			getActivity().unregisterReceiver(mSyncTimeReseiver);
		}
		super.onDestroy();
	}
}
