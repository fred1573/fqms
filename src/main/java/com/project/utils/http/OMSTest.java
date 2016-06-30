package com.project.utils.http;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.project.common.ApiURL;
import com.project.common.Constants;
import com.project.entity.finance.FinanceParentOrder;
import com.project.utils.HttpUtil;
import com.project.utils.encode.PassWordUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by sam on 2016/1/15.
 */
public class OMSTest {
    public static void main(String[] args) {
        Map<String, Object> paramsMap = new HashMap<>();
        // 获取时间戳
        long timestamp = System.currentTimeMillis();
        paramsMap.put("timestamp", timestamp);
        // 获取签名
        paramsMap.put("signature", PassWordUtil.getDirectSignature(timestamp));
        // 设置父渠道ID
        paramsMap.put("channelId", Constants.OMS_PROXY_PID);
        paramsMap.put("otaId", Constants.OMS_PROXY_PID);
        paramsMap.put("orderType", "CHECK_OUT ");
        // 设置同步开始时间
        paramsMap.put("startDate", "2016-01-11");
        // 设置同步结束时间
        paramsMap.put("endDate", Constants.GRAB_FINANCEORDER_END_TIME);
        String result = HttpUtil.httpPost("http://192.168.1.205:8888/" + ApiURL.OMS_FINANCE_ORDER, paramsMap, false);
        List<FinanceParentOrder> financeParentOrderList = null;
        if (StringUtils.isNotBlank(result)) {
            JSONObject jsonObject = JSON.parseObject(result);
            String status = jsonObject.getString("status");
            if ("200".equals(status)) {
                JSONArray rows = jsonObject.getJSONArray("rows");
                if (!CollectionUtils.isEmpty(rows)) {
                    financeParentOrderList = new ArrayList<>();
                    for (int i = 0; i < rows.size(); i++) {
                        String order = rows.get(i).toString();
                        financeParentOrderList.add(JSONObject.parseObject(order, FinanceParentOrder.class));
                    }
                }
            }
        }
    }
}
