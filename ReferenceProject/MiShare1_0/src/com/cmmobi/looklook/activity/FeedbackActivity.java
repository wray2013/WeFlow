package com.cmmobi.looklook.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import cn.zipper.framwork.core.ZActivity;
import cn.zipper.framwork.core.ZDialog;

import com.cmmobi.looklook.R;
import com.cmmobi.looklook.common.constant.Constant;
import com.cmmobi.looklook.common.gson.GsonResponse3;
import com.cmmobi.looklook.common.gson.Requester3;
import com.cmmobi.looklook.common.utils.CmmobiClickAgentWrapper;
import com.cmmobi.looklook.info.profile.AccountInfo;
import com.cmmobi.looklook.info.profile.ActiveAccount;
import com.cmmobi.looklook.prompt.Prompt;

/**
 * 意见反馈
 * 
 * @author youtian
 * 
 */
public class FeedbackActivity extends ZActivity implements TextWatcher{

	private ImageView iv_back; //返回
	private TextView tv_commit; //提交
	private EditText et_feedback;
	private TextView tv_count;
	private final int max = 500;
	private AccountInfo accountInfo;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_feedback);

		accountInfo = AccountInfo.getInstance(ActiveAccount.getInstance(this).getUID());
		
		iv_back = (ImageView) findViewById(R.id.iv_back);
		iv_back.setOnClickListener(this);
		iv_back.setOnLongClickListener(new View.OnLongClickListener() {
			
			@Override
			public boolean onLongClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(FeedbackActivity.this,LookLookActivity.class);
				intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				startActivity(intent);
				FeedbackActivity.this.finish();
				accountInfo.feedback = et_feedback.getText().toString().trim();
				return false;
			}
		});
		tv_commit = (TextView) findViewById(R.id.tv_commit);
		tv_commit.setOnClickListener(this);
		
		et_feedback = (EditText) findViewById(R.id.et_feedback);
		tv_count = (TextView) findViewById(R.id.tv_count);
     	
		if (accountInfo != null && accountInfo.feedback != null) {
			et_feedback.setText(accountInfo.feedback);
			et_feedback.setSelection(accountInfo.feedback.length());
			tv_count.setText(accountInfo.feedback.length() + "/" + max);
		}
		
		et_feedback.addTextChangedListener(this);
		
	}
	
	@Override
	public void onResume() {
		super.onResume();
		CmmobiClickAgentWrapper.onResume(this);
	}
	
	@Override
	public void onPause() {
		super.onPause();
		CmmobiClickAgentWrapper.onPause(this);
	}
	
	@Override
	public void onStop() {
		super.onStop();
		CmmobiClickAgentWrapper.onStop(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.iv_back:
			accountInfo.feedback = et_feedback.getText().toString().trim();
			this.finish();
			break;
		case R.id.tv_commit:
			String feedback = et_feedback.getText().toString().trim();
			accountInfo.feedback = feedback;
			if(feedback == null || feedback.equals("")){

			}else{
				ZDialog.show(R.layout.progressdialog, false, true, this);
				Requester3.feedback(handler, feedback);
			}
			break;
		}
	}

	@Override
	public boolean handleMessage(Message msg) {
		// TODO Auto-generated method stub
		switch (msg.what) {
		case Requester3.RESPONSE_TYPE_FEEDBACK: 
			ZDialog.dismiss();
			try {
				GsonResponse3.feedbackResponse res = (GsonResponse3.feedbackResponse) msg.obj;
				if(res != null){
					if ("0".equals(res.status)) {
						Prompt.Dialog(this, false, "提示", "发送成功", new DialogInterface.OnClickListener() {
							
							@Override
							public void onClick(DialogInterface dialog, int which) {
								// TODO Auto-generated method stub
								accountInfo.feedback = "";
								finish();
							}
						});
						
					} else {
						if(res.status.equals("200600")){
							Prompt.Dialog(this, false, "提示", Constant.CRM_STATUS[Integer.parseInt(res.crm_status)], null);
						}else{
							Prompt.Dialog(this, false, "提示", "操作失败，请稍后再试", null);
						}
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
	public void afterTextChanged(Editable s) {
		// TODO Auto-generated method stub
		String editNum = et_feedback.getText().toString();
		if (editNum.length() > max) {
			et_feedback.setText(editNum.substring(0, max));
			et_feedback.setSelection(editNum.substring(0, max).length());
		}
		tv_count.setText(editNum.length() + "/" + max);
	}

	@Override
	public void beforeTextChanged(CharSequence s, int start, int count,
			int after) {
		// TODO Auto-generated method stub
	}

	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) {
		// TODO Auto-generated method stub
		
	}
}
