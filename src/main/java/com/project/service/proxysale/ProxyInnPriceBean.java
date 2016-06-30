package com.project.service.proxysale;

import com.project.web.proxysale.ProxyInnPriceForm;

import java.util.Map;

/**
 * Created by Administrator on 2015/8/27.
 */
public interface ProxyInnPriceBean {

    Map<String, String> parsePriceQueryForm(ProxyInnPriceForm proxyInnPriceForm);

    Map<String,Object> parseBasePriceCheckedCrmParams(Integer pmsInnId, Long userId);

    Map<String,Object> parseSalePriceCheckedCrmParams(Integer pmsInnId, Long userId);

    Map<String,Object> parseBasePriceRejectedCrmParams(Integer pmsInnId, Long userId, String checkReason);

    Map<String,Object> parseSalePriceRejectedCrmParams(Integer pmsInnId, Long userId, String checkReason);
}
