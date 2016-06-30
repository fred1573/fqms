package com.project.entity.proxysale;

/**
 * 代销合同图片地址,数据从crm接口获取，不直接查库
 * Created by Administrator on 2015/8/24.
 */
public class ProxyContractImage {

    private String contractNo;
    private String url;

    public String getContractNo() {
        return contractNo;
    }

    public void setContractNo(String contractNo) {
        this.contractNo = contractNo;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

}
