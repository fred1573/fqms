package com.project.service.finance;

import com.project.entity.finance.FinanceInnChannelSettlement;
import com.project.entity.finance.FinanceInnSettlement;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * Created by admin on 2016/3/21.
 */
@Component
@Transactional
public interface FinanceOutArrearsExportService {
    //导出平账结算excel
    void createFinanceOutLevelArrearsExcel(HttpServletRequest request, List<FinanceInnSettlement> financeInnSettlementList, List<List<FinanceInnChannelSettlement>> financeInnChannelSettlementList, String status) throws Exception;
    //导出部分平账结算excel
    void createFinanceOutPartialArrearsExcel(HttpServletRequest request, List<FinanceInnSettlement> financeInnSettlementList, List<List<FinanceInnChannelSettlement>> financeInnChannelSettlementList, String status) throws Exception;
    //导出挂账结算
    void createFinanceOutArrearsExcel(HttpServletRequest request, List<FinanceInnSettlement> financeInnSettlementList, List<List<FinanceInnChannelSettlement>> financeInnChannelSettlementList, String status) throws Exception;
}
