package com.cmmobi.railwifi.activity;

import com.cmmobi.railwifi.R;
import com.cmmobi.railwifi.utils.ViewUtils;

import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

public class NewReceiptAddressActivity extends TitleRootActivity {

	private TextView tvAddr;
	private EditText edAddrDetail, edName, edTel, edCode;
	
	private String address = "";
	private String[] addrModify = null;
	private int position = -1;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setTitleText("新增收货地址");
		setRightButtonText("保存");
		
		Intent intent = getIntent();
		if(intent != null) {
			addrModify = intent.getStringArrayExtra("addressmodify");
			position = intent.getIntExtra("addressid", -1);
		}
		
		initView();
		
		if(addrModify != null && position >= 0) {
			setTitleText("修改收货地址");
			tvAddr.setText(addrModify[0]);
			edAddrDetail.setText(addrModify[1]);
			edName.setText(addrModify[2]);
			edTel.setText(addrModify[3]);
			edCode.setText(addrModify[4]);
		} else {
			Intent i = new Intent(this, AddressSelectorActivity.class);
			startActivityForResult(i, UserInfoAcitivity.REQUEST_CODE_ADDRESS);
		}
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
//		if(resultCode != RESULT_OK) return;
		if(resultCode == RESULT_OK) {
			if(UserInfoAcitivity.REQUEST_CODE_ADDRESS == requestCode && data !=null && data.getStringArrayExtra("address")!=null) {
				String[] result = data.getExtras().getStringArray("address");
				if(result != null) {
					String address = "";
					for(int i = 0; i < result.length; i ++) {
						if(!result[i].equals("")){
							address += result[i];
						}
					}
					tvAddr.setText(address);
				} else {
					finish();
				}
			} else {
				finish();
			}
		}
		super.onActivityResult(requestCode, resultCode, data);
	}
	
	private void initView() {
		// TODO Auto-generated method stub
		tvAddr = (TextView) findViewById(R.id.tv_addr);
		edAddrDetail = (EditText) findViewById(R.id.et_addr_detail);
		edName       = (EditText) findViewById(R.id.et_receipt_name);
		edTel        = (EditText) findViewById(R.id.et_receipt_tel);
		edCode       = (EditText) findViewById(R.id.et_code);
		
		findViewById(R.id.rl_addr).setOnClickListener(this);
		
		ViewUtils.setHeight(findViewById(R.id.rl_addr), 122);
		ViewUtils.setHeight(edAddrDetail, 122);
		ViewUtils.setHeight(edName, 122);
		ViewUtils.setHeight(edTel, 122);
		ViewUtils.setHeight(edCode, 122);
		
		ViewUtils.setTextSize(tvAddr, 36);
		ViewUtils.setTextSize((TextView) findViewById(R.id.tv_addr_hint), 28);
		ViewUtils.setTextSize(edAddrDetail, 28);
		ViewUtils.setTextSize(edName, 28);
		ViewUtils.setTextSize(edTel, 28);
		ViewUtils.setTextSize(edCode, 28);
		
		ViewUtils.setMarginTop(findViewById(R.id.tv_addr_hint), 20);
		
		ViewUtils.setMarginLeft(tvAddr, 30);
		ViewUtils.setMarginRight(tvAddr, 30);
		
		ViewUtils.setMarginLeft(edAddrDetail, 30);
		ViewUtils.setMarginRight(edAddrDetail, 30);
		
		ViewUtils.setMarginLeft(edName, 30);
		ViewUtils.setMarginRight(edName, 30);
		
		ViewUtils.setMarginLeft(edTel, 30);
		ViewUtils.setMarginRight(edTel, 30);
		
		ViewUtils.setMarginLeft(edCode, 30);
		ViewUtils.setMarginRight(edCode, 30);
	}
	
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch(v.getId()) {
		case R.id.rl_addr:
			Intent i = new Intent(this, AddressSelectorActivity.class);
			startActivityForResult(i, UserInfoAcitivity.REQUEST_CODE_ADDRESS);
			break;
		case R.id.btn_title_right: //保存
			if(edAddrDetail.getText().equals("")) {
				break;
			}
			if(edName.getText().equals("")) {
				break;
			}
			if(edTel.getText().equals("")) {
				break;
			}
			if(edCode.getText().equals("")) {
				break;
			}
			
			String[] result = new String[]{"", "", "", "", ""};
			result[0] = tvAddr.getText().toString();
			result[1] = edAddrDetail.getText().toString();
			result[2] = edName.getText().toString();
			result[3] = edTel.getText().toString();
			result[4] = edCode.getText().toString();
			giveResultBack(result);
			break;
		}
		super.onClick(v);
	}
	
	private void giveResultBack(String[] res) {
		Intent data=new Intent();
		data.putExtra("receipt", res);
		data.putExtra("position", position);
		setResult(RESULT_OK, data);
		finish();
	}

	@Override
	public boolean handleMessage(Message msg) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public int subContentViewId() {
		// TODO Auto-generated method stub
		return R.layout.activity_new_receipt_address;
	}

}
