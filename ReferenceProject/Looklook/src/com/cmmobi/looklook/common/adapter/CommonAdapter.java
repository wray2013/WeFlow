package com.cmmobi.looklook.common.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.cmmobi.looklook.common.utils.ViewCreator;

/**
 *  @author zhangwei 
 */
public class CommonAdapter<T> extends AbstractAdapter<T> {

	public CommonAdapter(LayoutInflater inflater, ViewCreator<T> creator) {
		super(inflater, creator);
	}

	@Override
	public View getView(int pos, View convertView, ViewGroup parent) {
		T data = mDataCache.get(pos);
		if(null == convertView){
			convertView = mCreator.createView(mInflater, pos, data);
		}else{
			mCreator.updateView(convertView, pos, data);
		}
		return convertView;
	}

}

