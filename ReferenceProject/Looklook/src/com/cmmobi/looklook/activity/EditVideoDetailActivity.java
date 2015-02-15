
package com.cmmobi.looklook.activity;

import java.io.File;
import java.io.IOException;
import java.util.List;

import android.app.Dialog;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnPreparedListener;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.RelativeLayout.LayoutParams;
import cn.zipper.framwork.core.ZDialog;
import cn.zipper.framwork.core.ZLog;

import com.cmmobi.looklook.R;
import com.cmmobi.looklook.activity.EditMediaDetailActivity.CreateOfflineDiaryThread;
import com.cmmobi.looklook.common.adapter.ImageAdapter;
import com.cmmobi.looklook.common.constant.Constant;
import com.cmmobi.looklook.common.gson.GsonResponse2;
import com.cmmobi.looklook.common.gson.GsonResponse2.MyDiary;
import com.cmmobi.looklook.common.gson.GsonResponse2.createStructureResponse;
import com.cmmobi.looklook.common.gson.GsonResponse2.diaryAttach;
import com.cmmobi.looklook.common.gson.GsonResponse2.modTagsOrPositionResponse;
import com.cmmobi.looklook.common.gson.Requester2;
import com.cmmobi.looklook.common.utils.BitmapUtils;
import com.cmmobi.looklook.common.utils.CmmobiClickAgentWrapper;
import com.cmmobi.looklook.common.utils.EffectTransCodeUtil;
import com.cmmobi.looklook.common.utils.MediaCoverUtils;
import com.cmmobi.looklook.common.utils.MediaCoverUtils.MediaCover;
import com.cmmobi.looklook.common.utils.UmengclickAgentWrapper;
import com.cmmobi.looklook.common.view.MyPositionLayout;
import com.cmmobi.looklook.downloader.EffectLibDownloader;
import com.cmmobi.looklook.fragment.EditMediaBeautifySoundFragment;
import com.cmmobi.looklook.fragment.EditMediaPositionFragment;
import com.cmmobi.looklook.fragment.EditMediaSoundtrackFragment;
import com.cmmobi.looklook.fragment.EditMediaTagFragment;
import com.cmmobi.looklook.fragment.EditMediaTextInputFragment;
import com.cmmobi.looklook.fragment.EditVideoDetailMainFragment;
import com.cmmobi.looklook.fragment.EditVideoEfffectFragment;
import com.cmmobi.looklook.fragment.EditVideoFrontCoverFragment;
import com.cmmobi.looklook.fragment.EditVideoMainFragment;
import com.cmmobi.looklook.fragment.EditVideoMontageFragment;
import com.cmmobi.looklook.fragment.EditVoiceMainFragment;
import com.cmmobi.looklook.info.location.MyLocationInfo;
import com.cmmobi.looklook.info.profile.AccountInfo;
import com.cmmobi.looklook.info.profile.ActiveAccount;
import com.cmmobi.looklook.info.profile.DiaryManager;
import com.cmmobi.looklook.info.profile.MediaValue;
import com.cmmobi.looklook.info.profile.TimeHelper;
import com.cmmobi.looklook.map.MyMapView;
import com.cmmobivideo.utils.EffectBean;
import com.cmmobivideo.utils.EffectUtils;
import com.cmmobivideo.utils.PluginUtils;
import com.cmmobivideo.workers.XMediaPlayer;
import com.cmmobivideo.workers.XMediaPlayer.OnInfoListener;

import effect.XEffectMediaPlayer;
import effect.XEffects;
import effect.XMp4Box;

public class EditVideoDetailActivity extends EditMediaDetailActivity implements OnClickListener, Callback{

	private final String TAG = "EditVideoDetailActivity";
	private FragmentManager fm;
	private EditVideoDetailMainFragment detailMainFragment;
	private EditMediaTagFragment tagFragment;
	private EditMediaPositionFragment posFragment;
	private EditMediaBeautifySoundFragment soundFragment;
	private EditMediaTextInputFragment textFragment;
	private EditVideoMainFragment mainFragment;
	private EditVideoEfffectFragment effectFragment;
	private EditVideoMontageFragment montageFragment;
	private EditVideoFrontCoverFragment frontcoverFragment;
	private EditMediaSoundtrackFragment soundTrackFragment;
	
	public RelativeLayout mainLayout = null;
	public LinearLayout detailMainLayout = null;
	public RelativeLayout rlVideoContent = null;
	
	public final static int FRAGMENT_CROP_VIEW = 0xff;// 裁剪fragment
	public final static int FRAGMENT_EFFECT_VIEW = 0xfe;// 特效fragment
	public final static int FRAGMENT_FRONTCOVER_VIEW = 0xfd;// 封面fragment
	public final static int FRAGMENT_SOUNDTRACK_VIEW = 0xfc;// 配乐
	private static final int HANDLE_HIDE_VIDEO_CONTROL_BAR = 0xfe;
	private String effectVideoPath = null;
	
	public int effectId = 0;
	
	private ImageView thumbImg = null;
	
	public boolean isSelectedDelete = false;
	
	private SeekBar playProcess;
	private ImageView seekBarBg = null;//进度条背景
	private int seekBarLength = 0;
	private TextView tvPlayTime;
	public ImageView ivPlay = null;
	private RelativeLayout rlVideoControl = null;
	private ImageView videoPreview = null;// 视频预览
	
	public Bitmap frontCoverBmp = null;
	public double frontCoverTime = 0;
	private boolean isFrontCoverChanged = false;
	private String frontCoverBmpPath = null;
	private diaryAttach videoAttach = null;
	public Bitmap[] mBmps = new Bitmap[5];
	public double totalTime = 0;
	public long duration = 0;// 单位为s
	public boolean isThumbsGenerated = false;
	private boolean isPlayerNeedToPlay;
	private boolean isPlayerPrepared;
	public int startValue = 0;
	public int endValue = 0;
	public boolean isAddEffect = false;
	
	public double videoMontageStartTime = 0;
	public double videoMontageEndTime = 0;
	
	public List<EffectBean> coverEffectBeans;
	public XEffects coverEffect;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_edit_video_detail);
		
		detailMainFragment = new EditVideoDetailMainFragment();
		tagFragment = new EditMediaTagFragment();
		posFragment = new EditMediaPositionFragment();
		soundFragment = new EditMediaBeautifySoundFragment();
		textFragment = new EditMediaTextInputFragment();
		mainFragment = new EditVideoMainFragment();
		effectFragment = new EditVideoEfffectFragment();
		montageFragment = new EditVideoMontageFragment();
		frontcoverFragment = new EditVideoFrontCoverFragment();
		soundTrackFragment = new EditMediaSoundtrackFragment();
		fm = getSupportFragmentManager();
		
		ivBack = (ImageView) findViewById(R.id.iv_back);
		ivDone = (ImageView) findViewById(R.id.iv_edit_done);
		ivBack.setOnClickListener(this);
		ivDone.setOnClickListener(this);
		
		thumbImg = (ImageView) findViewById(R.id.iv_edit_video_thumbnails);
		thumbImg.setOnClickListener(this);
		
		myLocInfo = MyLocationInfo.getInstance(this);
		myLoc =  myLocInfo.getLocation();

		initView(this);
		mMapView = (MyMapView) findViewById(R.id.bmv_activity_edit_video_detail_position);
		mMapView.setVisibility(View.GONE);
		myPositionLayout = (MyPositionLayout) findViewById(R.id.rl_activity_edit_media_my_position);
		myPositionLayout.setWillNotDraw(false);
		myPositionLayout.setVisibility(View.GONE);
		myPositionTv = (TextView) findViewById(R.id.tv_edit_media_myposition);
		myPositionTv.setVisibility(View.GONE);
		
		mainLayout = (RelativeLayout) findViewById(R.id.rl_edit_video_view);
		detailMainLayout = (LinearLayout) findViewById(R.id.ll_activity_edit_video_detail_main);
		mainLayout.setOnClickListener(this);
		
		diaryUUID = getIntent().getStringExtra(
				DiaryDetailActivity.INTENT_ACTION_DIARY_UUID);
		diaryString = getIntent().getStringExtra(
				DiaryDetailActivity.INTENT_ACTION_DIARY_STRING);
		
		initAttachs();
		
		loadVideoData();
		llXiaLa = (LinearLayout) findViewById(R.id.ll_edit_diary_style);
		llXiaLa.setOnClickListener(this);
		diaryEditStyle = (TextView) findViewById(R.id.tv_edit_name);
		inflater = LayoutInflater.from(this);
		rlTitleBar = findViewById(R.id.rl_title);
		
		handler = new Handler(this);
		
		if (frontCoverBmp != null) {
			thumbImg.setImageBitmap(frontCoverBmp);
		}
		
		goToPage(FRAGMENT_DETAIL_MAIN_VIEW, false);
		getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN|WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
		
		overridePendingTransition(R.anim.slide_in_right,
                R.anim.slide_out_left);
	}
	
	private void loadVideoData() {
		rlVideoContent=(RelativeLayout) findViewById(R.id.video_view);
//		rlVideoContent.setOnClickListener(this);
		
		effectUtils = new EffectUtils(this);
		effectUtils.parseXml("effectcfg/effectlist.xml");
		effectBeans = effectUtils.getEffects(EffectBean.TYPE_EFFECTS_VIDEO);
		if (PluginUtils.isPluginMounted()) {
			mEffects = new XEffects();
		}
		
		if (effectBeans != null) {
			effectBeans.remove(com.cmmobi.looklook.Config.MOSAIC_INDEX); // 6 is mosaic effect;
		}
		//播放时间
		tvPlayTime=(TextView) findViewById(R.id.tv_edit_video_duration);
		if(null==mMediaPlayer){
			mMediaPlayer = new XMediaPlayer(this, mEffects, true);
			mMediaPlayer.setUpdateTimePeriod(0.1f);
			mMediaPlayer.setListener(new MyOnInfoListener());
			rlVideoContent.addView((View)mMediaPlayer.getXSurfaceView());
			if (videoAttachPath != null) {
				mMediaPlayer.open(videoAttachPath);
				totalTime = mMediaPlayer.getTotalTime();
				if (totalTime > 0) {
					duration = (int) totalTime;
					montageEndTime = duration;
					videoMontageEndTime = totalTime;
					tvPlayTime.setText(EditVoiceMainFragment.convertTimeFormartMS("0") + "/" + EditVoiceMainFragment.convertTimeFormartMS(String.valueOf(duration)));
				}
				Log.d(TAG,"totalTime = " + totalTime + " duration = " + duration);
			}
		}
		
		rlVideoControl = (RelativeLayout) findViewById(R.id.rl_edit_video_control);
		videoPreview = (ImageView) findViewById(R.id.iv_video_thumbnail);
		
		ViewTreeObserver vto2 = videoPreview.getViewTreeObserver();   
		vto2.addOnGlobalLayoutListener(new OnGlobalLayoutListener() { 
			@Override   
			public void onGlobalLayout() { 
				if (videoFrontCoverUrl != null) {
					Log.d(TAG,"loadVideoData videoFrontCoverUrl = " + videoFrontCoverUrl);
					MediaValue mediaValue = AccountInfo.getInstance(userID).mediamapping.getMedia(userID, videoFrontCoverUrl);
					if (mediaValue != null) {
						videoPreview.setVisibility(View.VISIBLE);
				    	int height = videoPreview.getMeasuredHeight(); 
				    	int width  = videoPreview.getMeasuredWidth(); 
				    	Log.d(TAG,"measuredwidth = " + width + " measuredheight = " + height);
						frontCoverBmp = BitmapUtils.readBitmapAutoSize(HomeActivity.SDCARD_PATH + mediaValue.path, width, height);
						if (frontCoverBmp != null) {
							Log.d(TAG,"width = " + frontCoverBmp.getWidth() + " height = " + frontCoverBmp.getHeight());
						}
					} else {
						videoPreview.setImageBitmap(null);
					}
				} 
				
				if (frontCoverBmp == null) {
					Log.d(TAG,"frontCoverBmp = null");
					MediaCover mediaCover = MediaCoverUtils.getMediaCover(userID, videoAttachPath, 5);
					if (mediaCover != null) {
						frontCoverBmp = mediaCover.bm;
					}
				}
				
				if (frontCoverBmp != null) {
					videoPreview.setImageBitmap(frontCoverBmp);
					videoPreview.setVisibility(View.VISIBLE);
					thumbImg.setImageBitmap(frontCoverBmp);
					mainFragment.setFrontCoverBmp(frontCoverBmp);
				}
				videoPreview.getViewTreeObserver().removeGlobalOnLayoutListener(this);         
			}   
		}); 
		
		
		playProcess=(SeekBar) findViewById(R.id.sk_edit_video_seekbar);
		playProcess.setMax(100);
		playProcess.setProgress(0);
		
		seekBarBg = (ImageView) findViewById(R.id.v_seekbar_background);
		ViewTreeObserver vto = seekBarBg.getViewTreeObserver();   
		vto.addOnGlobalLayoutListener(new OnGlobalLayoutListener() {

			@Override
			public void onGlobalLayout() {
				// TODO Auto-generated method stub
				seekBarLength = seekBarBg.getMeasuredWidth();
			} 
			
		});
		
		
		ivPlay = (ImageView) findViewById(R.id.iv_edit_video_play);
		ivPlay.setOnClickListener(this);
		
		if (videoDuration == null) {
			videoDuration = "0";
		}
		tvPlayTime.setText(EditVoiceMainFragment.convertTimeFormartMS("0") + "/" + EditVoiceMainFragment.convertTimeFormartMS(String.valueOf(duration)));
		loadCoverEffectLib();
	}
	
	public void loadCoverEffectLib() {
		coverEffectBeans = effectUtils.getEffects(EffectBean.TYPE_EFFECTS_IMAGE);
		if (PluginUtils.isPluginMounted()) {
			coverEffect = new XEffects();
		}
		
		if (coverEffectBeans != null) {
			coverEffectBeans.remove(com.cmmobi.looklook.Config.MOSAIC_INDEX); // 6 is mosaic effect;
		}
	}
	
	public Bitmap switchEffect(int effectId,Bitmap srcBmp) {
		Bitmap dstBmp = null;
		if (PluginUtils.isPluginMounted() && coverEffectBeans != null && effectId < coverEffectBeans.size()) {
			dstBmp = Bitmap.createBitmap(srcBmp.getWidth(), srcBmp.getHeight(), Config.ARGB_8888);
			EffectBean bean = coverEffectBeans.get(effectId);
			if (coverEffect == null) {
				coverEffect = new XEffects();
			}
			effectUtils.changeEffectdWithEffectBean(
					coverEffect, 
					bean, 
					srcBmp.getWidth(), 
					srcBmp.getHeight(), 
					0, 
					null);
			Log.d(TAG,"width = " + srcBmp.getWidth() + " height = " + srcBmp.getHeight() + " name = " + bean.getZHName());
			coverEffect.processBitmap(srcBmp, dstBmp);
			
			if (effectId == 0) {
				dstBmp = srcBmp;
			}
			
			int id = effectId;
			if (id >= com.cmmobi.looklook.Config.MOSAIC_INDEX) {
				id += 1;
			}
		} else {
			dstBmp = srcBmp;
		}
		videoPreview.setImageBitmap(dstBmp);
		thumbImg.setImageBitmap(dstBmp);
		return dstBmp;
	}
	
	public void hideVideoView() {
		if(mMediaPlayer != null){
			mMediaPlayer.hideVideoSurfaceView();
		}
//		rlVideoContent.removeAllViews();
	}
	
	public void showVideoView() {
		if(mMediaPlayer != null){
			mMediaPlayer.showVideoSurfaceView();
		}
//		rlVideoContent.addView((View)mMediaPlayer.getXSurfaceView());
	}
	
	public void generateThumbsCovers(final ImageAdapter adapter) {
		if (videoAttachPath != null && !isThumbsGenerated) {
			/*try {
				
				final MediaPlayer mp=new MediaPlayer();
				mp.setDataSource(videoAttachPath);
				mp.prepare();
				mp.setOnPreparedListener(new OnPreparedListener() {
					@Override
					public void onPrepared(MediaPlayer arg0) {
						// TODO Auto-generated method stub
						duration = mp.getDuration();
						totalTime = duration / 1000.0;
						if (duration < 1000) {
							duration = 1000;
						}
						duration /= 1000;
						final double durationStep = duration / 4.0;
						Log.d(TAG,"durationStep = " + durationStep);
						new Thread(new Runnable() {
							
							@Override
							public void run() {
								// TODO Auto-generated method stub
								Bitmap bmp1 = getVideoCover(0);
								Bitmap bmp2 = getVideoCover(durationStep);
								Bitmap bmp3 = getVideoCover(2*durationStep);
								Bitmap bmp4 = getVideoCover(3*durationStep);
								Bitmap bmp5 = getVideoCover(4*durationStep);
								isThumbsGenerated = true;
								
								mBmps[0] = bmp1;
								mBmps[1] = bmp2;
								mBmps[2] = bmp3;
								mBmps[3] = bmp4;
								mBmps[4] = bmp5;
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
				});
			} catch (IllegalArgumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalStateException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}*/
			
			final double durationStep = duration / 4.0;
			Log.d(TAG,"durationStep = " + durationStep);
			new Thread(new Runnable() {
				
				@Override
				public void run() {
					// TODO Auto-generated method stub
					Bitmap bmp1 = getVideoCover(0);
					Bitmap bmp2 = getVideoCover(durationStep);
					Bitmap bmp3 = getVideoCover(2*durationStep);
					Bitmap bmp4 = getVideoCover(3*durationStep);
					Bitmap bmp5 = getVideoCover(4*durationStep);
					isThumbsGenerated = true;
					
					mBmps[0] = bmp1;
					mBmps[1] = bmp2;
					mBmps[2] = bmp3;
					mBmps[3] = bmp4;
					mBmps[4] = bmp5;
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
			
			if (isPlayerPrepared && mMediaPlayer.getStatus() == XEffectMediaPlayer.STATUS_OPENED) {
				mMediaPlayer.play();
				mMediaPlayer.pause();
				mMediaPlayer.seek(0);
//				videoPreview.setVisibility(View.VISIBLE);
			} else {
				isPlayerNeedToPlay = true;
			}
			
		} else {
			
		}
		
		
		
		if (isThumbsGenerated) {
			adapter.setBitmaps(mBmps);
			adapter.notifyDataSetChanged();
		}
	}
	
	public void previewFrontCover(double time) {
		if (mMediaPlayer != null) {

			if (mMediaPlayer.getStatus() == XEffectMediaPlayer.STATUS_STOP) {
				mMediaPlayer.play();
				mMediaPlayer.pause();
			}
			videoPreview.setVisibility(View.GONE);
			mMediaPlayer.seek(time);
			
		}
		frontCoverTime = time;
	}
	
	public void setFrontCover() {
		Log.d(TAG,"setFrontCover in frontCoverTime = " + frontCoverTime);
		Bitmap coverBmp = getVideoCover(frontCoverTime);
		if (coverBmp != null) {
			Log.d(TAG,"setFrontCover coverBmp not null");
			frontCoverBmp = coverBmp;
			mainFragment.setFrontCoverBmp(coverBmp);
			isFrontCoverChanged = true;
		}
	}
	
	public Bitmap getVideoCover(double time) {
		Bitmap bmp = mMediaPlayer.videScreenCapture(time,512,512);
		return bmp;
	}
	
	private class MyOnInfoListener implements OnInfoListener {

		@Override
		public void onStartPlayer(XMediaPlayer player) {
			Log.e(TAG, "onStartPlayer");
			montageEndTime = (int) player.getTotalTime();
			totalTime = player.getTotalTime();
			duration = (long)totalTime;
			videoDuration = String.valueOf((int)duration);
			videoPreview.setVisibility(View.GONE);
		}

		@Override
		public void onStopPlayer(XMediaPlayer player) {
			Log.e(TAG, "onStopPlayer");
		}

		@Override
		public void OnFinishPlayer(XMediaPlayer player) {
			Log.e(TAG, "OnFinishPlayer");
//			player.stop();
			ivPlay.setImageResource(R.drawable.btn_edit_audio_play);
			videoPreview.setVisibility(View.VISIBLE);
		}

		@Override
		public void onUpdateTime(XMediaPlayer player, double time) {
			Log.e(TAG, "onUpdateTime time = " + time);
			/*int process = (int) (time * 100 / player.getTotalTime());
			playProcess.setProgress(process);
			
			String curTimeStr = String.valueOf((int)time);
			setVideoCurrentTime(curTimeStr);*/
			
			if (player.getStatus() == XEffectMediaPlayer.STATUS_STOP) {
				time = videoMontageStartTime;
			}
			if (time >= videoMontageEndTime) {
				mMediaPlayer.pause();
				mMediaPlayer.seek(videoMontageStartTime);
				ivPlay.setImageResource(R.drawable.btn_edit_audio_play);
			}
			int process = (int) ((time - videoMontageStartTime) * 100 / (videoMontageEndTime - videoMontageStartTime));
			playProcess.setProgress(process);
			setVideoCurrentTime((int)time);
		}

		@Override
		public void onSurfaceCreated(XMediaPlayer player) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onPreparedPlayer(XMediaPlayer player) {
			isPlayerPrepared = true;
			totalTime = mMediaPlayer.getTotalTime();
			duration = (int) totalTime;
			montageEndTime = duration;
			videoMontageEndTime = totalTime;
			tvPlayTime.setText(EditVoiceMainFragment.convertTimeFormartMS("0") + "/" + EditVoiceMainFragment.convertTimeFormartMS(String.valueOf(duration)));
			setMontageTimeRange(montageStartTime, montageEndTime);
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
	
	private void setVideoCurrentTime(String curTime) {
		String curTimeStr = EditVoiceMainFragment.convertTimeFormartMS(curTime);
		String totalDuration = EditVoiceMainFragment.convertTimeFormartMS(String.valueOf(duration));
		tvPlayTime.setText(curTimeStr + "/" + totalDuration);
		Log.d(TAG,"curTime = " + curTimeStr + " videoDuration = " + videoDuration + " totalDuration = " + totalDuration);
	}
	
	private void setVideoCurrentTime(int curTime) {
		String curTimeStr = EditVoiceMainFragment.convertTimeFormartMS(String.valueOf(curTime));
		String totalDuration = EditVoiceMainFragment.convertTimeFormartMS(String.valueOf(duration));
		tvPlayTime.setText(curTimeStr + "/" + totalDuration);
	}
	
	@Override
	public void onResume() {
		mMapView.onResume();
		super.onResume();
		UmengclickAgentWrapper.onResume(this);
		CmmobiClickAgentWrapper.onResume(this);
	}
	
	public boolean isMianAttachChanged() {
		return (isDiaryMontaged || isAddSoundTrack || isAddEffect);
	}
	
	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		switch(arg0.getId()) {
		/*case R.id.ll_edit_media_sound_distinguish:
			edit.setHorizontallyScrolling(false);
			goToPage(FRAGMENT_TEXT_INPUT, false);
			break;*/
		case R.id.iv_back:
			finish();
			break;
		case R.id.iv_edit_done:
			if (isEditButtonPressed) {
				break;
			}
			isEditButtonPressed = true;
			Dialog dialog = ZDialog.show(R.layout.progressdialog, false, true, this,true);
			dialogTitle = (TextView) dialog.findViewById(R.id.progress_dialog_tv);
			isMainAttachChanged = isMianAttachChanged();
			mMediaPlayer.stop();
			shortRecMediaPlayer.stop();
			
			if (getString(R.string.edit_media_cover).equals(getEditDiaryStyle())) {
				if (isDiaryCreated()) {
					if (isFrontCoverChanged) {
						isExtraModifyDone = false;
					}
					modTagsOrPosition();
					addAttach();
					if (isAddSoundAttachDone && isTagPositionChangeDone ) {
						changeFrontCover(videoAttachCover);
					}
				} else {// 若日记未创建成功
					removeDiaryTask();
					createOfflineDiary();
				}
				saveLocalDiaryAndFinish();
			} else {
				saveAsNewDiary();
			}
			
			break;
		case R.id.ll_edit_diary_style:
			Log.d(TAG,"ll_edit_diary_style");
			showDuplicateDiaryName();
			break;
		case R.id.iv_edit_video_play:
			if(mMediaPlayer!=null && videoAttachPath != null){
				int status = mMediaPlayer.getStatus();
				Log.d(TAG,"mediapStatus = " + status);
				if (status == XEffectMediaPlayer.STATUS_UNKNOW || status == XEffectMediaPlayer.STATUS_STOP || status == XEffectMediaPlayer.STATUS_OPENED) {
					if (status == XEffectMediaPlayer.STATUS_UNKNOW) {
						if (videoAttachPath != null) {
							isPlayerPrepared = false;
							mMediaPlayer.open(videoAttachPath);
						}
					}
					if (isPlayerPrepared) {
						mMediaPlayer.play();
						if (videoMontageStartTime > 0.1) {
							mMediaPlayer.seek(videoMontageStartTime);
						}
					} else {
						isPlayerNeedToPlay = true;
					}
					ivPlay.setImageResource(R.drawable.btn_edit_audio_pause);
//					rlVideoControl.setVisibility(View.INVISIBLE);
				} else if (mMediaPlayer.getStatus() == XEffectMediaPlayer.STATUS_PALYING) {
					mMediaPlayer.pause();
					ivPlay.setImageResource(R.drawable.btn_edit_audio_play);
					setPlayBtnStatus();
				} else if (mMediaPlayer.getStatus() == XEffectMediaPlayer.STATUS_PAUSE) {
//					rlVideoControl.setVisibility(View.INVISIBLE);
					ivPlay.setImageResource(R.drawable.btn_edit_audio_pause);
					mMediaPlayer.resume();
				}
			}else{
				Log.e(TAG, "mMediaPlayer  is null");
			}
			break;
		case R.id.video_view:
//			setPlayBtnStatus();
			break;
		case R.id.ll_biezhen:
			if (shortRecMediaPlayer.isPlaying()) {
				tackView.stopAudio(shortRecMediaPlayer);
			} else {
				tackView.playAudio(audioPath,shortRecMediaPlayer);
			}
			break;
		case R.id.iv_edit_video_thumbnails:
			if (fragmentType == FRAGMENT_DETAIL_MAIN_VIEW 
			|| fragmentType == FRAGMENT_TAG_VIEW 
			|| fragmentType == FRAGMENT_TEXT_INPUT) {
				mainLayout.setVisibility(View.VISIBLE);
				detailMainLayout.setVisibility(View.GONE);
				if (mMediaPlayer.getStatus() == XEffectMediaPlayer.STATUS_PAUSE) {
					mMediaPlayer.seek(0);
					mMediaPlayer.resume();
				} else {
					mMediaPlayer.play();
				}
				playProcess.setProgress(0);
				ivPlay.setImageResource(R.drawable.btn_edit_audio_pause);
			}
			break;
		case R.id.rl_edit_video_view:
			if (fragmentType == FRAGMENT_DETAIL_MAIN_VIEW 
			|| fragmentType == FRAGMENT_TAG_VIEW 
			|| fragmentType == FRAGMENT_TEXT_INPUT) {
				mainLayout.setVisibility(View.GONE);
				detailMainLayout.setVisibility(View.VISIBLE);
				if (mMediaPlayer.getStatus() == XEffectMediaPlayer.STATUS_PALYING) {
					mMediaPlayer.pause();
				}
				ivPlay.setImageResource(R.drawable.btn_edit_audio_play);
			}
			break;
		}
	}
	
	private void changeFrontCover(diaryAttach attach) {
		if (isFrontCoverChanged && attach != null) {
			long currentTime = TimeHelper.getInstance().now();
			String imageID = String.valueOf(currentTime);
			String RECORD_FILE_DIR =  Constant.SD_STORAGE_ROOT + "/" + ActiveAccount.getInstance(this).getLookLookID() + "/pic";
			String outputFile = Environment.getExternalStorageDirectory() + RECORD_FILE_DIR + "/" + imageID +  "/" + imageID + ".jpg" ;
			
			if(BitmapUtils.saveBitmap2file(frontCoverBmp, outputFile)) {
				isExtraModifyDone = false;
				frontCoverBmpPath = outputFile;
				Log.d(TAG,"outputFile = " + outputFile + " attachid = " + attach.attachid);
				Requester2.uploadPicture(handler, outputFile, "2", attach.attachid);
			} else {
				isExtraModifyDone = true;
			}
		} else {
			isExtraModifyDone = true;
		}
	}

	//设置播放按钮状态
	private void setPlayBtnStatus(){
		if(View.INVISIBLE==rlVideoControl.getVisibility()){
			rlVideoControl.setVisibility(View.VISIBLE);
			handler.sendEmptyMessageDelayed(HANDLE_HIDE_VIDEO_CONTROL_BAR, 3000);
		}
	}
	
	public void setMontageTimeRange(float startTime,float endTime) {
		Log.d(TAG,"setMontageTimeRange in startTime = " + startTime + " endTime = " + endTime);
		montageStartTime = startTime;
		montageEndTime = endTime;
		if (mainFragment != null) {
			mainFragment.setTimeRange((int)startTime, (int)endTime);
		}
	}
	
	public List<Integer> getCheckedList() {
		return checkedList;
	}
	
	public void goToPage(int type, boolean record) {
		Fragment dst;
		String mViewName = null;
		dst = null;
		if (type == FRAGMENT_DETAIL_MAIN_VIEW) {
			if (fragmentType == FRAGMENT_POS_VIEW) {
				showVideoView();
//				mMediaPlayer.getXSurfaceView().setVisibility(View.VISIBLE);
			}
			dst = detailMainFragment;
		} else if (type == FRAGMENT_TAG_VIEW) {
			dst = tagFragment;
		} else if (type == FRAGMENT_POS_VIEW) {
			dst = posFragment;
		}  else if (type == FRAGMENT_SOUND_VIEW) {
			dst = soundFragment;
		} else if (type == FRAGMENT_MAIN_VIEW) {
			dst = mainFragment;
		} else if (type == FRAGMENT_CROP_VIEW) {
			if (thumbImg.isShown()) {
				onClick(thumbImg);
			}
			dst = montageFragment;
		} else if (type == FRAGMENT_EFFECT_VIEW) {
			if (thumbImg.isShown()) {
				onClick(thumbImg);
			}
			dst = effectFragment;
		} else if (type == FRAGMENT_FRONTCOVER_VIEW) {
			if (thumbImg.isShown()) {
				onClick(thumbImg);
			}
			dst = frontcoverFragment;
		} else if (type == FRAGMENT_SOUNDTRACK_VIEW) {
			if (thumbImg.isShown()) {
				onClick(thumbImg);
			}
			dst = soundTrackFragment;
		} else if (type == FRAGMENT_TEXT_INPUT) {
			dst = textFragment;
		} else {
			Log.e(TAG, "unknow fragment - type:" + type);
			return;
		}

		fragmentType = type;
		FragmentTransaction ft = fm.beginTransaction();

		if (fm.findFragmentById(R.id.fl_edit_video_detail_fragment) != null) {
			ft.replace(R.id.fl_edit_video_detail_fragment, dst, mViewName);
		} else {
			ft.add(R.id.fl_edit_video_detail_fragment, dst, mViewName);
		}

		if (record) {
			ft.addToBackStack(null);
		}

		ft.commit();
		
		if (type == FRAGMENT_DETAIL_MAIN_VIEW || type == FRAGMENT_MAIN_VIEW) {
			ivDone.setVisibility(View.VISIBLE);
			ivBack.setVisibility(View.VISIBLE);
		} else {
			ivDone.setVisibility(View.GONE);
			ivBack.setVisibility(View.GONE);
		}
	}
	
	@Override
	public void setMapViewVisibility(int visible) {
		mMapView.setVisibility(visible);
	}
	
	public MyMapView getMapView() {
		return mMapView;
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		mMapView.destroy();
		mMapView.destroyDrawingCache();
		try {
			if(mMediaPlayer!=null){
				mMediaPlayer.release();
				mMediaPlayer=null;
				isPlayerNeedToPlay = false;
				isPlayerPrepared = false;
			}
			
			if (shortRecMediaPlayer != null) {
				shortRecMediaPlayer.release();
				shortRecMediaPlayer = null;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		super.onDestroy();
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		mMapView.onPause();
		super.onPause();
		
		UmengclickAgentWrapper.onPause(this);
		CmmobiClickAgentWrapper.onPause(this);
	}
	
	@Override
	public void onStop(){
		if (shortRecMediaPlayer.isPlaying()) {
			tackView.stopAudio(shortRecMediaPlayer);
		}
		if (mMediaPlayer.isPlaying()) {
			mMediaPlayer.pause();
			ivPlay.setImageResource(R.drawable.btn_edit_audio_play);
		}
		super.onStop();
		CmmobiClickAgentWrapper.onStop(this);
	}
	
	protected void modifyMainAttach() {
		if (isMainAttachChanged) {
			if (isDiaryMontaged) {
				String inputFile = videoAttachPath;
				long currentTime = TimeHelper.getInstance().now();
				String videoID = String.valueOf(currentTime);
				String RECORD_FILE_DIR =  Constant.SD_STORAGE_ROOT + "/" + ActiveAccount.getInstance(this).getLookLookID() + "/video";
				String outputFile = Environment.getExternalStorageDirectory() + RECORD_FILE_DIR + "/" + videoID +  "/" + videoID + ".mp4" ;
			
				File dstFile = new File(outputFile);
		        if(!dstFile.getParentFile().exists()) {
		        	dstFile.getParentFile().mkdirs();
				}
		        editAttachPath = outputFile;
		        montageVideo(videoID,inputFile,outputFile);
			}
			if (mEffects.getEffectsCount() > 0) {
				try {
					String infilename = "";
					if (editAttachPath != null) {
						infilename = editAttachPath;
					} else {
						infilename = videoAttachPath;
					}
					Log.d(TAG,"infilename = " + infilename);
					EffectTransCodeUtil transCode = new EffectTransCodeUtil(handler, mEffects, infilename, this,EffectTransCodeUtil.MAIN_VIDEO);
					transCode.start("video");
					Log.d(TAG,"before await()");
					threadsSignal.await();
					Log.d(TAG,"after await()");
					
					if (effectVideoPath != null) {
						if (editAttachPath != null) {
							EditMediaDetailActivity.delFile(editAttachPath);
						}
						editAttachPath = effectVideoPath;
					}
					
					Log.d(TAG,"editAttachPath = " + editAttachPath + " effectVideoPath = " + effectVideoPath);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}
	
	private void montageVideo(String videoID,String inputFile,String outputFile) {
		XMp4Box mMp4Box = new XMp4Box();
		if (isSelectedDelete) {
			String RECORD_FILE_DIR =  Constant.SD_STORAGE_ROOT + "/" + ActiveAccount.getInstance(EditVideoDetailActivity.this).getLookLookID() + "/video";
			String outputFile1 = Environment.getExternalStorageDirectory() + RECORD_FILE_DIR + "/" + videoID +  "/" + "temp1.mp4";
			int ret1 = mMp4Box.splitFile(inputFile,outputFile1,0,montageStartTime);
			String outputFile2 = Environment.getExternalStorageDirectory() + RECORD_FILE_DIR + "/" + videoID +  "/" + "temp2.mp4";
			float videoDuration = (float) (montageFragment.totalTime);
			int ret2 = mMp4Box.splitFile(inputFile,outputFile2,montageEndTime,videoDuration);
			Log.d(TAG,"ret1 = " + ret1 + " ret2 = " + ret2 + " montageStartTime = " + montageStartTime + " montageEndTime = " + montageEndTime + " videoDuration = " + videoDuration);
			mMp4Box.appendOpen(outputFile);
			if (ret1 == 0) {
				mMp4Box.appendFile(outputFile1);
				EditMediaDetailActivity.delFile(outputFile1);
			} 
			if (ret2 == 0) {
				mMp4Box.appendFile(outputFile2);
				EditMediaDetailActivity.delFile(outputFile2);
			}
			mMp4Box.appendClose();
		} else {
			mMp4Box.splitFile(inputFile,outputFile,montageStartTime,montageEndTime);//1s
		}
	}
	
	/**
	 * 切换特效;
	 * 
	 * @param effect: 目标特效;
	 */
	public void switchEffect(int effectId) {
		
		if (PluginUtils.isPluginMounted() && effectBeans != null && effectId < effectBeans.size()) {
			EffectBean bean = effectBeans.get(effectId);
			
			effectUtils.changeEffectdWithEffectBean(
					mEffects, 
					bean, 
					mMediaPlayer.getVideoWith(), 
					mMediaPlayer.getVideoHeight(), 
					0, 
					null,
					mMediaPlayer.getVideoAngle());
			
			if (mMediaPlayer.isPause()) {
				mMediaPlayer.seek(mMediaPlayer.getCurrentTime());
			}
			
			int id = effectId;
			if (id >= com.cmmobi.looklook.Config.MOSAIC_INDEX) {
				id += 1;
			}
			CmmobiClickAgentWrapper.onEvent(this, "my_effect", String.valueOf(id));
			
		} else {
			EffectLibDownloader downloader = new EffectLibDownloader(this, true);
			downloader.askForDownloadEffectLib(null);
		}
	}
	
	@Override
	public boolean handleMessage(Message msg) {
		// TODO Auto-generated method stub
		switch(msg.what) {
		case Requester2.RESPONSE_TYPE_MODIFY_TAG:
			Log.d(TAG,"handleMessage RESPONSE_TYPE_MODIFY_TAG");
			GsonResponse2.modTagsOrPositionResponse tagandpositionResponse = (modTagsOrPositionResponse) msg.obj;
			if (tagandpositionResponse !=null && "0".equals(tagandpositionResponse.status)) {
				Log.d(TAG,"handleMessage RESPONSE_TYPE_MODIFY_TAG successed");
				DiaryManager diarymanager = DiaryManager.getInstance();
				MyDiary myLocalDiary = diarymanager.findLocalDiaryByUuid(diaryUUID);
				if (myLocalDiary !=  null) {
					Log.d(TAG,"save myLocalDiary not null");
					myLocalDiary.position = myDiary.position;
					myLocalDiary.tags = getMyDiary().tags;
					DiaryManager.getInstance().diaryDataChanged(myDiary.diaryuuid);
				}
			}
			isTagPositionChangeDone = true;
			saveLocalDiaryAndFinish();
			break;
		case Requester2.RESPONSE_TYPE_CREATE_STRUCTURE:
			Log.d(TAG,"handleMessage RESPONSE_TYPE_CREATE_STRUCTURE");
			structureResponse = (createStructureResponse) msg.obj;
			
			if (getString(R.string.edit_media_cover).equals(getEditDiaryStyle())) {// 覆盖模式
				if (isDiaryCreated()) {
					if (structureResponse != null && "0".equals(structureResponse.status)) {// 创建日记结构成功
						new CreateDiarySucessedThread().start();
					} else { // 创建日记结构失败
						new CreateDiaryFailedThread().start();
					}
				} else {
					new CreateOfflineDiaryThread().start();
				}
				
			} else {// 另存为模式
				if (structureResponse != null) {// 创建另存为日记结构成功
//					Log.d(TAG,"************attachsSize*******" + structureResponse.attachs.length);
					new CreateNewDiaryThread().start();
				} 
			}
			
			break;
		case EffectTransCodeUtil.HANDLER_PROCESS_EFFECTS_FINISH:
			Log.d(TAG,"VideoEffectTransCodeUtil.HANDLER_PROCESS_EFFECTS_FINISH");
			if (msg.arg1 == EffectTransCodeUtil.MAIN_VIDEO) {
				if (msg.obj != null) {
					effectVideoPath = (String) msg.obj;
				}
				effectState = 1;
				threadsSignal.countDown();
			} else if (msg.arg1 == EffectTransCodeUtil.SHORT_AUDIO) {
				if (msg.obj != null) {
					audioPath = (String) msg.obj;
				}
				shortAudioSignal.countDown();
			}
			break;
		case EffectTransCodeUtil.HANDLER_PROCESS_EFFECTS_SCHEDULE:
			int percent = msg.arg2;
			Log.d(TAG,"VideoEffectTransCodeUtil.HANDLER_PROCESS_EFFECTS_SCHEDULE percent = " + percent);
			if (dialogTitle != null) {
				String titel = "";
				if (msg.arg1 == EffectTransCodeUtil.MAIN_VIDEO) {
					titel = getString(R.string.process_video_effect) + " " + percent + "%";
				} else if (msg.arg1 == EffectTransCodeUtil.SHORT_AUDIO) {
					titel = getString(R.string.process_short_record_effect) + " " + percent + "%";
				}
				dialogTitle.setText(titel);
			}
			break;
		case EditMediaDetailActivity.HANDLER_DISMISS_PROCESS_DIALOG:
			changeFrontCover(videoAttachCover);
			saveLocalDiaryAndFinish();
			break;
		case EditMediaDetailActivity.HANDLER_SOUND_DELETE:
			isDeleteSound = true;
			hasShortSound = false;
			isAddSoundAttach = false;
			checkAuxiliaryAttachEmpty();
			detailMainFragment.processDelShortSoundMsg();
			break;
		case HANDLE_HIDE_VIDEO_CONTROL_BAR:
			rlVideoControl.setVisibility(View.INVISIBLE);
			break;
		case DiaryDetailActivity.HANDLER_UPDATE_LONG_RECORD_PLAYER_PROCESS:
			String curTime = (String) msg.obj;
			setVideoCurrentTime(curTime);
			break;
		case  Requester2.RESPONSE_TYPE_UPLOAD_PICTURE://上传视频封面
			Log.d(TAG, "RESPONSE_TYPE_UPLOAD_PICTURE in");
			GsonResponse2.uploadPictrue res = (GsonResponse2.uploadPictrue) msg.obj;
			mappingVideoCover(res);
			if (res != null && "0".equals(res.status)) {
				Log.d(TAG, "res.imageurl="+res.imageurl);
				
				isExtraModifyDone = true;
				saveLocalDiaryAndFinish();
			} else {
				isExtraModifyDone = true;
				saveLocalDiaryAndFinish();
			}
			break;
		}
		return false;
	}
	
	private void mappingVideoCover(GsonResponse2.uploadPictrue res) {
		Log.d(TAG,"mappingVideoCover in");
		String oldVideoCover = videoAttachCover.videocover;
		String relativePath = frontCoverBmpPath.replace(HomeActivity.SDCARD_PATH, "");
		if (res == null || res.imageurl == null || "".equals(res.imageurl)) {
			videoAttachCover.videocover = relativePath + ".vc";
		} else {
			videoAttachCover.videocover = res.imageurl;
		}
		boolean isChanged = false;
		if (videoAttachCover.videocover != null) {// 登记视频封面
			if(frontCoverBmpPath != null) {
				MediaValue mediaValueCover = new MediaValue();
				mediaValueCover.UID = userID;
				mediaValueCover.path = relativePath;
				mediaValueCover.totalSize = new File(frontCoverBmpPath).length();
				mediaValueCover.realSize = mediaValueCover.totalSize;
				mediaValueCover.MediaType = 2;
				mediaValueCover.url = videoAttachCover.videocover;
				ZLog.printObject(mediaValueCover);
				if (MediaValue.checkMediaAvailable(mediaValueCover, 2)) {
					isChanged = true;
					Log.d(TAG,"mappingVideoCover url = " + mediaValueCover.url);
					AccountInfo.getInstance(userID).mediamapping.setMedia(userID, mediaValueCover.url, mediaValueCover);
					if (AccountInfo.getInstance(userID).mediamapping.getMedia(userID, oldVideoCover) != null) {
						AccountInfo.getInstance(userID).mediamapping.delMedia(userID, oldVideoCover);
					}
				}
			}
		}
		if (!isChanged) {
			videoAttachCover.videocover = oldVideoCover;
		}
		MyDiary myLocalDiary = DiaryManager.getInstance().findLocalDiaryByUuid(diaryUUID);
		myLocalDiary.setVideoCoverUrl(videoAttachCover.videocover);
	}

	@Override
	protected String getMontageDuration() {
		// TODO Auto-generated method stub
		float duration = 1;
		if (isSelectedDelete) {
			float videoDuration = (float) (totalTime);
			duration = montageStartTime + (videoDuration - montageEndTime);
			if (duration < 1) {
				duration = 1;
			}
		} else {
			duration = montageEndTime - montageStartTime;
			if (duration < 1){
				duration = 1;
			}
		}
		return String.valueOf((int) duration);
	}

	@Override
	protected void copyMainAttach() {
		// TODO Auto-generated method stub
		String videoID = String.valueOf(TimeHelper.getInstance().now());
		String dstFilePath = Environment.getExternalStorageDirectory().getPath() + Constant.SD_STORAGE_ROOT + "/" 
				+ ActiveAccount.getInstance(this).getLookLookID() + "/video/" + videoID + "/" + videoID + ".mp4";
		
		long fileLength = copyFile2(videoAttachPath, dstFilePath);
		if (fileLength > 0) {
			editAttachPath = dstFilePath;
		}
	}

	@Override
	public void setSoundTrackText(String text) {
		// TODO Auto-generated method stub
		mainFragment.setSoundTrackText(text);
	}

	@Override
	public void setBeautifulSoundText(String text) {
		// TODO Auto-generated method stub
		detailMainFragment.setBeautifulSoundText(text);
	}
	
	public void setEffectText(String text) {
		mainFragment.setEffectText(text);
	}
	
	public void seekBarCropUp(int thumb1,int thumb2,int totalWidth) {
		if (mMediaPlayer != null) {
			mMediaPlayer.pause();
			ivPlay.setImageResource(R.drawable.btn_edit_audio_play);
		}
		int width = (int)((((float) (thumb2 - thumb1)) / (float)totalWidth) * seekBarLength);
		
		int leftMargin = (int)(((float)thumb1 / totalWidth) * seekBarLength);
		videoMontageStartTime = (float) (((float)thumb1 / totalWidth) * totalTime);
		videoMontageEndTime = (float) (((float)thumb2 / totalWidth) * totalTime);
		Log.d(TAG,"width = " + width + " thumb1 = " + thumb1 + " thumb2 = " 
			    + thumb2 + " totalWidth = " + totalWidth + " seekBarLength = " 
				+ seekBarLength + " leftMargin = " + leftMargin + " startTime = " 
			    + videoMontageStartTime + " endTime = " + videoMontageEndTime);
		
		mMediaPlayer.seek(videoMontageStartTime);
		setVideoCurrentTime((int)videoMontageStartTime);
		 
		RelativeLayout.LayoutParams params = new LayoutParams(width,
				LayoutParams.WRAP_CONTENT);
		params.leftMargin = leftMargin;
		playProcess.setLayoutParams(params);
		
		playProcess.requestLayout();
		playProcess.setProgress(0);
	}
	
}
