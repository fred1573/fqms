package com.project.utils;

import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import org.apache.commons.lang3.StringUtils;

import com.project.utils.encode.RandomUtil;

public class Chinese {
	
	public static String randomChinese(int count) {
		int hightPos, lowPos; // 定义高低位
		StringBuilder sb = new StringBuilder();
		Random random = new Random();
		for (int i = 0; i < count; i++) {
			hightPos = (176 + Math.abs(random.nextInt(39)));// 获取高位值
			lowPos = (161 + Math.abs(random.nextInt(93)));// 获取低位值
			byte[] b = new byte[2];
			b[0] = (new Integer(hightPos).byteValue());
			b[1] = (new Integer(lowPos).byteValue());
			try {
				sb.append(new String(b, "GBk"));
			} catch (UnsupportedEncodingException e) {
				sb.append("中");
			}
		}
		return sb.toString();
	}
		
	public static void main(String[] args) throws Exception {
		String[] names = {"香","园","美","丽","远","恒","富","贵","吉","达","豁","飞","腾","祥","寿","囍","禧","祥","吉","瑞","美 ","熙","喜","福","禄","高","良","优","玺","庆","彩",
				"如","意","龙","风","福","秀","妍","嫣","妩","妙","娇","姣","娈","姿","姬","娆","琳","琍","琦","玲"};
		List<String> nameList = Arrays.asList(names);
		for(int i=5000;i<5300;i++){
			System.out.println(StringUtils.join(RandomUtil.randomSome(nameList, i%5==0?2:i%5), "")+"客栈");
		}
	}
	
}
