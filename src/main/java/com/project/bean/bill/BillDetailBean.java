package com.project.bean.bill;

import java.util.Date;
import java.util.List;

/**
 * Created by xiamaoxuan on 2014/7/30.
 */
public class BillDetailBean {
	//支付记录ID
    private Integer payId;
	//主订单ID
    private Integer mainOrderId;
    //客栈ID
    private Integer innId;
    //支付宝信息
    private String ZFBName;
    //客栈名
    private String innName;
    //订单编号
    private String orderNo;
    //姓名
    private String name;
    //手机号码
    private String contact;
    //总金额
    private Double totalAmount;
    //是否结算（0:未结算,1:已结算）
    private String isBalance;
    
    private String inouts;
    
    private String roomTypes;
    
    private String orderInfos;
    
    private String productName;
    
    public String getProductName() {
		return productName;
	}

	public void setProductName(String productName) {
		this.productName = productName;
	}

	public String getOrderInfos() {
		return orderInfos;
	}

	public void setOrderInfos(String orderInfos) {
		this.orderInfos = orderInfos;
	}

	//房型,数量,单价,入住,退房
    private List<RoomDetailBean> roomDetailBeans;
    //支付时间
    private Date payAt;

    public Integer getPayId() {
		return payId;
	}

	public void setPayId(Integer payId) {
		this.payId = payId;
	}

	public String getZFBName() {
        return ZFBName;
    }

    public void setZFBName(String ZFBName) {
        this.ZFBName = ZFBName;
    }

    public Integer getMainOrderId() {
        return mainOrderId;
    }

    public void setMainOrderId(Integer mainOrderId) {
        this.mainOrderId = mainOrderId;
    }

    public Integer getInnId() {
        return innId;
    }

    public void setInnId(Integer innId) {
        this.innId = innId;
    }

    public String getInnName() {
        return innName;
    }

    public void setInnName(String innName) {
        this.innName = innName;
    }

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public Double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(Double totalAmount) {
        this.totalAmount = totalAmount;
    }

    public String getIsBalance() {
        return isBalance;
    }

    public void setIsBalance(String isBalance) {
        this.isBalance = isBalance;
    }

    public List<RoomDetailBean> getRoomDetailBeans() {
        return roomDetailBeans;
    }

    public void setRoomDetailBeans(List<RoomDetailBean> roomDetailBeans) {
        this.roomDetailBeans = roomDetailBeans;
    }

    public Date getPayAt() {
        return payAt;
    }

    public void setPayAt(Date payAt) {
        this.payAt = payAt;
    }

	public String getInouts() {
		return inouts;
	}

	public void setInouts(String inouts) {
		this.inouts = inouts;
	}

	public String getRoomTypes() {
		return roomTypes;
	}

	public void setRoomTypes(String roomTypes) {
		this.roomTypes = roomTypes;
	}
    
    
}
