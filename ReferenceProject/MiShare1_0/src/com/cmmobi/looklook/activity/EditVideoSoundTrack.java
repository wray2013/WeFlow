package com.cmmobi.looklook.activity;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnErrorListener;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.SeekBar.OnSeekBarChangeListener;
import cn.zipper.framwork.core.ZActivity;
import cn.zipper.framwork.core.ZApplication;
import cn.zipper.framwork.core.ZDialog;
import cn.zipper.framwork.core.ZLayoutInflater;
import cn.zipper.framwork.io.file.ZFileSystem;

import com.cmmobi.looklook.MainApplication;
import com.cmmobi.looklook.R;
import com.cmmobi.looklook.common.constant.Constant;
import com.cmmobi.looklook.common.gson.DiaryController;
import com.cmmobi.looklook.common.gson.GsonProtocol;
import com.cmmobi.looklook.common.gson.DiaryController.DiaryWrapper;
import com.cmmobi.looklook.common.gson.GsonRequest3.Attachs;
import com.cmmobi.looklook.common.gson.GsonResponse3.MainAttach;
import com.cmmobi.looklook.common.gson.GsonResponse3.MyDiary;
import com.cmmobi.looklook.common.utils.AudioSoundTrackBean;
import com.cmmobi.looklook.common.utils.BitmapUtils;
import com.cmmobi.looklook.common.utils.CmmobiClickAgentWrapper;
import com.cmmobi.looklook.common.utils.DiaryEditNote;
import com.cmmobi.looklook.common.utils.EffectTransCodeUtil;
import com.cmmobi.looklook.info.profile.AccountInfo;
import com.cmmobi.looklook.info.profile.ActiveAccount;
import com.cmmobi.looklook.info.profile.DiaryManager;
import com.cmmobi.looklook.info.profile.MediaValue;
import com.cmmobi.looklook.prompt.Prompt;
import com.cmmobi.statistics.CmmobiClickAgent;
import com.cmmobivideo.utils.EffectBean;
import com.cmmobivideo.utils.EffectUtils;
import com.cmmobivideo.utils.PluginUtils;
import com.cmmobivideo.workers.XMediaPlayer;
import com.cmmobivideo.workers.XMediaPlayer.OnInfoListener;
import com.google.gson.Gson;

import effect.EffectType;
import effect.XEffectMediaPlayer;
import effect.XEffects;

public class EditVideoSoundTrack extends ZActivity {
	
	private final String TAG = "EditVideoSoundTrack";
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
	private RadioGroup soundTrackGroup = null;
	public EffectUtils effectUtils;
	private ArrayList<AudioSoundTrackBean> trackBeanList = new ArrayList<AudioSoundTrackBean>();
	
	private ImageView ivVideoCover = null;
	private Bitmap videoCoverBmp = null;
	private String videoCoverUrl = null;
//	private Dialog finishDialog;
	private CountDownLatch processSignal = new CountDownLatch(1);
	private String outVideoPath = null;
	private ImageView ivDone = null;
	public TextView dialogTitle;
	private SeekBar volumeSeek;
	private double percent = 0.5;
	private AudioSoundTrackBean trackBean = null;
	public DiaryEditNote diaryEditNote = null;
	public String soundTrackPath = "";
	public int effectId = 0;
	private List<EffectBean> effectBeans = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_edit_video_soundtrack);
		
		videoLayout = (FrameLayout) findViewById(R.id.fl_edit_diary_video);
		dm = getResources().getDisplayMetrics();
		Log.d(TAG,"width = " + dm.widthPixels + " height = " + dm.heightPixels);
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(dm.widthPixels,dm.widthPixels);
		videoLayout.setLayoutParams(params);
		
		soundTrackGroup = (RadioGroup) findViewById(R.id.rg_sound_tracks_group);
		ivPlay = (ImageView) findViewById(R.id.iv_video_play);
		ivPlay.setOnClickListener(this);
		
		findViewById(R.id.fl_edit_diary_video).setOnClickListener(this);
		
		findViewById(R.id.iv_edit_diary_back).setOnClickListener(this);
		ivDone = (ImageView) findViewById(R.id.iv_edit_diary_save);
		ivDone.setOnClickListener(this);
		ivDone.setEnabled(false);
		
		ivVideoCover = (ImageView) findViewById(R.id.iv_video_preview);
		
		loadVideoData();
		
		volumeSeek = (SeekBar) findViewById(R.id.sk_edit_voice_seekbar);
        final int max = volumeSeek.getMax();
        volumeSeek.setProgress((int)(max * percent));
        volumeSeek.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
			
			@Override
			public void onStopTrackingTouch(SeekBar arg0) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onStartTrackingTouch(SeekBar arg0) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onProgressChanged(SeekBar arg0, int arg1, boolean arg2) {
				// TODO Auto-generated method stub
				percent = ((double) volumeSeek.getProgress()/ max);
				if (trackBean != null) {
					effectUtils.addMusic(mEffects, "effectcfg/audio/addmusic.cfg", trackBean.filePath, 
							mMediaPlayer.getChannels() , 
							mMediaPlayer.getSampleRate(), 
							mMediaPlayer.getBitsPerChannel(), 
							percent, 
							1);
					Log.d(TAG,"precent = " + percent);
				}
			}
		});
		
		videoCoverUrl = myDiary.getVideoCoverUrl();
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
		init();
//		initDialog();
	}
	
	/*private void initDialog() {
		finishDialog = new Dialog(this, R.style.networktask_dialog_style_none_background);
		Window w = finishDialog.getWindow();
		WindowManager.LayoutParams lp = w.getAttributes();

		lp.gravity = Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL;
		View view = ZLayoutInflater.inflate(R.layout.dialog_edit_diary_finish_menu,
				null);
		
		view.findViewById(R.id.replace).setOnClickListener(this);
		view.findViewById(R.id.save_as).setOnClickListener(this);
		view.findViewById(R.id.cancel).setOnClickListener(this);
		
		finishDialog.setContentView(view);
		
		finishDialog.getWindow().setBackgroundDrawable(getResources().getDrawable(
				R.drawable.del_dot_big));
		finishDialog.getWindow().setLayout(WindowManager.LayoutParams.FILL_PARENT,
				WindowManager.LayoutParams.WRAP_CONTENT);
	}*/
	
	private void init() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				createFile("hi-pop.mp3");
				createFile("diqu.mp3");
				createFile("huanledejuchilang.mp3");
				createFile("qingkuaidiyinyue.mp3");
				createFile("shuqing.mp3");
				createFile("wuqu.mp3");
				createFile("yaogun.mp3");
				createFile("youshang.mp3");
				createFile("youshanggangqinqu.mp3");
			}
		}).start();
		String preffix = LookLookActivity.SDCARD_PATH +Constant.SD_STORAGE_ROOT + "/" + "sound/";
		trackBeanList.clear();
		trackBeanList.add(new AudioSoundTrackBean(0, "", "原音"));
		trackBeanList.add(new AudioSoundTrackBean(1, preffix + "hi-pop.mp3","hi-pop"));
		trackBeanList.add(new AudioSoundTrackBean(2, preffix + "diqu.mp3","迪曲"));
		trackBeanList.add(new AudioSoundTrackBean(3, preffix + "huanledejuchilang.mp3","欢快的菊次郎"));
		trackBeanList.add(new AudioSoundTrackBean(4, preffix + "qingkuaidiyinyue.mp3","轻快地音乐"));
		trackBeanList.add(new AudioSoundTrackBean(5, preffix + "shuqing.mp3","抒情"));
		trackBeanList.add(new AudioSoundTrackBean(6, preffix + "wuqu.mp3","舞曲"));
		trackBeanList.add(new AudioSoundTrackBean(7, preffix + "yaogun.mp3","摇滚"));
		trackBeanList.add(new AudioSoundTrackBean(8, preffix + "youshang.mp3","忧伤"));
		trackBeanList.add(new AudioSoundTrackBean(9, preffix + "youshanggangqinqu.mp3","忧伤钢琴曲"));
		int size = trackBeanList.size();
		soundTrackGroup.removeAllViews();
		
		for (int i = 0;i < size;i++) {
			AudioSoundTrackBean trackBeanTemp = trackBeanList.get(i);
			LinearLayout layout = (LinearLayout) ZLayoutInflater.inflate(R.layout.include_soundtrack_buttons_stub);
			RadioButton radioButton = (RadioButton) layout.findViewById(R.id.radio_button);
			
			radioButton.setId(trackBeanTemp.trackID);
			radioButton.setText(trackBeanTemp.trackName);
			radioButton.setTag(trackBeanTemp);
			radioButton.setOnClickListener(this);

			layout.removeAllViewsInLayout();
			soundTrackGroup.addView(radioButton);
			
			if (diaryEditNote.isAddSoundTrack && trackBeanTemp.filePath.equals(soundTrackPath)) {
				soundTrackGroup.check(i);
				trackBean = trackBeanTemp;
			}
		}
		
		if (!diaryEditNote.isAddSoundTrack) {
			soundTrackGroup.check(0);
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
		AudioPlayer.stop();
		CmmobiClickAgentWrapper.onStop(this);
		super.onStop();
	}
	
	private void loadVideoData() {
		rlVideoContent=(RelativeLayout) findViewById(R.id.video_view);
//		rlVideoContent.setOnClickListener(this);
		
		effectUtils = new EffectUtils(this);
		if (PluginUtils.isPluginMounted()) {
			mEffects = new XEffects();
		}
		
		userID =  ActiveAccount.getInstance(ZApplication.getInstance()).getUID();
		String diaryString = getIntent().getStringExtra("diarystring");
		myDiary = new Gson().fromJson(diaryString,MyDiary.class);
		String diaryEditString = getIntent().getStringExtra(DiaryEditPreviewActivity.INTENT_ACTION_EDIT_NOTELIST);
		diaryEditNote = new Gson().fromJson(diaryEditString, DiaryEditNote.class);
		effectId = diaryEditNote.effectIndex;
//		diaryUUID = getIntent().getStringExtra("diaryuuid");
//		myDiary = DiaryManager.getInstance().findMyDiaryByUUID(diaryUUID);
//		String videoUrl = myDiary.getMainUrl();
//		
//		MediaValue mediaValue = AccountInfo.getInstance(userID).mediamapping.getMedia(userID, videoUrl);
//		if (mediaValue != null) {
//			videoPath =  Environment.getExternalStorageDirectory() + mediaValue.path;
//		}
//		videoPath = myDiary.getMainPath();
		soundTrackPath = diaryEditNote.soundtrackPath;
		
		videoPath = diaryEditNote.mediaPath;
		outVideoPath = videoPath;
		
		Log.d(TAG,"videoPath = " + videoPath);
		if(null==mMediaPlayer) {
			mMediaPlayer = new XMediaPlayer(this, mEffects, true);
			mMediaPlayer.setUpdateTimePeriod(0.1f);
			mMediaPlayer.setListener(new MyOnInfoListener());
			rlVideoContent.addView((View)mMediaPlayer.getXSurfaceView());
			if (videoPath != null) {
				mMediaPlayer.open(videoPath);
			}
		}
		
		if (diaryEditNote.isAddSoundTrack && !TextUtils.isEmpty(soundTrackPath)) {
			percent = diaryEditNote.percent;
			effectUtils.addMusic(mEffects, "effectcfg/audio/addmusic.cfg", soundTrackPath, 
					mMediaPlayer.getChannels(), 
					mMediaPlayer.getSampleRate(), 
					mMediaPlayer.getBitsPerChannel(), 
					percent, 
					1);
		}
		
		effectUtils.parseXml("effectcfg/effectlist.xml");
		effectBeans = effectUtils.getEffects(EffectBean.TYPE_EFFECTS_VIDEO);
		
		if (effectBeans != null) {
			effectUtils.removeEffectBean(effectBeans,EffectType.KEF_TYPE_MOSAIC);
		}
		
		if (diaryEditNote.isEffect) {
			switchEffect(diaryEditNote.effectIndex);
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
		} 
	}
	
	private class MyOnInfoListener implements OnInfoListener {

		@Override
		public void onStartPlayer(XMediaPlayer player) {
			Log.e(TAG, "onStartPlayer");
//			ivPlay.setImageResource(R.drawable.zanting);
			ivPlay.setVisibility(View.INVISIBLE);
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
			onClick(rlVideoContent);
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
	
	private void createFile(String fileName) {  
		String fileDirPath = LookLookActivity.SDCARD_PATH +Constant.SD_STORAGE_ROOT + "/" + "sound/";
        String filePath = fileDirPath + "/" + fileName;// 文件路径 
        if (ZFileSystem.isFileExists(filePath)) {
        	return;
        }
        try {
            File dir = new File(fileDirPath);// 目录路径  
            if (!dir.exists()) {// 如果不存在，则创建路径名  
                System.out.println("要存储的目录不存在");  
                if (dir.mkdirs()) {// 创建该路径名，返回true则表示创建成功  
                    System.out.println("已经创建文件存储目录");  
                } else {  
                    System.out.println("创建目录失败");  
                }  
            }  
            // 目录存在，则将apk中raw中的需要的文档复制到该目录下  
            File file = new File(filePath);  
            if (!file.exists()) {// 文件不存在  
                System.out.println("要打开的文件不存在");  
                String filename = "effectcfg/audio/" + fileName;
                InputStream ins = getAssets().open(filename);// 通过raw得到数据资源  
                System.out.println("开始读入");  
                FileOutputStream fos = new FileOutputStream(file);  
                System.out.println("开始写出");  
                byte[] buffer = new byte[8192];  
                int count = 0;// 循环写出  
                while ((count = ins.read(buffer)) > 0) {  
                    fos.write(buffer, 0, count);  
                }  
                System.out.println("已经创建该文件");  
                fos.close();// 关闭流  
                ins.close();  
            }  
        } catch (Exception e) {  
            e.printStackTrace();  
        }  
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
			BitmapUtils.releaseBmp(videoCoverBmp);
			System.gc();
		} catch (Exception e) {
			e.printStackTrace();
		}
		super.onDestroy();
	}
	
	
	@Override
	public boolean handleMessage(Message msg) {
		// TODO Auto-generated method stub
		switch(msg.what) {
//		case DiaryController.DIARY_REQUEST_DONE:
//			Log.d(TAG,"DiaryController.DIARY_REQUEST_DONE in");
//			MyDiary diary = ((DiaryWrapper) msg.obj).diary;
//			MyDiary localDiary = DiaryManager.getInstance().findMyDiaryByUUID(diary.diaryuuid);
//			ZDialog.dismiss();
//			Intent intent = new Intent(DiaryPreviewActivity.DIARY_EDIT_DONE);
//			intent.putExtra(DiaryPreviewActivity.INTENT_ACTION_DIARY_UUID, diaryUUID);
//			LocalBroadcastManager.getInstance(MainApplication.getAppInstance()).sendBroadcast(intent);
//			finish();
//			break;
		case EffectTransCodeUtil.HANDLER_PROCESS_EFFECTS_FINISH:
			Log.d(TAG,"VideoEffectTransCodeUtil.HANDLER_PROCESS_EFFECTS_FINISH arg1 = " + msg.arg1);
			if (msg.arg1 == EffectTransCodeUtil.MAIN_VIDEO) {
				if (msg.obj != null) {
					outVideoPath = (String) msg.obj;
				}
				processSignal.countDown();
			}
			break;
		case EffectTransCodeUtil.HANDLER_PROCESS_EFFECTS_SCHEDULE:
			int percent = msg.arg2;
			Log.d(TAG,"VideoEffectTransCodeUtil.HANDLER_PROCESS_EFFECTS_SCHEDULE percent = " + percent);
			if (dialogTitle != null) {
				dialogTitle.setText(getString(R.string.process_video_effect) + " " + percent + "%");
			}
			break;
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
				AudioPlayer.destory();
			} else if (mMediaPlayer.getStatus() == XEffectMediaPlayer.STATUS_PALYING) {
				mMediaPlayer.pause();
//				ivPlay.setImageResource(R.drawable.play);
//				hidePlayBtn();
				ivPlay.setVisibility(View.VISIBLE);
			} else if (mMediaPlayer.getStatus() == XEffectMediaPlayer.STATUS_PAUSE) {
				mMediaPlayer.resume();
//				ivPlay.setImageResource(R.drawable.zanting);
//				hidePlayBtn();
				ivPlay.setVisibility(View.INVISIBLE);
				AudioPlayer.destory();
			}
			break;
		/*case R.id.video_view:
			ivPlay.setVisibility(View.VISIBLE);
			hidePlayBtn();
			break;*/
		/*case R.id.replace:
			finishDialog.dismiss();
			modifyMainAttach();
			break;
		case R.id.save_as:
			finishDialog.dismiss();
			break;
		case R.id.cancel:
			finishDialog.dismiss();
			break;*/
		default:
			int viewId = view.getId();
			if (viewId >= 0 && viewId < trackBeanList.size()) {
				AudioSoundTrackBean trackBeanTemp = null;
				if (view.getTag() != null) {
					trackBeanTemp = (AudioSoundTrackBean) view.getTag();
				}
				
				if (viewId == 0) {
					mEffects.cleanEffects();
					ivDone.setEnabled(false);
					trackBean = null;
				} else if (effectUtils != null && trackBeanTemp != null 
						&& ZFileSystem.isFileExists(trackBeanTemp.filePath)) {
					trackBean = trackBeanTemp;
					effectUtils.addMusic(mEffects, "effectcfg/audio/addmusic.cfg", trackBeanTemp.filePath, 
						mMediaPlayer.getChannels(), 
						mMediaPlayer.getSampleRate(), 
						mMediaPlayer.getBitsPerChannel(), 
						percent, 
						1);
					ivDone.setEnabled(true);
					CmmobiClickAgent.onEvent(this, "background_muisc",String.valueOf(viewId));
					Log.d(TAG,"onclick use soundtrack");
				}
				
				if (mMediaPlayer.getStatus() != XEffectMediaPlayer.STATUS_PALYING) {
					if (viewId == 0) {
						AudioPlayer.stop();
					} else if (trackBean != null && ZFileSystem.isFileExists(trackBean.filePath)) {
						AudioPlayer.stop();
						AudioPlayer.playAudio(trackBean.filePath, handler);
					}
				}
				
				
				if (diaryEditNote.isAddSoundTrack && !TextUtils.isEmpty(diaryEditNote.soundtrackPath)) {
					if (trackBeanTemp.filePath.equals(diaryEditNote.soundtrackPath)) {
						ivDone.setEnabled(false);
					} else {
						ivDone.setEnabled(true);
					}
				}
				soundTrackPath = trackBeanTemp.filePath;
			}
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
						EffectTransCodeUtil transCode = new EffectTransCodeUtil(handler, mEffects, infilename, EditVideoSoundTrack.this,EffectTransCodeUtil.MAIN_VIDEO);
						transCode.start("video");
						try {
							processSignal.await();
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}
				
				myDiary.modifyMainAttach(outVideoPath, "");
				
				EditVideoSoundTrack.this.runOnUiThread(new Runnable() {
					
					@Override
					public void run() {
						// TODO Auto-generated method stub
						ZDialog.dismiss();
						String diaryString = new Gson().toJson(myDiary);
						Intent intent = new Intent();
						intent.putExtra(DiaryEditPreviewActivity.INTENT_ACTION_DIARY_STRING, diaryString);
						intent.putExtra(DiaryEditPreviewActivity.INTENT_ACTION_SOUNDTRACK, soundTrackPath);
						intent.putExtra(DiaryEditPreviewActivity.INTENT_ACTION_SOUND_PERCENT, percent);
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
				attach = DiaryController.getInstanse().createNewAttach(attachID, attachUUID, GsonProtocol.ATTACH_TYPE_VIDEO, GsonProtocol.SUFFIX_MP4, GsonProtocol.ATTACH_LEVEL_MAIN, GsonProtocol.ATTACH_OPERATE_TYPE_UPDATE, "",outVideoPath);
				Attachs[] attachs = new Attachs[1];
				attachs[0] = attach;
				DiaryController.getInstanse().updateDiary(handler, myDiary, attachs);
				DiaryController.getInstanse().diaryContentIsReady(diaryUUID);*/
				Log.d(TAG,"updateDiary in");
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
	
	private static MediaPlayer mp;
	public static class AudioPlayer{
		public static int status = 3;//1-播放中 2-暂停 3-停止
		public static String mPath = null;
		public static void playAudio(String path,final Handler handler){
			Log.d("AudioPlayer", "path="+path);
			
			if(null==path||0==path.length())return;
			if (path.equals(mPath)) {
				mPath = null;
				return;
			}
			if(2==status)
			{
				if(mp!=null)mp.start();
				status = 1;
			}
			else if (3==status)
			{
				try {
					mp=new MediaPlayer();
					mp.setOnCompletionListener(new OnCompletionListener() {
						
						@Override
						public void onCompletion(MediaPlayer mp) {
							stop();
						}
					});
					mp.setOnErrorListener(new OnErrorListener() {
						
						@Override
						public boolean onError(MediaPlayer mp, int what, int extra) {
							return false;
						}
					});
					mp.setDataSource(path);
					mp.prepare();
					mp.start();
					status=1;
				} catch (Exception e) {
					stop();
					Prompt.Alert("您的网络不给力呀");
					e.printStackTrace();
				}
			}
			mPath = path;
		}
		
		public static void pause(){
			if(mp!=null){
				mp.pause();
				status=2;
			}
		}
		
		public static int getDuration() {
			return mp.getDuration();
		}
		
		public static void stop(){
			if(mp!=null){
				mp.release();
				mp=null;
			}
			status=3;
		}
		
		public static void destory() {
			stop();
			mPath = null;
		}
	}
}
