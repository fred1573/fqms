package com.project.service.finance;

import com.project.core.orm.Page;
import com.project.entity.finance.FinanceInnChannelSettlement;
import com.project.entity.finance.FinanceParentOrder;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.transaction.Transactional;
import java.util.List;
import java.util.Map;

/**
 * Created by admin on 2016/1/13.
 */
@Component
@Transactional
public interface FinanceInnChannelSettlementService {

    /**
     * 获取总金额，总订单等属性
     *
     * @param settlementTime
     * @param channelId
     * @return
     */
    Map<String, Object> findInnChannelSettlementStatus(String settlementTime, Integer channelId);

    /**
     * 一键填充客栈实付金额
     *
     * @param settlementTime
     * @param channel
     */
    void fillRealPay(String settlementTime, Integer channel);

    /**
     * 设置客栈实付金额
     *
     * @param jsonData
     */
    void updateRealPay(String jsonData);

    /**
     * 获取需要导出的客栈和订单列表
     *
     * @param response
     * @param channelId
     * @param settlementTime
     */
    void exportInnOrder(HttpServletResponse response, Integer channelId, String settlementTime);

    /**
     * 获取需要导出的客栈和订单列表（异步）
     *
     * @param request
     * @param channelId
     * @param settlementTime
     */
    void batchExportInnOrder(HttpServletRequest request, Integer channelId, String settlementTime, String channelName);

    /**
     * 创建表格
     *
     * @param response
     * @param financeInnChannelSettlementList
     * @param financeParentOrderList
     */
    void createFinanceExcel(HttpServletResponse response, List<FinanceInnChannelSettlement> financeInnChannelSettlementList, List<List<FinanceParentOrder>> financeParentOrderList);

    /**
     * 创建表格（异步）
     *
     * @param request
     * @param financeInnChannelSettlementList
     * @param financeParentOrderList
     */
    void batchCreateFinanceExcel(HttpServletRequest request, List<FinanceInnChannelSettlement> financeInnChannelSettlementList, List<List<FinanceParentOrder>> financeParentOrderList, String channelName);

    /**
     * 按条件筛选客栈
     *
     * @param page
     * @param settlementTime
     * @param channelId
     * @param innName
     * @param isMatch
     * @return
     */
    Page<FinanceInnChannelSettlement> financeInnChannelSettlementWithRequire(Page<FinanceInnChannelSettlement> page, String settlementTime, Integer channelId, String innName, Boolean isMatch);

    /**
     * 导出批量代付execl
     *
     * @param response
     * @throws Exception
     */
    void createFinanceExcelWithPay(HttpServletResponse response, String settlementTime) throws Exception;

    /**
     * 按渠道统计客栈订单信息
     *
     * @param innId
     * @param settlementTime
     * @return
     */
    List<FinanceInnChannelSettlement> selectChannelOrder(Integer innId, String settlementTime, Boolean isMatch);

    /**
     * 统计客栈订单信息
     *
     * @param innId
     * @param settlementTime
     * @return
     */
    List<Map<String, Object>> statisticChannelOrderTotal(Integer innId, String settlementTime);

    Map<String, Object> getInnChannelOrderAmount(String settlementTime, Integer channelId, String innName);

    /**
     * 根据ID和账期查询渠道客栈信息
     *
     * @param id
     * @param settlementTime
     * @return
     */
    List<FinanceInnChannelSettlement> findFinanceInnChannelSettlementByInnId(Integer id, String settlementTime);

    /**
     * 挂账客栈按渠道统计
     *
     * @param settlementTime
     * @param innId
     * @param isMatch
     * @return
     */
    Map<String, Object> statisticArrearsChannel(String settlementTime, Integer innId, Boolean isMatch);

    /**
     * 指定渠道下的客栈暂收金额详情
     *
     * @param channelId
     * @param settlementTime
     * @return
     */
    Page<Map<String,Object>> findFqTempInn(Page<Map<String,Object>> page, Integer channelId, String settlementTime);


}
