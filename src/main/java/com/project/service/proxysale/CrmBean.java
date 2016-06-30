package com.project.service.proxysale;

import java.util.Map;

/**
 *
 * Created by Administrator on 2015/8/31.
 */
public interface CrmBean {

    /**
     * 更新CRM客栈状态
     */
    void updateCRM(Map<String, Object> params);

    /**
     * 获取客栈审核状态(已底价审核通过)
     * 1.合同是否审核通过:
     *      1)是，到2.
     *      2)否，合同是否待审核或已重新提交（CRM接口）：
     *          i）是，则判断卖价审核状态，获取oms最后一条卖价审核单状态，若为待审核或审核通过，则返回状态待审核；若为审核否决，则返回审核否决
     *          ii)否，判断最新审核状态是否是否决，是：返回审核否决
     * 2.合同审核通过，判断卖价审核状态
     *      1)待审核：返回底价审核通过
     *      2)审核否决：返回底价审核通过
     *      3)审核通过：返回审核通过
     */
    String getInnStatusWithBasePriceChecked(Integer innId);

    /**
     * 获取客栈审核状态(已卖价审核通过)
     * 1.合同是否审核通过:
     *      1)是，到2.
     *      2)否，合同是否待审核或已重新提交（CRM接口）：
     *          i）是，则判断底价审核状态，获取oms最后一条底价审核单状态，若为待审核或审核通过，则返回状态待审核；若为审核否决，则返回审核否决
     *          ii)否，判断最新审核状态是否是否决，是：返回审核否决
     * 2.合同审核通过，判断底价审核状态
     *      1)待审核：返回卖价审核通过
     *      2)审核否决：返回卖价审核通过
     *      3)审核通过：返回审核通过
     */
    String getInnStatusWithSalePriceChecked(Integer innId);

    String getInnStatusWithBasePriceReject(Integer innId);

    String getInnStatusWithSalePriceReject(Integer innId);

    /**
     * 获取客栈审核状态(已合同审核通过)
     * 1.底价是否审核通过，获取oms最后一条底价审核单状态:
     *      1)若为审核通过，则到2；：
     *      2)若为审核否决，则获取oms最后一条卖价审核状态：
     *          i)若为待审核，则返回审核否决；
     *          ii)若为审核否决，则返回审核否决；
     *          iii)若为审核通过，则返回卖价审核通过
     *      3)若为待审核，则获取oms最后一条卖价审核状态：
     *          i)若为待审核，则返回待审核；
     *          ii)若为审核否决，则返回审核否决；
     *          iii)若为审核通过，则返回卖价审核通过
     * 2.底价审核通过，判断卖价审核状态
     *      1)待审核：返回底价审核通过
     *      2)审核否决：返回底价审核通过
     *      3)审核通过：返回审核通过
     */
    String getInnStatusWithContractChecked(Integer pmsInnId);

    /**
     * 计算失败原因
     * @param innId
     * @param reason
     * @return
     */
    String getContractRejectedReason(Integer innId, String reason);

    String getBaseRejectedReason(Integer innId, String reason);

    String getSaleRejectedReason(Integer innId, String reason);
}
