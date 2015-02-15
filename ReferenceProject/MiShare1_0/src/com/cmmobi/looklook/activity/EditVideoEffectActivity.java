package com.cmmobi.looklook.activity;

import java.util.HashMap;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Message;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.RelativeLayout;
import android.widget.TextView;
import cn.zipper.framwork.core.ZActivity;
import cn.zipper.framwork.core.ZApplication;
import cn.zipper.framwork.core.ZDialog;
import cn.zipper.framwork.core.ZLayoutInflater;

import com.cmmobi.looklook.R;
import com.cmmobi.looklook.common.adapter.ImageAdapter;
import com.cmmobi.looklook.common.gson.DiaryController;
import com.cmmobi.looklook.common.gson.GsonResponse3.MyDiary;
import com.cmmobi.looklook.common.utils.BitmapUtils;
import com.cmmobi.looklook.common.utils.CmmobiClickAgentWrapper;
import com.cmmobi.looklook.common.utils.DiaryEditNote;
import com.cmmobi.looklook.common.utils.EffectTransCodeUtil;
import com.cmmobi.looklook.common.view.VideoMontageView;
import com.cmmobi.looklook.common.view.VideoMontageView.VideoMontageChangeListener;
import com.cmmobi.looklook.info.profile.AccountInfo;
import com.cmmobi.looklook.info.profile.ActiveAccount;
import com.cmmobi.looklook.info.profile.MediaValue;
import com.cmmobi.statistics.CmmobiClickAgent;
import com.cmmobivideo.utils.EffectBean;
import com.cmmobivideo.utils.EffectUtils;
import com.cmmobivideo.utils.Mp4InfoUtils;
import com.cmmobivideo.utils.PluginUtils;
import com.cmmobivideo.workers.XMediaPlayer;
import com.cmmobivideo.workers.XMediaPlayer.OnInfoListener;
import com.google.gson.Gson;

import effect.EffectType;
import effect.XEffectMediaPlayer;
import effect.XEffects;

public class EditVideoEffectActivity extends ZActivity implements VideoMontageChangeListener {

	private final String TAG = "EditVideoEffectActivity";
	private FrameLayout videoLayout = null;
	private DisplayMetrics dm = new DisplayMetrics();
	private RelativeLayout rlVideoContent = null;
	private XMediaPlayer mMediaPlayer = null;
	private XEffects mEffects = null;
	private XEffects mCoverEffects = null;
	public EffectUtils effectUtils;
	public List<EffectBean> effectBeans;
	public List<EffectBean> coverEffectBeans;
	private String videoPath = null;
	private ImageView ivPlay = null;
	private boolean isPlayerNeedToPlay;
	private boolean isPlayerPrepared;
	
	private ImageView ivVideoCover = null;
	private Bitmap videoCoverBmp = null;
	private String videoCoverUrl = null;
	
//	private String diaryUUID = null;
	private MyDiary myDiary = null;
	private String userID = null;
	
	private VideoMontageView montageView = null;
	private GridView gridView = null;
	private ImageAdapter adapter = null;
	
	private ImageView ivBack = null;
	private ImageView ivDone = null;
	
	private double videoDuration = 0;
	private final int COVER_NUM = 16;
	private int seekWidth = 0;
	public Bitmap[] mBmps = new Bitmap[COVER_NUM];
	private Bitmap defaultBmp = null;
	private LinearLayout effectGroup = null;
	private ImageView ivSelectedView = null;
	private CountDownLatch processSignal = new CountDownLatch(1);
	private String effectVideoPath = null;
	public TextView dialogTitle;
//	private Dialog finishDialog;
	public DiaryEditNote diaryEditNote = null;
	public int curEffectId = 0;
	private float VIEW_SPACING = 7.5f;
	private HorizontalScrollView horiScrView = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_edit_video_effect);
		
		videoLayout = (FrameLayout) findViewById(R.id.fl_edit_diary_video);
		dm = getResources().getDisplayMetrics();
		Log.d(TAG,"width = " + dm.widthPixels + " height = " + dm.heightPixels + " density = " + dm.density);
		RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(dm.widthPixels,dm.widthPixels);
		params.addRule(RelativeLayout.BELOW, R.id.rl_edit_video_effect_top);
		videoLayout.setLayoutParams(params);
		VIEW_SPACING = getResources().getDimension(R.dimen.effect_margin);
		Log.d(TAG,"VIEW_SPACEING = " + VIEW_SPACING + " density = " + dm.density);
		
		horiScrView = (HorizontalScrollView) findViewById(R.id.hsv_photo_effects);
		
		ivPlay = (ImageView) findViewById(R.id.iv_video_play);
		ivPlay.setOnClickListener(this);
		
		findViewById(R.id.fl_edit_diary_video).setOnClickListener(this);
		
		ivVideoCover = (ImageView) findViewById(R.id.iv_video_preview);
		ivBack = (ImageView) findViewById(R.id.iv_edit_diary_back);
		ivDone = (ImageView) findViewById(R.id.iv_edit_diary_save);
		
		ivBack.setOnClickListener(this);
		ivDone.setOnClickListener(this);
		ivDone.setEnabled(false);
		
		montageView = (VideoMontageView) findViewById(R.id.vv_edit_video_montageview);
		gridView = (GridView) findViewById(R.id.gv_edit_video_montage_imgs);
		adapter = new ImageAdapter(this);
		gridView.setAdapter(adapter);
		loadVideoData();
		effectGroup = (LinearLayout) findViewById(R.id.effect_group);
		
		videoCoverUrl = myDiary.getVideoCoverUrl();
		Drawable drawable = getResources().getDrawable(R.drawable.effect_icon);
		defaultBmp = BitmapUtils.drawableToBitmap(drawable);
		
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
				
				if (defaultBmp != null) {
					Log.d(TAG,"bmpWidth =  " + defaultBmp.getWidth() + " bmpHeight = " + defaultBmp.getHeight());
					new Thread(new Runnable() {
						
						@Override
						public void run() {
							// TODO Auto-generated method stub
							initEffect();
						}
					}).start();
				}
				
				ivVideoCover.getViewTreeObserver().removeGlobalOnLayoutListener(this);         
			}   
		}); 
//		initDialog();
	}
	
	private void initEffect() {
		
		int index = 0;
		int selectIndex = diaryEditNote.isEffect ? diaryEditNote.effectIndex:0;
		int leftMargin = (int)(VIEW_SPACING * dm.density);
		int width = (dm.widthPixels - 5 * leftMargin) / 4; 
		defaultBmp = Bitmap.createScaledBitmap(defaultBmp, width,width, true);
		Log.d(TAG,"leftMargin = " + leftMargin);
		
		for (final EffectBean effect:coverEffectBeans) {
			final RelativeLayout layout = (RelativeLayout) ZLayoutInflater.inflate(R.layout.include_effect_buttons_stub);
			final ImageView effectImage= (ImageView) layout.findViewById(R.id.effect_image);
			final TextView effectTv = (TextView) layout.findViewById(R.id.effect_name);
			final ImageView effectSelectedImage = (ImageView) layout.findViewById(R.id.effect_selected_image);
			LinearLayout.LayoutParams params = new LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT);
			params.setMargins(leftMargin, 0, 0, 0);
			layout.setLayoutParams(params);
			layout.setId(index);
			final Bitmap effectBmp = switchEffect(index, defaultBmp);
			final Bitmap coverEffectBmp = switchEffect(index, videoCoverBmp);
			EffectWrapper effectWrapper = new EffectWrapper();
			effectWrapper.effectId = index;
			effectWrapper.effectSelectedView = effectSelectedImage;
			effectWrapper.effectBmp = coverEffectBmp;
			if (index == selectIndex) {
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						// TODO Auto-generated method stub
						if (coverEffectBmp != null) {
							ivVideoCover.setImageBitmap(coverEffectBmp);
						}
					}
				});
				
				ivSelectedView = effectSelectedImage;
				effectSelectedImage.setVisibility(View.VISIBLE);
			}
			
			layout.setTag(effectWrapper);
			index++;
			runOnUiThread(new Runnable() {
				
				@Override
				public void run() {
					// TODO Auto-generated method stub
					effectImage.setImageBitmap(effectBmp);
					effectTv.setText(effect.getZHName());
					layout.setOnClickListener(EditVideoEffectActivity.this);
					effectGroup.addView(layout);
				}
			});
		}
	}
	
	public Bitmap switchEffect(int effectId,Bitmap srcBmp) {
		if (srcBmp == null) {
			return null;
		}
		Bitmap dstBmp = null;
		if (PluginUtils.isPluginMounted() && coverEffectBeans != null  && effectId < coverEffectBeans.size()) {
			
			Log.d(TAG,"[switchEffect] srcBmp:"+srcBmp.getConfig().name());
			Log.d(TAG,"[switchEffect] srcBmp:"+srcBmp);
			
			dstBmp = Bitmap.createBitmap(srcBmp.getWidth(), srcBmp.getHeight(), Config.ARGB_8888);
			EffectBean bean = coverEffectBeans.get(effectId);
			
			effectUtils.changeEffectdWithEffectBean(
					mCoverEffects, 
					bean, 
					srcBmp.getWidth(), 
					srcBmp.getHeight(), 
					0, 
					null);
			Log.d(TAG,"width = " + srcBmp.getWidth() + " height = " + srcBmp.getHeight() + " name = " + bean.getZHName());
			mCoverEffects.processBitmap(srcBmp, dstBmp);
			
			if (effectId == 0) {
				dstBmp = srcBmp;
			}
			int id = effectId;
			if (id >= com.cmmobi.looklook.Config.MOSAIC_INDEX) {
				id += 1;
			}
			
		} else {
			dstBmp = null;//srcBmp;
			
		}
		Log.d(TAG,"[switchEffect] dstBmp:"+dstBmp);
		return dstBmp;
	}
	
	
	
	private void loadVideoData() {
		rlVideoContent=(RelativeLayout) findViewById(R.id.video_view);
//		rlVideoContent.setOnClickListener(this);
		
		effectUtils = new EffectUtils(this);
		if (PluginUtils.isPluginMounted()) {
			mEffects = new XEffects();
		}
		
		userID =  ActiveAccount.getInstance(ZApplication.getInstance()).getUID();
//		diaryUUID = getIntent().getStringExtra("diaryuuid");
//		myDiary = DiaryManager.getInstance().findMyDiaryByUUID(diaryUUID);
		String diaryString = getIntent().getStringExtra("diarystring");
		myDiary = new Gson().fromJson(diaryString,MyDiary.class);
		
		String diaryEditString = getIntent().getStringExtra(DiaryEditPreviewActivity.INTENT_ACTION_EDIT_NOTELIST);
		diaryEditNote = new Gson().fromJson(diaryEditString, DiaryEditNote.class);
		curEffectId = diaryEditNote.effectIndex;
//		String videoUrl = myDiary.getMainUrl();
//		
//		MediaValue mediaValue = AccountInfo.getInstance(userID).mediamapping.getMedia(userID, videoUrl);
//		if (mediaValue != null) {
//			videoPath =  Environment.getExternalStorageDirectory() + mediaValue.path;
//			effectVideoPath = videoPath;
//		}
//		videoPath = myDiary.getMainPath();
		videoPath = diaryEditNote.mediaPath;
		effectVideoPath = videoPath;
		videoDuration = new Mp4InfoUtils(videoPath).totaltime;
		
		montageView.setTotalTime(videoDuration);
		montageView.setMontageDisabled(true);
		montageView.setVideoMontageChangeListener(this);
		
		Log.d(TAG, "videoPath = " + videoPath);
		if(null==mMediaPlayer) {
			mMediaPlayer = new XMediaPlayer(this, mEffects, true);
			mMediaPlayer.setUpdateTimePeriod(0.1f);
			mMediaPlayer.setListener(new MyOnInfoListener());
			rlVideoContent.addView((View)mMediaPlayer.getXSurfaceView());
			if (videoPath != null) {
				mMediaPlayer.open(videoPath);
			}
		}
		
		effectUtils = new EffectUtils(this);
		effectUtils.parseXml("effectcfg/effectlist.xml");
		coverEffectBeans = effectUtils.getEffects(EffectBean.TYPE_EFFECTS_IMAGE);
		effectBeans = effectUtils.getEffects(EffectBean.TYPE_EFFECTS_VIDEO);
		if (PluginUtils.isPluginMounted()) {
			mCoverEffects = new XEffects();
		}
		
		if (effectBeans != null) {
			effectUtils.removeEffectBean(effectBeans,EffectType.KEF_TYPE_MOSAIC);
			effectUtils.removeEffectBean(coverEffectBeans,EffectType.KEF_TYPE_MOSAIC);
		}
		
		if (diaryEditNote.isEffect) {
			switchEffect(diaryEditNote.effectIndex);
		}
		
		if (diaryEditNote.isAddSoundTrack) {
			double percent = diaryEditNote.percent;
			effectUtils.addMusic(mEffects, "effectcfg/audio/addmusic.cfg", diaryEditNote.soundtrackPath, 
					mMediaPlayer.getChannels(), 
					mMediaPlayer.getSampleRate(), 
					mMediaPlayer.getBitsPerChannel(), 
					percent, 
					1);
		}
		
		generateThumbsCovers();
	}
	
	public void generateThumbsCovers() {
		if (videoPath != null) {
			
			ZDialog.show(R.layout.progressdialog, false, true, this,false);
			final double durationStep = videoDuration / (COVER_NUM - 1);
			Log.d(TAG,"durationStep = " + durationStep);
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
							ZDialog.dismiss();
						}
					});
				}
			}).start();
		}
	}
	
	private void smoothScroll(View view) {
		int[] location = new int[2];
	    view.getLocationOnScreen(location);
	    int x = location[0];
	    
	    if (x - 2 * VIEW_SPACING * dm.density <= 0) {
	    	horiScrView.smoothScrollBy((int)(-1 * VIEW_SPACING * dm.density) - view.getWidth(),0);
	    } else if (x + view.getWidth() + 2 * VIEW_SPACING * dm.density >= dm.widthPixels) {
	    	horiScrView.smoothScrollBy((int)(VIEW_SPACING * dm.density) + view.getWidth(), 0);
	    }
	}
	
	public Bitmap getVideoCover(double time) {
		Bitmap bmp = null;
		if (mMediaPlayer != null) {
			bmp = mMediaPlayer.videScreenCapture(time,80,144);
			if (bmp == null) {
				if (Math.abs(time - videoDuration) < 0.1) {
					bmp = mMediaPlayer.videScreenCapture(time - 0.2,80,144);
				}
			}
		}
		return bmp;
	}
	
	private class MyOnInfoListener implements OnInfoListener {

		@Override
		public void onStartPlayer(XMediaPlayer player) {
			Log.e(TAG, "onStartPlayer");
//			ivPlay.setImageResource(R.drawable.zanting);
			ivPlay.setVisibility(View.INVISIBLE);
			ivVideoCover.setVisibility(View.GONE);
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
//			ivPlay.setImageResource(R.drawable.play);
			ivPlay.setVisibility(View.VISIBLE);
			montageView.setCurTime(0);
//			onClick(rlVideoContent);
			ivVideoCover.setVisibility(View.VISIBLE);
		}

		@Override
		public void onUpdateTime(XMediaPlayer player, double time) {
			final double time1 = time;
			runOnUiThread(new Runnable() {
				
				@Override
				public void run() {
					// TODO Auto-generated method stub
					montageView.setCurTime(time1);
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
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		try {
			if(mMediaPlayer!=null){
				mMediaPlayer.release();
				mEffects.release();
				mMediaPlayer=null;
				mEffects = null;
				isPlayerNeedToPlay = false;
				isPlayerPrepared = false;
			}
			mCoverEffects.release();
			
			for (int i = 0;i < effectBeans.size();i++) {
				RelativeLayout layout = (RelativeLayout)findViewById(i);
				if (layout != null && layout.getTag() != null) {
					EffectWrapper wrapper = (EffectWrapper) layout.getTag();
					if (wrapper != null && wrapper.effectBmp != null && !wrapper.effectBmp.isRecycled()) {
						wrapper.effectBmp.recycle();
						wrapper.effectBmp = null;
					}
					
					ImageView effectImage= (ImageView) layout.findViewById(R.id.effect_image);
					if (effectImage != null) {
						BitmapDrawable mDrawable =  (BitmapDrawable) effectImage.getDrawable();
						if (mDrawable != null) {
							Bitmap image = mDrawable.getBitmap();
							BitmapUtils.releaseBmp(image);
						}
					}
				}
			}
			BitmapUtils.releaseBmp(videoCoverBmp);
			BitmapUtils.releaseBmp(defaultBmp);
			BitmapUtils.releaseBmps(mBmps);
		} catch (Exception e) {
			e.printStackTrace();
		}
		super.onDestroy();
	}
	
	@Override
	public boolean handleMessage(Message msg) {
		// TODO Auto-generated method stub
		switch(msg.what) {
		case EffectTransCodeUtil.HANDLER_PROCESS_EFFECTS_FINISH:
			Log.d(TAG,"VideoEffectTransCodeUtil.HANDLER_PROCESS_EFFECTS_FINISH arg1 = " + msg.arg1);
			if (msg.arg1 == EffectTransCodeUtil.MAIN_VIDEO) {
				if (msg.obj != null) {
					effectVideoPath = (String) msg.obj;
				}
				processSignal.countDown();
			}
			ZDialog.dismiss();
			break;
		case EffectTransCodeUtil.HANDLER_PROCESS_EFFECTS_SCHEDULE:
			int percent = msg.arg2;
			Log.d(TAG,"VideoEffectTransCodeUtil.HANDLER_PROCESS_EFFECTS_SCHEDULE percent = " + percent);
			if (dialogTitle != null) {
				dialogTitle.setText(getString(R.string.process_video_effect) + " " + percent + "%");
			}
			break;
		/*case DiaryController.DIARY_REQUEST_DONE:
			Log.d(TAG,"DiaryController.DIARY_REQUEST_DONE in");
			MyDiary diary = ((DiaryWrapper) msg.obj).diary;
			MyDiary localDiary = DiaryManager.getInstance().findMyDiaryByUUID(diary.diaryuuid);
			ZDialog.dismiss();
			Intent intent = new Intent(DiaryPreviewActivity.DIARY_EDIT_DONE);
			intent.putExtra(DiaryPreviewActivity.INTENT_ACTION_DIARY_UUID, diaryUUID);
			LocalBroadcastManager.getInstance(MainApplication.getAppInstance()).sendBroadcast(intent);
			finish();
			break;*/
		}
		return false;
	}

	@Override
	public void onClick(View view) {
		// TODO Auto-generated method stub
		switch(view.getId()) {
		case R.id.iv_edit_diary_back:
			finish();
			break;
		case R.id.iv_edit_diary_save:
//			finishDialog.show();
			if (mMediaPlayer.getStatus() == XEffectMediaPlayer.STATUS_PALYING) {
				mMediaPlayer.stop();
			}
			modifyMainAttach();
			break;
		case R.id.fl_edit_diary_video:
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
//					hidePlayBtn();
					ivVideoCover.setVisibility(View.GONE);
				} else {
					isPlayerNeedToPlay = true;
				}
			} else if (mMediaPlayer.getStatus() == XEffectMediaPlayer.STATUS_PALYING) {
				mMediaPlayer.pause();
//				ivPlay.setImageResource(R.drawable.play);
				ivPlay.setVisibility(View.VISIBLE);
//				hidePlayBtn();
			} else if (mMediaPlayer.getStatus() == XEffectMediaPlayer.STATUS_PAUSE) {
				mMediaPlayer.resume();
//				ivPlay.setImageResource(R.drawable.zanting);
				ivPlay.setVisibility(View.INVISIBLE);
//				hidePlayBtn();
			}
			break;
//		case R.id.video_view:
//			ivPlay.setVisibility(View.VISIBLE);
//			hidePlayBtn();
//			break;
//		case R.id.replace:
//			finishDialog.dismiss();
//			modifyMainAttach();
//			break;
//		case R.id.save_as:
//			finishDialog.dismiss();
//			break;
//		case R.id.cancel:
//			finishDialog.dismiss();
//			break;
		default:
			int viewId = view.getId();
			
			if (viewId >= 0 && viewId < effectBeans.size()) {
				EffectWrapper wrapper = (EffectWrapper) view.getTag();
				ivSelectedView.setVisibility(View.INVISIBLE);
				ivSelectedView = wrapper.effectSelectedView;
				videoCoverBmp = wrapper.effectBmp;
				if (videoCoverBmp != null) {
					ivVideoCover.setImageBitmap(videoCoverBmp);
				}
				ivSelectedView.setVisibility(View.VISIBLE);
				if (viewId == diaryEditNote.effectIndex) {
					ivDone.setEnabled(false);
				} else {
					ivDone.setEnabled(true);
				}
				switchEffect(viewId);
				smoothScroll(view);
			}
			
			curEffectId = viewId;
			HashMap<String,String> map = new HashMap<String, String>();
			map.put("label", String.valueOf(curEffectId));
			map.put("label2", DiaryEditPreviewActivity.isFromShootting ? "2" : "1");
			Log.d(TAG,"isFromShootting = " + DiaryEditPreviewActivity.isFromShootting);
			// 2014-4-8
			CmmobiClickAgentWrapper.onEvent(this, "edit_effect1", map);
			break;
		}
	}
	
	private void modifyMainAttach() {
		Dialog dialog = ZDialog.show(R.layout.progressdialog, false, true, this,true);
		dialogTitle = (TextView) dialog.findViewById(R.id.progress_dialog_tv);
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				if (mEffects != null && mEffects.getEffectsCount() > 0) {
					String infilename = videoPath;
					Log.d(TAG,"infilename =  " + infilename);
					if (infilename != null) {
						EffectTransCodeUtil transCode = new EffectTransCodeUtil(handler, mEffects, infilename, EditVideoEffectActivity.this,EffectTransCodeUtil.MAIN_VIDEO);
						transCode.start("video");
						try {
							processSignal.await();
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}
				myDiary.modifyMainAttach(effectVideoPath, "");
				/*String imageID = DiaryController.getNextUUID();
				String videoCoverPath = DiaryController.getAbsolutePathUnderUserDir()  +  "/pic/" + imageID + ".jpg";
				if(BitmapUtils.saveBitmap2file(videoCoverBmp, videoCoverPath)) {
					Log.d(TAG,"saveBitmap2file sucessed");
				}
				myDiary.modifyVideoCover(videoCoverPath);*/
				Log.d(TAG,"effectVideoPath = " + effectVideoPath);
				
				EditVideoEffectActivity.this.runOnUiThread(new Runnable() {
					
					@Override
					public void run() {
						// TODO Auto-generated method stub
						ZDialog.dismiss();
						String diaryString = new Gson().toJson(myDiary);
						Intent intent = new Intent();
						intent.putExtra(DiaryEditPreviewActivity.INTENT_ACTION_DIARY_STRING, diaryString);
						intent.putExtra(DiaryEditPreviewActivity.INTENT_ACTION_MEDIA_EFFECT, curEffectId);
						setResult(RESULT_OK, intent);
						finish();
					}
				});
				
				/*String attachID = "";
				String attachUUID = "";
				MainAttach mainAttach = myDiary.attachs.levelattach;
				if (mainAttach != null) {
					attachID = mainAttach.attachid;
					attachUUID = mainAttach.attachuuid;
				}
				Attachs attach = null;
				attach = DiaryController.getInstanse().createNewAttach(attachID, attachUUID, GsonProtocol.ATTACH_TYPE_VIDEO, GsonProtocol.SUFFIX_MP4, GsonProtocol.ATTACH_LEVEL_MAIN, GsonProtocol.ATTACH_OPERATE_TYPE_UPDATE, "",effectVideoPath);
				Attachs[] attachs = new Attachs[1];
				attachs[0] = attach;
				DiaryController.getInstanse().updateDiary(handler, myDiary, attachs);
				DiaryController.getInstanse().diaryContentIsReady(diaryUUID);
				Log.d(TAG,"updateDiary in");*/
			}
		}).start();
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

	@Override
	public void videoMontageValueChanged(int Thumb1Value, int Thumb2Value) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void noticeWidth(int montageWidth, int seekWidth) {
		// TODO Auto-generated method stub
		this.seekWidth = seekWidth;
	}

	@Override
	public void seekBarCropUp(int thumb1Value, int thumb2Value) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setMontageEnable(boolean enable) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void seekBarChange(int curPos) {
		// TODO Auto-generated method stub
		double curTime = curPos * videoDuration / seekWidth;

		Log.d(TAG,"seekBarChange in curTime = " + curTime + " curPos = " + curPos + " seekWidth = " + seekWidth);
		if (mMediaPlayer != null && curTime < videoDuration) {
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
			
		} 
	}

	@Override
	public boolean isMediaPlaying() {
		// TODO Auto-generated method stub
		return mMediaPlayer.isPlaying();
	}
	
	private class EffectWrapper {
		int effectId;
		Bitmap effectBmp;
		ImageView effectSelectedView;
	}

}
