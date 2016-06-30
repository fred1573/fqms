package com.project.service.api;

import com.project.bean.finance.*;
import com.project.core.orm.Page;
import com.project.dao.finance.FinanceInnSettlementDao;
import com.project.dao.finance.FinanceOrderDao;
import com.project.dao.finance.FinanceSpecialOrderDao;
import com.project.entity.finance.*;
import com.project.utils.CollectionsUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.util.*;

/**
 * Created by sam on 2016/4/14.
 */
@Service("apiFinanceService")
@Transactional
public class ApiFinanceServiceImpl implements ApiFinanceService {
    private static final Logger LOGGER = LoggerFactory.getLogger(ApiFinanceServiceImpl.class);
    @Resource
    private FinanceOrderDao financeOrderDao;
    @Resource
    private FinanceInnSettlementDao financeInnSettlementDao;
    @Resource
    private FinanceSpecialOrderDao financeSpecialOrderDao;

    @Override
    public boolean checkInnId(Integer innId, String appKey) {
        List<Map<String, Object>> innAppKey = financeOrderDao.getInnAppKey(innId, appKey);
        if (CollectionsUtil.isNotEmpty(innAppKey)) {
            return true;
        }
        LOGGER.error("appKey验证失败，innId=" + innId + ",appKey=" + appKey + "。");
        return false;
    }

    @Override
    public InnUnSettlementInfo getInnUnSettlementInfo(Integer innId) {
        checkInnId(innId);
        InnUnSettlementInfo innUnSettlementInfo = null;
        BigDecimal incomeSettlementAmount = BigDecimal.ZERO;
        // 总计应结算金额，应结金额-欠款金额
        BigDecimal totalSettlementAmount = BigDecimal.ZERO;
        Map<String, Object> balance = financeInnSettlementDao.selectUnSettlementInfo(innId, false);
        Map<String, Object> total = financeInnSettlementDao.selectUnSettlementInfo(innId, true);
        if (total != null) {
            innUnSettlementInfo = new InnUnSettlementInfo();
            Object amountTotal = total.get("amount");
            if(amountTotal != null) {
                totalSettlementAmount = new BigDecimal(String.valueOf(amountTotal));
            }
            Object amountBalance = balance.get("amount");
            if(amountBalance != null) {
                incomeSettlementAmount = new BigDecimal(String.valueOf(amountBalance));
            }
            innUnSettlementInfo.setIncomeSettlementAmount(incomeSettlementAmount);
            // 欠款金额，未结算且客栈结算金额小于0的账期
            innUnSettlementInfo.setArrearsSettlementAmount(totalSettlementAmount.subtract(incomeSettlementAmount));
            innUnSettlementInfo.setTotalSettlementAmount(totalSettlementAmount);
        }
        return innUnSettlementInfo;
    }

    @Override
    public Map<String, Object> findInnSettlementList(Integer innId, Integer pageSize, Integer pageNo) {
        checkInnId(innId);
        if (pageSize == null) {
            pageSize = 10;
        }
        if (pageNo == null) {
            pageNo = 1;
        }
        Page<FinanceInnSettlement> page = new Page<>();
        page.setOrder(Page.DESC);
        page.setOrderBy("settlement_time");
        if (pageNo != null) {
            page.setPageNo(pageNo);
        }
        if (pageSize != null) {
            page.setPageSize(pageSize);
        }
        Page<FinanceInnSettlement> financeInnSettlementPage = financeInnSettlementDao.selectFinanceInnSettlementListByInnId(page, innId);
        Map<String, Object> map = null;
        if (financeInnSettlementPage != null) {
            map = new HashMap<>();
            List<FinanceInnSettlement> result = (List<FinanceInnSettlement>) financeInnSettlementPage.getResult();
            if (CollectionsUtil.isNotEmpty(result)) {
                map.put("billList", buildBillDataList(result));
            }
            map.put("totalCount", financeInnSettlementPage.getTotalCount());
            map.put("totalPage", financeInnSettlementPage.getTotalPages());
        }
        return map;
    }

    @Override
    public Map<String, Object> findApiParentOrder(Integer innId, String settlementTime, Short priceStrategy, Integer billType, Integer pageSize, Integer pageNo) {
        checkInnId(innId);
        if (pageSize == null) {
            pageSize = 10;
        }
        if (pageNo == null) {
            pageNo = 1;
        }
        if(billType == null) {
            billType = 1;
        }
        if (billType == null) {
            throw new RuntimeException("账单类型不能为空");
        }
        if (billType == 1) {
            return getNormalBillList(innId, settlementTime, priceStrategy, pageSize, pageNo, true);
        } else if (billType == 2 || billType == 3) {
            return getSpecialBillList(innId, settlementTime, billType, priceStrategy, pageSize, pageNo, true);
        } else {
            throw new RuntimeException("账单类型异常");
        }
    }

    @Override
    public BillDetail findBillDetail(Integer innId, String settlementTime) {
        checkInnId(innId);
        if (StringUtils.isBlank(settlementTime)) {
            throw new RuntimeException("结算账期不能为空");
        }
        BillDetail billDetail = new BillDetail();
        // 普通账单集合，包括补款状态的账单
        List<ApiParentOrder> normalBillList = null;
        Map<String, Object> normalMap = getNormalBillList(innId, settlementTime, null, null, null, false);
        if (normalMap != null) {
            normalBillList = (List<ApiParentOrder>) normalMap.get("list");
        }
        // 赔付账单集合
        List<ApiParentOrder> debitBillList = null;
        Map<String, Object> debitMap = getSpecialBillList(innId, settlementTime, 2, null, null, null, false);
        if (debitMap != null) {
            debitBillList = (List<ApiParentOrder>) debitMap.get("list");
        }
        // 退款账单集合
        List<ApiParentOrder> refundBillList = null;
        Map<String, Object> refundMap = getSpecialBillList(innId, settlementTime, 3, null, null, null, false);
        if (refundMap != null) {
            refundBillList = (List<ApiParentOrder>) refundMap.get("list");
        }
        billDetail.setNormalBillList(normalBillList);
        billDetail.setDebitBillList(debitBillList);
        billDetail.setRefundBillList(refundBillList);
        return billDetail;
    }

    /**
     * 查询已接受和番茄退款的账单列表
     *
     * @param innId          PMS客栈ID
     * @param settlementTime 结算账期
     * @param priceStrategy  价格模式
     * @param pageSize       页容量
     * @param pageNo         当前页
     * @param isPage         是否分页
     * @return
     */
    private Map<String, Object> getNormalBillList(Integer innId, String settlementTime, Short priceStrategy, Integer pageSize, Integer pageNo, boolean isPage) {
        Page<FinanceParentOrder> page = new Page<>();
        if (pageNo != null) {
            page.setPageNo(pageNo);
        }
        if (pageSize != null) {
            page.setPageSize(pageSize);
        }
        Page<FinanceParentOrder> financeParentOrderPage = financeOrderDao.selectApiParentOrderList(page, innId, priceStrategy, settlementTime, isPage);
        Map<String, Object> map = null;
        if (financeParentOrderPage != null) {
            map = new HashMap<>();
            List<FinanceParentOrder> result = (List<FinanceParentOrder>) financeParentOrderPage.getResult();
            if (CollectionsUtil.isNotEmpty(result)) {
                map.put("list", buildFinanceParentOrder(result, 1));
                map.put("totalCount", financeParentOrderPage.getTotalCount());
                map.put("totalPage", financeParentOrderPage.getTotalPages());
            }
        }
        return map;
    }

    /**
     * 查询赔付、退款特殊账单
     * 退款订单中不与客栈结算的账单不展示给客栈老板
     *
     * @param innId          PMS客栈ID
     * @param settlementTime 结算账期
     * @param billType       特殊账单类型
     * @param priceStrategy  价格模式
     * @param pageSize       页容量
     * @param pageNo         当前页
     * @param isPage         是否分页
     * @return
     */
    private Map<String, Object> getSpecialBillList(Integer innId, String settlementTime, Integer billType, Short priceStrategy, Integer pageSize, Integer pageNo, boolean isPage) {
        Page<FinanceSpecialOrder> page = new Page<>();
        if (pageNo != null) {
            page.setPageNo(pageNo);
        }
        if (pageSize != null) {
            page.setPageSize(pageSize);
        }
        // 默认为赔付状态账单
        String status = FinanceSpecialOrder.DEBIT_STATUS;
        if (billType == 3) {
            status = FinanceSpecialOrder.REFUND_STATUS;
        }
        Map<String, Object> map = null;
        Page<FinanceSpecialOrder> financeSpecialOrderPage = financeSpecialOrderDao.selectSpecialBillList(page, status, innId, settlementTime, priceStrategy, isPage);
        if (financeSpecialOrderPage != null) {
            map = new HashMap<>();
            List<FinanceSpecialOrder> result = (List<FinanceSpecialOrder>) financeSpecialOrderPage.getResult();
            if (CollectionsUtil.isNotEmpty(result)) {
                map.put("list", buildFinanceSpecialOrder(result, billType));
                map.put("totalCount", financeSpecialOrderPage.getTotalCount());
                map.put("totalPage", financeSpecialOrderPage.getTotalPages());
            }
        }
        return map;
    }

    /**
     * 将父账单对象转换成账单列表对象
     *
     * @param financeParentOrderList
     * @return
     */
    private List<ApiParentOrder> buildFinanceParentOrder(List<FinanceParentOrder> financeParentOrderList, Integer billType) {
        List<ApiParentOrder> apiParentOrderList = null;
        if (CollectionsUtil.isNotEmpty(financeParentOrderList)) {
            apiParentOrderList = new ArrayList<>();
            for (FinanceParentOrder financeParentOrder : financeParentOrderList) {
                ApiParentOrder apiParentOrder = new ApiParentOrder();
                copyFinanceParentOrder(apiParentOrder, financeParentOrder, billType);
                BigDecimal fqSettlementAmount = BigDecimal.ZERO;
                // 获取客栈结算金额
                BigDecimal innSettlementAmount = financeParentOrder.getInnSettlementAmount();
                BigDecimal innAmount = financeParentOrder.getInnAmount();
                if(innSettlementAmount != null && innAmount != null) {
                    fqSettlementAmount = innAmount.subtract(innSettlementAmount);
                }
                apiParentOrder.setFqSettlementAmount(fqSettlementAmount);
                apiParentOrder.setInnSettlementAmount(financeParentOrder.getInnSettlementAmount());
                apiParentOrderList.add(apiParentOrder);
            }
        }
        return apiParentOrderList;
    }

    /**
     * 将父账单的属性拷贝到账单列表对象
     *
     * @param apiParentOrder     账单列表对象
     * @param financeParentOrder 父账单对象
     * @param billType 账单类型
     */
    private void copyFinanceParentOrder(ApiParentOrder apiParentOrder, FinanceParentOrder financeParentOrder, Integer billType) {
        apiParentOrder.setPriceStrategy(financeParentOrder.getOrderMode());
        apiParentOrder.setChannelOrderNo(financeParentOrder.getChannelOrderNo());
        apiParentOrder.setUserName(financeParentOrder.getUserName());
        apiParentOrder.setContact(financeParentOrder.getContact());
        apiParentOrder.setInnAmount(financeParentOrder.getInnAmount());
        apiParentOrder.setBillType(billType);
        Date orderTime = financeParentOrder.getOrderTime();
        String orderTimeStr = "未知";
        if (orderTime != null) {
            orderTimeStr = DateFormatUtils.format(orderTime, "yyyy-MM-dd HH:mm:ss");
        }
        apiParentOrder.setOrderTime(orderTimeStr);
        Set<FinanceOrder> channelOrderList = financeParentOrder.getChannelOrderList();
        List<ApiOrder> orderList = null;
        if (CollectionsUtil.isNotEmpty(channelOrderList)) {
            orderList = new ArrayList<>();
            for (FinanceOrder financeOrder : channelOrderList) {
                // 排除删除的子账单
                if (!financeOrder.getDeleted()) {
                    ApiOrder apiOrder = new ApiOrder();
                    // 房型
                    apiOrder.setChannelRoomTypeName(financeOrder.getChannelRoomTypeName());
                    // 房间数
                    apiOrder.setRoomTypeNums(financeOrder.getRoomTypeNums());
                    // 客栈单价
                    apiOrder.setInnAmount(financeOrder.getInnAmount());
                    // 入住日期
                    apiOrder.setCheckInAt(financeOrder.getCheckInAtStr());
                    // 退房日期
                    apiOrder.setCheckOutAt(financeOrder.getCheckOutAtStr());
                    orderList.add(apiOrder);
                }
            }
        }
        apiParentOrder.setOrderList(orderList);
    }

    /**
     * 将特殊账单对象转换成账单列表对象
     *
     * @param financeSpecialOrderList
     * @return
     */
    private List<ApiParentOrder> buildFinanceSpecialOrder(List<FinanceSpecialOrder> financeSpecialOrderList, Integer billType) {
        List<ApiParentOrder> apiParentOrderList = null;
        if (CollectionsUtil.isNotEmpty(financeSpecialOrderList)) {
            apiParentOrderList = new ArrayList<>();
            for (FinanceSpecialOrder financeSpecialOrder : financeSpecialOrderList) {
                ApiParentOrder apiParentOrder = new ApiParentOrder();
                FinanceParentOrder financeParentOrder = financeSpecialOrder.getFinanceParentOrder();
                if (financeParentOrder != null) {
                    copyFinanceParentOrder(apiParentOrder, financeParentOrder, billType);
                }
                // 赔付金额（赔付订单）
                apiParentOrder.setInnPayment(financeSpecialOrder.getInnPayment());
                // 退款金额（退款订单）
                apiParentOrder.setInnRefund(financeSpecialOrder.getInnRefund());
                apiParentOrderList.add(apiParentOrder);
            }
        }
        return apiParentOrderList;
    }

    /**
     * 检查客栈ID
     *
     * @param innId
     */
    private void checkInnId(Integer innId) {
        if (innId == null) {
            throw new RuntimeException("PMS客栈ID不能为空");
        }
    }

    /**
     * 根据客栈结算列表，构造客栈账单VO对象
     *
     * @param financeInnSettlementList
     * @return
     */
    private List<BillData> buildBillDataList(List<FinanceInnSettlement> financeInnSettlementList) {
        List<BillData> billDataList = null;
        if (CollectionsUtil.isNotEmpty(financeInnSettlementList)) {
            billDataList = new ArrayList<>();
            for (FinanceInnSettlement financeInnSettlement : financeInnSettlementList) {
                BillData billData = new BillData();
                FinanceInnSettlementInfo financeInnSettlementInfo = financeInnSettlement.getFinanceInnSettlementInfo();
                if (financeInnSettlementInfo != null) {
                    billData.setInnId(financeInnSettlementInfo.getId());
                }
                billData.setSettlementTime(financeInnSettlement.getSettlementTime());
                billData.setTotalOrder(financeInnSettlement.getTotalOrder());
                billData.setTotalAmount(financeInnSettlement.getTotalAmount());
                // 获取正常订单结算金额
                BigDecimal innSettlementAmount = financeInnSettlement.getInnSettlementAmount();
                // 获取番茄补款金额
                BigDecimal fqReplenishment = financeInnSettlement.getFqReplenishment();
                if (innSettlementAmount != null && fqReplenishment != null) {
                    innSettlementAmount = innSettlementAmount.add(fqReplenishment);
                }
                billData.setInnSettlementAmount(innSettlementAmount);
                billData.setRefundAmount(financeInnSettlement.getRefundAmount());
                billData.setInnPayment(financeInnSettlement.getInnPayment());
                billData.setAfterPaymentAmount(financeInnSettlement.getAfterPaymentAmount());
                String settlementStatus = financeInnSettlement.getSettlementStatus();
                // 0:未结算，1:已结算,2:纠纷延期
                String settlementStatusStr = "";
                if (StringUtils.isNotBlank(settlementStatus)) {
                    if ("0".equals(settlementStatus)) {
                        settlementStatusStr = "未结算";
                    } else if ("1".equals(settlementStatus)) {
                        settlementStatusStr = "已结算";
                    } else if ("2".equals(settlementStatus)) {
                        settlementStatusStr = "纠纷延期";
                    } else {
                        settlementStatusStr = "未知";
                    }
                }
                // 0：未确认，1：已确认，2：系统自动确认
                String confirmStatus = financeInnSettlement.getConfirmStatus();
                String confirmStatusStr = "未知";
                if (StringUtils.isNotBlank(confirmStatus)) {
                    if ("0".equals(confirmStatus)) {
                        confirmStatusStr = "未确认";
                    } else if ("1".equals(confirmStatus)) {
                        confirmStatusStr = "已确认";
                    } else if ("2".equals(confirmStatus)) {
                        confirmStatusStr = "系统自动确认";
                    } else {
                        confirmStatusStr = "未知";
                    }
                }
                billData.setSettlementStatus(settlementStatusStr);
                billData.setConfirmStatus(confirmStatusStr);
                billDataList.add(billData);
            }
        }
        return billDataList;
    }
}
