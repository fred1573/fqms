package com.project.service.proxysale;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.project.common.ApiURL;
import com.project.core.utils.springsecurity.SpringSecurityUtil;
import com.project.dao.account.HibernateUserDao;
import com.project.dao.finance.FinanceOperationLogDao;
import com.project.dao.proxysale.ProxyInnDao;
import com.project.dao.proxysale.ProxyInnOnoffDao;
import com.project.dao.proxysale.ProxyInnPriceDao;
import com.project.entity.account.User;
import com.project.entity.finance.FinanceOperationLog;
import com.project.entity.proxysale.PricePattern;
import com.project.entity.proxysale.ProxyAudit;
import com.project.entity.proxysale.ProxyInn;
import com.project.entity.proxysale.ProxyInnOnoff;
import com.project.service.account.AccountService;
import com.project.utils.CollectionsUtil;
import com.project.utils.HttpUtil;
import com.project.utils.SystemConfig;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.*;

/**
 *
 * Created by Administrator on 2015/8/4.
 */
@Service("proxyInnPriceService")
@Transactional
public class ProxyInnPriceServiceImpl implements ProxyInnPriceService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ProxyInnServiceImpl.class);
    public static final String JINGPIN = "JINGPIN";
    public static final String NORMAL = "NORMAL";
    public static final String OUT_REQUEST = "外部调用";

    @Autowired
    private ProxyInnService proxyInnService;
    @Autowired
    private AccountService accountService;
    @Autowired
    private ProxyAuditService proxyAuditService;
    @Autowired
    private ProxyInnPriceBean proxyInnPriceBean;
    @Autowired
    private ProxyInnPriceDao proxyInnPriceDao;
    @Autowired
    private CrmBean crmBean;
    @Autowired
    private ProxyAuditBean proxyAuditBean;
    @Autowired
    private PricePatternService pricePatternService;
    @Autowired
    private ProxyInnOnoffDao proxyInnOnoffDao;
    @Autowired
    private ProxyInnDao proxyInnDao;
    @Autowired
    private ProxyInnBean proxyInnBean;
    
    @Autowired
    private FinanceOperationLogDao financeOperationLogDao;
    @Autowired
    private HibernateUserDao hibernateUserDao;

    @Override
    public String list(Map<String, String> params) {
        return proxyInnPriceDao.list(params);
    }

    /**
     * 获取当前登录用户
     * @return
     */
    private User getCurrentUser() {
        String userName = SpringSecurityUtil.getCurrentUserName();
        return hibernateUserDao.findUserByUserCode(userName);
    }

    @Override
    public boolean defaultCheckSuc(String recordCode, Integer innId, Short pattern, Long userId) {
        String url = new HttpUtil().buildUrl(SystemConfig.PROPERTIES.get(SystemConfig.OMS_URL), ApiURL.OMS_PRICE_CHECKOUT, null);
        Map<String, Object> params = new HashMap<>();
        params.put("recordCode", recordCode);
        params.put("status", ProxyInnPriceService.STATUS_CHECKED);
        params.put("approvePerson", userId == null ? "" : userId);
        JSONObject jsonObject;
        try {
            String result = new HttpUtil().postForm(url, params);
            jsonObject = JSON.parseObject(result);

            boolean status = jsonObject.getBoolean("status");
            if(!status){
                String error = "调用审核更新接口失败,message=" + jsonObject.getString("message");
                LOGGER.error(error);
                throw new RuntimeException(error);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        //----------保存价格审核记录-----------
        proxyAuditService.save(innId, recordCode, ProxyAudit.STATUS_CHECKED, null, pattern);
        ProxyInn proxyInn = proxyInnDao.findByInnId(innId);
        User currentUser = getCurrentUser();
        // 保存操作日志
        financeOperationLogDao.save(new FinanceOperationLog("111", proxyInn == null ? "" : proxyInn.getInnName(), "价格审核通过", currentUser == null ? "自动审核" : currentUser.getSysUserName()));

        //----------通知CRM方-------------------
        Map<String, Object> crmParams;
        if(PricePattern.PATTERN_BASE_PRICE.shortValue() == pattern){
            crmParams = proxyInnPriceBean.parseBasePriceCheckedCrmParams(innId, userId);
        }else{
            crmParams = proxyInnPriceBean.parseSalePriceCheckedCrmParams(innId, userId);
        }
        crmBean.updateCRM(crmParams);
        return true;
    }

    @Override
    public boolean checkSuc(String recordCode, Integer innId, Short pattern) {
        User currentUser = getCurrentUser();
        Long userId = null;
        if(currentUser != null){
            userId = currentUser.getId();
        }
        defaultCheckSuc(recordCode, innId, pattern, userId);
        return true;
    }

    @Override
    public boolean checkReject(String recordCode, Integer innId, Short pattern, String reason) {
        User currentUser = getCurrentUser();
        Long userId = currentUser.getId();
        Map<String, Object> params = new HashMap<>();
        params.put("recordCode", recordCode);
        params.put("status", ProxyInnPriceService.STATUS_REJECT);
        if(StringUtils.isNotBlank(reason)){
            params.put("content", reason);
        }
        params.put("approvePerson", userId);
        String url = new HttpUtil().buildUrl(SystemConfig.PROPERTIES.get(SystemConfig.OMS_URL), ApiURL.OMS_PRICE_CHECKOUT, null);
        String response;
        try {
            response = new HttpUtil().postForm(url, params);
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
            throw new RuntimeException(e);
        }
        JSONObject respJson = JSON.parseObject(response);
        if(!respJson.getBoolean("status")){
            throw new RuntimeException("调用审核更新接口失败,message=" + respJson.getString("message"));
        }
        //------ 保存审核记录 -------
        proxyAuditService.save(innId, recordCode, ProxyAudit.STATUS_REJECTED, reason, pattern);
        ProxyInn proxyInn = proxyInnDao.findByInnId(innId);
        // 保存操作日志
        financeOperationLogDao.save(new FinanceOperationLog("111", proxyInn == null ? "" : proxyInn.getInnName(), "价格审核拒绝原因：" + reason, currentUser.getSysUserName()));
        //------ 更新CRM --------
        Map<String, Object> crmParams;
        String checkReason = crmBean.getContractRejectedReason(innId, reason);
        if(PricePattern.PATTERN_BASE_PRICE.shortValue() == pattern){
            checkReason = crmBean.getSaleRejectedReason(innId, checkReason);
            crmParams = proxyInnPriceBean.parseBasePriceRejectedCrmParams(innId, userId, checkReason);
        }else{
            checkReason = crmBean.getBaseRejectedReason(innId, checkReason);
            crmParams = proxyInnPriceBean.parseSalePriceRejectedCrmParams(innId, userId, checkReason);
        }
        crmBean.updateCRM(crmParams);
        return true;
    }

    @Override
    public String getLastRecordStatus(Integer accountId) {
        Map<String, String> params = new HashMap<>();
        params.put("accountId", accountId.toString());
        String lastRecord = proxyInnPriceDao.getLastRecord(params);
        JSONObject jsonObject = JSON.parseObject(lastRecord);
        if(!jsonObject.getBoolean("status")){
            throw new RuntimeException(jsonObject.getString("message"));
        }
        Object data = jsonObject.get("data");
        return data == null ? null : data.toString();
    }

    @Override
    public void syncAudit() {
        Map<String, String> params = new HashMap<>();
        params.put("status", ProxyInnPriceService.STATUS_EVERY);
        Integer count = 1;
        Integer rows = 100;
        params.put("rows", rows.toString());
        while(true){
            params.put("page", count.toString());
            String list = proxyInnPriceDao.list(params);
            JSONObject jsonObject = JSON.parseObject(list);
            if(!jsonObject.getBooleanValue("status")){
                LOGGER.error("查询价格审核记录失败");
                break;
            }
            JSONArray dataArray = jsonObject.getJSONArray("data");
            int size = dataArray.size();
            List<ProxyAudit> audits = new ArrayList<>();
            for (int i=0; i<size; i++) {
                JSONObject json = (JSONObject) dataArray.get(i);
                String status = json.getString("status");
                if(!(ProxyInnPriceService.STATUS_CHECKED.equals(status) || ProxyInnPriceService.STATUS_REJECT.equals(status))){
                    //状态不为审核通过或否决，不同步
                    continue;
                }
                String patternJson = json.getString("pattern");
                Integer accountId = json.getInteger("accountId");
                audits.add(proxyAuditBean.parse(json.getInteger("innId"), json.getString("recordCode"), status, json.getString("reason"), convert(patternJson, accountId), ProxyAudit.AUDIT_PRICE, json.getDate("dateUpdated"), convert(json.getLong("approvePerson"))));
            }
            //筛选出需要同步的记录
            proxyAuditService.syncPriceRcords(audits);
            if(size < rows){
                break;
            }
            count++;
        }
    }

    private Short convert(String pattern, Integer accountId){
        if(JINGPIN.equals(pattern)){
            return 1;
        }else if(NORMAL.equals(pattern)){
            return 2;
        }else {
            return pricePatternService.getPattern(accountId);
        }
    }

    private User convert(Long uid){
        if(uid == null){
            return null;
        }
        return accountService.getUser(uid);
    }

    @Override
    public void syncAudit(Date from, Date to) {

    }

    @Override
    public ProxyInnOnoff isOffedProxyInn(Integer innId) {
        // 根据客栈ID查询代销客栈对象
        ProxyInn proxyInn = proxyInnDao.findByInnId(innId);
        if (proxyInn == null) {
            throw new RuntimeException("客栈ID为【" + innId + "】的代销客栈不存在");
        }
        List<ProxyInnOnoff> proxyInnOnoffs = proxyInnOnoffDao.selectProxyInnOffList(proxyInn.getId());
        if(CollectionsUtil.isNotEmpty(proxyInnOnoffs)) {
            return proxyInnOnoffs.get(0);
        }
        return null;
    }
}