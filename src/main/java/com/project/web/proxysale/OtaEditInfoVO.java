package com.project.web.proxysale;

import com.project.web.area.ApiArea;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2015/6/29.
 */
public class OtaEditInfoVO {

    private BigDecimal basePriceStrategry = new BigDecimal(-1);//精品策略比例
    private BigDecimal salePriceStrategry = new BigDecimal(-1);//普通卖价策略比例
    private BigDecimal saleBasePriceStrategry = new BigDecimal(-1);//普通卖转底价策略比例

    private List<ApiArea> areas = new ArrayList<>();

    public BigDecimal getBasePriceStrategry() {
        return basePriceStrategry;
    }

    public void setBasePriceStrategry(BigDecimal basePriceStrategry) {
        this.basePriceStrategry = basePriceStrategry;
    }

    public BigDecimal getSalePriceStrategry() {
        return salePriceStrategry;
    }

    public void setSalePriceStrategry(BigDecimal salePriceStrategry) {
        this.salePriceStrategry = salePriceStrategry;
    }

    public BigDecimal getSaleBasePriceStrategry() {
        return saleBasePriceStrategry;
    }

    public void setSaleBasePriceStrategry(BigDecimal saleBasePriceStrategry) {
        this.saleBasePriceStrategry = saleBasePriceStrategry;
    }

    public List<ApiArea> getAreas() {
        return areas;
    }

    public void setAreas(List<ApiArea> areas) {
        this.areas = areas;
    }
}
