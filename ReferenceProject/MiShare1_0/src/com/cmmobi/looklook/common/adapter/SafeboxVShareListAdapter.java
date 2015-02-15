package com.cmmobi.looklook.common.adapter;

import java.util.ArrayList;

import javax.crypto.spec.PSource;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.text.Html;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import cn.zipper.framwork.io.network.ZNetworkStateDetector;

import com.cmmobi.looklook.R;
import com.cmmobi.looklook.activity.VshareDetailActivity;
import com.cmmobi.looklook.common.gson.GsonResponse3.MicListItem;
import com.cmmobi.looklook.common.service.CoreService;
import com.cmmobi.looklook.common.utils.DateUtils;
import com.cmmobi.looklook.common.utils.DensityUtil;
import com.cmmobi.looklook.common.view.FriendsExpressionView;
import com.cmmobi.looklook.common.view.VshareContentThumbnailView;
import com.cmmobi.looklook.fragment.SafeboxContentFragment;
import com.cmmobi.looklook.info.profile.ActiveAccount;
import com.cmmobi.looklook.info.profile.TimeHelper;
import com.cmmobi.looklook.prompt.Prompt;

/**
 * @author wuxiang
 * @email wuxiang@cmmobi.com
 * @date 2013-12-25
 */
public class SafeboxVShareListAdapter extends SafeboxSubAdapter implements
		OnLongClickListener, OnClickListener {

	private static final String TAG = SafeboxVShareListAdapter.class
			.getSimpleName();
	
	public Boolean isForTimer = false;
	private MicListItem mCurrentItem;

	/**
	 * 清楚选中状态
	 */
	public void purgeCheckedView() {
		checkedList.clear();
		isForTimer = false;
		notifyDataSetChanged();
	}

	/**
	 * 获取选中列表
	 */
	public ArrayList<MicListItem> getCheckedList() {
		return checkedList;
	}

	private Context context;
	private SafeboxContentFragment safeboxFragment;
	ArrayList<MicListItem> shareItems = new ArrayList<MicListItem>();
	private LayoutInflater inflater;
	protected DisplayMetrics dm = new DisplayMetrics();
	private int margin = 5;
	private int size = 0;

	public SafeboxVShareListAdapter(SafeboxContentFragment safeboxFragment,
			ArrayList<MicListItem> shareItems) {
		this.safeboxFragment = safeboxFragment;
		this.context = safeboxFragment.getActivity();
		inflater = LayoutInflater.from(context);
		this.shareItems = shareItems;
		((Activity) context).getWindowManager().getDefaultDisplay()
				.getMetrics(dm);
		this.size = dm.widthPixels / 5;
	}

	@Override
	public int getCount() {
		return shareItems.size();
	}

	@Override
	public Object getItem(int position) {
		return shareItems.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.activity_vshare_list_item,
					null);
			holder = new ViewHolder();
			holder.vctv_vshare = (VshareContentThumbnailView) convertView
					.findViewById(R.id.vctv_vshare);
			RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) holder.vctv_vshare
					.getLayoutParams();
			params.width = size;
			params.height = size;
			holder.vctv_vshare.setLayoutParams(params);
			holder.tv_time = (TextView) convertView.findViewById(R.id.tv_time);
			holder.tv_count = (TextView) convertView
					.findViewById(R.id.tv_count);
			holder.tv_lastMessage = (TextView) convertView
					.findViewById(R.id.tv_last_message);
			holder.tv_content = (TextView) convertView
					.findViewById(R.id.tv_content);
			holder.iv_jiaobiao = (ImageView) convertView
					.findViewById(R.id.iv_jiaobiao);
			holder.iv_error = (ImageView) convertView
					.findViewById(R.id.iv_error);
			holder.iv_checked = (ImageView) convertView
					.findViewById(R.id.iv_checked);
			holder.vContent = convertView.findViewById(R.id.rl_content);
			holder.vContent.setTag(R.id.iv_checked,holder.iv_checked);
			holder.iv_line = (ImageView) convertView.findViewById(R.id.iv_line);
			
			params = (RelativeLayout.LayoutParams) holder.iv_line.getLayoutParams();
			params.setMargins(size + DensityUtil.dip2px(context, 5), 0, 0, 0);
			holder.iv_line.setLayoutParams(params);
			holder.iv_throwaway = (ImageView) convertView.findViewById(R.id.iv_throwaway);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		holder.vContent.setOnClickListener(this);
		holder.vContent.setOnLongClickListener(this);
		holder.vctv_vshare.setOnClickListener(this);
		holder.iv_checked.setOnClickListener(this);
		holder.vContent.setTag(R.id.rl_content, shareItems.get(position));
		holder.iv_checked.setTag(R.id.rl_content, shareItems.get(position));
		holder.vctv_vshare.setTag(R.id.rl_content,shareItems.get(position));
		holder.tv_time.setText(DateUtils.getMyCommonShowDate(shareItems
				.get(position).update_time));
		holder.iv_jiaobiao.setVisibility(View.GONE);
		
		if(checkedList.contains(shareItems.get(position))){
			holder.iv_checked.setVisibility(View.VISIBLE);
		}else{
			holder.iv_checked.setVisibility(View.GONE);
		}
		
		if("1".equals(shareItems.get(position).capsule) && "1".equals(shareItems.get(position).burn_after_reading)){
			holder.iv_throwaway.setVisibility(View.VISIBLE);
		}else{
			holder.iv_throwaway.setVisibility(View.GONE);
		}
		
		if (shareItems.get(position).commentnum != null
				&& !shareItems.get(position).commentnum.equals("0")) {
			if (shareItems.get(position).commentnum.length() > 1) {
				holder.iv_jiaobiao.setVisibility(View.VISIBLE);
				holder.iv_jiaobiao.setImageResource(R.drawable.jiaobiao_2);
			} else if (shareItems.get(position).commentnum.length() == 1
					&& !shareItems.get(position).commentnum.trim().equals("0")) {
				holder.iv_jiaobiao.setVisibility(View.VISIBLE);
				holder.iv_jiaobiao.setImageResource(R.drawable.jiaobiao_1);
			}
			holder.tv_count.setVisibility(View.VISIBLE);
			holder.tv_count.setText(shareItems.get(position).commentnum);
			holder.vctv_vshare.setViewIsShowComment(false);
		} else {
			holder.tv_count.setVisibility(View.GONE);
			holder.iv_jiaobiao.setVisibility(View.GONE);
		}
		
		//（capsule为“0”&& is_clear为“0”）||(capsule为“1” && is_clear为0 && 到期)
		if(("0".equals(shareItems.get(position).capsule) && "0".equals(shareItems.get(position).is_clear) && !ActiveAccount.getInstance(context).getUID().equals(shareItems.get(position).micuserid)) || ("1".equals(shareItems.get(position).capsule) && "0".equals(shareItems.get(position).is_clear) && TimeHelper.getInstance().now() >= Long.parseLong(shareItems.get(position).capsule_time))){
			holder.vctv_vshare.setViewIsShowComment(true);
		}else{
			holder.vctv_vshare.setViewIsShowComment(false);
		}
		
		if("1".equals(shareItems.get(position).capsule) && "0".equals(shareItems.get(position).is_clear)){
			String str = "<font color=\"black\">距离开启：</font>" + "<font color=\"" + context.getResources().getColor(R.color.blue)+ "\">" + DateUtils.getCountdown(shareItems.get(position).capsule_time) + "</font>";
			holder.tv_lastMessage.setText(Html.fromHtml(str));
		}else{		
			if(TextUtils.isEmpty(shareItems.get(position).newcomment)){
				//holder.tv_lastMessage.setText(shareItems.get(position).micusernames);
				FriendsExpressionView.replacedExpressions(shareItems.get(position).micusernames, holder.tv_lastMessage);
			}else{
				FriendsExpressionView.replacedExpressions(shareItems.get(position).newcomment, holder.tv_lastMessage);
			}
		}
		
		if(TextUtils.isEmpty(shareItems.get(position).content)){
			String content = "";// 1视频、2音频 3图片
			Boolean hasVideo = false, hasAudio = false, hasPic = false;
			for(int i=0; i< shareItems.get(position).diarys.length; i++){
				if("1".equals(shareItems.get(position).diarys[i].type)){
					hasVideo = true;
				}else if("2".equals(shareItems.get(position).diarys[i].type)){
					hasAudio = true;
				}else if("3".equals(shareItems.get(position).diarys[i].type)){
					hasPic = true;
				}
			}
			if(hasVideo) content = "[视频]";
			if(hasPic) content = content + "[图片]";
			if(hasAudio) content = content + "[音频]";
			holder.tv_content.setText(content);
		}else{
			FriendsExpressionView.replacedExpressions(shareItems.get(position).content, holder.tv_content);
		}

		if(!isForTimer){
			holder.vctv_vshare.setContentDiaries(shareItems.get(position).headimageurl, null,
				shareItems.get(position).capsule, shareItems.get(position).burn_after_reading, shareItems.get(position).is_clear, shareItems.get(position).diarys);
		}
		if ("1".equals(shareItems.get(position).is_undisturb)) {
			holder.vctv_vshare.setViewIsShowUndisturb(true);
		} else {
			holder.vctv_vshare.setViewIsShowUndisturb(false);
		}
		return convertView;
	}

	private ArrayList<MicListItem> checkedList = new ArrayList<MicListItem>();

	@Override
	public boolean onLongClick(View v) {
		// 1.判断v是否选中
		// 2.未选中时，设置选中，同时记录选中数据
		if (v.getTag(R.id.iv_checked) != null) {
			View checkedView = (View) v.getTag(R.id.iv_checked);
			if (View.GONE == checkedView.getVisibility()) {
				checkedView.setVisibility(View.VISIBLE);
				checkedList.add((MicListItem) v.getTag(R.id.rl_content));
				if (!safeboxFragment.isCheckedTitleShow())
					safeboxFragment.showCheckedTitle();
			}
		}
		return true;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.vctv_vshare:{
			//进入微享详情页
			if(v.getTag(R.id.rl_content)!=null){
				MicListItem micListItem = (MicListItem) v
						.getTag(R.id.rl_content);
				jumpToDetailController(micListItem);
			}
			break;}
		case R.id.iv_checked:{
			v.setVisibility(View.GONE);
			checkedList.remove((MicListItem) v
					.getTag(R.id.rl_content));
			if (0 == checkedList.size())
				safeboxFragment.showNormalTitle();
			break;}
		case R.id.rl_content:
			if (v.getTag(R.id.iv_checked) != null) {
				View checkedView = (View) v.getTag(R.id.iv_checked);
				if (checkedList.size() > 0) {// 选中删除模式
					if (View.GONE == checkedView.getVisibility()) {
						checkedView.setVisibility(View.VISIBLE);
						checkedList
								.add((MicListItem) v.getTag(R.id.rl_content));
					} else {
						checkedView.setVisibility(View.GONE);
						checkedList.remove((MicListItem) v
								.getTag(R.id.rl_content));
						if (0 == checkedList.size())
							safeboxFragment.showNormalTitle();
					}
				} else {// 跳转到微享详情页
					MicListItem micListItem = (MicListItem) v
							.getTag(R.id.rl_content);
					jumpToDetailController(micListItem);
				}
			}

			break;
		default:
			break;
		}
		/*
		 * if(v instanceof VshareContentThumbnailView){
		 * if(((VshareContentThumbnailView) v).getViewSelected()){
		 * ((VshareContentThumbnailView) v).setViewSelected(false);
		 * checkedList.remove(v.getTag());
		 * if(0==checkedList.size())safeboxFragment.showNormalTitle(); }else{
		 * if(checkedList.size()>0){//选中删除模式 ((VshareContentThumbnailView)
		 * v).setViewSelected(true); checkedList.add((MicListItem)v.getTag());
		 * }else{//未选中状态，单击跳转到详情 Log.d(TAG, "onClick to detail"); MicListItem
		 * micListIte=(MicListItem)v.getTag();
		 * if(ZNetworkStateDetector.isAvailable()){ Intent intent = new
		 * Intent(context, VshareDetailActivity.class);
		 * intent.putExtra("publishid", micListIte.publishid);
		 * intent.putExtra("is_encrypt", "1"); intent.putExtra("micuserid",
		 * micListIte.micuserid);
		 * intent.putExtra("join_safebox",micListIte.mic_safebox);
		 * context.startActivity(intent); }else{ Prompt.Dialog(context, false,
		 * "提示", "操作失败，网络不给力", null); } } } }
		 */
	}

	public class ViewHolder {
		VshareContentThumbnailView vctv_vshare;
		TextView tv_time;
		TextView tv_lastMessage;
		TextView tv_count;
		ImageView iv_jiaobiao;
		TextView tv_content;
		ImageView iv_error;
		ImageView iv_checked;// 选中效果图
		View vContent;// 选中效果图
		ImageView iv_line;
		ImageView iv_throwaway;
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
	
	private void jumpToDetail(final MicListItem micListItem){	
		Intent intent = new Intent(context,
				VshareDetailActivity.class);
		intent.putExtra("publishid", micListItem.publishid);
		intent.putExtra("is_encrypt", "1");
		intent.putExtra("micuserid", micListItem.micuserid);
		intent.putExtra("create_time", micListItem.create_time);
		intent.putExtra("is_burn", micListItem.burn_after_reading);
		micListItem.is_clear = "1";
		if(micListItem.commentnum != null && !micListItem.commentnum.isEmpty() && !"0".equals(micListItem.commentnum)){
			micListItem.commentnum = "0";
			isForTimer = false;
			notifyDataSetChanged();
		}
		context.startActivity(intent);
	}
	
	/*
	 * static class ViewHolder { TextView tvDate; TableLayout tlDiaries; }
	 * 
	 * static class ThumbnailViewHolder{ ArrayList<VshareContentThumbnailView>
	 * thumbnailViews=new ArrayList<VshareContentThumbnailView>(); }
	 */

}
