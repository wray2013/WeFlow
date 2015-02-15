package com.cmmobi.looklook.common.adapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.http.entity.mime.content.ContentBody;

import android.content.Context;
import android.location.Location;
import android.text.Html;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.baidu.platform.comapi.map.l;
import com.cmmobi.looklook.R;
import com.cmmobi.looklook.common.gson.GsonResponse3.MicListItem;
import com.cmmobi.looklook.common.utils.DateUtils;
import com.cmmobi.looklook.common.utils.DensityUtil;
import com.cmmobi.looklook.common.view.FriendsExpressionView;
import com.cmmobi.looklook.common.view.VshareContentThumbnailView;
import com.cmmobi.looklook.info.profile.ActiveAccount;
import com.cmmobi.looklook.info.profile.TimeHelper;
import com.cmmobi.sns.utils.TimeUtil;
public class VshareListAdapter extends BaseAdapter {

	private Context context;
	private LayoutInflater inflater;
	private List<MicListItem> list = new ArrayList<MicListItem>();
	private int size = 0;
	public Boolean isForTimer = false;
	
	public VshareListAdapter(final Context context) {
		this.context = context;
		DisplayMetrics dm = context.getApplicationContext().getResources().getDisplayMetrics();
		this.size = dm.widthPixels/5;
		inflater = LayoutInflater.from(context);
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return list.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return list.get(position);
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
					R.layout.activity_vshare_list_item, null);
			holder = new ViewHolder();
			holder.rl_content = (RelativeLayout) convertView.findViewById(R.id.rl_content);
			holder.vctv_vshare = (VshareContentThumbnailView) convertView.findViewById(R.id.vctv_vshare);
			RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) holder.vctv_vshare.getLayoutParams();
			params.width = size;
			params.height = size;
			holder.vctv_vshare.setLayoutParams(params);
			holder.tv_time = (TextView) convertView.findViewById(R.id.tv_time);
			holder.tv_count = (TextView) convertView.findViewById(R.id.tv_count);
			holder.tv_lastMessage = (TextView) convertView.findViewById(R.id.tv_last_message);
			holder.tv_content = (TextView) convertView.findViewById(R.id.tv_content);
			holder.iv_jiaobiao = (ImageView) convertView.findViewById(R.id.iv_jiaobiao);
			holder.iv_error = (ImageView) convertView.findViewById(R.id.iv_error);
			holder.iv_nodata = (ImageView) convertView.findViewById(R.id.iv_nodata);
			holder.iv_line = (ImageView) convertView.findViewById(R.id.iv_line);
			holder.iv_throwaway = (ImageView) convertView.findViewById(R.id.iv_throwaway);
			params = (RelativeLayout.LayoutParams) holder.iv_line.getLayoutParams();
			params.setMargins(size + DensityUtil.dip2px(context, 5), 0, 0, 0);
			holder.iv_line.setLayoutParams(params);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		
		if(list.size() == 1 && (list.get(0).micuserid == null || list.get(0).micuserid.isEmpty())){
			holder.iv_nodata.setVisibility(View.VISIBLE);
			holder.rl_content.setVisibility(View.GONE);
			return convertView;
		}else {
			holder.iv_nodata.setVisibility(View.GONE);
			holder.rl_content.setVisibility(View.VISIBLE);
		}
		
		//holder.vctv_vshare.setContentDiaries(null, null, null);
		holder.tv_time.setText("");
		holder.tv_time.setVisibility(View.VISIBLE);
		holder.tv_count.setText("");
		holder.tv_lastMessage.setText("");
		holder.tv_content.setText("");
		holder.iv_jiaobiao.setVisibility(View.GONE);
		holder.iv_error.setAnimation(null);
		holder.iv_error.setVisibility(View.GONE);
			
		if("1".equals(list.get(position).capsule) && "1".equals(list.get(position).burn_after_reading)){
			holder.iv_throwaway.setVisibility(View.VISIBLE);
		}else{
			holder.iv_throwaway.setVisibility(View.GONE);
		}
		
		holder.tv_time.setText(DateUtils.getMyCommonShowDate(list.get(position).update_time));
		holder.iv_jiaobiao.setVisibility(View.GONE);
		if(list.get(position).commentnum!=null && !list.get(position).commentnum.equals("0")){
			if(list.get(position).commentnum.length()>1){
				holder.iv_jiaobiao.setVisibility(View.VISIBLE);
				holder.iv_jiaobiao.setImageResource(R.drawable.jiaobiao_2);
			}else if(list.get(position).commentnum.length()==1 && !list.get(position).commentnum.trim().equals("0")){
				holder.iv_jiaobiao.setVisibility(View.VISIBLE);
				holder.iv_jiaobiao.setImageResource(R.drawable.jiaobiao_1);
			}
			holder.tv_count.setVisibility(View.VISIBLE);
			holder.tv_count.setText(list.get(position).commentnum);
			holder.vctv_vshare.setViewIsShowComment(false);
		}else{
			holder.tv_count.setVisibility(View.GONE);
			holder.iv_jiaobiao.setVisibility(View.GONE);
		}
		
		//（capsule为“0”&& is_clear为“0”）||(capsule为“1” && is_clear为0 && 到期)
		if(("0".equals(list.get(position).capsule) && "0".equals(list.get(position).is_clear) && !ActiveAccount.getInstance(context).getUID().equals(list.get(position).micuserid)) || ("1".equals(list.get(position).capsule) && "0".equals(list.get(position).is_clear) && TimeHelper.getInstance().now() >= Long.parseLong(list.get(position).capsule_time))){
			holder.vctv_vshare.setViewIsShowComment(true);
		}else{
			holder.vctv_vshare.setViewIsShowComment(false);
		}
			
		
		if("1".equals(list.get(position).capsule) && "0".equals(list.get(position).is_clear)){
			String str = "<font color=\"black\">距离开启：</font>" + "<font color=\"" + context.getResources().getColor(R.color.blue)+ "\">" + DateUtils.getCountdown(list.get(position).capsule_time) + "</font>";
			holder.tv_lastMessage.setText(Html.fromHtml(str));
		}else{
			if(TextUtils.isEmpty(list.get(position).newcomment)){
				//holder.tv_lastMessage.setText(list.get(position).micusernames);
				FriendsExpressionView.replacedExpressions(list.get(position).micusernames, holder.tv_lastMessage);
			}else{
				FriendsExpressionView.replacedExpressions(list.get(position).newcomment, holder.tv_lastMessage);
			}
		}
		
//		holder.tv_content.setText(list.get(position).mic_title);
		if(TextUtils.isEmpty(list.get(position).content)){
			String content = "";// 1视频、2音频 3图片
			Boolean hasVideo = false, hasAudio = false, hasPic = false;
			for(int i=0; i< list.get(position).diarys.length; i++){
				if("1".equals(list.get(position).diarys[i].type)){
					hasVideo = true;
				}else if("2".equals(list.get(position).diarys[i].type)){
					hasAudio = true;
				}else if("3".equals(list.get(position).diarys[i].type)){
					hasPic = true;
				}
			}
			if(hasVideo) content = "[视频]";
			if(hasPic) content = content + "[图片]";
			if(hasAudio) content = content + "[音频]";
			holder.tv_content.setText(content);
		}else{
			FriendsExpressionView.replacedExpressions(list.get(position).content, holder.tv_content);
		}
		
		if(!isForTimer){
			holder.vctv_vshare.setContentDiaries(list.get(position).headimageurl, null, list.get(position).capsule, list.get(position).burn_after_reading, list.get(position).is_clear, list.get(position).diarys);
		}

		
		if("1".equals(list.get(position).is_undisturb)){
			holder.vctv_vshare.setViewIsShowUndisturb(true);
		}else{
			holder.vctv_vshare.setViewIsShowUndisturb(false);
		}
		
		if("0".equals(list.get(position).getUpload_status()) || "2".equals(list.get(position).getUpload_status())){
			RotateAnimation myAnimation_Rotate=new RotateAnimation(0.0f, 719f, Animation.RELATIVE_TO_SELF,0.5f,Animation.RELATIVE_TO_SELF, 0.5f);
			holder.iv_error.setImageResource(R.drawable.vshare_loading);
			holder.iv_error.setVisibility(View.VISIBLE);
			myAnimation_Rotate.setDuration(1200);
			myAnimation_Rotate.setRepeatCount(-1);
			myAnimation_Rotate.setInterpolator(new LinearInterpolator());
			holder.iv_error.setAnimation(myAnimation_Rotate);	
			myAnimation_Rotate.start();
			holder.tv_time.setVisibility(View.GONE);
			RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) holder.tv_content.getLayoutParams();
			params.rightMargin = DensityUtil.dip2px(context, 35);
			holder.tv_content.setLayoutParams(params);
		}else if("1".equals(list.get(position).getUpload_status())){
			holder.iv_error.setImageResource(R.drawable.yichang);
			holder.iv_error.setAnimation(null);
			holder.iv_error.setVisibility(View.VISIBLE);
			holder.tv_time.setVisibility(View.GONE);
			RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) holder.tv_content.getLayoutParams();
			params.rightMargin = DensityUtil.dip2px(context, 35);
			holder.tv_content.setLayoutParams(params);
		}else if("3".equals(list.get(position).getUpload_status()) || "".equals(list.get(position).getUpload_status())){
			holder.iv_error.setAnimation(null);
			holder.iv_error.setImageBitmap(null);
			holder.tv_time.setVisibility(View.VISIBLE);
			holder.iv_error.setVisibility(View.GONE);
		}
		
		return convertView;
	}
	
	public void setData(List<MicListItem> data){
		if(data != null && data.size()>0){
			this.list.clear();
			this.list.addAll(data);
		}else{
			this.list.clear();
			this.list.add(new MicListItem());
		}
	}
	
/*	public void addPreData(List<MicListItem> predata)
	{

		if (predata==null ||predata.size() <= 0)
		{
			return;
		}
		int i = 0;
		while (i < predata.size())
		{
			list.add(i, predata.get(i));
			i++;
		}
		notifyDataSetChanged();
	}
	
	public void addAfterData(List<MicListItem> afterdata)
	{
		if (afterdata == null || afterdata.size() <= 0)
		{
			return;
		}
		list.addAll(afterdata);
		notifyDataSetChanged();
	}
	
*/
	public boolean isEmpty()
	{
		return list.isEmpty();
	}
	
	public List<MicListItem> getDatas(){
		return list;
	}
	
	public MicListItem findItemByPublishId(String publishid){
		if(TextUtils.isEmpty(publishid)) return null;
		for(int i=0;i<list.size();i++){
			if(publishid.equals(list.get(i).publishid)){
				return list.get(i);
			}
		}
		return null;
	}
	
	public class ViewHolder {
		VshareContentThumbnailView vctv_vshare;
		TextView tv_time;
		TextView tv_lastMessage;
		TextView tv_count;
		ImageView iv_jiaobiao;
		TextView tv_content;
		ImageView iv_error;
		ImageView iv_nodata;
		RelativeLayout rl_content;
		ImageView iv_line;
		ImageView iv_throwaway;
	}

	
}