package com.project.entity.finance;

import com.project.entity.account.User;

import javax.persistence.*;
import java.util.Date;

/**
 * 账期对象
 * Created by sam on 2015/12/30.
 */
@Entity
@Table(name = "finance_account_period")
public class FinanceAccountPeriod {
    // 主键ID
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    // 创建时间
    @Column(name = "date_created")
    private Date dateCreated = new Date();
    // 最后修改时间
    @Column(name = "date_updated")
    private Date dateUpdated = new Date();
    // 创建人
    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "creator")
    private User creator;
    // 最后修改人
    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "modifior")
    private User modifior;
    // 版本号，修改次数
    @Column(name = "version")
    private int version;
    // 是否逻辑删除
    @Column(name = "deleted")
    private boolean deleted;
    // 结算周期(yyyy-MM-dd至yyyy-MM-dd)
    @Column(name = "settlement_time")
    private String settlementTime;
    // 账期状态(0:正常,1:锁定)
    @Column(name = "account_status")
    private String accountStatus;
    //是否发送账单（默认false:未发送,true:已发送）
    @Column(name="send_bill_status")
    private boolean sendBillStatus;

    public boolean getSendBillStatus() {
        return sendBillStatus;
    }

    public void setSendBillStatus(boolean sendBillStatus) {
        this.sendBillStatus = sendBillStatus;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Date getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(Date dateCreated) {
        this.dateCreated = dateCreated;
    }

    public Date getDateUpdated() {
        return dateUpdated;
    }

    public void setDateUpdated(Date dateUpdated) {
        this.dateUpdated = dateUpdated;
    }

    public User getCreator() {
        return creator;
    }

    public void setCreator(User creator) {
        this.creator = creator;
    }

    public boolean isDeleted() {
        return deleted;
    }

    public User getModifior() {
        return modifior;
    }

    public void setModifior(User modifior) {
        this.modifior = modifior;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public boolean getDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

    public String getSettlementTime() {
        return settlementTime;
    }

    public void setSettlementTime(String settlementTime) {
        this.settlementTime = settlementTime;
    }

    public String getAccountStatus() {
        return accountStatus;
    }

    public void setAccountStatus(String accountStatus) {
        this.accountStatus = accountStatus;
    }


}
