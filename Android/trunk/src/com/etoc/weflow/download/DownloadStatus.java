package com.etoc.weflow.download;

public enum DownloadStatus {
	WAIT(1),PERPARE(2),RUN(3),PAUSE(4),DONE(5),FAIL(6),USERPAUSE(7),USERRESUME(8);
	//WAIT,    不在执行队列中
	//PERPARE, 在执行队列中，开始下载之前（之前没有下载数据或冷启动续传前的对比）
	//RUN,     下载数据
	//PAUSE，     因为网络原因（无网或变成3g），磁盘原因（没有空间，写磁盘失败），导致的全局性因素
	//DONE,    下载成功
	//FAIL,     非全局原因，如url失效，服务器拒绝
	
	private int index;
	private String reason;
	
	public static final String REASON_NETWORK_NO_WIFI = "网络非wifi";
	public static final String REASON_STORAGE_NO_SPACE = "空间不足";
	public static final String REASON_IO_EXCEPION = "磁盘读写异常";
	public static final String REASON_STORAGE_NO_SDCARD = "没有监检测到sdcard";
	public static final String REASON_USER_PAUSE = "用户暂停";
	
	private DownloadStatus(int index) {
        this.index = index;
    }
	
	public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }
    
	public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
    
}
