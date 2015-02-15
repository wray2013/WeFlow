package com.cmmobi.looklook.common.adapter;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.cmmobi.looklook.R;
import com.cmmobi.looklook.common.gson.GsonResponse3.MyDiary;
import com.cmmobi.looklook.common.gson.GsonResponse3.MyDiaryList;
import com.cmmobi.looklook.common.view.ContentThumbnailView;
import com.cmmobi.looklook.common.view.Xdialog;
import com.cmmobi.looklook.fragment.MyZoneFragment.DiariesItem;
import com.cmmobi.looklook.fragment.MyZoneFragment.MyZoneItem;

/**
 * Copyright (C) ......
 * All rights reserved.
 * @package com.cmmobi.looklook.activity
 * @filename ChoiceDiaryListActivity.java
 * @summary 分享界面日记列表，参考VShareDiaryListAdapter类
 * @author Lanhai
 * @date 2013-12-5
 * @version 1.0
 */
public class ShareDiarySelectAdapter extends BaseAdapter implements OnClickListener {

	private static final String TAG = ShareDiarySelectAdapter.class.getSimpleName();
	private Context context;
	private ArrayList<MyZoneItem> myZoneItems;
	private LayoutInflater inflater;
	protected DisplayMetrics dm = new DisplayMetrics();
	private int layoutWidth;
	private int margin=5;
	private ArrayList<ContentThumbnailView> ctvList = new ArrayList<ContentThumbnailView>();
	private ItemClick itemClick = null;
	
	public ShareDiarySelectAdapter(Context context,ArrayList<MyZoneItem> list) {
		this.context = context;
		inflater = LayoutInflater.from(context);
		this.myZoneItems = list;
		((Activity) context).getWindowManager().getDefaultDisplay()
		.getMetrics(dm);
		layoutWidth=(dm.widthPixels-margin*6)/3;
	}
	
	public void setSelected(ArrayList<MyDiary> diarys)
	{
		// 改为只计数
		selected = diarys.size();
	}

	@Override
	public int getCount() {
		return myZoneItems.size();
	}

	@Override
	public Object getItem(int position) {
		return myZoneItems.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}
	
	public void setItemClick(ItemClick itemClick) {
		this.itemClick = itemClick;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// 每天日记布局
		ViewHolder holder = null;
		ThumbnailViewHolder thumbnailViewHolder=null;
		if (convertView == null||null==convertView.getTag()) {
			convertView = inflater.inflate(
					R.layout.include_my_zone_diary_item, null);
			holder = new ViewHolder();
			thumbnailViewHolder=new ThumbnailViewHolder();
			holder.tvDate=(TextView) convertView.findViewById(R.id.tv_date);
			holder.tlDiaries=(TableLayout) convertView.findViewById(R.id.tl_diary_items);
			convertView.setTag(holder);
			holder.tlDiaries.setTag(thumbnailViewHolder);
			for(int i=0;i<1;i++){
				TableRow tr=new TableRow(context);
				for(int j=0;j<3;j++){
					ContentThumbnailView view=new ContentThumbnailView(context);
					tr.addView(view);
					LayoutParams params=view.getLayoutParams();
					params.height=layoutWidth;
					params.width=layoutWidth;
					view.setLayoutParams(params);
					view.setPadding(margin, margin, margin, margin);
					view.setOnClickListener(this);
					thumbnailViewHolder.thumbnailViews.add(view);
				}
				holder.tlDiaries.addView(tr);
			}
		} else {
			holder = (ViewHolder) convertView.getTag();
			thumbnailViewHolder=(ThumbnailViewHolder) holder.tlDiaries.getTag();
		}
		
		MyZoneItem myZoneItem=myZoneItems.get(position);
		if(myZoneItem instanceof DiariesItem){
			DiariesItem diariesItem=(DiariesItem) myZoneItem;
			ArrayList<MyDiaryList> myDiaryGroups=diariesItem.diaryGroups;
			ArrayList<MyDiary[]> diaries=diariesItem.diaries;
			int itemSize=diaries.size();
			//当可重用个数大于当前需要显示的元素时，删除多余的
			int length=thumbnailViewHolder.thumbnailViews.size();
			for(int i=itemSize;i<length;i++){
				ContentThumbnailView contentThumbnailView=thumbnailViewHolder.thumbnailViews.get(i);
				contentThumbnailView.setVisibility(View.GONE);
			}
			
			int totalRows=itemSize%3==0?itemSize/3:itemSize/3+1;
			int count=0;
			int trCount=holder.tlDiaries.getChildCount();
			for(int i=0;i<(itemSize>length?length:itemSize);i++){
				ContentThumbnailView contentThumbnailView=thumbnailViewHolder.thumbnailViews.get(i);
				contentThumbnailView.setVisibility(View.VISIBLE);
				contentThumbnailView.setContentDiaries("0", diaries.get(count));
				ctvList.add(contentThumbnailView);
				Position pos = new Position();
				pos.p = ctvList.size()-1;
				contentThumbnailView.setTag(R.id.tag_first, pos);
				contentThumbnailView.setTag(R.id.tag_second, myDiaryGroups.get(count));
				contentThumbnailView.setTag(R.id.tag_third, diaries.get(count)[0]);
				if(selectList.contains(diaries.get(count)[0]) && myDiaryGroups.get(count).isgroup.equals("0")){//设置选中状态
					contentThumbnailView.setViewSelected(true);
				}else{
					contentThumbnailView.setViewSelected(false);
				}
				count++;
			}
			//现有行中新增元素
			for(int i=0;i<trCount;i++){
				for(int j=0;j<3;j++){
					int itemCount=thumbnailViewHolder.thumbnailViews.size();
					if(i*3+(j+1)>itemCount&&itemCount<itemSize){//新增元素
						ContentThumbnailView view=new ContentThumbnailView(context);
						TableRow tr= (TableRow) holder.tlDiaries.getChildAt(i);
						tr.addView(view);
						LayoutParams params=view.getLayoutParams();
						params.height=layoutWidth;
						params.width=layoutWidth;
						view.setLayoutParams(params);
						view.setPadding(margin, margin, margin, margin);
						view.setOnClickListener(this);
						view.setContentDiaries("0", diaries.get(count));
						ctvList.add(view);
						Position pos = new Position();
						pos.p = ctvList.size()-1;
						view.setTag(R.id.tag_first, pos);
						view.setTag(R.id.tag_second, myDiaryGroups.get(count));
						view.setTag(R.id.tag_third, diaries.get(count)[0]);
						if(selectList.contains(diaries.get(count)[0]) && myDiaryGroups.get(count).isgroup.equals("0")){//设置选中状态
							view.setViewSelected(true);
						}else{
							view.setViewSelected(false);
						}
						thumbnailViewHolder.thumbnailViews.add(view);
						count++;
					}
				}
			}
			//新增行中增加新元素
			for(int i=0;i<totalRows-trCount;i++){
				TableRow tr=new TableRow(context);
				for(int j=0;j<3;j++){
					if(thumbnailViewHolder.thumbnailViews.size()<itemSize){
						ContentThumbnailView view=new ContentThumbnailView(context);
						tr.addView(view);
						LayoutParams params=view.getLayoutParams();
						params.height=layoutWidth;
						params.width=layoutWidth;
						view.setLayoutParams(params);
						view.setPadding(margin, margin, margin, margin);
						view.setOnClickListener(this);
						view.setContentDiaries("0", diaries.get(count));
						ctvList.add(view);
						Position pos = new Position();
						pos.p = ctvList.size()-1;
						view.setTag(R.id.tag_first, pos);
						view.setTag(R.id.tag_second, myDiaryGroups.get(count));
						view.setTag(R.id.tag_third, diaries.get(count)[0]);
						if(selectList.contains(diaries.get(count)[0]) && myDiaryGroups.get(count).isgroup.equals("0")){//设置选中状态
							view.setViewSelected(true);
						}else{
							view.setViewSelected(false);
						}
						count++;
						thumbnailViewHolder.thumbnailViews.add(view);
					}
				}
				holder.tlDiaries.addView(tr);
			}
			if(position>0){
				MyZoneItem last=myZoneItems.get(position-1);
				if(last instanceof DiariesItem){
					if(!((DiariesItem)last).strDate.equals(diariesItem.strDate)){
						holder.tvDate.setVisibility(View.VISIBLE);
						holder.tvDate.setText(diariesItem.strDate);
					}else{
						holder.tvDate.setVisibility(View.GONE);
					}
				}
			}else{
				holder.tvDate.setVisibility(View.VISIBLE);
				holder.tvDate.setText(diariesItem.strDate);
			}
			
		}
		return convertView;
	}

//	private ArrayList<Object> checkedList=new ArrayList<Object>(); 
	Position p;
	private ArrayList<MyDiary> selectList= new ArrayList<MyDiary>();
	private int selected = 0;

	@Override
	public void onClick(View v) {
		if(v instanceof ContentThumbnailView){
			//1.判断v是否选中
			//2.未选中时，设置选中，同时记录选中数据
			if(!((ContentThumbnailView) v).getViewSelected()){
				// 组不让选择
				MyDiaryList diaryList = (MyDiaryList) v.getTag(R.id.tag_second);
				if(diaryList.isgroup.equals("1"))
				{
					new Xdialog.Builder(context)
					.setMessage(context.getResources().getString(R.string.share_error_isgroup))
					.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
						}
					})
					.create().show();
					return;
				}
				else if(selectList.size() + selected >= 9)
				{
					new Xdialog.Builder(context)
					.setMessage(context.getResources().getString(R.string.share_error_max))
					.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
						}
					})
					.create().show();
					return;
				}
				((ContentThumbnailView) v).setViewSelected(true);
				p = (Position)v.getTag(R.id.tag_first);
//				checkedList.add(v.getTag(R.id.tag_second));
				selectList.add((MyDiary)v.getTag(R.id.tag_third));
			}else{
				((ContentThumbnailView) v).setViewSelected(false);
//				checkedList.remove(v.getTag(R.id.tag_second));
				selectList.remove((MyDiary)v.getTag(R.id.tag_third));
				p = null;
			}
			if (itemClick != null) {
				itemClick.itemClick();
			}
		}
		
	}

	public ArrayList<MyDiary> getCheckedDiary(){
		
//		return checkedList;
		return selectList;
		
	}
	
	static class ViewHolder {
		TextView tvDate;
		TableLayout tlDiaries;
	}
	
	static class ThumbnailViewHolder{
		ArrayList<ContentThumbnailView> thumbnailViews=new ArrayList<ContentThumbnailView>();
	}

	class Position{
		int p;
	}
	
	public interface ItemClick {
		public void itemClick();
	}
}
