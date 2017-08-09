package com.ypx.jiehunle.ypx_bezierqqrefreshdemo.util;

import android.annotation.SuppressLint;
import android.text.TextUtils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * 日期工具类
 *
 * @author yangpeixing
 *
 */
@SuppressLint("SimpleDateFormat")
public class DateUtils {

	public static final String MM_DD_HH_MM = "MM-dd HH:mm";

	/**
	 * 获取当前时间
	 *
	 * @return
	 */
	public static String getDate() {
		Date date = new Date();// 获取当前日期对象
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");// 设置格式

		return format.format(date);
	}

	public static String getDate(String formatStr) {
		Date date = new Date();// 获取当前日期对象
		SimpleDateFormat format = new SimpleDateFormat(formatStr);// 设置格式
		return format.format(date);
	}

	public static String getDate(String formatStr, long times) {
		Date date = new Date(times);// 获取当前日期对象
		SimpleDateFormat format = new SimpleDateFormat(formatStr);// 设置格式
		return format.format(date);
	}

	/**
	 * @param t
	 * @return
	 */
	public static String getNormalTime(String t) {
		if (TextUtils.isEmpty(t))
			return "";
		Long time = Long.parseLong(t);
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		return format.format(new Date(time));
	}

	@SuppressLint("SimpleDateFormat")
	public static String getYMTime(long t) {
		SimpleDateFormat format = new SimpleDateFormat("yyyyMM");
		return format.format(new Date(t));
	}

	/**
	 * 得到本月的第一天
	 *
	 * @param detailYM
	 */
	@SuppressLint("SimpleDateFormat")
	public static String getMonthFirstDay(Calendar calendar) {

		calendar.set(Calendar.DAY_OF_MONTH,
				calendar.getActualMinimum(Calendar.DAY_OF_MONTH));
		return String.valueOf(calendar.get(Calendar.DAY_OF_MONTH));
	}

	/**
	 * 得到本月的最后一天
	 *
	 * @param calendar
	 */
	@SuppressLint("SimpleDateFormat")
	public static String getMonthLastDay(Calendar calendar) {

		calendar.set(Calendar.DAY_OF_MONTH,
				calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
		return String.valueOf(calendar.get(Calendar.DAY_OF_MONTH));
	}

	@SuppressLint("SimpleDateFormat")
	public static String getDateStr(String dateStr) {
		if (TextUtils.isEmpty(dateStr)) {
			return "";
		}
		if (dateStr.length() >= 8) {
			dateStr = dateStr.substring(0, 8);
			String yearStr = dateStr.substring(0, 4);
			String monthStr = dateStr.substring(4, 6);
			String dayStr = dateStr.substring(6, 8);
			String returnStr = new StringBuilder(yearStr).append("-")
					.append(monthStr).append("-").append(dayStr).toString();
			return returnStr;
		} else {
			return "";
		}
	}
}
