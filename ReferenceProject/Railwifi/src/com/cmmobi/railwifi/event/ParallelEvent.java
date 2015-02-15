package com.cmmobi.railwifi.event;

public enum ParallelEvent{
	CACHE_SCAN_DU, CACHE_CLEAR, FILE_DOWNLOAD, APP_UPDATE;

	private String value;
	
	public String getValue() {
        return value;
    }
	
	public void setValue(String value){
		this.value = value;
	}
}
