package com.cmmobi.looklook.common.adapter;

import java.util.ArrayList;
import java.util.Date;

import android.app.Activity;
import android.content.Intent;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cmmobi.looklook.R;
import com.cmmobi.looklook.activity.DiaryPreviewActivity;
import com.cmmobi.looklook.activity.LookLookActivity;
import com.cmmobi.looklook.activity.OtherZoneActivity;
import com.cmmobi.looklook.common.gson.GsonResponse3;
import com.cmmobi.looklook.common.gson.Requester3;
import com.cmmobi.looklook.common.gson.GsonResponse3.MyCommentListItem;
import com.cmmobi.looklook.common.gson.GsonResponse3.MyDiary;
import com.cmmobi.looklook.common.gson.GsonResponse3.MyDiaryList;
import com.cmmobi.looklook.common.gson.GsonResponse3.addfriendResponse;
import com.cmmobi.looklook.common.utils.DateUtils;
import com.cmmobi.looklook.common.view.ContentThumbnailView;
import com.cmmobi.looklook.common.view.FriendsExpressionView;
import com.cmmobi.looklook.common.view.InputUtilView;
import com.cmmobi.looklook.common.view.XEditDialog;
import com.cmmobi.looklook.common.web.TackView;
import com.cmmobi.looklook.info.profile.AccountInfo;
import com.cmmobi.looklook.info.profile.ActiveAccount;
import com.cmmobi.looklook.info.profile.ContactManager;
import com.cmmobi.looklook.info.profile.DiaryManager;
import com.cmmobi.looklook.info.profile.WrapUser;
import com.cmmobi.looklook.prompt.Prompt;
import com.nostra13.universalimageloader.core.ImageLoader;

public class CommentsContentAdapter extends BaseAdapter implements Callback{

	LayoutInflater inflater;
	ArrayList<MyCommentListItem> contentList;
	Activity activity;
	InputUtilView ipuView;
    String commentType;
    
	private int[] icons = { R.drawable.wave1, R.drawable.wave2, R.drawable.wave3, };
	
	private Handler handler;
	
	public CommentsContentAdapter(Activity activity,
			ArrayList<MyCommentListItem> list,String commentType) {
		this.contentList = list;
		this.activity = activity;
		this.commentType = commentType;
		inflater = LayoutInflater.from(activity);
		ipuView = new InputUtilView(activity);
		handler = new Handler(this);
	}

	class ViewHolder {
		ImageView headIcon;;
		TextView nickname;
		TextView commentContent;
		TextView publishTime;
		TackView audioView;
		RelativeLayout rlyTackArea;
		TextView tvTackText;
		ContentThumbnailView ctv;
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

	private void addListener(final ViewHolder holder) {

	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		Log.e("getView", "getView has beel called!");
		final ViewHolder holder;
		final MyCommentListItem myCommentListItem = contentList.get(position);
		if (convertView == null || null == convertView.getTag()) {
			holder = new ViewHolder();
			convertView = inflater.inflate(R.layout.accept_send_comment_item,
					null);
			holder.ctv = (ContentThumbnailView) convertView
					.findViewById(R.id.ctv_target_pic);
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

		ImageLoader.getInstance().displayImage(myCommentListItem.headimageurl,
				holder.headIcon, ActiveAccount.getInstance(activity).getUID(),
				1);
		holder.headIcon.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				try {
					if(myCommentListItem.userid!=null){
						String myuid = ActiveAccount.getInstance(activity).getUID();
						if(myCommentListItem.userid.equals(myuid)){
							// 不处理我的icon
						}else{
							
							AccountInfo accinfo = AccountInfo.getInstance(myuid);
							ContactManager friendsListContactManager=accinfo.friendsListName;
							WrapUser currUserInfo=friendsListContactManager.findUserByUserid(myCommentListItem.userid);
							
							if(currUserInfo == null){
								// 不在好友列表中
								new XEditDialog.Builder(activity)
								.setTitle(R.string.xeditdialog_title)
								.setPositiveButton(R.string.send, new OnClickListener() {
									@Override
									public void onClick(View v) {
										//加好友
										Requester3.addFriend(handler, myCommentListItem.userid, v.getTag().toString());
									}
								})
								.setNegativeButton(android.R.string.cancel, null)
								.create().show();
								
							}else{
								// 是好友，并跳转
								Intent intent = new Intent(activity, OtherZoneActivity.class);
								intent.putExtra(OtherZoneActivity.OTHER_ZONE_USERID, myCommentListItem.userid);
								activity.startActivity(intent);
							}
							
							
						}
					}
				} catch (Exception e) {
				}
			}
		});
		
		
		if (commentType.equals("2")) {// 我发出的评论不显示自己的昵称
			holder.nickname.setText("");
		} else {
			
			String name = "";
			// 优先显示备注
			if(!TextUtils.isEmpty(myCommentListItem.nickmarkname)){
				name = myCommentListItem.nickmarkname;
			}else{
				name = myCommentListItem.nickname;
			}
			
			// 回复
			if("2".equals(myCommentListItem.commenttype)){
				String replyName = "";
				if(!TextUtils.isEmpty(myCommentListItem.replymarkname)){
					replyName = myCommentListItem.replymarkname;
				}else{
					replyName = myCommentListItem.replynickname;
				}
				
				//holder.nickname.setText(Html.fromHtml(name + "<font color=\"black\">回复</font>" +  replyName));
				FriendsExpressionView.replacedExpressionsReply(name + "回复" +  replyName, holder.nickname, name.length(), name.length()+2);
			}else{
				//holder.nickname.setText(name);
				FriendsExpressionView.replacedExpressions(name, holder.nickname);
			}
		}
		
		if (!myCommentListItem.commentway.equals("1")){  // 语音 ，语音+文字
			holder.rlyTackArea.setVisibility(View.VISIBLE);
			holder.commentContent.setVisibility(View.GONE);
			
			holder.audioView.setSoundIcons(icons);
			holder.audioView.setPlaytime(myCommentListItem.playtime);
			if (myCommentListItem.audiourl != null) {
				holder.audioView.setRecogniPath(myCommentListItem.audiourl);
				holder.audioView.setOnClickListener(new OnClickListener() {
					//点击语音条，播放语音评论
					@Override
					public void onClick(View v) {
//						holder.audioView.playAudio(myCommentListItem.audiourl);
						holder.audioView.setAudio(myCommentListItem.audiourl, 1);
					}
				});
			}
			
			if(!TextUtils.isEmpty(myCommentListItem.commentcontent)){
				holder.tvTackText.setVisibility(View.VISIBLE);
				FriendsExpressionView.replacedExpressions(myCommentListItem.commentcontent.trim(), holder.tvTackText);
			}else{
				holder.tvTackText.setVisibility(View.GONE);
			}
			
		}else{
			holder.rlyTackArea.setVisibility(View.GONE);
			holder.commentContent.setVisibility(View.VISIBLE);
			
			try {
				/*if ("2".equals(myCommentListItem.commenttype)) {
					String name = "";
					if(!TextUtils.isEmpty(myCommentListItem.replymarkname)){
						name = myCommentListItem.replymarkname;
					}else{
						name = myCommentListItem.replynickname;
					}
					FriendsExpressionView.replacedExpressions("回复<font color=\"black\">"+name+"</font>: " + myCommentListItem.commentcontent.trim(), holder.commentContent);
				}else{
				}*/
				FriendsExpressionView.replacedExpressions(myCommentListItem.commentcontent.trim(), holder.commentContent);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
			
		String time = DateUtils.getMyCommonListShowDate(new Date(Long
				.parseLong(myCommentListItem.createtime)));
		holder.publishTime.setText(time);
		
		// 日记
		String isInSafeBox = "0";
		/*try {
			isInSafeBox = myCommentListItem.diaries[0].join_safebox;
		} catch (Exception e) {
			e.printStackTrace();
		}*/
		
		// 日记预览图
		if(myCommentListItem.diaries == null || myCommentListItem.diaries.length == 0){
			holder.ctv.setVisibility(View.INVISIBLE);
		}else{
			holder.ctv.setVisibility(View.VISIBLE);
		}
		holder.ctv.setContentDiaries(isInSafeBox, myCommentListItem.diaries);
		/*holder.ctv.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// 跳转预览页
				try {
					Intent vsharedetail = new Intent(activity, DiaryPreviewActivity.class);
					
					MyDiaryList DiaryListInfo = myCommentListItem.diaryids[0];
					
					MyDiary[] diaries = myCommentListItem.diaries;
					ArrayList<MyDiary> diarys = new ArrayList<GsonResponse3.MyDiary>();
					
					for(int i=0; i< diaries.length; i++){
						if(diaries.length != 1){
							if(diaries[i].diaryid.equals(DiaryListInfo.diaryid)){
								continue;
							}
						}
						if(TextUtils.isEmpty(vsharedetail.getStringExtra(DiaryPreviewActivity.INTENT_ACTION_DIARY_UUID))){
							vsharedetail.putExtra(DiaryPreviewActivity.INTENT_ACTION_DIARY_UUID, diaries[i].diaryuuid);
						}
						diarys.add(diaries[i]);
					}
					DiaryManager.getInstance().setmMyDiaryBuf(diarys);
					vsharedetail.putExtra(DiaryPreviewActivity.INTENT_ACTION_SHOW_MODE, DiaryPreviewActivity.SHOW_MODE_SIMPLE);
					activity.startActivity(vsharedetail);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});*/

		addListener(holder);
		return convertView;
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

}
