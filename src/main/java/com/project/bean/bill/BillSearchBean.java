package com.project.bean.bill;

import java.util.Date;

import org.springframework.format.annotation.DateTimeFormat;

import com.project.utils.time.DateUtil;

/**
 * Created by xiamaoxuan on 2014/7/31.
 */
public class BillSearchBean {
    //当前页
    private int nowPage=1;
    //每页页数
    private int pageSize=10;
    //总页数
    private int totalPage;
    //查询条件0表示客栈名称，1表示订单号，2表示手机号码
    private int searchCondition;
    //0表示客栈名称
    public static final int INN_NAME_CODE=0;
    //1表示订单号
    public static final int ORDER_NO_CODE=1;
    //2表示手机号码
    public static final int CONTACT_CODE=2;
    private String payMode = "-1";
    private Integer isBalance = -1;
    private String productCode;

    
	public String getProductCode() {
		return productCode;
	}

	public void setProductCode(String productCode) {
		this.productCode = productCode;
	}

	public Integer getIsBalance() {
		return isBalance;
	}

	public void setIsBalance(Integer isBalance) {
		this.isBalance = isBalance;
	}

	//关键词
    private String keyWord="";
    //客栈ID
    private Integer innId;
    //开始时间
    @DateTimeFormat(pattern="yyyy-MM-dd")
    private Date startDate= DateUtil.addDay(new Date(),-10);
    //结束时间
    @DateTimeFormat(pattern="yyyy-MM-dd")
    private Date endDate=new Date();
    //统计bean
    private BillCountBean countBean;
    //是否是代收
    private boolean collection=true;
    //对账类别
    private String type;

    public Integer getInnId() {
        return innId;
    }

    public void setInnId(Integer innId) {
        this.innId = innId;
    }

    public int getNowPage() {
        return nowPage;
    }

    public void setNowPage(int nowPage) {
        this.nowPage = nowPage;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public int getTotalPage() {
        return totalPage;
    }

    public void setTotalPage(int totalPage) {
        this.totalPage = totalPage;
    }

    public int getSearchCondition() {
        return searchCondition;
    }

    public void setSearchCondition(int searchCondition) {
        this.searchCondition = searchCondition;
    }

    public String getKeyWord() {
        return keyWord;
    }

    public void setKeyWord(String keyWord) {
        this.keyWord = keyWord;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public BillCountBean getCountBean() {
        return countBean;
    }

    public void setCountBean(BillCountBean countBean) {
        this.countBean = countBean;
    }

    public boolean isCollection() {
        return collection;
    }

    public void setCollection(boolean collection) {
        this.collection = collection;
    }

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getPayMode() {
		return payMode;
	}

	public void setPayMode(String payMode) {
		this.payMode = payMode;
	}
}

