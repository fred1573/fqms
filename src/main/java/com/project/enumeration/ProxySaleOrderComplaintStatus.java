package com.project.enumeration;

/**
 * 代销订单投诉处理状态
 *
 * @author yuneng.huang on 2016/6/13.
 */
public enum ProxySaleOrderComplaintStatus implements EnumDescription {
    //开始
    STARTED("开始"),
    //完成
    FINISH("完成");

    ProxySaleOrderComplaintStatus(String description) {
        this.description = description;
    }

    private String description;

    @Override
    public String getDescription() {
        return description;
    }
}
