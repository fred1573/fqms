package com.project.dao.proxysale;

import com.project.core.orm.hibernate.HibernateDao;
import com.project.entity.proxysale.ProxyAudit;
import org.apache.commons.collections.CollectionUtils;
import org.hibernate.Query;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author zsl
 */
@Component("proxyAuditDao")
public class ProxyAuditDao extends HibernateDao<ProxyAudit, Integer> {

    public ProxyAudit getLast(Integer innId, Integer type, Short pattern) {
        StringBuilder hql = new StringBuilder();
        hql.append("from ProxyAudit pa where pa.innId=? and pa.type=?");
        if (type.intValue() == ProxyAudit.AUDIT_PRICE) {
            hql.append(" and pa.pattern=?");
        }
        hql.append(" order by pa.auditTime desc");
        Query query = getSession().createQuery(hql.toString()).setInteger(0, innId).setInteger(1, type);
        if (type.intValue() == ProxyAudit.AUDIT_PRICE) {
            query = query.setShort(2, pattern);
        }
        List list = query.setMaxResults(1).list();
        return CollectionUtils.isNotEmpty(list) ? (ProxyAudit) list.get(0) : null;
    }

    public void syncPriceRecords(ProxyAudit audit){
        ProxyAudit proxyAudit = findUniqueWithSql("select * from tomato_proxysale_audit pa where pa.type=1 and pa.record_no=?", audit.getRecordNo());
        if(proxyAudit != null){
            return;
        }
        save(audit);
    }

    public boolean hasCheckedPriceRecord(Integer innId, Short pattern){
        String sql = "select * from tomato_proxysale_audit pa where pa.inn_id=? and pa.type=1 and pa.status=? and pa.pattern=?";
        Long count = countForLongWithSql(sql, innId, ProxyAudit.STATUS_CHECKED, pattern);
        return !(count <= 0);
    }
    
    
    public boolean selectContractsRecoreds (Integer innId){
        String sql = "select * from tomato_proxysale_audit pa where pa.inn_id=?  and pa.status=?    and pa.type=2 ";
        Long count = countForLongWithSql(sql, innId,ProxyAudit.STATUS_CHECKED);
        return !(count <= 0);
    }

    public void saveOrUpdate(ProxyAudit proxyAudit){
        getSession().saveOrUpdate(proxyAudit);
    }

    public boolean hasExsitByRecordCode(String recordCode){
        String sql = "select * from tomato_proxysale_audit pa where pa.record_no=?";
        return countForLongWithSql(sql, recordCode) > 0;
    }
}
