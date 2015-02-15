package com.cmmobi.looklook.common.utils;



import android.view.LayoutInflater;
import android.view.View;


public class ViewBuilder<T> implements ViewCreator<T> {

	@Override
	public View createView(LayoutInflater inflater, int position, T data) {
		return null;
	}

	@Override
	public void updateView(View view, int position, T data) { }

	@Override
	public void releaseView(View view, T data) { }

}
