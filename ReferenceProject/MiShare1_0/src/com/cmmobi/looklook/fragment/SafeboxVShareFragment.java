package com.cmmobi.looklook.fragment;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Message;
import android.support.v4.content.LocalBroadcastManager;
import android.text.Html;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import cn.zipper.framwork.io.network.ZNetworkStateDetector;

import com.cmmobi.looklook.MainApplication;
import com.cmmobi.looklook.R;
import com.cmmobi.looklook.common.adapter.SafeboxVShareListAdapter;
import com.cmmobi.looklook.common.gson.GsonResponse3;
import com.cmmobi.looklook.common.gson.GsonResponse3.MicListItem;
import com.cmmobi.looklook.common.gson.GsonResponse3.safeboxmicResponse;
import com.cmmobi.looklook.common.gson.Requester3;
import com.cmmobi.looklook.common.service.CoreService;
import com.cmmobi.looklook.common.service.RemoteManager;
import com.cmmobi.looklook.common.utils.CmmobiClickAgentWrapper;
import com.cmmobi.looklook.common.utils.DateUtils;
import com.cmmobi.looklook.common.view.pulltorefresh.PullToRefreshBase;
import com.cmmobi.looklook.common.view.pulltorefresh.PullToRefreshBase.Mode;
import com.cmmobi.looklook.common.view.pulltorefresh.PullToRefreshBase.OnRefreshListener2;
import com.cmmobi.looklook.common.view.pulltorefresh.PullToRefreshListView;
import com.cmmobi.looklook.info.profile.PrivateMessageManager;
import com.cmmobi.looklook.info.profile.TimeHelper;
import com.cmmobi.looklook.prompt.Prompt;
import com.cmmobi.statistics.CmmobiClickAgent;

/**
 * @author wuxiang
 * @email wuxiang@cmmobi.com
 * @date  2013-12-25
 */
public class SafeboxVShareFragment extends SafeboxSubFragment implements
	OnRefreshListener2<ListView> {
	
	private static final String TAG = SafeboxVShareFragment.class.getSimpleName();
	private View contentView;
	private PullToRefreshListView xlvMySafebox;
	private ListView lvMyZoneList;
	private ArrayList<MicListItem> myVShareItems = new ArrayList<MicListItem>();
	private SafeboxVShareListAdapter myVShareListAdapter;
	private Boolean isHasNextPage = true;
	
	private Timer timer;
	
	private BroadcastReceiver mSyncTimeReseiver = new BroadcastReceiver(){
		 @Override
		  public void onReceive(Context context, Intent intentr) {    
			    // Get extra data included in the Intent
				if(CoreService.BRODCAST_SYNC_TIME_DONE.equals(intentr.getAction())){
					System.out.println("=== TimeHelper sync time done ====" + TimeHelper.getInstance().now());
					myVShareListAdapter.isForTimer = true;
					myVShareListAdapter.notifyDataSetChanged();
					myVShareListAdapter.jumpToDetailAfterSyncTime();
				}
		  }
	};
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		contentView = inflater.inflate(R.layout.safebox_me, null);
		xlvMySafebox = (PullToRefreshListView) contentView
				.findViewById(R.id.xlv_my_safebox);
		xlvMySafebox.setShowIndicator(false);
		xlvMySafebox.setOnRefreshListener(this);
		lvMyZoneList = xlvMySafebox.getRefreshableView();
		SafeboxContentFragment safeboxContentFragment=FragmentHelper.getInstance(getActivity()).getSafeboxContentFragment();
		myVShareListAdapter = new SafeboxVShareListAdapter(safeboxContentFragment, myVShareItems);
		lvMyZoneList.setAdapter(myVShareListAdapter);
		isUp=false;
		Requester3.myMicList(handler, "","",is_encrypt);
		getActivity().registerReceiver(mSyncTimeReseiver, new IntentFilter(CoreService.BRODCAST_SYNC_TIME_DONE));
		
		return contentView;
	}
	
	@Override
	public void onHiddenChanged(boolean hidden) {
		// TODO Auto-generated method stub
		super.onHiddenChanged(hidden);
		if(hidden){
			if(timer!=null){
				timer.cancel();
				timer = null;
			}
		}else{
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
		System.out.println("SafeboxVShareFragment === onHiddenChanged" + hidden);
	}
	
	@Override
	public void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
		System.out.println("SafeboxVShareFragment === onStop");
		if(timer!=null){
			timer.cancel();
			timer = null;
		}
		}

	@Override
	public void delete() {
		// TODO Auto-generated method stub
		
	}

	private ArrayList<MicListItem> checkedList=new ArrayList<MicListItem>();
	public static final String ACTION_VSHARE_CHANGED="ACTION_VSHARE_CHANGED";
	@Override
	public void removeSafebox() {
		if(myVShareListAdapter!=null){
			checkedList.clear();
			checkedList=(ArrayList<MicListItem>) myVShareListAdapter.getCheckedList().clone();
			String publishids=getDiaryPublishid(checkedList);
			if(!TextUtils.isEmpty(publishids)){
				int num=publishids.split(",").length;
				HashMap<String, String> hs=new HashMap<String, String>();
				hs.put("label", "2");
//				hs.put("labe12", publishids);
				hs.put("labe12", num+"");
				//2014-4-8 wuxiang
				CmmobiClickAgentWrapper.onEvent(getActivity(), "out_safe_box", hs);
				Requester3.safeboxmic(handler, publishids, "0");
				Prompt.showProgressDialog(getActivity());
			}
		}
	}
	
	// 从指定列表中返回日记组uuid，逗号分隔
		private String getDiaryPublishid(ArrayList<MicListItem> checkedList) {
			String publishids = "";
			if (checkedList != null) {
				for (int i = 0; i < checkedList.size(); i++) {
					MicListItem micListItem = checkedList.get(i);
					if (micListItem != null) {
						publishids += micListItem.publishid;
						if (i < checkedList.size() - 1) {
							publishids += ",";
						}
					}
				}
			}
			return publishids;
		}

	@Override
	public void purgeCheckedView() {
		if(myVShareListAdapter!=null)
			myVShareListAdapter.purgeCheckedView();
	}

	@Override
	public void updateViews(Object data) {
		if(null==contentView)return;
		isUp=false;
		Requester3.myMicList(handler, "","",is_encrypt);
	}
	
	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		System.out.println("SafeboxVShareFragment === onResume" + isVisible());
		if(isVisible()){
			isUp=false;
			Requester3.myMicList(handler, "","",is_encrypt);
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
	}

	private String is_encrypt = "1";
	
	private String firstTime="";
	private String lastTime="";
	private boolean isUp=false;
	@Override
	public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
		isUp=false;
		lastTime="";
		Requester3.myMicList(handler, "","1",is_encrypt);
	}

	@Override
	public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
		isUp=true;
		if(isHasNextPage){
			if(xlvMySafebox != null) {
				xlvMySafebox.setNoMoreData(getActivity(), true);
			}
		} else {
			if(xlvMySafebox != null) {
				xlvMySafebox.setNoMoreData(getActivity(), false);
			}
		}
		Requester3.myMicList(handler, lastTime,"2",is_encrypt);
	}
	
	private void removeCheckedList(){
		myVShareItems.removeAll(checkedList);
		// 显示正常标题栏
		showNormalTitle();
		myVShareListAdapter.purgeCheckedView();
	}
	
	public void showNormalTitle() {
		SafeboxContentFragment safeboxContentFragment=FragmentHelper.getInstance(getActivity()).getSafeboxContentFragment();
		safeboxContentFragment.showNormalTitle();
	}

	@Override
	public boolean handleMessage(Message msg) {
		switch(msg.what){
		case Requester3.RESPONSE_TYPE_SAFEBOXMIC:{
			Prompt.dimissProgressDialog();
			safeboxmicResponse response=(safeboxmicResponse) msg.obj;
			if(response!=null&&response.status.equals("0")){
				//Prompt.Dialog(getActivity(), false, "提示", "成功移出保险箱",null);
				Toast.makeText(getActivity(), "成功移出保险箱",
					     1000).show();
				removeCheckedList();
				LocalBroadcastManager.getInstance(getActivity()).sendBroadcast(new Intent(ACTION_VSHARE_CHANGED));
			}
			break;}
		case Requester3.RESPONSE_TYPE_MYMICLIST:
			GsonResponse3.myMicListResponse myMicListList = (GsonResponse3.myMicListResponse) msg.obj;
			if (myMicListList != null) {
				if (myMicListList.status.equals("0")) {
					
					if(myMicListList.showmiclist !=null && myMicListList.showmiclist.length>0){
						if(TextUtils.isEmpty(firstTime)||Long.parseLong(firstTime)<Long.parseLong(myMicListList.first_comment_time)){
							firstTime=myMicListList.first_comment_time;
							PrivateMessageManager pmm = accountInfo.privateMsgManger;
							pmm.hSubScript.t_safebox_miccomment=firstTime;
							try {
								RemoteManager.getInstance(MainApplication.getAppInstance()).CallService(accountInfo);
							} catch (Exception e) {
								// TODO: handle exception
								e.printStackTrace();
							}
						}
					}
					if(!isUp) {//下拉刷新，清空缓存
						myVShareItems.clear();
					} else {
						if("1".equals(myMicListList.hasnextpage)){
							isHasNextPage = true;
						}else{
							isHasNextPage = false;
						}
					}
					if(myMicListList.showmiclist !=null && myMicListList.showmiclist.length>0){
						if(TextUtils.isEmpty(lastTime)||Long.parseLong(lastTime)>Long.parseLong(myMicListList.last_comment_time))
							lastTime=myMicListList.last_comment_time;
						myVShareItems.addAll(Arrays.asList(myMicListList.showmiclist));
					}
					myVShareListAdapter.isForTimer = false;
					myVShareListAdapter.notifyDataSetChanged();
				}
			}
			xlvMySafebox.onRefreshComplete();
			break;
		case VShareFragment.MSG_UPDATE_DATA:
			myVShareListAdapter.isForTimer = true;
			myVShareListAdapter.notifyDataSetChanged();
			break;
		default:
			break;
		}
		return false;
	}
	/*private String lastDate="";
	private ArrayList<VShareItem> getVShareItems(MicListItem[] micListItems){
		ArrayList<VShareItem> shareItems=new ArrayList<VShareItem>();
		if(micListItems!=null){
			Date today=new Date();
			VShareItem shareItem=null;
			for(int i=0;i<micListItems.length;i++){
				MicListItem micListItem=micListItems[i];
				String strDate=DiaryManager.getInstance().getMyZoneShowDate(micListItem.update_time, today);
				if("".equals(lastDate)||!lastDate.equals(strDate)||(shareItem!=null&&shareItem.micListItems.size()>2)){
					shareItem=new VShareItem();
					shareItems.add(shareItem);
				}
				if(shareItem!=null){
					shareItem.micListItems.add(micListItem);
					shareItem.strDate=strDate;
					lastDate=strDate;
				}else{
					Log.e(TAG, "shareItem is null");
				}
			}
		}
		return shareItems;
	}*/
	
	/*public class VShareItem{
		public String strDate;
		public ArrayList<MicListItem> micListItems=new ArrayList<MicListItem>();
	}*/
	
	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		if(mSyncTimeReseiver!=null){
			getActivity().unregisterReceiver(mSyncTimeReseiver);
		}
		super.onDestroy();
	}
}
