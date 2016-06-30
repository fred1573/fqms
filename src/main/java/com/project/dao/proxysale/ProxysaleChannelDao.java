package com.project.dao.proxysale;

import com.project.bean.bo.RelationInnBo;
import com.project.core.orm.hibernate.SimpleHibernateDao;
import com.project.entity.proxysale.ProxysaleChannel;
import org.hibernate.transform.Transformers;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;


@Component
public class ProxysaleChannelDao extends SimpleHibernateDao<ProxysaleChannel, Integer> {

	public List<ProxysaleChannel> findByProxyInnId(Integer innId, Short strategy) {
		List<Object> params = new ArrayList<Object>();
		params.add(innId);
		StringBuilder sql = new StringBuilder(" select * from tomato_proxysale_channel_inn  where proxy_inn = ? ");
		if (null != strategy) {
			sql.append("  and strategy = ? ");
			params.add(strategy);
		}
		return findWithSql(sql.toString(), params.toArray());
	}


	public List<ProxysaleChannel> findByChannelId(Integer cid, Short strategy) {

		List<Object> params = new ArrayList<Object>();
		params.add(cid);
		StringBuilder sql = new StringBuilder(" select * from tomato_proxysale_channel_inn  where channel = ? ");
		if (null != strategy) {
			sql.append("  and strategy = ? ");
			params.add(strategy);
		}
		return findWithSql(sql.toString(), cid);
	}


	public void delete(Integer id) {
		super.delete(id);
	}

	public List<ProxysaleChannel> findValidByProxyId(Integer innId, Short... strategies) {
		List<Object> params;
		params = new ArrayList<>();
		params.add(innId);
		StringBuilder sql = new StringBuilder(" select * from tomato_proxysale_channel_inn  where valid = true and proxy_inn = ? ");
		if (null != strategies) {
			StringBuffer sb = new StringBuffer();
			for (Short strategy : strategies) {
				sb.append(strategy).append(",");
			}
			String subStr = sb.substring(0, sb.lastIndexOf(","));
			sql.append("and strategy in (").append(subStr).append(")");
		}
		return findWithSql(sql.toString(), params.toArray());
	}

	public List<ProxysaleChannel> findValidByChannelId(Integer channelId, Short... strategies) {
		List<Object> params;
		params = new ArrayList<>();
		params.add(channelId);
		StringBuilder sql = new StringBuilder(" select * from tomato_proxysale_channel_inn  where valid = true and channel = ? ");
		if (null != strategies) {
			StringBuffer sb = new StringBuffer();
			for (Short strategy : strategies) {
				sb.append(strategy).append(",");
			}
			String subStr = sb.substring(0, sb.lastIndexOf(","));
			sql.append("and strategy in (").append(subStr).append(")");
		}
		return findWithSql(sql.toString(), params.toArray());
	}

	public List<RelationInnBo> findRelationInn(Integer channelId) {
		String sql = "SELECT t3.inn as inn_id, t2.pattern, t2.outer_id as outer_id FROM tomato_proxysale_channel_inn t1 INNER JOIN tomato_proxysale_price_pattern t2 ON t1.proxy_inn = t2.proxy_inn INNER JOIN tomato_proxysale_inn t3 ON t3.\"id\" = t2.proxy_inn WHERE t1.\"valid\" = 't' AND t3.status<>0 AND t1.channel = " + channelId + " AND t2.pattern = ( CASE WHEN t1.strategy IN (2, 3) THEN 2 ELSE 1 END ) ORDER BY t3.inn";
		List<RelationInnBo> result = getSession().createSQLQuery(sql).setResultTransformer(Transformers.aliasToBean(RelationInnBo.class)).list();
		return result;
	}

	public List<ProxysaleChannel> findValidByChannelIdAndInnId(Integer channelId, Integer innId ) {
		String sql = new String(" select * from tomato_proxysale_channel_inn  where valid = true and channel = ? and proxy_inn=? ");
		return findWithSql(sql.toString(),channelId,innId );
	}
}
