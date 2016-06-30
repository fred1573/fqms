package com.project.entity.proxysale;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.project.enumeration.ProxySaleOrderComplaintStatus;
import com.project.enumeration.ProxySaleOrderComplaintType;

import java.util.Date;

/**
 * 供销订单投诉跟进记录
 *
 * @author yuneng.huang on 2016/6/13.
 */
public class OrderComplaintProcessLog {

    private Long id;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    private Date createTime = new Date();
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    private Date updateTime = new Date();

    //处理人id
    private Long processUserId;
    //处理人名
    private String processUserName;
    //投诉类型
    private ProxySaleOrderComplaintType complaintType;
    //跟进记录描述
    private String note;
    //代销订单投诉id
    private Long orderComplaintId;
    //投诉处理状态
    private ProxySaleOrderComplaintStatus complaintStatus = ProxySaleOrderComplaintStatus.STARTED;


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

    public Long getProcessUserId() {
        return processUserId;
    }

    public void setProcessUserId(Long processUserId) {
        this.processUserId = processUserId;
    }

    public String getProcessUserName() {
        return processUserName;
    }

    public void setProcessUserName(String processUserName) {
        this.processUserName = processUserName;
    }

    public ProxySaleOrderComplaintType getComplaintType() {
        return complaintType;
    }

    public void setComplaintType(ProxySaleOrderComplaintType complaintType) {
        this.complaintType = complaintType;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public Long getOrderComplaintId() {
        return orderComplaintId;
    }

    public void setOrderComplaintId(Long orderComplaintId) {
        this.orderComplaintId = orderComplaintId;
    }

    public ProxySaleOrderComplaintStatus getComplaintStatus() {
        return complaintStatus;
    }

    public void setComplaintStatus(ProxySaleOrderComplaintStatus complaintStatus) {
        this.complaintStatus = complaintStatus;
    }

    public String getComplaintTypeName() {
        if (this.complaintType == null) {
            return null;
        }
        return this.complaintType.getDescription();
    }
}
