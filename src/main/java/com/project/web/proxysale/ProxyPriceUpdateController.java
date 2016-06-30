package com.project.web.proxysale;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.project.bean.proxysale.PriceDetailQuery;
import com.project.bean.vo.AjaxResult;
import com.project.common.Constants;
import com.project.core.orm.Page;
import com.project.entity.proxysale.ProxyInn;
import com.project.service.proxysale.PriceUpdateService;
import com.project.service.proxysale.ProxyInnBean;
import com.project.service.proxysale.ProxyInnService;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author frd
 */
@Controller
@RequestMapping("/proxysale/price")
public class ProxyPriceUpdateController {

    private static final Logger LOGGER = LoggerFactory.getLogger(ProxyPriceUpdateController.class);

    @Autowired
    private ProxyInnService proxyInnService;
    @Autowired
    private ProxyInnBean proxyInnBean;
    @Autowired
    private PriceUpdateService priceUpdateService;

    @RequestMapping(value = "/list")
    public ModelAndView list(Page page, @RequestParam(value = "innName", required = false) String innName) {
        ModelAndView mav = new ModelAndView("/proxysale/price/list");

        page = proxyInnService.findPriceUpdateInnList(page, innName);
        List list = page.getResult();
        if (CollectionUtils.isNotEmpty(list)) {
            List<PriceUpdateView> result = new ArrayList<>();
            for (Object o : list) {
                Object[] objs = (Object[]) o;
                result.add(new PriceUpdateView(objs[0].toString(), objs[1].toString(), Integer.parseInt(objs[2].toString())));
            }
            mav.addObject("result", result);
        }

        mav.addObject("page", page);
        mav.addObject("currentBtn", "price");
        mav.addObject("innName", innName);
        return mav;
    }

    @RequestMapping(value = "/detail")
    public ModelAndView detail(@RequestParam("proxyInnId") Integer proxyInnId) {
        ModelAndView mav = new ModelAndView("/proxysale/price/detail");
        ProxyInn proxyInn = proxyInnService.get(proxyInnId);
        mav.addObject("currentBtn", "price");
        mav.addObject("proxyInn", proxyInn);
        return mav;
    }

    @RequestMapping("/getChannels")
    @ResponseBody
    public AjaxResult getChannels(@RequestParam("proxyInnId") Integer proxyInnId,
                                  @RequestParam("pricePattern") Float pricePattern,
                                  @RequestParam("innId") Integer innId) {
        try {
            Map<String, List<Map<String, Object>>> proxyInnChannel = proxyInnService.getProxyInnChannel(proxyInnId, pricePattern, innId);
            List<Map<String, Object>> sales = proxyInnChannel.get("sale");
            if (CollectionUtils.isNotEmpty(sales)) {
                for (int i = sales.size() - 1; i >= 0; i--) {
                    Map<String, Object> map = sales.get(i);
                    if (!Boolean.valueOf(map.get("isOpen").toString())) {
                        sales.remove(i);
                    }
                }
            }
            return new AjaxResult(Constants.HTTP_OK, sales, "success");
        } catch (Exception e) {
            return new AjaxResult(Constants.HTTP_500, e.getMessage());
        }
    }

    @RequestMapping(value = "/roomDetail")
    @ResponseBody
    public AjaxResult getDetailPrice(RoomDetailForm roomDetailForm) {
        checkDetailForm(roomDetailForm);
        PriceDetailQuery detailQuery = proxyInnBean.parse(roomDetailForm);
        String result = proxyInnService.findPriceDetailByChannel(detailQuery);
        JSONObject jsonObject = JSON.parseObject(result);
        return new AjaxResult(Constants.HTTP_OK, jsonObject);
    }

    private void checkDetailForm(RoomDetailForm roomDetailForm) {
        if (roomDetailForm.getChannelId() == null) {
            throw new RuntimeException("渠道ID不能为空");
        }
        if (roomDetailForm.getProxyInnId() == null) {
            throw new RuntimeException("代销客栈ID不能为空");
        }
    }

    @RequestMapping(value = "/doUpdate", method = RequestMethod.POST)
    @ResponseBody
    public AjaxResult doPriceUpdate(
            @RequestParam("innName") String innName,
            @RequestParam("accountId") Integer accountId,
            @RequestParam("otaList") String otaList,
            @RequestParam("roomList") String roomList) {
        try {
            proxyInnService.updatePrice(accountId, otaList, roomList, innName);
        } catch (Exception e) {
            return new AjaxResult(Constants.HTTP_500, e.getMessage());
        }
        return new AjaxResult(Constants.HTTP_OK, "");
    }

    /**
     * 获取所有目的地名称
     *
     * @return
     */
    @RequestMapping(value = "/regionName")
    @ResponseBody
    public AjaxResult getRegionName() {
        try {
            List<Map<String, Object>> regionName = priceUpdateService.findRegionName();
            return new AjaxResult(Constants.HTTP_OK, regionName);
        } catch (Exception e) {
            return new AjaxResult(Constants.HTTP_500, e.getMessage());
        }
    }

    /**
     * 获取卖价渠道
     *
     * @return
     */
    @RequestMapping(value = "/channelName")
    @ResponseBody
    public AjaxResult getChannelName() {
        try {
            List<Map<String, Object>> saleChannel = priceUpdateService.findSaleChannel();
            return new AjaxResult(Constants.HTTP_OK, saleChannel);
        } catch (Exception e) {
            return new AjaxResult(Constants.HTTP_500, e.getMessage());
        }
    }

    /**
     * 批量调价
     *
     * @return
     */
    @RequestMapping(value = "/batchUpdatePrice")
    @ResponseBody
    public AjaxResult batchUpdatePrice(String jsonStr) {
        try {
            priceUpdateService.batchUpdatePrice(jsonStr);
            return new AjaxResult(Constants.HTTP_OK, "操作成功");
        } catch (Exception e) {
            priceUpdateService.saveErrorLog(jsonStr);
            return new AjaxResult(Constants.HTTP_500, e.getMessage());
        }
    }

}

