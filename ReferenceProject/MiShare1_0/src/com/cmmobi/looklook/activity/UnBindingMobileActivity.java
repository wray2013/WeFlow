package com.cmmobi.looklook.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import cn.zipper.framwork.core.ZDialog;

import com.cmmobi.looklook.R;
import com.cmmobi.looklook.activity.login.BindingMobileNoActivity;
import com.cmmobi.looklook.common.gson.GsonResponse3;
import com.cmmobi.looklook.common.gson.GsonResponse3.MyBind;
import com.cmmobi.looklook.common.gson.Requester3;
import com.cmmobi.looklook.common.utils.CmmobiClickAgentWrapper;
import com.cmmobi.looklook.info.profile.AccountInfo;
import com.cmmobi.looklook.info.profile.ActiveAccount;
import com.cmmobi.looklook.info.profile.LoginSettingManager;
import com.cmmobi.looklook.prompt.Prompt;
import com.cmmobi.looklook.prompt.TickUpHelper;

public class UnBindingMobileActivity extends TitleRootActivity {
	
	
	private TextView mMobileNo;
	private TextView mTvMobileOK;
//	private TextView mTvMobileError;
	private Button   mBtnSendMms;
	
	private EditText mEditYzm;
	private TextView mTvYzmOK;
	private TextView mTvYzmError;
	private TextView mTvYzm;
	private String bindedPhonNo;
	private String countryNo = "";
	private ProgressBar verifyProBar = null;
	private boolean isSended = false;
	private LinearLayout spaceLayout = null;
	private static final int HANDLER_FLAG_ENABLE_YZM = 0x012383799;
	private InputMethodManager inputMethodManager;
	
	@Override
	public int subContentViewId() {
		return R.layout.activity_unbinding_mobile;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setTitle("变更手机号绑定");
		setRightButtonText("下一步");
		getRightButton().setTextColor(this.getResources().getColor(R.color.gray));
		
//		setLeftButtonText("暂不绑定");
//		getLeftButton().setTextColor(this.getResources().getColor(R.color.blue));
		
		
		mMobileNo = (TextView) findViewById(R.id.edit_mobile_no);
		mTvMobileOK = (TextView) findViewById(R.id.tv_mobile_ok);
//		mTvMobileError = (TextView) findViewById(R.id.tv_mobile_error);
		mBtnSendMms = (Button) findViewById(R.id.btn_send_mms);
		mBtnSendMms.setOnClickListener(this);
		
		mEditYzm = (EditText) findViewById(R.id.edit_yzm);
		mTvYzmOK = (TextView) findViewById(R.id.tv_yzm_ok);
		mTvYzmError = (TextView) findViewById(R.id.tv_yzm_error);
		mTvYzm = (TextView) findViewById(R.id.tv_yzm);
		
		verifyProBar = (ProgressBar) findViewById(R.id.pb_verify_wait);
		
//		mMobileNo.addTextChangedListener(mMobleNoTextWatcher);
		mEditYzm.addTextChangedListener(mYzmTextWatcher);
		mEditYzm.setVisibility(View.GONE);
		
		spaceLayout = (LinearLayout) findViewById(R.id.ll_space_area);
		spaceLayout.setOnClickListener(this);
		
//		TickUpHelper.getInstance(getHandler()).stop(0);
		setBtnBingingEnabled(false);
		
		inputMethodManager = ((InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE));
		
		ActiveAccount activeAccount = ActiveAccount.getInstance(this);
		String userID = activeAccount.getUID();
		AccountInfo accountInfo = AccountInfo.getInstance(userID);
		if (accountInfo != null) {
			LoginSettingManager lsm = accountInfo.setmanager;
			// 手机账号绑定
			MyBind phonebindstate = lsm.getBinding_type(
					LoginSettingManager.BINDING_TYPE_PHONE,
					LoginSettingManager.BINDING_INFO_POINTLESS);
			if (phonebindstate != null) {
				bindedPhonNo = phonebindstate.binding_info;
				countryNo = phonebindstate.country_code;
				if (countryNo == null) {
					countryNo = "";
				}
				if (bindedPhonNo != null) {
					mMobileNo.setText(bindedPhonNo);
				}
			}
		}
	}
	
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		CmmobiClickAgentWrapper.onPause(this);
		super.onPause();
	}
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		CmmobiClickAgentWrapper.onResume(this);
		super.onResume();
	}
	
	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		CmmobiClickAgentWrapper.onStop(this);
		super.onStop();
		TickUpHelper.getInstance(handler).stop(0);
	}
	
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_title_right: // 触发完成绑定
			CmmobiClickAgentWrapper.onEvent(this, "next_step");
			
			Intent intent = new Intent();
			intent.putExtra(BindingMobileNoActivity.INTENT_REQUEST_BINGED, true);
			intent.setClass(this, BindingMobileNoActivity.class);
			startActivity(intent);
			finish();
//			ZDialog.show(R.layout.progressdialog, true, true, this);
//			Requester3.changeBinding(handler, "1", bindedPhonNo,mEditYzm.getText().toString().trim() , "", "", "");
			break;
		case R.id.btn_send_mms: // 发送验证码
			if (!isFastDoubleClick()) {
				break;
			}
			isSended = true;
			ZDialog.show(R.layout.progressdialog, true, true, this);
			Requester3.getCheckNo(getHandler(), /*countryNo +*/ bindedPhonNo, "5");
			mEditYzm.setVisibility(View.VISIBLE);
			// 2014-4-8
			CmmobiClickAgentWrapper.onEvent(this, "send_text","1");
			break;
		case R.id.ll_space_area:
			hideSoftInputFromWindow();
			break;
		default:
			break;
		}
		super.onClick(v);
	}
	
	/**
	 * 隐藏键盘
	 */
	public void hideSoftInputFromWindow(){
		try {
			if(inputMethodManager == null){
				inputMethodManager = ((InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE));
			}
//			if(activity.getCurrentFocus()!=null){
				inputMethodManager.hideSoftInputFromWindow(mEditYzm.getWindowToken(),
						InputMethodManager.HIDE_NOT_ALWAYS);
//			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private static long lastClickTime;

	public static boolean isFastDoubleClick()
	{
		long time = System.currentTimeMillis();
		long timeD = time - lastClickTime;
		if (0 < timeD && timeD < 800)
		{
			return false;
		}
		lastClickTime = time;
		return true;
	}
	
	
	@Override
	public boolean handleMessage(Message msg) {
		switch (msg.what) {
		case Requester3.RESPONSE_TYPE_CHECKNO:
			ZDialog.dismiss();
			if(msg.obj!=null){
				GsonResponse3.checkNoResponse obj = (GsonResponse3.checkNoResponse)(msg.obj);
				if(obj!=null && obj.status!=null && obj.status.equals("0")){
					getHandler().sendEmptyMessage(HANDLER_FLAG_ENABLE_YZM);
					Prompt.Dialog(this, false, "提醒", "验证码已发出，请注意查收" , null);
				}else{
					Prompt.Dialog(this, false, "提醒", Prompt.GetStatus(obj.status, obj.crm_status), null);
				}					
			}else{
				Prompt.Dialog(this, false, "提醒", "网络异常" , null);
			}
			break;
		case Requester3.RESPONSE_TYPE_CHANGEBINDING:
			mEditYzm.setEnabled(true);
			verifyProBar.setVisibility(View.GONE);
			ZDialog.dismiss();
			if(msg.obj!=null){
				GsonResponse3.changeBindingResponse obj = (GsonResponse3.changeBindingResponse)(msg.obj);
				if(obj!=null && obj.status!=null && obj.status.equals("0")){
					if (mTvYzmError.getVisibility() == View.GONE && !TextUtils.isEmpty(mEditYzm.getText().toString())) {
						mEditYzm.setVisibility(View.GONE);
						mTvYzmOK.setVisibility(View.VISIBLE);
				    	mTvYzmError.setVisibility(View.GONE);
				    	mBtnSendMms.setVisibility(View.GONE);
				    	mTvYzm.setText(mEditYzm.getText().toString());
				    	mTvYzm.setVisibility(View.VISIBLE);
				    	setBtnBingingEnabled(true);
					}
				}else if(obj!=null && obj.status!=null && obj.status.equals("200600")){
					if ("手机验证码错误".equals(Prompt.GetStatus(obj.status, obj.crm_status))) {
						mTvYzmError.setVisibility(View.VISIBLE);
					} else {
						Prompt.Dialog(this, false, "提醒", Prompt.GetStatus(obj.status, obj.crm_status), null);
					}
				}else{
					Prompt.Dialog(this, false, "提醒", "网络异常" , null);
				}
			}
			break;
		case HANDLER_FLAG_ENABLE_YZM:
			TickUpHelper.getInstance(getHandler()).init();
			TickUpHelper.getInstance(getHandler()).start(60);
			mBtnSendMms.setEnabled(false);
			break;
			
		case TickUpHelper.HANDLER_FLAG_TICK_UP:
			int tick = (Integer) msg.obj;
			mBtnSendMms.setText("重新发送\n( " + (60 - tick) + "秒)");
			break;
		case TickUpHelper.HANDLER_FLAG_TICK_STOP:
			if (isSended) {
				mBtnSendMms.setText("重新发送");
				mBtnSendMms.setEnabled(true);
//				mEditYzm.setVisibility(View.GONE);
//				mTvYzmError.setVisibility(View.GONE);
			} else {
				mBtnSendMms.setText("发送");
			}

			break;
			
		default:
			break;
		}
		return false;
	}
	
	
	/*private TextWatcher mMobleNoTextWatcher = new TextWatcher(){
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
			String mobileNo = mMobileNo.getText().toString().trim();
			if(!TextUtils.isEmpty(mobileNo)){
				if(Prompt.checkPhoneNum(mobileNo)){
					showMobileStataOk();
				}else{
					showMobileStataError();
				}
			}else{
				clearMobileStata();
			}
		}
    };*/

    private TextWatcher mYzmTextWatcher = new TextWatcher(){
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
			String yzm = mEditYzm.getText().toString();
			if(!TextUtils.isEmpty(yzm)){
				if(Prompt.checkYZM(yzm)){
					showYzmStataOk();
				}else{
					showYzmStataError();
				}
			}else{
				clearYzmStata();
			}
		}
    };
    
    
    /**
     * 显示用户名状态OK
     */
    private void showMobileStataOk(){
    	mTvMobileOK.setVisibility(View.VISIBLE);
//    	mTvMobileError.setVisibility(View.GONE);
    	mBtnSendMms.setVisibility(View.VISIBLE);
//    	checkBindingBtnState();
    }
    
    /**
     * 清楚用户名状态
     */
    private void clearMobileStata(){
    	mTvMobileOK.setVisibility(View.GONE);
//    	mTvMobileError.setVisibility(View.GONE);
    	mBtnSendMms.setVisibility(View.GONE);
//    	checkBindingBtnState();
    }
	
    /**
     * 显示用户名状态错误
     */
    private void showMobileStataError(){
    	mTvMobileOK.setVisibility(View.GONE);
//    	mTvMobileError.setVisibility(View.VISIBLE);
//    	mTvMobileError.setText("手机格式错误");
    	mBtnSendMms.setVisibility(View.GONE);
//    	checkBindingBtnState();
    }
    
    /**
     * 显示用户名已存在状态
     */
    private void showMobileStataExist(){
    	mTvMobileOK.setVisibility(View.GONE);
//    	mTvMobileError.setVisibility(View.VISIBLE);
//    	mTvMobileError.setText(errStr);
    	mBtnSendMms.setVisibility(View.GONE);
//    	checkBindingBtnState();
    }
    
    
    /**
     * 显示验证码状态OK
     */
    private void showYzmStataOk(){
//    	mTvYzmOK.setVisibility(View.VISIBLE);
    	mTvYzmError.setVisibility(View.GONE);
//    	ZDialog.show(R.layout.progressdialog, true, true, this);
    	verifyProBar.setVisibility(View.VISIBLE);
		Requester3.changeBinding(handler, "1", bindedPhonNo,mEditYzm.getText().toString().trim() , "", "", "");
//    	checkBindingBtnState();
		mEditYzm.setEnabled(false);
    }
    
    /**
     * 显示验证码状态Error
     */
    private void showYzmStataError(){
    	mTvYzmOK.setVisibility(View.GONE);
    	mTvYzmError.setVisibility(View.VISIBLE);
    	verifyProBar.setVisibility(View.GONE);
    	setBtnBingingEnabled(false);
//    	checkBindingBtnState();
    }
    
    /**
     * 清楚用户名状态
     */
    private void clearYzmStata(){
    	mTvYzmOK.setVisibility(View.GONE);
    	mTvYzmError.setVisibility(View.GONE);
//    	checkBindingBtnState();
    }
    
    
    /**
     * 检查注册按钮状态
     */
    private void checkBindingBtnState(){
    	if(mTvYzmOK.isShown()){
    		setBtnBingingEnabled(true);
    	}else{
    		setBtnBingingEnabled(false);
    	}
    }
    
    /**
     * 更具状态设置注册按钮
     * @param enabled
     */
    private void setBtnBingingEnabled(boolean enabled){
    	if(enabled){
    		getRightButton().setTextColor(this.getResources().getColor(R.color.blue));
    	}else{
    		getRightButton().setTextColor(this.getResources().getColor(R.color.gray));
    	}
    	getRightButton().setEnabled(enabled);
    }
    
}
