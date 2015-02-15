package com.cmmobi.looklook.common.view;

import java.util.Comparator;

import com.cmmobi.looklook.fragment.WrapRenrenUser;


public class RenrenComparator implements Comparator<WrapRenrenUser> {

	@Override
	public int compare(WrapRenrenUser lhs, WrapRenrenUser rhs) {
		if (lhs.sortKey == null || rhs.sortKey == null) {
			return -1;
		}
		return lhs.sortKey.compareTo(rhs.sortKey);
	}

}
