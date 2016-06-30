package com.project.utils;

import java.math.BigDecimal;
import java.math.BigInteger;

public class NumberUtil {
	/**
	 * 对double数据进行取精度.
	 * 
	 * @param value
	 *            double数据.
	 * @param scale
	 *            精度位数(保留的小数位数).
	 * @param roundingMode
	 *            精度取值方式.
	 * @return 精度计算后的数据.
	 */
	public static double round(double value, int scale, int roundingMode) {
		BigDecimal bd = new BigDecimal(value);
		bd = bd.setScale(scale, roundingMode);
		double d = bd.doubleValue();
		bd = null;
		return d;
	}

	public static double round(double value, int scale) {
		BigDecimal bd = new BigDecimal(value);
		bd = bd.setScale(scale, BigDecimal.ROUND_HALF_UP);
		double d = bd.doubleValue();
		bd = null;
		return d;
	}

	public static Double roundD(double value, int scale) {
		BigDecimal bd = new BigDecimal(value);
		bd = bd.setScale(scale, BigDecimal.ROUND_HALF_UP);
		Double d = bd.doubleValue();
		bd = null;
		return d;
	}

	public static Integer parseInt(String value) {
		int result = 0;
		if (null != value) {
			try {
				result = Integer.valueOf(value);
			} catch (Exception e) {
			}
		}
		return result;
	}

	public static Double parseDouble(String value) {
		double result = 0;
		if (null != value) {
			try {
				result = Double.valueOf(value);
			} catch (Exception e) {
			}
		}
		return result;
	}

	public static Integer isNull(BigInteger num) {
		return (num == null)?0:num.intValue();
	}

	/**
	 * 当BigDecimal为null时返回BigDecimal.ZERO
	 * @param sour
	 * @return
	 */
	public static BigDecimal wrapNull(BigDecimal sour) {
		return sour == null ? BigDecimal.ZERO : sour;
	}
}
