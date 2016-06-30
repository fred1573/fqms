package com.project.entity.finance;

import javax.persistence.*;
import java.util.Date;

/**
 * 财务对账操作记录
 * Created by 番茄桑 on 2015/9/23.
 */
@Entity
@Table(name = "finance_operation_log")
public class FinanceOperationLog {
    // 主键ID
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    // 操作人
    @Column(name = "operate_user")
    private String operateUser;
    // 操作时间
    @Column(name = "operate_time")
    private Date operateTime = new Date();
    // 结算时间
    @Column(name = "settlement_time")
    private String settlementTime;
    // 客栈ID
    @Column(name = "inn_id")
    private Integer innId;
    // 客栈名称
    @Column(name = "inn_name")
    private String innName;
    // 渠道ID
    @Column(name = "channel_id")
    private Integer channelId;
    // 渠道名称
    @Column(name = "channel_name")
    private String channelName;
    // 操作内容
    @Column(name = "operate_content")
    private String operateContent;
    // 操作对象
    @Column(name = "operate_object")
    private String operateObject;

    /**
     * 操作类型
     * 1:渠道对账
     * 2:收到渠道款项
     * 3:发送客栈账单
     * 4:结算客栈款项
     * 5:客栈管理
     * 6：渠道管理
     * 7：代销审核
     * 8:修改订单
     * 9:新增账期
     * 10:取消订单
     * 101：编辑分销商
     * 102：客栈管理-上架
     * 103：客栈管理-下架
     * 104：客栈管理-修改总抽佣比例
     * 105：客栈管理-修改渠道设置
     * 106：客栈管理-关房
     * 107：客栈管理-移除客栈
     * 108：客栈管理-批量上线渠道
     * 109：客栈管理-批量下线渠道
     * 110：客栈管理-批量关房
     * 111：客栈管理-价格审核
     * 112：客栈管理-合同审核
     * 113：客栈管理-调价
     * 114：关房结果
     * 201：编辑结算信息
     * 115：房态切换
     * 116:下架房型
     * 117:运营活动
     * 118:批量调价
     */
    @Column(name = "operate_type")
    private String operateType;


    public FinanceOperationLog() {
    }

    public FinanceOperationLog(String operateType, String operateObject, String operateContent, String operateUser) {
        this.operateType = operateType;
        this.operateObject = operateObject;
        this.operateContent = operateContent;
        this.operateUser = operateUser;
    }

    public FinanceOperationLog(String operateType, String operateObject, String operateContent, String operateUser, Date operateTime) {
        this.operateType = operateType;
        this.operateObject = operateObject;
        this.operateContent = operateContent;
        this.operateUser = operateUser;
        this.operateTime = operateTime;
    }

    public FinanceOperationLog(String operateUser, Integer innId, String innName,
                               Integer channelId, String channelName, String operateContent,
                               Integer operateType
    ) {
        this.channelId = channelId;
        this.channelName = channelName;
        this.innId = innId;
        this.innName = innName;
        this.operateContent = operateContent;
        this.operateTime = new Date();
        this.operateType = operateType + "";
        this.operateUser = operateUser;

    }

    public String getOperateObject() {
        return operateObject;
    }

    public void setOperateObject(String operateObject) {
        this.operateObject = operateObject;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getOperateUser() {
        return operateUser;
    }

    public void setOperateUser(String operateUser) {
        this.operateUser = operateUser;
    }

    public Date getOperateTime() {
        return operateTime;
    }

    public void setOperateTime(Date operateTime) {
        this.operateTime = operateTime;
    }

    public String getSettlementTime() {
        return settlementTime;
    }

    public void setSettlementTime(String settlementTime) {
        this.settlementTime = settlementTime;
    }

    public Integer getInnId() {
        return innId;
    }

    public void setInnId(Integer innId) {
        this.innId = innId;
    }

    public String getInnName() {
        return innName;
    }

    public void setInnName(String innName) {
        this.innName = innName;
    }

    public Integer getChannelId() {
        return channelId;
    }

    public void setChannelId(Integer channelId) {
        this.channelId = channelId;
    }

    public String getChannelName() {
        return channelName;
    }

    public void setChannelName(String channelName) {
        this.channelName = channelName;
    }

    public String getOperateContent() {
        return operateContent;
    }

    public void setOperateContent(String operateContent) {
        this.operateContent = operateContent;
    }

    public String getOperateType() {
        return operateType;
    }

    public void setOperateType(String operateType) {
        this.operateType = operateType;
    }
}
