package com.project.entity.proxysale;

import javax.persistence.*;
import java.util.Date;

/**
 * price strategy class
 * Created by Administrator on 2015/6/4.
 */
@Entity
@Table(name = "tomato_proxysale_price_strategy")
public class PriceStrategy {

    public static final Short STRATEGY_BASE_PRICE = 1;//活动价，以前也叫底价、精品价 =。=
    public static final Short STRATEGY_SALE_PRICE = 2;//普通卖价，以前也叫卖价，普通价 O_o
    public static final Short STRATEGY_SALE_BASE_PRICE = 3;//普通卖转底，以前没有这个价 o_O???

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(name = "channel")
    private Integer channel;
    @Column
    private Float percentage;
    @Column
    private Short strategy;
    @Column
    private boolean valid = true;
    @Column(name = "create_time")
    private Date createTime;

    public PriceStrategy(){}

    public PriceStrategy(Integer channel, Float percentage, Short strategy, boolean valid){
        this.channel = channel;
        this.percentage = percentage;
        this.strategy = strategy;
        this.valid = valid;
        this.createTime = new Date();
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getChannel() {
        return channel;
    }

    public void setChannel(Integer channel) {
        this.channel = channel;
    }

    public Float getPercentage() {
        return percentage;
    }

    public void setPercentage(Float percentage) {
        this.percentage = percentage;
    }

    public Short getStrategy() {
        return strategy;
    }

    public void setStrategy(Short strategy) {
        this.strategy = strategy;
    }

    public Boolean isValid() {
        return valid;
    }

    public void setValid(Boolean valid) {
        this.valid = valid;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }
}
