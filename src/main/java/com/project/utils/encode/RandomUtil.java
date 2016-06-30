package com.project.utils.encode;

import java.util.Collections;
import java.util.List;
import java.util.Random;

import com.project.utils.time.VDateTimeUtil;


public class RandomUtil {
	private static Random random = new Random();
	private static final String allChar = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ"; 

	/**
	 * 返回随机ID.
	 */
	public static long randomId() {
		return random.nextLong();
	}

	/**
	 * 返回随机名称, prefix字符串+5位随机数字.
	 */
	public static String randomName(String prefix) {
		return prefix + random.nextInt(10000);
	}

	/**
	 * 从输入list中随机返回一个对象.
	 */
	public static <T> T randomOne(List<T> list) {
		return randomSome(list, 1).get(0);
	}

	/**
	 * 从输入list中随机返回随机个对象.
	 */
	public static <T> List<T> randomSome(List<T> list) {
		return randomSome(list, random.nextInt(list.size()));
	}

	/**
	 * 从输入list中随机返回count个对象.
	 */
	public static <T> List<T> randomSome(List<T> list, int count) {
		Collections.shuffle(list);
		return list.subList(0, count);
	}
	
	/**
	 * 生成25位编码，格式为：yyMMddHHmmssSSS + 10 随机数
	 */
	public static synchronized String getBusNumber() {

		StringBuffer result = new StringBuffer();
		int ID_BYTES = 10;
		// 取时间
		String dateTime = VDateTimeUtil.getCurrentTimeNum();
		result = result.append(dateTime.substring(2));
		// 取10位随机数
		for (int i = 0; i < ID_BYTES; i++) {
			result = result.append(random.nextInt(10));
		}
		return result.toString();
	}

	/**
	 * 生成14位编码，格式为：yyMMdd + 6位随机数
	 */
	public static synchronized String getOrderNumber() {
		StringBuffer result = new StringBuffer();
		int ID_BYTES = 6;
		// 取时间
		String dateTime = VDateTimeUtil.getCurrentTimeNum();
		result = result.append(dateTime.substring(0,8));
		// 取10位随机数
		for (int i = 0; i < ID_BYTES; i++) {
			result = result.append(random.nextInt(10));
		}
		return result.toString();
	}

	/**
	 * 获取随机字符
	 * 
	 * @param select
	 *            类型: 0:a-z 1:A-Z 2:0-9 3:随机
	 * @return
	 */
	public static synchronized Character getRandomChar(int select) {
		int tempval = 0;
		if (select == 0) {
			tempval = (int) ((float) 'a' + random.nextFloat() * (float) ('z' - 'a'));
		} else if (select == 1) {
			tempval = (int) ((float) 'A' + random.nextFloat() * (float) ('Z' - 'A'));
		} else if (select == 2) {
			tempval = (int) ((float) '0' + random.nextFloat() * (float) ('9' - '0'));
		}else{
			tempval = allChar.charAt(random.nextInt(allChar.length()));
		}
		return new Character((char) tempval);
	}

	/**
	 * 获取指定长度随机字符串
	 * 
	 * @param length
	 * @return
	 */
	public static synchronized String getRandomString(int length) {
		StringBuffer buffer = new StringBuffer();
		for (int i = 0; i < length; i++) {
			int randomSelect = (int) (random.nextFloat() * 100) % 3;
			buffer.append(getRandomChar(randomSelect));
		}
		return buffer.toString();
	}
	
	/**
	 * 获取指定最大长度随机数
	 * @param length
	 * @return
	 */
	public static synchronized long getRandomNumber(int maxLength) {
		StringBuffer buffer = new StringBuffer();
		for (int i = 0; i < maxLength; i++) {
			buffer.append(getRandomChar(2));
		}
		return maxLength==0?0:Long.parseLong(buffer.toString());
	}
	
	/**
	 * 获取指定最大长度随机数
	 * @param length
	 * @return
	 */
	public static synchronized long getRandomNumberNotGt30(int maxLength) {
		long returnNum = 0;
		StringBuffer buffer = new StringBuffer();
		for (int i = 0; i < maxLength; i++) {
			buffer.append(getRandomChar(2));
		}
		returnNum = maxLength==0?0:Long.parseLong(buffer.toString());
		if(returnNum>30){
			return getRandomNumberNotGt30(maxLength);
		}
		return returnNum;
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		for(int j=0;j<10;j++){
			System.out.println("2:"+RandomUtil.getRandomNumberNotGt30(1));
			System.out.println("3:"+RandomUtil.getRandomNumberNotGt30(3));
		}
	}
	
}
