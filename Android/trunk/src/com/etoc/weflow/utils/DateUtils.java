/**
 * 
 */
package com.etoc.weflow.utils;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import android.text.TextUtils;
import android.util.Log;

/**
 * @author wuxiang
 *
 * @create 2013-4-10
 */
public class DateUtils {
	private static final String TAG="DateUtils";
	public static final String DATE_FORMAT_NORMAL="yyyy-MM-dd HH:mm";
	public static final String DATE_FORMAT_NORMAL_1="yyyy-MM-dd";
	public static final String DATE_FORMAT_NORMAL_2="yyyy/MM/dd HH:mm";
	public static final String DATE_FORMAT_NORMAL_3="yyyy.MM.dd";
	public static final String DATE_FORMAT_NORMAL_4="yyyy年MM月dd日 HH:mm";
	public static final String DATE_FORMAT_SHORT="MM-dd HH:mm";
	public static final String DATE_FORMAT_SHORT_1="MM月dd日 HH:mm";
	public static final String DATE_FORMAT_TODAY="HH:mm";
	public static final String DATE_FORMAT_DAY="MM月dd日";
	public static final String DATE_FORMAT_DAY_1="MM月dd";
	public static final String DATE_FORMAT_DAY_WEEK="MM月dd日 E";
	public static final String DATE_FORMAT_YEAR_DAY="yyyy年MM月dd日 E";
	public static final String DATE_FORMAT_YEAR_DAY_1="yy年MM月dd日";
	public static Date stringToDate(String date,String format){
		try {
			SimpleDateFormat dateFormat=new SimpleDateFormat(format);
			Date dDate=dateFormat.parse(date);
			return dDate;
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	//根据给定日期返回制定
	public static String dateFormat(String date,String inFormat,String outFormat){
		SimpleDateFormat dateFormat=new SimpleDateFormat(outFormat);
		return dateFormat.format(stringToDate(date,inFormat));
	}
	
	public static String dateToString(Date date,String format){
		SimpleDateFormat dateFormat=new SimpleDateFormat(format);
		return dateFormat.format(date);
	}
	
	public static boolean isToday(String date,String format){
		Date dDate=stringToDate(date,format);
		Date today=new Date();
		Date start=new Date(today.getYear(), today.getMonth(), today.getDate(), 0, 0, 0);
		Date end=new Date(today.getYear(), today.getMonth(), today.getDate(), 23, 59, 59);
		if(dDate.getTime()>=start.getTime()&&dDate.getTime()<=end.getTime())
			return true;
		return false;
	}
	
	public static String getNormlDate(long mill){
		return (new Date(mill)).toLocaleString();
	}
	
	public static String getStringFromMilli(String milli, String format){

		try{
			if (null == format || "".equals(format)) {
				format = "yyyy-MM-dd HH:mm";
			}
			
			if (null != milli && !"".equals(milli)) {
				
				Date date = new Date(Long.parseLong(milli));
				
				SimpleDateFormat sdf = new SimpleDateFormat(format);

				return sdf.format(date);
			}
			
		}catch(Exception e){
			e.printStackTrace();
		}

	
		return "";
	}
	
	public static String getDayString(){
		String format = "yyyyMMdd";
		long milli = System.currentTimeMillis();
		try{
			Date date = new Date(milli);
			SimpleDateFormat sdf = new SimpleDateFormat(format);
			return sdf.format(date);
		}catch(Exception e){
			e.printStackTrace();
		}
	
		return "";
	}
	
	public static String getDayStringFromMilli(String milli){
		String format = "yyyy-M-dd";
		try{
			if (null != milli && !"".equals(milli)) {
				
				Date date = new Date(Long.parseLong(milli));
				
				SimpleDateFormat sdf = new SimpleDateFormat(format);
				return sdf.format(date);
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	
		return "";
	}
	
	public static String getDayStringFromMilli(long milli){
		String format = "yyyy-MM-dd";
		try{
			Date date = new Date(milli);
			SimpleDateFormat sdf = new SimpleDateFormat(format);
			return sdf.format(date);
		}catch(Exception e){
			e.printStackTrace();
		}
	
		return "";
	}
	
	/**
	 * 返回格式00:10, 00:10:10, 10:10:10
	 */
	public static String getFormatTime(int time){
		int iTime= time/1000;
		String format="";
		for(int i=0;i<3;i++){
			if(0==format.length()){
				format=new DecimalFormat("00").format(iTime%60);
			}else{
				format=new DecimalFormat("00").format(iTime%60)+":"+format;
			}
			
			iTime=iTime/60;
			
			if(iTime==0 && i<2){
				format = "00:" + format;
				break;
			}
		}
		return format;
	}
	
	
	
	
	/**
	 * 
	 */
	public static String getStringweekday(String milli){
		if(!TextUtils.isEmpty(milli)){
			String day=getStringFromMilli(milli, "E");
			if(day.contains("一")||day.contains("Mon")){
				return "星期一";
			}else if(day.contains("二")||day.contains("Tue")){
				return "星期二";
			}else if(day.contains("三")||day.contains("Wed")){
				return "星期三";
			}else if(day.contains("四")||day.contains("Thu")){
				return "星期四";
			}else if(day.contains("五")||day.contains("Fri")){
				return "星期五";
			}else if(day.contains("六")||day.contains("Sat")){
				return "星期六";
			}else if(day.contains("日")||day.contains("Sun")){
				return "星期日";
			}else{
				Log.e(TAG, "getStringFromMilli error day="+day);
			}
		}else{
			Log.e(TAG, "milli error");
		}
		return "";
	}
	
	public static boolean isNum(String str){
		if(null==str)return false;
		return str.matches("^\\d+$");
	}
}
