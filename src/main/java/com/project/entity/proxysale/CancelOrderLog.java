package com.project.entity.proxysale;

/**
 * Created by admin on 2016/4/28.
 */

import javax.persistence.*;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 代销取消订单日志
 */
@Entity
@Table(name = "cancel_order_log")
public class CancelOrderLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Integer id;
    //分销商订单号
    @Column(name = "channel_order_no")
    private String channelOrderNo;
    //操作人
    @Column(name = "operate_user")
    private String operateUser;
    //备注
    @Column(name = "remark")
    private String remark;
    //操作时间
    @Column(name = "operate_time")
    private String operateTime;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getChannelOrderNo() {
        return channelOrderNo;
    }

    public void setChannelOrderNo(String channelOrderNo) {
        this.channelOrderNo = channelOrderNo;
    }

    public String getOperateUser() {
        return operateUser;
    }

    public void setOperateUser(String operateUser) {
        this.operateUser = operateUser;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getOperateTime() {
        return operateTime;
    }

    public void setOperateTime(String operateTime) {
        this.operateTime = operateTime;

    }
}
