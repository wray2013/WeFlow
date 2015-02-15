package com.cmmobi.looklook.fragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cmmobi.looklook.R;
import com.cmmobi.looklook.activity.EditMediaDetailActivity;
import com.cmmobi.looklook.activity.EditPhotoDetailActivity;
import com.cmmobi.looklook.common.gson.GsonResponse2.MyDiary;
import com.cmmobi.looklook.common.gson.GsonResponse2.TAG;
import com.cmmobi.looklook.common.gson.GsonResponse2.taglistItem;
import com.cmmobi.looklook.common.utils.CmmobiClickAgentWrapper;
import com.cmmobi.looklook.info.profile.DiaryManager;

public class EditMediaTagFragment extends Fragment implements OnClickListener{

	private static final String TAG = "EditMediaTagFragment";
	private EditMediaDetailActivity mActivity = null;
	private GridView tagGrid = null;
	private List<Map<String,String>> tagList;
	private int tagNum;
	private ImageView tagEditFinish = null;
	private ImageView tagEditCancle = null;
	private List<Integer> checkedList ;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		CmmobiClickAgentWrapper.onEvent(mActivity, "my_edit_de", "4");
		CmmobiClickAgentWrapper.onEventBegin(mActivity, "my_edit_de", "4");
		View view = inflater.inflate(R.layout.fragment_edit_media_tag, container, false);
		
		checkedList = mActivity.getCheckedList();
		tagGrid = (GridView) view.findViewById(R.id.gv_edit_media_tag);
		tagNum = 0;
		TagAdapter adapter = new TagAdapter(mActivity, initTag());
		tagGrid.setAdapter(adapter);
		
		
		tagEditFinish = (ImageView) view.findViewById(R.id.iv_edit_media_tag_yes);
		tagEditCancle = (ImageView) view.findViewById(R.id.iv_edit_media_tag_no);
		
		tagEditFinish.setOnClickListener(this);
		tagEditCancle.setOnClickListener(this);
		
		return view;
	}

	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		switch(arg0.getId()) {
		case R.id.ll_edit_media_tag_item:
			Log.d(TAG,"onClick ll_edit_media_tag_item in");
			int position = ((TagAdapter.ViewHolder)(arg0.getTag())).position;
			Log.d(TAG,"******in position = " + position + " checkedList = " + checkedList);
			boolean isChecked = Boolean.valueOf(tagList.get(position).get("checked"));
			if (isChecked) {
				arg0.setBackgroundResource(R.drawable.biaoqian_normal);
				tagList.get(position).put("checked", String.valueOf(false));
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
				arg0.setBackgroundResource(R.drawable.biaoqian_selected);
				tagList.get(position).put("checked", String.valueOf(true));
				tagNum++;
				if (checkedList.size() < 3) {
					checkedList.add(position);
					Log.d(TAG,"onClick add position = " + position);
				}
			}
			Log.d(TAG,"++++++out position = " + position + " checkedList = " + checkedList);
			break;
		case R.id.iv_edit_media_tag_yes:
			Log.d(TAG,"checkList = " + checkedList);
			int checkedSize = checkedList.size();
			mActivity.tagStr = "";
			mActivity.clearTag();
	    	for (int i = 0; i < checkedSize; i++) {
	    	    int pos = checkedList.get(i);
	    	    if (pos >= 0 && pos < tagList.size()) {
	    	    	TAG tag = new TAG();
	    	    	tag.id = tagList.get(pos).get("id");
	    	    	tag.name = tagList.get(pos).get("name");
	    	    	mActivity.addTag(tag);
	    	    	mActivity.tagStr += tag.name;
    				if (i != checkedSize - 1) {
    					mActivity.tagStr += "，";
    				}
	    	    }
	    	}
	    	if (checkedSize > 0) {
	    		if (mActivity.getTagList().size() > 0) {
					mActivity.getMyDiary().tags = (TAG[]) mActivity.getTagList().toArray(new TAG[mActivity.getTagList().size()]);
				}
	    		
	    		mActivity.isDiaryDetailChanged = true;
	    	} else {
	    		if (!mActivity.getTags().equals(EditMediaDetailActivity.getTagString(mActivity.myDiary))) {
	    			mActivity.getMyDiary().tags = (TAG[]) mActivity.getTagList().toArray(new TAG[mActivity.getTagList().size()]);
	    			mActivity.isDiaryDetailChanged = true;
	    			mActivity.tagStr = "";
	    		}
	    	}
	    	
	    	CmmobiClickAgentWrapper.onEventEnd(mActivity, "my_edit_de", "4");
			mActivity.goToPage(EditPhotoDetailActivity.FRAGMENT_DETAIL_MAIN_VIEW, false);
			break;
		case R.id.iv_edit_media_tag_no:
			CmmobiClickAgentWrapper.onEventEnd(mActivity, "my_edit_de", "4");
			mActivity.goToPage(EditPhotoDetailActivity.FRAGMENT_DETAIL_MAIN_VIEW, false);
			break;
		}
	}
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);

		Log.e(TAG, "NearByFragment - onAttach");

		try {
			mActivity = (EditMediaDetailActivity) activity;
			// onGotoPageListener = (OnGotoPageListener) activity;
		} catch (ClassCastException e) {
			throw new ClassCastException(activity.toString() + " must implement OnGotoPageListener");
		}
	}
	
    private List<Map<String,String>> initTag() {
    	tagList = new ArrayList<Map<String,String>>();
		DiaryManager diarymanager = DiaryManager.getInstance();
		List<taglistItem> tagStrList = diarymanager.getTags();
//        String [] tags = getResources().getStringArray(R.array.activity_edit_media_tags);
    	int size = tagStrList.size();
    	
    	TAG[] tags = mActivity.getMyDiary().tags;
    	int tagSize = 0;
    	if (tags != null) {
    	   tagSize = tags.length;
    	}
    	
    	for (int i = 0;i < size; i++) {
    		HashMap<String,String> hashmap = new HashMap<String,String>();
    		hashmap.put("id", tagStrList.get(i).id);
    		hashmap.put("name", tagStrList.get(i).name);
    		hashmap.put("checked", tagStrList.get(i).checked);
    		Log.d(TAG,"i = " + i + " id = " + tagStrList.get(i).id + " name = " + tagStrList.get(i).name + " checked = " + tagStrList.get(i).checked);
    		for (int j = 0;j < tagSize;j++) {
    			if (tags[j].id.equals(tagStrList.get(i).id)) {
    				if (!checkedList.contains(i)) {
    					checkedList.add(i);
    				}
    			}
    		}
    		tagList.add(hashmap);
    	}
    	
    	Log.d(TAG,"EditMediaTagFragment initTag checkedList " + checkedList);
    	int checkedSize = checkedList.size();
    	for (int i = 0; i < checkedSize; i++) {
    	    int position = checkedList.get(i);
    	    if (position >= 0 && position < size) {
    	    	tagList.get(position).put("checked", String.valueOf(true));
    	    }
    	}
    	tagNum = checkedList.size();
//    	checkedList.clear();
    	
    	return tagList;
    }
	
	private class TagAdapter extends BaseAdapter {

		private Context context;
		private LayoutInflater inflater;

		public TagAdapter(Context context, List<Map<String, String>> data) {
			this.context = context;
			inflater = LayoutInflater.from(context);
		}

		@Override
		public int getCount() {
			Log.d(TAG,"getCount = " + tagList.size());
			return tagList.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder = null;

			if (convertView == null) {
				convertView = inflater.inflate(
						R.layout.activity_media_tag_item, null);
				holder = new ViewHolder();
				holder.tv = (TextView)convertView.findViewById(R.id.tv_edit_media_tag);
				holder.ll = (LinearLayout) convertView.findViewById(R.id.ll_edit_media_tag_item);
				
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			
			if (tagList.get(position).get("checked").equals(String.valueOf(true))) {
				Log.d(TAG,"========position = " + position);
				convertView.setBackgroundResource(R.drawable.biaoqian_selected);
//				if (checkedList.size() < 3) {
//					checkedList.add(position);
//				}
//				tagNum++;
			} else {
				convertView.setBackgroundResource(R.drawable.biaoqian_normal);
			}
			
			holder.position = position;
			holder.tv.setText(tagList.get(position).get("name"));
			holder.ll.setOnClickListener(EditMediaTagFragment.this);
			return convertView;
		}

		class ViewHolder {
			LinearLayout ll;
			TextView tv;
			public int position;
		}

	}
}
