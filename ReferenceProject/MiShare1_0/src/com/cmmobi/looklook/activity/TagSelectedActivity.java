package com.cmmobi.looklook.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import cn.zipper.framwork.core.ZActivity;

import com.cmmobi.looklook.R;
import com.cmmobi.looklook.common.adapter.TagAdapter;
import com.cmmobi.looklook.common.gson.GsonResponse3.TAG;
import com.cmmobi.looklook.common.gson.GsonResponse3.taglistItem;
import com.cmmobi.looklook.common.utils.CmmobiClickAgentWrapper;
import com.cmmobi.looklook.info.profile.DiaryManager;

public class TagSelectedActivity extends ZActivity {

	private final String TAG = "TagSelectedActivity";
	
	private List<taglistItem> tagList;
	private List<Integer> checkedList = new ArrayList<Integer>(3);
	private int tagNum = 0;
	private GridView tagGrid = null;
	private TextView tvSave;
	private ImageView ivBack;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_tag_selected);
		
		tagGrid = (GridView) findViewById(R.id.gv_edit_media_tag);
		tagNum = 0;
		TagAdapter adapter = new TagAdapter(this, initTag(),this);
		tagGrid.setAdapter(adapter);
		
		tvSave = (TextView) findViewById(R.id.tv_edit_tag_save);
		tvSave.setOnClickListener(this);
		
		ivBack = (ImageView) findViewById(R.id.iv_edit_media_tag_back);
		ivBack.setOnClickListener(this);
	}
	
	private void initTagList(List<taglistItem> tagStrList) {
		String [] tags = getResources().getStringArray(R.array.activity_edit_media_tags);
		int tagSize = tags.length;
		for (int i = 0;i < tagSize;i++) {
			taglistItem tagItem = new taglistItem();
    		tagItem.id = String.valueOf(i + 1);
    		tagItem.name = tags[i];
    		tagItem.checked = "0";
    		tagStrList.add(tagItem);
		}
	}
	
	private List<taglistItem> initTag() {
    	tagList = new ArrayList<taglistItem>();
		DiaryManager diarymanager = DiaryManager.getInstance();
		List<taglistItem> tagStrList = diarymanager.getTags();
		
		if (tagStrList == null || tagStrList.size() == 0) {
			tagStrList = new ArrayList<taglistItem>();
			initTagList(tagStrList);
		}
    	int size = tagStrList.size();
    	
    	TAG[] tags = null;
    	ArrayList<String> tagIdsList = getIntent().getStringArrayListExtra("tagids");
    	tags = getDiaryTag(tagIdsList);
    	/*if (myDiary != null) {
    		tags = myDiary.tags;
    	}*/
    	int tagSize = 0;
    	if (tags != null) {
    	   tagSize = tags.length;
    	}
    	
    	for (int i = 0;i < size; i++) {
    		taglistItem tagItem = new taglistItem();
    		tagItem.id = tagStrList.get(i).id;
    		tagItem.name = tagStrList.get(i).name;
    		tagItem.checked = "0";
    		Log.d(TAG,"i = " + i + " id = " + tagStrList.get(i).id + " name = " + tagStrList.get(i).name + " checked = " + tagStrList.get(i).checked);
    		for (int j = 0;j < tagSize;j++) {
    			if (tags[j].id.equals(tagStrList.get(i).id)) {
    				if (!checkedList.contains(i)) {
    					checkedList.add(i);
    				}
    			}
    		}
    		tagList.add(tagItem);
    	}
    	
    	Log.d(TAG,"initTag checkedList " + checkedList);
    	int checkedSize = checkedList.size();
    	for (int i = 0; i < checkedSize; i++) {
    	    int position = checkedList.get(i);
    	    if (position >= 0 && position < size) {
    	    	tagList.get(position).checked = String.valueOf("1");
    	    }
    	}
    	tagNum = checkedList.size();
    	
    	return tagList;
    }
	
	public static TAG[] getDiaryTag(List<String> tagIdsList) {
		if (tagIdsList == null || tagIdsList.size() == 0) {
			return null;
		}
		DiaryManager diarymanager = DiaryManager.getInstance();
		List<taglistItem> tagStrList = diarymanager.getTags();
		ArrayList<TAG> tagList = new ArrayList<TAG>();
		for (String id:tagIdsList) {
			for (taglistItem item:tagStrList) {
				if (id.equals(item.id)) {
					TAG tag = new TAG();
					tag.id = item.id;
					tag.name = item.name;
					tagList.add(tag);
				}
			}
		}
		if (tagList.size() == 0) {
			return null;
		}
		
		return tagList.toArray(new TAG[tagList.size()]);
	}
	
	@Override
	public boolean handleMessage(Message arg0) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void onClick(View view) {
		// TODO Auto-generated method stub
		switch (view.getId()) {
		case R.id.ll_edit_media_tag_item:
			Log.d(TAG,"onClick ll_edit_media_tag_item in");
			TagAdapter.ViewHolder holder = (TagAdapter.ViewHolder)(view.getTag());
			int position = holder.position;
			Log.d(TAG,"******in position = " + position + " checkedList = " + checkedList);
			boolean isChecked = tagList.get(position).checked.equals(String.valueOf("1"));
			if (isChecked) {
				holder.tv.setTextColor(Color.parseColor("#7D7D7D"));
				view.setBackgroundResource(R.drawable.bianji_biaoqian_normal);
				tagList.get(position).checked = String.valueOf("0");
				tagNum--;
				int checkedPos = checkedList.indexOf(position);
				if (checkedPos >= 0) {
					Log.d(TAG,"onClick remove checkedPos = " + checkedPos + " position = " + position);
					checkedList.remove(checkedPos);
				}
			} else {
				if (tagNum >= 3) {
					return;
				}
				holder.tv.setTextColor(Color.WHITE);
				view.setBackgroundResource(R.drawable.bianji_biaoqian_selected);
				tagList.get(position).checked = String.valueOf("1");
				tagNum++;
				if (checkedList.size() < 3) {
					checkedList.add(position);
					Log.d(TAG,"onClick add position = " + position);
				}
			}
			break;
		case R.id.tv_edit_tag_save:
			Intent intent = new Intent();
			ArrayList<String> tagStrList = new ArrayList<String>();
			for (int checkedIndex : checkedList) {
				tagStrList.add(tagList.get(checkedIndex).name);
			}
			intent.putStringArrayListExtra("tagstrings", tagStrList);
			
			ArrayList<String> tagIdList = new ArrayList<String>();
			for (int checkedIndex : checkedList) {
				tagIdList.add(tagList.get(checkedIndex).id);
				HashMap<String,String> map = new HashMap<String, String>();
				map.put("label", tagList.get(checkedIndex).id);
				map.put("label2", DiaryEditPreviewActivity.isFromShootting ? "2" : "1");
				// 2014-4-8
				CmmobiClickAgentWrapper.onEvent(this, "edit_hashtag", map);
			}
			if (tagIdList.size() == 0) {
				HashMap<String,String> map = new HashMap<String, String>();
				map.put("label", "0");
				map.put("label2", DiaryEditPreviewActivity.isFromShootting ? "2" : "1");
				// 2014-4-8
				CmmobiClickAgentWrapper.onEvent(this, "edit_hashtag", map);
			}
			intent.putStringArrayListExtra("tagids", tagIdList);
			setResult(RESULT_OK,intent);
			finish();
			break;
		case R.id.iv_edit_media_tag_back:
			finish();
			break;
		}
	}

}
