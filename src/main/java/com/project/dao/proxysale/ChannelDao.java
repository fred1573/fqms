package com.project.dao.proxysale;

import com.project.core.orm.hibernate.HibernateDao;
import com.project.entity.area.Area;
import com.project.entity.proxysale.Channel;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 *
 * Created by Administrator on 2015/6/5.
 */

@Component
public class ChannelDao extends HibernateDao<Channel, Integer> {

    public void update(Channel channel){
        getSession().update(channel);
    }

    public List<Channel> findByArea(Area area){
        String hql = "select c from Channel c inner join c.saleArea b where b.id=?";
        return find(hql, area.getId());
    }

    public Channel get(Integer id){
        return (Channel) getSession().get(Channel.class, id);
    }

}
