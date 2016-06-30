package com.project.entity.proxysale;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.project.enumeration.ProxySaleOrderComplaintStatus;
import com.project.enumeration.ProxySaleOrderComplaintType;
import org.joda.time.DateTime;
import org.joda.time.Minutes;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * 代销订单投诉
 *
 * @author yuneng.huang on 2016/6/7.
 */
public class ProxySaleOrderComplaint {

    private Long id;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    private Date createTime = new Date();
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    private Date updateTime = new Date();
    //完成人id
    private Long finishUserId;
    //完成人名
    private String finishUserName;
    //完成时间
    private Date finishTime;
    //最新投诉类型
    private ProxySaleOrderComplaintType complaintType;
    //投诉处理状态
    private ProxySaleOrderComplaintStatus complaintStatus=ProxySaleOrderComplaintStatus.STARTED;
    // 渠道订单Id
    private String channelId;
    // 渠道名称
    private String channelName;
    //子分销商ID
    private String channelChildId;
    // 子分销商名称
    private String channelCodeName;
    //目的地
    private String regionName;
    // 客栈id
    private Long innId;
    // 客栈名称
    private String innName;
    //客栈电话
    private String innPhone;
    //客户经理
    private String customerManager;
    // 分销商订单号
    private String channelOrderNo;
    // 用户真实姓名（顾客姓名）
    private String userName;
    // 联系方式
    private String contact;
    //子订单列表
    List<ProxySaleSubOrder> channelOrderList;
    // 分销商订单总金额
    private BigDecimal totalAmount;
    // 下单时间
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    private Date orderTime;
    // OMS订单号
    private String orderNo;
    //最新的处理记录
    private OrderComplaintProcessLog processLog;

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    public OrderComplaintProcessLog getProcessLog() {
        return processLog;
    }

    public void setProcessLog(OrderComplaintProcessLog processLog) {
        this.processLog = processLog;
    }

    public String getInnPhone() {
        return innPhone;
    }

    public void setInnPhone(String innPhone) {
        this.innPhone = innPhone;
    }

    public ProxySaleOrderComplaintStatus getComplaintStatus() {
        return complaintStatus;
    }

    public void setComplaintStatus(ProxySaleOrderComplaintStatus complaintStatus) {
        this.complaintStatus = complaintStatus;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public Long getFinishUserId() {
        return finishUserId;
    }

    public void setFinishUserId(Long finishUserId) {
        this.finishUserId = finishUserId;
    }

    public String getFinishUserName() {
        return finishUserName;
    }

    public void setFinishUserName(String finishUserName) {
        this.finishUserName = finishUserName;
    }

    public Date getFinishTime() {
        return finishTime;
    }

    public void setFinishTime(Date finishTime) {
        this.finishTime = finishTime;
    }

    public ProxySaleOrderComplaintType getComplaintType() {
        return complaintType;
    }

    public void setComplaintType(ProxySaleOrderComplaintType complaintType) {
        this.complaintType = complaintType;
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

    public String getChannelChildId() {
        return channelChildId;
    }

    public void setChannelChildId(String channelChildId) {
        this.channelChildId = channelChildId;
    }

    public String getChannelCodeName() {
        return channelCodeName;
    }

    public void setChannelCodeName(String channelCodeName) {
        this.channelCodeName = channelCodeName;
    }

    public String getRegionName() {
        return regionName;
    }

    public void setRegionName(String regionName) {
        this.regionName = regionName;
    }

    public Long getInnId() {
        return innId;
    }

    public void setInnId(Long innId) {
        this.innId = innId;
    }

    public String getInnName() {
        return innName;
    }

    public void setInnName(String innName) {
        this.innName = innName;
    }

    public String getCustomerManager() {
        return customerManager;
    }

    public void setCustomerManager(String customerManager) {
        this.customerManager = customerManager;
    }

    public String getChannelOrderNo() {
        return channelOrderNo;
    }

    public void setChannelOrderNo(String channelOrderNo) {
        this.channelOrderNo = channelOrderNo;
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


    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public List<ProxySaleSubOrder> getChannelOrderList() {
        return channelOrderList;
    }

    public void setChannelOrderList(List<ProxySaleSubOrder> channelOrderList) {
        this.channelOrderList = channelOrderList;
    }

    public Date getOrderTime() {
        return orderTime;
    }

    public void setOrderTime(Date orderTime) {
        this.orderTime = orderTime;
    }

    public int getFinishTimeMinutes() {
        return Minutes.minutesBetween(new DateTime(this.createTime), new DateTime(this.finishTime)).getMinutes();
    }

    public String getOrderTimeStr() {
        if (this.orderTime == null) {
            return "";
        }
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return dateFormat.format(this.orderTime);
    }

    public String getCheckInAndOutStr() {
        if (CollectionUtils.isEmpty(this.channelOrderList)) {
            return "";
        }
        StringBuilder builder = new StringBuilder();
        for (ProxySaleSubOrder proxySaleSubOrder : channelOrderList) {
            Date checkInAt = proxySaleSubOrder.getCheckInAt();
            if (checkInAt != null) {
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                String checkInStr = dateFormat.format(checkInAt);
                builder.append(checkInStr).append("/");
            }
            Date checkOutAt = proxySaleSubOrder.getCheckOutAt();
            if (checkOutAt != null) {
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                String checkOutStr = dateFormat.format(checkOutAt);
                builder.append(checkOutStr);
            }
            builder.append("  ");
        }
        return builder.toString();
    }

}
