package com.cmmobi.looklook.common.adapter;

import java.util.ArrayList;
import java.util.Date;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cmmobi.looklook.R;
import com.cmmobi.looklook.activity.OtherZoneActivity;
import com.cmmobi.looklook.activity.VshareDetailActivity;
import com.cmmobi.looklook.activity.VshareDetailActivity.CommentItem;
import com.cmmobi.looklook.activity.VshareDetailActivity.VshareDetail;
import com.cmmobi.looklook.activity.VshareDetailActivity.VshareDetailItem;
import com.cmmobi.looklook.common.gson.GsonResponse3.DiaryDetailComment;
import com.cmmobi.looklook.common.gson.GsonResponse3.addfriendResponse;
import com.cmmobi.looklook.common.gson.Requester3;
import com.cmmobi.looklook.common.utils.CmmobiClickAgentWrapper;
import com.cmmobi.looklook.common.utils.DateUtils;
import com.cmmobi.looklook.common.utils.DensityUtil;
import com.cmmobi.looklook.common.view.DiaryDetailContentView;
import com.cmmobi.looklook.common.view.FriendsExpressionView;
import com.cmmobi.looklook.common.view.InputUtilView;
import com.cmmobi.looklook.common.view.XEditDialog;
import com.cmmobi.looklook.common.web.TackView;
import com.cmmobi.looklook.info.profile.AccountInfo;
import com.cmmobi.looklook.info.profile.ActiveAccount;
import com.cmmobi.looklook.info.profile.ContactManager;
import com.cmmobi.looklook.info.profile.WrapUser;
import com.cmmobi.looklook.prompt.Prompt;
import com.nostra13.universalimageloader.core.ImageLoader;

public class VshareDetailCommentsAdapter extends BaseAdapter implements OnClickListener,Callback {

	LayoutInflater inflater;
	ArrayList<VshareDetailItem> contentList = new ArrayList<VshareDetailItem>();
	VshareDetailActivity activity;
	InputUtilView ipuView;
    
	private int[] icons = { R.drawable.wave1, R.drawable.wave2, R.drawable.wave3, };
	
	private Handler handler;
	
	public VshareDetailCommentsAdapter(VshareDetailActivity activity) {
		this.activity = activity;
		inflater = LayoutInflater.from(activity);
		handler = new Handler(this);
		ipuView = new InputUtilView(activity);
	}

	class CommentViewHolder {
		ImageView headIcon;
		TextView nickname;
		TextView commentContent;
		TextView publishTime;
		TackView audioView;
		RelativeLayout rlyTackArea;
		TextView tvTackText;
	}
	
	class ContentViewHolder {
//		public TextView mTvShareTitle;
		public TextView mTVShareTime;
		public TextView mTVShareCommentHint;
		//分享内容
		public DiaryDetailContentView diaryDetailContentView;
		//评论按钮
		public ImageButton mBtnComment;
		public TextView mTvSharePosition;
		public ImageView mIvSharePosition;
		public ImageView mIvline, mIvBurn, mIvShareTime;
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

	public void setList(ArrayList<VshareDetailItem> contentList){
		this.contentList.clear();
		this.contentList.addAll(contentList);
	}
	
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		Log.e("getView", "getView has beel called!");
		if(position == 0){
			final ContentViewHolder holder;
			final VshareDetail mContent = (VshareDetail)contentList.get(position);
			if (convertView == null || null == convertView.getTag() || !(convertView.getTag() instanceof ContentViewHolder)) {
				holder = new ContentViewHolder();
				convertView = inflater.inflate(R.layout.vshare_detail_content_item,
						null);
				holder.mBtnComment = (ImageButton) convertView.findViewById(R.id.ib_operate_comment);
				holder.mTVShareCommentHint = (TextView) convertView.findViewById(R.id.tv_comment_hint);
				holder.diaryDetailContentView = (DiaryDetailContentView) convertView.findViewById(R.id.ddc_diarydetail);
//				holder.mTvShareTitle = (TextView) convertView.findViewById(R.id.tv_vsharetitle);
				holder.mTVShareTime = (TextView) convertView.findViewById(R.id.tv_createtime);
				holder.mIvShareTime = (ImageView) convertView.findViewById(R.id.iv_time);
				holder.mTvSharePosition = (TextView) convertView.findViewById(R.id.tv_position);
				holder.mIvSharePosition = (ImageView) convertView.findViewById(R.id.iv_position);
				holder.mIvline = (ImageView) convertView.findViewById(R.id.iv_line);
				holder.mIvBurn = (ImageView) convertView.findViewById(R.id.iv_vshare_burn);
				convertView.setTag(holder);
			} else {
				holder = (ContentViewHolder) convertView.getTag();
			}
			
			DisplayMetrics dm = activity.getResources().getDisplayMetrics();
			/*RelativeLayout.LayoutParams lp = (android.widget.RelativeLayout.LayoutParams) holder.mTVShareCommentHint.getLayoutParams();
			lp.rightMargin = dm.widthPixels * 58 / 640;
			holder.mTVShareCommentHint.setLayoutParams(lp);*/
			holder.mTVShareCommentHint.setTextSize(TypedValue.COMPLEX_UNIT_SP, px2sp(activity, 20f));
			
			RelativeLayout.LayoutParams paramsTime = (RelativeLayout.LayoutParams) holder.mIvShareTime.getLayoutParams();
			RelativeLayout.LayoutParams paramsPos = (RelativeLayout.LayoutParams) holder.mIvSharePosition.getLayoutParams();
			
			paramsTime.setMargins((int)((float)dm.widthPixels * 169/1080 + DensityUtil.dip2px(activity, 20)), 0, 0, 0);
			paramsPos.setMargins((int)((float)dm.widthPixels * 169/1080  + DensityUtil.dip2px(activity, 20)), 0, 0, 0);
			
			holder.mIvShareTime.setLayoutParams(paramsTime);
			holder.mIvSharePosition.setLayoutParams(paramsPos);
			
			holder.mBtnComment.setOnClickListener(this);
			
			if(mContent.miShareinfo != null){
//				holder.diaryDetailContentView.setContent(mContent.miShareinfo);
				holder.diaryDetailContentView.setContent(mContent);
			}
			//holder.mTvShareTitle.setText(mContent.miShareinfo.mic_title);
//			FriendsExpressionView.replacedExpressions(mContent.miShareinfo.mic_title, holder.mTvShareTitle);
			/*holder.mTVShareTime.setText(DateUtils.getDetailShowDate(new Date(Long
					.parseLong(mContent.miShareinfo.create_time))));*/
			holder.mTVShareTime.setText(DateUtils.getStringFromMilli(
					mContent.miShareinfo.create_time,"yyyy.MM.dd HH:mm"));
			if("1".equals(mContent.miShareinfo.position_status) && !TextUtils.isEmpty(mContent.miShareinfo.position)){
				holder.mTvSharePosition.setText(mContent.miShareinfo.position);
				holder.mTvSharePosition.setVisibility(View.VISIBLE);
				holder.mIvSharePosition.setVisibility(View.VISIBLE);
			}else{
				holder.mTvSharePosition.setVisibility(View.GONE);
				holder.mIvSharePosition.setVisibility(View.GONE);
			}
			
			if(contentList.size()>1){
				holder.mIvline.setVisibility(View.VISIBLE);
			}else{
				holder.mIvline.setVisibility(View.GONE);
			}
			
			if(mContent.isBurn) {
				holder.mIvBurn.setVisibility(View.VISIBLE);
				holder.mTVShareCommentHint.setVisibility(View.VISIBLE);
				holder.mBtnComment.setVisibility(View.GONE);
			} else {
				holder.mIvBurn.setVisibility(View.GONE);
				holder.mTVShareCommentHint.setVisibility(View.GONE);
				holder.mBtnComment.setVisibility(View.VISIBLE);
			}
			
		}else{
			final CommentViewHolder holder;
			final DiaryDetailComment mComment= ((CommentItem)contentList.get(position)).comment;
			if (convertView == null || null == convertView.getTag() || !(convertView.getTag() instanceof CommentViewHolder)) {
				holder = new CommentViewHolder();
				convertView = inflater.inflate(R.layout.vshare_detail_comment_item,
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
				holder = (CommentViewHolder) convertView.getTag();
			}

			ImageLoader.getInstance().displayImage(mComment.headimageurl,
					holder.headIcon, ActiveAccount.getInstance(activity).getUID(),
					1);
			holder.headIcon.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					try {
						if(mComment.userid!=null){
							String myuid = ActiveAccount.getInstance(activity).getUID();
							if(mComment.userid.equals(myuid)){
								// 不处理我的icon
							}else{
								// 验证是否是自己的好友
								AccountInfo accinfo = AccountInfo.getInstance(myuid);
								ContactManager friendsListContactManager=accinfo.friendsListName;
								WrapUser currUserInfo=friendsListContactManager.findUserByUserid(mComment.userid);
								
								if(currUserInfo == null){
									if(mComment.userid.equals(accinfo.serviceUser.userid)){
										// 是客服，并跳转
	//										Intent intent = new Intent(activity, OtherZoneActivity.class);
	//										intent.putExtra(OtherZoneActivity.OTHER_ZONE_USERID, mComment.userid);
	//										activity.startActivity(intent);
									}else{
									// 不在好友列表中
									new XEditDialog.Builder(activity)
									.setTitle(R.string.xeditdialog_title)
									.setPositiveButton(R.string.send, new OnClickListener() {
										@Override
										public void onClick(View v) {
											//加好友
											Requester3.addFriend(handler, mComment.userid, v.getTag().toString());
										}
									})
									.setNegativeButton(android.R.string.cancel, null)
									.create().show();
									}
								}else{
									// 是好友，并跳转
									Intent intent = new Intent(activity, OtherZoneActivity.class);
									intent.putExtra(OtherZoneActivity.OTHER_ZONE_USERID, mComment.userid);
									activity.startActivity(intent);
								}
							}
						}
					} catch (Exception e) {
					}
				}
			});
			
			
			String name = "";
			// 优先显示备注
			if(!TextUtils.isEmpty(mComment.nickmarkname)){
				name = mComment.nickmarkname;
			}else{
				name = mComment.nickname;
			}
			
			// 回复
			if("2".equals(mComment.commenttype)){
				String replyName = "";
				if(!TextUtils.isEmpty(mComment.replymarkname)){
					replyName = mComment.replymarkname;
				}else{
					replyName = mComment.replynickname;
				}
				//holder.nickname.setText(Html.fromHtml(name + "<font color=\"black\">回复</font>" +  replyName));
				FriendsExpressionView.replacedExpressionsReply(name + "回复" +  replyName, holder.nickname, name.length(), name.length()+2);			
			}else{
				//holder.nickname.setText(name);
				FriendsExpressionView.replacedExpressions(name, holder.nickname);
			}
			
			if (!mComment.commentway.equals("1")){  // 语音 ，语音+文字
				holder.rlyTackArea.setVisibility(View.VISIBLE);
				holder.commentContent.setVisibility(View.GONE);
				
				holder.audioView.setSoundIcons(icons);
				holder.audioView.setPlaytime(mComment.playtime);
				if (mComment.audiourl != null) {
					holder.audioView.setRecogniPath(mComment.audiourl);
					holder.audioView.setOnClickListener(new OnClickListener() {
						//点击语音条，播放语音评论
						@Override
						public void onClick(View v) {
//							holder.audioView.playAudio(myCommentListItem.audiourl);
							holder.audioView.setAudio(mComment.audiourl, 1);
						}
					});
				}
				
				if(!TextUtils.isEmpty(mComment.commentcontent)){
					holder.tvTackText.setVisibility(View.VISIBLE);
					FriendsExpressionView.replacedExpressions(mComment.commentcontent.trim(), holder.tvTackText);
				}else{
					holder.tvTackText.setVisibility(View.GONE);
				}
				
			}else{
				holder.rlyTackArea.setVisibility(View.GONE);
				holder.commentContent.setVisibility(View.VISIBLE);
				
				try {
					FriendsExpressionView.replacedExpressions(mComment.commentcontent.trim(), holder.commentContent);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			
				
			String time = DateUtils.getMyCommonListShowDate(new Date(Long
					.parseLong(mComment.createtime)));
			holder.publishTime.setText(time);
		}
		
		return convertView;
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.ib_operate_comment:
			// 20140408
			CmmobiClickAgentWrapper.onEvent(this.activity, "sh_comment");
			activity.showCommentInputView();
			break;

		default:
			break;
		}
	}

	@Override
	public boolean handleMessage(Message msg) {
		switch (msg.what) {
		case Requester3.RESPONSE_TYPE_ADD_FRIEND:
			if (msg.obj != null) {
				addfriendResponse response = (addfriendResponse) msg.obj;
				if ("0".equals(response.status)) {
					if(response.target_userid == null){
						break;
					}
					Prompt.Alert(activity, "好友申请已发送");
				}
			}
			break;
		default:
			break;
		}
		return false;
	}

	public static int px2sp(Context context, float pxValue) {
		final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
		return (int) (pxValue / fontScale + 0.5f);
	}

}
