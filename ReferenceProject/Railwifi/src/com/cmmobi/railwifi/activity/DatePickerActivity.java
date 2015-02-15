package com.cmmobi.railwifi.activity;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.view.View;

import com.cmmobi.railwifi.R;
import com.cmmobi.railwifi.dialog.PromptDialog;
import com.cmmobi.railwifi.network.GsonResponseObject;
import com.cmmobi.railwifi.network.Requester;
import com.cmmobi.railwifi.utils.ConStant;
import com.cmmobi.railwifi.utils.DateUtils;
import com.squareup.timessquare.CalendarPickerView;
import com.squareup.timessquare.CalendarPickerView.DateSelectableFilter;
import com.squareup.timessquare.CalendarPickerView.OnDateSelectedListener;
import com.squareup.timessquare.CalendarPickerView.OnInvalidDateSelectedListener;
import com.squareup.timessquare.CalendarPickerView.SelectionMode;

public class DatePickerActivity extends TitleRootActivity {

	private CalendarPickerView calendar;
	private List<Date> selectableDateList = new ArrayList<Date>();
	private Date today = new Date();
	private Date selectedDate = null;
	private HashMap<String, LinePrice> priceMap = new HashMap<String, DatePickerActivity.LinePrice>();
	
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
		case Requester.RESPONSE_TYPE_TRAVEL_LINEPRICE:
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
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		setTitleText("选择日期");
		setRightButtonText("确定>>");
		getRightButton().setEnabled(false);
		
		initViews();

		String lineId = getIntent().getStringExtra(ConStant.INTENT_LINE_ID);
		Requester.requestTravelLinePrice(handler,lineId);
		
	}
	
	private void initViews() {
		final Calendar todayCal = Calendar.getInstance();
		today = todayCal.getTime();
		Log.d("=AAA=","firstDate = " + today.getTime());
	    
	    final Calendar endDate = Calendar.getInstance();
	    endDate.set(Calendar.DAY_OF_MONTH, endDate.getMaximum(Calendar.DAY_OF_MONTH));
	    
	    calendar = (CalendarPickerView) findViewById(R.id.calendar_view);
//	    initCalendarView(today, endDate.getTime());
	    calendar.setOnInvalidDateSelectedListener(new OnInvalidDateSelectedListener() {
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
		});
	}
	
	private void initCalendarView(Date minDate,Date maxDate) {
		calendar.init(minDate, maxDate) //
        .inMode(SelectionMode.SINGLE);
	}
	
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch(v.getId()) {
		case R.id.btn_title_right:
			Intent intent = new Intent();
			String dateStr = DateUtils.getDayStringFromMilli(selectedDate.getTime());
			LinePrice linePrice = priceMap.get(dateStr);
			intent.putExtra(RailTravelOrderInfoActivity.INTENT_DATE_PICKER_RESULT, "" + dateStr);
			intent.putExtra(RailTravelOrderInfoActivity.INTENT_DATE_ADULT_PRICE, linePrice.adultPrice);
			intent.putExtra(RailTravelOrderInfoActivity.INTENT_DATE_KID_PRICE, linePrice.kidPrice);
			setResult(RESULT_OK, intent);
			finish();
			break;
		}
		super.onClick(v);
	}
	

}
