package com.project.service.proxysale;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.project.common.ApiURL;
import com.project.common.Constants;
import com.project.core.utils.springsecurity.SpringSecurityUtil;
import com.project.dao.finance.FinanceOperationLogDao;
import com.project.dao.inn.InnRegionDao;
import com.project.dao.proxysale.PriceStrategyDao;
import com.project.entity.finance.FinanceOperationLog;
import com.project.entity.ota.OtaInfo;
import com.project.service.ota.OtaInfoService;
import com.project.utils.CollectionsUtil;
import com.project.utils.HttpUtil;
import com.project.utils.StringUtil;
import com.project.utils.SystemConfig;
import com.project.utils.encode.MD5;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * Created by admin on 2016/5/30.
 */
@Service
@Transactional
public class PriceUpdateServiceImpl implements PriceUpdateService {
    @Autowired
    private InnRegionDao innRegionDao;
    @Autowired
    private PriceStrategyDao priceStrategyDao;
    @Autowired
    private OtaInfoService otaInfoService;
    @Autowired
    private FinanceOperationLogDao financeOperationLogDao;
    private static final Logger LOGGER = LoggerFactory.getLogger(PriceUpdateServiceImpl.class);

    @Override
    public List<Map<String, Object>> findRegionName() {
        return innRegionDao.findRegionName();
    }

    @Override
    public List<Map<String, Object>> findSaleChannel() {
        List<Map<String, Object>> saleChannel = priceStrategyDao.findSaleChannel();
        List<OtaInfo> otaInfos = null;
        try {
            otaInfos = otaInfoService.list();
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
        List<Map<String, Object>> channelNames = new ArrayList<>();
        if (CollectionsUtil.isNotEmpty(saleChannel)) {
            for (Map<String, Object> map : saleChannel) {
                if (CollectionsUtil.isNotEmpty(otaInfos)) {
                    for (OtaInfo otaInfo : otaInfos) {
                        if (null != map.get("id") && null != otaInfo.getOtaId() && map.get("id").equals(otaInfo.getOtaId())) {
                            Map<String, Object> map1 = new HashMap<>();
                            map1.put("channelId", otaInfo.getOtaId());
                            map1.put("channelName", otaInfo.getName());
                            channelNames.add(map1);
                        }
                    }
                }
            }
        }
        return channelNames;

    }

    @Override
    public void batchUpdatePrice(String jsonStr) {
        JSONObject jsonObject = (JSONObject) JSONObject.parse(jsonStr);
        Integer regionId = jsonObject.getInteger("regionId");
        if (null == regionId) {
            throw new RuntimeException("目的地未选择");
        }
        List<Integer> accountIdWithRegion = findAccountIdWithRegion(regionId);
        if (CollectionsUtil.isEmpty(accountIdWithRegion)) {
            throw new RuntimeException("未找到调价客栈");
        }
        JSONArray jsonArray = jsonObject.getJSONArray("channelIds");
        if (CollectionsUtil.isEmpty(jsonArray)) {
            throw new RuntimeException("调价渠道未选择");
        }
        List<Integer> otaList = new ArrayList<>();
        for (int i = 0; i < jsonArray.size(); i++) {
            Integer id = jsonArray.getInteger(i);
            otaList.add(id);
        }
        String from = jsonObject.getString("from");
        if (StringUtils.isBlank(from)) {
            throw new RuntimeException("请选择开始时间");
        }
        String to = jsonObject.getString("to");
        if (StringUtils.isBlank(to)) {
            throw new RuntimeException("请选择结束时间");
        }
        Integer extraPrice = jsonObject.getInteger("extraPrice");
        if (null == extraPrice) {
            throw new RuntimeException("请填写调价金额");
        }
        List<String> otaName = new ArrayList<>();
        JSONArray otaNames = jsonObject.getJSONArray("channelNames");
        if (CollectionsUtil.isEmpty(otaNames)) {
            throw new RuntimeException("请传入渠道名称");
        }
        for (int i = 0; i < otaNames.size(); i++) {
            String name = otaNames.getString(i);
            otaName.add(name);
        }
        StringBuilder stringBuilder = new StringBuilder(SystemConfig.PROPERTIES.get(SystemConfig.OMS_URL));
        stringBuilder.append(ApiURL.BATCH_UPDATE_PRICE);
        Map<String, Object> param = new HashMap<>();
        Map<String, Object> params = new HashMap<>();
        param.put("accountIds", accountIdWithRegion);
        param.put("extraPrice", extraPrice);
        param.put("from", from);
        param.put("to", to);
        param.put("otaList", otaList);
        params.put("data", new JSONObject(param).toJSONString());
        params.put("otaId", Constants.OMS_PROXY_PID.toString());
        long currentTimeMillis = System.currentTimeMillis();
        params.put("timestamp", String.valueOf(currentTimeMillis));
        String omsSignature = MD5.getOMSSignature(currentTimeMillis);
        params.put("signature", omsSignature);
        LOGGER.info("准备请求OMS批量调价接口。目的地:" + regionId + "渠道:" + otaName + "accountId" + accountIdWithRegion);
        String result = HttpUtil.httpPost(stringBuilder.toString(), params, false);
        if (StringUtils.isBlank(result)) {
            throw new RuntimeException("OMS返回数据为空");
        }
        JSONObject jsonObject1 = JSONObject.parseObject(result);
        String status = jsonObject1.getString("status");
        if (!status.equals(Constants.HTTP_OK + "")) {
            throw new RuntimeException("OMS接口请求失败，原因：" + jsonObject1.getString("message"));
        }
        //操作日志
        saveLog(otaName, regionId, from, to, extraPrice);
    }

    /**
     * 批量调价操作日志
     *
     * @param names
     * @param regionId
     * @param from
     * @param to
     * @param extraPrice
     */
    private void saveLog(List<String> names, Integer regionId, String from, String to, Integer extraPrice) {
        Map<String, Object> regionName = priceStrategyDao.findRegionName(regionId);
        String name = "";
        if (regionName.size() > 0) {
            name = (String) regionName.get("name");
        }
        String channelName = StringUtil.list2String(names);
        //日志内容，调价大于0添加+号
        StringBuilder content = new StringBuilder(channelName);
        content.append(";");
        content.append("(");
        content.append(from);
        content.append("-");
        content.append(to);
        content.append(")");
        if (extraPrice > 0) {
            content.append("+");
        }
        content.append(extraPrice);
        FinanceOperationLog financeOperationLog = new FinanceOperationLog("118", name, content.toString(), SpringSecurityUtil.getCurrentUserName(), new Date());
        financeOperationLogDao.save(financeOperationLog);

    }

    @Override
    public void saveErrorLog(String jsonStr) {
        JSONObject jsonObject = (JSONObject) JSONObject.parse(jsonStr);
        Integer regionId = jsonObject.getInteger("regionId");
        if (null == regionId) {
            throw new RuntimeException("目的地未选择");
        }
        String from = jsonObject.getString("from");
        String to = jsonObject.getString("to");
        Integer extraPrice = jsonObject.getInteger("extraPrice");
        List<String> otaName = new ArrayList<>();
        JSONArray otaNames = jsonObject.getJSONArray("channelNames");

        for (int i = 0; i < otaNames.size(); i++) {
            String name = otaNames.getString(i);
            otaName.add(name);
        }

        Map<String, Object> regionName = priceStrategyDao.findRegionName(regionId);
        String name = "";
        if (regionName.size() > 0) {
            name = (String) regionName.get("name");
        }
        String channelName = StringUtil.list2String(otaName);
        StringBuilder content = new StringBuilder("【操作失败】");

        content.append(channelName);
        content.append(";");
        content.append("(");
        content.append(from);
        content.append("-");
        content.append(to);
        content.append(")");
        if (extraPrice > 0) {
            content.append("+");
        }
        content.append(extraPrice);
        FinanceOperationLog financeOperationLog = new FinanceOperationLog("118", name, content.toString(), SpringSecurityUtil.getCurrentUserName(), new Date());
        financeOperationLogDao.save(financeOperationLog);

    }

    /**
     * 根据目的地查询卖价客栈
     *
     * @param regionId
     * @return
     */
    private List<Integer> findAccountIdWithRegion(Integer regionId) {
        List<Map<String, Object>> accountId = priceStrategyDao.findAccountId(regionId);
        if (CollectionsUtil.isNotEmpty(accountId)) {
            List list = new ArrayList();
            for (Map<String, Object> map : accountId) {
                Integer id = (Integer) map.get("accountid");
                list.add(id);
            }
            return list;
        }
        return null;
    }
}
