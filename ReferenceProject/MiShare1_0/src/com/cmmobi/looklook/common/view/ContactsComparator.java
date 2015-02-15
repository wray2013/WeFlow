package com.cmmobi.looklook.common.view;

import java.util.Comparator;

import com.cmmobi.looklook.info.profile.WrapUser;

public class ContactsComparator implements Comparator<WrapUser> {

	@Override
	public int compare(WrapUser lhs, WrapUser rhs) {
		if (lhs.sortKey == null && rhs.sortKey != null) {
			return 1;
		}else if(lhs.sortKey != null && rhs.sortKey == null){
			return -1;
		}else if(lhs.sortKey == null && rhs.sortKey == null){
			return 0;
		}
		return lhs.sortKey.compareTo(rhs.sortKey);
	}

}
