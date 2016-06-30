package com.project.dao.proxysale;

import com.project.common.ApiURL;
import com.project.utils.HttpUtil;
import com.project.utils.SystemConfig;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * Created by Administrator on 2015/8/28.
 */
@Component("proxyContractDao")
public class ProxyContractDao {

    public String queryConstracts(Map<String, String> params){
        String url = new HttpUtil().buildUrl(SystemConfig.PROPERTIES.get(SystemConfig.CRM_URL), ApiURL.CRM_CONTRACT_LIST, params);
        return new HttpUtil().get(url);
    }
}
