package com.project.service.proxysale;

import com.project.bean.proxysale.PriceDetailQuery;
import com.project.entity.proxysale.ProxyInn;
import com.project.web.proxysale.ProxyInnFormAdd;
import com.project.web.proxysale.RoomDetailForm;

/**
 *
 * Created by Administrator on 2015/8/25.
 */
public interface ProxyInnBean {

    ProxyInn parse(ProxyInnFormAdd proxyInnFormAdd);

    void setPricePattern(ProxyInnFormAdd proxyInnFormAdd, ProxyInn proxyInn);

    Integer getOnshelfStatus(Integer innId);

    /**
     * 删除oms中对应的代销客栈
     * @param innId 客栈ID
     */
    void deleteInOMS(Integer innId);

    /**
     * 删除crm中对应的代销客栈
     * @param innId 客栈ID
     */
    void deleteInCRM(Integer innId);

    PriceDetailQuery parse(RoomDetailForm roomDetailForm);

    boolean isCanOnshelf(ProxyInn proxyInn, Short pattern);

    /**
     * 策略与模式转换
     */
    Short convertStrategy2Pattern(Short strategy);
}
