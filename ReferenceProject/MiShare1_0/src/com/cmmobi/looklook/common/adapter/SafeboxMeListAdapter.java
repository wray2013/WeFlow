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
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.cmmobi.looklook.R;
import com.cmmobi.looklook.activity.DiaryPreviewActivity;
import com.cmmobi.looklook.common.gson.GsonResponse3.MyDiary;
import com.cmmobi.looklook.common.gson.GsonResponse3.MyDiaryList;
import com.cmmobi.looklook.common.view.ContentThumbnailView;
import com.cmmobi.looklook.fragment.MyZoneFragment.DiariesItem;
import com.cmmobi.looklook.fragment.MyZoneFragment.MyZoneItem;
import com.cmmobi.looklook.fragment.SafeboxContentFragment;
import com.cmmobi.looklook.info.profile.DiaryManager;

/**
 * @author wuxiang
 * @email wuxiang@cmmobi.com
 * @date  2013-11-1
 */
public class SafeboxMeListAdapter extends SafeboxSubAdapter implements OnLongClickListener,OnClickListener {

	private static final String TAG = SafeboxMeListAdapter.class.getSimpleName();
	private Context context;
	private SafeboxContentFragment safeboxFragment;
	ArrayList<MyZoneItem> myZoneItems=new ArrayList<MyZoneItem>();
	private LayoutInflater inflater;
	protected DisplayMetrics dm = new DisplayMetrics();
	private int layoutWidth;
	private int margin=5;
	public SafeboxMeListAdapter(SafeboxContentFragment safeboxFragment,ArrayList<MyZoneItem> myZoneItems) {
		this.safeboxFragment=safeboxFragment;
		this.context = safeboxFragment.getActivity();
		inflater = LayoutInflater.from(context);
		this.myZoneItems=myZoneItems;
		((Activity) context).getWindowManager().getDefaultDisplay()
		.getMetrics(dm);
		layoutWidth=(dm.widthPixels-margin*6)/3;
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
	
	/**
	 * 清楚选中状态
	 */
	public void purgeCheckedView(){
		checkedList.clear();
		notifyDataSetChanged();
	}
	
	/**
	 * 获取选中列表
	 */
	public ArrayList<MyDiaryList> getCheckedList(){
		return checkedList;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
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
					view.setOnLongClickListener(this);
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
				contentThumbnailView.setContentDiaries(myDiaryGroups.get(count).getFilterShareInfo(),"0", diaries.get(count));
				contentThumbnailView.setTag(myDiaryGroups.get(count));
				if(checkedList.contains(myDiaryGroups.get(count))){//设置选中状态
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
						view.setOnLongClickListener(this);
						view.setOnClickListener(this);
						view.setContentDiaries(myDiaryGroups.get(count).getFilterShareInfo(),"0", diaries.get(count));
						view.setTag(myDiaryGroups.get(count));
						if(checkedList.contains(myDiaryGroups.get(count))){//设置选中状态
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
						view.setOnLongClickListener(this);
						view.setOnClickListener(this);
						view.setContentDiaries(myDiaryGroups.get(count).getFilterShareInfo(),"0", diaries.get(count));
						view.setTag(myDiaryGroups.get(count));
						if(checkedList.contains(myDiaryGroups.get(count))){//设置选中状态
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
//						holder.tvDate.setText(diariesItem.strDate);
						holder.tvDate.setText(diariesItem.textStyle);
					}else{
						holder.tvDate.setVisibility(View.GONE);
					}
				}
			}else{
				holder.tvDate.setVisibility(View.VISIBLE);
//				holder.tvDate.setText(diariesItem.strDate);
				holder.tvDate.setText(diariesItem.textStyle);
			}
		}
		return convertView;
	}

	private ArrayList<MyDiaryList> checkedList=new ArrayList<MyDiaryList>(); 
	@Override
	public boolean onLongClick(View v) {
		if(v instanceof ContentThumbnailView){
			//1.判断v是否选中
			//2.未选中时，设置选中，同时记录选中数据
			if(!((ContentThumbnailView) v).getViewSelected()){
				((ContentThumbnailView) v).setViewSelected(true);
				if(v.getTag()!=null&&v.getTag() instanceof MyDiaryList)
					checkedList.add((MyDiaryList)v.getTag());
			}
			if(!safeboxFragment.isCheckedTitleShow())
				safeboxFragment.showCheckedTitle();
		}
		return true;
	}

	@Override
	public void onClick(View v) {
		if(v instanceof ContentThumbnailView){
			if(((ContentThumbnailView) v).getViewSelected()){
				((ContentThumbnailView) v).setViewSelected(false);
				checkedList.remove(v.getTag());
				if(0==checkedList.size())safeboxFragment.showNormalTitle();
			}else{
				if(checkedList.size()>0){//选中删除模式
					((ContentThumbnailView) v).setViewSelected(true);
					checkedList.add((MyDiaryList)v.getTag());
				}else{//未选中状态，单击跳转到详情
					Log.d(TAG, "onClick to detail");
					if(v.getTag()!=null){
						MyDiaryList diaryGroup=(MyDiaryList) v.getTag();
						String groupUUID=diaryGroup.diaryuuid;
						ArrayList<MyDiaryList> diaryGroups=(ArrayList<MyDiaryList>) getDiaryGroup().clone();
						DiaryManager.getInstance().setDetailDiaryList(diaryGroups, 1);
						
						Intent intent = new Intent(context, DiaryPreviewActivity.class);
						intent.putExtra(DiaryPreviewActivity.INTENT_ACTION_DIARY_UUID, groupUUID);
						context.startActivity(intent);
					}
				}
			}
		}
	}
	
	private ArrayList<MyDiaryList> getDiaryGroup(){
		ArrayList<MyDiaryList> diaryGroups=new ArrayList<MyDiaryList>();
		if(myZoneItems!=null){
			for(int i=0;i<myZoneItems.size();i++){
				MyZoneItem item=myZoneItems.get(i);
				if(item instanceof DiariesItem){
					diaryGroups.addAll(((DiariesItem) item).diaryGroups);
				}
			}
		}
		return diaryGroups;
		
	}

	static class ViewHolder {
		TextView tvDate;
		TableLayout tlDiaries;
	}
	
	static class ThumbnailViewHolder{
		ArrayList<ContentThumbnailView> thumbnailViews=new ArrayList<ContentThumbnailView>();
	}

}
