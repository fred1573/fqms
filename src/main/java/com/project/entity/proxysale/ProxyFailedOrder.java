package com.project.entity.proxysale;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * 创建失败的订单备录
 * Created by Administrator on 2015/7/3.
 */
@Entity
@Table(name = "tomato_proxysale_failed_order")
public class ProxyFailedOrder {

    @Id
    private String id;
    @Column(name = "ota_order_no")
    private String otaOrderNo;
    @Column
    private String source;
    @Column
    private String reason;

    private ProxyFailedOrder() {
    }

    public ProxyFailedOrder(String id, String orderJson, String reason) {
        this.id = id;
        this.source = orderJson;
        this.reason = reason;
    }

    public ProxyFailedOrder(String id, String otaOrderNo, String orderJson, String reason) {
        this(id, orderJson, reason);
        this.otaOrderNo = otaOrderNo;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getOtaOrderNo() {
        return otaOrderNo;
    }

    public void setOtaOrderNo(String otaOrderNo) {
        this.otaOrderNo = otaOrderNo;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }
}
