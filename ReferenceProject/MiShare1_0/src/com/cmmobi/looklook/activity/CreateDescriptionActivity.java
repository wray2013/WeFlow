package com.cmmobi.looklook.activity;

import java.util.HashMap;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Environment;
import android.os.Message;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.support.v4.content.LocalBroadcastManager;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import cn.zipper.framwork.core.ZActivity;
import cn.zipper.framwork.core.ZApplication;
import cn.zipper.framwork.core.ZDialog;
import cn.zipper.framwork.core.ZLayoutInflater;
import cn.zipper.framwork.io.file.ZFileSystem;
import cn.zipper.framwork.utils.ZStringUtils;

import com.cmmobi.looklook.Config;
import com.cmmobi.looklook.MainApplication;
import com.cmmobi.looklook.R;
import com.cmmobi.looklook.common.constant.Constant;
import com.cmmobi.looklook.common.gson.DiaryController;
import com.cmmobi.looklook.common.gson.DiaryController.DiaryWrapper;
import com.cmmobi.looklook.common.gson.GsonProtocol;
import com.cmmobi.looklook.common.gson.GsonRequest3.Attachs;
import com.cmmobi.looklook.common.gson.GsonResponse3.AuxAttach;
import com.cmmobi.looklook.common.gson.GsonResponse3.MyDiary;
import com.cmmobi.looklook.common.utils.CmmobiClickAgentWrapper;
import com.cmmobi.looklook.common.utils.EffectTransCodeUtil;
import com.cmmobi.looklook.common.utils.LowStorageChecker;
import com.cmmobi.looklook.common.view.AddExpressionView;
import com.cmmobi.looklook.common.view.EmojiPaser;
import com.cmmobi.looklook.common.view.FriendsExpressionView;
import com.cmmobi.looklook.common.view.VolumeStateView;
import com.cmmobi.looklook.common.web.TackView;
import com.cmmobi.looklook.info.profile.AccountInfo;
import com.cmmobi.looklook.info.profile.ActiveAccount;
import com.cmmobi.looklook.info.profile.DiaryManager;
import com.cmmobi.looklook.prompt.Prompt;
import com.cmmobi.looklook.prompt.TickUpHelper;
import com.cmmobi.statistics.CmmobiClickAgent;
import com.cmmobivideo.utils.EffectBean;
import com.cmmobivideo.utils.EffectUtils;
import com.cmmobivideo.utils.Mp4InfoUtils;
import com.cmmobivideo.utils.PluginUtils;
import com.cmmobivideo.workers.XMediaPlayer;
import com.iflytek.msc.ExtAudioRecorder;
import com.iflytek.msc.ExtAudioRecorder.State;
import com.iflytek.msc.QISR_TASK;

import effect.EffectType;
import effect.XEffects;

public class CreateDescriptionActivity extends ZActivity implements OnTouchListener, OnLongClickListener, OnItemClickListener{

	private final String TAG = "CreateDescriptionActivity";
	private RadioGroup effectGroup = null;
	private ImageView deleteVoiceBtn = null;
	private RelativeLayout voiceLayout = null;
	private TackView voiceTackView = null;
	
	private ImageView ivBack = null;
	private TextView tvDone = null;
	private EditText contentEdit = null;
	private String audioPath = null;
	
	private int[] SOUND_ICONS = {R.drawable.yuyin_bofang_3,R.drawable.yuyin_bofang_2,R.drawable.yuyin_bofang_1};
	private XEffects shortRecEffect;
	private XMediaPlayer shortRecMediaPlayer;
	private EffectUtils shortRecEffectUtils;
	private LinearLayout voiceModifyLayout = null;
	private CountDownLatch shortAudioSignal = new CountDownLatch(1);
	private String diaryUUID = null;
	private String attachUUID = null;
	private MyDiary myDiary = null;
	private ImageView ivRecorder = null;
	
	private boolean hasVoice = false;
	private boolean hasAddVoice = false;
	private boolean hasText = false;
	private boolean isVoiceChanged = false;
	private String userID = null;
	private String outAudioPath = "";
	private String voiceAttachContent = "";
	private FrameLayout translucentLayout = null;
	
	private ImageView ivExpressionBtn = null;
	private AddExpressionView expressionView = null;
	private boolean isLongPressed = false;
	private boolean isOnRecorderButton = false;// 是否还在录音
	private LinearLayout bottomLayout = null;
	private boolean isRecogniseChanged = false;
	private boolean isDoneBtnClick = false;
	private String audioID = "";
	private boolean hasVoiceBefor = false;
	private VolumeStateView shortRecView;
	private ExtAudioRecorder ear;
	private int checkId = 0;
	private DisplayMetrics dm = new DisplayMetrics();
	private HashMap<String,AudioWrapper> audioMap = new HashMap<String, AudioWrapper>();
	
	private class AudioWrapper {
		String audioID;
		String audioPath;
		boolean recogniseDone;
		boolean audioDone;
		String audioContent = "";
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_voice_description_create);
		
		diaryUUID = getIntent().getStringExtra(DiaryPreviewActivity.INTENT_ACTION_DIARY_UUID);
		attachUUID = getIntent().getStringExtra(DiaryPreviewActivity.INTENT_ACTION_ATTACH_UUID);
		userID = ActiveAccount.getInstance(ZApplication.getInstance()).getUID();
		
		myDiary = DiaryManager.getInstance().findMyDiaryByUUID(diaryUUID);
		if (myDiary == null) {
			Prompt.Alert(this, "找不到该日记");
			finish();
			return;
		}
		
		if (attachUUID != null) {
			audioPath = myDiary.getAuxAttachPath(attachUUID);
			voiceAttachContent = myDiary.getAssistAttachTexTContent();
			if (audioPath != null) {
				hasVoice = true;
				hasVoiceBefor = true;
			}
		}
		
		voiceLayout = (RelativeLayout) findViewById(R.id.fl_voice_description);
		deleteVoiceBtn = (ImageView) findViewById(R.id.iv_voice_delete);
		deleteVoiceBtn.setOnClickListener(this);
		voiceTackView = (TackView) findViewById(R.id.ll_tackview);
		voiceTackView.setSoundIcons(SOUND_ICONS);
		voiceTackView.setOnClickListener(this);
		voiceModifyLayout = (LinearLayout) findViewById(R.id.ll_voice_modify);
		effectGroup = (RadioGroup) findViewById(R.id.effects_wrap_layout);
		
		LocalBroadcastManager.getInstance(this).registerReceiver(
				mMessageReceiver,
				new IntentFilter(ExtAudioRecorder.AUDIO_RECORDER_MSG));
		LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver,
			      new IntentFilter(QISR_TASK.QISR_RESULT_MSG));
		ear = ExtAudioRecorder.getInstanse(false, 2, 1);
		shortRecView = new VolumeStateView(this);
		dm = getResources().getDisplayMetrics();
		init();
		
		
		translucentLayout = (FrameLayout) findViewById(R.id.fl_translucent_layout);
		
		bottomLayout = (LinearLayout) findViewById(R.id.ll_activity_create_note);
		
		
		ivRecorder = (ImageView) findViewById(R.id.iv_recorder);
		ivRecorder.setOnTouchListener(this);
		ivRecorder.setOnLongClickListener(this);
		
		ivBack = (ImageView) findViewById(R.id.iv_back);
		ivBack.setOnClickListener(this);
		
		tvDone = (TextView) findViewById(R.id.tv_save);
		tvDone.setOnClickListener(this);
		tvDone.setEnabled(false);
		
		contentEdit = (EditText) findViewById(R.id.et_voice_description_input);
		contentEdit.addTextChangedListener(mTextWatcher);
		
		expressionView = new AddExpressionView(this,contentEdit);
		expressionView.setOnclickListener(this);
		
		ivExpressionBtn = (ImageView) findViewById(R.id.iv_biaoqing_input);
		ivExpressionBtn.setOnClickListener(this);
		
		contentEdit.setOnTouchListener(new OnTouchListener() {
			
			@Override
			public boolean onTouch(View arg0, MotionEvent arg1) {
				// TODO Auto-generated method stub
				Log.d(TAG,"edView onTouch in");
				switch (arg1.getAction()) {
				case MotionEvent.ACTION_DOWN:
					if (isLongPressed) {
						return true;
					}
					break;
				case MotionEvent.ACTION_UP:
					expressionView.hideExpressionView();
					ivExpressionBtn.setImageResource(R.drawable.btn_biaoqing_input);
					
				}
				return false;
			}
		});
		if (!TextUtils.isEmpty(voiceAttachContent)) {
			contentEdit.setText(voiceAttachContent);
			contentEdit.setSelection(voiceAttachContent.length());
		}
		if (!hasVoice) {
			// 隐藏键盘
			findViewById(R.id.text_notuse).requestFocus();
			voiceLayout.setVisibility(View.GONE);
		} else {
			ivRecorder.setVisibility(View.INVISIBLE);
			voiceTackView.setLocalAudio(audioPath);
			voiceLayout.setVisibility(View.VISIBLE);
		}
	}
	
	private void init() {
		shortRecEffectUtils = new EffectUtils(this);
		shortRecEffectUtils.parseXml("effectcfg/effectlist.xml");
		List<EffectBean> shortRecEffectBeans = shortRecEffectUtils.getEffects(EffectBean.TYPE_EFFECTS_AUDIO);
		effectGroup.removeAllViews();
		
		LinearLayout oriLayout = (LinearLayout) ZLayoutInflater.inflate(R.layout.include_voice_effect_buttons_stub);
		RadioButton oriRadio= (RadioButton) oriLayout.findViewById(R.id.radio_button);
		oriRadio.setId(0);
		oriRadio.setText("原声");
		oriRadio.setOnClickListener(this);
		oriRadio.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));

		oriLayout.removeAllViewsInLayout();
		effectGroup.addView(oriRadio);
//		effectGroup.check(0);
		
		int effectsSize = shortRecEffectBeans.size();
		
		for (int i = 1;i <= effectsSize;i++) {
			EffectBean bean = shortRecEffectBeans.get(i - 1);
			if (bean.getType() != EffectType.EFF_TYPE_TOM_CAT && bean.getType() != EffectType.EFF_TYPE_LOW_FREQUENCY) {
				continue;
			}
			
			LinearLayout layout = (LinearLayout) ZLayoutInflater.inflate(R.layout.include_voice_effect_buttons_stub);
			RadioButton radioButton = (RadioButton) layout.findViewById(R.id.radio_button);
			
			radioButton.setId(i);
			radioButton.setText(bean.getZHName());
			radioButton.setTag(bean);
			radioButton.setOnClickListener(this);
			RadioGroup.LayoutParams params = new RadioGroup.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
			params.leftMargin = (int) (20 * dm.density);
			Log.d(TAG,"leftMargin = " + params.leftMargin);
			radioButton.setLayoutParams(params);

			layout.removeAllViewsInLayout();
			effectGroup.addView(radioButton);
		}
		if (!hasVoice) {
			setRadioButtonEnable(false);
		} else {
			setRadioButtonEnable(true);
		}
		if (PluginUtils.isPluginMounted()) {
			shortRecEffect = new XEffects();
		}
		
		shortRecMediaPlayer = new XMediaPlayer(this, shortRecEffect, true);
	}
	
	private void setRadioButtonEnable(boolean enable) {
		int effectCount = effectGroup.getChildCount();
		for (int i = 0; i < effectCount; i++) {
			effectGroup.getChildAt(i).setEnabled(enable);
		}
	}
	
	
	@Override
	public boolean handleMessage(Message msg) {
		// TODO Auto-generated method stub
		switch (msg.what) {
		case ExtAudioRecorder.HANDLER_AUDIO_RECORDER_DONE:
			Log.d(TAG,"handleMessage HANDLER_AUDIO_RECORDER_DONE in");
			String audioID = (String)msg.obj;
			AudioWrapper audioWrapper = audioMap.get(audioID);
			if (!isOnRecorderButton) {
				AccountInfo.getInstance(userID).mediamapping.delMedia(userID, DiaryController.getRelativePath(audioWrapper.audioPath),true);
				break;
			}
			hasAddVoice = true;
			boolean isRecogniseDone = audioWrapper.recogniseDone;
			audioWrapper.audioDone = true;
			
			if (hasVoice) {
				if (attachUUID == null || myDiary.getAuxAttachPath(attachUUID) == null 
						|| !myDiary.getAuxAttachPath(attachUUID).equals(voiceTackView.getLocalPath())) {
					ZFileSystem.delFile(voiceTackView.getLocalPath());
				}
			}
			setRadioButtonEnable(true);
			effectGroup.check(checkId);
			voiceLayout.setVisibility(View.VISIBLE);
			voiceModifyLayout.setVisibility(View.VISIBLE);
			voiceTackView.setLocalAudio(audioWrapper.audioPath);
			ivRecorder.setVisibility(View.INVISIBLE);
			Log.d(TAG,"isRecogniseDong  = " + isRecogniseDone);
			Log.d(TAG,"duration = " + new Mp4InfoUtils(audioWrapper.audioPath).totaltime);
			if (ExtAudioRecorder.CheckPlugin() && !isRecogniseDone) {
				tvDone.setEnabled(false);
			}
			
			break;
		case QISR_TASK.HANDLER_QISR_RESULT_ADD:
			if (msg != null && msg.obj != null) {
				Bundle data = msg.getData();
				String audioid = data.getString("audioid");
				AudioWrapper audioWrapper1 = audioMap.get(audioid);
				if (audioWrapper1 != null) {
					audioWrapper1.audioContent += (String)msg.obj;
				}
			}
			break;
		case QISR_TASK.HANDLER_QISR_RESULT_CLEAN:
			if (msg != null && msg.obj != null) {
				Bundle data = msg.getData();
				String audioid = data.getString("audioid");
				AudioWrapper audioWrapper2 = audioMap.get(audioid);
				if (audioWrapper2 != null) {
					audioWrapper2.audioContent = "";
				}
			}
			break;
		case QISR_TASK.HANDLER_QISR_RESULT_DONE:
			
			Bundle data = msg.getData();
			String audioid = data.getString("audioid");
			if (!isOnRecorderButton) {
				audioMap.remove(audioid);
				break;
			}
			AudioWrapper audioWrapper3 = audioMap.get(audioid);
			audioWrapper3.recogniseDone = true;
			
			if (hasVoiceBefor) {
				contentEdit.setText(audioWrapper3.audioContent);
				contentEdit.setSelection(audioWrapper3.audioContent.length());
			} else {
				insertText(audioWrapper3.audioContent);
			}
			Log.d(TAG,"HANDLER_QISR_RESULT_DONE isLongClick = " + isLongPressed + " isOnRecorderButton = " + isOnRecorderButton);
			if (isLongPressed) {
				return false;
			}
			if (audioWrapper3.audioDone) {
				tvDone.setEnabled(true);
			}
			break;
		case DiaryController.DIARY_REQUEST_DONE:
			Log.d(TAG,"DiaryController.DIARY_REQUEST_DONE in");
			ZDialog.dismiss();
			if (!isDoneBtnClick) {
				break;
			}
			
			String diaryUUID = "";
			if (msg != null) {
				DiaryWrapper diaryWrapper = (DiaryWrapper) msg.obj;
				if (diaryWrapper == null) {
					Log.d(TAG,"日记返回为空");
					return false;
				}
				diaryUUID = diaryWrapper.diary.diaryuuid;
				if( diaryWrapper.diary.attachs.attach[0].playtime != null) {
					Log.d(TAG,"DiaryPreviewActivity playtime = " + diaryWrapper.diary.attachs.attach[0].playtime);
				}
			}
			Intent intent = new Intent(DiaryPreviewActivity.DIARY_EDIT_MOD);
			intent.putExtra(DiaryPreviewActivity.INTENT_ACTION_DIARY_UUID, diaryUUID);
			LocalBroadcastManager.getInstance(MainApplication.getAppInstance()).sendBroadcast(intent);
			
			finish();
			break;
		case TickUpHelper.HANDLER_FLAG_TICK_UP:
			int curTime = (Integer)msg.obj;
			Log.d(TAG,"TickUpHelper.HANDLER_FLAG_TICK_UP + curTime = " + curTime);
			if (isLongPressed) {
				shortRecView.updateTime(30 - curTime);
			}
			
			break;
		case TickUpHelper.HANDLER_FLAG_TICK_STOP:
			Log.d(TAG,"TickUpHelper.HANDLER_FLAG_TICK_STOP");
			stopShortRecord();
			break;
		case EffectTransCodeUtil.HANDLER_PROCESS_EFFECTS_FINISH:
			Log.d(TAG,"VideoEffectTransCodeUtil.HANDLER_PROCESS_EFFECTS_FINISH arg1 = " + msg.arg1);
			if (msg.arg1 == EffectTransCodeUtil.SHORT_AUDIO) {
				if (msg.obj != null) {
					outAudioPath = (String) msg.obj;
				}
				shortAudioSignal.countDown();
			}
			break;
		case EffectTransCodeUtil.HANDLER_PROCESS_EFFECTS_SCHEDULE:
			int percent = msg.arg2;
			Log.d(TAG,"VideoEffectTransCodeUtil.HANDLER_PROCESS_EFFECTS_SCHEDULE percent = " + percent);
			break;
		}
		return false;
	}
	
	private void insertText(String text) {
		contentEdit.getText().insert(contentEdit.getSelectionStart(), text);
		contentEdit.requestFocus();
	}
	
	private void stopShortRecord() {
		ear.stop();
		isLongPressed = false;
		shortRecView.dismiss();
		translucentLayout.setVisibility(View.GONE);
		releaseWakeLock();
		contentEdit.setEnabled(true);
	
		if (isOnRecorderButton) {
			if (hasVoice) {
				hasVoiceBefor = true;
			}
			
			hasVoice = true;
			changeDoneBtnState();
		}
		ivRecorder.setOnLongClickListener(this);
	}

	@Override
	public void onClick(View view) {
		// TODO Auto-generated method stub
		switch (view.getId()) {
		case R.id.iv_biaoqing_input:
			View exview = expressionView.getExpressionView();
			if (View.GONE != exview.getVisibility()) {
				expressionView.hideExpressionView();
				ivExpressionBtn.setImageResource(R.drawable.btn_biaoqing_input);
			} else {
				expressionView.showExpressionView();
				ivExpressionBtn.setImageResource(R.drawable.btn_wenzi_input);
			}
			break;
		case R.id.tv_save:
			if (isDoneBtnClick) {
				break;
			}
			// 2014-4-8
			CmmobiClickAgentWrapper.onEvent(this, "edit_voice_descr", "" + checkId);
			isDoneBtnClick = true;
			
			ZDialog.show(R.layout.progressdialog, false, true, this,false);
			if (attachUUID == null) {
				Log.d(TAG,"onClick R.id.tv_save in addVoiceDescrition");
				addVoiceDescrition();
			} else {
				Log.d(TAG,"onClick R.id.tv_save in modifyVoiceDescription");
				modifyVoiceDescription();
			}
			break;
		case R.id.iv_back:
			finish();
			break;
		case R.id.iv_voice_delete:
			hasAddVoice = false;
			voiceLayout.setVisibility(View.GONE);
//			voiceModifyLayout.setVisibility(View.GONE);
			voiceTackView.stopAudio(shortRecMediaPlayer);
			if (attachUUID == null || myDiary.getAuxAttachPath(attachUUID) == null || !myDiary.getAuxAttachPath(attachUUID).equals(audioPath)) {
				ZFileSystem.delFile(audioPath);
			}
			
			effectGroup.clearCheck();
			hasVoice = false;
			ivRecorder.setVisibility(View.VISIBLE);
			setRadioButtonEnable(false);
			changeDoneBtnState();
			break;
		case R.id.ll_tackview:
			voiceTackView.playAudio(audioPath,shortRecMediaPlayer);
			break;
		default:
			int viewId = view.getId();
			if (!hasVoice) {
				break;
			}
			checkId = viewId;
			if (viewId == 0) {
				if (shortRecEffect != null) {
					voiceTackView.playAudio(audioPath,shortRecMediaPlayer);
					shortRecEffect.cleanEffects();
				} else {
					voiceTackView.playAudio(audioPath);
				}
			} else {
				EffectBean bean = null;
				if (view.getTag() != null) {
					bean = (EffectBean) view.getTag();
				}
				if (bean != null && shortRecEffect != null) {
					voiceTackView.playAudio(audioPath,shortRecMediaPlayer);
					shortRecEffectUtils.changeAudioInflexion(shortRecEffect, bean.getFilePath(), 
						shortRecMediaPlayer.getChannels() , 
						shortRecMediaPlayer.getSampleRate(), 
						shortRecMediaPlayer.getBitsPerChannel(), 1);
				}
			}
			changeDoneBtnState();
			break;
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
	
	
	private void modifyVoiceDescription() {
		if (!isAttachChanged()) {
			Log.d(TAG,"modifyVoiceDescription in isAttachChanged");
			ZDialog.dismiss();
			finish();
			return;
		}
		
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				if (shortRecEffect != null && shortRecEffect.getEffectsCount() > 0) {
					String infilename = audioPath;
					Log.d(TAG,"infilename =  " + infilename);
					if (infilename != null) {
						EffectTransCodeUtil transCode = new EffectTransCodeUtil(handler, shortRecEffect, infilename, CreateDescriptionActivity.this,EffectTransCodeUtil.SHORT_AUDIO);
						transCode.start("audio");
						try {
							shortAudioSignal.await();
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}
				
				if (isAttachChanged()) {
					AuxAttach auxAttach = myDiary.getAuxAttach();
					String attachID = "";
					if (auxAttach != null) {
						attachID = auxAttach.attachid;
					}
					Attachs attach = null;
					String content = EmojiPaser.getInstance().format(contentEdit.getText().toString());
					if (content == null) {
						content = "";
					}
					if (hasVoice) {
						if ("".equals(ZStringUtils.nullToEmpty(outAudioPath))) {
							outAudioPath = audioPath;
						}
						attach = DiaryController.getInstanse().createNewAttach(attachID, attachUUID, GsonProtocol.ATTACH_TYPE_VOICE, GsonProtocol.SUFFIX_MP4, GsonProtocol.ATTACH_LEVEL_SUB, GsonProtocol.ATTACH_OPERATE_TYPE_UPDATE, content,outAudioPath);
					} else {
						attach = DiaryController.getInstanse().createNewAttach(attachID, attachUUID, GsonProtocol.ATTACH_TYPE_TEXT, "", GsonProtocol.ATTACH_LEVEL_SUB, GsonProtocol.ATTACH_OPERATE_TYPE_UPDATE, content,"");
					}
					Attachs[] attachs = new Attachs[1];
					attachs[0] = attach;
					DiaryController.getInstanse().updateDiary(handler, myDiary, attachs,GsonProtocol.TAG_UNCHANGED,"","","","","");
					DiaryController.getInstanse().diaryContentIsReady(diaryUUID);
					Log.d(TAG,"updateDiary in");
				}
			}
		}).start();
	}
	
	private void addVoiceDescrition() {
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				if (shortRecEffect != null && shortRecEffect.getEffectsCount() > 0) {
					String infilename = audioPath;
					Log.d(TAG,"infilename =  " + infilename);
					if (infilename != null) {
						EffectTransCodeUtil transCode = new EffectTransCodeUtil(handler, shortRecEffect, infilename, CreateDescriptionActivity.this,EffectTransCodeUtil.SHORT_AUDIO);
						transCode.start("audio");
						try {
							shortAudioSignal.await();
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}
				String content = contentEdit.getText().toString();
				
				String attachType = "";
				String attachUUID = "";
				String attachSuffix = "";
				if (hasVoice) {
					if ("".equals(ZStringUtils.nullToEmpty(content))) {
						attachType = GsonProtocol.ATTACH_TYPE_VOICE;
					} else {
						attachType = GsonProtocol.ATTACH_TYPE_VOICE_TEXT;
					}
					attachUUID = audioID;
					attachSuffix = GsonProtocol.SUFFIX_MP4;
					if (ZFileSystem.isFileExists(outAudioPath)) {
						ZFileSystem.delFile(audioPath);
						ZFileSystem.renameTo(outAudioPath, audioPath);
					}
				} else {
					attachType = GsonProtocol.ATTACH_TYPE_TEXT;
					attachUUID = DiaryController.getNextUUID();
				}
				
				content = EmojiPaser.getInstance().format(content);
				if (content == null) {
					content = "";
				}
				
				Attachs attach = DiaryController.getInstanse().createNewAttach("",
						attachUUID,
						attachType, 
						attachSuffix, 
						GsonProtocol.ATTACH_LEVEL_SUB, 
						GsonProtocol.ATTACH_OPERATE_TYPE_ADD, 
						content,
						"");
				Attachs attachs[] = new Attachs[1];
				attachs[0] = attach;
				
				DiaryController.getInstanse().updateDiary(handler, myDiary, attachs,GsonProtocol.TAG_UNCHANGED,"","","","","");
				DiaryController.getInstanse().diaryContentIsReady(myDiary.diaryuuid);
			}
		}).start();
			
	}
	
	private TextWatcher mTextWatcher = new TextWatcher() {  
		  
		public void afterTextChanged(Editable s) { 
			if (s == null || "".equals(s.toString())) {
				hasText = false;
	        } else {
	           	hasText = true;
	        }
	       	changeDoneBtnState();
        }  
			 
        public void beforeTextChanged(CharSequence s, int start, int count,  
               int after) {
        }  
 
        public void onTextChanged(CharSequence s, int start, int before,  
               int count) { 
           
        }  
    };
   
	private void changeDoneBtnState() {
		if ((!hasVoice && ((!hasText) || contentEdit.getText().toString().replace(" ", "").replace("\n", "").length() == 0)) || contentEdit.getText().length() > Config.MAX_NOTE_TEXT_LENGTH) {
			tvDone.setEnabled(false);
		} else if (attachUUID != null) {
			if (isAttachChanged()) {
				tvDone.setEnabled(true);
			} else {
				tvDone.setEnabled(false);
			}
		} else {
			tvDone.setEnabled(true);
		}
	}
	
	private boolean isAttachChanged() {
		if (!contentEdit.getText().toString().equals(voiceAttachContent)) {
			return true;
		} else if (audioPath == null) {
			return false;
		} else {
			if ((shortRecEffect != null && shortRecEffect.getEffectsCount() > 0) || !hasVoice || !hasVoiceBefor || hasAddVoice) {// 经过特效处理，或者语音被删除，或者新增语音
				return true;
			} else {
				return false;
			}
		}
	}
	
	@Override
	public boolean onTouch(View view, MotionEvent event) {
		// TODO Auto-generated method stub
		switch(view.getId()) {
		case R.id.iv_recorder:
		if (isLongPressed) {
			int action = event.getAction();
			if (action == MotionEvent.ACTION_DOWN) {
				Log.d(TAG,"ACTION_DOWN");
			} else if (action == MotionEvent.ACTION_UP || action == MotionEvent.ACTION_CANCEL) {
				Log.d(TAG,"ACTION_CANCEL = " + (action == MotionEvent.ACTION_CANCEL) + " isOnRecorderButton = " + isOnRecorderButton);
				
				if (isOnRecorderButton) {
					TickUpHelper.getInstance(handler).stop(2);
				} else {
					TickUpHelper.getInstance(handler).stop(1);
				}
				ivRecorder.setImageResource(R.drawable.btn_press_speek_selector);

				CmmobiClickAgent.onEventEnd(this, "record_voice");
			} else if(action == MotionEvent.ACTION_MOVE){
				if (event.getY() < 0) {
					if (isOnRecorderButton) {
						shortRecView.showCancle();
					}
					isOnRecorderButton = false;
				} else {
					if (!isOnRecorderButton) {
						shortRecView.showVolume();
					}
					isOnRecorderButton = true;
				}
				Log.d(TAG,"Y =  " + event.getY() + " isOnRecorderButton = " + isOnRecorderButton);
			}
		} else {
			int[] location = new int[2];
			bottomLayout.getLocationOnScreen(location);
			int y = location[1];
//			Log.d(TAG,".... onTouch in y = " + y + " height = " + bottomLayout.getHeight() + " screenHeight = " + dm.heightPixels);
			if (y < dm.heightPixels - bottomLayout.getHeight()) {
				expressionView.hideExpressionViewOnly();
				expressionView.hideKeyboard();
				return true;
			}
		}
		break;
		}
		return false;
	}
	
	private boolean isKeyBoradShown() {
		int[] location = new int[2];
		bottomLayout.getLocationOnScreen(location);
		int y = location[1];
		if (y < dm.heightPixels - bottomLayout.getHeight()) {
			if (!expressionView.isExpressionShown()) {
				return true;
			}
		}
		return false;
	}
	
	@Override
	public boolean onLongClick(View v) {
		// TODO Auto-generated method stub
		Log.d(TAG,"onLongClick viewId = " + v.getId());
		switch(v.getId()) {
		case R.id.iv_recorder:
			
			if (LowStorageChecker.check(this, null, R.string.cancel_record_audio)) {
				expressionView.hideKeyboard();
				if(!LookLookActivity.isSdcardMountedAndWritable()) {
					Prompt.Alert(this, "Sdcard不可用，无法录音");
					break;
				}
				
				isOnRecorderButton = true;
//				ivRecorder.setImageResource(R.drawable.btn_press_speek_press);
				ivRecorder.setImageResource(R.drawable.skjs);
				
				audioID = DiaryController.getNextUUID();
				
				String RECORD_FILE_DIR =  Constant.SD_STORAGE_ROOT + "/" + ActiveAccount.getInstance(this).getLookLookID() + "/audio";
				audioPath = Environment.getExternalStorageDirectory() + RECORD_FILE_DIR + "/" + audioID + ".mp4" ;
				AudioWrapper audioWrapper = new AudioWrapper();
				audioWrapper.audioID = audioID;
				audioWrapper.audioPath = audioPath;
				audioMap.put(audioID, audioWrapper);
				ear.setHandler(handler);
				if (!ear.start(audioID,RECORD_FILE_DIR, false, 3, true)) {
					Prompt.Alert(this, "录音失败");
					isOnRecorderButton = false;
					if (ear != null && ear.getStatus() == State.RECORDING) {
						ear.stop();
						ivRecorder.setOnLongClickListener(this);
					}
					break;
				}
				ivRecorder.setOnLongClickListener(null);
				CmmobiClickAgent.onEventBegin(this, "record_voice");
				
				isLongPressed = true;
				
				shortRecView.show();
				translucentLayout.setVisibility(View.VISIBLE);
				translucentLayout.requestFocus();
				contentEdit.setEnabled(false);
				
				acquireWakeLock();
			}
			break;
		case R.id.iv_back:
			startActivity(new Intent(this,LookLookActivity.class));
			finish();
			break;
		}
		
		return false;
	}
	
	private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			int type;
			Message msg = new Message();
			String content = "";
			// Get extra data included in the Intent
			if (ExtAudioRecorder.AUDIO_RECORDER_MSG.equals(intent
					.getAction())) {
				type = intent.getIntExtra("type", 0);
				if (type == ExtAudioRecorder.HANDLER_AUDIO_RECORDER_DONE) {
					long duration = intent.getLongExtra("audioduration",0);
					Bundle b = new  Bundle();
				    b.putLong("audioduration", duration);
				    msg.setData(b);
				    content = intent.getStringExtra("content");
					Log.d(TAG,"onReceive content = " + duration);
				}
				msg.what = type;
				msg.obj = content;
			} else if (QISR_TASK.QISR_RESULT_MSG.equals(intent.getAction())){
				type = intent.getIntExtra("type", 0);
			    String message = intent.getStringExtra("content");
			    String audioID = intent.getStringExtra("audioID");
			    Bundle b = new  Bundle();
			    b.putString("msg", message);
			    b.putString("audioid", audioID);
			    msg.setData(b);
			    Log.d(TAG, "Got message: " + message);
			    msg.what = type;
			    msg.obj = message;
			}

			handler.sendMessage(msg);
		}
	};
	
	protected void onStop() {
		if (ear != null && ear.isRecording) {
			isOnRecorderButton = false;
			ear.stop();
			ivRecorder.setOnLongClickListener(this);
		}
		if (voiceTackView != null && shortRecMediaPlayer != null) {
			voiceTackView.stopAudio(shortRecMediaPlayer);
		}
		CmmobiClickAgentWrapper.onStop(this);
		super.onStop();
	}
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		LocalBroadcastManager.getInstance(this).unregisterReceiver(mMessageReceiver);
		super.onDestroy();
	}
	
	WakeLock wakeLock = null;  
	   //获取电源锁，保持该服务在屏幕熄灭时仍然获取CPU时，保持运行  
	   private void acquireWakeLock()  
	   {  
	       if (null == wakeLock)  
	       {  
	    	   wakeLock = ((PowerManager) getSystemService(POWER_SERVICE))
	                   .newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK
	                           | PowerManager.ON_AFTER_RELEASE, TAG);
	    	   
	           if (null != wakeLock)  
	           {  
	               wakeLock.acquire();  
	           }  
	       }  
	   }
	   
	 //释放设备电源锁  
	   private void releaseWakeLock()  
	   {  
	       if (null != wakeLock)  
	       {  
	           wakeLock.release();  
	           wakeLock = null;  
	       }  
	   }

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		// TODO Auto-generated method stub
		insertText(arg1.getTag().toString());
		CmmobiClickAgentWrapper.onEvent(this, "edit_emote", ""+FriendsExpressionView.getExpIndex(arg1.getTag().toString()));
		if (!contentEdit.isFocused()) {
			contentEdit.requestFocus();
		}
	} 
	
	@Override
	public void finish()
	{
		if(expressionView != null)
		{
			expressionView.hideExpressionView();
			expressionView.hideKeyboard();
			expressionView.hideSoftKeyboard();
		}
		super.finish();
	}

}
