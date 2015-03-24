package com.etoc.weflow.activity;

import com.etoc.weflow.R;
import com.etoc.weflow.WeFlowApplication;
import com.etoc.weflow.dao.AccountInfo;
import com.etoc.weflow.dialog.PromptDialog;
import com.etoc.weflow.net.GsonResponseObject.FeedBackResp;
import com.etoc.weflow.net.Requester;
import com.etoc.weflow.utils.StringUtils;

import android.os.Bundle;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

public class FeedBackActivity extends TitleRootActivity {

	private TextView tvBtnSubmit, tvCharRemain;
	private EditText etContent;
	
	private int maxinputnum = 300;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		setTitleText("意见反馈");
		hideRightButton();
		
		initView();
		
	}
	
	private void initView() {
		// TODO Auto-generated method stub
		tvBtnSubmit = (TextView) findViewById(R.id.tv_btn_submit);
		tvCharRemain = (TextView) findViewById(R.id.tv_char_num);
		tvCharRemain.setText(maxinputnum + "");
		
		etContent = (EditText) findViewById(R.id.et_feedback);
		refreshBtnStatus(false);
		
		etContent.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence s, int arg1, int arg2, int arg3) {
				
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int arg1, int arg2,
					int arg3) {
				
			}
			
			@Override
			public void afterTextChanged(Editable edit) {
				String s = edit.toString();
				int current = maxinputnum - s.length();
				tvCharRemain.setText(current + "");
				refreshBtnStatus(s.length() > 0);
			}
		});
		
		tvBtnSubmit.setOnClickListener(this);
		
	}
	
	private void refreshBtnStatus(boolean canSubmit) {
		tvBtnSubmit.setEnabled(canSubmit);
	}
	
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch(v.getId()) {
		case R.id.tv_btn_submit:
			if (StringUtils.isEmpty(etContent.getText().toString())) {
				PromptDialog.Dialog(this, "温馨提示", "请输入一些意见吧！", "确定");
			}
			AccountInfo accountInfo = WeFlowApplication.getAppInstance().getAccountInfo();
			if (accountInfo != null && accountInfo.getUserid() != null) {
				Requester.feedBack(true, handler, accountInfo.getUserid(), etContent.getText().toString());
			}
			break;
		}
		super.onClick(v);
	}

	@Override
	protected int graviteType() {
		// TODO Auto-generated method stub
		return GRAVITE_LEFT;
	}
	
	@Override
	public boolean handleMessage(Message msg) {
		// TODO Auto-generated method stub
		switch(msg.what) {
		case Requester.RESPONSE_TYPE_FEED_BACK:
			if (msg.obj != null) {
				FeedBackResp resp = (FeedBackResp) msg.obj;
				if (Requester.isSuccessed(resp.status)) {
					PromptDialog.Alert("发送成功");
					finish();
				} else {
					PromptDialog.Alert("发送失败");
				}
			}
			break;
		}
		return false;
	}

	@Override
	public int subContentViewId() {
		// TODO Auto-generated method stub
		return R.layout.activity_feedback;
	}

}
