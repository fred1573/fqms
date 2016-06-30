package com.project.service.proxysale;

import com.project.core.orm.Page;
import com.project.core.orm.PropertyFilter;
import com.project.entity.area.Area;
import com.project.entity.proxysale.Channel;
import com.project.entity.proxysale.PriceStrategy;

import java.util.List;

/**
 *渠道代销服务
 * Created by Administrator on 2015/6/5.
 */

public interface ChannelService {

    /**
     * 添加渠道
     * @param channel
     */
    void add(Channel channel);

    /**
     *渠道修改
     * @param channel
     */
    void modify(Channel channel);

    void modify(Channel channel, PriceStrategy saleStrategy, PriceStrategy baseStrategy, PriceStrategy saleBaseStrategy);

    /**
     * get
     * @param id
     * @return
     */
    Channel get(Integer id);

    /**
     *
     * @param page
     * @param filters
     * @return channels
     */
    Page<Channel> find(Page<Channel> page, List<PropertyFilter> filters);

    /**
     * 根据客栈区域查询上线的渠道
     * @param area
     * @return
     */
    List<Channel> findValidByArea(Area area);

}
