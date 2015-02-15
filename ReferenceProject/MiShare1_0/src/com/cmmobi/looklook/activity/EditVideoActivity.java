package com.cmmobi.looklook.activity;

import java.util.Iterator;
import java.util.LinkedList;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Environment;
import android.os.Message;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;
import cn.zipper.framwork.core.ZActivity;
import cn.zipper.framwork.core.ZApplication;
import cn.zipper.framwork.core.ZDialog;
import cn.zipper.framwork.io.file.ZFileSystem;
import cn.zipper.framwork.utils.ZGraphics;

import com.cmmobi.looklook.R;
import com.cmmobi.looklook.common.adapter.ImageAdapter;
import com.cmmobi.looklook.common.constant.Constant;
import com.cmmobi.looklook.common.gson.DiaryController;
import com.cmmobi.looklook.common.gson.GsonResponse3.MyDiary;
import com.cmmobi.looklook.common.utils.BitmapUtils;
import com.cmmobi.looklook.common.utils.CmmobiClickAgentWrapper;
import com.cmmobi.looklook.common.utils.DiaryEditNote;
import com.cmmobi.looklook.common.view.VideoMontageView;
import com.cmmobi.looklook.common.view.VideoMontageView.VideoMontageChangeListener;
import com.cmmobi.looklook.info.profile.ActiveAccount;
import com.cmmobi.statistics.CmmobiClickAgent;
import com.cmmobivideo.utils.Mp4InfoUtils;
import com.cmmobivideo.utils.PluginUtils;
import com.cmmobivideo.workers.XMediaPlayer;
import com.cmmobivideo.workers.XMediaPlayer.OnInfoListener;
import com.google.gson.Gson;

import effect.EffectType;
import effect.Mp4Info;
import effect.XEffectMediaPlayer;
import effect.XEffects;
import effect.XGLLayer;
import effect.XMp4Box;

public class EditVideoActivity extends ZActivity implements VideoMontageChangeListener {
	
	private final String TAG = "EditVideoActivity";
	
	private FrameLayout videoLayout = null;
	private DisplayMetrics dm = new DisplayMetrics();
	private RelativeLayout rlVideoContent = null;
	private XMediaPlayer mMediaPlayer = null;
	private XEffects mEffects = null;
	private String videoPath = null;
	private ImageView ivPlay = null;
	private boolean isPlayerNeedToPlay;
	private boolean isPlayerPrepared;
//	private String diaryUUID = null;
	private MyDiary myDiary = null;
	private String userID = null;
	private VideoMontageView montageView = null;
	private GridView gridView = null;
	private ImageAdapter adapter = null;
	private double videoDuration = 0;
	private int montageWidth = 0;
	private int seekWidth = 0;
	private double startMontageTime = 0;
	private double endMontageTime = 0;
	private double videoCoverTime = 0;
	private ImageView montageSave = null;
	private ImageView montageDelete = null;
	private ImageView ivBack = null;
	private ImageView ivDone = null;
	private ImageView ivUndo = null;
	
	private ImageView ivCover = null;
	private ImageView ivCoverSetted = null;
	private RelativeLayout flCoverSetted = null;
	
	private final int COVER_NUM = 16;
	public Bitmap[] mBmps = new Bitmap[COVER_NUM];
	private final int OPERATE_CLIP = 0;// 裁剪
	private final int OPERATE_ROTATE = 1;// 旋转
	private final int OPERATE_COVER = 2;// 封面
	
	private ImageView ivVideoCover = null;
	private Bitmap videoCoverBmp = null;
	private Bitmap thumbBmp = null;
	private String videoCoverPath = null;
	private DiaryEditNote diaryEditNote = null;
	private String curOriginalVideoPath = null;
	
	private class OperateNote {
		int operateType;
		String videoPath;// 视频路径
		int angle;// 角度
		double coverTime;// 封面时间
	}
	
	private String curVideoPath = "";
	private int curAngle = 0;
	private double curCoverTime = 0;
	private double curVideoDuration = 0;
	private LinkedList<OperateNote> operateNotesList = new LinkedList<OperateNote>();
	private LinkedList<String> originalPathList = new LinkedList<String>();

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_edit_video);
		
		videoLayout = (FrameLayout) findViewById(R.id.fl_edit_diary_video);
		dm = getResources().getDisplayMetrics();
		Log.d(TAG,"width = " + dm.widthPixels + " height = " + dm.heightPixels);
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(dm.widthPixels,dm.widthPixels);
		videoLayout.setLayoutParams(params);
		
		montageView = (VideoMontageView) findViewById(R.id.vv_edit_video_montageview);
		gridView = (GridView) findViewById(R.id.gv_edit_video_montage_imgs);
		adapter = new ImageAdapter(this);
		gridView.setAdapter(adapter);
		
		ivVideoCover = (ImageView) findViewById(R.id.iv_video_preview);
		
		loadVideoData();
		
		videoCoverPath = myDiary.getVideoCoverPath();
		ViewTreeObserver vto2 = ivVideoCover.getViewTreeObserver();   
		vto2.addOnGlobalLayoutListener(new OnGlobalLayoutListener() { 
			@Override   
			public void onGlobalLayout() { 
				if (videoCoverPath != null) {
					Log.d(TAG,"loadVideoData videoFrontCoverPath = " + videoCoverPath + " url = " + myDiary.getVideoCoverUrl());
					ivVideoCover.setVisibility(View.VISIBLE);
			    	int height = ivVideoCover.getMeasuredHeight(); 
			    	int width  = ivVideoCover.getMeasuredWidth(); 
			    	Log.d(TAG,"measuredwidth = " + width + " measuredheight = " + height);
			    	videoCoverBmp = BitmapUtils.readBitmapAutoSize(videoCoverPath, width, height);
					if (videoCoverBmp != null) {
						Log.d(TAG,"width = " + videoCoverBmp.getWidth() + " height = " + videoCoverBmp.getHeight());
					}
				} else {
					ivVideoCover.setImageBitmap(null);
				}
				
				if (videoCoverBmp != null) {
					curAngle = mMediaPlayer.getCurrentOrientation();
					ivVideoCover.setImageBitmap(BitmapUtils.rotate(videoCoverBmp, (360-curAngle)%360, true));
					ivVideoCover.setVisibility(View.VISIBLE);
				}
				ivVideoCover.getViewTreeObserver().removeGlobalOnLayoutListener(this);         
			}   
		}); 
		
		ivPlay = (ImageView) findViewById(R.id.iv_video_play);
		ivPlay.setOnClickListener(this);
		findViewById(R.id.fl_edit_diary_video).setOnClickListener(this);
		montageSave = (ImageView) findViewById(R.id.iv_edit_video_save);
		montageDelete = (ImageView) findViewById(R.id.iv_edit_video_delete);
		montageSave.setOnClickListener(this);
		montageDelete.setOnClickListener(this);
		
		ivBack = (ImageView) findViewById(R.id.iv_edit_diary_back);
		ivDone = (ImageView) findViewById(R.id.iv_edit_diary_save);
		ivUndo = (ImageView) findViewById(R.id.iv_edit_diary_undo);
		
		ivCover = (ImageView) findViewById(R.id.iv_edit_video_cover);
		flCoverSetted = (RelativeLayout) findViewById(R.id.fl_edit_video_cover_setted);
		ivCoverSetted = (ImageView) findViewById(R.id.iv_edit_video_cover_setted);
		
		findViewById(R.id.iv_edit_video_rotate).setOnClickListener(this);
		
		ivCover.setOnClickListener(this);
		flCoverSetted.setOnClickListener(this);
		flCoverSetted.setVisibility(View.GONE);
		
		ivBack.setOnClickListener(this);
		ivDone.setOnClickListener(this);
		ivUndo.setOnClickListener(this);
		ivUndo.setEnabled(false);
		ivDone.setEnabled(false);
		
		setMontageEnable(false);
	}
	
	private void loadVideoData() {
		rlVideoContent=(RelativeLayout) findViewById(R.id.video_view);
//		rlVideoContent.setOnClickListener(this);
		
		if (PluginUtils.isPluginMounted()) {
			mEffects = new XEffects();
		}
		
		userID =  ActiveAccount.getInstance(ZApplication.getInstance()).getUID();
		String diaryString = getIntent().getStringExtra("diarystring");
		myDiary = new Gson().fromJson(diaryString,MyDiary.class);
		String diaryEditString = getIntent().getStringExtra(DiaryEditPreviewActivity.INTENT_ACTION_EDIT_NOTELIST);
		diaryEditNote = new Gson().fromJson(diaryEditString, DiaryEditNote.class);
		curOriginalVideoPath = diaryEditNote.mediaPath;
//		diaryUUID = getIntent().getStringExtra("diaryuuid");
//		myDiary = DiaryManager.getInstance().findMyDiaryByUUID(diaryUUID);
//		String videoUrl = myDiary.getMainUrl();
//		
//		MediaValue mediaValue = AccountInfo.getInstance(userID).mediamapping.getMedia(userID, videoUrl);
//		if (mediaValue != null) {
//			videoPath =  Environment.getExternalStorageDirectory() + mediaValue.path;
//		}
		videoPath = myDiary.getMainPath();
		Log.d(TAG,"videoPath = " + videoPath);
		videoDuration = new Mp4InfoUtils(videoPath).totaltime;
		curVideoDuration = videoDuration;
		curVideoPath = videoPath;
		
		startMontageTime = 0;
		endMontageTime = videoDuration;
		montageView.setTotalTime(videoDuration);
		montageView.setVideoMontageChangeListener(this);
		//播放时间
		if(null==mMediaPlayer) {
			mMediaPlayer = new XMediaPlayer(this, mEffects, true);
			mMediaPlayer.setUpdateTimePeriod(0.1f);
			mMediaPlayer.setListener(new MyOnInfoListener());
			rlVideoContent.addView((View)mMediaPlayer.getXSurfaceView());
			if (videoPath != null) {
				mMediaPlayer.open(videoPath);
			}
		}
		generateThumbsCovers();
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
	
	private double lastTime = 0.0;
	
	private class MyOnInfoListener implements OnInfoListener {

		@Override
		public void onStartPlayer(XMediaPlayer player) {
			Log.e(TAG, "onStartPlayer");
			ivVideoCover.setVisibility(View.GONE);
			ivPlay.setVisibility(View.INVISIBLE);
//			ivPlay.setImageResource(R.drawable.zanting);
		}

		@Override
		public void onStopPlayer(XMediaPlayer player) {
			Log.e(TAG, "onStopPlayer");
//			ivPlay.setImageResource(R.drawable.play);
			ivPlay.setVisibility(View.VISIBLE);
		}

		@Override
		public void OnFinishPlayer(XMediaPlayer player) {
			Log.e(TAG, "OnFinishPlayer");
			ivPlay.setVisibility(View.VISIBLE);
//			ivPlay.setImageResource(R.drawable.play);
			ivVideoCover.setVisibility(View.VISIBLE);
			if (mMediaPlayer != null) {
				curAngle = mMediaPlayer.getCurrentOrientation();
			}
			ivVideoCover.setImageBitmap(BitmapUtils.rotate(videoCoverBmp, (360-curAngle)%360, true));
//			onClick(rlVideoContent);
		}
		
		

		@Override
		public void onUpdateTime(final XMediaPlayer player, double time) {
			final double time1 = time;
			runOnUiThread(new Runnable() {
				
				@Override
				public void run() {
					// TODO Auto-generated method stub
					double time2 = time1;
					if (player.getStatus() == XEffectMediaPlayer.STATUS_STOP) {
						time2 = startMontageTime;
					}
					if (time2 >= endMontageTime) {
						mMediaPlayer.pause();
						mMediaPlayer.seek(startMontageTime);
						time2 = startMontageTime;
//						ivPlay.setImageResource(R.drawable.play);
						ivPlay.setVisibility(View.VISIBLE);
//						onClick(rlVideoContent);
					}
					
//					Log.d(TAG,"onUpdateTime startMontageTime = " + startMontageTime + " endMontageTime = " + endMontageTime + " time = " + time2);
					if (time2 >= startMontageTime && Math.abs(time2 - lastTime) > 0.05) {
						montageView.setCurTime(time2);
					}
					lastTime = time2;
				}
			});
			
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
	protected void onDestroy() {
		// TODO Auto-generated method stub
		Log.d(TAG,"onDestory in");
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
			BitmapUtils.releaseBmp(thumbBmp);
			BitmapUtils.releaseBmps(mBmps);
		} catch (Exception e) {
			e.printStackTrace();
		}
		super.onDestroy();
	}
	
	@Override
	public boolean handleMessage(Message arg0) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void onClick(View view) {
		// TODO Auto-generated method stub
		switch(view.getId()) {
		case R.id.fl_edit_diary_video:
		case R.id.iv_video_play:
			int status = mMediaPlayer.getStatus();
			if (status == XEffectMediaPlayer.STATUS_UNKNOW || status == XEffectMediaPlayer.STATUS_STOP || status == XEffectMediaPlayer.STATUS_OPENED) {
				if (status == XEffectMediaPlayer.STATUS_UNKNOW) {
					if (curVideoPath != null) {
						isPlayerPrepared = false;
						mMediaPlayer.open(curVideoPath);
					}
				}
				if (isPlayerPrepared) {
					mMediaPlayer.play();
//					hidePlayBtn();
					if (startMontageTime > 0.1) {
						mMediaPlayer.seek(startMontageTime);
					}
					ivVideoCover.setVisibility(View.GONE);
				} else {
					isPlayerNeedToPlay = true;
				}
			} else if (mMediaPlayer.getStatus() == XEffectMediaPlayer.STATUS_PALYING) {
				mMediaPlayer.pause();
//				ivPlay.setImageResource(R.drawable.play);
				ivPlay.setVisibility(View.VISIBLE);
				montageView.postInvalidate();
//				hidePlayBtn();
			} else if (mMediaPlayer.getStatus() == XEffectMediaPlayer.STATUS_PAUSE) {
				mMediaPlayer.resume();
				ivPlay.setVisibility(View.INVISIBLE);
//				ivPlay.setImageResource(R.drawable.zanting);
//				hidePlayBtn();
			}
			break;
		case R.id.iv_edit_video_save:
			montageVideo(curVideoPath, false);
			montageOriginalVideo(curOriginalVideoPath, false);
			// 2014-4-8
			CmmobiClickAgentWrapper.onEvent(this, "keep_selection",DiaryEditPreviewActivity.isFromShootting ? "2" : "1");
			break;
		case R.id.iv_edit_video_delete:
			montageVideo(curVideoPath, true);
			montageOriginalVideo(curOriginalVideoPath, true);
			// 2014-4-8
			CmmobiClickAgentWrapper.onEvent(this, "cut_selection",DiaryEditPreviewActivity.isFromShootting ? "2" : "1");
			break;
		case R.id.iv_edit_diary_back:
			deleteAllCacheFile();
			finish();
			break;
		case R.id.iv_edit_diary_save:
			modifyDiary();
			break;
		case R.id.iv_edit_diary_undo:
			processUndo();
			break;
		case R.id.fl_edit_video_cover_setted:
			flCoverSetted.setVisibility(View.GONE);
			ivCover.setVisibility(View.VISIBLE);
			thumbBmp = null;
			ivCoverSetted.setImageBitmap(null);
			removeCoverOperate();
			if (operateNotesList.size() == 0) {
				ivDone.setEnabled(false);
			}
			break;
		case R.id.iv_edit_video_cover:
			setVideoCover(mMediaPlayer.getCurrentTime());
			// 2014-4-8
			CmmobiClickAgentWrapper.onEvent(this, "cover",DiaryEditPreviewActivity.isFromShootting ? "2" : "1");
			break;
//		case R.id.video_view:
//			ivPlay.setVisibility(View.VISIBLE);
//			hidePlayBtn();
//			break;
		case R.id.iv_edit_video_rotate:
			changeRotation();
			// 2014-4-8
			CmmobiClickAgentWrapper.onEvent(this, "spin",DiaryEditPreviewActivity.isFromShootting ? "2" : "1");
			curAngle = mMediaPlayer.getCurrentOrientation();
			OperateNote operate = new OperateNote();
			operate.operateType = OPERATE_ROTATE;
			operate.videoPath = curVideoPath;
			operate.angle = curAngle;
			operate.coverTime = videoCoverTime;
			operateNotesList.add(operate);
			
			Bitmap bmp = BitmapUtils.rotate(thumbBmp, (360-curAngle)%360, true);
			ivCoverSetted.setImageBitmap(bmp);
			
//			if (ivVideoCover.getVisibility() == View.VISIBLE) {
				ivVideoCover.setImageBitmap(BitmapUtils.rotate(videoCoverBmp, (360-curAngle)%360, true));
//			}
			break;
		}
	}
	
	private void removeCoverOperate() {
		Iterator<OperateNote> iter = operateNotesList.iterator();
		while(iter.hasNext()) {
			OperateNote note = iter.next();
			if (note.operateType == OPERATE_COVER) {
				iter.remove();
			}
		}
	}
	
	/*private void hidePlayBtn() {
		handler.removeCallbacks(hidePlayBtn);
		handler.postDelayed(hidePlayBtn, 2000);
	}
	
	Runnable hidePlayBtn = new Runnable() {
		
		@Override
		public void run() {
			// TODO Auto-generated method stub
			ivPlay.setVisibility(View.GONE);
		}
	};*/
	
	private void processUndo() {
		if (operateNotesList.size() < 1) {
			ivUndo.setEnabled(false);
			return;
		}
		
		OperateNote operate = operateNotesList.removeLast();
		
		OperateNote secondLastOperate = null;
		if (operateNotesList.size() > 0) {
			secondLastOperate = operateNotesList.getLast();
		} else {
			ivUndo.setEnabled(false);
		}
		
		switch(operate.operateType) {
		case OPERATE_CLIP:
			if (secondLastOperate != null) {
				curVideoPath = secondLastOperate.videoPath;
				curVideoDuration = new Mp4InfoUtils(curVideoPath).totaltime;
				reloadVideo();
			} else {
				curVideoPath = videoPath;
				curVideoDuration = new Mp4InfoUtils(curVideoPath).totaltime;
				reloadVideo();
			}
			processOriginalUndo();
			break;
		case OPERATE_COVER:
			if (isCoverSetted()) {
				revocateVideoCover(secondLastOperate.coverTime);
			} else {
				flCoverSetted.setVisibility(View.GONE);
				ivCover.setVisibility(View.VISIBLE);
				ivCoverSetted.setImageBitmap(null);
				thumbBmp = null;
				videoCoverTime = 0;
			}
			break;
		case OPERATE_ROTATE:
			revocateRotate();
			break;
		}
		
		if (operate.videoPath != null && !operate.videoPath.equals(videoPath) && !operate.videoPath.equals(curVideoPath)) {
			ZFileSystem.delFile(operate.videoPath);
		}
	}
	
	private void processOriginalUndo() {
		if (videoPath.equals(diaryEditNote.mediaPath)) {
			return;
		}
		String originalPath = originalPathList.removeLast();
		
		String path = "";
		if (originalPathList.size() < 1) {
			path = diaryEditNote.mediaPath;
		} else {
			path = originalPathList.getLast();
		}
		curOriginalVideoPath = path;
		if (!originalPath.equals(curOriginalVideoPath) && originalPath.equals(diaryEditNote.mediaPath)) {
			ZFileSystem.delFile(originalPath);
		}
	}
	
	private void setVideoCover(double time) {
		
		ivDone.setEnabled(true);
		ivUndo.setEnabled(true);
		flCoverSetted.setVisibility(View.VISIBLE);
		ivCover.setVisibility(View.GONE);
		videoCoverTime = time;
		thumbBmp = mMediaPlayer.videScreenCapture(videoCoverTime, EffectType.EFFECT_VIDEO_DEFAULT_WIDTH, EffectType.EFFECT_VIDEO_DEFAULT_WIDTH);
		if(android.os.Build.MODEL.equalsIgnoreCase("GT-N7100")
				|| android.os.Build.MODEL.equalsIgnoreCase("GT-I9300")
				|| android.os.Build.MODEL.equals("GT-I9220")
				|| android.os.Build.MODEL.startsWith("GT-I9100")) {
			Mp4Info info = new Mp4Info(curVideoPath);
			thumbBmp = ZGraphics.rotate(thumbBmp, info.angle, true);
		}
		curAngle = mMediaPlayer.getCurrentOrientation();
		Log.d(TAG,"setVideoCover time = " + time + " curAngle = " + curAngle);
		Bitmap bmp = BitmapUtils.rotate(thumbBmp, (360-curAngle)%360, true);
		ivCoverSetted.setImageBitmap(bmp);
		
		OperateNote operate = new OperateNote();
		operate.operateType = OPERATE_COVER;
		operate.videoPath = curVideoPath;
		operate.angle = curAngle;
		operate.coverTime = videoCoverTime;
		operateNotesList.add(operate);
	}
	
	private void revocateVideoCover(double time) {
		flCoverSetted.setVisibility(View.VISIBLE);
		ivCover.setVisibility(View.GONE);
		videoCoverTime = time;
		thumbBmp = mMediaPlayer.videScreenCapture(videoCoverTime, EffectType.EFFECT_VIDEO_DEFAULT_WIDTH, EffectType.EFFECT_VIDEO_DEFAULT_WIDTH);
		curAngle = mMediaPlayer.getCurrentOrientation();
		Bitmap bmp = BitmapUtils.rotate(thumbBmp, (360-curAngle)%360, true);
		ivCoverSetted.setImageBitmap(bmp);
	}
	
	private boolean isCoverSetted() {
		for (OperateNote note:operateNotesList) {
			if (note.operateType == OPERATE_COVER) {
				return true;
			}
		}
		return false;
	}
	
	private boolean isVideoMontaged() {
		for (OperateNote note:operateNotesList) {
			if (note.operateType == OPERATE_CLIP) {
				return true;
			}
		}
		return false;
	}
	
	private boolean isVideoRolated() {
		for (OperateNote note:operateNotesList) {
			if (note.operateType == OPERATE_ROTATE) {
				return true;
			}
		}
		return false;
	}
	
	private void deleteCacheFile() {
		for(OperateNote note:operateNotesList) {
			if (note.videoPath != null && !note.videoPath.equals(videoPath) && !note.videoPath.equals(curVideoPath)) {
				ZFileSystem.delFile(note.videoPath);
			}
		}
		for (String path:originalPathList) {
			if (path != null && !path.equals(diaryEditNote.mediaPath) && path.equals(curOriginalVideoPath)) {
				ZFileSystem.delFile(path);
			}
		}
	}
	
	private void deleteAllCacheFile() {
		for(OperateNote note:operateNotesList) {
			if (note.videoPath != null && !note.videoPath.equals(videoPath)) {
				ZFileSystem.delFile(note.videoPath);
			}
		}
		
		for (String path:originalPathList) {
			if (path != null && !path.equals(diaryEditNote.mediaPath)) {
				ZFileSystem.delFile(path);
			}
		}
	}
	
	
	private void modifyDiary() {
		ZDialog.show(R.layout.progressdialog, false, true, this,true);
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				if (isVideoRolated()) {
					// 旋转操作
					String videoID = DiaryController.getNextUUID();
					String outVideoPath = DiaryController.getAbsolutePathUnderUserDir()  +  "/video/" + videoID + ".mp4" ;
					ZFileSystem.copy(curVideoPath, outVideoPath);
					mMediaPlayer.saveOrientation(outVideoPath);
					curVideoPath = outVideoPath;
				}
				final Intent intent = new Intent();
				
				// 删除缓存数据
				deleteCacheFile();
				
				if (!curVideoPath.equals(videoPath)) {
					myDiary.modifyMainAttach(curVideoPath, "");
					if (videoPath.endsWith(diaryEditNote.mediaPath)) {
						intent.putExtra(DiaryEditPreviewActivity.INTENT_ACTION_MEDIA_PATH, curVideoPath);
					} else {
						if (isVideoRolated()) {
							mMediaPlayer.saveOrientation(curOriginalVideoPath);
						}
						intent.putExtra(DiaryEditPreviewActivity.INTENT_ACTION_MEDIA_PATH, curOriginalVideoPath);
					}
				}
				
				if (isCoverSetted()) {
					if (ivCoverSetted != null) {
						BitmapDrawable mDrawable =  (BitmapDrawable) ivCoverSetted.getDrawable();
						if (mDrawable != null) {
							Bitmap image = mDrawable.getBitmap();
							String imageID = DiaryController.getNextUUID();
							String videoCoverPath = DiaryController.getAbsolutePathUnderUserDir()  +  "/pic/" + imageID + ".jpg";
							if (image != null) {
								if(BitmapUtils.saveBitmap2file(image, videoCoverPath)) {
									Log.d(TAG,"saveBitmap2file sucessed");
									myDiary.modifyVideoCover(videoCoverPath);
								}
							}
						}
					}
					// 设置封面
					/*Bitmap bmp = mMediaPlayer.videScreenCapture(videoCoverTime, EffectType.EFFECT_VIDEO_DEFAULT_WIDTH, EffectType.EFFECT_VIDEO_DEFAULT_WIDTH);
					
					if (bmp != null) {
						curAngle = mMediaPlayer.getCurrentOrientation();
						bmp = BitmapUtils.rotate(bmp, (360-curAngle)%360, true);
						String imageID = DiaryController.getNextUUID();
						String videoCoverPath = DiaryController.getAbsolutePathUnderUserDir()  +  "/pic/" + imageID + ".jpg";
						if(BitmapUtils.saveBitmap2file(bmp, videoCoverPath)) {
							Log.d(TAG,"saveBitmap2file sucessed");
						}
						
						myDiary.modifyVideoCover(videoCoverPath);
					} else {
						runOnUiThread(new Runnable() {
							
							@Override
							public void run() {
								// TODO Auto-generated method stub
								Toast.makeText(EditVideoActivity.this, "截屏失败", 1000).show();
							}
						});
						
					}*/
				}
				
				runOnUiThread(new Runnable() {
					
					@Override
					public void run() {
						// TODO Auto-generated method stub
						String diaryString = new Gson().toJson(myDiary);
						intent.putExtra(DiaryEditPreviewActivity.INTENT_ACTION_DIARY_STRING, diaryString);
						
						setResult(RESULT_OK, intent);
						ZDialog.dismiss();
						finish();
					}
				});
			}
		}).start();
		
	}
	
	private void montageVideo(String inputFile,boolean isSelectedDelete) {
		XMp4Box mMp4Box = new XMp4Box();
		String videoID = DiaryController.getNextUUID();
		String RECORD_FILE_DIR =  Constant.SD_STORAGE_ROOT + "/" + ActiveAccount.getInstance(this).getLookLookID() + "/video";
		String outputFile = Environment.getExternalStorageDirectory() + RECORD_FILE_DIR + "/" + videoID + ".mp4";
		if (isSelectedDelete) {
			String outputFile1 = Environment.getExternalStorageDirectory() + RECORD_FILE_DIR + "/" + videoID + "_temp1.mp4";
			int ret1 = mMp4Box.splitFile(inputFile,outputFile1,0,(float) startMontageTime);
			String outputFile2 = Environment.getExternalStorageDirectory() + RECORD_FILE_DIR + "/" + videoID + "_temp2.mp4";
			int ret2 = mMp4Box.splitFile(inputFile,outputFile2,(float)endMontageTime,(float)videoDuration);
			Log.d(TAG,"ret1 = " + ret1 + " ret2 = " + ret2 + " startTime = " + startMontageTime + " endTime = " + endMontageTime + " duration = " + videoDuration);
			Log.d(TAG,"len1 = " + (new Mp4InfoUtils(outputFile1).totaltime) + " len2 = " + (new Mp4InfoUtils(outputFile2).totaltime));
			mMp4Box.appendOpen(outputFile);
			if (ret1 == 0) {
				mMp4Box.appendFile(outputFile1);
				ZFileSystem.delFile(outputFile1);
			} 
			if (ret2 == 0) {
				mMp4Box.appendFile(outputFile2);
				ZFileSystem.delFile(outputFile2);
			}
			mMp4Box.appendClose();
		} else {
			int ret = mMp4Box.splitFile(inputFile,outputFile,(float)startMontageTime,(float)endMontageTime);//1s
			Log.d(TAG,"ret = " + ret + " startMontageTime = " + startMontageTime + " endMontageTime = " + endMontageTime);
		}
		
		if (isVideoRolated()) {
			mMediaPlayer.saveOrientation(outputFile);
		}
		
		ivDone.setEnabled(true);
		ivUndo.setEnabled(true);
		OperateNote operate = new OperateNote();
		operate.operateType = OPERATE_CLIP;
		operate.videoPath = outputFile;
		operate.angle = curAngle;
		operate.coverTime = videoCoverTime;
		curVideoPath = outputFile;
		curVideoDuration = new Mp4InfoUtils(curVideoPath).totaltime;
		Log.d(TAG,"curVideoPath = " + curVideoPath + " curVideoDuration = " + curVideoDuration + " inputFile = " + inputFile);
		operateNotesList.add(operate);
		
		reloadVideo();
	}
	
	private void  montageOriginalVideo(String inputFile,boolean isSelectedDelete) {
		if (videoPath.equals(diaryEditNote.mediaPath)) {
			return;
		}
		XMp4Box mMp4Box = new XMp4Box();
		String videoID = DiaryController.getNextUUID();
		String RECORD_FILE_DIR =  Constant.SD_STORAGE_ROOT + "/" + ActiveAccount.getInstance(this).getLookLookID() + "/video";
		String outputFile = Environment.getExternalStorageDirectory() + RECORD_FILE_DIR + "/" + videoID + ".mp4";
		if (isSelectedDelete) {
			String outputFile1 = Environment.getExternalStorageDirectory() + RECORD_FILE_DIR + "/" + videoID + "_temp1.mp4";
			int ret1 = mMp4Box.splitFile(inputFile,outputFile1,0,(float) startMontageTime);
			String outputFile2 = Environment.getExternalStorageDirectory() + RECORD_FILE_DIR + "/" + videoID + "_temp2.mp4";
			int ret2 = mMp4Box.splitFile(inputFile,outputFile2,(float)endMontageTime,(float)videoDuration);
			Log.d(TAG,"ret1 = " + ret1 + " ret2 = " + ret2 + " startTime = " + startMontageTime + " endTime = " + endMontageTime + " duration = " + videoDuration);
			Log.d(TAG,"len1 = " + (new Mp4InfoUtils(outputFile1).totaltime) + " len2 = " + (new Mp4InfoUtils(outputFile2).totaltime));
			mMp4Box.appendOpen(outputFile);
			if (ret1 == 0) {
				mMp4Box.appendFile(outputFile1);
				ZFileSystem.delFile(outputFile1);
			} 
			if (ret2 == 0) {
				mMp4Box.appendFile(outputFile2);
				ZFileSystem.delFile(outputFile2);
			}
			mMp4Box.appendClose();
		} else {
			int ret = mMp4Box.splitFile(inputFile,outputFile,(float)startMontageTime,(float)endMontageTime);//1s
			Log.d(TAG,"ret = " + ret + " startMontageTime = " + startMontageTime + " endMontageTime = " + endMontageTime);
		}
		originalPathList.add(outputFile);
		curOriginalVideoPath = outputFile;
		
	}
	
	public void changeRotation(){
//		if(glLayer == null)return;
//		int originalAngle = mMediaPlayer.getVideoAngle();
		int oritation = mMediaPlayer.getCurrentOrientation();
	   	Log.i(TAG, "rotation:"+oritation);
	   	int newOritation = XGLLayer.ORIENTATION_0;
	   	if(oritation ==  XGLLayer.ORIENTATION_0){
	   		newOritation = XGLLayer.ORIENTATION_270;
	   	}else if(oritation ==  XGLLayer.ORIENTATION_270){
	   		newOritation = XGLLayer.ORIENTATION_180;
		}else if(oritation ==  XGLLayer.ORIENTATION_180){
			newOritation = XGLLayer.ORIENTATION_90;
		}else if(oritation ==  XGLLayer.ORIENTATION_90){
			newOritation = XGLLayer.ORIENTATION_0;
		}
	   	mMediaPlayer.setCurrentOrientatin(newOritation);
	   	ivDone.setEnabled(true);
	   	ivUndo.setEnabled(true);
   }
	
	public void revocateRotate() {
		int oritation = mMediaPlayer.getCurrentOrientation();
	   	Log.i(TAG, "rotation:"+oritation);
	   	int newOritation = XGLLayer.ORIENTATION_0;
	   	if(oritation ==  XGLLayer.ORIENTATION_0){
	   		newOritation = XGLLayer.ORIENTATION_90;
	   	}else if(oritation ==  XGLLayer.ORIENTATION_270){
	   		newOritation = XGLLayer.ORIENTATION_0;
		}else if(oritation ==  XGLLayer.ORIENTATION_180){
			newOritation = XGLLayer.ORIENTATION_270;
		}else if(oritation ==  XGLLayer.ORIENTATION_90){
			newOritation = XGLLayer.ORIENTATION_180;
		}
	   	mMediaPlayer.setCurrentOrientatin(newOritation);
	   	
	   	curAngle = mMediaPlayer.getCurrentOrientation();
	   	Bitmap bmp = BitmapUtils.rotate(thumbBmp, (360-curAngle)%360, true);
		ivCoverSetted.setImageBitmap(bmp);
		
		ivVideoCover.setImageBitmap(BitmapUtils.rotate(videoCoverBmp, (360-curAngle)%360, true));
	}
	
	private void reloadVideo() {
		mMediaPlayer.stop();
		mMediaPlayer.open(curVideoPath);
		startMontageTime = 0;
		mMediaPlayer.seek(startMontageTime);
		endMontageTime = curVideoDuration;
		montageView.reset(curVideoDuration);
//		if (isVideoRolated()) {
//			mMediaPlayer.setCurrentOrientatin(curAngle);
//		}
		curAngle = mMediaPlayer.getCurrentOrientation();
		generateThumbsCovers();
	}
	
	public void generateThumbsCovers() {
		if (curVideoPath != null) {
			
			final double durationStep = curVideoDuration / (COVER_NUM - 1);
			Log.d(TAG,"durationStep = " + durationStep + " curVideoDuration = " + curVideoDuration);
			new Thread(new Runnable() {
				
				@Override
				public void run() {
					// TODO Auto-generated method stub
					for (int i = 0;i < COVER_NUM; i++) {
						mBmps[i] = getVideoCover(i * durationStep);
					}
					
					handler.post(new Runnable() {
						@Override
						public void run() {
							// TODO Auto-generated method stub
							adapter.setBitmaps(mBmps);
							adapter.notifyDataSetChanged();
						}
					});
				}
			}).start();
		}
	}
	
	public Bitmap getVideoCover(double time) {
		if (mMediaPlayer == null) {
			return null;
		}
		Bitmap bmp = mMediaPlayer.videScreenCapture(time,80,144);
		if (bmp == null) {
			if (Math.abs(time - curVideoDuration) < 0.1) {
				bmp = mMediaPlayer.videScreenCapture(time - 0.2,80,144);
			}
		}
		return bmp;
	}

	@Override
	public void videoMontageValueChanged(int Thumb1Value, int Thumb2Value) {
		// TODO Auto-generated method stub
		startMontageTime = calculateTime(Thumb1Value);
		endMontageTime = calculateTime(Thumb2Value);
		
//		Log.d(TAG,"thumb1Value = " + Thumb1Value + " thumb2Value = " + Thumb2Value + " startMontageTime = " + startMontageTime + " endMontageTime = " + endMontageTime);
	}
	

	@Override
	public void seekBarCropUp(int thumb1Value, int thumb2Value) {
		// TODO Auto-generated method stub
		startMontageTime = calculateTime(thumb1Value);
		endMontageTime = calculateTime(thumb2Value);
		double laveLen = startMontageTime + (curVideoDuration - endMontageTime);
		if (laveLen < VideoMontageView.SHORTEST_TIME) {
			montageDelete.setEnabled(false);
		} else {
			montageDelete.setEnabled(true);
		}
		mMediaPlayer.seek(startMontageTime);
		montageView.setCurTime(startMontageTime);
		Log.d(TAG,"seekBarCropUp  thumb1Value = " + thumb1Value + " thumb2Value = " + thumb2Value + " startMontageTime = " + startMontageTime + " endMontageTime = " + endMontageTime + " laveLen = " + laveLen);
	}

	@Override
	public void noticeWidth(int montageWidth, int seekWidth) {
		// TODO Auto-generated method stub
		Log.d(TAG,"montageWidth = " + montageWidth + " seekWidth = " + seekWidth);
		this.montageWidth = montageWidth;
		this.seekWidth = seekWidth;
	}
	
	private double calculateTime(int value) {
		return value * curVideoDuration / montageWidth;
	}

	@Override
	public void setMontageEnable(boolean enable) {
		// TODO Auto-generated method stub
		montageSave.setEnabled(enable);
		montageDelete.setEnabled(enable);
	}

	@Override
	public void seekBarChange(int curPos) {
		// TODO Auto-generated method stub
		double curTime = curPos * videoDuration / seekWidth;
		if (curTime < startMontageTime) {
			curTime = startMontageTime;
			montageView.setCurTime(curTime);
		}

		Log.d(TAG,"seekBarChange in curTime = " + curTime + " curPos = " + curPos + " seekWidth = " + seekWidth);
		if (mMediaPlayer != null) {
			int status = mMediaPlayer.getStatus();
			if (status == XEffectMediaPlayer.STATUS_UNKNOW || status == XEffectMediaPlayer.STATUS_STOP || status == XEffectMediaPlayer.STATUS_OPENED) {
				mMediaPlayer.play();
				mMediaPlayer.pause();
//				ivPlay.setImageResource(R.drawable.play);
				ivPlay.setVisibility(View.VISIBLE);
			}
			mMediaPlayer.seek(curTime);
			montageView.postInvalidate();
		}
	}

	@Override
	public boolean isMediaPlaying() {
		// TODO Auto-generated method stub
		return mMediaPlayer.isPlaying();
	}
}
