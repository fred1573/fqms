package com.project.service.proxysale;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.project.bean.proxysale.PriceDetailQuery;
import com.project.common.ApiURL;
import com.project.common.Constants;
import com.project.dao.area.AreaDao;
import com.project.dao.proxysale.ProxyAuditDao;
import com.project.dao.proxysale.ProxyInnDao;
import com.project.dao.region.RegionDao;
import com.project.entity.area.Area;
import com.project.entity.inn.InnRegion;
import com.project.entity.proxysale.PricePattern;
import com.project.entity.proxysale.ProxyInn;
import com.project.utils.HttpUtil;
import com.project.utils.SystemConfig;
import com.project.utils.encode.MD5;
import com.project.web.proxysale.ProxyInnFormAdd;
import com.project.web.proxysale.RoomDetailForm;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static com.project.entity.proxysale.PriceStrategy.STRATEGY_BASE_PRICE;
import static com.project.entity.proxysale.PriceStrategy.STRATEGY_SALE_BASE_PRICE;
import static com.project.entity.proxysale.PriceStrategy.STRATEGY_SALE_PRICE;

/**
 * Created by Administrator on 2015/8/26.
 */
@Component("proxyInnBean")
public class ProxyInnBeanImpl implements ProxyInnBean {

    private static Logger LOGGER = LoggerFactory.getLogger(ProxyInnBeanImpl.class);

    @Autowired
    private ProxyInnDao proxyInnDao;
    @Autowired
    private ProxyAuditDao proxyAuditDao;
    @Autowired
    private AreaDao areaDao;
    @Autowired
    private RegionDao regionDao;

    @Override
    public ProxyInn parse(ProxyInnFormAdd proxyInnFormAdd) {
        ProxyInn proxyInn;
        JSONObject jsonObject = JSON.parseObject(proxyInnFormAdd.getData());

        Integer innId = jsonObject.getInteger("innId");
        if (innId == null) {
            throw new RuntimeException("客栈ID不能为空");
        }
        if (proxyInnDao.findByInnId(innId) != null) {
            throw new RuntimeException("该客栈已开通了代销, 不能重复开通, innId=" + innId);
        }

        String cityCode = jsonObject.getString("cityCode");
        Area city;
        if (StringUtils.isBlank(cityCode) || (city = areaDao.getCityByCode(cityCode)) == null) {
            throw new RuntimeException("区域名不能为空或区域名未能正确匹配");
        }
        String innName = jsonObject.getString("brandName");
        if (StringUtils.isBlank(innName)) {
            throw new RuntimeException("客栈名称不能为空");
        }
        proxyInn = new ProxyInn();
        proxyInn.setInn(innId);
        proxyInn.setArea(city);
        proxyInn.setInnAddr(jsonObject.getString("addr"));
        proxyInn.setCreateTime(new Date());
        String innPhone = jsonObject.getString("frontPhone");
        if (StringUtils.isNotBlank(innPhone)) {
            proxyInn.setPhone(innPhone);
        }
        proxyInn.setInnName(innName.trim());
        setPricePattern(proxyInnFormAdd, proxyInn);
        proxyInn.setStatus(ProxyInn.STATUS_OFFSHELF);
        proxyInn.setValid(true);

        /**
         * 设置目的地
         */
        String regionId = jsonObject.getString("destinationCode");
        InnRegion innRegion = regionDao.get(Integer.valueOf(regionId));
        proxyInn.setInnRegion(innRegion);
        return proxyInn;
    }

    @Override
    public void setPricePattern(ProxyInnFormAdd proxyInnFormAdd, ProxyInn proxyInn) {
        String pricePatterns = proxyInnFormAdd.getPricePattern();
        String accountIds = proxyInnFormAdd.getAccountId();
        if (StringUtils.isBlank(pricePatterns)) {
            throw new RuntimeException("开通代销客栈,价格模式为空");
        }
        if (StringUtils.isBlank(accountIds)) {
            throw new RuntimeException("开通代销客栈,accountId不能为空");
        }
        String[] pricePatternArr = pricePatterns.split(",");
        String[] accountIdArr = accountIds.split(",");
        if (pricePatternArr.length != accountIdArr.length) {
            throw new RuntimeException("开通代销客栈,accountId与pricePattern不匹配");
        }
        for (int i = 0; i < pricePatternArr.length; i++) {
            String pricePattern = pricePatternArr[i];
            String accountId = accountIdArr[i];
            if (new Integer(pricePattern).shortValue() == PricePattern.PATTERN_BASE_PRICE) {
                proxyInn.makeBasePricePatternValid(Integer.parseInt(accountId));
            }
            if (new Integer(pricePattern).shortValue() == PricePattern.PATTERN_SALE_PRICE) {
                proxyInn.makeSalePricePatternValid(Integer.parseInt(accountId));
            }
        }
    }

    @Override
    public Integer getOnshelfStatus(Integer innId) {
        ProxyInn proxyInn = proxyInnDao.findByInnId(innId);
        boolean basePriceOnshelfed = proxyInn.isBasePriceOnshelfed();
        boolean salePriceOnshelfed = proxyInn.isSalePriceOnshelfed();
        if (basePriceOnshelfed && salePriceOnshelfed) {
            return ProxyInn.STATUS_ONSHELF;
        } else if (basePriceOnshelfed && !salePriceOnshelfed) {
            return ProxyInn.STATUS_SALE_OFFSHELF;
        } else if (!basePriceOnshelfed && salePriceOnshelfed) {
            return ProxyInn.STATUS_BASE_OFFSHELF;
        } else {
            return ProxyInn.STATUS_OFFSHELF;
        }
    }

    @Override
    public void deleteInOMS(Integer innId) {
        long currentMillis = System.currentTimeMillis();
        String sig = MD5.getOMSSignature(currentMillis);
        String url = SystemConfig.PROPERTIES.get(SystemConfig.OMS_URL) + ApiURL.OMS_INN_DEL;
        LOGGER.info("OMS移除客栈接口地址：" + url);
        Map<String, Object> kvs = new HashMap<>();
        kvs.put("innId", innId);
        kvs.put("otaId", Constants.OMS_PROXY_PID);
        kvs.put("timestamp", String.valueOf(currentMillis));
        kvs.put("signature", sig);
        LOGGER.info("OMS移除客栈接口请求参数：" + kvs);
        String resp = new HttpUtil().postForm(url, kvs);
        LOGGER.info("OMS移除客栈接口响应报文：" + resp);
        if (StringUtils.isBlank(resp)) {
            throw new RuntimeException("移除代销客栈调OMS接口 [" + url + "] 时异常, 接口返回:" + resp);
        }
        JSONObject jsonObject = JSON.parseObject(resp);
        if (!jsonObject.getBooleanValue("status")) {
            throw new RuntimeException("OMS接口错误：" + jsonObject.getString("message"));
        }
    }

    @Override
    public void deleteInCRM(Integer innId) {
        HttpUtil httpUtil = new HttpUtil();
        HashMap<String, String> param = new HashMap<>();
        param.put("pmsInnId", innId.toString());
        LOGGER.info("CRM移除客栈接口请求参数：" + param);
        String url = httpUtil.buildUrl(SystemConfig.PROPERTIES.get(SystemConfig.CRM_URL), ApiURL.CRM_INN_DEL, param);
        LOGGER.info("CRM移除客栈接口地址：" + url);
        String resp = httpUtil.get(url);
        LOGGER.info("CRM移除客栈接口响应报文：" + resp);
        if (StringUtils.isBlank(resp)) {
            throw new RuntimeException("移除代销客栈调CMS接口 [" + url + "] 时异常, 接口返回:" + resp);
        }
        JSONObject jsonObject = JSON.parseObject(resp);
        if (!jsonObject.getInteger("status").equals(Constants.HTTP_OK)) {
            throw new RuntimeException("CRM接口错误：" + jsonObject.getString("message"));
        }
    }

    @Override
    public PriceDetailQuery parse(RoomDetailForm roomDetailForm) {
        PriceDetailQuery query;
        Date from;
        Date to;
        try {
            String fromStr = roomDetailForm.getFrom();
            String toStr = roomDetailForm.getTo();
            if (StringUtils.isBlank(fromStr) || StringUtils.isBlank(toStr)) {
                Calendar calendar = Calendar.getInstance();
                from = calendar.getTime();
                calendar.add(Calendar.DAY_OF_YEAR, 15);
                to = calendar.getTime();
            } else {
                from = new SimpleDateFormat("yyyy-MM-dd").parse(fromStr);
                to = new SimpleDateFormat("yyyy-MM-dd").parse(toStr);
            }
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        query = new PriceDetailQuery(
                proxyInnDao.get(roomDetailForm.getProxyInnId()).getSaleOuterId(),
                roomDetailForm.getChannelId(),
                from,
                to
        );
        return query;
    }

    @Override
    public boolean isCanOnshelf(ProxyInn proxyInn, Short pattern) {
        Integer innId = proxyInn.getInn();
        // 没有合同也允许手动上架
        return proxyAuditDao.hasCheckedPriceRecord(innId, pattern)
                /*&& proxyAuditDao.selectContractsRecoreds(innId)*/;
    }

    @Override
    public Short convertStrategy2Pattern(Short strategy) {
        if (strategy.equals(STRATEGY_BASE_PRICE)) {
            return PricePattern.PATTERN_BASE_PRICE;
        } else if (strategy.equals(STRATEGY_SALE_PRICE) || strategy.equals(STRATEGY_SALE_BASE_PRICE)) {
            return PricePattern.PATTERN_SALE_PRICE;
        } else {
            throw new RuntimeException("策略异常, strategy=" + strategy);
        }
    }
}
