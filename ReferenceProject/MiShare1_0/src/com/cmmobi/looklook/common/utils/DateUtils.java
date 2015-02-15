/**
 * 
 */
package com.cmmobi.looklook.common.utils;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import android.text.TextUtils;
import android.util.Log;

import com.baidu.platform.comapi.map.n;
import com.cmmobi.looklook.info.profile.TimeHelper;
import com.hp.hpl.sparta.xpath.ThisNodeTest;

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
	
	public static String getMyCommonShowDate(String mill){
		try {
			Date date = new Date(Long.parseLong(mill));
			return getMyCommonShowDate(date);
		} catch (Exception e) {
			// TODO: handle exception
		}
		return null;
	}
	
//	public static String getMyCommonShowDate(Date dDate){
//		//Date dDate=stringToDate(date,format);
//		Date now = new Date();
//		long diff=(TimeHelper.getInstance().now() - dDate.getTime())/1000;
//		int now_day = now.getDay();
//		int cur_day = dDate.getDay();
//		
//		int now_month = now.getMonth();
//		int cur_month = dDate.getMonth();
//		
//		int now_year = now.getYear();
//		int cur_year = dDate.getYear();
//		
//		if(diff < 0 || (diff>=0&&diff<60)){
//			return "刚刚";
//		}else if(diff>=60&&diff<3600){
//			return (diff/60)+"分钟前";
//		}else if(diff>=3600&&diff<3600*5){  //5小时内
//			return (diff/3600)+"小时前";
//		}
//		
//		if(now_year == cur_year){
//			if(now_month == cur_month){
//				//同年同月，忽略年和月
//				if(now_day==cur_day){
//					//今天，忽略年月日
//					return dateToString(dDate, DATE_FORMAT_TODAY);
//				}else if(now_day==cur_day+1){
//					//昨天
//					return "昨天 " + dateToString(dDate, DATE_FORMAT_TODAY);
//				}else if(now_day==cur_day+2){
//					return "前天 " + dateToString(dDate, DATE_FORMAT_TODAY);
//				}else{
//					return  dateToString(dDate, DATE_FORMAT_SHORT);
//				}
//			}else{
//				//同年，不同月，忽略年
//				return  dateToString(dDate, DATE_FORMAT_SHORT);
//			}
//			
//		}else{
//			return dateToString(dDate, DATE_FORMAT_NORMAL);
//		}
//
//	}
	
	
	/*
	 * 【最新评论时间】：
		今天:XX:XX
		超过1天不到两天，显示 昨天
		超过昨天，显示星期几
		超过1周显示 月日
		超过1年显示 年月日
	 */
	public static String getMyCommonShowDate(Date dDate){
		//Date dDate=stringToDate(date,format);
		Date now = new Date();		
		Calendar cl = Calendar.getInstance(); 
		cl.setTime(now);
		int now_day = cl.get(Calendar.DAY_OF_YEAR);
		int now_month = cl.get(Calendar.MONTH);
		int now_year = cl.get(Calendar.YEAR);
		cl.setTime(dDate);
		int cur_day = cl.get(Calendar.DAY_OF_YEAR);
		int cur_month = cl.get(Calendar.MONTH);
		int cur_year = cl.get(Calendar.YEAR);

		
		if(now.getTime() - dDate.getTime() < 7*24*60*60*1000 && now_day>cur_day+1){
			return  getStringweekday(Long.toString(dDate.getTime()));
		}
		
		if(now_year == cur_year){
			if(now_month == cur_month){
				//同年同月，忽略年和月
				if(now_day==cur_day){
					//今天，忽略年月日
					return dateToString(dDate, DATE_FORMAT_TODAY);
				}else if(now_day==cur_day+1){
					return "昨天 ";
				}else {
					return dateToString(dDate, DATE_FORMAT_DAY);
				}
			}else{
				//同年，不同月，忽略年
				return  dateToString(dDate, DATE_FORMAT_DAY);
			}
			
		}else{
			return dateToString(dDate, DATE_FORMAT_YEAR_DAY_1);
		}

	}
	
	/*
	 * 【最新评论时间】：
		私语时间显示规则
		今天之内即显示: xx时：xx分
		超过24点即显示:昨天 xx时：xx分
		超过昨天即显示：xx月xx日，xx时：xx分 
		跨年显示：xx年xx月xx日，xx时：xx分

	 */
	public static String getMyCommonListShowDate(Date dDate){
		//Date dDate=stringToDate(date,format);
		Date now = new Date();		
		Calendar cl = Calendar.getInstance(); 
		cl.setTime(now);
		int now_day = cl.get(Calendar.DAY_OF_YEAR);
		int now_month = cl.get(Calendar.MONTH);
		int now_year = cl.get(Calendar.YEAR);
		int now_week = cl.get(Calendar.WEEK_OF_YEAR);
		cl.setTime(dDate);
		int cur_day = cl.get(Calendar.DAY_OF_YEAR);
		int cur_month = cl.get(Calendar.MONTH);
		int cur_year = cl.get(Calendar.YEAR);
		int cur_week = cl.get(Calendar.WEEK_OF_YEAR); 

		
		if(now_year == cur_year){
			if(now_month == cur_month){
				//同年同月，忽略年和月
				if(now_day==cur_day){
					//今天，忽略年月日
					return dateToString(dDate, DATE_FORMAT_TODAY);
				}else if(now_day==cur_day+1){
					return "昨天 " + dateToString(dDate, DATE_FORMAT_TODAY);
				}else {
					return dateToString(dDate, DATE_FORMAT_DAY) + "," + dateToString(dDate, DATE_FORMAT_TODAY);
				}
			}else{
				//同年，不同月，忽略年
				return  dateToString(dDate, DATE_FORMAT_DAY) + "," + dateToString(dDate, DATE_FORMAT_TODAY);
			}
			
		}else{
			return dateToString(dDate, DATE_FORMAT_YEAR_DAY_1) + "," + dateToString(dDate, DATE_FORMAT_TODAY);
		}

	}
	
	
	/*
	 * 	
		1小时之内的都显示XX分钟前
		超过1小时都显示2小时前
		超过1天显示昨天;超过2天显示前天
		超过多天(3天/3天以上)显示具体:XX月 XX日
		超过一年显示XX年XX月XX日

	 */
	public static String getDetailShowDate(Date dDate){
		//Date dDate=stringToDate(date,format);
		Date now = new Date();
		long diff=(TimeHelper.getInstance().now() - dDate.getTime())/1000;
		Calendar cl = Calendar.getInstance(); 
		cl.setTime(now);
		int now_day = cl.get(Calendar.DAY_OF_YEAR);
		int now_month = cl.get(Calendar.MONTH);
		int now_year = cl.get(Calendar.YEAR);
		cl.setTime(dDate);
		int cur_day = cl.get(Calendar.DAY_OF_YEAR);
		int cur_month = cl.get(Calendar.MONTH);
		int cur_year = cl.get(Calendar.YEAR);
		
		if(diff<60){
			return "1分钟前";
		}else if(diff>=60&&diff<3600){
			return (diff/60)+"分钟前";
		}else if(diff>=3600&&diff<3600*2){  //5小时内
			return "2小时前";
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
					return dateToString(dDate, DATE_FORMAT_DAY);
				}
			}else{
				return dateToString(dDate, DATE_FORMAT_DAY);
			}
			
		}else{
			return dateToString(dDate, DATE_FORMAT_YEAR_DAY_1);
		}
		
	}
	
	public static String getMyShareShowDate(Date dDate){
		//Date dDate=stringToDate(date,format);
		Date now = new Date();
		int now_year = now.getYear();
		int cur_year = dDate.getYear();
		
		if(now_year == cur_year){
			return dateToString(dDate, DATE_FORMAT_SHORT_1);
		}else{
			return dateToString(dDate, DATE_FORMAT_NORMAL_4);
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
	 * 返回格式 00:00
	 */
	public static String getFormatTime0000(String time){
		if(time == null) return null;
		String format="";
		try {
			int iTime=Integer.parseInt(time);
			for(int i=0;i<2;i++){
				if(0==format.length()){
					format=new DecimalFormat("00").format(iTime%60);
				}else{
					format=new DecimalFormat("00").format(iTime%60)+":"+format;
				}
				iTime=iTime/60;
			}
		} catch (Exception e) {
			// TODO: handle exception
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
	
	
	/*
	 * 【时光胶囊倒计时】：
		超过1小时的倒计时展示形式为 xx天：xx小时：xx分
		一小时以内的倒计时展示形式为 xx分：xx秒
		时间到期后，距离可开启：后面变为“已可开启”


	 */
	public static String getCountdown(String capsule_time){
		if(!isNum(capsule_time)) return null;
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); 
		java.util.Date now = new Date(TimeHelper.getInstance().now());
		java.util.Date date= new Date(Long.parseLong(capsule_time)); 
		long l= date.getTime() - now.getTime(); 
		if(l<=0){
			return "已可开启";
		}
		long day=l/(24*60*60*1000); 
		long hour=(l/(60*60*1000)-day*24); 
		long min=((l/(60*1000))-day*24*60-hour*60); 
		//long s=(l/1000-day*24*60*60-hour*60*60-min*60); 
		//System.out.println(""+day+"天"+hour+"小时"+min+"分"+s+"秒"); 
		String result = "";
		if(day >0){
			result = day + "天";
		}
		if(hour>0 || day>0){
			result = result + hour + "小时";
		}
		
		if(min>0 ||hour>0 || day>0){
			result = result + min + "分";
		}
			
		if(min==0 && hour ==0 && day ==0){
			return "1分";
		}else{
			return result;
		}
	}
}
