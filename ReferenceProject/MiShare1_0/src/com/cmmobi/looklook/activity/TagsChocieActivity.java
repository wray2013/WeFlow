package com.cmmobi.looklook.activity;

import java.util.ArrayList;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

import com.cmmobi.looklook.R;
import com.cmmobi.looklook.common.gson.GsonResponse3.taglistItem;
import com.cmmobi.looklook.common.utils.CmmobiClickAgentWrapper;
import com.cmmobi.looklook.fragment.MyZoneFragment;
import com.cmmobi.looklook.info.profile.DiaryManager;

/**
 * @author wuxiang
 * @email wuxiang@cmmobi.com
 * @date  2013-12-2
 */
public class TagsChocieActivity extends TitleRootActivity implements OnItemClickListener {
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setTitle(R.string.str_tagschoice);
		setRightButtonText(R.string.str_tagschoice_save);
		getRightButton().setOnClickListener(this);
		init();
		loadTags();
	}
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_title_right:
			//2014-4-8 wuxiang
			String tagids=getStrCheckedTagids();
			Log.d("Tags", tagids);
			if(TextUtils.isEmpty(tagids)){
				CmmobiClickAgentWrapper.onEvent(this, "hashtag", "0");
			}
			/*else{
				CmmobiClickAgentWrapper.onEvent(this, "hashtag",tagids);
			}*/
			LocalBroadcastManager.getInstance(this).sendBroadcast(new Intent(MyZoneFragment.ACTION_ADD_TAGS).putExtra("tagids", getCheckedTagids()));
			finish();
			break;

		default:
			break;
		}
		super.onClick(v);
	}
	
	@Override
	protected void onResume() {
		//2014-4-8 wuxiang
		CmmobiClickAgentWrapper.onResume(this);
		super.onResume();
	}

	@Override
	protected void onPause() {
		//2014-4-8 wuxiang
		CmmobiClickAgentWrapper.onPause(this);
		super.onPause();
	}

	@Override
	protected void onStop() {
		//2014-4-8 wuxiang
		CmmobiClickAgentWrapper.onStop(this);
		super.onStop();
	}



	private GridView gv;
	private GridViewAdapter gridViewAdapter;
	private void init(){
		gv=(GridView) findViewById(R.id.gv_tags);
		gridViewAdapter=new GridViewAdapter(this);
		gv.setAdapter(gridViewAdapter);
		gv.setSelector(new ColorDrawable(Color.TRANSPARENT));
		gv.setOnItemClickListener(this);
		
	}

	@Override
	public int subContentViewId() {
		return R.layout.activity_tags_choice;
	}
	
	private ArrayList<taglistItem> tags=new ArrayList<taglistItem>();
	private ArrayList<String> checkedTags=new ArrayList<String>();
	public void loadTags(){
		tags.clear();
		tags.addAll(DiaryManager.getInstance().getTags());
		gridViewAdapter.notifyDataSetChanged();
	}
	
	@Override
	public void onItemClick(AdapterView<?> arg0, View view, int position, long arg3) {
		if(checkedTags.contains(view.getTag())){//已选中，清除选中状态
			checkedTags.remove(view.getTag());
		}else{//未选中，设置选中
			if(checkedTags.size()>=3)return;//最多选中3个
			checkedTags.add(view.getTag().toString());
		}
		gridViewAdapter.notifyDataSetChanged();
	}

	/**
	 * 返回标签id数组
	 */
	private String[] getCheckedTagids(){
		return checkedTags.toArray(new String[checkedTags.size()]);
	}
	
	private String getStrCheckedTagids(){
		String res="";
		for(int i=0;i<checkedTags.size();i++){
			res+=checkedTags.get(i);
			CmmobiClickAgentWrapper.onEvent(this, "hashtag",checkedTags.get(i));
			if(i!=checkedTags.size()-1){
				res+=",";
			}
		}
		return res;
	}
	
	class GridViewAdapter extends BaseAdapter{
		
		private Context context;
		public GridViewAdapter(Context context){
			this.context=context;
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
			tv.setBackgroundResource(R.drawable.bianji_biaoqian_1);
			tv.setGravity(Gravity.CENTER);
			tv.setLayoutParams(new AbsListView.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
			tv.setText(tag.name);
			tv.setTag(tag.id);
			tv.setTextColor(Color.parseColor("#7D7D7D"));
			if(checkedTags.contains(tag.id)){
				tv.setTextColor(Color.WHITE);
				tv.setBackgroundResource(R.drawable.bianji_biaoqian_2);
			}
			return tv;
		}
		
	}

}
