package com.project.dao.proxysale;

import com.project.core.orm.hibernate.HibernateDao;
import com.project.entity.proxysale.PricePattern;
import com.project.entity.proxysale.ProxyInn;
import com.project.utils.CollectionsUtil;
import org.apache.commons.collections.CollectionUtils;
import org.hibernate.Query;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by Administrator on 2015/7/6.
 */
@Component("pricePatternDao")
public class PricePatternDao extends HibernateDao<PricePattern, Integer> {

    public PricePattern getByOuterId(Integer outerId) {
        return findUniqueBy("outerId", outerId);
    }

    /**
     * 根据代销客栈ID和价格策略查询客栈总抽佣比例
     *
     * @param proxyId  代销客栈ID
     * @param strategy 价格策略
     * @return
     */
    public PricePattern selectPricePattern(Integer proxyId, Short strategy) {
        List<PricePattern> pricePatternList = findWithSql("SELECT * FROM tomato_proxysale_price_pattern WHERE valid=TRUE AND proxy_inn='" + proxyId + "' AND pattern='" + strategy + "' ORDER BY id DESC LIMIT 1");
        if (CollectionsUtil.isNotEmpty(pricePatternList)) {
            return pricePatternList.get(0);
        }
        return null;
    }

    /**
     * 根据客栈ID和价格策略查询代销客栈的总抽佣比例
     *
     * @param innId    客栈ID
     * @param strategy 价格策略 1：精品，2：普通
     * @return 客栈有效的总抽佣比例
     */
    public Float selectPricePatternByInnId(Integer innId, Short strategy) {
        List<Map<String, Object>> dataMapList = findListMapWithSql("SELECT percentage FROM tomato_proxysale_inn tpi LEFT JOIN tomato_proxysale_price_pattern tppp ON tpi. ID = tppp.proxy_inn WHERE tppp. VALID = TRUE AND tppp.pattern = '" + strategy + "' AND tpi.inn = '" + innId + "'");
        if (CollectionsUtil.isNotEmpty(dataMapList)) {
            Map<String, Object> dataMap = dataMapList.get(0);
            if (dataMap != null && dataMap.size() > 0) {
                Object percentage = dataMap.get("percentage");
                if (percentage != null) {
                    return Float.parseFloat(String.valueOf(percentage));
                }
            }
        }
        return null;
    }

    public Set<PricePattern> batchFindByProxyInns(List<Integer> proxyInns, List<Short> pricePatterns) {
        Set<PricePattern> result = new HashSet<>();
        StringBuilder hql = new StringBuilder("select pp.proxyInn, pp.pattern, pp.percentage from PricePattern pp where pp.valid=true and pp.proxyInn.id in(:proxyInns)");
        if (CollectionUtils.isNotEmpty(pricePatterns)) {
            hql.append(" and pp.pattern in(:patterns)");
        }
        Query query = createQuery(hql.toString());
        query.setParameterList("proxyInns", proxyInns);
        if (CollectionUtils.isNotEmpty(pricePatterns)) {
            query.setParameterList("patterns", pricePatterns);
        }
        List<Object> list = query.list();
        if (CollectionUtils.isEmpty(list)) {
            return result;
        }
        for (Object o : list) {
            Object[] objects = (Object[]) o;
            result.add(new PricePattern((ProxyInn) objects[0], (Float) objects[2], (Short) objects[1], true));
        }
        return result;
    }

    /**
     * 查询卖价accountId
     *
     * @return
     */
    public List<Map<String, Object>> findAccountId() {
        String sql = "SELECT t.outer_id AS accountid,t.proxy_inn AS innid FROM tomato_proxysale_price_pattern t WHERE t.pattern=2 AND t.outer_id  NOTNULL AND t.proxy_inn NOTNULL";
        return findListMapWithSql(sql);
    }
}
