package com.etoc.weflow.download;

public enum DownloadType {
	MOVIE(1),MUSIC(2),BOOK(3),APP(4);
	
	private int index;
	
	private DownloadType(int index) {
        this.index = index;
    }
	
	public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

}
