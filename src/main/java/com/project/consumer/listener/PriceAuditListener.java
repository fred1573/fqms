package com.project.consumer.listener;

import com.alibaba.fastjson.JSON;
import com.project.bean.proxysale.ProxyPriceAudit;
import com.project.consumer.event.PriceAuditEvent;
import com.project.entity.proxysale.PricePattern;
import com.project.entity.proxysale.ProxyAudit;
import com.project.service.proxysale.PricePatternService;
import com.project.service.proxysale.ProxyAuditService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import javax.transaction.Transactional;
import java.util.Date;

/**
 * @author frd
 */
@Component
@Transactional
public class PriceAuditListener implements ApplicationListener<PriceAuditEvent> {

    private static final Logger LOGGER = LoggerFactory.getLogger(PriceAuditListener.class);

    @Autowired
    private ProxyAuditService proxyAuditService;
    @Autowired
    private PricePatternService pricePatternService;

    @Override
    public void onApplicationEvent(PriceAuditEvent event) {
        String content = event.getSource().toString();
        LOGGER.info("------------------priceAuditListener:" + content + "-----------------");
        ProxyPriceAudit priceAudit = JSON.parseObject(content, ProxyPriceAudit.class);
        ProxyAudit pa = new ProxyAudit();
        pa.setAuditTime(new Date());
        pa.setInnId(priceAudit.getInnId());
        pa.setRecordNo(priceAudit.getRecordCode());
        pa.setStatus(ProxyAudit.STATUS_CHECKED);
        pa.setType(ProxyAudit.AUDIT_PRICE);
        PricePattern pricePattern = pricePatternService.getByAccountId(priceAudit.getAccountId());
        pa.setPattern(pricePattern.getPattern());
        proxyAuditService.save(pa);
    }
}