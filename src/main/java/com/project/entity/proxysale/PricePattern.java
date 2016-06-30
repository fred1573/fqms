package com.project.entity.proxysale;

import javax.persistence.*;

/**
 * price pattern
 * Created by Administrator on 2015/6/4.
 */
@Entity
@Table(name = "tomato_proxysale_price_pattern")
public class PricePattern {

    public static final Short PATTERN_BASE_PRICE = 1;
    public static final Short PATTERN_SALE_PRICE = 2;
    

    public static final float DEFAULT_SALE_PERCENTAGE = 13;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "proxy_inn")
    private ProxyInn proxyInn;
    @Column
    private Float percentage;
    @Column
    private Short pattern;
    @Column(name = "outer_id")
    private Integer outerId;
    @Column
    private boolean valid;

    public PricePattern(){}

    public PricePattern(ProxyInn proxyInn, Float percentage, Short pattern, boolean valid){
        if(pattern.shortValue() == PricePattern.PATTERN_BASE_PRICE.shortValue()
                && percentage != null){
            throw new RuntimeException("底价模式不能设置百分比");
        }
        this.proxyInn = proxyInn;
        this.percentage = percentage;
        this.pattern = pattern;
        this.valid = valid;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Short getPattern() {
        return pattern;
    }

    public void setPattern(Short pattern) {
        this.pattern = pattern;
    }

    public Float getPercentage() {
        return percentage;
    }

    public void setPercentage(Float percentage) {
        this.percentage = percentage;
    }

    public ProxyInn getProxyInn() {
        return proxyInn;
    }

    public void setProxyInn(ProxyInn proxyInn) {
        this.proxyInn = proxyInn;
    }

    public Integer getOuterId() {
        return outerId;
    }

    public void setOuterId(Integer outerId) {
        this.outerId = outerId;
    }

    public boolean isValid() {
        return valid;
    }

    public void setValid(boolean valid) {
        this.valid = valid;
    }
}
