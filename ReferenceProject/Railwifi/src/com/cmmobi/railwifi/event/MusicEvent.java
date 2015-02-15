package com.cmmobi.railwifi.event;

public enum MusicEvent {
	SONG_PLAY, SONG_PAUSE, SONG_RESUME, SONG_STOP;
	private String musicId;
	
	public String getValue() {
        return musicId;
    }
	
	public MusicEvent setValue(String value){
		this.musicId = value;
		return this;
	}
}
