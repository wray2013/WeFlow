package com.cmmobi.looklook.common.adapter;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cmmobi.looklook.R;
import com.cmmobi.looklook.common.gson.WeiboResponse.RWComment;
import com.cmmobi.looklook.common.gson.WeiboResponse.RWCommentsInfo;
import com.cmmobi.looklook.common.gson.WeiboResponse.SinaComments;
import com.cmmobi.looklook.common.gson.WeiboResponse.TencentCommentInfo;
import com.cmmobi.looklook.common.gson.WeiboResponse.TencentComments;
import com.cmmobi.looklook.common.gson.WeiboResponse.sinComment;
import com.cmmobi.looklook.common.utils.DateUtils;
import com.cmmobi.looklook.common.utils.RenrenCmmLoader.TaskInfo;
import com.cmmobi.looklook.common.web.TackView;
import com.cmmobi.looklook.info.profile.ActiveAccount;
import com.nostra13.universalimageloader.core.ImageLoader;

public class AllWeiboCommentsAdapter extends BaseAdapter {

	private LayoutInflater inflater;
	private Activity activity;
	
	// 评论列表
	private SinaComments mSinaComments = null;
	private RWCommentsInfo mRenrenComments = null;
	private TencentComments mTencentComments = null;
	
	private Map<Long, TaskInfo> mCommentInfoList = new HashMap<Long, TaskInfo>();
	
//	private int[] icons = { R.drawable.wave1, R.drawable.wave2, R.drawable.wave3, };
	
	public AllWeiboCommentsAdapter(Activity activity,
			Object list) {
		if(list instanceof SinaComments)
		{
			mSinaComments = (SinaComments) list;
		}
		else if(list instanceof RWCommentsInfo)
		{
			mRenrenComments = (RWCommentsInfo) list;
		}
		else if(list instanceof TencentComments)
		{
			mTencentComments = (TencentComments) list;
		}
		this.activity = activity;
		inflater = LayoutInflater.from(activity);
	}
	
	public AllWeiboCommentsAdapter(Activity activity,
			Object list, Map<Long, TaskInfo> map) {
		mRenrenComments = (RWCommentsInfo) list;
		mCommentInfoList = map;
		this.activity = activity;
		inflater = LayoutInflater.from(activity);
	}

	class ViewHolder {
		ImageView headIcon;;
		TextView nickname;
		TextView commentContent;
		TextView publishTime;
		TackView audioView;
		RelativeLayout rlyTackArea;
		TextView tvTackText;
	}


	@Override
	public int getCount() {
		if(mSinaComments != null)
			return mSinaComments.comments.length;
		if(mTencentComments != null)
			return mTencentComments.data.info.length;
		if(mRenrenComments != null)
			return mRenrenComments.response.length;
		return 0;
	}

	@Override
	public Object getItem(int position) {
		if(mSinaComments != null)
			return mSinaComments.comments[position];
		if(mTencentComments != null)
			return mTencentComments.data.info[position];
		if(mRenrenComments != null)
			return mRenrenComments.response[position];
		return 0;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		final ViewHolder holder;
		if (convertView == null || null == convertView.getTag()) {
			holder = new ViewHolder();
			convertView = inflater.inflate(R.layout.list_all_comment_item,
					null);
			holder.headIcon = (ImageView) convertView
					.findViewById(R.id.iv_portrait);
			holder.nickname = (TextView) convertView
					.findViewById(R.id.tv_nickname);
			holder.commentContent = (TextView) convertView
					.findViewById(R.id.tv_comment_text);
			holder.publishTime = (TextView) convertView
					.findViewById(R.id.tv_comment_time);
			holder.rlyTackArea = (RelativeLayout) convertView
					.findViewById(R.id.rly_tack_area);
			holder.audioView = (TackView) convertView
					.findViewById(R.id.comment_tackview);
			holder.tvTackText = (TextView) convertView
					.findViewById(R.id.tv_tack_text);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		
		if(mSinaComments != null)
		{
			final sinComment myCommentListItem = mSinaComments.comments[position];
	
			ImageLoader.getInstance().displayImage(myCommentListItem.user.profile_image_url,
					holder.headIcon, ActiveAccount.getInstance(activity).getUID(),
					1);
			
			holder.nickname.setText(myCommentListItem.user.name);
			
			holder.rlyTackArea.setVisibility(View.GONE);
			holder.commentContent.setVisibility(View.VISIBLE);
				
			holder.commentContent.setText(myCommentListItem.text);
				
			try
			{
				//Wed Jun 01 00:50:25 +0800 2011
				Date d = new Date(myCommentListItem.created_at);
				String time = DateUtils.getMyCommonShowDate(d);
				holder.publishTime.setText(time);
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
			
			addListener(holder);
			return convertView;
		}
		if(mRenrenComments != null)
		{
			RWComment myCommentListItem = mRenrenComments.response[position];
	
			TaskInfo userInfo = mCommentInfoList.get(myCommentListItem.authorId);
			String url = "";
			String name = "";
			if(userInfo != null)
			{
				url = userInfo.imageUrl;
				name = userInfo.name;
			}
			
			ImageLoader.getInstance().displayImage(url,
					holder.headIcon, ActiveAccount.getInstance(activity).getUID(),
					1);
			
			holder.nickname.setText(name);
			
			holder.rlyTackArea.setVisibility(View.GONE);
			holder.commentContent.setVisibility(View.VISIBLE);
				
			holder.commentContent.setText(myCommentListItem.content);
				
			try
			{
				//2014-01-16 10:53:18
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				Date d = sdf.parse(myCommentListItem.time);
				String time = DateUtils.getMyCommonShowDate(d);
				
				holder.publishTime.setText(time);;
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
			
			addListener(holder);
			return convertView;
		}
		if(mTencentComments != null)
		{
			final TencentCommentInfo myCommentListItem = mTencentComments.data.info[position];
	
			ImageLoader.getInstance().displayImage(myCommentListItem.head,
					holder.headIcon, ActiveAccount.getInstance(activity).getUID(),
					1);
			
			holder.nickname.setText(myCommentListItem.nick);
			
			holder.rlyTackArea.setVisibility(View.GONE);
			holder.commentContent.setVisibility(View.VISIBLE);
				
			holder.commentContent.setText(myCommentListItem.text);
				
			try
			{
				String time = DateUtils.getMyCommonShowDate(new Date(Long
						.parseLong(myCommentListItem.timestamp)*1000));
				holder.publishTime.setText(time);
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
			
			addListener(holder);
			return convertView;
		}
		return null;
	}

	private void addListener(final ViewHolder holder) {

	}

}
