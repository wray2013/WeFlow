package cn.zipper.framwork.adapter;

import java.util.ArrayList;
import java.util.List;

import android.widget.BaseAdapter;
import cn.zipper.framwork.core.ZViewFinder;

public abstract class ZBaseAdapter<T> extends BaseAdapter {
	
	protected ZViewFinder viewFinder;
	protected List<T> list;
	
	public ZBaseAdapter() {
		this.viewFinder = new ZViewFinder();
		list = new ArrayList<T>();
	}
	
	public void setList(List<T> list) {
		this.list = list;
		this.notifyDataSetChanged();
	}
	
	public List<T> getList() {
		return list;
	}

	@Override
	public int getCount() {
		return list.size();
	}

	@Override
	public T getItem(int location) {
		return list.get(location);
	}

	@Override
	public long getItemId(int location) {
		return location;
	}
}
