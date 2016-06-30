package com.project.service.proxysale;

import com.project.entity.proxysale.ProxyAudit;
import com.project.web.proxysale.ProxyInnPriceForm;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Administrator on 2015/8/27.
 */
@Component("proxyInnPriceBean")
public class ProxyInnPriceBeanImpl implements ProxyInnPriceBean {

    @Autowired
    private ProxyInnBean proxyInnBean;
    @Autowired
    private CrmBean crmBean;
    @Autowired
    private ProxyAuditService proxyAuditService;

    @Override
    public Map<String, String> parsePriceQueryForm(ProxyInnPriceForm proxyInnPriceForm) {
        Map<String, String> params = new HashMap<>();
        params.put("rows", proxyInnPriceForm.getPageSize());
        params.put("page", proxyInnPriceForm.getPageNo());
        String keyword = proxyInnPriceForm.getKeyword();
        if (StringUtils.isNotBlank(keyword)) {
            params.put("keyword", keyword);
        } else {
            String status = proxyInnPriceForm.getStatus();
            if (StringUtils.isNotBlank(status)) {
                params.put("status", status);
            }
            String pattern = proxyInnPriceForm.getPattern();
            if (StringUtils.isNotBlank(pattern)) {
                params.put("pattern", pattern);
            }
            String from = proxyInnPriceForm.getFrom();
            if (StringUtils.isNotBlank(from)) {
                params.put("from", from);
            }
            String to = proxyInnPriceForm.getTo();
            if (StringUtils.isNotBlank(to)) {
                params.put("to", to);
            }
        }
        return params;
    }

    @Override
    public Map<String, Object> parseBasePriceCheckedCrmParams(Integer pmsInnId, Long userId) {
        Map<String, Object> crmParams = new HashMap<>();
        String innStatusWithBasePriceChecked = crmBean.getInnStatusWithBasePriceChecked(pmsInnId);
        String reason = crmBean.getContractRejectedReason(pmsInnId, "");
        reason = crmBean.getSaleRejectedReason(pmsInnId, reason);
        crmParams.put("checkMessage", reason);
        crmParams.put("pmsInnId", pmsInnId);
        crmParams.putAll(proxyAuditService.getAuditStatus(pmsInnId));
        crmParams.put("checkStatus", innStatusWithBasePriceChecked);//客栈审核状态
        crmParams.put("statusTime", new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(new Date()));
        crmParams.put("saleStatus", proxyInnBean.getOnshelfStatus(pmsInnId));//客栈上下架状态
        return crmParams;
    }

    @Override
    public Map<String, Object> parseSalePriceCheckedCrmParams(Integer pmsInnId, Long userId) {
        Map<String, Object> crmParams = new HashMap<>();
        String innStatusWithSalePriceChecked = crmBean.getInnStatusWithSalePriceChecked(pmsInnId);
        String reason = crmBean.getContractRejectedReason(pmsInnId, "");
        reason = crmBean.getBaseRejectedReason(pmsInnId, reason);
        crmParams.put("checkMessage", reason);
        crmParams.put("pmsInnId", pmsInnId);
        crmParams.putAll(proxyAuditService.getAuditStatus(pmsInnId));
        crmParams.put("checkStatus", innStatusWithSalePriceChecked);//客栈审核状态
        crmParams.put("statusTime", new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(new Date()));
        crmParams.put("saleStatus", proxyInnBean.getOnshelfStatus(pmsInnId));//客栈上下架状态
        return crmParams;
    }

    @Override
    public Map<String, Object> parseBasePriceRejectedCrmParams(Integer pmsInnId, Long userId, String checkReason) {
        Map<String, Object> crmParams = new HashMap<>();
        String innStatusWithBasePriceReject = crmBean.getInnStatusWithBasePriceReject(pmsInnId);
        crmParams.put("checkMessage", checkReason);
        crmParams.put("pmsInnId", pmsInnId);
        crmParams.putAll(proxyAuditService.getAuditStatus(pmsInnId));
        crmParams.put("checkStatus", innStatusWithBasePriceReject);//客栈审核状态
        crmParams.put("saleStatus", proxyInnBean.getOnshelfStatus(pmsInnId));//客栈上下架状态
        return crmParams;
    }

    @Override
    public Map<String, Object> parseSalePriceRejectedCrmParams(Integer pmsInnId, Long userId, String checkReason) {
        Map<String, Object> crmParams = new HashMap<>();
        String innStatusWithSalePriceReject = crmBean.getInnStatusWithSalePriceReject(pmsInnId);
        crmParams.put("checkMessage", checkReason);
        crmParams.put("pmsInnId", pmsInnId);
        crmParams.putAll(proxyAuditService.getAuditStatus(pmsInnId));
        crmParams.put("checkStatus", innStatusWithSalePriceReject);//客栈审核状态
        crmParams.put("saleStatus", proxyInnBean.getOnshelfStatus(pmsInnId));//客栈上下架状态
        return crmParams;
    }
}
