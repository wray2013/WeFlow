package com.cmmobi.looklook.common.adapter;

import java.util.ArrayList;

import android.widget.BaseAdapter;

/**
 * @author wuxiang
 * @email wuxiang@cmmobi.com
 * @date  2013-12-25
 */
public abstract class SafeboxSubAdapter<T> extends BaseAdapter {

	public abstract void purgeCheckedView();
	public abstract ArrayList<T> getCheckedList();
	
}
