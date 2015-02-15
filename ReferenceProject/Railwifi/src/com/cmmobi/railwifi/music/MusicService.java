package com.cmmobi.railwifi.music;

import java.util.List;

import android.content.Intent;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnErrorListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.util.Log;

import com.cmmobi.railwifi.network.GsonResponseObject;
import com.cmmobi.railwifi.network.GsonResponseObject.MusicElem;

public class MusicService implements OnCompletionListener, OnErrorListener{
	private static MusicService ins = null;
	
	private int badTryNum = 0;

	//为日志工具设置标签
	private static String TAG = "MusicService";
	//定义音乐播放器变量
	private Player mPlayer;

	private int music_index = 0;
	private int pic_index = 0;
	private Boolean isPlayed = false;
	private List<GsonResponseObject.MusicElem> musicItemList;

	private boolean isList = false;

	private MusicPlayListener listen;
	
//	public static String TAG_PLAY_MODE = "TAG_PLAY_MODE";

//	public static String BROADCAST_MUSIC_SERVICE_STARTED = "BROADCAST_MUSIC_SERVICE_STARTED";
	private MusicService(){
		mPlayer = new Player();//MediaPlayer.create(getApplicationContext(), Uri.parse(paths[index]));
	}
	
	public static MusicService getInstance(){
		if(ins==null){
			ins = new MusicService();
		}
		
		return ins;
	}
	public void stopPlay() {
		if(musicItemList==null || musicItemList.size()<=0){
			return;
		}
		if(music_index < musicItemList.size() && mPlayer !=null){
			mPlayer.stop();
		}
	}
		
	public Boolean playSong(){
		mPlayer.setOnCompletionListener(this);
		mPlayer.setOnErrorListener(this);
		
		if(musicItemList==null || musicItemList.size()<=0){
			return false;
		}
		if(music_index < musicItemList.size() && music_index >= 0 && mPlayer !=null){
			try {
				
				final MusicElem musicItem = musicItemList.get(music_index);

				Log.v(TAG, "play " + musicItem.name + ", " + musicItem.src_path);
//				EventBus.getDefault().post(MusicEvent.SONG_PLAY.setValue("123"/*musicId*/));
				if(listen!=null){
					listen.onPlaySong(musicItem);
				}
				
				if(musicItem.src_path!=null && !"".equals(musicItem.src_path)){
					mPlayer.playUrl(musicItem.src_path);
					isPlayed = true;
					return true;
				}

			} catch (IllegalArgumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (SecurityException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalStateException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}else{
			mPlayer.stop();
		}
		return false;
	}
	
	public void pausePlay(){
		mPlayer.pause();
	}
	
		
	
	//下一首
	public Boolean nextPlay(){
		if(musicItemList==null || musicItemList.size()<=0){
			return false;
		}
		
		if(music_index >= musicItemList.size()-1){
			music_index = 0;
//			mPlayer.stop();
//			isPlayed = false;
//			return false;
		}else{
			music_index ++;
		}
		
		pic_index = 0;
	
		return playSong();
	}
	
	//上一首
	public Boolean backPlay(){
		if(musicItemList==null || musicItemList.size()<=0){
			return false;
		}
		
		if(music_index <= 0){
			music_index = musicItemList.size() - 1;
//			mPlayer.stop();
//			isPlayed = false;
//			return false;
		}else{
			music_index --;
		}
		
		return playSong();
	}
	
	//播放当前音乐
	public Boolean startPlay(){		
		if(musicItemList==null || musicItemList.size()<=0){
			return false;
		}
		
		if(isPlayed && music_index >= 0 && music_index < musicItemList.size()){
			mPlayer.resume();
			return true;
		}
		
		if(music_index < 0){
			music_index = 0;
		}else if(music_index >= musicItemList.size()){
			music_index = musicItemList.size() -1;
		}
		return playSong();
	}
	
	//获取当前状态
	public Boolean isPlaying(){
		return mPlayer.isPlaying();
	} 
	
	//重置列表的播放状态
	public void setIsPlayed(Boolean isplayed){
		this.isPlayed = isplayed;
	}
	
	public void setPlayMode(final Boolean isList){
		this.isList = isList;
	}

	public void setPlayArray(List<GsonResponseObject.MusicElem> pathArray){
		musicItemList = pathArray;
		badTryNum = 0;
	}

	public boolean getIsFirstOrLast() {
		// TODO Auto-generated method stub
		return false;
	}
	
	public void setCurMusicId(String music_id) {
		// TODO Auto-generated method stub
		music_index = 0;
		if(musicItemList!=null && musicItemList.size()>0){
			for(int index=0; index<musicItemList.size(); index++){
				if(music_id.equals(musicItemList.get(index).media_id)){
					music_index = index;
					break;
				}
			}
		}
	}
	
	public String curSongName(){
		if(musicItemList!=null && music_index>=0 && music_index<musicItemList.size()){
			GsonResponseObject.MusicElem item = musicItemList.get(music_index);
			return item.name;
		}else{
			return "";
		}

	}
	
	public GsonResponseObject.MusicElem curSong(){
		GsonResponseObject.MusicElem item = musicItemList.get(music_index);
		return item;
	}
	
	public void setListener(MusicPlayListener l){
		listen = l;
	}

	public String nextPic() {
		// TODO Auto-generated method stub
		try{
			GsonResponseObject.MusicElem item = musicItemList.get(music_index);
			int pic_num = item.img_path.length;
			if(pic_index>=pic_num-1){
				pic_index = 0;
			}else{
				pic_index++;
			}

			return item.img_path[pic_index];
		}catch(Exception e){
			return null;
		}

	}

	@Override
	public void onCompletion(MediaPlayer mp) {
		// TODO Auto-generated method stub
		if(musicItemList==null || musicItemList.size()<=0){
			return;
		}
		if(isList){
			nextPlay();
		}else{
		    if(listen!=null){
		    	listen.onStopSong(musicItemList.get(music_index));
		    }	
		}
	}

	@Override
	public boolean onError(MediaPlayer mp, int what, int extra) {
		// TODO Auto-generated method stub
		if(listen!=null){
			MusicElem cur = (musicItemList!=null&&music_index>=0)?musicItemList.get(music_index):null;
			if(!listen.onError(cur, what, extra)){
				badTryNum++;
				if(badTryNum>2){
					badTryNum = 0;
					return true;
				}else{
					if(isList){
						nextPlay();
					}else{
					    if(listen!=null){
					    	listen.onStopSong(musicItemList.get(music_index));
					    }	
					}
				}


			}
		}
		return true;
	}


	

}

