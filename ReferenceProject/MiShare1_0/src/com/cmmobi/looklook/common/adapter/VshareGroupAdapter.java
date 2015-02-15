package com.cmmobi.looklook.common.adapter;

import java.util.ArrayList;
import java.util.List;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import android.text.Html;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.PopupWindow.OnDismissListener;
import cn.zipper.framwork.io.network.ZNetworkStateDetector;

import com.baidu.location.i;
import com.cmmobi.looklook.R;
import com.cmmobi.looklook.activity.SettingGesturePwdActivity;
import com.cmmobi.looklook.activity.SettingToCreateGestureActivity;
import com.cmmobi.looklook.activity.VshareDetailActivity;
import com.cmmobi.looklook.activity.VshareGroupActivity;
import com.cmmobi.looklook.common.constant.Constant;
import com.cmmobi.looklook.common.gson.GsonResponse3;
import com.cmmobi.looklook.common.gson.Requester3;
import com.cmmobi.looklook.common.gson.GsonResponse3.MicListItem;
import com.cmmobi.looklook.common.service.CoreService;
import com.cmmobi.looklook.common.utils.DateUtils;
import com.cmmobi.looklook.common.utils.DensityUtil;
import com.cmmobi.looklook.common.view.FriendsExpressionView;
import com.cmmobi.looklook.common.view.VshareContentThumbnailView;
import com.cmmobi.looklook.fragment.FragmentHelper;
import com.cmmobi.looklook.fragment.MenuFragment;
import com.cmmobi.looklook.info.profile.AccountInfo;
import com.cmmobi.looklook.info.profile.ActiveAccount;
import com.cmmobi.looklook.info.profile.LoginSettingManager;
import com.cmmobi.looklook.info.profile.TimeHelper;
import com.cmmobi.looklook.prompt.Prompt;
import com.cmmobi.statistics.CmmobiClickAgent;
public class VshareGroupAdapter extends BaseAdapter implements OnClickListener, OnLongClickListener, Callback {

	private Context context;
	private LayoutInflater inflater;
	private List<MicListItem> list = new ArrayList<MicListItem>();
	private List<MicListItemGroup> listGroups = new ArrayList<VshareGroupAdapter.MicListItemGroup>();
	private int size = 0;
	private String is_encrypt;
	//private PopupWindow pw_safebox; 
	
	private Handler handler;
	private String publishid;
	
	private MicListItem mCurrentItem;
	
	public VshareGroupAdapter(final Context context, String is_encrypt) {
		this.context = context;
		DisplayMetrics dm = context.getApplicationContext().getResources().getDisplayMetrics();
		this.size = dm.widthPixels/3 - DensityUtil.dip2px(context, 5);
		inflater = LayoutInflater.from(context);
		this.is_encrypt = is_encrypt;
		handler = new Handler(this);
		//initPopupChoice();
	}
	


	/*public void initPopupChoice(){
		View view = inflater.inflate(R.layout.activity_vshare_group_safebox_menu ,
				null);
		pw_safebox = new PopupWindow(view, LayoutParams.FILL_PARENT,
				LayoutParams.FILL_PARENT, true);
		pw_safebox.setBackgroundDrawable(context.getResources().getDrawable(
				R.color.transparent));

		view.findViewById(R.id.btn_joinsafe).setOnClickListener(this);
		view.findViewById(R.id.btn_cancel).setOnClickListener(this);
	}*/
	
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return listGroups.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return listGroups.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}
	
	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		if (convertView == null) {
			convertView = inflater.inflate(
					R.layout.include_vsharegroup_item, null);
			holder = new ViewHolder();
			holder.vctv_vshare_first = (VshareContentThumbnailView) convertView.findViewById(R.id.vctv_vshare_first);
			holder.vctv_vshare_second = (VshareContentThumbnailView) convertView.findViewById(R.id.vctv_vshare_second);
			holder.vctv_vshare_third = (VshareContentThumbnailView) convertView.findViewById(R.id.vctv_vshare_third);
			RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) holder.vctv_vshare_first.getLayoutParams();
			params.width = size;
			params.height = size + DensityUtil.dip2px(context, 25);
			holder.vctv_vshare_first.setLayoutParams(params);
			params = (RelativeLayout.LayoutParams) holder.vctv_vshare_second.getLayoutParams();
			params.width = size;
			params.height = size + DensityUtil.dip2px(context, 25);
			holder.vctv_vshare_second.setLayoutParams(params);
			params = (RelativeLayout.LayoutParams) holder.vctv_vshare_third.getLayoutParams();
			params.width = size;
			params.height = size + DensityUtil.dip2px(context, 25);
			holder.vctv_vshare_third.setLayoutParams(params);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		
		int i;
		if(listGroups.get(position).threeMicListItems.size() > 0){
			i=0;
			holder.vctv_vshare_first.setContentDiaries(listGroups.get(position).threeMicListItems.get(i).headimageurl, listGroups.get(position).threeMicListItems.get(i).create_time, listGroups.get(position).threeMicListItems.get(i).capsule, listGroups.get(position).threeMicListItems.get(i).burn_after_reading, listGroups.get(position).threeMicListItems.get(i).is_clear, listGroups.get(position).threeMicListItems.get(i).diarys);
			holder.vctv_vshare_first.setViewIsShowComment(false, listGroups.get(position).threeMicListItems.get(i).commentnum);
			holder.vctv_vshare_first.setVisibility(View.VISIBLE);
			holder.vctv_vshare_first.setTag(listGroups.get(position).threeMicListItems.get(i));
			holder.vctv_vshare_first.setOnClickListener(this);
			holder.vctv_vshare_first.setOnLongClickListener(this);
		}else{
			holder.vctv_vshare_first.setVisibility(View.GONE);
		}
		if(listGroups.get(position).threeMicListItems.size() > 1){
			i=1;
			holder.vctv_vshare_second.setContentDiaries(listGroups.get(position).threeMicListItems.get(i).headimageurl, listGroups.get(position).threeMicListItems.get(i).create_time, listGroups.get(position).threeMicListItems.get(i).capsule, listGroups.get(position).threeMicListItems.get(i).burn_after_reading, listGroups.get(position).threeMicListItems.get(i).is_clear, listGroups.get(position).threeMicListItems.get(i).diarys);
			holder.vctv_vshare_second.setViewIsShowComment(false, listGroups.get(position).threeMicListItems.get(i).commentnum);
			holder.vctv_vshare_second.setVisibility(View.VISIBLE);
			holder.vctv_vshare_second.setTag(listGroups.get(position).threeMicListItems.get(i));
			holder.vctv_vshare_second.setOnClickListener(this);
			holder.vctv_vshare_second.setOnLongClickListener(this);
		}else{
			holder.vctv_vshare_second.setVisibility(View.GONE);
		}
		if(listGroups.get(position).threeMicListItems.size() > 2){
			i=2;
			holder.vctv_vshare_third.setContentDiaries(listGroups.get(position).threeMicListItems.get(i).headimageurl, listGroups.get(position).threeMicListItems.get(i).create_time,listGroups.get(position).threeMicListItems.get(i).capsule, listGroups.get(position).threeMicListItems.get(i).burn_after_reading, listGroups.get(position).threeMicListItems.get(i).is_clear, listGroups.get(position).threeMicListItems.get(i).diarys);
			holder.vctv_vshare_third.setViewIsShowComment(false, listGroups.get(position).threeMicListItems.get(i).commentnum);
			holder.vctv_vshare_third.setVisibility(View.VISIBLE);
			holder.vctv_vshare_third.setTag(listGroups.get(position).threeMicListItems.get(i));
			holder.vctv_vshare_third.setOnClickListener(this);
			holder.vctv_vshare_third.setOnLongClickListener(this);
		}else{
			holder.vctv_vshare_third.setVisibility(View.GONE);
		}
		return convertView;
	}
	
	public void setData(List<MicListItem> data){
		if(data != null){
			this.list.clear();
			this.list.addAll(data);
		}
		getMicListItemGroup();
	}
	
	public boolean isEmpty()
	{
		return listGroups.isEmpty();
	}
		
	public class ViewHolder {
		VshareContentThumbnailView vctv_vshare_first;
		VshareContentThumbnailView vctv_vshare_second;
		VshareContentThumbnailView vctv_vshare_third;
	}

	class MicListItemGroup{
		ArrayList<MicListItem> threeMicListItems;
	}
	
	private void getMicListItemGroup(){
		listGroups.clear();
		MicListItemGroup mGroup = new MicListItemGroup();
		for(int i=0; i<list.size(); i++){
			if(i%3 == 0){
				mGroup = new MicListItemGroup();
				mGroup.threeMicListItems = new ArrayList<MicListItem>();
				mGroup.threeMicListItems.add(list.get(i));
				if(i== (list.size()-1)){
					listGroups.add(mGroup);
				}
			}else if(i%3 == 1){
				mGroup.threeMicListItems.add(list.get(i));
				if(i== (list.size()-1)){
					listGroups.add(mGroup);
				}
			}else{
				mGroup.threeMicListItems.add(list.get(i));
				listGroups.add(mGroup);
			}
		}
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		/*case R.id.btn_cancel:
			if(pw_safebox.isShowing()){
				pw_safebox.dismiss();
			}
			break;
		case R.id.btn_joinsafe:
			if(pw_safebox.isShowing()){
				pw_safebox.dismiss();
			}
			LoginSettingManager lsm = AccountInfo.getInstance(ActiveAccount.getInstance(context).getUID()).setmanager;
			if(lsm.getGesturepassword() != null){
				Requester3.safeboxmic(handler, publishid,"1");
			}else{
				//启动创建保险箱流程
				Intent intent = new Intent(context, SettingToCreateGestureActivity.class);
				intent.putExtra(SettingGesturePwdActivity.ACTION_PARAM, VshareGroupActivity.VSHAREGROUP);
				lsm.setIsFromSetting(false);
				context.startActivity(intent);
			}
			break;*/
		default:
			if(v.getTag() != null && v instanceof VshareContentThumbnailView) {
				final MicListItem item = (MicListItem) v.getTag();
				jumpToDetailController(item);
			}
			break;
		}
		
	}
	
	
	public void jumpToDetailAfterSyncTime(){
		if(ZNetworkStateDetector.isConnected()){
			final MicListItem item = mCurrentItem;
			if(item == null) return;
			if(TimeHelper.getInstance().now()>= Long.parseLong(item.capsule_time)){
				if("1".equals(item.burn_after_reading)){
					Prompt.Dialog(context, true, "提示", "该内容已被设置阅后即焚，退出后将无法再次查看", new DialogInterface.OnClickListener(){

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
				Prompt.Dialog(context, false, "提示", Html.fromHtml("亲，不要太心急，时光胶囊再过" + "<font color=\"" + context.getResources().getColor(R.color.blue)+ "\">" + DateUtils.getCountdown(item.capsule_time) + "</font>" + "就能开启啦"), null, R.string.promt_iknow, R.string.promt_iknow);
			}
		}else{
			Prompt.Dialog(context, false, "提示", "操作失败，网络不给力", null);
		}	
		mCurrentItem = null;
	}
	
	private void jumpToDetailController(final MicListItem micListItem){
		if(ZNetworkStateDetector.isConnected()){
			if(micListItem == null) return;
			if("1".equals(micListItem.capsule) && "0".equals(micListItem.is_clear)){
				mCurrentItem = micListItem;
				context.sendBroadcast(new Intent(CoreService.BRODCAST_SYNC_TIME));	
			}else if("1".equals(micListItem.burn_after_reading)){
				Prompt.Dialog(context, true, "提示", "该内容已被设置阅后即焚，退出后将无法再次查看", new DialogInterface.OnClickListener(){

					@Override
					public void onClick(DialogInterface dialog,
							int which) {
						// TODO Auto-generated method stub
						jumpToDetail(micListItem);
					}		
				}, R.string.promt_immediately, R.string.promt_later);

			}else{
				jumpToDetail(micListItem);
			}			
		}else{
			Prompt.Dialog(context, false, "提示", "操作失败，网络不给力", null);
		}	
	}
	
	private void jumpToDetail(final MicListItem item){
		Intent intent = new Intent(context, VshareDetailActivity.class);
		intent.putExtra("publishid", item.publishid);
		intent.putExtra("is_encrypt", is_encrypt);
		intent.putExtra("micuserid", item.micuserid);
		intent.putExtra("create_time", item.create_time);
		intent.putExtra("is_burn", item.burn_after_reading);
		context.startActivity(intent);
		for(int i=0; i< list.size(); i++){
			if(item.publishid.equals(list.get(i).publishid) && !"0".equals(list.get(i).commentnum)){
				list.get(i).commentnum = "0";
				getMicListItemGroup();
				notifyDataSetChanged();
				break;
			}
		}
		MicListItem itemLocal = AccountInfo.getInstance(ActiveAccount.getInstance(context).getUID()).vshareDataEntities.findMember(item.publishid);
		if(itemLocal !=null){
			itemLocal.commentnum = "0";
		}	
	}
	

	@Override
	public boolean onLongClick(View v) {
		// TODO Auto-generated method stub
		/*if(v.getTag() != null && v instanceof VshareContentThumbnailView) {
			MicListItem item = (MicListItem) v.getTag();
			pw_safebox.showAtLocation(((VshareGroupActivity)context).rl_vsharegroup,
					Gravity.BOTTOM, 0, 0);
			publishid = item.publishid;
			
		}*/
		return false;
	}

	@Override
	public boolean handleMessage(Message msg) {
		// TODO Auto-generated method stub
		switch(msg.what){
		case Requester3.RESPONSE_TYPE_SAFEBOXMIC:
			try {
				GsonResponse3.safeboxmicResponse safeResponse = (GsonResponse3.safeboxmicResponse) msg.obj;
				if (safeResponse != null) {
					if (safeResponse.status.equals("0")) {
						for(int i=0; i< list.size(); i++){
							if(publishid.equals(list.get(i).publishid)){
								list.remove(i);
								break;
							}
						}
						getMicListItemGroup();
						notifyDataSetChanged();
						//Prompt.Dialog(context, false, "提示", "成功加入保险箱",null);
						Toast.makeText(context, "成功加入保险箱",
							     1000).show();
					} else if (safeResponse.status.equals("200600")) {
						Prompt.Dialog(context, false, "提示",
								Constant.CRM_STATUS[Integer
										.parseInt(safeResponse.crm_status)], null);
					} else {
						Prompt.Dialog(context, false, "提示", "操作失败，请稍后再试",null);
					}
				} else {
					Prompt.Dialog(context, false, "提示", "操作失败，网络不给力", null);
				}
			} catch (Exception e) {
				// TODO: handle exception
			}
			break;
		default:
			
		}
		return false;
	}
	
/*	public void sendRequest(){
		if(pw_safebox.isShowing()){
			pw_safebox.dismiss();
		}
		LoginSettingManager lsm = AccountInfo.getInstance(ActiveAccount.getInstance(context).getUID()).setmanager;
		if(lsm.getGesturepassword() != null){
			Requester3.safeboxmic(handler, publishid,"1");
		}
	}*/
}