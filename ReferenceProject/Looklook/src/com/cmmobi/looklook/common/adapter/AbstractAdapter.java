package com.cmmobi.looklook.common.adapter;

import java.util.ArrayList;
import java.util.List;

import android.view.LayoutInflater;
import android.widget.BaseAdapter;

import com.cmmobi.looklook.common.utils.ViewCreator;


public abstract class AbstractAdapter<T> extends BaseAdapter {

	/** 数据缓存 */
	protected List<T> mDataCache;
	
	/** 用于从XML文件中创建Layout */
	protected LayoutInflater mInflater;
	
	/** View创建器 */
	protected ViewCreator<T> mCreator;
	
	/**
	 * </br><b>description : </b>	创建Adapter，需要给定View创建接口。
	 * @param inflater
	 * @param creator
	 */
	public AbstractAdapter(LayoutInflater inflater,ViewCreator<T> creator){
		mInflater = inflater;
		mCreator = creator;
	}

	/**
	 * </br><b>title : </b>		更新数据集
	 * </br><b>description :</b>更新数据集
	 * @param data
	 */
	public void update(List<T> data){
		clear();
		mDataCache = data;
		notifyDataSetChanged();
	}
	
	/**
	 * <b>description :</b>		清除缓存数据
	 */
	public void clear(){
		if( null != mDataCache ){
			mDataCache.clear();
			mDataCache = null;
		}
	}
	/**
	 * <b>description :</b>添加数据集，向数据缓存中添加多个元素。
	 * @param set
	 */
	public void add(List<T> set){
	    if( null == mDataCache ) mDataCache = new ArrayList<T>();
	    mDataCache.addAll(set);
	    notifyDataSetChanged();
	}
	
	/**
	 * </br><b>description :</b>添加数据元素，向数据缓存中添加单个元素。
	 * @param item
	 */
	public void add(T item){
	    if( null == mDataCache ) mDataCache = new ArrayList<T>();
	    mDataCache.add(item);
	    notifyDataSetChanged();
	}
	
	/**
	 * <b>description :</b>		交换两个元素的位置
	 * @param src
	 * @param target
	 */
	public void exchange(int src,int target){
		if (src > target) {
			int temp = target;
			target = src;
			target = temp;
		}
		T endObject = getItem(target);
		T startObject = getItem(src);
		mDataCache.set(src, endObject);
		mDataCache.set(target, startObject);
		notifyDataSetChanged();
	}
	
	public void remove(int position){
		if(mDataCache == null) return;
		mDataCache.remove(position);
		notifyDataSetChanged();
	}
	
	@Override
	public int getCount() {
		return null == mDataCache ? 0 : mDataCache.size();
	}

	@Override
	public T getItem(int position) {
		return null == mDataCache ? null : mDataCache.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}
}
