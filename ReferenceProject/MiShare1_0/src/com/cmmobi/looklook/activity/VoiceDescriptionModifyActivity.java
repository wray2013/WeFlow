package com.cmmobi.looklook.activity;

import java.util.List;
import java.util.concurrent.CountDownLatch;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.Message;
import android.support.v4.content.LocalBroadcastManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
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
import cn.zipper.framwork.utils.ZStringUtils;

import com.cmmobi.looklook.Config;
import com.cmmobi.looklook.MainApplication;
import com.cmmobi.looklook.R;
import com.cmmobi.looklook.common.gson.DiaryController;
import com.cmmobi.looklook.common.gson.DiaryController.DiaryWrapper;
import com.cmmobi.looklook.common.gson.GsonProtocol;
import com.cmmobi.looklook.common.gson.GsonRequest3.Attachs;
import com.cmmobi.looklook.common.gson.GsonResponse3.AuxAttach;
import com.cmmobi.looklook.common.gson.GsonResponse3.MyDiary;
import com.cmmobi.looklook.common.utils.EffectTransCodeUtil;
import com.cmmobi.looklook.common.web.TackView;
import com.cmmobi.looklook.info.profile.AccountInfo;
import com.cmmobi.looklook.info.profile.ActiveAccount;
import com.cmmobi.looklook.info.profile.DiaryManager;
import com.cmmobi.looklook.info.profile.MediaValue;
import com.cmmobi.looklook.prompt.Prompt;
import com.cmmobivideo.utils.EffectBean;
import com.cmmobivideo.utils.EffectUtils;
import com.cmmobivideo.utils.PluginUtils;
import com.cmmobivideo.workers.XMediaPlayer;

import effect.EffectType;
import effect.XEffects;

public class VoiceDescriptionModifyActivity extends ZActivity {

	private final String TAG = "VoiceDescriptionModifyActivity";
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
	
	private boolean hasVoice = false;
	private boolean hasText = false;
	private boolean isVoiceChanged = false;
	private String userID = null;
	private String outAudioPath = "";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.activity_voice_description_modify);
		
		diaryUUID = getIntent().getStringExtra(DiaryPreviewActivity.INTENT_ACTION_DIARY_UUID);
		attachUUID = getIntent().getStringExtra(DiaryPreviewActivity.INTENT_ACTION_ATTACH_UUID);
		userID = ActiveAccount.getInstance(ZApplication.getInstance()).getUID();
		
		myDiary = DiaryManager.getInstance().findMyDiaryByUUID(diaryUUID);
		if (myDiary == null) {
			Prompt.Alert(this, "找不到该日记");
			finish();
			return;
		} else {
			loadVoice();
		}
		
		voiceLayout = (RelativeLayout) findViewById(R.id.fl_voice_description);
		deleteVoiceBtn = (ImageView) findViewById(R.id.iv_voice_delete);
		deleteVoiceBtn.setOnClickListener(this);
		voiceTackView = (TackView) findViewById(R.id.ll_tackview);
		voiceTackView.setSoundIcons(SOUND_ICONS);
		voiceTackView.setOnClickListener(this);
		voiceModifyLayout = (LinearLayout) findViewById(R.id.ll_voice_modify);
		effectGroup = (RadioGroup) findViewById(R.id.effects_wrap_layout);
		
		if (audioPath != null) {
			outAudioPath = audioPath;
			voiceTackView.setLocalAudio(audioPath);
//			initTackView();
			init();
			hasVoice = true;
		} else {
			voiceLayout.setVisibility(View.GONE);
			voiceModifyLayout.setVisibility(View.GONE);
		}
		
		ivBack = (ImageView) findViewById(R.id.iv_back);
		ivBack.setOnClickListener(this);
		
		tvDone = (TextView) findViewById(R.id.tv_save);
		tvDone.setOnClickListener(this);
		
		contentEdit = (EditText) findViewById(R.id.et_voice_description_input);
		contentEdit.addTextChangedListener(mTextWatcher);
		contentEdit.setText(ZStringUtils.nullToEmpty(getAttachContent()));
	}
	
	private void loadVoice() {
		AuxAttach voiceAttach = getVoiceAttach();
		audioPath = getVoicePath(voiceAttach);
	}
	
	private void initTackView() {
		AuxAttach voiceAttach = getVoiceAttach();
		
		/*if(voiceAttach.playtime != null) {
			int time = 0;
			try {
				time = Integer.parseInt(voiceAttach.playtime);
				if(time != 0 && time <= 30) {
					float level = ((time - 1) / 10 + 1) / 3f;
					voiceTackView.setBackgroudLevel(level, 110);
				}
			} catch (NumberFormatException e) {
				voiceTackView.setVisibility(View.GONE);
				e.printStackTrace();
			}
		}*/
	}
	
	private AuxAttach getVoiceAttach() {
		AuxAttach auxAttach = myDiary.getAuxAttachByUUID(attachUUID);
		return auxAttach;
	}
	
	
	private String getVoicePath(AuxAttach attach) {
		if (attach == null) {
			return null;
		}
		String key = null;
		MediaValue mediaValue = null;
		if (ZStringUtils.emptyToNull(attach.attachurl) != null) {
			key = attach.attachurl;
		} else {
			key = attach.attachuuid;
		}
		mediaValue = AccountInfo.getInstance(userID).mediamapping.getMedia(userID, key);
		
		if (mediaValue != null) {
			return Environment.getExternalStorageDirectory() + mediaValue.localpath;
		}
		return null;
	}
	
	private String getAttachContent() {
		AuxAttach attach = getVoiceAttach();
		if (attach == null) {
			return null;
		} else {
			return attach.content;
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
		effectGroup.check(0);
		
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
			radioButton.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));

			layout.removeAllViewsInLayout();
			effectGroup.addView(radioButton);
		}
		
		if (PluginUtils.isPluginMounted()) {
			shortRecEffect = new XEffects();
		}
		
		shortRecMediaPlayer = new XMediaPlayer(this, shortRecEffect, true);
	}
	
	@Override
	public boolean handleMessage(Message msg) {
		// TODO Auto-generated method stub
		switch(msg.what) {
		case DiaryController.DIARY_REQUEST_DONE:
			Log.d(TAG,"DiaryController.DIARY_REQUEST_DONE in");
			MyDiary diary = ((DiaryWrapper) msg.obj).diary;
			MyDiary localDiary = DiaryManager.getInstance().findMyDiaryByUUID(diary.diaryuuid);
			ZDialog.dismiss();
			Intent intent = new Intent(DiaryPreviewActivity.DIARY_EDIT_MOD);
			intent.putExtra(DiaryPreviewActivity.INTENT_ACTION_DIARY_UUID, diaryUUID);
			LocalBroadcastManager.getInstance(MainApplication.getAppInstance()).sendBroadcast(intent);
			finish();
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

	@Override
	public void onClick(View view) {
		// TODO Auto-generated method stub
		int viewId = view.getId();
		switch(viewId) {
		case R.id.iv_voice_delete:
			voiceLayout.setVisibility(View.GONE);
			voiceModifyLayout.setVisibility(View.GONE);
			hasVoice = false;
			changeDoneBtnState();
			break;
		case R.id.iv_back:
			finish();
			break;
		case R.id.tv_save:
			modifyVoiceDescription();
			break;
		case R.id.ll_tackview:
			voiceTackView.playAudio(audioPath,shortRecMediaPlayer);
			break;
		default:
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
					changeDoneBtnState();
				}
			}
			break;
		}
	}
	
	private boolean isAttachChanged() {
		if (!contentEdit.getText().toString().equals(getAttachContent())) {
			return true;
		} else if (audioPath == null) {
			return false;
		} else {
			if (shortRecEffect.getEffectsCount() > 0 || !hasVoice) {// 经过特效处理，或者语音被删除
				return true;
			} else {
				return false;
			}
		}
	}
	
	private void modifyVoiceDescription() {
		if (!isAttachChanged()) {
			finish();
			return;
		}
		
		ZDialog.show(R.layout.progressdialog, false, true, this,true);
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				if (shortRecEffect != null && shortRecEffect.getEffectsCount() > 0) {
					String infilename = audioPath;
					Log.d(TAG,"infilename =  " + infilename);
					if (infilename != null) {
						EffectTransCodeUtil transCode = new EffectTransCodeUtil(handler, shortRecEffect, infilename, VoiceDescriptionModifyActivity.this,EffectTransCodeUtil.SHORT_AUDIO);
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
					AuxAttach auxAttach = getVoiceAttach();
					String attachID = "";
					if (auxAttach != null) {
						attachID = auxAttach.attachid;
					}
					Attachs attach = null;
					if (hasVoice) {
						attach = DiaryController.getInstanse().createNewAttach(attachID, attachUUID, GsonProtocol.ATTACH_TYPE_VOICE, GsonProtocol.SUFFIX_MP4, GsonProtocol.ATTACH_LEVEL_SUB, GsonProtocol.ATTACH_OPERATE_TYPE_UPDATE, contentEdit.getText().toString(),outAudioPath);
					} else {
						attach = DiaryController.getInstanse().createNewAttach(attachID, attachUUID, GsonProtocol.ATTACH_TYPE_TEXT, "", GsonProtocol.ATTACH_LEVEL_SUB, GsonProtocol.ATTACH_OPERATE_TYPE_UPDATE, contentEdit.getText().toString(),"");
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
		} else if (isAttachChanged()){
			tvDone.setEnabled(true);
		} else {
			tvDone.setEnabled(false);
		}
	}
}
