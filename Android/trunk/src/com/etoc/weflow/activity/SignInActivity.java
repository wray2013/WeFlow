package com.etoc.weflow.activity;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.etoc.weflow.R;
import com.etoc.weflow.WeFlowApplication;
import com.etoc.weflow.dao.AccountInfo;
import com.etoc.weflow.dialog.PromptDialog;
import com.etoc.weflow.net.GsonResponseObject.SignInListResp;
import com.etoc.weflow.net.GsonResponseObject.SignInResp;
import com.etoc.weflow.net.Requester;
import com.etoc.weflow.utils.NumberUtils;
import com.etoc.weflow.utils.ViewUtils;
import com.squareup.timessquare.CalendarPickerView;
import com.squareup.timessquare.CalendarPickerView.SelectionMode;

public class SignInActivity extends TitleRootActivity {

	private CalendarPickerView calendar;
	private List<Date> selectableDateList = new ArrayList<Date>();
	private Date today = new Date();
	private Date selectedDate = null;
	private TextView tvSignIn;
	private TextView tvSignInFlag;
	private TextView tvSignRecord;
	
	
	@Override
	public boolean handleMessage(Message msg) {
		// TODO Auto-generated method stub
		switch(msg.what) {
		case Requester.RESPONSE_TYPE_SIGN_IN_LIST:
			if (msg.obj != null) {
				SignInListResp resp = (SignInListResp) msg.obj;
				if (Requester.isSuccessed(resp.status)) {
					if (resp.signinlist != null) {
						String [] dates = resp.signinlist.split(",");
						if (dates.length > 0) {
							tvSignRecord.setVisibility(View.VISIBLE);
							tvSignRecord.setText("本月签到" + dates.length + "次；获得" + NumberUtils.convert2IntStr(resp.monthcoins) + "流量币");
							
							ArrayList<Date> dateList = new ArrayList<Date>();
							for (String dateStr:dates) {
					        	Long dateLong = Long.parseLong(dateStr);
					        	 Date date = new Date(dateLong); 
					        	 dateList.add(date);
					        }
							
							final Calendar todayCal = Calendar.getInstance();
							today = todayCal.getTime();
							todayCal.set(Calendar.DAY_OF_MONTH,1);//设置为1号,当前日期既为本月第一天 
					        Date firstDay = todayCal.getTime();
							Log.d("=AAA=","firstDate = " + today.getTime());
						    
						    final Calendar endDate = Calendar.getInstance();
						    endDate.set(Calendar.DAY_OF_MONTH, endDate.getMaximum(Calendar.DAY_OF_MONTH));
						    initCalendarView(firstDay, endDate.getTime(),dateList);
						}
					}
				}
			}
			break;
		case Requester.RESPONSE_TYPE_SIGN_IN:
			if (msg.obj != null) {
				SignInResp signResp = (SignInResp) msg.obj;
				if (Requester.isSuccessed(signResp.status)) {
					PromptDialog.Alert("签到成功");
					tvSignInFlag.setText("今日已签到");
					tvSignIn.setText("已签");
					tvSignIn.setEnabled(false);
					
					if (signResp.signinlist != null) {
						String [] dates = signResp.signinlist.split(",");
						if (dates.length > 0) {
							tvSignRecord.setVisibility(View.VISIBLE);
							tvSignRecord.setText("本月签到" + dates.length + "次；获得" + NumberUtils.convert2IntStr(signResp.monthcoins) + "流量币");
						}
					}
					
				}
			}
			break;
		}
		return false;
	}

	@Override
	public int subContentViewId() {
		// TODO Auto-generated method stub
		return R.layout.activity_date_picker;
	}
	
	@Override
	protected int graviteType() {
		// TODO Auto-generated method stub
		return GRAVITE_LEFT;
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		setTitleText("签到");
		
		hideRightButton();
		initViews();
		
		AccountInfo accountInfo = WeFlowApplication.getAppInstance().getAccountInfo();
		if (accountInfo != null) {
			Requester.getSignInList(true, handler, accountInfo.getUserid());
		}
	}
	
	private void initViews() {
		final Calendar todayCal = Calendar.getInstance();
		today = todayCal.getTime();
		todayCal.set(Calendar.DAY_OF_MONTH,1);//设置为1号,当前日期既为本月第一天 
        Date firstDay = todayCal.getTime();
		Log.d("=AAA=","firstDate = " + today.getTime());
	    
	    final Calendar endDate = Calendar.getInstance();
	    endDate.set(Calendar.DAY_OF_MONTH, endDate.getMaximum(Calendar.DAY_OF_MONTH));
	    
	    calendar = (CalendarPickerView) findViewById(R.id.calendar_view);
	    initCalendarView(firstDay, endDate.getTime(),new ArrayList<Date>());
	    
	    tvSignIn = (TextView) findViewById(R.id.tv_sign_in);
	    tvSignInFlag = (TextView) findViewById(R.id.tv_sign_in_flag);
	    
	    tvSignRecord  = (TextView) findViewById(R.id.tv_sign_in_tips);
	    
	    tvSignIn.setOnClickListener(this);
	    
	    ViewUtils.setMarginBottom(tvSignIn, 116);
	    ViewUtils.setSize(tvSignIn, 188, 188);
	    ViewUtils.setMarginBottom(tvSignInFlag, 64);
	    ViewUtils.setMarginTop(tvSignInFlag, 48);
	    ViewUtils.setTextSize(tvSignIn, 34);
	    ViewUtils.setTextSize(tvSignInFlag, 30);
	    ViewUtils.setTextSize(tvSignRecord, 32);
	    
	    tvSignRecord.setVisibility(View.GONE);
	    
	}
	
	private void initCalendarView(Date minDate,Date maxDate,List<Date> dateList) {
		calendar.init(minDate, maxDate) //
		.displayOnly()
		.setShortWeekdays(new String[]{"","周日","周一","周二","周三","周四","周五","周六"})
        .inMode(SelectionMode.MULTIPLE)
        .withSelectedDates(dateList);
	}
	
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch(v.getId()) {
		case R.id.btn_title_right:
			break;
		case R.id.tv_sign_in:
			AccountInfo accountInfo = WeFlowApplication.getAppInstance().getAccountInfo();
			if (accountInfo != null) {
				Requester.signIn(true, handler, accountInfo.getUserid());
			}
			break;
		}
		super.onClick(v);
	}
	

}
