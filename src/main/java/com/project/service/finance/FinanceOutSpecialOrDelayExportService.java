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
public interface FinanceOutSpecialOrDelayExportService {
    /**
     * 导出特殊客栈渠道明细
     */
    void createFinanceOutSpecialExcel(HttpServletRequest request, List<FinanceInnSettlement> financeInnSettlementList, List<List<FinanceInnChannelSettlement>> financeInnChannelSettlementList,String status)throws Exception;
}
