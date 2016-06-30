package com.project.entity.bill;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Created by xiamaoxuan on 2014/9/11 0011.
 */
@Entity
@Table(name = "wg_inn_wei_shop", schema = "public")
public class WgInnWeiShop {

    private Integer id;
    private Integer payType;
    private Integer innId;

    @Column(name = "pay_type", length = 32)
    public Integer getPayType() {
        return payType;
    }

    public void setPayType(Integer payType) {
        this.payType = payType;
    }
    @Id
    @Column(name = "id", unique = true, nullable = false)
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }
    @Column(name = "inn_id", length = 32)
    public Integer getInnId() {
        return innId;
    }

    public void setInnId(Integer innId) {
        this.innId = innId;
    }
}
