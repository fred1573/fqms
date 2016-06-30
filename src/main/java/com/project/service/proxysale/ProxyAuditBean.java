package com.project.service.proxysale;

import com.project.entity.account.User;
import com.project.entity.proxysale.ProxyAudit;

import java.util.Date;

/**
 *
 * Created by Administrator on 2015/8/28.
 */
public interface ProxyAuditBean {

    ProxyAudit parse(Integer innId, String recordNo, String status, String reason, Short pattern, Integer type);

    ProxyAudit parse(Integer innId, String recordNo, String status, String reason, Short pattern, Integer type, Date date, User user);
}
