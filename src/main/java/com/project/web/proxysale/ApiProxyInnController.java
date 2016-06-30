package com.project.web.proxysale;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.project.bean.bo.RelationInnBo;
import com.project.bean.vo.AjaxBase;
import com.project.bean.vo.AjaxResult;
import com.project.common.Constants;
import com.project.entity.proxysale.PriceStrategy;
import com.project.entity.proxysale.ProxyInn;
import com.project.entity.proxysale.ProxysaleChannel;
import com.project.service.proxysale.OtaHelper;
import com.project.service.proxysale.PricePatternService;
import com.project.service.proxysale.ProxyInnService;
import com.project.service.proxysale.ProxysaleChannelService;
import com.project.web.BaseController;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.math.BigDecimal;
import java.util.*;

/**
 * Created by Administrator on 2015/6/11.
 */
@Controller
@RequestMapping("/proxysale/api/inn")
public class ApiProxyInnController extends BaseController {

    private static final Logger LOGGER = LoggerFactory.getLogger(ApiProxyInnController.class);

    @Autowired
    private ProxyInnService proxyInnService;
    @Autowired
    private PricePatternService pricePatternService;
    @Autowired
    private OtaHelper otaHelper;
    @Autowired
    private ProxysaleChannelService proxysaleChannelService;


    /**
     * 返回客栈开通的渠道信息
     *
     * @param innId
     */
    @RequestMapping("/channel")
    @ResponseBody
    public AjaxProxyInnWithChannels getChannelByInnId(Integer innId) {
        try {
//            List<Map<String, Object>> result = proxyInnService.packChannel(innId);
            Map<String, List<Map<String, Object>>> channels = proxyInnService.getProxyInnChannel(null, null, innId);
            ProxyInn proxyInn = proxyInnService.findByInnId(innId);
            return new AjaxProxyInnWithChannels(Constants.HTTP_OK, channels, BigDecimal.valueOf(proxyInn.getSalePercentage()));
        } catch (Exception e) {
            return new AjaxProxyInnWithChannels(Constants.HTTP_OK, e.getMessage());
        }
    }

    /**
     * 客栈代销开通、编辑接口
     *
     * @param proxyInnFormAdd
     * @return
     */
    @RequestMapping(value = "/add", method = RequestMethod.POST)
    @ResponseBody
    public synchronized AjaxBase add(ProxyInnFormAdd proxyInnFormAdd) {
        LOGGER.info("-----------------------source data:" + proxyInnFormAdd.toString() + "--------------------");
        try {
            proxyInnService.add(proxyInnFormAdd);
            return new AjaxBase(Constants.HTTP_OK, "");
        } catch (Exception e) {
            LOGGER.error("转换开通客栈对象proxyInnFormAdd时异常, error:" + e.getMessage());
            return new AjaxBase(Constants.HTTP_500, e.getMessage());
        }
    }

    @RequestMapping("/query")
    @ResponseBody
    public AjaxProxyInn query(@RequestParam("channelId") Integer channelId) {
        List<RelationInnBo> relationInns = proxysaleChannelService.findRelationInn(channelId);
        List<ApiProxyInn> apis = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(relationInns)) {
            Integer lastInnId = relationInns.get(0).getInn_id();
            Set<ApiPricePattern> apiPricePatterns = new HashSet<>();
            for (RelationInnBo relationInnBo : relationInns) {
                ApiPricePattern app = new ApiPricePattern(relationInnBo.getPattern().toString(), relationInnBo.getOuter_id());
                int innId = relationInnBo.getInn_id();

                if (lastInnId == innId) {
                    apiPricePatterns.add(app);
                    apis.add(new ApiProxyInn(innId, apiPricePatterns));
                } else {
                    apiPricePatterns = new HashSet<>();
                    apiPricePatterns.add(app);
                    apis.add(new ApiProxyInn(innId, apiPricePatterns));
                    lastInnId = innId;
                }
            }
        }
        return new AjaxProxyInn(Constants.HTTP_OK, null, apis);
    }

    @RequestMapping(value = "/percentage", method = RequestMethod.GET)
    @ResponseBody
    public AjaxBase getPercentage(@RequestParam("innId") Integer innId,
                                  @RequestParam(value = "accountId", required = false) Integer accountId) {
        try {
            ProxyInn proxyInn = proxyInnService.findByInnId(innId);
            Float salePercentage = proxyInn.getSalePercentage();
            return new AjaxProxyInnPercentage(Constants.HTTP_OK, salePercentage);
        } catch (Exception e) {
            return new AjaxBase(Constants.HTTP_500, e.getMessage());
        }
    }

    /**
     * 根据accountId获取关联的渠道
     */
    @RequestMapping("/acc")
    @ResponseBody
    public AjaxRelationChannelByAccountId getRelationOtaByAccountId(@RequestParam("acc") String accIdsJson) {
        if (StringUtils.isBlank(accIdsJson)) {
            LOGGER.error("入参acc为空");
            return new AjaxRelationChannelByAccountId(Constants.HTTP_500, "acc必须传啊亲");
        }
        JSONArray accs = JSON.parseArray(accIdsJson);
        List<ApiAcc2OTA> apiAcc2OTAs = new ArrayList<>();
        for (int i = 0; i < accs.size(); i++) {
            ApiAcc2OTA apiAcc2OTA = new ApiAcc2OTA();
            Integer accId = accs.getInteger(i);
            apiAcc2OTA.setAccId(accId);
            List<Integer> otas;
            try {
                otas = pricePatternService.findRelationOtaByAccountId(accId);
            } catch (Exception e) {
                String error = "根据accountId获取对应的ota异常，exception info: " + e.getMessage();
                LOGGER.error(error);
                return new AjaxRelationChannelByAccountId(Constants.HTTP_500, error);
            }
            if (CollectionUtils.isNotEmpty(otas)) {
                apiAcc2OTA.setOtas(otas);
            }
            apiAcc2OTAs.add(apiAcc2OTA);
        }
        return new AjaxRelationChannelByAccountId(Constants.HTTP_OK, apiAcc2OTAs);
    }

    @RequestMapping(value = "/batch_ota")
    @ResponseBody
    public AjaxOTALink getOTALink(@RequestParam("inns") String innsJson) {
        List<ApiOTALink> otaLinks = new ArrayList<>();
        Map<Integer, String> otaLinkMaps;
        try {
            otaLinkMaps = proxyInnService.getOTALinks(innsJson);
        } catch (Exception e) {
            return new AjaxOTALink(Constants.HTTP_500, e.getMessage());
        }
        for (Integer innId : otaLinkMaps.keySet()) {
            otaLinks.add(new ApiOTALink(innId, otaLinkMaps.get(innId)));
        }
        return new AjaxOTALink(Constants.HTTP_OK, otaLinks);
    }

    /**
     * 根据渠道ID和销售模式获取可卖客栈
     */
    @RequestMapping(value = "/relation_by_ota")
    @ResponseBody
    public AjaxRelation getRelationByOtaAndStrategy(@RequestParam("otaId") Integer otaId,
                                                    @RequestParam("strategy") String strategyStr) {
        List<Map<String, Object>> content = new ArrayList<>();
        Short strategy = otaHelper.convertStrategy(strategyStr);
        if (strategy == null) {
            return new AjaxRelation(Constants.HTTP_OK, content);
        }
        List<ProxysaleChannel> pcs = proxysaleChannelService.findValidByChannelId(otaId, strategy);
        if (CollectionUtils.isNotEmpty(pcs)) {
            for (ProxysaleChannel pc : pcs) {
                if (pc == null || !pc.getValid()) {
                    continue;
                }
                Map<String, Object> innAccMap = new HashMap<>();
                if (strategy.equals(PriceStrategy.STRATEGY_BASE_PRICE) && strategy.equals(pc.getStrategy())) {
                    innAccMap.put("innId", pc.getProxyInn().getInn());
                    innAccMap.put("accountId", pc.getProxyInn().getBaseOuterId());
                } else if ((strategy.equals(PriceStrategy.STRATEGY_SALE_PRICE) || strategy.equals(PriceStrategy.STRATEGY_SALE_BASE_PRICE)) && strategy.equals(pc.getStrategy())) {
                    innAccMap.put("innId", pc.getProxyInn().getInn());
                    innAccMap.put("accountId", pc.getProxyInn().getSaleOuterId());
                } else {
                    continue;
                }
                content.add(innAccMap);
            }
        }
        return new AjaxRelation(Constants.HTTP_OK, content);
    }

    @RequestMapping("/heartbeat")
    @ResponseBody
    public AjaxResult autoBuildHeartbeat() {
        return new AjaxResult(200, "");
    }

    private class AjaxRelation extends AjaxBase {
        private List<Map<String, Object>> content;

        public AjaxRelation(int status, List<Map<String, Object>> content) {
            super(status);
            this.content = content;
        }

        public List<Map<String, Object>> getContent() {
            return content;
        }
    }

    private class AjaxOTALink extends AjaxBase {

        private List<ApiOTALink> otaLinks = new ArrayList<>();

        public AjaxOTALink(int status, List<ApiOTALink> otaLinks) {
            super(status);
            this.otaLinks = otaLinks;
        }

        public AjaxOTALink(int status, String message) {
            super(status, message);
        }

        public List<ApiOTALink> getOtaLinks() {
            return otaLinks;
        }

        public void setOtaLinks(List<ApiOTALink> otaLinks) {
            this.otaLinks = otaLinks;
        }
    }

    private class AjaxProxyInnPercentage extends AjaxBase {

        private Float percentage;

        public AjaxProxyInnPercentage(int status, Float percentage) {
            super(status);
            this.percentage = percentage;
        }

        public Float getPercentage() {
            return percentage;
        }

        public void setPercentage(Float percentage) {
            this.percentage = percentage;
        }
    }

    private class AjaxProxyInn extends AjaxBase {

        private Float percentage;

        private List<ApiProxyInn> proxyInns;

        public AjaxProxyInn(int status, String message) {
            super(status, message);
        }

        public AjaxProxyInn(int status, Float percentage, List<ApiProxyInn> apiProxyInns) {
            super(status);
            this.percentage = percentage;
            this.proxyInns = apiProxyInns;
        }

        public Float getPercentage() {
            return percentage;
        }

        public void setPercentage(Float percentage) {
            this.percentage = percentage;
        }

        public List<ApiProxyInn> getProxyInns() {
            return proxyInns;
        }

        public void setProxyInns(List<ApiProxyInn> proxyInns) {
            this.proxyInns = proxyInns;
        }
    }

    private class AjaxRelationChannelByAccountId extends AjaxBase {
        private List<ApiAcc2OTA> acc2OTAs;

        public AjaxRelationChannelByAccountId(int status, String message) {
            super(status, message);
        }

        public AjaxRelationChannelByAccountId(int status, List<ApiAcc2OTA> apiAcc2OTAs) {
            super(status);
            this.acc2OTAs = apiAcc2OTAs;
        }

        public List<ApiAcc2OTA> getAcc2OTAs() {
            return acc2OTAs;
        }

        public void setAcc2OTAs(List<ApiAcc2OTA> apiAcc2OTAs) {
            this.acc2OTAs = apiAcc2OTAs;
        }
    }

    private class AjaxProxyInnWithChannels extends AjaxBase {

        private Map<String, List<Map<String, Object>>> channels = new HashMap<>();
        private BigDecimal percentage = BigDecimal.ZERO;

        public AjaxProxyInnWithChannels(int status, String message) {
            super(status, message);
        }

        public AjaxProxyInnWithChannels(int status, Map<String, List<Map<String, Object>>> channels, BigDecimal percentage) {
            super(status);
            this.channels = channels;
            this.percentage = percentage;
        }

        public Map<String, List<Map<String, Object>>> getChannels() {
            return channels;
        }

        public void setChannels(Map<String, List<Map<String, Object>>> channels) {
            this.channels = channels;
        }

        public BigDecimal getPercentage() {
            return percentage;
        }

        public void setPercentage(BigDecimal percentage) {
            this.percentage = percentage;
        }
    }
}
