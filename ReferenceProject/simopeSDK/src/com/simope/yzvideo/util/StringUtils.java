package com.simope.yzvideo.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class StringUtils {
	public static final String DEFAULT_DATE_PATTERN = "yyyy-MM-dd";
	public static final String DEFAULT_DATETIME_PATTERN = "yyyy-MM-dd hh:mm:ss";
	public final static String EMPTY = "";

	/**
	 * 格式化日期字符串
	 * 
	 * @param date
	 * @param pattern
	 * @return
	 * @throws ParseException 
	 */
	public static Date formatString(String date,String pattern) throws ParseException{
		SimpleDateFormat format=new SimpleDateFormat(pattern);
		return format.parse(date);
	}

	public static String formatDate(Date date, String pattern) {
		SimpleDateFormat format = new SimpleDateFormat(pattern);
		return format.format(date);
	}

	/**
	 * 格式化日期字符串
	 * 
	 * @param date
	 * @return 例如2011-3-24
	 */
	public static String formatDate(Date date) {
		return formatDate(date, DEFAULT_DATE_PATTERN);
	}

	/**
	 * 获取当前时间 格式为yyyy-MM-dd 例如2011-07-08
	 * 
	 * @return
	 */
	public static String getDate() {
		return formatDate(new Date(), DEFAULT_DATE_PATTERN);
	}

	/**
	 * 获取当前时间
	 * 
	 * @return
	 */
	public static String getDateTime() {
		return formatDate(new Date(), DEFAULT_DATETIME_PATTERN);
	}

	/**
	 * 格式化日期时间字符串
	 * 
	 * @param date
	 * @return 例如2011-11-30 16:06:54
	 */
	public static String formatDateTime(Date date) {
		return formatDate(date, DEFAULT_DATETIME_PATTERN);
	}

	public static String join(final ArrayList<String> array, String separator) {
		StringBuffer result = new StringBuffer();
		if (array != null && array.size() > 0) {
			for (String str : array) {
				result.append(str);
				result.append(separator);
			}
			result.delete(result.length() - 1, result.length());
		}
		return result.toString();
	}

	public static boolean isEmpty(String str) {
		return str == null || str.length() == 0;
	}

	 public static String generateTime(long time) {
	        int totalSeconds = (int) (time / 1000);
	        int seconds = totalSeconds % 60;
	        int minutes = (totalSeconds / 60) % 60;
	        int hours = totalSeconds / 3600;
	        return String.format("%02d:%02d:%02d", hours, minutes, seconds);
	    }
	 
	 public static String generateADTime(long time) {
	        int totalSeconds = (int) (time / 1000);
	        int seconds = totalSeconds % 60;
	        int minutes = (totalSeconds / 60) % 60;
	        int hours = totalSeconds / 3600;
	        if(hours!=0){
	        	return String.format("广告剩余%02d小时%02d分%02d秒", hours, minutes, seconds);
	        }
	        if(minutes!=0){
	        	return String.format("广告剩余%02d分%02d秒", minutes, seconds);
	        }
	        if(seconds!=0){
	        	return String.format("广告剩余%02d秒", seconds);
	        }
	        return String.format("广告剩余%02d秒", totalSeconds);
	    }

}
