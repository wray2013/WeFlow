package com.cmmobi.looklook.activity;

import java.util.List;
import java.util.concurrent.CountDownLatch;

import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import cn.zipper.framwork.core.ZActivity;
import cn.zipper.framwork.core.ZDialog;
import cn.zipper.framwork.core.ZLayoutInflater;

import com.cmmobi.looklook.Config;
import com.cmmobi.looklook.R;
import com.cmmobi.looklook.common.gson.GsonResponse3.MainAttach;
import com.cmmobi.looklook.common.gson.GsonResponse3.MyDiary;
import com.cmmobi.looklook.common.utils.DiaryEditNote;
import com.cmmobi.looklook.common.utils.EffectTransCodeUtil;
import com.cmmobi.looklook.common.web.TackView;
import com.cmmobivideo.utils.EffectBean;
import com.cmmobivideo.utils.EffectUtils;
import com.cmmobivideo.utils.PluginUtils;
import com.cmmobivideo.workers.XMediaPlayer;
import com.google.gson.Gson;

import effect.EffectType;
import effect.XEffects;

public class del_EditNoteActivity extends ZActivity {

	private final String TAG = "EditNoteActivity";
	
	private DisplayMetrics dm = new DisplayMetrics();
	private RelativeLayout noteContentLayout = null;
	private MyDiary myDiary = null;
	private String textContent = null;
	private EditText etInput = null;
	private String audioPath = null;
	private TackView tackView = null;
	private XEffects shortRecEffect;
	private XMediaPlayer shortRecMediaPlayer;
	private EffectUtils shortRecEffectUtils;
	private RadioGroup effectGroup = null;
	private boolean hasVoice = false;
	private LinearLayout voiceModifyLayout = null;
	private TextView focusTextView = null;
	private int[] SOUND_ICONS = {R.drawable.yuyinbiaoqian_3,R.drawable.yuyinbiaoqian_4,R.drawable.yuyinbiaoqian_2};
//	private Dialog finishDialog;
	private CountDownLatch shortAudioSignal = new CountDownLatch(1);
	private MainAttach mainAttach = null;
	private String outAudioPath = null;
	private ImageView ivDone = null;
	private boolean isVoiceChanged = false;
	
	public DiaryEditNote diaryEditNote = null;
	private List<EffectBean> shortRecEffectBeans = null;
	public int curEffectId = 0;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_edit_note);
		
		noteContentLayout = (RelativeLayout) findViewById(R.id.rl_note_content);
		dm = getResources().getDisplayMetrics();
		Log.d(TAG,"width = " + dm.widthPixels + " height = " + dm.heightPixels);
		RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(dm.widthPixels,dm.widthPixels);
		params.addRule(RelativeLayout.BELOW, R.id.rl_edit_note_top);
		noteContentLayout.setLayoutParams(params);
		
		loadNote();
		etInput = (EditText) findViewById(R.id.et_word_input);
		tackView = (TackView) findViewById(R.id.ll_tackview);
		voiceModifyLayout = (LinearLayout) findViewById(R.id.ll_voice_modify);
		effectGroup = (RadioGroup) findViewById(R.id.effects_wrap_layout);
		findViewById(R.id.iv_edit_diary_back).setOnClickListener(this);
		ivDone = (ImageView)findViewById(R.id.iv_edit_diary_save);
		ivDone.setOnClickListener(this);
		ivDone.setEnabled(false);
		if (textContent != null) {
			etInput.setText(textContent);
		}
		etInput.addTextChangedListener(mTextWatcher);
		
		if (audioPath != null) {
			tackView.setLocalAudio(audioPath);
			tackView.setVisibility(View.VISIBLE);
			tackView.setBackground(R.drawable.btn_yuyinbiaoqian);
			tackView.setSoundIcons(SOUND_ICONS);
			tackView.setOnClickListener(this);
			outAudioPath = audioPath;
			init();
			hasVoice = true;
		} else {
			tackView.setVisibility(View.GONE);
			voiceModifyLayout.setVisibility(View.GONE);
			hasVoice = false;
		}
		
		// 隐藏键盘
		focusTextView = (TextView) findViewById(R.id.text_notuse);
		focusTextView.requestFocus();
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
		
		effectGroup.removeAllViews();
		
		LinearLayout oriLayout = (LinearLayout) ZLayoutInflater.inflate(R.layout.include_voice_effect_buttons_stub);
		RadioButton oriRadio= (RadioButton) oriLayout.findViewById(R.id.radio_button);
		oriRadio.setId(0);
		oriRadio.setText("原声");
		oriRadio.setOnClickListener(this);
		oriRadio.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));

		oriLayout.removeAllViewsInLayout();
		effectGroup.addView(oriRadio);
		
		
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
			if (diaryEditNote.isEffect && i == diaryEditNote.effectIndex) {
				effectGroup.check(i);
			}
		}
		
		if (!diaryEditNote.isEffect) {
			effectGroup.check(0);
		}
		
	}
	
	@Override
	public boolean handleMessage(Message msg) {
		// TODO Auto-generated method stub
		switch(msg.what) {
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
		case R.id.iv_edit_diary_back:
			finish();
			break;
		case R.id.iv_edit_diary_save:
//			finishDialog.show();
			modifyNoteDiary();
			break;
		case R.id.ll_tackview:
			tackView.playAudio(audioPath,shortRecMediaPlayer);
			break;
//		case R.id.replace:// 覆盖
//			finishDialog.dismiss();
//			modifyNoteDiary();
//			break;
//		case R.id.save_as:// 另存为
//			finishDialog.dismiss();
//			saveAsNewNoteDiary();
//			break;
//		case R.id.cancel:// 取消
//			finishDialog.dismiss();
//			break;
		default:
			if (viewId == 0) {
				if (shortRecEffect != null) {
					shortRecEffect.cleanEffects();
					tackView.playAudio(audioPath,shortRecMediaPlayer);
				} else {
					tackView.playAudio(audioPath);
				}
				isVoiceChanged = false;
			} else {
				EffectBean bean = null;
				if (view.getTag() != null) {
					bean = (EffectBean) view.getTag();
				}
				if (bean != null && shortRecEffect != null) {
					tackView.playAudio(audioPath,shortRecMediaPlayer);
					shortRecEffectUtils.changeAudioInflexion(shortRecEffect, bean.getFilePath(), 
						shortRecMediaPlayer.getChannels() , 
						shortRecMediaPlayer.getSampleRate(), 
						shortRecMediaPlayer.getBitsPerChannel(), 1);
				}
				isVoiceChanged = true;
			}
			if (diaryEditNote.isEffect) {
				if (viewId == diaryEditNote.effectIndex) {
					isVoiceChanged = false;
				} else {
					isVoiceChanged = true;
				}
			}
			
			curEffectId = viewId;
			
			changeDoneBtnState();
			break;
		
		}
	}
	
	private void modifyMainAttach() {
		if (shortRecEffect != null && shortRecEffect.getEffectsCount() > 0) {
			String infilename = audioPath;
			Log.d(TAG,"infilename =  " + infilename);
			if (infilename != null) {
				EffectTransCodeUtil transCode = new EffectTransCodeUtil(handler, shortRecEffect, infilename, del_EditNoteActivity.this,EffectTransCodeUtil.SHORT_AUDIO);
				transCode.start("audio");
				try {
					shortAudioSignal.await();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}
	
	private void modifyNoteDiary() {
		
		ZDialog.show(R.layout.progressdialog, false, true, this,true);
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				modifyMainAttach();
				
				myDiary.modifyMainAttach(outAudioPath, etInput.getText().toString());
				Log.d(TAG,"outAudioPath = " + outAudioPath);
				del_EditNoteActivity.this.runOnUiThread(new Runnable() {
					
					@Override
					public void run() {
						// TODO Auto-generated method stub
						ZDialog.dismiss();
						String diaryString = new Gson().toJson(myDiary);
						Intent intent = new Intent();
						intent.putExtra(DiaryEditPreviewActivity.INTENT_ACTION_DIARY_STRING, diaryString);
						if (isVoiceChanged) {
							intent.putExtra(DiaryEditPreviewActivity.INTENT_ACTION_MEDIA_EFFECT, curEffectId);
						}
						setResult(RESULT_OK, intent);
						finish();
					}
				});
				/*String attachID = "";
				String attachUUID = "";
				if (mainAttach != null) {
					attachID = mainAttach.attachid;
					attachUUID = mainAttach.attachuuid;
				}
				Attachs attach = null;
				if (hasVoice) {
					attach = DiaryController.getInstanse().createNewAttach(attachID, attachUUID, GsonProtocol.ATTACH_TYPE_VOICE, GsonProtocol.SUFFIX_MP4, GsonProtocol.ATTACH_LEVEL_MAIN, GsonProtocol.ATTACH_OPERATE_TYPE_UPDATE, etInput.getText().toString(),outAudioPath);
				} else {
					attach = DiaryController.getInstanse().createNewAttach(attachID, attachUUID, GsonProtocol.ATTACH_TYPE_TEXT, "", GsonProtocol.ATTACH_LEVEL_MAIN, GsonProtocol.ATTACH_OPERATE_TYPE_UPDATE, etInput.getText().toString(),"");
				}
				Attachs[] attachs = new Attachs[1];
				attachs[0] = attach;
				DiaryController.getInstanse().updateDiary(handler, myDiary, attachs);
				DiaryController.getInstanse().diaryContentIsReady(diaryUUID);
				Log.d(TAG,"updateDiary in");*/
			}
		}).start();
	}
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		try {
			if(shortRecMediaPlayer!=null){
				shortRecMediaPlayer.release();
				shortRecEffect.release();
				shortRecMediaPlayer=null;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		super.onDestroy();
	}
	
	/*private void saveAsNewNoteDiary() {
		ZDialog.show(R.layout.del_progressdialog, false, true, this,true);
		new Thread(new Runnable() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				modifyMainAttach();
				
				String attachID = "";
				String attachUUID = DiaryController.getNextUUID();
				Attachs attach = null;
				if (hasVoice) {
					attach = DiaryController.getInstanse().createNewAttach(attachID, attachUUID, GsonProtocol.ATTACH_TYPE_VOICE, GsonProtocol.SUFFIX_MP4, GsonProtocol.ATTACH_LEVEL_MAIN, GsonProtocol.ATTACH_OPERATE_TYPE_UPDATE, etInput.getText().toString(),outAudioPath);
				} else {
					attach = DiaryController.getInstanse().createNewAttach(attachID, attachUUID, GsonProtocol.ATTACH_TYPE_TEXT, "", GsonProtocol.ATTACH_LEVEL_MAIN, GsonProtocol.ATTACH_OPERATE_TYPE_UPDATE, etInput.getText().toString(),"");
				}
				Attachs[] attachs = new Attachs[1];
				attachs[0] = attach;
				
				DiaryController.getInstanse().savaAsDiary(handler, diaryUUID, attachs);
			}
		}).start();
	}*/
	
	
	private void loadNote() {
//		diaryUUID = getIntent().getStringExtra("diaryuuid");
//		userID = ActiveAccount.getInstance(ZApplication.getInstance()).getUID();
		
//		myDiary = DiaryManager.getInstance().findMyDiaryByUUID(diaryUUID);
		String diaryString = getIntent().getStringExtra("diarystring");
		myDiary = new Gson().fromJson(diaryString,MyDiary.class);
		
		String diaryEditString = getIntent().getStringExtra(DiaryEditPreviewActivity.INTENT_ACTION_EDIT_NOTELIST);
		diaryEditNote = new Gson().fromJson(diaryEditString, DiaryEditNote.class);
		
		textContent = myDiary.getMainTextContent();
//		audioPath = myDiary.getMainPath();
		audioPath = diaryEditNote.mediaPath;
		outAudioPath = audioPath;
		mainAttach = myDiary.attachs.levelattach;
		
		shortRecEffectUtils = new EffectUtils(this);
		shortRecEffectUtils.parseXml("effectcfg/effectlist.xml");
		shortRecEffectBeans = shortRecEffectUtils.getEffects(EffectBean.TYPE_EFFECTS_AUDIO);
		if (PluginUtils.isPluginMounted()) {
			shortRecEffect = new XEffects();
		}
		
		shortRecMediaPlayer = new XMediaPlayer(this, shortRecEffect, true);
		
		if (diaryEditNote.isEffect) {
			EffectBean bean = shortRecEffectBeans.get(diaryEditNote.effectIndex - 1);
			if (bean != null && shortRecEffect != null) {
				shortRecEffectUtils.changeAudioInflexion(shortRecEffect, bean.getFilePath(), 
					shortRecMediaPlayer.getChannels() , 
					shortRecMediaPlayer.getSampleRate(), 
					shortRecMediaPlayer.getBitsPerChannel(), 1);
			}
		}
		
	}
	
	private void changeDoneBtnState() {
		if ((etInput.getText().toString().equals(textContent) && !isVoiceChanged) 
				|| ("".equals(etInput.getText().toString()) && !hasVoice)
				|| etInput.getText().length() > Config.MAX_NOTE_TEXT_LENGTH) {
			ivDone.setEnabled(false);
		} else {
			ivDone.setEnabled(true);
		}
	}
	
	private TextWatcher mTextWatcher = new TextWatcher() {  
		  
        public void afterTextChanged(Editable s) { 
	       	
	       	changeDoneBtnState();
       }  
 
       public void beforeTextChanged(CharSequence s, int start, int count,  
               int after) {
       }  
 
       public void onTextChanged(CharSequence s, int start, int before,  
               int count) { 
       	Log.d(TAG,"onTextChanged in");
           
       }  
   };

}
