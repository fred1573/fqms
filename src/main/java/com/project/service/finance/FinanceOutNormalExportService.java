package com.project.service.finance;

import com.project.entity.finance.FinanceInnChannelSettlement;
import com.project.entity.finance.FinanceInnSettlement;
import com.project.entity.finance.FinanceParentOrder;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;
import java.util.List;

/**
 * Created by admin on 2016/3/21.
 */
@Component
@Transactional
public interface FinanceOutNormalExportService {


    /**
     * 导出正常客栈渠道明细
     */
    void createFinanceExcel(HttpServletRequest request, List<FinanceInnSettlement> financeInnSettlementList, List<List<FinanceInnChannelSettlement>> financeInnChannelSettlementList)throws Exception;

}
