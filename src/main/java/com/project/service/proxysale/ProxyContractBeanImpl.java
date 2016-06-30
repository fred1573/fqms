package com.project.service.proxysale;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.project.common.Constants;
import com.project.entity.proxysale.ProxyAudit;
import com.project.entity.proxysale.ProxyContract;
import com.project.entity.proxysale.ProxyContractImage;
import com.project.utils.SystemConfig;
import com.project.web.proxysale.ProxyContractForm;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2015/8/24.
 */
@Component("proxyContractBean")
public class ProxyContractBeanImpl implements ProxyContractBean {

    @Autowired
    private ProxyContractService proxyContractService;
    @Autowired
    private ProxyInnBean proxyInnBean;
    @Autowired
    private CrmBean crmBean;
    @Autowired
    private ProxyAuditService proxyAuditService;

    @Override
    public List<ProxyContract> parseProxyContracts(String jsonStr) {
        List<ProxyContract> proxyContracts = new ArrayList<>();
        if (StringUtils.isBlank(jsonStr)) {
            return proxyContracts;
        }
        JSONObject jsonObject = JSON.parseObject(jsonStr);
        Integer status = jsonObject.getInteger("status");
        if (status != null) {
            throw new RuntimeException("查询客栈代销合同异常，resultJson:" + jsonStr);
        }
        JSONArray contracts = jsonObject.getJSONArray("list");
        if (contracts == null || contracts.size() <= 0) {
            return proxyContracts;
        }
        ProxyContract proxyContract;
        for (Object contractJson : contracts.toArray()) {
            proxyContract = new ProxyContract();
            JSONObject contractJsonObject = (JSONObject) contractJson;
            proxyContract.setInnName(contractJsonObject.getString("innName"));
            proxyContract.setInnId(contractJsonObject.getInteger("innId"));
            proxyContract.setCommitTime(contractJsonObject.getDate("commitTime"));
            proxyContract.setStatus(contractJsonObject.getString("contractStatus"));
            proxyContract.setUserName(contractJsonObject.getString("userName"));
            proxyContracts.add(proxyContract);
        }
        return proxyContracts;
    }

    @Override
    public JSONObject parsePageFromJson(String jsonStr) {
        JSONObject jsonObject = JSON.parseObject(jsonStr);
        return jsonObject.getJSONObject("page");
    }

    @Override
    public List<ProxyContractImage> parseProxyContractImages(String jsonStr) {
        List<ProxyContractImage> proxyContractImages = new ArrayList<>();
        if (StringUtils.isBlank(jsonStr)) {
            return proxyContractImages;
        }
        JSONObject jsonObject = JSON.parseObject(jsonStr);
        Integer status = jsonObject.getInteger("status");
        if (status != Constants.HTTP_OK) {
            throw new RuntimeException(jsonObject.getString("message"));
        }
        JSONArray dataArr = jsonObject.getJSONArray("data");
        if (dataArr == null || dataArr.size() <= 0) {
            return proxyContractImages;
        }
        for (int i = 0; i < dataArr.size(); i++) {
            JSONObject data = (JSONObject) dataArr.get(i);
            String contractNo = data.getString("contractNo");
            JSONArray contractUrlList = data.getJSONArray("contractUrlList");
            if (contractUrlList == null || contractUrlList.size() <= 0) {
                return proxyContractImages;
            }
            ProxyContractImage proxyContractImage;
            for (Object urlObj : contractUrlList.toArray()) {
                proxyContractImage = new ProxyContractImage();
                proxyContractImage.setContractNo(contractNo);
                proxyContractImage.setUrl(SystemConfig.PROPERTIES.get(SystemConfig.FTP_URL) + urlObj.toString());
                proxyContractImages.add(proxyContractImage);
            }
        }
        return proxyContractImages;
    }

    @Override
    public Map<String, String> parseContractQueryParams(ProxyContractForm proxyContractForm) {
        Map<String, String> params = new HashMap<>();
        String from = proxyContractForm.getFrom();
        if (StringUtils.isNotBlank(from)) {
            params.put("from", from);
        }
        String to = proxyContractForm.getTo();
        if (StringUtils.isNotBlank(to)) {
            params.put("to", to);
        }
        String keyword = proxyContractForm.getKeyword();
        params.put("page", proxyContractForm.getPageNo().toString());
        params.put("rows", proxyContractForm.getPageSize().toString());
        if (StringUtils.isNotBlank(keyword)) {
            params.put("keyword", keyword.trim());
        }
        String status = proxyContractForm.getStatus();
        if (StringUtils.isNotBlank(status)) {
            params.put("contractStatus", status);//contractStatus:合同状态
        }
        return params;
    }

    @Override
    public String getContractNo(Integer innId) {
        Map<String, String> params = new HashMap<>();
        params.put("pmsInnId", innId.toString());
        List<ProxyContractImage> proxyContractImages = proxyContractService.listContractImages(params);
        return proxyContractImages.get(0).getContractNo();
    }

    @Override
    public Map<String, Object> parseCheckedCrmParams(Integer pmsInnId, Long userId) {
        Map<String, Object> crmParams = new HashMap<>();
        String reason = crmBean.getBaseRejectedReason(pmsInnId, "");
        reason = crmBean.getSaleRejectedReason(pmsInnId, reason);
        crmParams.put("checkMessage", reason);
        crmParams.put("pmsInnId", pmsInnId);
        crmParams.putAll(proxyAuditService.getAuditStatus(pmsInnId));
        crmParams.put("checkStatus", crmBean.getInnStatusWithContractChecked(pmsInnId));//客栈审核状态
        crmParams.put("saleStatus", proxyInnBean.getOnshelfStatus(pmsInnId));//客栈上下架状态
        return crmParams;
    }

    @Override
    public Map<String, Object> parseRejectedCrmParams(Integer pmsInnId, Long userId, String checkReason) {
        Map<String, Object> crmParams = new HashMap<>();
        crmParams.put("checkMessage", checkReason);
        crmParams.put("pmsInnId", pmsInnId);
        crmParams.putAll(proxyAuditService.getAuditStatus(pmsInnId));
        crmParams.put("checkStatus", ProxyAudit.STATUS_REJECTED);//客栈审核状态
        crmParams.put("saleStatus", proxyInnBean.getOnshelfStatus(pmsInnId));//客栈上下架状态
        return crmParams;
    }

    public static void main(String[] args) {
        JSONObject jsonObject = new JSONObject();
        JSONArray jsonArr = null;
        jsonObject.put("contracts", jsonArr);
        jsonObject.put("status", 200);
        new ProxyContractBeanImpl().parseProxyContracts(jsonObject.toJSONString());
    }

}
