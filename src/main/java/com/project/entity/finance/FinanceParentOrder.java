package com.project.entity.finance;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.project.bean.serializer.JsonDateTimeSerializer;
import com.project.utils.CollectionsUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.*;

/**
 * 主订单对象
 * Created by 番茄桑 on 2015/8/31.
 */
@Entity
@Table(name = "finance_parent_order")
public class FinanceParentOrder {
    // 账单状态，已接受(已分房)
    @Transient
    public static final String STATUS_ACCEPTED = "1";
    // 主键ID
    @Id
    private String id;
    // 下单人姓名
    @Column(name = "user_name")
    private String userName;
    // 下单人联系电话
    @Column(name = "contact")
    private String contact;
    // 渠道来源标示
    @Column(name = "channel_id")
    private Short channelId;
    // 渠道订单编号
    @Column(name = "channel_order_no")
    private String channelOrderNo;
    // 渠道价格策略(1:底价 2:卖价, 3:卖转底)
    @Column(name = "channel_price_policy")
    private Short channelPricePolicy;
    // 渠道上浮比例
    @Column(name = "channel_up_ratio")
    private BigDecimal channelUpRatio;
    // 渠道分佣比例
    @Column(name = "channel_commission_ratio")
    private BigDecimal channelCommissionRatio;
    // 客栈价格策略(1:底价 2:卖价 3:底价+卖价)
    @Column(name = "inn_price_policy")
    private Short innPricePolicy;
    // 客栈卖价时抽佣比例
    @Column(name = "inn_commission_ratio")
    private BigDecimal innCommissionRatio;
    // 分销商订单总金额
    @Column(name = "total_amount")
    private BigDecimal totalAmount;
    // 订单已付金额
    @Column(name = "paid_amount")
    private BigDecimal paidAmount;
    // 订单已付押金
    @Column(name = "paid_payment")
    private BigDecimal paidPayment;
    // 操作人
    @Column(name = "operated_user")
    private String operatedUser;
    // 下单时间
    @JsonSerialize(using = JsonDateTimeSerializer.class)
    @Column(name = "order_time")
    private Date orderTime;
    // 支付时间
    @Column(name = "pay_time")
    private Date payTime;
    // 结算时间
    @Column(name = "balance_time")
    private Date balanceTime;
    // 是否结算（0:未结算,1:已结算）
    @Column(name = "is_balance")
    private Short isBalance;
    // 状态（0:未处理、1:已接受（已分房）、2:已拒绝、3:已取消、4:验证失败、5：已接受（未分房）66:赔付、77:退款、88：补款）
    @Column(name = "status")
    private String status;
    // 关联客栈id
    @Column(name = "inn_id")
    private Integer innId;
    // 分销渠道来源标示
    @Column(name = "fx_channel_id")
    private Short fxChannelId;
    // 渠道开通编号(客栈)
    @Column(name = "account_id")
    private Integer accountId;
    // 备注
    @Column(name = "remark")
    private String remark;
    // 支付类型，prepay(预付)、assure(担保)
    @Column(name = "pay_type")
    private String payType;
    // 客栈名称
    @Column(name = "inn_name")
    private String innName;
    // 渠道名称
    @Column(name = "channel_name")
    private String channelName;
    // 价格策略(1:底价 2:卖价)
    @Column(name = "price_strategy")
    private Short priceStrategy;
    // 底价模式番茄加价比例
    @Column(name = "increase_rate")
    private BigDecimal increaseRate;
    // 卖价时渠道佣金比例
    @Column(name = "channel_commission_rate")
    private BigDecimal channelCommissionRate;
    // 卖价时番茄佣金比例
    @Column(name = "fq_commission_rate")
    private BigDecimal fqCommissionRate;
    // 卖价时客栈总抽佣比例
    @Transient
    private BigDecimal innCommissionRate;
    // 费用类型(1:房费，2:违约金)
    @Column(name = "cost_type")
    private Short costType = 1;
    // 核单状态
    @Column(name = "audit_status")
    private String auditStatus = "0";
    // 渠道商款项是否收到
    @Column(name = "is_arrival")
    private boolean isArrival;
    @Column(name = "settlement_time")
    private String settlementTime;
    // 渠道商结算金额
    @Column(name = "channel_settlement_amount")
    private BigDecimal channelSettlementAmount;
    // 番茄结算金额
    @Column(name = "fq_settlement_amount")
    private BigDecimal fqSettlementAmount;
    // 客栈结算金额
    @Column(name = "inn_settlement_amount")
    private BigDecimal innSettlementAmount;
    // 关联主订单集合
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER, mappedBy = "financeParentOrder")
    private Set<FinanceOrder> channelOrderList = new HashSet<>();
    // 封装后的房型展示
    @Transient
    private String channelRoomTypeName;
    // 封装后的入住/退房时间
    @Transient
    private String checkDate;
    // 订单模式（对外使用，展示给客栈老板）
    @Transient
    private String orderMode;
    // 订单模式的内部名称（内部使用，展示给营销人员）
    @Transient
    private String innerOrderMode;
    // 核单状态展示字段
    @Transient
    private String auditStatusStr;
    // 是否结算展示字段
    @Transient
    private String isBalanceStr;
    // 渠道商款项是否到账展示字段
    @Transient
    private String isArrivalStr;
    // 入住日期
    @Transient
    private Date checkInAt;
    // 退房日期
    @Transient
    private Date checkOutAt;
    // 房间数
    @Transient
    private Integer roomTypeNums;
    // 间夜数
    @Column(name = "room_nights")
    private Integer roomNights;
    //房间数
    @Column(name = "rooms")
    private Integer rooms;
    //夜数
    @Column(name = "nights")
    private Integer nights;
    @Transient
    private String orderStatusStr;
    //提前预定天数
    @Column(name = "reservation_days")
    private Integer reservationDays;
    //停留天数
    @Column(name = "stay_days")
    private Integer stayDays;
    // 渠道商总金额
    @Column(name = "channel_amount")
    private BigDecimal channelAmount;
    // 番茄加减价金额
    @Column(name = "extra_price")
    private BigDecimal extraPrice;
    // OMS订单号
    @Column(name = "order_no")
    private String orderNo;
    // 客栈订单价格（PMS订单价格）
    @Column(name = "inn_amount")
    private BigDecimal innAmount;
    // 产生时间(默认等于settlement_time)
    @Column(name = "produce_time")
    private String produceTime;
    // 结算状态（0:未结算，1:已结算）
    @Column(name = "settlement_status")
    private String settlementStatus;
    // 分销商代码
    @Column(name = "channel_code")
    private String channelCode;
    //番茄暂收金额
    @Column(name = "fq_temp")
    private BigDecimal fqTemp;
    //修改原因
    @Column(name = "modify_reason")
    private String modifyReason;

    public String getModifyReason() {
        return modifyReason;
    }

    public void setModifyReason(String modifyReason) {
        this.modifyReason = modifyReason;
    }

    public BigDecimal getFqTemp() {
        return fqTemp;
    }

    public void setFqTemp(BigDecimal fqTemp) {
        this.fqTemp = fqTemp;
    }

    public String getChannelCode() {
        return channelCode;
    }

    public void setChannelCode(String channelCode) {
        this.channelCode = channelCode;
    }

    public String getSettlementStatus() {
        return settlementStatus;
    }

    public void setSettlementStatus(String settlementStatus) {
        this.settlementStatus = settlementStatus;
    }

    public String getIsBalanceStr() {
        String settlementStatus = this.getSettlementStatus();
        if (settlementStatus != null) {
            String isBalanceTemp;
            if ("0".equals(settlementStatus)) {
                isBalanceTemp = "未结算";
            } else if ("1".equals(settlementStatus)) {
                isBalanceTemp = "已结算";
            } else {
                isBalanceTemp = "异常状态";
            }
            return isBalanceTemp;
        }
        return isBalanceStr;
    }

    public void setIsBalanceStr(String isBalanceStr) {
        this.isBalanceStr = isBalanceStr;
    }

    public String getProduceTime() {
        return produceTime;
    }

    public void setProduceTime(String produceTime) {
        this.produceTime = produceTime;
    }

    public BigDecimal getInnAmount() {
        return innAmount;
    }

    public void setInnAmount(BigDecimal innAmount) {
        this.innAmount = innAmount;
    }

    public BigDecimal getChannelAmount() {
        return channelAmount;
    }

    public void setChannelAmount(BigDecimal channelAmount) {
        this.channelAmount = channelAmount;
    }

    public BigDecimal getExtraPrice() {
        return extraPrice;
    }

    public void setExtraPrice(BigDecimal extraPrice) {
        this.extraPrice = extraPrice;
    }

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    public String getInnerOrderMode() {
        Short priceStrategy = this.getPriceStrategy();
        if (priceStrategy != null) {
            String priceStrategyStr = "暂无";
            if (priceStrategy == 1) {
                priceStrategyStr = "精品(活动)";
            } else if (priceStrategy == 2) {
                priceStrategyStr = "普通(卖)";
            } else if (priceStrategy == 3) {
                priceStrategyStr = "普通(底)";
            }
            return priceStrategyStr;
        }
        return innerOrderMode;
    }

    public void setInnerOrderMode(String innerOrderMode) {
        this.innerOrderMode = innerOrderMode;
    }

    public BigDecimal getInnCommissionRate() {
        BigDecimal fqCommissionRate = getFqCommissionRate();
        BigDecimal channelCommissionRate = getChannelCommissionRate();
        if (fqCommissionRate != null && channelCommissionRate != null) {
            return fqCommissionRate.add(channelCommissionRate);
        }
        return innCommissionRate;
    }

    public void setInnCommissionRate(BigDecimal innCommissionRate) {
        this.innCommissionRate = innCommissionRate;
    }

    public String getOrderStatusStr() {
        String status = this.getStatus();
        if (status != null) {
            String orderStatusStrTemp;
            // 0:未处理、1:已接受（已分房）、2:已拒绝、3:已取消、4:验证失败、5：已接受（未分房））
            if ("0".equals(status)) {
                orderStatusStrTemp = "未处理";
            } else if (STATUS_ACCEPTED.equals(status)) {
                orderStatusStrTemp = "已接受(已分房)";
            } else if ("2".equals(status)) {
                orderStatusStrTemp = "已拒绝";
            } else if ("3".equals(status)) {
                orderStatusStrTemp = "已取消";
            } else if ("4".equals(status)) {
                orderStatusStrTemp = "验证失败";
            } else if ("5".equals(status)) {
                orderStatusStrTemp = "已接受(未分房)";
            } else if (FinanceSpecialOrder.DEBIT_STATUS.equals(status)) {
                orderStatusStrTemp = "赔付";
            } else if (FinanceSpecialOrder.REFUND_STATUS.equals(status)) {
                orderStatusStrTemp = "退款";
            } else if (FinanceSpecialOrder.REPLENISHMENT_STATUS.equals(status)) {
                orderStatusStrTemp = "补款";
            } else {
                orderStatusStrTemp = "异常状态";
            }
            return orderStatusStrTemp;
        }
        return orderStatusStr;
    }

    public void setOrderStatusStr(String orderStatusStr) {
        this.orderStatusStr = orderStatusStr;
    }

    public Integer getRoomTypeNums() {
        Set<FinanceOrder> channelOrderList = this.getChannelOrderList();
        int num = 0;
        if (!CollectionsUtil.isEmpty(channelOrderList)) {
            for (FinanceOrder financeOrder : channelOrderList) {
                num += financeOrder.getRoomTypeNums();
            }
            return num;
        }
        return roomTypeNums;
    }

    public void setRoomTypeNums(Integer roomTypeNums) {
        this.roomTypeNums = roomTypeNums;
    }

    public Integer getRoomNights() {
        return roomNights;
    }

    public void setRoomNights(Integer roomNights) {
        this.roomNights = roomNights;
    }

    public Date getCheckInAt() {
        Set<FinanceOrder> channelOrderList = this.getChannelOrderList();
        if (!CollectionsUtil.isEmpty(channelOrderList)) {
            // 获取子订单集合
            List<FinanceOrder> financeOrders = new ArrayList<>(channelOrderList);
            clearFinanceOrderList(financeOrders);
            // 第一次排序，获得入住日期
            Collections.sort(financeOrders, new Comparator() {
                @Override
                public int compare(Object object1, Object object2) {
                    return ((FinanceOrder) object1).getCheckInAt().compareTo(((FinanceOrder) object2).getCheckInAt());
                }
            });
            return financeOrders.get(0).getCheckInAt();
        }
        return checkInAt;
    }

    public void setCheckInAt(Date checkInAt) {
        this.checkInAt = checkInAt;
    }

    public Date getCheckOutAt() {
        Set<FinanceOrder> channelOrderList = this.getChannelOrderList();
        if (!CollectionsUtil.isEmpty(channelOrderList)) {
            // 获取子订单集合
            List<FinanceOrder> financeOrders = new ArrayList<>(channelOrderList);
            clearFinanceOrderList(financeOrders);
            Collections.sort(financeOrders, new Comparator() {
                @Override
                public int compare(Object object1, Object object2) {
                    return ((FinanceOrder) object1).getCheckOutAt().compareTo(((FinanceOrder) object2).getCheckOutAt());
                }
            });
            return financeOrders.get(financeOrders.size() - 1).getCheckOutAt();
        }
        return checkOutAt;
    }

    private void clearFinanceOrderList(List<FinanceOrder> financeOrders) {
        for (Iterator<FinanceOrder> iterator = financeOrders.iterator(); iterator.hasNext(); ) {
            FinanceOrder financeOrder = iterator.next();
            if (financeOrder.getDeleted()) {
                iterator.remove();
            }
        }
    }

    public void setCheckOutAt(Date checkOutAt) {
        this.checkOutAt = checkOutAt;
    }

    public String getIsArrivalStr() {
        return this.isArrival ? "已收到" : "未收到";
    }

    public void setIsArrivalStr(String isArrivalStr) {
        this.isArrivalStr = isArrivalStr;
    }

    public String getAuditStatusStr() {
        String auditStatus = this.getAuditStatus();
        if (StringUtils.isNotBlank(auditStatus)) {
            String auditStatusTemp;
            if ("1".equals(auditStatus)) {
                auditStatusTemp = "已核成功";
            } else if ("2".equals(auditStatus)) {
                auditStatusTemp = "已核失败";
            } else {
                auditStatusTemp = "未核";
            }
            return auditStatusTemp;
        }
        return auditStatusStr;
    }

    public void setAuditStatusStr(String auditStatusStr) {
        this.auditStatusStr = auditStatusStr;
    }

    public String getOrderMode() {
        Short priceStrategy = this.getPriceStrategy();
        if (priceStrategy != null) {
            String priceStrategyStr = "暂无";
            if (priceStrategy == 1) {
                priceStrategyStr = "精品代销";
            } else if (priceStrategy == 2) {
                priceStrategyStr = "普通代销";
            } else if (priceStrategy == 3) {
                priceStrategyStr = "普通代销";
            }
            return priceStrategyStr;
        }
        return orderMode;
    }

    public void setOrderMode(String orderMode) {
        this.orderMode = orderMode;
    }

    public String getCheckDate() {
        Set<FinanceOrder> channelOrderList = this.getChannelOrderList();
        if (!CollectionsUtil.isEmpty(channelOrderList)) {
            // 获取子订单集合
            List<FinanceOrder> financeOrders = new ArrayList<>(channelOrderList);
            clearFinanceOrderList(financeOrders);
            // 第一次排序，获得入住日期
            Collections.sort(financeOrders, new Comparator() {
                @Override
                public int compare(Object object1, Object object2) {
                    return ((FinanceOrder) object1).getCheckInAt().compareTo(((FinanceOrder) object2).getCheckInAt());
                }
            });
            FinanceOrder beginFinanceOrder = financeOrders.get(0);
            String checkInAt = DateFormatUtils.format(beginFinanceOrder.getCheckInAt(), "yyyy.MM.dd");
            Collections.sort(financeOrders, new Comparator() {
                @Override
                public int compare(Object object1, Object object2) {
                    return ((FinanceOrder) object1).getCheckOutAt().compareTo(((FinanceOrder) object2).getCheckOutAt());
                }
            });
            FinanceOrder endFinanceOrder = financeOrders.get(financeOrders.size() - 1);
            String checkOutAt = DateFormatUtils.format(endFinanceOrder.getCheckOutAt(), "yyyy.MM.dd");
            return checkInAt + "—" + checkOutAt;
        }
        return checkDate;
    }

    public void setCheckDate(String checkDate) {
        this.checkDate = checkDate;
    }

    public String getChannelRoomTypeName() {
        Set<FinanceOrder> channelOrderList = this.getChannelOrderList();
        if (!CollectionsUtil.isEmpty(channelOrderList)) {
            Set<String> roomType = new HashSet<>();
            for (FinanceOrder financeOrder : channelOrderList) {
                roomType.add(financeOrder.getChannelRoomTypeName());
            }
            return roomType.toString().replaceAll("\\[", "").replaceAll("\\]", "");
        }
        return channelRoomTypeName;
    }

    public void setChannelRoomTypeName(String channelRoomTypeName) {
        this.channelRoomTypeName = channelRoomTypeName;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public Short getChannelId() {
        return channelId;
    }

    public void setChannelId(Short channelId) {
        this.channelId = channelId;
    }

    public String getChannelOrderNo() {
        return channelOrderNo;
    }

    public void setChannelOrderNo(String channelOrderNo) {
        this.channelOrderNo = channelOrderNo;
    }

    public Short getChannelPricePolicy() {
        return channelPricePolicy;
    }

    public void setChannelPricePolicy(Short channelPricePolicy) {
        this.channelPricePolicy = channelPricePolicy;
    }

    public BigDecimal getChannelUpRatio() {
        return channelUpRatio;
    }

    public void setChannelUpRatio(BigDecimal channelUpRatio) {
        this.channelUpRatio = channelUpRatio;
    }

    public BigDecimal getChannelCommissionRatio() {
        return channelCommissionRatio;
    }

    public void setChannelCommissionRatio(BigDecimal channelCommissionRatio) {
        this.channelCommissionRatio = channelCommissionRatio;
    }

    public Short getInnPricePolicy() {
        return innPricePolicy;
    }

    public void setInnPricePolicy(Short innPricePolicy) {
        this.innPricePolicy = innPricePolicy;
    }

    public BigDecimal getInnCommissionRatio() {
        return innCommissionRatio;
    }

    public void setInnCommissionRatio(BigDecimal innCommissionRatio) {
        this.innCommissionRatio = innCommissionRatio;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public BigDecimal getPaidAmount() {
        return paidAmount;
    }

    public void setPaidAmount(BigDecimal paidAmount) {
        this.paidAmount = paidAmount;
    }

    public BigDecimal getPaidPayment() {
        return paidPayment;
    }

    public void setPaidPayment(BigDecimal paidPayment) {
        this.paidPayment = paidPayment;
    }

    public String getOperatedUser() {
        return operatedUser;
    }

    public void setOperatedUser(String operatedUser) {
        this.operatedUser = operatedUser;
    }

    public Date getOrderTime() {
        return orderTime;
    }

    public void setOrderTime(Date orderTime) {
        this.orderTime = orderTime;
    }

    public Date getPayTime() {
        return payTime;
    }

    public void setPayTime(Date payTime) {
        this.payTime = payTime;
    }

    public Date getBalanceTime() {
        return balanceTime;
    }

    public void setBalanceTime(Date balanceTime) {
        this.balanceTime = balanceTime;
    }

    public Short getIsBalance() {
        return isBalance;
    }

    public void setIsBalance(Short isBalance) {
        this.isBalance = isBalance;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Integer getInnId() {
        return innId;
    }

    public void setInnId(Integer innId) {
        this.innId = innId;
    }

    public Short getFxChannelId() {
        return fxChannelId;
    }

    public void setFxChannelId(Short fxChannelId) {
        this.fxChannelId = fxChannelId;
    }

    public Integer getAccountId() {
        return accountId;
    }

    public void setAccountId(Integer accountId) {
        this.accountId = accountId;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getPayType() {
        return payType;
    }

    public void setPayType(String payType) {
        this.payType = payType;
    }

    public String getInnName() {
        return innName;
    }

    public void setInnName(String innName) {
        this.innName = innName;
    }

    public String getChannelName() {
        return channelName;
    }

    public void setChannelName(String channelName) {
        this.channelName = channelName;
    }

    public Short getPriceStrategy() {
        return priceStrategy;
    }

    public void setPriceStrategy(Short priceStrategy) {
        this.priceStrategy = priceStrategy;
    }

    public BigDecimal getIncreaseRate() {
        return increaseRate;
    }

    public void setIncreaseRate(BigDecimal increaseRate) {
        this.increaseRate = increaseRate;
    }

    public BigDecimal getChannelCommissionRate() {
        return channelCommissionRate;
    }

    public void setChannelCommissionRate(BigDecimal channelCommissionRate) {
        this.channelCommissionRate = channelCommissionRate;
    }

    public BigDecimal getFqCommissionRate() {
        return fqCommissionRate;
    }

    public void setFqCommissionRate(BigDecimal fqCommissionRate) {
        this.fqCommissionRate = fqCommissionRate;
    }

    public Short getCostType() {
        return costType;
    }

    public void setCostType(Short costType) {
        this.costType = costType;
    }

    public Set<FinanceOrder> getChannelOrderList() {
        return channelOrderList;
    }

    public void setChannelOrderList(Set<FinanceOrder> channelOrderList) {
        this.channelOrderList = channelOrderList;
    }

    public String getAuditStatus() {
        return auditStatus;
    }

    public void setAuditStatus(String auditStatus) {
        this.auditStatus = auditStatus;
    }

    public boolean getIsArrival() {
        return isArrival;
    }

    public void setIsArrival(boolean isArrival) {
        this.isArrival = isArrival;
    }

    public String getSettlementTime() {
        return settlementTime;
    }

    public void setSettlementTime(String settlementTime) {
        this.settlementTime = settlementTime;
    }

    public BigDecimal getChannelSettlementAmount() {
        return channelSettlementAmount;
    }

    public void setChannelSettlementAmount(BigDecimal channelSettlementAmount) {
        this.channelSettlementAmount = channelSettlementAmount;
    }

    public BigDecimal getFqSettlementAmount() {
        return fqSettlementAmount;
    }

    public void setFqSettlementAmount(BigDecimal fqSettlementAmount) {
        this.fqSettlementAmount = fqSettlementAmount;
    }

    public BigDecimal getInnSettlementAmount() {
        return innSettlementAmount;
    }

    public void setInnSettlementAmount(BigDecimal innSettlementAmount) {
        this.innSettlementAmount = innSettlementAmount;
    }


    public Integer getReservationDays() {
        return reservationDays;
    }

    public void setReservationDays(Integer reservationDays) {
        this.reservationDays = reservationDays;
    }

    public Integer getStayDays() {
        return stayDays;
    }

    public void setStayDays(Integer stayDays) {
        this.stayDays = stayDays;
    }

    public Integer getNights() {

        return nights;
    }

    public void setNights(Integer nights) {
        this.nights = nights;
    }

    public Integer getRooms() {
        return rooms;
    }

    public void setRooms(Integer rooms) {
        this.rooms = rooms;
    }


}
