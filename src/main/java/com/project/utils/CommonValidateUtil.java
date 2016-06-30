package com.project.utils;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.project.common.Constants;
import com.project.exception.ServiceException;

/**
 * 通用校验方式
 * 
 * @author mowei
 * @version 
 */
public class CommonValidateUtil {

	private static Map<String, Map<String,String>> regexMap = new HashMap<String, Map<String, String>>();

	/** 中文 */
	public static final String CHINESE = "chinese";
	/** 数字 */
	public static final String NUMBER = "num";
	/** 整数 */
	public static final String INTEGE = "intege";
	/** 正整数 */
	public static final String INTEGE1 = "intege1";
	/** 负整数 */
	public static final String INTEGE2 = "intege2";
	/** 日期10位YYYY-MM-DD */
	public static final String DATE_YYYYMMDD = "yyyymmdd";
	/** email */
	public static final String EMAIL = "email";

	/**
	 * 初始化数据
	 */
	static {
		// 中文
		HashMap<String, String> chineseMap = new HashMap<String, String>();
		chineseMap.put("key", "^[\\u4E00-\\u9FA5\\uF900-\\uFA2D]+$");
		chineseMap.put("value", "请输入中文！");
		regexMap.put(CHINESE, chineseMap);
		
		// 数字
		HashMap<String, String> numMap = new HashMap<String, String>();
		numMap.put("key", "^([+-]?)\\d*\\.?\\d+$");
		numMap.put("value", "请输入数字！");
		regexMap.put(NUMBER, numMap);

		// 整数
		HashMap<String, String> integeMap = new HashMap<String, String>();
		integeMap.put("key", "^-?[1-9]\\d*$");
		integeMap.put("value", "请输入整数！");
		regexMap.put("intege", integeMap);
		
		// 正整数
		HashMap<String, String> intege1Map = new HashMap<String, String>();
		intege1Map.put("key", "^[1-9]\\d*$");
		intege1Map.put("value", "请输入正整数！");
		regexMap.put(INTEGE1, intege1Map);

		// 负整数
		HashMap<String, String> intege2Map = new HashMap<String, String>();
		intege2Map.put("key", "^-[1-9]\\d*$");
		intege2Map.put("value", "请输入负整数！");
		regexMap.put(INTEGE2, intege2Map);
		
		// 日期10位YYYY-MM-DD
		HashMap<String, String> date10Map = new HashMap<String, String>();
		date10Map.put("key", "^((((19|20)\\d{2})-(0?[13-9]|1[012])-(0?[1-9]|[12]\\d|30))|(((19|20)\\d{2})-(0?[13578]|1[02])-31)|(((19|20)\\d{2})-0?2-(0?[1-9]|1\\d|2[0-8]))|((((19|20)([13579][26]|[2468][048]|0[48]))|(2000))-0?2-29))$");
		date10Map.put("value", "请输入正确的日期格式(YYYY-MM-DD)！");
		regexMap.put(DATE_YYYYMMDD, date10Map);
		
		// email
		HashMap<String, String> emailMap = new HashMap<String, String>();
		emailMap.put("key", "^([a-zA-Z0-9_\\.\\-])+\\@(([a-zA-Z0-9\\-])+\\.)+([a-zA-Z0-9]{2,4})+$");
		emailMap.put("value", "请输入正确的email！");
		regexMap.put(EMAIL, emailMap);
		
		// 手机号
		HashMap<String, String> mobileMap = new HashMap<String, String>();
		mobileMap.put("key", "^(13|14|15|18)[0-9]{9}$");
		mobileMap.put("value", "请输入正确的手机号！");
		regexMap.put(Constants.MOBILE, mobileMap);
	}

	/**
	 * 校验字符数据合法性
	 * 
	 * @param srcVal 待校验字符串
	 * @param regex 校验规则
	 * @param canNull 是否可为空,这个NULL包含空字符串
	 * @param minLength
	 * @param maxLength 第一个为最小长度，第二个为最大长度，不做限制时，无需定义该参数
	 */
	public static final Boolean checkStr(String srcVal,
			String regexKey, boolean canNull, int...length) throws ServiceException {
		if (!canNull) {
			if (StringUtils.isBlank(srcVal)) {
				return false;
			}
		}

		// 匹配校验格式,如果不输入KEY，则表示为不作校验
		if(StringUtils.isNotEmpty(regexKey)) {
			Map<String, String> regex = regexMap.get(regexKey);
			if(regex == null) {
				return false;
			}
			boolean isMatched = srcVal.matches(regex.get("key"));
			if (!isMatched) {
				return false;
			}
		}

		// 校验长度
		if(length != null) {
			if(length.length >= 1) {
				// 校验最小长度
				int len = length[0];
				if(srcVal.getBytes().length < len) {
					// 如果小于最小长度
					return false;
				}
			}
			if(length.length >= 2) {
				// 校验最大长度
				int len = length[1];
				if(srcVal.getBytes().length > len) {
					// 如果小于最大长度
					return false;
				}
			}
		}
		return true;
	}
	
	public static void main(String[] args) {
		try {
			System.out.println(CommonValidateUtil.checkStr("就", "chinese", false));
		} catch (ServiceException e) {
			System.out.println(e.getMessage());
		}
	}
}
