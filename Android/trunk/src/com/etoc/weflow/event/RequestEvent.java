package com.etoc.weflow.event;

public enum RequestEvent {
	RESP_NULL, LOADING_START, LOADING_END,RESUMING_DOWNLOADING, PAUSE_DOWNLOAIND, PAUSE_EXCEPTION;;
	private String value;
	
	public String getValue() {
        return value;
    }
	
	public void setValue(String value){
		this.value = value;
	}
}
