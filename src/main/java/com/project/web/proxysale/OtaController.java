package com.project.web.proxysale;

import com.project.bean.vo.AjaxResult;
import com.project.common.Constants;
import com.project.entity.area.Area;
import com.project.entity.ota.OtaInfo;
import com.project.entity.proxysale.Channel;
import com.project.entity.proxysale.PriceStrategy;
import com.project.service.area.AreaService;
import com.project.service.ota.OtaInfoService;
import com.project.service.proxysale.ChannelService;
import com.project.service.proxysale.PriceStrategyService;
import com.project.utils.HttpUtil;
import com.project.web.BaseController;
import com.project.web.area.ApiArea;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.*;

/**
 * Created by Administrator on 2015/6/25.
 */
@Controller
@RequestMapping(value = "/proxysale/ota", method = RequestMethod.GET)
public class OtaController extends BaseController{

    private static final Logger LOGGER = LoggerFactory.getLogger(OtaController.class);

    private static final String CURRENT_PAGE = "proxysale";
//    private static final Integer DEFAULT_PAGE_SIZE = 15;

    @Autowired
    private OtaInfoService otaInfoService;
    @Autowired
    private ChannelService channelService;
    @Autowired
    private AreaService areaService;
    @Autowired
    private PriceStrategyService priceStrategyService;

    @RequestMapping
    public String list(Model model){
        List<OtaInfo> otaInfos = null;
        try {
            otaInfos = otaInfoService.list();
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
        if(CollectionUtils.isNotEmpty(otaInfos)){
            for (OtaInfo otaInfo : otaInfos) {
                Integer otaId = otaInfo.getOtaId();
                Channel channel = channelService.get(otaId);
                if(channel == null){
                    channel = new Channel();
                    channel.setId(otaId);
                    channel.setChannelName(otaInfo.getName());
                    channelService.add(channel);
                }
                List<PriceStrategy> priceStrategies = priceStrategyService.findValidByChannel(channel.getId());
                channel.setPriceStrategies(priceStrategies);
                otaInfo.setChannel(channel);
            }
        }
        model.addAttribute("currentPage", CURRENT_PAGE);
        model.addAttribute("currentBtn", "channel");
        model.addAttribute("otaInfos", otaInfos);
        return "/proxysale/channel/list";
    }

    @RequestMapping(value = "/{id}/modify", method = RequestMethod.POST)
    @ResponseBody
    public AjaxResult modify(@PathVariable("id")Integer id,
                             @RequestParam(value = "basePer", required = false)Float basePercentage,
                             @RequestParam(value = "salePer", required = false)Float salePercentage,
                             @RequestParam(value = "saleBasePer", required = false)Float saleBasePercentage,
                             @RequestParam("areas")String areas) {
        Channel channel = channelService.get(id);
        if(channel == null){
            return new AjaxResult(Constants.HTTP_500, "渠道不存在");
        }
        try {
            if(StringUtils.isBlank(areas)){
                throw new RuntimeException("区域不能为空");
            }
            channel.setSaleArea(areaService.getCollection(areas.split(",")));
            channelService.modify(channel,
                    new PriceStrategy(id, salePercentage, PriceStrategy.STRATEGY_SALE_PRICE, true),
                    new PriceStrategy(id, basePercentage, PriceStrategy.STRATEGY_BASE_PRICE, true),
                    new PriceStrategy(id, saleBasePercentage, PriceStrategy.STRATEGY_SALE_BASE_PRICE, true));
        } catch (Exception e) {
            return new AjaxResult(Constants.HTTP_500, e.getMessage());
        }
        return new AjaxResult(Constants.HTTP_OK, StringUtils.EMPTY);
    }

    public static void main(String[] args) {
        HttpUtil httpUtil = new HttpUtil();
        Map<String, Object> kvs = new HashMap<>();
        kvs.put("basePer", "");
        kvs.put("id", 903);
        kvs.put("salePer", 13);
        int[] arr = {1902, 1};
        kvs.put("areas", arr);
        httpUtil.postForm("http://localhost:8080/proxysale/ota/903/modify", kvs);
    }

    @RequestMapping("/{id}")
    @ResponseBody
    public AjaxResult getEditInfo(@PathVariable("id")Integer id){
        OtaEditInfoVO vo = new OtaEditInfoVO();
        List<PriceStrategy> priceStrategies = priceStrategyService.findValidByChannel(id);
        if(CollectionUtils.isNotEmpty(priceStrategies)){
            for (PriceStrategy strategy : priceStrategies) {
                if(strategy.getStrategy().shortValue() == PriceStrategy.STRATEGY_BASE_PRICE){
                    vo.setBasePriceStrategry(new BigDecimal(strategy.getPercentage()));
                }
                if(strategy.getStrategy().shortValue() == PriceStrategy.STRATEGY_SALE_PRICE){
                    vo.setSalePriceStrategry(new BigDecimal(strategy.getPercentage()));
                }
                if(strategy.getStrategy().shortValue() == PriceStrategy.STRATEGY_SALE_BASE_PRICE){
                    vo.setSaleBasePriceStrategry(new BigDecimal(strategy.getPercentage()));
                }
            }
        }
        Set<Area> saleArea = channelService.get(id).getSaleArea();
        List<ApiArea> apiAreas = new ArrayList<>();
        if(CollectionUtils.isNotEmpty(saleArea)){
            for (Area area : saleArea) {
                apiAreas.add(new ApiArea(area));
            }
        }
        vo.setAreas(apiAreas);
        return new AjaxResult(Constants.HTTP_OK, vo);
    }
}
