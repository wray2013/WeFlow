package com.cmmobi.railwifi.event;

public enum DialogEvent{
	LOADING_START, LOADING_END, CALL_HELP_DIALOG, /*PROMPT,*/ DOWNLOAD, UPDATE_FORCE_DIALOG_ALWAYS, UPDATE_FORCE_DIALOG_DISMISS, UPDATE_NORMAL_DIALOG;

	private String title;
	private String content;
	private String ok;
	private String url;
	private int notifyid;
	
	
	public String getTitle() {
        return title;
    }
	
	public void setTitle(String title){
		this.title = title;
	}
	
	public String getContent() {
        return content;
    }
	
	public void setContent(String content){
		this.content = content;
	}
	
	public String getOK() {
        return ok;
    }
	
	public void setOK(String ok){
		this.ok = ok;
	}
	
	public String getUrl() {
        return url;
    }
	
	public void setUrl(String url){
		this.url = url;
	}
	
	public int getNotifyID() {
        return notifyid;
    }
	
	public void setNotifyID(int notifyid){
		this.notifyid = notifyid;
	}
}
