package com.ai.sboss.arrangement.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.ai.sboss.arrangement.exception.BizException;
import com.ai.sboss.arrangement.exception.ResponseCode;

/**
 * 高并发日期处理工具类
 * 
 * @author jiamin
 */
public final class DateUtils {
	public final static String FORMAT_DATE = "yyyy-MM-dd";
	public final static String FORMAT_DATETIME = "yyyy-MM-dd HH:mm:ss";

	public final static String FORMAT_DATE_ZH = "yyyy年MM月dd日";
	public final static String FORMAT_DATETIME_ZH = "yyyy年MM月dd日 HH时mm分ss秒";

	private static final ThreadLocal<SimpleDateFormat> threadLocal = new ThreadLocal<SimpleDateFormat>();
	private static final Object object = new Object();

	/**
	 * 禁止实例化
	 */
	private DateUtils() {
		
	}
	
	/**
	 * 获取SimpleDateFormat
	 * @param pattern 日期格式
	 * @return SimpleDateFormat对象
	 */
	private static SimpleDateFormat getDateFormat(String pattern) {
		SimpleDateFormat dateFormat = threadLocal.get();
		if (dateFormat == null) {
			synchronized (object) {
				if (dateFormat == null) {
					dateFormat = new SimpleDateFormat(pattern);
					dateFormat.setLenient(false);
					threadLocal.set(dateFormat);
				}
			}
		}
		dateFormat.applyPattern(pattern);
		return dateFormat;
	}

	/**
	 * 将日期字符串转化为日期,日期字符串为空返回null
	 * @param dateStr 日期字符串
	 * @param pattern 日期格式
	 * @return 格式化后的日期
	 * @throws BizException 日期格式化错误异常
	 */
	public static Date parseDate(String dateStr, String pattern) throws BizException {
		Date formatDate = null;
		if (dateStr != null && !dateStr.trim().equals("")) {
			try {
				formatDate = getDateFormat(pattern).parse(dateStr);
			} catch (ParseException e) {
				throw new BizException("字符串转化成日期异常", ResponseCode._402);
			}
		}
		return formatDate;
	}

	/**
	 * 将日期转化为日期字符串，日期为null返回字符串null
	 * @param date 日期
	 * @param pattern 日期格式
	 * @return 日期字符串
	 * @throws BizException 
	 */
	public static String formatDate(Date date, String pattern) throws BizException {
		String formatDateStr = null;
		if (date != null) {
			formatDateStr = getDateFormat(pattern).format(date);
		}
		return formatDateStr;
	}
	
	/**
	 * 将时间戳转化成,指定格式的日期 date
	 * @param date 日期是时间戳  长整型
	 * @param pattern 日期格式
	 * @return 日期字符串
	 * @throws BizException 
	 */
	public static String formatDate(long date, String pattern) throws BizException {
		return getDateFormat(pattern).format(date);
	}
	
//	public static void main(String[] args) {
//		try {
//			//System.out.println(formatDate(new Date(), FORMAT_DATETIME_ZH));
//			//System.out.println(formatDate(new Date(), "aaaazxczc"));
//			//System.out.println(parseDate("2014年08月19日 18时51分12秒", FORMAT_DATETIME_ZH));
//			//System.out.println(parseDate("2014年08月19日 18时51分12秒", "sadas"));
//			System.out.println(formatDate(-100000000000L, FORMAT_DATETIME_ZH));
//		} catch (BizException e) {
//			e.printStackTrace();
//		}
//	}
}
