package com.cmmobi.railwifi.music;

import com.cmmobi.railwifi.network.GsonResponseObject;

public interface MusicPlayListener {
	public boolean onPlaySong(GsonResponseObject.MusicElem music);
	public boolean onResumeSong(GsonResponseObject.MusicElem music);
	public boolean onPauseSong(GsonResponseObject.MusicElem music);
	public boolean onStopSong(GsonResponseObject.MusicElem music);
	public boolean onError(GsonResponseObject.MusicElem music, int what, int extra);

}
