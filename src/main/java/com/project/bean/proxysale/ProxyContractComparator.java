package com.project.bean.proxysale;

import com.project.entity.proxysale.ProxyContract;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Comparator;
import java.util.Date;

/**
 * Created by Administrator on 2015/9/6.
 */
public class ProxyContractComparator implements Comparator<ProxyContract> {
    @Override
    public int compare(ProxyContract o1, ProxyContract o2) {
        Date time1;
        Date time2;
        try {
            time1 = new SimpleDateFormat("yyyy-MM-dd").parse(o1.getCommitTime());
            time2 = new SimpleDateFormat("yyyy-MM-dd").parse(o2.getCommitTime());
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        return time1.compareTo(time2);
    }
}
