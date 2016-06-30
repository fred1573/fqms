package com.project.service.proxysale;

import com.project.entity.proxysale.ProxyAudit;

import java.util.List;
import java.util.Map;

/**
 *
 * Created by Administrator on 2015/8/28.
 */
public interface ProxyAuditService {

    /**
     * 保存合同审核记录
     */
    void save(Integer innId, String recordNo, String status, String reason);

    /**
     * 保存价格审核记录
     */
    void save(Integer innId, String recordNo, String status, String reason, Short pattern);

    /**
     * 获取最新一条合同记录
     */
    ProxyAudit getLastContractRecord(Integer innId);

    /**
     * 获取最新一条价格审核记录
     */
    ProxyAudit getLastPriceRecord(Integer innId, Short pattern);

    /**
     * 同步价格审核记录
     */
    void syncPriceRcords(List<ProxyAudit> audits);

    /**
     * 客栈是否存在已通过的价格审核记录
     */
    boolean hasPriceRecordChecked(Integer innId, Short pattern);
    
    /**
     *  查询客栈是否有合同审核通过的记录
     * @param innId
     * @return
     */
    Boolean hasContractssChecked(Integer innId);
    /**
     * 取得合同、普通价格、精品价格的最后审核结果，没有则不返回，如未审核精品价格，则只返回合同和普通价格的审核结果
     */
    Map<String, Object> getAuditStatus(Integer innId);

    boolean hasExsitByRecordCode(String recordCode);

    void save(ProxyAudit audit);
}
