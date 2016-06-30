package com.project.web.proxysale;

import com.project.bean.vo.AjaxBase;
import com.project.common.Constants;
import com.project.entity.ota.OtaInfo;
import com.project.entity.proxysale.PriceStrategy;
import com.project.service.ota.OtaInfoService;
import com.project.service.proxysale.PriceStrategyService;
import com.project.utils.SystemConfig;
import com.project.web.BaseController;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Administrator on 2015/6/4.
 */
@Controller
@RequestMapping("/proxysale/api/channel/")
public class ApiOtaController extends BaseController {

    @Autowired
    private OtaInfoService otaInfoService;
    @Autowired
    private PriceStrategyService priceStrategyService;

    @RequestMapping(value="/{id}")
    @ResponseBody
    public AjaxBase query(@PathVariable("id")Integer otaId, @RequestParam("strategy")Short strategy,
                          @RequestParam(value = "time", required = false)String time){
        try {
            //底价模式获取比例时传渠道ID为102，返回比例0
            if(otaId.equals(Integer.valueOf(Constants.OMS_PROXY_PID))){
                return new AjaxChannel(Constants.HTTP_OK, 0f);
            }
            OtaInfo otaInfo = otaInfoService.getByOtaId(otaId);
            if(otaInfo != null){
                PriceStrategy history = priceStrategyService.findHistory(otaId, time == null ? new Date() : new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").parse(time), strategy);
                if (history != null) {
                    return new AjaxChannel(Constants.HTTP_OK, history.getPercentage());
                }
                return new AjaxBase(Constants.HTTP_500, "该渠道没有该价格策略");
            }
            return new AjaxBase(Constants.HTTP_500, "渠道不存在");
        } catch (Exception e) {
            return new AjaxBase(Constants.HTTP_500, e.getMessage());
        }
    }

    class AjaxChannel extends AjaxBase{

        private Float percentage;

        public AjaxChannel(int status, String message) {
            super(status, message);
        }

        public AjaxChannel(int status, Float percentage) {
            super(status);
            this.percentage = percentage;
        }

        public Float getPercentage() {
            return percentage;
        }

        public void setPercentage(Float percentage) {
            this.percentage = percentage;
        }

        @Override
        public String toString() {
            return ToStringBuilder.reflectionToString(this);
        }
    }
}


