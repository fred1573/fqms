package com.project.dao.proxysale;

import com.project.core.orm.hibernate.SimpleHibernateDao;
import com.project.entity.proxysale.ProxysaleChannel;
import com.project.utils.CollectionsUtil;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * @author Hunhun
 *         2015-09-17 17:28
 */
@Component
public class ChannelInnDao extends SimpleHibernateDao<ProxysaleChannel, Integer> {

    public Long count(Integer channelId, Integer proxyInnId) {
        return countForLongWithSql("select pci.id from tomato_proxysale_channel_inn pci where pci.channel=? and pci.proxy_inn=?", channelId, proxyInnId);
    }

    /**
     * 查询客栈是否和指定渠道关联
     *
     * @param proxyId   代销客栈ID
     * @param strategy  价格策略
     * @param channelId 渠道ID
     * @return
     */
    public boolean isOnShelf(Integer proxyId, Short strategy, Integer channelId) {
        List<Map<String, Object>> data = findListMapWithSql("SELECT * FROM tomato_proxysale_channel_inn WHERE VALID = TRUE AND channel = '" + channelId + "' AND proxy_inn = '" + proxyId + "' AND strategy = '" + strategy + "' ORDER BY ID DESC LIMIT 1");
        if (CollectionsUtil.isNotEmpty(data)) {
            return true;
        }
        return false;
    }

    /**
     * 根据客栈ID和价格策略查询该客栈当前关联的渠道ID集合
     *
     * @param proxyInnId   客栈ID
     * @param priceStrategy 价格策略
     * @return 该客栈当前关联的渠道ID集合
     */
    public List<ProxysaleChannel> selectProxySaleChannelList(Integer proxyInnId, Short priceStrategy) {
        return findWithSql("SELECT * FROM tomato_proxysale_channel_inn WHERE VALID = TRUE AND proxy_inn = '" + proxyInnId + "' AND strategy = '" + priceStrategy + "'");
    }

    /**
     * 根据代销客栈ID、价格策略、渠道ID查询客栈渠道关联关系
     * @param proxyInnId 代销客栈ID
     * @param priceStrategy 价格策略
     * @param channelId 渠道ID
     * @return
     */
    public ProxysaleChannel selectProxySaleChannel(Integer proxyInnId, Short priceStrategy, Integer channelId) {
        return findUniqueWithSql("SELECT * FROM tomato_proxysale_channel_inn WHERE VALID = TRUE AND proxy_inn = '" + proxyInnId + "' AND strategy = '" + priceStrategy + "' AND channel='" + channelId + "' order by create_time desc limit 1");
    }

    /**
     * 逻辑删除指定客栈指定价格模式的渠道关联关系
     *
     * @param proxyInnId   代销客栈ID
     * @param priceStrategy 价格策略
     */
    public void removeChannelInn(Integer proxyInnId, Short priceStrategy) {
        executeUpdateWithSql("update tomato_proxysale_channel_inn set valid=false where proxy_inn='" + proxyInnId + "' and strategy='" + priceStrategy + "'");
    }

    /**
     * 逻辑删除指定客栈指定价格模式和渠道关联关系
     * @param proxyInnId 代销客栈ID
     * @param priceStrategy 价格模式
     * @param channelId 渠道ID
     */
    public void removeChannelInn(Integer proxyInnId, Short priceStrategy, Integer channelId) {
        executeUpdateWithSql("update tomato_proxysale_channel_inn set valid=false where proxy_inn='" + proxyInnId + "' and strategy='" + priceStrategy + "' and channel='" + channelId + "'");
    }

}
