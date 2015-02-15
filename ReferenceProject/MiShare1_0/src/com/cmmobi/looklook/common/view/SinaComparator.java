package com.cmmobi.looklook.common.view;

import java.util.Comparator;

import com.cmmobi.looklook.fragment.WrapSinaUser;


public class SinaComparator implements Comparator<WrapSinaUser> {

	@Override
	public int compare(WrapSinaUser lhs, WrapSinaUser rhs) {
		if (lhs.sortKey == null || rhs.sortKey == null) {
			return -1;
		}
		return lhs.sortKey.compareTo(rhs.sortKey);
	}

}
