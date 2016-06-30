package com.project.service.finance;

import javax.servlet.http.HttpServletRequest;

/**
 * 进账结算信息导出
 *
 * @author frd
 */

public interface FinanceIncomeExportService {

    /**
     * 导出分销商下所有客栈结算数据汇总及明细，这个导出很不友好0_o...
     */
    void exportInnSettlement(HttpServletRequest request, String settlementTime, Integer channelId);

    void createIncomeChannelFinanceExcel(HttpServletRequest request, String settlementTime);

    /**
     * 导出番茄暂收
     *
     * @param request
     * @param channelId
     * @param settlementTime
     * @param channelName
     */
    void exportFqTemp(HttpServletRequest request, Integer channelId, String settlementTime, String channelName);
}
