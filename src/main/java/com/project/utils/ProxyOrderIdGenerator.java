package com.project.utils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.UUID;

/**
 * 代销订单ID生成器
 * Created by Administrator on 2015/7/3.
 */
public class ProxyOrderIdGenerator {

    public static final String ZERO = "0";
    public static final String SPLIT = "-";

    public static String generate(){
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int min = calendar.get(Calendar.MINUTE);
        int second = calendar.get(Calendar.SECOND);
        int millisecond = calendar.get(Calendar.MILLISECOND);
        return new StringBuilder()
                .append(year)
                .append(fillUp(month + 1))
                .append(fillUp(day))
                .append(fillUp(hour))
                .append(fillUp(min))
                .append(fillUp(second))
                .append(millisecond)
                .append(getRandomNum()).toString();
    }

    private static String getRandomNum(){
        return SPLIT + UUID.randomUUID();
    }

    private static String fillUp(Integer value){
        if(value < 10){
            return ZERO + value;
        }
        return value.toString();
    }

    public static void main(String[] args) {
        List<String> list = new ArrayList<>();
        for (int i = 0; i < 1000; i++) {
            String id = generate();
            if(!list.contains(id)){
                list.add(id);
            }
            System.out.println(id);
        }
        System.out.println(list.size());
    }
}
