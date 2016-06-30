package com.project.bean.finance;

import java.util.List;

/**
 * 数据统计柱状和线性混搭表数据封装对象
 * Created by sam on 2015/12/24.
 */
public class BarAndLineData {
    // X轴数据
    private List<String> axis;
    // Y轴数据
    private List<List<String>> series;

    public List<String> getAxis() {
        return axis;
    }

    public void setAxis(List<String> axis) {
        this.axis = axis;
    }

    public List<List<String>> getSeries() {
        return series;
    }

    public void setSeries(List<List<String>> series) {
        this.series = series;
    }
}
