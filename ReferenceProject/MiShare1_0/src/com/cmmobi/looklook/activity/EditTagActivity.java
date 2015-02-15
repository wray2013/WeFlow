package com.cmmobi.looklook.activity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.os.Message;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ToggleButton;
import cn.zipper.framwork.core.ZActivity;
import cn.zipper.framwork.core.ZApplication;
import cn.zipper.framwork.utils.ZGraphics;

import com.cmmobi.looklook.R;
import com.cmmobi.looklook.common.gson.GsonResponse3.MyDiary;
import com.cmmobi.looklook.common.gson.GsonResponse3.TAG;
import com.cmmobi.looklook.common.gson.GsonResponse3.taglistItem;
import com.cmmobi.looklook.common.listener.MyWaveformListener;
import com.cmmobi.looklook.common.utils.BitmapUtils;
import com.cmmobi.looklook.common.utils.CmmobiClickAgentWrapper;
import com.cmmobi.looklook.common.web.TackView;
import com.cmmobi.looklook.info.profile.AccountInfo;
import com.cmmobi.looklook.info.profile.ActiveAccount;
import com.cmmobi.looklook.info.profile.DiaryManager;
import com.cmmobi.looklook.info.profile.MediaValue;
import com.cmmobi.looklook.offlinetask.OfflineTaskManager;
import com.cmmobi.looklook.prompt.Prompt;
import com.cmmobivideo.utils.EffectUtils;
import com.cmmobivideo.utils.PluginUtils;
import com.cmmobivideo.utils.XUtils;
import com.cmmobivideo.workers.XMediaPlayer;
import com.cmmobivideo.workers.XMediaPlayer.OnInfoListener;
import com.cmmobivideo.workers.XMediaPlayer.XPlayerWaveformListener;
import com.google.gson.Gson;

import effect.XEffectDefine;
import effect.XEffectMediaPlayer;
import effect.XEffects;

/**
 * 信息编辑（各种日记的信息编辑界面）
 * @author wangjm
 *
 */
public class EditTagActivity extends ZActivity {
	
	private final String TAG = "EditTagActivity";
	
	private FrameLayout contentLayout = null;
	private DisplayMetrics dm = new DisplayMetrics();
	private ToggleButton toggleBtn = null;
	private TextView tvPosition = null;
	private ImageView ivWeather = null;
	private boolean isPositionVisiable = true;
	private List<TAG> tagList = new ArrayList<TAG>();
	private ArrayList<String> tagIds = new ArrayList<String>();
	private List<String> tagStrings = new ArrayList<String>();
	private int tagNum = 0;
	private ImageView ivDone = null;
	private ImageView ivBack = null;
	private TextView tvTag1 = null;
	private TextView tvTag2 = null;
	private TextView tvTag3 = null;
	private TextView tvTag4 = null;
	public static final int TAGSELECTED = 0x0013;
	private String userID = "";
	private String diaryUUID = "";
	private MyDiary myDiary = null;
	private FrameLayout videoLayout = null;
	private ImageView photoView = null;
	private RelativeLayout noteLayout = null;
	private RelativeLayout audioLayout = null;
	
	// 视频
	private RelativeLayout rlVideoContent = null;
	private XMediaPlayer mMediaPlayer = null;
	private XEffects mEffects = null;
	public EffectUtils effectUtils;
	private String videoPath = null;
	private String audioPath = null;
	private ImageView ivPlay = null;
	private boolean isPlayerNeedToPlay;
	private boolean isPlayerPrepared;
	private ImageView ivVideoCover = null;
	private RelativeLayout waveLayout = null;
	private Bitmap videoCoverBmp = null;
	
	// 图片
	private String picPath = "";
	private Bitmap originBitmap = null;
	
	// 便签
	private String textContent = "";
	private String noteAudioPath = "";
	private TackView tackView = null;
	private int[] SOUND_ICONS = {R.drawable.yuyinbiaoqian_3,R.drawable.yuyinbiaoqian_4,R.drawable.yuyinbiaoqian_2};
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_edit_diary_info);
		
		userID =  ActiveAccount.getInstance(ZApplication.getInstance()).getUID();
		String diaryString = getIntent().getStringExtra("diarystring");
		myDiary = new Gson().fromJson(diaryString,MyDiary.class);
				
		if (myDiary.tags != null) {
			tagList = Arrays.asList(myDiary.tags);
			for (TAG tag:tagList) {
				tagIds.add(tag.id);
			}
		}
		
		ivDone = (ImageView) findViewById(R.id.iv_edit_diary_save);
		ivBack = (ImageView) findViewById(R.id.iv_edit_diary_back);
		
		ivDone.setOnClickListener(this);
		ivDone.setEnabled(false);
		ivBack.setOnClickListener(this);
		
		videoLayout = (FrameLayout) findViewById(R.id.fl_edit_diary_video);
		photoView = (ImageView) findViewById(R.id.iv_edit_diary_photo);
		noteLayout = (RelativeLayout) findViewById(R.id.rl_edit_diary_note);
		audioLayout = (RelativeLayout) findViewById(R.id.rl_edit_diary_audio);
		
		String diaryType = myDiary.getDiaryMainType();
		if ("1".equals(diaryType)) {
			videoLayout.setVisibility(View.VISIBLE);
			photoView.setVisibility(View.GONE);
			noteLayout.setVisibility(View.GONE);
			audioLayout.setVisibility(View.GONE);
			loadVideoData();
		} else if ("2".equals(diaryType)) {
			videoLayout.setVisibility(View.GONE);
			audioLayout.setVisibility(View.VISIBLE);
			photoView.setVisibility(View.GONE);
			noteLayout.setVisibility(View.GONE);
			loadAudioData();
		} else if ("3".equals(diaryType)) {
			videoLayout.setVisibility(View.GONE);
			photoView.setVisibility(View.VISIBLE);
			noteLayout.setVisibility(View.GONE);
			audioLayout.setVisibility(View.GONE);
			loadPicture();
		} else if ("4".equals(diaryType) || "5".equals(diaryType) || "6".equals(diaryType)) {
			videoLayout.setVisibility(View.GONE);
			photoView.setVisibility(View.GONE);
			noteLayout.setVisibility(View.VISIBLE);
			audioLayout.setVisibility(View.GONE);
			loadNote();
		}
		
		contentLayout = (FrameLayout) findViewById(R.id.fl_edit_diary_content);
		dm = getResources().getDisplayMetrics();
		Log.d(TAG,"width = " + dm.widthPixels + " height = " + dm.heightPixels);
		RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(dm.widthPixels,dm.widthPixels);
		params.addRule(RelativeLayout.BELOW,R.id.rl_edit_diary_info_top);
		contentLayout.setLayoutParams(params);
		
		tvTag1 = (TextView) findViewById(R.id.tv_edit_diary_tag_1);
		tvTag2 = (TextView) findViewById(R.id.tv_edit_diary_tag_2);
		tvTag3 = (TextView) findViewById(R.id.tv_edit_diary_tag_3);
		tvTag4 = (TextView) findViewById(R.id.tv_edit_diary_tag_4);
		
		showTag(tagList);
	}
	
	private void loadVideoData() {
		rlVideoContent=(RelativeLayout) findViewById(R.id.video_view);
		rlVideoContent.setOnClickListener(this);
		
		effectUtils = new EffectUtils(this);
		if (PluginUtils.isPluginMounted()) {
			mEffects = new XEffects();
		}
		
		ivPlay = (ImageView) findViewById(R.id.iv_video_play);
		ivPlay.setOnClickListener(this);
		
		ivVideoCover = (ImageView) findViewById(R.id.iv_video_preview);
		
		
		videoPath = myDiary.getMainPath();
		Log.d(TAG,"videoPath = " + videoPath);
		if(null==mMediaPlayer) {
			mMediaPlayer = new XMediaPlayer(this, mEffects, true);
			mMediaPlayer.setUpdateTimePeriod(0.1f);
			mMediaPlayer.setListener(new MyOnInfoListener());
			rlVideoContent.addView((View)mMediaPlayer.getXSurfaceView());
			if (videoPath != null) {
//				mMediaPlayer.open(videoPath);
			}
		}
		
		final String videoCoverUrl = myDiary.getVideoCoverUrl();
		ViewTreeObserver vto2 = ivVideoCover.getViewTreeObserver();   
		vto2.addOnGlobalLayoutListener(new OnGlobalLayoutListener() { 
			@Override   
			public void onGlobalLayout() { 
				if (videoCoverUrl != null) {
					Log.d(TAG,"loadVideoData videoFrontCoverUrl = " + videoCoverUrl);
					MediaValue mediaValue = AccountInfo.getInstance(userID).mediamapping.getMedia(userID, videoCoverUrl);
					if (mediaValue != null) {
						ivVideoCover.setVisibility(View.VISIBLE);
				    	int height = ivVideoCover.getMeasuredHeight(); 
				    	int width  = ivVideoCover.getMeasuredWidth(); 
				    	Log.d(TAG,"measuredwidth = " + width + " measuredheight = " + height);
				    	videoCoverBmp = BitmapUtils.readBitmapAutoSize(LookLookActivity.SDCARD_PATH + mediaValue.localpath, width, height);
						if (videoCoverBmp != null) {
							Log.d(TAG,"width = " + videoCoverBmp.getWidth() + " height = " + videoCoverBmp.getHeight());
						}
					} else {
						ivVideoCover.setImageBitmap(null);
					}
				} 
				
				if (videoCoverBmp != null) {
					ivVideoCover.setImageBitmap(videoCoverBmp);
					ivVideoCover.setVisibility(View.VISIBLE);
				}
				ivVideoCover.getViewTreeObserver().removeGlobalOnLayoutListener(this);         
			}   
		}); 
	}
	
	private void loadAudioData() {
		audioPath = myDiary.getMainPath();
		effectUtils = new EffectUtils(this);
		if (PluginUtils.isPluginMounted()) {
			mEffects = new XEffects();
		}
		
		ivPlay = (ImageView) findViewById(R.id.iv_play);
		ivPlay.setOnClickListener(this);
		waveLayout = (RelativeLayout) findViewById(R.id.rl_wave_layout);
		
		if(null==mMediaPlayer) {
			mMediaPlayer = new XMediaPlayer(this, mEffects, true);
			mMediaPlayer.setEnableWaveform(true,XEffectDefine.JAVA_A_PLAY_WAVEFORM);//启用波形图
			mMediaPlayer.setWaveformListener(new MyWaveformListener(mMediaPlayer,waveLayout));
			mMediaPlayer.setUpdateTimePeriod(0.1f);
			mMediaPlayer.setListener(new MyAudioOnInfoListener());
			if (audioPath != null) {
				mMediaPlayer.open(audioPath);
			}
		}
	}
	
	private class MyOnInfoListener implements OnInfoListener {

		@Override
		public void onStartPlayer(XMediaPlayer player) {
			Log.e(TAG, "onStartPlayer");
			ivPlay.setImageResource(R.drawable.zanting);
			ivVideoCover.setVisibility(View.GONE);
		}

		@Override
		public void onStopPlayer(XMediaPlayer player) {
			Log.e(TAG, "onStopPlayer");
			ivPlay.setImageResource(R.drawable.play);
		}

		@Override
		public void OnFinishPlayer(XMediaPlayer player) {
			Log.e(TAG, "OnFinishPlayer");
			ivPlay.setImageResource(R.drawable.play);
			onClick(rlVideoContent);
			ivVideoCover.setVisibility(View.VISIBLE);
		}

		@Override
		public void onUpdateTime(XMediaPlayer player, double time) {
		}

		@Override
		public void onSurfaceCreated(XMediaPlayer player) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onPreparedPlayer(XMediaPlayer player) {
			isPlayerPrepared = true;
			if (isPlayerNeedToPlay) {
				player.play();
			} 
		}

		@Override
		public void onVideoSizeChanged(XMediaPlayer player, int w, int h) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onError(XMediaPlayer player, int what, int extra) {
			// TODO Auto-generated method stub
			
		}
	}
	
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		CmmobiClickAgentWrapper.onPause(this);
		super.onPause();
	}
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		CmmobiClickAgentWrapper.onResume(this);
		super.onResume();
	}
	
	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		CmmobiClickAgentWrapper.onStop(this);
		super.onStop();
	}
	
	private class MyAudioOnInfoListener implements OnInfoListener {

		@Override
		public void onStartPlayer(XMediaPlayer player) {
			Log.e(TAG, "onStartPlayer");
			ivPlay.setImageResource(R.drawable.yinpin_zanting);
		}

		@Override
		public void onStopPlayer(XMediaPlayer player) {
			Log.e(TAG, "onStopPlayer");
			ivPlay.setImageResource(R.drawable.yinpin_play);
		}

		@Override
		public void OnFinishPlayer(XMediaPlayer player) {
			Log.e(TAG, "OnFinishPlayer");
			ivPlay.setImageResource(R.drawable.yinpin_play);
		}

		@Override
		public void onUpdateTime(XMediaPlayer player, double time) {
		}

		@Override
		public void onSurfaceCreated(XMediaPlayer player) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onPreparedPlayer(XMediaPlayer player) {
			isPlayerPrepared = true;
			if (isPlayerNeedToPlay) {
				player.play();
			} 
		}

		@Override
		public void onVideoSizeChanged(XMediaPlayer player, int w, int h) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onError(XMediaPlayer player, int what, int extra) {
			// TODO Auto-generated method stub
			
		}
	}
	
	private void loadPicture() {
		picPath =  myDiary.getMainPath();
		
		ViewTreeObserver vto1 = photoView.getViewTreeObserver();   
		vto1.addOnGlobalLayoutListener(new OnGlobalLayoutListener() { 
			@Override   
			public void onGlobalLayout() {
				int height = photoView.getMeasuredHeight(); 
		    	int width  = photoView.getMeasuredWidth(); 
				originBitmap = BitmapUtils.readBitmapAutoSize(picPath, width, height);
				originBitmap = ZGraphics.rotate(originBitmap, XUtils.getExifOrientation(picPath), true);
				if (originBitmap == null) {
					Prompt.Alert(EditTagActivity.this, "图片不存在");
					EditTagActivity.this.finish();
					return;
				}
				photoView.setImageBitmap(originBitmap);
				photoView.getViewTreeObserver().removeGlobalOnLayoutListener(this);
			}   
		});
	}
	
	private void loadNote() {
		tackView = (TackView) findViewById(R.id.ll_tackview);
		
		textContent = myDiary.getMainTextContent();
		noteAudioPath = myDiary.getMainPath();
		
		TextView contentView = (TextView) findViewById(R.id.tv_text_content);
		contentView.setText(textContent);
		
		if (noteAudioPath != null) {
			tackView.setVisibility(View.VISIBLE);
			tackView.setLocalAudio(noteAudioPath);
			tackView.setBackground(R.drawable.btn_yuyinbiaoqian);
			tackView.setSoundIcons(SOUND_ICONS);
			tackView.setOnClickListener(this);
		} else {
			tackView.setVisibility(View.GONE);
		}
	}
	
	private void showTag(List<TAG> list) {
		List<String> tagStrList = new ArrayList<String>();
		if (list == null) {
			showTag();
		} else {
			for (TAG tag:list) {
				tagStrList.add(tag.name);
			}
			showTag(tagStrList.toArray(new String[tagStrList.size()]));
		}
	}
	
	private void showTag(String ...tags) {
		tvTag1.setOnClickListener(null);
		tvTag2.setOnClickListener(null);
		tvTag3.setOnClickListener(null);
		tvTag4.setOnClickListener(null);
		if (tags == null || tags.length == 0) {
			tvTag1.setText("+");
			tvTag2.setVisibility(View.GONE);
			tvTag3.setVisibility(View.GONE);
			tvTag4.setVisibility(View.GONE);
			tvTag1.setOnClickListener(this);
			return;
		}
		int tagsLen = tags.length;
		
		if (tagsLen == 1) {
			tvTag1.setText(tags[0]);
			tvTag2.setText("+");
			tvTag2.setVisibility(View.VISIBLE);
			tvTag3.setVisibility(View.GONE);
			tvTag4.setVisibility(View.GONE);
			tvTag2.setOnClickListener(this);
		}
		
		if (tagsLen == 2) {
			tvTag1.setText(tags[0]);
			tvTag2.setText(tags[1]);
			tvTag2.setVisibility(View.VISIBLE);
			tvTag3.setText("+");
			tvTag3.setVisibility(View.VISIBLE);
			tvTag4.setVisibility(View.GONE);
			tvTag3.setOnClickListener(this);
		}
		
		if (tagsLen == 3) {
			tvTag1.setText(tags[0]);
			tvTag2.setText(tags[1]);
			tvTag2.setVisibility(View.VISIBLE);
			tvTag3.setText(tags[2]);
			tvTag3.setVisibility(View.VISIBLE);
			tvTag4.setText("+");
			tvTag4.setVisibility(View.VISIBLE);
			tvTag4.setOnClickListener(this);
		}
	}

	@Override
	public boolean handleMessage(Message arg0) {
		// TODO Auto-generated method stub
		return false;
	}
	
	public static TAG[] getDiaryTag(List<String> tagIdsList) {
		if (tagIdsList == null || tagIdsList.size() == 0) {
			return null;
		}
		DiaryManager diarymanager = DiaryManager.getInstance();
		List<taglistItem> tagStrList = diarymanager.getTags();
		ArrayList<TAG> tagList = new ArrayList<TAG>();
		for (String id:tagIdsList) {
			for (taglistItem item:tagStrList) {
				if (id.equals(item.id)) {
					TAG tag = new TAG();
					tag.id = item.id;
					tag.name = item.name;
					tagList.add(tag);
				}
			}
		}
		if (tagList.size() == 0) {
			return null;
		}
		
		return tagList.toArray(new TAG[tagList.size()]);
	}

	@Override
	public void onClick(View view) {
		// TODO Auto-generated method stub
		switch(view.getId()) {
		case R.id.tv_edit_diary_tag_1:
		case R.id.tv_edit_diary_tag_2:
		case R.id.tv_edit_diary_tag_3:
		case R.id.tv_edit_diary_tag_4:
			Intent intent = new Intent();
			intent.putStringArrayListExtra("tagids", tagIds);
			intent.setClass(this, TagSelectedActivity.class);
			startActivityForResult(intent, TAGSELECTED);
			break;
		case R.id.iv_video_play:
			int status = mMediaPlayer.getStatus();
			if (status == XEffectMediaPlayer.STATUS_UNKNOW || status == XEffectMediaPlayer.STATUS_STOP || status == XEffectMediaPlayer.STATUS_OPENED) {
				if (status == XEffectMediaPlayer.STATUS_UNKNOW) {
					if (videoPath != null) {
						isPlayerPrepared = false;
						mMediaPlayer.open(videoPath);
					}
				}
				if (isPlayerPrepared) {
					mMediaPlayer.play();
					hidePlayBtn();
					ivVideoCover.setVisibility(View.GONE);
				} else {
					isPlayerNeedToPlay = true;
				}
			} else if (mMediaPlayer.getStatus() == XEffectMediaPlayer.STATUS_PALYING) {
				mMediaPlayer.pause();
				ivPlay.setImageResource(R.drawable.play);
				hidePlayBtn();
			} else if (mMediaPlayer.getStatus() == XEffectMediaPlayer.STATUS_PAUSE) {
				mMediaPlayer.resume();
				ivPlay.setImageResource(R.drawable.zanting);
				hidePlayBtn();
			}
			break;
		case R.id.iv_play:
			int statu = mMediaPlayer.getStatus();
			if (statu == XEffectMediaPlayer.STATUS_UNKNOW || statu == XEffectMediaPlayer.STATUS_STOP || statu == XEffectMediaPlayer.STATUS_OPENED) {
				if (statu == XEffectMediaPlayer.STATUS_UNKNOW) {
					if (audioPath != null) {
						isPlayerPrepared = false;
						mMediaPlayer.open(audioPath);
					}
				}
				if (isPlayerPrepared) {
					mMediaPlayer.play();
				} else {
					isPlayerNeedToPlay = true;
				}
			} else if (mMediaPlayer.getStatus() == XEffectMediaPlayer.STATUS_PALYING) {
				mMediaPlayer.pause();
				ivPlay.setImageResource(R.drawable.yinpin_play);
			} else if (mMediaPlayer.getStatus() == XEffectMediaPlayer.STATUS_PAUSE) {
				mMediaPlayer.resume();
				ivPlay.setImageResource(R.drawable.yinpin_zanting);
			}
			break;
		case R.id.video_view:
			ivPlay.setVisibility(View.VISIBLE);
			hidePlayBtn();
			break;
		case R.id.ll_tackview:
			tackView.playAudio(noteAudioPath);
			break;
		case R.id.iv_edit_diary_save:
		{
			myDiary.tags = getDiaryTag(tagIds);
			String diaryString = new Gson().toJson(myDiary);
			Intent tagIntent = new Intent();
			tagIntent.putExtra(DiaryEditPreviewActivity.INTENT_ACTION_DIARY_STRING, diaryString);
			setResult(RESULT_OK, tagIntent);
//			OfflineTaskManager.getInstance().addPositionOrTagTask("", diaryUUID, getTagIdString(tagIds), "");
			finish();
			break;
		}
		case R.id.iv_edit_diary_back:
			finish();
			break;
		}
	}
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		Log.d(TAG,"onDestory  in");
		super.onDestroy();
		try {
			if(mMediaPlayer!=null){
				mMediaPlayer.release();
				mEffects.release();
				mMediaPlayer=null;
				mEffects = null;
				isPlayerNeedToPlay = false;
				isPlayerPrepared = false;
			}
			BitmapUtils.releaseBmp(videoCoverBmp);
			BitmapUtils.releaseBmp(originBitmap);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	public static String getTagIdString(List<String> tagStrList) {
		String ret = "";
		for (String tagStr:tagStrList) {
			ret += tagStr + ",";
		}
		if (ret.endsWith(",")) {
			ret = ret.substring(0, ret.length() - 1);
		}
		return ret;
	}
	
	private void hidePlayBtn() {
		handler.removeCallbacks(hidePlayBtn);
		handler.postDelayed(hidePlayBtn, 2000);
	}
	
	Runnable hidePlayBtn = new Runnable() {
		
		@Override
		public void run() {
			// TODO Auto-generated method stub
			ivPlay.setVisibility(View.GONE);
		}
	};
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		if (requestCode == TAGSELECTED && data != null) {
			
			tagStrings = data.getStringArrayListExtra("tagstrings");
			tagIds = data.getStringArrayListExtra("tagids");
			if (getIdStrs(tagIds).equals(myDiary.getTagIds())) {
				ivDone.setEnabled(false);
			} else {
				ivDone.setEnabled(true);
			}
			Log.d(TAG,"onActivityResult in tagString = " + tagStrings);
			if (tagStrings != null) {
				showTag(tagStrings.toArray(new String[tagStrings.size()]));
			}
			
			
		}
		super.onActivityResult(requestCode, resultCode, data);
	}
	
	private static String getIdStrs(List<String> tagLst) {
		String retStr = "";
		if (tagLst != null) {
			for(String tag:tagLst) {
				retStr += tag + ",";
			}
		}
		if (!"".equals(retStr)) {
			retStr = retStr.substring(0, retStr.length() - 1);
		}
		return retStr;
	}

}
