/**
 * 
 */
package com.cmmobi.looklook.common.utils;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.cmmobi.looklook.info.profile.TimeHelper;

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
	public static final String DATE_FORMAT_SHORT="MM-dd HH:mm";
	public static final String DATE_FORMAT_TODAY="HH:mm";
	public static final String DATE_FORMAT_DAY="MM月dd日";

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
	
	public static String getMyCommonShowDate(Date dDate){
		//Date dDate=stringToDate(date,format);
		Date now = new Date();
		long diff=(TimeHelper.getInstance().now() - dDate.getTime())/1000;
		int now_day = now.getDay();
		int cur_day = dDate.getDay();
		
		int now_month = now.getMonth();
		int cur_month = dDate.getMonth();
		
		int now_year = now.getYear();
		int cur_year = dDate.getYear();
		
		if(diff < 0 || (diff>0&&diff<60)){
			return "刚刚";
		}else if(diff>=60&&diff<3600){
			return (diff/60)+"分钟前";
		}else if(diff>=3600&&diff<3600*5){  //5小时内
			return (diff/3600)+"小时前";
		}
		
		if(now_year == cur_year){
			if(now_month == cur_month){
				//同年同月，忽略年和月
				if(now_day==cur_day){
					//今天，忽略年月日
					return dateToString(dDate, DATE_FORMAT_TODAY);
				}else if(now_day==cur_day+1){
					//昨天
					return "昨天 " + dateToString(dDate, DATE_FORMAT_TODAY);
				}else if(now_day==cur_day+2){
					return "前天 " + dateToString(dDate, DATE_FORMAT_TODAY);
				}else{
					return  dateToString(dDate, DATE_FORMAT_SHORT);
				}
			}else{
				//同年，不同月，忽略年
				return  dateToString(dDate, DATE_FORMAT_SHORT);
			}
			
		}else{
			return dateToString(dDate, DATE_FORMAT_NORMAL);
		}

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
	
	/**
	 * 返回格式 00:00:00
	 */
	public static String getFormatTime(double time){
		int iTime=(int)time;
		String format="";
		for(int i=0;i<3;i++){
			if(0==format.length()){
				format=new DecimalFormat("00").format(iTime%60);
			}else{
				format=new DecimalFormat("00").format(iTime%60)+":"+format;
			}
			iTime=iTime/60;
		}
		return format;
	}
	
	/**
	 * 返回格式  3'44''
	 */
	public static String getPlayTime(String playtime){
		if(playtime!=null&&playtime.length()>0){
			try {
				int iTime=Integer.parseInt(playtime);
				String res=new DecimalFormat("00").format(iTime%60)+"''";
				iTime=iTime/60;
				if(iTime>0){
					res=new DecimalFormat("00").format(iTime%60)+"'"+res;
				}
				return res;
			} catch (NumberFormatException e) {
				e.printStackTrace();
				return "";
			}
		}else{
			Log.e(TAG, "playtime is null");
			return "";
		}
	}
	
	public static boolean isNum(String str){
		if(null==str)return false;
		return str.matches("^\\d+$");
	}
}
