package com.cmmobi.looklook.common.storage;



/**
 *  @author zhangwei 
 */
public class StorageValue {
	public StorageValue(String key, int fileSize){
		//this.drawable = drawable;
		this.fileSize = fileSize;
		this.key = key;
	}
	
	//public Drawable drawable;
	public int fileSize;
	public String key;

}
