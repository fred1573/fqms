package com.project.bean.proxysale;

import com.project.entity.proxysale.CloseDate;
import com.project.entity.proxysale.CloseLog;
import com.project.utils.CollectionsUtil;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.commons.lang3.time.DateUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 关房客栈对象
 * Created by 番茄桑 on 2015/9/9.
 */
public class InnCloseDate {
    // 客栈ID
    private Integer innId;
    // 需要执行批量关房的日期集合
    private Set<String> closeDateList;

    public InnCloseDate() {

    }
    public InnCloseDate(CloseLog closeLog) {
        this.innId = closeLog.getInnId();
        this.closeDateList = getMergeDate(closeLog);
    }

    private Set<String> getMergeDate(CloseLog closeLog) {
        Set<CloseDate> closeDates = closeLog.getCloseDates();
        Set<String> dateSet = new HashSet<>();
        if(!CollectionsUtil.isEmpty(closeDates)) {
            for(CloseDate closeDate : closeDates) {
                String closeBeginDate = closeDate.getCloseBeginDate();
                String closeEndDate = closeDate.getCloseEndDate();
                dateSet.addAll(getDate(closeBeginDate, closeEndDate));
            }
        }
        return dateSet;
    }

    private Set<String> getDate(String beginDateStr, String endDateStr) {
        Set<String> dates = new HashSet<>();
        Calendar beginCalendar = stringToCalendar(beginDateStr);
        Calendar endCalendar = stringToCalendar(endDateStr);
        while(beginCalendar.compareTo(endCalendar) <= 0) {
            dates.add(DateFormatUtils.format(beginCalendar, "yyyy-MM-dd"));
            beginCalendar.add(Calendar.DATE, 1);
        }
        return dates;
    }

    /**
     * 字符串转Calendar
     * @param dateStr
     * @return
     */
    private Calendar stringToCalendar(String dateStr) {
        SimpleDateFormat sdf= new SimpleDateFormat("yyyy-MM-dd");
        Date date = null;
        try {
            date = sdf.parse(dateStr);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return DateUtils.toCalendar(date);
    }

    public Integer getInnId() {
        return innId;
    }

    public void setInnId(Integer innId) {
        this.innId = innId;
    }

    public Set<String> getCloseDateList() {
        return closeDateList;
    }

    public void setCloseDateList(Set<String> closeDateList) {
        this.closeDateList = closeDateList;
    }
}
