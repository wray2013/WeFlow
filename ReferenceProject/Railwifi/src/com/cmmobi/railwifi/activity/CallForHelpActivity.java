package com.cmmobi.railwifi.activity;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Message;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.cmmobi.railwifi.R;
import com.cmmobi.railwifi.dialog.PromptDialog;
import com.cmmobi.railwifi.network.GsonResponseObject;
import com.cmmobi.railwifi.network.GsonResponseObject.commonContent;
import com.cmmobi.railwifi.network.Requester;
import com.cmmobi.railwifi.utils.DisplayUtil;
import com.cmmobi.railwifi.utils.MyTextWatcher;
import com.cmmobi.railwifi.utils.ViewUtils;
import com.cmmobi.railwifi.view.ListDialog;

public class CallForHelpActivity extends TitleRootActivity implements TextWatcher {

	private RelativeLayout rlContactInfo;
	private EditText etName;
	private EditText etPhone;
	private TextView tvCredentials;
	private EditText etCredentialNo;
	private EditText etCar;
	private EditText etSeatNo;
	private TextView tvHelpType;
	private EditText etDetail;
	private ImageView ivPhoneError;
	private ImageView ivIdError;
	private ImageView ivNameError;
	private ImageView ivCarError;
	private ImageView ivSeatError;
	private Boolean isClickedSend = false;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setTitleText("旅客求助");
		setRightButtonText("发送>>");
		initViews();	
	}
	
	private void initViews() {
		rlContactInfo = (RelativeLayout) findViewById(R.id.rl_contact_info);
		ViewUtils.setMarginTop(rlContactInfo, 12);
		ViewUtils.setMarginBottom(rlContactInfo, 12);
		ViewUtils.setMarginLeft(rlContactInfo, 12);
		ViewUtils.setMarginRight(rlContactInfo, 12);
		etName = (EditText) findViewById(R.id.et_contact_name);
		etPhone = (EditText) findViewById(R.id.et_contact_celphone);
		tvCredentials = (TextView) findViewById(R.id.tv_credentials_type);
		tvCredentials.setPadding(DisplayUtil.getSize(this, 18), 0, DisplayUtil.getSize(this, 33), 0);
		tvCredentials.setTypeface(Typeface.MONOSPACE, Typeface.BOLD_ITALIC);
		etCredentialNo = (EditText) findViewById(R.id.et_contact_id_num);
		etCar = (EditText) findViewById(R.id.et_car_no);
		etSeatNo = (EditText) findViewById(R.id.et_seat_no);
		tvHelpType = (TextView) findViewById(R.id.tv_help_type);
		tvHelpType.setPadding(DisplayUtil.getSize(this, 18), 0, DisplayUtil.getSize(this, 33), 0);
		tvHelpType.setTypeface(Typeface.MONOSPACE, Typeface.BOLD_ITALIC);
		
		ivPhoneError = (ImageView) findViewById(R.id.iv_phone_error);
		RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) ivPhoneError.getLayoutParams();
		lp.height = DisplayUtil.getSize(this, 64);
		lp.width = lp.height;
		lp.rightMargin = DisplayUtil.getSize(this, 12);
		ivPhoneError.setLayoutParams(lp);
		
		ivIdError  = (ImageView) findViewById(R.id.iv_id_error);
		lp = (RelativeLayout.LayoutParams) ivIdError.getLayoutParams();
		lp.height = DisplayUtil.getSize(this, 64);
		lp.width = lp.height;
		lp.rightMargin = DisplayUtil.getSize(this, 12);
		ivIdError.setLayoutParams(lp);
		
		ivNameError = (ImageView) findViewById(R.id.iv_name_error);
		lp = (RelativeLayout.LayoutParams) ivNameError.getLayoutParams();
		lp.height = DisplayUtil.getSize(this, 64);
		lp.width = lp.height;
		lp.rightMargin = DisplayUtil.getSize(this, 12);
		ivNameError.setLayoutParams(lp);
		
		ivCarError = (ImageView) findViewById(R.id.iv_car_error);
		lp = (RelativeLayout.LayoutParams) ivCarError.getLayoutParams();
		lp.height = DisplayUtil.getSize(this, 64);
		lp.width = lp.height;
		lp.rightMargin = DisplayUtil.getSize(this, 12);
		ivCarError.setLayoutParams(lp);
		
		ivSeatError = (ImageView) findViewById(R.id.iv_seat_error);
		lp = (RelativeLayout.LayoutParams) ivSeatError.getLayoutParams();
		lp.height = DisplayUtil.getSize(this, 64);
		lp.width = lp.height;
		lp.rightMargin = DisplayUtil.getSize(this, 12);
		ivSeatError.setLayoutParams(lp);
		
		etDetail = (EditText) findViewById(R.id.et_detail);
		etDetail.addTextChangedListener(this);
		ViewUtils.setSize(etName, 522, 99);
		ViewUtils.setSize(etPhone, 522, 99);
		ViewUtils.setSize(tvCredentials, 522, 99);
		ViewUtils.setSize(etCredentialNo, 522, 99);
		ViewUtils.setSize(etSeatNo, 522, 99);
		ViewUtils.setSize(etCar, 522, 99);
		ViewUtils.setSize(tvHelpType, 522, 99);
		ViewUtils.setMarginRight(etDetail, 12);
		ViewUtils.setMarginLeft(etDetail, 12);
		
		((TextView) findViewById(R.id.tv_contact_name)).setTextSize(DisplayUtil.textGetSizeSp(this, 36));
		((TextView) findViewById(R.id.tv_contact_celphone)).setTextSize(DisplayUtil.textGetSizeSp(this, 36));
		((TextView) findViewById(R.id.tv_credentials)).setTextSize(DisplayUtil.textGetSizeSp(this, 36));
		((TextView) findViewById(R.id.tv_contact_id_num)).setTextSize(DisplayUtil.textGetSizeSp(this, 36));
		((TextView) findViewById(R.id.tv_car)).setTextSize(DisplayUtil.textGetSizeSp(this, 36));
		((TextView) findViewById(R.id.tv_seat)).setTextSize(DisplayUtil.textGetSizeSp(this, 36));
		((TextView) findViewById(R.id.tv_help)).setTextSize(DisplayUtil.textGetSizeSp(this, 36));
		ViewUtils.setMarginTop(findViewById(R.id.tv_contact_name), 24);
		ViewUtils.setMarginLeft(findViewById(R.id.tv_contact_name), 12);
		findViewById(R.id.rl_all).setPadding(0, 0, 0, DisplayUtil.getSize(this, 12));
		
		ViewUtils.setSize(findViewById(R.id.tv_contact_name), 150, 99);
		ViewUtils.setSize(findViewById(R.id.tv_contact_celphone), 150, 99);
		ViewUtils.setSize(findViewById(R.id.tv_credentials), 150, 99);
		ViewUtils.setSize(findViewById(R.id.tv_contact_id_num), 150, 99);
		ViewUtils.setSize(findViewById(R.id.tv_car), 150, 99);
		ViewUtils.setSize(findViewById(R.id.tv_seat), 150, 99);
		ViewUtils.setSize(findViewById(R.id.tv_help), 150, 99);
		ViewUtils.setSize(etDetail, 696, 248);
		
		tvCredentials.setOnClickListener(this);
		tvHelpType.setOnClickListener(this);
		
		etName.addTextChangedListener(new CallforHelpTextWatcher(etName));
		etPhone.addTextChangedListener(new CallforHelpTextWatcher(etPhone));
		etCredentialNo.addTextChangedListener(new CallforHelpTextWatcher(etCredentialNo));
		etSeatNo.addTextChangedListener(new CallforHelpTextWatcher(etSeatNo));
		etCar.addTextChangedListener(new CallforHelpTextWatcher(etCar));
		
		etDetail.setOnTouchListener(new OnTouchListener() {
            
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // TODO Auto-generated method stub
                v.getParent().requestDisallowInterceptTouchEvent(true);
                return false;
            }
        }); 
		
		Requester.requestBaseInfo(handler);
	}
	
	private class CallforHelpTextWatcher implements TextWatcher{
		private EditText et;
		public CallforHelpTextWatcher(EditText et) {
			// TODO Auto-generated constructor stub
			this.et = et;
		}
		
		@Override
		public void onTextChanged(CharSequence s, int start, int before, int count) {
			// TODO Auto-generated method stub
			et.setHintTextColor(0xff888888);
			if(et.getId() == R.id.et_contact_celphone){
				ivPhoneError.setVisibility(View.GONE);
			}else if(et.getId() == R.id.et_contact_id_num){
				ivIdError.setVisibility(View.GONE);
			}else if(et.getId() == R.id.et_contact_name){
				ivNameError.setVisibility(View.GONE);
			}else if(et.getId() == R.id.et_seat_no){
				ivSeatError.setVisibility(View.GONE);
			}else if(et.getId() == R.id.et_car_no){
				ivCarError.setVisibility(View.GONE);
			}
		}
		
		@Override
		public void beforeTextChanged(CharSequence s, int start, int count,
				int after) {
			// TODO Auto-generated method stub
			
		}
		
		@Override
		public void afterTextChanged(Editable s) {
			// TODO Auto-generated method stub
			
		}
	};
	
	@Override
	public boolean handleMessage(Message msg) {
		// TODO Auto-generated method stub
		switch (msg.what) {
		case Requester.RESPONSE_TYPE_TRAIN_HELP:
			GsonResponseObject.TrainHelpResp response = (GsonResponseObject.TrainHelpResp) msg.obj;
			
			if(response!=null && "0".equals(response.status)){
			/*	PromptDialog.Dialog(this, false, "操作成功", "提交求助成功！", "确定", "", new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						finish();
					}
				}, null);*/
				Toast.makeText(this, "发送成功", Toast.LENGTH_LONG).show();
				finish();
			}else{
				//PromptDialog.Dialog(this, "操作失败", "当前网络不佳，提交求助失败！", "稍后再试");
				//Toast.makeText(this, "发送失败", Toast.LENGTH_LONG).show();
			}
			break;
		case Requester.RESPONSE_TYPE_BASE_INFO:
			GsonResponseObject.BaseInfoResp baseInfoResp = (GsonResponseObject.BaseInfoResp) msg.obj;		
			if(baseInfoResp!=null && "0".equals(baseInfoResp.status)&& !TextUtils.isEmpty(baseInfoResp.train_num)){
				if(isClickedSend){
					isClickedSend = false;
					InputMethodManager imm = (InputMethodManager) this
							  .getSystemService(Context.INPUT_METHOD_SERVICE);
					if(TextUtils.isEmpty(etName.getText().toString())){
						etName.setHintTextColor(0xffc60606);
						etName.setFocusable(true);
						etName.requestFocus();
						imm.showSoftInput(etName, 0);
					}else if(!PromptDialog.checkName(etName.getText().toString())){
						ivNameError.setVisibility(View.VISIBLE);
						etName.requestFocus();
						imm.showSoftInput(etName, 0);
					}else if(TextUtils.isEmpty(etPhone.getText().toString())){
						etPhone.setHintTextColor(0xffc60606);
						etPhone.requestFocus();
						imm.showSoftInput(etPhone, 0);
					}else if(!PromptDialog.checkPhoneNum(etPhone.getText().toString())){
						ivPhoneError.setVisibility(View.VISIBLE);
						etPhone.requestFocus();
						imm.showSoftInput(etPhone, 0);
					}else if(TextUtils.isEmpty(etCredentialNo.getText().toString())){
						etCredentialNo.setHintTextColor(0xffc60606);
						etCredentialNo.requestFocus();
						imm.showSoftInput(etCredentialNo, 0);
					}else if(tvCredentials.getText().equals("身份证") && !PromptDialog.checkIdCard(etCredentialNo.getText().toString())){
						ivIdError.setVisibility(View.VISIBLE);
						etCredentialNo.requestFocus();
						imm.showSoftInput(etCredentialNo, 0);
					}else if(tvCredentials.getText().equals("护照") && !PromptDialog.checkPassport(etCredentialNo.getText().toString())){
						ivIdError.setVisibility(View.VISIBLE);
						etCredentialNo.requestFocus();
						imm.showSoftInput(etCredentialNo, 0);
					}else if(TextUtils.isEmpty(etCar.getText().toString())){
						etCar.setHintTextColor(0xffc60606);
						etCar.requestFocus();
						imm.showSoftInput(etCar, 0);
					}else if(!PromptDialog.checkCarNum(etCar.getText().toString())){
						ivCarError.setVisibility(View.VISIBLE);
						etCar.requestFocus();
						imm.showSoftInput(etCar, 0);
					}else if(TextUtils.isEmpty(etSeatNo.getText().toString())){
						etSeatNo.setHintTextColor(0xffc60606);
						etSeatNo.requestFocus();
						imm.showSoftInput(etSeatNo, 0);
					}else if(!PromptDialog.checkSeatNum(etSeatNo.getText().toString())){
						ivSeatError.setVisibility(View.VISIBLE);
						etSeatNo.requestFocus();
						imm.showSoftInput(etSeatNo, 0);
					}else if(TextUtils.isEmpty(etDetail.getText().toString().trim())){
						etDetail.setText("");
						etDetail.setHintTextColor(0xffc60606);
						etDetail.requestFocus();
						imm.showSoftInput(etDetail, 0);
					}else {
						// to request
						String certificatetype = "1";
						if("身份证".equals(tvCredentials.getText().toString().trim())){
							certificatetype = "1";
						}else{
							certificatetype = "2";
						}
						
						String helptype = "1";
						if("重大疾病".equals(tvHelpType.getText().toString().trim())){
							helptype = "1";
						}else if("争斗纠纷".equals(tvHelpType.getText().toString().trim())){
							helptype = "2";
						}else if("举报报警".equals(tvHelpType.getText().toString().trim())){
							helptype = "3";
						}else{
							helptype = "4";
						}
						Requester.requestTrainHelp(handler, etName.getText().toString().trim(), etPhone.getText().toString().trim(), certificatetype, etCredentialNo.getText().toString().trim(), helptype, etCar.getText().toString().trim(), etSeatNo.getText().toString().trim(), etDetail.getText().toString().trim(), baseInfoResp.train_num);
						
						//requestPush(handler, baseInfoResp.dev_id, baseInfoResp.train_num, "旅客求助", "姓名:" + etName.getText().toString().trim() + ", 手机:" + etPhone.getText().toString().trim() + ", 证件类:" + tvCredentials.getText().toString().trim() + ", 证件号:" + etCredentialNo.getText().toString().trim() + ", 车厢:" + etCar.getText().toString().trim() + ", 座位:" + etSeatNo.getText().toString().trim() + ", 类型:" + tvHelpType.getText().toString().trim() + ", 详情:" + etDetail.getText().toString().trim(), "");
					}	
					
				}else{
					getRightButton().setEnabled(true);
				}
			}else{
				if(isClickedSend){
					finish();
				}else{
					getRightButton().setEnabled(false);
				}
				Toast.makeText(this, "检测您当前不在列车上...", Toast.LENGTH_LONG).show();
			}
			break;
		default:
			break;
		}
		return false;
	}

	@Override
	public int subContentViewId() {
		// TODO Auto-generated method stub
		return R.layout.activity_call_help;
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		super.onClick(v);
		switch (v.getId()) {
		case R.id.btn_title_right:
			isClickedSend = true;
			Requester.requestBaseInfo(handler);
			break;
		case R.id.tv_credentials_type:
			ListDialog dialog = new ListDialog(this,tvCredentials);
			dialog.setTitle("证件类型");
			List<String> listStr = new ArrayList<String>();
			listStr.add("身份证");
			listStr.add("护照");
			dialog.setDate(listStr);
			dialog.show();
			break;
		case R.id.tv_help_type:
			ListDialog dialogHelp = new ListDialog(this,tvHelpType);
			dialogHelp.setTitle("类型");
			List<String> listStrHelp = new ArrayList<String>();
			listStrHelp.add("重大疾病");
			listStrHelp.add("争斗纠纷");
			listStrHelp.add("举报报警");
			listStrHelp.add("其它");
			dialogHelp.setDate(listStrHelp);
			dialogHelp.show();
			break;
		default:
			break;
		}
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

	//输入表情前的光标位置
	private int cursorPos;

	@Override
	public void afterTextChanged(Editable s) {
		// TODO Auto-generated method stub
		etDetail.removeTextChangedListener(this);
		String editNumOri = etDetail.getText().toString();
		cursorPos = etDetail.getSelectionStart();
		String editNum = MyTextWatcher.removeExpression(editNumOri);
		if (!editNum.equals(editNumOri)) {
			
			if (cursorPos > editNum.length()) {
				cursorPos = editNum.length();
			}
			etDetail.setText(editNum);
			etDetail.setSelection(cursorPos);
		}
		etDetail.addTextChangedListener(this);
	}
	

	

}
