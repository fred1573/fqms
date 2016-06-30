package com.project.entity.proxysale;

import com.project.entity.account.User;

import javax.persistence.*;
import java.util.Date;

/**
 * @author Administrator
 *         2015-10-15 15:41
 */
@Entity
@Table(name = "tomato_proxysale_inn_onoff")
@SequenceGenerator(name = "proxysale_inn_onoff_id_seq", sequenceName = "tomato_proxysale_inn_onoff_id_seq")
public class ProxyInnOnoff {
	
	public static  final String ON = "ON";
	public static  final String OFF = "OFF";
	

    private Long id;
    private ProxyInn proxyInn;
    // 模式：1-底价 2-卖价
    private Short pattern;
    private Date time;
    private User operator;
    // 操作类型,OFF-下架/ON-上架
    private String operateType;
    // 备注(下架原因)
    private String remark;

    public ProxyInnOnoff() {}

    public ProxyInnOnoff(ProxyInn proxyInn, Short pattern, User operator,String operateType, String remark) {
        this.proxyInn = proxyInn;
        this.pattern = pattern;
        this.operator = operator;
        this.time = new Date();
        this.operateType = operateType;
        this.remark = remark;
    }

    public ProxyInnOnoff(ProxyInn proxyInn, Short pattern, User operator,String operateType) {
        this.proxyInn = proxyInn;
        this.pattern = pattern;
        this.operator = operator;
        this.time = new Date();
        this.operateType = operateType;
    }
    
    public ProxyInnOnoff(ProxyInn proxyInn, Short pattern, User operator) {
        this.proxyInn = proxyInn;
        this.pattern = pattern;
        this.operator = operator;
        this.time = new Date();
    }

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "proxysale_inn_onoff_id_seq")
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "proxy_inn")
    public ProxyInn getProxyInn() {
        return proxyInn;
    }

    public void setProxyInn(ProxyInn proxyInn) {
        this.proxyInn = proxyInn;
    }

    @Column
    public Short getPattern() {
        return pattern;
    }

    public void setPattern(Short pattern) {
        this.pattern = pattern;
    }

    @Column
    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "operator")
    public User getOperator() {
        return operator;
    }

    public void setOperator(User operator) {
        this.operator = operator;
    }

    @Column(name = "operate_type")
    public String getOperateType() {
        return operateType;
    }

    public void setOperateType(String operateType) {
        this.operateType = operateType;
    }

    @Column(name = "remark")
    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }
}
