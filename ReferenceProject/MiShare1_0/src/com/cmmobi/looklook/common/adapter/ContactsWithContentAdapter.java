package com.cmmobi.looklook.common.adapter;

import java.util.ArrayList;
import java.util.List;

import javax.crypto.spec.PSource;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import cn.zipper.framwork.core.ZApplication;

import com.cmmobi.looklook.R;
import com.cmmobi.looklook.activity.OtherZoneActivity;
import com.cmmobi.looklook.common.gson.GsonResponse3.AuxAttach;
import com.cmmobi.looklook.common.gson.GsonResponse3.Contents;
import com.cmmobi.looklook.common.utils.DensityUtil;
import com.cmmobi.looklook.common.view.ContentThumbnailView;
import com.cmmobi.looklook.common.view.FriendsExpressionView;
import com.cmmobi.looklook.common.web.TackView;
import com.cmmobi.looklook.info.profile.AccountInfo;
import com.cmmobi.looklook.info.profile.ActiveAccount;
import com.nostra13.universalimageloader.api.AnimateFirstDisplayListener;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageLoadingListener;
import com.cmmobi.looklook.common.utils.*;

public class ContactsWithContentAdapter extends BaseAdapter{

	private List<Contents> contents = new ArrayList<Contents>();
	private LayoutInflater inflater;

	private Context context;
	private int[] icons = { R.drawable.wave1, R.drawable.wave2, R.drawable.wave3, };
	private AccountInfo accountInfo;
	private String userID;
	
	//使用开源的webimageloader
	private DisplayImageOptions options;
	protected ImageLoader imageLoader;
	private ImageLoadingListener animateFirstListener;

	public ContactsWithContentAdapter(Context context) {
		this.context = context;
		this.inflater = LayoutInflater.from(context);
		animateFirstListener = new AnimateFirstDisplayListener();
		imageLoader = ImageLoader.getInstance();
		
		userID = ActiveAccount.getInstance(ZApplication.getInstance()).getUID();
		accountInfo = AccountInfo.getInstance(userID);
		
		options = new DisplayImageOptions.Builder()
		.showStubImage(R.drawable.moren_touxiang)
		.showImageForEmptyUri(R.drawable.moren_touxiang)
		.showImageOnFail(R.drawable.moren_touxiang)
		.cacheInMemory(true)
		.cacheOnDisc(true)
		.build();
		
	}

	@Override
	public int getCount() {
		return contents.size();
	}
	public void setData(List<Contents> wrapUsers) {
		if(wrapUsers == null){
			return;
		}
		this.contents.clear();
		this.contents.addAll(wrapUsers);	
	}

	@Override
	public Object getItem(int position) {
		return position;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	public void updateUI(String userid) {
		
		
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		final ViewHolder viewHolder;
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.list_item_contacts_with_content, null);
			viewHolder = new ViewHolder();
			viewHolder.contactsImageView = (ImageView) convertView
					.findViewById(R.id.contacts_image);
			viewHolder.nicknameTextView = (TextView) convertView
					.findViewById(R.id.tv_nickname);
			viewHolder.timeTextView = (TextView) convertView
					.findViewById(R.id.tv_time);
			viewHolder.commentTackView = (TackView) convertView.findViewById(R.id.comment_tackview);
			viewHolder.ctv_content =(ContentThumbnailView) convertView.findViewById(R.id.ctv_content);
			viewHolder.commentTextView = (TextView) convertView.findViewById(R.id.tv_comment_text);
			viewHolder.rlyTackArea = (RelativeLayout) convertView.findViewById(R.id.rly_tack_area);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}

		/*viewHolder.contactsImageView.setTag(contents.get(position).diaries.userid);
		viewHolder.contactsImageView.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				
				if (null != v.getTag()) {
					try {
						Intent intent = new Intent(context,
								OtherZoneActivity.class);
						intent.putExtra(OtherZoneActivity.OTHER_ZONE_USERID, v.getTag() + "");
						context.startActivity(intent);
					} catch (Exception e) {
						// TODO: handle exception
					}
					
				}
			}
		});*/
		if(contents.get(position).nickmarkname!=null && !contents.get(position).nickmarkname.isEmpty()){
			//viewHolder.nicknameTextView.setText(contents.get(position).nickmarkname);
			FriendsExpressionView.replacedExpressions(contents.get(position).nickmarkname, viewHolder.nicknameTextView);
		}else{
			//viewHolder.nicknameTextView.setText(contents.get(position).diaries.nickname);
			FriendsExpressionView.replacedExpressions(contents.get(position).diaries.nickname, viewHolder.nicknameTextView);
		}
		if(contents.get(position)!=null && contents.get(position).diaries!=null && contents.get(position).diaries.headimageurl!=null){	
			imageLoader.displayImageEx(contents.get(position).diaries.headimageurl, viewHolder.contactsImageView, options, animateFirstListener, ActiveAccount.getInstance(context).getUID(), 1);
		}else{
			viewHolder.contactsImageView.setImageResource(R.drawable.moren_touxiang);
		}
		viewHolder.timeTextView.setText(DateUtils.getMyCommonShowDate(contents.get(position).diaries.updatetimemilli));
		
		if(contents.get(position).diaries!= null){
			viewHolder.ctv_content.setVisibility(View.VISIBLE);
			viewHolder.ctv_content.setContentDiaries("0", contents.get(position).diaries);
		}else{
			viewHolder.ctv_content.setVisibility(View.GONE);
		}
		LayoutParams params = (RelativeLayout.LayoutParams) viewHolder.ctv_content.getLayoutParams();
		params.width = (int)context.getResources().getDimension(R.dimen.contacts_image);
		params.height = params.width;
		viewHolder.ctv_content.setLayoutParams(params);
		
		final AuxAttach attach = contents.get(position).diaries.getAuxAttach();
		if(attach!=null && attach.attachurl!=null && !attach.attachurl.isEmpty()){
			viewHolder.rlyTackArea.setVisibility(View.VISIBLE);
			viewHolder.commentTextView.setVisibility(View.GONE);
			
			viewHolder.commentTackView.setSoundIcons(icons);
			viewHolder.commentTackView.setPlaytime(attach.playtime);
			viewHolder.commentTackView.setRecogniPath(attach.attachurl);
			/*viewHolder.commentTackView.setOnClickListener(new OnClickListener() {
					//点击语音条，播放语音评论
					@Override
					public void onClick(View v) {
//						holder.audioView.playAudio(myCommentListItem.audiourl);
						viewHolder.commentTackView.setAudio(attach.attachurl, 1);
					}
				});*/
		}else{
			viewHolder.rlyTackArea.setVisibility(View.GONE);
			viewHolder.commentTextView.setVisibility(View.VISIBLE);
			
			try {
				if(contents.get(position).diaries.getAssistAttachTexTContent()!=null && !contents.get(position).diaries.getAssistAttachTexTContent().isEmpty()){
					FriendsExpressionView.replacedExpressions(contents.get(position).diaries.getAssistAttachTexTContent(), viewHolder.commentTextView);
				}else{
					viewHolder.commentTextView.setText("");
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return convertView;
	}

	class ViewHolder {
		ImageView contactsImageView;
		TextView nicknameTextView;
		TextView commentTextView;
		TackView commentTackView;
		TextView timeTextView;
		RelativeLayout rlyTackArea;
		ContentThumbnailView ctv_content;
	}
		
}
