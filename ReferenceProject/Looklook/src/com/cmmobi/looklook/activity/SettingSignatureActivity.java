package com.cmmobi.looklook.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.support.v4.content.LocalBroadcastManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import cn.zipper.framwork.core.ZActivity;
import cn.zipper.framwork.core.ZApplication;
import cn.zipper.framwork.core.ZDialog;

import com.cmmobi.looklook.R;
import com.cmmobi.looklook.common.constant.Constant;
import com.cmmobi.looklook.common.gson.GsonResponse2;
import com.cmmobi.looklook.common.gson.Requester2;
import com.cmmobi.looklook.common.utils.CmmobiClickAgentWrapper;
import com.cmmobi.looklook.common.utils.DateUtils;
import com.cmmobi.looklook.common.utils.UmengclickAgentWrapper;
import com.cmmobi.looklook.info.profile.AccountInfo;
import com.cmmobi.looklook.info.profile.ActiveAccount;
import com.cmmobi.looklook.info.profile.PrivateCommonMessage;
import com.cmmobi.looklook.prompt.Prompt;

/**
 * 个性签名
 * 
 * @author youtian
 * 
 */
public class SettingSignatureActivity extends ZActivity implements TextWatcher{

	private EditText et_signature;
	private TextView tv_num;
	private final int Num = 14;
	private String content;
	private ImageView iv_back;
	private ImageView iv_commit;
	private ImageView iv_clear;
	
	private ImageView iv_tiaopi; //各种心情，默认选中这一个
	private ImageView iv_jusang;
	private ImageView iv_xinhuanufang;
	private ImageView iv_shengqi;
	private ImageView iv_chijing;
	private ImageView iv_gaoxing;
	private ImageView iv_yanwu;
	private ImageView iv_ku;
	private AccountInfo accountInfo;
	private String newsignature;
	private String moodid;
	private String signature;
	
	private LocalBroadcastManager lbc;
	private Boolean isFromSetting = false;
	private Boolean isCreate = true;
		
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setting_signature);

		et_signature = (EditText) findViewById(R.id.et_signature);
		tv_num = (TextView) findViewById(R.id.tv_num);
		iv_back = (ImageView) findViewById(R.id.iv_back);
		iv_commit = (ImageView) findViewById(R.id.iv_commit);

		et_signature.addTextChangedListener(this);
		iv_commit.setOnClickListener(this);
		iv_back.setOnClickListener(this);
		
		iv_clear = (ImageView) findViewById(R.id.iv_clear);
		iv_clear.setOnClickListener(this);
	
		iv_tiaopi = (ImageView) findViewById(R.id.iv_tiaopi);
		iv_jusang = (ImageView) findViewById(R.id.iv_jusang);
		iv_xinhuanufang = (ImageView) findViewById(R.id.iv_xinhuanufang);
		iv_shengqi = (ImageView) findViewById(R.id.iv_shengqi);
		iv_chijing = (ImageView) findViewById(R.id.iv_chijing);
		iv_gaoxing = (ImageView) findViewById(R.id.iv_gaoxing);
		iv_yanwu = (ImageView) findViewById(R.id.iv_yanwu);
		iv_ku = (ImageView) findViewById(R.id.iv_ku);
		iv_tiaopi.setOnClickListener(this);
		iv_jusang.setOnClickListener(this);
		iv_xinhuanufang.setOnClickListener(this);
		iv_shengqi.setOnClickListener(this);
		iv_chijing.setOnClickListener(this);
		iv_gaoxing.setOnClickListener(this);
		iv_yanwu.setOnClickListener(this);
		iv_ku.setOnClickListener(this);
		
		lbc = LocalBroadcastManager.getInstance(this);
		
	}
	
	@Override
	public void onResume() {
		super.onResume();
		try{
			if(isCreate){
				String userID = ActiveAccount.getInstance(ZApplication.getInstance()).getUID();
				accountInfo = AccountInfo.getInstance(userID);		
				if(this.getIntent().getExtras()!= null && this.getIntent().getExtras().getBoolean("fromSetting")){
					isFromSetting = true;
					moodid = this.getIntent().getExtras().getString("mood");
					signature = this.getIntent().getExtras().getString("signature");
				}else{
					if (accountInfo != null) {
						signature = accountInfo.signature;
						moodid = accountInfo.mood;
					}
				}
				
				if(signature != null && !signature.equals("")){
					et_signature.setText(signature);
					tv_num.setText((Num - signature.length()) + "");
					et_signature.setSelection(signature.length());
				}
				
				if(moodid != null && DateUtils.isNum(moodid)){
					setIvMood(moodid);
				}else{
					setIvMood("0");
				}
				isCreate = false;
				}
		}catch (Exception e){
			e.printStackTrace();
		}
		
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
		String editNum = et_signature.getText().toString();
		if (editNum.length() <= Num) {
			tv_num.setText("" + (Num - editNum.length()));
		} else {
			et_signature.setText(editNum.substring(0, Num));
			et_signature.setSelection(editNum.substring(0, Num).length());
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

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.iv_back:
			this.finish();
			break;
		case R.id.iv_commit:
			newsignature = et_signature.getText().toString().trim();
			if(isFromSetting){
				Intent ret = new Intent();
				ret.putExtra("newsignature", newsignature);
				ret.putExtra("newmood", moodid);
				this.setResult(RESULT_OK, ret);
				this.finish();
			}else{
				ZDialog.show(R.layout.progressdialog, false, true, this);
				Requester2.changeMood(handler, moodid, newsignature);
			}
			break;
		case R.id.iv_clear:
			et_signature.setText(null);
			tv_num.setText(Num + "");
			break;
		case R.id.iv_tiaopi:
			moodid = getMoodId(R.drawable.xinqing_tiaopi);
			setIvMood(moodid);
			break;
		case R.id.iv_jusang:
			moodid = getMoodId(R.drawable.xinqing_jusang);
			setIvMood(moodid);
			break;
		case R.id.iv_xinhuanufang:
			moodid = getMoodId(R.drawable.xinqing_xinhuanufang);
			setIvMood(moodid);
			break;
		case R.id.iv_shengqi:
			moodid = getMoodId(R.drawable.xinqing_shengqi);
			setIvMood(moodid);
			break;
		case R.id.iv_chijing:
			moodid = getMoodId(R.drawable.xinqing_chijing);
			setIvMood(moodid);
			break;
		case R.id.iv_gaoxing:
			moodid = getMoodId(R.drawable.xinqing_gaoxing);
			setIvMood(moodid);
			break;
		case R.id.iv_yanwu:
			moodid = getMoodId(R.drawable.xinqing_yanwu);
			setIvMood(moodid);
			break;
		case R.id.iv_ku:
			moodid = getMoodId(R.drawable.xinqing_ku);
			setIvMood(moodid);
			break;
		}

	}

	@Override
	public boolean handleMessage(Message msg) {
		// TODO Auto-generated method stub
		switch (msg.what) {
		case Requester2.RESPONSE_TYPE_MOOD: 
			ZDialog.dismiss();
			try {
				GsonResponse2.moodResponse res = (GsonResponse2.moodResponse) msg.obj;
				if(res != null){
					if ("0".equals(res.status)) {
						accountInfo.signature = newsignature;
						accountInfo.mood = moodid;
					    CmmobiClickAgentWrapper.onEvent(this, "my_ mood", "1");
						Intent intent = new Intent(SettingActivity.BROADCAST_PERSONALINFO_CHANGED);
						lbc.sendBroadcast(intent);
						Prompt.Dialog(this, false, "提示", "心情修改成功", new DialogInterface.OnClickListener() {
							
							@Override
							public void onClick(DialogInterface dialog, int which) {
								// TODO Auto-generated method stub
								SettingSignatureActivity.this.finish();
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
	
	private String getMoodId(int rid){
		for(int i=0; i< HomepageMyselfDiaryActivity.moods.length; i++){
			if(HomepageMyselfDiaryActivity.moods[i] == rid){
				return i + "";
			}
		}
		return "0";	
	}

	
	private void setIvMood(final String moodid){
		try {
			SettingSignatureActivity.this.runOnUiThread(new Runnable() {
				
				@Override
				public void run() {
					// TODO Auto-generated method stub
					int mid = Integer.parseInt(moodid);
					switch (HomepageMyselfDiaryActivity.moods[mid]) {
					case R.drawable.xinqing_tiaopi:
						iv_tiaopi.setBackgroundResource(R.drawable.xinqing_xuanzhong);
						iv_jusang.setBackgroundDrawable(null);
						iv_xinhuanufang.setBackgroundDrawable(null);
						iv_shengqi.setBackgroundDrawable(null);
						iv_chijing.setBackgroundDrawable(null);
						iv_gaoxing.setBackgroundDrawable(null);
						iv_yanwu.setBackgroundDrawable(null);
						iv_ku.setBackgroundDrawable(null);
						break;
					case R.drawable.xinqing_jusang:
						iv_tiaopi.setBackgroundDrawable(null);
						iv_jusang.setBackgroundResource(R.drawable.xinqing_xuanzhong);
						iv_xinhuanufang.setBackgroundDrawable(null);
						iv_shengqi.setBackgroundDrawable(null);
						iv_chijing.setBackgroundDrawable(null);
						iv_gaoxing.setBackgroundDrawable(null);
						iv_yanwu.setBackgroundDrawable(null);
						iv_ku.setBackgroundDrawable(null);
						break;
					case R.drawable.xinqing_xinhuanufang:
						iv_tiaopi.setBackgroundDrawable(null);
						iv_jusang.setBackgroundDrawable(null);
						iv_xinhuanufang.setBackgroundResource(R.drawable.xinqing_xuanzhong);
						iv_shengqi.setBackgroundDrawable(null);
						iv_chijing.setBackgroundDrawable(null);
						iv_gaoxing.setBackgroundDrawable(null);
						iv_yanwu.setBackgroundDrawable(null);
						iv_ku.setBackgroundDrawable(null);
						break;
					case R.drawable.xinqing_shengqi:
						iv_tiaopi.setBackgroundDrawable(null);
						iv_jusang.setBackgroundDrawable(null);
						iv_xinhuanufang.setBackgroundDrawable(null);
						iv_shengqi.setBackgroundResource(R.drawable.xinqing_xuanzhong);
						iv_chijing.setBackgroundDrawable(null);
						iv_gaoxing.setBackgroundDrawable(null);
						iv_yanwu.setBackgroundDrawable(null);
						iv_ku.setBackgroundDrawable(null);
						break;
					case R.drawable.xinqing_chijing:
						iv_tiaopi.setBackgroundDrawable(null);
						iv_jusang.setBackgroundDrawable(null);
						iv_xinhuanufang.setBackgroundDrawable(null);
						iv_shengqi.setBackgroundDrawable(null);
						iv_chijing.setBackgroundResource(R.drawable.xinqing_xuanzhong);
						iv_gaoxing.setBackgroundDrawable(null);
						iv_yanwu.setBackgroundDrawable(null);
						iv_ku.setBackgroundDrawable(null);
						break;
					case R.drawable.xinqing_gaoxing:
						iv_tiaopi.setBackgroundDrawable(null);
						iv_jusang.setBackgroundDrawable(null);
						iv_xinhuanufang.setBackgroundDrawable(null);
						iv_shengqi.setBackgroundDrawable(null);
						iv_chijing.setBackgroundDrawable(null);
						iv_gaoxing.setBackgroundResource(R.drawable.xinqing_xuanzhong);
						iv_yanwu.setBackgroundDrawable(null);
						iv_ku.setBackgroundDrawable(null);
						break;
					case R.drawable.xinqing_yanwu:
						iv_tiaopi.setBackgroundDrawable(null);
						iv_jusang.setBackgroundDrawable(null);
						iv_xinhuanufang.setBackgroundDrawable(null);
						iv_shengqi.setBackgroundDrawable(null);
						iv_chijing.setBackgroundDrawable(null);
						iv_gaoxing.setBackgroundDrawable(null);
						iv_yanwu.setBackgroundResource(R.drawable.xinqing_xuanzhong);
						iv_ku.setBackgroundDrawable(null);
						break;
					case R.drawable.xinqing_ku:
						iv_tiaopi.setBackgroundDrawable(null);
						iv_jusang.setBackgroundDrawable(null);
						iv_xinhuanufang.setBackgroundDrawable(null);
						iv_shengqi.setBackgroundDrawable(null);
						iv_chijing.setBackgroundDrawable(null);
						iv_gaoxing.setBackgroundDrawable(null);
						iv_yanwu.setBackgroundDrawable(null);
						iv_ku.setBackgroundResource(R.drawable.xinqing_xuanzhong);
						break;
					default:
						break;
					}
				}
			});
			
		} catch (Exception e) {
			// TODO: handle exception
		}
		
	}
}
