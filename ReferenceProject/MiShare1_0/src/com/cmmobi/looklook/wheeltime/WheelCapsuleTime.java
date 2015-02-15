package com.cmmobi.looklook.wheeltime;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import com.cmmobi.looklook.MainApplication;
import com.cmmobi.looklook.R;

import android.util.Log;
import android.view.View;
import android.widget.TextView;

public class WheelCapsuleTime {
	private final String TAG = "WheelCapsuleTime";
	private View view;
	private WheelViewtime wv_day;
	private WheelViewtime wv_hour;
	private WheelViewtime wv_minute;
	private TextView tvCaptureTime;
	private final int TEXTSIZE = 18;
	private final int MAX_VALUE = 80;
	private int leftEdge = -1 * MAX_VALUE;
	private int rightEdge = MAX_VALUE;
	private Date nextYear = null;
	private Date today = null;
	
	String [] minuteArrays = {"00","15","30","45"};
	
	public WheelCapsuleTime(View view) {
		super();
		this.view = view;
	}
	
	/**
	 * @Description: TODO 弹出日期时间选择器
	 */
	public void initDateTimePicker() {
		today = new Date();
		ArrayList<String> dateList = new ArrayList<String>();
		nextYear = getDateAfterYear(today, 1);
		for (int i = -1 * MAX_VALUE;i <= MAX_VALUE;i++) {
			dateList.add(getDateStr(getDateOffset(today, i)));
		}
		System.out.println("initDateTimePicker" + getDateOffset(nextYear, 365).toString());
		

		tvCaptureTime = (TextView) view.findViewById(R.id.tv_time_capsule);
		
		// 天
		wv_day = (WheelViewtime) view.findViewById(R.id.wvt_day);
		wv_day.setAdapter(new ArrayListWheelAdapter<String>(dateList));
		wv_day.setCurrentItem(MAX_VALUE);
		wv_day.addChangingListener(new OnWheelChangedListenertime() {
			
			@Override
			public void onChanged(WheelViewtime wheel, int oldValue, int newValue) {
				// TODO Auto-generated method stub
				Log.d(TAG,"oldValue = " + oldValue + " newValue = " + newValue + " leftEdge = " + leftEdge);
				Date date = getDateOffset(today, newValue + leftEdge);
				String day = getDateStrNormal(date);
				String hour = wv_hour.getTextItem(wv_hour.getCurrentItem());
				String minute = wv_minute.getTextItem(wv_minute.getCurrentItem());
				tvCaptureTime.setText(day + "  " + hour + ":" + minute);
				int count = wv_day.getAdapter().getItemsCount();
				Date now = new Date();
				if (date.after(nextYear)) {
					wv_day.setCurrentItem(newValue + daysBetween(date, nextYear));
				} else if (count - newValue < 70) {
					for (int i = 1;i <= 70;i++ ) {
						((ArrayListWheelAdapter) wv_day.getAdapter()).appendItem(getDateStr(getDateOffset(today, rightEdge + i)));
					}
					rightEdge += 70;
				} else if (now.after(date)) {
					wv_day.setCurrentItem(newValue + daysBetween(date, now));
				} else if (newValue < 70) {
					for (int i = 1;i <= 70;i++ ) {
						((ArrayListWheelAdapter) wv_day.getAdapter()).insertItem(getDateStr(getDateOffset(nextYear, leftEdge - i)));
					}
					leftEdge -= 70;
					wv_day.setCurrentItem(newValue + 70);
					wv_day.invalidate();
				}
				
				if (isToday(date)) {
					int hours = now.getHours();
					if (wv_hour.getCurrentItem() < hours) {
						wv_hour.setCurrentItem(hours);
					} else if (wv_hour.getCurrentItem() == hours) {
						int minuteIndex = now.getMinutes();
						if (wv_minute.getCurrentItem() < minuteIndex) {
							wv_minute.setCurrentItem(minuteIndex);
						}
					}
				}
			}
		});
		

		// 小时
		wv_hour = (WheelViewtime) view.findViewById(R.id.wvt_hour);
		wv_hour.setAdapter(new NumericWheelAdaptertime(0, 23,"%02d"));
		wv_hour.setCurrentItem(today.getHours());
		wv_hour.setCyclic(true);
		wv_hour.addChangingListener(new OnWheelChangedListenertime() {
			
			@Override
			public void onChanged(WheelViewtime wheel, int oldValue, int newValue) {
				// TODO Auto-generated method stub
				Date selectDate = getDateOffset(today, wv_day.getCurrentItem() + leftEdge);
				if (isToday(selectDate)) {
					Date now = new Date();
					int hour = now.getHours();
					if (newValue < hour) {
						wv_hour.setCurrentItem(hour);
					} else if (newValue == hour) {
						int minuteIndex = now.getMinutes() ;
						if (wv_minute.getCurrentItem() < minuteIndex) {
							wv_minute.setCurrentItem(minuteIndex);
						}
					}
				}
				
				String day = getDateStrNormal(selectDate);
				String hour = wv_hour.getTextItem(wv_hour.getCurrentItem());
				String minute = wv_minute.getTextItem(wv_minute.getCurrentItem());
				tvCaptureTime.setText(day + "  " + hour + ":" + minute);
			}
		});
		
		// 分钟
		wv_minute = (WheelViewtime) view.findViewById(R.id.wvt_minute);
		
		wv_minute.setAdapter(new NumericWheelAdaptertime(0, 59,"%02d"));
		int minuteIndex = today.getMinutes();
//		wv_minute.setAdapter(new ArrayWheelAdapter<String>(minuteArrays));
//		int minuteIndex = (today.getMinutes() / 15 + 1) % 4;
		wv_minute.setCurrentItem(minuteIndex);
		wv_minute.setCyclic(true);
		wv_minute.addChangingListener(new OnWheelChangedListenertime() {
			
			@Override
			public void onChanged(WheelViewtime wheel, int oldValue, int newValue) {
				// TODO Auto-generated method stub
				Date selectDate = getDateOffset(today, wv_day.getCurrentItem() + leftEdge);
				String day = getDateStrNormal(selectDate);
				String hour = wv_hour.getTextItem(wv_hour.getCurrentItem());
				String minute = wv_minute.getTextItem(wv_minute.getCurrentItem());
				tvCaptureTime.setText(day + "  " + hour + ":" + minute);
				if (isToday(selectDate)) {
					Date now = new Date();
					int hours = now.getHours();
					if (wv_hour.getCurrentItem() == hours) {
						int minuteIndex = now.getMinutes() ;
						if (newValue < minuteIndex) {
							wv_minute.setCurrentItem(minuteIndex);
						}
					}
				}
			}
		});
		
		tvCaptureTime.setText(getDateStrNormal(today) + " " + String.format("%02d", today.getHours()) + ":" + String.format("%02d", today.getMinutes()));
		
		// 根据屏幕密度来指定选择器字体的大小
		int textSize = 0;

		textSize = (int) (TEXTSIZE * MainApplication.getAppInstance().getResources().getDisplayMetrics().density);

		wv_day.TEXT_SIZE = textSize;
		wv_hour.TEXT_SIZE = textSize;
		wv_minute.TEXT_SIZE = textSize;
	}
	
	public static boolean isToday(Date date) {
		Date today = new Date();
		if (date.getYear() == today.getYear() && date.getMonth() == today.getMonth() && date.getDate() == today.getDate()) {
			return true;
		}
		return false;
	}
	
	public static int daysBetween(Date smdate,Date bdate) {
		try {
	        SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");  
	        smdate=sdf.parse(sdf.format(smdate));  
	        bdate=sdf.parse(sdf.format(bdate));  
	        Calendar cal = Calendar.getInstance();    
	        cal.setTime(smdate);    
	        long time1 = cal.getTimeInMillis();                 
	        cal.setTime(bdate);    
	        long time2 = cal.getTimeInMillis();         
	        long between_days=(time2-time1)/(1000*3600*24);  
	            
	        return (int) between_days;           
		} catch (Exception e) {
			e.printStackTrace();
		}
		return 0;
    } 
	
	  
	  /**
	   * 得到偏移的时间
	   * @param d
	   * @param day
	   * @return
	   */
	public static Date getDateOffset(Date d,int offset){
	    Calendar now = Calendar.getInstance();
	    now.setTime(d);
	    now.set(Calendar.DATE,now.get(Calendar.DATE)+offset);
	    return now.getTime();
	}
	
	public static Date getDateAfterYear(Date d,int year) {
		Calendar now = Calendar.getInstance();
	    now.setTime(d);
	    now.set(Calendar.YEAR,now.get(Calendar.YEAR)+year);
	    return now.getTime();
	}
	
	public static String [] weekStr = {"日","一","二","三","四","五","六"};
	public static String getDateStr(Date d) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(d);
		return (d.getMonth() + 1) + "月" + d.getDate() + "日" + " 周" + weekStr[d.getDay()];
	}
	
	public static String getDateStrNormal(Date d) {
		return (d.getYear() + 1900)+ "." + String.format("%02d", d.getMonth() + 1) + "." + String.format("%02d", d.getDate());
	}
	
	public String getTimeDescription() {
		return tvCaptureTime.getText().toString();
	}

	public Date getTimeCapsure() {
		Date date = getDateOffset(today, wv_day.getCurrentItem() + leftEdge);
		date.setHours(wv_hour.getCurrentItem());
		date.setMinutes(wv_minute.getCurrentItem());
		date.setSeconds(0);
		return date;
	}
}
