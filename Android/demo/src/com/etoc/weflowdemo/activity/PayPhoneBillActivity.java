package com.etoc.weflowdemo.activity;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.etoc.weflowdemo.MainApplication;
import com.etoc.weflowdemo.R;
import com.etoc.weflowdemo.dialog.PromptDialog;
import com.etoc.weflowdemo.util.DisplayUtil;
import com.etoc.weflowdemo.util.ViewUtils;

public class PayPhoneBillActivity extends TitleRootActivity {

	private TextView [] textViewArray = null;
	private TextView tvPhoneNum = null;
	private TextView tvFlowPay = null;
	private TextView tvAttr = null;
	private static float rate = 100.0f;
	private int[] paymentArray = {10,20,30,50,100,200};
	
	private static final int HANDLER_MSG_QUERY_RESULT = 0x00185027;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		initViews();
	}
	
	private void initViews() {
		setTitleText("充话费");
		setRightButtonText("已购");
		
		tvAttr = (TextView) findViewById(R.id.tv_attribution);
		tvPhoneNum = (TextView) findViewById(R.id.tv_account_phone);
		String phoneNum = getIntent().getStringExtra("phone");
		if (phoneNum != null) {
			tvPhoneNum.setText(phoneNum);
			queryTelAttr(phoneNum);
		}
		
		tvFlowPay = (TextView) findViewById(R.id.tv_flow);
		tvFlowPay.setText("0流量币");
		
		textViewArray = new TextView[6];
		textViewArray[0] = (TextView) findViewById(R.id.tv_pay_1);
		textViewArray[1] = (TextView) findViewById(R.id.tv_pay_2);
		textViewArray[2] = (TextView) findViewById(R.id.tv_pay_3);
		textViewArray[3] = (TextView) findViewById(R.id.tv_pay_4);
		textViewArray[4] = (TextView) findViewById(R.id.tv_pay_5);
		textViewArray[5] = (TextView) findViewById(R.id.tv_pay_6);
		for (int i = 0;i < 6; i++) {
			textViewArray[i].setOnClickListener(this);
			textViewArray[i].setTag(false);
			ViewUtils.setWidth(textViewArray[i], 146);
		}
		
		findViewById(R.id.tv_pay_btn).setOnClickListener(this);
	}
	
	private void queryTelAttr(final String tel) {
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				parseTelLoc(tel.trim());
			}
		}).start();
	}
	
	private void parseTelLoc(String tel) {

		String nameSpace = "http://WebXml.com.cn/";// 命名空间
		String methodName = "getMobileCodeInfo";// 调用的方法名称
		String endPoint = "http://webservice.webxml.com.cn/WebServices/MobileCodeWS.asmx";// EndPoint
		String soapAction = "http://WebXml.com.cn/getMobileCodeInfo"; // SOAP
																		// Action
		SoapObject request = new SoapObject(nameSpace, methodName); // 指定WebService的命名空间和调用的方法名

		request.addProperty("mobileCode", tel);
		request.addProperty("userID", "");
		
		SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
				SoapEnvelope.VER11);
		envelope.bodyOut = request;
		envelope.dotNet = true;

		envelope.setOutputSoapObject(request);

		HttpTransportSE se = new HttpTransportSE(endPoint);

		try {
			se.call(soapAction, envelope);
			Message msg = new Message();
			msg.what = HANDLER_MSG_QUERY_RESULT;
			if (envelope.getResponse() != null) {
				SoapObject object = (SoapObject) envelope.bodyIn;
				String result = object.getProperty(0).toString();
				Log.e("", "result = " + result);
				msg.obj = result;
			} else {
				msg.obj = "归属地查询失败";
			}
			handler.sendMessage(msg);

		} catch (Exception e) {
			System.out.println("代码运行出现异常" + e);
			e.printStackTrace();
		}

	}
	
	/**
	 * 
	 * @param result 18502717626：湖北 武汉 湖北联通GSM卡
	 * @return
	 */
	private String analyzeResult(String result) {
		String[] splits = result.split("\\s+");
		return splits[splits.length - 1];
	}
	
	@Override
	public boolean handleMessage(Message msg) {
		// TODO Auto-generated method stub
		switch(msg.what) {
		case HANDLER_MSG_QUERY_RESULT:
			String result = (String) msg.obj;
			if(result != null) {
				tvAttr.setText(analyzeResult(result));
			}
			break;
		}
		return false;
	}

	@Override
	public int subContentViewId() {
		// TODO Auto-generated method stub
		return R.layout.activity_pay_phone_bill;
	}
	
	private void changeStatus(View view) {
		for (int i = 0;i < 6;i++) {
			TextView tv = textViewArray[i];
			if (tv.getId() == view.getId()) {
				Boolean flag = (Boolean) view.getTag();
				if (flag) {
					tv.setBackgroundResource(R.drawable.bg_bill_item);
				} else {
					tv.setBackgroundResource(R.drawable.bg_bill_item_checked);
				}
				view.setTag(!flag);
				payFlow = paymentArray[i] * rate;
				tvFlowPay.setText((int)payFlow + "流量币");
			} else {
				tv.setBackgroundResource(R.drawable.bg_bill_item);
				tv.setTag(false);
			}
		}
	}
	
	private float payFlow = 0;
	private long lastRespNullTs = 0;
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.tv_pay_1:
		case R.id.tv_pay_2:
		case R.id.tv_pay_3:
		case R.id.tv_pay_4:
		case R.id.tv_pay_5:
		case R.id.tv_pay_6:
			changeStatus(v);
			break;
		case R.id.tv_pay_btn:
			long curRespNullTs = System.currentTimeMillis();
	        if(curRespNullTs - lastRespNullTs > 3000){
	        	Toast.makeText(MainApplication.getAppInstance(), "请求失败", Toast.LENGTH_LONG).show();
		        lastRespNullTs = curRespNullTs;
	        }
			
//			PromptDialog.Alert(PayPhoneBillActivity.class, "请求失败");
//			finish();
		default:
			break;
		}
		super.onClick(v);
	}

}
