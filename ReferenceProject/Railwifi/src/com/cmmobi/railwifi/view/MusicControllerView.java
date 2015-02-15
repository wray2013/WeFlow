package com.cmmobi.railwifi.view;

import java.util.ArrayList;
import java.util.List;

import android.R.string;
import android.content.Context;
import android.content.Intent;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.cmmobi.railwifi.R;
import com.cmmobi.railwifi.music.MusicService;
import com.cmmobi.railwifi.network.GsonResponseObject;
import com.cmmobi.railwifi.utils.DisplayUtil;

/**
 * 音乐播放控件
 * @author youtian
 * @email youtian@cmmobi.com
 * @date  2015-01-14
 */

public class MusicControllerView extends RelativeLayout{
	
	private static String TAG = "MusicControllerView";
	
	private Context context;
	
	private Boolean isBigView = true;
	private Boolean isList = false;
//	private static List<GsonResponseObject.MusicElem> pathArrayList;
	
	public static String BROADCAST_MUSIC_PLAY = "BROADCAST_MUSIC_PLAY";
	public static String BROADCAST_MUSIC_PAUSE = "BROADCAST_MUSIC_PAUSE";
	
	public MusicControllerView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		this.context = context;
		init();
	}

	public MusicControllerView(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.context = context;
		init();
	}

	public MusicControllerView(Context context) {
		super(context);
		this.context = context;
		init();
	}

	private LayoutInflater inflater;
	

	private Button btnBack;
	private Button btnNext;
	private Button btnPlay;
	
	public void startPlay(){
		MusicService musicService = MusicService.getInstance();
		int resPlay;
		int resPause;
		if(isBigView){
			resPause = R.drawable.btn_music_big_pause;
			resPlay = R.drawable.btn_music_big_play;
		}else{
			resPause = R.drawable.btn_music_pause;
			resPlay = R.drawable.btn_music_play;
		}
		if(musicService == null) return;
		musicService.setPlayMode(isList);
		if(!musicService.isPlaying()){
			if(musicService.startPlay()){
				btnPlay.setBackgroundResource(resPause);
				Intent intent = new Intent(BROADCAST_MUSIC_PLAY);
				context.sendBroadcast(intent);
			}else{
				btnPlay.setBackgroundResource(resPlay);
				Intent intent = new Intent(BROADCAST_MUSIC_PAUSE);
				context.sendBroadcast(intent);
			}

		}else{
			musicService.pausePlay();
			btnPlay.setBackgroundResource(resPlay);
			Intent intent = new Intent(BROADCAST_MUSIC_PAUSE);
			context.sendBroadcast(intent);
		}
	}
	
	public void resetButton(){
		int resPlay;
		if(isBigView){
			resPlay = R.drawable.btn_music_big_play;
		}else{
			resPlay = R.drawable.btn_music_play;
		}
		MusicService.getInstance().stopPlay();
		/*btnBack.setEnabled(false);
		btnNext.setEnabled(true);*/
		btnPlay.setBackgroundResource(resPlay);
		Intent intent = new Intent(BROADCAST_MUSIC_PAUSE);
		context.sendBroadcast(intent);
	}
	
	public void pause(){
		MusicService.getInstance().pausePlay();
		setButtonPause();
	}
	
	public void setButtonPause(){
		int resPause;
		if(isBigView){
			resPause = R.drawable.btn_music_big_pause;
		}else{
			resPause = R.drawable.btn_music_pause;
		}
		btnPlay.setBackgroundResource(resPause);
	}
	
	public void setButtonPlay(){
		int resPlay;
		if(isBigView){
			resPlay = R.drawable.btn_music_big_play;
		}else{
			resPlay = R.drawable.btn_music_play;
		}
		btnPlay.setBackgroundResource(resPlay);
	}
	
	private OnClickListener listener =  new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			MusicService musicService = MusicService.getInstance();
			int resPlay;
			int resPause;
			if(isBigView){
				resPause = R.drawable.btn_music_big_pause;
				resPlay = R.drawable.btn_music_big_play;
			}else{
				resPause = R.drawable.btn_music_pause;
				resPlay = R.drawable.btn_music_play;
			}
			if(musicService == null) return;
			musicService.setPlayMode(isList);
//			musicService.setPlayArray(pathArrayList);
			switch (v.getId()) {
			case R.id.btn_music_play_pause:
				startPlay();
				MusicControllerView.this.callOnClick();
				break;
			case R.id.btn_music_forward:
				if(musicService.backPlay()){
					btnPlay.setBackgroundResource(resPause);
					Intent intent = new Intent(BROADCAST_MUSIC_PLAY);
					context.sendBroadcast(intent);
					/*btnNext.setEnabled(true);
					
					if(musicService.getIsFirstOrLast()){
						btnBack.setEnabled(false);
					}*/
				}else{
					btnPlay.setBackgroundResource(resPlay);
					Intent intent = new Intent(BROADCAST_MUSIC_PAUSE);
					context.sendBroadcast(intent);
					//btnBack.setEnabled(false);
				}
				MusicControllerView.this.callOnClick();
				break;
			case R.id.btn_music_next:
				if(musicService.nextPlay()){
					btnPlay.setBackgroundResource(resPause);
					Intent intent = new Intent(BROADCAST_MUSIC_PLAY);
					context.sendBroadcast(intent);
					/*btnBack.setEnabled(true);
					
					if(musicService.getIsFirstOrLast()){
						btnNext.setEnabled(false);
					}*/
				}else{
					btnPlay.setBackgroundResource(resPlay);
					Intent intent = new Intent(BROADCAST_MUSIC_PAUSE);
					context.sendBroadcast(intent);
					//btnNext.setEnabled(false);
				}
				MusicControllerView.this.callOnClick();
				break;
			default:
				break;
				
			}
		}
	};
	
	
	private void init() {
		inflater = LayoutInflater.from(getContext());
		View v;
		v = inflater.inflate(R.layout.view_music_player_controller, null);

		btnBack = (Button) v.findViewById(R.id.btn_music_forward);
		btnNext = (Button) v.findViewById(R.id.btn_music_next);
		btnPlay = (Button) v.findViewById(R.id.btn_music_play_pause);
		//btnBack.setEnabled(false);
		btnBack.setOnClickListener(listener);
		btnNext.setOnClickListener(listener);
		btnPlay.setOnClickListener(listener);
		addView(v);
				
	}

	public int setDrawableAndGetWidth(Boolean isbig){
		isBigView = isbig;
		if(isbig){
			btnBack.setBackgroundResource(R.drawable.btn_music_big_back);
			btnNext.setBackgroundResource(R.drawable.btn_music_big_next);
			btnPlay.setBackgroundResource(R.drawable.btn_music_big_play);
			return DisplayUtil.getSize(context, 426);
		}else{
			btnBack.setBackgroundResource(R.drawable.btn_music_back);
			btnNext.setBackgroundResource(R.drawable.btn_music_next);
			btnPlay.setBackgroundResource(R.drawable.btn_music_play);
			return DisplayUtil.getSize(context, 306);
		}
	}
	
	public void setIsList(Boolean islist){
		isList = islist;
		if(!islist){
			btnNext.setEnabled(false);
			btnBack.setEnabled(false);
		}
		
	}
	
	public void setPathArray(List<GsonResponseObject.MusicElem> pathArray){
//		pathArrayList = pathArray;
		MusicService.getInstance().stopPlay();
		MusicService.getInstance().setPlayArray(pathArray);
	}
	
	@Override
	protected void onDetachedFromWindow() {
		// TODO Auto-generated method stub
		MusicService musicService = MusicService.getInstance();
		if(null != musicService) musicService.setIsPlayed(false);
		super.onDetachedFromWindow();
	}
}
