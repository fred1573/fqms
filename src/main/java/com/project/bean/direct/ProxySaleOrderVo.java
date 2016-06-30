package com.project.bean.direct;

import com.project.utils.CollectionsUtil;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 用于代销订单展示的VO对象
 * Created by 番茄桑 on 2015/7/29.
 */
public class ProxySaleOrderVo {
    private final static Logger LOGGER = LoggerFactory.getLogger(ProxySaleOrderVo.class);
    // 渠道订单Id
    private String channelId;
    // 渠道名称
    private String channelName;
    // 订单详情，子订单集合
    private List<SubOrder> channelOrderList;
    // 订单号
    private String channelOrderNo;
    // 订单状态（类型）
    private String conName;
    // 联系方式
    private String contact;
    // 主订单id
    private String id;
    // 操作人
    private String operatedUser;
    // 下单时间
    private String orderTime;
    // 订单已付金额
    private String paidAmount;
    // 支付类型(prepay(预付)、assure(担保))
    private String payType;
    // 特殊需求
    private String remark;
    // 分销商订单总金额
    private String totalAmount;
    // 用户真实姓名（顾客姓名）
    private String userName;
    // 客栈名称
    private String innName;
    // 搜索关键字(模糊匹配客栈名称、订单号、地区)
    private String queryValue;
    // 价格策略（1：精品(活动)，2：普通(卖)）3：普通(底)
    private String strategyType;
    // 区域
    private String city;
    // OMS订单号
    private String orderNo;
    //目的地
    private String regionName;
    //渠道订单单价
    private String channelAmount;
    //间夜数
    private String roomNights;
    //番茄调价
    private String extraPrice;
    // 子分销商名称
    private String channelCodeName;
    //pms客栈ID
    private Integer innId;
    //客户经理
    private String customerManager;
    //子分销商ID
    private String channelChildId;

    public String getChannelChildId() {
        return channelChildId;
    }

    public void setChannelChildId(String channelChildId) {
        this.channelChildId = channelChildId;
    }

    public String getCustomerManager() {
        return customerManager;
    }

    public void setCustomerManager(String customerManager) {
        this.customerManager = customerManager;
    }

    public Integer getInnId() {
        return innId;
    }

    public void setInnId(Integer innId) {
        this.innId = innId;
    }

    public String getChannelCodeName() {
        return channelCodeName;
    }

    public void setChannelCodeName(String channelCodeName) {
        this.channelCodeName = channelCodeName;
    }

    public String getExtraPrice() {
        return extraPrice;
    }

    public void setExtraPrice(String extraPrice) {
        this.extraPrice = extraPrice;
    }

    public String getChannelAmount() {
        return channelAmount;
    }

    public void setChannelAmount(String channelAmount) {
        this.channelAmount = channelAmount;
    }

    public String getRoomNights() {
        Integer rooms = 0;
        Integer nights = 0;
        Integer sum = 0;
        if (CollectionsUtil.isNotEmpty(this.channelOrderList)) {
            for (SubOrder subOrder : channelOrderList) {
                rooms = subOrder.getRoomTypeNums();
                nights = Integer.parseInt(subOrder.getNightNumber());
                if (rooms != null && nights != null) {
                    sum += rooms * nights;
                }
            }
        }
        return sum.toString();
    }

    public void setRoomNights(String roomNights) {
        this.roomNights = roomNights;
    }

    public String getRegionName() {
        return regionName;
    }

    public void setRegionName(String regionName) {
        this.regionName = regionName;
    }

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getStrategyType() {
        return strategyType;
    }

    public void setStrategyType(String strategyType) {
        this.strategyType = strategyType;
    }

    public String getQueryValue() {
        return queryValue;
    }

    public void setQueryValue(String queryValue) {
        this.queryValue = queryValue;
    }

    public String getInnName() {
        return innName;
    }

    public void setInnName(String innName) {
        this.innName = innName;
    }

    public String getChannelId() {
        return channelId;
    }

    public void setChannelId(String channelId) {
        this.channelId = channelId;
    }

    public String getChannelName() {
        return channelName;
    }

    public void setChannelName(String channelName) {
        this.channelName = channelName;
    }

    public List<SubOrder> getChannelOrderList() {
        //返回合并处理后的子订单
        return channelOrderList;
    }

    public void setChannelOrderList(List<SubOrder> channelOrderList) {
        this.channelOrderList = channelOrderList;
    }

    public String getChannelOrderNo() {
        return channelOrderNo;
    }

    public void setChannelOrderNo(String channelOrderNo) {
        this.channelOrderNo = channelOrderNo;
    }

    public String getConName() {
        return conName;
    }

    public void setConName(String conName) {
        this.conName = conName;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getOperatedUser() {
        return operatedUser;
    }

    public void setOperatedUser(String operatedUser) {
        this.operatedUser = operatedUser;
    }

    public String getOrderTime() {
        return orderTime;
    }

    public void setOrderTime(String orderTime) {
        this.orderTime = orderTime;
    }

    public String getPaidAmount() {
        return paidAmount;
    }

    public void setPaidAmount(String paidAmount) {
        this.paidAmount = paidAmount;
    }

    public String getPayType() {
        return payType;
    }

    public void setPayType(String payType) {
        this.payType = payType;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(String totalAmount) {
        this.totalAmount = totalAmount;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

}
