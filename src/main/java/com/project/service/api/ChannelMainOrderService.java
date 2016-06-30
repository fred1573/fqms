package com.project.service.api;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.project.bean.SearchChannelOrderBean;
import com.project.core.orm.Page;
import com.project.dao.api.ChannelMainOrderDao;
import com.project.entity.api.ChannelMainOrder;

/**
 * 
 * @author X
 *
 */

@Component
@Transactional
public class ChannelMainOrderService {
	
	@Autowired
	private ChannelMainOrderDao channelMainOrderDao;
	
	public List<ChannelMainOrder> findAll(){
		return channelMainOrderDao.findall();
	}
	
	public void getPage(Page<ChannelMainOrder> page, SearchChannelOrderBean searchBean){
		channelMainOrderDao.getPage(page, searchBean);
	}

	public ChannelMainOrder findById(Integer id) {
		return channelMainOrderDao.findUniqueBy("id", id);
	}
	
	public ChannelMainOrder findByOrderNo(String orderNo) {
		return channelMainOrderDao.findUniqueBy("channelOrderNo", orderNo);
	}

	public void update(ChannelMainOrder proMainOrder, String sysUser) {
		proMainOrder.setBalanceTime(new Date());
		channelMainOrderDao.save(proMainOrder);
	}
	
	public void removeUpdate(ChannelMainOrder proMainOrder, String sysUser) {
		proMainOrder.setBalanceTime(null);
		channelMainOrderDao.save(proMainOrder);
	}
	
	public Double getTotalInComePrice(SearchChannelOrderBean searchBean){
		return channelMainOrderDao.getTotalInComePrice(searchBean);
	}
	
	public Double getTotalSalePrice(SearchChannelOrderBean searchBean){
		return channelMainOrderDao.getTotalSalePrice(searchBean);
	}

}
