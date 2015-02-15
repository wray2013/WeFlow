package com.cmmobi.looklook.common.view;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cmmobi.looklook.R;
import com.cmmobi.looklook.common.gson.GsonResponse2.taglistItem;

/**
 * @author wuxiang
 * @email wuxiang@cmmobi.com
 * @date  2013-6-13
 */
public class TagsView extends LinearLayout {

	private static final String TAG="TagsView";
	
	public TagsView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	public TagsView(Context context) {
		super(context);
		init(context);
	}
	
	private GridView gv;
	private ImageView ivClose;
	private GridViewAdapter gridViewAdapter;
	private void init(Context context){
		LayoutInflater inflater=LayoutInflater.from(context);
		View v=inflater.inflate(R.layout.del_include_tags, null);
		setGravity(Gravity.CENTER_HORIZONTAL);
		int padding=context.getResources().getDimensionPixelSize(R.dimen.size_10dip);
		setPadding(padding, padding, padding, padding);
		addView(v, new LayoutParams(LayoutParams.FILL_PARENT,LayoutParams.FILL_PARENT));
		ivClose=(ImageView) v.findViewById(R.id.iv_close);
		gv=(GridView) v.findViewById(R.id.gv_tags);
		gridViewAdapter=new GridViewAdapter(context);
		gv.setAdapter(gridViewAdapter);
		gv.setSelector(new ColorDrawable(Color.TRANSPARENT));
		
	}
	
	private ArrayList<taglistItem> tags=new ArrayList<taglistItem>();
	private ArrayList<String> checkedTags=new ArrayList<String>();
	public void loadTags(DiaryManager_del diaryManager,boolean isIncludeSaveBox){
		checkedTags=diaryManager.getCheckedTags(isIncludeSaveBox);
		this.tags=diaryManager.getTags();
		gridViewAdapter.notifyDataSetChanged();
	}
	
	public void setCheckedTag(String tag){
		if(gridViewAdapter!=null)
			gridViewAdapter.setCheckedTag(tag);
	}
	
	public String getCheckedTag(){
		return gridViewAdapter.getCheckedTag();
	}
	
	public void setOnItemClickListener(OnItemClickListener onItemClickListener){
		if(gv!=null)
			gv.setOnItemClickListener(onItemClickListener);
	}
	
	public void setCloseListener(OnClickListener onClickListener){
		if(ivClose!=null)
			ivClose.setOnClickListener(onClickListener);
	}
	
	public boolean isValid(taglistItem tag){
		if(checkedTags.contains(tag.name))
			return true;
		return false;
	}
	
	public ArrayList<String> getTags(){
		ArrayList<String> tagList=new ArrayList<String>();
		if(tags!=null&&tags.size()>0){
			for(int i=0;i<tags.size();i++){
				tagList.add(tags.get(i).name);
			}
		}
		return tagList;
	}
	
	class GridViewAdapter extends BaseAdapter{
		
		private Context context;
		private String checkedTag;
		public GridViewAdapter(Context context){
			this.context=context;
		}
		
		public void setCheckedTag(String tag){
			this.checkedTag=tag;
			notifyDataSetChanged();
		}
		
		public String getCheckedTag(){
			return checkedTag;
		}
		
		@Override
		public int getCount() {
			return tags.size();
		}

		@Override
		public Object getItem(int position) {
			return tags.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			taglistItem tag = tags.get(position);
			TextView tv=new TextView(context);
			tv.setBackgroundResource(R.drawable.del_biaoqian_normal);
			tv.setGravity(Gravity.CENTER);
			tv.setLayoutParams(new AbsListView.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
			tv.setText(tag.name);
			if(checkedTags.contains(tag.name))
				tv.setBackgroundResource(R.drawable.del_biaoqian_2);
			return tv;
		}
		
	}
	
}
