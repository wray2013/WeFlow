package com.cmmobi.looklook.wheeltime;

import java.util.ArrayList;

public class ArrayListWheelAdapter<T> implements WheelAdaptertime {

	private ArrayList<T> itemList;
	
	public ArrayListWheelAdapter(ArrayList<T> list) {
		itemList = list;
	}
	
	@Override
	public int getItemsCount() {
		// TODO Auto-generated method stub
		return itemList.size();
	}

	@Override
	public String getItem(int index) {
		// TODO Auto-generated method stub
		if (index >= 0 && index < itemList.size()) {
			return itemList.get(index).toString();
		}
		return null;
	}

	@Override
	public int getMaximumLength() {
		// TODO Auto-generated method stub
		return -1;
	}
	
	public void appendItem(T item) {
		itemList.add(item);
	}
	
	public void insertItem(T item) {
		itemList.add(0, item);
	}

}
