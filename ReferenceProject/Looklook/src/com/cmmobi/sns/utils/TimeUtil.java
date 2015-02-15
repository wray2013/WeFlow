package com.cmmobi.sns.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class TimeUtil {
	public static String TIME = "06:00:00--08:00:00";
	public static int SPANTIME = 2;
	public static int SPANDAY = 3;

	public static boolean timeCompare(String time) {

		Date setdate = parseTime(time);
		Date currentdate = new Date();

		Calendar calendar = Calendar.getInstance();
		calendar.setTime(currentdate);
		calendar.set(Calendar.HOUR_OF_DAY, calendar.get(Calendar.HOUR_OF_DAY)
				+ SPANTIME);

		currentdate = calendar.getTime();

		if (setdate.after(currentdate)) {
			return true;
		}
		return false;

	}

	public static boolean timeCompare3Days(String time) {

		Date setdate = parseTime(time);
		Date currentdate = new Date();

		Calendar calendar = Calendar.getInstance();
		calendar.setTime(setdate);
		calendar.set(Calendar.DAY_OF_MONTH, calendar.get(Calendar.DAY_OF_MONTH)
				- SPANDAY);

		setdate = calendar.getTime();

		if (setdate.before(currentdate)) {
			return true;

		}
		return false;

	}

	public static Date parseTime(String time) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date date = null;
		try {
			date = sdf.parse(time);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return date;
	}

	private static String mCurrentTime() {
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String currenttime = df.format(new Date());
		return currenttime;
	}

	public static String getCurrentTime() {
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd ");
		String currenttime = df.format(new Date());
		return currenttime;
	}

}
