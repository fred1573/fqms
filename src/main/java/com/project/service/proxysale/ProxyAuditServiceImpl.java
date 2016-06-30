package com.project.service.proxysale;

import com.project.dao.proxysale.ProxyAuditDao;
import com.project.entity.proxysale.PricePattern;
import com.project.entity.proxysale.ProxyAudit;
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
 * Created by Administrator on 2015/8/28.
 */
@Service("proxyAuditService")
@Transactional
public class ProxyAuditServiceImpl implements ProxyAuditService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ProxyAuditServiceImpl.class);

    @Autowired
    private ProxyAuditDao proxyAuditDao;
    @Autowired
    private ProxyAuditBean proxyAuditBean;


    @Override
    public void save(Integer innId, String recordNo, String status, String reason) {
        ProxyAudit proxyAudit = proxyAuditDao.getLast(innId, ProxyAudit.AUDIT_CONTRACT, null);
        if(proxyAudit == null){
            proxyAudit = proxyAuditBean.parse(innId, recordNo, status, reason, null, ProxyAudit.AUDIT_CONTRACT);
        }else{
            proxyAudit.setStatus(status);
            if(status.equals(ProxyAudit.STATUS_REJECTED) && StringUtils.isNotBlank(reason)){
                proxyAudit.setReason(reason);
            }
        }
        LOGGER.info("-------trace----合同审核数据开始保存");
        proxyAuditDao.saveOrUpdate(proxyAudit);
        LOGGER.info("-------trace----合同审核数据保存成功");
    }

    @Override
    public void save(Integer innId, String recordNo, String status, String reason, Short pattern) {
        ProxyAudit proxyAudit = proxyAuditBean.parse(innId, recordNo, status, reason, pattern, ProxyAudit.AUDIT_PRICE);
        proxyAuditDao.save(proxyAudit);
    }

    @Override
    public ProxyAudit getLastContractRecord(Integer innId) {
        return proxyAuditDao.getLast(innId, ProxyAudit.AUDIT_CONTRACT, null);
    }

    @Override
    public ProxyAudit getLastPriceRecord(Integer innId, Short pattern) {
        return proxyAuditDao.getLast(innId, ProxyAudit.AUDIT_PRICE, pattern);
    }

    @Override
    public void syncPriceRcords(List<ProxyAudit> audits) {
        for (ProxyAudit audit : audits) {
            proxyAuditDao.syncPriceRecords(audit);
        }
    }

    @Override
    public boolean hasPriceRecordChecked(Integer innId, Short pattern) {
        return proxyAuditDao.hasCheckedPriceRecord(innId, pattern);
    }

	@Override
	public Boolean hasContractssChecked(Integer innId) {
		return  proxyAuditDao.selectContractsRecoreds(innId);
	}

    @Override
    public Map<String, Object> getAuditStatus(Integer innId) {
        Map<String, Object> statusMap = new HashMap<>();
        ProxyAudit lastContractRecord = getLastContractRecord(innId);
        if(lastContractRecord != null){
            statusMap.put("contractStatus", lastContractRecord.getStatus());
        }
        ProxyAudit lastBasePriceRecord = getLastPriceRecord(innId, PricePattern.PATTERN_BASE_PRICE);
        if(lastBasePriceRecord != null){
            statusMap.put("basePriceStatus", lastBasePriceRecord.getStatus());
        }
        ProxyAudit lastSalePriceRecord = getLastPriceRecord(innId, PricePattern.PATTERN_SALE_PRICE);
        if(lastSalePriceRecord != null){
            statusMap.put("salePriceStatus", lastSalePriceRecord.getStatus());
        }
        return statusMap;
    }

    @Override
    public boolean hasExsitByRecordCode(String recordCode) {
        return proxyAuditDao.hasExsitByRecordCode(recordCode);
    }

    @Override
    public void save(ProxyAudit audit) {
        proxyAuditDao.save(audit);
    }
}
