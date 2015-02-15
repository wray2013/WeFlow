package cn.zipper.framwork.utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import android.text.format.DateFormat;
import android.text.format.DateUtils;

import cn.zipper.framwork.core.ZLog;


public final class ZDateUtils {
	
	private ZDateUtils() {}
	
	public static String getFormatDate24() {
		return get("-", " ", ":", true);
	}
	
	public static String getFormatDate24(String split1, String split2, String split3) {
		return get(split1, split2, split3, true);
	}
	
	public static String getFormatDate12() {
		return get("-", " ", ":", false);
	}
	
	public static String getFormatDate12(String split1, String split2, String split3) {
		return get(split1, split2, split3, false);
	}
	
	private static String get(String split1, String split2, String split3, boolean is24) {
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append("yyyy");
		stringBuilder.append(split1);
		stringBuilder.append("MM");
		stringBuilder.append(split1);
		stringBuilder.append("dd");
		stringBuilder.append(split2);
		stringBuilder.append(is24 ? "kk" : "hh");
		stringBuilder.append(split3);
		stringBuilder.append("mm");
		stringBuilder.append(split3);
		stringBuilder.append("ss");
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(stringBuilder.toString());
		return simpleDateFormat.format(new Date());
	}
	
	public Date parse(String string) {
		Date date = null;
		try {
			SimpleDateFormat simpleDateFormat = new SimpleDateFormat();
			date = simpleDateFormat.parse(string);
		} catch (Exception e) {
			ZLog.e();
			ZLog.printStackTrace();
		}
		return date;
	}

	public static String getFormatDateByMilli(String m) {
		//"m":"1373463099631"
		if(m == null || m.equals("")) return null;
		long milli = Long.parseLong(m);
		String datetime = "";
		//today
        if(DateUtils.isToday(milli)) {
        	datetime = DateFormat.format("今天  kk:mm", milli).toString(); 
        //yesterday
        } else if(DateUtils.isToday(milli + DateUtils.DAY_IN_MILLIS)) {
        	datetime = DateFormat.format("昨天  kk:mm", milli).toString();   
        } else {
        	datetime = DateFormat.format("yyyy.MM.dd  kk:mm", milli).toString(); 
        }
		return datetime;
	}
	
	public static final int SECONDS_IN_DAY = 60 * 60 * 24;
    public static final long MILLIS_IN_DAY = 1000L * SECONDS_IN_DAY;
    
    public static boolean isSameDayOfMillis(final long ms1, final long ms2) {
        final long interval = ms1 - ms2;
        return interval < MILLIS_IN_DAY
                && interval > -1L * MILLIS_IN_DAY
                && toDay(ms1) == toDay(ms2);
    }

    private static long toDay(long millis) {
        return (millis + TimeZone.getDefault().getOffset(millis)) / MILLIS_IN_DAY;
    }
    
}
