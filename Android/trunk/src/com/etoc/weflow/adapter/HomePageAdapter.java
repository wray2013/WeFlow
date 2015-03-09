package com.etoc.weflow.adapter;

import android.content.Context;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

public class HomePageAdapter extends BaseAdapter {

	private LayoutInflater inflater;
	private Context ctx;
	protected DisplayMetrics dm;
	
	public HomePageAdapter(Context context, DisplayMetrics dm) {
		this.ctx = context;
		this.inflater = LayoutInflater.from(ctx);
		this.dm = dm;
		
		
		
	}
	
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public Object getItem(int arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public View getView(int arg0, View arg1, ViewGroup arg2) {
		// TODO Auto-generated method stub
		return null;
	}

}
