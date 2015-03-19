package com.etoc.weflow.download;

public enum DownloadEvent {
	STATUS_CHANGED, PROGRESS_CHANGED, DONE_CLEAN_ALL; 
	
	public static final int RUNNING_LIST_ADD = 0x10000000;
	public static final int RUNNING_LIST_DEL = 0x20000000;
	
	public static final int DONE_LIST_ADD = 0x40000000;
	public static final int DONE_LIST_DEL = 0x80000000;
	
	private int type; //1 only running list, 2 only done list, -1 all
	private int downloadSize = 0;
	private String url = "";
	
	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public int getDownloadSize() {
		return downloadSize;
	}

	public void setDownloadSize(int downloadSize) {
		this.downloadSize = downloadSize;
	}

	public int getWholeSize() {
		return wholeSize;
	}

	public void setWholeSize(int wholeSize) {
		this.wholeSize = wholeSize;
	}

	private int wholeSize = 0;
	
	public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

}
