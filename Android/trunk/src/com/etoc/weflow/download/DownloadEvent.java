package com.etoc.weflow.download;

public enum DownloadEvent {
	STATUS_CHANGED, PROGRESS_CHANGED, DONE_CLEAN_ALL; 
	
	public static final int RUNNING_LIST_ADD = 0x10000000;
	public static final int RUNNING_LIST_DEL = 0x20000000;
	
	public static final int DONE_LIST_ADD = 0x40000000;
	public static final int DONE_LIST_DEL = 0x80000000;
	
	private int type; //1 only running list, 2 only done list, -1 all
	
	public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

}
