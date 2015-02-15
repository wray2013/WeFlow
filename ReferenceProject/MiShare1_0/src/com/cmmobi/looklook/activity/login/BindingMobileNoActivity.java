package com.cmmobi.looklook.activity.login;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.support.v4.content.LocalBroadcastManager;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import cn.zipper.framwork.core.ZDialog;

import com.cmmobi.looklook.R;
import com.cmmobi.looklook.activity.LookLookActivity;
import com.cmmobi.looklook.activity.TitleRootActivity;
import com.cmmobi.looklook.common.gson.GsonResponse3;
import com.cmmobi.looklook.common.gson.GsonResponse3.MyBind;
import com.cmmobi.looklook.common.gson.Requester3;
import com.cmmobi.looklook.common.utils.CmmobiClickAgentWrapper;
import com.cmmobi.looklook.fragment.MyZoneFragment;
import com.cmmobi.looklook.fragment.SafeboxVShareFragment;
import com.cmmobi.looklook.info.profile.AccountInfo;
import com.cmmobi.looklook.info.profile.ActiveAccount;
import com.cmmobi.looklook.info.profile.LoginSettingManager;
import com.cmmobi.looklook.prompt.Prompt;
import com.cmmobi.looklook.prompt.TickUpHelper;

public class BindingMobileNoActivity extends TitleRootActivity {
	
	
	private EditText mEditMobileNo;
	private TextView mTvMobileOK;
	private TextView mTvMobileError;
	private TextView mTvMobileNo;
	private Button   mBtnSendMms;
	
	private EditText mEditYzm;
	private TextView mTvYzmOK;
	private TextView mTvYzmError;
	private TextView mTvYzm;
	
	private TextView mTvCountryNo;
	private TextView mTvCountryName;
	private String countryNo = "+86";
	private String countryName = "中国";
	private String bindedPhonNo = "";
	private String bindedCountryCode = "";
	
	private LoginSettingManager lsm;
	
	private static final int ACTV_REQUEST_CODE_COUNTRY_LIST = 101;
	private static final int HANDLER_FLAG_ENABLE_YZM = 0x012383799;
	public static final String INTENT_REQUEST_BINGED = "INTENT_REQUEST_BINDED";
	public boolean isAccountBinded = false;
	private boolean isSended = false;
	private ProgressBar verifyProBar = null;
	
	private InputMethodManager inputMethodManager;
	
	private boolean isFromFriend = false;
	
	@Override
	public int subContentViewId() {
		return R.layout.activity_binding_mobile_no;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setTitle("账号绑定");
		setRightButtonText("下一步");
		getRightButton().setTextColor(this.getResources().getColor(R.color.gray));
		
		findViewById(R.id.lly_choose_country).setOnClickListener(this);
		
		
		mEditMobileNo = (EditText) findViewById(R.id.edit_mobile_no);
		mTvMobileNo = (TextView) findViewById(R.id.tv_mobile_no);
		mTvMobileOK = (TextView) findViewById(R.id.tv_mobile_ok);
		mTvMobileError = (TextView) findViewById(R.id.tv_mobile_error);
		mBtnSendMms = (Button) findViewById(R.id.btn_send_mms);
		mBtnSendMms.setOnClickListener(this);
		
		mEditYzm = (EditText) findViewById(R.id.edit_yzm);
		mTvYzm = (TextView) findViewById(R.id.tv_yzm);
		mTvYzmOK = (TextView) findViewById(R.id.tv_yzm_ok);
		mTvYzmError = (TextView) findViewById(R.id.tv_yzm_error);
		
		
		mTvCountryNo = (TextView) findViewById(R.id.tv_country_no);
		mTvCountryName = (TextView) findViewById(R.id.tv_country_name);
		verifyProBar = (ProgressBar) findViewById(R.id.pb_verify_wait);
		
		mEditMobileNo.addTextChangedListener(mMobleNoTextWatcher);
		mEditYzm.addTextChangedListener(mYzmTextWatcher);
		mEditYzm.setVisibility(View.GONE);
		
		findViewById(R.id.ll_space_area).setOnClickListener(this);
		
		String userID = ActiveAccount.getInstance(this).getUID();
		AccountInfo accountInfo = AccountInfo.getInstance(userID);
		if (accountInfo != null) {
			lsm = accountInfo.setmanager;
			
			MyBind phonebindstate = lsm.getBinding_type(
					LoginSettingManager.BINDING_TYPE_PHONE,
					LoginSettingManager.BINDING_INFO_POINTLESS);
			
			if (phonebindstate != null) {
				bindedPhonNo = phonebindstate.binding_info;
				bindedCountryCode = phonebindstate.country_code;
			}
		}
		
		isAccountBinded = getIntent().getBooleanExtra(INTENT_REQUEST_BINGED, false);
		
//		TickUpHelper.getInstance(getHandler()).stop(0);
		setBtnBingingEnabled(false);
		
		isFromFriend = getIntent().getBooleanExtra("isfinish",false);
	}
	
	
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_title_right: // 触发完成绑定
			CmmobiClickAgentWrapper.onEvent(this, "next_step");
			ZDialog.show(R.layout.progressdialog, true, true, this);
			String phoneNo = mEditMobileNo.getText().toString().trim();
			if (isAccountBinded) {
				if (phoneNo.equals(bindedPhonNo) /*&& countryNo.equals(bindedCountryCode)*/) {
					Prompt.Dialog(this, false, "提醒", "原手机与新手机号不能相同" , null);
				} else {
					Requester3.changeBinding(handler, "2", 
							phoneNo,
							mEditYzm.getText().toString().trim(), "", "", "");
				}
			} else {
				Requester3.bindPhone(handler, 
						LoginSettingManager.BINDING_TYPE_PHONE, 
						phoneNo, 
						mEditYzm.getText().toString().trim(),
						"","","");
			}
			break;
		case R.id.btn_send_mms: // 发送验证码
			if (!isFastDoubleClick()) {
				break;
			}
			isSended = true;
			if (isAccountBinded) {
				String phone = mEditMobileNo.getText().toString().trim();
				if (phone.equals(bindedPhonNo) /*&& countryNo.equals(bindedCountryCode)*/) {
					Prompt.Dialog(this, false, "提醒", "原绑定手机号和新绑定手机号不能相同" , null);
				} else {
					Requester3.getCheckNo(getHandler(), mEditMobileNo.getText().toString().trim(), "6");
					ZDialog.show(R.layout.progressdialog, true, true, this);
					mEditYzm.setVisibility(View.VISIBLE);
				}
			} else {
				Requester3.getCheckNo(getHandler(), mEditMobileNo.getText().toString().trim(), "2");
				ZDialog.show(R.layout.progressdialog, true, true, this);
				mEditYzm.setVisibility(View.VISIBLE);
			}
			// 2014-4-8
			CmmobiClickAgentWrapper.onEvent(this, "send_text","1");
			
			break;
		case R.id.lly_choose_country:
			Intent intent = new Intent(this,CountryListActivity.class);
			startActivityForResult(intent, ACTV_REQUEST_CODE_COUNTRY_LIST);
			break;
		case R.id.ll_space_area:
			hideSoftInputFromWindow();
			break;
		default:
			break;
		}
		super.onClick(v);
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
					showMobileStataExist();
				}					
			}else{
				Prompt.Dialog(this, false, "提醒", "网络异常" , null);
			}
			break;
		case Requester3.RESPONSE_TYPE_BINDING:
			ZDialog.dismiss();
			try{
				GsonResponse3.bindingResponse grb = (GsonResponse3.bindingResponse) msg.obj;
				if(grb != null){
					if(grb.status.equals("0")){
						MyBind phoneBind = new MyBind();
						phoneBind.binding_type = LoginSettingManager.BINDING_TYPE_PHONE;
						phoneBind.binding_info = mEditMobileNo.getText().toString().trim();
						phoneBind.country_code = countryNo;
						lsm.addBindingInfo(phoneBind);
						// 2014-4-8
						CmmobiClickAgentWrapper.onEvent(this, "enter_phonenumber", mEditMobileNo.getText().toString().trim().substring(0, 7));
						String isFinish1 = getIntent().getStringExtra(MyZoneFragment.EXTRA_FROM_PROMPT);
						if (isFinish1 == null) {
							CmmobiClickAgentWrapper.onEvent(this, "finshed_blingding1");
						} else {
							CmmobiClickAgentWrapper.onEvent(this, "finshed_blingding2");
						}
						Log.d("==WJM==","isFinish1 = " + isFinish1);
						LocalBroadcastManager.getInstance(this).sendBroadcast(new Intent(SafeboxVShareFragment.ACTION_VSHARE_CHANGED));
						this.setResult(RESULT_OK);
						Prompt.Dialog(this, false, "提示", "绑定成功", new DialogInterface.OnClickListener() {
						
							@Override
							public void onClick(DialogInterface dialog, int which) {
								// TODO Auto-generated method stub
								if(!isFromFriend){
									Intent intent = new Intent(BindingMobileNoActivity.this,LookLookActivity.class);
									intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
									startActivity(intent);
								}
								BindingMobileNoActivity.this.finish();
							}
						});
					}else if(grb.status.equals("200600")){
//						Prompt.Dialog(this, false, "提示", Constant.CRM_STATUS[Integer.parseInt(grb.crm_status)], null);
						if ("手机验证码错误".equals(Prompt.GetStatus(grb.status, grb.crm_status))) {
							mTvYzmError.setVisibility(View.VISIBLE);
						} else {
							Prompt.Dialog(this, false, "提醒", Prompt.GetStatus(grb.status, grb.crm_status), null);
						}
					}else{
						Prompt.Dialog(this, false, "提示", "操作失败，请稍后再试", null);
					}
				}else{
					Prompt.Dialog(this, false, "提示", "操作失败，网络不给力", null);
				}
				
			}catch (Exception e) {
				// TODO: handle exception
			}
			break;
		case Requester3.RESPONSE_TYPE_CHANGEBINDING:
			ZDialog.dismiss();
			try{
				GsonResponse3.changeBindingResponse cbr = (GsonResponse3.changeBindingResponse) msg.obj;
				if(cbr != null){
					if(cbr.status.equals("0")){
						MyBind phoneBind = new MyBind();
						phoneBind.binding_type = LoginSettingManager.BINDING_TYPE_PHONE;
						phoneBind.binding_info = mEditMobileNo.getText().toString().trim();
						phoneBind.country_code = countryNo;
						lsm.deleteBindingInfo(LoginSettingManager.BINDING_TYPE_PHONE, "");
						lsm.addBindingInfo(phoneBind);
						// 2014-4-8
						CmmobiClickAgentWrapper.onEvent(this, "change_phone", mEditMobileNo.getText().toString().trim().substring(0, 7));
						LocalBroadcastManager.getInstance(this).sendBroadcast(new Intent(SafeboxVShareFragment.ACTION_VSHARE_CHANGED));
						this.setResult(RESULT_OK);
						Prompt.Dialog(this, false, "提示", "绑定成功", new DialogInterface.OnClickListener() {
						
							@Override
							public void onClick(DialogInterface dialog, int which) {
								// TODO Auto-generated method stub
								BindingMobileNoActivity.this.finish();
							}
						});
					}else if(cbr.status.equals("200600")){
//						Prompt.Dialog(this, false, "提示", Constant.CRM_STATUS[Integer.parseInt(cbr.crm_status)], null);
						if ("手机验证码错误".equals(Prompt.GetStatus(cbr.status, cbr.crm_status))) {
							mTvYzmError.setVisibility(View.VISIBLE);
						} else {
							Prompt.Dialog(this, false, "提醒", Prompt.GetStatus(cbr.status, cbr.crm_status), null);
						}
					}else{
						Prompt.Dialog(this, false, "提示", "操作失败，请稍后再试", null);
					}
				}else{
					Prompt.Dialog(this, false, "提示", "操作失败，网络不给力", null);
				}
				
			}catch (Exception e) {
				// TODO: handle exception
			}
			break;
		case Requester3.RESPONSE_TYPE_VERIFYSMS:
			verifyProBar.setVisibility(View.GONE);
			GsonResponse3.verifySMSResponse vsr = (GsonResponse3.verifySMSResponse) msg.obj;
			if (vsr != null) {
				if(vsr.status.equals("0")){
					if (mTvMobileOK.getVisibility() == View.VISIBLE 
							&& mTvYzmError.getVisibility() == View.GONE 
							&& !TextUtils.isEmpty(mEditYzm.getText().toString())) {
						setBtnBingingEnabled(true);
						mTvYzmOK.setVisibility(View.VISIBLE);
				    	mTvYzmError.setVisibility(View.GONE);
				    	mTvMobileNo.setVisibility(View.VISIBLE);
				    	mEditMobileNo.setVisibility(View.GONE);
				    	mTvMobileNo.setText(mEditMobileNo.getText().toString());
				    	mTvYzm.setVisibility(View.VISIBLE);
				    	mEditYzm.setVisibility(View.GONE);
				    	mTvYzm.setText(mEditYzm.getText().toString());
				    	mBtnSendMms.setVisibility(View.GONE);
					}
				} else if(vsr.status.equals("200600")){
					if ("手机验证码错误".equals(Prompt.GetStatus(vsr.status, vsr.crm_status))) {
						mTvYzmError.setVisibility(View.VISIBLE);
						mTvYzmOK.setVisibility(View.GONE);
					} else {
						Prompt.Dialog(this, false, "提醒", Prompt.GetStatus(vsr.status, vsr.crm_status), null);
					}
				}else{
					Prompt.Dialog(this, false, "提示", "操作失败，请稍后再试", null);
				}
			}else{
				Prompt.Dialog(this, false, "提示", "操作失败，网络不给力", null);
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
	
	/**
	 * 隐藏键盘
	 */
	public void hideSoftInputFromWindow(){
		try {
			if(inputMethodManager == null){
				inputMethodManager = ((InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE));
			}
			if(getCurrentFocus()!=null){
				inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
						InputMethodManager.HIDE_NOT_ALWAYS);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	private TextWatcher mMobleNoTextWatcher = new TextWatcher(){
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
			String mobileNo = mEditMobileNo.getText().toString().trim();
			if(!TextUtils.isEmpty(mobileNo)){
				if ("+86".equals(countryNo)) {
					if(Prompt.checkPhoneNum(mobileNo)){
						showMobileStataOk();
					}else{
						showMobileStataError();
					}
				} else {
					clearMobileStata_send();
				}
			}else{
				clearMobileStata();
			}
		}
    };

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
    	mTvMobileError.setVisibility(View.GONE);
    	mBtnSendMms.setVisibility(View.VISIBLE);
//    	checkBindingBtnState();
    	
    }
    
    /**
     * 清楚用户名状态
     */
    private void clearMobileStata(){
    	mTvMobileOK.setVisibility(View.GONE);
    	mTvMobileError.setVisibility(View.GONE);
    	mBtnSendMms.setVisibility(View.GONE);
    	setBtnBingingEnabled(false);
//    	checkBindingBtnState();
    }
	
    /**
     * 显示用户名状态错误
     */
    private void showMobileStataError(){
    	mTvMobileOK.setVisibility(View.GONE);
    	mTvMobileError.setVisibility(View.VISIBLE);
    	mTvMobileError.setText("手机格式错误");
    	mBtnSendMms.setVisibility(View.GONE);
    	mEditYzm.setVisibility(View.GONE);
    	mTvYzmError.setVisibility(View.GONE);
    	mTvYzmOK.setVisibility(View.GONE);
    	verifyProBar.setVisibility(View.GONE);
    	setBtnBingingEnabled(false);
//    	checkBindingBtnState();
    }
    
    /**
     * 显示用户名已存在状态
     */
    private void showMobileStataExist(){
    	mTvMobileOK.setVisibility(View.GONE);
    	mTvMobileError.setVisibility(View.VISIBLE);
    	mTvMobileError.setText("账号已被占用");
    	mBtnSendMms.setVisibility(View.GONE);
    	setBtnBingingEnabled(false);
//    	checkBindingBtnState();
    }
    
    
    /**
     * 显示验证码状态OK
     */
    private void showYzmStataOk(){
//    	mTvYzmOK.setVisibility(View.VISIBLE);
    	mTvYzmError.setVisibility(View.GONE);
//    	checkBindingBtnState();
    	String phoneNo = mEditMobileNo.getText().toString().trim();
		if (isAccountBinded) {
			if (phoneNo.equals(bindedPhonNo) && countryNo.equals(bindedCountryCode)) {
				Prompt.Dialog(this, false, "提醒", "原手机与新手机号不能相同" , null);
			} else {
				/*Requester3.changeBinding(handler, "2", 
						countryNo + phoneNo,
						mEditYzm.getText().toString().trim(), "", "", "");*/
				verifyProBar.setVisibility(View.VISIBLE);
				Requester3.verifySMS(handler,phoneNo,mEditYzm.getText().toString().trim(),"6");
			}
		} else {
			/*Requester3.bindPhone(handler, 
					LoginSettingManager.BINDING_TYPE_PHONE, 
					countryNo + phoneNo, 
					mEditYzm.getText().toString().trim(),
					"","","");*/
			verifyProBar.setVisibility(View.VISIBLE);
			Requester3.verifySMS(handler,phoneNo,mEditYzm.getText().toString().trim(),"2");
		}
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
    	setBtnBingingEnabled(false);
//    	checkBindingBtnState();
    }
    
    
    /**
     * 检查注册按钮状态
     */
    private void checkBindingBtnState(){
    	if((("+86".equals(countryNo) && mTvMobileOK.isShown()) || (!"+86".equals(countryNo) && !TextUtils.isEmpty(mEditMobileNo.getText().toString().trim())))&& mTvYzmOK.isShown()){
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
    
    private void clearMobileStata_send() {
    	mTvMobileOK.setVisibility(View.GONE);
    	mTvMobileError.setVisibility(View.GONE);
    	mBtnSendMms.setVisibility(View.VISIBLE);
//    	checkBindingBtnState();
    }
    
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    	super.onActivityResult(requestCode, resultCode, data);
    	
    	if(requestCode == ACTV_REQUEST_CODE_COUNTRY_LIST){
    		
    		if(data!= null){
    			
    			countryName = data.getStringExtra("countryName");
    			countryNo = data.getStringExtra("countryNo");
				String mobileNo = mEditMobileNo.getText().toString().trim();
				if(!TextUtils.isEmpty(mobileNo)){
					if ("+86".equals(countryNo)) {
						if(Prompt.checkPhoneNum(mobileNo)){
							showMobileStataOk();
						}else{
							showMobileStataError();
						}
					} else {
						clearMobileStata_send();
					}
				}else{
					clearMobileStata();
				}
    			
    			mTvCountryNo.setText(countryNo);
    			mTvCountryName.setText(countryName);
    		}
    		
    	}
    	
    }
    
}
