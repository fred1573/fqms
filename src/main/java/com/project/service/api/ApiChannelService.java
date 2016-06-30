package com.project.service.api;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.project.cache.ApiChannelCache;
import com.project.cache.abstractCache.IDataCache;
import com.project.common.Constants;
import com.project.dao.api.ApiChannelDao;
import com.project.entity.api.ApiChannel;

/**
 * 
 * @author X
 *
 */

@Component
@Transactional
public class ApiChannelService {
	
	@Autowired
	private ApiChannelDao apiChannelDao;
	
	public List<ApiChannel> findAll(){
		return apiChannelDao.findall();
	}
	
	/**
	 * 获取所有第三方渠道
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<ApiChannel> getAllApiChannel() {
		IDataCache dataCache = new ApiChannelCache();
		return (List<ApiChannel>) dataCache.getOrElse(Constants.CACHE_FLAG_ALL_CHANNEL);
	}

	public void create(ApiChannel apiChannel) {
	}

}
