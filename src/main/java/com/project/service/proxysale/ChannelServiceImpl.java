package com.project.service.proxysale;

import com.project.bean.proxysale.SyncChannel;
import com.project.core.orm.Page;
import com.project.core.orm.PropertyFilter;
import com.project.core.utils.springsecurity.SpringSecurityUtil;
import com.project.dao.account.HibernateUserDao;
import com.project.dao.finance.FinanceOperationLogDao;
import com.project.dao.proxysale.ChannelDao;
import com.project.dao.proxysale.ProxysaleChannelDao;
import com.project.entity.account.User;
import com.project.entity.area.Area;
import com.project.entity.finance.FinanceOperationLog;
import com.project.entity.ota.OtaInfo;
import com.project.entity.proxysale.Channel;
import com.project.entity.proxysale.PriceStrategy;
import com.project.entity.proxysale.ProxysaleChannel;
import com.project.service.account.AccountService;
import com.project.service.ota.OtaInfoService;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.transaction.Transactional;
import java.util.*;

/**
 * Created by Administrator on 2015/6/5.
 */
@Service("channelService")
@Transactional
public class ChannelServiceImpl implements ChannelService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ChannelServiceImpl.class);

    @Autowired
    private ChannelDao channelDao;
    @Autowired
    private ProxyInnBean proxyInnBean;
    @Autowired
    private ChannelInnRelation channelInnRelation;
    @Autowired
    private PriceStrategyService priceStrategyService;
    @Autowired
    private AccountService accountService;
    @Autowired
    private SyncChannel syncChannel;
    @Autowired
    private ProxyInnService proxyInnService;
    @Autowired
    private ProxysaleChannelDao proxysaleChannelDao;
    @Autowired
    private PriceStrategyBean priceStrategyBean;
    @Autowired
    private FinanceOperationLogDao financeOperationLogDao;
    @Resource
    private OtaInfoService otaInfoService;
    @Autowired
    private HibernateUserDao hibernateUserDao;

    @Override
    public void add(Channel channel) {
        channelDao.save(channel);
    }

    @Override
    public void modify(Channel channel) {
        LOGGER.info("-----trace------modify-------");
        check(channel);
        channel.setUpdateTime(new Date());
        channel.setOperator(getCurrentUser());

        //旧的分销商与客栈的对应关系
        List<ProxysaleChannel> oldPCs = proxysaleChannelDao.findValidByChannelId(channel.getId(), null);
        List<PriceStrategy> strategies = priceStrategyService.findValidByChannel(channel.getId());
        if (CollectionUtils.isEmpty(strategies)) {
            return;
        }

        //更新
        channelInnRelation.update(channel, strategies);

        //更新后分销商与客栈的对应关系
        Set<ProxysaleChannel> newPCs = channel.getPcs();

        Map<String, Collection<ProxysaleChannel>> commonAndDiff = channelInnRelation.getCommonAndDiff(oldPCs, new ArrayList<>(newPCs));

        //直接干掉，就是干
        Collection<ProxysaleChannel> oldDiffs = commonAndDiff.get(ChannelInnRelation.OLD_DIFFS);
        //直接添加
        Collection<ProxysaleChannel> newDiffs = commonAndDiff.get(ChannelInnRelation.NEW_DIFFS);

        for (ProxysaleChannel pc : oldDiffs) {
            syncChannel.syncOnShelf(pc.getProxyInn(), channel, proxyInnBean.convertStrategy2Pattern(pc.getStrategy()), false);
            pc.setValid(false);
            proxysaleChannelDao.save(pc);
        }
        for (ProxysaleChannel pc : newDiffs) {
            syncChannel.syncOnShelf(pc.getProxyInn(), channel, proxyInnBean.convertStrategy2Pattern(pc.getStrategy()), true);
            proxysaleChannelDao.save(pc);
        }
    }

    @Override
    public void modify(Channel channel, PriceStrategy saleStrategy, PriceStrategy baseStrategy, PriceStrategy saleBaseStrategy) {
        priceStrategyBean.add(baseStrategy, saleStrategy, saleBaseStrategy);
        StringBuilder content = new StringBuilder();

        content.append("渠道ID[");
        content.append(channel.getId());
        content.append("]修改后:");
        Float base = baseStrategy.getPercentage();
        if (base != null) {
            content.append(" 底价加价比例：");
            content.append(base);
        }
        Float sale = saleStrategy.getPercentage();
        if (sale != null) {
            content.append(" 卖价分佣比例：");
            content.append(sale);
        }
        Float saleBase = saleBaseStrategy.getPercentage();
        if (saleBase != null) {
            content.append(" 卖转底加价比例：");
            content.append(saleBase);
        }
        content.append(" 销售区域: ");
        Set<Area> saleArea = channel.getSaleArea();
        if (null != saleArea && !saleArea.isEmpty()) {
            for (Area area : saleArea) {
                content.append(area.getName());
                content.append(";");
            }
        }
        // 通过OMS接口查询渠道名称
        OtaInfo otaInfo = otaInfoService.getByOtaId(channel.getId());
        String channelName = "";
        if (otaInfo != null) {
            channelName = otaInfo.getName();
        }
        financeOperationLogDao.save(new FinanceOperationLog("101", channelName, content.toString(), proxyInnService.getCurrentUser().getSysUserName()));
        modify(channel);
    }

    @Override
    public Channel get(Integer id) {
        return channelDao.get(id);
    }

    @Override
    public Page<Channel> find(Page<Channel> page, List<PropertyFilter> filters) {
        return channelDao.findPage(page, filters);
    }

    private void check(Channel channel) {
        Set<Area> saleArea = channel.getSaleArea();
        if (CollectionUtils.size(saleArea) <= 0 || CollectionUtils.size(saleArea) > 10) {
            throw new RuntimeException("销售区域数量应界于1-10之间");
        }
    }

    @Override
    public List<Channel> findValidByArea(Area area) {
        List<Channel> results = new ArrayList<>();
        getRecursiveChannels(area, results);
        return results;
    }

    /**
     * 递归获取渠道
     */
    private void getRecursiveChannels(Area area, List<Channel> results) {
        boolean over = false;
        if (area.getId() == 1) {
            over = true;
        }
        List<Channel> channels = channelDao.findByArea(area);
        results.addAll(channels);
        //全国范围查询后结束
        if (!over) {
            getRecursiveChannels(area.getParent(), results);
        }
    }

    private User getCurrentUser() {
        String userName = SpringSecurityUtil.getCurrentUserName();
        return hibernateUserDao.findUserByUserCode(userName);
    }

}
