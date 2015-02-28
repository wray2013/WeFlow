package com.etoc.weflowdemo.event;

public enum RequestEvent {
	RESP_NULL, LOADING_START, LOADING_END;
	private String value;
	
	public String getValue() {
        return value;
    }
	
	public void setValue(String value){
		this.value = value;
	}
}
