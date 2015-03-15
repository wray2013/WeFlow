package com.etoc.weflow.activity.login;

import android.os.Bundle;
import android.os.Message;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.etoc.weflow.R;
import com.etoc.weflow.activity.TitleRootActivity;
import com.etoc.weflow.dialog.PromptDialog;
import com.etoc.weflow.utils.StringUtils;
import com.etoc.weflow.utils.TickDownHelper;

public class RegisterResetActivity extends TitleRootActivity {

	public static final int TYPE_REGIST = 0;
	public static final int TYPE_RESET  = 1;
	
	private static final int STEP_ONE = 1;
	private static final int STEP_TWO = 2;
	
	private int currentType = TYPE_REGIST;
	private int currentStep = STEP_ONE;
	
	private TickDownHelper tickDown = null;
	private boolean hasGetValidCode = false;
	
	//UI Components
	private TextView tvStep1, tvStep2, tvValidCode, tvBtnNext;
	private View vDivider;
	private EditText edAccount, edValidCode;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		if(getIntent() != null) {
			currentType = getIntent().getIntExtra("type", TYPE_REGIST);
		}
		
		switch(currentType) {
		case TYPE_REGIST:
			setTitleText("注册流量钱包");
			setTitleGravity(GRAVITE_CENTER);
			break;
		case TYPE_RESET:
			setTitleText("忘记密码");
			setTitleGravity(GRAVITE_LEFT);
			break;
		}
		hideRightButton();
		
		initView();
		
		refreshViewStatus();
	}
	
	private void initView() {
		// TODO Auto-generated method stub
		edAccount   = (EditText) findViewById(R.id.et_account);
		edValidCode = (EditText) findViewById(R.id.et_password);
		
		tvStep1     = (TextView) findViewById(R.id.tv_step1);
		tvStep2     = (TextView) findViewById(R.id.tv_step2);
		vDivider    = findViewById(R.id.view_line);
		tvValidCode = (TextView) findViewById(R.id.tv_valid_code);
		tvBtnNext   = (TextView) findViewById(R.id.tv_next_btn);
		
		tvValidCode.setOnClickListener(this);
		tvBtnNext.setOnClickListener(this);
		
		tickDown = new TickDownHelper(handler);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch(v.getId()) {
		case R.id.tv_valid_code: //发送验证码
			if (StringUtils.isEmpty(edAccount.getText().toString())) {
				PromptDialog.Dialog(this, "温馨提示", "请填写手机号", "确定");
			} else if (PromptDialog.checkPhoneNum(edAccount.getText().toString())) {
//				Requester.sendSMS(handler, edAccount.getText().toString());
				hasGetValidCode = true;
				tvValidCode.setEnabled(false);
				tvValidCode.setText("重新发送(60)");
				tickDown.start(60);
			} else {
				PromptDialog.Dialog(this, "温馨提示", "手机号格式错误", "确定");
			}
			break;
		case R.id.tv_next_btn: //下一步
			doNext();
			break;
		}
		super.onClick(v);
	}
	
	private void doNext() {
		switch(currentStep) {
		case STEP_ONE:
			//TODO:check validNum
			if (StringUtils.isEmpty(edAccount.getText().toString())) {
				PromptDialog.Dialog(this, "温馨提示", "请填写手机号", "确定");
			} else if(!PromptDialog.checkPhoneNum(edAccount.getText().toString())) {
				PromptDialog.Dialog(this, "温馨提示", "手机号格式错误", "确定");
			} else {
			// ……
				currentStep = STEP_TWO;
				refreshViewStatus();
			}
			break;
		case STEP_TWO:
			
			break;
		}
	}
	
	private void refreshViewStatus() {
		edAccount.setText("");
		edValidCode.setText("");
		switch(currentType) {
		case TYPE_REGIST:
			tvStep2.setText("2.设置密码");
			if(currentStep == STEP_ONE) {
				tvStep1.setTextColor(getResources().getColor(R.color.pagertab_color_orange));
				tvStep2.setTextColor(getResources().getColor(R.color.text_grey));
				vDivider.setVisibility(View.VISIBLE);
				tvValidCode.setVisibility(View.VISIBLE);
				
				edAccount.setHint("请输入手机号");
				edValidCode.setHint("请输入验证码");
				tvBtnNext.setText("下一步");
				
			} else if(currentStep == STEP_TWO) {
				tvStep2.setTextColor(getResources().getColor(R.color.pagertab_color_orange));
				tvStep1.setTextColor(getResources().getColor(R.color.text_grey));
				vDivider.setVisibility(View.GONE);
				tvValidCode.setVisibility(View.GONE);
				
				edAccount.setHint("请输入密码");
				edValidCode.setHint("再次输入密码");
				tvBtnNext.setText("完成");
				
			}
			break;
		case TYPE_RESET:
			tvStep2.setText("2.重置密码");
			if(currentStep == STEP_ONE) {
				tvStep1.setTextColor(getResources().getColor(R.color.pagertab_color_orange));
				tvStep2.setTextColor(getResources().getColor(R.color.text_grey));
				vDivider.setVisibility(View.VISIBLE);
				tvValidCode.setVisibility(View.VISIBLE);
				
				edAccount.setHint("请输入手机号");
				edValidCode.setHint("请输入验证码");
				tvBtnNext.setText("下一步");
				
			} else if(currentStep == STEP_TWO) {
				tvStep2.setTextColor(getResources().getColor(R.color.pagertab_color_orange));
				tvStep1.setTextColor(getResources().getColor(R.color.text_grey));
				vDivider.setVisibility(View.GONE);
				tvValidCode.setVisibility(View.GONE);
				
				edAccount.setHint("请输入新密码");
				edValidCode.setHint("再次输入新密码");
				tvBtnNext.setText("完成");
				
			}
			break;
		}
	}

	@Override
	public boolean handleMessage(Message msg) {
		// TODO Auto-generated method stub
		switch(msg.what) {
		case TickDownHelper.HANDLER_FLAG_TICK_DOWN:
			Integer sec = (Integer)msg.obj;
			tvValidCode.setText("重新发送(" + sec + ")");
			break;
		case TickDownHelper.HANDLER_FLAG_TICK_STOP:
			hasGetValidCode = false;
			Integer secStop = (Integer)msg.obj;
			tvValidCode.setEnabled(true);
			tvValidCode.setText("获取验证码");
			break;
		}
		return false;
	}

	@Override
	public int subContentViewId() {
		// TODO Auto-generated method stub
		return R.layout.activity_register_reset;
	}

}
