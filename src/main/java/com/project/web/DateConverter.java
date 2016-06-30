package com.project.web;

import org.apache.commons.lang3.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.convert.converter.Converter;
import org.springframework.util.StringUtils;

import java.text.ParseException;
import java.util.Date;

/**
 * 日期转换器
 *
 * @author: yuneng.huang
 * Date: 15-1-19
 * Time: 下午4:46
 */
public class DateConverter implements Converter<String, Date> {
    private static String[] patterns = new String[]{"yyyy-MM-dd HH:mm:ss", "yyyy-MM-dd HH:mm","yyyy-MM-dd","yyyy-MM"};
    private Logger logger = LoggerFactory.getLogger(DateConverter.class);

    @Override
    public Date convert(String source) {
        Date formatDate = null;
        if (!StringUtils.hasText(source)) {
            return null;
        }
        try {
            formatDate = DateUtils.parseDate(source, patterns);
        } catch (ParseException e) {
            logger.error("日期转换出错，source："+source,e);
        }

        return formatDate;
    }

}
