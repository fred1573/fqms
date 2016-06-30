package com.project.entity.proxysale;

/**
 * Created by admin on 2016/5/12.
 */
//代销房型
public class ProxyRoomType {
    //oms开通渠道id
    private Integer accountId;
    //客栈id
    private Integer innId;
    //oms房型id
    private Integer omsRoomTypeId;
    //ota房型id
    private Integer otaRoomTypeId;
    //渠道id
    private Integer otaId;
    //客栈名称
    private String innName;
    //策略模式（1：精品代销底价；2：普通代销卖价）
    private Integer strategyType;
    //房型名称
    private String roomTypeName;

    public Integer getOtaRoomTypeId() {
        return otaRoomTypeId;
    }

    public void setOtaRoomTypeId(Integer otaRoomTypeId) {
        this.otaRoomTypeId = otaRoomTypeId;
    }

    public String getRoomTypeName() {
        return roomTypeName;
    }

    public void setRoomTypeName(String roomTypeName) {
        this.roomTypeName = roomTypeName;
    }

    public Integer getInnId() {
        return innId;
    }

    public void setInnId(Integer innId) {
        this.innId = innId;
    }

    public Integer getAccountId() {
        return accountId;
    }

    public void setAccountId(Integer accountId) {
        this.accountId = accountId;
    }

    public Integer getOmsRoomTypeId() {
        return omsRoomTypeId;
    }

    public void setOmsRoomTypeId(Integer omsRoomTypeId) {
        this.omsRoomTypeId = omsRoomTypeId;
    }

    public Integer getOtaId() {
        return otaId;
    }

    public void setOtaId(Integer otaId) {
        this.otaId = otaId;
    }

    public String getInnName() {
        return innName;
    }

    public void setInnName(String innName) {
        this.innName = innName;
    }

    public Integer getStrategyType() {
        return strategyType;
    }

    public void setStrategyType(Integer strategyType) {
        this.strategyType = strategyType;
    }
}
