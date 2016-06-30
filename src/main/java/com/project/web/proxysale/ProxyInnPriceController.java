package com.project.web.proxysale;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.project.bean.vo.AjaxBase;
import com.project.bean.vo.AjaxResult;
import com.project.common.Constants;
import com.project.entity.proxysale.ProxyInn;
import com.project.entity.proxysale.ProxyInnOnoff;
import com.project.service.proxysale.ProxyInnPriceBean;
import com.project.service.proxysale.ProxyInnPriceService;
import com.project.service.proxysale.ProxyInnService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 代销价格审核
 * Created by Administrator on 2015/8/3.
 */

@Controller
@RequestMapping("/proxysale/inn/price")
public class ProxyInnPriceController {

    private static final Logger LOGGER = LoggerFactory.getLogger(ProxyInnPriceController.class);
    @Autowired
    private ProxyInnPriceService proxyInnPriceService;
    @Autowired
    private ProxyInnService proxyInnService;
    @Autowired
    private ProxyInnPriceBean proxyInnPriceBean;

    @RequestMapping
    public String index(){
        return "proxysale/audit/price/list";
    }

    /**
     * 获取审核列表
     * @return
     */
    @RequestMapping(method = RequestMethod.GET, value = "/list")
    @ResponseBody
    public Map<String ,Object> list(ProxyInnPriceForm proxyInnPriceForm){
        Map<String,Object> resultMap = new HashMap<>();

        Map<String, String> params = proxyInnPriceBean.parsePriceQueryForm(proxyInnPriceForm);
        JSONArray data;
        String result;
        try {
            result = proxyInnPriceService.list(params);
            JSONObject resJSON = JSON.parseObject(result);
            boolean responseStatus = resJSON.getBooleanValue("status");
            if(!responseStatus){
                String message = resJSON.getString("message");
                throw new RuntimeException(message);
            }
            resultMap.put("total", resJSON.getIntValue("total"));
            resultMap.put("page", proxyInnPriceForm.getPageNo());//页码
            resultMap.put("pageSize", proxyInnPriceForm.getPageSize());//每页条数
            data = resJSON.getJSONArray("data");
            for (Object jsonData : data) {
                JSONObject json = (JSONObject)jsonData;
                Integer innId = json.getInteger("innId");
                ProxyInn proxyInn = proxyInnService.findByInnId(innId);
                if(proxyInn == null){
                    LOGGER.error("代销客栈不存在, innId" + innId);
                    continue;
                }
                json.put("otaLink", proxyInn.getOtaLink());
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
            resultMap.put("status","500");
            resultMap.put("message",e.getMessage());
            return resultMap;
        }
        resultMap.put("status",true);
        resultMap.put("data", data);
        return resultMap;
    }

    /**
     * 审核通过/否决   历史数据处理完就干掉
     * @return
     */
    @RequestMapping(value = "/checkout/{recordCode}", method = RequestMethod.POST)
    @ResponseBody
    public AjaxBase checkout(@PathVariable("recordCode")String recordCode,
                             @RequestParam("status")String status,
                             @RequestParam("innId")Integer innId,
                             @RequestParam(value = "reason", required = false)String reason,
                             @RequestParam("pattern")Short pattern){

        try {
            if(ProxyInnPriceService.STATUS_CHECKED.equals(status)){
                proxyInnPriceService.checkSuc(recordCode, innId, pattern);
            }else if(ProxyInnPriceService.STATUS_REJECT.equals(status)){
                proxyInnPriceService.checkReject(recordCode, innId, pattern, reason);
            }else{
                throw new RuntimeException("审核状态异常，status=" + status);
            }
            return new AjaxBase(Constants.HTTP_OK, "");
        } catch (RuntimeException e) {
            LOGGER.error(e.getMessage());
            return new AjaxBase(Constants.HTTP_500, e.getMessage());
        }
    }

    /**
     * 根据客栈ID查询该客栈是否有被下架过的历史
     * @param innId
     * @return
     */
    @RequestMapping(value = "/checkOnOffLog", method = RequestMethod.POST)
    @ResponseBody
    public AjaxResult checkout(Integer innId) {
        ProxyInnOnoff offedProxyInn = proxyInnPriceService.isOffedProxyInn(innId);
        if(offedProxyInn == null) {
            return new AjaxResult(Constants.HTTP_OK, false);
        } else {
            return new AjaxResult(Constants.HTTP_OK, true, offedProxyInn.getRemark());
        }
    }



    /**
     * path:md5(fillaudit)
     */
    @RequestMapping(value = "/1e631429081c7296")
    public void fillAudit(){
        proxyInnPriceService.syncAudit();
    }
}
