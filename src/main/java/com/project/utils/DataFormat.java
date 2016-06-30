package com.project.utils;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;

/**
 * 提供数据对象格式化公用函数。
*/
public class DataFormat {
	
	/**
	* 将objValue对象格式化；
	* @param objValue
	* @return String
	*/
	public static String format(Object objValue) {
		return format(objValue, null);
	}
	/**
	* 将objValue对象格式化；
	* @param objValue
	* @param formatOption 格式化格式
	* @return String
	*/
	public static String format(Object objValue, String formatOption) {
		String rtn = "";
		if (objValue != null) {
			if (objValue instanceof Integer) {
				Integer intValue = (Integer) objValue;
				rtn = formatLong(new Long(intValue.toString()), formatOption);
			} else if (objValue instanceof Long) {
				Long lngValue = (Long) objValue;
				rtn = formatLong(lngValue, formatOption);
			} else if (objValue instanceof Boolean) {
				Boolean blValue = (Boolean) objValue;
				rtn = formatBoolean(blValue);
			} else if (objValue instanceof String) {
				String strValue = (String) objValue;
				rtn = formatString(strValue);
			} else if (objValue instanceof Double) {
				Double dblValue = (Double) objValue;
				rtn = formatDouble(dblValue, formatOption);
			} else if (objValue instanceof BigDecimal) {
				BigDecimal bdlValue = (BigDecimal) objValue;
				rtn = formatBigDecimal(bdlValue, formatOption);
			} else if (objValue instanceof Float) {
				Float fltValue = (Float) objValue;
				rtn = formatFloat(fltValue, formatOption);
			} else if (objValue instanceof Timestamp) {
				Timestamp tisValue = (Timestamp) objValue;
				rtn = formatTimestamp(tisValue, formatOption);
			} else {
				rtn = objValue.toString();
			}
		}
		return rtn;
	}
	
	/**
	* 将字符串d格式化成是/否格式 ；
	* @param d
	* @return String
	*/
	public static String formatBoolean(Boolean d) {
		if (d != null)
			if (Boolean.TRUE.equals(d))
				return "是";
			else
				return "否";
		else
			return "";
	}
	
	/**
	* 将字符串d格式化 ；
	* @param d
	* @return String
	*/
	public static String formatString(String d) {
		if (d != null)
			return d;
		else
			return "";
	}
	
	/**
	* 将BigDecimal d格式化成#,##0.00格式 ；
	* @param d
	* @return String
	*/
	public static String formatBigDecimal(BigDecimal d) {
		return formatBigDecimal(d, null);
	}
	/**
	* 将BigDecimal d格式化成num格式
	* @param d
	* @param formatOption 格式化参数
	* @return String
	*/
	public static String formatBigDecimal(BigDecimal d, String formatOption) {
		String strNumber = "";
		if (d != null) {
			if (formatOption == null)
				formatOption = "#,##0.00";
			DecimalFormat df = new DecimalFormat(formatOption);
			strNumber = df.format(d);
		}
		return strNumber;
	}
	
	
	/**
	* 将字符串d格式化成#,##0.00格式 ；
	* @param d
	* @return String
	*/
	public static String formatFloat(Float d) {
		return formatFloat(d, null);
	}
	/**
	* 将字符串d格式化成num格式
	* @param d
	* @param formatOption 格式化参数
	* @return String
	*/
	public static String formatFloat(Float d, String formatOption) {
		String strNumber = "";
		if (d != null) {
			if (formatOption == null)
				formatOption = "#,##0.00";
			DecimalFormat df = new DecimalFormat(formatOption);
			strNumber = df.format(d);
		}
		return strNumber;
	}	
	
	/**
	* 将Double d格式化成#,##0.00格式 ；
	* @param d
	* @return String
	*/
	public static String formatDouble(Double d) {
		return formatDouble(d, null);
	}
	/**
	* 将Double d格式化成num格式
	* @param d
	* @param formatOption 格式化参数
	* @return String
	*/
	public static String formatDouble(Double d, String formatOption) {
		String strNumber = "";
		if (d != null) {
			if (formatOption == null)
				formatOption = "#,##0.00";
			DecimalFormat df = new DecimalFormat(formatOption);
			strNumber = df.format(d);
		}
		return strNumber;
	}
	/**
	* 将Long d格式化成#,##0格式 ；
	* @param d
	* @return String
	*/
	public static String formatLong(Long d) {
		return formatLong(d, null);
	}
	/**
	* 将Long d格式化成#,##0格式 ；
	* @param d
	* @return String
	*/
	public static String formatLong(Long d, String formatOption) {
		if (d != null) {
			String strNumber = null;
			if (formatOption == null)
				formatOption = "###0";
			DecimalFormat df = new DecimalFormat(formatOption);
			strNumber = df.format(d);
			return strNumber;
		} else
			return "";
	}
	/**
	* 将Timestamp dt格式化成yyyy-MM-dd HH:mm:ss格式 ；
	* @param dt
	* @param formatOption 格式化参数
	* @return String
	*/
	public static String formatTimestamp(Timestamp dt, String formatOption) {
		String newDate = "";
		if (dt == null) {
			return newDate;
		} else {
			if (formatOption == null)
				formatOption = "yyyy-MM-dd HH:mm:ss";
			try {
				SimpleDateFormat dateStyle = new SimpleDateFormat(formatOption);
				newDate = dateStyle.format(dt);
			}catch(Exception e){
				e.printStackTrace();
			} 
//			finally {
				return newDate;
//			}
		}
	}
	
	/**
	* 将字符串source中的子字符串<、>用字符串&lt;、&gt;代替 ；
	* @param source
	* @return String
	*/
	public static final String formatHtml(Object source) {
		if (source == null)
			return "";
		String value = format(source);
		char content[] = new char[value.length()];
		value.getChars(0, value.length(), content, 0);
		StringBuffer result = new StringBuffer(content.length + 50);
		for (int i = 0; i < content.length; i++)
			switch (content[i]) {
				case 60 : // '<'
					result.append("&lt;");
					break;

				case 62 : // '>'
					result.append("&gt;");
					break;

				case 38 : // '&'
					result.append("&amp;");
					break;

				case 34 : // '"'
					result.append("&quot;");
					break;

				case 39 : // '\''
					result.append("&#39;");
					break;

				default :
					result.append(content[i]);
					break;
			}
		return result.toString();
	}
	
	/**
	* 将字符串source中的子字符串"用字符串\"代替 ；
	* @param source
	* @return String
	*/
	public static final String formatToVar(Object source) {
		if (source == null)
			return "";
		String value = format(source);
		char content[] = new char[value.length()];
		value.getChars(0, value.length(), content, 0);
		StringBuffer result = new StringBuffer(content.length + 50);
		for (int i = 0; i < content.length; i++)
			switch (content[i]) {
				case '\\' : // '\\\\'
					result.append("\\\\");
					break;
				case '"' : // '"'
					result.append("\\\"");
					break;
				case '\r' : // 换行
					result.append("\\n");
					break;
				case '\n' : // 回车
					result.append("\\n");
					break;
				default :
					result.append(content[i]);
					break;
			}
		return result.toString();
	}
	
	/**
	* 将字符串source中的子字符串strBereplaced用字符串strReplace代替 ；
	* @param source 源字符串
	* @param strBereplaced 要替换的子字符串
	* @param strReplace 目标字符串
	* @return String 返回字符串
	*/
	public static final String replaceAllString(
		String source,
		String strBereplaced,
		String strReplace) {
		//jdk1.4支持
		return source.replaceAll(strBereplaced,strReplace);
	}
	
	/**
	 * 计算百分比
	 * @param a
	 * @param b
	 * @return
	 */
	public static String countPercentage(double a,double b){
		DecimalFormat df = new DecimalFormat();
		df.setMaximumFractionDigits(2);
		df.setMinimumFractionDigits(2);
		return df.format(a*100/b) + "%";
	}
	
	public static void main(String[] args){
		System.out.println(countPercentage(22,Double.valueOf("80")));
	}
}
