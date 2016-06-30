package com.project.web.proxysale;

import com.project.bean.vo.AjaxResult;
import com.project.common.Constants;
import com.project.entity.proxysale.ProxyContract;
import com.project.entity.proxysale.ProxyContractImage;
import com.project.service.proxysale.ProxyContractBean;
import com.project.service.proxysale.ProxyContractService;
import com.project.service.proxysale.ProxyInnService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 合同
 * Created by Administrator on 2015/8/25.
 */
@Controller
@RequestMapping(value = "/proxysale/contract")
public class ProxyContractController {

    @Autowired
    private ProxyContractService proxyContractService;
    @Autowired
    private ProxyContractBean proxyContractBean;
    @Autowired
    private ProxyInnService proxyInnService;

    @RequestMapping(method = RequestMethod.GET)
    public String index(){
        return "proxysale/audit/contract/list";
    }

    @RequestMapping(value = "/list", method = RequestMethod.GET)
    @ResponseBody
    public Map<String, Object> list(ProxyContractForm proxyContractForm,
                                    Model model){
        Map<String, String> params = proxyContractBean.parseContractQueryParams(proxyContractForm);
        Map<String, Object> result = proxyContractService.listContracts(params);
        model.addAttribute("innName", proxyContractForm.getKeyword());
        return result;
    }

    /**
     * 审核否决接口
     */
    @RequestMapping(value = "/{innId}/audit", method = RequestMethod.POST)
    @ResponseBody
    public AjaxResult audit(@PathVariable("innId")Integer innId,
                            @RequestParam("status")String status,
                            @RequestParam(value = "reason", required = false)String reason){
        if(ProxyContract.STATUS_CHECKED.equals(status)){
            proxyContractService.auditSuc(innId);
        }else if(ProxyContract.STATUS_REJECTED.equals(status)){
            if(StringUtils.isBlank(reason)){
                return new AjaxResult(Constants.HTTP_500, "否决原因必填");
            }
            proxyContractService.auditFail(innId, reason);
        }
        return new AjaxResult(Constants.HTTP_OK, "");
    }

    /**
     * 通过合同审核
     * @param jsonData 前端封装的json数据
     * @return 执行结果
     */
    @RequestMapping(value = "/passAuditContract")
    @ResponseBody
    public AjaxResult passAuditContract(String jsonData) {
        try{
            proxyContractService.passAuditContract(jsonData);
        } catch (Exception e) {
            return new AjaxResult(Constants.HTTP_500, e.getMessage());
        }
        return new AjaxResult(Constants.HTTP_OK, "success");
    }

    @RequestMapping(value = "/{innId}/images", method = RequestMethod.GET)
    public ModelAndView imageList(@PathVariable("innId")Integer innId){
        ModelAndView mav = new ModelAndView("/proxysale/audit/contract/image");
        Map<String, String> params = new HashMap<>();
        params.put("pmsInnId", innId.toString());
        List<ProxyContractImage> proxyContractImages = proxyContractService.listContractImages(params);
        mav.addObject("proxyContractImages", proxyContractImages);
        mav.addObject("proxyInn", proxyInnService.findByInnId(innId));
        return mav;
    }
}
