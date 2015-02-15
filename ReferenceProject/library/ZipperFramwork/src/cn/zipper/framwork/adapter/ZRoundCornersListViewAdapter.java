package cn.zipper.framwork.adapter;

import java.util.ArrayList;
import java.util.List;

import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;
import cn.zipper.framwork.core.ZApplication;
import cn.zipper.framwork.core.ZLayoutInflater;
import cn.zipper.framwork.core.ZLog;
import cn.zipper.framwork.core.ZViewFinder;

public  class ZRoundCornersListViewAdapter {
	
	public static final int ITEM_TYPE_TOP = 1;
	public static final int ITEM_TYPE_MIDDLE = 2;
	public static final int ITEM_TYPE_BOTTOM = 3;
	public static final int ITEM_TYPE_SINGLE = 4;
	public static final int ITEM_TYPE_DIVIDER = 5;
	public static final int ITEM_TYPE_SPACE = 6;
	
	public static class RoundCornersItem {
		protected int itemType;
		protected View view;
		
		public RoundCornersItem(int itemType, View view) {
			this.itemType = itemType;
			this.view = view;
		}
	}
	
	private class RoundCornersGroup {
		private View titleView;
		private List<RoundCornersItem> list;
		private boolean useSpaceView;
		
		private RoundCornersGroup(View titleView, boolean useSpaceView) {
			this.titleView = titleView;
			this.list = new ArrayList<RoundCornersItem>();
			this.useSpaceView = useSpaceView;
		}
	}
	
	private int backgroundResource;
	private int backgroundEdgeSize;
	private int topItemResource;
	private int middleItemResource;
	private int bottomItemResource;
	private int singleItemResource;
	private int dividerResource;
	private int spaceResource;
	private int viewId;
	private List<RoundCornersGroup> list;
	private RoundCornersGroup currentGroup;
	private OnClickListener listener;
	private ZViewFinder viewFinder;
	
	public ZRoundCornersListViewAdapter(int backgroundResource, int backgroundEdgeSize, int topItemResource, int middleItemResource, int bottomItemResource, int singleItemResource, int dividerResource, int spaceResource, OnClickListener listener) {
		this.backgroundResource = backgroundResource;
		this.backgroundEdgeSize = backgroundEdgeSize;
		this.topItemResource = topItemResource;
		this.middleItemResource = middleItemResource;
		this.bottomItemResource = bottomItemResource;
		this.singleItemResource = singleItemResource;
		this.dividerResource = dividerResource;
		this.spaceResource = spaceResource;
		this.listener = listener;
		this.list = new ArrayList<RoundCornersGroup>();
		this.viewFinder = new ZViewFinder();
	}
	
	public void createGroup(View titleView, boolean useSpaceView) {
		currentGroup = new RoundCornersGroup(titleView, useSpaceView);
		list.add(currentGroup);
	}
	
	public ZViewFinder addItem(RoundCornersItem item) {
		currentGroup.list.add(item);
		if (item.itemType == ITEM_TYPE_TOP || item.itemType == ITEM_TYPE_MIDDLE) {
			View view = ZLayoutInflater.inflate(dividerResource);
			RoundCornersItem item2 = new RoundCornersItem(ITEM_TYPE_DIVIDER, view);
			LayoutParams params = new LayoutParams(LayoutParams.FILL_PARENT, 1);
			item2.view.setLayoutParams(params);
			currentGroup.list.add(item2);
		}
		viewFinder.set(item.view);
		
		return viewFinder;
	}
	
	public void adaptTo(LinearLayout layout) {
		for (RoundCornersGroup group : list) {
			
			if (group.titleView != null) {
				layout.addView(group.titleView);
			}
			
			LinearLayout groupLayout = new LinearLayout(ZApplication.getInstance());
			LayoutParams params = new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
			groupLayout.setLayoutParams(params);
			groupLayout.setOrientation(LinearLayout.VERTICAL);
			groupLayout.setBackgroundResource(backgroundResource);
			groupLayout.setPadding(backgroundEdgeSize, backgroundEdgeSize, backgroundEdgeSize, backgroundEdgeSize);
			
			for (RoundCornersItem item : group.list) {
				getItemView(groupLayout, item);
				groupLayout.addView(item.view);
			}
			
			layout.addView(groupLayout);
			
			if (group.useSpaceView) {
				View space = ZLayoutInflater.inflate(spaceResource);
				LayoutParams spaceParams = new LayoutParams(LayoutParams.FILL_PARENT, 25);
				space.setLayoutParams(spaceParams);
				layout.addView(space);
			}
		}
	}
	
	public  void getItemView(LinearLayout groupLayout, RoundCornersItem item) {
		switch (item.itemType) {
		case ITEM_TYPE_TOP:
			item.view.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
			item.view.setBackgroundResource(topItemResource);
			item.view.setOnClickListener(listener);
			item.view.setId(viewId);
			viewId ++;
			break;
			
		case ITEM_TYPE_MIDDLE:
			item.view.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
			item.view.setBackgroundResource(middleItemResource);
			item.view.setOnClickListener(listener);
			item.view.setId(viewId);
			viewId ++;
			break;
			
		case ITEM_TYPE_BOTTOM:
			item.view.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
			item.view.setBackgroundResource(bottomItemResource);
			item.view.setOnClickListener(listener);
			item.view.setId(viewId);
			viewId ++;
			break;
			
		case ITEM_TYPE_SINGLE:
			item.view.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
			item.view.setBackgroundResource(singleItemResource);
			item.view.setOnClickListener(listener);
			item.view.setId(viewId);
			viewId ++;
			break;
		}
	}

}
