package com.etoc.weflow.activity.login;

import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.text.InputType;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.etoc.weflow.R;
import com.etoc.weflow.activity.TitleRootActivity;
import com.etoc.weflow.dialog.PromptDialog;
import com.etoc.weflow.net.GsonResponseObject.getAuthCodeResponse;
import com.etoc.weflow.net.GsonResponseObject.registerResponse;
import com.etoc.weflow.net.GsonResponseObject.resetPasswordResponse;
import com.etoc.weflow.net.GsonResponseObject.verifyAuthCodeResponse;
import com.etoc.weflow.net.Requester;
import com.etoc.weflow.utils.StringUtils;
import com.etoc.weflow.utils.TickDownHelper;
import com.etoc.weflow.utils.ViewUtils;

public class RegisterResetActivity extends TitleRootActivity {

	public static final int TYPE_REGIST = 1;
	public static final int TYPE_RESET  = 2;
	
	private static final int STEP_ONE = 1;
	private static final int STEP_TWO = 2;
	
	private int currentType = TYPE_REGIST;
	private int currentStep = STEP_ONE;
	private String currentTel = "";
	
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
		
		ViewUtils.setHeight(findViewById(R.id.ll_login_top), 72);
		ViewUtils.setHeight(findViewById(R.id.view_divier_v), 48);
		ViewUtils.setHeight(findViewById(R.id.view_line), 48);
		ViewUtils.setHeight(tvBtnNext, 113);
		ViewUtils.setHeight(findViewById(R.id.rl_account), 113);
		ViewUtils.setHeight(edValidCode, 113);
		ViewUtils.setWidth(tvValidCode, 192);
		
		ViewUtils.setMarginTop(findViewById(R.id.rl_account), 32);
		ViewUtils.setMarginLeft(findViewById(R.id.rl_account), 32);
		ViewUtils.setMarginRight(findViewById(R.id.rl_account), 32);
		
		ViewUtils.setMarginTop(edValidCode, 36);
		ViewUtils.setMarginLeft(edValidCode, 32);
		ViewUtils.setMarginRight(edValidCode, 32);
		
		ViewUtils.setMarginTop(findViewById(R.id.rl_login_bottom), 36);
		ViewUtils.setMarginLeft(tvBtnNext, 32);
		ViewUtils.setMarginRight(tvBtnNext, 32);
		
		ViewUtils.setTextSize(edAccount, 32);
		ViewUtils.setTextSize(edValidCode, 32);
		ViewUtils.setTextSize(tvBtnNext, 32);
		ViewUtils.setTextSize(tvStep1, 32);
		ViewUtils.setTextSize(tvStep2, 32);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch(v.getId()) {
		case R.id.tv_valid_code: //发送验证码
			if (StringUtils.isEmpty(edAccount.getText().toString())) {
				PromptDialog.Dialog(this, "温馨提示", "请填写手机号", "确定");
			} else if (PromptDialog.checkPhoneNum(edAccount.getText().toString())) {
				Requester.sendSMS(handler, edAccount.getText().toString(), currentType + "");
				hasGetValidCode = true;
				tvValidCode.setEnabled(false);
				tvValidCode.setText("重新发送(60)");
				tvValidCode.setTextColor(getResources().getColor(R.color.text_grey));
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
			} else if(StringUtils.isEmpty(edValidCode.getText().toString())) {
				PromptDialog.Dialog(this, "温馨提示", "请填写收到的验证码", "确定");
			} else {
				
				currentTel =  edAccount.getText().toString();
				Requester.verifyAuthCode(handler, currentTel, edValidCode.getText().toString(), currentType + "");
				/*currentStep = STEP_TWO;
				refreshViewStatus();*/
			}
			break;
		case STEP_TWO:
			if (StringUtils.isEmpty(edAccount.getText().toString()) ||
					StringUtils.isEmpty(edValidCode.getText().toString())) {
				PromptDialog.Dialog(this, "温馨提示", "请设置密码", "确定");
			} else if (!edAccount.getText().toString()
					.equals(edValidCode.getText().toString())) {
				PromptDialog.Dialog(this, "温馨提示", "两次密码输入不一致，请重新设置", "确定");
			} else {
				if(currentType == TYPE_REGIST) { //注册
					Requester.register(handler, edAccount.getText().toString(), edValidCode.getText().toString());
				} else if(currentType == TYPE_RESET) { //密码重置
					Requester.resetPassword(handler, currentTel, edValidCode.getText().toString());
				}
			}
			break;
		}
	}
	
	private void refreshViewStatus() {
		edAccount.requestFocus();
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
				edAccount.setInputType(InputType.TYPE_CLASS_PHONE);
				edValidCode.setInputType(InputType.TYPE_CLASS_NUMBER);
				tvBtnNext.setText("下一步");
				
			} else if(currentStep == STEP_TWO) {
				tvStep2.setTextColor(getResources().getColor(R.color.pagertab_color_orange));
				tvStep1.setTextColor(getResources().getColor(R.color.text_grey));
				vDivider.setVisibility(View.GONE);
				tvValidCode.setVisibility(View.GONE);
				
				edAccount.setHint("请输入密码");
				edValidCode.setHint("再次输入密码");
				edAccount.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
				edValidCode.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
				tvBtnNext.setText("完  成");
				
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
				edAccount.setInputType(InputType.TYPE_CLASS_PHONE);
				edValidCode.setInputType(InputType.TYPE_CLASS_NUMBER);
				tvBtnNext.setText("下一步");
				
			} else if(currentStep == STEP_TWO) {
				tvStep2.setTextColor(getResources().getColor(R.color.pagertab_color_orange));
				tvStep1.setTextColor(getResources().getColor(R.color.text_grey));
				vDivider.setVisibility(View.GONE);
				tvValidCode.setVisibility(View.GONE);
				
				edAccount.setHint("请输入新密码");
				edValidCode.setHint("再次输入新密码");
				edAccount.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
				edValidCode.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
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
			tvValidCode.setTextColor(getResources().getColor(R.color.text_grey));
			break;
		case TickDownHelper.HANDLER_FLAG_TICK_STOP:
			hasGetValidCode = false;
			Integer secStop = (Integer)msg.obj;
			tvValidCode.setEnabled(true);
			tvValidCode.setText("获取验证码");
			tvValidCode.setTextColor(getResources().getColor(R.color.pagertab_color_orange));
			break;
		case Requester.RESPONSE_TYPE_SENDSMS:
			getAuthCodeResponse getAuthResp = (getAuthCodeResponse) msg.obj;
			if(getAuthResp != null) {
				if("0".equals(getAuthResp.status)) { //发送成功
					PromptDialog.Alert(RegisterResetActivity.class, "验证码发送成功，请查收");
				}
			} else {
				PromptDialog.Alert(RegisterResetActivity.class, "您的网络不给力啊！");
			}
			break;
		case Requester.RESPONSE_TYPE_VERIFY_CODE:
			verifyAuthCodeResponse codeResp = (verifyAuthCodeResponse) msg.obj;
			if(codeResp != null) {
				if("0".equals(codeResp.status)) { //验证成功
					currentStep = STEP_TWO;
					refreshViewStatus();
				}
			} else {
				PromptDialog.Alert(RegisterResetActivity.class, "您的网络不给力啊！");
			}
			break;
		case Requester.RESPONSE_TYPE_REGISTER:
			registerResponse regResp = (registerResponse) msg.obj;
			if(regResp != null) {
				if("0".equals(regResp.status)) { //注册成功
					PromptDialog.Alert(RegisterResetActivity.class, "成功注册");
					//TODO:跳转主页
					
				}
			} else {
				PromptDialog.Alert(RegisterResetActivity.class, "您的网络不给力啊！");
			}
			break;
		case Requester.RESPONSE_TYPE_RESET_PWD:
			resetPasswordResponse resetResp = (resetPasswordResponse) msg.obj;
			if(resetResp != null) {
				if("0".equals(resetResp.status)) { //重置成功
					PromptDialog.Alert(RegisterResetActivity.class, "密码修改成功,请重新登录");
					//TODO:跳转登录页
					Intent loginIntent = new Intent(this, LoginActivity.class);
					loginIntent.putExtra("tel", currentTel);
					startActivity(loginIntent);
					finish();
				}
			} else {
				PromptDialog.Alert(RegisterResetActivity.class, "您的网络不给力啊！");
			}
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
