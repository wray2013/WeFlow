package com.cmmobi.looklook.common.response.wrapper;

import com.cmmobi.looklook.common.gson.GsonResponse2.nearVideoItem;


/**
 *  @author zhangwei 
 */
public class NearByVideoItemWrapper {

	public NearByVideoItemWrapper(nearVideoItem it){
		item = it;
	}
	
	public nearVideoItem item;
}
