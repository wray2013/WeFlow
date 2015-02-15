package com.cmmobi.looklook.common.utils;

import java.io.IOException;

import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnErrorListener;
import android.os.Environment;

import com.cmmobi.looklook.MainApplication;
import com.cmmobi.looklook.info.profile.AccountInfo;
import com.cmmobi.looklook.info.profile.ActiveAccount;
import com.cmmobi.looklook.info.profile.MediaValue;

public class AudioPlayerHelper implements OnCompletionListener,OnErrorListener {
	private MediaPlayer mp;
	private boolean isPlaying;
	private AudioPlayerHelper(){
		mp = new MediaPlayer();
		isPlaying = false;
	}
	
	static AudioPlayerHelper ins;
	
	public static AudioPlayerHelper getInstance(){
		if(ins==null){
			ins = new AudioPlayerHelper();
		}
		
		return ins;
	} 
	
	
	public void play(String uri){
		stop();
		
		
		String uid = ActiveAccount.getInstance(MainApplication.getAppInstance()).getUID();
		MediaValue mv = AccountInfo.getInstance(uid).mediamapping.getMedia(uid, uri);
		
        String path = null;
        if (MediaValue.checkMediaAvailable(mv)) {
        	//hit
        	path = Environment.getExternalStorageDirectory() + mv.path;
        }else{
        	//not hit
        	path = uri;
        }
        
		if(isPlaying)
			return;
		mp = new MediaPlayer(); 
		mp.setOnCompletionListener(this);
		mp.setOnErrorListener(this);
		try {
			mp.setDataSource(path);
			mp.prepare();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalStateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		mp.start();
		isPlaying = true;
	}
	
	
	public void stop(){
		if(mp!=null){
			mp.stop();
			mp.release();
			mp = null;
			isPlaying = false;
		}
	}


	@Override
	public boolean onError(MediaPlayer mp, int what, int extra) {
		// TODO Auto-generated method stub
		isPlaying = false;
		stop();
		return false;
	}


	@Override
	public void onCompletion(MediaPlayer mp) {
		// TODO Auto-generated method stub
		isPlaying = false;
		stop();
	}

}
