package com.project.consumer.listener;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.project.consumer.event.UpdateInnEvent;
import com.project.dao.finance.FinanceInnSettlementInfoDao;
import com.project.dao.region.RegionDao;
import com.project.entity.area.Area;
import com.project.entity.inn.InnRegion;
import com.project.entity.proxysale.ProxyInn;
import com.project.service.area.AreaService;
import com.project.service.proxysale.ChannelInnRelation;
import com.project.service.proxysale.ProxyInnService;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;

/**
 * @author frd
 */
@Component
@Lazy(value = false)
@Transactional
public class UpdateInnListener implements ApplicationListener<UpdateInnEvent> {

    public static final Logger LOGGER = LoggerFactory.getLogger(UpdateInnListener.class);

    @Autowired
    private ProxyInnService proxyInnService;
    @Autowired
    private AreaService areaService;
    @Autowired
    private RegionDao regionDao;
    @Autowired
    private ChannelInnRelation channelInnRelation;
    @Autowired
    private FinanceInnSettlementInfoDao financeInnSettlementInfoDao;

    @Override
    public void onApplicationEvent(UpdateInnEvent updateInnEvent) {
        String content = updateInnEvent.getSource().toString();
        LOGGER.info("--------------updateInnListener:" + content + "-----------");
        JSONObject jsonObject = JSON.parseObject(content);
        Integer innId = jsonObject.getInteger("innId");
        String innName = jsonObject.getString("brandName");
        if(innId != null && StringUtils.isNotBlank(innName)) {
            // 更新代销客栈结算信息
            financeInnSettlementInfoDao.updateFinanceInnSettlementInfo(innId, innName);
        }
        ProxyInn proxyInn = proxyInnService.findByInnId(innId);
        if(proxyInn == null) {
            LOGGER.error("【同步客栈信息】客栈不存在, innId:", innId);
            return;
        }
        String cityCode = jsonObject.getString("cityCode");
        Area city = areaService.getCityByCode(cityCode);
        if(city != null && !city.getId().equals(proxyInn.getArea().getId())) {
            proxyInn.setArea(city);
        }

        if(StringUtils.isNotBlank(innName) && !innName.equals(proxyInn.getInnName())) {
            proxyInn.setInnName(innName);
        }

        String innAddr = jsonObject.getString("addr");
        if(StringUtils.isNotBlank(innAddr) && !innAddr.equals(proxyInn.getInnAddr())) {
            proxyInn.setInnAddr(innAddr);
        }

        String innPhone = jsonObject.getString("frontPhone");
        if(StringUtils.isNotBlank(innPhone) && !innPhone.equals(proxyInn.getPhone())) {
            proxyInn.setPhone(innPhone);
        }

        Integer regionId = jsonObject.getInteger("destinationCode");
        InnRegion innRegion = regionDao.get(regionId);
        if(innRegion != null) {
            if(proxyInn.getInnRegion() == null
                    || !proxyInn.getInnRegion().getId().equals(innRegion.getId())) {
                proxyInn.setInnRegion(innRegion);
            }
        }
        //注意，这点非常重要，合并代码时不要把注释的代码恢复了
        /*ProxyInn newProxyInn = new ProxyInn();
        newProxyInn.setArea(proxyInn.getArea());
        newProxyInn.setPricePatterns(proxyInn.getPricePatterns());
        channelInnRelation.update(newProxyInn);
        proxyInnService.modifyBackend(proxyInn, new ArrayList<>(newProxyInn.getPcs()));*/
        proxyInnService.modify(proxyInn);
    }

}
