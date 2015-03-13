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
import com.squareup.timessquare.CalendarPickerView;
import com.squareup.timessquare.CalendarPickerView.SelectionMode;

public class SignInActivity extends TitleRootActivity {

	private CalendarPickerView calendar;
	private List<Date> selectableDateList = new ArrayList<Date>();
	private Date today = new Date();
	private Date selectedDate = null;
	private HashMap<String, LinePrice> priceMap = new HashMap<String, SignInActivity.LinePrice>();
	private TextView tvSignIn;
	private TextView tvSignInFlag;
	
	class LinePrice{
		String adultPrice;
		String kidPrice;
		public LinePrice(String ap,String kp) {
			adultPrice = ap;
			kidPrice = kp;
		}
	}
	
	@Override
	public boolean handleMessage(Message msg) {
		// TODO Auto-generated method stub
		switch(msg.what) {
		/*case Requester.RESPONSE_TYPE_TRAVEL_LINEPRICE:
			if (msg.obj != null) {
				Date maxDate = new Date();
				GsonResponseObject.travelLinePriceResp travelLinePriceResp = (GsonResponseObject.travelLinePriceResp) msg.obj;
				if ("0".equals(travelLinePriceResp.status)) {
					if (travelLinePriceResp.lineprice != null) {
						for (GsonResponseObject.LinePriceElem elem:travelLinePriceResp.lineprice) {
	//						Long time = Long.parseLong(elem.date);
							SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
							Date date = null;
							try {
								date = sdf.parse(elem.date);
								if (date.after(maxDate)) {
									maxDate = date;
								}
								selectableDateList.add(date);
								calendar.addPriceMap(elem.date, elem.adult_price);
								priceMap.put(elem.date, new LinePrice(elem.adult_price,elem.kid_price));
							} catch (ParseException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							
							Log.d("=AAA=","===== date = " + date);
						}
						Calendar cal = Calendar.getInstance();
						cal.setTime(maxDate);
						cal.add(Calendar.DAY_OF_MONTH, 1);
						Log.d("=AAA=","maxDate = " + maxDate);
	//					selectableDateList.get(selectableDateList.size() - 1);
						initCalendarView(today, cal.getTime());
						calendar.validateAndUpdate();
					}
				} else {
					PromptDialog.Dialog(this, "获取日期失败", "网络错误 ：" + travelLinePriceResp.status, "稍后再试");
				}
			} else {
//				PromptDialog.Dialog(this, "获取日期失败", "当前网络状态不佳", "稍后再试");
			}
			break;*/
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
		setRightButtonText("确定>>");
		getRightButton().setEnabled(false);
		
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
	    
	    tvSignIn.setOnClickListener(this);
	    /*calendar.setOnInvalidDateSelectedListener(new OnInvalidDateSelectedListener() {
			@Override
			public void onInvalidDateSelected(Date date) {
				// TODO Auto-generated method stub
				
			}
		});
	    
	    calendar.setDateSelectableFilter(new DateSelectableFilter() {
			
			@Override
			public boolean isDateSelectable(Date date) {
				// TODO Auto-generated method stub
				boolean ret = CalendarPickerView.containsDate(selectableDateList, date);
//				if (ret) {
//					Log.d("=AAA=","isDateSelectable date = " + date);
//				}
				return ret;
			}
		});
	    
	    calendar.setOnDateSelectedListener(new OnDateSelectedListener() {
			
			@Override
			public void onDateUnselected(Date date) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onDateSelected(Date date) {
				// TODO Auto-generated method stub
				selectedDate = date;
				getRightButton().setEnabled(true);
			}
		});*/
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
