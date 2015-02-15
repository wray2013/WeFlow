package com.cmmobi.railwifi.activity;

import java.util.ArrayList;
import java.util.Collections;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import ca.laplanete.mobile.pageddragdropgrid.DragDropGrid;
import ca.laplanete.mobile.pageddragdropgrid.PagedDragDropGridAdapter;

import com.cmmobi.railwifi.R;
import com.cmmobi.railwifi.activity.ModuleAddActivity.TagAddWrapper;
import com.cmmobi.railwifi.utils.ModuleUtils;
import com.cmmobi.railwifi.utils.ModuleUtils.TagWrapper;
import com.cmmobi.railwifi.utils.ViewUtils;
import com.google.gson.Gson;

public class ModuleMoveActivity extends TitleRootActivity {

	DragDropGrid dragGrid = null;
	TagDragGridAdapter adapter = null;
	
	ArrayList<TagAddWrapper> tagList;
	@Override
	public boolean handleMessage(Message msg) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public int subContentViewId() {
		// TODO Auto-generated method stub
		return R.layout.activity_module_move;
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		initViews();
	}
	
	private void initViews() {
		setRightButtonText("完成>>");
		
		dragGrid = (DragDropGrid) findViewById(R.id.gridview);
		
		ArrayList<Integer> selectedList = getIntent().getIntegerArrayListExtra("modules");
		
		tagList = new ArrayList<TagAddWrapper>();
		if (selectedList != null) {
			for (Integer id:selectedList) {
				TagAddWrapper addWrapper = new TagAddWrapper();
				addWrapper.isAdded = true;
				addWrapper.wrapper = ModuleUtils.findModules(id);
				tagList.add(addWrapper);
			}
		}
		
		for (TagWrapper wrapper:ModuleUtils.tagLists) {
			if (!selectedList.contains(wrapper.tagId)) {
				TagAddWrapper addWrapper = new TagAddWrapper();
				addWrapper.wrapper = wrapper;
				addWrapper.isAdded = false;
				tagList.add(addWrapper);
			}
		}
		adapter = new TagDragGridAdapter(this, tagList);
		dragGrid.setAdapter(adapter);
		
		dragGrid.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				switch (v.getId()) {
				case R.id.rl_tag_layout:
					RelativeLayout rlRoot = (RelativeLayout) v.findViewById(R.id.rl_tag_root);
					ImageView ivSelected = (ImageView) v.findViewById(R.id.iv_tag_selected);
					
					TagAddWrapper tagWrapper = (TagAddWrapper) v.getTag();
					tagWrapper.isAdded = !tagWrapper.isAdded;
					
					if (tagWrapper.isAdded) {
						rlRoot.setBackgroundResource(R.drawable.bg_module_add_normal);
						ivSelected.setVisibility(View.VISIBLE);
					} else {
						rlRoot.setBackgroundResource(R.drawable.white);
						ivSelected.setVisibility(View.GONE);
					}
					break;
				}
			}
		});
		
		/**
		 * 当手指起来时，背景置为白色
		 */
		dragGrid.setOnItemTouchListener(new OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
				switch (v.getId()) {
				case R.id.rl_tag_layout:
					View view = v.findViewById(R.id.rl_tag_root);
					int action = event.getAction();
					TagAddWrapper tagWrapper = (TagAddWrapper) v.getTag();
					switch (action & MotionEvent.ACTION_MASK) {
					case MotionEvent.ACTION_DOWN:
						if (tagWrapper.isAdded) {
							view.setBackgroundResource(R.drawable.bg_module_add_pressed);
						} else {
							view.setBackgroundResource(R.drawable.gray);
						}
						break;
					case MotionEvent.ACTION_MOVE:
						break;
					case MotionEvent.ACTION_UP:
					case MotionEvent.ACTION_CANCEL:
						if (tagWrapper.isAdded) {
							view.setBackgroundResource(R.drawable.bg_module_add_normal);
						} else {
							view.setBackgroundResource(R.drawable.white);
						}
						break;
					}
					break;
				}
				
				return false;
			}
		});
	}
	
	
	class TagAddWrapper {
		public TagWrapper wrapper;
		public boolean isAdded;
		
		@Override
		public boolean equals(Object o) {
			// TODO Auto-generated method stub
			return wrapper.tagId == ((TagAddWrapper) o).wrapper.tagId;
		}
	}
	
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.btn_title_right:
			Intent mIntent = new Intent();
			ArrayList<Integer> selectedModules = getSelectedModule();
			mIntent.putIntegerArrayListExtra("selectedmodule", selectedModules);
			SharedPreferences mySharedPreferences= getSharedPreferences("selected_modules", 
					Activity.MODE_PRIVATE); 
			SharedPreferences.Editor editor = mySharedPreferences.edit();
			String contentStr = new Gson().toJson(selectedModules);
			editor.putString("modules", contentStr);
			editor.commit();
			setResult(RESULT_OK,mIntent);
			finish();
			break;
		default:
			break;
		}
		super.onClick(v);
	}
	
	private ArrayList<Integer> getSelectedModule() {
		ArrayList<Integer> moduleList = new ArrayList<Integer>();
		for (TagAddWrapper addWrapper:tagList) {
			if (addWrapper.isAdded) {
				moduleList.add(addWrapper.wrapper.tagId);
			}
		}
		return moduleList;
	}
	
	class TagDragGridAdapter implements PagedDragDropGridAdapter {

		private Context context;
		private ArrayList<TagAddWrapper> tagList = new ArrayList<TagAddWrapper>();
		
		public TagDragGridAdapter(Context context,ArrayList<TagAddWrapper> list) {
			this.context = context;
			this.tagList = list;
		}
		@Override
		public int pageCount() {
			// TODO Auto-generated method stub
			return 1;
		}

		@Override
		public int itemCountInPage(int page) {
			// TODO Auto-generated method stub
			return tagList.size();
		}

		@Override
		public View view(int page, int index) {
			// TODO Auto-generated method stub
			View convertView = View.inflate(context, R.layout.item_homepage_tag, null);
			ImageView ivTagImg = (ImageView) convertView.findViewById(R.id.iv_tag_img);
			TextView tvTagText = (TextView) convertView.findViewById(R.id.tv_tag_text);
			RelativeLayout rlRoot = (RelativeLayout) convertView.findViewById(R.id.rl_tag_root);
			ImageView ivSelected = (ImageView) convertView.findViewById(R.id.iv_tag_selected);
			
			ViewUtils.setSize(rlRoot, 224, 162);
			ViewUtils.setMarginTop(rlRoot, 12);
			TagAddWrapper wrapper = tagList.get(index);
			
			ivTagImg.setImageResource(wrapper.wrapper.drawableRes);
			tvTagText.setText(wrapper.wrapper.tagDesc);
			
			rlRoot.setTag(wrapper);
			
			rlRoot.setOnTouchListener(new OnTouchListener() {
				
				@Override
				public boolean onTouch(View v, MotionEvent event) {
					// TODO Auto-generated method stub
					int action = event.getAction();
					TagAddWrapper tagWrapper = (TagAddWrapper) v.getTag();
					switch (action & MotionEvent.ACTION_MASK) {
					case MotionEvent.ACTION_DOWN:
						if (tagWrapper.isAdded) {
							v.setBackgroundResource(R.drawable.bg_module_add_pressed);
						} else {
							v.setBackgroundResource(R.drawable.gray);
						}
						break;
					case MotionEvent.ACTION_MOVE:
						break;
					case MotionEvent.ACTION_UP:
					case MotionEvent.ACTION_CANCEL:
						Log.d("=AAA=","ModuleMoveActivity ACTION_UP");
						if (tagWrapper.isAdded) {
							v.setBackgroundResource(R.drawable.bg_module_add_normal);
						} else {
							v.setBackgroundResource(R.drawable.white);
						}
						break;
					}
					return false;
				}
			});
			Log.d("=AAA=","id = " + wrapper.wrapper.tagId + " isAdded = " + wrapper.isAdded);
			if (wrapper.isAdded) {
				rlRoot.setBackgroundResource(R.drawable.bg_module_add_normal);
				ivSelected.setVisibility(View.VISIBLE);
			} else {
				rlRoot.setBackgroundResource(R.drawable.white);
				ivSelected.setVisibility(View.GONE);
			}
			return convertView;
		}

		@Override
		public int rowCount() {
			// TODO Auto-generated method stub
			return AUTOMATIC;
		}

		@Override
		public int columnCount() {
			// TODO Auto-generated method stub
			return AUTOMATIC;
		}

		@Override
		public void printLayout() {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void swapItems(int pageIndex, int itemIndexA, int itemIndexB) {
			// TODO Auto-generated method stub
			Collections.swap(tagList, itemIndexA, itemIndexB);
		}

		@Override
		public void moveItemToPreviousPage(int pageIndex, int itemIndex) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void moveItemToNextPage(int pageIndex, int itemIndex) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void deleteItem(int pageIndex, int itemIndex) {
			// TODO Auto-generated method stub
			tagList.remove(itemIndex);
		}

		@Override
		public int deleteDropZoneLocation() {
			// TODO Auto-generated method stub
			return BOTTOM;
		}

		@Override
		public boolean showRemoveDropZone() {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public int getPageWidth(int page) {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public Object getItemAt(int page, int index) {
			// TODO Auto-generated method stub
			return tagList.get(index);
		}

		@Override
		public boolean disableZoomAnimationsOnChangePage() {
			// TODO Auto-generated method stub
			return true;
		}
		
	}

}
