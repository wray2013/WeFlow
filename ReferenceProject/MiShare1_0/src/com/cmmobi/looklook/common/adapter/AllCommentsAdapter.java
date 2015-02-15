package com.cmmobi.looklook.common.adapter;

import java.util.ArrayList;
import java.util.Date;

import android.app.Activity;
import android.content.Intent;
import android.text.Html;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cmmobi.looklook.R;
import com.cmmobi.looklook.activity.OtherZoneActivity;
import com.cmmobi.looklook.common.gson.GsonResponse3.diarycommentlistItem;
import com.cmmobi.looklook.common.utils.DateUtils;
import com.cmmobi.looklook.common.view.FriendsExpressionView;
import com.cmmobi.looklook.common.view.InputUtilView;
import com.cmmobi.looklook.common.web.TackView;
import com.cmmobi.looklook.info.profile.ActiveAccount;
import com.nostra13.universalimageloader.core.ImageLoader;

public class AllCommentsAdapter extends BaseAdapter {

	private ArrayList<diarycommentlistItem> contentList;
	private LayoutInflater inflater;
	private InputUtilView ipuView;
	private Activity activity;
	private String mic_userid;
	
	private int[] icons = { R.drawable.wave1, R.drawable.wave2, R.drawable.wave3, };
	private int[] icons_micuser = { R.drawable.yuyinbofang_faqiren_2, R.drawable.yuyinbofang_faqiren_3, R.drawable.yuyinbofang_faqiren_1, };

	public AllCommentsAdapter(Activity activity,
			ArrayList<diarycommentlistItem> list, String mic_userid) {
		contentList = list;
		this.activity = activity;
		inflater = LayoutInflater.from(activity);
		ipuView = new InputUtilView(activity);
		this.mic_userid = mic_userid;
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
		return contentList.size();
	}

	@Override
	public Object getItem(int position) {
		return contentList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		final ViewHolder holder;
		final diarycommentlistItem myCommentListItem = contentList.get(position);
		if(mic_userid == null){
			if (convertView == null || null == convertView.getTag()) {
			holder = new ViewHolder();
			if(mic_userid !=null && mic_userid.equals(myCommentListItem.userid)){
			convertView = inflater.inflate(R.layout.list_all_comment_item_right,
					null);
			}else{
				convertView = inflater.inflate(R.layout.list_all_comment_item,
						null);
			}
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
			
			if(mic_userid !=null && mic_userid.equals(myCommentListItem.userid)){
				holder.audioView.setSoundIcons(icons_micuser);
			}else{
				holder.audioView.setSoundIcons(icons);
			}
			
					convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		}else {
			holder = new ViewHolder();
			if(mic_userid !=null && mic_userid.equals(myCommentListItem.userid)){
			convertView = inflater.inflate(R.layout.list_all_comment_item_right,
					null);
			}else{
				convertView = inflater.inflate(R.layout.list_all_comment_item,
						null);
			}
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
			
			if(mic_userid !=null && mic_userid.equals(myCommentListItem.userid)){
				holder.audioView.setSoundIcons(icons_micuser);
			}else{
				holder.audioView.setSoundIcons(icons);
			}	
		}

		ImageLoader.getInstance().displayImage(myCommentListItem.headimageurl,
				holder.headIcon, ActiveAccount.getInstance(activity).getUID(),
				1);
		holder.headIcon.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				/** 此处添加点击头像跳转至他人空间页处理
				 */
				if(myCommentListItem.userid!=null && !myCommentListItem.userid.equals(ActiveAccount.getInstance(activity).getUID())){
//					Intent intent = new Intent(activity, HomepageOtherDiaryActivity.class);
//					intent.putExtra(HomepageOtherDiaryActivity.INTENT_ACTION_USERID, myCommentListItem.userid);
//					activity.startActivity(intent);
					Intent intent = new Intent(activity, OtherZoneActivity.class);
					intent.putExtra(OtherZoneActivity.OTHER_ZONE_USERID, myCommentListItem.userid);
					activity.startActivity(intent);
				}
			}
		});
		
		// 优先显示备注
		if(!TextUtils.isEmpty(myCommentListItem.nickmarkname)){
			holder.nickname.setText(myCommentListItem.nickmarkname);
		}else{
			holder.nickname.setText(myCommentListItem.nickname);
		}
					
					
		if (!myCommentListItem.commentway.equals("1")){  // 语音 ，语音+文字
			holder.rlyTackArea.setVisibility(View.VISIBLE);
			holder.commentContent.setVisibility(View.GONE);
			
			holder.audioView.setPlaytime(myCommentListItem.playtime);
			
			if (myCommentListItem.audiourl != null) {
//				holder.audioView.setSoundIcons(SOUND_ICONS);
//				holder.audioView.setAudio(myCommentListItem.audiourl, 1);
				holder.audioView.setOnClickListener(new OnClickListener() {
					//点击语音条，播放语音评论
					@Override
					public void onClick(View v) {
						holder.audioView.setAudio(myCommentListItem.audiourl, 1);
//						holder.audioView.playAudio(myCommentListItem.audiourl);
					}
				});
			}
			
			if ("2".equals(myCommentListItem.commenttype)) {
				if(!TextUtils.isEmpty(myCommentListItem.commentcontent)){
					holder.tvTackText.setVisibility(View.VISIBLE);
//					FriendsExpressionView.replacedExpressions("回复<font color=\"black\">"+myCommentListItem.replynickname+"</font>: " + myCommentListItem.commentcontent.trim(), holder.tvTackText);
					String name = "";
					if(!TextUtils.isEmpty(myCommentListItem.replymarkname)){
						name = myCommentListItem.replymarkname;
					}else{
						name = myCommentListItem.replynickname;
					}
					FriendsExpressionView.replacedExpressions("回复<font color=\"black\">"+name+"</font>: " + myCommentListItem.commentcontent.trim(), holder.tvTackText);
				}else{
					holder.tvTackText.setVisibility(View.VISIBLE);
					holder.tvTackText.setText(Html.fromHtml("回复<font color=\"black\">"+myCommentListItem.replynickname+"</font>: "));
				}
				}else{
					if(!TextUtils.isEmpty(myCommentListItem.commentcontent)){
						holder.tvTackText.setVisibility(View.VISIBLE);
						FriendsExpressionView.replacedExpressions(myCommentListItem.commentcontent.trim(), holder.tvTackText);
					}else{
						holder.tvTackText.setVisibility(View.GONE);
					}
				}
		}else{
			holder.rlyTackArea.setVisibility(View.GONE);
			holder.commentContent.setVisibility(View.VISIBLE);
			
			try {
				if ("2".equals(myCommentListItem.commenttype)) {
					String name = "";
					if(!TextUtils.isEmpty(myCommentListItem.replymarkname)){
						name = myCommentListItem.replymarkname;
					}else{
						name = myCommentListItem.replynickname;
					}
					FriendsExpressionView.replacedExpressions("回复<font color=\"black\">"+name+"</font>: " + myCommentListItem.commentcontent.trim(), holder.commentContent);
				}else{
					FriendsExpressionView.replacedExpressions(myCommentListItem.commentcontent.trim(), holder.commentContent);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
			
		String time = DateUtils.getMyCommonListShowDate(new Date(Long
				.parseLong(myCommentListItem.createtime)));
		holder.publishTime.setText(time);
		
		addListener(holder);
		return convertView;
	}

	private void addListener(final ViewHolder holder) {

	}

}
