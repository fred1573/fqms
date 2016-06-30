package com.project.service.proxysale;

import com.project.entity.proxysale.ProxyContractImage;

import java.util.List;
import java.util.Map;

/**
 * 代销合同
 * Created by Administrator on 2015/8/24.
 */
public interface ProxyContractService {

    Map<String, Object> listContracts(Map<String, String> params);

    List<ProxyContractImage> listContractImages(Map<String, String> params);

    String getContractStatus(Integer innId);

    void auditSuc(Integer innId);

    void auditFail(Integer innId, String reason);

    void delContractImages(String[] contractImageIds);

    /**
     * 通过合同审核
     * @param jsonData 前端封装的json数据
     */
    void passAuditContract(String jsonData);

}
