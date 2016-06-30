package com.project.service.proxysale;

import com.project.entity.proxysale.ProxyInnOnoff;

import java.util.Date;
import java.util.Map;

/**
 *
 * Created by Administrator on 2015/8/4.
 */
public interface ProxyInnPriceService {

    String STATUS_UNCHECK = "UNCHECK";  //待审核
    String STATUS_CHECKED = "CHECKED";  //通过
    String STATUS_REJECT = "REJECT";    //驳回
    String STATUS_EVERY = "EVERY";    //任意

    String list(Map<String, String> params);

    /**
     * 审核通过, 通知toms上架该客栈，成功则回复oms审核通过，否则回复oms审核通过失败
     * 历史数据处理完就干掉
     */
    boolean checkSuc(String recordCode, Integer innId, Short pattern);

    /**
     * 历史数据处理完就干掉
     * @param recordCode
     * @param innId
     * @param pattern
     * @param userId
     * @return
     */
    boolean defaultCheckSuc(String recordCode, Integer innId, Short pattern, Long userId);

    /**
     * 审核驳回
     */
    boolean checkReject(String recordCode, Integer innId, Short pattern, String reason);

    /**
     * 获取最新一条价格审核记录状态
     */
    String getLastRecordStatus(Integer accountId);

    /**
     * 全量同步价格审核记录
     */
    void syncAudit();

    /**
     * 按时间同步价格审核记录
     */
    void syncAudit(Date from, Date to);

    /**
     * 根据客栈ID查询客栈的下架历史记录集合
     * @param innId 客栈ID
     * @return 下架历史记录，如果有多个取最近的一条
     */
    ProxyInnOnoff isOffedProxyInn(Integer innId);

}
