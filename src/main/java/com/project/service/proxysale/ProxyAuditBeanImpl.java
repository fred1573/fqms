package com.project.service.proxysale;

import com.project.core.utils.springsecurity.SpringSecurityUtil;
import com.project.dao.account.HibernateUserDao;
import com.project.entity.account.User;
import com.project.entity.proxysale.ProxyAudit;
import com.project.service.account.AccountService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 *
 * Created by Administrator on 2015/8/28.
 */
@Component("proxyAuditBean")
public class ProxyAuditBeanImpl implements ProxyAuditBean {

    @Autowired
    private AccountService accountService;
    @Autowired
    private HibernateUserDao hibernateUserDao;

    @Override
    public ProxyAudit parse(Integer innId, String recordNo, String status, String reason, Short pattern, Integer type) {
        if(innId == null || StringUtils.isBlank(recordNo) || StringUtils.isBlank(status) || type == null
                || type.equals(ProxyAudit.AUDIT_PRICE) && pattern == null){
            throw new RuntimeException("转换审核记录实体时异常，参数缺失");
        }
        return parse(innId, recordNo, status, reason, pattern, type, new Date(), hibernateUserDao.findUserByUserCode(SpringSecurityUtil.getCurrentUserName()));
    }

    @Override
    public ProxyAudit parse(Integer innId, String recordNo, String status, String reason, Short pattern, Integer type, Date date, User user) {
        ProxyAudit proxyAudit = new ProxyAudit();
        proxyAudit.setInnId(innId);
        proxyAudit.setRecordNo(recordNo);
        proxyAudit.setReason(reason);
        proxyAudit.setType(type);
        proxyAudit.setAuditTime(date);
        proxyAudit.setPattern(pattern);
        proxyAudit.setStatus(status);
        proxyAudit.setAuditor(user);
        return proxyAudit;
    }
}
