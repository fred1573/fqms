package com.project.service.proxysale;

import com.project.bean.vo.AjaxResult;
import com.project.entity.proxysale.ProxyInn;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;

/**
 * 重构关房有关的业务逻辑实现
 * Created by sam on 2015/12/10.
 */
@Component
@Transactional
public interface OnOffRoomService {
    /**
     * 根据客栈ID获取客栈的关房记录
     *
     * @param innId
     * @return
     */
    AjaxResult getInnCloseInfo(Integer innId);

    /**
     * 区域批量关房
     * @param closeInfo
     * @return
     */
    AjaxResult areaOff(String closeInfo);

    /**
     * 客栈关房
     * @param closeInfo
     * @return
     */
    AjaxResult innOff(String closeInfo);

    /**
     * 客栈上架前，自动继承全国和归属地的有效关房记录
     *
     * @param proxyInn
     */
    boolean preOnShelf(ProxyInn proxyInn);

    /**
     * 批量开房
     * @param closeBeginDate
     * @param closeEndDate
     * @return
     */
    AjaxResult batchOpenRoom(String closeBeginDate, String closeEndDate);

    /**
     * 从消息中心处理分销商关房失败的对象
     * @param content
     */
    void processChannelFailRoomOff(String content);

    /**
     * 从消息中心处理OMS关房失败的对象
     * @param content
     */
    void processOmsFailRoomOff(String content);
}
