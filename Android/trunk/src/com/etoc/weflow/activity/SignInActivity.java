package com.etoc.weflow.activity;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.etoc.weflow.R;
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
	    initCalendarView(firstDay, endDate.getTime());
	    
	    tvSignIn = (TextView) findViewById(R.id.tv_sign_in);
	    tvSignInFlag = (TextView) findViewById(R.id.tv_sign_in_flag);
	    
	    tvSignRecord  = (TextView) findViewById(R.id.tv_sign_in_tips);
	    
	    tvSignIn.setOnClickListener(this);
	    
	    ViewUtils.setMarginBottom(tvSignIn, 116);
	    ViewUtils.setSize(tvSignIn, 188, 188);
	    ViewUtils.setMarginBottom(tvSignInFlag, 68);
	    ViewUtils.setMarginTop(tvSignInFlag, 48);
	    ViewUtils.setTextSize(tvSignIn, 34);
	    ViewUtils.setTextSize(tvSignInFlag, 30);
	    ViewUtils.setTextSize(tvSignRecord, 32);
	    
	    
	    
	}
	
	private void initCalendarView(Date minDate,Date maxDate) {
		ArrayList<Date> dateList = new ArrayList<Date>();
		final Calendar todayCal = Calendar.getInstance();
		for (int i = 0;i < 3;i++) {
			todayCal.add(Calendar.DATE, -3);
			dateList.add(todayCal.getTime());
		}
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
			tvSignIn.setText("已签");
			tvSignInFlag.setText("今日已签到");
			calendar.selectDate(new Date());
			break;
		}
		super.onClick(v);
	}
	

}
