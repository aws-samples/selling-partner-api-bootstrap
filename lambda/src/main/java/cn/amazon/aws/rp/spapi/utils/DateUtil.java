package cn.amazon.aws.rp.spapi.utils;

import cn.amazon.aws.rp.spapi.constants.DateConstants;
import org.apache.commons.lang3.StringUtils;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoField;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
import java.time.temporal.TemporalUnit;
import java.util.*;

/**
 * @description:
 * @className: DateUtil
 * @type: JAVA
 * @date: 2020/11/12 19:53
 * @author: zhangkui
 */
public class DateUtil {
	public static final String DATE = "yyyy-MM-dd";
	/**
	 * 例如:2018-12-28 10:00:00
	 */
	public static final String DATE_TIME = "yyyy-MM-dd HH:mm:ss";
	/**
	 * 例如:10:00:00
	 */
	public static final String TIME = "HHmmss";
	/**
	 * 例如:10:00
	 */
	public static final String TIME_WITHOUT_SECOND = "HH:mm";

	/**
	 * 例如:2018-12-28 10:00
	 */
	public static final String DATE_TIME_WITHOUT_SECONDS = "yyyy-MM-dd HH:mm";


	public static final String DATE_FORMAT = "yyyy/M/d H:m:s";


	public static String getFmtDate(String date){
		SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
		SimpleDateFormat sdf1 = new SimpleDateFormat(DATE_TIME);
		try {
			if(StringUtils.isNotBlank(date)){
				Date d = sdf.parse(date);
				date = sdf1.format(d);
			}
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return date;
	}

	/**
	 * 获取年
	 *
	 * @return 年
	 */
	public static int getYear() {
		LocalTime localTime = LocalTime.now();
		return localTime.get(ChronoField.YEAR);
	}

	/**
	 * 获取月份
	 *
	 * @return 月份
	 */
	public static int getMonth() {
		LocalTime localTime = LocalTime.now();
		return localTime.get(ChronoField.MONTH_OF_YEAR);
	}

	/**
	 * 获取某月的第几天
	 *
	 * @return 几号
	 */
	public static int getMonthOfDay() {
		LocalTime localTime = LocalTime.now();
		return localTime.get(ChronoField.DAY_OF_MONTH);
	}

	/**
	 * 格式化日期为字符串
	 *
	 * @param date date
	 * @param pattern 格式
	 * @return 日期字符串
	 */
	public static String format(Date date,String pattern){

		Instant instant = date.toInstant();

		LocalDateTime localDateTime = LocalDateTime.ofInstant(instant, ZoneId.systemDefault());

		return localDateTime.format(DateTimeFormatter.ofPattern(pattern));
	}

	/**
	 * 解析字符串日期为Date
	 *
	 * @param dateStr 日期字符串
	 * @param pattern 格式
	 * @return Date
	 */
	public static Date parse(String dateStr, String pattern) {

		LocalDateTime localDateTime = LocalDateTime.parse(dateStr, DateTimeFormatter.ofPattern(pattern));
		Instant instant = localDateTime.atZone(ZoneId.systemDefault()).toInstant();
		return Date.from(instant);
	}

	/**
	 * 为Date增加分钟,减传负数
	 *
	 * @param date        日期
	 * @param plusMinutes 要增加的分钟数
	 * @return 新的日期
	 */
	public static Date addMinutes(Date date, Long plusMinutes) {
		LocalDateTime dateTime = LocalDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault());
		LocalDateTime newDateTime = dateTime.plusMinutes(plusMinutes);
		return Date.from(newDateTime.atZone(ZoneId.systemDefault()).toInstant());
	}

	/**
	 * 增加时间
	 *
	 * @param date date
	 * @param hour 要增加的小时数
	 * @return new date
	 */
	public static Date addHour(Date date, Long hour) {
		LocalDateTime dateTime = LocalDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault());
		LocalDateTime localDateTime = dateTime.plusHours(hour);
		return Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
	}

	/**
	 * @return 返回当天的起始时间
	 */
	public static Date getStartTime() {

		LocalDateTime now = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0);
		return localDateTime2Date(now);
	}


	/**
	 * @return 返回当天的结束时间
	 */
	public static Date getEndTime() {
		LocalDateTime now = LocalDateTime.now().withHour(23).withMinute(59).withSecond(59).withNano(999);
		return localDateTime2Date(now);
	}

	/**
	 * 减月份
	 *
	 * @param monthsToSubtract 月份
	 * @return Date
	 */
	public static Date minusMonths(long monthsToSubtract){
		LocalDate localDate = LocalDate.now().minusMonths(monthsToSubtract);

		return localDate2Date(localDate);
	}

	/**
	 * LocalDate类型转为Date
	 *
	 * @param localDate LocalDate object
	 * @return Date object
	 */
	public static Date localDate2Date(LocalDate localDate) {

		ZonedDateTime zonedDateTime = localDate.atStartOfDay(ZoneId.systemDefault());

		return Date.from(zonedDateTime.toInstant());
	}

	/**
	 * LocalDateTime类型转为Date
	 *
	 * @param localDateTime LocalDateTime object
	 * @return Date object
	 */
	public static Date localDateTime2Date(LocalDateTime localDateTime) {
		return Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
	}

	/**
	 * 查询当前年的第一天
	 *
	 * @param pattern 格式，默认格式yyyyMMdd
	 * @return 20190101
	 */
	public static String getFirstDayOfCurrentYear(String pattern) {
		LocalDateTime localDateTime = LocalDateTime.now().withMonth(1).withDayOfMonth(1);

		if (StringUtils.isEmpty(pattern)) {
			pattern = "yyyyMMdd";
		}

		return format(localDateTime2Date(localDateTime), pattern);
	}

	/**
	 * 查询前一年最后一个月第一天
	 *
	 * @param pattern 格式，默认格式yyyyMMdd
	 * @return 20190101
	 */
	public static String getLastMonthFirstDayOfPreviousYear(String pattern) {
		LocalDateTime localDateTime = LocalDateTime.now().minusYears(1L).withMonth(12).withDayOfMonth(1);

		if (StringUtils.isEmpty(pattern)) {
			pattern = "yyyyMMdd";
		}

		return format(localDateTime2Date(localDateTime), pattern);
	}

	/**
	 * 查询前一年最后一个月第一天
	 *
	 * @param pattern 格式，默认格式yyyyMMdd
	 * @return 20190101
	 */
	public static String getLastMonthLastDayOfPreviousYear(String pattern) {
		LocalDateTime localDateTime = LocalDateTime.now().minusYears(1L).with(TemporalAdjusters.lastDayOfYear());

		if (StringUtils.isEmpty(pattern)) {
			pattern = "yyyyMMdd";
		}

		return format(localDateTime2Date(localDateTime), pattern);
	}

	/**
	 * 获取当前日期
	 *
	 * @param pattern 格式，默认格式yyyyMMdd
	 * @return 20190101
	 */
	public static String getCurrentDay(String pattern) {
		LocalDateTime localDateTime = LocalDateTime.now();

		if (StringUtils.isEmpty(pattern)) {
			pattern = "yyyyMMdd";
		}

		return format(localDateTime2Date(localDateTime), pattern);
	}

	//Date转换为LocalDateTime
	public static LocalDateTime convertDateToLDT(Date date,ZoneOffset zoneOffset) {
		if(Objects.nonNull(date)){
			if(Objects.nonNull(zoneOffset)){
				return LocalDateTime.ofInstant(date.toInstant(), zoneOffset);
			}
			return LocalDateTime.ofInstant(date.toInstant(),ZoneId.systemDefault());
		}
		return null;
	}

	//LocalDateTime转换为Date
	public static Date convertLDTToDate(LocalDateTime time) {
		return Date.from(time.atZone(ZoneId.systemDefault()).toInstant());
	}


	//获取指定日期的毫秒
	public static Long getMilliByTime(LocalDateTime time) {
		return time.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
	}

	//获取指定日期的秒
	public static Long getSecondsByTime(LocalDateTime time) {
		return time.atZone(ZoneId.systemDefault()).toInstant().getEpochSecond();
	}

	//获取指定时间的指定格式
	public static String formatTime(LocalDateTime time,String pattern,ZoneOffset zoneOffset) {
		if(Objects.nonNull(zoneOffset)){
			return time.atOffset(zoneOffset).format(DateTimeFormatter.ofPattern(pattern));
		}
		return time.format(DateTimeFormatter.ofPattern(pattern));
	}

	//获取当前时间的指定格式
	public static String formatNow(String pattern,ZoneOffset zoneOffset) {
		return formatTime(LocalDateTime.now(), pattern,zoneOffset);
	}

	//日期加上一个数,根据field不同加不同值,field为ChronoUnit.*
	public static LocalDateTime plus(LocalDateTime time, long number, TemporalUnit field) {
		return time.plus(number, field);
	}

	//日期减去一个数,根据field不同减不同值,field参数为ChronoUnit.*
	public static LocalDateTime minu(LocalDateTime time, long number, TemporalUnit field){
		return time.minus(number,field);
	}

	/**
	 * 获取两个日期的差  field参数为ChronoUnit.*
	 * @param startTime
	 * @param endTime
	 * @param field  单位(年月日时分秒)
	 * @return
	 */
	public static long betweenTwoTime(LocalDateTime startTime, LocalDateTime endTime, ChronoUnit field) {
		Period period = Period.between(LocalDate.from(startTime), LocalDate.from(endTime));
		if (field == ChronoUnit.YEARS) return period.getYears();
		if (field == ChronoUnit.MONTHS) return period.getYears() * 12 + period.getMonths();
		return field.between(startTime, endTime);
	}

	//获取一天的开始时间，2017,7,22 00:00
	public static LocalDateTime getDayStart(LocalDateTime time) {
		return time.withHour(0)
				.withMinute(0)
				.withSecond(0)
				.withNano(0);
	}

	//获取一天的结束时间，2017,7,22 23:59:59.999999999
	public static LocalDateTime getDayEnd(LocalDateTime time) {
		return time.withHour(23)
				.withMinute(59)
				.withSecond(59)
				.withNano(999999999);
	}

	/**
	 * local时间转换成UTC时间
	 * @param localTime
	 * @return
	 */
	public static Date localToUTC(String localTime) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date localDate= null;
		try {
			localDate = sdf.parse(localTime);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		long localTimeInMillis=localDate.getTime();
		// long时间转换成Calendar
		Calendar calendar= Calendar.getInstance();
		calendar.setTimeInMillis(localTimeInMillis);
		// 取得时间偏移量
		int zoneOffset = calendar.get(Calendar.ZONE_OFFSET);
		// 取得夏令时差
		int dstOffset = calendar.get(Calendar.DST_OFFSET);
		// 从本地时间里扣除这些差量，即可以取得UTC时间
		calendar.add(Calendar.MILLISECOND, -(zoneOffset + dstOffset));
		// 取得的时间就是UTC标准时间
		Date utcDate=new Date(calendar.getTimeInMillis());
		return utcDate;
	}

	/**
	 * utc时间转成local时间
	 * @param utcTime
	 * @return
	 */
	public static Date utcToLocal(String utcTime){
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
		Date utcDate = null;
		try {
			utcDate = sdf.parse(utcTime);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		sdf.setTimeZone(TimeZone.getDefault());
		Date locatlDate = null;
		String localTime = sdf.format(utcDate.getTime());
		try {
			locatlDate = sdf.parse(localTime);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return locatlDate;
	}

	public static Date getTimeZoneDate(String date){
		//将时间字符串转化为标准时间格式
		Date result = new Date();
		if (StringUtils.isNotBlank(date)) {
			//SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", local);
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
			try {
				result = sdf.parse(date);
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}
		return result;
	}

	/**
	 * 将Date类转换为XMLGregorianCalendar
	 * @param date
	 * @return
	 */
	public static XMLGregorianCalendar dateToXmlDate(Date date){
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		DatatypeFactory dtf = null;
		try {
			dtf = DatatypeFactory.newInstance();
		} catch (DatatypeConfigurationException e) {
		}
		XMLGregorianCalendar dateType = dtf.newXMLGregorianCalendar();
		dateType.setYear(cal.get(Calendar.YEAR));
		//由于Calendar.MONTH取值范围为0~11,需要加1
		dateType.setMonth(cal.get(Calendar.MONTH)+1);
		dateType.setDay(cal.get(Calendar.DAY_OF_MONTH));
		dateType.setHour(cal.get(Calendar.HOUR_OF_DAY));
		dateType.setMinute(cal.get(Calendar.MINUTE));
		dateType.setSecond(cal.get(Calendar.SECOND));
		return dateType;
	}

	/**
	 * 将XMLGregorianCalendar转换为Date
	 * @param cal
	 * @return
	 */
	public static Date xmlDate2Date(XMLGregorianCalendar cal){
		return cal.toGregorianCalendar().getTime();
	}

    /*public static void main(String[] args) throws ParseException, DatatypeConfigurationException {
        String date = "2020/3/31 1:58:59";
        getFmtDate(date);
        //XMLGregorianCalendar xt = DatatypeFactory.newInstance().newXMLGregorianCalendar(2020, 4, 1, 1, 0, 0, 999, 0);
    }*/


	/**
	 * @Author: wulh
	 * @Description:  LocalDateTime转换为XMLGregorianCalendar
	 * @Date: 2020/9/2 15:43
	 * @Param:
	 * @return:
	 **/
	public static XMLGregorianCalendar localDateTimeToXMLGregorianCalendar(LocalDateTime localDateTime){
		GregorianCalendar gcal = GregorianCalendar.from(ZonedDateTime.of(localDateTime,ZoneId.systemDefault()));
		XMLGregorianCalendar time = null;
		try {
			time = DatatypeFactory.newInstance().newXMLGregorianCalendar(gcal);
		} catch (DatatypeConfigurationException e) {
			e.printStackTrace();
		}
		return time;
	}

	/**
	 * @Author: wulh
	 * @Description:  XMLGregorianCalendar转换成LocalDate
	 * @Date: 2020/9/2 15:46
	 * @Param:
	 * @return:
	 **/
	public static LocalDateTime xmlGregorianCalendarToLocalDateTime(XMLGregorianCalendar calendar){
		return calendar.toGregorianCalendar().toZonedDateTime().toLocalDateTime();
	}

	public static LocalDateTime toZone(final LocalDateTime time, final ZoneId fromZone, final ZoneId toZone) {
		final ZonedDateTime zonedtime = time.atZone(fromZone);
		final ZonedDateTime converted = zonedtime.withZoneSameInstant(toZone);
		return converted.toLocalDateTime();
	}

	public static LocalDateTime toZone(final LocalDateTime time, final ZoneId toZone) {
		return DateUtil.toZone(time, ZoneId.systemDefault(), toZone);
	}

	public static LocalDateTime toUtc(final LocalDateTime time, final ZoneId fromZone) {
		return DateUtil.toZone(time, fromZone, ZoneOffset.UTC);
	}

	public static LocalDateTime toUtc(final LocalDateTime time) {
		return DateUtil.toUtc(time, ZoneId.systemDefault());
	}

	public static LocalDateTime getLocalDateTime(String date){
		DateTimeFormatter df = DateTimeFormatter.ofPattern(DATE_TIME);
		return LocalDateTime.parse(date, df);
	}
	public static String getDateFormat(LocalDateTime date){
		DateTimeFormatter df = DateTimeFormatter.ofPattern(DATE_TIME);
		return df.format(date);
	}
}
