package com.cmmobi.looklook.common.view;

import android.app.Activity;
import android.os.Message;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupWindow;

import com.cmmobi.looklook.R;
import com.cmmobi.looklook.activity.LookLookActivity;
import com.cmmobi.looklook.common.constant.Constant;
import com.cmmobi.looklook.common.gson.DiaryController;
import com.cmmobi.looklook.fragment.XFragment;
import com.cmmobi.looklook.info.profile.ActiveAccount;
import com.cmmobi.looklook.info.profile.TimeHelper;
import com.cmmobi.looklook.prompt.Prompt;
import com.iflytek.msc.ExtAudioRecorder;

public class InputUtilView implements OnClickListener, OnTouchListener{

	private boolean isPopShow = false;
	private Activity activity;
	private XFragment trFragment;
	private LayoutInflater inflate;
	private View inputPop;
	private PopupWindow inputPopupWindow;

	private ImageView jianpan;
	private ImageView biaoqing;
	private EditText shurukuang;
	private ImageView luyin;
	private ImageView fasong;
	private Message msg;
	private CountDownPopupWindow shortRecView;
	private ExtAudioRecorder eaRecorder;
	
	private boolean isOnRecorderButton = false;
	private long startRecTime = 0;

	public InputUtilView(Activity activity) {
		//trFragment = fragment;
		this.activity = activity;
		inflate = LayoutInflater.from(activity);
	}

	public boolean showInput() {
		inputPop = activity.getLayoutInflater().inflate(
				R.layout.input_popupwindow_view, null);
		// 创建PopupWindow实例
		inputPopupWindow = new PopupWindow(inputPop,
				ViewGroup.LayoutParams.FILL_PARENT,
				ViewGroup.LayoutParams.WRAP_CONTENT, false);

		jianpan = (ImageView) inputPop.findViewById(R.id.jianpan);
		biaoqing = (ImageView) inputPop.findViewById(R.id.expression_input);
		shurukuang = (EditText) inputPop.findViewById(R.id.et_shuru);
		luyin = (ImageView) inputPop.findViewById(R.id.press_talk);
		fasong = (ImageView) inputPop.findViewById(R.id.send);

		inputPopupWindow.setBackgroundDrawable(activity.getResources()
				.getDrawable(R.drawable.del_dot_big));
		inputPopupWindow.setOutsideTouchable(true);

		inputPopupWindow.showAtLocation(inputPop, Gravity.BOTTOM
				| Gravity.CENTER_VERTICAL, 0, 0);
		isPopShow = true;
		return isPopShow;
	}

	public void dismissPop() {
		if (inputPopupWindow != null && inputPopupWindow.isShowing()) {
			inputPopupWindow.dismiss();
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.jianpan:

			break;
		case R.id.expression_input:

			break;
		case R.id.et_shuru:

			break;
		case R.id.press_talk:

			break;
		case R.id.send:

			break;

		default:
			break;
		}

	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		int action = event.getAction();
		if (action == MotionEvent.ACTION_DOWN) {
			if (shortRecView == null) {
				shortRecView = new CountDownPopupWindow(activity);
			}
			shortRecView.show();
			msg = trFragment.getHandler().obtainMessage();
			
			if(!LookLookActivity.isSdcardMountedAndWritable()) {
				Prompt.Alert(activity, "Sdcard不可用，无法录音");
			}else{
				startRecTime = TimeHelper.getInstance().now();
				String audioID = DiaryController.getNextUUID();
				String RECORD_FILE_DIR =  Constant.SD_STORAGE_ROOT + "/" + ActiveAccount.getInstance(activity).getLookLookID() + "/audio";
				eaRecorder.start(audioID,RECORD_FILE_DIR, false, 3, true);
			}
			
		} else if (action == MotionEvent.ACTION_UP) {
			shortRecView.dismiss();

		} else if (action == MotionEvent.ACTION_MOVE) {
			if (event.getY() < 0) {
				isOnRecorderButton = false;
			} else {
				isOnRecorderButton = true;
			}
		}
		return false;
	}
}
