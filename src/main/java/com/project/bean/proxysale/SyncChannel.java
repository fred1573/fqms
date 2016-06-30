package com.project.bean.proxysale;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.project.common.ApiURL;
import com.project.common.Constants;
import com.project.core.utils.springsecurity.SpringSecurityUtil;
import com.project.dao.proxysale.ProxysaleChannelDao;
import com.project.entity.proxysale.*;
import com.project.utils.HttpUtil;
import com.project.utils.SystemConfig;
import com.project.utils.encode.MD5;
import com.project.web.proxysale.ApiPricePattern;
import com.tomato.mq.client.support.MQClientBuilder;
import com.tomato.mq.support.core.SysMessage;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.math.BigDecimal;
import java.util.*;

/**
 * Created by Administrator on 2015/7/7.
 */
public class SyncChannel {

    private static final Logger LOGGER = LoggerFactory.getLogger(SyncChannel.class);

    private ThreadPoolTaskExecutor poolTaskExecutor;
    @Autowired
    private ProxysaleChannelDao proxysaleChannelDao;

    public void setPoolTaskExecutor(ThreadPoolTaskExecutor poolTaskExecutor) {
        this.poolTaskExecutor = poolTaskExecutor;
    }

    /**
     * 客栈对指定渠道进行上下线操作
     *
     * @param proxyInn     代销客栈独享
     * @param pricePattern 价格侧率
     * @param isSj         是否是上架操作
     * @param channel      渠道对象
     */
    public void syncOnShelf(final ProxyInn proxyInn, Short pricePattern, boolean isSj, Channel channel) {
        String companyCode = channel.getCompanyCode();
        syncOmsUpdateInnSellStatus(proxyInn, pricePattern, channel.getId(), isSj);
        if (StringUtils.isBlank(companyCode)) {
            LOGGER.info(String.format("公司 %d 不推送关联的客栈", channel.getId()));
            return;
        }
        final Map<String, Object> values = getValues(proxyInn, isSj, channel);
        Set<PricePattern> validPatterns = proxyInn.getValidPatterns();
        List priceModelArray = new ArrayList<>();
        for (PricePattern pattern : validPatterns) {
            if (pattern.getPattern().equals(pricePattern)) {
                String pm = "";
                if (pricePattern.shortValue() == PricePattern.PATTERN_BASE_PRICE) {
                    pm = "DI";
                } else if (pricePattern.shortValue() == PricePattern.PATTERN_SALE_PRICE) {
                    pm = "MAI";
                }
                ApiPricePattern apiPricePattern = new ApiPricePattern(pm, pattern.getOuterId());
                priceModelArray.add(apiPricePattern);
            }
        }
        JSONArray jsonArray = new JSONArray(priceModelArray);
        values.put("priceModelJson", jsonArray.toJSONString());
        asyncRequest(values);
    }

    private void asyncRequest(final Map<String, Object> values) {
        MQClientBuilder.build().send(new SysMessage(Constants.MQ_PROJECT_IDENTIFICATION, Constants.MQ_EVENT_BIZTYPE_PROXY_INN_ONSHELF, new JSONObject(values).toJSONString()));
    }


    /**
     * 通知相关渠道下架该客栈信息
     *
     * @param proxyInn
     * @param pricePattern
     * @param isSj
     */
    public void syncOnShelf(final ProxyInn proxyInn, Short pricePattern, boolean isSj) {
        List<ProxysaleChannel> channels;
        if (pricePattern.equals(PricePattern.PATTERN_BASE_PRICE)) {
            channels = proxysaleChannelDao.findValidByProxyId(proxyInn.getId(), PriceStrategy.STRATEGY_BASE_PRICE);
        } else if (pricePattern.equals(PricePattern.PATTERN_SALE_PRICE)) {
            channels = proxysaleChannelDao.findValidByProxyId(proxyInn.getId(), PriceStrategy.STRATEGY_SALE_PRICE, PriceStrategy.STRATEGY_SALE_BASE_PRICE);
        } else {
            throw new RuntimeException("客栈模式异常");
        }
        if (CollectionUtils.isEmpty(channels)) {
            return;
        }
        for (ProxysaleChannel proxysaleChannel : channels) {
            syncOmsUpdateInnSellStatus(proxyInn, pricePattern, proxysaleChannel.getChannel().getId(), isSj);
            Channel channel = proxysaleChannel.getChannel();
            if (StringUtils.isBlank(channel.getCompanyCode())) {
                LOGGER.info(String.format("公司 %d 不推送关联的客栈", channel.getId()));
                continue;
            }
            final Map<String, Object> values = getValues(proxyInn, isSj, channel);
            Set<PricePattern> validPatterns = proxyInn.getValidPatterns();
            List priceModelArray = new ArrayList<>();
            analyPriceModel(pricePattern, validPatterns, priceModelArray);
            JSONArray jsonArray = new JSONArray(priceModelArray);
            values.put("priceModelJson", jsonArray.toJSONString());
            asyncRequest(values);
        }
    }

    private Map<String, Object> getValues(ProxyInn proxyInn, boolean isSj, Channel channel) {
        Map<String, Object> values = new HashMap<>();
        values.put("companyCode", channel.getCompanyCode());
        values.put("innId", proxyInn.getInn());
        values.put("sj", isSj);
        return values;
    }

    private void syncOmsUpdateInnSellStatus(ProxyInn proxyInn, Short pattern, Integer otaId, boolean valid) {
        Set<PricePattern> validPatterns = proxyInn.getValidPatterns();
        for (PricePattern validPattern : validPatterns) {
            if (validPattern.getPattern().equals(pattern)) {
                Map<String, Object> params = new HashMap<>();
                params.put("accountId", validPattern.getOuterId());
                params.put("innId", proxyInn.getInn());
                params.put("channelId", otaId);
                params.put("valid", valid ? 1 : 0);
                long currentMillis = System.currentTimeMillis();
                params.put("timestamp", currentMillis);
                params.put("otaId", Constants.OMS_PROXY_PID);
                params.put("signature", MD5.getOMSSignature(currentMillis));
                String response = HttpUtil.httpPost(new HttpUtil().buildUrl(SystemConfig.PROPERTIES.get(SystemConfig.OMS_URL), ApiURL.OMS_UPDATE_INN_SELL_STATUS, null), params, false);
                LOGGER.info("oms客栈[proxyInnId:" + proxyInn.getInn() + "]上下架同步接口调用结果:{}, 操作人：" + SpringSecurityUtil.getCurrentUserName(), response);
                JSONObject jsonObject = JSON.parseObject(response);
                if (!jsonObject.getInteger("status").equals(Constants.HTTP_OK)) {
                    String error = jsonObject.getString("message");
                    LOGGER.error(error);
                    throw new RuntimeException(error);
                }
                break;
            }
        }
    }

    public void syncCommission(final Channel channel, List<PriceStrategy> strategies) {
        final Map<String, Object> param = new HashMap<>();
        JSONObject jsonObject = new JSONObject();
        Map<String, BigDecimal> commissionMap = new HashMap<>();
        for (PriceStrategy strategy : strategies) {
            if (strategy.getPercentage() == null) {
                continue;
            }
            if (strategy.getStrategy().equals(PriceStrategy.STRATEGY_BASE_PRICE)) {
                commissionMap.put("DI", new BigDecimal(strategy.getPercentage()));
            } else if (strategy.getStrategy().equals(PriceStrategy.STRATEGY_SALE_PRICE)) {
                commissionMap.put("MAI", new BigDecimal(strategy.getPercentage()));
            } else if (strategy.getStrategy().equals(PriceStrategy.STRATEGY_SALE_BASE_PRICE)) {
                commissionMap.put("MAI2DI", new BigDecimal(strategy.getPercentage()));
            } else {
                LOGGER.error("未识别的策略,strategy:", strategy.getStrategy());
            }
        }
        if (commissionMap.size() == 0) {
            LOGGER.info("策略未有变更，无须同步");
            return;
        }
        jsonObject.put("commission", commissionMap);
        String companyCode = channel.getCompanyCode();
        if (companyCode == null) {
            //-------通知oms----------
            LOGGER.info("-----------------修改渠道信息通知OMS------------------");
            final String oms_commission_url = SystemConfig.PROPERTIES.get(SystemConfig.OMS_URL) + ApiURL.OMS_OTA_COMMISSION;
            jsonObject.put("companyCode", channel.getId());
            param.put("data", jsonObject.toJSONString());
            param.put("otaId", Constants.OMS_PROXY_PID);
            long ts = System.currentTimeMillis();
            param.put("timestamp", ts);
            param.put("signature", MD5.getOMSSignature(ts));
            poolTaskExecutor.execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        new HttpUtil().postForm(oms_commission_url, param, false);
                    } catch (Exception e) {
                        LOGGER.error("invoke channel commission update error, url" + oms_commission_url, e);
                    }
                }
            });
        } else {
            //-------通知toms----------
            LOGGER.info("-----------------修改渠道信息通知TOMS------------------");
            final String toms_commission_url = SystemConfig.PROPERTIES.get(SystemConfig.TOMS_URL) + ApiURL.TOMS_OTA_COMMISSION;
            jsonObject.put("companyCode", companyCode);
            param.put("param", jsonObject.toJSONString());
            poolTaskExecutor.execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        new HttpUtil().postForm(toms_commission_url, param, false);
                    } catch (Exception e) {
                        LOGGER.error("invoke channel commission update error, url" + toms_commission_url + ", channelId=" + channel.getId(), e);
                    }
                }
            });
        }
    }

    public void syncOnShelf(final ProxyInn proxyInn, Channel channel, Short pricePattern, boolean isSj) {
        if (channel == null || proxyInn == null) {
            return;
        }
        syncOmsUpdateInnSellStatus(proxyInn, pricePattern, channel.getId(), isSj);
        if (StringUtils.isBlank(channel.getCompanyCode())) {
            LOGGER.info(String.format("公司 %d 不推送关联的客栈", channel.getId()));
            return;
        }
        final Map<String, Object> values = getValues(proxyInn, isSj, channel);
        Set<PricePattern> validPatterns = proxyInn.getValidPatterns();
        List priceModelArray = new ArrayList<>();
        analyPriceModel(pricePattern, validPatterns, priceModelArray);
        JSONArray jsonArray = new JSONArray(priceModelArray);
        values.put("priceModelJson", jsonArray.toJSONString());
        asyncRequest(values);
    }

    private void analyPriceModel(Short pricePattern, Set<PricePattern> validPatterns, List priceModelArray) {
        for (PricePattern pattern : validPatterns) {
            if (pattern.getPattern().shortValue() == pricePattern.shortValue()) {
                String pm;
                if (pricePattern.shortValue() == PricePattern.PATTERN_BASE_PRICE) {
                    pm = "DI";
                } else if (pricePattern.shortValue() == PricePattern.PATTERN_SALE_PRICE) {
                    pm = "MAI";
                } else {
                    continue;
                }
                ApiPricePattern apiPricePattern = new ApiPricePattern(pm, pattern.getOuterId());
                priceModelArray.add(apiPricePattern);
            }
        }
    }

}
