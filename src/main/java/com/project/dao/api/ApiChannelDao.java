package com.project.dao.api;

import java.util.List;

import org.springframework.stereotype.Component;

import com.project.core.orm.hibernate.HibernateDao;
import com.project.entity.api.ApiChannel;

@Component
public class ApiChannelDao extends HibernateDao<ApiChannel, Long> {

	public List<ApiChannel> findall() {
		return this.getAll();
	}

}
