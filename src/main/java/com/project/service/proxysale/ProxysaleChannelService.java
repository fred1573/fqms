package com.project.service.proxysale;

import com.project.bean.bo.RelationInnBo;
import com.project.entity.proxysale.ProxysaleChannel;

import java.util.List;


public interface ProxysaleChannelService {


	List<ProxysaleChannel> findProxysaleChannelByChannel(Integer cid, Short strategy);

	List<ProxysaleChannel> findProxysaleChannelByProxyInnId(Integer innId, Short strategy);

	void  delete(Integer id);

	void  update(ProxysaleChannel proxysaleChannel);

	/**
	 * 根据代销客栈ID查询有效的关联
	 * @param proxyId
	 * @param strategies
	 * @return
	 */
	List<ProxysaleChannel> findValidByProxyId(Integer proxyId, Short... strategies);

	/**
	 * 根据渠道ID查询有效关联
	 * @param channelId
	 * @param strategies
	 * @return
	 */
	List<ProxysaleChannel> findValidByChannelId(Integer channelId, Short... strategies);

	/**
	 * 根据渠道ID,和oms标识accountId查询是否上架
	 * @param channelId
	 * @param accountId
	 * @return
	 */
	ProxysaleChannel findValidByChannelIdAndAccountId(Integer channelId, Integer accountId);

	/**
	 * 根据渠道ID查询有效关联的客栈信息，包括客栈ID、关联策略、accountId
	 * @param channelId
	 * @return
	 */
	List<RelationInnBo> findRelationInn(Integer channelId);
}
