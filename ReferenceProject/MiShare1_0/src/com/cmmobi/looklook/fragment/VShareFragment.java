package com.cmmobi.looklook.fragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Message;
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
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.PopupWindow.OnDismissListener;
import android.widget.RelativeLayout;
import android.widget.Toast;
import cn.zipper.framwork.io.network.ZNetworkStateDetector;

import com.cmmobi.looklook.MainApplication;
import com.cmmobi.looklook.R;
import com.cmmobi.looklook.activity.DiaryPreviewActivity;
import com.cmmobi.looklook.activity.LookLookActivity;
import com.cmmobi.looklook.activity.SettingGesturePwdActivity;
import com.cmmobi.looklook.activity.SettingPersonalInfoActivity;
import com.cmmobi.looklook.activity.ShareDiaryActivity;
import com.cmmobi.looklook.activity.ShareLookLookFriendsActivity;
import com.cmmobi.looklook.activity.ShareSelectMenu;
import com.cmmobi.looklook.activity.VshareDetailActivity;
import com.cmmobi.looklook.common.adapter.VshareListAdapter;
import com.cmmobi.looklook.common.constant.Constant;
import com.cmmobi.looklook.common.gson.GsonResponse3;
import com.cmmobi.looklook.common.gson.GsonResponse3.MicListItem;
import com.cmmobi.looklook.common.gson.GsonResponse3.MyDiary;
import com.cmmobi.looklook.common.gson.GsonResponse3.UserObj;
import com.cmmobi.looklook.common.gson.Requester3;
import com.cmmobi.looklook.common.service.CoreService;
import com.cmmobi.looklook.common.service.RemoteManager;
import com.cmmobi.looklook.common.utils.CmmobiClickAgentWrapper;
import com.cmmobi.looklook.common.utils.DateUtils;
import com.cmmobi.looklook.common.view.pulltorefresh.PullToRefreshBase;
import com.cmmobi.looklook.common.view.pulltorefresh.PullToRefreshBase.OnRefreshListener2;
import com.cmmobi.looklook.common.view.pulltorefresh.PullToRefreshListView;
import com.cmmobi.looklook.info.profile.AccountInfo;
import com.cmmobi.looklook.info.profile.ActiveAccount;
import com.cmmobi.looklook.info.profile.TimeHelper;
import com.cmmobi.looklook.misharetask.MiShareTaskManager;
import com.cmmobi.looklook.prompt.Prompt;
import com.cmmobi.looklook.receiver.CmmobiPushReceiver;
import com.google.gson.Gson;

/**
 * @author youtian
 * @email youtian@cmmobi.com
 * @date  2013-10-24
 */
public class VShareFragment extends XFragment implements OnRefreshListener2<ListView>, OnClickListener{

	private View contentView;
	private PullToRefreshListView xlv_vshareData;
	private ListView lv_vshareData;
	private VshareListAdapter vshareListAdapter;
	private ArrayList<MicListItem> listItems = new ArrayList<GsonResponse3.MicListItem>();
	
	public final static int MSG_UPDATE_DATA = 0XFF001;
	
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
	public static String IS_FROM_VSHARE = "is_frmo_vshare";
	
/*	public static String SP_NAME="vshare_checked";
	public static String SP_KEY="time";*/
	
	public Boolean isHasNextPage = true;
	private static final int REFRESH_COMPLETE = 0xffabab;
	
	private static Boolean isVisible = false; 
	
	public static String OTHERZONE_MIC_UPDATE = "OTHERZONE_MIC_UPDATE";
	
	private String onClickedPublishId;
	
	private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
		  @Override
		  public void onReceive(Context context, Intent intent) {    
			    // Get extra data included in the Intent
				if(LookLookActivity.MIC_LIST_CHANGE.equals(intent.getAction())){			
					loadLocalData();
				}else if(MicListItem.INTENT_STATUS_CHANGE.equals(intent.getAction()) && intent.getExtras() != null) {
					String uuid = intent.getExtras().getString(MicListItem.BUNDLE_SHARE_UUID);
					String status = intent.getExtras().getString(MicListItem.BUNDLE_SHARE_STATUS);
					if("3".equals(status)){
						MicListItem it = accountInfo.vshareLocalDataEntities.findMemberuuid(uuid);
						if(it!=null){
							//accountInfo.vshareDataEntities.insertMember(0, it);
							isPullDown = true;
							String time = "";
							String refresh_type = "";
							if(accountInfo.vshareDataEntities.getCache().size()>0){
								time = accountInfo.vshareDataEntities.getCache().get(0).update_time;
								refresh_type = "1";
							}
							Requester3.myMicList(handler, time , refresh_type, is_encrypt);
							//System.out.println("=======miclistItem =====" + uuid + "--" + status + "--" + it.publishid);
							accountInfo.vshareLocalDataEntities.removeMemberuuid(uuid);
						}else{
							//System.out.println("=======miclistItem =====" + uuid + "--" + status + "-- null");
						}
					}
					Intent otherIntent = new Intent(OTHERZONE_MIC_UPDATE);
					LocalBroadcastManager.getInstance(MainApplication.getInstance())
							.sendBroadcast(otherIntent);
					loadLocalData();
				}else if(ZoneBaseFragment.ACTION_ZONEBASE_USERINFO.equals(intent.getAction())){
					Boolean needRefresh = false;
					for(int i=0; i< accountInfo.vshareDataEntities.getCache().size();i++){
						if(userID.equals(accountInfo.vshareDataEntities.getCache().get(i).micuserid)){
							needRefresh =true;
							accountInfo.vshareDataEntities.getCache().get(i).headimageurl = accountInfo.headimageurl;
						}
					}
					for(int i=0; i< accountInfo.vshareLocalDataEntities.getCache().size();i++){
						if(userID.equals(accountInfo.vshareLocalDataEntities.getCache().get(i).micuserid)){
							needRefresh = true;
							accountInfo.vshareLocalDataEntities.getCache().get(i).headimageurl = accountInfo.headimageurl;
						}
					}
					if(needRefresh){
						loadLocalData();
					}
					
				}
				
		  }
		  		
	};
	
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
				}
				
		  }
	};
	
	private BroadcastReceiver mSyncTimeReseiver = new BroadcastReceiver(){
		 @Override
		  public void onReceive(Context context, Intent intentr) {    
			    // Get extra data included in the Intent
				if(CoreService.BRODCAST_SYNC_TIME_DONE.equals(intentr.getAction())){
					System.out.println("=== TimeHelper sync time done ====" + TimeHelper.getInstance().now());
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
		rl_newvshare =(RelativeLayout) contentView.findViewById(R.id.rl_newvshare);
		
		DisplayMetrics dm = this.getActivity().getApplicationContext().getResources().getDisplayMetrics();
		RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) rl_newvshare.getLayoutParams();
		params.height = dm.widthPixels/5;
		rl_newvshare.setLayoutParams(params);
		
		rl_newvshare.setOnClickListener(this);
		
		xlv_vshareData = (PullToRefreshListView) contentView.findViewById(R.id.xlv_vshare_data);
		xlv_vshareData.setShowIndicator(false);
		lv_vshareData = xlv_vshareData.getRefreshableView();
		xlv_vshareData.setOnRefreshListener(this);
		xlv_vshareData.setIsVshare();
		lv_vshareData.setStackFromBottom(false);
		vshareListAdapter = new VshareListAdapter(this.getActivity());
		lv_vshareData.setAdapter(vshareListAdapter);
	
		lv_vshareData.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub
				if(vshareListAdapter.getDatas().size() == 0 || "0".equals(vshareListAdapter.getDatas().get(arg2-1).getUpload_status()) || "1".equals(vshareListAdapter.getDatas().get(arg2-1).getUpload_status())){
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
								jumpToDetail(vshareListAdapter.getDatas().get(p));
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
		
		LocalBroadcastManager.getInstance(getActivity()).registerReceiver(mReseiver, new IntentFilter(MenuFragment.ACTION_SAFEBOX_MODE_CHANGED));
		loadLocalData();
		IntentFilter filter = new IntentFilter();
		filter.addAction(LookLookActivity.MIC_LIST_CHANGE);
		filter.addAction(MicListItem.INTENT_STATUS_CHANGE);
		filter.addAction(ZoneBaseFragment.ACTION_ZONEBASE_USERINFO);
		if(mMessageReceiver!=null){
			LocalBroadcastManager.getInstance(getActivity()).registerReceiver(mMessageReceiver,filter);
		}
		
		getActivity().registerReceiver(mSyncTimeReseiver, new IntentFilter(CoreService.BRODCAST_SYNC_TIME_DONE));
		
		return contentView;
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
		startActivity(intent);
	}
	
	
	private Timer timer;
	
	public void onVisible(){
		isVisible = true;
		if(!isAdded()){
			return;
		}
		if(accountInfo.newZoneMicCount>0){
			loadLocalData();
		}
		if(timer == null){
		timer = new Timer(true);
		TimerTask task = new TimerTask(){  
		      public void run() {  
		      Message message = new Message();      
		      message.what = MSG_UPDATE_DATA;      
		      handler.sendMessage(message);    
		   }  
		};
		timer.schedule(task,60000, 60000); //延时1000ms后执行，1000ms执行一次	
		}
		//showPrompt();
		CmmobiPushReceiver.cancelNotification(this.getActivity(), CmmobiPushReceiver.NOTIFY_INDEX_VSHARE_LIST);
	}
	
	public void onInvisible(){
		isVisible = false;
		if(timer !=null){
			timer.cancel();
			timer = null;
		}
	}
	
	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		System.out.println("Vsharefragment ==== onResume");
		if(isVisible()){
			//if(accountInfo.newZoneMicCount>0){
				loadLocalData();
			//}
			/*IntentFilter filter = new IntentFilter();
			filter.addAction(LookLookActivity.MIC_LIST_CHANGE);
			filter.addAction(MicListItem.INTENT_STATUS_CHANGE);
			LocalBroadcastManager.getInstance(getActivity()).registerReceiver(mMessageReceiver,filter);
			*/
			CmmobiPushReceiver.cancelNotification(this.getActivity(), CmmobiPushReceiver.NOTIFY_INDEX_VSHARE_LIST);
		}
	}
	
	@Override
	public void onStop() {
		// TODO Auto-generated method stub

		System.out.println("Vsharefragment ==== onStop" + isVisible());
		/*if (isVisible()) {
			LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(mMessageReceiver);
		}*/
		super.onStop();
	}

	
	private void loadLocaldataForTimer(){
		vshareListAdapter.isForTimer = true;
		vshareListAdapter.notifyDataSetChanged();
	}
	private void loadLocalData(){
		
		LoadDataTask task = new LoadDataTask();
		task.execute();
	}
	
	private Boolean hasData = false;
	// 搜索结果表排序
    class LoadDataTask extends AsyncTask<Void, Integer, Void> {  
        // 可变长的输入参数，与AsyncTask.exucute()对应  
        @Override  
        protected Void doInBackground(Void... param) {  
            try {  
            	if(isVisible){
        			accountInfo.newZoneMicCount = 0;
        			System.out.println("=======vshareFragment loadLocalData =====");
        			Intent msgIntent = new Intent(LookLookActivity.UPDATE_MASK);
        			LocalBroadcastManager.getInstance(getActivity()).sendBroadcast(msgIntent);
        		}
        		if(accountInfo.vSharePublishId != null){
        			if(accountInfo.vshareDataEntities.findMember(accountInfo.vSharePublishId) != null) 
        				accountInfo.vshareDataEntities.findMember(accountInfo.vSharePublishId).commentnum = "0";
        			accountInfo.vSharePublishId = null;
        		}

        		listItems.clear();
        		accountInfo.vshareDataEntities.removeDuplicateWithOrder();
        		List<MicListItem> micHistory = accountInfo.vshareDataEntities.getCache();
        		
        		List<MicListItem> micLocal = accountInfo.vshareLocalDataEntities.getCache();
        		hasData = false;
        		if(micLocal !=null && micLocal.size()>0){
        			listItems.addAll(micLocal);
        			hasData = true;
        		}
        		if(micHistory != null && micHistory.size()>0){
        			listItems.addAll(micHistory);
        			hasData = true;
        		}
        		
            } catch (Exception e) {  
                e.printStackTrace();  
            }  
            
            return null;
        }  
  
        
        @Override  
        protected void onPostExecute(Void result) {  
            // 返回HTML页面的内容
        	vshareListAdapter.setData(listItems);
        	vshareListAdapter.isForTimer = false;
			vshareListAdapter.notifyDataSetChanged();
        }  
        @Override  
        protected void onProgressUpdate(Integer... values) {  
            // 更新进度  
        }  
    }
		
	/*private void showPrompt(){
		SharedPreferences sp = getActivity().getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
		if (sp.getInt(SP_KEY, 0) == 0){
			Prompt.Dialog(getActivity(), true, "提示", "是否邀请好友？",
					new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog,
								int which) {
							// TODO Auto-generated method stub
							FriendsContactsFragment ifragment = FragmentHelper.getInstance(getActivity()).getFriendsContactsFragment(FriendsContactsFragment.TAG_INVITE);
							switchContent(ifragment);
							Intent iIntent = new Intent(FriendsContactsFragment.FRIENDSCONTACTS_TAB_CHANGED);
							iIntent.putExtra("tab", FriendsContactsFragment.TAG_INVITE);
							LocalBroadcastManager.getInstance(getActivity()).sendBroadcast(iIntent);
						}

					});
			sp.edit().putInt(SP_KEY, 1).commit();
		}else if(sp.getInt(SP_KEY, 0) == 1 || sp.getInt(SP_KEY, 0) == 2){
			Prompt.Dialog(getActivity(), false, "提示", "微享内容是不能删除的", null);
			sp.edit().putInt(SP_KEY, sp.getInt(SP_KEY, 0)+1).commit();
		}
	}*/
	
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
		String refresh_type = "";
		System.out.println("===== pull down");
		if(accountInfo.vshareDataEntities.getCache().size()>0){
			time = accountInfo.vshareDataEntities.getCache().get(0).update_time;
			refresh_type = "1";
		}
		Requester3.myMicList(handler, time , refresh_type, is_encrypt);
	}

	@Override
	public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
		// TODO Auto-generated method stub
		isPullDown = false;
		String time = "";
		String refresh_type = "";
		int size = accountInfo.vshareDataEntities.getCache().size();
		if(size>0){
			time = accountInfo.vshareDataEntities.getCache().get(size-1).update_time;
			refresh_type = "2";
		}
		if(isHasNextPage){
			if(xlv_vshareData != null) {
				xlv_vshareData.setNoMoreData(getActivity(), true);
			}
			Requester3.myMicList(handler, time , refresh_type, is_encrypt);
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
		case Requester3.RESPONSE_TYPE_MYMICLIST:
			try {
				GsonResponse3.myMicListResponse myMicListList = (GsonResponse3.myMicListResponse) msg.obj;
				if (myMicListList != null) {
					if("1".equals(myMicListList.is_refresh)){
						accountInfo.newZoneMicCount = 0;
						Intent msgIntent = new Intent(LookLookActivity.UPDATE_MASK);
						LocalBroadcastManager.getInstance(getActivity()).sendBroadcast(msgIntent);
						accountInfo.vshareDataEntities.clearList();
					}
					
					if(isPullDown){
						accountInfo.newZoneMicCount = 0;
						Intent msgIntent = new Intent(LookLookActivity.UPDATE_MASK);
						LocalBroadcastManager.getInstance(getActivity()).sendBroadcast(msgIntent);
					}
					if (myMicListList.status.equals("0")) {
						if(myMicListList.showmiclist !=null && myMicListList.showmiclist.length>0){
							for(int i=0; i< myMicListList.showmiclist.length;i++){								
								if(isPullDown){
									accountInfo.vshareDataEntities.insertMember(i, myMicListList.showmiclist[i]);
								}else{
									accountInfo.vshareDataEntities.addMember(myMicListList.showmiclist[i]);
								}
								
							}
						}
					/*	vshareListAdapter.setData(accountInfo.vshareDataEntities.getCache());
						vshareListAdapter.notifyDataSetChanged();
						if(vshareListAdapter.getCount()==0){
							iv_nodata.setVisibility(View.VISIBLE);
						}else{
							iv_nodata.setVisibility(View.GONE);
						}*/
						
						loadLocalData();
						
						if(isPullDown){
							getTimemilli();
						}else{
							if("1".equals(myMicListList.hasnextpage)){
								isHasNextPage = true;
							}else{
								isHasNextPage = false;
							}
						}
						/*if("1".equals(myMicListList.hasnextpage)){
							pageno ++;
							Requester3.myMicList(handler, pageno + "", is_encrypt);
						}*/			
					} else if (myMicListList.status.equals("200600")) {
						Prompt.Dialog(getActivity(), false, "提示",
								Constant.CRM_STATUS[Integer
										.parseInt(myMicListList.crm_status)], null);
					} else {
						//Prompt.Dialog(getActivity(), false, "提示", "操作失败，请稍后再试",null);
					}
				} else {
					Prompt.Dialog(getActivity(), false, "提示", "操作失败，网络不给力", null);
				}
			} catch (Exception e) {
				// TODO: handle exception
			}
			xlv_vshareData.onRefreshComplete();
			break;
		case VShareFragment.REFRESH_COMPLETE:
			xlv_vshareData.onRefreshComplete();
			break;
		case Requester3.RESPONSE_TYPE_SAFEBOXMIC:
			try {
				GsonResponse3.safeboxmicResponse safeResponse = (GsonResponse3.safeboxmicResponse) msg.obj;
				if (safeResponse != null) {
					if (safeResponse.status.equals("0")) {
						accountInfo.vshareDataEntities.removeMember(publishid);
						loadLocalData();
					//	Prompt.Dialog(getActivity(), false, "提示", "成功加入保险箱",null);
						Toast.makeText(getActivity(), "成功加入保险箱",
							     1000).show();

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
							Intent shareIntent = new Intent(VShareFragment.this.getActivity(), SettingPersonalInfoActivity.class);
							shareIntent.putExtra("createvshare", "createvshare");
							startActivity(shareIntent);	
						}
					});
					
				}else{
					//新微享
					/*Intent shareIntent = new Intent(VShareFragment.this.getActivity(), ShareDiaryActivity.class);
					shareIntent.putExtra(ShareDialog.TYPY_SHARE, ShareDialog.TYPE_SHARE_V);
					startActivity(shareIntent);*/
					Intent intent = new Intent(VShareFragment.this.getActivity(), ShareSelectMenu.class);
					intent.putExtra(IS_FROM_VSHARE, true);
					startActivityForResult(intent, ShareDiaryActivity.REQUEST_CODE_DIARY);
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
				if(publishuuid!=null && accountInfo.vshareLocalDataEntities.findMemberuuid(publishuuid) != null){
					MiShareTaskManager.getInstance(accountInfo.userid).removeMiShareTask(publishuuid);
					accountInfo.vshareLocalDataEntities.removeMemberuuid(publishuuid);
					loadLocalData();
				}else{
					if("1".equals(accountInfo.vshareDataEntities.findMember(publishid).is_undisturb)){
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
				if(publishuuid!=null && accountInfo.vshareLocalDataEntities.findMemberuuid(publishuuid) != null){
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

	private void getTimemilli(){
		if(accountInfo.vshareDataEntities.getCache().size()>0){
			accountInfo.privateMsgManger.hSubScript.t_zone_mic = accountInfo.vshareDataEntities.getCache().get(0).update_time;
			accountInfo.privateMsgManger.hSubScript.t_zone_miccomment = accountInfo.vshareDataEntities.getCache().get(0).update_time;
			RemoteManager.getInstance(this.getActivity()).CallService(accountInfo);
		}
	}		
	
	
	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		if(mReseiver != null){
			LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(mReseiver);
		}
		if(mMessageReceiver!=null){
			LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(mMessageReceiver);
		}
		if(mSyncTimeReseiver!=null){
			getActivity().unregisterReceiver(mSyncTimeReseiver);
		}
		super.onDestroy();
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		switch(requestCode)
		{
		case ShareDiaryActivity.REQUEST_CODE_DIARY:
			if(resultCode == getActivity().RESULT_OK)
			{
				Intent intent = new Intent(getActivity(), ShareLookLookFriendsActivity.class);
//				intent.putStringArrayListExtra(INTENT_ACTION_SHARE_DIARY_LIST, listStr);
				startActivityForResult(intent, ShareDiaryActivity.REQUEST_CODE_USER);
			}
			break;
		case ShareDiaryActivity.REQUEST_CODE_USER:
			if(resultCode == getActivity().RESULT_OK)
			{
				ArrayList<UserObj> nameList = data.getParcelableArrayListExtra("invite_list");
				Set<UserObj> userGroup = new HashSet<UserObj>();
				if (nameList != null && nameList.size() > 0)
				{
					for (int i = 0; i < nameList.size(); i++)
					{
						UserObj id = nameList.get(i);
						userGroup.add(id);
					}
				}
				
				Intent shareIntent = new Intent(getActivity(), ShareDiaryActivity.class);
				UserObj[] users = userGroup.toArray(new UserObj[userGroup.size()]);
				if(users != null){
					shareIntent.putExtra(ShareDiaryActivity.INTENT_ACTION_SHARE_DIARY_LIST, new Gson().toJson(users));
				}
				
				if (ShareDiaryActivity.diaryGroup!= null && ShareDiaryActivity.diaryGroup.size() > 0) {
					MyDiary[] diarys = ShareDiaryActivity.diaryGroup.toArray(new MyDiary[ShareDiaryActivity.diaryGroup.size()]);
					if (diarys != null) {
						shareIntent.putExtra(DiaryPreviewActivity.INTENT_ACTION_DIARY_STRING, new Gson().toJson(diarys));
					}
				}
				
				startActivity(shareIntent);
				
				// 统计
				CmmobiClickAgentWrapper.onEvent(getActivity(), "micro_contact", "" + nameList.size());
			}
			break;
		}
	}
	
	
}
