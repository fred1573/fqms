package com.project.dao.proxysale;

import com.project.core.orm.Page;
import com.project.core.orm.hibernate.HibernateDao;
import com.project.entity.proxysale.ProxyInn;
import com.project.entity.proxysale.ProxyInnDelLog;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.Query;
import org.springframework.stereotype.Component;

import java.math.BigInteger;
import java.util.List;

/**
 *
 * Created by Administrator on 2015/6/9.
 */
@Component
public class ProxyInnDao extends HibernateDao<ProxyInn, Integer> {

    public void update(ProxyInn proxyInn) {
        getSession().update(proxyInn);
    }

    public Page<ProxyInn> find(Page<ProxyInn> page, Integer areaId, String innName, Integer status) {
        String sql;
        Page<ProxyInn> result;
        StringBuilder sqlBuilder = new StringBuilder(" select ia.inn, ia.\"id\", ia.create_time, ia.available_room_num, ia.edit_time, ia.base_price_onoff_time, ia.sale_price_onoff_time, ia.status, ia.\"valid\", ia.edit_operator, ia.onoff_operator, ia.failed_reason, ia.area, ia.inn_name, ia.inn_addr, ia.ota_link, ia.phone, ia.region  from tomato_proxysale_inn ia ");
        boolean hasStatus = false;
        sqlBuilder.append(" where ia.valid=true ");
        if(StringUtils.isNotBlank(innName)){
            sqlBuilder.append(" and ia.inn_name like '%" + innName + "%' ");
        }
        if(status != null){
            hasStatus = true;
            sqlBuilder.append(" and ia.status=? ");
        }
        if(areaId != null) {
            sqlBuilder.append("AND (ia.area = " + areaId + ")");
        }
        sqlBuilder.append(" order by create_time desc ");
        sql = sqlBuilder.toString();
        if(hasStatus){
            result = findPageWithSql(page, sql, status);
        }else{
            result = findPageWithSql(page, sql);
        }
        return result;
    }
    
    /**
     * 根据区域ID查询代销客栈对象集合
     * @param areaId 区域ID
     * @return
     */
    public List<ProxyInn> findByAreaId(Integer areaId) {
        return findWithSql("SELECT * FROM tomato_proxysale_inn t1 INNER JOIN tomato_base_area t2 ON t1.area = t2. ID WHERE t1.valid=true and (t2. ID = " + areaId + " OR t2.parent = " + areaId + ")");
    }

    /**
     * 根据区域ID和状态集合查询满足条件的代销客栈
     * @param areaId 区域ID
     * @param status 状态集合，如果是多个使用逗号分隔
     * @return 满足查询条件的代销客栈集合
     */
    public List<ProxyInn> findByAreaId(Integer areaId, String status) {
        StringBuilder sql = new StringBuilder("SELECT * FROM tomato_proxysale_inn t1 INNER JOIN tomato_base_area t2 ON t1.area = t2. ID WHERE t1.valid=true AND t1.status in (" + status + ")");
        if(areaId != 1) {
            sql.append(" and (t2. ID = " + areaId + " OR t2.parent = " + areaId + ")");
        }
        return findWithSql(sql.toString());
    }

    /**
     * 根据PMS客栈ID查询代销客栈对象
     * @param innId PMS客栈ID
     * @return 代销客栈对象
     */
    public ProxyInn findByInnId(Integer innId){
        return findUniqueWithSql("select * from tomato_proxysale_inn pi where pi.inn=? and pi.valid=true", innId);
    }

    /**
     * 根据PMS客栈id查询更新时间最新的代销客栈对象
     * @param innId PMS客栈ID
     * @return
     */
    public ProxyInn selectProxyInnByInnId(Integer innId) {
        return findUniqueWithSql("select * from tomato_proxysale_inn pi where pi.inn=? ORDER BY id DESC LIMIT 1", innId);
    }

    /**
     * 查询全部代销客栈
     * @return 代销客栈集合
     */
    public List<ProxyInn> findAll() {
        return findWithSql("select * from tomato_proxysale_inn pi where pi.valid=true");
    }

    
    public  Object[]  getUpAndDownSummary(Integer areaId,Integer inn,Boolean isToday){
		StringBuilder recordCount = new StringBuilder("select   coalesce(sum(case when tp.pattern=1  and tp.operate_type='ON' then 1 end ),0) as base_on ,  "+
   " coalesce(sum(case when tp.pattern=1 and tp.operate_type='OFF' then 1 end ),0 )as base_off , "+  
   " coalesce(sum(case when tp.pattern=2 and  tp.operate_type='ON'  then 1 end ),0) as sale_on , "+ 
   " coalesce(sum(case when tp.pattern=2 and tp.operate_type='OFF'   then 1 end ),0) as sale_off  from "
				+ " tomato_proxysale_inn_onoff  tp  JOIN  tomato_proxysale_inn "
				+ " t on (  tp.proxy_inn = t.id  ) join   tomato_base_area ta ON ( t.area = ta. id)  where  1=1  " );
		if(isToday){
			recordCount.append("  AND  tp.time >= current_date   ");
		}
		if(null!=areaId){
			recordCount.append(" AND (ta. id = " + areaId + " or ta.parent = " + areaId + ")");
		}
		if(null!=inn){
			recordCount.append(" AND  tp.proxy_inn =   "+inn);
		}
		Query query =   getSession().createSQLQuery(recordCount.toString());
		return (Object[]) query.list().get(0);
    }

    public  List  getHostelSummary(Integer areaId){
        StringBuilder sql = new StringBuilder(" select ia.status FROM ProxyInn ia WHERE ia.valid = TRUE");
        if(null!=areaId){
            sql.append(" AND (ia.area.id = " + areaId + ")");
        }
        return (List) find(sql.toString());
    }

    public void saveDelLog(ProxyInnDelLog proxyInnDelLog){
        getSession().save(proxyInnDelLog);
    }

    public Page<ProxyInnDelLog> findDelList(Page<ProxyInnDelLog> page) {
        String sql = "select * from tomato_proxysale_inn_del_log t";
        page = (Page<ProxyInnDelLog>) findPageWithSql(ProxyInnDelLog.class, page, sql);
        return page;
    }

    public Page findPriceUpdateInnList(Page page, String innName) {
        int pageSize = page.getPageSize();
        String limit = "limit " + pageSize + " offset " + (page.getPageNo()-1)* pageSize;
        StringBuilder sql = new StringBuilder().append("select DISTINCT t3.name as regionName, t1.inn_name as innName, t1.id as proxyInnId from ")
                .append("tomato_proxysale_inn t1 inner join tomato_proxysale_channel_inn t2 on t1.id=t2.proxy_inn left join tomato_inn_region t3 on t3.id=t1.region ")
                .append("where t1.status in(1,3) AND t1.inn_name LIKE '%"+ innName +"%' and t1.valid='t' and t2.valid='t' and t2.strategy in(2,3) order by t1.inn_name ");
        Object totalCount = getSession().createSQLQuery(String.format("select count(*) from (%s) as tt", sql)).uniqueResult();
        List list = getSession().createSQLQuery(sql.append(limit).toString()).list();
        page.setResult(list);
        page.setTotalCount(((BigInteger)totalCount).longValue());
        return page;
    }

    public Page findPriceUpdateInnList(Page page) {
        int pageSize = page.getPageSize();
        String limit = "limit " + pageSize + " offset " + (page.getPageNo()-1)* pageSize;
        StringBuilder sql = new StringBuilder().append("select DISTINCT t3.name as regionName, t1.inn_name as innName, t1.id as proxyInnId from ")
                .append("tomato_proxysale_inn t1 inner join tomato_proxysale_channel_inn t2 on t1.id=t2.proxy_inn left join tomato_inn_region t3 on t3.id=t1.region ")
                .append("where t1.status in(1,3) and t1.valid='t' and t2.valid='t' and t2.strategy in(2,3) order by t1.inn_name ");
        Object totalCount = getSession().createSQLQuery(String.format("select count(*) from (%s) as tt", sql.toString())).uniqueResult();

        List list = getSession().createSQLQuery(sql.append(limit).toString()).list();
        page.setResult(list);
        page.setTotalCount(((BigInteger)totalCount).longValue());
        return page;
    }
}
