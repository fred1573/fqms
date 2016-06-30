package com.project.web.proxysale;

/**
 * Created by Administrator on 2015/8/26.
 */
public class ProxyInnFormModify {

    //客栈详细地址
    private String innAddr;
    //区县名
    private String area;
    //客栈ID
    private Integer innId;
    //客栈名称
    private String innName;
    //客栈电话
    private String innPhone;

    public String getInnAddr() {
        return innAddr;
    }

    public void setInnAddr(String innAddr) {
        this.innAddr = innAddr;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
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

    public String getInnPhone() {
        return innPhone;
    }

    public void setInnPhone(String innPhone) {
        this.innPhone = innPhone;
    }

    @Override
    public String toString() {
        return "ProxyInnFormModify{" +
                "innAddr='" + innAddr + '\'' +
                ", area='" + area + '\'' +
                ", innId=" + innId +
                ", innName='" + innName + '\'' +
                ", innPhone='" + innPhone + '\'' +
                '}';
    }
}
