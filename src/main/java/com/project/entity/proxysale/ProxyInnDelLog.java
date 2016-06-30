package com.project.entity.proxysale;

import com.project.entity.account.User;

import javax.persistence.*;
import java.util.Date;

/**
 * 代销客栈移除记录
 * @author hunhun
 *         2015-11-16 15:20
 */
@Entity
@Table(name = "tomato_proxysale_inn_del_log")
public class ProxyInnDelLog {

    private Integer id;
    private ProxyInn proxyInn;
    private Date delTime;
    private User user;
    private String reason;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "proxy_inn")
    public ProxyInn getProxyInn() {
        return proxyInn;
    }

    public void setProxyInn(ProxyInn proxyInn) {
        this.proxyInn = proxyInn;
    }

    @Column(name = "del_time")
    public Date getDelTime() {
        return delTime;
    }

    public void setDelTime(Date delTime) {
        this.delTime = delTime;
    }

    @ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "_user")
    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @Column
    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
}
