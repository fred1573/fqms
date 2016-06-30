package com.project.service.proxysale;

import java.util.List;
import java.util.Map;

/**
 * Created by admin on 2016/5/30.
 */
public interface PriceUpdateService {
    /**
     * 获取区域名称
     *
     * @return
     */
    List<Map<String, Object>> findRegionName();

    /**
     * 根据ID获取渠道名称
     *
     * @return
     */
    List<Map<String, Object>> findSaleChannel();

    /**
     * 批量调价
     *
     * @param jsonStr
     */
    void batchUpdatePrice(String jsonStr);

    /**
     * 失败日志
     *
     * @param jsonStr
     */
    void saveErrorLog(String jsonStr);
}
