package com.project.service.proxysale;

import com.project.core.utils.springsecurity.SpringSecurityUtil;
import com.project.dao.account.HibernateUserDao;
import com.project.entity.account.User;
import com.project.entity.area.Area;
import com.project.entity.proxysale.*;
import com.project.service.account.AccountService;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 *
 * Created by Administrator on 2015/6/5.
 */
@Component
public class ChannelInnRelation {

    public static final String KEY_SPLIT = "-";
    public static final String OLD_DIFFS = "oldDiff";
    public static final String COMMONS = "common";
    public static final String NEW_DIFFS = "newDiff";
    @Autowired
    private ChannelService channelService;
    @Autowired
    private ProxyInnService proxyInnService;
    @Autowired
    private PriceStrategyService priceStrategyService;
    @Autowired
    private AccountService accountService;
    @Autowired
    private PricePatternService pricePatternService;
    @Autowired
    private HibernateUserDao hibernateUserDao;

    /**
     * 是否有交集
     *
     * @param priceStrategies
     * @param pricePatterns
     * @return
     */
    private boolean hasIntersection(List<PriceStrategy> priceStrategies, Set<PricePattern> pricePatterns) {
        for (PriceStrategy priceStrategy : priceStrategies) {
            for (PricePattern pricePattern : pricePatterns) {
                Short pattern = pricePattern.getPattern();
                Short strategy = priceStrategy.getStrategy();
                //模式:卖价 --->  策略:卖价&卖转底
                if (pattern.equals(PricePattern.PATTERN_SALE_PRICE)) {
                    if ((strategy.equals(PriceStrategy.STRATEGY_SALE_PRICE) || strategy.equals(PriceStrategy.STRATEGY_SALE_BASE_PRICE))
                            && pricePattern.getPercentage().compareTo(priceStrategy.getPercentage()) >= 0) {
                        //卖价或卖转底价匹配成功
                        return true;
                    }
                } else if (pattern.equals(PricePattern.PATTERN_BASE_PRICE)) {
                    if (strategy.equals(PriceStrategy.STRATEGY_BASE_PRICE)) {
                        //底价匹配成功
                        return true;
                    }
                } else {
                    throw new RuntimeException("价格模式异常");
                }
            }
        }
        return false;
    }

    /**
     * 当渠道信息变化时映射客栈信息
     * 规则：
     * 1.渠道的销售区域包含客栈所在的区域
     * 2.如果底价策略有效，则
     * 3.如果交集只包含卖价，则须总抽佣比例>=分佣比例
     *
     * @param channel
     */
    public void update(Channel channel, List<PriceStrategy> priceStrategies) {
        //渠道销售的区域
        Set<Area> channelSaleAreas = channel.getSaleArea();
        List<Integer> proxyInnsTemp = new ArrayList<>();
        for (Area channelArea : channelSaleAreas) {
            boolean isGlobal = false;
            if (channelArea.getId() == 1) {
                isGlobal = true;
                proxyInnsTemp.addAll(proxyInnService.findAll());
            } else {
                proxyInnsTemp.addAll(proxyInnService.findByArea(channelArea));
            }
            if (isGlobal) {
                break;
            }
        }
        if (CollectionUtils.isEmpty(proxyInnsTemp)) {
            channel.setPcs(new HashSet<ProxysaleChannel>());
            return;
        }
        process(channel, priceStrategies, proxyInnsTemp);
    }

    /**
     * 优化：
     * 迭代中通过hibernate级联单个查询效率太慢，改用sql一次查询
     *
     * @param channel
     * @param priceStrategies
     * @param proxyInnsTemp
     */
    private void process(Channel channel, List<PriceStrategy> priceStrategies, List<Integer> proxyInnsTemp) {
        Set<ProxysaleChannel> pcs = new HashSet<>();
        User user = hibernateUserDao.findUserByUserCode(SpringSecurityUtil.getCurrentUserName());
        //得到键为代销客栈，值为对应的有效的销售模式的map
        Set<PricePattern> pricePatterns = pricePatternService.batchFindValidPricePattern(proxyInnsTemp);
        pcs.addAll(getMatches(priceStrategies, pricePatterns, user, channel));
        channel.setPcs(pcs);
    }

    /**
     * 获取匹配关联
     *
     * @param priceStrategies
     * @param pricePatterns
     * @return
     */
    private List<ProxysaleChannel> getMatches(List<PriceStrategy> priceStrategies, Set<PricePattern> pricePatterns, User user, Channel channel) {
        List<ProxysaleChannel> results = new ArrayList<>();
        for (PriceStrategy priceStrategy : priceStrategies) {
            for (PricePattern pricePattern : pricePatterns) {
                Short pattern = pricePattern.getPattern();
                Short strategy = priceStrategy.getStrategy();
                //模式:卖价 --->  策略:卖价&卖转底
                if (pattern.equals(PricePattern.PATTERN_SALE_PRICE)) {
                    if ((strategy.equals(PriceStrategy.STRATEGY_SALE_PRICE) || strategy.equals(PriceStrategy.STRATEGY_SALE_BASE_PRICE))
                            && pricePattern.getPercentage().compareTo(priceStrategy.getPercentage()) >= 0) {
                        //卖价或卖转底价匹配成功
                        wrapMatch(results, pricePattern, strategy, user, channel);
                    }
                } else if (pattern.equals(PricePattern.PATTERN_BASE_PRICE)) {
                    if (strategy.equals(PriceStrategy.STRATEGY_BASE_PRICE)) {
                        //底价匹配成功
                        wrapMatch(results, pricePattern, strategy, user, channel);
                    }
                } else {
                    throw new RuntimeException("价格模式异常");
                }
            }
        }
        return results;
    }

    private void wrapMatch(List<ProxysaleChannel> results, PricePattern pricePattern, Short strategy, User user, Channel channel) {
        ProxysaleChannel pc = new ProxysaleChannel();
        pc.setChannel(channel);
        pc.setCreateTime(new Date());
        pc.setOperator(user);
        pc.setProxyInn(pricePattern.getProxyInn());
        pc.setStrategy(strategy);
        pc.setValid(true);
        results.add(pc);
    }

    private void wrapMatch(Set<ProxysaleChannel> results, Short strategy, Channel channel) {
        ProxysaleChannel pc = new ProxysaleChannel();
        pc.setChannel(channel);
        pc.setCreateTime(new Date());
        pc.setStrategy(strategy);
        results.add(pc);
    }

    /**
     * 当客栈信息变化时映射渠道信息
     * 规则同setInnAudit2Channel(Channel channel)
     *
     * @param proxyInn
     */
    public void update(ProxyInn proxyInn) {
        Area area = proxyInn.getArea();
        Set<PricePattern> pricePatterns = proxyInn.getValidPatterns();
        Set<ProxysaleChannel> pcs = new HashSet<>();
        List<Channel> channelTemp = channelService.findValidByArea(area);
        for (Channel channel : channelTemp) {
            List<PriceStrategy> priceStrategies = priceStrategyService.findValidByChannel(channel.getId());
            for (PriceStrategy priceStrategy : priceStrategies) {
                for (PricePattern pricePattern : pricePatterns) {
                    Short pattern = pricePattern.getPattern();
                    Short strategy = priceStrategy.getStrategy();
                    //模式:卖价 --->  策略:卖价&卖转底
                    if (pattern.equals(PricePattern.PATTERN_SALE_PRICE)) {
                        if ((strategy.equals(PriceStrategy.STRATEGY_SALE_PRICE) || strategy.equals(PriceStrategy.STRATEGY_SALE_BASE_PRICE))
                                && pricePattern.getPercentage().compareTo(priceStrategy.getPercentage()) >= 0) {
                            //卖价或卖转底价匹配成功
                            wrapMatch(pcs, strategy, channel);
                        }
                    } else if (pattern.equals(PricePattern.PATTERN_BASE_PRICE)) {
                        if (strategy.equals(PriceStrategy.STRATEGY_BASE_PRICE)) {
                            //底价匹配成功
                            wrapMatch(pcs, strategy, channel);
                        }
                    } else {
                        throw new RuntimeException("价格模式异常");
                    }
                }
            }
        }
        //  proxyInn.setChannels(channels);
        proxyInn.setPcs(pcs);
    }

    /**
     * 客栈编辑时获取关联的渠道
     */
    public List<Channel> getChannelsWithoutPersistence(ProxyInn proxyInn) {
        if (proxyInn == null) {
            throw new RuntimeException("客栈不存在");
        }
        Area area = proxyInn.getArea();
        Set<PricePattern> pricePatterns = proxyInn.getValidPatterns();
        List<Channel> channels = new ArrayList<>();
        List<Channel> channelTemp = channelService.findValidByArea(area);
        for (Channel channel : channelTemp) {
            List<PriceStrategy> priceStrategies = priceStrategyService.findValidByChannel(channel.getId());
            if (hasIntersection(priceStrategies, pricePatterns)) {
                channels.add(channel);
            }
        }
        return channels;
    }

    /**
     * 计算两组分销商-客栈关联关系的交集，old集合的差集，new集合的差离，此方法很强大
     * 采用map计数，优点：不用排序，算法简单；缺点：当集合数据量很大时，需占用很大空间
     * 重要的事情说三点，此方法很强大，此方法很强大
     */
    public Map<String, Collection<ProxysaleChannel>> getCommonAndDiff(List<ProxysaleChannel> olds, List<ProxysaleChannel> news) {
        //结果数据，存有交集，新旧两个差集
        Map<String, Collection<ProxysaleChannel>> result = new HashMap<>();
        //old差集
        Map<String, ProxysaleChannel> oldDiffs = new HashMap<>();
        //交集
        Map<String, ProxysaleChannel> commons = new HashMap<>();
        //new差集
        Map<String, ProxysaleChannel> newDiffs = new HashMap<>();

        if(CollectionUtils.isEmpty(olds)) {
            olds = new ArrayList<>();
        }
        if(CollectionUtils.isEmpty(news)) {
            news = new ArrayList<>();
        }

        //先将所有olds装进oldDiff
        for (ProxysaleChannel pc : olds) {
            oldDiffs.put(getMapKey(pc), pc);
        }

        for (ProxysaleChannel pc : news) {
            String key = getMapKey(pc);
            if (oldDiffs.containsKey(key)) {
                //有交集
                oldDiffs.remove(key);
                commons.put(key, pc);
            } else {
                //new差集
                newDiffs.put(key, pc);
            }
        }

        result.put(OLD_DIFFS, oldDiffs.values());
        result.put(COMMONS, commons.values());
        result.put(NEW_DIFFS, newDiffs.values());
        return result;
    }

    private String getMapKey(ProxysaleChannel pc) {
        return pc.getChannel().getId() + KEY_SPLIT + pc.getProxyInn().getId() + KEY_SPLIT + pc.getStrategy();
    }

}
