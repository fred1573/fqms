package com.project.bean.vo;

/**
 * 房态切换VO对象
 * Created by sam on 2016/4/15.
 */
public class RoomStatusVo {
    private Integer innId;
    // 客栈名称
    private String innName;
    // 注册账号
    private String userCode;
    // 目前使用房态，1：PMS，2：ebk
    private Integer adminType;
    // 房态展示字段
    private String adminTypeStr;

    public RoomStatusVo() {

    }

    public RoomStatusVo(Integer innId, String innName, String userCode, Integer adminType, String adminTypeStr) {
        this.innId = innId;
        this.innName = innName;
        this.userCode = userCode;
        this.adminType = adminType;
        this.adminTypeStr = adminTypeStr;
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

    public String getUserCode() {
        return userCode;
    }

    public void setUserCode(String userCode) {
        this.userCode = userCode;
    }

    public Integer getAdminType() {
        return adminType;
    }

    public void setAdminType(Integer adminType) {
        this.adminType = adminType;
    }

    public String getAdminTypeStr() {
        return adminTypeStr;
    }

    public void setAdminTypeStr(String adminTypeStr) {
        this.adminTypeStr = adminTypeStr;
    }
}
