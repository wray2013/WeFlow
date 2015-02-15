package com.cmmobi.looklook.common.view;

import java.util.Comparator;

import com.cmmobi.looklook.fragment.WrapTencentUser;


public class TencentComparator implements Comparator<WrapTencentUser> {

	@Override
	public int compare(WrapTencentUser lhs, WrapTencentUser rhs) {
		if (lhs.sortKey == null || rhs.sortKey == null) {
			return -1;
		}
		return lhs.sortKey.compareTo(rhs.sortKey);
	}

}
