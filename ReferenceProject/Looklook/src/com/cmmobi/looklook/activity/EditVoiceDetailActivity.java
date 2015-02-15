package com.cmmobi.looklook.activity;

import java.io.File;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import android.app.Dialog;
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
import android.widget.RelativeLayout.LayoutParams;
import android.widget.SeekBar;
import android.widget.TextView;
import cn.zipper.framwork.core.ZDialog;

import com.cmmobi.looklook.R;
import com.cmmobi.looklook.common.constant.Constant;
import com.cmmobi.looklook.common.gson.GsonResponse2;
import com.cmmobi.looklook.common.gson.GsonResponse2.MyDiary;
import com.cmmobi.looklook.common.gson.GsonResponse2.createStructureResponse;
import com.cmmobi.looklook.common.gson.GsonResponse2.modTagsOrPositionResponse;
import com.cmmobi.looklook.common.gson.Requester2;
import com.cmmobi.looklook.common.utils.CmmobiClickAgentWrapper;
import com.cmmobi.looklook.common.utils.EffectTransCodeUtil;
import com.cmmobi.looklook.common.utils.UmengclickAgentWrapper;
import com.cmmobi.looklook.common.view.MyPositionLayout;
import com.cmmobi.looklook.fragment.EditMediaBeautifySoundFragment;
import com.cmmobi.looklook.fragment.EditMediaPositionFragment;
import com.cmmobi.looklook.fragment.EditMediaSoundtrackFragment;
import com.cmmobi.looklook.fragment.EditMediaTagFragment;
import com.cmmobi.looklook.fragment.EditMediaTextInputFragment;
import com.cmmobi.looklook.fragment.EditVoiceDetailMainFragment;
import com.cmmobi.looklook.fragment.EditVoiceMainFragment;
import com.cmmobi.looklook.fragment.EditVoiceMontageFragment;
import com.cmmobi.looklook.info.location.MyLocationInfo;
import com.cmmobi.looklook.info.profile.ActiveAccount;
import com.cmmobi.looklook.info.profile.DiaryManager;
import com.cmmobi.looklook.info.profile.TimeHelper;
import com.cmmobi.looklook.map.MyMapView;
import com.cmmobivideo.utils.EffectUtils;
import com.cmmobivideo.utils.PluginUtils;
import com.cmmobivideo.workers.XMediaPlayer;
import com.cmmobivideo.workers.XMediaPlayer.OnInfoListener;

import effect.XEffectMediaPlayer;
import effect.XEffects;
import effect.XMp4Box;

public class EditVoiceDetailActivity extends EditMediaDetailActivity implements OnClickListener, Callback{

	private final String TAG = "EditVoiceDetailActivity";
	private FragmentManager fm;
	private EditVoiceDetailMainFragment detailMainFragment;
	private EditMediaTagFragment tagFragment;
	private EditMediaPositionFragment posFragment;
	private EditMediaBeautifySoundFragment soundFragment;
	private EditMediaTextInputFragment textFragment;
	private EditVoiceMainFragment mainFragment;
	private EditVoiceMontageFragment montageFragment;
	private EditMediaSoundtrackFragment soundtrackFragment;
	
	public RelativeLayout mainLayout = null;
	private RelativeLayout rlAudioControl = null;
	public LinearLayout detailMainLayout = null;
	
	public boolean isSelectedDelete = false;
	
	public ImageView ivPlay;// 播放暂停按钮
	private ImageView thumbView;// 详情页磁带图标
	private SeekBar playProcess;//长录音播放进度
	private ImageView seekBarBg = null;//进度条背景
	private int seekBarLength = 0;
	private TextView tvAudioDuration;// audio时长
	private boolean isPlaying = false;
	private String effectAudioPath = null;
	private boolean isPlayerNeedToPlay;
	private boolean isPlayerPrepared;
	public double totalTime = 0;
	public long duration = 0;// 单位为s
	
	public int startValue = 0;
	public int endValue = 0;
	
	public double audioMontageStartTime = 0;
	public double audioMontageEndTime = 0;
	
	public final static int FRAGMENT_CROP_VIEW = 0xff;// 音频剪辑fragment
	public final static int FRAGMENT_SOUNDTRACK_VIEW = 0xfe;// 配音fragment
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_edit_voice_detail);
		
		detailMainFragment = new EditVoiceDetailMainFragment();
		tagFragment = new EditMediaTagFragment();
		posFragment = new EditMediaPositionFragment();
		soundFragment = new EditMediaBeautifySoundFragment();
		textFragment = new EditMediaTextInputFragment();
		mainFragment = new EditVoiceMainFragment();
		montageFragment = new EditVoiceMontageFragment();
		soundtrackFragment = new EditMediaSoundtrackFragment();
		
		fm = getSupportFragmentManager();
		
		ivBack = (ImageView) findViewById(R.id.iv_back);
		ivDone = (ImageView) findViewById(R.id.iv_edit_done);
		ivBack.setOnClickListener(this);
		ivDone.setOnClickListener(this);
		
		myLocInfo = MyLocationInfo.getInstance(this);
		myLoc =  myLocInfo.getLocation();

		initView(this);
		
		mMapView = (MyMapView) findViewById(R.id.bmv_activity_edit_voice_detail_position);
		mMapView.setVisibility(View.GONE);
		myPositionLayout = (MyPositionLayout) findViewById(R.id.rl_activity_edit_media_my_position);
		myPositionLayout.setVisibility(View.GONE);
		myPositionLayout.setWillNotDraw(false);
		myPositionTv = (TextView) findViewById(R.id.tv_edit_media_myposition);
		myPositionTv.setVisibility(View.GONE);
		
		mainLayout = (RelativeLayout) findViewById(R.id.rl_activity_edit_voice_main);
		mainLayout.setOnClickListener(this);
		detailMainLayout = (LinearLayout) findViewById(R.id.ll_activity_edit_voice_detail_main);
		thumbView = (ImageView) findViewById(R.id.iv_edit_voice_thumbnails);
		thumbView.setOnClickListener(this);
//		recPlayerView = (ImageView) findViewById(R.id.iv_edit_long_rec_player);
//		recPlayerView.setOnClickListener(this);
		
		diaryUUID = getIntent().getStringExtra(
				DiaryDetailActivity.INTENT_ACTION_DIARY_UUID);
		diaryString = getIntent().getStringExtra(
				DiaryDetailActivity.INTENT_ACTION_DIARY_STRING);
		
		initAttachs();
		
		tvAudioDuration = (TextView) findViewById(R.id.tv_edit_voice_duration);
		if (mainAudioDuration != null) {
			tvAudioDuration.setText(EditVoiceMainFragment.convertTimeFormartMS("0") 
					+ "/" + EditVoiceMainFragment.convertTimeFormartMS(mainAudioDuration));
		}
		
		loadAudioData();
		
		ivPlay = (ImageView) findViewById(R.id.iv_edit_voice_play);
		ivPlay.setOnClickListener(this);
		
		playProcess=(SeekBar) findViewById(R.id.sk_edit_voice_seekbar);
		playProcess.setMax(100);
		playProcess.setProgress(0);
		seekBarBg = (ImageView) findViewById(R.id.v_seekbar_background);
		ViewTreeObserver vto2 = seekBarBg.getViewTreeObserver();   
		vto2.addOnGlobalLayoutListener(new OnGlobalLayoutListener() {

			@Override
			public void onGlobalLayout() {
				// TODO Auto-generated method stub
				seekBarLength = seekBarBg.getMeasuredWidth();
			} 
			
		});
		
		llXiaLa = (LinearLayout) findViewById(R.id.ll_edit_diary_style);
		llXiaLa.setOnClickListener(this);
		diaryEditStyle = (TextView) findViewById(R.id.tv_edit_name);
		inflater = LayoutInflater.from(this);
		rlTitleBar = findViewById(R.id.rl_title);
		
		handler = new Handler(this);
		
		if (mainAudioPath == null) {
			thumbView.setVisibility(View.INVISIBLE);
			thumbView.setClickable(false);
		}
		goToPage(FRAGMENT_DETAIL_MAIN_VIEW, false);
		getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN|WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
		
		overridePendingTransition(R.anim.slide_in_right,
                R.anim.slide_out_left);
	}
	
	private void loadAudioData() {
		
		effectUtils = new EffectUtils(this);
		effectUtils.parseXml("effectcfg/effectlist.xml");
		
		if (PluginUtils.isPluginMounted()) {
			mEffects = new XEffects();
		}
		
		if(null==mMediaPlayer){
			mMediaPlayer = new XMediaPlayer(this, mEffects, true);
			mMediaPlayer.setUpdateTimePeriod(0.1f);
			mMediaPlayer.setListener(new MyOnInfoListener());
			if (mainAudioPath != null) {
				mMediaPlayer.open(mainAudioPath);
				totalTime = mMediaPlayer.getTotalTime();
				audioMontageEndTime = totalTime;
				duration = (int) (totalTime);
				montageEndTime = (float) totalTime;
				tvAudioDuration.setText(EditVoiceMainFragment.convertTimeFormartMS("0") 
						+ "/" + EditVoiceMainFragment.convertTimeFormartMS(String.valueOf(duration)));
			}
		}
	}
	
	private class MyOnInfoListener implements OnInfoListener {

		@Override
		public void onStartPlayer(XMediaPlayer player) {
			Log.e(TAG, "onStartPlayer");
//			totalTime = player.getTotalTime();
//			duration = (long)totalTime;
//			montageEndTime = duration;
//			mainAudioDuration = String.valueOf(duration);
		}

		@Override
		public void onStopPlayer(XMediaPlayer player) {
			Log.e(TAG, "onStopPlayer");
		}

		@Override
		public void OnFinishPlayer(XMediaPlayer player) {
			Log.e(TAG, "OnFinishPlayer");
			ivPlay.setImageResource(R.drawable.btn_edit_audio_play);
		}

		@Override
		public void onUpdateTime(XMediaPlayer player, double time) {
			Log.e(TAG, "onUpdateTime time = " + time + " audioMontageEndTime = " + audioMontageEndTime + " audioMontageStartTime = " + audioMontageStartTime);
			
			if (player.getStatus() == XEffectMediaPlayer.STATUS_STOP) {
				time = audioMontageStartTime;
			}
			if (time >= audioMontageEndTime) {
				mMediaPlayer.pause();
				mMediaPlayer.seek(audioMontageStartTime);
				ivPlay.setImageResource(R.drawable.btn_edit_audio_play);
			}
			int process = (int) ((time - audioMontageStartTime) * 100 / (audioMontageEndTime - audioMontageStartTime));
			playProcess.setProgress(process);
			setAudioCurrentTime((int)time);
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
	public void onResume() {
		mMapView.onResume();
		super.onResume();
		UmengclickAgentWrapper.onResume(this);
		CmmobiClickAgentWrapper.onResume(this);
	}
	
	public boolean isMainAttachChanged() {
		return (isDiaryMontaged || isAddSoundTrack);
	}
	
	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		switch(arg0.getId()) {
		/*case R.id.ll_edit_media_sound_distinguish:
			goToPage(FRAGMENT_TEXT_INPUT, false);
			edit.setHorizontallyScrolling(false);
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
			mMediaPlayer.stop();
			shortRecMediaPlayer.stop();
			dialogTitle = (TextView) dialog.findViewById(R.id.progress_dialog_tv);
			isMainAttachChanged = isMainAttachChanged();
			if (getString(R.string.edit_media_cover).equals(getEditDiaryStyle())) {
				if (isDiaryCreated()) {
					modTagsOrPosition();
					addAttach();
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
			showDuplicateDiaryName();
			break;
		case R.id.iv_edit_voice_play:
			if(mMediaPlayer!=null && mainAudioPath != null){
				int status = mMediaPlayer.getStatus();
				Log.d(TAG,"mediapStatus = " + status);
				if (status == XEffectMediaPlayer.STATUS_UNKNOW || status == XEffectMediaPlayer.STATUS_STOP || status == XEffectMediaPlayer.STATUS_OPENED) {
					if (status == XEffectMediaPlayer.STATUS_UNKNOW) {
						if (videoAttachPath != null) {
							isPlayerPrepared = false;
							mMediaPlayer.open(mainAudioPath);
						}
					}
					if (isPlayerPrepared) {
						mMediaPlayer.play();
						if (audioMontageStartTime > 0.1) {
							mMediaPlayer.seek(audioMontageStartTime);
						}
					} else {
						isPlayerNeedToPlay = true;
					}
					ivPlay.setImageResource(R.drawable.btn_edit_audio_pause);
				} else if (mMediaPlayer.getStatus() == XEffectMediaPlayer.STATUS_PALYING) {
					mMediaPlayer.pause();
					ivPlay.setImageResource(R.drawable.btn_edit_audio_play);
				} else if (mMediaPlayer.getStatus() == XEffectMediaPlayer.STATUS_PAUSE) {
					ivPlay.setImageResource(R.drawable.btn_edit_audio_pause);
					mMediaPlayer.resume();
				}
			}else{
				Log.e(TAG, "mMediaPlayer  is null");
			}
			break;
		case R.id.ll_biezhen:
			if (shortRecMediaPlayer.isPlaying()) {
				tackView.stopAudio(shortRecMediaPlayer);
			} else {
				tackView.playAudio(audioPath,shortRecMediaPlayer);
			}
			break;
		case R.id.iv_edit_voice_thumbnails:
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
		case R.id.rl_activity_edit_voice_main:
			if (fragmentType == FRAGMENT_DETAIL_MAIN_VIEW 
				|| fragmentType == FRAGMENT_TAG_VIEW 
				|| fragmentType == FRAGMENT_TEXT_INPUT) {
				mainLayout.setVisibility(View.GONE);
				detailMainLayout.setVisibility(View.VISIBLE);
				if (mMediaPlayer.getStatus() == XEffectMediaPlayer.STATUS_PALYING) {
					mMediaPlayer.pause();
				}
				ivPlay.setImageResource(R.drawable.btn_edit_audio_play);
				break;
			}
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
			dst = detailMainFragment;
		} else if (type == FRAGMENT_TAG_VIEW) {
			dst = tagFragment;
		} else if (type == FRAGMENT_POS_VIEW) {
			dst = posFragment;
		} else if (type == FRAGMENT_SOUND_VIEW) {
			dst = soundFragment;
		} else if (type == FRAGMENT_MAIN_VIEW) {
			dst = mainFragment;
		} else if (type == FRAGMENT_CROP_VIEW) {
			if (thumbView.isShown()) {
				onClick(thumbView);
			}
			dst = montageFragment;
		} else if (type == FRAGMENT_SOUNDTRACK_VIEW) {
			dst = soundtrackFragment;
			if (thumbView.isShown()) {
				onClick(thumbView);
			}
		} else if (type == FRAGMENT_TEXT_INPUT) {
			dst = textFragment;
		} else {
			return;
		}

		fragmentType = type;
		FragmentTransaction ft = fm.beginTransaction();

		if (fm.findFragmentById(R.id.fl_edit_voice_detail_fragment) != null) {
			ft.replace(R.id.fl_edit_voice_detail_fragment, dst, mViewName);
		} else {
			ft.add(R.id.fl_edit_voice_detail_fragment, dst, mViewName);
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
		mMapView.destroy();
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
	
	@Override
	public boolean handleMessage(Message msg) {
		// TODO Auto-generated method stub
		switch(msg.what) {
		case Requester2.RESPONSE_TYPE_MODIFY_TAG:
			Log.d(TAG,"handleMessage  RESPONSE_TYPE_MODIFY_TAG");
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
		case EditMediaDetailActivity.HANDLER_DISMISS_PROCESS_DIALOG:
			saveLocalDiaryAndFinish();
			break;
		case EditMediaDetailActivity.HANDLER_SOUND_DELETE:
			isDeleteSound = true;
			isAddSoundAttach = false;
			hasShortSound = false;
			checkAuxiliaryAttachEmpty();
			detailMainFragment.processDelShortSoundMsg();
			break;
		/*case DiaryDetailActivity.HANDLER_UPDATE_LONG_RECORD_PLAYER_COMPLETE:
			isPlaying = false;
			ivPlay.setImageResource(R.drawable.btn_edit_audio_play);
			playProcess.setProgress(0);
			setAudioCurrentTime(0);
			break;
		case DiaryDetailActivity.HANDLER_UPDATE_LONG_RECORD_PLAYER_PROCESS:
			if(playProcess!=null) {
				int progress = (Integer)msg.obj;
				playProcess.setProgress(progress);
				setAudioCurrentTime(progress);
			}
			break;*/
		case EffectTransCodeUtil.HANDLER_PROCESS_EFFECTS_FINISH:
			Log.d(TAG,"VideoEffectTransCodeUtil.HANDLER_PROCESS_EFFECTS_FINISH");
			if (msg.arg1 == EffectTransCodeUtil.MAIN_AUDIO) {
				if (msg.obj != null) {
					effectAudioPath = (String) msg.obj;
				}
				threadsSignal.countDown();
				Log.d(TAG,"HANDLER_PROCESS_EFFECTS_FINISH SHORT_AUDIO audioPath = " + audioPath);
			} else if (msg.arg1 == EffectTransCodeUtil.SHORT_AUDIO) {
				if (msg.obj != null) {
					audioPath = (String) msg.obj;
				}
				shortAudioSignal.countDown();
				Log.d(TAG,"HANDLER_PROCESS_EFFECTS_FINISH SHORT_AUDIO audioPath = " + audioPath);
			}
			break;
		case EffectTransCodeUtil.HANDLER_PROCESS_EFFECTS_SCHEDULE:
			int percent = msg.arg2;
			Log.d(TAG,"VideoEffectTransCodeUtil.HANDLER_PROCESS_EFFECTS_SCHEDULE percent = " + percent);
			if (dialogTitle != null) {
				String titel = "";
				if (msg.arg1 == EffectTransCodeUtil.MAIN_AUDIO) {
					titel = getString(R.string.process_long_record_effect) + " " + percent + "%";
				} else if (msg.arg1 == EffectTransCodeUtil.SHORT_AUDIO) {
					titel = getString(R.string.process_short_record_effect) + " " + percent + "%";
				}
				dialogTitle.setText(titel);
			}
			break;
		}
		return false;
	}
	
	private void setAudioCurrentTime(int curTime) {
		String curTimeStr = EditVoiceMainFragment.convertTimeFormartMS(String.valueOf(curTime));
		String totalDuration = EditVoiceMainFragment.convertTimeFormartMS(String.valueOf(duration));
		tvAudioDuration.setText(curTimeStr + "/" + totalDuration);
	}

	@Override
	protected void modifyMainAttach() {
		if (isDiaryMontaged) {
			String inputFile = mainAudioPath;
			long currentTime = TimeHelper.getInstance().now();
			String voiceID = String.valueOf(currentTime);
			String RECORD_FILE_DIR =  Constant.SD_STORAGE_ROOT + "/" + ActiveAccount.getInstance(this).getLookLookID() + "/audio";
			String outputFile = Environment.getExternalStorageDirectory() + RECORD_FILE_DIR + "/" + voiceID +  "/" + voiceID + ".mp4" ;
		
			File dstFile = new File(outputFile);
	        if(!dstFile.getParentFile().exists()) {
	        	dstFile.getParentFile().mkdirs();
			}
	        editAttachPath = outputFile;
	        montageAudio(voiceID, inputFile, outputFile);//1s
		}
		
		if (mEffects.getEffectsCount() > 0) {
			try {
				
				String infilename = "";
				if (editAttachPath != null) {
					infilename = editAttachPath;
				} else {
					infilename = mainAudioPath;
				}
				Log.d(TAG,"infilename = " + infilename);
				EffectTransCodeUtil transCode = new EffectTransCodeUtil(handler, mEffects, infilename, this,EffectTransCodeUtil.MAIN_AUDIO);
				transCode.start("audio");
				threadsSignal = new CountDownLatch(1);
				threadsSignal.await();
				
				if (effectAudioPath != null) {
					if (editAttachPath != null) {
						EditMediaDetailActivity.delFile(editAttachPath);
					}
					editAttachPath = effectAudioPath;
				}
				
				Log.d(TAG,"editAttachPath = " + editAttachPath + " effectAudioPath = " + effectAudioPath);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	private void montageAudio(String videoID,String inputFile,String outputFile) {
		XMp4Box mMp4Box = new XMp4Box();
		if (isSelectedDelete) {
			String RECORD_FILE_DIR =  Constant.SD_STORAGE_ROOT + "/" + ActiveAccount.getInstance(this).getLookLookID() + "/audio";
			String outputFile1 = Environment.getExternalStorageDirectory() + RECORD_FILE_DIR + "/" + videoID +  "/" + "temp1.mp4";
			int ret1 = mMp4Box.splitFile(inputFile,outputFile1,0,montageStartTime);
			String outputFile2 = Environment.getExternalStorageDirectory() + RECORD_FILE_DIR + "/" + videoID +  "/" + "temp2.mp4";
			float videoDuration = (float) (montageFragment.totalTime);
			int ret2 = mMp4Box.splitFile(inputFile,outputFile2,montageEndTime,videoDuration);
			Log.d(TAG,"ret1 = " + ret1 + " ret2 = " + ret2);
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
	
	@Override
	public String getMontageDuration() {
		float duration = 1;
		if (isSelectedDelete) {
			float attachDuration = (float) (montageFragment.totalTime);
			duration = montageStartTime + (attachDuration - montageEndTime);
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
		if (mainAudioPath == null) {
			return;
		}
		String audioID = String.valueOf(TimeHelper.getInstance().now());
		String dstFilePath = Environment.getExternalStorageDirectory().getPath() + Constant.SD_STORAGE_ROOT + "/" 
				+ ActiveAccount.getInstance(this).getLookLookID() + "/audio/" + audioID + "/" + audioID + ".mp4";
		
		long fileLength = copyFile2(mainAudioPath, dstFilePath);
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
	
	public void seekBarCropUp(int thumb1,int thumb2,int totalWidth) {
		if (mMediaPlayer != null) {
			mMediaPlayer.pause();
			ivPlay.setImageResource(R.drawable.btn_edit_audio_play);
		}
		int width = (int)((((float) (thumb2 - thumb1)) / (float)totalWidth) * seekBarLength);
		
		int leftMargin = (int)(((float)thumb1 / totalWidth) * seekBarLength);
		audioMontageStartTime = (float) (((float)thumb1 / totalWidth) * totalTime);
		audioMontageEndTime = (float) (((float)thumb2 / totalWidth) * totalTime);
		Log.d(TAG,"width = " + width + " thumb1 = " + thumb1 + " thumb2 = " 
			    + thumb2 + " totalWidth = " + totalWidth + " seekBarLength = " 
				+ seekBarLength + " leftMargin = " + leftMargin + " startTime = " 
			    + audioMontageStartTime + " endTime = " + audioMontageEndTime);
		
		mMediaPlayer.seek(audioMontageStartTime);
		setAudioCurrentTime((int)audioMontageStartTime);
		 
		RelativeLayout.LayoutParams params = new LayoutParams(width,
				LayoutParams.WRAP_CONTENT);
		params.leftMargin = leftMargin;
		playProcess.setLayoutParams(params);
		
		playProcess.requestLayout();
		playProcess.setProgress(0);
	}
	
}
