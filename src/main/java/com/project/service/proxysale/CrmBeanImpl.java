package com.project.service.proxysale;

import com.alibaba.fastjson.JSONObject;
import com.project.common.Constants;
import com.project.entity.proxysale.PricePattern;
import com.project.entity.proxysale.ProxyAudit;
import com.tomato.mq.client.support.MQClientBuilder;
import com.tomato.mq.support.core.SysMessage;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 *
 * Created by Administrator on 2015/8/31.
 */
@Component("crmBean")
public class CrmBeanImpl implements CrmBean {

    private static final String INN_STATUS_UNCHECK = "UNCHECK";
    private static final String INN_STATUS_CHECKED = "CHECKED";
    private static final String INN_STATUS_BASE_CHECKED = "BASE_CHECKED";
    private static final String INN_STATUS_SALE_CHECKED = "SALE_CHECKED";
    private static final String INN_STATUS_REJECTED = "REJECTED";

    @Autowired
    private ProxyContractService proxyContractService;
    @Autowired
    private ProxyAuditService proxyAuditService;

    @Override
    public void updateCRM(Map<String, Object> params) {
//        String url = new HttpUtil().buildUrl(SystemConfig.PROPERTIES.get(SystemConfig.CRM_URL), ApiURL.CRM_AUDIT, null);
//        new HttpUtil().postForm(url, params);
        //消息服务 -- 事件
        MQClientBuilder.build().send(new SysMessage(Constants.MQ_PROJECT_IDENTIFICATION, Constants.MQ_EVENT_BIZTYPE_PROXY_INN_STATUS, new JSONObject(params).toJSONString()));
    }

    @Override
    public String getInnStatusWithBasePriceChecked(Integer innId) {
        String contractStatus = proxyContractService.getContractStatus(innId);
        String lastSaleStatus;
        if (StringUtils.isNotBlank(contractStatus)) {
            if (ProxyAudit.STATUS_CHECKED.equals(contractStatus)) {
                ProxyAudit lastSalePrice = proxyAuditService.getLastPriceRecord(innId, PricePattern.PATTERN_SALE_PRICE);
                lastSaleStatus = (lastSalePrice == null ? null : lastSalePrice.getStatus());
                if (StringUtils.isBlank(lastSaleStatus) || ProxyAudit.STATUS_UNCHECK.equals(lastSaleStatus) || ProxyAudit.STATUS_REJECTED.equals(lastSaleStatus)) {
                    return INN_STATUS_BASE_CHECKED;
                } else if (ProxyAudit.STATUS_CHECKED.equals(lastSaleStatus)) {
                    return INN_STATUS_CHECKED;
                } else {
                    throw new RuntimeException("获取最后一条普通审核单状态异常，status=" + lastSaleStatus);
                }
            } else if (ProxyAudit.STATUS_REJECTED.equals(contractStatus)) {
                return INN_STATUS_REJECTED;
            } else {
                throw new RuntimeException("获取合同审核单状态异常, status=" + contractStatus);
            }
        } else {
            //客栈未被BD绑定
            return INN_STATUS_UNCHECK;
        }
    }

    @Override
    public String getInnStatusWithSalePriceChecked(Integer innId) {
        String contractStatus = proxyContractService.getContractStatus(innId);
        String lastBaseStatus;
        if (StringUtils.isNotBlank(contractStatus)) {
            if (ProxyAudit.STATUS_CHECKED.equals(contractStatus)) {
                ProxyAudit lastBasePrice = proxyAuditService.getLastPriceRecord(innId, PricePattern.PATTERN_BASE_PRICE);
                lastBaseStatus = (lastBasePrice == null ? null : lastBasePrice.getStatus());
                if (StringUtils.isBlank(lastBaseStatus) || ProxyAudit.STATUS_UNCHECK.equals(lastBaseStatus) || ProxyAudit.STATUS_REJECTED.equals(lastBaseStatus)) {
                    return INN_STATUS_SALE_CHECKED;
                } else if (ProxyAudit.STATUS_CHECKED.equals(lastBaseStatus)) {
                    return INN_STATUS_CHECKED;
                } else {
                    throw new RuntimeException("获取最后一条精品审核单状态异常，status=" + lastBaseStatus);
                }
            } else if (ProxyAudit.STATUS_REJECTED.equals(contractStatus)) {
                return INN_STATUS_REJECTED;
            } else {
                throw new RuntimeException("获取合同审核单状态异常, status=" + contractStatus);
            }
        } else {
            //客栈未被BD绑定
            return INN_STATUS_UNCHECK;
        }

    }

    @Override
    public String getInnStatusWithBasePriceReject(Integer innId) {
        String contractStatus = proxyContractService.getContractStatus(innId);
        String lastSaleStatus;
        if (StringUtils.isNotBlank(contractStatus)) {
            if (ProxyAudit.STATUS_CHECKED.equals(contractStatus)) {
                ProxyAudit lastSalePrice = proxyAuditService.getLastPriceRecord(innId, PricePattern.PATTERN_SALE_PRICE);
                lastSaleStatus = (lastSalePrice == null ? null : lastSalePrice.getStatus());
                if (StringUtils.isBlank(lastSaleStatus) || ProxyAudit.STATUS_UNCHECK.equals(lastSaleStatus) || ProxyAudit.STATUS_REJECTED.equals(lastSaleStatus)) {
                    return INN_STATUS_REJECTED;
                } else if (ProxyAudit.STATUS_CHECKED.equals(lastSaleStatus)) {
                    return INN_STATUS_SALE_CHECKED;
                } else {
                    throw new RuntimeException("获取最后一条普通审核单状态异常，status=" + lastSaleStatus);
                }
            } else if (ProxyAudit.STATUS_REJECTED.equals(contractStatus)) {
                return INN_STATUS_REJECTED;
            } else {
                throw new RuntimeException("获取合同审核单状态异常, status=" + contractStatus);
            }
        } else {
            //客栈未被BD绑定
            return INN_STATUS_UNCHECK;
        }
    }

    @Override
    public String getInnStatusWithSalePriceReject(Integer innId) {
        String contractStatus = proxyContractService.getContractStatus(innId);
        String lastBaseStatus;
        if (StringUtils.isNotBlank(contractStatus)) {
            if (ProxyAudit.STATUS_CHECKED.equals(contractStatus)) {
                ProxyAudit lastBasePrice = proxyAuditService.getLastPriceRecord(innId, PricePattern.PATTERN_BASE_PRICE);
                lastBaseStatus = (lastBasePrice == null ? null : lastBasePrice.getStatus());
                if (StringUtils.isBlank(lastBaseStatus) || ProxyAudit.STATUS_UNCHECK.equals(lastBaseStatus) || ProxyAudit.STATUS_REJECTED.equals(lastBaseStatus)) {
                    return INN_STATUS_REJECTED;
                } else if (ProxyAudit.STATUS_CHECKED.equals(lastBaseStatus)) {
                    return INN_STATUS_BASE_CHECKED;
                } else {
                    throw new RuntimeException("获取最后一条精品审核单状态异常，status=" + lastBaseStatus);
                }
            } else if (ProxyAudit.STATUS_REJECTED.equals(contractStatus)) {
                return INN_STATUS_REJECTED;
            } else {
                throw new RuntimeException("获取合同审核单状态异常, status=" + contractStatus);
            }
        } else {
            //客栈未被BD绑定
            return INN_STATUS_UNCHECK;
        }
    }

    @Override
    public String getInnStatusWithContractChecked(Integer pmsInnId) {
        ProxyAudit lastBasePrice = proxyAuditService.getLastPriceRecord(pmsInnId, PricePattern.PATTERN_BASE_PRICE);
        String lastBaseStatus = (lastBasePrice == null ? null : lastBasePrice.getStatus());
        if (ProxyAudit.STATUS_CHECKED.equals(lastBaseStatus)) {
            //--------------查询最后一条卖价审核单状态---------------
            ProxyAudit lastSalePrice = proxyAuditService.getLastPriceRecord(pmsInnId, PricePattern.PATTERN_SALE_PRICE);
            String lastSaleStatus = (lastSalePrice == null ? null : lastSalePrice.getStatus());
            if (ProxyAudit.STATUS_CHECKED.equals(lastSaleStatus)) {
                return INN_STATUS_CHECKED;
            } else if (StringUtils.isBlank(lastSaleStatus) || ProxyAudit.STATUS_UNCHECK.equals(lastSaleStatus) || ProxyAudit.STATUS_REJECTED.equals(lastSaleStatus)) {
                return INN_STATUS_BASE_CHECKED;
            } else {
                throw new RuntimeException("获取最后一条普通审核单状态异常, status=" + lastSaleStatus);
            }
        } else if (ProxyAudit.STATUS_REJECTED.equals(lastBaseStatus)) {
            ProxyAudit lastSalePrice = proxyAuditService.getLastPriceRecord(pmsInnId, PricePattern.PATTERN_SALE_PRICE);
            String lastSaleStatus = (lastSalePrice == null ? null : lastSalePrice.getStatus());
            if (ProxyAudit.STATUS_CHECKED.equals(lastSaleStatus)) {
                return INN_STATUS_SALE_CHECKED;
            } else if (StringUtils.isBlank(lastSaleStatus) || ProxyAudit.STATUS_UNCHECK.equals(lastSaleStatus) || ProxyAudit.STATUS_REJECTED.equals(lastSaleStatus)) {
                return INN_STATUS_REJECTED;
            } else {
                throw new RuntimeException("获取最后一条普通审核单状态异常, status=" + lastSaleStatus);
            }
        } else if (ProxyAudit.STATUS_UNCHECK.equals(lastBaseStatus) || StringUtils.isBlank(lastBaseStatus)) {
            //--------------查询最后一条卖价审核单状态---------------
            ProxyAudit lastSalePrice = proxyAuditService.getLastPriceRecord(pmsInnId, PricePattern.PATTERN_SALE_PRICE);
            String lastSaleStatus = (lastSalePrice == null ? null : lastSalePrice.getStatus());
            if (ProxyAudit.STATUS_CHECKED.equals(lastSaleStatus)) {
                return INN_STATUS_SALE_CHECKED;
            } else if (ProxyAudit.STATUS_UNCHECK.equals(lastSaleStatus) || StringUtils.isBlank(lastSaleStatus)) {
                return INN_STATUS_UNCHECK;
            } else if (ProxyAudit.STATUS_REJECTED.equals(lastSaleStatus)) {
                return INN_STATUS_REJECTED;
            } else {
                throw new RuntimeException("获取最后一条普通审核单状态异常, status=" + lastSaleStatus);
            }
        } else {
            throw new RuntimeException("获取最后一条精品审核单状态异常, status=" + lastBaseStatus);
        }
    }

    @Override
    public String getContractRejectedReason(Integer innId, String reason) {
        StringBuilder sb = new StringBuilder().append(reason);
        //合同否决原因
        ProxyAudit lastContractRecord = proxyAuditService.getLastContractRecord(innId);
        String contractStatus = (lastContractRecord == null ? null : lastContractRecord.getStatus());
        if (contractStatus != null && ProxyAudit.STATUS_REJECTED.equals(contractStatus)) {
            sb.append("\t").append(lastContractRecord.getReason());
        }
        return sb.toString();
    }

    @Override
    public String getBaseRejectedReason(Integer innId, String reason) {
        StringBuilder sb = new StringBuilder().append(reason);
        //底价否决原因
        ProxyAudit lastBasePriceRecord = proxyAuditService.getLastPriceRecord(innId, PricePattern.PATTERN_BASE_PRICE);
        String lastBaseRecordStatus = (lastBasePriceRecord == null ? null : lastBasePriceRecord.getStatus());
        if (lastBaseRecordStatus != null && ProxyAudit.STATUS_REJECTED.equals(lastBaseRecordStatus)) {
            sb.append("\t").append(lastBasePriceRecord.getReason());
        }
        return sb.toString();
    }

    @Override
    public String getSaleRejectedReason(Integer innId, String reason) {
        StringBuilder sb = new StringBuilder().append(reason);
        //卖价否决原因
        ProxyAudit lastSalePriceRecord = proxyAuditService.getLastPriceRecord(innId, PricePattern.PATTERN_SALE_PRICE);
        String lastSaleRecordStatus = (lastSalePriceRecord == null ? null : lastSalePriceRecord.getStatus());
        if (lastSaleRecordStatus != null && ProxyAudit.STATUS_REJECTED.equals(lastSaleRecordStatus)) {
            sb.append("\t").append(lastSalePriceRecord.getReason());
        }
        return sb.toString();
    }
}
