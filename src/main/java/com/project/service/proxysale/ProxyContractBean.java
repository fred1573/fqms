package com.project.service.proxysale;

import com.alibaba.fastjson.JSONObject;
import com.project.entity.proxysale.ProxyContract;
import com.project.entity.proxysale.ProxyContractImage;
import com.project.web.proxysale.ProxyContractForm;

import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2015/8/24.
 */
public interface ProxyContractBean {

    List<ProxyContract> parseProxyContracts(String jsonStr);

    JSONObject parsePageFromJson(String jsonStr);

    List<ProxyContractImage> parseProxyContractImages(String jsonStr);

    Map<String,String> parseContractQueryParams(ProxyContractForm proxyContractForm);

    String getContractNo(Integer innId);

    Map<String,Object> parseCheckedCrmParams(Integer pmsInnId, Long userId);

    Map<String,Object> parseRejectedCrmParams(Integer pmsInnId, Long userId, String checkReason);
}
