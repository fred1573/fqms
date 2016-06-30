package com.project.service.finance;

import com.project.core.orm.Page;
import com.project.entity.finance.FinanceParentOrder;
import com.project.entity.finance.FinanceSpecialOrder;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Map;

/**
 * 账单核对业务逻辑处理对象
 * Created by sam on 2016/3/15.
 */
@Component
@Transactional
public interface BillCheckService {
    /**
     * 根据账单ID查询账单的详细信息
     * 包括父订单、子订单集合和可选的
     *
     * @param billId 账单ID
     * @return
     */
    Map<String, Object> findBillDetailInfo(String billId);

    /**
     * 更新订单
     *
     * @param jsonData
     */
    void updateOrder(String jsonData);

    /**
     * 根据结算月份查询渠道结算的汇总数据
     *
     * @param settlementTime 结算时间
     * @return 渠道商名称、渠道ID、订单总数、订单总金额、渠道结算金额、客栈结算金额
     */
    List<Map<String, Object>> findFinanceChannelSettlement(String settlementTime);

    /**
     * 统计渠道指定结算月份的结算汇总数据
     *
     * @param settlementTime 结算月份
     * @return 订单总数、订单总金额、渠道结算金额、客栈结算金额
     */
    Map<String, Object> findTotalChannelSettlement(String settlementTime);

    /**
     * 根据分销商ID、账期、账单状态，统计账单总数、分销商订单总额、分销商结算金额、 客栈订单总额为、客栈结算金额
     * @param channelId 分销商ID
     * @param settlementTime 账期
     * @param status 账单状态
     * @return
     */
    Map<String, Object> findTotalChannelOrder(Integer channelId, String settlementTime, Integer status);

    /**
     * 账单核对详情页面，分页查询订单列表
     *
     * @param page           分页对象
     * @param channelId      渠道ID
     * @param settlementTime 结算月份
     * @param auditStatus    核单状态
     * @param priceStrategy  价格策略
     * @param isBalance  是否结算
     * @param keyWord        模糊搜索关键字（客栈名称/订单号）
     * @param orderStatus    订单状态
     * @return
     */
    Page<FinanceParentOrder> findFinanceParentOrder(Page<FinanceParentOrder> page, Integer channelId, String settlementTime, String auditStatus, Short priceStrategy, Short isBalance, String keyWord, Integer orderStatus);

    /**
     * 分页查询特殊账单列表
     * @param page
     * @param statusKey 特殊账单状态，可选值：debit（赔付）、refund（退款）、replenishment（补款）
     * @param channelId 分销商ID
     * @param settlementTime 账期
     * @param auditStatus 是否核单
     * @param priceStrategy 价格模式
     * @param isBalance 是否结算
     * @param keyWord 搜索关键字
     * @return
     */
    Page<FinanceSpecialOrder> findSpecialOrderList(Page<FinanceSpecialOrder> page, String statusKey, Integer channelId, String settlementTime, String auditStatus, Short priceStrategy, Short isBalance, String keyWord);

    /**
     * 根据渠道ID、结算月份和核单状态查询数据库中满足条件的订单状态集合
     *
     * @param channelId      渠道ID
     * @param settlementTime 结算月份
     * @param auditStatus    核单状态
     * @return 满足条件的订单状态集合
     */
    String getExistOrderStatus(Integer channelId, String settlementTime, String auditStatus);
}
