package com.cmmobi.looklook.common.adapter;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.cmmobi.looklook.R;
import com.cmmobi.looklook.activity.del_AllCommentsActivity;
import com.cmmobi.looklook.common.gson.GsonResponse3.MyDiary;
import com.cmmobi.looklook.common.view.ContentThumbnailView;
import com.cmmobi.looklook.common.view.DiaryDetailCoverGroup;
import com.cmmobi.looklook.common.view.DiaryDetailPraiseGroup;
import com.cmmobi.looklook.common.view.InputUtilView;
import com.cmmobi.looklook.dialog.ShareDialog;
import com.cmmobi.looklook.fragment.ZoneBaseFragment;

public class ActivDetailAwardAdapter extends BaseAdapter{

	LayoutInflater inflater;
	ArrayList contentList;
	Context context;
	DisplayMetrics dm = new DisplayMetrics();
	InputUtilView ipuView;
	int layoutWidth;
	int margin = 3;
	
	MyDiary[] mydiary;
	

	boolean isButtonshow = false;
	boolean isShowAllText = false;
	boolean isPraise = false;

	public ActivDetailAwardAdapter(Activity actv, ArrayList list) {
		contentList = list;
		context = actv;
		inflater = LayoutInflater.from(context);
		actv.getWindowManager().getDefaultDisplay().getMetrics(dm);
		layoutWidth= (dm.widthPixels - margin*6)/3;
		ipuView = new InputUtilView(actv);
	}

	class ViewHolder {
		ImageView more;
		ImageView zan;
		ImageView zhuanfa;
		ImageView pinglun;
		TextView wenziPart;
		TextView wenziAll;
		TextView wenziButton;
		//TableLayout attachs;
		//ListView comment_content;
		TextView moreComments;
		DiaryDetailPraiseGroup zanList;
		DiaryDetailCoverGroup attachs;
	}
	
	class AttachsViewHolder{
		ArrayList<ContentThumbnailView> attachViews=new ArrayList<ContentThumbnailView>();
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return contentList.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return contentList.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		final ViewHolder holder;
		final AttachsViewHolder avHolder;
		// GridView grid_friends;
		// AttachAdapter aadapter;
		if (convertView == null) {
			holder = new ViewHolder();
			avHolder = new AttachsViewHolder();
			convertView = inflater.inflate(R.layout.my_friend_item_v31, null);

			holder.more = (ImageView) convertView.findViewById(R.id.iv_more);
			holder.pinglun = (ImageView) convertView
					.findViewById(R.id.iv_pinglun);
			holder.zan = (ImageView) convertView.findViewById(R.id.iv_zan);
			holder.zhuanfa = (ImageView) convertView.findViewById(R.id.iv_zhuanfa);
			holder.wenziPart = (TextView)convertView.findViewById(R.id.mood_describe_part);
			holder.wenziAll = (TextView)convertView.findViewById(R.id.mood_describe_all);
			holder.wenziButton = (TextView)convertView.findViewById(R.id.mood_more);
			//holder.attachs = (TableLayout)convertView.findViewById(R.id.tl_attachs);
			//holder.comment_content = (ListView)convertView.findViewById(R.id.comment_content_list);
			holder.moreComments = (TextView)convertView.findViewById(R.id.more_comments);
			holder.zanList = (DiaryDetailPraiseGroup)convertView.findViewById(R.id.ddpg_zanlist);
			holder.attachs = (DiaryDetailCoverGroup)convertView.findViewById(R.id.ddcg_attachs);
			holder.attachs.initCoverView(context, ocl);
			
			//holder.more.setOnClickListener(clickListener);
			//holder.attachs.setTag(avHolder);
			convertView.setTag(holder);
			
			/*if (mydiary != null && mydiary.length > 0) {
				for (int i = 0; i < 3; i++) {
					TableRow tr = new TableRow(context);
					for (int j = 0; j < 3; j++) {
						ContentThumbnailView view = new ContentThumbnailView(
								context);
						view.setContentDiaries("0", mydiary);
						tr.addView(view);
						LayoutParams params = view.getLayoutParams();
						params.height = layoutWidth;
						params.width = layoutWidth;
						view.setLayoutParams(params);
						view.setPadding(margin, margin, margin, margin);
						avHolder.attachViews.add(view);
					}
					holder.attachs.setVisibility(View.VISIBLE);
					holder.attachs.addView(tr);
				}
			}else{
				holder.attachs.setVisibility(View.GONE);
			}*/
		} else {
			holder = (ViewHolder) convertView.getTag();
			//avHolder = (AttachsViewHolder) holder.attachs.getTag();
		}
		addListener(holder, position);
		
		//holder.comment_content.setAdapter(new CommentAdapter());
		
		return convertView;
	}
	
	//附件点击事件
	OnClickListener ocl = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			
		}
	};

	private void addListener(final ViewHolder holder, int position) {
		
		holder.more.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if (!isButtonshow) {
					holder.pinglun.setVisibility(View.VISIBLE);
					holder.zhuanfa.setVisibility(View.VISIBLE);
					holder.zan.setVisibility(View.VISIBLE);
					isButtonshow = true;
				} else {
					holder.pinglun.setVisibility(View.INVISIBLE);
					holder.zhuanfa.setVisibility(View.INVISIBLE);
					holder.zan.setVisibility(View.INVISIBLE);
					isButtonshow = false;
				}
				Log.e("more button", "more button has been cilcked!");
			}
		});
		
		holder.zan.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// 添加赞操作点击事件请求处理
				if (!isPraise) {
					// TODO 赞操作
					//Requester2.enjoyandforward(getHandler(), diaryID, null);
				} else {
					// ZToast.showLong("已经赞过此日记！");
					//Requester2.deletepublishAndEnjoy(getHandler(), myDiary.publishid, diaryID);
				}
			}
		});
		
		holder.zhuanfa.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// 添加转发操作点击事件请求处理
				context.startActivity(new Intent(context,ShareDialog.class));
			}
		});
		
		holder.pinglun.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// 添加评论操作点击事件请求处理
				ipuView.showInput();
				holder.pinglun.setVisibility(View.VISIBLE);
				holder.zhuanfa.setVisibility(View.VISIBLE);
				holder.zan.setVisibility(View.VISIBLE);
			}
		});
		
		holder.wenziButton.setOnClickListener(new OnClickListener() {			
			@Override
			public void onClick(View v) {
				if(isShowAllText){
					holder.wenziPart.setVisibility(View.VISIBLE);
					holder.wenziAll.setVisibility(View.GONE);
					holder.wenziButton.setText(R.string.diary_all_content);
					isShowAllText = false;
				}else{
					holder.wenziPart.setVisibility(View.GONE);
					holder.wenziAll.setVisibility(View.VISIBLE);
					holder.wenziButton.setText(R.string.diary_hide_content);
					isShowAllText = true;
				}
			}
		});
		
		holder.moreComments.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.setClass(context, del_AllCommentsActivity.class);
				context.startActivity(intent);
			}
		});
	}
	
	class CommentAdapter extends BaseAdapter{

		public CommentAdapter(){
			
		}
		
		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return 3;
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder;
			if(convertView == null){
				holder = new ViewHolder();
				convertView = inflater.inflate(R.layout.comment_item, null);
				convertView.setTag(holder);
			}else{
				holder = (ViewHolder) convertView.getTag();
			}
			return convertView;
		}
		
	    class ViewHolder {
	    	
	    }
		
	}
	
}
