package com.project.dao.proxysale;

import com.project.core.orm.hibernate.HibernateDao;
import com.project.entity.proxysale.PriceStrategy;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2015/7/6.
 */
@Component("priceStrategyDao")
public class PriceStrategyDao extends HibernateDao<PriceStrategy, Integer> {

    /**
     * 根据渠道ID查询有效的价格策略
     *
     * @param channelId
     * @return
     */
    public List<PriceStrategy> findValidByChannel(Integer channelId) {
        String sql = "select * from tomato_proxysale_price_strategy pps where pps.channel=? and pps.valid=true";
        return findWithSql(sql, channelId);
    }

    /**
     * 根据渠道ID查询有效的价格策略
     *
     * @param channelId
     * @return
     */
    public PriceStrategy findValidByChannelAndStrategy(Integer channelId, Short strategy) {
        String sql = "select * from tomato_proxysale_price_strategy pps where pps.channel=? and pps.strategy=? and pps.valid=true";
        return findUniqueWithSql(sql, channelId, strategy);
    }

    /**
     * 根据渠道ID和价格策略查询渠道的分佣/加价比例
     * 只返回最近的一条记录
     *
     * @param channelId 渠道ID
     * @param strategy  价格策略
     * @return
     */
    public PriceStrategy selectPriceStrategy(Integer channelId, Short strategy) {
        return findUniqueWithSql("SELECT * FROM tomato_proxysale_price_strategy WHERE valid=TRUE AND channel='" + channelId + "' AND strategy='" + strategy + "' ORDER BY create_time DESC LIMIT 1");
    }

    /**
     * 根据价格策略查询渠道
     *
     * @param strategy
     * @return
     */
    public List<PriceStrategy> selectPriceStrategy(Short strategy) {
        return findWithSql("select * from tomato_proxysale_price_strategy where valid=true and strategy='" + strategy + "'");
    }

    /**
     * 根据价格策略查询渠道开通的价格策略
     *
     * @param strategy
     * @return
     */
    public List<PriceStrategy> selectPriceStrategyByArea(Short strategy) {
        String sql = "select * from tomato_proxysale_price_strategy where valid=true";
        if (strategy.equals((short) 1)) {
            sql += " and strategy=1";
        } else if (strategy.equals((short) 2)) {
            sql += " and strategy in (2, 3)";
        }
        return findWithSql(sql);
    }


    public PriceStrategy findHistory(Integer channelId, Date time, Short strategy) {
        String hqlTime = "select max(pps.createTime) from PriceStrategy pps where pps.channel=? and pps.strategy=? and pps.createTime<=?";
        Object maxTime = findUnique(hqlTime, channelId, strategy, time);
        if (maxTime == null) {
            return null;
        }
        String hqlId = "select max(pps.id) from PriceStrategy pps where pps.createTime=? and pps.strategy=?";
        Integer maxId = findUnique(hqlId, maxTime, strategy);
        if (maxId == null) {
            return null;
        }
        PriceStrategy priceStrategy = (PriceStrategy) getSession().get(PriceStrategy.class, maxId);
        return priceStrategy;
    }

    public List<PriceStrategy> findvalidWithoutSale2Base(Integer channelId) {
        //ProxyStrategy.STRATEGY_SALE_BASE_PRICE = 3
        String sql = "select * from tomato_proxysale_price_strategy pps where pps.channel=? and pps.valid=true and pps.strategy<>3";
        return findWithSql(sql, channelId);
    }

    public void update(PriceStrategy strategy) {
        getSession().update(strategy);
    }

    /**
     * 查询开通卖价的渠道
     *
     * @return
     */
    public List<Map<String, Object>> findSaleChannel() {
        String sql = "SELECT distinct channel AS id from tomato_proxysale_price_strategy WHERE (strategy=2 OR strategy=3) AND valid=TRUE";
        return findListMapWithSql(sql);
    }

    /**
     * 批量调价根据目的地查询客栈accountId
     *
     * @return
     */
    public List<Map<String, Object>> findAccountId(Integer regionId) {
        String sql = "SELECT DISTINCT t2.outer_id AS accountid FROM tomato_proxysale_inn t1 LEFT JOIN tomato_proxysale_price_pattern t2 ON t1.id=t2.proxy_inn WHERE  t1.status='1' and t1.valid=TRUE AND t2.valid=TRUE AND t1.region=? AND  t2.pattern=2 ";
        return findListMapWithSql(sql, regionId);
    }

    /**
     * 查询目的地名
     *
     * @return
     */
    public Map<String, Object> findRegionName(Integer regionId) {
        String sql = "SELECT name as name from tomato_inn_region WHERE id=?";
        return findMapWithSql(sql, regionId);
    }
}
