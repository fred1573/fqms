package com.project.service.finance;

import com.project.core.orm.Page;
import com.project.entity.finance.FinanceInnSettlement;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;
import java.util.List;

/**
 * Created by admin on 2016/3/9.
 */
@Component
@Transactional

public interface FinanceArrearInnService {

    /**
     * 客栈平账处理
     * @param jsonData
     */
    void FinanceLevelArrears(String jsonData);

    /**
     * 查询往期挂账信息
     * @param innId
     * @return
     */
    List<FinanceInnSettlement> findPastArrears(Integer innId, String settlementTime);


    /**
     * 根据账期,删除挂账记录
     *
     * @param settlementTime
     */
    void deleteFinanceArrearsInn(String settlementTime);



    /**
     * 分页查询累计挂账
     * @param page
     * @param settlementTime
     * @param arrearsStatus
     * @param innName
     * @return
     */
    Page<FinanceInnSettlement> getTotalArrearsPage(Page<FinanceInnSettlement> page, String settlementTime, String arrearsStatus, String innName);
}
