package com.project.bean.finance;

import java.util.List;
import java.util.Map;

/**
 * Created by admin on 2015/12/25.
 */
public class PieData {
    // 状态或时间段关键字
    private List<String> keys;
    // 订单数
    private List<String> values;
    // 表格数据
    private List<Map<String, Object>> listMap;

    public List<Map<String, Object>> getListMap() {
        return listMap;
    }

    public void setListMap(List<Map<String, Object>> listMap) {
        this.listMap = listMap;
    }

    public List<String> getKeys() {
        return keys;
    }

    public void setKeys(List<String> keys) {
        this.keys = keys;
    }

    public List<String> getValues() {
        return values;
    }

    public void setValues(List<String> values) {
        this.values = values;
    }


}
