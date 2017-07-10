package com.spider.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;


public class DateUtil {
	private final static String Month_FORMAT = "yyyyMM";

	/**
	 * @description:将时间转为字符串，并且格式为yyyy-MM-dd
	 * @param:
	 * @return: String
	 */
	public static String parseDate(Date date) {
		if (date != null) {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			return sdf.format(date);
		} else {
			return null;
		}
	}
	public static Date getCurrentDate() {
		return new Date();
	}

	/**
	 * @description:将时间转为字符串，并且格式为yyyy-MM-dd HH:mm
	 * @param:
	 * @return: String
	 */
	public static String parseDateTimeToMin(Date date) {
		if (date != null) {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
			return sdf.format(date);
		} else {
			return null;
		}
	}

	/**
	 * @description:将时间转为字符串，并且格式为yyyy-MM-dd HH:mm:ss
	 * @param:
	 * @return: String
	 */
	public static String parseDateTimeToSec(Date date) {
		if (date != null) {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			return sdf.format(date);
		} else {
			return null;
		}
	}

	/**
	 * @description：通过同步的方式给上传的文件重新获取名称。以时间解析得到.用于文件名的前缀名
	 * @param
	 * @return：String
	 * @方法编号：
	 */
	public synchronized static String getTimeForFileName() {
		try {
			Thread.sleep(1L);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		Date date = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmssS");
		return sdf.format(date);

	}

	/**
	 * @description：生成年月（201011）
	 * @author: TanZhengLian
	 * @param
	 * @return：String
	 */
	public static String getCurrYearMonth(int imonth) {
		java.util.Calendar c = new GregorianCalendar();
		c.setTime(new Date());
		if (imonth != 0)
			c.add(Calendar.MONTH, imonth);
		SimpleDateFormat formatter = new SimpleDateFormat(Month_FORMAT);
		return formatter.format((c.getTime()));

	}

	/**
	 * @description：生成年月（201011）
	 * @author: TanZhengLian
	 * @param
	 * @return：String
	 */
	public static String getCurrYearMonth() {
		java.util.Calendar c = new GregorianCalendar();
		c.setTime(new Date());
		SimpleDateFormat formatter = new SimpleDateFormat(Month_FORMAT);
		return formatter.format((c.getTime()));

	}

	/**
	 * description:生成年
	 * @author: TanZhengLian
	 *
	 * @return String
	 */
	public static String getCurrYear() {
		java.util.Calendar c = new GregorianCalendar();
		c.setTime(new Date());
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy");
		return formatter.format((c.getTime()));

	}

	/**
	 * description:生成月
	 * @author: TanZhengLian
	 *
	 * @return String
	 */
	public static String getCurrMoonth() {
		java.util.Calendar c = new GregorianCalendar();
		c.setTime(new Date());
		SimpleDateFormat formatter = new SimpleDateFormat("MM");
		return formatter.format((c.getTime()));

	}

	/**
	 * @description:字符串转为时间 yyyy-MM-dd HH:mm
	 * @author: TanZhengLian
	 * @param:
	 * @return: Date
	 */
	public static Date parseDateTimeToMin(String str) {
		if (str != null && !"".equals(str)) {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
			Date d = null;
			try {
				d = sdf.parse(str);
			} catch (ParseException e) {
				//e.printStackTrace();
			}
			return d;
		} else {
			return null;
		}
	}

	/**
	 * @description:字符串转为时间 yyyy-MM-dd HH:mm:ss
	 * @author: TanZhengLian
	 * @param str
	 * @param format
	 * @return
	 */
	public static Date parseDateTimeToSecond(String str) {
		if (str != null && !"".equals(str)) {
			SimpleDateFormat sdf=null;
			sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			Date d = null;
			try {
				d = sdf.parse(str);
			} catch (ParseException e) {
				//e.printStackTrace();
			}
			return d;
		} else {
			return null;
		}
	}

	/**
	 * @description:字符串转为时间 yyyy-MM-dd HH:mm:ss
	 * @author: TanZhengLian
	 * @param str
	 * @param format
	 * @return
	 */
	public static Date parseDateTimeToSecond(String str,String format) {
		if (str != null && !"".equals(str)) {
			SimpleDateFormat sdf=null;
			sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			if(format!=null){
				sdf=new SimpleDateFormat(format);
			}
			Date d = null;
			try {
				d = sdf.parse(str);
			} catch (ParseException e) {
				e.printStackTrace();
			}
			return d;
		} else {
			return null;
		}
	}

	/**
	 * @description:字符串转为时间  yyyy-MM-dd
	 * @author: TanZhengLian
	 * @param:
	 * @return: Date
	 */
	public static Date parseDate(String str) {
		if (str != null && !"".equals(str)) {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
			Date d = null;
			try {
				d = sdf.parse(str);
			} catch (ParseException e) {
				//e.printStackTrace();
			}
			return d;
		} else {
			return null;
		}
	}

	/**
	 * @description:日期加天数得到新的日期
	 * @author: TanZhengLian
	 * @param:
	 * @return: Date
	 */
	public static Date addDate(Date d, Integer day) {
		if (d != null) {
			Calendar cal = Calendar.getInstance();
			cal.setTime(d);
			cal.set(Calendar.HOUR_OF_DAY, 23);
			cal.set(Calendar.MINUTE, 59);
			cal.set(Calendar.SECOND, 59);
			cal.add(Calendar.DATE, day);
			Date date = cal.getTime();
			return date;
		} else {
			return null;
		}

	}

	/**
	 * @description: 日期加分钟得到新的日期
	 * @author: TanZhengLian
	 * @param:
	 * @return: Date
	 * @方法编号：
	 */
	public static Date addMinutes(Date d, Integer minu) {
		if (d != null) {
			Calendar cal = Calendar.getInstance();
			cal.setTime(d);
			cal.add(Calendar.MINUTE, minu);
			Date date = cal.getTime();
			return date;
		} else {
			return null;
		}
	}

	/**
	 * @description:将时间转为字符串，并且格式为yyyy/MM/dd HH:mm:ss
	 * @author: TanZhengLian
	 * @param:
	 * @return: String
	 */
	public static String parseDateTimeToStr(Date date) {
		if (date != null) {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
			return sdf.format(date);
		} else {
			return null;
		}
	}

	/**
	 * @description:将时间转为字符串，并且格式为dateFormat
	 * @author: TanZhengLian
	 * @param: sdf = new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss");
	 * @return: String
	 */
	public static String parseDateTimeToStrByDateFormat(Date date,
			String dateFormat) {
		SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
		if (date != null) {
			return sdf.format(date);
		} else {
			return sdf.format(new Date());
		}
	}

    /**
     * 计算两个日期之间相差的天数
     * @param smdate 较小的时间
     * @param bdate  较大的时间
     * @return 相差天数
     * @throws ParseException
     */
    public static int daysBetween(Date smdate,Date bdate) throws ParseException{
        SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
        smdate=sdf.parse(sdf.format(smdate));
        bdate=sdf.parse(sdf.format(bdate));
        Calendar cal = Calendar.getInstance();
        cal.setTime(smdate);
        long time1 = cal.getTimeInMillis();
        cal.setTime(bdate);
        long time2 = cal.getTimeInMillis();
        long between_days=(time2-time1)/(1000*3600*24);

       return Integer.parseInt(String.valueOf(between_days));
    }


    /**
     * @description:字符串的日期格式的计算
     * @author: TanZhengLian
     * @param smdate
     * @param bdate
     * @return
     * @throws ParseException
     */
    public static int daysBetween(String smdate,String bdate) throws ParseException{
        SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
        Calendar cal = Calendar.getInstance();
        cal.setTime(sdf.parse(smdate));
        long time1 = cal.getTimeInMillis();
        cal.setTime(sdf.parse(bdate));
        long time2 = cal.getTimeInMillis();
        long between_days=(time2-time1)/(1000*3600*24);

       return Integer.parseInt(String.valueOf(between_days));
    }

	/**
	 * @description:判断时间
	 * @author: TanZhengLian
	 * @param date
	 * @return
	 */
	public static boolean isDate(String date) {
    	String regex="\\d{4}(\\-|\\/|.)\\d{1,2}\\1\\d{1,2}.*";
        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(date);
        return m.matches();
    }

	/**
	 * @description:判断时间
	 * @author: TanZhengLian
	 * @param date
	 * @return
	 */
	public static boolean isCNDate(String date) {
    	String regex="\\d{4}[\\u4e00-\\u9fa5]\\d{2}[\\u4e00-\\u9fa5]\\d{2}[\\u4e00-\\u9fa5].*";
        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(date);
        return m.matches();
    }


	public static Date getDate(String date){
		Date  result=null;
		if(StringUtils.isNotEmpty(date)){
			date=date.replace("年", "-");
			date=date.replace("月", "-");
			date=date.replace("日", " ");
			date=date.replace("时", ":");
			date=date.replace("分", ":");
			date=date.replace("秒", "");
			date=date.replace("/", "-");
			date=date.replace("\\", "-");
			result=parseDateTimeToSecond(date);
			if(result==null){
				result=parseDateTimeToMin(date);
			}
			if(result==null){
				result=parseDate(date);
			}
			if(result==null){
				try {
					SimpleDateFormat f3 = new SimpleDateFormat("yyyy-MMdd");
					result=f3.parse(date);
				} catch (ParseException e) {}
			}
			if(result==null){
				try {
					SimpleDateFormat f3 = new SimpleDateFormat("yy.MM.dd");
					result=f3.parse(date);
				} catch (ParseException e) {}
			}
			if(result == null){
				result=parseDateTimeToSecond(date,"yyyyMMdd");
			}
		}
		return result;
	}
}
