package com.project.service.proxysale;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.project.common.ApiURL;
import com.project.core.utils.springsecurity.SpringSecurityUtil;
import com.project.dao.account.HibernateUserDao;
import com.project.dao.finance.FinanceOperationLogDao;
import com.project.dao.proxysale.ProxyContractDao;
import com.project.dao.proxysale.ProxyInnDao;
import com.project.entity.finance.FinanceOperationLog;
import com.project.entity.proxysale.ProxyAudit;
import com.project.entity.proxysale.ProxyContract;
import com.project.entity.proxysale.ProxyContractImage;
import com.project.entity.proxysale.ProxyInn;
import com.project.service.account.AccountService;
import com.project.utils.HttpUtil;
import com.project.utils.SystemConfig;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2015/8/24.
 */
@Service("proxyContractService")
@Transactional
public class ProxyContractServiceImpl implements ProxyContractService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ProxyContractServiceImpl.class);

    @Autowired
    private ProxyContractBean proxyContractBean;
    @Autowired
    private AccountService accountService;
    @Autowired
    private ProxyInnService proxyInnService;
    @Autowired
    private ProxyContractDao proxyContractDao;
    @Autowired
    private ProxyAuditService proxyAuditService;
    @Autowired
    private CrmBean crmBean;
    @Autowired
    private ProxyInnDao proxyInnDao;
    @Autowired
    private FinanceOperationLogDao financeOperationLogDao;
    @Autowired
    private HibernateUserDao hibernateUserDao;

    @Override
    public Map<String, Object> listContracts(Map<String, String> params) {
        String jsonStr = proxyContractDao.queryConstracts(params);
        List<ProxyContract> proxyContracts = proxyContractBean.parseProxyContracts(jsonStr);
        JSONObject pageJson = proxyContractBean.parsePageFromJson(jsonStr);
        Map<String, Object> result = new HashMap<>();
        result.put("status", true);
        result.put("total", pageJson.getInteger("rowsCount"));
        result.put("page", pageJson.getInteger("page"));
        result.put("pageSize", pageJson.getInteger("rows"));
        result.put("data", proxyContracts);
        return  result;
    }

    @Override
    public List<ProxyContractImage> listContractImages(Map<String, String> params) {
        String url = new HttpUtil().buildUrl(SystemConfig.PROPERTIES.get(SystemConfig.CRM_URL), ApiURL.CRM_CONTRACT_IMAGE, params);
        return proxyContractBean.parseProxyContractImages(new HttpUtil().get(url));
    }

    @Override
    public String getContractStatus(Integer innId) {
//        ProxyInn proxyInn = proxyInnService.findByInnId(innId);
//        ProxyContractForm proxyContractForm = new ProxyContractForm();
//        try {
//            proxyContractForm.setKeyword(URLEncoder.encode(proxyInn.getInnName(), "UTF-8"));
//        } catch (UnsupportedEncodingException e) {
//            LOGGER.error(String.format("合同查询关键词编码失败, keyword=%s", proxyInn.getInnName()), e);
//        }
//        Map<String, String> params = proxyContractBean.parseContractQueryParams(proxyContractForm);
//        String jsonStr = proxyContractDao.queryConstracts(params);
//        List<ProxyContract> proxyContracts = proxyContractBean.parseProxyContracts(jsonStr);
//        if(CollectionUtils.isEmpty(proxyContracts)){
//            return null;
//        }
//        return proxyContracts.get(0).getStatus();
        ProxyAudit lastContractRecord = proxyAuditService.getLastContractRecord(innId);
        return lastContractRecord == null ? null : lastContractRecord.getStatus();
    }

    @Override
    public void auditSuc(Integer innId) {
        //------------------保存合同审核记录------------------
        proxyAuditService.save(innId, proxyContractBean.getContractNo(innId), ProxyAudit.STATUS_CHECKED, null);

        //----------客栈上架-------------------
        ProxyInn proxyInn = proxyInnService.findByInnId(innId);
        proxyInnService.onshelf(proxyInn);

        //-------------通知CRM--------------------
        Map<String, Object> crmParams = proxyContractBean.parseCheckedCrmParams(innId, hibernateUserDao.findUserByUserCode(SpringSecurityUtil.getCurrentUserName()).getId());
        crmBean.updateCRM(crmParams);
    }

    @Override
    public void auditFail(Integer innId, String reason) {
        String currentUserName = SpringSecurityUtil.getCurrentUserName();
        Long userId = hibernateUserDao.findUserByUserCode(currentUserName).getId();
        //保存合同审核记录
        proxyAuditService.save(innId, proxyContractBean.getContractNo(innId), ProxyAudit.STATUS_REJECTED, reason);

        //---------通知CRM-----------
        String checkReason = crmBean.getSaleRejectedReason(innId, reason);
        checkReason = crmBean.getBaseRejectedReason(innId, checkReason);
        Map<String, Object> crmParams = proxyContractBean.parseRejectedCrmParams(innId, userId, checkReason);
        crmBean.updateCRM(crmParams);
        ProxyInn proxyInn = proxyInnDao.findByInnId(innId);
        // 保存操作日志
        financeOperationLogDao.save(new FinanceOperationLog("112", proxyInn == null ? "" : proxyInn.getInnName(), "合同审核拒绝原因：" + reason, currentUserName));
    }

    @Override
    public void delContractImages(String[] contractImageIds) {

    }

    @Override
    public void passAuditContract(String jsonData) {
        if (StringUtils.isBlank(jsonData)) {
            throw new RuntimeException("数据异常");
        }
        JSONObject jsonObject = JSON.parseObject(jsonData);
        Integer type = jsonObject.getInteger("type");
        if (type == null) {
            throw new RuntimeException("修改操作类型异常");
        }
        Integer innId = jsonObject.getInteger("innId");
        if (innId == null) {
            throw new RuntimeException("客栈ID不能为空");
        }
        Float pricePattern = jsonObject.getFloat("pricePattern");
        if (pricePattern == null) {
            throw new RuntimeException("普通代销总抽佣比例不能为空");
        }
        ProxyInn proxyInn = proxyInnDao.findByInnId(innId);
        if (proxyInn == null) {
            throw new RuntimeException("代销客栈不存在");
        }
        // 普通代销渠道ID集合
        String sale = jsonObject.getString("sale");
        List<Integer> saleChannelIdList = JSON.parseArray(sale, Integer.class);
        // 设置客栈渠道关联关系，保存客栈普通代销的抽佣比例
        proxyInnService.initProxySaleInnChannel(proxyInn, pricePattern, saleChannelIdList);
        //------------------保存合同审核记录------------------
        proxyAuditService.save(innId, proxyContractBean.getContractNo(innId), ProxyAudit.STATUS_CHECKED, null);
        //----------客栈上架-------------------
        proxyInnService.onshelf(proxyInn);
        //-------------通知CRM--------------------
        LOGGER.info("---------trace------通知CRM开始----");
        Map<String, Object> crmParams = proxyContractBean.parseCheckedCrmParams(innId, hibernateUserDao.findUserByUserCode(SpringSecurityUtil.getCurrentUserName()).getId());
        crmBean.updateCRM(crmParams);
        LOGGER.info("---------trace------通知CRM成功----");
        // 保存操作日志
        LOGGER.info("---------trace------保存操作日志开始----");
        financeOperationLogDao.save(new FinanceOperationLog("112", proxyInn == null ? "" : proxyInn.getInnName(), "合同审核通过", SpringSecurityUtil.getCurrentUserName()));
        LOGGER.info("---------trace------保存操作日志成功----");
    }

}
