package com.project.entity.finance;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Date;

/**挂账客栈
 * Created by admin on 2016/3/9.
 */
@Entity
@Table(name = "finance_arrear_inn")
public class FinanceArrearInn {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    //客栈ID
    @Column(name="inn_id")
    private Integer innId;
    //客栈挂账状态
    @Column(name="status")
    private String status;
    //往期挂账
    @Column(name="arrear_past")
    private BigDecimal arrearPast;
    //剩余挂账
    @Column(name="arrear_remaining")
    private BigDecimal arrearRemaining;
    //账期
    @Column(name = "settlement_time")
    private String settlementTime;
    @Column(name="operate_time")
    private Date operateTime=new Date();
    //平账备注
    @Column(name="remark")
    private String remark;
    @Column(name="manual_level")
    private Boolean ManualLevel;

    public Boolean getManualLevel() {
        return ManualLevel;
    }

    public void setManualLevel(Boolean manualLevel) {
        ManualLevel = manualLevel;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public Date getOperateTime() {
        return operateTime;
    }

    public void setOperateTime(Date operateTime) {
        this.operateTime = operateTime;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getInnId() {
        return innId;
    }

    public void setInnId(Integer innId) {
        this.innId = innId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public BigDecimal getArrearPast() {
        return arrearPast;
    }

    public void setArrearPast(BigDecimal arrearPast) {
        this.arrearPast = arrearPast;
    }

    public BigDecimal getArrearRemaining() {
        return arrearRemaining;
    }

    public void setArrearRemaining(BigDecimal arrearRemaining) {
        this.arrearRemaining = arrearRemaining;
    }

    public String getSettlementTime() {
        return settlementTime;
    }

    public void setSettlementTime(String settlementTime) {
        this.settlementTime = settlementTime;
    }
}
