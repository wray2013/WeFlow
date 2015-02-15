package com.cmmobi.looklook.common.adapter;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import cn.zipper.framwork.core.ZApplication;
import cn.zipper.framwork.core.ZDialog;
import cn.zipper.framwork.core.ZLog;

import com.cmmobi.looklook.R;
import com.cmmobi.looklook.activity.del_AllCommentsActivity;
import com.cmmobi.looklook.activity.DiaryDetailDefines;
import com.cmmobi.looklook.activity.DiaryPreviewActivity;
import com.cmmobi.looklook.activity.OtherZoneActivity;
import com.cmmobi.looklook.activity.PraiseListActivity;
import com.cmmobi.looklook.common.gson.GsonResponse3;
import com.cmmobi.looklook.common.gson.GsonResponse3.CollectDiary;
import com.cmmobi.looklook.common.gson.GsonResponse3.CollectionComment;
import com.cmmobi.looklook.common.gson.GsonResponse3.DiaryInfo;
import com.cmmobi.looklook.common.gson.GsonResponse3.DiaryRelation;
import com.cmmobi.looklook.common.gson.GsonResponse3.EnjoyHead;
import com.cmmobi.looklook.common.gson.GsonResponse3.MyDiary;
import com.cmmobi.looklook.common.gson.GsonResponse3.ShareCommentList;
import com.cmmobi.looklook.common.gson.Requester3;
import com.cmmobi.looklook.common.utils.CmmobiClickAgentWrapper;
import com.cmmobi.looklook.common.utils.DateUtils;
import com.cmmobi.looklook.common.view.ContentThumbnailView;
import com.cmmobi.looklook.common.view.DiaryDetailContentView;
import com.cmmobi.looklook.common.view.DiaryDetailCoverGroup;
import com.cmmobi.looklook.common.view.DiaryDetailPraiseGroup;
import com.cmmobi.looklook.common.view.FriendsExpressionView;
import com.cmmobi.looklook.common.view.InputRecoderView;
import com.cmmobi.looklook.common.view.InputRecoderView.AudioRecoderBean;
import com.cmmobi.looklook.common.web.TackView;
import com.cmmobi.looklook.dialog.ShareDialog;
import com.cmmobi.looklook.info.profile.AccountInfo;
import com.cmmobi.looklook.info.profile.ActiveAccount;
import com.cmmobi.looklook.info.profile.DiaryManager;
import com.cmmobi.looklook.info.profile.NetworkTaskInfo;
import com.cmmobi.looklook.info.profile.TimeHelper;
import com.cmmobi.looklook.networktask.UploadNetworkTask;
import com.cmmobi.looklook.prompt.Prompt;
import com.google.gson.Gson;
import com.nostra13.universalimageloader.core.ImageLoader;

public class CollectionsContentAdapter extends BaseAdapter implements View.OnClickListener,InputRecoderView.OnSendListener{

	
	private int[] icons = { R.drawable.wave1, R.drawable.wave2, R.drawable.wave3, };
	
	
	private ArrayList<GsonResponse3.CollectDiary> contentList;
	private InputRecoderView inpRecoderView;
	private FragmentActivity actv;
	
	/** 回复评论 - 记录评论及评论组
	 ********************/
	private CollectionComment mCurrCCmment;
	private ShareCommentList mCurrCmmList;
	
	/** 直接回复日记--记住当前日记
	 ********************/
	private DiaryInfo mCurrDiaryInfo;
	
	/**  当前删除的评论
	 **************/
	private CollectionComment mCurrDelCmm;
	private ShareCommentList mCurrDelCmmList;
	
	
	/**  当前取消收藏
	 **************/
	private CollectDiary mCurrDelCdiary;
	
	private View mCurrPraiseView;
	
	private AccountInfo accountInfo;
	private String myUserid;
	
	private HashMap<Integer, ArrayList<View>> cmmViewCache;
	
	
	public CollectionsContentAdapter(FragmentActivity actv,
			ArrayList<GsonResponse3.CollectDiary> collectionList,Handler handler,InputRecoderView inpRecoderView) {
		contentList = collectionList;
		this.actv = actv;
		this.inpRecoderView = inpRecoderView;
		inpRecoderView.setOnSendListener(this);
		myUserid = ActiveAccount.getInstance(actv).getUID();
		accountInfo = AccountInfo.getInstance(myUserid);
		cmmViewCache = new HashMap<Integer, ArrayList<View>>();
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
		
		GsonResponse3.CollectDiary cdiary = contentList.get(position);
		
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = LayoutInflater.from(actv).inflate(R.layout.list_item_collections, null);

			holder.mIVHeadIcon = (ImageView) convertView.findViewById(R.id.iv_master_icon);
			holder.mTVNickName = (TextView) convertView.findViewById(R.id.tv_master_name);
			holder.mTVShareText = (TextView) convertView.findViewById(R.id.tv_share_content);
			holder.mTVMoreText = (TextView) convertView.findViewById(R.id.tv_share_content_more);
			holder.mCovergroup = (DiaryDetailCoverGroup) convertView.findViewById(R.id.vb_covergroup);
			holder.mTVShareLasttime = (TextView) convertView.findViewById(R.id.tv_share_lasttime);
			holder.mTVFavoriteText = (TextView) convertView.findViewById(R.id.tv_favorite_text);
			holder.mIVOperatePraise = (ImageButton) convertView.findViewById(R.id.ib_operate_praise);
			holder.diaryDetailContentView = (DiaryDetailContentView) convertView.findViewById(R.id.ddc_diarydetail);
			holder.mIVOperateComment = (ImageButton) convertView.findViewById(R.id.ib_operate_comment);
			holder.mIVOperateShare = (ImageButton) convertView.findViewById(R.id.ib_operate_share);
			holder.mIVOperateMore = (ImageButton) convertView.findViewById(R.id.ib_operate_more);
			holder.mPraiseGroup = (DiaryDetailPraiseGroup) convertView.findViewById(R.id.vb_praisegroup);
			holder.mllyCommentArea = (LinearLayout) convertView.findViewById(R.id.ll_comment_area);
			holder.mllyComment = (LinearLayout) convertView.findViewById(R.id.ll_comment);
			holder.mllyOperateMenu = (LinearLayout) convertView.findViewById(R.id.ll_operate_menu);
			holder.mIBShowpraise = (ImageButton) convertView.findViewById(R.id.ib_showpraise);

//			holder.mCovergroup.initCoverView(actv, ocl);

			convertView.setTag(holder);

		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		if ((cdiary != null) && (cdiary.diaryinfo != null) && (cdiary.diaryinfo.diaries != null) && cdiary.diaryinfo.diaries.length > 0) {
			MyDiary[] mydiary = cdiary.diaryinfo.diaries;
			
			holder.mIVOperateComment.setTag(cdiary.diaryinfo);
			holder.mIVOperateShare.setTag(cdiary.diaryinfo);
			holder.mTVFavoriteText.setTag(cdiary);
			
			// 转发  公开的日记才能转发
			try {
				if("3".equals(mydiary[0].publish_status)){
					holder.mIVOperateShare.setEnabled(true);
				}else{
					holder.mIVOperateShare.setEnabled(false);
				}
			} catch (Exception e) {
			}
			
			// 日记
			try {
				initDiaryCover(holder.mCovergroup, cdiary.diaryinfo);
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			// 赞列表
//			if(holder.mPraiseGroup.getChildCount()==0){
				holder.mPraiseGroup.removeAllViews();
				try {
					for(int i = 0; i < cdiary.diaryinfo.enjoyheadurl.size() ; i++) {
						ImageView praise = new ImageView(actv);
						praise.setScaleType(ScaleType.FIT_XY);
						praise.setTag(cdiary.diaryinfo.enjoyheadurl.get(i));
						praise.setOnClickListener(new OnClickListener() {
							@Override
							public void onClick(View v) {
								try {
									EnjoyHead tag = (EnjoyHead) v.getTag();
									String myuid = ActiveAccount.getInstance(actv).getUID();
									if(tag.userid.equals(myuid)){
										// 不处理我的icon
									}else{
										Intent intent = new Intent(actv, OtherZoneActivity.class);
										intent.putExtra(OtherZoneActivity.OTHER_ZONE_USERID, tag.userid);
										actv.startActivity(intent);
									}
								} catch (Exception e) {
								}
							}
						});
						ImageLoader.getInstance().displayImage(cdiary.diaryinfo.enjoyheadurl.get(i).headimageurl, praise, ActiveAccount.getInstance(actv).getUID(), 1);
						holder.mPraiseGroup.addView(praise);
					}
				} catch (Exception e) {
				}
//			}
			
			// 头像昵称
			holder.mIVHeadIcon.setTag(cdiary.diaryinfo.diaryid);
			ImageLoader.getInstance().displayImage(mydiary[0].headimageurl, holder.mIVHeadIcon, ActiveAccount.getInstance(actv).getUID(), 1);
			
			if(!TextUtils.isEmpty(cdiary.nickmarkname)){
				holder.mTVNickName.setText(cdiary.nickmarkname);
			}else{
				holder.mTVNickName.setText(mydiary[0].nickname);
			}
			
			// 分享内容
			holder.mTVShareText.setText(cdiary.diaryinfo.sharecontent);
			holder.mTVShareText.post(new Runnable() {
				@Override
				public void run() {
					int line = holder.mTVShareText.getLineCount();
					if(line>6){
						holder.mTVMoreText.setVisibility(View.VISIBLE);
					}else{
						holder.mTVMoreText.setVisibility(View.GONE);
					}
				}
			});
			
			// 分享时间
			String time = "";
			try {
				time = DateUtils.getDetailShowDate(new Date(Long
						.parseLong(cdiary.share_updatetime)));
			} catch (Exception e) {
			}
			holder.mTVShareLasttime.setText(time);
			
			// 赞按钮
			holder.mIVOperatePraise.setTag(cdiary.diaryinfo);
			boolean isPraise = hasDiaryPraise(cdiary.diaryinfo.diaryid);
			if(isPraise){
				holder.mIVOperatePraise.setBackgroundResource(R.drawable.btn_diarydetail_praise_cancel);
			}else{
				holder.mIVOperatePraise.setBackgroundResource(R.drawable.btn_diarydetail_praise);	
			}
			
			// 跳转赞列表
			holder.mIBShowpraise.setTag(cdiary.diaryinfo);
			
			// 评论
			holder.mllyComment.removeAllViews();
			
			int cacheIndex = 0;
			for(int i = 0; cdiary.diaryinfo.commentlist != null&&i < cdiary.diaryinfo.commentlist.size();i++){
				ShareCommentList cmmlist = cdiary.diaryinfo.commentlist.get(i);
				// 微薄评论  不处理
				if(!TextUtils.isEmpty(cmmlist.weiboid)){
					continue;
				}
				
				int count = 0;
				try {
					count = Integer.parseInt(cmmlist.comment_count);
//					count = cmmlist.comments.size();
				} catch (Exception e) {
				}
				
				if(count == 0 && cmmlist.comments.size() == 0){
					continue;
				}
				
				long start = System.currentTimeMillis();
				
				ArrayList<View> posCmmView = cmmViewCache.get(position);
				if(posCmmView == null ){
					posCmmView = new ArrayList<View>();
				}
				
				View cmmgroup = null;
				if(posCmmView.size() > cacheIndex){
					cmmgroup = posCmmView.get(cacheIndex);
				}else{
					cmmgroup = LayoutInflater.from(actv).inflate(R.layout.diary_comment_group, null);
					posCmmView.add(cmmgroup);
				}
				cmmViewCache.put(position, posCmmView);
				if(cmmgroup.getParent()!=null){
					((ViewGroup)cmmgroup.getParent()).removeView(cmmgroup);
				}
				holder.mllyComment.addView(cmmgroup);
				cacheIndex++;
				
				
				
				TextView cmmShareTime = (TextView) cmmgroup.findViewById(R.id.tv_comment_share_time);
				TextView cmmShareType = (TextView) cmmgroup.findViewById(R.id.tv_comment_share_type);
				
				try {
					String cmmSharetime = DateUtils.getMyCommonShowDate(new Date(Long
							.parseLong(cmmlist.share_time)));
					cmmShareTime.setText(cmmSharetime);
				} catch (Exception e) {
					e.printStackTrace();
				}
				
				// 1新浪 2人人 5 qzone空间 6腾讯 9微信朋友圈 10短信 11邮箱 12微信好友 100站内公开 101朋友圈 103微享 
				if("103".equals(cmmlist.share_status)){
					cmmShareType.setText("微信");
				}else if("101".equals(cmmlist.share_status)){
					cmmShareType.setText("朋友圈");
				}else{
					cmmShareType.setText("公开");
				}
				
				// 更多评论
				View cmmMore = cmmgroup.findViewById(R.id.lly_comment_more_area);
				if(count <= 3 && cmmlist.comments.size() <= 3){
					cmmMore.setVisibility(View.GONE);
				}else{
					cmmMore.setTag(cmmlist);
					cmmMore.setOnClickListener(new View.OnClickListener() {
						@Override
						public void onClick(View v) {
							ShareCommentList tag = (ShareCommentList) v.getTag();
							Intent intent = new Intent();
							intent.setClass(actv,del_AllCommentsActivity.class);
							intent.putExtra(del_AllCommentsActivity.REQ_PARAM_PUBLIC, tag.publishid);
							intent.putExtra(del_AllCommentsActivity.REQ_PARAM_SHARE_STATUS, tag.share_status);
							intent.putExtra(del_AllCommentsActivity.REQ_PARAM_WEIBOID, tag.weiboid);
							actv.startActivity(intent);
						}
					});
				}
				
				
				RelativeLayout one = (RelativeLayout) cmmgroup.findViewById(R.id.comment_group_one);
				RelativeLayout two = (RelativeLayout) cmmgroup.findViewById(R.id.comment_group_two);
				RelativeLayout three = (RelativeLayout) cmmgroup.findViewById(R.id.comment_group_three);
				
				View[] vs = new View[3];
				vs[0] = one;
				vs[1] = two;
				vs[2] = three;
				
				long end = System.currentTimeMillis();
				ZLog.e("getview time0 = " + (end - start));
				
				long start1 = System.currentTimeMillis();
				
				for(int j = 0; j<vs.length;j++){
					View view = vs[j];
					// 最多显示3条数据，并小于总条数
					if(j <  cmmlist.comments.size()){
						final CollectionComment ccmmitem = cmmlist.comments.get(j);
						
						if(ccmmitem == null){
							view.setVisibility(View.GONE);
							continue;
						}
						view.setOnClickListener(this);
						view.setTag(ccmmitem);
						view.setTag(view.getId(),cmmlist);
						view.setTag(view.getId()*2,cdiary.diaryinfo);
						
						ImageView iv = (ImageView) view.findViewById(R.id.iv_portrait);
						iv.setTag(ccmmitem);
						iv.setOnClickListener(new OnClickListener() {
							@Override
							public void onClick(View v) {
								try {
									
									CollectionComment tag =  (CollectionComment) v.getTag();
									String myuid = ActiveAccount.getInstance(actv).getUID();
									if(tag.userid.equals(myuid)){
										// 不处理我的icon
									}else{
										Intent intent = new Intent(actv, OtherZoneActivity.class);
										intent.putExtra(OtherZoneActivity.OTHER_ZONE_USERID, tag.userid);
										actv.startActivity(intent);
									}
								} catch (Exception e) {
								}
							}
						});
						ImageLoader.getInstance().displayImage(ccmmitem.headimageurl,iv, ActiveAccount.getInstance(actv).getUID(),1);
						
						// 优先显示备注
						TextView cmmNickName = (TextView) view.findViewById(R.id.tv_nickname);
						if(!TextUtils.isEmpty(ccmmitem.nickmarkname)){
							cmmNickName.setText(ccmmitem.nickmarkname);
						}else{
							cmmNickName.setText(ccmmitem.nickname);
						}
						
						
						RelativeLayout cmmRlyTackArea = (RelativeLayout) view.findViewById(R.id.rly_tack_area);
						final TackView cmmAudioView = (TackView) view.findViewById(R.id.comment_tackview);
						TextView cmmTvTackText = (TextView) view.findViewById(R.id.tv_tack_text);
						TextView cmmTvCmmText = (TextView) view.findViewById(R.id.tv_comment_text);
						TextView cmmTvTime = (TextView) view.findViewById(R.id.tv_comment_time);
						
						
						if (!ccmmitem.commentway.equals("1")){  // 语音 ，语音+文字
							cmmRlyTackArea.setVisibility(View.VISIBLE);
							cmmAudioView.setVisibility(View.VISIBLE);
							cmmTvCmmText.setVisibility(View.GONE);
							cmmAudioView.setSoundIcons(icons);
							cmmAudioView.setPlaytime(ccmmitem.playtime);
							if (ccmmitem.audiourl != null) {
								cmmAudioView.setOnClickListener(new OnClickListener() {
									//点击语音条，播放语音评论
									@Override
									public void onClick(View v) {
										if(!TextUtils.isEmpty(ccmmitem.audiourl)){
//											cmmAudioView.playAudio(ccmmitem.audiourl);
											cmmAudioView.setAudio(ccmmitem.audiourl, 1);
										}
									}
								});
							}
							
							if(!TextUtils.isEmpty(ccmmitem.commentcontent)){
								cmmTvTackText.setVisibility(View.VISIBLE);
//								cmmTvTackText.setText(ccmmitem.commentcontent.trim());
								FriendsExpressionView.replacedExpressions(ccmmitem.commentcontent.trim(), cmmTvTackText);
							}else{
								cmmTvTackText.setVisibility(View.GONE);
							}
							
						}else{
							cmmRlyTackArea.setVisibility(View.GONE);
							cmmTvCmmText.setVisibility(View.VISIBLE);
							
							try {
								if ("2".equals(ccmmitem.commenttype)) {
									String name = "";
									if(!TextUtils.isEmpty(ccmmitem.replymarkname)){
										name = ccmmitem.replymarkname;
									}else{
										name = ccmmitem.replynickname;
									}
									FriendsExpressionView.replacedExpressions("回复<font color=\"black\">"+name+"</font>: " + ccmmitem.commentcontent.trim(), cmmTvCmmText);
								}else{
									FriendsExpressionView.replacedExpressions(ccmmitem.commentcontent.trim(), cmmTvCmmText);
								}
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
						
							
						String cmmtime = DateUtils.getMyCommonShowDate(new Date(Long
								.parseLong(ccmmitem.createtime)));
						cmmTvTime.setText(cmmtime);
						
					}else{
						view.setVisibility(View.GONE);
					}
				}
				
				long end1 = System.currentTimeMillis();
				ZLog.e("getview time1 = " + (end1 - start1));
			}
			
		}
		addListener(holder, position);

		return convertView;
	}

	


	private void addListener(final ViewHolder holder, int position) {

		holder.mIVHeadIcon.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				try {
					
					DiaryRelation diaryid =  (DiaryRelation) v.getTag();
					String myuid = ActiveAccount.getInstance(actv).getUID();
					if(diaryid.userid.equals(myuid)){
						// 不处理我的icon
					}else{
						Intent intent = new Intent(actv, OtherZoneActivity.class);
						intent.putExtra(OtherZoneActivity.OTHER_ZONE_USERID, diaryid.userid);
						actv.startActivity(intent);
					}
				} catch (Exception e) {
				}
			}
		});;
		
		
		
		// 收藏 - 取消收藏
		holder.mTVFavoriteText.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				CollectDiary cdiary = ( CollectDiary) v.getTag();
				mCurrDelCdiary = cdiary;
				
				StringBuilder sb = new StringBuilder();
				try {
					for(ShareCommentList cmmlist: cdiary.diaryinfo.commentlist){
						String pid = cmmlist.publishid;
						if(TextUtils.isEmpty(pid)){
							continue;
						}
						if(!sb.toString().contains(pid)){
							sb.append(pid+",");
						}
					}
					if(sb.length()>0){
						sb.deleteCharAt(sb.length()-1);
					}
					ZDialog.show(R.layout.progressdialog, true, true, actv);
					Requester3.removeCollectDiary(handler, cdiary.diaryinfo.diaryid.diaryid, sb.toString());
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		
		
		// 赞操作
		holder.mIVOperatePraise.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// 添加赞操作点击事件请求处理
				mCurrPraiseView = v;
				v.setClickable(false);
				DiaryInfo dinfo = (DiaryInfo) v.getTag();
				boolean isPraise = hasDiaryPraise(dinfo.diaryid);
				// 添加赞操作点击事件请求处理
				String publishid = findXFirstPublishid(dinfo.commentlist);
				if (!isPraise) {
					Requester3.enjoy(handler, dinfo.diaryid.diaryid, publishid);
					
					/**统计***/
					HashMap<String, String> ids= new HashMap<String, String>();
					ids.put("label1", dinfo.diaryid.diaryid);
					ids.put("label2", publishid);
					CmmobiClickAgentWrapper.onEvent(v.getContext(), "content_thrumb_up", ids);
					
				} else {
					Requester3.deleteEnjoy(handler, dinfo.diaryid.diaryid, publishid);
					
					/**统计***/
					HashMap<String, String> ids= new HashMap<String, String>();
					ids.put("label1", dinfo.diaryid.diaryid);
					ids.put("label2", publishid);
					CmmobiClickAgentWrapper.onEvent(v.getContext(), "cancel_thrumb_up", ids);
				}
				
				
				
			}
		});
		
		// 赞操作
		holder.mIBShowpraise.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				DiaryInfo diaryinfo = (DiaryInfo) v.getTag();
				Intent intentPraise = new Intent(actv, PraiseListActivity.class);
				intentPraise.putExtra(DiaryDetailDefines.INTENT_DIARY_DIARYID, diaryinfo.diaryid.diaryid);
				intentPraise.putExtra(DiaryDetailDefines.INTENT_DIARY_PUBLISHID, findXFirstPublishid(diaryinfo.commentlist));
				intentPraise.putExtra(DiaryDetailDefines.INTENT_DIARY_TYPE, "2");
				actv.startActivity(intentPraise);
			}
		});

		// 评论
		holder.mIVOperateComment.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// 添加评论操作点击事件请求处理
				mCurrCCmment = null;
				mCurrCmmList = null;
				if(!inpRecoderView.isShown()){
					mCurrDiaryInfo = (DiaryInfo) v.getTag();
					inpRecoderView.setVisibility(View.VISIBLE);
				}else{
					inpRecoderView.setVisibility(View.GONE);
				}
			}
		});

		// 分享 转发
		holder.mIVOperateShare.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// 添加转发操作点击事件请求处理
				try {
					DiaryInfo diaryinfo = (DiaryInfo) v.getTag();
					
					GsonResponse3.MyDiaryList myDiaryList = new GsonResponse3.MyDiaryList();
					myDiaryList.isgroup = diaryinfo.diaryid.isgroup;
					myDiaryList.contain = diaryinfo.diaryid.contain;
					myDiaryList.diaryid = diaryinfo.diaryid.diaryid;
					myDiaryList.diaryuuid = diaryinfo.diaryid.diaryuuid;
					myDiaryList.publishid = findXFirstPublishid(diaryinfo.commentlist);
					
					
					
					/**统计***/
					HashMap<String, String> ids= new HashMap<String, String>();
					ids.put("label1", myDiaryList.diaryid);
					ids.put("label2", myDiaryList.publishid);
					CmmobiClickAgentWrapper.onEvent(v.getContext(), "content_retweet", ids);
					
					String diaryInfoId = diaryinfo.diaryid.diaryid;
					final ArrayList<MyDiary> diaryArrList = new ArrayList<GsonResponse3.MyDiary>();
					
					for (int i = 0; i < diaryinfo.diaries.length; i++) {
						if(diaryinfo.diaries.length != 1){
							if(diaryinfo.diaries[i].diaryid.equals(diaryInfoId) ){
								continue;
							}
						}
						diaryArrList.add(diaryinfo.diaries[i]);
					}
					
					Intent intent = new Intent(actv, ShareDialog.class);
					intent.putExtra(DiaryPreviewActivity.INTENT_ACTION_DIARY_STRING, new Gson().toJson(diaryArrList));
					intent.putExtra(DiaryPreviewActivity.INTENT_ACTION_DIARYLIST_STRING, new Gson().toJson(myDiaryList));
					actv.startActivity(intent);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		
		// 文字收起或显示全部
		holder.mTVMoreText.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(holder.mTVShareText.getMaxLines() == 6){
					holder.mTVShareText.setMaxLines(100);
					holder.mTVMoreText.setText(R.string.diary_hide_content);
				}else{
					holder.mTVShareText.setMaxLines(6);
					holder.mTVMoreText.setText(R.string.diary_all_content);
				}
			}
		});
		
		// 操作 more
		holder.mIVOperateMore.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (!holder.mllyOperateMenu.isShown()) {
					holder.mllyOperateMenu.setVisibility(View.VISIBLE);
				} else {
					holder.mllyOperateMenu.setVisibility(View.INVISIBLE);
				}
			}
		});
	}


	class ViewHolder {
		ImageView mIVHeadIcon;   // 头像
		TextView mTVNickName;    // 昵称
		TextView mTVShareText;  // 分享的文字
		TextView mTVMoreText;     // 全文
		DiaryDetailCoverGroup mCovergroup; // 日记封面
		TextView mTVShareLasttime;     // 分享时间
		TextView mTVFavoriteText;      // 收藏文字
		ImageView mIVOperatePraise;    // 赞操作
		DiaryDetailContentView diaryDetailContentView;    // 赞操作
		ImageView mIVOperateComment;   // 评论操作
		ImageView mIVOperateShare;     // 分享操作
		ImageView mIVOperateMore;      // 弹出操作区域按钮
		LinearLayout mllyOperateMenu;   // 赞评论等操作区域
		DiaryDetailPraiseGroup mPraiseGroup;  // 赞列表控件
		ImageButton mIBShowpraise;         // 赞按钮
		LinearLayout mllyComment;           // 评论区域
		LinearLayout mllyCommentArea;           // 评论区域包含更多
//		LinearLayout mllyCommentMoreArea;  // 更多评论容器
//		TextView mBtnCommentMore;           // 更多评论
	}


	Handler handler = new Handler(){
		public void dispatchMessage(android.os.Message msg) {
			switch (msg.what) {
			
			// 删除评论
			case Requester3.RESPONSE_TYPE_DELETE_COMMENT:
				
				ZDialog.dismiss();
				
				GsonResponse3.deleteCommentResponse delCmmResp = (GsonResponse3.deleteCommentResponse)msg.obj;
				
				if(delCmmResp!=null){
					if("0".equals(delCmmResp.status)){
						Prompt.Alert("删除评论成功");
						try {
							mCurrDelCmmList.comments.remove(mCurrDelCmm);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}else{
						Prompt.Alert("服务器返回错误，错误码：" + delCmmResp.status);
					}
				}
				
				mCurrDelCmm = null;
				mCurrDelCmmList = null;
				
				CollectionsContentAdapter.this.notifyDataSetChanged();
				
				break;
				
				// 评论接口
			case Requester3.RESPONSE_TYPE_COMMENT:
				
				ZDialog.dismiss();
				
				GsonResponse3.commentResponse cmmresp = (GsonResponse3.commentResponse)msg.obj;
				if(cmmresp!=null){
					if("0".equals(cmmresp.status)){
						
						Prompt.Alert("评论成功");
						
						if(mCurrSendBean != null ){
							
							String userID = ActiveAccount.getInstance(ZApplication.getInstance()).getUID();
							String diaryID = "";
							String publishid = "";
							String commenttype = "";
							String replynickname = "";
							String replymarkname = "";
							
							// 回复
							if(mCurrCCmment!=null&& mCurrDiaryInfo==null){
								diaryID = mCurrCCmment.diaryid;
								commenttype = "2";
								replynickname = mCurrCCmment.nickname;
								replymarkname = mCurrCCmment.nickmarkname;
								publishid = mCurrCCmment.publishid;
							}else // 直接评论 
							if(mCurrCCmment==null&& mCurrDiaryInfo!=null){
								diaryID = mCurrDiaryInfo.diaryid.diaryid;
								commenttype = "1";
								publishid = mCurrDiaryInfo.commentlist.get(0).publishid;
							}
							
							// 语音加文字
							if("2".equals(mCurrSendBean.commenttype) ||  
							    "3".equals(mCurrSendBean.commenttype) ){
								// 上传语音
								try {
									//MyDiary.relpaceMediaMapping(mCurrSendBean.localFilePath, cmmresp.audiopath/*.replace("_", "/")*/);
									
									NetworkTaskInfo tt = new NetworkTaskInfo(userID, diaryID, cmmresp.commentid, 
											Environment.getExternalStorageDirectory() + mCurrSendBean.localFilePath,
											cmmresp.audiopath, "3", "2");
									
									UploadNetworkTask uploadtask = new UploadNetworkTask(tt);// 创建上传/下载任务
									uploadtask.start();
									
								} catch (Exception e) {
									e.printStackTrace();
								}
							}
							
							// 保存评论
							String userid = ActiveAccount.getInstance(actv).getLookLookID();
							AccountInfo accinfo = AccountInfo.getInstance(userid);
							
							CollectionComment addCmm = new CollectionComment();
							
							addCmm.userid       = userid;   //评论用户ID
							addCmm.headimageurl = accinfo.headimageurl;  //头像URL
							addCmm.nickname     = accinfo.nickname;   //昵称
							addCmm.sex          = accinfo.sex;        // 0男，1女， 2未知 
							
							addCmm.commentid   = cmmresp.commentid;   //评论ID，当前评论id
							addCmm.commentuuid = cmmresp.commentuuid;  //评论UUID，当前评论uuid
							addCmm.createtime  = TimeHelper.getInstance().now() + "";  // 时间戳
							
							addCmm.publishid = publishid;   //日记ID
							addCmm.diaryid = diaryID;   //日记ID
							addCmm.commentcontent = mCurrSendBean.content;  //评论
							addCmm.audiourl = mCurrSendBean.localFilePath;     // 语音地址 
							addCmm.playtime = mCurrSendBean.playtime;    //语音播放时长
							addCmm.commentway = mCurrSendBean.commenttype;  // 评论方式1、文字 2、声音3、声音加文字
							addCmm.commenttype = commenttype;  //评论类型：1、评论 2回复
							addCmm.replynickname = replynickname;  //被回复人昵称
							addCmm.replymarkname = replymarkname;  //被回复人昵称
							
							// 回复
							if(mCurrCCmment != null && mCurrCmmList != null){
								
								mCurrCmmList.comments.add(0, addCmm);
								
							}else if(mCurrDiaryInfo!=null){ // 直接评论
								
								for(ShareCommentList item : mCurrDiaryInfo.commentlist){
									// 加到最后分享除第三方
									if("100".equals(item.share_status)||
											"101".equals(item.share_status)||
											"103".equals(item.share_status)){
										item.comments.add(0, addCmm);
										break;
									}
								}
							}
							
							CollectionsContentAdapter.this.notifyDataSetChanged();
							
							cleanCmmData(true);
						}
						
					}else{
						Prompt.Alert("服务器返回错误，错误码：" + cmmresp.status);
					}
				}
				
				
				break;
				
				// 取消收藏
			case Requester3.RESPONSE_TYPE_REMOVE_COLLECT_DIARY:
				
				ZDialog.dismiss();
				
				GsonResponse3.removeCollectDiaryResponse removeCDiary = (GsonResponse3.removeCollectDiaryResponse)msg.obj;
				if(removeCDiary == null){
					return;
				}
				
				if("0".equals(removeCDiary.status)){
					Prompt.Alert("取消收藏成功");
					
					try {
						if(mCurrDelCdiary!=null){
							try {
								contentList.remove(mCurrDelCdiary);
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
						DiaryManager.getInstance().removeCollectDiaryID(mCurrDelCdiary.diaryinfo.diaryid.diaryid);
						
					} catch (Exception e) {
					}
					
					CollectionsContentAdapter.this.notifyDataSetChanged();
				}else{
					Prompt.Alert("取消收藏失败，错误码：" + removeCDiary.status);
				}
				break;
				
				// 赞动作
			case Requester3.RESPONSE_TYPE_DIARY_ENJOY:
				
				mCurrPraiseView.setClickable(true);
				DiaryInfo dinfo = (DiaryInfo) mCurrPraiseView.getTag();
				
				GsonResponse3.enjoyResponse response = (GsonResponse3.enjoyResponse) msg.obj;
				if(response!=null){
					if(response.status!=null && "0".equals(response.status)){
						try {
							
							mCurrPraiseView.setBackgroundResource(R.drawable.btn_diarydetail_praise_cancel);
							Prompt.Alert("己赞！");
							
							DiaryManager.getInstance().addPraiseDiaryID(dinfo.diaryid.diaryid,"");
							EnjoyHead newEnjoy = new EnjoyHead();
							newEnjoy.headimageurl = accountInfo.headimageurl;
							newEnjoy.userid = myUserid;
							
							dinfo.enjoyheadurl.add(0, newEnjoy);
						} catch (Exception e) {
						}
						CollectionsContentAdapter.this.notifyDataSetChanged();
					}else{
						Prompt.Alert("赞失败！");
					}
				}else{
					Prompt.Alert("赞失败！");
				}
				break;
				
				// 取消赞
			case Requester3.RESPONSE_TYPE_DELETE_ENJOY_DIARY:
				mCurrPraiseView.setClickable(true);
				DiaryInfo delDiaryInfo = (DiaryInfo) mCurrPraiseView.getTag();
				
				GsonResponse3.deleteEnjoyResponse deleteEnjoy= (GsonResponse3.deleteEnjoyResponse) msg.obj;
				try {
					
					if(deleteEnjoy != null && "0".equals(deleteEnjoy.status)){
						try {
							
							mCurrPraiseView.setBackgroundResource(R.drawable.btn_diarydetail_praise);
							Prompt.Alert("己取消赞！");
							DiaryManager.getInstance().removePraiseDiaryID(delDiaryInfo.diaryid.diaryid, "");
							EnjoyHead myejoy = null;
							for(EnjoyHead ej : delDiaryInfo.enjoyheadurl){
								if(myUserid.equals(ej.userid)){
									myejoy = ej;
								}
							}
							if(myejoy!=null){
								delDiaryInfo.enjoyheadurl.remove(myejoy);
							}
						} catch (Exception e) {
						}
						CollectionsContentAdapter.this.notifyDataSetChanged();
					}else{
						Prompt.Alert("取消赞失败！");
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
				break;
			default:
				break;
			}
		};
	};
	
	private DialogFragment dialogFragment;
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.comment_group_one:   // 评论item 
		case R.id.comment_group_two:
		case R.id.comment_group_three:
//		case R.id.rl_recent_comment:
			CollectionComment item = (CollectionComment) v.getTag();
			ShareCommentList cmmlist = (ShareCommentList) v.getTag(v.getId());
			DiaryInfo diaryInfo = (DiaryInfo) v.getTag(v.getId()*2);
			
			String myUserid = ActiveAccount.getInstance(actv).getLookLookID();
			boolean toReply = true;
			// 自己评论的
			if(!TextUtils.isEmpty(myUserid)&&myUserid.equals(item.userid)){
				toReply = false;
			}
			// 可以回复，在判断是否能删除
			boolean replyShowDel = true;
			if(toReply){
				try {
					if(!TextUtils.isEmpty(myUserid) && !myUserid.equals(diaryInfo.diaries[0].userid)){
						replyShowDel = false;
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			dialogFragment = CommentDialogFragment.newInstance(this,item,cmmlist,toReply,replyShowDel);
			dialogFragment.show(actv.getSupportFragmentManager(), "dialog");
			
			break;
		case R.id.btn_comment_delete:  // dialog 评论删除
			
			if(dialogFragment!=null ){
				dialogFragment.dismiss();
			}
			
			mCurrDelCmm = (CollectionComment) v.getTag();
			mCurrDelCmmList = (ShareCommentList) v.getTag(v.getId());
			
			ZDialog.show(R.layout.progressdialog, true, true, actv);
			Requester3.deleteComment(handler,mCurrDelCmm.publishid, mCurrDelCmm.commentid,mCurrDelCmm.commentuuid);
			
			break;
		case R.id.btn_comment_reply: // dialog 评论回复
			
			if(dialogFragment!=null ){
				dialogFragment.dismiss();
			}
			
//			Requester3.comment(handler,mDelItem.publishid, mDelItem.commentid,mDelItem.commentuuid);
			
			// 清除直接回复
			mCurrDiaryInfo = null;
			mCurrCCmment = (CollectionComment) v.getTag();
			mCurrCmmList = (ShareCommentList) v.getTag(v.getId());
			inpRecoderView.setVisibility(View.VISIBLE);
			
			break;
			
		default:
			break;
		}
	}
	
	private AudioRecoderBean mCurrSendBean;
	
	/**
	 * 语音控件 : 发送按钮回调
	 */
	@Override
	public void onSend(AudioRecoderBean bean) {
		
		mCurrSendBean = bean;
		
		// 直接回复
		if(mCurrCCmment == null && mCurrDiaryInfo != null){
			ZDialog.show(R.layout.progressdialog, true, true, actv);
			String publishid = "";
			String diaryid = "";
			try {
				// 获取最新分享，除第三方
				for(ShareCommentList item : mCurrDiaryInfo.commentlist){
					if("100".equals(item.share_status)||
							"101".equals(item.share_status)||
							"103".equals(item.share_status)){
//						publishid = mCurrDiaryInfo.commentlist.get(0).publishid;
						publishid = item.publishid;
						diaryid = mCurrDiaryInfo.diaryid.diaryid;
						
						break;
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			/**统计***/
			HashMap<String, String> ids= new HashMap<String, String>();
			ids.put("label", publishid);
			ids.put("label2", bean.commenttype);
			CmmobiClickAgentWrapper.onEvent(actv, "con_com_success", ids);
			
			Requester3.comment(handler, bean.content, "" , "1" ,publishid,bean.commenttype,bean.commentuuid, diaryid, "1");
		}else  // 评论回复
		if(mCurrCCmment != null && mCurrDiaryInfo == null){
			ZDialog.show(R.layout.progressdialog, true, true, actv);
			Requester3.comment(handler, bean.content,mCurrCCmment.commentid,"2",mCurrCCmment.publishid,bean.commenttype,bean.commentuuid, mCurrCCmment.diaryid, "1");
			
			/**统计***/
			HashMap<String, String> ids= new HashMap<String, String>();
			ids.put("label", mCurrCCmment.commentid);
			ids.put("label2", bean.commenttype);
			CmmobiClickAgentWrapper.onEvent(actv, "content_reply", ids);
			
		}else{
			ZLog.e("send error。。。。");
		}
		
		
		
		
	}
	
	
	
	
	public static class CommentDialogFragment extends DialogFragment {

		private static CommentDialogFragment dialog;
		private OnClickListener listener;
		private ShareCommentList cmmlist;
		private CollectionComment ccmm;
		private boolean hasReply;
		private boolean replyShowDel;
		
		public static CommentDialogFragment newInstance(OnClickListener listener,CollectionComment ccmm,ShareCommentList cmmlist,boolean hasReply,boolean replyShowDel){
			if(dialog == null){
				dialog = new CommentDialogFragment();
			}
			dialog.listener = listener;
			dialog.hasReply = hasReply;
			dialog.ccmm = ccmm;
			dialog.cmmlist = cmmlist;
			dialog.replyShowDel = replyShowDel;
			return dialog;
		}
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
        	
        	final Dialog d = new Dialog(getActivity(), R.style.dialog_theme); 
        	
        	LayoutInflater inflater = LayoutInflater.from(getActivity());
        	
        	View v = null;
        	Button delete;
        	Button cancel;
        	Button reply;
        	
        	if(!hasReply){
        		v = inflater.inflate(R.layout.dialogfragment_comment_operate_two, null);
        		delete = (Button) v.findViewById(R.id.btn_comment_delete);
        		delete.setTag(ccmm);
        		delete.setTag(delete.getId(),cmmlist);
        		cancel = (Button) v.findViewById(R.id.btn_comment_cancel);
        		delete.setOnClickListener(listener);
        		cancel.setOnClickListener(new OnClickListener(){
					@Override
					public void onClick(View v){
						d.dismiss();
					}
				});
        	} else {
        		v = inflater.inflate(R.layout.dialogfragment_comment_operate_three, null);
        		reply = (Button) v.findViewById(R.id.btn_comment_reply);
        		reply.setTag(ccmm);
        		reply.setTag(reply.getId(),cmmlist);
        		reply.setOnClickListener(listener);
        		
        		delete = (Button) v.findViewById(R.id.btn_comment_delete);
        		delete.setTag(ccmm);
        		delete.setTag(delete.getId(),cmmlist);
        		delete.setOnClickListener(listener);
        		
        		if(!replyShowDel){
        			delete.setVisibility(View.GONE);
        			reply.setBackgroundResource(R.drawable.btn_menu_one);
        		}
        		
        		cancel = (Button) v.findViewById(R.id.btn_comment_cancel);
        		cancel.setOnClickListener(new OnClickListener(){
					@Override
					public void onClick(View v){
						d.dismiss();
					}
				});
        	}        	
        	
        	LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT); 
        	d.addContentView(v, params);
        	
    		Window window = d.getWindow();
    		window.setGravity(Gravity.BOTTOM);
    		android.view.WindowManager.LayoutParams p = window.getAttributes();
			p.width = LayoutParams.MATCH_PARENT; // 设置屏幕宽度
//			p.alpha = 0.9f; // 增加一点按钮透明
			window.setAttributes(p);      //设置生效
    		return d;
        }
    }


	/**
	 * 隐藏输入框时清楚缓存
	 * 
	 */
	public void cleanCmmData(boolean clearView){
		mCurrCCmment = null;
		mCurrCmmList = null;
		mCurrDiaryInfo = null;
		if(clearView){
			inpRecoderView.clearView();
		}
	}
	
	
	/**
	 * 查下日记是否已赞
	 * @param diaryrelation
	 * @return
	 */
	private boolean hasDiaryPraise(DiaryRelation diaryrelation) {
		try {
			if(DiaryManager.getInstance().isPraise(diaryrelation.diaryid, "")){
				return true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
	
	
	/**
	 * 查找公开或微享的publishid
	 * @param commentlist
	 * @return
	 */
	private String findXFirstPublishid(ArrayList<ShareCommentList> commentlist){
		if(commentlist==null|| commentlist.size() ==0 ){
			return "";
		}
		for(ShareCommentList item : commentlist){
			if("100".equals(item.share_status)||
					"101".equals(item.share_status)||
					"103".equals(item.share_status)){
				return item.publishid;
			}
		}
		return "";
	}
	
	
	/**
	 * 初始化日记封面ui
	 * @param holder
	 * @param mydiary
	 */
	private void initDiaryCover(DiaryDetailCoverGroup mCovergroup, DiaryInfo diaryInfo) throws RuntimeException{
		mCovergroup.removeAllViews();
		MyDiary[] diaries = diaryInfo.diaries;
		
		if(diaries.length == 1){
			
			ContentThumbnailView view = new ContentThumbnailView(actv);
			view.setTag(diaries[0]);
			view.setContentDiaries(diaries[0].join_safebox, diaries[0]);
			mCovergroup.addView(view);
			view.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					// 跳转预览页
					try {
						MyDiary mydiaries = (MyDiary) v.getTag();
						Intent vsharedetail = new Intent(actv, DiaryPreviewActivity.class);
						ArrayList<MyDiary> diarys = new ArrayList<GsonResponse3.MyDiary>();
						diarys.add(mydiaries);
						vsharedetail.putExtra(DiaryPreviewActivity.INTENT_ACTION_DIARY_UUID, mydiaries.diaryuuid);
						DiaryManager.getInstance().setmMyDiaryBuf(diarys);
						vsharedetail.putExtra(DiaryPreviewActivity.INTENT_ACTION_SHOW_MODE, DiaryPreviewActivity.SHOW_MODE_SIMPLE);
						actv.startActivity(vsharedetail);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});
			
		}else{
			
			String diaryInfoId = diaryInfo.diaryid.diaryid;
			
			final ArrayList<MyDiary> diaryList = new ArrayList<GsonResponse3.MyDiary>();
			
			for (int i = 0; i < diaries.length; i++) {
				
				if(diaries[i].diaryid.equals(diaryInfoId) ){
					continue;
				}
				diaryList.add(diaries[i]);
				
				ContentThumbnailView view = new ContentThumbnailView(actv);
				view.setTag(diaries[i].diaryuuid);
				view.setContentDiaries(diaries[i].join_safebox, diaries[i]);
				mCovergroup.addView(view);
				view.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						// 跳转预览页
						try {
							String diaryuuid = (String) v.getTag();
							Intent vsharedetail = new Intent(actv, DiaryPreviewActivity.class);
							vsharedetail.putExtra(DiaryPreviewActivity.INTENT_ACTION_DIARY_UUID, diaryuuid);
							DiaryManager.getInstance().setmMyDiaryBuf(diaryList);
							vsharedetail.putExtra(DiaryPreviewActivity.INTENT_ACTION_SHOW_MODE, DiaryPreviewActivity.SHOW_MODE_SIMPLE);
							actv.startActivity(vsharedetail);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				});
			}
		}
		
		mCovergroup.requestLayout();
	}
	
	
}
