package com.cmmobi.looklook.common.view;

import java.util.HashMap;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.LocalBroadcastManager;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import cn.zipper.framwork.core.ZLog;
import cn.zipper.framwork.io.file.ZFileSystem;
import cn.zipper.framwork.io.network.ZNetworkStateDetector;

import com.cmmobi.common.net.NetworkTypeUtility;
import com.cmmobi.looklook.R;
import com.cmmobi.looklook.activity.GuideActivity;
import com.cmmobi.looklook.common.constant.Constant;
import com.cmmobi.looklook.common.utils.CmmobiClickAgentWrapper;
import com.cmmobi.looklook.info.profile.ActiveAccount;
import com.cmmobi.looklook.info.profile.TimeHelper;
import com.cmmobi.looklook.prompt.Prompt;
import com.cmmobi.looklook.prompt.TickUpHelper;
import com.cmmobivideo.utils.Mp4InfoUtils;
import com.iflytek.msc.ExtAudioRecorder;
import com.iflytek.msc.QISR_TASK;
import com.iflytek.msc.ExtAudioRecorder.State;
import com.tencent.mm.sdk.channel.ConstantsMMessage;

/**
 *  底部录音控件
 *  
 *  实现缓存
 *  则必须调用 setInputStrKey(..,..)
 *  
 *  回复某人
 *  则调用setReplyName(name)
 *  
 *  如果不需回复某人则必须调用
 *  setNoReply()
 */
public class InputRecoderView extends LinearLayout implements View.OnClickListener,OnItemClickListener,
													View.OnLongClickListener,View.OnTouchListener{

	public InputRecoderView(Context context) {
		super(context);
		init(context);
	}
	public InputRecoderView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}
	public InputRecoderView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(context);
	}
	
	private static final String TAG = "InputRecoderView";
	
	private InputMethodManager inputMethodManager;
	private InputExpressionView expressionView;   // 表情控件
	private VolumeStateView shortRecView;   // 录音倒计时的pop
	
	private EditText editInput;     
	private Button btnInputText;     
	private Button btnBiaoQing;     
	private Button btnMic;     
	private Button btnJianpan;     
	private Button btnRecoder;
	private Button btnSend;
	
	
	private ExtAudioRecorder ear;   //录音
	
	private boolean isOnRecorderButton = false;
	private boolean supportAudioReg = false;
	private boolean isLongPressed = false;
	
	private AudioRecoderBean audioBean;   // 录音后对象
	private StringBuilder reg_text;   // 翻译后文字
	private Activity activity;
	private String userid;
	
	private String replyName;
	
	private OnSendListener sendListener;
	
	private static final String KEY_STATE = "key_state";
	
	//本地记录状态      true 语音 ，false 文字
	private boolean isMicstate = false;   
	
	private String inputStrKey = "";
	
	
	private static HashMap<String, String> strCache = new HashMap<String, String>();
	
	private boolean inited = false;
	
	
	/**
	 * 注册广播
	 */
	public void mRegisterReceiver(){
		try {
			// 注册录音广播
			LocalBroadcastManager.getInstance(activity).registerReceiver(mMessageReceiver,
					new IntentFilter(ExtAudioRecorder.AUDIO_RECORDER_MSG));
			// 注册转文字广播
//			LocalBroadcastManager.getInstance(activity).registerReceiver(mMessageReceiver,
//				      new IntentFilter(QISR_TASK.QISR_RESULT_MSG));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 解绑广播
	 */
	public void mUnRegisterReceiver(){
		try {
			LocalBroadcastManager.getInstance(activity).unregisterReceiver(mMessageReceiver);
			
			if (ear != null && ear.isRecording) {
				isLongPressed = false;
				isOnRecorderButton = false;
				TickUpHelper.getInstance(handler).stop(0);
				ear.stop();
				btnRecoder.setOnLongClickListener(this);
				if (shortRecView != null) {
					shortRecView.dismiss();
				}
			}
			
			SharedPreferences sp = this.getContext().getSharedPreferences(GuideActivity.SP_NAME, Context.MODE_PRIVATE);
			sp.edit().putBoolean(KEY_STATE, isMicstate).commit();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	
	private void init(Context context) {
		
		SharedPreferences sp = context.getSharedPreferences(GuideActivity.SP_NAME, Context.MODE_PRIVATE);
		isMicstate = sp.getBoolean(KEY_STATE, false);
		
		activity = (Activity) context;
		userid = ActiveAccount.getInstance(context).getLookLookID();
		
		View view = LayoutInflater.from(context).inflate(R.layout.input_recoder_view, null);
		LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.MATCH_PARENT);
		this.addView(view, params);
		
		editInput    = (EditText) view.findViewById(R.id.edit_input);
		editInput.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View arg0, MotionEvent arg1) {
				switch (arg1.getAction()) {
				case MotionEvent.ACTION_DOWN:
					break;
				case MotionEvent.ACTION_UP:
					onClick(btnInputText);
				}
				return false;
			}
		});
		editInput.addTextChangedListener(mEditCommListener);
		
		btnInputText = (Button) view.findViewById(R.id.btn_text_input);
		btnBiaoQing = (Button) view.findViewById(R.id.btn_biaoqing_input);
		btnMic = (Button) view.findViewById(R.id.btn_mic_input);
		btnJianpan = (Button) view.findViewById(R.id.btn_jianpan);
		btnRecoder = (Button) view.findViewById(R.id.btn_recorder);
		btnSend = (Button) view.findViewById(R.id.btn_send);
		
		btnInputText.setOnClickListener(this);
		btnBiaoQing.setOnClickListener(this);
		btnMic.setOnClickListener(this);
		btnJianpan.setOnClickListener(this);
		btnRecoder.setOnLongClickListener(this);
		btnSend.setOnClickListener(this);
		
		btnRecoder.setOnTouchListener(this);
		
		View expressionLayout = view.findViewById(R.id.expression_relative_layout);
		expressionView = new InputExpressionView(this.getContext(),expressionLayout, editInput);
		expressionView.setOnclickListener(this);
		
		shortRecView = new VolumeStateView(activity);
		ear = ExtAudioRecorder.getInstanse(false, 2, 1);
		
		supportAudioReg = ExtAudioRecorder.CheckPlugin();
		
//		editInput.setOnEditorActionListener(this);
		
		
		// 语音
		if(isMicstate){
			
			setInputMicState();
			
		}else{ // 文字
			
			setInputTextState();
		}
		
		inited = true;
	}
	
	private void setInputTextState() {
		
		// view初始化完成后做统计
		if(inited){
			// mishare 2014-4-8
			CmmobiClickAgentWrapper.onEvent(this.getContext(), "edit_content");
		}
					
		isMicstate = false;
		btnInputText.setVisibility(View.GONE);
		btnMic.setVisibility(View.VISIBLE);
		
		editInput.setVisibility(View.VISIBLE);
		editInput.requestFocus();
		btnRecoder.setVisibility(View.GONE);
		btnRecoder.setTextColor(getResources().getColor(R.color.blue));
		btnRecoder.setBackgroundResource(R.drawable.btn_audio_input);
		
		hideExpressionView();
		
		btnSend.setVisibility(View.VISIBLE);
	}
	
	private void setInputMicState() {
		
		isMicstate = true;
		btnMic.setVisibility(View.GONE);
		btnInputText.setVisibility(View.VISIBLE);
		
		editInput.setVisibility(View.GONE);
		btnRecoder.setVisibility(View.VISIBLE);
		btnRecoder.setTextColor(getResources().getColor(R.color.blue));
		btnRecoder.setBackgroundResource(R.drawable.btn_audio_input);
		editInput.clearFocus();
		
		hideExpressionView();
		
		btnSend.setVisibility(View.INVISIBLE);
	}
	
	/**
	 * 设置发送输入的回调
	 * @param sendListener
	 */
	public void setOnSendListener(OnSendListener sendListener){
		this.sendListener = sendListener;
	}
	
	
	/**
	 * 必须调用 输入文字缓存key
	 * 
	 * @param type
	 * @param _inputStrKey
	 */
	public void setInputStrKey(InputStrType type, String _inputStrKey){
		inputStrKey = type.toString() + _inputStrKey;
		
		String value = strCache.get(inputStrKey);
		
		if(!TextUtils.isEmpty(value)){
			editInput.setText(value);
		}else{
			editInput.setText("");
		}
	}
	
	/**
	 * 设置回复名称
	 * @param replyName
	 */
	public void setReplyName(String replyName){
		this.replyName = replyName;
		
		// 文本输入
		FriendsExpressionView.replacedExpressionsHint("回复"+ replyName+":", editInput);
		// 语音输入
		FriendsExpressionView.replacedExpressions("按住回复："+replyName, btnRecoder);
		
		// 语音
		if(isMicstate){
			setInputMicState();
		}else{ // 文字
			setInputTextState();
		}
	}
	
	public void setNoReply(){
		this.replyName = "";
		clearView();
	}
	 
	
	/**
	 * 清除控件状态
	 */
	public void clearView(){
		audioBean = null;
		replyName = "";
//		editInput.setText("");
		editInput.setHint("最多输入500字");
		if (expressionView.getExpressionView().isShown()) {
			expressionView.getExpressionView().setVisibility(View.GONE);
		}
		
		if(btnJianpan.isShown()){
			btnJianpan.setVisibility(View.GONE);
			btnBiaoQing.setVisibility(View.VISIBLE);
		}
		btnRecoder.setText("按住说话");
		btnRecoder.setBackgroundResource(R.drawable.btn_audio_input);
		btnRecoder.setTextColor(getResources().getColor(R.color.blue));
		
		hideSoftInputFromWindow();
		
		this.setVisibility(View.GONE);
	}
	
	/**
	 * 设置录音按钮是否可用
	 */
	public void setRecordBtnEnabled(boolean enabled)
	{
		btnRecoder.setEnabled(enabled);
	}
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_text_input:   //  T 输入文字
			
			setInputTextState();
			
			showSoftInput();
			
			break;
		case R.id.btn_mic_input:   // 麦克
			
			setInputMicState();
			
			hideSoftInputFromWindow();
			
			break;
		case R.id.btn_biaoqing_input:   // 表情
			
			isMicstate = false;
			
			View exview = expressionView.getExpressionView();
			if (View.GONE == exview.getVisibility()) {
				expressionView.showExpressionView(activity);
				editInput.requestFocus();
			}
			v.setVisibility(View.GONE);
			btnJianpan.setVisibility(View.VISIBLE);
			
			if(btnRecoder.isShown()){
				btnInputText.setVisibility(View.GONE);
				btnMic.setVisibility(View.VISIBLE);
				editInput.setVisibility(View.VISIBLE);
				btnRecoder.setVisibility(View.GONE);
				editInput.requestFocus();
			}
			
			btnSend.setVisibility(View.VISIBLE);
			
			break;
		case R.id.btn_jianpan:   // 键盘
			
			onClick(btnInputText);
			
			break;
		case R.id.btn_send:   // 发送
			
			String cmm = editInput.getText().toString().trim();
			if(!TextUtils.isEmpty(cmm)){
				callbackSend();
			}
			
			break;
		default:
			break;
		}
	}
	
	/**
	 * 选择表情
	 */
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		editInput.append(view.getTag().toString());		
		
		// mishare 2014-4-8
		CmmobiClickAgentWrapper.onEvent(this.getContext(), "edit_emote", ""+FriendsExpressionView.getExpIndex(view.getTag().toString()));
	}
	
	Handler handler = new Handler(){
		public void dispatchMessage(android.os.Message msg) {
			switch (msg.what) {
			case TickUpHelper.HANDLER_FLAG_TICK_UP:   // 倒计时 更新pop时间
				int secordsLeft = (Integer) msg.obj;
				if(shortRecView!=null){
					shortRecView.updateTime(30 - secordsLeft);
				}
				break;
			case TickUpHelper.HANDLER_FLAG_TICK_STOP:   // 停止 录音
				btnRecoder.setOnLongClickListener(InputRecoderView.this);
				ear.stop();
				isLongPressed = false;
				shortRecView.dismiss();
				break;
			case QISR_TASK.HANDLER_QISR_RESULT_CLEAN:   // 开始语音解码
				reg_text = new StringBuilder();
				break;
			case QISR_TASK.HANDLER_QISR_RESULT_ADD:  // 完成语音解码
				if(msg.obj!=null){
					Bundle b = msg.getData();
					if(b!=null && b.getString("content")!=null){
						reg_text.append(b.getString("content"));
					}
				}
				break;
			case ExtAudioRecorder.HANDLER_AUDIO_RECORDER_DONE:  // 录音完成
				Log.e(TAG, "ExtAudioRecorder.HANDLER_AUDIO_RECORDER_DONE:");
				if(shortRecView!=null){
					shortRecView.dismiss();
				}
				
				try {
					String fullPath = Environment.getExternalStorageDirectory() + audioBean.localFilePath;
					
					Mp4InfoUtils tmp4info = new Mp4InfoUtils(fullPath);
					
					audioBean.playtime = (int)tmp4info.totaltime+"";
					
					if (!ZFileSystem.isFileExists(fullPath) || tmp4info.totaltime < 2) {
						audioBean = null;
						break;
					}
					// mishare 2014-4-8   统计语音及网络
					CmmobiClickAgentWrapper.onEventDuration(InputRecoderView.this.getContext(), "voice",CmmobiClickAgentWrapper.getNetworkLabel(
							NetworkTypeUtility.getNetwork(InputRecoderView.this.getContext())),((int)tmp4info.totaltime)*1000);
					
					if(isOnRecorderButton){
						callbackSend();
					}else{
						audioBean = null;
					}
					
				} catch (Exception e) {
					e.printStackTrace();
				}
				
//				if(!supportAudioReg){
//					handler.obtainMessage(QISR_TASK.HANDLER_QISR_RESULT_DONE, (String)msg.obj).sendToTarget();
//				}
					
				break;
			/*case QISR_TASK.HANDLER_QISR_RESULT_DONE:  // 解码完成
				
				if(!TextUtils.isEmpty(reg_text)){
					if(audioBean != null){
						audioBean.content = reg_text.toString();
					}
					editInput.setText(reg_text.toString());
				}else{
					editInput.setText("");
					if(audioBean != null){
						audioBean.content = "";
					}
				}
				
				break;*/
			default:
				break;
			}
		};
	};
	
	@Override
	public boolean onTouch(View v, MotionEvent event) {
		
//		if(!isLongPressed){
//		}else{ // 录音
			
			switch (event.getAction()) {
			case MotionEvent.ACTION_UP:
				
				btnRecoder.setBackgroundResource(R.drawable.btn_audio_input);
				btnRecoder.setTextColor(getResources().getColor(R.color.blue));
				if(TextUtils.isEmpty(replyName)){
					btnRecoder.setText("按住说话");
				}else{
					//btnRecoder.setText("按住回复："+replyName);
					FriendsExpressionView.replacedExpressions("按住回复："+replyName, btnRecoder);
				}
				if(shortRecView.isShowing() /*isLongPressed*/){
					/*isLongPressed = false;
					vs.dismissView();*/
					TickUpHelper.getInstance(handler).stop(2);
				}
				break;
			case MotionEvent.ACTION_DOWN:
				btnRecoder.setBackgroundResource(R.drawable.btn_audio_input_press);
				btnRecoder.setTextColor(getResources().getColor(R.color.white));
				btnRecoder.setText("松开结束");
				if(isLongPressed){
					if (expressionView.getExpressionView().isShown()) {
						expressionView.getExpressionView().setVisibility(View.GONE);
					}
					hideSoftInputFromWindow();
				}
				break;
			case MotionEvent.ACTION_MOVE:
				if(isLongPressed){
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
				}
			}
//		}
		return false;
	}
	
	@Override
	public boolean onLongClick(View v) {
		switch(v.getId()){
		case R.id.btn_recorder:   // 开始录音
			
//			btnRecoder.setBackgroundResource(R.drawable.btn_press_speek_press);
			
			isLongPressed = true;
			isOnRecorderButton = true;
			
			hideSoftInputFromWindow();
			
			audioBean = new AudioRecoderBean();
			
			String audioID = String.valueOf(TimeHelper.getInstance().now());
			String shortPath = Constant.SD_STORAGE_ROOT + "/" + userid + "/shortaudio";
			String localFilePath = shortPath + "/" + audioID + ".mp4";
			
			audioBean.commentuuid = audioID;
			audioBean.localFilePath = localFilePath;
			
			ear.setHandler(handler);
			Log.d(TAG,"supportAudioReg = " + supportAudioReg);
			if (!ear.start(audioID,shortPath, false, 4, supportAudioReg)) {
				Prompt.Alert(activity, "录音失败");
				isOnRecorderButton = false;
				if (ear != null && ear.getStatus() == State.RECORDING) {
					ear.stop();
					btnRecoder.setOnLongClickListener(this);
				}
				break;
			}
			
			btnRecoder.setOnLongClickListener(null);
			shortRecView.show();
			shortRecView.updateTime(30);
			
			break;
		}
		return true;
	}
	
	
	/**
	 * 接收录音广播
	 */
	private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			int type;
			Message msg = new Message();
			if (ExtAudioRecorder.AUDIO_RECORDER_MSG.equals(intent
					.getAction())) {
				type = intent.getIntExtra("type", 0);
			    String content = intent.getStringExtra("content");
			    String playtime = intent.getStringExtra("playtime");
			    msg.what = type;
			    msg.obj = content;
			    if(playtime!=null){
				    Bundle b = new  Bundle();
				    b.putString("playtime", playtime);
				    msg.setData(b);
			    }
			} else if (QISR_TASK.QISR_RESULT_MSG.equals(intent.getAction())){
				type = intent.getIntExtra("type", 0);
				String content = intent.getStringExtra("content");
			    String audioID = intent.getStringExtra("audioID");
			    Bundle b = new  Bundle();
			    b.putString("content", content);
			    b.putString("audioid", audioID);
			    msg.setData(b);
			    msg.what = type;
			    msg.obj = content;
			}

			handler.sendMessage(msg);
		}
	};
	
	
	/*@Override
	public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
		if(actionId == EditorInfo.IME_ACTION_SEND){
			if (!TextUtils.isEmpty(v.getText())) {
				callbackSend();
				ZLog.e("--> onEditorAction ++");
			}else{
				Prompt.Alert(activity, "请输入消息内容");
			}
		}
		return false;
	}*/

	/**
	 * 调用回调
	 */
	public void callbackSend(){
		
		String cmm = editInput.getText().toString().trim();
		try {
			// 替换搜噶输入法的表情
			cmm = EmojiPaser.getInstance().format(cmm);
		} catch (Exception e) {
			e.printStackTrace();
		}
		if(TextUtils.isEmpty(cmm) && audioBean == null){
			Prompt.Alert(activity, "请输入消息内容");
		}else{
			if(audioBean == null){
				// 发送文字
				audioBean = new AudioRecoderBean();
				audioBean.commenttype = "1";
				audioBean.commentuuid = String.valueOf(TimeHelper.getInstance().now());;
				audioBean.content = cmm;
				
				if(!TextUtils.isEmpty(inputStrKey)){
					strCache.remove(inputStrKey);
				}
			}else{
//				if(!TextUtils.isEmpty(cmm)){
//					// 语音+文字
//					audioBean.commenttype = "3";
//					audioBean.content = cmm;
//				}else{
					// 语音
					audioBean.commenttype = "2";
//					audioBean.content = cmm;
//				}
			}
			
			ZLog.e("--> callbackSend ++");
			
			
			if(sendListener!=null){
				sendListener.onSend(audioBean);
			}
		}
		if("1".equals(audioBean.commenttype)){
			editInput.setText("");
//		}else if("2".equals(audioBean.commenttype)){
//			audioBean = null;
		}
		audioBean = null;
		replyName = "";
	}
	
	
	/**
	 * 显示键盘
	 */
	public void showSoftInput(){
		try {
			if(inputMethodManager == null){
				inputMethodManager = ((InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE));
			}
			inputMethodManager.showSoftInput(activity.getCurrentFocus(), 0);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 显示键盘
	 */
	public void showSoftKeyBoard(){
		if (!isMicstate) {
			try {
				editInput.requestFocus();
				if(inputMethodManager == null){
					inputMethodManager = ((InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE));
				}
				inputMethodManager.showSoftInput(activity.getCurrentFocus(), 0);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	public void requestInputFocus() {
		editInput.requestFocus();
	}
	
	/**
	 * 隐藏键盘
	 */
	public void hideSoftInputFromWindow(){
		try {
			if(inputMethodManager == null){
				inputMethodManager = ((InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE));
			}
//			if(activity.getCurrentFocus()!=null){
				inputMethodManager.hideSoftInputFromWindow(this.getWindowToken(),
						InputMethodManager.HIDE_NOT_ALWAYS);
//			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	/**
	 * 判断表情是否显示
	 * @return
	 */
	public boolean isExpressionShow(){
		
		return expressionView.getExpressionView().isShown();
	}
	
	/**
	 * 隐藏表情
	 */
	public void hideExpressionView(){
		if (isExpressionShow()) {
			expressionView.hideExpressionView(activity);
		}
		if(btnJianpan.isShown()){
			btnJianpan.setVisibility(View.GONE);
			btnBiaoQing.setVisibility(View.VISIBLE);
		}
	}
	
	/**
     * 昵称的TextChange
     */
	private TextWatcher mEditCommListener = new TextWatcher(){
		@Override
		public void beforeTextChanged(CharSequence s, int start, int count,
				int after) {
		}
		@Override
		public void onTextChanged(CharSequence s, int start, int before,
				int count) {
		}
		@Override
		public void afterTextChanged(Editable s) {
			String str = editInput.getText().toString();
			if(!TextUtils.isEmpty(str)){
				btnSend.setTextColor(getResources().getColor(R.color.blue));
			}else{
//				if(audioBean == null){
				btnSend.setTextColor(getResources().getColor(R.color.gray));
//				}else{
//					btnSend.setTextColor(getResources().getColor(R.color.blue));
//				}
			}
			
			// 加入回复缓存
			if(!TextUtils.isEmpty(inputStrKey)){
				strCache.put(inputStrKey, str);
			}
		}
  };
	
	/**
	 * 控件输入完带发送回调
	 */
	public interface OnSendListener{
		
		void onSend(AudioRecoderBean bean);
		
	}
	
	
	
	public class AudioRecoderBean{
		public String commentuuid;
		public String localFilePath;
		public String content;
		public String playtime;
		public String commenttype;  //  1文字 2语音 3、声音加文字
	}
	
	
	public enum InputStrType{
		
		COMMENT, PRIVATE_MSG;
		
	};
	
	
	
}
