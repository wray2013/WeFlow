package com.cmmobi.looklook.wheeltime;

import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import android.view.View;

import com.cmmobi.looklook.MainApplication;
import com.cmmobi.looklook.R;

public class WheelMaintime {

	private View view;
	private WheelViewtime wv_year;
	private WheelViewtime wv_month;
	private WheelViewtime wv_day;
	/*private WheelViewtime wv_hours;
	private WheelViewtime wv_mins;*/
	private static int START_YEAR = 1900, END_YEAR =  Calendar.getInstance().get(Calendar.YEAR);
	private final int TEXTSIZE = 18;

	public View getView() {
		return view;
	}

	public void setView(View view) {
		this.view = view;
	}

	public static int getSTART_YEAR() {
		return START_YEAR;
	}

	public static void setSTART_YEAR(int sTART_YEAR) {
		START_YEAR = sTART_YEAR;
	}

	public static int getEND_YEAR() {
		return END_YEAR;
	}

	public static void setEND_YEAR(int eND_YEAR) {
		END_YEAR = eND_YEAR;
	}

	public WheelMaintime(View view) {
		super();

		this.view = view;
		setView(view);
	}

	/**
	 * @Description: TODO 弹出日期时间选择器
	 */
	public void initDateTimePicker() {
		Calendar calendar = Calendar.getInstance();
		final int year = calendar.get(Calendar.YEAR);
		final int month = calendar.get(Calendar.MONTH);
		final int day = calendar.get(Calendar.DATE);

		// 添加大小月月份并将其转换为list,方便之后的判断
		String[] months_big = { "1", "3", "5", "7", "8", "10", "12" };
		String[] months_little = { "4", "6", "9", "11" };

		final List<String> list_big = Arrays.asList(months_big);
		final List<String> list_little = Arrays.asList(months_little);

		// 年
		wv_year = (WheelViewtime) view.findViewById(R.id.wvt_year);
		wv_year.setAdapter(new NumericWheelAdaptertime(START_YEAR, END_YEAR));// 设置"年"的显示数据
		wv_year.setCyclic(true);// 可循环滚动
		wv_year.setLabel("年");// 添加文字
		wv_year.setCurrentItem(year - START_YEAR - 20);// 初始化时显示的数据

		// 月
		wv_month = (WheelViewtime) view.findViewById(R.id.wvt_month);
		wv_month.setAdapter(new NumericWheelAdaptertime(1, 12));
		wv_month.setCyclic(true);
		wv_month.setLabel("月");
		wv_month.setCurrentItem(month);

		// 日
		wv_day = (WheelViewtime) view.findViewById(R.id.wvt_day);
		wv_day.setCyclic(true);
		// 判断大小月及是否闰年,用来确定"日"的数据
		if (list_big.contains(String.valueOf(month + 1))) {
			wv_day.setAdapter(new NumericWheelAdaptertime(1, 31));
		} else if (list_little.contains(String.valueOf(month + 1))) {
			wv_day.setAdapter(new NumericWheelAdaptertime(1, 30));
		} else {
			// 闰年
			if ((year % 4 == 0 && year % 100 != 0) || year % 400 == 0)
				wv_day.setAdapter(new NumericWheelAdaptertime(1, 29));
			else
				wv_day.setAdapter(new NumericWheelAdaptertime(1, 28));
		}
		wv_day.setLabel("日");
		wv_day.setCurrentItem(day - 1);

		// 添加"年"监听
		OnWheelChangedListenertime wheelListener_year = new OnWheelChangedListenertime() {
			public void onChanged(WheelViewtime wheel, int oldValue,
					int newValue) {
				int year_num = newValue + START_YEAR;
				// 判断大小月及是否闰年,用来确定"日"的数据
				if (year_num == year) {
					if (wv_month.getCurrentItem() + 1 > month) {
						wv_month.setCurrentItem(month);
					}
				}
				if (list_big
						.contains(String.valueOf(wv_month.getCurrentItem() + 1))) {
					wv_day.setAdapter(new NumericWheelAdaptertime(1, 31));
				} else if (list_little.contains(String.valueOf(wv_month
						.getCurrentItem() + 1))) {
					wv_day.setAdapter(new NumericWheelAdaptertime(1, 30));
				} else {
					if ((year_num % 4 == 0 && year_num % 100 != 0)
							|| year_num % 400 == 0)
						wv_day.setAdapter(new NumericWheelAdaptertime(1, 29));
					else
						wv_day.setAdapter(new NumericWheelAdaptertime(1, 28));
				}
				
				wv_day.setCurrentItem(wv_day.getCurrentItem());
				if (year_num == year && wv_month.getCurrentItem() == month && wv_day.getCurrentItem() + 1 > day) {
					wv_day.setCurrentItem(day - 1);
				}
			}
		};
		// 添加"月"监听
		OnWheelChangedListenertime wheelListener_month = new OnWheelChangedListenertime() {
			public void onChanged(WheelViewtime wheel, int oldValue,
					int newValue) {
				int month_num = newValue + 1;
				if (wv_year.getCurrentItem() + START_YEAR == year) {
					if (month_num > month) {
						wv_month.setCurrentItem(month);
					}
				}
				// 判断大小月及是否闰年,用来确定"日"的数据
				if (list_big.contains(String.valueOf(month_num))) {
					wv_day.setAdapter(new NumericWheelAdaptertime(1, 31));
				} else if (list_little.contains(String.valueOf(month_num))) {
					wv_day.setAdapter(new NumericWheelAdaptertime(1, 30));
				} else {
					if (((wv_year.getCurrentItem() + START_YEAR) % 4 == 0 && (wv_year
							.getCurrentItem() + START_YEAR) % 100 != 0)
							|| (wv_year.getCurrentItem() + START_YEAR) % 400 == 0)
						wv_day.setAdapter(new NumericWheelAdaptertime(1, 29));
					else
						wv_day.setAdapter(new NumericWheelAdaptertime(1, 28));
				}
				wv_day.setCurrentItem(wv_day.getCurrentItem());
				
				wv_day.setCurrentItem(wv_day.getCurrentItem());
				if (wv_year.getCurrentItem() + START_YEAR == year && wv_month.getCurrentItem() + 1 == month && wv_day.getCurrentItem() + 1 > day) {
					wv_day.setCurrentItem(day - 1);
				}
			}
		};
		
		OnWheelChangedListenertime wheelListener_day = new OnWheelChangedListenertime() {
			public void onChanged(WheelViewtime wheel, int oldValue,
					int newValue) {
				// 判断大小月及是否闰年,用来确定"日"的数据
				if (wv_year.getCurrentItem() + START_YEAR == year && wv_month.getCurrentItem() == month && wv_day.getCurrentItem() + 1 > day) {
					wv_day.setCurrentItem(day - 1);
				}
			}
		};
		wv_year.addChangingListener(wheelListener_year);
		wv_month.addChangingListener(wheelListener_month);
		wv_day.addChangingListener(wheelListener_day);

		// 根据屏幕密度来指定选择器字体的大小
		int textSize = 0;

		textSize = (int) (TEXTSIZE * MainApplication.getAppInstance().getResources().getDisplayMetrics().density);

		wv_day.TEXT_SIZE = textSize;
		wv_month.TEXT_SIZE = textSize;
		wv_year.TEXT_SIZE = textSize;

	}
	
	/**
	 * @Description: TODO 弹出日期时间选择器
	 */
	public void initDateTimePicker(final int year, final int month, final int day) {

		Calendar calendar = Calendar.getInstance();
		final int curYear = calendar.get(Calendar.YEAR);
		final int curMonth = calendar.get(Calendar.MONTH);
		final int curDay = calendar.get(Calendar.DATE);
		// 添加大小月月份并将其转换为list,方便之后的判断
		String[] months_big = { "1", "3", "5", "7", "8", "10", "12" };
		String[] months_little = { "4", "6", "9", "11" };

		final List<String> list_big = Arrays.asList(months_big);
		final List<String> list_little = Arrays.asList(months_little);

		// 年
		wv_year = (WheelViewtime) view.findViewById(R.id.wvt_year);
		wv_year.setAdapter(new NumericWheelAdaptertime(START_YEAR, END_YEAR));// 设置"年"的显示数据
		wv_year.setCyclic(true);// 可循环滚动
		wv_year.setLabel("年");// 添加文字
		wv_year.setCurrentItem(year - START_YEAR);// 初始化时显示的数据

		// 月
		wv_month = (WheelViewtime) view.findViewById(R.id.wvt_month);
		wv_month.setAdapter(new NumericWheelAdaptertime(1, 12));
		wv_month.setCyclic(true);
		wv_month.setLabel("月");
		wv_month.setCurrentItem(month);

		// 日
		wv_day = (WheelViewtime) view.findViewById(R.id.wvt_day);
		wv_day.setCyclic(true);
		// 判断大小月及是否闰年,用来确定"日"的数据
		if (list_big.contains(String.valueOf(month + 1))) {
			wv_day.setAdapter(new NumericWheelAdaptertime(1, 31));
		} else if (list_little.contains(String.valueOf(month + 1))) {
			wv_day.setAdapter(new NumericWheelAdaptertime(1, 30));
		} else {
			// 闰年
			if ((year % 4 == 0 && year % 100 != 0) || year % 400 == 0)
				wv_day.setAdapter(new NumericWheelAdaptertime(1, 29));
			else
				wv_day.setAdapter(new NumericWheelAdaptertime(1, 28));
		}
		wv_day.setLabel("日");
		wv_day.setCurrentItem(day - 1);

		// 添加"年"监听
		OnWheelChangedListenertime wheelListener_year = new OnWheelChangedListenertime() {
			public void onChanged(WheelViewtime wheel, int oldValue,
					int newValue) {
				int year_num = newValue + START_YEAR;
				// 判断大小月及是否闰年,用来确定"日"的数据
				if (year_num == curYear) {
					if (wv_month.getCurrentItem() + 1 > curMonth) {
						wv_month.setCurrentItem(curMonth);
					}
				}
				if (list_big
						.contains(String.valueOf(wv_month.getCurrentItem() + 1))) {
					wv_day.setAdapter(new NumericWheelAdaptertime(1, 31));
				} else if (list_little.contains(String.valueOf(wv_month
						.getCurrentItem() + 1))) {
					wv_day.setAdapter(new NumericWheelAdaptertime(1, 30));
				} else {
					if ((year_num % 4 == 0 && year_num % 100 != 0)
							|| year_num % 400 == 0)
						wv_day.setAdapter(new NumericWheelAdaptertime(1, 29));
					else
						wv_day.setAdapter(new NumericWheelAdaptertime(1, 28));
				}
				
				wv_day.setCurrentItem(wv_day.getCurrentItem());
				if (year_num == curYear && wv_month.getCurrentItem() == curMonth && wv_day.getCurrentItem() + 1 > curDay) {
					wv_day.setCurrentItem(curDay - 1);
				}
			}
		};
		// 添加"月"监听
		OnWheelChangedListenertime wheelListener_month = new OnWheelChangedListenertime() {
			public void onChanged(WheelViewtime wheel, int oldValue,
					int newValue) {
				int month_num = newValue + 1;
				if (wv_year.getCurrentItem() + START_YEAR == curYear) {
					if (month_num > curMonth) {
						wv_month.setCurrentItem(curMonth);
					}
				}
				// 判断大小月及是否闰年,用来确定"日"的数据
				if (list_big.contains(String.valueOf(month_num))) {
					wv_day.setAdapter(new NumericWheelAdaptertime(1, 31));
				} else if (list_little.contains(String.valueOf(month_num))) {
					wv_day.setAdapter(new NumericWheelAdaptertime(1, 30));
				} else {
					if (((wv_year.getCurrentItem() + START_YEAR) % 4 == 0 && (wv_year
							.getCurrentItem() + START_YEAR) % 100 != 0)
							|| (wv_year.getCurrentItem() + START_YEAR) % 400 == 0)
						wv_day.setAdapter(new NumericWheelAdaptertime(1, 29));
					else
						wv_day.setAdapter(new NumericWheelAdaptertime(1, 28));
				}
				wv_day.setCurrentItem(wv_day.getCurrentItem());
				
				wv_day.setCurrentItem(wv_day.getCurrentItem());
				if (wv_year.getCurrentItem() + START_YEAR == curYear && wv_month.getCurrentItem() == curMonth && wv_day.getCurrentItem() + 1 > curDay) {
					wv_day.setCurrentItem(curDay - 1);
				}
			}
		};
		
		OnWheelChangedListenertime wheelListener_day = new OnWheelChangedListenertime() {
			public void onChanged(WheelViewtime wheel, int oldValue,
					int newValue) {
				// 判断大小月及是否闰年,用来确定"日"的数据
				if (wv_year.getCurrentItem() + START_YEAR == curYear && wv_month.getCurrentItem() == curMonth && wv_day.getCurrentItem() + 1 > curDay) {
					wv_day.setCurrentItem(curDay - 1);
				}
			}
		};
		wv_year.addChangingListener(wheelListener_year);
		wv_month.addChangingListener(wheelListener_month);
		wv_day.addChangingListener(wheelListener_day);

		// 根据屏幕密度来指定选择器字体的大小
		int textSize = 0;

		textSize = (int) (TEXTSIZE * MainApplication.getAppInstance().getResources().getDisplayMetrics().density);

		wv_day.TEXT_SIZE = textSize;
		wv_month.TEXT_SIZE = textSize;
		wv_year.TEXT_SIZE = textSize;

	}

	public String getTime() {
		StringBuffer sb = new StringBuffer();
		sb.append((wv_year.getCurrentItem() + START_YEAR)).append("-")
				.append((wv_month.getCurrentItem() + 1)).append("-")
				.append((wv_day.getCurrentItem() + 1));
		return sb.toString();
	}
	
	public String getYear() {
		return "" + (wv_year.getCurrentItem() + START_YEAR);
	}
}
