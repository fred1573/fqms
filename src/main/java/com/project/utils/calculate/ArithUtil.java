package com.project.utils.calculate;

import java.math.BigDecimal;

/**
 * 提供精确的浮点数运算
 * 
 * @author mowei
 * 
 */
public class ArithUtil {
	/**
	 * 默认除法运算精度
	 */
	private static final int DEF_DIV_SCALE = 10;

	private ArithUtil() {
	}

	/**
	 * 精确的加法运算
	 * 
	 * @param m
	 *            被加数
	 * @param mn
	 *            加数
	 * @return 和
	 */
	public static double add(double m, double... mn) {
		BigDecimal result = new BigDecimal(Double.toString(m));
		for (int i = 0; i < mn.length; i++) {
			BigDecimal b = new BigDecimal(Double.toString(mn[i]));
			result = result.add(b);
		}
		return result.doubleValue();
	}

	/**
	 * 精确的减法运算
	 * 
	 * @param m
	 *            被减数
	 * @param mn
	 *            减数
	 * @return 差
	 */
	public static double sub(double m, double... mn) {
		BigDecimal result = new BigDecimal(Double.toString(m));
		for (int i = 0; i < mn.length; i++) {
			BigDecimal b = new BigDecimal(Double.toString(mn[i]));
			result = result.subtract(b);
		}
		return result.doubleValue();
	}

	/**
	 * 精确的乘法运算
	 * 
	 * @param m
	 *            被乘数
	 * @param mn
	 *            乘数
	 * @return 积
	 */
	public static double mul(double m, double... mn) {
		BigDecimal result = new BigDecimal(Double.toString(m));
		for (int i = 0; i < mn.length; i++) {
			BigDecimal b = new BigDecimal(Double.toString(mn[i]));
			result = result.multiply(b);
		}
		return result.doubleValue();
	}

	/**
	 * 
	 * 提供（相对）精确的除法运算，当发生除不尽的情况时，精确到
	 * 
	 * 小数点以后10位，以后的数字四舍五入。
	 * 
	 * @param v1
	 *            被除数
	 * 
	 * @param v2
	 *            除数
	 * 
	 * @return 两个参数的商
	 * 
	 */
	public static double div(double v1, double v2) {
		return div(v1, v2, DEF_DIV_SCALE);
	}

	/**
	 * （相对）精确的除法运算。当发生除不尽的情况时，由scale参数指定精度，以后的数字四舍五入。
	 * 
	 * @param v1
	 *            被除数
	 * @param v2
	 *            除数
	 * @param scale
	 *            表示表示需要精确到小数点以后几位。
	 * @return 两个参数的商
	 * 
	 */
	public static double div(double v1, double v2, int scale) {
		if (scale < 0) {
			throw new IllegalArgumentException("The scale must be a positive integer or zero");
		}
		BigDecimal b1 = new BigDecimal(Double.toString(v1));
		BigDecimal b2 = new BigDecimal(Double.toString(v2));
		return b1.divide(b2, scale, BigDecimal.ROUND_HALF_UP).doubleValue();

	}

	/**
	 * 精确的小数位四舍五入处理。
	 * 
	 * @param v
	 *            需要四舍五入的数字
	 * @param scale
	 *            小数点后保留几位
	 * @return 四舍五入后的结果
	 */
	public static double round(double v, int scale) {
		if (scale < 0) {
			throw new IllegalArgumentException("The scale must be a positive integer or zero");
		}
		BigDecimal b = new BigDecimal(Double.toString(v));
		BigDecimal one = new BigDecimal("1");
		return b.divide(one, scale, BigDecimal.ROUND_HALF_UP).doubleValue();
	}
	
	public static void main(String[] args) {
//		double a = 200.90d;
//		double b = 2d;
//		System.out.println(div(a,b,1));
	}
}