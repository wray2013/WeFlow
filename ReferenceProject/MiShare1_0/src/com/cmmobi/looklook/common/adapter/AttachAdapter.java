package com.cmmobi.looklook.common.adapter;

import java.util.ArrayList;

import com.cmmobi.looklook.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

public class AttachAdapter extends BaseAdapter {

	ArrayList<String> contentURLs;
	LayoutInflater inflater;
	public AttachAdapter(Context context, ArrayList<String> contentURLs){
		this.contentURLs = contentURLs;
		inflater = LayoutInflater.from(context);
	}
	
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return contentURLs.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return contentURLs.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		String contentURL = contentURLs.get(position);
		ViewHolder holder;
		if(convertView == null){
			holder = new ViewHolder();
			convertView = inflater.inflate(R.layout.friends_content_attachs, null);
		}else{
			convertView.getTag();
		}
		return null;
	}
	
	class ViewHolder{
	}

}
