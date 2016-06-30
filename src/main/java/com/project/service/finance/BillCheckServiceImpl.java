package com.project.service.finance;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.project.bean.bo.ChannelSettlementData;
import com.project.core.orm.Page;
import com.project.dao.finance.*;
import com.project.entity.finance.*;
import com.project.utils.BeanUtil;
import com.project.utils.CollectionsUtil;
import com.project.utils.FinanceHelper;
import com.project.utils.NumberUtil;
import com.project.utils.time.DateUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.util.*;

/**
 * Created by sam on 2016/3/15.
 */
@Service("billCheckService")
@Transactional
public class BillCheckServiceImpl implements BillCheckService {
    private static final Logger LOGGER = LoggerFactory.getLogger(BillCheckServiceImpl.class);
    @Resource
    private FinanceOrderDao financeOrderDao;
    @Resource
    private FinanceAccountPeriodDao financeAccountPeriodDao;
    @Resource
    private FinanceSpecialOrderDao financeSpecialOrderDao;
    @Resource
    private FinanceHelper financeHelper;
    @Resource
    private FinanceOperationLogDao financeOperationLogDao;
    @Resource
    private FinanceChannelSettlementDao financeChannelSettlementDao;

    @Override
    public Map<String, Object> findBillDetailInfo(String billId) {
        Map<String, Object> dataMap = null;
        // 查询父账单、子账单集合
        FinanceParentOrder financeParentOrder = financeOrderDao.findById(billId);
        if (financeParentOrder != null) {
            dataMap = new HashMap<>();
            dataMap.put("parentOrder", financeParentOrder);
            // 查询是否是特殊账单
            FinanceSpecialOrder financeSpecialOrder = financeSpecialOrderDao.selectFinanceSpecialOrderByOrderId(financeParentOrder.getId());
            if (financeSpecialOrder != null) {
                dataMap.put("specialOrder", financeSpecialOrder);
            }
            String settlementTime = financeParentOrder.getSettlementTime();
            // 查询可以修改的账期对象集合
            List<String> financeAccountPeriodList = financeAccountPeriodDao.selectFinanceAccountPeriodListBySettlementTime(settlementTime);
            dataMap.put("accountPeriodList", financeAccountPeriodList);
        }
        return dataMap;
    }

    @Override
    public void updateOrder(String jsonData) {
        if (StringUtils.isBlank(jsonData)) {
            throw new RuntimeException("账单数据为空");
        }
        JSONObject jsonObject = JSON.parseObject(jsonData);
        // 获取父账单json对象
        String parentOrder = jsonObject.getString("parentOrder");
        if (StringUtils.isBlank(parentOrder)) {
            throw new RuntimeException("账单对象不存在");
        }
        FinanceParentOrder financeParentOrder = JSONObject.parseObject(parentOrder, FinanceParentOrder.class);
        if (financeParentOrder == null) {
            throw new RuntimeException("账单对象解析失败");
        }
        String id = financeParentOrder.getId();
        if (id == null) {
            throw new RuntimeException("账单ID不能为空");
        }
        // 查询原账单对象
        FinanceParentOrder oldFinanceParentOrder = financeOrderDao.findById(id);
        if (oldFinanceParentOrder == null) {
            throw new RuntimeException("原始账单不存在");
        }
        String specialOrder = jsonObject.getString("specialOrder");
        // 获取特殊账单对象
        FinanceSpecialOrder financeSpecialOrder = JSONObject.parseObject(specialOrder, FinanceSpecialOrder.class);
        // 保存账单
        saveOrUpdateOrder(financeParentOrder, oldFinanceParentOrder, financeSpecialOrder);
        // 获取修改原因
        String modifyReason = jsonObject.getString("modifyReason");
        // 保存操作日志
        saveUpdateOrderLog(oldFinanceParentOrder, modifyReason);
    }

    /**
     * 更新账单对象
     *
     * @param financeParentOrder    前台传递的修改后的账单对象
     * @param oldFinanceParentOrder 数据库中原有的账单对象
     * @param financeSpecialOrder   前台传递的修改后的特殊账单对象
     */
    private void saveOrUpdateOrder(FinanceParentOrder financeParentOrder, FinanceParentOrder oldFinanceParentOrder, FinanceSpecialOrder financeSpecialOrder) {
        // 获取结算状态
        String settlementStatus = oldFinanceParentOrder.getSettlementStatus();
        // 如果是已结算账单，不能再次修改，只能执行复制操作
        if ("1".equals(settlementStatus)) {
            processSettlementOrder(financeParentOrder, oldFinanceParentOrder, financeSpecialOrder);
        } else {
            processUnSettlementOrder(financeParentOrder, oldFinanceParentOrder, financeSpecialOrder);
        }
    }

    /**
     * 处理已结算账单，结算账单，不能再次修改，只能执行复制操作
     *
     * @param financeParentOrder    修改后的账单对象
     * @param oldFinanceParentOrder 数据库中保存的原账单对象
     * @param financeSpecialOrder   数据库中保存的原特殊账单对象（可以为空）
     */
    private void processSettlementOrder(FinanceParentOrder financeParentOrder, FinanceParentOrder oldFinanceParentOrder, FinanceSpecialOrder financeSpecialOrder) {
        if (!isNeedCopyBill(financeParentOrder, oldFinanceParentOrder)) {
            throw new RuntimeException("已结算状态的账单不能再次修改");
        }
        // 获得修改后的产生周期
        String newProduceTime = financeParentOrder.getProduceTime();
        // 特殊账单复制流程
        if (financeSpecialOrder != null) {
            Integer id = financeSpecialOrder.getId();
            if (id == null) {
                throw new RuntimeException("特殊账单ID不能为空");
            }
            FinanceSpecialOrder oldFinanceSpecialOrder = financeSpecialOrderDao.selectFinanceSpecialOrderById(id);
            if (oldFinanceSpecialOrder == null) {
                throw new RuntimeException("特殊账单对象不存在");
            }
            FinanceSpecialOrder financeSpecialOrderCopy = copyFinanceSpecialOrder(newProduceTime, oldFinanceSpecialOrder);
            // 保存原特殊账单对象
            financeSpecialOrderDao.save(oldFinanceSpecialOrder);
            // 保存复制后的特殊账单对象
            financeSpecialOrderDao.save(financeSpecialOrderCopy);
        } else {
            // 正常账单复制流程
            FinanceParentOrder financeParentOrderCopy = copyFinanceParentOrder(newProduceTime, oldFinanceParentOrder);
            // 保存原账单对象
            financeOrderDao.save(oldFinanceParentOrder);
            // 保存复制后的账单对象
            financeOrderDao.save(financeParentOrderCopy);
        }
    }

    /**
     * 处理未结算
     *
     * @param financeParentOrder    修改后的账单对象
     * @param oldFinanceParentOrder 数据库中保存的原账单对象
     * @param financeSpecialOrder   修改后的特殊账单对象（可以为空）
     */
    private void processUnSettlementOrder(FinanceParentOrder financeParentOrder, FinanceParentOrder oldFinanceParentOrder, FinanceSpecialOrder financeSpecialOrder) {
        // 账单原状态
        String oldStatus = String.valueOf(oldFinanceParentOrder.getStatus());
        // 修改后的账单状态
        String newStatus = String.valueOf(financeParentOrder.getStatus());
        String newProduceTime = financeParentOrder.getProduceTime();
        String oldProduceTime = oldFinanceParentOrder.getProduceTime();
        // 如果修改后是特殊账单
        if (isSpecialStatus(newStatus)) {
            if (financeSpecialOrder == null) {
                throw new RuntimeException("特殊账单数据不能为空");
            }
            // 正常单转特殊单
            processSpecialOrder(financeParentOrder, oldFinanceParentOrder, financeSpecialOrder);
            // 保存修改后的账单对象
            financeSpecialOrderDao.save(financeSpecialOrder);
            // 判断是否需要复制账单
            if (!newProduceTime.equals(oldProduceTime)) {
                FinanceSpecialOrder financeSpecialOrderCopy = copyFinanceSpecialOrder(newProduceTime, financeSpecialOrder);
                // 保存复制的账单
                financeSpecialOrderDao.save(financeSpecialOrderCopy);
            }
        } else {
            // 特殊账单->正常账单
            if (isSpecialStatus(oldStatus)) {
                // 获取原订单ID
                String id = oldFinanceParentOrder.getId();
                // 删除特殊账单对象
                if (StringUtils.isNotBlank(id)) {
                    financeSpecialOrderDao.delete(id);
                }
            }
            // 处理正常账单的数据
            processParentOrder(financeParentOrder, oldFinanceParentOrder, financeSpecialOrder);
            financeOrderDao.save(oldFinanceParentOrder);
            if (!newProduceTime.equals(oldProduceTime)) {
                FinanceParentOrder financeParentOrderCopy = copyFinanceParentOrder(newProduceTime, oldFinanceParentOrder);
                // 保存复制的账单
                financeOrderDao.save(financeParentOrderCopy);
            }
        }
    }

    /**
     * 计算往来状态
     *
     * @param financeParentOrder
     * @param oldFinanceParentOrder
     * @param financeSpecialOrder   @return
     */
    private Short processContactsStatus(FinanceParentOrder financeParentOrder, FinanceParentOrder oldFinanceParentOrder, FinanceSpecialOrder financeSpecialOrder) {
        Short contactsStatus = null;
        if (financeParentOrder != null && financeSpecialOrder != null) {
            String status = financeParentOrder.getStatus();
            // 退款账单才有往来状态
            if (FinanceSpecialOrder.REFUND_STATUS.equals(status)) {
                boolean innSettlement = financeSpecialOrder.getInnSettlement();
                if (!innSettlement) {
                    String produceTime = financeParentOrder.getProduceTime();
                    String settlementTime = oldFinanceParentOrder.getSettlementTime();
                    // 产生周期为本账期的退款：本期（平）；
                    if (produceTime.equals(settlementTime)) {
                        contactsStatus = FinanceSpecialOrder.CONTACTS_STATUS_THIS;
                    } else {
                        // 产生周期为下个账期（及以后）的退款：后期（挂）
                        contactsStatus = FinanceSpecialOrder.CONTACTS_STATUS_PAST;
                    }
                }
            }
        }
        return contactsStatus;
    }

    /**
     * 处理特殊账单对象，
     *
     * @param financeParentOrder    修改后的账单对象
     * @param oldFinanceParentOrder 数据库中的账单对象
     * @param financeSpecialOrder   修改后的特殊账单对象，主要处理对象
     */
    private void processSpecialOrder(FinanceParentOrder financeParentOrder, FinanceParentOrder oldFinanceParentOrder, FinanceSpecialOrder financeSpecialOrder) {
        // 处理正常账单部分的数据
        processParentOrder(financeParentOrder, oldFinanceParentOrder, financeSpecialOrder);
        Short contactsStatus = processContactsStatus(financeParentOrder, oldFinanceParentOrder, financeSpecialOrder);
        // 设置往来状态
        financeSpecialOrder.setContactsStatus(contactsStatus);
        // 保存新的聚合关系
        financeSpecialOrder.setFinanceParentOrder(oldFinanceParentOrder);
    }

    /**
     * 处理父账单对象
     *
     * @param newFinanceParentOrder 修改后的账单对象
     * @param oldFinanceParentOrder 数据库保存的原始账单对象
     */
    private void processParentOrder(FinanceParentOrder newFinanceParentOrder, FinanceParentOrder oldFinanceParentOrder, FinanceSpecialOrder financeSpecialOrder) {
        //获取番茄暂收金额
        BigDecimal fqTemp = newFinanceParentOrder.getFqTemp();
        if (null != fqTemp) {
            oldFinanceParentOrder.setFqTemp(fqTemp);
        }
        //获取修改原因
        String modifyReason = newFinanceParentOrder.getModifyReason();
        if (null != modifyReason) {
            oldFinanceParentOrder.setModifyReason(modifyReason);
        }
        // 获取修改后的订单状态
        String newStatus = newFinanceParentOrder.getStatus();
        if (newStatus == null) {
            throw new RuntimeException("订单状态不能为空");
        }
        String oldStatus = oldFinanceParentOrder.getStatus();
        if (!newStatus.equals(oldStatus)) {
            oldFinanceParentOrder.setStatus(newStatus);
        }
        // 获取价格策略
        Short priceStrategy = oldFinanceParentOrder.getPriceStrategy();
        if (priceStrategy == 1) {
            BigDecimal increaseRate = newFinanceParentOrder.getIncreaseRate();
            if (increaseRate == null) {
                increaseRate = BigDecimal.ZERO;
            }
            // 重新设置番茄加价比例
            oldFinanceParentOrder.setIncreaseRate(increaseRate);
        } else if (priceStrategy == 2 || priceStrategy == 3) {
            // 获取客栈分佣比例
            BigDecimal innCommissionRate = newFinanceParentOrder.getInnCommissionRate();
            if (innCommissionRate == null) {
                throw new RuntimeException("客栈分佣比例不能为空");
            }
            // 获取渠道分佣比例
            BigDecimal channelCommissionRatio = newFinanceParentOrder.getChannelCommissionRate();
            if (channelCommissionRatio == null) {
                throw new RuntimeException("渠道分佣比例不能为空");
            }
            // 重新设置新的渠道分佣比例
            oldFinanceParentOrder.setChannelCommissionRate(channelCommissionRatio);
            // 重新设置新的番茄分佣比例
            oldFinanceParentOrder.setFqCommissionRate(innCommissionRate.subtract(channelCommissionRatio));
        } else {
            throw new RuntimeException("客栈分佣比例不能为空");
        }

        Set<FinanceOrder> newChannelOrderList = newFinanceParentOrder.getChannelOrderList();
        if (CollectionsUtil.isEmpty(newChannelOrderList)) {
            throw new RuntimeException("子订单不能为空");
        }
        // 设置子订单中的属性
        processFinanceOrder(oldFinanceParentOrder.getChannelOrderList(), newChannelOrderList);
        // 获取订单总金额
        BigDecimal totalAmount = newFinanceParentOrder.getTotalAmount();
        if (totalAmount == null) {
            throw new RuntimeException("订单总金额不能为空");
        }
        // 设置产生周期
        oldFinanceParentOrder.setProduceTime(newFinanceParentOrder.getProduceTime());
        // 重新设置客栈订单总金额、分销商订单总金额、运营调价后的订单总金额（卖转底）
        buildOrderAmount(oldFinanceParentOrder);
        // 重新计算夜数、间夜数、提前预定天数、停留天数、房间数
        financeHelper.packFinanceOrderNewFields(oldFinanceParentOrder);
        // 重新计算渠道分佣金额
        buildChannelSettlementAmount(oldFinanceParentOrder, financeSpecialOrder);
        // 重新计算客栈的结算金额
        buildInnSettlementAmount(oldFinanceParentOrder, financeSpecialOrder);
        // 重新计算番茄的分佣金额
        buildFqSettlementAmount(oldFinanceParentOrder);
    }

    /**
     * 计算分销商结算金额
     *
     * @param oldFinanceParentOrder
     * @param financeSpecialOrder
     */
    private void buildChannelSettlementAmount(FinanceParentOrder oldFinanceParentOrder, FinanceSpecialOrder financeSpecialOrder) {
        String status = String.valueOf(oldFinanceParentOrder.getStatus());
        if (isSpecialStatus(status)) {
            BigDecimal channelSettlementAmount = null;
            // 如果是赔付账单
            if (status.equals(FinanceSpecialOrder.DEBIT_STATUS)) {
                channelSettlementAmount = financeSpecialOrder.getChannelDebit();
            } else if (status.equals(FinanceSpecialOrder.REFUND_STATUS)) { // 如果是退款账单
                channelSettlementAmount = financeSpecialOrder.getChannelRefund();
            } else if (status.equals(FinanceSpecialOrder.REPLENISHMENT_STATUS)) { // 如果是补款账单
                channelSettlementAmount = BigDecimal.ZERO;
            }
            oldFinanceParentOrder.setChannelSettlementAmount(channelSettlementAmount);
        } else {
            financeHelper.buildChannelSettlementAmount(oldFinanceParentOrder);
        }
    }

    /**
     * 计算客栈结算金额
     *
     * @param oldFinanceParentOrder
     * @param financeSpecialOrder
     */
    private void buildInnSettlementAmount(FinanceParentOrder oldFinanceParentOrder, FinanceSpecialOrder financeSpecialOrder) {
        String status = String.valueOf(oldFinanceParentOrder.getStatus());
        if (status.equals(FinanceSpecialOrder.DEBIT_STATUS)) {
            oldFinanceParentOrder.setInnSettlementAmount(financeSpecialOrder.getInnPayment());
        } else if (status.equals(FinanceSpecialOrder.REFUND_STATUS)) {
            oldFinanceParentOrder.setInnSettlementAmount(financeSpecialOrder.getInnRefund());
        } else {
            financeHelper.buildInnSettlementAmount(oldFinanceParentOrder);
        }
    }

    /**
     * 计算番茄结算金额
     *
     * @param oldFinanceParentOrder
     */
    private void buildFqSettlementAmount(FinanceParentOrder oldFinanceParentOrder) {
        String status = String.valueOf(oldFinanceParentOrder.getStatus());
        // 如果是特殊账单，不计算佣金
        if (isSpecialStatus(status)) {
            oldFinanceParentOrder.setFqSettlementAmount(BigDecimal.ZERO);
        } else {
            financeHelper.buildFqSettlementAmount(oldFinanceParentOrder);
        }
    }

    /**
     * 拷贝前端传递的子订单中的属性到数据库中的子订单对象
     *
     * @param oldChannelOrderList 数据库中的旧订单对象
     * @param newChannelOrderList 前端传递的修改后的子订单对象
     */
    private void processFinanceOrder(Set<FinanceOrder> oldChannelOrderList, Set<FinanceOrder> newChannelOrderList) {
        for (FinanceOrder newFinanceOrder : newChannelOrderList) {
            if (CollectionsUtil.isNotEmpty(oldChannelOrderList)) {
                for (FinanceOrder oldFinanceOrder : oldChannelOrderList) {
                    if (newFinanceOrder.getId().equals(oldFinanceOrder.getId())) {
                        Integer roomTypeNums = newFinanceOrder.getRoomTypeNums();
                        if (roomTypeNums != null) {
                            // 修改房间数量
                            oldFinanceOrder.setRoomTypeNums(roomTypeNums);
                        }
                        BigDecimal bookPrice = newFinanceOrder.getBookPrice();
                        if (bookPrice != null) {
                            // 修改分销商单价
                            oldFinanceOrder.setBookPrice(bookPrice);
                        }
                        BigDecimal innAmount = newFinanceOrder.getInnAmount();
                        if (innAmount != null) {
                            oldFinanceOrder.setInnAmount(innAmount);
                        }
                        String checkInAtStr = newFinanceOrder.getCheckInAtStr();
                        if (StringUtils.isBlank(checkInAtStr)) {
                            throw new RuntimeException("入住时间不能为空");
                        }
                        Date checkInAt = DateUtil.parse(checkInAtStr);
                        // 修改入住日期
                        oldFinanceOrder.setCheckInAt(checkInAt);
                        String checkOutAtStr = newFinanceOrder.getCheckOutAtStr();
                        if (StringUtils.isBlank(checkOutAtStr)) {
                            throw new RuntimeException("退房时间不能为空");
                        }
                        Date checkOutAt = DateUtil.parse(checkOutAtStr);
                        // 修改退房日期
                        oldFinanceOrder.setCheckOutAt(checkOutAt);
                        // 重新计算夜数
                        oldFinanceOrder.setDeleted(newFinanceOrder.getDeleted());

                        // 重新计算夜数
                        oldFinanceOrder.setNights(DateUtil.getDifferDay(checkInAt, checkOutAt));
                    }
                }
            }
        }
    }

    /**
     * 复制特殊账单，包括聚合的父账单对象，付账单聚合的子账单对象
     *
     * @param newProduceTime         修改后的账单产生周期
     * @param oldFinanceSpecialOrder 数据库中保存的特殊账单对象
     * @return
     */
    private FinanceSpecialOrder copyFinanceSpecialOrder(String newProduceTime, FinanceSpecialOrder oldFinanceSpecialOrder) {
        // 检查是否可以进行复制操作
        FinanceParentOrder oldFinanceParentOrder = oldFinanceSpecialOrder.getFinanceParentOrder();
        checkCopy(oldFinanceParentOrder);
        // 设置原账单的产生周期为修改后的产生周期
        oldFinanceParentOrder.setProduceTime(newProduceTime);
        // 用于保存修改后的账单对象
        FinanceSpecialOrder financeSpecialOrderCopy = new FinanceSpecialOrder();
        BeanUtil.copy(oldFinanceSpecialOrder, financeSpecialOrderCopy);
        FinanceParentOrder financeParentOrderCopy = financeSpecialOrderCopy.getFinanceParentOrder();
        // 设置特殊账单的ID
        financeSpecialOrderCopy.setId(null);
        // 设置复制账单的账期为产生周期
        financeParentOrderCopy.setSettlementTime(newProduceTime);
        financeParentOrderCopy.setProduceTime(newProduceTime);
        // 设置父账单对象的ID，包括子账单对象的ID
        generateFinanceOrderId(financeParentOrderCopy);
        financeSpecialOrderCopy.setContactsStatus(FinanceSpecialOrder.CONTACTS_STATUS_THIS);
        return financeSpecialOrderCopy;
    }

    /**
     * 复制父账单，包括聚合的子账单对象
     *
     * @param newProduceTime        新的产生周期
     * @param oldFinanceParentOrder 数据库中保存的账单对象
     * @return
     */
    private FinanceParentOrder copyFinanceParentOrder(String newProduceTime, FinanceParentOrder oldFinanceParentOrder) {

        checkCopy(oldFinanceParentOrder);
        // 设置原账单的产生周期为修改后的产生周期
        oldFinanceParentOrder.setProduceTime(newProduceTime);
        FinanceParentOrder financeParentOrderCopy = new FinanceParentOrder();
        BeanUtil.copy(oldFinanceParentOrder, financeParentOrderCopy);
        generateFinanceOrderId(financeParentOrderCopy);
        // 设置复制账单的账期为产生周期
        financeParentOrderCopy.setSettlementTime(newProduceTime);
        financeParentOrderCopy.setProduceTime(newProduceTime);
        // 设置结算状态为未结算
        financeParentOrderCopy.setSettlementStatus("0");
        // 设置核单状态为未核
        financeParentOrderCopy.setAuditStatus("0");
        return financeParentOrderCopy;
    }

    /**
     * 重新生成父账单ID以及子账单ID
     *
     * @param financeParentOrder
     */
    private void generateFinanceOrderId(FinanceParentOrder financeParentOrder) {
        financeParentOrder.setId(financeParentOrder.getId() + "_1");
        Set<FinanceOrder> channelOrderList = financeParentOrder.getChannelOrderList();
        if (CollectionsUtil.isNotEmpty(channelOrderList)) {
            for (FinanceOrder financeOrder : channelOrderList) {
                financeOrder.setId(financeOrder.getId() + "_1");
            }
        }
    }

    /**
     * 根据修改后的子订单，重新计算分销商订单金额、客栈订单金额、加减价总额
     *
     * @param financeParentOrder
     * @return
     */
    private void buildOrderAmount(FinanceParentOrder financeParentOrder) {
        Set<FinanceOrder> channelOrderList = financeParentOrder.getChannelOrderList();
        BigDecimal totalAmount = BigDecimal.ZERO;
        BigDecimal innAmount = BigDecimal.ZERO;
        BigDecimal extraPrice = BigDecimal.ZERO;
        BigDecimal channelAmount = BigDecimal.ZERO;
        if (!CollectionsUtil.isEmpty(channelOrderList)) {
            for (FinanceOrder financeOrder : channelOrderList) {
                // 获取分销商单价
                BigDecimal bookPrice = financeOrder.getBookPrice();
                // 获取客栈单价
                BigDecimal childInnAmount = financeOrder.getInnAmount();
                // 获取夜数
                Integer nights = financeOrder.getNights();
                // 获取间数
                Integer roomTypeNums = financeOrder.getRoomTypeNums();
                BigDecimal childExtraPrice = NumberUtil.wrapNull(financeOrder.getExtraPrice());
                BigDecimal childChannelAmount = NumberUtil.wrapNull(financeOrder.getChannelAmount());
                // 获取间夜数
                BigDecimal roomNights = new BigDecimal(nights.longValue()).multiply(new BigDecimal(roomTypeNums.longValue()));
                boolean deleted = financeOrder.getDeleted();
                if (!deleted) {
                    totalAmount = totalAmount.add(bookPrice.multiply(roomNights));
                    innAmount = innAmount.add(childInnAmount.multiply(roomNights));
                    extraPrice = extraPrice.add(childExtraPrice);
                    channelAmount = channelAmount.add(childChannelAmount);
                }
            }
        }
        financeParentOrder.setInnAmount(innAmount);
        financeParentOrder.setTotalAmount(totalAmount);
        financeParentOrder.setExtraPrice(extraPrice);
        financeParentOrder.setChannelAmount(channelAmount);
    }

    /**
     * 检查账单是否可以复制
     *
     * @param oldFinanceParentOrder
     */
    private void checkCopy(FinanceParentOrder oldFinanceParentOrder) {
        List<FinanceParentOrder> financeParentOrders = financeOrderDao.selectFinanceParentOrderListById(oldFinanceParentOrder.getId());
        if (CollectionsUtil.isNotEmpty(financeParentOrders)) {
            throw new RuntimeException("该账单已复制成功，请勿重复再次操作");
        }
    }

    /**
     * 判断账单状态是否属于特殊账单
     *
     * @param status
     * @return
     */
    private static boolean isSpecialStatus(String status) {
        List<String> specialStatus = Arrays.asList(FinanceSpecialOrder.SPECIAL_STATUS);
        if (specialStatus.contains(status)) {
            return true;
        }
        return false;
    }

    /**
     * 判断修改账单的操作是否需要复制账单
     *
     * @param financeParentOrder    前台修改后的账单对象
     * @param oldFinanceParentOrder 数据库中的账单对象
     * @return
     */
    private boolean isNeedCopyBill(FinanceParentOrder financeParentOrder, FinanceParentOrder oldFinanceParentOrder) {
        String newProduceTime = financeParentOrder.getProduceTime();
        String oldProduceTime = oldFinanceParentOrder.getProduceTime();
        if (!newProduceTime.equals(oldProduceTime)) {
            return true;
        }
        return false;
    }

    /**
     * 构造修改账单的操作记录对象
     *
     * @param oldFinanceParentOrder 数据库中原有的账单对象
     * @param modifyReason          修改原因
     * @return
     */
    private void saveUpdateOrderLog(FinanceParentOrder oldFinanceParentOrder, String modifyReason) {
        FinanceOperationLog financeOperationLog = new FinanceOperationLog();
        financeOperationLog.setOperateUser(financeHelper.getCurrentUser());
        financeOperationLog.setSettlementTime(oldFinanceParentOrder.getSettlementTime());
        financeOperationLog.setOperateType("8");
        StringBuilder operateContent = new StringBuilder();
        operateContent.append("账期【");
        operateContent.append(oldFinanceParentOrder.getSettlementTime());
        operateContent.append("]");
        operateContent.append("修改订单【");
        operateContent.append(oldFinanceParentOrder.getChannelOrderNo());
        operateContent.append("】");
        operateContent.append(",分销商【");
        operateContent.append(oldFinanceParentOrder.getChannelName());
        operateContent.append("】,");
        operateContent.append("客栈【");
        operateContent.append(oldFinanceParentOrder.getInnName());
        operateContent.append("(");
        operateContent.append(oldFinanceParentOrder.getInnId());
        operateContent.append(")】");
        operateContent.append("，修改原因：");
        operateContent.append(modifyReason);
        financeOperationLog.setOperateContent(operateContent.toString());
        financeOperationLogDao.save(financeOperationLog);
    }

    @Override
    public List<Map<String, Object>> findFinanceChannelSettlement(String settlementTime) {
        List<Map<String, Object>> dataMapList = financeOrderDao.getFinanceChannelSettlement(settlementTime, false);
        Collection<ChannelSettlementData> channelSettlementDataList = financeHelper.combineMap(dataMapList);
        List<Map<String, Object>> resultData = new ArrayList<>();
        if (CollectionsUtil.isNotEmpty(channelSettlementDataList)) {
            for (ChannelSettlementData data : channelSettlementDataList) {
                Map<String, Object> dataMap = new HashMap<>();
                // 获取渠道ID
                Integer channelId = data.getChannelId();
                String auditStatus = "0";
                FinanceChannelSettlement financeChannelSettlement = financeChannelSettlementDao.selectFinanceChannelSettlement(channelId, settlementTime);
                if (financeChannelSettlement != null) {
                    auditStatus = financeChannelSettlement.getAuditStatus();
                }
                dataMap.put("id", data.getChannelId());
                dataMap.put("name", data.getChannelName());
                dataMap.put("auditStatus", auditStatus);
                // 设置分销商的订单总数
                dataMap.put("total", data.getOrderAmount());
                // 设置分销商账单总额
                dataMap.put("channel", data.getChannelOrderAmount());
                // 设置分销商结算金额
                // 正常账单中分销商结算总额-退款账单中分销商结算总额-赔付账单中分销商结算总额
                BigDecimal amount = data.getChannelSettlementAmount();
                // -退款账单中分销商结算总额
                amount = amount.subtract(data.getChannelRefund());
                // -赔付账单中分销商结算总额
                amount = amount.subtract(data.getChannelDebit());
                dataMap.put("amount", amount);
                // 设置客栈订单总额
                dataMap.put("orders", data.getInnOrderAmount());
                // 设置客栈结算金额
                // 正常账单中客栈结算总额-退款账单中客栈结算总额-赔付账单中客栈结算总额+补款账单中客栈结算总额
                BigDecimal inn = data.getInnSettlementAmount().add(data.getFqReplenishment());
                inn = inn.subtract(data.getInnPayment());
                inn = inn.subtract(data.getInnRefund());
                dataMap.put("inn", inn);
                resultData.add(dataMap);
            }
        }
        return resultData;
    }

    @Override
    public Map<String, Object> findTotalChannelSettlement(String settlementTime) {
        List<Map<String, Object>> dataMapList = financeOrderDao.getFinanceChannelSettlement(settlementTime, true);
        Collection<ChannelSettlementData> channelSettlementDataList = financeHelper.combineMap(dataMapList);
        Map<String, Object> resultData = new HashMap<>();
        if (CollectionsUtil.isNotEmpty(channelSettlementDataList)) {
            // 订单总数
            int total = 0;
            // 分销商订单总额为
            BigDecimal channel = BigDecimal.ZERO;
            // 分销商结算金额
            BigDecimal amount = BigDecimal.ZERO;
            // 客栈订单总额为
            BigDecimal orders = BigDecimal.ZERO;
            // 客栈结算金额
            BigDecimal inn = BigDecimal.ZERO;
            // 退款账单中分销商结算总额
            BigDecimal channelRefund = BigDecimal.ZERO;
            // 赔付账单中分销商结算总额
            BigDecimal channelDebit = BigDecimal.ZERO;
            // 退款账单中客栈结算总额
            BigDecimal innRefund = BigDecimal.ZERO;
            // 赔付账单中客栈结算总额
            BigDecimal innPayment = BigDecimal.ZERO;
            // 补款账单中客栈结算总额
            BigDecimal fqReplenishment = BigDecimal.ZERO;

            for (ChannelSettlementData data : channelSettlementDataList) {
                total += data.getOrderAmount();
                channel = channel.add(data.getChannelOrderAmount());
                amount = amount.add(data.getChannelSettlementAmount());
                orders = orders.add(data.getInnOrderAmount());
                inn = inn.add(data.getInnSettlementAmount());
                channelRefund = channelRefund.add(data.getChannelRefund());
                channelDebit = channelDebit.add(data.getChannelDebit());
                innRefund = innRefund.add(data.getInnRefund());
                innPayment = innPayment.add(data.getInnPayment());
                fqReplenishment = fqReplenishment.add(data.getFqReplenishment());
            }
            // 正常账单中分销商结算总额-退款账单中分销商结算总额-赔付账单中分销商结算总额
            amount = amount.subtract(channelRefund);
            amount = amount.subtract(channelDebit);
            // 正常账单中客栈结算总额-退款账单中客栈结算总额-赔付账单中客栈结算总额+补款账单中客栈结算总额
            inn = inn.subtract(innRefund);
            inn = inn.subtract(innPayment);
            inn = inn.add(fqReplenishment);

            resultData.put("total", total);
            resultData.put("channel", channel);
            resultData.put("amount", amount);
            resultData.put("orders", orders);
            resultData.put("inn", inn);
        }
        return resultData;
    }

    @Override
    public Map<String, Object> findTotalChannelOrder(Integer channelId, String settlementTime, Integer status) {
        List<Map<String, Object>> data = financeOrderDao.selectTotalChannelSettlement(channelId, settlementTime, status);
        if (CollectionsUtil.isNotEmpty(data)) {
            return data.get(0);
        }
        return null;
    }

    @Override
    public Page<FinanceParentOrder> findFinanceParentOrder(Page<FinanceParentOrder> page, Integer channelId, String settlementTime, String auditStatus, Short priceStrategy, Short isBalance, String keyWord, Integer orderStatus) {
        return financeOrderDao.selectFinanceParentOrder(page, channelId, settlementTime, auditStatus, priceStrategy, isBalance, keyWord, orderStatus);
    }

    @Override
    public Page<FinanceSpecialOrder> findSpecialOrderList(Page<FinanceSpecialOrder> page, String statusKey, Integer channelId, String settlementTime, String auditStatus, Short priceStrategy, Short isBalance, String keyWord) {
        return financeSpecialOrderDao.selectSpecialOrderList(page, statusKey, channelId, settlementTime, auditStatus, priceStrategy, isBalance, keyWord);
    }


    @Override
    public String getExistOrderStatus(Integer channelId, String settlementTime, String auditStatus) {
        if (StringUtils.isNoneBlank(auditStatus)) {
            return financeOrderDao.selectExistOrderStatus(channelId, settlementTime, auditStatus);
        }
        return null;
    }

}
