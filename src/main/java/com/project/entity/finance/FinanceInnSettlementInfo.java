package com.project.entity.finance;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * 客栈结算基本信息表
 * Created by sam on 2016/1/13.
 */
@Entity
@Table(name = "finance_inn_settlement_info")
public class FinanceInnSettlementInfo {
    // 主键ID
    @Id
    private Integer id;
    // 客栈名称
    @Column(name = "inn_name")
    private String innName;
    // 目的地ID
    @Column(name = "region_id")
    private Integer regionId;
    // 目的地名称
    @Column(name = "region_name")
    private String regionName;
    // 客栈联系电话
    @Column(name = "inn_contact")
    private String innContact;
    //短信通知联系号码1
    @Column(name = "contact1")
    private String contact1;
    //短信通知联系号码2
    @Column(name = "contact2")
    private String contact2;
    //微信号
    @Column(name = "wx_open_id")
    private String wxOpenId;
    // 开户类型：个人/对公
    @Column(name = "bank_type")
    private String bankType;
    // 开户人姓名
    @Column(name = "bank_account")
    private String bankAccount;
    // 银行卡号
    @Column(name = "bank_code")
    private String bankCode;
    // 开户行
    @Column(name = "bank_name")
    private String bankName;
    // 开户行地址
    @Column(name = "bank_region")
    private String bankRegion;
    // 最后修改时间
    @Column(name = "date_updated")
    private Date dateUpdated = new Date();
    // 开户行所在省
    @Column(name = "bank_province")
    private String bankProvince;
    // 开户行所在城市
    @Column(name = "bank_city")
    private String bankCity;
    // 城市code
    @Column(name = "city_code")
    private String cityCode;
    // 城市名
    @Column(name = "city_name")
    private String cityName;

    public String getBankProvince() {
        return bankProvince;
    }

    public void setBankProvince(String bankProvince) {
        this.bankProvince = bankProvince;
    }

    public String getBankCity() {
        return bankCity;
    }

    public void setBankCity(String bankCity) {
        this.bankCity = bankCity;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getInnName() {
        return innName;
    }

    public void setInnName(String innName) {
        this.innName = innName;
    }

    public Integer getRegionId() {
        return regionId;
    }

    public void setRegionId(Integer regionId) {
        this.regionId = regionId;
    }

    public String getRegionName() {
        return regionName;
    }

    public void setRegionName(String regionName) {
        this.regionName = regionName;
    }

    public String getInnContact() {
        return innContact;
    }

    public void setInnContact(String innContact) {
        this.innContact = innContact;
    }

    public String getContact1() {
        return contact1;
    }

    public void setContact1(String contact1) {
        this.contact1 = contact1;
    }

    public String getContact2() {
        return contact2;
    }

    public void setContact2(String contact2) {
        this.contact2 = contact2;
    }

    public String getWxOpenId() {
        return wxOpenId;
    }

    public void setWxOpenId(String wxOpenId) {
        this.wxOpenId = wxOpenId;
    }

    public String getBankType() {
        return bankType;
    }

    public void setBankType(String bankType) {
        this.bankType = bankType;
    }

    public String getBankAccount() {
        return bankAccount;
    }

    public void setBankAccount(String bankAccount) {
        this.bankAccount = bankAccount;
    }

    public String getBankCode() {
        return bankCode;
    }

    public void setBankCode(String bankCode) {
        this.bankCode = bankCode;
    }

    public String getBankName() {
        return bankName;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName;
    }

    public String getBankRegion() {
        return bankRegion;
    }

    public void setBankRegion(String bankRegion) {
        this.bankRegion = bankRegion;
    }

    public Date getDateUpdated() {
        return dateUpdated;
    }

    public void setDateUpdated(Date dateUpdated) {
        this.dateUpdated = dateUpdated;
    }

    public String getCityCode() {
        return cityCode;
    }

    public void setCityCode(String cityCode) {
        this.cityCode = cityCode;
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }
}
