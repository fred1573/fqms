package com.project.service.api;

import com.project.bean.finance.BillDetail;
import com.project.bean.finance.InnUnSettlementInfo;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;
import java.util.Map;

/**
 * 财务对账对外提供的HTTP接口服务，业务逻辑处理接口
 * Created by sam on 2016/4/14.
 */
@Component
@Transactional
public interface ApiFinanceService {
    /**
     * 根据客栈ID和appkey验证参数是否合法
     *
     * @param innId
     * @param appKey
     * @return
     */
    boolean checkInnId(Integer innId, String appKey);

    /**
     * 根据PMS客栈ID查询客栈未结算数据
     * 包括应结金额、欠款金额、剩余结算金额
     * @param innId
     * @return
     */
    InnUnSettlementInfo getInnUnSettlementInfo(Integer innId);

    /**
     * 根据PMS客栈ID查询客栈往期的账单列表
     * @param innId PMS客栈ID
     * @param pageSize 页容量
     * @param pageNo 当前页数
     * @return
     */
    Map<String, Object> findInnSettlementList(Integer innId, Integer pageSize, Integer pageNo);

    /**
     * 分页查询代销客栈指定状态账单列表
     * @param innId PMS客栈ID
     * @param settlementTime 结算账期
     * @param priceStrategy 价格策略
     * @param billType 1：正常订单，2：赔付订单，3：退款订单
     * @param pageSize 页容量
     * @param pageNo 当前页数
     * @return
     */
    Map<String, Object> findApiParentOrder(Integer innId, String settlementTime, Short priceStrategy, Integer billType, Integer pageSize, Integer pageNo);

    /**
     * 根据
     * @param innId
     * @param settlementTime
     * @return
     */
    BillDetail findBillDetail(Integer innId, String settlementTime);
}
