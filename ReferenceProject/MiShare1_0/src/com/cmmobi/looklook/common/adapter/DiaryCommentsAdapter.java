package com.cmmobi.looklook.common.adapter;

import java.util.ArrayList;
import java.util.Date;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import android.text.Html;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.FrameLayout.LayoutParams;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import cn.zipper.framwork.core.ZApplication;

import com.cmmobi.looklook.R;
import com.cmmobi.looklook.activity.DiaryDetailDefines;
import com.cmmobi.looklook.activity.OtherZoneActivity;
import com.cmmobi.looklook.activity.PraiseListActivity;
import com.cmmobi.looklook.common.gson.GsonResponse3.DiaryDetailComment;
import com.cmmobi.looklook.common.gson.GsonResponse3.EnjoyHead;
import com.cmmobi.looklook.common.gson.GsonResponse3.MyDiary;
import com.cmmobi.looklook.common.gson.GsonResponse3.addfriendResponse;
import com.cmmobi.looklook.common.gson.Requester3;
import com.cmmobi.looklook.common.utils.DateUtils;
import com.cmmobi.looklook.common.view.DiaryDetailPraiseGroup;
import com.cmmobi.looklook.common.view.FriendsExpressionView;
import com.cmmobi.looklook.common.view.XEditDialog;
import com.cmmobi.looklook.common.web.TackView;
import com.cmmobi.looklook.info.profile.AccountInfo;
import com.cmmobi.looklook.info.profile.ActiveAccount;
import com.cmmobi.looklook.prompt.Prompt;
import com.nostra13.universalimageloader.api.AnimateFirstDisplayListener;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.SimpleBitmapDisplayer;


public class DiaryCommentsAdapter extends BaseAdapter implements Callback
{
	private int[] icons = { R.drawable.wave1, R.drawable.wave2, R.drawable.wave3, };
	
	private Handler handler = null;

	class CommentViewHolder {
		ImageView headIcon;;
		TextView nickname;
		TextView commentContent;
		TextView publishTime;
		TackView audioView;
		RelativeLayout rlyTackArea;
		TextView tvTackText;
		ImageView commentLine;
	}
	
	private ArrayList<EnjoyHead> enjoyHeads = null;
	private ArrayList<DiaryDetailComment> comments = null;
	
	private Context mContext = null;
	private LayoutInflater mInflater = null;
	private String mUserID = null;
	private MyDiary myDiary = null;
	
	private boolean hide = false;
	
	public DiaryCommentsAdapter(Context context, ArrayList<EnjoyHead> enjoyHeads,
			ArrayList<DiaryDetailComment> comments, MyDiary diary)
	{
		mContext = context;
		mInflater = LayoutInflater.from(context);
		mUserID = ActiveAccount.getInstance(ZApplication.getInstance()).getUID();
		this.enjoyHeads = enjoyHeads;
		this.comments = comments;
		this.myDiary = diary;
		handler = new Handler(this);
	}
	
	public void hideComment(boolean hide)
	{
		this.hide = hide;
		this.notifyDataSetChanged();
	}
	
	@Override
	public int getCount()
	{
		if(hide)
		{
			return 0;
		}
		else
		{
			return comments.size() + 1;
		}
	}

	@Override
	public Object getItem(int position)
	{
		if(position > 0)
		{
			return comments.get(position - 1);
		}
		else
		{
			return null;
		}
	}

	@Override
	public long getItemId(int position)
	{
		if(position > 0)
		{
			return Long.parseLong(comments.get(position - 1).commentid);
		}
		else
		{
			return 0;
		}
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent)
	{
		if(position == 0)
		{
			View v = mInflater.inflate(R.layout.view_praise_list, null, true);
			DiaryDetailPraiseGroup mPraiseGroup = (DiaryDetailPraiseGroup) v.findViewById(R.id.vb_praisegroup);
			
			mPraiseGroup.removeAllViews();
			
			// 分享
			ImageLoader loader = ImageLoader.getInstance();
			
//			loader.init(ImageLoaderConfiguration.createDefault(this));
			
			DisplayImageOptions options = new DisplayImageOptions.Builder()
			.showStubImage(R.drawable.icon_head_default)
			.showImageForEmptyUri(R.drawable.icon_head_default)
			.showImageOnFail(R.drawable.icon_head_default)
			.cacheInMemory(true).cacheOnDisc(true)
			.imageScaleType(ImageScaleType.EXACTLY)
			.displayer(new SimpleBitmapDisplayer())
			// .displayer(new CircularBitmapDisplayer()) 圆形图片
			// .displayer(new RoundedBitmapDisplayer(20)) 圆角图片
			.build();
			
			AnimateFirstDisplayListener imageLoadingListener = new AnimateFirstDisplayListener();
			// 赞
			if (enjoyHeads != null)
			{
				for(int i = 0; i < enjoyHeads.size(); i++)
				{
					ImageView praise = new ImageView(mContext);
					praise.setScaleType(ScaleType.FIT_XY);
		//			loader.displayImage("http://news.xinhuanet.com/local/2013-11/27/125766428_11n.jpg", praise, ActiveAccount.getInstance(this).getUID(), 1);
					loader.displayImageEx(enjoyHeads.get(i).headimageurl, praise, options, imageLoadingListener,  mUserID, 1);
					mPraiseGroup.addView(praise);
				}
			} 
			
			if (enjoyHeads == null || enjoyHeads.size() == 0)
			{
				AbsListView.LayoutParams params = new AbsListView.LayoutParams(0,0);
				params.height = 0;
				v.setLayoutParams(params);
				v.setVisibility(View.GONE);
			}
			
			
			if(enjoyHeads != null && enjoyHeads.size() > 0)
			{
				v.setOnClickListener(new OnClickListener()
				{
					
					@Override
					public void onClick(View v)
					{
						if(enjoyHeads != null && enjoyHeads.size() >= 0)
						{
							Intent praiseintent = new Intent(mContext, PraiseListActivity.class);
							praiseintent.putExtra(DiaryDetailDefines.INTENT_DIARY_USER, myDiary.userid);
							praiseintent.putExtra(DiaryDetailDefines.INTENT_DIARY_DIARYID, myDiary.diaryid);
							praiseintent.putExtra(DiaryDetailDefines.INTENT_DIARY_PUBLISHID, myDiary.publishid);
							praiseintent.putExtra(DiaryDetailDefines.INTENT_DIARY_TYPE, "1");
							mContext.startActivity(praiseintent);
						}
					}
				});

			}
			
			return v;
		}
		else
		{
			final CommentViewHolder holder;
			final DiaryDetailComment mComment= comments.get(position-1);
			if (convertView == null || null == convertView.getTag() || !(convertView.getTag() instanceof CommentViewHolder)) {
				holder = new CommentViewHolder();
				convertView = mInflater.inflate(R.layout.vshare_detail_comment_item,
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
				holder.commentLine = (ImageView) convertView.findViewById(R.id.comment_line);
				convertView.setTag(holder);
			} else {
				holder = (CommentViewHolder) convertView.getTag();
			}
			
			if (position == 1 && (enjoyHeads == null || enjoyHeads.size() == 0)) {
				holder.commentLine.setVisibility(View.VISIBLE);
			} else {
				holder.commentLine.setVisibility(View.GONE);
			}

			ImageLoader.getInstance().displayImage(mComment.headimageurl,
					holder.headIcon, mComment.userid,
					1);
			holder.headIcon.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					try {
						if(mComment.userid!=null){
							String myuid = mUserID;
							if(mComment.userid.equals(myuid)){
								// 不处理我的icon
							}else{
								
								AccountInfo accountInfo = AccountInfo.getInstance(mUserID);
								
								if(accountInfo.friendsListName.findUserByUserid(mComment.userid) == null)
								{
									new XEditDialog.Builder(mContext)
									.setTitle(R.string.xeditdialog_title)
									.setPositiveButton(android.R.string.ok, new OnClickListener() {
										
										@Override
										public void onClick(View v) {
											//加好友
											Requester3.addFriend(handler, mComment.userid, v.getTag().toString());
										}
									})
									.setNegativeButton(android.R.string.cancel, null)
									.create().show();
								}
								
								
								Intent intent = new Intent(mContext, OtherZoneActivity.class);
								intent.putExtra(OtherZoneActivity.OTHER_ZONE_USERID, mComment.userid);
								mContext.startActivity(intent);
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
			
				
			String time = DateUtils.getMyCommonShowDate(new Date(Long
					.parseLong(mComment.createtime)));
			holder.publishTime.setText(time);
			
			return convertView;
		}
	}

	@Override
	public boolean handleMessage(Message msg)
	{
		switch (msg.what)
		{
		case Requester3.RESPONSE_TYPE_ADD_FRIEND:
			if (msg.obj != null)
			{
				addfriendResponse aresponse = (addfriendResponse) msg.obj;
				if ("0".equals(aresponse.status))
				{
					if (aresponse.target_userid == null)
					{
						break;
					}
					Prompt.Alert(mContext, "好友申请已发送");
				}
			}
			break;
		default:
			break;
		}
		return false;
	}
}
