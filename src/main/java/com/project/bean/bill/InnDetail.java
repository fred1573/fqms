package com.project.bean.bill;

/**
 * Created by xiamaoxuan on 2014/8/27 0027.
 */
public class InnDetail {
    //客栈名
    private String innName;
    //联系人
    private String contactName;
    //电话号码
    private String contact;
    //支付宝账号
    private String ZFBName;
    //银行
    private String bankName;
    //银行卡号
    private String bankCard;
    //开户人姓名
    private String bankCardHolder;
    //所在地区
    private String bankArea;
    /** 支付宝账号 */
    private String alipayCode;
	/** 支付宝收款人 */
    private String alipayUser;
	/** 财付通账号 */
    private String tenpayCode;
	/** 财付通收款人 */
    private String tenpayUser;

    public String getBankArea() {
        return bankArea;
    }

    public void setBankArea(String bankArea) {
        this.bankArea = bankArea;
    }

    public String getInnName() {
        return innName;
    }

    public void setInnName(String innName) {
        this.innName = innName;
    }

    public String getContactName() {
        return contactName;
    }

    public void setContactName(String contactName) {
        this.contactName = contactName;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public String getZFBName() {
        return ZFBName;
    }

    public void setZFBName(String ZFBName) {
        this.ZFBName = ZFBName;
    }

    public String getBankName() {
        return bankName;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName;
    }

    public String getBankCard() {
        return bankCard;
    }

    public void setBankCard(String bankCard) {
        this.bankCard = bankCard;
    }

    public String getBankCardHolder() {
        return bankCardHolder;
    }

    public void setBankCardHolder(String bankCardHolder) {
        this.bankCardHolder = bankCardHolder;
    }

	public String getAlipayCode() {
		return alipayCode;
	}

	public void setAlipayCode(String alipayCode) {
		this.alipayCode = alipayCode;
	}

	public String getAlipayUser() {
		return alipayUser;
	}

	public void setAlipayUser(String alipayUser) {
		this.alipayUser = alipayUser;
	}

	public String getTenpayCode() {
		return tenpayCode;
	}

	public void setTenpayCode(String tenpayCode) {
		this.tenpayCode = tenpayCode;
	}

	public String getTenpayUser() {
		return tenpayUser;
	}

	public void setTenpayUser(String tenpayUser) {
		this.tenpayUser = tenpayUser;
	}
    
}
