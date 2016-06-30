package com.project.dao.proxysale;

import com.project.common.ApiURL;
import com.project.utils.HttpUtil;
import com.project.utils.SystemConfig;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 *
 * Created by Administrator on 2015/8/28.
 */
@Component("proxyInnPriceDao")
public class ProxyInnPriceDao {

    public String list(Map<String, String> params){
        String url = new HttpUtil().buildUrl(SystemConfig.PROPERTIES.get(SystemConfig.OMS_URL), ApiURL.OMS_PRICE_LIST, params);
        return new HttpUtil().get(url, false);
    }

    public String getLastRecord(Map<String, String> params){
        String url = new HttpUtil().buildUrl(SystemConfig.PROPERTIES.get(SystemConfig.OMS_URL), ApiURL.OMS_PRICE_LAST, params);
        return new HttpUtil().get(url);
    }
}
