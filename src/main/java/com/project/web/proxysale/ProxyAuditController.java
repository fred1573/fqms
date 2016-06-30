package com.project.web.proxysale;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.project.bean.vo.AjaxResult;
import com.project.common.Constants;
import com.project.entity.proxysale.PricePattern;
import com.project.entity.proxysale.ProxyAudit;
import com.project.service.account.AccountService;
import com.project.service.proxysale.ProxyAuditService;
import com.project.service.proxysale.ProxyInnPriceBean;
import com.project.service.proxysale.ProxyInnPriceService;
import com.project.service.proxysale.ProxyInnService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Date;
import java.util.Map;

/**
 * @author Administrator
 *         2015-11-18 16:47
 */
@Controller
@RequestMapping(value = "/proxysale/audit")
public class ProxyAuditController {

    private static final Integer PAGE_SIZE = 1000;
    private static final Logger LOGGER = LoggerFactory.getLogger(ProxyAuditController.class);

    @Autowired
    private ProxyInnPriceService proxyInnPriceService;
    @Autowired
    private ProxyAuditService proxyAuditService;
    @Autowired
    private ProxyInnPriceBean proxyInnPriceBean;
    @Autowired
    private AccountService accountService;
    @Autowired
    private ProxyInnService proxyInnService;

    /**
     * 同步价格审核记录(通过&否决)
     * @return
     */
    @RequestMapping
    @ResponseBody
    public AjaxResult syncPriceRecord(){
        ProxyInnPriceForm proxyInnPriceForm = new ProxyInnPriceForm();
        proxyInnPriceForm.setPageSize(PAGE_SIZE.toString());
        Integer pageNo = 1;
        proxyInnPriceForm.setPageNo(pageNo.toString());
        process(proxyInnPriceForm, ProxyInnPriceService.STATUS_CHECKED, ProxyAudit.STATUS_CHECKED);
        process(proxyInnPriceForm, ProxyInnPriceService.STATUS_REJECT, ProxyAudit.STATUS_REJECTED);
        return new AjaxResult(Constants.HTTP_OK, "");
    }

    private void process(ProxyInnPriceForm proxyInnPriceForm, String priceStatus, String auditStatus) {
        proxyInnPriceForm.setStatus(priceStatus);
        Map<String, String> params = proxyInnPriceBean.parsePriceQueryForm(proxyInnPriceForm);
        Integer totalCount = null;
        Integer count = 0;
        do{
            String checkedList = proxyInnPriceService.list(params);
            JSONObject jsonObject = JSON.parseObject(checkedList);
            if(totalCount == null){
                totalCount = jsonObject.getInteger("total");
            }
            JSONArray data = jsonObject.getJSONArray("data");
            count += data.size();
            for (Object o : data) {
                JSONObject json = (JSONObject)o;
                String recordCode = json.getString("recordCode");
                if(proxyAuditService.hasExsitByRecordCode(recordCode)){
                    continue;
                }
                Long approvePerson = json.getLong("approvePerson");
                Date time = json.getDate("dateUpdated");
                Integer innId = json.getInteger("innId");
                String pattern = json.getString("pattern");

                ProxyAudit audit = new ProxyAudit();
                if(approvePerson != null){
                    audit.setAuditor(accountService.getUser(approvePerson));
                }
                audit.setAuditTime(time);
                audit.setInnId(innId);
                Short convertPattern = convertPattern(pattern);
                if(convertPattern == null){
                    LOGGER.error("模式异常, recordNo=" + recordCode);
                    continue;
                }
                audit.setPattern(convertPattern);
                audit.setRecordNo(recordCode);
                audit.setStatus(auditStatus);
                audit.setType(ProxyAudit.AUDIT_PRICE);
                proxyAuditService.save(audit);
            }
            params.put("page", Integer.valueOf(Integer.parseInt(params.get("page")) + 1).toString());
        }while (count < totalCount);
    }

    private Short convertPattern(String pattern){
        if("JINPING".equals(pattern)){
            return PricePattern.PATTERN_BASE_PRICE;
        }else if("NORMAL".equals(pattern)){
            return PricePattern.PATTERN_SALE_PRICE;
        }else{
            return null;
        }
    }

    @RequestMapping("/hasContractChecked/{id}")
    @ResponseBody
    public AjaxResult hasContractChecked(@PathVariable("id") Integer id) {
        return new AjaxResult(Constants.HTTP_OK, proxyAuditService.hasContractssChecked(proxyInnService.get(id).getInn()));
    }
}
