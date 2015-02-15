package com.cmmobi.looklook.activity;

import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import cn.zipper.framwork.core.ZActivity;
import cn.zipper.framwork.core.ZDialog;
import cn.zipper.framwork.core.ZViewFinder;

import com.cmmobi.looklook.R;
import com.cmmobi.looklook.common.constant.Constant;
import com.cmmobi.looklook.common.gson.GsonResponse2;
import com.cmmobi.looklook.common.gson.Requester2;
import com.cmmobi.looklook.common.utils.CmmobiClickAgentWrapper;
import com.cmmobi.looklook.common.utils.UmengclickAgentWrapper;
import com.cmmobi.looklook.prompt.Prompt;

/**
 * 意见反馈界面
 * 
 * @author youtian
 * 
 */
public class SettingFeedBackActivity extends ZActivity implements TextWatcher {

	private TextView textnum;
	private EditText edit;
	private int Num = 140;
	private String feedbackcontent;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setting_feedback);
		
		ZViewFinder finder = getZViewFinder();
		finder.setOnClickListener(R.id.iv_commit, this);
		finder.setOnClickListener(R.id.iv_back, this);
		textnum = finder.findTextView(R.id.textnum);
		edit = finder.findEditText(R.id.edit);
		edit.addTextChangedListener(this);

	}
	
	@Override
	public void onResume() {
		super.onResume();
		UmengclickAgentWrapper.onResume(this);
		CmmobiClickAgentWrapper.onResume(this);
	}
	
	@Override
	public void onPause() {
		super.onPause();
		UmengclickAgentWrapper.onPause(this);
		CmmobiClickAgentWrapper.onPause(this);
	}
	
	@Override
	public void onStop() {
		super.onStop();
		CmmobiClickAgentWrapper.onStop(this);
	}

	@Override
	public void afterTextChanged(Editable s) {
		String editNum = edit.getText().toString();
		if (editNum.length() <= 140) {
			textnum.setText("" + (Num - editNum.length()));
		} else {
			edit.setText(editNum.substring(0, Num));
			edit.setSelection(editNum.substring(0, Num).length());
		}
	}

	@Override
	public void beforeTextChanged(CharSequence s, int start, int count,
			int after) {

	}

	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean handleMessage(Message msg) {

		switch (msg.what) {

		case Requester2.RESPONSE_TYPE_FEEDBACK:
			try {
				ZDialog.dismiss();
				GsonResponse2.feedbackResponse response = (GsonResponse2.feedbackResponse) msg.obj;				
				if (response != null){ 
					if(response.status.equals("0")) {
						Prompt.Dialog(this, false, "提示", "谢谢，已成功反馈", new DialogInterface.OnClickListener(){

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								// TODO Auto-generated method stub
								SettingFeedBackActivity.this.finish();
							}});
						
					}else if(response.status.equals("200600")){
						Prompt.Dialog(this, false, "提示", Constant.CRM_STATUS[Integer.parseInt(response.crm_status)], null);
					}else{
						Prompt.Dialog(this, false, "提示", "操作失败，请稍后再试", null);
					}
				}else{
					Prompt.Dialog(this, false, "提示", "操作失败，网络不给力", null);
				}
			} catch (Exception e) {
				// TODO: handle exception
			}
			break;

		}

		return false;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.iv_commit:
			feedbackcontent = edit.getText().toString().trim();
			if(feedbackcontent != null){
				ZDialog.show(R.layout.progressdialog, false, true, this);
				Requester2.feedback(getHandler(), feedbackcontent);
			}else{
				Prompt.Dialog(this, false, "提示", "请输入反馈的内容", null);
			}
			break;
		case R.id.iv_back:
			finish();
			break;
		}

	}
	
	@Override
	public void onBackPressed(){
		super.onBackPressed();
	}
}
